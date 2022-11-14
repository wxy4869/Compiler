package mid.inst;

import mid.BasicBlock;
import mid.User;

public abstract class Inst extends User {
    BasicBlock parent;

    public Inst(BasicBlock parent) {
        this.parent = parent;
        if (!this.parent.isEnd()) {
            this.parent.getInsts().add(this);
        }
    }

    public BasicBlock getParent() {
        return parent;
    }

    public void setParent(BasicBlock parent) {
        this.parent = parent;
    }
}
