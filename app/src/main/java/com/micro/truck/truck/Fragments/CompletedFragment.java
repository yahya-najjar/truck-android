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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.micro.truck.truck.Activities.OrderActivity;
import com.micro.truck.truck.Adapters.OrderRecyclerViewAdapter;
import com.micro.truck.truck.Api.CallBackListener;
import com.micro.truck.truck.Api.SendGetJsonApi;
import com.micro.truck.truck.MainActivity;
import com.micro.truck.truck.Masters.MasterFragment;
import com.micro.truck.truck.Models.Order;
import com.micro.truck.truck.R;
import com.micro.truck.truck.Utils.LoadingDialog;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static android.support.constraint.Constraints.TAG;


public class CompletedFragment extends MasterFragment implements OrderRecyclerViewAdapter.ItemClickListener , RatingDialogListener {

    public static final String FRAGMENT_NAME = "COMPLETED_FRAGMENT";
    private RecyclerView mRecyclerView;
    private LoadingDialog mLoadingDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView errorTextView;
    private LinearLayoutManager mLayoutManager;
    private OrderRecyclerViewAdapter mAdapter;
    private Button ratingButton;
    private int order_id;

    int UserId=0;
    String Token="";
    public ArrayList<Order> completed_orders = new ArrayList<>();

    public CompletedFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_completed, container, false);
        findViewsById(view);
        assignActions();
        getData();
        return view;
    }

    private void findViewsById(View view) {
        mLoadingDialog = LoadingDialog.getInstance(getActivity());
        mRecyclerView = view.findViewById(R.id.completed_recyclerView);
        errorTextView = view.findViewById(R.id.error_textView);
        mSwipeRefreshLayout = view.findViewById(R.id.mSwipeRefreshLayout);
        ratingButton = view.findViewById(R.id.order_button);
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
        mLoadingDialog.showDialog();
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("limit", 10);
            jsonParam.put("lat", 0);
            jsonParam.put("lng", 0);
            jsonParam.put("status", 3);
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
                    mLoadingDialog.closeDialog();
                    result = jsonin.optString("result");
                    if (result.equals("success")){

                        JSONArray jArray = jsonin.getJSONArray("content");

                        if (jArray != null) {
                            completed_orders.clear();
                            for (int i=0;i<jArray.length();i++){
                                Order temp = new Order();
                                Gson json = new Gson();
                                temp = json.fromJson(jArray.get(i).toString(),Order.class);
                                completed_orders.add(temp);
                            }
                        }

                        if (completed_orders.size() <= 0){
                            errorTextView.setText(R.string.no_completed_orders);
                            errorTextView.setVisibility(View.VISIBLE);
                        }
                        mLayoutManager = new LinearLayoutManager(getContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);

                        mAdapter = new OrderRecyclerViewAdapter(getContext(),completed_orders);
                        mAdapter.setClickListener(CompletedFragment.this);
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

        order_id = mAdapter.getItem(position).getId();
        if (view.getId() == R.id.order_button){
            Log.d("content","order button");
            new AppRatingDialog.Builder()
                    .setPositiveButtonText("Submit")
                    .setNegativeButtonText("Cancel")
                    .setNeutralButtonText("Later")
                    .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                    .setDefaultRating(2)
                    .setTitle("Rate this Trip")
                    .setDescription("Please select some stars and give your feedback")
                    .setCommentInputEnabled(true)
                    .setDefaultComment("This trip is pretty cool !")
                    .setStarColor(R.color.starColor)
                    .setNoteDescriptionTextColor(R.color.white)
                    .setTitleTextColor(R.color.orange)
                    .setDescriptionTextColor(R.color.white)
                    .setHint("Please write your comment here ...")
                    .setHintTextColor(R.color.orange)
                    .setCommentTextColor(R.color.orange)
                    .setCommentBackgroundColor(R.color.colorPrimaryDark)
                    .setWindowAnimation(R.style.MyDialogFadeAnimation)
                    .setCancelable(false)
                    .setCanceledOnTouchOutside(false)
                    .create(getActivity())
                    .setTargetFragment(CompletedFragment.this, 1) // only if listener is implemented by fragment
                    .show();
        }
//        Toast.makeText(getContext(), "You clicked Completed" + mAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onPositiveButtonClicked(int rate, String comment) {
        Log.d("rated",comment);
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("limit", 10);
            jsonParam.put("rating", rate);
            jsonParam.put("comment", comment);
            jsonParam.put("order_id", order_id);
            Token = readSharedPreferenceString("token");
            //jsonParam.put("FCM_Token", FCM_Token);
            //jsonParam.put("Platform", "android");

        }
        catch (Exception e) {}
        new SendGetJsonApi(getContext(), "ratingOrder", jsonParam,Token, new CallBackListener() {
            @Override
            public void onFinish(String response) {
                String result="";
                // Create the root JSONObject from the JSON string.
                try {
                    JSONObject jsonin = new JSONObject(response);
                    Log.d("content", String.valueOf(jsonin));

                    result = jsonin.optString("result");
                    if (result.equals("success")){
//                        ratingButton.setEnabled(false);
                        Toast.makeText(getContext(), "Thank You For Your FeedBack " , Toast.LENGTH_LONG).show();
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
                                Log.d("content", "not auth");

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

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

}
