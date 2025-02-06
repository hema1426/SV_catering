package com.winapp.svcatering.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.winapp.svcatering.thermalprinter.App;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class InternetConnector_Receiver extends BroadcastReceiver {
    static Context context;

    public InternetConnector_Receiver(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            boolean isVisible = App.isActivityVisible();// Check if
            // activity
            // is
            // visible
            // or not
            Log.i("Activity is Visible ", "Is activity visible : " + isVisible);
            showInternetAlert(context);

            // If it is visible then trigger the task else do nothing
            if (isVisible == true) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager
                        .getActiveNetworkInfo();
                // Check internet connection and accrding to state change the
                // text of activity by calling method
                if (networkInfo != null && networkInfo.isConnected()) {
                    //  new MainActivity().changeTextStatus(true);
                } else {
                    // new MainActivity().changeTextStatus(false);
                    isConnectingToInternet(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean isConnectingToInternet(Context applicationContext) {
        context = applicationContext;
        ConnectivityManager cm = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            SweetAlertDialog pDialog = new SweetAlertDialog(applicationContext, SweetAlertDialog.ERROR_TYPE);
            pDialog.setTitleText("No Internet Connection!");
            pDialog.setContentText("Please Check Internet Connection.");
            // .setCancelText("It's Okay")
            pDialog.setCancelable(false);
            pDialog.setConfirmText("Okay");
            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    //  context.startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));
                    sDialog.dismiss();
                    ((Activity) context).finish();
                }
            }).show();
            return false;
        } else
            return true;
    }

    public static void showInternetAlert(Context applicationContext) {
        context = applicationContext;
        ConnectivityManager cm = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            SweetAlertDialog pDialog = new SweetAlertDialog(applicationContext, SweetAlertDialog.ERROR_TYPE);
            pDialog.setTitleText("No Internet Connection!");
            pDialog.setContentText("Please Check Internet Connection.");
            // .setCancelText("It's Okay")
            pDialog.setCancelable(false);
            pDialog.setConfirmText("Okay");
            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    //  context.startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));
                    sDialog.dismiss();
                    ((Activity) context).finish();
                }
            }).show();
        }
    }
}