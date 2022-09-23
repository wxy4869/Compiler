package front.ASD;

import utils.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class CompUnit implements Node{
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
        File file = new File("output.txt");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (Decl value : decl) {
            value.printMoi();
        }
        for (FuncDef value : funcDef) {
            value.printMoi();
        }
        mainFuncDef.printMoi();

        IOUtils.write("<CompUnit>\n");
    }
}