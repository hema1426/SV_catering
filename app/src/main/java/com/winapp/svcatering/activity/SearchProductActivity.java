package com.winapp.svcatering.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.CustomerNameAdapter;
import com.winapp.svcatering.adapter.SelectProductAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.AppUtils;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.HomePageModel;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.google.gson.Gson;

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


public class SearchProductActivity extends NavigationActivity {

    private RecyclerView productRecyclerView;
    EditText productSearchView;
    ArrayList<ProductsModel> products;
    SelectProductAdapter adapter;
    LinearLayout emptyLayout;
    SweetAlertDialog pDialog;
    private DBHelper dbHelper;
    String companyCode;
    public static ArrayList<ProductsModel> productList;
    HashMap<String ,String> user;
    SessionManager session;
    ArrayList<CustomerDetails> customerDetails;
    ProgressDialog dialog;
    TextView noOfProducts;
    String locationCode;
    FloatingActionButton sortButton;
    public static String stockProductView="2";

    public EditText customerNameEdittext;
    public Button btnCancel;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor customerPredEdit;
    public static TextView selectCustomer;
    public ArrayList<CustomerModel> customerList;
    public TextView dateText;
    private View progressLayout;
    private LinearLayout rootLayout;
    public CustomerNameAdapter customerNameAdapter;
    public BottomSheetBehavior behavior;
    public TextView userName;
    private ArrayList<CustomerDetails> allCustomersList;
    private RecyclerView customerView;
    public static TextView textCartItemCount;
    public LinearLayout transLayout;
    public static int mCartItemCount = 0;
    public FloatingActionButton addProductButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_search_product, contentFrameLayout);
        // setContentView(R.layout.activity_dashboard);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Products");
       // setContentView(R.layout.activity_search_product);
        productRecyclerView = findViewById(R.id.searchRecyclerview);
        productSearchView=findViewById(R.id.search_view);
        emptyLayout=findViewById(R.id.empty_layout);
        dbHelper=new DBHelper(this);
        session=new SessionManager(this);
        user=session.getUserDetails();
        dbHelper=new DBHelper(this);
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        noOfProducts=findViewById(R.id.no_of_products);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        sortButton=findViewById(R.id.fab);
        selectCustomer = findViewById(R.id.select_customer);
        dateText = findViewById(R.id.date);
        progressLayout=findViewById(R.id.customer_progress);
        userName = findViewById(R.id.user_name);
        rootLayout=findViewById(R.id.rootLayout);
        sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
        customerView = findViewById(R.id.customerView);
        userName.setText(user.get(SessionManager.KEY_USER_NAME));
        transLayout=findViewById(R.id.transparent_layout);
        addProductButton=findViewById(R.id.add_product);

        customerNameEdittext = findViewById(R.id.customer_search);
        btnCancel = findViewById(R.id.btn_cancel);

        dbHelper=new DBHelper(this);
        customerDetails=dbHelper.getCustomer();

        productList=new ArrayList<>();
       // productList= dbHelper.getAllProducts();
        productList = AppUtils.getProductsList();
        if (productList!=null && productList.size()>0){
            populateProductsData(productList);
        }else {
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("CompanyCode",companyCode);
                jsonObject.put("LocationCode",locationCode);
                //jsonObject.put("CategoryCode",catagoriesId);
                jsonObject.put("PageSize",5000);
                jsonObject.put("PageNo","1");
                getAllProducts(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        Objects.requireNonNull(getSupportActionBar()).setTitle("Search");
  //      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        productRecyclerView.setHasFixedSize(true);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));

       // populateProductsData(ProductsFragment.productList);
        productRecyclerView.setVisibility(View.VISIBLE);

        productRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && addProductButton.getVisibility() == View.VISIBLE) {
                 //   addProductButton.hide();
                } else if (dy < 0 && addProductButton.getVisibility() != View.VISIBLE) {
                  //  addProductButton.show();
                }
            }
        });


        //adding a TextChangedListener
        //to call a method whenever there is some change on the EditText
        productSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
              /*  if (editable.toString().length()==0){
                    productRecyclerView.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.GONE);
                }else {
                    filter(editable.toString());
                }*/

                if (editable.toString().length()>0){
                    filter(editable.toString());
                }else {
                   // populateProductsData(dbHelper.getAllProducts());
                    populateProductsData(AppUtils.getProductsList());
                }
            }
        });


        // Setting the sorting
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View menuItemView = findViewById(R.id.fab);
                PopupMenu popupMenu = new PopupMenu(SearchProductActivity.this, menuItemView);
                popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_all:
                                setProductsDisplay("All Products");
                                return true;
                            case R.id.action_stock:
                                setProductsDisplay("In Stock");
                                return true;
                            case R.id.action_outstock:
                                setProductsDisplay("Out of Stock");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });


        String customerId = sharedPreferences.getString("customerId", "");
        if (customerId != null && !customerId.equals("empty") && !customerId.isEmpty()) {
            customerDetails = dbHelper.getCustomer(customerId);
            selectCustomer.setText(customerDetails.get(0).getCustomerName());
        }else {
            selectCustomer.setText("Select Customer");
        }

        customerList=dbHelper.getAllCustomers();
        if (customerList!=null && customerList.size()>0){
            setAdapter(customerList);
        }else {
           // getCustomers();
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
                        transLayout.setVisibility(View.VISIBLE);
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        customerNameEdittext.setText("");
                        // Then just use the following:
                        transLayout.setVisibility(View.GONE);
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    filterCustomer(cusname);
                }else {
                    setAdapter(customerList);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

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

    public void getCustomers() {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "MasterApi/GetCustomer_All?Requestdata={CompanyCode:" + companyCode + "}";
        // Initialize a new JsonArrayRequest instance
        String Resourc_Name;
        String ResourceID;
        customerList.clear();
        allCustomersList=new ArrayList<>();
        // PartId.add("Select Id");
        Log.w("AllCustomerUrl:",url);
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

                                CustomerModel model = new CustomerModel();
                                model.setCustomerCode(customerObject.optString("CustomerCode"));
                                model.setCustomerName(customerObject.optString("CustomerName"));
                                model.setCustomerAddress(customerObject.optString("Address1"));
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
                            }
                        }
                        HomePageModel.customerList = new ArrayList<>();
                        HomePageModel.customerList.addAll(customerList);
                        setAdapter(customerList);
                        dbHelper.insertCustomerList(customerList);
                        //   dbHelper.insertCustomersDetails(allCustomersList);
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
            viewCloseBottomSheet();
            int count =dbHelper.numberOfRows();
            if (count>0){
                showProductDeleteAlert(customer);
            }else {
                setCustomerDetails(customer);
            }

        });
        customerView.setAdapter(customerNameAdapter);
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

    public void showAlertDialog(){
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(SearchProductActivity.this);
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
                        setupBadge();
                        Utils.refreshActionBarMenu(SearchProductActivity.this);
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

    public void setCustomerDetails(String customerId){
        customerDetails = dbHelper.getCustomer(customerId);
        if (customerDetails.size()> 0){
            selectCustomer.setText(customerDetails.get(0).getCustomerName());
            // Storing the key and its value as the data fetched
            customerPredEdit = sharedPreferences.edit();
            customerPredEdit.putString("customerId", customerId);
            customerPredEdit.apply();
        }else {
            showAlertDialog();
        }
    }

    public void setProductsDisplay(String action){
        try {
            // productsAdapterNew.setWithFooter(false);
            // sortAdapter.resetPosition();
            ArrayList<ProductsModel> filteredProducts = new ArrayList<>();
            //   for (ProductsModel s : selectProductAdapter.getProductsList()) {
            for (ProductsModel s : AppUtils.getProductsList()) {
                if (action.equals("In Stock")){
                    if (s.getStockQty()!=null && !s.getStockQty().equals("null")){
                        if (Double.parseDouble(s.getStockQty())>0){
                            filteredProducts.add(s);
                        }
                    }
                }else if (action.equals("Out of Stock")){
                    if (s.getStockQty()!=null && !s.getStockQty().equals("null")) {
                        if (Double.parseDouble(s.getStockQty()) < 0 || Double.parseDouble(s.getStockQty()) == 0) {
                            filteredProducts.add(s);
                        }
                    }
                }else {
                    filteredProducts.add(s);
                }
            }

            // Setting filter option in Localdb
            if (action.equals("In Stock")){
                dbHelper.insertSettings("stock_view","1");
            }else if (action.equals("Out of Stock")){
                dbHelper.insertSettings("stock_view","0");
            }else {
                dbHelper.insertSettings("stock_view","2");
            }
            //calling a method of the adapter class and passing the filtered list
            if (filteredProducts.size()>0){
                for (ProductsModel model: filteredProducts){
                    Log.w("products_Option_values:",model.getProductName());
                }
                if (adapter!=null){
                    adapter.filterList(filteredProducts);
                }
                adapter.filterList(filteredProducts);
               // productLayout.setVisibility(View.VISIBLE);
                noOfProducts.setText(filteredProducts.size()+" Products");
                emptyLayout.setVisibility(View.GONE);
            }else {
                //productLayout.setVisibility(View.GONE);
                noOfProducts.setText(filteredProducts.size()+" Products");
                emptyLayout.setVisibility(View.VISIBLE);
            }
        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
           // FirebaseCrashlytics.getInstance().recordException(ex);
        }
    }

    private void filterCustomer(String text) {
        try {
            //new array list that will hold the filtered data
            ArrayList<CustomerModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (CustomerModel s : customerList) {
                //if the existing elements contains the search input
                if (s.getCustomerName().toLowerCase().contains(text.toLowerCase()) || s.getCustomerCode().toLowerCase().contains(text.toLowerCase())) {
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



    private void filter(String text) {
        try {
            //new array list that will hold the filtered data
            ArrayList<ProductsModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (ProductsModel s : adapter.getProductsList()) {
                //if the existing elements contains the search input
                if (s.getProductName().toLowerCase().contains(text.toLowerCase())) {
                    //adding the element to filtered list
                    filterdNames.add(s);
                }
            }
            //calling a method of the adapter class and passing the filtered list
            if (filterdNames.size()>0){
                adapter.filterList(filterdNames);
                productRecyclerView.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
            }else {
                emptyLayout.setVisibility(View.VISIBLE);
                productRecyclerView.setVisibility(View.GONE);
            }
            noOfProducts.setText(filterdNames.size()+" Products");
        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }


    private void populateProductsData(ArrayList<ProductsModel> productsList) {
        try {

            // Get the Settings to Display the Product list with the Settings
            ArrayList<SettingsModel> settings=dbHelper.getSettings();
            if (settings.size()>0) {
                for (SettingsModel model : settings) {
                    if (model.getSettingName().equals("stock_view")) {
                        if (model.getSettingValue().equals("1")){
                            stockProductView="1";
                        }else if (model.getSettingValue().equals("2")){
                            stockProductView="2";
                        }else {
                            stockProductView="0";
                        }
                    }
                }
            }
            // Filter the products list depending the Settings
            ArrayList<ProductsModel> filteredProducts = new ArrayList<>();
            //   for (ProductsModel s : selectProductAdapter.getProductsList()) {
            for (ProductsModel product : productsList) {
                if (stockProductView.equals("1")){
                    if (product.getStockQty()!=null && !product.getStockQty().equals("null")){
                        if (Double.parseDouble(product.getStockQty())>0){
                            filteredProducts.add(product);
                        }
                    }
                }else if (stockProductView.equals("0")){
                    if (product.getStockQty()!=null && !product.getStockQty().equals("null")) {
                        if (Double.parseDouble(product.getStockQty()) < 0 || Double.parseDouble(product.getStockQty()) == 0) {
                            filteredProducts.add(product);
                        }
                    }
                }else {
                    filteredProducts.add(product);
                }
            }


            if (filteredProducts.size()>0){
                emptyLayout.setVisibility(View.GONE);
                productRecyclerView.setVisibility(View.VISIBLE);
                noOfProducts.setText(productsList.size()+" Products");
                adapter = new SelectProductAdapter(this,filteredProducts, new SelectProductAdapter.CallBack() {
                    @Override
                    public void searchProduct(ProductsModel model) {
                        SharedPreferences sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
                        SharedPreferences.Editor customerPredEdit= sharedPreferences.edit();
                        String selectCustomerId = sharedPreferences.getString("customerId", "");
                        customerDetails=new ArrayList<>();
                        if (selectCustomerId!=null && !selectCustomerId.isEmpty()){
                            customerDetails=dbHelper.getCustomer(selectCustomerId);
                        }
                       /* if (customerDetails!=null && customerDetails.size()>0){
                            productSearchView.setText("");
                            Gson gson = new Gson();
                            String object = gson.toJson(model);
                            Intent intent = new Intent(SearchProductActivity.this, DescriptionActivity.class);
                            intent.putExtra("productDetails", object);
                            startActivity(intent);
                        }else {
                            showAlert();
                        }*/
                        Gson gson = new Gson();
                        String object = gson.toJson(model);
                        Intent intent = new Intent(SearchProductActivity.this, DescriptionActivity.class);
                        intent.putExtra("productDetails", object);
                        startActivity(intent);
                    }
                });
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
                productRecyclerView.setLayoutManager(mLayoutManager);
                // productRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, GridSpacingItemDecoration.dpToPx(this,10), true));
                productRecyclerView.setItemAnimator(new DefaultItemAnimator());
                noOfProducts.setText(filteredProducts.size()+" Products");
                productRecyclerView.setAdapter(adapter);
            }else {
                noOfProducts.setText(productsList.size()+" Products");
                emptyLayout.setVisibility(View.VISIBLE);
                productRecyclerView.setVisibility(View.GONE);
            }

        }catch (Exception ex){
            Log.e("TAG","Error in Populating the data:"+ex.getMessage());
        }
    }

    public void showAlert(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Warning !")
                .setContentText("Please Choose your Customer!")
                .setConfirmText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }


    @Override
    public void onBackPressed() {
        //Execute your code here
        finish();
    }

    public void getAllProducts(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(SearchProductActivity.this);
        String url=Utils.getBaseUrl(this) +"ProductList";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_pdt_url:",url);
        productList=new ArrayList<>();
        products=new ArrayList<>();
        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Getting Products List...");
        dialog.show();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        dialog.dismiss();
                      //  {"productCode":"FG\/001245","productName":"RUM","companyName":"","supplierCategoryNumber":"","uomCode":"Ctn",
                        //  "uomName":"Carton","cartonPrice":"3000.000000","piecePrice":"0.000000","pcsPerCarton":"100.000000",
                        //  "price":"100.000000","taxType":"E","havTax":"Y","taxCode":"SR","taxRate":"7.000000","barCode":"",
                        //  "isActive":"N","createUser":"1","createDate":"13\/07\/2021","modifyDate":"29\/07\/2021","remarks":"",
                        //  "warehouse":"01","stockInHand":"0.000000","averagePrice":0,"manageBatchOrSerial":"None","manageBatchNumber":"N",
                        //  "manageSerialNumber":"N","batchNumber":"","expiryDate":null,"manufactureDate":"31\/07\/2021","imageURL":""}
                        Log.w("Response_SAP_PRODUCTS:",response.toString());
                        // Loop through the array elements
                        JSONArray productArray=response.optJSONArray("responseData");
                        for(int i=0;i<productArray.length();i++){
                            // Get current json object
                            JSONObject productObject = productArray.getJSONObject(i);
                            ProductsModel product =new ProductsModel();
                            if (productObject.optString("isActive").equals("N")) {
                                product.setCompanyCode("1");
                                product.setProductName(productObject.optString("productName"));
                                product.setProductCode(productObject.optString("productCode"));
                                product.setWeight("");
                                product.setProductImage(productObject.optString("imageURL"));
                                product.setWholeSalePrice("0.00");
                                product.setRetailPrice(productObject.optDouble("retailPrice"));
                                product.setCartonPrice(productObject.optString("cartonPrice"));
                                product.setPcsPerCarton(productObject.optString("pcsPerCarton"));
                                product.setUnitCost(productObject.optString("price"));
                                if (!productObject.optString("stockInHand").equals("null")){
                                    product.setStockQty(productObject.optString("stockInHand"));
                                }else {
                                    product.setStockQty("0");
                                }

                                product.setUomCode(productObject.optString("uomCode"));
                                //  product.setProductBarcode(productObject.optString("BarCode")); Add values In Futue
                                product.setProductBarcode("");
                                productList.add(product);
                            }
                        }
                        HomePageModel.productsList=new ArrayList<>();
                        HomePageModel.productsList.addAll(productList);
                        if (productList.size()>0){
                            populateProductsData(productList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtils.setProductsList(productList);
                                    dialog.dismiss();
                                    // dbHelper.insertProducts(getActivity(),productList);
                                }
                            });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        View actionView = menuItem.getActionView();
        textCartItemCount = actionView.findViewById(R.id.cart_badge);
        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });
        return true;
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

    @Override
    protected void onResume() {
        super.onResume();
        mCartItemCount = helper.numberOfRows();
        setupBadge();
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
        Utils.refreshActionBarMenu(SearchProductActivity.this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        ArrayList<CartModel> localCart = helper.getAllCartItems();
        double price = 0;
        for (int j = 0; j < localCart.size(); j++) {
            if (localCart.get(j).getCART_COLUMN_NET_PRICE()!=null && !localCart.get(j).getCART_COLUMN_NET_PRICE().equals("null")){
                price += Double.parseDouble(localCart.get(j).getCART_COLUMN_NET_PRICE());
            }
        }
        menu.getItem(0).setTitle("Net Amt: $ " + twoDecimalPoint(price));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //finish();
            onBackPressed();
        }else if (item.getItemId()==R.id.action_add){
            Toast.makeText(getApplicationContext(),"Product Add module in progress",Toast.LENGTH_LONG).show();
        }
        return true;
    }

}