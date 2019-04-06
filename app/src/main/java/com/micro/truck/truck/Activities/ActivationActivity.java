package com.micro.truck.truck.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.chaos.view.PinView;
import com.micro.truck.truck.Api.CallBackListener;
import com.micro.truck.truck.MainActivity;
import com.micro.truck.truck.Masters.AppCompatActivityMenu;
import com.micro.truck.truck.R;
import com.micro.truck.truck.Api.SendGetJsonApi;

import org.json.JSONObject;

/**
 * Created by Ya7ya on 11/3/2018.
 */

public class ActivationActivity extends AppCompatActivityMenu {
    PinView etvlidation;
    TextView mobileTxt;
    Button bRegister,ClearCashBtn,ResendActiveCodeBtn;
    public int UserId=0;


    TextView Resactive;
    //
    public String Acode = "",Email="",Token="";

    ProgressBar pg;
    ColorStateList colorliststate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        Log.d("content", "this is activation acctivity");

        setContentView(R.layout.activity_activation);

        UserId = readSharedPreferenceInt("UserId");
        Token = readSharedPreferenceString("Token");
        Email = readSharedPreferenceString("Email");


        //retrive name
        ///

        Resactive = (TextView) findViewById(R.id.Resactive);
        etvlidation=(PinView)  findViewById(R.id.pinView);
        mobileTxt = (TextView) findViewById(R.id.mobileTxt);

        pg = (ProgressBar) findViewById(R.id.progressBar1);

        bRegister = (Button)findViewById(R.id.bRegister);
        ResendActiveCodeBtn = (Button)findViewById(R.id.ResendActiveCodeBtn);
        ClearCashBtn = (Button) findViewById(R.id.ClearCashBtn);

//        SpannableString content = new SpannableString(ResendActiveCodeBtn.getText());
//        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
//        ResendActiveCodeBtn.setText(content);
        colorliststate = ResendActiveCodeBtn.getTextColors();

//        SpannableString content1 = new SpannableString(ClearCashBtn.getText());
//        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
//        ClearCashBtn.setText(content1);


        mobileTxt.setText(getString(R.string.cur_email) + " " + Email);

//        if (isNetworkConnected(this)) {
//            CheckVersion();
//
//            // check if token not sent
//            int  TokenSent = readSharedPreferenceFCMInt("TokenSent");
//            if (TokenSent != 1)
//            {
//                String FCM_Token = readSharedPreferenceFCMString("FCM_Token");
//                if(UserId > 0 && !Token.equals("") && !FCM_Token.equals(""))
//                {
//                    new MyFirebaseSendTokenToServer(this,UserId,Token,FCM_Token).Execute();
//                }
//            }
//        }
    }

    public void ActivationAction(View v) {

        Resactive.setText("");

        Acode = etvlidation.getText().toString().trim();

        if (Acode.equals("")) {
            etvlidation.setError(getString(R.string.code_required));etvlidation.requestFocus();  return;
        }

        bRegister.setEnabled(false);
        ResendActiveCodeBtn.setEnabled(false);
        ClearCashBtn.setEnabled(false);
        bRegister.setTextColor(Color.GRAY);
        ResendActiveCodeBtn.setTextColor(Color.GRAY);
        ClearCashBtn.setTextColor(Color.GRAY);

        pg.setVisibility(View.VISIBLE);


        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("user_id", UserId);
            jsonParam.put("email", Email);
            jsonParam.put("code", Acode);

        }
        catch (Exception e) {}

        new SendGetJsonApi(this,"verify",jsonParam,
                new CallBackListener() {
                    @Override
                    public void onFinish(String resultjson)
                    {
                        try {
                            // Create the root JSONObject from the JSON string.
                            JSONObject jsonin = new JSONObject(resultjson);

                            String result = jsonin.optString("result");
                            if (result.equals("success"))
                            {
                                writeSharedPreferenceInt("Verified",1);
                                Toast.makeText(ActivationActivity.this,getString(R.string.activation_done), Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ActivationActivity.this, MainActivity.class));
                                finish();
                            }
                            else
                            {
                                String error_des = jsonin.optString("error_des");
                                int error_code = jsonin.optInt("error_code");
                                // try error_des

                                if (error_code == -1)
                                {
                                    Toast.makeText(ActivationActivity.this, error_des, Toast.LENGTH_LONG).show();
                                    getSharedPreferences(APP_PREFS, MODE_PRIVATE).edit().clear().apply();

                                    startActivity(new Intent(ActivationActivity.this, LoginActivity.class));
                                    finish();
                                }
                                else if(!error_des.equals(""))
                                {
                                    Resactive.setText(error_des);
                                }else
                                {
                                    Resactive.setText(getString(R.string.fail_try_agian));
                                }

                            }

                        }
                        catch ( Exception e)
                        {
                            Resactive.setText(getString(R.string.fail_try_agian));
                        }

                        bRegister.setEnabled(true);
                        ResendActiveCodeBtn.setEnabled(true);
                        ClearCashBtn.setEnabled(true);
                        bRegister.setTextColor(Color.WHITE);
                        ResendActiveCodeBtn.setTextColor(colorliststate);
                        ClearCashBtn.setTextColor(colorliststate);

                        pg.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onProgress(int process) {
                    }
                }

        ).Execute();

    }


    public void ResendActiveCodeAction(View v) {

        Resactive.setText("");

        bRegister.setEnabled(false);
        ResendActiveCodeBtn.setEnabled(false);
        ClearCashBtn.setEnabled(false);
        ResendActiveCodeBtn.setBackground(getResources().getDrawable(R.drawable.rounded_white));
        ResendActiveCodeBtn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        pg.setVisibility(View.VISIBLE);


        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("id", UserId);
            jsonParam.put("Token", Token);
            jsonParam.put("email", Email);
        }
        catch (Exception e) {}


        new SendGetJsonApi(this,"recover",jsonParam,
                new CallBackListener() {
                    @Override
                    public void onFinish(String resultjson)
                    {
                        try {
                            // Create the root JSONObject from the JSON string.
                            JSONObject jsonin = new JSONObject(resultjson);

                            String result = jsonin.optString("result");
                            if (result.equals("success"))
                            {
                                Resactive.setText(getString(R.string.activation_code_resent_please_wait));
                            }
                            else
                            {
                                String error_des = jsonin.optString("error_des");
                                int error_code = jsonin.optInt("error_code");
                                // try error_des

                                if (error_code == -1)
                                {
                                    Toast.makeText(ActivationActivity.this, error_des, Toast.LENGTH_LONG).show();
                                    getSharedPreferences(APP_PREFS, MODE_PRIVATE).edit().clear().apply();

                                    startActivity(new Intent(ActivationActivity.this, LoginActivity.class));
                                    finish();
                                }
                                else if(!error_des.equals(""))
                                {
                                    Resactive.setText(error_des);
                                }else
                                {
                                    Resactive.setText(getString(R.string.fail_try_agian));
                                }

                            }

                        }
                        catch ( Exception e)
                        {
                            Resactive.setText(getString(R.string.fail_try_agian));
                        }

                        bRegister.setEnabled(true);
                        ResendActiveCodeBtn.setEnabled(true);
                        ClearCashBtn.setEnabled(true);
//                        bRegister.setTextColor(Color.WHITE);
//                        ResendActiveCodeBtn.setTextColor(colorliststate);
//                        ClearCashBtn.setTextColor(colorliststate);

                        pg.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onProgress(int process) {
                    }
                }

        ).Execute();


    }

    public void ClearCashAction(View v)
    {
        ClearCashBtn.setBackground(getResources().getDrawable(R.drawable.rounded_white));
        ClearCashBtn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.are_sure_to_re_register));
        dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                getSharedPreferences(APP_PREFS, MODE_PRIVATE).edit().clear().commit();

                startActivity(new Intent(ActivationActivity.this, RegisterActivity.class));
                finish();
            }
        });
        dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Action for "Cancel".
                dialog.cancel();
            }
        });

        final AlertDialog alert = dialog.create();
        alert.show();



    }

}
