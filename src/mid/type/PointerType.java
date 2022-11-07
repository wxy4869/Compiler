package mid.type;

public class PointerType implements Type{
    Type innerType;

    public PointerType(Type innerType) {
        this.innerType = innerType;
    }

    @Override
    public String toString() {
        return String.format("%s*", innerType);
    }

    public Type getInnerType() {
        return innerType;
    }

    public void setInnerType(Type innerType) {
        this.innerType = innerType;
    }
}
