package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.OrderDetailsHistoryActivity;
import com.winapp.svcatering.model.OrderHistoryModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    public static ArrayList<OrderHistoryModel> orderList;
    public static ArrayList<OrderHistoryModel> orderFilterList;
    @SuppressLint("StaticFieldLeak")
    public static Context mContext;
    public static CallBack callBack;
    public static String activityl;

    public OrderHistoryAdapter(Context context, RecyclerView mRecyclerView,String activity, ArrayList<OrderHistoryModel> orderList, CallBack callBack) {

        OrderHistoryAdapter.orderList = orderList;
        orderFilterList = orderList;
        activityl =activity ;
        mContext=context;
        OrderHistoryAdapter.callBack =callBack;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                assert linearLayoutManager != null;
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
        return orderList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.order_history_items, parent, false);
            return new SalesOrderViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder  viewHolder, int position) {
        if ( viewHolder instanceof SalesOrderViewHolder) {
            OrderHistoryModel model=orderList.get(position);
            ((SalesOrderViewHolder) viewHolder).orderDate.setText(model.getOrderDate());
           // ((SalesOrderViewHolder) viewHolder).orderTime.setText(model.getOrderTime());
            ((SalesOrderViewHolder) viewHolder).customerName.setText(model.getCustomerName());
            //((SalesOrderViewHolder) viewHolder).orderStatus.setText(model.getOrderStatus());
            ((SalesOrderViewHolder) viewHolder).orderId.setText(model.getOrderNumber());
            ((SalesOrderViewHolder) viewHolder).paidAmount.setText(model.getPaidAmount());
          //  ((SalesOrderViewHolder) viewHolder).dueDays.setText(model.getDueDelayDays());
            ((SalesOrderViewHolder) viewHolder).dueAmount.setText(model.getDueAmount());

            ((SalesOrderViewHolder) viewHolder).rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Onclick of the Load the Products details
                    Intent intent=new Intent(mContext, OrderDetailsHistoryActivity.class);
                    intent.putExtra("activityFrom", activityl);
                    intent.putExtra("customerCodeHis",model.getCustomerCode());
                    intent.putExtra("orderNumberHis",model.getOrderId());
                    intent.putExtra("orderDateHis",model.getOrderDate());
                    intent.putExtra("customerNameHis",model.getCustomerName());
                    intent.putExtra("paidAmountHis",model.getPaidAmount());
                    intent.putExtra("dueAmountHis",model.getDueAmount());
                   // intent.putExtra("orderStatusHis",model.getOrderStatus());
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();
                }
            });

//            if (model.getOrderStatus().equals("Open")){
//                ((SalesOrderViewHolder) viewHolder).orderStatus.setTextColor(Color.parseColor("#229954"));
//            }else {
//                ((SalesOrderViewHolder) viewHolder).orderStatus.setTextColor(Color.parseColor("#5DADE2"));
//            }

            if (position % 2==1){
                ((SalesOrderViewHolder) viewHolder).rootLayout.setBackgroundColor(Color.parseColor("#f3f3f3"));
            }

        } else if ( viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)  viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    public void setLoaded() {
        isLoading = false;
        callBack.calculateNetTotal(orderList);
    }

    static class SalesOrderViewHolder extends RecyclerView.ViewHolder {
        private TextView orderDate;
      //  private TextView orderTime;
        private TextView customerName;
        private TextView orderId;
      //  private TextView orderStatus;
        private TextView paidAmount;
        private TextView dueAmount;
       // private TextView dueDays;
      //  private TextView btnShowDetails;
        private LinearLayout rootLayout;
        public SalesOrderViewHolder(View view) {
            super(view);
            orderDate=view.findViewById(R.id.order_dateHis);
           // orderTime=view.findViewById(R.id.order_time);
            customerName=view.findViewById(R.id.customer_nameHis);
            orderId=view.findViewById(R.id.order_idHis);
          //  orderStatus=view.findViewById(R.id.order_status);
            paidAmount=view.findViewById(R.id.paid_amountHis);
            dueAmount=view.findViewById(R.id.due_amountHis);;
           // dueDays=view.findViewById(R.id.due_days);
           // btnShowDetails=view.findViewById(R.id.reOrderTxt);
            rootLayout=view.findViewById(R.id.orderHistoryLay);
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
        void calculateNetTotal(ArrayList<OrderHistoryModel> salesList);
        void showMoreOption(String salesorderId,String customerName,String status);
    }

    public void filterList(ArrayList<OrderHistoryModel> filterdNames) {
        orderList = filterdNames;
        notifyDataSetChanged();
    }

    public static ArrayList<OrderHistoryModel> getNotalInvoiceList(){
        return orderList;
    }

    public static ArrayList<OrderHistoryModel> getOrderList(){
        return orderFilterList;
    }

}