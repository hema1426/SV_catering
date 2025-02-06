package com.winapp.svcatering.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.winapp.svcatering.BuildConfig;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.CartAdapterNew;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;
import com.winapp.svcatering.model.SalesOrderPrintPreviewModel;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.utils.CaptureSignatureView;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.FileCompressor;
import com.winapp.svcatering.utils.ImageUtil;
import com.winapp.svcatering.utils.InternetConnectivity;
import com.winapp.svcatering.utils.LocationTrack;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.SettingUtils;
import com.winapp.svcatering.utils.SharedPreferenceUtil;
import com.winapp.svcatering.utils.Utils;
import com.winapp.svcatering.zebraprinter.TSCPrinter;
import com.winapp.svcatering.zebraprinter.ZebraPrinterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.winapp.svcatering.utils.Utils.fourDecimalPoint;
import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartView;
    private DBHelper dbHelper;
    private CartAdapterNew cartAdapter;
    private LinearLayout emptyLayout;
    private LinearLayout cartLayout;
    private Button checkOut;
    private Button shopNow;
    private SessionManager session;
    private String customerId;
    private FrameLayout checkoutLayout;
    private TextView netAmount;
    private String nettotal;
    private ArrayList<CartModel> localCart;
    private Menu menu1;
    private TextView subTotalText;
    private TextView taxText;
    private TextView netTotalText;
    private ArrayList<CustomerDetails> customerDetails;
    private ImageView removeAll;
    private TextView totalItems;
    private TextView taxTypeText;

    private SweetAlertDialog pDialog;
    private JSONObject customerResponse = new JSONObject();
    private String companyCode;
    String percentApi = "";

    private String subTotalValue;
    private String netTotalvalue;
    private String netTax;
    private String userName;
    private String locationCode;
    private String currentDate;
    private String printerMacId;
    private static String printerType;
    private SharedPreferences sharedPreferences;
    private ArrayList<SalesOrderPrintPreviewModel> salesOrderHeaderDetails;
    private ArrayList<SalesOrderPrintPreviewModel.SalesList> salesOrderList;
    AlertDialog alert11;
    DialogInterface alertInterface;
    boolean isPrintCheck = false;
    public static View invoicePrintOption;
    public static boolean isShowMore = false;
    public static LinearLayout transLayout;
    public static CheckBox cashCollected;
    public static CheckBox invoicePrint;
    public static CheckBox deliveryPrint;
    public static CheckBox emailInvoice;
    public static LinearLayout copyLayout;
    public static boolean isPrintEnable = false;
    public ImageView copyMinus;
    public ImageView copyPlus;
    public Button yesButton;
    public Button noButton;
    public TextView copyText;
    public static String saveAction = "SalesOrder";
    public static int noofCopyPrint = 0;
    static BottomSheetBehavior behavior;
    public static ProgressDialog printAlertDialog;
    private static ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails;
    private static ArrayList<InvoicePrintPreviewModel.InvoiceList> invoicePrintList;
    public static View summaryLayout;
    public ImageView showMore;
    public ImageView closeShowmore;
    public TextView subTotalTextValue;
    public TextView itemDiscountText;
    public EditText billDiscAmount;
    public EditText billDiscPercentage;
    public TextWatcher billDiscAmountTextWatcher;
    public TextWatcher billDiscPercentageTextWatcher;
    public static String itemDiscountAmount = "0.00";
    public static String billDiscountPercentage;
    public static String billDiscountAmount = "0.00";
    public ImageView signatureCapture;
    public CaptureSignatureView captureSignatureView;
    public AlertDialog alert;
    public Double totalVal = 0.00;
    public TextView signatureTitle;
    // These are the variables for the Getting locations
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    TextView locationText;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 2;
    File mPhotoFile;
    FileCompressor mCompressor;
    TextView selectImage;
    boolean isAllowLowStock = false;
    double currentLocationLatitude = 0.0;
    double currentLocationLongitude = 0.0;
    public static String signatureString = "";
    public static String imageString = "";
    public static HashMap<String, String> user;
    String selectCustomerId;
    public boolean isBillDiscountTouch = false;
    public static String netSubtottalValue;
    public static String netTotalValue;
    public static String totalValue;
    public static String netTaxvalue;
    private LinearLayout signatureLayout;
    private LinearLayout attachmentLayout;
    private TextView taxTitle;
    public static String currentDateStringSO;
    private String currentSaveDateTime = "";

    public static String currentDateSO;
    public static String companyName;
    public static boolean isDeliveryPrint = false;
    public static boolean isInvoicePrint = false;
    private JSONObject customerObject;
    public static String current_latitude = "0.00";
    public static String current_longitude = "0.00";
    public static String current_addr = "";
    public String currentDateString;
    private SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        dbHelper = new DBHelper(this);
        session = new SessionManager(this);
        mCompressor = new FileCompressor(this);
        user = session.getUserDetails();
        sharedPreferenceUtil = new SharedPreferenceUtil(this);

        percentApi = sharedPreferenceUtil.getStringPreference(sharedPreferenceUtil.KEY_CART_ITEM_DISC, "");
        Log.w("percentApi..", "" + percentApi);
        signatureString = "";

        companyCode = user.get(SessionManager.KEY_COMPANY_CODE);
        userName = user.get(SessionManager.KEY_USER_NAME);
        companyName = user.get(SessionManager.KEY_COMPANY_NAME);
        locationCode = user.get(SessionManager.KEY_LOCATION_CODE);
        // recyclerview
        subTotalTextValue = findViewById(R.id.sub_total_text);
        itemDiscountText = findViewById(R.id.item_disc);
        billDiscAmount = findViewById(R.id.bill_disc_amount);
        billDiscPercentage = findViewById(R.id.bill_disc_percentage);
        cartView = findViewById(R.id.cartView);
        emptyLayout = findViewById(R.id.empty_layout);
        cartLayout = findViewById(R.id.cart_layout);
        shopNow = findViewById(R.id.shop_now);
        checkOut = findViewById(R.id.check_out);
        checkoutLayout = findViewById(R.id.check_out_layout);
        netAmount = findViewById(R.id.net_amount);
        subTotalText = findViewById(R.id.balance_value);
        netTotalText = findViewById(R.id.net_total);
        taxText = findViewById(R.id.tax);
        removeAll = findViewById(R.id.remove_all);
        totalItems = findViewById(R.id.total_items);
        taxTypeText = findViewById(R.id.tax_type);
        invoicePrintOption = findViewById(R.id.invoice_save_option);
        transLayout = findViewById(R.id.trans_layout);
        cashCollected = findViewById(R.id.cash_collected);
        invoicePrint = findViewById(R.id.invoice_print);
        deliveryPrint = findViewById(R.id.delivery_print);
        copyLayout = findViewById(R.id.copy_layout);
        copyMinus = findViewById(R.id.copy_minus);
        copyPlus = findViewById(R.id.copy_plus);
        yesButton = findViewById(R.id.buttonYes);
        noButton = findViewById(R.id.buttonNo);
        copyText = findViewById(R.id.copy);
        emailInvoice = findViewById(R.id.email_invoice);
        summaryLayout = findViewById(R.id.summary_layout);
        showMore = findViewById(R.id.show_more);
        closeShowmore = findViewById(R.id.close);
        signatureCapture = findViewById(R.id.signature_capture);
        captureSignatureView = findViewById(R.id.signature);
        signatureTitle = findViewById(R.id.signature_title);
        locationText = findViewById(R.id.locationText);
        selectImage = findViewById(R.id.select_image);
        signatureLayout = findViewById(R.id.signature_layout);
        attachmentLayout = findViewById(R.id.attachement_layout);
        taxTitle = findViewById(R.id.tax_title);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType = sharedPreferences.getString("printer_type", "");
        printerMacId = sharedPreferences.getString("mac_address", "");

        sharedPreferences = getSharedPreferences("customerPref", MODE_PRIVATE);
        selectCustomerId = sharedPreferences.getString("customerId", "");
        if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
            //  customerDetails=dbHelper.getCustomer(selectCustomerId);
            try {
                getCustomerDetails(selectCustomerId, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // getPermission();

        Log.w("Printer_Mac_Id:", printerMacId);
        Log.w("Printer_Type:", printerType);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cart");

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);


        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDateString = df.format(c);

        SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = df1.format(c);


        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDateStringSO = df2.format(c);

        SimpleDateFormat df3 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDateSO = df3.format(c);

      /*  if (customerDetails!=null && customerDetails.size()>0){
            customerId=customerDetails.get(0).getCustomerCode();
            try {
                getCustomerDetails(customerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
*/
        ArrayList<SettingsModel> settings = dbHelper.getSettings();
        if (settings != null) {
            if (settings.size() > 0) {
                for (SettingsModel model : settings) {
                    if (model.getSettingName().equals("invoice_switch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            signatureLayout.setVisibility(View.VISIBLE);
                            attachmentLayout.setVisibility(View.VISIBLE);
                        } else {
                            signatureLayout.setVisibility(View.GONE);
                            attachmentLayout.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }

        if (mPhotoFile != null && mPhotoFile.length() > 0) {
            selectImage.setText("View Image");
            selectImage.setTag("view_image");
        } else {
            selectImage.setText("Select Image");
            selectImage.setTag("select_image");
        }

        if (InternetConnectivity.isConnected(this)) {
            getLocalData();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet found", Toast.LENGTH_SHORT).show();
        }

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // proceedCheckout();
                // showRemoveAlert("Checkout");
                String text = netTotalText.getText().toString();

                if (totalVal > 0.0) {
                    ArrayList<SettingsModel> settings = dbHelper.getSettings();
                    if (settings != null) {
                        if (settings.size() > 0) {
                            for (SettingsModel model : settings) {
                                if (model.getSettingName().equals("invoice_switch")) {
                                    Log.w("SettingName:", model.getSettingName());
                                    Log.w("SettingValue:", model.getSettingValue());
                                    if (model.getSettingValue().equals("1")) {
                                        saveAction = "Invoice";
                                        //  createInvoiceJson(Integer.parseInt(noofCopy.getText().toString()));
                                        Log.w("SavingAction1:", saveAction);
                                        showSaveOption(saveAction);
                                    } else {
                                        saveAction = "SalesOrder";
                                        //  createAndValidateJsonObject(Integer.parseInt(noofCopy.getText().toString()));
                                        Log.w("SavingAction2:", saveAction);
                                        showSaveOption(saveAction);
                                    }
                                    break;
                                }
                            }
                        } else {
                            saveAction = "SalesOrder";
                            // createAndValidateJsonObject(Integer.parseInt(noofCopy.getText().toString()));
                            Log.w("SavingAction4:", saveAction);
                            showSaveOption(saveAction);
                        }
                    } else {
                        saveAction = "SalesOrder";
                        // createAndValidateJsonObject(Integer.parseInt(noofCopy.getText().toString()));
                        Log.w("SavingAction4:", saveAction);
                        showSaveOption(saveAction);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), " Nettotal should not be negative", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // define the check out button listener
      /*  checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // proceedCheckout();
               // showRemoveAlert("Checkout");
                ArrayList<SettingsModel> settings=dbHelper.getSettings();
                if (settings.size()>0){
                    for (SettingsModel model:settings){
                        if (model.getSettingName().equals("invoice_switch")){
                            if (model.getSettingValue().equals("1")){
                                showAlert("Invoice");
                            }else {
                                showAlert("SalesOrder");
                            }
                        }else {
                            showAlert("SalesOrder");
                        }
                    }
                }else {
                    showAlert("SalesOrder");
                }
            }
        });*/

        //  if (action.equals("Invoice")){
        //  createInvoiceJson(Integer.parseInt(noofCopy.getText().toString()));
        // }else {
        // createAndValidateJsonObject(Integer.parseInt(noofCopy.getText().toString()));
        //  }

        shopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CategoriesActivity.class);
                startActivity(intent);
                finish();
            }
        });

        removeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveAlert("RemoveAll");
            }
        });

        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        transLayout.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        transLayout.setVisibility(View.GONE);
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);

            }
        });

        invoicePrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try {
                    if (validatePrinterConfiguration()) {
                        if (b) {
                            isPrintEnable = true;
                            isInvoicePrint = true;
                            copyLayout.setVisibility(View.VISIBLE);
                            if (saveAction.equals("SalesOrder") || saveAction.equals("SalesEdit")) {
                                emailInvoice.setVisibility(View.GONE);
                            } else {
                                emailInvoice.setVisibility(View.VISIBLE);
                            }
                        } else {
                            isPrintEnable = false;
                            isInvoicePrint = false;
                            copyText.setText("1");
                            copyLayout.setVisibility(View.GONE);
                            emailInvoice.setVisibility(View.GONE);
                        }
                    } else {
                        invoicePrint.setChecked(false);
                        copyLayout.setVisibility(View.GONE);
                        isPrintEnable = false;
                        isInvoicePrint = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        deliveryPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try {
                    if (validatePrinterConfiguration()) {
                        if (b) {
                            isPrintEnable = true;
                            isDeliveryPrint = true;
                            copyLayout.setVisibility(View.VISIBLE);
                            if (saveAction.equals("SalesOrder") || saveAction.equals("SalesEdit")) {
                                emailInvoice.setVisibility(View.GONE);
                            } else {
                                emailInvoice.setVisibility(View.VISIBLE);
                            }
                        } else {
                            isPrintEnable = false;
                            isDeliveryPrint = false;
                            copyText.setText("1");
                            copyLayout.setVisibility(View.GONE);
                            emailInvoice.setVisibility(View.GONE);
                        }
                    } else {
                        deliveryPrint.setChecked(false);
                        copyLayout.setVisibility(View.GONE);
                        isPrintEnable = false;
                        isDeliveryPrint = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        copyPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(copyText.getText().toString());
                value++;
                copyText.setText(String.valueOf(value));
            }
        });

        copyMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(copyText.getText().toString());
                if (value != 1) {
                    value--;
                    copyText.setText(String.valueOf(value));
                }
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSheet();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noofCopyPrint = Integer.parseInt(copyText.getText().toString());
                if (saveAction.equals("SalesOrder") || saveAction.equals("SalesEdit")) {
                    createAndValidateJsonObject(noofCopyPrint);
                } else {
                    createInvoiceJson(noofCopyPrint);
                }
                closeSheet();
            }
        });

        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invoicePrintOption.setVisibility(View.GONE);
                summaryLayout.setVisibility(View.VISIBLE);
                viewCloseBottomSheet();
            }
        });

        closeShowmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSheet();
                hideKeybaord(view);
            }
        });

        billDiscAmount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isBillDiscountTouch = true;
                return false;
            }
        });


        billDiscPercentage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isBillDiscountTouch = true;
                return false;
            }
        });

        billDiscAmountTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                double subtotal = getSubtotal();
                if (isBillDiscountTouch) {
                    if (!editable.toString().isEmpty()) {
                        if (editable.toString().equals(".")) {
                            billDiscAmount.setText("0.");
                            billDiscAmount.setSelection(billDiscAmount.getText().length());
                            setPercentageAmount(Double.parseDouble("0"));
                            double net_subtotal = subtotal - 0;
                            netTotalCalculation(net_subtotal);
                        } else {
                            setPercentageAmount(Double.parseDouble(editable.toString()));
                            double net_subtotal = subtotal - Double.parseDouble(editable.toString());
                            netTotalCalculation(net_subtotal);
                        }
                    } else {
                        setPercentageAmount(0);
                        netTotalCalculation(subtotal);
                    }
                }
            }
        };
        billDiscAmount.addTextChangedListener(billDiscAmountTextWatcher);

        billDiscPercentageTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                double subtotal = getSubtotal();
                if (isBillDiscountTouch) {
                    if (!editable.toString().isEmpty()) {
                        if (editable.toString().equals(".")) {
                            billDiscPercentage.setText("0.");
                            billDiscPercentage.setSelection(billDiscPercentage.getText().length());
                            double amount = percentageToAmount(Double.parseDouble("0"));
                            billDiscAmount.removeTextChangedListener(billDiscAmountTextWatcher);
                            billDiscAmount.setText(twoDecimalPoint(amount) + "");
                            billDiscAmount.addTextChangedListener(billDiscAmountTextWatcher);
                            double net_subtotal = subtotal - Double.parseDouble(billDiscAmount.getText().toString());
                            netTotalCalculation(net_subtotal);
                        } else {
                            double amount = percentageToAmount(Double.parseDouble(editable.toString()));
                            Log.w("PercentageAmount:", amount + "");
                            billDiscAmount.removeTextChangedListener(billDiscAmountTextWatcher);
                            billDiscAmount.setText(twoDecimalPoint(amount) + "");
                            billDiscAmount.addTextChangedListener(billDiscAmountTextWatcher);
                            double net_subtotal = subtotal - Double.parseDouble(billDiscAmount.getText().toString());
                            netTotalCalculation(net_subtotal);
                        }
                    } else {
                        billDiscAmount.removeTextChangedListener(billDiscAmountTextWatcher);
                        billDiscAmount.setText("");
                        billDiscAmount.addTextChangedListener(billDiscAmountTextWatcher);
                        netTotalCalculation(subtotal);
                    }
                }
            }
        };
        billDiscPercentage.addTextChangedListener(billDiscPercentageTextWatcher);

        signatureTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignatureAlert();
            }
        });

        signatureCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignatureAlert();
            }
        });
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectImage.getTag().equals("view_image")) {
                    showImage();
                } else {
                    selectImage();
                }
            }
        });
    }

    /**
     * Alert dialog for capture or select from galley
     */
    private void selectImage() {
        final CharSequence[] items = {
                "Take Photo",
                /* "Choose from Library",*/
                "Cancel"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                requestStoragePermission(true);
            } //else if (items[item].equals("Choose from Library")) {
            // requestStoragePermission(false);
            // }
            else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Select image fro gallery
     */
    private void dispatchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);

                    imageString = ImageUtil.getBase64StringImage(mPhotoFile);
                    //  Log.w("GivenImage1:",imageString);
                    Utils.w("GivenImage1", imageString);
                    showImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }


               /* Glide.with(MainActivity.this)
                        .load(mPhotoFile)
                        .apply(new RequestOptions().centerCrop()
                                .circleCrop()
                                .placeholder(R.drawable.profile_pic_place_holder))
                        .into(imageViewProfilePic);*/

            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
                    selectImage.setText(selectedImage.toString());

                    imageString = ImageUtil.getBase64StringImage(mPhotoFile);
                    // Log.w("GivenImage2:",imageString);
                    Utils.w("GivenImage2", imageString);

                } catch (IOException e) {
                    e.printStackTrace();
                }
              /*
                Glide.with(MainActivity.this)
                        .load(mPhotoFile)
                        .apply(new RequestOptions().centerCrop()
                                .circleCrop()
                                .placeholder(R.drawable.profile_pic_place_holder))
                        .into(imageViewProfilePic);*/
            }
        }
    }


    public void showImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.image_view_layout, null);
        ImageView imageView = dialogView.findViewById(R.id.invoice_image);
        Glide.with(this)
                .load(mPhotoFile)
                .error(R.drawable.no_image_found)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(imageView);
        builder.setCancelable(false);
        builder.setTitle("Invoice Image");
        builder.setView(dialogView);
        builder.setNeutralButton("NEW IMAGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectImage();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectImage.setTag("view_image");
                selectImage.setText("View Image");
                dialog.dismiss();
            }
        }).create().show();

    }

    /**
     * Requesting multiple permissions (storage and camera) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission(boolean isCamera) {
        String[] permission = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA};
        }

        Dexter.withContext(this)
                .withPermissions(permission)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent();
                            } else {
                                dispatchGalleryIntent();
                            }
                        }

                        for(int i = 0 ; i < report.getDeniedPermissionResponses().size();i++){
                            Log.d("cg_perm",report.getDeniedPermissionResponses()
                                    .get(i).getPermissionName());
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                                   PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(
                        error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT)
                                .show())
                .onSameThread()
                .check();


}

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage(
                "This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /**
     * Create file with current timestamp name
     *
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }

    /**
     * Get real file path from URI
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void getPermission() {
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
    }

    public void getCurrentLocation() {
        locationTrack = new LocationTrack(CartActivity.this);
        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            current_latitude = String.valueOf(latitude);
            current_longitude = String.valueOf(longitude);
            String currentAddress = Utils.getCompleteAddress(CartActivity.this, latitude, longitude);
            if (currentAddress != null && !currentAddress.isEmpty()) {
                locationText.setText(currentAddress);
                current_addr = currentAddress ;
            }
        } else {
            // locationTrack.showSettingsAlert();
        }
    }

    public void setCurrentLocation(double latitude, double longitude) {
        this.currentLocationLatitude = latitude;
        this.currentLocationLongitude = longitude;
        String currentAddress = Utils.getCompleteAddress(CartActivity.this, latitude, longitude);
        if (currentAddress != null && !currentAddress.isEmpty()) {
            locationText.setText(currentAddress);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  getCurrentLocation();
    }

    public void showSignatureAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.signature_layout, null);
        alertDialog.setView(customLayout);
        final Button acceptButton = customLayout.findViewById(R.id.buttonYes);
        final Button cancelButton = customLayout.findViewById(R.id.buttonNo);
        final Button clearButton = customLayout.findViewById(R.id.buttonClear);
        LinearLayout mContent = customLayout.findViewById(R.id.signature_layout);
        acceptButton.setEnabled(false);
        acceptButton.setAlpha(0.4f);
        CaptureSignatureView mSig = new CaptureSignatureView(CartActivity.this, null, new CaptureSignatureView.OnSignatureDraw() {
            @Override
            public void onSignatureCreated() {
                acceptButton.setEnabled(true);
                acceptButton.setAlpha(1f);
            }
        });
        mContent.addView(mSig, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // byte[] signature = captureSignatureView.getBytes();
                Bitmap signature = mSig.getBitmap();
                signatureCapture.setImageBitmap(signature);
                signatureString = ImageUtil.convertBimaptoBase64(signature);
                Log.w("ImageSignature:", signatureString);
                alert.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSig.ClearCanvas();
            }
        });
        alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }


    /**
     * @param percentage to amount
     *                   send the net percentage to calculate the exact amount
     */
    public double percentageToAmount(double percentage) {
        return (getSubtotal() / 100.0f) * percentage;
    }

    public double calculatePercentage(double obtained, double total) {
        return obtained * 100 / total;
    }

    public void setPercentageAmount(double value) {
        double per = calculatePercentage(value, getSubtotal());
        Log.w("PercentageTxt:", per + "");
        //billDiscAmount.removeTextChangedListener(billDiscAmountTextWatcher);
        if (per == 0.0) {
            billDiscPercentage.removeTextChangedListener(billDiscPercentageTextWatcher);
            billDiscPercentage.setText("");
            billDiscPercentage.addTextChangedListener(billDiscPercentageTextWatcher);
        } else {
            billDiscPercentage.removeTextChangedListener(billDiscPercentageTextWatcher);
            billDiscPercentage.setText(twoDecimalPoint(per) + "");
            billDiscPercentage.addTextChangedListener(billDiscPercentageTextWatcher);
        }
    }
    private void hideKeybaord(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }
    public double getSubtotal() {
        double subtotal = 0;
        double tax = 0;
        double net_total_value = 0;
        double net_total = 0;
        localCart = dbHelper.getAllCartItems();
        for (int j = 0; j < localCart.size(); j++) {
            // if (localCart.get(j).getCART_TOTAL_VALUE()!=null && !localCart.get(j).getCART_TOTAL_VALUE().equals("null")){
            //   subtotal += Double.parseDouble(localCart.get(j).getCART_TOTAL_VALUE());
            // }
            if (localCart.get(j).getCART_TAX_VALUE() != null && !localCart.get(j).getCART_TAX_VALUE().equals("null")) {
                tax += Double.parseDouble(localCart.get(j).getCART_TAX_VALUE());
            }
            if (localCart.get(j).getCART_COLUMN_NET_PRICE() != null && !localCart.get(j).getCART_COLUMN_NET_PRICE().equals("null")) {
                net_total += Double.parseDouble(localCart.get(j).getCART_COLUMN_NET_PRICE());
            }

            if (localCart.get(j).getSubTotal() != null && !localCart.get(j).getSubTotal().equals("null")) {
                subtotal += Double.parseDouble(localCart.get(j).getSubTotal());
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences("customerPref", MODE_PRIVATE);
        selectCustomerId = sharedPreferences.getString("customerId", "");
        customerDetails = dbHelper.getCustomer(selectCustomerId);
        if (customerDetails.get(0).getTaxType().equals("I")) {
            net_total_value = net_total;
        } else {
            net_total_value = subtotal;
        }

        return net_total_value;
    }

    public void netTotalCalculation(double total) {
        try {
            double taxAmount = 0.0, netTotal = 0.0;
            double taxAmount1 = 0.0, netTotal1 = 0.0;
            double return_qty = 0.0;
            double pcspercarton = 0.0;
            customerDetails = dbHelper.getCustomer(selectCustomerId);
            String taxVal = customerDetails.get(0).getTaxPerc();
            String taxType = customerDetails.get(0).getTaxType();
            Log.w("TaxType:", taxType);
            Log.w("TaxValue:", taxVal);

            if (!billDiscPercentage.getText().toString().isEmpty()) {
                billDiscountPercentage = billDiscPercentage.getText().toString();
            } else {
                billDiscountPercentage = "0";
            }

            if (billDiscAmount.getText() != null && !billDiscAmount.getText().toString().isEmpty()) {
                billDiscountAmount = billDiscAmount.getText().toString();
            } else {
                billDiscountAmount = "0.00";
            }

            double tt = total;
            String Prodtotal = twoDecimalPoint(tt);

            double subtotalValue = 0.0;

            String itemDisc = itemDiscountText.getText().toString();
            if (!itemDisc.matches("")) {
                double itmDisc = Double.parseDouble(itemDisc);
                subtotalValue = tt - itmDisc;
            } else {
                subtotalValue = tt;
            }


            String sbTtl = twoDecimalPoint(subtotalValue);
            subTotalTextValue.setText("$ " + Utils.twoDecimalPoint(subtotalValue));
            subTotalText.setText(Utils.twoDecimalPoint(subtotalValue));
            subTotalValue = Utils.twoDecimalPoint(subtotalValue);
            // sl_total_inclusive.setText("" + sbTtl);
            tt = subtotalValue;

            if (!taxType.matches("") && !taxVal.matches("")) {

                double taxValueCalc = Double.parseDouble(taxVal);

                if (taxType.matches("E")) {

                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subtotalValue * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount1);
                        taxText.setText("" + prodTax);

                        netTotal1 = subtotalValue + taxAmount1;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        netTotalText.setText("" + ProdNetTotal);
                        totalVal = Double.valueOf((ProdNetTotal));

                        netTax = prodTax;
                        netTotalvalue = ProdNetTotal;
                    } else {

                        taxAmount = (tt * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);
                        taxText.setText("" + prodTax);

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        netTotalText.setText("" + ProdNetTotal);
                        totalVal = Double.valueOf((ProdNetTotal));

                        netTax = prodTax;
                        netTotalvalue = ProdNetTotal;
                    }

                } else if (taxType.matches("I")) {
                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subtotalValue * taxValueCalc)
                                / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount1);
                        taxText.setText("" + prodTax);

                        // netTotal1 = subTotal + taxAmount1;
                        netTotal1 = subtotalValue;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        netTotalText.setText("" + ProdNetTotal);
                        totalVal = Double.valueOf((ProdNetTotal));

                        double dTotalIncl = netTotal1 - taxAmount1;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        //  sl_total_inclusive.setText(totalIncl);

                        netTax = prodTax;
                        netTotalvalue = ProdNetTotal;
                    } else {
                        taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        taxText.setText("" + prodTax);

                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        netTotalText.setText("" + ProdNetTotal);
                        totalVal = Double.valueOf((ProdNetTotal));

                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        //  sl_total_inclusive.setText(totalIncl);

                        netTax = prodTax;
                        netTotalvalue = ProdNetTotal;
                    }

                } else if (taxType.matches("Z")) {

                    taxText.setText("0.0");
                    if (!itemDisc.matches("")) {
                        // netTotal1 = subTotal + taxAmount;
                        netTotal1 = subtotalValue;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        netTotalText.setText("" + ProdNetTotal);
                        totalVal = Double.valueOf((ProdNetTotal));

                        netTax = "0.0";
                        netTotalvalue = ProdNetTotal;
                    } else {
                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        netTotalText.setText("" + ProdNetTotal);
                        totalVal = Double.valueOf((ProdNetTotal));

                        netTax = "0.0";
                        netTotalvalue = ProdNetTotal;
                    }

                } else {
                    taxText.setText("0.0");
                    netTotalText.setText("" + Prodtotal);
                    totalVal = Double.valueOf((Prodtotal));
                    netTax = "0.0";
                    netTotalvalue = Prodtotal;
                }

            } else if (taxVal.matches("")) {
                taxText.setText("0.0");
                netTotalText.setText("" + Prodtotal);
                totalVal = Double.valueOf((Prodtotal));
                netTax = "0.0";
                netTotalvalue = Prodtotal;
            } else {
                taxText.setText("0.0");
                netTotalText.setText("" + Prodtotal);
                totalVal = Double.valueOf((Prodtotal));
                netTax = "0.0";
                netTotalvalue = Prodtotal;
            }

            if (subtotalValue < 0) {
                billDiscAmount.setText("");
                billDiscPercentage.setText("");
                billDiscPercentage.clearFocus();
                billDiscAmount.clearFocus();
            } else {
                Utils.refreshActionBarMenu(this);
            }
        } catch (Exception e) {

        }
    }

    public void closeSheet() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public static void showSaveOption(String action) {
        if (action.equals("SalesOrder")) {
            cashCollected.setVisibility(View.GONE);
            deliveryPrint.setVisibility(View.GONE);
            invoicePrint.setVisibility(View.VISIBLE);
            invoicePrint.setText("Sales Order Print");
        } else {
            cashCollected.setVisibility(View.VISIBLE);
            deliveryPrint.setVisibility(View.VISIBLE);
            invoicePrint.setVisibility(View.VISIBLE);
            invoicePrint.setText("Invoice Print");
        }
        invoicePrintOption.setVisibility(View.VISIBLE);
        summaryLayout.setVisibility(View.GONE);
        viewCloseBottomSheet();
        saveAction = action;
    }

    public static void viewCloseBottomSheet() {
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void showAlert(String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.save_option_dialog, null);
        builder.setCancelable(false);
        final CheckBox checkPrint = dialoglayout.findViewById(R.id.save_print);
        final TextView titleText = dialoglayout.findViewById(R.id.title);
        final LinearLayout copyLayout = dialoglayout.findViewById(R.id.copy_layout);
        final TextView noofCopy = dialoglayout.findViewById(R.id.copy);
        final ImageView copyMinus = dialoglayout.findViewById(R.id.copy_minus);
        final ImageView copyPlus = dialoglayout.findViewById(R.id.copy_plus);
        if (action.equals("Invoice")) {
            titleText.setText("Save and Print Invoice");
            builder.setTitle("Save Invoice Option");
        } else {
            titleText.setText("Save and Print Sales Order");
            builder.setTitle("Save Sales Order Option");
        }
        // checkPrint.setEnabled(false);
        checkPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    copyLayout.setVisibility(View.VISIBLE);
                    isPrintCheck = true;
                } else {
                    isPrintCheck = false;
                    noofCopy.setText("1");
                    copyLayout.setVisibility(View.GONE);
                }
            }
        });

        copyMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int val = Integer.parseInt(noofCopy.getText().toString());
                if (!noofCopy.getText().toString().equals("1")) {
                    val--;
                    noofCopy.setText(String.valueOf(val));
                }
            }
        });

        copyPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int val = Integer.parseInt(noofCopy.getText().toString());
                val++;
                noofCopy.setText(String.valueOf(val));
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (action.equals("Invoice")) {
                    createInvoiceJson(Integer.parseInt(noofCopy.getText().toString()));
                } else {
                    createAndValidateJsonObject(Integer.parseInt(noofCopy.getText().toString()));
                }
            }
        });
        builder.setView(dialoglayout);
        builder.show();
    }

    private void getLocalData() {

        int count = dbHelper.numberOfRows();
        if (count > 0) {
            cartLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            localCart = new ArrayList<>();
            localCart = dbHelper.getAllCartItems();
            if (localCart.size() == 0) {
                //  menu1.findItem(R.id.action_remove).setVisible(false);
                totalItems.setText("( " + 0 + " )");
            } else {
                totalItems.setText("( " + localCart.size() + " )");
            }
            cartAdapter = new CartAdapterNew(this, localCart, new CartAdapterNew.CallBack() {
                @Override
                public void updateNetAmount(String action) {
                    if (action.equals("net_amount_update")) {
                        totalProductPrice();
                    } else {
                        getLocalData();
                    }
                }
            });
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            cartView.setLayoutManager(mLayoutManager);
            cartView.setItemAnimator(new DefaultItemAnimator());
            cartView.setAdapter(cartAdapter);
            checkoutLayout.setVisibility(View.VISIBLE);
            totalProductPrice();
            invalidateOptionsMenu();
            getCurrentLocation();
        } else {
            checkoutLayout.setVisibility(View.GONE);
            cartLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
    }

    public void totalProductPrice() {
        try {
            localCart = dbHelper.getAllCartItems();
            double sub_total = 0.0;
            double net_tax = 0.0;
            double total_value = 0.0;
            double net_total = 0.0;
            double net_item_discount = 0.0;
            for (int j = 0; j < localCart.size(); j++) {
                if (localCart.get(j).getCART_TOTAL_VALUE() != null && !localCart.get(j).getCART_TOTAL_VALUE().equals("null")) {
                    total_value += Double.parseDouble(localCart.get(j).getCART_TOTAL_VALUE());
                }

                if (localCart.get(j).getSubTotal() != null && !localCart.get(j).getSubTotal().equals("null")) {
                    sub_total += Double.parseDouble(localCart.get(j).getSubTotal());
                }
                if (localCart.get(j).getCART_TAX_VALUE() != null && !localCart.get(j).getCART_TAX_VALUE().equals("null")) {
                    net_tax += Double.parseDouble(localCart.get(j).getCART_TAX_VALUE());
                }

                if (localCart.get(j).getCART_COLUMN_NET_PRICE() != null && !localCart.get(j).getCART_COLUMN_NET_PRICE().equals("null")) {
                    net_total += Double.parseDouble(localCart.get(j).getCART_COLUMN_NET_PRICE());
                }

                if (localCart.get(j).getDiscount() != null && !localCart.get(j).getDiscount().equals("null") && !localCart.get(j).getDiscount().isEmpty()) {
                    net_item_discount += Double.parseDouble(localCart.get(j).getDiscount());
                }
            }

            SharedPreferences sharedPreferences = getSharedPreferences("customerPref", MODE_PRIVATE);
            selectCustomerId = sharedPreferences.getString("customerId", "");
            customerDetails = dbHelper.getCustomer(selectCustomerId);

            if (customerDetails.get(0).getTaxType().equals("I")) {
                taxTitle.setText("GST ( Inc )");
            } else if (customerDetails.get(0).getTaxType().equals("E")) {
                taxTitle.setText("GST ( Exc )");
            } else {
                taxTitle.setText("GST ( Zero )");
            }

            if (customerDetails.get(0).getTaxType().equals("I")) {

                taxText.setText(fourDecimalPoint(net_tax));
                netTotalText.setText("$ " + twoDecimalPoint(net_total));
                totalVal = Double.valueOf(twoDecimalPoint(net_total));

                subTotalTextValue.setText("$ " + twoDecimalPoint(sub_total));
                itemDiscountText.setText(twoDecimalPoint(net_item_discount));

                double sub_total1 = net_total - net_tax;
                subTotalTextValue.setText(Utils.twoDecimalPoint(sub_total));
                subTotalText.setText("$ " + twoDecimalPoint(sub_total1));
                double sub_total12 = sub_total1 + net_tax;

                subTotalValue = String.valueOf(sub_total12);
                netTax = fourDecimalPoint(net_tax);
                itemDiscountAmount = twoDecimalPoint(net_item_discount);
                totalValue = Utils.twoDecimalPoint(total_value);
                netTotalvalue = twoDecimalPoint(net_total);

            } else {

                subTotalText.setText("$ " + twoDecimalPoint(sub_total));
                taxText.setText(fourDecimalPoint(net_tax));
                netTotalText.setText("$ " + twoDecimalPoint(net_total));
                totalVal = Double.valueOf(twoDecimalPoint(net_total));
                totalVal = net_total;
                subTotalTextValue.setText("$ " + twoDecimalPoint(sub_total));
                itemDiscountText.setText(twoDecimalPoint(net_item_discount));

                totalValue = Utils.twoDecimalPoint(total_value);
                subTotalValue = String.valueOf(sub_total);
                netTax = fourDecimalPoint(net_tax);
                netTotalvalue = twoDecimalPoint(net_total);
                itemDiscountAmount = twoDecimalPoint(net_item_discount);

            }

        } catch (Exception ex) {
            Log.e("Exp:UpdatingTotal", Objects.requireNonNull(ex.getMessage()));
        }
    }

    public void taxCaluclation(double subTotal) {
        try {
            String taxValue = customerDetails.get(0).getTaxPerc();
            String taxType = customerDetails.get(0).getTaxType();
            String itemDisc = "";
            double taxAmount = 0.0;
            double netTotal = 0.0;
            Log.w("TaxType:", taxType);
            Log.w("TaxValue:", taxValue);
            if (!taxType.matches("") && !taxValue.matches("")) {

                double taxValueCalc = Double.parseDouble(taxValue);

                if (taxType.matches("E")) {

                    if (!itemDisc.matches("")) {
                        taxAmount = (subTotal * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);

                        netTotal = subTotal + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);

                        taxText.setText("$ " + prodTax);
                        netTotalText.setText("$ " + ProdNetTotal);
                        totalVal = Double.valueOf(ProdNetTotal);
                        netAmount.setText("$ " + ProdNetTotal);
                        taxTypeText.setText("( Exclusive )");

                        netTax = prodTax;
                        netTotalvalue = ProdNetTotal;

                    } else {

                        taxAmount = (subTotal * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);

                        netTotal = subTotal + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);

                        taxText.setText("$ " + prodTax);
                        netTotalText.setText("$ " + ProdNetTotal);
                        totalVal = Double.valueOf(ProdNetTotal);

                        netAmount.setText("$ " + ProdNetTotal);
                        taxTypeText.setText("( Exclusive )");
                        netTax = prodTax;
                        netTotalvalue = ProdNetTotal;
                    }

                } else if (taxType.matches("I")) {
                    if (!itemDisc.matches("")) {
                        taxAmount = (subTotal * taxValueCalc)
                                / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        netTotal = subTotal + taxAmount;
                        netTotal = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal);

                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);

                        taxText.setText(prodTax);
                        subTotalText.setText("$ " + totalIncl);
                        netTotalText.setText("$ " + ProdNetTotal);
                        totalVal = Double.valueOf(ProdNetTotal);
                        netAmount.setText("$ " + ProdNetTotal);
                        taxTypeText.setText("( Inclusive )");
                        netTax = prodTax;
                        netTotalvalue = ProdNetTotal;
                    } else {
                        taxAmount = (subTotal * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);

                        netTotal = subTotal + taxAmount;
                        netTotal = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal);

                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);

                        subTotalText.setText("$ " + totalIncl);
                        taxText.setText("$ " + prodTax);
                        netTotalText.setText("$ " + ProdNetTotal);
                        totalVal = Double.valueOf(ProdNetTotal);
                        totalVal = Double.valueOf(ProdNetTotal);
                        netAmount.setText("$ " + ProdNetTotal);
                        taxTypeText.setText("( Inclusive )");
                        netTax = prodTax;
                        netTotalvalue = ProdNetTotal;
                    }

                } else if (taxType.matches("Z")) {
                    if (!itemDisc.matches("")) {
                        netTotal = subTotal + taxAmount;
                        netTotal = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal);

                        taxText.setText("0.0");
                        netTotalText.setText("$ " + ProdNetTotal);
                        totalVal = Double.valueOf(ProdNetTotal);
                        netAmount.setText("$ " + ProdNetTotal);
                        netTax = "0.0";
                        netTotalvalue = ProdNetTotal;
                    } else {
                        netTotal = subTotal + taxAmount;
                        netTotal = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal);

                        taxText.setText("0.0");
                        netTotalText.setText("$ " + ProdNetTotal);
                        totalVal = Double.valueOf(ProdNetTotal);
                        netAmount.setText("$ " + ProdNetTotal);
                        netTax = "0.0";
                        netTotalvalue = ProdNetTotal;
                    }

                } else {
                    taxText.setText("0.0");
                    netTotalText.setText("$ " + subTotal);
                    totalVal = subTotal;
                    netAmount.setText("$ " + subTotal);
                    netTax = "0.0";
                    netTotalvalue = String.valueOf(subTotal);
                }

            } else if (taxValue.matches("")) {
                taxText.setText("0.0");
                netTotalText.setText("$ " + subTotal);
                totalVal = subTotal;
                netAmount.setText("$ " + subTotal);
                netTax = "0.0";
                netTotalvalue = String.valueOf(subTotal);
            } else {
                taxText.setText("0.0");
                netTotalText.setText("$ " + subTotal);
                totalVal = subTotal;
                netAmount.setText("$ " + subTotal);
                netTax = "0.0";
                netTotalvalue = String.valueOf(subTotal);
            }

        } catch (Exception ex) {
            Log.e("Exp:updating_total", Objects.requireNonNull(ex.getMessage()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart_menu, menu);
     /*   MenuItem action_delete=menu.getItem(R.id.action_delete);
        int count =dbHelper.numberOfRows();
        if (count>0){
            action_delete.setVisible(true);
        }else {
            action_delete.setVisible(false);
        }*/
        return true;
    }


    public void showRemoveAlert(String action) {
       /* String title;
        if (action.equals("RemoveAll")){
            title="Are you sure want to remove all products ?";
        }else {
            title="Are you sure want to place the orders ?";
        }
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                // .setTitleText("Are you sure?")
                .setContentText(title)
                .setConfirmText("YES")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        dbHelper.removeAllItems();
                        getLocalData();
                        sDialog.dismissWithAnimation();
                    }
                })
                .showCancelButton(true)
                .setCancelText("No")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                }).show();*/

        AlertDialog.Builder builder1 = new AlertDialog.Builder(CartActivity.this);
        builder1.setTitle("Warning..!");
        builder1.setMessage("Are you sure want to remove all products ?");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dbHelper.removeAllItems();
                        getLocalData();
                    }
                });

        builder1.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
            case R.id.action_delete:
                showRemoveAlert("RemoveAll");
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void createInvoiceJson(int copy) {

        JSONObject rootJsonObject = new JSONObject();
        JSONObject invoiceHeader = new JSONObject();
        JSONObject signatureObject = new JSONObject();
        JSONObject invoiceImageObject = new JSONObject();
        JSONArray invoiceDetailsArray = new JSONArray();
        JSONObject invoiceObject = new JSONObject();
        JSONArray returnProductArray = new JSONArray();

        // Sales Header Add values
        ArrayList<CartModel> localCart;
        localCart = dbHelper.getAllCartItems();
        double net_sub_total = 0.0;
        double net_tax = 0.0;
        double net_total = 0.0;
        double net_discount = 0;
        double total_value = 0;
        if (localCart.size() > 0) {
            for (CartModel model : localCart) {
                if (model.getSubTotal() != null && !model.getSubTotal().isEmpty()) {
                    net_sub_total += Double.parseDouble(model.getSubTotal());
                }
                if (model.getCART_TAX_VALUE() != null && !model.getCART_TAX_VALUE().isEmpty()) {
                    net_tax += Double.parseDouble(model.getCART_TAX_VALUE());
                }
                if (model.getCART_COLUMN_NET_PRICE() != null && !model.getCART_COLUMN_NET_PRICE().isEmpty()) {
                    net_total += Double.parseDouble(model.getCART_COLUMN_NET_PRICE());
                }
                if (model.getDiscount() != null && !model.getDiscount().equals("null") && !model.getDiscount().isEmpty()) {
                    net_discount += Double.parseDouble(model.getDiscount());
                }
                if (model.getCART_TOTAL_VALUE() != null && !model.getCART_TOTAL_VALUE().equals("null")) {
                    total_value += Double.parseDouble(model.getCART_TOTAL_VALUE());
                }
            }
        }

        if (customerResponse.optString("TaxType").equals("I")) {

            double sub_total = net_total - net_tax;
            double sub_total1 = sub_total + net_tax;

            netSubtottalValue = Utils.twoDecimalPoint(sub_total1);
            netTaxvalue = Utils.twoDecimalPoint(net_tax);
            netTotalValue = Utils.twoDecimalPoint(sub_total1);
            itemDiscountAmount = Utils.twoDecimalPoint(net_discount);
            totalValue = Utils.twoDecimalPoint(total_value);

        } else {
            netSubtottalValue = Utils.twoDecimalPoint(net_sub_total);
            netTaxvalue = Utils.twoDecimalPoint(net_tax);
            netTotalValue = Utils.twoDecimalPoint(net_total);
            itemDiscountAmount = Utils.twoDecimalPoint(net_discount);
            totalValue = Utils.twoDecimalPoint(total_value);
        }
        JSONArray detailsArray = customerResponse.optJSONArray("responseData");
        JSONObject object = detailsArray.optJSONObject(0);

        try {
            // Sales Header Add values

            rootJsonObject.put("invoiceNumber", "");
            rootJsonObject.put("mode", "I");
            rootJsonObject.put("soNo", "");
            rootJsonObject.put("doNo", "");
            rootJsonObject.put("invoiceDate", currentDate);
            rootJsonObject.put("customerCode", object.get("customerCode"));
            rootJsonObject.put("customerName", object.get("customerName"));
            rootJsonObject.put("address", object.get("address"));
            rootJsonObject.put("street", object.get("street"));
            rootJsonObject.put("city", object.get("city"));
            rootJsonObject.put("creditLimit", object.get("creditLimit"));
            rootJsonObject.put("Remark", "");
            rootJsonObject.put("customerReferenceNo", "");
            rootJsonObject.put("currencyName", "Singapore Dollar");

            rootJsonObject.put("taxTotal", netTaxvalue);
            rootJsonObject.put("subTotal", subTotalValue);
            rootJsonObject.put("total", totalValue);
            rootJsonObject.put("netTotal", netTotalValue);
            rootJsonObject.put("itemDiscount", itemDiscountAmount);
            rootJsonObject.put("billDiscount", billDiscountAmount);
            if (currentSaveDateTime == null || currentSaveDateTime.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                currentSaveDateTime = currentDateandTime;
            }
            rootJsonObject.put("currentDateTime", currentSaveDateTime);

            rootJsonObject.put("totalDiscount", "0");
            rootJsonObject.put("billDiscountPercentage", billDiscountPercentage);
            rootJsonObject.put("deliveryCode", SettingUtils.getDeliveryAddressCode());
            rootJsonObject.put("delCustomerName", "");
            rootJsonObject.put("delAddress1", object.optString("delAddress1"));
            rootJsonObject.put("delAddress2 ", object.optString("delAddress2"));
            rootJsonObject.put("delAddress3 ", object.optString("delAddress3"));
            rootJsonObject.put("delPhoneNo", object.optString("contactNo"));
            rootJsonObject.put("remark", object.optString("remark"));
            rootJsonObject.put("haveTax", object.optString("haveTax"));
            rootJsonObject.put("taxType", object.optString("taxType"));
            rootJsonObject.put("taxPerc", object.optString("taxPercentage"));
            rootJsonObject.put("taxCode", object.optString("taxCode"));
            rootJsonObject.put("currencyCode", object.optString("currencyCode"));
            rootJsonObject.put("currencyValue", "");
            rootJsonObject.put("CurrencyRate", "1");
            rootJsonObject.put("status", "0");
            rootJsonObject.put("postalCode", object.optString("postalCode"));
            rootJsonObject.put("createUser", userName);
            rootJsonObject.put("modifyUser", userName);
            rootJsonObject.put("companyName", companyName);
            rootJsonObject.put("stockUpdated", "1");
            rootJsonObject.put("invoiceType", "M");
            rootJsonObject.put("companyCode", companyCode);
            rootJsonObject.put("locationCode", locationCode);
            rootJsonObject.put("latitude", current_latitude);
            rootJsonObject.put("longitude", current_longitude);
            rootJsonObject.put("CurrentAddress", current_addr);
            rootJsonObject.put("Paymode", "");
            rootJsonObject.put("ChequeDateString", "");
            rootJsonObject.put("BankCode", "");
            rootJsonObject.put("AccountNo", "");
            rootJsonObject.put("ChequeNo", "");
            rootJsonObject.put("image", imageString);
            rootJsonObject.put("signature", signatureString);
            // Sales Details Add to the Objects
            localCart = dbHelper.getAllCartItems();

            int index = 1;
            for (CartModel model : localCart) {
                invoiceObject = new JSONObject();
                rootJsonObject.put("invoiceNumber", "");
                invoiceObject.put("companyCode", companyCode);
                invoiceObject.put("invoiceDate", currentDateString);
                invoiceObject.put("slNo", index);
                invoiceObject.put("productCode", model.getCART_COLUMN_PID());
                invoiceObject.put("productName", model.getCART_COLUMN_PNAME());
                invoiceObject.put("cartonQty", model.getCART_COLUMN_CTN_QTY());
                invoiceObject.put("unitQty", model.getCART_COLUMN_QTY());
                double data = Double.parseDouble(model.getCART_PCS_PER_CARTON());
                double cn_qty = Double.parseDouble(model.getCART_COLUMN_CTN_QTY());
                double lqty = Double.parseDouble(model.getCART_COLUMN_QTY());
                double net_qty = (cn_qty * data) + lqty;
                invoiceObject.put("qty", String.valueOf(net_qty));
                // convert into int
                int value = (int) data;
                invoiceObject.put("pcsPerCarton", String.valueOf(value));
                //    double priceValue=Double.parseDouble(model.getCART_UNIT_PRICE()) / net_qty;
//                if (object.optString("taxType").equals("I")){
//                    invoiceObject.put("price",Utils.twoDecimalPoint(priceValue));
//                }else {
//                    invoiceObject.put("price",Utils.twoDecimalPoint(priceValue));
//                }
                invoiceObject.put("price", model.getCART_COLUMN_CTN_PRICE());
                //  invoiceObject.put("cartonPrice",model.getCART_COLUMN_CTN_PRICE());
                invoiceObject.put("total", model.getCART_TOTAL_VALUE());
                if (model.getDiscount() != null && !model.getDiscount().isEmpty()) {
                    invoiceObject.put("itemDiscount", model.getDiscount());
                    //     invoiceObject.put("DiscountPercentage",model.getDiscount());

                } else {
                    invoiceObject.put("itemDiscount", "0.00");
                    //   invoiceObject.put("DiscountPercentage","0.00");
                }
                invoiceObject.put("totalTax", model.getCART_TAX_VALUE());
                invoiceObject.put("subTotal", model.getSubTotal());
                invoiceObject.put("netTotal", model.getCART_COLUMN_NET_PRICE());
                invoiceObject.put("taxType", object.optString("taxType"));
                invoiceObject.put("taxPerc", object.optString("taxPercentage"));

                double return_subtotal = 0;
                if (model.getReturn_qty() != null && !model.getReturn_qty().isEmpty() && !model.getReturn_qty().equals("null")) {
                    return_subtotal = Double.parseDouble(model.getReturn_qty()) * Double.parseDouble(model.getCART_UNIT_PRICE());
                }

                assert model.getReturn_qty() != null;
                if (!model.getReturn_qty().isEmpty() && !model.getReturn_qty().toString().equals("null")) {
                    invoiceObject.put("returnLQty", model.getReturn_qty());
                    invoiceObject.put("returnQty", model.getReturn_qty());
                } else {
                    invoiceObject.put("returnLQty", "0");
                    invoiceObject.put("returnQty", "0");
                }

                if (!model.getFoc_qty().toString().isEmpty() && !model.getFoc_qty().equals("null")) {
                    invoiceObject.put("focQty", model.getFoc_qty());
                } else {
                    invoiceObject.put("focQty", "0");
                }

                if (!model.getExchange_qty().isEmpty() && !model.getExchange_qty().equals("null")) {
                    invoiceObject.put("exchangeQty", model.getExchange_qty());
                } else {
                    invoiceObject.put("exchangeQty", "0");

                }

                invoiceObject.put("returnSubTotal", return_subtotal + "");
                invoiceObject.put("returnNetTotal", return_subtotal + "");
                invoiceObject.put("returnReason", "");
                invoiceObject.put("taxCode", object.optString("taxCode"));
                invoiceObject.put("uomCode", model.getUomCode());
                invoiceObject.put("retailPrice", model.getCART_COLUMN_CTN_PRICE());
                invoiceObject.put("itemRemarks", "");
                invoiceObject.put("locationCode", locationCode);
                invoiceObject.put("createUser", userName);
                invoiceObject.put("modifyUser", userName);
                invoiceDetailsArray.put(invoiceObject);
                index++;
            }

            signatureObject.put("InvoiceNo", "");
            signatureObject.put("CompanyCode", companyCode);
            signatureObject.put("Latitude", currentLocationLatitude);
            signatureObject.put("Longitude", currentLocationLongitude);
            signatureObject.put("RefSignature", signatureString);
            signatureObject.put("ModifyUser", userName);
            signatureObject.put("Modifydate", "");
            signatureObject.put("TranType", "IN");
            signatureObject.put("Address1", "");
            signatureObject.put("Address2", "");
            signatureObject.put("SlNo", 0);
            signatureObject.put("RefSignaturestring", null);


//            invoiceImageObject.put("InvoiceNo", "");
//            invoiceImageObject.put("CompanyCode", companyCode);
//            invoiceImageObject.put("SlNo", 0);
//            invoiceImageObject.put("TranType", "IN");
//            invoiceImageObject.put("CustomerCode", object.get("customerCode"));
//            invoiceImageObject.put("CustomerName", object.get("customerName"));
//            invoiceImageObject.put("DeliveryCode", SettingUtils.getDeliveryAddressCode());
//            invoiceImageObject.put("CompanyName", user.get(SessionManager.KEY_COMPANY_NAME));
//            invoiceImageObject.put("ModifyUser", userName);
//            invoiceImageObject.put("RefPhotostring", null);


            // rootJsonObject.put("IsSaveSO",false);
            //  rootJsonObject.put("InvoiceHeader", invoiceHeader);
            rootJsonObject.put("ReturnDetails", returnProductArray);
            rootJsonObject.put("PostingInvoiceDetails", invoiceDetailsArray);
            //  rootJsonObject.put("InvoiceSignature",signatureObject);
            // rootJsonObject.put("InvoicePhoto",invoiceImageObject);

            Log.w("RootJsonForSave:", rootJsonObject.toString());


            saveSalesOrder(rootJsonObject, "Invoice", copy);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("Given_Error:", Objects.requireNonNull(e.getMessage()));
        }
    }


    public void createAndValidateJsonObject(int copy) {
        JSONObject rootJsonObject = new JSONObject();
        JSONObject saleHeader = new JSONObject();
        JSONArray saleDetailsArray = new JSONArray();
        JSONObject saleObject = new JSONObject();
        try {
            // Sales Header Add values
            ArrayList<CartModel> localCart;
            localCart = dbHelper.getAllCartItems();
            double net_sub_total = 0.0;
            double net_tax = 0.0;
            double net_total = 0.0;
            double net_discount = 0;
            double total_value = 0;
            if (localCart.size() > 0) {
                for (CartModel model : localCart) {
                    if (model.getSubTotal() != null && !model.getSubTotal().isEmpty()) {
                        net_sub_total += Double.parseDouble(model.getSubTotal());
                    }
                    if (model.getCART_TAX_VALUE() != null && !model.getCART_TAX_VALUE().isEmpty()) {
                        net_tax += Double.parseDouble(model.getCART_TAX_VALUE());
                    }
                    if (model.getCART_COLUMN_NET_PRICE() != null && !model.getCART_COLUMN_NET_PRICE().isEmpty()) {
                        net_total += Double.parseDouble(model.getCART_COLUMN_NET_PRICE());
                    }

                    if (model.getDiscount() != null && !model.getDiscount().equals("null") && !model.getDiscount().isEmpty()) {
                        net_discount += Double.parseDouble(model.getDiscount());
                    }

                    if (model.getCART_TOTAL_VALUE() != null && !model.getCART_TOTAL_VALUE().equals("null")) {
                        total_value += Double.parseDouble(model.getCART_TOTAL_VALUE());
                    }
                }
            }

            if (customerResponse.optString("TaxType").equals("I")) {

                double sub_total = net_total - net_tax;
                double sub_total1 = sub_total + net_tax;

                netSubtottalValue = Utils.twoDecimalPoint(sub_total1);
                netTaxvalue = Utils.twoDecimalPoint(net_tax);
                netTotalValue = Utils.twoDecimalPoint(sub_total1);
                itemDiscountAmount = Utils.twoDecimalPoint(net_discount);
                totalValue = Utils.twoDecimalPoint(total_value);

            } else {
                netSubtottalValue = Utils.twoDecimalPoint(net_sub_total);
                netTaxvalue = Utils.twoDecimalPoint(net_tax);
                netTotalValue = Utils.twoDecimalPoint(net_total);
                itemDiscountAmount = Utils.twoDecimalPoint(net_discount);
                totalValue = Utils.twoDecimalPoint(total_value);
            }

            JSONArray detailsArray = customerResponse.optJSONArray("responseData");
            JSONObject object = detailsArray.optJSONObject(0);

            if (currentSaveDateTime == null || currentSaveDateTime.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                currentSaveDateTime = currentDateandTime;
            }

            rootJsonObject.put("soNumber", "");
            rootJsonObject.put("mode", "I");
            rootJsonObject.put("status", "");
            rootJsonObject.put("soDate", currentDateStringSO);
            rootJsonObject.put("currentDateTime", currentSaveDateTime);
            rootJsonObject.put("customerCode", object.optString("customerCode"));
            rootJsonObject.put("customerName", object.optString("customerName"));
            rootJsonObject.put("address", object.optString("address"));
            rootJsonObject.put("street", object.optString("street"));
            rootJsonObject.put("city", object.optString("city"));
            rootJsonObject.put("creditLimit", object.optString("creditLimit"));
            rootJsonObject.put("remark", "");
            rootJsonObject.put("currencyName", "Singapore Dollar");
            rootJsonObject.put("total", totalValue);
            rootJsonObject.put("itemDiscount", itemDiscountAmount);
            rootJsonObject.put("billDiscount", billDiscountAmount);
            rootJsonObject.put("billDiscountPercentage", billDiscountPercentage);
            rootJsonObject.put("subTotal", subTotalValue);
            rootJsonObject.put("taxTotal", netTaxvalue);
            rootJsonObject.put("netTotal", netTotalValue);
            rootJsonObject.put("DeliveryCode", SettingUtils.getDeliveryAddressCode());
            rootJsonObject.put("delCustomerName", "");
            rootJsonObject.put("delAddress1", object.optString("delAddress1"));
            rootJsonObject.put("delAddress2 ", object.optString("delAddress2"));
            rootJsonObject.put("delAddress3 ", object.optString("delAddress3"));
            rootJsonObject.put("delPhoneNo", object.optString("contactNo"));
            rootJsonObject.put("haveTax", object.optString("haveTax"));
            rootJsonObject.put("taxType", object.optString("taxType"));
            rootJsonObject.put("taxPerc", object.optString("taxPercentage"));
            rootJsonObject.put("taxCode", object.optString("taxCode"));
            rootJsonObject.put("currencyCode", object.optString("currencyCode"));
            rootJsonObject.put("currencyValue", "");
            rootJsonObject.put("currencyRate", "1");
            rootJsonObject.put("postalCode", object.optString("postalCode"));
            rootJsonObject.put("createUser", userName);
            rootJsonObject.put("modifyUser", userName);
            rootJsonObject.put("stockUpdated", "1");
            rootJsonObject.put("companyCode", companyCode);
            rootJsonObject.put("locationCode", locationCode);
            rootJsonObject.put("signature", signatureString);
            rootJsonObject.put("latitude", current_latitude);
            rootJsonObject.put("longitude", current_longitude);
            rootJsonObject.put("CurrentAddress", current_addr);
            rootJsonObject.put("image", imageString);
            rootJsonObject.put("signature", signatureString);

            // Sales Details Add to the Objects
            localCart = dbHelper.getAllCartItems();

            Log.w("Given_local_cart_size:", String.valueOf(localCart.size()));

            //  "PostingSalesOrderDetails":[{"companyCode":"WINAPP_DEMO","soDate":"20210806","slNo":1,"productCode":"FG\/001245","productName":
            //  "RUM","cartonQty":"0","unitQty":"5","qty":"5.0","pcsPerCarton":"100","price":"5.00","cartonPrice":"500","retailPrice":"500",
            //  "total":"25.0","itemDiscount":"0","totalTax":"0.0","subTotal":"25.0","netTotal":"25.00","taxType":"I","taxPerc":"","returnLQty":"0",
            //  "returnQty":"0","focQty":"0","exchangeQty":"0","returnSubTotal":"0.0","returnNetTotal":"0.0","taxCode":"","uomCode":"Ctn",
            //  "itemRemarks":"","locationCode":"01","createUser":"User1","modifyUser":"User1"}]}

            int index = 1;
            for (CartModel model : localCart) {
                saleObject = new JSONObject();

                saleObject.put("companyCode", companyCode);
                saleObject.put("slNo", index);
                saleObject.put("productCode", model.getCART_COLUMN_PID());
                saleObject.put("productName", model.getCART_COLUMN_PNAME());
                saleObject.put("cartonQty", model.getCART_COLUMN_CTN_QTY());
                saleObject.put("unitQty", model.getCART_COLUMN_QTY());
                double data = Double.parseDouble(model.getCART_PCS_PER_CARTON());
                double cn_qty = Double.parseDouble(model.getCART_COLUMN_CTN_QTY());
                double lqty = Double.parseDouble(model.getCART_COLUMN_QTY());
                double net_qty = (cn_qty * data) + lqty;
                saleObject.put("qty", String.valueOf(net_qty));
                // convert into int
                int value = (int) data;
                saleObject.put("pcsPerCarton", String.valueOf(value));
//                double priceValue=Double.parseDouble(model.getCART_UNIT_PRICE()) / net_qty;
//                if (object.optString("taxType").equals("I")){
//                    saleObject.put("price",Utils.twoDecimalPoint(priceValue));
//                }else {
//                    saleObject.put("price",Utils.twoDecimalPoint(priceValue));
//                }
                saleObject.put("price", model.getCART_COLUMN_CTN_PRICE());
                saleObject.put("total", model.getCART_TOTAL_VALUE());
                if (model.getDiscount() != null && !model.getDiscount().isEmpty()) {
                    saleObject.put("itemDiscount", model.getDiscount());
                    saleObject.put("DiscountPercentage", model.getDiscount());

                } else {
                    saleObject.put("itemDiscount", "0.00");
                    saleObject.put("DiscountPercentage", "0.00");

                }
//                Log.w("cartPricss",""+Utils.twoDecimalPoint(priceValue)+
//                        "cart_unit.."+model.getCART_UNIT_PRICE());

                saleObject.put("totalTax", model.getCART_TAX_VALUE());
                saleObject.put("subTotal", model.getSubTotal());
                saleObject.put("netTotal", model.getCART_COLUMN_NET_PRICE());
                saleObject.put("taxType", object.optString("taxType"));
                saleObject.put("taxPerc", object.optString("taxPercentage"));
                if (model.getFoc_qty() != null && !model.getFoc_qty().isEmpty() && !model.getFoc_qty().equals("null")) {
                    saleObject.put("focQty", model.getFoc_qty());
                } else {
                    saleObject.put("focQty", "0");
                }

                double return_subtotal = 0;
                if (model.getReturn_qty() != null && !model.getReturn_qty().isEmpty() && !model.getReturn_qty().equals("null")) {
                    return_subtotal = Double.parseDouble(model.getReturn_qty()) * Double.parseDouble(model.getCART_UNIT_PRICE());
                }

                assert model.getReturn_qty() != null;
                if (!model.getReturn_qty().isEmpty() && !model.getReturn_qty().toString().equals("null")) {
                    saleObject.put("returnLQty", model.getReturn_qty());
                    saleObject.put("returnQty", model.getReturn_qty());
                } else {
                    saleObject.put("returnLQty", "0");
                    saleObject.put("returnQty", "0");
                }

                if (!model.getExchange_qty().isEmpty() && !model.getExchange_qty().equals("null")) {
                    saleObject.put("exchangeQty", model.getExchange_qty());
                } else {
                    saleObject.put("exchangeQty", "0");
                }

                saleObject.put("returnSubTotal", return_subtotal + "");
                saleObject.put("returnNetTotal", return_subtotal + "");

                saleObject.put("taxCode", object.optString("taxCode"));
                saleObject.put("uomCode", model.getUomCode());
                saleObject.put("retailPrice", model.getCART_COLUMN_CTN_PRICE());
                saleObject.put("itemRemarks", "");
                saleObject.put("locationCode", locationCode);
                saleObject.put("createUser", userName);
                saleObject.put("modifyUser", userName);

                saleDetailsArray.put(saleObject);
                index++;
            }

            rootJsonObject.put("PostingSalesOrderDetails", saleDetailsArray);
            Log.w("RootSaveJson:", rootJsonObject.toString());

            saveSalesOrder(rootJsonObject, "SalesOrder", copy);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("Given_Error:", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void getCustomerDetails(String customerCode, boolean isloader) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        // Initialize a new JsonArrayRequest instance
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("CustomerCode", customerCode);
            // jsonObject.put("CompanyCode",companyCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Utils.getBaseUrl(getApplicationContext()) + "Customer";
        Log.w("Given_url_custDet:", url+jsonObject);
        ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Customer Details Loading...");
        if (isloader) {
            progressDialog.show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        Log.w("SAp_response_custDet:", response.toString());
                        //  {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"CUS\/686","customerName":"VH FACTORY",
                        //  "groupCode":"100","contactPerson":"","creditLimit":"150.000000","currencyCode":"SGD","currencyName":"Singapore Dollar",
                        //  "taxType":"","taxCode":"SR","taxName":"Sales Standard Rated Supplier SR","taxPercentage":"7.000000","balance":"21.600000",
                        //  "outstandingAmount":"128.400000","address":"","street":"","city":"","state":"","zipCode":"","country":"",
                        //  "createDate":"13\/07\/2021","updateDate":"30\/07\/2021","active":"N","remark":""}]}
                        if (response.length() > 0) {
                            if (response.optString("statusCode").equals("1")) {
                                customerObject = response;
                                customerResponse = response;
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error in getting response", Toast.LENGTH_LONG).show();
                            }
                        }
                        // pDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            showAlertDialog();
            // Do something when error occurred
            //  pDialog.dismiss();
            Log.w("Error_throwing:", error.toString());
            progressDialog.dismiss();
            // Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
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

    public void saveSalesOrder(JSONObject jsonBody, String action, int copy) {
        try {
            SweetAlertDialog pDialog = new SweetAlertDialog(CartActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            if (action.equals("SalesOrder")) {
                pDialog.setTitleText("Saving Sales Order...");
            } else if (action.equals("DeliveryOrder")) {
                pDialog.setTitleText("Saving Delivery Order...");
            } else {
                pDialog.setTitleText("Saving Invoice...");
            }
            pDialog.setCancelable(false);
            pDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
            Log.w("GivenInvoiceReqCart:", jsonBody.toString());
            String URL = "";
            if (action.equals("SalesOrder")) {
                URL = Utils.getBaseUrl(this) + "PostingSalesOrder";
            } else if (action.equals("DeliveryOrder")) {
                URL = Utils.getBaseUrl(this) + "PostingDeliveryOrder";
            } else {
                URL = Utils.getBaseUrl(this) + "PostingInvoice";
            }
            Log.w("Given_URL_InvApiCart:", URL);
            //    {"statusCode":2,"statusMessage":"Failed","responseData":{"docNum":null,"error":"Invoice :One of the base documents has already been closed  [INV1.BaseEntry][line: 1]"}}
            JsonObjectRequest salesOrderRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, response -> {
                Log.w("Invoice_Res_cartSap:", response.toString());
                Utils.clearCustomerSession(this);
                // dbHelper.removeCustomer();
                // {"statusCode":1,"statusMessage":"Invoice Created Successfully","responseData":{"docNum":"35","error":null}}
                pDialog.dismiss();
                String statusCode = response.optString("statusCode");
                String message = response.optString("statusMessage");
                JSONObject responseData = null;
                try {
                    responseData = response.getJSONObject("responseData");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.w("printenab_cart",""+isPrintEnable);
                if (statusCode.equals("1")) {
                    if (action.equals("SalesOrder") || action.equals("SalesEdit")) {
                        if (isPrintEnable) {
                            try {
                                dbHelper.removeAllItems();
                                JSONObject object = response.optJSONObject("responseData");
                                String doucmentNo = object.optString("docNum");
                                //   String result=object.optString("Result");
                                if (!doucmentNo.isEmpty()) {
                                    // getSalesOrderDetails(doucmentNo, copy);
                                    Intent intent = new Intent(this, SalesOrderListActivity.class);
                                    intent.putExtra("printSoNumber", doucmentNo);
                                    intent.putExtra("noOfCopy", String.valueOf(copy));
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error in getting printing data", Toast.LENGTH_SHORT).show();
                                    redirectActivity();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            dbHelper.removeAllItems();
                            redirectActivity();
                        }
                        isPrintEnable = false;
                    } else {
                        if (isPrintEnable) {
                            try {
                                //updateStockQty();
                                dbHelper.removeAllItems();
                                JSONObject object = response.optJSONObject("responseData");
                                String doucmentNo = object.optString("docNum");
                                //   String result=object.optString("Result");
                                if (!doucmentNo.isEmpty()) {
                                    // getInvoicePrintDetails(doucmentNo, copy);
                                    Intent intent = new Intent(getApplicationContext(), NewInvoiceListActivity.class);
                                    intent.putExtra("printInvoiceNumber", doucmentNo);
                                    intent.putExtra("noOfCopy", String.valueOf(copy));
                                    intent.putExtra("DOPrint", isDeliveryPrint);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error in getting printing data", Toast.LENGTH_SHORT).show();
                                    redirectActivity();
                                }
                                Log.w("cartSavEntr", "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.w("cartSavEntr1", "");

                            //updateStockQty();
                            dbHelper.removeAllItems();
                            redirectActivity();

//                            if (message.equals("Invoice Created Successfully")){
//                                try {
//                                    redirectActivity();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
                        }
                        isPrintEnable = false;
                    }
                } else {
                    Log.w("ErrorValues:", responseData.optString("error"));
                    if (responseData != null) {
                        Toast.makeText(getApplicationContext(), responseData.optString("error"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error in Saving Data...", Toast.LENGTH_SHORT).show();
                    }
                }
            }, error -> {
                Log.w("SalesOrder_Response:", error.toString());
                pDialog.dismiss();
            }) {
                /* @Override
                 public byte[] getBody() {
                     return jsonBody.toString().getBytes();
                 }*/
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> params = new HashMap<>();
                    String creds = String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD);
                    String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                    params.put("Authorization", auth);
                    return params;
                }
            };
            salesOrderRequest.setRetryPolicy(new RetryPolicy() {
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
            requestQueue.add(salesOrderRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStockQty() {
        localCart = dbHelper.getAllCartItems();
        if (localCart.size() > 0) {
            for (CartModel model : localCart) {
                double data = Double.parseDouble(model.getCART_PCS_PER_CARTON());
                double cn_qty = Double.parseDouble(model.getCART_COLUMN_CTN_QTY());
                double lqty = Double.parseDouble(model.getCART_COLUMN_QTY());
                double net_qty = (cn_qty * data) + lqty;
                dbHelper.updateProductStock(model.getCART_COLUMN_PID(), (int) net_qty, "addInvoice");
            }
            dbHelper.removeAllItems();
        }
    }


    public boolean validatePrinterConfiguration() throws JSONException {
        boolean printetCheck = false;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(CartActivity.this, "This device does not support bluetooth", Toast.LENGTH_SHORT).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            Toast.makeText(CartActivity.this, "Enable bluetooth and connect the printer", Toast.LENGTH_SHORT).show();
        } else {
            // Bluetooth is enabled
            if (!printerType.isEmpty() && !printerMacId.isEmpty()) {
                printetCheck = true;
            } else {
                Toast.makeText(CartActivity.this, "Please configure Printer", Toast.LENGTH_SHORT).show();
            }
        }
        return printetCheck;
    }

    public void clearData() {
        dbHelper.removeAllItems();
        //dbHelper.removeCustomer();
        resetCustomerDetails();
    }


    public void getInvoicePrintDetails(String invoiceNumber, int copy) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("CompanyCode", companyCode);
        jsonObject.put("InvoiceNo", invoiceNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        String url = Utils.getBaseUrl(CartActivity.this) + "SalesApi/GetInvoiceByCode?Requestdata=" + jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url);
        printAlertDialog = new ProgressDialog(CartActivity.this);
        printAlertDialog.setCancelable(false);
        printAlertDialog.setMessage("Generating Invoice Printing Data...");
        printAlertDialog.show();
        invoiceHeaderDetails = new ArrayList<>();
        invoicePrintList = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.w("Invoice_Details:", response.toString());
                        if (response.length() > 0) {
                            InvoicePrintPreviewModel model = new InvoicePrintPreviewModel();
                            model.setInvoiceNumber(response.optString("InvoiceNo"));
                            model.setInvoiceDate(response.optString("InvoiceDateString"));
                            model.setCustomerCode(response.optString("CustomerCode"));
                            model.setCustomerName(response.optString("CustomerName"));
                            model.setAddress(response.optString("Address1"));
                            model.setDeliveryAddress(response.optString("shipAddress1")+response.optString("shipAddress2")+response.optString("shipAddress3")+
                                    response.optString("shipStreet"));
                            model.setSubTotal(response.optString("SubTotal"));
                            model.setNetTax(response.optString("Tax"));
                            model.setNetTotal(response.optString("NetTotal"));
                            model.setTaxType(response.optString("TaxType"));
                            model.setTaxValue(response.optString("TaxPerc"));
                            model.setOutStandingAmount(response.optString("BalanceAmount"));
                            model.setBillDiscount(response.optString("BillDIscount"));
                            model.setItemDiscount(response.optString("ItemDiscount"));
                            model.setAllowDeliveryAddress(response.optString("showShippingAddress"));
                            JSONArray products = response.getJSONArray("InvoiceDetails");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject object = products.getJSONObject(i);
                                if (Double.parseDouble(object.optString("LQty")) > 0) {
                                    InvoicePrintPreviewModel.InvoiceList invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                                    invoiceListModel.setProductCode(object.optString("ProductCode"));
                                    invoiceListModel.setDescription(object.optString("ProductName"));
                                    invoiceListModel.setLqty(object.optString("LQty"));
                                    invoiceListModel.setCqty(object.optString("CQty"));
                                    invoiceListModel.setNetQty(object.optString("LQty"));
                                    double qty = Double.parseDouble(object.optString("LQty"));
                                    double price = Double.parseDouble(object.optString("Price"));

                                    double nettotal = qty * price;
                                    invoiceListModel.setTotal(String.valueOf(nettotal));
                                    invoiceListModel.setPricevalue(String.valueOf(price));
                                    invoiceListModel.setCartonPrice(object.optString("CartonPrice"));
                                    invoiceListModel.setUnitPrice(object.optString("Price"));
                                    invoiceListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                    invoiceListModel.setItemtax(object.optString("Tax"));
                                    invoiceListModel.setSubTotal(object.optString("SubTotal"));
                                    invoicePrintList.add(invoiceListModel);


                                    if (!object.optString("ReturnQty").isEmpty() && !object.optString("ReturnQty").equals("null") && Double.parseDouble(object.optString("ReturnQty")) > 0) {
                                        invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                                        invoiceListModel.setProductCode(object.optString("ProductCode"));
                                        invoiceListModel.setDescription(object.optString("ProductName"));
                                        invoiceListModel.setLqty(object.optString("LQty"));
                                        invoiceListModel.setCqty(object.optString("CQty"));
                                        invoiceListModel.setNetQty("-" + object.optString("ReturnQty"));

                                        double qty1 = Double.parseDouble(object.optString("ReturnQty"));
                                        double price1 = Double.parseDouble(object.optString("Price"));
                                        double nettotal1 = qty1 * price1;
                                        invoiceListModel.setTotal("-" + String.valueOf(nettotal1));
                                        invoiceListModel.setPricevalue(String.valueOf(price1));

                                        invoiceListModel.setUomCode(object.optString("UOMCode"));
                                        invoiceListModel.setCartonPrice(object.optString("CartonPrice"));
                                        invoiceListModel.setUnitPrice(object.optString("Price"));
                                        invoiceListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        invoiceListModel.setItemtax(object.optString("Tax"));
                                        invoiceListModel.setSubTotal(object.optString("SubTotal"));
                                        invoicePrintList.add(invoiceListModel);
                                    }


                                    if (Double.parseDouble(object.optString("CQty")) > 0) {
                                        invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                                        invoiceListModel.setProductCode(object.optString("ProductCode"));
                                        invoiceListModel.setDescription(object.optString("ProductName"));
                                        invoiceListModel.setLqty(object.optString("LQty"));
                                        invoiceListModel.setCqty(object.optString("CQty"));
                                        invoiceListModel.setNetQty(object.optString("CQty"));

                                        double qty1 = Double.parseDouble(object.optString("CQty"));
                                        double price1 = Double.parseDouble(object.optString("CartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        invoiceListModel.setTotal(String.valueOf(nettotal1));
                                        invoiceListModel.setPricevalue(String.valueOf(price1));

                                        invoiceListModel.setUomCode(object.optString("UOMCode"));
                                        invoiceListModel.setCartonPrice(object.optString("CartonPrice"));
                                        invoiceListModel.setUnitPrice(object.optString("Price"));
                                        invoiceListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        invoiceListModel.setItemtax(object.optString("Tax"));
                                        invoiceListModel.setSubTotal(object.optString("SubTotal"));
                                        invoicePrintList.add(invoiceListModel);
                                    }

                                } else {
                                    if (Double.parseDouble(object.optString("CQty")) > 0) {
                                        Log.w("PrintedCqtyValue:", object.optString("CQty"));
                                        InvoicePrintPreviewModel.InvoiceList invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                                        invoiceListModel.setProductCode(object.optString("ProductCode"));
                                        invoiceListModel.setDescription(object.optString("ProductName"));
                                        invoiceListModel.setLqty(object.optString("LQty"));
                                        invoiceListModel.setCqty(object.optString("CQty"));
                                        invoiceListModel.setNetQty(object.optString("CQty"));

                                        double qty1 = Double.parseDouble(object.optString("CQty"));
                                        double price1 = Double.parseDouble(object.optString("CartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        invoiceListModel.setTotal(String.valueOf(nettotal1));
                                        invoiceListModel.setPricevalue(String.valueOf(price1));

                                        invoiceListModel.setUomCode(object.optString("UOMCode"));
                                        invoiceListModel.setCartonPrice(object.optString("CartonPrice"));
                                        invoiceListModel.setUnitPrice(object.optString("Price"));
                                        invoiceListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        invoiceListModel.setItemtax(object.optString("Tax"));
                                        invoiceListModel.setSubTotal(object.optString("SubTotal"));
                                        invoicePrintList.add(invoiceListModel);


                                        if (!object.optString("ReturnQty").isEmpty() && !object.optString("ReturnQty").equals("null") && Double.parseDouble(object.optString("ReturnQty")) > 0) {
                                            invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                                            invoiceListModel.setProductCode(object.optString("ProductCode"));
                                            invoiceListModel.setDescription(object.optString("ProductName"));
                                            invoiceListModel.setLqty(object.optString("LQty"));
                                            invoiceListModel.setCqty(object.optString("CQty"));
                                            invoiceListModel.setNetQty("-" + object.optString("ReturnQty"));

                                            double qty12 = Double.parseDouble(object.optString("ReturnQty"));
                                            double price12 = Double.parseDouble(object.optString("Price"));
                                            double nettotal12 = qty12 * price12;
                                            invoiceListModel.setTotal("-" + String.valueOf(nettotal12));
                                            invoiceListModel.setPricevalue(String.valueOf(price12));

                                            invoiceListModel.setUomCode(object.optString("UOMCode"));
                                            invoiceListModel.setCartonPrice(object.optString("CartonPrice"));
                                            invoiceListModel.setUnitPrice(object.optString("Price"));
                                            invoiceListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                            invoiceListModel.setItemtax(object.optString("Tax"));
                                            invoiceListModel.setSubTotal(object.optString("SubTotal"));
                                            invoicePrintList.add(invoiceListModel);
                                        }
                                    }

                                }
                            }

                            model.setInvoiceList(invoicePrintList);
                            invoiceHeaderDetails.add(model);
                        }
                        // pDialog.dismiss();
                        sentPrintData("Invoice", copy);
                        // validatePrinterConfiguration("Invoice",copy);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            //pDialog.dismiss();
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
    public void showAlertDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CartActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Warning..!");
        builder.setMessage("Customer Information is not valid,Please try again..");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                startActivity(getIntent());
                finish();
                dialogInterface.dismiss();
            }
        });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //  getActivity().getWindow().setBackgroundDrawableResource(R.color.primaryDark);
    }


    public void getSalesOrderDetails(String soNumber, int copy) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("CompanyCode", companyCode);
        jsonObject.put("SoNo", soNumber);
        jsonObject.put("LocationCode", locationCode);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "SalesApi/GetSOByCode?Requestdata=" + jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url);

        printAlertDialog = new ProgressDialog(CartActivity.this);
        printAlertDialog.setMessage("Generating SalesOrder Printing Data...");
        printAlertDialog.show();
        salesOrderHeaderDetails = new ArrayList<>();
        salesOrderList = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.w("Sales_Details:", response.toString());
                        if (response.length() > 0) {
                            SalesOrderPrintPreviewModel model = new SalesOrderPrintPreviewModel();
                            model.setSoNumber(response.optString("SoNo"));
                            model.setSoDate(response.optString("SoDateString"));
                            model.setCustomerCode(response.optString("CustomerCode"));
                            model.setCustomerName(response.optString("CustomerName"));
                            model.setAddress(response.optString("Address1"));
                            model.setDeliveryAddress(response.optString("shippingAddress"));
                            model.setSubTotal(response.optString("SubTotal"));
                            model.setNetTax(response.optString("Tax"));
                            model.setNetTotal(response.optString("NetTotal"));
                            model.setTaxType(response.optString("TaxType"));
                            model.setTaxValue(response.optString("TaxPerc"));
                            model.setOutStandingAmount(response.optString("BalanceAmount"));
                            model.setBillDiscount(response.optString("BillDIscount"));
                            model.setItemDiscount(response.optString("ItemDiscount"));
                            JSONArray products = response.getJSONArray("SoDetails");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject object = products.getJSONObject(i);
                                if (Double.parseDouble(object.optString("LQty")) > 0) {
                                    SalesOrderPrintPreviewModel.SalesList salesListModel = new SalesOrderPrintPreviewModel.SalesList();

                                    salesListModel.setProductCode(object.optString("ProductCode"));
                                    salesListModel.setDescription(object.optString("ProductName"));
                                    salesListModel.setLqty(object.optString("LQty"));
                                    salesListModel.setCqty(object.optString("CQty"));
                                    salesListModel.setNetQty(object.optString("LQty"));
                                    salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                    salesListModel.setUnitPrice(object.optString("Price"));
                                    double qty = Double.parseDouble(object.optString("LQty"));
                                    double price = Double.parseDouble(object.optString("Price"));

                                    double nettotal = qty * price;
                                    salesListModel.setTotal(String.valueOf(nettotal));
                                    salesListModel.setPricevalue(String.valueOf(price));

                                    salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                    salesListModel.setItemtax(object.optString("Tax"));
                                    salesListModel.setSubTotal(object.optString("SubTotal"));
                                    salesOrderList.add(salesListModel);


                                    if (Double.parseDouble(object.optString("CQty")) > 0) {
                                        salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(object.optString("ProductCode"));
                                        salesListModel.setDescription(object.optString("ProductName"));
                                        salesListModel.setLqty(object.optString("LQty"));
                                        salesListModel.setCqty(object.optString("CQty"));
                                        salesListModel.setNetQty(object.optString("CQty"));

                                        double qty1 = Double.parseDouble(object.optString("CQty"));
                                        double price1 = Double.parseDouble(object.optString("CartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(object.optString("UOMCode"));
                                        salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                        salesListModel.setUnitPrice(object.optString("Price"));
                                        salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        salesListModel.setItemtax(object.optString("Tax"));
                                        salesListModel.setSubTotal(object.optString("SubTotal"));
                                        salesOrderList.add(salesListModel);
                                    }

                                    if (!object.optString("ReturnQty").isEmpty() && !object.optString("ReturnQty").equals("null") && Double.parseDouble(object.optString("ReturnQty")) > 0) {
                                        salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(object.optString("ProductCode"));
                                        salesListModel.setDescription(object.optString("ProductName"));
                                        salesListModel.setLqty(object.optString("LQty"));
                                        salesListModel.setCqty(object.optString("CQty"));
                                        salesListModel.setNetQty(object.optString("ReturnQty"));

                                        double qty1 = Double.parseDouble(object.optString("ReturnQty"));
                                        double price1 = Double.parseDouble(object.optString("Price"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(object.optString("UOMCode"));
                                        salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                        salesListModel.setUnitPrice(object.optString("Price"));
                                        salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        salesListModel.setItemtax(object.optString("Tax"));
                                        salesListModel.setSubTotal(object.optString("SubTotal"));
                                        salesOrderList.add(salesListModel);
                                    }

                                } else {
                                    if (Double.parseDouble(object.optString("CQty")) > 0) {
                                        SalesOrderPrintPreviewModel.SalesList salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(object.optString("ProductCode"));
                                        salesListModel.setDescription(object.optString("ProductName"));
                                        salesListModel.setLqty(object.optString("LQty"));
                                        salesListModel.setCqty(object.optString("CQty"));
                                        salesListModel.setNetQty(object.optString("Qty"));
                                        salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                        salesListModel.setUnitPrice(object.optString("Price"));

                                        double qty1 = Double.parseDouble(object.optString("CQty"));
                                        double price1 = Double.parseDouble(object.optString("CartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(object.optString("UOMCode"));
                                        salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        salesListModel.setItemtax(object.optString("Tax"));
                                        salesListModel.setSubTotal(object.optString("SubTotal"));
                                        salesOrderList.add(salesListModel);


                                        if (!object.optString("ReturnQty").isEmpty() && !object.optString("ReturnQty").equals("null") && Double.parseDouble(object.optString("ReturnQty")) > 0) {
                                            salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                            salesListModel.setProductCode(object.optString("ProductCode"));
                                            salesListModel.setDescription(object.optString("ProductName"));
                                            salesListModel.setLqty(object.optString("LQty"));
                                            salesListModel.setCqty(object.optString("CQty"));
                                            salesListModel.setNetQty("-" + object.optString("ReturnQty"));

                                            double qty12 = Double.parseDouble(object.optString("ReturnQty"));
                                            double price12 = Double.parseDouble(object.optString("Price"));
                                            double nettotal12 = qty12 * price12;
                                            salesListModel.setTotal(String.valueOf(nettotal12));
                                            salesListModel.setPricevalue(String.valueOf(price12));

                                            salesListModel.setUomCode(object.optString("UOMCode"));
                                            salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                            salesListModel.setUnitPrice(object.optString("Price"));
                                            salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                            salesListModel.setItemtax(object.optString("Tax"));
                                            salesListModel.setSubTotal(object.optString("SubTotal"));
                                            salesOrderList.add(salesListModel);
                                        }

                                    }
                                }
                            }

                            model.setSalesList(salesOrderList);
                            salesOrderHeaderDetails.add(model);
                        }
                        sentPrintData("SalesOrder", copy);
                        // validatePrinterConfiguration("SalesOrder",copy);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
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

    public static void closeAlert() {
        printAlertDialog.dismiss();
    }

    public void redirectActivity() {
        if (saveAction.equals("SalesOrder") || saveAction.equals("SalesEdit")) {
            Intent intent = new Intent(CartActivity.this, SalesOrderListActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.w("cartSavEntr2", "");
            Intent intent = new Intent(CartActivity.this, NewInvoiceListActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void resetCustomerDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("customerPref", MODE_PRIVATE);
        SharedPreferences.Editor customerPredEdit = sharedPreferences.edit();
        customerPredEdit.putString("customerId", "");
        customerPredEdit.apply();
    }

    private void sentPrintData(String saveAction, int copy) throws IOException {
        if (saveAction.equals("SalesOrder") || saveAction.equals("SalesEdit")) {
            if (printerType.equals("TSC Printer")) {
                Toast.makeText(getApplicationContext(), "TSC printer in progress", Toast.LENGTH_SHORT).show();
                // TSCPrinter tscPrinter=new TSCPrinter(SalesOrderPrintPreview.this,printerMacId);
                //  tscPrinter.printInvoice(invoiceHeaderDetails,invoiceList);
                TSCPrinter printer = new TSCPrinter(CartActivity.this, printerMacId, "SalesOrder");
                printer.printSalesOrder(copy, salesOrderHeaderDetails, salesOrderList);
                printer.setOnCompletionListener(new TSCPrinter.OnCompletionListener() {
                    @Override
                    public void onCompleted() {
                        Utils.setSignature("");
                        Toast.makeText(getApplicationContext(), "SalesOrder printed successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (printerType.equals("Zebra Printer")) {
                ZebraPrinterActivity zebraPrinterActivity = new ZebraPrinterActivity(CartActivity.this, printerMacId);
                zebraPrinterActivity.printSalesOrder(copy, salesOrderHeaderDetails, salesOrderList);
            }
        } else {
            if (printerType.equals("TSC Printer")) {
                //Toast.makeText(getApplicationContext(),"TSC printer in progress",Toast.LENGTH_SHORT).show();
                // TSCPrinter tscPrinter=new TSCPrinter(SalesOrderPrintPreview.this,printerMacId);
                //  tscPrinter.printInvoice(invoiceHeaderDetails,invoiceList);
                TSCPrinter printer = new TSCPrinter(CartActivity.this, printerMacId);
                printer.printInvoice(copy, invoiceHeaderDetails, invoicePrintList, "true");
                printer.setOnCompletionListener(new TSCPrinter.OnCompletionListener() {
                    @Override
                    public void onCompleted() {
                        Utils.setSignature("");
                        Toast.makeText(getApplicationContext(), "Invoice printed successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (printerType.equals("Zebra Printer")) {
                if (isInvoicePrint && isDeliveryPrint) {
                    ZebraPrinterActivity zebraPrinterActivity = new ZebraPrinterActivity(CartActivity.this, printerMacId);
                    zebraPrinterActivity.printInvoice(copy, invoiceHeaderDetails, invoicePrintList, "true");
                    zebraPrinterActivity.setOnCompletionListener(() -> {
                        ZebraPrinterActivity zebraPrinterActivity1 = new ZebraPrinterActivity(CartActivity.this, printerMacId);
                        zebraPrinterActivity1.printDeliveryOrder(copy, invoiceHeaderDetails, invoicePrintList);
                        zebraPrinterActivity1.setOnCompletionListener(() -> {
                            Toast.makeText(CartActivity.this, "Printed Successfully", Toast.LENGTH_SHORT).show();
                            redirectActivity();
                        });
                    });
                    // zebraPrinterActivity.printDeliveryOrder(copy,invoiceHeaderDetails,invoicePrintList);
                } else if (isInvoicePrint) {
                    ZebraPrinterActivity zebraPrinterActivity = new ZebraPrinterActivity(CartActivity.this, printerMacId);
                    zebraPrinterActivity.printInvoice(copy, invoiceHeaderDetails, invoicePrintList, "false");
                    zebraPrinterActivity.setOnCompletionListener(() -> {
                        Toast.makeText(CartActivity.this, "Printed Successfully", Toast.LENGTH_SHORT).show();
                        redirectActivity();
                    });
                } else if (isDeliveryPrint) {
                    ZebraPrinterActivity zebraPrinterActivity = new ZebraPrinterActivity(CartActivity.this, printerMacId);
                    zebraPrinterActivity.printDeliveryOrder(copy, invoiceHeaderDetails, invoicePrintList);
                    zebraPrinterActivity.setOnCompletionListener(() -> {
                        Toast.makeText(CartActivity.this, "Printed Successfully", Toast.LENGTH_SHORT).show();
                        redirectActivity();
                    });
                }
            }
        }
    }

    private boolean hasPermission(Object permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(String.valueOf(permission)) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(String.valueOf(permissionsRejected.get(0)))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();
        for (Object perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(CartActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationTrack != null) {
            locationTrack.stopListener();
        }
    }
}