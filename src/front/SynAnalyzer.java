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

    public CompUnit CompUnit() {  // CompUnit -> {Decl} {FuncDef} MainFuncDef
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

    public Decl Decl() {  // Decl -> ConstDecl | VarDecl
        ConstDecl constDecl = null;
        VarDecl varDecl = null;
        if (getSymType(0).equals("CONSTTK")) {
            constDecl = ConstDecl();
        } else {
            varDecl = VarDecl();
        }
        return new Decl(constDecl, varDecl);
    }

    public ConstDecl ConstDecl() {  // ConstDecl -> 'const' BType ConstDef { ',' ConstDef } ';'
        ArrayList<ConstDef> constDef = new ArrayList<>();
        nextSym();
        nextSym();
        constDef.add(ConstDef());
        while (getSymType(0).equals("COMMA")) {
            nextSym();
            constDef.add(ConstDef());
        }
        if (!sym(0).getDst().equals("SEMICN")) {  // 错误处理
            ErrHandler.errors.add(new Error("i",sym(-1).getLineNum()));
        } else {
            nextSym();
        }
        return new ConstDecl(constDef);
    }

    public ConstDef ConstDef() {  // ConstDef -> Ident { '[' ConstExp ']' } '=' ConstInitVal
        Token ident;
        ArrayList<ConstExp> constExp = new ArrayList<>();
        ConstInitVal constInitVal;
        ident = sym(0);
        nextSym();
        while (getSymType(0).equals("LBRACK")) {
            nextSym();
            constExp.add(ConstExp());
            if (!sym(0).getDst().equals("RBRACK")) {  // 错误处理
                ErrHandler.errors.add(new Error("k",sym(-1).getLineNum()));
            } else {
                nextSym();
            }
        }
        nextSym();
        constInitVal = ConstInitVal();
        return new ConstDef(ident, constExp, constInitVal);
    }

    public ConstInitVal ConstInitVal() {  // ConstInitVal -> ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
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

    public VarDecl VarDecl() {  // VarDecl -> BType VarDef { ',' VarDef } ';'
        ArrayList<VarDef> varDef = new ArrayList<>();
        nextSym();
        varDef.add(VarDef());
        while (getSymType(0).equals("COMMA")) {
            nextSym();
            varDef.add(VarDef());
        }
        if (!sym(0).getDst().equals("SEMICN")) {  // 错误处理
            ErrHandler.errors.add(new Error("i",sym(-1).getLineNum()));
        } else {
            nextSym();
        }
        return new VarDecl(varDef);
    }

    public VarDef VarDef() {  // VarDef -> Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
        Token ident;
        ArrayList<ConstExp> constExp = new ArrayList<>();
        InitVal initVal = null;
        ident = sym(0);
        nextSym();
        while (getSymType(0).equals("LBRACK")) {
            nextSym();
            constExp.add(ConstExp());
            if (!sym(0).getDst().equals("RBRACK")) {  // 错误处理
                ErrHandler.errors.add(new Error("k",sym(-1).getLineNum()));
            } else {
                nextSym();
            }
        }
        if (getSymType(0).equals("ASSIGN")) {
            nextSym();
            initVal = InitVal();
        }
        return new VarDef(ident, constExp, initVal);
    }

    public InitVal InitVal() {  // InitVal -> Exp | '{' [ InitVal { ',' InitVal } ] '}
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

    public FuncDef FuncDef() {  // FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block
        FuncType funcType;
        Token ident;
        FuncFParams funcFParams = null;
        Block block;
        funcType = FuncType();
        ident = sym(0);
        nextSym();
        nextSym();
        if (!getSymType(0).equals("RPARENT")) {
            if (getSymType(0).equals("INTTK")) {
                funcFParams = FuncFParams();
            }
        }
        if (!sym(0).getDst().equals("RPARENT")) {  // 错误处理
            ErrHandler.errors.add(new Error("j",sym(-1).getLineNum()));
        } else {
            nextSym();
        }
        block = Block();
        return new FuncDef(funcType, ident, funcFParams, block);
    }

    public MainFuncDef MainFuncDef() {  // MainFuncDef -> 'int' 'main' '(' ')' Block
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

    public FuncType FuncType() {  // FuncType -> 'void' | 'int'
        Token funcType;
        funcType = sym(0);
        nextSym();
        return new FuncType(funcType);
    }

    public FuncFParams FuncFParams() {  // FuncFParams -> FuncFParams { ',' FuncFParam }
        ArrayList<FuncFParam> funcFParam = new ArrayList<>();
        funcFParam.add(FuncFParam());
        while (getSymType(0).equals("COMMA")) {
            nextSym();
            funcFParam.add(FuncFParam());
        }
        return new FuncFParams(funcFParam);
    }

    public FuncFParam FuncFParam() {  // FuncFParam -> BType Ident ['[' ']' { '[' ConstExp ']' }]
        Token ident;
        ArrayList<ConstExp> constExp = new ArrayList<>();
        boolean isPointer = false;
        nextSym();
        ident = sym(0);
        nextSym();
        if (getSymType(0).equals("LBRACK")) {
            isPointer = true;
            nextSym();
            if (!sym(0).getDst().equals("RBRACK")) {  // 错误处理
                ErrHandler.errors.add(new Error("k",sym(-1).getLineNum()));
            } else {
                nextSym();
            }
            while (getSymType(0).equals("LBRACK")) {
                nextSym();
                constExp.add(ConstExp());
                if (!sym(0).getDst().equals("RBRACK")) {  // 错误处理
                    ErrHandler.errors.add(new Error("k",sym(-1).getLineNum()));
                } else {
                    nextSym();
                }
            }
        }
        return new FuncFParam(ident, constExp, isPointer);
    }

    public Block Block() {  // Block -> '{' { BlockItem } '}'
        ArrayList<BlockItem> blockItem = new ArrayList<>();
        Token rbrace;
        nextSym();
        while (!getSymType(0).equals("RBRACE")) {
            blockItem.add(BlockItem());
        }
        rbrace = sym(0);
        nextSym();
        return new Block(blockItem, rbrace);
    }

    public BlockItem BlockItem() {  // BlockItem -> Decl | Stmt
        Decl decl = null;
        Stmt stmt = null;
        if (getSymType(0).equals("CONSTTK") || getSymType(0).equals("INTTK")) {
            decl = Decl();
        } else {
            stmt = Stmt();
        }
        return new BlockItem(decl, stmt);
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
    public Stmt Stmt() {
        Lval lval = null;
        Exp exp = null;
        ArrayList<Exp> exps = new ArrayList<>();
        Block block = null;
        Cond cond = null;
        Stmt stmt1 = null;
        Stmt stmt2 = null;
        Token breakTK = null;
        Token continueTK = null;
        Token returnTK = null;
        Token printf = null;
        Token formatString = null;
        int type = -1;
        if (getSymType(0).equals("LBRACE")) { // Stmt -> Block
            block = Block();
            type = 0;
        } else if (getSymType(0).equals("IFTK")) { // Stmt -> 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            nextSym();
            nextSym();
            cond = Cond();
            if (!sym(0).getDst().equals("RPARENT")) {  // 错误处理
                ErrHandler.errors.add(new Error("j",sym(-1).getLineNum()));
            } else {
                nextSym();
            }
            stmt1 = Stmt();
            if (getSymType(0).equals("ELSETK")) {
                nextSym();
                stmt2 = Stmt();
            }
            type = 1;
        } else if (getSymType(0).equals("WHILETK")) { // Stmt -> 'while' '(' Cond ')' Stmt
            nextSym();
            nextSym();
            cond = Cond();
            if (!sym(0).getDst().equals("RPARENT")) {  // 错误处理
                ErrHandler.errors.add(new Error("j",sym(-1).getLineNum()));
            } else {
                nextSym();
            }
            stmt1 = Stmt();
            type = 2;
        } else if (getSymType(0).equals("BREAKTK")) { // Stmt -> 'break' ';'
            breakTK = sym(0);
            nextSym();
            if (!sym(0).getDst().equals("SEMICN")) {  // 错误处理
                ErrHandler.errors.add(new Error("i",sym(-1).getLineNum()));
            } else {
                nextSym();
            }
            type = 3;
        } else if (getSymType(0).equals("CONTINUETK")) { // Stmt -> 'continue' ';'
            continueTK = sym(0);
            nextSym();
            if (!sym(0).getDst().equals("SEMICN")) {  // 错误处理
                ErrHandler.errors.add(new Error("i",sym(-1).getLineNum()));
            } else {
                nextSym();
            }
            type = 4;
        } else if (getSymType(0).equals("RETURNTK")) { // Stmt -> 'return' [Exp] ';'
            returnTK = sym(0);
            nextSym();
            int tmp = index;
            if (!getSymType(0).equals("SEMICN")) {
                try {
                    exp = Exp();
                    if (!getSymType(0).equals("SEMICN")) {
                        tmp = index;
                        exp = null;
                    }
                } catch (Exception e) {
                    index = tmp;
                }
            }
            if (!sym(0).getDst().equals("SEMICN")) {  // 错误处理
                ErrHandler.errors.add(new Error("i",sym(-1).getLineNum()));
            } else {
                nextSym();
            }
            type = 5;
        } else if (getSymType(0).equals("PRINTFTK")) { // Stmt -> 'printf''('FormatString{','Exp}')'';'
            printf = sym(0);
            nextSym();
            nextSym();
            formatString = sym(0);
            nextSym();
            while (getSymType(0).equals("COMMA")) {
                nextSym();
                exps.add(Exp());
            }
            if (!sym(0).getDst().equals("RPARENT")) {  // 错误处理
                ErrHandler.errors.add(new Error("j",sym(-1).getLineNum()));
            } else {
                nextSym();
            }
            if (!sym(0).getDst().equals("SEMICN")) {  // 错误处理
                ErrHandler.errors.add(new Error("i",sym(-1).getLineNum()));
            } else {
                nextSym();
            }
            type = 6;
        } else if (getSymType(0).equals("LPARENT") || getSymType(0).equals("INTCON")) { // Stmt -> [Exp] ';' 的第一种情况
            int tmp = index;
            if (!getSymType(0).equals("SEMICN")) {
                try {
                    exp = Exp();
                    if (!getSymType(0).equals("SEMICN")) {
                        tmp = index;
                        exp = null;
                    }
                } catch (Exception e) {
                    index = tmp;
                }
            }
            if (!sym(0).getDst().equals("SEMICN")) {  // 错误处理
                ErrHandler.errors.add(new Error("i",sym(-1).getLineNum()));
            } else {
                nextSym();
            }
            type = 9;
        }
        if (type == -1) {
            int tmp = index;
            Lval lvaltmp = Lval();
            if (getSymType(0).equals("ASSIGN")) {
                lval = lvaltmp;
                nextSym();
                if (!getSymType(0).equals("GETINTTK")) { // Stmt -> LVal '=' Exp ';'
                    exp = Exp();
                    if (!sym(0).getDst().equals("SEMICN")) {  // 错误处理
                        ErrHandler.errors.add(new Error("i",sym(-1).getLineNum()));
                    } else {
                        nextSym();
                    }
                    type = 7;
                } else { // Stmt -> LVal '=' 'getint''('')'';'
                    nextSym();
                    nextSym();
                    if (!sym(0).getDst().equals("RPARENT")) {  // 错误处理
                        ErrHandler.errors.add(new Error("j",sym(-1).getLineNum()));
                    } else {
                        nextSym();
                    }
                    if (!sym(0).getDst().equals("SEMICN")) {  // 错误处理
                        ErrHandler.errors.add(new Error("i",sym(-1).getLineNum()));
                    } else {
                        nextSym();
                    }
                    type = 8;
                }
            } else { // Stmt -> [Exp] ';' 的第二种情况
                index = tmp;
                if (!getSymType(0).equals("SEMICN")) {
                    exp = Exp();
                }
                nextSym();
                type = 9;
            }
        }
        return new Stmt(lval, exp, exps, block, cond, stmt1, stmt2, breakTK, continueTK, returnTK, printf, formatString, type);
    }

    public Exp Exp() {  // Exp -> AddExp
        AddExp addExp;
        addExp = AddExp();
        return new Exp(addExp);
    }

    public Cond Cond() {  // Cond -> LOrExp
        LOrExp lOrExp;
        lOrExp = LOrExp();
        return new Cond(lOrExp);
    }

    public Lval Lval() {  // Lval -> Ident {'[' Exp ']'}
        Token ident;
        ArrayList<Exp> exp = new ArrayList<>();
        ident = sym(0);
        nextSym();
        while (getSymType(0).equals("LBRACK")) {
            nextSym();
            exp.add(Exp());
            if (!sym(0).getDst().equals("RBRACK")) {  // 错误处理
                ErrHandler.errors.add(new Error("k",sym(-1).getLineNum()));
            } else {
                nextSym();
            }
        }
        return new Lval(ident, exp);
    }

    public PrimaryExp PrimaryExp() {  // PrimaryExp -> '(' Exp ')' | Lval | Number
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

    public Number Number() {  // Number -> IntConst
        Token number;
        number = sym(0);
        nextSym();
        return new Number(number);
    }

    public UnaryExp UnaryExp() {  // UnaryExp -> PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
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
            int tmp = index;
            if (!getSymType(0).equals("RPARENT")) {
                try {
                    funcRParams = FuncRParams();
                    if (!getSymType(0).equals("RPARENT")) {
                        tmp = index;
                        funcRParams = null;
                    }
                } catch (Exception e) {
                    index = tmp;
                }
            }
            if (!sym(0).getDst().equals("RPARENT")) {  // 错误处理
                ErrHandler.errors.add(new Error("j",sym(-1).getLineNum()));
            } else {
                nextSym();
            }
        } else {
            primaryExp = PrimaryExp();
        }
        return new UnaryExp(primaryExp, ident, funcRParams, unaryOp, unaryExp);
    }

    public UnaryOp UnaryOp() {  // UnaryOp -> '+' | '−' | '!'
        Token op;
        op = sym(0);
        nextSym();
        return new UnaryOp(op);
    }

    public FuncRParams FuncRParams() {  // FuncRParams -> Exp { ',' Exp }
        ArrayList<Exp> exp = new ArrayList<>();
        exp.add(Exp());
        while (getSymType(0).equals("COMMA")) {
            nextSym();
            exp.add(Exp());
        }
        return new FuncRParams(exp);
    }

    public MulExp MulExp() {  // MulExp -> UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
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

    public AddExp AddExp() {  // AddExp -> MulExp | AddExp ('+' | '−') MulExp
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

    public RelExp RelExp() {  // RelExp -> AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
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

    public EqExp EqExp() {  // EqExp -> RelExp | EqExp ('==' | '!=') RelExp
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

    public LAndExp LAndExp() {  // LAndExp -> EqExp | LAndExp '&&' EqExp
        ArrayList<EqExp> eqExp = new ArrayList<>();
        eqExp.add(EqExp());
        while (getSymType(0).equals("AND")) {
            nextSym();
            eqExp.add(EqExp());
        }
        return new LAndExp(eqExp);
    }

    public LOrExp LOrExp() {  // LOrExp -> LAndExp | LOrExp '||' LAndExp
        ArrayList<LAndExp> lAndExp = new ArrayList<>();
        lAndExp.add(LAndExp());
        while (getSymType(0).equals("OR")) {
            nextSym();
            lAndExp.add(LAndExp());
        }
        return new LOrExp(lAndExp);
    }

    public ConstExp ConstExp() {  // ConstExp -> AddExp
        AddExp addExp;
        addExp = AddExp();
        return new ConstExp(addExp);
    }
}
