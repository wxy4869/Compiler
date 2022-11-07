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

    public void printMoi(String path) {
        IOUtils.write(String.format("define dso_local %s %s(", type, name), path, true);
        int size = arguments.size();
        if (size != 0) {
            IOUtils.write(arguments.get(0).toString(), path, true);
            for (int i = 1; i < size; i++) {
                IOUtils.write(", ", path, true);
                IOUtils.write(arguments.get(i).toString(), path, true);
            }
        }
        IOUtils.write(") {\n", path, true);
        for (BasicBlock value : basicBlocks) {
            value.printMoi(path);
        }
        IOUtils.write("}\n", path, true);
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
