package com.micro.truck.truck.Api;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.micro.truck.truck.MainActivity;
import com.micro.truck.truck.BuildConfig;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ya7ya on 11/3/2018.
 */

public class SendGetJsonApi  {

    private String ApiUrl;
    private JSONObject JsonSend;
    private  String token;

    private int ConnectTimeout = 15000;
    private int ReadTimeout = 30000;


    private Map<String[],InputStream> Files = new HashMap<String[], InputStream>();

    private boolean isUploadFiles = false;

    private CallBackListener callBackListener;

    public boolean isCanceled = false;

    private HttpURLConnection urlConnection;

    private  Context context;


    public static String WebSite()
    {
        if (BuildConfig.BUILD_TYPE.contains("demo"))
        {
//            return "http://94.206.36.74/truck/api";
             return "http://192.168.1.7/Truck_App/public/api/";
        }
        else
        {
            return "http://go-dg.co/truck/api/";
//            return "http://188.160.3.18:96/truck/public/api/";
//            return  "http://192.168.170.32:8000/api/";
//            return  "http://10.0.0.14:8000/api/";
//            return "http://172.20.10.4:8000/api/";
//            return  "http://192.168.1.14:8000/api/";
        }
    }



    public SendGetJsonApi (Context context, String apiUrl, JSONObject jsonSend,CallBackListener callBackListener) {

        this.context = context;
        this.ApiUrl = apiUrl;
        this.JsonSend = jsonSend;
        this.callBackListener = callBackListener;
    }

    public SendGetJsonApi (Context context, String apiUrl, JSONObject jsonSend,String token,CallBackListener callBackListener) {

        this.context = context;
        this.ApiUrl = apiUrl;
        this.JsonSend = jsonSend;
        this.callBackListener = callBackListener;
        this.token = token;
    }

    public SendGetJsonApi (Context context, String apiUrl, JSONObject jsonSend ,int connectTimeout , int readTimeout,CallBackListener callBackListener ) {

        this.context = context;
        this.ApiUrl = apiUrl;
        this.JsonSend = jsonSend;

        this.ConnectTimeout = connectTimeout;
        this.ReadTimeout = readTimeout;
        this.callBackListener = callBackListener;
    }

    public SendGetJsonApi (Context context, String apiUrl, JSONObject jsonSend, Map<String[],InputStream> files, CallBackListener callBackListener ) {

        this.context = context;
        this.ApiUrl = apiUrl;
        this.JsonSend = jsonSend;
        this.Files = files;
        this.isUploadFiles = true;
        this.callBackListener = callBackListener;
    }


    public void Execute()
    {
        asyncTask atask = new asyncTask();
        atask.execute();
    }


    private class asyncTask extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            StringBuilder resultjosn = new StringBuilder();
            String link = WebSite() + ApiUrl;
            try {
                URL url = new URL(link);
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("POST");



                urlConnection.setRequestProperty("X-localization", MainActivity.curr_lang);
                urlConnection.setRequestProperty("Authorization","Bearer " + token );

                if (!isUploadFiles) {
                    urlConnection.setConnectTimeout(ConnectTimeout);
                    urlConnection.setReadTimeout(ReadTimeout);

                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");

                    urlConnection.connect(); // Note the connect() here


                    OutputStream os = urlConnection.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                    osw.write(JsonSend.toString());

                    osw.flush();
                    osw.close();

                } else {
                    uploadFilesAndData();
                }


                InputStream in;

                int status = urlConnection.getResponseCode();

                if (status == 200) {
                    in = new BufferedInputStream(urlConnection.getInputStream());
                } else {
                    in = urlConnection.getErrorStream();
                }


                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    resultjosn.append(line);
                }


            } catch (Exception e) {
                return new String("{'result':'error','error_des':'" + e.getMessage() + "'}");
            } finally {
                urlConnection.disconnect();
            }
            return resultjosn.toString();

        }


        private void uploadFilesAndData() throws Exception {

        }

        @Override
        protected void onPostExecute(String resultjson)
        {
            if ( context instanceof Activity) {
                Activity activity = (Activity)context;
                if ( activity.isFinishing() ) {
                    return;
                }
            }
            callBackListener.onFinish(resultjson);

        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            if ( context instanceof Activity) {
                Activity activity = (Activity)context;
                if ( activity.isFinishing() ) {
                    return;
                }
            }

            callBackListener.onProgress(values[0]);

        }
    }

}
