package com.winapp.svcatering.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.OrderHistoryAdapter;
import com.winapp.svcatering.model.OrderHistoryModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OrderHistoryActivity extends AppCompatActivity {

    // Define the variable to use the Activity
    private TextView totalOrderTotal;
    private TextView totalOutstanding;
    private TextView emptytxt_hisl;
    private RecyclerView orderListView;
    private Button btnReorder;
    private ArrayList<OrderHistoryModel> ordersList;
    private OrderHistoryAdapter orderHistoryAdapter;
    private SweetAlertDialog pDialog;
    private String companyCode;
    private String customerCode;
    private String activityFrom;
    private LinearLayout emptyLayout;
    private String customername;
    private String locationCode;
    String username="";
    private SessionManager session;
    private HashMap<String,String > user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        totalOrderTotal=findViewById(R.id.total_orders);
        totalOutstanding=findViewById(R.id.total_outstanding);
        orderListView=findViewById(R.id.orderListView);
        btnReorder=findViewById(R.id.btn_reorder);
        emptyLayout=findViewById(R.id.empty_layout);
        emptytxt_hisl = findViewById(R.id.emptytxt_his);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // Get the all orders of the particular customer
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        username=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);

        Log.w("activity_cg",getClass().getSimpleName().toString());

        if (getIntent()!=null){
            companyCode=getIntent().getStringExtra("companyCode");
            customerCode=getIntent().getStringExtra("custCodeHistory");
            activityFrom=getIntent().getStringExtra("activityHistory");
            customername=getIntent().getStringExtra("custNameHistory");
            getSupportActionBar().setTitle(customername+" - "+"Order History");
            Log.w("activityFrom_his",""+activityFrom);
//            JSONObject jsonObject=new JSONObject();
//            try {
////                jsonObject.put("CompanyCode",companyCode);
////                jsonObject.put("CustomerCode",customerCode);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            if (activityFrom.equals("SalesOrder")){
                try {
                    getCustomerSOHistory(customerCode);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }else {
                try {
                    getCustomerInvoiceHistory(customerCode);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }            }
        }

    }

    private void getCustomerSOHistory(String customer_id) throws JSONException {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CustomerCode",customer_id);
        jsonObject.put("FromDate","");
        jsonObject.put("ToDate","");
        jsonObject.put("LocationCode",locationCode);
        jsonObject.put("User",username);
        jsonObject.put("DocStatus","");
        String url= Utils.getBaseUrl(this) +"salesOrderList" ;
        Log.w("Given_url_SO_history:",url+jsonObject);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting Orders Details...");
        pDialog.setCancelable(false);
        pDialog.show();
        ordersList=new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try{
                Log.w("OrderHistoryRes_so:",response.toString());

                if (response.length() > 0) {
                    String statusCode=response.optString("statusCode");
                    String statusMessage=response.optString("statusMessage");

                    if (statusCode.equals("1")){
                        JSONArray invoiceArray=response.optJSONArray("responseData");
                        for (int i=0;i<invoiceArray.length();i++){
                            JSONObject object=invoiceArray.optJSONObject(i);
                            OrderHistoryModel model=new OrderHistoryModel();

                            model.setCustomerName(object.optString("customerName"));
                            model.setOrderDate(object.optString("soDate"));
                            model.setCustomerCode(object.optString("customerCode"));
                            model.setOrderId(object.optString("code"));
                            model.setOrderNumber("SalesOrder No# : "+object.optString("soNumber"));
                            model.setPaidAmount("Paid Amt : $ "+object.optString("paidAmount"));
                            model.setDueAmount("$ "+object.optString("balance"));
                            //   model.setOrderStatus(object.optString("invoiceStatus"));
                            model.setDueDelayDays("");

                            model.setOrderTime("");

                            ordersList.add(model);
                        }
                        if (ordersList.size() > 0) {
                            emptytxt_hisl.setVisibility(View.GONE);
                            orderListView.setVisibility(View.VISIBLE);
                            setOrdersAdapter(ordersList);
                        }
                        else{
                            orderListView.setVisibility(View.GONE);
                            emptytxt_hisl.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        orderListView.setVisibility(View.GONE);
                        emptytxt_hisl.setVisibility(View.VISIBLE);
                    }
                }
                pDialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }
        }, error -> {
            pDialog.dismiss();
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

    private void getCustomerInvoiceHistory(String customer_id) throws JSONException {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CustomerCode",customer_id);
        jsonObject.put("FromDate","");
        jsonObject.put("ToDate","");
        jsonObject.put("LocationCode",locationCode);
        jsonObject.put("User",username);
        jsonObject.put("DocStatus","");
        String url= Utils.getBaseUrl(this) +"InvoiceList" ;
        Log.w("Given_url_history:",url+jsonObject);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting Orders Details...");
        pDialog.setCancelable(false);
        pDialog.show();
        ordersList=new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try{
                Log.w("OrderHistoryResponse:",response.toString());

                if (response.length() > 0) {
                    String statusCode=response.optString("statusCode");
                    String statusMessage=response.optString("statusMessage");

                    if (statusCode.equals("1")){
                        JSONArray invoiceArray=response.optJSONArray("responseData");
                        for (int i=0;i<invoiceArray.length();i++){
                            JSONObject object=invoiceArray.optJSONObject(i);
                            OrderHistoryModel model=new OrderHistoryModel();

                            model.setCustomerName(object.optString("customerName"));
                            model.setOrderDate(object.optString("invoiceDate"));
                            model.setCustomerCode(object.optString("customerCode"));
                            model.setOrderId(object.optString("code"));
                            model.setOrderNumber("Invoice No# : "+object.optString("invoiceNumber"));
                            model.setPaidAmount("Paid Amt : $ "+object.optString("paidAmount"));
                            model.setDueAmount("$ "+object.optString("balance"));
                         //   model.setOrderStatus(object.optString("invoiceStatus"));
                            model.setDueDelayDays("");

                            model.setOrderTime("");

                            ordersList.add(model);
                        }
                        if (ordersList.size() > 0) {
                            emptytxt_hisl.setVisibility(View.GONE);
                            orderListView.setVisibility(View.VISIBLE);

                            setOrdersAdapter(ordersList);
                        }
                        else{
                            orderListView.setVisibility(View.GONE);
                            emptytxt_hisl.setVisibility(View.VISIBLE);
                        }
                    } else{
                        orderListView.setVisibility(View.GONE);
                        emptytxt_hisl.setVisibility(View.VISIBLE);
                    }
                }
                pDialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }
        }, error -> {
            pDialog.dismiss();
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


    private void getAllSalesOrderList(JSONObject jsonObject) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        String url= Utils.getBaseUrl(this) +"ProductApi/GetSalesOrderHistory?Requestdata="+jsonObject.toString();
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting Orders Details...");
        pDialog.setCancelable(false);
        pDialog.show();
        ordersList=new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url, null, response -> {
                    try{
                        Log.w("OrderHistoryResponse:",response.toString());
                        if (response.length()>0) {
                            for (int i = 0; i < response.length(); i++) {
                                // Get current json object

                                // [{"CompanyCode":1,"PageSize":null,"PageNo":null,"SoNo":"SO00010","SoDate":"\/Date(1605456000000)\/",
                                // "DeliveryDate":null,"LocationCode":"HQ","CustomerCode":"10367","ContactPerson":null,
                                // "IsRequiredWhatsAppMessage":null,"Total":62.5,"FTotal":null,"ItemDiscount":null,
                                // "BillDIscount":null,"TotalDiscount":null,"SubTotal":62.5,"FSubTotal":null,"Tax":0,
                                // "FTax":null,"NetTotal":62.5,"Remarks":"rest","SONextNo":0,"Status":0,"CurrencyCode":"null",
                                // "CurrencyName":null,"CurrencyValue":null,"CurrencyRate":1,"CalculateMethod":null,
                                // "CreateUser":"winapp","CreateDate":"\/Date(1605540360000)\/","ModifyUser":"winapp",
                                // "ModifyDate":"\/Date(1605541500000)\/","ErrorMessage":null,"Mode":null,
                                // "SoDateString":"16-11-2020","CreateDateString":"11:26:00","HaveTax":null,"OrderDateString":null,
                                // "DeliveryDateString":"","CustomerName":"Test zero","Address1":"add1","Address2":"add2","Address3":"add3",
                                // "PhoneNo":null,"DelCustomerName":null,"DelAddress1":null,"DelAddress2":null,"DelAddress3":null,
                                // "DelPhoneNo":null,"DelAttention":null,"Attention":null,"HandphoneNo":null,"IdentityUrl":null,
                                // "DeliveryCode":0,"OrderNo":null,"OrderDate":null,"QuoteNo":null,"TaxType":null,"TaxPerc":null,
                                // "TaxCode":0,"Email":null,"Website":null,"CompanyName":null,"VanName":null,"FNetTotal":0,"IsApproved":false,
                                // "ApprovedBy":"","ApprovedDate":null,"ApprovedDateString":"","SoPrintCount":null,"BillDiscountPercentage":null,
                                // "SalesAdditionInfo":null,"SalesManInfo":null,"PaidAmount":0,"BalanceAmount":62.5,"CustomerContactCode":0,
                                // "FBillDiscount":null,"SoDetails":null,"SalesAdditionalInfoMasters":null,"InvoiceHeaderReport":null,
                                // "FaxNo":null,"OutstandingBalance":null,"CreditLimit":null,"SRBalanceAmount":null,"PostalCode":null,
                                // "CustomerTypeCode":null}]
                                JSONObject invoiceObject = response.getJSONObject(i);
                                OrderHistoryModel model=new OrderHistoryModel();
                                model.setOrderDate(invoiceObject.optString("SoDateString"));
                                model.setOrderTime("");
                                model.setCustomerName(invoiceObject.optString("CustomerName"));
                                model.setCustomerCode(invoiceObject.optString("CustomerCode"));
                                model.setOrderNumber("Order No# : "+invoiceObject.optString("SoNo"));
                                model.setPaidAmount("Paid Amt : $ "+invoiceObject.optString("PaidAmount"));
                                model.setDueAmount("$ "+invoiceObject.optString("BalanceAmount"));
                                model.setOrderId(invoiceObject.optString("SoNo"));
                                model.setDueDelayDays("");
                                int status=invoiceObject.optInt("Status");
                                if (status==0){
                                    model.setOrderStatus("Open");
                                }else if (status==1){
                                    model.setOrderStatus("Delivered");
                                }else {
                                    model.setOrderStatus("Open");
                                }
                                ordersList.add(model);
                            }
                        }
                        pDialog.dismiss();
                        setOrdersAdapter(ordersList);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
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

    private void setOrdersAdapter(ArrayList<OrderHistoryModel> ordersList) {
        if (ordersList.size()>0){
            orderListView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            orderListView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OrderHistoryActivity.this);
            orderListView.setLayoutManager(layoutManager);
            orderHistoryAdapter = new OrderHistoryAdapter(this,orderListView, activityFrom,ordersList, new OrderHistoryAdapter.CallBack() {
                @Override
                public void calculateNetTotal(ArrayList<OrderHistoryModel> salesList) {

                }
                @Override
                public void showMoreOption(String salesorderId, String customerName, String status) {

                }
            });
            orderListView.setAdapter(orderHistoryAdapter);
        }else {
            emptyLayout.setVisibility(View.VISIBLE);
            orderListView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Execute your code here
        finish();
    }
}