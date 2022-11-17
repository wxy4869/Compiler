package mid.inst;


import mid.BasicBlock;
import mid.Value;
import mid.type.BaseType;

public class RetInst extends Inst {
    // ret <ret.type> {<ret.name>}
    Value ret;

    public RetInst(BasicBlock parent, Value ret) {
        super(parent);
        this.ret = ret;
    }

    @Override
    public String toString() {
        if (((BaseType)ret.getType()).getTag() == BaseType.Tag.I32) {
            return String.format("ret %s %s", ret.getType(), ret.getName());
        } else {
            return String.format("ret %s", ret.getType());
        }
    }
}
