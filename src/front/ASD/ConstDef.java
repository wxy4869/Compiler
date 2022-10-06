package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class ConstDef implements Node{
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
        IOUtils.write(ident.toString());
        for (ConstExp value : constExp) {
            IOUtils.write("LBRACK [\n");
            value.printMoi();
            IOUtils.write("RBRACK ]\n");
        }
        IOUtils.write("ASSIGN =\n");
        constInitVal.printMoi();
        IOUtils.write("<ConstDef>\n");
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