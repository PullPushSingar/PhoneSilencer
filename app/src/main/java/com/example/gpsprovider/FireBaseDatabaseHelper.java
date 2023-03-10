package com.example.gpsprovider;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FireBaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceLocation;
    private List<LocationData> Locations = new ArrayList<>();
    LocationData locations = new LocationData();

    public interface DataStatus{
        void DataIsLoaded(List<LocationData> locations,List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public FireBaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance("https://gpsprovider-5c01c-default-rtdb.europe-west1.firebasedatabase.app");
        mReferenceLocation = mDatabase.getReference();
    }

    public void savedLocations(final DataStatus dataStatus){
        mReferenceLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Locations.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : snapshot.getChildren()){
                    keys.add(keyNode.getKey());
//                    LocationData location = new LocationData();
//                    location.setLatitude(snapshot.child("latitude").getValue(String.class));
//                    location.setLongitude(snapshot.child("longitude").getValue(String.class));
//                    location.setId(snapshot.child("id").getValue(String.class));
//                    location.setAdress(snapshot.child("adress").getValue(String.class));
                    LocationData location = keyNode.getValue(LocationData.class);
                    Locations.add(location);
                }
                dataStatus.DataIsLoaded(Locations,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        }
    public void addLocation(LocationData location, final DataStatus dataStatus){
        String key = mReferenceLocation.push().getKey();
        mReferenceLocation.child(key).setValue(location).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dataStatus.DataIsInserted();
            }
        });

    }
    public void updateLocation(String key,LocationData location, final DataStatus dataStatus){
        mReferenceLocation.child(key).setValue(location).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dataStatus.DataIsUpdated();
            }
        });

    }
    public void deleteLocation(String key,final DataStatus dataStatus){
        mReferenceLocation.child(key).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dataStatus.DataIsDeleted();
            }
        });
    }

}
