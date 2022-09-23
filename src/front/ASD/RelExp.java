package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class RelExp implements Node{
    private ArrayList<AddExp> addExp;
    private ArrayList<Token> ops;

    public RelExp(ArrayList<AddExp> addExp, ArrayList<Token> ops) {
        this.addExp = addExp;
        this.ops = ops;
    }

    @Override
    public void printMoi() {
        int size = addExp.size();
        for (int i = 0; i < size; i++) {
            addExp.get(i).printMoi();
            IOUtils.write("<RelExp>\n");
            if (i < size - 1) {
                IOUtils.write(ops.get(i).toString());
            }
        }
    }
}