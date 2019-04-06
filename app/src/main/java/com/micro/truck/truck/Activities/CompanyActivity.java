package com.micro.truck.truck.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.micro.truck.truck.Api.CallBackListener;
import com.micro.truck.truck.Api.SendGetJsonApi;
import com.micro.truck.truck.Masters.AppCompatActivityMenu;
import com.micro.truck.truck.R;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Ya7ya on 11/3/2018.
 */

public class CompanyActivity extends AppCompatActivityMenu
{
    String Token;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Token = readSharedPreferenceString("token");

        setContentView(R.layout.activity_company_profile);
        TextView title = (TextView) findViewById(R.id.company_title)
                ,info = (TextView) findViewById(R.id.company_info);

        String company_info = (String) getIntent().getStringExtra("company");

        JSONObject jsonParam = new JSONObject();
        try {
            //jsonParam.put("FCM_Token", FCM_Token);
            //jsonParam.put("Platform", "android");
            jsonParam.put("term", company_info);
        }
        catch (Exception e) {}

        //call the OnlineTrucks api to get list of Truck
        new SendGetJsonApi(this, "company", jsonParam,Token, new CallBackListener() {
            @Override
            public void onFinish(String response) {
                String result="",terms="";
                try {
                    JSONObject jsonin = new JSONObject(response);
                    result = jsonin.optString("result");
                    if (result.equals("success")){
                        Log.d("content","user checked");
                        terms = jsonin.optString("content");
                        info.setText(terms);
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



        switch (company_info){
            case "policy":
                title.setText("Truck Up Privacy & Policy Terms");
                break;
            case "about":
                title.setText("About Truck Up");
            case "terms":
                title.setText("Truck Up terms & conditions");

        }

    }
}
