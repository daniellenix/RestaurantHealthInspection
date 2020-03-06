package ca.sfu.prjCalcium.pr1.Model;

public class Restaurant {
    private String trackingNumber;
    private String restaurantName;
    private String address;
    private String physicalCity;
    private String facType;
    private double latitude;
    private double longitude;
//    InspectionManager inspections;

    public Restaurant() {
        this.trackingNumber = "";
        this.restaurantName = "";
        this.address = "";
        this.physicalCity = "";
        this.facType = "";
        this.latitude = 0;
        this.longitude = 0;
//        this.inspections = null;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }
    public String getRestaurantName() {
        return restaurantName;
    }
    public String getAddress() {
        return address;
    }
    public String getPhysicalCity() {
        return physicalCity;
    }
    public String getFacType() {
        return facType;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
//    public InspectionManager getInspections() {
//        return inspections;
//    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setPhysicalCity(String physicalCity) {
        this.physicalCity = physicalCity;
    }
    public void setFacType(String facType) {
        this.facType = facType;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
//    public void setInspections(InspectionManager inspections) {
//        this.inspections = inspections;
//    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "tracking_number='" + trackingNumber + '\'' +
                ", restaurant_name='" + restaurantName + '\'' +
                ", address='" + address + '\'' +
                ", physical_city='" + physicalCity + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
