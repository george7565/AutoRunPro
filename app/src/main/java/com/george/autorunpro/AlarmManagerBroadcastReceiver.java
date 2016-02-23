package com.george.autorunpro;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";
    Cursor c;
    String packageName;
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();
        //You can do the processing here.
        Bundle extras = intent.getExtras();
        //StringBuilder msgStr = new StringBuilder();

        if(extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)){
            //Make sure this intent has been sent by the one-time timer button.
            //msgStr.append("One time Timer : ");
        }
        Format formatter = new SimpleDateFormat("H:m");
        String time = formatter.format(new Date());
       // Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();
        //running the application
        SqlOperator sqlOperator = new SqlOperator(context);
        try{
         c = sqlOperator.selectRecords();
            do{
                System.out.println(c.getString(c.getColumnIndex("appname")));
                System.out.println(c.getString(c.getColumnIndex("time")));
                if(c.getString(c.getColumnIndex("time")).equals(time));
                   packageName = c.getString(c.getColumnIndex("appname"));
            }
           while (c.moveToNext());
        }
        catch (Exception e){    Log.i("Cursor exception: ", e.toString());    }

        //
        //String selectQuery = "SELECT lastchapter FROM Bookdetails WHERE bookpath=?";
        //Cursor c = db.rawQuery(selectQuery, new String[] { fileName });
        //if (c.moveToFirst()) {
        //    temp_address = c.getString(c.getColumnIndex("lastchapter"));
        //}
        //c.close();
        //

        PackageManager packm = context.getPackageManager();
         Intent LaunchIntent = packm.getLaunchIntentForPackage(packageName);
         if (LaunchIntent != null) {
              context.startActivity(LaunchIntent);
          } else {
              Toast.makeText(context, "Application  not found", Toast.LENGTH_SHORT).show();
          }
          context.startActivity( LaunchIntent );
        //Release the lock
        wl.release();
    }


}
