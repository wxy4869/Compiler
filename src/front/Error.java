package front;

public class Error implements Comparable<Error> {
    private String errorType;
    private int lineNum;

    public Error(String errorType, int lineNum) {
        this.errorType = errorType;
        this.lineNum = lineNum;
    }

    @Override
    public String toString() {
        return this.lineNum + " " + this.errorType + "\n";
    }

    @Override
    public int compareTo(Error o) {
        return (this.getLineNum() < o.getLineNum()) ? -1 : 1;
    }

    public String getErrorType() {
        return errorType;
    }

    public int getLineNum() {
        return lineNum;
    }
}
