package models;

public class Store {
    public String username;
    public String name;
    public String email;
    public Integer phoneNo;
    public Address address;
    public Store() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Store(String username, String name, String email, Integer phoneNo, Address address) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.address = address;
    }
}
