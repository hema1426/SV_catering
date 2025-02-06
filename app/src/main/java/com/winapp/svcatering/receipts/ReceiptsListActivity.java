package com.winapp.svcatering.receipts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
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
import android.widget.ArrayAdapter;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.AddInvoiceActivityOld;
import com.winapp.svcatering.activity.FilterCustomerListActivity;
import com.winapp.svcatering.activity.NavigationActivity;
import com.winapp.svcatering.adapter.SelectCustomerAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerGroupModel;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.utils.BarCodeScanner;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.ImageUtil;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.winapp.svcatering.zebraprinter.TSCPrinter;
import com.winapp.svcatering.zebraprinter.ZebraPrinterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReceiptsListActivity extends NavigationActivity {

    private RecyclerView receiptsListView;
    private ReceiptsListAdapter receiptsAdapter;
    private ArrayList<ReceiptsModel> receiptsList;
    private SweetAlertDialog pDialog;
    private SessionManager session;
    private HashMap<String,String > user;
    private String companyId;
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
    private TextView netTotalText;
    private ArrayList<CustomerDetails> customerDetails;
    private LinearLayout transLayout;
    private View customerLayout;
    private View receiptsOptions;
    private TextView soCustomerName;
    private TextView receiptNumber;
    private TextView optionCancel;
    private TextView cancelSheet;

    Button searchButton;
    Button cancelSearch;
    Spinner userListSpinner;
    public static LinearLayout emptyLayout;
    public static LinearLayout outstandingLayout;
    View searchFilterView;
    EditText customerNameText;
    private int mYear, mMonth, mDay, mHour, mMinute;
    EditText fromDateText;
    EditText toDateText;
    boolean isSearchCustomerNameClicked;
    boolean addnewCustomer;
    String userName;
    FrameLayout recyclerViewLayout;

    /** Items entered by the user is stored in this ArrayList variable */
    ArrayList<String> userList = new ArrayList<String>();

    /** Declaring an ArrayAdapter to set items to ListView */
    ArrayAdapter<String> userAdapter;

    LinearLayout receiptDetailsLayout;
    LinearLayout printPreviewLayout;
    LinearLayout deleteLayout;
    FloatingActionButton receiptDetails;
    FloatingActionButton printPreview;
    FloatingActionButton deleteReceipt;
    ReceiptsModel receiptsModel;

    ArrayList<String> searchableCustomerList;
    SearchableSpinner customerListSpinner;
    View progressLayout;

    private String printerMacId;
    private String printerType;
    private SharedPreferences sharedPreferences;
    public String receiptNo;
    public String noofCopy;
    private ArrayList<ReceiptPrintPreviewModel> receiptsHeaderDetails;
    private ArrayList<ReceiptPrintPreviewModel.ReceiptsDetails> receiptsPrintList;
    public String payMode;
    public String customerCode;
    public String receiptDate;
    public String customername;

    private Spinner customerGroupSpinner;
    private ArrayList<CustomerGroupModel> customersGroupList;
    ProgressDialog dialog;
    private int FILTER_CUSTOMER_CODE=134;
    private TextView customerNameTextView;
    private String selectCustomerCode="";
    private String selectCustomerName="";
    public String currentDate="";
    public String locationCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_receipts_list, contentFrameLayout);
        // setContentView(R.layout.activity_invoice_list);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Receipts");
        receiptsListView =findViewById(R.id.salesOrderList);
        dbHelper=new DBHelper(this);
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);
        customerView=findViewById(R.id.customerList);
        netTotalText=findViewById(R.id.net_total);
        customerNameEdittext=findViewById(R.id.customer_search);
        transLayout=findViewById(R.id.trans_layout);
        customerDetails=dbHelper.getCustomer();
        customerLayout=findViewById(R.id.customer_layout);
        receiptsOptions =findViewById(R.id.receipt_options_layout);
        soCustomerName=findViewById(R.id.name);
        receiptNumber =findViewById(R.id.so_no);
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
        customerNameText=findViewById(R.id.customer_name_value);
        recyclerViewLayout=findViewById(R.id.reclerview_layout);
        progressLayout=findViewById(R.id.progress_layout);
        receiptDetailsLayout=findViewById(R.id.receipt_details_layout);
        receiptDetails=findViewById(R.id.receipts_details);
        deleteLayout=findViewById(R.id.delete_receipt_layout);
        deleteReceipt=findViewById(R.id.delete_receipt);
        printPreviewLayout=findViewById(R.id.print_preview_layout);
        printPreview=findViewById(R.id.print_preview);
        customerListSpinner=findViewById(R.id.customer_list_spinner);
        customerGroupSpinner=findViewById(R.id.customer_group);
        customerNameTextView=findViewById(R.id.customer_name_text);
        customerListSpinner.setTitle("Select Customer");
        receiptsList =new ArrayList<>();

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        Log.w("Printer_Mac_Id:",printerMacId);
        Log.w("Printer_Type:",printerType);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        fromDateText.setText(formattedDate);
        toDateText.setText(formattedDate);

        userList.add(userName);

        /** Defining the ArrayAdapter to set items to Spinner Widget */
        userAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, userList);

        /** Setting the adapter to the ListView */
        userListSpinner.setAdapter(userAdapter);

        /** Adding radio buttons for the spinner items */
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        getCustomersGroups("");

      /*  customerList=dbHelper.getAllCustomers();
        if (customerList!=null && customerList.size()>0){
            setAdapter(customerList);
        }else {
            getCustomers();
        }*/

       // getCustomers();

        if (getIntent() != null){
            receiptNo =getIntent().getStringExtra("receiptNumber");
            noofCopy=getIntent().getStringExtra("noOfCopy");
            if (receiptNo !=null){
                try {
                    getReceiptsDetails(receiptNo,Integer.parseInt(noofCopy));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            Date c1 = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c1);
            SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            currentDate = df1.format(c1);
            getReceiptsList("",currentDate,currentDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

      /*  receiptsListView.setAdapter(receiptsAdapter);
        receiptsAdapter.setOrder();
        receiptsAdapter.setOnLoadMoreListener(new ReceiptsListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                receiptsList.add(null);
                receiptsAdapter.notifyItemInserted(receiptsList.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");

                        //Remove loading item
                        receiptsList.remove(receiptsList.size() - 1);
                        receiptsAdapter.notifyItemRemoved(receiptsList.size());
                        //Load data
                        int index = receiptsList.size();
                        int end = index + 20;
                        pageNo=pageNo+1;
                        try {
                            //getReceiptsList(companyId, String.valueOf(pageNo));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                        if (receiptsOptions.getVisibility()==View.VISIBLE){
                            getSupportActionBar().setTitle("Select Option");
                        }else {
                            getSupportActionBar().setTitle("Select Customer");
                        }
                        transLayout.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        getSupportActionBar().setTitle("Receipts");
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

        cancelSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        fromDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateValueSearch(fromDateText);
            }
        });

        toDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateValueSearch(toDateText);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                    String oldFromDate = fromDateText.getText().toString();
                    String oldToDate=toDateText.getText().toString();
                    Date fromDate = null;
                    Date toDate=null;
                    try {
                        fromDate = new SimpleDateFormat("dd/MM/yyyy").parse(oldFromDate);
                        toDate = new SimpleDateFormat("dd/MM/yyyy").parse(oldToDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Use SimpleDateFormat#format() to format a Date into a String in a certain pattern.

                    String fromDateString = new SimpleDateFormat("yyyyMMdd").format(fromDate);
                    String toDateString = new SimpleDateFormat("yyyyMMdd").format(toDate);
                    System.out.println(fromDateString+"-"+toDateString); // 2011-01-18

                    searchFilterView.setVisibility(View.GONE);
                    isSearchCustomerNameClicked=false;
                    try {
                        if (selectCustomerCode.isEmpty()){
                            getReceiptsList("",fromDateString,toDateString);
                        }else {
                            getReceiptsList(selectCustomerCode,fromDateString,toDateString);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //  filterSearch(customer_name, userListSpinner.getSelectedItem().toString(), fromDateString, toDateString);
                    userListSpinner.setSelection(0);
                }
            }
        });

        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearchCustomerNameClicked=false;
                customerNameText.setText("");
                userListSpinner.setSelection(0);
                fromDateText.setText(formattedDate);
                toDateText.setText(formattedDate);
                searchFilterView.setVisibility(View.GONE);
                userListSpinner.setSelection(0);
                setFilterAdapeter();
            }
        });


        printPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();

                Intent intent=new Intent(ReceiptsListActivity.this, ReceiptsPrintPreview.class);
                intent.putExtra("receiptNumber",receiptNo.toString());
                intent.putExtra("customerCode",customerCode);
                intent.putExtra("customerName",soCustomerName.getText().toString());
                intent.putExtra("date",receiptDate);
                intent.putExtra("payMode",payMode);
                startActivity(intent);
            }
        });

        printPreviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                Intent intent=new Intent(ReceiptsListActivity.this, ReceiptsPrintPreview.class);
                intent.putExtra("receiptNumber",receiptNo.toString());
                intent.putExtra("customerCode",customerCode);
                intent.putExtra("customerName",soCustomerName.getText().toString());
                intent.putExtra("date",receiptDate);
                intent.putExtra("payMode",payMode);
                startActivity(intent);
            }
        });


        deleteReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                showRemoveAlert(receiptsModel.getReceiptNumber());
            }
        });


        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                showRemoveAlert(receiptsModel.getReceiptNumber());
            }
        });

        receiptDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                showCustomerDialog(receiptsModel.getReceiptNumber(),
                        receiptsModel.getInvoiceNumber(),
                        receiptsModel.getNetTotal(),
                        receiptsModel.getNetTotal(),
                        "0.00");
            }
        });

        receiptDetailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
                showCustomerDialog(receiptsModel.getReceiptNumber(),
                        receiptsModel.getInvoiceNumber(),
                        receiptsModel.getNetTotal(),
                        receiptsModel.getNetTotal(),
                        "0.00");
            }
        });

        customerNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), FilterCustomerListActivity.class);
                startActivityForResult(intent,FILTER_CUSTOMER_CODE);
            }
        });
    }


    @Override
    protected void onResume() {
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");
        super.onResume();
    }

    public void getCustomersGroups(String groupCode){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url=Utils.getBaseUrl(this) +"BPGroupList";
        customerList=new ArrayList<>();
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("GroupCode",groupCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("Given_urlcustGroup:",url+jsonObject);
        dialog=new ProgressDialog(ReceiptsListActivity.this);
        dialog.setMessage("Loading Customers Groups...");
        dialog.setCancelable(false);
        dialog.show();
        customersGroupList=new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.w("SAP_CUSTOMERS_GROUP:", response.toString());
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray customerDetailArray=response.optJSONArray("responseData");
                            for (int i=0;i<customerDetailArray.length();i++){
                                JSONObject object=customerDetailArray.optJSONObject(i);
                                CustomerGroupModel model = new CustomerGroupModel();
                                model.setCustomerGroupCode(object.optString("groupCode"));
                                model.setCustomerGroupName(object.optString("groupName"));
                                customersGroupList.add(model);
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"Error,in getting Customer Group list",Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                        if (customersGroupList.size()>0){
                            setCustomerGroupSpinner(customersGroupList);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    dialog.dismiss();
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
    }

    public void setCustomerGroupSpinner(ArrayList<CustomerGroupModel> customersGroupList){
        ArrayAdapter<CustomerGroupModel> adapter=new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,customersGroupList);
        customerGroupSpinner.setAdapter(adapter);
        customerGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String groupCode=customersGroupList.get(position).getCustomerGroupCode();
                String groupName=customersGroupList.get(position).getCustomerGroupName();
                getCustomers(groupCode);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void sendWhatsapp(){
        PackageManager pm=getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "This is  a Test"; // Replace with your own message.
            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");
            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    // Send the Message to the particular Number
    public void openWhatsAppView(){
        PackageManager pm=getPackageManager();
        try {
            String toNumber = "+919790664487"; // Replace with mobile phone number without +Sign or leading zeros, but with country code.
            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "" + toNumber + "?body=" + ""));
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(ReceiptsListActivity.this,"it may be you dont have whats app",Toast.LENGTH_LONG).show();
        }
    }

    public void openWhatsAppWithNumber(){
        try {
            String text = "This is a test";// Replace with your message.
            String toNumber = "+919790664487"; // Replace with mobile phone number without +Sign or leading zeros, but with country code
            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void shareContent(){
        Intent shareIntent;
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Share.png";
        OutputStream out = null;
        File file=new File(path);
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        path=file.getPath();
        Uri bmpUri = Uri.parse("file://"+path);
        shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT,"Hey please check this application " + "https://play.google.com/store/apps/details?id=" +getPackageName());
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent,"Share with"));
    }



/*
    public void shareIntentSpecificApps(String articleName, String articleContent, String imageURL) {
        List<Intent> intentShareList = new ArrayList<Intent>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        //shareIntent.setType("image/*");
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(shareIntent, 0);

        for (ResolveInfo resInfo : resolveInfoList) {
            String packageName = resInfo.activityInfo.packageName;
            String name = resInfo.activityInfo.name;
            Log.d("System Out", "Package Name : " + packageName);
            Log.d("System Out", "Name : " + name);

            if (packageName.contains("com.facebook") || packageName.contains("com.whatsapp")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, articleName);
                intent.putExtra(Intent.EXTRA_TEXT, articleName + "\n" + articleContent);
               // Drawable dr = ivArticleImage.getDrawable();
              //  Bitmap bmp = ((BitmapDrawable) dr.getCurrent()).getBitmap();
                intent.putExtra(Intent.EXTRA_STREAM, ImageUtil.getLocalBitmapUri());
                intent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intentShareList.add(intent);
            }
        }

        if (intentShareList.isEmpty()) {
            Toast.makeText(this, "No apps to share !", Toast.LENGTH_SHORT).show();
        } else {
            Intent chooserIntent = Intent.createChooser(intentShareList.remove(0), "Share Articles");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentShareList.toArray(new Parcelable[]{}));
            startActivity(chooserIntent);
        }
    }
*/




    public void showRemoveAlert(String receiptNo){
        new SweetAlertDialog(ReceiptsListActivity.this, SweetAlertDialog.WARNING_TYPE)
                // .setTitleText("Are you sure?")
                .setContentText("Are you sure want Delete this Receipt ?")
                .setConfirmText("YES")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        try {
                            sDialog.dismiss();
                            setReceiptDelete(receiptNo);
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

    private void getReceiptsDetails(String receiptNumber,int copy) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject = new JSONObject();
        //   jsonObject.put("CompanyCode", companyId);
        jsonObject.put("ReceiptNo", receiptNumber);
        jsonObject.put("LocationCode",locationCode);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "ReceiptDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url);
        Log.w("JsonObjectPrint:",jsonObject.toString());
       // pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
      //  pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
      //  pDialog.setTitleText("Getting Printing Data..");
       // pDialog.setCancelable(false);
       // pDialog.show();
        receiptsHeaderDetails = new ArrayList<>();
        receiptsList = new ArrayList<>();
        receiptsPrintList=new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        Log.w("Invoice_Details:", response.toString());

                        //  {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"C1001","customerName":"BRINDAS PTE LTD",
                        //  "receiptNumber":"9","receiptStatus":"O","receiptDate":"18\/8\/2021 12:00:00 am","netTotal":"5.350000",
                        //  "balanceAmount":5.35,"totalDiscount":0,"paidAmount":0,"contactPersonCode":"","createDate":"18\/8\/2021 12:00:00 am",
                        //  "updateDate":"18\/8\/2021 12:00:00 am","remark":"","fDocTotal":0,"fTaxAmount":0,"receivedAmount":0,"total":5.35,
                        //  "fTotal":0,"iTotalDiscount":0,"taxTotal":0.35,"iPaidAmount":0,"currencyCode":"SGD","currencyName":"Singapore Dollar",
                        //  "companyCode":"AATHI_LIVE_DB","docEntry":"9","address1":null,"taxPercentage":null,"discountPercentage":null,
                        //  "subTotal":5,"taxType":"I","taxCode":"IN","taxPerc":"7.000000","billDiscount":0,"signFlag":null,"signature":null,
                        //
                        //  "receiptDetails":[{"paymentNo":"9","paymentDate":"29\/8\/2021 12:00:00 am","cardCode":"C1001",
                        //  "cardName":"BRINDAS PTE LTD","paymentTot":225.02,"invoiceNo":"25","invoiceDate":"24\/8\/2021 12:00:00 am",
                        //  "slpName":"-No Sales Employee-","invoiceTot":225.02,"totDiscount":0,"invoiceAcct":null}]}]}

                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray responseArray=response.optJSONArray("responseData");
                            JSONObject responseObject=responseArray.optJSONObject(0);
                            ReceiptPrintPreviewModel model = new ReceiptPrintPreviewModel();
                            model.setReceiptNumber(responseObject.optString("receiptNumber"));
                            model.setReceiptDate(responseObject.optString("receiptDate"));
                            model.setPayMode(responseObject.optString("payMode"));
                            model.setAddress(responseObject.optString("address1") + responseObject.optString("address2") + responseObject.optString("address3"));
                            model.setAddress1(responseObject.optString("address1"));
                            model.setAddress2(responseObject.optString("address2"));
                            model.setAddress3(responseObject.optString("address3"));
                            model.setAddressstate(responseObject.optString("block")+" "+responseObject.optString("street")+" "
                                    +responseObject.optString("city"));
                            model.setAddresssZipcode(responseObject.optString("countryName")+" "+responseObject.optString("state")+" "
                                    +responseObject.optString("zipcode"));

                            model.setCustomerCode(responseObject.optString("customerCode"));
                            model.setCustomerName(responseObject.optString("customerName"));
                            model.setTotalAmount(responseObject.optString("netTotal"));
                            model.setPaymentType(responseObject.optString("paymentType"));
                            model.setBankCode(responseObject.optString("bankCode"));
                            model.setBankName(responseObject.optString("bankName"));
                            model.setChequeDate(responseObject.optString("checkDueDate"));
                            model.setChequeNo(responseObject.optString("checkNumber"));
                            model.setBankTransferDate(responseObject.optString("bankTransferDate"));
                            model.setBalanceAmount(responseObject.optString("balanceAmount"));
                            model.setCreditAmount(responseObject.optString("totalDiscount"));
                           // model.setSignFlag(responseObject.optString("signFlag"));
                           // String signFlag = responseObject.optString("signFlag");
                            if (responseObject.optString("signature")!=null && !responseObject.optString("signature").equals("null") && !responseObject.optString("signature").isEmpty()){
                                String signature = responseObject.optString("signature");
                                Utils.setSignature(signature);
                                createSignature();
                            } else {
                                Utils.setSignature("");
                            }
                            JSONArray detailsArray=responseObject.optJSONArray("receiptDetails");
                            for (int i=0;i<detailsArray.length();i++){
                                JSONObject object=detailsArray.getJSONObject(i);
                                ReceiptPrintPreviewModel.ReceiptsDetails invoiceListModel = new ReceiptPrintPreviewModel.ReceiptsDetails();
                                invoiceListModel.setInvoiceNumber(object.optString("invoiceNo"));
                                invoiceListModel.setInvoiceDate(object.optString("invoiceDate"));
                                invoiceListModel.setAmount(object.optString("paidAmount"));
                                invoiceListModel.setDiscountAmount(object.optString("discountAmount"));
                                receiptsPrintList.add(invoiceListModel);
                            }
                            model.setReceiptsDetailsList(receiptsPrintList);
                            receiptsHeaderDetails.add(model);
                        }else {

                        }
                        printReceipt(copy);
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
    private void createSignature() {
        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
            try {
                ImageUtil.saveStamp(this, Utils.getSignature(), "Signature");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /*private void getReceiptsDetails(String receiptNumber,int copy) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("CompanyCode", companyId);
        jsonObject.put("ReceiptNo", receiptNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "SalesApi/GetReceiptsByCode?Requestdata=" + jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Receipt Print...");
        pDialog.setCancelable(false);
        pDialog.show();
        receiptsHeaderDetails = new ArrayList<>();
        receiptsPrintList = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.w("Invoice_Details:", response.toString());
                        if (response.length() > 0) {
                            ReceiptPrintPreviewModel model = new ReceiptPrintPreviewModel();
                            model.setReceiptNumber(response.optString("ReceiptNo"));
                            model.setReceiptDate(response.optString("ReceiptDateString"));
                            model.setPayMode(response.optString("Paymode"));
                            model.setCustomerCode(response.optString("CustomerCode"));
                            model.setCustomerName(response.optString("CustomerName"));
                            ReceiptPrintPreviewModel.ReceiptsDetails invoiceListModel = new ReceiptPrintPreviewModel.ReceiptsDetails();
                            invoiceListModel.setInvoiceNumber(response.optString("InvoiceNo"));
                            invoiceListModel.setInvoiceDate(response.optString("InvoiceDateString"));
                            invoiceListModel.setAmount(response.optString("PaidAmount"));

                         *//*   JSONArray products = response.getJSONArray("InvoiceDetails");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject object = products.getJSONObject(i);
                                ReceiptPrintPreviewModel.ReceiptsDetails invoiceListModel = new ReceiptPrintPreviewModel.ReceiptsDetails();


                                receiptsList.add(invoiceListModel);
                            }*//*
                            receiptsPrintList.add(invoiceListModel);
                            model.setReceiptsDetailsList(receiptsPrintList);
                            receiptsHeaderDetails.add(model);
                        }
                        pDialog.dismiss();
                        printReceipt(copy);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
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


    private void setReceiptDelete(String srNumber) throws JSONException {
        // Initialize a new RequestQueue instance

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyId);
        jsonObject.put("Code",srNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(ReceiptsListActivity.this);
        String url= Utils.getBaseUrl(ReceiptsListActivity.this) +"SalesApi/DeleteReceipts?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(ReceiptsListActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Deleting Receipt...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    try{
                        Log.w("Receipt Response:",response.toString());
                        if (response.length()>0){
                            boolean issaved=response.optBoolean("IsSaved");
                            String result=response.optString("Result");
                            boolean isDeleted=response.optBoolean("IsDeleted");
                            if (result.equals("pass") && isDeleted){
                                finish();
                                startActivity(getIntent());
                            }else {
                                Toast.makeText(getApplicationContext(),"Error in Deleting Receipt",Toast.LENGTH_LONG).show();
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

    private void setFilterAdapeter() {
        startActivity(getIntent());
        finish();
    }

    public void filterSearch(String customerName, String createUser, String fromdate, String todate) {
        try {
            ArrayList<ReceiptsModel> filterdNames = new ArrayList<>();
            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
            Date from_date=null;
            Date to_date=null;
            try {
                from_date=sdf.parse(fromdate);
                to_date=sdf.parse(todate);
            }  catch (ParseException e) {
                e.printStackTrace();
            }
            for (ReceiptsModel model: ReceiptsListAdapter.getReceiptsList()){
                Date compareDate=sdf.parse(model.getDate());
                if (from_date.equals(to_date)){
                    if (from_date.equals(compareDate)){
                        if (!customerName.isEmpty()){
                            if (model.getName().toLowerCase().contains(customerName.toLowerCase())) {
                                if (model.getUser()!=null && !model.getUser().equals("null")){
                                    if (model.getUser().equals(createUser)){
                                        filterdNames.add(model);
                                    }
                                }
                                receiptsAdapter.filterList(filterdNames);
                            }
                        }else {
                            if (model.getName().toLowerCase().contains(customerName.toLowerCase())) {
                                if (model.getUser()!=null && !model.getUser().equals("null")){
                                    if (model.getUser().equals(createUser)){
                                        filterdNames.add(model);
                                    }
                                }
                                receiptsAdapter.filterList(filterdNames);
                            }
                            receiptsAdapter.filterList(filterdNames);
                        }
                    }
                } else if(compareDate.compareTo(from_date) >= 0 && compareDate.compareTo(to_date) <= 0) {
                    System.out.println("Compare date occurs after from date");
                    if (!customerName.isEmpty()){
                        if (model.getName().toLowerCase().contains(customerName.toLowerCase())) {
                            if (model.getName().toLowerCase().contains(customerName.toLowerCase())) {
                                if (model.getUser()!=null && !model.getUser().equals("null")){
                                    if (model.getUser().equals(createUser)){
                                        filterdNames.add(model);
                                    }
                                }
                                receiptsAdapter.filterList(filterdNames);
                            }
                            receiptsAdapter.filterList(filterdNames);
                        }
                    }else {
                        if (model.getName().toLowerCase().contains(customerName.toLowerCase())) {
                            if (model.getUser()!=null && !model.getUser().equals("null")){
                                if (model.getUser().equals(createUser)){
                                    filterdNames.add(model);
                                }
                            }
                            receiptsAdapter.filterList(filterdNames);
                        }
                        receiptsAdapter.filterList(filterdNames);
                    }
                }
                receiptsAdapter.filterList(filterdNames);
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


    public void setNettotal(ArrayList<ReceiptsModel> salesOrderList){
        try {
            double net_amount=0.0;
            for (ReceiptsModel model:salesOrderList){
                if (model.getNetTotal()!=null && !model.getNetTotal().equals("null"))
                    net_amount+=Double.parseDouble(model.getNetTotal());
            }
            netTotalText.setText("$ "+ Utils.twoDecimalPoint(net_amount));
        }catch (Exception ex){}
    }

    public void getReceiptsList(String customerCode,String fromdate,String todate) throws JSONException {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("User",userName);
        jsonObject.put("LocationCode",locationCode);
        jsonObject.put("CustomerCode",customerCode);
        jsonObject.put("FromDate",fromdate);
        jsonObject.put("ToDate",todate);
        String url=Utils.getBaseUrl(this) +"ReceiptList";
        Log.w("Given_url:",url+"/"+jsonObject.toString());
        receiptsList=new ArrayList<>();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting All Receipts...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        Log.w("Response_isReceipt:",response.toString());
                        pDialog.dismiss();
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray receiptArray=response.optJSONArray("responseData");
                            for (int i=0;i<receiptArray.length();i++){
                                JSONObject receiptsObject=receiptArray.optJSONObject(i);
                                ReceiptsModel model = new ReceiptsModel();
                                model.setName(receiptsObject.optString("customerName"));
                                model.setReceiptNumber(receiptsObject.optString("receiptNo"));
                                model.setTransactionMode(receiptsObject.optString("payMode"));
                                model.setCustomerCode(receiptsObject.optString("customerCode"));
                                model.setCustomerName(receiptsObject.optString("customerName"));
                                model.setNetTotal(receiptsObject.optString("paidAmount"));
                                model.setInvoiceNumber(receiptsObject.optString("invoiceNo"));
                                model.setInvoiceDate(receiptsObject.optString("invoiceDate"));
                                model.setReceiptCode(receiptsObject.optString("code"));
                              //  model.setUser(receiptsObject.optString("CreateUser"));
                              //  model.setCreditLimit(receiptsObject.optString("Credit"));
                                model.setDate(receiptsObject.optString("receiptDate"));
                                ArrayList<ReceiptsListAdapter.InvoiceModel> invoiceList = new ArrayList<>();
                                model.setInvoiceList(invoiceList);
                                receiptsList.add(model);
                            }

                            Log.w("ReceiptsSize:",receiptsList.size()+"");
                            setNettotal(receiptsList);
                            //receiptsAdapter.notifyDataSetChanged();
                            //receiptsAdapter.setLoaded();

                            if (receiptsList.size()>0){
                                setReceiptsAdapter(receiptsList);
                                recyclerViewLayout.setVisibility(View.VISIBLE);
                                emptyLayout.setVisibility(View.GONE);
                            }else {
                                recyclerViewLayout.setVisibility(View.GONE);
                                emptyLayout.setVisibility(View.VISIBLE);
                            }
                           // if (receiptNo!=null && receiptNo.equals("pass")){
                                //getReceiptsDetails(receiptsList.get(0).getReceiptNumber(), Integer.parseInt(noofCopy));
                           // }
                        }else {
                            recyclerViewLayout.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
                        }

                /*        if (response.length()>0) {
                            for (int i = 0; i < response.length(); i++) {
                                // Get current json object
                                JSONObject receiptsObject = response.getJSONObject(i);
                                if (!receiptsObject.optString("ErrorMessage").equals("No Receipts Header Found.")) {
                                    ReceiptsModel model = new ReceiptsModel();
                                    model.setName(receiptsObject.optString("CustomerName"));
                                    model.setReceiptNumber(receiptsObject.optString("ReceiptNo"));
                                    model.setTransactionMode(receiptsObject.optString("Paymode"));
                                    model.setCustomerCode(receiptsObject.optString("CustomerCode"));
                                    model.setCustomerName(receiptsObject.optString("CustomerName"));
                                    model.setNetTotal(receiptsObject.optString("PaidAmount"));
                                    model.setInvoiceNumber(receiptsObject.optString("InvoiceNo"));
                                    model.setInvoiceDate(receiptsObject.optString("InvoiceDate"));
                                    model.setUser(receiptsObject.optString("CreateUser"));
                                    model.setCreditLimit(receiptsObject.optString("Credit"));
                                    model.setDate(receiptsObject.optString("ReceiptDateString"));
                                    ArrayList<ReceiptsListAdapter.InvoiceModel> invoiceList = new ArrayList<>();
                                    model.setInvoiceList(invoiceList);
                                    receiptsList.add(model);
                                }
                            }
                            //  Collections.reverse(receiptsList);
                            Log.w("ReceiptsSize:",receiptsList.size()+"");
                            setNettotal(receiptsList);
                            receiptsAdapter.notifyDataSetChanged();
                            receiptsAdapter.setLoaded();

                            if (receiptsList.size()>0){
                                recyclerViewLayout.setVisibility(View.VISIBLE);
                                emptyLayout.setVisibility(View.GONE);
                            }else {
                                recyclerViewLayout.setVisibility(View.GONE);
                                emptyLayout.setVisibility(View.VISIBLE);
                            }
                            if (receiptNo!=null && receiptNo.equals("pass")){
                                getReceiptsDetails(receiptsList.get(0).getReceiptNumber(), Integer.parseInt(noofCopy));
                            }
                        }*/
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
    }


    public void setReceiptsAdapter(ArrayList<ReceiptsModel> receiptsList){
        receiptsListView.setHasFixedSize(true);
        receiptsListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        receiptsAdapter =new ReceiptsListAdapter(this, receiptsListView, receiptsList, new ReceiptsListAdapter.CallBack() {
            @Override
            public void calculateNetTotal(ArrayList<ReceiptsModel> receiptsList) {
                setNettotal(receiptsList);
            }
            @Override
            public void showMoreOption(String receipt_number, String customerName, String customercode,String mode,ReceiptsModel receipts){
                customerLayout.setVisibility(View.GONE);
                receiptsOptions.setVisibility(View.VISIBLE);
                customerCode=customercode;
                payMode=mode;
                receiptNumber.setText(receipt_number);
                receiptNo=receipt_number;
                soCustomerName.setText(customerName);
                receiptsModel=receipts;
                receiptDate=receipts.getDate();
                customerLayout.setVisibility(View.GONE);
                receiptsOptions.setVisibility(View.VISIBLE);
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void openWhatsapp() {
                //sendWhatsapp();
                //openWhatsAppView();
                // openWhatsAppWithNumber();
                shareContent();
            }
        });
        receiptsListView.setAdapter(receiptsAdapter);
    }

    public String getTimeStamp(String timestamp) {
        String timeValue = null;
        Matcher m = Pattern.compile("\\((.*?)\\)").matcher(timestamp);
        while(m.find()) {
            System.out.println(m.group(1));
            timeValue=m.group(1);
            getDate(Long.parseLong(timeValue));
        }
        return timeValue;
    }

    private String getDate(long time) {
        Timestamp stamp = new Timestamp(time);
        Date date = new Date(stamp.getTime());
        SimpleDateFormat df1 = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        String datevalue=df1.format(date);
        Log.w("TimeValue:",datevalue);
        return  datevalue;
    }

    private Date getDateValue(long time) {
        Timestamp stamp = new Timestamp(time);
        return new Date(stamp.getTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sorting_menu, menu);

        MenuItem barcode=menu.findItem(R.id.action_barcode);
        MenuItem addcustomer=menu.findItem(R.id.action_add);
        MenuItem filter=menu.findItem(R.id.action_filter);

        filter.setVisible(true);
        barcode.setVisible(false);
        addcustomer.setVisible(false);

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
            Collections.sort(receiptsList, new Comparator<ReceiptsModel>(){
                public int compare(ReceiptsModel obj1, ReceiptsModel obj2) {
                    // ## Ascending order
                    return obj1.getName().compareToIgnoreCase(obj2.getName()); // To compare string values
                    // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                    // ## Descending order
                    // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                    // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                }
            });
            receiptsAdapter.notifyDataSetChanged();
        }else if (item.getItemId()==R.id.action_amount){
            Collections.sort(receiptsList, new Comparator<ReceiptsModel>(){
                public int compare(ReceiptsModel obj1, ReceiptsModel obj2) {
                    // ## Ascending order
                    //  return obj1.getNetTotal().compareToIgnoreCase(obj2.getNetTotal()); // To compare string values
                    return Double.valueOf(obj1.getNetTotal()).compareTo(Double.valueOf(obj2.getNetTotal())); // To compare integer values

                    // ## Descending order
                    // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                    // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                }
            });
            receiptsAdapter.notifyDataSetChanged();
        }else if (item.getItemId()==R.id.action_date){

            try {
                Collections.sort(receiptsList, new Comparator<ReceiptsModel>(){
                    public int compare(ReceiptsModel obj1, ReceiptsModel obj2) {
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
                receiptsAdapter.notifyDataSetChanged();
            }catch (Exception ex){
                Log.w("Error:",ex.getMessage());
            }
        }else if (item.getItemId()==R.id.action_add){
            customerDetails=dbHelper.getCustomer();
            if (customerDetails.size()>0){
              //  showCustomerDialog(this,customerDetails.get(0).getCustomerName(),customerDetails.get(0).getCustomerCode(),customerDetails.get(0).getCustomerAddress1());
            }else {
                customerLayout.setVisibility(View.VISIBLE);
                receiptsOptions.setVisibility(View.GONE);
                viewCloseBottomSheet();
            }
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

    private void sortArray(ArrayList<ReceiptsModel> arraylist) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //your own date format
        if (arraylist != null) {
            Collections.sort(arraylist, new Comparator<ReceiptsModel>() {
                @Override
                public int compare(ReceiptsModel o1, ReceiptsModel o2) {
                    try {
                        return simpleDateFormat.parse(o2.getDateSortingString()).compareTo(simpleDateFormat.parse(o1.getDateSortingString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });
        }
    }



    private void showCustomerDialog(String  receipt_no,String invoice_no,String net_total,String paid_amount,String credit_amount) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.receipts_details_layout, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        TextView receiptNumber =dialogView.findViewById(R.id.receipt_no);
        TextView invoiceNumber =dialogView.findViewById(R.id.invoice_no);
        TextView netTotal=dialogView.findViewById(R.id.net_total);
        TextView paidAmount=dialogView.findViewById(R.id.paid_amount);
        TextView creditAmount=dialogView.findViewById(R.id.credit_amount);

        receiptNumber.setText(receipt_no);
        invoiceNumber.setText(invoice_no);
        netTotal.setText(net_total);
        if (paid_amount.equals("null")){
            paidAmount.setText("0.00");
        }else {
            paidAmount.setText(paid_amount);
        }
        if (credit_amount.equals("null")){
            creditAmount.setText("0.00");
        }else {
            creditAmount.setText(credit_amount);
        }

        Button yesButton=dialogView.findViewById(R.id.buttonOk);
        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    public void getCustomers(String groupCode){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url=Utils.getBaseUrl(this) +"CustomerList";
        customerList=new ArrayList<>();
        searchableCustomerList=new ArrayList<>();
        searchableCustomerList.add("Select Customer");
        Log.w("Given_url_customer:",url);
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("GroupCode",groupCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        Log.w("Response_Customer:", response.toString());
                        // pDialog.dismiss();
                        // Loop through the array elements

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
                                searchableCustomerList.add(object.optString("customerName")+"-"+object.optString("customerCode"));
                                // }
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"Error,in getting Customer list",Toast.LENGTH_LONG).show();
                        }
                        if (customerList.size()>0){
                            setAdapter(customerList);
                            setDataToAdapter(searchableCustomerList);
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

    public void setDataToAdapter(ArrayList<String> arrayList) {
        // Creating ArrayAdapter using the string array and default spinner layout
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ReceiptsListActivity.this, android.R.layout.simple_spinner_item, arrayList);
        // Specify layout to be used when list of choices appears
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Applying the adapter to our spinner
        customerListSpinner.setAdapter(arrayAdapter);
       // customerListSpinner.setOnItemSelectedListener(this);
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
                if (count>0){
                    showProductDeleteAlert(customer);
                }else {
                    viewCloseBottomSheet();
                    dbHelper.removeAllItems();
                    Intent intent=new Intent(ReceiptsListActivity.this, AddInvoiceActivityOld.class);
                    intent.putExtra("customerId",customer);
                    intent.putExtra("activityFrom","SalesOrder");
                    startActivity(intent);
                }
                Log.w("Customer_id:", customer);
            }
        });
        customerView.setAdapter(customerNameAdapter);
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
                        viewCloseBottomSheet();
                        Intent intent=new Intent(ReceiptsListActivity.this, AddInvoiceActivityOld.class);
                        intent.putExtra("customerId",customerId);
                        intent.putExtra("activityFrom","SalesOrder");
                        startActivity(intent);
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

    public void getDateValueSearch(EditText dateEditext){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(ReceiptsListActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateEditext.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void printReceipt(int copy) throws IOException {
        if (printerType.equals("TSC Printer")) {
            //  TSCPrinter tscPrinter = new TSCPrinter(ReceiptsPrintPreview.this, printerMacId);
            //  tscPrinter.printInvoice(receiptsHeaderDetails, receiptsList);
            TSCPrinter printer=new TSCPrinter(ReceiptsListActivity.this,printerMacId,"Receipt");
            printer.printReceipts(copy,receiptsHeaderDetails,receiptsPrintList);
            printer.setOnCompletionListener(new TSCPrinter.OnCompletionListener() {
                @Override
                public void onCompleted() {
                    Utils.setSignature("");
                    Toast.makeText(getApplicationContext(),"Receipt printed successfully!",Toast.LENGTH_SHORT).show();
                   finish();
                }
            });
        } else if (printerType.equals("Zebra Printer")) {
            ZebraPrinterActivity zebraPrinterActivity = new ZebraPrinterActivity(ReceiptsListActivity.this, printerMacId);
            try {
                zebraPrinterActivity.printReceipts(copy,receiptsHeaderDetails, receiptsPrintList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK && requestCode==FILTER_CUSTOMER_CODE){
            assert data != null;
            String customername=data.getStringExtra("customerName");
            String customercode=data.getStringExtra("customerCode");
            customerNameTextView.setText(customername);
            selectCustomerCode=customercode;
            selectCustomerName=customername;
        }
    }

    @Override
    public void onBackPressed() {
        //Execute your code here
       // Intent intent=new Intent(ReceiptsListActivity.this, NewInvoiceListActivity.class);
       // startActivity(intent);
        finish();
    }
}