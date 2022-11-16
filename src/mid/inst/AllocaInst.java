package mid.inst;

import mid.BasicBlock;
import mid.Value;

public class AllocaInst extends Inst {
    /* <dst.name> = alloca <dst.type.innerType>
     * dst 是 PointerType, 理解为地址
     * dst 是 i32* 时, 指令是 %1 = alloca i32
     */
    Value dst;

    public AllocaInst(BasicBlock parent, Value dst) {
        super(parent);
        this.dst = dst;
    }

    @Override
    public String toString() {
        return String.format("%s = alloca %s", dst.getName(), dst.getType().getInnerType());
    }

    public Value getDst() {
        return dst;
    }

    public void setDst(Value dst) {
        this.dst = dst;
    }
}
