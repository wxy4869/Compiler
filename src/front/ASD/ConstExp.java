package front.ASD;

import utils.IOUtils;

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

    public AddExp getAddExp() {
        return addExp;
    }
}