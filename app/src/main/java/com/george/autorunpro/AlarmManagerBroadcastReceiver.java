package com.george.autorunpro;

import java.util.Calendar;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    final public static String REQ_ID= "id";
    Cursor c;
    int id;
    String packageName,type;
    ActivityManager am;
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        Bundle extras = intent.getExtras();

        if(extras != null ) {
            id = extras.getInt(REQ_ID);
            type = extras.getString("type");
        }

        System.out.println("Inside reciever id="+id);
        SqlOperator sqlOperator = new SqlOperator(context);
        String query = "select * from 'AppAlarms' where id ="+id;

        try{
            c = sqlOperator.selectRecord(query);c.moveToFirst();
            System.out.println(c.getString(c.getColumnIndex("appname")));
            System.out.println(c.getString(c.getColumnIndex("time")));
            packageName = c.getString(c.getColumnIndex("appname"));
            int mode = c.getInt(c.getColumnIndex("mode"));

            if(type.equals("repeating")) {
                int mon = c.getInt(c.getColumnIndex("monday"));
                int tue = c.getInt(c.getColumnIndex("tuesday"));
                int wed = c.getInt(c.getColumnIndex("wednesday"));
                int thu = c.getInt(c.getColumnIndex("thursday"));
                int fri = c.getInt(c.getColumnIndex("friday"));
                int sat = c.getInt(c.getColumnIndex("saturday"));
                int sun = c.getInt(c.getColumnIndex("sunday"));
                //getting current day from phone calender
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK); //sun == 1 and sat ==7
                //
                switch (day) {
                    case Calendar.SUNDAY:
                        if(sun == 1){choose(context,mode);}
                        break;

                    case Calendar.MONDAY:
                        if(mon == 1){choose(context,mode);}
                        break;

                    case Calendar.TUESDAY:
                        if(tue == 1){choose(context,mode);}
                        break;

                    case Calendar.WEDNESDAY:
                        if(wed == 1){choose(context,mode);}
                        break;

                    case Calendar.THURSDAY:
                        if(thu == 1){choose(context,mode);}
                        break;

                    case Calendar.FRIDAY:
                        if(fri == 1){choose(context,mode);}
                        break;

                    case Calendar.SATURDAY:
                        if(sat == 1){choose(context,mode);}
                        break;
                }

            }
            else {
                //one time fire only turning off after time
                ContentValues cv = new ContentValues();
                cv.put("status",0);
                sqlOperator.updateRecord(c.getInt(c.getColumnIndex("id")),cv);
                choose(context,mode);
            }
            c.close();
        }
        catch (Exception e){    Log.i("Cursor exception: ", e.toString());    }

        //Release the lock
        wl.release();
    }

    void startapplication(Context context) {
        PackageManager packm = context.getPackageManager();
        Intent LaunchIntent = packm.getLaunchIntentForPackage(packageName);
        Notification_Service notification_service = new Notification_Service(context,"Starting App","AutoRun Pro has started "+getTitle(context,packageName)+" for you");
        notification_service.showNotification();
        if (LaunchIntent != null) {
            context.startActivity(LaunchIntent);
        } else {
            Toast.makeText(context, "Application  not found", Toast.LENGTH_SHORT).show();
        }
    }

    void stopapplication(Context context){

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
        am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(packageName);
        Notification_Service notification_service = new Notification_Service(context,"Stopping App","AutoRun Pro has stopped "+getTitle(context,packageName)+" for you");
        notification_service.showNotification();

    }
    void choose(Context context,int mode){

        if (mode == 0)
            startapplication(context);
        else
            stopapplication(context);

    }
    protected String getTitle(Context context,String  packageName){

        ApplicationInfo applicationInfo = null;
        PackageManager packageManager = context.getPackageManager();

        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);

        }
        catch (final PackageManager.NameNotFoundException e) { }
        final String title = (String) ((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");

        return title;
    }




}
