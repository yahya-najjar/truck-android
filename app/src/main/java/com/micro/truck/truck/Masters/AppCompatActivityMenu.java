package com.micro.truck.truck.Masters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.micro.truck.truck.BuildConfig;
import com.micro.truck.truck.Firebase.MyFirebaseMessagingService;

import android.os.Build;
import android.view.View;


import java.util.Locale;

/**
 * Created by Ya7ya on 11/3/2018.
 */

public class AppCompatActivityMenu extends AppCompatActivity {

    public static final String APP_PREFS = "TruckApp";

    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;

    public static String curr_lang = "en";


    @Override
    protected void attachBaseContext(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_PREFS+"_lang",Context.MODE_PRIVATE);
        String lang =  sharedPreferences.getString("lang","");
        String sys_lang = Locale.getDefault().getLanguage();

        if (lang.equals("ar") || lang.equals("en")) curr_lang = lang;
        else  curr_lang = sys_lang;

        if (!curr_lang.equals(sys_lang) && !lang.equals(""))
        {
            Configuration config = context.getResources().getConfiguration();
            Locale locale = new Locale(curr_lang);
            Locale.setDefault(locale);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale);
            } else {
                config.locale = locale;
            }

            if (Build.VERSION.SDK_INT >= 25) {
                context = context.createConfigurationContext(config);

            } else {
                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            }
        }

        super.attachBaseContext(context);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (curr_lang.equals("ar")){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }


    ///////////////////
    //Read from Shared Preferance (INTEGER)
    public int readSharedPreferenceInt(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }
    //Read from Shared Preferance (String)
    public String readSharedPreferenceString(String key){
        SharedPreferences sharedPreferences = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }
    ///////////////////

    //write shared preferences in integer
    public void writeSharedPreferenceInt(String key , int value) {

        SharedPreferences sharedPreferences = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, value);
        editor.commit();
    }
    //write shared preferences in String
    public void writeSharedPreferenceString(String key , String value ){

        SharedPreferences sharedPrefereSt = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefereSt.edit();

        editor.putString(key, value);
        editor.commit();
    }
    ///////////////////////////////////////////  read FCM
    //Read from Shared Preferance (INTEGER)
    public int readSharedPreferenceFCMInt(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(MyFirebaseMessagingService.APP_PREFS_FCM, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }
    //Read from Shared Preferance (String)
    public String readSharedPreferenceFCMString(String key){
        SharedPreferences sharedPreferences = getSharedPreferences(MyFirebaseMessagingService.APP_PREFS_FCM, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }
    ///////////////////

    // change language
    private void ChangeLanguage()
    {
        //get current

        if (curr_lang.equals("ar"))
        {
            curr_lang = "en";
        }
        else
        {
            curr_lang = "ar";
        }


        SharedPreferences sharedPrefereSt = getSharedPreferences(APP_PREFS + "_lang", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefereSt.edit();
        editor.putString("lang", curr_lang);
        editor.commit();


        finish();
        startActivity(getIntent());
    }
}

