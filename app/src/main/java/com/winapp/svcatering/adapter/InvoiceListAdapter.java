package com.winapp.svcatering.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.InvoiceModel;

import java.util.ArrayList;

public class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListAdapter.ViewHolder> {
    private ArrayList<InvoiceModel> invoiceList;
    public CallBack callBack;
    public InvoiceListAdapter(ArrayList<InvoiceModel> invoiceList, CallBack callBack) {
        this.invoiceList = invoiceList;
        this.callBack=callBack;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invoice_list_items, viewGroup, false);
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
        InvoiceModel invoice = invoiceList.get(i);
        viewHolder.name.setText(invoice.getName());
        viewHolder.date.setText(invoice.getDate());
     //   viewHolder.address.setText(invoice.getAddress());
        viewHolder.soNumber.setText(invoice.getInvoiceNumber());
        viewHolder.balance.setText(invoice.getBalance());
        viewHolder.netTotal.setText(invoice.getNetTotal());

    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
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
           // address=view.findViewById(R.id.address);
            netTotal=view.findViewById(R.id.net_total);
        }
    }

    public interface CallBack{
        void sortProduct(String letter);
    }
}
