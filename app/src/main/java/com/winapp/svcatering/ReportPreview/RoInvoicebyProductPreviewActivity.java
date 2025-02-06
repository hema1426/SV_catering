package com.winapp.svcatering.ReportPreview;

import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.ReportPreview.adapter.RoInvoicebyProductPreviewAdapter;
import com.winapp.svcatering.model.InvoiceByProductModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.Utils;
import com.winapp.svcatering.zebraprinter.TSCPrinter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RoInvoicebyProductPreviewActivity extends AppCompatActivity {
    private TextView companyNametext;
    private TextView companyAddress1Text;
    private TextView companyAddress2Text;
    private TextView companyAddress3Text;
    private TextView companyPhoneText;
    private TextView companyGstText;
    private String company_name;
    private String company_address1;
    private String company_address2;
    private String company_address3;
    public SweetAlertDialog pDialog;
    String username = "";
    String companyId = "";
    private ArrayList<InvoiceByProductModel> invoiceProductModel ;
    private ArrayList<InvoiceByProductModel.ProductDetails> invoiceProductDetailsList;
    private TextView fromdatel;
    private TextView todatel;
    private TextView locationl;
    public String fromdatetxt = "";
    public String todatetxt = "";
    public String loccodetxt = "";
    private RecyclerView rv_invpdt_perv_listl;
    public RoInvoicebyProductPreviewAdapter roInvoicebyProductPreviewAdapter;
    public double sum_qtyamt = 0.0;
    public TextView total_qtyl;
    private AlertDialog alert11;

    private String printerMacId;
    private String printerType;
    private SharedPreferences sharedPreferences;
    private String from_date;
    private String to_date;
    private String locationCode;
    private String customer_name;
    private String customer_id;
    private String status;
    private JSONObject jsonObject;
    private RequestQueue requestQueue;
    private String url;
    private JsonObjectRequest jsonObjectRequest;
    private ArrayList<InvoiceByProductModel> invoiceByProductList;
    private ArrayList<InvoiceByProductModel.ProductDetails> productDetailsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ro_invoicebyproduct_preview);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Invoice By Products");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.w("activity_cg",getClass().getSimpleName().toString());

        companyNametext =findViewById (R.id.company_name);
        companyAddress1Text =findViewById (R.id.company_addr1);
        companyAddress2Text =findViewById (R.id.company_addr2);
        companyAddress3Text =findViewById (R.id.company_addr3);
        companyGstText =findViewById (R.id.company_gst);
        companyPhoneText =findViewById (R.id.company_phone);
        fromdatel = findViewById(R.id.fromdate_invpdt);
        todatel = findViewById(R.id.todate_invpdt);
        locationl = findViewById(R.id.loc_invpdt);
        rv_invpdt_perv_listl =  findViewById(R.id.rv_invpdt_perv_list);
        total_qtyl = findViewById(R.id.total_qty_invpdt);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        if (getIntent()!=null){
            fromdatetxt=getIntent().getStringExtra("fromDate");
            todatetxt=getIntent().getStringExtra("toDate");
            companyId=getIntent().getStringExtra("companyId");
            loccodetxt=getIntent().getStringExtra("locationCode");
            customer_name=getIntent().getStringExtra("customerName");
            customer_id=getIntent().getStringExtra("customerCode");
            username=getIntent().getStringExtra("userName");
            status=getIntent().getStringExtra("status");
        }

        Date fromDate = null;
        try {
            fromDate = new SimpleDateFormat("dd/MM/yyyy").parse(fromdatetxt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date toDate = null;
        try {
            toDate = new SimpleDateFormat("dd/MM/yyyy").parse(todatetxt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String fromDateString = new SimpleDateFormat("yyyyMMdd").format(fromDate);
        String toDateString = new SimpleDateFormat("yyyyMMdd").format(toDate);

        from_date=fromdatetxt;
        to_date=todatetxt;
        locationCode=loccodetxt;
        fromdatel.setText(fromdatetxt);
        todatel.setText(todatetxt);
        locationl.setText(loccodetxt);

        try {
            getInvoiceByProduct(customer_id,fromDateString,toDateString,status,1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getInvoiceByProduct(String customer_id,String from_date,String to_date,String status,int copy) throws JSONException {
        // Initialize a new RequestQueue instance
        jsonObject=new JSONObject();
        jsonObject.put("User",username);
        jsonObject.put("CustomerCode",customer_id);
        jsonObject.put("Warehouse",locationCode);
        jsonObject.put("FromDate",from_date);
        jsonObject.put("ToDate",to_date);
        jsonObject.put("Status",status);

        requestQueue = Volley.newRequestQueue(this);
        url= Utils.getBaseUrl(this) +"ReportInvoiceByProduct";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url+"/"+jsonObject.toString());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        invoiceByProductList=new ArrayList<>();
        productDetailsList=new ArrayList<>();
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try{
                Log.w("InvoiceByProductRes:",response.toString());
                String status_value="ALL";
                if (status.equals("O")){
                    status_value="NOT PAID";
                }else if (status.equals("C")){
                    status_value="PAID";
                }else {
                    status_value="ALL";
                }

                String statusCode=response.optString("statusCode");
                if (statusCode.equals("1")){
                    JSONArray responseData=response.optJSONArray("responseData");
                    assert responseData != null;
                    if (responseData.length()>0){
                        JSONObject object=responseData.optJSONObject(0);
                        InvoiceByProductModel invoiceByProductModel=new InvoiceByProductModel();
                        invoiceByProductModel.setFromDate(object.optString("fromDate"));
                        invoiceByProductModel.setToDate(object.optString("toDate"));
                        invoiceByProductModel.setUserName(username);
                        invoiceByProductModel.setLocationCode(object.optString("warehouseCode"));
                        invoiceByProductModel.setLocationName(object.optString("warehouseName"));
                        invoiceByProductModel.setPrintType(status_value);
                        JSONArray detailsArray=object.optJSONArray("reportInvByProductDetails");
                        for (int i = 0; i< Objects.requireNonNull(detailsArray).length(); i++){
                            JSONObject detailsObject=detailsArray.getJSONObject(i);
                            InvoiceByProductModel.ProductDetails productDetails=new InvoiceByProductModel.ProductDetails();
                            productDetails.setProductCode(detailsObject.optString("productCode"));
                            productDetails.setProductName(detailsObject.optString("productName"));
                            productDetails.setProductQty(detailsObject.optString("quantity"));
                            productDetailsList.add(productDetails);
                        }
                        invoiceByProductModel.setProductDetails(productDetailsList);
                        invoiceByProductList.add(invoiceByProductModel);
                    }else {
                        pDialog.dismiss();
                        finish();
                        Toast.makeText(getApplicationContext(),"No Record Found...",Toast.LENGTH_SHORT).show();
                    }
                }
                pDialog.dismiss();
                if (productDetailsList.size()>0){
                    setInvProductAdapter(productDetailsList);
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



    private void setInvProductAdapter(ArrayList<InvoiceByProductModel.ProductDetails> invoiceProductList) {
        for (int i=0;i<invoiceProductList.size();i++) {
            sum_qtyamt += Double.parseDouble(invoiceProductList.get(i).getProductQty());
        }
        total_qtyl.setText(twoDecimalPoint(sum_qtyamt));
        roInvoicebyProductPreviewAdapter = new RoInvoicebyProductPreviewAdapter(this, invoiceProductList);
        rv_invpdt_perv_listl.setHasFixedSize(true);
        rv_invpdt_perv_listl.setLayoutManager(new LinearLayoutManager(this));
        rv_invpdt_perv_listl.setAdapter(roInvoicebyProductPreviewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.print_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_print) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
                Toast.makeText(getApplicationContext(),"This device does not support bluetooth",Toast.LENGTH_SHORT).show();
            } else if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enabled :)
                Toast.makeText(getApplicationContext(),"Enable bluetooth and connect the printer",Toast.LENGTH_SHORT).show();
            } else {
                // Bluetooth is enabled
                if (!printerType.isEmpty()){
                    try {
                        setInvoiceByProductPrint(1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Please configure Printer",Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }else if (id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validatePrinterConfiguration(){
        boolean printetCheck=false;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this,"This device does not support bluetooth",Toast.LENGTH_SHORT).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            Toast.makeText(this,"Enable bluetooth and connect the printer",Toast.LENGTH_SHORT).show();
        } else {
            // Bluetooth is enabled
            if (!printerType.isEmpty() && !printerMacId.isEmpty()){
                printetCheck=true;
            }else {
                Toast.makeText(this,"Please configure Printer",Toast.LENGTH_SHORT).show();
            }
        }
        return printetCheck;
    }

    public void setInvoiceByProductPrint(int copy) throws IOException {
        if (validatePrinterConfiguration()){
            if (printerType.equals("TSC Printer")){
                TSCPrinter printer=new TSCPrinter(this,printerMacId,"InvoiceByProduct");
                printer.printInvoiceByProduct(copy,productDetailsList,from_date,to_date,locationCode);
            }else {
                Toast.makeText(getApplicationContext(),"This Printer not Support...!",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Please configure the Printer",Toast.LENGTH_SHORT).show();
        }
    }
}