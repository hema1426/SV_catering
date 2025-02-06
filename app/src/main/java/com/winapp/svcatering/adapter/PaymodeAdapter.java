package com.winapp.svcatering.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.PaymentTypeModel;

import java.util.ArrayList;

public class PaymodeAdapter extends RecyclerView.Adapter<PaymodeAdapter.ViewHolder> {
    private ArrayList<PaymentTypeModel> paymodes;
    public CallBack callBack;
    private int selectedPosition = -1;// no selection by default

    public PaymodeAdapter(ArrayList<PaymentTypeModel> paymodes, CallBack callBack) {
        this.paymodes = paymodes;
        this.callBack=callBack;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.paymode_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.paymodeCheck.setText(paymodes.get(position).getPaymentTypeName());

        viewHolder.paymodeCheck.setOnClickListener(view -> {
            selectedPosition = viewHolder.getAdapterPosition();
            notifyDataSetChanged();
        });
        if (selectedPosition==position){
            viewHolder.paymodeCheck.setChecked(true);
            callBack.selectPayment(paymodes.get(position).getPaymentTypeCode(),paymodes.get(position).getPaymentTypeName());
        } else {
            viewHolder.paymodeCheck.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return paymodes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CheckBox paymodeCheck;
        private LinearLayout selectLayout;
        public ViewHolder(View view) {
            super(view);
            paymodeCheck = view.findViewById(R.id.pay_mode);
            selectLayout=view.findViewById(R.id.select_layout);
        }
    }

    public interface CallBack{
        void selectPayment(String paymentCode,String paymentName);
    }

    public void resetPosition(){
        selectedPosition=-1;
        notifyDataSetChanged();
    }
}