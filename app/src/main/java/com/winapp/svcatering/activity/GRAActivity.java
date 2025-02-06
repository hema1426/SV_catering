package com.winapp.svcatering.activity;

import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.GRACartModel;
import com.winapp.svcatering.adapter.GRATableViewAdapter;
import com.winapp.svcatering.model.AppUtils;
import com.winapp.svcatering.model.GRAListModel;
import com.winapp.svcatering.model.ProductsModel;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GRAActivity extends AppCompatActivity implements View.OnClickListener, GRATableViewAdapter.CallBack{

    public AutoCompleteTextView supplier;
    private String supplier_name;
    private String supplier_code;
    public ArrayList<String> uomList;
    public ArrayList<String> supplierList;
    public StringRequest saveProductRequest;
    public JsonArrayRequest jsonArrayRequest;
    private String companyId;
    private SweetAlertDialog pDialog;
    private SessionManager session;
    private HashMap<String ,String > user;
    private String username;
    public int selectedProduct =-1;
    public TextView productNameText;
    public ArrayList<ProductsModel> productList;
    public String product_name;
    public String product_code ="";
    private EditText cartonPrice;
    private EditText unitPrice;
    private EditText cartonQtyValue;
    private EditText unitQtyValue;
    private EditText qtyValue;
    private TextWatcher cartonPriceTextWatcher;
    private TextWatcher unitPriceTextWatcher;
    private TextView pcsPerCarton;
    private TextView subTotalValue;
    private TextView taxValueText;
    private TextView netTotalValue;
    private TextView taxTitle;
    private TextWatcher cqtyTW, lqtyTW, qtyTW;
    private String beforeLooseQty,ss_Cqty;
    boolean isAllowLowStock=false;
    public ArrayList<GRACartModel> cartList;
    public RecyclerView summaryListView;
    public GRATableViewAdapter adapter;
    public Button addButton;
    public LinearLayout summaryLayout;
    public TextView emptyText;
    public int editPos=-1;
    public ImageView backButton;
    public String locationCode;
    public String currentDateString;
    public JSONObject supplierObject=new JSONObject();
    public FloatingActionButton saveGRA;
    public String activity_from="";
    public GRAListModel model;
    public String mode="Add";
    public TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graactivity);

        supplier=findViewById(R.id.supplier_name);
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        username=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        productNameText =findViewById(R.id.product_name);
        cartonPrice=findViewById(R.id.carton_price);
        unitPrice=findViewById(R.id.unit_price);
        cartonQtyValue=findViewById(R.id.ctn_qty);
        unitQtyValue=findViewById(R.id.unit_qty);
        qtyValue=findViewById(R.id.qty);
        pcsPerCarton=findViewById(R.id.pcs_percarton);
        subTotalValue=findViewById(R.id.sub_total);
        taxValueText=findViewById(R.id.tax);
        netTotalValue=findViewById(R.id.net_total);
        taxTitle=findViewById(R.id.tax_title);
        titleText=findViewById(R.id.title);
        summaryListView=findViewById(R.id.summaryListView);
        emptyText=findViewById(R.id.empty_text);
        summaryLayout=findViewById(R.id.summary_layout);
        addButton=findViewById(R.id.add_button);
      //  backButton=findViewById(R.id.back_button);
        saveGRA=findViewById(R.id.save_gra);
        cartList=new ArrayList<>();
        productNameText.setOnClickListener(this);
      // backButton.setOnClickListener(this);
        saveGRA.setOnClickListener(this);

        enableFields(false);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        // get Permission for the getting the Current location and Adding Image Permission to obtained

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        currentDateString = df.format(c);

        if (getIntent()!=null){
            activity_from=getIntent().getStringExtra("from");
        }

        if (activity_from.equals("edit_gra")){
            Gson gson = new Gson();
            model = gson.fromJson(getIntent().getStringExtra("GraDetails"), GRAListModel.class);
            supplier_code=model.getSupplierCode();
            supplier_name=model.getSupplierName();
            mode="Edit";
            titleText.setText("Edit - "+model.getGraNumber());
            supplier.setText(supplier_name+" - "+supplier_code);
            supplier.setEnabled(false);
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("CompanyCode",companyId);
                jsonObject.put("GRANo",model.getGraNumber());
                getGRADetails(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            try {
                JSONObject object=new JSONObject();
                object.put("CompanyCode",companyId);
                getAllSuppliers(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        init();
    }

    public void getGRADetails(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(GRAActivity.this);
        String url= Utils.getBaseUrl(this) +"PurchaseApi/GetGRAByCode?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("GRA_Details_URL:",url);
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("GRA Details loading....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        cartList=new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try{
                        progressDialog.dismiss();
                        Log.w("Response_GRA_Details:",response.toString());
                        subTotalValue.setText(twoDecimalPoint(Double.parseDouble(response.optString("SubTotal"))));
                        taxValueText.setText(twoDecimalPoint(Double.parseDouble(response.optString("Tax"))));
                        netTotalValue.setText(twoDecimalPoint(Double.parseDouble(response.optString("NetTotal"))));
                        JSONArray detailsArray=response.optJSONArray("GraDetails");
                        for (int i=0;i<detailsArray.length();i++){
                            JSONObject object=detailsArray.optJSONObject(i);
                            GRACartModel productModel=new GRACartModel();
                            productModel.setProductId(object.optString("ProductCode"));
                            productModel.setProductName(object.optString("ProductName"));
                            productModel.setUnitPrice(object.optString("UnitPrice"));
                            productModel.setPcsPerCarton(object.optString("PcsPerCarton"));
                            productModel.setCartonPrice(object.optString("CartonPrice"));
                            productModel.setCartonQty((int)Double.parseDouble(object.optString("CQty"))+"");
                            productModel.setUnitQty((int)Double.parseDouble(object.optString("LQty"))+"");
                            productModel.setNetQty((int)Double.parseDouble(object.optString("Qty"))+"");
                            productModel.setTotalValue(object.optString("SubTotal"));
                            productModel.setSubTotal(object.optString("SubTotal"));
                            productModel.setTaxValue(object.optString("Tax"));
                            productModel.setNetTotal(object.optString("NetTotal"));
                            cartList.add(productModel);
                        }
                        setSummaryListViewAdapter(cartList);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Do something when error occurred
                    pDialog.dismiss();
                    Log.e("Error_throwing:",error.toString());
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

    public void init(){
        unitPriceTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().equals(".")){
                    unitPrice.setText("0.");
                    unitPrice.setSelection(unitPrice.getText().length());
                }

                // Sathish 21/07/2020
                // if (isReverseCalculationEnabled){
                //  if (isPriceEdit){
                if (unitPrice.getText().toString().equals("")){
                    cartonPrice.removeTextChangedListener(cartonPriceTextWatcher);
                    cartonPrice.setText("");
                    cartonPrice.addTextChangedListener(cartonPriceTextWatcher);
                }
                if (!pcsPerCarton.getText().toString().equals("")){
                    double pcspercarton=Double.parseDouble(pcsPerCarton.getText().toString());
                    double unit_price=0;
                    if (unitPrice.getText().toString().equals("")){
                        unit_price=0;
                    }else {
                        unit_price=Double.parseDouble(unitPrice.getText().toString());
                    }
                    if (pcspercarton>1){
                        double cart_price=(pcspercarton * unit_price);
                        cartonPrice.removeTextChangedListener(cartonPriceTextWatcher);
                        cartonPrice.setText(twoDecimalPoint(cart_price));
                        cartonPrice.addTextChangedListener(cartonPriceTextWatcher);
                    }else {
                        if (!unitPrice.getText().toString().isEmpty()){
                            cartonPrice.removeTextChangedListener(cartonPriceTextWatcher);
                            cartonPrice.setText(twoDecimalPoint(Double.parseDouble(unitPrice.getText().toString())));
                            cartonPrice.addTextChangedListener(cartonPriceTextWatcher);
                        }else {
                            cartonPrice.removeTextChangedListener(cartonPriceTextWatcher);
                            cartonPrice.setText("0.00");
                            cartonPrice.addTextChangedListener(cartonPriceTextWatcher);
                        }
                    }
                    //  }
                    //  }
                    setCalculationView();
                }else {
                    setCalculationView();
                }
            }
        };
        unitPrice.addTextChangedListener(unitPriceTextWatcher);

        cartonPriceTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().equals(".")){
                    cartonPrice.setText("0.");
                    cartonPrice.setSelection(cartonPrice.getText().length());
                }

                // if (isReverseCalculationEnabled){
                //  if (isCartonPriceEdit){
                if (cartonPrice.getText().toString().equals("")){
                    unitPrice.removeTextChangedListener(unitPriceTextWatcher);
                    unitPrice.setText("");
                    unitPrice.addTextChangedListener(unitPriceTextWatcher);
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
                        unitPrice.removeTextChangedListener(unitPriceTextWatcher);
                        unitPrice.setText(twoDecimalPoint(unit_price));
                        unitPrice.addTextChangedListener(unitPriceTextWatcher);
                    }else {
                        if (!cartonPrice.getText().toString().isEmpty()){
                            unitPrice.removeTextChangedListener(unitPriceTextWatcher);
                            unitPrice.setText(twoDecimalPoint(Double.parseDouble(cartonPrice.getText().toString())));
                            unitPrice.addTextChangedListener(unitPriceTextWatcher);
                        }else {
                            unitPrice.removeTextChangedListener(unitPriceTextWatcher);
                            unitPrice.setText("0.00");
                            unitPrice.addTextChangedListener(unitPriceTextWatcher);
                        }
                    }
                    //   }
                    // }
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

                if (!qty.matches("")) {

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

                        unitQtyValue.removeTextChangedListener(lqtyTW);
                        unitQtyValue.setText("");
                        unitQtyValue.addTextChangedListener(lqtyTW);

                    }
                    setCalculationView();
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
                    unitQtyValue.requestFocus();
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
                if (cartonQtyValue.getText().toString().matches("0")) {
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
                        String lqty = unitQtyValue.getText().toString();
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
            }
        };
        cartonQtyValue.addTextChangedListener(cqtyTW);


        unitQtyValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (unitQtyValue.getText().toString().matches("0")) {
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
                beforeLooseQty = unitQtyValue.getText().toString();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (unitQtyValue.getText().toString().matches("0")) {
                    // looseQtyCalcPcsOne(0);
                    looseQtyCalc();
                } else {
                    looseQtyCalc();
                }

                int length = unitQtyValue.length();
                if (length == 0) {

                    if (unitQtyValue.getText().toString().matches("0")) {

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

            }

        };
        unitQtyValue.addTextChangedListener(lqtyTW);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!supplier.getText().toString().isEmpty()){
                    if (!productNameText.getText().toString().isEmpty()){
                        if (Double.parseDouble(netTotalValue.getText().toString())>0){
                            if (addButton.getTag().equals("Add")){
                                addOrUpdateItem("Add");
                            }else {
                                addOrUpdateItem("Update");
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"Enter the price or qty",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Select the Product",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Please Select the Supplier..",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void cartonQty() {
        String crtnQty = cartonQtyValue.getText().toString();

        if (!pcsPerCarton.getText().toString().matches("") && !crtnQty.matches("")) {

            double data = Double.parseDouble(crtnQty);
            int value = (int)data;
            double net_pcs_carton = Double.parseDouble(pcsPerCarton.getText().toString());
            int pcspercarton = (int) net_pcs_carton;
            int qty = 0;
            String lsQty = unitQtyValue.getText().toString();

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

    public void looseQtyCalc() {
        String crtnQty = cartonQtyValue.getText().toString();
        String lsQty = unitQtyValue.getText().toString();

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

    public int reverse(int number){
        Log.w("ReverseNumberis:",Math.abs(number) * -1+"");
        return Math.abs(number) * -1;
    }

    public double reverseValue(double number){
        Log.w("ReverseValueis:",Math.abs(number) * -1+"");
        return Math.abs(number) * -1;
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

                        unitQtyValue.removeTextChangedListener(lqtyTW);
                        unitQtyValue.setText(Utils.getQtyValue(loose));
                        unitQtyValue.addTextChangedListener(lqtyTW);

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


    public void setSupplierAdapter(ArrayList<String> supplierList){
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(GRAActivity.this, android.R.layout.select_dialog_item, supplierList){
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
        supplier.setThreshold(1);
        supplier.setAdapter(autoCompleteAdapter);
        setSupplier();
        supplier.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String[] supplierValue =adapterView.getItemAtPosition(i).toString().trim().split("-");
                supplier_name= supplierValue[0].trim();
                supplier_code= supplierValue[1].trim();
                supplier.clearFocus();
                Log.w("SupplierName:",supplier_name);
                Log.w("SupplierCode:",supplier_code);
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("CompanyCode",companyId);
                    jsonObject.put("SupplierCode",supplier_code);
                    getSupplierDetails(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getAllSuppliers(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(GRAActivity.this);
        String url= Utils.getBaseUrl(this) +"MasterApi/GetSupplier_All?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_Supplier_URL:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading Supplier, Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        supplierList=new ArrayList<>();
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try{
                        Log.w("Response_is_Supplier:",response.toString());
                        // Loop through the array elements
                        for(int i=0;i<response.length();i++){
                            // Get current json object
                            JSONObject object = response.getJSONObject(i);
                            if (object.optString("SupplierName")!=null && !object.optString("SupplierName").equals("null")){
                                String supplierName=object.optString("SupplierName");
                                String supplierCode=object.optString("SupplierCode");
                                supplierList.add(supplierName+" - "+supplierCode);
                            }
                        }
                        pDialog.dismiss();
                        setSupplierAdapter(supplierList);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Do something when error occurred
                    pDialog.dismiss();
                    Log.e("Error_throwing:",error.toString());
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

    public void getSupplierDetails(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(GRAActivity.this);
        String url= Utils.getBaseUrl(this) +"MasterApi/GetSupplier?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_Supplier_URL:",url);
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Supplier Details loading....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try{
                        progressDialog.dismiss();
                        Log.w("Response_Details:",response.toString());
                        // Loop through the array elements
                        supplierObject=response;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Do something when error occurred
                    pDialog.dismiss();
                    Log.e("Error_throwing:",error.toString());
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

    public void createGRAJson() throws JSONException {
        //  {"GRAHeader":{"GRANo":"","Mode":"I","GRADateString":"09-24-2021","SupplierCode":"00001","SupplierName":"Winapp Testing",
        //  "ContactPerson":"mahudoomfai","GroupGRNNo":"","Address1":"syed","Address2":"road","Address3":"singapore",
        //  "PhoneNo":"98670074","InvoiceNo":"","Remarks":"","CurrencyName":"singapore Dollar","Tax":0.52,"SubTotal":7.50,
        //  "Total":7.50,"NetTotal":8.02,"ItemDiscount":0.00,"BillDIscount":0.00,"FTotal":7.50,"FItemDiscount":0,"FBillDiscount":0,
        //  "FSubTotal":7.50,"FTax":0,"FNetTotal":7.50,"TotalDiscount":0,"BillDiscountPercentage":0,"DONo":"",
        //  "InvoiceDateString":"09-24-2021","DoDateString":"09-24-2021","RoundTax":0,"RoundOff":0,"ContainerNo":"",
        //  "HaveTax":"true","TaxType":"E","TaxPerc":"7.0","TaxCode":2,"CompanyShortCode":"","CurrencyCode":"SGD",
        //  "CurrencyValue":"1.000","CurrencyRate":1,"Status":0,"PostalCode":"640210","CreateUser":"winapp","ModifyUser":"winapp",
        //  "Email":"mahudoomsg.winapp@gmail.com","FaxNo":"","CompanyCode":"1","LocationCode":"HQ"},

        JSONObject rootJsonObject=new JSONObject();
        JSONObject headerObject=new JSONObject();
        JSONArray detailArray=new JSONArray();
        JSONObject detailObject=new JSONObject();

        if (mode.equals("Add")){
            headerObject.put("GRANo","");
            headerObject.put("Mode","I");
        }else {
            headerObject.put("GRANo",model.getGraNumber());
            headerObject.put("Mode","E");
        }
        headerObject.put("GRADateString",currentDateString);
        headerObject.put("SupplierCode",supplier_code);
        headerObject.put("SupplierName",supplier_name);
        headerObject.put("ContactPerson",supplierObject.optString("ContactPerson"));
        headerObject.put("GroupGRNNo","");
        headerObject.put("Address1",supplierObject.optString("Address1"));
        headerObject.put("Address2",supplierObject.optString("Address2"));
        headerObject.put("Address3",supplierObject.optString("Address3"));
        headerObject.put("PhoneNo",supplierObject.optString("PhoneNo"));
        headerObject.put("InvoiceNo","");
        headerObject.put("Remarks","");
        headerObject.put("CurrencyName","Singapore Dollar");
        headerObject.put("Tax",taxValueText.getText().toString());
        headerObject.put("SubTotal",subTotalValue.getText().toString());
        headerObject.put("Total",subTotalValue.getText().toString());
        headerObject.put("NetTotal",netTotalValue.getText().toString());
        headerObject.put("ItemDiscount","0.00");
        headerObject.put("BillDIscount","0.00");
        headerObject.put("FTax",taxValueText.getText().toString());
        headerObject.put("FSubTotal",subTotalValue.getText().toString());
        headerObject.put("FTotal",subTotalValue.getText().toString());
        headerObject.put("FNetTotal",netTotalValue.getText().toString());
        headerObject.put("FItemDiscount","0.00");
        headerObject.put("FBillDiscount","0.00");
        headerObject.put("DONo","");
        headerObject.put("InvoiceDateString","");
        headerObject.put("DoDateString","");
        headerObject.put("RoundTax",0);
        headerObject.put("RoundOff",0);
        headerObject.put("ContainerNo","");
        headerObject.put("HaveTax",true);
        headerObject.put("TaxType",supplierObject.optString("TaxType"));
        headerObject.put("TaxPerc",supplierObject.optString("TaxPerc"));
        headerObject.put("TaxCode",supplierObject.optString("TaxCode"));
        headerObject.put("CompanyShortCode","");
        headerObject.put("CurrencyCode","SGD");
        headerObject.put("CurrencyValue","1.000");
        headerObject.put("CurrencyRate",1);
        headerObject.put("Status",0);
        headerObject.put("PostalCode",supplierObject.optString("PostalCode"));
        headerObject.put("LocationCode",locationCode);
        headerObject.put("CreateUser",username);
        headerObject.put("ModifyUser",username);
        headerObject.put("Email",supplierObject.optString("Email"));
        headerObject.put("FaxNo",supplierObject.optString("FaxNo"));
        headerObject.put("CompanyCode",companyId);

        //  "GRADetail":[{"CompanyCode":"1","GRANo":"","slNo":1,"ProductCode":"0000002","ProductName":"ORANGE","CQty":5,"LQty":0,
        //  "Qty":5,"PcsPerCarton":1,"UnitPrice":1.50,"CartonPrice":1.50,"Total":7.50,"ItemDiscountPercentage":0,"Tax":0,
        //  "SubTotal":7.50,"NetTotal":7.50,"TaxType":"E","TaxPerc":7.0,"ItemDiscount":0,"FItemDiscount":0,"FKiloPrice":0.0,
        //  "KiloPrice":0.0,"ReturnQty":0,"BillDiscount":0,"TotalDiscount":0,"FTotal":7.5,"TaxCode":2,"UOMCode":"PCS",
        //  "FBillDiscount":0,"FSubTotal":7.50,"FTax":0,"FNetTotal":7.50,"FPrice":"1.50","ItemRemarks":"","PONO":"",
        //  "LocationCode":"HQ","ProductBarcode":"","CreateUser":"winapp","ModifyUser":"winapp","POslNo":0,"ActualCQty":0,
        //  "ActualLQty":0,"ActualQty":0,"FOCCQty":0}]}

        int index=1;
        for (GRACartModel model:adapter.getCartList()){
            detailObject=new JSONObject();
            detailObject.put("CompanyCode",companyId);
            detailObject.put("GRANo","");
            detailObject.put("slNo",index);
            detailObject.put("ProductCode",model.getProductId());
            detailObject.put("ProductName",model.getProductName());
            detailObject.put("CQty",model.getCartonQty());
            detailObject.put("LQty",model.getUnitQty());
            detailObject.put("Qty",model.getNetQty());
            detailObject.put("PcsPerCarton",(int)Double.parseDouble(model.getPcsPerCarton()));
            detailObject.put("UnitPrice",model.getUnitPrice());
            detailObject.put("CartonPrice",model.getCartonPrice());
            detailObject.put("Total",model.getTotalValue());
            detailObject.put("ItemDiscountPercentage",0);
            detailObject.put("Tax",model.getTaxValue());
            detailObject.put("SubTotal",model.getSubTotal());
            detailObject.put("NetTotal",model.getNetTotal());
            detailObject.put("FTax",model.getTaxValue());
            detailObject.put("FSubTotal",model.getSubTotal());
            detailObject.put("FNetTotal",model.getNetTotal());
            detailObject.put("FPrice",0.0);
            detailObject.put("TaxType",supplierObject.optString("TaxType"));
            detailObject.put("TaxCode",supplierObject.optString("TaxCode"));
            detailObject.put("TaxPerc",supplierObject.optString("TaxPerc"));
            detailObject.put("ItemDiscount",0);
            detailObject.put("FItemDiscount",0);
            detailObject.put("FKiloPrice",0.0);
            detailObject.put("KiloPrice",0.0);
            detailObject.put("ReturnQty",0);
            detailObject.put("BillDiscount",0);
            detailObject.put("TotalDiscount",0);
            detailObject.put("FTotal",model.getTotalValue());
            detailObject.put("UOMCode",model.getUomCode());
            detailObject.put("FBillDiscount",0);
            detailObject.put("ItemRemarks","");
            detailObject.put("PONO","");
            detailObject.put("LocationCode",locationCode);
            detailObject.put("ProductBarcode","");
            detailObject.put("CreateUser",username);
            detailObject.put("ModifyUser",username);
            detailObject.put("POslNo",0);
            detailObject.put("ActualCQty",0);
            detailObject.put("ActualLQty",0);
            detailObject.put("ActualQty",0);
            detailObject.put("FOCCQty",0);

            detailArray.put(detailObject);
        }
        rootJsonObject.put("GRAHeader",headerObject);
        rootJsonObject.put("GRADetail",detailArray);

        Log.w("GivenSaveJsonGRA:",rootJsonObject.toString());
        savePurchaseInvoice(rootJsonObject);

    }


    public void savePurchaseInvoice(JSONObject jsonBody){
        try {
            pDialog = new SweetAlertDialog(GRAActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Processing Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(GRAActivity.this);
            Log.w("GRAJson:",jsonBody.toString());
            String URL= Utils.getBaseUrl(this)+"PurchaseApi/SavePurchaseInvoice";
            Log.w("GRAURL:",URL);
            StringRequest saveStockTakeRequest = new StringRequest(Request.Method.POST, URL, response -> {
                Log.w("GRA_Saveresponse:",response.toString());
                //  {"IsSaved":true,"Result":"~0000184"}
                try {
                    JSONObject object=new JSONObject(response);
                    boolean isSaved=object.optBoolean("IsSaved");
                    String result=object.optString("Result");
                    if (isSaved && !result.isEmpty()){
                        if (mode.equals("Edit")){
                            Toast.makeText(getApplicationContext(),"Updated Successfully...",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(),"Saved Successfully...",Toast.LENGTH_SHORT).show();
                        }
                        Intent intent=new Intent();
                        intent.putExtra("key","Refresh");
                        setResult(RESULT_OK,intent);
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(),"Error is Saving StockTake try again...",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            }, error -> {
                Log.w("ErrorResponse:",error.toString());
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
            saveStockTakeRequest.setRetryPolicy(new RetryPolicy() {
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
            requestQueue.add(saveStockTakeRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSupplier(){
        if (supplier_code!=null && !supplier_code.isEmpty()){
            for (String s:supplierList){
                if (s.contains(supplier_code)){
                    supplier.setText(s);
                    break;
                }
            }
        }
    }

    public void setCalculationView() {
        try {
            double taxAmount = 0.0, netTotal = 0.0;
            double taxAmount1 = 0.0, netTotal1 = 0.0;
            double return_qty=0.0;
            double pcspercarton=0.0;
            double cqtyCalc=0;
            double lqtyCalc=0;

            //   SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
            //  String selectCustomerId = sharedPreferences.getString("customerId", "");
            //  if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
            //customerDetails = dbHelper.getCustomer(selectCustomerId);
            // }
            String taxValue=supplierObject.optString("TaxPerc");
            String taxType=supplierObject.optString("TaxType");
            Log.w("TaxType12:",taxType);
            Log.w("TaxValue12:",taxValue);
            String lPrice = unitPrice.getText().toString();
            String cPrice = cartonPrice.getText().toString();
            String cqty = cartonQtyValue.getText().toString();
            String lqty = unitQtyValue.getText().toString();

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

            cqtyCalc = Double.parseDouble(cqty);
            lqtyCalc = Double.parseDouble(lqty);
            double tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc);

            String Prodtotal = twoDecimalPoint(tt);

            double subTotal = 0.0;

            String itemDisc = "0.00";
            if (!itemDisc.matches("")) {
                double itmDisc = Double.parseDouble(itemDisc);
                subTotal = tt - itmDisc;
            } else {
                subTotal = tt;
            }

            String sbTtl = twoDecimalPoint(subTotal);

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
                        subTotalValue.setText(twoDecimalPoint(subTotal));
                    } else {

                        taxAmount = (tt * taxValueCalc) / 100;
                        String prodTax = twoDecimalPoint(taxAmount);
                        taxValueText.setText("" + prodTax);

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        taxTitle.setText("GST ( Exc )");
                        netTotalValue.setText("" + ProdNetTotal);
                        subTotalValue.setText(twoDecimalPoint(subTotal));
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
                        subTotalValue.setText(twoDecimalPoint(sub_total));
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
                        subTotalValue.setText(twoDecimalPoint(sub_total));
                    }

                } else if (taxType.matches("Z")) {

                    taxValueText.setText("0.0");
                    if (!itemDisc.matches("")) {
                        // netTotal1 = subTotal + taxAmount;
                        netTotal1 = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        netTotalValue.setText("" + ProdNetTotal);
                        subTotalValue.setText(twoDecimalPoint(subTotal));
                        taxTitle.setText("GST ( Zero )");
                    } else {
                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        netTotalValue.setText("" + ProdNetTotal);
                        subTotalValue.setText(twoDecimalPoint(subTotal));
                        taxTitle.setText("GST ( Zero )");
                    }

                } else {
                    taxValueText.setText("0.0");
                    netTotalValue.setText("" + Prodtotal);
                    subTotalValue.setText(twoDecimalPoint(subTotal));
                    taxTitle.setText("GST ( Zero )");
                }

            } else if (taxValue.matches("")) {
                taxValueText.setText("0.0");
                netTotalValue.setText("" + Prodtotal);
                subTotalValue.setText(twoDecimalPoint(subTotal));
                taxTitle.setText("GST ( Zero )");
            } else {
                taxValueText.setText("0.0");
                netTotalValue.setText("" + Prodtotal);
                subTotalValue.setText(twoDecimalPoint(subTotal));
                taxTitle.setText("GST ( Zero )");
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.product_name){
            showProductsAlertDialog();
        }else if (v.getId()==android.R.id.home){
            if (adapter!=null){
                if (adapter.getCartList().size()>0){
                    showAlertDialog();
                }else {
                    finish();
                }
            }else {
                finish();
            }
        }else if (v.getId()==R.id.save_gra){
            if (adapter!=null && adapter.getCartList().size()>0){
                showAlertDialogForSave();
            }else {
                Toast.makeText(getApplicationContext(),"Add product(s) to save",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showAlertDialogForSave(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Information...!");
        if (mode.equals("Edit")){
            builder1.setMessage("Are you sure want to Update ?");
        }else {
            builder1.setMessage("Are you sure want to save ?");
        }
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            createGRAJson();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    public void showAlertDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Warning...!");
        builder1.setMessage("Your Data will be lost, Are you sure want to back?.");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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

    @Override
    public void onBackPressed() {
        if (adapter!=null){
            if (adapter.getCartList().size()>0){
                showAlertDialog();
            }else {
                finish();
            }
        }else {
            finish();
        }
    }

    public void showProductsAlertDialog(){
        String[] productArray = new String[AppUtils.getProductsList().size()];
        int index=0;
        for (ProductsModel brand :AppUtils.getProductsList()){
            productArray[index]= brand.getProductName()+"-"+brand.getProductCode();
            index++;
        }
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(GRAActivity.this);
        mBuilder.setTitle("Choose a Product");
        mBuilder.setCancelable(false);
        mBuilder.setSingleChoiceItems(productArray, selectedProduct, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                productNameText.setText(productArray[i]);
                product_name = productArray[i];
                String[] brandValues= productArray[i].trim().split("-");
                product_code =brandValues[1];
                selectedProduct =i;
                setProductDetails(product_code);
                dialogInterface.dismiss();
                enableFields(true);
                qtyValue.requestFocus();
            }
        });
        mBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedProduct =-1;
                dialogInterface.dismiss();
            }
        });
      /*  mBuilder.setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedProduct =-1;
                dialogInterface.dismiss();
            }
        });*/
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void enableFields(boolean enable){
        try {
            cartonQtyValue.setEnabled(enable);
            unitQtyValue.setEnabled(enable);
            qtyValue.setEnabled(enable);
            cartonPrice.setEnabled(enable);
            unitPrice.setEnabled(enable);
        }catch (Exception e){}
    }

    public void setProductDetails(String product_code){
        for (ProductsModel product :AppUtils.getProductsList()){
            if (product.getProductCode().equals(product_code)){
                cartonPrice.setText(twoDecimalPoint(Double.parseDouble(product.getWholeSalePrice())));
                unitPrice.setText(twoDecimalPoint(Double.parseDouble(product.getUnitCost())));
                pcsPerCarton.setText(product.getPcsPerCarton());
                qtyValue.requestFocus();
                //  addItem(product);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addOrUpdateItem(String action) {
        try {
            if (action.equals("Add")){
                GRACartModel productModel=new GRACartModel();
                productModel.setProductId(product_code);
                productModel.setProductName(product_name);
                productModel.setUnitPrice(unitPrice.getText().toString());
                productModel.setPcsPerCarton(pcsPerCarton.getText().toString());
                productModel.setCartonPrice(cartonPrice.getText().toString());
                productModel.setCartonQty(cartonQtyValue.getText().toString());
                productModel.setUnitQty(unitQtyValue.getText().toString());
                productModel.setNetQty(qtyValue.getText().toString());
                productModel.setTotalValue(subTotalValue.getText().toString());
                productModel.setSubTotal(subTotalValue.getText().toString());
                productModel.setTaxValue(taxValueText.getText().toString());
                productModel.setNetTotal(netTotalValue.getText().toString());
                if (isProductAlreadyExist(product_code)){
                    Toast.makeText(getApplicationContext(),"Product Updated..!",Toast.LENGTH_SHORT).show();
                    cartList.set(productIndex(product_code),productModel);
                    adapter.notifyDataSetChanged();
                }else {
                    cartList.add(productModel);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    } else {
                        getCartList();
                    }
                }
            }else {
                GRACartModel productModel=new GRACartModel();
                productModel.setProductId(product_code);
                productModel.setProductName(product_name);
                productModel.setUnitPrice(unitPrice.getText().toString());
                productModel.setPcsPerCarton(pcsPerCarton.getText().toString());
                productModel.setCartonPrice(cartonPrice.getText().toString());
                productModel.setCartonQty(cartonQtyValue.getText().toString());
                productModel.setUnitQty(unitQtyValue.getText().toString());
                productModel.setNetQty(qtyValue.getText().toString());
                productModel.setTotalValue(subTotalValue.getText().toString());
                productModel.setSubTotal(subTotalValue.getText().toString());
                productModel.setTaxValue(taxValueText.getText().toString());
                productModel.setNetTotal(netTotalValue.getText().toString());
                cartList.set(editPos,productModel);
                if (adapter!=null){
                    adapter.notifyDataSetChanged();
                }else {
                    getCartList();
                }
            }
            clearFields();
            clearFocus();
        }catch (Exception e){}
    }

    public boolean isProductAlreadyExist(String product_code){
        boolean isCheck=false;
        if (adapter!=null){
            for (GRACartModel model:adapter.getCartList()){
                if (model.getProductId().equals(product_code)){
                    isCheck=true;
                    break;
                }
            }
        }
        return isCheck;
    }

    public int productIndex(String product_code){
        int index=0;
        if (adapter!=null){
            for (GRACartModel model:adapter.getCartList()){
                if (model.getProductId().equals(product_code)){
                    break;
                }
                index++;
            }
        }
        return index;
    }

    public void clearFields(){
        try {
            productNameText.setText("");
            cartonQtyValue.setText("");
            unitQtyValue.setText("");
            qtyValue.setText("");
            cartonPrice.setText("");
            unitPrice.setText("");
            addButton.setText("Add");
            addButton.setTag("Add");
            editPos=-1;
            pcsPerCarton.setText("0");
            selectedProduct=-1;
            // Calculate the Overall products Details
            setNetTotalValue();
            enableFields(false);
        }catch (Exception e){}
    }

    private void getCartList(){
        setSummaryListViewAdapter(cartList);
    }

    private void setSummaryListViewAdapter(ArrayList<GRACartModel> cartList){
        if (cartList.size()>0){
            emptyText.setVisibility(View.GONE);
            summaryLayout.setVisibility(View.VISIBLE);
            summaryListView.setVisibility(View.VISIBLE);
            summaryListView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GRAActivity.this);
            summaryListView.setLayoutManager(layoutManager);
            adapter = new GRATableViewAdapter(this, cartList,this);
            summaryListView.setAdapter(adapter);
        }else {
            emptyText.setVisibility(View.VISIBLE);
            summaryLayout.setVisibility(View.GONE);
            summaryListView.setVisibility(View.GONE);
            // tvStatus.setText("No Stock Take Item found...!");
        }
        if (mode.equals("Edit")){
            JSONObject object =new JSONObject();
            try {
                object.put("CompanyCode",companyId);
                object.put("SupplierCode",supplier_code);
                getSupplierDetails(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void clearFocus(){
        View current = getCurrentFocus();
        if (current != null) current.clearFocus();
    }

    @Override
    public void editProduct(GRACartModel model,int editPosition) {
        try {
            editPos=editPosition;
            pcsPerCarton.setText(model.getPcsPerCarton());
            product_code=model.getProductId();
            product_name=model.getProductName();
            cartonPrice.setText(model.getCartonPrice());
            unitPrice.setText(model.getUnitPrice());
            cartonQtyValue.setText(model.getCartonQty());
            unitQtyValue.setText(model.getUnitQty());
            qtyValue.setText(model.getNetQty());
            productNameText.setText(model.getProductName());
            addButton.setText("Update");
            addButton.setTag("update");
            enableFields(true);
        }catch (Exception e){}
    }

    @Override
    public void deleteProduct(int editPos) {
        removeItem(editPos);
    }

    private void removeItem(int actualPosition) {
        cartList.remove(actualPosition);
        adapter.notifyItemRemoved(actualPosition);
        adapter.notifyItemRangeChanged(actualPosition, cartList.size());
        if (cartList.size()==0){
            summaryLayout.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
            adapter=null;
        }
        clearFields();
        setNetTotalValue();
    }

    public void setNetTotalValue(){
        if (adapter!=null){
            double total_value=0.0;
            double tax_value=0.0;
            double net_total_value=0.0;
            for (GRACartModel model:adapter.getCartList()){
                total_value+=Double.parseDouble(model.getTotalValue());
                tax_value+=Double.parseDouble(model.getTaxValue());
                net_total_value+=Double.parseDouble(model.getNetTotal());
            }
            subTotalValue.setText(twoDecimalPoint(total_value));
            taxValueText.setText(twoDecimalPoint(tax_value));
            netTotalValue.setText(twoDecimalPoint(net_total_value));
        }
    }

}