package models;

public class Address {
    public String streetName;
    public Integer streetNo;
    public String city;
    public String country;
    public String postcode;

    public Address() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Address(String streetName, Integer streetNo, String city, String country, String postcode) {
        this.streetName = streetName;
        this.streetNo = streetNo;
        this.city = city;
        this.country = country;
        this.postcode = postcode;
    }
}
