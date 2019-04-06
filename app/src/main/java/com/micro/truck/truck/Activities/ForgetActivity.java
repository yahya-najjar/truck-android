package com.micro.truck.truck.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.micro.truck.truck.Api.CallBackListener;
import com.micro.truck.truck.Api.SendGetJsonApi;
import com.micro.truck.truck.MainActivity;
import com.micro.truck.truck.Masters.AppCompatActivityMenu;
import com.micro.truck.truck.R;

import org.json.JSONObject;

public class ForgetActivity extends AppCompatActivityMenu {

    EditText EmailTxt;
    TextView Resactive;
    ProgressBar progressBar;
    Button SearchButton,CancelButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reset_password);

        Resactive =  findViewById(R.id.Resactive);
        EmailTxt = findViewById(R.id.email_search);
        SearchButton = findViewById(R.id.search_btn);
        CancelButton = findViewById(R.id.cancel_search);
        progressBar = findViewById(R.id.progressBar2);

    }

    public void ResetPassword(View view){
        String email = EmailTxt.getText().toString().trim();

        if (email.equals("")) {
            EmailTxt.setError(getString(R.string.username_required));EmailTxt.requestFocus();  return;
        }

        SearchButton.setEnabled(false);
        SearchButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        SearchButton.setBackground(getResources().getDrawable(R.drawable.rounded_white));

        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("email", email);
            //jsonParam.put("FCM_Token", FCM_Token);
            //jsonParam.put("Platform", "android");

        }
        catch (Exception e) {}

        new SendGetJsonApi(this,"resetPassword",jsonParam,
                new CallBackListener() {
                    @Override
                    public void onFinish(String resultjson)
                    {
                        String result="";
                        try {
                            // Create the root JSONObject from the JSON string.
                            JSONObject jsonin = new JSONObject(resultjson);

                            result = jsonin.optString("result");
                            Log.d("content", "Reset password response");

                            if (result.equals("success"))
                            {
                                JSONObject contentjson = new JSONObject(jsonin.optString("content"));
                                writeSharedPreferenceString("Email",email);
                                    startActivity(new Intent(getBaseContext() , CheckResetActivity.class));

                            }
                            else
                            {
                                String error_des = jsonin.optString("error_des");
                                // try error_des
                                if(!error_des.equals("")){
                                    Resactive.setText(error_des);
                                }else
                                {
                                    Resactive.setText(getString(R.string.fail_try_agian));
                                }
                                //this.finish();
                            }

                        }
                        catch ( Exception e)
                        {
                            Resactive.setText(getString(R.string.fail_try_agian));
                            result = "error";
                            //this.finish();
                        }

                        if (!result.equals("success"))
                        {
                            Resactive.setEnabled(true);
                            Resactive.setTextColor(Color.WHITE);
                        }

                        //Make ProgressBar invisible
                        progressBar.setVisibility(View.INVISIBLE);
                        SearchButton.setEnabled(true);
                        SearchButton.setTextColor(Color.WHITE);
                        SearchButton.setBackground(getResources().getDrawable(R.drawable.rounded));


                    }

                    @Override
                    public void onProgress(int process) {
                    }
                }

        ).Execute();
    }

    public void CancelResetPassword(View view){
        CancelButton.setTextColor(getResources().getColor(R.color.orange));
        CancelButton.setBackground(getResources().getDrawable(R.drawable.rounded_white));
        finish();
    }

}
