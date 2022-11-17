package mid;

import mid.type.Type;
import utils.IOUtils;

import java.util.ArrayList;

public class Function extends User{
    // define dso_local <type> <name>(<params>) { }
    Module parent;
    ArrayList<BasicBlock> basicBlocks;
    ArrayList<Argument> arguments;

    public Function(String name, Type type) {
        super(name, type);
        this.parent = Module.module;
        this.basicBlocks = new ArrayList<>();
        this.arguments = new ArrayList<>();
        this.parent.getFunctions().add(this);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(String.format("define dso_local %s %s(", type, name));
        int size = arguments.size();
        for (int i = 0; i < size; i++) {
            str.append(arguments.get(i));
            if (i != size - 1) {
                str.append(", ");
            }
        }
        str.append(") {\n");
        for (BasicBlock value : basicBlocks) {
            str.append(value);
        }
        str.append("}\n");
        return str.toString();
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public void setBasicBlocks(ArrayList<BasicBlock> basicBlocks) {
        this.basicBlocks = basicBlocks;
    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(ArrayList<Argument> arguments) {
        this.arguments = arguments;
    }
}
