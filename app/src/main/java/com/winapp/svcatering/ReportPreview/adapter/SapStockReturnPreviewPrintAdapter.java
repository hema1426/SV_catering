package com.winapp.svcatering.ReportPreview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.StockBadRequestReturnModel;

import java.util.ArrayList;

public class SapStockReturnPreviewPrintAdapter extends RecyclerView.Adapter<SapStockReturnPreviewPrintAdapter.ViewHolder> {

    private ArrayList<StockBadRequestReturnModel.StockBadRequestReturnDetails> stockBadRequestReturnDetailsArrayList;
    private Context context;
    View view;
    private String printView;

    public SapStockReturnPreviewPrintAdapter(Context context, ArrayList<StockBadRequestReturnModel.StockBadRequestReturnDetails> stockBadRequestReturnDetailsArrayList, String printView) {
        this.context=context;
        this.stockBadRequestReturnDetailsArrayList = stockBadRequestReturnDetailsArrayList;
        this.printView=printView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.sap_ro_stockreturn_preview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        StockBadRequestReturnModel.StockBadRequestReturnDetails stockBadRequestReturnDetails =stockBadRequestReturnDetailsArrayList.get(position);

        viewHolder.sno.setText(String.valueOf(position+1));
        viewHolder.qty.setText(stockBadRequestReturnDetails.getQty().toString());
        viewHolder.desc.setText(stockBadRequestReturnDetails.getDescription());
        viewHolder.uom.setText(stockBadRequestReturnDetails.getUomCode());

    }

    @Override
    public int getItemCount() {
        return stockBadRequestReturnDetailsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView sno;
        private TextView desc;
        private TextView qty;
        private TextView uom;

        public ViewHolder(View view) {
            super(view);
            sno=view.findViewById(R.id.item_sNo);
            desc=view.findViewById(R.id.item_desc);
            qty=view.findViewById(R.id.item_qty);
            uom=view.findViewById(R.id.item_uom);
        }
    }

}