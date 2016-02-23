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

    public void SetRepeatAlarm(Context context, Calendar calendar)
    {
     /*   Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        if(Calendar.getInstance().after(calendar)){
            // Move to tomorrow
            calendar.add(Calendar.DATE, 1);
        }
*/

        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //After after 5 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, pi);
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setOnetimeTimer(Context context,Calendar calendar){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), pi);
    }
    /*
* Date futureDate = new Date(new Date().getTime() + 86400000);


    futureDate.setHours(8);
    futureDate.setMinutes(0);
    futureDate.setSeconds(0);
Intent intent = new Intent(con, MyAppReciever.class);

    PendingIntent sender = PendingIntent.getBroadcast(con, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);

    am.set(AlarmManager.RTC_WAKEUP, futureDate.getTimeInMillis(), sender);
*
* */


}
