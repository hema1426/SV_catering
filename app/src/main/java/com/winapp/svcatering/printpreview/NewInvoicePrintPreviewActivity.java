package com.winapp.svcatering.printpreview;

import static com.winapp.svcatering.activity.NewInvoiceListActivity.shortCodeStr;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
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
import com.winapp.svcatering.adapter.NewInvoicePrintPreviewAdapter;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;
import com.winapp.svcatering.thermalprinter.PrinterUtils;
import com.winapp.svcatering.tscprinter.TSCPrinterActivity;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.ImageUtil;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
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

public class NewInvoicePrintPreviewActivity extends AppCompatActivity {

    private String companyId;
    private SweetAlertDialog pDialog;
    private String invoiceNumber;
    private TextView deliveryAddr_print_txtl ;
    private LinearLayout deliveryAddr_print_layl;
    private SessionManager session;
    private HashMap<String, String> user;
    private ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails;
    private ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList;
    private RecyclerView invoiceListView;
    private NewInvoicePrintPreviewAdapter adapter;
    private TextView invoiceNumberText;
    private TextView invoiceDateText;
    private TextView customerCodetext;
    private TextView customerNameText;
    private TextView addressText;
    private TextView deliveryAddressText;
    private TextView billDiscountText;
    private TextView subtotalText;
    private TextView billDiscountTxt;

    private TextView taxValueText;
    private TextView netTotalText;
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
    LinearLayout address4Layout;
    TextView customerAddress1;
    TextView customerAddress2;
    TextView customerAddress3;
    TextView customerAddress4;
    String company_phone;
    String company_gst;
    private ArrayList<InvoicePrintPreviewModel.SalesReturnList> salesReturnList ;

    private TextView gstText;
    private TextView gstTitle;
    private TextView cnsubtotalText;
    private TextView cnnetTotalText;
    private TextView cngstText,cnno;
    private TextView cngstTitle;
    private LinearLayout cn_lay;
    private LinearLayout normalLayl;
    private LinearLayout transLayl;
    private TextView outstanding_amountl;

    private TextView paymentText;
    private String username;
    private TextView userText;
    private LinearLayout cn_layl;
    private TextView balanceText;
    private LinearLayout mainLayout;
    private String locationCode;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invoice_print_preview);
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
        deliveryAddr_print_txtl = findViewById(R.id.deliveryAddr_print_txt);
        deliveryAddr_print_layl = findViewById(R.id.deliveryAddr_print_lay);
        customerCodetext =findViewById (R.id.customer_code);
        addressText =findViewById (R.id.customer_addr_inv);
        paymentText=findViewById (R.id.payment_terms);
        userText =findViewById (R.id.invoice_user);
        subtotalText =findViewById (R.id.insubtotal);
        billDiscountTxt =findViewById (R.id.invBillDiscount);

        address1Layout=findViewById(R.id.address1Layout);
        address2Layout=findViewById(R.id.address2Layout);
        address3Layout=findViewById(R.id.address3Layout);
        address4Layout=findViewById(R.id.address4Layout);

        customerAddress1=findViewById(R.id.cus_address1);
        customerAddress2=findViewById(R.id.cus_address2);
        customerAddress3=findViewById(R.id.cus_address3);
        customerAddress4=findViewById(R.id.cus_address4);

        netTotalText =findViewById (R.id.innettotal);
        gstText =findViewById (R.id.ingst_text);
        cnsubtotalText =findViewById (R.id.cnrsubtotal);
        cnnetTotalText =findViewById (R.id.cnrnet_total);
        cngstText =findViewById (R.id.cnrgst_text);
        gstTitle = findViewById (R.id.ingst_title);
        cngstTitle = findViewById (R.id.cngst_title);
        balanceText = findViewById (R.id.inbalance);
        cnno = findViewById (R.id.cn_no);
        cn_layl = findViewById (R.id.cn_lay);
        mainLayout=findViewById(R.id.main_layout);
        invoiceListView = findViewById (R.id.rv_invoicelist);
        transLayl = findViewById (R.id.transLay);
        normalLayl = findViewById (R.id.normalLay);
        outstanding_amountl  = findViewById (R.id.outstanding_amount);

        Log.e("compnam..",""+company_name);

        if(company_name.equalsIgnoreCase("Trans Orient Singapore Pte Ltd")){
            transLayl.setVisibility(View.VISIBLE);
            normalLayl.setVisibility(View.GONE);
        }
        else {
            transLayl.setVisibility(View.GONE);
            normalLayl.setVisibility(View.VISIBLE);
        }
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
        textviewTitle.setText("INVOICE");
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
        String url = Utils.getBaseUrl(this) + "InvoiceDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given__InvoicePreview:", url+"--"+jsonObject.toString());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        invoiceHeaderDetails = new ArrayList<>();
        salesReturnList=new ArrayList<>();
        // {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"WinApp","customerName":"WinApp","invoiceNumber":"33",
        // "invoiceStatus":"O","invoiceDate":"6\/8\/2021 12:00:00 am","netTotal":"26.750000","balanceAmount":"26.750000","totalDiscount":
        // "0.000000","paidAmount":"0.000000","contactPersonCode":"","createDate":"6\/8\/2021 12:00:00 am","updateDate":"6\/8\/2021 12:00:00 am",
        // "remark":"","fDocTotal":"0.000000","fTaxAmount":"0.000000","receivedAmount":"0.000000","total":"26.750000","fTotal":"0.000000",
        // "iTotalDiscount":"0.000000","taxTotal":"1.750000","iPaidAmount":"0.000000","currencyCode":"SGD","currencyName":"Singapore Dollar",
        // "companyCode":"WINAPP_DEMO","docEntry":"20","invoiceDetails":[{"slNo":"1","companyCode":"WINAPP_DEMO","invoiceNo":"33",
        // "productCode":"FG\/001245","productName":"RUM","quantity":"5.000000","price":"5.000000","currency":"SGD","taxRate":"0.000000",
        // "discountPercentage":"0.000000","lineTotal":"26.750000","fRowTotal":"0.000000","warehouseCode":"01","salesEmployeeCode":"-1",
        // "accountCode":"400000","taxStatus":"Y","unitPrice":"5.000000","customerCategoryNo":"","barCodes":"","totalTax":"1.750000",
        // "fTaxAmount":"0.000000","taxCode":"","taxType":"Y","taxPerc":"0.000000","uoMCode":null,"invoiceDate":"6\/8\/2021 12:00:00 am",
        // "dueDate":"6\/8\/2021 12:00:00 am","createDate":"6\/8\/2021 12:00:00 am","updateDate":"6\/8\/2021 12:00:00 am","createdUser":"manager"}]}]}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try {
                Log.w("DetailsResponse::", response.toString());
                String statusCode=response.optString("statusCode");
                if (statusCode.equals("1")){
                    JSONArray responseData=response.getJSONArray("responseData");
                    JSONObject object=responseData.optJSONObject(0);

                    Log.w("delivertAddrPrevie",""+object.optString("showShippingAddress")+".."+
                            object.optString("shippingAddress"));

                    InvoicePrintPreviewModel model = new InvoicePrintPreviewModel();
                    model.setInvoiceNumber(object.optString("invoiceNumber"));
                    model.setInvoiceDate(object.optString("invoiceDate"));
                    model.setCustomerCode(object.optString("customerCode"));
                    model.setCustomerName(object.optString("customerName"));
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
                    model.setAddressstate(object.optString("street")+" "+
                            object.optString("block")+" "+object.optString("city"));
                    model.setAddresssZipcode(object.optString("countryName")+" "+object.optString("state")+" "
                            +object.optString("zipcode"));

                    model.setSoNumber(object.optString("soNumber"));
                    model.setSoDate(object.optString("soDate"));
                    model.setDoDate(object.optString("doDate"));
                    model.setDoNumber(object.optString("doNumber"));
                    model.setAllowDeliveryAddress(object.optString("showShippingAddress"));
                    model.setDeliveryAddress(object.optString("shipAddress1")+object.optString("shipAddress2")+object.optString("shipAddress3")+
                            object.optString("shipStreet"));
                    model.setCurrentAddress(object.optString("CurrentAddress"));

                    String signFlag=object.optString("signFlag");
                    if (signFlag.equals("Y")){
                        String signature=object.optString("signature");
                        Utils.setSignature(signature);
                        createSignature();
                    }else {
                        Utils.setSignature("");
                    }
                    if(object.optString("showShippingAddress").equalsIgnoreCase("Yes")){
                        deliveryAddr_print_layl.setVisibility(View.VISIBLE);
                        deliveryAddr_print_txtl.setText(object.optString("shipAddress1")+object.optString("shipAddress2")+object.optString("shipAddress3")+
                                object.optString("shipStreet"));
                    }
                    else{
                        deliveryAddr_print_layl.setVisibility(View.GONE);
                    }
                    JSONArray detailsArray=object.optJSONArray("invoiceDetails");
                    invoiceList = new ArrayList<>();

                    for (int i=0;i<detailsArray.length();i++) {
                        JSONObject detailObject = detailsArray.optJSONObject(i);
                        if (Double.parseDouble(detailObject.optString("quantity")) > 0) {
                            InvoicePrintPreviewModel.InvoiceList invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                            invoiceListModel.setProductCode(detailObject.optString("productCode"));
                            invoiceListModel.setDescription(detailObject.optString("productName"));
                            invoiceListModel.setLqty(detailObject.optString("unitQty"));
                            invoiceListModel.setCqty(detailObject.optString("cartonQty"));
                            invoiceListModel.setNetQty(detailObject.optString("quantity"));
                            invoiceListModel.setExcQty(detailObject.optString("exc_Qty"));
                            invoiceListModel.setNetQuantity(detailObject.optString("netQuantity"));
                            invoiceListModel.setFocQty(detailObject.optString("foc_Qty"));
                            invoiceListModel.setSaleType("");
                            if (detailObject.optString("bP_CatalogNo") != null) {
                                invoiceListModel.setCustomerItemCode(detailObject.optString("bP_CatalogNo"));
                            }
                            invoiceListModel.setReturnQty(detailObject.optString("returnQty"));
                            invoiceListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                            invoiceListModel.setUomCode(detailObject.optString("uomCode"));
                            invoiceListModel.setUnitPrice(detailObject.optString("price"));
                            double qty = Double.parseDouble(detailObject.optString("quantity"));
                            double price = Double.parseDouble(detailObject.optString("price"));

                            double nettotal = qty * price;
                            invoiceListModel.setTotal(String.valueOf(nettotal));
                            invoiceListModel.setPricevalue(String.valueOf(price));

                            invoiceListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                            invoiceListModel.setItemtax(detailObject.optString("totalTax"));
                            invoiceListModel.setSubTotal(detailObject.optString("subTotal"));
                            invoiceList.add(invoiceListModel);
                            Log.w("invoicSizeEntr1","");

                        }
                        if (!detailObject.optString("returnQty").isEmpty() && !detailObject.optString("returnQty").equals("null")
                                && Double.parseDouble(detailObject.optString("returnQty")) > 0) {
                            InvoicePrintPreviewModel.InvoiceList invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                            invoiceListModel.setProductCode(detailObject.optString("productCode"));
                            invoiceListModel.setDescription(detailObject.optString("productName"));
                            invoiceListModel.setLqty(detailObject.optString("unitQty"));
                            invoiceListModel.setCqty(detailObject.optString("cartonQty"));
                            invoiceListModel.setNetQty(detailObject.optString("quantity"));
                            invoiceListModel.setExcQty(detailObject.optString("exc_Qty"));
                            invoiceListModel.setNetQuantity(detailObject.optString("returnQty"));
                            invoiceListModel.setFocQty(detailObject.optString("foc_Qty"));
                            invoiceListModel.setSaleType("Return");
                            if (detailObject.optString("bP_CatalogNo") != null) {
                                invoiceListModel.setCustomerItemCode(detailObject.optString("bP_CatalogNo"));
                            }
                            invoiceListModel.setReturnQty(detailObject.optString("returnQty"));
                            invoiceListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                            invoiceListModel.setUnitPrice(detailObject.optString("price"));
                            double qty = Double.parseDouble(detailObject.optString("quantity"));
                            double price = Double.parseDouble(detailObject.optString("price"));

                            double nettotal = qty * price;
                            invoiceListModel.setTotal("0.00");
                            invoiceListModel.setPricevalue("0.00");

                            invoiceListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                            invoiceListModel.setItemtax(detailObject.optString("totalTax"));
                            invoiceListModel.setSubTotal(detailObject.optString("subTotal"));
                            invoiceList.add(invoiceListModel);
                            Log.w("invoicSizeEntr2","");

                        }
                        if (!detailObject.optString("exc_Qty").isEmpty() && !detailObject.optString("exc_Qty").equals("null")
                                && Double.parseDouble(detailObject.optString("exc_Qty")) > 0) {
                            InvoicePrintPreviewModel.InvoiceList invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                            invoiceListModel.setProductCode(detailObject.optString("productCode"));
                            invoiceListModel.setDescription(detailObject.optString("productName"));
                            invoiceListModel.setLqty(detailObject.optString("unitQty"));
                            invoiceListModel.setCqty(detailObject.optString("cartonQty"));
                            invoiceListModel.setNetQty(detailObject.optString("quantity"));
                            invoiceListModel.setExcQty(detailObject.optString("exc_Qty"));
                            invoiceListModel.setNetQuantity(detailObject.optString("exc_Qty"));
                            invoiceListModel.setFocQty(detailObject.optString("foc_Qty"));
                            invoiceListModel.setSaleType("Exchange");

                            if (detailObject.optString("bP_CatalogNo") != null) {
                                invoiceListModel.setCustomerItemCode(detailObject.optString("bP_CatalogNo"));
                            }
                            invoiceListModel.setReturnQty(detailObject.optString("returnQty"));
                            invoiceListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                            invoiceListModel.setUnitPrice(detailObject.optString("price"));
                            double qty = Double.parseDouble(detailObject.optString("quantity"));
                            double price = Double.parseDouble(detailObject.optString("price"));

                            double nettotal = qty * price;
                            invoiceListModel.setTotal("0.00");
                            invoiceListModel.setPricevalue("0.00");

                            invoiceListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                            invoiceListModel.setItemtax(detailObject.optString("totalTax"));
                            invoiceListModel.setSubTotal(detailObject.optString("subTotal"));
                            invoiceList.add(invoiceListModel);
                            Log.w("invoicSizeEntr3","");

                        }
                        if (!detailObject.optString("foc_Qty").isEmpty() && !detailObject.optString("foc_Qty").equals("null")
                                && Double.parseDouble(detailObject.optString("foc_Qty")) > 0) {
                            InvoicePrintPreviewModel.InvoiceList invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                            invoiceListModel.setProductCode(detailObject.optString("productCode"));
                            invoiceListModel.setDescription(detailObject.optString("productName"));
                            invoiceListModel.setLqty(detailObject.optString("unitQty"));
                            invoiceListModel.setCqty(detailObject.optString("cartonQty"));
                            invoiceListModel.setNetQty(detailObject.optString("quantity"));
                            invoiceListModel.setExcQty(detailObject.optString("exc_Qty"));
                            invoiceListModel.setNetQuantity(detailObject.optString("foc_Qty"));
                            invoiceListModel.setFocQty(detailObject.optString("foc_Qty"));
                            invoiceListModel.setSaleType("FOC");

                            if (detailObject.optString("bP_CatalogNo") != null) {
                                invoiceListModel.setCustomerItemCode(detailObject.optString("bP_CatalogNo"));
                            }
                            invoiceListModel.setReturnQty(detailObject.optString("returnQty"));
                            invoiceListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                            invoiceListModel.setUnitPrice(detailObject.optString("price"));
                            double qty = Double.parseDouble(detailObject.optString("quantity"));
                            double price = Double.parseDouble(detailObject.optString("price"));

                            double nettotal = qty * price;
                            invoiceListModel.setTotal("0.00");
                            invoiceListModel.setPricevalue("0.00");

                            invoiceListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                            invoiceListModel.setItemtax(detailObject.optString("totalTax"));
                            invoiceListModel.setSubTotal(detailObject.optString("subTotal"));
                            invoiceList.add(invoiceListModel);
                            Log.w("invoicSizeEntr4","");

                        }
                        model.setInvoiceList(invoiceList);
                    }

                   Log.w("invoicSize",""+invoiceList.size());


                    JSONArray SRArray=object.optJSONArray("sR_Details");
                    assert SRArray != null;
                    if (SRArray.length() > 0){
                        JSONObject SRoblect = SRArray.optJSONObject(0);
                        InvoicePrintPreviewModel.SalesReturnList salesReturnModel = new InvoicePrintPreviewModel.SalesReturnList();
                        salesReturnModel.setSalesReturnNumber(SRoblect.optString("salesReturnNumber"));
                        salesReturnModel.setsRSubTotal(SRoblect.optString("sR_SubTotal"));
                        salesReturnModel.setsRTaxTotal(SRoblect.optString("sR_TaxTotal"));
                        salesReturnModel.setsRNetTotal(SRoblect.optString("sR_NetTotal"));
                        salesReturnList.add(salesReturnModel);
                    }
                    model.setSalesReturnList(salesReturnList);
                    invoiceHeaderDetails.add(model);
                    if (invoiceList.size() > 0) {
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
            invoiceNumberText.setText(model.getInvoiceNumber());
            invoiceDateText.setText(model.getInvoiceDate());
            customerCodetext.setText(model.getCustomerCode());
            customerNameText.setText(model.getCustomerName());
            paymentText.setText(model.getPaymentTerm());
            userText.setText(username);
            Log.w("custAddres",""+model.getAddress());

//            if (model.getAddress().isEmpty()){
//                addressText.setVisibility(View.GONE);
//            } else {
//                addressText.setText(model.getAddress());
//            }
            Log.w("addrs1",""+model.getAddress1());

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
          /*  if (!model.getDeliveryAddress().isEmpty()) {
                addressLayout.setVisibility(View.VISIBLE);
                deliveryAddressText.setText(model.getDeliveryAddress());
            }*/

            if(shortCodeStr.equalsIgnoreCase("FUXIN")){
                gstTitle.setText("GST ( " + model.getTaxType() + " : " + Utils.fourDecimalPoint(Double.parseDouble(model.getTaxValue())) + " % ) ");
                if (model.getTaxType().equals("I")) {
                    double sub_total = Double.parseDouble(model.getNetTotal()) - Double.parseDouble(model.getNetTax());
                    //billDiscountText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getBillDiscount())));
                    subtotalText.setText(Utils.fourDecimalPoint(sub_total));

                    gstText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getNetTax())));
                    billDiscountTxt.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getBillDiscount())));
                    netTotalText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getNetTotal())));
                    outstanding_amountl.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getOutStandingAmount())));
                    // itemDiscount.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getItemDiscount())));
                } else {
                    // billDiscountText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getBillDiscount())));
                    subtotalText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getSubTotal())));
                    gstText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getNetTax())));
                    billDiscountTxt.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getBillDiscount())));
                    netTotalText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getNetTotal())));
                    outstanding_amountl.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getOutStandingAmount())));
                    //  outstandingText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getOutStandingAmount())));
                    //  itemDiscount.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getItemDiscount())));
                }

                if (model.getSalesReturnList().size() > 0) {
                    cn_layl.setVisibility(View.VISIBLE);
                    cnno.setText(model.getSalesReturnList().get(0).getSalesReturnNumber());
                    //   Log.w("SubTotal:",model.getSalesReturnList().get(0).getsRSubTotal());
                    //   Log.w("TaxValue:",model.getSalesReturnList().get(0).getsRTaxTotal());
                    //  Log.w("NetTotalValue:",model.getSalesReturnList().get(0).getsRNetTotal());
                    cnsubtotalText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getSalesReturnList().get(0).getsRSubTotal())));
                    cngstTitle.setText("GST ( " + model.getTaxType() + " : " + Utils.fourDecimalPoint(Double.parseDouble(model.getTaxValue())) + " % ) ");
                    cngstText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getSalesReturnList().get(0).getsRTaxTotal())));
                    cnnetTotalText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getSalesReturnList().get(0).getsRNetTotal())));
                    balanceText.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getOverAllTotal())));
                }
            }else {

                gstTitle.setText("GST ( " + model.getTaxType() + " : " + Utils.twoDecimalPoint(Double.parseDouble(model.getTaxValue())) + " % ) ");
                if (model.getTaxType().equals("I")) {
                    double sub_total = Double.parseDouble(model.getNetTotal()) - Double.parseDouble(model.getNetTax());
                    //billDiscountText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBillDiscount())));
                    subtotalText.setText(Utils.twoDecimalPoint(sub_total));

                    gstText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTax())));
                    billDiscountTxt.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBillDiscount())));
                    netTotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));
                    outstanding_amountl.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getOutStandingAmount())));
                    // itemDiscount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getItemDiscount())));
                } else {
                    // billDiscountText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBillDiscount())));
                    subtotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getSubTotal())));
                    gstText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTax())));
                    billDiscountTxt.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBillDiscount())));
                    netTotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));
                    outstanding_amountl.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getOutStandingAmount())));
                    //  outstandingText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getOutStandingAmount())));
                    //  itemDiscount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getItemDiscount())));
                }

                if (model.getSalesReturnList().size() > 0) {
                    cn_layl.setVisibility(View.VISIBLE);
                    cnno.setText(model.getSalesReturnList().get(0).getSalesReturnNumber());
                    //   Log.w("SubTotal:",model.getSalesReturnList().get(0).getsRSubTotal());
                    //   Log.w("TaxValue:",model.getSalesReturnList().get(0).getsRTaxTotal());
                    //  Log.w("NetTotalValue:",model.getSalesReturnList().get(0).getsRNetTotal());
                    cnsubtotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getSalesReturnList().get(0).getsRSubTotal())));
                    cngstTitle.setText("GST ( " + model.getTaxType() + " : " + Utils.twoDecimalPoint(Double.parseDouble(model.getTaxValue())) + " % ) ");
                    cngstText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getSalesReturnList().get(0).getsRTaxTotal())));
                    cnnetTotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getSalesReturnList().get(0).getsRNetTotal())));
                    balanceText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getOverAllTotal())));
                }
            }

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
            companyGstText.setText("GST REG NO : "+company_gst);
            companyGstText.setVisibility(View.VISIBLE);
        }
        invoiceListView.setHasFixedSize(true);
        // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        invoiceListView.setLayoutManager(new LinearLayoutManager(NewInvoicePrintPreviewActivity.this, LinearLayoutManager.VERTICAL, false));
        adapter = new NewInvoicePrintPreviewAdapter(NewInvoicePrintPreviewActivity.this, invoiceList,"Invoice",company_name);
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
            finish();
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
        AlertDialog.Builder builder1 = new AlertDialog.Builder(NewInvoicePrintPreviewActivity.this);
        builder1.setMessage("Do you want to print this Invoice ?.");
        builder1.setCancelable(false);
        builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertInterface = dialog;
                if (printerType.equals("TSC Printer")) {
                   dialog.dismiss();
                    //TSCPrinter tscPrinter=new TSCPrinter(InvoicePrintPreviewActivity.this,printerMacId);
                    // tscPrinter.printInvoice(invoiceHeaderDetails,invoiceList);
                    try {

                        PrinterUtils printer=new PrinterUtils(NewInvoicePrintPreviewActivity.this,printerMacId);
                        printer.printInvoice(1,invoiceHeaderDetails,invoiceList,"false");

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
                    ZebraPrinterActivity zebraPrinterActivity = new ZebraPrinterActivity(NewInvoicePrintPreviewActivity.this, printerMacId);
                    zebraPrinterActivity.printInvoice(1, invoiceHeaderDetails, invoiceList,"false");
                } else {
                    try {
                       final TSCPrinterActivity print = new TSCPrinterActivity(NewInvoicePrintPreviewActivity.this, printerMacId, printerType);
                        print.initGenericPrinter();
                        print.setInitCompletionListener(() -> {
                            print.printInvoice(1, invoiceHeaderDetails, invoiceList);
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
}