package com.winapp.svcatering.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.CurrencyDenominationAdapter;
import com.winapp.svcatering.adapter.ExpenseAdapter;
import com.winapp.svcatering.model.CurrencyModel;
import com.winapp.svcatering.model.ExpenseModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.MyKeyboard;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SettlementActivity extends AppCompatActivity {

    private ArrayList<CurrencyModel> currencyList;
    private CurrencyDenominationAdapter currencyDenominationAdapter;
    private ExpenseAdapter expenseAdapter;
    private ArrayList<ExpenseModel> expenseList;
    private RecyclerView denominationView;
    private RecyclerView expenseView;
    private String companyCode;
    private String username;
    private String companyCode1;
    private SessionManager session;
    private HashMap<String ,String > user;
    private TextView settlementNo;
    private TextView settlementDate;
    private TextView userName;
    private TextView totalCashCollect;
    private TextView locationCode;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private TextView buttonDenomination;
    private TextView buttonExpense;
    private LinearLayout denominationLayout;
    private LinearLayout expenseLayout;
    private TextView currencyTotal;
    private TextView expenseTotal;
    private TextView netTotal;
    private Button saveSettlement;
    private String net_total_value = "0.00";
    private String expense_net_total = "0.00";
    private String action="";
    private int REQUEST_SETTLEMENT= 33;
    private String editSettlementNumber;
    private String editSettlementDate;
    private String editSettlementUser;
    private String editLocationCode;
    private String editSettlementAmount;
    private LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        );
        setContentView(R.layout.activity_settlement);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.w("activity_cg",getClass().getSimpleName().toString());

        denominationView=findViewById(R.id.denomination_view);
        expenseView=findViewById(R.id.expense_view);
        settlementDate=findViewById(R.id.date);
        userName=findViewById(R.id.username);
        totalCashCollect=findViewById(R.id.total_cash_collect);
        locationCode=findViewById(R.id.location_code);
        settlementNo=findViewById(R.id.settlement_no);
        buttonDenomination=findViewById(R.id.button_denomination);
        buttonExpense=findViewById(R.id.button_expense);
        denominationLayout=findViewById(R.id.denomination_layout);
        expenseLayout=findViewById(R.id.expense_layout);
        currencyTotal=findViewById(R.id.currency_total);
        netTotal=findViewById(R.id.net_totalSettle);
        expenseTotal=findViewById(R.id.expense_total);
        saveSettlement=findViewById(R.id.save_settlement);
        rootLayout=findViewById(R.id.rootLayout);

        session=new SessionManager(this);
        user=session.getUserDetails();

        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        username=user.get(SessionManager.KEY_USER_NAME);
        companyCode1=user.get(SessionManager.KEY_LOCATION_CODE);

        if (getIntent()!=null){
            action=getIntent().getStringExtra("action");
        }

        if (action!=null && !action.isEmpty()){
            if (action.equals("Add")){
                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String formattedDate = df.format(c);
                settlementDate.setText(formattedDate);
                locationCode.setText(user.get(SessionManager.KEY_LOCATION_CODE));
                userName.setText(user.get(SessionManager.KEY_USER_NAME));
                settlementDate.setEnabled(true);
                saveSettlement.setText("Save Settlement");
                getSupportActionBar().setTitle(" Add Settlement");
                try {
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("CompanyCode",companyCode);
                    getCurrencyDenomination(jsonObject);
                    setExpense();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                getSupportActionBar().setTitle("Edit Settlement");
                settlementDate.setEnabled(false);
                settlementDate.setText(getIntent().getStringExtra("editDate"));
                settlementNo.setText(getIntent().getStringExtra("editSettlementNumber"));
                locationCode.setText(getIntent().getStringExtra("editLocationCode"));
                userName.setText(getIntent().getStringExtra("editUser"));
                saveSettlement.setText("Update Settlement");
                try {
                    getSettlementDetails(companyCode,settlementNo.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // totalCashCollect.setText(getIntent().getStringExtra("editCashCollection"));
            }
        }else {
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);
            getSupportActionBar().setTitle(" Add Settlement");

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);
            settlementDate.setText(formattedDate);
            locationCode.setText(user.get(SessionManager.KEY_LOCATION_CODE));
            userName.setText(user.get(SessionManager.KEY_USER_NAME));
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("CompanyCode",companyCode);
                getCurrencyDenomination(jsonObject);
                setExpense();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //setExpense();

        settlementDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(settlementDate);
            }
        });

        buttonExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseLayout.setVisibility(View.VISIBLE);
                denominationLayout.setVisibility(View.GONE);
                buttonExpense.setBackgroundResource(R.color.colorPrimary);
                buttonDenomination.setBackgroundResource(R.color.colorGreen);
            }
        });

        buttonDenomination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseLayout.setVisibility(View.GONE);
                denominationLayout.setVisibility(View.VISIBLE);
                buttonExpense.setBackgroundResource(R.color.colorGreen);
                buttonDenomination.setBackgroundResource(R.color.colorPrimary);
            }
        });
        saveSettlement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    double netvalue=Double.parseDouble(net_total_value);
                    double cashcollect=Double.parseDouble(totalCashCollect.getText().toString());
                    if (cashcollect > 0){
                        if (netvalue > 0){
                           // if (netvalue >= cashcollect){
                                setSettlementJson();
                          //  }else {
                             //   Toast.makeText(getApplicationContext(),"Amount mismatch",Toast.LENGTH_SHORT).show();
                           // }
                        }else {
                            Toast.makeText(getApplicationContext(),"Enter the Amount to Save Settlement",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        showAlertForSettlement(settlementDate.getText().toString());
                    }
                }catch (Exception e){}
            }
        });
    }

     public void setExpense(){
        expenseList=new ArrayList<>();
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("CompanyCode",companyCode);
            getExpenses(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setSettlementJson(){
       // {"SettlementHeader":{"CompanyCode":"1","SettlementDateString":"25\/08\/2021","SettlementNo":"","SettlementBy":"winapp",
        // "TotalAmount":100.00,"Mode":"I","CreateUser":"winapp","ModifyUser":"winapp","LocationCode":"HQ"},
        // "SettlementDetail":[{"CompanyCode":"1","SettlementNo":"","DenominationCount":"1","Denomination":10,"Total":100,"SlNo":1,
        // "CreateUser":"winapp","ModifyUser":"winapp"}],
        //
        try {
            JSONObject rootObject =new JSONObject();
            JSONObject settlementObject=new JSONObject();
            JSONObject expenseObject=new JSONObject();

            JSONArray settlementDetailArray=new JSONArray();
            JSONArray expenseDetailArray=new JSONArray();

          //  {"SettlementNo":"","SettlementDate":"20210911","User":"AADHIVAN 1","LocationCode":"HO","TotalCash": "550",
            //  "MiscIncome": "550","CostOfSales": "550","PurchasesImport": "550","PurchasesLocal": "550","OpeningStock": "550",
            //  "FreightAndHandlingCharge": "550","ImportPermitCharges": "550","ChangeInInventories": "550","TotalExpense": "550",
            //  "LaboratoryCharges": "550","PostingSettlementDetails" :[{"Currency":"1000","Count":"1","Total":"1000"},
            //  {"Currency":"100","Count":"2","Total":"200"},{"Currency":"10","Count":"1","Total":"100"},
            //  {"Currency":"20","Count":"11","Total":"220"},{"Currency":"1","Count":"1","Total":"1"},
            //  {"Currency":"100","Count":"1","Total":"100"}]}

            if (action.equals("Add")){
                rootObject.put("SettlementNo","");
                rootObject.put("Mode","I");
            }else {
                rootObject.put("SettlementNo",settlementNo.getText().toString().trim());
                rootObject.put("Mode","E");
            }
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            // get Permission for the getting the Current location and Adding Image Permission to obtained

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            String settlementDate = df.format(c);

            rootObject.put("CompanyCode",companyCode);
            rootObject.put("SettlementDate",settlementDate);
            rootObject.put("TotalCash",net_total_value);
            rootObject.put("TotalExpense","");
            rootObject.put("User",userName.getText().toString());
            rootObject.put("LocationCode",locationCode.getText().toString());
            rootObject.put("TotalExpense",expense_net_total);
            rootObject.put("JournalEntryNo","");
            int index=1;
            for (CurrencyModel model:currencyDenominationAdapter.getCurrencyList()){
                settlementObject=new JSONObject();
                if (model.getCount()!=null && !model.getCount().isEmpty()){
                    settlementObject.put("Count",(int)Double.parseDouble(model.getCount()));
                }else {
                    settlementObject.put("Count","");
                }
                settlementObject.put("Currency",model.getCurrencyName());
                settlementObject.put("Total",model.getTotal());
                settlementDetailArray.put(settlementObject);
                index++;
            }

            // "SettlementExpenseDetail":[{"SettlementNo":"","GroupNo":"","GroupName":"",
            // "Amount":100,"SlNo":1,"CreateUser":"winapp","ModifyUser":"winapp"}]}

            int ind=1;
            for (ExpenseModel model:expenseList){
                expenseObject=new JSONObject();
                if (model.getExpenseTotal()!=null && !model.getExpenseTotal().isEmpty() && !model.getExpenseTotal().equals("null")){
                   // if (Double.parseDouble(model.getExpenseTotal()) > 0){
                        expenseObject.put("GroupNo",model.getGroupNo());
                        expenseObject.put("GroupName",model.getExpenseName());
                        expenseObject.put("Amount",model.getExpenseTotal());
                        expenseDetailArray.put(expenseObject);
                        ind++;
                  //  }
                }
            }
            rootObject.put("PostingSettlementDetails",settlementDetailArray);
            rootObject.put("PostingSettlementExpensesDetails",expenseDetailArray);

            Log.w("SettlementRootJson:", rootObject.toString());
            showAlert(rootObject,1);

        }catch (Exception exception){}
    }

    public void showAlert(JSONObject rootObject,int copy){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Are you sure want to Save this Settlement ?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveSettlement(rootObject,1);
                        dialog.dismiss();
                    }
                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = alertDialog.create();
        alert11.show();
    }


    public  void saveSettlement(JSONObject jsonBody,int copy){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(SettlementActivity.this);
            Log.w("GivenInvoiceRequest:",jsonBody.toString());
            String URL= Utils.getBaseUrl(this)+"PostingSettlement";
            Log.w("Given_InvoiceApi:",URL);
            ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Processing...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            JsonObjectRequest salesOrderRequest = new JsonObjectRequest(Request.Method.POST, URL,jsonBody, response -> {
                Log.w("SettlementResponse:",response.toString());
                progressDialog.dismiss();
                String settlementNol = "" ;
                String statusCode=response.optString("statusCode");

                    if (statusCode.equals("1")){
                        JSONObject jsonObject=response.optJSONObject("responseData");

                        settlementNol = jsonObject.optString("docNum");

                         Toast.makeText(getApplicationContext(),"Settlement Saved Successfully",Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
//                    JSONObject SRoblect = SRArray.optJSONObject(0);
//                    settlementNol = SRoblect.optString("salesReturnNumber") ;

                    intent.putExtra("key", "Refresh");
                    intent.putExtra("settlemt_PrintNo", settlementNol);
                    //startActivityForResult(intent, REQUEST_SETTLEMENT);

                    setResult(RESULT_OK, intent);
                    finish();
                }else {
                    JSONObject object=response.optJSONObject("responseData");
                    assert object != null;
                    String error=object.optString("error");
                    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                Log.w("ErrorSettlement:",error.toString());
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> params = new HashMap<>();
                    String creds = String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD);
                    String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                    params.put("Authorization", auth);
                    return params;
                }
            };
            salesOrderRequest.setRetryPolicy(new RetryPolicy() {
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
            requestQueue.add(salesOrderRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCurrencyDenomination(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SettlementDenomination";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_Denomination_URL:",url);
        currencyList=new ArrayList<>();
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Getting Currency...");
        dialog.setCancelable(false);
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                    try{
                        Log.w("Response_Currency:",response.toString());
                        // Loop through the array elements
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray detailsArray=response.optJSONArray("responseData");
                            assert detailsArray != null;
                            if (detailsArray.length()>0){
                                for (int i=0;i<detailsArray.length();i++){
                                    JSONObject currencyObject = detailsArray.getJSONObject(i);
                                    CurrencyModel product =new CurrencyModel();
                                    product.setCurrencyName(currencyObject.optString("currency"));
                                    product.setCount("");
                                    product.setId(String.valueOf(i));
                                    product.setTotal("0.00");
                                    currencyList.add(product);
                                }
                                if (currencyList.size()>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAdapter(currencyList);
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            }else {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"No Currency Found...!",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            JSONObject object=response.optJSONObject("responseData");
                            assert object != null;
                            String error=object.optString("error");
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
        }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", Constants.API_SECRET_CODE,Constants.API_SECRET_PASSWORD);
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

    private void setAdapter(ArrayList<CurrencyModel> currencList) {
        denominationView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        currencyDenominationAdapter = new CurrencyDenominationAdapter(this,currencList,
                new CurrencyDenominationAdapter.CallBack() {
            @Override
            public void setKeyboard(EditText countEditext) {
                MyKeyboard keyboard = (MyKeyboard) findViewById(R.id.keyboard);
                // prevent system keyboard from appearing when EditText is tapped
                countEditext.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                countEditext.setTextIsSelectable(true);
                // pass the InputConnection from the EditText to the keyboard
                InputConnection ic = countEditext.onCreateInputConnection(new EditorInfo());
                keyboard.setInputConnection(ic);
            }

            @Override
            public void setCurrencyTotal() {
                setNetTotalValues();
            }
        });
        denominationView.setAdapter(currencyDenominationAdapter);
        setNetTotalValues();
        rootLayout.setVisibility(View.VISIBLE);
    }


    public void getExpenses(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SettlementExpenses";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_Expense_URL:",url);
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Getting Expenses...");
        dialog.setCancelable(false);
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null,
                response -> {
                    try{
                        Log.w("Response_Expense:",response.toString());
                        // Loop through the array elements
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray detailsArray=response.optJSONArray("responseData");
                            if(detailsArray.length()>0){
                                for (int i=0;i<detailsArray.length();i++){
                                    JSONObject expenseObject = detailsArray.getJSONObject(i);
                                    ExpenseModel product =new ExpenseModel();
                                    product.setExpenseName(expenseObject.optString("groupName"));
                                    product.setExpenseId(String.valueOf(i+1));
                                    product.setGroupNo(expenseObject.optString("groupNo"));
                                    product.setExpenseTotal("0.00");
                                    expenseList.add(product);
                                }
                                if (expenseList.size()>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setExpenseAdapter(expenseList);
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            }else {
                                Toast.makeText(getApplicationContext(),"No Expense Found...!",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                            // Get the CashCollect Amount Details::
                            String stmDate = settlementDate.getText().toString();
                            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(stmDate);
                            // Use SimpleDateFormat#format() to format a Date into a String in a certain pattern.
                            String searchDate = new SimpleDateFormat("yyyyMMdd").format(date);
                            System.out.println(searchDate); // 2011-01-18
                            JSONObject cashObject=new JSONObject();
                            cashObject.put("User",userName.getText().toString());
                            cashObject.put("Date",searchDate);
                            getCashCollectAmount(cashObject);
                        }else {

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
        }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", Constants.API_SECRET_CODE,Constants.API_SECRET_PASSWORD);
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

    private void getSettlementDetails(String companyCode, String editSettlementNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("SettlementNo",editSettlementNumber);
        jsonObject.put("LocationCode",locationCode);
        String url= Utils.getBaseUrl(this) +"SettlementDetails";
        // Initialize a new JsonArrayRequest instance
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Getting Settlement Details...");
        dialog.setCancelable(false);
        dialog.show();
        Log.w("Given_url:",url+"/"+jsonObject);
        expenseList=new ArrayList<>();
        expenseList.clear();
        currencyList=new ArrayList<>();
        currencyList.clear();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                response -> {
                    try{
                        Log.w("EditSettlementResponse:",response.toString());
                        dialog.dismiss();
                        if (response.length()>0){
                            String statusCode=response.optString("statusCode");
                            if (statusCode.equals("1")){
                                JSONArray responseDataArray=response.getJSONArray("responseData");
                                JSONObject responseObject=responseDataArray.getJSONObject(0);
                                String settlementAmount=responseObject.optString("totalAmount");
                                totalCashCollect.setText(Utils.twoDecimalPoint(Double.parseDouble(settlementAmount)));
                                JSONArray denominationArray=responseObject.optJSONArray("settlementDetails");
                                for (int i = 0; i< Objects.requireNonNull(denominationArray).length(); i++){
                                    JSONObject currencyObject = denominationArray.optJSONObject(i);
                                    CurrencyModel product =new CurrencyModel();
                                    product.setCurrencyName(currencyObject.optString("currency"));
                                    product.setCount(currencyObject.optString("count"));
                                    product.setId(String.valueOf(i));
                                    product.setTotal(currencyObject.optString("total"));
                                    currencyList.add(product);
                                }
                                if (currencyList.size()>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setNetTotalValues();
                                            setAdapter(currencyList);
                                        }
                                    });
                                }
                                JSONArray expenseArray=responseObject.optJSONArray("settlementExpensesDetails");
                                for (int i = 0; i< Objects.requireNonNull(expenseArray).length(); i++){
                                    JSONObject expenseObject=expenseArray.optJSONObject(i);
                                    ExpenseModel model=new ExpenseModel();
                                    model.setExpenseName(expenseObject.optString("groupName"));
                                    model.setExpenseId(String.valueOf(i+1));
                                    model.setGroupNo(expenseObject.optString("groupNo"));
                                    model.setExpenseTotal(expenseObject.optString("amount"));
                                    expenseList.add(model);
                                }
                                if (expenseList.size()>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setNetTotalValues();
                                            setExpenseAdapter(expenseList);
                                        }
                                    });
                                }
                            }else {


                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
            Toast.makeText(getApplicationContext(),"Server Error,Please check",Toast.LENGTH_LONG).show();
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

    private void setExpenseAdapter(ArrayList<ExpenseModel> expenseList) {
        try {
            expenseView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            expenseAdapter = new ExpenseAdapter(expenseList, new ExpenseAdapter.CallBack() {
                @Override
                public void sortKeyboard(EditText expenseEditext) {
                    MyKeyboard keyboard = (MyKeyboard) findViewById(R.id.keyboard);
                    // prevent system keyboard from appearing when EditText is tapped
                    expenseEditext.setRawInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    expenseEditext.setTextIsSelectable(true);
                    // pass the InputConnection from the EditText to the keyboard
                    InputConnection ic = expenseEditext.onCreateInputConnection(new EditorInfo());
                    keyboard.setInputConnection(ic);
                }
                @Override
                public void setExpenseTotal() {
                    setNetTotalValues();
                }
            });
            expenseView.setAdapter(expenseAdapter);
        }catch (Exception e){}
    }

    public void setNetTotalValues(){
        try {
            double total_currency=0.0;
            double expense_amount=0.0;
            double net_total;
            Log.w("currenctlist",""+currencyDenominationAdapter.getCurrencyList());
            for (CurrencyModel model:currencyDenominationAdapter.getCurrencyList()){
                total_currency+=Double.parseDouble(model.getTotal());
            }
            currencyTotal.setText("Currency Total : $ "+Utils.twoDecimalPoint(total_currency));
            for (ExpenseModel model:expenseList){
                if (!model.getExpenseTotal().isEmpty()){
                    expense_amount+=Double.parseDouble(model.getExpenseTotal());
                }
            }
            expenseTotal.setText("Expense Total : $ "+Utils.twoDecimalPoint(expense_amount));
            net_total=total_currency + expense_amount;

            Log.w("settlenettot",""+net_total);

            netTotal.setText("$ "+Utils.twoDecimalPoint(net_total));
            net_total_value=Utils.twoDecimalPoint(net_total);
            expense_net_total=Utils.twoDecimalPoint(expense_amount);

        }catch (Exception exception){}
    }

    public void getDate(TextView dateEditext){
        try {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            dateEditext.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            // Get the CashCollect Amount Details::
                            String stmDate = settlementDate.getText().toString();
                            Date date = null;
                            try {
                                date = new SimpleDateFormat("dd-MM-yyyy").parse(stmDate);
                                // Use SimpleDateFormat#format() to format a Date into a String in a certain pattern.
                                String searchDate = new SimpleDateFormat("yyyyMMdd").format(date);
                                System.out.println(searchDate); // 2011-01-18
                                JSONObject cashObject=new JSONObject();
                                cashObject.put("User",userName.getText().toString());
                                cashObject.put("Date",searchDate);
                                getCashCollectAmount(cashObject);
                            } catch (ParseException | JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, mYear, mMonth, mDay);
           // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        }catch (Exception exception){}
        // Get Current Date
    }

    public void getCashCollectAmount(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SettlementTotalCash";
        // Initialize a new JsonArrayRequest instance
        Log.w("AmountCollect_URL:",url+"/"+jsonObject.toString());
        currencyList=new ArrayList<>();
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Processing Please Wait...");
        dialog.setCancelable(false);
        dialog.show();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try{
                Log.w("Res_CashCollectAmount:",response.toString());
                dialog.dismiss();
                // Loop through the array elements
                if (response.length()>0){
                    String statusCode=response.optString("statusCode");
                    if (statusCode.equals("1")){
                        JSONArray jsonArray=response.optJSONArray("responseData");
                        JSONObject object=jsonArray.optJSONObject(0);
                        totalCashCollect.setText(object.optString("cashSum"));
                    }else {
                        totalCashCollect.setText("0.00");
                        showAlertForSettlement(settlementDate.getText().toString());
                    }
                }else {

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }, error -> {
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
        }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", Constants.API_SECRET_CODE,Constants.API_SECRET_PASSWORD);
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

    public void showAlertForSettlement(String date){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("No Settlement Found For "+date+" Try another Date..");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}