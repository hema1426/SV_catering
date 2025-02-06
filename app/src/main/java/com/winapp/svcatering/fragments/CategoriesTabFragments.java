package com.winapp.svcatering.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.DescriptionActivity;
import com.winapp.svcatering.activity.MainHomeActivity;
import com.winapp.svcatering.adapter.LoadMoreProductsAdapter;
import com.winapp.svcatering.adapter.ProductAdapterLoadMore;
import com.winapp.svcatering.adapter.ProductsAdapter;
import com.winapp.svcatering.adapter.SortAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.CustomRecyclerView;
import com.winapp.svcatering.utils.GridSpacingItemDecoration;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

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

public class CategoriesTabFragments extends Fragment implements PopupMenu.OnMenuItemClickListener, ProductAdapterLoadMore.Callbacks{

    private static final String ARG_SECTION_NUMBER = "section_number";
    private String catagoriesId;
    private View progressBarLayout;
    private ProductsAdapter productsAdapter;
    private ProductAdapterLoadMore productsAdapterNew;
    private RecyclerView productsView;
    SweetAlertDialog pDialog;
    public static ArrayList<ProductsModel> productListFilter;
    public static ArrayList<ProductsModel> productListDisplay;
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
    BottomSheetBehavior behavior;

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
    LinearLayout transLayout;
    ImageView cancelSheet;
    String userName;
    String locationCode;
    LinearLayout sortLayout;
    TextView emptyText;
    static ProgressDialog dialog;
    public String selectCustomerId;


    public CategoriesTabFragments() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        catagoriesId = getArguments() != null ? getArguments().getString(ARG_SECTION_NUMBER) : "1";
        Log.w("CatagoriesId",catagoriesId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.catagories_tabs_layout, container, false);
        session=new SessionManager(getActivity());
        dbHelper=new DBHelper(getActivity());
        user=session.getUserDetails();
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        progressBarLayout = view.findViewById(R.id.progress_layout);
        productsView =view.findViewById(R.id.categoriesView);
        progressBarLayout.setVisibility(View.GONE);
        lettersRecyclerview=view.findViewById(R.id.sorting_letters);
        emptyLayout=view.findViewById(R.id.empty_layout);
        sortButton=view.findViewById(R.id.fab);
        sortLayout=view.findViewById(R.id.sort_layout);
        emptyText=view.findViewById(R.id.empty_text);

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
        // portraitLayout=view.findViewById(R.id.description_layout_portrait);
        // landscapeLayout=view.findViewById(R.id.description_layout_land);

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("CategoryCode",catagoriesId);
            jsonObject.put("LocationCode",locationCode);
            getAllProducts(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int spanCount=0;
        int dp=0;
        if (Utils.isTablet(getActivity())){
            spanCount=3;
            dp=10;
        }else {
            spanCount=2;
            dp=10;
        }

        int mNoOfColumns = Utils.calculateNoOfColumns(getActivity(), 180);
        // GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, mNoOfColumns);
        mLayoutManager = new GridLayoutManager(getActivity(), mNoOfColumns);
        productsView.setLayoutManager(mLayoutManager);
        // categoriesView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        productsView.addItemDecoration(new GridSpacingItemDecoration(spanCount, GridSpacingItemDecoration.dpToPx(getActivity(), dp), true));
        productsView.setItemAnimator(new DefaultItemAnimator());
        // define the sorting letters
        lettersRecyclerview.setHasFixedSize(true);
        // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        lettersRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        SortAdapter adapter = new SortAdapter(Utils.getSorting(), new SortAdapter.CallBack() {
            @Override
            public void sortProduct(String letter) {
                if (letter.equals("All")){
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("CompanyCode",companyCode);
                        jsonObject.put("LocationCode",locationCode);
                        jsonObject.put("CategoryCode",catagoriesId);
                        jsonObject.put("PageSize",50);
                        jsonObject.put("PageNo",pageNo);
                        getAllProducts(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    filter(letter);
                }
            }
        });
        lettersRecyclerview.setAdapter(adapter);

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
                if (!ctnQtyValue.getText().toString().equals("0")){
                    int count=Integer.parseInt(ctnQtyValue.getText().toString());
                    int ctn=count-1;
                    ctnQtyValue.setText(String.valueOf(ctn));
                    setCalculation();
                }
            }
        });

        ctnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count=Integer.parseInt(ctnQtyValue.getText().toString());
                int ctn=count+1;
                ctnQtyValue.setText(String.valueOf(ctn));
                setCalculation();
            }
        });

        pcsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pcsQtyValue.getText().toString().equals("0")){
                    int count=Integer.parseInt(pcsQtyValue.getText().toString());
                    int ctn=count-1;
                    pcsQtyValue.setText(String.valueOf(ctn));
                    setCalculation();
                }
            }
        });

        pcsPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count=Integer.parseInt(pcsQtyValue.getText().toString());
                int ctn=count+1;
                pcsQtyValue.setText(String.valueOf(ctn));
                setCalculation();
            }
        });


        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                    cartonprice=ctnPrice.getText().toString();
                }

                if (!discountEditext.getText().toString().isEmpty()){
                    discount=discountEditext.getText().toString();
                }


                double return_amt=(Double.parseDouble(return_qty)*Double.parseDouble(lPriceCalc));
                double total=(Double.parseDouble(ctn_qty) * Double.parseDouble(cartonprice)) + (Double.parseDouble(loose_qty) * Double.parseDouble(lPriceCalc));
                double sub_total=total-return_amt-Double.parseDouble(discount);


                if (pcsQtyValue.getText().toString().isEmpty() && ctnQtyValue.getText().toString().isEmpty()){
                    isQtyEntered=false;
                }else isQtyEntered= !pcsQtyValue.getText().toString().equals("0") || !ctnQtyValue.getText().toString().equals("0");


                SharedPreferences sharedPreferences = getContext().getSharedPreferences("customerPref",MODE_PRIVATE);
                SharedPreferences.Editor customerPredEdit= sharedPreferences.edit();
                String selectCustomerId = sharedPreferences.getString("customerId", "");
                if (!selectCustomerId.isEmpty()){
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
                                ctnPrice.getText().toString(),
                                unitPrice.getText().toString(),
                                String.valueOf(pcspercarton),
                                taxTextView.getText().toString(),
                                String.valueOf(sub_total),
                                customerDetails.get(0).getTaxType(),
                                focEditText.getText().toString(),
                                focType,
                                exchangeEditext.getText().toString(),
                                exchangeType,
                                discountEditext.getText().toString(),
                                returnEditext.getText().toString(),
                                returnType,"",String.valueOf(total),availability.getText().toString(),
                                "","0.00",availability.getText().toString());
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
                            Utils.refreshActionBarMenu(getActivity());
                            MainHomeActivity.setupBadge(getContext());

                            //  Intent intent=new Intent(DescriptionActivity.this,MainHomeActivity.class);
                            //  startActivity(intent);
                            //  finish();
                        }else {
                            Toast.makeText(getActivity(),"Error in Add product",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getActivity(),"Add the Qty",Toast.LENGTH_SHORT).show();
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

        cancelSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });


        return  view;
    }

    public void setData(ProductsModel model){
        //  Gson gson = new Gson();
        // ProductsModel model = gson.fromJson(getIntent().getStringExtra("productDetails"), ProductsModel.class);
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("customerPref",MODE_PRIVATE);
            SharedPreferences.Editor customerPredEdit= sharedPreferences.edit();
            String selectCustomerId = sharedPreferences.getString("customerId", "");
            customerDetails=new ArrayList<>();
            if (selectCustomerId!=null && !selectCustomerId.isEmpty()){
                customerDetails=dbHelper.getCustomer(selectCustomerId);
                getProductPrice(model.getProductCode());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        viewCloseBottomSheet();
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
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
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

        try {
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
                        taxTitle.setText("Tax ( Exclusive )");
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
                        taxAmount = (subTotal * taxValueCalc) / (100 + taxValueCalc);
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
        }catch (Exception ex){}

    }

    public void viewCloseBottomSheet() {
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        // get the Customer name from the local db
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

    @SuppressLint("SetTextI18n")
    private void populateCategoriesData(ArrayList<ProductsModel> productList) {
        try {
            productListDisplay=new ArrayList<>();
            productListDisplay.clear();
            if (productList.size()>0){
                productListDisplay=productList;
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
                        setData(model);
                        // viewCloseBottomSheet();
                    }
                });
                productsAdapterNew.setCallback(this);
                productsAdapterNew.setWithFooter(false); //enabling footer to show
                productsView.setAdapter(productsAdapterNew);
                productsView.setVisibility(View.VISIBLE);
                sortLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);

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
                sortLayout.setVisibility(View.GONE);
                emptyText.setText("No Products Found !");
                emptyLayout.setVisibility(View.VISIBLE);
                productsView.setVisibility(View.GONE);
            }
        }catch (Exception ex){
            Log.e("TAG","Error in Populating the data:"+ex.getMessage());
        }
    }

    public void showAlert(){
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
    }

    public void showLowStock(){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
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
            // sortAdapter.resetPosition();
            ArrayList<ProductsModel> filterdNames = new ArrayList<>();
            for (ProductsModel s : productsAdapterNew.getList()) {
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
                emptyText.setText("No Products found!");
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
            for (ProductsModel s : productListFilter) {
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
                emptyText.setText("No Products found!");
                emptyLayout.setVisibility(View.VISIBLE);
                productsView.setVisibility(View.GONE);
            }
        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onClickLoadMore() {
        Log.w("Load_more_calling:","Success");
      /*  productsAdapterNew.setWithFooter(false); // hide footer
        pageNo=pageNo+1;
        // now add remaining elements
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("CompanyCode",companyCode);
            jsonObject.put("LocationCode",locationCode);
            jsonObject.put("CategoryCode",catagoriesId);
            jsonObject.put("PageSize",50);
            jsonObject.put("PageNo",pageNo);

            getAllProducts(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        productsAdapterNew.notifyDataSetChanged(); // more elements will be added
*/
    }

    public void getAllProducts(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url=Utils.getBaseUrl(getContext()) +"CategoryDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_categ_pdt_url:",url+"--"+jsonObject.toString());
        newProductList=new ArrayList<>();
        productListFilter=new ArrayList<>();
        ArrayList<ProductsModel> productList=new ArrayList<>();
        productList.clear();
        emptyLayout.setVisibility(View.VISIBLE);
        emptyText.setText("Products Loading...");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject,
                response -> {
                    try{
                        Log.w("ResCateg_pdt:",response.toString());
                        // Loop through the array elements
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray responseData=response.optJSONArray("responseData");
                            JSONObject responseObject=responseData.getJSONObject(0);
                            JSONArray detailsArray=responseObject.optJSONArray("productDetails");
                            for(int i=0;i<detailsArray.length();i++){
                                // Get current json object
                                JSONObject productObject = detailsArray.getJSONObject(i);
                                ProductsModel product =new ProductsModel();
                               // if (productObject.optBoolean("IsActive")) {
                                    product.setCompanyCode(productObject.optString("CompanyCode"));
                                    product.setProductName(productObject.optString("productName"));
                                    product.setProductCode(productObject.optString("productCode"));
                                    product.setWeight("0.00");
                                    product.setProductImage(productObject.optString("imageURL"));
                                    product.setWholeSalePrice(productObject.optString("price"));
                                    product.setRetailPrice(productObject.optDouble("price"));
                                    product.setCartonPrice(productObject.optString("price"));
                                    product.setUnitCost(productObject.optString("price"));
                                    product.setLastPrice( productObject.optString("lastSalesPrice"));
                                    product.setPcsPerCarton(productObject.optString("pcsPerCarton"));
                                product.setDefaultUom(productObject.optString("defaultSalesUOM"));
                                if(!productObject.optString("uomCode").isEmpty() || productObject.optString("uomCode")!= null){
                                    product.setUomCode(productObject.optString("uomCode"));
                                }
                                else{
                                    product.setUomCode("PCS");
                                }
                                    product.setStockQty(productObject.optString("stockInHand"));
                                    // newProductList.add(product);
                                    productList.add(product);
                                    Log.w("pdtsizeCatgry",""+productList.size());
                               // }
                            }
                            // productList.addAll(newProductList);
                            productListFilter.addAll(productList);
                            emptyLayout.setVisibility(View.GONE);
                            //  pDialog.dismiss();
                            populateCategoriesData(productList);
                        }else {

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    emptyLayout.setVisibility(View.GONE);
                    // Do something when error occurred
                   // pDialog.dismiss();
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

    public static CategoriesTabFragments newInstance(String categoriesId) {
        CategoriesTabFragments fragment = new CategoriesTabFragments();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_NUMBER, categoriesId);
        fragment.setArguments(args);
        return fragment;
    }
}