package table;

import front.Token;
import mid.Value;

import java.util.ArrayList;

public class FuncParam implements Symbol {
    private Token ident;
    private String name;
    private int dimension;
    private ArrayList<Integer> size;
    private Value addr;

    public FuncParam(Token ident, String name, int dimension, ArrayList<Integer> size) {
        this.ident = ident;
        this.name = name;
        this.dimension = dimension;
        this.size = size;
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

    public Value getAddr() {
        return addr;
    }

    public void setAddr(Value addr) {
        this.addr = addr;
    }
}
