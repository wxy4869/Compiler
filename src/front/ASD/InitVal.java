package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class InitVal implements Node{
    // InitVal -> Exp | '{' [ InitVal { ',' InitVal } ] '}
    private Exp exp;
    private ArrayList<InitVal> initVal;

    public InitVal(Exp exp, ArrayList<InitVal> initVal) {
        this.exp = exp;
        this.initVal = initVal;
    }

    @Override
    public void printMoi() {
        if (exp != null) {
            exp.printMoi();
        } else {
            IOUtils.write("LBRACE {\n", "output.txt", true);
            int size = initVal.size();
            if (size > 0) {
                initVal.get(0).printMoi();
                for (int i = 1; i < size; i++) {
                    IOUtils.write("COMMA ,\n", "output.txt", true);
                    initVal.get(i).printMoi();
                }
            }
            IOUtils.write("RBRACE }\n", "output.txt", true);
        }
        IOUtils.write("<InitVal>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>();
        if (exp != null) {
            child.add(exp);
        } else {
            child.addAll(initVal);
        }
        return child;
    }

    public Exp getExp() {
        return exp;
    }

    public ArrayList<InitVal> getInitVal() {
        return initVal;
    }
}