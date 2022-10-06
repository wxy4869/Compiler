package front.ASD;

import front.Token;
import utils.IOUtils;
import utils.Pair;

import java.util.ArrayList;

public class AddExp implements Node{
    private ArrayList<MulExp> mulExp;
    private ArrayList<Token> ops;

    public AddExp(ArrayList<MulExp> mulExp, ArrayList<Token> ops) {
        this.mulExp = mulExp;
        this.ops = ops;
    }

    @Override
    public void printMoi() {
        int size = mulExp.size();
        for (int i = 0; i < size; i++) {
            mulExp.get(i).printMoi();
            IOUtils.write("<AddExp>\n");
            if (i < size - 1) {
                IOUtils.write(ops.get(i).toString());
            }
        }
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>(mulExp);
    }

    public Pair<Token, Integer> getDimension() {
        return mulExp.get(0).getDimension();
    }

    public ArrayList<MulExp> getMulExp() {
        return mulExp;
    }

    public ArrayList<Token> getOps() {
        return ops;
    }
}