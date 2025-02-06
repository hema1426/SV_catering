package com.winapp.svcatering.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.SalesOrderAdapterNew;
import com.winapp.svcatering.adapter.SelectCustomerAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.fragments.CustomerFragment;
import com.winapp.svcatering.model.AppUtils;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.SalesOrderModel;
import com.winapp.svcatering.model.SalesOrderPrintPreviewModel;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.model.UserListModel;
import com.winapp.svcatering.printpreview.SalesOrderPrintPreview;
import com.winapp.svcatering.utils.BarCodeScanner;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.ImageUtil;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.SharedPreferenceUtil;
import com.winapp.svcatering.utils.UserAdapter;
import com.winapp.svcatering.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.winapp.svcatering.zebraprinter.TSCPrinter;
import com.winapp.svcatering.zebraprinter.ZebraPrinterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class SalesOrderListActivity extends NavigationActivity implements AdapterView.OnItemSelectedListener {

    public static RecyclerView salesOrdersView;
    public static SalesOrderAdapterNew salesOrderAdapter;
    private ArrayList<SalesOrderModel> salesOrderList;
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
    public Button btnCancel;
    public TextView customerName;
    public EditText customerNameEdittext;
    public DBHelper dbHelper;
    public TextView netTotalText;
    double netTotalApi = 0.00;
    private ArrayList<CustomerDetails> customerDetails;
    public LinearLayout transLayout;
    public View customerLayout;
    public View salesOrderOptionLayout;
    public TextView soCustomerName;
    public TextView soNumber;
    public TextView optionCancel;
    public TextView cancelSheet;
    public String userName;
    public FloatingActionButton editSalesOrder;
    public FloatingActionButton deleteSaleOrder;
    public FloatingActionButton convertToInvoice;
    public FloatingActionButton printPreview;
    public String locationCode;
    public String salesOrderStatus;
    public LinearLayout editLayout;
    public LinearLayout deleteLayout;
    public LinearLayout convertLayout;
    public LinearLayout printPreviewLayout;
    boolean isSearchCustomerNameClicked;
    boolean addnewCustomer;
    public View searchFilterView;
    public EditText customerNameText;
    private int mYear, mMonth, mDay, mHour, mMinute;
    public EditText fromDate;
    public EditText toDate;
    public Button searchButton;
    public Button cancelSearch;
    public Spinner salesOrderStatusSpinner;
    public static LinearLayout emptyLayout;
    public static LinearLayout outstandingLayout;
    public String printSoNumber;
    public String noOfCopy;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private ArrayList<SalesOrderPrintPreviewModel> salesOrderHeaderDetails;
    private ArrayList<SalesOrderPrintPreviewModel.SalesList> salesPrintList;
    private String printerMacId;
    private String printerType;
    private SharedPreferences sharedPreferences;
    public View progressLayout;
    boolean redirectInvoice;
    public static String selectedCustomerId="";
    public String isFound="true";
    private Button createSalesOrder;
    private int customerSelectCode=13;
    public String createInvoiceSetting="true";
    public String editSo="false";
    private int FILTER_CUSTOMER_CODE=134;
    String currentDate;
    private ArrayList<UserListModel> usersList;
    private Spinner salesManSpinner;
    private String selectedUser="";
    public static String shortCodeStr = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_sales_order_list, contentFrameLayout);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sales Orders");

        salesOrdersView=findViewById(R.id.salesOrderList);
        dbHelper=new DBHelper(this);
        session=new SessionManager(this);
        user=session.getUserDetails();
        sharedPreferenceUtil = new SharedPreferenceUtil(this);

        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get((SessionManager.KEY_LOCATION_CODE));

        customerView=findViewById(R.id.customerList);
        netTotalText=findViewById(R.id.net_total_List);
        customerNameEdittext=findViewById(R.id.customer_search);
        transLayout=findViewById(R.id.trans_layout);
        customerDetails=dbHelper.getCustomer();
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
        createSalesOrder=findViewById(R.id.create_sales);
        salesManSpinner=findViewById(R.id.salesman_spinner);
        salesManSpinner.setOnItemSelectedListener(this);

        shortCodeStr = sharedPreferenceUtil.getStringPreference(sharedPreferenceUtil
                .KEY_SHORT_CODE,"");
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        fromDate.setText(formattedDate);
        toDate.setText(formattedDate);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        Log.w("Printer_Mac_Id:",printerMacId);
        Log.w("Printer_Type:",printerType);

        dbHelper.removeAllItems();
        dbHelper.removeAllInvoiceItems();
        try {
            getAllUsers();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppUtils.setProductsList(null);

        ArrayList<SettingsModel> settings=dbHelper.getSettings();
        if (settings!=null) {
            if (settings.size() > 0) {
                for (SettingsModel model : settings) {
                    if (model.getSettingName().equals("create_invoice_switch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            createInvoiceSetting="true";
                        }else {
                            createInvoiceSetting="false";
                        }
                    }else if (model.getSettingName().equals("editSO")){
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("True")) {
                            editSo="true";
                        }else {
                            editSo="false";
                        }
                    }
                }
            }
        }

        //dbHelper.removeAllProducts();



    /*    customerList=dbHelper.getAllCustomers();
        if (customerList!=null && customerList.size()>0){
            setAdapter(customerList);
        }else {
            getCustomers();
           // new GetCustomersTask().execute();
        }*/


        Date c1 = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c1);
        SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = df1.format(c1);

        salesOrderList=new ArrayList<>();
        try {
            getSalesOrderList(companyId,"1",currentDate,currentDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        salesOrdersView.setHasFixedSize(true);
        salesOrdersView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        salesOrderAdapter=new SalesOrderAdapterNew(this, salesOrdersView, salesOrderList, new SalesOrderAdapterNew.CallBack() {
            @Override
            public void calculateNetTotal(ArrayList<SalesOrderModel> salesList) {
                setNettotalFun(salesList);
            }
            @Override
            public void showMoreOption(String salesorderId,String customerName,String status){
                customerLayout.setVisibility(View.GONE);
                salesOrderOptionLayout.setVisibility(View.VISIBLE);
                soNumber.setText(salesorderId);
                soCustomerName.setText(customerName);
                salesOrderStatus =status;
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
               // viewCloseBottomSheet();
            }
        });
        salesOrdersView.setAdapter(salesOrderAdapter);

        /*salesOrderAdapter.setOnLoadMoreListener(new SalesOrderAdapterNew.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                salesOrderList.add(null);
                salesOrderAdapter.notifyItemInserted(salesOrderList.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");

                        //Remove loading item
                        salesOrderList.remove(salesOrderList.size() - 1);
                        salesOrderAdapter.notifyItemRemoved(salesOrderList.size());
                        //Load data
                        int index = salesOrderList.size();
                        int end = index + 20;
                        pageNo=pageNo+1;
                        //getSalesOrderList(companyId, String.valueOf(pageNo));
                    }
                }, 5000);
            }
        });*/

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
                        if (redirectInvoice){
                            if (createInvoiceSetting.equals("true")){
                                Intent intent=new Intent(getApplicationContext(),CreateNewInvoiceActivity.class);
                                intent.putExtra("customerName",soCustomerName.getText().toString());
                                intent.putExtra("customerCode",selectedCustomerId.toString());
                                startActivity(intent);
                                finish();
                            }else {
                                CustomerFragment.isLoad=true;
                                Intent intent=new Intent(SalesOrderListActivity.this, AddInvoiceActivityOld.class);
                                intent.putExtra("customerId",selectedCustomerId);
                                intent.putExtra("activityFrom","SalesOrder");
                                startActivity(intent);
                                finish();
                            }
                        }
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

        customerNameEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                String cusname=editable.toString();
                if (!cusname.isEmpty()){
                    filter(cusname);
                }
            }
        });

        optionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        cancelSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        createSalesOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearchCustomerNameClicked=false;
                customerLayout.setVisibility(View.VISIBLE);
                salesOrderOptionLayout.setVisibility(View.GONE);
                searchFilterView.setVisibility(View.GONE);
                //  viewCloseBottomSheet();
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    viewCloseBottomSheet();
                    if (!salesOrderStatus.equals("Closed") && !salesOrderStatus.equals("InProgress Invoice")){
                        if (editSo.equals("true")){
                            getSalesOrderDetails(soNumber.getText().toString(),"Edit");
                        }else {
                            Toast.makeText(getApplicationContext(),
                                    "You Don't have permission to Edit",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),
                                "This Sales order already Closed",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        editSalesOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    viewCloseBottomSheet();
                    if (!salesOrderStatus.equals("Closed") && !salesOrderStatus.equals("InProgress Invoice")){
                        if (editSo.equals("true")){
                            getSalesOrderDetails(soNumber.getText().toString(),"Edit");
                        }else {
                            Toast.makeText(getApplicationContext(),
                                    "You Don't have permission to Edit",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"This Sales order already Closed",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                if (salesOrderStatus.equals("Open")){
                    if (editSo.equals("true")) {
                        showRemoveAlert(soNumber.getText().toString());
                    }else {
                        Toast.makeText(getApplicationContext(),
                                "You Don't have permission to Delete",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Can't Delete Closed SalesOrder",Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteSaleOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                if (salesOrderStatus.equals("Open")){
                    if (editSo.equals("true")) {
                        showRemoveAlert(soNumber.getText().toString());
                    }else {
                        Toast.makeText(getApplicationContext(),
                                "You Don't have permission to Delete",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Can't Delete Closed SalesOrder",Toast.LENGTH_SHORT).show();
                }
            }
        });

        convertLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    viewCloseBottomSheet();
                    if (!salesOrderStatus.equals("Closed") && !salesOrderStatus.equals("InProgress Invoice")){
                        getSalesOrderDetails(soNumber.getText().toString(),"ConvertInvoice");
                    }else {
                        Toast.makeText(getApplicationContext(),"This Sales order already Closed",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        convertToInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    viewCloseBottomSheet();
                    if (!salesOrderStatus.equals("Closed") && !salesOrderStatus.equals("InProgress Invoice")){
                        getSalesOrderDetails(soNumber.getText().toString(),"ConvertInvoice");
                    }else {
                        Toast.makeText(getApplicationContext(),"This Sales order already Closed",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        customerNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearchCustomerNameClicked=true;
               // viewCloseBottomSheet();
                Intent intent=new Intent(getApplicationContext(),FilterCustomerListActivity.class);
                startActivityForResult(intent,FILTER_CUSTOMER_CODE);
            }
        });

        printPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  viewCloseBottomSheet();
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Intent intent=new Intent(SalesOrderListActivity.this, SalesOrderPrintPreview.class);
                intent.putExtra("soNumber",soNumber.getText().toString());
                startActivity(intent);
            }
        });

        printPreviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  viewCloseBottomSheet();
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Intent intent=new Intent(SalesOrderListActivity.this, SalesOrderPrintPreview.class);
                intent.putExtra("soNumber",soNumber.getText().toString());
                startActivity(intent);
            }
        });


        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(fromDate);
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(toDate);
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String customer_name=customerNameText.getText().toString();
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
                    isSearchCustomerNameClicked=true;
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
                        if (salesOrderStatusSpinner.getSelectedItem().equals("ALL")){
                            invoice_status="";
                        }else if (salesOrderStatusSpinner.getSelectedItem().equals("CLOSED")){
                            invoice_status="C";
                        }else if (salesOrderStatusSpinner.getSelectedItem().equals("OPEN")){
                            invoice_status="O";
                        }
                        setFilterSearch(SalesOrderListActivity.this,companyId,selectedCustomerId,invoice_status,fromDateString,toDateString);
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                    ///filterSearch(customer_name, salesOrderStatusSpinner.getSelectedItem().toString(),fromDate.getText().toString(),toDate.getText().toString());
                    salesOrderStatusSpinner.setSelection(0);
                }
            }
        });

        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearchCustomerNameClicked=false;
                customerNameText.setText("");
                fromDate.setText(formattedDate);
                toDate.setText(formattedDate);
                searchFilterView.setVisibility(View.GONE);
                salesOrderStatusSpinner.setSelection(0);
                setFilterAdapeter();
            }
        });
    }

    public void setCustomerDetails(String customerId){
        SharedPreferences sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
        SharedPreferences.Editor customerPredEdit= sharedPreferences.edit();
        customerPredEdit.putString("customerId", customerId);
        customerPredEdit.apply();
    }


   /* public void getSalesOrderDetails(String soNumber,int copy) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("SalesOrderNo", soNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SalesOrderDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        salesOrderHeaderDetails =new ArrayList<>();
        salesPrintList =new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Sales_DetailsSAP::",response.toString());
                        if (response.length()>0) {
                            SalesOrderPrintPreviewModel model = new SalesOrderPrintPreviewModel();
                            model.setSoNumber(response.optString("SoNo"));
                            model.setSoDate(response.optString("SoDateString"));
                            model.setCustomerCode(response.optString("CustomerCode"));
                            model.setCustomerName(response.optString("CustomerName"));
                            model.setAddress(response.optString("Address1"));
                            model.setDeliveryAddress(response.optString("Address1"));
                            model.setSubTotal(response.optString("SubTotal"));
                            model.setNetTax(response.optString("Tax"));
                            model.setNetTotal(response.optString("NetTotal"));
                            model.setTaxType(response.optString("TaxType"));
                            model.setTaxValue(response.optString("TaxPerc"));
                            model.setOutStandingAmount(response.optString("BalanceAmount"));
                            model.setBillDiscount(response.optString("BillDIscount"));
                            model.setItemDiscount(response.optString("ItemDiscount"));
                            JSONArray products = response.getJSONArray("SoDetails");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject object = products.getJSONObject(i);
                                if (Double.parseDouble(object.optString("LQty")) > 0) {
                                    SalesOrderPrintPreviewModel.SalesList salesListModel = new SalesOrderPrintPreviewModel.SalesList();

                                    salesListModel.setProductCode(object.optString("ProductCode"));
                                    salesListModel.setDescription(object.optString("ProductName"));
                                    salesListModel.setLqty(object.optString("LQty"));
                                    salesListModel.setCqty(object.optString("CQty"));
                                    salesListModel.setNetQty(object.optString("LQty"));
                                    salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                    salesListModel.setUnitPrice(object.optString("Price"));
                                    double qty = Double.parseDouble(object.optString("LQty"));
                                    double price = Double.parseDouble(object.optString("Price"));

                                    double nettotal = qty * price;
                                    salesListModel.setTotal(String.valueOf(nettotal));
                                    salesListModel.setPricevalue(String.valueOf(price));

                                    salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                    salesListModel.setItemtax(object.optString("Tax"));
                                    salesListModel.setSubTotal(object.optString("SubTotal"));
                                    salesPrintList.add(salesListModel);


                                    if (Double.parseDouble(object.optString("CQty")) > 0) {
                                        salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(object.optString("ProductCode"));
                                        salesListModel.setDescription(object.optString("ProductName"));
                                        salesListModel.setLqty(object.optString("LQty"));
                                        salesListModel.setCqty(object.optString("CQty"));
                                        salesListModel.setNetQty(object.optString("CQty"));

                                        double qty1 = Double.parseDouble(object.optString("CQty"));
                                        double price1 = Double.parseDouble(object.optString("CartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(object.optString("UOMCode"));
                                        salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                        salesListModel.setUnitPrice(object.optString("Price"));
                                        salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        salesListModel.setItemtax(object.optString("Tax"));
                                        salesListModel.setSubTotal(object.optString("SubTotal"));
                                        salesPrintList.add(salesListModel);
                                    }

                                    if (!object.optString("ReturnQty").isEmpty() && Double.parseDouble(object.optString("ReturnQty")) > 0) {
                                        salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(object.optString("ProductCode"));
                                        salesListModel.setDescription(object.optString("ProductName"));
                                        salesListModel.setLqty(object.optString("LQty"));
                                        salesListModel.setCqty(object.optString("CQty"));
                                        salesListModel.setNetQty(object.optString("ReturnQty"));

                                        double qty1 = Double.parseDouble(object.optString("ReturnQty"));
                                        double price1 = Double.parseDouble(object.optString("Price"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(object.optString("UOMCode"));
                                        salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                        salesListModel.setUnitPrice(object.optString("Price"));
                                        salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        salesListModel.setItemtax(object.optString("Tax"));
                                        salesListModel.setSubTotal(object.optString("SubTotal"));
                                        salesPrintList.add(salesListModel);
                                    }

                                } else {
                                    if (Double.parseDouble(object.optString("CQty")) > 0) {
                                        SalesOrderPrintPreviewModel.SalesList salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(object.optString("ProductCode"));
                                        salesListModel.setDescription(object.optString("ProductName"));
                                        salesListModel.setLqty(object.optString("LQty"));
                                        salesListModel.setCqty(object.optString("CQty"));
                                        salesListModel.setNetQty(object.optString("Qty"));
                                        salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                        salesListModel.setUnitPrice(object.optString("Price"));

                                        double qty1 = Double.parseDouble(object.optString("CQty"));
                                        double price1 = Double.parseDouble(object.optString("CartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(object.optString("UOMCode"));
                                        salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        salesListModel.setItemtax(object.optString("Tax"));
                                        salesListModel.setSubTotal(object.optString("SubTotal"));
                                        salesPrintList.add(salesListModel);


                                        if (!object.optString("ReturnQty").isEmpty() && Double.parseDouble(object.optString("ReturnQty")) > 0) {
                                            salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                            salesListModel.setProductCode(object.optString("ProductCode"));
                                            salesListModel.setDescription(object.optString("ProductName"));
                                            salesListModel.setLqty(object.optString("LQty"));
                                            salesListModel.setCqty(object.optString("CQty"));
                                            salesListModel.setNetQty("-" + object.optString("ReturnQty"));

                                            double qty12 = Double.parseDouble(object.optString("ReturnQty"));
                                            double price12 = Double.parseDouble(object.optString("Price"));
                                            double nettotal12 = qty12 * price12;
                                            salesListModel.setTotal(String.valueOf(nettotal12));
                                            salesListModel.setPricevalue(String.valueOf(price12));

                                            salesListModel.setUomCode(object.optString("UOMCode"));
                                            salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                            salesListModel.setUnitPrice(object.optString("Price"));
                                            salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                            salesListModel.setItemtax(object.optString("Tax"));
                                            salesListModel.setSubTotal(object.optString("SubTotal"));
                                            salesPrintList.add(salesListModel);
                                        }

                                    }
                                }
                            }
                            model.setSalesList(salesPrintList);
                            salesOrderHeaderDetails.add(model);
                        }
                        sentPrintDate(copy);
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
    }*/


    private void getAllUsers() throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"UserList";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url_UserList:",url);
        usersList =new ArrayList<>();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("User",userName);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try{
                        Log.w("UserListResponse:",response.toString());
                        String statusCode=response.optString("statusCode");
                        String message=response.optString("statusMessage");
                        if (statusCode.equals("1")){
                            JSONArray responseData=response.getJSONArray("responseData");
                            for (int i=0;i<responseData.length();i++){
                                JSONObject object=responseData.optJSONObject(i);
                                UserListModel model=new UserListModel();
                                model.setUserName(object.optString("userName"));
                                model.setGender(object.optString("sex"));
                                model.setJobTitle(object.optString("jobTitle"));
                                usersList.add(model);
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }
                        if (usersList.size()>0){
                            setUserListAdapter(usersList);
                        }else {
                            Toast.makeText(getApplicationContext(),"No User Found...",Toast.LENGTH_SHORT).show();
                        }
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

    public void setUserListAdapter(ArrayList<UserListModel> usersList){
        UserAdapter customAdapter=new UserAdapter(getApplicationContext(),usersList);
        salesManSpinner.setAdapter(customAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedUser=usersList.get(position).getUserName();
        Log.w("UserSelected:",selectedUser);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedUser="";
    }

    private void getSalesOrderDetails(String soNumber,int copy) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        //  jsonObject.put("CompanyCode",companyId);
        jsonObject.put("SalesOrderNo", soNumber);
        // jsonObject.put("LocationCode",locationCode);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SalesOrderDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
     //   pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
     //   pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
      //  pDialog.setTitleText("Generating Print Preview...");
      //  pDialog.setCancelable(false);
      //  pDialog.show();
        salesOrderHeaderDetails =new ArrayList<>();
        salesPrintList =new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try{
                        Log.w("Sales_Details_list:",response.toString()+jsonObject);
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray responseData=response.getJSONArray("responseData");
                            JSONObject object=responseData.optJSONObject(0);

                            SalesOrderPrintPreviewModel model=new SalesOrderPrintPreviewModel();
                            model.setSoNumber(object.optString("soNumber"));
                            model.setSoDate(object.optString("soDate"));
                            model.setCustomerCode(object.optString("customerCode"));
                            model.setCustomerName(object.optString("customerName"));
                            model.setAddress(object.optString("address1") + object.optString("address2") + object.optString("address3"));
                            model.setAddress1(object.optString("address1"));
                            model.setAddress2(object.optString("address2"));
                            model.setAddress3(object.optString("address3"));
                            model.setAddressstate(object.optString("block")+" "+object.optString("street")+" "
                                    +object.optString("city"));
                            model.setAddresssZipcode(object.optString("countryName")+" "+object.optString("state")+" "
                                    +object.optString("zipcode"));

                            // model.setDeliveryAddress(model.getAddress());
                            model.setSubTotal(object.optString("subTotal"));
                            model.setNetTax(object.optString("taxTotal"));
                            model.setNetTotal(object.optString("netTotal"));
                            model.setTaxType(object.optString("taxType"));
                            model.setTaxValue(object.optString("taxPerc"));
                            model.setOutStandingAmount(object.optString("outstandingAmount"));
                            model.setBillDiscount(object.optString("billDiscount"));
                            model.setItemDiscount(object.optString("totalDiscount"));
                            Utils.setInvoiceMode("SalesOrder");
                            String signFlag=object.optString("signFlag");
                            Log.d("cg_signflag",""+signFlag);
                            if (signFlag.equals("Y")){
                                String signature=object.optString("signature");
                                Utils.setSignature(signature);
                                createSignature();
                            }else {
                                Utils.setSignature("");
                            }

                            JSONArray detailsArray=object.optJSONArray("salesOrderDetails");
                            for (int i=0;i<detailsArray.length();i++){
                                JSONObject detailObject=detailsArray.optJSONObject(i);

                                SalesOrderPrintPreviewModel.SalesList salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                salesListModel.setProductCode(detailObject.optString("productCode"));
                                salesListModel.setDescription(detailObject.optString("productName"));
                                salesListModel.setLqty(detailObject.optString("unitQty"));
                                salesListModel.setCqty(detailObject.optString("cartonQty"));
                                salesListModel.setNetQty(detailObject.optString("quantity"));
                                salesListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                                salesListModel.setUnitPrice(detailObject.optString("price"));

                                double qty1 = Double.parseDouble(detailObject.optString("quantity"));
                                double price1 = Double.parseDouble(detailObject.optString("price"));
                                double nettotal1 = qty1 * price1;
                                salesListModel.setTotal(String.valueOf(nettotal1));
                                salesListModel.setPricevalue(String.valueOf(price1));

                                salesListModel.setUomCode(detailObject.optString("uomCode"));
                                salesListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                                salesListModel.setItemtax(detailObject.optString("totalTax"));
                                salesListModel.setSubTotal(detailObject.optString("subTotal"));
                                salesPrintList.add(salesListModel);


                                if (!detailObject.optString("ReturnQty").isEmpty() && Double.parseDouble(detailObject.optString("ReturnQty")) > 0) {
                                    salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                    salesListModel.setProductCode(detailObject.optString("ProductCode"));
                                    salesListModel.setDescription(detailObject.optString("ProductName"));
                                    salesListModel.setLqty(detailObject.optString("LQty"));
                                    salesListModel.setCqty(detailObject.optString("CQty"));
                                    salesListModel.setNetQty("-"+detailObject.optString("ReturnQty"));

                                    double qty12 = Double.parseDouble(detailObject.optString("ReturnQty"));
                                    double price12 = Double.parseDouble(detailObject.optString("Price"));
                                    double nettotal12 = qty12 * price12;
                                    salesListModel.setTotal(String.valueOf(nettotal12));
                                    salesListModel.setPricevalue(String.valueOf(price12));

                                    salesListModel.setUomCode(detailObject.optString("UOMCode"));
                                    salesListModel.setCartonPrice(detailObject.optString("CartonPrice"));
                                    salesListModel.setUnitPrice(detailObject.optString("Price"));
                                    salesListModel.setPcsperCarton(detailObject.optString("PcsPerCarton"));
                                    salesListModel.setItemtax(detailObject.optString("Tax"));
                                    salesListModel.setSubTotal(detailObject.optString("subTotal"));
                                    salesPrintList.add(salesListModel);
                                }

                                model.setSalesList(salesPrintList);
                                salesOrderHeaderDetails.add(model);
                            }
                            sentPrintDate(copy);
                           // pDialog.dismiss();
                        }else {

                        }
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

    private void createSignature(){
        if (Utils.getSignature()!=null && !Utils.getSignature().isEmpty()){
            try {
                ImageUtil.saveStamp(this,Utils.getSignature(),"Signature");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");
        super.onResume();
    }


    private void sentPrintDate(int copy) throws IOException {
        if (Utils.validatePrinterConfiguration(this,printerType,printerMacId)) {

            if (printerType.equals("TSC Printer")) {
                TSCPrinter printer = new TSCPrinter(SalesOrderListActivity.this, printerMacId, "SalesOrder");
                printer.printSalesOrder(copy, salesOrderHeaderDetails, salesPrintList);
                printer.setOnCompletionListener(() -> {
                    Utils.setSignature("");
                    Toast.makeText(getApplicationContext(), "SalesOrder printed successfully!", Toast.LENGTH_SHORT).show();
                });
            } else if (printerType.equals("Zebra Printer")) {
                ZebraPrinterActivity zebraPrinterActivity = new ZebraPrinterActivity(SalesOrderListActivity.this, printerMacId);
                zebraPrinterActivity.printSalesOrder(copy, salesOrderHeaderDetails, salesPrintList);
            }
        }
    }


    public static void filterSearch(String customerName, String invoiceStatus, String fromdate, String todate) {
        try {
            ArrayList<SalesOrderModel> filterdNames = new ArrayList<>();
            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
            Date from_date=null;
            Date to_date=null;
            try {
                from_date=sdf.parse(fromdate);
                to_date=sdf.parse(todate);
            }  catch (ParseException e) {
                e.printStackTrace();
            }
            for (SalesOrderModel model: SalesOrderAdapterNew.getSalesOrderList()){
                Date compareDate=sdf.parse(model.getDate());
                if (from_date.equals(to_date)){
                    if (from_date.equals(compareDate)){
                        if (!customerName.isEmpty()){
                            if (model.getName().toLowerCase().contains(customerName.toLowerCase())) {
                                switch (invoiceStatus) {
                                    case "ALL":
                                        filterdNames.add(model);
                                        break;
                                    case "OPEN":
                                        if (model.getStatus().equals("0")) {
                                            filterdNames.add(model);
                                        }
                                        break;
                                    case "CLOSED":
                                        if (!model.getStatus().equals("0")) {
                                            filterdNames.add(model);
                                        }
                                        break;
                                }
                                salesOrderAdapter.filterList(filterdNames);
                            }
                        }else {
                            switch (invoiceStatus) {
                                case "ALL":
                                    filterdNames.add(model);
                                    break;
                                case "OPEN":
                                    if (model.getStatus().equals("0")) {
                                        filterdNames.add(model);
                                    }
                                    break;
                                case "CLOSED":
                                    if (!model.getStatus().equals("0")) {
                                        filterdNames.add(model);
                                    }
                                    break;
                            }
                            salesOrderAdapter.filterList(filterdNames);
                        }
                    }
                } else if(compareDate.compareTo(from_date) >= 0 && compareDate.compareTo(to_date) <= 0) {
                    System.out.println("Compare date occurs after from date");
                    if (!customerName.isEmpty()){
                        if (model.getName().toLowerCase().contains(customerName.toLowerCase())) {
                            switch (invoiceStatus) {
                                case "ALL":
                                    filterdNames.add(model);
                                    break;
                                case "OPEN":
                                    if (model.getStatus().equals("0")) {
                                        filterdNames.add(model);
                                    }
                                    break;
                                case "CLOSED":
                                    if (!model.getStatus().equals("0")) {
                                        filterdNames.add(model);
                                    }
                                    break;
                            }
                            salesOrderAdapter.filterList(filterdNames);
                        }
                    }else {
                        switch (invoiceStatus) {
                            case "ALL":
                                filterdNames.add(model);
                                break;
                            case "OPEN":
                                if (model.getStatus().equals("0")) {
                                    filterdNames.add(model);
                                }
                                break;
                            case "CLOSED":
                                if (!model.getStatus().equals("0")) {
                                    filterdNames.add(model);
                                }
                                break;
                        }
                        salesOrderAdapter.filterList(filterdNames);
                    }
                }
                salesOrderAdapter.filterList(filterdNames);
            }

            Log.w("FilteredSize:",filterdNames.size()+"");

            if (filterdNames.size()>0){
                salesOrdersView.setVisibility(View.VISIBLE);
                outstandingLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
              //  setNettotal(filterdNames);
                // invoiceAdapter.filterList(filterdNames);
            }else {
                salesOrdersView.setVisibility(View.GONE);
                outstandingLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            }


       }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }


    public void setSalesOrderAdapter(Context context,ArrayList<SalesOrderModel> salesList,String invoiceStatus){
        ArrayList<SalesOrderModel> filterdNames = new ArrayList<>();
     /*   for (SalesOrderModel model: SalesOrderAdapterNew.getSalesOrderList()){
            switch (invoiceStatus) {
                case "ALL":
                    filterdNames.add(model);
                    break;
                case "OPEN":
                    if (model.getStatus().equals("0")) {
                        filterdNames.add(model);
                    }
                    break;
                case "CLOSED":
                    if (!model.getStatus().equals("0")) {
                        filterdNames.add(model);
                    }
                    break;
            }
        }*/
        salesOrdersView.setHasFixedSize(true);
        salesOrdersView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        salesOrderAdapter=new SalesOrderAdapterNew(context, salesOrdersView, salesList, new SalesOrderAdapterNew.CallBack() {
            @Override
            public void calculateNetTotal(ArrayList<SalesOrderModel> salesList) {
                setNettotalFun(salesList);
            }
            @Override
            public void showMoreOption(String salesorderId,String customerName,String status){
                customerLayout.setVisibility(View.GONE);
                salesOrderOptionLayout.setVisibility(View.VISIBLE);
                soNumber.setText(salesorderId);
                soCustomerName.setText(customerName);
                salesOrderStatus =status;
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                // viewCloseBottomSheet();
            }
        });
        salesOrdersView.setAdapter(salesOrderAdapter);
        if (salesOrderList.size()>0){
            setNettotalFun(salesOrderList);
        }

        selectedCustomerId="";
    }


    public void setNettotalFun(ArrayList<SalesOrderModel> salesOrderList){
            double net_amount=0.0;
            for (SalesOrderModel model:salesOrderList){
                if (model.getNetTotal()!=null && !model.getNetTotal().equals("null")){
                    net_amount=net_amount+Double.parseDouble(model.getNetTotal());
                }
            }
            netTotalText.setText("$ "+Utils.twoDecimalPoint(net_amount));
    }

      public void showRemoveAlert(String salesOrderId){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                // .setTitleText("Are you sure?")
                .setContentText("Are you sure want Delete SalesOrder ?")
                .setConfirmText("YES")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        try {
                            sDialog.dismiss();
                           // viewCloseBottomSheet();
                            setDeleteSalesOrder(salesOrderId);
                            if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).showCancelButton(true)
                .setCancelText("No")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }}).show();
    }



    private void getSalesOrderDetails(String soNumber,String action) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("SalesOrderNo",soNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //    EditSODetails
        //    EditSODetailsWithFOC
        String url= Utils.getBaseUrl(this) +"EditSODetailsWithFOC";
        Log.w("JsonValue:",jsonObject.toString());
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url_salesEdit:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting SalesOrder Details...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try{
                        Log.w("So_Details_edit:",response.toString());
                        if (response.length()>0){
                            String statusCode=response.optString("statusCode");
                            if (statusCode.equals("1")){
                                JSONArray salesArray=response.optJSONArray("responseData");
                                JSONObject salesObject=salesArray.optJSONObject(0);
                                String salesorder_no=salesObject.optString("soNumber");
                                String salesorder_code = salesObject.optString("code");
                                String order_no=salesObject.optString("customerReferenceNo");
                                String company_code=salesObject.optString("CompanyCode");
                                String customer_code=salesObject.optString("customerCode");
                                String customer_name=salesObject.optString("customerName");
                                String customerBill_Disc=salesObject.optString("customerDiscountPercentage");

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
                                String signFlag=salesObject.optString("signFlag");
                                Utils.setInvoiceMode("SalesOrder");
                                if (signFlag.equals("Y")){
                                    String signature=salesObject.optString("signature");
                                    Utils.setSignature(signature);
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
                                customerDetails=dbHelper.getCustomer();
                                dbHelper.removeAllItems();
                                dbHelper.removeAllInvoiceItems();
                                dbHelper.removeCustomerTaxes();
                                CustomerDetails model=new CustomerDetails();
                                model.setCustomerCode(customer_code);
                                model.setCustomerName(customer_name);
                                model.setCustomerAddress1(salesObject.optString("address1"));
                                model.setTaxPerc( salesObject.optString("taxPerc"));
                                model.setTaxType(salesObject.optString("taxType"));
                                model.setTaxCode( salesObject.optString("taxCode"));
                                ArrayList<CustomerDetails> taxList =new ArrayList<>();
                                taxList.add(model);
                                Log.w("TaxModelPrint::",model.toString());
                                dbHelper.insertCustomerTaxValues(taxList);

                                JSONArray products=salesObject.getJSONArray("salesOrderDetails");
                                for (int i=0;i<products.length();i++){
                                    JSONObject object=products.getJSONObject(i);
                                    String lqty="0.0";
                                    String cqty="0.0";
                                    if (!object.optString("unitQty").equals("null")){
                                        lqty=object.optString("unitQty");
                                    }

                                    if (!object.optString("quantity").equals("null")){
                                        cqty=object.optString("quantity");
                                    }
                                    double priceValue=0.0;
                                    String return_qty="0";
                                    double net_qty=Double.parseDouble(cqty) - Double.parseDouble(return_qty);
                                    String price_value=object.optString("price");
                                    //String price_value=object.optString("grossPrice");

                                    double return_amt=(Double.parseDouble(return_qty)*Double.parseDouble(price_value));
                                    double total1=(net_qty * Double.parseDouble(price_value));
                                    double sub_total1=total1-return_amt;

                                    long laterDate = System.currentTimeMillis();
                                    int millisec = 18000;
                                    Timestamp original = new Timestamp(laterDate);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTimeInMillis(original.getTime());
                                    cal.add(Calendar.MILLISECOND, millisec);
                                    Timestamp timeStamp = new Timestamp(cal.getTime().getTime());

                                   dbHelper.insertCreateInvoiceCartEdit(
                                            object.optString("productCode"),
                                            object.optString("productName"),
                                            object.optString("uomCode"),
                                            cqty.toString(),
                                            return_qty,
                                            String.valueOf(net_qty),
                                            object.optString("focQty"),
                                            price_value,
                                            object.optString("stockInHand"),
                                            object.optString("total"),
                                            object.optString("subTotal"),
                                            object.optString("taxAmount"),
                                            object.optString("netTotal"),
                                           "",
                                           "",
                                           "",
                                           "",
                                           "0",
                                           object.optString("minimumSellingPrice"),
                                           object.optString("stockInHand") , String.valueOf(timeStamp),
                                           object.optString("itemAllowFOC")
                                    );
                                    Log.w("ProductsLength:",products.length()+""+object.optString("itemAllowFOC"));
                                    Log.w("ActualPrintProducts:",dbHelper.numberOfRowsInInvoice()+"");
                                    if (products.length()==dbHelper.numberOfRowsInInvoice()){
                                        redirectActivity(action,customer_code,customer_name,salesorder_code
                                                ,order_no,customerBill_Disc);
                                        break;
                                    }
                                }
                            }else {
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

    public void redirectActivity(String action,String customer_code,String customer_name,String salesorder_code,
                                 String order_no,String customerBill_Disc){
        //  if (products.length()==dbHelper.numberOfRowsInInvoice()){
        Log.w("acttionSO",""+action);
        Utils.setCustomerSession(SalesOrderListActivity.this,customer_code);
        if (action.equals("Edit")){
            Intent intent=new Intent(getApplicationContext(),CreateNewInvoiceActivity.class);
            intent.putExtra("customerName",customer_name);
            intent.putExtra("customerCode",customer_code);
            intent.putExtra("editSoNumber",salesorder_code);
            intent.putExtra("customerBillDisc",customerBill_Disc);
            intent.putExtra("orderNo",order_no);
            intent.putExtra("from","SalesEdit");
            startActivity(intent);
            finish();
        }else {
            Intent intent=new Intent(getApplicationContext(),CreateNewInvoiceActivity.class);
            intent.putExtra("customerName",customer_name);
            intent.putExtra("customerCode",customer_code);
            intent.putExtra("editSoNumber",salesorder_code);
            intent.putExtra("orderNo",order_no);
            intent.putExtra("customerBillDisc",customerBill_Disc);
            Log.w("acttionSOdisc",""+customerBill_Disc);
            intent.putExtra("from","ConvertInvoice");
            startActivity(intent);
            finish();
        }
    }


/*
    public void redirectActivity(String action,String customer_code,String customer_name,String salesorder_code){
      //  if (products.length()==dbHelper.numberOfRowsInInvoice()){
            Utils.setCustomerSession(SalesOrderListActivity.this,customer_code);
            if (action.equals("Edit")){
                if (createInvoiceSetting.equals("true")){
                    Intent intent=new Intent(getApplicationContext(),CreateNewInvoiceActivity.class);
                    intent.putExtra("customerName",customer_name);
                    intent.putExtra("customerCode",customer_code);
                    intent.putExtra("editSoNumber",salesorder_code);
                    intent.putExtra("from","SalesEdit");
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent=new Intent(SalesOrderListActivity.this,AddInvoiceActivity.class);
                    intent.putExtra("billDiscount",bill_discount);
                    intent.putExtra("itemDiscount",item_discount);
                    intent.putExtra("subTotal",sub_total);
                    intent.putExtra("customerId",customer_code);
                    intent.putExtra("soNumber",salesorder_code);
                    intent.putExtra("activityFrom","SalesEdit");
                    startActivity(intent);
                    finish();
                }
            }else {

                if (createInvoiceSetting.equals("true")){
                    Intent intent=new Intent(getApplicationContext(),CreateNewInvoiceActivity.class);
                    intent.putExtra("customerName",customer_name);
                    intent.putExtra("customerCode",customer_code);
                    intent.putExtra("editSoNumber",salesorder_code);
                    intent.putExtra("from","ConvertInvoice");
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent=new Intent(SalesOrderListActivity.this,AddInvoiceActivity.class);
                    intent.putExtra("billDiscount",bill_discount);
                    intent.putExtra("itemDiscount",item_discount);
                    intent.putExtra("subTotal",sub_total);
                    intent.putExtra("customerId",customer_code);
                    intent.putExtra("soNumber",salesorder_code);
                    intent.putExtra("soDate",so_date);
                    intent.putExtra("activityFrom","ConvertInvoice");
                    startActivity(intent);
                    finish();
                }

            }
       // }
    }
*/



    private void setDeleteSalesOrder(String soNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("SoNo",soNumber);
        jsonObject.put("SoStatus","O");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"CancellationDocument";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Deleting SalesOrder...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        Log.w("SaleOrder_Response_is:",response.toString());
                       // {
                        //    "statusCode": 1,
                            //    "statusMessage": "Invoice Cancelled Successfully",
                             //   "responseData": {
                            //"docNum": "",
                              //      "error": null
                       // }
                        if (response.length()>0){
                            String statusCode=response.optString("statusCode");
                            String message=response.optString("statusMessage");
                          //  boolean isDeleted=response.optBoolean("IsDeleted");
                            if (statusCode.equals("1")){
                                Toast.makeText(getApplicationContext(),"Sales Order deleted Success...!",Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(getIntent());
                            }else {
                                Toast.makeText(getApplicationContext(),"Error in Deleting SalesOrder",Toast.LENGTH_LONG).show();
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

    public void setFilterSearch(Context context, String companyId, String customerCode, String status, String fromdate, String todate) throws JSONException {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
       // {"CustomerCode":"","ReceiptNo":"","StartDate":"","EndDate":,"CompanyCode":"1"}
        JSONObject jsonObject=new JSONObject();
        if (selectedUser!=null && !selectedUser.isEmpty()){
            jsonObject.put("User",selectedUser);
        }else {
            jsonObject.put("User",userName);
        }
        jsonObject.put("CustomerCode",customerCode);
        jsonObject.put("FromDate",fromdate);
        jsonObject.put("ToDate", todate);
        jsonObject.put("DocStatus",status);
        // Initialize a new JsonArrayRequest instance
        String url = Utils.getBaseUrl(this) + "SalesOrderList";
        Log.w("Given_url_FilterSearch:",url+"-"+jsonObject.toString());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting SalesOrders...");
        pDialog.setCancelable(false);
        pDialog.show();

        salesOrderList=new ArrayList<>();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        Log.w("Response_Filter:",response.toString());
                        pDialog.dismiss();
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray salesOrderArray=response.optJSONArray("responseData");
                            for (int i=0;i<salesOrderArray.length();i++){
                                JSONObject object=salesOrderArray.optJSONObject(i);
                                SalesOrderModel model=new SalesOrderModel();
                                model.setName(object.optString("customerName"));
                                model.setDate(object.optString("soDate"));
                                model.setBalance(object.optString("balance"));
                                model.setSaleOrderNumber(object.optString("soNumber"));
                                model.setAddress(object.optString("Address1"));
                                model.setNetTotal(object.optString("netTotal"));
                                model.setStatus(object.optString("soStatus"));
                                model.setSalesOrderCode(object.optString("code"));
                                //  isFound=invoiceObject.optString("ErrorMessage");

                               // netTotalApi +=Double.parseDouble(object.optString("netTotal"));
                               // netTotalText.setText("$ "+Utils.twoDecimalPoint(netTotalApi));

                                ArrayList<SalesOrderPrintPreviewModel.SalesList> salesLists=new ArrayList<>();
                                model.setSalesList(salesLists);
                                salesOrderList.add(model);

                            }
                           // salesOrderAdapter.notifyDataSetChanged();
                          //  salesOrderAdapter.setLoaded();
                            setShowHide();
                        }else {
                            setShowHide();
                            //Toast.makeText(getApplicationContext(),"Error in getting SalesOrder Data",Toast.LENGTH_LONG).show();
                        }
                        setSalesOrderAdapter(context,salesOrderList, status);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
            Toast.makeText(getApplicationContext(),"Server Error,Please try again..",Toast.LENGTH_LONG).show();
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


    public void getSalesOrderList(String companyCode,String pageNo,String fromdate,String todate) throws JSONException {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        JSONObject jsonObject=new JSONObject();
        if (selectedUser!=null && !selectedUser.isEmpty()){
            jsonObject.put("User",selectedUser);
        }else {
            jsonObject.put("User",userName);
        }
        jsonObject.put("CustomerCode","");
        jsonObject.put("FromDate",fromdate);
        jsonObject.put("ToDate", todate);
        jsonObject.put("DocStatus","");
        String url = Utils.getBaseUrl(this) + "SalesOrderList";
        Log.w("Given_url:",url+"-"+jsonObject.toString());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting Salesorder...");
        pDialog.setCancelable(false);
        if (pageNo.equals("1")){
            pDialog.show();
        }
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try{
                       // {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"WinApp","customerName":"WinApp","soNumber":"5","soStatus":"O",
                        // "soDate":"06\/08\/2021","netTotal":"26.750000","balance":"26.750000","totalDiscount":"0.000000","paidAmount":"0.000000",
                        // "contactPersonCode":"","createDate":"07\/08\/2021","updateDate":"07\/08\/2021","remark":"Add sales order through mobile"},
                        // {"customerCode":"WinApp","customerName":"WinApp","soNumber":"4","soStatus":"O","soDate":"06\/08\/2021","netTotal":"26.750000",
                        // "balance":"26.750000","totalDiscount":"0.000000","paidAmount":"0.000000","contactPersonCode":"","createDate":"07\/08\/2021",
                        // "updateDate":"07\/08\/2021","remark":""},{"customerCode":"WinApp","customerName":"WinApp","soNumber":"3","soStatus":"O",
                        // "soDate":"06\/08\/2021","netTotal":"26.750000","balance":"26.750000","totalDiscount":"0.000000","paidAmount":"0.000000",
                        // "contactPersonCode":"","createDate":"07\/08\/2021","updateDate":"07\/08\/2021","remark":""},
                        // {"customerCode":"EC001","customerName":"Exclusive customer2","soNumber":"2","soStatus":"O","soDate":"07\/08\/2021",
                        // "netTotal":"45762284.350000","balance":"45762284.350000","totalDiscount":"432004.950000","paidAmount":"0.000000",
                        // "contactPersonCode":"0","createDate":"07\/08\/2021","updateDate":"07\/08\/2021","remark":""},
                        // {"customerCode":"CUS\/686","customerName":"VH FACTORY","soNumber":"1","soStatus":"O","soDate":"07\/08\/2021","netTotal":"524.300000",
                        // "balance":"524.300000","totalDiscount":"0.000000","paidAmount":"0.000000","contactPersonCode":"0","createDate":"07\/08\/2021",
                        // "updateDate":"07\/08\/2021","remark":""}]}
                        Log.w("API_For_SalesOrder:",response.toString());
                        pDialog.dismiss();
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray salesOrderArray=response.optJSONArray("responseData");
                            for (int i=0;i<salesOrderArray.length();i++){
                                JSONObject object=salesOrderArray.optJSONObject(i);
                                SalesOrderModel model=new SalesOrderModel();
                                model.setName(object.optString("customerName"));
                                model.setDate(object.optString("soDate"));
                                model.setBalance(object.optString("balance"));
                                model.setSaleOrderNumber(object.optString("soNumber"));
                                model.setAddress(object.optString("Address1"));
                                model.setNetTotal(object.optString("netTotal"));
                                model.setStatus(object.optString("soStatus"));
                                model.setSalesOrderCode(object.optString("code"));
                              //  isFound=invoiceObject.optString("ErrorMessage");
                                ArrayList<SalesOrderPrintPreviewModel.SalesList> salesLists=new ArrayList<>();
                                model.setSalesList(salesLists);
                                salesOrderList.add(model);

                            }
                            salesOrderAdapter.setLoaded();
                            salesOrderAdapter.notifyDataSetChanged();
                            setShowHide();
                        }else {
                            setShowHide();
                            //Toast.makeText(getApplicationContext(),"Error in getting SalesOrder Data",Toast.LENGTH_LONG).show();
                        }
                      /*  if (response.length()>0) {
                            for (int i = 0; i < response.length(); i++) {
                                // Get current json object
                                JSONObject invoiceObject = response.getJSONObject(i);
                                SalesOrderModel model=new SalesOrderModel();
                                model.setName(invoiceObject.optString("CustomerName"));
                                model.setDate(invoiceObject.optString("SoDateString"));
                                model.setBalance(invoiceObject.optString("BalanceAmount"));
                                model.setSaleOrderNumber(invoiceObject.optString("SoNo"));
                                model.setAddress(invoiceObject.optString("Address1"));
                                model.setNetTotal(invoiceObject.optString("NetTotal"));
                                model.setStatus(invoiceObject.optString("Status"));
                                isFound=invoiceObject.optString("ErrorMessage");
                                ArrayList<SalesOrderPrintPreviewModel.SalesList> salesLists=new ArrayList<>();
                                model.setSalesList(salesLists);
                                salesOrderList.add(model);
                            }

                          *//*  if (salesOrderList.size()>0){
                                salesOrdersView.setVisibility(View.VISIBLE);
                                emptyLayout.setVisibility(View.GONE);
                                outstandingLayout.setVisibility(View.VISIBLE);
                            }else {
                                salesOrdersView.setVisibility(View.GONE);
                                emptyLayout.setVisibility(View.VISIBLE);
                                outstandingLayout.setVisibility(View.GONE);
                            }*//*

                            salesOrderAdapter.notifyDataSetChanged();
                            salesOrderAdapter.setLoaded();
                        }*/

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
            Toast.makeText(getApplicationContext(),"Server Error,Please try again..",Toast.LENGTH_LONG).show();
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

    public void setShowHide(){
        if (salesOrderList.size()>0){
            salesOrdersView.setVisibility(View.VISIBLE);
            outstandingLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }else {
            salesOrdersView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            outstandingLayout.setVisibility(View.GONE);
        }

        if (getIntent() !=null){
            printSoNumber=getIntent().getStringExtra("printSoNumber");
            noOfCopy=getIntent().getStringExtra("noOfCopy");
            if (printSoNumber!=null && !printSoNumber.isEmpty()){
                try {
                    getSalesOrderDetails(printSoNumber,Integer.parseInt(noOfCopy));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public void setFilterAdapeter(){
        salesOrdersView.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
        outstandingLayout.setVisibility(View.VISIBLE);
        salesOrdersView.setHasFixedSize(true);
        salesOrdersView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        salesOrderAdapter=new SalesOrderAdapterNew(this, salesOrdersView, salesOrderList, new SalesOrderAdapterNew.CallBack() {
            @Override
            public void calculateNetTotal(ArrayList<SalesOrderModel> salesList) {
                setNettotalFun(salesList);
            }
            @Override
            public void showMoreOption(String salesorderId,String customerName,String status){
                customerLayout.setVisibility(View.GONE);
                salesOrderOptionLayout.setVisibility(View.VISIBLE);
                soNumber.setText(salesorderId);
                soCustomerName.setText(customerName);
                salesOrderStatus =status;
                //viewCloseBottomSheet();
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

        });
        salesOrdersView.setAdapter(salesOrderAdapter);
        if (salesOrderList.size()>0){
            setNettotalFun(salesOrderList);
        }
       /* salesOrderAdapter.setOnLoadMoreListener(new SalesOrderAdapterNew.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                salesOrderList.add(null);
                salesOrderAdapter.notifyItemInserted(salesOrderList.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");

                        //Remove loading item
                        salesOrderList.remove(salesOrderList.size() - 1);
                        salesOrderAdapter.notifyItemRemoved(salesOrderList.size());
                        //Load data
                        int index = salesOrderList.size();
                        int end = index + 20;
                        pageNo=pageNo+1;
                      //  getSalesOrderList(companyId, String.valueOf(pageNo));
                    }
                }, 5000);
            }
        });*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sorting_menu, menu);
      //  MenuItem action_save = menu.findItem(R.id.action_filter);
       // action_save.setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//finish();
            onBackPressed();

         /*   case R.id.action_remove:
                showRemoveAlert();
                break;*/
        }else if (item.getItemId()==R.id.action_customer_name){
            Collections.sort(salesOrderList, new Comparator<SalesOrderModel>(){
                public int compare(SalesOrderModel obj1, SalesOrderModel obj2) {
                    // ## Ascending order
                    return obj1.getName().compareToIgnoreCase(obj2.getName()); // To compare string values
                    // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                    // ## Descending order
                    // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                    // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                }
            });
            salesOrderAdapter.notifyDataSetChanged();
        }else if (item.getItemId()==R.id.action_amount){
            Collections.sort(salesOrderList, new Comparator<SalesOrderModel>(){
                public int compare(SalesOrderModel obj1, SalesOrderModel obj2) {
                    // ## Ascending order
                    //  return obj1.getNetTotal().compareToIgnoreCase(obj2.getNetTotal()); // To compare string values
                    return Double.valueOf(obj1.getNetTotal()).compareTo(Double.valueOf(obj2.getNetTotal())); // To compare integer values

                    // ## Descending order
                    // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                    // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                }
            });
            salesOrderAdapter.notifyDataSetChanged();
        }else if (item.getItemId()==R.id.action_date){

            try {
                Collections.sort(salesOrderList, new Comparator<SalesOrderModel>(){
                    public int compare(SalesOrderModel obj1, SalesOrderModel obj2) {
                        SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");
                        // Get the two dates to be compared
                        Date d1 = null;
                        Date d2=null;
                        try {
                            d1 = sdfo.parse(obj1.getDate());
                            d2 = sdfo.parse(obj2.getDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // ## Ascending order
                        //  return obj1.getNetTotal().compareToIgnoreCase(obj2.getNetTotal()); // To compare string values
                        return d1.compareTo(d2); // To compare integer values

                        // ## Descending order
                        // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                        // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                    }
                });
                salesOrderAdapter.notifyDataSetChanged();

            }catch (Exception ex){
                Log.w("Error:",ex.getMessage());
            }
        }else if (item.getItemId()==R.id.action_add){

            Intent intent=new Intent(getApplicationContext(),CustomerListActivity.class);
            intent.putExtra("from","so");
            startActivityForResult(intent,customerSelectCode);

          /*  isSearchCustomerNameClicked=false;
            addnewCustomer=true;
            SharedPreferences sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
                String selectCustomerId = sharedPreferences.getString("customerId", "");
                if (selectCustomerId!=null && !selectCustomerId.isEmpty()) {
                    customerDetails = dbHelper.getCustomer(selectCustomerId);
                    if (customerDetails.size()>0){
                        showCustomerDialog(this,customerDetails.get(0).getCustomerName(),customerDetails.get(0).getCustomerCode(),customerDetails.get(0).getCustomerAddress1());
                    }else {
                        customerLayout.setVisibility(View.VISIBLE);
                        searchFilterView.setVisibility(View.GONE);
                        salesOrderOptionLayout.setVisibility(View.GONE);
                        //viewCloseBottomSheet();
                        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    }
                }else {
                    customerLayout.setVisibility(View.VISIBLE);
                    searchFilterView.setVisibility(View.GONE);
                    salesOrderOptionLayout.setVisibility(View.GONE);
                    //viewCloseBottomSheet();
                    if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }*/


        } else if (item.getItemId() == R.id.action_barcode) {
            Intent intent=new Intent(getApplicationContext(), BarCodeScanner.class);
            startActivity(intent);
        }else if (item.getItemId()==R.id.action_filter){
            if (searchFilterView.getVisibility()==View.VISIBLE){
                searchFilterView.setVisibility(View.GONE);
                customerNameText.setText("");
                isSearchCustomerNameClicked=false;
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                //slideUp(searchFilterView);
            }else {
                customerNameText.setText("");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == customerSelectCode) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("customerCode");
                Utils.setCustomerSession(this,result);
                Intent intent=new Intent(SalesOrderListActivity.this, AddInvoiceActivityOld.class);
                intent.putExtra("customerId",result);
                intent.putExtra("activityFrom","SalesOrder");
                startActivity(intent);
               // finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }else if (requestCode == FILTER_CUSTOMER_CODE && resultCode==Activity.RESULT_OK){
            selectedCustomerId=data.getStringExtra("customerCode");
            String selectCustomerName=data.getStringExtra("customerName");
            customerNameText.setText(selectCustomerName);
        }
    } //onActivityResult

    private void showCustomerDialog(Activity activity,String customer_name,String customer_code,String desc) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.cutom_alert_dialog, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        TextView customerName=dialogView.findViewById(R.id.customer_name_value);
        TextView description=dialogView.findViewById(R.id.description);

        customerName.setText(customer_name+" - "+customer_code);
        description.setText("Do you want to continue this customer ?");

        Button yesButton=dialogView.findViewById(R.id.buttonYes);
        Button noButton=dialogView.findViewById(R.id.buttonNo);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        alertDialog.setCancelable(false);
        alertDialog.show();
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                dbHelper.removeCustomer();
                dbHelper.removeAllItems();
                AddInvoiceActivityOld.customerId=customer_code;
                setCustomerDetails(customer_code);
                selectedCustomerId=customer_code;
                redirectInvoice=false;
                Intent intent=new Intent(SalesOrderListActivity.this, AddInvoiceActivityOld.class);
                intent.putExtra("customerId",customer_code);
                intent.putExtra("activityFrom","SalesOrder");
                startActivity(intent);
                finish();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                customerLayout.setVisibility(View.VISIBLE);
                salesOrderOptionLayout.setVisibility(View.GONE);
                searchFilterView.setVisibility(View.GONE);
              //  viewCloseBottomSheet();
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }



    private class GetCustomersTask extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute() {
            super.onPreExecute();
            customerList=new ArrayList<>();
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + "DoINBackGround", "On doInBackground...");
            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(SalesOrderListActivity.this);
            String url= Utils.getBaseUrl(SalesOrderListActivity.this) +"MasterApi/GetCustomer_All?Requestdata={CompanyCode:"+companyId+"}";
            Log.w("Given_url:",url);
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
                                CustomerModel model=new CustomerModel();
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

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }, error -> {
                pDialog.dismiss();
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

            return "You are at PostExecute";
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressLayout.setVisibility(View.GONE);
            setAdapter(customerList);
            if (customerList.size()>0){
                dbHelper.insertCustomerList(customerList);
            }
        }
    }

    public void getCustomers(){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url=Utils.getBaseUrl(this) +"CustomerList";
        customerList=new ArrayList<>();
        //emptyTextView.setText("Customers List loading please wait...");
        Log.w("Given_url:",url);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                response -> {
                    try {
                        // pDialog.dismiss();
                        // Loop through the array elements
                        Log.w("Customer_Response:",response.toString());
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray customerDetailArray=response.optJSONArray("responseData");
                            for (int i=0;i<customerDetailArray.length();i++){
                                JSONObject object=customerDetailArray.optJSONObject(i);
                                //  if (customerObject.optBoolean("IsActive")) {
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
                                model.setBillDiscPercentage(object.optString("discountPercentage"));
                                //  model.setCustomerBarcode(object.optString("BarCode"));
                                // model.setCustomerBarcode(String.valueOf(i));
                                if (object.optString("outstandingAmount").equals("null") || object.optString("outstandingAmount").isEmpty()) {
                                    model.setOutstandingAmount("0.00");
                                } else {
                                    model.setOutstandingAmount(object.optString("outstandingAmount"));
                                }
                                customerList.add(model);
                                // }
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"Error,in getting Customer list",Toast.LENGTH_LONG).show();
                        }

                        if (customerList.size()>0){
                            customerView.setVisibility(View.VISIBLE);
                            new InsertCustomerTask().execute();
                            setAdapter(customerList);
                        }else {
                            customerView.setVisibility(View.GONE);
                            progressLayout.setVisibility(View.GONE);
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

   private class InsertCustomerTask extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround", "On doInBackground...");
            dbHelper.removeAllCustomers();
            dbHelper.insertCustomerList(customerList);
            return "You are at PostExecute";
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private void setAdapter(ArrayList<CustomerModel> customerNames) {
        customerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        customerNameAdapter = new SelectCustomerAdapter(this, customerNames, new SelectCustomerAdapter.CallBack() {
            @Override
            public void searchCustomer(String customer,String customername, int pos) {
                customerLayout.setVisibility(View.VISIBLE);
                if (isSearchCustomerNameClicked){
                    viewCloseBottomSheet();
                    //searchFilterView.setVisibility(View.GONE);
                    setCustomerDetails(customer);
                    customerNameText.setText(customername);
                    selectedCustomerId=customer;
                    redirectInvoice=false;
                }else if (addnewCustomer){
                    int count = dbHelper.numberOfRows();
                    if (count > 0) {
                        showProductDeleteAlert(customer);
                    } else {
                        viewCloseBottomSheet();
                        dbHelper.removeAllItems();
                        addnewCustomer=false;
                        setCustomerDetails(customer);
                        selectedCustomerId=customer;
                        redirectInvoice=false;
                        //Intent intent = new Intent(SalesOrderListActivity.this, AddInvoiceActivity.class);
                      //  intent.putExtra("customerId", customer);
                      //  intent.putExtra("activityFrom", "SalesOrder");
                      //  startActivity(intent);
                     //   finish();
                    }
                }
                Log.w("Customer_id:", customer);
            }
        });
        customerView.setAdapter(customerNameAdapter);
    }

    public void closeView(){
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
                        addnewCustomer=false;
                        setCustomerDetails(customerId);
                        viewCloseBottomSheet();
                        selectedCustomerId=customerId;
                        redirectInvoice=false;
                       // Intent intent=new Intent(SalesOrderListActivity.this,AddInvoiceActivity.class);
                       // intent.putExtra("customerId",customerId);
                       // intent.putExtra("activityFrom","SalesOrder");
                       // startActivity(intent);
                       // finish();
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


    private void filter(String text) {
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

        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }




    public void viewCloseBottomSheet(){
        hideKeyboard();
        if (isSearchCustomerNameClicked || addnewCustomer){
            customerLayout.setVisibility(View.VISIBLE);
            salesOrderOptionLayout.setVisibility(View.GONE);
            redirectInvoice=false;
        }else {
            customerLayout.setVisibility(View.GONE);
            salesOrderOptionLayout.setVisibility(View.VISIBLE);
        }
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        customerList=dbHelper.getAllCustomers();
        setAdapter(customerList);
        // get the Customer name from the local db
    }

    public void hideKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void getDate(EditText dateEditext){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(SalesOrderListActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateEditext.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    @Override
    public void onBackPressed() {
        //Execute your code here
       // Intent intent=new Intent(getApplicationContext(),MainActivity.class);
       // startActivity(intent);
        finish();

    }
}