import front.ErrHandler;
import front.LexAnalyzer;
import front.SynAnalyzer;
import front.Token;
import table.SymGenerator;
import utils.IOUtils;

import java.util.Collections;

public class Compiler {
    public static int op = 3; // 1 词法分析 2 语法分析 3 错误处理

    public static void main(String [] args) {
        LexAnalyzer.analyze(IOUtils.read());

        if (op == 1) {
            StringBuilder s = new StringBuilder();
            for (Token token: LexAnalyzer.tokens) {
                s.append(token.toString());
            }
            IOUtils.write(s.toString());
        }

        SynAnalyzer synAnalyzer = new SynAnalyzer();
        synAnalyzer.analyze();
        if (op == 2) {
            SynAnalyzer.root.printMoi();
        }

        SymGenerator symGenerator = new SymGenerator();
        symGenerator.generate(SynAnalyzer.root, false);
        if (op == 3) {
            StringBuilder s = new StringBuilder();
            Collections.sort(ErrHandler.errors);
            for (front.Error error: ErrHandler.errors) {
                s.append(error.toString());
            }
            IOUtils.write(s.toString());
        }
    }
}
