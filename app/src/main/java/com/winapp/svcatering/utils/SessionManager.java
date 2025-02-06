package com.winapp.svcatering.utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.winapp.svcatering.activity.LoginActivity;
import com.winapp.svcatering.activity.MainHomeActivity;
import com.winapp.svcatering.activity.RegisterActivity;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Sharedpref file name
    public static final String PREF_NAME = "Pref";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    private static final String IS_REGISTER= "IsRegisterIn";

    // User name (make variable public to access from outside)
    public static final String KEY_CUSTOMER_ID="customer_id";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_MOBILE="mobile";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public static final String KEY_USER_NAME="user_name";
    public static final String KEY_PASSWORD="password";
    public static final String KEY_ROLL_NAME="roll_name";
    public static final String KEY_LOCATION_CODE="location_code";
    public static final String KEY_USER_PERMISSION="is_permission";
    public static final String IS_LOCATION_PERMISSION ="is_main_location";
    public static final String KEY_COMPANY_CODE="company_code";
    public static final String KEY_COMPANY_NAME="company_name";
    public static final String KEY_ADDRESS1="address1";
    public static final String KEY_ADDRESS2="address2";
    public static final String KEY_ADDRESS3="address3";

    public static final String KEY_REGISTER_EMAIL="register_email";
    public static final String KEY_REGISTER_MOBILE="register_mobile";

    public static final String KEY_COMPANY_SHORTCODE="short_code";
    public static final String KEY_COMPANY_LOGO="company_logo";
    public static final String KEY_QR_CODE="qr_code";
    public static final String KEY_PAID_STAMP="paid_stamp";
    public static final String KEY_UN_PAID_STAMP="unpaid_stamp";
    public static final String KEY_COUNTRY="country_name";
    public static final String KEY_POSTAL_CODE="postal_code";
    public static final String KEY_PHONE_NO="phone_no";
    public static final String KEY_COMPANY_REG_NO="reg_no";
    public static final String KEY_PAY_NOW="pay_now";
    public static final String KEY_BANK="bank";
    public static final String KEY_CHEQUE="cheque";
    public static final String KEY_SALESMAN_NAME="salesman_name";
    public static final String KEY_SALESMAN_PHONE="salesman_phone";
    public static final String KEY_SALESMAN_EMAIL="salesman_email";
    public static final String KEY_SALESMAN_OFFICE="salesman_office";
    public static final String KEY_NEGATIVE_STOCK="negative_stock";
    private SharedPreferences.Editor registerPrefsEditor;
    private SharedPreferences registerPreferences;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;


    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        registerPreferences = context.getSharedPreferences("registerPref", MODE_PRIVATE);
        registerPrefsEditor = registerPreferences.edit();

        loginPreferences = context.getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
    }

    public void createRegister(String mobileNumber,String emailId){
        editor.putBoolean(IS_REGISTER, true);
        editor.putString(KEY_REGISTER_MOBILE,mobileNumber);
        editor.putString(KEY_REGISTER_EMAIL,emailId);
        editor.commit();
    }
    /**
     * Create login session
     *
     * */
    public void createLoginSession(String user_name,String password,String roll_name, String location_code,String is_permission,String is_location_permission,
                                   String company_code,String company_name,String address1,String address2,String address3,
                                   String country,String postalcode,String phoneno,String regno,String logo,
                                   String qrcode,String paid,String unpaid,String paynow,String bank,String cheque,
                                   String salesManName , String salesManPhone , String salesManEmail ,
                                   String salesManOffice ,String negativeStock){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER_NAME, user_name);
        editor.putString(KEY_PASSWORD,password);
        editor.putString(KEY_ROLL_NAME,roll_name);
        editor.putString(KEY_LOCATION_CODE,location_code);
        editor.putString(KEY_USER_PERMISSION,is_permission);
        editor.putString(IS_LOCATION_PERMISSION, is_location_permission);
        editor.putString(KEY_COMPANY_CODE,company_code);
        editor.putString(KEY_COMPANY_NAME,company_name);
        editor.putString(KEY_ADDRESS1,address1);
        editor.putString(KEY_ADDRESS2,address2);
        editor.putString(KEY_ADDRESS3,address3);
        editor.putString(KEY_COUNTRY,country);
        editor.putString(KEY_POSTAL_CODE,postalcode);
        editor.putString(KEY_PHONE_NO,phoneno);
        editor.putString(KEY_COMPANY_REG_NO,regno);
        editor.putString(KEY_COMPANY_LOGO,logo);
        editor.putString(KEY_QR_CODE,qrcode);
        editor.putString(KEY_PAID_STAMP,paid);
        editor.putString(KEY_UN_PAID_STAMP,unpaid);
        editor.putString(KEY_PAY_NOW,paynow);
        editor.putString(KEY_BANK,bank);
        editor.putString(KEY_CHEQUE,cheque);
        editor.putString(KEY_SALESMAN_NAME,salesManName);
        editor.putString(KEY_SALESMAN_PHONE,salesManPhone);
        editor.putString(KEY_SALESMAN_EMAIL,salesManEmail);
        editor.putString(KEY_SALESMAN_OFFICE,salesManOffice);
        editor.putString(KEY_NEGATIVE_STOCK,negativeStock);

        editor.commit();
    }

    /**
     * Set company Details to the Session
     */

    public void setCompanyDetails(String companyShortCode,String companyLogo){
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_COMPANY_SHORTCODE,companyShortCode);
        editor.putString(KEY_COMPANY_LOGO,companyLogo);
        editor.commit();
    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getCompanyDetails(){
        HashMap<String, String> company = new HashMap<String, String>();
        company.put(KEY_COMPANY_SHORTCODE, pref.getString(KEY_COMPANY_SHORTCODE, null));
        company.put(KEY_COMPANY_LOGO,pref.getString(KEY_COMPANY_LOGO,null));
        return company;
    }


    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, MainHomeActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            _context.startActivity(i);
        }
    }

  //  public void createLoginSession(String user_name,String roll_name, String location_code,String is_permission,String is_main_location,
                               //    String company_code,String company_name,String address1,String address2,String address3){

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USER_NAME, pref.getString(KEY_USER_NAME, null));
        user.put(KEY_PASSWORD,pref.getString(KEY_PASSWORD,null));
        user.put(KEY_ROLL_NAME,pref.getString(KEY_ROLL_NAME,null));
        user.put(KEY_LOCATION_CODE,pref.getString(KEY_LOCATION_CODE,null));
        user.put(KEY_USER_PERMISSION,pref.getString(KEY_USER_PERMISSION,null));
        user.put(IS_LOCATION_PERMISSION,pref.getString(IS_LOCATION_PERMISSION,null));
        user.put(KEY_COMPANY_CODE,pref.getString(KEY_COMPANY_CODE,null));
        user.put(KEY_COMPANY_NAME,pref.getString(KEY_COMPANY_NAME,null));
        user.put(KEY_ADDRESS1,pref.getString(KEY_ADDRESS1,null));
        user.put(KEY_ADDRESS2,pref.getString(KEY_ADDRESS2,null));
        user.put(KEY_ADDRESS3,pref.getString(KEY_ADDRESS3,null));
        user.put(KEY_POSTAL_CODE,pref.getString(KEY_POSTAL_CODE,null));
        user.put(KEY_COMPANY_REG_NO,pref.getString(KEY_COMPANY_REG_NO,null));
        user.put(KEY_COUNTRY,pref.getString(KEY_COUNTRY,null));
        user.put(KEY_PHONE_NO,pref.getString(KEY_PHONE_NO,null));
        user.put(KEY_COMPANY_LOGO,pref.getString(KEY_COMPANY_LOGO,null));
        user.put(KEY_QR_CODE,pref.getString(KEY_QR_CODE,null));
        user.put(KEY_PAID_STAMP,pref.getString(KEY_PAID_STAMP,null));
        user.put(KEY_UN_PAID_STAMP,pref.getString(KEY_UN_PAID_STAMP,null));
        user.put(KEY_PAY_NOW,pref.getString(KEY_PAY_NOW,null));
        user.put(KEY_BANK,pref.getString(KEY_BANK,null));
        user.put(KEY_CHEQUE,pref.getString(KEY_CHEQUE,null));
        user.put(KEY_SALESMAN_NAME,pref.getString(KEY_SALESMAN_NAME,null));
        user.put(KEY_SALESMAN_PHONE,pref.getString(KEY_SALESMAN_PHONE,null));
        user.put(KEY_SALESMAN_EMAIL,pref.getString(KEY_SALESMAN_EMAIL,null));
        user.put(KEY_SALESMAN_OFFICE,pref.getString(KEY_SALESMAN_OFFICE,null));
        user.put(KEY_NEGATIVE_STOCK,pref.getString(KEY_NEGATIVE_STOCK,null));

        return user;
    }

    public HashMap<String, String> getRegisterDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_REGISTER_EMAIL, pref.getString(KEY_REGISTER_EMAIL, null));
        user.put(KEY_REGISTER_MOBILE,pref.getString(KEY_REGISTER_MOBILE,null));
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);
        i.putExtra("from","homePage");
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        _context.startActivity(i);
    }

    public void removeRegisterSession(){
        try {
            // Clearing all data from Shared Preferences
            registerPrefsEditor.clear();
            registerPrefsEditor.commit();
            editor.clear();
            editor.commit();
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
            // After logout redirect user to Login Activity
            Intent i = new Intent(_context, RegisterActivity.class);
            i.putExtra("from","homePage");
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            _context.startActivity(i);
            ((Activity)_context).finish();
        }catch (Exception e){}
    }

    public boolean isRegisterIn(){
        return pref.getBoolean(IS_REGISTER,false);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}