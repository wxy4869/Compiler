package table;

import mid.Value;

public class FuncParam implements Symbol {
    private String name;
    private int dimension;
    private int size;
    private Value addr;

    public FuncParam(String name, int dimension, int size) {
        this.name = name;
        this.dimension = dimension;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getDimension() {
        return dimension;
    }

    public int getSize() {
        return size;
    }

    public Value getAddr() {
        return addr;
    }

    public void setAddr(Value addr) {
        this.addr = addr;
    }
}
