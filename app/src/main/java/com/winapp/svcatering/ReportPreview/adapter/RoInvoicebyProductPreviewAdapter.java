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
import com.winapp.svcatering.model.InvoiceByProductModel;

import java.util.ArrayList;


public class RoInvoicebyProductPreviewAdapter extends RecyclerView.Adapter<RoInvoicebyProductPreviewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<InvoiceByProductModel.ProductDetails> dataList;

    public RoInvoicebyProductPreviewAdapter(Context context, ArrayList<InvoiceByProductModel.ProductDetails> invoiceProductlist) {
        this.context = context;
        this.dataList = invoiceProductlist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.ro_invbyproduct_preview_item, parent, false));
    }

    public ArrayList<InvoiceByProductModel.ProductDetails> getGRAlist() {
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
        public TextView sno;
        public TextView qty;
        public TextView pdtdesc;


        MyViewHolder(View itemView) {
            super(itemView);

            this.pdtdesc = (TextView) itemView.findViewById(R.id.item_desc_invpdt);
            this.qty = (TextView) itemView.findViewById(R.id.item_qty_invpdt);
            this.sno = (TextView) itemView.findViewById(R.id.item_sno_invpdt);
        }

        public void updateList(ArrayList<InvoiceByProductModel.ProductDetails> list) {
            dataList = list;
            notifyDataSetChanged();
        }

        @SuppressLint("ClickableViewAccessibility")
        public void setData(InvoiceByProductModel.ProductDetails InvoiceProductItem) {
            int pos = getAdapterPosition();
            sno.setText(String.valueOf(pos+1));

            qty.setText(twoDecimalPoint(Double.parseDouble(InvoiceProductItem.getProductQty())));
            pdtdesc.setText(InvoiceProductItem.getProductName());
        }

    }

}