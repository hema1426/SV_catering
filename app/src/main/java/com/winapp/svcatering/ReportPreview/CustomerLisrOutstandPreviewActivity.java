package com.winapp.svcatering.ReportPreview;

import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import com.winapp.svcatering.ReportPreview.adapter.SapRoCustomerOutstandingARPreviewAdapter;
import com.winapp.svcatering.ReportPreview.adapter.SapRoCustomerOutstandingPreviewAdapter;
import com.winapp.svcatering.model.CustomerStateModel;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CustomerLisrOutstandPreviewActivity extends AppCompatActivity {

    String companyId = "1";
    String locationCode;
    SweetAlertDialog pDialog;
    private ArrayList<CustomerStateModel> customerStateList ;
    private ArrayList<CustomerStateModel.CustInvoiceDetails> custInvoiceDetailsList ;
    private ArrayList<CustomerStateModel.CustInvoiceDetailsAR> custInvoiceDetailsARList ;
    private TextView fromdat,todat,nettotal,nettotal_txt_final,nettotalAr1;
    private TextView balance,balanceAr1,balance_final,cust_name;
    private RecyclerView customerListView;
    private RecyclerView customerListView1;
    private LinearLayout ArCustlistLayl;
    private SapRoCustomerOutstandingPreviewAdapter adapter;
    private SapRoCustomerOutstandingARPreviewAdapter adapter1;
    double mNettotal =0.0;
    double mBalance =0.0;
    double mNettotal1 =0.0;
    double mBalance1 =0.0;
    double mNettotalFinal =0.0;
    double mBalanceFinal =0.0;
    private String printerMacId;
    private String printerType;
    private SharedPreferences sharedPreferences;
    private String from_date;
    private String to_date;
    public String fromdatetxt = "";
    public String todatetxt = "";
    public String loccodetxt = "";
    private String customer_name;
    private String customer_code;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sap_ro_customer_outstand_preview);
        getSupportActionBar().setTitle("Customer Outstanding Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.w("activity_cg",getClass().getSimpleName().toString());

        fromdat =findViewById (R.id.fromdate_txt);
        todat =findViewById (R.id.todate_txt);
        nettotal =findViewById (R.id.nettotal_txt);
        nettotalAr1 =findViewById (R.id.nettotal_txt1);
        nettotal_txt_final =findViewById (R.id.nettotal_txt_final);
        balance =findViewById (R.id.balance_txt);
        balanceAr1 =findViewById (R.id.balance_txt1);
        balance_final =findViewById (R.id.balance_txt_final);
        cust_name =findViewById (R.id.customer_txt);
        customerListView = findViewById (R.id.rv_customerlist);
        customerListView1 = findViewById (R.id.rv_customerlist1);
        ArCustlistLayl = findViewById (R.id.ArCustlistLay);

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
        fromdat.setText(Utils.getCurrentDateWithSlace());
        try {
            getCustomerStatement(customer_code,"",fromDateString,toDateString,"",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getCustomerStatement(String customer_id,String customer_name,String from_date,String to_date, String status,int copy) throws JSONException {

        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CustomerCode",customer_id);
        jsonObject.put("LocationCode",loccodetxt);
      /*  jsonObject.put("Status",status);
        jsonObject.put("FromDate",from_date);
        jsonObject.put("ToDate",to_date);*/
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"ReportCustomerOutstandingInvoice";

        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url+"/"+jsonObject.toString());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();

        customerStateList =new ArrayList<>();
        custInvoiceDetailsList =new ArrayList<>();
        custInvoiceDetailsARList =new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try{
                        Log.w("CustStat:",response.toString());
                        pDialog.dismiss();
                        String statusCode=response.optString("statusCode");
                        String statusMessage=response.optString("statusMessage");
                        if (statusCode.equals("1")){
                            JSONArray  jsonArray=response.optJSONArray("responseData");
                            assert jsonArray != null;
                            if(jsonArray.length()>0) {
                                JSONObject detailObject = jsonArray.optJSONObject(0);

                                CustomerStateModel model = new CustomerStateModel();
                                model.setCustomerName(detailObject.optString("customerName"));

                                double nettotal1 = 0.0;
                                double nettotal2 = 0.0;
                                double balance1 = 0.0;
                                double balance2 = 0.0;

                                JSONArray jsonArray1 = detailObject.optJSONArray("reportCustomerStatementDetails");

                                for (int i = 0; i < Objects.requireNonNull(jsonArray1).length(); i++) {
                                    JSONObject object = jsonArray1.optJSONObject(i);
                                    CustomerStateModel.CustInvoiceDetails custInvoiceDetailModel = new CustomerStateModel.CustInvoiceDetails();

                                    custInvoiceDetailModel.setInvoiceNumber(object.optString("invoiceNo"));
                                    custInvoiceDetailModel.setInvoiceDate(object.optString("invoiceDate"));
                                    custInvoiceDetailModel.setNetTotal(object.optString("netTotal"));
                                    custInvoiceDetailModel.setBalanceAmount(object.optString("balance"));

                                    nettotal1 += Double.parseDouble(object.optString("netTotal"));
                                    balance1 += Double.parseDouble(object.optString("balance"));
                                    mNettotal = Double.parseDouble(twoDecimalPoint(nettotal1));
                                    mBalance = Double.parseDouble(twoDecimalPoint(balance1));

                                    custInvoiceDetailsList.add(custInvoiceDetailModel);
                                }

                                JSONArray jsonArray2 = detailObject.optJSONArray("reportCustomerARCreditMemoStatementDetails");
                                if (jsonArray2.length() > 0) {
                                    for (int i = 0; i < Objects.requireNonNull(jsonArray2).length(); i++) {
                                        JSONObject object = jsonArray2.optJSONObject(i);
                                        CustomerStateModel.CustInvoiceDetailsAR custInvoiceDetailModel1 = new CustomerStateModel.CustInvoiceDetailsAR();

                                        custInvoiceDetailModel1.setInvoiceNumber(object.optString("arInvoiceNo"));
                                        custInvoiceDetailModel1.setInvoiceDate(object.optString("arInvoiceDate"));
                                        custInvoiceDetailModel1.setNetTotal(object.optString("arNetTotal"));
                                        custInvoiceDetailModel1.setBalanceAmount(object.optString("arBalance"));

                                        nettotal2 += Double.parseDouble(object.optString("arNetTotal"));
                                        balance2 += Double.parseDouble(object.optString("arBalance"));
                                        mNettotal1 = Double.parseDouble(twoDecimalPoint(nettotal2));
                                        mBalance1 = Double.parseDouble(twoDecimalPoint(balance2));

                                        custInvoiceDetailsARList.add(custInvoiceDetailModel1);
                                    }
                                }


                                model.setCustInvoiceDetailList(custInvoiceDetailsList);
                                model.setCustInvoiceDetailsARList(custInvoiceDetailsARList);
                                customerStateList.add(model);

                                if (custInvoiceDetailsList.size() > 0) {
                                    setCustomerAdapter(customerStateList);
                                    nettotal.setText(String.valueOf(mNettotal));
                                    balance.setText(String.valueOf(mBalance));
                                }
                                if (custInvoiceDetailsARList.size() > 0) {
                                    setCustomerAdapterAR(customerStateList);
                                    ArCustlistLayl.setVisibility(View.VISIBLE);

                                    nettotalAr1.setText(String.valueOf(mNettotal1));
                                    balanceAr1.setText(String.valueOf(mBalance1));
                                    mNettotalFinal = mNettotal - mNettotal1;
                                    mBalanceFinal = mBalance - mBalance1;
                                    nettotal_txt_final.setText(twoDecimalPoint(mNettotalFinal));
                                    balance_final.setText(twoDecimalPoint(mBalanceFinal));
                                    Log.w("custnettot", "" + mNettotal + ".." + mBalance);
                                    Log.w("custnettot11", "" + mNettotal1 + ".." + mBalance1);
                                    Log.w("custnettotfinal", "" + mNettotalFinal + ".." + mBalanceFinal);
                                } else {
                                    ArCustlistLayl.setVisibility(View.GONE);
                                }

                            }
                            else {
                                pDialog.dismiss();
                                finish();
                                Toast.makeText(getApplicationContext(),"No Record Found...",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            finish();
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

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    public void setCustomerAdapter(ArrayList<CustomerStateModel> customerStateLists ) {
        cust_name.setText(customerStateLists.get(0).getCustomerName());
        customerListView.setHasFixedSize(true);
        customerListView.setLayoutManager(new LinearLayoutManager(CustomerLisrOutstandPreviewActivity.this, LinearLayoutManager.VERTICAL, false));
        adapter = new SapRoCustomerOutstandingPreviewAdapter(CustomerLisrOutstandPreviewActivity.this, custInvoiceDetailsList, "Transfer Detail");
        customerListView.setAdapter(adapter);
    }
    public void setCustomerAdapterAR(ArrayList<CustomerStateModel> customerStateLists ) {
      //  cust_name.setText(customerStateLists.get(0).getCustomerName());
        customerListView1.setHasFixedSize(true);
        customerListView1.setLayoutManager(new LinearLayoutManager(CustomerLisrOutstandPreviewActivity.this, LinearLayoutManager.VERTICAL, false));
        adapter1 = new SapRoCustomerOutstandingARPreviewAdapter(CustomerLisrOutstandPreviewActivity.this, custInvoiceDetailsARList, "Transfer Detail");
        customerListView1.setAdapter(adapter1);
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
                        setCustomerOutstandingPrint(1);
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

    public void setCustomerOutstandingPrint(int copy) throws IOException {
        if (validatePrinterConfiguration()){
            if (printerType.equals("TSC Printer")){
                TSCPrinter printer=new TSCPrinter(this,printerMacId,"CustomerOutstanding");
                printer.printCustomerOutstanding(copy,customerStateList,Utils.getCurrentDateWithSlace());
            }
        }else {
            Toast.makeText(getApplicationContext(),"Please configure the Printer",Toast.LENGTH_SHORT).show();
        }
    }
}