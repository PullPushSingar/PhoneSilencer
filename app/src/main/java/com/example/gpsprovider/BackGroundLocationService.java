package com.example.gpsprovider;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class BackGroundLocationService extends Service {
    private static final int TODO = 14;
    Location currentLocation;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    private LocationManager locationManager;
    private LocationListener locationListener;
    List <LocationData> locationData = new ArrayList<>();
    private static final String CHANNEL_ID = "PhoneSilencerServiceChannel";
    public static final int NOTIFICATION_ID = 1;
    FusedLocationProviderClient fusedLocationProviderClient;
    NotificationCompat notificationCompat;
    Toast toast;

    @Override
    public void onCreate() {
        super.onCreate();
//        locationRequest = new LocationRequest();
//        locationRequest.setInterval(1000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        locationCallBack = new LocationCallback() {
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                Log.e("service",String.valueOf(locationResult.getLastLocation().getLongitude()));
//                Log.e("service",String.valueOf(locationResult.getLastLocation().getLatitude()));
//
//            }
//        };
//
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(@NonNull Location location) {
//                System.out.println("ONlocationchanged");
//                System.out.println(location.getLongitude());
//                System.out.println(location.getLatitude());
//            }
//
//            };
//
//        if(checkLocationPermission());
//        {
//
//
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);
//
//
//
//        }
    }

    ;


    public int onStartCommand(Intent intent, int flags, int startId) {

        // tworzenie obiektu klasy z metodą createNotification()
        NotificationHelper notificationHelper = new NotificationHelper(this);
        // wywołanie metody createNotification() na obiekcie
        notificationHelper.createNotification();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.e("service", String.valueOf(locationResult.getLastLocation().getLongitude()));
                Log.e("service", String.valueOf(locationResult.getLastLocation().getLatitude()));
                currentLocation = locationResult.getLastLocation();
                System.out.println(currentLocation.getLatitude());
                System.out.println(currentLocation.getLongitude());
            }
        };
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(BackGroundLocationService.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        Context context = getApplicationContext();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);

        checkNotificationPolicy();

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            Log.e("Service", "Service is running");
                            locationListUpdate();
                            System.out.println(locationData.size());
                            if (currentLocation != null) {
                                 if(checkIfInDefinedArea(currentLocation)){
                                     changeAudioStatus(currentLocation);
                                 }
                                Log.e("Service", String.valueOf(currentLocation.getLongitude()));
                                Log.e("Service", String.valueOf(currentLocation.getLatitude()));






                            }else{
                                Log.e("Service","NULL");
                            }






                            try{

                                Thread.sleep(3000);
                            }catch (InterruptedException r){
                                r.printStackTrace();
                            }
                        }
                    }
                }
        ).start();
//        OnTaskRemoved(intent);
//        try {
//            Thread.sleep(2500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        startService(intent);
       return super.onStartCommand(intent,flags,startId);

    }

    private void OnTaskRemoved(Intent intent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Destroy");
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return false;
        }
        return true;
    }

    public void checkNotificationPolicy() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // if user granted access else ask for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.isNotificationPolicyAccessGranted()) {

            } else {
                // Open Setting screen to ask for permisssion
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            }
        }
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

    private boolean checkIfInDefinedArea(Location location) {
        // Współrzędne granic obszaru zdefiniowane przez użytkownika
//        double minLatitude = 50.0f;
//        double maxLatitude = 52.0f;
//        double minLongitude = 17.0f;
//        double maxLongitude = 20.0f;
//
//
//        // Sprawdź, czy położenie jest w granicach obszaru
//        if (location.getLatitude() >= minLatitude && location.getLatitude() <= maxLatitude
//                && location.getLongitude() >= minLongitude && location.getLongitude() <= maxLongitude) {
//            return true;
//        }
//        return false;
        for (LocationData loc : locationData){
            double latitude = Double.parseDouble(loc.getLatitude());
            double longitude = Double.parseDouble(loc.getLongitude());
            double distance = Double.parseDouble(loc.getDistance());
            Location comparelocation = new Location(location);
            comparelocation.setLatitude(latitude);
            comparelocation.setLongitude(longitude);

            if (location.distanceTo(comparelocation) < distance){
                return  true;
            }

        }
        return false;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void locationListUpdate(){

        new FireBaseDatabaseHelper().savedLocations(new FireBaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<LocationData> locations, List<String> keys) {
                if(locations.size() > 0){
                    locationData = locations;
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
    }
}