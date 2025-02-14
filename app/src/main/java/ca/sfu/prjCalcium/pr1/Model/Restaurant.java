package ca.sfu.prjCalcium.pr1.Model;

import android.content.Context;

/**
 * Represent a restaurant.
 */
public class Restaurant {
    private String trackingNumber;
    private String restaurantName;
    private String address;
    private String physicalCity;
    private String facType;
    private double latitude;
    private double longitude;
    private InspectionManager inspections;
    private boolean faved;

    public Restaurant() {
        this.trackingNumber = "";
        this.restaurantName = "";
        this.address = "";
        this.physicalCity = "";
        this.facType = "";
        this.latitude = 0;
        this.longitude = 0;
        this.inspections = new InspectionManager();
        this.faved = false;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhysicalCity() {
        return physicalCity;
    }

    public void setPhysicalCity(String physicalCity) {
        this.physicalCity = physicalCity;
    }

    public String getFacType() {
        return facType;
    }

    public void setFacType(String facType) {
        this.facType = facType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public InspectionManager getInspections() {
        return inspections;
    }

    public void setInspections(InspectionManager inspections) {
        this.inspections = inspections;
    }

    public void setInspections(Context context) { // Set the inspection of this restaurant by its ID by reading
        inspections.addInspectionsByTrackingNumber(context, this.getTrackingNumber());
    }

    public boolean isFaved() {
        return faved;
    }

    public void setFaved(boolean faved) {
        this.faved = faved;
    }

    public void toggleFaved() {
        this.faved = !this.isFaved();
    }

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
