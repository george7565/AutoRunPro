package com.george.autorunpro;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Akshay on 22-Mar-16.
 */
public class Notification_Service {


    private Context context;
    private String title;
    private String content;
    private SharedPreferences prefs;
    //setting vibrating pattern
    private static final long[] CYCLES = new long[] { 100, 500, 500,  500};

    public Notification_Service(Context context, String title, String content){

        this.context = context;
        this.content = content;
        this.title = title;
    }

   public void showNotification(){

           try {
               prefs = PreferenceManager.getDefaultSharedPreferences(context);
               boolean notificationVibToggle = prefs.getBoolean("notificationVibToggle",true);
               boolean notificationLightToggle = prefs.getBoolean("notificationLightToggle",true);
               boolean notificationMergeToggle = prefs.getBoolean("notificationMergeToggle",false);
               boolean notificationSoundToggle = prefs.getBoolean("notificationSoundToggle",true);

               int smallIcon = R.drawable.launcher;
               Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.launcher);
               long when = System.currentTimeMillis();
               Intent notificationIntent = new Intent();
               notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        /*create new task for each notification with pending intent so we set Intent.FLAG_ACTIVITY_NEW_TASK */
               PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,0 );
        /*get the system service that manage notification NotificationManager*/
               NotificationManager notificationManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        /*build the notification*///
               NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                       .setWhen(when)
                       .setContentText(content)
                       .setContentTitle(title)
                       .setSmallIcon(smallIcon)
                       .setAutoCancel(true)
                       .setTicker(content)
                       .setContentIntent(pendingIntent);

            if(notificationLightToggle)
                notificationBuilder.setLights(Color.BLUE,2000,2000);

            if(notificationSoundToggle)
                notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

           if(notificationVibToggle)
                notificationBuilder.setVibrate(CYCLES);

           if(notificationMergeToggle)
               notificationManager.notify(567, notificationBuilder.build());
           else
               notificationManager.notify((int) when, notificationBuilder.build());
               /*sending notification to system. Here we use unique id (when)for making different each notification
         * if we use same id, then first notification replace by the last notification*/
           }
           catch (Exception e) {
               Log.e("Notification Exception", e.getMessage());
           }

   }

}
