package com.winapp.svcatering.ReportPreview;

import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

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
import com.winapp.svcatering.ReportPreview.adapter.RoSettleDenominatPreviewAdapter;
import com.winapp.svcatering.ReportPreview.adapter.RoSettleExpensPreviewAdapter;
import com.winapp.svcatering.ReportPreview.adapter.RoSettlementPreviewAdapter;
import com.winapp.svcatering.model.SettlementReceiptDetailModel;
import com.winapp.svcatering.model.SettlementReceiptModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.Utils;
import com.winapp.svcatering.zebraprinter.TSCPrinter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RoSettlementPreviewActivity extends AppCompatActivity {
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
    private ArrayList<SettlementReceiptDetailModel> settlementReceiptModelList ;
    private ArrayList<SettlementReceiptModel.CurrencyDenomination> denominationArrayList ;
    private ArrayList<SettlementReceiptModel.Expense> expenseArrayList ;
    private TextView setle_fromdatel;
    private TextView setle_todatel;
    private TextView setle_usernamel;
    public String fromdatetxt = "";
    public String todatetxt = "";
    private RecyclerView rv_settle_listl;
    private RecyclerView rv_settle_expens_listl;
    private RecyclerView rv_settle_donaminat_listl;
    public RoSettlementPreviewAdapter settlementPreviewAdapter;
    public RoSettleDenominatPreviewAdapter settleDenominatPreviewAdapter;
    public RoSettleExpensPreviewAdapter settleExpensPreviewAdapter;
    public LinearLayout settlelay;
    public LinearLayout expenselay;
    public LinearLayout denominatlay;
    private TextView invoice_amt;
    private TextView paid_amt;
    private TextView less_amt;
    private TextView cash_amt;
    private TextView cheque_amt;
    private TextView currenc_total;
    private TextView expense_total;
    private TextView grand_total;
    private TextView invoice_collect;
    private TextView discount_amt;
    private TextView net_amt;
    double currencytotal = 0.0;
    double expensetotal = 0.0;

    private String printerMacId;
    private String printerType;
    private SharedPreferences sharedPreferences;
    private String from_date;
    private String to_date;
    private String locationCode;
    private String customer_name;
    private String customer_code;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ro_settlement_preview);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settlement Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.w("activity_cg",getClass().getSimpleName().toString());

        setle_fromdatel = findViewById(R.id.setle_fromdate);
        setle_todatel = findViewById(R.id.setle_todate);
        setle_usernamel = findViewById(R.id.username_setle);
        rv_settle_listl = findViewById(R.id.rv_setle_perv_list);
        rv_settle_expens_listl = findViewById(R.id.rv_expense_perv_list);
        rv_settle_donaminat_listl = findViewById(R.id.rv_denominat_perv_list);
        expenselay = findViewById(R.id.expens_lay);
        denominatlay = findViewById(R.id.denominat_lay);
        invoice_amt = findViewById(R.id.totalInv_amt_setle);
        paid_amt = findViewById(R.id.total_paid_setle);
        less_amt = findViewById(R.id.total_less_setle);
        cash_amt = findViewById(R.id.total_cash_setle);
        cheque_amt = findViewById(R.id.total_cheque_setle);
        currenc_total = findViewById(R.id.total_denominat);
        expense_total = findViewById(R.id.total_expens);
        invoice_collect = findViewById(R.id.total_inv_setle);
        discount_amt = findViewById(R.id.total_discount_setle);
        net_amt = findViewById(R.id.netamt_setle);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        if (getIntent()!=null){
            fromdatetxt=getIntent().getStringExtra("fromDate");
            todatetxt=getIntent().getStringExtra("toDate");
            companyId=getIntent().getStringExtra("companyId");
            locationCode=getIntent().getStringExtra("locationCode");
            customer_name=getIntent().getStringExtra("customerName");
            customer_code=getIntent().getStringExtra("customerCode");
            username=getIntent().getStringExtra("userName");
        }

        from_date=fromdatetxt;
        to_date=todatetxt;
        try {
             getSettlementReport(customer_code,from_date,to_date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getSettlementReport(String customer_id,String from_date,String to_date) throws JSONException {

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyId);
        jsonObject.put("CustomerCode",customer_id);
        jsonObject.put("LocationCode",locationCode);
        jsonObject.put("FromDate",from_date);
        jsonObject.put("EndDate",to_date);
        jsonObject.put("ModifyUser",username);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SalesApi/GetSettlementDetailReport?Requestdata="+jsonObject.toString();

        Log.w("Settlement_Summary:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        settlementReceiptModelList = new ArrayList<>();
        denominationArrayList = new ArrayList<>();
        expenseArrayList = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try{
                Log.w("SettlementResponse:",response.toString());
                JSONArray receiptDetailsArray=response.optJSONArray("DetailList");

//                invoice_amt.setText(twoDecimalPoint(Double.parseDouble(settlementReceiptModelList.get(0).getTotalInvoiceAmount())));
//                paid_amt.setText(twoDecimalPoint(Double.parseDouble(settlementReceiptModelList.get(0).getTotalPaidAmount())));
//                less_amt.setText(twoDecimalPoint(Double.parseDouble(settlementReceiptModelList.get(0).getTotalLessAmount())));
//                cash_amt.setText(twoDecimalPoint(Double.parseDouble(settlementReceiptModelList.get(0).getTotalCashAmount())));
//                cheque_amt.setText(twoDecimalPoint(Double.parseDouble(settlementReceiptModelList.get(0).getTotalChequeAmount())));
//
//                invoice_collect.setText(twoDecimalPoint(Double.parseDouble(settlementReceiptModelList.get(0).getTotalInvoiceAmount())));
//                discount_amt.setText(twoDecimalPoint(Double.parseDouble(settlementReceiptModelList.get(0).getTotalLessAmount())));
//                net_amt.setText(twoDecimalPoint(Double.parseDouble(settlementReceiptModelList.get(0).getTotalCashAmount())));
//
                for (int i=0;i<receiptDetailsArray.length();i++){
                    JSONObject receiptObject=receiptDetailsArray.optJSONObject(i);
                    SettlementReceiptDetailModel model =new SettlementReceiptDetailModel();
                    model.setReceiptNo(receiptObject.optString("ReceiptNo"));
                    model.setReceiptDate(receiptObject.optString("ReceiptDate"));
                    model.setCustomerName(receiptObject.optString("CustomerName"));
                    model.setCustomerCode(receiptObject.optString("CustomerCode"));
                    model.setPaidAmount(receiptObject.optString("PaidAmount"));
                    model.setCreditAmount(receiptObject.optString("CreditAmount"));
                    double finalAmount = Double.parseDouble(receiptObject.optString("PaidAmount")) - Double.parseDouble(receiptObject.optString("CreditAmount"));
                    model.setFinalPaidAmount(twoDecimalPoint(finalAmount));
                    model.setPaymode(receiptObject.optString("Paymode"));
                    receiptObject.optString("ChequeDate");
                    if(!receiptObject.optString("ChequeDate").isEmpty() &&
                            !receiptObject.optString("ChequeDate").equalsIgnoreCase("null")) {
                        String chequedate = receiptObject.optString("ChequeDate");
                        String date_value= chequedate.replaceAll("[^0-9]", "");
                        getDate(Long.parseLong(date_value));
                        model.setChequeDate(getDate(Long.parseLong(date_value)));
                    }

                    model.setBankCode(receiptObject.optString("BankCode"));
                    model.setChequeNo(receiptObject.optString("ChequeNo"));

                    settlementReceiptModelList.add(model);
                }
                JSONArray denominationArray=response.optJSONArray("DetailSettlementList");
                assert denominationArray != null;
                if (denominationArray.length()>0){
                    for (int i=0;i<denominationArray.length();i++){
                        JSONObject denominationObject=denominationArray.optJSONObject(i);
                        SettlementReceiptModel.CurrencyDenomination denomination=new SettlementReceiptModel.CurrencyDenomination();
                        denomination.setDenomination(denominationObject.optString("Denomination"));
                        denomination.setCount(denominationObject.optString("DenominationCount"));
                        denomination.setTotal(denominationObject.optString("DetTotal"));

                        // Adding the Denomination Details to the Arraylist
                        denominationArrayList.add(denomination);
                    }
                }

                JSONArray expenseArray=response.optJSONArray("ExpenseSettlementList");
                assert expenseArray!=null;
                if (denominationArray.length()>0){
                    for (int i=0;i<expenseArray.length();i++){
                        JSONObject expenseObject=expenseArray.optJSONObject(i);
                        SettlementReceiptModel.Expense expense=new SettlementReceiptModel.Expense();
                        expense.setExpeneName(expenseObject.optString("GroupName"));
                        expense.setExpenseTotal(expenseObject.optString("NetTotal"));

                        // Adding the expense Details to Arraylist
                        expenseArrayList.add(expense);
                    }
                }

                if (settlementReceiptModelList.size()>0){
                    setSettlementAdapter(settlementReceiptModelList);
                }
                if (denominationArrayList.size()>0){
                    denominatlay.setVisibility(View.VISIBLE);
                    setDonaminationAdapter(denominationArrayList);
                }
                if (expenseArrayList.size()>0){
                    expenselay.setVisibility(View.VISIBLE);
                    setExpenseAdapter(expenseArrayList);
                }
                pDialog.dismiss();

            }catch (Exception e){
                e.printStackTrace();
            }
            pDialog.dismiss();
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


    private void setSettlementAdapter(ArrayList<SettlementReceiptDetailModel> settlementReceiptModelList) {

        settlementPreviewAdapter= new RoSettlementPreviewAdapter(this, settlementReceiptModelList);
        rv_settle_listl.setHasFixedSize(true);
        rv_settle_listl.setLayoutManager(new LinearLayoutManager(this));
        rv_settle_listl.setAdapter(settlementPreviewAdapter);

    }


    private void setDonaminationAdapter(ArrayList<SettlementReceiptModel.CurrencyDenomination> denominationArrayList) {

        settleDenominatPreviewAdapter = new RoSettleDenominatPreviewAdapter(this, denominationArrayList);
        rv_settle_donaminat_listl.setHasFixedSize(true);
        rv_settle_donaminat_listl.setLayoutManager(new LinearLayoutManager(this));
        rv_settle_donaminat_listl.setAdapter(settleDenominatPreviewAdapter);

        for(int i = 0 ; i < denominationArrayList.size() ; i++) {
            currencytotal += Double.parseDouble(denominationArrayList.get(i).getTotal());
        }
        Log.e("currtotl",""+currencytotal +denominationArrayList.get(0).getTotal());
        currenc_total.setText(twoDecimalPoint(currencytotal));
    }

    private void setExpenseAdapter(ArrayList<SettlementReceiptModel.Expense> expenseArrayList) {

        settleExpensPreviewAdapter = new RoSettleExpensPreviewAdapter(this, expenseArrayList);
        rv_settle_expens_listl.setHasFixedSize(true);
        rv_settle_expens_listl.setLayoutManager(new LinearLayoutManager(this));
        rv_settle_expens_listl.setAdapter(settleExpensPreviewAdapter);

        for(int i = 0 ; i < expenseArrayList.size() ; i++) {
            expensetotal += Double.parseDouble(expenseArrayList.get(i).getExpenseTotal());
        }
        expense_total.setText(twoDecimalPoint(expensetotal));
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
                        setSettlementReportPrint(expenseArrayList,from_date,to_date,"",settlementReceiptModelList,denominationArrayList,1);
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

    public void setSettlementReportPrint(ArrayList<SettlementReceiptModel.Expense> expenseList,String  from_date,String to_date,
                                         String username,ArrayList<SettlementReceiptDetailModel> receiptlist,
                                         ArrayList<SettlementReceiptModel.CurrencyDenomination> denomination,
                                         int copy) throws IOException {
        if (validatePrinterConfiguration()){
            if (printerType.equals("TSC Printer")){
                TSCPrinter printer=new TSCPrinter(this,printerMacId,"Settlement");
               // printer.setSettlementPrint(expenseList,customer_name,from_date,to_date,username,receiptlist,denomination,copy);
            }
        }else {
            Toast.makeText(getApplicationContext(),"Please configure the Printer",Toast.LENGTH_SHORT).show();
        }
    }
}
