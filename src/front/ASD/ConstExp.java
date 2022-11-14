package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class ConstExp implements Node{
    // ConstExp -> AddExp
    private AddExp addExp;

    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }

    @Override
    public void printMoi() {
        addExp.printMoi();
        IOUtils.write("<ConstExp>\n", "output.txt", true);
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

    public AddExp getAddExp() {
        return addExp;
    }
}