package com.winapp.svcatering.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.animation.TranslateAnimation;
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
import com.winapp.svcatering.adapter.SelectCustomerAdapter;
import com.winapp.svcatering.adapter.SortAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.fragments.AllInvoices;
import com.winapp.svcatering.fragments.PaidInvoices;
import com.winapp.svcatering.fragments.UnpaidInvoices;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.InvoiceModel;
import com.winapp.svcatering.adapter.InvoiceAdapter;
import com.winapp.svcatering.model.UserRoll;
import com.winapp.svcatering.printpreview.InvoicePrintPreviewActivity;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class InvoiceListActivityCopy extends NavigationActivity implements TabLayout.OnTabSelectedListener{

    private RecyclerView invoiceListView;
    private InvoiceAdapter invoiceAdapter;
    private ArrayList<InvoiceModel> invoiceList;
    private SweetAlertDialog pDialog;
    int pageNo=1;
    private SessionManager session;
    private HashMap<String,String > user;
    private String companyId;
    //This is our tablayout
    private TabLayout tabLayout;
    //This is our viewPager
    private ViewPager viewPager;
    LinearLayout operationLayout;
    LinearLayout emptyLayout;
    private Toolbar toolbar;
    private RecyclerView lettersRecyclerview;
    private RecyclerView customerView;
    SortAdapter adapter;
    ArrayList<String> letters;
    private SearchableSpinner PartSpinner;
    ArrayList<String> PartName;
    List<String> PartId;
    public static TextView selectCustomer;
    Button btnCancel;
    TextView customerName;
    static BottomSheetBehavior behavior;
    EditText customerNameEdittext;
    SelectCustomerAdapter customerNameAdapter;
    TextView dateText;
    TextView userName;
    String companyCode;
    String customerId;
    DBHelper dbHelper;
    ArrayList<CustomerModel> customerList;
    TextView totalCustomers;
    Button cancelSheet;
    public String invStatus;

    private ArrayList<CustomerDetails> customerDetails;
    TextView netTotalText;
    static View customerLayout;
    static View salesOrderOptionLayout;
    TextView optionCancel;
    LinearLayout transLayout;
    View searchFilterView;
    EditText customerNameText;
    EditText fromDate;
    EditText toDate;
    Spinner invoiceStatus;
    private int mYear, mMonth, mDay, mHour, mMinute;
    public static boolean isSearchCustomerNameClicked=false;
    Button searchButton;
    Button cancelSearch;
    Menu menu;
    static View invoiceOptionLayout;
    public static TextView invoiceCustomerName;
    public static TextView invoiceNumber;
    FloatingActionButton editInvoice;
    FloatingActionButton deleteInvoice;
    FloatingActionButton cashCollection;
    FloatingActionButton printPreview;
    public static String invoiceCustomerCodeValue;
    public  static String invoiceCustomerValue;
    public static String  invoiceNumberValue;
    LinearLayout editInvoiceLayout;
    LinearLayout deleteInvoiceLayout;
    LinearLayout cashCollectionLayout;
    LinearLayout printPreviewLayout;
    LinearLayout duplicateInvLayout;
    FloatingActionButton duplicatinvImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_invoice_list, contentFrameLayout);
       // setContentView(R.layout.activity_invoice_list);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Invoice List");
        invoiceListView =findViewById(R.id.invoiceList);
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        operationLayout=findViewById(R.id.operation_layout);
        emptyLayout=findViewById(R.id.empty_layout);
        customerNameEdittext=findViewById(R.id.customer_search);
        netTotalText=findViewById(R.id.net_total_value);
        session=new SessionManager(this);
        user=session.getUserDetails();
        dbHelper=new DBHelper(this);
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        customerView=findViewById(R.id.customerList);
        totalCustomers=findViewById(R.id.total_customers);
        cancelSheet=findViewById(R.id.cancel_sheet);
        viewPager = findViewById(R.id.pager);
        searchFilterView=findViewById(R.id.search_filter);
        invoiceStatus=findViewById(R.id.invoice_status);
        customerNameText=findViewById(R.id.customer_name_value);
        fromDate=findViewById(R.id.from_date);
        toDate =findViewById(R.id.to_date);
        searchButton=findViewById(R.id.btn_search);
        transLayout=findViewById(R.id.trans_layout);

        customerLayout=findViewById(R.id.customer_layout);
        invoiceOptionLayout=findViewById(R.id.invoice_option);
        optionCancel=findViewById(R.id.option_cancel);
        cancelSheet=findViewById(R.id.cancel_sheet);
        cancelSearch=findViewById(R.id.btn_cancel);
        invoiceCustomerName=findViewById(R.id.invoice_name);
        invoiceNumber=findViewById(R.id.sr_no);
        editInvoice=findViewById(R.id.edit_invoice);
        deleteInvoice=findViewById(R.id.delete_invoice);
        cashCollection=findViewById(R.id.cash_collection);
        printPreview=findViewById(R.id.print_preview);

        editInvoiceLayout=findViewById(R.id.edit_invoice_layout);
        deleteInvoiceLayout=findViewById(R.id.delete_invoice_layout);
        cashCollectionLayout=findViewById(R.id.cash_collection_layout);
        printPreviewLayout=findViewById(R.id.preview_invoice_layout);
        duplicateInvLayout=findViewById(R.id.duplicate_inv_layout);
        duplicatinvImg = findViewById(R.id.duplicat_invImg);

        //Initializing the tablayout
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupViewPager(viewPager);

        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        fromDate.setText(formattedDate);
        toDate.setText(formattedDate);

        getCustomers();

        ArrayList<UserRoll> userRolls=dbHelper.getUserPermissions();
        if (userRolls.size()>0) {
            for (UserRoll roll : userRolls) {
                if (roll.getFormName().equals("Edit Invoice")){
                    if (roll.getHavePermission().equals("true")){
                        editInvoiceLayout.setVisibility(View.VISIBLE);
                    }else {
                        editInvoiceLayout.setVisibility(View.GONE);
                    }
                }else if (roll.getFormName().equals("Delete Invoice")){
                    if (roll.getHavePermission().equals("true")){
                        deleteInvoiceLayout.setVisibility(View.VISIBLE);
                    }else {
                        deleteInvoiceLayout.setVisibility(View.GONE);
                    }
                }
            }
        }

        invoiceListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        invoiceAdapter=new InvoiceAdapter(this, invoiceListView, invoiceList, new InvoiceAdapter.CallBack() {
            @Override
            public void calculateNetTotal(ArrayList<InvoiceModel> invoiceList) {
                setNettotal(invoiceList);
            }
            @Override
            public void showMoreOption(String salesorderId, String customerName ,String status) {
                invStatus =status;

               // Intent intent=new Intent(InvoiceListActivity.this,CashCollectionActivity.class);
               // startActivity(intent);

            }
        });
        invoiceListView.setAdapter(invoiceAdapter);
        invoiceAdapter.setOnLoadMoreListener(new InvoiceAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                invoiceList.add(null);
                invoiceAdapter.notifyItemInserted(invoiceList.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Remove loading item
                        invoiceList.remove(invoiceList.size() - 1);
                        invoiceAdapter.notifyItemRemoved(invoiceList.size());
                        pageNo=pageNo+1;
                      //  getInvoices(companyId, String.valueOf(pageNo));

                    }
                }, 5000);
            }
        });

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
                        if (invoiceOptionLayout.getVisibility()==View.VISIBLE){
                            getSupportActionBar().setTitle("Select Option");
                        }else {
                            getSupportActionBar().setTitle("Select Customer");
                        }
                        transLayout.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        getSupportActionBar().setTitle("Invoices");
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

        cancelSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

         invoiceStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
                ((TextView) parent.getChildAt(0)).setTextSize(12);
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

         customerNameText.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 isSearchCustomerNameClicked=true;
                 viewCloseBottomSheet();
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
                 SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
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
                     isSearchCustomerNameClicked=false;
                   //  AllInvoices.filterSearch(customer_name,invoiceStatus.getSelectedItem().toString(),fromDate.getText().toString(),toDate.getText().toString());
                     invoiceStatus.setSelection(0);
                 }
             }
         });

         cancelSearch.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 isSearchCustomerNameClicked=false;
                 customerNameText.setText("");
                 invoiceStatus.setSelection(0);
                 fromDate.setText(formattedDate);
                 toDate.setText(formattedDate);
                 searchFilterView.setVisibility(View.GONE);
                 invoiceStatus.setSelection(0);
                 AllInvoices invoices=new AllInvoices();
                 invoices.filterCancel();
             }
         });

         optionCancel.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 viewCloseBottomSheet();
             }
         });

         editInvoiceLayout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 try {
                     viewCloseBottomSheet();
                     getInvoiceDetails(invoiceNumberValue);
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }
         });


         editInvoice.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 try {
                     viewCloseBottomSheet();
                     getInvoiceDetails(invoiceNumberValue);
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }
         });

         deleteInvoiceLayout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 showRemoveAlert(invoiceNumberValue);
             }
         });

         deleteInvoice.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 showRemoveAlert(invoiceNumberValue);
             }
         });

         cashCollectionLayout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 viewCloseBottomSheet();
                 Intent intent=new Intent(InvoiceListActivityCopy.this, CashCollectionActivity.class);
                 intent.putExtra("customerCode",invoiceCustomerCodeValue);
                 intent.putExtra("customerName",invoiceCustomerValue);
                 startActivity(intent);
                 finish();
             }
         });

         cashCollection.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                  viewCloseBottomSheet();
                  Intent intent=new Intent(InvoiceListActivityCopy.this, CashCollectionActivity.class);
                  intent.putExtra("customerCode",invoiceCustomerCodeValue);
                  intent.putExtra("customerName",invoiceCustomerValue);
                  startActivity(intent);
                  finish();
             }
         });


         printPreview.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 viewCloseBottomSheet();
                 Intent intent=new Intent(InvoiceListActivityCopy.this, InvoicePrintPreviewActivity.class);
                 intent.putExtra("invoiceNumber",invoiceNumberValue);
                 startActivity(intent);
             }
         });

         printPreviewLayout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 viewCloseBottomSheet();
                 Intent intent=new Intent(InvoiceListActivityCopy.this, InvoicePrintPreviewActivity.class);
                 intent.putExtra("invoiceNumber",invoiceNumberValue);
                 startActivity(intent);
             }
         });

    }

    public void showRemoveAlert(String InvoiceId){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                // .setTitleText("Are you sure?")
                .setContentText("Are you sure want Delete Invoice ?")
                .setConfirmText("YES")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        try {
                            sDialog.dismiss();
                            viewCloseBottomSheet();
                            setDeleteInvoice(InvoiceId);
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

    public static void showInvoiceOption(String invoiceCustomerCode,String customerName,String invoiceNum){
        invoiceCustomerCodeValue=invoiceCustomerCode;
        invoiceCustomerValue=customerName;
        invoiceNumberValue=invoiceNum;
        customerLayout.setVisibility(View.GONE);
        invoiceOptionLayout.setVisibility(View.VISIBLE);
        invoiceNumber.setText(invoiceNum);
        invoiceCustomerName.setText(invoiceCustomerValue);
        viewCloseBottomSheet();
    }

    private void getInvoiceDetails(String invoiceNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyId);
        jsonObject.put("InvoiceNo",invoiceNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SalesApi/GetInvoiceByCode?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting Invoice Details...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Invoice_Details:",response.toString());
                        if (response.length()>0){
                            String invoice_no=response.optString("InvoiceNo");
                            String company_code=response.optString("CompanyCode");
                            String customer_code=response.optString("CustomerCode");
                            String customer_name=response.optString("CustomerName");
                            String total=response.optString("Total");
                            String sub_total=response.optString("SubTotal");
                            String tax=response.optString("Tax");
                            String net_total=response.optString("NetTotal");
                            String currency_rate=response.optString("CurrencyRate");
                            String currency_name=response.optString("CurrencyName");
                            String tax_type=response.optString("TaxType");
                            String tax_perc=response.optString("TaxPerc");
                            String tax_code=response.optString("TaxCode");
                            String phone_no=response.optString("DelPhoneNo");
                            dbHelper.removeCustomer();
                            dbHelper.insertCustomer(
                                    customer_code,
                                    customer_name,
                                    phone_no,
                                    response.optString("Address1"),
                                    response.optString("Address2"),
                                    response.optString("Address3"),
                                    response.optString("IsActive"),
                                    response.optString("HaveTax"),
                                    response.optString("TaxType"),
                                    response.optString("TaxPerc"),
                                    response.optString("TaxCode"),
                                    response.optString("CreditLimit"),
                                    "Singapore",
                                    response.optString("CurrencyCode"));

                            dbHelper.removeAllItems();
                            JSONArray products=response.getJSONArray("InvoiceDetails");
                            for (int i=0;i<products.length();i++){
                                JSONObject object=products.getJSONObject(i);

                                String retail_price="0.0";
                                String unit_price="0.0";
                                if (!object.optString("RetailPrice").equals("null")){
                                    retail_price=object.optString("RetailPrice");
                                }

                                if (!object.optString("UnitCost").equals("null")){
                                    unit_price=object.optString("UnitCost");
                                }

                                dbHelper.insertCart(
                                        object.optString("ProductCode"),
                                        object.optString("ProductName"),
                                        object.optString("CQty"),
                                        object.optString("LQty"),
                                        object.optString("SubTotal"),
                                        "",
                                        object.optString("NetTotal"),
                                        "weight",
                                        object.optString("CartonPrice"),
                                        object.optString("Price"),
                                        object.optString("PcsPerCarton"),
                                        object.optString("Tax"),
                                        object.optString("SubTotal"),
                                        object.optString("TaxType"),
                                        object.optString("FOCQty"),
                                        "",
                                        object.optString("ExchangeQty"),
                                        "",
                                        object.optString("ItemDiscount"),
                                        object.optString("ReturnQty"),
                                        "","","","",object.optString("UOMCode"),
                                        "0.00","");

                                int count=dbHelper.numberOfRows();
                                if (products.length()==count){
                                    Intent intent=new Intent(InvoiceListActivityCopy.this, AddInvoiceActivityOld.class);
                                    intent.putExtra("customerId",customer_code);
                                    intent.putExtra("invoiceNumber",invoice_no);
                                    intent.putExtra("activityFrom","InvoiceEdit");
                                    startActivity(intent);
                                    finish();
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


    private void setDeleteInvoice(String invoiceNumber) throws JSONException {
        // Initialize a new RequestQueue instance

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyId);
        jsonObject.put("InvoiceNo",invoiceNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SalesApi/DeleteInvoice?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Deleting Invoice...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    try{
                        Log.w("DelInvoice_Response_is:",response.toString());
                       // {"IsSaved":false,"Result":"pass","IsDeleted":true}
                        if (response.length()>0){
                            boolean issaved=response.optBoolean("IsSaved");
                            String result=response.optString("Result");
                            boolean isDeleted=response.optBoolean("IsDeleted");
                            if (result.equals("pass") && isDeleted){
                                finish();
                                startActivity(getIntent());
                            }else {
                                Toast.makeText(getApplicationContext(),"Error in Deleting Invoice",Toast.LENGTH_LONG).show();
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


    public void setNettotal(ArrayList<InvoiceModel> invoiceList){
        double net_amount=0.0;
        for (InvoiceModel model:invoiceList){
            if (model.getBalance()!=null && !model.getBalance().equals("null")){
                net_amount=net_amount+Double.parseDouble(model.getBalance());
            }
        }
        netTotalText.setText("$ "+Utils.twoDecimalPoint(net_amount));
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllInvoices(), "ALL");
        adapter.addFragment(new PaidInvoices(), "PAID");
        adapter.addFragment(new UnpaidInvoices(), "OUTSTANDING");
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                setMenuShowHide(tab.getPosition(),tab);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void setMenuShowHide(int pos,TabLayout.Tab tab){
        if (pos!=0){
            if (this.menu!=null){
                MenuItem action_filter = menu.findItem(R.id.action_filter);
                action_filter.setVisible(false);
            }
        }else {
            if (this.menu!=null){
                MenuItem action_filter = menu.findItem(R.id.action_filter);
                action_filter.setVisible(true);
            }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//finish();
            onBackPressed();

         /*   case R.id.action_remove:
                showRemoveAlert();
                break;*/
        }else if (item.getItemId()==R.id.action_customer_name){
            Collections.sort(invoiceList, new Comparator<InvoiceModel>(){
                public int compare(InvoiceModel obj1, InvoiceModel obj2) {
                    // ## Ascending order
                    return obj1.getName().compareToIgnoreCase(obj2.getName()); // To compare string values
                    // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                    // ## Descending order
                    // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                    // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                }
            });
            invoiceAdapter.notifyDataSetChanged();
        }else if (item.getItemId()==R.id.action_amount){
            Collections.sort(invoiceList, new Comparator<InvoiceModel>(){
                public int compare(InvoiceModel obj1, InvoiceModel obj2) {
                    // ## Ascending order
                  //  return obj1.getNetTotal().compareToIgnoreCase(obj2.getNetTotal()); // To compare string values
                     return Double.valueOf(obj1.getNetTotal()).compareTo(Double.valueOf(obj2.getNetTotal())); // To compare integer values

                    // ## Descending order
                    // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                    // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                }
            });
            invoiceAdapter.notifyDataSetChanged();
        }else if (item.getItemId()==R.id.action_date){

            try {
                Collections.sort(invoiceList, new Comparator<InvoiceModel>(){
                    public int compare(InvoiceModel obj1, InvoiceModel obj2) {
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
                invoiceAdapter.notifyDataSetChanged();
            }catch (Exception ex){
                Log.w("Error:",ex.getMessage());
            }
        }else if (item.getItemId()==R.id.action_add){
            customerDetails=dbHelper.getCustomer();
            if (customerDetails.size()>0){
                showCustomerDialog(this,customerDetails.get(0).getCustomerName(),customerDetails.get(0).getCustomerCode(),customerDetails.get(0).getCustomerAddress1());
            }else {
                customerLayout.setVisibility(View.VISIBLE);
                invoiceOptionLayout.setVisibility(View.GONE);
                customerNameText.setText("");
                searchFilterView.setVisibility(View.GONE);
                isSearchCustomerNameClicked=false;
                viewCloseBottomSheet();
            }
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
        alertDialog.setCancelable(false);
        alertDialog.show();
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                dbHelper.removeCustomer();
                dbHelper.removeAllItems();
                AddInvoiceActivityOld.customerId=customer_code;
                Intent intent=new Intent(InvoiceListActivityCopy.this, AddInvoiceActivityOld.class);
                intent.putExtra("customerId",customer_code);
                intent.putExtra("activityFrom","Invoice");
                startActivity(intent);
                finish();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                customerLayout.setVisibility(View.VISIBLE);
                invoiceOptionLayout.setVisibility(View.GONE);
                viewCloseBottomSheet();
            }
        });
    }


    @Override
    public void onBackPressed() {
        //Execute your code here
        //Intent intent=new Intent(getApplicationContext(),MainActivity.class);
       // startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sorting_menu, menu);
        this.menu=menu;
        MenuItem item = menu.findItem(R.id.action_barcode);
        item.setVisible(false);

        MenuItem addInvoice=menu.findItem(R.id.action_add);
        ArrayList<UserRoll> userRolls=helper.getUserPermissions();
        if (userRolls.size()>0) {
            for (UserRoll roll : userRolls) {
                if (roll.getFormName().equals("Add Invoice")){
                    if (roll.getHavePermission().equals("true")){
                        addInvoice.setVisible(true);
                    }else {
                        addInvoice.setVisible(false);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void getCustomers(){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"MasterApi/GetCustomer_All?Requestdata={CompanyCode:"+companyCode+"}";
        customerList=new ArrayList<>();
        Log.w("Given_url:",url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url, null,
                response -> {
                    try {
                        // pDialog.dismiss();
                        // Loop through the array elements
                        Log.w("Customer_Response:",response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            // Get current json object
                            JSONObject productObject = response.getJSONObject(i);
                            CustomerModel model=new CustomerModel();
                            model.setCustomerCode(productObject.optString("CustomerCode"));
                            model.setCustomerName(productObject.optString("CustomerName"));
                            model.setCustomerAddress(productObject.optString("Address1"));
                            if (productObject.optString("BalanceAmount").equals("null") || productObject.optString("BalanceAmount").isEmpty()){
                                model.setOutstandingAmount("0.00");
                            }else {
                                model.setOutstandingAmount(productObject.optString("BalanceAmount"));
                            }
                            customerList.add(model);
                        }
                        if (customerList.size()>0){
                            setAdapter(customerList);
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

    private void setAdapter(ArrayList<CustomerModel> customerNames) {
        customerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        customerNameAdapter = new SelectCustomerAdapter(this, customerNames, new SelectCustomerAdapter.CallBack() {
            @Override
            public void searchCustomer(String customer,String customername, int pos) {
                if (isSearchCustomerNameClicked){
                    viewCloseBottomSheet();
                  //  searchFilterView.setVisibility(View.GONE);
                    customerNameText.setText(customername);
                }else {
                    int count =dbHelper.numberOfRows();
                    if (count>0){
                        showProductDeleteAlert(customer);
                    }else {
                       // viewCloseBottomSheet();
                        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        } else {
                            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        dbHelper.removeAllItems();
                        Intent intent=new Intent(InvoiceListActivityCopy.this, AddInvoiceActivityOld.class);
                        intent.putExtra("customerId",customer);
                        intent.putExtra("activityFrom","Invoice");
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        customerView.setAdapter(customerNameAdapter);
        totalCustomers.setText(customerNames.size()+" Customers");
    }

    public void showProductDeleteAlert(String customerId){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Warning !");
        builder1.setMessage("Products in Cart will be removed..");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        } else {
                            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        dialog.cancel();
                        dbHelper.removeAllItems();
                        Intent intent=new Intent(InvoiceListActivityCopy.this, AddInvoiceActivityOld.class);
                        intent.putExtra("customerId",customerId);
                        intent.putExtra("activityFrom","Invoice");
                        startActivity(intent);
                        finish();
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

    public static void viewCloseBottomSheet(){
      //  hideKeyboard();
        if (isSearchCustomerNameClicked){
            customerLayout.setVisibility(View.VISIBLE);
            invoiceOptionLayout.setVisibility(View.GONE);
        }else {
            customerLayout.setVisibility(View.GONE);
            invoiceOptionLayout.setVisibility(View.VISIBLE);
        }
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
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

    private void filter(String text) {
        try {
            //new array list that will hold the filtered data
            ArrayList<CustomerModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (CustomerModel s : customerList) {
                //if the existing elements contains the search input
                if (s.getCustomerName().toLowerCase().contains(text.toLowerCase())) {
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

    // slide the view from its current position to below itself
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideUp(View view){
       // view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }


    public void getDate(EditText dateEditext){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(InvoiceListActivityCopy.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateEditext.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

}