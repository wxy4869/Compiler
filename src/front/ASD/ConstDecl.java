package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class ConstDecl implements Node{
    // ConstDecl -> 'const' BType ConstDef { ',' ConstDef } ';'
    private ArrayList<ConstDef> constDef;

    public ConstDecl(ArrayList<ConstDef> constDef) {
        this.constDef = constDef;
    }

    @Override
    public void printMoi() {
        IOUtils.write("CONSTTK const\n", "output.txt", true);
        IOUtils.write("INTTK int\n", "output.txt", true);
        constDef.get(0).printMoi();
        int size = constDef.size();
        for (int i = 1; i < size; i++) {
            IOUtils.write("COMMA ,\n", "output.txt", true);
            constDef.get(i).printMoi();
        }
        IOUtils.write("SEMICN ;\n", "output.txt", true);
        IOUtils.write("<ConstDecl>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>(constDef);
    }

    public ArrayList<ConstDef> getConstDef() {
        return constDef;
    }
}