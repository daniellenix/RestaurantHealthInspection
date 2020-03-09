package ca.sfu.prjCalcium.pr1.Model;

/**
 * Represent a violation in an inspection.
 */
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
        if (100 <= this.code && this.code <= 199) {
            return code + ": Regulation Violation";
        }

        if (200 <= this.code && this.code <= 299) {
            return code + ": Food Violation";
        }

        if (300 <= this.code && this.code <= 399) {
            return code + ": Equipments Violation";
        }

        if (400 <= this.code && this.code <= 499) {
            return code + ": Employees Violation";
        }

        if (500 <= this.code && this.code <= 599) {
            return code + ": Operator Violation";
        }

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
