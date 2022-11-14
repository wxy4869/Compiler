package front.ASD;

import front.Token;
import utils.IOUtils;
import utils.Pair;

import java.util.ArrayList;

public class UnaryExp implements Node{
    // UnaryExp -> PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
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
            IOUtils.write(ident.toString(), "output.txt", true);
            IOUtils.write("LPARENT (\n", "output.txt", true);
            if (funcRParams != null) {
                funcRParams.printMoi();
            }
            IOUtils.write("RPARENT )\n", "output.txt", true);
        } else {
            unaryOp.printMoi();
            unaryExp.printMoi();
        }
        IOUtils.write("<UnaryExp>\n", "output.txt", true);
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

    /* UnaryExp     -> PrimaryExp
     *              | Ident '(' [FuncRParams] ')'  这种情况在计算 ConstExp 时不会出现
     *              | UnaryOp UnaryExp
     */
    public int calValue() {
        if (primaryExp != null) {
            return primaryExp.calValue();
        } else {
            if (unaryOp.getOp().getSrc().equals("+")) {
                return unaryExp.calValue();
            } else if (unaryOp.getOp().getSrc().equals("-")) {
                return -unaryExp.calValue();
            } else {
                return (unaryExp.calValue() == 0) ? 0 : 1;
            }
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