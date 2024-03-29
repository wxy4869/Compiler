package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class Stmt implements Node{
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
    private Lval lval;
    private Exp exp;
    private ArrayList<Exp> exps;
    private Block block;
    private Cond cond;
    private Stmt stmt1;
    private Stmt stmt2;
    private Token breakTK;
    private Token continueTK;
    private Token returnTK;
    private Token printf;
    private Token formatString;
    private int type;

    public Stmt(Lval lval, Exp exp, ArrayList<Exp> exps, Block block, Cond cond, Stmt stmt1, Stmt stmt2, Token breakTK, Token continueTK, Token returnTK, Token printf, Token formatString, int type) {
        this.lval = lval;
        this.exp = exp;
        this.exps = exps;
        this.block = block;
        this.cond = cond;
        this.stmt1 = stmt1;
        this.stmt2 = stmt2;
        this.breakTK = breakTK;
        this.continueTK = continueTK;
        this.returnTK = returnTK;
        this.printf = printf;
        this.formatString = formatString;
        this.type = type;
    }

    @Override
    public void printMoi() {
        if (type == 0) {
            block.printMoi();
        } else if (type == 1) {
            IOUtils.write("IFTK if\n", "output.txt", true);
            IOUtils.write("LPARENT (\n", "output.txt", true);
            cond.printMoi();
            IOUtils.write("RPARENT )\n", "output.txt", true);
            stmt1.printMoi();
            if (stmt2 != null) {
                IOUtils.write("ELSETK else\n", "output.txt", true);
                stmt2.printMoi();
            }
        } else if (type == 2) {
            IOUtils.write("WHILETK while\n", "output.txt", true);
            IOUtils.write("LPARENT (\n", "output.txt", true);
            cond.printMoi();
            IOUtils.write("RPARENT )\n", "output.txt", true);
            stmt1.printMoi();
        } else if (type == 3) {
            IOUtils.write("BREAKTK break\n", "output.txt", true);
            IOUtils.write("SEMICN ;\n", "output.txt", true);
        } else if (type == 4) {
            IOUtils.write("CONTINUETK continue\n", "output.txt", true);
            IOUtils.write("SEMICN ;\n", "output.txt", true);
        } else if (type == 5) {
            IOUtils.write("RETURNTK return\n", "output.txt", true);
            if (exp != null) {
                exp.printMoi();
            }
            IOUtils.write("SEMICN ;\n", "output.txt", true);
        } else if (type == 6) {
            IOUtils.write("PRINTFTK printf\n", "output.txt", true);
            IOUtils.write("LPARENT (\n", "output.txt", true);
            IOUtils.write(formatString.toString(), "output.txt", true);
            for (Exp value : exps) {
                IOUtils.write("COMMA ,\n", "output.txt", true);
                value.printMoi();
            }
            IOUtils.write("RPARENT )\n", "output.txt", true);
            IOUtils.write("SEMICN ;\n", "output.txt", true);
        } else if (type == 7) {
            lval.printMoi();
            IOUtils.write("ASSIGN =\n", "output.txt", true);
            exp.printMoi();
            IOUtils.write("SEMICN ;\n", "output.txt", true);
        } else if (type == 8) {
            lval.printMoi();
            IOUtils.write("ASSIGN =\n", "output.txt", true);
            IOUtils.write("GETINTTK getint\n", "output.txt", true);
            IOUtils.write("LPARENT (\n", "output.txt", true);
            IOUtils.write("RPARENT )\n", "output.txt", true);
            IOUtils.write("SEMICN ;\n", "output.txt", true);
        } else if (type == 9) {
            if (exp != null) {
                exp.printMoi();
            }
            IOUtils.write("SEMICN ;\n", "output.txt", true);
        }
        IOUtils.write("<Stmt>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>();
        if (type == 0) {
            child.add(block);
        } else if (type == 1 || type == 2) {
            child.add(cond);
            child.add(stmt1);
            if (stmt2 != null) {
                child.add(stmt2);
            }
        } else if (type == 5 && exp != null) {
            child.add(exp);
        } else if (type == 6) {
            child.addAll(exps);
        } else if (type == 7) {
            child.add(lval);
            child.add(exp);
        } else if (type == 8) {
            child.add(lval);
        } else if (type == 9 && exp != null) {
            child.add(exp);
        }
        return child;
    }

    public Lval getLval() {
        return lval;
    }

    public Exp getExp() {
        return exp;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    public Block getBlock() {
        return block;
    }

    public Cond getCond() {
        return cond;
    }

    public Stmt getStmt1() {
        return stmt1;
    }

    public Stmt getStmt2() {
        return stmt2;
    }

    public Token getBreakTK() {
        return breakTK;
    }

    public Token getContinueTK() {
        return continueTK;
    }

    public Token getReturnTK() {
        return returnTK;
    }

    public Token getPrintf() {
        return printf;
    }

    public Token getFormatString() {
        return formatString;
    }

    public int getType() {
        return type;
    }
}