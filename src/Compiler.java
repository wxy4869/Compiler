import front.LexAnalyzer;
import front.SynAnalyzer;
import front.Token;
import utils.IOUtils;

public class Compiler {
    public static int op = 2; // 1 词法分析 2 语法分析

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
    }
}
