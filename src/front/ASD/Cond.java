package front.ASD;

import utils.IOUtils;

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
}