package com.winapp.svcatering.ReportPreview.adapter;


import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.winapp.svcatering.R;
import com.winapp.svcatering.model.SettlementReceiptModel;

import java.util.ArrayList;


public class RoSettleExpensPreviewAdapter extends RecyclerView.Adapter<RoSettleExpensPreviewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<SettlementReceiptModel.Expense> dataList;

    public RoSettleExpensPreviewAdapter(Context context, ArrayList<SettlementReceiptModel.Expense> expenseArrayList) {
        this.context = context;
        this.dataList = expenseArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.ro_setle_expense_preview_item, parent, false));
    }

    public ArrayList<SettlementReceiptModel.Expense> getGRAlist() {
        return dataList;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView expens_name;
        public TextView expens_total;
        public TextView expens_sno;


        MyViewHolder(View itemView) {
            super(itemView);

            this.expens_name = (TextView) itemView.findViewById(R.id.item_setle_expens_name);
            this.expens_total = (TextView) itemView.findViewById(R.id.item_setle_expens_total);
            this.expens_sno = (TextView) itemView.findViewById(R.id.item_setle_expens_sno);
        }

        public void updateList(ArrayList<SettlementReceiptModel.Expense> list) {
            dataList = list;
            notifyDataSetChanged();
        }

        @SuppressLint("ClickableViewAccessibility")
        public void setData(SettlementReceiptModel.Expense expenseItem) {
            int pos = getAdapterPosition();
            expens_sno.setText(String.valueOf(pos+1));

            expens_name.setText(expenseItem.getExpeneName());
            expens_total.setText(twoDecimalPoint(Double.parseDouble(expenseItem.getExpenseTotal())));
        }

    }

}