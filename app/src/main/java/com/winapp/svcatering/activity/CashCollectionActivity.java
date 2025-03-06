package com.winapp.svcatering.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.winapp.svcatering.adapter.PaymodeAdapter;
import com.winapp.svcatering.adapter.SalesReturnAdapter;
import com.winapp.svcatering.fragments.CashInvoiceFragment;
import com.winapp.svcatering.fragments.TotalFragment;
import com.winapp.svcatering.model.BankListModel;
import com.winapp.svcatering.model.CashCollectionInvoiceModel;
import com.winapp.svcatering.model.PaymentTypeModel;
import com.winapp.svcatering.utils.CaptureSignatureView;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.FileCompressor;
import com.winapp.svcatering.utils.ImageUtil;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.winapp.svcatering.utils.Utils.dbHelper;

public class CashCollectionActivity extends AppCompatActivity {

    private LinearLayout invoiceTab;
    private LinearLayout totalTab;
    private ImageView totalIcon;
    private ImageView invoiceIcon;
    private View invoiceView;
    private View totalView;
    private TextView invoiceTitle;
    private TextView totalTitle;
    private LinearLayout splitLayout;
    public static EditText totalPaid;
    public static EditText totalDiscount;
    private String customerCode;
    public static TextView customerName;
    public static TextView customerId;
    private String customername;
    public static EditText totalOutstanding;
    public static BottomSheetBehavior behavior;
    public LinearLayout transLayout;
    public static Spinner paymentMethod;
    AutoCompleteTextView bankAutoComplete;
    ArrayAdapter<String> autoCompleteAdapter;
    private int mYear, mMonth, mDay, mHour, mMinute;
    public static EditText dueDateEdittext;
    private ImageView cancelSheet;
    public static TextView selectedBank;
    private Button cancelButton;
    private CheckBox printCheckBox;
    File mPhotoFile;
    FileCompressor mCompressor;
    public static String imageString = "";
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 2;
    private Button okButton;
    public static EditText amountText;
    public static EditText selectedAmount;
    public static String payMethod;
    public static EditText chequeNo;
    public static EditText netAmount;
    public static EditText differenceAmount;
    public static Button btnSplit;
    public static SweetAlertDialog pDialog;
    public ArrayList<BankListModel> bankListDetails;
    public ArrayList<PaymentTypeModel> paymentTypeList;
    public ArrayList<String> bankList;
    private SessionManager session;
    private HashMap<String,String > user;
    private static String companyId;
    public static TextView bankCode;
    LinearLayout bankLayout;
    Boolean netAmountFocus = false ;
    ImageView salesReturn;
    public static EditText returnAmountEditText;
    private LinearLayout bottomLayout;
    private Spinner bankNameEntry;
    private TextWatcher netAmountTextWatcher;
    private View bankOptionLayout;
    private View lTotalLayout;
    boolean check=false;
    public String bankName;
    public String chequeNoValue;
    public String chequeDate;
    public ImageView editCheque;
    public static File imageFile;
    private AlertDialog dialog;
    int minteger = 1;
    public Button decreaseButton;
    public Button increaseButton;
    public TextView noOfCopyText;
    public TextView selectImagel;
    public boolean isReceiptPrint=false;
    public RecyclerView paymodeView;
    public static EditText paymentDate;
    public static String paymentCode="";
    public static String paymentName="";
    public ImageView signatureCapture;
    public CaptureSignatureView captureSignatureView;
    public AlertDialog alert;
    public TextView signatureTitle;
    public static String signatureString="";
    public static Button btnClear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.isTablet(this)){
            setContentView(R.layout.activity_new_cash_collection);
        }else {
            setContentView(R.layout.cash_collection_mobile);
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Receipt");
        signatureString = "";
        imageString = "";

        Log.w("activity_cg",getClass().getSimpleName().toString()+" CashInvoiceFragment");

        netAmount=findViewById(R.id.net_amount);
        btnSplit=findViewById(R.id.btn_split);
        invoiceTab=findViewById(R.id.invoice_tab);
        totalTab=findViewById(R.id.total_tab);
        totalIcon=findViewById(R.id.total_icon);
        invoiceIcon=findViewById(R.id.invoice_icon);
        invoiceView=findViewById(R.id.invoice_view);
        totalView=findViewById(R.id.total_view);
        invoiceTitle=findViewById(R.id.invoice_title);
        totalTitle=findViewById(R.id.total_title);
        splitLayout=findViewById(R.id.split_layout);
        totalPaid=findViewById(R.id.total_paid);
        totalDiscount=findViewById(R.id.total_discount);
        customerName=findViewById(R.id.customer_name_value);
        customerId=findViewById(R.id.customer_id);
        totalOutstanding=findViewById(R.id.total_outstanding);
        dueDateEdittext=findViewById(R.id.due_date);
        transLayout=findViewById(R.id.trans_layout);
        cancelSheet=findViewById(R.id.cancel_sheet);
        cancelButton=findViewById(R.id.cancel);
        okButton=findViewById(R.id.ok);
        selectedBank=findViewById(R.id.selected_bank_name);
        paymentMethod=findViewById(R.id.payment_method);
        bankAutoComplete=findViewById(R.id.bank_name);
        amountText=findViewById(R.id.amount_text);
        chequeNo=findViewById(R.id.cheque_no);
        bankAutoComplete.clearFocus();
        netAmount.setSelectAllOnFocus(true);
        differenceAmount=findViewById(R.id.difference_amount);
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        bankCode=findViewById(R.id.selected_bank_code);
        bankLayout=findViewById(R.id.bank_layout);
        salesReturn=findViewById(R.id.sales_return);
        returnAmountEditText=findViewById(R.id.return_amount);
        bottomLayout=findViewById(R.id.bottom_layout);
        bankNameEntry=findViewById(R.id.bank_name_entry);
        selectedAmount=findViewById(R.id.selected_amount);
        bankOptionLayout=findViewById(R.id.bank_option);
        lTotalLayout=findViewById(R.id.ltotal_layout);
        editCheque=findViewById(R.id.edit_cheque);
        btnClear=findViewById(R.id.btn_clr);

        mCompressor = new FileCompressor(this);

        // Remove all invoice Details to be removed before load the new data
        dbHelper.removeAllInvoices();
        dbHelper.removeAllSalesReturn();

        if (getIntent()!=null){
            customerCode=getIntent().getStringExtra("customerCode");
            customername=getIntent().getStringExtra("customerName");
            customerId.setText(customerCode);
            customerName.setText(customername);
            changeFragment(new CashInvoiceFragment(customerCode));
        }


        invoiceTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invoiceView.setVisibility(View.VISIBLE);
                invoiceIcon.setImageResource(R.drawable.ic_pay);
                invoiceTitle.setTextColor(Color.parseColor("#006DDB"));
                splitLayout.setVisibility(View.VISIBLE);
                totalView.setVisibility(View.GONE);
                totalTitle.setTextColor(Color.parseColor("#757575"));
                totalIcon.setImageResource(R.drawable.ic_balance_amount_unselect);

                changeFragment(new CashInvoiceFragment(customerCode));

            }
        });

        totalTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                splitLayout.setVisibility(View.GONE);
                invoiceView.setVisibility(View.GONE);
                invoiceIcon.setImageResource(R.drawable.ic_pay_unselect);
                invoiceTitle.setTextColor(Color.parseColor("#757575"));
                totalView.setVisibility(View.VISIBLE);
                totalTitle.setTextColor(Color.parseColor("#006DDB"));
                totalIcon.setImageResource(R.drawable.ic_balance_amount);
                changeFragment(new TotalFragment());
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
                        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        if (bankLayout.getVisibility()==View.VISIBLE){
                            getSupportActionBar().setTitle("Fill The Details");
                        }else {
                            getSupportActionBar().setTitle("Overall Details");
                        }

                        transLayout.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        getSupportActionBar().setTitle("Receipt");
                        transLayout.setVisibility(View.GONE);
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

        editCheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet("BankOption");
            }
        });

        paymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Your code here
                final String payment_method = (String) paymentMethod.getItemAtPosition(position);
                Log.w("Payment_method:",payment_method);
                if (payment_method.equals("CHEQUE")){
                    editCheque.setVisibility(View.VISIBLE);
                    dueDateEdittext.setText(chequeDate);
                    chequeNo.setText(chequeNoValue);
                    selectValue(bankNameEntry,bankName);
                    paymentCode="";
                    paymentName="";
                    viewCloseBottomSheet("BankOption");
                  /*  if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }*/
                }else if (payment_method.equals("CHEQUE")){
                    editCheque.setVisibility(View.VISIBLE);
                    dueDateEdittext.setText(chequeDate);
                    chequeNo.setText(chequeNoValue);
                    selectValue(bankNameEntry,bankName);
                    paymentCode="";
                    paymentName="";
                    viewCloseBottomSheet("BankOption");
                  /*  if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }*/
                }
                else if (payment_method.equals("Bank Transfer")){
                    check=false;
                    editCheque.setVisibility(View.GONE);
                    bankName="";
                    chequeNoValue="";
                    chequeDate="";
                    dueDateEdittext.setText("");
                    chequeNo.setText("");
                    bankNameEntry.setSelection(0);
                    if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                    showBankTransferOption();
                }else {
                    check=false;
                    editCheque.setVisibility(View.GONE);
                    bankName="";
                    chequeNoValue="";
                    chequeDate="";
                    paymentCode="";
                    paymentName="";
                    dueDateEdittext.setText("");
                    chequeNo.setText("");
                    bankNameEntry.setSelection(0);
                    if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
                payMethod=payment_method;
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

     /*   paymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String payment_method = (String) paymentMethod.getSelectedItem();
                Log.w("Payment_method:",payment_method);
                if (payment_method.equals("CHEQUE")){
                    if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }else {
                    if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
                payMethod=payment_method;
            }
        });*/

        getBankList(companyId);

        dueDateEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(dueDateEdittext);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check=false;
                paymentMethod.setSelection(0);
                closeSheet();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { if (chequeNo.getText().toString().isEmpty()){
                chequeNo.setError("Enter Check Number");
            }else if (dueDateEdittext.getText().toString().isEmpty()){
                dueDateEdittext.setError("Select Check Date");
            }else if (!chequeNo.getText().toString().isEmpty() && !dueDateEdittext.getText().toString().isEmpty() ){
                bankName=bankNameEntry.getSelectedItem().toString();
                chequeDate=dueDateEdittext.getText().toString();
                chequeNoValue=chequeNo.getText().toString();
                check=true;
                dueDateEdittext.setError(null);
                chequeNo.setError(null);
            }
                if (check){
                    closeSheet();
                    chequeNo.clearFocus();
                }else {
                    Toast.makeText(getApplicationContext(),"Fill the Required values",Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check=false;
                paymentMethod.setSelection(0);
                closeSheet();
            }
        });

        bankAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selectBank = (String)parent.getItemAtPosition(position);
                selectedBank.setText(selectBank+" - ");
                for (BankListModel model:bankListDetails){
                    if (model.getBankName().trim().equals(selectBank.trim())){
                        bankCode.setText(model.getBankCode());
                    }
                }
                bankLayout.setVisibility(View.VISIBLE);
                bankAutoComplete.clearFocus();
                Log.w("Selected_bank",selectBank);
            }
        });

        bankNameEntry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectBank=adapterView.getItemAtPosition(i).toString();
                selectedBank.setText(selectBank+" - ");
                for (BankListModel model:bankListDetails){
                    if (model.getBankName().trim().equals(selectBank.trim())){
                        bankCode.setText(model.getBankCode());
                    }
                }
                bankLayout.setVisibility(View.VISIBLE);
                Log.w("Selected_bank",selectBank);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(netAmountFocus) {
                    if (!netAmount.getText().toString().equals(".")) {
                        if (!netAmount.getText().toString().isEmpty() &&
                                (Double.parseDouble(netAmount.getText().toString()) > 0.00)) {
                            Log.w("dsplit11", "");
                            CashInvoiceFragment.splitInvoices();
                        } else {
                            Toast.makeText(getApplicationContext(), "Value Should not be empty", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Value Should not be empty", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Log.w("dsplit11aa", "");
                }

            }
        });

        salesReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CashInvoiceFragment.showSalesReturnLayout();
            }
        });

        netAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    netAmountFocus = true ;
                } else {
                    netAmountFocus = false ;
                }
            }
        });

        // Define the Textwatcher of the Total Paid Amount

        netAmountTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() &&   charSequence.toString().charAt(0) != ('.')){
                    calculateDifferenceAmount(charSequence.toString());
                }else {
                    calculateDifferenceAmount("0.00");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        netAmount.addTextChangedListener(netAmountTextWatcher);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CashInvoiceFragment.clearDataValues();
            }
        });
    }

    private void selectValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public void calculateDifferenceAmount(String netamount){
        double selected_amount=0.0;
        double differecne_amount=0.0;
        selected_amount=Double.parseDouble(selectedAmount.getText().toString());
        differecne_amount= Double.parseDouble(netamount) - selected_amount;
        differenceAmount.setText(Utils.twoDecimalPoint(differecne_amount));
    }

    private void getBankTransfer() {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"ReceiptBankTransferPayType";
        // Initialize a new JsonArrayRequest instance
        paymentTypeList =new ArrayList<>();
        Log.w("BanKtransferURL:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Bank Transfer Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try{
                        Log.w("Response_Paymodes::",response.toString());
                        pDialog.dismiss();
                        // Loop through the array elements
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray bankArray=response.getJSONArray("responseData");
                            for(int i=0;i<bankArray.length();i++){
                                // Get current json object
                                JSONObject jsonObject=bankArray.getJSONObject(i);
                                PaymentTypeModel model=new PaymentTypeModel();
                                model.setPaymentTypeCode(jsonObject.optString("paymentTypeCode"));
                                model.setPaymentTypeName(jsonObject.optString("paymentTypeName"));
                                paymentTypeList.add(model);
                            }
                            if (paymentTypeList.size()>0){
                                // setBankDetails(bankList);
                            }
                        }else {

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
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

    private void getBankList(String companyCode) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"BankList";
        // Initialize a new JsonArrayRequest instance
        bankListDetails =new ArrayList<>();
        bankList=new ArrayList<>();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Banks Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Response_is:",response.toString());
                        pDialog.dismiss();
                        // Loop through the array elements
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray bankArray=response.getJSONArray("responseData");
                            for(int i=0;i<bankArray.length();i++){
                                // Get current json object
                                JSONObject jsonObject=bankArray.getJSONObject(i);
                                BankListModel model=new BankListModel();
                                model.setBankCode(jsonObject.optString("bankCode"));
                                model.setBankName(jsonObject.optString("bankName"));
                                bankList.add(jsonObject.optString("bankName"));
                                bankListDetails.add(model);
                            }
                            if (bankList.size()>0){
                                setBankDetails(bankList);
                            }
                        }else {

                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
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

    public void setBankDetails(ArrayList<String> banks){
        autoCompleteAdapter = new ArrayAdapter<String>(CashCollectionActivity.this, R.layout.cust_spinner_item, banks){
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(12);
            /*    Typeface Type = getFont () ;  // custom method to get a font from "assets" folder
                ((TextView) v).setTypeface(Type);
                ((TextView) v).setTextColor(YourColor);*/
                ((TextView) v) .setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
                return v;
            }
        };
        bankAutoComplete.setThreshold(1);
        bankAutoComplete.setAdapter(autoCompleteAdapter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, banks);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        bankNameEntry.setAdapter(adapter);
        getBankTransfer();
    }




    public void changeFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    public void setFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<CashCollectionInvoiceModel> list=dbHelper.getAllInvoices();
        double amt=0.0;
        for (CashCollectionInvoiceModel model:list){
            if (!model.getPayable().isEmpty()){
                amt+=Double.parseDouble(model.getPayable());
            }
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                if (behavior.getState()== BottomSheetBehavior.STATE_EXPANDED){
                    closeSheet();
                }else {
                    ArrayList<CashCollectionInvoiceModel> list1=dbHelper.getAllInvoices();
                    double amt1=0.0;
                    for (CashCollectionInvoiceModel model:list1){
                        if (!model.getPayable().isEmpty()){
                            amt1+=Double.parseDouble(model.getPayable());
                        }
                    }
                    if(!netAmount.getText().toString().equals(".")){
                        if (netAmount.getText().toString()!=null &&
                                !netAmount.getText().toString().isEmpty() &&
                                Double.parseDouble(netAmount.getText().toString()) > 0 &&
                                Double.parseDouble(selectedAmount.getText().toString()) >= 0){
                            showCashCollectionClearAlert();
                        }else {
//                        Intent intent=new Intent(CashCollectionActivity.this,NewInvoiceListActivity.class);
//                        startActivity(intent);
                            onBackPressed();
                            finish();
                        }
                    }
                }
                break;
            case R.id.action_save:
                if (behavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
                    closeSheet();
                }
                ArrayList<CashCollectionInvoiceModel> list2=dbHelper.getAllInvoices();
                double amt2=0.0;
                for (CashCollectionInvoiceModel model:list2){
                    if (!model.getPayable().isEmpty()){
                        amt2+=Double.parseDouble(model.getPayable());
                    }
                }

                Log.w("GivenAmount:",netAmount.getText().toString());

                if (payMethod.equals("CHEQUE") || payMethod.equals("Bank Transfer")){
                    if (payMethod.equals("CHEQUE")){
                        if (chequeNo.getText().toString().isEmpty()){
                            chequeNo.setError("Enter Check Number");
                        }else if (dueDateEdittext.getText().toString().isEmpty()){
                            dueDateEdittext.setError("Select Check Date");
                        }else if (!chequeNo.getText().toString().isEmpty() && !dueDateEdittext.getText().toString().isEmpty() ){
                            check=true;
                            dueDateEdittext.setError(null);
                            chequeNo.setError(null);
                        }
                        if (check){
                            closeSheet();
                            if (Double.parseDouble(selectedAmount.getText().toString()) > 0){
                                //showAlert();
                                if (Double.parseDouble(differenceAmount.getText().toString())< 0) {
                                    netAmount.setError(null);
                                    Toast.makeText(getApplicationContext(),"Excess Amount should not be negative",Toast.LENGTH_SHORT).show();
                                }else {
                                    ArrayList<CashCollectionInvoiceModel> cashReceipts=CashInvoiceFragment.cashCollectionInvoiceAdapter.getList();
                                    double net_paid_amount=0.0;
                                    for (CashCollectionInvoiceModel model:cashReceipts){
                                        if (model.getPayable()!=null && !model.getPayable().isEmpty() && !model.getPayable().equals("null"))
                                            net_paid_amount+=Double.parseDouble(model.getPayable());
                                    }
                                    if (net_paid_amount > 0){
                                        netAmount.setError(null);
                                        //showAlert();
                                        showSaveAlert();
                                    }else {
                                        Toast.makeText(getApplicationContext(),"Select Invoice to save receipt",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else {
                                netAmount.setError(null);
                                Toast.makeText(getApplicationContext(),"Add Invoice amount to proceed",Toast.LENGTH_SHORT).show();
                            }
                            chequeNo.clearFocus();
                        }else {
                            viewCloseBottomSheet("BankOption");
                            Toast.makeText(getApplicationContext(),"Fill the Required values",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        // Arguments for the Bank Transfer Mode
                        closeSheet();
                        if (Double.parseDouble(selectedAmount.getText().toString()) > 0){
                            // showAlert();
                            if (Double.parseDouble(differenceAmount.getText().toString())< 0) {
                                netAmount.setError(null);
                                Toast.makeText(getApplicationContext(),"Excess Amount should not be negative",Toast.LENGTH_SHORT).show();
                            }else {
                                ArrayList<CashCollectionInvoiceModel> cashReceipts=CashInvoiceFragment.cashCollectionInvoiceAdapter.getList();
                                double net_paid_amount=0.0;
                                for (CashCollectionInvoiceModel model:cashReceipts){
                                    if (model.getPayable()!=null && !model.getPayable().isEmpty() && !model.getPayable().equals("null"))
                                        net_paid_amount+=Double.parseDouble(model.getPayable());
                                }
                                if (net_paid_amount > 0){
                                    netAmount.setError(null);
                                    //showAlert();
                                    showSaveAlert();
                                }else {
                                    Toast.makeText(getApplicationContext(),"Select Invoice to save receipt",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else {
                            netAmount.setError(null);
                            Toast.makeText(getApplicationContext(),"Add Invoice amount to proceed",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    if(!netAmount.getText().toString().equals(".")) {

                        if (!netAmount.getText().toString().isEmpty() &&
                                netAmount.getText().toString() != null &&
                                Double.parseDouble(netAmount.getText().toString()) > 0) {

                            if (Double.parseDouble(differenceAmount.getText().toString()) < 0) {
                                netAmount.setError(null);
                                Toast.makeText(getApplicationContext(), "Excess Amount should not be negative", Toast.LENGTH_SHORT).show();
                            } else {
                                ArrayList<CashCollectionInvoiceModel> cashReceipts = CashInvoiceFragment.cashCollectionInvoiceAdapter.getList();
                                double net_paid_amount = 0.0;
                                for (CashCollectionInvoiceModel model : cashReceipts) {
                                    if (model.getPayable() != null && !model.getPayable().isEmpty() && !model.getPayable().equals("null"))
                                        net_paid_amount += Double.parseDouble(model.getPayable());
                                }
                                if (net_paid_amount > 0) {
                                    netAmount.setError(null);
                                    //showAlert();
                                    showSaveAlert();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Select Invoice to save receipt", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else {
                            netAmount.setError("Enter the Amount");
                            Toast.makeText(getApplicationContext(),"Please Add values to save receipt",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        netAmount.setError("Enter the Amount");
                        Toast.makeText(getApplicationContext(),"Please Add values to save receipt",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.action_more:
                viewCloseBottomSheet("TotalView");
                setFragment(new TotalFragment());
                break;

        }
        return true;
    }

    public void showAlert(){
        new SweetAlertDialog(CashCollectionActivity.this, SweetAlertDialog.WARNING_TYPE)
                // .setTitleText("Are you sure?")
                .setContentText("Are you sure want to save Receipt ?")
                .setConfirmText("YES")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        try {
                            // CashCollectionInvoiceAdapter.saveReceipts();
                            Log.w("Net_Returnvalue:", SalesReturnAdapter.net_return_value);
                            CashInvoiceFragment.saveReceipt(Integer.parseInt(noOfCopyText.getText().toString()),isReceiptPrint);
                            // CashInvoiceFragment.showSaveOption("Add");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).showCancelButton(true)
                .setCancelText("No")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                }).show();
    }

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
                    Utils.setSelectImage(imageString);

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
                    selectImagel.setText(selectedImage.toString());

                    imageString = ImageUtil.getBase64StringImage(mPhotoFile);
                    Utils.setSelectImage(imageString);

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

    public void showSaveAlert(){
        try {
            // create an alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // set the custom layout
            builder.setCancelable(false);
            final View customLayout = getLayoutInflater().inflate(R.layout.receipt_confirm_layout, null);
            builder.setView(customLayout);
            // add a button
            okButton=customLayout.findViewById(R.id.btn_ok);
            cancelButton=customLayout.findViewById(R.id.btn_cancel);
            Button signatureButton=customLayout.findViewById(R.id.btn_signature);
            signatureCapture=customLayout.findViewById(R.id.signature_capture);
            printCheckBox=customLayout.findViewById(R.id.receipt_print_check);
            LinearLayout printLayout=customLayout.findViewById(R.id.print_layout);
            decreaseButton=customLayout.findViewById(R.id.decrease);
            increaseButton=customLayout.findViewById(R.id.increase);
            selectImagel = customLayout.findViewById(R.id.select_imagea);

            noOfCopyText=customLayout.findViewById(R.id.no_of_copy);

            if (mPhotoFile != null && mPhotoFile.length() > 0) {
                selectImagel.setText("View Image");
                selectImagel.setTag("view_image");
            } else {
                selectImagel.setText("Select Image");
                selectImagel.setTag("select_image");
            }
            selectImagel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectImagel.getTag().equals("view_image")) {
                        showImage();
                    } else {
                        selectImage();
                    }
                }
            });


            okButton.setOnClickListener(view1 -> {
                try {
                    dialog.dismiss();
                    CashInvoiceFragment.saveReceipt(Integer.parseInt(noOfCopyText.getText().toString()),isReceiptPrint);
                }catch (Exception exception){}
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            signatureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSignatureAlert();
                }
            });

            printCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if ( isChecked ) {
                        printLayout.setVisibility(View.VISIBLE);
                        isReceiptPrint=true;
                    }else {
                        isReceiptPrint=false;
                        printLayout.setVisibility(View.GONE);
                    }
                }
            });
            // create and show the alert dialog
            dialog = builder.create();
            dialog.show();
        }catch (Exception exception){}
    }
    public void showImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CashCollectionActivity.this);
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
        builder.setTitle("Receipt Image");
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
                selectImagel.setTag("view_image");
                selectImagel.setText("View Image");
                dialog.dismiss();
            }
        }).create().show();

    }
    private void selectImage() {
        final CharSequence[] items = {
                "Take Photo",
                /* "Choose from Library",*/
                "Cancel"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(CashCollectionActivity.this);
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

    public void showSignatureAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.signature_layout, null);
        alertDialog.setView(customLayout);
        final Button acceptButton=customLayout.findViewById(R.id.buttonYes);
        final Button cancelButton=customLayout.findViewById(R.id.buttonNo);
        final Button clearButton=customLayout.findViewById(R.id.buttonClear);
        LinearLayout mContent = customLayout.findViewById(R.id.signature_layout);


        acceptButton.setEnabled(false);
        acceptButton.setAlpha(0.4f);
        CaptureSignatureView mSig = new CaptureSignatureView(CashCollectionActivity.this, null, new CaptureSignatureView.OnSignatureDraw() {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dbHelper.removeAllInvoices();
        //  Intent intent=new Intent(CashCollectionActivity.this,NewInvoiceListActivity.class);
        // startActivity(intent);
        finish();
    }

    public void showCashCollectionClearAlert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Warning !");
        builder1.setMessage("It will clear all data,Do you want to go back?");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dbHelper.removeAllInvoices();
                        finish();
                    }
                });
        builder1.setNegativeButton(
                "No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_menu_cashcollection, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }


    public void viewCloseBottomSheet(String showView){
        hideKeyboard();
        if (showView.equals("BankOption")){
            bankOptionLayout.setVisibility(View.VISIBLE);
            lTotalLayout.setVisibility(View.GONE);
        }else if (showView.equals("TotalView")){
            bankOptionLayout.setVisibility(View.GONE);
            lTotalLayout.setVisibility(View.VISIBLE);
        }
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } /*else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }*/
        // get the Customer name from the local db
    }

    public static void closeSheet(){
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void hideKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    public void getDate(EditText dateEditext){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateEditext.setText((dayOfMonth)+ "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            bottomLayout.setVisibility(View.GONE);
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            bottomLayout.setVisibility(View.VISIBLE);
        }
    }

    public void increaseInteger(View view) {
        minteger = minteger + 1;
        display(minteger);
    }

    public void decreaseInteger(View view) {
        if (minteger > 1){
            minteger = minteger - 1;
            display(minteger);
        }
    }

    private void display(int number) {
        noOfCopyText.setText("" +number);
    }


    public void showBankTransferOption(){
        try {
            // create an alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // set the custom layout
            builder.setCancelable(false);
            final View customLayout = getLayoutInflater().inflate(R.layout.bank_transfer_options, null);
            builder.setView(customLayout);
            // add a button
            okButton=customLayout.findViewById(R.id.btn_ok);
            cancelButton=customLayout.findViewById(R.id.btn_cancel);
            paymodeView=customLayout.findViewById(R.id.pay_mode_view);
            paymentDate=customLayout.findViewById(R.id.payment_date);
            setPaymodeAdapter();
            paymentDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDate(paymentDate);
                }
            });
            okButton.setOnClickListener(view1 -> {
                try {
                    if (paymentCode!=null && !paymentCode.isEmpty()){
                        if (!paymentDate.getText().toString().isEmpty()){
                            dialog.dismiss();
                        }else {
                            Toast.makeText(getApplicationContext(),"Select Payment Date",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Select Paymode",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception exception){}
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    paymentCode="";
                    paymentName="";
                    paymentMethod.setSelection(0);
                    dialog.dismiss();
                }
            });

            // create and show the alert dialog
            dialog = builder.create();
            dialog.show();
        }catch (Exception exception){}
    }


    public void setPaymodeAdapter(){
        try {
            paymodeView.setHasFixedSize(true);
            paymodeView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            PaymodeAdapter adapter = new PaymodeAdapter(paymentTypeList, new PaymodeAdapter.CallBack() {
                @Override
                public void selectPayment(String paymentcode,String paymentname) {
                    paymentCode=paymentcode;
                    paymentName=paymentname;
                }
            });
            paymodeView.setAdapter(adapter);
        }catch (Exception e){}
    }
}