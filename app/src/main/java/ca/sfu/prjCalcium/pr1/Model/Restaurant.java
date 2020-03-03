package ca.sfu.prjCalcium.pr1.Model;

public class Restaurant {
    private String tracking_number;
    private String restaurant_name;
    private String address;
    private String physical_city;
    private double latitude;
    private double longitude;

    public String getTracking_number() {
        return tracking_number;
    }

    public void setTracking_number(String tracking_number) {
        this.tracking_number = tracking_number;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhysical_city() {
        return physical_city;
    }

    public void setPhysical_city(String physical_city) {
        this.physical_city = physical_city;
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

    @Override
    public String toString() {
        return "Restaurant{" +
                "tracking_number='" + tracking_number + '\'' +
                ", restaurant_name='" + restaurant_name + '\'' +
                ", address='" + address + '\'' +
                ", physical_city='" + physical_city + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
