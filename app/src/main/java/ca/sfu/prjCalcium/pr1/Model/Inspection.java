package ca.sfu.prjCalcium.pr1.Model;

import java.util.Date;

/**
 * Represent an inspection event.
 */
public class Inspection {
    private String trackingNumber;
    private Date inspectionDate;
    private String inspectionType;
    private int numCritical;
    private int numNonCritical;
    private String hazardRating;
    private ViolationManager vioLump;

    public Inspection() {
        this.trackingNumber = "";
        this.inspectionDate = null;
        this.inspectionType = "";
        this.numCritical = 0;
        this.numNonCritical = 0;
        this.hazardRating = "";
        this.vioLump = new ViolationManager();
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Date getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(Date inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
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

    public ViolationManager getVioLump() {
        return vioLump;
    }

    public void setVioLump(ViolationManager vioLump) {
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
