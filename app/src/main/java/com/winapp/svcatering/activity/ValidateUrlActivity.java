package com.winapp.svcatering.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.utils.Constants;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ValidateUrlActivity extends AppCompatActivity {
    // Define the variables
    private Button validateUrl;
    private SweetAlertDialog pDialog;
    private EditText urlEdittext;
    private String givenUrl;
    private JsonObjectRequest jsonObjectRequest;
    private DBHelper dbHelper;
    private String validateUrlPort;
    private String validateRemainingUrl;
    private String validateFullUrl="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_validate_url);

        dbHelper=new DBHelper(this);
        urlEdittext=findViewById(R.id.domain_url);
        validateUrl=findViewById(R.id.btn_validate);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Validating the Url...");
        pDialog.setCancelable(false);

        if (getIntent() !=null){
            validateUrlPort=getIntent().getStringExtra("validateUrl");
            validateRemainingUrl=Constants.VALIDATE_URL_REMAINING;
        }
        validateFullUrl=validateUrlPort+validateRemainingUrl;
        Log.w("FullUrl:",validateFullUrl);
        if (!validateFullUrl.isEmpty()){
            setURLValidation(validateFullUrl);
        }else {
            showAlert();
        }
        urlEdittext.setText(validateFullUrl);
        validateUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                givenUrl=urlEdittext.getText().toString();
                if (!givenUrl.isEmpty()) {
                    setURLValidation(givenUrl);
                }else {
                    showAlert();
                }
            }
        });
    }

    private void setURLValidation(String url) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        //pDialog.show();
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                    try{
                        Log.w("Response_is:",response.toString());
                        if (response.length()>0){
                            if (response.optString("ErrorMessage").equals("null")){
                                wordPos(url,"api");
                            }else {
                                Toast.makeText(getApplicationContext(),"Given Url is invalid",Toast.LENGTH_LONG).show();
                            }
                        }
                       // pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
          //  pDialog.dismiss();
            Log.w("Error_throwing:",error.toString());
            Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
        }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD);
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

    public void wordPos(String url,String findString){
        int position = url.indexOf(findString);
        getString(url,position+4);
    }

    public void getString(String url,int lastpos){
        String substring = url.substring(0, lastpos);
        System.out.println(substring);
        System.out.println(substring.length());
        // Define the Client URL to Load Content..
        //substring=Constants.NEW_LIVE_URL_AADHI;
        Log.w("GivenSubString:",substring);
        boolean status=dbHelper.insertUrl(substring);
        if (status){
            Intent intent=new Intent(ValidateUrlActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(getApplicationContext(),"Error in Insert Url",Toast.LENGTH_LONG).show();
        }
    }

    public void showAlert(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Warning !")
                .setContentText("Please enter URL")
                .setConfirmText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
     }
}