package mid;

import utils.IOUtils;

import java.util.ArrayList;

public class Module {
    public static final Module module = new Module();  // 单例
    ArrayList<GlobalVariable> globalVariables = new ArrayList<>();
    ArrayList<Function> functions = new ArrayList<>();

    public void printMoi(String path) {
        IOUtils.write("declare i32 @getint()\n", path, false);
        IOUtils.write("declare void @putch(i32)\n", path, true);
        IOUtils.write("declare void @putint(i32)\n\n", path, true);
        for (GlobalVariable value : globalVariables) {
            value.printMoi(path);
        }
        for (Function value : functions) {
            IOUtils.write("\n", path, true);
            value.printMoi(path);
        }
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
