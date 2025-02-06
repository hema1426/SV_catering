package com.winapp.svcatering.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.CustomerNameAdapter;
import com.winapp.svcatering.adapter.LoadMoreProductsAdapter;
import com.winapp.svcatering.adapter.ProductAdapterLoadMore;
import com.winapp.svcatering.adapter.ProductsAdapter;
import com.winapp.svcatering.adapter.SortAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.fragments.ProductsFragment;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.CustomRecyclerView;
import com.winapp.svcatering.utils.GridSpacingItemDecoration;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

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

import static com.winapp.svcatering.activity.AddInvoiceActivityOld.activityFrom;
import static com.winapp.svcatering.utils.Utils.fourDecimalPoint;
import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

public class ShowCatagoryActivity extends AppCompatActivity implements ProductAdapterLoadMore.Callbacks {

    private View progressBarLayout;
    private ProductsAdapter productsAdapter;
    private ProductAdapterLoadMore productsAdapterNew;
    private RecyclerView productsView;
    SweetAlertDialog pDialog;
    public static ArrayList<ProductsModel> productList;
    public static ArrayList<ProductsModel> newProductList;
    private RecyclerView lettersRecyclerview;
    SortAdapter adapter;
    ArrayList<String> letters;
    private LinearLayout emptyLayout;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LoadMoreProductsAdapter moreProductsAdapter;
    FloatingActionButton sortButton;
    HashMap<String ,String> user;
    SessionManager session;
    String companyCode;
    int pageNo=1;
    GridLayoutManager mLayoutManager;
    String sortLetter;
    public static DBHelper dbHelper;
    public static BottomSheetBehavior behavior;

    private ImageView mainImage;
    private TextView itemName;
    private TextView availability;
    private TextView netPrice;
    private TextView ctnPrice;
    private TextView ctnQty;
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
    private Button addToCart;
    private TextView ctnQtyText;
    private TextView cartonText;
    private TextView unitPrice;
    double net_amount=0.0;
    double carton_amount=0.0;
    double loose_amount=0.0;
    int cnQty=0;
    int lqty=0;
    double pcspercarton=0;
    // Customer details arraylist
    ArrayList<CustomerDetails> customerDetails;
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
    ProductsModel model;
    private String negativeStockStr = "No";

    LinearLayout transLayout;
    ImageView cancelSheet;
    View portraitLayout;
    View landscapeLayout;
    SortAdapter sortAdapter;
    String catagoriesId;
    String catagoryName;
    String locationCode;
    String userName;
    LinearLayout filterLayout;
    String brandId;
    String brandName;
    String fromValue;
    String departmentId;
    String departmentName;
    TextView qtyTextView;

    public static TextView textCartItemCount;
    public static int mCartItemCount = 0;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor customerPredEdit;
    public static TextView selectCustomer;
    public ArrayList<CustomerModel> customerList;
    private RecyclerView customerView;
    private View progressLayout;
    private LinearLayout rootLayout;
    public CustomerNameAdapter customerNameAdapter;
    public TextView dateText;
    private TextView userNametext;

    public EditText customerNameEdittext;
    public Button btnCancel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_catagory);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.w("activity_cg",getClass().getSimpleName().toString());

        session=new SessionManager(this);
        dbHelper=new DBHelper(this);
        user=session.getUserDetails();
        sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        negativeStockStr=user.get(SessionManager.KEY_NEGATIVE_STOCK);

        progressBarLayout = findViewById(R.id.progress_layout);
        productsView =findViewById(R.id.categoriesView);
        progressBarLayout.setVisibility(View.GONE);
        lettersRecyclerview=findViewById(R.id.sorting_letters);
        emptyLayout=findViewById(R.id.empty_layout);
        sortButton=findViewById(R.id.fab);
        customerDetails=dbHelper.getCustomer();
        mainImage=findViewById(R.id.item_image);
        itemName=findViewById(R.id.item_name);
        availability=findViewById(R.id.availabilty);
        netPrice =findViewById(R.id.price);
        ctnPrice=findViewById(R.id.ctn_price);
        ctnQty=findViewById(R.id.ctn_qty);
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
        transLayout=findViewById(R.id.trans_layout);
        cancelSheet=findViewById(R.id.cancel_sheet);
        filterLayout=findViewById(R.id.filter_layout);
        qtyTextView=findViewById(R.id.qty);
        selectCustomer = findViewById(R.id.select_customer);
        progressLayout=findViewById(R.id.customer_progress);
        customerView = findViewById(R.id.customerView);
        rootLayout=findViewById(R.id.rootLayout);
        dateText = findViewById(R.id.date);
        userNametext = findViewById(R.id.user_name);

        userNametext.setText(userName);

        customerNameEdittext = findViewById(R.id.customer_search);
        btnCancel = findViewById(R.id.btn_cancel);

        String customerId = sharedPreferences.getString("customerId", "");
        if (customerId != null && !customerId.equals("empty") && !customerId.isEmpty()) {
            customerDetails = dbHelper.getCustomer(customerId);
            selectCustomer.setText(customerDetails.get(0).getCustomerName());
        }else {
            selectCustomer.setText("Select Customer");
        }

        //  portraitLayout=view.findViewById(R.id.description_layout_portrait);
        //  landscapeLayout=view.findViewById(R.id.description_layout_land);


       // intent.putExtra("departmentId",deparment.getDepartmentCode());
       // intent.putExtra("departmentName",deparment.getDepartmentName());
       // intent.putExtra("from","department");

        customerList=dbHelper.getAllCustomers();
        if (customerList!=null && customerList.size()>0){
            setAdapter(customerList);
        }else {
           // getCustomers();
        }


        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        dateText.setText(formattedDate);

        if (getIntent() !=null){
            if (getIntent().getStringExtra("from").equals("brand")){
                brandId=getIntent().getStringExtra("brandId");
                brandName=getIntent().getStringExtra("brandName");
                fromValue=getIntent().getStringExtra("from");
                getSupportActionBar().setTitle(brandName);
            }else if (getIntent().getStringExtra("from").equals("department")){
                departmentId=getIntent().getStringExtra("departmentId");
                departmentName=getIntent().getStringExtra("departmentName");
                fromValue=getIntent().getStringExtra("from");
                getSupportActionBar().setTitle(departmentName);
            } else {
                catagoriesId=getIntent().getStringExtra("catagoriesId");
                catagoryName=getIntent().getStringExtra("catagoryName");
                fromValue=getIntent().getStringExtra("from");
                getSupportActionBar().setTitle(catagoryName);
            }

        }

        productList=new ArrayList<>();
        // productList=dbHelper.getAllCatalogProducts();

      /*  JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("CompanyCode",companyCode);
            jsonObject.put("LocationCode",locationCode);
            jsonObject.put("CategoryCode",catagoriesId);
            jsonObject.put("PageSize",50);
            jsonObject.put("PageNo",pageNo);
            getAllProducts(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        if (productList!=null){
            if (productList.size()>0){
                populateCategoriesData();
            }else {
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("CompanyCode",companyCode);
                    jsonObject.put("LocationCode",locationCode);
                    if (fromValue.equals("brand")){
                        jsonObject.put("BrandCode",brandId);
                    }else if (fromValue.equals("department")){
                        jsonObject.put("DepartmentCode",departmentId);
                    }else {
                        jsonObject.put("CategoryCode",catagoriesId);
                    }
                    jsonObject.put("PageSize",5000);
                    jsonObject.put("PageNo",pageNo);
                    getAllProducts(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else {
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("CompanyCode",companyCode);
                jsonObject.put("LocationCode",locationCode);
                if (fromValue.equals("brand")){
                    jsonObject.put("BrandCode",brandId);
                }else if (fromValue.equals("department")){
                    jsonObject.put("DepartmentCode",departmentId);
                }else {
                    jsonObject.put("CategoryCode",catagoriesId);
                }
                jsonObject.put("PageSize",5000);
                jsonObject.put("PageNo",pageNo);
                getAllProducts(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int spanCount=0;
        int dp=0;
        if (Utils.isTablet(this)){
            spanCount=3;
            dp=10;
        }else {
            spanCount=2;
            dp=10;
        }
        int mNoOfColumns = Utils.calculateNoOfColumns(this, 180);
        // GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, mNoOfColumns);
        mLayoutManager = new GridLayoutManager(this, mNoOfColumns);
        productsView.setLayoutManager(mLayoutManager);
        // categoriesView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        productsView.addItemDecoration(new GridSpacingItemDecoration(spanCount, GridSpacingItemDecoration.dpToPx(this, dp), true));
        productsView.setItemAnimator(new DefaultItemAnimator());
        // define the sorting letters
        lettersRecyclerview.setHasFixedSize(true);
        // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        lettersRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        sortAdapter = new SortAdapter(Utils.getSorting(), new SortAdapter.CallBack() {
            @Override
            public void sortProduct(String letter) {
                if (letter.equals("All")){
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("CompanyCode",companyCode);
                        jsonObject.put("LocationCode",locationCode);
                        if (fromValue.equals("brand")){
                            jsonObject.put("BrandCode",brandId);
                        }else if (fromValue.equals("department")){
                            jsonObject.put("DepartmentCode",departmentId);
                        }else {
                            jsonObject.put("CategoryCode",catagoriesId);
                        }
                        jsonObject.put("PageSize",5000);
                        jsonObject.put("PageNo",pageNo);
                        getAllProducts(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    };
                }else {
                    filter(letter);
                }
            }
        });
        lettersRecyclerview.setAdapter(sortAdapter);

        // Setting the sorting
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View menuItemView = findViewById(R.id.fab);
                PopupMenu popupMenu = new PopupMenu(ShowCatagoryActivity.this, menuItemView);
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

        if (activityFrom!=null){
            if (activityFrom.equals("InvoiceEdit")
                    || activityFrom.equals("SalesEdit")
                    || activityFrom.equals("ConvertInvoice")
                    || activityFrom.equals("ReOrderInvoice")
                    || activityFrom.equals("ReOrderSales")
            ){
                dbHelper.removeAllItems();
                Utils.clearCustomerSession(this);
            }
        }

        setRecyclerViewSettings();

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
                      //  int orientation = getActivity().getResources().getConfiguration().orientation;
                        transLayout.setVisibility(View.VISIBLE);
                       // MainHomeActivity.customerTransLayout.setVisibility(View.VISIBLE);
                       // MainHomeActivity.tabTransLayout.setVisibility(View.VISIBLE);
                       /* if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            // code for portrait mode
                            portraitLayout.setVisibility(View.VISIBLE);
                            landscapeLayout.setVisibility(View.GONE);
                        } else {
                            portraitLayout.setVisibility(View.GONE);
                            landscapeLayout.setVisibility(View.VISIBLE);
                            // code for landscape mode
                        }*/
                        //    MainHomeActivity.transLayout.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        transLayout.setVisibility(View.GONE);
                        ctnQtyValue.setText("0");
                        pcsQtyValue.setText("0");
                        taxTextView.setText("0.0000");
                        totalTextView.setText("0.00");
                        focEditText.setText("");
                        exchangeEditext.setText("");
                        returnEditext.setText("");
                        discountEditext.setText("");
                        netPrice.setText("0.00");
                        customerNameEdittext.setText("");
                        Utils.hideKeyboard(ShowCatagoryActivity.this,rootLayout);
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
                double stock=Double.parseDouble(availability.getText().toString());
                double net_qty=Double.parseDouble(qtyTextView.getText().toString());
                if (net_qty + 2  > stock){
                    if (negativeStockStr.equalsIgnoreCase("No")) {
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
                double stock=Double.parseDouble(availability.getText().toString());
                double net_qty=Double.parseDouble(qtyTextView.getText().toString());
                if (net_qty + 1  > stock){
                    if (negativeStockStr.equalsIgnoreCase("No")) {
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


        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String focType="pcs";
                String exchangeType="pcs";
                String returnType="pcs";
                String carton_price="0.0";
                String unit_price="0.0";
                String lPriceCalc="0";
                String loose_qty="0";
                String ctn_qty="0";
                String cartonprice="0";
                String discount="0";
                String return_qty="0";

                if (focSwitch.isChecked()){
                    focType="ctn";
                }
                if (exchangeSwitch.isChecked()){
                    exchangeType="ctn";
                }
                if (returnSwitch.isChecked()){
                    returnType="ctn";
                }

                if (pcsQtyValue.getText().toString().isEmpty() && ctnQtyValue.getText().toString().isEmpty()){
                    isQtyEntered=false;
                }else isQtyEntered= !pcsQtyValue.getText().toString().equals("0") || !ctnQtyValue.getText().toString().equals("0");


                if (!ctnPrice.getText().toString().isEmpty()){
                    carton_price=ctnPrice.getText().toString();
                }

                if (!unitPrice.getText().toString().isEmpty()){
                    unit_price=unitPrice.getText().toString();
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
                    cartonprice=ctnPrice.getText().toString();
                }

                if (!discountEditext.getText().toString().isEmpty()){
                    discount=discountEditext.getText().toString();
                }


                double return_amt=(Double.parseDouble(return_qty)*Double.parseDouble(unit_price));
                double total=(Double.parseDouble(ctn_qty) * Double.parseDouble(cartonprice)) + (Double.parseDouble(loose_qty) * Double.parseDouble(unit_price));
                double sub_total=total-return_amt-Double.parseDouble(discount);



                if (isQtyEntered){
                    boolean status= dbHelper.insertCart(
                            model.getProductCode(),
                            model.getProductName(),
                            ctnQtyValue.getText().toString(),
                            pcsQtyValue.getText().toString(),
                            model.getUnitCost(),
                            model.getProductImage(),
                            netPrice.getText().toString(),
                            model.getWeight(),
                            carton_price,
                            unit_price,
                            ctnQty.getText().toString(),
                            taxTextView.getText().toString(),
                            String.valueOf(sub_total),
                            customerDetails.get(0).getTaxType(),
                            focEditText.getText().toString(),
                            focType,
                            exchangeEditext.getText().toString(),
                            exchangeType,
                            discountEditext.getText().toString(),
                            returnEditext.getText().toString(),
                            returnType,"",String.valueOf(total),availability.getText().toString(),"",
                            "0.00",availability.getText().toString());
                    if (status){
                        Toast.makeText(ShowCatagoryActivity.this,"Product Added Successfully",Toast.LENGTH_LONG).show();
                        viewCloseBottomSheet();
                        ctnQtyValue.setText("0");
                        pcsQtyValue.setText("0");
                        taxTextView.setText("0.0000");
                        totalTextView.setText("0.00");
                        focEditText.setText("");
                        exchangeEditext.setText("");
                        returnEditext.setText("");
                        discountEditext.setText("");
                        netPrice.setText("0.00");
                        MainHomeActivity.setupBadge(ShowCatagoryActivity.this);
                        Utils.refreshActionBarMenu(Objects.requireNonNull(ShowCatagoryActivity.this));

                    }else {
                        Toast.makeText(ShowCatagoryActivity.this,"Error in Add product",Toast.LENGTH_LONG).show();
                    }
                }else {
                    showAlert();
                }
            }
        });


        ctnQtyValue.addTextChangedListener(new TextWatcher() {
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


        pcsQtyValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // calculate the net amount when update the values
                setCalculation();
            }
        });

        unitPrice.addTextChangedListener(new TextWatcher() {
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

        ctnPrice.addTextChangedListener(new TextWatcher() {
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

        cancelSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        selectCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        customerNameEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String cusname = editable.toString();
                if (!cusname.isEmpty()) {
                    filterCustomer(cusname);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

    }


    private void setAdapter(ArrayList<CustomerModel> customerNames) {
        progressLayout.setVisibility(View.GONE);
        if (customerList.size()> 0) {
            rootLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }else {
            rootLayout.setVisibility(View.GONE);
            emptyLayout.setEnabled(false);
            emptyLayout.setClickable(false);
            emptyLayout.setVisibility(View.VISIBLE);
        }
        customerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        customerNameAdapter = new CustomerNameAdapter(customerNames, (customer,customerName, pos) -> {
            viewCloseBottomSheet();
            int count =dbHelper.numberOfRows();
            if (count>0){
                showProductDeleteAlert(customer);
            }else {
                setCustomerDetails(customer);
            }

        });
        customerView.setAdapter(customerNameAdapter);
    }

    public void setCustomerDetails(String customerId){
        Log.w("Given_customer_id:",customerId);
        customerDetails=new ArrayList<>();
        customerDetails = dbHelper.getCustomer(customerId);
        customerNameEdittext.setText("");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (customerDetails.size()> 0){
            selectCustomer.setText(customerDetails.get(0).getCustomerName());
            // Storing the key and its value as the data fetched
            customerPredEdit = sharedPreferences.edit();
            customerPredEdit.putString("customerId", customerId);
            customerPredEdit.apply();
        }
    }

    public void showProductDeleteAlert(String customerId){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Warning !");
        builder1.setMessage("Products in Cart will be removed..");
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dbHelper.removeAllItems();
                        setupBadge();
                        Utils.refreshActionBarMenu(ShowCatagoryActivity.this);
                        try {
                            setCustomerDetails(customerId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        builder1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }



    public void setData(ProductsModel model){
        //  Gson gson = new Gson();
        // ProductsModel model = gson.fromJson(getIntent().getStringExtra("productDetails"), ProductsModel.class);

        if (model.getStockQty() !=null && !Objects.requireNonNull(model.getStockQty()).equals("null")){
            availability.setText(model.getStockQty());
        }else {
            availability.setText("0");
        }

        if (model.getPcsPerCarton() !=null && !model.getPcsPerCarton().equals("null")){
            cartonText.setText("1 * "+model.getPcsPerCarton());
        }else {
            cartonText.setText("1 * 1");
        }

        // if (getIntent()!=null){
        itemName.setText(model.getProductName());
        unitPrice.setText(model.getUnitCost());
        ctnPrice.setText(String.valueOf(model.getRetailPrice()));
        double data = Double.parseDouble(model.getPcsPerCarton());
        // convert into int
        int value = (int)data;
        ctnQty.setText(String.valueOf(value));
        // }

        if (ctnQty.getText()!=null){
            double data1 = Double.parseDouble(model.getPcsPerCarton());
            // convert into int
            int value1 = (int)data1;
            if (value1==1){
                pcsQtyLayout.setVisibility(View.GONE);
            }else {
                pcsQtyLayout.setVisibility(View.VISIBLE);
            }
        }

        if (model.getProductImage()!=null && !model.getProductImage().equals("null") ){
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
        }else {
            mainImage.setImageResource(R.drawable.no_image_found);
        }

        viewCloseBottomSheet();
    }

    public void setCalculation() {
        // Define the Required variables to calulate the Amount
        try {
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
                cprice = Double.parseDouble(ctnPrice.getText().toString());
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

            // calculating the net total
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
            }

            double net_qty=(cnQty * pcspercarton)+lqty;
            int value = (int)net_qty;
            qtyTextView.setText(String.valueOf(value));

            if (return_qty != 0) {
                if (returnSwitch.isChecked()) {
                    return_qty = Double.parseDouble(returnEditext.getText().toString()) * pcspercarton;
                } else {
                    return_qty = Double.parseDouble(returnEditext.getText().toString());
                }
                double return_amt = (return_qty * lprice);
                net_amount = net_amount - return_amt;
            }
            if (discount != 0.0) {
                net_amount = net_amount - discount;
            }
            netPrice.setText(twoDecimalPoint(net_amount));
            taxCalculation(net_amount);

        } catch (Exception ex) {
        }
    }

    public void taxCalculation(double subTotal){

        customerDetails=dbHelper.getCustomer();

        String taxValue=customerDetails.get(0).getTaxPerc();
        String taxType=customerDetails.get(0).getTaxType();
        String itemDisc="";
        double taxAmount=0.0;
        double netTotal=0.0;
        Log.w("TaxType:",taxType);
        Log.w("TaxValue:",taxValue);

        if (!taxType.matches("") && !taxValue.matches("")) {

            double taxValueCalc = Double.parseDouble(taxValue);

            if (taxType.matches("E")) {

                if (!itemDisc.matches("")) {
                    taxAmount = (subTotal * taxValueCalc) / 100;
                    String prodTax = fourDecimalPoint(taxAmount);
                    // sl_tax.setText("" + prodTax);

                    netTotal = subTotal + taxAmount;
                    String ProdNetTotal = twoDecimalPoint(netTotal);
                    //sl_netTotal.setText("" + ProdNetTotal);

                    Log.w("Tax_1",prodTax);
                    Log.w("Net_amount:",ProdNetTotal);
                    taxTitle.setText("Tax ( Exc )");
                    totalTextView.setText(twoDecimalPoint(subTotal));
                    taxTextView.setText(String.valueOf(prodTax));
                    netTotalTextView.setText(ProdNetTotal);
                } else {

                    taxAmount = (subTotal * taxValueCalc) / 100;
                    String prodTax = fourDecimalPoint(taxAmount);

                    //sl_tax.setText("" + prodTax);

                    netTotal = subTotal + taxAmount;
                    String ProdNetTotal = twoDecimalPoint(netTotal);
                    // sl_netTotal.setText("" + ProdNetTotal);

                    Log.w("Tax_1",prodTax);
                    Log.w("Net_amount:",ProdNetTotal);
                    taxTitle.setText("Tax ( Exc )");
                    totalTextView.setText(twoDecimalPoint(subTotal));
                    taxTextView.setText(String.valueOf(prodTax));
                    netTotalTextView.setText(ProdNetTotal);
                }

            } else if (taxType.matches("I")) {
                if (!itemDisc.matches("")) {
                    taxAmount = (subTotal * taxValueCalc)
                            / (100 + taxValueCalc);
                    String prodTax = fourDecimalPoint(taxAmount);

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
                    totalTextView.setText(twoDecimalPoint(Double.parseDouble(totalIncl)));
                    taxTextView.setText(prodTax);
                    netTotalTextView.setText(ProdNetTotal);
                    taxTitle.setText("Tax ( Inc )");
                } else {
                    taxAmount = (subTotal * taxValueCalc) / (100 + taxValueCalc);
                    String prodTax = fourDecimalPoint(taxAmount);

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
                    totalTextView.setText(totalIncl);
                    taxTextView.setText(String.valueOf(prodTax));
                    netTotalTextView.setText(ProdNetTotal);
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
                    totalTextView.setText(twoDecimalPoint(subTotal));
                    taxTextView.setText("0.0");
                    netTotalTextView.setText(ProdNetTotal);
                } else {
                    netTotal = subTotal + taxAmount;
                    netTotal = subTotal;
                    String ProdNetTotal = twoDecimalPoint(netTotal);
                    //  sl_netTotal.setText("" + ProdNetTotal);

                    //  Log.w("Tax_1",prodTax);
                    Log.w("Net_amount:",ProdNetTotal);
                    taxTitle.setText("Tax");
                    totalTextView.setText(twoDecimalPoint(subTotal));
                    taxTextView.setText("0.0");
                    netTotalTextView.setText(ProdNetTotal);
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
    }



    public void viewCloseBottomSheet() {
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            customerNameEdittext.setText("");
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        // get the Customer name from the local db
    }


    public void getAllProducts(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url=Utils.getBaseUrl(this) +"ProductApi/GetProduct_All_ByFacets?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_product_url:",url);
        newProductList=new ArrayList<>();
        pDialog = new SweetAlertDialog(ShowCatagoryActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Products Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
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
                                product.setRetailPrice(productObject.optDouble("RetailPrice"));
                                product.setWholeSalePrice(productObject.optString("WholeSalePrice"));
                                product.setStockQty(productObject.optString("StockQty"));
                                product.setCartonPrice(productObject.optString("CartonPrice"));
                                product.setPcsPerCarton(productObject.optString("PcsPerCarton"));
                                product.setUnitCost(productObject.optString("UnitCost"));
                                product.setStockQty(productObject.optString("StockQty"));
                                newProductList.add(product);
                            }
                        }
                        productList.addAll(newProductList);
                        //                      dbHelper.insertCatalogProducts(productList);
                        pDialog.dismiss();
                        populateCategoriesData();
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

    private CustomRecyclerView.OnScrollListener mScrollListener = new CustomRecyclerView.OnScrollListener() {
        @Override
        protected void onGoUp() {
            Log.w("Going","Up");
        }
        @Override
        protected void onGoDown() {
            Log.w("Going","Down");
        }
    };

    private void setRecyclerViewSettings() {
        productsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    //Scrolling down
                    Log.w("Scroll","Down");
                } else if (dy < 0) {
                    //Scrolling up
                    Log.w("Scroll","up");
                }
            }
        });
    }

    private void populateCategoriesData() {
        try {
            if (productList.size()>0){
                productsAdapterNew = new ProductAdapterLoadMore(ShowCatagoryActivity.this,
                        productList, new ProductAdapterLoadMore.CallBack() {
                    @Override
                    public void callDescription(String productId,String productName,String price,String imageString,String weight,
                                                String cartonPrice,String unitPrice,String pcsPercarton,String stock) {

                        if (MainHomeActivity.selectCustomer.getText().toString().equals("Choose Customer")){
                            showAlert();
                        }else {
                            Intent intent = new Intent(getApplicationContext(), DescriptionActivity.class);
                            intent.putExtra("productId",productId);
                            intent.putExtra("productName",productName);
                            intent.putExtra("price",price);
                            intent.putExtra("imageString", imageString);
                            intent.putExtra("weight",weight);
                            intent.putExtra("cartonPrice",cartonPrice);
                            intent.putExtra("unitPrice",unitPrice);
                            intent.putExtra("pcsPerCarton",pcsPercarton);
                            intent.putExtra("stockQty",stock);

                            startActivity(intent);
                            // viewCloseBottomSheet();
                        }
                    }

                    @Override
                    public void showLowStockAlert() {
                        showLowStock();
                    }

                    @Override
                    public void showBottomDescription(ProductsModel productsModel) {
                        model=productsModel;
                        setData(model);
                        // viewCloseBottomSheet();
                    }
                });
                productsAdapterNew.setCallback(this);

                if (productList.size()!=50){
                    productsAdapterNew.setWithFooter(false); //enabling footer to show
                }else {
                    productsAdapterNew.setWithFooter(true); //enabling footer to show
                }
                productsView.setAdapter(productsAdapterNew);
                emptyLayout.setVisibility(View.GONE);
                filterLayout.setVisibility(View.VISIBLE);
                productsView.setVisibility(View.VISIBLE);

                mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return productsAdapterNew.isPositionFooter(position) ? mLayoutManager.getSpanCount() : 1;
                    }
                });

                final int[] pastVisiblesItems = new int[1];
                final int[] visibleItemCount = new int[1];
                final int[] totalItemCount = new int[1];
                productsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView,
                                           int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        visibleItemCount[0] = mLayoutManager.getChildCount();
                        totalItemCount[0] = mLayoutManager.getItemCount();
                        pastVisiblesItems[0] = mLayoutManager.findFirstVisibleItemPosition();
                        if ((visibleItemCount[0] + pastVisiblesItems[0]) >= totalItemCount[0]) {
                            //bottom of recyclerview
                            Log.w("Reached_bottom_","Success");
                        }
                    }
                });
            }else {
                emptyLayout.setVisibility(View.VISIBLE);
                filterLayout.setVisibility(View.GONE);
                productsView.setVisibility(View.GONE);
            }
        }catch (Exception ex){
            Log.e("TAG","Error in Populating the data:"+ex.getMessage());
        }
    }

    public void showAlert(){
        new SweetAlertDialog(ShowCatagoryActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Warning !")
                .setContentText("Please Add the Qty!")
                .setConfirmText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }

    public void showLowStock(){
        new SweetAlertDialog(ShowCatagoryActivity.this, SweetAlertDialog.WARNING_TYPE)
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

    public void setFilter(String val){
//        Toast.makeText(getActivity(),"Pressed Here",Toast.LENGTH_SHORT).show();
        filter(val);
        Log.w("Printed","Success");
    }

    public void setProductsDisplay(String action){
        try {
            // productsAdapterNew.setWithFooter(false);
            sortAdapter.resetPosition();
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
                if (productsAdapterNew!=null){
                    productsAdapterNew.filterList(filterdNames);
                }
                productsAdapterNew.filterList(filterdNames);
                productsView.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
            }else {
                emptyLayout.setVisibility(View.VISIBLE);
                productsView.setVisibility(View.GONE);
            }
        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }

    private void filterCustomer(String text) {
        try {
            //new array list that will hold the filtered data
            ArrayList<CustomerModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (CustomerModel s : customerList) {
                //if the existing elements contains the search input
                if (s.getCustomerName().toLowerCase().contains(text.toLowerCase()) || s.getCustomerCode().toLowerCase().contains(text.toLowerCase())) {
                    //adding the element to filtered list
                    filterdNames.add(s);
                }
            }
            //calling a method of the adapter class and passing the filtered list
            customerNameAdapter.filterList(filterdNames);

        } catch (Exception ex) {
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }

    public void filter(String text) {
        try {
            productsAdapterNew.setWithFooter(false);
            //new array list that will hold the filtered data
            ArrayList<ProductsModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (ProductsModel s : productList) {
                //if the existing elements contains the search input
                int diff=s.getProductName().charAt(0)-text.charAt(0);
                if (diff==0) {
                    //adding the element to filtered list
                    filterdNames.add(s);
                }
            }
            //calling a method of the adapter class and passing the filtered list
            if (filterdNames.size()>0){
                for (ProductsModel model:filterdNames){
                    Log.w("Filter_products:",model.getProductName());
                }
                if (productsAdapterNew!=null){
                    productsAdapterNew.filterList(filterdNames);
                }
                productsAdapterNew.filterList(filterdNames);
                productsView.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
            }else {
                emptyLayout.setVisibility(View.VISIBLE);
                productsView.setVisibility(View.GONE);
            }
        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onClickLoadMore() {
        Log.w("Load_more_calling:","Success");
        productsAdapterNew.setWithFooter(false); // hide footer
        pageNo=pageNo+1;
        // now add remaining elements
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("CompanyCode",companyCode);
            jsonObject.put("LocationCode",locationCode);
            if (fromValue.equals("brand")){
                jsonObject.put("BrandCode",brandId);
            }else if (fromValue.equals("department")){
                jsonObject.put("DepartmentCode",departmentId);
            }else {
                jsonObject.put("CategoryCode",catagoriesId);
            }
            jsonObject.put("PageSize",5000);
            jsonObject.put("PageNo",pageNo);
            getAllProducts(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("Given_load_page_no",pageNo+"");
        productsAdapterNew.notifyDataSetChanged(); // more elements will be added
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem myItem = menu.findItem(R.id.action_total);
        ArrayList<CartModel> localCart = dbHelper.getAllCartItems();
        double net_amount = 0.00;
        for (int j = 0; j < localCart.size(); j++) {
            if (localCart.get(j).getCART_COLUMN_NET_PRICE()!=null && !localCart.get(j).getCART_COLUMN_NET_PRICE().isEmpty() && !localCart.get(j).getCART_COLUMN_NET_PRICE().equals("null")){
                net_amount += Double.parseDouble(localCart.get(j).getCART_COLUMN_NET_PRICE());
            }
        }
        menu.getItem(0).setTitle("Net Amt: $ " + twoDecimalPoint(net_amount));
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        final MenuItem cartTotal=menu.findItem(R.id.action_total);
        final MenuItem chooseCompany=menu.findItem(R.id.choose_company);
        chooseCompany.setVisible(false);
        View actionView = menuItem.getActionView();
        textCartItemCount = actionView.findViewById(R.id.cart_badge);
        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductsFragment.closeSheet();
                onOptionsItemSelected(menuItem);
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_search) {
            ProductsFragment.closeSheet();
            Intent intent=new Intent(ShowCatagoryActivity.this,SearchProductActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public static void setupBadge() {
        mCartItemCount = dbHelper.numberOfRows();
        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ProductsFragment.closeSheet();
        mCartItemCount = dbHelper.numberOfRows();
        setupBadge();
        Utils.refreshActionBarMenu(ShowCatagoryActivity.this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}