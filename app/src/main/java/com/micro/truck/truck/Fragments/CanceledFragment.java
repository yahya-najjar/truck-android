package com.micro.truck.truck.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.micro.truck.truck.Adapters.OrderRecyclerViewAdapter;
import com.micro.truck.truck.Api.CallBackListener;
import com.micro.truck.truck.Api.SendGetJsonApi;
import com.micro.truck.truck.Masters.MasterFragment;
import com.micro.truck.truck.Models.Order;
import com.micro.truck.truck.R;
import com.micro.truck.truck.Utils.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CanceledFragment extends MasterFragment implements OrderRecyclerViewAdapter.ItemClickListener {

    public static final String FRAGMENT_NAME = "COMPLETED_FRAGMENT";
    private RecyclerView mRecyclerView;
    private LoadingDialog mLoadingDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView errorTextView;
    private LinearLayoutManager mLayoutManager;
    private OrderRecyclerViewAdapter mAdapter;


    int UserId=0;
    String Token="";
    public ArrayList<Order> canceled = new ArrayList<>();

    public CanceledFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_canceled, container, false);
        findViewsById(view);
        assignActions();
        getData();
        return view;
    }

    private void findViewsById(View view) {
        mLoadingDialog = LoadingDialog.getInstance(getActivity());
        mRecyclerView = view.findViewById(R.id.canceled_recyclerView);
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
            jsonParam.put("status", -2);
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
                    mLoadingDialog.closeDialog();

                    result = jsonin.optString("result");
                    if (result.equals("success")){

                        JSONArray jArray = jsonin.getJSONArray("content");

                        if (jArray != null) {
                            canceled.clear();
                            for (int i=0;i<jArray.length();i++){
                                Order temp = new Order();
                                Gson json = new Gson();
                                temp = json.fromJson(jArray.get(i).toString(),Order.class);
                                canceled.add(temp);
                            }
                        }

                        if (canceled.size() <= 0){
                            errorTextView.setText(R.string.no_canceled_orders);
                            errorTextView.setVisibility(View.VISIBLE);
                        }
                        mLayoutManager = new LinearLayoutManager(getContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);

                        mAdapter = new OrderRecyclerViewAdapter(getContext(),canceled);
                        mAdapter.setClickListener(CanceledFragment.this);
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
        Log.d("content","order buttonmm");

        if (view.getId() == R.id.order_button){
            Log.d("content","order button");
        }
        Toast.makeText(getContext(), "You clicked Canceled" + mAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

    }

}
