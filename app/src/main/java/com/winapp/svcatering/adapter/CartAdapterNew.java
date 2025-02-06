package com.winapp.svcatering.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.winapp.svcatering.R;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.AppUtils;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;
import static com.winapp.svcatering.utils.Utils.fourDecimalPoint;
import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

/**
 * This Adapter class used to display the All profile images and names
 */

public class CartAdapterNew extends
             RecyclerView.Adapter<CartAdapterNew.ProfileViewHolder> {
    /**
     * Declare the Context and Arraylist variables
     */
    private Context mContext;
    private ArrayList<CartModel> cartList;
    private DBHelper dbHelper;
    public  CallBack callBack;
    private SessionManager session;
    private String customerId;
    private String negativeStockStr = "No";
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
        private TextView itemDescription;
        private ImageView thumbnail;
        private TextView productRate;
        private TextView itemQty;
        private Button moreButton;
        private ImageView itemAdd;
        private ImageView itemRemove;
        private final ProgressBar progressBar;
        LinearLayout removeItemLayout;
        TextView pcsQtyValue;
        TextView ctnQtyValue;
        private ImageView ctnPlus;
        private ImageView ctnMinus;
        private ImageView pcsPlus;
        private ImageView pcsMinus;
        private LinearLayout pcsLayout;
        private LinearLayout ltotalLayout;
        private TextView cqty;
        private TextView lqty;
        private TextView cprice;
        private TextView lprice;
        private TextView ltottal;
        private TextView ctotal;

        public ProfileViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.item_name);
            itemDescription = view.findViewById(R.id.item_desc);
            thumbnail = view.findViewById(R.id.item_image);
            productRate=view.findViewById(R.id.item_price);
            pcsQtyValue=view.findViewById(R.id.pcs_qty_value);
            ctnQtyValue=view.findViewById(R.id.ctn_qty_value);
            ctnMinus=view.findViewById(R.id.ctn_minus);
            ctnPlus=view.findViewById(R.id.ctn_plus);
            pcsMinus=view.findViewById(R.id.pcs_minus);
            pcsPlus=view.findViewById(R.id.pcs_plus);
            itemRemove=view.findViewById(R.id.remove_item);
            pcsLayout=view.findViewById(R.id.pcs_layout);
            // moreButton=view.findViewById(R.id.more_btn);
            progressBar = view.findViewById(R.id.progressBar);

            ltotalLayout=view.findViewById(R.id.ltotal_layout);
            cqty=view.findViewById(R.id.cqty);
            lqty=view.findViewById(R.id.lqty);
            cprice=view.findViewById(R.id.cprice);
            lprice=view.findViewById(R.id.lprice);
            ltottal=view.findViewById(R.id.ltotal);
            ctotal=view.findViewById(R.id.ctotal);
        }
    }

    /**
     * Constructor for the send the arraylist and context
     * @param mContext
     * @param
     *
     */

    public CartAdapterNew(Context mContext, ArrayList<CartModel> cartListItems, CallBack callBack){
        this.mContext=mContext;
        this.cartList=cartListItems;
        this.callBack=callBack;
    }

    /**
     *
     * @param parent
     * @param viewType
     * @return
     */

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (Utils.isTablet(mContext)){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_cart_items, parent, false);
        }else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_mobile_layout, parent, false);
        }
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
                negativeStockStr=user.get(SessionManager.KEY_NEGATIVE_STOCK);

            // display name with Caps letter
                holder.productName.setText(products.getCART_COLUMN_PNAME().substring(0, 1).toUpperCase() +products.getCART_COLUMN_PNAME().substring(1).toLowerCase());
             //   holder.itemQty.setText(products.getCART_COLUMN_QTY());
                holder.productRate.setText("$ "+Utils.twoDecimalPoint(Double.parseDouble(products.getSubTotal())));
           // double  pcspercarton=Double.parseDouble(products.getCART_PCS_PER_CARTON());
            double  pcspercarton=1;
                if (pcspercarton>1){
                    holder.pcsLayout.setVisibility(View.VISIBLE);
                    holder.ltotalLayout.setVisibility(View.VISIBLE);
                }else {
                    holder.pcsLayout.setVisibility(View.GONE);
                    holder.ltotalLayout.setVisibility(View.INVISIBLE);
                }

              Double d = Double.valueOf(products.getCART_COLUMN_CTN_QTY());
              Integer value = d.intValue();
              holder.ctnQtyValue.setText(String.valueOf(value));
              Double d1 = Double.valueOf(products.getCART_COLUMN_QTY());
              Integer value1 = d1.intValue();
              holder.pcsQtyValue.setText(String.valueOf(value1));

              holder.cqty.setText(String.valueOf(value)+" * ");
              holder.lqty.setText(String.valueOf(value1)+" * ");

            double cprice=Double.parseDouble(products.getCART_COLUMN_CTN_PRICE());
            double lprice=Double.parseDouble(products.getCART_UNIT_PRICE());

            holder.cprice.setText(String.valueOf(cprice));
            holder.lprice.setText(String.valueOf(lprice));

            holder.ctotal.setText(" = "+Utils.twoDecimalPoint(value * cprice));
            holder.ltottal.setText(" = "+Utils.twoDecimalPoint(value1 * lprice));

          //  double net_val=Double.parseDouble(holder.ctotal.getText().toString()) + Double.parseDouble(holder.ltottal.getText().toString());
           // holder.productRate.setText("$ "+Utils.twoDecimalPoint(net_val));

              /*  if (Double.parseDouble(products.getCART_COLUMN_CTN_PRICE())==0){
                    holder.pcsMinus.setEnabled(false);
                    holder.pcsPlus.setEnabled(false);
                }*/

                // loading products cover using Glide library

            // Getting image from the Local DB
            String imagePath=dbHelper.getProductImage(products.getCART_COLUMN_PID());
            if (!imagePath.isEmpty()){
                String imageFullpath= Constants.folderPath+"/"+imagePath;
                File file = new File(imageFullpath);
                if (file.exists()){
                    Glide.with(mContext)
                            .load(file)
                            .error(R.drawable.no_image_found)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }
                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(holder.thumbnail);
                }else {
                    Glide.with(mContext)
                            .load(products.getCART_COLUMN_IMAGE())
                            .error(R.drawable.no_image_found)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }
                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(holder.thumbnail);
                }
            }else {
                Glide.with(mContext)
                        .load(products.getCART_COLUMN_IMAGE())
                        .error(R.drawable.no_image_found)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).into(holder.thumbnail);
             }

            // OnclickListener on Image Thumbnail
            holder.thumbnail.setOnClickListener(view -> {

            });

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
                    setValues(holder,products,Integer.parseInt(holder.ctnQtyValue.getText().toString()),Integer.parseInt(holder.pcsQtyValue.getText().toString()));
                    holder.productRate.setText("$ "+setCalculation(products,
                            Integer.parseInt(holder.ctnQtyValue.getText().toString()),
                            Integer.parseInt(holder.pcsQtyValue.getText().toString())));
                }
            }
           /* if (!holder.ctnQtyValue.getText().toString().equals("1")){
                int count=Integer.parseInt(holder.ctnQtyValue.getText().toString());
                int ctn=count-1;
                holder.ctnQtyValue.setText(String.valueOf(ctn));
                holder.productRate.setText("$ "+setCalculation(products,
                        Integer.parseInt(holder.ctnQtyValue.getText().toString()),
                        Integer.parseInt(holder.pcsQtyValue.getText().toString())));
            }*/
        });

        holder.ctnPlus.setOnClickListener(view -> {

            getLowStockSetting();
            double pcsperctn=Double.parseDouble(products.getCART_PCS_PER_CARTON());
          //  double stock=Double.parseDouble(getStockQty(products.getCART_COLUMN_PID()));
            double stock=Double.parseDouble(products.getStockQty());
            double allow_cn_qty=stock / pcsperctn;
            double net_qty=(Double.parseDouble(holder.ctnQtyValue.getText().toString()) * pcsperctn)+Double.parseDouble(holder.pcsQtyValue.getText().toString());
            int val = (int)net_qty;
            double net_qty_value=val + pcsperctn;
            double net_qty_allow=net_qty_value / pcsperctn ;
            Log.w("Net_Qty:", String.valueOf(val));
            Log.w("Allow_cn_qty:",String.valueOf(stock));
            if (val+1 > stock){
//                if (isAllowLowStock){
                if (negativeStockStr.equalsIgnoreCase("Yes")){

                    int count=Integer.parseInt(holder.ctnQtyValue.getText().toString());
                    int ctn=count+1;
                    holder.ctnQtyValue.setText(String.valueOf(ctn));
                    setValues(holder,products,Integer.parseInt(holder.ctnQtyValue.getText().toString()),Integer.parseInt(holder.pcsQtyValue.getText().toString()));
                    holder.productRate.setText("$ "+setCalculation(products,
                            Integer.parseInt(holder.ctnQtyValue.getText().toString()),
                            Integer.parseInt(holder.pcsQtyValue.getText().toString())));
                }else {
                    showLowStock();
                }
            }else {
                int count=Integer.parseInt(holder.ctnQtyValue.getText().toString());
                int ctn=count+1;
                holder.ctnQtyValue.setText(String.valueOf(ctn));
                setValues(holder,products,Integer.parseInt(holder.ctnQtyValue.getText().toString()),Integer.parseInt(holder.pcsQtyValue.getText().toString()));
                holder.productRate.setText("$ "+setCalculation(products,
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
                    setValues(holder,products,Integer.parseInt(holder.ctnQtyValue.getText().toString()),Integer.parseInt(holder.pcsQtyValue.getText().toString()));
                    holder.productRate.setText("$ "+setCalculation(products,
                            Integer.parseInt(holder.ctnQtyValue.getText().toString()),
                            Integer.parseInt(holder.pcsQtyValue.getText().toString())));

                }

            }

          /*  if (!holder.pcsQtyValue.getText().toString().equals("1")){
                int count=Integer.parseInt(holder.pcsQtyValue.getText().toString());
                int ctn=count-1;
                holder.pcsQtyValue.setText(String.valueOf(ctn));
                holder.productRate.setText("$ "+setCalculation(products,
                        Integer.parseInt(holder.ctnQtyValue.getText().toString()),
                        Integer.parseInt(holder.pcsQtyValue.getText().toString())));
            }*/
        });

        holder.pcsPlus.setOnClickListener(view -> {
            getLowStockSetting();
            double pcsperctn=Double.parseDouble(products.getCART_PCS_PER_CARTON());
            double stock=Double.parseDouble(products.getStockQty());
            double net_qty=(Double.parseDouble(holder.ctnQtyValue.getText().toString()) * pcsperctn)+Double.parseDouble(holder.pcsQtyValue.getText().toString());
            int val = (int)net_qty;
            if (val + 1  > stock){
//                if (isAllowLowStock){
                if (negativeStockStr.equalsIgnoreCase("Yes")){

                    int count=Integer.parseInt(holder.pcsQtyValue.getText().toString());
                    int ctn=count+1;
                    holder.pcsQtyValue.setText(String.valueOf(ctn));
                    setValues(holder,products,Integer.parseInt(holder.ctnQtyValue.getText().toString()),Integer.parseInt(holder.pcsQtyValue.getText().toString()));
                    holder.productRate.setText("$ "+setCalculation(products,
                            Integer.parseInt(holder.ctnQtyValue.getText().toString()),
                            Integer.parseInt(holder.pcsQtyValue.getText().toString())));
                }else {
                    showLowStock();
                }
            }else {
                int count=Integer.parseInt(holder.pcsQtyValue.getText().toString());
                int ctn=count+1;
                holder.pcsQtyValue.setText(String.valueOf(ctn));
                setValues(holder,products,Integer.parseInt(holder.ctnQtyValue.getText().toString()),Integer.parseInt(holder.pcsQtyValue.getText().toString()));
                holder.productRate.setText("$ "+setCalculation(products,
                        Integer.parseInt(holder.ctnQtyValue.getText().toString()),
                        Integer.parseInt(holder.pcsQtyValue.getText().toString())));
            }
        });

        holder.itemRemove.setOnClickListener(view -> showRemoveItemAlert(products.getCART_COLUMN_PID()));
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
        try {
            for (ProductsModel model: AppUtils.getProductsList()){
                if (model.getProductCode().equals(productId)){
                    Log.w("ProductName:",model.getProductName());
                    net_stock=model.getStockQty();
                }
            }
        }catch (Exception ex){
        }
        return net_stock;
    }


    public void setValues(final ProfileViewHolder holder,CartModel products,int cqty,int lqty){
        try {
            holder.cqty.setText(String.valueOf(cqty)+" * ");
            holder.lqty.setText(String.valueOf(lqty)+" * ");

            double cprice=Double.parseDouble(products.getCART_COLUMN_CTN_PRICE());
            double lprice=Double.parseDouble(products.getCART_UNIT_PRICE());

            holder.cprice.setText(String.valueOf(cprice));
            holder.lprice.setText(String.valueOf(lprice));

            holder.ctotal.setText(" = "+Utils.twoDecimalPoint(cqty * cprice));
            holder.ltottal.setText(" = "+Utils.twoDecimalPoint(lqty * lprice));
        }catch (Exception ex){}
    }

    private String setCalculation(CartModel model,int cqty,int qty) {
        // Define the Required variables to calulate the Amount
        String sub_total = null;
        double discount = 0.0;

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
           /* boolean update= dbHelper.updateCart(model.getCART_COLUMN_PID(),String.valueOf(lqty),String.valueOf(cnQty), sub_total);
            if (update){
                callBack.updateNetAmount("net_amount_update");
                Log.w("Update_success","Success");
            }else {
                Toast.makeText(mContext,"Error in update",Toast.LENGTH_LONG).show();
            }*/
        }catch (Exception ex1){}
        assert sub_total != null;
        if(model.getDiscount().isEmpty()){
            discount = 0.00 ;
        }
        else {
            discount = Double.parseDouble(model.getDiscount());
        }
        sub_total = String.valueOf(Double.parseDouble(sub_total) - discount);
        taxCalculation(model.getCART_COLUMN_PID(),String.valueOf(lqty),String.valueOf(cnQty),Double.parseDouble(sub_total));
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
                        boolean update= dbHelper.updateCart(productId,lqty,cnqty, ProdNetTotal,prodTax,ProdNetTotal);
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
                        boolean update= dbHelper.updateCart(productId,lqty,cnqty, ProdNetTotal,prodTax,ProdNetTotal);
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
                        boolean update= dbHelper.updateCart(productId,lqty,cnqty, twoDecimalPoint(subTotal),"0.00",ProdNetTotal);
                        if (update){
                            callBack.updateNetAmount("net_amount_update");
                            Log.w("Update_success","Success");
                        }else {
                            Toast.makeText(mContext,"Error in update",Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }else {
                netTotal = subTotal + taxAmount;
                netTotal = subTotal;
                String ProdNetTotal = twoDecimalPoint(netTotal);
                Log.w("Net_amountAdpt:",ProdNetTotal);
                boolean update= dbHelper.updateCart(productId,lqty,cnqty, twoDecimalPoint(subTotal),"0.00",ProdNetTotal);
                if (update){
                    callBack.updateNetAmount("net_amount_update");
                    Log.w("Update_success","Success");
                }else {
                    Toast.makeText(mContext,"Error in update",Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception ex){}
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
                    }).showCancelButton(true)
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