package com.winapp.svcatering.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.AppUtils;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.HomePageModel;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewCompanySwitchActivity extends AppCompatActivity {

    private TextView loadingText;
    private String companyId;
    private String locationCode;
    private String companyName;
    private ArrayList<CustomerModel> customerList;
    private DBHelper dbHelper;
    public static ArrayList<ProductsModel> productList;
    public SessionManager session;
    public String username;
    public String password;
    public String rollname;
    public String isuserpermission;
    public String ismainlocation;
    public String address1;
    public String address2;
    public String address3;
    public HashMap<String ,String> user;
    private ArrayList<CustomerDetails> allCustomersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_company_switch);
        dbHelper=new DBHelper(this);
        session=new SessionManager(this);
        user=session.getUserDetails();
        username=user.get(SessionManager.KEY_USER_NAME);
        password=user.get(SessionManager.KEY_PASSWORD);
        rollname=user.get(SessionManager.KEY_ROLL_NAME);
        isuserpermission=user.get(SessionManager.KEY_USER_PERMISSION);
        ismainlocation=user.get(SessionManager.IS_LOCATION_PERMISSION);
        address1=user.get(SessionManager.KEY_ADDRESS1);
        address2=user.get(SessionManager.KEY_ADDRESS2);
        address3=user.get(SessionManager.KEY_ADDRESS3);

        loadingText=findViewById(R.id.indicator_text);
        // get the values from the Intent
        if (getIntent()!=null){
            companyId=getIntent().getStringExtra("companyCode");
            locationCode=getIntent().getStringExtra("locationCode");
            companyName=getIntent().getStringExtra("companyName");
        }

        try {
           // getCustomers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCustomers() throws JSONException {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyId);
        String url= Utils.getBaseUrl(this) +"MasterApi/GetCustomer_All?Requestdata="+jsonObject.toString();
        customerList=new ArrayList<>();
        allCustomersList=new ArrayList<>();
        loadingText.setText("Customers List Loading...");
        Log.w("Given_url:",url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.w("Response_is_Customer:", response.toString());
                        // Loop through the array elements
                        for (int i = 0; i < response.length(); i++) {
                            // Get current json object
                            JSONObject customerObject = response.getJSONObject(i);
                            CustomerModel model=new CustomerModel();
                            if (customerObject.optBoolean("IsActive")) {
                                model.setCustomerCode(customerObject.optString("CustomerCode"));
                                model.setCustomerName(customerObject.optString("CustomerName"));
                                model.setAddress1(customerObject.optString("Address1"));
                                model.setHaveTax(customerObject.optString("HaveTax"));
                                model.setTaxType(customerObject.optString("TaxType"));
                                model.setTaxPerc(customerObject.optString("TaxPerc"));
                                model.setTaxCode(customerObject.optString("TaxCode"));
                                if (customerObject.optString("BalanceAmount").equals("null") || customerObject.optString("BalanceAmount").isEmpty()) {
                                    model.setOutstandingAmount("0.00");
                                } else {
                                    model.setOutstandingAmount(customerObject.optString("BalanceAmount"));
                                }
                                customerList.add(model);

                              /*  CustomerDetails details=new CustomerDetails();
                                details.setCustomerCode(productObject.optString("CustomerCode"));
                                details.setCustomerName(productObject.optString("CustomerName"));
                                details.setPhoneNo(productObject.optString("PhoneNo"));
                                details.setCustomerAddress1(productObject.optString("Address1"));
                                details.setCustomerAddress2(productObject.optString("Address2"));
                                details.setCustomerAddress3(productObject.optString("Address3"));
                                details.setIsActive(productObject.optString("IsActive"));
                                details.setHaveTax(productObject.optString("HaveTax"));
                                details.setTaxType(productObject.optString("TaxType"));
                                details.setTaxPerc(productObject.optString("TaxPerc"));
                                details.setTaxCode(productObject.optString("TaxCode"));
                                details.setCreditLimit(productObject.optString("CreditLimit"));
                                details.setCountry(productObject.optString("Country"));
                                details.setCurrencyCode(productObject.optString("CurrencyCode"));
                                details.setBalanceAmount(productObject.optString("BalanceAmount"));

                                allCustomersList.add(details);*/
                            }
                        }
                        if (customerList.size()>0){
                          //  new InsertCustomerTask().execute();
                           // dbHelper.removeAllCustomersDetails();
                           // dbHelper.insertCustomersDetails(allCustomersList);
                            dbHelper.removeAllCustomers();
                            dbHelper.insertCustomerList(customerList);
                            JSONObject object =new JSONObject();
                            try {
                                object.put("CompanyCode",companyId);
                                object.put("LocationCode",locationCode);
                                object.put("PageSize",5000);
                                object.put("PageNo","1");
                                getAllProducts(object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
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

    private class InsertCustomerTask extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(Void...arg0) {
            dbHelper.removeAllCustomers();
            dbHelper.insertCustomerList(customerList);
            return "You are at PostExecute";
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    public void getAllProducts(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(NewCompanySwitchActivity.this);
        // String url= Utils.getBaseUrl(getContext()) +"ProductApi/GetProduct_All?Requestdata={CompanyCode:"+companyCode+",PageSize:1300,PageNo:"+pageNo+"}";
        String url=Utils.getBaseUrl(NewCompanySwitchActivity.this) +"ProductApi/GetProduct_All_ByFacets?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_product_url:",url);
        productList=new ArrayList<>();
        loadingText.setText("Products Loading....");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Response_is:",response.toString());
                        // Loop through the array elements
                        for(int i=0;i<response.length();i++){
                            // Get current json object
                            JSONObject productObject = response.getJSONObject(i);
                            ProductsModel product =new ProductsModel();
                            if (productObject.optBoolean("IsActive")) {
                                product.setCompanyCode(productObject.optString("CompanyCode"));
                                product.setProductName(productObject.optString("ProductName"));
                                product.setProductCode(productObject.optString("ProductCode"));
                                product.setWeight(productObject.optString("Weight"));
                                product.setProductImage(productObject.optString("ProductImagePath"));
                                product.setRetailPrice(productObject.optDouble("RetailPrice"));
                                product.setCartonPrice(productObject.optString("CartonPrice"));
                                product.setWholeSalePrice(productObject.optString("WholeSalePrice"));
                                product.setPcsPerCarton(productObject.optString("PcsPerCarton"));
                                product.setUnitCost(productObject.optString("UnitCost"));
                                product.setStockQty(productObject.optString("StockQty"));
                                product.setUomCode(productObject.optString("UOMCode"));
                                //  product.setProductBarcode(productObject.optString("BarCode")); Add values In Futue
                                product.setProductBarcode("");
                                productList.add(product);
                            }
                        }
                        HomePageModel.productsList=new ArrayList<>();
                        HomePageModel.productsList.addAll(productList);
                        dbHelper.removeAllProducts();
                        if (productList.size()>0){
                           // dbHelper.insertProducts(this,productList);
                            AppUtils.setProductsList(productList);
                        }
                        if (getIntent().getStringExtra("from").equals("Login")){
                            Toast.makeText(getApplicationContext(),"Welcome ,"+username,Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(NewCompanySwitchActivity.this,DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                           getCompanyDetails(companyId);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
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

    public void getCompanyDetails(String companyCode) throws JSONException {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyCode);
        loadingText.setText("Company Details Loading....");
        String url=Utils.getBaseUrl(this) +"MasterApi/Get_Company?Requestdata="+jsonObject.toString();
        Log.w("Given_url:",url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("_CompanyDetails:",response.toString());
                        if (response.length()>0) {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object=response.getJSONObject(i);
                                Log.w("GivenCompanyLogo:",object.optString("LogoString"));
                                session.setCompanyDetails(object.optString("ShortCode"),object.optString("LogoString"));
                                // No need Load the Content Straight to the Dashboard
                                Toast.makeText(getApplicationContext(),"Company Switched Successfully",Toast.LENGTH_SHORT).show();
                                switchCompany();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"Error in Getting Company details, Try again..",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
            Toast.makeText(getApplicationContext(),"Server not responding,please try again...",Toast.LENGTH_LONG).show();
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

    public void switchCompany(){
        dbHelper.removeAllItems();
        dbHelper.removeCustomer();
        dbHelper.removeAllInvoices();
       // session.createLoginSession(username,password,rollname,locationCode,isuserpermission,ismainlocation, companyId,companyName,address1,address2,address3,"","","","");
        Intent intent=new Intent(NewCompanySwitchActivity.this,DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}