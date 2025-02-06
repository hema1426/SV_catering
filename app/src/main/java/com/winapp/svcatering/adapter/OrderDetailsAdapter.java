package com.winapp.svcatering.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.winapp.svcatering.R;
import com.winapp.svcatering.db.DBHelper;

import java.util.ArrayList;
import java.util.Objects;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter .ViewHolder> {
    private ArrayList<OrderDetailsModel> orderDetailsList;
    public CallBack callBack;
    public DBHelper dbHelper;
    public Context context;
    public OrderDetailsAdapter(Context context,ArrayList<OrderDetailsModel> orderDetailsList, CallBack callBack) {
        this.orderDetailsList = orderDetailsList;
        this.callBack=callBack;
        this.context=context;
        dbHelper=new DBHelper(context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_details_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        try {
            // Define the model class object to define the values

            OrderDetailsModel model= orderDetailsList.get(position);
            viewHolder.productName.setText(model.getProductName());
            viewHolder.ctnQty.setText(model.getCartonPrice());
            viewHolder.pcsQty.setText(model.getPcsQty());
            viewHolder.netQty.setText(model.getNetQty());
            viewHolder.netAmount.setText("$ "+model.getNetAmount());
            viewHolder.productCheck.setChecked(model.isProductCheck);

            if (position % 2==1){
                viewHolder.rootLayout.setBackgroundColor(Color.parseColor("#f3f3f3"));
            }else {
                viewHolder.rootLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            }

        /*    viewHolder.productCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    insertOrderProducts(viewHolder.productCheck.isChecked(),model);
                    model.setProductCheck(viewHolder.productCheck.isChecked());
                    Log.e("ProductCheck:", String.valueOf(viewHolder.productCheck.isChecked()));
                    callBack.setNetTotalValue(orderDetailsList);
                }
            });*/

          /*  viewHolder.productCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    insertOrderProducts(viewHolder.productCheck.isChecked(),model);
                    model.setProductCheck(viewHolder.productCheck.isChecked());
                    Log.e("ProductCheck:", String.valueOf(viewHolder.productCheck.isChecked()));
                    callBack.setNetTotalValue(orderDetailsList);
                }
            });*/

            viewHolder.productCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    insertOrderProducts(viewHolder.productCheck.isChecked(),model);
                    model.setProductCheck(viewHolder.productCheck.isChecked());
                    Log.e("ProductCheck:", String.valueOf(viewHolder.productCheck.isChecked()));
                    callBack.setNetTotalValue(orderDetailsList);
                }
            });

        }catch (Exception ex){
            Log.e("OrderList Exception::", Objects.requireNonNull(ex.getMessage()));
        }
    }

    @Override
    public int getItemCount() {
        return orderDetailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView productName;
        private TextView ctnQty;
        private TextView pcsQty;
        private TextView netQty;
        private TextView netAmount;
        private CheckBox productCheck;
        private LinearLayout rootLayout;
        public ViewHolder(View view) {
            super(view);
            productName=view.findViewById(R.id.product_name);
            ctnQty=view.findViewById(R.id.ctn_qty);
            pcsQty=view.findViewById(R.id.pcs_qty);
            netQty=view.findViewById(R.id.net_qty);
            netAmount=view.findViewById(R.id.net_amount);
            productCheck=view.findViewById(R.id.product_check);
            rootLayout=view.findViewById(R.id.rootLayout);
        }
    }

    public void insertOrderProducts(boolean isCheked,OrderDetailsModel model){
        Log.w("ModelValues:",model.toString());
        if (isCheked){

            Gson gson = new Gson();

           //transform a java object to json
            Log.w("jsonValues =" , gson.toJson(model).toString());

       /*     boolean status= dbHelper.insertCart(
                    productId,
                    productName,
                    ctn_qty,
                    loose_qty,
                    subTotalValue.getText().toString(),"",
                    netTotalValue.getText().toString(),"weight",
                    cartonPrice.getText().toString(),
                    loosePrice.getText().toString(),
                    pcsPerCarton.getText().toString(),
                    taxValueText.getText().toString(),
                    String.valueOf(sub_total),
                    customerDetails.get(0).getTaxType(),
                    focEditText.getText().toString(),
                    focType,
                    exchangeEditext.getText().toString(),
                    exchangeType,
                    discount,
                    returnEditext.getText().toString(),
                    returnType,"",
                    String.valueOf(total),
                    stockQtyValue.getText().toString(),
                    uomText.getText().toString());
            */

          //  {"cartonPrice":"45.0","ctnQty":"5.0","isProductCheck":false,"loosePrice":"15.0",
            //  "netAmount":"256.8","netQty":"16.0","pcsPerCarton":"3","pcsQty":"1.0",
            //  "productId":"ALU003","productName":"ALUMINUM FOIL AF318","subTotal":"240.0","tax":"16.8"}
            /*dbHelper.insertCart(
                    model.getProductId(),
                    model.getProductName(),
                    model.getCtnQty(),
                    model.getPcsQty(),
                    model.getSubTotal(),
                    "",
                    model.getSubTotal(),
                    "weight",
                    model.getCartonPrice(),
                    model.getLoosePrice(),
                    model.getPcsPerCarton(),
                    model.getTax(),
                    model.getSubTotal(),
                    model.getTaxType(),
                    model.getFocQty(),
                    "",
                    model.getExchangeQty(),
                    "",
                    model.getItemDiscount(),
                    model.getReturnQty(),
                    "","",model.getTotal(),model.getStockQty(),model.getUomcode());*/

            Log.w("ModelDisplay:",model.toString());
            String return_qty = "0";
            double net_qty = Double.parseDouble(model.getCtnQty()) - Double.parseDouble(return_qty);
            String price_value = model.getLoosePrice();
            boolean status=
                    dbHelper.insertCreateInvoiceCartEdit(
                            model.getProductId(),
                            model.getProductName(),
                            model.getUomcode(),
                            model.getCtnQty(),
                            model.getReturnQty(),
                            String.valueOf(net_qty),
                            model.getFocQty(),
                            price_value,
                            model.getStockQty(),
                            model.getTotal(),
                            model.getSubTotal(),
                            model.getPriceWithGST(),
                            model.getNetAmount(),
                            model.getItemDiscount(),
                            model.getBillDisc(),
                            "",
                            "",
                            model.getExchangeQty(),
                            model.getMinimumSellingPrice(),model.getStockQty(),"",model.getIsItemFOC()
                    );
;


            /*dbHelper.insertCart(
                    model.getProductId(),
                    model.getProductName(),
                    model.getCtnQty(),
                    model.getPcsQty(),
                    "",
                    "",
                    model.getNetAmount(),
                    "6",
                    "7",
                    "8",
                    "90",
                    "10",
                    "11",
                    "12",
                    "13",
                    "14",
                    "15",
                    "16",
                    "17",
                    "18",
                    "19",
                    "20",
                    model.getNetAmount(),
                    "22",
                    "23");*/


       //     Toast.makeText(context,"Product added to your list",Toast.LENGTH_SHORT).show();
        }else {
            dbHelper.deleteInvoiceProduct(model.getProductId());
          //  Toast.makeText(context,"Product removed from your list",Toast.LENGTH_SHORT).show();
        }
    }

    /* dbHelper.insertCart(
             object.optString("ProductCode"),
             object.optString("ProductName"),
    cqty,
    lqty,
            object.optString("SubTotal"),
            "",
            object.optString("NetTotal"),
            "weight",
            object.optString("CartonPrice"),
            object.optString("Price"),
            object.optString("PcsPerCarton"),
            object.optString("Tax"),
            object.optString("SubTotal"),
            object.optString("TaxType"),
            object.optString("FOCQty"),
            "",
            object.optString("ExchangeQty"),
            "",
            object.optString("ItemDiscount"),
            object.optString("ReturnQty"),
            "");*/


    public static class OrderDetailsModel{
        private String productId;
        private String productName;
        private String ctnQty;
        private String pcsQty;
        private String netQty;
        private String netAmount;
        private String cartonPrice;
        private String loosePrice;
        private String total;
        private String subTotal;
        private String pcsPerCarton;
        private String tax;
        private String taxType;
        private String focQty;
        private String exchangeQty;
        private String itemDiscount;
        private String returnQty;
        private boolean isProductCheck;
        private String stockQty;
        private String uomcode;
        private String priceWithGST;
        private String itemDisc;
        private String billDisc;
        private String billDiscPercentage;
        private String minimumSellingPrice;
        private String updateTime;
        private String isItemFOC;

        public String getIsItemFOC() {
            return isItemFOC;
        }

        public void setIsItemFOC(String isItemFOC) {
            this.isItemFOC = isItemFOC;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getMinimumSellingPrice() {
            return minimumSellingPrice;
        }

        public void setMinimumSellingPrice(String minimumSellingPrice) {
            this.minimumSellingPrice = minimumSellingPrice;
        }

        public String getItemDisc() {
            return itemDisc;
        }

        public void setItemDisc(String itemDisc) {
            this.itemDisc = itemDisc;
        }

        public String getBillDisc() {
            return billDisc;
        }

        public void setBillDisc(String billDisc) {
            this.billDisc = billDisc;
        }

        public String getBillDiscPercentage() {
            return billDiscPercentage;
        }

        public void setBillDiscPercentage(String billDiscPercentage) {
            this.billDiscPercentage = billDiscPercentage;
        }

        public String getPriceWithGST() {
            return priceWithGST;
        }

        public void setPriceWithGST(String priceWithGST) {
            this.priceWithGST = priceWithGST;
        }

        public String getUomcode() {
            return uomcode;
        }

        public void setUomcode(String uomcode) {
            this.uomcode = uomcode;
        }

        public String getStockQty() {
            return stockQty;
        }

        public void setStockQty(String stockQty) {
            this.stockQty = stockQty;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getCartonPrice() {
            return cartonPrice;
        }

        public void setCartonPrice(String cartonPrice) {
            this.cartonPrice = cartonPrice;
        }

        public String getLoosePrice() {
            return loosePrice;
        }

        public void setLoosePrice(String loosePrice) {
            this.loosePrice = loosePrice;
        }

        public String getSubTotal() {
            return subTotal;
        }

        public void setSubTotal(String subTotal) {
            this.subTotal = subTotal;
        }

        public String getPcsPerCarton() {
            return pcsPerCarton;
        }

        public void setPcsPerCarton(String pcsPerCarton) {
            this.pcsPerCarton = pcsPerCarton;
        }

        public String getTax() {
            return tax;
        }

        public void setTax(String tax) {
            this.tax = tax;
        }

        public String getTaxType() {
            return taxType;
        }

        public void setTaxType(String taxType) {
            this.taxType = taxType;
        }

        public String getFocQty() {
            return focQty;
        }

        public void setFocQty(String focQty) {
            this.focQty = focQty;
        }

        public String getExchangeQty() {
            return exchangeQty;
        }

        public void setExchangeQty(String exchangeQty) {
            this.exchangeQty = exchangeQty;
        }

        public String getItemDiscount() {
            return itemDiscount;
        }

        public void setItemDiscount(String itemDiscount) {
            this.itemDiscount = itemDiscount;
        }

        public String getReturnQty() {
            return returnQty;
        }

        public void setReturnQty(String returnQty) {
            this.returnQty = returnQty;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getCtnQty() {
            return ctnQty;
        }

        public void setCtnQty(String ctnQty) {
            this.ctnQty = ctnQty;
        }

        public String getPcsQty() {
            return pcsQty;
        }

        public void setPcsQty(String pcsQty) {
            this.pcsQty = pcsQty;
        }

        public String getNetQty() {
            return netQty;
        }

        public void setNetQty(String netQty) {
            this.netQty = netQty;
        }

        public String getNetAmount() {
            return netAmount;
        }

        public void setNetAmount(String netAmount) {
            this.netAmount = netAmount;
        }

        public boolean isProductCheck() {
            return isProductCheck;
        }

        public void setProductCheck(boolean productCheck) {
            isProductCheck = productCheck;
        }
    }

    //OrderDetailsModel model
    public interface CallBack{
        void setNetTotalValue(ArrayList<OrderDetailsModel> list);
    }
}