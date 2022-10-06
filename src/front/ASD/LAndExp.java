package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class LAndExp implements Node{
    private ArrayList<EqExp> eqExp;

    public LAndExp(ArrayList<EqExp> eqExp) {
        this.eqExp = eqExp;
    }

    @Override
    public void printMoi() {
        int size = eqExp.size();
        for (int i = 0; i < size; i++) {
            eqExp.get(i).printMoi();
            IOUtils.write("<LAndExp>\n");
            if (i < size - 1) {
                IOUtils.write("AND &&\n");
            }
        }
    }

    public ArrayList<EqExp> getEqExp() {
        return eqExp;
    }
}