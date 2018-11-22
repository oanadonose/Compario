package models;

public class Store {
    //public String username;
    public String email;
    public String phoneNo;
    public Address address;
    public Store() {
        // Default constructor required for calls to DataSnapshot.getValue(Store.class)
    }

    public Store(String email, String phoneNo, Address address) {
        //this.username = username;
        this.email = email;
        this.phoneNo = phoneNo;
        this.address = address;
    }
}
