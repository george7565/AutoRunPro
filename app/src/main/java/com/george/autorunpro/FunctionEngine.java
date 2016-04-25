package com.george.autorunpro;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
/**
 * Created by akshay on 3/25/16.
 */
public class FunctionEngine {

    private static final int ON =0;
    private static final int OFF = 1;
    private Context context;


    public FunctionEngine(Context context){
       this.context = context;

    }

    public boolean wifi(int mode){

        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(mode == ON){
            System.out.println("wifi on");
            return  wifiManager.setWifiEnabled(true);

        }
        else{
            System.out.println("wifi off");
            return wifiManager.setWifiEnabled(false);
        }
    }//wifi close

    public void airplane(int mode){}
    public void data(int mode){}

    public boolean bluetooth(int mode){

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (mode == ON && !isEnabled) {
            return bluetoothAdapter.enable();
        }
        else if(mode == OFF && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;

    }
    /*int codes for profile switching
       normal = 2;
       vibrate = 1;
       silent = 0;
    */
    public boolean silent(int mode){

        AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(mode == ON){
            try {
                mAudioManager.setRingerMode(0);
                return true;
            }catch (Exception e){
                return false;
            }
        }else{
            try{
            mAudioManager.setRingerMode(2);
                return true;
            }catch (Exception e){
                return false;
            }
        }

    }

    public boolean vibrate(int mode){

        AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(mode == ON){
            try {
                mAudioManager.setRingerMode(1);
                return true;
            }catch (Exception e){
                return false;
            }
        }else{
            try {
                mAudioManager.setRingerMode(2);
                return true;
            }catch (Exception e){
                return false;
            }

        }

    }


}//engine close
