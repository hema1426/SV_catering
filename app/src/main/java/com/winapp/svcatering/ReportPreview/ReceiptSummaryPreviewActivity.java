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
import com.winapp.svcatering.ReportPreview.adapter.ReceiptSummaryPreviewAdapter;
import com.winapp.svcatering.model.ReceiptSummaryModel;
import com.winapp.svcatering.utils.Constants;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReceiptSummaryPreviewActivity extends AppCompatActivity {
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
    String username = "winapp";
    String companyId = "1";
    private ArrayList<ReceiptSummaryModel> receiptSummaryList ;
    private ArrayList<ReceiptSummaryModel.ReceiptDetails> receiptSummaryDetailsList ;
    private TextView recpt_fromdatel;
    private TextView recpt_todatel;
    private TextView loc_recptl;
    public String fromdatetxt = "";
    public String todatetxt = "";
    public String loccodetxt = "";
    private RecyclerView rv_recptsum_perv_listl;
    public ReceiptSummaryPreviewAdapter receiptSummaryPreviewAdapter;
    public double sum_cashamt = 0.0;
    public double sum_chequeamt = 0.0;
    public double sum_totalamt = 0.0;
    public TextView recpt_chequeamtl;
    public TextView recpt_cashamtl;
    public TextView recpt_total;

    private String printerMacId;
    private String printerType;
    private SharedPreferences sharedPreferences;
    private String from_date;
    private String to_date;
    private String locationCode;
    private String customer_name;
    private String customer_code;
    private JSONObject jsonObject;
    private String url;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private ArrayList<ReceiptSummaryModel> receiptDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_summary_preview);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Receipt Summary Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.w("activity_cg",getClass().getSimpleName().toString());

        companyNametext =findViewById (R.id.company_name);
        companyAddress1Text =findViewById (R.id.company_addr1);
        companyAddress2Text =findViewById (R.id.company_addr2);
        companyAddress3Text =findViewById (R.id.company_addr3);
        companyGstText =findViewById (R.id.company_gst);
        companyPhoneText =findViewById (R.id.company_phone);
        recpt_fromdatel = findViewById(R.id.recptsum_fromdate);
        recpt_todatel = findViewById(R.id.recptsum_todate);
        loc_recptl = findViewById(R.id.loc_recptsum);
        rv_recptsum_perv_listl =  findViewById(R.id.rv_recptsum_perv_list);
        recpt_chequeamtl = findViewById(R.id.chequeamt_recptsum);
        recpt_cashamtl = findViewById(R.id.cashamt_recptsum);
        recpt_total = findViewById(R.id.total_recptsum);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        if (getIntent()!=null){
            fromdatetxt=getIntent().getStringExtra("fromDate");
            todatetxt=getIntent().getStringExtra("toDate");
            companyId=getIntent().getStringExtra("companyId");
            loccodetxt=getIntent().getStringExtra("locationCode");
            customer_name=getIntent().getStringExtra("customerName");
            customer_code=getIntent().getStringExtra("customerCode");
            username=getIntent().getStringExtra("userName");
        }

        from_date=fromdatetxt;
        to_date=todatetxt;
        locationCode=loccodetxt;

        recpt_fromdatel.setText(fromdatetxt);
        recpt_todatel.setText(todatetxt);
        loc_recptl.setText(loccodetxt);

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

        try {
            getReceiptSummary(customer_code,fromDateString,toDateString,1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getReceiptSummary(String customer_id,String from_date,String to_date,int copy) throws JSONException {
        // Initialize a new RequestQueue instance
        jsonObject=new JSONObject();
        jsonObject.put("User",username);
        jsonObject.put(    "Status" , "O");
        jsonObject.put("CustomerCode",customer_id);
        jsonObject.put("FromDate",from_date);
        jsonObject.put("ToDate",to_date);
        jsonObject.put("LocationCode",locationCode);
        requestQueue = Volley.newRequestQueue(this);
        url= Utils.getBaseUrl(this) +"ReportReceiptSummary";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_Summary:",url+"/"+jsonObject.toString());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        receiptDetails =new ArrayList<>();
        ArrayList<ReceiptSummaryModel.ReceiptDetails> receiptDetailsList=new ArrayList<>();
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try{
                Log.w("ReceiptDetailsRes:",response.toString());
                String statusCode=response.optString("statusCode");
                if (statusCode.equals("1")){
                    JSONArray responseData=response.optJSONArray("responseData");
                    assert responseData != null;
                    if (responseData.length()>0){
                        JSONObject object=responseData.optJSONObject(0);
                        ReceiptSummaryModel receiptModel =new ReceiptSummaryModel();
                        receiptModel.setFromDate(object.optString("fromDate"));
                        receiptModel.setToDate(object.optString("toDate"));
                        receiptModel.setCustomerName(object.optString("customerName"));
                        receiptModel.setCustomerCode(object.optString("customerCode"));
                        receiptModel.setUser(username);
                        JSONArray detailsArray=object.optJSONArray("reportReceiptSummaryDetails");
                        for (int i = 0; i< Objects.requireNonNull(detailsArray).length(); i++){
                            // Get the Receipt details for the Particular products
                            JSONObject detailsObject=detailsArray.getJSONObject(i);
                            ReceiptSummaryModel.ReceiptDetails details =new ReceiptSummaryModel.ReceiptDetails();
                            details.setInvoiceNo(detailsObject.optString("invoiceNo"));
                            details.setReceiptNo(detailsObject.optString("receiptNo"));
                            details.setReceiptDate(detailsObject.optString("receiptDate"));
                            details.setCustomerCode(detailsObject.optString("customerCode"));
                            details.setPaidAmount(detailsObject.optString("paidAmount"));
                            details.setCustomerName(detailsObject.optString("customerName"));
                            details.setPaymode(detailsObject.optString("payMode"));
                            receiptDetailsList.add(details);
                        }
                        receiptModel.setReceiptDetailsList(receiptDetailsList);
                        receiptDetails.add(receiptModel);
                        pDialog.dismiss();
                    }else {
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"No Record Found...",Toast.LENGTH_SHORT).show();
                    }
                }
                pDialog.dismiss();
                if (receiptDetailsList.size()>0){
                    setReceiptSumAdapter(receiptDetailsList);
                }else {
                    Toast.makeText(getApplicationContext(),"No Record Found...",Toast.LENGTH_SHORT).show();
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

    private void setReceiptSumAdapter(ArrayList<ReceiptSummaryModel.ReceiptDetails> receiptsList) {

        for (int i=0;i<receiptsList.size();i++) {
            sum_totalamt += Double.parseDouble(receiptsList.get(i).getPaidAmount());

            if (receiptsList.get(i).getPaymode().equalsIgnoreCase("Cash")) {
                sum_cashamt += Double.parseDouble(receiptsList.get(i).getPaidAmount());
            } else {
                sum_chequeamt += Double.parseDouble(receiptsList.get(i).getPaidAmount());
            }
        }
        recpt_chequeamtl.setText(twoDecimalPoint(sum_chequeamt));
        recpt_cashamtl.setText(twoDecimalPoint(sum_cashamt));
        recpt_total.setText(twoDecimalPoint(sum_totalamt));

        receiptSummaryPreviewAdapter = new ReceiptSummaryPreviewAdapter(this, receiptsList);
        rv_recptsum_perv_listl.setHasFixedSize(true);
        rv_recptsum_perv_listl.setLayoutManager(new LinearLayoutManager(this));
        rv_recptsum_perv_listl.setAdapter(receiptSummaryPreviewAdapter);
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
                        setReceiptSummaryPrint(receiptDetails,1);
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

    public void setReceiptSummaryPrint(ArrayList<ReceiptSummaryModel> receiptDetails,int copy) throws IOException {
        if (validatePrinterConfiguration()){
            if (printerType.equals("Zebra Printer")){
                ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(this,printerMacId);
                zebraPrinterActivity.printReceiptSummary(copy,receiptDetails);
            } else if (printerType.equals("TSC Printer")) {
                TSCPrinter printer = new TSCPrinter(this, printerMacId,"ReceiptSummary");
                printer.printReceiptSummary(copy, from_date, to_date, receiptDetails);
            }
            else {
                Toast.makeText(getApplicationContext(),"This Printer not Support...!",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Please configure the Printer",Toast.LENGTH_SHORT).show();
        }
    }
}