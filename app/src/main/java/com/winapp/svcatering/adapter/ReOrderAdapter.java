package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.AppUtils;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;
import static com.winapp.svcatering.utils.Utils.fourDecimalPoint;
import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

public class ReOrderAdapter extends
        RecyclerView.Adapter<ReOrderAdapter.ProfileViewHolder> {
    /**
     * Declare the Context and Arraylist variables
     */
    private Context mContext;
    private ArrayList<CartModel> cartList;
    private DBHelper dbHelper;
    public  CallBack callBack;
    private SessionManager session;
    private String customerId;
    double net_amount=0.0;
    double carton_amount=0.0;
    double loose_amount=0.0;
    int cnQty=0;
    int lqty=0;
    double pcspercarton=0;
    View itemView;
    ArrayList<CustomerDetails> customerDetails;
    boolean isAllowLowStock=false;

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        // Declare the Required Variables
        private TextView productName;
        private TextView netQty;
        private ImageView thumbnail;
        private TextView productRate;
        private TextView itemQty;
        private Button moreButton;
        private ImageView itemAdd;
        private ImageView itemRemove;
        LinearLayout rootLayout;
        TextView pcsQtyValue;
        TextView ctnQtyValue;
        private ImageView ctnPlus;
        private ImageView ctnMinus;
        private ImageView pcsPlus;
        private ImageView pcsMinus;
        private LinearLayout pcsLayout;

        public ProfileViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.product_name);
            productRate=view.findViewById(R.id.item_price);
            pcsQtyValue=view.findViewById(R.id.pcs_qty_value);
            ctnQtyValue=view.findViewById(R.id.ctn_qty_value);
            netQty=view.findViewById(R.id.qty);
            ctnMinus=view.findViewById(R.id.ctn_minus);
            ctnPlus=view.findViewById(R.id.ctn_plus);
            pcsMinus=view.findViewById(R.id.pcs_minus);
            pcsPlus=view.findViewById(R.id.pcs_plus);
            pcsLayout=view.findViewById(R.id.pcs_layout);
            rootLayout=view.findViewById(R.id.rootLayout);

        }
    }

    /**
     * Constructor for the send the arraylist and context
     * @param mContext
     * @param
     *
     */

    public ReOrderAdapter(Context mContext, ArrayList<CartModel> cartListItems, CallBack callBack){
        this.mContext=mContext;
        this.cartList=cartListItems;
        this.callBack=callBack;
    }
    /**
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reorder_cart_items, parent, false);
        return new ProfileViewHolder(itemView);
    }
    /**
     *
     * @param holder
     * @param position
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ProfileViewHolder holder, final int position) {
        try {
            final CartModel products = cartList.get(position);
            session=new SessionManager(mContext);
            dbHelper=new DBHelper(mContext);
            HashMap<String ,String > user=session.getUserDetails();
            customerId=user.get(SessionManager.KEY_CUSTOMER_ID);
            // display name with Caps letter
            holder.productName.setText(products.getCART_COLUMN_PNAME().substring(0, 1).toUpperCase() +products.getCART_COLUMN_PNAME().substring(1).toLowerCase());
            //   holder.itemQty.setText(products.getCART_COLUMN_QTY());
            holder.productRate.setText("$ "+Utils.twoDecimalPoint(Double.parseDouble(products.getCART_TOTAL_VALUE())));
            Log.w("PcsPercortion:",products.CART_PCS_PER_CARTON);
            double  pcspercarton=Double.parseDouble(products.getCART_PCS_PER_CARTON());
            if (pcspercarton>1){
                holder.pcsLayout.setVisibility(View.VISIBLE);
            }else {
                holder.pcsLayout.setVisibility(View.GONE);
            }

            Double d = Double.valueOf(products.getCART_COLUMN_CTN_QTY());
            Integer value = d.intValue();
            holder.ctnQtyValue.setText(String.valueOf(value));
            Double d1 = Double.valueOf(products.getCART_COLUMN_QTY());
            Integer value1 = d1.intValue();
            holder.pcsQtyValue.setText(String.valueOf(value1));
            double pcs = Double.parseDouble(products.getCART_PCS_PER_CARTON());
            double net_qty_value=(value*pcs)+value1;
            holder.netQty.setText(String.valueOf(net_qty_value));

            holder.ctnMinus.setOnClickListener(view -> {
                double data = Double.parseDouble(products.getCART_PCS_PER_CARTON());
                double cn_qty=Double.parseDouble(holder.ctnQtyValue.getText().toString());
                double lqty=Double.parseDouble(holder.pcsQtyValue.getText().toString());
                double net_qty=(cn_qty*data)+lqty;
                Log.e("Net_value_CTN:", String.valueOf(net_qty));
                if (lqty==0 && cn_qty==1){
                    showRemoveItemAlert(products.getCART_COLUMN_PID());
                }else {
                    int count=Integer.parseInt(holder.ctnQtyValue.getText().toString());
                    int ctn=count-1;
                    if (count!=0){
                        holder.ctnQtyValue.setText(String.valueOf(ctn));
                        holder.productRate.setText("$ "+setCalculation(products,
                                Integer.parseInt(holder.ctnQtyValue.getText().toString()),
                                Integer.parseInt(holder.pcsQtyValue.getText().toString())));
                    }
                }
            });

            holder.ctnPlus.setOnClickListener(view -> {

                getLowStockSetting();
                double pcsperctn=Double.parseDouble(products.getCART_PCS_PER_CARTON());
                double stock=Double.parseDouble(getStockQty(products.getCART_COLUMN_PID()));
                double allow_cn_qty=stock / pcsperctn;
                double net_qty=(Double.parseDouble(holder.ctnQtyValue.getText().toString()) * pcsperctn)+Double.parseDouble(holder.pcsQtyValue.getText().toString());
                int val = (int)net_qty;
                double net_qty_val=val + pcsperctn;
                double net_qty_allow=net_qty_val / pcsperctn ;
                Log.w("Net_Qty:", String.valueOf(val));
                Log.w("Allow_cn_qty:",String.valueOf(stock));
                if (val+1 > stock){
                    if (isAllowLowStock){
                        int count = Integer.parseInt(holder.ctnQtyValue.getText().toString());
                        int ctn = count + 1;
                        holder.ctnQtyValue.setText(String.valueOf(ctn));
                        holder.productRate.setText("$ " + setCalculation(products,
                                Integer.parseInt(holder.ctnQtyValue.getText().toString()),
                                Integer.parseInt(holder.pcsQtyValue.getText().toString())));
                    }else {
                        showLowStock();
                    }
                }else {
                    int count = Integer.parseInt(holder.ctnQtyValue.getText().toString());
                    int ctn = count + 1;
                    holder.ctnQtyValue.setText(String.valueOf(ctn));
                    holder.productRate.setText("$ " + setCalculation(products,
                            Integer.parseInt(holder.ctnQtyValue.getText().toString()),
                            Integer.parseInt(holder.pcsQtyValue.getText().toString())));
                }
            });

            holder.pcsMinus.setOnClickListener(view -> {
                double data = Double.parseDouble(products.getCART_PCS_PER_CARTON());
                double cn_qty=Double.parseDouble(holder.ctnQtyValue.getText().toString());
                double lqty=Double.parseDouble(holder.pcsQtyValue.getText().toString());
                double net_qty=(cn_qty*data)+lqty;
                Log.e("Net_value_PCS:", String.valueOf(net_qty));
                if (cn_qty==0 && lqty==1) {
                    showRemoveItemAlert(products.getCART_COLUMN_PID());
                }else {
                    int count=Integer.parseInt(holder.pcsQtyValue.getText().toString());
                    int ctn=count-1;
                    if (count!=0){
                        holder.pcsQtyValue.setText(String.valueOf(ctn));
                        holder.productRate.setText("$ "+setCalculation(products,
                                Integer.parseInt(holder.ctnQtyValue.getText().toString()),
                                Integer.parseInt(holder.pcsQtyValue.getText().toString())));
                    }
                }
            });

            holder.pcsPlus.setOnClickListener(view -> {
                getLowStockSetting();
                double pcsperctn=Double.parseDouble(products.getCART_PCS_PER_CARTON());
                double stock=Double.parseDouble(getStockQty(products.getCART_COLUMN_PID()));
                double net_qty=(Double.parseDouble(holder.ctnQtyValue.getText().toString()) * pcsperctn)+Double.parseDouble(holder.pcsQtyValue.getText().toString());
                int val = (int)net_qty;
                if (val + 1  > stock){
                    if (isAllowLowStock){
                        int count = Integer.parseInt(holder.pcsQtyValue.getText().toString());
                        int ctn = count + 1;
                        holder.pcsQtyValue.setText(String.valueOf(ctn));
                        holder.productRate.setText("$ " + setCalculation(products,
                                Integer.parseInt(holder.ctnQtyValue.getText().toString()),
                                Integer.parseInt(holder.pcsQtyValue.getText().toString())));
                    }else {
                        showLowStock();
                    }
                }else {
                    int count = Integer.parseInt(holder.pcsQtyValue.getText().toString());
                    int ctn = count + 1;
                    holder.pcsQtyValue.setText(String.valueOf(ctn));
                    holder.productRate.setText("$ " + setCalculation(products,
                            Integer.parseInt(holder.ctnQtyValue.getText().toString()),
                            Integer.parseInt(holder.pcsQtyValue.getText().toString())));
                }
            });

            if (position % 2==1 ){
                holder.rootLayout.setBackgroundColor(Color.parseColor("#f3f3f3"));
            }else {
                holder.rootLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            }

           // holder.itemRemove.setOnClickListener(view -> showRemoveItemAlert(products.getCART_COLUMN_PID()));
        }catch (Exception ex){
            Log.e("Error_in_adapter:", Objects.requireNonNull(ex.getMessage()));
        }
    }

    /**
     * Get = the low stock invoice setting to allow the Negative stock allow the invoice
     */
    public void getLowStockSetting(){
        ArrayList<SettingsModel> settings=dbHelper.getSettings();
        if (settings.size()>0) {
            for (SettingsModel model : settings) {
                if (model.getSettingName().equals("allow_negative_switch")) {
                    isAllowLowStock= model.getSettingValue().equals("1");
                }
            }
        }
    }


    public void showLowStock(){
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Warning !")
                .setContentText("Low Stock Please check!")
                .setConfirmText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();
    }

    public String getStockQty(String productId){
        String net_stock = "0";
        for (ProductsModel model: AppUtils.getProductsList()){
            if (model.getProductCode().equals(productId)){
                Log.w("ProductName:",model.getProductName());
                net_stock=model.getStockQty();
            }
        }
        return net_stock;
    }


    private String setCalculation(CartModel model,int cqty,int qty) {
        // Define the Required variables to calulate the Amount
        String sub_total = null;
        try {
            double cprice=Double.parseDouble(model.getCART_COLUMN_CTN_PRICE());
            double lprice=Double.parseDouble(model.getCART_UNIT_PRICE());
            cnQty=cqty;
            lqty=qty;
            pcspercarton=Double.parseDouble(model.getCART_PCS_PER_CARTON());
            // calculating the net total
            Log.w("LPrice:", String.valueOf(lprice));
            Log.w("CPrice:", String.valueOf(cprice));
            Log.w("PcsCarton:", String.valueOf(pcspercarton));
            if (pcspercarton>1){
                carton_amount=(cnQty*cprice);
                loose_amount=(lqty*lprice);
                net_amount=carton_amount+loose_amount;
                // netPrice.setText(String.valueOf(net_amount));
                sub_total = String.valueOf(net_amount);
            }else {
                carton_amount=(cnQty*cprice);
                // netPrice.setText(String.valueOf(carton_amount));
                sub_total =String.valueOf(carton_amount);
            }
            Log.w("Defined_product_price:", sub_total);
            taxCalculation(model.getCART_COLUMN_PID(),String.valueOf(lqty),String.valueOf(cnQty),Double.parseDouble(sub_total));
        }catch (Exception ex1){}
        assert sub_total != null;
        return  Utils.twoDecimalPoint(Double.parseDouble(sub_total));
    }

    public void taxCalculation(String productId,String lqty,String cnqty,double subTotal){
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences("customerPref",MODE_PRIVATE);
            String selectCustomerId = sharedPreferences.getString("customerId", "");
            customerDetails=dbHelper.getCustomer(selectCustomerId);

            String taxValue=customerDetails.get(0).getTaxPerc();
            String taxType=customerDetails.get(0).getTaxType();
            String itemDisc="";
            double taxAmount=0.0;
            double netTotal=0.0;

            Log.w("TaxType:",taxType);
            Log.w("TaxValue:",taxValue);

            if (!taxType.matches("") && !taxValue.matches("")) {

                double taxValueCalc = Double.parseDouble(taxValue);

                if (taxType.matches("E")) {

                    if (!itemDisc.matches("")) {
                        taxAmount = (subTotal * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);
                        netTotal = subTotal + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        Log.w("Tax_1",prodTax);
                        Log.w("Net_amount:",ProdNetTotal);
                        boolean update= dbHelper.updateCart(productId,lqty,cnqty, twoDecimalPoint(subTotal),prodTax,ProdNetTotal);
                        if (update){
                            callBack.updateNetAmount("net_amount_update");
                            Log.w("Update_success","Success");
                        }else {
                            Toast.makeText(mContext,"Error in update",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        taxAmount = (subTotal * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);
                        netTotal = subTotal + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        Log.w("Tax_1",prodTax);
                        Log.w("Net_amount:",ProdNetTotal);
                        boolean update= dbHelper.updateCart(productId,lqty,cnqty, twoDecimalPoint(subTotal),prodTax,ProdNetTotal);
                        if (update){
                            callBack.updateNetAmount("net_amount_update");
                            Log.w("Update_success","Success");
                        }else {
                            Toast.makeText(mContext,"Error in update",Toast.LENGTH_LONG).show();
                        }
                    }

                } else if (taxType.matches("I")) {
                    if (!itemDisc.matches("")) {
                        taxAmount = (subTotal * taxValueCalc)
                                / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        netTotal = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        Log.w("Tax_1",prodTax);
                        Log.w("Net_amount:",ProdNetTotal);
                        boolean update= dbHelper.updateCart(productId,lqty,cnqty, totalIncl,prodTax,ProdNetTotal);
                        if (update){
                            callBack.updateNetAmount("net_amount_update");
                            Log.w("Update_success","Success");
                        }else {
                            Toast.makeText(mContext,"Error in update",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        taxAmount = (subTotal * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        netTotal = subTotal + taxAmount;
                        netTotal = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        Log.w("Tax_1",prodTax);
                        Log.w("Net_amount:",ProdNetTotal);
                        boolean update= dbHelper.updateCart(productId,lqty,cnqty, totalIncl,prodTax,ProdNetTotal);
                        if (update){
                            callBack.updateNetAmount("net_amount_update");
                            Log.w("Update_success","Success");
                        }else {
                            Toast.makeText(mContext,"Error in update",Toast.LENGTH_LONG).show();
                        }
                    }
                } else if (taxType.matches("Z")) {
                    if (!itemDisc.matches("")) {
                        netTotal = subTotal + taxAmount;
                        netTotal = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        Log.w("Net_amount:",ProdNetTotal);
                        boolean update= dbHelper.updateCart(productId,lqty,cnqty, twoDecimalPoint(subTotal),"0.0",ProdNetTotal);
                        if (update){
                            callBack.updateNetAmount("net_amount_update");
                            Log.w("Update_success","Success");
                        }else {
                            Toast.makeText(mContext,"Error in update",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        netTotal = subTotal + taxAmount;
                        netTotal = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        Log.w("Net_amount:",ProdNetTotal);
                        boolean update= dbHelper.updateCart(productId,lqty,cnqty, twoDecimalPoint(subTotal),"0.0",ProdNetTotal);
                        if (update){
                            callBack.updateNetAmount("net_amount_update");
                            Log.w("Update_success","Success");
                        }else {
                            Toast.makeText(mContext,"Error in update",Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }

        }catch (Exception er){}

    }


    public void showRemoveItemAlert(final String pid){
        try {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    // .setTitleText("Are you sure?")
                    .setContentText("Are you sure want to remove this item ?")
                    .setConfirmText("YES")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            dbHelper.deleteProduct(pid);
                            callBack.updateNetAmount("remove_item");
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .showCancelButton(true)
                    .setCancelText("No")
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .show();
        }catch (Exception ex){}

    }

    /**
     * @return array list count
     */

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public interface CallBack {
        void updateNetAmount(String action);
    }
}
