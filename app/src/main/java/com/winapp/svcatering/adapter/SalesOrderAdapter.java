package com.winapp.svcatering.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.SalesOrderModel;

import java.util.ArrayList;

public class SalesOrderAdapter extends RecyclerView.Adapter<SalesOrderAdapter.ViewHolder> {
    private ArrayList<SalesOrderModel> salesOrderList;
    public CallBack callBack;
    public SalesOrderAdapter(ArrayList<SalesOrderModel> salesorders, CallBack callBack) {
        this.salesOrderList = salesorders;
        this.callBack=callBack;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sales_order_list_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
      /*  viewHolder.tv_letters.setText(salesOrderList.get(i));

        viewHolder.tv_letters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.sortProduct(viewHolder.tv_letters.getText().toString());
            }
        });*/
      SalesOrderModel salesOrder=salesOrderList.get(i);
      viewHolder.name.setText(salesOrder.getName());
      viewHolder.date.setText(salesOrder.getDate());
      viewHolder.address.setText(salesOrder.getAddress());
      viewHolder.soNumber.setText(salesOrder.getSaleOrderNumber());
      viewHolder.balance.setText(salesOrder.getBalance());
      viewHolder.netTotal.setText(salesOrder.getNetTotal());

    }

    @Override
    public int getItemCount() {
        return salesOrderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView date;
        private TextView soNumber;
        private TextView balance;
        private TextView netTotal;
        private TextView address;
        public ViewHolder(View view) {
            super(view);
            name=view.findViewById(R.id.name);
            date=view.findViewById(R.id.date);
            soNumber=view.findViewById(R.id.so_no);
            balance=view.findViewById(R.id.balance);
            address=view.findViewById(R.id.address);
            netTotal=view.findViewById(R.id.net_total);
        }
    }

    public interface CallBack{
        void sortProduct(String letter);
    }

}
