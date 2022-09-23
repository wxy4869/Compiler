package front.ASD;

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
}