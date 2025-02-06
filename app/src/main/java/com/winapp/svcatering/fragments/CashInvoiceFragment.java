package com.winapp.svcatering.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.CashCollectionActivity;
import com.winapp.svcatering.activity.NewInvoiceListActivity;
import com.winapp.svcatering.adapter.NewCashCollectionAdapter;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.receipts.ReceiptPrintPreviewModel;
import com.winapp.svcatering.adapter.SalesReturnAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CashCollectionInvoiceModel;
import com.winapp.svcatering.model.SalesReturnModel;
import com.winapp.svcatering.receipts.ReceiptsModel;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CashInvoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CashInvoiceFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private static RecyclerView cashInvoiceView;
   // private static CashCollectionInvoiceAdapter cashCollectionInvoiceAdapter;
    public static NewCashCollectionAdapter cashCollectionInvoiceAdapter;
    public static CheckBox cashCheck;
    static ArrayList<CashCollectionInvoiceModel> cashInvoices;
    View view;
    static SweetAlertDialog pDialog;
    private SessionManager session;
    private HashMap<String,String > user;
    private static String companyId;
    private static String customerCode;
    private static String userName;
    private static String locationCode;
    private static String currentDate;
    public static String amountpayable;
    public static DBHelper dbHelper;
    private static FragmentActivity myContext;
    static double discount=0.0;
    static double paid_amount=0.0;
    public String selectedCustomerCode;
    public static LinearLayout emptyLayout;
    public static String currentDateString;
    public static LinearLayout salesReturnLayout;
    ImageView closeLayout;
    static RecyclerView salesReturnView;
    public static ArrayList<SalesReturnModel> salesReturnList;
    private static LinearLayoutManager mLinearLayoutManager;
    static BottomSheetBehavior behavior;
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
    static SalesReturnAdapter salesReturnAdapter;
    public static Parcelable recyclerViewState;
    public boolean isReceiptPrintCheck=false;

    public static ArrayList<ReceiptPrintPreviewModel> receiptsHeaderDetails;
    public static ArrayList<ReceiptsModel> receiptsList;
    public static ArrayList<ReceiptPrintPreviewModel.ReceiptsDetails> receiptsPrintList;
    public static String printerMacId;
    public static String printerType;
    public static SharedPreferences sharedPreferences;
    public String receiptNo;
    public String noofCopy;


    public CashInvoiceFragment(){

    }

    public CashInvoiceFragment(String customerCode) {
        this.customerCode=customerCode;
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CashInvoiceFragment.
     */
    public static CashInvoiceFragment newInstance(String param1, String param2) {
        //  CashInvoiceFragment fragment = new CashInvoiceFragment();
        //   Bundle args = new Bundle();
        //  args.putString(ARG_PARAM1, param1);
        //  args.putString(ARG_PARAM2, param2);
        //  fragment.setArguments(args);
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view=inflater.inflate((R.layout.fragment_cash_invoice),container,false);
        dbHelper=new DBHelper(getActivity());
        cashInvoiceView=view.findViewById(R.id.cash_invoice);
        cashCheck=view.findViewById(R.id.cash_check);
        emptyLayout=view.findViewById(R.id.empty_layout);
        cashInvoices=new ArrayList<>();
        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        userName=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        salesReturnLayout=view.findViewById(R.id.sales_return_layout);
        closeLayout=view.findViewById(R.id.close_layout);
        salesReturnView=view.findViewById(R.id.sales_return_view);
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
        salesReturnLayout.setVisibility(View.GONE);

        sharedPreferences = myContext.getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        Log.w("Printer_Mac_Id:",printerMacId);
        Log.w("Printer_Type:",printerType);


        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDateString = df.format(c);

        SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = df1.format(c);

        cashInvoices=dbHelper.getAllInvoices();

        if (cashInvoices.size()>0){
            selectedCustomerCode=cashInvoices.get(0).getCustomerCode();
            if (selectedCustomerCode.equals(customerCode)){
                setInvoiceAdapter(cashInvoices);
            }else {
                JSONObject jsonObject=new JSONObject();
                try {
                   // jsonObject.put("CompanyCode",companyId);
                    jsonObject.put("CustomerCode",customerCode);
                    jsonObject.put("LocationCode",locationCode);
                    getCashCollectionInvoice(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else {
            JSONObject jsonObject=new JSONObject();
            try {
                //jsonObject.put("CompanyCode",companyId);
                jsonObject.put("CustomerCode",customerCode);
                jsonObject.put("LocationCode",locationCode);
                getCashCollectionInvoice(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayList<SettingsModel> settings=dbHelper.getSettings();
        if (settings!=null) {
            if (settings.size() > 0) {
                for (SettingsModel model : settings) {
                    if (model.getSettingName().equals("receiptSwitch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            isReceiptPrintCheck=true;
                        }else {
                            isReceiptPrintCheck=false;
                        }
                    }
                }
            }
        }

      /*  JSONObject object =new JSONObject();
        try {
            object.put("CompanyCode",companyId);
            object.put("CustomerCode",customerCode);
            getSalesReturn(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        salesReturnList=dbHelper.getAllSalesReturn();
        if (salesReturnList.size()>0){
            String selectedCustomerCode=salesReturnList.get(0).getCustomerCode();
            if (selectedCustomerCode!=null){
                if (selectedCustomerCode.equals(customerCode)){
                    setSalesReturnAdapter(salesReturnList);
                }else {
                    JSONObject object =new JSONObject();
                    try {
                        object.put("CompanyCode",companyId);
                        object.put("CustomerCode",customerCode);
                        //getSalesReturn(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else {
            JSONObject object =new JSONObject();
            try {
                object.put("CompanyCode",companyId);
                object.put("CustomerCode",customerCode);
               // getSalesReturn(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        cashCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    checkAll("check");
                }else {
                    checkAll("uncheck");
                }
            }
        });

        closeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salesReturnLayout.setVisibility(View.GONE);
            }
        });

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

        invoicePrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    isPrintEnable=true;
                    copyLayout.setVisibility(View.VISIBLE);
                }else {
                    isPrintEnable=false;
                    copyText.setText("1");
                    copyLayout.setVisibility(View.GONE);
                    emailInvoice.setVisibility(View.GONE);
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

       /* yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noofCopyPrint=Integer.parseInt(copyText.getText().toString());
                try {
                    saveReceipt(noofCopyPrint);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                closeSheet();
            }
        });*/

        return view;
    }

    public static void showSalesReturnLayout(){
        if (salesReturnList.size()!=0){
            salesReturnLayout.setVisibility(View.VISIBLE);
        }else {
            Toast.makeText(myContext,"No Sales return found",Toast.LENGTH_SHORT).show();
        }
    }

    public static void reloadFunction(){
        cashInvoices=new ArrayList<>();
        cashInvoices=dbHelper.getAllInvoices();
        if (cashInvoices.size()>0){
            String selectedCustomerCode=cashInvoices.get(0).getCustomerCode();
            if (selectedCustomerCode.equals(customerCode)){
                setInvoiceAdapter(cashInvoices);
            }else {
                JSONObject jsonObject=new JSONObject();
                try {
                    // jsonObject.put("CompanyCode",companyId);
                    jsonObject.put("CustomerCode",customerCode);
                    jsonObject.put("LocationCode",locationCode);
                    getCashCollectionInvoice(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else {
            JSONObject jsonObject=new JSONObject();
            try {
              //  jsonObject.put("CompanyCode",companyId);
                jsonObject.put("CustomerCode",customerCode);
                jsonObject.put("LocationCode",locationCode);
                getCashCollectionInvoice(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void resetValues(){
        JSONObject object =new JSONObject();
        try {
            object.put("CompanyCode",companyId);
            object.put("CustomerCode",customerCode);
            getSalesReturn(object);
            Toast.makeText(getActivity(),"Called here",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void viewCloseBottomSheet(){
        //  hideKeyboard();
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
        cashCollected.setVisibility(View.GONE);
        deliveryPrint.setVisibility(View.GONE);
        invoicePrint.setVisibility(View.VISIBLE);
        invoicePrint.setText("Receipt Print");
        viewCloseBottomSheet();
        saveAction=action;
    }

    public static void checkAll(String action){

        try {
            if (action.equals("check")){
                for (CashCollectionInvoiceModel model:cashInvoices){
                    double paid_amt=0.0;
                    double net_amt=0.0;
                    double balance=0.0;
                    double discount=0.0;
                    double discount_amt=0.0;
                    model.setChecked("true");
                    model.setDisabledDiscount(false);
                    if (!model.getPaidAmount().isEmpty()){
                        paid_amt=Double.parseDouble(model.getPaidAmount());
                    }
                    if (!model.getNetTotal().isEmpty()){
                        net_amt=Double.parseDouble(model.getNetTotal());
                    }

                    if (model.getDiscount()!=null && !model.getDiscount().isEmpty()){
                        discount=Double.parseDouble(model.getDiscount());
                    }

                    if (model.getDiscountAmount().isEmpty()){
                        discount_amt=Double.parseDouble(model.getDiscountAmount());
                    }

                    balance=net_amt-balance-discount-paid_amt-discount_amt;
                    model.setPayable(String.valueOf(balance));
                    model.setDiscount("0.0");
                    model.setBalance("0.0");

                    if (model.getNetTotal()!=null && !model.getNetTotal().isEmpty()){
                        paid_amount+=Double.parseDouble(model.getPayable());
                    }
                    if (model.getDiscount()!=null && !model.getDiscount().isEmpty()){
                        discount+=Double.parseDouble(model.getDiscount());
                    }

                    // updated the Checked values to insert local db
                    dbHelper.updateInvoiceChecked(model.getInvoiceNumber(),"true");

                }
                //setCalculation();

               // CashCollectionActivity.totalPaid.setText(Utils.twoDecimalPoint(paid_amount));
               // CashCollectionActivity.totalDiscount.setText(Utils.twoDecimalPoint(discount));

              //  mLinearLayoutManager.scrollToPosition(cashInvoices.size() - 1); // yourList is the ArrayList that you are passing to your RecyclerView Adapter.
                setNetTotal();
            }else {
                for (CashCollectionInvoiceModel model:cashInvoices){
                    double paid_amt=0.0;
                    double net_amt=0.0;
                    double balance=0.0;
                    double discount=0.0;
                    model.setChecked("false");
                    model.setDisabledDiscount(true);
                    model.setPayable("");
                    model.setDiscount("");
                    if (!model.getPaidAmount().isEmpty()){
                        paid_amt=Double.parseDouble(model.getPaidAmount().toString());
                    }
                    if (!model.getNetTotal().isEmpty()){
                        net_amt=Double.parseDouble(model.getNetTotal());
                    }

                    if (!model.getDiscount().isEmpty()){
                        discount=Double.parseDouble(model.getDiscount());
                    }
                    balance=net_amt-balance-discount-paid_amt;
                    model.setBalance(Utils.twoDecimalPoint(balance));

                    // updated the Checked values to insert local db
                    dbHelper.updateInvoiceChecked(model.getInvoiceNumber(),"false");
                }
                //setReverseCalculation();
                cashCollectionInvoiceAdapter.notifyDataSetChanged();
                CashCollectionActivity.totalPaid.setText("");
                CashCollectionActivity.totalDiscount.setText("");
                dbHelper.removeAllInvoices();

                JSONObject jsonObject=new JSONObject();
                try {
                   // jsonObject.put("CompanyCode",companyId);
                    jsonObject.put("CustomerCode",customerCode);
                    jsonObject.put("LocationCode",locationCode);
                    getCashCollectionInvoice(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject object =new JSONObject();
                try {
                    object.put("CompanyCode",companyId);
                    object.put("CustomerCode",customerCode);
                   // getSalesReturn(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                CashCollectionActivity.totalPaid.setText("0.00");
                CashCollectionActivity.totalDiscount.setText("0.00");
                CashCollectionActivity.amountText.setText("0.00");
                CashCollectionActivity.selectedAmount.setText("0.00");
                CashCollectionActivity.returnAmountEditText.setText("0.00");

            }
        }catch (Exception ex){}
    }

    public static void refreshReturn(){
      /*  CashCollectionActivity.returnAmountEditText.setText("0.00");
        JSONObject object =new JSONObject();
        try {
            object.put("CompanyCode",companyId);
            object.put("CustomerCode",customerCode);
            getSalesReturn(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

      for ( SalesReturnModel model:salesReturnList){
          model.setIsCheked("false");
          model.setPaidAmount("0.00");
      }
      salesReturnAdapter.notifyDataSetChanged();
      CashCollectionActivity.returnAmountEditText.setText("0.00");

    }

    public static void setNetTotal(){
        double net_value=0.0;
        double net_discount=0.0;
        double net_outstanding=0.0;
        double paid_amount=0.0;

        double entered_net_amount=0.0;
        double difference_amount=0.0;
        double selected_amount=0.0;

        ArrayList<CashCollectionInvoiceModel> list=dbHelper.getAllInvoices();
        for(CashCollectionInvoiceModel model:list){

            Log.w("NetAmount_display:",model.getBalance());

            net_outstanding+=Double.parseDouble(model.getBalance());
            //if (model.isChecked()){
            net_value+=Double.parseDouble(model.getNetTotal());
            if (!model.getDiscount().isEmpty()){
                net_discount+=Double.parseDouble(model.getDiscount());
            }
            if (!model.getPayable().isEmpty()){
                paid_amount+=Double.parseDouble(model.getPayable());
            }
            // }
        }
        CashCollectionActivity.totalPaid.setText(Utils.twoDecimalPoint(paid_amount));
        CashCollectionActivity.amountText.setText(Utils.twoDecimalPoint(paid_amount));

        CashCollectionActivity.selectedAmount.setText(Utils.twoDecimalPoint(paid_amount));
        CashCollectionActivity.totalDiscount.setText(Utils.fourDecimalPoint(net_discount));
        CashCollectionActivity.totalOutstanding.setText(Utils.twoDecimalPoint(net_outstanding));

        if (!CashCollectionActivity.netAmount.getText().toString().isEmpty()){
            entered_net_amount=Double.parseDouble(CashCollectionActivity.netAmount.getText().toString());
        }

        if (!CashCollectionActivity.selectedAmount.getText().toString().isEmpty()){
            selected_amount=Double.parseDouble(CashCollectionActivity.selectedAmount.getText().toString());
        }

        if (!CashCollectionActivity.differenceAmount.getText().toString().isEmpty()){
            difference_amount=Double.parseDouble(CashCollectionActivity.differenceAmount.getText().toString());
        }

        difference_amount=entered_net_amount-selected_amount;
        CashCollectionActivity.differenceAmount.setText(Utils.twoDecimalPoint(difference_amount));

        cashCollectionInvoiceAdapter.notifyDataSetChanged();
    }


   /* public static void splitValues(String netamount){
        try {
            double net_value=Double.parseDouble(netamount);
            int index=cashInvoices.size()-1;
            for(int i=cashInvoices.size(); i>1; i--){
                System.out.println("The value of i is: "+i);
            }
            for (CashCollectionInvoiceModel model:cashInvoices){
                if (net_value>0){
                    if (Double.parseDouble(cashInvoices.get(index).getNetTotal()) > net_value){
                        model.setChecked(true);
                        model.setPayable(Utils.twoDecimalPoint(net_value));
                        net_value=0;
                    }else if (Double.parseDouble(cashInvoices.get(index).getNetTotal())< net_value){
                        model.setChecked(true);

                        double paid=net_value - Double.parseDouble(cashInvoices.get(index).getNetTotal());
                        model.setPayable(Utils.twoDecimalPoint(Double.parseDouble(cashInvoices.get(index).getNetTotal())));
                        net_value=paid;
                    }
                }
                index--;
            }
            cashCollectionInvoiceAdapter.notifyDataSetChanged();
            CashCollectionActivity.netAmount.setText("");
            CashCollectionActivity.netAmount.clearFocus();
        }catch (Exception ex){}
    }*/

    /*public static void splitInvoices(){
       // CashCollectionActivity.netAmount.setText("");
        CashCollectionActivity.netAmount.clearFocus();
        InputMethodManager imm = (InputMethodManager) myContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(CashCollectionActivity.netAmount.getWindowToken(), 0);
        int paidAmount = Integer.parseInt(CashCollectionActivity.netAmount.getText().toString());
        if(cashInvoices.size() >0) {
            //  Collections.reverse(sapSplitCashModelList);
            double tempPaidAmount = paidAmount;
            for (int i = cashInvoices.size() - 1; i >= 0; i--) {
                CashCollectionInvoiceModel cashmodel = cashInvoices.get(i);
                cashmodel.setPayable("");
                cashmodel.setBalance(cashmodel.getNetTotal());
                cashmodel.setIsbackground(false);
                cashmodel.setIseditable(false);
                cashmodel.setIsselect(false);

                if (tempPaidAmount > 0) {
                    if (tempPaidAmount > Double.parseDouble(cashmodel.getNetTotal())) {
                        tempPaidAmount = tempPaidAmount - Double.parseDouble(cashmodel.getNetTotal());
                        cashmodel.setPayable(cashmodel.getNetTotal());
                        cashmodel.setIsbackground(true);
                        cashmodel.setIseditable(true);
                        cashmodel.setIsselect(true);
                        cashmodel.setBalance("0.00");
                    } else {
                        double balance = Double.parseDouble(cashmodel.getNetTotal()) - tempPaidAmount;
                        cashmodel.setPayable(String.valueOf(tempPaidAmount));
                        cashmodel.setBalance(String.valueOf(balance));
                        cashmodel.setIsbackground(true);
                        cashmodel.setIseditable(true);
                        tempPaidAmount = 0;
                    }

                }
            }
        }
        if (cashCollectionInvoiceAdapter != null)
            cashCollectionInvoiceAdapter.notifyDataSetChanged();
    }*/

    public static void splitInvoices(){
        try {
            CashCollectionActivity.netAmount.clearFocus();
            InputMethodManager imm = (InputMethodManager) myContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(CashCollectionActivity.netAmount.getWindowToken(), 0);
            double paidAmount = Double.parseDouble(CashCollectionActivity.netAmount.getText().toString());
            if(cashInvoices.size() >0) {
                double tempPaidAmount = paidAmount;
                for (int i = cashInvoices.size() - 1; i >= 0; i--) {
                    CashCollectionInvoiceModel cashmodel = cashInvoices.get(i);
                    cashmodel.setPayable("0.00");
                    cashmodel.setBalance(cashmodel.getNetBalance());
                    cashmodel.setIsbackground(false);
                    cashmodel.setIseditable(false);
                    Log.d("dplit ", " "+cashmodel.isPreSelect() +" "+cashmodel.getNetTotal());
                    if (tempPaidAmount > 0) {
                        if(cashmodel.isPreSelect()){
                            if (tempPaidAmount > Double.parseDouble(cashmodel.getNetBalance()) ) {
                                tempPaidAmount = tempPaidAmount - Double.parseDouble(cashmodel.getNetBalance());
                                cashmodel.setPayable(cashmodel.getNetBalance());
                                //     cashmodel.setIseditable(false);
                                //  cashmodel.setTextclr(true);
                                cashmodel.setBalance("0.00");
                            } else {
                                double balance = Double.parseDouble(cashmodel.getNetBalance()) - tempPaidAmount;
                                cashmodel.setPayable(String.valueOf(tempPaidAmount));
                                cashmodel.setBalance(Utils.twoDecimalPoint(balance));
                                tempPaidAmount = 0;
                            }
                        }
                    }
                }
                if(tempPaidAmount > 0){
                    for (int i = cashInvoices.size() - 1; i >= 0; i--) {
                        CashCollectionInvoiceModel cashmodel = cashInvoices.get(i);
                        if(!cashmodel.isPreSelect()){
                            if (tempPaidAmount > Double.parseDouble(cashmodel.getNetBalance()) ) {
                                tempPaidAmount = tempPaidAmount - Double.parseDouble(cashmodel.getNetBalance());
                                cashmodel.setPayable(cashmodel.getNetBalance());
                                // cashmodel.setIsselect(true);
                                // cashmodel.setTextclr(true);
                                cashmodel.setBalance("0.00");
                            } else {
                                double balance = Double.parseDouble(cashmodel.getNetBalance()) - tempPaidAmount;
                                cashmodel.setPayable(String.valueOf(tempPaidAmount));
                                cashmodel.setBalance(Utils.twoDecimalPoint(balance));
                                tempPaidAmount = 0;
                            }
                        }

                    }}
            }
            if (cashCollectionInvoiceAdapter != null)
                cashCollectionInvoiceAdapter.notifyDataSetChanged();

            cashInvoiceView.scrollToPosition(cashInvoices.size() - 1);
        }catch (Exception e){}
    }

    public static void clearDataValues(){
        try {
            /*ProgressDialog progressDialog=new ProgressDialog(myContext);
            progressDialog.setMessage("Processing...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            CashCollectionActivity.netAmount.setText("");
            CashCollectionActivity.netAmount.clearFocus();
            for (int i = cashInvoices.size() - 1; i >= 0; i--) {
                CashCollectionInvoiceModel cashmodel = cashInvoices.get(i);
                cashmodel.setPayable("");
                cashmodel.setBalance(cashmodel.getNetTotal());
                cashmodel.setIsbackground(false);
                cashmodel.setIseditable(false);
                cashmodel.setIsselect(false);
            }
            if (cashCollectionInvoiceAdapter != null)
                cashCollectionInvoiceAdapter.notifyDataSetChanged();

            progressDialog.dismiss();*/

            // Define the new Function for Reload Data
            CashCollectionActivity.netAmount.setText("");
            CashCollectionActivity.netAmount.clearFocus();
            CashCollectionActivity.selectedAmount.setText("0.00");
            CashCollectionActivity.differenceAmount.setText("0.00");
            CashCollectionActivity.totalPaid.setText("0.00");
            CashCollectionActivity.totalDiscount.setText("0.00");
            CashCollectionActivity.returnAmountEditText.setText("0.00");
            reloadFunction();

        }catch (Exception e){}

    }


    public static void splitValues(String netamount){
        try {
            double net_payable_amount=0;
            for (CashCollectionInvoiceModel m:cashInvoices){
             net_payable_amount+=Double.parseDouble(m.getNetTotal());
            }
            if (net_payable_amount <  Double.parseDouble(netamount)){
                Toast.makeText(myContext,"Amount Exceed",Toast.LENGTH_LONG).show();
            }else {
                double net_value=Double.parseDouble(netamount);
                int index=0;
                for (CashCollectionInvoiceModel model:cashInvoices){
                    if (net_value>0){
                        if (Double.parseDouble(model.getNetTotal()) > net_value){
                            model.setChecked("true");
                            model.setPayable(Utils.twoDecimalPoint(net_value));
                            net_value=0;
                        }else if (Double.parseDouble(model.getNetTotal())< net_value){
                            model.setChecked("true");

                            double paid=net_value - Double.parseDouble(model.getNetTotal());
                            model.setPayable(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));
                            net_value=paid;
                        }
                    }
                    index++;
                }
                cashCollectionInvoiceAdapter.notifyDataSetChanged();
                CashCollectionActivity.netAmount.setText("");
                CashCollectionActivity.netAmount.clearFocus();
            }
            cashInvoiceView.scrollToPosition(cashInvoices.size() - 1);
        }catch (Exception ex){}
    }


    public static void amountCalculation(double amt){
        paid_amount+=amt;
        CashCollectionActivity.amountText.setText(Utils.twoDecimalPoint(paid_amount));
        CashCollectionActivity.selectedAmount.setText(Utils.twoDecimalPoint(paid_amount));
        amountpayable=Utils.twoDecimalPoint(paid_amount);
    }

    public static void getCashCollectionInvoice(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(myContext);
        // Initialize a new JsonArrayRequest instance
        String url=Utils.getBaseUrl(myContext) +"OutstandingInvoiceList";
        Log.w("Given_url_cashl:",url+jsonObject);
        pDialog = new SweetAlertDialog(myContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting Invoice Details...");
        pDialog.setCancelable(false);
        pDialog.show();
        cashInvoices=new ArrayList<>();
        final double[] netOutstanding = {0.0};
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject,
                response -> {
                    try{
                        Log.w("_CashCollection:",response.toString());
                        pDialog.dismiss();
                        dbHelper.removeAllInvoices();
                        if (response.length()>0) {
                            String statusCode=response.optString("statusCode");
                            if (statusCode.equals("1")){
                                JSONArray invoiceArray=response.optJSONArray("responseData");
                               // {"invoiceNo":"16","invoiceDate":"5\/8\/2021 12:00:00 am","transDate":"5\/8\/2021 12:00:00 am","transactionType":"INV",
                                // "status":"O","paidAmount":"0.000000","total":"15321330.000000","balanceAmount":"15321330.000000","customerCode":"WinApp",
                                // "customerName":"WinApp","fTotal":"0.000000","totalDiscount":"0.000000","taxRate":"0.000000","taxAmount":"1002330.000000",
                                // "remarks":"","currencyCode":"SGD","createDate":"5\/8\/2021 12:00:00 am","modifiedDate":"5\/8\/2021 12:00:00 am","address1":"",
                                // "dueDate":"5\/8\/2021 12:00:00 am","fPaidAmount":"0.000000","fNetTotal":"0.000000","fBalanceAmount":"0.000000",
                                // "customerGroupCode":"100"}
                                for (int i = 0; i< Objects.requireNonNull(invoiceArray).length(); i++){
                                    JSONObject object=invoiceArray.optJSONObject(i);
                                    CashCollectionInvoiceModel model=new CashCollectionInvoiceModel();
                                    model.setInvoiceNumber(object.optString("invoiceNo"));
                                    model.setNetTotal(object.optString("total"));
                                    model.setPaidAmount(object.optString("paidAmount"));
                                    model.setDiscountAmount(object.optString("totalDiscount"));
                                    model.setBalance(object.optString("balanceAmount"));
                                    model.setNetBalance(object.optString("balanceAmount"));
                                    model.setInvoiceDate(object.optString("invoiceDate"));
                                    model.setCustomerCode(object.optString("customerCode"));
                                    model.setInvoiceCode(object.optString("code"));
                                    if (object.optString("transactionType").equals("RE")){
                                        model.setTranType("Excess");
                                    }else {
                                        model.setTranType(object.optString("transactionType"));
                                    }
                                    model.setDisabledDiscount(true);
                                    model.setChecked("false");

                                    netOutstanding[0] +=Double.parseDouble(object.optString("balanceAmount"));

                                    cashInvoices.add(model);
                                }
                            }else {

                            }
                        }
                        CashCollectionActivity.totalOutstanding.setText(Utils.twoDecimalPoint(netOutstanding[0]));
                        setInvoiceAdapter(cashInvoices);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
          //  Toast.makeText(myContext,"Server Error, Please Try Again",Toast.LENGTH_LONG).show();
            showAlert();
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
        jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(jsonArrayRequest);
    }

    public static void showAlert(){
        AlertDialog.Builder builder=new AlertDialog.Builder(myContext);
        builder.setCancelable(false);
       // builder.setTitle("Information");
        builder.setMessage("Whoops! Server Error,Try again..");
        builder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                myContext.finish();
                myContext.startActivity(new Intent(myContext,CashCollectionActivity.class));
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                myContext.finish();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private static void setInvoiceAdapter(ArrayList<CashCollectionInvoiceModel> cashInvoices) {
        if (cashInvoices.size()>0){
            emptyLayout.setVisibility(View.GONE);
            cashInvoiceView.setVisibility(View.VISIBLE);
            cashInvoiceView.setNestedScrollingEnabled(true);
            CashCollectionActivity.netAmount.setEnabled(true);
            CashCollectionActivity.selectedBank.setEnabled(true);
            CashCollectionActivity.paymentMethod.setEnabled(true);
            CashCollectionActivity.btnSplit.setEnabled(true);
            CashCollectionActivity.btnSplit.setAlpha(0.9F);
            cashCheck.setEnabled(true);
            int count=0;
            for (CashCollectionInvoiceModel model:cashInvoices){
                if (model.getChecked().equals("true")){
                    count++;
                    if (count==cashInvoices.size()){
                        cashCheck.setChecked(true);
                    }else {
                        cashCheck.setChecked(false);
                    }
                }
            }







// Save state

//            recyclerViewState =cashInvoiceView.getLayoutManager().onSaveInstanceState();

// Restore state
         //   cashInvoiceView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

            mLinearLayoutManager = new LinearLayoutManager(myContext);
            cashInvoiceView.setLayoutManager(mLinearLayoutManager);
           // cashInvoiceView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            cashCollectionInvoiceAdapter=new NewCashCollectionAdapter(myContext,cashInvoices, new NewCashCollectionAdapter.CallBack() {
                @Override
                public void setPaidTotal(double netvalue,double netdiscount,double total_outstanding) {
                    CashCollectionActivity.totalPaid.setText(Utils.twoDecimalPoint(netvalue));
                    CashCollectionActivity.totalDiscount.setText(Utils.fourDecimalPoint(netdiscount));
                    CashCollectionActivity.totalOutstanding.setText(Utils.twoDecimalPoint(total_outstanding));
                }
                @Override
                public void refreshData() {
                    cashInvoiceView.post(new Runnable() {
                        @Override
                        public void run() {
                            cashCollectionInvoiceAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
            cashInvoiceView.setAdapter(cashCollectionInvoiceAdapter);
        }else {
            CashCollectionActivity.netAmount.setEnabled(false);
            CashCollectionActivity.selectedBank.setEnabled(false);
            CashCollectionActivity.paymentMethod.setEnabled(false);
            CashCollectionActivity.btnSplit.setEnabled(false);
            CashCollectionActivity.btnSplit.setAlpha(0.5F);
            cashCheck.setEnabled(false);
            emptyLayout.setVisibility(View.VISIBLE);
            cashInvoiceView.setVisibility(View.GONE);
        }
    }

    public static void getSalesReturn(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(myContext));
        // Initialize a new JsonArrayRequest instance
        String url=Utils.getBaseUrl(myContext) +"SalesApi/GetAllSalesReturn?Requestdata="+jsonObject.toString();
        Log.w("Given_url:",url);
        Log.w("Enter","SalesReturn");
        salesReturnList=new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Sales_Return_values:",response.toString());
                        if (response.length()>0) {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object=response.getJSONObject(i);
                                SalesReturnModel model=new SalesReturnModel();
                                model.setSalesReturnNumber(object.optString("SalesReturnNo"));
                                model.setSalesReturnDate(object.optString("SalesReturnDateString"));
                                model.setPaidAmount(object.optString("PaidAmount"));
                                model.setBalanceAmount(object.optString("BalanceAmount"));
                                model.setIsCheked("false");
                                model.setCustomerCode(object.optString("CustomerCode"));
                                salesReturnList.add(model);
                            }
                        }

                        clearData();
                        salesReturnLayout.setVisibility(View.GONE);
                        if (salesReturnList.size()>0){
                            dbHelper.insertSalesReturn(salesReturnList);
                            setSalesReturnAdapter(salesReturnList);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
            Toast.makeText(myContext,error.toString(),Toast.LENGTH_LONG).show();
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
        jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(jsonArrayRequest);
    }

    public static void clearData(){
        dbHelper.removeAllSalesReturn();
    }

    public static void setSalesReturnAdapter(ArrayList<SalesReturnModel> salesReturnList){
        if (salesReturnList.size()>0){
            salesReturnView.setHasFixedSize(true);
            salesReturnLayout.setVisibility(View.GONE);
            // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            salesReturnView.setLayoutManager(new LinearLayoutManager(myContext, LinearLayoutManager.VERTICAL, false));
            salesReturnAdapter = new SalesReturnAdapter(myContext,salesReturnList);
            salesReturnView.setAdapter(salesReturnAdapter);
            salesReturnLayout.setVisibility(View.VISIBLE);
        }else {
            salesReturnLayout.setVisibility(View.GONE);
        }
    }

/*
    public static void saveReceipt(int noofCopyPrint) throws JSONException {

        JSONObject rootObject=new JSONObject();
        JSONObject receiptHeader=new JSONObject();
        JSONArray receiptDetailsArray=new JSONArray();
        JSONObject receiptDetails=new JSONObject();

        if(CashCollectionActivity.payMethod.equals("CHEQUE")){
            receiptHeader.put("Paymode","Cheque");
            receiptHeader.put("SlipNo",null);
            receiptHeader.put("BankCode",CashCollectionActivity.bankCode.getText().toString());
            receiptHeader.put("ChequeNo",CashCollectionActivity.chequeNo.getText().toString());
            receiptHeader.put("ChequeDate",CashCollectionActivity.dueDateEdittext.getText().toString());
            receiptHeader.put("ChequeDateString","");
            receiptHeader.put("BankInDate",null);
            receiptHeader.put("BankInDateString",null);
        }else {
            receiptHeader.put("Paymode","Cash");
            receiptHeader.put("SlipNo",null);
            receiptHeader.put("BankCode",null);
            receiptHeader.put("ChequeNo",null);
            receiptHeader.put("ChequeDate",null);
            receiptHeader.put("BankInDate",null);
            receiptHeader.put("BankInDateString",null);
            receiptHeader.put("ChequeDateString","");
        }

        ArrayList<CashCollectionInvoiceModel> cashReceipts=dbHelper.getAllInvoices();
        double net_paid_amount=0.0;
        for (CashCollectionInvoiceModel model:cashReceipts){
            if (!model.getPayable().isEmpty())
            net_paid_amount+=Double.parseDouble(model.getPayable());
        }


      //  "{\"ReceiptsHeader\":{\"ReceiptNo\":\"\",\"ReceiptDate\":\"\\\/Date(1611072000000)\\\/\",\"CompanyCode\":1,
        //  \"CreateUser\":\"winapp\",\"CreateDate\":\"\\\/Date(1611131241708)\\\/\",\"ModifyUser\":\"winapp\",\"ModifyDate\":\"\\\/Date(1611131241708)\\\/\",
        //  \"PaidAmount\":87.25,\"CreditAmount\":0.00,\"CustomerCode\":\"703859\",\"CustomerName\":null,\"BankCode\":\"\",
        //  \"ChequeDate\":\"\\\/Date(1611072000000)\\\/\",\"ChequeNo\":\"\",\"Paymode\":\"CASH\",\"DepositAmount\":0.00,\"ChequeStatus\":null,
        //  \"Remarks\":\"\",\"BankInDate\":null,\"VerifiedWithBank\":null,\"VerifiedBy\":null,\"VerifiedDate\":null,\"Remarks2\":null,\
        //  "CurrencyCode\":\"SGD\",\"CurrencyRate\":1,\"FPaidAmount\":87.25,\"FCreditAmount\":0.00,\"FDepositAmount\":0.00,\"DebitAmount\":0.00,\
        //  "FDebitAmount\":0.00,\"FAdvancePaymentAmount\":0.00,\"IsClosed\":null,\"ClosedBy\":null,\"paymentType\":\"INV\",\"DeliveryAddress\":\"\",\
        //  "LocationCode\":\"HQ\",\"PayTo\":null,\"InvoiceDate\":null,\"InvoiceDateString\":null,\"AccountReceiptType\":null,
        //  \"Amount\":87.25,\"SlipNo\":null,\"BIDate\":null,\"NextReceiptNo\":null,\"ReceiptNextNo\":0,\"Mode\":\"I\",\"AccountNo\":\"123456\",\
        //  "CustomerAccountNo\":null,\"Description\":null,\"ErrorMessage\":null,\"AdvancePaymentAmount\":0.00,\"AccountEnabled\":null,\"BRNo\":null,
        //  \"Debit\":null,\"Credit\":null,\"InvoiceNo\":\"\",\"Discount\":null,\"BadDebts\":null,\"DepositNo\":\"\",\
        //  "ReceiptDateString\":\"20\/1\/2021\",\"ChequeDateString\":\"20\/1\/2021\",\"BankInDateString\":null,\
        //  "CurrencyName\":null,\"CurrencyValue\":null,\"ReferenceNo\":null,\"ClosedDate\":null,\"CompanyShortCode\":\"\",\"ReceiptsDetails\":null,
        //  \"DetPaid\":null,\"DetNetTotal\":null,\"DetDeposit\":null,\"DetCredit\":null,\"DetDebitAmount\":null,\"CustomerTypeCode\":null},\r\n\
        //
        //



        //New Implementation for saving the save Receipts:

        receiptHeader.put("ReceiptNo","");
        receiptHeader.put("ReceiptDate",currentDate);
        receiptHeader.put("CompanyCode", companyId);
        receiptHeader.put("CreateUser",userName);
        receiptHeader.put("CreateDate",currentDate);
        receiptHeader.put("ModifyUser",userName);
        receiptHeader.put("ModifyDate",currentDate);
        receiptHeader.put("PaidAmount",net_paid_amount);
        receiptHeader.put("CreditAmount",CashCollectionActivity.totalDiscount.getText().toString());
        receiptHeader.put("CustomerCode",customerCode);
        receiptHeader.put("CustomerName",CashCollectionActivity.customerName.getText().toString());
        receiptHeader.put("DepositAmount","0");
        receiptHeader.put("ChequeStatus",null);
        receiptHeader.put("Remarks","");
        receiptHeader.put("VerifiedWithBank","false");
        receiptHeader.put("VerifiedBy",null);
        receiptHeader.put("VerifiedDate",null);
        receiptHeader.put("Remarks2","");
        receiptHeader.put("CurrencyCode","SGD");
        receiptHeader.put("CurrencyRate","1");
        receiptHeader.put("FPaidAmount",net_paid_amount);
        receiptHeader.put("FCreditAmount","0.00");
        receiptHeader.put("FDepositAmount","0");
        receiptHeader.put("DebitAmount","0.00");
        receiptHeader.put("FDebitAmount","0");
        receiptHeader.put("FAdvancePaymentAmount","0.00");
        receiptHeader.put("IsClosed",null);
        receiptHeader.put("ClosedBy",null);
        receiptHeader.put("PaymentType","INV");
        receiptHeader.put("DeliveryAddress","");
        receiptHeader.put("LocationCode",locationCode);
        receiptHeader.put("PayTo",null);
        receiptHeader.put("InvoiceDate",null);
        receiptHeader.put("InvoiceDateString",null);
        receiptHeader.put("AccountReceiptType",null);
        receiptHeader.put("Amount",CashCollectionActivity.amountText.getText().toString());
        receiptHeader.put("SlipNo",null);
        receiptHeader.put("BIDate",null);
        receiptHeader.put("NextReceiptNo",null);
        receiptHeader.put("ReceiptNextNo","0");
        receiptHeader.put("Mode","I");
        receiptHeader.put("AccountNo","");
        receiptHeader.put("CustomerAccountNo",null) ;
        receiptHeader.put("Description",null);
        receiptHeader.put("ErrorMessage",null);
        receiptHeader.put("AdvancePaymentAmount","0.00");
        receiptHeader.put("AccountEnabled",null);
        receiptHeader.put("BRNo",null);
        receiptHeader.put("Debit",null);
        receiptHeader.put("Credit",null);
        receiptHeader.put("InvoiceNo","");
        receiptHeader.put("Discount",null);
        receiptHeader.put("BadDebts",null);
        receiptHeader.put("DepositNo","");
        receiptHeader.put("ReceiptDateString",currentDateString);
        receiptHeader.put("CurrencyName",null);
        receiptHeader.put("CurrencyValue",null);
        receiptHeader.put("ReferenceNo",null);
        receiptHeader.put("ClosedDate",null);
        receiptHeader.put("CompanyShortCode","");
        receiptHeader.put("ReceiptsDetails",null);
        receiptHeader.put("DetPaid",null);
        receiptHeader.put("DetNetTotal",null);
        receiptHeader.put("DetDeposit",null);
        receiptHeader.put("DetCredit",null);
        receiptHeader.put("DetDebitAmount",null);
        receiptHeader.put("CustomerTypeCode",null);


        ArrayList<CashCollectionInvoiceModel> cashReceiptList=dbHelper.getAllInvoices();

        for (int i=0;i<cashReceiptList.size();i++){

            if (!cashReceiptList.get(i).getPayable().isEmpty()){


                //  "ReceiptsDetail\":[{\"ReceiptNo\":null,\"PayTo\":null,\"InvoiceNo\":\"IN21-00115\",\
                //  "InvoiceDate\":null,\"InvoiceDateString\":null,\"CompanyCode\":1,\"NetTotal\":187.25,\"PaidAmount\":87.25,\
                //  "BalanceAmount\":87.25,\"FBalanceAmount\":null,\"CreditAmount\":0,\"DebitAmount\":0,\"DepositAmount\":0,\"DepositNo\":\"\",\
                //  "CreateUser\":\"winapp\",\"CreateDate\":\"\\\/Date(1611131242108)\\\/\",\"ModifyUser\":\"winapp\",
                //  \"ModifyDate\":\"\\\/Date(1611131242109)\\\/\",\"ErrorMessage\":null,\"AdvancePaymentAmount\":0,
                //  \"CustomerCode\":\"703859\",\"AccountEnabled\":null,\"AccountNo\":null,\"CurrencyCode\":\"SGD\",
                //  \"CurrencyRate\":1,\"CurrencyValue\":null,\"FNetTotal\":0,\"FPaidAmount\":87.25,\"FCreditAmount\":0.00,
                //  \"FDepositAmount\":0.00,\"FDebitAmount\":0.00,\"FAdvancePaymentAmount\":0.00,\"FGainOrLoss\":0.00,\"SONo\":\"\",
                //  \"paymentType\":\"INV\"}]}"

                receiptDetails=new JSONObject();
                receiptDetails.put("ReceiptNo", null);
                receiptDetails.put("PayTo", null);
                receiptDetails.put("InvoiceNo", cashReceiptList.get(i).getInvoiceNumber());
                receiptDetails.put("InvoiceDate",changeDateFormat(cashReceiptList.get(i).getInvoiceDate(),"MM/dd/yyyy") );
                receiptDetails.put("InvoiceDateString", changeDateFormat(cashReceiptList.get(i).getInvoiceDate(),"dd/mm/yyyy"));
                receiptDetails.put("CompanyCode", companyId);
                receiptDetails.put("NetTotal", cashReceiptList.get(i).getNetTotal());
                receiptDetails.put("PaidAmount", cashReceiptList.get(i).getPayable());
                receiptDetails.put("BalanceAmount",cashReceiptList.get(i).getPayable());
                receiptDetails.put("FBalanceAmount",null);
                receiptDetails.put("CreditAmount", cashReceiptList.get(i).getDiscount());
                receiptDetails.put("DebitAmount", "0");
                receiptDetails.put("DepositAmount", "0");
                receiptDetails.put("DepositNo", null);
                receiptDetails.put("CreateUser", userName);
                receiptDetails.put("CreateDate", currentDate);
                receiptDetails.put("ModifyUser", userName);
                receiptDetails.put("ModifyDate", currentDate);
                receiptDetails.put("ErrorMessage", null);
                receiptDetails.put("AdvancePaymentAmount", "0");
                receiptDetails.put("CustomerCode", customerCode);
                receiptDetails.put("AccountEnabled", null);
                receiptDetails.put("AccountNo", null);
                receiptDetails.put("CurrencyCode", "SGD");
                receiptDetails.put("CurrencyRate", "1");
                receiptDetails.put("CurrencyValue",null);
                receiptDetails.put("FNetTotal", "0");
                receiptDetails.put("FPaidAmount", cashReceiptList.get(i).getPayable());
                receiptDetails.put("FCreditAmount", "0");
                receiptDetails.put("FDepositAmount", "0");
                receiptDetails.put("FDebitAmount", "0");
                receiptDetails.put("FAdvancePaymentAmount", "0.00");
                receiptDetails.put("FGainOrLoss", "0.00");
                receiptDetails.put("SONo", "");
                receiptDetails.put("paymentType", "INV");

                receiptDetailsArray.put(receiptDetails);
            }
        }

        rootObject.put("ReceiptsHeader",receiptHeader);
        rootObject.put("ReceiptsDetail",receiptDetailsArray);
        String cashCollectionString= Utils.jsonToEscapeString(rootObject.toString());
        Log.w("EscapeString:",cashCollectionString);

        saveReceipts(cashCollectionString,noofCopyPrint);
    //    Intent intent=new Intent(myContext, PrinterActivity.class);
//        myContext.startActivity(intent);
    }
*/

    public static void saveReceipt(int noofCopyPrint,boolean isPrintEnable) throws JSONException, ParseException {

       /* {"ReceiptsHeader":{"ReceiptNo":"",
                "ReceiptDateString":"",
                "AccountNo":"",
                "CustomerCode":"",
                "CustomerName":"",
                "Paymode":"",
                "BankCode":"",
                "ChequeNo":"",
                "ChequeDateString":"",
                "paymentType":"INV",
                "CurrencyCode":"SGD",
                "CurrencyRate":"1",
                "InvoiceNo":"",
                "Status":0,
                "Mode":"I",
                "CompanyShortCode":"",
                "DepositNo":"",
                "CreditAmount":"0.00",
                "FCreditAmount":"0.00",
                "PaidAmount":"0.00",
                "FPaidAmount":"0.00",
                "DepositAmount":"0.00",
                "FDepositAmount":"0.00",
                "AdvancePaymentAmount":"0.00",
                "FAdvancePaymentAmount":"0.00",
                "Debitamount":"0.00",
                "FDebitAmount":"0.00",
                "DeliveryAddress":"",
                "WrightOff":"0.00",
                "TotalCustomerPaidAmount":"0.00",
                "ExcessPaidAmount":"0.00",
                "Balance":"0.00",
                "CompanyCode":1,
                "CreatUser":"winapp"
        },
            "ReceiptsDetail":[{ "CustomerCode":"","InvoiceNo": "","NetTotal":0.00,"BalanceAmount":0.00,"Paidamount":0.00, "Creditamount":0.00,"DepositAmount":0.00,"SONo": "","FPaidAmount": 0.00,"DebitAmount":0.00,"FCreditAmount":0.00, "FDebitAmount": 0.00, "FNetTotal": 0.00, "FDepositAmount":0.00,"FGainOrLoss": 0.00, "CurrencyCode": "", "paymentType": "", "CurrencyRate": "", "DepositNo": "", "AdvancePaymentAmount": 0.00, "FAdvancePaymentAmount": 0.00}],
            "SalesReturnAR":[{}],"ReceiptCreditDetail":[{}]}
*/
        JSONObject rootObject=new JSONObject();
        JSONObject receiptHeader=new JSONObject();
        JSONArray receiptDetailsArray=new JSONArray();
        JSONObject receiptDetails=new JSONObject();

      /*  {
            "ReceiptNo":"",
                "ReceiptDateString":"",
                "RefNo": "ref321",
                "AccountNo":"",
                "CustomerCode":"CUS/686",
                "CustomerName":"",
                "Paymode":"CASH",
                "BankCode":"",
                "ChequeNo":"",
                "ChequeDateString":"",
                "paymentType":"INV",
                "CurrencyCode":"SGD",
                "CurrencyRate":"1",
                "InvoiceNo":"",
                "Status":0,
                "Mode":"I",
                "CompanyShortCode":"",
                "DepositNo":"",
                "CreditAmount":"0.00",
                "FCreditAmount":"0.00",
                "PaidAmount":"0.00",
                "FPaidAmount":"0.00",
                "DepositAmount":"0.00",
                "FDepositAmount":"0.00",
                "AdvancePaymentAmount":"0.00",
                "FAdvancePaymentAmount":"0.00",
                "Debitamount":"0.00",
                "FDebitAmount":"0.00",
                "DeliveryAddress":"",
                "WrightOff":"0.00",
                "TotalCustomerPaidAmount":"0.00",
                "ExcessPaidAmount":"0.00",
                "Balance":"0.00",
                "CompanyCode":1,
                "CreatUser":"winapp",
                "Remark":"Mobile Integration Incoming Payment",
                "ReceiptsDetail":
[
            {
                "CustomerCode":"",
                    "InvoiceNo": "10",
                    "NetTotal":"321.00",
                    "BalanceAmount":0.00,
                    "Paidamount":0.00,
                    "Creditamount":0.00,
                    "DepositAmount":0.00,
                    "SONo": "",
                    "FPaidAmount": 0.00,
                    "DebitAmount":0.00,
                    "FCreditAmount":0.00,
                    "FDebitAmount": 0.00,
                    "FNetTotal": 0.00,
                    "FDepositAmount":0.00,
                    "FGainOrLoss": 0.00,
                    "CurrencyCode": "",
                    "paymentType": "",
                    "CurrencyRate": "",
                    "DepositNo": "",
                    "AdvancePaymentAmount": 0.00,
                    "FAdvancePaymentAmount": 0.00
            }
]
        }*/



        if(CashCollectionActivity.payMethod.equals("CHEQUE")){
            DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = originalFormat.parse(CashCollectionActivity.dueDateEdittext.getText().toString());
            String chequeDate= targetFormat.format(date);

            receiptHeader.put("Paymode","Cheque");
            receiptHeader.put("SlipNo","");
            receiptHeader.put("BankCode",CashCollectionActivity.bankCode.getText().toString());
            receiptHeader.put("ChequeNo",CashCollectionActivity.chequeNo.getText().toString());
            receiptHeader.put("ChequeDateString",chequeDate);
            receiptHeader.put("BankInDate","");
            receiptHeader.put("BankInDateString","");
        }else if (CashCollectionActivity.payMethod.equals("CASH")){
            receiptHeader.put("Paymode","Cash");
            receiptHeader.put("SlipNo","");
            receiptHeader.put("BankCode","");
            receiptHeader.put("ChequeNo","");
            receiptHeader.put("ChequeDate","");
            receiptHeader.put("BankInDate","");
            receiptHeader.put("BankInDateString","");
            receiptHeader.put("ChequeDateString","");
        }else {
            DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = originalFormat.parse(CashCollectionActivity.paymentDate.getText().toString());
            String paymentDate=targetFormat.format(date);

            receiptHeader.put("Paymode","BankTransfer");
            receiptHeader.put("SlipNo","");
            receiptHeader.put("BankCode","");
            receiptHeader.put("ChequeNo","");
            receiptHeader.put("ChequeDate","");
            receiptHeader.put("BankInDate","");
            receiptHeader.put("BankInDateString","");
            receiptHeader.put("ChequeDateString","");
            receiptHeader.put("BankPaymentType",CashCollectionActivity.paymentCode);
            receiptHeader.put("BankTransferDate",paymentDate);
        }

        ArrayList<CashCollectionInvoiceModel> cashReceipts=cashCollectionInvoiceAdapter.getList();
        double net_paid_amount=0.0;
        for (CashCollectionInvoiceModel model:cashReceipts){
            if (model.getPayable()!=null && !model.getPayable().isEmpty() && !model.getPayable().equals("null"))
                net_paid_amount+=Double.parseDouble(model.getPayable());
        }
        //New Implementation for saving the save Receipts:
        receiptHeader.put("ReceiptNo","");
        receiptHeader.put("RefNo",customerCode);
        receiptHeader.put("ReceiptDate",currentDate);
        receiptHeader.put("CompanyCode", companyId);
        receiptHeader.put("CreateUser",userName);
        receiptHeader.put("CreateDate",currentDate);
        receiptHeader.put("ModifyUser",userName);
        receiptHeader.put("ModifyDate",currentDate);
        receiptHeader.put("PaidAmount",CashCollectionActivity.selectedAmount.getText().toString());
        receiptHeader.put("CreditAmount",CashCollectionActivity.totalDiscount.getText().toString());
        receiptHeader.put("CustomerCode",customerCode);
        receiptHeader.put("CustomerName",CashCollectionActivity.customerName.getText().toString());
        receiptHeader.put("DepositAmount","0");
        receiptHeader.put("ChequeStatus","");
        receiptHeader.put("Status","0");
        receiptHeader.put("Remark","");
        receiptHeader.put("VerifiedWithBank","false");
        receiptHeader.put("VerifiedBy","");
        receiptHeader.put("VerifiedDate","");
        receiptHeader.put("CurrencyCode","SGD");
        receiptHeader.put("CurrencyRate","1");
        receiptHeader.put("FPaidAmount",net_paid_amount);
        receiptHeader.put("FCreditAmount",CashCollectionActivity.totalDiscount.getText().toString());
        receiptHeader.put("FDepositAmount","0");
        receiptHeader.put("DebitAmount","0.00");
        receiptHeader.put("FDebitAmount","0");
        receiptHeader.put("FAdvancePaymentAmount","0.00");
        receiptHeader.put("IsClosed","");
        receiptHeader.put("ClosedBy","");
        receiptHeader.put("PaymentType","INV");
        receiptHeader.put("DeliveryAddress","");
        receiptHeader.put("LocationCode",locationCode);
        receiptHeader.put("PayTo","");
        receiptHeader.put("InvoiceDate","");
        receiptHeader.put("InvoiceDateString","");
        receiptHeader.put("AccountReceiptType","");
        receiptHeader.put("SlipNo","");
        receiptHeader.put("BIDate","");
        receiptHeader.put("NextReceiptNo","");
        receiptHeader.put("ReceiptNextNo","0");
        receiptHeader.put("Mode","I");
        receiptHeader.put("AccountNo","");
        receiptHeader.put("CustomerAccountNo","") ;
        receiptHeader.put("Description","");
        receiptHeader.put("ErrorMessage","");
        receiptHeader.put("AccountEnabled","");
        receiptHeader.put("BRNo","");
        receiptHeader.put("Debit","");
        receiptHeader.put("Credit","");
        receiptHeader.put("InvoiceNo","");
        receiptHeader.put("Discount","");
        receiptHeader.put("BadDebts","");
        receiptHeader.put("DepositNo","");
        receiptHeader.put("ReceiptDateString",currentDateString);
        receiptHeader.put("CurrencyName","");
        receiptHeader.put("CurrencyValue","");
        receiptHeader.put("ReferenceNo","");
        receiptHeader.put("ClosedDate","");
        receiptHeader.put("CompanyShortCode","");
        receiptHeader.put("ReceiptsDetails","");
        receiptHeader.put("DetPaid","");
        receiptHeader.put("DetNetTotal","");
        receiptHeader.put("DetDeposit","");
        receiptHeader.put("DetCredit","");
        receiptHeader.put("DetDebitAmount","");
        receiptHeader.put("CustomerTypeCode","");
        receiptHeader.put("IsAdvancePaid",false);
        receiptHeader.put("TotalCustomerPaidAmount",CashCollectionActivity.netAmount.getText().toString());
        receiptHeader.put("ExcessPaidAmount",CashCollectionActivity.differenceAmount.getText().toString());
        receiptHeader.put("Balance",CashCollectionActivity.differenceAmount.getText().toString());
        receiptHeader.put("WrightOff","0.00");
        receiptHeader.put("AdvancePaymentAmount",CashCollectionActivity.differenceAmount.getText().toString());
        receiptHeader.put("Signature",Utils.getSignature());
        receiptHeader.put("RefPhoto",Utils.getSelectImage());

       // IsAdvancePaid=0,TotalCustomerPaidAmount=PaidAmount,ExcessPaidAmount=0,WrightOff=0,Balance=0

        ArrayList<CashCollectionInvoiceModel> cashReceiptList=cashCollectionInvoiceAdapter.getList();

        for (int i=0;i<cashReceiptList.size();i++){

            if (cashReceiptList.get(i).getPayable()!=null && !cashReceiptList.get(i).getPayable().isEmpty() && Double.parseDouble(cashReceiptList.get(i).getPayable())>0.00){

                receiptDetails=new JSONObject();
                receiptDetails.put("ReceiptNo", "");
                receiptDetails.put("PayTo", "");
                receiptDetails.put("InvoiceNo", cashReceiptList.get(i).getInvoiceCode());
                receiptDetails.put("InvoiceDate",
                        /*changeDateFormat(cashReceiptList.get(i).getInvoiceDate(),"dd/MM/yyyy")*/ cashReceiptList.get(i).getInvoiceDate());
                receiptDetails.put("InvoiceDateString",
                        /*changeDateFormat(cashReceiptList.get(i).getInvoiceDate(),"dd/MM/yyyy")*/cashReceiptList.get(i).getInvoiceDate());
                receiptDetails.put("CompanyCode", companyId);
                receiptDetails.put("NetTotal", cashReceiptList.get(i).getNetTotal());
                if (cashReceiptList.get(i).getTranType().equals("SR")){
                    receiptDetails.put("PaidAmount", "-"+cashReceiptList.get(i).getPayable());
                    receiptDetails.put("FBalanceAmount","-"+cashReceiptList.get(i).getPayable());
                }else {
                    receiptDetails.put("PaidAmount", cashReceiptList.get(i).getPayable());
                    receiptDetails.put("FBalanceAmount",cashReceiptList.get(i).getPayable());
                }
                //receiptDetails.put("BalanceAmount",cashReceiptList.get(i).getPayable());

                if (cashReceiptList.get(i).getDiscount()!=null && !cashReceiptList.get(i).getDiscount().equals("null") && !cashReceiptList.get(i).getDiscount().isEmpty()){
                    receiptDetails.put("CreditAmount", cashReceiptList.get(i).getDiscount());
                    receiptDetails.put("DiscountAmount",cashReceiptList.get(i).getDiscount());
                    receiptDetails.put("FCreditAmount", cashReceiptList.get(i).getDiscount());
                }else {
                    receiptDetails.put("CreditAmount", "0.00");
                    receiptDetails.put("FCreditAmount", "0.00");
                    receiptDetails.put("DiscountAmount","0.00");
                }
                receiptDetails.put("DebitAmount", "0");
                receiptDetails.put("DepositAmount", "0");
                receiptDetails.put("DepositNo", "");
                receiptDetails.put("CreateUser", userName);
                receiptDetails.put("CreateDate", currentDate);
                receiptDetails.put("ModifyUser", userName);
                receiptDetails.put("ModifyDate", currentDate);
                receiptDetails.put("ErrorMessage", "");
               // receiptDetails.put("AdvancePaymentAmount", "0");
                receiptDetails.put("CustomerCode", customerCode);
                receiptDetails.put("AccountEnabled", "");
                receiptDetails.put("AccountNo", "");
                receiptDetails.put("CurrencyCode", "SGD");
                receiptDetails.put("CurrencyRate", "1");
                receiptDetails.put("CurrencyValue","");
                receiptDetails.put("FNetTotal", cashReceiptList.get(i).getNetTotal());
                receiptDetails.put("FPaidAmount", cashReceiptList.get(i).getPayable());
                receiptDetails.put("FDepositAmount", "0");
                receiptDetails.put("FDebitAmount", "0");
               // receiptDetails.put("FAdvancePaymentAmount", "0.00");
                receiptDetails.put("FGainOrLoss", "0.00");
                receiptDetails.put("SONo", "");
                if (cashReceiptList.get(i).getTranType().equals("Excess")){
                    receiptDetails.put("paymentType", "RE");
                }else {
                    receiptDetails.put("paymentType", cashReceiptList.get(i).getTranType());
                }
                receiptDetailsArray.put(receiptDetails);
            }
        }

       // rootObject.put("ReceiptsHeader",receiptHeader);
        receiptHeader.put("ReceiptsDetail",receiptDetailsArray);
       // rootObject.put("ReturnArray","");

       // String cashCollectionString= Utils.jsonToEscapeString(rootObject.toString());

        Log.w("EscapeString:",receiptHeader.toString());

        Utils.setInvoiceMode("Invoice");
        Utils.setReceiptMode("true");
        saveReceipts(receiptHeader,noofCopyPrint,isPrintEnable);

    }

   // 16-10-2020
    public static String changeDateFormat(String date,String format){
        @SuppressLint("SimpleDateFormat")
        DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
        @SuppressLint("SimpleDateFormat")
        DateFormat outputFormat = new SimpleDateFormat(format);
        String resultDate = "";
        try {
            resultDate=outputFormat.format(inputFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultDate;
    }

    public static void saveReceipts(JSONObject jsonBody,int noofCopyPrint,boolean isPrint){
        try {
            pDialog = new SweetAlertDialog(myContext, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Saving Receipts...");
            pDialog.setCancelable(false);
            pDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(myContext);
            Log.w("GivenRequest:",jsonBody.toString());
            String URL=Utils.getBaseUrl(myContext)+"PostingReceipt";
            Log.w("Given_ReceiptsAPI:",URL);
            JsonObjectRequest receiptRequest = new JsonObjectRequest(Request.Method.POST, URL,jsonBody, response -> {
                Log.w("Save_receipt_response:",response.toString());
                try {
                    pDialog.dismiss();
                   // {"statusCode":1,"statusMessage":"Receipt \/ Incoming payment saved successfully","responseData":{"docNum":"13","error":null}}
                   // {"statusCode":2,"statusMessage":"Failed","responseData":{"docNum":null,"error":"Incoming Payment:Account for cash payments has not been defined"}}
                    String statusCode=response.optString("statusCode");
                    JSONObject responseData=response.optJSONObject("responseData");
                    if (statusCode.equals("1")){
                        JSONObject receiptObject=response.optJSONObject("responseData");
                        String receiptNumber=receiptObject.optString("docNum");
                        Toast.makeText(myContext,"Payment Success",Toast.LENGTH_LONG).show();
                        if (isPrint){
                            //  receiptNo =getIntent().getStringExtra("receiptNumber");
                            //  noofCopy=getIntent().getStringExtra("noOfCopy");
                            dbHelper.removeAllInvoices();
                          /*  Intent intent=new Intent(myContext, ReceiptsListActivity.class);
                            intent.putExtra("receiptNumber",receiptNumber);
                            intent.putExtra("noOfCopy",String.valueOf(noofCopyPrint));
                            myContext.startActivity(intent);
                            myContext.finish();*/

                            // Redirect to Invoice List Activity
                            getReceiptsDetails(receiptNumber,noofCopyPrint);
                            Intent intent=new Intent(myContext, NewInvoiceListActivity.class);
                            myContext.startActivity(intent);
                            myContext.finish();
                        }else {
                            dbHelper.removeAllInvoices();
                            pDialog.dismiss();
                          /*  Intent intent=new Intent(myContext, ReceiptsListActivity.class);
                            myContext.startActivity(intent);
                            myContext.finish();*/

                            // Redirect to Invoice List Activity
                            Intent intent=new Intent(myContext, NewInvoiceListActivity.class);
                            myContext.startActivity(intent);
                            myContext.finish();
                        }
                       // isPrint=false;
                    }else {
                        Toast.makeText(myContext,responseData.optString("error"),Toast.LENGTH_SHORT).show();
                    }
                  /*  JSONObject object=new JSONObject(response);
                    boolean isSaved=object.optBoolean("IsSaved");
                    String receiptNumber=object.optString("Result");
                    if (isSaved){
                        if (isPrintEnable){
                          //  receiptNo =getIntent().getStringExtra("receiptNumber");
                          //  noofCopy=getIntent().getStringExtra("noOfCopy");
                            dbHelper.removeAllInvoices();
                            pDialog.dismiss();
                            Intent intent=new Intent(myContext, ReceiptsListActivity.class);
                            intent.putExtra("receiptNumber",receiptNumber);
                            intent.putExtra("noOfCopy",String.valueOf(noofCopyPrint));
                            myContext.startActivity(intent);
                            myContext.finish();
                        }else {
                            dbHelper.removeAllInvoices();
                            pDialog.dismiss();
                            Intent intent=new Intent(myContext, ReceiptsListActivity.class);
                            myContext.startActivity(intent);
                            myContext.finish();
                        }
                        isPrintEnable=false;
                    }else {
                        Toast.makeText(myContext,"Error in Save Receipt",Toast.LENGTH_SHORT).show();
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.w("Receipt_Error_Response:",error.toString());
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
            receiptRequest.setRetryPolicy(new RetryPolicy() {
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
            requestQueue.add(receiptRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getReceiptsDetails(String receiptNumber,int copy) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject = new JSONObject();
        //   jsonObject.put("CompanyCode", companyId);
        jsonObject.put("ReceiptNo", receiptNumber);
        jsonObject.put("LocationCode",locationCode);
        RequestQueue requestQueue = Volley.newRequestQueue(myContext);
        String url = Utils.getBaseUrl(myContext) + "ReceiptDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url);
        Log.w("JsonObjectPrint:",jsonObject.toString());
        // pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        //  pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        //  pDialog.setTitleText("Getting Printing Data..");
        // pDialog.setCancelable(false);
        // pDialog.show();
        receiptsHeaderDetails = new ArrayList<>();
        receiptsList = new ArrayList<>();
        receiptsPrintList=new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        Log.w("Invoice_Details:", response.toString());

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
                            model.setCustomerCode(responseObject.optString("customerCode"));
                            model.setCustomerName(responseObject.optString("customerName"));
                            model.setTotalAmount(responseObject.optString("netTotal"));
                            model.setPaymentType(responseObject.optString("paymentType"));
                            model.setBankCode(responseObject.optString("bankCode"));
                            model.setBankName(responseObject.optString("bankName"));
                            model.setChequeDate(responseObject.optString("checkDueDate"));
                            model.setChequeNo(responseObject.optString("checkNumber"));
                            model.setBankTransferDate(responseObject.optString("bankTransferDate"));
                            model.setCreditAmount(responseObject.optString("totalDiscount"));
                            if (responseObject.optString("signature")!=null && !responseObject.optString("signature").equals("null") && !responseObject.optString("signature").isEmpty()){
                                String signature = responseObject.optString("signature");
                                Utils.setSignature(signature);
                                createSignature();
                                Log.w("signReturn",""+signature) ;
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

                                receiptsPrintList.add(invoiceListModel);
                            }
                            model.setReceiptsDetailsList(receiptsPrintList);
                            receiptsHeaderDetails.add(model);
                        }else {

                        }
                        printReceipt(copy);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            // pDialog.dismiss();
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

    public static void printReceipt(int copy) throws IOException {
        if (printerType.equals("TSC Printer")) {
            //  TSCPrinter tscPrinter = new TSCPrinter(ReceiptsPrintPreview.this, printerMacId);
            //  tscPrinter.printInvoice(receiptsHeaderDetails, receiptsList);
            TSCPrinter printer=new TSCPrinter(myContext,printerMacId,"Receipt");
            printer.printReceipts(copy,receiptsHeaderDetails,receiptsPrintList);
            printer.setOnCompletionListener(new TSCPrinter.OnCompletionListener() {
                @Override
                public void onCompleted() {
                    Utils.setSignature("");
                    Toast.makeText(myContext,"Receipt printed successfully!",Toast.LENGTH_SHORT).show();
                }
            });
        } else if (printerType.equals("Zebra Printer")) {
            ZebraPrinterActivity zebraPrinterActivity = new ZebraPrinterActivity(myContext, printerMacId);
            try {
                zebraPrinterActivity.printReceipts(copy,receiptsHeaderDetails, receiptsPrintList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void createSignature() {
        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
            try {
                ImageUtil.saveStamp(myContext, Utils.getSignature(), "Signature");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }
}