package com.winapp.svcatering.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.TransferDetailModel;

import java.util.ArrayList;

public class TransferPreviewPrintAdapter extends RecyclerView.Adapter<TransferPreviewPrintAdapter.ViewHolder> {

    private ArrayList<TransferDetailModel.TransferDetails> transferDetailsLists;
    private Context context;
    View view;
    private String printView;
    public TransferPreviewPrintAdapter(Context context, ArrayList<TransferDetailModel.TransferDetails> transferDetails, String printView) {
        this.context=context;
        this.transferDetailsLists = transferDetails;
        this.printView=printView;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.transfer_preview_print_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        TransferDetailModel.TransferDetails transferList =transferDetailsLists.get(position);

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