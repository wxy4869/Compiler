package front.ASD;

import front.Token;
import utils.IOUtils;

import java.util.ArrayList;

public class Block implements Node{
    private ArrayList<BlockItem> blockItem;
    private Token rbrace;

    public Block(ArrayList<BlockItem> blockItem, Token rbrace) {
        this.blockItem = blockItem;
        this.rbrace = rbrace;
    }

    @Override
    public void printMoi() {
        IOUtils.write("LBRACE {\n");
        for (BlockItem value : blockItem) {
            value.printMoi();
        }
        IOUtils.write("RBRACE }\n");
        IOUtils.write("<Block>\n");
    }

    @Override
    public ArrayList<Node> getChild() {
        return new ArrayList<>(blockItem);
    }

    public ArrayList<BlockItem> getBlockItem() {
        return blockItem;
    }

    public Token getRbrace() {
        return rbrace;
    }
}