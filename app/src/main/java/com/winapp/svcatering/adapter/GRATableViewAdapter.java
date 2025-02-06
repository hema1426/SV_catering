package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;

public class GRATableViewAdapter extends RecyclerView.Adapter {
    ArrayList<GRACartModel> summaryList;
    Context context;
    CallBack callBack;

    public GRATableViewAdapter(Context context,ArrayList<GRACartModel> summaryList,CallBack callBack) {
        this.summaryList = summaryList;
        this.context=context;
        this.callBack=callBack;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gra_table_list_item, parent, false);
        return new RowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RowViewHolder rowViewHolder = (RowViewHolder) holder;
        int rowPos = rowViewHolder.getAdapterPosition();
        try {
            GRACartModel model=summaryList.get(position);
            rowViewHolder.itemCode.setText(model.getProductId());
            rowViewHolder.itemName.setText(model.getProductName());
            rowViewHolder.cartonQty.setText(model.getCartonQty());
            rowViewHolder.looseQty.setText(model.getUnitQty());
            rowViewHolder.cartonPrice.setText(model.getCartonPrice());
            rowViewHolder.loosePrice.setText(model.getUnitPrice());
            rowViewHolder.total.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getTotalValue())));
            rowViewHolder.subTotal.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getSubTotal())));
            rowViewHolder.netTotal.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));
          /*  double data = Double.parseDouble(model.getPcsPerCarton());
            double cn_qty=Double.parseDouble(model.getCartonQty());
            double lqty=Double.parseDouble(model.getUnitQty());
            double net_qty=(cn_qty*data)+lqty;*/
            rowViewHolder.qtyText.setText(model.getNetQty());
            rowViewHolder.taxText.setText(model.getTaxValue());

            rowViewHolder.editProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.editProduct(model,position);
                }
            });

            rowViewHolder.deleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.deleteProduct(position);
                }
            });

        }catch (Exception ex){

        }
    }

    @Override
    public int getItemCount() {
        return summaryList.size(); // one more to add header row
    }

    public ArrayList<GRACartModel> getCartList(){
        return summaryList;
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
        private ImageView editProduct;
        private ImageView deleteProduct;

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
            subTotal=view.findViewById(R.id.sub_total);
            netTotal=view.findViewById(R.id.net_total);
            taxText=view.findViewById(R.id.tax);
            mainLayout=view.findViewById(R.id.main_layout);
            editProduct=view.findViewById(R.id.edit_product);
            deleteProduct=view.findViewById(R.id.delete_product);
        }
    }

    public interface CallBack{
        void editProduct(GRACartModel model,int editPos);
        void deleteProduct(int pos);
    }
}