package com.winapp.svcatering.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {

    private EditText emailId;
    private EditText mobileNoText;
    private EditText deviceNameText;
    private Button registerButton;
    private boolean isValidEmail,isValidMobile,isDeviceName;
    private SweetAlertDialog pDialog;
    private String deviceId;
    private SharedPreferences registerPreferences;
    private SharedPreferences.Editor registerPrefsEditor;
    private DBHelper dbHelper;

    private EditText deviceName;
    private EditText licence_1;
    private EditText licence_2;
    private EditText licence_3;
    private EditText licence_4;
    private EditText licence_5;
    private Button activateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Utils.isTablet(this)){
            setContentView(R.layout.activity_register_tablet);
        }else {
            setContentView(R.layout.activity_register_mobile);
        }

        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        registerPreferences = getSharedPreferences("registerPref", MODE_PRIVATE);
        registerPrefsEditor = registerPreferences.edit();
        dbHelper=new DBHelper(this);

        emailId=findViewById(R.id.email_id);
        mobileNoText=findViewById(R.id.mobile_no);
        registerButton=findViewById(R.id.btn_register);
        deviceNameText=findViewById(R.id.device_name);

        activateButton=findViewById(R.id.btn_activate);
        licence_1=findViewById(R.id.licence1);
        licence_2=findViewById(R.id.licence2);
        licence_3=findViewById(R.id.licence3);
        licence_4=findViewById(R.id.licence4);
        licence_5=findViewById(R.id.licence5);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validateDetails();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        licence_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 4) {
                    licence_2.requestFocus();
                }
            }
        });

        licence_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 4) {
                    licence_3.requestFocus();
                }
            }
        });

        licence_3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 4) {
                    licence_4.requestFocus();
                }
            }
        });

        licence_4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 4) {
                    licence_5.requestFocus();
                }
            }
        });

        licence_5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 4) {
                    licence_5.clearFocus();
                    licence_5.clearAnimation();
                    deviceNameText.requestFocus();
                }
            }
        });

        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateLicence();
            }
        });
    }

    public void validateLicence(){
        //Check the Licence Key Values
        StringBuilder builder=new StringBuilder();
        boolean isValidKey=false;
        String  licence_key="";
        if (!licence_1.getText().toString().isEmpty() &&
                !licence_2.getText().toString().isEmpty() &&
                !licence_3.getText().toString().isEmpty() &&
                !licence_4.getText().toString().isEmpty() &&
                !licence_5.getText().toString().isEmpty() ){
            builder.append(licence_1.getText().toString());
            builder.append("-");
            builder.append(licence_2.getText().toString());
            builder.append("-");
            builder.append(licence_3.getText().toString());
            builder.append("-");
            builder.append(licence_4.getText().toString());
            builder.append("-");
            builder.append(licence_5.getText().toString());

            licence_key=builder.toString().trim();
            isValidKey=true;
            Log.w("FullValidLicenceKey::",licence_key);
        }else{
            Toast.makeText(getApplicationContext(),"Enter the Valid Licence Key..!",Toast.LENGTH_SHORT).show();
        }

        // Check for a valid password.
        if (deviceNameText.getText().toString().isEmpty()) {
            deviceNameText.setError("Device name is Empty");
            isDeviceName = false;
        } else {
            isDeviceName = true;
        }

        if (isDeviceName && isValidKey) {
            setSession(licence_key,deviceNameText.getText().toString());
        }
    }

    public  void setSession(String licenceKey, String device){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
            String URL=Constants.NEW_LICENCE_URL;
            ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Device is Registering...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            StringRequest registerRequest = new StringRequest(Request.Method.POST, URL, response -> {
                Log.w("RegisterResponse:",response.toString());
                progressDialog.dismiss();
                try {
                    if (response!=null && !response.isEmpty()){
                        JSONObject jsonObject=new JSONObject(response);
                        String  status=jsonObject.optString("Status");
                        String  message=jsonObject.optString("Msg");
                        String  Url=jsonObject.optString("URL");
                        if (status.equals("1")){
                            setNewLicence(Url);
                        }else{
                            showAlert(message);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Error in Validate..,Try again",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.w("ErrorLicence:",error.toString());
                progressDialog.dismiss();
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("AppCode", Constants.APP_CODE);
                    parameters.put("LicenceKey", licenceKey.toString());
                    parameters.put("DeviceId", deviceId);
                    parameters.put("DeviceName", device);
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
                public void retry(VolleyError error) throws VolleyError {
                }
            });
            requestQueue.add(registerRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNewLicence(String apiUrl){
        try {
            registerPrefsEditor.putBoolean("saveRegister", true);
            registerPrefsEditor.putString("emailId", "");
            registerPrefsEditor.putString("mobileNo", "");
            registerPrefsEditor.putString("deviceName",deviceId);
            registerPrefsEditor.putString("apiUrl",apiUrl);
            registerPrefsEditor.commit();
            String fullUrl=apiUrl+Constants.PART_URL;
            boolean status=dbHelper.insertUrl(fullUrl);
            Toast.makeText(getApplicationContext(),"Licence validated Successfully...",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            finish();
        }catch (Exception e){}
    }

    public void showAlert(String message){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Information");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void validateDetails() throws JSONException {
        if (emailId.getText().toString().trim().isEmpty()){
            isValidEmail=false;
            emailId.setError(getResources().getString(R.string.invalid_email));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailId.getText().toString()).matches()) {
            emailId.setError(getResources().getString(R.string.invalid_email));
            isValidEmail = false;
        }else {
            isValidEmail=true;
        }
        if (mobileNoText.getText().toString().trim().isEmpty()){
            isValidMobile=false;
        }else {
            isValidMobile=true;
        }
        if (isValidMobile && isValidEmail){
            JSONObject registerObject=new JSONObject();
            registerObject.put("HandPhNo",mobileNoText.getText().toString());
            registerObject.put("EmailAddress",emailId.getText().toString());
            registerObject.put("DeviceMacId",deviceId);
            registerObject.put("OtpCode",null);
            setFirstTimeValidation(registerObject);
        }
    }

    private void setFirstTimeValidation(JSONObject jsonObject) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        String url = Constants.FIRST_TIME_ACTIVATION_URL+jsonObject.toString();
        Log.w("Given_RegisterUrl:", url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Device is Registering...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.w("FirstTimeValidationUrl",String.valueOf(response));
                        String result=response.optString("result");
                        String subject=response.optString("subject");
                        String message=response.optString("errorMessage");
                        boolean isactive=response.optBoolean("isActive");
                        boolean isstoped=response.optBoolean("isStoped");
                        boolean isexpiry=response.optBoolean("isExpired");
                        String catalog_api_url=response.optString("catelogApiUrl");
                        if (result.equals("fail")){
                            if (isactive && !isstoped && !isexpiry){
                                Intent intent=new Intent(RegisterActivity.this,ValidateUrlActivity.class);
                                intent.putExtra("validateUrl",catalog_api_url);
                                startActivity(intent);
                                finish();
                            }else {
                                showActivationAlert(message);
                            }
                        }else {
                            trigerOtp(jsonObject,catalog_api_url);
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
                    Toast.makeText(this, "Server is not connected to internet.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Your device is not connected to internet.",
                            Toast.LENGTH_SHORT).show();
                }
            } else if (error instanceof NetworkError || error.getCause() instanceof ConnectException
                    || (error.getCause().getMessage() != null
                    && error.getCause().getMessage().contains("connection"))){
                Toast.makeText(this, "Your device is not connected to internet.",
                        Toast.LENGTH_SHORT).show();
            } else if (error.getCause() instanceof MalformedURLException){
                Toast.makeText(this, "Bad Request.", Toast.LENGTH_SHORT).show();
            } else if (error instanceof ParseError || error.getCause() instanceof IllegalStateException
                    || error.getCause() instanceof JSONException
                    || error.getCause() instanceof XmlPullParserException){
                Toast.makeText(this, "Parse Error (because of invalid json or xml).",
                        Toast.LENGTH_SHORT).show();
            } else if (error.getCause() instanceof OutOfMemoryError){
                Toast.makeText(this, "Out Of Memory Error.", Toast.LENGTH_SHORT).show();
            }else if (error instanceof AuthFailureError){
                Toast.makeText(this, "server couldn't find the authenticated request.",
                        Toast.LENGTH_SHORT).show();
            } else if (error instanceof ServerError || error.getCause() instanceof ServerError) {
                Toast.makeText(this, "Server is not responding.", Toast.LENGTH_SHORT).show();
            }else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
                    || error.getCause() instanceof ConnectTimeoutException
                    || error.getCause() instanceof SocketException
                    || (error.getCause().getMessage() != null
                    && error.getCause().getMessage().contains("Connection timed out"))) {
                Toast.makeText(this, "Connection timeout error",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "An unknown error occurred.",
                        Toast.LENGTH_SHORT).show();
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

    private void trigerOtp(JSONObject jsonObject,String catalogUrl) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        String url = Constants.PURCHASE_LICENCE_VIA_OTP+jsonObject.toString();
        Log.w("PurchaseLicenseOTPSend:", url);

       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.w("Trigger OTP Response:",String.valueOf(response));
                        String result=response.optString("result");
                        String subject=response.optString("subject");
                        String message=response.optString("errorMessage");
                        if (result.equals("fail")){
                            showActivationAlert(message);
                        }else {
                            Intent intent=new Intent(RegisterActivity.this,OTPVerification.class);
                            intent.putExtra("mobileNo",mobileNoText.getText().toString());
                            intent.putExtra("emailId",emailId.getText().toString());
                            intent.putExtra("validateUrl",catalogUrl);
                            startActivity(intent);
                            finish();
                        }
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
                    Toast.makeText(this, "Server is not connected to internet.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Your device is not connected to internet.",
                            Toast.LENGTH_SHORT).show();
                }
            } else if (error instanceof NetworkError || error.getCause() instanceof ConnectException ){
                Toast.makeText(this, "Your device is not connected to internet.",
                        Toast.LENGTH_SHORT).show();
            } else if (error.getCause() instanceof MalformedURLException){
                Toast.makeText(this, "Bad Request.", Toast.LENGTH_SHORT).show();
            } else if (error instanceof ParseError || error.getCause() instanceof IllegalStateException
                    || error.getCause() instanceof JSONException
                    || error.getCause() instanceof XmlPullParserException){
                Toast.makeText(this, "Parse Error (because of invalid json or xml).",
                        Toast.LENGTH_SHORT).show();
            } else if (error.getCause() instanceof OutOfMemoryError){
                Toast.makeText(this, "Out Of Memory Error.", Toast.LENGTH_SHORT).show();
            }else if (error instanceof AuthFailureError){
                Toast.makeText(this, "server couldn't find the authenticated request.",
                        Toast.LENGTH_SHORT).show();
            } else if (error instanceof ServerError || error.getCause() instanceof ServerError) {
                Toast.makeText(this, "Server is not responding.", Toast.LENGTH_SHORT).show();
            }else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
                    || error.getCause() instanceof ConnectTimeoutException
                    || error.getCause() instanceof SocketException
                    || (error.getCause().getMessage() != null
                    && error.getCause().getMessage().contains("Connection timed out"))) {
                Toast.makeText(this, "Connection timeout error",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "An unknown error occurred.",
                        Toast.LENGTH_SHORT).show();
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

    public void showActivationAlert(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
        builder1.setTitle("Information !");
        builder1.setMessage(message);
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton(
                "CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}