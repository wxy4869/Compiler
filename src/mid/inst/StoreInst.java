package mid.inst;

import mid.BasicBlock;
import mid.Value;

public class StoreInst extends Inst {
    // store <src.type> <src.name>, <dst.type>* <dst.name>
    Value src;
    Value dst;

    public StoreInst(BasicBlock parent, Value src, Value dst) {
        super(parent);
        this.src = src;
        this.dst = dst;
    }

    @Override
    public String toString() {
        return String.format("store %s %s, %s* %s", src.getType(), src.getName(), dst.getType(), dst.getName());
    }

    public Value getSrc() {
        return src;
    }

    public void setSrc(Value src) {
        this.src = src;
    }

    public Value getDst() {
        return dst;
    }

    public void setDst(Value dst) {
        this.dst = dst;
    }
}
