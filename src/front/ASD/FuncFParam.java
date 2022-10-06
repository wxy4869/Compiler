package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class FuncFParam implements Node{
    private Token ident;
    private ConstExp constExp;
    private int type;

    public FuncFParam(Token ident, ConstExp constExp, int type) {
        this.ident = ident;
        this.constExp = constExp;
        this.type = type;
    }

    @Override
    public void printMoi() {
        IOUtils.write("INTTK int\n");
        IOUtils.write(ident.toString());
        if (type == 1) {
            IOUtils.write("LBRACK [\n");
            IOUtils.write("RBRACK ]\n");
        } else if (type == 2) {
            IOUtils.write("LBRACK [\n");
            IOUtils.write("RBRACK ]\n");
            IOUtils.write("LBRACK [\n");
            constExp.printMoi();
            IOUtils.write("RBRACK ]\n");
        }
        IOUtils.write("<FuncFParam>\n");
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>();
        if (type == 2) {
            child.add(constExp);
        }
        return child;
    }

    public Token getIdent() {
        return ident;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public int getType() {
        return type;
    }
}