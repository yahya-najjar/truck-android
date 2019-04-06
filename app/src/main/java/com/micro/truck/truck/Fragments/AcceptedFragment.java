package com.micro.truck.truck.Fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.micro.truck.truck.Activities.GpsActivity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;


public class AcceptedFragment extends MasterFragment implements OrderRecyclerViewAdapter.ItemClickListener {




    public static final String FRAGMENT_NAME = "ACCEPTED_FRAGMENT";
    private RecyclerView mRecyclerView;
    private LoadingDialog mLoadingDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView errorTextView;
    private LinearLayoutManager mLayoutManager;
    private OrderRecyclerViewAdapter mAdapter;


    int UserId=0;
    String Token="";
    public ArrayList<Order> accepted_orders = new ArrayList<>();

    public AcceptedFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accepted, container, false);
        findViewsById(view);
        assignActions();
        getData();
        return view;
    }


    private void findViewsById(View view) {
        mLoadingDialog = LoadingDialog.getInstance(getActivity());
        mRecyclerView = view.findViewById(R.id.accepted_recyclerView);
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
            jsonParam.put("status", 1);
            //jsonParam.put("FCM_Token", FCM_Token);
            //jsonParam.put("Platform", "android");

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
                    Log.d("content", String.valueOf(jsonin));

                    result = jsonin.optString("result");
                    mLoadingDialog.closeDialog();
                    if (result.equals("success")){

                        JSONArray jArray = jsonin.getJSONArray("content");

                        if (jArray != null) {
                            accepted_orders.clear();
                            for (int i=0;i<jArray.length();i++){
                                Order temp = new Order();
                                Gson json = new Gson();
                                temp = json.fromJson(jArray.get(i).toString(),Order.class);
                                accepted_orders.add(temp);
                            }
                        }

                        if (accepted_orders.size() <= 0){
                            errorTextView.setText(R.string.no_accepted_orders);
                            errorTextView.setVisibility(View.VISIBLE);
                        }
                        mLayoutManager = new LinearLayoutManager(getContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);

                        mAdapter = new OrderRecyclerViewAdapter(getContext(),accepted_orders);
                        mAdapter.setClickListener(AcceptedFragment.this);
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



    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.order_button){
            Log.d("content","order button");
            Intent gps_intent = new Intent(getContext(), GpsActivity.class);
            gps_intent.putExtra("latitude",mAdapter.getItem(position).getLat());
            gps_intent.putExtra("longitude",mAdapter.getItem(position).getLng());
            startActivity(gps_intent);
        }
          Toast.makeText(getContext(), "You clicked " + mAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

    }

}
