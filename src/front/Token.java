package front;

public class Token {
    private String src;
    private String dst;
    private int intVal;
    private String strVal;
    private int lineNum;

    public Token(String src, String dst, int intVal, String strVal, int lineNum) {
        this.src = src;
        this.dst = dst;
        if (this.dst.equals("INTCON")) {
            this.intVal = intVal;
        } else if (this.dst.equals("STRCON")) {
            this.strVal = strVal;
        }
        this.lineNum = lineNum;
    }

    @Override
    public String toString() {
        return this.dst + " " + this.src + "\n";
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public int getIntVal() {
        return intVal;
    }

    public void setIntVal(int intVal) {
        this.intVal = intVal;
    }

    public String getStrVal() {
        return strVal;
    }

    public void setStrVal(String strVal) {
        this.strVal = strVal;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }
}
