package com.example.gpsprovider;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShowSavedLocationsByDatabase extends AppCompatActivity {

    private RecyclerView mRecyclerViev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_locations_by_database);
        mRecyclerViev = (RecyclerView) findViewById(R.id.recycerviev_locations);
        new FireBaseDatabaseHelper().savedLocations(new FireBaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<LocationData> locations, List<String> keys) {
                new RecyclerViev_Config().setConfig(mRecyclerViev,ShowSavedLocationsByDatabase.this,locations,keys);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }
}