package table;

import front.ASD.*;
import front.ErrHandler;
import front.Error;
import front.Token;
import javafx.util.Pair;

import java.util.ArrayList;

public class SymGenerator {
    public static SymTable table;
    private SymTable currentTable;

    public SymGenerator() {
        table = new SymTable(null, false, false);
        currentTable = table;
    }

    public void generate(Node node, boolean inLoop) {
        boolean inLoopTmp = false;
        if (node == null) {
            return;
        }

        // ------------------------------------------------------------------------------------------------ 变量
        if (node instanceof ConstDef) {
            ConstDef constDef = (ConstDef)node;
            String name = constDef.getIdent().getSrc();
            if (currentTable.findSymbol(name, false)) {
                ErrHandler.errors.add(new Error("b", constDef.getIdent().getLineNum()));
            } else {
                int dimension = constDef.getConstExp() == null ? 0 : constDef.getConstExp().size();
                currentTable.symbolMap.put(name, new Def(name, true, dimension));
            }
        } else if (node instanceof VarDef) {
            VarDef varDef = (VarDef)node;
            String name = varDef.getIdent().getSrc();
            if (currentTable.findSymbol(name, false)) {
                ErrHandler.errors.add(new Error("b", varDef.getIdent().getLineNum()));
            } else {
                int dimension = varDef.getConstExp() == null ? 0 : varDef.getConstExp().size();
                currentTable.symbolMap.put(name, new Def(name, false, dimension));
            }
        // ------------------------------------------------------------------------------------------------ 函数
        } else if (node instanceof FuncDef) {
            FuncDef funcDef = (FuncDef)node;
            String name = funcDef.getIdent().getSrc();
            boolean isVoid = funcDef.getFuncType().getFuncType().getDst().equals("VOIDTK");
            if (currentTable.findSymbol(name, false)) {
                ErrHandler.errors.add(new Error("b", funcDef.getIdent().getLineNum()));
            } else {
                ArrayList<FuncFParam> funcFParam = funcDef.getFuncFParams().getFuncFParam();
                ArrayList<Integer> paramDimension = new ArrayList<>();
                for (FuncFParam value : funcFParam) {
                    int dimension = value.getType() == 1 ? 1 : 0;
                    paramDimension.add(dimension);
                }
                int paramNum = funcDef.getFuncFParams() == null ? 0 : funcDef.getFuncFParams().getFuncFParam().size();
                currentTable.symbolMap.put(name, new Func(name, isVoid, paramNum, paramDimension));
            }
            SymTable tmpTable = new SymTable(currentTable, true, isVoid);
            currentTable = tmpTable;
        } else if (node instanceof MainFuncDef) {
            MainFuncDef mainFuncDef = (MainFuncDef)node;
            if (currentTable.findSymbol("main", false)) {
                ErrHandler.errors.add(new Error("b", mainFuncDef.getMain().getLineNum()));
            } else {
                currentTable.symbolMap.put("main", new Func("main", false, 0, null));
            }
            SymTable tmpTable = new SymTable(currentTable, true, false);
            currentTable = tmpTable;
        // ------------------------------------------------------------------------------------------------ 形参
        } else if (node instanceof FuncFParam) {
            FuncFParam funcFParam = (FuncFParam)node;
            String name = funcFParam.getIdent().getSrc();
            if (currentTable.findSymbol(name, false)) {
                ErrHandler.errors.add(new Error("b", funcFParam.getIdent().getLineNum()));
            } else {
                int dimension = funcFParam.getType() == 1 ? 1 : 0;
                currentTable.symbolMap.put(name, new FuncParam(name, dimension));
            }
        // ------------------------------------------------------------------------------------------------ 使用
        } else if (node instanceof Stmt) {
            Stmt stmt = (Stmt)node;
            if (stmt.getType() == 0) {
                SymTable tmpTable = new SymTable(currentTable, false, false);
                currentTable = tmpTable;
            } else if (stmt.getType() == 5) {
                if (currentTable.isFunc && currentTable.isVoid && stmt.getExp() != null) {
                    ErrHandler.errors.add(new Error("f", stmt.getReturnTK().getLineNum()));
                }
            } else if (stmt.getType() == 7 || stmt.getType() == 8) {
                String name = stmt.getLval().getIdent().getSrc();
                Symbol symbol = currentTable.getSymbol(name, true);
                if (symbol instanceof Def && ((Def)symbol).isConst()) {
                    ErrHandler.errors.add(new Error("h", stmt.getLval().getIdent().getLineNum()));
                }
            } else if (stmt.getType() == 6) {
                int formatNum = 0;
                int expNum = stmt.getExps().size();
                String tmp = stmt.getFormatString().getSrc();
                int size = tmp.length();
                for (int i = 0; i < size; i++) {
                    if (tmp.charAt(i) == '%') {
                        formatNum++;
                    }
                }
                if (formatNum != expNum) {
                    ErrHandler.errors.add(new Error("l", stmt.getPrintf().getLineNum()));
                }
            } else if (stmt.getType() == 2) {
                inLoopTmp = true;
            } else if ((stmt.getType() == 3 || stmt.getType() == 4) && !inLoop) {
                int lineNum = stmt.getBreakTK() != null ? stmt.getBreakTK().getLineNum() : stmt.getContinueTK().getLineNum();
                ErrHandler.errors.add(new Error("m", lineNum));
            }
        } else if (node instanceof Lval) {
            Lval lval = (Lval)node;
            String name = lval.getIdent().getSrc();
            if (!currentTable.findSymbol(name, true)) {
                ErrHandler.errors.add(new Error("c", lval.getIdent().getLineNum()));
            }
        } else if (node instanceof UnaryExp) {
            UnaryExp unaryExp = (UnaryExp)node;
            if (unaryExp.getIdent() != null) {
                String name = unaryExp.getIdent().getSrc();
                if (!currentTable.findSymbol(name, true)) {
                    ErrHandler.errors.add(new Error("c", unaryExp.getIdent().getLineNum()));
                }
                if (unaryExp.getFuncRParams() != null) {
                    Symbol symbol = currentTable.getSymbol(name, true);
                    Func func = (Func) symbol;
                    int paramNumF = func.getParamNum();
                    int paramNumR = unaryExp.getFuncRParams() == null ? 0 : unaryExp.getFuncRParams().getExp().size();
                    if (paramNumF != paramNumR) {
                        ErrHandler.errors.add(new Error("d", unaryExp.getIdent().getLineNum()));
                    }
                    ArrayList<Integer> paramDimensionF = func.getParamDimension();
                    int size = unaryExp.getFuncRParams().getExp().size();
                    for (int i = 0; i < size; i++) {
                        int paramDimensionR = 0;
                        Pair<Token, Integer> tmp = unaryExp.getFuncRParams().getExp().get(i).getDimension();
                        Token tmpToken = tmp.getKey();
                        if (tmpToken != null) {
                            Symbol tmpSymbol = currentTable.getSymbol(tmpToken.getSrc(), true);
                            if (tmpSymbol != null) {
                                if (tmpSymbol instanceof Def) {
                                    paramDimensionR = ((Def) tmpSymbol).getDimension() - tmp.getValue();
                                }
                            }
                        }
                        if (paramDimensionF.get(i) != paramDimensionR) {
                            ErrHandler.errors.add(new Error("e", unaryExp.getIdent().getLineNum()));
                            break;
                        }
                    }
                }
            }
        }

        for (Node value : node.getChild()) {
            generate(value, inLoopTmp);
        }

        if (node instanceof Block) {
            Block block = (Block)node;
            ArrayList<BlockItem> blockItem = block.getBlockItem();
            Stmt stmt = blockItem.get(blockItem.size() - 1).getStmt();
            if (stmt == null || stmt.getType() != 5) {
                ErrHandler.errors.add(new Error("g", block.getRbrace().getLineNum()));
            }
            currentTable = currentTable.parent;
        }
    }
}
