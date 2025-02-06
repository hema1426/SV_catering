package com.winapp.svcatering.ReportPreview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.ProductsModel;

import java.util.ArrayList;

public class RoProductStockPreviewAdapter extends RecyclerView.Adapter<RoProductStockPreviewAdapter.ViewHolder> {

    private ArrayList<ProductsModel> productsModelList;
    private Context context;
    View view;
    private String printView;
    public RoProductStockPreviewAdapter(Context context, ArrayList<ProductsModel> productsModelList, String printView) {
        this.context=context;
        this.productsModelList = productsModelList;
        this.printView=printView;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.ro_product_stock_preview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ProductsModel productsModel =productsModelList.get(position);

        viewHolder.slNo.setText(String.valueOf(position+1));
        viewHolder.pdtcode.setText(productsModel.getProductCode());
        viewHolder.pdtname.setText(productsModel.getProductName());
        viewHolder.stock.setText(productsModel.getStockQty());
    }

    @Override
    public int getItemCount() {
        return productsModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView slNo;
        private TextView pdtcode;
        private TextView pdtname,stock;

        public ViewHolder(View view) {
            super(view);
            slNo=view.findViewById(R.id.itemsno);
            pdtcode=view.findViewById(R.id.item_pdtstockcode);
            pdtname=view.findViewById(R.id.item_pdtstockname);
            stock=view.findViewById(R.id.item_pdtstock);
        }
    }

}