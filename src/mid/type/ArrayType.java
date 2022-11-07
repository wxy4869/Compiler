package mid.type;

public class ArrayType implements Type{
    int size;
    Type innerType;

    public ArrayType(int size, Type innerType) {
        this.size = size;
        this.innerType = innerType;
    }

    @Override
    public String toString() {
        return String.format("[%d x %s]", size, innerType);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Type getInnerType() {
        return innerType;
    }

    public void setInnerType(Type innerType) {
        this.innerType = innerType;
    }
}
