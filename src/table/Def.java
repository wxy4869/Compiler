package table;

import front.Token;
import mid.Value;

import java.util.ArrayList;

public class Def implements Symbol {
    private Token ident;
    private String name;
    private boolean isConst;
    private int dimension;
    private ArrayList<Integer> size;
    private int initVal;
    private ArrayList<Integer> initArrayVal;
    private boolean isZeroInit;
    private Value addr;

    public Def(Token ident, String name, boolean isConst, int dimension, ArrayList<Integer> size, int initVal, ArrayList<Integer> initArrayVal, boolean isZeroInit) {
        this.ident = ident;
        this.name = name;
        this.isConst = isConst;
        this.dimension = dimension;
        this.size = size;
        this.initVal = initVal;
        this.initArrayVal = initArrayVal;
        this.isZeroInit = isZeroInit;
    }

    @Override
    public int getLineNum() {
        return ident.getLineNum();
    }

    public Token getIdent() {
        return ident;
    }

    public void setIdent(Token ident) {
        this.ident = ident;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
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

    public Value getAddr() {
        return addr;
    }

    public void setAddr(Value addr) {
        this.addr = addr;
    }
}
