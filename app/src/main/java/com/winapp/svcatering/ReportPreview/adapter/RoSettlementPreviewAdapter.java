package com.winapp.svcatering.ReportPreview.adapter;

import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.SettlementReceiptDetailModel;

import java.util.ArrayList;


public class RoSettlementPreviewAdapter extends RecyclerView.Adapter<RoSettlementPreviewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<SettlementReceiptDetailModel> dataList;

    public RoSettlementPreviewAdapter(Context context, ArrayList<SettlementReceiptDetailModel> SettlementReceiptDetailModelList) {
        this.context = context;
        this.dataList = SettlementReceiptDetailModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.ro_settlement_preview_item, parent, false));
    }

    public ArrayList<SettlementReceiptDetailModel> getGRAlist() {
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
        public TextView setle_name;
        public TextView setle_paid;
        public TextView setle_no;
        public TextView setle_finalpaid;
        public TextView setle_credit;
        public LinearLayout banklay;
        public TextView bankcode;
        public TextView chequeno;
        public TextView chequedate;

        MyViewHolder(View itemView) {
            super(itemView);

            this.setle_name = (TextView) itemView.findViewById(R.id.itemsetl_name);
            this.setle_no = (TextView) itemView.findViewById(R.id.item_setleno);
            this.setle_paid = (TextView) itemView.findViewById(R.id.itempaid_setle);
            this.setle_finalpaid = (TextView) itemView.findViewById(R.id.itemfinalpaid_setle);
            this.setle_credit = (TextView) itemView.findViewById(R.id.itemcredi_setle);
            this.banklay = (LinearLayout) itemView.findViewById(R.id.bank_laysetle);
            this.bankcode = (TextView) itemView.findViewById(R.id.bankcode_setle);
            this.chequeno = (TextView) itemView.findViewById(R.id.cheque_no_setle);
            this.chequedate = (TextView) itemView.findViewById(R.id.cheque_date_setle);
        }

        public void updateList(ArrayList<SettlementReceiptDetailModel> list) {
            dataList = list;
            notifyDataSetChanged();
        }

        @SuppressLint("ClickableViewAccessibility")
        public void setData(SettlementReceiptDetailModel settlementItem) {

            setle_name.setText(settlementItem.getCustomerName());
            setle_no.setText(settlementItem.getReceiptNo());
            setle_paid.setText(twoDecimalPoint(Double.parseDouble(settlementItem.getPaidAmount())));
            setle_finalpaid.setText(twoDecimalPoint(Double.parseDouble(settlementItem.getPaidAmount())));
            setle_credit.setText(twoDecimalPoint(Double.parseDouble(settlementItem.getCreditAmount())));

            if(settlementItem.getPaymode().equalsIgnoreCase("Cheque")){
                banklay.setVisibility(View.VISIBLE);
                bankcode.setText(settlementItem.getBankCode());
                chequeno.setText(settlementItem.getChequeNo());
                chequedate.setText(settlementItem.getChequeDate());
            }
            else{
                banklay.setVisibility(View.GONE);
            }

        }

    }

}