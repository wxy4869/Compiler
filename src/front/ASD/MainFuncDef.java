package front.ASD;

import front.Token;
import utils.IOUtils;

public class MainFuncDef implements Node{
    private Token main;
    private Block block;

    public MainFuncDef(Token main, Block block) {
        this.main = main;
        this.block = block;
    }

    @Override
    public void printMoi() {
        IOUtils.write("INTTK int\n");
        IOUtils.write("MAINTK main\n");
        IOUtils.write("LPARENT (\n");
        IOUtils.write("RPARENT )\n");
        block.printMoi();
        IOUtils.write("<MainFuncDef>\n");
    }

    public Token getMain() {
        return main;
    }

    public Block getBlock() {
        return block;
    }
}