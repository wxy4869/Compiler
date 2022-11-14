package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class VarDecl implements Node{
    // VarDecl -> BType VarDef { ',' VarDef } ';'
    private ArrayList<VarDef> varDef;

    public VarDecl(ArrayList<VarDef> varDef) {
        this.varDef = varDef;
    }

    @Override
    public void printMoi() {
        IOUtils.write("INTTK int\n", "output.txt", true);
        varDef.get(0).printMoi();
        int size = varDef.size();
        for (int i = 1; i < size; i++) {
            IOUtils.write("COMMA ,\n", "output.txt", true);
            varDef.get(i).printMoi();
        }
        IOUtils.write("SEMICN ;\n", "output.txt", true);
        IOUtils.write("<VarDecl>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>(varDef);
    }

    public ArrayList<VarDef> getVarDef() {
        return varDef;
    }
}