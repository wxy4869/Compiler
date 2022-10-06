package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class ConstInitVal implements Node{
    private ConstExp constExp;
    private ArrayList<ConstInitVal> constInitVal;

    public ConstInitVal(ConstExp constExp, ArrayList<ConstInitVal> constInitVal) {
        this.constExp = constExp;
        this.constInitVal = constInitVal;
    }

    @Override
    public void printMoi() {
        if (constExp != null) {
            constExp.printMoi();
        } else {
            IOUtils.write("LBRACE {\n");
            int size = constInitVal.size();
            if (size > 0) {
                constInitVal.get(0).printMoi();
                for (int i = 1; i < size; i++) {
                    IOUtils.write("COMMA ,\n");
                    constInitVal.get(i).printMoi();
                }
            }
            IOUtils.write("RBRACE }\n");
        }
        IOUtils.write("<ConstInitVal>\n");
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>();
        if (constExp != null) {
            child.add(constExp);
        } else {
            child.addAll(constInitVal);
        }
        return child;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public ArrayList<ConstInitVal> getConstInitVal() {
        return constInitVal;
    }
}