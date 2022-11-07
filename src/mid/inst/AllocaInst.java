package mid.inst;

import mid.BasicBlock;
import mid.Value;

public class AllocaInst extends Inst {
    // <dst.name> = alloca <dst.type>
    Value dst;

    public AllocaInst(BasicBlock parent, Value dst) {
        super(parent);
        this.dst = dst;
    }

    @Override
    public String toString() {
        return String.format("%s = alloca %s", dst.getName(), dst.getType());
    }

    public Value getDst() {
        return dst;
    }

    public void setDst(Value dst) {
        this.dst = dst;
    }
}
