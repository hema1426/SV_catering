package com.winapp.svcatering.salesreturn;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tscdll.TSCActivity;
import com.winapp.svcatering.R;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.ImageUtil;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.winapp.svcatering.zebraprinter.TSCPrinter;
import com.winapp.svcatering.zebraprinter.ZebraPrinterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SalesReturnPrintPreview extends AppCompatActivity {

    private String companyId;
    private SweetAlertDialog pDialog;
    private String salesReturnNumber;
    private SessionManager session;
    private HashMap<String ,String> user;
    private ArrayList<SalesReturnPrintPreviewModel> salesReturnHeader;
    private ArrayList<SalesReturnPrintPreviewModel.SalesReturnDetails> salesReturnList;
    private RecyclerView invoiceListView;
    private SalesReturnPrintPreviewAdapter adapter;
    private TextView srNumberText;
    private TextView srDateText;
    private TextView customerCodetext;
    private TextView customerNameText;
    private TextView addressText;
    private TextView deliveryAddressText;
    private TextView billDiscountText;
    private TextView subtotalText;
    private TextView taxValueText;
    private TextView netTotalText;
    private TextView outstandingText;
    private TextView taxTitle;
    private TextView itemDiscount;
    private TextView companyNametext;
    private TextView companyAddress1Text;
    private TextView companyAddress2Text;
    private String company_name;
    private String company_address1;
    private String company_address2;
    private String company_address3;
    private RelativeLayout rootLayout;
    private LinearLayout addressLayout;
    LinearLayout address1Layout;
    LinearLayout address2Layout;
    LinearLayout address3Layout;
    LinearLayout address4Layout;

    TextView customerAddress1;
    TextView customerAddress2;
    TextView customerAddress3;
    TextView customerAddress4;
    SharedPreferences sharedPreferences;
    String printerMacId;
    String printerType;
    TSCActivity TscDll;
    AlertDialog alert11;
    DialogInterface alertInterface;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return_print_preview);
        setTitle();
        TscDll = new TSCActivity();
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        company_name=user.get(SessionManager.KEY_COMPANY_NAME);
        company_address1=user.get(SessionManager.KEY_ADDRESS1);
        company_address2=user.get(SessionManager.KEY_ADDRESS2);
        company_address3=user.get(SessionManager.KEY_ADDRESS3);
        invoiceListView=findViewById(R.id.invoiceList);
        srNumberText =findViewById(R.id.sr_no);
        srDateText =findViewById(R.id.sr_date);
        customerCodetext=findViewById(R.id.customer_code);
        customerNameText=findViewById(R.id.customer_name_value);
        deliveryAddressText=findViewById(R.id.delivery_address);
        billDiscountText=findViewById(R.id.bill_disc);
        subtotalText=findViewById(R.id.balance_value);
        taxValueText=findViewById(R.id.tax);
        netTotalText=findViewById(R.id.net_total);
        outstandingText=findViewById(R.id.outstanding_amount);
        taxTitle=findViewById(R.id.tax_title);
        itemDiscount=findViewById(R.id.item_disc);
        companyNametext=findViewById(R.id.company_name);
        companyAddress1Text=findViewById(R.id.address1);
        companyAddress2Text=findViewById(R.id.address2);
        addressLayout=findViewById(R.id.adressLayout);
        address1Layout=findViewById(R.id.address1Layout);
        address2Layout=findViewById(R.id.address2Layout);
        address3Layout=findViewById(R.id.address3Layout);
        address4Layout=findViewById(R.id.address4Layout);

        customerAddress1=findViewById(R.id.cus_address1);
        customerAddress2=findViewById(R.id.cus_address2);
        customerAddress3=findViewById(R.id.cus_address3);
        customerAddress4=findViewById(R.id.cus_address4);

        rootLayout=findViewById(R.id.rootLayout);
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        Log.w("Printer_Mac_Id:",printerMacId);
        Log.w("Printer_Type:",printerType);

        if (getIntent() != null){
            salesReturnNumber =getIntent().getStringExtra("srNumber");
            if (salesReturnNumber !=null){
                try {
                    getSalesReturnDetails(salesReturnNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void getSalesReturnDetails(String srNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("SalesReturnNo", srNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(SalesReturnPrintPreview.this);
        String url= Utils.getBaseUrl(SalesReturnPrintPreview.this) +"SalesReturnDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url_SalesReturn:",url+"/"+jsonObject.toString());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        salesReturnHeader =new ArrayList<>();
        salesReturnList =new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        Log.w("SalesReturn_Details:",response.toString());
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray responseData=response.optJSONArray("responseData");
                            assert responseData != null;
                            if (responseData.length()>0){
                                SalesReturnPrintPreviewModel model=new SalesReturnPrintPreviewModel();
                                JSONObject object =responseData.optJSONObject(0);
                                model.setSrNo(object.optString("srNumber"));
                                model.setSrDate(object.optString("srDate"));
                                model.setCustomerCode(object.optString("customerCode"));
                                model.setCustomerName(object.optString("customerName"));
                                model.setSubTotal(object.optString("subTotal"));
                                model.setTax(object.optString("taxTotal"));
                                model.setNetTotal(object.optString("netTotal"));
                                model.setBillDisc(object.optString("billDiscount"));
                                model.setItemDisc(object.optString("iTotalDiscount"));
                                model.setTaxType(object.optString("taxType"));
                                model.setTaxValue(object.optString("taxPerc"));
                                model.setAddress(object.optString("address1") + object.optString("address2") + object.optString("address3"));
                                model.setAddress1(object.optString("address1"));
                                model.setAddress2(object.optString("address2"));
                                model.setAddress3(object.optString("address3"));
                                model.setAddressstate(object.optString("block")+" "+object.optString("street")+" "
                                        +object.optString("city"));
                                model.setAddresssZipcode(object.optString("countryName")+" "+object.optString("state")+" "
                                        +object.optString("zipcode"));
                                model.setSignFlag(object.optString("signFlag"));
                             //   String signFlag=object.optString("signFlag");
                                if (object.optString("signature")!=null &&
                                        !object.optString("signature").equals("null") && !object.optString("signature").isEmpty()){
                                    String signature = object.optString("signature");
                                    Utils.setSignature(signature);
                                    createSignature();
                                } else {
                                    Utils.setSignature("");
                                }

                                JSONArray salesReturnDetails=object.optJSONArray("salesReturnDetails");
                                for (int i = 0; i< Objects.requireNonNull(salesReturnDetails).length(); i++){
                                    JSONObject detailsObject =salesReturnDetails.getJSONObject(i);
                                    SalesReturnPrintPreviewModel.SalesReturnDetails salesReturnModel =new SalesReturnPrintPreviewModel.SalesReturnDetails();
                                    salesReturnModel.setProductCode(detailsObject.optString("productCode"));
                                    salesReturnModel.setDescription( detailsObject.optString("productName"));
                                    salesReturnModel.setLqty(detailsObject.optString("quantity"));
                                    salesReturnModel.setCqty(detailsObject.optString("cartonQty"));
                                    salesReturnModel.setNetqty(detailsObject.optString("quantity"));
                                    salesReturnModel.setCartonPrice(detailsObject.optString("price"));
                                    salesReturnModel.setPrice(detailsObject.optString("price"));
                                    salesReturnModel.setPcspercarton(detailsObject.optString("pcsPerCarton"));
                                    salesReturnModel.setTax(detailsObject.optString("totalTax"));
//                                    salesReturnModel.setTotal(detailsObject.optString("netTotal"));
                                     salesReturnModel.setTotal(detailsObject.optString("total"));
                                    salesReturnModel.setSubTotal(detailsObject.optString("subTotal"));
                                    salesReturnModel.setUomCode(object.optString("UOMCode"));
                                    salesReturnList.add(salesReturnModel);
                                }
                                model.setSalesReturnList(salesReturnList);
                                salesReturnHeader.add(model);
                            }
                            if (salesReturnList.size()>0){
                                setInvoiceAdapter();
                            }
                        }else {

                        }
                      /*  if (response.length()>0){
                            SalesReturnPrintPreviewModel model=new SalesReturnPrintPreviewModel();
                            model.setSrNo(response.optString("SalesReturnNo"));
                            model.setSrDate(response.optString("SalesReturnDateString"));
                            model.setCustomerCode(response.optString("CustomerCode"));
                            model.setCustomerName(response.optString("CustomerName"));
                            model.setSubTotal(response.optString("SubTotal"));
                            model.setTax(response.optString("Tax"));
                            model.setNetTotal(response.optString("NetTotal"));
                            model.setBillDisc(response.optString("BillDIscount"));
                            model.setItemDisc(response.optString("ItemDiscount"));
                            model.setTaxType(response.optString("TaxType"));
                            model.setTaxValue(response.optString("TaxPerc"));

                            JSONArray products=response.getJSONArray("SalesReturnDetails");
                            for (int i=0;i<products.length();i++) {
                                JSONObject object = products.getJSONObject(i);
                                SalesReturnPrintPreviewModel.SalesReturnDetails salesReturnModel =new SalesReturnPrintPreviewModel.SalesReturnDetails();
                                salesReturnModel.setProductCode(object.optString("ProductCode"));
                                salesReturnModel.setDescription( object.optString("ProductName"));
                                salesReturnModel.setLqty(object.optString("LQty"));
                                salesReturnModel.setCqty(object.optString("CQty"));
                                salesReturnModel.setNetqty(object.optString("Qty"));
                                salesReturnModel.setCartonPrice(object.optString("CartonPrice"));
                                salesReturnModel.setPrice(object.optString("Price"));
                                salesReturnModel.setPcspercarton(object.optString("PcsPerCarton"));
                                salesReturnModel.setTax(object.optString("Tax"));
                                salesReturnModel.setTotal(object.optString("NetTotal"));
                                salesReturnModel.setSubTotal(object.optString("SubTotal"));
                                salesReturnList.add(salesReturnModel);
                            }
                            model.setSalesReturnList(salesReturnList);
                            salesReturnHeader.add(model);
                        }
                        if (salesReturnList.size()>0){
                            setInvoiceAdapter();
                        }*/
                        pDialog.dismiss();
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
    private void createSignature() {
        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
            try {
                ImageUtil.saveStamp(this, Utils.getSignature(), "Signature");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void setInvoiceAdapter(){
        for (SalesReturnPrintPreviewModel model: salesReturnHeader){
            srNumberText.setText(model.getSrNo());
            srDateText.setText(model.getSrDate());
            customerCodetext.setText(model.getCustomerCode());
            customerNameText.setText(model.getCustomerName());

            if (!model.getAddress1().isEmpty()){
                address1Layout.setVisibility(View.VISIBLE);
                customerAddress1.setText(model.getAddress1());
            }
            if (!model.getAddress2().isEmpty()){
                address2Layout.setVisibility(View.VISIBLE);
                customerAddress2.setText(model.getAddress2());
            }
            if (!model.getAddress3().isEmpty()){
                address3Layout.setVisibility(View.VISIBLE);
                customerAddress3.setText(model.getAddress3());
            }
            if (!model.getAddressstate().isEmpty() ) {
                if (!model.getAddresssZipcode().isEmpty()) {
                    address4Layout.setVisibility(View.VISIBLE);
                    customerAddress4.setText(model.getAddressstate() + " " + model.getAddresssZipcode());
                } else {
                    address4Layout.setVisibility(View.VISIBLE);
                    customerAddress4.setText(model.getAddressstate());
                }
            }
            else{
                if (!model.getAddresssZipcode().isEmpty() ) {
                    address4Layout.setVisibility(View.VISIBLE);
                    customerAddress4.setText(model.getAddresssZipcode());
                }
            }

            billDiscountText.setText(model.getBillDisc());
            subtotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getSubTotal())));
            taxValueText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getTax())));
            netTotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));
            taxTitle.setText("Tax ( "+model.getTaxType()+" : "+model.getTaxValue()+" % ) ");
            itemDiscount.setText(model.getItemDisc());
        }

        companyNametext.setText(company_name);
        companyAddress1Text.setText(company_address1);
        companyAddress2Text.setText(company_address2 +","+company_address3);
        invoiceListView.setHasFixedSize(true);
        // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        invoiceListView.setLayoutManager(new LinearLayoutManager(SalesReturnPrintPreview.this, LinearLayoutManager.VERTICAL, false));
        adapter = new SalesReturnPrintPreviewAdapter(SalesReturnPrintPreview.this, salesReturnList);
        invoiceListView.setAdapter(adapter);
        rootLayout.setVisibility(View.VISIBLE);
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
                    showPrintAlert();
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

    public void showPrintAlert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SalesReturnPrintPreview.this);
        builder1.setMessage("Do you want to print this SalesReturn ?.");
        builder1.setCancelable(false);
        builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertInterface=dialog;
                if (printerType.equals("TSC Printer")){
                    dialog.dismiss();
                   // TSCPrinter tscPrinter=new TSCPrinter(SalesReturnPrintPreview.this,printerMacId);
                //    tscPrinter.printInvoice(salesReturnHeader, salesReturnList);
                    TSCPrinter printer=new TSCPrinter(SalesReturnPrintPreview.this,printerMacId,"SalesReturn");
                    try {
                        printer.printSalesReturn(1,salesReturnHeader,salesReturnList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if (printerType.equals("Zebra Printer")){
                    ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(SalesReturnPrintPreview.this,printerMacId);
                    try {
                        zebraPrinterActivity.printSalesReturn(1, salesReturnHeader, salesReturnList);
                    } catch (IOException e) {
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

    public void closeAlert(){
        if (alertInterface!=null){
            alertInterface.dismiss();
        }
    }

    public void setTitle(){
        //Customize the ActionBar
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar_title, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("SALES RETURN");
        Objects.requireNonNull(abar).setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setHomeButtonEnabled(true);
    }

}