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
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;

public class NewProductSummaryAdapter extends RecyclerView.Adapter<NewProductSummaryAdapter.ViewHolder>{

    private ArrayList<CreateInvoiceModel> summaryList;
    public CallBack callBack;
    public Context context;
   double stockVal = 0.0;
    double qtyVal= 0.0;
    double finalStockVal = 0.0;

    public NewProductSummaryAdapter(Context context,ArrayList<CreateInvoiceModel> customers, CallBack callBack) {
        this.summaryList = customers;
        this.callBack=callBack;
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.create_invoice_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        try {
            CreateInvoiceModel model= summaryList.get(i);
            viewHolder.productCode.setText(model.getProductCode().trim());
            viewHolder.actualQty.setText(String.valueOf(getQty(model.getActualQty())));
            viewHolder.product.setText(model.getProductName().trim());

//            if(Objects.equals(CreateNewInvoiceActivity.Companion.getActivityFrom(), "Duplicate")){
//                stockVal = Double.parseDouble(model.getStockProductQty());
//                qtyVal = Double.parseDouble(model.getNetQty());
//
//                finalStockVal = stockVal - qtyVal ;
//                Log.w("stockSumval",""+stockVal+".."+qtyVal);
//                if(stockVal < 0.0){
//                    viewHolder.product.setText(model.getProductName().trim());
//                    viewHolder.stockQtyInvoice.setText(" ( " +model.getStockProductQty()+" No Stock )");
//                    Log.w("stockSum",""+model.getStockProductQty());
//                }else{
//                    if(finalStockVal < 0.0) {
//                        viewHolder.product.setText(model.getProductName().trim());
//                        viewHolder.stockQtyInvoice.setText(" ( " + finalStockVal + " No Stock )");
//                        Log.w("stockSum1",""+finalStockVal);
//                    }
//                    else {
//                        viewHolder.product.setText(model.getProductName().trim());
//                        viewHolder.stockQtyInvoice.setText(" ( " + finalStockVal + " )");
//                        Log.w("stockSum2",""+finalStockVal);
//                    }
//                }
//            }
//            else{
//                viewHolder.product.setText(model.getProductName().trim());
           // }

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
            viewHolder.uomItem.setText(String.valueOf(model.getUomCode()));
            viewHolder.returnQty.setText(String.valueOf(getQty(model.getReturnQty())));
            viewHolder.focQty.setText(String.valueOf(getQty(model.getFocQty())));
         //   viewHolder.priceValue.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getPrice())));
            viewHolder.priceValue.setText(model.getPrice());

            viewHolder.subTotalValue.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getSubTotal())));
            viewHolder.gstValue.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getGstAmount())));
            viewHolder.netTotalValue.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));
            viewHolder.exchValue.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getExchangeQty())));

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
                                    callBack.removeItem(model.getProductCode(),model.getUpdateTime());
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
        private TextView stockQtyInvoice;
        private TextView productCode;
        private TextView actualQty;
        private TextView netQty;
        private TextView uomItem;
        private TextView returnQty;
        private TextView focQty;
        private TextView priceValue;
        private TextView totalValue;
        private TextView subTotalValue;
        private TextView gstValue,exchValue;
        private TextView netTotalValue;
        private ImageView removeItem;
        private ImageView editProduct;
        public ViewHolder(View view) {
            super(view);
            product = view.findViewById(R.id.product);
            stockQtyInvoice = view.findViewById(R.id.stockQtyInv);
            productCode =view.findViewById(R.id.item_code);
            actualQty =view.findViewById(R.id.actual_qty);
            netQty=view.findViewById(R.id.net_qty);
            uomItem=view.findViewById(R.id.uomItem_inv);
            returnQty =view.findViewById(R.id.return_qty);
            focQty =view.findViewById(R.id.foc);
            priceValue=view.findViewById(R.id.price);
            subTotalValue=view.findViewById(R.id.sub_total);
            gstValue=view.findViewById(R.id.tax);
            netTotalValue=view.findViewById(R.id.net_total);
            exchValue=view.findViewById(R.id.exchItem);
           // removeItem=view.findViewById(R.id.remove_item);
           // editProduct=view.findViewById(R.id.edit_product);
        }

    }

    public interface CallBack{
        void searchCustomer(String letter, int pos);
        void removeItem(String pid, String updateTime);
        void editItem(CreateInvoiceModel model);
    }
    public ArrayList<CreateInvoiceModel> getList(){
        return summaryList;
    }

    public void filterList(ArrayList<CreateInvoiceModel> filterdNames) {
        this.summaryList = filterdNames;
        notifyDataSetChanged();
    }
}