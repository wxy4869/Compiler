package table;

public class Def implements Symbol {
    private String name;
    private boolean isConst;
    private int dimension;
    // private int [] size;
    // private InitVal initVal;
    // private ConstInitVal constInitVal;

    public Def(String name, boolean isConst, int dimension) {
        this.name = name;
        this.isConst = isConst;
        this.dimension = dimension;
    }

    public String getName() {
        return name;
    }

    public boolean isConst() {
        return isConst;
    }

    public int getDimension() {
        return dimension;
    }
}
