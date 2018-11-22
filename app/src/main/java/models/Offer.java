package models;

public class Offer {
    public String storename;
    public String name;
    public Integer price;
    public String category;

    public Offer() {
        // Default constructor required for calls to DataSnapshot.getValue(Offer.class)
    }

    public Offer(String storename, String name, Integer price, String category) {
        this.storename = storename;
        this.name = name;
        this.price = price;
        this.category = category;
    }
}
