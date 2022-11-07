package mid.inst;

import mid.BasicBlock;
import mid.Value;

public class PutInst extends Inst{
    // call void @putch(i32 114)
    // call void @putint(i32 %1)
    Value dst;
    boolean isInt;

    public PutInst(BasicBlock parent, Value dst, boolean isInt) {
        super(parent);
        this.dst = dst;
        this.isInt = isInt;
    }

    @Override
    public String toString() {
        if (isInt) {
            return String.format("call void @putint(i32 %s)", dst.getName());
        } else {
            return String.format("call void @putch(i32 %s)", dst.getName());
        }
    }

    public Value getDst() {
        return dst;
    }

    public void setDst(Value dst) {
        this.dst = dst;
    }

    public boolean isInt() {
        return isInt;
    }

    public void setInt(boolean anInt) {
        isInt = anInt;
    }
}
