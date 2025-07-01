package com.winapp.svcatering.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.StockTakeDetailModel;

import java.util.ArrayList;

public class StockTakePreviewPrintAdapter extends RecyclerView.Adapter<StockTakePreviewPrintAdapter.ViewHolder> {

    private ArrayList<StockTakeDetailModel.StockTakeDetail> transferDetailsLists;
    private Context context;
    View view;
    private String printView;
    public StockTakePreviewPrintAdapter(Context context, ArrayList<StockTakeDetailModel.StockTakeDetail> transferDetails, String printView) {
        this.context=context;
        this.transferDetailsLists = transferDetails;
        this.printView=printView;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.take_preview_print_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        StockTakeDetailModel.StockTakeDetail transferList =transferDetailsLists.get(position);

        viewHolder.slNo.setText(String.valueOf(position+1));
        viewHolder.product.setText(transferList.getDescription());
        viewHolder.qty.setText(transferList.getQty());
        viewHolder.uom.setText(transferList.getUomCode());
    }

    @Override
    public int getItemCount() {
        return transferDetailsLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView slNo;
        private TextView product;
        private TextView qty,uom;

        public ViewHolder(View view) {
            super(view);
            slNo=view.findViewById(R.id.itemsno);
            product=view.findViewById(R.id.itemproduct);
            qty=view.findViewById(R.id.itemqty);
            uom=view.findViewById(R.id.itemuom);
        }
    }

}