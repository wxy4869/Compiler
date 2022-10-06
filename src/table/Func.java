package table;

import java.util.ArrayList;

public class Func implements Symbol {
    private String name;
    private boolean isVoid;
    private int paramNum;
    private ArrayList<Integer> paramDimension;

    public Func(String name, boolean isVoid, int paramNum, ArrayList<Integer> paramDimension) {
        this.name = name;
        this.isVoid = isVoid;
        this.paramNum = paramNum;
        this.paramDimension = paramDimension;
    }

    public String getName() {
        return name;
    }

    public boolean isVoid() {
        return isVoid;
    }

    public int getParamNum() {
        return paramNum;
    }

    public ArrayList<Integer> getParamDimension() {
        return paramDimension;
    }
}
