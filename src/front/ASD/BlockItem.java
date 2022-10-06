package front.ASD;

import java.util.ArrayList;

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

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>();
        if (decl != null) {
            child.add(decl);
        }
        if (stmt != null) {
            child.add(stmt);
        }
        return child;
    }

    public Decl getDecl() {
        return decl;
    }

    public Stmt getStmt() {
        return stmt;
    }
}