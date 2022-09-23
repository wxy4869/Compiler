package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class LOrExp implements Node{
    private ArrayList<LAndExp> lAndExp;

    public LOrExp(ArrayList<LAndExp> lAndExp) {
        this.lAndExp = lAndExp;
    }

    @Override
    public void printMoi() {
        int size = lAndExp.size();
        for (int i = 0; i < size; i++) {
            lAndExp.get(i).printMoi();
            IOUtils.write("<LOrExp>\n");
            if (i < size - 1) {
                IOUtils.write("OR ||\n");
            }
        }
    }
}