package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class ConstDef implements Node{
    // ConstDef -> Ident { '[' ConstExp ']' } '=' ConstInitVal
    private Token ident;
    private ArrayList<ConstExp> constExp;
    private ConstInitVal constInitVal;

    public ConstDef(Token ident, ArrayList<ConstExp> constExp, ConstInitVal constInitVal) {
        this.ident = ident;
        this.constExp = constExp;
        this.constInitVal = constInitVal;
    }

    @Override
    public void printMoi() {
        IOUtils.write(ident.toString(), "output.txt", true);
        for (ConstExp value : constExp) {
            IOUtils.write("LBRACK [\n", "output.txt", true);
            value.printMoi();
            IOUtils.write("RBRACK ]\n", "output.txt", true);
        }
        IOUtils.write("ASSIGN =\n", "output.txt", true);
        constInitVal.printMoi();
        IOUtils.write("<ConstDef>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>(constExp);
        child.add(constInitVal);
        return child;
    }

    public Token getIdent() {
        return ident;
    }

    public ArrayList<ConstExp> getConstExp() {
        return constExp;
    }

    public ConstInitVal getConstInitVal() {
        return constInitVal;
    }
}