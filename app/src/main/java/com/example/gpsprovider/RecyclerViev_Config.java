package com.example.gpsprovider;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViev_Config {
    private Context mContext;

    public void setConfig(RecyclerView recyclerView, Context context, List<LocationData> LOCATIONS, List<String> keys){
        mContext = context;
        LocationAdapter mLocationAdapter = new LocationAdapter(LOCATIONS, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mLocationAdapter);
    }


    class LocationItemViev extends RecyclerView.ViewHolder {
         TextView mAdress;
         TextView Id;
         TextView mLongitude;
         TextView mLatitude;

        private String key;

        public LocationItemViev(ViewGroup parent){


            super(LayoutInflater.from(mContext).
                    inflate(R.layout.location_list_item,parent,false));

            mAdress = (TextView) itemView.findViewById(R.id.loaction_Adress);
            Id = (TextView) itemView.findViewById(R.id.loaction_ID);
            mLongitude = (TextView) itemView.findViewById(R.id.locationg_longitude);
            mLatitude = (TextView) itemView.findViewById(R.id.loaction_latitude);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, Update_Location_Data.class);
                    intent.putExtra("key",key);
                    intent.putExtra("adress",mAdress.getText().toString());
                    intent.putExtra("Note",Id.getText().toString());
                    intent.putExtra("latitude",mLatitude.getText().toString());
                    intent.putExtra("longitude",mLongitude.getText().toString());

                    mContext.startActivity(intent);
                }
            });

        }
        public void  bind(LocationData location,String key){
            mAdress.setText(location.getAdress());
            Id.setText(location.getId());
            mLongitude.setText(location.getLongitude());
            mLatitude.setText(location.getLatitude());
            this.key = key;
        }
    }
    class LocationAdapter extends RecyclerView.Adapter<LocationItemViev>{
        private List<LocationData> mLocationList;
        private List<String> mKeys;

        public LocationAdapter(List<LocationData> mLocationList, List<String> mKeys) {
            this.mLocationList = mLocationList;
            this.mKeys = mKeys;
        }

        public LocationAdapter() {
            super();
        }

        @NonNull
        @Override
        public LocationItemViev onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LocationItemViev(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull LocationItemViev holder, int position) {
            holder.bind(mLocationList.get(position),mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mLocationList.size();
        }
    }



}
