package com.example.donos.compario;

public class DataModel {
    String name;
    String company;
    String distance;
    Integer offers;
    //soon: string pic?
    public DataModel(String name, String company, String distance, Integer offers) {
        this.name=name;
        this.company=company;
        this.distance=distance;
        this.offers=offers;
    }
    public String getName(){
        return name;
    }
    public String getCompany(){
        return company;
    }
    public String getDistance(){
        return distance;
    }
    public Integer getOffers(){
        return offers;
    }

}
