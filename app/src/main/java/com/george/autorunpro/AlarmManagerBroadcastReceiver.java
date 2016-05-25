package com.george.autorunpro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    final public static String REQ_ID= "id";
    Cursor c;
    int id;
    String packageName,type,funcname;
    boolean is_a_function;
    private SharedPreferences prefs;
    boolean checkIfEnabled = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "reciever inside"     , Toast.LENGTH_LONG).show();
        //scheduling alarms after reboot by service
        String action = intent.getAction();
        Toast.makeText(context, "intent is "+action     , Toast.LENGTH_LONG).show();
        if ("android.intent.action.BOOT_COMPLETED".equals(action) || "android.intent.action.MY_PACKAGE_REPLACED".equals(action) )
        {
            Intent i = new Intent(context, AlarmSchedulerService.class);
            context.startService(i);
        }

        //
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AutorunPro");
        //Acquire the lock
        wl.acquire();

        Bundle extras = intent.getExtras();

        if(extras != null ) {
            id = extras.getInt(REQ_ID);
            type = extras.getString("type");
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        checkIfEnabled = prefs.getBoolean("notificationToggle",true);
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



                    Boolean issinglealarm = alarmtype_selector(context, 0);
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
                    int alonetype = c.getInt(c.getColumnIndex("alonetype"));
                    int mode;
                    if (alonetype != -1)
                        mode = alonetype;
                    else
                        mode= c.getInt(c.getColumnIndex("mode"));

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
   /*
   * Alarmtype selector function checks whether alarm is repeating or not ,if repeating does weekdays check
   * returns boolean,if alarm is to be triggerred and calls the chooser function which calls the event function
   * */
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
            context.startActivity(LaunchIntent);
            showNotificationOnEnable(context,"Started "+getTitle(context,packageName)+" for you",checkIfEnabled);
        } else {
            showNotificationOnEnable(context,"Error, cannot start "+getTitle(context,packageName)+"",checkIfEnabled);
        }
    }

    void choose(Context context,int mode){

        System.out.println("in the chooser coose");
        System.out.println(is_a_function);
        if(!is_a_function) {
                appendLog(c.getString(c.getColumnIndex("appname")) +"   "+c.getString(c.getColumnIndex("time")));
                startapplication(context);
        }
        else{
             String status = "";
             System.out.println("in the chooser");
             FunctionEngine functionEngine = new FunctionEngine(context);
             appendLog(c.getString(c.getColumnIndex("funcname")) +"   "+c.getString(c.getColumnIndex("time")));
            //setting checkifenabled to false for false conditions so notifications dont trigger
             if (funcname.equals("Wifi")){

                 if(functionEngine.wifi(mode)){
                     if(mode == 0)status = "Enabled";
                     else status = "Disabled";
                 }else{
                     checkIfEnabled = false;
                 }
             }

             else if(funcname.equals("Bluetooth")){
                 if(functionEngine.bluetooth(mode)){
                     if(mode == 0)status = "Enabled";
                     else status = "Disabled";
                 }
                 else {
                     checkIfEnabled = false;
                 }

             }

             else if(funcname.equals("Vibrate Mode")){
                 if(functionEngine.vibrate(mode)){
                     if(mode == 0)status = "Enabled";
                     else status = "Disabled";
                 }
                 else{
                     checkIfEnabled = false;
                 }
             }

             else if(funcname.equals("Silent Mode")){
                 if(functionEngine.silent(mode)){
                     if(mode == 0)status = "Enabled";
                     else status = "Disabled";
                 }
                 else{
                     checkIfEnabled = false;
                 }
             }
            else if(funcname.equals("Stop Audio")){
                 if(functionEngine.pauseAudio())
                     funcname = "Stopped audio playback";
                 else
                     checkIfEnabled = false;
             }
            showNotificationOnEnable(context,status+" "+funcname+ " for you",checkIfEnabled);
        }

    }
    protected String getTitle(Context context,String  packageName){

        ApplicationInfo applicationInfo = null;
        PackageManager packageManager = context.getPackageManager();

        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);

        }
        catch (final PackageManager.NameNotFoundException e) { e.printStackTrace();}

        return (String) ((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");
    }




    void showNotificationOnEnable(Context context,String msg,boolean isEnabled){

        if(isEnabled){
            Notification_Service notification_service = new Notification_Service(context,"AutoRun Pro",msg);
            notification_service.showNotification();
        }
    }

    public void appendLog(String text)
    {
        File logFile = new File("sdcard/log.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
