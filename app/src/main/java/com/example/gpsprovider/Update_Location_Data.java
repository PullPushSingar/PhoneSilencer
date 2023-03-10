package com.example.gpsprovider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class Update_Location_Data extends AppCompatActivity {


        private EditText mNote_editTxt;
        private Button mUpdate_btn;
        private Button mDelete_btn;
        private Spinner diastance_spinner;

        private String key;
        private String adress;
        private String Note;
        private String longitude;
        private String latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_location_data);
        key = getIntent().getStringExtra("key");
        adress = getIntent().getStringExtra("adress");
        Note = getIntent().getStringExtra("Note");
        longitude = getIntent().getStringExtra("longitude");
        latitude = getIntent().getStringExtra("latitude");


        mNote_editTxt = (EditText) findViewById(R.id.Note_Update);
        mUpdate_btn = (Button) findViewById(R.id.update_btn);
        mDelete_btn = (Button) findViewById(R.id.Delete_BUTTON);
        diastance_spinner = (Spinner) findViewById(R.id.distance_spinner);

        mUpdate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationData location = new LocationData();
                location.setAdress(adress);
                location.setId(mNote_editTxt.getText().toString());
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                location.setDistance(diastance_spinner.getSelectedItem().toString());

                new FireBaseDatabaseHelper().updateLocation(key, location, new FireBaseDatabaseHelper.DataStatus() {
                    @Override
                    public void DataIsLoaded(List<LocationData> locations, List<String> keys) {

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
                finish();
            }
        });
        mDelete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FireBaseDatabaseHelper().deleteLocation(key, new FireBaseDatabaseHelper.DataStatus() {
                    @Override
                    public void DataIsLoaded(List<LocationData> locations, List<String> keys) {

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
                finish();

            }
        });
    }

}