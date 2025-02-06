package com.winapp.svcatering.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.winapp.svcatering.R;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;

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
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OTPVerification extends AppCompatActivity {

    private Button btnVerify;
    private String emailId;
    private String mobileNo;
    private String deviceId;
    private PinView pinView;
    private String otpValue;
    private String validateUrl;
    private SweetAlertDialog pDialog;
    JsonObjectRequest jsonObjectRequest;
    SessionManager session;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_o_t_p_verification);
        session=new SessionManager(this);

        btnVerify=findViewById(R.id.verify);
        pinView=findViewById(R.id.pinView);
        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (getIntent()!=null){
            mobileNo=getIntent().getStringExtra("mobileNo");
            emailId=getIntent().getStringExtra("emailId");
            validateUrl=getIntent().getStringExtra("validateUrl");
        }

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 otpValue= Objects.requireNonNull(pinView.getText()).toString();
               //  Intent intent=new Intent(OTPVerification.this,LoginActivity.class);
              //   startActivity(intent);
              //   finish();
                if (!otpValue.isEmpty()){
                    JSONObject registerObject=new JSONObject();
                    try {
                        registerObject.put("HandPhNo",mobileNo);
                        registerObject.put("EmailAddress",emailId);
                        registerObject.put("OtpCode",otpValue);
                       // registerObject.put("DeviceMacId",deviceId);
                        setOTPVerification(registerObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"OTP is Empty",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setOTPVerification(JSONObject jsonObject) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        String url = Constants.VALIDATE_LICENCE_VIA_OTP+jsonObject.toString();
        Log.w("Given_RegisterUrl:", url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("OTP is Validating...");
        pDialog.setCancelable(false);
        pDialog.show();
        jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        //  {"result":"fail","subject":"Not Available","status":500,"errorMessage":"License is not available.",
                        //  "licenseMobileRequest":{"handPhNo":"9790664487","emailAddress":"sathish.infotech2000@gmail.com",
                        //  "companyName":"","licenseLimit":2,"deviceMacId":null,"otpCode":null}}
                        Log.w("OTP_Response:", String.valueOf(response));
                        String result=response.optString("result");
                        String subject=response.optString("subject");
                        String message=response.optString("errorMessage");
                        if (result.equals("fail")){
                            showActivationAlert(message);
                        }else {
                            session.createRegister(mobileNo,emailId);
                            Intent intent=new Intent(OTPVerification.this,ValidateUrlActivity.class);
                            intent.putExtra("validateUrl",validateUrl);
                            startActivity(intent);
                            finish();
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
        AlertDialog.Builder builder1 = new AlertDialog.Builder(OTPVerification.this);
        builder1.setTitle("Information !");
        builder1.setMessage(message+" Contact Administrator");
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