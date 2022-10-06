package front.ASD;

import front.Token;
import javafx.util.Pair;
import utils.IOUtils;

import java.util.ArrayList;

public class MulExp implements Node{
    private ArrayList<UnaryExp> unaryExp;
    private ArrayList<Token> ops;

    public MulExp(ArrayList<UnaryExp> unaryExp, ArrayList<Token> ops) {
        this.unaryExp = unaryExp;
        this.ops = ops;
    }

    @Override
    public void printMoi() {
        int size = unaryExp.size();
        for (int i = 0; i < size; i++) {
            unaryExp.get(i).printMoi();
            IOUtils.write("<MulExp>\n");
            if (i < size - 1) {
                IOUtils.write(ops.get(i).toString());
            }
        }
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>(unaryExp);
    }

    public Pair<Token, Integer> getDimension() {
        return unaryExp.get(0).getDimension();
    }

    public ArrayList<UnaryExp> getUnaryExp() {
        return unaryExp;
    }

    public ArrayList<Token> getOps() {
        return ops;
    }
}