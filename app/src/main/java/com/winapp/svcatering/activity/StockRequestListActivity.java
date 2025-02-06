package com.winapp.svcatering.activity;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.winapp.svcatering.adapter.TransferAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.TransferDetailModel;
import com.winapp.svcatering.model.TransferModel;
import com.winapp.svcatering.newtransfer.TransferInActivity;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.winapp.svcatering.zebraprinter.TSCPrinter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StockRequestListActivity extends NavigationActivity implements View.OnClickListener {

    public RecyclerView stockRequestList;
    private LinearLayout transferInButton;
    private LinearLayout transferOutButton;
    public TransferAdapter requestAdapter;
    public ArrayList<TransferModel> requestList;
    public DBHelper dbHelper;
    private SweetAlertDialog pDialog;
    private TextView transferInText;
    private TextView transferOutText;
    private String username;
    private String locationCode;
    private EditText stockRequestText;
    ProgressDialog dialog;
    static String printerMacId;
    static String printerType;
    public SharedPreferences sharedPreferences;
    ArrayList<TransferDetailModel> transferDetailModels;
    ArrayList<TransferDetailModel.TransferDetails> transferDetailsList;
    public String transferType="Transfer In";
    public Button addRequest;
    public TextView emptyText;
    public TextView requestNoTitle;
    public String currentDate="";
    private TextView fromDate;
    private TextView toDate;
    private TextView searchButton;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private LinearLayout requestSentLayout;
    private LinearLayout requestReceiveLayout;
    private View requestSentView;
    private View requestReceiveView;
    private String mode="In";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_stock_request_list, contentFrameLayout);
        getSupportActionBar().setTitle("Stock Request List");
        Log.w("activity_cg",getClass().getSimpleName().toString());

        dbHelper=new DBHelper(this);
        stockRequestList =findViewById(R.id.transferProductList);
        transferInButton=findViewById(R.id.transfer_in);
        transferOutButton=findViewById(R.id.transfer_out);
        transferInText=findViewById(R.id.tranfer_in_text);
        transferOutText=findViewById(R.id.transfer_out_text);
        stockRequestText =findViewById(R.id.transfer_search);
        searchButton=findViewById(R.id.search);
        addRequest =findViewById(R.id.add_transfer);
        emptyText=findViewById(R.id.empty_text);
        requestNoTitle=findViewById(R.id.transfer_no);
        fromDate=findViewById(R.id.from_date);
        requestSentView=findViewById(R.id.request_sent_view);
        requestReceiveView=findViewById(R.id.request_receive_view);
        requestReceiveLayout=findViewById(R.id.request_receive);
        requestSentLayout=findViewById(R.id.request_sent);
        toDate=findViewById(R.id.to_date);
        transferOutText.setOnClickListener(this);
        transferInText.setOnClickListener(this);
        transferOutButton.setOnClickListener(this);
        transferInButton.setOnClickListener(this);
        session=new SessionManager(this);
        user=session.getUserDetails();
        username=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);

        requestNoTitle.setText("REQUEST NO");

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = df1.format(c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        fromDate.setText(formattedDate);
        toDate.setText(formattedDate);

        dbHelper.removeAllInvoiceItems();
        dbHelper.removeAllReturn();

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        if (getIntent()!=null){
            String docNumber=getIntent().getStringExtra("docNum");
//            assert docNumber != null;
            if (docNumber!=null && !docNumber.isEmpty()){
                transferType=getIntent().getStringExtra("transferType");
                try {
                    getStockRequestDetails(1,docNumber,transferType,"Print");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        stockRequestText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (requestAdapter !=null){
                    requestAdapter.getFilter().filter(s.toString());
                }
            }
        });

        getStockRequestList("In",currentDate,currentDate);
        mode="In";

        requestSentLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                requestSentView.setVisibility(View.VISIBLE);
                requestReceiveView.setVisibility(View.GONE);
                String fromdate=Utils.convertDate(fromDate.getText().toString(),"dd-MM-yyyy","yyyyMMdd");
                String todate=Utils.convertDate(toDate.getText().toString(),"dd-MM-yyyy","yyyyMMdd");
                getStockRequestList("Out",fromdate,todate);
                mode="Out";
            }
        });

        requestReceiveLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                requestSentView.setVisibility(View.GONE);
                requestReceiveView.setVisibility(View.VISIBLE);
                String fromdate=Utils.convertDate(fromDate.getText().toString(),"dd-MM-yyyy","yyyyMMdd");
                String todate=Utils.convertDate(toDate.getText().toString(),"dd-MM-yyyy","yyyyMMdd");
                getStockRequestList("In",fromdate,todate);
                mode="In";
            }
        });

        addRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent=new Intent(getApplicationContext(),TransferActivity.class);
                intent.putExtra("transferType","Stock Request");
                startActivity(intent);*/

                Intent intent=new Intent(getApplicationContext(), TransferInActivity.class);
                intent.putExtra("transferType","Stock Request");
                startActivity(intent);
            }
        });

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // fromDate.setText(Utils.getDate(StockRequestListActivity.this,fromDate));
                getDate(fromDate);
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toDate.setText(Utils.getDate(StockRequestListActivity.this,toDate));
                getDate(toDate);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (!fromDate.getText().toString().isEmpty() && !toDate.getText().toString().isEmpty()){
                    String fromdate=Utils.convertDate(fromDate.getText().toString(),"dd-MM-yyyy","yyyyMMdd");
                    String todate=Utils.convertDate(toDate.getText().toString(),"dd-MM-yyyy","yyyyMMdd");
                    getStockRequestList(mode,fromdate,todate);
                }else {
                    Toast.makeText(getApplicationContext(),"Select the Date to Search..!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getDate(TextView dateTextView){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(StockRequestListActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateTextView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }



    public void getStockRequestList(String mode,String fromdate,String todate){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"InventoryTransferRequestList";
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("User",username);
            jsonObject.put("CustomerCode","");
            jsonObject.put("VendorCode","");
            jsonObject.put("FromDate",fromdate);
            jsonObject.put("ToDate",todate);
            jsonObject.put("DocStatus","");
            jsonObject.put("WarehouseCode" ,locationCode);
            jsonObject.put("InorOut",mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("Given_url:",url+"---"+jsonObject.toString());

        if (dialog != null && dialog.isShowing())
            dialog.cancel();
        dialog=new ProgressDialog(StockRequestListActivity.this);
        dialog.setMessage("Loading StockRequest List...");
        dialog.setCancelable(false);
//        if (!dialog.isShowing()) {
        dialog.show();
//
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        dialog.dismiss();
                        Log.w("Response_StockRequest:", response.toString());
                        requestList =new ArrayList<>();

                        String statusCode=response.optString("statusCode");
                        String statusMessage=response.optString("statusMessage");
                        if (statusCode.equals("1")){
                            JSONArray transferDetailsArray =response.optJSONArray("responseData");
                            for (int i = 0; i< transferDetailsArray.length(); i++){
                                JSONObject object= transferDetailsArray.optJSONObject(i);
                                TransferModel model=new TransferModel();
                                model.setTransferNo(object.optString("invTransReqNo"));
                                model.setDate(object.optString("docDate"));
                                model.setFromLocation(object.optString("fromWhsCode"));
                                model.setToLocation(object.optString("toWhsCode"));
                                model.setUser(object.optString("user"));
                                model.setStatus(object.optString("invTransReqStatus"));
                                requestList.add(model);
                            }
                            if (requestList.size()>0){
                                stockRequestList.setVisibility(View.VISIBLE);
                                emptyText.setVisibility(View.GONE);
                                setTransferListAdapter(requestList);
                            }else {
                                stockRequestList.setVisibility(View.GONE);
                                emptyText.setVisibility(View.VISIBLE);
                            }
                        }else {
                            stockRequestList.setVisibility(View.GONE);
                            emptyText.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(),statusMessage,Toast.LENGTH_LONG).show();
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

    public void setTransferListAdapter(ArrayList<TransferModel> stockRequestList){
        try {
            requestAdapter = new TransferAdapter(this, stockRequestList, new TransferAdapter.CallBack() {
                @Override
                public void callDescription(String requestNo,String mode) {
                    Log.w("GivenTransferNo::", requestNo.toString());
                    if (mode.equals("Print")){
                        try {
                            getStockRequestDetails(1, requestNo.toString(),"Stock Request","Print");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Intent intent=new Intent(getApplicationContext(),TransferPreviewPrintActivity.class);
                        intent.putExtra("title","Stock Request");
                        intent.putExtra("transferNumber",requestNo);
                        startActivity(intent);
                    }
                }

                @Override
                public void convertTransfer(String requestId) {
                    try {
                        getStockRequestDetails(1, requestId.toString(),"Stock Request","");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            int mNoOfColumns = Utils.calculateNoOfColumns(getApplicationContext(),200);
            this.stockRequestList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            this.stockRequestList.setItemAnimator(new DefaultItemAnimator());
            this.stockRequestList.setAdapter(requestAdapter);
            //categoriesView.setVisibility(View.VISIBLE);
            //emptyLayout.setVisibility(View.GONE);
        }catch (Exception ex){
            Log.e("TAG","Error in Populating the data:"+ex.getMessage());
        }
    }

    private void getStockRequestDetails(int copy, String transferNo, String type,String action) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("InvTransReqNo",transferNo);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url=Utils.getBaseUrl(this) +"InventoryTransferRequestDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        if (action.equals("Print")){
            pDialog.setTitleText("Generating Print Preview...");
        }else {
            pDialog.setTitleText("Processing Please wait...");
        }
        pDialog.setCancelable(false);
        pDialog.show();
        transferDetailModels =new ArrayList<>();
        transferDetailsList =new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try{
                        Log.w("StockRequestDetails:",response.toString());

                        pDialog.dismiss();
                        String statusCode=response.optString("statusCode");
                        String statusMessage=response.optString("statusMessage");
                        if (statusCode.equals("1")){
                            JSONArray  transferDetailsArray=response.optJSONArray("responseData");
                            assert transferDetailsArray != null;
                            JSONObject detailObject=transferDetailsArray.optJSONObject(0);
                            TransferDetailModel model=new TransferDetailModel();
                            model.setNumber(detailObject.optString("invTransReqNo"));
                            model.setStatus(detailObject.optString("invTransReqStatus"));
                            model.setDate(detailObject.optString("docDate"));
                            model.setFromLocation(detailObject.optString("fromWhsCode"));
                            model.setToLocation(detailObject.optString("toWhsCode"));
                            model.setFromLocationName(detailObject.optString("fromWarehouseName"));
                            model.setToLocationName(detailObject.optString("toWarehouseName"));

                            JSONArray itemsArray=detailObject.optJSONArray("itItem");
                            for (int i = 0; i< Objects.requireNonNull(itemsArray).length(); i++){
                                JSONObject objectItem= itemsArray.optJSONObject(i);
                                TransferDetailModel.TransferDetails transferModel =new TransferDetailModel.TransferDetails();
                                transferModel.setItemCode(objectItem.optString("itemCode"));
                                transferModel.setDescription(objectItem.optString("itemName"));
                                transferModel.setQty(objectItem.optString("qty"));
                                transferModel.setUomCode(objectItem.optString("uomCode"));
                                transferDetailsList.add(transferModel);
                            }
                            model.setTransferDetailsList(transferDetailsList);
                            transferDetailModels.add(model);

                            if (action.equals("Print")){
                                printTransfer(transferNo,transferDetailModels,type);
                            }else {
                                convertAndRedirect(transferNo,transferDetailModels,transferDetailsList);
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),statusMessage,Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            // pDialog.dismiss();
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
    public void convertAndRedirect(String requestNo,ArrayList<TransferDetailModel> transferDetailModels,ArrayList<TransferDetailModel.TransferDetails> transferDetail){
        String return_qty="0";
        String price_value ="0.00";
        String qty_value ="0";
        dbHelper.removeAllInvoiceItems();
        for (TransferDetailModel.TransferDetails model:transferDetail){
            dbHelper.insertCreateInvoiceCart(
                    model.getItemCode(),
                    model.getDescription(),
                    model.getUomCode(),
                    "",
                    (int)Double.parseDouble(model.getQty())+"",
                    return_qty,
                    (int)Double.parseDouble(model.getQty())+"", "0",
                    price_value,
                    "0",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00","",
                    "",
                    "",
                    "",
                    "0",
                    "","","",""
            );
        }
        int count=dbHelper.numberOfRowsInInvoice();
        String fromLocation=transferDetailModels.get(0).getFromLocation();
        String toLocation=transferDetailModels.get(0).getToLocation();
        String invoiceDate=transferDetailModels.get(0).getDate();
        String transferType="Convert Transfer";
        if (count==transferDetail.size()){
            Intent intent=new Intent(getApplicationContext(),TransferProductAddActivity.class);
            intent.putExtra("fromLocationCode",fromLocation);
            intent.putExtra("toLocationCode",toLocation);
            intent.putExtra("currentDate",invoiceDate);
            intent.putExtra("requestNo",requestNo);
            intent.putExtra("transferType",transferType);
            startActivity(intent);
        }
    }

    public void printTransfer(String transferNo,ArrayList<TransferDetailModel> transferDetailModels,String type ){
        if (transferDetailModels.size()>0){
            if (Utils.validatePrinterConfiguration(this,printerType,printerMacId)) {

                TSCPrinter printer=new TSCPrinter(this,printerMacId,"Transfer");
            try {
                printer.printTransferDetail(1,transferNo,type,transferDetailModels);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }else {
            Toast.makeText(getApplicationContext(),"Please configure the Printer",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
               // onBackPressed();
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