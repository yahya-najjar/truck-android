package com.micro.truck.truck.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.micro.truck.truck.Api.CallBackListener;
import com.micro.truck.truck.Api.SendGetJsonApi;
import com.micro.truck.truck.MainActivity;
import com.micro.truck.truck.Masters.AppCompatActivityMenu;
import com.micro.truck.truck.Models.Truck;
import com.micro.truck.truck.R;
import com.micro.truck.truck.Utils.CustomDialog;
import com.micro.truck.truck.Utils.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.micro.truck.truck.Masters.AppCompatActivityMenu.APP_PREFS;

public class MapActivity extends AppCompatActivityMenu implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    private GoogleMap googleMap;
    ArrayList<LatLng> markerPoints;
    SupportMapFragment supportMapFragment;
    GPSTracker gps;
    LocationManager mLocationManager;
    Double longitude = 36.2789919;
    Double latitude = 33.5082554;
    boolean isMyLocation= true;

    Boolean grant = false;

    boolean checkGPS;
    boolean checkNetwork;

    Bundle bundle;
    int truck_id;
    int UserId=0;
    String Token="";
    String details="";
    EditText map_details;
    Button cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = savedInstanceState;
        setContentView(R.layout.activity_map);
        truck_id = (int) getIntent().getIntExtra("truck",0);
        map_details = (EditText) findViewById(R.id.map_details);
        cancel = findViewById(R.id.cancel_map);
        cancel.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this,R.drawable.ic_left_arrow),null,null,null);


        if (Build.VERSION.SDK_INT >= 23) {

            List<String> permissionNeeded = new ArrayList<>();

            String[] allPermissionNeeded = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION};


            for (String permission : allPermissionNeeded)
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
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


        gps = new GPSTracker(this);

        gps = new GPSTracker(this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            // \n is for new line
            Log.d("Tag",latitude + " " + longitude);
            Toast.makeText(this, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Log.d("content", "Fragment set Ok");

        if (supportMapFragment == null) {
            Log.d("content", "supportMapFragment null");

            FragmentManager fm = getSupportFragmentManager();
            assert fm != null;
            FragmentTransaction ft = fm.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, supportMapFragment).commit();

        }


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        supportMapFragment.getMapAsync(this);

        Log.d("content", "get Map Async Ok");


    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("")
                        .setMessage("")
                        .setPositiveButton("Ok", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(this, new String[]
                                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        googleMap.setMyLocationEnabled(true);

                    }

                } else {


                }
                return;
            }

        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        // Showing Alert Message
        alertDialog.show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Location location = getLastKnownLocation();
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //Request location updates:
                googleMap.setMyLocationEnabled(true);
            }
        }

        markerPoints = new ArrayList<LatLng>();

        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        // For dropping a marker at a point on the Map
        Log.d("lan", longitude + "");
        Log.d("lan", latitude + "");
        LatLng sydney = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(sydney).
                title("Title").snippet("TitleName"));

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                (cameraPosition));
        Log.d("content","map ready");



        Log.d("content","map checkLocationPermission");


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
//                allPoints.add(point);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(point).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                        (cameraPosition));
                isMyLocation = false;
                latitude = point.latitude;
                longitude = point.longitude;
                Log.d("content","Map clicked");
                Log.d("lan", longitude + "");
                Log.d("lan", latitude + "");

            }
        });

        Log.d("content","cameraPosition");

    }


    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        for (String provider : providers) {

            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.i("user", "onResume");
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(this), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    public void CancelMapAction(View view){
//        CancelButton.setTextColor(getResources().getColor(R.color.orange));
//        CancelButton.setBackground(getResources().getDrawable(R.drawable.rounded_white));
        finish();
    }

    public  void MapRequestAction(View view){
        details = map_details.getText().toString().trim();
        Log.d("content",details);
        CustomDialog.getInstance(getString(R.string.are_sure_to_book),
                        view1 -> {
                                book();
                        },null,MapActivity.this
                        ).showDialog();
    }

    public  void book(){
        Log.d("content","here you should book");
        JSONObject jsonParam = new JSONObject();
        UserId = readSharedPreferenceInt("UserId");
        Token = readSharedPreferenceString("token");

        try {
            jsonParam.put("comment", details);
            jsonParam.put("truck_id", truck_id);
            jsonParam.put("lat", latitude);
            jsonParam.put("lng", longitude);
            jsonParam.put("location", "Damascus");

        }
        catch (Exception e) {}

        //call the OnlineTrucks api to get list of Truck
        new SendGetJsonApi(this, "order", jsonParam,Token, new CallBackListener() {
            @Override
            public void onFinish(String response) {
                String result="";
                // Create the root JSONObject from the JSON string.
                try {
                    JSONObject jsonin = new JSONObject(response);
                    Log.d("content", String.valueOf(jsonin));

                    result = jsonin.optString("result");
                    if (result.equals("success")){
                        Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
                        startActivity(intent);
                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                    }
                    else {
                        int error_code = jsonin.optInt("error_code");
                        String error_des = jsonin.optString("error_des");

                        switch (error_code){
                            case 1:
                                if(!error_des.equals("")){
                                    Log.d("content", error_des);
                                    Toast.makeText(getBaseContext(), error_des , Toast.LENGTH_LONG).show();
                                }else {
                                    Log.d("content", "not success");
                                }
                                break;
                            case -1:
                                Toast.makeText(getBaseContext(), "You have to login again " , Toast.LENGTH_SHORT).show();
                                getSharedPreferences(APP_PREFS, MODE_PRIVATE).edit().clear().commit();
                                Intent intent4 = new Intent(getBaseContext(), LoginActivity.class);
                                intent4.addFlags(intent4.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent4);
                                finish();
                                break;
                        }

                        //this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onProgress(int process) {}
        }).Execute();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(getBaseContext() , MainActivity.class);
        intent1.addFlags(intent1.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
        finish();
    }

    //    @Override
//    public void setTitle(CharSequence title) {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
//        setSupportActionBar(toolbar);
//        super.setTitle("");
//    }
}
