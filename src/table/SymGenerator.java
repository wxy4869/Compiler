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
            int lineNum = constDef.getIdent().getLineNum();
            if (currentTable.getSymbol(name, false, lineNum) != null) {
                ErrHandler.errors.add(new Error("b", constDef.getIdent().getLineNum()));
            } else {
                int dimension = constDef.getConstExp().size();
                ArrayList<Integer> size = new ArrayList<>();
                int initVal = 0;
                ArrayList<Integer> initArrayVal = new ArrayList<>();
                for (ConstExp value : constDef.getConstExp()) {
                    size.add(value.calValue());
                }
                if (dimension == 0) {
                    initVal = constDef.getConstInitVal().getConstExp().calValue();
                } else {
                    constArrayInit(initArrayVal, constDef.getConstInitVal());
                }
                currentTable.symbolMap.put(name, new Def(constDef.getIdent(), name, true, dimension, size, initVal, initArrayVal, false));
            }
        } else if (node instanceof VarDef) {
            VarDef varDef = (VarDef)node;
            String name = varDef.getIdent().getSrc();
            int lineNum = varDef.getIdent().getLineNum();
            if (currentTable.getSymbol(name, false, lineNum) != null) {
                ErrHandler.errors.add(new Error("b", varDef.getIdent().getLineNum()));
            } else {
                int dimension = varDef.getConstExp().size();
                ArrayList<Integer> size = new ArrayList<>();
                int initVal = 0;
                ArrayList<Integer> initArrayVal = new ArrayList<>();
                boolean isZeroInit = false;
                for (ConstExp value : varDef.getConstExp()) {
                    size.add(value.calValue());
                }
                if (currentTable.getDepth() == 0) {
                    if (varDef.getInitVal() != null) {
                        if (dimension == 0) {
                            initVal = varDef.getInitVal().getExp().calValue();
                        } else {
                            arrayInit(initArrayVal, varDef.getInitVal());
                        }
                    } else {
                        isZeroInit = true;
                    }
                }
                currentTable.symbolMap.put(name, new Def(varDef.getIdent(), name, false, dimension, size, initVal, initArrayVal, isZeroInit));
            }
        // ------------------------------------------------------------------------------------------------ 函数
        } else if (node instanceof FuncDef) {
            FuncDef funcDef = (FuncDef)node;
            String name = funcDef.getIdent().getSrc();
            int lineNum = funcDef.getIdent().getLineNum();
            boolean isVoid = funcDef.getFuncType().getFuncType().getDst().equals("VOIDTK");
            if (currentTable.getSymbol(name, false, lineNum) != null) {
                ErrHandler.errors.add(new Error("b", funcDef.getIdent().getLineNum()));
            } else {
                ArrayList<Integer> paramDimension = new ArrayList<>();
                if (funcDef.getFuncFParams() != null) {
                    ArrayList<FuncFParam> funcFParam = funcDef.getFuncFParams().getFuncFParam();
                    for (FuncFParam value : funcFParam) {
                        int dimension = (!value.isPointer()) ? 0 : value.getConstExp().size() + 1;
                        paramDimension.add(dimension);
                    }
                }
                int paramNum = funcDef.getFuncFParams() == null ? 0 : funcDef.getFuncFParams().getFuncFParam().size();
                currentTable.symbolMap.put(name, new Func(funcDef.getIdent(), name, isVoid, paramNum, paramDimension));
            }
            currentTable = new SymTable(currentTable, true, isVoid, currentTable.depth + 1);
            table.put(funcDef.getBlock(), currentTable);
        } else if (node instanceof MainFuncDef) {
            MainFuncDef mainFuncDef = (MainFuncDef)node;
            if (currentTable.getSymbol("main", false, mainFuncDef.getMain().getLineNum()) != null) {
                ErrHandler.errors.add(new Error("b", mainFuncDef.getMain().getLineNum()));
            } else {
                currentTable.symbolMap.put("main", new Func(mainFuncDef.getMain(), "main", false, 0, null));
            }
            currentTable = new SymTable(currentTable, true, false, currentTable.depth + 1);
            table.put(mainFuncDef.getBlock(), currentTable);
        // ------------------------------------------------------------------------------------------------ 形参
        } else if (node instanceof FuncFParam) {
            FuncFParam funcFParam = (FuncFParam)node;
            String name = funcFParam.getIdent().getSrc();
            int lineNum = funcFParam.getIdent().getLineNum();
            if (currentTable.getSymbol(name, false, lineNum) != null) {
                ErrHandler.errors.add(new Error("b", funcFParam.getIdent().getLineNum()));
            } else {
                int dimension = (!funcFParam.isPointer()) ? 0 : funcFParam.getConstExp().size() + 1;
                ArrayList<Integer> size = new ArrayList<>();
                size.add(0);  // 形参的第一个 [ ] 是空的
                for (ConstExp value : funcFParam.getConstExp()) {
                    size.add(value.calValue());
                }
                currentTable.symbolMap.put(name, new FuncParam(funcFParam.getIdent(), name, dimension, size));
            }
        // ------------------------------------------------------------------------------------------------ 使用
        } else if (node instanceof Stmt) {
            Stmt stmt = (Stmt)node;
            if (stmt.getType() == 0) {
                currentTable = new SymTable(currentTable, false, false, currentTable.depth + 1);
                table.put(stmt.getBlock(), currentTable);
            } else if (stmt.getType() == 5) {
                if (currentTable.isFuncVoid() && stmt.getExp() != null) {
                    ErrHandler.errors.add(new Error("f", stmt.getReturnTK().getLineNum()));
                }
            } else if (stmt.getType() == 7 || stmt.getType() == 8) {
                String name = stmt.getLval().getIdent().getSrc();
                int lineNum = stmt.getLval().getIdent().getLineNum();
                Symbol symbol = currentTable.getSymbol(name, true, lineNum);
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
            int lineNum = lval.getIdent().getLineNum();
            if (currentTable.getSymbol(name, true, lineNum) == null) {
                ErrHandler.errors.add(new Error("c", lval.getIdent().getLineNum()));
            }
        } else if (node instanceof UnaryExp) {
            UnaryExp unaryExp = (UnaryExp)node;
            if (unaryExp.getIdent() != null) {
                String name = unaryExp.getIdent().getSrc();
                int lineNum = unaryExp.getIdent().getLineNum();
                Symbol symbol = currentTable.getSymbol(name, true, lineNum);
                if (symbol == null) {
                    ErrHandler.errors.add(new Error("c", unaryExp.getIdent().getLineNum()));
                }
                if (symbol != null && unaryExp.getIdent() != null) {
                    Func func = (Func) symbol;
                    int paramNumF = func.getParamNum();  // 形参
                    int paramNumR = unaryExp.getFuncRParams() == null ? 0 : unaryExp.getFuncRParams().getExp().size();  // 实参
                    if (paramNumF != paramNumR) {
                        ErrHandler.errors.add(new Error("d", unaryExp.getIdent().getLineNum()));
                    }
                    ArrayList<Integer> paramDimensionF = func.getParamDimension();
                    for (int i = 0; i < paramNumF && i < paramNumR; i++) {
                        int paramDimensionR = 0;
                        Pair<Token, Integer> tmp = unaryExp.getFuncRParams().getExp().get(i).getDimension();
                        Token tmpToken = tmp.getFirst();
                        if (tmpToken != null) {
                            Symbol tmpSymbol = currentTable.getSymbol(tmpToken.getSrc(), true, tmpToken.getLineNum());
                            if (tmpSymbol != null) {
                                if (tmpSymbol instanceof Def) {
                                    paramDimensionR = ((Def)tmpSymbol).getDimension() - tmp.getSecond();
                                } else if (tmpSymbol instanceof Func) {
                                    paramDimensionR = ((Func)tmpSymbol).isVoid() ? -1 : 0;
                                } else {
                                    paramDimensionR = ((FuncParam)tmpSymbol).getDimension() - tmp.getSecond();
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

    public void constArrayInit(ArrayList<Integer> initArrayVal, ConstInitVal constInitVal) {
        if (constInitVal.getConstExp() != null) {
            initArrayVal.add(constInitVal.getConstExp().calValue());
        } else {
            for (ConstInitVal value : constInitVal.getConstInitVal()) {
                constArrayInit(initArrayVal, value);
            }
        }
    }

    public void arrayInit(ArrayList<Integer> initArrayVal, InitVal initVal) {
        if (initVal.getExp() != null) {
            initArrayVal.add(initVal.getExp().calValue());
        } else {
            for (InitVal value : initVal.getInitVal()) {
                arrayInit(initArrayVal, value);
            }
        }
    }
}
