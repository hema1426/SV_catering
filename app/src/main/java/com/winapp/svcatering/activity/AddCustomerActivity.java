package com.winapp.svcatering.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.winapp.svcatering.R;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.TaxModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.SingleChoiceDialogFragment;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddCustomerActivity extends AppCompatActivity implements SingleChoiceDialogFragment.SingleChoiceListener{

    private EditText addTaxType;
    private EditText customerCode;
    private EditText customerName;
    private EditText postalCode;
    private EditText country;
    private EditText address1;
    private EditText address2;
    private EditText address3;
    private EditText deliveryAddress;
    private EditText emailId;
    private EditText notesText;
    private String companyCode;
    private FloatingActionButton addCustomer;
    private boolean isValidName=false,isValidPostalAddress=false,isValidCountry=false,isValidAddress1=false,isValidTax=false;
    private SweetAlertDialog pDialog;
    SessionManager session;
    HashMap<String, String> user;
    private static String currentDate;
    private static String userName;
    ArrayList<TaxModel> taxList;
    ArrayList<CustomerModel> customerList;
    ProgressDialog dialog;
    DBHelper dbHelper;
    boolean boolean_permission;

    public static int REQUEST_PERMISSIONS = 154;

    private static String FILE;
    String[] invoiceTitlelistArray, invoicepricelistArray, invoiceQNTlistArray, invoiceNOlistArray;
    String invoiceNO = "";
    TextView path_pdf;
    File storagePath;
    /**
     * Called when the activity is first created.
     */

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
       // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
     //   FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
       // getLayoutInflater().inflate(R.layout.activity_add_customer, contentFrameLayout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add New Customer");
        setContentView(R.layout.activity_add_customer);

        dbHelper=new DBHelper(this);
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);

        addTaxType=findViewById(R.id.tax_type);
        customerName=findViewById(R.id.customer_name_value);
        postalCode=findViewById(R.id.postal_code);
        country=findViewById(R.id.country);
        address1=findViewById(R.id.address1);
        address2=findViewById(R.id.address2);
        address3=findViewById(R.id.address3);
        deliveryAddress=findViewById(R.id.delivery_address);
        emailId=findViewById(R.id.email_id);
        notesText=findViewById(R.id.notes);
        session = new SessionManager(this);
        user = session.getUserDetails();
        companyCode = user.get(SessionManager.KEY_COMPANY_CODE);
        addCustomer=findViewById(R.id.add_customer);
        customerCode=findViewById(R.id.customer_code);
        userName=user.get(SessionManager.KEY_USER_NAME);

        customerCode.setText(String.valueOf(getCustomerId()));
        taxList=new ArrayList<>();

        getAllTaxes(companyCode);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
        currentDate = df.format(c);

        addTaxType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment singleChoiceDialog = new SingleChoiceDialogFragment(taxList);
                singleChoiceDialog.setCancelable(false);
                singleChoiceDialog.show(getSupportFragmentManager(), "Select Tax");
            }
        });

        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validateDetails();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });





       /* if (boolean_permission){
            storagePath = Environment.getExternalStorageDirectory();
            invoiceNO = "KJHS_q23";
            FILE = Environment.getExternalStorageDirectory() + "/" + invoiceNO+".pdf";
            fatchinvoiceList();
        }else {
            fn_permission();
            storagePath = Environment.getExternalStorageDirectory();
            invoiceNO = "KJHS_q23";
            FILE = Environment.getExternalStorageDirectory() + "/" + invoiceNO+".pdf";
            fatchinvoiceList();
        }*/
    }


    private void validateDetails() throws JSONException {

        if (customerName.getText().toString().isEmpty()){
            customerName.setError("Customer name empty");
            isValidName=false;
        }else {
            isValidName=true;
        }
        if (postalCode.getText().toString().isEmpty()){
            isValidPostalAddress=false;
            postalCode.setError("Postal Code is empty");
        }else {
            isValidPostalAddress=true;
        }

        if (country.getText().toString().isEmpty()){
            isValidCountry=false;
            country.setError("Country is empty");
        }else {
            isValidCountry=true;
        }

        if (address1.getText().toString().isEmpty()){
            isValidAddress1=false;
            address1.setError("Address is empty");
        }else {
            isValidAddress1=true;
        }
        if (addTaxType.getText().toString().isEmpty()){
            isValidTax=false;
            addTaxType.setError("Tax type is empty");
        }else {
            isValidTax=true;
        }

        // Validate the All required Values to Check
        if (isValidName && isValidPostalAddress && isValidCountry && isValidTax && isValidAddress1){
            validateTheDetails();
        }
    }

    @Override
    public void onPositiveButtonClicked(String[] list, int position) {
        addTaxType.setText(list[position]);
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    public void validateTheDetails() throws JSONException {
        String taxperc=null;
        String taxcode=null;
        String taxtype=null;
        if (!addTaxType.getText().toString().equals("Tax Type")){
            for (TaxModel model:taxList){
                if (model.getTaxName().equals(addTaxType.getText().toString())){
                    taxperc=model.getTaxPerc();
                    taxcode=model.getTaxCode();
                    taxtype=model.getTaxType();
                }
            }
            Log.e("TaxPerce:",taxperc);
        }

        JSONObject rootObject=new JSONObject();
        rootObject.put("CompanyCode",companyCode);
        rootObject.put("CustomerCode",customerCode.getText().toString());
        rootObject.put("CustomerName",customerName.getText().toString());
        rootObject.put("CurrencyCode","SGD");
       // rootObject.put("CurrencyRate","1");
      //  rootObject.put("CurrencyName","");
        rootObject.put("CustomerNameML",null);
        rootObject.put("CustomerPrefix",null);
        rootObject.put("ContactPerson",null);
        rootObject.put("ContactNo",null);
        rootObject.put("Address1",address1.getText().toString());
        rootObject.put("Address2",address2.getText().toString());
        rootObject.put("Address3",address3.getText().toString());
        rootObject.put("PhoneNo",null);
        rootObject.put("HandphoneNo",null);
        rootObject.put("HaveTax",null);
        rootObject.put("Email",emailId.getText().toString());
        rootObject.put("CreateUser",userName);
        rootObject.put("CreateDate",currentDate);
        rootObject.put("ModifyUser",userName);
        rootObject.put("ModifyDate",currentDate);
        rootObject.put("isActive",true);
        rootObject.put("FaxNo",null);
        rootObject.put("TaxType",taxtype);
        rootObject.put("TaxCode",taxcode);
        rootObject.put("TaxPerc",taxperc);
        rootObject.put("CreditLimit",null);
        rootObject.put("Remarks",notesText.getText().toString());
        rootObject.put("DeliveryCode","0");
        rootObject.put("Delivery",deliveryAddress.getText().toString());
        rootObject.put("Latitude",null);
        rootObject.put("Longitude",null);
        rootObject.put("CompanyName","");
        rootObject.put("PostalCode",postalCode.getText().toString());
        rootObject.put("Country",country.getText().toString());

        addCustomer(rootObject);

    }


    public  void addCustomer(JSONObject jsonBody){
        try {
            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = Utils.getBaseUrl(this) + "MasterApi/AddCustomer?Requestdata="+jsonBody.toString()+"";
            // Initialize a new JsonArrayRequest instance
            Log.w("Given_url:", url);
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Adding Customer...");
            pDialog.setCancelable(false);
            pDialog.show();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    null,
                    response -> {
                        try {
                            Log.w("Response_is:", response.toString());
                            if (response.length() > 0) {
                                boolean isSaved=response.optBoolean("IsSaved");
                                String result=response.optString("Result");
                                if (isSaved && result.equals("pass")){
                                    Toast.makeText(getApplicationContext(),"Customer Created Successfully",Toast.LENGTH_LONG).show();
                                    Intent intent=new Intent(AddCustomerActivity.this,CustomerListActivity.class);
                                    intent.putExtra("Message","newCustomer");
                                    startActivity(intent);
                                    finish();
                                    // getCustomerDetails(customerCode.getText().toString());
                                   // getCustomers(companyCode);
                                }else {
                                    Toast.makeText(getApplicationContext(),"Error in Server",Toast.LENGTH_LONG).show();
                                }
                            }
                            pDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // Do something when error occurred
                pDialog.dismiss();
                Log.w("Error_throwing:", error.toString());
            }) {
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
        }catch (Exception ex){}
    }

    public int getCustomerId(){
        int min = 10000;
        int max = 20000;
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

    public void getAllTaxes(String companyCode){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        String url= Utils.getBaseUrl(this) +"MasterApi/GetTax_All?Requestdata={\"CompanyCode\":\""+companyCode+"\"}";
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting Taxes...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Response_is:",response.toString());
                        pDialog.dismiss();
                        if (response.length()>0) {
                            for (int i = 0; i < response.length(); i++) {
                                // Get current json object
                                JSONObject object=response.getJSONObject(i);
                                TaxModel model=new TaxModel();
                                model.setTaxCode(object.optString("TaxCode"));
                                model.setTaxName(object.optString("TaxName"));
                                model.setTaxPerc(object.optString("TaxPercentage"));
                                model.setTaxShortCode(object.optString("ShortCode"));
                                model.setTaxType(object.optString("TaxType"));
                                taxList.add(model);
                            }
                        }
                        if (taxList.size()>0){
                            new SingleChoiceDialogFragment(taxList);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
            Toast.makeText(this,error.toString(),Toast.LENGTH_LONG).show();
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

    public void getCustomers(String companyCode) throws JSONException {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyCode);
        String url= Utils.getBaseUrl(this) +"MasterApi/GetCustomer_All?Requestdata="+jsonObject.toString();
        customerList=new ArrayList<>();
        Log.w("Given_url:",url);
        dialog=new ProgressDialog(AddCustomerActivity.this);
        dialog.setMessage("Updating Customers List...");
        dialog.setCancelable(false);
        dialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.w("Response_is:", response.toString());

                        // Loop through the array elements
                        for (int i = 0; i < response.length(); i++) {
                            // Get current json object
                            JSONObject customerObject = response.getJSONObject(i);
                            if (customerObject.optBoolean("IsActive")){
                                CustomerModel model=new CustomerModel();
                                model.setCustomerCode(customerObject.optString("CustomerCode"));
                                model.setCustomerName(customerObject.optString("CustomerName"));
                                model.setAddress1(customerObject.optString("Address1"));
                                model.setAddress2(customerObject.optString("Address2"));
                                model.setAddress3(customerObject.optString("Address3"));
                                model.setHaveTax(customerObject.optString("HaveTax"));
                                model.setTaxType(customerObject.optString("TaxType"));
                                model.setTaxPerc(customerObject.optString("TaxPerc"));
                                model.setTaxCode(customerObject.optString("TaxCode"));
                                model.setCustomerAddress(customerObject.optString("Address1"));
                                model.setCustomerBarcode(customerObject.optString("BarCode"));
                                // model.setCustomerBarcode(String.valueOf(i));
                                if (customerObject.optString("BalanceAmount").equals("null") || customerObject.optString("BalanceAmount").isEmpty()){
                                    model.setOutstandingAmount("0.00");
                                }else {
                                    model.setOutstandingAmount(customerObject.optString("BalanceAmount"));
                                }
                                customerList.add(model);
                            }
                        }
                        dialog.dismiss();
                        if (customerList.size()>0){
                            dbHelper.removeAllCustomers();
                            dbHelper.insertCustomerList(customerList);
                            Intent intent=new Intent(AddCustomerActivity.this,CustomerListActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(),"Error in adding Customer in DB",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    dialog.dismiss();
                    // Do something when error occurred
                    Log.w("Error_throwing:",error.toString());
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
        jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(jsonArrayRequest);
    }

    private void getCustomerDetails(String customerCode) throws JSONException {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(AddCustomerActivity.this);
        // Initialize a new JsonArrayRequest instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CustomerCode",customerCode);
        jsonObject.put("CompanyCode",companyCode);
        String url= Utils.getBaseUrl(AddCustomerActivity.this) +"MasterApi/GetCustomer?Requestdata="+jsonObject.toString();
        Log.w("Given_url:",url);
        customerList=new ArrayList<>();
        dialog=new ProgressDialog(AddCustomerActivity.this);
        dialog.setMessage("Updating Customers List...");
        dialog.setCancelable(false);
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Res_is_get_summary:",response.toString());
                        if (response.length()>0) {
                            if (response.optString("ErrorMessage").equals("null")){
                                if (response.optBoolean("IsActive")){
                                    CustomerModel model=new CustomerModel();
                                    model.setCustomerCode(response.optString("CustomerCode"));
                                    model.setCustomerName(response.optString("CustomerName"));
                                    model.setAddress1(response.optString("Address1"));
                                    model.setAddress2(response.optString("Address2"));
                                    model.setAddress3(response.optString("Address3"));
                                    model.setHaveTax(response.optString("HaveTax"));
                                    model.setTaxType(response.optString("TaxType"));
                                    model.setTaxPerc(response.optString("TaxPerc"));
                                    model.setTaxCode(response.optString("TaxCode"));
                                    model.setCustomerAddress(response.optString("Address1"));
                                    model.setCustomerBarcode(response.optString("BarCode"));
                                    // model.setCustomerBarcode(String.valueOf(i));
                                    if (response.optString("BalanceAmount").equals("null") || response.optString("BalanceAmount").isEmpty()){
                                        model.setOutstandingAmount("0.00");
                                    }else {
                                        model.setOutstandingAmount(response.optString("BalanceAmount"));
                                    }
                                    customerList.add(model);
                                }
                                dialog.dismiss();
                                if (customerList.size()>0){
                                    dbHelper.insertCustomerList(customerList);
                                    Intent intent=new Intent(AddCustomerActivity.this,CustomerListActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Toast.makeText(getApplicationContext(),"Error in adding Customer in DB",Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(AddCustomerActivity.this,"Error in getting response",Toast.LENGTH_LONG).show();
                            }
                        }
                        //pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            // pDialog.dismiss();
            Log.w("Error_throwing:",error.toString());
            // Toast.makeText(this,error.toString(),Toast.LENGTH_LONG).show();
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




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;

         /*   case R.id.action_remove:
                showRemoveAlert();
                break;*/
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}