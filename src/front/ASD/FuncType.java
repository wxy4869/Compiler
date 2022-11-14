package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class FuncType implements Node{
    // FuncType -> 'void' | 'int'
    private Token funcType;

    public FuncType(Token funcType) {
        this.funcType = funcType;
    }

    @Override
    public void printMoi() {
        IOUtils.write(funcType.toString(), "output.txt", true);
        IOUtils.write("<FuncType>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>();
    }

    public Token getFuncType() {
        return funcType;
    }
}