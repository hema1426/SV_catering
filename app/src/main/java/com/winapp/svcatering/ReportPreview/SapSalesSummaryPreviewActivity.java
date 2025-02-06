package com.winapp.svcatering.ReportPreview;


import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import com.winapp.svcatering.ReportPreview.adapter.SapSalesSumPreviewPrintAdapter;
import com.winapp.svcatering.ReportPreview.adapter.SapSalesSum_InvoicePreviewPrintAdapter;
import com.winapp.svcatering.model.InvoiceSummaryModel;
import com.winapp.svcatering.model.ReportSalesSummaryModel;
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

public class SapSalesSummaryPreviewActivity extends AppCompatActivity {
    private TextView companyNametext;
    private TextView companyAddress1Text;
    private TextView companyAddress2Text;
    private TextView companyAddress3Text;
    private TextView companyPhoneText;
    private LinearLayout Invoicelist_lay;
    private TextView companyGstText;
    private String company_name;
    private String company_address1;
    private String company_address2;
    private String company_address3;

    private TextView customerCode;
    private TextView customerName;
    private TextView userName;
    private TextView subTotall;
    private TextView netTotall;
    private TextView gst_txt;
    private SweetAlertDialog pDialog;
    private String doNo;
    private RecyclerView rv_salessum_Previewlist;
    private RecyclerView rv_salesSum_Invoicelist;
    double sum_salesummaryamt = 0.0;
    double sum_invsummaryamt =0.0;
    double sum_invsummarybalance = 0.0;
    private ArrayList<ReportSalesSummaryModel> reportSalesSummaryList;
    private ArrayList<ReportSalesSummaryModel.ReportSalesSummaryDetails> reportSalesSummaryDetailsList;
    private ArrayList<ReportSalesSummaryModel.ReportSalesSummaryInvDetails> reportSalesSummaryInvDetailsList;
    public SapSalesSumPreviewPrintAdapter sapSalesSumPreviewPrintAdapter;
    public SapSalesSum_InvoicePreviewPrintAdapter sapSalesSumInvoicePreviewPrintAdapter;
    private TextView fromdatel;
    private TextView totalreceived;
    private TextView totalreceivedInv;
    private TextView Invbalance;
    private TextView totalSales;
    private TextView totalcash_received;
    private TextView totalrefund;
    private TextView totalcashinhand;
    private TextView todatel;
    String username = "winapp";
    String companyId = "1";
    private ArrayList<InvoiceSummaryModel> invoiceSummaryModel ;
    private ArrayList<InvoiceSummaryModel.SummaryDetails> invoiceSummaryDetailsList ;
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
    private String customer_code = "";
    private JSONObject jsonObject;
    private String url;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sap_ro_salessummary_preview_print);
        getSupportActionBar().setTitle("Sales Summary Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.w("activity_cg",getClass().getSimpleName().toString());

        companyNametext =findViewById (R.id.company_name);
        companyAddress1Text =findViewById (R.id.company_addr1);
        companyAddress2Text =findViewById (R.id.company_addr2);
        companyAddress3Text =findViewById (R.id.company_addr3);
        companyGstText =findViewById (R.id.company_gst);
        companyPhoneText =findViewById (R.id.company_phone);

        Invoicelist_lay =findViewById (R.id.summaryInv_lay);
        totalreceived = findViewById (R.id.total_receivd);
        totalreceivedInv = findViewById (R.id.total_receivd_inv);
        Invbalance = findViewById (R.id.balance_inv);
        totalSales = findViewById (R.id.total_sales);
        totalcash_received = findViewById (R.id.total_cashReceivd);
        totalrefund = findViewById (R.id.total_refunded);
        totalcashinhand = findViewById (R.id.total_cash_inHand);

        todatel =findViewById (R.id.todate);
        fromdatel =findViewById (R.id.fromdate);
        userName =findViewById (R.id.user_pre);

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

        rv_salessum_Previewlist =findViewById (R.id.rv_salessum_perview_list);
        rv_salesSum_Invoicelist =findViewById (R.id.rv_salessum_invoice_list);

        try {
            getReportSalesSummary(1,fromDateString,toDateString,username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getReportSalesSummary(int copy,String fromDate,String toDate,String user) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("FromDate",fromDate);
//        jsonBody.put("LocationCode",loccodetxt);
        jsonBody.put("CustomerCode",customer_code);
        jsonBody.put("Status","");
        jsonBody.put("ToDate",toDate);
        jsonBody.put("User",user);
//        "Warehouse":"VAN 5"
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"ReportSalesSummary";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url_Report:",url+jsonBody);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        reportSalesSummaryList =new ArrayList<>();
        reportSalesSummaryDetailsList =new ArrayList<>();
        reportSalesSummaryInvDetailsList =new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try{
                        Log.w("ReportSalesSummary:",response.toString());

                        pDialog.dismiss();
                        String statusCode=response.optString("statusCode");
                        String statusMessage=response.optString("statusMessage");
                        if (statusCode.equals("1")){
                            JSONArray  summaryDetailsArray=response.optJSONArray("responseData");
                            assert summaryDetailsArray != null;
                            JSONObject detailObject=summaryDetailsArray.optJSONObject(0);
                            ReportSalesSummaryModel model=new ReportSalesSummaryModel();
                            model.setCompanyId(detailObject.optString("user"));
                            userName.setText(detailObject.optString("user"));


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

                                sum_salesummaryamt += Double.parseDouble(reportSalesSummaryModel.getAmount());

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

                                sum_invsummaryamt += Double.parseDouble(reportSalesSummaryInvModel.getAmount());
                                sum_invsummarybalance += Double.parseDouble(reportSalesSummaryInvModel.getBalanceAmount());

                                reportSalesSummaryInvDetailsList.add(reportSalesSummaryInvModel);
                            }
                            if(reportSalesSummaryDetailsList.size()>0) {
                                model.setReportSalesSummaryDetailsList(reportSalesSummaryDetailsList);
                                setAdapter(reportSalesSummaryDetailsList);
                            }
                            if(reportSalesSummaryInvDetailsList.size()>0) {
                                model.setReportSalesSummaryInvDetailsList(reportSalesSummaryInvDetailsList);
                                Invoicelist_lay.setVisibility(View.VISIBLE);
                                setAdapterInvoice(reportSalesSummaryInvDetailsList);
                            }
                            reportSalesSummaryList.add(model);
                            totalSales.setText(Utils.twoDecimalPoint(Double.parseDouble(reportSalesSummaryList.get(0).getTotalSales())));
                            totalcash_received.setText(Utils.twoDecimalPoint(Double.parseDouble(reportSalesSummaryList.get(0).getCashReceived())));
                            totalrefund.setText(Utils.twoDecimalPoint(Double.parseDouble(reportSalesSummaryList.get(0).getRefunded())));
                            totalcashinhand.setText(Utils.twoDecimalPoint(Double.parseDouble(reportSalesSummaryList.get(0).getCashInHand())));

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
        totalreceived.setText(twoDecimalPoint(sum_salesummaryamt));
        rv_salessum_Previewlist.setHasFixedSize(true);
        rv_salessum_Previewlist.setLayoutManager(new LinearLayoutManager(SapSalesSummaryPreviewActivity.this, LinearLayoutManager.VERTICAL, false));
        sapSalesSumPreviewPrintAdapter = new SapSalesSumPreviewPrintAdapter(SapSalesSummaryPreviewActivity.this, salesSummaryDetailsArrayList, "sales summary");
        rv_salessum_Previewlist.setAdapter(sapSalesSumPreviewPrintAdapter);
    }

    public void setAdapterInvoice(ArrayList<ReportSalesSummaryModel.ReportSalesSummaryInvDetails> salesSummaryDetailsArrayList ) {
        totalreceivedInv.setText(twoDecimalPoint(sum_invsummaryamt));
        Invbalance.setText(twoDecimalPoint(sum_invsummarybalance));
        rv_salesSum_Invoicelist.setHasFixedSize(true);
        rv_salesSum_Invoicelist.setLayoutManager(new LinearLayoutManager(SapSalesSummaryPreviewActivity.this, LinearLayoutManager.VERTICAL, false));
        sapSalesSumInvoicePreviewPrintAdapter = new SapSalesSum_InvoicePreviewPrintAdapter(SapSalesSummaryPreviewActivity.this, salesSummaryDetailsArrayList, "sales summary");
        rv_salesSum_Invoicelist.setAdapter(sapSalesSumInvoicePreviewPrintAdapter);
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
