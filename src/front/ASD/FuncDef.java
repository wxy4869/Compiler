package front.ASD;

import front.Token;
import utils.IOUtils;

public class FuncDef implements Node{
    private FuncType funcType;
    private Token ident;
    private FuncFParams funcFParams;
    private Block block;

    public FuncDef(FuncType funcType, Token ident, FuncFParams funcFParams, Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = funcFParams;
        this.block = block;
    }

    @Override
    public void printMoi() {
        funcType.printMoi();
        IOUtils.write(ident.toString());
        IOUtils.write("LPARENT (\n");
        if (funcFParams != null) {
            funcFParams.printMoi();
        }
        IOUtils.write("RPARENT )\n");
        block.printMoi();
        IOUtils.write("<FuncDef>\n");
    }

    public FuncType getFuncType() {
        return funcType;
    }

    public Token getIdent() {
        return ident;
    }

    public FuncFParams getFuncFParams() {
        return funcFParams;
    }

    public Block getBlock() {
        return block;
    }
}