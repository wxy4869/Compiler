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
        String retStr = ret.getType().toString();
        if (((BaseType)ret.getType()).getTag() == BaseType.Tag.I32) {
            retStr = retStr + " " + ret.getName();
        }
        return String.format("ret %s", retStr);
    }
}
