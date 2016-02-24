package com.george.autorunpro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Akshay on 23-Feb-16.
 */
public class AlarmSet {

    //final public static String ONE_TIME = "onetime";

    public void SetRepeatAlarm(Context context, Calendar calendar,int req_id)
    {

        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra("id" , req_id);
        PendingIntent pi = PendingIntent.getBroadcast(context, req_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //After after 5 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, pi);
    }

    public void CancelAlarm(Context context,int req_id)
    {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra("id" ,req_id);
        PendingIntent sender = PendingIntent.getBroadcast(context, req_id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setOnetimeTimer(Context context,Calendar calendar,int req_id){
        System.out.println("in alarm set fn id= "+ req_id);
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra("id", req_id);
        PendingIntent pi = PendingIntent.getBroadcast(context, req_id, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), pi);
    }


}
