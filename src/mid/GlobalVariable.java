package mid;

import mid.type.ArrayType;
import mid.type.BaseType;
import mid.type.Type;
import utils.IOUtils;

import java.util.ArrayList;

public class GlobalVariable extends User{
    // <name> = dso_local <global | constant> <type initVal | type initArrayVal>
    Module parent;
    boolean isConst;
    ArrayList<Integer> size;
    int initVal;
    ArrayList<Integer> initArrayVal;
    boolean isZeroInit;
    int offset;  // 用于将初始化打印

    public GlobalVariable(String name, Type type, boolean isConst, ArrayList<Integer> size, int initVal, ArrayList<Integer> initArrayVal, boolean isZeroInit) {
        super(name, type);
        this.parent = Module.module;
        this.isConst = isConst;
        this.size = size;
        this.initVal = initVal;
        this.initArrayVal = initArrayVal;
        this.isZeroInit = isZeroInit;
        this.parent.getGlobalVariables().add(this);
        this.offset = 0;
    }

    public void genInitStr(StringBuilder initStr, Type typeNow) {
        if (typeNow instanceof BaseType) {
            initStr.append(String.format("%s %s", typeNow, initArrayVal.get(offset)));
            offset++;
        } else {
            int size = ((ArrayType)typeNow).getSize();
            initStr.append(String.format("%s [", typeNow));
            for (int i = 0; i < size; i++) {
                genInitStr(initStr, ((ArrayType)typeNow).getInnerType());
                if (i != size - 1) {
                    initStr.append(", ");
                }
            }
            initStr.append("]");
        }
    }

    @Override
    public String toString() {
        String constStr = (isConst) ? "constant" : "global";
        StringBuilder initStr = new StringBuilder("");;
        if (type instanceof BaseType) {
            if (isZeroInit) {
                initStr.append(String.format("%s 0", type));
            } else {
                initStr.append(String.format("%s %d", type, initVal));
            }
        } else {
            if (isZeroInit) {
                initStr.append(String.format("%s zeroinitializer", type));
            } else {
                genInitStr(initStr, type);
            }
        }
        return String.format("%s = dso_local %s %s\n", name, constStr, initStr);
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public ArrayList<Integer> getSize() {
        return size;
    }

    public void setSize(ArrayList<Integer> size) {
        this.size = size;
    }

    public int getInitVal() {
        return initVal;
    }

    public void setInitVal(int initVal) {
        this.initVal = initVal;
    }

    public ArrayList<Integer> getInitArrayVal() {
        return initArrayVal;
    }

    public void setInitArrayVal(ArrayList<Integer> initArrayVal) {
        this.initArrayVal = initArrayVal;
    }

    public boolean isZeroInit() {
        return isZeroInit;
    }

    public void setZeroInit(boolean zeroInit) {
        isZeroInit = zeroInit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
