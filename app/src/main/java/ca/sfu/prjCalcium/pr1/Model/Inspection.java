package ca.sfu.prjCalcium.pr1.Model;

public class Inspection {
    private String trackingNumber;
    private String inspectionDate;
    private String inspeType;
    private int numCritical;
    private int numNonCritical;
    private String hazardRating;
    private String vioLump;

    public Inspection() {
        this.trackingNumber = "";
        this.inspectionDate = "";
        this.inspeType = "";
        this.numCritical = 0;
        this.numNonCritical = 0;
        this.hazardRating = "";
        this.vioLump = "";
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
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
                "tracking_number='" + trackingNumber + '\'' +
                ", inspection_Date='" + inspectionDate + '\'' +
                ", numCritical=" + numCritical +
                ", numNonCritical=" + numNonCritical +
                ", hazardRating='" + hazardRating + '\'' +
                ", vioLump='" + vioLump + '\'' +
                '}';
    }
}
