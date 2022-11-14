package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class UnaryOp implements Node{
    // UnaryOp -> '+' | '−' | '!'
    private Token op;

    public UnaryOp(Token op) {
        this.op = op;
    }

    @Override
    public void printMoi() {
        IOUtils.write(op.toString(), "output.txt", true);
        IOUtils.write("<UnaryOp>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>();
    }

    public Token getOp() {
        return op;
    }
}