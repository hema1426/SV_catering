package com.winapp.svcatering.salesreturn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.TableViewAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.fragments.SummaryFragment;
import com.winapp.svcatering.model.AppUtils;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.ReturnProductsModel;
import com.winapp.svcatering.utils.CaptureSignatureView;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.ImageUtil;
import com.winapp.svcatering.utils.LocationTrack;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.SettingUtils;
import com.winapp.svcatering.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.MODE_PRIVATE;

public class SalesReturnSummary extends Fragment {

    private static FragmentActivity myContext;
    private RecyclerView summaryView;
    private TableViewAdapter adapter;
    private static DBHelper dbHelper;
    private static ArrayList<CartModel> localCart;
    private TextView customerName;
    private TextView address1;
    private TextView address2;
    private TextView address3;
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
    private String netTax;
    private String customerId;
    private HashMap<String ,String> user;
    private static String netTotalValue;
    private static String taxValue;
    private LinearLayout invoicePanel;
    private LinearLayout addressPanel;
    private LinearLayout emptyText;
    private View titleLayout;
    private LinearLayout bottomLayout;
    static BottomSheetBehavior behavior;
    private ImageView showMore;
    private ImageView closeView;
    public static String currentDate;
    public static String salesReturnNo;
    public static View summaryLayout;
    public static View invoicePrintOption;
    public static boolean isShowMore=false;
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
    public SharedPreferences sharedPreferences;
    static String printerMacId;
    static String printerType;
    private TextView userNametext;
    private JSONObject customerObject;
    private LinearLayout signatureLayout;
    private LinearLayout attachementLayout;
    public ImageView signatureCapture;
    public CaptureSignatureView captureSignatureView;
    public AlertDialog alert;
    public TextView signatureTitle;
    public static String signatureString="";
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    TextView locationText;
    public static String current_latitude="0.00";
    public static String current_longitude="0.00";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_summary, container, false);
        dbHelper=new DBHelper(getActivity());
        summaryView = view.findViewById(R.id.recyclerViewDeliveryProductList);
        customerName=view.findViewById(R.id.customer_name);
        address1=view.findViewById(R.id.address1);
        address2=view.findViewById(R.id.address2);
        address3=view.findViewById(R.id.address3);
        invoiceDate=view.findViewById(R.id.date);
        invoiceNumber=view.findViewById(R.id.sr_no);
        subTotal=view.findViewById(R.id.sub_total_text);
        totalText=view.findViewById(R.id.total);
        taxText=view.findViewById(R.id.tax);
        netTotalText=view.findViewById(R.id.net_total);
        addressLayout=view.findViewById(R.id.addresspanel);
        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);
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
        userNametext=view.findViewById(R.id.user_name);
        locationText=view.findViewById(R.id.locationText);

        signatureLayout=view.findViewById(R.id.signature_layout);
        attachementLayout=view.findViewById(R.id.attachement_layout);
        signatureCapture=view.findViewById(R.id.signature_capture);
        captureSignatureView=view.findViewById(R.id.signature);
        signatureTitle=view.findViewById(R.id.signature_title);

        userNametext.setText("User : "+user.get(SessionManager.KEY_USER_NAME));

       // showMore.setVisibility(View.GONE);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDateString = df.format(c);

        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = df2.format(c);

        sharedPreferences = getActivity().getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        Log.w("Printer_Mac_Id:",printerMacId);
        Log.w("Printer_Type:",printerType);

       // getPermission();

        try {
            if (dbHelper!=null){
             //   customerDetails=dbHelper.getCustomer();
            }
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
            String selectCustomerId = sharedPreferences.getString("customerId", "");
            if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
                customerDetails = dbHelper.getCustomer(selectCustomerId);
            }
            if (customerResponse.length()==0){
                if (customerDetails!=null){
                 //   getCustomerDetails(selectCustomerId,false);
                }
            }else {
               // getCustomerDetails(selectCustomerId,false);
            }
        }catch (Exception ex){
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
        String selectCustomerId = sharedPreferences.getString("customerId", "");
        getCustomerDetails(selectCustomerId,false);

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
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        invoicePrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (validatePrinterConfiguration()){
                    if (b){
                        isPrintEnable=true;
                        copyLayout.setVisibility(View.VISIBLE);
                    }else {
                        isPrintEnable=false;
                        copyText.setText("1");
                        copyLayout.setVisibility(View.GONE);
                        emailInvoice.setVisibility(View.GONE);
                    }
                }else {
                    invoicePrint.setChecked(false);
                    copyLayout.setVisibility(View.GONE);
                    isPrintEnable=false;
                }
            }
        });

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
                createSalesReturnJson(saveAction);
                closeSheet();
            }
        });

        return view;
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

   /* public void viewCloseBottomSheet(){
        hideKeyboard();
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        // get the Customer name from the local db
    }*/

    public void hideKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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

    public static void showSaveOption(String action){
        isShowMore=false;
        cashCollected.setVisibility(View.GONE);
        deliveryPrint.setVisibility(View.GONE);
        invoicePrint.setVisibility(View.VISIBLE);
        invoicePrint.setText("Sales Return Print");
        viewCloseBottomSheet();
        saveAction=action;
    }

    private void setCalculation() {

        try {
            localCart=new ArrayList<>();
            localCart = dbHelper.getAllCartItems();
            if (localCart.size()>0){

                Date c = Calendar.getInstance().getTime();

                System.out.println("Current time => " + c);
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                String formattedDate = df.format(c);
                invoiceDate.setText(formattedDate);
                invoiceNumber.setText("#");

                double price=0;
                double tax=0;
                double net_total=0;
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
                }
                subTotal.setText("$ "+Utils.twoDecimalPoint(price));
                taxText.setText("$ "+ Utils.fourDecimalPoint(tax));
                totalText.setText("$ "+Utils.twoDecimalPoint(price));
                double finalNetTotal=price+tax;
                netTotalText.setText("$ "+Utils.twoDecimalPoint(net_total));

                subTotalValue=Utils.twoDecimalPoint(price);
                taxValue=Utils.fourDecimalPoint(tax);
                netTotalValue=Utils.twoDecimalPoint(finalNetTotal);

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

    private ArrayList<CartModel> getSummaryList() {
        localCart=new ArrayList<>();
        localCart = dbHelper.getAllCartItems();
        return localCart;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
         //   setCalculation();
           // getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    public static void createSalesReturnJson(String action) {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDateString = df.format(c);

        SimpleDateFormat df3 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = df3.format(c);

        JSONObject rootJsonObject = new JSONObject();
        JSONObject srHeaderDetails = new JSONObject();
        JSONObject srBatchDetails=new JSONObject();
        JSONArray returnProductArray=new JSONArray();
        JSONArray saleDetailsArray =new JSONArray();
        JSONObject returnProductObject=new JSONObject();
        JSONArray srBatchArray=new JSONArray();
        JSONObject saleObject =new JSONObject();

        salesReturnNo = SalesReturnList.salesReturnNo;


       // {"SRNumber":"","Mode":"I","SRDate":"20210827","customerCode":"C1006","customerName":"A S KUMAR","address":"","street":"","city":"",
        // "creditLimit":"0.000000","remark":"","currencyName":"Singapore Dollar","taxTotal":"3.50","subTotal":"50.00","total":"50.00",
        // "netTotal":"53.50","itemDiscount":"0.00","billDiscount":"0.00","totalDiscount":null,"billDiscountPercentage":"0","deliveryCode":"0",
        // "DelCustomerName":"","delAddress1":"","delAddress2":null,"delAddress3":null,"delPhoneNo":"","haveTax":"","taxType":"Z","taxPerc":"",
        // "taxCode":"ZR","currencyCode":"SGD","currencyValue":"","CurrencyRate":"1","status":"","postalCode":"","createUser":"User1",
        // "modifyUser":"User1","companyName":null,"stockUpdated":null,"SOType":null,"companyCode":"AATHI_LIVE_DB","locationCode":"HQ","DONo":null,
        // "signature":"",
        //
        // "PostingDeliveryOrderDetails":[{"companyCode":"AATHI_LIVE_DB","SRDate":"20210827","slNo":"1","productCode":"SKU-AAFZ-0001",
        // "productName":"ASHOKA BHATURA 325 GM","cartonQty":"5","unitQty":"0","qty":"5.0","pcsPerCarton":"1","price":"10.00","cartonPrice":"10.00",
        // "retailPrice":"10.00","total":"50.0","itemDiscount":"0","totalTax":"3.50","subTotal":"50.00","netTotal":"53.50","taxType":"Z","taxPerc":"",
        // "returnLQty":"0","returnQty":"0","focQty":"0","exchangeQty":"0","returnSubTotal":"0.0","returnNetTotal":"0.0","taxCode":"ZR","uomCode":"PCS",
        // "itemRemarks":"","locationCode":"01","createUser":"User1","modifyUser":"User1"}]}


        try {
            // Sales Header Add values

            JSONArray detailsArray=customerResponse.optJSONArray("responseData");
            JSONObject object=detailsArray.optJSONObject(0);

           /* if (activityFrom.equals("SREdit")){
                rootJsonObject.put("SRNumber", AddInvoiceActivity.editSoNumber);
                rootJsonObject.put("mode", "E");
                rootJsonObject.put("status", "O");
            }else {
                rootJsonObject.put("soNumber", "");
                rootJsonObject.put("mode", "I");
                rootJsonObject.put("status", "");
            }*/

            rootJsonObject.put("SRNumber", "");
            rootJsonObject.put("mode", "I");
            rootJsonObject.put("status", "");

            rootJsonObject.put("SRDate", currentDateString);
            rootJsonObject.put("customerCode", object.optString("customerCode"));
            rootJsonObject.put("customerName", object.optString("customerName"));
            rootJsonObject.put("address", object.optString("address"));
            rootJsonObject.put("street", object.optString("street"));
            rootJsonObject.put("city", object.optString("city"));
            rootJsonObject.put("creditLimit", object.optString("creditLimit"));
            rootJsonObject.put("remark", "");
            rootJsonObject.put("currencyName", "Singapore Dollar");
            rootJsonObject.put("total", netTotalValue);
            rootJsonObject.put("itemDiscount", "0.00");
            rootJsonObject.put("billDiscount", "0.00");
            rootJsonObject.put("billDiscountPercentage", "0.00");
            rootJsonObject.put("subTotal", subTotalValue);
            rootJsonObject.put("taxTotal", taxValue);
            rootJsonObject.put("netTotal", netTotalValue);
            rootJsonObject.put("DeliveryCode", SettingUtils.getDeliveryAddressCode());
            rootJsonObject.put("delCustomerName", "");
            rootJsonObject.put("currentDateTime",Utils.getCurrentDateTime());
            rootJsonObject.put("delAddress1", object.optString("delAddress1"));
            rootJsonObject.put("delAddress2 ", object.optString("delAddress2"));
            rootJsonObject.put("delAddress3 ", object.optString("delAddress3"));
            rootJsonObject.put("delPhoneNo", object.optString("contactNo"));
            rootJsonObject.put("haveTax", object.optString("haveTax"));
            rootJsonObject.put("taxType", object.optString("taxType"));
            rootJsonObject.put("taxPerc", object.optString("taxPerc"));
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
                saleObject.put("total",Utils.twoDecimalPoint(Double.parseDouble(model.getCART_TOTAL_VALUE())));
                saleObject.put("itemDiscount",model.getDiscount());
                saleObject.put("itemDiscountPercentage","0");
                saleObject.put("totalTax",model.getCART_TAX_VALUE());
                saleObject.put("subTotal",Utils.twoDecimalPoint(Double.parseDouble(model.getSubTotal())));
                saleObject.put("netTotal",Utils.twoDecimalPoint(Double.parseDouble(model.getCART_COLUMN_NET_PRICE())));
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
                saleObject.put("DamageStock",model.getReturn_type());
                saleObject.put("itemRemarks","");
                saleObject.put("locationCode",locationCode);
                saleObject.put("createUser",userName);
                saleObject.put("modifyUser",userName);


                ArrayList<ReturnProductsModel> returnProducts=dbHelper.getReturnProducts(model.getCART_COLUMN_PID());
                returnProductArray=new JSONArray();
                if (returnProducts.size() > 0){
                    for (ReturnProductsModel returnProductsModel:returnProducts){
                        Log.w("ReturnProductsValues:",returnProductsModel.getProductCode()+"-"+returnProductsModel.getProductName()+"--"+returnProductsModel.getReturnQty());
                        if (Integer.parseInt(returnProductsModel.getReturnQty()) > 0){
                            returnProductObject=new JSONObject();
                            returnProductObject.put("ReturnReason",returnProductsModel.getReturnReason());
                            returnProductObject.put("ReturnQty",returnProductsModel.getReturnQty());
                            returnProductArray.put(returnProductObject);
                        }
                    }
                }
                saleObject.put("ReturnDetails",returnProductArray);
                saleDetailsArray.put(saleObject);
                index++;
            }

            rootJsonObject.put("PostingSalesReturnDetails",saleDetailsArray);
            Log.w("RootSalesReturn:",rootJsonObject.toString());

            saveSalesReturn(rootJsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("Given_Error:", Objects.requireNonNull(e.getMessage()));
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

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();
        for (Object perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
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

    public static void saveSalesReturn(JSONObject jsonBody){
        try {
            pDialog = new SweetAlertDialog(myContext, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Saving Sales Return...");
            pDialog.setCancelable(false);
            pDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(myContext);
            Log.w("GivenSalesReturn:",jsonBody.toString());
            String URL=Utils.getBaseUrl(myContext)+"PostingSalesReturn";
            Log.w("Given_SalesReturnApi:",URL);
            JsonObjectRequest salesOrderRequest = new JsonObjectRequest(Request.Method.POST, URL,jsonBody, response -> {
                Log.w("Sales_returnResponse:",response.toString());
                try {
                 //   {"statusCode":1,"statusMessage":"Sales Return Created Successfully","responseData":{"docNum":"1000005","error":null}}
                    String statusCode=response.optString("statusCode");
                    String  statusMessage=response.optString("statusMessage");
                    JSONObject jsonObject=response.optJSONObject("responseData");
                    String salesReturnNumber=jsonObject.optString("docNum");
                    if (statusCode.equals("1")){
                        dbHelper.removeAllItems();
                        dbHelper.removeCustomer();
                        dbHelper.removeAllReturn();
                        Utils.clearCustomerSession(myContext);
                        AppUtils.setProductsList(null);
                        pDialog.dismiss();
                        SalesReturnList.isEdit=false;
                        if (isPrintEnable){
                            Intent intent=new Intent(myContext,SalesReturnActivity.class);
                            intent.putExtra("srNumber",salesReturnNumber);
                            intent.putExtra("noOfCopy",String.valueOf(noofCopyPrint));
                            myContext.startActivity(intent);
                            myContext.finish();
                        }else {
                            Intent intent=new Intent(myContext,SalesReturnActivity.class);
                            myContext.startActivity(intent);
                            myContext.finish();
                        }
                    }else {
                        pDialog.dismiss();
                        Toast.makeText(myContext,statusMessage,Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.w("SR_Error:",error.toString());
                pDialog.dismiss();
            }) {
                @Override
                public byte[] getBody() {
                    return jsonBody.toString().getBytes();
                }
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
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
                                SummaryFragment.customerResponse=response;
                                customerResponse=response;
                                customerObject=response;
                                 setAllValues(response);
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
          //  showAlertDialog();
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

    public void setAllValues(JSONObject customerResponse){
        JSONArray detailsArray=customerResponse.optJSONArray("responseData");
        if (detailsArray!=null && detailsArray.length()>0){
            JSONObject object=detailsArray.optJSONObject(0);
            customerName.setText(object.optString("customerName"));
            address1.setText(object.optString("address"));
            setCalculation();
        }
    }
}