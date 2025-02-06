package com.winapp.svcatering.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.ProductAnalyzeModel;

import java.util.ArrayList;

public class ProductAnalysisAdapter extends RecyclerView.Adapter {
    ArrayList<ProductAnalyzeModel.ProductDetails> detailsList;
    Context context;

    public ProductAnalysisAdapter(Context context,ArrayList<ProductAnalyzeModel.ProductDetails> detailsList) {
        this.detailsList = detailsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.product_analyse_details_items, parent, false);
        return new RowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RowViewHolder rowViewHolder = (RowViewHolder) holder;
        int rowPos = rowViewHolder.getAdapterPosition();
        try {
            ProductAnalyzeModel.ProductDetails model= detailsList.get(position);
            rowViewHolder.slNo.setText(String.valueOf(position+1));
            rowViewHolder.invoiceNo.setText(model.getInvoiceNo());
            rowViewHolder.qtyValue.setText(model.getQty());
            rowViewHolder.costValue.setText(model.getCost());
            rowViewHolder.netPrice.setText(model.getNetPrice());
            rowViewHolder.netProfit.setText(model.getProfit());
        }catch (Exception ex){

        }
    }

    @Override
    public int getItemCount() {
        return detailsList.size(); // one more to add header row
    }

    public void filterList(ArrayList<ProductAnalyzeModel.ProductDetails> filterdNames) {
        detailsList = filterdNames;
        notifyDataSetChanged();
    }


    public static class RowViewHolder extends RecyclerView.ViewHolder {
        protected TextView slNo;
        protected TextView invoiceNo;
        protected TextView qtyValue;
        protected TextView costValue;
        private TextView netPrice;
        private TextView netProfit;
        public RowViewHolder(View view) {
            super(view);
            slNo=view.findViewById(R.id.sl_no);
            invoiceNo=view.findViewById(R.id.invoice_no);
            qtyValue=view.findViewById(R.id.qty);
            costValue=view.findViewById(R.id.cost);
            netPrice=view.findViewById(R.id.net_price);
            netProfit=view.findViewById(R.id.profit);
        }
    }
}