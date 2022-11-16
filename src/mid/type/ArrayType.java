package mid.type;

import mid.Value;

import java.util.ArrayList;

public class ArrayType implements Type{
    int size;
    Type innerType;
    int capacity;

    public ArrayType(int size, Type innerType, int capacity) {
        this.size = size;
        this.innerType = innerType;
        this.capacity = capacity;
    }

    public ArrayList<Value> offset2index(int offset) {
        ArrayList<Value> indexs = new ArrayList<>();
        Type typeNow = this;
        int capacityNow;
        while (typeNow instanceof ArrayType) {
            capacityNow = ((ArrayType)typeNow).getCapacity();
            indexs.add(new Value(Integer.toString(offset / capacityNow), new BaseType(BaseType.Tag.I32)));
            offset %= capacityNow;
            typeNow = ((ArrayType)typeNow).getInnerType();
        }
        indexs.add(new Value(Integer.toString(offset), new BaseType(BaseType.Tag.I32)));
        return indexs;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
