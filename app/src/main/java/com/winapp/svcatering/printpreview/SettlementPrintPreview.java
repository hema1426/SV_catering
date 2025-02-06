package com.winapp.svcatering.printpreview;

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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.CurrencyDenominationAdapter;
import com.winapp.svcatering.adapter.SettleExpensePreviewAdapter;
import com.winapp.svcatering.model.CurrencyModel;
import com.winapp.svcatering.model.ExpenseModel;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;
import com.winapp.svcatering.tscprinter.TSCPrinterActivity;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.MyKeyboard;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
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

public class SettlementPrintPreview extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    private String companyId;
    private SweetAlertDialog pDialog;
    private String settlementNumber;
    private SessionManager session;
    private HashMap<String, String> user;
    private ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails;
    private ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList;
    private RecyclerView currencyView;
    private CurrencyDenominationAdapter denominationAdapter;
    private RecyclerView expenseView;
    private SettleExpensePreviewAdapter ExpensePreviewAdapter;

    private LinearLayout expenslayoutl;
    private TextView settlementNumberText;
    private TextView settlementDateText;
    private TextView customerCodetext;
    private TextView locationCodeText;
    private TextView addressText;
    private TextView deliveryAddressText;
    private TextView billDiscountText;
    private TextView subtotalText;
    private TextView taxValueText;
    private TextView netTotalText;
    private TextView expenseTotalText;

    private LinearLayout expenseTotalLay;

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
    private LinearLayout rootLayout;
    private LinearLayout addressLayout;
    private SharedPreferences sharedPreferences;
    private String printerMacId;
    private String printerType;
    private TSCActivity TscDll;
    private AlertDialog alert11;
    private DialogInterface alertInterface;
    public static int REQUEST_PERMISSIONS = 154;
    boolean boolean_permission;
    boolean boolean_save;
    private Bitmap bitmap;
    private ProgressDialog progressDialog;
    private static final String TAG = InvoicePrintPreviewActivity.class.getSimpleName();
    public static final String SAMPLE_FILE = "android_tutorial.pdf";
    private PDFView pdfView;
    private Integer pageNumber = 0;
    private String pdfFileName;
    static BottomSheetBehavior behavior;
    private LinearLayout shareLayout;
    private File pdfFile;
    private LinearLayout printLayout;
    private Button cancelButton;
    private static final int PERMISSION_REQUEST_CODE = 100;
    public String outstanding_amount="0.0";
    private ArrayList<CurrencyModel> currencyList;
    private CurrencyDenominationAdapter currencyDenominationAdapter;
    private ArrayList<ExpenseModel> expenseList;
    private RecyclerView denominationView;
    private TextView settlementBy;
    private String locationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        setContentView(R.layout.activity_settlement_print_preview);

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
        currencyView = findViewById(R.id.invoiceList);
        settlementNumberText = findViewById(R.id.sr_no);
        settlementDateText = findViewById(R.id.sr_date);
        customerCodetext = findViewById(R.id.customer_code);
        locationCodeText = findViewById(R.id.location_code);
        deliveryAddressText = findViewById(R.id.delivery_address);
        billDiscountText = findViewById(R.id.bill_disc);
        subtotalText = findViewById(R.id.sub_total);
        taxValueText = findViewById(R.id.tax);
        netTotalText = findViewById(R.id.net_total);
        expenseTotalText = findViewById(R.id.expense_total);
        expenseTotalLay = findViewById(R.id.expenseTotalLayl);
        outstandingText = findViewById(R.id.outstanding_amount);
        taxTitle = findViewById(R.id.tax_title);
        itemDiscount = findViewById(R.id.item_disc);
        companyNametext = findViewById(R.id.company_name);
        companyAddress1Text = findViewById(R.id.address1);
        companyAddress2Text = findViewById(R.id.address2);
        expenseView = findViewById(R.id.expenseList_settle);
        expenslayoutl = findViewById(R.id.expenslayout);
        addressLayout = findViewById(R.id.adressLayout);
        settlementBy=findViewById(R.id.user);
        rootLayout = findViewById(R.id.rootLayout);
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType = sharedPreferences.getString("printer_type", "");
        printerMacId = sharedPreferences.getString("mac_address", "");
        shareLayout=findViewById(R.id.share_layout);
        printLayout=findViewById(R.id.print_layout);
        cancelButton=findViewById(R.id.cancel);
        pdfView = findViewById(R.id.pdfView);
        expenseTotalText = findViewById(R.id.expense_total);

        View bottomSheet = findViewById(R.id.design_bottom_sheet);
       // behavior = BottomSheetBehavior.from(bottomSheet);

        checkPermission();
        requestPermission();

        Log.w("Printer_Mac_Id:", printerMacId);
        Log.w("Printer_Type:", printerType);

        if (getIntent() != null) {
            settlementNumber = getIntent().getStringExtra("settlementNumber");
            outstanding_amount=getIntent().getStringExtra("outstandingAmount");
            if (settlementNumber != null) {
                try {
                    getSettlementDetails(companyId,settlementNumber);
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
    private void getSettlementDetails(String companyCode, String editSettlementNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("SettlementNo",editSettlementNumber);
        jsonObject.put("LocationCode",locationCode);
        String url= Utils.getBaseUrl(this) +"SettlementDetails";
        // Initialize a new JsonArrayRequest instance
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Getting Settlement Details...");
        dialog.setCancelable(false);
        dialog.show();
        Log.w("Given_settle_print:",url+".. "+jsonObject);
        expenseList=new ArrayList<>();
        expenseList.clear();
        currencyList=new ArrayList<>();
        currencyList.clear();
        String expenseAmt = "0.00";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                response -> {
                    try{
                        Log.w("EditSettlementResponse:",response.toString());
                        dialog.dismiss();
                        if (response.length()>0){
                            String statusCode=response.optString("statusCode");
                            if (statusCode.equals("1")) {
                                JSONArray responseDataArray = response.getJSONArray("responseData");
                                JSONObject responseObject = responseDataArray.getJSONObject(0);
                                String settlementAmount = responseObject.optString("totalAmount");

                                JSONArray denominationArray = responseObject.optJSONArray("settlementDetails");
                                for (int i = 0; i < Objects.requireNonNull(denominationArray).length(); i++) {
                                    JSONObject currencyObject = denominationArray.optJSONObject(i);
                                    CurrencyModel product = new CurrencyModel();
                                    product.setCurrencyName(currencyObject.optString("currency"));
                                    product.setCount(currencyObject.optString("count"));
                                    product.setId(String.valueOf(i));
                                    product.setTotal(currencyObject.optString("total"));
                                    currencyList.add(product);
                                }
                                if (currencyList.size() > 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setCurrencyAdapter(responseObject);
                                        }
                                    });
                                }
                                    JSONArray expenseArray = responseObject.optJSONArray("settlementExpensesDetails");
                                if (expenseArray != null) {
                                    for (int i = 0; i < Objects.requireNonNull(expenseArray).length(); i++) {
                                        JSONObject expenseObject = expenseArray.optJSONObject(i);
                                        ExpenseModel model = new ExpenseModel();
                                        model.setExpenseName(expenseObject.optString("groupName"));
                                        model.setExpenseId(String.valueOf(i + 1));
                                        model.setGroupNo(expenseObject.optString("groupNo"));
                                        model.setExpenseTotal(expenseObject.optString("amount"));
                                        expenseList.add(model);
                                    }
                                    Log.w("expenseViewSizdd",""+expenseList.size());
                                    if (expenseList.size() > 0) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                expenslayoutl.setVisibility(View.VISIBLE);
                                                setExpensePreviewAdapter(expenseList);
                                                expenseTotalLay.setVisibility(View.VISIBLE);
                                                expenseTotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(responseObject.optString("totalExpense"))));
                                            }
                                        });
                                    } else {
                                        expenslayoutl.setVisibility(View.GONE);
                                        expenseTotalLay.setVisibility(View.GONE);
                                    }
                                }

                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
            Toast.makeText(getApplicationContext(),"Server Error,Please check",Toast.LENGTH_LONG).show();
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


    private void setExpensePreviewAdapter(ArrayList<ExpenseModel> expenseList) {
            expenseView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            ExpensePreviewAdapter = new SettleExpensePreviewAdapter(expenseList, new SettleExpensePreviewAdapter.CallBack() {
                @Override
                public void sortKeyboard(EditText expenseEditext) {
                    MyKeyboard keyboard = (MyKeyboard) findViewById(R.id.keyboard);
//                    // prevent system keyboard from appearing when EditText is tapped
//                    expenseEditext.setRawInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                   expenseEditext.setTextIsSelectable(true);
                    expenseEditext.setBackground(null);
//                    // pass the InputConnection from the EditText to the keyboard
                    InputConnection ic = expenseEditext.onCreateInputConnection(new EditorInfo());
                    keyboard.setInputConnection(ic);
                }
                @Override
                public void setExpenseTotal() {
                }
            });
            expenseView.setAdapter(ExpensePreviewAdapter);
    }

    public void setCurrencyAdapter(JSONObject jsonObject) {
        netTotalText.setText(Utils.twoDecimalPoint(Double.parseDouble(jsonObject.optString("totalAmount"))));
      //  for (InvoicePrintPreviewModel model : invoiceHeaderDetails) {
            settlementNumberText.setText(jsonObject.optString("settlementNo"));
            settlementDateText.setText(jsonObject.optString("settlementDate"));
           // customerCodetext.setText(model.getCustomerCode());
            locationCodeText.setText(jsonObject.optString("locationCode"));
            settlementBy.setText(jsonObject.optString("user"));
           // if (!model.getDeliveryAddress().isEmpty()) {
               // addressLayout.setVisibility(View.VISIBLE);
               // deliveryAddressText.setText(model.getDeliveryAddress());
          //  }
          //  taxTitle.setText("GST ( " + model.getTaxType() + " : " + model.getTaxValue() + " % ) ");
      //  }
        companyNametext.setText(company_name);
        companyAddress1Text.setText(company_address1);
        companyAddress2Text.setText(company_address2 + "," + company_address3);
        currencyView.setHasFixedSize(true);
        // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        currencyView.setLayoutManager(new LinearLayoutManager(SettlementPrintPreview.this, LinearLayoutManager.VERTICAL, false));
        denominationAdapter = new CurrencyDenominationAdapter(SettlementPrintPreview.this, currencyList, new CurrencyDenominationAdapter.CallBack() {
            @Override
            public void setKeyboard(EditText count) {

            }

            @Override
            public void setCurrencyTotal() {

            }
        });
        currencyView.setAdapter(denominationAdapter);
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
        File filePath = new File(dir, settlementNumber +".pdf");
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
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SettlementPrintPreview.this);
        builder1.setMessage("Do you want to print Settlement ?.");
        builder1.setCancelable(false);
        builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertInterface = dialog;
                if (printerType.equals("TSC Printer")) {
                    dialog.dismiss();
                    try {
                        com.winapp.svcatering.zebraprinter.TSCPrinter printer=new com.winapp.svcatering.zebraprinter.TSCPrinter(SettlementPrintPreview.this,printerMacId,"Settlement");
                        printer.setSettlementPrintSave(1,
                                settlementNumber.toString(),
                                settlementDateText.getText().toString(),
                                locationCodeText.getText().toString(),
                                settlementBy.getText().toString(),
                                currencyList,
                                expenseList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (printerType.equals("Zebra Printer")) {
                    ZebraPrinterActivity zebraPrinterActivity = new ZebraPrinterActivity(SettlementPrintPreview.this, printerMacId);
                    zebraPrinterActivity.printSettlement(1,
                            settlementNumber.toString(),
                            settlementDateText.getText().toString(),
                            locationCodeText.getText().toString(),
                            settlementBy.getText().toString(),
                            currencyList,expenseList
                            );
                } else {
                    try {
                        final TSCPrinterActivity print = new TSCPrinterActivity(SettlementPrintPreview.this, printerMacId, printerType);
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
        textviewTitle.setText("SETTLEMENT");
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