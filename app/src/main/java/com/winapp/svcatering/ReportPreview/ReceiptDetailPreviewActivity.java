package com.winapp.svcatering.ReportPreview;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import com.winapp.svcatering.ReportPreview.adapter.ReceiptDetailPreviewAdapter;
import com.winapp.svcatering.model.ReceiptDetailsModel;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReceiptDetailPreviewActivity extends AppCompatActivity {
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
    private ArrayList<ReceiptDetailsModel.ReceiptDetails> receiptDetailsList ;
    private ArrayList<ReceiptDetailsModel> receiptDetailsModels ;
    private TextView recpt_fromdatel;
    private TextView recpt_todatel;
    private TextView loc_recptl;
    public String fromdatetxt = "";
    public String todatetxt = "";
    public String loccodetxt = "";
    private RecyclerView rv_recpt_perview_listl;
    public ReceiptDetailPreviewAdapter receiptDetailPreviewAdapter;

    private String printerMacId;
    private String printerType;
    private SharedPreferences sharedPreferences;
    private String from_date;
    private String to_date;
    private String locationCode;
    private String customer_name;
    private String customer_code;
    private JSONObject jsonObject;
    private RequestQueue requestQueue;
    private String url;
    private JsonObjectRequest jsonObjectRequest;
    ArrayList<ReceiptDetailsModel> receiptDetails;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_detail_preview);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Receipt Details Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.w("activity_cg",getClass().getSimpleName().toString());

        companyNametext =findViewById (R.id.company_name);
        companyAddress1Text =findViewById (R.id.company_addr1);
        companyAddress2Text =findViewById (R.id.company_addr2);
        companyAddress3Text =findViewById (R.id.company_addr3);
        companyGstText =findViewById (R.id.company_gst);
        companyPhoneText =findViewById (R.id.company_phone);
        recpt_fromdatel = findViewById(R.id.recpt_fromdate);
         recpt_todatel = findViewById(R.id.recpt_todate);
        loc_recptl = findViewById(R.id.loc_recpt);
        rv_recpt_perview_listl =  findViewById(R.id.rv_recpt_perview_list);

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
            getReceiptDetails(customer_code,fromDateString,toDateString,1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getReceiptDetails(String customer_id,String from_date,String to_date,int copy) throws JSONException {
        jsonObject=new JSONObject();
        jsonObject.put("User",username);
        jsonObject.put("CustomerCode",customer_id);
        jsonObject.put("FromDate",from_date);
        jsonObject.put("ToDate",to_date);

        requestQueue = Volley.newRequestQueue(this);
        url= Utils.getBaseUrl(this) +"ReportReceiptDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_Summary:",url+"/"+jsonObject.toString());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        receiptDetails =new ArrayList<>();
        ArrayList<ReceiptDetailsModel.ReceiptDetails> receiptDetailsList=new ArrayList<>();
        ArrayList<ReceiptDetailsModel.ReceiptDetails.InvoiceDetails> invoiceDetailsList=new ArrayList<>();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try{
                        Log.w("ReceiptDetailsRes:",response.toString());
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray responseData=response.optJSONArray("responseData");
                            assert responseData != null;
                            if (responseData.length()>0){
                                JSONObject object=responseData.optJSONObject(0);
                                ReceiptDetailsModel receiptModel =new ReceiptDetailsModel();
                                receiptModel.setFromDate(object.optString("fromDate"));
                                receiptModel.setToDate(object.optString("toDate"));
                                receiptModel.setCustomerName(object.optString("customerName"));
                                receiptModel.setCustomerCode(object.optString("customerCode"));
                                JSONArray detailsArray=object.optJSONArray("reportReceiptDetails");
                                for (int i = 0; i< Objects.requireNonNull(detailsArray).length(); i++){

                                    // Get the Receipt details for the Particular products
                                    JSONObject detailsObject=detailsArray.getJSONObject(i);
                                    ReceiptDetailsModel.ReceiptDetails details =new ReceiptDetailsModel.ReceiptDetails();
                                    details.setReceiptNumber(detailsObject.optString("receiptNo"));
                                    details.setReceiptDate(detailsObject.optString("receiptDate"));
                                    details.setPaymode(detailsObject.optString("paymode"));
                                    details.setPaymodeTotal(detailsObject.optString("paymodeTotal"));
                                    details.setReceiptTotal(detailsObject.optString("receiptTotal"));
                                    details.setCustomerCode(detailsObject.optString("customerCode"));
                                    details.setCustomerName(detailsObject.optString("customerName"));
                                    details.setChequeDate(detailsObject.optString("chequeDate"));
                                    details.setBankCode(detailsObject.optString("bankCode"));
                                    details.setChequeNo(detailsObject.optString("chequeNo"));

                                    receiptDetailsList.add(details);

                                    // Get the Invoice Details for the Particular products

                                    JSONArray invoiceArray=detailsObject.optJSONArray("reportReceiptInvoiceDetails");
                                    for (int j=0;j<invoiceArray.length();j++){
                                        JSONObject invoiceObject=invoiceArray.optJSONObject(j);
                                        ReceiptDetailsModel.ReceiptDetails.InvoiceDetails invoiceDetails=new ReceiptDetailsModel.ReceiptDetails.InvoiceDetails();
                                        invoiceDetails.setInvoiceNumber(invoiceObject.optString("invoiceNo"));
                                        invoiceDetails.setInvoiceDate(invoiceObject.optString("invoiceDate"));
                                        invoiceDetails.setNetTotal(invoiceObject.optString("netTotal"));
                                        invoiceDetailsList.add(invoiceDetails);
                                    }
                                    details.setInvoiceDetailsList(invoiceDetailsList);
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
                        if (invoiceDetailsList.size()>0 || receiptDetailsList.size()>0){
                            setReceiptAdapter(receiptDetailsList);
                        }else {
                            finish();
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

    private void setReceiptAdapter(ArrayList<ReceiptDetailsModel.ReceiptDetails> receiptsList) {
        receiptDetailPreviewAdapter = new ReceiptDetailPreviewAdapter(this, receiptsList);
        rv_recpt_perview_listl.setHasFixedSize(true);
        rv_recpt_perview_listl.setLayoutManager(new LinearLayoutManager(this));
        rv_recpt_perview_listl.setAdapter(receiptDetailPreviewAdapter);
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
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
                        setReceiptDetailsPrint(receiptDetails,1);
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

    public void setReceiptDetailsPrint(ArrayList<ReceiptDetailsModel> receiptDetails,int copy) throws IOException {
        if (validatePrinterConfiguration()){
            if (printerType.equals("Zebra Printer")){
                ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(this,printerMacId);
                zebraPrinterActivity.printReceiptDetails(copy,receiptDetails);
            }else if (printerType.equals("TSC Printer")){
                TSCPrinter printer=new TSCPrinter(this,printerMacId,"ReceiptDetails");
                printer.printReceiptDetail(1,from_date,to_date,receiptDetails);
            } else {
                Toast.makeText(getApplicationContext(),"This Printer not Support...!",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Please configure the Printer",Toast.LENGTH_SHORT).show();
        }
    }

}
