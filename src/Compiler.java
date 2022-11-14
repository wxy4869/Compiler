import front.*;
import front.ASD.CompUnit;
import mid.MidGenerator;
import mid.Module;
import table.SymGenerator;
import utils.IOUtils;

import java.lang.Error;
import java.util.Collections;

public class Compiler {
    public static int op = 4; // 1 词法分析 2 语法分析 3 错误处理 4 中间代码

    public static void main(String [] args) {
        LexAnalyzer lexAnalyzer = new LexAnalyzer();
        lexAnalyzer.analyze(IOUtils.read());
        if (op == 1) {
            StringBuilder s = new StringBuilder();
            for (Token token: LexAnalyzer.tokens) {
                s.append(token.toString());
            }
            IOUtils.write(s.toString(), "output.txt", false);
        }

        SynAnalyzer synAnalyzer = new SynAnalyzer();
        synAnalyzer.analyze();
        if (op == 2) {
            SynAnalyzer.root.printMoi();
        }

        SymGenerator symGenerator = new SymGenerator();
        symGenerator.generate(SynAnalyzer.root, false);
        if (ErrHandler.errors.size() != 0 && op != 3) {
            throw new Error("error in testfile");
        }
        if (op == 3) {
            StringBuilder s = new StringBuilder();
            Collections.sort(ErrHandler.errors);
            for (front.Error error: ErrHandler.errors) {
                s.append(error.toString());
            }
            IOUtils.write(s.toString(), "error.txt", false);
        }

        MidGenerator midGenerator = new MidGenerator();
        midGenerator.generate(SynAnalyzer.root);
        if (op == 4) {
            Module.module.printMoi("llvm_ir.txt");
        }
    }
}
