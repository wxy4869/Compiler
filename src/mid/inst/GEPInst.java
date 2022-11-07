package mid.inst;

import mid.BasicBlock;
import mid.Value;

import java.util.ArrayList;

public class GEPInst extends Inst {
    // <dst.name> = getelementptr <src.type>, <src.type>* <src.name>{, <index.type> <index.name>}
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
        if (indexs != null) {
            int size = indexs.size();
            if (size != 0) {
                indexStr.append(indexs.get(0).getType());
                indexStr.append(" ");
                indexStr.append(indexs.get(0).getName());
            }
            for (int i = 1; i < size; i++) {
                indexStr.append(", ");
                indexStr.append(indexs.get(i).getType());
                indexStr.append(" ");
                indexStr.append(indexs.get(i).getName());
            }
        }
        return String.format("%s = getelementptr %s, %s* %s, %s",
                dst.getName(), src.getType(), src.getType(), src.getName(), indexStr);
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
