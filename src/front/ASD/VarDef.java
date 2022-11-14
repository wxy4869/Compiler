package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class VarDef implements Node{
    // VarDef -> Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
    private Token ident;
    private ArrayList<ConstExp> constExp;
    private InitVal initVal;

    public VarDef(Token ident, ArrayList<ConstExp> constExp, InitVal initVal) {
        this.ident = ident;
        this.constExp = constExp;
        this.initVal = initVal;
    }

    @Override
    public void printMoi() {
        IOUtils.write(ident.toString(), "output.txt", true);
        for (ConstExp value : constExp) {
            IOUtils.write("LBRACK [\n", "output.txt", true);
            value.printMoi();
            IOUtils.write("RBRACK ]\n", "output.txt", true);
        }
        if (initVal != null) {
            IOUtils.write("ASSIGN =\n", "output.txt", true);
            initVal.printMoi();
        }
        IOUtils.write("<VarDef>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>(constExp);
        if (initVal != null) {
            child.add(initVal);
        }
        return child;
    }

    public Token getIdent() {
        return ident;
    }

    public ArrayList<ConstExp> getConstExp() {
        return constExp;
    }

    public InitVal getInitVal() {
        return initVal;
    }
}