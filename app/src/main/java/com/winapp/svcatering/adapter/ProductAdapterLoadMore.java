package com.winapp.svcatering.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.DescriptionActivity;
import com.winapp.svcatering.activity.MainHomeActivity;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.ProductsModel;
import com.google.gson.Gson;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sathish on 02/09/2020.
 */

public class ProductAdapterLoadMore extends RecyclerView.Adapter {

    public interface Callbacks {
        public void onClickLoadMore();
    }

    private Callbacks mCallbacks;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private boolean mWithHeader = false;
    private boolean mWithFooter = false;
    private Context mContext;
    private ArrayList<ProductsModel> productsList;
    private ArrayList<ProductsModel> productListDisplay;
    private SharedPreferences sharedpreferences;
    private int mContainerId;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private final static String TAG = "DashBoardActivity";
    private CallBack callBack;
    private ArrayList<CustomerDetails> customerDetails;
    private DBHelper dbHelper;
    boolean isAllowLowStock=false;


    public  ProductAdapterLoadMore(Context context, ArrayList<ProductsModel> productsList, CallBack callBack) {
        this.productsList = Utils.getProductList(productsList);
        this.productListDisplay=Utils.getProductList(productsList);
        this.mContext=context;
        this.callBack=callBack;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == TYPE_FOOTER) {
            itemView = View.inflate(parent.getContext(), R.layout.load_more_layout, null);
            return new LoadMoreViewHolder(itemView);
        } else {
            itemView = View.inflate(parent.getContext(), R.layout.product_items, null);
            return new ElementsViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof LoadMoreViewHolder) {
            LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
            loadMoreViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mCallbacks!=null)
                        mCallbacks.onClickLoadMore();
                }
            });
        } else {
            ElementsViewHolder productViewHolder = (ElementsViewHolder) holder;
            try {
                final ProductsModel products = productsList.get(position);
                dbHelper=new DBHelper(mContext);
                customerDetails=dbHelper.getCustomer();
                // loading products cover using Glide library
                productViewHolder.productName.setText(products.getProductName().trim());
                productViewHolder.productPrice.setText("$ "+String.valueOf(products.getWholeSalePrice()));
                productViewHolder.ctnQty.setText("CTN : "+(int)Double.parseDouble(products.getPcsPerCarton()));

                if (products.getStockQty().equals("null")){
                    productViewHolder.stockCount.setText("STK : 0");
                }else {
                    productViewHolder.stockCount.setText("STK : "+(int)Double.parseDouble(products.getStockQty()));
                }

                if (products.getStockQty()!=null && !products.getStockQty().equals("null")){
                    if (Double.parseDouble(products.getStockQty())==0){
                        productViewHolder.stockStatus.setText("No Stock");
                        productViewHolder.stockStatus.setBackgroundColor(Color.parseColor("#D24848"));
                        productViewHolder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                    }else if (Double.parseDouble(products.getStockQty())>0){
                        productViewHolder.stockStatus.setText("In Stock");
                        productViewHolder.stockStatus.setBackgroundColor(Color.parseColor("#2ECC71"));
                        productViewHolder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                    }else if (Double.parseDouble(products.getStockQty())<0){
                        productViewHolder.stockStatus.setText("No Stock");
                        productViewHolder.stockStatus.setBackgroundColor(Color.parseColor("#D24848"));
                        productViewHolder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                    /*else if (Double.parseDouble(products.getStockQty()) >10){
                        productViewHolder.stockStatus.setText("In Stock");
                        productViewHolder.stockStatus.setBackgroundColor(Color.parseColor("#58D68D"));
                        productViewHolder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                    }else if (Double.parseDouble(products.getStockQty()) < 5){
                        productViewHolder.stockStatus.setText("In Stock");
                        productViewHolder.stockStatus.setBackgroundColor(Color.parseColor("#85C1E9"));
                        productViewHolder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                    }*/
                }else {
                    productViewHolder.stockStatus.setText("No Stock");
                    productViewHolder.stockStatus.setBackgroundColor(Color.parseColor("#D24848"));
                    productViewHolder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                }


           /*     if (products.getStockQty()!=null && !products.getStockQty().equals("null")){
                    if (Double.parseDouble(products.getStockQty())==0){
                        productViewHolder.stockStatus.setText("No Stock");
                        productViewHolder.stockStatus.setBackgroundColor(Color.parseColor("#EC7063"));
                        productViewHolder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                    }else if (Double.parseDouble(products.getStockQty()) > 5 && Double.parseDouble(products.getStockQty()) < 10){
                        productViewHolder.stockStatus.setText("In Stock");
                        productViewHolder.stockStatus.setBackgroundColor(Color.parseColor("#5DADE2"));
                        productViewHolder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                    }else if (Double.parseDouble(products.getStockQty()) >10){
                        productViewHolder.stockStatus.setText("In Stock");
                        productViewHolder.stockStatus.setBackgroundColor(Color.parseColor("#58D68D"));
                        productViewHolder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                    }else if (Double.parseDouble(products.getStockQty()) < 5){
                        productViewHolder.stockStatus.setText("In Stock");
                        productViewHolder.stockStatus.setBackgroundColor(Color.parseColor("#85C1E9"));
                        productViewHolder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                }else {
                    productViewHolder.stockStatus.setText("No Stock");
                    productViewHolder.stockStatus.setBackgroundColor(Color.parseColor("#EC7063"));
                    productViewHolder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                }*/

                // Getting image from the Local DB
                String imagePath=dbHelper.getProductImage(products.getProductCode());
                if (!imagePath.isEmpty()){
                      String imageFullpath= Constants.folderPath +"/"+imagePath;
                      File file = new File(imageFullpath);
                      Log.w("RealImagePath:",imageFullpath);
                      if (file.exists()){
                          Glide.with(mContext)
                                  .asBitmap()
                                  //.apply(myOptions)
                                  .load(file)
                                  .error(R.drawable.no_image_found)
                                  .listener(new RequestListener<Bitmap>() {
                                      @Override
                                      public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                          productViewHolder.thumbnail.setImageResource(R.drawable.no_image_found);
                                          return false;
                                      }
                                      @Override
                                      public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                          return false;
                                      }
                                  }).into(productViewHolder.thumbnail);
                      }else {
                          Glide.with(mContext)
                                  .asBitmap()
                                  //.apply(myOptions)
                                  .load(products.getProductImage())
                                  .error(R.drawable.no_image_found)
                                  .listener(new RequestListener<Bitmap>() {
                                      @Override
                                      public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                          productViewHolder.thumbnail.setImageResource(R.drawable.no_image_found);
                                          return false;
                                      }
                                      @Override
                                      public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                          return false;
                                      }
                                  }).into(productViewHolder.thumbnail);
                      }
                }else {
                    if (products.getProductImage()!=null && !products.getProductImage().equals("null") ){
                        RequestOptions myOptions = new RequestOptions().fitCenter();
                        Glide.with(mContext)
                                .asBitmap()
                                //.apply(myOptions)
                                .load(products.getProductImage())
                                .error(R.drawable.no_image_found)
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        productViewHolder.thumbnail.setImageResource(R.drawable.no_image_found);
                                        return false;
                                    }
                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                }).into(productViewHolder.thumbnail);
                    }else {
                        productViewHolder.thumbnail.setImageResource(R.drawable.no_image_found);
                    }
                }
                // OnclickListener on Image Thumbnail
                productViewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("customerPref",MODE_PRIVATE);
                        SharedPreferences.Editor customerPredEdit= sharedPreferences.edit();
                        String selectCustomerId = sharedPreferences.getString("customerId", "");
                        customerDetails=new ArrayList<>();
                        if (selectCustomerId!=null && !selectCustomerId.isEmpty()){
                            customerDetails=dbHelper.getCustomer(selectCustomerId);
                        }
                        ArrayList<SettingsModel> settings=dbHelper.getSettings();
                        if (settings.size()>0) {
                            for (SettingsModel model : settings) {
                                if (model.getSettingName().equals("allow_negative_switch")) {
                                    isAllowLowStock= model.getSettingValue().equals("1");
                                }
                            }
                        }
                        if (productViewHolder.stockStatus.getText().equals("No Stock")){
                            if (isAllowLowStock){

                                // Disable the Customer Choose for Showing the Products
                               // if (customerDetails.size()>0){
                                    int orientation = mContext.getResources().getConfiguration().orientation;
                                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                                        // code for portrait mode
                                        if (mContext instanceof MainHomeActivity){
                                            callBack.showBottomDescription(products);
                                        }else {
                                            Gson gson = new Gson();
                                            String object = gson.toJson(products);
                                            Intent intent = new Intent(mContext, DescriptionActivity.class);
                                            intent.putExtra("productDetails", object);
                                            mContext.startActivity(intent);
                                        }
                                    } else {
                                        // code for landscape mode
                                        Gson gson = new Gson();
                                        String object = gson.toJson(products);
                                        Intent intent = new Intent(mContext, DescriptionActivity.class);
                                        intent.putExtra("productDetails", object);
                                        mContext.startActivity(intent);
                                    }
                               // }else {
                                 //   showAlert();
                               // }
                            }else {
                              //  callBack.showLowStockAlert();
                                Gson gson = new Gson();
                                String object = gson.toJson(products);
                                Intent intent = new Intent(mContext, DescriptionActivity.class);
                                intent.putExtra("productDetails", object);
                                mContext.startActivity(intent);
                            }
                        }else {
                           // if (customerDetails.size()>0){
                                int orientation = mContext.getResources().getConfiguration().orientation;
                                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                                    // code for portrait mode
                                    if (mContext instanceof MainHomeActivity /*|| mContext instanceof CategoriesActivity*/){
                                        callBack.showBottomDescription(products);
                                    }else {
                                        Gson gson = new Gson();
                                        String object = gson.toJson(products);
                                        Intent intent = new Intent(mContext, DescriptionActivity.class);
                                        intent.putExtra("productDetails", object);

                                        mContext.startActivity(intent);

                                    }
                                } else {
                                    // code for landscape mode
                                      Gson gson = new Gson();
                                      String object = gson.toJson(products);
                                      Intent intent = new Intent(mContext, DescriptionActivity.class);
                                      intent.putExtra("productDetails", object);
                                    mContext.startActivity(intent);
                                }
                           // }else {
                              //  showAlert();
                           // }
                        }
                    }
                });
            }catch (Exception ex){ Log.e("Error_in_adapter:", Objects.requireNonNull(ex.getMessage()));
            }
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = productsList.size();
        if (mWithHeader)
            itemCount++;
        if (mWithFooter)
            itemCount++;
        return itemCount;
    }

    public void showAlert(){
        try {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
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

    @Override
    public int getItemViewType(int position) {
        if (mWithHeader && isPositionHeader(position))
            return TYPE_HEADER;
        if (mWithFooter && isPositionFooter(position))
            return TYPE_FOOTER;
        return TYPE_ITEM;
    }

    public boolean isPositionHeader(int position) {
        return position == 0 && mWithHeader;
    }

    public boolean isPositionFooter(int position) {
        return position == getItemCount() - 1 && mWithFooter;
    }

    public void setWithHeader(boolean value){
        mWithHeader = value;
    }

    public void setWithFooter(boolean value){
        mWithFooter = value;
    }

    public void setCallback(Callbacks callbacks){
        mCallbacks = callbacks;
    }

    public class ElementsViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnail;
        private TextView productName;
        private TextView stockStatus;
        private TextView ctnQty;
        private TextView productPrice;
        private TextView stockCount;
        private LinearLayout statusLayout;

        public ElementsViewHolder(View view) {
            super(view);
            productName=view.findViewById(R.id.product_name);
            stockStatus=view.findViewById(R.id.stock_status);
            ctnQty=view.findViewById(R.id.ctn_qty);
            productPrice =view.findViewById(R.id.net_price);
            stockCount =view.findViewById(R.id.stock_count);
            thumbnail = view.findViewById(R.id.thumbnail);
          //  progressBar = view.findViewById(R.id.progressBar);
            statusLayout=view.findViewById(R.id.status_layout);
        }
    }

    public class LoadMoreViewHolder  extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public LoadMoreViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar1);
        }
    }

    public interface CallBack {
        void callDescription(String productId,String productName,String price,String imageString,String weight,String cartonPrice,String unitPrice,String pcsPercarton,String stock);
        void showLowStockAlert();
        void showBottomDescription(ProductsModel model);
    }

    public void filterList(ArrayList<ProductsModel> products) {
        this.productsList = Utils.getProductList(products);
        notifyDataSetChanged();
    }

    public ArrayList<ProductsModel> getList(){
        return  this.productListDisplay;
    }


}