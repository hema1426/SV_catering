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
import com.winapp.svcatering.ReportPreview.adapter.RoReceiptSettlePreviewAdapter;
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

public class RoReceiptSettlePreviewActivity extends AppCompatActivity {
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
    private ArrayList<SettlementReceiptModel> settlementReceiptModelList ;
    private ArrayList<SettlementReceiptDetailModel> settlementReceiptDetailModelList ;

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
    public RoReceiptSettlePreviewAdapter settlementPreviewAdapter;
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
    private LinearLayout expens_total_layl;
    private TextView grand_total;
//    private TextView invoice_collect;
//    private TextView discount_amt;
//    private TextView net_amt;
    double currencytotal = 0.0;
    double expensetotal = 0.0;
    double expensetotalapi = 0.0;
    double denominattotalapi = 0.0;

    private LinearLayout excess_total_layl;
    private LinearLayout shortage_total_layl;
    private TextView total_shortage_txt;
    private TextView total_excess_txt;
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
        setContentView(R.layout.activity_ro_receipt_settle_preview);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settlement With Receipt");
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
        expens_total_layl = findViewById(R.id.expens_total_lay);
        excess_total_layl = findViewById(R.id.excess_total_lay);
        shortage_total_layl = findViewById(R.id.shortage_total_lay);
        total_excess_txt = findViewById(R.id.total_excess);
        total_shortage_txt = findViewById(R.id.total_shortage);

//        invoice_collect = findViewById(R.id.total_inv_setle);
//        discount_amt = findViewById(R.id.total_discount_setle);
//        net_amt = findViewById(R.id.netamt_setle);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        if (getIntent()!=null){
            fromdatetxt=getIntent().getStringExtra("fromDate");
            username=getIntent().getStringExtra("userName");
        }
      //  setle_fromdatel.setText(fromdatetxt);
        setle_usernamel.setText(username);

        from_date=fromdatetxt;
        to_date=todatetxt;
        try {
             getSettlementReport(from_date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getSettlementReport(String from_date) throws JSONException {

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("date",from_date);
        jsonObject.put("user",username);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"ReportSettlementWithReceipt";

        Log.w("Settl_receipt_Summary:",url + ".. "+jsonObject);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        settlementReceiptModelList = new ArrayList<>();
        settlementReceiptDetailModelList = new ArrayList<>();
        denominationArrayList = new ArrayList<>();
        expenseArrayList = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try{

                Log.w("SettlReceiptRes:",response.toString());
                String statusCode = response.optString("statusCode");
                if (statusCode.equals("1")) {


                    JSONArray resArray = response.optJSONArray("responseData");
                    for (int j = 0; j < Objects.requireNonNull(resArray).length(); j++) {

                        JSONObject resObject = resArray.optJSONObject(j);

                        setle_fromdatel.setText(resObject.getString("receiptDate"));

                        SettlementReceiptModel model1 = new SettlementReceiptModel();

                        invoice_amt.setText(twoDecimalPoint(Double.parseDouble(resObject.getString("totalInvoiceAmount"))));
                        paid_amt.setText(twoDecimalPoint(Double.parseDouble(resObject.getString("totalPaidAmount"))));
                        less_amt.setText(twoDecimalPoint(Double.parseDouble(resObject.getString("totalLessAmount"))));
                        cash_amt.setText(twoDecimalPoint(Double.parseDouble(resObject.getString("totalCashAmount"))));
                        cheque_amt.setText(twoDecimalPoint(Double.parseDouble(resObject.getString("totalCheckAmount"))));


                        model1.setTotalInvoiceAmount(resObject.optString("totalInvoiceAmount"));
                        model1.setTotalPaidAmount(resObject.optString("totalPaidAmount"));
                        model1.setTotalLessAmount(resObject.optString("totalLessAmount"));
                        model1.setTotalCashAmount(resObject.optString("totalCashAmount"));
                        model1.setTotalChequeAmount(resObject.optString("totalCheckAmount"));

                        JSONArray receiptDetailsArray = resObject.optJSONArray("reportSettlementWithReceiptDetails");

                        if (receiptDetailsArray.length() > 0) {

                            for (int i = 0; i < Objects.requireNonNull(receiptDetailsArray).length(); i++) {
                                JSONObject receiptObject = receiptDetailsArray.optJSONObject(i);
                                SettlementReceiptDetailModel model = new SettlementReceiptDetailModel();

                                model.setReceiptNo(receiptObject.optString("receiptNo"));
                                model.setReceiptDate(receiptObject.optString("receiptDate"));
                                model.setCustomerName(receiptObject.optString("customerName"));
                                model.setCustomerCode(receiptObject.optString("customerCode"));
                                model.setPaidAmount(receiptObject.optString("paymodeTotal"));
                                model.setCreditAmount(receiptObject.optString("receiptTotal"));
                                model.setPaymode(receiptObject.optString("paymode"));
                                   model.setChequeDate(receiptObject.optString("chequeDate"));
                                   model.setBankCode(receiptObject.optString("bankCode"));
                                   model.setChequeNo(receiptObject.optString("chequeNo"));

                                settlementReceiptDetailModelList.add(model);
                            }
                        }
                        Log.w("denominatsss",""+resObject.optJSONArray("reportSettlementWithReceiptDenomination"));

                        JSONArray denominationArray = resObject.optJSONArray("reportSettlementWithReceiptDenomination");

                        if (denominationArray.length() > 0) {
                            for (int i = 0; i < denominationArray.length(); i++) {
                                JSONObject denominationObject = denominationArray.optJSONObject(i);
                                SettlementReceiptModel.CurrencyDenomination denomination = new SettlementReceiptModel.CurrencyDenomination();
                                denomination.setDenomination(denominationObject.optString("denomination"));
                                denomination.setCount(denominationObject.optString("count"));
                                denomination.setTotal(denominationObject.optString("total"));

                                denominattotalapi += Double.parseDouble(denominationObject.optString("total"));

                                // Adding the Denomination Details to the Arraylist
                                denominationArrayList.add(denomination);
                            }
                        }
                        Log.w("expensss",""+resObject.optJSONArray("reportSettlementWithReceiptExpenses"));

                        JSONArray expenseArray = resObject.optJSONArray("reportSettlementWithReceiptExpenses");

                        if (expenseArray.length() > 0) {
                            for (int i = 0; i < expenseArray.length(); i++) {
                                JSONObject expenseObject = expenseArray.optJSONObject(i);

                                SettlementReceiptModel.Expense expense = new SettlementReceiptModel.Expense();
                                expense.setExpeneName(expenseObject.optString("expenses"));
                                expense.setExpenseTotal(expenseObject.optString("total"));

                                expensetotalapi += Double.parseDouble(expenseObject.optString("total"));

                                // Adding the expense Details to Arraylist
                                expenseArrayList.add(expense);
                            }
                        }
                        settlementReceiptModelList.add(model1);

               double excessOrShortage = denominattotalapi - (Double.parseDouble(resObject.getString("totalCashAmount"))
                       - expensetotalapi);
                        Log.w("expnsettle",""+denominattotalapi+".."+
                                (Double.parseDouble(resObject.getString("totalCashAmount")))+".."+expensetotalapi);
                        if (excessOrShortage > 0.00){
                            total_excess_txt.setText(twoDecimalPoint(excessOrShortage));
                            excess_total_layl.setVisibility(View.VISIBLE);
                        }
                        else{
                            total_shortage_txt.setText(twoDecimalPoint(excessOrShortage));
                            shortage_total_layl.setVisibility(View.VISIBLE);
                        }
                    }

                }
                else {
                    Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
                }

                if (settlementReceiptModelList.size()>0){
                    setSettlementAdapter(settlementReceiptDetailModelList);
                }
                if (denominationArrayList.size()>0){
                    denominatlay.setVisibility(View.VISIBLE);
                    Log.w("donomsizeee",""+denominationArrayList.size());
                    setDonaminationAdapter(denominationArrayList);
                }
                else {
                    denominatlay.setVisibility(View.GONE);

                }
                if (expenseArrayList.size()>0){
                    expenselay.setVisibility(View.VISIBLE);
                    Log.w("expensizee",""+expenseArrayList.size());
                    setExpenseAdapter(expenseArrayList);
                }
                else {
                    expenselay.setVisibility(View.GONE);
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

        settlementPreviewAdapter= new RoReceiptSettlePreviewAdapter(this, settlementReceiptModelList);
        rv_settle_listl.setHasFixedSize(true);
        rv_settle_listl.setLayoutManager(new LinearLayoutManager(this));
        rv_settle_listl.setAdapter(settlementPreviewAdapter);

//        invoice_collect.setText(twoDecimalPoint(Double.parseDouble(settlementReceiptModelList.get(0).getTotalInvoiceAmount())));
//        discount_amt.setText(twoDecimalPoint(Double.parseDouble(settlementReceiptModelList.get(0).getTotalLessAmount())));
//        net_amt.setText(twoDecimalPoint(Double.parseDouble(settlementReceiptModelList.get(0).getTotalCashAmount())));
    }


    private void setDonaminationAdapter(ArrayList<SettlementReceiptModel.CurrencyDenomination> denominationArrayList) {

        settleDenominatPreviewAdapter = new RoSettleDenominatPreviewAdapter(this, denominationArrayList);
        rv_settle_donaminat_listl.setHasFixedSize(true);
        rv_settle_donaminat_listl.setLayoutManager(new LinearLayoutManager(this));
        rv_settle_donaminat_listl.setAdapter(settleDenominatPreviewAdapter);

        for(int i = 0 ; i < denominationArrayList.size() ; i++) {
            currencytotal += Double.parseDouble(denominationArrayList.get(i).getTotal());
        }
        Log.w("currtotl",""+currencytotal +denominationArrayList.get(0).getTotal());
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
        Log.w("currtotlExp",""+currencytotal +expenseArrayList.get(0).getExpenseTotal());
        if(expensetotal > 0) {
            expenselay.setVisibility(View.VISIBLE);
            expense_total.setText(twoDecimalPoint(expensetotal));
        }
        else{
            expenselay.setVisibility(View.GONE);
        }
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
                        setSettlementReportPrint(expenseArrayList,settlementReceiptModelList,settlementReceiptDetailModelList,denominationArrayList,1);
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

    public void setSettlementReportPrint(ArrayList<SettlementReceiptModel.Expense> expenseList,
                                         ArrayList<SettlementReceiptModel> receiptlist,
                                         ArrayList<SettlementReceiptDetailModel> receiptDetaillist,
                                         ArrayList<SettlementReceiptModel.CurrencyDenomination> denomination,int copy) throws IOException {
        if (validatePrinterConfiguration()){
            if (printerType.equals("TSC Printer")){
                TSCPrinter printer=new TSCPrinter(this,printerMacId,"ReceiptSettlement");
                printer.setSettleReceiptPrint(copy,username,fromdatetxt,receiptlist,receiptDetaillist,denomination,expenseList);
            }
        }else {
            Toast.makeText(getApplicationContext(),"Please configure the Printer",Toast.LENGTH_SHORT).show();
        }
    }
}
