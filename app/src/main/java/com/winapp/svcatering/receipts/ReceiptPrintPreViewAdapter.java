package com.winapp.svcatering.receipts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;

import java.util.ArrayList;

public class ReceiptPrintPreViewAdapter extends RecyclerView.Adapter<ReceiptPrintPreViewAdapter.ViewHolder> {

    private ArrayList<ReceiptPrintPreviewModel.ReceiptsDetails> receiptsList;
    private Context context;
    public ReceiptPrintPreViewAdapter(Context context,ArrayList<ReceiptPrintPreviewModel.ReceiptsDetails> receipts) {
        this.context=context;
        this.receiptsList = receipts;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.receipts_print_preview_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ReceiptPrintPreviewModel.ReceiptsDetails receiptList = receiptsList.get(position);
        viewHolder.slNo.setText(String.valueOf(position+1));
        viewHolder.invoiceNo.setText(receiptList.getInvoiceNumber());
        viewHolder.invoiceDate.setText(receiptList.getInvoiceDate());
        viewHolder.amount.setText(receiptList.getAmount());
        viewHolder.discount.setText(receiptList.getDiscountAmount());

    }

    @Override
    public int getItemCount() {
        return receiptsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView slNo;
        private TextView invoiceNo;
        private TextView invoiceDate;
        private TextView amount;
        private TextView discount;
        public ViewHolder(View view) {
            super(view);
            slNo=view.findViewById(R.id.sl_no);
            invoiceNo=view.findViewById(R.id.invoice_no);
            invoiceDate=view.findViewById(R.id.invoice_date);
            amount=view.findViewById(R.id.amount);
            discount=view.findViewById(R.id.disc_receipt);

        }
    }

}
