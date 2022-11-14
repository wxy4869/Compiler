package front.ASD;

import utils.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class CompUnit implements Node{
    // CompUnit -> {Decl} {FuncDef} MainFuncDef
    private ArrayList<Decl> decl;
    private ArrayList<FuncDef> funcDef;
    private MainFuncDef mainFuncDef;

    public CompUnit(ArrayList<Decl> decl, ArrayList<FuncDef> funcDef, MainFuncDef mainFuncDef) {
        this.decl = decl;
        this.funcDef = funcDef;
        this.mainFuncDef = mainFuncDef;
    }

    @Override
    public void printMoi() {
        IOUtils.write("", "output.txt", false);
        for (Decl value : decl) {
            value.printMoi();
        }
        for (FuncDef value : funcDef) {
            value.printMoi();
        }
        mainFuncDef.printMoi();
        IOUtils.write("<CompUnit>\n", "output.txt", true);
    }

    @Override
    public ArrayList<Node> getChild() {
        ArrayList<Node> child = new ArrayList<>();
        child.addAll(decl);
        child.addAll(funcDef);
        child.add(mainFuncDef);
        return child;
    }

    public ArrayList<Decl> getDecl() {
        return decl;
    }

    public ArrayList<FuncDef> getFuncDef() {
        return funcDef;
    }

    public MainFuncDef getMainFuncDef() {
        return mainFuncDef;
    }
}