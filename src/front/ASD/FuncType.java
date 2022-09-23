package front.ASD;

import front.Token;
import utils.IOUtils;

public class FuncType implements Node{
    private Token funcType;

    public FuncType(Token funcType) {
        this.funcType = funcType;
    }

    @Override
    public void printMoi() {
        IOUtils.write(funcType.toString());
        IOUtils.write("<FuncType>\n");
    }
}