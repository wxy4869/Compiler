package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class ConstExp implements Node{
    private AddExp addExp;

    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }

    @Override
    public void printMoi() {
        addExp.printMoi();
        IOUtils.write("<ConstExp>\n");
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>();
        child.add(addExp);
        return child;
    }

    public AddExp getAddExp() {
        return addExp;
    }
}