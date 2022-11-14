package mid.inst;

import mid.BasicBlock;
import mid.Value;

public class ZextInst extends Inst{
    // <dst.name> = zext <src.type> <src.name> to <dst.type>
    Value src;
    Value dst;

    public ZextInst(BasicBlock parent, Value src, Value dst) {
        super(parent);
        this.src = src;
        this.dst = dst;
    }

    @Override
    public String toString() {
        return String.format("%s = zext %s %s to %s", dst.getName(), src.getType(), src.getName(), dst.getType());
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
