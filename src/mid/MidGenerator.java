package mid;

import front.ASD.*;
import front.SynAnalyzer;
import mid.inst.*;
import mid.type.*;
import table.*;
import utils.Pair;

import java.util.ArrayList;

public class MidGenerator {
    private int regID;
    private int basicBlockID;
    private Function currentFunction;
    private BasicBlock currentBasicBlock;
    private SymTable currentSymTable;

    public MidGenerator() {
        this.regID = 0;
        this.basicBlockID = 1;
        this.currentFunction = null;
        this.currentBasicBlock = null;
        this.currentSymTable = SymGenerator.table.get(SynAnalyzer.root);
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
        Def def = (Def)currentSymTable.getSymbol(node.getIdent().getSrc(), false);
        String name;
        Type type;
        if (def.getDimension() == 0) {
            type = new BaseType(BaseType.Tag.I32);
        } else if (def.getDimension() == 1) {
            type = new ArrayType(def.getSize1(), new BaseType(BaseType.Tag.I32));
        } else {
            type = new ArrayType(def.getSize1(), new ArrayType(def.getSize2(), new BaseType(BaseType.Tag.I32)));
        }
        if (currentSymTable.getDepth() == 0) {
            name = "@" + node.getIdent().getSrc();
            new GlobalVariable(name, type, true,
                    def.getSize1(), def.getSize2(),
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
            int size1 = def.getSize1();
            ArrayList<Integer> initArrayVal = def.getInitArrayVal();
            for (int i = 0; i < size1; i++) {
                Value dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                regID++;
                ArrayList<Value> indexs = new ArrayList<>();
                indexs.add(new Value(Integer.toString(i), new BaseType(BaseType.Tag.I32)));
                new GEPInst(currentBasicBlock, value, dst, indexs);
                Value src = new Value(Integer.toString(initArrayVal.get(i)), new BaseType(BaseType.Tag.I32));
                new StoreInst(currentBasicBlock, src, dst);
            }
        } else {
            int size1 = def.getSize1();
            int size2 = def.getSize2();
            ArrayList<Integer> initArrayVal = def.getInitArrayVal();
            for (int i = 0; i < size1; i++) {
                for (int j = 0; j < size2; j++) {
                    Value dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    ArrayList<Value> indexs = new ArrayList<>();
                    indexs.add(new Value(Integer.toString(i), new BaseType(BaseType.Tag.I32)));
                    indexs.add(new Value(Integer.toString(j), new BaseType(BaseType.Tag.I32)));
                    new GEPInst(currentBasicBlock, value, dst, indexs);
                    Value src = new Value(Integer.toString(initArrayVal.get(i * size1 + j)), new BaseType(BaseType.Tag.I32));
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
        Def def = (Def)currentSymTable.getSymbol(node.getIdent().getSrc(), false);
        String name;
        Type type;
        if (def.getDimension() == 0) {
            type = new BaseType(BaseType.Tag.I32);
        } else if (def.getDimension() == 1) {
            type = new ArrayType(def.getSize1(), new BaseType(BaseType.Tag.I32));
        } else {
            type = new ArrayType(def.getSize1(), new ArrayType(def.getSize2(), new BaseType(BaseType.Tag.I32)));
        }
        if (currentSymTable.getDepth() == 0) {
            name = "@" + node.getIdent().getSrc();
            new GlobalVariable(name, type, false,
                    def.getSize1(), def.getSize2(),
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
            int size1 = def.getSize1();
            ArrayList<InitVal> initArrayVal = node.getInitVal();
            for (int i = 0; i < size1; i++) {
                Value dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                regID++;
                ArrayList<Value> indexs = new ArrayList<>();
                indexs.add(new Value(Integer.toString(i), new BaseType(BaseType.Tag.I32)));
                new GEPInst(currentBasicBlock, value, dst, indexs);
                Value src = ExpVisitor(initArrayVal.get(i).getExp());
                new StoreInst(currentBasicBlock, src, dst);
            }
        } else {
            int size1 = def.getSize1();
            int size2 = def.getSize2();
            ArrayList<InitVal> initArrayVal1 = node.getInitVal();
            for (int i = 0; i < size1; i++) {
                ArrayList<InitVal> initArrayVal2 = initArrayVal1.get(i).getInitVal();
                for (int j = 0; j < size2; j++) {
                    Value dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    ArrayList<Value> indexs = new ArrayList<>();
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
        Func func = (Func)currentSymTable.getSymbol(node.getIdent().getSrc(), true);
        int size = currentBasicBlock.getInsts().size();
        if ((size == 0 || !(currentBasicBlock.getInsts().get(size - 1) instanceof RetInst)) && func.isVoid()) {
            new RetInst(currentBasicBlock, new Value(null, new BaseType(BaseType.Tag.VOID)));
        }
    }

    public void MainFuncDefVisitor(MainFuncDef node) {
        currentFunction = new Function("@main", new BaseType(BaseType.Tag.I32));
        currentBasicBlock = new BasicBlock("0", currentFunction);
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
        FuncParam funcParam = (FuncParam)currentSymTable.getSymbol(node.getIdent().getSrc(), false);
        String paramName = "%" + regID;
        regID++;
        Type type;
        int size;
        if (node.getType() == 0) {
            type = new BaseType(BaseType.Tag.I32);
        } else if (node.getType() == 1) {
            type = new PointerType(new BaseType(BaseType.Tag.I32));
        } else {
            size = funcParam.getSize();
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

    public String StmtVisitor(Stmt node) {
        if (node.getType() == 0) {
            // currentBasicBlock = new BasicBlock(Integer.toString(basicBlockID), currentFunction);
            // basicBlockID++;
            currentSymTable = SymGenerator.table.get(node.getBlock());
            BlockVisitor(node.getBlock());
            currentSymTable = currentSymTable.getParent();
        } else if (node.getType() == 1) {

        } else if (node.getType() == 2) {

        } else if (node.getType() == 3) {

        } else if (node.getType() == 4) {

        } else if (node.getType() == 5) {
            Value ret;
            if (node.getExp() != null) {
                ret = ExpVisitor(node.getExp());
            } else {
                ret = new Value(null, new BaseType(BaseType.Tag.VOID));
            }
            new RetInst(currentBasicBlock, ret);
        } else if (node.getType() == 6) {
            String fmtStr = node.getFormatString().getSrc();  // fmtStr 中包括两个引号 "
            int len = fmtStr.length();
            int j = 0;
            for (int i = 1; i < len - 1; i++) {
                if (fmtStr.charAt(i) == '%') {
                    Value dst = ExpVisitor(node.getExps().get(j));
                    new PutInst(currentBasicBlock, dst, true);
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
        } else if (node.getType() == 7) {
            Value src = ExpVisitor(node.getExp());
            Value dst = LvalVisitor(node.getLval(), true);
            new StoreInst(currentBasicBlock, src, dst);
        } else if (node.getType() == 8) {
            Value src = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
            regID++;
            new GetInst(currentBasicBlock, src);
            Value dst = LvalVisitor(node.getLval(), true);
            new StoreInst(currentBasicBlock, src, dst);
        } else {
            if (node.getExp() != null) {
                ExpVisitor(node.getExp());
            }
        }
        // TODO
        return null;
    }

    public Value ExpVisitor(Exp node) {
        return AddExpVisitor(node.getAddExp());
    }

    public String CondVisitor(Cond node) {
        // TODO
        return null;
    }

    /**
     * isLeft = true 的情况
     *      Stmt -> Lval '=' Exp ';'
     *      Stmt -> Lval '=' 'getint' '(' ')' ';'
     * isLeft = false 的情况
     *      PrimaryExp -> Lval
     */
    /* public Value LvalVisitor(Lval node, boolean isLeft) {
        Symbol symbol = currentSymTable.getSymbol(node.getIdent().getSrc(), true);
        Value src;
        Value dst;
        int dimension;
        if (symbol instanceof Def) {
            src = ((Def)symbol).getAddr();
            dimension = ((Def)symbol).getDimension();
        } else {
            src = ((FuncParam)symbol).getAddr();
            dimension = ((FuncParam)symbol).getDimension();
        }
        if (dimension == 0) {
            if (isLeft) {
                return src;
            } else {
                dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                regID++;
                new LoadInst(currentBasicBlock, src, dst);
                return dst;
            }
        } else if (dimension == 1) {
            Value mid, tmp;
            if (node.getExp().size() == 0) {
                tmp = new Value("%" + regID, new PointerType(new BaseType(BaseType.Tag.I32)));
                regID++;
                new LoadInst(currentBasicBlock, src, tmp);
                mid = new Value("%" + regID, new PointerType(new BaseType(BaseType.Tag.I32)));
                regID++;
                new GEPInst(currentBasicBlock, mid, tmp, null);
                if (isLeft) {
                    return mid;
                } else {
                    dst = new Value("%" + regID, new PointerType(new BaseType(BaseType.Tag.I32)));
                    regID++;
                    new LoadInst(currentBasicBlock, mid, dst);
                    return dst;
                }
            } else {
                ArrayList<Value> indexs = new ArrayList<>();
                indexs.add(ExpVisitor(node.getExp().get(0)));
                mid = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                regID++;
                new GEPInst(currentBasicBlock, mid, src, indexs);
                if (isLeft) {
                    return mid;
                } else {
                    dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    new LoadInst(currentBasicBlock, mid, dst);
                    return dst;
                }
            }
        } else {
            Value mid;
            if (node.getExp().size() == 1) {
                ArrayList<Value> indexs = new ArrayList<>();
                indexs.add(ExpVisitor(node.getExp().get(0)));
                Type midType = (src.getType() instanceof PointerType) ?
                        ((PointerType)src.getType()).getInnerType() : ((ArrayType)src.getType()).getInnerType();
                mid = new Value("%" + regID, midType);
                regID++;
                new GEPInst(currentBasicBlock, mid, src, indexs);
                if (isLeft) {
                    return mid;
                } else {
                    dst = new Value("%" + regID, midType);
                    regID++;
                    new LoadInst(currentBasicBlock, mid, dst);
                    return dst;
                }
            } else {
                ArrayList<Value> indexs = new ArrayList<>();
                int size = node.getExp().size();
                for (int i = 0; i < size; i++) {
                    indexs.add(ExpVisitor(node.getExp().get(i)));
                }
                mid = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                regID++;
                new GEPInst(currentBasicBlock, mid, src, indexs);
                if (isLeft) {
                    return mid;
                } else {
                    dst = new Value("%" + regID, new BaseType(BaseType.Tag.I32));
                    regID++;
                    new LoadInst(currentBasicBlock, mid, dst);
                    return dst;
                }
            }
        }
    } */
    public Value LvalVisitor(Lval node, boolean isLeft) {
        Symbol symbol = currentSymTable.getSymbol(node.getIdent().getSrc(), true);
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
                    Type type;
                    if (def.getDimension() - node.getExp().size() == 2) {
                        type = src.getType();
                    } else {
                        type = ((ArrayType)src.getType()).getInnerType();
                    }
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
                    return src;
                } else {
                    if (size == funcParam.getDimension()) {
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
            Func func = (Func)currentSymTable.getSymbol(node.getIdent().getSrc(), true);
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
                // TODO
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

    public String RelExpVisitor(RelExp node) {
        // TODO
        return null;
    }

    public String EqExpVisitor(EqExp node) {
        // TODO
        return null;
    }

    public String LAndExpVisitor(LAndExp node) {
        // TODO
        return null;
    }

    public String LOrExpVisitor(LOrExp node) {
        // TODO
        return null;
    }

    /* public String ConstExpVisitor(ConstExp node) { return null; } */
}
