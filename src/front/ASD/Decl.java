package front.ASD;

import java.util.ArrayList;

public class Decl implements Node{
    private ConstDecl constDecl;
    private VarDecl varDecl;

    public Decl(ConstDecl constDecl, VarDecl varDecl) {
        this.constDecl = constDecl;
        this.varDecl = varDecl;
    }

    @Override
    public void printMoi() {
        if (constDecl != null) {
            constDecl.printMoi();
        }
        if (varDecl != null) {
            varDecl.printMoi();
        }
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>();
        if (constDecl != null) {
            child.add(constDecl);
        }
        if (varDecl != null) {
            child.add(varDecl);
        }
        return child;
    }

    public ConstDecl getConstDecl() {
        return constDecl;
    }

    public VarDecl getVarDecl() {
        return varDecl;
    }
}