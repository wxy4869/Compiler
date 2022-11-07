package mid;

import mid.type.Type;

public class Argument extends Value{
    Function parent;

    public Argument(String name, Type type, Function parent) {
        super(name, type);
        this.parent = parent;
        this.parent.getArguments().add(this);
    }

    @Override
    public String toString() {
        return String.format("%s", type);
    }

    public Function getParent() {
        return parent;
    }

    public void setParent(Function parent) {
        this.parent = parent;
    }
}
