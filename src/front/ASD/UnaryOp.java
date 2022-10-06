package front.ASD;

import front.Token;
import utils.IOUtils;

public class UnaryOp implements Node{
    private Token op;

    public UnaryOp(Token op) {
        this.op = op;
    }

    @Override
    public void printMoi() {
        IOUtils.write(op.toString());
        IOUtils.write("<UnaryOp>\n");
    }

    public Token getOp() {
        return op;
    }
}