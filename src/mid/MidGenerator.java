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

    public MidGenerator() {
        this.regID = 0;
        this.currentFunction = null;
        this.currentBasicBlock = null;
        this.currentCondBlock = new Stack<>();
        this.currentOutBlock = new Stack<>();
        this.currentSymTable = SymGenerator.table.get(SynAnalyzer.root);
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
        String name;
        Type type;
        if (def.getDimension() == 0) {
            type = new BaseType(BaseType.Tag.I32);
        } else if (def.getDimension() == 1) {
            type = new ArrayType(def.getSize().get(0), new BaseType(BaseType.Tag.I32));
        } else {
            type = new ArrayType(def.getSize().get(0), new ArrayType(def.getSize().get(1), new BaseType(BaseType.Tag.I32)));
        }
        if (currentSymTable.getDepth() == 0) {
            name = "@" + node.getIdent().getSrc();
            new GlobalVariable(name, type, true,
                    def.getSize().get(0), def.getSize().get(1),
                    def.getInitVal(), def.getInitArrayVal(), false);
            def.setAddr(new Value(name, type));
        } else {
            name = "%" + regID;
            regID++;
            Value dst = new Value(name, type);
            new AllocaInst(currentBasicBlock, dst);
            def.setAddr(dst);
            ConstInitValVisitor(node.getConstInitVal(), dst, def);
        }
    }

    public void ConstInitValVisitor(ConstInitVal node, Value value, Def def) {
        if (def.getDimension() == 0) {
            Value src = new Value(Integer.toString(def.getInitVal()), new BaseType(BaseType.Tag.I32));
            new StoreInst(currentBasicBlock, src, value);
        } else if (def.getDimension() == 1) {
            int size1 = def.getSize().get(0);
            ArrayList<Integer> initArrayVal = def.getInitArrayVal();
            for (int i = 0; i < size1; i++) {
                Value dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                regID++;
                ArrayList<Value> indexs = new ArrayList<>();
                indexs.add(new Value(Integer.toString(0), new BaseType(BaseType.Tag.I32)));
                indexs.add(new Value(Integer.toString(i), new BaseType(BaseType.Tag.I32)));
                new GEPInst(currentBasicBlock, value, dst, indexs);
                Value src = new Value(Integer.toString(initArrayVal.get(i)), new BaseType(BaseType.Tag.I32));
                new StoreInst(currentBasicBlock, src, dst);
            }
        } else {
            int size1 = def.getSize().get(0);
            int size2 = def.getSize().get(1);
            ArrayList<Integer> initArrayVal = def.getInitArrayVal();
            for (int i = 0; i < size1; i++) {
                for (int j = 0; j < size2; j++) {
                    Value dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    ArrayList<Value> indexs = new ArrayList<>();
                    indexs.add(new Value(Integer.toString(0), new BaseType(BaseType.Tag.I32)));
                    indexs.add(new Value(Integer.toString(i), new BaseType(BaseType.Tag.I32)));
                    indexs.add(new Value(Integer.toString(j), new BaseType(BaseType.Tag.I32)));
                    new GEPInst(currentBasicBlock, value, dst, indexs);
                    Value src = new Value(Integer.toString(initArrayVal.get(i * size2 + j)), new BaseType(BaseType.Tag.I32));
                    new StoreInst(currentBasicBlock, src, dst);
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
        String name;
        Type type;
        if (def.getDimension() == 0) {
            type = new BaseType(BaseType.Tag.I32);
        } else if (def.getDimension() == 1) {
            type = new ArrayType(def.getSize().get(0), new BaseType(BaseType.Tag.I32));
        } else {
            type = new ArrayType(def.getSize().get(0), new ArrayType(def.getSize().get(1), new BaseType(BaseType.Tag.I32)));
        }
        if (currentSymTable.getDepth() == 0) {
            name = "@" + node.getIdent().getSrc();
            new GlobalVariable(name, type, false,
                    def.getSize().get(0), def.getSize().get(1),
                    def.getInitVal(), def.getInitArrayVal(), def.isZeroInit());
            def.setAddr(new Value(name, type));
        } else {
            name = "%" + regID;
            regID++;
            Value dst = new Value(name, type);
            new AllocaInst(currentBasicBlock, dst);
            def.setAddr(dst);
            if (node.getInitVal() != null) {
                InitValVisitor(node.getInitVal(), dst, def);
            }
        }
    }

    public void InitValVisitor(InitVal node, Value value, Def def) {
        if (def.getDimension() == 0) {
            Value src = ExpVisitor(node.getExp());
            new StoreInst(currentBasicBlock, src, value);
        } else if (def.getDimension() == 1) {
            int size1 = def.getSize().get(0);
            ArrayList<InitVal> initArrayVal = node.getInitVal();
            for (int i = 0; i < size1; i++) {
                Value dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                regID++;
                ArrayList<Value> indexs = new ArrayList<>();
                indexs.add(new Value(Integer.toString(0), new BaseType(BaseType.Tag.I32)));
                indexs.add(new Value(Integer.toString(i), new BaseType(BaseType.Tag.I32)));
                new GEPInst(currentBasicBlock, value, dst, indexs);
                Value src = ExpVisitor(initArrayVal.get(i).getExp());
                new StoreInst(currentBasicBlock, src, dst);
            }
        } else {
            int size1 = def.getSize().get(0);
            int size2 = def.getSize().get(1);
            ArrayList<InitVal> initArrayVal1 = node.getInitVal();
            for (int i = 0; i < size1; i++) {
                ArrayList<InitVal> initArrayVal2 = initArrayVal1.get(i).getInitVal();
                for (int j = 0; j < size2; j++) {
                    Value dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    ArrayList<Value> indexs = new ArrayList<>();
                    indexs.add(new Value(Integer.toString(0), new BaseType(BaseType.Tag.I32)));
                    indexs.add(new Value(Integer.toString(i), new BaseType(BaseType.Tag.I32)));
                    indexs.add(new Value(Integer.toString(j), new BaseType(BaseType.Tag.I32)));
                    new GEPInst(currentBasicBlock, value, dst, indexs);
                    Value src = ExpVisitor(initArrayVal2.get(j).getExp());
                    new StoreInst(currentBasicBlock, src, dst);
                }
            }
        }
    }

    public void FuncDefVisitor(FuncDef node) {
        String funcName = "@" + node.getIdent().getSrc();
        Type type = (node.getFuncType().getFuncType().getDst().equals("VOIDTK")) ?
                new BaseType(BaseType.Tag.VOID) : new BaseType(BaseType.Tag.I32);
        currentFunction = new Function(funcName, type);
        currentBasicBlock = new BasicBlock("0", currentFunction);
        currentFunction.getBasicBlocks().add(currentBasicBlock);
        regID = 0;
        currentSymTable = SymGenerator.table.get(node.getBlock());
        if (node.getFuncFParams() != null) {
            ArrayList<Pair<Value, FuncParam>> addrs = FuncFParamsVisitor(node.getFuncFParams());
            currentBasicBlock.setName(Integer.toString(regID));
            regID++;
            for (Pair<Value, FuncParam> src : addrs) {
                Value dst = new Value("%" + regID, src.getFirst().getType());
                regID++;
                src.getSecond().setAddr(dst);
                new AllocaInst(currentBasicBlock, dst);
                new StoreInst(currentBasicBlock, src.getFirst(), dst);
            }
        } else {
            currentBasicBlock.setName(Integer.toString(regID));
            regID++;
        }
        BlockVisitor(node.getBlock());
        currentSymTable = currentSymTable.getParent();
        Func func = (Func)currentSymTable.getSymbol(node.getIdent().getSrc(), true, node.getIdent().getLineNum());
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
        Type type;
        int size;
        if (!node.isPointer()) {
            type = new BaseType(BaseType.Tag.I32);
        } else if (node.isPointer()) {
            type = new PointerType(new BaseType(BaseType.Tag.I32));
        } else {
            size = funcParam.getSize().get(1);
            type = new PointerType(new ArrayType(size, new BaseType(BaseType.Tag.I32)));
        }
        new Argument(paramName, type, currentFunction);
        Value addr = new Value(paramName, type);
        funcParam.setAddr(addr);
        return new Pair<>(addr, funcParam);
    }

    public void BlockVisitor(Block node) {
        for (BlockItem value : node.getBlockItem()) {
            BlockItemVisitor(value);
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
                    // Value dst = ExpVisitor(node.getExps().get(j));
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
     */
    public Value LvalVisitor(Lval node, boolean isLeft) {
        Symbol symbol = currentSymTable.getSymbol(node.getIdent().getSrc(), true, node.getIdent().getLineNum());
        if (symbol instanceof Def) {
            Def def = (Def)symbol;
            Value src = def.getAddr(), tmp;
            for (Exp exp : node.getExp()) {
                ArrayList<Value> indexs = new ArrayList<>();
                indexs.add(new Value("0", new BaseType(BaseType.Tag.I32)));
                indexs.add(ExpVisitor(exp));
                tmp = new Value("%" + regID, ((ArrayType)src.getType()).getInnerType());
                regID++;
                new GEPInst(currentBasicBlock, src, tmp, indexs);
                src = tmp;
            }
            if (isLeft) {
                return src;
            } else {
                if (node.getExp().size() != def.getDimension()) {
                    ArrayList<Value> indexs = new ArrayList<>();
                    indexs.add(new Value("0", new BaseType(BaseType.Tag.I32)));
                    indexs.add(new Value("0", new BaseType(BaseType.Tag.I32)));
                    Type type = new PointerType(((ArrayType)src.getType()).getInnerType());
                    tmp = new Value("%" + regID, type);
                    regID++;
                    new GEPInst(currentBasicBlock, src, tmp, indexs);
                } else {
                    tmp = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    new LoadInst(currentBasicBlock, src, tmp);
                }
                return tmp;
            }
        } else {
            FuncParam funcParam = (FuncParam)symbol;
            Value src = funcParam.getAddr();
            int size = node.getExp().size();
            if (size == 0 && isLeft) {
                return src;
            }
            Value tmp = new Value("%" + regID, src.getType());
            regID++;
            new LoadInst(currentBasicBlock, src, tmp);
            if (size == 0) {
                return tmp;
            } else {
                Type type = ((PointerType)src.getType()).getInnerType();
                src = new Value(tmp.getName(), type);
                for (int i = 0; i < funcParam.getDimension(); i++) {
                    ArrayList<Value> indexs = new ArrayList<>();
                    if (i != 0) {
                        indexs.add(new Value("0", new BaseType(BaseType.Tag.I32)));
                    }
                    if (i >= size) {
                        indexs.add(new Value("0", new BaseType(BaseType.Tag.I32)));
                    } else {
                        indexs.add(ExpVisitor(node.getExp().get(i)));
                    }
                    tmp = new Value("%" + regID, type);
                    regID++;
                    new GEPInst(currentBasicBlock, src, tmp, indexs);
                    src = tmp;
                }
                if (isLeft) {
                    src = new Value(src.getName(), new BaseType(BaseType.Tag.I32));
                    return src;
                } else {
                    if (size != funcParam.getDimension()) {
                        tmp = new Value(tmp.getName(), new PointerType(((ArrayType)tmp.getType()).getInnerType()));
                    } else {
                        tmp = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                        regID++;
                        src = new Value(src.getName(), new BaseType(BaseType.Tag.I32));
                        new LoadInst(currentBasicBlock, src, tmp);
                    }
                    return tmp;
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
                cond = LAndExpVisitor(node.getlAndExp().get(i), trueBlock, nextBlock);
                if (i != size - 1) {
                    nextBlock = new BasicBlock(Integer.toString(regID), currentFunction);
                    regID++;
                    new BrInst(currentBasicBlock, cond, trueBlock, nextBlock, null);
                }
            }
            return cond;
        }
    }

    /* public String ConstExpVisitor(ConstExp node) { return null; } */
}
