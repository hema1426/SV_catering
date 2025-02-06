package com.winapp.svcatering.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.os.AsyncTask;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.winapp.svcatering.BuildConfig;
import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.AddInvoiceActivityOld;
import com.winapp.svcatering.activity.DeliveryOrderListActivity;
import com.winapp.svcatering.activity.NewInvoiceListActivity;
import com.winapp.svcatering.activity.SalesOrderListActivity;
import com.winapp.svcatering.adapter.TableViewAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.AppUtils;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;
import com.winapp.svcatering.model.SalesOrderPrintPreviewModel;
import com.winapp.svcatering.tscprinter.TSCPrinterActivity;
import com.winapp.svcatering.utils.CaptureSignatureView;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.FileCompressor;
import com.winapp.svcatering.utils.ImageUtil;
import com.winapp.svcatering.utils.LocationTrack;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.SettingUtils;
import com.winapp.svcatering.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.winapp.svcatering.activity.AddInvoiceActivityOld.activityFrom;
import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

public class SummaryFragment extends Fragment {

    private static FragmentActivity myContext;
    private RecyclerView summaryView;
    private TableViewAdapter adapter;
    private static DBHelper dbHelper;
    private static ArrayList<CartModel> localCart;
    public static TextView customerName;
    public static TextView address1;
    public static TextView address2;
    public static TextView address3;
    private TextView invoiceDate;
    private TextView invoiceNumber;
    private TextView subTotal;
    private TextView totalText;
    private TextView taxText;
    private TextView netTotalText;
    private ArrayList<CustomerDetails> customerDetails;
    private ImageView showHideButton;
    private LinearLayout addressLayout;
    private static SweetAlertDialog pDialog;
    private static String userName;
    private static String locationCode;
    private static String currentDateString;
    private SessionManager session;
    private static String companyCode;
    public static JSONObject customerResponse=new JSONObject();
    private static String subTotalValue;
    private static String totalValue;
    private String netTax;
    private String customerId;
    private static HashMap<String ,String> user;
    public static String netTotalValue;
    public static String taxValue;
    LinearLayout invoicePanel;
    LinearLayout addressPanel;
    LinearLayout emptyText;
    View titleLayout;
    LinearLayout bottomLayout;
    public static BottomSheetBehavior behavior;
    ImageView showMore;
    ImageView closeView;
    public static String currentDate;
    public static View summaryLayout;
    public static View invoicePrintOption;
    public static boolean isShowMore=false;
    public static boolean isClose=false;
    public static LinearLayout transLayout;
    public static CheckBox cashCollected;
    public static CheckBox invoicePrint;
    public static CheckBox deliveryPrint;
    public static CheckBox emailInvoice;
    public static LinearLayout copyLayout;
    public static boolean isPrintEnable=false;
    public ImageView copyMinus;
    public ImageView copyPlus;
    public Button yesButton;
    public Button noButton;
    public TextView copyText;
    public static String saveAction;
    public static int noofCopyPrint=0;
    private static ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails;
    private static ArrayList<InvoicePrintPreviewModel.InvoiceList> invoicePrintList;
    private static ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList;

    public String printInvoiceNumber;
    public String noOfCopy;
    static String printerMacId;
    static String printerType;
    SharedPreferences sharedPreferences;
    public static ProgressDialog printAlertDialog;
    private static ArrayList<SalesOrderPrintPreviewModel> salesOrderHeaderDetails;
    private static ArrayList<SalesOrderPrintPreviewModel.SalesList> salesPrintList;
    public static String redirectAction;
    public TextView itemDiscountText;
    public EditText billDiscAmount;
    public EditText billDiscPercentage;
    public TextWatcher billDiscAmountTextWatcher;
    public TextWatcher billDiscPercentageTextWatcher;
    public static String itemDiscountAmount="0.00";
    public static String billDiscountPercentage="0";
    public static String billDiscountAmount="0.00";
    public ImageView signatureCapture;
    public CaptureSignatureView captureSignatureView;
    public AlertDialog alert;
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
    boolean isAllowLowStock=false;
    private String netTotalvalue;
    public boolean isBillDiscountTouch=false;
    public boolean isBillPercentageTouch=false;
    static double currentLocationLatitude=0.0;
    static double currentLocationLongitude=0.0;
    public static String signatureString="";
    public static String imageString;
    public TextView userNameText;
    public String selectCustomerId;
    private boolean isViewShown = false;
    private LinearLayout signatureLayout;
    private LinearLayout attachementLayout;
    TextView taxTitle;
    public static String currentDateStringSO;
    public static String currentDateSO;
    public static String companyName;
    public TextView deliveryAddressText;
    public TextView deliverAddressTitle;
    public static boolean isDeliveryPrint=false;
    public static boolean isInvoicePrint=false;
    private static ArrayList<SalesOrderPrintPreviewModel.SalesList> salesOrderList;
    public CoordinatorLayout rootLayout;
    public static String current_latitude="0.00";
    public static String current_longitude="0.00";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.invoice_summary, container, false);
        dbHelper=new DBHelper(getActivity());
        mCompressor = new FileCompressor(requireActivity());
        summaryView = view.findViewById(R.id.recyclerViewDeliveryProductList);
        customerName=view.findViewById(R.id.customer_name_val);
        address1=view.findViewById(R.id.address1);
        address2=view.findViewById(R.id.address2);
        address3=view.findViewById(R.id.address3);
        invoiceDate=view.findViewById(R.id.date);
        deliverAddressTitle=view.findViewById(R.id.delivery_address_text);
        invoiceNumber=view.findViewById(R.id.sr_no);
        subTotal=view.findViewById(R.id.sub_total_text);
        totalText=view.findViewById(R.id.total);
        taxText=view.findViewById(R.id.tax);
        netTotalText=view.findViewById(R.id.net_total);
        //  showHideButton=view.findViewById(R.id.show_hide);
        addressLayout=view.findViewById(R.id.addresspanel);
        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);
        companyName=user.get(SessionManager.KEY_COMPANY_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        emptyText=view.findViewById(R.id.empty_text);
        titleLayout=view.findViewById(R.id.title_layout);
        invoicePanel=view.findViewById(R.id.productpanel);
        bottomLayout=view.findViewById(R.id.bottom_panel);
        showMore=view.findViewById(R.id.show_more);
        closeView=view.findViewById(R.id.close);
        summaryLayout=view.findViewById(R.id.summary_layout);
        invoicePrintOption=view.findViewById(R.id.invoice_save_option);
        transLayout=view.findViewById(R.id.trans_layout);
        cashCollected=view.findViewById(R.id.cash_collected);
        invoicePrint=view.findViewById(R.id.invoice_print);
        deliveryPrint=view.findViewById(R.id.delivery_print);
        copyLayout=view.findViewById(R.id.copy_layout);
        copyMinus=view.findViewById(R.id.copy_minus);
        copyPlus=view.findViewById(R.id.copy_plus);
        yesButton=view.findViewById(R.id.buttonYes);
        noButton=view.findViewById(R.id.buttonNo);
        copyText=view.findViewById(R.id.copy);
        emailInvoice=view.findViewById(R.id.email_invoice);
        taxTitle=view.findViewById(R.id.tax_title);
        deliveryAddressText=view.findViewById(R.id.delivery_address);
        rootLayout=view.findViewById(R.id.rootLayout);

        signatureCapture=view.findViewById(R.id.signature_capture);
        captureSignatureView=view.findViewById(R.id.signature);
        signatureTitle=view.findViewById(R.id.signature_title);
        locationText=view.findViewById(R.id.locationText);
        selectImage=view.findViewById(R.id.select_image);
        billDiscAmount=view.findViewById(R.id.bill_disc_amount);
        billDiscPercentage=view.findViewById(R.id.bill_disc_percentage);
        itemDiscountText=view.findViewById(R.id.item_disc);
        userNameText=view.findViewById(R.id.user_name);

        signatureLayout=view.findViewById(R.id.signature_layout);
        attachementLayout=view.findViewById(R.id.attachement_layout);

        userNameText.setText("User : "+userName);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        // get Permission for the getting the Current location and Adding Image Permission to obtained

        signatureString="";
        getPermission();

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDateString = df.format(c);

        SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = df1.format(c);


        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDateStringSO = df2.format(c);

        SimpleDateFormat df3 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDateSO = df3.format(c);

        sharedPreferences = getActivity().getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        Log.w("Printer_Mac_Id:",printerMacId);
        Log.w("Printer_Type:",printerType);

    /*    if (dbHelper!=null){
            customerDetails=dbHelper.getCustomer();
        }

        if (CustomerFragment.customerResponse!=null && CustomerFragment.customerResponse.length()==0){
            if (customerDetails!=null){
                customerId=customerDetails.get(0).getCustomerCode();
                try {
                    getCustomerDetails(customerId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else {
            try {
                getCustomerDetails(AddInvoiceActivity.customerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/

       /* showHideButton.setOnClickListener(view1 -> {
            if (showHideButton.getTag().equals("hide")){
                showHideButton.setTag("show");
                addressLayout.setVisibility(View.VISIBLE);
                slideUp(addressLayout);
                showHideButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            }else {
                slideDown(addressLayout);
                addressLayout.setVisibility(View.GONE);
                showHideButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                showHideButton.setTag("hide");
            }
        });*/

    /*   if (activityFrom.equals("Invoice") || activityFrom.equals("InvoiceEdit") || activityFrom.equals("ReOrderInvoice")){
           signatureLayout.setVisibility(View.VISIBLE);
           attachementLayout.setVisibility(View.VISIBLE);
       }else {
           signatureLayout.setVisibility(View.GONE);
           attachementLayout.setVisibility(View.GONE);
       }*/


        if (activityFrom.equals("InvoiceEdit")
                || activityFrom.equals("SalesEdit")
                || activityFrom.equals("ConvertInvoice")
                || activityFrom.equals("ReOrderInvoice")
                || activityFrom.equals("ReOrderSales")
                || activityFrom.equals("DeliveryOrderEdit")
        ){
            if (AddInvoiceActivityOld.bill_discount!=null && !AddInvoiceActivityOld.bill_discount.isEmpty() && !AddInvoiceActivityOld.bill_discount.equals("null")){
                String bill_disc= AddInvoiceActivityOld.bill_discount;
                billDiscAmount.removeTextChangedListener(billDiscAmountTextWatcher);
                billDiscAmount.setText(bill_disc);
                billDiscAmount.addTextChangedListener(billDiscAmountTextWatcher);
                Log.w("PrintedBillDiscount:",bill_disc);
                double per=calculatePercentage(Double.parseDouble(bill_disc),getSubtotal());
                billDiscPercentage.removeTextChangedListener(billDiscPercentageTextWatcher);
                billDiscPercentage.setText(twoDecimalPoint(per)+"");
                billDiscPercentage.addTextChangedListener(billDiscPercentageTextWatcher);
                isBillDiscountTouch=true;
            }
        }

        View bottomSheet = view.findViewById(R.id.design_bottom_sheet);
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

       // setCalculation();

        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isShowMore=true;
                viewCloseBottomSheet();
            }
        });

        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSheet();
            }
        });

        invoicePrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (validatePrinterConfiguration()){
                    if (b){
                        isPrintEnable=true;
                        isInvoicePrint=true;
                        copyLayout.setVisibility(View.VISIBLE);
                        if (saveAction.equals("SalesOrder") || saveAction.equals("SalesEdit")){
                            emailInvoice.setVisibility(View.GONE);
                        }else {
                            emailInvoice.setVisibility(View.VISIBLE);
                        }
                    } else {
                        isPrintEnable=false;
                        isInvoicePrint=false;
                        copyText.setText("1");
                        copyLayout.setVisibility(View.GONE);
                        emailInvoice.setVisibility(View.GONE);
                    }
                }else {
                    invoicePrint.setChecked(false);
                    copyLayout.setVisibility(View.GONE);
                    isPrintEnable=false;
                    isInvoicePrint=false;
                }
            }
        });

        deliveryPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (validatePrinterConfiguration()){
                    if (b){
                        isPrintEnable=true;
                        isDeliveryPrint=true;
                        copyLayout.setVisibility(View.VISIBLE);
                        if (saveAction.equals("SalesOrder") || saveAction.equals("SalesEdit")){
                            emailInvoice.setVisibility(View.GONE);
                        }else {
                            emailInvoice.setVisibility(View.VISIBLE);
                        }
                    }else {
                        isPrintEnable=false;
                        isDeliveryPrint=false;
                        copyText.setText("1");
                        copyLayout.setVisibility(View.GONE);
                        emailInvoice.setVisibility(View.GONE);
                    }
                }else {
                    deliveryPrint.setChecked(false);
                    copyLayout.setVisibility(View.GONE);
                    isPrintEnable=false;
                    isDeliveryPrint=false;
                }
            }
        });

        copyPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value=Integer.parseInt(copyText.getText().toString());
                value++;
                copyText.setText(String.valueOf(value));
            }
        });

        copyMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value=Integer.parseInt(copyText.getText().toString());
                if (value!=1){
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
                noofCopyPrint=Integer.parseInt(copyText.getText().toString());
                if (saveAction.equals("SalesOrder") || saveAction.equals("SalesEdit")){
                    createAndValidateJsonObject(noofCopyPrint);
                }else if (saveAction.equals("DeliveryOrder")|| saveAction.equals("DeliveryEdit")){
                    createDOJson(noofCopyPrint);
                } else {
                    createInvoiceJson(noofCopyPrint);
                }
                closeSheet();
            }
        });

        billDiscAmount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isBillDiscountTouch=true;
                isBillPercentageTouch=false;
                return false;
            }
        });


        billDiscPercentage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isBillPercentageTouch=true;
                isBillDiscountTouch=false;
                return false;
            }
        });

        billDiscAmountTextWatcher =new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                double subtotal=getSubtotal();
                Log.w("SubTotalAmount:",subtotal+"");
                if (isBillDiscountTouch){
                    if (!editable.toString().isEmpty()){
                        if (editable.toString().equals(".")){
                            billDiscAmount.setText("0.");
                            billDiscAmount.setSelection(billDiscAmount.getText().length());
                            setPercentageAmount(Double.parseDouble("0"));
                            double net_subtotal= subtotal-0;
                            netTotalCalculation(net_subtotal);
                        }else {
                            setPercentageAmount(Double.parseDouble(editable.toString()));
                            double net_subtotal=subtotal-Double.parseDouble(editable.toString());
                            netTotalCalculation(net_subtotal);
                        }
                    }else {
                        setPercentageAmount(0);
                        netTotalCalculation(subtotal);
                    }
                }
            }
        };
        billDiscAmount.addTextChangedListener(billDiscAmountTextWatcher);


        billDiscPercentageTextWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                double subtotal=getSubtotal();
                if (isBillPercentageTouch){
                    if (!editable.toString().isEmpty()){
                        if (editable.toString().equals(".")){
                            billDiscPercentage.setText("0.");
                            billDiscPercentage.setSelection(billDiscPercentage.getText().length());
                            double amount=percentageToAmount(Double.parseDouble("0"));
                            billDiscAmount.removeTextChangedListener(billDiscAmountTextWatcher);
                            billDiscAmount.setText(twoDecimalPoint(amount)+"");
                            billDiscAmount.addTextChangedListener(billDiscAmountTextWatcher);
                            double net_subtotal=subtotal-Double.parseDouble(billDiscAmount.getText().toString());
                            netTotalCalculation(net_subtotal);
                        }else {
                            double amount=percentageToAmount(Double.parseDouble(editable.toString()));
                            Log.w("PercentageAmount:",amount+"");
                            billDiscAmount.removeTextChangedListener(billDiscAmountTextWatcher);
                            billDiscAmount.setText(twoDecimalPoint(amount)+"");
                            billDiscAmount.addTextChangedListener(billDiscAmountTextWatcher);
                            double net_subtotal=subtotal-Double.parseDouble(billDiscAmount.getText().toString());
                            netTotalCalculation(net_subtotal);
                        }
                    }else {
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
                if (selectImage.getTag().equals("view_image")){
                    showImage();
                }else {
                    selectImage();
                }
            }
        });


        if (mPhotoFile!=null && mPhotoFile.length()>0){
            selectImage.setText("View Image");
            selectImage.setTag("view_image");
        }else {
            selectImage.setText("Select Image");
            selectImage.setTag("select_image");
        }

        int count=dbHelper.numberOfRows();
        if (count > 0){
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
            selectCustomerId = sharedPreferences.getString("customerId", "");
            if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
                customerDetails = dbHelper.getCustomer(selectCustomerId);
                try {
                    getCustomerDetails(selectCustomerId,false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {
            rootLayout.setVisibility(View.VISIBLE);
            invoicePanel.setVisibility(View.GONE);
            addressLayout.setVisibility(View.GONE);
            summaryView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
            titleLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
        }


        return view;
    }


    public static void viewCloseBottomSheet(){
        //  hideKeyboard();

        if (isShowMore){
            summaryLayout.setVisibility(View.VISIBLE);
            invoicePrintOption.setVisibility(View.GONE);
        }else {
            invoicePrintOption.setVisibility(View.VISIBLE);
            summaryLayout.setVisibility(View.GONE);
        }
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        // get the Customer name from the local db
    }

    public void closeSheet(){
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void hideKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    // slide the view from below itself to the current position
    public void slideUp(View view){
        // view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),          // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        //  animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void showImage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.image_view_layout, null);
        ImageView imageView=dialogView.findViewById(R.id.invoice_image);
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

    // slide the view from its current position to below itself
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        //animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void setBillDiscountAmount(){
        if (activityFrom.equals("InvoiceEdit")
                || activityFrom.equals("SalesEdit")
                || activityFrom.equals("ConvertInvoice")
                || activityFrom.equals("ReOrderInvoice")
                || activityFrom.equals("ReOrderSales")
        ){
            if (AddInvoiceActivityOld.bill_discount!=null && !AddInvoiceActivityOld.bill_discount.isEmpty() && !AddInvoiceActivityOld.bill_discount.equals("null")){
                String bill_disc= AddInvoiceActivityOld.bill_discount;
                billDiscAmount.removeTextChangedListener(billDiscAmountTextWatcher);
                billDiscAmount.setText(bill_disc);
                billDiscAmount.addTextChangedListener(billDiscAmountTextWatcher);
                Log.w("PrintedBillDiscount:",bill_disc);
                double per=calculatePercentage(Double.parseDouble(bill_disc),getSubtotal());
                billDiscPercentage.removeTextChangedListener(billDiscPercentageTextWatcher);
                billDiscPercentage.setText(twoDecimalPoint(per)+"");
                billDiscPercentage.addTextChangedListener(billDiscPercentageTextWatcher);
                isBillDiscountTouch=true;
            }
        }
    }

    private void setCalculation() {

        try {
            localCart=new ArrayList<>();
            localCart = dbHelper.getAllCartItems();
            if (localCart.size()>0){
                JSONArray detailsArray=customerResponse.optJSONArray("responseData");
                JSONObject object=detailsArray.optJSONObject(0);
                customerName.setText(object.optString("customerName"));
                address1.setText(object.optString("address"));
                Date c = Calendar.getInstance().getTime();

                System.out.println("Current time => " + c);
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                String formattedDate = df.format(c);
                invoiceDate.setText(formattedDate);
                if (SettingUtils.getDeliveryAddress()!=null && !SettingUtils.getDeliveryAddress().isEmpty()){
                    deliverAddressTitle.setVisibility(View.VISIBLE);
                    deliveryAddressText.setVisibility(View.VISIBLE);
                    deliveryAddressText.setText(SettingUtils.getDeliveryAddress());
                }else {
                    deliverAddressTitle.setVisibility(View.GONE);
                    deliveryAddressText.setVisibility(View.GONE);
                }
                invoiceNumber.setText("#");

             /*   double price=0;
                double tax=0;
                double net_total=0;
                double net_discount=0;
                for (int j = 0;j<localCart.size();j++){
                    if (localCart.get(j).getCART_TOTAL_VALUE()!=null && !localCart.get(j).getCART_TOTAL_VALUE().equals("null")){
                        price+= Double.parseDouble(localCart.get(j).getCART_TOTAL_VALUE());
                    }
                    if (localCart.get(j).getCART_TAX_VALUE()!=null && !localCart.get(j).getCART_TAX_VALUE().equals("null")){
                        tax+=Double.parseDouble(localCart.get(j).getCART_TAX_VALUE());
                    }
                    if (localCart.get(j).getCART_COLUMN_NET_PRICE()!=null && !localCart.get(j).getCART_COLUMN_NET_PRICE().equals("null")){
                        net_total+=Double.parseDouble(localCart.get(j).getCART_COLUMN_NET_PRICE());
                    }

                    if (localCart.get(j).getDiscount() !=null && !localCart.get(j).getDiscount().equals("null")){
                        net_discount+=Double.parseDouble(localCart.get(j).getDiscount());
                    }
                }
                subTotal.setText("$ "+Utils.twoDecimalPoint(price));
                taxText.setText("$ "+ Utils.twoDecimalPoint(tax));
                totalText.setText("$ "+Utils.twoDecimalPoint(price));
                double finalNetTotal=price+tax;
                netTotalText.setText("$ "+Utils.twoDecimalPoint(net_total));
                itemDiscountText.setText(Utils.twoDecimalPoint(net_discount));

                subTotalValue=Utils.twoDecimalPoint(price);
                taxValue=Utils.twoDecimalPoint(tax);
                netTotalValue=Utils.twoDecimalPoint(net_total);
                itemDiscountAmount=Utils.twoDecimalPoint(net_discount);
*/
                adapter = new TableViewAdapter(getSummaryList());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                summaryView.setLayoutManager(linearLayoutManager);
                summaryView.setAdapter(adapter);
                summaryView.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.GONE);
                invoicePanel.setVisibility(View.VISIBLE);
                addressLayout.setVisibility(View.VISIBLE);
                titleLayout.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.VISIBLE);


                setSummaryTotal();
            }else {
                invoicePanel.setVisibility(View.GONE);
                addressLayout.setVisibility(View.GONE);
                summaryView.setVisibility(View.GONE);
                emptyText.setVisibility(View.VISIBLE);
                titleLayout.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.GONE);
            }
        }catch (Exception ex){}
    }

    public void setSummaryTotal(){
        try {
            ArrayList<CartModel> localCart;
            localCart = dbHelper.getAllCartItems();
            double net_sub_total=0.0;
            double net_tax=0.0;
            double net_total=0.0;
            double net_discount=0;
            double total_value=0;
            if (localCart.size()>0){
                for (CartModel model:localCart){
                    if (model.getSubTotal()!=null &&!model.getSubTotal().isEmpty()){
                        net_sub_total+=Double.parseDouble(model.getSubTotal());
                    }
                    if (model.getCART_TAX_VALUE()!=null && !model.getCART_TAX_VALUE().isEmpty()){
                        net_tax+=Double.parseDouble(model.getCART_TAX_VALUE());
                    }
                    if (model.getCART_COLUMN_NET_PRICE()!=null && !model.getCART_COLUMN_NET_PRICE().isEmpty()){
                        net_total+=Double.parseDouble(model.getCART_COLUMN_NET_PRICE());
                    }

                    if (model.getDiscount() !=null && !model.getDiscount().equals("null")){
                        net_discount+=Double.parseDouble(model.getDiscount());
                    }

                    if (model.getCART_TOTAL_VALUE()!=null && !model.getCART_TOTAL_VALUE().equals("null")){
                        total_value+=Double.parseDouble(model.getCART_TOTAL_VALUE());
                    }
                }

                JSONArray detailsArray=customerResponse.optJSONArray("responseData");
                JSONObject object=detailsArray.optJSONObject(0);

                if (object.optString("taxType").equals("I")){
                    taxTitle.setText("GST ( Inc )");
                }else if (object.optString("taxType").equals("E")){
                    taxTitle.setText("GST ( Exc )");
                }else {
                    taxTitle.setText("GST ( Zero )");
                }

                if (object.optString("taxType").equals("I")){
                    subTotal.setText(Utils.twoDecimalPoint(net_total));
                    taxText.setText(Utils.twoDecimalPoint(net_tax));
                    netTotalText.setText(Utils.twoDecimalPoint(net_total));
                    itemDiscountText.setText(Utils.twoDecimalPoint(net_discount));

                    double sub_total=net_total - net_tax;
                    totalText.setText(Utils.twoDecimalPoint(sub_total));
                    double sub_total1=sub_total + net_tax;

                   // subTotalValue=Utils.twoDecimalPoint(sub_total1);
                    subTotalValue=Utils.twoDecimalPoint(net_total);

                    taxValue=Utils.twoDecimalPoint(net_tax);
                    netTotalValue=Utils.twoDecimalPoint(net_total);
                    itemDiscountAmount=Utils.twoDecimalPoint(net_discount);
                    totalValue=Utils.twoDecimalPoint(total_value);

                }else {
                    subTotal.setText(Utils.twoDecimalPoint(net_sub_total));
                    taxText.setText(Utils.twoDecimalPoint(net_tax));
                    totalText.setText(Utils.twoDecimalPoint(net_sub_total));
                    netTotalText.setText(Utils.twoDecimalPoint(net_total));
                    itemDiscountText.setText(Utils.twoDecimalPoint(net_discount));

                    subTotalValue=Utils.twoDecimalPoint(net_sub_total);
                    taxValue=Utils.twoDecimalPoint(net_tax);
                    netTotalValue=Utils.twoDecimalPoint(net_total);
                    itemDiscountAmount=Utils.twoDecimalPoint(net_discount);
                    totalValue=Utils.twoDecimalPoint(total_value);
                }

            }
        }catch (Exception ex){}
    }

    private ArrayList<CartModel> getSummaryList() {
        localCart=new ArrayList<>();
        localCart = dbHelper.getAllCartItems();
        return localCart;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    /*    try {
            if (isVisibleToUser) {
//            setBillDiscountAmount();
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
                Utils.refreshActionBarMenu(requireActivity());
            }
            if (getView() != null && isVisibleToUser) {
                isViewShown = true;
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
                selectCustomerId = sharedPreferences.getString("customerId", "");
                if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
                    customerDetails = dbHelper.getCustomer(selectCustomerId);
                    try {
                        getCustomerDetails(selectCustomerId,false,"home");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                isViewShown = false;
            }
        }catch (Exception exception){}*/

    }

    public static void showSaveOption(String action){
        isShowMore=false;
        if (action.equals("SalesOrder") || action.equals("SalesEdit") || action.equals("ReSalesOrder")){
            // SummaryFragment.createAndValidateJsonObject();
            cashCollected.setVisibility(View.GONE);
            deliveryPrint.setVisibility(View.GONE);
            invoicePrint.setVisibility(View.VISIBLE);
            invoicePrint.setText("Sales Order Print");
            viewCloseBottomSheet();
            saveAction=action;
        } else if (action.equals("DeliveryOrder") || action.equals("DeliveryOrderEdit")){
            cashCollected.setVisibility(View.GONE);
            deliveryPrint.setVisibility(View.GONE);
            invoicePrint.setVisibility(View.VISIBLE);
            invoicePrint.setText("Delivery Order Print");
            viewCloseBottomSheet();
            saveAction=action;
        } else {
            // SummaryFragment.createInvoiceJson();
            if (signatureString!=null && !signatureString.isEmpty()){
                cashCollected.setVisibility(View.GONE);
                deliveryPrint.setVisibility(View.GONE);
                invoicePrint.setVisibility(View.VISIBLE);
                invoicePrint.setText("Invoice Print");
                viewCloseBottomSheet();
                saveAction=action;
            }else {
             isShowMore=true;
             viewCloseBottomSheet();
             Toast.makeText(myContext,"Please obtain the Signature..!",Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void createDOJson(int copy) {

        JSONObject rootJsonObject = new JSONObject();
        JSONArray saleDetailsArray=new JSONArray();
        JSONObject deliveryObject =new JSONObject();

        try {
            // Sales Header Add values

            JSONArray detailsArray=customerResponse.optJSONArray("responseData");
            JSONObject object=detailsArray.optJSONObject(0);

            if (activityFrom.equals("DeliveryOrderEdit")){
                rootJsonObject.put("doNumber", AddInvoiceActivityOld.editSoNumber);
                rootJsonObject.put("mode", "E");
                rootJsonObject.put("status", "O");
            }else {
                rootJsonObject.put("doNumber", "");
                rootJsonObject.put("mode", "I");
                rootJsonObject.put("status", "");
            }

            // {"doNumber":"","mode":"I","doDate":"20210817","customerCode":"C1003","customerName":"WinApp","address":"","street":"","city":"",
            // "creditLimit":"0.000000","remark":"Add Delivery order through mobile","currencyName":"Singapore Dollar","taxTotal":"0.00",
            // "subTotal":"25.00","total":"25.00","netTotal":"25.00","itemDiscount":"0.00","billDiscount":"0.00","totalDiscount":"0",
            // "billDiscountPercentage":"0","deliveryCode":"0","delCustomerName":"","delAddress1":"","delAddress2 ":"","delAddress3 ":"",
            // "delPhoneNo":"","haveTax":"","taxType":"I","taxPerc":"","taxCode":"","currencyCode":"SGD","currencyValue":"","CurrencyRate":"1",
            // "status":"O","postalCode":"","createUser":"User1","modifyUser":"User1","companyName":"WINAPP_DEMO","stockUpdated":"1","soType":"M",
            // "companyCode":"WINAPP_DEMO","locationCode":"HQ",
            // "Signature":"OnSAPifIwatchthesentmessage,Ifoundthemessagesentasemailwiththeattachment(andI'mablealsotoseetheattachment),
            // butonoutlookIcan'tseeanyattachment",
            //

            rootJsonObject.put("doDate", currentDateStringSO);
            rootJsonObject.put("customerCode", object.optString("customerCode"));
            rootJsonObject.put("customerName", object.optString("customerName"));
            rootJsonObject.put("address", object.optString("address"));
            rootJsonObject.put("street", object.optString("street"));
            rootJsonObject.put("city", object.optString("city"));
            rootJsonObject.put("creditLimit", object.optString("creditLimit"));
            rootJsonObject.put("remark", CustomerFragment.remarksEditText.getText().toString());
            rootJsonObject.put("currencyName", "Singapore Dollar");
            rootJsonObject.put("total", totalValue);
            rootJsonObject.put("itemDiscount", itemDiscountAmount);
            rootJsonObject.put("billDiscount", billDiscountAmount);
            rootJsonObject.put("billDiscountPercentage", billDiscountPercentage);
            rootJsonObject.put("subTotal", subTotalValue);
            rootJsonObject.put("taxTotal", taxValue);
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
            rootJsonObject.put("currencyValue","");
            rootJsonObject.put("currencyRate", "1");
            rootJsonObject.put("postalCode", object.optString("postalCode"));
            rootJsonObject.put("createUser",userName);
            rootJsonObject.put("modifyUser",userName);
            rootJsonObject.put("companyCode",companyCode);
            rootJsonObject.put("locationCode",locationCode);
            rootJsonObject.put("signature",signatureString);
            rootJsonObject.put("latitude",current_latitude);
            rootJsonObject.put("longitude",current_longitude);

            // Sales Details Add to the Objects
            localCart=dbHelper.getAllCartItems();

            Log.w("Given_local_cart_size:", String.valueOf(localCart.size()));

            //
            // "PostingDeliveryOrderDetails":[{"companyCode":"WINAPP_DEMO","doDate":"20210806","slNo":1,
            // "productCode":"SKU-AAFZ-0001","productName":"RUM","cartonQty":"0","unitQty":"5","qty":"10.0","pcsPerCarton":"100","price":"10.00",
            // "cartonPrice":"500","retailPrice":"500","total":"25.0","itemDiscount":"0","totalTax":"0.0","subTotal":"25.0","netTotal":"25.00",
            // "taxType":"I","taxPerc":"","returnLQty":"0","returnQty":"0","focQty":"0","exchangeQty":"0","returnSubTotal":"0.0","returnNetTotal":"0.0",
            // "taxCode":"","uomCode":"Ctn","itemRemarks":"","locationCode":"01","createUser":"User1","modifyUser":"User1"}]}
            int index=1;
            for (CartModel model:localCart){
                deliveryObject =new JSONObject();

                deliveryObject.put("companyCode",companyCode);
                deliveryObject.put("doDate", currentDateStringSO);
                deliveryObject.put("slNo",index);
                deliveryObject.put("productCode",model.getCART_COLUMN_PID());
                deliveryObject.put("productName",model.getCART_COLUMN_PNAME());
                deliveryObject.put("cartonQty",model.getCART_COLUMN_CTN_QTY());
                deliveryObject.put("unitQty",model.getCART_COLUMN_QTY());
                double data = Double.parseDouble(model.getCART_PCS_PER_CARTON());
                double cn_qty=Double.parseDouble(model.getCART_COLUMN_CTN_QTY());
                double lqty=Double.parseDouble(model.getCART_COLUMN_QTY());
                double net_qty=(cn_qty*data)+lqty;
                deliveryObject.put("qty",String.valueOf(net_qty));
                // convert into int
                int value = (int)data;
                deliveryObject.put("pcsPerCarton",String.valueOf(value));
                double priceValue=Double.parseDouble(model.getCART_UNIT_PRICE()) / net_qty;
                if (object.optString("taxType").equals("I")){
                    deliveryObject.put("price",Utils.twoDecimalPoint(priceValue));
                }else {
                    deliveryObject.put("price",Utils.twoDecimalPoint(priceValue));
                }
                deliveryObject.put("cartonPrice",model.getCART_COLUMN_CTN_PRICE());
                deliveryObject.put("total",model.getCART_TOTAL_VALUE());
                deliveryObject.put("itemDiscount",model.getDiscount());
                deliveryObject.put("itemDiscountPercentage","0");
                deliveryObject.put("totalTax",model.getCART_TAX_VALUE());
                deliveryObject.put("subTotal",model.getSubTotal());
                deliveryObject.put("netTotal",model.getCART_COLUMN_NET_PRICE());
                deliveryObject.put("taxType",object.optString("taxType"));
                deliveryObject.put("taxPerc",object.optString("taxPercentage"));
                if (model.getFoc_qty()!=null && !model.getFoc_qty().isEmpty() && !model.getFoc_qty().equals("null")){
                    deliveryObject.put("focQty", model.getFoc_qty());
                }else {
                    deliveryObject.put("focQty", "0");
                }

                double return_subtotal=0;
                if (model.getReturn_qty()!=null && !model.getReturn_qty().isEmpty()  && !model.getReturn_qty().equals("null")){
                    return_subtotal=Double.parseDouble(model.getReturn_qty()) * Double.parseDouble(model.getCART_UNIT_PRICE());
                }

                assert model.getReturn_qty() != null;
                if (!model.getReturn_qty().isEmpty() && !model.getReturn_qty().toString().equals("null")){
                    deliveryObject.put("returnLQty", model.getReturn_qty());
                    deliveryObject.put("returnQty", model.getReturn_qty());
                }else {
                    deliveryObject.put("returnLQty", "0");
                    deliveryObject.put("returnQty", "0");
                }

                if (!model.getExchange_qty().isEmpty() && !model.getExchange_qty().equals("null")){
                    deliveryObject.put("exchangeQty", model.getExchange_qty());
                }else {
                    deliveryObject.put("exchangeQty", "0");
                }

                deliveryObject.put("returnSubTotal",return_subtotal+"");
                deliveryObject.put("returnNetTotal", return_subtotal+"");

                deliveryObject.put("taxCode",object.optString("taxCode"));
                deliveryObject.put("uomCode",model.getUomCode());
                deliveryObject.put("retailPrice",model.getCART_COLUMN_CTN_PRICE());
                deliveryObject.put("itemRemarks","");
                deliveryObject.put("locationCode","HO");
                deliveryObject.put("createUser",userName);
                deliveryObject.put("modifyUser",userName);

                saleDetailsArray.put(deliveryObject);
                index++;
            }

            rootJsonObject.put("PostingDeliveryOrderDetails",saleDetailsArray);
            Log.w("RootSaveJson:",rootJsonObject.toString());

            saveSalesOrder(rootJsonObject,"DeliveryOrder",copy);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("Given_Error:", Objects.requireNonNull(e.getMessage()));
        }


    }

    public static void createAndValidateJsonObject(int copy) {
        JSONObject rootJsonObject = new JSONObject();
        JSONArray saleDetailsArray=new JSONArray();
        JSONObject saleObject=new JSONObject();

      //  {"soNumber":"","mode":"I","soDate":"20210806","customerCode":"WinApp","customerName":"WinApp","address":"","street":"","city":"",
        //  "creditLimit":"0.000000","remark":"","currencyName":"Singapore Dollar","taxTotal":"0.00","subTotal":"25.00","total":"25.00",
        //  "netTotal":"25.00","itemDiscount":"0.00","billDiscount":"0.00","totalDiscount":"0","billDiscountPercentage":"0","deliveryCode":"0",
        //  "delCustomerName":"","delAddress1":"","delAddress2 ":"","delAddress3 ":"","delPhoneNo":"","haveTax":"","taxType":"I","taxPerc":"",
        //  "taxCode":"","currencyCode":"SGD","currencyValue":"","CurrencyRate":"1","status":"0","postalCode":"","createUser":"User1",
        //  "modifyUser":"User1","companyName":"WINAPP_DEMO","stockUpdated":"1","soType":"M","companyCode":"WINAPP_DEMO","locationCode":"HQ",
        //

        try {
            // Sales Header Add values

            JSONArray detailsArray=customerResponse.optJSONArray("responseData");
            JSONObject object=detailsArray.optJSONObject(0);

            if (activityFrom.equals("SalesEdit")){
                rootJsonObject.put("soNumber", AddInvoiceActivityOld.editSoNumber);
                rootJsonObject.put("mode", "E");
                rootJsonObject.put("status", "O");
            }else {
                rootJsonObject.put("soNumber", "");
                rootJsonObject.put("mode", "I");
                rootJsonObject.put("status", "");
            }

            rootJsonObject.put("soDate", currentDateStringSO);
            rootJsonObject.put("customerCode", object.optString("customerCode"));
            rootJsonObject.put("customerName", object.optString("customerName"));
            rootJsonObject.put("orderNo", Utils.getOrderNo());
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
            rootJsonObject.put("taxTotal", taxValue);
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
            rootJsonObject.put("currencyValue","");
            rootJsonObject.put("currencyRate", "1");
            rootJsonObject.put("postalCode", object.optString("postalCode"));
            rootJsonObject.put("createUser",userName);
            rootJsonObject.put("modifyUser",userName);
            rootJsonObject.put("stockUpdated","1");
            rootJsonObject.put("companyCode",companyCode);
            rootJsonObject.put("locationCode",locationCode);
            rootJsonObject.put("signature",signatureString);
            rootJsonObject.put("latitude",current_latitude);
            rootJsonObject.put("longitude",current_longitude);

            // Sales Details Add to the Objects
            localCart=dbHelper.getAllCartItems();

            Log.w("Given_local_cart_size:", String.valueOf(localCart.size()));

            //  "PostingSalesOrderDetails":[{"companyCode":"WINAPP_DEMO","soDate":"20210806","slNo":1,"productCode":"FG\/001245","productName":
            //  "RUM","cartonQty":"0","unitQty":"5","qty":"5.0","pcsPerCarton":"100","price":"5.00","cartonPrice":"500","retailPrice":"500",
            //  "total":"25.0","itemDiscount":"0","totalTax":"0.0","subTotal":"25.0","netTotal":"25.00","taxType":"I","taxPerc":"","returnLQty":"0",
            //  "returnQty":"0","focQty":"0","exchangeQty":"0","returnSubTotal":"0.0","returnNetTotal":"0.0","taxCode":"","uomCode":"Ctn",
            //  "itemRemarks":"","locationCode":"01","createUser":"User1","modifyUser":"User1"}]}

            int index=1;
            for (CartModel model:localCart){
                saleObject=new JSONObject();

                saleObject.put("companyCode",companyCode);
                saleObject.put("slNo",index);
                saleObject.put("productCode",model.getCART_COLUMN_PID());
                saleObject.put("productName",model.getCART_COLUMN_PNAME());
                saleObject.put("cartonQty",model.getCART_COLUMN_CTN_QTY());
                saleObject.put("unitQty",model.getCART_COLUMN_QTY());
                double data = Double.parseDouble(model.getCART_PCS_PER_CARTON());
                double cn_qty=Double.parseDouble(model.getCART_COLUMN_CTN_QTY());
                double lqty=Double.parseDouble(model.getCART_COLUMN_QTY());
                double net_qty=(cn_qty*data)+lqty;
                saleObject.put("qty",String.valueOf(net_qty));
                // convert into int
                int value = (int)data;
                saleObject.put("pcsPerCarton",String.valueOf(value));
                double priceValue=Double.parseDouble(model.getCART_UNIT_PRICE()) / net_qty;
                if (object.optString("taxType").equals("I")){
                    saleObject.put("price",Utils.twoDecimalPoint(priceValue));
                }else {
                    saleObject.put("price",Utils.twoDecimalPoint(priceValue));
                }
                saleObject.put("cartonPrice",model.getCART_COLUMN_CTN_PRICE());
                saleObject.put("total",model.getCART_TOTAL_VALUE());
                saleObject.put("itemDiscount",model.getDiscount());
                saleObject.put("itemDiscountPercentage","0");
                saleObject.put("totalTax",model.getCART_TAX_VALUE());
                saleObject.put("subTotal",model.getSubTotal());
                saleObject.put("netTotal",model.getCART_COLUMN_NET_PRICE());
                saleObject.put("taxType",object.optString("taxType"));
                saleObject.put("taxPerc",object.optString("taxPercentage"));
                if (model.getFoc_qty()!=null && !model.getFoc_qty().isEmpty() && !model.getFoc_qty().equals("null")){
                    saleObject.put("focQty", model.getFoc_qty());
                }else {
                    saleObject.put("focQty", "0");
                }

                double return_subtotal=0;
                if (model.getReturn_qty()!=null && !model.getReturn_qty().isEmpty()  && !model.getReturn_qty().equals("null")){
                    return_subtotal=Double.parseDouble(model.getReturn_qty()) * Double.parseDouble(model.getCART_UNIT_PRICE());
                }

                assert model.getReturn_qty() != null;
                if (!model.getReturn_qty().isEmpty() && !model.getReturn_qty().toString().equals("null")){
                    saleObject.put("returnLQty", model.getReturn_qty());
                    saleObject.put("returnQty", model.getReturn_qty());
                }else {
                    saleObject.put("returnLQty", "0");
                    saleObject.put("returnQty", "0");
                }

                if (!model.getExchange_qty().isEmpty() && !model.getExchange_qty().equals("null")){
                    saleObject.put("exchangeQty", model.getExchange_qty());
                }else {
                    saleObject.put("exchangeQty", "0");
                }

                saleObject.put("returnSubTotal",return_subtotal+"");
                saleObject.put("returnNetTotal", return_subtotal+"");

                saleObject.put("taxCode",object.optString("taxCode"));
                saleObject.put("uomCode",model.getUomCode());
                saleObject.put("retailPrice",model.getCART_COLUMN_CTN_PRICE());
                saleObject.put("itemRemarks","");
                saleObject.put("locationCode","HO");
                saleObject.put("createUser",userName);
                saleObject.put("modifyUser",userName);

                saleDetailsArray.put(saleObject);
                index++;
            }

            rootJsonObject.put("PostingSalesOrderDetails",saleDetailsArray);
            Log.w("RootSaveJson:",rootJsonObject.toString());

            saveSalesOrder(rootJsonObject,"SalesOrder",copy);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("Given_Error:", Objects.requireNonNull(e.getMessage()));
        }
    }

    public static void saveSalesOrder(JSONObject jsonBody,String action,int copy){
        try {
            pDialog = new SweetAlertDialog(myContext, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            if (action.equals("SalesOrder")){
                pDialog.setTitleText("Saving Sales Order...");
            }else if (action.equals("DeliveryOrder")){
                pDialog.setTitleText("Saving Delivery Order...");
            } else {
                pDialog.setTitleText("Saving Invoice...");
            }
            pDialog.setCancelable(false);
            pDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(myContext);
            Log.w("GivenInvoiceRequest:",jsonBody.toString());
            String URL="";
            if (action.equals("SalesOrder")){
                 URL=Utils.getBaseUrl(myContext)+"PostingSalesOrder";
            }else if (action.equals("DeliveryOrder")){
                URL=Utils.getBaseUrl(myContext)+"PostingDeliveryOrder";
            } else {
                 URL=Utils.getBaseUrl(myContext)+"PostingInvoice";
            }
            Log.w("Given_InvoiceApi:",URL);
        //    {"statusCode":2,"statusMessage":"Failed","responseData":{"docNum":null,"error":"Invoice :One of the base documents has already been closed  [INV1.BaseEntry][line: 1]"}}
            JsonObjectRequest salesOrderRequest = new JsonObjectRequest(Request.Method.POST, URL,jsonBody, response -> {
                Log.w("Invoice_ResponseSap:",response.toString());
                Utils.clearCustomerSession(myContext);
                AppUtils.setProductsList(null);
                // dbHelper.removeCustomer();
                // {"statusCode":1,"statusMessage":"Invoice Created Successfully","responseData":{"docNum":"35","error":null}}
                pDialog.dismiss();
                String statusCode=response.optString("statusCode");
                String message=response.optString("statusMessage");
                JSONObject responseData = null;
                try {
                    responseData=response.getJSONObject("responseData");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (statusCode.equals("1")){
                    if (action.equals("SalesOrder") || action.equals("SalesEdit")){
                        if (isPrintEnable){
                        try {
                            dbHelper.removeAllItems();
                            JSONObject object=response.optJSONObject("responseData");
                            String doucmentNo =object.optString("docNum");
                            //   String result=object.optString("Result");
                            if (!doucmentNo.isEmpty()) {
                               // getSalesOrderDetails(doucmentNo, copy);
                                Intent intent=new Intent(myContext,SalesOrderListActivity.class);
                                intent.putExtra("printSoNumber",doucmentNo);
                                intent.putExtra("noOfCopy",String.valueOf(copy));
                                myContext.startActivity(intent);
                                myContext.finish();
                            }else {
                                Toast.makeText(myContext,"Error in getting printing data",Toast.LENGTH_SHORT).show();
                                redirectActivity();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        }else {
                            dbHelper.removeAllItems();
                            redirectActivity();
                        }
                        isPrintEnable=false;
                    }else if (action.equals("DeliveryOrder") || action.equals("DeliveryOrderEdit")){
                        if (isPrintEnable){
                           // {"statusCode":1,"statusMessage":"Delivery Order Created Successfully","responseData":{"docNum":"11","error":null}}
                        try {
                            dbHelper.removeAllItems();
                            JSONObject object=response.optJSONObject("responseData");
                            String doucmentNo =object.optString("docNum");
                         //   String result=object.optString("Result");
                            if (!doucmentNo.isEmpty()) {
                                getDoDetails(doucmentNo, copy);
                            }else {
                                Toast.makeText(myContext,"Error in getting printing data",Toast.LENGTH_SHORT).show();
                                redirectActivity();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        }else {
                            dbHelper.removeAllItems();
                            redirectActivity();
                        }
                        isPrintEnable=false;
                    } else {
                        if (isPrintEnable){
                        try {
                            //updateStockQty();
                            dbHelper.removeAllItems();
                            JSONObject object=response.optJSONObject("responseData");
                            String doucmentNo =object.optString("docNum");
                            //   String result=object.optString("Result");
                            if (!doucmentNo.isEmpty()) {
                               // getInvoicePrintDetails(doucmentNo, copy);
                                Intent intent=new Intent(myContext,NewInvoiceListActivity.class);
                                intent.putExtra("printInvoiceNumber",doucmentNo);
                                intent.putExtra("noOfCopy",String.valueOf(copy));
                                myContext.startActivity(intent);
                                myContext.finish();
                            }else {
                                Toast.makeText(myContext,"Error in getting printing data",Toast.LENGTH_SHORT).show();
                                redirectActivity();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        }else {
                            //updateStockQty();
                            dbHelper.removeAllItems();
                            if (message.equals("Invoice Created Successfully")){
                                try {
                            JSONArray detailsArray=customerResponse.optJSONArray("responseData");
                            JSONObject object=detailsArray.optJSONObject(0);
                            dbHelper.removeCustomer(object.get("customerCode").toString());
                            getCustomerDetails(object.get("customerCode").toString(),true,"save");
                           } catch (JSONException e) {
                               e.printStackTrace();
                              }
                            }
                            // dbHelper.removeAllCustomers();
                        }
                        isPrintEnable=false;
                    }
                }else {
                    Log.w("ErrorValues:",responseData.optString("error"));
                    if (responseData!=null){
                        Toast.makeText(myContext,responseData.optString("error"),Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(myContext,"Error in Saving Data...",Toast.LENGTH_SHORT).show();
                    }
                }
            }, error -> {
                Log.w("SalesOrder_Response:",error.toString());
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

    public static void getDoDetails(String doNumber,int noofCopyPrint) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject = new JSONObject();
        // jsonObject.put("CompanyCode", companyId);
        jsonObject.put("DeliveryOrderNo", doNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(myContext);
        String url = Utils.getBaseUrl(myContext) + "DeliveryOrderDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url);
        pDialog = new SweetAlertDialog(myContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        invoiceHeaderDetails = new ArrayList<>();
        invoiceList = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
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
                                model.setSoNumber(object.optString("soNumber"));
                                model.setSoDate(object.optString("soDate"));
                                model.setDoDate(object.optString("doDate"));
                                model.setDoNumber(object.optString("doNumber"));

                                JSONArray detailsArray=object.optJSONArray("deliveryOrderDetails");
                                for (int i = 0; i < detailsArray.length(); i++) {
                                    JSONObject detailObject = detailsArray.getJSONObject(i);
                                    InvoicePrintPreviewModel.InvoiceList invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                                    invoiceListModel.setProductCode(detailObject.optString("productCode"));
                                    invoiceListModel.setDescription(detailObject.optString("productName"));
                                    invoiceListModel.setLqty(detailObject.optString("unitQty"));
                                    invoiceListModel.setCqty(detailObject.optString("unitQty"));
                                    invoiceListModel.setNetQty(detailObject.optString("unitQty"));
                                    double qty = Double.parseDouble(detailObject.optString("unitQty"));
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
                                Toast.makeText(myContext,"Error in printing data...",Toast.LENGTH_SHORT).show();
                            }
                        }
                        sentPrintData("DeliveryOrder",noofCopyPrint);
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

    private static class GetCustomerInfo extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();
        ProgressDialog progressDialog;
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(myContext);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Update Customer Info...");
            progressDialog.show();
        }
        protected String doInBackground(Void...arg0) {
            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(myContext);
            // Initialize a new JsonArrayRequest instance
            JSONArray detailsArray=customerResponse.optJSONArray("responseData");
            JSONObject object=detailsArray.optJSONObject(0);
            String customerCode=object.optString("customerCode");
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("CustomerCode",customerCode);
                jsonObject.put("CompanyCode",companyCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url= Utils.getBaseUrl(myContext) +"MasterApi/GetCustomer?Requestdata="+jsonObject.toString();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    url,
                    null,
                    response -> {
                        try{
                            Log.w("response_Customer_Asyc:",response.toString());
                            ArrayList<CustomerModel> customerList=new ArrayList<>();
                            if (response.length()>0) {
                                if (response.optString("ErrorMessage").equals("null")){
                                    progressDialog.dismiss();
                                    CustomerModel model=new CustomerModel();
                                    if (response.optBoolean("IsActive")) {
                                        model.setCustomerCode(response.optString("CustomerCode"));
                                        model.setCustomerName(response.optString("CustomerName"));
                                        model.setAddress1(response.optString("Address1"));
                                        model.setCustomerAddress(response.optString("Address1"));
                                        model.setHaveTax(response.optString("HaveTax"));
                                        model.setTaxType(response.optString("TaxType"));
                                        model.setTaxPerc(response.optString("TaxPerc"));
                                        model.setTaxCode(response.optString("TaxCode"));
                                        if (response.optString("BalanceAmount").equals("null") || response.optString("BalanceAmount").isEmpty()) {
                                            model.setOutstandingAmount("0.00");
                                        } else {
                                            model.setOutstandingAmount(response.optString("BalanceAmount"));
                                        }
                                        customerList.add(model);

                                    }
                                    dbHelper.insertCustomerList(customerList);
                                    AppUtils.setProductsList(null);
                                }else {
                                    Toast.makeText(myContext,"Error in getting response",Toast.LENGTH_LONG).show();
                                }
                            }
                            // pDialog.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }, error -> {
                // Do something when error occurred
                //  pDialog.dismiss();
                Log.w("Error_throwing:",error.toString());
                progressDialog.dismiss();
                // Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
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
            return "You are at PostExecute";
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


  /*  public static void getCustomerDetails(String customerCode,boolean isloader) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(myContext);
        // Initialize a new JsonArrayRequest instance
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("CustomerCode",customerCode);
            jsonObject.put("CompanyCode",companyCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url= Utils.getBaseUrl(myContext) +"MasterApi/GetCustomer?Requestdata="+jsonObject.toString();
        Log.w("Given_url:",url);
        ProgressDialog progressDialog=new ProgressDialog(myContext);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Update Customer Info...");
        if (isloader){
            progressDialog.show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("response_Customer:",response.toString());
                        ArrayList<CustomerModel> customerList=new ArrayList<>();
                        if (response.length()>0) {
                            if (response.optString("ErrorMessage").equals("null")){
                                progressDialog.dismiss();
                                CustomerModel model=new CustomerModel();
                                if (response.optBoolean("IsActive")) {
                                    model.setCustomerCode(response.optString("CustomerCode"));
                                    model.setCustomerName(response.optString("CustomerName"));
                                    model.setAddress1(response.optString("Address1"));
                                    model.setCustomerAddress(response.optString("Address1"));
                                    model.setHaveTax(response.optString("HaveTax"));
                                    model.setTaxType(response.optString("TaxType"));
                                    model.setTaxPerc(response.optString("TaxPerc"));
                                    model.setTaxCode(response.optString("TaxCode"));
                                    if (response.optString("BalanceAmount").equals("null") || response.optString("BalanceAmount").isEmpty()) {
                                        model.setOutstandingAmount("0.00");
                                    } else {
                                        model.setOutstandingAmount(response.optString("BalanceAmount"));
                                    }
                                    customerList.add(model);

                                }
                                dbHelper.insertCustomerList(customerList);
                                AppUtils.setProductsList(null);
                                Intent intent=new Intent(myContext, NewInvoiceListActivity.class);
                                myContext.startActivity(intent);
                                myContext.finish();
                            }else {
                                Toast.makeText(myContext,"Error in getting response",Toast.LENGTH_LONG).show();
                            }
                        }
                        // pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
                    // Do something when error occurred
            //  pDialog.dismiss();
            Log.w("Error_throwing:",error.toString());
            progressDialog.dismiss();
            // Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
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
    }*/



    public static void updateStockQty(){
        localCart=dbHelper.getAllCartItems();
        if (localCart.size()>0){
            for (CartModel model:localCart){
                double data = Double.parseDouble(model.getCART_PCS_PER_CARTON());
                double cn_qty=Double.parseDouble(model.getCART_COLUMN_CTN_QTY());
                double lqty=Double.parseDouble(model.getCART_COLUMN_QTY());
                double net_qty=(cn_qty*data)+lqty;
                dbHelper.updateProductStock(model.getCART_COLUMN_PID(),(int)net_qty,"addInvoice");
            }
            dbHelper.removeAllItems();
        }
    }

    public static void getCustomerDetails(String customerCode,boolean isloader,String from) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(myContext);
        // Initialize a new JsonArrayRequest instance
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("CustomerCode",customerCode);
            // jsonObject.put("CompanyCode",companyCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url= Utils.getBaseUrl(myContext) +"Customer";
        Log.w("Given_url:",url);
        ProgressDialog progressDialog=new ProgressDialog(myContext);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Customer Details Loading...");
        if (isloader){
            progressDialog.show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        progressDialog.dismiss();
                        Log.w("SAP-response_customer:",response.toString());
                        ArrayList<CustomerModel> customerList=new ArrayList<>();
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            SummaryFragment.customerResponse=response;
                            JSONArray customerDetailArray=response.optJSONArray("responseData");
                            for (int i=0;i<customerDetailArray.length();i++){
                                JSONObject object=customerDetailArray.optJSONObject(i);
                                //  if (customerObject.optBoolean("IsActive")) {
                                CustomerModel model = new CustomerModel();
                                model.setCustomerCode(object.optString("customerCode"));
                                model.setCustomerName(object.optString("customerName"));
                                model.setAddress1(object.optString("address"));
                                model.setAddress2(object.optString("street"));
                                model.setAddress3(object.optString("city"));
                                model.setCustomerAddress(object.optString("address"));
                                model.setHaveTax(object.optString("HaveTax"));
                                model.setTaxType(object.optString("taxType"));
                                model.setTaxPerc(object.optString("taxPercentage"));
                                model.setTaxCode(object.optString("taxCode"));



                                address1.setText(model.getAddress1());
                                //  model.setCustomerBarcode(object.optString("BarCode"));
                                // model.setCustomerBarcode(String.valueOf(i));
                                if (object.optString("outstandingAmount").equals("null") || object.optString("outstandingAmount").isEmpty()) {
                                    model.setOutstandingAmount("0.00");
                                } else {
                                    model.setOutstandingAmount(object.optString("outstandingAmount"));
                                }
                                customerList.add(model);
                                // }
                            }
                            dbHelper.insertCustomerList(customerList);
                            if (from.equals("save")){
                                redirectActivity();
                            }
                          //  SummaryFragment summaryFragment=new SummaryFragment();
                          //  summaryFragment.setCalculation();
                           // AppUtils.setProductsList(null);
                        }else {
                            Toast.makeText(myContext,"Error,in getting Customer list",Toast.LENGTH_LONG).show();
                        }
                        // pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            //  pDialog.dismiss();
            Log.w("Error_throwing:",error.toString());
            progressDialog.dismiss();
            // Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
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

    public static void createInvoiceJson(int copy) {

       // JSONObject rootJsonObject = new JSONObject();
        JSONObject rootJsonObject = new JSONObject();
        JSONObject signatureObject=new JSONObject();
        JSONObject invoiceImageObject=new JSONObject();
        JSONArray invoiceDetailsArray =new JSONArray();
        JSONObject invoiceObject =new JSONObject();

        //  {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"CUS\/686","customerName":"VH FACTORY","groupCode":"100",
        //  "contactPerson":"","creditLimit":"150.000000","currencyCode":"SGD","currencyName":"Singapore Dollar","taxType":"","taxCode":"SR",
        //  "taxName":"Sales Standard Rated Supplier SR","taxPercentage":"7.000000","balance":"21.600000","outstandingAmount":"128.400000",
        //  "address":"","street":"","city":"","state":"","zipCode":"","country":"","createDate":"13\/07\/2021","updateDate":"30\/07\/2021",
        //  "active":"N","remark":""}]}

        JSONArray detailsArray=customerResponse.optJSONArray("responseData");
        JSONObject object=detailsArray.optJSONObject(0);

        try {
            // Sales Header Add values
            if (activityFrom.equals("InvoiceEdit")){
                rootJsonObject.put("invoiceNumber", AddInvoiceActivityOld.editInvoiceNumber);
                rootJsonObject.put("mode", "E");
            }else if (activityFrom.equals("ConvertInvoiceFromDO")){
                rootJsonObject.put("mode", "I");
                rootJsonObject.put("soNo","");
                rootJsonObject.put("doNo", AddInvoiceActivityOld.editDoNumber);
                rootJsonObject.put("invoiceNumber", "");
            }
            else if (activityFrom.equals("ConvertInvoice")){
                rootJsonObject.put("mode", "I");
                rootJsonObject.put("soNo", AddInvoiceActivityOld.editSoNumber);
                rootJsonObject.put("invoiceNumber", "");
            }else {
                rootJsonObject.put("invoiceNumber", "");
                rootJsonObject.put("mode", "I");
            }
            rootJsonObject.put("invoiceDate", currentDate);
            rootJsonObject.put("customerCode", object.get("customerCode"));
            rootJsonObject.put("customerName", object.get("customerName"));
            rootJsonObject.put("address", object.get("address"));
            rootJsonObject.put("street", object.get("street"));
            rootJsonObject.put("city", object.get("city"));
            rootJsonObject.put("creditLimit", object.get("creditLimit"));
            rootJsonObject.put("remark", "");
            rootJsonObject.put("currencyName", "Singapore Dollar");

            rootJsonObject.put("taxTotal", taxValue);
            rootJsonObject.put("subTotal", subTotalValue);
            rootJsonObject.put("total", totalValue);
            rootJsonObject.put("netTotal", netTotalValue);
            rootJsonObject.put("itemDiscount", itemDiscountAmount);
            rootJsonObject.put("billDiscount", billDiscountAmount);
            rootJsonObject.put("orderNo", Utils.getOrderNo());


          /*  if (customerResponse.optString("CurrencyCode").equals("SGD")){
                rootJsonObject.put("FTotal", totalValue);
                rootJsonObject.put("FItemDiscount", itemDiscountAmount);
                rootJsonObject.put("FBillDiscount", billDiscountAmount);
                rootJsonObject.put("FSubTotal", subTotalValue);
                rootJsonObject.put("FTax", taxValue);
                rootJsonObject.put("FNetTotal", netTotalValue);
            }else {
                rootJsonObject.put("FTotal", "0");
                rootJsonObject.put("FItemDiscount", "0");
                rootJsonObject.put("FBillDiscount", "0");
                rootJsonObject.put("FSubTotal", "0");
                rootJsonObject.put("FTax", "0");
                rootJsonObject.put("FNetTotal", "0");
            }*/

            rootJsonObject.put("totalDiscount","0");
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
            rootJsonObject.put("currencyValue","");
            rootJsonObject.put("CurrencyRate", "1");
            rootJsonObject.put("status", "0");
            rootJsonObject.put("postalCode", object.optString("postalCode"));
            rootJsonObject.put("createUser",userName);
            rootJsonObject.put("modifyUser",userName);
            rootJsonObject.put("companyName",companyName);
            rootJsonObject.put("stockUpdated","1");
            rootJsonObject.put("invoiceType","M");
            rootJsonObject.put("companyCode",companyCode);
            rootJsonObject.put("locationCode",locationCode);
            rootJsonObject.put("signature",signatureString);
            rootJsonObject.put("latitude",current_latitude);
            rootJsonObject.put("longitude",current_longitude);

            // Sales Details Add to the Objects
            localCart=dbHelper.getAllCartItems();

            int index=1;
            for (CartModel model:localCart){
                invoiceObject=new JSONObject();
                if (activityFrom.equals("InvoiceEdit")){
                    rootJsonObject.put("invoiceNumber", AddInvoiceActivityOld.editInvoiceNumber);
                }else {
                    rootJsonObject.put("invoiceNumber", "");
                }
                invoiceObject.put("companyCode",companyCode);
                invoiceObject.put("invoiceDate", currentDateString);
                invoiceObject.put("slNo",index);
                invoiceObject.put("productCode",model.getCART_COLUMN_PID());
                invoiceObject.put("productName",model.getCART_COLUMN_PNAME());
                invoiceObject.put("cartonQty",model.getCART_COLUMN_CTN_QTY());
                invoiceObject.put("unitQty",model.getCART_COLUMN_QTY());
                double data = Double.parseDouble(model.getCART_PCS_PER_CARTON());
                double cn_qty=Double.parseDouble(model.getCART_COLUMN_CTN_QTY());
                double lqty=Double.parseDouble(model.getCART_COLUMN_QTY());
                double net_qty=(cn_qty*data)+lqty;
                invoiceObject.put("qty",String.valueOf(net_qty));
                // convert into int
                int value = (int)data;
                invoiceObject.put("pcsPerCarton",String.valueOf(value));
                invoiceObject.put("pcsPerCarton",String.valueOf(value));
                double priceValue=Double.parseDouble(model.getCART_UNIT_PRICE()) / net_qty;
                if (object.optString("taxType").equals("I")){
                    invoiceObject.put("price",Utils.twoDecimalPoint(priceValue));
                }else {
                    invoiceObject.put("price",Utils.twoDecimalPoint(priceValue));
                }
                invoiceObject.put("cartonPrice",model.getCART_COLUMN_CTN_PRICE());
                invoiceObject.put("total",model.getCART_TOTAL_VALUE());
                invoiceObject.put("itemDiscount",model.getDiscount());
                invoiceObject.put("totalTax",model.getCART_TAX_VALUE());
                invoiceObject.put("subTotal",model.getSubTotal());
                invoiceObject.put("netTotal",model.getCART_COLUMN_NET_PRICE());
                invoiceObject.put("taxType",object.optString("taxType"));
                invoiceObject.put("taxPerc",object.optString("taxPercentage"));

                double return_subtotal=0;
                if (model.getReturn_qty()!=null && !model.getReturn_qty().isEmpty()  && !model.getReturn_qty().equals("null")){
                    return_subtotal=Double.parseDouble(model.getReturn_qty()) * Double.parseDouble(model.getCART_UNIT_PRICE());
                }

                assert model.getReturn_qty() != null;
                if (!model.getReturn_qty().isEmpty() && !model.getReturn_qty().toString().equals("null")){
                    invoiceObject.put("returnLQty", model.getReturn_qty());
                    invoiceObject.put("returnQty", model.getReturn_qty());
                }else {
                    invoiceObject.put("returnLQty", "0");
                    invoiceObject.put("returnQty", "0");
                }

                if (!model.getFoc_qty().toString().isEmpty() &&!model.getFoc_qty().equals("null")){
                    invoiceObject.put("focQty", model.getFoc_qty());
                }else {
                    invoiceObject.put("focQty", "0");
                }

                if (!model.getExchange_qty().isEmpty() && !model.getExchange_qty().equals("null")){
                    invoiceObject.put("exchangeQty", model.getExchange_qty());
                }else {
                    invoiceObject.put("exchangeQty", "0");

                }

                invoiceObject.put("returnSubTotal",return_subtotal+"");
                invoiceObject.put("returnNetTotal", return_subtotal+"");
                invoiceObject.put("taxCode",object.optString("taxCode"));
                invoiceObject.put("uomCode",model.getUomCode());
                invoiceObject.put("retailPrice",model.getCART_COLUMN_CTN_PRICE());
                invoiceObject.put("itemRemarks","");
                invoiceObject.put("locationCode","HO");
                invoiceObject.put("createUser",userName);
                invoiceObject.put("modifyUser",userName);
                invoiceDetailsArray.put(invoiceObject);
                index++;
            }

            if (activityFrom.equals("InvoiceEdit")){
                signatureObject.put("InvoiceNo", AddInvoiceActivityOld.editInvoiceNumber);
            }else {
                signatureObject.put("InvoiceNo","");
            }

            signatureObject.put("CompanyCode",companyCode);
            signatureObject.put("Latitude",currentLocationLatitude);
            signatureObject.put("Longitude",currentLocationLongitude);
            signatureObject.put("RefSignature",signatureString);
            signatureObject.put("ModifyUser",userName);
            signatureObject.put("Modifydate","");
            signatureObject.put("TranType","IN");
            signatureObject.put("Address1","");
            signatureObject.put("Address2","");
            signatureObject.put("SlNo",0);
            signatureObject.put("RefSignaturestring",null);


          /*  "CompanyCode": 0,
                    "InvoiceNo": null,
                    "SlNo": 0,
                    "RefPhoto": null,
                    "TranType": null,
                    "RefPhotostring": null,
                    "CustomerCode": null,
                    "DeliveryCode": null,
                    "CustomerName": null,
                    "CompanyName": null,
                    "ModifyUser": null*/

            if (activityFrom.equals("InvoiceEdit")){
                invoiceImageObject.put("InvoiceNo", AddInvoiceActivityOld.editInvoiceNumber);
            }else {
                invoiceImageObject.put("InvoiceNo","");
            }
            invoiceImageObject.put("CompanyCode",companyCode);
            invoiceImageObject.put("SlNo",0);
            invoiceImageObject.put("TranType","IN");
            invoiceImageObject.put("image",imageString);
            invoiceImageObject.put("CustomerCode",object.get("customerCode"));
            invoiceImageObject.put("CustomerName",object.get("customerName"));
            invoiceImageObject.put("DeliveryCode",SettingUtils.getDeliveryAddressCode());
            invoiceImageObject.put("CompanyName",user.get(SessionManager.KEY_COMPANY_NAME));
            invoiceImageObject.put("ModifyUser",userName);
            invoiceImageObject.put("RefPhotostring",null);


           // rootJsonObject.put("IsSaveSO",false);
          //  rootJsonObject.put("InvoiceHeader", invoiceHeader);
            rootJsonObject.put("PostingInvoiceDetails", invoiceDetailsArray);
          //  rootJsonObject.put("InvoiceSignature",signatureObject);
           // rootJsonObject.put("InvoicePhoto",invoiceImageObject);

            Log.w("RootJsonForSave:",rootJsonObject.toString());

            saveSalesOrder(rootJsonObject,"Invoice",copy);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("Given_Error:", Objects.requireNonNull(e.getMessage()));
        }
    }

    private static void getInvoicePrintDetails(String invoiceNumber, int copy) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject = new JSONObject();
        // jsonObject.put("CompanyCode", companyId);
        jsonObject.put("InvoiceNo", invoiceNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(myContext);
        String url = Utils.getBaseUrl(myContext) + "InvoiceDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url);
        pDialog = new SweetAlertDialog(myContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        invoiceHeaderDetails = new ArrayList<>();
        invoicePrintList = new ArrayList<>();
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        Log.w("DetailsResponse::", response.toString());
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray responseData=response.getJSONArray("responseData");
                            JSONObject object=responseData.optJSONObject(0);

                            InvoicePrintPreviewModel model = new InvoicePrintPreviewModel();
                            model.setInvoiceNumber(object.optString("invoiceNumber"));
                            model.setInvoiceDate(object.optString("invoiceDate"));
                            model.setCustomerCode(object.optString("customerCode"));
                            model.setCustomerName(object.optString("customerName"));
                            model.setAddress(object.optString("address1"));
                            model.setDeliveryAddress("No Address");
                            model.setSubTotal(object.optString("subTotal"));
                            model.setNetTax(object.optString("taxTotal"));
                            model.setNetTotal(object.optString("netTotal"));
                            model.setTaxType(object.optString("taxType"));
                            model.setTaxValue(object.optString("taxPerc"));
                            model.setOutStandingAmount(object.optString("totalOutstandingAmount"));
                            model.setBalanceAmount(object.optString("balanceAmount"));
                            Utils.setInvoiceOutstandingAmount(object.optString("balanceAmount"));
                            model.setBillDiscount(object.optString("billDiscount"));
                            model.setItemDiscount(object.optString("totalDiscount"));

                            JSONArray detailsArray=object.optJSONArray("invoiceDetails");
                            for (int i=0;i<detailsArray.length();i++){
                                JSONObject detailObject=detailsArray.optJSONObject(i);
                                InvoicePrintPreviewModel.InvoiceList invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();                                        invoiceListModel.setProductCode(detailObject.optString("productCode"));
                                invoiceListModel.setDescription(detailObject.optString("productName"));
                                invoiceListModel.setLqty(detailObject.optString("unitQty"));
                                invoiceListModel.setCqty(detailObject.optString("cartonQty"));
                                invoiceListModel.setNetQty(detailObject.optString("quantity"));
                                invoiceListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                                invoiceListModel.setUnitPrice(detailObject.optString("price"));

                                double qty1 = Double.parseDouble(detailObject.optString("quantity"));
                                double price1 = Double.parseDouble(detailObject.optString("cartonPrice"));
                                double nettotal1 = qty1 * price1;
                                invoiceListModel.setTotal(String.valueOf(nettotal1));
                                invoiceListModel.setPricevalue(String.valueOf(price1));

                                invoiceListModel.setUomCode(detailObject.optString("uomCode"));
                                invoiceListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                                invoiceListModel.setItemtax(detailObject.optString("totalTax"));
                                invoiceListModel.setSubTotal(detailObject.optString("subTotal"));
                                invoicePrintList.add(invoiceListModel);

                                if (!detailObject.optString("ReturnQty").isEmpty() && Double.parseDouble(detailObject.optString("ReturnQty")) > 0) {
                                    invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                                    invoiceListModel.setProductCode(detailObject.optString("ProductCode"));
                                    invoiceListModel.setDescription(detailObject.optString("ProductName"));
                                    invoiceListModel.setLqty(detailObject.optString("LQty"));
                                    invoiceListModel.setCqty(detailObject.optString("CQty"));
                                    invoiceListModel.setNetQty("-"+detailObject.optString("ReturnQty"));

                                    double qty12 = Double.parseDouble(detailObject.optString("ReturnQty"));
                                    double price12 = Double.parseDouble(detailObject.optString("Price"));
                                    double nettotal12 = qty12 * price12;
                                    invoiceListModel.setTotal(String.valueOf(nettotal12));
                                    invoiceListModel.setPricevalue(String.valueOf(price12));

                                    invoiceListModel.setUomCode(detailObject.optString("UOMCode"));
                                    invoiceListModel.setCartonPrice(detailObject.optString("CartonPrice"));
                                    invoiceListModel.setUnitPrice(detailObject.optString("Price"));
                                    invoiceListModel.setPcsperCarton(detailObject.optString("PcsPerCarton"));
                                    invoiceListModel.setItemtax(detailObject.optString("Tax"));
                                    invoiceListModel.setSubTotal(detailObject.optString("subTotal"));
                                    invoicePrintList.add(invoiceListModel);
                                }
                                model.setInvoiceList(invoicePrintList);
                                invoiceHeaderDetails.add(model);
                            }
                            pDialog.dismiss();
                            ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(myContext,printerMacId);
                            if (isInvoicePrint){
                                zebraPrinterActivity.printInvoice(copy,invoiceHeaderDetails,invoicePrintList,"false");
                            }else {
                                zebraPrinterActivity.printDeliveryOrder(copy,invoiceHeaderDetails,invoicePrintList);
                            }
                         //   sentPrintData("Invoice",copy);
                           // pDialog.dismiss();
                        }else {

                        }
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


    private static void getSalesOrderDetails(String soNumber, int copy) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        //  jsonObject.put("CompanyCode",companyId);
        jsonObject.put("SalesOrderNo", soNumber);
        // jsonObject.put("LocationCode",locationCode);
        RequestQueue requestQueue = Volley.newRequestQueue(myContext);
        String url= Utils.getBaseUrl(myContext) +"SalesOrderDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(myContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting SalesOrder Details...");
        pDialog.setCancelable(false);
        pDialog.show();
        salesOrderHeaderDetails =new ArrayList<>();
        salesOrderList =new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        // {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"WinApp","customerName":"WinApp","soNumber":"3",
                        // "soStatus":"O","soDate":"6\/8\/2021 12:00:00 am","netTotal":"26.750000","balanceAmount":"26.750000",
                        // "totalDiscount":"0.000000","paidAmount":"0.000000","contactPersonCode":"","createDate":"7\/8\/2021 12:00:00 am",
                        // "updateDate":"7\/8\/2021 12:00:00 am","remark":"","fDocTotal":"0.000000","fTaxAmount":"0.000000",
                        // "receivedAmount":"0.000000","total":"26.750000","fTotal":"0.000000","iTotalDiscount":"0.000000",
                        // "taxTotal":"1.750000","iPaidAmount":"0.000000","currencyCode":"SGD","currencyName":"Singapore Dollar",
                        // "companyCode":"WINAPP_DEMO","docEntry":"3","address1":"SingaporeShipTo1   Changi 890323 SG","taxPercentage":"0.000000",
                        // "discountPercentage":"0.000000",
                        //
                        //
                        // "salesOrderDetails":[{"slNo":"1","companyCode":"WINAPP_DEMO","soNo":"3",
                        // "productCode":"FG\/001245","productName":"Milk","quantity":"5.000000","cartonQty":"1.000000",
                        // "price":"5.000000","currency":"SGD","taxRate":"0.000000","discountPercentage":"0.000000",
                        // "lineTotal":"26.750000","fRowTotal":"0.000000","warehouseCode":"01","salesEmployeeCode":"-1","accountCode":"400000",
                        // "taxStatus":"Y","unitPrice":"5.000000","customerCategoryNo":"","barCodes":"","totalTax":"1.750000",
                        // "fTaxAmount":"0.000000","taxCode":"","taxType":"E","taxPerc":"0.000000","uoMCode":null,"soDate":"6\/8\/2021 12:00:00 am",
                        // "dueDate":"6\/8\/2021 12:00:00 am","createDate":"7\/8\/2021 12:00:00 am","updateDate":"7\/8\/2021 12:00:00 am",
                        // "createdUser":"manager","uomCode":"Ctn","uoMName":"Carton","cartonPrice":"3000.000000","piecePrice":"0.000000",
                        // "pcsPerCarton":"100.000000","lPrice":"100.000000","unitQty":"1.000000","retailPrice":"100.000000"}]}]}
                        Log.w("Sales_Details:",response.toString());
                        pDialog.dismiss();
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray responseData=response.getJSONArray("responseData");
                            JSONObject object=responseData.optJSONObject(0);

                            SalesOrderPrintPreviewModel model=new SalesOrderPrintPreviewModel();
                            model.setSoNumber(object.optString("soNumber"));
                            model.setSoDate(object.optString("soDate"));
                            model.setCustomerCode(object.optString("customerCode"));
                            model.setCustomerName(object.optString("customerName"));
                            model.setAddress(object.optString("address1") + object.optString("address2") + object.optString("address3"));
                            model.setDeliveryAddress(model.getAddress());
                            model.setSubTotal(object.optString("subTotal"));
                            model.setNetTax(object.optString("taxTotal"));
                            model.setNetTotal(object.optString("netTotal"));
                            model.setTaxType(object.optString("taxType"));
                            model.setTaxValue(object.optString("taxPerc"));
                            model.setOutStandingAmount(object.optString("outstandingAmount"));
                            model.setBillDiscount(object.optString("billDiscount"));
                            model.setItemDiscount(object.optString("totalDiscount"));

                           // deliveryAddressText.setText(model.getAddress());

                            JSONArray detailsArray=object.optJSONArray("salesOrderDetails");
                            for (int i=0;i<detailsArray.length();i++){
                                JSONObject detailObject=detailsArray.optJSONObject(i);
                                if (Double.parseDouble(detailObject.optString("quantity"))>0){
                                    SalesOrderPrintPreviewModel.SalesList salesListModel =new SalesOrderPrintPreviewModel.SalesList();
                                    salesListModel.setProductCode(detailObject.optString("productCode"));
                                    salesListModel.setDescription( detailObject.optString("productName"));
                                    salesListModel.setLqty(detailObject.optString("unitQty"));
                                    salesListModel.setCqty(detailObject.optString("cartonQty"));
                                    salesListModel.setNetQty(detailObject.optString("quantity"));
                                    salesListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                                    salesListModel.setUnitPrice(detailObject.optString("price"));
                                    double qty=Double.parseDouble(detailObject.optString("quantity"));
                                    double price=Double.parseDouble(detailObject.optString("price"));

                                    double nettotal=qty * price;
                                    salesListModel.setTotal(String.valueOf(nettotal));
                                    salesListModel.setPricevalue(String.valueOf(price));

                                    salesListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                                    salesListModel.setItemtax(detailObject.optString("totalTax"));
                                    salesListModel.setSubTotal(detailObject.optString("subTotal"));
                                    salesOrderList.add(salesListModel);

                                   /* if (Double.parseDouble(detailObject.optString("quantity")) > 0) {
                                        salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(detailObject.optString("productCode"));
                                        salesListModel.setDescription(detailObject.optString("productName"));
                                        salesListModel.setLqty(detailObject.optString("unitQty"));
                                        salesListModel.setCqty(detailObject.optString("cartonQty"));
                                        salesListModel.setNetQty(detailObject.optString("cartonQty"));

                                        double qty1 = Double.parseDouble(detailObject.optString("cartonQty"));
                                        double price1 = Double.parseDouble(detailObject.optString("cartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(detailObject.optString("uomCode"));
                                        salesListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                                        salesListModel.setUnitPrice(detailObject.optString("price"));
                                        salesListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                                        salesListModel.setItemtax(detailObject.optString("totalTax"));
                                        salesListModel.setSubTotal(detailObject.optString("subTotal"));
                                        salesOrderList.add(salesListModel);
                                    }*/

                                    if (!detailObject.optString("ReturnQty").isEmpty() && Double.parseDouble(detailObject.optString("ReturnQty")) > 0) {
                                        salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(detailObject.optString("ProductCode"));
                                        salesListModel.setDescription(detailObject.optString("ProductName"));
                                        salesListModel.setLqty(detailObject.optString("LQty"));
                                        salesListModel.setCqty(detailObject.optString("CQty"));
                                        salesListModel.setNetQty(detailObject.optString("ReturnQty"));

                                        double qty1 = Double.parseDouble(detailObject.optString("ReturnQty"));
                                        double price1 = Double.parseDouble(detailObject.optString("Price"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(detailObject.optString("uomCode"));
                                        salesListModel.setCartonPrice(detailObject.optString("CartonPrice"));
                                        salesListModel.setUnitPrice(detailObject.optString("Price"));
                                        salesListModel.setPcsperCarton(detailObject.optString("PcsPerCarton"));
                                        salesListModel.setItemtax(detailObject.optString("Tax"));
                                        salesListModel.setSubTotal(detailObject.optString("subTotal"));
                                        salesOrderList.add(salesListModel);
                                    }

                                }

                            /*    else {
                                    if (Double.parseDouble(detailObject.optString("quantity")) > 0) {
                                        SalesOrderPrintPreviewModel.SalesList salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(detailObject.optString("productCode"));
                                        salesListModel.setDescription(detailObject.optString("productName"));
                                        salesListModel.setLqty(detailObject.optString("unitQty"));
                                        salesListModel.setCqty(detailObject.optString("cartonQty"));
                                        salesListModel.setNetQty(detailObject.optString("quantity"));
                                        salesListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                                        salesListModel.setUnitPrice(detailObject.optString("price"));

                                        double qty1 = Double.parseDouble(detailObject.optString("cartonQty"));
                                        double price1 = Double.parseDouble(detailObject.optString("cartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(detailObject.optString("uomCode"));
                                        salesListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                                        salesListModel.setItemtax(detailObject.optString("totalTax"));
                                        salesListModel.setSubTotal(detailObject.optString("subTotal"));
                                        salesOrderList.add(salesListModel);


                                        if (!detailObject.optString("ReturnQty").isEmpty() && Double.parseDouble(detailObject.optString("ReturnQty")) > 0) {
                                            salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                            salesListModel.setProductCode(detailObject.optString("ProductCode"));
                                            salesListModel.setDescription(detailObject.optString("ProductName"));
                                            salesListModel.setLqty(detailObject.optString("LQty"));
                                            salesListModel.setCqty(detailObject.optString("CQty"));
                                            salesListModel.setNetQty("-"+detailObject.optString("ReturnQty"));

                                            double qty12 = Double.parseDouble(detailObject.optString("ReturnQty"));
                                            double price12 = Double.parseDouble(detailObject.optString("Price"));
                                            double nettotal12 = qty12 * price12;
                                            salesListModel.setTotal(String.valueOf(nettotal12));
                                            salesListModel.setPricevalue(String.valueOf(price12));

                                            salesListModel.setUomCode(detailObject.optString("UOMCode"));
                                            salesListModel.setCartonPrice(detailObject.optString("CartonPrice"));
                                            salesListModel.setUnitPrice(detailObject.optString("Price"));
                                            salesListModel.setPcsperCarton(detailObject.optString("PcsPerCarton"));
                                            salesListModel.setItemtax(detailObject.optString("Tax"));
                                            salesListModel.setSubTotal(detailObject.optString("subTotal"));
                                            salesOrderList.add(salesListModel);
                                        }

                                    }
                                }*/


                                model.setSalesList(salesOrderList);
                                salesOrderHeaderDetails.add(model);
                            }

                           // sentPrintData("SalesOrder",noofCopyPrint);
                            if (printerType.equals("Zebra Printer")) {
                                ZebraPrinterActivity zebraPrinterActivity = new ZebraPrinterActivity(myContext, printerMacId);
                                zebraPrinterActivity.printSalesOrder(noofCopyPrint, salesOrderHeaderDetails, salesOrderList);
                            }else if (printerType.equals("TSC Printer")){
                                TSCPrinter printer=new TSCPrinter(myContext,printerMacId,"SalesOrder");
                                printer.printSalesOrder(noofCopyPrint,salesOrderHeaderDetails,salesOrderList);
                                printer.setOnCompletionListener(new TSCPrinter.OnCompletionListener() {
                                    @Override
                                    public void onCompleted() {
                                        Utils.setSignature("");
                                        Toast.makeText(myContext,"SalesOrder printed successfully!",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            pDialog.dismiss();
                        }else {

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

    public static boolean validatePrinterConfiguration(){
        boolean printetCheck=false;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(myContext,"This device does not support bluetooth",Toast.LENGTH_SHORT).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            Toast.makeText(myContext,"Enable bluetooth and connect the printer",Toast.LENGTH_SHORT).show();
        } else {
            // Bluetooth is enabled
            if (!printerType.isEmpty() && !printerMacId.isEmpty()){
                printetCheck=true;
            }else {
                Toast.makeText(myContext,"Please configure Printer",Toast.LENGTH_SHORT).show();
            }
        }
        return printetCheck;
    }

    private static void sentPrintData(String saveAction, int copy) throws IOException {
        Log.w("IsprintInvoice:",isInvoicePrint+"IsprintDO:"+isDeliveryPrint);
        if (saveAction.equals("SalesOrder") || saveAction.equals("SalesEdit")){
            if (printerType.equals("TSC Printer")){
                //Toast.makeText(myContext,"TSC printer in progress",Toast.LENGTH_SHORT).show();
                // TSCPrinter tscPrinter=new TSCPrinter(SalesOrderPrintPreview.this,printerMacId);
                //  tscPrinter.printInvoice(invoiceHeaderDetails,invoiceList);
                TSCPrinter printer=new TSCPrinter(myContext,printerMacId,"SalesOrder");
                printer.printSalesOrder(copy,salesOrderHeaderDetails,salesOrderList);
                printer.setOnCompletionListener(new TSCPrinter.OnCompletionListener() {
                    @Override
                    public void onCompleted() {
                        Utils.setSignature("");
                        Toast.makeText(myContext,"SalesOrder printed successfully!",Toast.LENGTH_SHORT).show();
                    }
                });
            }else if (printerType.equals("Zebra Printer")){
                ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(myContext,printerMacId);
                zebraPrinterActivity.printSalesOrder(copy,salesOrderHeaderDetails,salesOrderList);
            }else {
                try {
                    final TSCPrinterActivity print = new TSCPrinterActivity(myContext, printerMacId, printerType);
                    print.initGenericPrinter();
                    print.setInitCompletionListener(() -> {
                        print.printSalesOrder(1, salesOrderHeaderDetails, salesPrintList);
                        print.setOnCompletedListener(() -> {
                            //helper.showLongToast(R.string.printed_successfully);
                            Toast.makeText(myContext, "Printed Successfully..!", Toast.LENGTH_SHORT).show();
                            closeAlert();
                            Intent intent=new Intent(myContext,SalesOrderListActivity.class);
                            myContext.startActivity(intent);
                            myContext.finish();
                        });
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if (saveAction.equals("DeliveryOrder")){
            if (printerType.equals("TSC Printer")){
                Toast.makeText(myContext,"TSC printer in progress",Toast.LENGTH_SHORT).show();
                // TSCPrinter tscPrinter=new TSCPrinter(SalesOrderPrintPreview.this,printerMacId);
                //  tscPrinter.printInvoice(invoiceHeaderDetails,invoiceList);
            }else if (printerType.equals("Zebra Printer")){
                ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(myContext,printerMacId);
                zebraPrinterActivity.printInvoice(copy,invoiceHeaderDetails,invoiceList,"true");
            }else {
                try {
                    final TSCPrinterActivity print = new TSCPrinterActivity(myContext, printerMacId, printerType);
                    print.initGenericPrinter();
                    print.setInitCompletionListener(() -> {
                        print.printSalesOrder(1, salesOrderHeaderDetails, salesPrintList);
                        print.setOnCompletedListener(() -> {
                            //helper.showLongToast(R.string.printed_successfully);
                            Toast.makeText(myContext, "Printed Successfully..!", Toast.LENGTH_SHORT).show();
                            closeAlert();
                            Intent intent=new Intent(myContext,SalesOrderListActivity.class);
                            myContext.startActivity(intent);
                            myContext.finish();
                        });
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (printerType.equals("TSC Printer")){
                Toast.makeText(myContext,"TSC printer in progress",Toast.LENGTH_SHORT).show();
                // TSCPrinter tscPrinter=new TSCPrinter(SalesOrderPrintPreview.this,printerMacId);
                //  tscPrinter.printInvoice(invoiceHeaderDetails,invoiceList);
            }else if (printerType.equals("Zebra Printer")){
                if (isInvoicePrint && isDeliveryPrint){
                    ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(myContext,printerMacId);
                    zebraPrinterActivity.printInvoice(copy,invoiceHeaderDetails,invoicePrintList,"true");
                    zebraPrinterActivity.setOnCompletionListener(() -> {
                        ZebraPrinterActivity zebraPrinterActivity1=new ZebraPrinterActivity(myContext,printerMacId);
                        zebraPrinterActivity1.printDeliveryOrder(copy,invoiceHeaderDetails,invoicePrintList);
                        zebraPrinterActivity1.setOnCompletionListener(() ->{
                            Toast.makeText(myContext,"Printed Successfully",Toast.LENGTH_SHORT).show();
                            redirectActivity();
                        });
                    });
                        // zebraPrinterActivity.printDeliveryOrder(copy,invoiceHeaderDetails,invoicePrintList);
                }else if (isInvoicePrint){
                    ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(myContext,printerMacId);
                    zebraPrinterActivity.printInvoice(copy,invoiceHeaderDetails,invoicePrintList,"false");
                    zebraPrinterActivity.setOnCompletionListener(() -> {
                        Toast.makeText(myContext,"Printed Successfully",Toast.LENGTH_SHORT).show();
                        redirectActivity();
                    });
                }else if (isDeliveryPrint){
                    ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(myContext,printerMacId);
                    zebraPrinterActivity.printDeliveryOrder(copy,invoiceHeaderDetails,invoicePrintList);
                    zebraPrinterActivity.setOnCompletionListener(() -> {
                        Toast.makeText(myContext,"Printed Successfully",Toast.LENGTH_SHORT).show();
                        redirectActivity();
                    });
                }
            }else {
                try {
                    final TSCPrinterActivity print = new TSCPrinterActivity(myContext, printerMacId, printerType);
                    print.initGenericPrinter();
                    print.setInitCompletionListener(() -> {
                        print.printInvoice(1, invoiceHeaderDetails, invoicePrintList);
                        print.setOnCompletedListener(() -> {
                            //helper.showLongToast(R.string.printed_successfully);
                            Toast.makeText(myContext, "Printed Successfully..!", Toast.LENGTH_SHORT).show();
                            closeAlert();
                            Intent intent=new Intent(myContext,NewInvoiceListActivity.class);
                            myContext.startActivity(intent);
                            myContext.finish();
                        });
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void closeAlert(){
        printAlertDialog.dismiss();
    }

    public static void redirectActivity(){
        if (saveAction.equals("SalesOrder") || saveAction.equals("SalesEdit")){
            Intent intent=new Intent(myContext, SalesOrderListActivity.class);
            myContext.startActivity(intent);
            myContext.finish();
        }else if (saveAction.equals("DeliveryOrder") || saveAction.equals("DeliveryOrderEdit")){
            Intent intent=new Intent(myContext, DeliveryOrderListActivity.class);
            myContext.startActivity(intent);
            myContext.finish();
        } else {
            Intent intent=new Intent(myContext, NewInvoiceListActivity.class);
            myContext.startActivity(intent);
            myContext.finish();
        }
    }

    // Adding the function for the Image and Signature adding function to Apply the Requrements
    /**
     * Alert dialog for capture or select from galley
     */
    private void selectImage() {
        final CharSequence[] items = {
                "Take Photo",
                "Cancel"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                requestStoragePermission(true);
            } /*else if (items[item].equals("Choose from Library")) {
                requestStoragePermission(false);
            } */else if (items[item].equals("Cancel")) {
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
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", photoFile);
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
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                    imageString=ImageUtil.getBase64StringImage(mPhotoFile);
                    //  Log.w("GivenImage1:",imageString);
                    Utils.w("GivenImage1",imageString);
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

                    imageString=ImageUtil.getBase64StringImage(mPhotoFile);
                    //  Log.w("GivenImage1:",imageString);
                    Utils.w("GivenImage2",imageString);

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
    /**
     * Requesting multiple permissions (storage and camera) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission(boolean isCamera) {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
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
                        error -> Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
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
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
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
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }
    /**
     * Get real file path from URI
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
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

    public void getPermission(){
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        getCurrentLocation();
    }

    public void getCurrentLocation(){
        locationTrack = new LocationTrack(getContext());
        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            String currentAddress=Utils.getCompleteAddress(getActivity(),latitude,longitude);
            if (currentAddress!=null && !currentAddress.isEmpty()){
                locationText.setText(currentAddress);
                current_latitude=String.valueOf(latitude);
                current_longitude=String.valueOf(longitude);
            }
        } else {
            locationTrack.showSettingsAlert();
        }
    }

    public void setCurrentLocation(double latitude,double longitude){
        this.currentLocationLatitude=latitude;
        this.currentLocationLongitude=longitude;
        String currentAddress=Utils.getCompleteAddress(getActivity(),latitude,longitude);
        if (currentAddress!=null && !currentAddress.isEmpty()){
            locationText.setText(currentAddress);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // getCurrentLocation();
    }

    public void showSignatureAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        final View customLayout = getLayoutInflater().inflate(R.layout.signature_layout, null);
        alertDialog.setView(customLayout);
        final Button acceptButton=customLayout.findViewById(R.id.buttonYes);
        final Button cancelButton=customLayout.findViewById(R.id.buttonNo);
        final Button clearButton=customLayout.findViewById(R.id.buttonClear);
        LinearLayout mContent = customLayout.findViewById(R.id.signature_layout);
        acceptButton.setEnabled(false);
        acceptButton.setAlpha(0.4f);
        CaptureSignatureView mSig = new CaptureSignatureView(getActivity(), null, new CaptureSignatureView.OnSignatureDraw() {
            @Override
            public void onSignatureCreated() {
                acceptButton.setEnabled(true);
                acceptButton.setAlpha(1f);
            }
        });
        mContent.addView(
                mSig,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // byte[] signature = captureSignatureView.getBytes();
                Bitmap signature = mSig.getBitmap();
                signatureCapture.setImageBitmap(signature);
                signatureString= ImageUtil.convertBimaptoBase64(signature);
                Utils.setSignature(signatureString);
                alert.dismiss();
                Log.w("SignatureString:",signatureString);
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
     *
     *
     * @param percentage to amount
     * send the net percentage to calculate the exact amount
     *
     */
    public double percentageToAmount(double percentage){
        return (getSubtotal() / 100.0f) * percentage;
    }

    public double calculatePercentage(double obtained, double total) {
        return obtained * 100 / total;
    }

    public void setPercentageAmount(double value){
        double per=calculatePercentage(value,getSubtotal());
        Log.w("PercentageTxt:",per+"");
        //billDiscAmount.removeTextChangedListener(billDiscAmountTextWatcher);
        if (per==0.0){
            billDiscPercentage.removeTextChangedListener(billDiscPercentageTextWatcher);
            billDiscPercentage.setText("");
            billDiscPercentage.addTextChangedListener(billDiscPercentageTextWatcher);
        }else {
            billDiscPercentage.removeTextChangedListener(billDiscPercentageTextWatcher);
            billDiscPercentage.setText(twoDecimalPoint(per)+"");
            billDiscPercentage.addTextChangedListener(billDiscPercentageTextWatcher);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void hideKeybaord(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }
    public double getSubtotal(){
        double subtotal =0;
        double discount=0.0;
        double tax=0;
        double net_total_value=0;
        double net_total=0;
        localCart=dbHelper.getAllCartItems();
        for (int j = 0;j<localCart.size();j++){
            if (localCart.get(j).getDiscount()!=null && !localCart.get(j).getDiscount().equals("null")){
                discount += Double.parseDouble(localCart.get(j).getDiscount());
            }
            if (localCart.get(j).getCART_TAX_VALUE()!=null && !localCart.get(j).getCART_TAX_VALUE().equals("null")){
                tax+=Double.parseDouble(localCart.get(j).getCART_TAX_VALUE());
            }
            if (localCart.get(j).getCART_COLUMN_NET_PRICE()!=null && !localCart.get(j).getCART_COLUMN_NET_PRICE().equals("null")){
                net_total+=Double.parseDouble(localCart.get(j).getCART_COLUMN_NET_PRICE());
            }

            if (localCart.get(j).getSubTotal()!=null && !localCart.get(j).getSubTotal().equals("null")){
                subtotal += Double.parseDouble(localCart.get(j).getSubTotal());
            }
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
        selectCustomerId = sharedPreferences.getString("customerId", "");
        customerDetails=dbHelper.getCustomer(selectCustomerId);
        if (customerDetails.get(0).getTaxType().equals("I")){
            net_total_value=(net_total+discount);
        }else {
            net_total_value=(subtotal+discount);
        }

        return net_total_value;
    }

    public double getReturnTotal(){
        double return_amt=0.0;
        ArrayList<CartModel> cartValues=dbHelper.getAllCartItems();
        for (CartModel model:cartValues){
            if (model.getReturn_qty()!=null && !model.getReturn_qty().equals("null") && !model.getReturn_qty().isEmpty()){
                return_amt+=Double.parseDouble(model.getReturn_qty()) * Double.parseDouble(model.getCART_UNIT_PRICE());
            }
        }
        return return_amt;
    }

    public void netTotalCalculation(double total) {
        try {

            double taxAmount = 0.0, netTotal = 0.0;
            double taxAmount1 = 0.0, netTotal1 = 0.0;
            double return_qty=0.0;
            double pcspercarton=0.0;

            Log.w("GivenSubtotalAmount:",total+"");

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
            selectCustomerId = sharedPreferences.getString("customerId", "");
            customerDetails=dbHelper.getCustomer(selectCustomerId);
            String taxVal =customerDetails.get(0).getTaxPerc();
            String taxType=customerDetails.get(0).getTaxType();
            Log.w("TaxType2323:",taxType);
            Log.w("TaxValue434:", taxVal);

            if (!billDiscPercentage.getText().toString().isEmpty()){
                billDiscountPercentage=billDiscPercentage.getText().toString();
            }else {
                billDiscountPercentage="0";
            }

            if (!billDiscAmount.getText().toString().isEmpty()){
                billDiscountAmount=billDiscAmount.getText().toString();
            }else {
                billDiscountAmount="0";
            }

            double tt=total;
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

            totalText.setText(Utils.twoDecimalPoint(subtotalValue));
            subTotal.setText(Utils.twoDecimalPoint(subtotalValue));
            subTotalValue=Utils.twoDecimalPoint(subtotalValue);

            if (!itemDisc.isEmpty()){
                itemDiscountAmount=Utils.twoDecimalPoint(Double.parseDouble(itemDisc));
            }else {
                itemDiscountAmount="0.00";
            }

            tt=subtotalValue;

            if (!taxType.matches("") && !taxVal.matches("")) {

                double taxValueCalc = Double.parseDouble(taxVal);

                if (taxType.matches("E")) {

                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subtotalValue * taxValueCalc) / 100;
                        String prodTax = twoDecimalPoint(taxAmount1);
                        taxText.setText("" + prodTax);

                        netTotal1 = subtotalValue + taxAmount1;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        netTotalText.setText("" + ProdNetTotal);

                        netTax=prodTax;
                        netTotalvalue=ProdNetTotal;

                        taxValue=Utils.twoDecimalPoint(Double.parseDouble(prodTax));
                        netTotalValue=Utils.twoDecimalPoint(Double.parseDouble(ProdNetTotal));
                        taxTitle.setText("GST ( Exc )");
                    } else {

                        taxAmount = (tt * taxValueCalc) / 100;
                        String prodTax = twoDecimalPoint(taxAmount);
                        taxText.setText("" + prodTax);

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        netTotalText.setText("" + ProdNetTotal);

                        netTax=prodTax;
                        netTotalvalue=ProdNetTotal;

                        taxValue=Utils.twoDecimalPoint(Double.parseDouble(prodTax));
                        netTotalValue=Utils.twoDecimalPoint(Double.parseDouble(ProdNetTotal));
                        taxTitle.setText("GST ( Exc )");
                    }

                } else if (taxType.matches("I")) {
                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subtotalValue * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = twoDecimalPoint(taxAmount1);
                        taxText.setText("" + prodTax);

                        // netTotal1 = subTotal + taxAmount1;
                        netTotal1 = subtotalValue;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        netTotalText.setText("" + ProdNetTotal);

                        double dTotalIncl = netTotal1 - taxAmount1;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        //  sl_total_inclusive.setText(totalIncl);

                        netTax=prodTax;
                        netTotalvalue=ProdNetTotal;

                        taxValue=Utils.twoDecimalPoint(Double.parseDouble(prodTax));
                        netTotalValue=Utils.twoDecimalPoint(Double.parseDouble(ProdNetTotal));

                      //  totalText.setText(totalIncl);
                      //  subTotal.setText(totalIncl);
                        double net_subtotal=subtotalValue-Double.parseDouble(prodTax);
                        totalText.setText(Utils.twoDecimalPoint(net_subtotal));
                        taxTitle.setText("GST ( Inc )");

                    } else {
                        taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = twoDecimalPoint(taxAmount);
                        taxText.setText("" + prodTax);

                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        netTotalText.setText("" + ProdNetTotal);


                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        //  sl_total_inclusive.setText(totalIncl);

                        netTax=prodTax;
                        netTotalvalue=ProdNetTotal;

                        taxValue=Utils.twoDecimalPoint(Double.parseDouble(prodTax));
                        netTotalValue=Utils.twoDecimalPoint(Double.parseDouble(ProdNetTotal));

                        double net_subtotal=subtotalValue-Double.parseDouble(prodTax);
                        totalText.setText(Utils.twoDecimalPoint(net_subtotal));

                      //  totalText.setText(totalIncl);
                    //    subTotal.setText(totalIncl);

                        taxTitle.setText("GST ( Inc )");
                    }

                } else if (taxType.matches("Z")) {

                    taxText.setText("0.0");
                    if (!itemDisc.matches("")) {
                        // netTotal1 = subTotal + taxAmount;
                        netTotal1 = subtotalValue;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        netTotalText.setText("" + ProdNetTotal);

                        netTax="0.0";
                        netTotalvalue=ProdNetTotal;

                        taxValue="0.0";
                        netTotalValue=Utils.twoDecimalPoint(Double.parseDouble(ProdNetTotal));
                        taxTitle.setText("GST ( Zero )");
                    } else {
                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        netTotalText.setText("" + ProdNetTotal);

                        netTax="0.0";
                        netTotalvalue=ProdNetTotal;

                        taxValue="0.0";
                        netTotalValue=Utils.twoDecimalPoint(Double.parseDouble(ProdNetTotal));
                        taxTitle.setText("GST ( Zero )");
                    }

                }


                else {
                    taxText.setText("0.0");
                    netTotalText.setText("" + Prodtotal);
                    netTax="0.0";
                    netTotalvalue=Prodtotal;
                    taxValue="0.0";
                    netTotalValue=Utils.twoDecimalPoint(Double.parseDouble(Prodtotal));
                    taxTitle.setText("GST ( Zero )");
                }

            } else if (taxVal.matches("")) {
                taxText.setText("0.0");
                netTotalText.setText("" + Prodtotal);
                netTax="0.0";
                netTotalvalue=Prodtotal;
                taxValue=Utils.twoDecimalPoint(Double.parseDouble(netTax));
                netTotalValue=Utils.twoDecimalPoint(Double.parseDouble(netTotalvalue));
                taxTitle.setText("GST ( Zero )");
            } else {
                taxText.setText("0.0");
                netTotalText.setText("" + Prodtotal);
                netTax="0.0";
                netTotalvalue=Prodtotal;

                taxValue=Utils.twoDecimalPoint(Double.parseDouble(netTax));
                netTotalValue=Utils.twoDecimalPoint(Double.parseDouble(netTotalvalue));
                taxTitle.setText("GST ( Zero )");
            }
            // Reset the Values if the Values in Negative
            if (subtotalValue < 0){
                billDiscAmount.setText("");
                billDiscPercentage.setText("");
                billDiscAmount.clearFocus();
                billDiscPercentage.clearFocus();
                Toast.makeText(requireActivity(),"Discount amount exceed..!",Toast.LENGTH_SHORT).show();
            }else {
                Utils.refreshActionBarMenu(getActivity());
            }
        } catch (Exception e) {

        }
    }

    private boolean hasPermission(Object permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (getActivity().checkSelfPermission(String.valueOf(permission)) == PackageManager.PERMISSION_GRANTED);
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
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void getCustomerDetails(String customerCode,boolean isloader) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        // Initialize a new JsonArrayRequest instance
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("CustomerCode",customerCode);
            // jsonObject.put("CompanyCode",companyCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url= Utils.getBaseUrl(getContext()) +"Customer";
        Log.w("Given_url:",url);
        ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Customer Details Loading...");
        if (isloader){
            progressDialog.show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        Log.w("SAP-response_customer:",response.toString());
                        //  {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"CUS\/686","customerName":"VH FACTORY",
                        //  "groupCode":"100","contactPerson":"","creditLimit":"150.000000","currencyCode":"SGD","currencyName":"Singapore Dollar",
                        //  "taxType":"","taxCode":"SR","taxName":"Sales Standard Rated Supplier SR","taxPercentage":"7.000000","balance":"21.600000",
                        //  "outstandingAmount":"128.400000","address":"","street":"","city":"","state":"","zipCode":"","country":"",
                        //  "createDate":"13\/07\/2021","updateDate":"30\/07\/2021","active":"N","remark":""}]}
                        if (response.length()>0) {
                            if (response.optString("statusCode").equals("1")){
                                customerResponse=response;
                                CustomerFragment.customerResponse=response;
                                setCalculation();
                                progressDialog.dismiss();
                            }else {
                                Toast.makeText(getActivity(),"Error in getting response",Toast.LENGTH_LONG).show();
                            }
                        }
                        // pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
                    // Do something when error occurred
            //  pDialog.dismiss();
            Log.w("Error_throwing:",error.toString());
            progressDialog.dismiss();
            // Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationTrack!=null){
            locationTrack.stopListener();
        }
    }
}