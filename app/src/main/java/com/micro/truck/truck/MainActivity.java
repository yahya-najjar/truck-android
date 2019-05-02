package com.micro.truck.truck;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.micro.truck.truck.Activities.CompanyActivity;
import com.micro.truck.truck.Activities.MapActivity;
import com.micro.truck.truck.Activities.OrderActivity;
import com.micro.truck.truck.Masters.AppCompatActivityMenu;
import com.micro.truck.truck.Activities.LoginActivity;
import com.micro.truck.truck.Activities.ShowActivity;
import com.micro.truck.truck.Adapters.MyRecyclerViewAdapter;
import com.micro.truck.truck.Api.CallBackListener;
import com.micro.truck.truck.Api.SendGetJsonApi;
import com.micro.truck.truck.Models.Order;
import com.micro.truck.truck.Models.Truck;
import com.micro.truck.truck.Utils.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivityMenu implements MyRecyclerViewAdapter.ItemClickListener, LocationListener {
    int UserId=0;
    String Token="";

    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DrawerLayout mDrawerLayout;
    public ArrayList<Truck> online_trucks = new ArrayList<>();

    private SwipeRefreshLayout mySwipeRefreshLayout;

    GPSTracker gps;
    LocationManager mLocationManager;
    Double longitude = 36.2789919;
    Double latitude = 33.5082554;
    boolean isMyLocation= true;

    Boolean grant = false;
    Timer timerRefresh;
    SeekBar seekBar;
    TextView seekBar_text;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserId = readSharedPreferenceInt("UserId");
        Token = readSharedPreferenceString("token");
        int ActivateStatus = readSharedPreferenceInt("Verified");

        checkUser();
        setContentView(R.layout.activity_drawer_layout);
        Log.d("content", "Main activity started");

        getLocation();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name,R.string.app_name);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        NavigationView navigationView = findViewById(R.id.nav_view);

        NavigationView left_navigation = findViewById(R.id.nav_filter);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        left_navigation.bringToFront();

        NavigationView.OnNavigationItemSelectedListener mNavigationListener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Log.d("content","clicled");
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                Log.d("content","closed");

                int id = menuItem.getItemId();
                Log.d("content",String.valueOf(id));

                if (id == R.id.logout) {
                    getSharedPreferences(APP_PREFS, MODE_PRIVATE).edit().clear().commit();
                    Intent intent4 = new Intent(getBaseContext(), LoginActivity.class);
                    intent4.addFlags(intent4.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent4);
                    finish();
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.my_orders) {
                    Log.d("content","my orders");
                    Intent intent5 = new Intent(getBaseContext(), OrderActivity.class);
                    startActivity(intent5);
                    finish();
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.profile) {
                } else if (id == R.id.setting) {

                } else if (id == R.id.policy) {
                    Intent intent6 = new Intent(getBaseContext(), CompanyActivity.class);
                    intent6.putExtra("company","policy");
                    startActivity(intent6);
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.about) {
                    Intent intent7 = new Intent(getBaseContext(), CompanyActivity.class);
                    intent7.putExtra("company","about");
                    startActivity(intent7);
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    return true;
                }else if (id == R.id.lang) {
                    ChangeLanguage();
                    return true;
                }
                return true;
            }
        };
        left_navigation.setNavigationItemSelectedListener(mNavigationListener);


//        NavigationView.OnNavigationItemSelectedListener fNavigationListener = new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                return false;
//            }
//        };

//        left_navigation.setNavigationItemSelectedListener(fNavigationListener);

        setSeekBar();

        //Customize the action bar ( button and logo )
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_more);
        actionbar.setTitle(getResources().getString(R.string.nearest_trucks));

        actionbar.setDisplayShowHomeEnabled(true);
//        actionbar.setIcon(R.drawable.rsz_logo_splash);

        Log.d("content", "actionbar started");



        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
//                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                        Log.d("content", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        getData();
//                        myUpdateOperation();
                    }
                }
        );

        runTimerRefresh();
    }

    // the end ot OnCreate

    public void getLocation(){
        if (Build.VERSION.SDK_INT >= 23) {

            List<String> permissionNeeded = new ArrayList<>();

            String[] allPermissionNeeded = {
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
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
//            Toast.makeText(this, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    }
    public void checkUser(){
        JSONObject jsonParam = new JSONObject();
        try {
            //jsonParam.put("FCM_Token", FCM_Token);
            //jsonParam.put("Platform", "android");
        }
        catch (Exception e) {}

        //call the OnlineTrucks api to get list of Truck
        new SendGetJsonApi(this, "check", jsonParam,Token, new CallBackListener() {
            @Override
            public void onFinish(String response) {
                String result="";
                try {
                    JSONObject jsonin = new JSONObject(response);
                    result = jsonin.optString("result");
                    if (result.equals("success")){
                            Log.d("content","user checked");
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onProgress(int process) {}
        }).Execute();


    }


    //this start the the drawer when click action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_search:
                mDrawerLayout.openDrawer(Gravity.END);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    //i will call this in SendGetJsonApi object to fill the Services with the adapter
    public void getData(){

        Log.d("content",latitude.toString());
        Log.d("content",longitude.toString());
        Locale locale = new Locale(curr_lang);
        Geocoder gc=new Geocoder(getApplicationContext(), locale);
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

        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("limit", 10);
            jsonParam.put("lat", latitude);
            jsonParam.put("lng", longitude);
            //jsonParam.put("FCM_Token", FCM_Token);
            //jsonParam.put("Platform", "android");

        }
        catch (Exception e) {}

        //call the OnlineTrucks api to get list of Truck
        new SendGetJsonApi(this, "OnlineTrucks", jsonParam,Token, new CallBackListener() {
            @Override
            public void onFinish(String response) {
                String result="";
                // Create the root JSONObject from the JSON string.
                try {
                    JSONObject jsonin = new JSONObject(response);
                    Log.d("content", String.valueOf(jsonin));

                    result = jsonin.optString("result");
                    if (result.equals("success")){

                        JSONArray jArray = jsonin.getJSONArray("content");

                        if (jArray != null) {
                            Log.d("content", "0:");
                            online_trucks.clear();
                            for (int i=0;i<jArray.length();i++){
                                Truck temp = new Truck();
                                Gson json = new Gson();
                                temp = json.fromJson(jArray.get(i).toString(),Truck.class);
                                online_trucks.add(temp);
                            }
                        }
//                        getData();
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                mLayoutManager = new LinearLayoutManager(getBaseContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
//                for (int i=0;i<5;i++){
//                    Truck temp = new Truck(0,"yahya","09304","location",200,"mercedes",934904212,934904212,1,1,200,200,0.0,0.0,3,"image",40.0,"12/12/2020","12/12/2010");
//                    Gson json = new Gson();
////                                temp = json.fromJson(jArray.get(i).toString(),Truck.class);
//                    online_trucks.add(temp);
//                }

                mAdapter = new MyRecyclerViewAdapter(MainActivity.this,online_trucks);
                mAdapter.setClickListener(MainActivity.this);

                mRecyclerView.setAdapter(mAdapter);

                mySwipeRefreshLayout.setRefreshing(false);
            }
            @Override
            public void onProgress(int process) {}
        }).Execute();



    }

    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.truck_data){
            Intent intent = new Intent(this,ShowActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("truck",mAdapter.getItem(position));
            startActivity(intent);
        }
        if (view.getId() == R.id.direct_request){
            Intent intent = new Intent(getBaseContext(), MapActivity.class);
            //intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("truck",mAdapter.getItem(position).getId());
            startActivity(intent);
        }
//          Toast.makeText(this, "You clicked " + mAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

    }

    // change language
    private void ChangeLanguage()
    {
        //get current

        if (curr_lang.equals("ar"))
        {
            curr_lang = "en";
        }
        else
        {
            curr_lang = "ar";
        }

        SharedPreferences sharedPrefereSt = getSharedPreferences(APP_PREFS + "_lang", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefereSt.edit();
        editor.putString("lang", curr_lang);
        editor.commit();

        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onResume() {
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

        mySwipeRefreshLayout.setRefreshing(true);
        getData();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    private void runTimerRefresh()
    {
        if (timerRefresh == null)
            timerRefresh = new Timer();
        else  { timerRefresh.cancel();timerRefresh = null; timerRefresh = new Timer(); }

        timerRefresh.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                mySwipeRefreshLayout.setRefreshing(true);
                getData();
            }

        },30000,30000);
    }

    public void OpenMyDrawer(View view){
        mDrawerLayout.openDrawer(Gravity.END);
    }

    public void setSeekBar(){
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar_text = findViewById(R.id.seekBar_text);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                seekBar_text.setText(String.valueOf(progress) + " Km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar_text.setText(String.valueOf(seekBar.getProgress()) + " Km");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("seekstoped","seek_stoped");
                getData();
                seekBar_text.setText(String.valueOf(seekBar.getProgress()) + " Km");
            }
        });


    }
}
