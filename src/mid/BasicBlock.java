package mid;

import mid.inst.Inst;
import mid.type.Type;
import utils.IOUtils;

import java.util.ArrayList;

public class BasicBlock extends Value{
    Function parent;
    ArrayList<Inst> insts;
    boolean isEnd;

    public BasicBlock(String name, Function parent) {
        super(name, null);
        this.parent = parent;
        this.insts = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(String.format(";<label>:%s:\n", name));
        for (Inst value : insts) {
            str.append(String.format("\t%s\n", value.toString()));
        }
        return str.toString();
    }

    public Function getParent() {
        return parent;
    }

    public void setParent(Function parent) {
        this.parent = parent;
    }

    public ArrayList<Inst> getInsts() {
        return insts;
    }

    public void setInsts(ArrayList<Inst> insts) {
        this.insts = insts;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }
}
