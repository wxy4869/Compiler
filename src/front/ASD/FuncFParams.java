package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class FuncFParams implements Node{
    private ArrayList<FuncFParam> funcFParam;

    public FuncFParams(ArrayList<FuncFParam> funcFParam) {
        this.funcFParam = funcFParam;
    }

    @Override
    public void printMoi() {
        funcFParam.get(0).printMoi();
        int size = funcFParam.size();
        for (int i = 1; i < size; i++) {
            IOUtils.write("COMMA ,\n");
            funcFParam.get(i).printMoi();
        }
        IOUtils.write("<FuncFParams>\n");
    }
}