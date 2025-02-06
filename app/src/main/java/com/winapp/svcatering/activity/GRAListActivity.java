package com.winapp.svcatering.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.GRAListAdapter;
import com.winapp.svcatering.model.GRAListModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

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

public class GRAListActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    /**
     *
     *Define the variables of the Dashboard Activity
     * @param savedInstanceState
     */

    public ArrayList<GRAListModel> graList;
    public GRAListAdapter graListAdapter;
    public RecyclerView graListView;
    public TextView totalProducts;
    public LinearLayout emptyLayout;
    public LinearLayout titleLayout;
    public TextView tvStatus;
    public EditText searchProduct;
    public TextWatcher searchTextWatcher;
    public LinearLayout productAddLayout;
    public LinearLayout mainLayout;
    public TextView cancelBtn;
    public TextView addProduct;
    public ImageView backButton;
    public SessionManager session;
    public HashMap<String ,String > user;
    public String companyId;
    public String locationCode;
    public ImageView scanBarcode;
    public int RESULT_CODE=12;
    public String scannedBarcode="";
    public ImageView addProductNew;
    public int GRA_ADD_REQUEST_CODE=133;
    public ImageView filter;
    public String currentDateString;
    public TextView fromDate;
    public TextView toDate;
    boolean isfromDate=true;
    public AlertDialog alertDialog;
    public String selectFromDate="";
    public String selectToDate="";
    public ArrayList<String> supplierList;
    private DatePickerDialog dpd;
    private SweetAlertDialog pDialog;
    private Spinner supplierSpinner;
    private String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gralist);
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        init();
    }
    public void init(){
        graListView =findViewById(R.id.gra_list_view);
        totalProducts=findViewById(R.id.total_products);
        emptyLayout=findViewById(R.id.empty_layout);
        //  titleLayout=findViewById(R.id.title_layout);
       // tvStatus=findViewById(R.id.tvStatus);
       // progressBar=findViewById(R.id.progressBar);
      //  searchProduct=findViewById(R.id.search_product);
       // productAddLayout=findViewById(R.id.product_add_layout);
        mainLayout=findViewById(R.id.main_layout);
        cancelBtn=findViewById(R.id.cancel);
        addProduct=findViewById(R.id.add_product);
    //    backButton=findViewById(R.id.back_button);
      //  scanBarcode=findViewById(R.id.scan_barcode);
      //  addProductNew=findViewById(R.id.add_product_new);
      //  filter=findViewById(R.id.filter);
      //  Utils.checkConnection(searchProduct);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        // get Permission for the getting the Current location and Adding Image Permission to obtained

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        currentDateString = df.format(c);

        // Call the Products List
        JSONObject object =new JSONObject();
        // {"CompanyCode":"1","GRANo":"","SupplierCode":"","StartDate":"","EndDate":""}
        try {
            object.put("CompanyCode",companyId);
            object.put("GRANo","");
            object.put("SupplierCode","");
            object.put("StartDate",currentDateString);
            object.put("EndDate",currentDateString);
            getAllPurchaseList(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        searchTextWatcher =new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()){
                    filterProducts(editable.toString());
                }else {
                    setGRAAdapter(graList);
                }
            }
        };
        searchProduct.addTextChangedListener(searchTextWatcher);


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.setVisibility(View.VISIBLE);
                productAddLayout.setVisibility(View.GONE);
                searchProduct.setText("");
              //  Utils.hideKeyBoard(GRAListActivity.this,searchProduct);
                finish();
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),GRAListActivity.class);
                startActivityForResult(intent,GRA_ADD_REQUEST_CODE);
                // finish();
            }
        });

        addProductNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),GRAListActivity.class);
                intent.putExtra("from","add_gra");
                startActivityForResult(intent,GRA_ADD_REQUEST_CODE);
                // finish();
            }
        });

      /*  backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),DashBoardActivity.class);
                intent.putExtra("from","add_product");
                startActivity(intent);
                finish();
            }
        });*/

        scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scannedBarcode="";
              //  Intent intent=new Intent(GRAListActivity.this, BarCodeScanner.class);
             //   intent.putExtra("from","add_gra");
              //  startActivityForResult(intent,RESULT_CODE);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    public void getAllSuppliers(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(GRAListActivity.this);
        String url=  Utils.getBaseUrl(this) +"MasterApi/GetSupplier_All?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_Supplier_URL:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading Supplier, Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        supplierList=new ArrayList<>();
        supplierList.add("Select Supplier");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try{
                        Log.w("Response_is_Supplier:",response.toString());
                        // Loop through the array elements
                        for(int i=0;i<response.length();i++){
                            // Get current json object
                            JSONObject object = response.getJSONObject(i);
                            if (object.optString("SupplierName")!=null && !object.optString("SupplierName").equals("null")){
                                String supplierName=object.optString("SupplierName");
                                String supplierCode=object.optString("SupplierCode");
                                supplierList.add(supplierName+"-"+supplierCode);
                            }
                        }
                        pDialog.dismiss();
                        if (supplierList.size()>0){
                            //   setSupplierAdapter(supplierSpinner);
                        }else {
                            Toast.makeText(getApplicationContext(),"No Supplier Found...",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Do something when error occurred
                    pDialog.dismiss();
                    Log.e("Error_throwing:",error.toString());
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

    public void setSupplierAdapter(Spinner supplierSpinner){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, supplierList);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        supplierSpinner.setAdapter(dataAdapter);
    }

    public void filterProducts(String name){
        try {
            //new array list that will hold the filtered data
            ArrayList<GRAListModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (GRAListModel s : graList) {
                //if the existing elements contains the search input
                if (s.getGraNumber().toLowerCase().contains(name.toLowerCase()) || s.getSupplierName().toLowerCase().contains(name.toLowerCase())) {
                    //adding the element to filtered list
                    filterdNames.add(s);
                }
            }
            //calling a method of the adapter class and passing the filtered list
            if (filterdNames.size()>0){
                emptyLayout.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
                productAddLayout.setVisibility(View.GONE);
                graListAdapter.filterList(filterdNames);
                totalProducts.setText("( "+filterdNames.size()+" )"+"Purchase orders");
            }else {
                emptyLayout.setVisibility(View.GONE);
                mainLayout.setVisibility(View.GONE);
                productAddLayout.setVisibility(View.VISIBLE);
             //   Utils.hideKeyBoard(GRAListActivity.this,searchProduct);
            }

        } catch (Exception ex) {
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }

    public void getAllPurchaseList(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(GRAListActivity.this);
        String url=  Utils.getBaseUrl(this) +"PurchaseApi/GetAllPurchaseInvoiceSearch?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_product_url:",url);
        graList =new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try{
                        Log.w("Response_GRA:",response.toString());
                        // Loop through the array elements
                        for(int i=0;i<response.length();i++){
                            // Get current json object
                            JSONObject graObject = response.getJSONObject(i);
                            GRAListModel graModel =new GRAListModel();
                            graModel.setSupplierName(graObject.optString("SupplierName"));
                            graModel.setSupplierCode(graObject.optString("SupplierCode"));
                            graModel.setGraDate(graObject.optString("GRADateString"));
                            graModel.setNetTotal(graObject.optString("NetTotal"));
                            graModel.setGraNumber(graObject.optString("GRANo"));
                            graModel.setLocationCode(graObject.optString("LocationCode"));
                            graList.add(graModel);
                        }
                        setGRAAdapter(graList);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Do something when error occurred
                    Log.e("Error_throwing:",error.toString());
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

    private void setGRAAdapter(ArrayList<GRAListModel> graList) {
        if (graList.size()>0){
            searchProduct.setEnabled(true);
            productAddLayout.setVisibility(View.GONE);
            totalProducts.setText("( "+ graList.size()+" )"+" Purchase Invoices");
//            titleLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            graListView.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.VISIBLE);
            graListView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GRAListActivity.this);
            graListView.setLayoutManager(layoutManager);
            graListAdapter = new GRAListAdapter(this, graList, new GRAListAdapter.GRAOnClickListener() {
                @Override
                public void onGRAClick(GRAListModel model) {
                    Gson gson = new Gson();
                    String object = gson.toJson(model);
                    Intent intent = new Intent(getApplicationContext(), GRAActivity.class);
                    intent.putExtra("to_activity","gra_list");
                    intent.putExtra("GraDetails", object);
                    intent.putExtra("from","edit_gra");
                    intent.putExtra("scan_barcode","");
                    startActivityForResult(intent,GRA_ADD_REQUEST_CODE);
                }
            });
            graListView.setAdapter(graListAdapter);
        }else {
            searchProduct.setEnabled(false);
            emptyLayout.setVisibility(View.GONE);
         //   progressBar.setVisibility(View.GONE);
            tvStatus.setText("No Products Found..!");
            tvStatus.setVisibility(View.GONE);
            mainLayout.setVisibility(View.GONE);
            productAddLayout.setVisibility(View.VISIBLE);
            // titleLayout.setVisibility(View.GONE);
        }
        try {
            JSONObject object=new JSONObject();
            object.put("CompanyCode",companyId);
            getAllSuppliers(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_CODE) {
                String barcodeText=data.getExtras().getString("Contents");
                Log.w("BarcodeText:",barcodeText);
              //  MediaPlayer mp = MediaPlayer.create(this, R.raw.beep); // sound is inside res/raw/mysound
              //  mp.start();
              //  scannedBarcode=barcodeText;
               // searchAndSendActivity(barcodeText);
            }else if (requestCode==GRA_ADD_REQUEST_CODE){
                String keyValue=data.getExtras().getString("key");
                if (keyValue.equals("Refresh")){
                    JSONObject object =new JSONObject();
                    // {"CompanyCode":"1","GRANo":"","SupplierCode":"","StartDate":"","EndDate":""}
                    try {
                        object.put("CompanyCode",companyId);
                        object.put("GRANo","");
                        object.put("SupplierCode","");
                        object.put("StartDate",currentDateString);
                        object.put("EndDate",currentDateString);
                        getAllPurchaseList(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void openDialog(){
        LayoutInflater inflater = LayoutInflater.from(GRAListActivity.this);
        View subView = inflater.inflate(R.layout.filter_layout_gra, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(subView);
        builder.setCancelable(false);
        fromDate=subView.findViewById(R.id.from_date);
        toDate=subView.findViewById(R.id.to_date);
        final TextView cancelButton=subView.findViewById(R.id.cancel_button);
        final TextView applyButton=subView.findViewById(R.id.apply_button);
        final Spinner supplierSpinner =subView.findViewById(R.id.supplierSpinner);
        final ImageView searchCloseButton=subView.findViewById(R.id.search_close_btn);
        final String[] supplier_code = {""};


        if (supplierList.size()>0){
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, supplierList);
            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            supplierSpinner.setAdapter(dataAdapter);
        }else {
            Toast.makeText(getApplicationContext(),"No Supplier Found...",Toast.LENGTH_SHORT).show();
        }

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isfromDate=true;
                datePicker();
            }
        });
        searchCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isfromDate=false;
                datePicker();
            }
        });

        //   {"TransactionNo":"","CustomerCode":"537546","StartDate":"01/07/2021","EndDate":"01/07/2021","CompanyCode":"1","Status":"0"}
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!fromDate.getText().toString().equals("From Date") && !toDate.getText().toString().equals("To Date")){
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date date2 = null;
                    Date date1=null;
                    try {
                        date1 = sdf.parse(fromDate.getText().toString());
                        date2 = sdf.parse(toDate.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (date1.compareTo(date2) <= 0) {
                        System.out.println("Date1 is before Date2");
                        if (!supplierSpinner.getSelectedItem().equals("Select Supplier")){
                            String[] value=supplierSpinner.getSelectedItem().toString().split("-");
                            supplier_code[0] = value[1];
                        }else  {
                            supplier_code[0] ="";
                        }
                        alertDialog.dismiss();
                        JSONObject object =new JSONObject();
                        // {"CompanyCode":"1","GRANo":"","SupplierCode":"","StartDate":"","EndDate":""}
                        try {
                            object.put("CompanyCode",companyId);
                            object.put("GRANo","");
                            object.put("SupplierCode",supplier_code[0]);
                            object.put("StartDate",fromDate.getText().toString());
                            object.put("EndDate",toDate.getText().toString());
                            getAllPurchaseList(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"From date should not be exceed To date",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                selectFromDate="";
                selectToDate="";

            }
        });

      /*  builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });*/
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void datePicker(){
        Calendar now = Calendar.getInstance();
        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            dpd.initialize(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        }
        dpd.setOnCancelListener(dialog -> {
            Log.d("DatePickerDialog", "Dialog was cancelled");
            dpd = null;
        });
        // dpd.setMinDate(now);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date date = null;
      /*  for (int i = 0;i < holidays.length; i++) {
            try {
                date = sdf.parse(holidays[i]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar = dateToCalendar(date);
            System.out.println(calendar.getTime());
            List<Calendar> dates = new ArrayList<>();
            dates.add(calendar);
            Calendar[] disabledDays1 = dates.toArray(new Calendar[dates.size()]);
            dpd.setDisabledDays(disabledDays1);
        }*/
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");
    }

 /*   public void searchAndSendActivity(String barcode){
        Gson gson = new Gson();
        GRAListModel model=getProductData(barcode);
        if (model!=null){
            Toast.makeText(getApplicationContext(),"Product Found",Toast.LENGTH_SHORT).show();
            String object = gson.toJson(model);
            Intent intent = new Intent(getApplicationContext(), ProductAddActivity.class);
            intent.putExtra("productDetails", object);
            intent.putExtra("to_activity","product_list");
            intent.putExtra("from","edit_product");
            intent.putExtra("scan_barcode",scannedBarcode);
            startActivity(intent);
            finish();
        }else {
            showBarcodeAlert(barcode);
            Toast.makeText(getApplicationContext(),"No Product found",Toast.LENGTH_SHORT).show();
        }

    }*/

    public GRAListModel getProductData(String keyId){
        int index=0;
        GRAListModel value=null;
        for(GRAListModel model : graList) {
            if (model.getGraNumber().contains(keyId)) {
                value=model;
            }
            index++;
        }
        return value;
    }

 /* *//*  public void show*//*BarcodeAlert(String barcode){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Information !");
        builder.setMessage("Product Not found for this barcode -"+barcode+" "+"Do you want add existing product or create new one?");
        builder.setPositiveButton("ADD EXIST", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("ADD NEW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(getApplicationContext(),ProductAddActivity.class);
                intent.putExtra("from","add_product");
                intent.putExtra("to_activity","product_list");
                intent.putExtra("activity","productList");
                intent.putExtra("scan_barcode",scannedBarcode);
                startActivity(intent);
                finish();
            }
        });
        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                scannedBarcode="";
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }*/

    @Override
    public void onBackPressed() {
      //  Intent  intent=new Intent(getApplicationContext(),DashBoardActivity.class);
      //  startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
       // MyApplication.getInstance().setConnectivityListener(this);
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
        if (isfromDate){
            fromDate.setText(date);
            selectFromDate=date;
        }else {
            toDate.setText(date);
            selectToDate=date;
        }
    }
}