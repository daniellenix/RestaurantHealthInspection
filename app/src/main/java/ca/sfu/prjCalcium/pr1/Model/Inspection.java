package ca.sfu.prjCalcium.pr1.Model;

public class Inspection {
    private String tracking_number;
    private String inspection_Date;
    private String inspeType;
    private int numCritical;
    private int numNonCritical;
    private String hazardRating;
    private String vioLump;

    public String getTracking_number() {
        return tracking_number;
    }

    public void setTracking_number(String tracking_number) {
        this.tracking_number = tracking_number;
    }

    public String getInspection_Date() {
        return inspection_Date;
    }

    public void setInspection_Date(String inspection_Date) {
        this.inspection_Date = inspection_Date;
    }

    public String getInspeType() {
        return inspeType;
    }

    public void setInspeType(String inspeType) {
        this.inspeType = inspeType;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public void setNumCritical(int numCritical) {
        this.numCritical = numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public void setNumNonCritical(int numNonCritical) {
        this.numNonCritical = numNonCritical;
    }

    public String getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(String hazardRating) {
        this.hazardRating = hazardRating;
    }

    public String getVioLump() {
        return vioLump;
    }

    public void setVioLump(String vioLump) {
        this.vioLump = vioLump;
    }

    @Override
    public String toString() {
        return "Inspection{" +
                "tracking_number='" + tracking_number + '\'' +
                ", inspection_Date='" + inspection_Date + '\'' +
                ", numCritical=" + numCritical +
                ", numNonCritical=" + numNonCritical +
                ", hazardRating='" + hazardRating + '\'' +
                ", vioLump='" + vioLump + '\'' +
                '}';
    }
}
