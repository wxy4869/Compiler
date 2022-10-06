package front.ASD;

import front.Token;
import utils.IOUtils;

public class FuncFParam implements Node{
    private Token ident;
    private ConstExp constExp;
    private int dimension;

    public FuncFParam(Token ident, ConstExp constExp, int dimension) {
        this.ident = ident;
        this.constExp = constExp;
        this.dimension = dimension;
    }

    @Override
    public void printMoi() {
        IOUtils.write("INTTK int\n");
        IOUtils.write(ident.toString());
        if (dimension == 1) {
            IOUtils.write("LBRACK [\n");
            IOUtils.write("RBRACK ]\n");
        } else if (dimension == 2) {
            IOUtils.write("LBRACK [\n");
            IOUtils.write("RBRACK ]\n");
            IOUtils.write("LBRACK [\n");
            constExp.printMoi();
            IOUtils.write("RBRACK ]\n");
        }
        IOUtils.write("<FuncFParam>\n");
    }

    public Token getIdent() {
        return ident;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public int getDimension() {
        return dimension;
    }
}