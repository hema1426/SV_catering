package com.winapp.svcatering.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.ReOrderAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.fragments.CustomerFragment;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

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

public class ReOrderCartActivity extends AppCompatActivity {

    // define the required variables
    private DBHelper dbHelper;
    private LinearLayout emptyLayout;
    private ArrayList<CartModel> reorderCart;
    private ReOrderAdapter reOrderAdapter;
    private RecyclerView reorderView;
    private TextView subTotalText;
    private TextView taxText;
    private TextView netTotalText;
    private LinearLayout transLayout;
    public static BottomSheetBehavior behavior;
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
    public SweetAlertDialog pDialog;
    private static String currentDateString;
    private static String currentDate;
    private static JSONObject customerResponse=new JSONObject();
    private ArrayList<CustomerDetails> customerDetails;
    private String customerId;
    private SessionManager session;
    private HashMap<String ,String> user;
    private String companyCode;
    private String userName;
    private String locationCode;
    private String netTax;
    private String netsubTotal;
    private String netAmount;

    SharedPreferences sharedPreferences;
    static String printerMacId;
    static String printerType;
    TextView taxTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_order_cart);
        getSupportActionBar().setTitle("Reorder Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        emptyLayout=findViewById(R.id.empty_layout);
        reorderView=findViewById(R.id.reorderList);
        subTotalText =findViewById(R.id.balance_value);
        netTotalText=findViewById(R.id.net_total);
        transLayout=findViewById(R.id.trans_layout);
        taxText=findViewById(R.id.tax);
        transLayout=findViewById(R.id.trans_layout);
        cashCollected=findViewById(R.id.cash_collected);
        invoicePrint=findViewById(R.id.invoice_print);
        deliveryPrint=findViewById(R.id.delivery_print);
        copyLayout=findViewById(R.id.copy_layout);
        copyMinus=findViewById(R.id.copy_minus);
        copyPlus=findViewById(R.id.copy_plus);
        yesButton=findViewById(R.id.buttonYes);
        noButton=findViewById(R.id.buttonNo);
        copyText=findViewById(R.id.copy);
        emailInvoice=findViewById(R.id.email_invoice);
        dbHelper=new DBHelper(this);
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        taxTitle=findViewById(R.id.tax_title);
        getLocalData();

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        currentDateString = df.format(c);

        SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        currentDate = df1.format(c);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        Log.w("Printer_Mac_Id:",printerMacId);
        Log.w("Printer_Type:",printerType);

        if (dbHelper!=null){
            customerDetails=dbHelper.getCustomer();
        }

        if (customerResponse.length()==0){
            if (customerDetails!=null){
                customerId=customerDetails.get(0).getCustomerCode();
                getCustomerDetails(customerId);
            }
        }else {
            getCustomerDetails(AddInvoiceActivityOld.customerId);
        }

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
                if (validatePrinterConfiguration()){
                    if (b){
                        isPrintEnable=true;
                        copyLayout.setVisibility(View.VISIBLE);
                        if (saveAction.equals("SalesOrder") || saveAction.equals("SalesEdit")){
                            emailInvoice.setVisibility(View.GONE);
                        }else {
                            emailInvoice.setVisibility(View.VISIBLE);
                        }
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
                }else {
                    createInvoiceJson(noofCopyPrint);
                }
                closeSheet();
            }
        });
    }

    public void closeSheet(){
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public  boolean validatePrinterConfiguration(){
        boolean printetCheck=false;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(ReOrderCartActivity.this,"This device does not support bluetooth",Toast.LENGTH_SHORT).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            Toast.makeText(ReOrderCartActivity.this,"Enable bluetooth and connect the printer",Toast.LENGTH_SHORT).show();
        } else {
            // Bluetooth is enabled
            if (!printerType.isEmpty() && !printerMacId.isEmpty()){
                printetCheck=true;
            }else {
                Toast.makeText(ReOrderCartActivity.this,"Please configure Printer",Toast.LENGTH_SHORT).show();
            }
        }
        return printetCheck;
    }


    private void getLocalData() {
        int count =dbHelper.numberOfRows();
        if (count>0){
            emptyLayout.setVisibility(View.GONE);
            reorderCart = new ArrayList<>();
            reorderCart = dbHelper.getAllCartItems();
            reOrderAdapter = new ReOrderAdapter(this, reorderCart, new ReOrderAdapter.CallBack() {
                @Override
                public void updateNetAmount(String action) {
                    totalProductPrice();
                }
            });
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            reorderView.setLayoutManager(mLayoutManager);
            reorderView.setItemAnimator(new DefaultItemAnimator());
            reorderView.setAdapter(reOrderAdapter);
            totalProductPrice();
            //invalidateOptionsMenu();
        }else {
            reorderView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
    }
    public void totalProductPrice() {
        try {
            ArrayList<CartModel> localCart = dbHelper.getAllCartItems();
            double sub_total=0.0;
            double net_tax=0.0;
            double total_value=0.0;
            double net_total=0.0;
            double net_item_discount=0.0;
            for (int j = 0;j<localCart.size();j++){
                if (localCart.get(j).getCART_TOTAL_VALUE()!=null && !localCart.get(j).getCART_TOTAL_VALUE().equals("null")){
                    total_value+= Double.parseDouble(localCart.get(j).getCART_TOTAL_VALUE());
                }

                if (localCart.get(j).getSubTotal()!=null && !localCart.get(j).getSubTotal().equals("null")){
                    sub_total+= Double.parseDouble(localCart.get(j).getSubTotal());
                }
                if (localCart.get(j).getCART_TAX_VALUE()!=null && !localCart.get(j).getCART_TAX_VALUE().equals("null")){
                    net_tax+= Double.parseDouble(localCart.get(j).getCART_TAX_VALUE());
                }

                if (localCart.get(j).getCART_COLUMN_NET_PRICE()!=null && !localCart.get(j).getCART_COLUMN_NET_PRICE().equals("null")){
                    net_total+= Double.parseDouble(localCart.get(j).getCART_COLUMN_NET_PRICE());
                }

                if (localCart.get(j).getDiscount()!=null && !localCart.get(j).getDiscount().equals("null") && !localCart.get(j).getDiscount().isEmpty()){
                    net_item_discount+= Double.parseDouble(localCart.get(j).getDiscount());
                }
            }

            SharedPreferences sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
            String selectCustomerId = sharedPreferences.getString("customerId", "");
            customerDetails=dbHelper.getCustomer(selectCustomerId);

            if (customerDetails.get(0).getTaxType().equals("I")){
                taxTitle.setText("GST ( Inc )");
            }else if (customerDetails.get(0).getTaxType().equals("E")){
                taxTitle.setText("GST ( Exc )");
            }else {
                taxTitle.setText("GST ( Zero )");
            }

         /*   if (customerDetails.get(0).getTaxType().equals("I")){

                taxText.setText(fourDecimalPoint(net_tax));
                netTotalText.setText("$ "+ twoDecimalPoint(net_total));
                subTotalTextValue.setText("$ "+twoDecimalPoint(sub_total));
                itemDiscountText.setText(twoDecimalPoint(net_item_discount));

                double sub_total1=net_total - net_tax;
                subTotalTextValue.setText(Utils.twoDecimalPoint(sub_total));
                subTotalText.setText("$ "+twoDecimalPoint(sub_total1));
                double sub_total12=sub_total1 + net_tax;

                subTotalValue= String.valueOf(sub_total12);
                netTax=fourDecimalPoint(net_tax);
                itemDiscountAmount=twoDecimalPoint(net_item_discount);
                totalValue=Utils.twoDecimalPoint(total_value);
                netTotalvalue=twoDecimalPoint(net_total);

            }else {

                subTotalText.setText("$ "+twoDecimalPoint(sub_total));
                taxText.setText(fourDecimalPoint(net_tax));
                netTotalText.setText("$ "+ twoDecimalPoint(net_total));
                subTotalTextValue.setText("$ "+twoDecimalPoint(sub_total));
                itemDiscountText.setText(twoDecimalPoint(net_item_discount));

                totalValue=Utils.twoDecimalPoint(total_value);
                subTotalValue= String.valueOf(sub_total);
                netTax=fourDecimalPoint(net_tax);
                netTotalvalue=twoDecimalPoint(net_total);
                itemDiscountAmount=twoDecimalPoint(net_item_discount);*/

           // }

        }catch (Exception ex){
            Log.e("Exp:UpdatingTotal", Objects.requireNonNull(ex.getMessage()));
        }
    }


    @Override
    public void onBackPressed() {
        //Execute your code here
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            switch (AddInvoiceActivityOld.activityFrom) {
                case "SalesOrder":
                case "SalesEdit":
                case "ReOrderSales":
                    Log.w("ActionFromvalue:", AddInvoiceActivityOld.activityFrom);
                    showSaveOption("SalesOrder");
                    break;
                case "Invoice":
                case "InvoiceEdit":
                case "ConvertInvoice":
                case "ReOrderInvoice":
                    Log.w("ActionFromvalue:", AddInvoiceActivityOld.activityFrom);
                    showSaveOption("Invoice");
                    break;

            }
            return true;
        }else if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCustomerDetails(String customerCode) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        String url= Utils.getBaseUrl(this) +"MasterApi/GetCustomer?Requestdata={\"CustomerCode\": \""+customerCode+"\",\"CompanyCode\": \""+companyCode+"\"}";
        Log.w("Given_url:",url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Response_is:",response.toString());
                        if (response.length()>0) {
                            if (response.optString("ErrorMessage").equals("null")){
                                customerResponse=response;
                            }else {
                                Toast.makeText(getApplicationContext(),"Error in getting response",Toast.LENGTH_LONG).show();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
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


    public  void createInvoiceJson(int copy) {

        JSONObject rootJsonObject = new JSONObject();
        JSONObject invoiceHeader = new JSONObject();
        JSONArray invoiceDetailsArray =new JSONArray();
        JSONObject invoiceObject =new JSONObject();

        try {
            // Sales Header Add values
            invoiceHeader.put("InvoiceNo", "");
            invoiceHeader.put("InvoiceDate", currentDate);
            invoiceHeader.put("TranDate", currentDate);
            invoiceHeader.put("InvoiceDateString", currentDate);
            invoiceHeader.put("TranDateString",currentDate);
            invoiceHeader.put("OrderDateString",currentDate);
            invoiceHeader.put("ProductStatus",null);
            invoiceHeader.put("OrderNo", "");
            invoiceHeader.put("ContactPerson", "");
            invoiceHeader.put("QuoteNo", "");
            invoiceHeader.put("FReceivedAmount",null);
            invoiceHeader.put("ReceivedAmount",null);
            invoiceHeader.put("CustomerCode", customerResponse.get("CustomerCode"));
            invoiceHeader.put("CustomerName", customerResponse.get("CustomerName"));
            invoiceHeader.put("Address1", customerResponse.get("Address1"));
            invoiceHeader.put("Address2", customerResponse.get("Address2"));
            invoiceHeader.put("Address3", customerResponse.get("Address3"));
            invoiceHeader.put("CreditLimit", customerResponse.get("CreditLimit"));
            invoiceHeader.put("OutstandingBalance", "0");
            invoiceHeader.put("Attention", "");
            invoiceHeader.put("Remarks", "");
            invoiceHeader.put("CurrencyName", "");
            invoiceHeader.put("Total", netAmount);
            invoiceHeader.put("FTotal", "0");
            invoiceHeader.put("ItemDiscount", "0");
            invoiceHeader.put("FItemDiscount", "0");
            invoiceHeader.put("TotalDiscount","0");
            invoiceHeader.put("BillDIscount", "0");
            invoiceHeader.put("FBillDiscount", "0");
            invoiceHeader.put("BillDiscountPercentage", "0");
            invoiceHeader.put(" InvoiceNextNo", 0.0);
            invoiceHeader.put( "PaidAmount", null);
            invoiceHeader.put("CreditAmount", null);
            invoiceHeader.put("DepositAmount", null);
            invoiceHeader.put("BalanceAmount", null);
            invoiceHeader.put("SpecialRemarks", null);
            invoiceHeader.put("DeletedRemarks", null);
            invoiceHeader.put("SubTotal", netsubTotal);
            invoiceHeader.put("FSubTotal", "0");
            invoiceHeader.put("Tax", netTax);
            invoiceHeader.put("FTax", "0");
            invoiceHeader.put("NetTotal", netAmount);
            invoiceHeader.put("FNetTotal", "0");
            invoiceHeader.put("DeliveryCode", customerResponse.optString("DeliveryCode"));
            invoiceHeader.put("DelCustomerName", "");
            invoiceHeader.put("DelAddress1", "");
            invoiceHeader.put("DelAddress2 ", "");
            invoiceHeader.put("DelAddress3 ", "");
            invoiceHeader.put("DelPhoneNo", customerResponse.optString("ContactNo"));
            invoiceHeader.put("Remarks", customerResponse.optString("Remarks"));
            invoiceHeader.put("HaveTax", customerResponse.optString("HaveTax"));
            invoiceHeader.put("TaxType", customerResponse.optString("TaxType"));
            invoiceHeader.put("TaxPerc", customerResponse.optString("TaxPerc"));
            invoiceHeader.put("TaxCode", customerResponse.optString("TaxCode"));
            invoiceHeader.put("CompanyShortCode", "");
            invoiceHeader.put("CurrencyCode", customerResponse.optString("CurrencyCode"));
            invoiceHeader.put("CurrencyValue","");
            invoiceHeader.put("CurrencyRate", "1");
            invoiceHeader.put("Status", "0");
            invoiceHeader.put("PostalCode", customerResponse.optString("PostalCode"));
            invoiceHeader.put("DelPostalcode", "");
            invoiceHeader.put("CreateUser",userName);
            invoiceHeader.put("CreateDate",null);
            invoiceHeader.put("ModifyUser",userName);
            invoiceHeader.put("ModifyDate",null);
            invoiceHeader.put("ErrorMessage","");
            invoiceHeader.put("CreateDateString","");
            invoiceHeader.put("Email",customerResponse.optString("Email"));
            invoiceHeader.put("Website",customerResponse.optString("Website"));
            invoiceHeader.put("CompanyName",customerResponse.optString("CompanyName"));
            invoiceHeader.put("VanName",customerResponse.optString("VanName"));
            invoiceHeader.put("IsApproved",false);
            invoiceHeader.put("ApprovedBy","");
            invoiceHeader.put("ApprovedDate",null);
            invoiceHeader.put("ApprovedDateString","");
            invoiceHeader.put("SoPrintCount",null);
            invoiceHeader.put("PaidAmount",null);
            invoiceHeader.put("BalanceAmount",null);
            invoiceHeader.put("CustomerContactCode","0");
            invoiceHeader.put("FBillDiscount","");
            invoiceHeader.put("FaxNo","");
            invoiceHeader.put("OutstandingBalance","");
            invoiceHeader.put("CreditLimit","");
            invoiceHeader.put("OutstandingBalance","");
            invoiceHeader.put("PageNo","0");
            invoiceHeader.put("PageSize","0");
            invoiceHeader.put("Mode", "I");
            invoiceHeader.put("SoDate",null);
            invoiceHeader.put("CompanyCode",companyCode);
            invoiceHeader.put("DeliveryDate",null);
            invoiceHeader.put("LocationCode",locationCode);
            invoiceHeader.put("IsRequiredWhatsAppMessage","");

            // Sales Details Add to the Objects
            ArrayList<CartModel> localCart=dbHelper.getAllCartItems();

            int index=1;
            for (CartModel model:localCart){
                invoiceObject=new JSONObject();
                invoiceObject.put("InvoiceNo","");
                invoiceObject.put("CompanyCode",companyCode);
                invoiceObject.put("AverageCost", null);
                invoiceObject.put("InvoiceQty", null);
                invoiceObject.put(        "InvoiceFocQty", null);
                invoiceObject.put(  "CreateUser", null);
                invoiceObject.put(        "InvoiceDate", currentDate);
                invoiceObject.put("InvoiceDateString", currentDate);
                invoiceObject.put(                    "Profit", null);
                invoiceObject.put("slNo",index);
                invoiceObject.put("ProductCode",model.getCART_COLUMN_PID());
                invoiceObject.put("ProductName",model.getCART_COLUMN_PNAME());
                invoiceObject.put("CQty",model.getCART_COLUMN_CTN_QTY());
                invoiceObject.put("LQty",model.getCART_COLUMN_QTY());
                double data = Double.parseDouble(model.getCART_PCS_PER_CARTON());
                double cn_qty=Double.parseDouble(model.getCART_COLUMN_CTN_QTY());
                double lqty=Double.parseDouble(model.getCART_COLUMN_QTY());
                double net_qty=(cn_qty*data)+lqty;
                invoiceObject.put("Qty",String.valueOf(net_qty));
                // convert into int
                int value = (int)data;
                invoiceObject.put("PcsPerCarton",String.valueOf(value));
                invoiceObject.put("Price",model.getCART_UNIT_PRICE());
                invoiceObject.put("CartonPrice",model.getCART_COLUMN_CTN_PRICE());
                invoiceObject.put("Total",model.getCART_TOTAL_VALUE());
                invoiceObject.put("ItemDiscount","0");
                invoiceObject.put("ItemDiscountPercentage","0");
                invoiceObject.put("Tax",model.getCART_TAX_VALUE());
                invoiceObject.put("SubTotal",model.getCART_TOTAL_VALUE());
                invoiceObject.put("NetTotal",model.getCART_COLUMN_NET_PRICE());
                invoiceObject.put("TaxType",customerResponse.optString("TaxType"));
                invoiceObject.put("TaxPerc",customerResponse.optString("TaxPerc"));
                invoiceObject.put("ReturnSubTotal",null);
                invoiceObject.put("BillDiscount", "0");
                invoiceObject.put("TotalDiscount", "0");
                invoiceObject.put("InvoiceQty", null);
                invoiceObject.put("InvoiceFocQty", null);
                invoiceObject.put("FOCQty", 0);
                invoiceObject.put("FTotal","0");
                invoiceObject.put("TaxCode",customerResponse.optString("TaxCode"));
                invoiceObject.put("UOMCode","CTN");
                invoiceObject.put("FItemDiscount","0");
                invoiceObject.put("FBillDiscount" ,"0");
                invoiceObject.put("FSubTotal","0");
                invoiceObject.put( "FTax","0");
                invoiceObject.put("FNetTotal","0");
                invoiceObject.put("RetailPrice","0");
                invoiceObject.put("FPrice","0");
                invoiceObject.put("ItemRemarks","");
                invoiceObject.put("ExchangeQty", "0");
                invoiceObject.put("KiloPrice" ,null);
                invoiceObject.put("FKiloPrice",null);
                invoiceObject.put("ProductBarcode",null);
                invoiceObject.put("ProductStatus","");
                invoiceObject.put("LocationCode",locationCode);
                invoiceObject.put("DoNo","");
                invoiceObject.put("DoDate",null);
                invoiceObject.put("InvoiceNo",null);
                invoiceObject.put("InvoiceDate",null);
                invoiceObject.put("CreateUser",userName);
                invoiceObject.put("ModifyUser",userName);
                invoiceObject.put("ModifyDate",null);
                invoiceObject.put("StockUpdated", null);
                invoiceObject.put("ReturnCQty", null);
                invoiceObject.put("ReturnLQty", null);
                invoiceObject.put(       "ReturnQty", null);
                invoiceObject.put(       "SoCQty", null);
                invoiceObject.put(       "SoLQty", null);
                invoiceObject.put(       "SoQty", null);
                invoiceObject.put(       "SoFOCQty", null);
                invoiceObject.put(                    "HaveMFg", null);
                invoiceObject.put("IsFrozen", null);
                invoiceObject.put("NonStockItem", null);
                invoiceObject.put("ReturnFSubTotal", null);
                invoiceObject.put(      "ReturnFTax", null);
                invoiceObject.put(      "ReturnFNetTotal", null);
                invoiceObject.put(      "DeliveryOrderNo", null);
                invoiceObject.put("DoQty",null);
                invoiceObject.put("DoFocQty",null);
                invoiceObject.put("InvoiceQty",null);
                invoiceObject.put("InvoiceFocQty",null);
                invoiceObject.put("ErrorMessage","");
                invoiceObject.put("IsClosed",null);
                invoiceObject.put("InvoiceCQty","");
                invoiceObject.put("DOCQty","");
                invoiceObject.put("MinimumSellingPrice","");
                invoiceObject.put("ProductWeight","");
                invoiceObject.put("HaveBatch","");
                invoiceObject.put("HaveExpiry","");
                invoiceObject.put("Haveserial",null);
                invoiceObject.put("HaveMfg",null);
                invoiceObject.put("HavePackage","");
                invoiceObject.put("OriginalCQty",null);
                invoiceObject.put("OriginalQty",null);
                invoiceObject.put("IsScanned",null);
                invoiceObject.put("ProductPrice","0");
                invoiceObject.put("ProductCartonPrice","0");
                invoiceObject.put("ProductAverageCost","0");
                invoiceObject.put("ProductUom","");
                invoiceObject.put("ProductQty","0");
                invoiceObject.put("ProductNoOfCarton","0");
                invoiceObject.put("HaveSerial","");
                invoiceObject.put("HaveAttribute","");
                invoiceObject.put("MinimumCartonSellingPrice",null);
                invoiceObject.put("ProductTaxPerc",null);
                invoiceObject.put("BrandName","");
                invoiceDetailsArray.put(invoiceObject);
                index++;
            }

            rootJsonObject.put("IsSaveSO",false);
            rootJsonObject.put("InvoiceHeader", invoiceHeader);
            rootJsonObject.put("InvoiceDetail", invoiceDetailsArray);

            saveSalesOrder(rootJsonObject,"Invoice",copy);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("Given_Error:", Objects.requireNonNull(e.getMessage()));
        }


    }




    public  void createAndValidateJsonObject(int copy) {
        JSONObject rootJsonObject = new JSONObject();
        JSONObject saleHeader = new JSONObject();
        JSONArray saleDetailsArray=new JSONArray();
        JSONObject saleObject=new JSONObject();
        try {
            // Sales Header Add values

            saleHeader.put("SoNo", "");
            saleHeader.put("Mode", "I");
            saleHeader.put("SoDateString", currentDateString);
            saleHeader.put("OrderDateString", currentDateString);
            saleHeader.put("DeliveryDateString", null);
            saleHeader.put("OrderNo", "");
            saleHeader.put("ContactPerson", "");
            saleHeader.put("QuoteNo", "");
            saleHeader.put("CustomerCode", customerResponse.get("CustomerCode"));
            saleHeader.put("CustomerName", customerResponse.get("CustomerName"));
            saleHeader.put("Address1", customerResponse.get("Address1"));
            saleHeader.put("Address2", customerResponse.get("Address2"));
            saleHeader.put("Address3", customerResponse.get("Address3"));
            saleHeader.put("CreditLimit", customerResponse.get("CreditLimit"));
            saleHeader.put("OutstandingBalance", "0");
            saleHeader.put("Attention", "");
            saleHeader.put("Remarks", CustomerFragment.remarksEditText.getText().toString());
            saleHeader.put("CurrencyName", "");
            saleHeader.put("Total", netAmount);
            saleHeader.put("FTotal", "0");
            saleHeader.put("ItemDiscount", "0");
            saleHeader.put("FItemDiscount", "0");
            saleHeader.put("BillDIscount", "0");
            saleHeader.put("FBillDiscount", "0");
            saleHeader.put("BillDiscountPercentage", "0");
            saleHeader.put("SubTotal", netsubTotal);
            saleHeader.put("FSubTotal", "0");
            saleHeader.put("Tax", netTax);
            saleHeader.put("FTax", "0");
            saleHeader.put("NetTotal", netAmount);
            saleHeader.put("FNetTotal", "0");
            saleHeader.put("DeliveryCode", customerResponse.optString("DeliveryCode"));
            saleHeader.put("DelCustomerName", "");
            saleHeader.put("DelAddress1", "");
            saleHeader.put("DelAddress2 ", "");
            saleHeader.put("DelAddress3 ", "");
            saleHeader.put("DelPhoneNo", customerResponse.optString("ContactNo"));
            saleHeader.put("Remarks", customerResponse.optString("Remarks"));
            saleHeader.put("HaveTax", customerResponse.optString("HaveTax"));
            saleHeader.put("TaxType", customerResponse.optString("TaxType"));
            saleHeader.put("TaxPerc", customerResponse.optString("TaxPerc"));
            saleHeader.put("TaxCode", customerResponse.optString("TaxCode"));
            saleHeader.put("CompanyShortCode", "");
            saleHeader.put("CurrencyCode", customerResponse.optString("CurrencyCode"));
            saleHeader.put("CurrencyValue","");
            saleHeader.put("CurrencyRate", "1");
            saleHeader.put("Status", "0");
            saleHeader.put("PostalCode", customerResponse.optString("PostalCode"));
            saleHeader.put("DelPostalcode", "");
            saleHeader.put("CreateUser",userName);
            saleHeader.put("CreateDate",null);
            saleHeader.put("ModifyUser",userName);
            saleHeader.put("ModifyDate",null);
            saleHeader.put("ErrorMessage","");
            saleHeader.put("CreateDateString","");
            saleHeader.put("Email",customerResponse.optString("Email"));
            saleHeader.put("Website",customerResponse.optString("Website"));
            saleHeader.put("CompanyName",customerResponse.optString("CompanyName"));
            saleHeader.put("VanName",customerResponse.optString("VanName"));
            saleHeader.put("IsApproved",false);
            saleHeader.put("ApprovedBy","");
            saleHeader.put("ApprovedDate",null);
            saleHeader.put("ApprovedDateString","");
            saleHeader.put("SoPrintCount",null);
            saleHeader.put("PaidAmount",null);
            saleHeader.put("BalanceAmount",null);
            saleHeader.put("CustomerContactCode","0");
            saleHeader.put("FBillDiscount","");
            saleHeader.put("FaxNo","");
            saleHeader.put("OutstandingBalance","");
            saleHeader.put("CreditLimit","");
            saleHeader.put("OutstandingBalance","");
            saleHeader.put("PageNo","0");
            saleHeader.put("PageSize","0");
            saleHeader.put("SoDate",null);
            saleHeader.put("CompanyCode",companyCode);
            saleHeader.put("DeliveryDate",null);
            saleHeader.put("LocationCode",locationCode);
            saleHeader.put("IsRequiredWhatsAppMessage","");

            // Sales Details Add to the Objects
            ArrayList<CartModel> localCart=dbHelper.getAllCartItems();

            Log.w("Given_local_cart_size:", String.valueOf(localCart.size()));

            int index=0;
            for (CartModel model:localCart){
                saleObject=new JSONObject();

                saleObject.put("SoNo", "");
                saleObject.put("CompanyCode",companyCode);
                saleObject.put("slNo",index);
                saleObject.put("ProductCode",model.getCART_COLUMN_PID());
                saleObject.put("ProductName",model.getCART_COLUMN_PNAME());
                saleObject.put("CQty",model.getCART_COLUMN_CTN_QTY());
                saleObject.put("LQty",model.getCART_COLUMN_QTY());
                double data = Double.parseDouble(model.getCART_PCS_PER_CARTON());
                double cn_qty=Double.parseDouble(model.getCART_COLUMN_CTN_QTY());
                double lqty=Double.parseDouble(model.getCART_COLUMN_QTY());
                double net_qty=(cn_qty*data)+lqty;
                saleObject.put("Qty",String.valueOf(net_qty));
                // convert into int
                int value = (int)data;
                saleObject.put("PcsPerCarton",String.valueOf(value));
                saleObject.put("Price",model.getCART_UNIT_PRICE());
                saleObject.put("CartonPrice",model.getCART_COLUMN_CTN_PRICE());
                saleObject.put("Total",model.getCART_TOTAL_VALUE());
                saleObject.put("ItemDiscount",model.getDiscount());
                saleObject.put("ItemDiscountPercentage","0");
                saleObject.put("Tax",model.getCART_TAX_VALUE());
                saleObject.put("SubTotal",model.getCART_TOTAL_VALUE());
                saleObject.put("NetTotal",model.getCART_COLUMN_NET_PRICE());
                saleObject.put("TaxType",customerResponse.optString("TaxType"));
                saleObject.put("TaxPerc",customerResponse.optString("TaxPerc"));
                saleObject.put("BillDiscount", "0");
                saleObject.put("TotalDiscount", "0");
                saleObject.put("FOCQty", 0);
                saleObject.put("FTotal","0");
                saleObject.put("TaxCode",customerResponse.optString("TaxCode"));
                saleObject.put("UOMCode","CTN");
                saleObject.put("FItemDiscount","0");
                saleObject.put("FBillDiscount" ,"0");
                saleObject.put("FSubTotal","0");
                saleObject.put( "FTax","0");
                saleObject.put("FNetTotal","0");
                saleObject.put("RetailPrice","0");
                saleObject.put("FPrice","0");
                saleObject.put("ItemRemarks","");
                saleObject.put("ExchangeQty", "0");
                saleObject.put("KiloPrice" ,null);
                saleObject.put("FKiloPrice",null);
                saleObject.put("ProductBarcode",null);
                saleObject.put("ProductStatus","");
                saleObject.put("LocationCode",locationCode);
                saleObject.put("DoNo","");
                saleObject.put("DoDate",null);
                saleObject.put("InvoiceNo",null);
                saleObject.put("InvoiceDate",null);
                saleObject.put("CreateUser",userName);
                saleObject.put("ModifyUser",userName);
                saleObject.put("ModifyDate",null);
                saleObject.put("DoQty",null);
                saleObject.put("DoFocQty",null);
                saleObject.put("InvoiceQty",null);
                saleObject.put("InvoiceFocQty",null);
                saleObject.put("ErrorMessage","");
                saleObject.put("IsClosed",null);
                saleObject.put("InvoiceCQty","");
                saleObject.put("DOCQty","");
                saleObject.put("MinimumSellingPrice","");
                saleObject.put("ProductWeight","");
                saleObject.put("HaveBatch","");
                saleObject.put("HaveExpiry","");
                saleObject.put("Haveserial",null);
                saleObject.put("HaveMfg",null);
                saleObject.put("HavePackage","");
                saleObject.put("OriginalCQty",null);
                saleObject.put("OriginalQty",null);
                saleObject.put("IsScanned",null);
                saleObject.put("ProductPrice","0");
                saleObject.put("ProductCartonPrice","0");
                saleObject.put("ProductAverageCost","0");
                saleObject.put("ProductUom","");
                saleObject.put("ProductQty","0");
                saleObject.put("ProductNoOfCarton","0");
                saleObject.put("HaveSerial","");
                saleObject.put("HaveAttribute","");
                saleObject.put("MinimumCartonSellingPrice",null);
                saleObject.put("ProductTaxPerc",null);
                saleObject.put("BrandName","");
                saleDetailsArray.put(saleObject);
                index++;
            }

            rootJsonObject.put("IsSaveSO",true);
            rootJsonObject.put("SOHeader",saleHeader);
            rootJsonObject.put("SODetail",saleDetailsArray);

            saveSalesOrder(rootJsonObject,"SalesOrder",copy);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("Given_Error:", Objects.requireNonNull(e.getMessage()));
        }
    }

    public  void saveSalesOrder(JSONObject jsonBody,String action,int copy){
        try {
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            if (action.equals("SalesOrder")){
                pDialog.setTitleText("Saving Sales Order...");
            }else {
                pDialog.setTitleText("Saving Invoice...");
            }
            pDialog.setCancelable(false);
            pDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            Log.w("GivenInvoiceRequest:",jsonBody.toString());
            String URL= Utils.getBaseUrl(this)+"SalesApi/SaveSO_API";
            Log.w("Given_InvoiceApi:",URL);
            StringRequest salesOrderRequest = new StringRequest(Request.Method.POST, URL, response -> {
                Log.w("Invoice_Response:",response.toString());
                dbHelper.removeAllItems();
                dbHelper.removeCustomer();
                pDialog.dismiss();
                if (action.equals("SalesOrder") || action.equals("SalesEdit")){
                    if (isPrintEnable){
                        Intent intent=new Intent(getApplicationContext(), SalesOrderListActivity.class);
                        intent.putExtra("printSoNumber",response);
                        intent.putExtra("noOfCopy",String.valueOf(copy));
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent=new Intent(getApplicationContext(), SalesOrderListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    isPrintEnable=false;
                }else {
                    if (isPrintEnable){
                        Intent intent=new Intent(getApplicationContext(), NewInvoiceListActivity.class);
                        intent.putExtra("printInvoiceNumber",response);
                        intent.putExtra("noOfCopy",String.valueOf(copy));
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent=new Intent(getApplicationContext(), NewInvoiceListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    isPrintEnable=false;
                }
            }, error -> {
                Log.w("SalesOrder_Response:",error.toString());
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_menu, menu);

        MenuItem total=menu.findItem(R.id.action_total);
        total.setVisible(false);

        return true;
    }

    public static void showSaveOption(String action){
        if (action.equals("SalesOrder")){
            cashCollected.setVisibility(View.GONE);
            deliveryPrint.setVisibility(View.GONE);
            invoicePrint.setVisibility(View.VISIBLE);
            invoicePrint.setText("Sales Order Print");
        }else {
            cashCollected.setVisibility(View.VISIBLE);
            deliveryPrint.setVisibility(View.VISIBLE);
            invoicePrint.setVisibility(View.VISIBLE);
            invoicePrint.setText("Invoice Print");
        }
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        saveAction=action;
    }
}