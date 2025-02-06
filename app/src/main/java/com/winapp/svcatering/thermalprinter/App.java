package com.winapp.svcatering.thermalprinter;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;

/**
 * Created by yechao on 2020/3/26/026.
 * Describe :
 */
public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        FirebaseApp.initializeApp(this);

       // FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
    }

    public static Context getContext() {
        return mContext;
    }


    // Gloabl declaration of variable to use in whole app

    public static boolean activityVisible; // Variable that will check the
    // current activity state

    public static boolean isActivityVisible() {
        return activityVisible; // return true or false
    }

    public static void activityResumed() {
        activityVisible = true;// this will set true when activity resumed

    }

    public static void activityPaused() {
        activityVisible = false;// this will set false when activity paused

    }
}
