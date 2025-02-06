package com.winapp.svcatering.ReportPreview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.winapp.svcatering.R;
import com.winapp.svcatering.model.ReportSalesSummaryModel;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;

public class SapSalesSum_InvoicePreviewPrintAdapter extends RecyclerView.Adapter<SapSalesSum_InvoicePreviewPrintAdapter.ViewHolder> {

    private ArrayList<ReportSalesSummaryModel.ReportSalesSummaryInvDetails> reportSalesSummaryDetailsArrayList;
    private Context context;
    View view;
    private String printView;

    public SapSalesSum_InvoicePreviewPrintAdapter(Context context, ArrayList<ReportSalesSummaryModel.ReportSalesSummaryInvDetails> reportSalesSummaryDetailsArrayList, String printView) {
        this.context=context;
        this.reportSalesSummaryDetailsArrayList = reportSalesSummaryDetailsArrayList;
        this.printView=printView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.sap_ro_salessum_invoice_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ReportSalesSummaryModel.ReportSalesSummaryInvDetails reportSalesSummarymodel =reportSalesSummaryDetailsArrayList.get(position);
        viewHolder.InvslNo.setText(String.valueOf(position+1));
        viewHolder.InvtransNo.setText(reportSalesSummarymodel.getTransNo());
        viewHolder.Invcust.setText(reportSalesSummarymodel.getCustomer());
        viewHolder.Invprice.setText("$   "+ Utils.twoDecimalPoint(Double.parseDouble(reportSalesSummarymodel.getAmount())));
    }

    @Override
    public int getItemCount() {
        return reportSalesSummaryDetailsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView InvslNo;
        private TextView InvtransNo;
        private TextView Invcust;
        private TextView Invprice;

        public ViewHolder(View view) {
            super(view);
            InvslNo =view.findViewById(R.id.item_sNoInv);
            InvtransNo =view.findViewById(R.id.item_transfNoInv);
            Invcust =view.findViewById(R.id.item_custInv);
            Invprice =view.findViewById(R.id.item_amtInv);
        }
    }
}