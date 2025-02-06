package com.winapp.svcatering.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.SalesReturnAdapter;
import com.winapp.svcatering.fragments.CashInvoiceFragment;
import com.winapp.svcatering.fragments.TotalFragment;
import com.winapp.svcatering.model.BankListModel;
import com.winapp.svcatering.model.CashCollectionInvoiceModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.winapp.svcatering.utils.Utils.dbHelper;

public class NewCashCollectionActivity extends AppCompatActivity {

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
    BottomSheetBehavior behavior;
    public LinearLayout transLayout;
    public static Spinner paymentMethod;
    AutoCompleteTextView bankAutoComplete;
    ArrayAdapter<String> autoCompleteAdapter;
    private int mYear, mMonth, mDay, mHour, mMinute;
    public static EditText dueDateEdittext;
    private ImageView cancelSheet;
    public static TextView selectedBank;
    private Button cancelButton;
    private Button okButton;
    public static EditText amountText;
    public static String payMethod;
    public static EditText chequeNo;
    public static EditText netAmount;
    public static Button btnSplit;
    public static SweetAlertDialog pDialog;
    public ArrayList<BankListModel> bankListDetails;
    public ArrayList<String> bankList;
    private SessionManager session;
    private HashMap<String,String > user;
    private static String companyId;
    public static TextView bankCode;
    LinearLayout bankLayout;
    ImageView salesReturn;
    public static EditText returnAmountEditText;
    private LinearLayout bottomLayout;
    private Spinner bankNameEntry;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.isTablet(this)){
            setContentView(R.layout.activity_cash_collection);
        }else {
            setContentView(R.layout.cash_collection_mobile);
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cash Collection");

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
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        bankCode=findViewById(R.id.selected_bank_code);
        bankLayout=findViewById(R.id.bank_layout);
        salesReturn=findViewById(R.id.sales_return);
        returnAmountEditText=findViewById(R.id.return_amount);
        bottomLayout=findViewById(R.id.bottom_layout);
        bankNameEntry=findViewById(R.id.bank_name_entry);

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
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        getSupportActionBar().setTitle("Fill The Details");
                        transLayout.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        getSupportActionBar().setTitle("Cash Collection");
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

        paymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Your code here
                final String payment_method = (String) paymentMethod.getItemAtPosition(position);
                Log.w("Payment_method:",payment_method);
                if (payment_method.equals("CHEQUE")){
                    viewCloseBottomSheet();
                    if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }else {
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
                paymentMethod.setSelection(0);
                viewCloseBottomSheet();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check=false;
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
                    viewCloseBottomSheet();
                    chequeNo.clearFocus();
                }else {
                    Toast.makeText(getApplicationContext(),"Fill the Required values",Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMethod.setSelection(0);
                viewCloseBottomSheet();
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
                if (!netAmount.getText().toString().isEmpty() && (Double.parseDouble(netAmount.getText().toString())>0.00)){
                    CashInvoiceFragment.splitValues(netAmount.getText().toString());
                }else {
                    Toast.makeText(getApplicationContext(),"Value Should not be empty",Toast.LENGTH_LONG).show();
                }
            }
        });

        salesReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CashInvoiceFragment.showSalesReturnLayout();
            }
        });
    }

    private void getBankList(String companyCode) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"MasterApi/GetBank_All?Requestdata={CompanyCode:"+companyCode+"}";
        // Initialize a new JsonArrayRequest instance
        bankListDetails =new ArrayList<>();
        bankList=new ArrayList<>();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Banks Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Response_is:",response.toString());
                        pDialog.dismiss();
                        // Loop through the array elements
                        for(int i=0;i<response.length();i++){
                            // Get current json object
                            JSONObject jsonObject=response.getJSONObject(i);
                            BankListModel model=new BankListModel();
                            model.setBankCode(jsonObject.optString("BankCode"));
                            model.setBankName(jsonObject.optString("Description"));
                            bankList.add(jsonObject.optString("Description"));
                            bankListDetails.add(model);
                        }

                        if (bankList.size()>0){
                            setBankDetails(bankList);
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
        autoCompleteAdapter = new ArrayAdapter<String>(NewCashCollectionActivity.this, android.R.layout.select_dialog_item, banks){
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
    }


    public void changeFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
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
                ArrayList<CashCollectionInvoiceModel> list1=dbHelper.getAllInvoices();
                double amt1=0.0;
                for (CashCollectionInvoiceModel model:list1){
                    if (!model.getPayable().isEmpty()){
                        amt1+=Double.parseDouble(model.getPayable());
                    }
                }
                if (list.size()>0){
                    showCashCollectionClearAlert();
                }else {
                    Intent intent=new Intent(NewCashCollectionActivity.this,NewInvoiceListActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.action_save:
                ArrayList<CashCollectionInvoiceModel> list2=dbHelper.getAllInvoices();
                double amt2=0.0;
                for (CashCollectionInvoiceModel model:list2){
                    if (!model.getPayable().isEmpty()){
                        amt2+=Double.parseDouble(model.getPayable());
                    }
                }
               /* if (list.size()>0 && amt2>0){
                    // This method enable for the without printing method to save and Redirect to the Receipts List
                    showAlert();
                    // This Method for enable printing option and Redirect to the Receipts List
                    //  CashInvoiceFragment.showSaveOption("Add");
                }else {
                    Toast.makeText(getApplicationContext(),"Please Add values to save receipt",Toast.LENGTH_SHORT).show();
                }*/

                Log.w("GivenAmount:",netAmount.getText().toString());

                if (Double.parseDouble(netAmount.getText().toString()) > 0){
                    // This method enable for the without printing method to save and Redirect to the Receipts List
                    showAlert();
                    // This Method for enable printing option and Redirect to the Receipts List
                    //  CashInvoiceFragment.showSaveOption("Add");
                }else {
                    Toast.makeText(getApplicationContext(),"Please Add values to save receipt",Toast.LENGTH_SHORT).show();
                }

                break;

        }
        return true;
    }

    public void showAlert(){
        new SweetAlertDialog(NewCashCollectionActivity.this, SweetAlertDialog.WARNING_TYPE)
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
                          //  CashInvoiceFragment.saveReceipt(1);
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
            okButton.setOnClickListener(view1 -> {
                try {
                   // CashInvoiceFragment.saveReceipt(1,);
                }catch (Exception exception){}
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            // create and show the alert dialog
            dialog = builder.create();
            dialog.show();
        }catch (Exception exception){}
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dbHelper.removeAllInvoices();
        Intent intent=new Intent(NewCashCollectionActivity.this,NewInvoiceListActivity.class);
        startActivity(intent);
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


    public void viewCloseBottomSheet(){
        hideKeyboard();
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        // get the Customer name from the local db
    }

    public void hideKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
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
                        dateEditext.setText((monthOfYear + 1)+ "-" + dayOfMonth + "-" + year);
                    }
                }, mYear, mMonth, mDay);
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
}