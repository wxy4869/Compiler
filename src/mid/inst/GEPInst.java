package mid.inst;

import mid.BasicBlock;
import mid.Value;

import java.util.ArrayList;

public class GEPInst extends Inst {
    /* <dst.name> = getelementptr <src.type.innerType>, <src.type> <src.name>{, <index.type> <index.name>}
     * src 是 PointerType
     */
    Value src;
    Value dst;
    ArrayList<Value> indexs;

    public GEPInst(BasicBlock parent, Value src, Value dst, ArrayList<Value> indexs) {
        super(parent);
        this.src = src;
        this.dst = dst;
        this.indexs = indexs;
    }

    @Override
    public String toString() {
        StringBuilder indexStr = new StringBuilder();
        int size = indexs.size();
        for (int i = 0; i < size; i++) {
            indexStr.append(String.format("%s %s", indexs.get(i).getType(), indexs.get(i).getName()));
            if (i != size - 1) {
                indexStr.append(", ");
            }
        }
        return String.format("%s = getelementptr %s, %s %s, %s", dst.getName(), src.getType().getInnerType(), src.getType(), src.getName(), indexStr);
    }

    public Value getDst() {
        return dst;
    }

    public void setDst(Value dst) {
        this.dst = dst;
    }

    public Value getSrc() {
        return src;
    }

    public void setSrc(Value src) {
        this.src = src;
    }

    public ArrayList<Value> getIndexs() {
        return indexs;
    }

    public void setIndexs(ArrayList<Value> indexs) {
        this.indexs = indexs;
    }
}
