package table;

import front.Token;

import java.util.ArrayList;

public class Func implements Symbol {
    private Token ident;
    private String name;
    private boolean isVoid;
    private int paramNum;
    private ArrayList<Integer> paramDimension;

    public Func(Token ident, String name, boolean isVoid, int paramNum, ArrayList<Integer> paramDimension) {
        this.ident = ident;
        this.name = name;
        this.isVoid = isVoid;
        this.paramNum = paramNum;
        this.paramDimension = paramDimension;
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

    public boolean isVoid() {
        return isVoid;
    }

    public void setVoid(boolean aVoid) {
        isVoid = aVoid;
    }

    public int getParamNum() {
        return paramNum;
    }

    public void setParamNum(int paramNum) {
        this.paramNum = paramNum;
    }

    public ArrayList<Integer> getParamDimension() {
        return paramDimension;
    }

    public void setParamDimension(ArrayList<Integer> paramDimension) {
        this.paramDimension = paramDimension;
    }
}
