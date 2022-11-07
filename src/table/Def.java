package table;

import mid.Value;

import java.util.ArrayList;

public class Def implements Symbol {
    private String name;
    private boolean isConst;
    private int dimension;
    private int size1;
    private int size2;
    private int initVal;
    private ArrayList<Integer> initArrayVal;
    private boolean isZeroInit;
    private Value addr;

    public Def(String name, boolean isConst, int dimension, int size1, int size2) {
        this.name = name;
        this.isConst = isConst;
        this.dimension = dimension;
        this.size1 = size1;
        this.size2 = size2;
        if (dimension != 0) {
            initArrayVal = new ArrayList<>();
        }
        this.isZeroInit = false;
    }

    public String getName() {
        return name;
    }

    public boolean isConst() {
        return isConst;
    }

    public int getDimension() {
        return dimension;
    }

    public int getSize1() {
        return size1;
    }

    public int getSize2() {
        return size2;
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

    public boolean isZeroInit() {
        return isZeroInit;
    }

    public void setZeroInit(boolean zeroInit) {
        isZeroInit = zeroInit;
    }

    public Value getAddr() {
        return addr;
    }

    public void setAddr(Value addr) {
        this.addr = addr;
    }
}
