package com.winapp.svcatering.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.AddInvoiceActivityOld;
import com.winapp.svcatering.adapter.ProductSummaryAdapter;
import com.winapp.svcatering.adapter.SelectProductAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.AppUtils;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.HomePageModel;
import com.winapp.svcatering.model.ProductSummaryModel;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.utils.BarCodeScanner;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;
import static com.winapp.svcatering.activity.AddInvoiceActivityOld.activityFrom;
import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

public class ProductFragment extends Fragment {

    LinearLayout returnLayout;
    ImageView showHideButton;
    private ProductSummaryAdapter productSummaryAdapter;
    private ArrayList<ProductSummaryModel> productSummaryList;
    RecyclerView productSummaryView;
    String companyCode;
    HashMap<String ,String> user;
    SessionManager session;
    SweetAlertDialog pDialog;
    public static ArrayList<ProductsModel> productList;
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
    EditText cartonQtyValue;
    EditText looseQtyValue;
    EditText qtyValue;
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
    static ProgressDialog dialog;
    public static String stockProductView="2";

    boolean isReverseCalculationEnabled=true;
    boolean isCartonPriceEdit=true;
    boolean isPriceEdit=true;
    public CheckBox salesReturn;
    public TextView salesReturnText;
    public TextView minimumSellingPriceText;
    public static EditText barcodeText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_product, container, false);
        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        dbHelper=new DBHelper(getActivity());
        dialog=new ProgressDialog(getActivity());
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        returnLayout=view.findViewById(R.id.return_layout);
        showHideButton=view.findViewById(R.id.show_hide);
        productSummaryView=view.findViewById(R.id.product_summary);
        productListView=view.findViewById(R.id.productList);
        btnCancel=view.findViewById(R.id.cancel_sheet);
        searchProduct=view.findViewById(R.id.search_product);
        productNameEditext=view.findViewById(R.id.product_search);
        transLayout=view.findViewById(R.id.trans_layout);
        totalProducts=view.findViewById(R.id.total_products);
        productAutoComplete=view.findViewById(R.id.product_name);
        cartonPrice=view.findViewById(R.id.carton_price);
        loosePrice=view.findViewById(R.id.loose_price);
        pcsPerCarton=view.findViewById(R.id.pcs_per_ctn);
        uomText=view.findViewById(R.id.uom);
        stockCount=view.findViewById(R.id.stock_count);
        cartonQtyValue=view.findViewById(R.id.carton_qty);
        looseQtyValue=view.findViewById(R.id.loose_qty);
        qtyValue=view.findViewById(R.id.qty);
        subTotalValue=view.findViewById(R.id.balance_value);
        taxValueText=view.findViewById(R.id.tax);
        netTotalValue=view.findViewById(R.id.net_total);
        taxTitle=view.findViewById(R.id.tax_title);
        addProduct=view.findViewById(R.id.add_product);
        itemCount=view.findViewById(R.id.item_count);
        noproductText=view.findViewById(R.id.no_product_text);
        focSwitch=view.findViewById(R.id.foc_switch);
        exchangeSwitch=view.findViewById(R.id.exchange_switch);
        returnSwitch=view.findViewById(R.id.return_switch);
        focEditText=view.findViewById(R.id.foc_text);
        exchangeEditext=view.findViewById(R.id.exchange_text);
        returnEditext=view.findViewById(R.id.return_text);
        discountEditext=view.findViewById(R.id.discount_text);
        scanProduct=view.findViewById(R.id.scan_product);
        stockQtyValue=view.findViewById(R.id.stock_qty);
        stockLayout=view.findViewById(R.id.stock_layout);
        focLayout=view.findViewById(R.id.foc_layout);
        emptyLayout=view.findViewById(R.id.empty_layout);
        sortButton=view.findViewById(R.id.fab);
        productLayout=view.findViewById(R.id.product_layout);
        salesReturn=view.findViewById(R.id.sales_return);
        salesReturnText=view.findViewById(R.id.sales_return_text);
        minimumSellingPriceText=view.findViewById(R.id.minimum_selling_price);
        barcodeText=view.findViewById(R.id.barcode_text);
        products=new ArrayList<>();
        productAutoComplete.clearFocus();
        barcodeText.requestFocus();

        focEditText.setEnabled(false);
        exchangeEditext.setEnabled(false);
        discountEditext.setEnabled(false);
        returnEditext.setEnabled(false);


        if (activityFrom.equals("InvoiceEdit")
                || activityFrom.equals("Invoice")
        ){
            salesReturnText.setVisibility(View.VISIBLE);
            salesReturn.setVisibility(View.VISIBLE);
        }else {
            salesReturnText.setVisibility(View.GONE);
            salesReturn.setVisibility(View.GONE);
        }


        getProducts();

        // productList= dbHelper.getAllProducts();
        productList=new ArrayList<>();
        productList= AppUtils.getProductsList();
        if (productList!=null && productList.size()>0){
            setAdapter(productList);
        }else {
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("CompanyCode",companyCode);
                jsonObject.put("LocationCode",locationCode);
                //jsonObject.put("CategoryCode",catagoriesId);
                jsonObject.put("PageSize",5000);
                jsonObject.put("PageNo","1");
                getAllProducts(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        ArrayList<SettingsModel> settings=dbHelper.getSettings();
        if (settings!=null) {
            if (settings.size() > 0) {
                for (SettingsModel model : settings) {
                    if (model.getSettingName().equals("IsPriceSettingReverseCalc")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            isReverseCalculationEnabled=true;
                        }else {
                            isReverseCalculationEnabled=false;
                        }
                    }
                }
            }else {
                isReverseCalculationEnabled=false;
            }
        }else {
            isReverseCalculationEnabled=false;
        }

        showHideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showHideButton.getTag().equals("hide")){
                    returnLayout.setVisibility(View.VISIBLE);
                    showHideButton.setTag("show");
                    slideUp(returnLayout);
                    showHideButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                }else {
                    slideDown(returnLayout);
                    returnLayout.setVisibility(View.GONE);
                    showHideButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    showHideButton.setTag("hide");

                }
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
        selectCustomerId = sharedPreferences.getString("customerId", "");
        if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
            customerDetails = dbHelper.getCustomer(selectCustomerId);
        }

        View bottomSheet = view.findViewById(R.id.design_bottom_sheet);
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
                        AddInvoiceActivityOld.disableTab(false);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        transLayout.setVisibility(View.GONE);
                        AddInvoiceActivityOld.disableTab(true);
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


       /* cartonQtyValue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                cartonQtyValue.addTextChangedListener(cartonTextWatcher);
                qtyValue.removeTextChangedListener(qtyTextWatcher);
                looseQtyValue.removeTextChangedListener(lqtyTextWatcher);
                return false;
            }
        });

        qtyValue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                cartonQtyValue.removeTextChangedListener(cartonTextWatcher);
                looseQtyValue.removeTextChangedListener(lqtyTextWatcher);
                qtyValue.addTextChangedListener(qtyTextWatcher);
                return false;
            }
        });

        looseQtyValue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                cartonQtyValue.removeTextChangedListener(cartonTextWatcher);
                looseQtyValue.addTextChangedListener(lqtyTextWatcher);
                qtyValue.removeTextChangedListener(qtyTextWatcher);
                return false;
            }
        });

        cartonPrice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                cartonPrice.addTextChangedListener(cartonPriceTextWatcher);
                return false;
            }
        });*/



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
                String product =editable.toString();
                if (!product.isEmpty()){
                    filter(product);
                }else {
                    setAdapter(AppUtils.getProductsList());
                }
            }
        });

        returnEditext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //setCalculation();
                setCalculationView();
                //setButtonView();
            }
        });

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
                setCalculationView();
                //setButtonView();
            }
        });


        discountEditext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setCalculationView();
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
                // Sathish 21/07/2020
                if (isReverseCalculationEnabled){
                    if (isPriceEdit){
                        if (loosePrice.getText().toString().equals("")){
                            cartonPrice.removeTextChangedListener(cartonPriceTextWatcher);
                            cartonPrice.setText("");
                            cartonPrice.addTextChangedListener(cartonPriceTextWatcher);
                        }
                        if (!pcsPerCarton.getText().toString().equals("")){
                            double pcspercarton=Double.parseDouble(pcsPerCarton.getText().toString());
                            double unit_price=0;
                            if (loosePrice.getText().toString().equals("")){
                                unit_price=0;
                            }else {
                                unit_price=Double.parseDouble(loosePrice.getText().toString());
                            }
                            if (pcspercarton>1){
                                double cart_price=(pcspercarton * unit_price);
                                cartonPrice.removeTextChangedListener(cartonPriceTextWatcher);
                                cartonPrice.setText(twoDecimalPoint(cart_price));
                                cartonPrice.addTextChangedListener(cartonPriceTextWatcher);
                            }else {
                                if (!loosePrice.getText().toString().isEmpty()){
                                    cartonPrice.removeTextChangedListener(cartonPriceTextWatcher);
                                    cartonPrice.setText(twoDecimalPoint(Double.parseDouble(loosePrice.getText().toString())));
                                    cartonPrice.addTextChangedListener(cartonPriceTextWatcher);
                                }else {
                                    cartonPrice.removeTextChangedListener(cartonPriceTextWatcher);
                                    cartonPrice.setText("0.00");
                                    cartonPrice.addTextChangedListener(cartonPriceTextWatcher);
                                }
                            }
                        }
                    }
                    setCalculationView();
                }else {
                    setCalculationView();
                }
            }
        };
        loosePrice.addTextChangedListener(loosePriceTextWatcher);


        cartonPriceTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // setCalculation();
                //setButtonView()

                if (isReverseCalculationEnabled){
                    if (isCartonPriceEdit){
                        if (cartonPrice.getText().toString().equals("")){
                            loosePrice.removeTextChangedListener(loosePriceTextWatcher);
                            loosePrice.setText("");
                            loosePrice.addTextChangedListener(loosePriceTextWatcher);
                        }
                        if (!pcsPerCarton.getText().toString().equals("")){
                            double pcspercarton=Double.parseDouble(pcsPerCarton.getText().toString());
                            double carton_price=0;
                            if (cartonPrice.getText().toString().equals("")){
                                carton_price=0;
                            }else {
                                carton_price=Double.parseDouble(cartonPrice.getText().toString());
                            }
                            if (pcspercarton>1){
                                double unit_price=(carton_price / pcspercarton);
                                loosePrice.removeTextChangedListener(loosePriceTextWatcher);
                                loosePrice.setText(twoDecimalPoint(unit_price));
                                loosePrice.addTextChangedListener(loosePriceTextWatcher);
                            }else {
                                if (!cartonPrice.getText().toString().isEmpty()){
                                    loosePrice.removeTextChangedListener(loosePriceTextWatcher);
                                    loosePrice.setText(twoDecimalPoint(Double.parseDouble(cartonPrice.getText().toString())));
                                    loosePrice.addTextChangedListener(loosePriceTextWatcher);
                                }else {
                                    loosePrice.removeTextChangedListener(loosePriceTextWatcher);
                                    loosePrice.setText("0.00");
                                    loosePrice.addTextChangedListener(loosePriceTextWatcher);
                                }
                            }
                        }
                    }
                    setCalculationView();
                }else {
                    setCalculationView();
                }


            }
        };
        cartonPrice.addTextChangedListener(cartonPriceTextWatcher);



        qtyValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    String qty = qtyValue.getText().toString();

                    if (!qty.matches("")) {
                        double qtyCalc = Double.parseDouble(qty);
                        if (qtyValue.getText().toString().matches("0")) {

                        } else {
                            qtyCalculation();
                        }
                        // Add the method to calculate values
                        setCalculationView();
                    }
                    return true;
                }
                return false;
            }
        });

        qtyTW = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                String qty = qtyValue.getText().toString();

                double stock=0.0;
                getLowStockSetting();

                if (!qty.matches("")) {

                    if (!stockQtyValue.getText().toString().isEmpty()){
                        stock=Double.parseDouble(stockQtyValue.getText().toString());
                    }

                    qtyCalculation();
                    setCalculationView();
                    if (stock < Double.parseDouble(qty)){
                        // Toast.makeText(getActivity(),"Stock is Low please check",Toast.LENGTH_SHORT).show();
                        //  qtyValue.setText("");
                    }else {
                        double qtyCalc = Double.parseDouble(qty);
                        if (qtyValue.getText().toString().matches("0")) {

                        } else {
                            qtyCalculation();
                            //  calculateQty();
                        }
                        setCalculationView();
                    }
                    reverse(Integer.parseInt(qty));
                }

                int length = qtyValue.getText().length();
                if (length == 0) {

                    if (qtyValue.getText().toString().matches("0")) {

                    } else {

                        setCalculationView();

                        cartonQtyValue.removeTextChangedListener(cqtyTW);
                        cartonQtyValue.setText("");
                        cartonQtyValue.addTextChangedListener(cqtyTW);

                        looseQtyValue.removeTextChangedListener(lqtyTW);
                        looseQtyValue.setText("");
                        looseQtyValue.addTextChangedListener(lqtyTW);

                    }
                    setCalculationView();
                }
                if (activityFrom.equals("ReOrderSales")){
                    // setButtonView();
                }else {
                    if (!isAllowLowStock){
                        checkLowStock();
                    }
                    // setButtonView();
                }
            }
        };

        qtyValue.addTextChangedListener(qtyTW);

        cartonQtyValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    if (cartonQtyValue.getText().toString().matches("0")) {
                        // cartonQtyPcsOne(0);
                    } else {
                        cartonQty();
                    }
                    looseQtyValue.requestFocus();
                    return true;
                }
                return false;
            }
        });

        cqtyTW = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ss_Cqty = cartonQtyValue.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                getLowStockSetting();
                if (cartonQtyValue.getText().toString().matches("0")) {

                    //cartonQtyPcsOne(0);
                    cartonQty();
                    int length = cartonQtyValue.getText().length();
                    if (length == 0) {
                        //  cartonQtyPcsOne(0);
                    }
                } else {
                    cartonQty();
                }
                int length = cartonQtyValue.length();
                if (length == 0) {

                    if (cartonQtyValue.getText().toString().matches("0")) {

                    } else {
                        String lqty = looseQtyValue.getText().toString();
                        if (lqty.matches("")) {
                            lqty = "0";
                        }
                        if (!lqty.matches("")) {
                            qtyValue.removeTextChangedListener(qtyTW);
                            if (lqty.equals("0")){
                                qtyValue.setText("");
                            }else {
                                qtyValue.setText(lqty);
                            }
                            qtyValue.addTextChangedListener(qtyTW);

                            if (qtyValue.length() != 0) {
                                qtyValue.setSelection(qtyValue.length());
                            }
                            double lsQty = Double.parseDouble(lqty);
                            setCalculationView();

                        }
                    }
                }
                if (activityFrom.equals("ReOrderSales")){
                    //setButtonView();
                }else {
                    if (!isAllowLowStock){
                        checkLowStock();
                    }
                    // setButtonView();
                }
            }
        };
        cartonQtyValue.addTextChangedListener(cqtyTW);


       /* cartonTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String qty=s.toString().trim();
                looseQtyCalc();
                setButtonView();
            }
        };
        cartonQtyValue.addTextChangedListener(cartonTextWatcher);*/




        looseQtyValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (looseQtyValue.getText().toString().matches("0")) {
                        //  looseQtyCalcPcsOne(0);
                    } else {
                        looseQtyCalc();
                    }
                    qtyValue.requestFocus();
                    return true;
                }
                return false;
            }
        });

        lqtyTW = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeLooseQty = looseQtyValue.getText().toString();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (looseQtyValue.getText().toString().matches("0")) {
                    // looseQtyCalcPcsOne(0);
                    looseQtyCalc();
                } else {
                    looseQtyCalc();
                }

                int length = looseQtyValue.length();
                if (length == 0) {

                    if (looseQtyValue.getText().toString().matches("0")) {

                    } else {

                        String qty = qtyValue.getText().toString();
                        if (!beforeLooseQty.matches("") && !qty.matches("")) {

                            int qtyCnvrt = Integer.parseInt(qty);
                            int lsCnvrt = Integer.parseInt(beforeLooseQty);

                            qtyValue.removeTextChangedListener(qtyTW);
                            qtyValue.setText("" + (qtyCnvrt - lsCnvrt));
                            qtyValue.addTextChangedListener(qtyTW);

                            if (qtyValue.length() != 0) {
                                qtyValue.setSelection(qtyValue.length());
                            }

                            looseQtyCalc();

                        }
                    }
                }
                if (!activityFrom.equals("SalesOrder") ||
                        !activityFrom.equals("SalesEdit") ||
                        activityFrom.equals("ReOrderSales")){
                    //setButtonView();
                }else {
                    if (!isAllowLowStock){
                        checkLowStock();
                    }
                    // setButtonView();
                }
            }

        };
        looseQtyValue.addTextChangedListener(lqtyTW);

        /*lqtyTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String qty=s.toString().trim();
                looseQtyCalcNew();
                setButtonView();
            }
        };
        looseQtyValue.addTextChangedListener(lqtyTextWatcher);*/

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = productAutoComplete.getText().toString();
                if (!productAutoComplete.getText().toString().isEmpty()) {

                    if (!qtyValue.getText().toString().isEmpty() && !s.isEmpty() &&
                            !qtyValue.getText().toString().equals("0") && !qtyValue.getText().toString().equals("00")
                            && !qtyValue.getText().toString().equals("000") &&
                            !qtyValue.getText().toString().equals("0000") &&
                            !netTotalValue.getText().toString().equals("0.00")) {

                        if (addProduct.getText().toString().equals("Update")) {
                            // if (!cartonPrice.getText().toString().isEmpty() && !cartonPrice.getText().toString().equals("0.00") && !cartonPrice.getText().toString().equals("0.0") && !cartonPrice.getText().toString().equals("0")){
                            if (salesReturn.isChecked()) {
                                if (isReverseCalculationEnabled) {
                                    if (loosePrice.getText() != null && !loosePrice.getText().toString().isEmpty()) {
                                        if (Double.parseDouble(loosePrice.getText().toString()) > 0) {
                                            double minimumsellingprice = Double.parseDouble(minimumSellingPriceText.getText().toString());
                                            if (minimumsellingprice <= Double.parseDouble(loosePrice.getText().toString())) {
                                                updateProduct();
                                            } else {
                                                showMinimumSellingpriceAlert(minimumSellingPriceText.getText().toString());
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "Price should not be zero", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "Enter the price", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    updateProduct();
                                }
                            } else {
                                if (Double.parseDouble(netTotalValue.getText().toString()) > 0) {
                                    if (isReverseCalculationEnabled) {
                                        if (loosePrice.getText() != null && !loosePrice.getText().toString().isEmpty()) {
                                            if (Double.parseDouble(loosePrice.getText().toString()) > 0) {
                                                double minimumsellingprice = Double.parseDouble(minimumSellingPriceText.getText().toString());
                                                if (minimumsellingprice <= Double.parseDouble(loosePrice.getText().toString())) {
                                                    updateProduct();
                                                } else {
                                                    showMinimumSellingpriceAlert(minimumSellingPriceText.getText().toString());
                                                }
                                            } else {
                                                Toast.makeText(getActivity(), "Price should not be zero", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "Enter the price", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        updateProduct();
                                    }

                                } else {
                                    Toast.makeText(getActivity(), "Enter the product price", Toast.LENGTH_SHORT).show();
                                }
                            }
                            //  }
                        } else {
                            if (salesReturn.isChecked()) {

                                if (loosePrice.getText().toString() != null && !loosePrice.getText().toString().isEmpty()) {
                                    if (Double.parseDouble(loosePrice.getText().toString()) > 0) {
                                        double minimumsellingprice = Double.parseDouble(minimumSellingPriceText.getText().toString());
                                        if (minimumsellingprice <= Double.parseDouble(loosePrice.getText().toString())) {
                                            addProduct("Add");
                                        } else {
                                            showMinimumSellingpriceAlert(minimumSellingPriceText.getText().toString());
                                        }
                                    } else {
                                        if ((focEditText.getText() != null && !focEditText.getText().toString().isEmpty() && !focEditText.getText().toString().equals("0")) || (returnEditext.getText() != null && !returnEditext.getText().toString().isEmpty() && !returnEditext.getText().toString().equals("0"))) {
                                            addProduct("Add");
                                        } else {
                                            Toast.makeText(getActivity(), "Price should not be zero", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Enter the price", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (Double.parseDouble(netTotalValue.getText().toString()) > 0) {
                                    if (isReverseCalculationEnabled) {
                                        if (loosePrice.getText().toString() != null && !loosePrice.getText().toString().isEmpty()) {
                                            if (Double.parseDouble(loosePrice.getText().toString()) > 0) {
                                                double minimumsellingprice = Double.parseDouble(minimumSellingPriceText.getText().toString());
                                                if (minimumsellingprice <= Double.parseDouble(loosePrice.getText().toString())) {
                                                    addProduct("Add");
                                                } else {
                                                    showMinimumSellingpriceAlert(minimumSellingPriceText.getText().toString());
                                                }
                                            } else {
                                                if ((focEditText.getText() != null && !focEditText.getText().toString().isEmpty() && !focEditText.getText().toString().equals("0")) || (returnEditext.getText() != null && !returnEditext.getText().toString().isEmpty() && !returnEditext.getText().toString().equals("0"))) {
                                                    addProduct("Add");
                                                } else {
                                                    Toast.makeText(getActivity(), "Price should not be zero", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "Enter the price", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        if ((focEditText.getText() != null && !focEditText.getText().toString().isEmpty() && !focEditText.getText().toString().equals("0")) || (returnEditext.getText() != null && !returnEditext.getText().toString().isEmpty() && !returnEditext.getText().toString().equals("0"))) {
                                            if (!qtyValue.getText().toString().isEmpty() && !qtyValue.getText().toString().equals("0")) {
                                                if (Double.parseDouble(netTotalValue.getText().toString()) > 0) {
                                                    double minimumsellingprice = Double.parseDouble(minimumSellingPriceText.getText().toString());
                                                    if (minimumsellingprice <= Double.parseDouble(loosePrice.getText().toString())) {
                                                        addProduct("Add");
                                                    } else {
                                                        showMinimumSellingpriceAlert(minimumSellingPriceText.getText().toString());
                                                    }
                                                } else {
                                                    Toast.makeText(getActivity(), "Enter the price of the product", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                double minimumsellingprice = Double.parseDouble(minimumSellingPriceText.getText().toString());
                                                if (minimumsellingprice <= Double.parseDouble(loosePrice.getText().toString())) {
                                                    addProduct("Add");
                                                } else {
                                                    showMinimumSellingpriceAlert(minimumSellingPriceText.getText().toString());
                                                }
                                            }
                                        } else {
                                            if (qtyValue.getText() != null && !qtyValue.getText().toString().isEmpty() && !qtyValue.getText().toString().equals("0")) {
                                                if (Double.parseDouble(netTotalValue.getText().toString()) > 0) {
                                                    double minimumsellingprice = Double.parseDouble(minimumSellingPriceText.getText().toString());
                                                    if (minimumsellingprice <= Double.parseDouble(loosePrice.getText().toString())) {
                                                        addProduct("Add");
                                                    } else {
                                                        showMinimumSellingpriceAlert(minimumSellingPriceText.getText().toString());
                                                    }
                                                } else {
                                                    Toast.makeText(getActivity(), "Enter the price of the product", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                showAlert();
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Enter the product price", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    } else {

                  /*  if (!qtyValue.getText().toString().isEmpty() && !qtyValue.getText().toString().equals("0") && (!focEditText.getText().toString().isEmpty() && !focEditText.getText().toString().equals("0")) || (!returnEditext.getText().toString().isEmpty() && !returnEditext.getText().toString().equals("0")) ){
                        addProduct("Add");
                    }else {
                        if (Double.parseDouble(netTotalValue.getText().toString()) > 0){
                            addProduct("Add");
                        }else {
                            Toast.makeText(getActivity(),"Enter the price of the product",Toast.LENGTH_SHORT).show();
                        }
                    }
*/
                        if ((focEditText.getText() != null && !focEditText.getText().toString().isEmpty() && !focEditText.getText().toString().equals("0")) || (returnEditext.getText() != null && !returnEditext.getText().toString().isEmpty() && !returnEditext.getText().toString().equals("0"))) {
                            if (!qtyValue.getText().toString().isEmpty() && !qtyValue.getText().toString().equals("0")) {
                                if (Double.parseDouble(netTotalValue.getText().toString()) > 0) {
                                    double minimumsellingprice = Double.parseDouble(minimumSellingPriceText.getText().toString());
                                    if (minimumsellingprice <= Double.parseDouble(loosePrice.getText().toString())) {
                                        addProduct("Add");
                                    } else {
                                        showMinimumSellingpriceAlert(minimumSellingPriceText.getText().toString());
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Enter the price of the product", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                addProduct("Add");
                            }
                        } else {
                            showAlert();
                        }
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Select product", Toast.LENGTH_SHORT).show();
                }
            }

        });

        productAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selectProduct = (String)parent.getItemAtPosition(position);
                String[] product = selectProduct.split("-", 0);
                productId=product[1].trim();
                productName=product[0].trim();
                qtyValue.requestFocus();
                Log.w("Selected_product",product[1]);
              /*  try {
                    getProductPrice(productId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                setProductResult(product[1].trim());
            }
        });

        focSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", ""+isChecked);
                if (isChecked){
                    focEditText.setHint("FOC ctn");
                }else {
                    focEditText.setHint("FOC pcs");
                }
                setCalculationView();
            }
        });

        exchangeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", ""+isChecked);
                if (isChecked){
                    exchangeEditext.setHint("Exchange ctn");
                }else {
                    exchangeEditext.setHint("Exchange pcs");
                }
                setCalculationView();
            }
        });

        returnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", ""+isChecked);
                if (isChecked){
                    returnEditext.setHint("Return ctn");
                }else {
                    returnEditext.setHint("Return pcs");
                }
                // setCalculation();
                setCalculationView();
            }

        });


        scanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), BarCodeScanner.class);
                startActivityForResult(intent,RESULT_CODE);
            }
        });

        // Hide the FOC Layout depending the Setting Invoice or Sales order

  /*      if (
                activityFrom.equals("InvoiceEdit")
                || activityFrom.equals("ConvertInvoice")
                || activityFrom.equals("ReOrderInvoice")
                || activityFrom.equals("Invoice")
        )
        {
            focLayout.setVisibility(View.VISIBLE);
        }else {
            focLayout.setVisibility(View.GONE);
        }*/



        // Setting the sorting
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View menuItemView = getView().findViewById(R.id.fab);
                PopupMenu popupMenu = new PopupMenu(getActivity(), menuItemView);
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

        // Define the sales Return Concept for the Invoice

        salesReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (salesReturn.isChecked()){
                    qtyValue.setBackgroundResource(R.drawable.invoice_edit_text_return);
                    looseQtyValue.setBackgroundResource(R.drawable.invoice_edit_text_return);
                    cartonQtyValue.setBackgroundResource(R.drawable.invoice_edit_text_return);
                    cartonPrice.setBackgroundResource(R.drawable.invoice_edit_text_return);
                    loosePrice.setBackgroundResource(R.drawable.invoice_edit_text_return);
                    focEditText.setBackgroundResource(R.drawable.invoice_edit_text_return);
                    exchangeEditext.setBackgroundResource(R.drawable.invoice_edit_text_return);
                    discountEditext.setBackgroundResource(R.drawable.invoice_edit_text_return);
                    returnEditext.setBackgroundResource(R.drawable.invoice_edit_text_return);
                    returnEditext.setEnabled(false);
                    discountEditext.setEnabled(false);
                    exchangeEditext.setEnabled(false);
                    focEditText.setEnabled(false);
                    uomText.setBackgroundResource(R.drawable.invoice_edit_text_return);
                    pcsPerCarton.setBackgroundResource(R.drawable.invoice_edit_text_return);
                    stockQtyValue.setBackgroundResource(R.drawable.invoice_edit_text_return);
                    stockCount.setBackgroundResource(R.drawable.invoice_edit_text_return);
                    productAutoComplete.setBackgroundResource(R.drawable.invoice_edit_text_return);
                    qtyValue.setText("");
                }else {
                    qtyValue.setBackgroundResource(R.drawable.invoice_edit_text);
                    looseQtyValue.setBackgroundResource(R.drawable.invoice_edit_text);
                    cartonQtyValue.setBackgroundResource(R.drawable.invoice_edit_text);
                    cartonPrice.setBackgroundResource(R.drawable.invoice_edit_text);
                    loosePrice.setBackgroundResource(R.drawable.invoice_edit_text);
                    focEditText.setBackgroundResource(R.drawable.invoice_edit_text);
                    exchangeEditext.setBackgroundResource(R.drawable.invoice_edit_text);
                    discountEditext.setBackgroundResource(R.drawable.invoice_edit_text);
                    returnEditext.setBackgroundResource(R.drawable.invoice_edit_text);
                    returnEditext.setEnabled(true);
                    discountEditext.setEnabled(true);
                    exchangeEditext.setEnabled(true);
                    focEditText.setEnabled(true);
                    uomText.setBackgroundResource(R.drawable.invoice_edit_text);
                    pcsPerCarton.setBackgroundResource(R.drawable.invoice_edit_text);
                    stockQtyValue.setBackgroundResource(R.drawable.invoice_edit_text);
                    stockCount.setBackgroundResource(R.drawable.invoice_edit_text);
                    productAutoComplete.setBackgroundResource(R.drawable.invoice_edit_text);
                }
            }
        });

        return view;
    }

    public void showMinimumSellingpriceAlert(String sellingPrice){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(requireActivity());
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

    public void checkLowStock(){
        double stock=0.0;
        double qty=0.0;
        if (!stockQtyValue.getText().toString().isEmpty()){
            if (!stockQtyValue.getText().toString().isEmpty()){
                stock=Double.parseDouble(stockQtyValue.getText().toString());
            }
            if (!qtyValue.getText().toString().isEmpty()){
                qty=Double.parseDouble(qtyValue.getText().toString());
            }
            if (stock < qty){
                Toast.makeText(getActivity(),"Low Stock please check",Toast.LENGTH_SHORT).show();
                qtyValue.removeTextChangedListener(qtyTW);
                qtyValue.setText("");
                qtyValue.addTextChangedListener(qtyTW);

                looseQtyValue.removeTextChangedListener(lqtyTW);
                looseQtyValue.setText("");
                looseQtyValue.addTextChangedListener(lqtyTW);

                cartonQtyValue.removeTextChangedListener(cqtyTW);
                cartonQtyValue.setText("");
                cartonQtyValue.addTextChangedListener(cqtyTW);
                setCalculationView();
            }
        }

    }

    public int reverse(int number){
        Log.w("ReverseNumberis:",Math.abs(number) * -1+"");
       return Math.abs(number) * -1;
    }

    public double reverseValue(double number){
        Log.w("ReverseValueis:",Math.abs(number) * -1+"");
        return Math.abs(number) * -1;
    }

    public void setCalculationView() {
        try {
            double taxAmount = 0.0, netTotal = 0.0;
            double taxAmount1 = 0.0, netTotal1 = 0.0;
            double return_qty=0.0;
            double pcspercarton=0.0;
            double cqtyCalc=0;
            double lqtyCalc=0;
            double net_qty=0.0;

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
            String selectCustomerId = sharedPreferences.getString("customerId", "");
            if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
                customerDetails = dbHelper.getCustomer(selectCustomerId);
            }
            String taxValue=customerDetails.get(0).getTaxPerc();
            String taxType=customerDetails.get(0).getTaxType();
            Log.w("TaxType12:",taxType);
            Log.w("TaxValue12:",taxValue);

            String lPrice = loosePrice.getText().toString();
            String cPrice = cartonPrice.getText().toString();
            String cqty = cartonQtyValue.getText().toString();
            String lqty = looseQtyValue.getText().toString();

            if (lPrice.matches("")) {
                lPrice = "0";
            }

            if (cPrice.matches("")) {
                cPrice = "0";
            }

            if (cqty.matches("")) {
                cqty = "0";
            }

            if (lqty.matches("")) {
                lqty = "0";
            }

            double lPriceCalc = Double.parseDouble(lPrice);
            double cPriceCalc = Double.parseDouble(cPrice);

             if (salesReturn.isChecked()){
                 cqtyCalc = reverse(Integer.parseInt(cqty));
                 lqtyCalc = reverse(Integer.parseInt(lqty));
             }else {
                 cqtyCalc = Double.parseDouble(cqty);
                 lqtyCalc = Double.parseDouble(lqty);
             }

            if (!pcsPerCarton.getText().toString().isEmpty() && !pcsPerCarton.getText().toString().equals("null")){
                pcspercarton=Double.parseDouble(pcsPerCarton.getText().toString());
                double data = pcspercarton;
                double cn_qty=Double.parseDouble(cqty);
                double lqtyvalue=Double.parseDouble(lqty);
                net_qty=(cn_qty*data)+lqtyvalue;
            }

           // double tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc);

            double tt = net_qty * lPriceCalc;

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

            if (!returnEditext.getText().toString().isEmpty() && !returnEditext.getText().toString().equals("null")){
                return_qty=Double.parseDouble(returnEditext.getText().toString());
            }

            if (return_qty!=0){
                if (returnSwitch.isChecked()){
                    return_qty=Double.parseDouble(returnEditext.getText().toString()) * pcspercarton;
                }else {
                    return_qty=Double.parseDouble(returnEditext.getText().toString());
                }
                double return_amt=0.0;
                if (pcspercarton > 1){
                    return_amt=(return_qty*lPriceCalc);
                }else if (pcspercarton==1){
                    return_amt=(return_qty*cPriceCalc);
                }
                subTotal=subTotal-return_amt;
            }

            // sl_total_inclusive.setText("" + sbTtl);
            tt=subTotal;

            if (!taxType.matches("") && !taxValue.matches("")) {

                double taxValueCalc = Double.parseDouble(taxValue);

                if (taxType.matches("E")) {

                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subTotal * taxValueCalc) / 100;
                        String prodTax = twoDecimalPoint(taxAmount1);
                        taxValueText.setText("" + prodTax);

                        netTotal1 = subTotal + taxAmount1;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        netTotalValue.setText("" + ProdNetTotal);
                        taxTitle.setText("GST ( Exc )");
                        subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                    } else {

                        taxAmount = (tt * taxValueCalc) / 100;
                        String prodTax = twoDecimalPoint(taxAmount);
                        taxValueText.setText("" + prodTax);

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        taxTitle.setText("GST ( Exc )");
                        netTotalValue.setText("" + ProdNetTotal);
                        subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                    }

                } else if (taxType.matches("I")) {
                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subTotal * taxValueCalc)
                                / (100 + taxValueCalc);
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
                    } else {
                        taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = twoDecimalPoint(taxAmount);
                        taxValueText.setText("" + prodTax);

                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        netTotalValue.setText("" + ProdNetTotal);


                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        //  sl_total_inclusive.setText(totalIncl);
                        double sub_total=subTotal-taxAmount;
                        taxTitle.setText("GST ( Inc )");
                        subTotalValue.setText(Utils.twoDecimalPoint(sub_total));
                    }

                } else if (taxType.matches("Z")) {

                    taxValueText.setText("0.0");
                    if (!itemDisc.matches("")) {
                        // netTotal1 = subTotal + taxAmount;
                        netTotal1 = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        netTotalValue.setText("" + ProdNetTotal);
                        subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                        taxTitle.setText("GST ( Zero )");
                    } else {
                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        netTotalValue.setText("" + ProdNetTotal);
                        subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                        taxTitle.setText("GST ( Zero )");
                    }

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

        }
    }



    public void cartonQty() {
        String crtnQty = cartonQtyValue.getText().toString();

        if (!pcsPerCarton.getText().toString().matches("") && !crtnQty.matches("")) {

            double data = Double.parseDouble(crtnQty);
            int value = (int)data;
            double net_pcs_carton = Double.parseDouble(pcsPerCarton.getText().toString());
            int pcspercarton = (int) net_pcs_carton;
            int qty = 0;
            String lsQty = looseQtyValue.getText().toString();

            if (!lsQty.matches("")) {

                double lvalue = Double.parseDouble(lsQty);
                int lqty = (int)lvalue;
                qty = (value * pcspercarton) + lqty;
            } else {
                qty = value * pcspercarton;
            }

            qtyValue.removeTextChangedListener(qtyTW);
            qtyValue.setText("" + qty);
            qtyValue.addTextChangedListener(qtyTW);

            if (qtyValue.length() != 0) {
                qtyValue.setSelection(qtyValue.length());
            }
            setCalculationView();
        }
    }

    public void qtyCalculation() {
        try {
            String qty = qtyValue.getText().toString();
            Log.d("qty recalc", "" + qty);
            String crtnperQty = pcsPerCarton.getText().toString();
            double q = 0, r = 0;

            if (crtnperQty.matches("0") || crtnperQty.matches("null")
                    || crtnperQty.matches("0.00")) {
                crtnperQty = "1";
            }

            if (!crtnperQty.matches("")) {
                if (!qty.matches("")) {
                    try {
                        double qty_nt = Double.parseDouble(qty);
                        double pcs_nt = Double.parseDouble(crtnperQty);

                        Log.d("qty_nt", "" + qty_nt);
                        Log.d("pcs_nt", "" + pcs_nt);

                        q = (int) (qty_nt / pcs_nt);
                        r = (qty_nt % pcs_nt);

                        Log.d("cqty", "" + q);
                        Log.d("lqty", "" + r);

                        String ctn = twoDecimalPoint(q);
                        String loose = twoDecimalPoint(r);

                        cartonQtyValue.removeTextChangedListener(cqtyTW);
                        cartonQtyValue.setText(Utils.getQtyValue(ctn));
                        cartonQtyValue.addTextChangedListener(cqtyTW);

                        looseQtyValue.removeTextChangedListener(lqtyTW);
                        looseQtyValue.setText(Utils.getQtyValue(loose));
                        looseQtyValue.addTextChangedListener(lqtyTW);

                        if (loose.isEmpty()) {
                            // productTotal(qty_nt);
                        } else {
                            setCalculationView();
                        }

                    } catch (ArithmeticException e) {
                        System.out.println("Err: Divided by Zero");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


   /* public void clQty() {
        String qty = qtyValue.getText().toString();
        String crtnperQty = pcsPerCarton.getText().toString();
        int q = 0, r = 0;

        if (crtnperQty.matches("0") || crtnperQty.matches("null") || crtnperQty.matches("0.00")) {
            crtnperQty = "1";
        }

        if (!crtnperQty.matches("")) {
            if (!qty.matches("")) {
                try {

                    double data = Double.parseDouble(qty);
                    int value = (int)data;
                    double DoubleValue = Double.parseDouble(pcsPerCarton.getText().toString());
                    int net_val = (int) DoubleValue;
                    Log.d("qty_nt", "" + value);
                    Log.d("pcs_nt", "" + net_val);

                    q = value / net_val;
                    r = value % net_val;

                    Log.d("cqty", "" + q);
                    Log.d("lqty", "" + r);

                    cartonQtyValue.setText("" + q);
                    looseQtyValue.setText("" + r);

                } catch (ArithmeticException e) {
                    System.out.println("Err: Divided by Zero");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }*/

    public void setButtonView(){
        if (salesReturn.isChecked()){
            if (Double.parseDouble(netTotalValue.getText().toString()) < 0){
                addProduct.setAlpha(0.9F);
                addProduct.setEnabled(true);
            }else {
                //Toast.makeText(getActivity(),"Add Return Qty value",Toast.LENGTH_LONG).show();
            }
        }else {
            if (Double.parseDouble(netTotalValue.getText().toString()) > 0){
                addProduct.setAlpha(0.9F);
                addProduct.setEnabled(true);
            }else {
                if ((!focEditText.getText().toString().isEmpty() && !focEditText.getText().toString().equals("0")) || (!returnEditext.getText().toString().isEmpty() && !returnEditext.getText().toString().equals("0"))){
                    addProduct.setAlpha(0.9F);
                    addProduct.setEnabled(true);
                }else {
                    addProduct.setAlpha(0.4F);
                    addProduct.setEnabled(false);
                }
            }
        }
    }

    public void setProductResult(String productId){
        try {
            getLowStockSetting();
            for (ProductsModel model:productList) {
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
                    stockLayout.setVisibility(View.VISIBLE);

                    if (Double.parseDouble(model.getStockQty()) == 0 || Double.parseDouble(model.getStockQty()) < 0) {
                        if (isAllowLowStock){
                            productAutoComplete.clearFocus();
                            cartonPrice.setText(model.getUnitCost());
                            loosePrice.setText(model.getUnitCost());
                            uomText.setText(model.getUomCode());
                            stockCount.setText(model.getStockQty());
                            pcsPerCarton.setText(model.getPcsPerCarton());
                            if (Double.parseDouble(model.getPcsPerCarton()) > 1) {
                                cartonQtyValue.setEnabled(true);
                                loosePrice.setEnabled(true);
                                looseQtyValue.setEnabled(true);
                            } else {
                                cartonQtyValue.setEnabled(false);
                                loosePrice.setEnabled(true);
                                looseQtyValue.setEnabled(false);
                            }
                            //   cartonQtyValue.setEnabled(true);
                            //  looseQtyValue.setEnabled(true);
                            qtyValue.setEnabled(true);
                            // qtyValue.requestFocus();
                            qtyValue.setText("");
                            cartonQtyValue.setText("");
                            looseQtyValue.setText("");
                            cartonPrice.setEnabled(true);
                            qtyValue.requestFocus();
                            openKeyborard(qtyValue);
                            qtyValue.setSelection(qtyValue.getText().length());

                            focEditText.setEnabled(true);
                            exchangeEditext.setEnabled(true);
                            discountEditext.setEnabled(true);
                            returnEditext.setEnabled(true);

                        }else {
                            Toast.makeText(getActivity(), "Low Stock Please check", Toast.LENGTH_SHORT).show();
                            productAutoComplete.clearFocus();
                            productAutoComplete.setText("");
                            qtyValue.setEnabled(false);
                            cartonQtyValue.setEnabled(false);
                            looseQtyValue.setEnabled(false);
                            cartonPrice.setEnabled(false);
                            loosePrice.setEnabled(true);
                        }
                    } else {
                        productAutoComplete.clearFocus();
                        cartonPrice.setText(model.getUnitCost());
                        loosePrice.setText(model.getUnitCost());
                        uomText.setText(model.getUomCode());
                        stockCount.setText(model.getStockQty());
                        pcsPerCarton.setText(model.getPcsPerCarton());
                        if (Double.parseDouble(model.getPcsPerCarton()) > 1) {
                            cartonQtyValue.setEnabled(true);
                            loosePrice.setEnabled(true);
                            looseQtyValue.setEnabled(true);
                        } else {
                            cartonQtyValue.setEnabled(false);
                            loosePrice.setEnabled(true);
                            looseQtyValue.setEnabled(false);
                        }
                        //   cartonQtyValue.setEnabled(true);
                        //  looseQtyValue.setEnabled(true);
                        qtyValue.setEnabled(true);
                        // qtyValue.requestFocus();
                        qtyValue.setText("");
                        cartonQtyValue.setText("");
                        looseQtyValue.setText("");
                        cartonPrice.setEnabled(true);
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

    public void addProduct(String action){
        if (isProductExist(productId)){
            Log.w("ProductIdView:",productId);
            showExistingProductAlert(productId,productName);
        }else {
            insertProducts();
        }
    }

    public void insertProducts(){
        try {
            String focType="pcs";
            String exchangeType="pcs";
            String returnType="pcs";
            String discount="0";
            String return_qty="0";
            String lPriceCalc="0";
            String cartonprice="0";

            if (focSwitch.isChecked()){
                focType="ctn";
            }
            if (exchangeSwitch.isChecked()){
                exchangeType="ctn";
            }
            if (returnSwitch.isChecked()){
                returnType="ctn";
            }

            String ctn_qty="0";
            String loose_qty="0";
            if (!cartonQtyValue.getText().toString().isEmpty()){
                ctn_qty=cartonQtyValue.getText().toString();
            }

            if (!looseQtyValue.getText().toString().isEmpty()){
                loose_qty=looseQtyValue.getText().toString();
            }

            if (!discountEditext.getText().toString().isEmpty()){
                discount=discountEditext.getText().toString();
            }
            if (!returnEditext.getText().toString().isEmpty()){
                return_qty=returnEditext.getText().toString();
            }

            if (!loosePrice.getText().toString().isEmpty()){
                lPriceCalc=loosePrice.getText().toString();
            }

            if (!cartonPrice.getText().toString().isEmpty()){
                cartonprice=cartonPrice.getText().toString();
            }

            double return_amt=(Double.parseDouble(return_qty)*Double.parseDouble(lPriceCalc));
            double total=(Double.parseDouble(ctn_qty) * Double.parseDouble(cartonprice)) + (Double.parseDouble(loose_qty) * Double.parseDouble(lPriceCalc));
            double sub_total=total-return_amt-Double.parseDouble(discount);

            if (salesReturn.isChecked()){
                ctn_qty=reverse(Integer.parseInt(ctn_qty))+"";
                loose_qty=reverse(Integer.parseInt(loose_qty))+"";
                sub_total=reverseValue(sub_total);
                total=reverseValue(total);
            }

            double priceValue=0.0;
            double data = Double.parseDouble(pcsPerCarton.getText().toString());
            double cn_qty=Double.parseDouble(cartonQtyValue.getText().toString());
            double lqty=Double.parseDouble(looseQtyValue.getText().toString());
            double net_qty=(cn_qty*data)+lqty;

            if (customerDetails.get(0).getTaxType().equals("I")){
                double subtotal=Double.parseDouble(subTotalValue.getText().toString());
                priceValue= subtotal / net_qty;
            }else {
                priceValue=Double.parseDouble(cartonPrice.getText().toString());
            }

            boolean status= dbHelper.insertCart(
                    productId.toString().trim(),
                    productName,
                    ctn_qty,
                    loose_qty,
                    subTotalValue.getText().toString(),
                    "",
                    netTotalValue.getText().toString(),
                    "weight",
                    cartonPrice.getText().toString(),

                    subTotalValue.getText().toString(),

                    pcsPerCarton.getText().toString(),
                    taxValueText.getText().toString(),
                    subTotalValue.getText().toString(),
                    customerDetails.get(0).getTaxType(),
                    focEditText.getText().toString(),
                    focType,
                    exchangeEditext.getText().toString(),
                    exchangeType,
                    discount,
                    returnEditext.getText().toString(),
                    returnType,"",
                    String.valueOf(total),
                    stockQtyValue.getText().toString(),
                    uomText.getText().toString(),
                    minimumSellingPriceText.getText().toString(),
                    stockQtyValue.getText().toString()
                    );
            if (status){
                cartonQtyValue.setText("");
                looseQtyValue.setText("");
                subTotalValue.setText("0.0");
                taxValueText.setText("0.00");
                netTotalValue.setText("0.0");
                productAutoComplete.setText("");
                cartonPrice.setText("0.00");
                loosePrice.setText("0.00");
                cartonQtyValue.setText("");
                looseQtyValue.setText("");
                qtyValue.setText("");
                pcsPerCarton.setText("");
                uomText.setText("");
                stockCount.setText("");
                qtyValue.clearFocus();
                cartonQtyValue.clearFocus();
                productAutoComplete.clearFocus();
                focEditText.setText("");
                exchangeEditext.setText("");
                discountEditext.setText("");
                returnEditext.setText("");
                focSwitch.setChecked(false);
                exchangeSwitch.setChecked(false);
                returnSwitch.setChecked(false);
                stockLayout.setVisibility(View.GONE);
                cartonPrice.setEnabled(false);
                loosePrice.setEnabled(false);
                looseQtyValue.setEnabled(false);
                qtyValue.setEnabled(false);
                cartonQtyValue.setEnabled(false);

                focEditText.setEnabled(false);
                exchangeEditext.setEnabled(false);
                discountEditext.setEnabled(false);
                returnEditext.setEnabled(false);

                Toast.makeText(getActivity(),"Product Added Successfully",Toast.LENGTH_LONG).show();
                getProducts();
                //  setSummaryTotal();
            }else {
                Toast.makeText(getActivity(),"Error in Add product",Toast.LENGTH_LONG).show();
            }
        }catch (Exception ec){
            Log.e("Error_InsertProduct:",ec.getMessage());
        }

    }

    public void showExistingProductAlert(String productId,String productName){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(requireActivity());
        builder1.setTitle("Warning !");
        builder1.setMessage(productName+" - "+productId+ "\nAlready Exist Do you want to replace ? ");
        builder1.setCancelable(false);
        builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                insertProducts();
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

    public void clearFields(){
        cartonQtyValue.setText("");
        looseQtyValue.setText("");
        subTotalValue.setText("0.0");
        taxValueText.setText("0.00");
        netTotalValue.setText("0.0");
        productAutoComplete.setText("");
        cartonPrice.setText("0.00");
        loosePrice.setText("0.00");
        cartonQtyValue.setText("");
        looseQtyValue.setText("");
        qtyValue.setText("");
        pcsPerCarton.setText("");
        uomText.setText("");
        stockCount.setText("");
        qtyValue.clearFocus();
        cartonQtyValue.clearFocus();
        productAutoComplete.clearFocus();
        focEditText.setText("");
        exchangeEditext.setText("");
        discountEditext.setText("");
        returnEditext.setText("");
        focSwitch.setChecked(false);
        exchangeSwitch.setChecked(false);
        returnSwitch.setChecked(false);
        stockLayout.setVisibility(View.GONE);
        cartonPrice.setEnabled(false);
        loosePrice.setEnabled(false);
        looseQtyValue.setEnabled(false);
        qtyValue.setEnabled(false);
        cartonQtyValue.setEnabled(false);
        salesReturn.setChecked(false);
        getProducts();
        setSummaryTotal();
    }

    public void updateProduct(){
        String focType="pcs";
        String exchangeType="pcs";
        String returnType="pcs";
        String return_qty="0";
        String lPriceCalc="0";
        String cartonprice="0";
        String discount="0";
        if (focSwitch.isChecked()){
            focType="ctn";
        }
        if (exchangeSwitch.isChecked()){
            exchangeType="ctn";
        }
        if (returnSwitch.isChecked()){
            returnType="ctn";
        }

        String ctn_qty="0";
        String loose_qty="0";
        if (!cartonQtyValue.getText().toString().isEmpty()){
            ctn_qty=cartonQtyValue.getText().toString();
        }

        if (!looseQtyValue.getText().toString().isEmpty()){
            loose_qty=looseQtyValue.getText().toString();
        }

        if (!returnEditext.getText().toString().isEmpty()){
            return_qty=returnEditext.getText().toString();
        }

        if (!discountEditext.getText().toString().isEmpty()){
            discount=discountEditext.getText().toString();
        }

        if (!loosePrice.getText().toString().isEmpty()){
            lPriceCalc=loosePrice.getText().toString();
        }

        if (!cartonPrice.getText().toString().isEmpty()){
            cartonprice=cartonPrice.getText().toString();
        }

        double return_amt=(Double.parseDouble(return_qty)*Double.parseDouble(lPriceCalc));
        double total=(Double.parseDouble(ctn_qty) * Double.parseDouble(cartonprice)) + (Double.parseDouble(loose_qty) * Double.parseDouble(lPriceCalc));
        double sub_total=total-return_amt-Double.parseDouble(discount);

        boolean status= dbHelper.insertCart(
                productId.toString().trim(),
                productName,
                ctn_qty,
                loose_qty,
                subTotalValue.getText().toString(),
                "",
                netTotalValue.getText().toString(),
                "weight",
                cartonPrice.getText().toString(),

                subTotalValue.getText().toString(),

                pcsPerCarton.getText().toString(),
                taxValueText.getText().toString(),
                subTotalValue.getText().toString(),
                customerDetails.get(0).getTaxType(),
                focEditText.getText().toString(),
                focType,
                exchangeEditext.getText().toString(),
                exchangeType,
                discount,
                returnEditext.getText().toString(),
                returnType,"",
                String.valueOf(total),
                stockQtyValue.getText().toString(),
                uomText.getText().toString(),
                minimumSellingPriceText.getText().toString(), stockQtyValue.getText().toString());


        if (status){
            cartonQtyValue.setText("");
            looseQtyValue.setText("");
            subTotalValue.setText("0.0");
            taxValueText.setText("0.00");
            netTotalValue.setText("0.0");
            productAutoComplete.setText("");
            cartonPrice.setText("0.00");
            loosePrice.setText("0.00");
            cartonQtyValue.setText("");
            looseQtyValue.setText("");
            qtyValue.setText("");
            pcsPerCarton.setText("");
            uomText.setText("");
            stockCount.setText("");
            qtyValue.clearFocus();
            cartonQtyValue.clearFocus();
            productAutoComplete.clearFocus();
            focEditText.setText("");
            exchangeEditext.setText("");
            discountEditext.setText("");
            returnEditext.setText("");
            focSwitch.setChecked(false);
            exchangeSwitch.setChecked(false);
            returnSwitch.setChecked(false);
            stockLayout.setVisibility(View.GONE);
            cartonPrice.setEnabled(false);
            loosePrice.setEnabled(false);
            looseQtyValue.setEnabled(false);
            qtyValue.setEnabled(false);
            cartonQtyValue.setEnabled(false);
            Utils.refreshActionBarMenu(requireActivity());
            addProduct.setText("Add");
            salesReturn.setEnabled(true);
            salesReturn.setChecked(false);
            getProducts();
            hideKeyboard();
            setSummaryTotal();
        }else {
            Toast.makeText(getActivity(),"Error in Add product",Toast.LENGTH_LONG).show();
        }
    }


    public void looseQtyCalc() {
        String crtnQty = cartonQtyValue.getText().toString();
        String lsQty = looseQtyValue.getText().toString();

        if (lsQty.matches("") || lsQty.equals("0")) {
            lsQty = "0";
        }

        if (!pcsPerCarton.getText().toString().matches("") && !crtnQty.matches("") && !lsQty.matches("")) {

            double cartonQtyCalc = Double.parseDouble(crtnQty);
            double cartonPerQtyCalc = Double.parseDouble(pcsPerCarton.getText().toString());
            double looseQtyCalc = Double.parseDouble(lsQty);
            double qty;

            qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

            qtyValue.removeTextChangedListener(qtyTW);
            if (qty==0){
                qtyValue.setText("");
            }else {
                qtyValue.setText(Utils.getQtyValue(String.valueOf(qty)));
            }
            qtyValue.addTextChangedListener(qtyTW);

            if (qtyValue.length() != 0) {
                qtyValue.setSelection(qtyValue.length());
            }

            setCalculationView();
        }

        if (!lsQty.matches("") && !lsQty.equals("0")) {

            double looseQtyCalc = Double.parseDouble(lsQty);
            double qty;

            if (!crtnQty.matches("") && !pcsPerCarton.getText().toString().matches("")) {
                double cartonQtyCalc = Double.parseDouble(crtnQty);
                double cartonPerQtyCalc = Double.parseDouble(pcsPerCarton.getText().toString());
                qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
            } else {
                qty = looseQtyCalc;
            }

            qtyValue.removeTextChangedListener(qtyTW);
            qtyValue.setText(Utils.getQtyValue(String.valueOf(qty)));
            qtyValue.addTextChangedListener(qtyTW);

            if (qtyValue.length() != 0) {
                qtyValue.setSelection(qtyValue.length());
            }
            setCalculationView();
        }
    }

    // calculation for the product
    public void setCalculation(){
        // Define the Required variables to calulate the Amount
        try {
            double cnQty=0.0;
            double lqty=0.0;
            double pcspercarton=0.0;
            double carton_amount=0.0;
            double net_amount=0.0;
            double loose_amount=0.0;
            double cprice=0.0;
            double lprice=0.0;
            double discount=0.0;
            double return_qty=0;

            if (!cartonPrice.getText().toString().isEmpty()){
                if (!cartonPrice.getText().toString().equals("null")){
                    cprice=Double.parseDouble(cartonPrice.getText().toString());
                }
            }
            if (!loosePrice.getText().toString().isEmpty()){
                if (!loosePrice.getText().toString().equals("null")){
                    lprice=Double.parseDouble(loosePrice.getText().toString());
                }
            }

            if (!cartonQtyValue.getText().toString().isEmpty()){
                cnQty=Double.parseDouble(cartonQtyValue.getText().toString());
            }

            if (!looseQtyValue.getText().toString().isEmpty()){
                lqty=Double.parseDouble(looseQtyValue.getText().toString());
            }

            if (!discountEditext.getText().toString().isEmpty()){
                discount=Double.parseDouble(discountEditext.getText().toString());
            }

            if (!pcsPerCarton.getText().toString().isEmpty()){
                pcspercarton=Double.parseDouble(pcsPerCarton.getText().toString());
            }

            if (!returnEditext.getText().toString().isEmpty()){
                return_qty=Double.parseDouble(returnEditext.getText().toString());
            }

            // calculating the net total
            if (pcspercarton>1){
                carton_amount=(cnQty*cprice);
                loose_amount=(lqty*lprice);
                net_amount=carton_amount+loose_amount;
                // netPrice.setText(twoDecimalPoint(net_amount));
                Log.w("Net_amount_1:", String.valueOf(net_amount));
            }else {
                net_amount=(cnQty*cprice);
                // netPrice.setText(twoDecimalPoint(carton_amount));
                Log.w("Net_amount_2:", String.valueOf(net_amount));
            }

            if (return_qty!=0){
                if (returnSwitch.isChecked()){
                    return_qty=Double.parseDouble(returnEditext.getText().toString()) * pcspercarton;
                }else {
                    return_qty=Double.parseDouble(returnEditext.getText().toString());
                }
                double return_amt=(return_qty*lprice);
                net_amount=net_amount-return_amt;
            }
            if (discount!=0.0){
                net_amount=net_amount-discount;
            }

            taxCalculation(net_amount);
        }catch (Exception ex){
            Log.e("ErrorInSet Calculation:",ex.getMessage());
        }

    }

    public void taxCalculation(double subTotal){
        try {

            customerDetails=dbHelper.getCustomer();
            String taxValue=customerDetails.get(0).getTaxPerc();
            String taxType=customerDetails.get(0).getTaxType();
            String itemDisc="0";
            double taxAmount=0.0;
            double netTotal=0.0;
            Log.w("TaxType:",taxType);
            Log.w("TaxValue:",taxValue);


            if (!taxType.matches("") && !taxValue.matches("")) {

                double taxValueCalc = Double.parseDouble(taxValue);

                if (taxType.matches("E")) {

                    if (!itemDisc.matches("")) {
                        taxAmount = (subTotal * taxValueCalc) / 100;
                        String prodTax = twoDecimalPoint(taxAmount);
                        // sl_tax.setText("" + prodTax);

                        netTotal = subTotal + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        //sl_netTotal.setText("" + ProdNetTotal);

                        Log.w("Tax_1",prodTax);
                        Log.w("Net_amount:",ProdNetTotal);
                        taxTitle.setText("Tax ( Exclusive )");
                        subTotalValue.setText(twoDecimalPoint(subTotal));
                        taxValueText.setText(String.valueOf(prodTax));
                        netTotalValue.setText(ProdNetTotal);
                    } else {

                        taxAmount = (subTotal * taxValueCalc) / 100;
                        String prodTax = twoDecimalPoint(taxAmount);

                        //sl_tax.setText("" + prodTax);

                        netTotal = subTotal + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        // sl_netTotal.setText("" + ProdNetTotal);

                        Log.w("Tax_1",prodTax);
                        Log.w("Net_amount:",ProdNetTotal);
                        taxTitle.setText("Tax ( Exc )");
                        subTotalValue.setText(twoDecimalPoint(subTotal));
                        taxValueText.setText(String.valueOf(prodTax));
                        netTotalValue.setText(ProdNetTotal);
                    }

                } else if (taxType.matches("I")) {
                    if (!itemDisc.matches("")) {
                        taxAmount = (subTotal * taxValueCalc)
                                / (100 + taxValueCalc);
                        String prodTax = twoDecimalPoint(taxAmount);

                        // sl_tax.setText("" + prodTax);

                        // netTotal1 = subTotal + taxAmount1;
                        netTotal = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal);

                        //  sl_netTotal.setText("" + ProdNetTotal);


                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);

                        //   sl_total_inclusive.setText(totalIncl);

                        Log.w("Tax_1",prodTax);
                        Log.w("Net_amount:",ProdNetTotal);
                        subTotalValue.setText(twoDecimalPoint(Double.parseDouble(totalIncl)));
                        taxValueText.setText(prodTax);
                        netTotalValue.setText(ProdNetTotal);
                        taxTitle.setText("Tax ( Inc )");
                    } else {
                        taxAmount = (subTotal * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = twoDecimalPoint(taxAmount);

                        // sl_tax.setText("" + prodTax);

                        netTotal = subTotal + taxAmount;
                        netTotal = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal);

                        //   sl_netTotal.setText("" + ProdNetTotal);

                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);

                        //  sl_total_inclusive.setText(totalIncl);

                        Log.w("Tax_1",prodTax);
                        Log.w("Net_amount:",ProdNetTotal);
                        taxTitle.setText("Tax ( Inc )");
                        subTotalValue.setText(totalIncl);
                        taxValueText.setText(String.valueOf(prodTax));
                        netTotalValue.setText(ProdNetTotal);
                    }

                } else if (taxType.matches("Z")) {

                    ///  sl_tax.setText("0.0");
                    if (!itemDisc.matches("")) {
                        netTotal = subTotal + taxAmount;
                        netTotal = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal);

                        //  sl_netTotal.setText("" + ProdNetTotal);

                        /// Log.w("Tax_1",prodTax);
                        Log.w("Net_amount:",ProdNetTotal);
                        taxTitle.setText("Tax");
                        subTotalValue.setText(twoDecimalPoint(subTotal));
                        taxValueText.setText("0.0");
                        netTotalValue.setText(ProdNetTotal);
                    } else {
                        netTotal = subTotal + taxAmount;
                        netTotal = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        //  sl_netTotal.setText("" + ProdNetTotal);

                        //  Log.w("Tax_1",prodTax);
                        Log.w("Net_amount:",ProdNetTotal);
                        taxTitle.setText("Tax");
                        subTotalValue.setText(twoDecimalPoint(subTotal));
                        taxValueText.setText("0.0");
                        netTotalValue.setText(ProdNetTotal);
                    }

                } else {
                    // sl_tax.setText("0.0");
                    // sl_netTotal.setText("" + Prodtotal);
                    //  totalTextView.setText(String.valueOf(subTotal));
                    //  taxTextView.setText(String.valueOf(prodTax));
                    // netTotalTextView.setText(ProdNetTotal);
                }

            } else if (taxValue.matches("")) {
                //  sl_tax.setText("0.0");
                //  sl_netTotal.setText("" + Prodtotal);
                //  totalTextView.setText(String.valueOf(subTotal));
                //  taxTextView.setText(String.valueOf(prodTax));
                //   netTotalTextView.setText(ProdNetTotal);
            } else {
                //  sl_tax.setText("0.0");
                // sl_netTotal.setText("" + Prodtotal);
                //totalTextView.setText(String.valueOf(subTotal));
                // taxTextView.setText(String.valueOf(prodTax));
                // netTotalTextView.setText(Prodtotal);
            }
        }catch (Exception ex){}


    }


    public void viewCloseBottomSheet(){
        hideKeyboard();
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

        productListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        selectProductAdapter=new SelectProductAdapter(getActivity(),filteredProducts, new SelectProductAdapter.CallBack() {
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
                cartonPrice.setText(model.getUnitCost()+"");
                loosePrice.setText(model.getUnitCost());
                uomText.setText(model.getUomCode());
                stockCount.setText(model.getStockQty());
                pcsPerCarton.setText(model.getPcsPerCarton());
                qtyValue.requestFocus();
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                openKeyborard(qtyValue);
                qtyValue.setEnabled(true);
                cartonQtyValue.setEnabled(true);
                // looseQtyValue.setEnabled(true);
                cartonPrice.setEnabled(true);
                qtyValue.setText("");
                focEditText.setEnabled(true);
                exchangeEditext.setEnabled(true);
                discountEditext.setEnabled(true);
                returnEditext.setEnabled(true);
                if (Double.parseDouble(model.getPcsPerCarton())>1){
                    cartonQtyValue.setEnabled(true);
                    loosePrice.setEnabled(true);
                    looseQtyValue.setEnabled(true);
                }else {
                    cartonQtyValue.setEnabled(false);
                    loosePrice.setEnabled(true);
                    looseQtyValue.setEnabled(false);
                }
                cartonQtyValue.setText("");
                looseQtyValue.setText("");
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
                //viewCloseBottomSheet();
            }
        });
        productListView.setAdapter(selectProductAdapter);
        totalProducts.setText(filteredProducts.size()+" Products");
        // get the Customer name from the local db
    }

    public void openKeyborard(EditText editText){
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void setSummaryTotal(){
        try {
            ArrayList<CartModel> localCart;
            localCart = dbHelper.getAllCartItems();
            double net_sub_total=0.0;
            double net_tax=0.0;
            double net_total=0.0;
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
                }
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
                selectCustomerId = sharedPreferences.getString("customerId", "");
                customerDetails=dbHelper.getCustomer(selectCustomerId);
                String taxValue =customerDetails.get(0).getTaxPerc();
                String taxType=customerDetails.get(0).getTaxType();

                Log.w("GivenEditTax:",net_tax+"");
                Log.w("GivenNetTotalValues:",net_total+"");

                setCalculationSummaryView(net_sub_total);

              /*  if (taxType.equals("I")){
                    double sub_total=net_total-net_tax;
                    subTotalValue.setText(Utils.twoDecimalPoint(sub_total));
                    taxValueText.setText(Utils.twoDecimalPoint(net_tax));
                    netTotalValue.setText(Utils.twoDecimalPoint(net_total));
                }else {
                    subTotalValue.setText(Utils.twoDecimalPoint(net_sub_total));
                    taxValueText.setText(Utils.twoDecimalPoint(net_tax));
                    netTotalValue.setText(Utils.twoDecimalPoint(net_total));
                }*/
            }
        }catch (Exception ex){}
    }

    public void setCalculationSummaryView(double subTotal) {
        try {
            double taxAmount1 = 0.0, netTotal1 = 0.0;

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
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

    @SuppressLint("SetTextI18n")
    private void getProducts() {
        productSummaryList=new ArrayList<>();
        ArrayList<CartModel> localCart;
        localCart = dbHelper.getAllCartItems();
        if (localCart.size()>0){
            for (CartModel cart:localCart){
                ProductSummaryModel model=new ProductSummaryModel();
                model.setProductName(cart.CART_COLUMN_PNAME);
                model.setDescription("description");
                model.setNetAmount(cart.getSubTotal());
                double pcsctn=Double.parseDouble(cart.CART_PCS_PER_CARTON);
                double cnqty=Double.parseDouble(cart.CART_COLUMN_CTN_QTY);
                double netqty=pcsctn*cnqty+Double.parseDouble(cart.CART_COLUMN_QTY);
                model.setQty(String.valueOf((int)netqty));
                model.setCtnQty(cart.CART_COLUMN_CTN_QTY);
                model.setPcsQty(cart.CART_COLUMN_QTY);
                model.setProductId(cart.CART_COLUMN_PID);
                model.setPcspercarton(cart.CART_PCS_PER_CARTON);
                model.setCartonprice(cart.CART_COLUMN_CTN_PRICE);
                model.setLooseprice(cart.CART_UNIT_PRICE);
                model.setFoc_qty(cart.getFoc_qty());
                model.setFoc_type(cart.getFoc_type());
                model.setExchange_qty(cart.getExchange_qty());
                model.setExchange_type(cart.getExchange_type());
                model.setDiscount(cart.getDiscount());
                model.setReturn_qty(cart.getReturn_qty());
                model.setReturn_type(cart.getReturn_type());
                model.setUom_code(cart.getUomCode());
                model.setMinimumSellingPrice(cart.getMinimumSellingPrice());

              /*  model.setProductName(cart.getCART_COLUMN_PNAME());
                model.setDescription("description");
                model.setNetAmount(cart.getCART_TOTAL_VALUE());
                double pcsctn=Double.parseDouble(cart.getCART_PCS_PER_CARTON());
                double cnqty=Double.parseDouble(cart.getCART_COLUMN_CTN_QTY());
                double netqty=pcsctn*cnqty+Double.parseDouble(cart.getCART_COLUMN_QTY());
                model.setQty(String.valueOf((int)netqty));
                model.setCtnQty(cart.getCART_COLUMN_CTN_QTY());
                model.setPcsQty(cart.getCART_COLUMN_QTY());
                model.setProductId(cart.getCART_COLUMN_PID());
                model.setPcspercarton(cart.getCART_PCS_PER_CARTON());
                model.setCartonprice(cart.getCART_COLUMN_NET_PRICE());
                model.setLooseprice(cart.getCART_UNIT_PRICE());
                model.setFoc_qty(cart.getFoc_qty());
                model.setFoc_type(cart.getFoc_type());
                model.setExchange_qty(cart.getExchange_qty());
                model.setExchange_type(cart.getExchange_type());
                model.setDiscount(cart.getDiscount());
                model.setReturn_qty(cart.getReturn_qty());
                model.setReturn_type(cart.getReturn_type());
                model.setUom_code(cart.getUomCode());
*/

                productSummaryList.add(model);
            }
            itemCount.setText("Items ( "+localCart.size()+" )");
            productSummaryView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            productSummaryAdapter=new ProductSummaryAdapter(productSummaryList, new ProductSummaryAdapter.CallBack() {
                @Override
                public void searchCustomer(String letter, int pos) {
                }
                @Override
                public void removeItem(String pid) {
                    showRemoveItemAlert(pid);
                }
                @Override
                public void editItem(int pos,String pid,String cqty, String lqty, String netQty, String productname, String ctnprice, String lprice,String pcspercarton,ProductSummaryModel model) {
                    salesReturn.setChecked(false);
                    salesReturn.setEnabled(false);
                    productId=pid;
                    productName=productname;
                    cartonPrice.setEnabled(true);
                    loosePrice.setEnabled(true);
                    cartonQtyValue.setText("");
                    qtyValue.setText("");
                    looseQtyValue.setText("");
                    qtyValue.requestFocus();

                    //  looseQtyValue.setEnabled(true);

                    double pcsctn=Double.parseDouble(model.getPcspercarton());
                    double ctnqty=Double.parseDouble(model.getCtnQty());
                    double looseqty=Double.parseDouble(model.getPcsQty());
                    double netqty=(ctnqty * pcsctn)+looseqty;

                    if (model.getMinimumSellingPrice()!=null && !model.getMinimumSellingPrice().isEmpty()){
                        minimumSellingPriceText.setText(model.getMinimumSellingPrice());
                    }else {
                        minimumSellingPriceText.setText("0.00");
                    }
                    cartonQtyValue.removeTextChangedListener(cqtyTW);
                    cartonQtyValue.setText(Utils.getQtyValue(String.valueOf(ctnqty)));
                    cartonQtyValue.addTextChangedListener(cqtyTW);

                    looseQtyValue.removeTextChangedListener(lqtyTW);
                    looseQtyValue.setText(Utils.getQtyValue(String.valueOf(looseqty)));
                    looseQtyValue.addTextChangedListener(lqtyTW);

                    qtyValue.removeTextChangedListener(qtyTW);
                    qtyValue.setText(Utils.getQtyValue(String.valueOf(netqty)));
                    qtyValue.addTextChangedListener(qtyTW);

                    productAutoComplete.setText(productname+"-"+pid);
                    uomText.setText(model.getUom_code());

                    if (Double.parseDouble(model.getPcspercarton())>1){
                        cartonQtyValue.setEnabled(true);
                        loosePrice.setEnabled(true);
                        looseQtyValue.setEnabled(true);
                    }else {
                        cartonQtyValue.setEnabled(false);
                        loosePrice.setEnabled(true);
                        looseQtyValue.setEnabled(false);
                    }

                    double lpricevalue=Double.parseDouble(lprice);
                    double cpricevalue=Double.parseDouble(ctnprice);
                    double price=lpricevalue / netqty;

                    loosePrice.setText(ctnprice);
                    cartonPrice.setText(ctnprice);
                    pcsPerCarton.setText(pcspercarton);

                    addProduct.setText("Update");
                    qtyValue.requestFocus();
                    qtyValue.setSelectAllOnFocus(true);
                    qtyValue.setSelection(qtyValue.getText().length());
                    qtyValue.setEnabled(true);
                    //  cartonQtyValue.setEnabled(true);
                    if (model.getFoc_type().equals("ctn")){
                        focSwitch.setChecked(true);
                    }else {
                        focSwitch.setChecked(false);
                    }
                    if (model.getExchange_type().equals("ctn")){
                        exchangeSwitch.setChecked(true);
                    }else {
                        exchangeSwitch.setChecked(false);
                    }
                    if (model.getReturn_type().equals("ctn")){
                        returnSwitch.setChecked(true);
                    }else {
                        returnSwitch.setChecked(false);
                    }

                    if (model.getFoc_qty()!=null && !model.getFoc_qty().isEmpty() && !model.getFoc_qty().equals("null")){
                        focEditText.setText(model.getFoc_qty());
                    }else {
                        focEditText.setText("0");
                    }
                    if (model.getExchange_qty()!=null && !model.getExchange_qty().isEmpty() && !model.getExchange_qty().equals("null")){
                        exchangeEditext.setText(model.getExchange_qty());
                    }else {
                        exchangeEditext.setText("0");
                    }

                    if (model.getDiscount()!=null && !model.getDiscount().isEmpty() && !model.getDiscount().equals("null")){
                        discountEditext.setText(model.getDiscount());
                    }else {
                        discountEditext.setText("0");
                    }

                    if (model.getReturn_qty()!=null && !model.getReturn_qty().isEmpty() && !model.getReturn_qty().equals("null")){
                        returnEditext.setText(model.getReturn_qty());
                    }else {
                        returnEditext.setText("0");
                    }

                    focEditText.setEnabled(true);
                    exchangeEditext.setEnabled(true);
                    discountEditext.setEnabled(true);
                    returnEditext.setEnabled(true);

                    stockLayout.setVisibility(View.VISIBLE);


                    for (ProductsModel m:productList) {
                        if (m.getProductCode().equals(productId)) {
                            if (m.getStockQty()!=null && !m.getStockQty().equals("null")){
                                if (Double.parseDouble(m.getStockQty()) == 0 || Double.parseDouble(m.getStockQty()) < 0) {
                                    stockQtyValue.setText(m.getStockQty());
                                    stockQtyValue.setTextColor(Color.parseColor("#D24848"));
                                    stockCount.setText(m.getStockQty());
                                } else if (Double.parseDouble(m.getStockQty()) > 0) {
                                    stockQtyValue.setTextColor(Color.parseColor("#2ECC71"));
                                    stockQtyValue.setText(m.getStockQty());
                                    stockCount.setText(m.getStockQty());
                                }
                            }
                        }
                    }
                    // setCalculation();
                    setCalculationView();
                }
            });
            productSummaryView.setAdapter(productSummaryAdapter);
            noproductText.setVisibility(View.GONE);
            productSummaryView.setVisibility(View.VISIBLE);
            itemCount.setVisibility(View.VISIBLE);
        }else {
            itemCount.setVisibility(View.GONE);
            noproductText.setVisibility(View.VISIBLE);
            productSummaryView.setVisibility(View.GONE);
        }
        Utils.refreshActionBarMenu(requireActivity());
        hideKeyboard();
        setSummaryTotal();
    }

    /*public void updateProduct(int value){
        int insertIndex = value;
        data.addAll(insertIndex, items);
        adapter.notifyItemRangeInserted(insertIndex, items.size());
    }*/

    public void showRemoveItemAlert(final String pid){

        try {
            new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                    // .setTitleText("Are you sure?")
                    .setContentText("Are you sure want to remove this item ?")
                    .setConfirmText("YES")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            dbHelper.deleteProduct(pid);
                            clearFields();
                            sDialog.dismissWithAnimation();
                            getProducts();
                            setSummaryTotal();
                            Utils.refreshActionBarMenu(requireActivity());
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setSummaryTotal();

            // Refresh your fragment here
            //setCalculation();
            // dbHelper=new DBHelper(getActivity());
            // if (dbHelper.getAllCartItems()!=null){
            //     getProducts();
            /// }
            assert getFragmentManager() != null;
            // setSummaryTotal();
            try {
                int count=dbHelper.numberOfRows();
                if (count>0){
                    getProducts();
                }else {
                    getFragmentManager().beginTransaction().detach(this).attach(this).commit();
                }
            }catch (Exception ex){

            }
        }
        if (getView() != null && isVisibleToUser) {


            isViewShown = true;
            dbHelper=new DBHelper(getActivity());
            //ArrayList<ProductsModel> productList=dbHelper.getAllProducts();
            ArrayList<ProductsModel> productList=AppUtils.getProductsList();
            if (productList!=null &&  productList.size()>0){
                Log.w("GetValues:","Success");
                setAdapter(productList);
            }else {
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("CompanyCode",companyCode);
                    jsonObject.put("LocationCode",locationCode);
                    //jsonObject.put("CategoryCode",catagoriesId);
                    jsonObject.put("PageSize",5000);
                    jsonObject.put("PageNo","1");
                    getAllProducts(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            isViewShown = false;
        }
    }

    public void getProductPrice(String productId) throws JSONException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyCode);
        jsonObject.put("CustomerCode",selectCustomerId);
        jsonObject.put("LocationCode",locationCode);
        jsonObject.put("ProductCode",productId);
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        //http://223.25.81.205:100//es/data/api/ProductApi/GetCustomerProductPrice?Requestdata={"CompanyCode":"10","CustomerCode":"TEST B ","LocationCode":"HQ","ProductCode":"ALU001"}
        String url=Utils.getBaseUrl(getContext()) +"ProductApi/GetCustomerProductPrice?Requestdata="+jsonObject.toString();
        // String url="http://223.25.81.205:100/es/data/api/ProductApi/GetCustomerProductPrice?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_product_url:",url);
        dialog.setMessage("Getting Product price...");
        dialog.setCancelable(false);
        dialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                      /*  [
                        {
                            "InvoiceNo": null,
                                "CustomerCode": null,
                                "PcsPerCarton": 1,
                                "RetailPrice": null,
                                "Companycode": null,
                                "Price": 18.00000,
                                "CartonPrice": 18.0000,
                                "NewPrice": null,
                                "NewCartonPrice": null,
                                "MinimumStock": null,
                                "FPrice": 18.00000,
                                "FCartonPrice": 0.0000
                        }
]*/                   Log.w("ProductPriceResponse::",response.toString());
                        JSONArray jsonArray=new JSONArray(response.toString());
                        for (int i =0;i<jsonArray.length();i++){
                            JSONObject object=jsonArray.optJSONObject(i);
                            loosePrice.setText(object.optString("Price"));
                            cartonPrice.setText(object.optString("CartonPrice"));
                        }
                        dialog.dismiss();
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

    public void getAllProducts(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url=Utils.getBaseUrl(getContext()) +"ProductList";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_pdttUrl:",url);
        productList=new ArrayList<>();
        products=new ArrayList<>();
        dialog=new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setMessage("Getting Products List...");
        dialog.show();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        dialog.dismiss();
                        //  {"productCode":"FG\/001245","productName":"RUM","companyName":"","supplierCategoryNumber":"","uomCode":"Ctn",
                        //  "uomName":"Carton","cartonPrice":"3000.000000","piecePrice":"0.000000","pcsPerCarton":"100.000000",
                        //  "price":"100.000000","taxType":"E","havTax":"Y","taxCode":"SR","taxRate":"7.000000","barCode":"",
                        //  "isActive":"N","createUser":"1","createDate":"13\/07\/2021","modifyDate":"29\/07\/2021","remarks":"",
                        //  "warehouse":"01","stockInHand":"0.000000","averagePrice":0,"manageBatchOrSerial":"None","manageBatchNumber":"N",
                        //  "manageSerialNumber":"N","batchNumber":"","expiryDate":null,"manufactureDate":"31\/07\/2021","imageURL":""}
                        Log.w("Response_SAP_PRODUCTS:",response.toString());
                        // Loop through the array elements
                        JSONArray productArray=response.optJSONArray("responseData");
                        for(int i=0;i<productArray.length();i++){
                            // Get current json object
                            JSONObject productObject = productArray.getJSONObject(i);
                            ProductsModel product =new ProductsModel();
                            if (productObject.optString("isActive").equals("N")) {
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
                                if (!productObject.optString("stockInHand").equals("null")){
                                    product.setStockQty(productObject.optString("stockInHand"));
                                }else {
                                    product.setStockQty("0");
                                }

                                product.setUomCode(productObject.optString("uomCode"));
                                //  product.setProductBarcode(productObject.optString("BarCode")); Add values In Futue
                                product.setProductBarcode("");
                                productList.add(product);
                            }
                        }
                        HomePageModel.productsList=new ArrayList<>();
                        HomePageModel.productsList.addAll(productList);
                        setAdapter(productList);
                        HomePageModel.productsList.addAll(productList);
                        if (productList.size()>0){
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtils.setProductsList(productList);
                                    dialog.dismiss();
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

  /*  public void getAllProducts(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        String url=Utils.getBaseUrl(getContext()) +"ProductApi/GetProduct_All_ByFacets?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_product_url:",url);
        productList=new ArrayList<>();
        products=new ArrayList<>();
        dialog.setMessage("Getting Products List...");
        dialog.setCancelable(false);
        dialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Response_is:",response.toString());
                        // Loop through the array elements
                        for(int i=0;i<response.length();i++){
                            // Get current json object
                            JSONObject productObject = response.getJSONObject(i);
                            ProductsModel product =new ProductsModel();
                            if (productObject.optBoolean("IsActive")) {
                                product.setCompanyCode(productObject.optString("CompanyCode"));
                                product.setProductName(productObject.optString("ProductName"));
                                product.setProductCode(productObject.optString("ProductCode"));
                                product.setWeight(productObject.optString("Weight"));
                                product.setProductImage(productObject.optString("ProductImagePath"));
                                product.setWholeSalePrice(productObject.optString("WholeSalePrice"));
                                product.setRetailPrice(productObject.optDouble("RetailPrice"));
                                product.setCartonPrice(productObject.optString("CartonPrice"));
                                product.setPcsPerCarton(productObject.optString("PcsPerCarton"));
                                product.setUnitCost(productObject.optString("UnitCost"));
                                if (!productObject.optString("StockQty").equals("null")){
                                    product.setStockQty(productObject.optString("StockQty"));
                                }else {
                                    product.setStockQty("0");
                                }

                                product.setUomCode(productObject.optString("UOMCode"));
                                //  product.setProductBarcode(productObject.optString("BarCode")); Add values In Futue
                                product.setProductBarcode("");

                                products.add(productObject.optString("ProductName") + " - " + productObject.optString("ProductCode"));
                                productList.add(product);
                            }
                        }
                        HomePageModel.productsList=new ArrayList<>();
                        HomePageModel.productsList.addAll(productList);
                        setAdapter(productList);
                        if (productList.size()>0){
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtils.setProductsList(productList);
                                    dialog.dismiss();
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
    }*/

    public static void closeDialog(){
        if (dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
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

        productListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        selectProductAdapter=new SelectProductAdapter(getActivity(),filteredProducts, new SelectProductAdapter.CallBack() {
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
                cartonPrice.setText(model.getUnitCost()+"");
                loosePrice.setText(model.getUnitCost());
                uomText.setText(model.getUomCode());
                stockCount.setText(model.getStockQty());
                pcsPerCarton.setText(model.getPcsPerCarton());
                qtyValue.requestFocus();
                cartonPrice.setEnabled(true);
                // looseQtyValue.setEnabled(true);
                qtyValue.setText("");
                focEditText.setEnabled(true);
                exchangeEditext.setEnabled(true);
                discountEditext.setEnabled(true);
                returnEditext.setEnabled(true);

                if (model.getMinimumSellingPrice()!=null && !model.getMinimumSellingPrice().isEmpty()){
                    minimumSellingPriceText.setText(model.getMinimumSellingPrice());
                }else {
                    minimumSellingPriceText.setText("0.00");
                }

                if (Double.parseDouble(model.getPcsPerCarton())>1){
                    cartonQtyValue.setEnabled(true);
                    loosePrice.setEnabled(true);
                    looseQtyValue.setEnabled(true);
                    qtyValue.setEnabled(true);
                }else {
                    cartonQtyValue.setEnabled(false);
                    loosePrice.setEnabled(true);
                    looseQtyValue.setEnabled(false);
                    qtyValue.setEnabled(true);
                }
                cartonQtyValue.setText("");
                looseQtyValue.setText("");
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
                products.add(product.getProductName()+" - "+product.getProductCode());
            }
        }

        productListView.setAdapter(selectProductAdapter);
        autoCompleteAdapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.select_dialog_item, products){
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_CODE) {
                String barcodeText=data.getExtras().getString("Contents");
                ProductsModel model=getProductWithBarcode(barcodeText);
                if (model!=null){
                    productAutoComplete.setText(model.getProductName()+" - "+model.getProductCode());
                    cartonPrice.setText(model.getUnitCost()+"");
                    loosePrice.setText(model.getUnitCost());
                    uomText.setText(model.getUomCode());
                    stockCount.setText(model.getStockQty());
                    pcsPerCarton.setText(model.getPcsPerCarton());
                    qtyValue.requestFocus();
                    looseQtyValue.setEnabled(true);
                    qtyValue.setEnabled(true);
                    cartonQtyValue.setEnabled(true);
                    cartonPrice.setEnabled(true);
                    loosePrice.setEnabled(true);
                }else {
                    Toast.makeText(getActivity(),"No Such Product",Toast.LENGTH_SHORT).show();
                }
                /*if (product.size()>0){
                    for (ProductsModel model:product){
                        productAutoComplete.setText(model.getProductName()+" - "+model.getProductCode());
                        cartonPrice.setText(model.getUnitCost()+"");
                        loosePrice.setText(model.getUnitCost());
                        uomText.setText(model.getUomCode());
                        stockCount.setText(model.getStockQty());
                        pcsPerCarton.setText(model.getPcsPerCarton());
                        qtyValue.requestFocus();
                        looseQtyValue.setEnabled(true);
                        qtyValue.setEnabled(true);
                        cartonQtyValue.setEnabled(true);
                        cartonPrice.setEnabled(true);
                        loosePrice.setEnabled(true);
                    }
                }else {
                    Toast.makeText(getActivity(),"No Such Products",Toast.LENGTH_SHORT).show();
                }*/
            }
        }
    }

    public ProductsModel getProductWithBarcode(String barcode){
        ProductsModel productsModel=null;
        if (AppUtils.getProductsList().size()>0){
            for (ProductsModel model:AppUtils.getProductsList()){
                if (model.getProductBarcode().contains(barcode)){
                    productsModel=model;
                    break;
                }
            }
        }else {
            Toast.makeText(getActivity(),"Product List Empty..!",Toast.LENGTH_SHORT).show();
        }
        return productsModel;
    }

    public void showAlert(){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("Daiya", "ORIENTATION_LANDSCAPE");
            ProductsFragment.closeSheet();

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("Daiya", "ORIENTATION_PORTRAIT");
            ProductsFragment.closeSheet();
        }
    }

    public void netTotalCalculation(double total,double billDiscountAmount,double itemDiscountAmount) {
        try {
            double taxAmount = 0.0, netTotal = 0.0;
            double taxAmount1 = 0.0, netTotal1 = 0.0;
            double return_qty=0.0;
            double pcspercarton=0.0;
            customerDetails=dbHelper.getCustomer(selectCustomerId);
            String taxValue =customerDetails.get(0).getTaxPerc();
            String taxType=customerDetails.get(0).getTaxType();
            Log.w("TaxType:",taxType);
            Log.w("TaxValue:", taxValue);

            double tt=total;
            String Prodtotal = twoDecimalPoint(tt);

            double subTotal = 0.0;

            String itemDisc = String.valueOf(itemDiscountAmount);
            if (!itemDisc.matches("")) {
                double itmDisc = Double.parseDouble(itemDisc);
                subTotal = tt - itmDisc;
            } else {
                subTotal = tt;
            }
            String sbTtl = twoDecimalPoint(subTotal);

            if (billDiscountAmount>0){
                subTotal=subTotal-billDiscountAmount;
            }

            tt=subTotal;

            if (!taxType.matches("") && !taxValue.matches("")) {

                double taxValueCalc = Double.parseDouble(taxValue);

                if (taxType.matches("E")) {

                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subTotal * taxValueCalc) / 100;
                        String prodTax = twoDecimalPoint(taxAmount1);
                        taxValueText.setText("" + prodTax);

                        netTotal1 = subTotal + taxAmount1;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        netTotalValue.setText("" + ProdNetTotal);
                        taxTitle.setText("GST ( Exc )");
                        subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                    } else {

                        taxAmount = (tt * taxValueCalc) / 100;
                        String prodTax = twoDecimalPoint(taxAmount);
                        taxValueText.setText("" + prodTax);

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        taxTitle.setText("GST ( Exc )");
                        netTotalValue.setText("" + ProdNetTotal);
                        subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                    }

                } else if (taxType.matches("I")) {
                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subTotal * taxValueCalc)
                                / (100 + taxValueCalc);
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
                    } else {
                        taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = twoDecimalPoint(taxAmount);
                        taxValueText.setText("" + prodTax);

                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        netTotalValue.setText("" + ProdNetTotal);


                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        //  sl_total_inclusive.setText(totalIncl);
                        double sub_total=subTotal-taxAmount;
                        taxTitle.setText("GST ( Inc )");
                        subTotalValue.setText(Utils.twoDecimalPoint(sub_total));
                    }

                } else if (taxType.matches("Z")) {

                    taxValueText.setText("0.0");
                    if (!itemDisc.matches("")) {
                        // netTotal1 = subTotal + taxAmount;
                        netTotal1 = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        netTotalValue.setText("" + ProdNetTotal);
                        subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                        taxTitle.setText("GST ( Zero )");
                    } else {
                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        netTotalValue.setText("" + ProdNetTotal);
                        subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
                        taxTitle.setText("GST ( Zero )");
                    }

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


    // slide the view from below itself to the current position
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),          // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        // animate.setFillAfter(true);
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

    public void getBarcodeValues(String scannedBarcode){
        ProductsModel model=getScannedProduct(scannedBarcode);
        if (model!=null){
            barcodeText.setText("");
            barcodeText.requestFocus();
            setBarcodeProducts(model);
        }else {
            Toast.makeText(getActivity(),"No Product Found..!",Toast.LENGTH_SHORT).show();
        }
    }

    public void setBarcodeProducts(ProductsModel model){
        if (model!=null){
            productAutoComplete.setText(model.getProductName()+" - "+model.getProductCode());
            cartonPrice.setText(model.getUnitCost()+"");
            loosePrice.setText(model.getUnitCost());
            uomText.setText(model.getUomCode());
            stockCount.setText(model.getStockQty());
            pcsPerCarton.setText(model.getPcsPerCarton());
            qtyValue.requestFocus();
            looseQtyValue.setEnabled(true);
            qtyValue.setEnabled(true);
            cartonQtyValue.setEnabled(true);
            cartonPrice.setEnabled(true);
            loosePrice.setEnabled(true);
        }else {
            Toast.makeText(getActivity(),"No Such Product",Toast.LENGTH_SHORT).show();
        }
    }

    public ProductsModel getScannedProduct(String scannedBarcode){
        ProductsModel productsModel=null;
        if (AppUtils.getProductsList().size()>0){
            for (ProductsModel model:AppUtils.getProductsList()){
                if (model.getProductBarcode().contains(scannedBarcode)|| model.getProductBarcode().equals(scannedBarcode)){
                    productsModel= model;
                    break;
                }
            }
        }else {
            Toast.makeText(getActivity(),"No Product to Scan...!",Toast.LENGTH_SHORT).show();
        }
        return productsModel ;
    }

}