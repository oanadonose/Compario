package models;

import com.google.android.gms.maps.model.LatLng;

public class Address {
    public String streetName;
    public String streetNo;
    public String city;
    public String country;
    public String postcode;
    public Double latitude;
    public Double longitude;

    public Address() {
        // Default constructor required for calls to DataSnapshot.getValue(Address.class)
    }

    public Address(String streetName, String streetNo, String city, String country, String postcode, Double latitude, Double longitude) {
        this.streetName = streetName;
        this.streetNo = streetNo;
        this.city = city;
        this.country = country;
        this.postcode = postcode;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
