package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.bumptech.glide.request.target.Target;
import com.winapp.svcatering.R;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This Adapter class used to display the All profile images and names
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProfileViewHolder> {
    /**
     * Declare the Context and Arraylist variables
     */
    private Context mContext;
    private ArrayList<ProductsModel> productsList;
    SharedPreferences sharedpreferences;
    private int mContainerId;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private final static String TAG = "DashBoardActivity";
    CallBack callBack;
    DBHelper dbHelper;
    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        // Declare the Required Variables
        private ImageView thumbnail;
        private final ProgressBar progressBar;
        private TextView productName;
        private TextView stockStatus;
        private TextView ctnQty;
        private TextView netPcs;
        private TextView stockCount;
        LinearLayout statusLayout;
        public ProfileViewHolder(View view) {
            super(view);
            productName=view.findViewById(R.id.product_name);
            stockStatus=view.findViewById(R.id.stock_status);
            ctnQty=view.findViewById(R.id.ctn_qty);
            netPcs=view.findViewById(R.id.net_price);
            stockCount =view.findViewById(R.id.stock_count);
            thumbnail = view.findViewById(R.id.thumbnail);
            progressBar = view.findViewById(R.id.progressBar);
            statusLayout=view.findViewById(R.id.status_layout);
        }
    }

    /**
     * Constructor for the send the arraylist and context
     * @param mContext
     * @param productsList
     */
    public ProductsAdapter(Context mContext, ArrayList<ProductsModel> productsList, CallBack callBack) {
        this.mContext = mContext;
        this.productsList = Utils.getProductList(productsList);
        this.callBack=callBack;
    }
    /**
     *
     * @param parent
     * @param viewType
     * @return
     */

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_items, parent, false);
        return new ProfileViewHolder(itemView);
    }

    /**
     *
     * @param holder
     * @param position
     */

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ProfileViewHolder holder, final int position) {
        try {
            final ProductsModel products = productsList.get(position);
            dbHelper=new DBHelper(mContext);
            // loading products cover using Glide library
            holder.productName.setText(products.getProductName().trim());
            holder.netPcs.setText("$ "+String.valueOf(products.getRetailPrice()));
            holder.ctnQty.setText("CTN : "+products.getPcsPerCarton());

            if (products.getStockQty().equals("null")){
                holder.stockCount.setText("STOCK : 0");
            }else {
                holder.stockCount.setText("STOCK :"+products.getStockQty());
            }

          /*  String input = products.getProductImage();
            boolean isBase64 = input.indexOf("base64") != -1 ? true : false;
            if (isBase64) {
                Bitmap bitmap=getImage(products.getProductImage());
                holder.thumbnail.setImageBitmap(bitmap);
            }else {
                holder.thumbnail.setImageResource(R.drawable.no_image_found);
            }*/

         // #AF0606 red Color
            // # 08A703 Green Color



            if (products.getStockQty()!=null && !products.getStockQty().equals("null")){
                if (Double.parseDouble(products.getStockQty())==0){
                    holder.stockStatus.setText("No Stock");
                    holder.stockStatus.setBackgroundColor(Color.parseColor("#EC7063"));
                    holder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                }else if (Double.parseDouble(products.getStockQty()) > 5 && Double.parseDouble(products.getStockQty()) < 10){
                    holder.stockStatus.setText("In Stock");
                    holder.stockStatus.setBackgroundColor(Color.parseColor("#5DADE2"));
                    holder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                }else if (Double.parseDouble(products.getStockQty()) >10){
                    holder.stockStatus.setText("In Stock");
                    holder.stockStatus.setBackgroundColor(Color.parseColor("#58D68D"));
                    holder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                }else if (Double.parseDouble(products.getStockQty()) < 5){
                    holder.stockStatus.setText("In Stock");
                    holder.stockStatus.setBackgroundColor(Color.parseColor("#85C1E9"));
                    holder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }else {
                holder.stockStatus.setText("No Stock");
                holder.stockStatus.setBackgroundColor(Color.parseColor("#EC7063"));
                holder.stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
            }

            // Getting image from the Local DB
            String folderPath = Environment.getExternalStorageDirectory() + "/CatalogErp/Products";
            String imagePath=dbHelper.getProductImage(products.getProductCode());
            if (!imagePath.isEmpty()){
                String imageFullpath=folderPath+"/"+imagePath;
                File file = new File(imageFullpath);
                Uri imageUri = Uri.fromFile(file);
                Glide.with(mContext).load(imageUri)
                        .fitCenter()
                        .into(holder.thumbnail);
                Log.w("GivenImagePath:",folderPath+"/"+imagePath);
            }else {
                if (products.getProductImage() != null && !products.getProductImage().equals("null")) {
                    Glide.with(mContext)
                            .load(products.getProductImage())
                            .error(R.drawable.no_image_found)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    holder.thumbnail.setImageResource(R.drawable.no_image_found);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(holder.thumbnail);
                } else {
                    holder.thumbnail.setImageResource(R.drawable.no_image_found);
                }
            }

            // OnclickListener on Image Thumbnail
            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.callDescription(
                            products.getProductCode(),
                            products.getProductName(),
                            String.valueOf(products.getRetailPrice()),
                            products.getProductImage(),
                            products.getWeight(),
                            String.valueOf(products.getRetailPrice()),
                            products.getUnitCost(),
                            products.getPcsPerCarton(),
                            products.getStockQty()
                    );

                 /*   if (!holder.stockStatus.getText().equals("No Stock")){
                        callBack.showLowStockAlert();
                    }else {
                        callBack.callDescription(
                                products.getProductCode(),
                                products.getProductName(),
                                String.valueOf(products.getRetailPrice()),
                                products.getProductImage(),
                                products.getWeight(),
                                String.valueOf(products.getRetailPrice()),
                                products.getUnitCost(),
                                products.getPcsPerCarton(),
                                products.getStockQty()
                        );
                    }*/

                }
            });
        }catch (Exception ex){ Log.e("Error_in_adapter:", Objects.requireNonNull(ex.getMessage()));
        }
    }
    /**
     *
     * @return array list count
     */
    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public interface CallBack {
        void callDescription(String productId,String productName,String price,String imageString,String weight,String cartonPrice,String unitPrice,String pcsPercarton,String stock);
        void showLowStockAlert();
    }

    public Bitmap getImage(String base64String){
        String base64Image = base64String.split(",")[1];
        byte[] decodedString = Base64.decode(base64Image, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public void filterList(ArrayList<ProductsModel> filterdNames) {
        this.productsList = filterdNames;
        notifyDataSetChanged();
    }
}