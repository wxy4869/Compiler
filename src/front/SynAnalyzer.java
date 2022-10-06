package front;

import front.ASD.*;
import front.ASD.Number;

import java.util.ArrayList;

public class SynAnalyzer {
    public static Node root = null;
    private int index = 0;
    public static int tokenSize = LexAnalyzer.tokens.size();

    private Token sym(int offset) {
        return LexAnalyzer.tokens.get(index + offset);
    }

    private String getSymType(int offset) {
        if (index + offset < tokenSize) {
            return LexAnalyzer.tokens.get(index + offset).getDst();
        } else {
            return String.valueOf(false);
        }
    }

    private void nextSym() {
        index++;
    }

    public void analyze() {
        root = CompUnit();
    }

    public CompUnit CompUnit() {
        ArrayList<Decl> decl = new ArrayList<>();
        ArrayList<FuncDef> funcDef = new ArrayList<>();
        MainFuncDef mainFuncDef;
        while (!getSymType(1).equals("MAINTK") && !getSymType(2).equals("LPARENT")) {
            decl.add(Decl());
        }
        while (!getSymType(1).equals("MAINTK")) {
            funcDef.add(FuncDef());
        }
        mainFuncDef = MainFuncDef();
        return new CompUnit(decl, funcDef, mainFuncDef);
    }

    public Decl Decl() {
        ConstDecl constDecl = null;
        VarDecl varDecl = null;
        if (getSymType(0).equals("CONSTTK")) {
            constDecl = ConstDecl();
        } else {
            varDecl = VarDecl();
        }
        return new Decl(constDecl, varDecl);
    }

    public ConstDecl ConstDecl() {
        ArrayList<ConstDef> constDef = new ArrayList<>();
        nextSym();
        nextSym();
        constDef.add(ConstDef());
        while (!getSymType(0).equals("SEMICN")) {
            nextSym();
            constDef.add(ConstDef());
        }
        nextSym();
        return new ConstDecl(constDef);
    }

    public ConstDef ConstDef() {
        Token ident;
        ArrayList<ConstExp> constExp = new ArrayList<>();
        ConstInitVal constInitVal;
        ident = sym(0);
        nextSym();
        while (getSymType(0).equals("LBRACK")) {
            nextSym();
            constExp.add(ConstExp());
            nextSym();
        }
        nextSym();
        constInitVal = ConstInitVal();
        return new ConstDef(ident, constExp, constInitVal);
    }

    public ConstInitVal ConstInitVal() {
        ConstExp constExp = null;
        ArrayList<ConstInitVal> constInitVal = new ArrayList<>();
        if (!getSymType(0).equals("LBRACE")) {
            constExp = ConstExp();
        } else {
            nextSym();
            if (!getSymType(0).equals("RBRACE")) {
                constInitVal.add(ConstInitVal());
                while (getSymType(0).equals("COMMA")) {
                    nextSym();
                    constInitVal.add(ConstInitVal());
                }
            }
            nextSym();
        }
        return new ConstInitVal(constExp, constInitVal);
    }

    public VarDecl VarDecl() {
        ArrayList<VarDef> varDef = new ArrayList<>();
        nextSym();
        varDef.add(VarDef());
        while (!getSymType(0).equals("SEMICN")) {
            nextSym();
            varDef.add(VarDef());
        }
        nextSym();
        return new VarDecl(varDef);
    }

    public VarDef VarDef() {
        Token ident;
        ArrayList<ConstExp> constExp = new ArrayList<>();
        InitVal initVal = null;
        ident = sym(0);
        nextSym();
        while (getSymType(0).equals("LBRACK")) {
            nextSym();
            constExp.add(ConstExp());
            nextSym();
        }
        if (getSymType(0).equals("ASSIGN")) {
            nextSym();
            initVal = InitVal();
        }
        return new VarDef(ident, constExp, initVal);
    }

    public InitVal InitVal() {
        Exp exp = null;
        ArrayList<InitVal> initVal = new ArrayList<>();
        if (!getSymType(0).equals("LBRACE")) {
            exp = Exp();
        } else {
            nextSym();
            if (!getSymType(0).equals("RBRACE")) {
                initVal.add(InitVal());
                while (getSymType(0).equals("COMMA")) {
                    nextSym();
                    initVal.add(InitVal());
                }
            }
            nextSym();
        }
        return new InitVal(exp, initVal);
    }

    public FuncDef FuncDef() {
        FuncType funcType;
        Token ident;
        FuncFParams funcFParams = null;
        Block block;
        funcType = FuncType();
        ident = sym(0);
        nextSym();
        nextSym();
        if (!getSymType(0).equals("RPARENT")) {
            funcFParams = FuncFParams();
        }
        nextSym();
        block = Block();
        return new FuncDef(funcType, ident, funcFParams, block);
    }

    public MainFuncDef MainFuncDef() {
        Token main;
        Block block;
        nextSym();
        main = sym(0);
        nextSym();
        nextSym();
        nextSym();
        block = Block();
        return new MainFuncDef(main, block);
    }

    public FuncType FuncType() {
        Token funcType;
        funcType = sym(0);
        nextSym();
        return new FuncType(funcType);
    }

    public FuncFParams FuncFParams() {
        ArrayList<FuncFParam> funcFParam = new ArrayList<>();
        funcFParam.add(FuncFParam());
        while (getSymType(0).equals("COMMA")) {
            nextSym();
            funcFParam.add(FuncFParam());
        }
        return new FuncFParams(funcFParam);
    }

    public FuncFParam FuncFParam() {
        Token ident;
        ConstExp constExp = null;
        int type = 0;
        nextSym();
        ident = sym(0);
        nextSym();
        if (getSymType(0).equals("LBRACK")) {
            type = 1;
            nextSym();
            nextSym();
            if (getSymType(0).equals("LBRACK")) {
                type = 2;
                nextSym();
                constExp = ConstExp();
                nextSym();
            }
        }
        return new FuncFParam(ident, constExp, type);
    }

    public Block Block() {
        ArrayList<BlockItem> blockItem = new ArrayList<>();
        nextSym();
        while (!getSymType(0).equals("RBRACE")) {
            blockItem.add(BlockItem());
        }
        nextSym();
        return new Block(blockItem);
    }

    public BlockItem BlockItem() {
        Decl decl = null;
        Stmt stmt = null;
        if (getSymType(0).equals("CONSTTK") || getSymType(0).equals("INTTK")) {
            decl = Decl();
        } else {
            stmt = Stmt();
        }
        return new BlockItem(decl, stmt);
    }

    public Stmt Stmt() {
        Lval lval = null;
        Exp exp = null;
        ArrayList<Exp> exps = new ArrayList<>();
        Block block = null;
        Cond cond = null;
        Stmt stmt1 = null;
        Stmt stmt2 = null;
        Token printf = null;
        Token formatString = null;
        int type = -1;
        if (getSymType(0).equals("LBRACE")) { // Block
            block = Block();
            type = 0;
        } else if (getSymType(0).equals("IFTK")) { // 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            nextSym();
            nextSym();
            cond = Cond();
            nextSym();
            stmt1 = Stmt();
            if (getSymType(0).equals("ELSETK")) {
                nextSym();
                stmt2 = Stmt();
            }
            type = 1;
        } else if (getSymType(0).equals("WHILETK")) { // 'while' '(' Cond ')' Stmt
            nextSym();
            nextSym();
            cond = Cond();
            nextSym();
            stmt1 = Stmt();
            type = 2;
        } else if (getSymType(0).equals("BREAKTK")) { // 'break' ';'
            nextSym();
            nextSym();
            type = 3;
        } else if (getSymType(0).equals("CONTINUETK")) { // 'continue' ';'
            nextSym();
            nextSym();
            type = 4;
        } else if (getSymType(0).equals("RETURNTK")) { // 'return' [Exp] ';'
            nextSym();
            if (!getSymType(0).equals("SEMICN")) {
                exp = Exp();
            }
            nextSym();
            type = 5;
        } else if (getSymType(0).equals("PRINTFTK")) { // 'printf''('FormatString{','Exp}')'';'
            printf = sym(0);
            nextSym();
            nextSym();
            formatString = sym(0);
            nextSym();
            while (!getSymType(0).equals("RPARENT")) {
                nextSym();
                exps.add(Exp());
            }
            nextSym();
            nextSym();
            type = 6;
        } else if (getSymType(0).equals("LPARENT") || getSymType(0).equals("INTCON")) { // [Exp] ';' 的第一种情况
            if (!getSymType(0).equals("SEMICN")) {
                exp = Exp();
            }
            nextSym();
            type = 9;
        }
        if (type == -1) {
            int tmp = index;
            Lval lvaltmp = Lval();
            if (getSymType(0).equals("ASSIGN")) {
                lval = lvaltmp;
                nextSym();
                if (!getSymType(0).equals("GETINTTK")) { // LVal '=' Exp ';'
                    exp = Exp();
                    nextSym();
                    type = 7;
                } else { // LVal '=' 'getint''('')'';'
                    nextSym();
                    nextSym();
                    nextSym();
                    nextSym();
                    type = 8;
                }
            } else { // [Exp] ';' 的第二种情况
                index = tmp;
                if (!getSymType(0).equals("SEMICN")) {
                    exp = Exp();
                }
                nextSym();
                type = 9;
            }
        }
        /*else if (getSymType(0).equals("IDENFR") && (
                getSymType(1).equals("ASSIGN") ||
                (getSymType(1).equals("LBRACK") && getSymType(4).equals("ASSIGN")) ||
                (getSymType(1).equals("LBRACK") && getSymType(4).equals("LBRACK") && getSymType(7).equals("ASSIGN")))) {
            // a =          1
            // a[1] =       4
            // a[1][1] =    7
            lval = Lval();
            nextSym();
            if (!getSymType(0).equals("GETINTTK")) { // LVal '=' Exp ';'
                exp = Exp();
                nextSym();
                type = 7;
            } else { // LVal '=' 'getint''('')'';'
                nextSym();
                nextSym();
                nextSym();
                nextSym();
                type = 8;
            }
        } else { // [Exp] ';'
            if (!getSymType(0).equals("SEMICN")) {
                exp = Exp();
            }
            nextSym();
            type = 9;
        }*/
        return new Stmt(lval, exp, exps, block, cond, stmt1, stmt2, printf, formatString, type);
    }

    public Exp Exp() {
        AddExp addExp;
        addExp = AddExp();
        return new Exp(addExp);
    }

    public Cond Cond() {
        LOrExp lOrExp;
        lOrExp = LOrExp();
        return new Cond(lOrExp);
    }

    public Lval Lval() {
        Token ident;
        ArrayList<Exp> exp = new ArrayList<>();
        ident = sym(0);
        nextSym();
        while (getSymType(0).equals("LBRACK")) {
            nextSym();
            exp.add(Exp());
            nextSym();
        }
        return new Lval(ident, exp);
    }

    public PrimaryExp PrimaryExp() {
        Exp exp = null;
        Lval lval = null;
        Number number = null;
        if (getSymType(0).equals("LPARENT")) {
            nextSym();
            exp = Exp();
            nextSym();
        } else if (getSymType(0).equals("IDENFR")) {
            lval = Lval();
        } else if (getSymType(0).equals("INTCON")){
            number = Number();
        }
        return new PrimaryExp(exp, lval, number);
    }

    public Number Number() {
        Token number;
        number = sym(0);
        nextSym();
        return new Number(number);
    }

    public UnaryExp UnaryExp() {
        PrimaryExp primaryExp = null;
        Token ident = null;
        FuncRParams funcRParams = null;
        UnaryOp unaryOp = null;
        UnaryExp unaryExp = null;
        if (getSymType(0).equals("PLUS") || getSymType(0).equals("MINU") || getSymType(0).equals("NOT")) {
            unaryOp = UnaryOp();
            unaryExp = UnaryExp();
        } else if (getSymType(0).equals("IDENFR") && getSymType(1).equals("LPARENT")){
            ident = sym(0);
            nextSym();
            nextSym();
            if (!getSymType(0).equals("RPARENT")) {
                funcRParams = FuncRParams();
            }
            nextSym();
        } else {
            primaryExp = PrimaryExp();
        }
        return new UnaryExp(primaryExp, ident, funcRParams, unaryOp, unaryExp);
    }

    public UnaryOp UnaryOp() {
        Token op;
        op = sym(0);
        nextSym();
        return new UnaryOp(op);
    }

    public FuncRParams FuncRParams() {
        ArrayList<Exp> exp = new ArrayList<>();
        exp.add(Exp());
        while (getSymType(0).equals("COMMA")) {
            nextSym();
            exp.add(Exp());
        }
        return new FuncRParams(exp);
    }

    public MulExp MulExp() {
        ArrayList<UnaryExp> unaryExp = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        unaryExp.add(UnaryExp());
        while (getSymType(0).equals("MULT") || getSymType(0).equals("DIV") || getSymType(0).equals("MOD")) {
            ops.add(sym(0));
            nextSym();
            unaryExp.add(UnaryExp());
        }
        return new MulExp(unaryExp, ops);
    }

    public AddExp AddExp() {
        ArrayList<MulExp> mulExp = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        mulExp.add(MulExp());
        while (getSymType(0).equals("PLUS") || getSymType(0).equals("MINU")) {
            ops.add(sym(0));
            nextSym();
            mulExp.add(MulExp());
        }
        return new AddExp(mulExp, ops);
    }

    public RelExp RelExp() {
        ArrayList<AddExp> addExp = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        addExp.add(AddExp());
        while (getSymType(0).equals("LSS") || getSymType(0).equals("LEQ") || getSymType(0).equals("GRE") || getSymType(0).equals("GEQ")) {
            ops.add(sym(0));
            nextSym();
            addExp.add(AddExp());
        }
        return new RelExp(addExp, ops);
    }

    public EqExp EqExp() {
        ArrayList<RelExp> relExp = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        relExp.add(RelExp());
        while (getSymType(0).equals("EQL") || getSymType(0).equals("NEQ")) {
            ops.add(sym(0));
            nextSym();
            relExp.add(RelExp());
        }
        return new EqExp(relExp, ops);
    }

    public LAndExp LAndExp() {
        ArrayList<EqExp> eqExp = new ArrayList<>();
        eqExp.add(EqExp());
        while (getSymType(0).equals("AND")) {
            nextSym();
            eqExp.add(EqExp());
        }
        return new LAndExp(eqExp);
    }

    public LOrExp LOrExp() {
        ArrayList<LAndExp> lAndExp = new ArrayList<>();
        lAndExp.add(LAndExp());
        while (getSymType(0).equals("OR")) {
            nextSym();
            lAndExp.add(LAndExp());
        }
        return new LOrExp(lAndExp);
    }

    public ConstExp ConstExp() {
        AddExp addExp;
        addExp = AddExp();
        return new ConstExp(addExp);
    }
}