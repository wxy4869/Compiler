package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class InitVal implements Node{
    private Exp exp;
    private ArrayList<InitVal> initVal;

    public InitVal(Exp exp, ArrayList<InitVal> initVal) {
        this.exp = exp;
        this.initVal = initVal;
    }

    @Override
    public void printMoi() {
        if (exp != null) {
            exp.printMoi();
        } else {
            IOUtils.write("LBRACE {\n");
            int size = initVal.size();
            if (size > 0) {
                initVal.get(0).printMoi();
                for (int i = 1; i < size; i++) {
                    IOUtils.write("COMMA ,\n");
                    initVal.get(i).printMoi();
                }
            }
            IOUtils.write("RBRACE }\n");
        }
        IOUtils.write("<InitVal>\n");
    }
}