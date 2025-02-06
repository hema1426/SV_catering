package com.winapp.svcatering.receipts;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.os.Bundle;
import android.os.Environment;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReceiptsPrintPreview extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    private String companyId;
    private SweetAlertDialog pDialog;
    private String receiptNumber;
    private SessionManager session;
    private HashMap<String, String> user;
    Double netTotal = 0.0;
    Double netBal = 0.0;

    private ArrayList<ReceiptPrintPreviewModel> receiptsHeaderDetails;
    private ArrayList<ReceiptPrintPreviewModel.ReceiptsDetails> receiptsList;
    private RecyclerView receiptsListView;
    private ReceiptPrintPreViewAdapter adapter;
    private TextView receiptNumberText;
    private TextView receiptDateText;
    private TextView customerCodetext;
    private TextView customerNameText;
    private TextView addressText;
    private TextView deliveryAddressText;
    private TextView billDiscountText;
    private TextView balanceReceipt;
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
    TextView paymodeText;
    ReceiptsModel receiptsModel;
    String receiptDate;
    String customerName;
    String customerCode;
    String paymode;

    private static final int PERMISSION_REQUEST_CODE = 100;
    boolean boolean_permission;
    boolean boolean_save;
    Bitmap bitmap;
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    static BottomSheetBehavior behavior;
    LinearLayout shareLayout;
    File pdfFile;
    LinearLayout printLayout;
    Button cancelButton;
    private String locationCode;

    LinearLayout address1Layout;
    LinearLayout address2Layout;
    LinearLayout address3Layout;
    LinearLayout address4Layout;

    TextView customerAddress1;
    TextView customerAddress2;
    TextView customerAddress3;
    TextView customerAddress4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts_print_preview);
        setTitle();
        TscDll = new TSCActivity();
        session = new SessionManager(this);
        user = session.getUserDetails();
        companyId = user.get(SessionManager.KEY_COMPANY_CODE);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        company_name = user.get(SessionManager.KEY_COMPANY_NAME);
        company_address1 = user.get(SessionManager.KEY_ADDRESS1);
        company_address2 = user.get(SessionManager.KEY_ADDRESS2);
        company_address3 = user.get(SessionManager.KEY_ADDRESS3);
        receiptsListView = findViewById(R.id.invoiceList);
        receiptNumberText = findViewById(R.id.receipt_no);
        receiptDateText = findViewById(R.id.receipt_date);
        customerCodetext = findViewById(R.id.customer_code_no);
        customerNameText = findViewById(R.id.customer_name_value);
        deliveryAddressText = findViewById(R.id.delivery_address);
        billDiscountText = findViewById(R.id.bill_disc);
        balanceReceipt = findViewById(R.id.balance_value);
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
        paymodeText=findViewById(R.id.pay_mode);

        address1Layout=findViewById(R.id.address1Layout);
        address2Layout=findViewById(R.id.address2Layout);
        address3Layout=findViewById(R.id.address3Layout);
        address4Layout=findViewById(R.id.address4Layout);

        customerAddress1=findViewById(R.id.cus_address1);
        customerAddress2=findViewById(R.id.cus_address2);
        customerAddress3=findViewById(R.id.cus_address3);
        customerAddress4=findViewById(R.id.cus_address4);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType = sharedPreferences.getString("printer_type", "");
        printerMacId = sharedPreferences.getString("mac_address", "");

        shareLayout=findViewById(R.id.share_layout);
        printLayout=findViewById(R.id.print_layout);
        cancelButton=findViewById(R.id.cancel);

        pdfView= (PDFView)findViewById(R.id.pdfView);
        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

        Log.w("Printer_Mac_Id:", printerMacId);
        Log.w("Printer_Type:", printerType);

        checkPermission();

        if (getIntent() != null) {
            receiptNumber = getIntent().getStringExtra("receiptNumber");
            paymode=getIntent().getStringExtra("payMode");
            customerCode=getIntent().getStringExtra("customerCode");
            customerName=getIntent().getStringExtra("customerName");
            receiptDate=getIntent().getStringExtra("date");

            Log.w("CustomerCodeDisplay:",customerCode);
            Log.w("PaymentMode:",paymode);
            Log.w("ReceiptDate:",receiptDate);

            if (receiptNumber != null) {
                try {
                    getReceiptsDetails(receiptNumber);
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
                try {
                    printPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    public void printPreview() throws IOException {
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

    private void getReceiptsDetails(String receiptNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject = new JSONObject();
     //   jsonObject.put("CompanyCode", companyId);
        jsonObject.put("ReceiptNo", receiptNumber);
        jsonObject.put("LocationCode",locationCode);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "ReceiptDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url);
        Log.w("JsonObjectPrint:",jsonObject.toString());
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        receiptsHeaderDetails = new ArrayList<>();
        receiptsList = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        Log.w("ReceiptDetails_Details:", response.toString());

                      //  {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"C1001","customerName":"BRINDAS PTE LTD",
                        //  "receiptNumber":"9","receiptStatus":"O","receiptDate":"18\/8\/2021 12:00:00 am","netTotal":"5.350000",
                        //  "balanceAmount":5.35,"totalDiscount":0,"paidAmount":0,"contactPersonCode":"","createDate":"18\/8\/2021 12:00:00 am",
                        //  "updateDate":"18\/8\/2021 12:00:00 am","remark":"","fDocTotal":0,"fTaxAmount":0,"receivedAmount":0,"total":5.35,
                        //  "fTotal":0,"iTotalDiscount":0,"taxTotal":0.35,"iPaidAmount":0,"currencyCode":"SGD","currencyName":"Singapore Dollar",
                        //  "companyCode":"AATHI_LIVE_DB","docEntry":"9","address1":null,"taxPercentage":null,"discountPercentage":null,
                        //  "subTotal":5,"taxType":"I","taxCode":"IN","taxPerc":"7.000000","billDiscount":0,"signFlag":null,"signature":null,
                        //
                        //  "receiptDetails":[{"paymentNo":"9","paymentDate":"29\/8\/2021 12:00:00 am","cardCode":"C1001",
                        //  "cardName":"BRINDAS PTE LTD","paymentTot":225.02,"invoiceNo":"25","invoiceDate":"24\/8\/2021 12:00:00 am",
                        //  "slpName":"-No Sales Employee-","invoiceTot":225.02,"totDiscount":0,"invoiceAcct":null}]}]}

                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray responseArray=response.optJSONArray("responseData");
                            JSONObject responseObject=responseArray.optJSONObject(0);
                            ReceiptPrintPreviewModel model = new ReceiptPrintPreviewModel();
                            model.setReceiptNumber(responseObject.optString("receiptNumber"));
                            model.setReceiptDate(responseObject.optString("receiptDate"));
                            model.setPayMode(responseObject.optString("payMode"));
                            model.setAddress(responseObject.optString("address1") + responseObject.optString("address2") + responseObject.optString("address3"));
                            model.setAddress1(responseObject.optString("address1"));
                            model.setAddress2(responseObject.optString("address2"));
                            model.setAddress3(responseObject.optString("address3"));
                            model.setAddressstate(responseObject.optString("block")+" "+responseObject.optString("street")+" "
                                    +responseObject.optString("city"));
                            model.setAddresssZipcode(responseObject.optString("countryName")+" "+responseObject.optString("state")+" "
                                    +responseObject.optString("zipcode"));

                            model.setCustomerCode(responseObject.optString("customerCode"));
                            model.setCustomerName(responseObject.optString("customerName"));
                            model.setTotalAmount(responseObject.optString("netTotal"));
                            model.setPaymentType(responseObject.optString("paymentType"));
                            model.setBankCode(responseObject.optString("bankCode"));
                            model.setBankName(responseObject.optString("bankName"));
                            model.setChequeDate(responseObject.optString("checkDueDate"));
                            model.setBankTransferDate(responseObject.optString("bankTransferDate"));
                            model.setCreditAmount(responseObject.optString("totalDiscount"));
                            model.setChequeNo(responseObject.optString("checkNumber"));
                            Utils.setInvoiceMode("Invoice");
                            Utils.setReceiptMode("true");
                            if (responseObject.optString("signature")!=null && !responseObject.optString("signature").equals("null") && !responseObject.optString("signature").isEmpty()){
                                String signature = responseObject.optString("signature");
                                Utils.setSignature(signature);
                                createSignature();
                            } else {
                                Utils.setSignature("");
                            }

                            JSONArray detailsArray=responseObject.optJSONArray("receiptDetails");
                            for (int i=0;i<detailsArray.length();i++){
                                JSONObject object=detailsArray.getJSONObject(i);
                                ReceiptPrintPreviewModel.ReceiptsDetails invoiceListModel = new ReceiptPrintPreviewModel.ReceiptsDetails();
                                invoiceListModel.setInvoiceNumber(object.optString("invoiceNo"));
                                invoiceListModel.setInvoiceDate(object.optString("invoiceDate"));
                                invoiceListModel.setAmount(object.optString("paidAmount"));
                                invoiceListModel.setDiscountAmount(object.optString("discountAmount"));
                                invoiceListModel.setCreditAmount(object.optString("creditAmount"));
                                invoiceListModel.setBalanceAmount(object.optString("balanceAmount"));

                                netTotal += Double.parseDouble(object.optString("paidAmount"));
                                netBal += Double.parseDouble(object.optString("balanceAmount"));

                                receiptsList.add(invoiceListModel);
                            }
                            model.setReceiptsDetailsList(receiptsList);
                            receiptsHeaderDetails.add(model);
                            if (receiptsList.size() > 0) {
                                netTotalText.setText(Utils.twoDecimalPoint(netTotal));
                                balanceReceipt.setText(Utils.twoDecimalPoint(netBal));

                                setInvoiceAdapter();
                            }
                        }else {

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

    private void createSignature() {
        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
            try {
                ImageUtil.saveStamp(this, Utils.getSignature(), "Signature");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void setInvoiceAdapter() {
        for (ReceiptPrintPreviewModel model : receiptsHeaderDetails) {
            receiptNumberText.setText(model.getReceiptNumber());
            receiptDateText.setText(model.getReceiptDate());
            customerCodetext.setText(model.getCustomerCode());
            paymodeText.setText(model.getPayMode());
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
        }

        companyNametext.setText(company_name);
        companyAddress1Text.setText(company_address1);
        companyAddress2Text.setText(company_address2 + "," + company_address3);
        receiptsListView.setHasFixedSize(true);
        // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        receiptsListView.setLayoutManager(new LinearLayoutManager(ReceiptsPrintPreview.this, LinearLayoutManager.VERTICAL, false));
        adapter = new ReceiptPrintPreViewAdapter(ReceiptsPrintPreview.this, receiptsList);
        receiptsListView.setAdapter(adapter);
        rootLayout.setVisibility(View.VISIBLE);
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
        document.finishPage(page);


        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/PdfTett/";
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
        File filePath = new File(dir,receiptNumber+".pdf");
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
                    try {
                        showPrintAlert();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please configure Printer", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }else if (id == R.id.action_pdf) {
            if (boolean_permission) {
               // progressDialog = new ProgressDialog(this);
               // progressDialog.setMessage("Please wait");
                bitmap = Utils.loadBitmapFromView(rootLayout, rootLayout.getWidth(), rootLayout.getHeight());
                createPdf();
            } else {
                Toast.makeText(getApplicationContext(),"Permission required to share content",Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void showPrintAlert() throws IOException{
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ReceiptsPrintPreview.this);
        builder1.setMessage("Do you want to print this Receipt ?.");
        builder1.setCancelable(false);
        builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertInterface = dialog;
                if (printerType.equals("TSC Printer")) {
                    dialog.dismiss();
                  //  TSCPrinter tscPrinter = new TSCPrinter(ReceiptsPrintPreview.this, printerMacId);
                  //  tscPrinter.printInvoice(receiptsHeaderDetails, receiptsList);
                    TSCPrinter printer=new TSCPrinter(ReceiptsPrintPreview.this,printerMacId,"Receipt");
                   // try {
                        printer.printReceipts(1,receiptsHeaderDetails,receiptsList);
                    printer.setOnCompletionListener(new TSCPrinter.OnCompletionListener() {
                        @Override
                        public void onCompleted() {
                            Utils.setSignature("");
                            Toast.makeText(getApplicationContext(),"Receipt printed successfully!",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                } else if (printerType.equals("Zebra Printer")) {
                    ZebraPrinterActivity zebraPrinterActivity = new ZebraPrinterActivity(ReceiptsPrintPreview.this, printerMacId);
                    try {
                        zebraPrinterActivity.printReceipts(1,receiptsHeaderDetails, receiptsList);
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

    public void closeAlert() {
        if (alertInterface != null) {
            alertInterface.dismiss();
        }
    }


    public void setTitle() {
        //Customize the ActionBar
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar_title, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("RECEIPT");
        Objects.requireNonNull(abar).setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setHomeButtonEnabled(true);
    }
}