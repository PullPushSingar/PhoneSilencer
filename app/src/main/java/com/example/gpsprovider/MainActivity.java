package com.example.gpsprovider;


import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int NOTIFICATION_ID = 1;
    static Location getCurrentLocation() {
        return currentLocation;
    }

    private static Context context;
    public static final int DEFAULT_UPDATE_INTERVAL = 60;
    public static final int FAST_UPDATE_INTERVAL = 15;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private Thread thread;

    private static final int ON_DO_NOT_DISTURB_CALLBACK_CODE = 101;
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_updates, tv_address, tv_sensor, tv_CaountOfCrumbs;

    Switch sw_locationupdates, sw_gps;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button btn_newWaypoint, btn_showWayPointList, btn_showMap;

    boolean updateOn = false;

    static Location currentLocation;
    public static List<Location> savedLocations = new ArrayList<>();
    LocationData locationData;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Write a message to the database



        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_CaountOfCrumbs = findViewById(R.id.tv_CountOfCrumbs);
        btn_newWaypoint = findViewById(R.id.btn_newWayPoint);
        btn_showWayPointList = findViewById(R.id.btn_showWayPointList);
        btn_showMap = findViewById(R.id.btn_showMap);


        sw_gps = findViewById(R.id.sw_gps);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);
        System.out.println(tv_accuracy == null);
        locationRequest = new LocationRequest();
        savedLocations = new ArrayList<>();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        checkNotificationPolicy();
        locationCallBack = new LocationCallback() {


            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIValues(locationResult.getLastLocation());

            }
        };

        btn_newWaypoint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MyApplication myApplication = (MyApplication) getApplicationContext();
                savedLocations = myApplication.getMyLocations();
//                if (savedLocations != null && !savedLocations.contains(currentLocation)) {
//                    savedLocations.add(currentLocation);
//                }
                savedLocations.add(currentLocation);
                System.out.println(savedLocations.size());
                Geocoder geocoder = new Geocoder(MainActivity.this);


                List<Address> addressess = null;
                try {
                    addressess = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                    tv_address.setText(addressess.get(0).getAddressLine(0));
                } catch (Exception e) {
                    tv_address.setText("Unable to get street adress");

                }
                int Id = 0;
                LocationData locationData1 = new LocationData();

                    locationData1.setAdress(addressess.get(0).getAddressLine(0));
                    locationData1.setId(String.valueOf("Note"));
                    locationData1.setLongitude(String.valueOf(currentLocation.getLongitude()));
                    locationData1.setLatitude(String.valueOf(currentLocation.getLatitude()));
                    locationData1.setDistance(String.valueOf("50"));
                    new FireBaseDatabaseHelper().addLocation(locationData1, new FireBaseDatabaseHelper.DataStatus() {
                        @Override
                        public void DataIsLoaded(List<LocationData> locations, List<String> keys) {
                            Toast.makeText(MainActivity.this,"Location has been saved",Toast.LENGTH_SHORT).show();
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
        });
        btn_showWayPointList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ShowSavedLocationsByDatabase.class);
                startActivity(i);
            }
        });

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");

                }
            }


        });


        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationupdates.isChecked()) {
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });
        btn_showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ShowPointOnMap.class);
                startActivity(i);
            }
        });

        updateGPS();


        //changeAudioStatus(currentLocation);
        Context context = getApplicationContext();
        Intent intent = new Intent(MainActivity.this,BackGroundLocationService.class);
        if (Build.VERSION.SDK_INT >= 26){
            context.startService(intent);




            }


        }



    private void stopLocationUpdates() {
        tv_updates.setText("Location is NOT being tracked");
        tv_lat.setText("Not tracking location");
        tv_lon.setText("Not tracking location");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void startLocationUpdates() {


        tv_updates.setText(("Location is being tracked"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(this, "This app requires permission to be granted in to work properly", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }


    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {

                @Override
                public void onSuccess(Location location) {
                    if(location != null) {
                        currentLocation = location;
                        System.out.println(currentLocation.getLatitude());
                        System.out.println(currentLocation.getLongitude());
                        updateUIValues(currentLocation);
                        changeAudioStatus(currentLocation);
                    }

                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);

            }
        }

    }

    private void updateUIValues(Location location) {
        if (location != null) {
            tv_lat.setText(String.valueOf(location.getLatitude()));
            tv_lon.setText(String.valueOf(location.getLongitude()));
            tv_accuracy.setText(String.valueOf(location.getAccuracy()));
            changeAudioStatus(location);

            if (location.hasAltitude()) {
                tv_altitude.setText(String.valueOf(location.getAltitude()));
            } else {
                tv_altitude.setText(("Not available"));
            }
        }

        Geocoder geocoder = new Geocoder(MainActivity.this);


        try {
            List<Address> addressess = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addressess.get(0).getAddressLine(0));
        } catch (Exception e) {
            tv_address.setText("Unable to get street adress");

        }

        MyApplication myApplication = (MyApplication) getApplicationContext();
        currentLocation = location;
        savedLocations = myApplication.getMyLocations();
        if (savedLocations != null) {
           // tv_CaountOfCrumbs.setText(Integer.toString(savedLocations.size()));
            new FireBaseDatabaseHelper().savedLocations(new FireBaseDatabaseHelper.DataStatus() {
                @Override
                public void DataIsLoaded(List<LocationData> locations, List<String> keys) {
                    tv_CaountOfCrumbs.setText(Integer.toString(locations.size()));
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

    private boolean checkIfInDefinedArea(Location location) {
        // Współrzędne granic obszaru zdefiniowane przez użytkownika
        double minLatitude = 30.0f;
        double maxLatitude = 31.0f;
        double minLongitude = -124.0f;
        double maxLongitude = -120.0f;


        // Sprawdź, czy położenie jest w granicach obszaru
        if (location.getLatitude() >= minLatitude && location.getLatitude() <= maxLatitude
                && location.getLongitude() >= minLongitude && location.getLongitude() <= maxLongitude) {
            return true;
        }
        return false;

    }

    public void changeAudioStatus(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        boolean inDefinedArea = checkIfInDefinedArea(location);
        if (inDefinedArea && !(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)) {
            // Wyciszamy telefon
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            System.out.println("Wyciszony");

        } else if (!inDefinedArea){
            // Odciszamy telefon
            if (!(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                System.out.println("nie wyciszony");
            }


        }

    }

    public void checkNotificationPolicy() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // if user granted access else ask for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.isNotificationPolicyAccessGranted()) {

            } else {
                // Open Setting screen to ask for permisssion
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivityForResult(intent, ON_DO_NOT_DISTURB_CALLBACK_CODE);
            }
        }






}

    public void goToJsos(View view) {
        String url = "https://jsos.pwr.edu.pl/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    public void goToEportal(View view) {
        String url = "https://eportal.pwr.edu.pl/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

}
