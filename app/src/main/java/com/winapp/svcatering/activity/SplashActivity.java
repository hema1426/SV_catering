package com.winapp.svcatering.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashActivity extends AppCompatActivity {

    // declare the variables to splash screen time
    public static int SPLASH_TIME=3000;
    private SessionManager session;
    private ImageView logoImage;
    private DBHelper dbHelper;
    public HashMap<String, String> user;
    public String userName;
    public String password;
    public String companyCode;
    public String mobileNumber;
    public String emailId;
    public String deviceId;
    public SweetAlertDialog  pDialog;
    public HashMap<String ,String> registerUser;
    private String api_url;
    private SharedPreferences.Editor registerPrefsEditor;
    private SharedPreferences registerPreferences;
    private boolean isRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the Toolbar and Status bar for full Screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        session=new SessionManager(this);
        user = session.getUserDetails();
        registerUser=session.getRegisterDetails();
        userName=user.get(SessionManager.KEY_USER_NAME);
        companyCode = user.get(SessionManager.KEY_COMPANY_CODE);
        emailId=user.get(SessionManager.KEY_EMAIL);
        mobileNumber=user.get(SessionManager.KEY_MOBILE);
        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        registerPreferences = getSharedPreferences("registerPref", MODE_PRIVATE);
        registerPrefsEditor = registerPreferences.edit();

        isRegister = registerPreferences.getBoolean("saveRegister", false);
        api_url=registerPreferences.getString("apiUrl","");
        dbHelper=new DBHelper(this);
        // handler for redirect the main activity
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over

                // Intent i = new Intent(SplashActivity.this,LoginActivity.class);
                //startActivity(i);
                //finish();

                if (InternetConnector_Receiver.isConnectingToInternet(SplashActivity.this)){
                   *//* if (session.isRegisterIn()){
                        JSONObject jsonObject=new JSONObject();
                        try {
                            String mobile=registerUser.get(SessionManager.KEY_REGISTER_MOBILE);
                            String email=registerUser.get(SessionManager.KEY_REGISTER_EMAIL);
                            jsonObject.put("HandPhNo",mobile);
                            jsonObject.put("EmailAddress",email);
                            getLicenseStatus(jsonObject,"session");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        JSONObject jsonObject=new JSONObject();
                        getLicenseStatus(jsonObject,"notsession");
                    }*//*


                    if (session.isLoggedIn()){
                        try {
                            Intent i = new Intent(SplashActivity.this,DashboardActivity.class);
                            startActivity(i);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        // Need to Change the License to Register Activity
                        String substring=Constants.TRANS_ORIENT_DEMO;
                        Log.w("GivenSubString:",substring);
                        boolean status=dbHelper.insertUrl(substring);
                        Intent i = new Intent(SplashActivity.this,LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }
        }, SPLASH_TIME);*/


        new Handler().postDelayed(() -> {
            // Testing Function
            if (session.isLoggedIn()){
                startActivity(
                        new Intent(SplashActivity.this,DashboardActivity.class)
                );
                finish();
            }else {
        String apiUrl=Constants.SV_Catering_DEMO_URL;
      //  String apiUrl = Constants.SV_Catering_SINGAPO_DEMO_URL_1;

                dbHelper.insertUrl(apiUrl);
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                finish();
            }

            // Real execute function with licence code
          /*  if (InternetConnector_Receiver.isConnectingToInternet(SplashActivity.this)){
                if (api_url!=null && !api_url.isEmpty()){
                    // setValidateURL(api_url.toString());
                    setLicenceValidate();
                }else{
                    Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                    startActivity(intent);
                    finish();
                }
            }else {
                Toast.makeText(getApplicationContext(),"No Internet Found...!",Toast.LENGTH_SHORT).show();
            }*/

        }, SPLASH_TIME);


    }


    public  void setLicenceValidate(){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this);
            String URL=Constants.NEW_LICENCE_CHECK_URL;
            // ProgressDialog progressDialog=new ProgressDialog(this);
            // progressDialog.setMessage("Validate Session...");
            // progressDialog.setCancelable(false);
            // progressDialog.show();
            StringRequest registerRequest = new StringRequest(Request.Method.POST, URL, response -> {
                Log.w("RegisterResponse12:",response.toString());
                //progressDialog.dismiss();
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String  status=jsonObject.optString("Status");
                    String  message=jsonObject.optString("Msg");
                    if (status.equals("1")){
                        if (session.isLoggedIn()) {
                            startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            finish();
                        }
                    }else{
                        showAlert(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.w("ErrorRegister:",error.toString());
                //progressDialog.dismiss();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("AppCode", Constants.APP_CODE);
                    parameters.put("DeviceId", deviceId);
                    return parameters;
                }
            };
            registerRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }
                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }
                @Override
                public void retry(VolleyError error) {
                }
            });
            requestQueue.add(registerRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAlert(String message){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Information");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            if (message.equals("Device Not Registered ! Contact Admin.")){
                removeSession();
            }else {
                dialogInterface.dismiss();
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
                finishAffinity();
                //     android.os.Process.killProcess(android.os.Process.myPid());
            }
            dialogInterface.dismiss();
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    public void removeSession(){
        session.removeRegisterSession();
    }

    private void getLicenseStatus(JSONObject jsonObject, String sessionStatus) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        String url=null;
        if (sessionStatus.equals("session")){
            url = Constants.SPLASH_SCREEN_VALIDATION+jsonObject.toString();
        }else {
            url = Constants.SPLASH_SCREEN_VALIDATION;
        }
        Log.w("Given_SPLASH_URL:", url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Validate Licence...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.w("SalesOrderResponse:", String.valueOf(response));
                        String result=response.optString("result");
                        String subject=response.optString("subject");
                        String message=response.optString("errorMessage");
                        if (result.equals("fail")){
                            showActivationAlert(message);
                        }else if (result.equals("pass")){
                            if (session.isLoggedIn()){
                                try {
                                   /* Intent i = new Intent(SplashActivity.this,LoginActivity.class);
                                    startActivity(i);
                                    finish();*/
                                    Intent i = new Intent(SplashActivity.this,DashboardActivity.class);
                                    startActivity(i);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else {
                                // Need to Change the License to Register Activity
                                Intent i = new Intent(SplashActivity.this,RegisterActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                        pDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    pDialog.dismiss();
            if(error instanceof NoConnectionError){
                ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = null;
                if (cm != null) {
                    activeNetwork = cm.getActiveNetworkInfo();
                }
                if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
                    Toast.makeText(this, "Server is not connected to internet.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Your device is not connected to internet.", Toast.LENGTH_SHORT).show();
                }
            } /*else if (error instanceof NetworkError || error.getCause() instanceof ConnectException
                    || (Objects.requireNonNull(error.getCause()).getMessage() != null
                    && Objects.requireNonNull(error.getCause().getMessage()).contains("connection"))){
                Toast.makeText(this, "Your device is not connected to internet.",
                        Toast.LENGTH_SHORT).show();
            } */else if (error.getCause() instanceof MalformedURLException){
                Toast.makeText(this, "Bad Request.", Toast.LENGTH_SHORT).show();
            } else if (error instanceof ParseError || error.getCause() instanceof IllegalStateException
                    || error.getCause() instanceof JSONException
                    || error.getCause() instanceof XmlPullParserException){
                Toast.makeText(this, "Parse Error (because of invalid json or xml).", Toast.LENGTH_SHORT).show();
            } else if (error.getCause() instanceof OutOfMemoryError){
                Toast.makeText(this, "Out Of Memory Error.", Toast.LENGTH_SHORT).show();
            }else if (error instanceof AuthFailureError){
                Toast.makeText(this, "server couldn't find the authenticated request.", Toast.LENGTH_SHORT).show();
            } else if (error instanceof ServerError || error.getCause() instanceof ServerError) {
                Toast.makeText(this, "Server is not responding.", Toast.LENGTH_SHORT).show();
            }else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
                    || error.getCause() instanceof ConnectTimeoutException
                    || error.getCause() instanceof SocketException
                    || (error.getCause().getMessage() != null
                    && error.getCause().getMessage().contains("Connection timed out"))) {
                Toast.makeText(this, "Connection timeout error", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "An unknown error occurred.", Toast.LENGTH_SHORT).show();
            }
            // Do something when error occurred
            Log.w("Error_throwing:", error.toString());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", Constants.LICENSE_VALIDATE_SECRET_CODE, Constants.LICENSE_VALIDATE_SECRET_PASSWORD);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    private void validateLicense(JSONObject jsonObject) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        String url = Constants.FIRST_TIME_ACTIVATION_URL+jsonObject.toString();
        Log.w("Given_RegisterUrl:", url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Device is Registering...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.w("License Response",String.valueOf(response));
                        String result=response.optString("result");
                        String subject=response.optString("subject");
                        String message=response.optString("errorMessage");
                        if (result.equals("fail")){
                            showActivationAlert(message);
                        }else {
                            if (Utils.getBaseUrl(SplashActivity.this)!=null) {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Intent intent=new Intent(getApplicationContext(),ValidateUrlActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        pDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            if(error instanceof NoConnectionError){
                ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = null;
                if (cm != null) {
                    activeNetwork = cm.getActiveNetworkInfo();
                }
                if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
                    Toast.makeText(this, "Server is not connected to internet.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Your device is not connected to internet.", Toast.LENGTH_SHORT).show();
                }
            } else if (error instanceof NetworkError
                    || error.getCause() instanceof ConnectException
                    || (error.getCause().getMessage() != null
                    && error.getCause().getMessage().contains("connection"))){
                Toast.makeText(this, "Your device is not connected to internet.", Toast.LENGTH_SHORT).show();
            } else if (error.getCause() instanceof MalformedURLException){
                Toast.makeText(this, "Bad Request.", Toast.LENGTH_SHORT).show();
            } else if (error instanceof ParseError
                    || error.getCause() instanceof IllegalStateException
                    || error.getCause() instanceof JSONException
                    || error.getCause() instanceof XmlPullParserException){
                Toast.makeText(this, "Parse Error (because of invalid json or xml).", Toast.LENGTH_SHORT).show();
            } else if (error.getCause() instanceof OutOfMemoryError){
                Toast.makeText(this, "Out Of Memory Error.", Toast.LENGTH_SHORT).show();
            }else if (error instanceof AuthFailureError){
                Toast.makeText(this, "server couldn't find the authenticated request.", Toast.LENGTH_SHORT).show();
            } else if (error instanceof ServerError
                    || error.getCause() instanceof ServerError) {
                Toast.makeText(this, "Server is not responding.", Toast.LENGTH_SHORT).show();
            }else if (error instanceof TimeoutError
                    || error.getCause() instanceof SocketTimeoutException
                    || error.getCause() instanceof ConnectTimeoutException
                    || error.getCause() instanceof SocketException
                    || (error.getCause().getMessage() != null
                    && error.getCause().getMessage().contains("Connection timed out"))) {
                Toast.makeText(this, "Connection timeout error", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "An unknown error occurred.", Toast.LENGTH_SHORT).show();
            }
            // Do something when error occurred
            pDialog.dismiss();
            Log.w("Error_throwing:", error.toString());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", Constants.LICENSE_VALIDATE_SECRET_CODE, Constants.LICENSE_VALIDATE_SECRET_PASSWORD);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    public void showActivationAlert(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SplashActivity.this);
        builder1.setTitle("Information !");
        builder1.setMessage(message);
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}