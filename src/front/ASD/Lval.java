package front.ASD;

import front.Token;
import table.Def;
import table.SymGenerator;
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

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>(exp);
    }

    public int calValue() {
        Def def = (Def) SymGenerator.currentTable.getSymbol(ident.getSrc(), true);
        if (def.getDimension() == 0) {
            return def.getInitVal();
        } else if (def.getDimension() == 1) {
            int index1 = exp.get(0).calValue();
            return def.getInitArrayVal().get(index1);
        } else {
            int index1 = exp.get(0).calValue();
            int index2 = exp.get(1).calValue();
            return def.getInitArrayVal().get(index1 * def.getSize1() + index2);
        }
    }

    public Token getIdent() {
        return ident;
    }

    public ArrayList<Exp> getExp() {
        return exp;
    }
}