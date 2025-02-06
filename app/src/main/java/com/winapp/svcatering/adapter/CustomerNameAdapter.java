package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONException;

import java.util.ArrayList;

public class CustomerNameAdapter extends RecyclerView.Adapter<CustomerNameAdapter.ViewHolder> {

    private ArrayList<CustomerModel> customers;
    public CallBack callBack;

    public CustomerNameAdapter(ArrayList<CustomerModel> countries,CallBack callBack) {
        this.customers = countries;
        this.callBack=callBack;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cutomer_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {

        CustomerModel model=customers.get(i);
        viewHolder.customerName.setText(model.getCustomerName().trim());
       // viewHolder.customerAddress.setText(model.getAddress1());
        viewHolder.customerCode.setText(model.getCustomerCode());

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

        viewHolder.customerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    callBack.sortProduct(model.getCustomerCode(),model.getCustomerName(),i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        viewHolder.customerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    callBack.sortProduct(model.getCustomerCode(),model.getCustomerName(),i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView customerName;
        private TextView customerAddress;
        private TextView customerCode;
        private CardView customerCard;
        private LinearLayout customerLayout;
        private ImageView moreIcon;
        private ImageView locationIcon;
        private TextView outstandingAmount;
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
        }
    }

    public interface CallBack{
        void sortProduct(String letter,String customerName,int pos) throws JSONException;
    }

    public void filterList(ArrayList<CustomerModel> filterdNames) {
        this.customers = filterdNames;
        notifyDataSetChanged();
    }

}