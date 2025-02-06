package com.winapp.svcatering.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.TransferPreviewPrintAdapter;
import com.winapp.svcatering.model.TransferDetailModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.winapp.svcatering.zebraprinter.TSCPrinter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TransferPreviewPrintActivity extends AppCompatActivity {
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
    private ArrayList<TransferDetailModel> transferDetailModels;
    private ArrayList<TransferDetailModel.TransferDetails> transferDetailsList;
    private TextView transfertype, transferno, from_locat,to_locat,transferdate ,toloc_namel,fromloc_namel;
    private RecyclerView transferListView;
    private TransferPreviewPrintAdapter adapter;
    SessionManager session;
    HashMap<String,String> user;
    String companyId;
    String company_phone;
    String company_gst;
    SweetAlertDialog pDialog;
    AlertDialog alert11;
    String transferNo;
    String type;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_preview_print);
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
        transfertype =findViewById (R.id.transfer_type);
        transferno =findViewById (R.id.transfer_no);
        from_locat =findViewById (R.id.from_loc);
        to_locat =findViewById (R.id.to_loc);
        toloc_namel =findViewById (R.id.toloc_name);
        fromloc_namel =findViewById (R.id.fromloc_name);
        transferdate =findViewById (R.id.transfer_date);
        mainLayout=findViewById(R.id.main_layout);
        transferListView = findViewById (R.id.rv_transferlist);
        setCompanyDetails();

        try {
            if (getIntent()!=null){
                if (getIntent().getStringExtra("title").equals("Stock Request")){
                    transferNo=getIntent().getStringExtra("transferNumber");
                    type=getIntent().getStringExtra("title");
                    setTitle(getIntent().getStringExtra("title"));
                    getStockRequestDetails(transferNo,"Stock Request");
                }else {
                    transferNo=getIntent().getStringExtra("transferNumber");
                    type=getIntent().getStringExtra("title");
                    setTitle(getIntent().getStringExtra("title"));
                    getTransferDetails(transferNo,"TransferIn");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    private void getStockRequestDetails(String transferNo, String type) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("InvTransReqNo",transferNo);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url=Utils.getBaseUrl(this) +"InventoryTransferRequestDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        transferDetailModels =new ArrayList<>();
        transferDetailsList =new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try{
                        Log.w("TransferDetail:",response.toString());

                        pDialog.dismiss();
                        String statusCode=response.optString("statusCode");
                        String statusMessage=response.optString("statusMessage");
                        if (statusCode.equals("1")){
                            JSONArray  transferDetailsArray=response.optJSONArray("responseData");
                            assert transferDetailsArray != null;
                            JSONObject detailObject=transferDetailsArray.optJSONObject(0);
                            TransferDetailModel model=new TransferDetailModel();
                            model.setNumber(detailObject.optString("invTransReqNo"));
                            model.setStatus(detailObject.optString("invTransReqStatus"));
                            model.setDate(detailObject.optString("docDate"));
                            model.setFromLocation(detailObject.optString("fromWhsCode"));
                            model.setToLocation(detailObject.optString("toWhsCode"));
                            model.setFromLocationName(detailObject.optString("fromWarehouseName"));
                            model.setToLocationName(detailObject.optString("toWarehouseName"));

                            JSONArray itemsArray=detailObject.optJSONArray("itItem");
                            for (int i = 0; i< Objects.requireNonNull(itemsArray).length(); i++){
                                JSONObject objectItem= itemsArray.optJSONObject(i);
                                TransferDetailModel.TransferDetails transferModel =new TransferDetailModel.TransferDetails();
                                transferModel.setDescription(objectItem.optString("itemName"));
                                transferModel.setQty(objectItem.optString("qty"));
                                transferModel.setUomCode(objectItem.optString("uomCode"));
                                transferDetailsList.add(transferModel);
                            }
                            model.setTransferDetailsList(transferDetailsList);
                            transferDetailModels.add(model);

                           // printTransfer(transferNo,transferDetailModels,type);
                            if (transferDetailsList.size() > 0) {
                                setTransferAdapter();
                                transfertype.setText(type);
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


    private void getTransferDetails(String transferNo,String type) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("InvTransNo",transferNo);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) +"InventoryTransferDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_urlTrans:",url+".."+jsonBody);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        transferDetailModels =new ArrayList<>();
        transferDetailsList =new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try{
                        Log.w("TransferDetail:",response.toString());

                        pDialog.dismiss();
                        String statusCode=response.optString("statusCode");
                        String statusMessage=response.optString("statusMessage");
                        if (statusCode.equals("1")){
                            JSONArray transferDetailsArray=response.optJSONArray("responseData");
                            assert transferDetailsArray != null;
                            JSONObject detailObject=transferDetailsArray.optJSONObject(0);
                            TransferDetailModel model=new TransferDetailModel();
                            model.setNumber(detailObject.optString("invTransNo"));
                            model.setStatus(detailObject.optString("invTransStatus"));
                            model.setDate(detailObject.optString("docDate"));
                            model.setFromLocation(detailObject.optString("fromWhsCode"));
                            model.setToLocation(detailObject.optString("toWhsCode"));
                            model.setFromLocationName(detailObject.optString("fromWarehouseName"));
                            model.setToLocationName(detailObject.optString("toWarehouseName"));

                            JSONArray itemsArray=detailObject.optJSONArray("itItem");
                            for (int i = 0; i< Objects.requireNonNull(itemsArray).length(); i++){
                                JSONObject objectItem= itemsArray.optJSONObject(i);
                                TransferDetailModel.TransferDetails transferModel =new TransferDetailModel.TransferDetails();
                                transferModel.setDescription(objectItem.optString("itemName"));
                                transferModel.setQty(objectItem.optString("qty"));
                                transferModel.setUomCode(objectItem.optString("uomCode"));
                                transferDetailsList.add(transferModel);
                            }
                            model.setTransferDetailsList(transferDetailsList);
                            transferDetailModels.add(model);

                            // printTransfer(transferNo,transferDetailModels,type);
                            if (transferDetailsList.size() > 0) {
                                setTransferAdapter();
                                transfertype.setText(type);
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

    public void setTransferAdapter() {
        try {
            for (TransferDetailModel model : transferDetailModels) {
                transferno.setText(transferNo);
                transferdate.setText(model.getDate());
                from_locat.setText(model.getFromLocation());
                to_locat.setText(model.getToLocation());
                Log.w("tran_toLoc",""+model.getToLocationName());
                toloc_namel.setText(model.getToLocationName());
                fromloc_namel.setText(model.getFromLocationName());
            }
            transferListView.setHasFixedSize(true);
            transferListView.setLayoutManager(new LinearLayoutManager(TransferPreviewPrintActivity.this, LinearLayoutManager.VERTICAL, false));
            adapter = new TransferPreviewPrintAdapter(TransferPreviewPrintActivity.this, transferDetailsList, "Transfer Detail");
            transferListView.setAdapter(adapter);
            mainLayout.setVisibility(View.VISIBLE);
        }catch (Exception exception){}
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_print) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
                Toast.makeText(getApplicationContext(), "This device does not support bluetooth", Toast.LENGTH_SHORT).show();
            } else if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enabled :)
                Toast.makeText(getApplicationContext(), "Enable bluetooth and connect the printer", Toast.LENGTH_SHORT).show();
            } else {
                // Bluetooth is enabled
                if (!printerType.isEmpty()) {
                    showPrintAlert();
                } else {
                    Toast.makeText(getApplicationContext(), "Please configure Printer", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        } else if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_pdf) {

        }
        return super.onOptionsItemSelected(item);
    }

    public void showPrintAlert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(TransferPreviewPrintActivity.this);
        builder1.setMessage("Do you want to print this Transfer ?.");
        builder1.setCancelable(false);
        builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               // alertInterface = dialog;
                if (printerType.equals("TSC Printer")) {
                    dialog.dismiss();
                    try {
                        printTransfer(transferNo,transferDetailModels,type);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alert11 = builder1.create();
        alert11.show();
    }

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
        return true;
    }

}