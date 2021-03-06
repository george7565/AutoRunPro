package com.george.autorunpro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Akshay on 23-Feb-16.
 */
public  class AlarmSet {

    //final public static String ONE_TIME = "onetime";

    public static  void SetRepeatAlarm(Context context, Calendar calendar,int req_id)
    {

        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra("id" , req_id);
        intent.putExtra("type","repeating");
        PendingIntent pi = PendingIntent.getBroadcast(context, req_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, pi);
    }

    public static  void CancelAlarm(Context context,int req_id)
    {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra("id" ,req_id);
        PendingIntent sender = PendingIntent.getBroadcast(context, req_id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public static void setOnetimeTimer(Context context,Calendar calendar,int req_id){
        System.out.println("in alarm set fn id= "+ req_id);
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra("id", req_id);
        intent.putExtra("type","onetime");
        PendingIntent pi = PendingIntent.getBroadcast(context, req_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), pi);
    }


}
