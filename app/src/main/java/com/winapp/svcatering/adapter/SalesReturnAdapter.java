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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.CashCollectionActivity;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CashCollectionInvoiceModel;
import com.winapp.svcatering.model.SalesReturnModel;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;

public class SalesReturnAdapter extends RecyclerView.Adapter<SalesReturnAdapter.ViewHolder> {
    private ArrayList<SalesReturnModel> salesReturnList;
    public CallBack callBack;
    private int selectedPosition = -1;// no selection by default
    private Context context;
    private DBHelper dbHelper;
    public static String net_return_value="0";

    public SalesReturnAdapter(Context context,ArrayList<SalesReturnModel> salesReturnList) {
        this.salesReturnList = salesReturnList;
        this.callBack=callBack;
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sales_return_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        SalesReturnModel model=salesReturnList.get(position);
        dbHelper=new DBHelper(context);
        viewHolder.salesReturnNo.setText(model.getSalesReturnNumber());
        viewHolder.salesReturnDate.setText(model.getSalesReturnDate());
        viewHolder.paidAmount.setText(model.getPaidAmount());
        viewHolder.balanceAmount.setText(model.getBalanceAmount());
        if (model.getIsCheked().equals("true")){
            viewHolder.salesReturnCheck.setChecked(true);
        }else {
            viewHolder.salesReturnCheck.setChecked(false);
        }
       /* if (Double.parseDouble(viewHolder.balanceAmount.getText().toString())==0.0){
            viewHolder.salesReturnCheck.setEnabled(false);
        }else {
            viewHolder.salesReturnCheck.setEnabled(true);
        }*/
        viewHolder.salesReturnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSalesReturnValues(viewHolder.salesReturnCheck.isChecked(),viewHolder.balanceAmount.getText().toString(),viewHolder,model);
            }
        });

        if (position % 2==1){
            viewHolder.mainLayout.setBackgroundColor(Color.parseColor("#f5f5f5"));
        }
       // setValues();
       // setSalesReturnValues(viewHolder.salesReturnCheck.isChecked(),viewHolder.balanceAmount.getText().toString(),viewHolder,model);
    }

    private void setValues() {
        double net_value=0.0;
        double net_paid_amount=0.0;
        double final_net_amount=0.0;
        if (!CashCollectionActivity.amountText.getText().toString().isEmpty()){
            net_paid_amount=Double.parseDouble(CashCollectionActivity.amountText.getText().toString());
        }
        ArrayList<SalesReturnModel> salesReturnList=dbHelper.getAllSalesReturn();
        for(SalesReturnModel model:salesReturnList){
            if (model.getIsCheked().equals("true")){
                net_value+=Double.parseDouble(model.getBalanceAmount());
            }
        }

        final_net_amount=net_paid_amount-net_value;

        Log.e("FinalNetAmount:", String.valueOf(final_net_amount));
        Log.e("NetPaidAmount:", String.valueOf(net_paid_amount));
        Log.e("NetReturnValue:", String.valueOf(net_value));

        CashCollectionActivity.totalPaid.setText(Utils.twoDecimalPoint(final_net_amount));
        CashCollectionActivity.amountText.setText(Utils.twoDecimalPoint(final_net_amount));
        CashCollectionActivity.returnAmountEditText.setText(Utils.twoDecimalPoint(net_value));
    }

    public void setSalesReturnValues(boolean ischecked,String balanceamount,ViewHolder viewHolder,SalesReturnModel model){
        double final_net_amount=0.0;
        double net_paid_amount=0.0;
        double net_return_amount=Double.parseDouble(balanceamount);

        ArrayList<CashCollectionInvoiceModel> list=dbHelper.getAllInvoices();
        for(CashCollectionInvoiceModel model1:list){
            if (!model1.getPayable().isEmpty()){
                net_paid_amount+=Double.parseDouble(model1.getPayable());
            }
            // }
        }

       /* if (!CashCollectionActivity.amountText.getText().toString().isEmpty()){
            net_paid_amount=Double.parseDouble(CashCollectionActivity.totalPaid.getText().toString());
        }*/

       Log.e("Net_paid_amount_value:", String.valueOf(net_paid_amount));

        Log.w("Return_amount_total",String.valueOf(net_return_amount));

        if (ischecked){
            if (net_paid_amount!=0){
                if (net_return_amount > net_paid_amount){
                    Toast.makeText(context,"Sales Return amount exceed",Toast.LENGTH_SHORT).show();
                    viewHolder.salesReturnCheck.setChecked(false);
                }else {
                    model.setReturnChecked(true);
                    final_net_amount=net_paid_amount;
                    model.setIsCheked("true");
                    model.setBalanceAmount(viewHolder.balanceAmount.getText().toString());
                    model.setPaidAmount(viewHolder.balanceAmount.getText().toString());
                    dbHelper.updateSalesReturn(model.getSalesReturnNumber(),"0.00",viewHolder.balanceAmount.getText().toString(),"true");
                    setNetTotal(final_net_amount,viewHolder,"remove",viewHolder.balanceAmount.getText().toString());
                }
            }else {
                Toast.makeText(context,"Please Pay invoice amount",Toast.LENGTH_SHORT).show();
                viewHolder.salesReturnCheck.setChecked(false);
            }
        }else {
            model.setReturnChecked(false);
            Log.w("Net_paid_amount:", String.valueOf(net_paid_amount));
            Log.w("Net_return_amount:", String.valueOf(net_return_amount));
          //  dbHelper.deleteSalesReturn(model.getSalesReturnNumber());
            dbHelper.updateSalesReturn(model.getSalesReturnNumber(),"0.00",viewHolder.balanceAmount.getText().toString(),"false");
            setNetTotal(net_paid_amount,viewHolder,"add",viewHolder.balanceAmount.getText().toString());
        }
    }

    public void setNetTotal(double netTotal,ViewHolder viewHolder,String action,String return_amount){
        try {
            double net_return_add_value =0.0;
            double net_return_remove_value=0.0;
            double final_net_amount=0.0;
            double net_paid_amount_final=0.0;

            ArrayList<CashCollectionInvoiceModel> invoice_list =dbHelper.getAllInvoices();
           for(CashCollectionInvoiceModel model1: invoice_list){
            if (!model1.getPayable().isEmpty()){
                net_paid_amount_final+=Double.parseDouble(model1.getPayable());
               }
             }
            ArrayList<SalesReturnModel> salesRList=dbHelper.getAllSalesReturn();
            for(SalesReturnModel model:salesRList){
                if (model.getIsCheked().equals("true")){
                    Log.e("Final_balance_amount:",model.getBalanceAmount());
                    net_return_add_value +=Double.parseDouble(model.getBalanceAmount());
                }
            }
            net_return_value=Utils.twoDecimalPoint(net_return_add_value);

            Log.e("FinalNetAmount:", String.valueOf(net_paid_amount_final));
            Log.e("FinalReturnAmount:", String.valueOf(net_return_add_value));

            final_net_amount=net_paid_amount_final-net_return_add_value;
            /*if (action.equals("remove")){
                final_net_amount=netTotal - Double.parseDouble(return_amount);
            }else {
                final_net_amount=netTotal + Double.parseDouble(return_amount);
            }*/



            if (final_net_amount<0 ){
                Toast.makeText(context,"Sales Return amount exceed",Toast.LENGTH_SHORT).show();
                viewHolder.salesReturnCheck.setChecked(false);
            }else if (net_return_add_value ==0.00){
                ArrayList<CashCollectionInvoiceModel> list=dbHelper.getAllInvoices();
                double net_paid_amount=0.0;
            for(CashCollectionInvoiceModel model1:list){
            if (!model1.getPayable().isEmpty()){
                net_paid_amount+=Double.parseDouble(model1.getPayable());
               }
              }
                CashCollectionActivity.totalPaid.setText(Utils.twoDecimalPoint(net_paid_amount));
                CashCollectionActivity.amountText.setText(Utils.twoDecimalPoint(net_paid_amount));
                CashCollectionActivity.returnAmountEditText.setText(net_return_value);
            }
            else {
                CashCollectionActivity.totalPaid.setText(Utils.twoDecimalPoint(final_net_amount));
                CashCollectionActivity.amountText.setText(Utils.twoDecimalPoint(final_net_amount));
                CashCollectionActivity.returnAmountEditText.setText(net_return_value);
            }
        }catch (Exception ex){
        }
    }

    @Override
    public int getItemCount() {
        return salesReturnList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView salesReturnNo;
        private TextView salesReturnDate;
        private TextView paidAmount;
        private TextView balanceAmount;
        private CheckBox salesReturnCheck;
        private LinearLayout mainLayout;
        public ViewHolder(View view) {
            super(view);
           salesReturnNo=view.findViewById(R.id.sales_return_no);
           salesReturnDate=view.findViewById(R.id.date);
           paidAmount=view.findViewById(R.id.paid_amount);
           balanceAmount=view.findViewById(R.id.balance_amount);
           salesReturnCheck=view.findViewById(R.id.sales_return_check);
           mainLayout=view.findViewById(R.id.main_layout);
        }
    }

    public interface CallBack{
        void sortProduct(String letter);
    }
}