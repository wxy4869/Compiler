package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class FuncRParams implements Node{
    private ArrayList<Exp> exp;

    public FuncRParams(ArrayList<Exp> exp) {
        this.exp = exp;
    }

    @Override
    public void printMoi() {
        exp.get(0).printMoi();
        int size = exp.size();
        for (int i = 1; i < size; i++) {
            IOUtils.write("COMMA ,\n");
            exp.get(i).printMoi();
        }
        IOUtils.write("<FuncRParams>\n");
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>(exp);
    }

    public ArrayList<Exp> getExp() {
        return exp;
    }
}