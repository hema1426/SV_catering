package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.CustomerScheduleModel;
import com.winapp.svcatering.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * This Adapter class used to display the All profile images and names
 */

public class SchedulingCustomerAdapter extends RecyclerView.Adapter<SchedulingCustomerAdapter.ProfileViewHolder> {
    /**
     * Declare the Context and Arraylist variables
     */
    private Context mContext;
    private ArrayList<CustomerScheduleModel> scheduleList;
    public  CallBack callBack;
    private SessionManager session;
    String customerId;

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        // Declare the Required Variables
        private TextView name;
        private TextView product;
        private TextView time;
        private TextView amount;
        private TextView status;
        private LinearLayout rootLayout;
        public ProfileViewHolder(View view) {
            super(view);
            name=view.findViewById(R.id.name);
            product=view.findViewById(R.id.product);
            time=view.findViewById(R.id.time);
            amount=view.findViewById(R.id.payment);
            status=view.findViewById(R.id.status);
            rootLayout=view.findViewById(R.id.rootLayout);
        }
    }

    /**
     * Constructor for the send the arraylist and context
     * @param mContext
     * @param
     *
     */

    public SchedulingCustomerAdapter(Context mContext, ArrayList<CustomerScheduleModel> scheduleList, CallBack callBack){
        this.mContext=mContext;
        this.scheduleList = scheduleList;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customers_items, parent, false);
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
            CustomerScheduleModel model= scheduleList.get(position);
            session=new SessionManager(mContext);
            HashMap<String ,String > user=session.getUserDetails();
            customerId=user.get(SessionManager.KEY_CUSTOMER_ID);
            holder.name.setText(model.getCustomerName());
            holder.time.setText(model.getDateTime());
            holder.product.setText(model.getProducts());
            holder.amount.setText(model.getPaymentAmount());
            holder.status.setText(model.getStatus());

            if (model.getStatus().equals("Open")){
                holder.status.setTextColor(Color.parseColor("#2ECC71"));
            }else if (model.getStatus().equals("Closed")){
                holder.status.setTextColor(Color.parseColor("#5DADE2"));
            }else if (model.getStatus().equals("Cancel")){
                holder.status.setTextColor(Color.parseColor("#EC7063"));
            }
            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.getCustomerDetails(model.getCustomerCode());
                }
            });

            holder.status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.getCustomerDetails(model.getCustomerCode());
                }
            });

        }catch (Exception ex){
            Log.e("Error_in_adapter:", Objects.requireNonNull(ex.getMessage()));
        }
    }

    /**
     * @return array list count
     */

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public interface CallBack {
        void getCustomerDetails(String customerCode);
    }

    public void filterList(ArrayList<CustomerScheduleModel> filterdNames) {
        this.scheduleList = filterdNames;
        notifyDataSetChanged();
    }
}
