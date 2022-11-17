package mid;

import utils.IOUtils;

import java.util.ArrayList;

public class Module {
    public static final Module module = new Module();  // 单例
    ArrayList<GlobalVariable> globalVariables = new ArrayList<>();
    ArrayList<Function> functions = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("declare i32 @getint()\n");
        str.append("declare void @putch(i32)\n");
        str.append("declare void @putint(i32)\n");
        for (GlobalVariable value : globalVariables) {
            str.append(value);
        }
        for (Function value : functions) {
            str.append(value);
        }
        return str.toString();
    }

    public ArrayList<GlobalVariable> getGlobalVariables() {
        return globalVariables;
    }

    public void setGlobalVariables(ArrayList<GlobalVariable> globalVariables) {
        this.globalVariables = globalVariables;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(ArrayList<Function> functions) {
        this.functions = functions;
    }
}
