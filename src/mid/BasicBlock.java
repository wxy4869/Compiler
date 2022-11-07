package mid;

import mid.inst.Inst;
import mid.type.Type;
import utils.IOUtils;

import java.util.ArrayList;

public class BasicBlock extends Value{
    Function parent;
    ArrayList<Inst> insts;

    public BasicBlock(String name, Function parent) {
        super(name, null);
        this.parent = parent;
        this.insts = new ArrayList<>();
        this.parent.getBasicBlocks().add(this);
    }

    public void printMoi(String path) {
        IOUtils.write(String.format(";<label>:%s:\n", name), path, true);
        for (Inst value : insts) {
            IOUtils.write(String.format("\t%s\n", value.toString()), path, true);
        }
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
}
