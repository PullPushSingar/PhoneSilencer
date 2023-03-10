package com.example.gpsprovider;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    List<Location> myLocations = new ArrayList<>() ;
    private static MyApplication singleton;


    public List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocation(List<Location> myLocation) {
        this.myLocations = myLocation;
    }



    public MyApplication getInstance(){
        System.out.println("singleton");
        return singleton;
    }

    public void OnCreate(){

        super.onCreate();
        singleton = this;
        System.out.println("Arraylist");
    }
}
