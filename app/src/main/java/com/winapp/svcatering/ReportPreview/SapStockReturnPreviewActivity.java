package com.winapp.svcatering.ReportPreview;

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
import com.winapp.svcatering.ReportPreview.adapter.SapStockReturnPreviewPrintAdapter;
import com.winapp.svcatering.model.StockBadRequestReturnModel;
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

public class SapStockReturnPreviewActivity extends AppCompatActivity {
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
    private TextView customerCode;
    private TextView customerName;
    private RecyclerView rv_stockreturn_Previewlistl;
    private ArrayList<StockBadRequestReturnModel> stockBadRequestReturnList;
    private ArrayList<StockBadRequestReturnModel.StockBadRequestReturnDetails> stockBadRequestReturnDetailsList;
    public SapStockReturnPreviewPrintAdapter sapStockSumPreviewPrintAdapter;
    private TextView transfertol;
    private TextView reasonl;
    private TextView returndate;
    private SweetAlertDialog pDialog;
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
    public String fromdatetxt = "";
    public String todatetxt = "";
    public String loccodetxt = "";
    public String username;
    public String currentDate;
    public String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sap_ro_stockreturn_preview_print);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Bad Stock Return");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.w("activity_cg",getClass().getSimpleName().toString());

        companyNametext =findViewById (R.id.company_name);
        companyAddress1Text =findViewById (R.id.company_addr1);
        companyAddress2Text =findViewById (R.id.company_addr2);
        companyAddress3Text =findViewById (R.id.company_addr3);
        companyGstText =findViewById (R.id.company_gst);
        companyPhoneText =findViewById (R.id.company_phone);

        transfertol =findViewById (R.id.transferto);
        reasonl =findViewById (R.id.reason);
        returndate =findViewById (R.id.return_date);
        rv_stockreturn_Previewlistl =findViewById (R.id.rv_stockreturn_Previewlist);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = df1.format(c);

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
            StockBadRequestReturn(1,"DM","Damaged/Expired",fromDateString,toDateString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void StockBadRequestReturn(int copy,String warehouse,String reason,String from_date,String to_date) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("Warehouse",warehouse);
        jsonBody.put("Reason",reason);
        jsonBody.put("FromDate",from_date);
        jsonBody.put("ToDate",to_date);
        jsonBody.put("User",username);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"ReportStockReturn";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_stockRetururl:",url+jsonBody);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        stockBadRequestReturnList =new ArrayList<>();
        stockBadRequestReturnDetailsList =new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try{
                        Log.w("StockBadRequestReturn:",response.toString());

                        String statusCode=response.optString("statusCode");
                        String statusMessage=response.optString("statusMessage");

                        if (statusCode.equals("1")){
                            JSONArray  stockReturnArray = response.optJSONArray("responseData");
                            assert stockReturnArray != null;
                            if (stockReturnArray.length() > 0){
                                JSONObject detailObject=stockReturnArray.optJSONObject(0);
                                StockBadRequestReturnModel model=new StockBadRequestReturnModel();
//                            model.setWarehouseCode(detailObject.optString("warehouseCode"));
//                            model.setReason(detailObject.optString("reason"));
                                reasonl.setText(detailObject.optString("reason"));
                                transfertol.setText(detailObject.optString("transferTo"));
                                returndate.setText(detailObject.optString("date"));
                                JSONArray itemsArray=detailObject.optJSONArray("reportSalesReturnDetails");
                                for (int i = 0; i< Objects.requireNonNull(itemsArray).length(); i++){
                                    JSONObject objectItem= itemsArray.optJSONObject(i);
                                    StockBadRequestReturnModel.StockBadRequestReturnDetails stockBadRequestReturnModel =
                                            new StockBadRequestReturnModel.StockBadRequestReturnDetails();
                                    stockBadRequestReturnModel.setDescription(objectItem.optString("description"));
                                    stockBadRequestReturnModel.setQty(objectItem.optString("quantity"));
                                    stockBadRequestReturnModel.setUomCode(objectItem.optString("uomCode"));

                                    Log.e("uomdd", "" + (int)Double.parseDouble("-"+(objectItem.optString("quantity"))));

                                    stockBadRequestReturnDetailsList.add(stockBadRequestReturnModel);
                                }
                                model.setStockBadRequestReturnDetailsList(stockBadRequestReturnDetailsList);
                                stockBadRequestReturnList.add(model);
                                if(stockBadRequestReturnDetailsList.size()>0){
                                    setAdapter(stockBadRequestReturnDetailsList);
                                }else {
                                    Toast.makeText(getApplicationContext(),"No Record Found...!",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }else {
                                Toast.makeText(getApplicationContext(),"No Record Found...!",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),statusMessage,Toast.LENGTH_SHORT).show();
                            finish();
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

    public void setAdapter(ArrayList<StockBadRequestReturnModel.StockBadRequestReturnDetails> stockBadRequestReturnDetailsList) {
        rv_stockreturn_Previewlistl.setHasFixedSize(true);
        rv_stockreturn_Previewlistl.setLayoutManager(new LinearLayoutManager(SapStockReturnPreviewActivity.this, LinearLayoutManager.VERTICAL, false));
        sapStockSumPreviewPrintAdapter = new SapStockReturnPreviewPrintAdapter(SapStockReturnPreviewActivity.this, stockBadRequestReturnDetailsList, "stock summary");
        rv_stockreturn_Previewlistl.setAdapter(sapStockSumPreviewPrintAdapter);
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
                        setBadStockReturnPrint(stockBadRequestReturnList,1);
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

    public void setBadStockReturnPrint(ArrayList<StockBadRequestReturnModel> stockBadRequestReturnList, int copy) throws IOException {
        if (validatePrinterConfiguration()){
            if (printerType.equals("TSC Printer")){
                TSCPrinter printer = new TSCPrinter(this, printerMacId,"BadStock");
                printer.printStockBadRequestReturn(copy, from_date,to_date,stockBadRequestReturnList);
            } else {
                Toast.makeText(getApplicationContext(),"This Printer not Support...!",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Please configure the Printer",Toast.LENGTH_SHORT).show();
        }
    }
}
