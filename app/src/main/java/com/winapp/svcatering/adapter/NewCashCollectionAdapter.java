package com.winapp.svcatering.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.CashCollectionActivity;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.fragments.CashInvoiceFragment;
import com.winapp.svcatering.model.CashCollectionInvoiceModel;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;

public class NewCashCollectionAdapter extends RecyclerView.Adapter<NewCashCollectionAdapter.ViewHolder> {

    private static ArrayList<CashCollectionInvoiceModel> cashCollectionInvoices;
    public CallBack callBack;
    public static TextWatcher payableTextWatcher;
    public static TextWatcher discountTextWatcher;
    public Context context;
    public onRefreshData onRefreshData;
    public static DBHelper dbHelper;

    public NewCashCollectionAdapter(Context context,ArrayList<CashCollectionInvoiceModel> invoices, CallBack callBack) {
        this.cashCollectionInvoices = invoices;
        this.callBack=callBack;
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cash_collection_invoice_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        CashCollectionInvoiceModel model= cashCollectionInvoices.get(position);
        dbHelper=new DBHelper(context);
        viewHolder.invoiceNumber.setText(model.getInvoiceNumber());
        viewHolder.cashChecked.setOnCheckedChangeListener(null);
        viewHolder.cashChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                model.setIsselect(isChecked);
                model.setPreSelect(isChecked);
            }
        });

        if (model.getIsselect()!=null){
            if(model.getIsselect()){
                viewHolder.cashChecked.setEnabled(true);
                viewHolder.cashChecked.setChecked(true);
            }else {
                viewHolder.cashChecked.setEnabled(true);
                viewHolder.cashChecked.setChecked(false);
            }
        }

        if (model.getIsbackground()!=null){
            if(model.getIsbackground()){
                viewHolder.itemView.setBackgroundResource(R.color.lit_blue);
            } else {
                viewHolder.itemView.setBackgroundResource(R.color.white);
            }
        }

        if (model.getIseditable()!=null){
            if(model.getIseditable()){
                viewHolder.payableText.setEnabled(false);
            } else {
                viewHolder.payableText.setEnabled(true);
            }
        }


        // viewHolder.payableText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        //  viewHolder.discountText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

        viewHolder.netTotal.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));
       /* if (model.getPayable()!=null && !model.getPayable().isEmpty()){
            viewHolder.payableText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getPayable())));
            viewHolder.mainLayout.setBackgroundColor(Color.parseColor("#A9DFBF"));
        }else {
            viewHolder.mainLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        if (model.getDiscount()!=null && !model.getDiscount().isEmpty()){
            viewHolder.discountText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getDiscount())));
            viewHolder.mainLayout.setBackgroundColor(Color.parseColor("#A9DFBF"));
        }else {
            viewHolder.mainLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }*/
        viewHolder.balanceAmount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBalance())));
        viewHolder.netBalanceText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetBalance())));

        if (model.getNetBalance()!=null && !model.getNetBalance().isEmpty()){
            Log.w("NetBalanceValues1:",model.getNetBalance());
            if (Double.parseDouble(model.getNetBalance()) < 0){
                Log.w("GivenValues1:",Double.parseDouble(model.getNetBalance())+"");
                viewHolder.discountText.setEnabled(false);
                viewHolder.discountText.setFocusable(false);
                viewHolder.discountText.setBackgroundColor(Color.parseColor("#f3f3f3"));
            }else {
                viewHolder.discountText.setEnabled(true);
                viewHolder.discountText.setFocusable(true);
            }
        }
      /*  if (!model.isDisabledDiscount()){
            viewHolder.discountText.setEnabled(false);
        }else {
            viewHolder.discountText.setEnabled(true);
        }*/
/*
        if (model.getChecked().equals("true")){
            viewHolder.cashChecked.setChecked(true);
        }else {
            viewHolder.cashChecked.setChecked(false);
        }*/

        viewHolder.invoiceDate.setText(model.getInvoiceDate());
        viewHolder.paidAmount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getPaidAmount())));
        if (model.getPayable()!=null){
            if (model.getPayable().isEmpty()){
                viewHolder.payableText.setText("");
            }else {
                viewHolder.payableText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getPayable())));
            }
        }
        viewHolder.discountAmount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getDiscountAmount())));
        viewHolder.tranType.setText(model.getTranType());
        viewHolder.payableText.setSelectAllOnFocus(true);
        viewHolder.discountText.setSelectAllOnFocus(true);

        payableTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (viewHolder.payableText.hasFocus()) {

                    try {
                        double discount=0.0;
                        double payable=0.0;
                        double balance=0.0;
                        double netbalance=0.0;

                        netbalance=Double.parseDouble(viewHolder.netBalanceText.getText().toString());

                        if (editable.toString().equals("-")){
                            viewHolder.payableText.removeTextChangedListener(payableTextWatcher);
                            viewHolder.payableText.setText("");
                            viewHolder.payableText.addTextChangedListener(payableTextWatcher);
                        }
                        if (!editable.toString().isEmpty()){
                            if (model.getTranType().equals("INV")){
                                if (Double.parseDouble(editable.toString()) > netbalance){
                                    //Toast.makeText(context,"Amount exceed",Toast.LENGTH_SHORT).show();
                                    model.setPayable("");
                                    model.setBalance(netbalance+"");

                                    viewHolder.payableText.removeTextChangedListener(payableTextWatcher);
                                    viewHolder.payableText.setText("");
                                    viewHolder.payableText.addTextChangedListener(payableTextWatcher);                                //setCalculation(model,viewHolder,editable.toString());
                                    setNetTotal();
                                }else {
                                    // model.setPayable(editable.toString());
                                    setCalculation(model,viewHolder,editable.toString());
                                    // setNetTotal();
                                }
                            }else {
                                Log.w("GivenBalanceAmount1:",viewHolder.balanceAmount.getText().toString());
                                double net_balance=Double.parseDouble(viewHolder.netBalanceText.getText().toString());
                                double pay_amount=Double.parseDouble(editable.toString());
                                double bal=net_balance+pay_amount;
                                if (bal  > 0){
                                    Toast.makeText(context,"Transaction value exceed",Toast.LENGTH_SHORT).show();
                                    model.setPayable("");
                                    model.setBalance(netbalance+"");

                                    viewHolder.payableText.removeTextChangedListener(payableTextWatcher);
                                    viewHolder.payableText.setText("");
                                    viewHolder.payableText.addTextChangedListener(payableTextWatcher);

                                    //setCalculation(model,viewHolder,editable.toString());
                                    setNetTotal();
                                }else {
                                    model.setPayable(editable.toString());
                                    setCalculation(model,viewHolder,editable.toString());
                                    setNetTotal();
                                }
                            }
                        }else {
                            if (!viewHolder.discountText.getText().toString().isEmpty()){
                                discount=Double.parseDouble(viewHolder.discountText.getText().toString());
                            }else {
                                model.setDiscount("");
                                discount=0.0;
                            }
                            balance=Double.parseDouble(model.getNetBalance()) - discount;
                            model.setBalance(balance+"");
                            model.setPayable("");
                            viewHolder.discountText.setText("");
                            viewHolder.balanceAmount.setText(balance+"");
                            setNetTotal();

                        }
                    }catch (Exception ec){}
                }else{
                    Log.w("receiptentr11:","");
                }
            }
        };
        if (viewHolder.payableText.isFocusable()){
            viewHolder.payableText.addTextChangedListener(payableTextWatcher);
        }

        discountTextWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // CashInvoiceFragment.amountCalculation();
                if (editable.toString().equals("-")) {
                    viewHolder.discountText.setText("");
                }

                double payable=0.0;
                double discount=0.0;
                double netbalance=0.0;
                double balance=0.0;

                if (!viewHolder.payableText.getText().toString().isEmpty()){
                    payable=Double.parseDouble(viewHolder.payableText.getText().toString());
                }

                netbalance=Double.parseDouble(viewHolder.netBalanceText.getText().toString());
                balance=netbalance-payable;
                if (!editable.toString().isEmpty()){
                    if (balance < 0){
                        // Toast.makeText(context,"New Balance exceed",Toast.LENGTH_SHORT).show();
                        model.setDiscount("0.00");
                        setDiscountCalculation(model,viewHolder,editable.toString());
                        setNetTotal();
                    }else {
                        model.setDiscount(editable.toString());
                        setDiscountCalculation(model,viewHolder,editable.toString());
                        setNetTotal();
                    }
                }else {
                    model.setDiscount("0.00");
                    setDiscountCalculation(model,viewHolder,editable.toString());
                    setNetTotal();
                }

            }
        };
        if (viewHolder.discountText.isFocusable()){
            viewHolder.discountText.addTextChangedListener(discountTextWatcher);
        }

        viewHolder.cashChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.cashChecked.isChecked()){
                    model.setChecked("true");
                    double paid_amt=0.0;
                    double net_amt=0.0;
                    double balance=0.0;
                    double discount=0.0;
                    double net_balance_amt=0;
                    //  viewHolder.discountText.setEnabled(false);
                    if (!viewHolder.paidAmount.getText().toString().isEmpty()){
                        paid_amt=Double.parseDouble(viewHolder.paidAmount.getText().toString());
                    }
                    if (!viewHolder.netTotal.getText().toString().isEmpty()){
                        net_amt=Double.parseDouble(viewHolder.netTotal.getText().toString());
                    }

                    if (!viewHolder.discountText.getText().toString().isEmpty()){
                        discount=Double.parseDouble(viewHolder.discountText.getText().toString());
                    }

                    if (!viewHolder.netBalanceText.getText().toString().isEmpty()){
                        net_balance_amt=Double.parseDouble(model.getNetBalance());
                    }

                    balance=net_balance_amt-discount;
                    model.setBalance("0.00");
                    viewHolder.payableText.setText(Utils.twoDecimalPoint(balance));
                    viewHolder.balanceAmount.setText("0.00");
                    if (balance < 0){
                        model.setPayable(Math.abs(balance)+"");
                    }else {
                        model.setPayable(String.valueOf(balance));
                    }
                    Log.w("GetPayableAmount:",model.getPayable()+"");
                    setNetTotal();
                    viewHolder.payableText.setEnabled(false);
                }else {
                    model.setChecked("false");
                    model.setPayable("");
                    model.setDiscount("");
                    viewHolder.payableText.setText("");
                    viewHolder.discountText.setEnabled(true);
                    viewHolder.discountText.setText("");
                    double paid_amt=0.0;
                    double net_amt=0.0;
                    double balance=0.0;
                    double discount=0.0;
                    double net_balance_amt=0.0;
                    if (!viewHolder.paidAmount.getText().toString().isEmpty()){
                        paid_amt=Double.parseDouble(viewHolder.paidAmount.getText().toString());
                    }
                    if (!viewHolder.netTotal.getText().toString().isEmpty()){
                        net_amt=Double.parseDouble(viewHolder.netTotal.getText().toString());
                    }

                    if (!viewHolder.discountText.getText().toString().isEmpty()){
                        discount=Double.parseDouble(viewHolder.discountText.getText().toString());
                    }

                    if (!viewHolder.netBalanceText.getText().toString().isEmpty()){
                        net_balance_amt=Double.parseDouble(model.getNetBalance());
                    }

                    balance=net_balance_amt-discount;
                    model.setBalance(Utils.twoDecimalPoint(balance));
                    viewHolder.balanceAmount.setText(Utils.twoDecimalPoint(balance));

                    setNetTotal();
                    viewHolder.payableText.setEnabled(true);
                }
            }
        });

    }

    public void checkedCheckBox(){
        int checked_count=0;
        for (CashCollectionInvoiceModel model:cashCollectionInvoices){
            if (model.getChecked().equals("true")){
                checked_count++;
            }
        }
        Log.w("CheckedCount:",checked_count+"");
        Log.w("TotalCount:",cashCollectionInvoices.size()+"");
        if (checked_count==cashCollectionInvoices.size()){
            CashInvoiceFragment.cashCheck.setChecked(true);
        }else {
            CashInvoiceFragment.cashCheck.setChecked(false);
        }
    }


    public void setUpdate(){
        /**
         * @function for the update the values to the CashCollection Activity and update the net total
         * display the Net amount payable and balance amount
         */
        double net_value=0.0;
        double net_discount=0.0;
        double net_outstanding=0.0;
        double net_payable_amount =0.0;
        double net_value_outstanding=0.0;
        double net_return=0.0;
        ArrayList<CashCollectionInvoiceModel> list=dbHelper.getAllInvoices();
        for(CashCollectionInvoiceModel model:list){
            net_outstanding+=Double.parseDouble(model.getBalance());
            net_value+=Double.parseDouble(model.getNetTotal());
            if (!model.getDiscount().isEmpty() && !model.getDiscount().equals(".")){
                net_discount+=Double.parseDouble(model.getDiscount());
            }
            if (!model.getPayable().isEmpty() && !model.getPayable().equals(".")){
                net_payable_amount +=Double.parseDouble(model.getPayable());
            }
        }
        // Display the Net Payable Amount to User Pay
        if (Double.parseDouble(CashCollectionActivity.returnAmountEditText.getText().toString())>0){
            net_return=Double.parseDouble(CashCollectionActivity.returnAmountEditText.getText().toString());
        }
        net_value_outstanding=net_outstanding-net_return;
        CashCollectionActivity.amountText.setText(Utils.twoDecimalPoint(net_payable_amount));
        CashCollectionActivity.selectedAmount.setText(Utils.twoDecimalPoint(net_payable_amount));
        CashCollectionActivity.totalPaid.setText(Utils.twoDecimalPoint(net_payable_amount));
        CashCollectionActivity.totalDiscount.setText(Utils.fourDecimalPoint(net_discount));
        // Define the Outstanding
        CashCollectionActivity.totalOutstanding.setText(Utils.twoDecimalPoint(net_outstanding));
    }

    public void setNetTotal(){
        try {

            double net_value=0.0;
            double net_discount=0.0;
            double net_outstanding=0.0;
            double paid_amount=0.0;
            double net_paid_amount_value=0.0;

            double entered_net_amount=0.0;
            double difference_amount=0.0;
            double selected_amount=0.0;

            double debit_amout=0.0;

            ArrayList<CashCollectionInvoiceModel> list=getList();
            for(CashCollectionInvoiceModel model:list){
                net_outstanding+=Double.parseDouble(model.getNetBalance());
                //if (model.isChecked()){
                net_value+=Double.parseDouble(model.getNetTotal());
                // if (model.getChecked().equals("true")){
                if (model.getDiscount()!=null && !model.getDiscount().isEmpty() && !model.getDiscount().equals(".")){
                    net_discount+=Double.parseDouble(model.getDiscount());
                }
                // }
                if (model.getTranType().equals("INV")){
                    if (model.getPayable()!=null && !model.getPayable().isEmpty() && !model.getPayable().equals(".")){
                        paid_amount+=Double.parseDouble(model.getPayable());
                    }
                }

                if (model.getTranType().equals("CN") || model.getTranType().equals("RE") || model.getTranType().equals("SR") || model.getTranType().equals("Excess")){
                    if (model.getPayable()!=null && !model.getPayable().isEmpty() && !model.getPayable().equals(".")){
                        debit_amout+=Double.parseDouble(model.getPayable());
                    }
                }
                // }
            }

            net_paid_amount_value=paid_amount-debit_amout;

            Log.w("SalesReturnValue1:",SalesReturnAdapter.net_return_value);
            Log.w("NetOutStanding1:", String.valueOf(net_outstanding));

            if (paid_amount < Double.parseDouble(SalesReturnAdapter.net_return_value) ){
                CashInvoiceFragment.refreshReturn();
            }

        /*    if (SalesReturnAdapter.net_return_value!=null && Double.parseDouble(SalesReturnAdapter.net_return_value) > 0){
                double net_paid_amount=paid_amount - Double.parseDouble(SalesReturnAdapter.net_return_value);
                if (net_paid_amount < 0) {
                    CashCollectionActivity.totalPaid.setText("0.00");
                    CashCollectionActivity.amountText.setText("0.00");
                    CashCollectionActivity.selectedAmount.setText("0.00");
                    CashCollectionActivity.totalDiscount.setText(Utils.fourDecimalPoint(net_discount));
                    CashCollectionActivity.totalOutstanding.setText(Utils.twoDecimalPoint(net_outstanding));
                }else {
                    CashCollectionActivity.totalPaid.setText(Utils.twoDecimalPoint(net_paid_amount));
                    CashCollectionActivity.amountText.setText(Utils.twoDecimalPoint(net_paid_amount));
                    CashCollectionActivity.selectedAmount.setText(Utils.twoDecimalPoint(net_paid_amount));
                    CashCollectionActivity.totalDiscount.setText(Utils.fourDecimalPoint(net_discount));
                    double net_outstand_value=net_outstanding;
                    CashCollectionActivity.totalOutstanding.setText(Utils.twoDecimalPoint(net_outstand_value));


                    // Display the Difference Amount of the Paid Invoice Amount
                    if (!CashCollectionActivity.netAmount.getText().toString().isEmpty()){
                        entered_net_amount=Double.parseDouble(CashCollectionActivity.netAmount.getText().toString());
                    }

                    if (!CashCollectionActivity.selectedAmount.getText().toString().isEmpty()){
                        selected_amount=Double.parseDouble(CashCollectionActivity.selectedAmount.getText().toString());
                    }

                    if (!CashCollectionActivity.differenceAmount.getText().toString().isEmpty()){
                        difference_amount=Double.parseDouble(CashCollectionActivity.differenceAmount.getText().toString());
                    }

                    difference_amount=entered_net_amount-selected_amount;
                    CashCollectionActivity.differenceAmount.setText(Utils.twoDecimalPoint(difference_amount));

                }

            }else {*/
            CashCollectionActivity.totalPaid.setText(Utils.twoDecimalPoint(net_paid_amount_value));
            CashCollectionActivity.amountText.setText(Utils.twoDecimalPoint(net_paid_amount_value));
            CashCollectionActivity.selectedAmount.setText(Utils.twoDecimalPoint(net_paid_amount_value));
            CashCollectionActivity.totalDiscount.setText(Utils.twoDecimalPoint(net_discount));
            CashCollectionActivity.returnAmountEditText.setText(Utils.twoDecimalPoint(debit_amout));
            double net_value_amount=0.0;
            double net_amount=Double.parseDouble(CashCollectionActivity.amountText.getText().toString());
            if (net_amount < 0){
                net_value_amount=Math.abs(net_amount);
            }else {
                net_value_amount=Double.parseDouble(CashCollectionActivity.amountText.getText().toString());
            }
            double net_outstand_value=net_outstanding-Double.parseDouble(CashCollectionActivity.amountText.getText().toString())- Double.parseDouble(CashCollectionActivity.totalDiscount.getText().toString());
            if (net_outstand_value < 0){
                CashCollectionActivity.totalOutstanding.setText("0.00");
            }else {
                CashCollectionActivity.totalOutstanding.setText(Utils.twoDecimalPoint(net_outstand_value));
            }

            // Display the Difference Amount of the Paid Invoice Amount
            if (!CashCollectionActivity.netAmount.getText().toString().isEmpty()){
                entered_net_amount=Double.parseDouble(CashCollectionActivity.netAmount.getText().toString());
            }

            if (!CashCollectionActivity.selectedAmount.getText().toString().isEmpty()){
                selected_amount=Double.parseDouble(CashCollectionActivity.selectedAmount.getText().toString());
            }

            if (!CashCollectionActivity.differenceAmount.getText().toString().isEmpty()){
                difference_amount=Double.parseDouble(CashCollectionActivity.differenceAmount.getText().toString());
            }

            difference_amount=entered_net_amount-selected_amount;
            CashCollectionActivity.differenceAmount.setText(Utils.twoDecimalPoint(difference_amount));
            //   }

        }catch (Exception ex){
        }
    }



    public void setNetTotal1(){
        try {

            double net_value=0.0;
            double net_discount=0.0;
            double net_outstanding=0.0;
            double paid_amount=0.0;

            double entered_net_amount=0.0;
            double difference_amount=0.0;
            double selected_amount=0.0;

            ArrayList<CashCollectionInvoiceModel> list=dbHelper.getAllInvoices();
            for(CashCollectionInvoiceModel model:list){
                net_outstanding+=Double.parseDouble(model.getBalance());
                //if (model.isChecked()){
                net_value+=Double.parseDouble(model.getNetTotal());
                if (!model.getDiscount().isEmpty() && !model.getDiscount().equals(".")){
                    net_discount+=Double.parseDouble(model.getDiscount());
                }
                if (!model.getPayable().isEmpty() && !model.getPayable().equals(".")){
                    paid_amount+=Double.parseDouble(model.getPayable());
                }
                // }
            }

            Log.w("SalesReturnValuea:",SalesReturnAdapter.net_return_value);
            Log.w("NetOutStandinga:", String.valueOf(net_outstanding));

            if (paid_amount < Double.parseDouble(SalesReturnAdapter.net_return_value) ){
                CashInvoiceFragment.refreshReturn();
            }

            if (SalesReturnAdapter.net_return_value!=null && Double.parseDouble(SalesReturnAdapter.net_return_value) > 0){
                double net_paid_amount=paid_amount - Double.parseDouble(SalesReturnAdapter.net_return_value);

                if (net_paid_amount < 0) {
                    CashCollectionActivity.totalPaid.setText("0.00");
                    CashCollectionActivity.amountText.setText("0.00");
                    CashCollectionActivity.selectedAmount.setText("0.00");
                    CashCollectionActivity.totalDiscount.setText(Utils.fourDecimalPoint(net_discount));
                    CashCollectionActivity.totalOutstanding.setText(Utils.twoDecimalPoint(net_outstanding));
                }else {
                    CashCollectionActivity.totalPaid.setText(Utils.twoDecimalPoint(net_paid_amount));
                    CashCollectionActivity.amountText.setText(Utils.twoDecimalPoint(net_paid_amount));
                    CashCollectionActivity.selectedAmount.setText(Utils.twoDecimalPoint(net_paid_amount));
                    CashCollectionActivity.totalDiscount.setText(Utils.fourDecimalPoint(net_discount));
                    double net_outstand_value=net_outstanding;
                    CashCollectionActivity.totalOutstanding.setText(Utils.twoDecimalPoint(net_outstand_value));


                    // Display the Difference Amount of the Paid Invoice Amount
                    if (!CashCollectionActivity.netAmount.getText().toString().isEmpty()){
                        entered_net_amount=Double.parseDouble(CashCollectionActivity.netAmount.getText().toString());
                    }

                    if (!CashCollectionActivity.selectedAmount.getText().toString().isEmpty()){
                        selected_amount=Double.parseDouble(CashCollectionActivity.selectedAmount.getText().toString());
                    }

                    if (!CashCollectionActivity.differenceAmount.getText().toString().isEmpty()){
                        difference_amount=Double.parseDouble(CashCollectionActivity.differenceAmount.getText().toString());
                    }

                    difference_amount=entered_net_amount-selected_amount;
                    CashCollectionActivity.differenceAmount.setText(Utils.twoDecimalPoint(difference_amount));

                }

            }else {
                CashCollectionActivity.totalPaid.setText(Utils.twoDecimalPoint(paid_amount));
                CashCollectionActivity.amountText.setText(Utils.twoDecimalPoint(paid_amount));
                CashCollectionActivity.selectedAmount.setText(Utils.twoDecimalPoint(paid_amount));
                CashCollectionActivity.totalDiscount.setText(Utils.fourDecimalPoint(net_discount));
                CashCollectionActivity.totalOutstanding.setText(Utils.twoDecimalPoint(net_outstanding));

                // Display the Difference Amount of the Paid Invoice Amount
                if (!CashCollectionActivity.netAmount.getText().toString().isEmpty()){
                    entered_net_amount=Double.parseDouble(CashCollectionActivity.netAmount.getText().toString());
                }

                if (!CashCollectionActivity.selectedAmount.getText().toString().isEmpty()){
                    selected_amount=Double.parseDouble(CashCollectionActivity.selectedAmount.getText().toString());
                }

                if (!CashCollectionActivity.differenceAmount.getText().toString().isEmpty()){
                    difference_amount=Double.parseDouble(CashCollectionActivity.differenceAmount.getText().toString());
                }

                difference_amount=entered_net_amount-selected_amount;
                CashCollectionActivity.differenceAmount.setText(Utils.twoDecimalPoint(difference_amount));
            }

        }catch (Exception ex){

        }
    }

    public void setCalculation(CashCollectionInvoiceModel model,ViewHolder viewHolder,String value){
        try {
            double net_total=Double.parseDouble(model.getNetBalance());
            double payable=0.0;
            double discount=0.0;
            double paid_amt=0.0;
            double balance=0.0;
            double discount_amt=0.0;
            if (!value.isEmpty()){
                payable=Double.parseDouble(value);
            }
            if (!viewHolder.discountText.getText().toString().isEmpty()){
                discount=Double.parseDouble(viewHolder.discountText.getText().toString());
            }

            if (!viewHolder.paidAmount.getText().toString().isEmpty()){
                paid_amt=Double.parseDouble(model.getPaidAmount());
            }

            if (model.getDiscountAmount().isEmpty()){
                discount_amt=Double.parseDouble(model.getDiscountAmount());
            }
            Log.w("NeTTotal1:",net_total+"");
            if (model.getTranType().equals("INV")){
                balance=net_total-payable-discount;
            }else {
                balance=net_total+payable-discount;
            }
            viewHolder.balanceAmount.setText(Utils.twoDecimalPoint(balance));
            model.setPayable(viewHolder.payableText.getText().toString());
            if (value.isEmpty()){
                paid_amt=Double.parseDouble(model.getPaidAmount());
            }
            double paid_value=paid_amt+payable;
            if (balance<0){
                // viewHolder.paidAmount.setText(Utils.twoDecimalPoint(Double.parseDouble(viewHolder.payableText.getText().toString())));
            }else {
                //  viewHolder.paidAmount.setText(Utils.twoDecimalPoint(paid_value));
            }

            setNetTotal();

            //    model.setBalance(Utils.twoDecimalPoint(balance));
            //  model.setDiscount(Utils.twoDecimalPoint(discount));
            //  model.setPayable(Utils.twoDecimalPoint(payable));

          /*  if (payable==Double.parseDouble(model.getBalance())){
                viewHolder.discountAmount.setEnabled(false);
            }
*/
        }catch (Exception ex){}
    }

    public void setDiscountCalculation(CashCollectionInvoiceModel model,ViewHolder viewHolder,String value){
        try {
            double net_total=Double.parseDouble(viewHolder.netTotal.getText().toString());
            double payable=0.0;
            double discount=0.0;
            double paid_amt=0.0;
            double dicount_amt=0.0;
            double net_balance=0.0;
            if (!value.isEmpty()){
                discount=Double.parseDouble(value);
            }
            if (!viewHolder.payableText.getText().toString().isEmpty()){
                payable=Double.parseDouble(viewHolder.payableText.getText().toString());
            }

            if (!viewHolder.paidAmount.getText().toString().isEmpty()){
                paid_amt=Double.parseDouble(model.getPaidAmount());
            }

            if (!model.getDiscountAmount().isEmpty()){
                dicount_amt=Double.parseDouble(model.getDiscountAmount());
            }

            if (!viewHolder.netBalanceText.getText().toString().isEmpty()){
                net_balance=Double.parseDouble(viewHolder.netBalanceText.getText().toString());
            }

            Log.w("Discountvalues1:",discount+"");

            double balance=net_balance-discount-payable;

            if (viewHolder.cashChecked.isChecked()) {
                double disc = 0.0;
                double main_bal=0.0;
                double net_pay=0.0;
                if (!value.isEmpty()) {
                    disc = Double.parseDouble(value);
                    main_bal=Double.parseDouble(viewHolder.netBalanceText.getText().toString());
                    Log.w("Main_Balance1:",String.valueOf(main_bal));
                    net_pay=main_bal-disc;
                    viewHolder.balanceAmount.setText("0.0");
                    viewHolder.payableText.removeTextChangedListener(payableTextWatcher);
                    viewHolder.payableText.setText(Utils.twoDecimalPoint(net_pay));
                    viewHolder.payableText.addTextChangedListener(payableTextWatcher);
                    model.setPayable(viewHolder.payableText.getText().toString());
                    model.setDiscount(value);
                    if (model.getTranType().equals("INV") && Double.parseDouble(viewHolder.payableText.getText().toString()) < 0){
                        Toast.makeText(context, "Balance exceed", Toast.LENGTH_SHORT).show();
                        viewHolder.discountText.removeTextChangedListener(discountTextWatcher);
                        viewHolder.payableText.setText("");
                        model.setPayable("");
                        viewHolder.discountText.setText("");
                        viewHolder.balanceAmount.setText(model.getNetBalance());
                        // viewHolder.discountText.setSelection(viewHolder.discountText.getText().length());
                        viewHolder.discountText.addTextChangedListener(discountTextWatcher);
                        model.setDiscount("0.00");
                        Log.w("ValuesExceed:",viewHolder.discountText.getText().toString());
                    }
                } else {
                    model.setDiscount("0.00");
                    model.setBalance("0.00");
                    viewHolder.balanceAmount.setText("0.0");
                    main_bal=Double.parseDouble(viewHolder.netBalanceText.getText().toString());
                    viewHolder.payableText.removeTextChangedListener(payableTextWatcher);
                    viewHolder.payableText.setText(Utils.twoDecimalPoint(main_bal));
                    viewHolder.payableText.addTextChangedListener(payableTextWatcher);
                    model.setPayable(viewHolder.payableText.getText().toString());
                }
            } else {
                if (model.getTranType().equals("INV")){
                    Log.w("TestDailog1:",model.getDiscount()+"");
                    if (balance < 0){
                        model.setDiscount("0.0");
                        viewHolder.discountText.setText("");
                        balance=net_balance-payable;
                        viewHolder.balanceAmount.setText(Utils.twoDecimalPoint(balance));
                        Toast.makeText(context,"Balance exceed",Toast.LENGTH_SHORT).show();
                    }else {
                        model.setPayable(viewHolder.payableText.getText().toString());
                        model.setDiscount(viewHolder.discountText.getText().toString());
                        viewHolder.balanceAmount.setText(Utils.twoDecimalPoint(balance));
                    }
                }else {
                    Log.w("TestDailog2:",model.getDiscount()+"");
                }
            }
            setNetTotal();
        }catch (Exception ex){

        }
    }

    @Override
    public int getItemCount() {
        return cashCollectionInvoices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private CheckBox cashChecked;
        private TextView invoiceNumber;
        private TextView netTotal;
        private TextView balanceAmount;
        public  EditText payableText;
        public  EditText discountText;
        public TextView invoiceDate;
        public TextView paidAmount;
        public TextView discountAmount;
        public LinearLayout mainLayout;
        public TextView netBalanceText;
        public TextView tranType;

        public ViewHolder(View view) {
            super(view);
            cashChecked=view.findViewById(R.id.cash_check);
            invoiceNumber=view.findViewById(R.id.sr_no);
            netTotal=view.findViewById(R.id.net_total);
            balanceAmount=view.findViewById(R.id.balance);
            payableText=view.findViewById(R.id.payable);
            invoiceDate=view.findViewById(R.id.sr_date);
            discountText=view.findViewById(R.id.discount);
            paidAmount=view.findViewById(R.id.paid_amount);
            discountAmount=view.findViewById(R.id.discount_amount);
            mainLayout=view.findViewById(R.id.main_layout);
            netBalanceText=view.findViewById(R.id.net_balance);
            tranType=view.findViewById(R.id.tran_type);
            discountText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            payableText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface onRefreshData{
        void refresh();
    }

    public interface CallBack{
        void setPaidTotal(double nettotal,double discount,double netoutstanding);
        void refreshData();
    }

    public void filterList(ArrayList<CashCollectionInvoiceModel> filterdNames) {
        this.cashCollectionInvoices = filterdNames;
        notifyDataSetChanged();
    }

    public ArrayList<CashCollectionInvoiceModel> getList(){
        return this.cashCollectionInvoices;
    }

}