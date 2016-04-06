package com.george.autorunpro;

import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    final public static String REQ_ID= "id";
    Cursor c;
    int id;
    String packageName,type,funcname;
    boolean is_a_function;
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
            if(id < 500) {

                try {
                    is_a_function = false;
                    String query = "select * from 'AppAlarms' where id =" + id;
                    SqlOperator sqlOperator = new SqlOperator(context);
                    c = sqlOperator.selectRecord(query);
                    c.moveToFirst();
                    System.out.println(c.getString(c.getColumnIndex("appname")));
                    System.out.println(c.getString(c.getColumnIndex("time")));
                    packageName = c.getString(c.getColumnIndex("appname"));
                    int mode = c.getInt(c.getColumnIndex("mode"));
                    Boolean issinglealarm = alarmtype_selector(context, mode);
                    if(issinglealarm){
                        ContentValues cv = new ContentValues();
                        cv.put("status", 0);
                        sqlOperator.updateRecord(c.getInt(c.getColumnIndex("id")), cv);
                    }

                    c.close();
                } catch (Exception e) {
                    Log.i("Cursor exception: ", e.toString());
                }
            }
        else{
                try {
                    is_a_function = true;
                    String query = "select * from 'FuncAlarms' where id =" + id;
                    SqlOperator2 sqlOperator = new SqlOperator2(context);
                    c = sqlOperator.selectRecord(query);
                    c.moveToFirst();
                    System.out.println(c.getString(c.getColumnIndex("funcname")));
                    System.out.println(c.getString(c.getColumnIndex("time")));
                    funcname = c.getString(c.getColumnIndex("funcname"));
                    int mode = c.getInt(c.getColumnIndex("mode"));
                    Boolean issinglealarm = alarmtype_selector(context, mode);
                    if(issinglealarm){
                        ContentValues cv = new ContentValues();
                        cv.put("status", 0);
                        sqlOperator.updateRecord(c.getInt(c.getColumnIndex("id")), cv);
                    }

                    c.close();
                } catch (Exception e) {
                    Log.i("Cursor exception: ", e.toString());
                }
            }

        //Release the lock
        wl.release();

    }
     private boolean alarmtype_selector(Context context,int mode){

        if (type.equals("repeating")) {
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
                    if (sun == 1) {
                        choose(context, mode);
                    }
                    break;

                case Calendar.MONDAY:
                    if (mon == 1) {
                        choose(context, mode);
                    }
                    break;

                case Calendar.TUESDAY:
                    if (tue == 1) {
                        choose(context, mode);
                    }
                    break;

                case Calendar.WEDNESDAY:
                    if (wed == 1) {
                        choose(context, mode);
                    }
                    break;

                case Calendar.THURSDAY:
                    if (thu == 1) {
                        choose(context, mode);
                    }
                    break;

                case Calendar.FRIDAY:
                    if (fri == 1) {
                        choose(context, mode);
                    }
                    break;

                case Calendar.SATURDAY:
                    if (sat == 1) {
                        choose(context, mode);
                    }
                    break;

            }
            return  false;

        } else {
            //one time fire only turning off after time
            choose(context, mode);
            return true;
        }

    }

    void startapplication(Context context) {
        PackageManager packm = context.getPackageManager();
        Intent LaunchIntent = packm.getLaunchIntentForPackage(packageName);
        if (LaunchIntent != null) {
            Notification_Service notification_service = new Notification_Service(context,"AutoRun Pro","Started "+getTitle(context,packageName)+" for you");
            notification_service.showNotification();
            context.startActivity(LaunchIntent);
        } else {
            Notification_Service notification_service = new Notification_Service(context,"AutoRun Pro","Error!, cannot start "+getTitle(context,packageName)+"");
            notification_service.showNotification();
        }
    }

    void stopapplication(Context context){
       //checking app is in foreground,then only a stop is needed
        //stop refers to minimising as android dont like killing apps
        am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = am.getRunningAppProcesses();
        Log.i("on reciever","in the stop application");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.i("For less than lollipop","hhhh");
            for(ActivityManager.RunningAppProcessInfo appProcess : procInfos) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (appProcess.processName.equals(packageName)) {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(startMain);
                        am.killBackgroundProcesses(packageName);

                        Notification_Service notification_service = new Notification_Service(context, "AutoRun Pro", "Stopped " + getTitle(context, packageName) + " for you");
                        notification_service.showNotification();

                    }
                    //Log.i("Foreground App", appProcess.processName);
                }
            }

            }else{
            Log.i("For lollipop and above","hhh");
            @SuppressWarnings("WrongConstant")
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),
                            usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    Log.i("Package Found on top",mySortedMap.get(mySortedMap.lastKey()).getPackageName());
                    if(packageName.equals(mySortedMap.get(mySortedMap.lastKey()).getPackageName())){

                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(startMain);
                        am.killBackgroundProcesses(packageName);

                        Notification_Service notification_service = new Notification_Service(context, "AutoRun Pro", "Stopped " + getTitle(context, packageName) + " for you");
                        notification_service.showNotification();
                    }
                }
            }


            }






    }
    void choose(Context context,int mode){

        System.out.println("in the chooser coose");
        System.out.println(is_a_function);
        if(!is_a_function) {
            if (mode == 0)
                startapplication(context);
            else
                stopapplication(context);
        }
        else{
             String status = "";
             System.out.println("in the chooser");
             FunctionEngine functionEngine = new FunctionEngine(context);

             if (funcname.equals("Wifi")){

                 if(functionEngine.wifi(mode)){
                     if(mode == 0)status = "Enabled";
                     else status = "Disabled";
                 }else{
                    status = "Error in enabling/disabling";
                 }
             }

             else if(funcname.equals("Bluetooth")){
                 if(functionEngine.bluetooth(mode)){
                     if(mode == 0)status = "Enabled";
                     else status = "Disabled";
                 }
                 else {
                     status = "Error in enabling/disabling";
                 }

             }

             else if(funcname.equals("Vibrate Mode")){
                 if(functionEngine.vibrate(mode)){
                     if(mode == 0)status = "Enabled";
                     else status = "Disabled";
                 }
                 else{
                     status = "Error in enabling/disabling";
                 }
             }

             else if(funcname.equals("Silent Mode")){
                 if(functionEngine.silent(mode)){
                     if(mode == 0)status = "Enabled";
                     else status = "Disabled";
                 }
                 else{
                     status = "Error in enabling/disabling";
                 }
             }

            Notification_Service notification_service = new Notification_Service(context,"AutoRun Pro",status+" "+funcname+ " for you");
            notification_service.showNotification();
        }

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
