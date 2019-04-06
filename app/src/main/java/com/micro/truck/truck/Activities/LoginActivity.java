package com.micro.truck.truck.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
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

/**
 * Created by Ya7ya on 11/3/2018.
 */

public class LoginActivity extends AppCompatActivityMenu {
    Button blogin,Rbutton;
    EditText EmailTxt, PasswordTxt;

    TextView Reslog,ForgetPassword;
    public int is_verified=0,/*Enabled=0,*/ UserId=0;
    public String Token="",FCM_Token="";
    ProgressBar pg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserId = readSharedPreferenceInt("UserId");
        Token = readSharedPreferenceString("token");
        is_verified = readSharedPreferenceInt("Verified");

        if(!Token.equals("") && is_verified == 1 /*&& Enabled == 1*/)
        {
            Log.d("content", "token and is verified");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else if(!Token.equals("") && is_verified == 0 /*&& Enabled == 1*/)
        {


            Log.d("content", "token and is not verified");

            startActivity(new Intent(this, ActivationActivity.class));
            finish();
        }
        else
        {

            Log.d("content", "no token and is not verified");

            setContentView(R.layout.activity_login);

            pg = (ProgressBar) findViewById(R.id.progressBar1);
            EmailTxt = (EditText) findViewById(R.id.email);
            PasswordTxt = (EditText) findViewById(R.id.password);
            blogin = (Button)findViewById(R.id.blogin);
            Reslog=(TextView) findViewById(R.id.Reslogin);
            Rbutton = (Button) findViewById(R.id.signup);
            ForgetPassword = (TextView) findViewById(R.id.forget_password);
            SpannableString content = new SpannableString(ForgetPassword.getText());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            ForgetPassword.setText(content);


        }
    }

    public void LoginBtnAction(View v)
    {
        Log.d("content", "login btn pressed");

        String email = EmailTxt.getText().toString().trim();
        String password = PasswordTxt.getText().toString().trim();

        if (email.equals("")) {
            EmailTxt.setError(getString(R.string.username_required));EmailTxt.requestFocus();  return;
        }

        if (password.equals("")) {
            PasswordTxt.setError(getString(R.string.password_required));PasswordTxt.requestFocus();  return;
        }


        blogin.setEnabled(false);
        blogin.setTextColor(Color.BLACK);

        pg.setVisibility(View.VISIBLE);

//        FCM_Token = readSharedPreferenceFCMString("FCM_Token");
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("email", email);
            jsonParam.put("password", password);
            jsonParam.put("type",1);
            //jsonParam.put("FCM_Token", FCM_Token);
            //jsonParam.put("Platform", "android");

        }
        catch (Exception e) {}

        Log.d("content", "login 1");

        new SendGetJsonApi(this,"login",jsonParam,
                new CallBackListener() {
                    @Override
                    public void onFinish(String resultjson)
                    {
                        String result="";
                        try {
                            // Create the root JSONObject from the JSON string.
                            JSONObject jsonin = new JSONObject(resultjson);

                            result = jsonin.optString("result");
                            Log.d("content", "login 2");
                            Log.d("content",String.valueOf(jsonin));

                            if (result.equals("success"))
                            {
                                JSONObject contentjson = new JSONObject(jsonin.optString("content"));
                                int user_id = contentjson.optInt("id");
                                String first_name = contentjson.optString("first_Name");
                                String last_name = contentjson.optString("last_Name");
                                String token = contentjson.optString("token");
                                String email = contentjson.optString("email");
                                int is_verified = contentjson.optInt("is_verified");

                                //if (active_status == 1)
                                //{
                                //when save success
                                writeSharedPreferenceInt("UserId",user_id);
                                writeSharedPreferenceString("FirstName",first_name);
                                writeSharedPreferenceString("LastName",last_name);
                                writeSharedPreferenceString("Email",email);
                                writeSharedPreferenceString("token",token);
                                writeSharedPreferenceInt("Verified",is_verified);
                                //writeSharedPreferenceInt("Activated",Active_Done);

                                if (is_verified == 1)
                                {
                                    Intent intent1 = new Intent(getBaseContext() , MainActivity.class);
                                    intent1.addFlags(intent1.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent1);
                                    finish();
                                }
                                else
                                {
                                    Intent intent2 = new Intent(getBaseContext() , ActivationActivity.class);
                                    intent2.addFlags(intent2.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent2);
                                    finish();
                                }
                            /*}
                            else
                            {
                                Reslog.setText(getString(R.string.user_not_enabled));
                                blogin.setEnabled(true);
                                blogin.setTextColor(Color.WHITE);
                            }*/

                            }
                            else
                            {
                                String error_des = jsonin.optString("error_des");
                                // try error_des
                                if(!error_des.equals("")){
                                    Reslog.setText(error_des);
                                }else
                                {
                                    Reslog.setText(getString(R.string.fail_try_agian));
                                }
                                //this.finish();
                            }

                        }
                        catch ( Exception e)
                        {
                            Reslog.setText(getString(R.string.fail_try_agian));
                            result = "error";
                            //this.finish();
                        }

                        if (!result.equals("success"))
                        {
                            blogin.setEnabled(true);
                            blogin.setTextColor(Color.WHITE);
                        }

                        //Make ProgressBar invisible
                        pg.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onProgress(int process) {
                    }
                }

        ).Execute();
    }

    public  void  ForgetPassword(View v){
        ForgetPassword.setTextColor(getResources().getColor(R.color.orange));
        startActivity(new Intent(getBaseContext() , ForgetActivity.class));
    }

    public void GoToRegister(View v)
    {
        Rbutton.setBackground(getResources().getDrawable(R.drawable.rounded_white));
        Rbutton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        startActivity(new Intent(getBaseContext() , RegisterActivity.class));

    }

}
