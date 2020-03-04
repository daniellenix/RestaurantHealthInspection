package ca.sfu.prjCalcium.pr1.Model;

public class Violation {
    private int code;
    private String critical;
    private String details;
    private String repeat;

    public Violation(int code, String critical, String details, String repeat) {
        this.code = code;
        this.critical = critical;
        this.details = details;
        this.repeat = repeat;
    }

    public int getCode() {
        return code;
    }

    public String isCritical() {
        return critical;
    }

    public String getDetails() {
        return details;
    }

    public String isRepeat() {
        return repeat;
    }

    @Override
    public String toString() {
        return "" +
                code + "," +
                critical + "," +
                details + "," +
                repeat
                ;
    }
}
