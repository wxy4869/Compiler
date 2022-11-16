package mid.inst;

import mid.BasicBlock;
import mid.Value;

public class LoadInst extends Inst {
    /* <dst.name> = load <dst.type>, <src.type> <src.name>
     * src 是 PointerType, 理解为地址
     * src.innerType = dst
     */
    Value src;
    Value dst;

    public LoadInst(BasicBlock parent, Value src, Value dst) {
        super(parent);
        this.src = src;
        this.dst = dst;
    }

    @Override
    public String toString() {
        return String.format("%s = load %s, %s %s", dst.getName(), dst.getType(), src.getType(), src.getName());
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
