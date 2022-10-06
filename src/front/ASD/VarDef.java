package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class VarDef implements Node{
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
        IOUtils.write(ident.toString());
        for (ConstExp value : constExp) {
            IOUtils.write("LBRACK [\n");
            value.printMoi();
            IOUtils.write("RBRACK ]\n");
        }
        if (initVal != null) {
            IOUtils.write("ASSIGN =\n");
            initVal.printMoi();
        }
        IOUtils.write("<VarDef>\n");
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