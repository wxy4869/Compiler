package mid;

import front.ASD.*;
import front.SynAnalyzer;
import mid.inst.*;
import mid.type.*;
import table.*;
import utils.Pair;

import java.util.ArrayList;
import java.util.Stack;

public class MidGenerator {
    private int regID;
    private Function currentFunction;
    private BasicBlock currentBasicBlock;
    private Stack<BasicBlock> currentCondBlock;
    private Stack<BasicBlock> currentOutBlock;
    private SymTable currentSymTable;
    private int arrayOffset; // 用于数组初始化

    public MidGenerator() {
        this.regID = 0;
        this.currentFunction = null;
        this.currentBasicBlock = null;
        this.currentCondBlock = new Stack<>();
        this.currentOutBlock = new Stack<>();
        this.currentSymTable = SymGenerator.table.get(SynAnalyzer.root);
        this.arrayOffset = 0;
    }

    public Type getDefType(Def def) {  // 用于 ConstDefVisitor 和 VarDefVisitor
        Type type = new BaseType(BaseType.Tag.I32);
        int capacity = 1;
        for (int i = def.getDimension() - 1; i >= 0 ; i--) {
            capacity *= def.getSize().get(i);
            type = new ArrayType(def.getSize().get(i), type, capacity);
        }
        return type;
    }

    public void setCurBasicBlock(BasicBlock newBasicBlock) {  // 用于 StmtVisitor 的 if 和 while、LAndExpVisitor 和 LOrExpVisitor
        currentBasicBlock = newBasicBlock;
        currentFunction.getBasicBlocks().add(currentBasicBlock);
        currentBasicBlock.setName(Integer.toString(regID));
        regID++;
    }

    public Value ifNeedZextInst(Value src, BaseType.Tag targetTag) {  // 用于 BinaryInst 前判断是否需要进行 I32 和 I1 的转换
        if (((BaseType)src.getType()).getTag() != targetTag) {
            Value tmp =  new Value("%" + regID, new BaseType(targetTag));
            regID++;
            new ZextInst(currentBasicBlock, src, tmp);
            src = tmp;
        }
        return src;
    }

    public void generate(Node node) {
        CompUnitVisitor((CompUnit)node);
    }

    public void CompUnitVisitor(CompUnit node) {  // CompUnit -> {Decl} {FuncDef} MainFuncDef
        for (Decl value : node.getDecl()) {
            DeclVisitor(value);
        }
        for (FuncDef value : node.getFuncDef()) {
            FuncDefVisitor(value);
        }
        MainFuncDefVisitor(node.getMainFuncDef());
    }

    public void DeclVisitor(Decl node) {  // Decl -> ConstDecl | VarDecl
        if (node.getConstDecl() != null) {
            ConstDeclVisitor(node.getConstDecl());
        }
        if (node.getVarDecl() != null) {
            VarDeclVisitor(node.getVarDecl());
        }
    }

    public void ConstDeclVisitor(ConstDecl node) {  // ConstDecl -> 'const' BType ConstDef { ',' ConstDef } ';'
        for (ConstDef value : node.getConstDef()) {
            ConstDefVisitor(value);
        }
    }

    public void ConstDefVisitor(ConstDef node) {  // ConstDef -> Ident { '[' ConstExp ']' } '=' ConstInitVal
        Def def = (Def)currentSymTable.getSymbol(node.getIdent().getSrc(), false, node.getIdent().getLineNum());
        Type type = getDefType(def);  //
        if (currentSymTable.getDepth() == 0) {
            String name = "@" + node.getIdent().getSrc();
            new GlobalVariable(name, type, true, def.getSize(), def.getInitVal(), def.getInitArrayVal(), false);
            def.setAddr(new Value(name, new PointerType(type)));
        } else {
            Value dst = new Value("%" + regID, new PointerType(type));
            regID++;
            new AllocaInst(currentBasicBlock, dst);
            def.setAddr(dst);
            ConstInitValVisitor(node.getConstInitVal(), dst, def);
            arrayOffset = 0;
        }
    }

    public void ConstInitValVisitor(ConstInitVal node, Value value, Def def) {  // ConstInitVal -> ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        if (def.getDimension() == 0) {
            Value src = new Value(Integer.toString(def.getInitVal()), new BaseType(BaseType.Tag.I32));
            new StoreInst(currentBasicBlock, src, value);
        } else {
            if (node.getConstExp() != null) {
                Value dst = new Value("%" + regID, new PointerType(new BaseType(BaseType.Tag.I32)));
                regID++;
                ArrayList<Value> indexs = ((ArrayType)value.getType().getInnerType()).offset2index(arrayOffset);
                new GEPInst(currentBasicBlock, value, dst, indexs);
                Value src = new Value(Integer.toString(def.getInitArrayVal().get(arrayOffset)), new BaseType(BaseType.Tag.I32));
                arrayOffset++;
                new StoreInst(currentBasicBlock, src, dst);
            } else {
                for (ConstInitVal child : node.getConstInitVal()) {
                    ConstInitValVisitor(child, value, def);  // 递归
                }
            }
        }
    }

    public void VarDeclVisitor(VarDecl node) {  // VarDecl -> BType VarDef { ',' VarDef } ';'
        for (VarDef value : node.getVarDef()) {
            VarDefVisitor(value);
        }
    }

    public void VarDefVisitor(VarDef node) {  // VarDef -> Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
        Def def = (Def)currentSymTable.getSymbol(node.getIdent().getSrc(), false, node.getIdent().getLineNum());
        Type type = getDefType(def);  //
        if (currentSymTable.getDepth() == 0) {
            String name = "@" + node.getIdent().getSrc();
            new GlobalVariable(name, type, false, def.getSize(), def.getInitVal(), def.getInitArrayVal(), def.isZeroInit());
            def.setAddr(new Value(name, new PointerType(type)));
        } else {
            Value dst = new Value("%" + regID, new PointerType(type));
            regID++;
            new AllocaInst(currentBasicBlock, dst);
            def.setAddr(dst);
            if (node.getInitVal() != null) {
                InitValVisitor(node.getInitVal(), dst, def);
                arrayOffset = 0;
            }
        }
    }

    public void InitValVisitor(InitVal node, Value value, Def def) {  // InitVal -> Exp | '{' [ InitVal { ',' InitVal } ] '}
        if (def.getDimension() == 0) {
            Value src = ExpVisitor(node.getExp());
            new StoreInst(currentBasicBlock, src, value);
        } else {
            if (node.getExp() != null) {
                Value dst = new Value("%" + regID, new PointerType(new BaseType(BaseType.Tag.I32)));
                regID++;
                ArrayList<Value> indexs = ((ArrayType)value.getType().getInnerType()).offset2index(arrayOffset);
                arrayOffset++;
                new GEPInst(currentBasicBlock, value, dst, indexs);
                Value src = ExpVisitor(node.getExp());
                new StoreInst(currentBasicBlock, src, dst);
            } else {
                for (InitVal child : node.getInitVal()) {
                    InitValVisitor(child, value, def);  // 递归
                }
            }
        }
    }

    public void FuncDefVisitor(FuncDef node) {  // FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block
        Func func = (Func)currentSymTable.getSymbol(node.getIdent().getSrc(), true, node.getIdent().getLineNum());
        String funcName = "@" + node.getIdent().getSrc();
        Type type = (func.isVoid()) ? new BaseType(BaseType.Tag.VOID) : new BaseType(BaseType.Tag.I32);
        currentFunction = new Function(funcName, type);
        currentBasicBlock = new BasicBlock("0", currentFunction);
        currentFunction.getBasicBlocks().add(currentBasicBlock);
        regID = 0;
        currentSymTable = SymGenerator.table.get(node.getBlock());
        if (node.getFuncFParams() != null) {
            ArrayList<Pair<Value, FuncParam>> addrs = FuncFParamsVisitor(node.getFuncFParams());  // 返回的 Value 是 BaseType 或 PointerType, 即 I32, I32* 或 [2 x i32]*
            currentBasicBlock.setName(Integer.toString(regID));
            regID++;
            for (Pair<Value, FuncParam> src : addrs) {
                Value dst = new Value("%" + regID, new PointerType(src.getFirst().getType()));
                regID++;
                src.getSecond().setAddr(dst);  // dst 是 PointerType, dst.innerType 与 src.getFirst 类型相同, 是 BaseType 或 PointerType, 即 I32, I32* 或 [2 x i32]*
                new AllocaInst(currentBasicBlock, dst);
                new StoreInst(currentBasicBlock, src.getFirst(), dst);
            }
        } else {
            currentBasicBlock.setName(Integer.toString(regID));
            regID++;
        }
        BlockVisitor(node.getBlock());
        currentSymTable = currentSymTable.getParent();
        int size = currentBasicBlock.getInsts().size();
        if ((size == 0 || !(currentBasicBlock.getInsts().get(size - 1) instanceof RetInst)) && func.isVoid()) {
            new RetInst(currentBasicBlock, new Value(null, type));
        }
    }

    public void MainFuncDefVisitor(MainFuncDef node) {  // MainFuncDef -> 'int' 'main' '(' ')' Block
        currentFunction = new Function("@main", new BaseType(BaseType.Tag.I32));
        currentBasicBlock = new BasicBlock("0", currentFunction);
        currentFunction.getBasicBlocks().add(currentBasicBlock);
        regID = 1;
        currentSymTable = SymGenerator.table.get(node.getBlock());
        BlockVisitor(node.getBlock());
        currentSymTable = currentSymTable.getParent();
    }

    /* public String FuncTypeVisitor(FuncType node) { return null; } */

    public ArrayList<Pair<Value, FuncParam>> FuncFParamsVisitor(FuncFParams node) {  // FuncFParams -> FuncFParams { ',' FuncFParam }
        ArrayList<Pair<Value, FuncParam>> addrs = new ArrayList<>();
        for (FuncFParam value : node.getFuncFParam()) {
            Pair<Value, FuncParam> addr = FuncFParamVisitor(value);
            addrs.add(addr);
        }
        return addrs;
    }

    public Pair<Value, FuncParam> FuncFParamVisitor(FuncFParam node) {  // FuncFParam -> BType Ident ['[' ']' { '[' ConstExp ']' }]
        FuncParam funcParam = (FuncParam)currentSymTable.getSymbol(node.getIdent().getSrc(), false, node.getIdent().getLineNum());
        String paramName = "%" + regID;
        regID++;
        Type type = new BaseType(BaseType.Tag.I32);
        int capacity = 1;
        for (int i = funcParam.getDimension() - 1; i > 0 ; i--) {  // 形参的第一个 [ ] 是空的, 保存为 0, 所以不访问 i = 0
            capacity *= funcParam.getSize().get(i);
            type = new ArrayType(funcParam.getSize().get(i), type, capacity);
        }
        if (node.isPointer()) {  // a[] 或 a[][2] 时 isPointer 为 true
            type = new PointerType(type);  // 处理后 type 为 PointerType, 即 I32* 或 [2 x i32]*
        }
        new Argument(paramName, type, currentFunction);
        Value addr = new Value(paramName,type);  // addr 是 BaseType 或 PointerType, 即 I32, I32* 或 [2 x i32]*
        return new Pair<>(addr, funcParam);  // setAddr 在 FuncDefVisitor 中进行
    }

    public void BlockVisitor(Block node) {  // Block -> '{' { BlockItem } '}'
        for (BlockItem value : node.getBlockItem()) {
            BlockItemVisitor(value);
            if (currentBasicBlock.isEnd()) {  // 如果在 BlockItem 中将 currentBasicBlock 的 isEnd 设置为 true, 则语法树中这个 Block 中后续的 BlockItem 不再翻译成 llvm
                break;
            }
        }
    }

    public void BlockItemVisitor(BlockItem node) {  // BlockItem -> Decl | Stmt
        if (node.getDecl() != null) {
            DeclVisitor(node.getDecl());
        }
        if (node.getStmt() != null) {
            StmtVisitor(node.getStmt());
        }
    }

    /* 0. Stmt -> Block
     * 1. Stmt -> 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
     * 2. Stmt -> 'while' '(' Cond ')' Stmt
     * 3. Stmt -> 'break' ';'
     * 4. Stmt -> 'continue' ';'
     * 5. Stmt -> 'return' [Exp] ';'
     * 6. Stmt -> 'printf''('FormatString{','Exp}')'';'
     * 7. Stmt -> LVal '=' Exp ';'
     * 8. Stmt -> LVal '=' 'getint''('')'';'
     * 9. Stmt -> [Exp] ';'
     */
    public void StmtVisitor(Stmt node) {
        if (node.getType() == 0) {  // Stmt -> Block
            currentSymTable = SymGenerator.table.get(node.getBlock());
            BlockVisitor(node.getBlock());
            currentSymTable = currentSymTable.getParent();
        } else if (node.getType() == 1) {  // Stmt -> 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            BasicBlock trueBlock = new BasicBlock(null, currentFunction);
            BasicBlock falseBlock = (node.getStmt2() != null) ? new BasicBlock(null, currentFunction) : null;
            BasicBlock outBlock = new BasicBlock(null, currentFunction);
            // 处理 Cond
            if (falseBlock == null) {
                Value cond = CondVisitor(node.getCond(), trueBlock, outBlock);
                new BrInst(currentBasicBlock, cond, trueBlock, outBlock, null);
            } else {
                Value cond = CondVisitor(node.getCond(), trueBlock, falseBlock);
                new BrInst(currentBasicBlock, cond, trueBlock, falseBlock, null);
            }
            currentBasicBlock.setEnd(true);
            // 处理 if Stmt
            setCurBasicBlock(trueBlock);  //
            StmtVisitor(node.getStmt1());
            new BrInst(currentBasicBlock, null, null, null, outBlock);
            currentBasicBlock.setEnd(true);
            // 处理 else Stmt
            if (falseBlock != null) {
                setCurBasicBlock(falseBlock);  //
                StmtVisitor(node.getStmt2());
                new BrInst(currentBasicBlock, null, null, null, outBlock);
                currentBasicBlock.setEnd(true);
            }
            // 结束
            setCurBasicBlock(outBlock);  //
        } else if (node.getType() == 2) {  // Stmt -> 'while' '(' Cond ')' Stmt
            BasicBlock condBlock = new BasicBlock(null, currentFunction);
            BasicBlock loopBlock = new BasicBlock(null, currentFunction);
            BasicBlock outBlock = new BasicBlock(null, currentFunction);
            // 开始
            currentCondBlock.push(condBlock);
            currentOutBlock.push(outBlock);
            new BrInst(currentBasicBlock, null, null, null, condBlock);
            currentBasicBlock.setEnd(true);
            // 处理 Cond
            setCurBasicBlock(condBlock);  //
            Value cond = CondVisitor(node.getCond(), loopBlock, outBlock);
            new BrInst(currentBasicBlock, cond, loopBlock, outBlock, null);
            currentBasicBlock.setEnd(true);
            // 处理 Stmt
            setCurBasicBlock(loopBlock);  //
            StmtVisitor(node.getStmt1());
            new BrInst(currentBasicBlock, null, null, null, condBlock);
            currentBasicBlock.setEnd(true);
            // 结束
            currentCondBlock.pop();
            currentOutBlock.pop();
            setCurBasicBlock(outBlock);  //
        } else if (node.getType() == 3) {  // Stmt -> 'break' ';'
            new BrInst(currentBasicBlock, null, null, null, currentOutBlock.peek());
            currentBasicBlock.setEnd(true);
        } else if (node.getType() == 4) {  // Stmt -> 'continue' ';'
            new BrInst(currentBasicBlock, null, null, null, currentCondBlock.peek());
            currentBasicBlock.setEnd(true);
        } else if (node.getType() == 5) {  // Stmt -> 'return' [Exp] ';'
            Value ret;
            if (node.getExp() != null) {
                ret = ExpVisitor(node.getExp());
            } else {
                ret = new Value(null, new BaseType(BaseType.Tag.VOID));
            }
            new RetInst(currentBasicBlock, ret);
            currentBasicBlock.setEnd(true);
        } else if (node.getType() == 6) {  // Stmt -> 'printf''('FormatString{','Exp}')'';'
            String fmtStr = node.getFormatString().getSrc();  // fmtStr 中包括两个引号 "
            ArrayList<Value> expValue = new ArrayList<>();
            int size = node.getExps().size();
            for (int i = 0; i < size; i++) {
                expValue.add(ExpVisitor(node.getExps().get(i)));
            }
            int len = fmtStr.length();
            for (int i = 1, j = 0; i < len - 1; i++) {
                if (fmtStr.charAt(i) == '%') {
                    new PutInst(currentBasicBlock, expValue.get(j), true);
                    i++;
                    j++;
                } else if (fmtStr.charAt(i) == '\\') {
                    Value dst = new Value(Integer.toString('\n'), new BaseType(BaseType.Tag.I32));
                    new PutInst(currentBasicBlock, dst, false);
                    i++;
                } else {
                    Value dst = new Value(Integer.toString(fmtStr.charAt(i)), new BaseType(BaseType.Tag.I32));
                    new PutInst(currentBasicBlock, dst, false);
                }
            }
        } else if (node.getType() == 7) {  // Stmt -> LVal '=' Exp ';'
            Value src = ExpVisitor(node.getExp());
            Value dst = LvalVisitor(node.getLval(), true);
            new StoreInst(currentBasicBlock, src, dst);
        } else if (node.getType() == 8) {  // Stmt -> LVal '=' 'getint''('')'';'
            Value src = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
            regID++;
            new GetInst(currentBasicBlock, src);
            Value dst = LvalVisitor(node.getLval(), true);
            new StoreInst(currentBasicBlock, src, dst);
        } else {  // Stmt -> [Exp] ';'
            if (node.getExp() != null) {
                ExpVisitor(node.getExp());
            }
        }
    }

    public Value ExpVisitor(Exp node) {  // Exp -> AddExp
        return AddExpVisitor(node.getAddExp());
    }

    public Value CondVisitor(Cond node, BasicBlock trueBlock, BasicBlock falseBlock) {  // Cond -> LOrExp
        return LOrExpVisitor(node.getlOrExp(), trueBlock, falseBlock);
    }

    /* isLeft = true 的情况
     *      Stmt -> Lval '=' Exp ';'
     *      Stmt -> Lval '=' 'getint' '(' ')' ';'
     * isLeft = false 的情况
     *      PrimaryExp -> Lval
     *
     * Def / FuncParam
     * 变量 / 数组
     *      FuncParam 且数组时
     *          需要 LoadInst 将 addr 中的内容取出
     *          如果有 exp, FuncParam 中 GEPInst 的 indexs 直接从 exp 开始
     *          如果无 exp, FuncParam 中直接返回
     * isLeft : isLeft = true 时, 不需要 LoadInst, 返回的是 PointerType
     */
    public Value LvalVisitor(Lval node, boolean isLeft) {  // Lval -> Ident {'[' Exp ']'}
        Symbol symbol = currentSymTable.getSymbol(node.getIdent().getSrc(), true, node.getIdent().getLineNum());
        if (symbol instanceof Def) {
            Def def = (Def) symbol;
            Value src = def.getAddr();
            if (def.getDimension() == 0) {  // 变量
                if (isLeft) {
                    return src;  // src 是符号表中的 addr, PointerType
                } else {
                    Value dst = new Value("%" + regID, src.getType().getInnerType());
                    regID++;
                    new LoadInst(currentBasicBlock, src, dst);  // src 是符号表中的 addr, PointerType; dst 是 BaseType I32
                    return dst;  // dst 是 LoadInst 的结果, BaseType I32
                }
            } else {  // 数组
                Value dst = src;  // dst 是符号表中的 addr, PointerType
                ArrayList<Value> indexs = new ArrayList<>();
                if (node.getExp().size() != 0) {
                    Type type = src.getType().getInnerType();
                    indexs.add(new Value("0", new BaseType(BaseType.Tag.I32)));
                    for (Exp exp : node.getExp()) {
                        type = type.getInnerType();
                        indexs.add(ExpVisitor(exp));
                    }
                    dst = new Value("%" + regID, new PointerType(type));
                    regID++;
                    new GEPInst(currentBasicBlock, src, dst, indexs);
                }
                if (def.getDimension() != node.getExp().size()) {
                    indexs = new ArrayList<>();
                    indexs.add(new Value("0", new BaseType(BaseType.Tag.I32)));
                    indexs.add(new Value("0", new BaseType(BaseType.Tag.I32)));
                    Value tmp = dst;
                    dst = new Value("%" + regID, new PointerType(dst.getType().getInnerType().getInnerType()));
                    regID++;
                    new GEPInst(currentBasicBlock, tmp, dst, indexs);
                    return dst;  // dst 是符号表中的 addr 或 GEPInst 的结果
                }
                if (isLeft) {
                    return dst;  // dst 是符号表中的 addr 或 GEPInst 的结果
                } else {
                    Value tmp = dst;  // dst 是符号表中的 addr 或 GEPInst 的结果
                    dst = new Value("%" + regID, tmp.getType().getInnerType());
                    regID++;
                    new LoadInst(currentBasicBlock, tmp, dst);
                    return dst;  // dst 是 LoadInst 的结果
                }
            }
        } else {
            FuncParam funcParam = (FuncParam) symbol;
            Value src = funcParam.getAddr();
            if (funcParam.getDimension() == 0) {
                if (isLeft) {
                    return src;
                } else {
                    Value dst = new Value("%" + regID, src.getType().getInnerType());
                    regID++;
                    new LoadInst(currentBasicBlock, src, dst);
                    return dst;
                }
            } else {
                Value dst = new Value("%" + regID, src.getType().getInnerType());  // 与 Def 不同: FuncParam 且数组时, 需要 LoadInst 将 addr 中的内容取出
                regID++;  //..
                new LoadInst(currentBasicBlock, src, dst);  //..
                src = dst;  // 与 Def 不同: 一维数组时, Def 中是 [2 x i32]*, FuncParam 中是 i32*; 二维数组时, Def 中是 [2 x [3 x i32]]*, FuncParam 中是 [2 x i32]*
                ArrayList<Value> indexs = new ArrayList<>();
                if (node.getExp().size() != 0) {
                    Type type = src.getType();  //  与 Def 不同: 如果有 exp, FuncParam 中 GEPInst 的 indexs 直接从 exp 开始
                    // indexs.add(new Value("0", new BaseType(BaseType.Tag.I32)));  //..
                    for (Exp exp : node.getExp()) {
                        type = type.getInnerType();
                        indexs.add(ExpVisitor(exp));
                    }
                    dst = new Value("%" + regID, new PointerType(type));
                    regID++;
                    new GEPInst(currentBasicBlock, src, dst, indexs);
                }
                if (funcParam.getDimension() != node.getExp().size()) {
                    if (node.getExp().size() == 0) {  // 与 Def 不同: 如果无 exp, FuncParam 中直接返回
                        return dst;  //..
                    }  //..
                    indexs = new ArrayList<>();
                    indexs.add(new Value("0", new BaseType(BaseType.Tag.I32)));
                    indexs.add(new Value("0", new BaseType(BaseType.Tag.I32)));
                    Value tmp = dst;
                    dst = new Value("%" + regID, new PointerType(dst.getType().getInnerType().getInnerType()));
                    regID++;
                    new GEPInst(currentBasicBlock, tmp, dst, indexs);  // 与 Def 相同
                    return dst;
                }
                if (isLeft) {
                    return dst;
                } else {
                    Value tmp = dst;
                    dst = new Value("%" + regID, tmp.getType().getInnerType());
                    regID++;
                    new LoadInst(currentBasicBlock, tmp, dst);
                    return dst;
                }
            }
        }
    }

    public Value PrimaryExpVisitor(PrimaryExp node) {  // PrimaryExp -> '(' Exp ')' | Lval | Number
        if (node.getExp() != null) {
            return ExpVisitor(node.getExp());
        } else if (node.getLval() != null) {
            return LvalVisitor(node.getLval(), false);
        } else {
            return NumberVisitor(node.getNumber());
        }
    }

    public Value NumberVisitor(front.ASD.Number node) {  // Number -> IntConst
        return new Value(Integer.toString(node.getNumber().getIntVal()), new BaseType(BaseType.Tag.I32));
    }

    public Value UnaryExpVisitor(UnaryExp node) {  // UnaryExp -> PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        if (node.getPrimaryExp() != null) {  // UnaryExp -> PrimaryExp
            return PrimaryExpVisitor(node.getPrimaryExp());
        } else if (node.getIdent() != null) {  // UnaryExp -> Ident '(' [FuncRParams] ')'
            Func func = (Func)currentSymTable.getSymbol(node.getIdent().getSrc(), true, node.getIdent().getLineNum());
            String funcName = "@" + node.getIdent().getSrc();
            Type funcType;
            Value dst;
            ArrayList<Value> params = new ArrayList<>();
            if (node.getFuncRParams() != null) {
                params = FuncRParamsVisitor(node.getFuncRParams());
            }
            if (func.isVoid()) {
                funcType = new BaseType(BaseType.Tag.VOID);
                dst = null;
            } else {
                funcType = new BaseType(BaseType.Tag.I32);
                dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                regID++;
            }
            new CallInst(currentBasicBlock, new Value(funcName, funcType), dst, params);
            return dst;
        } else {  // UnaryExp -> UnaryOp UnaryExp
            Value src1 = new Value("0", new BaseType(BaseType.Tag.I32));
            Value src2 = UnaryExpVisitor(node.getUnaryExp());
            src2 = ifNeedZextInst(src2, BaseType.Tag.I32);  //
            Value dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
            regID++;
            if (node.getUnaryOp().getOp().getSrc().equals("+")) {
                new BinaryInst(currentBasicBlock, BinaryInst.Tag.ADD, dst, src1, src2);
            } else if (node.getUnaryOp().getOp().getSrc().equals("-")) {
                new BinaryInst(currentBasicBlock, BinaryInst.Tag.SUB, dst, src1, src2);
            } else {
                dst.setType(new BaseType(BaseType.Tag.I1));
                new BinaryInst(currentBasicBlock, BinaryInst.Tag.EQ, dst, src1, src2);
            }
            return dst;
        }
    }

    /* public String UnaryOpVisitor(UnaryOp node) { return null; } */

    public ArrayList<Value> FuncRParamsVisitor(FuncRParams node) {  // FuncRParams -> Exp { ',' Exp }
        ArrayList<Value> values = new ArrayList<>();
        for (Exp value : node.getExp()) {
            values.add(ExpVisitor(value));
        }
        return values;
    }

    public Value MulExpVisitor(MulExp node) {  // MulExp -> UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        int size = node.getUnaryExp().size();
        if (size == 1) {
            return UnaryExpVisitor(node.getUnaryExp().get(0));
        } else {
            Value dst = null, src1, src2;
            src1 = UnaryExpVisitor(node.getUnaryExp().get(0));
            for (int i = 1; i < size; i++) {
                src2 = UnaryExpVisitor(node.getUnaryExp().get(i));
                src1 = ifNeedZextInst(src1, BaseType.Tag.I32);  //
                src2 = ifNeedZextInst(src2, BaseType.Tag.I32);  //
                dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                regID++;
                if (node.getOps().get(i - 1).getSrc().equals("*")) {
                    new BinaryInst(currentBasicBlock, BinaryInst.Tag.MUL, dst, src1, src2);
                } else if (node.getOps().get(i - 1).getSrc().equals("/")) {
                    new BinaryInst(currentBasicBlock, BinaryInst.Tag.SDIV, dst, src1, src2);
                } else {
                    new BinaryInst(currentBasicBlock, BinaryInst.Tag.SREM, dst, src1, src2);
                }
                src1 = dst;
            }
            return dst;
        }
    }

    public Value AddExpVisitor(AddExp node) {  // AddExp -> MulExp | AddExp ('+' | '−') MulExp
        int size = node.getMulExp().size();
        if (size == 1) {
            return MulExpVisitor(node.getMulExp().get(0));
        } else {
            Value dst = null, src1, src2;
            src1 = MulExpVisitor(node.getMulExp().get(0));
            for (int i = 1; i < size; i++) {
                src2 = MulExpVisitor(node.getMulExp().get(i));
                src1 = ifNeedZextInst(src1, BaseType.Tag.I32);  //
                src2 = ifNeedZextInst(src2, BaseType.Tag.I32);  //
                dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                regID++;
                if (node.getOps().get(i - 1).getSrc().equals("+")) {
                    new BinaryInst(currentBasicBlock, BinaryInst.Tag.ADD, dst, src1, src2);
                } else {
                    new BinaryInst(currentBasicBlock, BinaryInst.Tag.SUB, dst, src1, src2);
                }
                src1 = dst;
            }
            return dst;
        }
    }

    public Value RelExpVisitor(RelExp node) {  // RelExp -> AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
        int size = node.getAddExp().size();
        if (size == 1) {
            return AddExpVisitor(node.getAddExp().get(0));  // 返回 I32
        } else {
            Value dst = null, src1, src2;
            src1 = AddExpVisitor(node.getAddExp().get(0));
            for (int i = 1; i < size; i++) {
                src2 = AddExpVisitor(node.getAddExp().get(i));
                src1 = ifNeedZextInst(src1, BaseType.Tag.I32);  //
                src2 = ifNeedZextInst(src2, BaseType.Tag.I32);  //
                dst = new Value("%" + regID, new BaseType(BaseType.Tag.I1));
                regID++;
                if (node.getOps().get(i - 1).getSrc().equals("<")) {
                    new BinaryInst(currentBasicBlock, BinaryInst.Tag.SLT, dst, src1, src2);
                } else if (node.getOps().get(i - 1).getSrc().equals(">")) {
                    new BinaryInst(currentBasicBlock, BinaryInst.Tag.SGT, dst, src1, src2);
                } else if (node.getOps().get(i - 1).getSrc().equals("<=")) {
                    new BinaryInst(currentBasicBlock, BinaryInst.Tag.SLE, dst, src1, src2);
                } else {
                    new BinaryInst(currentBasicBlock, BinaryInst.Tag.SGE, dst, src1, src2);
                }
                src1 = dst;
            }
            return dst;  // 返回 I1
        }
    }

    public Value EqExpVisitor(EqExp node) {  // EqExp -> RelExp | EqExp ('==' | '!=') RelExp
        int size = node.getRelExp().size();
        if (size == 1) {
            Value value = RelExpVisitor(node.getRelExp().get(0));
            if (((BaseType)value.getType()).getTag() == BaseType.Tag.I32) {
                Value dst = new Value("%" + regID, new BaseType(BaseType.Tag.I1));
                regID++;
                new BinaryInst(currentBasicBlock, BinaryInst.Tag.NE, dst, value, new Value("0", new BaseType(BaseType.Tag.I32)));
                value = dst;
            }
            return value;  // 返回 I1
        } else {
            Value dst = null, src1, src2;
            src1 = RelExpVisitor(node.getRelExp().get(0));
            for (int i = 1; i < size; i++) {
                src2 = RelExpVisitor(node.getRelExp().get(i));
                src1 = ifNeedZextInst(src1, BaseType.Tag.I32);  //
                src2 = ifNeedZextInst(src2, BaseType.Tag.I32);  //
                dst = new Value("%" + regID, new BaseType(BaseType.Tag.I1));
                regID++;
                if (node.getOps().get(i - 1).getSrc().equals("==")) {
                    new BinaryInst(currentBasicBlock, BinaryInst.Tag.EQ, dst, src1, src2);
                } else {
                    new BinaryInst(currentBasicBlock, BinaryInst.Tag.NE, dst, src1, src2);
                }
                src1 = dst;
            }
            return dst;  // 返回 I1
        }
    }

    public Value LAndExpVisitor(LAndExp node, BasicBlock trueBlock, BasicBlock falseBlock) {  // LAndExp -> EqExp | LAndExp '&&' EqExp
        int size = node.getEqExp().size();
        Value cond;
        for (int i = 0; i < size - 1; i++) {
            BasicBlock  nextBlock = new BasicBlock(null, currentFunction);
            cond = EqExpVisitor(node.getEqExp().get(i));
            new BrInst(currentBasicBlock, cond, nextBlock, falseBlock, null);
            currentBasicBlock.setEnd(true);
            setCurBasicBlock(nextBlock);
        }
        cond = EqExpVisitor(node.getEqExp().get(size - 1));  // 最后一个判断的 BrInst 在调用此函数的函数中, 即在 LOrExpVisitor 中
        return cond;
    }

    public Value LOrExpVisitor(LOrExp node, BasicBlock trueBlock, BasicBlock falseBlock) {  // LOrExp -> LAndExp | LOrExp '||' LAndExp
        int size = node.getlAndExp().size();
        Value cond;
        for (int i = 0; i < size - 1; i++) {
            BasicBlock nextBlock = new BasicBlock(null, currentFunction);
            cond = LAndExpVisitor(node.getlAndExp().get(i), trueBlock, nextBlock);
            new BrInst(currentBasicBlock, cond, trueBlock, nextBlock, null);
            currentBasicBlock.setEnd(true);
            setCurBasicBlock(nextBlock);
        }
        cond = LAndExpVisitor(node.getlAndExp().get(size - 1), trueBlock, falseBlock);  // 最后一个判断的 BrInst 在调用此函数的函数中, 即在 StmtVisitor 中
        return cond;
    }

    /* public String ConstExpVisitor(ConstExp node) { return null; } */
}
