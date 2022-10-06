package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class Stmt implements Node{
    private Lval lval;
    private Exp exp;
    private ArrayList<Exp> exps;
    private Block block;
    private Cond cond;
    private Stmt stmt1;
    private Stmt stmt2;
    private Token printf;
    private Token formatString;
    private int type;

    public Stmt(Lval lval, Exp exp, ArrayList<Exp> exps, Block block, Cond cond, Stmt stmt1, Stmt stmt2, Token printf, Token formatString, int type) {
        this.lval = lval;
        this.exp = exp;
        this.exps = exps;
        this.block = block;
        this.cond = cond;
        this.stmt1 = stmt1;
        this.stmt2 = stmt2;
        this.printf = printf;
        this.formatString = formatString;
        this.type = type;
    }

    @Override
    public void printMoi() {
        if (type == 0) {
            block.printMoi();
        } else if (type == 1) {
            IOUtils.write("IFTK if\n");
            IOUtils.write("LPARENT (\n");
            cond.printMoi();
            IOUtils.write("RPARENT )\n");
            stmt1.printMoi();
            if (stmt2 != null) {
                IOUtils.write("ELSETK else\n");
                stmt2.printMoi();
            }
        } else if (type == 2) {
            IOUtils.write("WHILETK while\n");
            IOUtils.write("LPARENT (\n");
            cond.printMoi();
            IOUtils.write("RPARENT )\n");
            stmt1.printMoi();
        } else if (type == 3) {
            IOUtils.write("BREAKTK break\n");
            IOUtils.write("SEMICN ;\n");
        } else if (type == 4) {
            IOUtils.write("CONTINUETK continue\n");
            IOUtils.write("SEMICN ;\n");
        } else if (type == 5) {
            IOUtils.write("RETURNTK return\n");
            if (exp != null) {
                exp.printMoi();
            }
            IOUtils.write("SEMICN ;\n");
        } else if (type == 6) {
            IOUtils.write("PRINTFTK printf\n");
            IOUtils.write("LPARENT (\n");
            IOUtils.write(formatString.toString());
            for (Exp value : exps) {
                IOUtils.write("COMMA ,\n");
                value.printMoi();
            }
            IOUtils.write("RPARENT )\n");
            IOUtils.write("SEMICN ;\n");
        } else if (type == 7) {
            lval.printMoi();
            IOUtils.write("ASSIGN =\n");
            exp.printMoi();
            IOUtils.write("SEMICN ;\n");
        } else if (type == 8) {
            lval.printMoi();
            IOUtils.write("ASSIGN =\n");
            IOUtils.write("GETINTTK getint\n");
            IOUtils.write("LPARENT (\n");
            IOUtils.write("RPARENT )\n");
            IOUtils.write("SEMICN ;\n");
        } else if (type == 9) {
            if (exp != null) {
                exp.printMoi();
            }
            IOUtils.write("SEMICN ;\n");
        }
        IOUtils.write("<Stmt>\n");
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