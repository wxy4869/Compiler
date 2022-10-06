package front.ASD;

import front.Token;
import utils.IOUtils;
import utils.Pair;

import java.util.ArrayList;

public class UnaryExp implements Node{
    private PrimaryExp primaryExp;
    private Token ident;
    private FuncRParams funcRParams;
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;

    public UnaryExp(PrimaryExp primaryExp, Token ident, FuncRParams funcRParams, UnaryOp unaryOp, UnaryExp unaryExp) {
        this.primaryExp = primaryExp;
        this.ident = ident;
        this.funcRParams = funcRParams;
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }

    @Override
    public void printMoi() {
        if (primaryExp != null) {
            primaryExp.printMoi();
        } else if (ident != null) {
            IOUtils.write(ident.toString());
            IOUtils.write("LPARENT (\n");
            if (funcRParams != null) {
                funcRParams.printMoi();
            }
            IOUtils.write("RPARENT )\n");
        } else {
            unaryOp.printMoi();
            unaryExp.printMoi();
        }
        IOUtils.write("<UnaryExp>\n");
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>();
        if (primaryExp != null) {
            child.add(primaryExp);
        } else if (ident != null) {
            child.add(funcRParams);
        } else {
            child.add(unaryOp);
            child.add(unaryExp);
        }
        return child;
    }

    public Pair<Token, Integer> getDimension() {
        if (primaryExp != null) {
            return primaryExp.getDimension();
        } else if (ident != null) {
            return new Pair<>(ident, null);
        } else {
            return unaryExp.getDimension();
        }
    }

    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }

    public Token getIdent() {
        return ident;
    }

    public FuncRParams getFuncRParams() {
        return funcRParams;
    }

    public UnaryOp getUnaryOp() {
        return unaryOp;
    }

    public UnaryExp getUnaryExp() {
        return unaryExp;
    }
}