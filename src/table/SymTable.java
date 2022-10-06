package table;

import java.util.HashMap;
import java.util.Map;

public class SymTable {
    Map<String, Symbol> symbolMap;
    SymTable parent;
    boolean isFunc;
    boolean isVoid;

    public SymTable(SymTable parent, boolean isFunc, boolean isVoid) {
        this.symbolMap = new HashMap<>();
        this.parent = parent;
        this.isFunc = isFunc;
        this.isVoid = isVoid;
    }

    public boolean findSymbol(String symbol, boolean inParent) {
        if (symbolMap.containsKey(symbol)) {
            return true;
        } else if (inParent && parent != null) {
            return parent.findSymbol(symbol, inParent);
        }
        return false;
    }

    public Symbol getSymbol(String symbol, boolean inParent) {
        if (symbolMap.containsKey(symbol)) {
            return symbolMap.get(symbol);
        } else if (inParent && parent != null) {
            return parent.getSymbol(symbol, inParent);
        }
        return null;
    }
}
