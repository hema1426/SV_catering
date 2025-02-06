package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.TransferProductAddActivity;
import com.winapp.svcatering.model.CreateInvoiceModel;

import java.util.ArrayList;
import java.util.Objects;

public class TransferNewSummaryAdapter extends RecyclerView.Adapter<TransferNewSummaryAdapter.ViewHolder>{

    private ArrayList<CreateInvoiceModel> summaryList;
    public CallBack callBack;
    public Context context;

    public TransferNewSummaryAdapter(Context context,ArrayList<CreateInvoiceModel> customers, CallBack callBack) {
        this.summaryList = customers;
        this.callBack=callBack;
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transfer_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        try {
            CreateInvoiceModel model= summaryList.get(i);
            viewHolder.product.setText(model.getProductName().trim());
            viewHolder.productCode.setText(model.getProductCode());
            viewHolder.actualQty.setText(model.getActualQty());

            if (context instanceof TransferProductAddActivity){
                viewHolder.netQty.setVisibility(View.GONE);
                viewHolder.returnQty.setVisibility(View.GONE);
                viewHolder.focQty.setVisibility(View.GONE);
                viewHolder.priceValue.setVisibility(View.GONE);
                viewHolder.subTotalValue.setVisibility(View.GONE);
                viewHolder.gstValue.setVisibility(View.GONE);
                viewHolder.netTotalValue.setVisibility(View.GONE);
            }
            viewHolder.netQty.setText(String.valueOf(getQty(model.getNetQty())));
            viewHolder.returnQty.setText(model.getReturnQty());
            viewHolder.focQty.setText(model.getFocQty());
            viewHolder.priceValue.setText(model.getPrice());
            viewHolder.subTotalValue.setText(model.getSubTotal());
            viewHolder.gstValue.setText(model.getGstAmount());
            viewHolder.netTotalValue.setText(model.getNetTotal());

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popup = new PopupMenu(v.getContext(), v);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit_menu:
                                    callBack.editItem(model);
                                    return true;
                                case R.id.delete_menu:
                                    callBack.removeItem(model.getProductCode());
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.inflate(R.menu.activity_main_drawer);
                    popup.show();
                    return false;
                }
            });

     /*       viewHolder.removeItem.setOnClickListener(view -> callBack.removeItem(model.getProductId()));
            viewHolder.editProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            */

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

        private TextView product;
        private TextView productCode;
        private TextView actualQty;
        private TextView netQty;
        private TextView returnQty;
        private TextView focQty;
        private TextView priceValue;
        private TextView totalValue;
        private TextView subTotalValue;
        private TextView gstValue;
        private TextView netTotalValue;
        private ImageView removeItem;
        private ImageView editProduct;
        public ViewHolder(View view) {
            super(view);
            product = view.findViewById(R.id.product);
            productCode =view.findViewById(R.id.item_code);
            actualQty =view.findViewById(R.id.actual_qty);
            netQty=view.findViewById(R.id.net_qty);
            returnQty =view.findViewById(R.id.return_qty);
            focQty =view.findViewById(R.id.foc);
            priceValue=view.findViewById(R.id.price);
            subTotalValue=view.findViewById(R.id.sub_total);
            gstValue=view.findViewById(R.id.tax);
            netTotalValue=view.findViewById(R.id.net_total);
            // removeItem=view.findViewById(R.id.remove_item);
            // editProduct=view.findViewById(R.id.edit_product);
        }

    }

    public interface CallBack{
        void searchCustomer(String letter, int pos);
        void removeItem(String pid);
        void editItem(CreateInvoiceModel model);
    }

    public void filterList(ArrayList<CreateInvoiceModel> filterdNames) {
        this.summaryList = filterdNames;
        notifyDataSetChanged();
    }
}