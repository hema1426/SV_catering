package com.winapp.svcatering.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;

import java.util.ArrayList;

public class ExpensePrintPreviewAdapter extends RecyclerView.Adapter<ExpensePrintPreviewAdapter.ViewHolder> {

    private ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceLists;
    private Context context;
    View view;
    private String printView;
    public ExpensePrintPreviewAdapter(Context context, ArrayList<InvoicePrintPreviewModel.InvoiceList> invoices, String printView) {
        this.context=context;
        this.invoiceLists = invoices;
        this.printView=printView;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
       /*  if (context instanceof NewInvoiceListActivity){
             view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invoice_details_view_items, viewGroup, false);
         }else {
             view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invoice_print_preview_item, viewGroup, false);
         }*/

        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.expense_print_preview_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        InvoicePrintPreviewModel.InvoiceList invoiceList=invoiceLists.get(position);

        viewHolder.slNo.setText(String.valueOf(position+1));
        viewHolder.serviceNameStr.setText(invoiceList.getDescription());
        viewHolder.amountStr.setText(invoiceList.getPricevalue());
        viewHolder.accountStr.setText(invoiceList.getAccountName());

//        if (invoiceList.getUomCode()!=null && !invoiceList.getUomCode().equals("null") && !invoiceList.getUomCode().isEmpty()){
//            viewHolder.description.setText(invoiceList.getDescription()+" ("+invoiceList.getUomCode()+")");
//        }else {
//            viewHolder.description.setText(invoiceList.getDescription());
//        }
//
//        if (Double.parseDouble(invoiceList.getTotal()) < 0.00){
//            viewHolder.qtyValue.setText((int)Double.parseDouble(invoiceList.getNetQty())+" (as Return)");
//        }else if (Double.parseDouble(invoiceList.getTotal())==0.00){
//            if (invoiceList.getReturnQty()!=null && !invoiceList.getReturnQty().isEmpty() && Double.parseDouble(invoiceList.getReturnQty()) > 0){
//                viewHolder.qtyValue.setText((int)Double.parseDouble(invoiceList.getNetQty())+" ( as Return)");
//            }else {
//                viewHolder.qtyValue.setText((int)Double.parseDouble(invoiceList.getNetQty())+" ( as FOC)");
//            }
//        }else {
//            viewHolder.qtyValue.setText((int)Double.parseDouble(invoiceList.getNetQty())+"");
//        }

       /* if (invoiceList.getNetQty().contains("-")){
            viewHolder.qtyValue.setText((int)Double.parseDouble(invoiceList.getNetQty())+" (as Return)");
        }else {
            viewHolder.qtyValue.setText((int)Double.parseDouble(invoiceList.getNetQty())+"");
        }*/
       /* if (printView.equals("Invoice")){
            viewHolder.price.setVisibility(View.VISIBLE);
            viewHolder.total.setVisibility(View.VISIBLE);
        }else {
            viewHolder.price.setVisibility(View.GONE);
            viewHolder.total.setVisibility(View.GONE);
        }*/
        //viewHolder.price.setText(Utils.twoDecimalPoint(Double.parseDouble(invoiceList.getPricevalue())));

    }

    @Override
    public int getItemCount() {
        return invoiceLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView slNo;
        private TextView serviceNameStr;
        private TextView amountStr;
        private TextView accountStr;

        private TextView price;
        private TextView total;
        public ViewHolder(View view) {
            super(view);
            slNo=view.findViewById(R.id.expens_itemSno);
            serviceNameStr=view.findViewById(R.id.expens_itemServname);
            amountStr=view.findViewById(R.id.expens_itemServAmt);
            accountStr=view.findViewById(R.id.expens_accountname);


        }
    }

}