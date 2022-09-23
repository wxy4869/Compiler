package front.ASD;

public class BlockItem implements Node{
    private Decl decl;
    private Stmt stmt;

    public BlockItem(Decl decl, Stmt stmt) {
        this.decl = decl;
        this.stmt = stmt;
    }

    @Override
    public void printMoi() {
        if (decl != null) {
            decl.printMoi();
        }
        if (stmt != null) {
            stmt.printMoi();
        }
    }
}