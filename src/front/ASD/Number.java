package front.ASD;

import front.Token;
import utils.IOUtils;

public class Number implements Node{
    private Token number;

    public Number(Token number) {
        this.number = number;
    }

    @Override
    public void printMoi() {
        IOUtils.write(number.toString());
        IOUtils.write("<Number>\n");
    }

    public Token getNumber() {
        return number;
    }
}
