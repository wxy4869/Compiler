package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class MainFuncDef implements Node{
    // MainFuncDef -> 'int' 'main' '(' ')' Block
    private Token main;
    private Block block;

    public MainFuncDef(Token main, Block block) {
        this.main = main;
        this.block = block;
    }

    @Override
    public void printMoi() {
        IOUtils.write("INTTK int\n", "output.txt", true);
        IOUtils.write("MAINTK main\n", "output.txt", true);
        IOUtils.write("LPARENT (\n", "output.txt", true);
        IOUtils.write("RPARENT )\n", "output.txt", true);
        block.printMoi();
        IOUtils.write("<MainFuncDef>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>();
        child.add(block);
        return child;
    }

    public Token getMain() {
        return main;
    }

    public Block getBlock() {
        return block;
    }
}