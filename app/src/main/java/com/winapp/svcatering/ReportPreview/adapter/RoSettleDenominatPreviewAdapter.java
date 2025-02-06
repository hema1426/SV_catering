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
import com.winapp.svcatering.model.SettlementReceiptModel;

import java.util.ArrayList;


public class RoSettleDenominatPreviewAdapter extends RecyclerView.Adapter<RoSettleDenominatPreviewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<SettlementReceiptModel.CurrencyDenomination> dataList;

    public RoSettleDenominatPreviewAdapter(Context context, ArrayList<SettlementReceiptModel.CurrencyDenomination> denominationArrayList) {
        this.context = context;
        this.dataList = denominationArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.ro_setle_denominat_preview_item, parent, false));
    }

    public ArrayList<SettlementReceiptModel.CurrencyDenomination> getGRAlist() {
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
        public TextView denomi_name;
        public TextView denomi_total;
        public TextView denomi_count;
        public TextView denomi_sno;


        MyViewHolder(View itemView) {
            super(itemView);

            this.denomi_name = (TextView) itemView.findViewById(R.id.item_setle_denominat_name);
            this.denomi_total = (TextView) itemView.findViewById(R.id.item_setle_denominat_total);
            this.denomi_count = (TextView) itemView.findViewById(R.id.item_setle_denominat_count);
            this.denomi_sno = (TextView) itemView.findViewById(R.id.item_setle_denominatn_sno);
        }

        public void updateList(ArrayList<SettlementReceiptModel.CurrencyDenomination> list) {
            dataList = list;
            notifyDataSetChanged();
        }

        @SuppressLint("ClickableViewAccessibility")
        public void setData(SettlementReceiptModel.CurrencyDenomination denominationItem) {
            int pos = getAdapterPosition();
            denomi_sno.setText(String.valueOf(pos+1));

            denomi_name.setText(denominationItem.getDenomination());
            denomi_count.setText(denominationItem.getCount());
//            if(denominationItem.getCount().isEmpty() && !denominationItem.getCount().equalsIgnoreCase("null")) {
//                denomi_count.setText(denominationItem.getCount());
//            }
//            else{
//                denomi_count.setText("0");
//            }
            denomi_total.setText(twoDecimalPoint(Double.parseDouble(denominationItem.getTotal())));
        }

    }

}