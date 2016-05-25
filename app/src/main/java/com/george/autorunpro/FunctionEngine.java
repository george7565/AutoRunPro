package com.george.autorunpro;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.util.Log;

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

        if(mode == ON && !wifiManager.isWifiEnabled()){
            System.out.println("wifi on");
            return  wifiManager.setWifiEnabled(true);

        }
        else if(mode == OFF && wifiManager.isWifiEnabled()){
            System.out.println("wifi off");
            return wifiManager.setWifiEnabled(false);
        }
        else
            return false;
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
        return false;

    }
    /*int codes for profile switching
       normal = 2;
       vibrate = 1;
       silent = 0;
    */
    public boolean silent(int mode){

        AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int mode_now = mAudioManager.getRingerMode();
        if(mode == ON && mode_now != 0){
            try {
                mAudioManager.setRingerMode(0);
                return true;
            }catch (Exception e){
                return false;
            }
        }else if(mode == OFF && mode_now != 2){
            try{
            mAudioManager.setRingerMode(2);
                return true;
            }catch (Exception e){
                return false;
            }
        }
        return false;

    }

    public boolean vibrate(int mode){

        AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int mode_now = mAudioManager.getRingerMode();
        if(mode == ON && mode_now != 1){
            try {
                mAudioManager.setRingerMode(1);
                return true;
            }catch (Exception e){
                return false;
            }
        }else if(mode == OFF && mode_now != 2){
            try {
                mAudioManager.setRingerMode(2);
                return true;
            }catch (Exception e){
                return false;
            }
        }
        return false;

    }
    public boolean pauseAudio() {

        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (manager.isMusicActive()) {
            // Request audio focus for playback
            int result = manager.requestAudioFocus(afChangeListener,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Log.i("inside stop", "audio focus obtained");
                return true;
                // Start playback.//hack to stop playback doing nothing with the focus and not notifying other app.
            }

        }
        return false;
    }
    AudioManager.OnAudioFocusChangeListener afChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        // Pause playback
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // Resume playback
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        //am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
                        //am.abandonAudioFocus(afChangeListener);
                        // Stop playback
                    }
                }
            };

}//engine close
