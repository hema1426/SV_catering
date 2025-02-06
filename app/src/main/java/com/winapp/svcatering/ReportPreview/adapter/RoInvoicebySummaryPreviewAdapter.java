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
import com.winapp.svcatering.model.InvoiceSummaryModel;

import java.util.ArrayList;


public class RoInvoicebySummaryPreviewAdapter extends RecyclerView.Adapter<RoInvoicebySummaryPreviewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<InvoiceSummaryModel.SummaryDetails> dataList;

    public RoInvoicebySummaryPreviewAdapter(Context context, ArrayList<InvoiceSummaryModel.SummaryDetails> invoiceSummarylist) {
        this.context = context;
        this.dataList = invoiceSummarylist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.ro_invbysummary_preview_item, parent, false));
    }

    public ArrayList<InvoiceSummaryModel.SummaryDetails> getGRAlist() {
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
        public TextView invoiceno;
        public TextView sno;
        public TextView paid;
        public TextView balance;
        public TextView nettotal;


        MyViewHolder(View itemView) {
            super(itemView);

            this.invoiceno = (TextView) itemView.findViewById(R.id.item_invno_invsum);
            this.balance = (TextView) itemView.findViewById(R.id.item_balance_invsum);
            this.nettotal = (TextView) itemView.findViewById(R.id.item_nettotal_invsum);
            this.paid = (TextView) itemView.findViewById(R.id.item_paid_invsum);
            this.sno = (TextView) itemView.findViewById(R.id.item_sno_invsum);
        }

        public void updateList(ArrayList<InvoiceSummaryModel.SummaryDetails> list) {
            dataList = list;
            notifyDataSetChanged();
        }

        @SuppressLint("ClickableViewAccessibility")
        public void setData(InvoiceSummaryModel.SummaryDetails InvoiceSumItem) {
            int pos = getAdapterPosition();
            sno.setText(String.valueOf(pos+1));

            paid.setText(twoDecimalPoint(Double.parseDouble(InvoiceSumItem.getPaidAmount())));
            balance.setText(twoDecimalPoint(Double.parseDouble(InvoiceSumItem.getBalanceAmount())));
            nettotal.setText(twoDecimalPoint(Double.parseDouble(InvoiceSumItem.getNetTotal())));
            invoiceno.setText(InvoiceSumItem.getInvoiceNumber());
        }

    }

}