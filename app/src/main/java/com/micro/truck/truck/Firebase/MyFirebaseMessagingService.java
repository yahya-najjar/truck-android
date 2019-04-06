package com.micro.truck.truck.Firebase;

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
        import android.app.NotificationChannel;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Color;
        import android.media.RingtoneManager;
        import android.net.Uri;
        import android.support.v4.app.NotificationCompat;
        import android.util.Log;


        import com.google.firebase.messaging.FirebaseMessaging;
        import com.google.firebase.messaging.FirebaseMessagingService;
        import com.google.firebase.messaging.RemoteMessage;
        import com.micro.truck.truck.BuildConfig;
        import com.micro.truck.truck.MainActivity;
        import com.micro.truck.truck.Masters.AppCompatActivityMenu;
        import com.micro.truck.truck.R;

        import java.util.Locale;
        import java.util.concurrent.atomic.AtomicInteger;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private final static AtomicInteger c = new AtomicInteger(0);

    public static final String APP_PREFS_FCM = "Truck_FCM";

    public int UserId=0;
    public String Token = "",FCM_Token="";
    /**
     * Called when message is received.
     *
     * param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        //Log.d("NEW_TOKEN",s);
        if (BuildConfig.BUILD_TYPE.contains("demo"))
        {
            FirebaseMessaging.getInstance().subscribeToTopic("all-users-demo");
        }
        else
        {
            FirebaseMessaging.getInstance().subscribeToTopic("all-users");
        }
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        // reset token
        writeSharedPreferenceFCMInt("TokenSent",0);

        if (refreshedToken != null && !refreshedToken.equals("")) {
            // save new token
            writeSharedPreferenceFCMString("FCM_Token", refreshedToken);
            // send to server
            sendRegistrationToServer(refreshedToken);
        }
    }
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.

        UserId = readSharedPreferenceInt("UserId");
        Token = readSharedPreferenceString("Token");

        FCM_Token= token;

        if(UserId > 0 && !Token.equals(""))
        {
            new MyFirebaseSendTokenToServer(this,UserId,Token,FCM_Token).Execute();
        }


    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        // sender id 265903541033

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            /*if ( Check if data needs to be processed by long running job  true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
                handleNow();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }*/
            sendNotification(remoteMessage.getData().get("body"),remoteMessage.getData().get("bodyAr"), remoteMessage.getData().get("action"));

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody(),null,"");
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    /*private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }*/




    //Read from Shared Preferance (INTEGER)
    private int readSharedPreferenceInt(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(AppCompatActivityMenu.APP_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }
    //Read from Shared Preferance (String)
    private String readSharedPreferenceString(String key){
        SharedPreferences sharedPreferences = getSharedPreferences(AppCompatActivityMenu.APP_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }



    //write shared preferences in integer
    private void writeSharedPreferenceFCMInt(String key , int value) {

        SharedPreferences sharedPreferences = getSharedPreferences(APP_PREFS_FCM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, value);
        editor.commit();
    }
    //write shared preferences in String
    private void writeSharedPreferenceFCMString(String key , String value ){

        SharedPreferences sharedPrefereSt = getSharedPreferences(APP_PREFS_FCM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefereSt.edit();

        editor.putString(key, value);
        editor.commit();
    }







    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody ,String messageBodyAr, String action) {

        if (messageBody==null) messageBody="";
        if (messageBodyAr==null) messageBodyAr = messageBody;


        SharedPreferences sharedPreferences = getSharedPreferences(AppCompatActivityMenu.APP_PREFS + "_lang", Context.MODE_PRIVATE);
        String lang =  sharedPreferences.getString("lang","");
        String sys_lang = Locale.getDefault().getLanguage();
        if (lang.equals("ar") || sys_lang.equals("ar")) messageBody = messageBodyAr;


        if (action==null) action="";

        Intent intent;
        if (action.equals("show_my_view" ))
        {
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("FCM_ACTION", "show_my_view");
        }
        else if (action.equals("show_votes" ))
        {
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("FCM_ACTION", "show_votes");
        }
        else
        {
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);



        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) return;

        String CHANNEL_ID  = "fcm_default_channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }


        //String channelId = getString(R.string.default_notification_channel_id);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this,CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)

                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);



        notificationManager.notify(c.incrementAndGet() /* ID of notification */, notificationBuilder.build());
    }


}