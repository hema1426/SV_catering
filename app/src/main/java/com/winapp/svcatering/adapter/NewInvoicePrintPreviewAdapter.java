package com.winapp.svcatering.adapter;

import static com.winapp.svcatering.activity.NewInvoiceListActivity.shortCodeStr;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;

public class NewInvoicePrintPreviewAdapter extends RecyclerView.Adapter<NewInvoicePrintPreviewAdapter.ViewHolder> {

    private ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceLists;
    private String companyName;
    private Context context;
    View view;
    private String printView;
    public NewInvoicePrintPreviewAdapter(Context context, ArrayList<InvoicePrintPreviewModel.InvoiceList> invoices,
                                         String printView , String companyName) {
        this.context=context;
        this.invoiceLists = invoices;
        this.printView=printView;
        this.companyName=companyName;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invoice_print_preview_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        InvoicePrintPreviewModel.InvoiceList invoiceList=invoiceLists.get(position);
        viewHolder.slNo.setText(String.valueOf(position+1));
//        viewHolder.product.setText(invoiceList.getDescription()+" ("+invoiceList.getUomCode()+")");

        if (invoiceList.getUomCode()!=null && !invoiceList.getUomCode().equals("null") && !invoiceList.getUomCode().isEmpty()){
          if(invoiceList.getCustomerItemCode()!=null && !invoiceList.getCustomerItemCode().equals("null")
                  && !invoiceList.getCustomerItemCode().isEmpty()){
              viewHolder.product.setText(invoiceList.getDescription()+" ("+invoiceList.getUomCode()+")"+"-"+invoiceList.getCustomerItemCode());
          }else {
              viewHolder.product.setText(invoiceList.getDescription() + " (" + invoiceList.getUomCode() + ")");
          }
        }else {
            if(invoiceList.getCustomerItemCode()!=null && !invoiceList.getCustomerItemCode().equals("null")
                    && !invoiceList.getCustomerItemCode().isEmpty()){
                viewHolder.product.setText(invoiceList.getDescription()+"-"+invoiceList.getCustomerItemCode());
            }else {
                viewHolder.product.setText(invoiceList.getDescription());
            }
        }
        if (invoiceList.getSaleType().equals("Return")){
            viewHolder.net.setText((int)Double.parseDouble(invoiceList.getNetQuantity())+" (as Return)");
        }else if (invoiceList.getSaleType().equals("FOC")){
            viewHolder.net.setText((int)Double.parseDouble(invoiceList.getNetQuantity())+" ( as FOC)");
        } else if (invoiceList.getSaleType().equals("Exchange")){
            viewHolder.net.setText((int)Double.parseDouble(invoiceList.getNetQuantity())+" ( as Exch)");
        }else {
            viewHolder.net.setText((int)Double.parseDouble(invoiceList.getNetQuantity())+"");
        }
//        if (invoiceList.getUomCode()!=null && !invoiceList.getUomCode().equals("null")
//                && !invoiceList.getUomCode().isEmpty()) {
//
//            if (invoiceList.getFocQty() != null && !invoiceList.getFocQty().isEmpty()
//                    && Double.parseDouble(invoiceList.getFocQty()) > 0) {
//                if (invoiceList.getExcQty() != null && !invoiceList.getExcQty().isEmpty()
//                        && Double.parseDouble(invoiceList.getExcQty()) > 0) {
//                    Log.w("excInv", "" + invoiceList.getExcQty());
//
//                    viewHolder.product.setText((invoiceList.getDescription()) +
//                            " (" + invoiceList.getUomCode() + ")" + " ( as FOC)" + " ( as Exc)");
//                } else {
//                    Log.w("excInv11", "" + invoiceList.getExcQty());
//
//                    viewHolder.product.setText((invoiceList.getDescription()) +
//                            " (" + invoiceList.getUomCode() + ")" + " ( as FOC)");
//                }
//            } else {
//                if (invoiceList.getExcQty() != null && !invoiceList.getExcQty().isEmpty()
//                        && Double.parseDouble(invoiceList.getExcQty()) > 0) {
//                    viewHolder.product.setText((invoiceList.getDescription()) +
//                            " (" + invoiceList.getUomCode() + ")" + " ( as Exc)");
//
//                }
//                else{
//             viewHolder.product.setText(invoiceList.getDescription()+" ("+invoiceList.getUomCode()+")");
//                }
//            }
//        }
//
//        else {
//            viewHolder.product.setText(invoiceList.getDescription());
//        }

//            if (invoiceList.getUomCode()!=null && !invoiceList.getUomCode().equals("null") && !invoiceList.getUomCode().isEmpty()){
//            if (Double.parseDouble(invoiceList.getTotal()) < 0.00){
//                viewHolder.product.setText((invoiceList.getDescription())+" ("+invoiceList.getUomCode()+")"+" (as Return)");
//            }else if (Double.parseDouble(invoiceList.getTotal())==0.00){
//                if (invoiceList.getReturnQty()!=null && !invoiceList.getReturnQty().isEmpty() && Double.parseDouble(invoiceList.getReturnQty()) > 0){
//                    viewHolder.product.setText((invoiceList.getDescription())+" ("+invoiceList.getUomCode()+")"+" ( as Return)");
//                }else {
//                    if (invoiceList.getExcQty()!=null && !invoiceList.getExcQty().isEmpty() && Double.parseDouble(invoiceList.getExcQty()) > 0) {
//                        viewHolder.product.setText((invoiceList.getDescription())+" ("+invoiceList.getUomCode()+")" + " ( as FOC)");
//                    }
//                    else{
//                        viewHolder.product.setText((invoiceList.getDescription())+" ("+invoiceList.getUomCode()+")" + " ( as FOC)"+ " ( as Exc)");
//                    }
//                }
//            }else {
//                viewHolder.product.setText(invoiceList.getDescription()+" ("+invoiceList.getUomCode()+")");
//            }
//        }
//        else {
//
//            if (Double.parseDouble(invoiceList.getTotal()) < 0.00){
//                viewHolder.product.setText(Double.parseDouble(invoiceList.getDescription())+" (as Return)");
//            }else if (Double.parseDouble(invoiceList.getTotal())==0.00){
//                if (invoiceList.getReturnQty()!=null && !invoiceList.getReturnQty().isEmpty() && Double.parseDouble(invoiceList.getReturnQty()) > 0){
//                    viewHolder.product.setText((invoiceList.getDescription())+" ( as Return)");
//                }else {
//                    if (invoiceList.getExcQty()!=null && !invoiceList.getExcQty().isEmpty() && Double.parseDouble(invoiceList.getExcQty()) > 0) {
//                        viewHolder.product.setText((invoiceList.getDescription()) + " ( as FOC)");
//                    }
//                    else{
//                        viewHolder.product.setText((invoiceList.getDescription()) + " ( as FOC)"+ " ( as Exc)");
//                    }
//                }
//            }else {
//                viewHolder.product.setText((invoiceList.getDescription()));
//            }
//        }

        Log.w("cpmadpta",""+companyName);
        if (companyName.equalsIgnoreCase("Trans Orient Singapore Pte Ltd") ||
                companyName.equalsIgnoreCase("RAYMANG EGGS & POULTRY SUPPLIER")) {
            viewHolder.rtn.setVisibility(View.INVISIBLE);
            viewHolder.iss.setVisibility(View.INVISIBLE);
        }
        else {
            viewHolder.rtn.setVisibility(View.VISIBLE);
            viewHolder.iss.setVisibility(View.VISIBLE);
            viewHolder.iss.setText(invoiceList.getNetQty());
            viewHolder.rtn.setText(invoiceList.getReturnQty());

        }
      //  viewHolder.net.setText(invoiceList.getNetQuantity());

       // viewHolder.price.setText(Utils.twoDecimalPoint(Double.parseDouble(invoiceList.getPricevalue())));
        if(shortCodeStr.equalsIgnoreCase("FUXIN")) {
            viewHolder.price.setText(Utils.fourDecimalPoint(Double.parseDouble((invoiceList.getPricevalue()))));
            viewHolder.total.setText(Utils.fourDecimalPoint(Double.parseDouble(invoiceList.getTotal())));
        }else{
            if(shortCodeStr.equalsIgnoreCase("SUPERSTAR")) {
                viewHolder.price.setText(Utils.fourDecimalPoint(Double.parseDouble((invoiceList.getPricevalue()))));
                viewHolder.total.setText(Utils.twoDecimalPoint(Double.parseDouble(invoiceList.getTotal())));
            }else{
                viewHolder.price.setText(invoiceList.getPricevalue());
                viewHolder.total.setText(Utils.twoDecimalPoint(Double.parseDouble(invoiceList.getTotal())));
            }
        }
    }

    @Override
    public int getItemCount() {
        return invoiceLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView slNo;
        private TextView product;
        private TextView rtn;
        private TextView net,iss;
        private TextView price;
        private TextView total;
        public ViewHolder(View view) {
            super(view);
            slNo=view.findViewById(R.id.itemsno);
            product=view.findViewById(R.id.itemproduct);
            rtn=view.findViewById(R.id.itemrtn);
            iss=view.findViewById(R.id.itemiss);
            net=view.findViewById(R.id.itemnet);
            price=view.findViewById(R.id.itemprice);
            total=view.findViewById(R.id.itemtotal);
        }
    }

}