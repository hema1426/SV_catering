package com.winapp.svcatering.salesreturn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.winapp.svcatering.R;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.ProductSummaryModel;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;

public class SalesReturnSummaryAdapter extends RecyclerView.Adapter<SalesReturnSummaryAdapter.ViewHolder> {

    private ArrayList<ProductSummaryModel> summaryList;
    public CallBack callBack;
    public DBHelper dbHelper;
    public Context context;
    private ArrayList<CustomerDetails> customerDetails;

    public SalesReturnSummaryAdapter(Context context,ArrayList<ProductSummaryModel> customers, CallBack callBack) {
        this.summaryList = customers;
        this.callBack=callBack;
        this.context=context;
        dbHelper=new DBHelper(this.context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.product_return_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        try {
            ProductSummaryModel model= summaryList.get(i);
            viewHolder.itemName.setText(model.getProductName().trim());
            viewHolder.itemDescription.setText(model.getDescription());
            viewHolder.netAmount.setText("$ "+ Utils.twoDecimalPoint(Double.parseDouble(model.getNetAmount())));
            viewHolder.netQty.setText(String.valueOf(getQty(model.getQty())));

            if (Double.parseDouble(model.getCtnQty()) > 0){
                viewHolder.ctnQty.setText(String.valueOf(getQty(model.getCtnQty())) +" x " + model.getCartonprice());
            }else {
                viewHolder.ctnQty.setText(String.valueOf(getQty(model.getCtnQty())));
            }

            if (Double.parseDouble(model.getPcsQty()) > 0){
                viewHolder.pcsQty.setText(String.valueOf(getQty(model.getPcsQty())) + " x " + model.getLooseprice());
            }else {
                viewHolder.pcsQty.setText(String.valueOf(getQty(model.getPcsQty())));
            }
            viewHolder.removeItem.setOnClickListener(view -> callBack.removeItem(model.getProductId()));
            viewHolder.editProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  viewHolder.editProduct.performClick();
                    callBack.editItem(
                            i,
                            model.getProductId(),
                            model.getCtnQty(),
                            model.getPcsQty(),
                            String.valueOf(getQty(model.getQty())),
                            model.getProductName().trim(),
                            model.getCartonprice(),
                            model.getLooseprice(),
                            model.getPcspercarton(),model);
                }
            });

            viewHolder.salesReturnTypeCheck.setChecked(model.getTypeCheckValue().equals("Y"));

            viewHolder.salesReturnTypeCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.salesReturnTypeCheck.isChecked()){
                        model.setTypeCheckValue("Y");
                    }else {
                        model.setTypeCheckValue("N");
                    }
                    dbHelper.updateReturnCheck( model.getProductId(),model.getTypeCheckValue());
                }
            });

        }catch (Exception ex){
            Log.w("Error_in_products:", Objects.requireNonNull(ex.getMessage()));
        }

    }

    public int getQty(String qty){
        double val=Double.parseDouble(qty);
        return (int)val;
    }

    @Override
    public int getItemCount() {
        return summaryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView itemName;
        private TextView itemDescription;
        private TextView netAmount;
        private TextView netQty;
        private TextView ctnQty;
        private TextView pcsQty;
        private ImageView removeItem;
        private ImageView editProduct;
        private CheckBox salesReturnTypeCheck;
        public ViewHolder(View view) {
            super(view);
            itemName = view.findViewById(R.id.item_name);
            itemDescription=view.findViewById(R.id.item_desc);
            netAmount=view.findViewById(R.id.net_amount);
            netQty=view.findViewById(R.id.qty);
            ctnQty=view.findViewById(R.id.ctn_qty);
            pcsQty=view.findViewById(R.id.pcs_qty);
            removeItem=view.findViewById(R.id.remove_item);
            editProduct=view.findViewById(R.id.edit_product);
            salesReturnTypeCheck=view.findViewById(R.id.return_type_check);
        }

    }

    public interface CallBack{
        void searchCustomer(String letter, int pos);
        void removeItem(String pid);
        void editItem(int pos,String pid,String cqty,String lqty,String netQty,String productname,String ctnprice,String looseprice,String pcspercarton,ProductSummaryModel model);
    }

    public void filterList(ArrayList<ProductSummaryModel> filterdNames) {
        this.summaryList = filterdNames;
        notifyDataSetChanged();
    }
}