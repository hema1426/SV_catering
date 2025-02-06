package com.winapp.svcatering.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.utils.Utils;

import java.util.List;

public class TableViewAdapter extends RecyclerView.Adapter {
    List<CartModel> summaryList;

    public TableViewAdapter(List<CartModel> summaryList) {
        this.summaryList = summaryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.table_list_item, parent, false);

        return new RowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RowViewHolder rowViewHolder = (RowViewHolder) holder;
        int rowPos = rowViewHolder.getAdapterPosition();
        try {
            CartModel model=summaryList.get(position);
            rowViewHolder.itemCode.setText(model.getCART_COLUMN_PID());
            rowViewHolder.itemName.setText(model.getCART_COLUMN_PNAME());
            rowViewHolder.cartonQty.setText(model.getCART_COLUMN_CTN_QTY());
            rowViewHolder.looseQty.setText(model.getCART_COLUMN_QTY());
            rowViewHolder.cartonPrice.setText(model.getCART_COLUMN_CTN_PRICE());
            rowViewHolder.loosePrice.setText(model.getCART_UNIT_PRICE());
            rowViewHolder.total.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getCART_TOTAL_VALUE())));
            if (model.getFoc_qty()!=null && !model.getFoc_qty().equals("null") && !model.getFoc_qty().isEmpty()){
                rowViewHolder.focText.setText(model.getFoc_qty()+" "+model.getFoc_type());
            }else {
                rowViewHolder.focText.setText("0");
            }
          //  rowViewHolder.discText.setText("0");

           if (model.getDiscount() != null && !model.getDiscount().isEmpty()) {
               rowViewHolder.discText.setText(model.getDiscount());
           }else {
               rowViewHolder.discText.setText("0");
            }

            rowViewHolder.subTotal.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getSubTotal())));
            rowViewHolder.netTotal.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getCART_COLUMN_NET_PRICE())));
            double data = Double.parseDouble(model.getCART_PCS_PER_CARTON());
            double cn_qty=Double.parseDouble(model.getCART_COLUMN_CTN_QTY());
            double lqty=Double.parseDouble(model.getCART_COLUMN_QTY());
            double net_qty=(cn_qty*data)+lqty;
            rowViewHolder.qtyText.setText(String.valueOf(net_qty));
            rowViewHolder.taxText.setText(model.getCART_TAX_VALUE());

          /*  if (position % 2==0){
                rowViewHolder.mainLayout.setBackgroundColor(Color.parseColor("#d3d3d3"));
            }else {
                rowViewHolder.mainLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }*/


        }catch (Exception ex){

        }

      /*  if (rowPos == 0) {
            // Header Cells. Main Headings appear here
            rowViewHolder.txtRank.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtMovieName.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtYear.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtCost.setBackgroundResource(R.drawable.table_header_cell_bg);

            rowViewHolder.txtRank.setText("S.No");
            rowViewHolder.txtMovieName.setText("Product");
            rowViewHolder.txtYear.setText("Carton Qty");
            rowViewHolder.txtCost.setText("Loose Qty");
        } else {
            MovieModel modal = movieList.get(rowPos-1);

            // Content Cells. Content appear here
            rowViewHolder.txtRank.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtMovieName.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtYear.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtCost.setBackgroundResource(R.drawable.table_content_cell_bg);

            rowViewHolder.txtRank.setText(modal.getRank()+"");
            rowViewHolder.txtMovieName.setText(modal.getMovieName());
            rowViewHolder.txtYear.setText(modal.getYear()+"");
            rowViewHolder.txtCost.setText(modal.getBudgetInMillions()+"");
        }*/
    }

    @Override
    public int getItemCount() {
        return summaryList.size(); // one more to add header row
    }

    public class RowViewHolder extends RecyclerView.ViewHolder {
        protected TextView itemCode;
        protected TextView itemName;
        protected TextView cartonQty;
        protected TextView looseQty;
        private TextView cartonPrice;
        private TextView loosePrice;
        private TextView qtyText;
        private TextView focText;
        private TextView total;
        private TextView discText;
        private TextView subTotal;
        private TextView netTotal;
        private TextView taxText;
        private LinearLayout mainLayout;

        public RowViewHolder(View view) {
            super(view);
            itemCode= view.findViewById(R.id.item_code);
            itemName= view.findViewById(R.id.product);
            cartonQty= view.findViewById(R.id.carton_qty);
            looseQty= view.findViewById(R.id.loose_qty);
            cartonPrice=view.findViewById(R.id.ctn_price);
            loosePrice=view.findViewById(R.id.loose_price);
            qtyText=view.findViewById(R.id.qty);
            focText=view.findViewById(R.id.foc);
            total=view.findViewById(R.id.total);
            discText=view.findViewById(R.id.disc);
            subTotal=view.findViewById(R.id.balance_value);
            netTotal=view.findViewById(R.id.net_total);
            taxText=view.findViewById(R.id.tax);
            mainLayout=view.findViewById(R.id.main_layout);
        }
    }
}