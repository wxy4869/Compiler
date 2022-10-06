package front.ASD;

import front.Token;
import javafx.util.Pair;
import utils.IOUtils;

import java.util.ArrayList;

public class PrimaryExp implements Node{
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
            IOUtils.write("LPARENT (\n");
            exp.printMoi();
            IOUtils.write("RPARENT )\n");
        } else if (lval != null) {
            lval.printMoi();
        } else if (number != null) {
            number.printMoi();
        }
        IOUtils.write("<PrimaryExp>\n");
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