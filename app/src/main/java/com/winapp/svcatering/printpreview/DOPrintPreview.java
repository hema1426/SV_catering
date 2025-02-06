package com.winapp.svcatering.printpreview;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.InvoicePrintPreviewAdapter;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;
import com.winapp.svcatering.thermalprinter.PrinterUtils;
import com.winapp.svcatering.tscprinter.TSCPrinterActivity;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.winapp.svcatering.zebraprinter.TSCPrinter;
import com.winapp.svcatering.zebraprinter.ZebraPrinterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DOPrintPreview extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    private String companyId;
    private SweetAlertDialog pDialog;
    private String doNumber;
    private SessionManager session;
    private HashMap<String, String> user;
    private ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails;
    private ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList;
    private RecyclerView invoiceListView;
    private InvoicePrintPreviewAdapter adapter;
    private TextView invoiceNumberText;
    private TextView invoiceDateText;
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

    private TextView companyAddress3Text;
    private TextView companyPhoneText;
    private TextView companyGstText;
    String company_phone="";
    String company_gst="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        setContentView(R.layout.activity_d_o_print_preview);
        setTitle();
        TscDll = new TSCActivity();
        session = new SessionManager(this);
        user = session.getUserDetails();
        companyId = user.get(SessionManager.KEY_COMPANY_CODE);
        company_name = user.get(SessionManager.KEY_COMPANY_NAME);
        company_address1 = user.get(SessionManager.KEY_ADDRESS1);
        company_address2 = user.get(SessionManager.KEY_ADDRESS2);
        company_address3 = user.get(SessionManager.KEY_ADDRESS3);
        company_phone=user.get(SessionManager.KEY_PHONE_NO);
        company_gst=user.get(SessionManager.KEY_COMPANY_REG_NO);

        invoiceListView = findViewById(R.id.invoiceList);
        invoiceNumberText = findViewById(R.id.sr_no);
        invoiceDateText = findViewById(R.id.sr_date);
        customerCodetext = findViewById(R.id.customer_code);
        customerNameText = findViewById(R.id.customer_name_value);
        deliveryAddressText = findViewById(R.id.delivery_address);
        billDiscountText = findViewById(R.id.bill_disc);
        subtotalText = findViewById(R.id.balance_value);
        taxValueText = findViewById(R.id.tax);
        netTotalText = findViewById(R.id.net_total);
        outstandingText = findViewById(R.id.outstanding_amount);
        taxTitle = findViewById(R.id.tax_title);
        itemDiscount = findViewById(R.id.item_disc);
        companyNametext = findViewById(R.id.company_name);
        companyAddress1Text = findViewById(R.id.address1);
        companyAddress2Text = findViewById(R.id.address2);
        addressLayout = findViewById(R.id.adressLayout);
        rootLayout = findViewById(R.id.rootLayout);

        companyAddress3Text=findViewById(R.id.address3);
        companyGstText=findViewById(R.id.gst_no);
        companyPhoneText=findViewById(R.id.mobile_no);

        address1Layout=findViewById(R.id.address1Layout);
        address2Layout=findViewById(R.id.address2Layout);
        address3Layout=findViewById(R.id.address3Layout);

        customerAddress1=findViewById(R.id.cus_address1);
        customerAddress2=findViewById(R.id.cus_address2);
        customerAddress3=findViewById(R.id.cus_address3);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType = sharedPreferences.getString("printer_type", "");
        printerMacId = sharedPreferences.getString("mac_address", "");
        shareLayout=findViewById(R.id.share_layout);
        printLayout=findViewById(R.id.print_layout);
        cancelButton=findViewById(R.id.cancel);

        pdfView = findViewById(R.id.pdfView);
        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

        checkPermission();
        requestPermission();

        Log.w("Printer_Mac_Id:", printerMacId);
        Log.w("Printer_Type:", printerType);

        if (getIntent() != null) {
            doNumber = getIntent().getStringExtra("doNumber");
            outstanding_amount=getIntent().getStringExtra("outstandingAmount");
            if (doNumber != null) {
                try {
                    getDoDetails(doNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                sharePdfView(pdfFile);
            }
        });

        printLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                printPreview();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    private void getDoDetails(String doNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject = new JSONObject();
       // jsonObject.put("CompanyCode", companyId);
        jsonObject.put("DeliveryOrderNo", doNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "DeliveryOrderDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        invoiceHeaderDetails = new ArrayList<>();
        invoiceList = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
                    try {
                        Log.w("DoDetails:", response.toString());
                        if (response.length() > 0) {

                          //  {"statusCode":1,"statusMessage":"Success",
                            //  "responseData":[{"customerCode":"C1002","customerName":"1+1MINI MART","doNumber":"2","doStatus":"O",
                            //  "doDate":"20\/8\/2021 12:00:00 am","netTotal":"80.250000","balanceAmount":"80.250000","totalDiscount":"0.000000",
                            //  "paidAmount":"0.000000","contactPersonCode":"0","createDate":"20\/8\/2021 12:00:00 am",
                            //  "updateDate":"20\/8\/2021 12:00:00 am","remark":"","fDocTotal":"0.000000","fTaxAmount":"0.000000",
                            //  "receivedAmount":"0.000000","total":"80.250000","fTotal":"0.000000","iTotalDiscount":"0.000000",
                            //  "taxTotal":"5.250000","iPaidAmount":"0.000000","currencyCode":"SGD","currencyName":"Singapore Dollar",
                            //  "companyCode":"AATHI_LIVE_DB","docEntry":"2","address1":"","taxPercentage":"0.000000",
                            //  "discountPercentage":"0.000000","subTotal":"75.000000","taxType":"E","taxCode":"EX","taxPerc":"7.000000",
                            //  "billDiscount":"0.000000","signFlag":"N","signature":"",

                            //  "deliveryOrderDetails":[{"slNo":"1",
                            //  "companyCode":"AATHI_LIVE_DB","doNo":"2","productCode":"SKU-AAFZ-0001","productName":"ASHOKA BHATURA 325 GM",
                            //  "quantity":"5.000000","cartonQty":"1.000000","price":"15.000000","currency":"SGD","taxRate":"0.000000",
                            //  "discountPercentage":"0.000000","lineTotal":"80.250000","fRowTotal":"0.000000","warehouseCode":"01",
                            //  "salesEmployeeCode":"-1","accountCode":"410101","taxStatus":"Y","unitPrice":"15.000000","customerCategoryNo":"",
                            //  "barCodes":"","totalTax":"5.250000","fTaxAmount":"0.000000","taxCode":"","taxType":"E","taxPerc":"0.000000",
                            //  "uoMCode":null,"doDate":"20\/8\/2021 12:00:00 am","dueDate":"20\/8\/2021 12:00:00 am",
                            //  "createDate":"20\/8\/2021 12:00:00 am","updateDate":"20\/8\/2021 12:00:00 am","createdUser":"manager",
                            //  "uomCode":"PCS","uoMName":"PCS","cartonPrice":"0.000000","piecePrice":"0.000000","pcsPerCarton":"1.000000",
                            //  "lPrice":"0.000000","unitQty":"1.000000","retailPrice":"0.000000","netTotal":"75.000000",
                            //  "subTotal":"75.00000000000","purchaseTaxPerc":"0.000000","purchaseTaxRate":"7.000000","taxAmount":"5.250000",
                            //  "purchaseTaxCode":"","total":"80.250000","itemDiscount":"0.000000"}]}]}

                            String statusCode=response.optString("statusCode");
                            if (statusCode.equals("1")){
                                JSONArray headerArray=response.optJSONArray("responseData");
                                JSONObject object=headerArray.optJSONObject(0);
                                InvoicePrintPreviewModel model = new InvoicePrintPreviewModel();
                                model.setInvoiceNumber(object.optString("doNumber"));
                                model.setInvoiceDate(object.optString("doDate"));
                                model.setCustomerCode(object.optString("customerCode"));
                                model.setCustomerName(object.optString("customerName"));
                                model.setAddress(object.optString("address1"));
                                model.setDeliveryAddress(object.optString("shippingAddress"));
                                model.setSubTotal(object.optString("subTotal"));
                                model.setNetTax(object.optString("totalTax"));
                                model.setNetTotal(object.optString("netTotal"));
                                model.setTaxType(object.optString("taxType"));
                                model.setTaxValue(object.optString("taxPerc"));
                                model.setOutStandingAmount(object.optString("OutstandingBalance"));
                                model.setBillDiscount(object.optString("billDiscount"));
                                model.setItemDiscount(object.optString("totalDiscount"));
                                String signFlag=object.optString("signFlag");
                                if (signFlag.equals("Y")){
                                    String signature=object.optString("signature");
                                    Utils.setSignature(signature);
                                }else {
                                    Utils.setSignature("");
                                }


                                model.setAddress1(object.optString("address1"));
                                model.setAddress2(object.optString("address2"));
                                model.setAddress3(object.optString("address3"));

                                JSONArray detailsArray=object.optJSONArray("deliveryOrderDetails");
                                for (int i = 0; i < detailsArray.length(); i++) {
                                    JSONObject detailObject = detailsArray.getJSONObject(i);
                                     InvoicePrintPreviewModel.InvoiceList invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                                        invoiceListModel.setProductCode(detailObject.optString("productCode"));
                                        invoiceListModel.setDescription(detailObject.optString("productName"));
                                        invoiceListModel.setLqty(detailObject.optString("unitQty"));
                                        invoiceListModel.setCqty(detailObject.optString("unitQty"));
                                        invoiceListModel.setNetQty(detailObject.optString("quantity"));
                                    invoiceListModel.setNetQuantity(detailObject.optString("quantity"));
                                    invoiceListModel.setSaleType("");

                                        double qty = Double.parseDouble(detailObject.optString("quantity"));
                                        double price = Double.parseDouble(detailObject.optString("price"));

                                        double nettotal = qty * price;
                                        invoiceListModel.setTotal(String.valueOf(nettotal));
                                        invoiceListModel.setPricevalue(String.valueOf(price));
                                        invoiceListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                                        invoiceListModel.setUnitPrice(detailObject.optString("price"));
                                        invoiceListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                                        invoiceListModel.setItemtax(detailObject.optString("taxAmount"));
                                        invoiceListModel.setSubTotal(detailObject.optString("subTotal"));
                                        invoiceList.add(invoiceListModel);
                                    }
                                model.setInvoiceList(invoiceList);
                                invoiceHeaderDetails.add(model);
                            }else {

                            }
                        }
                        if (invoiceList.size() > 0) {
                            setInvoiceAdapter();
                        }
                        pDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            pDialog.dismiss();
            Log.w("Error_throwing:", error.toString());
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

    public void setInvoiceAdapter() {
        double net_qty=0.0;
        for (InvoicePrintPreviewModel.InvoiceList model:invoiceList){
            net_qty+=Double.parseDouble(model.getNetQty());
        }
        netTotalText.setText((int)net_qty+"");
        for (InvoicePrintPreviewModel model : invoiceHeaderDetails) {
            invoiceNumberText.setText(model.getInvoiceNumber());
            invoiceDateText.setText(model.getInvoiceDate());
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

          /*  if (!model.getDeliveryAddress().isEmpty()) {
                addressLayout.setVisibility(View.VISIBLE);
                deliveryAddressText.setText(model.getDeliveryAddress());
            }*/
            taxTitle.setText("GST ( " + model.getTaxType() + " : " + Utils.twoDecimalPoint(Double.parseDouble(model.getTaxValue())) + " % ) ");
          /*  if (model.getTaxType().equals("I")) {
                double sub_total = Double.parseDouble(model.getNetTotal()) - Double.parseDouble(model.getNetTax());
                billDiscountText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBillDiscount())));
                subtotalText.setText(Utils.twoDecimalPoint(sub_total));
                taxValueText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTax())));
                netTotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));
                outstandingText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getOutStandingAmount())));
                itemDiscount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getItemDiscount())));
            } else {
                billDiscountText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBillDiscount())));
                subtotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getSubTotal())));
                taxValueText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTax())));
                netTotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));
                outstandingText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getOutStandingAmount())));
                itemDiscount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getItemDiscount())));
            }*/
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
        invoiceListView.setLayoutManager(new LinearLayoutManager(DOPrintPreview.this, LinearLayoutManager.VERTICAL, false));
        adapter = new InvoicePrintPreviewAdapter(DOPrintPreview.this, invoiceList,"Do");
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
                createPdf();
            } else {
                Toast.makeText(getApplicationContext(),"Permission required to share content",Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void printPreview(){
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

    }

    private void createPdf(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        canvas.drawPaint(paint);


        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        // canvas.drawText("TestData",0,0,null);
        document.finishPage(page);


        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/PdfTett/";
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
        File filePath = new File(dir, doNumber +".pdf");
        try {
            document.writeTo(new FileOutputStream(filePath));
            // btn_generate.setText("Check PDF");
            pdfFile=filePath;
            displayFromAsset(filePath);
            // viewPdfFile(filePath);
            //  openPDFFiles(filePath.toString());
            boolean_save=true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
    }


    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void viewPdfFile(File file_name_path) {
        Log.w("FilePathHere:",file_name_path.toString());
        // File file = new File(file_name_path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file_name_path), "application/pdf");
        startActivity(intent);
    }







    public void sharePdfView(File pdfFile){
        //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        //   pdfUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", pdfFile);
        //  } else {
        //   pdfUri = Uri.fromFile(pdfFile);
        // }
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdfFile));
        startActivity(Intent.createChooser(share, "Share"));
    }

    public void showPdf(){
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


    private void displayFromAsset(File assetFileName) {
        Log.w("PrintPreViewPath:",assetFileName.toString());
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        //  pdfFileName = assetFileName;
        //   Log.w(getFilesDir()+assetFileName,"FilePath");
        pdfView.fromFile(assetFileName)
                .defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }


    @Override
    public void loadComplete(int nbPages) {
        // PdfDocument.Meta meta = pdfView.getDocumentMeta();
        // printBookmarksTree(pdfView.getTableOfContents(), "-");

    }



    /*public void createpdf() {
        Rect bounds = new Rect();
        int pageWidth = 300;
        int pageheight = 470;
        int pathHeight = 2;

        final String fileName = "mypdf";
        file_name_path = "/pdfsdcard_location/" + fileName + ".pdf";
        PdfDocument myPdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint paint2 = new Paint();
        Path path = new Path();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageheight, 1).create();
        PdfDocument.Page documentPage = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = documentPage.getCanvas();
        int y = 25; // x = 10,
        int x = 10;

        paint.getTextBounds(tv_title.getText().toString(), 0, tv_title.getText().toString().length(), bounds);
        x = (canvas.getWidth() / 2) - (bounds.width() / 2);
        canvas.drawText(tv_title.getText().toString(), x, y, paint);

        paint.getTextBounds(tv_sub_title.getText().toString(), 0, tv_sub_title.getText().toString().length(), bounds);
        x = (canvas.getWidth() / 2) - (bounds.width() / 2);
        y += paint.descent() - paint.ascent();
        canvas.drawText(tv_sub_title.getText().toString(), x, y, paint);

        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);

//horizontal line
        path.lineTo(pageWidth, pathHeight);
        paint2.setColor(Color.GRAY);
        paint2.setStyle(Paint.Style.STROKE);
        path.moveTo(x, y);

        canvas.drawLine(0, y, pageWidth, y, paint2);

//blank space
        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);

        y += paint.descent() - paint.ascent();
        x = 10;
        canvas.drawText(tv_location.getText().toString(), x, y, paint);

        y += paint.descent() - paint.ascent();
        x = 10;
        canvas.drawText(tv_city.getText().toString(), x, y, paint);

//blank space
        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);

//horizontal line
        path.lineTo(pageWidth, pathHeight);
        paint2.setColor(Color.GRAY);
        paint2.setStyle(Paint.Style.STROKE);
        path.moveTo(x, y);
        canvas.drawLine(0, y, pageWidth, y, paint2);

//blank space
        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);

        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.logo);
        Bitmap b = (Bitmap.createScaledBitmap(bitmap, 100, 50, false));
        canvas.drawBitmap(b, x, y, paint);
        y += 25;
        canvas.drawText(getString(R.string.app_name), 120, y, paint);


        myPdfDocument.finishPage(documentPage);

        File file = new File(this.getExternalFilesDir(null).getAbsolutePath() + file_name_path);
        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDocument.close();
        viewPdfFile();
    }
    public void viewPdfFile() {

        File file = new File(this.getExternalFilesDir(null).getAbsolutePath() + file_name_path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        startActivity(intent);
    }
*/

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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

    public void showPrintAlert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(DOPrintPreview.this);
        builder1.setMessage("Do you want to print this Delivery Order ?.");
        builder1.setCancelable(false);
        builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertInterface = dialog;
                if (printerType.equals("TSC Printer")) {
                    dialog.dismiss();
                    //TSCPrinter tscPrinter=new TSCPrinter(DOPrintPreview.this,printerMacId);
                    // tscPrinter.printInvoice(invoiceHeaderDetails,invoiceList);

                    TSCPrinter printer=new TSCPrinter(getApplicationContext(),printerMacId,"DO");

                       // printer.printDeliveryOrder1(1,invoiceHeaderDetails,invoiceList);
                        new PrinterUtils(getApplicationContext(), printerMacId).printDeliveryOrder1(
                                1,
                                invoiceHeaderDetails,
                                invoiceList);

                    printer.setOnCompletionListener(new TSCPrinter.OnCompletionListener() {
                        @Override
                        public void onCompleted() {
                            Utils.setSignature("");
                            Toast.makeText(getApplicationContext(),"Delivery Order printed successfully!",Toast.LENGTH_SHORT).show();
                        }
                    });
//                    try {
//                        final TSCPrinterActivity print = new TSCPrinterActivity(DOPrintPreview.this, printerMacId, printerType);
//                        print.initGenericPrinter();
//                        print.setInitCompletionListener(new TSCPrinterActivity.InitCompletionListener() {
//                            @Override
//                            public void initCompleted() {
//                                print.printInvoice(1, invoiceHeaderDetails, invoiceList);
//                                print.setOnCompletedListener(new TSCPrinterActivity.OnCompletedListener() {
//                                    @Override
//                                    public void onCompleted() {
//                                        //helper.showLongToast(R.string.printed_successfully);
//                                        Toast.makeText(getApplicationContext(), "Printed Successfully..!", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        });
//
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }

                } else if (printerType.equals("Zebra Printer")) {
                    ZebraPrinterActivity zebraPrinterActivity = new ZebraPrinterActivity(DOPrintPreview.this, printerMacId);
                    zebraPrinterActivity.printDeliveryOrder(1, invoiceHeaderDetails, invoiceList);
                } else {
                    try {
                        final TSCPrinterActivity print = new TSCPrinterActivity(DOPrintPreview.this, printerMacId, printerType);
                        print.initGenericPrinter();
                        print.setInitCompletionListener(() -> {
                            print.printInvoice(1, invoiceHeaderDetails, invoiceList);
                            print.setOnCompletedListener(() -> {
                                //helper.showLongToast(R.string.printed_successfully);
                                Toast.makeText(getApplicationContext(), "Printed Successfully..!", Toast.LENGTH_SHORT).show();
                            });
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


    public void setTitle(){
        //Customize the ActionBar
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar_title, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("DELIVERY ORDER");
        Objects.requireNonNull(abar).setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setHomeButtonEnabled(true);
    }

    public void printInvoice(){
        try {
            TscDll.openport(printerMacId);
            //  TscDll.downloadpcx("UL.PCX");
            // TscDll.downloadbmp("Triangle.bmp");
            // TscDll.downloadttf("ARIAL.TTF");
            TscDll.setup(70, 110, 4, 4, 0, 0, 0);
            TscDll.clearbuffer();
            TscDll.sendcommand("SET TEAR ON\n");
            TscDll.sendcommand("SET COUNTER @1 1\n");
            TscDll.sendcommand("@1 = \"0001\"\n");
            TscDll.sendcommand("TEXT 100,300,\"3\",0,1,1,@1\n");
            //   TscDll.sendcommand("PUTPCX 100,300,\"UL.PCX\"\n");
            //   TscDll.sendcommand("PUTBMP 100,520,\"Triangle.bmp\"\n");
            TscDll.sendcommand("TEXT 100,760,\"2\",0,15,15,\"Sample print Text\"\n");
            TscDll.sendcommand("TEXT 25,190,”TST24.BF2\",0,1,1,”日傑茶坊 TEL:0000–0000\"");
            TscDll.sendcommand("TEXT 0,0,\"FONT001\",0,1,1,\"THIS IS 桂花烏龍奶茶\"\n");
            TscDll.barcode(100, 100, "128", 100, 1, 0, 3, 3, "123456789");
            TscDll.printerfont(100, 250, "3", 0, 1, 1, "Test Printing");
            String status = TscDll.status();
            Log.w("Status_Print:",status);
            TscDll.printlabel(2, 1);
            TscDll.sendfile("zpl.txt");
            TscDll.closeport();
            Toast.makeText(getApplicationContext(),"Printed Successfully",Toast.LENGTH_SHORT).show();
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}