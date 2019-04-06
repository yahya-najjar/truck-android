package com.micro.truck.truck.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.micro.truck.truck.Api.CallBackListener;
import com.micro.truck.truck.Api.SendGetJsonApi;
import com.micro.truck.truck.MainActivity;
import com.micro.truck.truck.Masters.AppCompatActivityMenu;
import com.micro.truck.truck.R;

import org.json.JSONObject;

public class CheckResetActivity extends AppCompatActivityMenu {
    PinView resetCode;
    EditText passwordText,confirmPasswordText;
    TextView resetError;
    Button bCheck;
    ProgressBar pg;

    String reset_code,Token,Email,password,password_confirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_check_reset);

        bCheck = findViewById(R.id.bCheck);
        resetCode = findViewById(R.id.reset_code);
        passwordText = findViewById(R.id.new_password);
        confirmPasswordText = findViewById(R.id.new_password_confirm);
        resetError = findViewById(R.id.reset_error);
        pg = findViewById(R.id.progressBar3);
    }

    public void CheckResetCode(View view){
        reset_code = resetCode.getText().toString().trim();
        password = passwordText.getText().toString().trim();
        password_confirm = confirmPasswordText.getText().toString().trim();

        if (reset_code.equals("")) {
            resetCode.setError(getString(R.string.code_required));resetCode.requestFocus();  return;
        }


        bCheck.setEnabled(false);
        bCheck.setBackground(getResources().getDrawable(R.drawable.rounded_white));
        bCheck.setTextColor(getResources().getColor(R.color.colorPrimaryDark));


        if (password.equals("")) {
            passwordText.setError(getString(R.string.password_required));passwordText.requestFocus();  return;
        }

        if (password.length() < 6) {
            passwordText.setError(getString(R.string.password_length));passwordText.requestFocus();  return;
        }

        if (!password.equals(password_confirm)) {
            confirmPasswordText.setError(getString(R.string.password_confirm_not_equal_password));confirmPasswordText.requestFocus();  return;
        }

        JSONObject jsonParam = new JSONObject();
        Token = readSharedPreferenceString("token");
        Email = readSharedPreferenceString("Email");

        Log.d("content", "Reset token");
        Log.d("content", Token);
        Log.d("content", "Reset email");
        Log.d("content", Email);



        try {
            jsonParam.put("new_password", password);
            jsonParam.put("email", Email);
            jsonParam.put("reset_code",reset_code);

        }catch (Exception e) {}

        new SendGetJsonApi(this,"reset",jsonParam,
                new CallBackListener() {
                    @Override
                    public void onFinish(String resultjson)
                    {
                        String result="";
                        try {
                            // Create the root JSONObject from the JSON string.
                            JSONObject jsonin = new JSONObject(resultjson);

                            result = jsonin.optString("result");
                            Log.d("content", "Reset response");

                            if (result.equals("success"))
                            {
                                JSONObject contentjson = new JSONObject(jsonin.optString("content"));
                                Log.d("content",String.valueOf(contentjson));
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
                                    startActivity(new Intent(getBaseContext() , MainActivity.class));
                                    finish();
                                }
                                else
                                {
                                    startActivity(new Intent(getBaseContext() , ActivationActivity.class));
                                    finish();
                                }
                            }
                            else
                            {
                                String error_des = jsonin.optString("error_des");
                                // try error_des
                                if(!error_des.equals("")){
                                    resetError.setText(error_des);
                                }else
                                {
                                    resetError.setText(getString(R.string.fail_try_agian));
                                }
                                //this.finish();
                            }

                        }
                        catch ( Exception e)
                        {
                            resetError.setText(getString(R.string.fail_try_agian));
                            result = "error";
                            //this.finish();
                        }

                        if (!result.equals("success"))
                        {
                            resetError.setEnabled(true);
                            resetError.setTextColor(Color.WHITE);
                        }

                        //Make ProgressBar invisible
                        pg.setVisibility(View.INVISIBLE);
                        bCheck.setEnabled(true);
                        bCheck.setBackground(getResources().getDrawable(R.drawable.rounded));
                        bCheck.setTextColor(Color.WHITE);

                    }

                    @Override
                    public void onProgress(int process) {
                    }
                }

        ).Execute();

    }
}
