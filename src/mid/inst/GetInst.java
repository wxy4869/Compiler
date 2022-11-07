package mid.inst;

import mid.BasicBlock;
import mid.Value;

public class GetInst extends Inst{
    // <dst.name> = call i32 @getint()
    Value dst;

    public GetInst(BasicBlock parent, Value dst) {
        super(parent);
        this.dst = dst;
    }

    @Override
    public String toString() {
        return String.format("%s = call i32 @getint()", dst.getName());
    }

    public Value getDst() {
        return dst;
    }

    public void setDst(Value dst) {
        this.dst = dst;
    }
}
