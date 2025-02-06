package com.winapp.svcatering.salesreturn;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
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
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.winapp.svcatering.adapter.SelectProductAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.AppUtils;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.HomePageModel;
import com.winapp.svcatering.model.ItemGroupList;
import com.winapp.svcatering.model.ProductSummaryModel;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.model.StockAdjustmentModel;
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
import static com.winapp.svcatering.utils.Utils.fourDecimalPoint;
import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

public class SalesReturnProduct extends Fragment {

    LinearLayout returnLayout;
    ImageView showHideButton;
    private SalesReturnSummaryAdapter productSummaryAdapter;
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
    BottomSheetBehavior behavior;
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
    EditText stockAdjustmentText;

    String[] stockAdjustmentList;
    ArrayList<StockAdjustmentModel> stockAdjustList;
    public static String stockAdjustRefCode="0";
    public RecyclerView adjustmentRecyclerView;
    public View productLayout;
    public View adjustmentLayout;
    public Button cancelOption;
    public int editItemPosition;
    FloatingActionButton sortButton;
    LinearLayout emptyLayout;

    boolean isReverseCalculationEnabled=true;
    boolean isCartonPriceEdit=true;
    boolean isPriceEdit=true;
    public String selectCustomerId;
    static ProgressDialog dialog;
    public String returnType="N";
    public String username;
    ArrayList<ItemGroupList> itemGroup;
    Spinner groupspinner;
    public TextView netReturnQty;
    public EditText expiryReturnQty;
    public EditText damageReturnQty;
    public TextWatcher expiryQtyTextWatcher;
    public TextWatcher damageQtyTextWatcher;
    public Button cancelReturn;
    public Button saveReturn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_sales_return_product, container, false);
        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        dbHelper=new DBHelper(getActivity());
        username=user.get(SessionManager.KEY_USER_NAME);
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
        groupspinner=view.findViewById(R.id.spinner_group);
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
        adjustmentRecyclerView=view.findViewById(R.id.adjustListView);
        productLayout=view.findViewById(R.id.product_layout);
        adjustmentLayout=view.findViewById(R.id.adjustment_layout);
        cancelOption=view.findViewById(R.id.btn_cancel);
        sortButton=view.findViewById(R.id.fab);
        emptyLayout=view.findViewById(R.id.empty_layout);
        cancelReturn=view.findViewById(R.id.cancel_return);
        saveReturn=view.findViewById(R.id.save_return);
        netReturnQty=view.findViewById(R.id.net_return_qty);
        expiryReturnQty=view.findViewById(R.id.saleable_return_qty);
        damageReturnQty=view.findViewById(R.id.damage_qty);
        products=new ArrayList<>();
        productAutoComplete.clearFocus();
        stockAdjustmentText =view.findViewById(R.id.select_stock_adjusment);
        saveReturn.setVisibility(View.GONE);
        cancelReturn.setVisibility(View.GONE);

        loosePrice.setSelectAllOnFocus(true);
        cartonPrice.setSelectAllOnFocus(true);

        sortButton.setVisibility(View.GONE);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
        selectCustomerId = sharedPreferences.getString("customerId", "");
        customerDetails=dbHelper.getCustomer(selectCustomerId);

        getProducts();

        JSONObject object=new JSONObject();
        try {
            object.put("CompanyCode",companyCode);
           // getAllAdjustment(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        productList=new ArrayList<>();
      //  productList= dbHelper.getAllProducts();
        productList= AppUtils.getProductsList();
        if (productList!=null && productList.size()>0){
            setAdapter(productList);
        }else {
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("User",username);
                jsonObject.put("CardCode",selectCustomerId);
                getAllProducts(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            getGrouplist();
        } catch (JSONException e) {
            e.printStackTrace();
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

        cancelOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        searchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productLayout.setVisibility(View.VISIBLE);
                adjustmentLayout.setVisibility(View.GONE);
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
                String cusname=editable.toString();
                if (!cusname.isEmpty()){
                    filter(cusname);
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
                productTotalNew();
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
                // setCalculation();
                productTotalNew();
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
                    productTotalNew();
                }else {
                    productTotalNew();
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
                    productTotalNew();
                }else {
                    productTotalNew();
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
                            clQty();
                            //calculateQty();
                        }
                        // Add the method to calculate values
                        productTotalNew();
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String qty = qtyValue.getText().toString();

                double stock=0.0;

                if (!qty.toString().isEmpty()){
                    expiryReturnQty.setText(qty.toString());
                }else {
                    expiryReturnQty.setText("");
                }

                if (!qty.matches("")) {

                    if (!stockQtyValue.getText().toString().isEmpty()){
                        stock=Double.parseDouble(stockQtyValue.getText().toString());
                    }

                      clQty();
                      productTotalNew();
                    if (stock < Double.parseDouble(qty)){
                        // Toast.makeText(getActivity(),"Stock is Low please check",Toast.LENGTH_SHORT).show();
                        //  qtyValue.setText("");
                    }else {
                        double qtyCalc = Double.parseDouble(qty);
                        if (qtyValue.getText().toString().matches("0")) {

                        } else {
                            clQty();
                            //  calculateQty();
                        }
                        productTotalNew();
                    }
                }

                int length = qtyValue.getText().length();
                if (length == 0) {

                    if (qtyValue.getText().toString().matches("0")) {

                    } else {

                        productTotalNew();

                        cartonQtyValue.removeTextChangedListener(cqtyTW);
                        cartonQtyValue.setText("");
                        cartonQtyValue.addTextChangedListener(cqtyTW);

                        looseQtyValue.removeTextChangedListener(lqtyTW);
                        looseQtyValue.setText("");
                        looseQtyValue.addTextChangedListener(lqtyTW);

                    }
                    productTotalNew();
                }
                //setButtonView();
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cartonQty();
            }
        };
        cartonQtyValue.addTextChangedListener(cqtyTW);


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
                //setButtonView();
            }

        };
        looseQtyValue.addTextChangedListener(lqtyTW);

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=productAutoComplete.getText().toString();
                if (!qtyValue.getText().toString().isEmpty() && !s.isEmpty() && !qtyValue.getText().toString().equals("0") && !qtyValue.getText().toString().equals("00") && !qtyValue.getText().toString().equals("000") && !qtyValue.getText().toString().equals("0000") && !netTotalValue.getText().toString().equals("0.00")){
                    if (addProduct.getText().toString().equals("Update")){
                        // if (!cartonPrice.getText().toString().isEmpty() && !cartonPrice.getText().toString().equals("0.00") && !cartonPrice.getText().toString().equals("0.0") && !cartonPrice.getText().toString().equals("0")){
                       /* if (!stockAdjustmentText.getText().toString().equals("Select")){
                            updateProduct();
                        }else {
                            Toast.makeText(getActivity(),"Select Stock Adjustment",Toast.LENGTH_SHORT).show();
                        }*/

                        updateProduct();

                        //  }

                    }else {
                        addProduct("Add");
                       /* if (!stockAdjustmentText.getText().toString().equals("Select")){
                            addProduct("Add");
                        }else {
                            Toast.makeText(getActivity(),"Select Stock Adjustment",Toast.LENGTH_SHORT).show();
                        }*/
                    }
                }else {
                    showAlert();
                }
            }
        });

        productAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selectProduct = (String)parent.getItemAtPosition(position);
                String[] product = selectProduct.split("-", 0);
                productId=product[1].trim();
                productName=product[0].trim();
                Log.w("Selected_product",product[1]);
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
                productTotalNew();
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
                productTotalNew();
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
                productTotalNew();
            }

        });


        scanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), BarCodeScanner.class);
                startActivityForResult(intent,RESULT_CODE);
            }
        });

        // Onclick Listener in StockAdjustment

        stockAdjustmentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle("Choose an item");
                mBuilder.setSingleChoiceItems(stockAdjustmentList, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        stockAdjustmentText.setText(stockAdjustmentList[i]);
                        for (StockAdjustmentModel model:stockAdjustList){
                            if (model.getDescription().equals(stockAdjustmentText.getText().toString().trim())){
                                stockAdjustRefCode=model.getRefCode();
                            }
                        }
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();*/
              if (stockAdjustList.size()>0){
                  productLayout.setVisibility(View.GONE);
                  adjustmentLayout.setVisibility(View.VISIBLE);
                  viewCloseBottomSheet();
              }else {
                  Toast.makeText(getActivity(),"Stock Adjustment not found",Toast.LENGTH_SHORT).show();
              }
            }
        });

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

        expiryQtyTextWatcher =new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!qtyValue.getText().toString().isEmpty()){
                    String netreturnqty=qtyValue.getText().toString();
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
                            Toast.makeText(getContext(),"Return Qty Exceed...",Toast.LENGTH_SHORT).show();
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


        damageQtyTextWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!qtyValue.getText().toString().isEmpty()){
                    String netreturnqty=qtyValue.getText().toString();
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
                            Toast.makeText(getContext(),"Return Qty Exceed...",Toast.LENGTH_SHORT).show();
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

        return view;
    }

    public void setProductsDisplay(String action){
        try {
            // productsAdapterNew.setWithFooter(false);
            // sortAdapter.resetPosition();
            ArrayList<ProductsModel> filterdNames = new ArrayList<>();
            for (ProductsModel s : productList) {
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
            //calling a method of the adapter class and passing the filtered list
            if (filterdNames.size()>0){
                for (ProductsModel model:filterdNames){
                    Log.w("products_Option_values:",model.getProductName());
                }
                if (selectProductAdapter!=null){
                    selectProductAdapter.filterList(filterdNames);
                }
                selectProductAdapter.filterList(filterdNames);
                productLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
            }else {
                productLayout.setVisibility(View.GONE);
               // emptyLayout.setVisibility(View.VISIBLE);
            }
        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
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
                qtyValue.setText("");
                looseQtyValue.setText("");
                cartonQtyValue.setText("");
                productTotalNew();
            }
        }

    }

    public void productTotalNew() {
        try {
            double taxAmount = 0.0, netTotal = 0.0;
            double taxAmount1 = 0.0, netTotal1 = 0.0;
            double return_qty=0.0;
            double pcspercarton=0.0;

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
            String selectCustomerId = sharedPreferences.getString("customerId", "");
            if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
                customerDetails = dbHelper.getCustomer(selectCustomerId);
            }
            String taxValue=customerDetails.get(0).getTaxPerc();
            String taxType=customerDetails.get(0).getTaxType();

            Log.w("TaxType1:",taxType);
            Log.w("TaxValue1:",taxValue);

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

            if (!pcsPerCarton.getText().toString().isEmpty() && !pcsPerCarton.getText().toString().equals("null")){
                pcspercarton=Double.parseDouble(pcsPerCarton.getText().toString());
            }

            double lPriceCalc = Double.parseDouble(lPrice);
            double cPriceCalc = Double.parseDouble(cPrice);

            double cqtyCalc = Double.parseDouble(cqty);
            double lqtyCalc = Double.parseDouble(lqty);

            double tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc);

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

           // if (!returnEditext.getText().toString().isEmpty() && !returnEditext.getText().toString().equals("null")){
              //  return_qty=Double.parseDouble(returnEditext.getText().toString());
           // }

            if (!qtyValue.getText().toString().isEmpty() && !qtyValue.getText().toString().equals("null")){
                return_qty=Double.parseDouble(qtyValue.getText().toString());
                netReturnQty.setText(String.valueOf(return_qty));
            }else {
                netReturnQty.setText("0");
                expiryReturnQty.setText("");
                damageReturnQty.setText("");
            }



            subTotalValue.setText(Utils.twoDecimalPoint(subTotal));
            // sl_total_inclusive.setText("" + sbTtl);
            tt=subTotal;

            if (!taxType.matches("") && !taxValue.matches("")) {

                double taxValueCalc = Double.parseDouble(taxValue);

                if (taxType.matches("E")) {

                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subTotal * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount1);
                        taxValueText.setText("" + prodTax);

                        netTotal1 = subTotal + taxAmount1;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        netTotalValue.setText("" + ProdNetTotal);

                        Log.w("TaxValue-E-1:",prodTax);
                        Log.w("NetTotal-E-1:",ProdNetTotal);
                    } else {

                        taxAmount = (tt * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);
                        taxValueText.setText("" + prodTax);

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        netTotalValue.setText("" + ProdNetTotal);

                        Log.w("TaxValue-E-2:",prodTax);
                        Log.w("NetTotal-E-2:",ProdNetTotal);
                    }

                } else if (taxType.matches("I")) {
                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subTotal * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount1);
                        taxValueText.setText("" + prodTax);

                        // netTotal1 = subTotal + taxAmount1;
                        netTotal1 = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        netTotalValue.setText("" + ProdNetTotal);

                        double dTotalIncl = netTotal1 - taxAmount1;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.w("TaxValue-I-1:",prodTax);
                        Log.w("NetTotal-I-1:",ProdNetTotal);
                    } else {
                        taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        taxValueText.setText("" + prodTax);

                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        netTotalValue.setText("" + ProdNetTotal);


                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.w("TaxValue-I-2:",prodTax);
                        Log.w("NetTotal-I-2:",ProdNetTotal);
                    }

                } else if (taxType.matches("Z")) {

                    taxValueText.setText("0.0");
                    if (!itemDisc.matches("")) {
                        // netTotal1 = subTotal + taxAmount;
                        netTotal1 = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        netTotalValue.setText("" + ProdNetTotal);
                        Log.w("TaxValue-Z-1:","0.0");
                        Log.w("NetTotal-Z-1:",ProdNetTotal);
                    } else {
                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        netTotalValue.setText("" + ProdNetTotal);

                        Log.w("TaxValue-Z-2:","0.0");
                        Log.w("NetTotal-Z-2:",ProdNetTotal);
                    }

                } else {
                    taxValueText.setText("0.0");
                    netTotalValue.setText("" + Prodtotal);
                }

            } else if (taxValue.matches("")) {
                taxValueText.setText("0.0");
                netTotalValue.setText("" + Prodtotal);
            } else {
                taxValueText.setText("0.0");
                netTotalValue.setText("" + Prodtotal);
            }
            setButtonView();
        } catch (Exception e) {
            Log.w("Error_in_Calculating:",e.getMessage());
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

            // productTotal(qty);
            productTotalNew();
        }
    }

    public void clQty() {
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
                            productTotalNew();
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

    public void setButtonView(){
        if (!netTotalValue.getText().equals("0.00")){
            addProduct.setAlpha(0.9F);
            addProduct.setEnabled(true);
        }else {
            addProduct.setAlpha(0.4F);
            addProduct.setEnabled(false);
        }
    }

    private void looseQtyCalcNew() {
        try {
            String lqty=looseQtyValue.getText().toString();
            String ctnQty=cartonQtyValue.getText().toString();
            String pcspercarton=pcsPerCarton.getText().toString();
            if (lqty.isEmpty()){
                lqty="";
            }
            if (ctnQty.isEmpty()){
                cartonQtyValue.removeTextChangedListener(cartonTextWatcher);
                qtyValue.removeTextChangedListener(qtyTextWatcher);
                qtyValue.setText(""+lqty);
                qtyValue.addTextChangedListener(qtyTextWatcher);
                //setCalculation();
            }
            if (!ctnQty.isEmpty() && !pcspercarton.isEmpty()){
                if (lqty.isEmpty()){
                    lqty="0";
                }
                double pcsctn=Double.parseDouble(pcspercarton);
                double ctn=Double.parseDouble(ctnQty);
                double netqty=pcsctn*ctn+Double.parseDouble(lqty);
                int value = (int)netqty;
                qtyValue.removeTextChangedListener(qtyTextWatcher);
                qtyValue.setText(""+value);
                qtyValue.addTextChangedListener(qtyTextWatcher);
                // setCalculation();
            }
        }catch (Exception ex){

        }
        if (qtyValue.getText().toString().isEmpty() && looseQtyValue.getText().toString().isEmpty() && cartonQtyValue.getText().toString().isEmpty()){
            netTotalValue.setText("0.00");
            taxValueText.setText("0.000");
            subTotalValue.setText("0.00");
        }
    }

    public void setProductResult(String productId){
        try {
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
                  //  stockLayout.setVisibility(View.VISIBLE);
                    productAutoComplete.clearFocus();
                    cartonPrice.setText(model.getRetailPrice() + "");
                    loosePrice.setText(model.getWholeSalePrice());
                    uomText.setText(model.getUomCode());
                    stockCount.setText(model.getStockQty());
                   // pcsPerCarton.setText(model.getPcsPerCarton());
                    pcsPerCarton.setText("1");
                    if (Double.parseDouble(model.getPcsPerCarton()) > 1) {
                        cartonQtyValue.setEnabled(true);
                        loosePrice.setEnabled(true);
                        looseQtyValue.setEnabled(true);
                    } else {
                        cartonQtyValue.setEnabled(false);
                        loosePrice.setEnabled(false);
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

                } else {
                    Log.w("Not_found", "Product");
                }
            }
        }catch (Exception ex){
        }
    }

    public void addProduct(String action){

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
            if (!qtyValue.getText().toString().isEmpty()){
                return_qty=qtyValue.getText().toString();
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
                    "N","",
                    String.valueOf(total),
                    stockQtyValue.getText().toString(),
                    uomText.getText().toString(),"0.00",stockQtyValue.getText().toString());

            // Adding Return Qty Table values
            if (Integer.parseInt(return_qty) > 0){
                dbHelper.updateReturnQty("Delete","0","Saleable Return",productId);
                dbHelper.updateReturnQty("Delete","0","Damaged/Expired",productId);
                dbHelper.insertReturnProduct(productId,productName,expiryReturnQty.getText().toString(),"Saleable Return");
                dbHelper.insertReturnProduct(productId,productName,damageReturnQty.getText().toString(),"Damaged/Expired");
            }


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
                cartonQtyValue.setEnabled(false);
                expiryReturnQty.setText("");
                netReturnQty.setText("");
                damageReturnQty.setText("");

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

    public void updateProduct(){
        String focType="pcs";
        String exchangeType="pcs";
        String returnTypeValue ="pcs";
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
            returnTypeValue ="ctn";
        }

        String ctn_qty="0";
        String loose_qty="0";
        if (!cartonQtyValue.getText().toString().isEmpty()){
            ctn_qty=cartonQtyValue.getText().toString();
        }

        if (!looseQtyValue.getText().toString().isEmpty()){
            loose_qty=looseQtyValue.getText().toString();
        }


        if (!qtyValue.getText().toString().isEmpty()){
            return_qty=qtyValue.getText().toString();
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

      //  double return_amt=(Double.parseDouble(return_qty)*Double.parseDouble(lPriceCalc));
        double total=(Double.parseDouble(ctn_qty) * Double.parseDouble(cartonprice)) + (Double.parseDouble(loose_qty) * Double.parseDouble(lPriceCalc));
        double sub_total=total-Double.parseDouble(discount);


        boolean status=dbHelper.insertCart(
                productId,
                productName,
                ctn_qty,
                loose_qty,
                subTotalValue.getText().toString(),"",
                netTotalValue.getText().toString(),"weight",
                cartonPrice.getText().toString(),
                loosePrice.getText().toString(),
                pcsPerCarton.getText().toString(),
                taxValueText.getText().toString(),
                String.valueOf(sub_total),
                customerDetails.get(0).getTaxType(),
                focEditText.getText().toString(),
                focType,
                exchangeEditext.getText().toString(),
                exchangeType,
                discountEditext.getText().toString(),
                returnEditext.getText().toString(),
                returnType,stockAdjustRefCode,String.valueOf(total),
                "",uomText.getText().toString(),"0.00","");

        // Adding Return Qty Table values
        if (Integer.parseInt(return_qty) > 0){
            dbHelper.updateReturnQty("Delete","0","Saleable Return",productId);
            dbHelper.updateReturnQty("Delete","0","Damaged/Expired",productId);
            dbHelper.insertReturnProduct(productId,productName,expiryReturnQty.getText().toString(),"Saleable Return");
            dbHelper.insertReturnProduct(productId,productName,damageReturnQty.getText().toString(),"Damaged/Expired");
        }

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
            getProducts();
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
            cartonQtyValue.setEnabled(false);
            expiryReturnQty.setText("");
            netReturnQty.setText("");
            damageReturnQty.setText("");
            Utils.refreshActionBarMenu(requireActivity());
            addProduct.setText("Add");
            stockAdjustmentText.setText("Select");
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
            qtyValue.setText(Utils.getQtyValue(String.valueOf(qty)));
            qtyValue.addTextChangedListener(qtyTW);

            if (qtyValue.length() != 0) {
                qtyValue.setSelection(qtyValue.length());
            }

            productTotalNew();
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
            productTotalNew();
        }
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

        productListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        selectProductAdapter=new SelectProductAdapter(getActivity(),productList, new SelectProductAdapter.CallBack() {
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
                productName=productsModel.getProductName();
                productAutoComplete.setText(model.getProductName()+" - "+model.getProductCode());
                cartonPrice.setText(model.getUnitCost()+"");
                loosePrice.setText(model.getUnitCost());
                uomText.setText(model.getUomCode());
                stockCount.setText(model.getStockQty());
               // pcsPerCarton.setText(model.getPcsPerCarton());
                pcsPerCarton.setText("1");
                qtyValue.setEnabled(true);
                qtyValue.setText("");
                qtyValue.requestFocus();
                openKeyborard(qtyValue);
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                cartonQtyValue.setEnabled(true);
                // looseQtyValue.setEnabled(true);
                cartonPrice.setEnabled(true);
                focEditText.setEnabled(true);
                exchangeEditext.setEnabled(true);
                discountEditext.setEnabled(true);
                returnEditext.setEnabled(true);
                model.setPcsPerCartion("1");
                if (model.getPcsPerCarton()!=null && !model.getPcsPerCarton().isEmpty() && !model.getPcsPerCarton().equals("null") && Double.parseDouble(model.getPcsPerCarton())>1){
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
       // totalProducts.setText(filteredProducts.size()+" Products");
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
        ArrayList<CartModel> localCart;
        localCart = dbHelper.getAllCartItems();
        double net_sub_total=0.0;
        double net_tax=0.0;
        double net_total=0.0;
        if (localCart.size()>0){
            for (CartModel model:localCart){
                if (!model.getCART_TOTAL_VALUE().isEmpty()){
                    net_sub_total+=Double.parseDouble(model.getCART_TOTAL_VALUE());
                }
                if (!model.getCART_TAX_VALUE().isEmpty()){
                    net_tax+=Double.parseDouble(model.getCART_TAX_VALUE());
                }
                if (!model.getCART_COLUMN_NET_PRICE().isEmpty()){
                    net_total+=Double.parseDouble(model.CART_COLUMN_NET_PRICE);
                }
            }
            subTotalValue.setText(Utils.twoDecimalPoint(net_sub_total));
            taxValueText.setText(Utils.fourDecimalPoint(net_tax));
            netTotalValue.setText(Utils.twoDecimalPoint(net_total));
        }
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
                model.setNetAmount(cart.CART_COLUMN_NET_PRICE);
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
                model.setTypeCheckValue(cart.getReturn_type());

                productSummaryList.add(model);
            }
            itemCount.setText("Items ( "+localCart.size()+" )");
            productSummaryView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            productSummaryAdapter=new SalesReturnSummaryAdapter(getContext(),productSummaryList, new SalesReturnSummaryAdapter.CallBack() {
                @Override
                public void searchCustomer(String letter, int pos) {

                }
                @Override
                public void removeItem(String pid) {
                    showRemoveItemAlert(pid);
                }

                @Override
                public void editItem(int pos,String pid,String cqty, String lqty, String netQty, String productname, String ctnprice, String lprice,String pcspercarton,ProductSummaryModel model) {
                    //  cartonQtyValue.removeTextChangedListener(cartonTextWatcher);
                    //    qtyValue.removeTextChangedListener(qtyTextWatcher);

                    // Edit Item Position assigned values
                /*    editItemPosition=pos;
                    for (StockAdjustmentModel m:stockAdjustList){
                        if (m.getRefCode().equals(SalesReturnList.stockAdjustRefNoList.get(editItemPosition))){
                            stockAdjustmentText.setText(m.getDescription());
                        }
                    }*/

                    productId=pid;
                    productName=productname;
                    cartonPrice.setEnabled(true);
                    loosePrice.setEnabled(true);
                    cartonQtyValue.setText("");
                    qtyValue.setText("");
                    looseQtyValue.setText("");
                    returnType=model.getReturn_type();

                    //  looseQtyValue.setEnabled(true);

                    double pcsctn=Double.parseDouble(model.getPcspercarton());
                    double ctnqty=Double.parseDouble(model.getCtnQty());
                    double looseqty=Double.parseDouble(model.getPcsQty());
                    double netqty=(ctnqty * pcsctn)+looseqty;

                    cartonQtyValue.setText(Utils.getQtyValue(String.valueOf(ctnqty)));
                    looseQtyValue.setText(Utils.getQtyValue(String.valueOf(looseqty)));
                    qtyValue.setText(Utils.getQtyValue(String.valueOf(netqty)));
                    productAutoComplete.setText(productname+"-"+pid);
                    if (Double.parseDouble(model.getPcspercarton())>1){
                        cartonQtyValue.setEnabled(true);
                        loosePrice.setEnabled(true);
                        looseQtyValue.setEnabled(true);
                    }else {
                        cartonQtyValue.setEnabled(false);
                        loosePrice.setEnabled(false);
                        looseQtyValue.setEnabled(false);
                    }
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

                    focEditText.setText(model.getFoc_qty());
                    exchangeEditext.setText(model.getExchange_qty());
                    discountEditext.setText(model.getDiscount());
                    returnEditext.setText(model.getReturn_qty());

                    //stockLayout.setVisibility(View.VISIBLE);

                    for (ProductsModel m:productList) {
                        if (m.getProductCode().equals(productId)) {
                            if (Double.parseDouble(m.getStockQty()) == 0 || Double.parseDouble(m.getStockQty()) < 0) {
                                stockQtyValue.setText(m.getStockQty());
                                stockQtyValue.setTextColor(Color.parseColor("#D24848"));
                            } else if (Double.parseDouble(m.getStockQty()) > 0) {
                                stockQtyValue.setTextColor(Color.parseColor("#2ECC71"));
                                stockQtyValue.setText(m.getStockQty());
                            }
                        }
                    }
                    // setCalculation();
                    productTotalNew();
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
        Utils.refreshActionBarMenu(getActivity());
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
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    // .setTitleText("Are you sure?")
                    .setContentText("Are you sure want to remove this item ?")
                    .setConfirmText("YES")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            dbHelper.deleteProduct(pid);
                            sDialog.dismissWithAnimation();
                            getProducts();
                            Utils.refreshActionBarMenu(getActivity());
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

    public void getAllProducts(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
      //  String url=Utils.getBaseUrl(getContext()) +"ProductList";
        String url= Utils.getBaseUrl(getContext()) +"CustomerProductList";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_SAP_PROUCT_URL:",url);
        productList=new ArrayList<>();
        products=new ArrayList<>();
        dialog=new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setMessage("Getting Products List...");
        dialog.show();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
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
                                product.setBarcode(productObject.optString("barcode"));
                                product.setRetailPrice(productObject.optDouble("retailPrice"));
                                product.setCartonPrice(productObject.optString("cartonPrice"));
                                product.setPcsPerCarton(productObject.optString("pcsPerCarton"));
                                product.setUnitCost(productObject.optString("price"));
                                product.setUnitCost(productObject.optString("itemAllowFOC"));

                                if (!productObject.optString("stockInHand").equals("null")){
                                    product.setStockQty(productObject.optString("stockInHand"));
                                }else {
                                    product.setStockQty("0");
                                }
                                if (productObject.optString("bP_CatalogNo") != null){
                                    product.setCustomerItemCode(productObject.optString("bP_CatalogNo"));
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

    @SuppressLint("SetTextI18n")
    private void setAdapter(ArrayList<ProductsModel> productList) {
        productListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        selectProductAdapter=new SelectProductAdapter(getActivity(),productList, new SelectProductAdapter.CallBack() {
            @Override
            public void searchProduct(ProductsModel model) {
                try {
                   // getProductPrice(model.getProductCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                productAutoComplete.setText(model.getProductName()+" - "+model.getProductCode());
                cartonPrice.setText(model.getRetailPrice()+"");
                loosePrice.setText(model.getWholeSalePrice());
                uomText.setText(model.getUomCode());
                stockCount.setText(model.getStockQty());
                //pcsPerCarton.setText(model.getPcsPerCarton());
                pcsPerCarton.setText("1");
                qtyValue.setText("");
                qtyValue.requestFocus();
                cartonPrice.setEnabled(true);
                // looseQtyValue.setEnabled(true);
                if (model.getPcsPerCarton()!=null && !model.getPcsPerCarton().isEmpty() && !model.getPcsPerCarton().equals("null") && Double.parseDouble(model.getPcsPerCarton())>1){
                    cartonQtyValue.setEnabled(true);
                    loosePrice.setEnabled(true);
                    looseQtyValue.setEnabled(true);
                }else {
                    cartonQtyValue.setEnabled(false);
                    loosePrice.setEnabled(false);
                    looseQtyValue.setEnabled(false);
                }
                cartonQtyValue.setText("");
                looseQtyValue.setText("");
               // stockLayout.setVisibility(View.VISIBLE);
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

        // products.add(productObject.optString("ProductName")+" - "+productObject.optString("ProductCode"));
        products=new ArrayList<>();
        for (ProductsModel product:productList){
            products.add(product.getProductName()+" - "+product.getProductCode());
        }
        productListView.setAdapter(selectProductAdapter);
       /* autoCompleteAdapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.select_dialog_item, products){
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(14);
            *//*    Typeface Type = getFont () ;  // custom method to get a font from "assets" folder
                ((TextView) v).setTypeface(Type);
                ((TextView) v).setTextColor(YourColor);*//*
                ((TextView) v) .setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
                return v;
            }
        };
        productAutoComplete.setThreshold(1);
        productAutoComplete.setAdapter(autoCompleteAdapter);*/
        totalProducts.setText(productList.size()+" Products");
    }



    private void filter(String text) {
        try {
            //new array list that will hold the filtered data
            ArrayList<ProductsModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (ProductsModel s : productList) {
                //if the existing elements contains the search input
                if (s.getProductName().toLowerCase().contains(text.toLowerCase()) || s.getProductCode().toLowerCase().contains(text.toLowerCase())) {
                    //adding the element to filtered list
                    filterdNames.add(s);
                }
            }
            //calling a method of the adapter class and passing the filtered list
            selectProductAdapter.filterList(filterdNames);

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
                ArrayList<ProductsModel> product=dbHelper.getProductFromBarcode(barcodeText);
                if (product.size()>0){
                    for (ProductsModel model:product){
                        productAutoComplete.setText(model.getProductName()+" - "+model.getProductCode());
                        cartonPrice.setText(model.getRetailPrice()+"");
                        loosePrice.setText(model.getWholeSalePrice());
                        uomText.setText(model.getUomCode());
                        stockCount.setText(model.getStockQty());
                        //pcsPerCarton.setText(model.getPcsPerCarton());
                        pcsPerCarton.setText("1");
                        qtyValue.requestFocus();
                        looseQtyValue.setEnabled(true);
                        qtyValue.setEnabled(true);
                        cartonQtyValue.setEnabled(true);
                        cartonPrice.setEnabled(true);
                        loosePrice.setEnabled(true);
                    }
                }else {
                    Toast.makeText(getActivity(),"No Such Products",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void getAllAdjustment(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url=Utils.getBaseUrl(getContext()) +"ProductApi/GetStockAdjustmentMaster?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_product_url:",url);
        stockAdjustList=new ArrayList<>();
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
                            JSONObject stockObject = response.getJSONObject(i);
                            if (!stockObject.optString("ErrorMessage").equals("No StockAdjustmentMaster data Found.")){
                                StockAdjustmentModel stockadjustment =new StockAdjustmentModel();
                                stockadjustment.setDescription(stockObject.optString("Description"));
                                stockadjustment.setRefCode(stockObject.optString("RefCode"));
                                stockAdjustList.add(stockadjustment);
                            }
                        }
                        if (stockAdjustList.size()>0){
                            stockAdjustmentList = new String[stockAdjustList.size()];
                            for(int j =0;j<stockAdjustList.size();j++){
                                stockAdjustmentList[j] = stockAdjustList.get(j).getDescription();
                            }
                            setAdjustmentAdapter();
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
                String creds = String.format("%s:%s","winapp","admin");
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


    private void setupGroup(ArrayList<ItemGroupList> itemGroupLists) {
        ArrayAdapter<ItemGroupList> myAdapter = new ArrayAdapter<ItemGroupList>(getContext(), android.R.layout.simple_list_item_1, itemGroupLists);
        groupspinner.setAdapter(myAdapter);
        groupspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject jsonObject=new JSONObject();
                try {
                    String itemCode=itemGroup.get(i).getGroupCode();
                    Log.e("selectspinn",""+ itemCode);
                    if (!itemCode.equals("Select Brand")){
                        jsonObject.put("User",username);
                        jsonObject.put("CardCode",selectCustomerId);
                        jsonObject.put("ItemGroupCode",itemCode);
                        getAllProducts(jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    private ArrayList<ItemGroupList> getGrouplist() throws JSONException {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url= Utils.getBaseUrl(getContext()) +"ItemGroupList";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url_group:",url);
        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading Groups...");
        pDialog.setCancelable(false);
        pDialog.show();
        itemGroup =new ArrayList<>();
        itemGroup.add(new ItemGroupList("Select Brand", "Select Brand"));
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

    public void setAdjustmentAdapter(){
        adjustmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        AdjustmentAdapter adjustmentAdapter=new AdjustmentAdapter(stockAdjustList, new AdjustmentAdapter.CallBack() {
            @Override
            public void setAdjustment(String adjustment, String adjustmentId) {
                stockAdjustmentText.setText(adjustment);
                viewCloseBottomSheet();
                stockAdjustRefCode=adjustmentId;
              /*  if (SalesReturnList.stockAdjustRefNoList!=null && SalesReturnList.stockAdjustRefNoList.size()>0){
                    SalesReturnList.stockAdjustRefNoList.set(editItemPosition,adjustmentId);
                    for (String s:SalesReturnList.stockAdjustRefNoList){
                        Log.w("AdjustRefCode_EDIT:",s);
                    }
                }else {
                    if (productSummaryList.size()==0){
                        SalesReturnList.stockAdjustRefNoList.add(0,adjustmentId);
                    }else {
                        SalesReturnList.stockAdjustRefNoList.add(productSummaryList.size(),adjustmentId);
                    }
                    Log.w("Position:",productSummaryList.size()+"");
                    Log.w("AdjustmentId",adjustmentId);
                }*/
            }
        });
        adjustmentRecyclerView.setAdapter(adjustmentAdapter);
    }

    public static class AdjustmentAdapter extends RecyclerView.Adapter<AdjustmentAdapter.ViewHolder> {
        private ArrayList<StockAdjustmentModel> letters;
        private int selectedPosition = -1;// no selection by default
        private CallBack callBack;

        public AdjustmentAdapter(ArrayList<StockAdjustmentModel> countries,CallBack callBack) {
            this.letters = countries;
            this.callBack=callBack;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adjustment_layout_items, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
            viewHolder.title.setText(letters.get(position).getDescription());
            viewHolder.selectTitleCheck.setChecked(letters.get(position).isIschecked());

            if (selectedPosition == position) {
                viewHolder.itemView.setSelected(true);
                viewHolder.selectTitleCheck.setChecked(true);
            } else {
                viewHolder.itemView.setSelected(false);
                viewHolder.selectTitleCheck.setChecked(false);
            }

            viewHolder.selectTitleCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedPosition >= 0)
                        notifyItemChanged(selectedPosition);
                        selectedPosition = viewHolder.getAdapterPosition();
                        notifyItemChanged(selectedPosition);
                        callBack.setAdjustment(letters.get(position).getDescription(),letters.get(position).getRefCode());
                  }
             });

    /*    viewHolder.tv_letters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition >= 0)
                    notifyItemChanged(selectedPosition);
                    selectedPosition = viewHolder.getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                    callBack.sortProduct(viewHolder.tv_letters.getText().toString());
            }
        });*/

        }

        @Override
        public int getItemCount() {
            return letters.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView title;
            private CheckBox selectTitleCheck;
            public ViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.title);
                selectTitleCheck=view.findViewById(R.id.adjustment_check);
            }
        }
        public void resetPosition(){
            selectedPosition=-1;
            notifyDataSetChanged();
        }

        interface CallBack {
            void setAdjustment(String adjustment,String adjustmentId);
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
        dialog=new ProgressDialog(getContext());
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





    public void showAlert(){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Warning !")
                .setContentText("Net total should not be zero")
                .setConfirmText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
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
}