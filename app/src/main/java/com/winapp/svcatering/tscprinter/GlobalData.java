package com.winapp.svcatering.tscprinter;

import android.bluetooth.BluetoothAdapter;

public class GlobalData {
    public static BluetoothService mService=null;
    public static BluetoothAdapter mBluetoothAdapter = null;
    static TransparentTransmission mTTransmission=null;
    static int languagevalue=0;
    static String address="";
    private static String user="";

    public static final int GSSO=0;
    public static final int GSSR=1;
    public static final int GSSC=2;
    public static final int GSST=3;
    public static final int GSFV=4;

    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final int CONNENTED = 6;
    public static final int LOGINSUCCESS = 7;
    public static final int LOGINFAIL = 8;
    public static final int RESULT = 9;


    public static void setBluetoothService(BluetoothService mService)
    {
        GlobalData.mService=mService;
    }
    public static BluetoothService getBluetoothService()
    {
        return mService;
    }
    public static void setUser(String user)
    {
        GlobalData.user=user;
    }
    public static String getUser()
    {
        return user;
    }
}

