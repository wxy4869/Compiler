package front.ASD;

import utils.IOUtils;

public class MainFuncDef implements Node{
    private Block block;

    public MainFuncDef(Block block) {
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
}