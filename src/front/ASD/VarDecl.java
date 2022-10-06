package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class VarDecl implements Node{
    private ArrayList<VarDef> varDef;

    public VarDecl(ArrayList<VarDef> varDef) {
        this.varDef = varDef;
    }

    @Override
    public void printMoi() {
        IOUtils.write("INTTK int\n");
        varDef.get(0).printMoi();
        int size = varDef.size();
        for (int i = 1; i < size; i++) {
            IOUtils.write("COMMA ,\n");
            varDef.get(i).printMoi();
        }
        IOUtils.write("SEMICN ;\n");
        IOUtils.write("<VarDecl>\n");
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>(varDef);
    }

    public ArrayList<VarDef> getVarDef() {
        return varDef;
    }
}