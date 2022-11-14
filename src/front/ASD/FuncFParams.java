package front.ASD;

import utils.IOUtils;

import java.util.ArrayList;

public class FuncFParams implements Node{
    // FuncFParams -> FuncFParams { ',' FuncFParam }
    private ArrayList<FuncFParam> funcFParam;

    public FuncFParams(ArrayList<FuncFParam> funcFParam) {
        this.funcFParam = funcFParam;
    }

    @Override
    public void printMoi() {
        funcFParam.get(0).printMoi();
        int size = funcFParam.size();
        for (int i = 1; i < size; i++) {
            IOUtils.write("COMMA ,\n", "output.txt", true);
            funcFParam.get(i).printMoi();
        }
        IOUtils.write("<FuncFParams>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>(funcFParam);
    }

    public ArrayList<FuncFParam> getFuncFParam() {
        return funcFParam;
    }
}