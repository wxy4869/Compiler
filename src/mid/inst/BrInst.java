package mid.inst;

import mid.BasicBlock;
import mid.Value;

public class BrInst extends Inst{
    /* br i1 <cond>, label <trueLabel>, label <falseLabel>
     * br label <dstLabel>
     */
    Value cond;
    BasicBlock trueLabel;
    BasicBlock falseLabel;
    BasicBlock dstLabel;

    public BrInst(BasicBlock parent, Value cond, BasicBlock trueLabel, BasicBlock falseLabel, BasicBlock dstLabel) {
        super(parent);
        this.cond = cond;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
        this.dstLabel = dstLabel;
    }

    @Override
    public String toString() {
        if (dstLabel == null) {
            return String.format("br i1 %s, label %%%s, label %%%s", cond.getName(), trueLabel.getName(), falseLabel.getName());
        } else {
            return String.format("br label %%%s", dstLabel.getName());
        }
    }

    public Value getCond() {
        return cond;
    }

    public void setCond(Value cond) {
        this.cond = cond;
    }

    public BasicBlock getTrueLabel() {
        return trueLabel;
    }

    public void setTrueLabel(BasicBlock trueLabel) {
        this.trueLabel = trueLabel;
    }

    public BasicBlock getFalseLabel() {
        return falseLabel;
    }

    public void setFalseLabel(BasicBlock falseLabel) {
        this.falseLabel = falseLabel;
    }

    public BasicBlock getDstLabel() {
        return dstLabel;
    }

    public void setDstLabel(BasicBlock dstLabel) {
        this.dstLabel = dstLabel;
    }
}
