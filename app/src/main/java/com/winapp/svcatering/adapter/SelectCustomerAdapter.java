package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.AddInvoiceActivityOld;
import com.winapp.svcatering.activity.CustomerListActivity;
import com.winapp.svcatering.activity.DeliveryOrderListActivity;
import com.winapp.svcatering.activity.InvoiceListActivityCopy;
import com.winapp.svcatering.activity.NewInvoiceListActivity;
import com.winapp.svcatering.activity.OrderHistoryActivity;
import com.winapp.svcatering.activity.SalesOrderListActivity;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.salesreturn.SalesReturnActivity;
import com.winapp.svcatering.utils.SharedPreferenceUtil;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;

public class SelectCustomerAdapter extends RecyclerView.Adapter<SelectCustomerAdapter.ViewHolder> {
    private ArrayList<CustomerModel> customers;
    public CallBack callBack;
    public Context context;
    private SharedPreferenceUtil sharedPreferenceUtil;

    public  String activityName = "";

    public OnMoreButtonClicked onMoreButtonClicked;
    public SelectCustomerAdapter(Context context,ArrayList<CustomerModel> customers, CallBack callBack) {
        this.customers = customers;
        this.callBack=callBack;
        this.context=context;
    }

    public SelectCustomerAdapter(Context context,ArrayList<CustomerModel> customers, OnMoreButtonClicked onMoreButtonClicked) {
        this.customers = customers;
        this.callBack=callBack;
        this.context=context;
        this.onMoreButtonClicked=onMoreButtonClicked;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.select_customer_items, viewGroup, false);
        sharedPreferenceUtil =new SharedPreferenceUtil(context);

        activityName = sharedPreferenceUtil.getStringPreference(sharedPreferenceUtil.KEY_ACTIVITY,"");

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        CustomerModel model=customers.get(i);
        viewHolder.customerName.setText(model.getCustomerName());
        viewHolder.customerCode.setText(model.getCustomerCode());
        viewHolder.phoneNo.setText(model.getPhoneNo());
        viewHolder.outstandingAmount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getOutstandingAmount())));

        if (model.getAddress1()!=null && !model.getAddress1().isEmpty()){
            viewHolder.locationIcon.setVisibility(View.VISIBLE);
            viewHolder.customerAddress.setText(model.getAddress1());
            viewHolder.customerAddress.setVisibility(View.VISIBLE);
        }else {
            viewHolder.locationIcon.setVisibility(View.VISIBLE);
            viewHolder.customerAddress.setText("No Address");
            viewHolder.customerAddress.setVisibility(View.VISIBLE);
        }

        if (context instanceof CustomerListActivity){
            viewHolder.moreIcon.setVisibility(View.VISIBLE);
        }else {
            viewHolder.moreIcon.setVisibility(View.GONE);
        }
        if (context instanceof SalesOrderListActivity
                || context instanceof InvoiceListActivityCopy
                || context instanceof NewInvoiceListActivity
                || context instanceof DeliveryOrderListActivity
                || context instanceof SalesReturnActivity
                || context instanceof AddInvoiceActivityOld) {
            viewHolder.customerCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    callBack.searchCustomer(model.getCustomerCode(), model.getCustomerName(),i);
                }
            });
        }
        if (context instanceof SalesOrderListActivity
                || context instanceof InvoiceListActivityCopy
                || context instanceof NewInvoiceListActivity
                || context instanceof DeliveryOrderListActivity
                || context instanceof AddInvoiceActivityOld
                || context instanceof SalesReturnActivity){
            viewHolder.customerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    callBack.searchCustomer(model.getCustomerCode(),model.getCustomerName(),i);
                }
            });
        }

        if (context instanceof CustomerListActivity){
            viewHolder.moreIcon.setVisibility(View.GONE);
            viewHolder.phoneNoLay.setVisibility(View.VISIBLE);
            viewHolder.customerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  onMoreButtonClicked.moreOption(model.getCustomerName(),model.getCustomerCode(),model.getOutstandingAmount());

                    onMoreButtonClicked.createInvoice(model.getCustomerCode(),model.getCustomerName(),model.getTaxCode(),
                            model.getTaxPerc(),model.getTaxType() , model.getBillDiscPercentage() ,
                            model.getAllowFOC(),model.getMailId());

                }
            });
        }
        Log.w("custContex",""+activityName);
     if (activityName.equals("SalesOrder") || activityName.equals("Invoice")){
       // if ( activityName.equals("Invoice")){
            viewHolder.three_dot_custl.setVisibility(View.GONE);
            if (activityName.equals("SalesOrder")){
                activityName = "SalesOrder" ;
            }
            else{
                activityName = "Invoice" ;
            }
        }
        else{
            viewHolder.three_dot_custl.setVisibility(View.GONE);

        }


        viewHolder.moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMoreButtonClicked.moreOption(model.getCustomerName(),model.getCustomerCode(),
                        model.getOutstandingAmount(),model.getAllowFOC());
            }
        });
        viewHolder.three_dot_custl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view,i,model);

            }
        });


        if (i % 2==0){
            viewHolder.customerLayout.setBackgroundColor(Color.parseColor("#F8F9F9"));
        }else {
            viewHolder.customerLayout.setBackgroundColor(Color.parseColor("#EAEDED"));
        }
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView customerName;
        private TextView customerAddress;
        private TextView customerCode , phoneNo;
        private CardView customerCard;
        private LinearLayout customerLayout , phoneNoLay;
        private ImageView moreIcon;
        private ImageView locationIcon;
        private TextView outstandingAmount;
        private ImageView three_dot_custl;

        public ViewHolder(View view) {
            super(view);
            customerName = view.findViewById(R.id.name);
            customerCode=view.findViewById(R.id.customer_code);
            customerAddress=view.findViewById(R.id.customer_address);
            customerCard=view.findViewById(R.id.cardlist_item);
            customerLayout=view.findViewById(R.id.root_layout);
            moreIcon=view.findViewById(R.id.more_icon);
            locationIcon=view.findViewById(R.id.location_icon);
            outstandingAmount=view.findViewById(R.id.outstanding_amount);
            phoneNoLay=view.findViewById(R.id.phoneNoLayCust);
            phoneNo=view.findViewById(R.id.cust_phoneNo);
            three_dot_custl=view.findViewById(R.id.three_dot_cust);
        }
    }

    public interface CallBack{
        void searchCustomer(String letter,String customerName, int pos);
    }

    public interface OnMoreButtonClicked{
        void moreOption(String customename,String id,String outstanding ,String isFoc);
        void createInvoice(String customerCode,String customerName,String taxcode,String taxperc,String taxtype,
                           String BillDisc,String isFOC , String mailId);
    }
    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, int position,CustomerModel model) {
        PopupMenu popup = new PopupMenu(context, view, Gravity.END);
        MenuInflater inflater = popup.getMenuInflater();

        inflater.inflate(R.menu.three_dot_menu, popup.getMenu());

        //set menu item click listener here
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int position;

        /**
         * @param position
         */
        MyMenuItemClickListener(int position) {

            this.position = position;
        }

        /**
         * Click listener for popup menu items
         */

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.order_history_cust:

                    Intent intent=new Intent(context, OrderHistoryActivity.class);
                    intent.putExtra("custNameHistory",customers.get(position).getCustomerName());
                    intent.putExtra("custCodeHistory",customers.get(position).getCustomerCode());
                    intent.putExtra("activityHistory", activityName);

                    context.startActivity(intent);
                    Log.w("ordrHistorMenu",""+activityName);
                    return true;
//                case R.id.edit:
//                    // ...
//                    return true;
//                case R.id.delete:
//                    // ...
//                    return true;
//                case R.id.favourite:
//                    // ...
//                    return true;
                default:
            }
            return false;
        }
    }
    public void filterList(ArrayList<CustomerModel> filterdNames) {
        this.customers = filterdNames;
        notifyDataSetChanged();
    }

}