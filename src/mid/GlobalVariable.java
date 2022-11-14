package mid;

import mid.type.ArrayType;
import mid.type.BaseType;
import mid.type.Type;
import utils.IOUtils;

import java.util.ArrayList;

public class GlobalVariable extends User{
    // <name> = dso_local <global | constant> <type> <initVal | initArrayVal>
    Module parent;
    boolean isConst;
    int size1;
    int size2;
    int initVal;
    ArrayList<Integer> initArrayVal;
    boolean isZeroInit;

    public GlobalVariable(String name, Type type, boolean isConst,
                          int size1, int size2, int initVal, ArrayList<Integer> initArrayVal, boolean isZeroInit) {
        super(name, type);
        this.parent = Module.module;
        this.isConst = isConst;
        this.size1 = size1;
        this.size2 = size2;
        this.initVal = initVal;
        this.initArrayVal = initArrayVal;
        this.isZeroInit = isZeroInit;
        this.parent.getGlobalVariables().add(this);
    }

    public void printMoi(String path) {
        String constStr = (isConst) ? "constant" : "global";
        StringBuilder initStr;
        if (type instanceof BaseType) {
            if (isZeroInit) {
                initStr = new StringBuilder(Integer.toString(0));
            } else {
                initStr = new StringBuilder(Integer.toString(initVal));
            }
        } else {
            if (isZeroInit) {
                initStr = new StringBuilder("zeroinitializer");
            } else {
                initStr = new StringBuilder("[");
                ArrayType arrayType1 = (ArrayType) type;
                if (size2 == 0) {
                    // [i32 1, i32 2]
                    initStr.append(arrayType1.getInnerType().toString()).append(" ").append(initArrayVal.get(0));
                    for (int i = 1; i < size1; i++) {
                        initStr.append(", ");
                        initStr.append(arrayType1.getInnerType().toString()).append(" ").append(initArrayVal.get(i));
                    }
                    initStr.append("]");
                } else {
                    // [[2 x i32] [i32 1, i32 2], [2 x i32] [i32 3, i32 4]]
                    for (int i = 0; i < size1; i++) {
                        initStr.append(arrayType1.getInnerType().toString());
                        initStr.append(" [");
                        ArrayType arrayType2 = (ArrayType) arrayType1.getInnerType();
                        for (int j = 0; j < size2; j++) {
                            initStr.append(arrayType2.getInnerType().toString()).append(" ").append(initArrayVal.get(i * size2 + j));
                            if (j != size2 - 1) {
                                initStr.append(", ");
                            }
                        }
                        if (i != size1 - 1) {
                            initStr.append("], ");
                        } else {
                            initStr.append("]");
                        }
                    }
                    initStr.append("]");
                }
            }
        }
        IOUtils.write(String.format("%s = dso_local %s %s %s\n", name, constStr, type, initStr), path, true);
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public int getSize1() {
        return size1;
    }

    public void setSize1(int size1) {
        this.size1 = size1;
    }

    public int getSize2() {
        return size2;
    }

    public void setSize2(int size2) {
        this.size2 = size2;
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
}
