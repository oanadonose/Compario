package models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Offer {
    public String storeName;
    public String offerTitle;
    public String price;
    public String category;
    public String city;
    public String country;

    public Offer() {
        // Default constructor required for calls to DataSnapshot.getValue(Offer.class)
    }

    public Offer(String storeName, String offerTitle, String price, String category, String city, String country) {
        this.storeName = storeName;
        this.offerTitle = offerTitle;
        this.price = price;
        this.category = category;
        this.city = city;
        this.country = country;
    }
    public Offer(String storeName, String offerTitle, String price, String category) {
        this.storeName = storeName;
        this.offerTitle = offerTitle;
        this.price = price;
        this.category = category;
    }
    public String getStoreName(){
        return storeName;
    }
    public String getOfferTitle(){
        return offerTitle;
    }
    public String getPrice(){
        return price;
    }
    public String getCategory(){
        return category;
    }
    // [START offer_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("store_name", storeName);
        result.put("offer_title", offerTitle);
        result.put("price", price);
        result.put("category", category);
        return result;
    }
    // [END offer_to_map]
}
