package mid.inst;

import mid.BasicBlock;
import mid.Value;

import java.util.Locale;

public class BinaryInst extends Inst {
    // <dst.name> = add <dst.type> <src1.name>, <src2.name>
    Tag tag;
    Value dst;
    Value src1;
    Value src2;

    public BinaryInst(BasicBlock parent, Tag tag, Value dst, Value src1, Value src2) {
        super(parent);
        this.tag = tag;
        this.dst = dst;
        this.src1 = src1;
        this.src2 = src2;
    }

    public static enum Tag {
        ADD,    // +
        SUB,    // -
        MUL,    // *
        SDIV,   // /
        SREM,   // %
        /*
        LT,     // <
        LE,     // <=
        GT,     // >
        GE,     // >=
        EQ,     // ==
        NE,     // !=
        */
    }

    @Override
    public String toString() {
        String op;
        if (tag == Tag.ADD || tag == Tag.SUB || tag == Tag.MUL || tag == Tag.SDIV || tag == Tag.SREM) {
            op = tag.name().toLowerCase(Locale.ROOT);
        } else {
            op = "icmp";
        }
        return String.format("%s = %s %s %s, %s", dst.getName(), op, dst.getType(), src1.getName(), src2.getName());
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Value getDst() {
        return dst;
    }

    public void setDst(Value dst) {
        this.dst = dst;
    }

    public Value getSrc1() {
        return src1;
    }

    public void setSrc1(Value src1) {
        this.src1 = src1;
    }

    public Value getSrc2() {
        return src2;
    }

    public void setSrc2(Value src2) {
        this.src2 = src2;
    }
}
