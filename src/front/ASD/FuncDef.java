package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class FuncDef implements Node{
    // FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block
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
        IOUtils.write(ident.toString(), "output.txt", true);
        IOUtils.write("LPARENT (\n", "output.txt", true);
        if (funcFParams != null) {
            funcFParams.printMoi();
        }
        IOUtils.write("RPARENT )\n", "output.txt", true);
        block.printMoi();
        IOUtils.write("<FuncDef>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>();
        child.add(funcType);
        if (funcFParams != null) {
            child.add(funcFParams);
        }
        child.add(block);
        return child;
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