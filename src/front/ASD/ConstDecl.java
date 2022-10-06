package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class ConstDecl implements Node{
    private ArrayList<ConstDef> constDef;

    public ConstDecl(ArrayList<ConstDef> constDef) {
        this.constDef = constDef;
    }

    @Override
    public void printMoi() {
        IOUtils.write("CONSTTK const\n");
        IOUtils.write("INTTK int\n");
        constDef.get(0).printMoi();
        int size = constDef.size();
        for (int i = 1; i < size; i++) {
            IOUtils.write("COMMA ,\n");
            constDef.get(i).printMoi();
        }
        IOUtils.write("SEMICN ;\n");
        IOUtils.write("<ConstDecl>\n");
    }

    public ArrayList<ConstDef> getConstDef() {
        return constDef;
    }
}