package com.micro.truck.truck.Fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.micro.truck.truck.Activities.LoginActivity;
import com.micro.truck.truck.Activities.MapActivity;
import com.micro.truck.truck.Activities.OrderActivity;
import com.micro.truck.truck.Activities.ShowActivity;
import com.micro.truck.truck.Adapters.MyRecyclerViewAdapter;
import com.micro.truck.truck.Adapters.OrderRecyclerViewAdapter;
import com.micro.truck.truck.Api.CallBackListener;
import com.micro.truck.truck.Api.SendGetJsonApi;
import com.micro.truck.truck.Masters.MasterFragment;
import com.micro.truck.truck.Models.Order;
import com.micro.truck.truck.R;
import com.micro.truck.truck.Utils.GPSTracker;
import com.micro.truck.truck.Utils.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.micro.truck.truck.Masters.AppCompatActivityMenu.APP_PREFS;
import static com.micro.truck.truck.Masters.AppCompatActivityMenu.curr_lang;


public class PendingFragment extends MasterFragment implements OrderRecyclerViewAdapter.ItemClickListener , LocationListener {

    public static final String FRAGMENT_NAME = "PENDING_FRAGMENT";
    private RecyclerView mRecyclerView;
    private LoadingDialog mLoadingDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView errorTextView;
    private LinearLayoutManager mLayoutManager;
    private OrderRecyclerViewAdapter mAdapter;

    GPSTracker gps;
    LocationManager mLocationManager;
    Double longitude = 36.2789919;
    Double latitude = 33.5082554;
    boolean isMyLocation= true;
    Boolean grant = false;


    int UserId=0;
    String Token="";
    List<Address> addresses;
    String location;
    public ArrayList<Order> pending_orders = new ArrayList<>();

    public PendingFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending, container, false);
        findViewsById(view);
        getLocation();
        assignActions();
        getData();
        return view;
    }

    private void findViewsById(View view) {
        mLoadingDialog = LoadingDialog.getInstance(getActivity());
        mRecyclerView = view.findViewById(R.id.pending_recyclerView);
        errorTextView = view.findViewById(R.id.error_textView);
        mSwipeRefreshLayout = view.findViewById(R.id.mSwipeRefreshLayout);
    }

    private void assignActions() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            getData();
        });
    }

    private void getData() {

        UserId = readSharedPreferenceInt("UserId");
        Token = readSharedPreferenceString("token");
        JSONObject jsonParam = new JSONObject();
        mLoadingDialog.showDialog();
        try {
            jsonParam.put("limit", 10);
            jsonParam.put("lat", 0);
            jsonParam.put("lng", 0);
            jsonParam.put("status", 0);
        }
        catch (Exception e) {}

        //call the OnlineTrucks api to get list of Truck
        new SendGetJsonApi(getActivity(), "myOrders", jsonParam,Token, new CallBackListener() {
            @Override
            public void onFinish(String response) {
                String result="";
                // Create the root JSONObject from the JSON string.
                try {
                    JSONObject jsonin = new JSONObject(response);

                    result = jsonin.optString("result");
                    mLoadingDialog.closeDialog();
                    if (result.equals("success")){

                        JSONArray jArray = jsonin.getJSONArray("content");

                        if (jArray != null) {
                            pending_orders.clear();
                            for (int i=0;i<jArray.length();i++){
                                Order temp = new Order();
                                Gson json = new Gson();
                                temp = json.fromJson(jArray.get(i).toString(),Order.class);
                                pending_orders.add(temp);
                            }
                        }

                        if (pending_orders.size() <= 0){
                            errorTextView.setText(R.string.no_pending_orders);
                            errorTextView.setVisibility(View.VISIBLE);
                        }
                        mLayoutManager = new LinearLayoutManager(getContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);

                        mAdapter = new OrderRecyclerViewAdapter(getContext(),pending_orders);
                        mAdapter.setClickListener(PendingFragment.this);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    else {
                        String error_des = jsonin.optString("error_des");
                        if(!error_des.equals("")){
                            Log.d("content", error_des);
                        }else {
                            Log.d("content", "not success");
                        }
                        //this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onProgress(int process) {

            }

        }).Execute();

    }

    public void getLocation(){
        if (Build.VERSION.SDK_INT >= 23) {

            List<String> permissionNeeded = new ArrayList<>();

            String[] allPermissionNeeded = {
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION};


            for (String permission : allPermissionNeeded)
                if (getContext().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                    permissionNeeded.add(permission);

            if (permissionNeeded.size() > 0) {
                requestPermissions(permissionNeeded.toArray(new String[0]) , 1);
            }
            else
            {
                grant = true;
            }

        }
        else
        {
            grant = true;
        }


        gps = new GPSTracker(getContext());

        gps = new GPSTracker(getContext());
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            // \n is for new line
            Log.d("Tag",latitude + " " + longitude);
//            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
//            try {
//                this.addresses = geocoder.getFromLocation(latitude, longitude, 1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Log.d("content",String.valueOf(addresses));
//            Toast.makeText(this, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i("user", "onResume");
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

//        mySwipeRefreshLayout.setRefreshing(true);
//        getData();
    }


    @Override
    public void onPause() {
        super.onPause();

        Log.i("user", "onPause");
        mLocationManager.removeUpdates(this);
    }


    @Override
    public void onLocationChanged(Location location) {
        if (isMyLocation){
            Log.i("user", String.valueOf(location.getLatitude()));
            Log.i("user", String.valueOf(location.getLongitude()));
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("user", "Provider " + provider + " has now status: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("user", "Provider " + provider + " is enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("user", "Provider " + provider + " is disabled");
    }


    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.order_button){
            // cancel the order

            Log.d("content","here you should book");
            JSONObject jsonParam = new JSONObject();
            UserId = readSharedPreferenceInt("UserId");
            Token = readSharedPreferenceString("token");

            Locale locale = new Locale(curr_lang);
            Geocoder gc=new Geocoder(getContext(), locale);
            StringBuilder builder = new StringBuilder();
            try {
                List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);//here we pass two static lati longi of white house and set the max return value 1
                if (addresses.size()>0){
                    Address address=addresses.get(0);//i featched the result from the list
                    //looping into the max address line contain the result
                    builder.append(address.getCountryName()+ ", " + address.getAdminArea() + ", " + address.getSubAdminArea()); //appending the addressline of the given latitude longitude
                    location =builder.toString();
                }

            } catch (IOException e) {
                e.printStackTrace();
                builder.append("");
            }

            try {
                jsonParam.put("location", location);
                jsonParam.put("order_id", mAdapter.getItem(position).getId());
                jsonParam.put("lat", latitude);
                jsonParam.put("lng", longitude);

            }
            catch (Exception e) {}

            //call the OnlineTrucks api to get list of Truck
            new SendGetJsonApi(getContext(), "cancelOrder", jsonParam,Token, new CallBackListener() {
                @Override
                public void onFinish(String response) {
                    String result="";
                    // Create the root JSONObject from the JSON string.
                    try {
                        JSONObject jsonin = new JSONObject(response);
                        Log.d("content", String.valueOf(jsonin));

                        result = jsonin.optString("result");
                        if (result.equals("success")){
                            getData();
                        }
                        else {
                            int error_code = jsonin.optInt("error_code");
                            String error_des = jsonin.optString("error_des");

                            switch (error_code){
                                case 1:
                                    if(!error_des.equals("")){
                                        Log.d("content", error_des);
                                        Toast.makeText(getContext(), error_des , Toast.LENGTH_LONG).show();
                                    }else {
                                        Log.d("content", "not success");
                                    }
                                    break;
                                case -1:
                                    Log.d("content","token error");
                                    Toast.makeText(getContext(), "You have to login again " , Toast.LENGTH_SHORT).show();
                                    getContext().getSharedPreferences(APP_PREFS, MODE_PRIVATE).edit().clear().commit();
                                    Intent intent4 = new Intent(getContext(), LoginActivity.class);
                                    intent4.addFlags(intent4.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent4);
                                    getActivity().finish();
                                    break;
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onProgress(int process) {}
            }).Execute();

        }

    }



}
