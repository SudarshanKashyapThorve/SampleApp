package com.au10tix.au10sample;

import android.app.Application;
import android.os.StrictMode;


public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        new android.os.Handler().post(new Runnable() {
//
//            @Override
//            public void run() {
                setupStrictMode();
//            }
//        });
    }

    private void setupStrictMode(){
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
                .penaltyLog().penaltyFlashScreen()
                .build());

    }
}
