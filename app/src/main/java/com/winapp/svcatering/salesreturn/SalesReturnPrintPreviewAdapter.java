package com.winapp.svcatering.salesreturn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;

import java.util.ArrayList;

public class SalesReturnPrintPreviewAdapter extends RecyclerView.Adapter<SalesReturnPrintPreviewAdapter.ViewHolder> {

    private ArrayList<SalesReturnPrintPreviewModel.SalesReturnDetails> salesReturnList;
    private Context context;

    public SalesReturnPrintPreviewAdapter(Context context, ArrayList<SalesReturnPrintPreviewModel.SalesReturnDetails> invoices) {
        this.context = context;
        this.salesReturnList = invoices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sales_return_print_preview_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        try {
            SalesReturnPrintPreviewModel.SalesReturnDetails salesReturnList = this.salesReturnList.get(position);
            viewHolder.code.setText(salesReturnList.getProductCode());
            viewHolder.description.setText(salesReturnList.getDescription());
            viewHolder.qtyValue.setText(salesReturnList.getNetqty());
            viewHolder.price.setText(salesReturnList.getPrice());
            viewHolder.total.setText(salesReturnList.getTotal());
        }catch (Exception e){}
    }

    @Override
    public int getItemCount() {
        return salesReturnList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView code;
        private TextView description;
        private TextView qtyValue;
        private TextView price;
        private TextView total;

        public ViewHolder(View view) {
            super(view);
            code = view.findViewById(R.id.code);
            description = view.findViewById(R.id.description);
            qtyValue = view.findViewById(R.id.qty);
            price = view.findViewById(R.id.price);
            total = view.findViewById(R.id.total);
        }
    }
}