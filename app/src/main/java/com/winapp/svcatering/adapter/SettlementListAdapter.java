
package com.winapp.svcatering.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.winapp.svcatering.R;
import com.winapp.svcatering.model.SettlementListModel;
import com.winapp.svcatering.utils.Utils;
import java.util.ArrayList;

public class SettlementListAdapter extends RecyclerView.Adapter<SettlementListAdapter.ViewHolder> {
    private ArrayList<SettlementListModel> settlement;
    public CallBack callBack;
    private int selectedPosition = -1;// no selection by default

    public SettlementListAdapter(ArrayList<SettlementListModel> settlement, CallBack callBack) {
        this.settlement = settlement;
        this.callBack=callBack;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.settlement_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        try {
            SettlementListModel model= settlement.get(position);
            viewHolder.settlementNo.setText(model.getSettlementNumber());
            viewHolder.date.setText(model.getSettlementDate());
            if (model.getPaidAmount()!=null && !model.getPaidAmount().equals("null")){
                viewHolder.totalAmount.setText("$ "+ Utils.twoDecimalPoint(Double.parseDouble(model.getPaidAmount())));
            }else {
                viewHolder.totalAmount.setText("$ "+"0.00");
            }
            viewHolder.settlementBy.setText(model.getSettlementBy());
            viewHolder.locationCode.setText(model.getLocationCode());
            viewHolder.moreOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.showOption(
                            viewHolder.settlementNo.getText().toString(),
                            viewHolder.date.getText().toString(),
                            viewHolder.settlementBy.getText().toString(),
                            viewHolder.locationCode.getText().toString());
                }
            });
            if (Double.parseDouble(model.getTotalExpense()) > 0.00){
                viewHolder.expenseLayout.setVisibility(View.VISIBLE);
                viewHolder.expenseTotal.setText("$ "+model.getTotalExpense());
            }else {
                viewHolder.expenseLayout.setVisibility(View.GONE);
            }
        }catch (Exception exception){}
    }

    @Override
    public int getItemCount() {
        return settlement.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView settlementNo;
        private TextView totalAmount;
        private TextView date;
        private TextView locationCode;
        private TextView settlementBy;
        private TextView expenseTotal;
        private ImageView moreOption;
        private LinearLayout expenseLayout;
        public ViewHolder(View view) {
            super(view);
            settlementNo = view.findViewById(R.id.settlement_no);
            totalAmount =view.findViewById(R.id.total_amount);
            date =view.findViewById(R.id.date);
            locationCode=view.findViewById(R.id.location_code);
            expenseTotal=view.findViewById(R.id.expense_total);
            expenseLayout=view.findViewById(R.id.expense_layout);
            settlementBy=view.findViewById(R.id.user);
            moreOption=view.findViewById(R.id.more_option);
        }
    }

    public interface CallBack{
        void showOption(String settlementNumber, String date,String user,String location);
    }

    public void resetPosition(){
        selectedPosition=-1;
        notifyDataSetChanged();
    }
}