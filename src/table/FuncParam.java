package table;

public class FuncParam implements Symbol {
    private String name;
    private int dimension;
    // private int [] size;

    public FuncParam(String name, int dimension) {
        this.name = name;
        this.dimension = dimension;
    }

    public String getName() {
        return name;
    }

    public int getDimension() {
        return dimension;
    }
}
