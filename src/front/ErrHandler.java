package front;

import java.util.ArrayList;

public class ErrHandler {
    public static ArrayList<Error> errors = new ArrayList<>();

    public static void HandleA(Token token) {
        String formatString = token.getSrc().substring(1, token.getSrc().length() - 1);
        int size = formatString.length();
        for (int i = 0; i < size; i++) {
            if (!(formatString.charAt(i) == '%' || formatString.charAt(i) == 32 || formatString.charAt(i) == 33
                    || (formatString.charAt(i) >= 40 && formatString.charAt(i) <= 126))) {
                errors.add(new Error("a",token.getLineNum()));
                break;
            }
            if (formatString.charAt(i) == '%' && (i + 1 >= size || formatString.charAt(i + 1) != 'd')) {
                errors.add(new Error("a",token.getLineNum()));
                break;
            }
            if (formatString.charAt(i) == '\\' && (i + 1 >= size || formatString.charAt(i + 1) != 'n')) {
                errors.add(new Error("a",token.getLineNum()));
                break;
            }
        }
    }
}
