package com.micro.truck.truck.Firebase;

import android.content.Context;
import android.content.SharedPreferences;


import com.micro.truck.truck.Api.CallBackListener;
import com.micro.truck.truck.Api.SendGetJsonApi;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by khaled on 10/31/2017.
 */

public class MyFirebaseSendTokenToServer
{
    private int UserId=0;
    private String Token = "",FCM_Token="";
    private Context myContext;

    public MyFirebaseSendTokenToServer(Context context,int userId,String token,String fCM_Token)
    {
        UserId = userId;
        Token = token;
        FCM_Token = fCM_Token;
        myContext = context;
    }

    public void Execute()
    {
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("UserId", UserId);
            jsonParam.put("Token", Token);
            jsonParam.put("Platform", "android");
            jsonParam.put("FCM_Token", FCM_Token);
        }
        catch (Exception e) {}

        new SendGetJsonApi(myContext,"Dealers/TokenUser",jsonParam,
                new CallBackListener() {
                    @Override
                    public void onFinish(String resultjson) {
                        try {
                            // Create the root JSONObject from the JSON string.
                            JSONObject jsonin = new JSONObject(resultjson);
                            String result = jsonin.optString("result");
                            if (result.equals("success"))
                            {
                                if (myContext != null)
                                {
                                    writeSharedPreferenceFCMInt("TokenSent",1);
                                }
                            }

                        }
                        catch ( Exception e) {}
                    }
                    @Override
                    public void onProgress(int precess)
                    {
                    }
                }
        ).Execute();
    }


    //write shared preferences in String
    private void writeSharedPreferenceFCMInt(String key , int value) {

        SharedPreferences sharedPreferences = myContext.getSharedPreferences(MyFirebaseMessagingService.APP_PREFS_FCM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, value);
        editor.commit();
    }
}