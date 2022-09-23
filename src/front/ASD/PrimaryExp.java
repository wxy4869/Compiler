package front.ASD;

import utils.IOUtils;

public class PrimaryExp implements Node{
    private Exp exp;
    private Lval lval;
    private Number number;

    public PrimaryExp(Exp exp, Lval lval, Number number) {
        this.exp = exp;
        this.lval = lval;
        this.number = number;
    }

    @Override
    public void printMoi() {
        if (exp != null) {
            IOUtils.write("LPARENT (\n");
            exp.printMoi();
            IOUtils.write("RPARENT )\n");
        } else if (lval != null) {
            lval.printMoi();
        } else if (number != null) {
            number.printMoi();
        }
        IOUtils.write("<PrimaryExp>\n");
    }
}