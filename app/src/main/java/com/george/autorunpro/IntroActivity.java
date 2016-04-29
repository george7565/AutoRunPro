package com.george.autorunpro;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Akshay on 24-Apr-16.
 */
public class IntroActivity extends AppIntro {


    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Hi, Welcome to AutoRun Pro", "I will help you to automate functions of your phone", R.drawable.launcher, Color.parseColor("#0ba9f3")));
        addSlide(AppIntroFragment.newInstance("Autorun your Apps", "You can launch your apps at any time you want", R.drawable.launcher,Color.parseColor("#f12821")));
        addSlide(AppIntroFragment.newInstance("Toggle your phone functions", "You can toggle supported functions like wifi at your prefered time", R.drawable.launcher, Color.parseColor("#4d6a78")));
        addSlide(AppIntroFragment.newInstance("You are all set,Enjoy AutoRun Pro", "Get Started", R.drawable.intro4,  Color.parseColor("#4caf50")));
        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#00FFFFFF"));
        setSeparatorColor(Color.WHITE);
        setFadeAnimation();
        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);
        //setDepthAnimation();
        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);

    }


    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSkipPressed() {
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

}