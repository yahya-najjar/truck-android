package com.micro.truck.truck.Masters;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.micro.truck.truck.Utils.LoadingDialog;

import static com.micro.truck.truck.Masters.AppCompatActivityMenu.APP_PREFS;


public abstract class MasterFragment extends Fragment {

    private View view;
    private int layoutId;
    private LoadingDialog mLoadingDialog;


    ///////////////////
    //Read from Shared Preferance (INTEGER)
    public int readSharedPreferenceInt(String key) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }
    //Read from Shared Preferance (String)
    public String readSharedPreferenceString(String key){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }
    ///////////////////

    //write shared preferences in integer
    public void writeSharedPreferenceInt(String key , int value) {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, value);
        editor.commit();
    }
    //write shared preferences in String
    public void writeSharedPreferenceString(String key , String value ){

        SharedPreferences sharedPrefereSt = getContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefereSt.edit();

        editor.putString(key, value);
        editor.commit();
    }
}
