package table;

import java.util.HashMap;
import java.util.Map;

public class SymTable {
    Map<String, Symbol> symbolMap;
    SymTable parent;
    boolean isFunc;
    boolean isVoid;
    int depth;  // 用于判断是否为全局变量

    public SymTable(SymTable parent, boolean isFunc, boolean isVoid, int depth) {
        this.symbolMap = new HashMap<>();
        this.parent = parent;
        this.isFunc = isFunc;
        this.isVoid = isVoid;
        this.depth = depth;
    }

    public Symbol getSymbol(String name, boolean inParent, int lineNum) {
        if (symbolMap.containsKey(name)) {
            Symbol symbol = symbolMap.get(name);
            if (symbol.getLineNum() <= lineNum) {
                return symbol;
            }
            return parent.getSymbol(name, inParent, lineNum);
        } else if (inParent && parent != null) {
            return parent.getSymbol(name, inParent, lineNum);
        }
        return null;
    }

    public Map<String, Symbol> getSymbolMap() {
        return symbolMap;
    }

    public void setSymbolMap(Map<String, Symbol> symbolMap) {
        this.symbolMap = symbolMap;
    }

    public SymTable getParent() {
        return parent;
    }

    public void setParent(SymTable parent) {
        this.parent = parent;
    }

    public boolean isFunc() {
        return isFunc;
    }

    public void setFunc(boolean func) {
        isFunc = func;
    }

    public boolean isVoid() {
        return isVoid;
    }

    public void setVoid(boolean aVoid) {
        isVoid = aVoid;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
