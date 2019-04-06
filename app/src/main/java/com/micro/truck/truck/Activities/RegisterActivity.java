package com.micro.truck.truck.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.micro.truck.truck.Api.CallBackListener;
import com.micro.truck.truck.Masters.AppCompatActivityMenu;
import com.micro.truck.truck.R;
import com.micro.truck.truck.Api.SendGetJsonApi;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Ya7ya on 11/3/2018.
 */

public class RegisterActivity extends AppCompatActivityMenu {

    Button RegisterBtn;
    EditText FirstNameTxt,LastNameTxt, EmailTxt, PasswordTxt,PasswordConfirmTxt,MobNumberTxt,AgeNumberTxt,birthdayEditText;
    TextView Reslog,NewsFeed;
    LinearLayout ln;
    int gender;
    boolean news = true;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    CheckBox TermsCheckBox;
    RadioGroup genderRadioGroup;
    String FCM_Token;

    ProgressBar pg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        pg = (ProgressBar) findViewById(R.id.progressBar1);
        FirstNameTxt = (EditText) findViewById(R.id.first_name);
        LastNameTxt = (EditText) findViewById(R.id.last_name);
        EmailTxt = (EditText) findViewById(R.id.email);
        PasswordTxt = (EditText) findViewById(R.id.password);
        PasswordConfirmTxt = (EditText) findViewById(R.id.password_confirm);
        MobNumberTxt = (EditText) findViewById(R.id.mob_number);
//        AgeNumberTxt = (EditText) findViewById(R.id.age);
        NewsFeed = (TextView) findViewById(R.id.news_feed);
        ln = (LinearLayout) findViewById(R.id.news_f_bg);
        RegisterBtn = (Button)findViewById(R.id.registerBtn);
        RegisterBtn.setClickable(false);
        RegisterBtn.setAlpha(.5f);
        birthdayEditText = findViewById(R.id.birth_date);
        birthdayEditText.setInputType(InputType.TYPE_NULL);
        genderRadioGroup = findViewById(R.id.gender_radioGroup);
        TermsCheckBox = findViewById(R.id.terms_cons);



        Reslog=(TextView) findViewById(R.id.Reslogin);

        float dip = 23f;
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

        int margin = Math.round(px);

        TermsCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TermsCheckBox.isChecked()){
                    RegisterBtn.setAlpha(1f);
                    RegisterBtn.setEnabled(true);
                    RegisterBtn.setClickable(true);
                }
                else{
                    RegisterBtn.setAlpha(.5f);
                    RegisterBtn.setEnabled(false);
                    RegisterBtn.setClickable(false);
                }
            }
        });

        ln.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (news){
                    Log.d("content","true");
                    news = false;
//                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) NewsFeed.getLayoutParams();
//                    params.setMargins(margin,0,0,0);
//                    NewsFeed.setLayoutParams(params);

                    Animation a = new Animation() {

                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) NewsFeed.getLayoutParams();
                            params.leftMargin = (int)(margin * interpolatedTime);
                            NewsFeed.setLayoutParams(params);
                        }
                    };
                    a.setDuration(300); // in ms
                    NewsFeed.startAnimation(a);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ln.setBackgroundResource(R.drawable.grey_roundshape);
                            NewsFeed.setBackgroundResource(R.drawable.light_grey_roundshape);
                        }
                    }, 300);


                }
                else{
                    Log.d("content","false");
                    news = true;
//                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) NewsFeed.getLayoutParams();
//                    params.setMargins(0,0,0,0);
//                    NewsFeed.setLayoutParams(params);

                    Animation a = new Animation() {

                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) NewsFeed.getLayoutParams();
                            params.leftMargin = (int)(0 * interpolatedTime);
                            NewsFeed.setLayoutParams(params);
                        }
                    };
                    a.setDuration(300); // in ms
                    NewsFeed.startAnimation(a);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ln.setBackgroundResource(R.drawable.blue_roundshape);
                            NewsFeed.setBackgroundResource(R.drawable.white_roundshape);
                        }
                    }, 300);
                }
            }
        });

        birthdayEditText.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                    android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth,
                    mDateSetListener,
                    1980, 1, 1);
            datePickerDialog.getDatePicker().getTouchables().get(0).performClick();
            Calendar calendar = Calendar.getInstance();
            calendar.set(2000, 0, 1);
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
            calendar.set(1940, 1, 1);
            datePickerDialog.getDatePicker().setMinDate(calendar.getTime().getTime());
            datePickerDialog.show();
        });

        birthdayEditText.setOnFocusChangeListener((view, b) -> {
            if (b) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth,
                        mDateSetListener,
                        1980, 1, 1);
                datePickerDialog.getDatePicker().getTouchables().get(0).performClick();
                Calendar calendar = Calendar.getInstance();
                calendar.set(2000, 0, 1);
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
                calendar.set(1940, 0, 1);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTime().getTime());
                datePickerDialog.show();
            }
        });

        mDateSetListener = (datePicker, i, i1, i2) -> birthdayEditText.setText(i2 + "/" + (i1 + 1) + "/" + i);

        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.male_radioButton) {
                   gender = 1;
                } else {
                    gender = 0;
                }
            }
        });
    }

    public void RegisterBtnAction(View v)
    {
        String first_name = FirstNameTxt.getText().toString().trim();
        String last_name = LastNameTxt.getText().toString().trim();
        String email = EmailTxt.getText().toString().trim();
        String password = PasswordTxt.getText().toString().trim();
        String password_confirm = PasswordConfirmTxt.getText().toString().trim();
        String mobile = MobNumberTxt.getText().toString().trim();
        String dob = birthdayEditText.getText().toString().trim();



        if (first_name.equals("")) {
            FirstNameTxt.setError(getString(R.string.first_name_required));FirstNameTxt.requestFocus();  return;
        }
        if (last_name.equals("")) {
            LastNameTxt.setError(getString(R.string.last_name_required));LastNameTxt.requestFocus();  return;
        }

        if (email.equals("")) {
            EmailTxt.setError(getString(R.string.username_required));EmailTxt.requestFocus();  return;
        }

        if (password.equals("")) {
            PasswordTxt.setError(getString(R.string.password_required));PasswordTxt.requestFocus();  return;
        }

        if (password.length() < 6) {
            PasswordTxt.setError(getString(R.string.password_length));PasswordTxt.requestFocus();  return;
        }

        if (!password.equals(password_confirm)) {
            PasswordConfirmTxt.setError(getString(R.string.password_confirm_not_equal_password));PasswordConfirmTxt.requestFocus();  return;
        }

        Log.d("content", "register button");


        RegisterBtn.setEnabled(false);
        RegisterBtn.setTextColor(Color.GRAY);

        pg.setVisibility(View.VISIBLE);

        FCM_Token = readSharedPreferenceFCMString("FCM_Token");
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("first_name", first_name);
            jsonParam.put("last_name", last_name);
            jsonParam.put("email", email);
            jsonParam.put("password", password);
            jsonParam.put("phone", mobile);
            jsonParam.put("dob", dob);
            jsonParam.put("gender", gender);
            jsonParam.put("type", 1);
            jsonParam.put("FCM_Token", FCM_Token);
            jsonParam.put("Platform", "android");

        }
        catch (Exception e) {}

        Log.d("content", "after json init");


        new SendGetJsonApi(this,"register",jsonParam,
                new CallBackListener() {
                    @Override
                    public void onFinish(String resultjson)
                    {

                        Log.d("content", "request send");

                        String result="";
                        try {
                            // Create the root JSONObject from the JSON string.
                            JSONObject jsonin = new JSONObject(resultjson);

                            Log.d("content", "request send and json recieved 1");


                            result = jsonin.optString("result");

                            Log.d("content", "request send and json recieved 2");


                            if (result.equals("success"))
                            {
                                JSONObject contentjson = new JSONObject(jsonin.optString("content"));
                                Log.d("content", String.valueOf(contentjson));

                                int user_id = contentjson.optInt("id");
                                String first_name = contentjson.optString("first_name");
                                String last_name = contentjson.optString("last_name");
                                String token = contentjson.optString("token");
                                String email = contentjson.optString("email");
                                String phone = contentjson.optString("phone");
                                int active = contentjson.optInt("active");
                                int is_verified = contentjson.optInt("is_verified");
                                int registration_completed = contentjson.optInt("registration_completed");


                                //when save success
                                writeSharedPreferenceInt("UserId",user_id);
                                writeSharedPreferenceString("FirstName",first_name);
                                writeSharedPreferenceString("LastName",last_name);
                                writeSharedPreferenceString("token",token);
                                writeSharedPreferenceString("Email",email);
                                writeSharedPreferenceString("Phone",phone);
                                writeSharedPreferenceInt("Active",active);
                                writeSharedPreferenceInt("Verified",is_verified);
                                writeSharedPreferenceInt("Completed",registration_completed);

                                Log.d("content", "i'm going to activation");


                                startActivity(new Intent(getBaseContext() ,ActivationActivity.class));
                                finish();

                            }
                            else
                            {
                                String error_des = jsonin.optString("error_des");
//                                Log.d("content", "this is error else");
//                                Log.d("content", error_des);

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
                            RegisterBtn.setEnabled(true);
                            RegisterBtn.setTextColor(Color.WHITE);
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

    public void GoToTermsAction(View view){
        Intent intent = new Intent(this, CompanyActivity.class);
        intent.putExtra("company","policy");
        startActivity(intent);
    }
}
