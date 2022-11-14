package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class EqExp implements Node{
    // EqExp -> RelExp | EqExp ('==' | '!=') RelExp
    private ArrayList<RelExp> relExp;
    private ArrayList<Token> ops;

    public EqExp(ArrayList<RelExp> relExp, ArrayList<Token> ops) {
        this.relExp = relExp;
        this.ops = ops;
    }

    @Override
    public void printMoi() {
        int size = relExp.size();
        for (int i = 0; i < size; i++) {
            relExp.get(i).printMoi();
            IOUtils.write("<EqExp>\n", "output.txt", true);
            if (i < size - 1) {
                IOUtils.write(ops.get(i).toString(), "output.txt", true);
            }
        }
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>(relExp);
    }

    public ArrayList<RelExp> getRelExp() {
        return relExp;
    }

    public ArrayList<Token> getOps() {
        return ops;
    }
}