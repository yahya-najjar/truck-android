package com.micro.truck.truck.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.micro.truck.truck.Api.CallBackListener;
import com.micro.truck.truck.Api.SendGetJsonApi;
import com.micro.truck.truck.Masters.AppCompatActivityMenu;
import com.micro.truck.truck.Models.Truck;
import com.micro.truck.truck.R;
import com.micro.truck.truck.Utils.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class ShowActivity extends AppCompatActivityMenu {
    TextView driver_name,driver_name_2,supplier_name,email,driver_location,truck_model,capacity,price_per_hour,price_per_km,driver_phone,company_phone,distance,location;
    LinearLayout linear;
    int truck_id;

    ImageView image;
    Button request;
    RatingBar ratingBar;
    Truck truck;

    int UserId=0;
    String Token="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        truck = (Truck) getIntent().getSerializableExtra("truck");

        truck_id = truck.getId();
        driver_name = findViewById(R.id.driver_name);
        driver_name_2 = findViewById(R.id.driver_name_2);

        driver_name.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this,R.drawable.ic_profile),null,null,null);
        driver_name_2.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this,R.drawable.ic_profile),null,null,null);

        supplier_name = findViewById(R.id.supplier_name);
        supplier_name.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this,R.drawable.ic_briefcase),null,null,null);
        supplier_name.setText(truck.getSupplier_name());

        truck_model = findViewById(R.id.truck_model);
        capacity = findViewById(R.id.capacity);
        capacity.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this,R.drawable.ic_truck),null,null,null);
        price_per_km = findViewById(R.id.price_per_d);

        driver_phone = findViewById(R.id.driver_phone);
        driver_phone.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this,R.drawable.ic_address_card_regular),null,null,null);
//        driver_phone.setText(truck.getDriver_phone());

        distance = findViewById(R.id.distance);
        distance.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this,R.drawable.ic_map_marker_alt_solid),null,null,null);

        email = findViewById(R.id.driver_email);
        email.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this,R.drawable.ic_email),null,null,null);

        driver_location = findViewById(R.id.driver_location);
        driver_location.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this,R.drawable.ic_tracker),null,null,null);
        driver_location.setText(truck.getLocation());
//        company_phone = findViewById(R.id.company_phone);
        image = findViewById(R.id.image);
        ratingBar = findViewById(R.id.rating);

        driver_name.setText(truck.getDriver_name());
        driver_name_2.setText(truck.getDriver_name());

        truck_model.setText(truck.getModel());
        capacity.setText(""+truck.getCapacity());
//        price_per_km.setText(""+truck.getPrice_km());
        driver_phone.setText(""+truck.getDriver_phone());
        ratingBar.setRating(truck.getRating());
        distance.setText(String.valueOf(truck.getDistances()) + " Km");

//        company_phone.setText(""+truck.getCompany_phone());

        Log.d("content",truck.getImage());
        Glide.with(getBaseContext()).load(truck.getImage()).apply(new RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)).into(image);

        request = findViewById(R.id.request_btn);

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(),MapActivity.class);
                //intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("truck",truck.getId());
                startActivity(intent);
                //finish();

//                CustomDialog.getInstance(getString(R.string.are_sure_to_book),
//                        view1 -> {
//                                book();
//                        },null,ShowActivity.this
//                        ).showDialog();

            }
        });

        Log.d("content", String.valueOf(truck));
    }

    public void GpsInfo(View v){

//        intent.putExtra("truck",truck.getId());
        Intent gps_intent = new Intent(getBaseContext(),GpsActivity.class);
        gps_intent.putExtra("latitude",truck.getLat());
        gps_intent.putExtra("longitude",truck.getLng());
        startActivity(gps_intent);

    }

    public  void book(){
        Log.d("content","here you should book");
        JSONObject jsonParam = new JSONObject();
        UserId = readSharedPreferenceInt("UserId");
        Token = readSharedPreferenceString("token");

        try {
            jsonParam.put("comment", "this is comment");
            jsonParam.put("truck_id", truck_id);
            jsonParam.put("lat", 0);
            jsonParam.put("lng", 0);
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
                        startActivity(new Intent(getApplicationContext(), OrderActivity.class));
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
}
