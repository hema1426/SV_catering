package com.winapp.svcatering.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.CustomerNameAdapter;
import com.winapp.svcatering.adapter.SelectProductAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.fragments.BrandFragment;
import com.winapp.svcatering.fragments.CategoriesFragment;
import com.winapp.svcatering.fragments.DepartmentFragment;
import com.winapp.svcatering.fragments.ProductsFragment;
import com.winapp.svcatering.model.AppUtils;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.HomePageModel;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.HomeViewPager;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.winapp.svcatering.activity.AddInvoiceActivityOld.activityFrom;
import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;


public class MainHomeActivity extends NavigationActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static TextView textCartTotal;
    public NavigationView mNavigationView;
    private int mCurrentSelectedPosition = 0;
    public static TextView textCartItemCount;
    public static int mCartItemCount = 0;
    public static String customerId = "empty";
    public static DBHelper helper;
    public SessionManager session;
    public HashMap<String, String> user;
    public String username;
    public String companyname;
    private long lastBackPressTime = 0;
    private Toolbar toolbar;
    private static TabLayout tabLayout;
   // public static ViewPager viewPager;
    public static HomeViewPager viewPager;
    private RecyclerView lettersRecyclerview;
    private RecyclerView customerView;
    public CompanyChooseAdapter adapter;
    public ArrayList<String> letters;
    private SearchableSpinner PartSpinner;
    public ArrayList<CustomerModel> customerList;
    public List<String> PartId;
    public static TextView selectCustomer;
    public TextView customerName;
    public BottomSheetBehavior behavior;
    public EditText customerNameEdittext;
    public Button btnCancel;
    public CustomerNameAdapter customerNameAdapter;
    public TextView dateText;
    public TextView userName;
    public static String companyCode;
    public SweetAlertDialog pDialog;
    public DBHelper dbHelper;
    // Customer Array List
    public ArrayList<CustomerDetails> customerDetails;
    private ArrayList<CustomerDetails> allCustomersList;
    public ArrayList<ProductsModel> productList;
    private SelectProductAdapter selectProductAdapter;
    public static LinearLayout transLayout;
    public static LinearLayout customerTransLayout;
    public static LinearLayout tabTransLayout;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    public String locationCode;
    private View progressLayout;
    private LinearLayout rootLayout;
    private LinearLayout emptyLayout;
    public ArrayList<MultipleCompanyModel> companyList;
    public static boolean isLoadingFirstTime=true;
    public static String newSelectedCompany="0";
    public static String newSelectedCompanyName="";
    AlertDialog dialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor customerPredEdit;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_main_home, contentFrameLayout);
        // setContentView(R.layout.activity_dashboard);
       // Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ezyERP");
        // Storing data into SharedPreferences
        sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
       // Creating an Editor object to edit(write to the file
        if (!Utils.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        helper = new DBHelper(this);
        session = new SessionManager(this);
        user = session.getUserDetails();
        viewPager = findViewById(R.id.viewpager);
        dbHelper = new DBHelper(this);

        tabLayout = findViewById(R.id.tabs);
        lettersRecyclerview = findViewById(R.id.sorting_letters);
        PartSpinner = (SearchableSpinner) findViewById(R.id.spinnerPart);
        selectCustomer = findViewById(R.id.select_customer);
        customerView = findViewById(R.id.customerView);
        customerName = findViewById(R.id.customer_name_value);
        customerNameEdittext = findViewById(R.id.customer_search);
        btnCancel = findViewById(R.id.btn_cancel);
        dateText = findViewById(R.id.date);
        userName = findViewById(R.id.user_name);
        tabLayout.setupWithViewPager(viewPager);
        customerList = new ArrayList<>();
        PartId = new ArrayList<>();
        productList = new ArrayList<>();
        //productList = dbHelper.getAllProducts();
        userName.setText(user.get(SessionManager.KEY_USER_NAME));
        companyCode = user.get(SessionManager.KEY_COMPANY_CODE);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        transLayout=findViewById(R.id.trans_layout);
        customerTransLayout=findViewById(R.id.customer_translayout);
        tabTransLayout=findViewById(R.id.tab_translayout);
        progressLayout=findViewById(R.id.customer_progress);
        rootLayout=findViewById(R.id.rootLayout);
        emptyLayout=findViewById(R.id.empty_layout);
        setupViewPager(viewPager);

        String customerId = sharedPreferences.getString("customerId", "");
        if (customerId != null && !customerId.equals("empty") && !customerId.isEmpty()) {
            customerDetails = dbHelper.getCustomer(customerId);
            selectCustomer.setText(customerDetails.get(0).getCustomerName());
        }else {
            selectCustomer.setText("Select Customer");
        }

      /*  if (MainHomeActivity.customerId != null && !MainHomeActivity.customerId.equals("empty")) {
            try {
               // getCustomerDetails(MainHomeActivity.customerId);
                customerDetails = dbHelper.getCustomer(MainHomeActivity.customerId);
                selectCustomer.setText(customerDetails.get(0).getCustomerName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (customerId != null && !customerId.equals("empty") && !customerId.isEmpty()){
            customerDetails = dbHelper.getCustomer(customerId);
            selectCustomer.setText(customerDetails.get(0).getCustomerName());
        }*/

        /*else {
            if (dbHelper != null) {
                customerDetails = dbHelper.getCustomer();
            }
            assert dbHelper != null;
            if (dbHelper.getCustomer() != null && customerDetails.size() > 0) {
                selectCustomer.setText(customerDetails.get(0).getCustomerName());
            }
        }*/
     //   if (productList != null && productList.size() == 0) {
        //   getAllProducts("1");
      //  }

        customerList=dbHelper.getAllCustomers();
        if (customerList!=null && customerList.size()>0){
            setAdapter(customerList);
        }else {
            try {
               // getCustomers();
                new GetCustomerTask().execute();
            }catch (Exception ex){}
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
                        hideKeyboard();
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        hideKeyboard();
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
                viewCloseBottomSheet();
            }
        });

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, -20, 0);
            tab.requestLayout();
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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        if (getIntent().getStringExtra("customer_name") != null) {
            customerId = getIntent().getStringExtra("customer_id");
//            HomeFragment.selectCustomer.setText(customerName);
        }

        mCartItemCount = helper.numberOfRows();
        setupBadge(this);

        //ArrayList<ProductsModel> products=dbHelper.getAllProducts();
     /*   ArrayList<ProductsModel> products=AppUtils.getProductsList();
        if (products!=null && products.size()==0){
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("CompanyCode",companyCode);
                jsonObject.put("LocationCode",locationCode);
                jsonObject.put("PageSize",5000);
                jsonObject.put("PageNo","1");
                getAllProducts(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/

        if (activityFrom!=null){
            if (activityFrom.equals("InvoiceEdit")
                    || activityFrom.equals("SalesEdit")
                    || activityFrom.equals("ConvertInvoice")
                    || activityFrom.equals("ReOrderInvoice")
                    || activityFrom.equals("ReOrderSales")
            ){
                dbHelper.removeAllItems();
                Utils.clearCustomerSession(this);
            }
        }

    }

   /* public void clearAllData(){
        if (AddInvoiceActivity.activityFrom.equals("InvoiceEdit")
                || activityFrom.equals("SalesEdit")
                || activityFrom.equals("ConvertInvoice")
                || activityFrom.equals("ReOrderInvoice")
                || activityFrom.equals("ReOrderSales")
        )
    }
*/
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProductsFragment(), "PRODUCTS");
        adapter.addFragment(new DepartmentFragment(), "DEPARTMENT");
        adapter.addFragment(new CategoriesFragment(), "CATEGORIES");
        adapter.addFragment(new BrandFragment(), "BRANDS");
        //  adapter.addFragment(new CategoriesFragment(),"CATALOG");
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (ProductsFragment.behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    ProductsFragment.behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }/*else if (ShowCatagoryActivity.behavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    ShowCatagoryActivity.behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }*/
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public static void disableTab(boolean check){
        try {
            for (int i = 0; i < 4; i++){
                Objects.requireNonNull(tabLayout.getTabAt(i)).view.setEnabled(check);
            }
            if (check){
                viewPager.disableScroll(false);
            }else {
                viewPager.disableScroll(true);
            }

          //  Also don't forget to call viewPager.invalidate() method after calling disable scrolling method,
            //  because of that it instantly disable swipe over.
            //viewPager.invalidate();

        }catch (Exception ex){
            Log.w("Disble_movingError:", Objects.requireNonNull(ex.getMessage()));
        }

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private class GetCustomerTask extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();
        protected void onPreExecute() {
            customerList.clear();
            allCustomersList=new ArrayList<>();
            PartId.clear();
            super.onPreExecute();
        }
        protected String doInBackground(Void...arg0) {
            RequestQueue requestQueue = Volley.newRequestQueue(MainHomeActivity.this);
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("CompanyCode",companyCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = Utils.getBaseUrl(MainHomeActivity.this) + "MasterApi/GetCustomer_All?Requestdata="+jsonObject.toString();
            // Initialize a new JsonArrayRequest instance
            String Resourc_Name;
            String ResourceID;
            // PartId.add("Select Id");
            Log.w("AllCustomerUrl:",url);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
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

                                    CustomerModel model = new CustomerModel();
                                    model.setCustomerCode(customerObject.optString("CustomerCode"));
                                    model.setCustomerName(customerObject.optString("CustomerName"));
                                    model.setAddress1(customerObject.optString("Address1"));
                                    model.setHaveTax(customerObject.optString("HaveTax"));
                                    model.setTaxType(customerObject.optString("TaxType"));
                                    model.setTaxPerc(customerObject.optString("TaxPerc"));
                                    model.setTaxCode(customerObject.optString("TaxCode"));
                                    if (customerObject.optString("BalanceAmount").equals("null") || customerObject.optString("BalanceAmount").isEmpty()){
                                        model.setOutstandingAmount("0.00");
                                    }else {
                                        model.setOutstandingAmount(customerObject.optString("BalanceAmount"));
                                    }
                                    customerList.add(model);

                                /*CustomerDetails details=new CustomerDetails();
                                details.setCustomerCode(customerObject.optString("CustomerCode"));
                                details.setCustomerName(customerObject.optString("CustomerName"));
                                details.setPhoneNo(customerObject.optString("PhoneNo"));
                                details.setCustomerAddress1(customerObject.optString("Address1"));
                                details.setCustomerAddress2(customerObject.optString("Address2"));
                                details.setCustomerAddress3(customerObject.optString("Address3"));
                                details.setIsActive(customerObject.optString("IsActive"));
                                details.setHaveTax(customerObject.optString("HaveTax"));
                                details.setTaxType(customerObject.optString("TaxType"));
                                details.setTaxPerc(customerObject.optString("TaxPerc"));
                                details.setTaxCode(customerObject.optString("TaxCode"));
                                details.setCreditLimit(customerObject.optString("CreditLimit"));
                                details.setCountry(customerObject.optString("Country"));
                                details.setCurrencyCode(customerObject.optString("CurrencyCode"));
                                details.setBalanceAmount(customerObject.optString("BalanceAmount"));

                                allCustomersList.add(details);*/
                                }
                            }
                            //  dbHelper.insertCustomersDetails(allCustomersList);
                            //populateCategoriesData();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
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
            return "You are at PostExecute";
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            HomePageModel.customerList = new ArrayList<>();
            HomePageModel.customerList.addAll(customerList);
            dbHelper.removeAllCustomersDetails();
            dbHelper.removeAllCustomers();
            setAdapter(customerList);
            dbHelper.insertCustomerList(customerList);
        }
    }

    public void getCustomers() throws JSONException {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyCode);
        String url = Utils.getBaseUrl(this) + "MasterApi/GetCustomer_All?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        String Resourc_Name;
        String ResourceID;
        customerList.clear();
        allCustomersList=new ArrayList<>();
        PartId.clear();
        // PartId.add("Select Id");
        Log.w("AllCustomerUrl:",url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
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

                                CustomerModel model = new CustomerModel();
                                model.setCustomerCode(customerObject.optString("CustomerCode"));
                                model.setCustomerName(customerObject.optString("CustomerName"));
                                model.setAddress1(customerObject.optString("Address1"));
                                model.setHaveTax(customerObject.optString("HaveTax"));
                                model.setTaxType(customerObject.optString("TaxType"));
                                model.setTaxPerc(customerObject.optString("TaxPerc"));
                                model.setTaxCode(customerObject.optString("TaxCode"));
                                if (customerObject.optString("BalanceAmount").equals("null") || customerObject.optString("BalanceAmount").isEmpty()){
                                    model.setOutstandingAmount("0.00");
                                }else {
                                    model.setOutstandingAmount(customerObject.optString("BalanceAmount"));
                                }
                                customerList.add(model);

                                /*CustomerDetails details=new CustomerDetails();
                                details.setCustomerCode(customerObject.optString("CustomerCode"));
                                details.setCustomerName(customerObject.optString("CustomerName"));
                                details.setPhoneNo(customerObject.optString("PhoneNo"));
                                details.setCustomerAddress1(customerObject.optString("Address1"));
                                details.setCustomerAddress2(customerObject.optString("Address2"));
                                details.setCustomerAddress3(customerObject.optString("Address3"));
                                details.setIsActive(customerObject.optString("IsActive"));
                                details.setHaveTax(customerObject.optString("HaveTax"));
                                details.setTaxType(customerObject.optString("TaxType"));
                                details.setTaxPerc(customerObject.optString("TaxPerc"));
                                details.setTaxCode(customerObject.optString("TaxCode"));
                                details.setCreditLimit(customerObject.optString("CreditLimit"));
                                details.setCountry(customerObject.optString("Country"));
                                details.setCurrencyCode(customerObject.optString("CurrencyCode"));
                                details.setBalanceAmount(customerObject.optString("BalanceAmount"));

                                allCustomersList.add(details);*/
                            }
                        }
                        HomePageModel.customerList = new ArrayList<>();
                        HomePageModel.customerList.addAll(customerList);
                        dbHelper.removeAllCustomersDetails();
                        dbHelper.removeAllCustomers();
                        setAdapter(customerList);
                        dbHelper.insertCustomerList(customerList);
                      //  dbHelper.insertCustomersDetails(allCustomersList);
                        //populateCategoriesData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
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

    private void setAdapter(ArrayList<CustomerModel> customerNames) {
        progressLayout.setVisibility(View.GONE);
        if (customerList.size()> 0) {
            rootLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }else {
            rootLayout.setVisibility(View.GONE);
            emptyLayout.setEnabled(false);
            emptyLayout.setClickable(false);
            emptyLayout.setVisibility(View.VISIBLE);
        }
        customerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
         customerNameAdapter = new CustomerNameAdapter(customerNames, (customer,customerName, pos) -> {
            hideKeyboard();
            int count =dbHelper.numberOfRows();
            if (count>0){
                showProductDeleteAlert(customer);
            }else {
                setCustomerDetails(customer);
            }
           // getCustomerDetails(customer);
        });
        customerView.setAdapter(customerNameAdapter);
    }

    public void setCustomerDetails(String customerId){
        customerDetails = dbHelper.getCustomer(customerId);
        if (customerDetails.size()> 0){
            selectCustomer.setText(customerDetails.get(0).getCustomerName());
            // Storing the key and its value as the data fetched
            customerPredEdit = sharedPreferences.edit();
            customerPredEdit.putString("customerId", customerId);
            customerPredEdit.apply();
            closeBottomSheet();
        }else {
            viewCloseBottomSheet();
            showAlertDialog();
        }
    }

       public void showProductDeleteAlert(String customerId){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Warning !");
        builder1.setMessage("Products in Cart will be removed..");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dbHelper.removeAllItems();
                        setupBadge(MainHomeActivity.this);
                        Utils.refreshActionBarMenu(MainHomeActivity.this);
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


    public void showAlertDialog(){
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(MainHomeActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Warning..!");
        builder.setMessage("Customer Information is not valid,Please try again..");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                startActivity(getIntent());
                finish();
                dialogInterface.dismiss();
            }
        });
        android.app.AlertDialog alertDialog=builder.create();
        alertDialog.show();
        //  getActivity().getWindow().setBackgroundDrawableResource(R.color.primaryDark);
    }

    public void closeBottomSheet() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        setAdapter(customerList);
        // get the Customer name from the local db
        customerNameEdittext.setText("");
      //  hideKeyboard();
    }

    public void viewCloseBottomSheet() {
        hideKeyboard();
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        setAdapter(customerList);
        // get the Customer name from the local db
        customerNameEdittext.setText("");
    }

    public void hideKeyboard() {
        try {
            closeBottomSheet();
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void filter(String text) {
        try {
            //new array list that will hold the filtered data
            ArrayList<CustomerModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (CustomerModel s : customerList) {
                //if the existing elements contains the search input
                    if (s.getCustomerName().toLowerCase().contains(text.toLowerCase()) ||
                        s.getCustomerCode().toLowerCase().contains(text.toLowerCase())
                    ) {
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

    public void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

  /*  public boolean onPrepareOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
       // TextView title  = (TextView) findViewById(R.id.title);
        menu.getItem(0).setTitle("Welcome to the Menu");
      //  menu.getItem(1).setTitle(getString(R.string.payFor) + "...");
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        View actionView = menuItem.getActionView();
        textCartItemCount = actionView.findViewById(R.id.cart_badge);
        setupBadge(this);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductsFragment.closeSheet();
                onOptionsItemSelected(menuItem);
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            // setFragment(new SchedulingFragment());

            String a = "1011";

            return true;
        } else if (id == R.id.action_search) {
            ProductsFragment.closeSheet();
            Intent intent=new Intent(MainHomeActivity.this,SearchProductActivity.class);
            startActivity(intent);
        }else if (id==R.id.choose_company){
            ArrayList<MainHomeActivity.MultipleCompanyModel> companyList=dbHelper.getAllCompanies();
            if (companyList.size() > 0) {
                showCompanyDialog();
            }else {
                Toast.makeText(getApplicationContext(),"No Company Found",Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

   /* private void chooseCompanyDialog() {
        ArrayList<String> companies=new ArrayList<>();
        ArrayList<CompanyModel> companyList=dbHelper.getAllCompanies();
        for (CompanyModel model:companyList){
            companies.add(model.getCompanyName());
        }
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainHomeActivity.this);
        mBuilder.setTitle("Choose a  Company");
        mBuilder.setSingleChoiceItems(companies.toArray(new String[companies.size()]), 0, (dialogInterface, i) -> {
            // mResult.setText(listItems[i]);
            dialogInterface.dismiss();
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }*/

    private void displaySelectedScreen(int itemId) {
        //initializing the fragment object which is selected
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem myItem = menu.findItem(R.id.action_total);
        ArrayList<CartModel> localCart = dbHelper.getAllCartItems();
        double net_amount = 0.00;
        for (int j = 0; j < localCart.size(); j++) {
            if (localCart.get(j).getCART_COLUMN_NET_PRICE()!=null && !localCart.get(j).getCART_COLUMN_NET_PRICE().isEmpty() && !localCart.get(j).getCART_COLUMN_NET_PRICE().equals("null")){
                net_amount += Double.parseDouble(localCart.get(j).getCART_COLUMN_NET_PRICE());
            }
        }
        menu.getItem(0).setTitle("Net Amt: $ " + twoDecimalPoint(net_amount));
        return true;
    }
   /* // Replace the fragments
    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(content_frame, fragment);
        fragmentTransaction.commit();
    }*/

    public static void setupBadge(Context context) {
        helper = new DBHelper(context);
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


    public void getAllProducts(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url=Utils.getBaseUrl(this) +"ProductApi/GetProduct_All_ByFacets?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_product_url:",url+" .."+jsonObject);
        ArrayList<ProductsModel> productList=new ArrayList<>();
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
                                product.setWholeSalePrice(productObject.optString("WholeSalePrice"));
                                product.setRetailPrice(productObject.optDouble("RetailPrice"));
                                product.setCartonPrice(productObject.optString("CartonPrice"));
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
                        if (productList.size()>0){
                          //  dbHelper.insertProducts(this,productList);
                            AppUtils.setProductsList(productList);
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


    @Override
    public void onBackPressed() {
        if (ProductsFragment.behavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            ProductsFragment.closeSheet();
        }else {
            finish();
        }
      //  ProductsFragment.closeSheet();
      //  super.onBackPressed();
      //  Intent intent=new Intent(MainHomeActivity.this,DashboardActivity.class);
      //  startActivity(intent);
       // finish();
      //  DrawerLayout drawer = findViewById(R.id.drawer_layout);
       /* if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
      /*  if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            Snackbar snackbar = Snackbar
                    .make(drawer, "Click BACK again to exit", Snackbar.LENGTH_LONG);
            snackbar.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            finishAffinity();
            android.os.Process.killProcess(android.os.Process.myPid());
        }*/
    }

    // ((FragmentName) getFragmentManager().findFragmentById(R.id.fragment_id)).methodName();

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("Daiya", "ORIENTATION_LANDSCAPE");
            ProductsFragment.closeSheet();

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("Daiya", "ORIENTATION_PORTRAIT");
            ProductsFragment.closeSheet();
        }
    }


    private void showCompanyDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ProductsFragment.closeSheet();
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.multiple_company_layout, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        final RecyclerView companyListView=dialogView.findViewById(R.id.companyListView);
        ArrayList<MainHomeActivity.MultipleCompanyModel> companyList=dbHelper.getAllCompanies();
        companyListView.setHasFixedSize(true);

        int index=0;
        for (MainHomeActivity.MultipleCompanyModel model:companyList){
            if (model.getCompanyId().equals(companyCode)){
                MultipleCompanyModel values=companyList.get(index);
                MultipleCompanyModel values1=companyList.get(0);
                companyList.set(0,values);
                companyList.set(index,values1);
                break;
            }
            index++;
        }
        // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        companyListView.setLayoutManager(new LinearLayoutManager(MainHomeActivity.this, LinearLayoutManager.VERTICAL, false));
        CompanyChooseAdapter companyAdapter = new CompanyChooseAdapter(companyList,companyCode, new CompanyChooseAdapter.CallBack() {
            @Override
            public void sortProduct(String letter) {

            }
        });
        companyListView.setAdapter(companyAdapter);

        Button yesButton=dialogView.findViewById(R.id.buttonYes);
        Button noButton=dialogView.findViewById(R.id.buttonNo);

        //finally creating the alert dialog and displaying it
        dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.setCancelable(false);
        dialog.show();
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newSelectedCompany.equals("0")){
                    showConfirmDialog(newSelectedCompanyName);
                }else {
                    Toast.makeText(getApplicationContext(),"Please select the Company",Toast.LENGTH_SHORT).show();
                }
               // dialog.dismiss();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                newSelectedCompany="0";
            }
        });
    }

    public void showConfirmDialog(String companyname){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Information");
        builder.setMessage("Are you sure want to Switch over "+companyname+" ?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
                dialogInterface.dismiss();
                Intent intent=new Intent(MainHomeActivity.this,NewCompanySwitchActivity.class);
                intent.putExtra("from","Home");
                intent.putExtra("companyCode",newSelectedCompany);
                intent.putExtra("locationCode",locationCode);
                intent.putExtra("companyName",newSelectedCompanyName);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    public static class MultipleCompanyModel{
        public String companyName;
        public String companyId;
        public boolean isCompanySelected;
        public boolean isActive;

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public boolean isCompanySelected() {
            return isCompanySelected;
        }

        public void setCompanySelected(boolean companySelected) {
            isCompanySelected = companySelected;
        }
    }

    public static class CompanyChooseAdapter extends RecyclerView.Adapter<CompanyChooseAdapter.ViewHolder> {
        private ArrayList<MultipleCompanyModel> companies;
        public CallBack callBack;
        public String companyCode;
        private int selectedPosition = -1;// no selection by default

        public CompanyChooseAdapter(ArrayList<MultipleCompanyModel> companies,String companyCode, CompanyChooseAdapter.CallBack callBack) {
            this.companies = companies;
            this.callBack=callBack;
            this.companyCode=companyCode;
        }
        @Override
        public CompanyChooseAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.company_list_item, viewGroup, false);
            return new CompanyChooseAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CompanyChooseAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
            MultipleCompanyModel model=companies.get(position);
            viewHolder.companyName.setText(model.getCompanyName());
            if (companyCode.equals(companies.get(position).getCompanyId())){
                viewHolder.selected.setVisibility(View.VISIBLE);
                viewHolder.companyCheck.setVisibility(View.GONE);
            }else {
                viewHolder.companyCheck.setVisibility(View.VISIBLE);
            }

            if (selectedPosition == position) {
                viewHolder.itemView.setSelected(true);
                viewHolder.companyCheck.setChecked(true);
                viewHolder.title.setVisibility(View.VISIBLE);
                //viewHolder.selectLayout.setBackgroundResource(R.drawable.circle);
                //viewHolder.tv_letters.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                viewHolder.itemView.setSelected(false);
                viewHolder.companyCheck.setChecked(false);
                viewHolder.title.setVisibility(View.GONE);
               // viewHolder.selectLayout.setBackgroundResource(R.drawable.circle_not_selected);
              //  viewHolder.tv_letters.setTextColor(Color.parseColor("#212121"));
            }
            viewHolder.companyCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedPosition >= 0)
                        notifyItemChanged(selectedPosition);
                    selectedPosition = viewHolder.getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                    newSelectedCompany=model.getCompanyId();
                    newSelectedCompanyName=model.getCompanyName();
                   // callBack.sortProduct(viewHolder.tv_letters.getText().toString());
                }
            });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position!=0){
                        if (selectedPosition >= 0)
                            notifyItemChanged(selectedPosition);
                        selectedPosition = viewHolder.getAdapterPosition();
                        notifyItemChanged(selectedPosition);
                        newSelectedCompany=model.getCompanyId();
                        newSelectedCompanyName=model.getCompanyName();
                        // callBack.sortProduct(viewHolder.tv_letters.getText().toString());
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return companies.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView companyName;
            private CheckBox companyCheck;
            private TextView title;
            private TextView selected;
          //  private LinearLayout selectLayout;
            public ViewHolder(View view) {
                super(view);
                companyName = view.findViewById(R.id.company_name);
                companyCheck=view.findViewById(R.id.company_check);
                title=view.findViewById(R.id.title);
                selected=view.findViewById(R.id.selected);
               // selectLayout=view.findViewById(R.id.select_layout);
            }
        }
        public interface CallBack{
            void sortProduct(String letter);
        }
        public void resetPosition(){
            selectedPosition=-1;
            notifyDataSetChanged();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        ProductsFragment.closeSheet();
        mCartItemCount = helper.numberOfRows();
        setupBadge(this);
        customerList=new ArrayList<>();
        customerList=dbHelper.getAllCustomers();
        if (customerList!=null && customerList.size()>0){
            setAdapter(customerList);
        }
        assert dbHelper != null;
        String customerId = sharedPreferences.getString("customerId", "");
        Log.w("GivenCustomerId:",customerId);
        if (customerId!=null && !customerId.isEmpty() && !customerId.equals("empty")){
            customerDetails=dbHelper.getCustomer(customerId);
            if (dbHelper.getCustomer(customerId) != null && customerDetails.size() > 0) {
                selectCustomer.setText(customerDetails.get(0).getCustomerName());
            }else {
                selectCustomer.setText("Select Customer");
            }
        }else {
            selectCustomer.setText("Select Customer");
        }
        Utils.refreshActionBarMenu(MainHomeActivity.this);
    }
}