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
import com.winapp.svcatering.ReportPreview.adapter.RoInvoicebySummaryPreviewAdapter;
import com.winapp.svcatering.adapter.SapSalesSumPreviewPrintAdapter;
import com.winapp.svcatering.model.InvoiceSummaryModel;
import com.winapp.svcatering.model.ReportSalesSummaryModel;
import com.winapp.svcatering.model.ReportStockSummaryModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
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

public class RoInvoicebySummaryPreviewActivity extends AppCompatActivity {
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
    private ArrayList<InvoiceSummaryModel> invoiceSummaryModel ;
    private ArrayList<InvoiceSummaryModel.SummaryDetails> invoiceSummaryDetailsList ;
    private TextView fromdatel;
    private TextView todatel;
    private TextView locationl;
    public String fromdatetxt = "17-05-2022";
    public String todatetxt = "12_09-2022";
    public String loccodetxt = "HQ";
    private RecyclerView rv_invsum_perv_listl;
    public RoInvoicebySummaryPreviewAdapter roInvoicebySummaryPreviewAdapter;
    public double sum_balamt = 0.0;
    public double sum_totalamt = 0.0;
    public double sum_paidamt = 0.0;
    public TextView balancel;
    public TextView nettotal;
    public TextView paidl;

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
    private ArrayList<ReportSalesSummaryModel> reportSalesSummaryList;
    private ArrayList<ReportSalesSummaryModel.ReportSalesSummaryDetails> reportSalesSummaryDetailsList;
    private ArrayList<ReportSalesSummaryModel.ReportSalesSummaryInvDetails> reportSalesSummaryInvDetailsList;
    private ArrayList<ReportStockSummaryModel> reportStockSummaryList;
    private ArrayList<ReportStockSummaryModel.ReportStockSummaryDetails> reportStockSummaryDetailsList;
    private ArrayList<InvoiceSummaryModel> invoiceSummaryList;
    private SessionManager session;
    private HashMap<String,String> user;
    private String companyCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ro_invoicebysummary_preview);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Invoice By Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.w("activity_cg",getClass().getSimpleName().toString());

        companyNametext =findViewById (R.id.company_name);
        companyAddress1Text =findViewById (R.id.company_addr1);
        companyAddress2Text =findViewById (R.id.company_addr2);
        companyAddress3Text =findViewById (R.id.company_addr3);
        companyGstText =findViewById (R.id.company_gst);
        companyPhoneText =findViewById (R.id.company_phone);
        fromdatel = findViewById(R.id.fromdate_invsum);
        todatel = findViewById(R.id.todate_invsum);
        locationl = findViewById(R.id.loc_invsum);
        rv_invsum_perv_listl =  findViewById(R.id.rv_invsum_perv_list);
        balancel = findViewById(R.id.ro_balance_invsum);
        nettotal = findViewById(R.id.ro_nettotal_invsum);
        paidl = findViewById(R.id.ro_paid_invsum);
        session=new SessionManager(this);
        user=session.getUserDetails();
        username=user.get(SessionManager.KEY_USER_NAME);
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);

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

        fromdatel.setText(fromdatetxt);
        todatel.setText(todatetxt);
        locationl.setText(loccodetxt);

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
            getInvoicesBySummary(fromDateString,toDateString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getInvoicesBySummary(String from_date,String to_date) throws JSONException {
        // Initialize a new RequestQueue instance
        jsonObject=new JSONObject();
        jsonObject.put("User",username);
        jsonObject.put("FromDate",from_date);
        jsonObject.put("ToDate",to_date);
        jsonObject.put("LocationCode",locationCode);
        requestQueue = Volley.newRequestQueue(this);
        url= Utils.getBaseUrl(this) +"ReportSalesSummary";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_Summary:",url+"/"+jsonObject.toString());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        invoiceSummaryList=new ArrayList<>();
        reportSalesSummaryList =new ArrayList<>();
        reportSalesSummaryDetailsList =new ArrayList<>();
        reportSalesSummaryInvDetailsList=new ArrayList<>();
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try{
                Log.w("ReportSalesSummary:",response.toString());

                pDialog.dismiss();
                String statusCode=response.optString("statusCode");
                String statusMessage=response.optString("statusMessage");
                if (statusCode.equals("1")){
                    JSONArray summaryDetailsArray=response.optJSONArray("responseData");
                    assert summaryDetailsArray != null;
                    JSONObject detailObject=summaryDetailsArray.optJSONObject(0);
                    ReportSalesSummaryModel model=new ReportSalesSummaryModel();
                    model.setCompanyId(detailObject.optString("user"));
                    model.setCashInHand(detailObject.optString("cashInHand"));
                    model.setTotalSales(detailObject.optString("totalSales"));
                    model.setCashReceived(detailObject.optString("cashReceived"));
                    model.setRefunded(detailObject.optString("refunded"));

                    JSONArray cashSalesArray=detailObject.optJSONArray("cashSales");
                    for (int i = 0; i< Objects.requireNonNull(cashSalesArray).length(); i++){
                        JSONObject objectItem= cashSalesArray.optJSONObject(i);
                        ReportSalesSummaryModel.ReportSalesSummaryDetails reportSalesSummaryModel =
                                new ReportSalesSummaryModel.ReportSalesSummaryDetails();

                        reportSalesSummaryModel.setTransNo(objectItem.optString("transactionNo"));
                        reportSalesSummaryModel.setCustomer(objectItem.optString("customerName"));
                        reportSalesSummaryModel.setAmount(objectItem.optString("amount"));
                        reportSalesSummaryModel.setType(objectItem.optString("type"));
                        reportSalesSummaryModel.setPaymentDate(objectItem.optString("paymentDate"));

                        reportSalesSummaryDetailsList.add(reportSalesSummaryModel);
                    }
                    JSONArray invoiceArray=detailObject.optJSONArray("otherSales");
                    for (int i = 0; i< Objects.requireNonNull(invoiceArray).length(); i++){
                        JSONObject objectInv= invoiceArray.optJSONObject(i);
                        ReportSalesSummaryModel.ReportSalesSummaryInvDetails reportSalesSummaryInvModel =
                                new ReportSalesSummaryModel.ReportSalesSummaryInvDetails();

                        reportSalesSummaryInvModel.setTransNo(objectInv.optString("transactionNo"));
                        reportSalesSummaryInvModel.setCustomer(objectInv.optString("customerName"));
                        reportSalesSummaryInvModel.setAmount(objectInv.optString("amount"));
                        reportSalesSummaryInvModel.setType(objectInv.optString("type"));
                        reportSalesSummaryInvModel.setBalanceAmount(objectInv.optString("balanceAmount"));
                        reportSalesSummaryInvModel.setPaymentDate(objectInv.optString("paymentDate"));

                        reportSalesSummaryInvDetailsList.add(reportSalesSummaryInvModel);
                    }

                    model.setReportSalesSummaryDetailsList(reportSalesSummaryDetailsList);
                    if(reportSalesSummaryInvDetailsList.size()>0) {
                        model.setReportSalesSummaryInvDetailsList(reportSalesSummaryInvDetailsList);
                    }
                    reportSalesSummaryList.add(model);
                    if (reportSalesSummaryInvDetailsList.size() > 0 || reportSalesSummaryDetailsList.size() > 0){
                        setAdapter(reportSalesSummaryDetailsList);
                    }else {
                        finish();
                        Toast.makeText(getApplicationContext(),"No Record Found...",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),statusMessage,Toast.LENGTH_SHORT).show();
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
    public void setAdapter(ArrayList<ReportSalesSummaryModel.ReportSalesSummaryDetails> salesSummaryDetailsArrayList ) {

        rv_invsum_perv_listl.setHasFixedSize(true);
        rv_invsum_perv_listl.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        SapSalesSumPreviewPrintAdapter sapSalesSumPreviewPrintAdapter = new SapSalesSumPreviewPrintAdapter(this, salesSummaryDetailsArrayList, "sales summary");
        rv_invsum_perv_listl.setAdapter(sapSalesSumPreviewPrintAdapter);
    }

    private void setReceiptSumAdapter(ArrayList<InvoiceSummaryModel.SummaryDetails> invoiceSummaryList) {

        for (int i=0;i<invoiceSummaryList.size();i++) {
            sum_totalamt += Double.parseDouble(invoiceSummaryList.get(i).getNetTotal());
            sum_balamt += Double.parseDouble(invoiceSummaryList.get(i).getBalanceAmount());
            sum_paidamt += Double.parseDouble(invoiceSummaryList.get(i).getPaidAmount());

        }
        nettotal.setText(twoDecimalPoint(sum_totalamt));
        balancel.setText(twoDecimalPoint(sum_balamt));
        paidl.setText(twoDecimalPoint(sum_paidamt));

        roInvoicebySummaryPreviewAdapter = new RoInvoicebySummaryPreviewAdapter(this, invoiceSummaryList);
        rv_invsum_perv_listl.setHasFixedSize(true);
        rv_invsum_perv_listl.setLayoutManager(new LinearLayoutManager(this));
        rv_invsum_perv_listl.setAdapter(roInvoicebySummaryPreviewAdapter);
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
                        setInvoiceSummaryPrint(1);
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

    public void setInvoiceSummaryPrint(int copy) throws IOException {
        if (validatePrinterConfiguration()){
            if (printerType.equals("TSC Printer")){
                TSCPrinter printer=new TSCPrinter(this,printerMacId,"InvoiceBySummary");
                printer.printReportSalesSummary(copy,from_date, to_date,reportSalesSummaryList);
            }
        }else {
            Toast.makeText(getApplicationContext(),"Please configure the Printer",Toast.LENGTH_SHORT).show();
        }
    }

}