package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class Lval implements Node{
    private Token ident;
    private ArrayList<Exp> exp;

    public Lval(Token ident, ArrayList<Exp> exp) {
        this.ident = ident;
        this.exp = exp;
    }

    @Override
    public void printMoi() {
        IOUtils.write(ident.toString());
        for (Exp value : exp) {
            IOUtils.write("LBRACK [\n");
            value.printMoi();
            IOUtils.write("RBRACK ]\n");
        }
        IOUtils.write("<LVal>\n");
    }

    public Token getIdent() {
        return ident;
    }

    public ArrayList<Exp> getExp() {
        return exp;
    }
}