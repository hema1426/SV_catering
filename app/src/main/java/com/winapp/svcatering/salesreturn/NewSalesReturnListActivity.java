package com.winapp.svcatering.salesreturn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import com.winapp.svcatering.activity.CustomerListActivity;
import com.winapp.svcatering.activity.FilterCustomerListActivity;
import com.winapp.svcatering.activity.NavigationActivity;
import com.winapp.svcatering.adapter.SelectCustomerAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerGroupModel;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.SalesReturnModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.ImageUtil;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.winapp.svcatering.zebraprinter.TSCPrinter;
import com.winapp.svcatering.zebraprinter.ZebraPrinterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

public class NewSalesReturnListActivity extends NavigationActivity {

    private RecyclerView salesReturnView;
    private static LinearLayout emptyLayout;
    private FrameLayout recyclerLayout;
    private SessionManager session;
    private HashMap<String ,String > user;
    private String companyId;
    private SweetAlertDialog pDialog;
    private int pageNo=1;
    private BottomSheetBehavior behavior;
    private ArrayList<CustomerModel> customerList;
    private SelectCustomerAdapter customerNameAdapter;
    private RecyclerView customerView;
    public  static TextView selectCustomer;
    private Button btnCancel;
    private TextView customerName;
    private EditText customerNameEdittext;
    private DBHelper dbHelper;
    private static TextView netTotalText;
    private ArrayList<CustomerDetails> customerDetails;
    private LinearLayout transLayout;
    private View customerLayout;
    private View receiptsOptions;
    private TextView soCustomerName;
    private TextView srNumber;
    private TextView optionCancel;
    private TextView cancelSheet;
    private Button searchButton;
    private Button cancelSearch;
    private Spinner userListSpinner;
    public static LinearLayout outstandingLayout;
    private View searchFilterView;
    private EditText customerNameText;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private EditText fromDateText;
    private EditText toDateText;
    private boolean isSearchCustomerNameClicked;
    boolean addnewCustomer;
    private String userName;
    private static FrameLayout recyclerViewLayout;
    public static ArrayList<SalesReturnModel> salesReturnList;
    static SalesReturnlistAdapter salesReturnlistAdapter;
    LinearLayout editLayout;
    LinearLayout deleteLayout;
    LinearLayout printPreviewLayout;
    FloatingActionButton editSalesReturn;
    FloatingActionButton deleteSalesReturn;
    FloatingActionButton printPreview;
    String locationCode;
    public static ArrayList<String> searchableCustomerList;
    public static boolean isEdit=false;
    public static String salesReturnNo;
    public String salesReturnCode="";
    View progressLayout;
    Context mContext;
    public static String stockAdjustRefNo;
    public static ArrayList<String> stockAdjustRefNoList;
    private Spinner customerGroupSpinner;
    private ArrayList<CustomerGroupModel> customersGroupList;
    ProgressDialog dialog;
    String currentDate="";
    private int customerSelectCode=23;
    private String salesReturnNumber;
    private String noofCopy="1";
    private ArrayList<SalesReturnPrintPreviewModel> salesReturnHeader;
    private ArrayList<SalesReturnPrintPreviewModel.SalesReturnDetails> salesPrintReturnList;
    private String printerMacId;
    private String printerType;
    private SharedPreferences sharedPreferences;
    private int FILTER_CUSTOMER_CODE=134;
    private String selectCustomerCode="";
    private String selectCustomerName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_new_return_list, contentFrameLayout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sales Return List");
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        dbHelper=new DBHelper(this);

        salesReturnView=findViewById(R.id.salesReturnList);
        emptyLayout=findViewById(R.id.empty_layout);

        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        customerView=findViewById(R.id.customerList);
        netTotalText=findViewById(R.id.net_total);
        customerNameEdittext=findViewById(R.id.customer_search);
        transLayout=findViewById(R.id.trans_layout);
        //  customerDetails=dbHelper.getCustomer();
        customerLayout=findViewById(R.id.customer_layout);
        customerGroupSpinner=findViewById(R.id.customer_group);
        receiptsOptions =findViewById(R.id.receipt_options_layout);
        soCustomerName=findViewById(R.id.name);
        srNumber =findViewById(R.id.so_no);
        optionCancel=findViewById(R.id.option_cancel);
        cancelSheet=findViewById(R.id.cancel_sheet);
        fromDateText =findViewById(R.id.from_date);
        toDateText =findViewById(R.id.to_date);
        userListSpinner =findViewById(R.id.user_list_spinner);
        emptyLayout=findViewById(R.id.empty_layout);
        outstandingLayout=findViewById(R.id.outstanding_layout);
        cancelSearch=findViewById(R.id.btn_cancel);
        searchButton=findViewById(R.id.btn_search);
        searchFilterView=findViewById(R.id.search_filter);
        customerNameText=findViewById(R.id.customer_name_return);
        recyclerViewLayout=findViewById(R.id.reclerview_layout);
        editLayout=findViewById(R.id.edit_layout);
        deleteLayout=findViewById(R.id.delete_layout);
        printPreviewLayout=findViewById(R.id.print_preview_layout);
        editSalesReturn=findViewById(R.id.edit_sr);
        deleteSalesReturn=findViewById(R.id.delete_sr);
        printPreview=findViewById(R.id.print_preview);
        progressLayout=findViewById(R.id.progress_layout);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        fromDateText.setText(formattedDate);
        toDateText.setText(formattedDate);

        stockAdjustRefNoList=new ArrayList<>();

        dbHelper.removeAllItems();
        dbHelper.removeAllInvoiceItems();

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        Log.w("Printer_Mac_Id:",printerMacId);
        Log.w("Printer_Type:",printerType);

        if (getIntent() != null){
            salesReturnNumber =getIntent().getStringExtra("srNumber");
            noofCopy=getIntent().getStringExtra("noOfCopy");
            if (salesReturnNumber !=null){
                try {
                    getSalesReturnPrintDetails(salesReturnNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


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
                        transLayout.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
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

        try {
            Date c1 = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c1);
            SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            currentDate = df1.format(c1);
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("User",userName);
            jsonObject.put("CustomerCode","");
            jsonObject.put("FromDate",currentDate);
            jsonObject.put("ToDate", currentDate);
            jsonObject.put("DocStatus","");
            getSalesReturn(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


     /*   salesReturnView.setHasFixedSize(true);
        salesReturnView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        salesReturnlistAdapter =new SalesReturnlistAdapter(this, salesReturnView, salesReturnList, new SalesReturnlistAdapter.CallBack() {
            @Override
            public void calculateNetTotal(ArrayList<SalesReturnModel> receiptsList) {
                setNettotal(receiptsList);
            }
            @Override
            public void showMoreOption(String salesreturn_no, String customerName){
                customerLayout.setVisibility(View.GONE);
                receiptsOptions.setVisibility(View.VISIBLE);
                srNumber.setText(salesreturn_no);
                soCustomerName.setText(customerName);
                customerLayout.setVisibility(View.GONE);
                receiptsOptions.setVisibility(View.VISIBLE);
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        salesReturnView.setAdapter(salesReturnlistAdapter);*/
       /* salesReturnlistAdapter.setOnLoadMoreListener(new SalesReturnlistAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                salesReturnList.add(null);
                salesReturnlistAdapter.notifyItemInserted(salesReturnList.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");
                        try {
                            //Remove loading item
                            if (salesReturnList!=null){
                                salesReturnList.remove(salesReturnList.size() - 1);
                                salesReturnlistAdapter.notifyItemRemoved(salesReturnList.size());
                                //Load data
                                int index = salesReturnList.size();
                                int end = index + 20;
                                pageNo=pageNo+1;
                            }
                            JSONObject object=new JSONObject();
                            object.put("CompanyCode",companyId);
                            object.put("PageSize","20");
                            object.put("PageNo",pageNo);
                          //  getSalesReturn(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, 5000);
            }
        });*/

        deleteSalesReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveAlert(srNumber.getText().toString());
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveAlert(srNumber.getText().toString());
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        editSalesReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                try {
                    getSalesReturnDetails(srNumber.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                try {
                    getSalesReturnDetails(srNumber.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        printPreviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                Intent intent=new Intent(NewSalesReturnListActivity.this, SalesReturnPrintPreview.class);
                intent.putExtra("srNumber",srNumber.getText().toString());
                startActivity(intent);
            }
        });
        printPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                Intent intent=new Intent(NewSalesReturnListActivity.this, SalesReturnPrintPreview.class);
                intent.putExtra("srNumber",srNumber.getText().toString());
                startActivity(intent);
            }
        });

        optionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        customerNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //customerListSpinner.setVisibility(View.VISIBLE);
                Intent intent=new Intent(getApplicationContext(), FilterCustomerListActivity.class);
                startActivityForResult(intent,FILTER_CUSTOMER_CODE);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchFilterView.setVisibility(View.GONE);
                String fromdate=Utils.changeDateFormat(fromDateText.getText().toString());
                String todate=Utils.changeDateFormat(toDateText.getText().toString());
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("User",userName);
                    jsonObject.put("CustomerCode",selectCustomerCode);
                    jsonObject.put("FromDate",fromdate);
                    jsonObject.put("ToDate", todate);
                    jsonObject.put("DocStatus","");
                    getSalesReturn(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


              /*  String customer_name=customerNameText.getText().toString();
                SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
                Date d1 = null;
                Date d2=null;
                try {
                    d1 = sdformat.parse(fromDateText.getText().toString());
                    d2 = sdformat.parse(toDateText.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(d1.compareTo(d2) > 0) {
                    Toast.makeText(getApplicationContext(),"From date should not be greater than to date",Toast.LENGTH_SHORT).show();
                } else{
                    searchFilterView.setVisibility(View.GONE);
                    String fromdate=Utils.changeDateFormat(fromDateText.getText().toString());
                    String todate=Utils.changeDateFormat(toDateText.getText().toString());
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("User",userName);
                        jsonObject.put("CustomerCode",selectCustomerCode);
                        jsonObject.put("FromDate",fromdate);
                        jsonObject.put("ToDate", todate);
                        jsonObject.put("DocStatus","");
                        getSalesReturn(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                  //  SalesReturnList.filterSearch(customer_name, locationCode, fromDateText.getText().toString(), toDateText.getText().toString());
                }*/
            }
        });

        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerNameText.setText("");
                selectCustomerCode= "";
                fromDateText.setText(formattedDate);
                toDateText.setText(formattedDate);
                searchFilterView.setVisibility(View.GONE);
               // setFilterAdapeter();
            }
        });

        fromDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(fromDateText);
            }
        });

        toDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(toDateText);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK && requestCode==FILTER_CUSTOMER_CODE){
            assert data != null;
            String customername=data.getStringExtra("customerName");
            String customercode=data.getStringExtra("customerCode");
            customerNameText.setText(customername);
            selectCustomerCode=customercode;
            selectCustomerName=customername;
        }
    }


    public void getDate(EditText dateEditext){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(NewSalesReturnListActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateEditext.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void getSalesReturnDetails(String srNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("SalesReturnNo", srNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SalesReturnDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        stockAdjustRefNoList=new ArrayList<>();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting SalesReturn Details...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try{
                Log.w("EditDetails:",response.toString());
                if (response.length()>0){
                    String statusCode=response.optString("statusCode");
                    if (statusCode.equals("1")){
                        JSONArray salesArray=response.optJSONArray("responseData");
                        assert salesArray != null;
                        JSONObject salesObject=salesArray.optJSONObject(0);
                        String salesorder_no=salesObject.optString("srNumber");
                        String salesorder_code = salesObject.optString("code");
                        String company_code=salesObject.optString("CompanyCode");
                        String customer_code=salesObject.optString("customerCode");
                        String customer_name=salesObject.optString("customerName");
                        String total=salesObject.optString("total");
                        String sub_total=salesObject.optString("subTotal");
                        String bill_discount=salesObject.optString("billDiscount");
                        String item_discount=salesObject.optString("ItemDiscount");
                        String tax=salesObject.optString("taxTotal");
                        String net_total=salesObject.optString("netTotal");
                        String currency_rate=salesObject.optString("CurrencyRate");
                        String currency_name=salesObject.optString("currencyName");
                        String tax_type=salesObject.optString("taxType");
                        String tax_perc=salesObject.optString("taxPerc");
                        String tax_code=salesObject.optString("taxCode");
                        String phone_no=salesObject.optString("DelPhoneNo");
                        String so_date=salesObject.optString("soDate");
                     //   String signFlag=salesObject.optString("signFlag");
                        if (salesObject.optString("signature")!=null &&
                                !salesObject.optString("signature").equals("null") && !salesObject.optString("signature").isEmpty()){
                            String signature = salesObject.optString("signature");
                            Utils.setSignature(signature);
                            createSignature();
                        } else {
                            Utils.setSignature("");
                        }
                        dbHelper.removeCustomer();
                        dbHelper.insertCustomer(
                                customer_code,
                                customer_name,
                                phone_no,
                                salesObject.optString("address1"),
                                salesObject.optString("Address2"),
                                salesObject.optString("Address3"),
                                salesObject.optString("IsActive"),
                                salesObject.optString("HaveTax"),
                                salesObject.optString("taxType"),
                                salesObject.optString("taxPerc"),
                                salesObject.optString("taxCode"),
                                salesObject.optString("CreditLimit"),
                                "Singapore",
                                salesObject.optString("currencyCode"));
                        dbHelper.removeAllItems();

                        dbHelper.removeCustomerTaxes();
                        SalesReturnCustomer.customerCodeEdittext.setText(customer_code);
                        SalesReturnCustomer.customerName.setText(currency_name);
                        CustomerDetails model=new CustomerDetails();
                        model.setCustomerCode(customer_code);
                        model.setCustomerName(customer_name);
                        model.setCustomerAddress1(salesObject.optString("address1"));
                        model.setTaxPerc( salesObject.optString("taxPercentage"));
                        model.setTaxType(salesObject.optString("taxType"));
                        model.setTaxCode( salesObject.optString("taxCode"));
                        ArrayList<CustomerDetails> taxList =new ArrayList<>();
                        taxList.add(model);
                        dbHelper.insertCustomerTaxValues(taxList);

                        JSONArray products=salesObject.getJSONArray("salesReturnDetails");
                        for (int i=0;i<products.length();i++) {
                            JSONObject object = products.getJSONObject(i);

                            String lqty = "0.0";
                            String cqty = "0.0";
                            if (!object.optString("unitQty").equals("null")) {
                                lqty = object.optString("unitQty");
                            }

                            if (!object.optString("quantity").equals("null")) {
                                cqty = object.optString("quantity");
                            }

                            double actualPrice = Double.parseDouble(object.optString("unitPrice"));

                            dbHelper.insertCart(
                                    object.optString("productCode"),
                                    object.optString("productName"),
                                    cqty,
                                    "0",
                                    object.optString("subTotal"),
                                    "",
                                    object.optString("lineTotal"),
                                    "weight",
                                    String.valueOf(actualPrice),
                                    object.optString("subTotal"),
                                    object.optString("pcsPerCarton"),
                                    object.optString("totalTax"),
                                    object.optString("subTotal"),
                                    object.optString("taxType"),
                                    object.optString("FOCQty"),
                                    "",
                                    object.optString("ExchangeQty"),
                                    "",
                                    "0.00",
                                    object.optString("ReturnQty"),
                                    "",
                                    "",
                                    object.optString("total"),
                                    "",
                                    object.optString("uomCode"),
                                    object.optString("minimumSellingPrice"),"");


                        }
                        int count = dbHelper.numberOfRows();
                        if (products.length()==count){
                            isEdit=true;
                            // stockAdjustRefNo=object.optString("StockAdjRefCode");
                            SalesReturnActivity.productButton.performClick();
                        }
                    }
                }
                pDialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }
        }, error -> {
            // Do something when error occurred
            pDialog.dismiss();
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

    private void getSalesReturnPrintDetails(String srNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("SalesReturnNo", srNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(NewSalesReturnListActivity.this);
        String url= Utils.getBaseUrl(NewSalesReturnListActivity.this) +"SalesReturnDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url_SalesReturn:",url+"/"+jsonObject.toString());
        salesReturnHeader =new ArrayList<>();
        salesPrintReturnList =new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        Log.w("SalesReturn_Details:",response.toString());
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray responseData=response.optJSONArray("responseData");
                            assert responseData != null;
                            if (responseData.length()>0){
                                SalesReturnPrintPreviewModel model=new SalesReturnPrintPreviewModel();
                                JSONObject object =responseData.optJSONObject(0);
                                model.setSrNo(object.optString("srNumber"));
                                model.setSrDate(object.optString("srDate"));
                                model.setCustomerCode(object.optString("customerCode"));
                                model.setCustomerName(object.optString("customerName"));
                                model.setSubTotal(object.optString("subTotal"));
                                model.setTax(object.optString("taxTotal"));
                                model.setNetTotal(object.optString("netTotal"));
                                model.setBillDisc(object.optString("billDiscount"));
                                model.setItemDisc(object.optString("iTotalDiscount"));
                                model.setTaxType(object.optString("taxType"));
                                model.setTaxValue(object.optString("taxPerc"));
                                model.setSignFlag(object.optString("signFlag"));
                                model.setAddress(object.optString("address1") + object.optString("address2") + object.optString("address3"));
                                model.setAddress1(object.optString("address1"));
                                model.setAddress2(object.optString("address2"));
                                model.setAddress3(object.optString("address3"));
                                model.setAddressstate(object.optString("block")+" "+object.optString("street")+" "
                                        +object.optString("city"));
                                model.setAddresssZipcode(object.optString("countryName")+" "+object.optString("state")+" "
                                        +object.optString("zipcode"));
                               // String signFlag = object.optString("signFlag");
                                if (object.optString("signature")!=null &&
                                        !object.optString("signature").equals("null") && !object.optString("signature").isEmpty()){
                                    String signature = object.optString("signature");
                                    Utils.setSignature(signature);
                                    createSignature();
                                } else {
                                    Utils.setSignature("");
                                }

                                JSONArray salesReturnDetails=object.optJSONArray("salesReturnDetails");
                                for (int i = 0; i< Objects.requireNonNull(salesReturnDetails).length(); i++){
                                    JSONObject detailsObject =salesReturnDetails.getJSONObject(i);
                                    SalesReturnPrintPreviewModel.SalesReturnDetails salesReturnModel =new SalesReturnPrintPreviewModel.SalesReturnDetails();
                                    salesReturnModel.setProductCode(detailsObject.optString("productCode"));
                                    salesReturnModel.setDescription( detailsObject.optString("productName"));
                                    salesReturnModel.setLqty(detailsObject.optString("quantity"));
                                    salesReturnModel.setCqty(detailsObject.optString("cartonQty"));
                                    salesReturnModel.setNetqty(detailsObject.optString("quantity"));
                                    salesReturnModel.setCartonPrice(detailsObject.optString("price"));
                                    salesReturnModel.setPrice(detailsObject.optString("price"));
                                    salesReturnModel.setPcspercarton(detailsObject.optString("pcsPerCarton"));
                                    salesReturnModel.setTax(detailsObject.optString("totalTax"));
//                                    salesReturnModel.setTotal(detailsObject.optString("netTotal"));
                                    salesReturnModel.setTotal(detailsObject.optString("total"));
                                    salesReturnModel.setSubTotal(detailsObject.optString("subTotal"));
                                    salesReturnModel.setUomCode(object.optString("UOMCode"));
                                    salesPrintReturnList.add(salesReturnModel);
                                }
                                model.setSalesReturnList(salesPrintReturnList);
                                salesReturnHeader.add(model);
                            }
                            if (salesReturnList.size()>0){
                                printSalesReturn(Integer.parseInt(noofCopy));
                            }
                        }else {

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
    private void createSignature() {
        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
            try {
                ImageUtil.saveStamp(this, Utils.getSignature(), "Signature");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void printSalesReturn(int copy) throws IOException {
        if (printerType.equals("TSC Printer")){
            // TSCPrinter tscPrinter=new TSCPrinter(SalesReturnPrintPreview.this,printerMacId);
            //    tscPrinter.printInvoice(salesReturnHeader, salesReturnList);
            TSCPrinter printer=new TSCPrinter(NewSalesReturnListActivity.this,printerMacId,"SalesReturn");
            printer.printSalesReturn(copy,salesReturnHeader,salesPrintReturnList);
        }else if (printerType.equals("Zebra Printer")){
            ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(NewSalesReturnListActivity.this,printerMacId);
            try {
                zebraPrinterActivity.printSalesReturn(copy, salesReturnHeader, salesPrintReturnList);
            } catch (IOException e) {
                e.printStackTrace();
                   }
        }
    }



    public void getCustomers(){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"MasterApi/GetCustomer_All?Requestdata={CompanyCode:"+companyId+"}";
        customerList=new ArrayList<>();
        searchableCustomerList=new ArrayList<>();
        Log.w("Given_url:",url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.w("Response_is:", response.toString());
                        // pDialog.dismiss();
                        // Loop through the array elements
                        for (int i = 0; i < response.length(); i++) {
                            // Get current json object
                            JSONObject productObject = response.getJSONObject(i);
                            CustomerModel model=new CustomerModel();
                            model.setCustomerCode(productObject.optString("CustomerCode"));
                            model.setCustomerName(productObject.optString("CustomerName"));
                            model.setCustomerAddress(productObject.optString("Address1"));
                            customerList.add(model);
                            searchableCustomerList.add(productObject.optString("CustomerName")+" - "+productObject.optString("CustomerCode"));
                        }
                        if (customerList.size()>0){
                            setAdapter(customerList);
//                            ((SalesReturnActivity)getActivity()).setDataToAdapter(searchableCustomerList);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
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

    private void setAdapter(ArrayList<CustomerModel> customerNames) {
        progressLayout.setVisibility(View.GONE);
        customerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        customerNameAdapter = new SelectCustomerAdapter(this, customerNames, new SelectCustomerAdapter.CallBack() {
            @Override
            public void searchCustomer(String customer,String customername, int pos) {
                customerLayout.setVisibility(View.VISIBLE);
                receiptsOptions.setVisibility(View.GONE);
                int count =dbHelper.numberOfRows();
            }
        });
        customerView.setAdapter(customerNameAdapter);
    }

    public void getSalesReturn(JSONObject jsonObject){

        try {
            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            // Initialize a new JsonArrayRequest instance
            String url=Utils.getBaseUrl(this) +"SalesReturnList";
            Log.w("Given_url:",url);
            Log.w("Sales_Return-Object:",jsonObject.toString());
            salesReturnList=new ArrayList<>();
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Getting Sales Return...");
            pDialog.setCancelable(false);
            pDialog.show();
            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
                try{
                    Log.w("Sales_Return_values:",response.toString());
                    if (response.length()>0) {
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray detailsArray=response.optJSONArray("responseData");
                            for (int i = 0; i < detailsArray.length(); i++) {
                                JSONObject object=detailsArray.getJSONObject(i);
                                SalesReturnModel model=new SalesReturnModel();
                                model.setCustomerName(object.optString("customerName"));
                                model.setSalesReturnNumber(object.optString("srNumber"));
                                model.setSalesReturnDate(object.optString("srDate"));
                                model.setBalanceAmount(object.optString("balance"));
                                model.setNetAmount(object.optString("netTotal"));
                                model.setSalesReturnCode(object.optString("code"));
                                model.setLocationCode("");
                                model.setUser("");
                                salesReturnList.add(model);
                            }
                           // salesReturnlistAdapter.notifyDataSetChanged();
                          //  salesReturnlistAdapter.setLoaded();
                            if (salesReturnList.size()>0){
                                setSalesReturnAdapter(salesReturnList);
                                recyclerViewLayout.setVisibility(View.VISIBLE);
                                emptyLayout.setVisibility(View.GONE);
                            }else {
                                recyclerViewLayout.setVisibility(View.GONE);
                                emptyLayout.setVisibility(View.VISIBLE);
                            }
                        }else {
                            recyclerViewLayout.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
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
                Toast.makeText(this,"Server Error , Please Try again..",Toast.LENGTH_LONG).show();
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
        }catch (Exception ex){}
    }

    public void setSalesReturnAdapter(ArrayList<SalesReturnModel> salesReturnList){
        selectCustomerCode="";
        selectCustomerName="";
        salesReturnView.setHasFixedSize(true);
        salesReturnView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        setNettotal(salesReturnList);
        salesReturnlistAdapter =new SalesReturnlistAdapter(this, salesReturnView, salesReturnList, new SalesReturnlistAdapter.CallBack() {
            @Override
            public void calculateNetTotal(ArrayList<SalesReturnModel> salesReturnList) {
                setNettotal(salesReturnList);
            }
            @Override
            public void showMoreOption(String salesreturn_no, String customerName){
                customerLayout.setVisibility(View.GONE);
                receiptsOptions.setVisibility(View.VISIBLE);
                srNumber.setText(salesreturn_no);
                soCustomerName.setText(customerName);
                customerLayout.setVisibility(View.GONE);
                receiptsOptions.setVisibility(View.VISIBLE);
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        salesReturnView.setAdapter(salesReturnlistAdapter);
    }

    public void showRemoveAlert(String salesReturnId){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                // .setTitleText("Are you sure?")
                .setContentText("Are you sure want Delete SalesReturn ?")
                .setConfirmText("YES")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        try {
                            sDialog.dismiss();
                            setDeleteSalesReturn(salesReturnId);
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
                    }}).show();
    }



    private void setDeleteSalesReturn(String srNumber) throws JSONException {
        // Initialize a new RequestQueue instance

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyId);
        jsonObject.put("SalesReturnNo",srNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SalesApi/DeleteSR?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Deleting Sales return...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url,
                null,
                response -> {
                    try{
                        Log.w("SaleOrder_Response_is:",response.toString());
                        if (response.length()>0){
                            boolean issaved=response.optBoolean("IsSaved");
                            String result=response.optString("Result");
                            boolean isDeleted=response.optBoolean("IsDeleted");
                            if (result.equals("pass") && isDeleted){
                                this.finish();
                                startActivity(this.getIntent());
                            }else {
                                Toast.makeText(this,"Error in Deleting SalesOrder",Toast.LENGTH_LONG).show();
                            }
                        }
                        pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            pDialog.dismiss();
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

    public static void filterSearch(String customerName, String location, String fromdate, String todate) {
        try {
            ArrayList<SalesReturnModel> filterdNames = new ArrayList<>();
            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
            Date from_date=null;
            Date to_date=null;
            try {
                from_date=sdf.parse(fromdate);
                to_date=sdf.parse(todate);
            }  catch (ParseException e) {
                e.printStackTrace();
            }
            for (SalesReturnModel model: SalesReturnlistAdapter.getSalesReturnList()){
                Date compareDate=sdf.parse(model.getSalesReturnDate());
                if (from_date.equals(to_date)){
                    if (from_date.equals(compareDate)){
                        if (!customerName.isEmpty()){
                            if (model.getCustomerName().toLowerCase().contains(customerName.toLowerCase())) {
                                if (model.getLocationCode()!=null && !model.getLocationCode().equals("null")){
                                    if (model.getLocationCode().equals(location)){
                                        filterdNames.add(model);
                                    }
                                }
                                salesReturnlistAdapter.filterList(filterdNames);
                            }
                        }else {
                            if (model.getCustomerName().toLowerCase().contains(customerName.toLowerCase())) {
                                if (model.getLocationCode()!=null && !model.getLocationCode().equals("null")){
                                    if (model.getLocationCode().equals(location)){
                                        filterdNames.add(model);
                                    }
                                }
                                salesReturnlistAdapter.filterList(filterdNames);
                            }
                            salesReturnlistAdapter.filterList(filterdNames);
                        }
                    }
                } else if(compareDate.compareTo(from_date) >= 0 && compareDate.compareTo(to_date) <= 0) {
                    System.out.println("Compare date occurs after from date");
                    if (!customerName.isEmpty()){
                        if (model.getCustomerName().toLowerCase().contains(customerName.toLowerCase())) {
                            if (model.getCustomerName().toLowerCase().contains(customerName.toLowerCase())) {
                                if (model.getLocationCode()!=null && !model.getLocationCode().equals("null")){
                                    if (model.getLocationCode().equals(location)){
                                        filterdNames.add(model);
                                    }
                                }
                                salesReturnlistAdapter.filterList(filterdNames);
                            }
                            salesReturnlistAdapter.filterList(filterdNames);
                        }
                    }else {
                        if (model.getCustomerName().toLowerCase().contains(customerName.toLowerCase())) {
                            if (model.getLocationCode()!=null && !model.getLocationCode().equals("null")){
                                if (model.getLocationCode().equals(location)){
                                    filterdNames.add(model);
                                }
                            }
                            salesReturnlistAdapter.filterList(filterdNames);
                        }
                        salesReturnlistAdapter.filterList(filterdNames);
                    }
                }
                salesReturnlistAdapter.filterList(filterdNames);
            }

            Log.w("FilteredSize:",filterdNames.size()+"");

            if (filterdNames.size()>0){
                recyclerViewLayout.setVisibility(View.VISIBLE);
                outstandingLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
                //  setNettotal(filterdNames);
                // invoiceAdapter.filterList(filterdNames);
            }else {
                recyclerViewLayout.setVisibility(View.GONE);
                outstandingLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            }
        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }



    public static void setNettotal(ArrayList<SalesReturnModel> salesReturnList){
        try {
            double net_amount=0.0;
            double balance_amt=0.0;
            for (SalesReturnModel model: salesReturnList){
                if (model.getBalanceAmount()!=null && !model.getBalanceAmount().equals("null")){
                    balance_amt=Double.parseDouble(model.getBalanceAmount());
                }else {
                    balance_amt=0.0;
                }
                net_amount+=balance_amt;
            }
            netTotalText.setText("$ "+Utils.twoDecimalPoint(net_amount));
        }catch (Exception ex){}

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sorting_menu, menu);
        MenuItem item = menu.findItem(R.id.action_barcode);
        item.setVisible(false);

      /*  ArrayList<UserRoll> userRolls=helper.getUserPermissions();
        if (userRolls.size()>0) {
            for (UserRoll roll : userRolls) {
                if (roll.getFormName().equals("Add Invoice")){
                    if (roll.getHavePermission().equals("true")){
                       // addInvoice.setVisible(true);
                        if (createInvoiceSetting=="true"){
                            addInvoice.setVisible(true);
                        }else {
                            addInvoice.setVisible(false);
                        }
                    }else {
                        addInvoice.setVisible(true);
                    }
                }
            }
        }*/

        MenuItem filter=menu.findItem(R.id.action_filter);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
           // onBackPressed();
            finish();
         /*   case R.id.action_remove:
                showRemoveAlert();
                break;*/
        }else if (item.getItemId()==R.id.action_customer_name){

        }else if (item.getItemId()==R.id.action_amount){

        }else if (item.getItemId()==R.id.action_date){


        }else if (item.getItemId()==R.id.action_add){
            Intent intent=new Intent(getApplicationContext(), CustomerListActivity.class);
            intent.putExtra("from","sales_return");
            startActivityForResult(intent,customerSelectCode);

        }else if (item.getItemId()==R.id.action_filter){
            if (searchFilterView.getVisibility()==View.VISIBLE){
                searchFilterView.setVisibility(View.GONE);
                customerNameText.setText("");
                selectCustomerCode= "";
                isSearchCustomerNameClicked=false;
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                //slideUp(searchFilterView);
            }else {
                customerNameText.setText("");
                selectCustomerCode= "";
                isSearchCustomerNameClicked=false;
                searchFilterView.setVisibility(View.VISIBLE);
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                // slideDown(searchFilterView);
            }
        }
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}