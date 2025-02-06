package com.winapp.svcatering.ReportPreview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.winapp.svcatering.R;
import com.winapp.svcatering.model.ReportStockSummaryModel;

import java.util.ArrayList;

public class SapStockSummaryPreviewPrintAdapter extends RecyclerView.Adapter<SapStockSummaryPreviewPrintAdapter.ViewHolder> {

    private ArrayList<ReportStockSummaryModel.ReportStockSummaryDetails> reportStockSummaryDetailsArrayList;
    private Context context;
    View view;
    private String printView;

    public SapStockSummaryPreviewPrintAdapter(Context context, ArrayList<ReportStockSummaryModel.ReportStockSummaryDetails> reportStockSummaryDetailsArrayList, String printView) {
        this.context=context;
        this.reportStockSummaryDetailsArrayList = reportStockSummaryDetailsArrayList;
        this.printView=printView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.sap_ro_stocksummary_preview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ReportStockSummaryModel.ReportStockSummaryDetails reportStockSummaryDetails =reportStockSummaryDetailsArrayList.get(position);
        viewHolder.bf.setText((int)Double.parseDouble(reportStockSummaryDetails.getOpenQty())+"");
        viewHolder.in.setText((int)Double.parseDouble(reportStockSummaryDetails.getIn())+"");
        viewHolder.sales.setText((int)Double.parseDouble(reportStockSummaryDetails.getSalesQty())+"");
        viewHolder.foc.setText((int)Double.parseDouble(reportStockSummaryDetails.getFocQty())+"");
        viewHolder.tr.setText((int)Double.parseDouble(reportStockSummaryDetails.getOtherInorOut())+"");
        viewHolder.rplus.setText((int)Double.parseDouble(reportStockSummaryDetails.getRtnQty())+"");
        viewHolder.out.setText((int)Double.parseDouble(reportStockSummaryDetails.getOut())+"");
        viewHolder.bal.setText((int)Double.parseDouble(reportStockSummaryDetails.getBalance())+"");
        viewHolder.productname.setText(reportStockSummaryDetails.getProductName()+"");
    }

    @Override
    public int getItemCount() {
        return reportStockSummaryDetailsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView bf;
        private TextView in;
        private TextView sales;
        private TextView foc;
        private TextView tr;
        private TextView rplus;
        private TextView out;
        private TextView bal;
        private TextView productname;

        public ViewHolder(View view) {
            super(view);
            bf=view.findViewById(R.id.item_bf);
            in=view.findViewById(R.id.item_in);
            sales=view.findViewById(R.id.item_sales);
            foc=view.findViewById(R.id.item_foc);
            tr=view.findViewById(R.id.item_tr);
            rplus=view.findViewById(R.id.item_rplus);
            out=view.findViewById(R.id.item_out);
            bal=view.findViewById(R.id.item_bal);
            productname=view.findViewById(R.id.item_pdt);
        }
    }

}