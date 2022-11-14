package front.ASD;

import front.Token;
import table.Def;
import table.SymGenerator;
import utils.IOUtils;

import java.util.ArrayList;

public class Lval implements Node{
    // Lval -> Ident {'[' Exp ']'}
    private Token ident;
    private ArrayList<Exp> exp;

    public Lval(Token ident, ArrayList<Exp> exp) {
        this.ident = ident;
        this.exp = exp;
    }

    @Override
    public void printMoi() {
        IOUtils.write(ident.toString(), "output.txt", true);
        for (Exp value : exp) {
            IOUtils.write("LBRACK [\n", "output.txt", true);
            value.printMoi();
            IOUtils.write("RBRACK ]\n", "output.txt", true);
        }
        IOUtils.write("<LVal>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>(exp);
    }

    public int calValue() {
        Def def = (Def) SymGenerator.currentTable.getSymbol(ident.getSrc(), true, ident.getLineNum());
        int dimension = def.getDimension();
        if (def.getDimension() == 0) {
            return def.getInitVal();
        } else {
            ArrayList<Integer> size = def.getSize();
            int sum = 0, mul = 1;
            for (int i = dimension - 1; i >= 0; i--) {
                int index = exp.get(i).calValue();
                sum += index * mul;
                mul *= size.get(i);
            }
            return  def.getInitArrayVal().get(sum);
        }
    }

    public Token getIdent() {
        return ident;
    }

    public ArrayList<Exp> getExp() {
        return exp;
    }
}