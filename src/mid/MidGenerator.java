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

    public void generate(Node node) {
        CompUnitVisitor((CompUnit)node);
    }

    public void CompUnitVisitor(CompUnit node) {
        for (Decl value : node.getDecl()) {
            DeclVisitor(value);
        }
        for (FuncDef value : node.getFuncDef()) {
            FuncDefVisitor(value);
        }
        MainFuncDefVisitor(node.getMainFuncDef());
    }

    public void DeclVisitor(Decl node) {
        if (node.getConstDecl() != null) {
            ConstDeclVisitor(node.getConstDecl());
        }
        if (node.getVarDecl() != null) {
            VarDeclVisitor(node.getVarDecl());
        }
    }

    public void ConstDeclVisitor(ConstDecl node) {
        for (ConstDef value : node.getConstDef()) {
            ConstDefVisitor(value);
        }
    }

    public void ConstDefVisitor(ConstDef node) {
        Def def = (Def)currentSymTable.getSymbol(node.getIdent().getSrc(), false, node.getIdent().getLineNum());
        Type type = new BaseType(BaseType.Tag.I32);
        int capacity = 1;
        for (int i = def.getDimension() - 1; i >= 0 ; i--) {
            capacity *= def.getSize().get(i);
            type = new ArrayType(def.getSize().get(i), type, capacity);
        }
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

    public void ConstInitValVisitor(ConstInitVal node, Value value, Def def) {
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
                    ConstInitValVisitor(child, value, def);
                }
            }
        }
    }

    public void VarDeclVisitor(VarDecl node) {
        for (VarDef value : node.getVarDef()) {
            VarDefVisitor(value);
        }
    }

    public void VarDefVisitor(VarDef node) {
        Def def = (Def)currentSymTable.getSymbol(node.getIdent().getSrc(), false, node.getIdent().getLineNum());
        Type type = new BaseType(BaseType.Tag.I32);
        int capacity = 1;
        for (int i = def.getDimension() - 1; i >= 0 ; i--) {
            capacity *= def.getSize().get(i);
            type = new ArrayType(def.getSize().get(i), type, capacity);
        }
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

    public void InitValVisitor(InitVal node, Value value, Def def) {
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
                    InitValVisitor(child, value, def);
                }
            }
        }
    }

    public void FuncDefVisitor(FuncDef node) {
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
            new RetInst(currentBasicBlock, new Value(null, new BaseType(BaseType.Tag.VOID)));
        }
    }

    public void MainFuncDefVisitor(MainFuncDef node) {
        currentFunction = new Function("@main", new BaseType(BaseType.Tag.I32));
        currentBasicBlock = new BasicBlock("0", currentFunction);
        currentFunction.getBasicBlocks().add(currentBasicBlock);
        regID = 1;
        currentSymTable = SymGenerator.table.get(node.getBlock());
        BlockVisitor(node.getBlock());
        currentSymTable = currentSymTable.getParent();
    }

    /* public String FuncTypeVisitor(FuncType node) { return null; } */

    public ArrayList<Pair<Value, FuncParam>> FuncFParamsVisitor(FuncFParams node) {
        ArrayList<Pair<Value, FuncParam>> addrs = new ArrayList<>();
        for (FuncFParam value : node.getFuncFParam()) {
            Pair<Value, FuncParam> addr = FuncFParamVisitor(value);
            addrs.add(addr);
        }
        return addrs;
    }

    public Pair<Value, FuncParam> FuncFParamVisitor(FuncFParam node) {
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

    public void BlockVisitor(Block node) {
        for (BlockItem value : node.getBlockItem()) {
            BlockItemVisitor(value);
            if (currentBasicBlock.isEnd()) {
                break;
            }
        }
    }

    public void BlockItemVisitor(BlockItem node) {
        if (node.getDecl() != null) {
            DeclVisitor(node.getDecl());
        }
        if (node.getStmt() != null) {
            StmtVisitor(node.getStmt());
        }
    }

    public void StmtVisitor(Stmt node) {
        if (node.getType() == 0) {  // Stmt -> Block
            currentSymTable = SymGenerator.table.get(node.getBlock());
            BlockVisitor(node.getBlock());
            currentSymTable = currentSymTable.getParent();
        } else if (node.getType() == 1) {  // Stmt -> 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            BasicBlock trueBlock = new BasicBlock(null, currentFunction);
            BasicBlock falseBlock = (node.getStmt2() != null) ? new BasicBlock(null, currentFunction) : null;
            BasicBlock outBlock = new BasicBlock(null, currentFunction);
            if (falseBlock == null) {
                Value cond = CondVisitor(node.getCond(), trueBlock, outBlock);
                new BrInst(currentBasicBlock, cond, trueBlock, outBlock, null);
                currentBasicBlock.setEnd(true);
            } else {
                Value cond = CondVisitor(node.getCond(), trueBlock, falseBlock);
                new BrInst(currentBasicBlock, cond, trueBlock, falseBlock, null);
                currentBasicBlock.setEnd(true);
            }
            currentBasicBlock = trueBlock;
            currentFunction.getBasicBlocks().add(currentBasicBlock);
            currentBasicBlock.setName(Integer.toString(regID));
            regID++;
            StmtVisitor(node.getStmt1());
            new BrInst(currentBasicBlock, null, null, null, outBlock);
            currentBasicBlock.setEnd(true);
            if (falseBlock != null) {
                currentBasicBlock = falseBlock;
                currentFunction.getBasicBlocks().add(currentBasicBlock);
                currentBasicBlock.setName(Integer.toString(regID));
                regID++;
                StmtVisitor(node.getStmt2());
                new BrInst(currentBasicBlock, null, null, null, outBlock);
                currentBasicBlock.setEnd(true);
            }
            currentBasicBlock = outBlock;
            currentFunction.getBasicBlocks().add(currentBasicBlock);
            currentBasicBlock.setName(Integer.toString(regID));
            regID++;
        } else if (node.getType() == 2) {  // Stmt -> 'while' '(' Cond ')' Stmt
            BasicBlock condBlock = new BasicBlock(null, currentFunction);
            BasicBlock loopBlock = new BasicBlock(null, currentFunction);
            BasicBlock outBlock = new BasicBlock(null, currentFunction);
            currentCondBlock.push(condBlock);
            currentOutBlock.push(outBlock);
            new BrInst(currentBasicBlock, null, null, null, condBlock);
            currentBasicBlock.setEnd(true);
            currentBasicBlock = condBlock;
            currentFunction.getBasicBlocks().add(currentBasicBlock);
            currentBasicBlock.setName(Integer.toString(regID));
            regID++;
            Value cond = CondVisitor(node.getCond(), loopBlock, outBlock);
            new BrInst(currentBasicBlock, cond, loopBlock, outBlock, null);
            currentBasicBlock.setEnd(true);
            currentBasicBlock = loopBlock;
            currentFunction.getBasicBlocks().add(currentBasicBlock);
            currentBasicBlock.setName(Integer.toString(regID));
            regID++;
            StmtVisitor(node.getStmt1());
            new BrInst(currentBasicBlock, null, null, null, condBlock);
            currentBasicBlock.setEnd(true);
            currentCondBlock.pop();
            currentOutBlock.pop();
            currentBasicBlock = outBlock;
            currentFunction.getBasicBlocks().add(currentBasicBlock);
            currentBasicBlock.setName(Integer.toString(regID));
            regID++;
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

    public Value ExpVisitor(Exp node) {
        return AddExpVisitor(node.getAddExp());
    }

    public Value CondVisitor(Cond node, BasicBlock trueBlock, BasicBlock falseBlock) {
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
    public Value LvalVisitor(Lval node, boolean isLeft) {
        Symbol symbol = currentSymTable.getSymbol(node.getIdent().getSrc(), true, node.getIdent().getLineNum());
        if (symbol instanceof Def) {  // 变量
            Def def = (Def) symbol;
            Value src = def.getAddr();
            if (def.getDimension() == 0) {
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

    public Value PrimaryExpVisitor(PrimaryExp node) {
        if (node.getExp() != null) {
            return ExpVisitor(node.getExp());
        } else if (node.getLval() != null) {
            return LvalVisitor(node.getLval(), false);
        } else {
            return NumberVisitor(node.getNumber());
        }
    }

    public Value NumberVisitor(front.ASD.Number node) {
        return new Value(Integer.toString(node.getNumber().getIntVal()), new BaseType(BaseType.Tag.I32));
    }

    public Value UnaryExpVisitor(UnaryExp node) {
        if (node.getPrimaryExp() != null) {
            return PrimaryExpVisitor(node.getPrimaryExp());
        } else if (node.getIdent() != null) {
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
        } else {
            Value src1 = new Value("0", new BaseType(BaseType.Tag.I32));
            Value src2 = UnaryExpVisitor(node.getUnaryExp());
            if (((BaseType)src2.getType()).getTag() != BaseType.Tag.I32) {
                Value tmp =  new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                regID++;
                new ZextInst(currentBasicBlock, src2, tmp);
                src2 = tmp;
            }
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

    public ArrayList<Value> FuncRParamsVisitor(FuncRParams node) {
        ArrayList<Value> values = new ArrayList<>();
        for (Exp value : node.getExp()) {
            values.add(ExpVisitor(value));
        }
        return values;
    }

    public Value MulExpVisitor(MulExp node) {
        int size = node.getUnaryExp().size();
        if (size == 1) {
            return UnaryExpVisitor(node.getUnaryExp().get(0));
        } else {
            Value dst = null, src1, src2;
            src1 = UnaryExpVisitor(node.getUnaryExp().get(0));
            for (int i = 1; i < size; i++) {
                src2 = UnaryExpVisitor(node.getUnaryExp().get(i));
                if (((BaseType)src1.getType()).getTag() != BaseType.Tag.I32) {
                    Value tmp =  new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    new ZextInst(currentBasicBlock, src1, tmp);
                    src1 = tmp;
                }
                if (((BaseType)src2.getType()).getTag() != BaseType.Tag.I32) {
                    Value tmp =  new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    new ZextInst(currentBasicBlock, src2, tmp);
                    src2 = tmp;
                }
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

    public Value AddExpVisitor(AddExp node) {
        int size = node.getMulExp().size();
        if (size == 1) {
            return MulExpVisitor(node.getMulExp().get(0));
        } else {
            Value dst = null, src1, src2;
            src1 = MulExpVisitor(node.getMulExp().get(0));
            for (int i = 1; i < size; i++) {
                src2 = MulExpVisitor(node.getMulExp().get(i));
                if (((BaseType)src1.getType()).getTag() != BaseType.Tag.I32) {
                    Value tmp =  new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    new ZextInst(currentBasicBlock, src1, tmp);
                    src1 = tmp;
                }
                if (((BaseType)src2.getType()).getTag() != BaseType.Tag.I32) {
                    Value tmp =  new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    new ZextInst(currentBasicBlock, src2, tmp);
                    src2 = tmp;
                }
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

    public Value RelExpVisitor(RelExp node) {
        int size = node.getAddExp().size();
        if (size == 1) {
            /*Value value = AddExpVisitor(node.getAddExp().get(0));
            if (((BaseType)value.getType()).getTag() == BaseType.Tag.I32) {
                Value dst = new Value("%" + regID, new BaseType(BaseType.Tag.I1));
                regID++;
                new BinaryInst(currentBasicBlock, BinaryInst.Tag.NE, dst, value, new Value("0", new BaseType(BaseType.Tag.I32)));
                return dst;
            } else {
                return value;
            }*/
            return AddExpVisitor(node.getAddExp().get(0));
        } else {
            Value dst = null, src1, src2;
            src1 = AddExpVisitor(node.getAddExp().get(0));
            for (int i = 1; i < size; i++) {
                src2 = AddExpVisitor(node.getAddExp().get(i));
                if (((BaseType)src1.getType()).getTag() != BaseType.Tag.I32) {
                    Value tmp = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    new ZextInst(currentBasicBlock, src1, tmp);
                    src1 = tmp;
                }
                if (((BaseType)src2.getType()).getTag() != BaseType.Tag.I32) {
                    Value tmp = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    new ZextInst(currentBasicBlock, src2, tmp);
                    src2 = tmp;
                }
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
            return dst;
        }
    }

    public Value EqExpVisitor(EqExp node) {
        int size = node.getRelExp().size();
        if (size == 1) {
            Value value = RelExpVisitor(node.getRelExp().get(0));
            if (((BaseType)value.getType()).getTag() == BaseType.Tag.I32) {
                Value dst = new Value("%" + regID, new BaseType(BaseType.Tag.I1));
                regID++;
                new BinaryInst(currentBasicBlock, BinaryInst.Tag.NE, dst, value, new Value("0", new BaseType(BaseType.Tag.I32)));
                return dst;
            } else {
                return value;
            }
        } else {
            Value dst = null, src1, src2;
            src1 = RelExpVisitor(node.getRelExp().get(0));
            for (int i = 1; i < size; i++) {
                src2 = RelExpVisitor(node.getRelExp().get(i));
                if (((BaseType)src1.getType()).getTag() != BaseType.Tag.I32) {
                    Value tmp = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    new ZextInst(currentBasicBlock, src1, tmp);
                    src1 = tmp;
                }
                if (((BaseType)src2.getType()).getTag() != BaseType.Tag.I32) {
                    Value tmp = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    new ZextInst(currentBasicBlock, src2, tmp);
                    src2 = tmp;
                }
                dst = new Value("%" + regID, new BaseType(BaseType.Tag.I1));
                regID++;
                if (node.getOps().get(i - 1).getSrc().equals("==")) {
                    new BinaryInst(currentBasicBlock, BinaryInst.Tag.EQ, dst, src1, src2);
                } else {
                    new BinaryInst(currentBasicBlock, BinaryInst.Tag.NE, dst, src1, src2);
                }
                src1 = dst;
            }
            return dst;
        }
    }

    public Value LAndExpVisitor(LAndExp node, BasicBlock trueBlock, BasicBlock falseBlock) {
        int size = node.getEqExp().size();
        if (size == 1) {
            return EqExpVisitor(node.getEqExp().get(0));
        } else {
            Value cond = EqExpVisitor(node.getEqExp().get(0));
            BasicBlock nextBlock = new BasicBlock(Integer.toString(regID), currentFunction);
            regID++;
            new BrInst(currentBasicBlock, cond, nextBlock, falseBlock, null);
            for (int i = 1; i < size; i++) {
                currentBasicBlock.setEnd(true);
                currentBasicBlock = nextBlock;
                currentFunction.getBasicBlocks().add(currentBasicBlock);
                cond = EqExpVisitor(node.getEqExp().get(i));
                if (i != size - 1) {
                    nextBlock = new BasicBlock(Integer.toString(regID), currentFunction);
                    regID++;
                    new BrInst(currentBasicBlock, cond, nextBlock, falseBlock, null);
                }
            }
            return cond;
        }
    }

    public Value LOrExpVisitor(LOrExp node, BasicBlock trueBlock, BasicBlock falseBlock) {
        int size = node.getlAndExp().size();
        if (size == 1) {
            return LAndExpVisitor(node.getlAndExp().get(0), trueBlock, falseBlock);
        } else {
            Value cond = LAndExpVisitor(node.getlAndExp().get(0), trueBlock, falseBlock);
            BasicBlock nextBlock = new BasicBlock(Integer.toString(regID), currentFunction);
            regID++;
            new BrInst(currentBasicBlock, cond, trueBlock, nextBlock, null);
            for (int i = 1; i < size; i++) {
                currentBasicBlock.setEnd(true);
                currentBasicBlock = nextBlock;
                currentFunction.getBasicBlocks().add(currentBasicBlock);
                if (i != size - 1) {
                    BasicBlock tmpBlock = new BasicBlock(null, currentFunction);
                    cond = LAndExpVisitor(node.getlAndExp().get(i), trueBlock, tmpBlock);
                    nextBlock = new BasicBlock(Integer.toString(regID), currentFunction);
                    regID++;
                    tmpBlock.setName(nextBlock.getName());
                    new BrInst(currentBasicBlock, cond, trueBlock, nextBlock, null);
                } else {
                    cond = LAndExpVisitor(node.getlAndExp().get(i), trueBlock, falseBlock);
                }
            }
            return cond;
        }
    }

    /* public String ConstExpVisitor(ConstExp node) { return null; } */
}
