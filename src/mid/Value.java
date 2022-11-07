package mid;

import mid.type.Type;

import java.util.ArrayList;

public class Value {
    ArrayList<User> useList;  // Value 被哪些 User 使用
    String name;
    Type type;

    public Value() {

    }

    public Value(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public ArrayList<User> getUseList() {
        return useList;
    }

    public void setUseList(ArrayList<User> useList) {
        this.useList = useList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}