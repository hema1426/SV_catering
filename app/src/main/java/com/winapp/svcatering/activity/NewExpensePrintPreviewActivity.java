package com.winapp.svcatering.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tscdll.TSCActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.ExpensePrintPreviewAdapter;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;
import com.winapp.svcatering.printpreview.InvoicePrintPreviewActivity;
import com.winapp.svcatering.tscprinter.TSCPrinterActivity;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.ImageUtil;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.winapp.svcatering.zebraprinter.TSCPrinter;
import com.winapp.svcatering.zebraprinter.ZebraPrinterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NewExpensePrintPreviewActivity extends AppCompatActivity {

    private String companyId;
    private SweetAlertDialog pDialog;
    private String invoiceNumber;
    private SessionManager session;
    private HashMap<String, String> user;
    private ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails;
    private ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList;
    private RecyclerView invoiceListView;

    double subTotall = 0.0 ;
    private ExpensePrintPreviewAdapter adapter;
    private TextView invoiceNumberText;
    private TextView invoiceDateText;
//    private TextView customerCodetext;
    private TextView customerNameText;
    private TextView addressText;
    private TextView deliveryAddressText;
    private TextView billDiscountText;
    private TextView subtotalText;
    private TextView taxValueText;
//    private TextView netTotalText;
    private TextView outstandingText;
    private TextView taxTitle;
    private TextView itemDiscount;
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
    private RelativeLayout rootLayout;
    private LinearLayout addressLayout;
    SharedPreferences sharedPreferences;
    String printerMacId;
    String printerType;
    TSCActivity TscDll;
    AlertDialog alert11;
    DialogInterface alertInterface;
    public static int REQUEST_PERMISSIONS = 154;
    boolean boolean_permission;
    boolean boolean_save;
    Bitmap bitmap;
    ProgressDialog progressDialog;
    private static final String TAG = InvoicePrintPreviewActivity.class.getSimpleName();
    public static final String SAMPLE_FILE = "android_tutorial.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    static BottomSheetBehavior behavior;
    LinearLayout shareLayout;
    File pdfFile;
    LinearLayout printLayout;
    Button cancelButton;
    private static final int PERMISSION_REQUEST_CODE = 100;
    public String outstanding_amount="0.0";

    LinearLayout address1Layout;
    LinearLayout address2Layout;
    LinearLayout address3Layout;

    TextView customerAddress1;
    TextView customerAddress2;
    TextView customerAddress3;
    String company_phone;
    String company_gst;
    private ArrayList<InvoicePrintPreviewModel.SalesReturnList> salesReturnList ;

//    private TextView gstText;
//    private TextView gstTitle;
    private TextView cnsubtotalText;
    private TextView cnnetTotalText;
    private TextView cngstText,cnno;
    private TextView cngstTitle;
    private LinearLayout cn_lay;
    private TextView paymentText;
    private String username;

    private TextView codeText;
    private TextView userText;
    private LinearLayout cn_layl;
    private TextView balanceText;
    private LinearLayout mainLayout;
    private String locationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense_module_preview);
        setTitle();
        session = new SessionManager(this);
        user = session.getUserDetails();
        companyId = user.get(SessionManager.KEY_COMPANY_CODE);
        username=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        company_name = user.get(SessionManager.KEY_COMPANY_NAME);
        company_address1 = user.get(SessionManager.KEY_ADDRESS1);
        company_address2 = user.get(SessionManager.KEY_ADDRESS2);
        company_address3 = user.get(SessionManager.KEY_ADDRESS3);
        company_phone=user.get(SessionManager.KEY_PHONE_NO);
        company_gst=user.get(SessionManager.KEY_COMPANY_REG_NO);
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType = sharedPreferences.getString("printer_type", "");
        printerMacId = sharedPreferences.getString("mac_address", "");
        Log.w("activity_cg",getClass().getSimpleName().toString());

        companyNametext =findViewById (R.id.company_name);
        companyAddress1Text =findViewById (R.id.company_addr1);
        companyAddress2Text =findViewById (R.id.company_addr2);
        companyAddress3Text =findViewById (R.id.company_addr3);
        companyGstText =findViewById (R.id.company_gst);
        companyPhoneText =findViewById (R.id.company_phone);
        invoiceNumberText =findViewById (R.id.invoice_no);
        invoiceDateText =findViewById (R.id.invoice_date);
        customerNameText =findViewById (R.id.customer_name);
//        customerCodetext =findViewById (R.id.customer_code);
        addressText =findViewById (R.id.customer_address);
        codeText=findViewById (R.id.code_txt);
        userText =findViewById (R.id.invoice_user);
        subtotalText =findViewById (R.id.expen_subtotal);
//        netTotalText =findViewById (R.id.innettotal);
//        gstText =findViewById (R.id.ingst_text);
        cnsubtotalText =findViewById (R.id.cnrsubtotal);
        cnnetTotalText =findViewById (R.id.cnrnet_total);
        cngstText =findViewById (R.id.cnrgst_text);
//        gstTitle = findViewById (R.id.ingst_title);
        cngstTitle = findViewById (R.id.cngst_title);
        balanceText = findViewById (R.id.inbalance);
        cnno = findViewById (R.id.cn_no);
        cn_layl = findViewById (R.id.cn_lay);
        mainLayout=findViewById(R.id.main_layout);
        invoiceListView = findViewById (R.id.rv_expensePreview_list);
        try {
            if (getIntent()!=null){
                String invoiceNumber=getIntent().getStringExtra("invoiceNumber");
                getInvoiceDetails(invoiceNumber);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        textviewTitle.setText("Expense");
        Objects.requireNonNull(abar).setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setHomeButtonEnabled(true);
    }


    private void getInvoiceDetails(String invoiceNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject = new JSONObject();
        // jsonObject.put("CompanyCode", companyId);
        jsonObject.put("InvoiceNo", invoiceNumber);
        jsonObject.put("LocationCode",locationCode);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "APInvoiceDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_ExpensPreview:", url+"--"+jsonObject.toString());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        invoiceHeaderDetails = new ArrayList<>();
        invoiceList = new ArrayList<>();
        salesReturnList=new ArrayList<>();
              JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try {
                Log.w("ResExpensPrint::", response.toString());
                String statusCode=response.optString("statusCode");
                if (statusCode.equals("1")){
                    JSONArray responseData=response.getJSONArray("responseData");
                    JSONObject object=responseData.optJSONObject(0);

                    InvoicePrintPreviewModel model = new InvoicePrintPreviewModel();
                    model.setInvoiceNumber(object.optString("poNo"));
                    model.setInvoiceDate(object.optString("poDate"));
                    model.setCustomerCode(object.optString("vendorCode"));
                    model.setCustomerName(object.optString("vendorName"));
                   // model.setAddress(object.optString("address1"));
                    model.setAddress(object.optString("address1") + object.optString("address2") + object.optString("address3"));
                    model.setDeliveryAddress("");
                    model.setSubTotal(object.optString("subTotal"));
                    model.setNetTax(object.optString("taxTotal"));
                    model.setNetTotal(object.optString("netTotal"));
                    model.setTaxType(object.optString("taxType"));
                    model.setTaxValue(object.optString("taxPerc"));
                    model.setOutStandingAmount(object.optString("totalOutstandingAmount"));
                    model.setPaymentTerm(object.optString("paymentTerm"));
                    model.setBalanceAmount(object.optString("balanceAmount"));
                    model.setOverAllTotal(object.optString("overAllTotal"));
                    //   Utils.setInvoiceOutstandingAmount(object.optString("balanceAmount"));
                    //  Utils.setInvoiceMode("Invoice");
                    model.setBillDiscount(object.optString("billDiscount"));
                    model.setItemDiscount(object.optString("totalDiscount"));
                    model.setAddress1(object.optString("address1"));
                    model.setAddress2(object.optString("address2"));
                    model.setAddress3(object.optString("address3"));
                    model.setSoNumber(object.optString("soNumber"));
                    model.setSoDate(object.optString("soDate"));
                    model.setDoDate(object.optString("doDate"));
                    model.setDoNumber(object.optString("doNumber"));
                    String signFlag=object.optString("signFlag");
                    if (signFlag.equals("Y")){
                        String signature=object.optString("signature");
                        Utils.setSignature(signature);
                        createSignature();
                    }else {
                        Utils.setSignature("");
                    }
                    JSONArray detailsArray=object.optJSONArray("purchaseInvoiceDetails");
                    for (int i=0;i<detailsArray.length();i++){
                        JSONObject detailObject=detailsArray.optJSONObject(i);
                 //      if (Double.parseDouble(detailObject.optString("quantity"))>0) {
                            InvoicePrintPreviewModel.InvoiceList invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                            invoiceListModel.setProductCode(detailObject.optString("productCode"));
                            invoiceListModel.setDescription(detailObject.optString("productName"));
                            invoiceListModel.setLqty(detailObject.optString("unitQty"));
                            invoiceListModel.setCqty(detailObject.optString("cartonQty"));
                            invoiceListModel.setNetQty(detailObject.optString("quantity"));
                            invoiceListModel.setNetQuantity(detailObject.optString("netQuantity"));
                            invoiceListModel.setFocQty(detailObject.optString("foc_Qty"));
                            invoiceListModel.setReturnQty(detailObject.optString("returnQty"));
                            invoiceListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                            invoiceListModel.setUnitPrice(detailObject.optString("price"));
                            double qty = Double.parseDouble(detailObject.optString("quantity"));
                            double price = Double.parseDouble(detailObject.optString("price"));
                        //setAccountName
                        invoiceListModel.setUnitPrice(detailObject.optString("price"));

                        double nettotal = qty * price;
                           // invoiceListModel.setTotal(String.valueOf(nettotal));
                            invoiceListModel.setTotal(detailObject.optString("lineTotal"));
                            invoiceListModel.setPricevalue(detailObject.optString("price"));

                            invoiceListModel.setUomCode(detailObject.optString("uomCode"));
                            invoiceListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                            invoiceListModel.setItemtax(detailObject.optString("totalTax"));
                            invoiceListModel.setSubTotal(detailObject.optString("subTotal"));
                            invoiceListModel.setAccountName(detailObject.optString("accountName"));

                        double subTotal = Double.parseDouble(detailObject.optString("price"));
                        subTotall += subTotal ;

                        Log.w("subbgg",""+detailObject.optString("price"));
                        invoiceList.add(invoiceListModel);


                        } /*else  if (Double.parseDouble(detailObject.optString("returnQty"))>0) {
                           invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                           invoiceListModel.setProductCode(detailObject.optString("productCode"));
                           invoiceListModel.setDescription(detailObject.optString("productName"));
                           invoiceListModel.setLqty(detailObject.optString("unitQty"));
                           invoiceListModel.setCqty(detailObject.optString("cartonQty"));
                           invoiceListModel.setNetQty(detailObject.optString("quantity"));
                           invoiceListModel.setNetQuantity(detailObject.optString("netQuantity"));
                           invoiceListModel.setFocQty(detailObject.optString("foc_Qty"));
                           invoiceListModel.setReturnQty(detailObject.optString("returnQty"));
                           invoiceListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                           invoiceListModel.setUnitPrice(detailObject.optString("price"));
                           double qty = Double.parseDouble(detailObject.optString("netQuantity"));
                           double price = Double.parseDouble(detailObject.optString("price"));

                           double nettotal = qty * price;
                           invoiceListModel.setTotal(String.valueOf(nettotal));
                           invoiceListModel.setPricevalue(String.valueOf(price));

                           invoiceListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                           invoiceListModel.setItemtax(detailObject.optString("totalTax"));
                           invoiceListModel.setSubTotal(detailObject.optString("subTotal"));
                           invoiceList.add(invoiceListModel);
                       }*/
                  //  }
                    model.setInvoiceList(invoiceList);
//                    JSONArray SRArray=object.optJSONArray("sR_Details");
//                    assert SRArray != null;
//                    if (SRArray.length() > 0){
//                        JSONObject SRoblect = SRArray.optJSONObject(0);
//                        InvoicePrintPreviewModel.SalesReturnList salesReturnModel = new InvoicePrintPreviewModel.SalesReturnList();
//                        salesReturnModel.setSalesReturnNumber(SRoblect.optString("salesReturnNumber"));
//                        salesReturnModel.setsRSubTotal(SRoblect.optString("sR_SubTotal"));
//                        salesReturnModel.setsRTaxTotal(SRoblect.optString("sR_TaxTotal"));
//                        salesReturnModel.setsRNetTotal(SRoblect.optString("sR_NetTotal"));
//                        salesReturnList.add(salesReturnModel);
//                    }
               //     model.setSalesReturnList(salesReturnList);
                    invoiceHeaderDetails.add(model);
                    if (invoiceList.size() > 0) {
                        subtotalText.setText(Utils.twoDecimalPoint(subTotall));
                        setInvoiceAdapter();
                    }
                    pDialog.dismiss();
                }else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            // Do something when error occurred
            pDialog.dismiss();
            Log.w("Error_throwing:", error.toString());
            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
        }) {
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

    private void createSignature(){
        if (Utils.getSignature()!=null && !Utils.getSignature().isEmpty()){
            try {
                ImageUtil.saveStamp(this,Utils.getSignature(),"Signature");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setInvoiceAdapter() {
        for (InvoicePrintPreviewModel model : invoiceHeaderDetails) {
            Log.w("modelExpen",""+model);
            invoiceNumberText.setText(model.getInvoiceNumber());
            invoiceDateText.setText(model.getInvoiceDate());
            codeText.setText(model.getCustomerCode());
            customerNameText.setText(model.getCustomerName());
        //    paymentText.setText(model.getPaymentTerm());

            userText.setText(username);

            if (model.getAddress().isEmpty()){
                addressText.setVisibility(View.GONE);
            } else {
                addressText.setText(model.getAddress());
            }
//            if (!model.getAddress2().isEmpty()){
//                address2Layout.setVisibility(View.VISIBLE);
//                customerAddress2.setText(model.getAddress2());
//            }
//            if (!model.getAddress3().isEmpty()){
//                address3Layout.setVisibility(View.VISIBLE);
//                customerAddress3.setText(model.getAddress3());
//            }
          /*  if (!model.getDeliveryAddress().isEmpty()) {
                addressLayout.setVisibility(View.VISIBLE);
                deliveryAddressText.setText(model.getDeliveryAddress());
            }*/





          //  gstTitle.setText("GST ( " + model.getTaxType() + " : " + Utils.twoDecimalPoint(Double.parseDouble(model.getTaxValue())) + " % ) ");
            if (model.getTaxType().equals("I")) {
                double sub_total = Double.parseDouble(model.getNetTotal()) - Double.parseDouble(model.getNetTax());
                //billDiscountText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBillDiscount())));

                // subtotalText.setText(Utils.twoDecimalPoint(sub_total));

//                gstText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTax())));
//                netTotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));
//
                //  outstandingText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getOutStandingAmount())));
                // itemDiscount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getItemDiscount())));
            } else {
                // billDiscountText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBillDiscount())));

                //subtotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getSubTotal())));

//                gstText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTax())));
//                netTotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));
//
                //  outstandingText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getOutStandingAmount())));
                //  itemDiscount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getItemDiscount())));
            }

//            if (model.getSalesReturnList().size() > 0) {
//                cn_layl.setVisibility(View.VISIBLE);
//                cnno.setText(model.getSalesReturnList().get(0).getSalesReturnNumber());
//             //   Log.w("SubTotal:",model.getSalesReturnList().get(0).getsRSubTotal());
//             //   Log.w("TaxValue:",model.getSalesReturnList().get(0).getsRTaxTotal());
//              //  Log.w("NetTotalValue:",model.getSalesReturnList().get(0).getsRNetTotal());
//                cnsubtotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getSalesReturnList().get(0).getsRSubTotal())));
//                cngstTitle.setText("GST ( " + model.getTaxType() + " : " + Utils.twoDecimalPoint(Double.parseDouble(model.getTaxValue())) + " % ) ");
//                cngstText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getSalesReturnList().get(0).getsRTaxTotal())));
//                cnnetTotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getSalesReturnList().get(0).getsRNetTotal())));
//                balanceText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getOverAllTotal())));
//            }

        }
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
        invoiceListView.setHasFixedSize(true);
        // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        invoiceListView.setLayoutManager(new LinearLayoutManager(NewExpensePrintPreviewActivity.this, LinearLayoutManager.VERTICAL, false));
        adapter = new ExpensePrintPreviewAdapter(NewExpensePrintPreviewActivity.this, invoiceList,"Expense");
        invoiceListView.setAdapter(adapter);
        mainLayout.setVisibility(View.VISIBLE);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            boolean_permission=true;
            return true;
        } else {
            boolean_permission=false;
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            boolean_permission=true;
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
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
            Intent intent = new Intent(this, NewExpenseModuleListActivity.class);
            startActivity(intent);
          //  finish();
        } else if (id == R.id.action_pdf) {
            if (boolean_permission) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please wait");
                bitmap = Utils.loadBitmapFromView(rootLayout, rootLayout.getWidth(), rootLayout.getHeight());
              //  createPdf();
            } else {
                Toast.makeText(getApplicationContext(),"Permission required to share content",Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void showPrintAlert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(NewExpensePrintPreviewActivity.this);
        builder1.setMessage("Do you want to print this Expense ?.");
        builder1.setCancelable(false);
        builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertInterface = dialog;
                if (printerType.equals("TSC Printer")) {
                   dialog.dismiss();
                    //TSCPrinter tscPrinter=new TSCPrinter(InvoicePrintPreviewActivity.this,printerMacId);
                    // tscPrinter.printInvoice(invoiceHeaderDetails,invoiceList);
                    try {

                        TSCPrinter printer=new TSCPrinter(NewExpensePrintPreviewActivity.this,printerMacId,"Expense");
                        // try {
                        printer.printExpense(1,invoiceHeaderDetails, invoiceList);
                        printer.setOnCompletionListener(new TSCPrinter.OnCompletionListener() {
                            @Override
                            public void onCompleted() {
                                Utils.setSignature("");
                                Toast.makeText(getApplicationContext(),"Expense printed successfully!",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
   //                     printer.printInvoice(1,invoiceHeaderDetails,invoiceList,"false");

                                /*final TSCPrinterActivity print = new TSCPrinterActivity(NewInvoicePrintPreviewActivity.this, printerMacId, printerType);
                                print.initGenericPrinter();
                                print.setInitCompletionListener(new TSCPrinterActivity.InitCompletionListener() {
                                    @Override
                                    public void initCompleted() {
                                        print.printInvoice(1, invoiceHeaderDetails, invoiceList);
                                        print.setOnCompletedListener(new TSCPrinterActivity.OnCompletedListener() {
                                            @Override
                                            public void onCompleted() {
                                                //helper.showLongToast(R.string.printed_successfully);
                                                Toast.makeText(getApplicationContext(), "Printed Successfully..!", Toast.LENGTH_SHORT).show();
                                           }
                                        });
                                    }
                                });*/

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
              else if (printerType.equals("Zebra Printer")) {
                    ZebraPrinterActivity zebraPrinterActivity = new ZebraPrinterActivity(NewExpensePrintPreviewActivity.this, printerMacId);
                 //   zebraPrinterActivity.printInvoice(1, invoiceHeaderDetails, invoiceList,"false");
                } else {
                    try {
                       final TSCPrinterActivity print = new TSCPrinterActivity(NewExpensePrintPreviewActivity.this, printerMacId, printerType);
                        print.initGenericPrinter();
                        print.setInitCompletionListener(() -> {
                         //   print.printInvoice(1, invoiceHeaderDetails, invoiceList);
                        });
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

    public void closeAlert(){
        if (alertInterface!=null){
            alertInterface.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
           getMenuInflater().inflate(R.menu.print_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //finish();
        Intent intent = new Intent(this, NewExpenseModuleListActivity.class);
        startActivity(intent);
        finish();
    }

}