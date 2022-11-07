package mid.inst;

import mid.BasicBlock;
import mid.Value;

import java.util.ArrayList;

public class CallInst extends Inst {
    // {<dst.name> = }call <func.type> <func.name>({<param.type> <param.name>, })
    Value func;
    Value dst;
    ArrayList<Value> params;

    public CallInst(BasicBlock parent, Value func, Value dst, ArrayList<Value> params) {
        super(parent);
        this.func = func;
        this.dst = dst;
        this.params = params;
    }

    @Override
    public String toString() {
        String hasDst = (dst != null) ? dst.getName() + " = " : "";
        StringBuilder paramStr = new StringBuilder();
        int size = params.size();
        if (size != 0) {
            paramStr.append(params.get(0).getType());
            paramStr.append(" ");
            paramStr.append(params.get(0).getName());
        }
        for (int i = 1; i < size; i++) {
            paramStr.append(", ");
            paramStr.append(params.get(i).getType());
            paramStr.append(" ");
            paramStr.append(params.get(i).getName());
        }
        return String.format("%scall %s %s(%s)", hasDst, func.getType(), func.getName(), paramStr);
    }

    public Value getFunc() {
        return func;
    }

    public void setFunc(Value func) {
        this.func = func;
    }

    public Value getDst() {
        return dst;
    }

    public void setDst(Value dst) {
        this.dst = dst;
    }

    public ArrayList<Value> getParams() {
        return params;
    }

    public void setParams(ArrayList<Value> params) {
        this.params = params;
    }
}
