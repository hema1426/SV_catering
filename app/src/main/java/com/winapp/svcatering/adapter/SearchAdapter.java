package com.winapp.svcatering.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.DescriptionActivity;
import com.winapp.svcatering.model.ProductsModel;

import java.util.ArrayList;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<ProductsModel> productsNames;
    private Context context;
    public SearchAdapter(Context context, ArrayList<ProductsModel> names) {
        this.context=context;
        this.productsNames = names;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_layout, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ProductsModel products= productsNames.get(position);
        holder.textViewName.setText(products.getProductName());
        holder.textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DescriptionActivity.class);
                intent.putExtra("productId",products.getProductCode());
                intent.putExtra("productName",products.getProductName());
                intent.putExtra("price",String.valueOf(products.getRetailPrice()));
                intent.putExtra("imageString", products.getProductImage());
                intent.putExtra("weight",products.getWeight());
                intent.putExtra("cartonPrice",products.getCartonPrice());
                intent.putExtra("unitPrice",products.getUnitCost());
                intent.putExtra("pcsPerCarton",products.getPcsPerCarton());
                intent.putExtra("stockQty",products.getStockQty());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return productsNames.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
        }
    }
    //This method will filter the list
    //here we are passing the filtered data
    //and assigning it to the list with notifydatasetchanged method
    public void filterList(ArrayList<ProductsModel> filterdNames) {
        this.productsNames = filterdNames;
        notifyDataSetChanged();
    }
}