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

    public String getCritical() {
        return critical;
    }

    public String getDetails() {
        return details;
    }

    public String getRepeat() {
        return repeat;
    }

    //For Brief Information of Violation
    public String convertDetailsToCategories() {
        // code 100s = Regulations
        // code 200s = Food
        // code 300s = Equipments
        // code 400s = Employees
        // code 500s = Operator

        return "";
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
