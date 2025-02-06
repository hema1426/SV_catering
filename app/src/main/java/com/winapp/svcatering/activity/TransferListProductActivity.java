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
import android.view.Menu;
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

public class TransferListProductActivity extends NavigationActivity implements View.OnClickListener {

    public RecyclerView transferListView;
    private LinearLayout transferInButton;
    private LinearLayout transferOutButton;
    public TransferAdapter transferAdapter;
    public ArrayList<TransferModel> transferList;
    public DBHelper dbHelper;
    private SweetAlertDialog pDialog;
    private TextView transferInText;
    private TextView transferOutText;
    private String username;
    private String locationCode;
    private EditText transferSearchText;
    ProgressDialog dialog;
    static String printerMacId;
    static String printerType;
    public SharedPreferences sharedPreferences;
    ArrayList<TransferDetailModel> transferDetailModels;
    ArrayList<TransferDetailModel.TransferDetails> transferDetailsList;
    public String transferType = "Transfer In";
    public Button addTransfer;
    public TextView emptyText;
    private String currentDate = "";
    private TextView fromDate;
    private TextView toDate;
    private TextView searchButton;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String transferMode = "Transfer In";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_transfer_list_product, contentFrameLayout);
        getSupportActionBar().setTitle("Transfer");
        dbHelper = new DBHelper(this);
        transferListView = findViewById(R.id.transferProductList);
        transferInButton = findViewById(R.id.transfer_in);
        transferOutButton = findViewById(R.id.transfer_out);
        transferInText = findViewById(R.id.tranfer_in_text);
        transferOutText = findViewById(R.id.transfer_out_text);
        transferSearchText = findViewById(R.id.transfer_search);
        addTransfer = findViewById(R.id.add_transfer);
        searchButton = findViewById(R.id.search);
        fromDate = findViewById(R.id.from_date);
        toDate = findViewById(R.id.to_date);
        emptyText = findViewById(R.id.empty_text);
        transferOutText.setOnClickListener(this);
        transferInText.setOnClickListener(this);
        transferOutButton.setOnClickListener(this);
        transferInButton.setOnClickListener(this);
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

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        fromDate.setText(formattedDate);
        toDate.setText(formattedDate);

        //   addTransfer.setVisibility(View.GONE);
        //    transferInButton.setEnabled(false);
        //    transferInText.setEnabled(false);
        getTransferInRequest("In", currentDate, currentDate);

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


        transferSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (transferAdapter != null) {
                    transferAdapter.getFilter().filter(s.toString());
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (!fromDate.getText().toString().isEmpty() && !toDate.getText().toString().isEmpty()) {
                    String fromdate = Utils.convertDate(fromDate.getText().toString(), "dd-MM-yyyy", "yyyyMMdd");
                    String todate = Utils.convertDate(toDate.getText().toString(), "dd-MM-yyyy", "yyyyMMdd");
                    getTransferInRequest(transferMode, fromdate, todate);
                } else {
                    Toast.makeText(getApplicationContext(), "Select the Date to Search..!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getDate(TextView dateTextView) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(TransferListProductActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateTextView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void setTransferListAdapter(ArrayList<TransferModel> transferList) {
        try {
            transferAdapter = new TransferAdapter(this, transferList, new TransferAdapter.CallBack() {
                @Override
                public void callDescription(String transferNo, String mode) {
                    Log.w("GivenTransferNo::", transferNo.toString());
                    if (mode.equals("Print")) {
                        try {
                            getTransferDetails(1, transferNo.toString(), transferType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), TransferPreviewPrintActivity.class);
                        intent.putExtra("title", transferType);
                        intent.putExtra("transferNumber", transferNo);
                        startActivity(intent);
                    }
                }

                @Override
                public void convertTransfer(String requestId) {

                }
            });
            int mNoOfColumns = Utils.calculateNoOfColumns(getApplicationContext(), 200);
            transferListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            transferListView.setItemAnimator(new DefaultItemAnimator());
            transferListView.setAdapter(transferAdapter);
            //categoriesView.setVisibility(View.VISIBLE);
            //emptyLayout.setVisibility(View.GONE);
        } catch (Exception ex) {
            Log.e("TAG", "Error in Populating the data:" + ex.getMessage());
        }
    }

    public void getTransferInRequest(String mode, String fromdate, String todate) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "InventoryTransferList";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("User", username);
            jsonObject.put("CustomerCode", "");
            jsonObject.put("VendorCode", "");
            jsonObject.put("FromDate", fromdate);
            jsonObject.put("WarehouseCode", locationCode);
            jsonObject.put("ToDate", todate);
            jsonObject.put("DocStatus", "");
            jsonObject.put("InorOut", mode);

            if (mode.equals("Out")) {
                transferType = "Transfer Out";
            } else {
                transferType = "Transfer In";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("Given_url:", url + "---" + jsonObject.toString());
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
        dialog = new ProgressDialog(TransferListProductActivity.this);
        dialog.setMessage("Loading Transfers List...");
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
                        Log.w("Response_TRANSFER:", response.toString());
                        transferList = new ArrayList<>();
                        String statusCode = response.optString("statusCode");
                        String statusMessage = response.optString("statusMessage");
                        if (statusCode.equals("1")) {
                            JSONArray transferDetailsArray = response.optJSONArray("responseData");
                            for (int i = 0; i < transferDetailsArray.length(); i++) {
                                JSONObject object = transferDetailsArray.optJSONObject(i);
                                TransferModel model = new TransferModel();
                                model.setTransferNo(object.optString("invTransNo"));
                                model.setDate(object.optString("docDate"));
                                model.setFromLocation(object.optString("fromWhsCode"));
                                model.setToLocation(object.optString("toWhsCode"));
                                model.setUser(object.optString("user"));
                                model.setStatus(object.optString("invTransStatus"));
                                transferList.add(model);
                            }
                            if (transferList.size() > 0) {
                                transferListView.setVisibility(View.VISIBLE);
                                emptyText.setVisibility(View.GONE);
                                setTransferListAdapter(transferList);
                            } else {
                                transferListView.setVisibility(View.GONE);
                                emptyText.setVisibility(View.VISIBLE);
                            }
                        } else {
                            transferListView.setVisibility(View.GONE);
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
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        transferDetailModels = new ArrayList<>();
        transferDetailsList = new ArrayList<>();

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
                            TransferDetailModel model = new TransferDetailModel();
                            model.setNumber(detailObject.optString("invTransNo"));
                            model.setStatus(detailObject.optString("invTransStatus"));
                            model.setDate(detailObject.optString("docDate"));
                            model.setFromLocation(detailObject.optString("fromWhsCode"));
                            model.setToLocation(detailObject.optString("toWhsCode"));
                            model.setFromLocationName(detailObject.optString("fromWarehouseName"));
                            model.setToLocationName(detailObject.optString("toWarehouseName"));

                            JSONArray itemsArray = detailObject.optJSONArray("itItem");
                            for (int i = 0; i < Objects.requireNonNull(itemsArray).length(); i++) {
                                JSONObject objectItem = itemsArray.optJSONObject(i);
                                TransferDetailModel.TransferDetails transferModel = new TransferDetailModel.TransferDetails();
                                transferModel.setDescription(objectItem.optString("itemName"));
                                transferModel.setQty(objectItem.optString("qty"));
                                transferModel.setUomCode(objectItem.optString("uomCode"));
                                transferDetailsList.add(transferModel);
                            }
                            model.setTransferDetailsList(transferDetailsList);
                            transferDetailModels.add(model);

                            printTransfer(transferNo, transferDetailModels, type);

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

    public void printTransfer(String transferNo, ArrayList<TransferDetailModel> transferDetailModels, String type) {
        if (transferDetailModels.size() > 0) {
            if (Utils.validatePrinterConfiguration(this,printerType,printerMacId)) {


            TSCPrinter printer = new TSCPrinter(this, printerMacId, "Transfer");
            try {
                printer.printTransferDetail(1, transferNo, type, transferDetailModels);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }else {
            Toast.makeText(getApplicationContext(),"Please configure the Printer",Toast.LENGTH_SHORT).show();
        }
    }

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
        if (v.getId() == R.id.transfer_in) {
            transferInButton.setBackgroundColor(Color.parseColor("#2ECC71"));
            transferInText.setTextColor(Color.parseColor("#FFFFFF"));
            transferOutButton.setBackgroundColor(Color.parseColor("#d3d3d3"));
            transferOutText.setTextColor(Color.parseColor("#212121"));
            String fromdate = Utils.convertDate(fromDate.getText().toString(), "dd-MM-yyyy", "yyyyMMdd");
            String todate = Utils.convertDate(toDate.getText().toString(), "dd-MM-yyyy", "yyyyMMdd");
            getTransferInRequest("In", fromdate, todate);
            transferMode = "Transfer In";
            // addTransfer.setVisibility(View.GONE);
            transferInButton.setEnabled(false);
            transferOutButton.setEnabled(true);
            transferInText.setEnabled(false);
            transferOutText.setEnabled(true);
        } else if (v.getId() == R.id.transfer_out) {
            transferOutButton.setBackgroundColor(Color.parseColor("#2ECC71"));
            transferOutText.setTextColor(Color.parseColor("#FFFFFF"));
            transferInText.setTextColor(Color.parseColor("#212121"));
            transferInButton.setBackgroundColor(Color.parseColor("#d3d3d3"));
            String fromdate = Utils.convertDate(fromDate.getText().toString(), "dd-MM-yyyy", "yyyyMMdd");
            String todate = Utils.convertDate(toDate.getText().toString(), "dd-MM-yyyy", "yyyyMMdd");
            getTransferInRequest("Out", fromdate, todate);
            transferMode = "Transfer Out";
            //addTransfer.setVisibility(View.VISIBLE);
            transferInButton.setEnabled(true);
            transferOutButton.setEnabled(false);
            transferInText.setEnabled(true);
            transferOutText.setEnabled(false);
        } else if (v.getId() == R.id.tranfer_in_text) {
            transferInButton.setBackgroundColor(Color.parseColor("#2ECC71"));
            transferInText.setTextColor(Color.parseColor("#FFFFFF"));
            transferOutButton.setBackgroundColor(Color.parseColor("#d3d3d3"));
            transferOutText.setTextColor(Color.parseColor("#212121"));
            String fromdate = Utils.convertDate(fromDate.getText().toString(), "dd-MM-yyyy", "yyyyMMdd");
            String todate = Utils.convertDate(toDate.getText().toString(), "dd-MM-yyyy", "yyyyMMdd");
            getTransferInRequest("In", fromdate, todate);
            transferMode = "Transfer In";
            // addTransfer.setVisibility(View.GONE);
            transferInButton.setEnabled(false);
            transferOutButton.setEnabled(true);
            transferInText.setEnabled(false);
            transferOutText.setEnabled(true);
        } else if (v.getId() == R.id.transfer_out_text) {
            transferOutButton.setBackgroundColor(Color.parseColor("#2ECC71"));
            transferOutText.setTextColor(Color.parseColor("#FFFFFF"));
            transferInText.setTextColor(Color.parseColor("#212121"));
            transferInButton.setBackgroundColor(Color.parseColor("#d3d3d3"));
            String fromdate = Utils.convertDate(fromDate.getText().toString(), "dd-MM-yyyy", "yyyyMMdd");
            String todate = Utils.convertDate(toDate.getText().toString(), "dd-MM-yyyy", "yyyyMMdd");
            getTransferInRequest("Out", fromdate, todate);
            transferMode = "Transfer Out";
            // addTransfer.setVisibility(View.VISIBLE);
            transferInButton.setEnabled(true);
            transferOutButton.setEnabled(false);
            transferInText.setEnabled(true);
            transferOutText.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.transfer_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//finish();
            onBackPressed();
        } else if (item.getItemId() == R.id.action_add) {
            transferType = transferMode;
            // Intent intent=new Intent(getApplicationContext(),TransferActivity.class);
            //intent.putExtra("transferType",transferType);
            // startActivity(intent);

            Intent intent = new Intent(getApplicationContext(), TransferInActivity.class);
            intent.putExtra("transferType", transferType);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}