package com.winapp.svcatering.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.winapp.svcatering.R;
import com.winapp.svcatering.model.ProductsModel;

import java.util.ArrayList;

public class ProductsAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    ArrayList<ProductsModel> productsList;
    Context mContext;
    CallBack callBack;

    public ProductsAdapterNew(Context context, RecyclerView mRecyclerView, ArrayList<ProductsModel> invoiceList,CallBack callBack) {

        this.productsList =invoiceList;
        this.mContext=context;
        this.callBack=callBack;

        final GridLayoutManager gridLayoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                assert gridLayoutManager != null;
                totalItemCount = gridLayoutManager.getChildCount();
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return productsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.product_items, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  viewHolder, int position) {
        if ( viewHolder instanceof UserViewHolder) {
            ProductsModel productsModel = productsList.get(position);
            final ProductsModel products = productsList.get(position);
            // loading products cover using Glide library
            ((UserViewHolder) viewHolder).productName.setText(products.getProductName());
            ((UserViewHolder) viewHolder).netPcs.setText("$ "+String.valueOf(products.getRetailPrice()));
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
            if (products.getStockQty()!=null){
                ((UserViewHolder) viewHolder).stockStatus.setText("In Stock");
                //   holder.stockStatus.setBackgroundColor(Color.parseColor("#F50B0B"));
                ((UserViewHolder) viewHolder).stockStatus.setTextColor(Color.parseColor("#FFFFFF"));
            }else {
                ((UserViewHolder) viewHolder).stockStatus.setText("No Stock");
                ((UserViewHolder) viewHolder).stockStatus.setBackgroundColor(Color.parseColor("#E74C3C"));
                ((UserViewHolder) viewHolder).stockStatus.setTextColor(Color.parseColor("#FFFFF"));
            }

            if (products.getProductImage()!=null && !products.getProductImage().equals("null") ){
                Glide.with(mContext)
                        .load(products.getProductImage())
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
                        }).into(((UserViewHolder) viewHolder).thumbnail);
            }else {
                ((UserViewHolder) viewHolder).thumbnail.setImageResource(R.drawable.no_image_found);
            }

            // OnclickListener on Image Thumbnail
            ((UserViewHolder) viewHolder).thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ( ((UserViewHolder) viewHolder).stockStatus.getText().equals("No Stock")){

                    }else {
                        callBack.callDescription(
                                products.getProductCode(),
                                products.getProductName(),
                                String.valueOf(products.getRetailPrice()),
                                products.getProductImage(),
                                products.getWeight(),
                                products.getCartonPrice(),
                                products.getUnitCost(),
                                products.getPcsPerCarton(),
                                products.getStockQty()
                        );
                    }
                }
            });

        } else if ( viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)  viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return productsList == null ? 0 : productsList.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        // Declare the Required Variables
        private ImageView thumbnail;
        private TextView productName;
        private TextView stockStatus;
        private TextView ctnQty;
        private TextView netPcs;
        private TextView stockCount;
        LinearLayout statusLayout;
        //    private TextView address;
        public UserViewHolder(View view) {
            super(view);
            productName=view.findViewById(R.id.product_name);
            stockStatus=view.findViewById(R.id.stock_status);
            ctnQty=view.findViewById(R.id.ctn_qty);
            netPcs=view.findViewById(R.id.net_price);
            stockCount =view.findViewById(R.id.stock_count);
            thumbnail = view.findViewById(R.id.thumbnail);
            statusLayout=view.findViewById(R.id.status_layout);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar1);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface CallBack {
        void callDescription(String productId,String productName,String price,String imageString,String weight,String cartonPrice,String unitPrice,String pcsPercarton,String stock);
        void showLowStockAlert();
    }
}