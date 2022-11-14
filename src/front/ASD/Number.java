package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class Number implements Node{
    // Number -> IntConst
    private Token number;

    public Number(Token number) {
        this.number = number;
    }

    @Override
    public void printMoi() {
        IOUtils.write(number.toString(), "output.txt", true);
        IOUtils.write("<Number>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>();
    }

    public int calValue() {
        return number.getIntVal();
    }

    public Token getNumber() {
        return number;
    }
}
