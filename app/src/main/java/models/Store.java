package models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Store {
    //public String username;
    public String name;
    public String email;
    public String phone;
    public Address addressFull;
    public Address address;
    public Store() {
        // Default constructor required for calls to DataSnapshot.getValue(Store.class)
    }

    public Store(String name, String email, String phone, Address addressFull,Address address) {
        //this.username = username;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.addressFull = addressFull;
        this.address = address;
    }
    public Store(String name, String email, String phone, Address address){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
    // [START store_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("phone", phone);
        result.put("address", address);
        return result;
    }
    // [END post_to_map]

}
