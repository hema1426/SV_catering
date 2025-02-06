package com.winapp.svcatering.fragments;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.winapp.svcatering.activity.MainHomeActivity;
import com.winapp.svcatering.adapter.LoadMoreProductsAdapter;
import com.winapp.svcatering.adapter.ProductAdapterLoadMore;
import com.winapp.svcatering.adapter.SortAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.CustomRecyclerView;
import com.winapp.svcatering.utils.GridSpacingItemDecoration;
import com.winapp.svcatering.R;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.winapp.svcatering.activity.DescriptionActivity;
import com.winapp.svcatering.adapter.ProductsAdapter;
import com.winapp.svcatering.model.ProductsModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import cn.pedant.SweetAlert.SweetAlertDialog;
import static android.content.Context.MODE_PRIVATE;
import static com.winapp.svcatering.utils.Utils.fourDecimalPoint;
import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

public class ProductsFragment extends Fragment implements ProductAdapterLoadMore.Callbacks {

    private View progressBarLayout;
    private ProductsAdapter productsAdapter;
    private ProductAdapterLoadMore productsAdapterNew;
    private RecyclerView productsView;
    private SweetAlertDialog pDialog;
    public static ArrayList<ProductsModel> productList=new ArrayList<>();
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
    DBHelper dbHelper;
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
    boolean isNetTotalAbovezero=false;
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
    LinearLayout transLayout;
    ImageView cancelSheet;
    View portraitLayout;
    View landscapeLayout;
    SortAdapter sortAdapter;
    String locationCode;
    TextView qtyTextView;
    TextWatcher cartonQtyWatcher;
    TextWatcher qtyWatcher;
    boolean isAllowLowStock=false;
    LinearLayout unitPriceLayout;
    LinearLayout focLayout;
    TextView uomCodeText;
    String selectCustomerId;
    static ProgressDialog dialog;

    boolean isReverseCalculationEnabled=true;
    boolean isCartonPriceEdit=true;
    boolean isPriceEdit=true;

    TextWatcher cartonPriceTextWatcher;
    TextWatcher loosePriceTextWatcher;
    public String fillterLetter="";


    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_two, container, false);
        session=new SessionManager(getActivity());
        dbHelper=new DBHelper(getActivity());
        user=session.getUserDetails();
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        progressBarLayout = view.findViewById(R.id.progress_layout);
        productsView =view.findViewById(R.id.categoriesView);
        progressBarLayout.setVisibility(View.GONE);
        lettersRecyclerview=view.findViewById(R.id.sorting_letters);
        emptyLayout=view.findViewById(R.id.empty_layout);
        sortButton=view.findViewById(R.id.fab);
        customerDetails=dbHelper.getCustomer();
        mainImage=view.findViewById(R.id.item_image);
        itemName=view.findViewById(R.id.item_name);
        availability=view.findViewById(R.id.availabilty);
        netPrice =view.findViewById(R.id.price);
        ctnPrice=view.findViewById(R.id.ctn_price);
        ctnQty=view.findViewById(R.id.ctn_qty);
        //pcsQty=findViewById(R.id.pcs_qty);
        ctnQtyValue=view.findViewById(R.id.ctn_qty_value);
        pcsQtyValue=view.findViewById(R.id.pcs_qty_value);
        radioGroup=view.findViewById(R.id.radioGroup);
        radioNormal=view.findViewById(R.id.radioNormal);
        radioFoc=view.findViewById(R.id.radioFoc);
        ctnMinus=view.findViewById(R.id.ctn_minus);
        ctnPlus=view.findViewById(R.id.ctn_plus);
        pcsMinus=view.findViewById(R.id.pcs_minus);
        pcsPlus=view.findViewById(R.id.pcs_plus);
        addToCart=view.findViewById(R.id.add_to_cart);
        cartonText=view.findViewById(R.id.pcs);
        unitPrice=view.findViewById(R.id.unit_price);
        totalTextView=view.findViewById(R.id.total);
        taxTextView=view.findViewById(R.id.tax);
        netTotalTextView=view.findViewById(R.id.net_total);
        taxTitle=view.findViewById(R.id.tax_title);
        returnLayout=view.findViewById(R.id.return_layout);
        showHideButton=view.findViewById(R.id.show_hide);
        focSwitch=view.findViewById(R.id.foc_switch);
        exchangeSwitch=view.findViewById(R.id.exchange_switch);
        returnSwitch=view.findViewById(R.id.return_switch);
        focEditText=view.findViewById(R.id.foc_text);
        exchangeEditext=view.findViewById(R.id.exchange_text);
        returnEditext=view.findViewById(R.id.return_text);
        discountEditext=view.findViewById(R.id.discount_text);
        pcsQtyLayout=view.findViewById(R.id.pcs_qty_layout);
        transLayout=view.findViewById(R.id.trans_layout);
        cancelSheet=view.findViewById(R.id.cancel_sheet);
        qtyTextView=view.findViewById(R.id.qty);
        unitPriceLayout=view.findViewById(R.id.unit_price_layout);
        focLayout=view.findViewById(R.id.foc_layout);
        uomCodeText=view.findViewById(R.id.uom_code);
      //  portraitLayout=view.findViewById(R.id.description_layout_portrait);
      //  landscapeLayout=view.findViewById(R.id.description_layout_land);

       // productList=new ArrayList<>();
       // productList=dbHelper.getAllCatalogProducts();


       /* if (productList!=null){
            if (productList.size()>0){
                populateCategoriesData();
            }else {
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("CompanyCode",companyCode);
                    jsonObject.put("LocationCode",locationCode);
                    //jsonObject.put("CategoryCode",catagoriesId);
                    jsonObject.put("PageSize",50);
                    jsonObject.put("PageNo",pageNo);
                    getAllProducts(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else {
           */

        ArrayList<SettingsModel> settings1=dbHelper.getSettings();
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
        }


        JSONObject jsonObject=new JSONObject();
            try {
                productList=new ArrayList<>();
                jsonObject.put("CompanyCode",companyCode);
                jsonObject.put("LocationCode",locationCode);
                //jsonObject.put("CategoryCode",catagoriesId);
                jsonObject.put("PageSize",50);
                jsonObject.put("PageNo","1");
                getAllProducts(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
           // getAllProducts(String.valueOf(pageNo));
      //  }

        /*int spanCount=0;
        int dp=0;
        if (Utils.isTablet(getActivity())){
            spanCount=3;
            dp=1;
        }else {
            spanCount=2;
            dp=5;
        }

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        productsView.setLayoutManager(mLayoutManager);
        productsView.addItemDecoration(new GridSpacingItemDecoration(spanCount, GridSpacingItemDecoration.dpToPx(getActivity(),dp), true));
        productsView.setItemAnimator(new DefaultItemAnimator());*/

        int spanCount=0;
        int dp=0;
        if (Utils.isTablet(getActivity())){
            spanCount=3;
            dp=10;
        }else {
            spanCount=2;
            dp=10;
        }

        int mNoOfColumns = Utils.calculateNoOfColumns(requireActivity(), 180);
        // GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, mNoOfColumns);
        mLayoutManager = new GridLayoutManager(getActivity(), mNoOfColumns);
        mLayoutManager.setAutoMeasureEnabled(true);
        productsView.setLayoutManager(mLayoutManager);
        // categoriesView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        productsView.addItemDecoration(new GridSpacingItemDecoration(spanCount, GridSpacingItemDecoration.dpToPx(getActivity(), dp), true));
     //   AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(getActivity(), 200);
      //  productsView.setLayoutManager(layoutManager);
        productsView.setItemAnimator(new DefaultItemAnimator());



        // define the sorting letters
        lettersRecyclerview.setHasFixedSize(true);
        // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        lettersRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        sortAdapter = new SortAdapter(Utils.getSorting(), new SortAdapter.CallBack() {
            @Override
            public void sortProduct(String letter) {
                if (letter.equals("All")){
                    JSONObject jsonObject=new JSONObject();
                    try {
                        productList=new ArrayList<>();
                        fillterLetter="";
                        pageNo=1;
                        jsonObject.put("CompanyCode",companyCode);
                        jsonObject.put("LocationCode",locationCode);
                        //jsonObject.put("CategoryCode",catagoriesId);
                        jsonObject.put("PageSize",50);
                        jsonObject.put("PageNo",pageNo);
                        getAllProducts(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                   // filter(letter);
                    JSONObject jsonObject=new JSONObject();
                    try {
                        productList=new ArrayList<>();
                        fillterLetter=letter;
                        pageNo=1;
                        jsonObject.put("CompanyCode",companyCode);
                        jsonObject.put("LocationCode",locationCode);
                        jsonObject.put("ProductName",letter.toString());
                        jsonObject.put("PageSize",50);
                        jsonObject.put("PageNo",pageNo);
                        getAllProducts(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        lettersRecyclerview.setAdapter(sortAdapter);

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

        setRecyclerViewSettings();

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
                        int orientation = getActivity().getResources().getConfiguration().orientation;
                        transLayout.setVisibility(View.VISIBLE);
                        MainHomeActivity.disableTab(false);
                        MainHomeActivity.customerTransLayout.setVisibility(View.VISIBLE);
                        MainHomeActivity.tabTransLayout.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        transLayout.setVisibility(View.GONE);
                        MainHomeActivity.disableTab(true);
                        ctnQtyValue.setText("0");
                        pcsQtyValue.setText("0");
                        taxTextView.setText("0.0000");
                        totalTextView.setText("0.00");
                        focEditText.setText("");
                        exchangeEditext.setText("");
                        returnEditext.setText("");
                        discountEditext.setText("");
                        netPrice.setText("0.00");
                        MainHomeActivity.customerTransLayout.setVisibility(View.GONE);
                        MainHomeActivity.tabTransLayout.setVisibility(View.GONE);
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
                getLowStockSetting();
                double pcspercarton=Double.parseDouble(ctnQty.getText().toString());
                double stock=Double.parseDouble(availability.getText().toString());
                double allow_cn_qty=stock / pcspercarton;
                double net_qty_value=Double.parseDouble(qtyTextView.getText().toString())+pcspercarton;
                double net_qty_allow=net_qty_value / pcspercarton ;
                Log.w("Net_Qty:", String.valueOf(net_qty_allow));
                Log.w("Allow_cn_qty:",String.valueOf(allow_cn_qty));
                if (net_qty_allow > allow_cn_qty){
                    if (isAllowLowStock){
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
                    if (isAllowLowStock){
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

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
                selectCustomerId = sharedPreferences.getString("customerId", "");
                if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
                    if (isProductExist(model.getProductCode().trim())){
                        showExistingProductAlert(model.getProductCode(),model.getProductName());
                    }else {
                        insertProduct();
                    }
                }else {
                    showAlertCustomer();
                }
            }
        });


        cartonQtyWatcher =new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    getLowStockSetting();
                    double pcspercarton=Double.parseDouble(ctnQty.getText().toString());
                    double stock=Double.parseDouble(availability.getText().toString());
                    double allow_cn_qty=stock / pcspercarton;
                    double net_val=0;
                    double pcs_qty=0;
                    if (!editable.toString().isEmpty()){
                        net_val=Double.parseDouble(editable.toString());
                    }

                    if (!pcsQtyValue.getText().toString().isEmpty()){
                        pcs_qty=Double.parseDouble(pcsQtyValue.getText().toString());
                    }

                    double net_qty_value=net_val * pcspercarton + pcs_qty;

                    Log.w("Net_Qty:", String.valueOf(net_qty_value));
                    Log.w("Allow_cn_qty:",String.valueOf(allow_cn_qty));

                    if (net_qty_value > stock){
                        //showLowStock();
                        if (isAllowLowStock){
                            setCalculation();
                        }else {
                            ctnQtyValue.removeTextChangedListener(cartonQtyWatcher);
                            ctnQtyValue.setText("0");
                            ctnQtyValue.addTextChangedListener(cartonQtyWatcher);
                            ctnQtyValue.clearFocus();
                            Toast.makeText(getActivity(),"Low Stock please check",Toast.LENGTH_LONG).show();
                            setCalculation();
                        }
                    }else {
                        setCalculation();
                    }
                }catch (Exception ex){

                }
            }
        };
        ctnQtyValue.addTextChangedListener(cartonQtyWatcher);

       /* ctnQtyValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                double pcspercarton=Double.parseDouble(ctnQty.getText().toString());
                double stock=Double.parseDouble(availability.getText().toString());
                double allow_cn_qty=stock / pcspercarton;
                double net_qty_value=Double.parseDouble(qtyTextView.getText().toString())+pcspercarton;
                double net_qty_allow=net_qty_value / pcspercarton ;
                Log.w("Net_Qty:", String.valueOf(net_qty_allow));
                Log.w("Allow_cn_qty:",String.valueOf(allow_cn_qty));
                double net_val=0;
                if (!editable.toString().isEmpty()){
                    net_val=Double.parseDouble(editable.toString());
                }
                if (net_qty_allow > allow_cn_qty){
                    //showLowStock();
                    ctnQtyValue.setText(String.valueOf(net_val-1));
                    Toast.makeText(getActivity(),"Low Stock please check",Toast.LENGTH_LONG).show();
                    setCalculation();
                }else {
                    setCalculation();
                }
                }catch (Exception ex){

                }
            }
        });*/


       qtyWatcher=new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void afterTextChanged(Editable editable) {
               try {
                   getLowStockSetting();
                   double pcspercarton=Double.parseDouble(ctnQty.getText().toString());
                   double stock=Double.parseDouble(availability.getText().toString());
                   double allow_cn_qty=stock / pcspercarton;
                   double net_val=0;
                   double pcs_qty=0;
                   if (!editable.toString().isEmpty()){
                       pcs_qty=Double.parseDouble(editable.toString());
                   }

                   if (!ctnQtyValue.getText().toString().isEmpty()){
                       net_val=Double.parseDouble(ctnQtyValue.getText().toString());
                   }

                   double net_qty_value=net_val * pcspercarton + pcs_qty;

                   Log.w("Net_Qty:", String.valueOf(net_qty_value));
                   Log.w("Allow_cn_qty:",String.valueOf(allow_cn_qty));

                   if (net_qty_value > stock){
                       //showLowStock();
                       if (isAllowLowStock){
                           setCalculation();
                       }else {
                           pcsQtyValue.removeTextChangedListener(qtyWatcher);
                           pcsQtyValue.setText("0");
                           pcsQtyValue.addTextChangedListener(qtyWatcher);
                           pcsQtyValue.clearFocus();
                           Toast.makeText(getActivity(),"Low Stock please check",Toast.LENGTH_LONG).show();
                           setCalculation();
                       }
                   }else {
                       setCalculation();
                   }
               }catch (Exception ex){

               }
           }
       };
       pcsQtyValue.addTextChangedListener(qtyWatcher);


       /* pcsQtyValue.addTextChangedListener(new TextWatcher() {
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
        });*/


        loosePriceTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

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
                    setCalculation();
                }else {
                    setCalculation();
                }
            }
        };
        unitPrice.addTextChangedListener(loosePriceTextWatcher);

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
                        if (ctnPrice.getText().toString().equals("")){
                            unitPrice.removeTextChangedListener(loosePriceTextWatcher);
                            unitPrice.setText("");
                            unitPrice.addTextChangedListener(loosePriceTextWatcher);
                        }
                        if (!ctnQty.getText().toString().equals("")){
                            double pcspercarton=Double.parseDouble(ctnQty.getText().toString());
                            double carton_price=0;
                            if (ctnPrice.getText().toString().equals("")){
                                carton_price=0;
                            }else {
                                carton_price=Double.parseDouble(ctnPrice.getText().toString());
                            }
                            if (pcspercarton>1){
                                double unit_price=(carton_price / pcspercarton);
                                unitPrice.removeTextChangedListener(loosePriceTextWatcher);
                                unitPrice.setText(twoDecimalPoint(unit_price));
                                unitPrice.addTextChangedListener(loosePriceTextWatcher);
                            }else {
                                if (!ctnPrice.getText().toString().isEmpty()){
                                    unitPrice.removeTextChangedListener(loosePriceTextWatcher);
                                    unitPrice.setText(twoDecimalPoint(Double.parseDouble(ctnPrice.getText().toString())));
                                    unitPrice.addTextChangedListener(loosePriceTextWatcher);
                                }else {
                                    unitPrice.removeTextChangedListener(loosePriceTextWatcher);
                                    unitPrice.setText("0.00");
                                    unitPrice.addTextChangedListener(loosePriceTextWatcher);
                                }
                            }
                        }
                    }
                    setCalculation();
                }else {
                    setCalculation();
                }
            }
        };
        ctnPrice.addTextChangedListener(cartonPriceTextWatcher);


        cancelSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        ArrayList<SettingsModel> settings=dbHelper.getSettings();
        if (settings.size()>0) {
            for (SettingsModel model : settings) {
                if (model.getSettingName().equals("invoice_switch")) {
                    if (model.getSettingValue().equals("1")) {
                        focLayout.setVisibility(View.VISIBLE);
                    } else {
                        focLayout.setVisibility(View.GONE);
                    }
                }
            }
        }

        return view;
    }

    public void showAlertCustomer(){
        try {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
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

    public void getLowStockSetting(){
        ArrayList<SettingsModel> settings=dbHelper.getSettings();
        if (settings.size()>0) {
            for (SettingsModel model : settings) {
                if (model.getSettingName().equals("allow_negative_switch")) {
                    isAllowLowStock= model.getSettingValue().equals("1");
                }
            }
        }
    }

    public void showExistingProductAlert(String productId,String productName){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setTitle("Warning !");
        builder1.setMessage(productName+" - "+productId+ "\nAlready Exist Do you want to replace ? ");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        insertProduct();
                    }
                });
        builder1.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                       // clearFields();
                        viewCloseBottomSheet();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    public void insertProduct(){
        try {
            String focType="pcs";
            String exchangeType="pcs";
            String returnType="pcs";
            String carton_price="0.0";
            String unit_price="0.0";
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

            Log.w("SubTotalValues:",sub_total+"");


            if (isQtyEntered && Double.parseDouble(netTotalTextView.getText().toString())>0){
                boolean status= dbHelper.insertCart(
                        model.getProductCode(),
                        model.getProductName(),
                        ctnQtyValue.getText().toString(),
                        pcsQtyValue.getText().toString(),
                        model.getUnitCost(),
                        model.getProductImage(),
                        netTotalTextView.getText().toString(),
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
                        returnType,"",String.valueOf(total),
                        availability.getText().toString(),uomCodeText.getText().toString(),"0.00", availability.getText().toString());
                if (status){
                    Toast.makeText(getActivity(),"Product Added Successfully",Toast.LENGTH_LONG).show();
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
                    MainHomeActivity.setupBadge(getActivity());
                    Utils.refreshActionBarMenu(requireActivity());
                }else {
                    Toast.makeText(getActivity(),"Error in Add product",Toast.LENGTH_LONG).show();
                }
            }else {
                showAlert();
            }
        }catch (Exception ex){
            Log.e("Exp_to_add_product:",ex.getMessage());
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

    public void setData(ProductsModel model){
                //  Gson gson = new Gson();
                // ProductsModel model = gson.fromJson(getIntent().getStringExtra("productDetails"), ProductsModel.class);
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("customerPref",MODE_PRIVATE);
            SharedPreferences.Editor customerPredEdit= sharedPreferences.edit();
            String selectCustomerId = sharedPreferences.getString("customerId", "");
            customerDetails=new ArrayList<>();

            // Need to Getting Product price Depending the Customer
         /*   if (selectCustomerId!=null && !selectCustomerId.isEmpty()){
                customerDetails=dbHelper.getCustomer(selectCustomerId);
                getProductPrice(model.getProductCode());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
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

    /*    ArrayList<SettingsModel> settings=dbHelper.getSettings();
        if (settings.size()>0) {
            for (SettingsModel mo : settings) {
                if (mo.getSettingName().equals("invoice_switch")) {
                    if (mo.getSettingValue().equals("1")) {
                        focLayout.setVisibility(View.VISIBLE);
                    } else {
                        focLayout.setVisibility(View.GONE);
                    }
                }
            }
        }*/

            itemName.setText(model.getProductName());
            unitPrice.setText(model.getWholeSalePrice());
            ctnPrice.setText(String.valueOf(model.getRetailPrice()));
            double data = Double.parseDouble(model.getPcsPerCarton());
            // convert into int
            int value = (int)data;
            ctnQty.setText(String.valueOf(value));
            uomCodeText.setText(model.getUomCode());

            if (ctnQty.getText()!=null){
                double data1 = Double.parseDouble(model.getPcsPerCarton());
                // convert into int
                int value1 = (int)data1;
                if (value1==1){
                    pcsQtyLayout.setVisibility(View.GONE);
                    unitPriceLayout.setVisibility(View.GONE);
                }else {
                    pcsQtyLayout.setVisibility(View.VISIBLE);
                    unitPriceLayout.setVisibility(View.VISIBLE);
                }
            }

            if (!Utils.isTablet(requireActivity())){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, 120);
                mainImage.setLayoutParams(params);
            }

            // Getting image from the Local DB
            String imagePath=dbHelper.getProductImage(model.getProductCode());

            mainImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!imagePath.isEmpty()) {
                        String imageFullpath = Constants.folderPath + "/" + imagePath;
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
            }
            viewCloseBottomSheet();
        }catch (Exception ex){
        }
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
            netPrice.setText(twoDecimalPoint(net_amount));
            taxCalculation(net_amount);

        } catch (Exception ex) {
        }
    }

    public void taxCalculation(double subTotal){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
        selectCustomerId = sharedPreferences.getString("customerId", "");
        if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
            customerDetails = dbHelper.getCustomer(selectCustomerId);
        }
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
                    netTotal = subTotal + taxAmount;
                    String ProdNetTotal = twoDecimalPoint(netTotal);

                    Log.w("Tax_1",prodTax);
                    Log.w("Net_amount:",ProdNetTotal);
                    taxTitle.setText("Tax ( Exc )");
                    totalTextView.setText(twoDecimalPoint(subTotal));
                    taxTextView.setText(String.valueOf(prodTax));
                    netTotalTextView.setText(ProdNetTotal);
                } else {

                    taxAmount = (subTotal * taxValueCalc) / 100;
                    String prodTax = fourDecimalPoint(taxAmount);

                    netTotal = subTotal + taxAmount;
                    String ProdNetTotal = twoDecimalPoint(netTotal);

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

                    netTotal = subTotal;
                    String ProdNetTotal = twoDecimalPoint(netTotal);

                    double dTotalIncl = netTotal - taxAmount;
                    String totalIncl = twoDecimalPoint(dTotalIncl);
                    Log.d("totalIncl", "" + totalIncl);

                    Log.w("Tax_1",prodTax);
                    Log.w("Net_amount:",ProdNetTotal);
                    totalTextView.setText(twoDecimalPoint(Double.parseDouble(totalIncl)));
                    taxTextView.setText(prodTax);
                    netTotalTextView.setText(ProdNetTotal);
                    taxTitle.setText("Tax ( Inc )");
                } else {
                    taxAmount = (subTotal * taxValueCalc) / (100 + taxValueCalc);
                    String prodTax = fourDecimalPoint(taxAmount);

                    netTotal = subTotal + taxAmount;
                    netTotal = subTotal;
                    String ProdNetTotal = twoDecimalPoint(netTotal);

                    double dTotalIncl = netTotal - taxAmount;
                    String totalIncl = twoDecimalPoint(dTotalIncl);
                    Log.d("totalIncl", "" + totalIncl);

                    Log.w("Tax_1",prodTax);
                    Log.w("Net_amount:",ProdNetTotal);
                    taxTitle.setText("Tax ( Inc )");
                    totalTextView.setText(totalIncl);
                    taxTextView.setText(String.valueOf(prodTax));
                    netTotalTextView.setText(ProdNetTotal);
                }

            } else if (taxType.matches("Z")) {
                if (!itemDisc.matches("")) {
                    netTotal = subTotal + taxAmount;
                    netTotal = subTotal;
                    String ProdNetTotal = twoDecimalPoint(netTotal);
                    Log.w("Net_amount:",ProdNetTotal);
                    taxTitle.setText("Tax");
                    totalTextView.setText(twoDecimalPoint(subTotal));
                    taxTextView.setText("0.0");
                    netTotalTextView.setText(ProdNetTotal);
                } else {
                    netTotal = subTotal + taxAmount;
                    netTotal = subTotal;
                    String ProdNetTotal = twoDecimalPoint(netTotal);
                    Log.w("Net_amount:",ProdNetTotal);
                    taxTitle.setText("Tax");
                    totalTextView.setText(twoDecimalPoint(subTotal));
                    taxTextView.setText("0.0");
                    netTotalTextView.setText(ProdNetTotal);
                }
            }
        }
    }



    public void viewCloseBottomSheet() {
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        // get the Customer name from the local db
    }

    public static void closeSheet(){
        if (behavior!=null)
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


    public void getAllProducts(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        String url=Utils.getBaseUrl(getActivity()) +"ProductList";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_product_url:",url);
        newProductList=new ArrayList<>();
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Products Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Response_is:",response.toString());
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
                                if (!productObject.optString("stockInHand").equals("null")){
                                    product.setStockQty(productObject.optString("stockInHand"));
                                }else {
                                    product.setStockQty("0");
                                }
                                product.setUomCode(productObject.optString("uomCode"));
                                product.setProductBarcode(productObject.optString("barCode"));
                                productList.add(product);
                            }
                        }
                        productList.addAll(newProductList);
  //                      dbHelper.insertCatalogProducts(productList);
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
                productsView.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
                productsAdapterNew = new ProductAdapterLoadMore(getActivity(), productList, new ProductAdapterLoadMore.CallBack() {
                    @Override
                    public void callDescription(String productId,String productName,String price,String imageString,String weight,
                                                String cartonPrice,String unitPrice,String pcsPercarton,String stock) {
                        if (MainHomeActivity.selectCustomer.getText().toString().equals("Choose Customer")){
                            showAlert();
                        }else {
                            Intent intent = new Intent(getActivity(), DescriptionActivity.class);
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
                        setData(productsModel);
                        // viewCloseBottomSheet();
                    }
                });
                productsAdapterNew.setCallback(this);
                productsAdapterNew.setWithFooter(true); //enabling footer to show
                productsView.setAdapter(productsAdapterNew);
                productsView.setVisibility(View.VISIBLE);
                pDialog.dismiss();
            }else {
                pDialog.dismiss();
                productsView.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            }


           /* mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
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
            });*/
        }catch (Exception ex){
            Log.e("TAG","Error in Populating the data:"+ex.getMessage());
        }
    }

    public void showAlert(){
        String message="";
        if (isQtyEntered){
            message="Enter the price of the product !";
        }else {
            message="Please Add the Qty !";
        }
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
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

    public void showLowStock(){
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
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
            for (ProductsModel s : ProductsFragment.productList) {
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

    public void filter(String text) {
        try {
            productsAdapterNew.setWithFooter(false);
            //new array list that will hold the filtered data
            ArrayList<ProductsModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (ProductsModel s : productsAdapterNew.getList()) {
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
        // Checks the orientation of the screen
      //  if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        //    getAllProducts(String.valueOf(pageNo));
         //   Log.d("Daiya", "ORIENTATION_LANDSCAPE");

       // } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
           // getAllProducts(String.valueOf(pageNo));
           // Log.d("Daiya", "ORIENTATION_PORTRAIT");
      //  }
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
            jsonObject.put("ProductName",fillterLetter);
            jsonObject.put("PageSize",50);
            jsonObject.put("PageNo",pageNo);
            getAllProducts(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("Given_load_page_no",pageNo+"");
        productsAdapterNew.notifyDataSetChanged(); // more elements will be added

    }

    public void getProductPrice(String productId) throws JSONException {
        //  {"CompanyCode":"1","CustomerCode":"0003432","LocationCode":"HQ","ProductCode":"0000009"}
        // Initialize a new RequestQueue instance
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

    public void showImageFile(File imageUrl) {
        Dialog builder = new Dialog(getActivity(),android.R.style.Theme_Light);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
      //  builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        builder.setCanceledOnTouchOutside(true);
        ImageView imageView = new ImageView(getActivity());
        ImageView closeImage=new ImageView(getActivity());
        closeImage.setImageResource(R.drawable.ic_baseline_close_24);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;
        //layoutParams.setMargins(10, 10, 10, 10); // (left, top, right, bottom)
        closeImage.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        //layoutParams.setMargins(10, 10, 10, 10); // (left, top, right, bottom)
        imageView.setLayoutParams(layoutParams1);

        FrameLayout frameLayout=new FrameLayout(getActivity());
        frameLayout.addView(imageView);
        frameLayout.addView(closeImage);
        builder.addContentView(frameLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Glide.with(getActivity())
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

    public void showImageUrl(String imageUrl) {
        Dialog builder = new Dialog(getActivity(),android.R.style.Theme_Light);
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

        ImageView imageView = new ImageView(getActivity());
        Glide.with(getActivity())
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
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }

}