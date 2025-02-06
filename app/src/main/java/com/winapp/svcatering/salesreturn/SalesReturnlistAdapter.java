package com.winapp.svcatering.salesreturn;


import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.SalesReturnModel;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SalesReturnlistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private ArrayList<SalesReturnModel> salesReturnList;
    private Context mContext;
    private CallBack callBack;
    public static ArrayList<SalesReturnModel> salesReturnFilterList;

    public SalesReturnlistAdapter(Context context, RecyclerView mRecyclerView, ArrayList<SalesReturnModel> srList, CallBack callBack) {

        this.salesReturnList = srList;
        salesReturnFilterList = srList;
        this.mContext=context;
        this.callBack=callBack;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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
        return salesReturnList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.sales_return_list_items, parent, false);
            return new SalesReturnViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder  viewHolder, int position) {
        try {
            if ( viewHolder instanceof SalesReturnViewHolder) {

                SalesReturnModel srModel = salesReturnList.get(position);

                if (srModel.getCustomerName()!=null && !srModel.getCustomerName().equals("null")){
                    ((SalesReturnViewHolder) viewHolder).name.setText(srModel.getCustomerName());
                }else {
                    ((SalesReturnViewHolder) viewHolder).name.setText("");
                }
                if (srModel.getSalesReturnDate().equals("null")){
                    ((SalesReturnViewHolder) viewHolder).date.setText("");
                }else {
                    ((SalesReturnViewHolder) viewHolder).date.setText(srModel.getSalesReturnDate());
                }
                if (srModel.getSalesReturnNumber().equals("null")){
                    ((SalesReturnViewHolder) viewHolder).srNo.setText("");
                }else {
                    ((SalesReturnViewHolder) viewHolder).srNo.setText(srModel.getSalesReturnNumber());
                }

                ((SalesReturnViewHolder) viewHolder).netTotal.setText("$ "+ Utils.twoDecimalPoint(Double.parseDouble(srModel.getNetAmount())));
                ((SalesReturnViewHolder) viewHolder).balanceAmount.setText("$ "+Utils.twoDecimalPoint(Double.parseDouble(srModel.getBalanceAmount())));

                ((SalesReturnViewHolder) viewHolder).moreOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         callBack.showMoreOption(srModel.getSalesReturnCode(), srModel.getCustomerName());
                    }
                });

                ((SalesReturnViewHolder)viewHolder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        callBack.showMoreOption(srModel.getSalesReturnCode(), srModel.getCustomerName());
                        return false;
                    }
                });

               /* if (position % 2 ==1){
                    ((SalesReturnViewHolder) viewHolder).rootLayout.setBackgroundColor(Color.parseColor("#F7F9F9"));
                }else {
                    ((SalesReturnViewHolder) viewHolder).rootLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                }*/

            } else if ( viewHolder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder)  viewHolder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }catch (Exception ex){

        }
    }

    @Override
    public int getItemCount() {
        return salesReturnList == null ? 0 : salesReturnList.size();
    }

    public void setLoaded() {
        isLoading = false;
        callBack.calculateNetTotal(salesReturnList);
    }

    static class SalesReturnViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView date;
        private TextView srNo;
        private TextView netTotal;
        private TextView balanceAmount;
        private ImageView moreOption;
        private LinearLayout rootLayout;

        public SalesReturnViewHolder(View view) {
            super(view);
            name=view.findViewById(R.id.name);
            date=view.findViewById(R.id.date);
            netTotal=view.findViewById(R.id.net_total);
            srNo=view.findViewById(R.id.sr_no);
            balanceAmount=view.findViewById(R.id.balance_amount);
            netTotal=view.findViewById(R.id.net_total);
            moreOption=view.findViewById(R.id.more_option);
            rootLayout=view.findViewById(R.id.rootLayout);
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

    public void filterList(ArrayList<SalesReturnModel> filterdNames) {
        salesReturnList = filterdNames;
        notifyDataSetChanged();
    }

    public static ArrayList<SalesReturnModel> getSalesReturnList(){
        return salesReturnFilterList;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        Log.w("Given_date_printed:",date);
        return date;
    }

    public interface CallBack {
        void calculateNetTotal(ArrayList<SalesReturnModel> salesList);
        void showMoreOption(String receiptNo,String customerName);
    }

}