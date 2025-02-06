package com.winapp.svcatering.ReportPreview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.StockSummaryReportOpenModel;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;

public class SapStockSumOpenPreviewPrintAdapter extends RecyclerView.Adapter<SapStockSumOpenPreviewPrintAdapter.ViewHolder> {

    private ArrayList<StockSummaryReportOpenModel> reportStockSummaryDetailsArrayList;
    private Context context;
    View view;
    private String printView;

    public SapStockSumOpenPreviewPrintAdapter(Context context, ArrayList<StockSummaryReportOpenModel> reportStockSummaryDetailsArrayList, String printView) {
        this.context=context;
        this.reportStockSummaryDetailsArrayList = reportStockSummaryDetailsArrayList;
        this.printView=printView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.sap_ro_stocksum_open_preview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        StockSummaryReportOpenModel reportStockSummarymodel =reportStockSummaryDetailsArrayList.get(position);

        viewHolder.slNo.setText(String.valueOf(position+1));
        viewHolder.itemNameo.setText(reportStockSummarymodel.getItemName()+"-"+reportStockSummarymodel.getItemCode());

        if(!reportStockSummarymodel.getInwardQty().isEmpty() &&
                !reportStockSummarymodel.getInwardQty().equals("")){
            viewHolder.inqtyo.setText(Utils.twoDecimalPoint(Double.parseDouble(reportStockSummarymodel.getInwardQty())));
        }else{
            viewHolder.inqtyo.setText("0.0");
        }
        if(!reportStockSummarymodel.getClosingQty().isEmpty() &&
                !reportStockSummarymodel.getClosingQty().equals("")){
            viewHolder.closeBalo.setText(Utils.twoDecimalPoint(Double.parseDouble(reportStockSummarymodel.getClosingQty())));
        }else{
            viewHolder.closeBalo.setText("0.0");
        }
        if(!reportStockSummarymodel.getOpeningQty().isEmpty() &&
                !reportStockSummarymodel.getOpeningQty().equals("")){
            viewHolder.openBalo.setText(Utils.twoDecimalPoint(Double.parseDouble(reportStockSummarymodel.getOpeningQty())));
        }else{
            viewHolder.openBalo.setText("0.0");
        }
        if(!reportStockSummarymodel.getOutwardQty().isEmpty() &&
                !reportStockSummarymodel.getOutwardQty().equals("")){
            viewHolder.outqtyo.setText(Utils.twoDecimalPoint(Double.parseDouble(reportStockSummarymodel.getOutwardQty())));
        }else{
            viewHolder.outqtyo.setText("0.0");
        }
    }

    @Override
    public int getItemCount() {
        return reportStockSummaryDetailsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView slNo;
        private TextView itemNameo;
        private TextView openBalo;
        private TextView closeBalo;
        private TextView inqtyo;
        private TextView outqtyo;

        public ViewHolder(View view) {
            super(view);
            slNo =view.findViewById(R.id.item_sNo_open);
            itemNameo=view.findViewById(R.id.item_name_open);
            openBalo=view.findViewById(R.id.item_openingBal);
            closeBalo=view.findViewById(R.id.item_close_open);
            inqtyo=view.findViewById(R.id.item_inQty_open);
            outqtyo=view.findViewById(R.id.item_outQty_open);
        }
    }
}