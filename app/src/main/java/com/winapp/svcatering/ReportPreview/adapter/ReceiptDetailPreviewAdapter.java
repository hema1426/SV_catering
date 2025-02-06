package com.winapp.svcatering.ReportPreview.adapter;


import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.ReceiptDetailsModel;

import java.util.ArrayList;


public class ReceiptDetailPreviewAdapter extends RecyclerView.Adapter<ReceiptDetailPreviewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<ReceiptDetailsModel.ReceiptDetails> dataList;

    public ReceiptDetailPreviewAdapter(Context context, ArrayList<ReceiptDetailsModel.ReceiptDetails> receiptDetailsList) {
        this.context = context;
        this.dataList = receiptDetailsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.receipt_detail_preview_item, parent, false));
    }

    public ArrayList<ReceiptDetailsModel.ReceiptDetails> getGRAlist() {
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
        public TextView paidtxt;
        public LinearLayout banklay;
        public ImageView menu_img;
        public TextWatcher textWatcher;
        boolean isshown;
        public TextView bankcode;
        public TextView chequeno;
        public TextView chequedate;

        MyViewHolder(View itemView) {
            super(itemView);

            this.recpt_custname = (TextView) itemView.findViewById(R.id.itemrecpt_name);
            this.recpt_no = (TextView) itemView.findViewById(R.id.item_recptno);
            this.recpt_total = (TextView) itemView.findViewById(R.id.itemtotal_recpt);
            this.paymodetxt = (TextView) itemView.findViewById(R.id.item_paymode);
            this.sno_recpt = (TextView) itemView.findViewById(R.id.item_snorecpt);
            this.paidtxt = (TextView) itemView.findViewById(R.id.paidamt_itemrecpt);
            this.banklay = (LinearLayout) itemView.findViewById(R.id.bank_lay);
            this.bankcode = (TextView) itemView.findViewById(R.id.bankcode_recpt);
            this.chequeno = (TextView) itemView.findViewById(R.id.cheque_no_recpt);
            this.chequedate = (TextView) itemView.findViewById(R.id.cheque_date_recpt);

        }


        public void updateList(ArrayList<ReceiptDetailsModel.ReceiptDetails> list) {
            dataList = list;
            notifyDataSetChanged();
        }

        @SuppressLint("ClickableViewAccessibility")
        public void setData(ReceiptDetailsModel.ReceiptDetails receiptItem) {
            int pos = getAdapterPosition();
            sno_recpt.setText(String.valueOf(pos+1));

            recpt_custname.setText(receiptItem.getCustomerName());
            recpt_no.setText(receiptItem.getReceiptNumber());
            recpt_total.setText(twoDecimalPoint(Double.parseDouble(receiptItem.getReceiptTotal())));
            paymodetxt.setText(receiptItem.getPaymode());
            paidtxt.setText(twoDecimalPoint(Double.parseDouble(receiptItem.getReceiptTotal())));

            if(receiptItem.getPaymode().equalsIgnoreCase("Cheque")){
                banklay.setVisibility(View.VISIBLE);
                bankcode.setText(receiptItem.getBankCode());
                chequeno.setText(receiptItem.getChequeNo());
                chequedate.setText(receiptItem.getChequeDate());
            }
            else{
                banklay.setVisibility(View.GONE);
            }

        }

    }

}