package com.winapp.svcatering.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.ProductStockAnalysisAdapter;
import com.winapp.svcatering.adapter.SelectCustomerAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.ProductStockAnalysisModel;
import com.winapp.svcatering.model.SalesOrderPrintPreviewModel;
import com.winapp.svcatering.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ProductStockAnalysisActivity extends NavigationActivity {

    public static RecyclerView salesOrdersView;
    public static ProductStockAnalysisAdapter salesOrderAdapter;
    private ArrayList<ProductStockAnalysisModel> productsList;
    private SweetAlertDialog pDialog;
    private SessionManager session;
    private HashMap<String,String > user;
    private String companyId;
    int pageNo=1;
    private BottomSheetBehavior behavior;
    private ArrayList<CustomerModel> customerList;
    private SelectCustomerAdapter customerNameAdapter;
    private RecyclerView customerView;
    public static TextView selectCustomer;
    Button btnCancel;
    TextView customerName;
    EditText customerNameEdittext;
    DBHelper dbHelper;
    LinearLayout outstandingLayout;
    TextView netTotalText;
    private ArrayList<CustomerDetails> customerDetails;
    LinearLayout transLayout;
    View customerLayout;
    View salesOrderOptionLayout;
    TextView soCustomerName;
    TextView soNumber;
    TextView optionCancel;
    TextView cancelSheet;
    String userName;
    FloatingActionButton editSalesOrder;
    FloatingActionButton deleteSaleOrder;
    FloatingActionButton convertToInvoice;
    FloatingActionButton printPreview;
    String locationCode;
    String salesOrderStatus;
    LinearLayout editLayout;
    LinearLayout deleteLayout;
    LinearLayout convertLayout;
    LinearLayout printPreviewLayout;
    boolean isSearchCustomerNameClicked;
    boolean addnewCustomer;
    View searchFilterView;
    EditText customerNameText;
    private int mYear, mMonth, mDay, mHour, mMinute;
    EditText fromDate;
    EditText toDate;
    Button searchButton;
    Button cancelSearch;
    Spinner salesOrderStatusSpinner;
    public static LinearLayout emptyLayout;
    public String printSoNumber;
    public String noOfCopy;

    private ArrayList<SalesOrderPrintPreviewModel> salesOrderHeaderDetails;
    private ArrayList<SalesOrderPrintPreviewModel.SalesList> salesPrintList;
    private String printerMacId;
    private String printerType;
    private SharedPreferences sharedPreferences;
    View progressLayout;
    private Button filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_product_stock_analysis, contentFrameLayout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Analysis");

        salesOrdersView=findViewById(R.id.productListView);
        dbHelper=new DBHelper(this);
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get((SessionManager.KEY_LOCATION_CODE));
        customerView=findViewById(R.id.customerList);
        netTotalText=findViewById(R.id.net_total);
        customerNameEdittext=findViewById(R.id.customer_search);
        transLayout=findViewById(R.id.trans_layout);

        customerLayout=findViewById(R.id.customer_layout);
        salesOrderOptionLayout=findViewById(R.id.sales_option);
        soCustomerName=findViewById(R.id.name);
        soNumber=findViewById(R.id.so_no);
        optionCancel=findViewById(R.id.option_cancel);
        cancelSheet=findViewById(R.id.cancel_sheet);
        editSalesOrder=findViewById(R.id.edit_salesorder);
        deleteSaleOrder=findViewById(R.id.delete_salesorder);
        convertToInvoice=findViewById(R.id.convert_to_invoice);
        editLayout=findViewById(R.id.edit_layout);
        deleteLayout=findViewById(R.id.delete_layout);
        convertLayout=findViewById(R.id.convert_layout);
        printPreview=findViewById(R.id.print_preview);
        printPreviewLayout=findViewById(R.id.print_preview_layout);
        searchFilterView=findViewById(R.id.search_filter);
        customerNameText=findViewById(R.id.customer_name_value);
        fromDate=findViewById(R.id.from_date);
        toDate =findViewById(R.id.to_date);
        salesOrderStatusSpinner =findViewById(R.id.invoice_status);
        emptyLayout=findViewById(R.id.empty_layout);
        cancelSearch=findViewById(R.id.btn_cancel);
        searchButton=findViewById(R.id.btn_search);
        outstandingLayout=findViewById(R.id.outstanding_layout);
        progressLayout=findViewById(R.id.progress_layout);
        filter=findViewById(R.id.filter);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        fromDate.setText(formattedDate);
        toDate.setText(formattedDate);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        Log.w("Printer_Mac_Id:",printerMacId);
        Log.w("Printer_Type:",printerType);

        //dbHelper.removeAllProducts();

        productsList =new ArrayList<>();
        getProductList(companyId,"1");

        salesOrdersView.setHasFixedSize(true);

        salesOrdersView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        salesOrderAdapter=new ProductStockAnalysisAdapter(this, salesOrdersView, productsList, new ProductStockAnalysisAdapter.CallBack() {
            @Override
            public void calculateNetTotal(ArrayList<ProductStockAnalysisModel> salesList) {

            }
            @Override
            public void showMoreOption(){
                showBottomSheet();
            }
        });
        salesOrdersView.setAdapter(salesOrderAdapter);

        salesOrderAdapter.setOnLoadMoreListener(new ProductStockAnalysisAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                productsList.add(null);
                salesOrderAdapter.notifyItemInserted(productsList.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");

                        //Remove loading item
                        productsList.remove(productsList.size() - 1);
                        salesOrderAdapter.notifyItemRemoved(productsList.size());
                        //Load data
                        int index = productsList.size();
                        int end = index + 20;
                        pageNo=pageNo+1;
                        getProductList(companyId, String.valueOf(pageNo));
                    }
                }, 5000);
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
                        if (salesOrderOptionLayout.getVisibility()==View.VISIBLE){
                            getSupportActionBar().setTitle("Select Option");
                        }else {
                            getSupportActionBar().setTitle("Select Customer");
                        }
                        transLayout.setVisibility(View.VISIBLE);
                        transLayout.setClickable(false);
                        transLayout.setEnabled(false);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        getSupportActionBar().setTitle("Sales Order");
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


        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilter();
            }
        });

    }

    public void getProductList(String companyCode, String pageNo) {

        ProductStockAnalysisModel model=new ProductStockAnalysisModel();
        model.setProductName("Avin Milk");
        model.setPurchaseQty("100");
        model.setSalesQty("40");
        model.setBalanceQty("60");
        productsList.add(model);


        model=new ProductStockAnalysisModel();
        model.setProductName("Product A");
        model.setPurchaseQty("100");
        model.setSalesQty("40");
        model.setBalanceQty("60");
        productsList.add(model);


        model=new ProductStockAnalysisModel();
        model.setProductName("Product B");
        model.setPurchaseQty("100");
        model.setSalesQty("40");
        model.setBalanceQty("60");
        productsList.add(model);


        model=new ProductStockAnalysisModel();
        model.setProductName("Product C");
        model.setPurchaseQty("100");
        model.setSalesQty("40");
        model.setBalanceQty("60");
        productsList.add(model);

        model=new ProductStockAnalysisModel();
        model.setProductName("Product D");
        model.setPurchaseQty("100");
        model.setSalesQty("40");
        model.setBalanceQty("60");
        productsList.add(model);

        model=new ProductStockAnalysisModel();
        model.setProductName("Product E");
        model.setPurchaseQty("100");
        model.setSalesQty("40");
        model.setBalanceQty("60");
        productsList.add(model);

        model=new ProductStockAnalysisModel();
        model.setProductName("Product F");
        model.setPurchaseQty("100");
        model.setSalesQty("40");
        model.setBalanceQty("60");
        productsList.add(model);

        model=new ProductStockAnalysisModel();
        model.setProductName("Product G");
        model.setPurchaseQty("100");
        model.setSalesQty("40");
        model.setBalanceQty("60");
        productsList.add(model);

        model=new ProductStockAnalysisModel();
        model.setProductName("Product H");
        model.setPurchaseQty("100");
        model.setSalesQty("40");
        model.setBalanceQty("60");
        productsList.add(model);







   /*     // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        String url= Utils.getBaseUrl(this) +"ProductApi/GetSalesOrder_All?Requestdata={\"CompanyCode\":\""+companyCode+"\",\"PageSize\":20,\"PageNo\":\""+pageNo+"\"}";
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting Salesorder...");
        pDialog.setCancelable(false);
        if (pageNo.equals("1")){
            pDialog.show();
        }
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Response_is:",response.toString());
                        pDialog.dismiss();
                        if (response.length()>0) {
                            for (int i = 0; i < response.length(); i++) {
                                // Get current json object
                                JSONObject invoiceObject = response.getJSONObject(i);
                                ProductStockAnalysisModel model=new ProductStockAnalysisModel();
                                model.setName(invoiceObject.optString("CustomerName"));
                                model.setDate(invoiceObject.optString("SoDateString"));
                                model.setBalance(invoiceObject.optString("BalanceAmount"));
                                model.setSaleOrderNumber(invoiceObject.optString("SoNo"));
                                model.setAddress(invoiceObject.optString("Address1"));
                                model.setNetTotal(invoiceObject.optString("NetTotal"));
                                model.setStatus(invoiceObject.optString("Status"));
                                salesOrderList.add(model);
                            }

                            if (salesOrderList.size()>0){
                                salesOrdersView.setVisibility(View.VISIBLE);
                                emptyLayout.setVisibility(View.GONE);
                                outstandingLayout.setVisibility(View.VISIBLE);
                            }else {
                                salesOrdersView.setVisibility(View.GONE);
                                emptyLayout.setVisibility(View.VISIBLE);
                                outstandingLayout.setVisibility(View.GONE);
                            }

                            salesOrderAdapter.notifyDataSetChanged();
                            salesOrderAdapter.setLoaded();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
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
    }*/
    }

    public void showFilter(){
        final CharSequence[] items = {"All", "Highest Sales Product", "Out of Stock", "Lowest Sales Product"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductStockAnalysisActivity.this);
        builder.setTitle("Sort By");
       // builder.setIcon(R.drawable.ic_filter);
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
               // Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                      //  Toast.makeText(ShowDialog.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       // Toast.makeText(ShowDialog.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        Objects.requireNonNull(alert.getWindow()).setLayout(500, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected void setWrapContent(View v) {
        // Start with the provided view
        View current = v;

        // Travel up the tree until fail, modifying the LayoutParams
        do {
            // Get the parent
            ViewParent parent = current.getParent();

            // Check if the parent exists
            if (parent != null) {
                // Get the view
                try {
                    current = (View) parent;
                } catch (ClassCastException e) {
                    // This will happen when at the top view, it cannot be cast to a View
                    break;
                }

                // Modify the layout
                current.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
            }
        } while (current.getParent() != null);

        // Request a layout to be re-done
        current.requestLayout();
    }

    public void showBottomSheet(){
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
    public void closeSheet(){
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}