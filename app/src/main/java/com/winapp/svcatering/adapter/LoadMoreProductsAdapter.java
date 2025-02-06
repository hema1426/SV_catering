package com.winapp.svcatering.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.ProductsModel;

import java.util.ArrayList;
import java.util.Objects;

public class LoadMoreProductsAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private ArrayList<ProductsModel> productList;

    // The minimum amount of items to have below your current scroll position
// before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    public LoadMoreProductsAdapter(Context mContext, ArrayList<ProductsModel> productsList, RecyclerView recyclerView) {
        productList = productsList;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return productList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.product_items, parent, false);

            vh = new ProductViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_bar, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductViewHolder) {

            try {
                final ProductsModel products = (ProductsModel) productList.get(position);
                // loading products cover using Glide library
                ((ProductViewHolder) holder).productName.setText(products.getProductName());
                String input = products.getProductImage();
                boolean isBase64 = input.indexOf("base64") != -1 ? true : false;
                if (isBase64) {
                    Bitmap bitmap=getImage(products.getProductImage());
                    ((ProductViewHolder) holder).thumbnail.setImageBitmap(bitmap);
                }else {
                    ((ProductViewHolder) holder).thumbnail.setImageResource(R.drawable.no_image_found);
                }
                // OnclickListener on Image Thumbnail
                ((ProductViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      //  callBack.callDescription(products.getProductImage());
                    }
                });
            }catch (Exception ex){
                Log.e("Error_in_adapter:", Objects.requireNonNull(ex.getMessage()));
            }

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    //
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        // Declare the Required Variables
        private ImageView thumbnail;
        private TextView productName;
        private TextView stockStatus;
        private TextView ctnQty;
        private TextView netPcs;
        private TextView ctnPcs;
        LinearLayout statusLayout;

        public ProductViewHolder(View view) {
            super(view);
            productName=view.findViewById(R.id.product_name);
            stockStatus=view.findViewById(R.id.stock_status);
            ctnQty=view.findViewById(R.id.ctn_qty);
            netPcs=view.findViewById(R.id.net_price);
            ctnPcs=view.findViewById(R.id.stock_count);
            thumbnail = view.findViewById(R.id.thumbnail);
            statusLayout=view.findViewById(R.id.status_layout);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public Bitmap getImage(String base64String){
        String base64Image = base64String.split(",")[1];
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public void filterList(ArrayList<ProductsModel> filterdNames) {
        this.productList = filterdNames;
        notifyDataSetChanged();
    }
}