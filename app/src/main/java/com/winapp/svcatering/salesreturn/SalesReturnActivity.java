package com.winapp.svcatering.salesreturn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.FilterCustomerListActivity;
import com.winapp.svcatering.activity.NavigationActivity;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.NewLocationModel;
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

public class SalesReturnActivity extends NavigationActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {

    // define the Sales Return button for the operation of the sales return
    private TextView listButton;
    private TextView customerButton;
    public static TextView productButton;
    private TextView summaryButton;
    private View listingView;
    private View customerView;
    private View productView;
    private Menu menu;
    private String visibleFragment="listing";
    private static DBHelper dbHelper;
    private static String currentDateString;
    private static String currentDate;
    private static ArrayList<CartModel> localCart;
    private static ArrayList<CustomerDetails> customerDetails;
    private static String userName;
    private static String locationCode;
    private static String companyCode;
    private HashMap<String,String> user;
    private SessionManager session;
    private View searchFilterView;
    private EditText customerNameText;
    private BottomSheetBehavior behavior;
    private EditText fromDateText;
    private EditText toDateText;
    private Button searchButton;
    private Button cancelSearch;
    private Context mContext;
    private SearchableSpinner customerListSpinner;
    private SearchableSpinner locationSpinner;
    private String locName = "" ;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private EditText fromDate;
    private EditText toDate;
    private ArrayList<CustomerModel> customerList;
    private ArrayList<String> customerSearchList;

    private String printerMacId;
    private String printerType;
    private SharedPreferences sharedPreferences;
    private ArrayList<SalesReturnPrintPreviewModel> salesReturnHeader;
    private ArrayList<SalesReturnPrintPreviewModel.SalesReturnDetails> salesReturnList;
    private String salesReturnNumber;
    private String noofCopy="1";
    private int FILTER_CUSTOMER_CODE=134;
    private ArrayList<NewLocationModel.LocationDetails> locationDetailsl;
    private ArrayList<NewLocationModel.LocationDetails> locationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_sales_return, contentFrameLayout);
        // setContentView(R.layout.activity_invoice_list);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sales Return");

        session=new SessionManager(this);
        dbHelper=new DBHelper(this);
        user=session.getUserDetails();
        userName=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        // Define the Variables
        listButton=findViewById(R.id.btn_listing);
        customerButton=findViewById(R.id.btn_customer);
        productButton=findViewById(R.id.btn_product);
        summaryButton=findViewById(R.id.btn_summary);
        listingView=findViewById(R.id.list_view);
        customerView=findViewById(R.id.customer_view);
        productView=findViewById(R.id.product_view);
        searchFilterView=findViewById(R.id.search_filter);
        customerNameText=findViewById(R.id.customer_name_value);
        fromDateText =findViewById(R.id.from_date);
        toDateText =findViewById(R.id.to_date);
        cancelSearch=findViewById(R.id.btn_cancel);
        searchButton=findViewById(R.id.btn_search);
        customerListSpinner = findViewById(R.id.user_list_spinner);
        locationSpinner=findViewById(R.id.location_spinner);
        fromDate=findViewById(R.id.from_date);
        toDate =findViewById(R.id.to_date);
        //For set Title to Spinner
        customerListSpinner.setTitle("Select Customer");
        listButton.setOnClickListener(this::onClick);
        customerButton.setOnClickListener(this::onClick);
        productButton.setOnClickListener(this::onClick);
        summaryButton.setOnClickListener(this::onClick);
        locationList=new ArrayList<>();
        //locationList.add(locationCode);
        locationSpinner.setTitle("Select Location");

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        Log.w("Printer_Mac_Id:",printerMacId);
        Log.w("Printer_Type:",printerType);

        // Reset the Customer if already selected in other module-->Remove the Session of the customer
        Utils.clearCustomerSession(getApplicationContext());


        if (getIntent() != null){
            salesReturnNumber =getIntent().getStringExtra("srNumber");
            noofCopy=getIntent().getStringExtra("noOfCopy");
            if (salesReturnNumber !=null){
                try {
                    getSalesReturnDetails(salesReturnNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.w("locatretun:",""+Utils.getLocationList());

        locationList = Utils.getLocationList();
        if(locationList.size() > 0){
            setLocationDataToAdapter(locationList);
        }

        customerSearchList=new ArrayList<>();
        customerList=dbHelper.getAllCustomers();
        if (customerList!=null && customerList.size()>0){
            for (CustomerModel model:customerList){
                customerSearchList.add(model.getCustomerName());
            }
            setDataToAdapter(customerSearchList);
        }


        dbHelper.removeCustomer();
        dbHelper.removeAllItems();

        loadFragment(new SalesReturnList());

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        fromDateText.setText(formattedDate);
        toDateText.setText(formattedDate);

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
                String customer_name=customerNameText.getText().toString();
                SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
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
                  SalesReturnList.filterSearch(customer_name,locName, fromDateText.getText().toString(), toDateText.getText().toString());
                }
            }
        });

        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerNameText.setText("");
                fromDateText.setText(formattedDate);
                toDateText.setText(formattedDate);
                searchFilterView.setVisibility(View.GONE);
                setFilterAdapeter();
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

    }


    @Override
    protected void onResume() {
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");
        super.onResume();
    }

    public void setDataToAdapter(ArrayList<String> arrayList) {
        // Creating ArrayAdapter using the string array and default spinner layout
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SalesReturnActivity.this, android.R.layout.simple_spinner_item, arrayList);
        // Specify layout to be used when list of choices appears
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Applying the adapter to our spinner
        customerListSpinner.setAdapter(arrayAdapter);
        customerListSpinner.setOnItemSelectedListener(this);
    }

//    public void setLocationDataToAdapter(ArrayList<String> arrayList) {
//        // Creating ArrayAdapter using the string array and default spinner layout
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SalesReturnActivity.this, android.R.layout.simple_spinner_item, arrayList);
//        // Specify layout to be used when list of choices appears
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Applying the adapter to our spinner
//        locationSpinner.setAdapter(arrayAdapter);
//        locationSpinner.setOnItemSelectedListener(this);
//    }

    public void setLocationDataToAdapter(ArrayList<NewLocationModel.LocationDetails> locationList) {
        ArrayAdapter<NewLocationModel.LocationDetails> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, locationList);
        locationSpinner.setAdapter(adapter);
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String groupCode = locationList.get(position).getLocationCode();
                 locName = locationList.get(position).getLocationName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String customer_name = parent.getItemAtPosition(position).toString();
       // customerNameText.setText(customer_name);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void getDate(EditText dateEditext){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(SalesReturnActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateEditext.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void setFilterAdapeter() {
        startActivity(getIntent());
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btn_listing){
            int count=dbHelper.numberOfRows();
            if (count>0){
                showAlert();
            }else {
                setListingScreen();
            }
        }else if (view.getId()==R.id.btn_customer){
            listButton.setTextColor(Color.parseColor("#212121"));
            customerButton.setTextColor(Color.parseColor("#FFFFFF"));
            productButton.setTextColor(Color.parseColor("#212121"));
            summaryButton.setTextColor(Color.parseColor("#212121"));

            listButton.setBackgroundResource(R.drawable.button_unselect);
            customerButton.setBackgroundResource(R.drawable.button_order);
            productButton.setBackgroundResource(R.drawable.button_unselect);
            summaryButton.setBackgroundResource(R.drawable.button_unselect);

            listingView.setVisibility(View.GONE);
            customerView.setVisibility(View.GONE);
            productView.setVisibility(View.VISIBLE);

            visibleFragment="customer";
            invalidateOptionsMenu();

            // Load the Particular Fragment when click the button
            loadFragment(new SalesReturnCustomer());

        }else if (view.getId()==R.id.btn_product){
            SharedPreferences sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
            String selectCustomerId = sharedPreferences.getString("customerId", "");
            customerDetails=dbHelper.getCustomer(selectCustomerId);
            int size=customerDetails.size();
            if (!SalesReturnCustomer.customerCodeEdittext.getText().toString().isEmpty()){
                listButton.setTextColor(Color.parseColor("#212121"));
                customerButton.setTextColor(Color.parseColor("#212121"));
                productButton.setTextColor(Color.parseColor("#FFFFFF"));
                summaryButton.setTextColor(Color.parseColor("#212121"));

                listButton.setBackgroundResource(R.drawable.button_unselect);
                customerButton.setBackgroundResource(R.drawable.button_unselect);
                productButton.setBackgroundResource(R.drawable.button_order);
                summaryButton.setBackgroundResource(R.drawable.button_unselect);

                listingView.setVisibility(View.VISIBLE);
                customerView.setVisibility(View.GONE);
                productView.setVisibility(View.GONE);

                visibleFragment="product";
                invalidateOptionsMenu();

                // Load the Product Fragment
                loadFragment(new SalesReturnProduct());
            }else {
                Toast.makeText(getApplicationContext(),"Please select customer first",Toast.LENGTH_SHORT).show();
            }
        }else if (view.getId()==R.id.btn_summary){
            int count=dbHelper.numberOfRows();
            if (count>0){
                listButton.setTextColor(Color.parseColor("#212121"));
                customerButton.setTextColor(Color.parseColor("#212121"));
                productButton.setTextColor(Color.parseColor("#212121"));
                summaryButton.setTextColor(Color.parseColor("#FFFFFF"));

                listButton.setBackgroundResource(R.drawable.button_unselect);
                customerButton.setBackgroundResource(R.drawable.button_unselect);
                productButton.setBackgroundResource(R.drawable.button_unselect);
                summaryButton.setBackgroundResource(R.drawable.button_order);

                listingView.setVisibility(View.VISIBLE);
                customerView.setVisibility(View.VISIBLE);
                productView.setVisibility(View.GONE);

                visibleFragment="summary";
                invalidateOptionsMenu();

                // Load the Sales return Summary
                loadFragment(new SalesReturnSummary());
            }else {
                Toast.makeText(getApplicationContext(),"Please add product first",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sales_return_menu, menu);
        this.menu=menu;
        MenuItem addCustomer=menu.findItem(R.id.action_add);
        MenuItem filter=menu.findItem(R.id.action_filter);
        MenuItem saveItem=menu.findItem(R.id.action_save);

        if (visibleFragment.equals("listing")){
            addCustomer.setVisible(false);
            filter.setVisible(true);
            saveItem.setVisible(false);
        }else if (visibleFragment.equals("customer")){
            if (SalesReturnList.isEdit){
                addCustomer.setVisible(false);
            }else {
                addCustomer.setVisible(true);
            }
            filter.setVisible(false);
            saveItem.setVisible(false);
        }else if (visibleFragment.equals("product")){
            addCustomer.setVisible(false);
            filter.setVisible(false);
            saveItem.setVisible(false);
        }else if (visibleFragment.equals("summary")){
            addCustomer.setVisible(false);
            filter.setVisible(false);
            saveItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            SalesReturnCustomer.viewCloseBottomSheet();
            return true;
        }else if (id==R.id.action_save){
            //showSalesReturnSaveAlert();
            if (SalesReturnList.isEdit){
                //  SalesReturnSummary.createSalesReturnJson("Edit");
                SalesReturnSummary.showSaveOption("Edit");
            }else {
                //  SalesReturnSummary.createSalesReturnJson("Add");
                SalesReturnSummary.showSaveOption("Add");
            }
        }else if (id==R.id.action_filter){
            if (searchFilterView.getVisibility()==View.VISIBLE){
                searchFilterView.setVisibility(View.GONE);
                customerNameText.setText("");
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                //slideUp(searchFilterView);
            }else {
                customerNameText.setText("");
                searchFilterView.setVisibility(View.VISIBLE);
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                // slideDown(searchFilterView);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAlert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SalesReturnActivity.this);
        builder1.setTitle("Warning !");
        builder1.setMessage("Products and Customer Details will be erased.");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        setListingScreen();
                    }
                });
        builder1.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void showSalesReturnSaveAlert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(SalesReturnActivity.this);
        alert.setTitle("Information !");
        if (SalesReturnList.isEdit){
            alert.setMessage("Are you sure want to update Sales Return ?");
        }else {
            alert.setMessage("Are you sure want to save Sales Return ?");
        }
        alert.setCancelable(false);
        alert.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (SalesReturnList.isEdit){
                          //  SalesReturnSummary.createSalesReturnJson("Edit");
                            SalesReturnSummary.showSaveOption("Edit");
                        }else {
                          //  SalesReturnSummary.createSalesReturnJson("Add");
                            SalesReturnSummary.showSaveOption("Add");
                        }
                    }
                });
        alert.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = alert.create();
        alert11.show();
    }

    public void setListingScreen(){

        dbHelper.removeAllItems();
        dbHelper.removeCustomer();
        SalesReturnList.isEdit=false;

        listButton.setTextColor(Color.parseColor("#FFFFFF"));
        customerButton.setTextColor(Color.parseColor("#212121"));
        productButton.setTextColor(Color.parseColor("#212121"));
        summaryButton.setTextColor(Color.parseColor("#212121"));

        listButton.setBackgroundResource(R.drawable.button_order);
        customerButton.setBackgroundResource(R.drawable.button_unselect);
        productButton.setBackgroundResource(R.drawable.button_unselect);
        summaryButton.setBackgroundResource(R.drawable.button_unselect);

        listingView.setVisibility(View.GONE);
        customerView.setVisibility(View.VISIBLE);
        productView.setVisibility(View.VISIBLE);

        visibleFragment="listing";
        invalidateOptionsMenu();

        loadFragment(new SalesReturnList());
    }

    private void getSalesReturnDetails(String srNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("SalesReturnNo", srNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(SalesReturnActivity.this);
        String url= Utils.getBaseUrl(SalesReturnActivity.this) +"SalesReturnDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url_SalesReturn:",url+"/"+jsonObject.toString());
        //pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
       // pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
       // pDialog.setTitleText("Generating Print Preview...");
        //pDialog.setCancelable(false);
        //pDialog.show();
        salesReturnHeader =new ArrayList<>();
        salesReturnList =new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
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
                                String signFlag=object.optString("signFlag");
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
                                    salesReturnModel.setTotal(detailsObject.optString("netTotal"));
                                    salesReturnModel.setSubTotal(detailsObject.optString("subTotal"));
                                    salesReturnModel.setUomCode(object.optString("UOMCode"));
                                    salesReturnList.add(salesReturnModel);
                                }
                                model.setSalesReturnList(salesReturnList);
                                salesReturnHeader.add(model);
                            }
                            if (salesReturnList.size()>0){
                                printSalesReturn(Integer.parseInt(noofCopy));
                            }
                        }else {

                        }
                      /*  if (response.length()>0){
                            SalesReturnPrintPreviewModel model=new SalesReturnPrintPreviewModel();
                            model.setSrNo(response.optString("SalesReturnNo"));
                            model.setSrDate(response.optString("SalesReturnDateString"));
                            model.setCustomerCode(response.optString("CustomerCode"));
                            model.setCustomerName(response.optString("CustomerName"));
                            model.setSubTotal(response.optString("SubTotal"));
                            model.setTax(response.optString("Tax"));
                            model.setNetTotal(response.optString("NetTotal"));
                            model.setBillDisc(response.optString("BillDIscount"));
                            model.setItemDisc(response.optString("ItemDiscount"));
                            model.setTaxType(response.optString("TaxType"));
                            model.setTaxValue(response.optString("TaxPerc"));

                            JSONArray products=response.getJSONArray("SalesReturnDetails");
                            for (int i=0;i<products.length();i++) {
                                JSONObject object = products.getJSONObject(i);
                                SalesReturnPrintPreviewModel.SalesReturnDetails salesReturnModel =new SalesReturnPrintPreviewModel.SalesReturnDetails();
                                salesReturnModel.setProductCode(object.optString("ProductCode"));
                                salesReturnModel.setDescription( object.optString("ProductName"));
                                salesReturnModel.setLqty(object.optString("LQty"));
                                salesReturnModel.setCqty(object.optString("CQty"));
                                salesReturnModel.setNetqty(object.optString("Qty"));
                                salesReturnModel.setCartonPrice(object.optString("CartonPrice"));
                                salesReturnModel.setPrice(object.optString("Price"));
                                salesReturnModel.setPcspercarton(object.optString("PcsPerCarton"));
                                salesReturnModel.setTax(object.optString("Tax"));
                                salesReturnModel.setTotal(object.optString("NetTotal"));
                                salesReturnModel.setSubTotal(object.optString("SubTotal"));
                                salesReturnList.add(salesReturnModel);
                            }
                            model.setSalesReturnList(salesReturnList);
                            salesReturnHeader.add(model);
                        }
                        if (salesReturnList.size()>0){
                            setInvoiceAdapter();
                        }*/
                      //  pDialog.dismiss();
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


/*
    private void getSalesReturnDetails(String srNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyCode);
        jsonObject.put("SalesReturnNo", srNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(SalesReturnActivity.this);
        String url= Utils.getBaseUrl(SalesReturnActivity.this) +"SalesApi/GetSRByCode?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        salesReturnHeader =new ArrayList<>();
        salesReturnList =new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("SalesReturn_Details:",response.toString());
                        if (response.length()>0){
                            SalesReturnPrintPreviewModel model=new SalesReturnPrintPreviewModel();
                            model.setSrNo(response.optString("SalesReturnNo"));
                            model.setSrDate(response.optString("SalesReturnDateString"));
                            model.setCustomerCode(response.optString("CustomerCode"));
                            model.setCustomerName(response.optString("CustomerName"));
                            model.setSubTotal(response.optString("SubTotal"));
                            model.setTax(response.optString("Tax"));
                            model.setNetTotal(response.optString("NetTotal"));
                            model.setBillDisc(response.optString("BillDIscount"));
                            model.setItemDisc(response.optString("ItemDiscount"));
                            model.setTaxType(response.optString("TaxType"));
                            model.setTaxValue(response.optString("TaxPerc"));

                            JSONArray products=response.getJSONArray("SalesReturnDetails");
                            for (int i=0;i<products.length();i++) {
                                JSONObject object = products.getJSONObject(i);
                                SalesReturnPrintPreviewModel.SalesReturnDetails salesReturnModel =new SalesReturnPrintPreviewModel.SalesReturnDetails();
                                salesReturnModel.setProductCode(object.optString("ProductCode"));
                                salesReturnModel.setDescription( object.optString("ProductName"));
                                salesReturnModel.setLqty(object.optString("LQty"));
                                salesReturnModel.setCqty(object.optString("CQty"));
                                salesReturnModel.setNetqty(object.optString("Qty"));
                                salesReturnModel.setCartonPrice(object.optString("CartonPrice"));
                                salesReturnModel.setPrice(object.optString("Price"));
                                salesReturnModel.setPcspercarton(object.optString("PcsPerCarton"));
                                salesReturnModel.setTax(object.optString("Tax"));
                                salesReturnModel.setTotal(object.optString("NetTotal"));
                                salesReturnModel.setSubTotal(object.optString("SubTotal"));
                                salesReturnModel.setUomCode(object.optString("UOMCode"));
                                salesReturnList.add(salesReturnModel);
                            }
                            model.setSalesReturnList(salesReturnList);
                            salesReturnHeader.add(model);
                        }
                        printSalesReturn(Integer.parseInt(noofCopy));
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
    }
*/

    public void printSalesReturn(int copy) throws IOException {
        if (printerType.equals("TSC Printer")){
            // TSCPrinter tscPrinter=new TSCPrinter(SalesReturnPrintPreview.this,printerMacId);
            //    tscPrinter.printInvoice(salesReturnHeader, salesReturnList);
            TSCPrinter printer=new TSCPrinter(SalesReturnActivity.this,printerMacId,"SalesReturn");
            printer.printSalesReturn(copy,salesReturnHeader,salesReturnList);
        }else if (printerType.equals("Zebra Printer")){
            ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(SalesReturnActivity.this,printerMacId);
            try {
                zebraPrinterActivity.printSalesReturn(copy, salesReturnHeader, salesReturnList);
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
            customerNameText.setText(customername);
        }
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        finish();
    }

    public void loadFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }
}