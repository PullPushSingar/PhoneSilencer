package com.example.gpsprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class ShowSavedLocationList extends AppCompatActivity {
    public ListView lv_savedLocations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Geocoder geocoder = new Geocoder(ShowSavedLocationList.this);
        List<Address>  adressList = new ArrayList<>();
        List<String> ListOfString = new ArrayList<>();
        setContentView(R.layout.activity_show_saved_location_list);
        lv_savedLocations = findViewById(R.id.lv_Points);
        for (Location loc:MainActivity.savedLocations) {
            try {
                adressList = (geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(),1));
                String add = adressList.get(0).getAddressLine(0);

                    System.out.println(add);
                    ListOfString.add(add);



            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ShowSavedLocationList.this, android.R.layout.simple_list_item_1, ListOfString);
        System.out.println("przedadapterem");
        System.out.println("Po adaptzerze");
        if (lv_savedLocations != null) {
            lv_savedLocations.setAdapter(arrayAdapter);
        }
        System.out.println("null");
        lv_savedLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListOfString.add(position,"ABCDEF");
                System.out.println((ListOfString.get(position)));

                //MainActivity.savedLocations.remove(position);
            }
        });
    }


}