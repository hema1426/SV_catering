package com.winapp.svcatering.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
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
import com.winapp.svcatering.adapter.StockTakePreviewPrintAdapter;
import com.winapp.svcatering.model.StockTakeDetailModel;
import com.winapp.svcatering.model.TransferDetailModel;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StockTakePreviewPrintActivity extends AppCompatActivity {
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
    SharedPreferences sharedPreferences;
    String printerMacId;
    String printerType;
    private ArrayList<StockTakeDetailModel> stockTakeModels;
    private ArrayList<StockTakeDetailModel.StockTakeDetail> stockTakeDetailsList;
    private TextView transfertype, transferno, from_locat,to_locat,transferdate ,toloc_namel,fromloc_namel;
    private RecyclerView takeListView;
    private StockTakePreviewPrintAdapter adapter;
    SessionManager session;
    HashMap<String,String> user;
    String companyId;
    String company_phone;
    String company_gst;
    SweetAlertDialog pDialog;
    AlertDialog alert11;
    String takeNo,takeDate ,takeLoc, stockTakeNo;
    String type;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocktake_preview_print);
        session = new SessionManager(this);
        user = session.getUserDetails();
        companyId = user.get(SessionManager.KEY_COMPANY_CODE);
        company_name = user.get(SessionManager.KEY_COMPANY_NAME);
        company_address1 = user.get(SessionManager.KEY_ADDRESS1);
        company_address2 = user.get(SessionManager.KEY_ADDRESS2);
        company_address3 = user.get(SessionManager.KEY_ADDRESS3);
        company_phone=user.get(SessionManager.KEY_PHONE_NO);
        company_gst=user.get(SessionManager.KEY_COMPANY_REG_NO);
        Log.w("activity_cg",getClass().getSimpleName().toString());

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType = sharedPreferences.getString("printer_type", "");
        printerMacId = sharedPreferences.getString("mac_address", "");

        companyNametext =findViewById (R.id.company_name);
        companyAddress1Text =findViewById (R.id.company_addr1);
        companyAddress2Text =findViewById (R.id.company_addr2);
        companyAddress3Text =findViewById (R.id.company_addr3);
        companyGstText =findViewById (R.id.company_gst);
        companyPhoneText =findViewById (R.id.company_phone);
        transferno =findViewById (R.id.take_no);
        from_locat =findViewById (R.id.from_loc);
        transferdate =findViewById (R.id.take_date);
        mainLayout=findViewById(R.id.main_layout);
        takeListView = findViewById (R.id.rv_takelist);
        setCompanyDetails();

        try {
            if (getIntent()!=null){
                takeNo =getIntent().getStringExtra("takeNumber");
                stockTakeNo =getIntent().getStringExtra("stocktakeNo");
                takeDate =getIntent().getStringExtra("stocktakeDate");
                takeLoc =getIntent().getStringExtra("stocktakeLoc");

                transferno.setText(stockTakeNo);
                String apidate = "" ;
                String apidateFormat = "" ;

                apidate =takeDate ;
                SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                Date datem = null;
                datem = sdf.parse(apidate);

                apidateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(datem);
                transferdate.setText(apidateFormat);

                getStockTakeDetails(takeNo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        setTitle("Stock Take");

    }

    private void setCompanyDetails(){
        companyNametext.setText(company_name);
        if (!company_address1.isEmpty()){
            companyAddress1Text.setVisibility(View.VISIBLE);
            companyAddress1Text.setText(company_address1);
        }

        if (!company_address2.isEmpty()){
            companyAddress2Text.setVisibility(View.VISIBLE);
            companyAddress2Text.setText(company_address2);
        }

        if (!company_address3.isEmpty()){
            companyAddress3Text.setVisibility(View.VISIBLE);
            companyAddress3Text.setText(company_address3);
        }

        if (!company_phone.isEmpty()){
            companyPhoneText.setText("TEL : "+company_phone);
            companyPhoneText.setVisibility(View.VISIBLE);
        }

        if (!company_gst.isEmpty()){
            companyGstText.setText("CO REG NO : "+company_gst);
            companyGstText.setVisibility(View.VISIBLE);
        }
    }

    public void setTitle(String title){
        //Customize the ActionBar
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar_title, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText(title);
        Objects.requireNonNull(abar).setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setHomeButtonEnabled(true);
    }

    private void getStockTakeDetails(String takeNo) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("InventoryNo",takeNo);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url=Utils.getBaseUrl(this) +"StockTakeDetails";

         Log.w("Given_url:",url+jsonBody);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        stockTakeModels =new ArrayList<>();
        stockTakeDetailsList =new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try{
                        Log.w("TakeDetail:",response.toString());

                        pDialog.dismiss();
                        String statusCode=response.optString("statusCode");
                        String statusMessage=response.optString("statusMessage");
                        if (statusCode.equals("1")){
                            JSONArray  transferDetailsArray=response.optJSONArray("responseData");
                            assert transferDetailsArray != null;
                            JSONObject detailObject=transferDetailsArray.optJSONObject(0);
                            StockTakeDetailModel model=new StockTakeDetailModel();
                            model.setNumber(detailObject.optString("inventoryNo"));
                            model.setStatus(detailObject.optString("docStatus"));
                            model.setDate(detailObject.optString("docDate"));

                            JSONArray itemsArray=detailObject.optJSONArray("stockTakeDetails");
                            for (int i = 0; i< Objects.requireNonNull(itemsArray).length(); i++){
                                JSONObject objectItem= itemsArray.optJSONObject(i);
                                StockTakeDetailModel.StockTakeDetail transferModel =
                                        new StockTakeDetailModel.StockTakeDetail();

                                transferModel.setDescription(objectItem.optString("productName"));
                                transferModel.setItemCode(objectItem.optString("productCode"));
                                transferModel.setQty(objectItem.optString("quantity"));
                                transferModel.setLocation(objectItem.optString("warehouseCode"));
                                transferModel.setUomCode(objectItem.optString("uomCode"));
                                stockTakeDetailsList.add(transferModel);
                            }
                            model.setStockTakeDetailsList(stockTakeDetailsList);
                            stockTakeModels.add(model);

                            if (stockTakeDetailsList.size() > 0) {
                                setTakeAdapter();
                             //   transfertype.setText(type);
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


    public void setTakeAdapter() {
        try {
            for (StockTakeDetailModel model : stockTakeModels) {
                //transferno.setText(takeNo);
               // transferdate.setText(model.getDate());

                from_locat.setText(model.getStockTakeDetailsList().get(0).getLocation());
            }
            takeListView.setHasFixedSize(true);
            takeListView.setLayoutManager(new LinearLayoutManager(StockTakePreviewPrintActivity.this, LinearLayoutManager.VERTICAL, false));
            adapter = new StockTakePreviewPrintAdapter(StockTakePreviewPrintActivity.this, stockTakeDetailsList, "Transfer Detail");
            takeListView.setAdapter(adapter);
            mainLayout.setVisibility(View.VISIBLE);
        }catch (Exception exception){}
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_print) {
//            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//            if (mBluetoothAdapter == null) {
//                // Device does not support Bluetooth
//                Toast.makeText(getApplicationContext(), "This device does not support bluetooth", Toast.LENGTH_SHORT).show();
//            } else if (!mBluetoothAdapter.isEnabled()) {
//                // Bluetooth is not enabled :)
//                Toast.makeText(getApplicationContext(), "Enable bluetooth and connect the printer", Toast.LENGTH_SHORT).show();
//            } else {
//                // Bluetooth is enabled
//                if (!printerType.isEmpty()) {
//                    showPrintAlert();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Please configure Printer", Toast.LENGTH_SHORT).show();
//                }
//            }
//            return true;
//        } else if (id == android.R.id.home) {
//            finish();
//        } else if (id == R.id.action_pdf) {
//
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    public void showPrintAlert(){
//        AlertDialog.Builder builder1 = new AlertDialog.Builder(StockTakePreviewPrintActivity.this);
//        builder1.setMessage("Do you want to print this Transfer ?.");
//        builder1.setCancelable(false);
//        builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//               // alertInterface = dialog;
//                if (printerType.equals("TSC Printer")) {
//                    dialog.dismiss();
//                    try {
//                        printTransfer(transferNo, stockTakeModels,type);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        builder1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//            }
//        });
//        alert11 = builder1.create();
//        alert11.show();
//    }

    public void printTransfer(String transferNo,ArrayList<TransferDetailModel> transferDetailModels,String type ){
        if (transferDetailModels.size()>0){
            TSCPrinter printer=new TSCPrinter(this,printerMacId,"Transfer");
            try {
                printer.printTransferDetail(1,transferNo,type,transferDetailModels);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.print_menu, menu);

        MenuItem action_print = menu.findItem(R.id.action_print);
        action_print.setVisible(false);
        return true;
    }

}