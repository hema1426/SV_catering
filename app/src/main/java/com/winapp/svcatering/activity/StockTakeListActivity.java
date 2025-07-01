package com.winapp.svcatering.activity;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.StockTakeAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.StockTakeDetailModel;
import com.winapp.svcatering.model.StockTakeModel;
import com.winapp.svcatering.utils.Constants;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StockTakeListActivity extends NavigationActivity implements View.OnClickListener {

    public RecyclerView stockTakeView;
    public StockTakeAdapter stockTakeAdapter;
    public ArrayList<StockTakeModel> takeModelArrayList;
    public DBHelper dbHelper;
    private SweetAlertDialog pDialog;
    private String username;
    private String locationCode;
    private EditText searchTextl;
    ProgressDialog dialog;
    static String printerMacId;
    static String printerType;
    public SharedPreferences sharedPreferences;
    ArrayList<StockTakeDetailModel> stockTakeDetailModels;
    ArrayList<StockTakeDetailModel.StockTakeDetail> stockTakeDetailsList;
    public String transferType = "Transfer In";
    public Button addTransfer;
    public TextView emptyText;
    Spinner statusSpinner;
    private String currentDate = "";
    private TextView fromDate;
    private TextView toDate;
    Button cancelSearch;
    private TextView searchButton;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String transferMode = "Transfer In";
    View searchFilterView;
    private BottomSheetBehavior behavior;

    LinearLayout transLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_stock_take_list, contentFrameLayout);
        getSupportActionBar().setTitle("Stock Take");
        dbHelper = new DBHelper(this);
        stockTakeView = findViewById(R.id.stockTakeList);
        searchTextl = findViewById(R.id.search_take_ed);
        addTransfer = findViewById(R.id.add_transfer);
        searchButton = findViewById(R.id.btn_searchadjust);
        searchFilterView=findViewById(R.id.search_filtertake);
        fromDate=findViewById(R.id.from_dateadj);
        toDate =findViewById(R.id.to_dateadj);
        cancelSearch=findViewById(R.id.btn_canceladjust);
        statusSpinner =findViewById(R.id.statusadjust);
        emptyText = findViewById(R.id.empty_text_take);
        transLayout=findViewById(R.id.trans_layouttake);

        session = new SessionManager(this);
        user = session.getUserDetails();
        username = user.get(SessionManager.KEY_USER_NAME);
        locationCode = user.get(SessionManager.KEY_LOCATION_CODE);

        dbHelper.removeAllInvoiceItems();
        dbHelper.removeAllReturn();

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType = sharedPreferences.getString("printer_type", "");
        printerMacId = sharedPreferences.getString("mac_address", "");

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = df1.format(c);

        Date c1 = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c1);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c1);
        fromDate.setText(formattedDate);
        toDate.setText(formattedDate);

        //   addTransfer.setVisibility(View.GONE);
        //    transferInButton.setEnabled(false);
        //    transferInText.setEnabled(false);
        getStockTakelist("", currentDate, currentDate);

        if (getIntent() != null) {
            String docNumber = getIntent().getStringExtra("docNum");
            //  assert docNumber != null;
            if (docNumber != null && !docNumber.isEmpty()) {
                transferType = getIntent().getStringExtra("transferType");
                try {
                    getTransferDetails(1, docNumber, transferType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


//        View bottomSheet = findViewById(R.id.design_bottom_sheet);
//        behavior = BottomSheetBehavior.from(bottomSheet);
//        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                switch (newState) {
//                    case BottomSheetBehavior.STATE_DRAGGING:
//                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
//                        break;
//                    case BottomSheetBehavior.STATE_SETTLING:
//                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
//                        break;
//                    case BottomSheetBehavior.STATE_EXPANDED:
//                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
////                        if (adjustOptionLayout.getVisibility()==View.VISIBLE){
////                            getSupportActionBar().setTitle("Select Option");
//////                        }else {
//////                            getSupportActionBar().setTitle("Select Customer");
////                        }
////                        transLayout.setVisibility(View.VISIBLE);
////                        transLayout.setClickable(false);
////                        transLayout.setEnabled(false);
//                        break;
//                    case BottomSheetBehavior.STATE_COLLAPSED:
//                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
//                        getSupportActionBar().setTitle("Stock Take");
//                        transLayout.setVisibility(View.GONE);
////                        if (redirectInvoice){
////                            CustomerFragment.isLoad=true;
////                            Intent intent=new Intent(StockAdjustmentListActivity.this, AddInvoiceActivityOld.class);
////                            intent.putExtra("customerId",selectedCustomerId);
////                            intent.putExtra("activityFrom","DeliveryOrder");
////                            startActivity(intent);
////                            finish();
////                        }
//                        break;
//                    case BottomSheetBehavior.STATE_HIDDEN:
//                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
//                        break;
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);
//            }
//        });


        searchTextl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (stockTakeAdapter != null) {
                    stockTakeAdapter.getFilter().filter(s.toString());
                }
            }
        });
        addTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TransferActivity.class);
                intent.putExtra("transferType", transferType);
                startActivity(intent);
            }
        });

        // get From date
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate(fromDate);
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate(toDate);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
                Date d1 = null;
                Date d2=null;
                try {
                    d1 = sdformat.parse(fromDate.getText().toString());
                    d2 = sdformat.parse(toDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(d1.compareTo(d2) > 0) {
                    Toast.makeText(getApplicationContext(),"From date should not be greater than to date",Toast.LENGTH_SHORT).show();
                } else{
                    searchFilterView.setVisibility(View.GONE);
                    try {
                        String oldFromDate = fromDate.getText().toString();
                        String oldToDate=toDate.getText().toString();
                        Date fromDate = new SimpleDateFormat("dd/MM/yyyy").parse(oldFromDate);
                        Date toDate = new SimpleDateFormat("dd/MM/yyyy").parse(oldToDate);
                        // Use SimpleDateFormat#format() to format a Date into a String in a certain pattern.

                        String fromDateString = new SimpleDateFormat("yyyyMMdd").format(fromDate);
                        String toDateString = new SimpleDateFormat("yyyyMMdd").format(toDate);
                        System.out.println(fromDateString+"-"+toDateString); // 2011-01-18
                        String invoice_status="";
                        if (statusSpinner.getSelectedItem().equals("ALL")){
                            invoice_status="";
                        }else if (statusSpinner.getSelectedItem().equals("CLOSED")){
                            invoice_status="C";
                        }else if (statusSpinner.getSelectedItem().equals("OPEN")){
                            invoice_status="O";
                        }
                        getStockTakelist(invoice_status,fromDateString,toDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ///filterSearch(customer_name, salesOrderStatusSpinner.getSelectedItem().toString(),fromDate.getText().toString(),toDate.getText().toString());
                    statusSpinner.setSelection(0);
                }
            }
        });
        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusSpinner.setSelection(0);
                fromDate.setText(formattedDate);
                toDate.setText(formattedDate);
                searchFilterView.setVisibility(View.GONE);
                statusSpinner.setSelection(0);
//                setFilterAdapeter();
            }
        });

    }
    public void getDate(TextView dateEditext){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(StockTakeListActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateEditext.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void setTakeListAdapter(ArrayList<StockTakeModel> transferList) {
        try {
            stockTakeAdapter = new StockTakeAdapter(this, transferList, new StockTakeAdapter.CallBack() {
                @Override
                public void callDescription(String transferNo, String mode) {
                    if (mode.equals("Print")) {
                        try {
                            getTransferDetails(1, transferNo.toString(), transferType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), StockTakePreviewPrintActivity.class);
                        intent.putExtra("takeNumber", transferNo);
                        startActivity(intent);
                    }
                }

                @Override
                public void convertTransfer(String requestId) {

                }
            });
            int mNoOfColumns = Utils.calculateNoOfColumns(getApplicationContext(), 200);
            stockTakeView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            stockTakeView.setItemAnimator(new DefaultItemAnimator());
            stockTakeView.setAdapter(stockTakeAdapter);
            //categoriesView.setVisibility(View.VISIBLE);
            //emptyLayout.setVisibility(View.GONE);
        } catch (Exception ex) {
            Log.e("TAG", "Error in Populating the data:" + ex.getMessage());
        }
    }

    public void getStockTakelist(String status, String fromdate, String todate) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "StockTakeList";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("FromDate", fromdate);
            jsonObject.put("ToDate", todate);
            jsonObject.put("DocStatus", status);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("Given_url:", url + "---" + jsonObject.toString());
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
        dialog = new ProgressDialog(StockTakeListActivity.this);
        dialog.setMessage("Loading Stock Take List...");
        dialog.setCancelable(false);
//        if (!dialog.isShowing()) {
            dialog.show();
//        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        dialog.dismiss();
                        Log.w("Response_Take:", response.toString());
                        takeModelArrayList = new ArrayList<>();
                        String statusCode = response.optString("statusCode");
                        String statusMessage = response.optString("statusMessage");
                        if (statusCode.equals("1")) {
                            JSONArray transferDetailsArray = response.optJSONArray("responseData");
                            for (int i = 0; i < transferDetailsArray.length(); i++) {
                                JSONObject object = transferDetailsArray.optJSONObject(i);
                                StockTakeModel model = new StockTakeModel();
                                model.setStockTakeNo(object.optString("docNum"));
                                model.setDate(object.optString("docDate"));
                                model.setCode(object.optString("code"));
                                model.setLocation(object.optString("fromWhsCode"));
                                model.setStatus(object.optString("docStatus"));
                                takeModelArrayList.add(model);

                            }
                            if (takeModelArrayList.size() > 0) {
                                stockTakeView.setVisibility(View.VISIBLE);
                                emptyText.setVisibility(View.GONE);
                                setTakeListAdapter(takeModelArrayList);
                            } else {
                                stockTakeView.setVisibility(View.GONE);
                                emptyText.setVisibility(View.VISIBLE);
                            }
                        } else {
                            stockTakeView.setVisibility(View.GONE);
                            emptyText.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    dialog.dismiss();
                    // Do something when error occurred
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
    }

    private void getTransferDetails(int copy, String transferNo, String type) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("InvTransNo", transferNo);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "InventoryTransferDetails";

        Log.w("Given_url:", url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        stockTakeDetailModels = new ArrayList<>();
        stockTakeDetailsList = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try {
                        Log.w("TransferDetail:", response.toString());

                        pDialog.dismiss();
                        String statusCode = response.optString("statusCode");
                        String statusMessage = response.optString("statusMessage");
                        if (statusCode.equals("1")) {
                            JSONArray transferDetailsArray = response.optJSONArray("responseData");
                            assert transferDetailsArray != null;
                            JSONObject detailObject = transferDetailsArray.optJSONObject(0);
                            StockTakeDetailModel model = new StockTakeDetailModel();
                            model.setNumber(detailObject.optString("invTransNo"));
                            model.setStatus(detailObject.optString("invTransStatus"));
                            model.setDate(detailObject.optString("docDate"));

                            JSONArray itemsArray = detailObject.optJSONArray("itItem");
                            for (int i = 0; i < Objects.requireNonNull(itemsArray).length(); i++) {
                                JSONObject objectItem = itemsArray.optJSONObject(i);
                                StockTakeDetailModel.StockTakeDetail StockTakeModel =
                                        new StockTakeDetailModel.StockTakeDetail();
                                StockTakeModel.setDescription(objectItem.optString("itemName"));
                                StockTakeModel.setQty(objectItem.optString("qty"));
                                StockTakeModel.setUomCode(objectItem.optString("uomCode"));
                                stockTakeDetailsList.add(StockTakeModel);
                            }
                            model.setStockTakeDetailsList(stockTakeDetailsList);
                            stockTakeDetailModels.add(model);

                        //    printStockTake(transferNo, stockTakeDetailModels, type);

                        } else {
                            Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            // pDialog.dismiss();
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
    }

//    public void printStockTake(String transferNo, ArrayList<StockTakeModel> stockTakeDetailModels, String type) {
//        if (transferDetailModels.size() > 0) {
//            if (Utils.validatePrinterConfiguration(this,printerType,printerMacId)) {
//
//
//            TSCPrinter printer = new TSCPrinter(this, printerMacId, "Transfer");
//            try {
//                printer.printTransferDetail(1, transferNo, type, stockTakeDetailModels);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        }else {
//            Toast.makeText(getApplicationContext(),"Please configure the Printer",Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    protected void onResume() {
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType = sharedPreferences.getString("printer_type", "");
        printerMacId = sharedPreferences.getString("mac_address", "");
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.transfer_in) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.take_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//finish();
            onBackPressed();
        } else if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(getApplicationContext(), StockTakeAddActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId()==R.id.action_filter){
            if (searchFilterView.getVisibility()==View.VISIBLE){
                searchFilterView.setVisibility(View.GONE);

//                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
//                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                }
//
                //slideUp(searchFilterView);
            }else {
                searchFilterView.setVisibility(View.VISIBLE);
//                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
//                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                }

                // slideDown(searchFilterView);
            }
        }


        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}