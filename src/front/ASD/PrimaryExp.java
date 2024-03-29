package front.ASD;

import front.Token;
import table.Def;
import table.SymGenerator;
import utils.IOUtils;
import utils.Pair;

import java.util.ArrayList;

public class PrimaryExp implements Node{
    // PrimaryExp -> '(' Exp ')' | Lval | Number
    private Exp exp;
    private Lval lval;
    private Number number;

    public PrimaryExp(Exp exp, Lval lval, Number number) {
        this.exp = exp;
        this.lval = lval;
        this.number = number;
    }

    @Override
    public void printMoi() {
        if (exp != null) {
            IOUtils.write("LPARENT (\n", "output.txt", true);
            exp.printMoi();
            IOUtils.write("RPARENT )\n", "output.txt", true);
        } else if (lval != null) {
            lval.printMoi();
        } else if (number != null) {
            number.printMoi();
        }
        IOUtils.write("<PrimaryExp>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>();
        if (exp != null) {
            child.add(exp);
        } else if (lval != null) {
            child.add(lval);
        } else if (number != null) {
            child.add(number);
        }
        return child;
    }

    public Pair<Token, Integer> getDimension() {
        if (exp != null) {
            return exp.getDimension();
        } else if (lval != null) {
            return new Pair<>(lval.getIdent(), lval.getExp().size());
        } else {
            return new Pair<>(null, 0);
        }
    }

    public int calValue() {
        if (exp != null) {
            return exp.calValue();
        } else if (lval != null) {
            return lval.calValue();
        } else {
            return number.calValue();
        }
    }

    public Exp getExp() {
        return exp;
    }

    public Lval getLval() {
        return lval;
    }

    public Number getNumber() {
        return number;
    }
}