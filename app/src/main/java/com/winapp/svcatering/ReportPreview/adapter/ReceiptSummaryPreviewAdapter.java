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
import com.winapp.svcatering.model.ReceiptSummaryModel;

import java.util.ArrayList;


public class ReceiptSummaryPreviewAdapter extends RecyclerView.Adapter<ReceiptSummaryPreviewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<ReceiptSummaryModel.ReceiptDetails> dataList;

    public ReceiptSummaryPreviewAdapter(Context context, ArrayList<ReceiptSummaryModel.ReceiptDetails> receiptSummaryList) {
        this.context = context;
        this.dataList = receiptSummaryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(
                R.layout.receipt_summary_preview_item, parent, false));
    }

    public ArrayList<ReceiptSummaryModel.ReceiptDetails> getGRAlist() {
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
        public TextView recpt_custname;
        public TextView recpt_total;
        public TextView recpt_no;
        public TextView paymodetxt;
        public TextView sno_recpt;


        MyViewHolder(View itemView) {
            super(itemView);

            this.recpt_custname = (TextView) itemView.findViewById(R.id.itemrecptsum_name);
            this.recpt_no = (TextView) itemView.findViewById(R.id.item_recptnosum);
            this.recpt_total = (TextView) itemView.findViewById(R.id.itemtotal_recptsum);
            this.paymodetxt = (TextView) itemView.findViewById(R.id.itemmode_recptsum);
            this.sno_recpt = (TextView) itemView.findViewById(R.id.item_snorecptsum);
        }

        public void updateList(ArrayList<ReceiptSummaryModel.ReceiptDetails> list) {
            dataList = list;
            notifyDataSetChanged();
        }

        @SuppressLint("ClickableViewAccessibility")
        public void setData(ReceiptSummaryModel.ReceiptDetails receiptItem) {
            int pos = getAdapterPosition();
            sno_recpt.setText(String.valueOf(pos+1));

            recpt_custname.setText(receiptItem.getCustomerName());
            recpt_no.setText(receiptItem.getReceiptNo());
            recpt_total.setText(twoDecimalPoint(Double.parseDouble(receiptItem.getPaidAmount())));
            paymodetxt.setText(receiptItem.getPaymode());
        }

    }

}