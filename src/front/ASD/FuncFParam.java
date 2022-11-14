package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class FuncFParam implements Node{
    // FuncFParam -> BType Ident ['[' ']' { '[' ConstExp ']' }]
    private Token ident;
    private ArrayList<ConstExp> constExp;
    boolean isPointer;

    public FuncFParam(Token ident, ArrayList<ConstExp> constExp, Boolean isPointer) {
        this.ident = ident;
        this.constExp = constExp;
        this.isPointer = isPointer;
    }

    @Override
    public void printMoi() {
        IOUtils.write("INTTK int\n", "output.txt", true);
        IOUtils.write(ident.toString(), "output.txt", true);
        if (isPointer) {
            IOUtils.write("LBRACK [\n", "output.txt", true);
            IOUtils.write("RBRACK ]\n", "output.txt", true);
            for (ConstExp value : constExp) {
                IOUtils.write("LBRACK [\n", "output.txt", true);
                value.printMoi();
                IOUtils.write("RBRACK ]\n", "output.txt", true);
            }
        }
        IOUtils.write("<FuncFParam>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>(constExp);
    }

    public Token getIdent() {
        return ident;
    }

    public ArrayList<ConstExp> getConstExp() {
        return constExp;
    }

    public boolean isPointer() {
        return isPointer;
    }
}