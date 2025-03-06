package com.winapp.svcatering.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.winapp.svcatering.R;
import com.winapp.svcatering.ReportPreview.CustomerLisrOutstandPreviewActivity;
import com.winapp.svcatering.adapter.SelectCustomerAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.googlemap.MapsActivity;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerGroupModel;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.model.UserRoll;
import com.winapp.svcatering.salesreturn.NewSalesReturnProductAddActivity;
import com.winapp.svcatering.utils.BarCodeScanner;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.SharedPreferenceUtil;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CustomerListActivity extends NavigationActivity {
    private RecyclerView customerView;
    ArrayList<CustomerModel> customerList;
    SelectCustomerAdapter customerNameAdapter;
    SweetAlertDialog pDialog;
    SessionManager session;
    private HashMap<String, String> user;
    private String companyId;
    EditText customerNameEdittext;
    TextView totalCustomers;
    BottomSheetBehavior behavior;
    LinearLayout transLayout;
    TextView optionCancel;
    TextView customerName;
    TextView customerId;
    FloatingActionButton createSalesOrder;
    FloatingActionButton createInvoice;
    FloatingActionButton deleteCustomer;
    FloatingActionButton showLocation;
    FloatingActionButton addReceipt;
    DBHelper dbHelper;
    private SharedPreferenceUtil sharedPreferenceUtil;

    int RESULT_CODE = 12;
    LinearLayout createInvoiceLayout;
    LinearLayout addReceiptLayout;
    LinearLayout createOrderLayout;
    LinearLayout deleteLayout;
    LinearLayout showLocationLayout;
    ProgressDialog dialog;
    String message;
    private ArrayList<CustomerDetails> allCustomersList;
    private String outstandingAmount;
    private String username;

    private String billDiscApi,zoneStr;

    public String createInvoiceSetting = "false";
    private Spinner customerGroupSpinner;
    private ArrayList<CustomerGroupModel> customersGroupList;

    private static String FILE = Environment.getExternalStorageDirectory()
            + "/HelloWorld.pdf";

    private LinearLayout customerPrintPreviewLayout;
    private FloatingActionButton customerPrintPreview;
    private LinearLayout customerPrintLayout;
    private FloatingActionButton customerPrint;
    private String locationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_customer_list, contentFrameLayout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Customers");
        customerView = findViewById(R.id.customerList);
        session = new SessionManager(this);
        user = session.getUserDetails();
        companyId = user.get(SessionManager.KEY_COMPANY_CODE);
        username = user.get(SessionManager.KEY_USER_NAME);
        locationCode = user.get(SessionManager.KEY_LOCATION_CODE);
        customerNameEdittext = findViewById(R.id.customer_search);
        totalCustomers = findViewById(R.id.total_customers);
        transLayout = findViewById(R.id.trans_layout);
        optionCancel = findViewById(R.id.option_cancel);
        customerName = findViewById(R.id.name);
        customerId = findViewById(R.id.cus_id);
        createSalesOrder = findViewById(R.id.create_sales);
        createInvoice = findViewById(R.id.create_invoice);
        deleteCustomer = findViewById(R.id.delete_customer);
        createInvoiceLayout = findViewById(R.id.create_invoice_layout);
        createOrderLayout = findViewById(R.id.create_order_layout);
        deleteLayout = findViewById(R.id.delete_layout);
        showLocationLayout = findViewById(R.id.location_layout);
        showLocation = findViewById(R.id.show_location);
        customerGroupSpinner = findViewById(R.id.customer_group);
        addReceiptLayout = findViewById(R.id.add_receipt_layout);
        customerPrintPreviewLayout = findViewById(R.id.customer_outstanding_preview);
        customerPrintPreview = findViewById(R.id.customer_outstanding_print_preview);
        customerPrint = findViewById(R.id.customer_outstanding_print);
        customerPrintLayout = findViewById(R.id.outstanding_print);
        addReceipt = findViewById(R.id.add_receipt);

        dbHelper = new DBHelper(this);
        sharedPreferenceUtil = new SharedPreferenceUtil(this);

        zoneStr = sharedPreferenceUtil.getStringPreference(
                Constants.KEY_ADDRESS_ZONE_CODE,
                Constants.DEFAULT_STRING
        ) ;
      /*  if (getIntent()!=null){
            message=getIntent().getStringExtra("Message");
            assert message != null;
            if (message.equals("newCustomer")){
                getCustomers();
            }else {
                customerList=new ArrayList<>();
                customerList=dbHelper.getAllCustomers();
                if (customerList.size()>0){
                    setAdapter(customerList);
                }else {
                    getCustomers();
                }
            }
        }else {
            customerList=new ArrayList<>();
            customerList=dbHelper.getAllCustomers();
            if (customerList.size()>0){
                setAdapter(customerList);
            }else {
                getCustomers();
            }
        }*/

        try {
            getCustomersGroups(username);
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<SettingsModel> settings = dbHelper.getSettings();
        if (settings != null) {
            if (settings.size() > 0) {
                for (SettingsModel model : settings) {
                    if (model.getSettingName().equals("create_invoice_switch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            createInvoiceSetting = "true";
                        } else {
                            createInvoiceSetting = "false";
                        }
                    }
                }
            }
        }

        customerNameEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String cusname = editable.toString();
                if (!cusname.isEmpty()) {
                    filter(cusname);
                }
            }
        });

        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        getSupportActionBar().setTitle("Select Option");
                        transLayout.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        getSupportActionBar().setTitle("Customers");
                        transLayout.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);
            }
        });

        optionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        createOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                dbHelper.removeAllItems();
                AddInvoiceActivityOld.customerId = customerId.getText().toString();
                Utils.setCustomerSession(CustomerListActivity.this, customerId.getText().toString());
                if (createInvoiceSetting.equals("true")) {
                    Intent intent = new Intent(getApplicationContext(), CreateNewInvoiceActivity.class);
                    intent.putExtra("customerName", customerName.getText().toString());
                    intent.putExtra("customerCode", customerId.getText().toString());
                    intent.putExtra("customerBillDisc", billDiscApi);
                    intent.putExtra("activityFrom", "SalesOrder");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CustomerListActivity.this, AddInvoiceActivityOld.class);
                    intent.putExtra("customerId", customerId.getText().toString());
                    intent.putExtra("activityFrom", "SalesOrder");
                    startActivity(intent);
                    viewCloseBottomSheet();
                }
            }
        });


        createSalesOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                dbHelper.removeAllItems();
                AddInvoiceActivityOld.customerId = customerId.getText().toString();
                Utils.setCustomerSession(CustomerListActivity.this, customerId.getText().toString());
                if (createInvoiceSetting.equals("true")) {
                    Intent intent = new Intent(getApplicationContext(), CreateNewInvoiceActivity.class);
                    intent.putExtra("customerName", customerName.getText().toString());
                    intent.putExtra("customerCode", customerId.getText().toString());
                    intent.putExtra("customerBillDisc", billDiscApi);
                    intent.putExtra("activityFrom", "SalesOrder");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CustomerListActivity.this, AddInvoiceActivityOld.class);
                    intent.putExtra("customerId", customerId.getText().toString());
                    intent.putExtra("activityFrom", "SalesOrder");
                    startActivity(intent);
                    viewCloseBottomSheet();
                }
            }
        });

        createInvoiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                dbHelper.removeAllItems();
                AddInvoiceActivityOld.customerId = customerId.getText().toString();
                Utils.setCustomerSession(CustomerListActivity.this, customerId.getText().toString());
                if (createInvoiceSetting.equals("true")) {
                    Intent intent = new Intent(getApplicationContext(), CreateNewInvoiceActivity.class);
                    intent.putExtra("customerName", customerName.getText().toString());
                    intent.putExtra("customerCode", customerId.getText().toString());
                    intent.putExtra("customerBillDisc", billDiscApi);
                    intent.putExtra("activityFrom", "Invoice");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CustomerListActivity.this, AddInvoiceActivityOld.class);
                    intent.putExtra("customerId", customerId.getText().toString());
                    intent.putExtra("activityFrom", "Invoice");
                    startActivity(intent);
                    viewCloseBottomSheet();
                }
            }
        });

        createInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                dbHelper.removeAllItems();
                AddInvoiceActivityOld.customerId = customerId.getText().toString();
                Utils.setCustomerSession(CustomerListActivity.this, customerId.getText().toString());
                if (createInvoiceSetting.equals("true")) {
                    Intent intent = new Intent(getApplicationContext(), CreateNewInvoiceActivity.class);
                    intent.putExtra("customerName", customerName.getText().toString());
                    intent.putExtra("customerCode", customerId.getText().toString());
                    intent.putExtra("customerBillDisc", billDiscApi);
                    intent.putExtra("activityFrom", "Invoice");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CustomerListActivity.this, AddInvoiceActivityOld.class);
                    intent.putExtra("customerId", customerId.getText().toString());
                    intent.putExtra("activityFrom", "Invoice");
                    startActivity(intent);
                    viewCloseBottomSheet();
                }
            }
        });

        showLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerListActivity.this, MapsActivity.class);
                intent.putExtra("customerName", customerName.getText().toString());
                intent.putExtra("customerId", customerId.getText().toString());
                intent.putExtra("outstanding", outstandingAmount);
                intent.putExtra("address", "13,North Street , T.Nagar, Chennai..");
                intent.putExtra("latitude", "9.939093");
                intent.putExtra("longitude", "78.121719");
                startActivity(intent);
                viewCloseBottomSheet();
            }
        });

        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerListActivity.this, MapsActivity.class);
                intent.putExtra("customerName", customerName.getText().toString());
                intent.putExtra("customerId", customerId.getText().toString());
                intent.putExtra("outstanding", outstandingAmount);
                intent.putExtra("address", "13,North Street , T.Nagar, Chennai..");
                intent.putExtra("latitude", "9.939093");
                intent.putExtra("longitude", "78.121719");
                startActivity(intent);
                viewCloseBottomSheet();
            }
        });

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveAlert(customerId.getText().toString());
            }
        });

        deleteCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveAlert(customerId.getText().toString());
            }
        });

        addReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                Intent intent = new Intent(CustomerListActivity.this, CashCollectionActivity.class);
                intent.putExtra("customerCode", customerId.getText().toString());
                intent.putExtra("customerName", customerName.getText().toString());
                startActivity(intent);
                finish();
            }
        });

        addReceiptLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                Intent intent = new Intent(CustomerListActivity.this, CashCollectionActivity.class);
                intent.putExtra("customerCode", customerId.getText().toString());
                intent.putExtra("customerName", customerName.getText().toString());
                startActivity(intent);
                finish();
            }
        });


        // Define the Customer outstanding print and printpreview
        customerPrintPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCloseBottomSheet();
                Intent intent = new Intent(CustomerListActivity.this, CustomerLisrOutstandPreviewActivity.class);
                intent.putExtra("fromDate", Utils.getCurrentDateWithSlace().toString());
                intent.putExtra("toDate", Utils.getCurrentDateWithSlace().toString());
                intent.putExtra("locationCode", "");
                intent.putExtra("companyId", "");
                intent.putExtra("customerCode", customerId.getText().toString());
                intent.putExtra("customerName", customerName.getText().toString());
                intent.putExtra("userName", username);
                intent.putExtra("status", "O");
                startActivity(intent);
            }
        });

        customerPrintPreviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCloseBottomSheet();
                Intent intent = new Intent(CustomerListActivity.this, CustomerLisrOutstandPreviewActivity.class);
                intent.putExtra("fromDate", Utils.getCurrentDateWithSlace().toString());
                intent.putExtra("toDate", Utils.getCurrentDateWithSlace().toString());
                intent.putExtra("locationCode", "");
                intent.putExtra("companyId", "");
                intent.putExtra("customerCode", customerId.getText().toString());
                intent.putExtra("customerName", customerName.getText().toString());
                intent.putExtra("userName", username);
                intent.putExtra("status", "O");
                startActivity(intent);
            }
        });


    }

    public void setCustomerGroupSpinner(ArrayList<CustomerGroupModel> customersGroupList) {
        ArrayAdapter<CustomerGroupModel> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, customersGroupList);
        customerGroupSpinner.setAdapter(adapter);
        customerGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String groupCode = customersGroupList.get(position).getCustomerGroupCode();
                String groupName = customersGroupList.get(position).getCustomerGroupName();
                if(groupName.equalsIgnoreCase("Agent")) {  //faisal said this condition
                    getCustomers(groupCode,"");
                }else{
                    getCustomers(groupCode,zoneStr);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void showRemoveAlert(String customerId) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                // .setTitleText("Are you sure?")
                .setContentText("Are you sure want delete Customer ?")
                .setConfirmText("YES")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        try {
                            sDialog.dismiss();
                            viewCloseBottomSheet();
                            setDeleteCustomer(customerId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .showCancelButton(true)
                .setCancelText("No")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                }).show();
    }


    private void setDeleteCustomer(String customerCode) throws JSONException {
        // Initialize a new RequestQueue instance

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("CompanyCode", companyId);
        jsonObject.put("CustomerCode", customerCode);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "MasterApi/DeleteCustomer?Requestdata=" + jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url);
        dialog = new ProgressDialog(CustomerListActivity.this);
        dialog.setMessage("Updating Customer List...");
        dialog.setCancelable(false);
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    try {
                        Log.w("Delete_Response_is:", response.toString());
                        if (response.length() > 0) {
                            boolean issaved = response.optBoolean("IsSaved");
                            String result = response.optString("Result");
                            if (issaved && result.equals("pass")) {
                                finish();
                                startActivity(getIntent());
                            } else {
                                Toast.makeText(getApplicationContext(), "Error in Deleting Customer", Toast.LENGTH_LONG).show();
                            }
                        }
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            dialog.dismiss();
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


    public void viewCloseBottomSheet() {
        //  hideKeyboard();
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        // get the Customer name from the local db
    }

    public void getCustomersGroups(String username) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "BPGroupList";
        customerList = new ArrayList<>();
        allCustomersList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("User", username);
            jsonObject.put("LocationCode", locationCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("Given_url_custGroup:", url+jsonObject);
        dialog = new ProgressDialog(CustomerListActivity.this);
        dialog.setMessage("Loading Customers Groups...");
        dialog.setCancelable(false);
        dialog.show();
        customersGroupList = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url, jsonObject,
                response -> {
                    try {
                        Log.w("SAP_CUSTOMERS_GROUP:", response.toString());
                        String statusCode = response.optString("statusCode");
                        if (statusCode.equals("1")) {
                            JSONArray customerDetailArray = response.optJSONArray("responseData");
                            for (int i = 0; i < Objects.requireNonNull(customerDetailArray).length(); i++) {
                                JSONObject object = customerDetailArray.optJSONObject(i);
                                CustomerGroupModel model = new CustomerGroupModel();
                                model.setCustomerGroupCode(object.optString("groupCode"));
                                model.setCustomerGroupName(object.optString("groupName"));
                                customersGroupList.add(model);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error,in getting Customer Group list", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                        if (customersGroupList.size() > 0) {
                            setCustomerGroupSpinner(customersGroupList);
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


    public void getCustomers(String groupCode,String zoneStr) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "CustomerList";
        customerList = new ArrayList<>();
        allCustomersList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("GroupCode", groupCode);
            jsonObject.put("LocationCode", locationCode);
            jsonObject.put("Dormitory", zoneStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("Given_urlCustli:", url + "" + jsonObject.toString());
        dialog = new ProgressDialog(CustomerListActivity.this);
        dialog.setMessage("Loading Customers List...");
        dialog.setCancelable(false);
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        Log.w("Response_SAP_CUSTOMERS:", response.toString());
                        String statusCode = response.optString("statusCode");
                        String message = response.optString("statusMessage");
                        if (statusCode.equals("1")) {
                            JSONArray customerDetailArray = response.optJSONArray("responseData");
                            for (int i = 0; i < customerDetailArray.length(); i++) {
                                JSONObject object = customerDetailArray.optJSONObject(i);
                                CustomerModel model = new CustomerModel();
                                model.setCustomerCode(object.optString("customerCode"));
                                model.setCustomerName(object.optString("customerName"));
                                model.setPhoneNo(object.optString("ContactNo"));
                                model.setAddress1(object.optString("address"));
                                model.setAddress2(object.optString("street"));
                                model.setAddress3(object.optString("city"));
                                model.setCustomerAddress(object.optString("address"));
                                model.setHaveTax(object.optString("HaveTax"));
                                model.setTaxType(object.optString("taxType"));
                                model.setTaxPerc(object.optString("taxPercentage"));
                                model.setTaxCode(object.optString("taxCode"));
                                model.setBillDiscPercentage(object.optString("discountPercentage"));
                                model.setAllowFOC(object.optString("allowFOC"));
                                model.setMailId(object.optString("mailId"));

                                if (object.optString("outstandingAmount").equals("null") || object.optString("outstandingAmount").isEmpty()) {
                                    model.setOutstandingAmount("0.00");
                                } else {
                                    model.setOutstandingAmount(object.optString("outstandingAmount"));
                                }
                                customerList.add(model);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error:" + message, Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                        if (customerList.size() > 0) {
                            setAdapter(customerList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    dialog.dismiss();
//                    if (error instanceof TimeoutError){
//                        Toast.makeText(getApplicationContext(), error.getLocalizedMessage(),
//                                Toast.LENGTH_SHORT).show();
//
//                    }else
//                        Toast.makeText(getApplicationContext(), error.getLocalizedMessage(),
//                                Toast.LENGTH_SHORT).show();
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(4 * 1000,
                2, 2));
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    private void setAdapter(ArrayList<CustomerModel> customerNames) {
        customerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        totalCustomers.setText(customerNames.size() + " Customers");

        customerNameAdapter = new SelectCustomerAdapter(this, customerNames,
                new SelectCustomerAdapter.OnMoreButtonClicked() {
            @Override
            public void moreOption(String customer, String customer_code, String outstanding ,String isFOCApi) {
                if (getIntent() != null) {
                    String from = getIntent().getStringExtra("from");
                    if (from.equals("do") || from.equals("so") || from.equals("iv") || from.equals("sales_return")) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("customerCode", customer_code);
                        returnIntent.putExtra("isFOC", isFOCApi);

                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else {
                        customerName.setText(customer);
                        customerId.setText(customer_code);
                        outstandingAmount = outstanding;
                        viewCloseBottomSheet();
                    }
                } else {
                    customerName.setText(customer);
                    customerId.setText(customer_code);
                    outstandingAmount = outstanding;
                    viewCloseBottomSheet();
                }
            }

            @Override
            public void createInvoice(String customerCode, String cusname, String taxcode, String taxperc,
                                      String taxtype
                    ,String billDisc,String isFOCApi , String mailId) {
                if (createInvoiceSetting.equals("true")) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    dbHelper.removeCustomerTaxes();
                    CustomerDetails model = new CustomerDetails();
                    model.setCustomerCode(customerCode);
                    model.setCustomerName(cusname);
                    model.setCustomerAddress1("");
                    model.setTaxPerc(taxperc);
                    model.setTaxType(taxtype);
                    model.setTaxCode(taxcode);
                    model.setBillDiscPercentage(billDisc);
                    model.setAllowFOC(isFOCApi);
                    ArrayList<CustomerDetails> taxList = new ArrayList<>();
                    taxList.add(model);

                    billDiscApi = billDisc;
                    Log.w("billdiscpii::", billDiscApi);
                    dbHelper.insertCustomerTaxValues(taxList);
                    Utils.setCustomerSession(CustomerListActivity.this, customerCode);
                    Log.w("TaxValueUpdated::", "Success");
                    Log.w("CurrentSavingTime::", currentDateandTime);

                    String from = getIntent().getStringExtra("from");
                    if (from.equals("sales_return")) {
                        Intent intent = new Intent(getApplicationContext(), NewSalesReturnProductAddActivity.class);
                        intent.putExtra("customerName", cusname);
                        intent.putExtra("currentDateTime", currentDateandTime);
                        intent.putExtra("customerCode", customerCode);
                        intent.putExtra("from", from);
                        startActivity(intent);
                    } else if (from.equals("cus")) {
                        customerName.setText(cusname);
                        customerId.setText(customerCode);
                        //outstandingAmount=outstanding;
                        viewCloseBottomSheet();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), CreateNewInvoiceActivity.class);
                        intent.putExtra("customerName", cusname);
                        intent.putExtra("currentDateTime", currentDateandTime);
                        intent.putExtra("customerCode", customerCode);
                        intent.putExtra("customerBillDisc", billDisc);
                        intent.putExtra("from", from);
                        intent.putExtra("isFOC", isFOCApi);
                        intent.putExtra("isMailId", mailId);

                        startActivity(intent);
                    }

                } else {
                    String from = getIntent().getStringExtra("from");
                    if (from.equals("cus")) {
                        customerName.setText(cusname);
                        customerId.setText(customerCode);
                        //outstandingAmount=outstanding;
                        viewCloseBottomSheet();
                    }
                }
            }
        });
        customerView.setAdapter(customerNameAdapter);
    }

    private void filter(String text) {
        try {
            //new array list that will hold the filtered data
            ArrayList<CustomerModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (CustomerModel s : customerList) {
                //if the existing elements contains the search input
                if (s.getCustomerName().toLowerCase().contains(text.toLowerCase())
                        || s.getCustomerCode().toLowerCase().contains(text.toLowerCase())
                        || s.getPhoneNo().toLowerCase().contains(text.toLowerCase())) {
                    //adding the element to filtered list
                    filterdNames.add(s);
                }
            }
            //calling a method of the adapter class and passing the filtered list
            customerNameAdapter.filterList(filterdNames);
            totalCustomers.setText(filterdNames.size() + " Customers");

        } catch (Exception ex) {
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sorting_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_filter);
        menuItem.setVisible(false);
        MenuItem addcustomer = menu.findItem(R.id.action_add);
        addcustomer.setVisible(false);
        MenuItem barcode = menu.findItem(R.id.action_barcode);
        barcode.setVisible(false);
        ArrayList<UserRoll> userRolls = helper.getUserPermissions();
        if (userRolls.size() > 0) {
            for (UserRoll roll : userRolls) {
                if (roll.getFormName().equals("Customer Add")) {
                    if (roll.getHavePermission().equals("true")) {
                        addcustomer.setVisible(true);
                    } else {
                        addcustomer.setVisible(false);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//finish();
            // onBackPressed();
            finish();
        } else if (item.getItemId() == R.id.action_barcode) {
            if (customerNameEdittext.getText().toString().length() > 0) {
                customerNameEdittext.clearFocus();
                customerNameEdittext.setText("");
                setAdapter(customerList);
            }
            Intent intent = new Intent(CustomerListActivity.this, BarCodeScanner.class);
            startActivityForResult(intent, RESULT_CODE);
        } else if (item.getItemId() == R.id.action_add) {
            if (customerNameEdittext.getText().toString().length() > 0) {
                customerNameEdittext.clearFocus();
                customerNameEdittext.setText("");
                setAdapter(customerList);
            }
            Intent intent = new Intent(getApplicationContext(), AddCustomerActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_CODE) {
                String barcodeText = data.getExtras().getString("Contents");
                if (customerList.size() > 0) {
                    for (CustomerModel model : customerList) {
                        if (model.getCustomerBarcode().equals(barcodeText)) {
                            customerName.setText(model.getCustomerName());
                            customerId.setText(model.getCustomerCode());
                            viewCloseBottomSheet();
                        }
                    }
                } else {
                    Toast.makeText(CustomerListActivity.this, "No Such Customer", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //finish();
        Intent intent = new Intent(CustomerListActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}