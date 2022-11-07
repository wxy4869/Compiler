package mid;

import mid.type.Type;

import java.util.ArrayList;

public class User extends Value{
    ArrayList<Value> operandList;  // User 使用了哪些 Value

    public User() {

    }

    public User(String name, Type type) {
        super(name, type);
    }
}
