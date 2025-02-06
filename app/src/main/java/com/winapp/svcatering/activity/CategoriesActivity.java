package com.winapp.svcatering.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.CustomerNameAdapter;
import com.winapp.svcatering.adapter.ViewPagerAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.AllCategories;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerGroupModel;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.HomePageModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.SharedPreferenceUtil;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.winapp.svcatering.activity.AddInvoiceActivityOld.activityFrom;
import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

public class CategoriesActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private int noOfTabs = 3;
    private SweetAlertDialog pDialog;
    private SessionManager session;
    private HashMap<String, String> user;
    private String companyCode;
    public ArrayList<AllCategories> allCategoriesList;
    private ArrayList<String> categories = new ArrayList<>();
    public static TextView textCartItemCount;
    public static int mCartItemCount = 0;
    public static DBHelper helper;
    public LinearLayout emptyLayout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor customerPredEdit;
    public ArrayList<CustomerDetails> customerDetails;
    private DBHelper dbHelper;
    private SharedPreferenceUtil sharedPreferenceUtil;

    public static TextView selectCustomer;
    public ArrayList<CustomerModel> customerList;
    public TextView dateText;
    private View progressLayout;
    private LinearLayout rootLayout;
    private RecyclerView customerView;
    public CustomerNameAdapter customerNameAdapter;
    public BottomSheetBehavior behavior;
    public TextView userName;
    private ArrayList<CustomerDetails> allCustomersList;
    public EditText customerNameEdittext;
    public Button btnCancel;
    private View customerLayout;
    private View descriptionLayout;
    private ProgressDialog dialog;
    private Spinner customerGroupSpinner;
    private ArrayList<CustomerGroupModel> customersGroupList;
    private String username;
    private String allowFOCStr;
    public static JSONObject customerResponse = new JSONObject();
    private String creditLimitAmount = "0.00";
    private String outstandingAmount = "0.00";
    private String locationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Catalog");

        Log.w("activity_cg",getClass().getSimpleName().toString()+"ProductAdapterLoadMore");

        // product loading apis
//        https://c21326-EasySales-Test.cloudiax.com/api/CategoryDetails {"CategoryCode": "102", "LocationCode": "01"}
//        https://c21326-EasySales-Test.cloudiax.com/api/CategoryList

        session = new SessionManager(this);
        dbHelper = new DBHelper(this);
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        sharedPreferenceUtil.setStringPreference(sharedPreferenceUtil.KEY_ALLOW_FOC, "");

        user = session.getUserDetails();
        username = user.get(SessionManager.KEY_USER_NAME);
        locationCode = user.get(SessionManager.KEY_LOCATION_CODE);
        companyCode = user.get(SessionManager.KEY_COMPANY_CODE);

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager_Categor);
        userName = findViewById(R.id.user_name);
        emptyLayout = findViewById(R.id.empty_layout);
        helper = new DBHelper(this);
        selectCustomer = findViewById(R.id.select_customer);
        dateText = findViewById(R.id.date);
        progressLayout = findViewById(R.id.customer_progress);
        rootLayout = findViewById(R.id.rootLayout);
        sharedPreferences = getSharedPreferences("customerPref", MODE_PRIVATE);
        customerView = findViewById(R.id.customerView);
        userName.setText(user.get(SessionManager.KEY_USER_NAME));
        customerLayout = findViewById(R.id.customer_layout);
        descriptionLayout = findViewById(R.id.description_layout_portrait);
        customerGroupSpinner = findViewById(R.id.customer_group);

        customerNameEdittext = findViewById(R.id.customer_search);
        btnCancel = findViewById(R.id.btn_cancel);
        //emptyLayout=findViewById(R.id.empty_layout);

        String customerId = sharedPreferences.getString("customerId", "");
        if (customerId != null && !customerId.equals("empty") && !customerId.isEmpty()) {
            customerDetails = dbHelper.getCustomer(customerId);
            selectCustomer.setText(customerDetails.get(0).getCustomerName());
        } else {
            selectCustomer.setText("Select Customer");
        }

        try {
            getCustomersGroups(username);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        dateText.setText(formattedDate);

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
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        customerNameEdittext.setText("");
                        // Then just use the following:
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(rootLayout.getWindowToken(), 0);
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
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

        selectCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerLayout.setVisibility(View.VISIBLE);
                descriptionLayout.setVisibility(View.GONE);
                viewCloseBottomSheet();
            }
        });


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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        getCategories();
        // new GetCategoriesTask().execute();

        if (activityFrom != null) {
            if (activityFrom.equals("InvoiceEdit")
                    || activityFrom.equals("SalesEdit")
                    || activityFrom.equals("ConvertInvoice")
                    || activityFrom.equals("ReOrderInvoice")
                    || activityFrom.equals("ReOrderSales")
            ) {
                dbHelper.removeAllItems();
                Utils.clearCustomerSession(this);
            }
        }
        //  FirebaseCrashlytics.getInstance().sendUnsentReports();
        //   throw new ArithmeticException("Test fix"); // Force a crash
    }

    private void filter(String text) {
        try {
            //new array list that will hold the filtered data
            ArrayList<CustomerModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (CustomerModel s : customerList) {
                //if the existing elements contains the search input
                if (s.getCustomerName().toLowerCase().contains(text.toLowerCase())
                        || s.getCustomerCode().toLowerCase().contains(text.toLowerCase())) {
                    //adding the element to filtered list
                    filterdNames.add(s);
                }
            }
            //calling a method of the adapter class and passing the filtered list
            customerNameAdapter.filterList(filterdNames);

        } catch (Exception ex) {
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
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
        Log.w("Given_urlGroup:", url+jsonObject);
        dialog = new ProgressDialog(CategoriesActivity.this);
        dialog.setMessage("Loading Customers Groups...");
        dialog.setCancelable(false);
        dialog.show();
        customersGroupList = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        Log.w("SAP_CUSTOMERS_GROUP:", response.toString());
                        String statusCode = response.optString("statusCode");
                        if (statusCode.equals("1")) {
                            JSONArray customerDetailArray = response.optJSONArray("responseData");
                            for (int i = 0; i < customerDetailArray.length(); i++) {
                                JSONObject object = customerDetailArray.optJSONObject(i);
                                CustomerGroupModel model = new CustomerGroupModel();
                                model.setCustomerGroupCode(object.optString("groupCode"));
                                model.setCustomerGroupName(object.optString("groupName"));
                                customersGroupList.add(model);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error,in getting Customer Group list", Toast.LENGTH_LONG).show();
                        }
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

    public void setCustomerGroupSpinner(ArrayList<CustomerGroupModel> customersGroupList) {
        ArrayAdapter<CustomerGroupModel> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, customersGroupList);
        customerGroupSpinner.setAdapter(adapter);
        customerGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String groupCode = customersGroupList.get(position).getCustomerGroupCode();
                String groupName = customersGroupList.get(position).getCustomerGroupName();
                getCustomers(groupCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    public void getCustomers(String groupCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                synchronized (this) {
                RequestQueue requestQueue = Volley.newRequestQueue(CategoriesActivity.this);
                String url = Utils.getBaseUrl(CategoriesActivity.this) + "CustomerList";
                customerList = new ArrayList<>();
                customerList.clear();
                allCustomersList = new ArrayList<>();
//                dialog.setMessage("Loading Customers...");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (dialog != null && !dialog.isShowing()) {
//                            dialog.show();
//                        }
//                    }
//                });
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("GroupCode", groupCode);
                    jsonObject.put("LocationCode", locationCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.w("AllCustomerUrl:", url);
                JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                        response -> {
                            try {
                                Log.w("Response_is:", response.toString());
                                // Loop through the array elements
                                String statusCode = response.optString("statusCode");
                                if (statusCode.equals("1")) {
                                    JSONArray customerDetailArray = response.optJSONArray("responseData");
                                    for (int i = 0; i < customerDetailArray.length(); i++) {
                                        JSONObject object = customerDetailArray.optJSONObject(i);
                                        CustomerModel model = new CustomerModel();

                                        model.setCustomerCode(object.optString("customerCode"));
                                        model.setCustomerName(object.optString("customerName"));
                                        model.setAddress1(object.optString("address"));
                                        model.setAddress2(object.optString("street"));
                                        model.setAddress3(object.optString("city"));
                                        model.setCustomerAddress(object.optString("address"));
                                        model.setHaveTax(object.optString("HaveTax"));
                                        model.setTaxType(object.optString("taxType"));
                                        model.setTaxPerc(object.optString("taxPercentage"));
                                        model.setTaxCode(object.optString("taxCode"));
                                        model.setAllowFOC(object.optString("allowFOC"));
                                        model.setBillDiscPercentage(object.optString("discountPercentage"));


                                        if (object.optString("outstandingAmount").equals("null") || object.optString("outstandingAmount").isEmpty()) {
                                            model.setOutstandingAmount("0.00");
                                        } else {
                                            model.setOutstandingAmount(object.optString("outstandingAmount"));
                                        }
                                        customerList.add(model);
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error,in getting Customer list", Toast.LENGTH_LONG).show();
                                }
                                HomePageModel.customerList = new ArrayList<>();
                                HomePageModel.customerList.addAll(customerList);

                                setAdapter(customerList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            // Do something when error occurred
//                            if (dialog != null && dialog.isShowing()) {
//                                runOnUiThread(() -> dialog.dismiss());
//                            }
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
//            }
        }).start();
    }


    public void getCustomerDetails(String customerCode, boolean isloader, String from) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("CustomerCode", customerCode);
            // jsonObject.put("CompanyCode",companyCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("JsonValueForCustomer:", jsonObject.toString());
        String url = Utils.getBaseUrl(getApplicationContext()) + "Customer";
        Log.w("Given_url_cart:", url);
        ProgressDialog progressDialog = new ProgressDialog(CategoriesActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Customer Details Loading...");
        if (isloader) {
            progressDialog.show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        progressDialog.dismiss();
                        Log.w("SAP-res_cart_cust:", response.toString());
                        ArrayList<CustomerModel> customerList = new ArrayList<>();
                        String statusCode = response.optString("statusCode");
                        if (statusCode.equals("1")) {
                            customerResponse = response;
                            JSONArray customerDetailArray = response.optJSONArray("responseData");
                            for (int i = 0; i < customerDetailArray.length(); i++) {
                                JSONObject object = customerDetailArray.optJSONObject(i);
                                setAllValues(object);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error,in getting Customer list", Toast.LENGTH_LONG).show();
                        }
                        // pDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            //  pDialog.dismiss();
            Log.w("Error_throwing:", error.toString());
            progressDialog.dismiss();
            // Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
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

    private void setAllValues(JSONObject customerObject) {
        try {
            dbHelper.removeCustomerTaxes();
            CustomerDetails model = new CustomerDetails();
            model.setCustomerCode(customerObject.optString("customerCode"));
            model.setCustomerName(customerObject.optString("customerName"));
            model.setCustomerAddress1(customerObject.optString("address"));
            model.setTaxPerc(customerObject.optString("taxPercentage"));
            model.setTaxType(customerObject.optString("taxType"));
            model.setTaxCode(customerObject.optString("taxCode"));
            model.setAllowFOC(customerObject.optString("allowFOC"));
            String allowFOCL = customerObject.optString("allowFOC");
            sharedPreferenceUtil.setStringPreference(sharedPreferenceUtil.KEY_ALLOW_FOC, allowFOCL);

            ArrayList<CustomerDetails> taxList = new ArrayList<>();
            taxList.add(model);
            dbHelper.insertCustomerTaxValues(taxList);
        } catch (Exception exception) {
        }
    }

    private class InsertCustomerTask extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(Void... arg0) {
            dbHelper.removeAllCustomers();
            dbHelper.insertCustomerList(customerList);
            return "You are at PostExecute";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private void setAdapter(ArrayList<CustomerModel> customerNames) {
        progressLayout.setVisibility(View.GONE);
        if (customerList.size() > 0) {
            rootLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        } else {
            rootLayout.setVisibility(View.GONE);
            emptyLayout.setEnabled(false);
            emptyLayout.setClickable(false);
            emptyLayout.setVisibility(View.VISIBLE);
        }
        customerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        customerNameAdapter = new CustomerNameAdapter(customerNames, (customerId, customerName, pos) -> {
            viewCloseBottomSheet();
            int count = dbHelper.numberOfRows();
            if (count > 0) {
                showProductDeleteAlert(customerId);
            } else {
                selectCustomer.setText(customerName);
                setCustomerDetails(customerId);
            }

        });
        customerView.setAdapter(customerNameAdapter);
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();

    }

    public void showAlertDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CategoriesActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Warning..!");
        builder.setMessage("All Data Will be Cleared are you sure want to back ?");
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            dbHelper.removeAllInvoices();
            dbHelper.removeAllInvoiceItems();
            dbHelper.removeAllReturn();
            Utils.clearCustomerSession(this);
            finish();
        });
        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //  getActivity().getWindow().setBackgroundDrawableResource(R.color.primaryDark);
    }

    public void showProductDeleteAlert(String customerId) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Warning !");
        builder1.setMessage("Products in Cart will be removed..");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dbHelper.removeAllItems();
                        setupBadge();
                        Utils.refreshActionBarMenu(CategoriesActivity.this);
                        try {
                            setCustomerDetails(customerId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        builder1.setNegativeButton(
                "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void setCustomerDetails(String customerId) {
        Utils.setCustomerSession(this, customerId);
        getCustomerDetails(customerId, true, "");
    }


    public void viewCloseBottomSheet() {
        // hideKeyboard();
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        // get the Customer name from the local db
        // customerNameEdittext.setText("");
    }

    public void getCategories() {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "CategoryList";
        // Initialize a new JsonArrayRequest instance
        allCategoriesList = new ArrayList<>();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Categories Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        Log.w("Given_url_catal:",url);

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.w("Response_Category:", response.toString());
                        // Loop through the array elements
                        String statusCode = response.optString("statusCode");
                        JSONArray detailArray = response.optJSONArray("responseData");
                        if (statusCode.equals("1")) {
                            for (int i = 0; i < detailArray.length(); i++) {
                                // Get current json object
                                JSONObject categoryObject = detailArray.getJSONObject(i);
                                // if (categoryObject.optBoolean("IsActive")){
                                AllCategories categories = new AllCategories();
                                //  categories.setCompanyCode(categoryObject.optString("CompanyCode"));
                                categories.setCategoryCode(categoryObject.optString("categoryCode"));
                                categories.setCateGoryGroupName(categoryObject.optString("categoryName"));
                                categories.setDescription(categoryObject.optString("categoryName"));
                                // categories.setDisplayOrder(categoryObject.optString("DisplayOrder"));
                                // categories.setShowOnPos(categoryObject.optBoolean("ShowOnPOS"));
                                // categories.setActive(categoryObject.optBoolean("IsActive"));
                                // categories.setCategoryImage(categoryObject.getString("CategoryImagePath"));
                                allCategoriesList.add(categories);
                                //}
                            }
                            pDialog.dismiss();
                            if (allCategoriesList.size() > 0) {
                                setCatagoriesTabs(allCategoriesList);
                                viewPager.setVisibility(View.VISIBLE);
                                emptyLayout.setVisibility(View.GONE);
                            } else {
                                emptyLayout.setVisibility(View.VISIBLE);
                                viewPager.setVisibility(View.GONE);
                            }
                        } else {
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
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

    private void setCatagoriesTabs(ArrayList<AllCategories> allCategoriesList) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), allCategoriesList.size(), allCategoriesList);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        pDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        View actionView = menuItem.getActionView();
        textCartItemCount = actionView.findViewById(R.id.cart_badge);
        final MenuItem search = menu.findItem(R.id.choose_company);
        final MenuItem locati = menu.findItem(R.id.choose_location);
        search.setVisible(false);
        locati.setVisible(false);

        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
        return true;
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                // onBackPressed();
                break;


        }
        return true;
    }*/

    @Override
    public void onBackPressed() {
        int count = dbHelper.numberOfRowsInInvoice();
        if (count > 0) {
            showAlertDialog();
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            int count = dbHelper.numberOfRowsInInvoice();
            if (count > 0) {
                showAlertDialog();
            } else {
                finish();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupBadge();
        Utils.refreshActionBarMenu(CategoriesActivity.this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem myItem = menu.findItem(R.id.action_total);
        //  myItem.setTitle("Total: 100");
        ArrayList<CartModel> localCart = helper.getAllCartItems();
        double price = 0;
        for (int j = 0; j < localCart.size(); j++) {
            if (localCart.get(j).getCART_COLUMN_NET_PRICE() != null && !localCart.get(j).getCART_COLUMN_NET_PRICE().equals("null")) {
                price += Double.parseDouble(localCart.get(j).getCART_COLUMN_NET_PRICE());
            }
        }
        menu.getItem(0).setTitle("Net Amt: $ " + twoDecimalPoint(price));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home) {
            int count = dbHelper.numberOfRowsInInvoice();
            if (count > 0) {
                showAlertDialog();
            } else {
                finish();
            }
        } else if (id == R.id.action_cart) {
            // setFragment(new SchedulingFragment());

            String a = "1011";

            return true;
        } else if (id == R.id.action_search) {
            Intent intent = new Intent(CategoriesActivity.this, SearchProductActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public static void setupBadge() {
        mCartItemCount = helper.numberOfRows();
        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}