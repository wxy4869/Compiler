package front.ASD;

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

    public AddExp getAddExp() {
        return addExp;
    }
}