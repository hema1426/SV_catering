package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.ScheduleModel;
import com.winapp.svcatering.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * This Adapter class used to display the All profile images and names
 */

public class ShedulingAdapter extends RecyclerView.Adapter<ShedulingAdapter.ProfileViewHolder> {
    /**
     * Declare the Context and Arraylist variables
     */
    private Context mContext;
    private ArrayList<ScheduleModel> scheduleList;
    public  CallBack callBack;
    private SessionManager session;
    String customerId;

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        // Declare the Required Variables
        private TextView day;
        private TextView date;
        private TextView year;
        private TextView time;
        private TextView name;
        private Button status;
        private LinearLayout scheduleView;
        private TextView address;
        private TextView sortOrder;
        private Button selectButton;
        public ProfileViewHolder(View view) {
            super(view);
            day=view.findViewById(R.id.day);
            date=view.findViewById(R.id.date);
            year=view.findViewById(R.id.year);
            time=view.findViewById(R.id.time);
            name=view.findViewById(R.id.name);
            status=view.findViewById(R.id.status);
            address=view.findViewById(R.id.address1);
            scheduleView=view.findViewById(R.id.root_layout);
            sortOrder=view.findViewById(R.id.sort_order);
            selectButton=view.findViewById(R.id.select);
        }
    }

    /**
     * Constructor for the send the arraylist and context
     * @param mContext
     * @param
     *
     */

    public ShedulingAdapter(Context mContext, ArrayList<ScheduleModel> scheduleList, CallBack callBack){
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.scheduling_items_new, parent, false);
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
            ScheduleModel model= scheduleList.get(position);
            session=new SessionManager(mContext);
            HashMap<String ,String > user=session.getUserDetails();
            customerId=user.get(SessionManager.KEY_CUSTOMER_ID);
            holder.day.setText(model.getDay());
            holder.date.setText(model.getDate());;
            holder.year.setText(model.getYear());;
            holder.name.setText(model.getName());
            holder.status.setText(model.getStatus());
            holder.address.setText(model.getAddress());
            holder.sortOrder.setText("SortOrder : "+model.getSortOrder());

            holder.scheduleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.selectCustomer(model.getCustomerCode());
                }
            });

            holder.selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.selectCustomer(model.getCustomerCode());
                }
            });

            if (model.getStatus().equals("Open")){
                holder.status.setBackgroundResource(R.drawable.schedule_open);
            }else if (model.getStatus().equals("Closed")){
                holder.status.setBackgroundResource(R.drawable.schedule_closed);
            }

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
        void selectCustomer(String customer);
    }

    public void filterList(ArrayList<ScheduleModel> filterdNames) {
        this.scheduleList = filterdNames;
        notifyDataSetChanged();
    }
}