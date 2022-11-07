package front.ASD;

import front.Token;
import utils.IOUtils;
import utils.Pair;

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

    public int calValue() {
        int value = unaryExp.get(0).calValue();
        int size = unaryExp.size();
        for (int i = 1; i < size; i++) {
            if (ops.get(i - 1).getSrc().equals("*")) {
                value *= unaryExp.get(i).calValue();
            } else if (ops.get(i - 1).getSrc().equals("/")) {
                value /= unaryExp.get(i).calValue();
            } else {
                value %= unaryExp.get(i).calValue();
            }
        }
        return value;
    }

    public ArrayList<UnaryExp> getUnaryExp() {
        return unaryExp;
    }

    public ArrayList<Token> getOps() {
        return ops;
    }
}