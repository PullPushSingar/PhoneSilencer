package com.example.gpsprovider;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.example.gpsprovider.databinding.ActivityShowPointOnMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class ShowPointOnMap extends FragmentActivity implements OnMapReadyCallback {

    List <Double> latitudeList = new ArrayList<>();
    List <Double> longitudeList = new ArrayList<>();
    List <Double> distanceList = new ArrayList<>();
    List <LatLng> LatLngList = new ArrayList<>();
    List <LocationData> MapsLocationData = new ArrayList<>();
    private GoogleMap mMap;
    private ActivityShowPointOnMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        new FireBaseDatabaseHelper().savedLocations(new FireBaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<LocationData> locations, List<String> keys) {
                if(locations.size() > 0){
                    for (LocationData loc : locations){
                        MapsLocationData.add(loc);
                        latitudeList.add(Double.parseDouble(loc.getLatitude()));
                        longitudeList.add(Double.parseDouble(loc.getLongitude()));
                        distanceList.add(Double.parseDouble(loc.getDistance()));
                    }
                }
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

        binding = ActivityShowPointOnMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (Double lat : latitudeList){

            LatLng  currentLatLng= new LatLng(lat,longitudeList.get(latitudeList.indexOf(lat)));
            LatLngList.add(currentLatLng);
        }


        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        for (LatLng latlng : LatLngList){
            String adress = MapsLocationData.get(LatLngList.indexOf(latlng)).getAdress();
            mMap.addMarker(new MarkerOptions().position(latlng).title(adress));
            //  mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLngList.get(0)));
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(latlng)
                    .radius(distanceList.get(LatLngList.indexOf(latlng))).fillColor(Color.TRANSPARENT)
                    .strokeColor(Color.GREEN)
                    .strokeWidth(5f));

        }
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);
        Location currentLocation = new Location(MainActivity.getCurrentLocation());
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLatLng).title("CurrentLocation").icon(bitmapDescriptor));

        mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));


    }
}