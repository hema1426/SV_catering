package com.winapp.svcatering.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.CreditLimitDialogResponse;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;

public class CreditLimitDialogAdapter extends RecyclerView.Adapter<CreditLimitDialogAdapter.ViewHolder> {

    private ArrayList<CreditLimitDialogResponse> dataList;
    private Context context;
    View view;
    private String printView;
    public CreditLimitDialogAdapter(Context context, ArrayList<CreditLimitDialogResponse> dataList) {
        this.context=context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.credit_limit_dialog_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        CreditLimitDialogResponse list=dataList.get(position);

        viewHolder.slNo.setText(String.valueOf(position+1));
        viewHolder.custNameStr.setText(list.getCustomerName());
        viewHolder.amountStr.setText(Utils.twoDecimalPoint(Double.parseDouble(list.getCreditLine())));
        viewHolder.balStr.setText(Utils.twoDecimalPoint(Double.parseDouble(list.getBalance())));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView slNo;
        private TextView custNameStr;
        private TextView amountStr;
        private TextView balStr;
        private TextView price;
        private TextView total;
        public ViewHolder(View view) {
            super(view);
            slNo=view.findViewById(R.id.credit_itemSno);
            custNameStr =view.findViewById(R.id.credit_cust_name_item);
            amountStr=view.findViewById(R.id.credit_amt_item);
            balStr=view.findViewById(R.id.bal_amt_item);

        }
    }

}