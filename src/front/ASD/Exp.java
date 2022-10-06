package front.ASD;

import front.Token;
import javafx.util.Pair;
import utils.IOUtils;

public class Exp implements Node{
    private AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    @Override
    public void printMoi() {
        addExp.printMoi();
        IOUtils.write("<Exp>\n");
    }

    public Pair<Token, Integer> getDimension() {
        return addExp.getDimension();
    }

    public AddExp getAddExp() {
        return addExp;
    }
}