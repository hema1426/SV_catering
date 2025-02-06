package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.salesreturn.SalesReturnActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NewSelectProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    /**
     * @define the variable as we need and load more functions
     * @define Array list for the load the products
     */

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    public  ArrayList<ProductsModel> productList;
    public  ArrayList<ProductsModel> productFilterList;
    Context mContext;
    CallBack callBack;

    public NewSelectProductAdapter(Context context, RecyclerView mRecyclerView, ArrayList<ProductsModel> salesOrderList,CallBack callBack) {
        this.productList = salesOrderList;
        this.productFilterList =salesOrderList;
        this.mContext=context;
        this.callBack=callBack;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

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
        return productList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.select_product_items, parent, false);
            return new ProductListViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder  viewHolder, int position) {
        if ( viewHolder instanceof ProductListViewHolder) {
            ProductsModel model = productList.get(position);
            ((ProductListViewHolder) viewHolder).productName.setText(model.getProductName());
            ((ProductListViewHolder) viewHolder).productCode.setText(model.getProductCode());
            if (model.getStockQty()!=null && !model.getStockQty().equals("null")){
                if (Double.parseDouble(model.getStockQty()) == 0 || Double.parseDouble(model.getStockQty()) < 0) {
                    ((ProductListViewHolder) viewHolder).stockQty.setText(model.getStockQty()+" ( No Stock )");
                }else {
                    ((ProductListViewHolder) viewHolder).stockQty.setText(model.getStockQty());
                }
            }else {
                ((ProductListViewHolder) viewHolder).stockQty.setText(model.getStockQty()+" ( No Stock )");
            }
            ((ProductListViewHolder) viewHolder).price.setText(String.valueOf("$ "+model.getRetailPrice()));
            if (model.getStockQty()!=null && !model.getStockQty().equals("null")) {
                if (Double.parseDouble(model.getStockQty()) == 0 || Double.parseDouble(model.getStockQty()) < 0) {
                    ((ProductListViewHolder) viewHolder).stockQty.setTextColor(Color.parseColor("#D24848"));
                } else if (Double.parseDouble(model.getStockQty()) > 0) {
                    ((ProductListViewHolder) viewHolder).stockQty.setTextColor(Color.parseColor("#2ECC71"));
                }
            }
            ((ProductListViewHolder) viewHolder).mainItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Double.parseDouble(model.getStockQty()) == 0 || Double.parseDouble(model.getStockQty()) < 0) {
                        if (mContext instanceof SalesReturnActivity){
                            callBack.searchProduct(model);
                        }else {
                            showAlert();
                        }
                    }else {
                        callBack.searchProduct(model);
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
        return productList == null ? 0 : productList.size();
    }

    public void setLoaded() {
        isLoading = false;
       // callBack.calculateNetTotal(salesOrderList);
    }

    public void showAlert(){
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Warning !")
                .setContentText("Product stock is Low!")
                .setConfirmText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }

    static class ProductListViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private TextView productCode;
        private LinearLayout selectProduct;
        private TextView stockQty;
        private TextView price;
        private CardView mainItem;
        public ProductListViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.item_name);
            productCode=view.findViewById(R.id.item_code);
            selectProduct=view.findViewById(R.id.product_view);
            stockQty=view.findViewById(R.id.stock);
            price=view.findViewById(R.id.price);
            mainItem=view.findViewById(R.id.cardlist_item);
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

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        Log.w("Given_date_printed:",date);
        return date;
    }

    public interface CallBack{
        void searchProduct(ProductsModel model);
    }

    public void filterList(ArrayList<ProductsModel> filterdNames) {
        this.productList = filterdNames;
        notifyDataSetChanged();
    }

    public ArrayList<ProductsModel> getNotalInvoiceList(){
        return productList;
    }

    public ArrayList<ProductsModel> getProductList(){
        return productFilterList;
    }
}