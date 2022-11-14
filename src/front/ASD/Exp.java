package front.ASD;

import front.Token;
import utils.IOUtils;
import utils.Pair;

import java.util.ArrayList;

public class Exp implements Node{
    // Exp -> AddExp
    private AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    @Override
    public void printMoi() {
        addExp.printMoi();
        IOUtils.write("<Exp>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>();
        child.add(addExp);
        return child;
    }

    public int calValue() {
        return addExp.calValue();
    }

    public Pair<Token, Integer> getDimension() {
        return addExp.getDimension();
    }

    public AddExp getAddExp() {
        return addExp;
    }
}