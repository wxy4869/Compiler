package table;

import front.ASD.*;
import front.ErrHandler;
import front.Error;
import front.SynAnalyzer;
import front.Token;
import utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymGenerator {
    public static Map<Node, SymTable> table;
    public static SymTable currentTable;

    public SymGenerator() {
        table = new HashMap<>();
        currentTable = new SymTable(null, false, false, 0);
        table.put(SynAnalyzer.root, currentTable);
    }

    public void generate(Node node, boolean inLoop) {
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
                int size1 = 0, size2 = 0;
                if (dimension == 1) {
                    size1 = constDef.getConstExp().get(0).calValue();
                } else if (dimension == 2) {
                    size1 = constDef.getConstExp().get(0).calValue();
                    size2 = constDef.getConstExp().get(0).calValue();
                }
                Def def = new Def(name, true, dimension, size1, size2);
                currentTable.symbolMap.put(name, def);
                if (dimension == 0) {
                    def.setInitVal(constDef.getConstInitVal().getConstExp().calValue());
                } else if (dimension == 1) {
                    ArrayList<Integer> tmpList = def.getInitArrayVal();
                    ArrayList<ConstInitVal> initList = constDef.getConstInitVal().getConstInitVal();
                    for (int i = 0; i < size1; i++) {
                        tmpList.add(initList.get(i).getConstExp().calValue());
                    }
                } else {
                    ArrayList<Integer> tmpList = def.getInitArrayVal();
                    ArrayList<ConstInitVal> initList1 = constDef.getConstInitVal().getConstInitVal();
                    for (int i = 0; i < size1; i++) {
                        ArrayList<ConstInitVal> initList2 = initList1.get(i).getConstInitVal();
                        for (int j = 0; j < size2; j++) {
                            tmpList.add(initList2.get(j).getConstExp().calValue());
                        }
                    }
                }
            }
        } else if (node instanceof VarDef) {
            VarDef varDef = (VarDef)node;
            String name = varDef.getIdent().getSrc();
            if (currentTable.findSymbol(name, false)) {
                ErrHandler.errors.add(new Error("b", varDef.getIdent().getLineNum()));
            } else {
                int dimension = varDef.getConstExp() == null ? 0 : varDef.getConstExp().size();
                int size1 = 0, size2 = 0;
                if (dimension == 1) {
                    size1 = varDef.getConstExp().get(0).calValue();
                } else if (dimension == 2) {
                    size1 = varDef.getConstExp().get(0).calValue();
                    size2 = varDef.getConstExp().get(0).calValue();
                }
                Def def = new Def(name, false, dimension, size1, size2);
                currentTable.symbolMap.put(name, def);
                if (currentTable.getDepth() == 0) {
                    if (varDef.getInitVal() != null ) {
                        if (dimension == 0) {
                            def.setInitVal(varDef.getInitVal().getExp().calValue());
                        } else if (dimension == 1) {
                            ArrayList<Integer> tmpList = def.getInitArrayVal();
                            ArrayList<InitVal> initList = varDef.getInitVal().getInitVal();
                            for (int i = 0; i < size1; i++) {
                                tmpList.add(initList.get(i).getExp().calValue());
                            }
                        } else {
                            ArrayList<Integer> tmpList = def.getInitArrayVal();
                            ArrayList<InitVal> initList1 = varDef.getInitVal().getInitVal();
                            for (int i = 0; i < size1; i++) {
                                ArrayList<InitVal> initList2 = initList1.get(i).getInitVal();
                                for (int j = 0; j < size2; j++) {
                                    tmpList.add(initList2.get(j).getExp().calValue());
                                }
                            }
                        }
                    } else {
                        def.setZeroInit(true);
                    }
                }
            }
        // ------------------------------------------------------------------------------------------------ 函数
        } else if (node instanceof FuncDef) {
            FuncDef funcDef = (FuncDef)node;
            String name = funcDef.getIdent().getSrc();
            boolean isVoid = funcDef.getFuncType().getFuncType().getDst().equals("VOIDTK");
            if (currentTable.findSymbol(name, false)) {
                ErrHandler.errors.add(new Error("b", funcDef.getIdent().getLineNum()));
            } else {
                ArrayList<Integer> paramDimension = new ArrayList<>();
                if (funcDef.getFuncFParams() != null) {
                    ArrayList<FuncFParam> funcFParam = funcDef.getFuncFParams().getFuncFParam();
                    for (FuncFParam value : funcFParam) {
                        int dimension = value.getType();
                        paramDimension.add(dimension);
                    }
                }
                int paramNum = funcDef.getFuncFParams() == null ? 0 : funcDef.getFuncFParams().getFuncFParam().size();
                currentTable.symbolMap.put(name, new Func(name, isVoid, paramNum, paramDimension));
            }
            SymTable tmpTable = new SymTable(currentTable, true, isVoid, currentTable.depth + 1);
            currentTable = tmpTable;
            table.put(funcDef.getBlock(), currentTable);
        } else if (node instanceof MainFuncDef) {
            MainFuncDef mainFuncDef = (MainFuncDef)node;
            if (currentTable.findSymbol("main", false)) {
                ErrHandler.errors.add(new Error("b", mainFuncDef.getMain().getLineNum()));
            } else {
                currentTable.symbolMap.put("main", new Func("main", false, 0, null));
            }
            SymTable tmpTable = new SymTable(currentTable, true, false, currentTable.depth + 1);
            currentTable = tmpTable;
            table.put(mainFuncDef.getBlock(), currentTable);
        // ------------------------------------------------------------------------------------------------ 形参
        } else if (node instanceof FuncFParam) {
            FuncFParam funcFParam = (FuncFParam)node;
            String name = funcFParam.getIdent().getSrc();
            if (currentTable.findSymbol(name, false)) {
                ErrHandler.errors.add(new Error("b", funcFParam.getIdent().getLineNum()));
            } else {
                int dimension = funcFParam.getType();
                int size = 0;
                if (funcFParam.getConstExp() != null) {
                    size = funcFParam.getConstExp().calValue();
                }
                currentTable.symbolMap.put(name, new FuncParam(name, dimension, size));
            }
        // ------------------------------------------------------------------------------------------------ 使用
        } else if (node instanceof Stmt) {
            Stmt stmt = (Stmt)node;
            if (stmt.getType() == 0) {
                SymTable tmpTable = new SymTable(currentTable, false, false, currentTable.depth + 1);
                currentTable = tmpTable;
                table.put(stmt.getBlock(), currentTable);
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
                    if (tmp.charAt(i) == '%' && i + 1 < size && tmp.charAt(i + 1) == 'd') {
                        formatNum++;
                    }
                }
                if (formatNum != expNum) {
                    ErrHandler.errors.add(new Error("l", stmt.getPrintf().getLineNum()));
                }
            } else if (stmt.getType() == 2) {
                inLoop = true;
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
                boolean ys = currentTable.findSymbol(name, true);
                if (!ys) {
                    ErrHandler.errors.add(new Error("c", unaryExp.getIdent().getLineNum()));
                }
                if (ys && unaryExp.getFuncRParams() != null) {
                    Symbol symbol = currentTable.getSymbol(name, true);
                    Func func = (Func) symbol;
                    int paramNumF = func.getParamNum();
                    int paramNumR = unaryExp.getFuncRParams() == null ? 0 : unaryExp.getFuncRParams().getExp().size();
                    if (paramNumF != paramNumR) {
                        ErrHandler.errors.add(new Error("d", unaryExp.getIdent().getLineNum()));
                    }
                    ArrayList<Integer> paramDimensionF = func.getParamDimension();
                    for (int i = 0; i < paramNumF && i < paramNumR; i++) {
                        int paramDimensionR = 0;
                        Pair<Token, Integer> tmp = unaryExp.getFuncRParams().getExp().get(i).getDimension();
                        Token tmpToken = tmp.getFirst();
                        if (tmpToken != null) {
                            Symbol tmpSymbol = currentTable.getSymbol(tmpToken.getSrc(), true);
                            if (tmpSymbol != null) {
                                if (tmpSymbol instanceof Def) {
                                    paramDimensionR = ((Def)tmpSymbol).getDimension() - tmp.getSecond();
                                } else if (tmpSymbol instanceof Func) {
                                    paramDimensionR = ((Func)tmpSymbol).isVoid() ? -1 : 0;
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
        // ------------------------------------------------------------------------------------------------ 填值
        } /*else if (node instanceof PrimaryExp && ((PrimaryExp)node).getLval() != null) {
            PrimaryExp primaryExp = ((PrimaryExp)node);
            Lval lval = primaryExp.getLval();
            Symbol symbol = currentTable.getSymbol(lval.getIdent().getSrc(), true);
            if (symbol instanceof Def && ((Def) symbol).isConst()) {
                Def def = ((Def)symbol);
                if (def.getDimension() == 0) {
                    primaryExp.setLvalValue(def.getInitVal());
                } else if (def.getDimension() == 1) {
                    int index1 = lval.getExp().get(0).calValue();
                    primaryExp.setLvalValue(def.getInitArrayVal().get(index1));
                } else {
                    int index1 = lval.getExp().get(0).calValue();
                    int index2 = lval.getExp().get(1).calValue();
                    primaryExp.setLvalValue(def.getInitArrayVal().get(index1 * def.getSize1() + index2));
                }
            }
        }*/

        for (Node value : node.getChild()) {
            generate(value, inLoop);
        }

        if (node instanceof Block) {
            Block block = (Block)node;
            ArrayList<BlockItem> blockItem = block.getBlockItem();
            Stmt stmt = null;
            int size = blockItem.size();
            if (size > 0) {
                stmt = blockItem.get(size - 1).getStmt();
            }
            if (currentTable.isFunc && !currentTable.isVoid && (stmt == null || stmt.getType() != 5)) {
                ErrHandler.errors.add(new Error("g", block.getRbrace().getLineNum()));
            }
            currentTable = currentTable.parent;
        }
    }
}
