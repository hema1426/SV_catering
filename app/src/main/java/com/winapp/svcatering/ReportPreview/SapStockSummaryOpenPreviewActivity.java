package com.winapp.svcatering.ReportPreview;


import android.annotation.SuppressLint;
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
import com.winapp.svcatering.ReportPreview.adapter.SapStockSumOpenPreviewPrintAdapter;
import com.winapp.svcatering.model.InvoiceSummaryModel;
import com.winapp.svcatering.model.ReportStockSummaryModel;
import com.winapp.svcatering.model.StockSummaryReportOpenModel;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SapStockSummaryOpenPreviewActivity extends AppCompatActivity {
    private SweetAlertDialog pDialog;
    double sum_salesummaryamt = 0.0;
    double sum_invsummaryamt =0.0;
    double sum_invsummarybalance = 0.0;

    private RequestQueue requestQueue;

    private String url;
    private ArrayList<StockSummaryReportOpenModel> reportStockSummaryOpenList;
    private ArrayList<StockSummaryReportOpenModel> reportStockSummaryOpenList1;
    private ArrayList<StockSummaryReportOpenModel> reportStockSummaryOpenList2 = new ArrayList<>();

    private JSONObject jsonBody;
    public SapStockSumOpenPreviewPrintAdapter stockSumOpenPreviewPrintAdapter;

    private TextView fromdatel;
    private TextView todatel;
    String username = "";
    String companyId = "";
    private ArrayList<InvoiceSummaryModel> invoiceSummaryModel ;
    private ArrayList<InvoiceSummaryModel.SummaryDetails> invoiceSummaryDetailsList ;
    private TextView locationl;
    public String fromdatetxt = "";
    public String todatetxt = "";
    public String loccodetxt = "";
    private RecyclerView rv_stocksum_perv_listl;
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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sap_ro_stocksummary_open_preview_print);
        getSupportActionBar().setTitle("Stock Summary Opening Balance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.w("activity_cg",getClass().getSimpleName().toString());

//        companyNametext =findViewById (R.id.company_name);
//        companyAddress1Text =findViewById (R.id.company_addr1);
//        companyAddress2Text =findViewById (R.id.company_addr2);
//        companyAddress3Text =findViewById (R.id.company_addr3);
//        companyGstText =findViewById (R.id.company_gst);
//        companyPhoneText =findViewById (R.id.company_phone);
        rv_stocksum_perv_listl =findViewById (R.id.rv_stocksum_open_perview_list);

        todatel =findViewById (R.id.todate_open);
        fromdatel =findViewById (R.id.fromdate_open);
//        userName =findViewById (R.id.user_pre);

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

        try {
            getReportStockSummaryOpening(1,fromDateString,toDateString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getReportStockSummaryOpening(int copy,String from_d,String to_d) throws JSONException {
        // Initialize a new RequestQueue instance
        jsonBody = new JSONObject();
        jsonBody.put("Warehouse",locationCode);
        jsonBody.put("FromDate",from_d);
        jsonBody.put("ToDate",to_d);

        requestQueue = Volley.newRequestQueue(this);
         url= Utils.getBaseUrl(this) +"ReportStockSummaryAllItem";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url_summaryOpen:",url+"-"+jsonBody.toString());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        reportStockSummaryOpenList =new ArrayList<>();
        reportStockSummaryOpenList1 =new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try{
                        Log.w("ReportStockSummaryOpen:",response.toString());

                        pDialog.dismiss();
                        String statusCode=response.optString("statusCode");
                        String statusMessage=response.optString("statusMessage");
                        if (statusCode.equals("1")){
                            JSONArray  stockDetailsArray=response.optJSONArray("responseData");
                            if (stockDetailsArray.length() > 0) {
                                assert stockDetailsArray != null;
                                JSONObject object1 = stockDetailsArray.optJSONObject(0);
                                ReportStockSummaryModel model = new ReportStockSummaryModel();
                                //   model.setDeviceId(object1.optString(""));
                                //  model.setCompantId(object1.optString(""));
                                String openqty = "";
                                String closeqty = "";
                                String inqty = "";
                                String outqty = "";

                                for (int i = 0; i < stockDetailsArray.length(); i++) {
                                    JSONObject object = stockDetailsArray.optJSONObject(i);
                                    if (!object.optString("openingQty").isEmpty() &&
                                            object.optString("openingQty") != null &&
                                            !object.optString("openingQty").equals("") &&
                                            !object.optString("openingQty").equalsIgnoreCase("null")) {
                                        openqty = object.optString("openingQty");
                                    } else {
                                        openqty = "";
                                    }
                                    if (!object.optString("closingQty").isEmpty() &&
                                            object.optString("closingQty") != null &&
                                            !object.optString("closingQty").equals("") &&
                                            !object.optString("closingQty").equalsIgnoreCase("null")) {
                                        closeqty = object.optString("closingQty");
                                    } else {
                                        closeqty = "";
                                    }
                                    if (!object.optString("inwardQty").isEmpty() &&
                                            object.optString("inwardQty") != null &&
                                            !object.optString("inwardQty").equals("") &&
                                            !object.optString("inwardQty").equalsIgnoreCase("null")) {
                                        inqty = object.optString("inwardQty");
                                    } else {
                                        inqty = "";
                                    }
                                    if (!object.optString("outwardQty").isEmpty() &&
                                            object.optString("outwardQty") != null &&
                                            !object.optString("outwardQty").equals("") &&
                                            !object.optString("outwardQty").equalsIgnoreCase("null")) {
                                        outqty = object.optString("outwardQty");
                                    } else {
                                        outqty = "";
                                    }

                                    StockSummaryReportOpenModel reportStockSummaryOpenModel = new StockSummaryReportOpenModel(
                                            object.optString("itemCode"),object.optString("itemName"),
                                            object.optString("warehouse"),openqty,
                                            inqty,outqty,closeqty
                                    );
                                    reportStockSummaryOpenList.add(reportStockSummaryOpenModel);
                                }
                                if (reportStockSummaryOpenList.size()>0) {

                                    for(int i=0;i<reportStockSummaryOpenList.size();i++) {
                                        if (!reportStockSummaryOpenList.get(i).getOpeningQty().equals("") &&
                                                !reportStockSummaryOpenList.get(i).getClosingQty().equals("") &&
                                                !reportStockSummaryOpenList.get(i).getInwardQty().equals("") &&
                                                !reportStockSummaryOpenList.get(i).getOutwardQty().equals("")) {

                                            reportStockSummaryOpenList1.add(reportStockSummaryOpenList.get(i));

                                        } else if (!reportStockSummaryOpenList.get(i).getOpeningQty().equals("") ||
                                                !reportStockSummaryOpenList.get(i).getClosingQty().equals("") ||
                                                !reportStockSummaryOpenList.get(i).getInwardQty().equals("") ||
                                                !reportStockSummaryOpenList.get(i).getOutwardQty().equals("")) {
                                            reportStockSummaryOpenList1.add(reportStockSummaryOpenList.get(i));
                                        }
                                    }

                                        Log.w("summmasize", "" + reportStockSummaryOpenList1.size());
                                            setAdapter(reportStockSummaryOpenList1);

                                  //  TSCPrinter printer=new TSCPrinter(this,printerMacId,"StockSummary");
                                    //   printer.printReportStockSummaryOpen(copy,from_date,to_date,locationCode,companyCode,reportStockSummaryList);
                                  //  clearAllSelection();
                                }else {
                                    Toast.makeText(getApplicationContext(),"No StockSummary Found...",Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(getApplicationContext(),"No StockSummary Found...",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(),statusMessage,Toast.LENGTH_SHORT).show();
                        }

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


    public void setAdapter(ArrayList<StockSummaryReportOpenModel> salesSummaryDetailsArrayList ) {
        rv_stocksum_perv_listl.setHasFixedSize(true);
        rv_stocksum_perv_listl.setLayoutManager(new LinearLayoutManager(SapStockSummaryOpenPreviewActivity.this, LinearLayoutManager.VERTICAL, false));
        stockSumOpenPreviewPrintAdapter = new SapStockSumOpenPreviewPrintAdapter(SapStockSummaryOpenPreviewActivity.this, salesSummaryDetailsArrayList, "sales summary");
        rv_stocksum_perv_listl.setAdapter(stockSumOpenPreviewPrintAdapter);
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
                TSCPrinter printer=new TSCPrinter(this,printerMacId,"StockSummaryOpen");
                printer.printReportStockSummaryOpen(copy,from_date,to_date,reportStockSummaryOpenList1);
            }
        }else {
            Toast.makeText(getApplicationContext(),"Please configure the Printer",Toast.LENGTH_SHORT).show();
        }
    }
}
