import front.LexAnalyzer;
import front.Token;
import utils.IOUtils;

public class Compiler {
    public static int op = 1; // 1 词法分析

    public static void main(String [] args) {
        LexAnalyzer.analyze(IOUtils.read());

        if (op == 1) {
            StringBuilder s = new StringBuilder();
            for (Token token: LexAnalyzer.tokens) {
                s.append(token.toString());
            }
            IOUtils.write(s.toString());
        }
    }
}
