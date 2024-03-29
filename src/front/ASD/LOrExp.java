package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class LOrExp implements Node{
    // LOrExp -> LAndExp | LOrExp '||' LAndExp
    private ArrayList<LAndExp> lAndExp;

    public LOrExp(ArrayList<LAndExp> lAndExp) {
        this.lAndExp = lAndExp;
    }

    @Override
    public void printMoi() {
        int size = lAndExp.size();
        for (int i = 0; i < size; i++) {
            lAndExp.get(i).printMoi();
            IOUtils.write("<LOrExp>\n", "output.txt", true);
            if (i < size - 1) {
                IOUtils.write("OR ||\n", "output.txt", true);
            }
        }
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>(lAndExp);
    }

    public ArrayList<LAndExp> getlAndExp() {
        return lAndExp;
    }
}