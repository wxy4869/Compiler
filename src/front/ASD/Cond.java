package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class Cond implements Node{
    private LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    @Override
    public void printMoi() {
        lOrExp.printMoi();
        IOUtils.write("<Cond>\n");
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>();
        child.add(lOrExp);
        return child;
    }

    public LOrExp getlOrExp() {
        return lOrExp;
    }
}