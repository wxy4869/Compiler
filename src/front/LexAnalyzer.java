package front;

import java.util.ArrayList;
import java.util.HashMap;

public class LexAnalyzer {
    public static ArrayList<Token> tokens = new ArrayList<>();
    public static HashMap<String, String> table = new HashMap<>();

    public void analyze(String s) {
        init();

        int lineNum = 1;
        for (int i = 0; s.charAt(i) != '\0'; i++) {
            char c = s.charAt(i), d = s.charAt(i + 1);
            if (c == ' ') {
                continue;
            }

            if (c == '\n') {
                lineNum += 1;
            } else if (Character.isLetter(c) || c == '_') {
                int j = i;
                while (s.charAt(j) != '\0' && (Character.isLetter(s.charAt(j)) || s.charAt(j) == '_' || Character.isDigit(s.charAt(j)))) {
                    j++;
                }
                String substr = s.substring(i, j);
                tokens.add(new Token(substr, table.getOrDefault(substr, "IDENFR"), 0, null, lineNum));
                i = j - 1;
            } else if (Character.isDigit(c)) {
                int j = i;
                while (s.charAt(j) != '\0' && Character.isDigit(s.charAt(j))) {
                    j++;
                }
                String substr = s.substring(i, j);
                tokens.add(new Token(substr, "INTCON", Integer.parseInt(substr), null, lineNum));
                i = j - 1;
            } else if (c == '"') {
                int j = i + 1;
                while (s.charAt(j) != '"') {
                    j++;
                }
                String substr = s.substring(i, j + 1);
                Token token = new Token(substr, "STRCON", 0, substr, lineNum);
                tokens.add(token);
                ErrHandler.HandleA(token);
                i = j;
            } else if (c == '!') {
                if (d == '=') {
                    tokens.add(new Token("!=", "NEQ", 0, null, lineNum));
                    i++;
                } else {
                    tokens.add(new Token("!", "NOT", 0, null, lineNum));
                }
            } else if (c == '&' && d == '&') {
                tokens.add(new Token("&&", "AND", 0, null, lineNum));
            } else if (c == '|' && d == '|') {
                tokens.add(new Token("||", "OR", 0, null, lineNum));
            } else if (c == '+') {
                tokens.add(new Token("+", "PLUS", 0, null, lineNum));
            } else if (c == '-') {
                tokens.add(new Token("-", "MINU", 0, null, lineNum));
            } else if (c == '*') {
                tokens.add(new Token("*", "MULT", 0, null, lineNum));
            } else if (c == '/') {
                if (d == '/') {  // 注释
                    int j = i + 1;
                    while (s.charAt(j) != '\0' && s.charAt(j) != '\n') {
                        j++;
                    }
                    i = j;
                    lineNum++;
                } else if (d == '*') {  // 注释
                    int j = i + 2;
                    while (s.charAt(j) != '\0' && !(s.charAt(j) == '*' && s.charAt(j + 1) == '/')) {
                        if (s.charAt(j) == '\n') {
                            lineNum++;
                        }
                        j++;
                    }
                    i = j + 1;
                } else {
                    tokens.add(new Token("/", "DIV", 0, null, lineNum));
                }
            } else if (c == '%') {
                tokens.add(new Token("%", "MOD", 0, null, lineNum));
            }  else if (c == '<') {
                if (d == '=') {
                    tokens.add(new Token("<=", "LEQ", 0, null, lineNum));
                    i++;
                } else {
                    tokens.add(new Token("<", "LSS", 0, null, lineNum));
                }
            } else if (c == '>') {
                if (d == '=') {
                    tokens.add(new Token(">=", "GEQ", 0, null, lineNum));
                    i++;
                } else {
                    tokens.add(new Token(">", "GRE", 0, null, lineNum));
                }
            } else if (c == '=') {
                if (d == '=') {
                    tokens.add(new Token("==", "EQL", 0, null, lineNum));
                    i++;
                } else {
                    tokens.add(new Token("=", "ASSIGN", 0, null, lineNum));
                }
            } else if (c == ';') {
                tokens.add(new Token(";", "SEMICN", 0, null, lineNum));
            } else if (c == ',') {
                tokens.add(new Token(",", "COMMA", 0, null, lineNum));
            } else if (c == '(') {
                tokens.add(new Token("(", "LPARENT", 0, null, lineNum));
            } else if (c == ')') {
                tokens.add(new Token(")", "RPARENT", 0, null, lineNum));
            } else if (c == '[') {
                tokens.add(new Token("[", "LBRACK", 0, null, lineNum));
            } else if (c == ']') {
                tokens.add(new Token("]", "RBRACK", 0, null, lineNum));
            } else if (c == '{') {
                tokens.add(new Token("{", "LBRACE", 0, null, lineNum));
            } else if (c == '}') {
                tokens.add(new Token("}", "RBRACE", 0, null, lineNum));
            }
        }
    }

    private static void init() {
        table.put("main", "MAINTK");
        table.put("const", "CONSTTK");
        table.put("int", "INTTK");
        table.put("break", "BREAKTK");
        table.put("continue", "CONTINUETK");
        table.put("if", "IFTK");
        table.put("else", "ELSETK");
        table.put("while", "WHILETK");
        table.put("getint", "GETINTTK");
        table.put("printf", "PRINTFTK");
        table.put("return", "RETURNTK");
        table.put("void", "VOIDTK");
    }
}
