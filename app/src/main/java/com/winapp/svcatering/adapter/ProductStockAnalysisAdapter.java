package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.ProductStockAnalysisModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ProductStockAnalysisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    public static ArrayList<ProductStockAnalysisModel> salesOrderList;
    public static ArrayList<ProductStockAnalysisModel> salesOrderFilterList;
    Context mContext;
    CallBack callBack;

    public ProductStockAnalysisAdapter(Context context, RecyclerView mRecyclerView, ArrayList<ProductStockAnalysisModel> salesOrderList,CallBack callBack) {

        this.salesOrderList = salesOrderList;
        this.salesOrderFilterList=salesOrderList;
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
        return salesOrderList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.product_stock_analysis_item, parent, false);
            return new SalesOrderViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder  viewHolder, int position) {
        if ( viewHolder instanceof SalesOrderViewHolder) {
            ProductStockAnalysisModel salesOrderModel = salesOrderList.get(position);
            ((SalesOrderViewHolder) viewHolder).pname.setText(salesOrderModel.getProductName());
            ((SalesOrderViewHolder) viewHolder).purchaseQty.setText(salesOrderModel.getPurchaseQty());
            ((SalesOrderViewHolder) viewHolder).salesQty.setText(salesOrderModel.getSalesQty());
            ((SalesOrderViewHolder) viewHolder).balanceQty.setText(salesOrderModel.getBalanceQty());

            ((SalesOrderViewHolder) viewHolder).moreOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.showMoreOption();
                }
            });

        } else if ( viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)  viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return salesOrderList == null ? 0 : salesOrderList.size();
    }

    public void setLoaded() {
        isLoading = false;
        callBack.calculateNetTotal(salesOrderList);
    }

    static class SalesOrderViewHolder extends RecyclerView.ViewHolder {
        private TextView pname;
        private TextView purchaseQty;
        private TextView salesQty;
        private TextView balanceQty;
        private ImageView moreOption;
        private View indicator;
        public SalesOrderViewHolder(View view) {
            super(view);
            pname=view.findViewById(R.id.pname);
            purchaseQty=view.findViewById(R.id.purchase_qty);
            salesQty=view.findViewById(R.id.sales_qty);
            balanceQty=view.findViewById(R.id.balance_qty);
            moreOption=view.findViewById(R.id.view_details);
            indicator=view.findViewById(R.id.indicator);
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

    public interface CallBack {
        void calculateNetTotal(ArrayList<ProductStockAnalysisModel> salesList);
        void showMoreOption();
    }

    public void filterList(ArrayList<ProductStockAnalysisModel> filterdNames) {
        salesOrderList = filterdNames;
        notifyDataSetChanged();
    }

    public static ArrayList<ProductStockAnalysisModel> getNotalInvoiceList(){
        return salesOrderList;
    }

    public static ArrayList<ProductStockAnalysisModel> getSalesOrderList(){
        return salesOrderFilterList;
    }

}