package com.winapp.svcatering.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.winapp.svcatering.adapter.DeliveryOrderAdapter;
import com.winapp.svcatering.adapter.SelectCustomerAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.fragments.CustomerFragment;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.DeliveryOrderModel;
import com.winapp.svcatering.model.SalesOrderModel;
import com.winapp.svcatering.model.SalesOrderPrintPreviewModel;
import com.winapp.svcatering.printpreview.DOPrintPreview;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DeliveryOrderListActivity extends NavigationActivity implements DeliveryOrderAdapter.CallBack {

    public static RecyclerView doListView;
    public static DeliveryOrderAdapter deliveryOrderAdapter;
    private ArrayList<DeliveryOrderModel> deliveryOrderList;
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
    LinearLayout outstandingLayout;
    DBHelper dbHelper;
    TextView netTotalText;
    private SharedPreferences sharedPref_billdisc;
    private SharedPreferences.Editor myEdit;
    private ArrayList<CustomerDetails> customerDetails;
    LinearLayout transLayout;
    View customerLayout;
    View deliveryOrderOptionLayout;
    TextView soCustomerName;
    TextView doNumber;
    TextView optionCancel;
    TextView cancelSheet;
    String userName;
    FloatingActionButton editSalesOrder;
    FloatingActionButton deleteSaleOrder;
    FloatingActionButton convertToInvoice;
    FloatingActionButton printPreview;
    String locationCode;
    String deliveryOrderStatus;
    LinearLayout editLayout;
    LinearLayout deleteLayout;
    LinearLayout convertLayout;
    LinearLayout printPreviewLayout;
    boolean isSearchCustomerNameClicked;
    boolean addnewCustomer;
    private int request_cust_code=81;

    View searchFilterView;
    EditText customerNameText;
    private int mYear, mMonth, mDay, mHour, mMinute;
    EditText fromDate;
    EditText toDate;
    String oldToDatel = "";
    Date fromDatel ;
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
    boolean redirectInvoice;
    public static String selectedCustomerId = "";
    String isFound="true";
    private Button createSalesOrder;
    private int customerSelectCode=24;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_delivery_order_list, contentFrameLayout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Delivery Orders");

        doListView =findViewById(R.id.deliveryOrderList);
        dbHelper=new DBHelper(this);
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get((SessionManager.KEY_LOCATION_CODE));

        sharedPref_billdisc = getSharedPreferences("BillDiscPref", MODE_PRIVATE);
        myEdit = sharedPref_billdisc.edit();
        customerView=findViewById(R.id.customerList);
        netTotalText=findViewById(R.id.net_total);
        customerNameEdittext=findViewById(R.id.customer_search);
        transLayout=findViewById(R.id.trans_layout);
        customerLayout=findViewById(R.id.customer_layout);
        deliveryOrderOptionLayout =findViewById(R.id.sales_option);
        soCustomerName=findViewById(R.id.name);
        doNumber =findViewById(R.id.so_no);
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
        searchFilterView=findViewById(R.id.search_filterDO);
        customerNameText=findViewById(R.id.customer_name_value);
        fromDate=findViewById(R.id.from_datedo);
        toDate =findViewById(R.id.to_datedo);
        salesOrderStatusSpinner =findViewById(R.id.invoice_statusdo);
        emptyLayout=findViewById(R.id.empty_layout);
        cancelSearch=findViewById(R.id.btn_canceldo);
        searchButton=findViewById(R.id.btn_searchdo);
        outstandingLayout=findViewById(R.id.outstanding_layout);
        progressLayout=findViewById(R.id.progress_layout);
        createSalesOrder=findViewById(R.id.create_sales);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        fromDate.setText(formattedDate);
        toDate.setText(formattedDate);
        oldToDatel=toDate.getText().toString();

        try {
            fromDatel = new SimpleDateFormat("dd/MM/yyyy").parse(oldToDatel);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String toDateString = new SimpleDateFormat("yyyyMMdd").format(fromDatel);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        Log.w("Printer_Mac_Id:",printerMacId);
        Log.w("Printer_Type:",printerType);

        //dbHelper.removeAllProducts();

       /* if (getIntent() !=null){
            printSoNumber=getIntent().getStringExtra("printSoNumber");
            noOfCopy=getIntent().getStringExtra("noOfCopy");
            if (printSoNumber!=null && !printSoNumber.isEmpty()){
                try {
                    getSalesOrderDetails(printSoNumber,Integer.parseInt(noOfCopy));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }*/

   /*     customerList=dbHelper.getAllCustomers();
        if (customerList!=null && customerList.size()>0){
            setAdapter(customerList);
        }else {
            getCustomers();
            // new GetCustomersTask().execute();
        }*/


        getDeliveryOrderList("1","","",toDateString,toDateString);
        //setDOAdapter();

      /*  deliveryOrderAdapter.setOnLoadMoreListener(new DeliveryOrderAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                deliveryOrderList.add(null);
                deliveryOrderAdapter.notifyItemInserted(deliveryOrderList.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");

                        //Remove loading item
                        deliveryOrderList.remove(deliveryOrderList.size() - 1);
                        deliveryOrderAdapter.notifyItemRemoved(deliveryOrderList.size());
                        //Load data
                        int index = deliveryOrderList.size();
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
                        if (deliveryOrderOptionLayout.getVisibility()==View.VISIBLE){
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
                        getSupportActionBar().setTitle("Delivery Order");
                        transLayout.setVisibility(View.GONE);
                        if (redirectInvoice){
                            CustomerFragment.isLoad=true;
                            Intent intent=new Intent(DeliveryOrderListActivity.this, AddInvoiceActivityOld.class);
                            intent.putExtra("customerId",selectedCustomerId);
                            intent.putExtra("activityFrom","DeliveryOrder");
                            startActivity(intent);
                            finish();
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
                deliveryOrderOptionLayout.setVisibility(View.GONE);
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
                    if (!deliveryOrderStatus.equals("Closed") && !deliveryOrderStatus.equals("InProgress Invoice")){
                        getDOOrderDetails(doNumber.getText().toString(),"Edit");
                    }else {
                        Toast.makeText(getApplicationContext(),"This Delivery order already Closed",Toast.LENGTH_SHORT).show();
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
                    if (!deliveryOrderStatus.equals("Closed") && !deliveryOrderStatus.equals("InProgress Invoice")){
                        getDOOrderDetails(doNumber.getText().toString(),"Edit");
                    }else {
                        Toast.makeText(getApplicationContext(),"This Delivery order already Closed",Toast.LENGTH_SHORT).show();
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
                showRemoveAlert(doNumber.getText().toString());
            }
        });

        deleteSaleOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                showRemoveAlert(doNumber.getText().toString());
            }
        });

        convertLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    viewCloseBottomSheet();
                    if (!deliveryOrderStatus.equals("Closed")){
                        getDOOrderDetails(doNumber.getText().toString(),"ConvertInvoice");
                    }else {
                        Toast.makeText(getApplicationContext(),"This Delivery order already Closed",Toast.LENGTH_SHORT).show();
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
                    if (!deliveryOrderStatus.equals("Closed")){
                        getDOOrderDetails(doNumber.getText().toString(),"ConvertInvoice");
                    }else {
                        Toast.makeText(getApplicationContext(),"This Delivery order already Closed",Toast.LENGTH_SHORT).show();
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
                startActivityForResult(intent,request_cust_code);
            }
        });

        printPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                Intent intent=new Intent(DeliveryOrderListActivity.this, DOPrintPreview.class);
                intent.putExtra("doNumber", doNumber.getText().toString());
                startActivity(intent);
            }
        });

        printPreviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                Intent intent=new Intent(DeliveryOrderListActivity.this, DOPrintPreview.class);
                intent.putExtra("doNumber", doNumber.getText().toString());
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
                        getDeliveryOrderList("",selectedCustomerId,invoice_status,fromDateString,toDateString);
                    } catch (ParseException e) {
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
                salesOrderStatusSpinner.setSelection(0);
                fromDate.setText(formattedDate);
                toDate.setText(formattedDate);
                selectedCustomerId ="";
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
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        salesOrderHeaderDetails =new ArrayList<>();
        salesPrintList =new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        // {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"WinApp","customerName":"WinApp","soNumber":"3",
                        // "soStatus":"O","soDate":"6\/8\/2021 12:00:00 am","netTotal":"26.750000","balanceAmount":"26.750000",
                        // "totalDiscount":"0.000000","paidAmount":"0.000000","contactPersonCode":"","createDate":"7\/8\/2021 12:00:00 am",
                        // "updateDate":"7\/8\/2021 12:00:00 am","remark":"","fDocTotal":"0.000000","fTaxAmount":"0.000000",
                        // "receivedAmount":"0.000000","total":"26.750000","fTotal":"0.000000","iTotalDiscount":"0.000000",
                        // "taxTotal":"1.750000","iPaidAmount":"0.000000","currencyCode":"SGD","currencyName":"Singapore Dollar",
                        // "companyCode":"WINAPP_DEMO","docEntry":"3","address1":"SingaporeShipTo1   Changi 890323 SG","taxPercentage":"0.000000",
                        // "discountPercentage":"0.000000",
                        //
                        //
                        // "salesOrderDetails":[{"slNo":"1","companyCode":"WINAPP_DEMO","soNo":"3",
                        // "productCode":"FG\/001245","productName":"Milk","quantity":"5.000000","cartonQty":"1.000000",
                        // "price":"5.000000","currency":"SGD","taxRate":"0.000000","discountPercentage":"0.000000",
                        // "lineTotal":"26.750000","fRowTotal":"0.000000","warehouseCode":"01","salesEmployeeCode":"-1","accountCode":"400000",
                        // "taxStatus":"Y","unitPrice":"5.000000","customerCategoryNo":"","barCodes":"","totalTax":"1.750000",
                        // "fTaxAmount":"0.000000","taxCode":"","taxType":"E","taxPerc":"0.000000","uoMCode":null,"soDate":"6\/8\/2021 12:00:00 am",
                        // "dueDate":"6\/8\/2021 12:00:00 am","createDate":"7\/8\/2021 12:00:00 am","updateDate":"7\/8\/2021 12:00:00 am",
                        // "createdUser":"manager","uomCode":"Ctn","uoMName":"Carton","cartonPrice":"3000.000000","piecePrice":"0.000000",
                        // "pcsPerCarton":"100.000000","lPrice":"100.000000","unitQty":"1.000000","retailPrice":"100.000000"}]}]}
                        Log.w("Sales_Details:",response.toString());
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
                            model.setAddress2(object.optString("address2"));
                            model.setAddress3(object.optString("address3"));
                            //model.setDeliveryAddress("No Address");
                            model.setSubTotal(object.optString("subTotal"));
                            model.setNetTax(object.optString("taxTotal"));
                            model.setNetTotal(object.optString("netTotal"));
                            model.setTaxType(object.optString("taxType"));
                            model.setTaxValue(object.optString("taxPerc"));
                            model.setOutStandingAmount(object.optString("outstandingAmount"));
                            model.setBillDiscount(object.optString("billDiscount"));
                            model.setItemDiscount(object.optString("totalDiscount"));

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

                                double qty1 = Double.parseDouble(detailObject.optString("cartonQty"));
                                double price1 = Double.parseDouble(detailObject.optString("cartonPrice"));
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







                             /*   if (Double.parseDouble(detailObject.optString("unitQty"))>0){
                                    SalesOrderPrintPreviewModel.SalesList salesListModel =new SalesOrderPrintPreviewModel.SalesList();
                                    salesListModel.setProductCode(detailObject.optString("productCode"));
                                    salesListModel.setDescription( detailObject.optString("productName"));
                                    salesListModel.setLqty(detailObject.optString("unitQty"));
                                    salesListModel.setCqty(detailObject.optString("cartonQty"));
                                    salesListModel.setNetQty(detailObject.optString("unitQty"));
                                    salesListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                                    salesListModel.setUnitPrice(detailObject.optString("price"));
                                    double qty=Double.parseDouble(detailObject.optString("unitQty"));
                                    double price=Double.parseDouble(detailObject.optString("price"));

                                    double nettotal=qty * price;
                                    salesListModel.setTotal(String.valueOf(nettotal));
                                    salesListModel.setPricevalue(String.valueOf(price));

                                    salesListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                                    salesListModel.setItemtax(detailObject.optString("totalTax"));
                                    salesListModel.setSubTotal(detailObject.optString("subTotal"));
                                    salesPrintList.add(salesListModel);


                                    if (Double.parseDouble(detailObject.optString("cartonQty")) > 0) {
                                        salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(detailObject.optString("productCode"));
                                        salesListModel.setDescription(detailObject.optString("productName"));
                                        salesListModel.setLqty(detailObject.optString("unitQty"));
                                        salesListModel.setCqty(detailObject.optString("cartonQty"));
                                        salesListModel.setNetQty(detailObject.optString("cartonQty"));

                                        double qty1 = Double.parseDouble(detailObject.optString("cartonQty"));
                                        double price1 = Double.parseDouble(detailObject.optString("cartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(detailObject.optString("uomCode"));
                                        salesListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                                        salesListModel.setUnitPrice(detailObject.optString("price"));
                                        salesListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                                        salesListModel.setItemtax(detailObject.optString("totalTax"));
                                        salesListModel.setSubTotal(detailObject.optString("subTotal"));
                                        salesPrintList.add(salesListModel);
                                    }

                                    if (!detailObject.optString("ReturnQty").isEmpty() && Double.parseDouble(detailObject.optString("ReturnQty")) > 0) {
                                        salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(detailObject.optString("ProductCode"));
                                        salesListModel.setDescription(detailObject.optString("ProductName"));
                                        salesListModel.setLqty(detailObject.optString("unitQty"));
                                        salesListModel.setCqty(detailObject.optString("cartonQty"));
                                        salesListModel.setNetQty(detailObject.optString("returnQty"));

                                        double qty1 = Double.parseDouble(detailObject.optString("returnQty"));
                                        double price1 = Double.parseDouble(detailObject.optString("price"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(detailObject.optString("uomCode"));
                                        salesListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                                        salesListModel.setUnitPrice(detailObject.optString("price"));
                                        salesListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                                        salesListModel.setItemtax(detailObject.optString("totalTax"));
                                        salesListModel.setSubTotal(detailObject.optString("subTotal"));
                                        salesPrintList.add(salesListModel);
                                    }

                                }else {
                                    if (Double.parseDouble(detailObject.optString("cartonQty")) > 0) {
                                        SalesOrderPrintPreviewModel.SalesList salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(detailObject.optString("productCode"));
                                        salesListModel.setDescription(detailObject.optString("productName"));
                                        salesListModel.setLqty(detailObject.optString("unitQty"));
                                        salesListModel.setCqty(detailObject.optString("cartonQty"));
                                        salesListModel.setNetQty(detailObject.optString("quantity"));
                                        salesListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                                        salesListModel.setUnitPrice(detailObject.optString("price"));

                                        double qty1 = Double.parseDouble(detailObject.optString("cartonQty"));
                                        double price1 = Double.parseDouble(detailObject.optString("cartonPrice"));
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

                                    }*/
                                model.setSalesList(salesPrintList);
                                salesOrderHeaderDetails.add(model);
                            }
                            sentPrintDate(copy);
                            pDialog.dismiss();
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


    private void sentPrintDate(int copy) throws IOException {
        if (printerType.equals("TSC Printer")){
            //        dialog.dismiss();
//            printInvoice();
           // Toast.makeText(getApplicationContext(),"TSC printer in progress",Toast.LENGTH_SHORT).show();
            // TSCPrinter tscPrinter=new TSCPrinter(SalesOrderPrintPreview.this,printerMacId);
            //  tscPrinter.printInvoice(invoiceHeaderDetails,invoiceList);
            TSCPrinter printer=new TSCPrinter(DeliveryOrderListActivity.this,printerMacId,"DO");
          //  printer.printDeliveryOrder1(copy,salesOrderHeaderDetails,salesPrintList);
            printer.setOnCompletionListener(new TSCPrinter.OnCompletionListener() {
                @Override
                public void onCompleted() {
                    Utils.setSignature("");
                    Toast.makeText(getApplicationContext(),"Delivery Order printed successfully!",Toast.LENGTH_SHORT).show();
                }
            });
        }else if (printerType.equals("Zebra Printer")){
            ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(DeliveryOrderListActivity.this,printerMacId);
            zebraPrinterActivity.printSalesOrder(copy,salesOrderHeaderDetails,salesPrintList);
        }
    }


   /* public static void filterSearch(String customerName, String invoiceStatus, String fromdate, String todate) {
        try {
            ArrayList<DeliveryOrderModel> filterdNames = new ArrayList<>();
            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
            Date from_date=null;
            Date to_date=null;
            try {
                from_date=sdf.parse(fromdate);
                to_date=sdf.parse(todate);
            }  catch (ParseException e) {
                e.printStackTrace();
            }
            for (DeliveryOrderModel model: DeliveryOrderAdapter.getDeliveryOrderList()){
                Date compareDate=sdf.parse(model.getDate());
                if (from_date.equals(to_date)){
                    if (from_date.equals(compareDate)){
                        if (!customerName.isEmpty()){
                            if (model.getCustomerName().toLowerCase().contains(customerName.toLowerCase())) {
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
                                deliveryOrderAdapter.filterList(filterdNames);
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
                            deliveryOrderAdapter.filterList(filterdNames);
                        }
                    }
                } else if(compareDate.compareTo(from_date) >= 0 && compareDate.compareTo(to_date) <= 0) {
                    System.out.println("Compare date occurs after from date");
                    if (!customerName.isEmpty()){
                        if (model.getCustomerName().toLowerCase().contains(customerName.toLowerCase())) {
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
                            deliveryOrderAdapter.filterList(filterdNames);
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
                        deliveryOrderAdapter.filterList(filterdNames);
                    }
                }
                deliveryOrderAdapter.filterList(filterdNames);
            }

            Log.w("FilteredSize:",filterdNames.size()+"");

            if (filterdNames.size()>0){
                doListView.setVisibility(View.VISIBLE);
                outstandingLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
                //  setNettotal(filterdNames);
                // invoiceAdapter.filterList(filterdNames);
            }else {
                doListView.setVisibility(View.GONE);
                outstandingLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            }


        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }*/


    public void setSalesOrderAdapter(Context context, ArrayList<DeliveryOrderModel> salesList, String invoiceStatus){
        ArrayList<DeliveryOrderModel> filterdNames = new ArrayList<>();
        /*for (DeliveryOrderModel model: DeliveryOrderAdapter.getDeliveryOrderList()){
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
        doListView.setHasFixedSize(true);
        doListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        deliveryOrderAdapter =new DeliveryOrderAdapter(context, doListView, filterdNames,this);
        doListView.setAdapter(deliveryOrderAdapter);
    }


    public void setNettotal(ArrayList<SalesOrderModel> salesOrderList){
        try {
            double net_amount=0.0;
            for (SalesOrderModel model:salesOrderList){
                if (model.getNetTotal()!=null && !model.getNetTotal().equals("null")){
                    net_amount=net_amount+Double.parseDouble(model.getNetTotal());
                }
            }
            netTotalText.setText("$ "+Utils.twoDecimalPoint(net_amount));
        }catch (Exception ex){}
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



    private void getDOOrderDetails(String doNumber, String action) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("DeliveryOrderNo",doNumber);
        // jsonObject.put("SoNo", soNumber);
        //jsonObject.put("LocationCode",locationCode);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"DeliveryOrderDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_urlDO_detail:",url+jsonObject);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting DeliveryOrder Details...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try{
                        //  : {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"EC001","customerName":
                        //  "Exclusive customer2","soNumber":"8","soStatus":"C","soDate":"12\/8\/2021 12:00:00 am","netTotal":"529.650000",
                        //  "balanceAmount":"0.000000","totalDiscount":"0.000000","paidAmount":"529.650000","contactPersonCode":"0",
                        //  "createDate":"12\/8\/2021 12:00:00 am","updateDate":"12\/8\/2021 12:00:00 am","remark":"","fDocTotal":"0.000000",
                        //  "fTaxAmount":"0.000000","receivedAmount":"529.650000","total":"529.650000","fTotal":"0.000000",
                        //  "iTotalDiscount":"0.000000","taxTotal":"34.650000","iPaidAmount":"529.650000","currencyCode":"SGD",
                        //  "currencyName":"Singapore Dollar","companyCode":"WINAPP_DEMO","docEntry":"8","address1":"",
                        //  "taxPercentage":"0.000000","discountPercentage":"0.000000","subTotal":"495.000000","taxType":"E","taxCode":"SR",
                        //  "taxPerc":"7.000000","billDiscount":"0.000000",
                        //
                        //

                        Log.w("SalesOrder_DetailsSAP:",response.toString());
                        if (response.length()>0){

                            String statusCode=response.optString("statusCode");
                            if (statusCode.equals("1")){
                                JSONArray salesArray=response.optJSONArray("responseData");
                                JSONObject salesObject=salesArray.optJSONObject(0);
                                String do_number =salesObject.optString("doNumber");
                                String do_code = salesObject.optString("code");
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

                                JSONArray products=salesObject.getJSONArray("deliveryOrderDetails");
                      /*          for (int i=0;i<products.length();i++){
                                    JSONObject object=products.getJSONObject(i);

                                    //  "salesOrderDetails":[{"slNo":"1","companyCode":"WINAPP_DEMO",
                                    //  "soNo":"8","productCode":"FG\/001245","productName":"Milk","quantity":"5.000000","cartonQty":"1.000000",
                                    //  "price":"99.000000","currency":"SGD","taxRate":"0.000000","discountPercentage":"1.000000","lineTotal":"529.650000",
                                    //  "fRowTotal":"0.000000","warehouseCode":"01","salesEmployeeCode":"-1","accountCode":"400000","taxStatus":"Y",
                                    //  "unitPrice":"100.000000","customerCategoryNo":"","barCodes":"","totalTax":"34.650000","fTaxAmount":"0.000000",
                                    //  "taxCode":"SR","taxType":"E","taxPerc":"0.000000","uoMCode":null,"soDate":"12\/8\/2021 12:00:00 am",
                                    //  "dueDate":"12\/8\/2021 12:00:00 am","createDate":"12\/8\/2021 12:00:00 am","updateDate":"12\/8\/2021 12:00:00 am",
                                    //  "createdUser":"manager","uomCode":"Ctn","uoMName":"Carton","cartonPrice":"3000.000000","piecePrice":"0.000000",
                                    //  "pcsPerCarton":"100.000000","lPrice":"100.000000","unitQty":"1.000000","retailPrice":"100.000000",
                                    //  "netTotal":"495.000000","subTotal":"500.00000000000","purchaseTaxPerc":"1.000000","purchaseTaxRate":"7.000000",
                                    //  "taxAmount":"34.650000","purchaseTaxCode":"SR","total":"529.650000","itemDiscount":"5.000000"}]}]}

                                    String lqty="0.0";
                                    String cqty="0.0";
                                    if (!object.optString("unitQty").equals("null")){
                                        lqty=object.optString("unitQty");
                                    }

                                    if (!object.optString("quantity").equals("null")){
                                        cqty=object.optString("quantity");
                                    }

                                    double actualPrice=Double.parseDouble(object.optString("unitPrice"));

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
                                            "","",object.optString("total"),"",object.optString("uomCode"),
                                            object.optString("minimumSellingPrice")
                                            );

                                    int count=dbHelper.numberOfRows();
                                    if (products.length()==count){
                                        Utils.setCustomerSession(DeliveryOrderListActivity.this,customer_code);
                                        if (action.equals("Edit")){
                                            Intent intent=new Intent(DeliveryOrderListActivity.this,AddInvoiceActivity.class);
                                            intent.putExtra("billDiscount",bill_discount);
                                            intent.putExtra("itemDiscount",item_discount);
                                            intent.putExtra("subTotal",sub_total);
                                            intent.putExtra("customerId",customer_code);
                                            intent.putExtra("doNumber", do_code);
                                            intent.putExtra("activityFrom","DeliveryOrderEdit");
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Intent intent=new Intent(DeliveryOrderListActivity.this,AddInvoiceActivity.class);
                                            intent.putExtra("billDiscount",bill_discount);
                                            intent.putExtra("itemDiscount",item_discount);
                                            intent.putExtra("subTotal",sub_total);
                                            intent.putExtra("customerId",customer_code);
                                            intent.putExtra("doNumber", do_code);
                                            intent.putExtra("soDate",so_date);
                                            intent.putExtra("activityFrom","ConvertInvoiceFromDO");
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }*/


                                for (int i=0;i<products.length();i++){
                                    JSONObject object=products.getJSONObject(i);

                                    //  "salesOrderDetails":[{"slNo":"1","companyCode":"WINAPP_DEMO",
                                    //  "soNo":"8","productCode":"FG\/001245","productName":"Milk","quantity":"5.000000","cartonQty":"1.000000",
                                    //  "price":"99.000000","currency":"SGD","taxRate":"0.000000","discountPercentage":"1.000000","lineTotal":"529.650000",
                                    //  "fRowTotal":"0.000000","warehouseCode":"01","salesEmployeeCode":"-1","accountCode":"400000","taxStatus":"Y",
                                    //  "unitPrice":"100.000000","customerCategoryNo":"","barCodes":"","totalTax":"34.650000","fTaxAmount":"0.000000",
                                    //  "taxCode":"SR","taxType":"E","taxPerc":"0.000000","uoMCode":null,"soDate":"12\/8\/2021 12:00:00 am",
                                    //  "dueDate":"12\/8\/2021 12:00:00 am","createDate":"12\/8\/2021 12:00:00 am","updateDate":"12\/8\/2021 12:00:00 am",
                                    //  "createdUser":"manager","uomCode":"Ctn","uoMName":"Carton","cartonPrice":"3000.000000","piecePrice":"0.000000",
                                    //  "pcsPerCarton":"100.000000","lPrice":"100.000000","unitQty":"1.000000","retailPrice":"100.000000",
                                    //  "netTotal":"495.000000","subTotal":"500.00000000000","purchaseTaxPerc":"1.000000","purchaseTaxRate":"7.000000",
                                    //  "taxAmount":"34.650000","purchaseTaxCode":"SR","total":"529.650000","itemDiscount":"5.000000"}]}]}

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
                                    // String price_value=object.optString("price");
                                    String price_value=object.optString("price");

                                  //  double return_amt=(Double.parseDouble(return_qty)*Double.parseDouble(price_value));
                                    //double total1=(net_qty * Double.parseDouble(price_value));
                                    //double sub_total1=total1-return_amt;
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
                                            "0",
                                            String.valueOf(net_qty),
                                            "0",
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
                                            "0","0",
                                            object.optString("stockInHand"), String.valueOf(timeStamp),"Yes"
                                    );

                                    myEdit.putString("billDisc_amt", salesObject.optString("billDiscount"));
                                    myEdit.putString("billDisc_percent", salesObject.optString("discountPercentage"));
                                    myEdit.apply();

                                    Log.w("ProductsLength:",products.length()+"");
                                    Log.w("ActualPrintProducts:",dbHelper.numberOfRowsInInvoice()+"");
                                    if (products.length()==dbHelper.numberOfRowsInInvoice()){

                                        redirectActivity(action,customer_code,customer_name,do_code,customerBill_Disc);
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
    public void setDOAdapter() {

        doListView.setHasFixedSize(true);
        doListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        deliveryOrderAdapter = new DeliveryOrderAdapter(this, doListView, deliveryOrderList, this);
        doListView.setAdapter(deliveryOrderAdapter);
    }

    public void redirectActivity(String action,String customer_code,String customer_name,String do_code,String customerBill_Disc){
        //  if (products.length()==dbHelper.numberOfRowsInInvoice()){
        Log.w("acttionDO",""+action);
        Utils.setCustomerSession(DeliveryOrderListActivity.this,customer_code);
        if (action.equals("Edit")){
            Intent intent=new Intent(getApplicationContext(),CreateNewInvoiceActivity.class);
            intent.putExtra("customerName",customer_name);
            intent.putExtra("customerCode",customer_code);
            intent.putExtra("editDoNumber", do_code);
           // intent.putExtra("customerBillDisc", customerBill_Disc);
            intent.putExtra("from","DoEdit");
            startActivity(intent);
            finish();
        }else {
            Intent intent=new Intent(getApplicationContext(),CreateNewInvoiceActivity.class);
            intent.putExtra("customerName",customer_name);
            intent.putExtra("customerCode",customer_code);
            intent.putExtra("editDoNumber", do_code);
            intent.putExtra("editDOInvDate", do_code);

            //intent.putExtra("customerBillDisc", customerBill_Disc);
            intent.putExtra("from","ConvertInvoiceFromDO");
            Log.w("acttionDODisc",""+customerBill_Disc);

            startActivity(intent);
            finish();
        }
    }



    private void setDeleteSalesOrder(String soNumber) throws JSONException {
        // Initialize a new RequestQueue instance

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyId);
        jsonObject.put("SoNo",soNumber);
        jsonObject.put("CreateUser",userName);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SalesApi/DeleteSO?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Deleting SalesOrder...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    try{
                        Log.w("SaleOrder_Response_is:",response.toString());
                        if (response.length()>0){
                            boolean issaved=response.optBoolean("IsSaved");
                            String result=response.optString("Result");
                            boolean isDeleted=response.optBoolean("IsDeleted");
                            if (result.equals("pass") && isDeleted){
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

  /*  public void setFilterSearch(Context context, String companyId, String customerCode, String status, String fromdate, String todate) throws JSONException {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // {"CustomerCode":"","ReceiptNo":"","StartDate":"","EndDate":,"CompanyCode":"1"}
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CustomerCode",customerCode);
        jsonObject.put("ReceiptNo","");
        jsonObject.put("StartDate",fromdate);
        jsonObject.put("EndDate",todate);
        jsonObject.put("CompanyCode",companyId);
        // Initialize a new JsonArrayRequest instance
        String url=Utils.getBaseUrl(this) +"SalesApi/GetReceiptsSearchList?Requestdata="+jsonObject.toString();
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting Salesorder...");
        pDialog.setCancelable(false);
        pDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Response_Filter:",response.toString());
                        pDialog.dismiss();
                        if (response.length()>0) {
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
                                deliveryOrderList.add(model);
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

                            deliveryOrderAdapter.notifyDataSetChanged();
                            deliveryOrderAdapter.setLoaded();
                        }
                        //setShowHide();
                        pDialog.dismiss();
                        setSalesOrderAdapter(context, deliveryOrderList, status);
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
    }*/

        public void getDeliveryOrderList(String pageNo,String customerCode, String status, String fromdate, String todate) {

            // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject=new JSONObject();

            try {
                jsonObject.put("User",userName);
                jsonObject.put("CustomerCode",customerCode);
                jsonObject.put("FromDate",fromdate);
                jsonObject.put("ToDate", todate);
                jsonObject.put("DOStatus",status);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            deliveryOrderList =new ArrayList<>();

            // Initialize a new JsonArrayRequest instance
        String url = Utils.getBaseUrl(this) + "DeliveryOrderList";
        Log.w("Given_urlDoList:",url+jsonObject);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting Delivery Orders...");
        pDialog.setCancelable(false);
        if (pageNo.equals("1")){
            pDialog.show();
        }
        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        Log.w("DeliveryOdersResponse:",response.toString());
                      //  {"statusCode":1,"statusMessage":"Success",
                        //  "responseData":[{"customerCode":"C1003","customerName":"3C SUPER MART","doNumber":"4","doStatus":"O",
                        //  "doDate":"17\/08\/2021","netTotal":"100.000000","balance":"100.000000","totalDiscount":"0.000000",
                        //  "paidAmount":"0.000000","contactPersonCode":"","createDate":"20\/08\/2021","updateDate":"20\/08\/2021",
                        //  "remark":"Add Delivery order through mobile"},{"customerCode":"C1003","customerName":"3C SUPER MART",
                        //  "doNumber":"3","doStatus":"O","doDate":"17\/08\/2021","netTotal":"70.000000","balance":"70.000000",
                        //  "totalDiscount":"0.000000","paidAmount":"0.000000","contactPersonCode":"","createDate":"20\/08\/2021",
                        //  "updateDate":"20\/08\/2021","remark":"Add Delivery order through mobile"},{"customerCode":"C1002",
                        //  "customerName":"1+1MINI MART","doNumber":"2","doStatus":"O","doDate":"20\/08\/2021","netTotal":"80.250000",
                        //  "balance":"80.250000","totalDiscount":"0.000000","paidAmount":"0.000000","contactPersonCode":"0",
                        //  "createDate":"20\/08\/2021","updateDate":"20\/08\/2021","remark":""},{"customerCode":"C1001",
                        //  "customerName":"BRINDAS PTE LTD","doNumber":"1","doStatus":"C","doDate":"20\/08\/2021","netTotal":"10.490000",
                        //  "balance":"0.000000","totalDiscount":"0.000000","paidAmount":"10.490000","contactPersonCode":"0",
                        //  "createDate":"20\/08\/2021","updateDate":"20\/08\/2021","remark":"Add Delivery order through mobile"}]}
                        pDialog.dismiss();
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray salesOrderArray=response.optJSONArray("responseData");
                            for (int i=0;i<salesOrderArray.length();i++){
                                JSONObject object=salesOrderArray.optJSONObject(i);
                                DeliveryOrderModel model=new DeliveryOrderModel();
                                model.setCustomerName(object.optString("customerName"));
                                model.setCustomerCode(object.optString("customerCode"));
                                model.setDate(object.optString("doDate"));
                                model.setSubTotal(object.optString("balance"));
                                model.setDoNumber(object.optString("doNumber"));
                                model.setNetTotal(object.optString("netTotal"));
                                model.setDoStatus(object.optString("doStatus"));
                                model.setDoSign(object.optString("signFlag"));
                                model.setDoCode(object.optString("code"));
                                deliveryOrderList.add(model);
                            }
//                            deliveryOrderAdapter.notifyDataSetChanged();
                        //    deliveryOrderAdapter.setLoaded();
                            //setShowHide();
                            if (deliveryOrderList.size()>0){
                                if(deliveryOrderAdapter!=null) {
                                    deliveryOrderAdapter.notifyDataSetChanged();
                                }
                                doListView.setVisibility(View.VISIBLE);
                                emptyLayout.setVisibility(View.GONE);
                                setDOAdapter();

                                //outstandingLayout.setVisibility(View.VISIBLE);
                            }else {
                                doListView.setVisibility(View.GONE);
                                emptyLayout.setVisibility(View.VISIBLE);
                                //outstandingLayout.setVisibility(View.GONE);
                            }
                            selectedCustomerId ="";

                        }else {
                            doListView.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
                            selectedCustomerId ="";

                         // Toast.makeText(getApplicationContext(),"Error in getting SalesOrder Data",Toast.LENGTH_LONG).show();
                        }

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

    public void setShowHide(){
        if (deliveryOrderList.size()>0){
            doListView.setVisibility(View.VISIBLE);
            //outstandingLayout.setVisibility(View.VISIBLE);
        }else {
            doListView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            //outstandingLayout.setVisibility(View.GONE);
        }
    }


    public void setFilterAdapeter(){
        doListView.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
        outstandingLayout.setVisibility(View.GONE);
        doListView.setHasFixedSize(true);
        doListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        deliveryOrderAdapter =new DeliveryOrderAdapter(this, doListView, deliveryOrderList,this);

        doListView.setAdapter(deliveryOrderAdapter);

        deliveryOrderAdapter.setOnLoadMoreListener(new DeliveryOrderAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                deliveryOrderList.add(null);
                deliveryOrderAdapter.notifyItemInserted(deliveryOrderList.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");

                        //Remove loading item
                        deliveryOrderList.remove(deliveryOrderList.size() - 1);
                        deliveryOrderAdapter.notifyItemRemoved(deliveryOrderList.size());
                        //Load data
                        int index = deliveryOrderList.size();
                        int end = index + 20;
                        pageNo=pageNo+1;
                       // getSalesOrderList(companyId, String.valueOf(pageNo));
                    }
                }, 5000);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sorting_menu, menu);
        // MenuItem action_save = menu.findItem(R.id.action_filter);
        // action_save.setVisible(false);

        MenuItem action_barcode = menu.findItem(R.id.action_barcode);
        action_barcode.setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//finish();
            onBackPressed();
        }else if (item.getItemId()==R.id.action_add){
            Intent intent=new Intent(getApplicationContext(),CustomerListActivity.class);
            intent.putExtra("from","do");
            startActivityForResult(intent,customerSelectCode);
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
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("customerCode");
                Utils.setCustomerSession(this, result);
                Intent intent = new Intent(DeliveryOrderListActivity.this, AddInvoiceActivityOld.class);
                intent.putExtra("customerId", result);
                intent.putExtra("activityFrom", "DeliveryOrder");
                startActivity(intent);
                //  finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
        else if (requestCode == request_cust_code && resultCode == Activity.RESULT_OK) {
                    selectedCustomerId = data.getStringExtra("customerCode");
                    String selectCustomerName = data.getStringExtra("customerName");
                    Log.w("custCodfiltaa", "" + selectedCustomerId);
                    customerNameText.setText(selectCustomerName);
                }

    } //onActivityResult

    private void showCustomerDialog(Activity activity, String customer_name, String customer_code, String desc) {
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
              //  selectedCustomerId=customer_code;
                redirectInvoice=true;
                Intent intent=new Intent(DeliveryOrderListActivity.this, AddInvoiceActivityOld.class);
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
                deliveryOrderOptionLayout.setVisibility(View.GONE);
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

    @Override
    public void showMoreOption(String deliveryorderId, String customerName, String status) {
        customerLayout.setVisibility(View.GONE);
        deliveryOrderOptionLayout.setVisibility(View.VISIBLE);
        doNumber.setText(deliveryorderId);
        soCustomerName.setText(customerName);
        deliveryOrderStatus =status;
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
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
            RequestQueue requestQueue = Volley.newRequestQueue(DeliveryOrderListActivity.this);
            String url= Utils.getBaseUrl(DeliveryOrderListActivity.this) +"MasterApi/GetCustomer_All?Requestdata={CompanyCode:"+companyId+"}";
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
        String url= Utils.getBaseUrl(this) +"MasterApi/GetCustomer_All?Requestdata={CompanyCode:"+companyId+"}";
        customerList=new ArrayList<>();
        Log.w("Given_url:",url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.w("Response_is:", response.toString());
                        progressLayout.setVisibility(View.GONE);
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
                        setAdapter(customerList);
                     //   new SalesOrderListActivity.InsertCustomerTask().execute();
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
                    redirectInvoice=true;
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
                        redirectInvoice=true;
                        //Intent intent = new Intent(DeliveryOrderListActivity.this, AddInvoiceActivity.class);
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
                        redirectInvoice=true;
                        // Intent intent=new Intent(DeliveryOrderListActivity.this,AddInvoiceActivity.class);
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
     /*   if (isSearchCustomerNameClicked || addnewCustomer){
            customerLayout.setVisibility(View.VISIBLE);
            salesOrderOptionLayout.setVisibility(View.GONE);
        }else {
            customerLayout.setVisibility(View.GONE);
            salesOrderOptionLayout.setVisibility(View.VISIBLE);
        }*/
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(DeliveryOrderListActivity.this,
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