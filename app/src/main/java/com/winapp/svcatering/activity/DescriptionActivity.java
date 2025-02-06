package com.winapp.svcatering.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.winapp.svcatering.R;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.model.UomModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.SharedPreferenceUtil;
import com.winapp.svcatering.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.winapp.svcatering.utils.Utils.fourDecimalPoint;
import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

public class DescriptionActivity extends AppCompatActivity {

    private ImageView mainImage;
    private TextView itemName;
    private TextView availability;
    private TextView netPrice;
    private TextView ctnQty;
    private SharedPreferenceUtil sharedPreferenceUtil;
    double percentApi = 0.0;
    private TextView pcsQty;
    private EditText ctnQtyValue;
    private EditText pcsQtyValue;
    private RadioGroup radioGroup;
    private RadioButton radioNormal;
    private RadioButton radioFoc;
    private ImageView ctnPlus;
    private ImageView ctnMinus;
    private ImageView pcsPlus;
    private ImageView pcsMinus;
    private DBHelper dbHelper;
    private Button addToCart;
    private TextView ctnQtyText;
    private TextView cartonText;
    private EditText ctnPrice;
    private EditText unitPrice;

    private Spinner uomSpinnerCart;
    private ProductsModel productsModel;

    public boolean isUomSetting = false;
    private String uomCode = "";
    private double ctnStockVal =0.0;
    private String uomName = "";
    private String stockStr = "";
    double net_amount=0.0;
    double carton_amount=0.0;
    double loose_amount=0.0;
    int cnQty=0;
    int lqty=0;
    double pcspercarton=0;
    // Customer details arraylist
    ArrayList<CustomerDetails> customerDetails = new ArrayList<>();
    boolean isQtyEntered=false;
    private TextView totalTextView;
    private TextView taxTextView;
    private TextView netTotalTextView;
    private TextView taxTitle;
    LinearLayout returnLayout;
    ImageView showHideButton;
    Switch focSwitch;
    Switch exchangeSwitch;
    Switch returnSwitch;
    EditText focEditText;
    EditText exchangeEditext;
    EditText discountEditext;
    EditText returnEditext;
    LinearLayout pcsQtyLayout;
    TextView qtyTextView;
    LinearLayout unitPriceLayout;
    private ArrayList<UomModel> uomList;
    TextWatcher cartonQtyWatcher;
    TextWatcher qtyWatcher;
    LinearLayout focLayout;
    boolean isAllowLowStock = false;
    TextView uomCodeText;
    LinearLayout uomSpinnerLay_cart;
    static ProgressDialog dialog;
    String selectCustomerId;
    HashMap<String ,String> user;
    SessionManager session;
    String companyCode;
    String locationCode;
    private String allowFOCStr = "";

    private String negativeStockStr = "No";

    boolean isReverseCalculationEnabled=true;
    boolean isCartonPriceEdit=true;
    boolean isPriceEdit=true;

    TextWatcher cartonPriceTextWatcher;
    TextWatcher loosePriceTextWatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.isTablet(this)){
            Log.w("This is Tablet","Success");
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setContentView(R.layout.activity_desc_land);
            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                setContentView(R.layout.activity_description);
            }
        }else {
            Log.w("This is not tablet","Success");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.cart_selectpdt_next);
        }
        Log.w("activity_cg",getClass().getSimpleName().toString());

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
        selectCustomerId = sharedPreferences.getString("customerId", "");

        session=new SessionManager(this);
        dbHelper=new DBHelper(this);
        sharedPreferenceUtil =new SharedPreferenceUtil(this);

        allowFOCStr = sharedPreferenceUtil.getStringPreference(sharedPreferenceUtil.KEY_ALLOW_FOC, "");

        mainImage=findViewById(R.id.item_image);
        itemName=findViewById(R.id.item_nameDesc);
        availability=findViewById(R.id.availabilty);
        netPrice =findViewById(R.id.price);
        ctnPrice=findViewById(R.id.ctn_price);
        ctnQty=findViewById(R.id.ctn_qty);
        uomSpinnerLay_cart =findViewById(R.id.uomSpinnerLay_cart);
        //pcsQty=findViewById(R.id.pcs_qty);
        ctnQtyValue=findViewById(R.id.ctn_qty_value);
        pcsQtyValue=findViewById(R.id.pcs_qty_value);
        radioGroup=findViewById(R.id.radioGroup);
        radioNormal=findViewById(R.id.radioNormal);
        radioFoc=findViewById(R.id.radioFoc);
        ctnMinus=findViewById(R.id.ctn_minus);
        ctnPlus=findViewById(R.id.ctn_plus);
        pcsMinus=findViewById(R.id.pcs_minus);
        pcsPlus=findViewById(R.id.pcs_plus);
        addToCart=findViewById(R.id.add_to_cart);
        cartonText=findViewById(R.id.pcs);
        unitPrice=findViewById(R.id.unit_price);
        totalTextView=findViewById(R.id.total);
        taxTextView=findViewById(R.id.tax);
        netTotalTextView=findViewById(R.id.net_total);
        taxTitle=findViewById(R.id.tax_title);
        returnLayout=findViewById(R.id.return_layout);
        showHideButton=findViewById(R.id.show_hide);
        focSwitch=findViewById(R.id.foc_switch);
        exchangeSwitch=findViewById(R.id.exchange_switch);
        returnSwitch=findViewById(R.id.return_switch);
        focEditText=findViewById(R.id.foc_text);
        exchangeEditext=findViewById(R.id.exchange_text);
        returnEditext=findViewById(R.id.return_text);
        discountEditext=findViewById(R.id.discount_text);
        pcsQtyLayout=findViewById(R.id.pcs_qty_layout);
        qtyTextView=findViewById(R.id.qty);
        unitPriceLayout=findViewById(R.id.unit_price_layout);
        focLayout=findViewById(R.id.foc_layout);
        uomCodeText=findViewById(R.id.uom_code);
        uomSpinnerCart = findViewById(R.id.uomSpinner_cart);
        user=session.getUserDetails();
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        negativeStockStr=user.get(SessionManager.KEY_NEGATIVE_STOCK);

        pcsQtyValue.setSelectAllOnFocus(true);
        ctnQtyValue.setSelectAllOnFocus(true);

        sharedPreferenceUtil.setStringPreference(sharedPreferenceUtil.KEY_CART_ITEM_DISC,"0.0");

      /*  ArrayList<SettingsModel> settings1=dbHelper.getSettings();
        if (settings1!=null) {
            if (settings1.size() > 0) {
                for (SettingsModel model : settings1) {
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
        }*/

     //   getLowStockSetting();
        isUomSetting = true ;
        returnEditext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setCalculation();
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
                setCalculation();
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
                setCalculation();
            }
        });

        showHideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showHideButton.getTag().equals("hide")){
                    returnLayout.setVisibility(View.VISIBLE);
                    showHideButton.setTag("show");
                    Utils.slideUp(returnLayout);
                    showHideButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                }else {
                    Utils.slideDown(returnLayout);
                    returnLayout.setVisibility(View.GONE);
                    showHideButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    showHideButton.setTag("hide");

                }
            }
        });


        ctnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctnQtyValue.clearFocus();
                if (ctnQtyValue.getText().toString().isEmpty()){
                    ctnQtyValue.setText("0");
                }else {
                    if (!ctnQtyValue.getText().toString().equals("0")){
                        int count=Integer.parseInt(ctnQtyValue.getText().toString());
                        int ctn=count-1;
                        ctnQtyValue.setText(String.valueOf(ctn));
                        setCalculation();
                    }
                }
            }
        });

        ctnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctnQtyValue.clearFocus();
                getLowStockSetting();
                double pcspercarton=Double.parseDouble(ctnQty.getText().toString());
                double stock=Double.parseDouble(availability.getText().toString());
                double allow_cn_qty=stock / pcspercarton;
                double net_qty_value=Double.parseDouble(qtyTextView.getText().toString())+pcspercarton;
                double net_qty_allow=net_qty_value / pcspercarton ;
                Log.w("Net_Qty:", String.valueOf(net_qty_allow));
                Log.w("Allow_cn_qty:",String.valueOf(allow_cn_qty));
                if (net_qty_allow > allow_cn_qty){
//                    if (isAllowLowStock){
                    if (negativeStockStr.equalsIgnoreCase("Yes")){

                        if (!ctnQtyValue.getText().toString().isEmpty()) {
                            int count = Integer.parseInt(ctnQtyValue.getText().toString());
                            int ctn = count + 1;
                            ctnQtyValue.setText(String.valueOf(ctn));
                            setCalculation();
                        } else {
                            int count = 0;
                            int ctn = count + 1;
                            ctnQtyValue.setText(String.valueOf(ctn));
                            setCalculation();
                        }
                    }else {
                        showLowStock();
                    }
                }else {
                    if (!ctnQtyValue.getText().toString().isEmpty()) {
                        int count = Integer.parseInt(ctnQtyValue.getText().toString());
                        int ctn = count + 1;
                        ctnQtyValue.setText(String.valueOf(ctn));
                        setCalculation();
                    } else {
                        int count = 0;
                        int ctn = count + 1;
                        ctnQtyValue.setText(String.valueOf(ctn));
                        setCalculation();
                    }
                }
            }
        });

        pcsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pcsQtyValue.clearFocus();
                if (pcsQtyValue.getText().toString().isEmpty()){
                    pcsQtyValue.setText("0");
                }else {
                    if (!pcsQtyValue.getText().toString().equals("0")){
                        int count=Integer.parseInt(pcsQtyValue.getText().toString());
                        int ctn=count-1;
                        pcsQtyValue.setText(String.valueOf(ctn));
                        setCalculation();
                    }
                }
            }
        });

        pcsPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pcsQtyValue.clearFocus();
                getLowStockSetting();
                double stock=Double.parseDouble(availability.getText().toString());
                double net_qty=Double.parseDouble(qtyTextView.getText().toString());
                if (net_qty + 1  > stock){
//                    if (isAllowLowStock){
                    if (negativeStockStr.equalsIgnoreCase("Yes")){

                        if (!pcsQtyValue.getText().toString().isEmpty()){
                            int count=Integer.parseInt(pcsQtyValue.getText().toString());
                            int ctn=count+1;
                            pcsQtyValue.setText(String.valueOf(ctn));
                            setCalculation();
                        }else {
                            int count=0;
                            int ctn=count+1;
                            pcsQtyValue.setText(String.valueOf(ctn));
                            setCalculation();
                        }
                    }else {
                        showLowStock();
                    }
                }else {
                        if (!pcsQtyValue.getText().toString().isEmpty()){
                        int count=Integer.parseInt(pcsQtyValue.getText().toString());
                        int ctn=count+1;
                        pcsQtyValue.setText(String.valueOf(ctn));
                        setCalculation();
                    }else {
                        int count=0;
                        int ctn=count+1;
                        pcsQtyValue.setText(String.valueOf(ctn));
                        setCalculation();
                    }
                }
            }
        });


      /*  loosePriceTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setCalculation();
                // Sathish 21/04/2021
                if (isReverseCalculationEnabled){
                    if (isPriceEdit){
                        if (unitPrice.getText().toString().equals("")){
                            ctnPrice.removeTextChangedListener(cartonPriceTextWatcher);
                            ctnPrice.setText("");
                            ctnPrice.addTextChangedListener(cartonPriceTextWatcher);
                        }
                        if (!ctnQty.getText().toString().equals("")){
                            double pcspercarton=Double.parseDouble(ctnQty.getText().toString());
                            double unit_price=0;
                            if (unitPrice.getText().toString().equals("")){
                                unit_price=0;
                            }else {
                                unit_price=Double.parseDouble(unitPrice.getText().toString());
                            }
                            if (pcspercarton>1){
                                double cart_price=(pcspercarton * unit_price);
                                ctnPrice.removeTextChangedListener(cartonPriceTextWatcher);
                                ctnPrice.setText(twoDecimalPoint(cart_price));
                                ctnPrice.addTextChangedListener(cartonPriceTextWatcher);
                            }else {
                                if (!unitPrice.getText().toString().isEmpty()){
                                    ctnPrice.removeTextChangedListener(cartonPriceTextWatcher);
                                    ctnPrice.setText(twoDecimalPoint(Double.parseDouble(unitPrice.getText().toString())));
                                    ctnPrice.addTextChangedListener(cartonPriceTextWatcher);
                                }else {
                                    ctnPrice.removeTextChangedListener(cartonPriceTextWatcher);
                                    ctnPrice.setText("0.00");
                                    ctnPrice.addTextChangedListener(cartonPriceTextWatcher);
                                }
                            }
                        }
                    }
                }
            }
        };
        unitPrice.addTextChangedListener(loosePriceTextWatcher);*/

        cartonPriceTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setCalculation();

                if (isReverseCalculationEnabled){
                    if (isCartonPriceEdit){
                        if (ctnPrice.getText().toString().equals("")){
                            if (s.toString().equals(".")) {
                                ctnPrice.setText("0.");
                                unitPrice.removeTextChangedListener(loosePriceTextWatcher);
                                unitPrice.setText("");
                                unitPrice.addTextChangedListener(loosePriceTextWatcher);
                            }
                        }
                        if (!ctnQty.getText().toString().equals("")){
                            double pcspercarton=Double.parseDouble(ctnQty.getText().toString());
                            double carton_price=0;
                            if (!s.toString().equals(".")) {
                                if (ctnPrice.getText().toString().equals("")) {
                                    carton_price = 0;
                                } else {
                                    carton_price = Double.parseDouble(ctnPrice.getText().toString());
                                }
                            }
                            if (pcspercarton>1){
                                double unit_price=(carton_price / pcspercarton);
                                unitPrice.removeTextChangedListener(loosePriceTextWatcher);
                                unitPrice.setText(twoDecimalPoint(unit_price));
                                unitPrice.addTextChangedListener(loosePriceTextWatcher);
                            }else {
                                if (!s.toString().equals(".")) {
                                    if (!ctnPrice.getText().toString().isEmpty()) {
                                        unitPrice.removeTextChangedListener(loosePriceTextWatcher);
                                        unitPrice.setText(twoDecimalPoint(Double.parseDouble(ctnPrice.getText().toString())));
                                        unitPrice.addTextChangedListener(loosePriceTextWatcher);
                                    } else {
                                        unitPrice.removeTextChangedListener(loosePriceTextWatcher);
                                        unitPrice.setText("0.00");
                                        unitPrice.addTextChangedListener(loosePriceTextWatcher);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };
        ctnPrice.addTextChangedListener(cartonPriceTextWatcher);

        Gson gson = new Gson();
        ProductsModel model = gson.fromJson(getIntent().getStringExtra("productDetails"), ProductsModel.class);

        SharedPreferences sharedPreferences1 = getSharedPreferences("customerPref",MODE_PRIVATE);
        selectCustomerId = sharedPreferences1.getString("customerId", "");
        if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
            try {
                customerDetails = dbHelper.getCustomer(selectCustomerId);
               Log.w("allowfoc11",""+allowFOCStr);

                if(allowFOCStr.equalsIgnoreCase("Yes")){
                    focEditText.setEnabled(true);
                }
                else{
                    focEditText.setEnabled(false);
                }
               // getProductPrice(model.getProductCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (model.getStockQty() !=null && !Objects.requireNonNull(model.getStockQty()).equals("null")){
            availability.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getStockQty())));
        }else {
            availability.setText("0");
        }
        stockStr = model.getStockQty() ;

        if (model.getPcsPerCarton() !=null && !model.getPcsPerCarton().equals("null")){
            cartonText.setText("1 * "+model.getPcsPerCarton());
        }else {
            cartonText.setText("1 * 1");
        }

        if (getIntent()!=null){
            getSupportActionBar().setTitle(model.getProductName());
            itemName.setText(model.getProductName());
            unitPrice.setText(model.getWholeSalePrice());
            ctnPrice.setText(String.valueOf(model.getRetailPrice()));
            double data = Double.parseDouble(model.getPcsPerCarton());
//            allowFOC=getIntent().getStringExtra("AllowFOC_Catalog");

            // convert into int
            int value = (int)data;
            ctnQty.setText(String.valueOf(value));
            uomCodeText.setText(model.getUomCode());
            productsModel = model;

            Log.w("uomcoddss",""+isUomSetting);
            if(isUomSetting) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("CustomerCode", selectCustomerId);
                    jsonObject.put("ItemCode", model.getProductCode());
                    getUOM(jsonObject ,model);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                uomSpinnerLay_cart.setVisibility(View.VISIBLE);
            }

        }

        if (ctnQty.getText()!=null){
            double data = Double.parseDouble(model.getPcsPerCarton());
            // convert into int
            int value = (int)data;
            if (value==1){
                pcsQtyLayout.setVisibility(View.GONE);
                unitPriceLayout.setVisibility(View.GONE);
            }else {
                pcsQtyLayout.setVisibility(View.GONE);
                unitPriceLayout.setVisibility(View.GONE);
            }
        }


        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double discout = 0.0 ;

                SharedPreferences sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
                selectCustomerId = sharedPreferences.getString("customerId", "");
                if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
                    if (!discountEditext.getText().toString().isEmpty()) {
                        discout = Double.parseDouble(discountEditext.getText().toString());
                    }

                    if (!ctnPrice.getText().toString().isEmpty()){
                        if (!ctnPrice.getText().toString().equals(".")) {

                            percentApi=calculatePercentage(discout, Double.parseDouble(ctnPrice.getText().toString()));
                            Log.w("cartDis ",""+percentApi+discout+ctnPrice.getText().toString() );
                        }
                    }

                    if (isProductExist(model.getProductCode().trim())){
                        showExistingProductAlert(model.getProductCode(),model.getProductName(),model.getProductImage());
                    }else {
                        if (selectCustomerId!=null && !selectCustomerId.isEmpty()){
                            String focType="pcs";
                            String exchangeType="pcs";
                            String returnType="pcs";
                            String return_qty="0";
                            String lPriceCalc="0";
                            String loose_qty="0";
                            String ctn_qty="0";
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


                            if (!returnEditext.getText().toString().isEmpty()){
                                return_qty=returnEditext.getText().toString();
                            }

                            if (!pcsQtyValue.getText().toString().isEmpty()){
                                loose_qty=pcsQtyValue.getText().toString();
                            }

                            if (!ctnQtyValue.getText().toString().isEmpty()){
                                ctn_qty=ctnQtyValue.getText().toString();
                            }

                            if (!ctnPrice.getText().toString().isEmpty()){
                                if (!ctnPrice.getText().toString().equals(".")) {

                                    cartonprice = ctnPrice.getText().toString();
                                }
                            }

                            if (!discountEditext.getText().toString().isEmpty()){
                                discount=discountEditext.getText().toString();
                            }

                            if (!unitPrice.getText().toString().isEmpty()){
                                lPriceCalc=unitPrice.getText().toString();
                            }

                            double return_amt=(Double.parseDouble(return_qty)*Double.parseDouble(lPriceCalc));
                            double total=(Double.parseDouble(ctn_qty) * Double.parseDouble(cartonprice)) + (Double.parseDouble(loose_qty) * Double.parseDouble(lPriceCalc));
                            double sub_total=total-return_amt-Double.parseDouble(discount);

                            if (pcsQtyValue.getText().toString().isEmpty() && ctnQtyValue.getText().toString().isEmpty() &&
                                    !ctnQtyValue.getText().toString().equals(".")){
                                isQtyEntered=false;
                            }else isQtyEntered= !pcsQtyValue.getText().toString().equals("0") ||
                                    (!ctnQtyValue.getText().toString().equals("0") &&
                                            !ctnQtyValue.getText().toString().equals("."));

                            if (isQtyEntered && Double.parseDouble(netTotalTextView.getText().toString())>0){

                                boolean status = dbHelper.insertCart(
                                        model.getProductCode(),
                                        model.getProductName(),
                                        ctnQtyValue.getText().toString(),
                                        pcsQtyValue.getText().toString(),
                                        totalTextView.getText().toString(),
                                        model.getProductImage(),
                                        netTotalTextView.getText().toString(),
                                        "weight",
                                        ctnPrice.getText().toString(),
                                        unitPrice.getText().toString(),
                                        ctnQty.getText().toString(),
                                        taxTextView.getText().toString(),
                                        String.valueOf(sub_total),
                                        customerDetails.get(0).getTaxType(),
                                        focEditText.getText().toString(),
                                        focType,
                                        exchangeEditext.getText().toString(),
                                        exchangeType,
                                        String.valueOf(Utils.twoDecimalPoint(percentApi)),
                                        returnEditext.getText().toString(),
                                        returnType,"",
                                        String.valueOf(total),
                                        availability.getText().toString(),
                                        uomCode,"0.00",
                                        availability.getText().toString());


                    /*boolean status= dbHelper.insertCart(
                            model.getProductCode(),
                            model.getProductName(),
                            ctnQtyValue.getText().toString(),
                            pcsQtyValue.getText().toString(),
                            model.getUnitCost(),
                            model.getProductImage(),
                            netPrice.getText().toString(),
                            model.getWeight(),
                            ctnPrice.getText().toString(),
                            unitPrice.getText().toString(),
                            String.valueOf(pcspercarton),
                            taxTextView.getText().toString(),
                            totalTextView.getText().toString(),
                            customerDetails.get(0).getTaxType(),
                            focEditText.getText().toString(),
                            focType,
                            exchangeEditext.getText().toString(),
                            exchangeType,
                            discountEditext.getText().toString(),
                            returnEditext.getText().toString(),
                            returnType);*/
                                if (status){
                                    Toast.makeText(getApplicationContext(),"Product Added Successfully",Toast.LENGTH_LONG).show();
                                    //Intent intent=new Intent(DescriptionActivity.this,MainHomeActivity.class);
                                    // startActivity(intent);
                                    finish();
                                }else {
                                    Toast.makeText(getApplicationContext(),"Error in Add product",Toast.LENGTH_LONG).show();
                                }
                            }else {
                                showAlert();
                            }
                        }else {
                            showAlertCustomer();
                        }
                        //insertProduct();
                    }
                }else {
                    showAlertCustomer();
                }

            }
        });


        // Getting image from the Local DB
        String imagePath1=dbHelper.getProductImage(model.getProductCode());
        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!imagePath1.isEmpty()) {
                    String imageFullpath = Constants.folderPath + "/" + imagePath1;
                    File file = new File(imageFullpath);
                    if (file.exists()) {
                        showImageFile(file);
                    }else {
                        showImageUrl(model.getProductImage());
                    }
                }else {
                    showImageUrl(model.getProductImage());
                }
            }
        });


        // Getting image from the Local DB
        String imagePath=dbHelper.getProductImage(model.getProductCode());
        if (!imagePath.isEmpty()){
            String imageFullpath=Constants.folderPath+"/"+imagePath;
            File file = new File(imageFullpath);
            if (file.exists()){
                Glide.with(this)
                        .load(file)
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
                        }).into(mainImage);
            }else {
                Glide.with(this)
                        .load(model.getProductImage())
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
                        }).into(mainImage);
             }
        }else {
            if (model.getProductImage() != null && !model.getProductImage().equals("null")) {
                Glide.with(this)
                        .load(model.getProductImage())
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
                        }).into(mainImage);
            } else {
                mainImage.setImageResource(R.drawable.no_image_found);
            }
        }


      cartonQtyWatcher=new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void afterTextChanged(Editable editable) {
              getLowStockSetting();
              double net_qty=0.0;
              double ctn_qty=0.0;
              double pcs_ctn=0.0;
              double pcs_qty=0.0;
              if (!editable.toString().isEmpty()){
                  ctn_qty=Double.parseDouble(editable.toString());
              }
              if (!ctnQty.getText().toString().isEmpty()){
                  pcs_ctn=Double.parseDouble(ctnQty.getText().toString());
              }

              if (!pcsQtyValue.getText().toString().isEmpty()){
                  pcs_qty=Double.parseDouble(pcsQtyValue.getText().toString());
              }
              net_qty=(ctn_qty * pcs_ctn) + pcs_qty;
              double stock=Double.parseDouble(availability.getText().toString());
              if (net_qty  > stock){
//                  if (!isAllowLowStock){
                  if (negativeStockStr.equalsIgnoreCase("No")){
                      showLowStock();
                      ctnQtyValue.removeTextChangedListener(cartonQtyWatcher);
                      ctnQtyValue.setText("0");
                      ctnQtyValue.addTextChangedListener(cartonQtyWatcher);
                      ctnQtyValue.clearFocus();
                  }
              }
              setCalculation();
          }
      };
      ctnQtyValue.addTextChangedListener(cartonQtyWatcher);


      qtyWatcher=new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void afterTextChanged(Editable editable) {
              getLowStockSetting();
              double net_qty=0.0;
              double ctn_qty=0.0;
              double pcs_ctn=0.0;
              double pcs_qty=0.0;
              if (!ctnQtyValue.getText().toString().isEmpty()){
                  ctn_qty=Double.parseDouble(ctnQtyValue.getText().toString());
              }
              if (!ctnQty.getText().toString().isEmpty()){
                  pcs_ctn=Double.parseDouble(ctnQty.getText().toString());
              }
              if (!editable.toString().isEmpty()){
                  pcs_qty=Double.parseDouble(editable.toString());
              }
              net_qty=(ctn_qty * pcs_ctn) + pcs_qty;
              double stock=Double.parseDouble(availability.getText().toString());
              if (net_qty  > stock){
//                  if (!isAllowLowStock){
                  if (negativeStockStr.equalsIgnoreCase("No")){

                      showLowStock();
                      pcsQtyValue.removeTextChangedListener(qtyWatcher);
                      pcsQtyValue.setText("0");
                      pcsQtyValue.addTextChangedListener(qtyWatcher);
                      pcsQtyValue.clearFocus();
                  }
              }
              setCalculation();
          }
      };
      pcsQtyValue.addTextChangedListener(qtyWatcher);



      /*ctnQtyValue.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void afterTextChanged(Editable editable) {
              double net_qty=0.0;
              double ctn_qty=0.0;
              double pcs_ctn=0.0;
              double pcs_qty=0.0;
              if (!editable.toString().isEmpty()){
                  ctn_qty=Double.parseDouble(editable.toString());
              }
              if (!ctnQty.getText().toString().isEmpty()){
                  pcs_ctn=Double.parseDouble(ctnQty.getText().toString());
              }

              if (!pcsQtyValue.getText().toString().isEmpty()){
                  pcs_qty=Double.parseDouble(pcsQtyValue.getText().toString());
              }
              net_qty=(ctn_qty * pcs_ctn) + pcs_qty;
              double stock=Double.parseDouble(availability.getText().toString());
              if (net_qty  > stock){
                  showLowStock();
                  ctnQtyValue.setText("0");
                  ctnQtyValue.clearFocus();
              }
              setCalculation();
          }
      });*/


    /*  pcsQtyValue.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void afterTextChanged(Editable editable) {
              // calculate the net amount when update the values
              double net_qty=0.0;
              double ctn_qty=0.0;
              double pcs_ctn=0.0;
              double pcs_qty=0.0;
              if (!ctnQtyValue.getText().toString().isEmpty()){
                  ctn_qty=Double.parseDouble(ctnQtyValue.getText().toString());
              }
              if (!ctnQty.getText().toString().isEmpty()){
                  pcs_ctn=Double.parseDouble(ctnQty.getText().toString());
              }
              if (!editable.toString().isEmpty()){
                  pcs_qty=Double.parseDouble(editable.toString());
              }
              net_qty=(ctn_qty * pcs_ctn) + pcs_qty;
              double stock=Double.parseDouble(availability.getText().toString());
              if (net_qty  > stock){
                  showLowStock();
                  pcsQtyValue.setText("0");
                  pcsQtyValue.clearFocus();
              }
              setCalculation();
          }
      });
*/


    // Hide the FOC Layout for the Setting Based
        /*ArrayList<SettingsModel> settings=dbHelper.getSettings();
        if (settings.size()>0) {
            for (SettingsModel mod : settings) {
                if (mod.getSettingName().equals("invoice_switch")) {
                    if (mod.getSettingValue().equals("1")) {
                        focLayout.setVisibility(View.VISIBLE);
                    } else {
                        focLayout.setVisibility(View.GONE);
                    }
                }
            }
        }*/

    }

    public boolean isProductExist(String productId) {
        boolean isExist=false;
        try {
            ArrayList<CartModel> localCart = dbHelper.getAllCartItems();
            if (localCart.size() > 0) {
                for (CartModel cart : localCart) {
                    if (cart.getCART_COLUMN_PID()!=null){
                        if (cart.getCART_COLUMN_PID().equals(productId)) {
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

    public void showImageFile(File imageUrl) {
        Dialog builder = new Dialog(DescriptionActivity.this,android.R.style.Theme_Light);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //  builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        builder.setCanceledOnTouchOutside(true);
        ImageView imageView = new ImageView(DescriptionActivity.this);
        ImageView closeImage=new ImageView(DescriptionActivity.this);
        closeImage.setImageResource(R.drawable.ic_baseline_close_24);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;
        //layoutParams.setMargins(10, 10, 10, 10); // (left, top, right, bottom)
        closeImage.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        //layoutParams.setMargins(10, 10, 10, 10); // (left, top, right, bottom)
        imageView.setLayoutParams(layoutParams1);

        FrameLayout frameLayout=new FrameLayout(DescriptionActivity.this);
        frameLayout.addView(imageView);
        frameLayout.addView(closeImage);
        builder.addContentView(frameLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Glide.with(DescriptionActivity.this)
                .asBitmap()
                //.apply(myOptions)
                .load(imageUrl)
                .error(R.drawable.no_image_found)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        imageView.setImageResource(R.drawable.no_image_found);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(imageView);
        builder.show();
    }

    public void getUOM(JSONObject jsonObject ,  ProductsModel model) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "ItemUOMDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_UOM_URL:", url + jsonObject.toString());

        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading UOM...");
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try {
                uomList = new ArrayList<>();

                Log.w("Res_UOM:", response.toString());
                // Loop through the array elements
                JSONArray uomArray = response.optJSONArray("responseData");
                if (uomArray != null && uomArray.length() > 0) {
                    for (int j = 0; j < uomArray.length(); j++) {
                        JSONObject uomObject = uomArray.getJSONObject(j);
                        UomModel uomModel = new UomModel();
                        uomModel.setUomCode(uomObject.optString("uomCode"));
                        uomModel.setUomName(uomObject.optString("uomName"));
                        uomModel.setUomEntry(uomObject.optString("uomEntry"));
                        uomModel.setAltQty(uomObject.optString("altQty"));
                        uomModel.setBaseQty(uomObject.optString("baseQty"));
                        uomModel.setPrice(uomObject.optString("price"));
                        uomList.add(uomModel);
                    }
                }

                Log.w("UOM_TEXT:", uomArray.toString());
                pDialog.dismiss();
                if (uomList.size() > 0) {
                    runOnUiThread(() -> {
                        setUomList(uomList , model);
                    });
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

    private void setUomList(ArrayList<UomModel> uomList ,  ProductsModel model) {

        Log.w("UOMList:", uomList.toString() +".."+model.getDefaultUom()+"...."+model.getUomCode());
        ArrayAdapter<UomModel> adapter = new ArrayAdapter<>(this, R.layout.cust_spinner_item, uomList);
        uomSpinnerCart.setAdapter(adapter);
        setUOMCode(uomList);
        if (model != null) {
            //setuom
            Log.d("cg_uomsiz1", uomList.size() + "");
            for (int i = 0; i < uomList.size(); i++) {
                Log.w("cg_uomsiz", uomList.size() + "");
                if (uomList.get(i).getUomCode().equals(model.getDefaultUom())) {
                    Log.w("cg_uoomcode_", model.getDefaultUom());
                    uomSpinnerCart.setSelection(i);
                   // uomCode = uomList.get(i).getUomCode() ;
                    //uomText.setText(uomList.get(i).getUomCode());
                    ctnPrice.setText(uomList.get(i).getPrice());
                    break;
                }
            }
        }
    }

    public double calculatePercentage(double obtained, double total) {
        return obtained * 100 / total;
    }

    private void setUOMCode(ArrayList<UomModel> uomList) {
        uomSpinnerCart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    uomName = uomSpinnerCart.getSelectedItem().toString();
                    uomCode = uomList.get(position).getUomCode() ;
                  //  uomText.setText(uomList.get(position).getUomCode());
                   ctnPrice.setText(uomList.get(position).getPrice());
                if (uomName.equals("CTN")) {
                    double baseCtnQty = Double.parseDouble(uomList.get(position).getBaseQty());
                    double pdtStock = Double.parseDouble(stockStr);

                    ctnStockVal = pdtStock / baseCtnQty ;
                    availability.setText(String.valueOf(ctnStockVal));
                }else{
                    availability.setText(stockStr);

                }

                    Log.w("UOMQtyValueCart:", uomList.get(position).getUomEntry());
                    Log.w("SelectedUOMCart:", uomName + "");

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    public void showImageUrl(String imageUrl) {
        Dialog builder = new Dialog(DescriptionActivity.this,android.R.style.Theme_Light);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
                dialogInterface.dismiss();
            }
        });

        builder.setCanceledOnTouchOutside(true);

        ImageView imageView = new ImageView(DescriptionActivity.this);
        Glide.with(DescriptionActivity.this)
                .asBitmap()
                //.apply(myOptions)
                .load(imageUrl)
                .error(R.drawable.no_image_found)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        imageView.setImageResource(R.drawable.no_image_found);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(imageView);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
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
                else if (model.getSettingName().equals("UomSwitch")) {
                    Log.w("SettingNameI:", model.getSettingName());
                    Log.w("SettingValueI:", model.getSettingValue());
                    if (model.getSettingValue().equals("1")) {
                        isUomSetting = true;
                    } else {
                        isUomSetting = false;
                    }
                }
            }
        }
    }

    public void showAlertCustomer(){
        try {
            new SweetAlertDialog(DescriptionActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Warning !")
                    .setContentText("Please Choose your Customer!")
                    .setConfirmText("Cancel")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    }).show();
        }catch (Exception ex){}

    }

    private static String resizeBase64Image(String base64image) {
        int IMG_WIDTH = 640;
        int IMG_HEIGHT = 480;
        byte[] encodeByte = Base64.decode(base64image.getBytes(), Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);

     /*   if (image.getHeight() == 400 && image.getWidth() == 400) {
            return base64image;
        }*/
//        image = Bitmap.createScaledBitmap(image, IMG_WIDTH, IMG_HEIGHT, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);

        byte[] b = baos.toByteArray();
        System.gc();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }


    public static Bitmap convertStringToBitmap(String base64String) {
        return convertString64ToImage(resizeBase64Image(base64String));
    }

    private static Bitmap convertString64ToImage(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
               // onBackPressed();
                break;

         /*   case R.id.action_remove:
                showRemoveAlert();
                break;*/
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public boolean validateStock(){
        double net_qty=0.0;
        double stock_qty=0.0;
        boolean check=false;
        if (qtyTextView.getText().toString().isEmpty()){
            net_qty=0.0;
        }else {
            net_qty=Double.parseDouble(qtyTextView.getText().toString());
        }
        if (availability.getText().toString().isEmpty()){
            stock_qty=0.0;
        }else {
            stock_qty=Double.parseDouble(availability.getText().toString());
        }
        if (net_qty>stock_qty) {
            showLowStock();
        }else {
            check=true;
        }
        return  check;
    }

    public void showLowStock(){
        new SweetAlertDialog(DescriptionActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Warning !")
                .setContentText("Low Stock Please check!")
                .setConfirmText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }

    public void setCalculation() {
        // Define the Required variables to calulate the Amount
    //    try {
            double cnQty = 0.0;
            double lqty = 0.0;
            double pcspercarton = 0.0;
            double carton_amount = 0.0;
            double net_amount = 0.0;
            double loose_amount = 0.0;
            double cprice = 0.0;
            double lprice = 0.0;
            double discount = 0.0;
            double return_qty = 0;

            if (!ctnPrice.getText().toString().isEmpty()) {
                if (!ctnPrice.getText().toString().equals(".")) {

                    cprice = Double.parseDouble(ctnPrice.getText().toString());
                }
            }
            if (!unitPrice.getText().toString().isEmpty()) {
                lprice = Double.parseDouble(unitPrice.getText().toString());
            }

            if (!ctnQtyValue.getText().toString().isEmpty()) {
                cnQty = Double.parseDouble(ctnQtyValue.getText().toString());
            }

            if (!pcsQtyValue.getText().toString().isEmpty()) {
                lqty = Double.parseDouble(pcsQtyValue.getText().toString());
            }

            if (!discountEditext.getText().toString().isEmpty()) {
                discount = Double.parseDouble(discountEditext.getText().toString());
            }

            if (!ctnQty.getText().toString().isEmpty()) {
                pcspercarton = Double.parseDouble(ctnQty.getText().toString());
            }

            if (!returnEditext.getText().toString().isEmpty()) {
                return_qty = Integer.parseInt(returnEditext.getText().toString());
            }

          /*  // calculating the net total
            if (pcspercarton > 1) {
                carton_amount = (cnQty * cprice);
                loose_amount = (lqty * lprice);
                net_amount = carton_amount + loose_amount;
                // netPrice.setText(twoDecimalPoint(net_amount));
                Log.w("Net_amount_1:", String.valueOf(net_amount));
            } else {
                net_amount = (cnQty * cprice);
                // netPrice.setText(twoDecimalPoint(carton_amount));
                Log.w("Net_amount_2:", String.valueOf(net_amount));
            }*/

            net_amount = (cnQty * cprice);
            Log.w("Net_amount_2:", String.valueOf(net_amount));
            double net_qty=(cnQty);
            int value = (int)net_qty;
            qtyTextView.setText(String.valueOf(value));

            if (return_qty != 0) {
                if (returnSwitch.isChecked()) {
                    return_qty = Double.parseDouble(returnEditext.getText().toString()) * pcspercarton;
                } else {
                    return_qty = Double.parseDouble(returnEditext.getText().toString());
                }
                double return_amt=0.0;
                if (pcspercarton>1){
                     return_amt = (return_qty * lprice);
                }else if (pcspercarton==1){
                     return_amt = (return_qty * cprice);
                }
                net_amount = net_amount - return_amt;
            }
            if (discount != 0.0) {
                net_amount = net_amount - discount;
            }
//        if (!discountEditext.getText().toString().isEmpty()) {
//            discount = Double.parseDouble(discountEditext.getText().toString());
//        }
//            percentApi=calculatePercentage(discount,cprice);
//        Log.w("cartDis ",""+percentApi);

            netPrice.setText(twoDecimalPoint(net_amount));
            taxCalculation(net_amount);

            sharedPreferenceUtil.setStringPreference(sharedPreferenceUtil.KEY_CART_ITEM_DISC, Utils.twoDecimalPoint(percentApi));

//        } catch (Exception ex) {
//        }
    }

 /*   // calculation for the product
    public void setCalculation(){

        try {
            // Define the Required variables to calulate the Amount

            double cprice=Double.parseDouble(ctnPrice.getText().toString());
            double lprice=Double.parseDouble(unitPrice.getText().toString());

            if (!ctnQtyValue.getText().toString().isEmpty()){
                cnQty=Integer.parseInt(ctnQtyValue.getText().toString());
            }

            if (!pcsQtyValue.getText().toString().isEmpty()){
                lqty=Integer.parseInt(pcsQtyValue.getText().toString());
            }
            pcspercarton=Double.parseDouble(ctnQty.getText().toString());

            // calculating the net total
            if (pcspercarton>1){
                carton_amount=(cnQty*cprice);
                loose_amount=(lqty*lprice);
                net_amount=carton_amount+loose_amount;
                netPrice.setText(twoDecimalPoint(net_amount));
            }else {
                carton_amount=(cnQty*lprice);
                netPrice.setText(twoDecimalPoint(carton_amount));
            }

            taxCalculation(Double.parseDouble(netPrice.getText().toString()));
        }catch (Exception ex){}
    }
*/
    public void taxCalculation(double subTotal){

        SharedPreferences sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
        String selectCustomerId = sharedPreferences.getString("customerId", "");

        if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
            customerDetails = dbHelper.getCustomer(selectCustomerId);
            //setAllValues(customerDetails);

            String taxValue = customerDetails.get(0).getTaxPerc();
            String taxType = customerDetails.get(0).getTaxType();
            String itemDisc = "";
            double taxAmount = 0.0;
            double netTotal = 0.0;
            Log.w("TaxTypeCart:", taxType);
            Log.w("TaxValueCart:", taxValue);

            if (!taxType.matches("") && !taxValue.matches("")) {
                Log.w("cartEntytax", "");

                double taxValueCalc = Double.parseDouble(taxValue);

                if (taxType.matches("E")) {
                    taxAmount = (subTotal * taxValueCalc) / 100;
                    String prodTax = fourDecimalPoint(taxAmount);

                    netTotal = subTotal + taxAmount;
                    String ProdNetTotal = twoDecimalPoint(netTotal);

                    Log.w("Tax_1", prodTax);
                    Log.w("Net_amount:", ProdNetTotal);
                    taxTitle.setText("GST ( Exc )");
                    totalTextView.setText(twoDecimalPoint(subTotal));
                    taxTextView.setText(String.valueOf(prodTax));
                    netTotalTextView.setText(ProdNetTotal);

                } else if (taxType.matches("I")) {
                    taxAmount = (subTotal * taxValueCalc) / (100 + taxValueCalc);
                    String prodTax = fourDecimalPoint(taxAmount);

                    netTotal = subTotal + taxAmount;
                    netTotal = subTotal;
                    String ProdNetTotal = twoDecimalPoint(netTotal);

                    double dTotalIncl = netTotal - taxAmount;
                    String totalIncl = twoDecimalPoint(dTotalIncl);
                    Log.d("totalIncl", "" + totalIncl);

                    Log.w("Tax_1", prodTax);
                    Log.w("Net_amount:", ProdNetTotal);
                    taxTitle.setText("GST ( Inc )");
                    totalTextView.setText(totalIncl);
                    taxTextView.setText(String.valueOf(prodTax));
                    netTotalTextView.setText(ProdNetTotal);

                } else if (taxType.matches("Z")) {
                    netTotal = subTotal + taxAmount;
                    netTotal = subTotal;
                    String ProdNetTotal = twoDecimalPoint(netTotal);
                    Log.w("Net_amount:", ProdNetTotal);
                    taxTitle.setText("GST ( Zero )");
                    totalTextView.setText(twoDecimalPoint(subTotal));
                    taxTextView.setText("0.0");
                    netTotalTextView.setText(ProdNetTotal);
                }
            } else if (taxValue.matches("")) {
                netTotal = subTotal + taxAmount;
                netTotal = subTotal;
                String ProdNetTotal = twoDecimalPoint(netTotal);
                Log.w("Net_amount:", ProdNetTotal);
                taxTitle.setText("GST ( Zero )");
                totalTextView.setText(twoDecimalPoint(subTotal));
                taxTextView.setText("0.0");
                netTotalTextView.setText(ProdNetTotal);
            }
        }
    }

    public void showAlert(){
        String message="";
        if (isQtyEntered){
            message="Enter the price of the product !";
        }else {
            message="Please Add the Qty !";
        }
        new SweetAlertDialog(DescriptionActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Warning !")
                .setContentText(message)
                .setConfirmText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }

    public void getProductPrice(String productId) throws JSONException {
        //  {"CompanyCode":"1","CustomerCode":"0003432","LocationCode":"HQ","ProductCode":"0000009"}
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyCode);
        jsonObject.put("CustomerCode",selectCustomerId);
        jsonObject.put("LocationCode",locationCode);
        jsonObject.put("ProductCode",productId);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //http://223.25.81.205:100//es/data/api/ProductApi/GetCustomerProductPrice?Requestdata={"CompanyCode":"10","CustomerCode":"TEST B ","LocationCode":"HQ","ProductCode":"ALU001"}
        String url=Utils.getBaseUrl(this) +"ProductApi/GetCustomerProductPrice?Requestdata="+jsonObject.toString();
        // String url="http://223.25.81.205:100/es/data/api/ProductApi/GetCustomerProductPrice?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_product_url:",url);
        dialog=new ProgressDialog(this);
        dialog.setMessage("Getting Product price...");
        dialog.setCancelable(false);
        dialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("ProductPriceResponse::",response.toString());
                        JSONArray jsonArray=new JSONArray(response.toString());
                        for (int i =0;i<jsonArray.length();i++){
                            JSONObject object=jsonArray.optJSONObject(i);
                            unitPrice.setText(object.optString("Price"));
                            ctnPrice.setText(object.optString("CartonPrice"));
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

    public void showExistingProductAlert(String productId,String productName,String productImage){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Warning !");
        builder1.setMessage(productName+" - "+productId+ "\nAlready Exist Do you want to replace ? ");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        insertProduct(productId,productName,productImage);
                    }
                });
        builder1.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        // clearFields();
                        //viewCloseBottomSheet();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void insertProduct(String productId,String productName,String productImage){
        if (selectCustomerId!=null && !selectCustomerId.isEmpty()) {
            String focType = "pcs";
            String exchangeType = "pcs";
            String returnType = "pcs";
            String return_qty = "0";
            String lPriceCalc = "0";
            String loose_qty = "0";
            String ctn_qty = "0";
            String cartonprice = "0";
            String discount = "0";
            if (focSwitch.isChecked()) {
                focType = "ctn";
            }
            if (exchangeSwitch.isChecked()) {
                exchangeType = "ctn";
            }
            if (returnSwitch.isChecked()) {
                returnType = "ctn";
            }


            if (!returnEditext.getText().toString().isEmpty()) {
                return_qty = returnEditext.getText().toString();
            }

            if (!pcsQtyValue.getText().toString().isEmpty()) {
                loose_qty = pcsQtyValue.getText().toString();
            }

            if (!ctnQtyValue.getText().toString().isEmpty()) {
                ctn_qty = ctnQtyValue.getText().toString();
            }

            if (!ctnPrice.getText().toString().isEmpty()) {
                if (!ctnPrice.getText().toString().equals(".")){
                cartonprice = ctnPrice.getText().toString();
                }
            }

            if (!discountEditext.getText().toString().isEmpty()) {
                discount = discountEditext.getText().toString();
            }

            if (!unitPrice.getText().toString().isEmpty()) {
                lPriceCalc = unitPrice.getText().toString();
            }

            double return_amt = (Double.parseDouble(return_qty) * Double.parseDouble(lPriceCalc));
            double total = (Double.parseDouble(ctn_qty) * Double.parseDouble(cartonprice)) + (Double.parseDouble(loose_qty) * Double.parseDouble(lPriceCalc));
            double sub_total = total - return_amt - Double.parseDouble(discount);

            if (pcsQtyValue.getText().toString().isEmpty() && ctnQtyValue.getText().toString().isEmpty()) {
                isQtyEntered = false;
            } else
                isQtyEntered = !pcsQtyValue.getText().toString().equals("0") || !ctnQtyValue.getText().toString().equals("0");

            if (isQtyEntered && Double.parseDouble(netTotalTextView.getText().toString()) > 0) {

                boolean status = dbHelper.insertCart(
                        productId,
                        productName,
                        ctnQtyValue.getText().toString(),
                        pcsQtyValue.getText().toString(),
                        totalTextView.getText().toString(),
                        productImage,
                        netTotalTextView.getText().toString(),
                        "weight",
                        ctnPrice.getText().toString(),
                        unitPrice.getText().toString(),
                        ctnQty.getText().toString(),
                        taxTextView.getText().toString(),
                        String.valueOf(sub_total),
                        customerDetails.get(0).getTaxType(),
                        focEditText.getText().toString(),
                        focType,
                        exchangeEditext.getText().toString(),
                        exchangeType,
                        String.valueOf(percentApi),
                        returnEditext.getText().toString(),
                        returnType, "",
                        String.valueOf(total),
                        availability.getText().toString(),
                        uomCode,
                        "0.00",availability.getText().toString());


                    /*boolean status= dbHelper.insertCart(
                            model.getProductCode(),
                            model.getProductName(),
                            ctnQtyValue.getText().toString(),
                            pcsQtyValue.getText().toString(),
                            model.getUnitCost(),
                            model.getProductImage(),
                            netPrice.getText().toString(),
                            model.getWeight(),
                            ctnPrice.getText().toString(),
                            unitPrice.getText().toString(),
                            String.valueOf(pcspercarton),
                            taxTextView.getText().toString(),
                            totalTextView.getText().toString(),
                            customerDetails.get(0).getTaxType(),
                            focEditText.getText().toString(),
                            focType,
                            exchangeEditext.getText().toString(),
                            exchangeType,
                            discountEditext.getText().toString(),
                            returnEditext.getText().toString(),
                            returnType);*/
                if (status) {
                    Toast.makeText(getApplicationContext(), "Product Added Successfully", Toast.LENGTH_LONG).show();
                    //Intent intent=new Intent(DescriptionActivity.this,MainHomeActivity.class);
                    // startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error in Add product", Toast.LENGTH_LONG).show();
                }
            } else {
                showAlert();
            }
        }else {
            showAlertCustomer();
        }
    }
}