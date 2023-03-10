package com.example.gpsprovider;

public class LocationData {


// ...

    private String Id,longitude,latitude,adress,distance;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public LocationData() {
    }


    public void setId(String id) {
        Id = id;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getAdress() {
        return adress;
    }

    public LocationData(String id, String longitude, String latitude, String adress,String distance) {

        Id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.adress = adress;
        this.distance = distance;
    }



    public String getId() {
        return Id;
    }
}
