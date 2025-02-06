package com.winapp.svcatering.activity;

import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.SelectProductAdapter;
import com.winapp.svcatering.adapter.TransferNewSummaryAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.AppUtils;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CreateInvoiceModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.HomePageModel;
import com.winapp.svcatering.model.ItemGroupList;
import com.winapp.svcatering.model.ProductSummaryModel;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.thermalprinter.PrinterUtils;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class TransferProductAddActivity extends AppCompatActivity {

    LinearLayout returnLayout;
    ImageView showHideButton;
    private TransferNewSummaryAdapter productSummaryAdapter;
    private ArrayList<ProductSummaryModel> productSummaryList;
    RecyclerView productSummaryView;
    String companyCode;
    String username;
    HashMap<String ,String> user;
    SessionManager session;
    SweetAlertDialog pDialog;
    public static ArrayList<ProductsModel> productList;
    public static ArrayList<ProductsModel> stockProductList;
    private RecyclerView productListView;
    private SelectProductAdapter selectProductAdapter;
    Button btnCancel;
    public static BottomSheetBehavior behavior;
    ImageView searchProduct;
    EditText productNameEditext;
    LinearLayout transLayout;
    TextView totalProducts;
    ArrayList<String> products;
    AutoCompleteTextView productAutoComplete;
    ArrayAdapter<String> autoCompleteAdapter;
    EditText cartonPrice;
    EditText loosePrice;
    EditText uomText;
    EditText pcsPerCarton;
    EditText stockCount;
    EditText qtyValue;
    EditText returnQtyText;
    EditText customerNameText;
    EditText priceText;
    TextView subTotalValue;
    TextView taxValueText;
    TextView netTotalValue;
    ArrayList<CustomerDetails> customerDetails;
    private DBHelper dbHelper;
    TextView taxTitle;
    Button addProduct;
    ProductsModel productsModel;
    TextView itemCount;
    TextView noproductText;
    String productId;
    String productName;
    boolean isCartonQtyEdit=false;
    boolean isQtyEdit=false;
    TextWatcher qtyTextWatcher;
    TextWatcher cartonTextWatcher;
    TextWatcher lqtyTextWatcher;
    TextWatcher cartonPriceTextWatcher;
    TextWatcher loosePriceTextWatcher;
    Switch focSwitch;
    Switch exchangeSwitch;
    Switch returnSwitch;
    EditText focEditText;
    EditText exchangeEditext;
    EditText discountEditext;
    EditText returnEditext;
    ImageView scanProduct;
    int RESULT_CODE=12;
    String locationCode;
    TextView stockQtyValue;
    LinearLayout stockLayout;
    private TextWatcher cqtyTW, lqtyTW, qtyTW;
    String beforeLooseQty,ss_Cqty;
    boolean isAllowLowStock=false;
    LinearLayout focLayout;
    FloatingActionButton sortButton;
    LinearLayout emptyLayout;
    LinearLayout productLayout;

    private boolean isViewShown = false;
    public String selectCustomerId;
    static ProgressDialog progressDialog;
    public static String stockProductView="2";

    boolean isReverseCalculationEnabled=true;
    boolean isCartonPriceEdit=true;
    boolean isPriceEdit=true;
    public CheckBox salesReturn;
    public TextView salesReturnText;
    public TextView minimumSellingPriceText;
    public static EditText barcodeText;
    public static String customerCode;
    public static boolean isPrintEnable=false;
    private ImageView cancelSheet;
    public static TextView selectedBank;
    private Button cancelButton;
    private Button okButton;
    public static EditText amountText;
    private AlertDialog alert;
    public CheckBox invoicePrintCheck;

    static double currentLocationLatitude=0.0;
    static double currentLocationLongitude=0.0;
    public static String signatureString="";
    public static String imageString;

    public static String current_latitude="0.00";
    public static String current_longitude="0.00";
    public static String currentDate;
    public String companyName;
    public static JSONObject customerResponse=new JSONObject();

    private String printerMacId;
    private String printerType;
    private SharedPreferences sharedPreferences;
    private EditText remarkText;
    private String activityFrom="";
    public TextView saveTitle;
    private TextView saveMessage;
    private String editSoNumber="";
    public TextView returnAdj;
    public LinearLayout returnLayoutView;
    public Button cancelReturn;
    public Button saveReturn;
    public TextView netReturnQty;
    public EditText expiryReturnQty;
    public EditText damageReturnQty;
    public TextWatcher expiryQtyTextWatcher;
    public TextWatcher damageQtyTextWatcher;
    public TextView invoiceDate;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String fromLocationCode;
    private String stockRequestNo="";
    private String toLocationCode;
    private String remarks;
    private TextView fromLocation;
    private TextView toLocation;
    private TextView netQtyValue;
    private String transferType;
    private ArrayList<ItemGroupList> itemGroup;
    private Spinner groupspinner;
    private TextView brandTitle;
    private boolean isCheckStock=true;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_product_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.w("activity_cg",getClass().getSimpleName().toString());

        signatureString="";

        session=new SessionManager(this);
        user=session.getUserDetails();
        dbHelper=new DBHelper(this);
        progressDialog =new ProgressDialog(this);
        customerDetails=dbHelper.getCustomer();
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        companyName=user.get(SessionManager.KEY_COMPANY_NAME);
        username=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        returnLayout=findViewById(R.id.return_layout);
        showHideButton=findViewById(R.id.show_hide);
        productSummaryView=findViewById(R.id.product_summary);
        productListView=findViewById(R.id.productList);
        btnCancel=findViewById(R.id.cancel_sheet);
        searchProduct=findViewById(R.id.search_product);
        productNameEditext=findViewById(R.id.product_search);
        transLayout=findViewById(R.id.trans_layout);
        totalProducts=findViewById(R.id.total_products);
        productAutoComplete=findViewById(R.id.product_name);
        cartonPrice=findViewById(R.id.carton_price);
        priceText=findViewById(R.id.price);
        loosePrice=findViewById(R.id.loose_price);
        pcsPerCarton=findViewById(R.id.pcs_per_ctn);
        uomText=findViewById(R.id.uom);
        stockCount=findViewById(R.id.stock_count);
        brandTitle=findViewById(R.id.brand_title);
        qtyValue=findViewById(R.id.qty);
        subTotalValue=findViewById(R.id.balance_value);
        taxValueText=findViewById(R.id.tax);
        netTotalValue=findViewById(R.id.net_total);
        taxTitle=findViewById(R.id.tax_title);
        addProduct=findViewById(R.id.add_product);
        itemCount=findViewById(R.id.item_count);
        noproductText=findViewById(R.id.no_product_text);
        focSwitch=findViewById(R.id.foc_switch);
        exchangeSwitch=findViewById(R.id.exchange_switch);
        returnSwitch=findViewById(R.id.return_switch);
        focEditText=findViewById(R.id.foc);
        exchangeEditext=findViewById(R.id.exchange_text);
        returnEditext=findViewById(R.id.return_text);
        discountEditext=findViewById(R.id.discount_text);
        groupspinner=findViewById(R.id.spinner_group);
        scanProduct=findViewById(R.id.scan_product);
        stockQtyValue=findViewById(R.id.stock_qty);
        stockLayout=findViewById(R.id.stock_layout);
        focLayout=findViewById(R.id.foc_layout);
        emptyLayout=findViewById(R.id.empty_layout);
        sortButton=findViewById(R.id.fab);
        productLayout=findViewById(R.id.product_layout);
        salesReturn=findViewById(R.id.sales_return);
        salesReturnText=findViewById(R.id.sales_return_text);
        minimumSellingPriceText=findViewById(R.id.minimum_selling_price);
        barcodeText=findViewById(R.id.barcode_text);
        returnQtyText=findViewById(R.id.return_qty);
        customerNameText=findViewById(R.id.customer_name_text);
        remarkText=findViewById(R.id.remarks);
        returnAdj=findViewById(R.id.return_adj);
        returnLayoutView=findViewById(R.id.return_layout_view);
        cancelReturn=findViewById(R.id.cancel_return);
        saveReturn=findViewById(R.id.save_return);
        netReturnQty=findViewById(R.id.net_return_qty);
        expiryReturnQty=findViewById(R.id.saleable_return_qty);
        damageReturnQty=findViewById(R.id.damage_qty);
        invoiceDate=findViewById(R.id.invoice_date);
        fromLocation=findViewById(R.id.from_location);
        toLocation=findViewById(R.id.to_location);
        netQtyValue=findViewById(R.id.net_qty_value);
        products=new ArrayList<>();
        productAutoComplete.clearFocus();
        barcodeText.requestFocus();
     //   brandTitle.setVisibility(View.GONE);

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        printerType=sharedPreferences.getString("printer_type","");
        printerMacId=sharedPreferences.getString("mac_address","");

        productAutoComplete.setEnabled(false);

        //  PrinterUtils printerUtils=new PrinterUtils(this,printerMacId);
        //  printerUtils.connectPrinter();

        focEditText.setEnabled(false);
        exchangeEditext.setEnabled(false);
        discountEditext.setEnabled(false);
        returnEditext.setEnabled(false);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = df1.format(c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        invoiceDate.setText(formattedDate);


        if (getIntent()!=null){
            fromLocationCode=getIntent().getStringExtra("fromLocationCode");
            toLocationCode=getIntent().getStringExtra("toLocationCode");
            transferType=getIntent().getStringExtra("transferType");
            stockRequestNo=getIntent().getStringExtra("requestNo");
            getSupportActionBar().setTitle(transferType);
            if (!transferType.equals("Covert Transfer")){
                invoiceDate.setEnabled(true);
                invoiceDate.setText(getIntent().getStringExtra("currentDate"));
                currentDate=convertDate(invoiceDate.getText().toString());
            }else {
                invoiceDate.setEnabled(false);
            }
            remarks=getIntent().getStringExtra("remarks");
            fromLocation.setText(fromLocationCode);
            toLocation.setText(toLocationCode);
            getStockProductsList();
        }

       // groupspinner.setVisibility(View.GONE);

        try {
            getGrouplist();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
        selectCustomerId = sharedPreferences.getString("customerId", "");
        if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
            customerDetails = dbHelper.getCustomer(selectCustomerId);
          //  getCustomerDetails(selectCustomerId,false,"");
        }

        View bottomSheet =findViewById(R.id.design_bottom_sheet);
        bottomSheet.setBackgroundColor(Color.parseColor("#F2F2F2"));
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
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
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


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        searchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        productNameEditext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                String product = editable.toString();
                if (!product.isEmpty()){
                    filter(product);
                }else {
                    setAdapter(AppUtils.getProductsList());
                }
            }
        });

        returnQtyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()){
                    expiryReturnQty.setText(editable.toString());
                }else {
                    expiryReturnQty.setText("");
                }
            }
        });

        expiryQtyTextWatcher =new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!returnQtyText.getText().toString().isEmpty()){
                    String netreturnqty=returnQtyText.getText().toString();
                    int net_qty=Integer.parseInt(netreturnqty);
                    if(!s.toString().isEmpty()){
                        int damageqty=Integer.parseInt(s.toString());
                        if (net_qty > damageqty){
                            int value=net_qty-damageqty;
                            damageReturnQty.removeTextChangedListener(damageQtyTextWatcher);
                            damageReturnQty.setText(String.valueOf(value));
                            damageReturnQty.addTextChangedListener(damageQtyTextWatcher);
                        }else if (net_qty==damageqty){
                            damageReturnQty.removeTextChangedListener(damageQtyTextWatcher);
                            damageReturnQty.setText("0");
                            damageReturnQty.addTextChangedListener(damageQtyTextWatcher);
                        }else if (net_qty < damageqty){
                            expiryReturnQty.setText("0");
                            damageReturnQty.removeTextChangedListener(damageQtyTextWatcher);
                            damageReturnQty.setText(netreturnqty);
                            damageReturnQty.addTextChangedListener(damageQtyTextWatcher);
                            Toast.makeText(getApplicationContext(),"Return Qty Exceed...",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        damageReturnQty.removeTextChangedListener(damageQtyTextWatcher);
                        damageReturnQty.setText(netreturnqty);
                        damageReturnQty.addTextChangedListener(damageQtyTextWatcher);
                    }
                }
            }
        };
        expiryReturnQty.addTextChangedListener(expiryQtyTextWatcher);

      /*  expiryReturnQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!returnQtyText.getText().toString().isEmpty()){
                    String netreturnqty=returnQtyText.getText().toString();
                    int net_qty=Integer.parseInt(netreturnqty);
                    if(!s.toString().isEmpty()){
                        int damageqty=Integer.parseInt(s.toString());
                        if (net_qty > damageqty){
                            int value=net_qty-damageqty;
                            damageReturnQty.setText(String.valueOf(value));
                        }else if (net_qty==damageqty){
                            damageReturnQty.setText("0");
                        }else if (net_qty < damageqty){
                            expiryReturnQty.setText("");
                            damageReturnQty.setText(netreturnqty);
                            Toast.makeText(getApplicationContext(),"Return Qty Exceed...",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        damageReturnQty.setText(netreturnqty);
                    }
                }
            }
        });*/


        damageQtyTextWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!returnQtyText.getText().toString().isEmpty()){
                    String netreturnqty=returnQtyText.getText().toString();
                    int net_qty=Integer.parseInt(netreturnqty);
                    if(!s.toString().isEmpty()){
                        int damageqty=Integer.parseInt(s.toString());
                        if (net_qty > damageqty){
                            int value=net_qty-damageqty;
                            expiryReturnQty.removeTextChangedListener(expiryQtyTextWatcher);
                            expiryReturnQty.setText(String.valueOf(value));
                            expiryReturnQty.addTextChangedListener(expiryQtyTextWatcher);
                        }else if (net_qty==damageqty){
                            expiryReturnQty.removeTextChangedListener(expiryQtyTextWatcher);
                            expiryReturnQty.setText("0");
                            expiryReturnQty.addTextChangedListener(expiryQtyTextWatcher);
                        }else if (net_qty < damageqty){
                            damageReturnQty.setText("0");
                            expiryReturnQty.removeTextChangedListener(expiryQtyTextWatcher);
                            expiryReturnQty.setText(netreturnqty);
                            expiryReturnQty.addTextChangedListener(expiryQtyTextWatcher);
                            Toast.makeText(getApplicationContext(),"Return Qty Exceed...",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        expiryReturnQty.removeTextChangedListener(expiryQtyTextWatcher);
                        expiryReturnQty.setText(netreturnqty);
                        expiryReturnQty.addTextChangedListener(expiryQtyTextWatcher);
                    }
                }
            }
        };
        damageReturnQty.addTextChangedListener(damageQtyTextWatcher);

/*        damageReturnQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!returnQtyText.getText().toString().isEmpty()){
                    String netreturnqty=returnQtyText.getText().toString();
                    int net_qty=Integer.parseInt(netreturnqty);
                    if(!s.toString().isEmpty()){
                        int damageqty=Integer.parseInt(s.toString());
                        if (net_qty > damageqty){
                            int value=net_qty-damageqty;
                            expiryReturnQty.setText(String.valueOf(value));
                        }else if (net_qty==damageqty){
                            expiryReturnQty.setText("0");
                        }else if (net_qty < damageqty){
                            damageReturnQty.setText("");
                            expiryReturnQty.setText(netreturnqty);
                            Toast.makeText(getApplicationContext(),"Return Qty Exceed...",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        expiryReturnQty.setText(netreturnqty);
                    }
                }
            }
        });*/



        focEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //setCalculation();
                //setCalculationView();
                //setButtonView();
            }
        });


        loosePriceTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
             //   setCalculationView();
            }
        };
        priceText.addTextChangedListener(loosePriceTextWatcher);


        qtyTW = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setButtonView();
            }
        };

        qtyValue.addTextChangedListener(qtyTW);

        productAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selectProduct = (String)parent.getItemAtPosition(position);
                String[] product = selectProduct.split("~", 0);
                productId=product[1].trim();
                productName=product[0].trim();
                qtyValue.requestFocus();
                Log.w("Selected_product",product[1]);
                setProductResult(product[1].trim());
              /*  try {
                    getProductPrice(productId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

            }
        });


/*        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=productAutoComplete.getText().toString();
                if (!qtyValue.getText().toString().isEmpty() && !s.isEmpty() && !qtyValue.getText().toString().equals("0") && !qtyValue.getText().toString().equals("00") && !qtyValue.getText().toString().equals("000") && !qtyValue.getText().toString().equals("0000") && !netTotalValue.getText().toString().equals("0.00")){
                    if (addProduct.getText().toString().equals("Update")){
                        // if (!cartonPrice.getText().toString().isEmpty() && !cartonPrice.getText().toString().equals("0.00") && !cartonPrice.getText().toString().equals("0.0") && !cartonPrice.getText().toString().equals("0")){
                        if (priceText.getText() != null && !priceText.getText().toString().isEmpty()) {
                            if (Double.parseDouble(priceText.getText().toString()) > 0) {
                                double minimumsellingprice=Double.parseDouble(minimumSellingPriceText.getText().toString());
                                if (minimumsellingprice <= Double.parseDouble(priceText.getText().toString())){
                                    insertProducts();
                                }else {
                                    showMinimumSellingpriceAlert(minimumSellingPriceText.getText().toString());
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Price should not be zero", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter the price", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        if (priceText.getText().toString() != null && !priceText.getText().toString().isEmpty()) {
                            if (Double.parseDouble(priceText.getText().toString()) > 0) {
                                double minimumsellingprice=Double.parseDouble(minimumSellingPriceText.getText().toString());
                                if (minimumsellingprice <= Double.parseDouble(priceText.getText().toString())){
                                    addProduct("Add");
                                }else {
                                    showMinimumSellingpriceAlert(minimumSellingPriceText.getText().toString());
                                }
                            } else {
                                if ((focEditText.getText() != null && !focEditText.getText().toString().isEmpty() && !focEditText.getText().toString().equals("0")) || (returnEditext.getText() != null && !returnEditext.getText().toString().isEmpty() && !returnEditext.getText().toString().equals("0"))) {
                                    addProduct("Add");
                                } else {
                                    Toast.makeText(getApplicationContext(), "Price should not be zero", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter the price", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });*/

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=productAutoComplete.getText().toString();
                if (!qtyValue.getText().toString().isEmpty() && !s.isEmpty() && !qtyValue.getText().toString().equals("0") && !qtyValue.getText().toString().equals("00") && !qtyValue.getText().toString().equals("000") && !qtyValue.getText().toString().equals("0000") && !netTotalValue.getText().toString().equals("0.00")){
                    if (addProduct.getText().toString().equals("Update")){
                        if (transferType.equals("Stock Request")){
                            insertProducts();
                        }else {
                            if (checkLowStock()) {
                                insertProducts();
                            }
                        }
                    }else {
                        addProduct("Add");
                    }
                }
            }
        });

        returnAdj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnLayoutView.setVisibility(View.VISIBLE);
            }
        });

        cancelReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnLayoutView.setVisibility(View.GONE);
            }
        });

        saveReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (!expiryReturnQty.getText().toString().isEmpty() && !damageReturnQty.getText().toString().isEmpty()){
                    if (!damageReturnQty.getText().toString().isEmpty()){
             //           if (Integer.parseInt(damageReturnQty.getText().toString())> 0){
                            if (Integer.parseInt(expiryReturnQty.getText().toString()) == 0){
                                dbHelper.updateReturnQty("Delete","0","Expiry",productId);
                                dbHelper.updateReturnQty("Update",damageReturnQty.getText().toString(),"Damage",productId);
                            }else if (Integer.parseInt(damageReturnQty.getText().toString())==0){
                                dbHelper.updateReturnQty("Delete","0","Damage",productId);
                                dbHelper.updateReturnQty("Update",expiryReturnQty.getText().toString(),"Expiry",productId);
                            }else {
                                dbHelper.updateReturnQty("Update",damageReturnQty.getText().toString(),"Damage",productId);
                                dbHelper.updateReturnQty("Update",expiryReturnQty.getText().toString(),"Expiry",productId);
                            }
                      //  }
                    }
                }*/
                expiryReturnQty.clearFocus();
                damageReturnQty.clearFocus();
                returnLayoutView.setVisibility(View.GONE);
            }
        });

        invoiceDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate(invoiceDate);
            }
        });
        getProducts();


        // Setting the sorting
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View menuItemView = findViewById(R.id.fab);
                PopupMenu popupMenu = new PopupMenu(TransferProductAddActivity.this, menuItemView);
                popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_all:
                                setProductsDisplay("All Products");
                                return true;
                            case R.id.action_stock:
                                setProductsDisplay("In Stock");
                                return true;
                            case R.id.action_outstock:
                                setProductsDisplay("Out of Stock");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }

    public void setProductsDisplay(String action){
        try {
            // productsAdapterNew.setWithFooter(false);
            // sortAdapter.resetPosition();
            ArrayList<ProductsModel> filterdNames = new ArrayList<>();
            //   for (ProductsModel s : selectProductAdapter.getProductsList()) {
            for (ProductsModel s : AppUtils.getProductsList()) {
                if (action.equals("In Stock")){
                    if (s.getStockQty()!=null && !s.getStockQty().equals("null")){
                        if (Double.parseDouble(s.getStockQty())>0){
                            filterdNames.add(s);
                        }
                    }
                }else if (action.equals("Out of Stock")){
                    if (s.getStockQty()!=null && !s.getStockQty().equals("null")) {
                        if (Double.parseDouble(s.getStockQty()) < 0 || Double.parseDouble(s.getStockQty()) == 0) {
                            filterdNames.add(s);
                        }
                    }
                }else {
                    filterdNames.add(s);
                }
            }

            // Setting filter option in Localdb
            if (action.equals("In Stock")){
                dbHelper.insertSettings("stock_view","1");
            }else if (action.equals("Out of Stock")){
                dbHelper.insertSettings("stock_view","0");
            }else {
                dbHelper.insertSettings("stock_view","2");
            }
            //calling a method of the adapter class and passing the filtered list
            if (filterdNames.size()>0){
               /* for (ProductsModel model:filterdNames){
                    Log.w("products_Option_values:",model.getProductName());
                }*/
                if (selectProductAdapter!=null){
                    selectProductAdapter.filterList(filterdNames);
                }
                selectProductAdapter.filterList(filterdNames);
                productLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
                totalProducts.setText(filterdNames.size()+" Products");
            }else {
                productLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
                totalProducts.setText(filterdNames.size()+" Products");
            }
        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String convertDate(String strDate) {
        @SuppressLint("SimpleDateFormat")
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat")
        DateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
        String resultDate = "";
        try {
            resultDate=outputFormat.format(inputFormat.parse(strDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultDate;
    }

    public void getDate(TextView dateEditext){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(TransferProductAddActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateEditext.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        currentDate=convertDate(dateEditext.getText().toString());
                        Log.w("CurrentDateView:",currentDate);
                    }
                }, mYear, mMonth, mDay);
        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void setProductResult(String productId){
        try {
            getLowStockSetting();
            for (ProductsModel model:AppUtils.getProductsList()) {
                Log.w("Product_Code:", model.getProductCode());
                if (model.getProductCode().equals(productId.trim())) {
                    if (model.getStockQty() != null && !model.getStockQty().equals("null")) {
                        if (Double.parseDouble(model.getStockQty()) == 0 || Double.parseDouble(model.getStockQty()) < 0) {
                            stockQtyValue.setText(model.getStockQty());
                            stockQtyValue.setTextColor(Color.parseColor("#D24848"));
                        } else if (Double.parseDouble(model.getStockQty()) > 0) {
                            stockQtyValue.setTextColor(Color.parseColor("#2ECC71"));
                            stockQtyValue.setText(model.getStockQty());
                        }
                    }
                    // stockLayout.setVisibility(View.VISIBLE);

                    if (Double.parseDouble(model.getStockQty()) == 0 || Double.parseDouble(model.getStockQty()) < 0) {
                        if (isAllowLowStock){
                            productAutoComplete.clearFocus();
                            cartonPrice.setText(model.getUnitCost());
                            priceText.setText(model.getUnitCost());
                            loosePrice.setText(model.getUnitCost());
                            uomText.setText(model.getUomCode());
                            stockCount.setText(model.getStockQty());
                            pcsPerCarton.setText(model.getPcsPerCarton());

                            qtyValue.setEnabled(true);
                            qtyValue.setText("");

                            qtyValue.requestFocus();
                            openKeyborard(qtyValue);
                            qtyValue.setSelection(qtyValue.getText().length());

                            focEditText.setEnabled(true);
                            exchangeEditext.setEnabled(true);
                            discountEditext.setEnabled(true);
                            returnEditext.setEnabled(true);

                        }else {
                            Toast.makeText(getApplicationContext(), "Low Stock Please check", Toast.LENGTH_SHORT).show();
                            productAutoComplete.clearFocus();
                            productAutoComplete.setText("");
                            qtyValue.setEnabled(false);
                        }
                    } else {
                        productAutoComplete.clearFocus();
                        cartonPrice.setText(model.getUnitCost());
                        priceText.setText(model.getUnitCost());
                        loosePrice.setText(model.getUnitCost());
                        uomText.setText(model.getUomCode());
                        stockCount.setText(model.getStockQty());
                        pcsPerCarton.setText(model.getPcsPerCarton());

                        qtyValue.setEnabled(true);
                        qtyValue.setText("");
                        qtyValue.requestFocus();
                        openKeyborard(qtyValue);
                        qtyValue.setSelection(qtyValue.getText().length());
                        focEditText.setEnabled(true);
                        exchangeEditext.setEnabled(true);
                        discountEditext.setEnabled(true);
                        returnEditext.setEnabled(true);
                    }
                } else {
                    Log.w("Not_found", "Product");
                }
            }
        }catch (Exception ex){
        }

    }

    public void showAlert(){
        new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Warning !")
                .setContentText("Net total should not be zero or Atleast add one Qty")
                .setConfirmText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }

    public void showAlertForDeleteProduct(){
        new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Warning !")
                .setContentText("Products Will be cleared are you sure want to back?")
                .setConfirmText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        dbHelper.removeAllInvoiceItems();
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }

    public void showMinimumSellingpriceAlert(String sellingPrice){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
        builder1.setTitle("Warning !");
        builder1.setMessage(productName+"-"+"Minimum Selling Price is : $ "+Utils.twoDecimalPoint(Double.parseDouble(sellingPrice)));
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void addProduct(String action){
        if (isProductExist(productId)){
            Log.w("ProductIdView:",productId);
            showExistingProductAlert(productId,productName);
        }else {
            if (transferType.equals("Stock Request")){
                insertProducts();
            }else {
                if (checkLowStock()){
                    insertProducts();
                }
            }
        }
    }

    public void showExistingProductAlert(String productId,String productName){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
        builder1.setTitle("Warning !");
        builder1.setMessage(productName+" - "+productId+ "\nAlready Exist Do you want to replace ? ");
        builder1.setCancelable(false);
        builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                if (transferType.equals("Stock Request")){
                    insertProducts();
                }else {
                    if (checkLowStock()){
                        insertProducts();
                    }
                }
            }
        });
        builder1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                clearFields();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void insertProducts(){
        try {

            String return_qty="0";
            String price_value ="0";
            String qty_value ="0";

            if (!qtyValue.getText().toString().isEmpty()){
                qty_value =qtyValue.getText().toString();
            }

            boolean insertStatus=
                    dbHelper.insertCreateInvoiceCart(
                            productId.toString().trim(),
                            uomText.getText().toString(),
                            "",
                            productName,
                            qty_value,
                            return_qty,
                            qty_value,
                            "0",
                            price_value,
                            "0",
                            "0.00",
                            "0.00",
                            "0.00",
                            "0.00","0.00","0.00",
                            "",
                            "",
                            "","","","",""
                    );

            if (insertStatus){
                subTotalValue.setText("0.0");
                taxValueText.setText("0.00");
                netTotalValue.setText("0.0");
                productAutoComplete.setText("");
                priceText.setText("0.00");
                qtyValue.setText("");
                uomText.setText("");
                stockCount.setText("");
                qtyValue.clearFocus();
                addProduct.setText("Add");
                productAutoComplete.clearFocus();
                focEditText.setText("");
                exchangeEditext.setText("");
                discountEditext.setText("");
                returnQtyText.setText("");
                focSwitch.setChecked(false);
                exchangeSwitch.setChecked(false);
                returnSwitch.setChecked(false);
                stockLayout.setVisibility(View.GONE);
                // priceText.setEnabled(false);
                qtyValue.setEnabled(false);
                focEditText.setEnabled(false);
                exchangeEditext.setEnabled(false);
                discountEditext.setEnabled(false);
                hideKeyboard();
                getProducts();
            }else {
                Toast.makeText(getApplicationContext(),"Error in Add product",Toast.LENGTH_LONG).show();
            }
        }catch (Exception ec){  Log.e("Error_InsertProduct:",ec.getMessage());}
    }

    @SuppressLint("SetTextI18n")
    private void getProducts() {
        ArrayList<CreateInvoiceModel> products=dbHelper.getAllInvoiceProducts();
        if (products.size()>0){
            itemCount.setText("Products ( "+products.size()+" )");
            productSummaryView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            productSummaryAdapter=new TransferNewSummaryAdapter(this,products, new TransferNewSummaryAdapter.CallBack() {
                @Override
                public void searchCustomer(String letter, int pos) {
                }
                @Override
                public void removeItem(String pid) {
                    showRemoveItemAlert(pid);
                }
                @Override
                public void editItem(CreateInvoiceModel model) {
                    salesReturn.setChecked(false);
                    salesReturn.setEnabled(false);
                    productId=model.getProductCode();
                    productName=model.getProductName();
                    qtyValue.setText("");

                    double netqty=Double.parseDouble(model.getNetQty());

                  /*  if (model.getMinimumSellingPrice()!=null && !model.getMinimumSellingPrice().isEmpty()){
                        minimumSellingPriceText.setText(model.getMinimumSellingPrice());
                    }else {
                        minimumSellingPriceText.setText("0.00");
                    }*/

                    qtyValue.removeTextChangedListener(qtyTW);
                    qtyValue.setText(Utils.getQtyValue(String.valueOf(netqty)));
                    qtyValue.addTextChangedListener(qtyTW);

                    productAutoComplete.setText(model.getProductName()+"-"+model.getProductCode());

                    priceText.setText(model.getPrice());
                    getStock(model.getProductCode());
                    addProduct.setText("Update");
                    qtyValue.requestFocus();
                    qtyValue.setSelectAllOnFocus(true);
                    qtyValue.setSelection(qtyValue.getText().length());
                    qtyValue.setEnabled(true);

                    if (model.getFocQty()!=null && !model.getFocQty().isEmpty() && !model.getFocQty().equals("null")){
                        focEditText.setText(model.getFocQty());
                    }else {
                        focEditText.setText("0");
                    }

                    if (model.getReturnQty()!=null && !model.getReturnQty().isEmpty() && !model.getReturnQty().equals("null")){
                        returnQtyText.setText(model.getReturnQty());
                    }else {
                        returnQtyText.setText("0");
                    }

                    focEditText.setEnabled(true);
                    returnQtyText.setEnabled(true);

                }
            });
            stockLayout.setVisibility(View.VISIBLE);
            productSummaryView.setAdapter(productSummaryAdapter);
            noproductText.setVisibility(View.GONE);
            productSummaryView.setVisibility(View.VISIBLE);
            itemCount.setVisibility(View.VISIBLE);
        }else {
            itemCount.setVisibility(View.GONE);
            noproductText.setVisibility(View.VISIBLE);
            productSummaryView.setVisibility(View.GONE);
        }
        Utils.refreshActionBarMenu(this);
        hideKeyboard();
        setSummaryTotal();
    }

    public void showRemoveItemAlert(final String pid){

        try {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    // .setTitleText("Are you sure?")
                    .setContentText("Are you sure want to remove this item ?")
                    .setConfirmText("YES")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            dbHelper.deleteInvoiceProduct(pid);
                            clearFields();
                            sDialog.dismissWithAnimation();
                            getProducts();
                            setSummaryTotal();
                            Utils.refreshActionBarMenu(TransferProductAddActivity.this);
                        }
                    })
                    .showCancelButton(true)
                    .setCancelText("No")
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .show();
        }catch (Exception ex){}

    }

    public void hideKeyboard(){
        try {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
        }
    }

    public void setSummaryTotal(){
        try {
            ArrayList<CreateInvoiceModel> localCart;
            localCart = dbHelper.getAllInvoiceProducts();
            double net_sub_total=0.0;
            double net_tax=0.0;
            int net_qty=0;
            double net_total=0.0;
            if (localCart.size()>0){
                for (CreateInvoiceModel model:localCart){
                    if (model.getSubTotal()!=null &&!model.getSubTotal().isEmpty()){
                        net_sub_total+=Double.parseDouble(model.getSubTotal());
                    }
                    if (model.getGstAmount()!=null && !model.getGstAmount().isEmpty()){
                        net_tax+=Double.parseDouble(model.getGstAmount());
                    }
                    if (model.getNetTotal()!=null && !model.getNetTotal().isEmpty()){
                        net_total+=Double.parseDouble(model.getNetTotal());
                    }

                    if (model.getActualQty()!=null && !model.getActualQty().isEmpty()){
                        net_qty+=(int)Double.parseDouble(model.getActualQty());
                    }
                }
            }
            netQtyValue.setText(net_qty+"");
        }catch (Exception ex){}
    }

    public void clearFields(){
        subTotalValue.setText("0.0");
        taxValueText.setText("0.00");
        netTotalValue.setText("0.0");
        productAutoComplete.setText("");
        priceText.setText("0.00");
        qtyValue.setText("");
        pcsPerCarton.setText("");
        uomText.setText("");
        stockCount.setText("");
        qtyValue.clearFocus();
        productAutoComplete.clearFocus();
        focEditText.setText("");
        returnQtyText.setText("");
        stockLayout.setVisibility(View.GONE);
        // priceText.setEnabled(false);
        getProducts();
        setSummaryTotal();
    }

    public boolean isProductExist(String productId) {
        boolean isExist=false;
        try {
            ArrayList<CartModel> localCart = dbHelper.getAllCartItems();
            if (localCart.size() > 0) {
                for (CartModel cart : localCart) {
                    if (cart.getCART_COLUMN_PID()!=null){
                        if (cart.getCART_COLUMN_PID().equals(productId)) {
                            Log.w("ProductIdPrint:",cart.getCART_COLUMN_PID());
                            isExist = true;
                            break;
                        }
                    }
                }
            }
        }catch (Exception ex){
            Log.e("Exp_to_check_product:", Objects.requireNonNull(ex.getMessage()));
        }
        return isExist;
    }

    public boolean checkLowStock(){
        double stock=0.0;
        double qty=0.0;
        isCheckStock=true;
        if (!stockQtyValue.getText().toString().isEmpty()){
            if (!stockQtyValue.getText().toString().isEmpty()){
                stock=Double.parseDouble(stockQtyValue.getText().toString());
            }
            if (!qtyValue.getText().toString().isEmpty()){
                qty=Double.parseDouble(qtyValue.getText().toString());
            }
            if (stock < qty){
                Toast.makeText(getApplicationContext(),"Low Stock please check",Toast.LENGTH_SHORT).show();
                qtyValue.removeTextChangedListener(qtyTW);
                qtyValue.setText("");
                qtyValue.addTextChangedListener(qtyTW);
                isCheckStock=false;
                //setCalculationView();
            }
        }
        return isCheckStock;
    }

    /**
     * Get = the low stock invoice setting to allow the Negative stock allow the invoice
     */
    public void getLowStockSetting(){
        ArrayList<SettingsModel> settings=dbHelper.getSettings();
        if (settings.size()>0) {
            for (SettingsModel model : settings) {
                if (model.getSettingName().equals("allow_negative_switch")) {
                    isAllowLowStock= model.getSettingValue().equals("1");
                }
            }
        }else {
            isAllowLowStock=false;
        }
    }

    private ArrayList<ItemGroupList> getGrouplist() throws JSONException {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(getApplicationContext()) +"ItemGroupList";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url_group:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading Groups...");
        pDialog.setCancelable(false);
        pDialog.show();
        itemGroup =new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("grouplist:",response.toString());

                        pDialog.dismiss();
                        String statusCode=response.optString("statusCode");
                        String statusMessage=response.optString("statusMessage");
                        if (statusCode.equals("1")){
                            JSONArray  groupArray=response.optJSONArray("responseData");

                            for (int i = 0; i < groupArray.length(); i++) {
                                JSONObject jsonObject = groupArray.getJSONObject(i);
                                String groupName = jsonObject.getString("itemGroupName");
                                String groupCode = jsonObject.getString("itemGroupCode");
                                ItemGroupList itemGroupList = new ItemGroupList(groupCode, groupName);
                                itemGroup.add(itemGroupList);
                            }
                            if (itemGroup.size()>0){
                                setupGroup(itemGroup);
                            }
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
        return itemGroup;
    }

    private void setupGroup(ArrayList<ItemGroupList> itemGroupLists) {
        ArrayAdapter<ItemGroupList> myAdapter = new ArrayAdapter<ItemGroupList>(this, android.R.layout.simple_list_item_1, itemGroupLists);
        groupspinner.setAdapter(myAdapter);
        groupspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject jsonObject=new JSONObject();
                try {
                    String itemCode=itemGroup.get(i).getGroupCode();
                    Log.e("selectspinn",""+ itemCode);
                        jsonObject.put("WarehouseCode",fromLocationCode);
                        jsonObject.put("ItemGroupCode",itemCode);
                        getAllProducts(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    public void getStockProductsList(){
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("WarehouseCode",fromLocationCode);
            jsonObject.put("ItemGroupCode","All");
            getStockProductsList(jsonObject);
        }catch (Exception e){}

    }


    public void setCalculationSummaryView(double subTotal) {
        try {
            double taxAmount1 = 0.0, netTotal1 = 0.0;

            SharedPreferences sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
            String selectCustomerId = sharedPreferences.getString("customerId", "");
            if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
                customerDetails = dbHelper.getCustomer(selectCustomerId);
            }
            String taxValue=customerDetails.get(0).getTaxPerc();
            String taxType=customerDetails.get(0).getTaxType();

            Log.w("TaxType-Summary:",taxType);
            Log.w("TaxValue12-Summary:",taxValue);
            Log.w("SubTotalValues:",String.valueOf(subTotal));

            String Prodtotal = twoDecimalPoint(subTotal);

            if (!taxType.matches("") && !taxValue.matches("")) {
                double taxValueCalc = Double.parseDouble(taxValue);

                if (taxType.matches("E")) {

                    taxAmount1 = (subTotal * taxValueCalc) / 100;
                    String prodTax = twoDecimalPoint(taxAmount1);
                    taxValueText.setText("" + prodTax);

                    netTotal1 = subTotal + taxAmount1;
                    String ProdNetTotal = twoDecimalPoint(netTotal1);
                    netTotalValue.setText("" + ProdNetTotal);
                    taxTitle.setText("GST ( Exc )");
                    subTotalValue.setText(Utils.twoDecimalPoint(subTotal));

                } else if (taxType.matches("I")) {

                    taxAmount1 = (subTotal * taxValueCalc) / (100 + taxValueCalc);
                    String prodTax = twoDecimalPoint(taxAmount1);

                    taxValueText.setText("" + prodTax);
                    // netTotal1 = subTotal + taxAmount1;
                    netTotal1 = subTotal;
                    String ProdNetTotal = twoDecimalPoint(netTotal1);
                    netTotalValue.setText("" + ProdNetTotal);

                    double dTotalIncl = netTotal1 - taxAmount1;
                    String totalIncl = twoDecimalPoint(dTotalIncl);
                    Log.d("totalIncl", "" + totalIncl);

                    double sub_total=subTotal-taxAmount1;
                    taxTitle.setText("GST ( Inc )");
                    subTotalValue.setText(Utils.twoDecimalPoint(sub_total));


                } else if (taxType.matches("Z")) {

                    taxValueText.setText("0.0");
                    // netTotal1 = subTotal + taxAmount;
                    netTotal1 = subTotal;
                    String ProdNetTotal = twoDecimalPoint(netTotal1);
                    netTotalValue.setText("" + ProdNetTotal);
                    subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                    taxTitle.setText("GST ( Zero )");

                } else {
                    taxValueText.setText("0.0");
                    netTotalValue.setText("" + Prodtotal);
                    subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                    taxTitle.setText("GST ( Zero )");
                }

            } else if (taxValue.matches("")) {
                taxValueText.setText("0.0");
                netTotalValue.setText("" + Prodtotal);
                subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                taxTitle.setText("GST ( Zero )");
            } else {
                taxValueText.setText("0.0");
                netTotalValue.setText("" + Prodtotal);
                subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                taxTitle.setText("GST ( Zero )");
            }
        } catch (Exception e) {

        }
    }



    public void setCalculationView() {
        try {
            double taxAmount = 0.0, netTotal = 0.0;
            double taxAmount1 = 0.0, netTotal1 = 0.0;
            double return_qty=0;
            double pcspercarton=0.0;
            double cqtyCalc=0;
            double lqtyCalc=0;
            double net_qty=0.0;

            SharedPreferences sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
            String selectCustomerId = sharedPreferences.getString("customerId", "");
            if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
                customerDetails = dbHelper.getCustomer(selectCustomerId);
            }
            String taxValue=customerDetails.get(0).getTaxPerc();
            String taxType=customerDetails.get(0).getTaxType();
            Log.w("TaxType12:",taxType);
            Log.w("TaxValue12:",taxValue);

            String price = priceText.getText().toString();
            String qty = qtyValue.getText().toString();


            if (price.matches("")) {
                price = "0";
            }

            if (qty.matches("")) {
                qty = "0";
            }

            double cPriceCalc = Double.parseDouble(price);

            if (!returnQtyText.getText().toString().isEmpty() && !returnQtyText.getText().toString().equals("null")){
                return_qty=Double.parseDouble(returnQtyText.getText().toString());
                netReturnQty.setText(String.valueOf(return_qty));
            }else {
                netReturnQty.setText("0");
                expiryReturnQty.setText("");
                damageReturnQty.setText("");
            }

            net_qty=(Double.parseDouble(qty) - return_qty);

            double tt = net_qty * cPriceCalc;
            Log.w("TOTALVALUES:",String.valueOf(tt));

            String Prodtotal = twoDecimalPoint(tt);

            double subTotal = 0.0;

            String itemDisc = discountEditext.getText().toString();
            if (!itemDisc.matches("")) {
                double itmDisc = Double.parseDouble(itemDisc);
                subTotal = tt - itmDisc;
            } else {
                subTotal = tt;
            }

            String sbTtl = twoDecimalPoint(subTotal);


          /*  if (return_qty!=0){
                double return_amt=0.0;
                return_amt=(return_qty*cPriceCalc);
                subTotal=subTotal-return_amt;
            }*/

            // sl_total_inclusive.setText("" + sbTtl);
            tt=subTotal;
            Log.w("SubTotalValues:",String.valueOf(subTotal));

            if (!taxType.matches("") && !taxValue.matches("")) {
                double taxValueCalc = Double.parseDouble(taxValue);

                if (taxType.matches("E")) {

                    taxAmount1 = (subTotal * taxValueCalc) / 100;
                    String prodTax = twoDecimalPoint(taxAmount1);
                    taxValueText.setText("" + prodTax);

                    netTotal1 = subTotal + taxAmount1;
                    String ProdNetTotal = twoDecimalPoint(netTotal1);
                    netTotalValue.setText("" + ProdNetTotal);
                    taxTitle.setText("GST ( Exc )");
                    subTotalValue.setText(Utils.twoDecimalPoint(subTotal));

                } else if (taxType.matches("I")) {

                    taxAmount1 = (subTotal * taxValueCalc) / (100 + taxValueCalc);
                    String prodTax = twoDecimalPoint(taxAmount1);

                    taxValueText.setText("" + prodTax);
                    // netTotal1 = subTotal + taxAmount1;
                    netTotal1 = subTotal;
                    String ProdNetTotal = twoDecimalPoint(netTotal1);
                    netTotalValue.setText("" + ProdNetTotal);

                    double dTotalIncl = netTotal1 - taxAmount1;
                    String totalIncl = twoDecimalPoint(dTotalIncl);
                    Log.d("totalIncl", "" + totalIncl);

                    double sub_total=subTotal-taxAmount1;
                    taxTitle.setText("GST ( Inc )");
                    subTotalValue.setText(Utils.twoDecimalPoint(sub_total));


                } else if (taxType.matches("Z")) {

                    taxValueText.setText("0.0");
                    // netTotal1 = subTotal + taxAmount;
                    netTotal1 = subTotal;
                    String ProdNetTotal = twoDecimalPoint(netTotal1);
                    netTotalValue.setText("" + ProdNetTotal);
                    subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                    taxTitle.setText("GST ( Zero )");

                } else {
                    taxValueText.setText("0.0");
                    netTotalValue.setText("" + Prodtotal);
                    subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                    taxTitle.setText("GST ( Zero )");
                }

            } else if (taxValue.matches("")) {
                taxValueText.setText("0.0");
                netTotalValue.setText("" + Prodtotal);
                subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                taxTitle.setText("GST ( Zero )");
            } else {
                taxValueText.setText("0.0");
                netTotalValue.setText("" + Prodtotal);
                subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                taxTitle.setText("GST ( Zero )");
            }
            setButtonView();
        } catch (Exception e) {
            Log.w("Error_Throwing::",e.getMessage());
        }
    }


    public void setButtonView(){
        if (qtyValue.getText()!=null && !qtyValue.getText().toString().isEmpty() && Double.parseDouble(qtyValue.getText().toString()) > 0){
            addProduct.setAlpha(0.9F);
            addProduct.setEnabled(true);
        }else {
            addProduct.setAlpha(0.4F);
            addProduct.setEnabled(false);
        }
    }

    public void getAllProducts(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"ProductList";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_SAP_PROUCT_URL:",url+"-"+jsonObject.toString());
        productList=new ArrayList<>();
        products=new ArrayList<>();
        progressDialog =new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Getting Customer Products List...");
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        progressDialog.dismiss();
                        //  {"productCode":"FG\/001245","productName":"RUM","companyName":"","supplierCategoryNumber":"","uomCode":"Ctn",
                        //  "uomName":"Carton","cartonPrice":"3000.000000","piecePrice":"0.000000","pcsPerCarton":"100.000000",
                        //  "price":"100.000000","taxType":"E","havTax":"Y","taxCode":"SR","taxRate":"7.000000","barCode":"",
                        //  "isActive":"N","createUser":"1","createDate":"13\/07\/2021","modifyDate":"29\/07\/2021","remarks":"",
                        //  "warehouse":"01","stockInHand":"0.000000","averagePrice":0,"manageBatchOrSerial":"None","manageBatchNumber":"N",
                        //  "manageSerialNumber":"N","batchNumber":"","expiryDate":null,"manufactureDate":"31\/07\/2021","imageURL":""}
                        Log.w("Response_SAP_PRODUCTS:",response.toString());
                        // Loop through the array elements
                        JSONArray productArray=response.optJSONArray("responseData");
                        for(int i=0;i<productArray.length();i++) {
                            // Get current json object
                            JSONObject productObject = productArray.getJSONObject(i);
                            ProductsModel product = new ProductsModel();
                            if (productObject.optString("isActive").equals("N")) {
                                if (!productObject.optString("stockInHand").equals("null") && Double.parseDouble(productObject.optString("stockInHand")) > 0){
                                    product.setCompanyCode("1");
                                product.setProductName(productObject.optString("productName"));
                                product.setProductCode(productObject.optString("productCode"));
                                product.setWeight("");
                                product.setProductImage(productObject.optString("imageURL"));
                                product.setWholeSalePrice("0.00");
                                product.setRetailPrice(productObject.optDouble("retailPrice"));
                                product.setCartonPrice(productObject.optString("cartonPrice"));
                                product.setPcsPerCarton(productObject.optString("pcsPerCarton"));
                                product.setUnitCost(productObject.optString("price"));
                                product.setMinimumSellingPrice(productObject.optString("minimumSellingPrice"));
                                if (!productObject.optString("stockInHand").equals("null")) {
                                    product.setStockQty(productObject.optString("stockInHand"));
                                } else {
                                    product.setStockQty("0");
                                }

                                product.setUomCode(productObject.optString("uomCode"));
                                //  product.setProductBarcode(productObject.optString("BarCode")); Add values In Futue
                                product.setProductBarcode("");
                                productList.add(product);
                            }
                          }
                        }
                        HomePageModel.productsList=new ArrayList<>();
                        HomePageModel.productsList.addAll(productList);
                        setAdapter(productList);
                        if (productList.size()>0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtils.setProductsList(productList);
                                    progressDialog.dismiss();
                                    // dbHelper.insertProducts(getActivity(),productList);
                                }
                            });
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
        }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", Constants.API_SECRET_CODE,Constants.API_SECRET_PASSWORD);
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

    private void getStock(String pid){
        for (ProductsModel model:stockProductList){
            if (model.getProductCode().equals(pid)){
                stockCount.setText(model.getStockQty());
                stockQtyValue.setText(model.getStockQty());
                Log.w("StockQtyValue:",model.getStockQty());
                break;
            }
        }
    }

    public void getStockProductsList(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"ProductList";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_SAP_PROUCT_URL:",url+"-"+jsonObject.toString());
        stockProductList=new ArrayList<>();
        progressDialog =new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading Products Please wait...");
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        progressDialog.dismiss();
                        //  {"productCode":"FG\/001245","productName":"RUM","companyName":"","supplierCategoryNumber":"","uomCode":"Ctn",
                        //  "uomName":"Carton","cartonPrice":"3000.000000","piecePrice":"0.000000","pcsPerCarton":"100.000000",
                        //  "price":"100.000000","taxType":"E","havTax":"Y","taxCode":"SR","taxRate":"7.000000","barCode":"",
                        //  "isActive":"N","createUser":"1","createDate":"13\/07\/2021","modifyDate":"29\/07\/2021","remarks":"",
                        //  "warehouse":"01","stockInHand":"0.000000","averagePrice":0,"manageBatchOrSerial":"None","manageBatchNumber":"N",
                        //  "manageSerialNumber":"N","batchNumber":"","expiryDate":null,"manufactureDate":"31\/07\/2021","imageURL":""}
                        Log.w("Response_SAP_PRODUCTS:",response.toString());
                        // Loop through the array elements
                        JSONArray productArray=response.optJSONArray("responseData");
                        for(int i=0;i<productArray.length();i++) {
                            // Get current json object
                            JSONObject productObject = productArray.getJSONObject(i);
                            ProductsModel product = new ProductsModel();
                            if (productObject.optString("isActive").equals("N")) {
                                if (!productObject.optString("stockInHand").equals("null") && Double.parseDouble(productObject.optString("stockInHand")) > 0){
                                    product.setCompanyCode("1");
                                    product.setProductName(productObject.optString("productName"));
                                    product.setProductCode(productObject.optString("productCode"));
                                    product.setWeight("");
                                    product.setProductImage(productObject.optString("imageURL"));
                                    product.setWholeSalePrice("0.00");
                                    product.setRetailPrice(productObject.optDouble("retailPrice"));
                                    product.setCartonPrice(productObject.optString("cartonPrice"));
                                    product.setPcsPerCarton(productObject.optString("pcsPerCarton"));
                                    product.setUnitCost(productObject.optString("price"));
                                    product.setMinimumSellingPrice(productObject.optString("minimumSellingPrice"));
                                    if (!productObject.optString("stockInHand").equals("null")) {
                                        product.setStockQty(productObject.optString("stockInHand"));
                                    } else {
                                        product.setStockQty("0");
                                    }

                                    product.setUomCode(productObject.optString("uomCode"));
                                    //  product.setProductBarcode(productObject.optString("BarCode")); Add values In Futue
                                    product.setProductBarcode("");
                                    stockProductList.add(product);
                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
        }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", Constants.API_SECRET_CODE,Constants.API_SECRET_PASSWORD);
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

    @SuppressLint("SetTextI18n")
    private void setAdapter(ArrayList<ProductsModel> productList) {
        // Get the Settings to Display the Product list with the Settings
        ArrayList<SettingsModel> settings=dbHelper.getSettings();
        if (settings.size()>0) {
            for (SettingsModel model : settings) {
                if (model.getSettingName().equals("stock_view")) {
                    if (model.getSettingValue().equals("1")){
                        stockProductView="1";
                    }else if (model.getSettingValue().equals("2")){
                        stockProductView="2";
                    }else {
                        stockProductView="0";
                    }
                }
            }
        }
        // Filter the products list depending the Settings
        ArrayList<ProductsModel> filteredProducts = new ArrayList<>();
        //   for (ProductsModel s : selectProductAdapter.getProductsList()) {
        if (productList!=null && productList.size()>0){
            for (ProductsModel product : productList) {
                if (stockProductView.equals("1")){
                    if (product.getStockQty()!=null && !product.getStockQty().equals("null")){
                        if (Double.parseDouble(product.getStockQty())>0){
                            filteredProducts.add(product);
                        }
                    }
                }else if (stockProductView.equals("0")){
                    if (product.getStockQty()!=null && !product.getStockQty().equals("null")) {
                        if (Double.parseDouble(product.getStockQty()) < 0 || Double.parseDouble(product.getStockQty()) == 0) {
                            filteredProducts.add(product);
                        }
                    }
                }else {
                    filteredProducts.add(product);
                }
            }
        }

        if (filteredProducts.size()>0){
            productLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }else {
            productLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }

        productListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        selectProductAdapter=new SelectProductAdapter(this,filteredProducts, new SelectProductAdapter.CallBack() {
            @Override
            public void searchProduct(ProductsModel model) {
                // Need to implement the Product price Later
               /* try {
                    getProductPrice(model.getProductCode());
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                productAutoComplete.setText(model.getProductName()+" - "+model.getProductCode());
                productId=model.getProductCode();
                productName=model.getProductName();
                priceText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getUnitCost())));
                uomText.setText(model.getUomCode());
                stockCount.setText(model.getStockQty());
                // looseQtyValue.setEnabled(true);
                qtyValue.setText("");
                focEditText.setEnabled(true);
                exchangeEditext.setEnabled(true);
                discountEditext.setEnabled(true);
                returnQtyText.setEnabled(true);

                if (model.getMinimumSellingPrice()!=null && !model.getMinimumSellingPrice().isEmpty()){
                    minimumSellingPriceText.setText(model.getMinimumSellingPrice());
                }else {
                    minimumSellingPriceText.setText("0.00");
                }

                 stockLayout.setVisibility(View.VISIBLE);
                if (model.getStockQty()!=null && !model.getStockQty().equals("null")) {
                    if (Double.parseDouble(model.getStockQty()) == 0 || Double.parseDouble(model.getStockQty()) < 0) {
                        stockQtyValue.setText(model.getStockQty());
                        stockQtyValue.setTextColor(Color.parseColor("#D24848"));
                    } else if (Double.parseDouble(model.getStockQty()) > 0) {
                        stockQtyValue.setTextColor(Color.parseColor("#2ECC71"));
                        stockQtyValue.setText(model.getStockQty());
                    }
                }
                qtyValue.requestFocus();
                openKeyborard(qtyValue);
                viewCloseBottomSheet();
            }
        });

        // products.add(productObject.optString("ProductName")+" - "+productObject.optString("ProductCode"));
        products=new ArrayList<>();
        if (filteredProducts!=null && filteredProducts.size()>0){
            for (ProductsModel product:filteredProducts){
                products.add(product.getProductName()+" ~ "+product.getProductCode());
            }
        }

        productListView.setAdapter(selectProductAdapter);
        autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, products){
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(14);
            /*    Typeface Type = getFont () ;  // custom method to get a font from "assets" folder
                ((TextView) v).setTypeface(Type);
                ((TextView) v).setTextColor(YourColor);*/
                ((TextView) v) .setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
                return v;
            }
        };
        productAutoComplete.setThreshold(1);
        productAutoComplete.setAdapter(autoCompleteAdapter);
        totalProducts.setText(filteredProducts.size()+" Products");
        //dialog.dismiss();
    }


    public void viewCloseBottomSheet(){
        // hideKeyboard();
        productNameEditext.setText("");
        selectProductAdapter.notifyDataSetChanged();
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        // Showing the Products Depending the Settings Values
        ArrayList<SettingsModel> settings=dbHelper.getSettings();
        if (settings.size()>0) {
            for (SettingsModel model : settings) {
                if (model.getSettingName().equals("stock_view")) {
                    if (model.getSettingValue().equals("1")){
                        stockProductView="1";
                    }else if (model.getSettingValue().equals("2")){
                        stockProductView="2";
                    }else {
                        stockProductView="0";
                    }
                }
            }
        }
        // Filter the products list depending the Settings
        ArrayList<ProductsModel> filteredProducts = new ArrayList<>();
        //   for (ProductsModel s : selectProductAdapter.getProductsList()) {
        if (AppUtils.getProductsList()!=null && AppUtils.getProductsList().size()>0){
            for (ProductsModel product : AppUtils.getProductsList()) {
                if (stockProductView.equals("1")){
                    if (product.getStockQty()!=null && !product.getStockQty().equals("null")){
                        if (Double.parseDouble(product.getStockQty())>0){
                            filteredProducts.add(product);
                        }
                    }
                }else if (stockProductView.equals("0")){
                    if (product.getStockQty()!=null && !product.getStockQty().equals("null")) {
                        if (Double.parseDouble(product.getStockQty()) < 0 || Double.parseDouble(product.getStockQty()) == 0) {
                            filteredProducts.add(product);
                        }
                    }
                }else {
                    filteredProducts.add(product);
                }
            }
        }


        if (filteredProducts.size()>0){
            productLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }else {
            productLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }

        productListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        selectProductAdapter=new SelectProductAdapter(this,filteredProducts, new SelectProductAdapter.CallBack() {
            @Override
            public void searchProduct(ProductsModel model) {
                productsModel=model;
                productId=productsModel.getProductCode();

                // Need to implement the product price concept in SAP
              /*  try {
                    getProductPrice(productId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

                if (model.getMinimumSellingPrice()!=null && !model.getMinimumSellingPrice().isEmpty()){
                    minimumSellingPriceText.setText(model.getMinimumSellingPrice());
                }else {
                    minimumSellingPriceText.setText("0.00");
                }
                productName=productsModel.getProductName();
                productAutoComplete.setText(model.getProductName()+" - "+model.getProductCode());
                //  cartonPrice.setText(model.getUnitCost()+"");
                //  loosePrice.setText(model.getUnitCost());
                priceText.setText(model.getUnitCost());
                uomText.setText(model.getUomCode());
                stockCount.setText(model.getStockQty());
                pcsPerCarton.setText(model.getPcsPerCarton());
                qtyValue.setEnabled(true);
                qtyValue.requestFocus();
                //  behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                openKeyborard(qtyValue);

                // looseQtyValue.setEnabled(true);
                cartonPrice.setEnabled(true);
                focEditText.setEnabled(true);
                exchangeEditext.setEnabled(true);
                discountEditext.setEnabled(true);
                returnEditext.setEnabled(true);

                //stockLayout.setVisibility(View.VISIBLE);
                if (model.getStockQty()!=null && !model.getStockQty().equals("null")) {
                    if (Double.parseDouble(model.getStockQty()) == 0 || Double.parseDouble(model.getStockQty()) < 0) {
                        stockQtyValue.setText(model.getStockQty());
                        stockQtyValue.setTextColor(Color.parseColor("#D24848"));
                    } else if (Double.parseDouble(model.getStockQty()) > 0) {
                        stockQtyValue.setTextColor(Color.parseColor("#2ECC71"));
                        stockQtyValue.setText(model.getStockQty());
                    }
                }
                viewCloseBottomSheet();
            }
        });
        productListView.setAdapter(selectProductAdapter);
        totalProducts.setText(filteredProducts.size()+" Products");
        // get the Customer name from the local db
    }

    public void openKeyborard(EditText editText){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void filter(String text) {
        try {
            //new array list that will hold the filtered data
            ArrayList<ProductsModel> filterProducts = new ArrayList<>();
            //looping through existing elements
            //   for (ProductsModel s : selectProductAdapter.getProductsList()) {
            for (ProductsModel s : AppUtils.getProductsList()) {
                //if the existing elements contains the search input
                if (s.getProductName().toLowerCase().contains(text.toLowerCase()) || s.getProductCode().toLowerCase().contains(text.toLowerCase())) {
                    //adding the element to filtered list
                    filterProducts.add(s);
                }
            }
            //calling a method of the adapter class and passing the filtered list
            selectProductAdapter.filterList(filterProducts);

            //setAdapter(filterProducts);
            totalProducts.setText(filterProducts.size()+" Products");

        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
             /*   Intent intent = new Intent(CreateNewInvoiceActivity.this, CustomerListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                finish();
                return true;

            case R.id.action_save:
                showSaveAlert(transferType);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //  return super.onOptionsItemSelected(item);
    }

    public void showSaveAlert(String mode){
        try {
            // create an alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // set the custom layout
            builder.setCancelable(false);
            final View customLayout = getLayoutInflater().inflate(R.layout.invoice_save_option, null);
            builder.setView(customLayout);
            // add a button

            okButton=customLayout.findViewById(R.id.btn_ok);
            cancelButton=customLayout.findViewById(R.id.btn_cancel);
            invoicePrintCheck=customLayout.findViewById(R.id.invoice_print_check);
            saveMessage=customLayout.findViewById(R.id.save_message);
            saveTitle=customLayout.findViewById(R.id.save_title);
            //invoicePrintCheck.setVisibility(View.GONE);
            if (mode.equals("Transfer In") || mode.equals("Transfer Out") || mode.equals("Convert Transfer")){
                saveTitle.setText("Save Transfer");
                saveMessage.setText("Are you sure want to save Transfer?");
                invoicePrintCheck.setText("Transfer Print");
            }else {
                saveTitle.setText("Save Stock Request");
                saveMessage.setText("Are you sure want to save Stock Request?");
                invoicePrintCheck.setText("Stock Request Print");
            }
            invoicePrintCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (invoicePrintCheck.isChecked()){
                        isPrintEnable=true;
                    }else {
                        isPrintEnable=false;
                    }
                }
            });
            okButton.setOnClickListener(view1 -> {
                try {
                    alert.dismiss();
                    if (mode.equals("Transfer In") || mode.equals("Transfer Out") || mode.equals("Convert Transfer")) {
                        createTransferRequest();
                    }else {
                        createStockRequestJson();
                    }
                }catch (Exception exception){}
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                }
            });
            // create and show the alert dialog
            alert = builder.create();
            alert.show();
        }catch (Exception exception){}
    }

    public void createStockRequestJson() throws JSONException {

        JSONObject rootJson=new JSONObject();
        JSONObject itemsObject =new JSONObject();
        JSONArray itemsArray=new JSONArray();

        rootJson.put("invTransReqNo","");
        rootJson.put("invTransReqStatus","");
        rootJson.put("customerCode","");
        rootJson.put("customerName","");
        rootJson.put("fromWhsCode",fromLocationCode);
        rootJson.put("toWhsCode",toLocationCode);
        rootJson.put("docDate",currentDate);
        rootJson.put("docDueDate",currentDate);
        rootJson.put("transferType","");
        rootJson.put("user",username);

        // Sales Details Add to the Objects
        ArrayList<CreateInvoiceModel> localCart=dbHelper.getAllInvoiceProducts();
        int index=1;
        for (CreateInvoiceModel model:localCart){
            itemsObject =new JSONObject();
            itemsObject.put("itemCode",model.getProductCode());
            itemsObject.put("itemName",model.getProductName());
            itemsObject.put("qty",String.valueOf(model.getActualQty()));
            itemsObject.put("fromWhsCode",fromLocationCode);
            itemsObject.put("toWhsCode",toLocationCode);
            itemsObject.put("UomCode","PCS");
            itemsObject.put("docEntry","");
            itemsObject.put("objectType","");
            itemsObject.put("lineNum","");
            itemsObject.put("batchNum","");
            itemsObject.put("serialNum","");
            itemsArray.put(itemsObject);
            index++;
        }
        rootJson.put("itItem", itemsArray);

        Log.w("GivenStockRequest:",rootJson.toString());

        saveTransferOrRequest(rootJson,1,transferType);

    }

    public void createTransferRequest() throws JSONException {

        JSONObject rootJson=new JSONObject();
        JSONObject itemsObject =new JSONObject();
        JSONArray itemsArray=new JSONArray();


        if (transferType.equals("Convert Transfer")){
            rootJson.put("invTransReqNo",stockRequestNo);
        }else {
            rootJson.put("invTransReqNo","");
        }

        rootJson.put("invTransNo","");
        rootJson.put("invTransStatus","");
        rootJson.put("customerCode","");
        rootJson.put("customerName","");
        rootJson.put("fromWhsCode",fromLocationCode);
        rootJson.put("toWhsCode",toLocationCode);
        rootJson.put("docDate",currentDate);
        rootJson.put("docDueDate",currentDate);
        rootJson.put("transferType","");
        rootJson.put("user",username);

        // Sales Details Add to the Objects
        ArrayList<CreateInvoiceModel> localCart=dbHelper.getAllInvoiceProducts();
        int index=1;
        for (CreateInvoiceModel model:localCart){
            itemsObject =new JSONObject();
            itemsObject.put("itemCode",model.getProductCode());
            itemsObject.put("itemName",model.getProductName());
            itemsObject.put("qty",String.valueOf(model.getActualQty()));
            itemsObject.put("fromWhsCode",fromLocationCode);
            itemsObject.put("toWhsCode",toLocationCode);
            itemsObject.put("UomCode","PCS");
            itemsObject.put("docEntry","");
            itemsObject.put("objectType","");
            itemsArray.put(itemsObject);
            index++;
        }
        rootJson.put("itItem", itemsArray);

        Log.w("GivenTransferRequest:",rootJson.toString());

       // saveTransferOrRequest(rootJson,1,transferType);

    }


    public  void saveTransferOrRequest(JSONObject jsonBody, int copy,String transferType){
        try {
            pDialog = new SweetAlertDialog(TransferProductAddActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setCancelable(false);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            Log.w("GivenInvoiceRequest:",jsonBody.toString());
            String URL="";
            if (transferType.equals("Transfer In") || transferType.equals("Transfer Out") || transferType.equals("Convert Transfer")){
                URL=Utils.getBaseUrl(this)+"PostingInventoryTransfer";
                Log.w("Given_TransferApi:",URL);
                pDialog.setTitleText("Saving Transfer...");
            }else {
                URL=Utils.getBaseUrl(this)+"PostingInventoryTransferRequest";
                Log.w("Given_StockRequestApi:",URL);
                pDialog.setTitleText("Saving Stock Request...");

            }
            pDialog.show();

            JsonObjectRequest salesOrderRequest = new JsonObjectRequest(Request.Method.POST, URL,jsonBody, response -> {
                Log.w("Transfer_ResponseSap:",response.toString());
                pDialog.dismiss();
                String statusCode=response.optString("statusCode");
                String message=response.optString("statusMessage");
                JSONObject responseData = null;
                responseData=response.optJSONObject("responseData");
                if (statusCode.equals("1")){
                    if (transferType.equals("Transfer In") || transferType.equals("Transfer Out") || transferType.equals("Covert Transfer")){
                        assert responseData != null;
                        String docNum=responseData.optString("docNum");
                        Toast.makeText(getApplicationContext(),"Transfer Saved Success...!",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(),TransferListProductActivity.class);
                        if (isPrintEnable) {
                            intent.putExtra("docNum",docNum);
                            intent.putExtra("transferType",transferType);
                        }
                        startActivity(intent);
                        finish();
                    }else {
                        assert responseData != null;
                        String docNum=responseData.optString("docNum");
                        Toast.makeText(getApplicationContext(),"Transfer Saved Success...!",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(),StockRequestListActivity.class);
                        if (isPrintEnable) {
                            intent.putExtra("docNum",docNum);
                            intent.putExtra("transferType",transferType);
                        }
                        startActivity(intent);
                        finish();
                    }
                }else {
                   /* Intent intent=new Intent(getApplicationContext(),TransferListProductActivity.class);
                    if (isPrintEnable) {
                        intent.putExtra("docNum","22010004");
                        intent.putExtra("transferType",transferType);
                    }
                    startActivity(intent);
                    finish();*/
                    if (responseData!=null){
                        Toast.makeText(getApplicationContext(),responseData.optString("error"),Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(),"Error in Saving Data...",Toast.LENGTH_SHORT).show();
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

    public  void getCustomerDetails(String customerCode,boolean isloader,String from) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("CustomerCode",customerCode);
            // jsonObject.put("CompanyCode",companyCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("JsonValueForCustomer:",jsonObject.toString());
        String url= Utils.getBaseUrl(getApplicationContext()) +"Customer";
        Log.w("Given_url:",url);
        ProgressDialog progressDialog=new ProgressDialog(getApplicationContext());
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
                            customerResponse=response;
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
                        }else {
                            Toast.makeText(getApplicationContext(),"Error,in getting Customer list",Toast.LENGTH_LONG).show();
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

    public void redirectActivity(){
        Intent intent;
        if (activityFrom.equals("iv") || activityFrom.equals("ConvertInvoice")){
            intent = new Intent(TransferProductAddActivity.this, NewInvoiceListActivity.class);
        }else {
            intent = new Intent(TransferProductAddActivity.this, SalesOrderListActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            int count=dbHelper.numberOfRowsInInvoice();
            if (count>0){
                showDeleteAlert();
            }else {
                finish();
            }
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_HOME){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    public void showDeleteAlert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(TransferProductAddActivity.this);
        builder1.setMessage("Data Will be Cleared are you sure want to back?");
        builder1.setCancelable(false);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbHelper.removeAllInvoiceItems();
                        dbHelper.removeAllReturn();
                        finish();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void setNewPrint(){
        PrinterUtils printerUtils=new PrinterUtils(this,printerMacId);
        printerUtils.printLabel();
    }
}