package com.winapp.svcatering.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

public class CashCollectionInvoiceAdapter extends RecyclerView.Adapter<CashCollectionInvoiceAdapter.ViewHolder> {

    private static ArrayList<CashCollectionInvoiceModel> cashCollectionInvoices;
    public CallBack callBack;
    public static TextWatcher payableTextWatcher;
    public static TextWatcher discountTextWatcher;
    public Context context;
    public onRefreshData onRefreshData;
    public static DBHelper dbHelper;

    public CashCollectionInvoiceAdapter(Context context,ArrayList<CashCollectionInvoiceModel> invoices, CallBack callBack) {
        this.cashCollectionInvoices = invoices;
        this.callBack=callBack;
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cash_collection_invoice_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        CashCollectionInvoiceModel model= cashCollectionInvoices.get(position);
        dbHelper=new DBHelper(context);
        viewHolder.invoiceNumber.setText(model.getInvoiceNumber());

        viewHolder.payableText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        viewHolder.discountText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

        viewHolder.netTotal.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));
        if (model.getPayable()!=null && !model.getPayable().isEmpty()){
            viewHolder.payableText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getPayable())));
        }
        if (model.getDiscount()!=null && !model.getDiscount().isEmpty()){
            viewHolder.discountText.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getDiscount())));
        }
        viewHolder.balanceAmount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBalance())));

      /*  if (!model.isDisabledDiscount()){
            viewHolder.discountText.setEnabled(false);
        }else {
            viewHolder.discountText.setEnabled(true);
        }*/

      if (model.getChecked().equals("true")){
          viewHolder.cashChecked.setChecked(true);
      }else {
          viewHolder.cashChecked.setChecked(false);
       }

        viewHolder.invoiceDate.setText(model.getInvoiceDate());
        viewHolder.paidAmount.setText(model.getPaidAmount());
        viewHolder.discountAmount.setText(model.getDiscountAmount());
        viewHolder.payableText.setSelectAllOnFocus(true);
        viewHolder.discountText.setSelectAllOnFocus(true);

        payableTextWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (editable.toString().equals("-")){
                        viewHolder.payableText.setText("");
                    }
                    setCalculation(model, viewHolder, editable.toString());
                    if (!editable.toString().isEmpty() && !editable.toString().equals(".")){
                        CashInvoiceFragment.amountCalculation(Double.parseDouble(editable.toString()));
                    }
                    if (!editable.toString().isEmpty() && !editable.toString().equals("0.00") && !editable.toString().equals("0.0") && !editable.toString().equals("0")) {
                        // String invno,String indate,String nettotal,String payable,String discount,String bal,boolean checked
                        dbHelper.insertCashcollection(
                                model.getCustomerCode(),
                                viewHolder.invoiceNumber.getText().toString(),
                                viewHolder.invoiceDate.getText().toString(),
                                viewHolder.netTotal.getText().toString(),
                                viewHolder.payableText.getText().toString(),
                                viewHolder.discountText.getText().toString(),
                                viewHolder.balanceAmount.getText().toString(),
                                "",
                                viewHolder.paidAmount.getText().toString(),
                                viewHolder.discountAmount.getText().toString(),
                                "true"
                        );
                        setNetTotal();
                    }else {
                        dbHelper.updateInvoice(
                                viewHolder.invoiceNumber.getText().toString(),
                                "",
                                viewHolder.discountText.getText().toString(),
                                viewHolder.balanceAmount.getText().toString(),
                                "true");
                        setNetTotal();
                    }
                }catch (Exception ec){}
            }
        };
        viewHolder.payableText.addTextChangedListener(payableTextWatcher);



    /*    viewHolder.payableText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                viewHolder.payableText.addTextChangedListener(payableTextWatcher);
                return true;
            }
        });*/

       /* viewHolder.payableText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setCalculation(model, viewHolder, editable.toString());
                if (!editable.toString().isEmpty()){
                    CashInvoiceFragment.amountCalculation(Double.parseDouble(editable.toString()));
                }
                if (!editable.toString().isEmpty() && !editable.toString().equals("0.00") && !editable.toString().equals("0.0") && !editable.toString().equals("0")) {
                   // String invno,String indate,String nettotal,String payable,String discount,String bal,boolean checked
                    dbHelper.insertCashcollection(
                            model.getCustomerCode(),
                            viewHolder.invoiceNumber.getText().toString(),
                            viewHolder.invoiceDate.getText().toString(),
                            viewHolder.netTotal.getText().toString(),
                            viewHolder.payableText.getText().toString(),
                            viewHolder.discountText.getText().toString(),
                            viewHolder.balanceAmount.getText().toString(),
                            viewHolder.paidAmount.getText().toString(),
                            viewHolder.discountAmount.getText().toString(),
                            viewHolder.cashChecked.isChecked()
                    );
                    setNetTotal();
                }else {
                    dbHelper.updateInvoice(
                            viewHolder.invoiceNumber.getText().toString(),
                            "",
                            viewHolder.discountText.getText().toString(),
                            viewHolder.balanceAmount.getText().toString());
                    setNetTotal();
                }
            }
        });*/

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
               if (editable.toString().equals("-")){
                   viewHolder.discountText.setText("");
               }
               if (!editable.toString().isEmpty() && !editable.toString().equals("0.00") && !editable.toString().equals("0.0") && !editable.toString().equals("0")) {
                   setDiscountCalculation(model,viewHolder,viewHolder.discountText.getText().toString());
                   dbHelper.insertCashcollection(
                           model.getCustomerCode(),
                           viewHolder.invoiceNumber.getText().toString(),
                           viewHolder.invoiceDate.getText().toString(),
                           viewHolder.netTotal.getText().toString(),
                           viewHolder.payableText.getText().toString(),
                           viewHolder.discountText.getText().toString(),
                           viewHolder.balanceAmount.getText().toString(),
                           "",
                           viewHolder.paidAmount.getText().toString(),
                           viewHolder.discountAmount.getText().toString(),
                           "true");
                   setNetTotal();
               }else {
                   setDiscountCalculation(model,viewHolder,viewHolder.discountText.getText().toString());
                   dbHelper.updateInvoice(
                           viewHolder.invoiceNumber.getText().toString(),
                           viewHolder.payableText.getText().toString(),
                           "",
                           viewHolder.balanceAmount.getText().toString(),
                           "true");
                   setNetTotal();
               }
           }
       };
       viewHolder.discountText.addTextChangedListener(discountTextWatcher);


      /*  viewHolder.discountText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
               // CashInvoiceFragment.amountCalculation();
                if (!editable.toString().isEmpty() && !editable.toString().equals("0.00") && !editable.toString().equals("0.0") && !editable.toString().equals("0")) {
                    setDiscountCalculation(model,viewHolder,viewHolder.discountText.getText().toString());
                    dbHelper.insertCashcollection(
                            model.getCustomerCode(),
                            viewHolder.invoiceNumber.getText().toString(),
                            viewHolder.invoiceDate.getText().toString(),
                            viewHolder.netTotal.getText().toString(),
                            viewHolder.payableText.getText().toString(),
                            viewHolder.discountText.getText().toString(),
                            viewHolder.balanceAmount.getText().toString(),
                            viewHolder.paidAmount.getText().toString(),
                            viewHolder.discountAmount.getText().toString(),
                            viewHolder.cashChecked.isChecked()
                    );
                    setNetTotal();
                }else {
                    setDiscountCalculation(model,viewHolder,viewHolder.discountText.getText().toString());
                    dbHelper.updateInvoice(
                            viewHolder.invoiceNumber.getText().toString(),
                            viewHolder.payableText.getText().toString(),
                            "",
                            viewHolder.balanceAmount.getText().toString());
                    setNetTotal();
                }
            }
        });*/


        viewHolder.cashChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.cashChecked.isChecked()){
                    model.setChecked("true");
                    double paid_amt=0.0;
                    double net_amt=0.0;
                    double balance=0.0;
                    double discount=0.0;
                    double bal=0.0;
                  //  viewHolder.discountText.setEnabled(false);
                    if (!viewHolder.paidAmount.getText().toString().isEmpty()){
                        paid_amt=Double.parseDouble(viewHolder.paidAmount.getText().toString());
                    }
                    if (!viewHolder.netTotal.getText().toString().isEmpty()){
                        net_amt=Double.parseDouble(viewHolder.netTotal.getText().toString());
                    }

                    if (!viewHolder.paidAmount.getText().toString().isEmpty()){
                        bal=Double.parseDouble(viewHolder.paidAmount.getText().toString());
                    }

                    if (!viewHolder.discountText.getText().toString().isEmpty()){
                        discount=Double.parseDouble(viewHolder.discountText.getText().toString());
                    }
                    balance=bal-discount;
                    model.setPayable(String.valueOf(balance));
                    viewHolder.payableText.setText(Utils.twoDecimalPoint(balance));
                    viewHolder.balanceAmount.setText("0.00");
                    dbHelper.insertCashcollection(
                            model.getCustomerCode(),
                            viewHolder.invoiceNumber.getText().toString(),
                            viewHolder.invoiceDate.getText().toString(),
                            viewHolder.netTotal.getText().toString(),
                            viewHolder.payableText.getText().toString(),
                            viewHolder.discountText.getText().toString(),
                            viewHolder.balanceAmount.getText().toString(),
                            "",
                            viewHolder.paidAmount.getText().toString(),
                            viewHolder.discountAmount.getText().toString(),
                            "true"
                    );
                    setNetTotal();
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
                    double bal=0.0;
                    if (!viewHolder.paidAmount.getText().toString().isEmpty()){
                        paid_amt=Double.parseDouble(viewHolder.paidAmount.getText().toString());
                    }
                    if (!viewHolder.netTotal.getText().toString().isEmpty()){
                        net_amt=Double.parseDouble(viewHolder.netTotal.getText().toString());
                    }

                    if (!viewHolder.paidAmount.getText().toString().isEmpty()){
                        bal=Double.parseDouble(viewHolder.paidAmount.getText().toString());
                    }

                    if (!viewHolder.discountText.getText().toString().isEmpty()){
                        discount=Double.parseDouble(viewHolder.discountText.getText().toString());
                    }
                    balance=bal-discount;
                    viewHolder.balanceAmount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBalance())));
                  //  dbHelper.deleteInvoice(viewHolder.invoiceNumber.getText().toString());
                    dbHelper.updateInvoice(
                            viewHolder.invoiceNumber.getText().toString(),
                            viewHolder.payableText.getText().toString(),
                            "",
                            viewHolder.balanceAmount.getText().toString(),
                            "false");
                    setNetTotal();
                }
            }
        });
        setNetTotal();
        if (!viewHolder.payableText.getText().toString().isEmpty() && !viewHolder.payableText.getText().toString().equals("0.00") && !viewHolder.payableText.getText().toString().equals("0.0") && !viewHolder.payableText.getText().toString().equals("0")) {
            setCalculation(model,viewHolder,viewHolder.payableText.getText().toString());
        }
        if (!viewHolder.discountText.getText().toString().isEmpty() && !viewHolder.discountText.getText().toString().equals("0.00") && !viewHolder.discountText.getText().toString().equals("0.0") && !viewHolder.discountText.getText().toString().equals("0")) {
            setDiscountCalculation(model,viewHolder,viewHolder.discountText.getText().toString());
        }

    /*    if (position % 2==1){
            viewHolder.mainLayout.setBackgroundColor(Color.parseColor("#f5f5f5"));
        }*/
    }

    public void setNetTotal(){
        try {

         /*   int count=0;
            for (CashCollectionInvoiceModel m:cashCollectionInvoices){
                if (m.getChecked().equals("true")){
                    count++;
                }
            }

            Log.w("Count:",String.valueOf(count));
            if (count==0){
                CashInvoiceFragment fragment=new CashInvoiceFragment(cashCollectionInvoices.get(0).getCustomerCode());
                fragment.checkAll("uncheck");
            }
*/
            double net_value=0.0;
            double net_discount=0.0;
            double net_outstanding=0.0;
            double paid_amount=0.0;
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

            Log.w("SalesReturnValue:",SalesReturnAdapter.net_return_value);
            Log.w("NetOutStanding:", String.valueOf(net_outstanding));

            if (paid_amount < Double.parseDouble(SalesReturnAdapter.net_return_value) ){
              //  CashInvoiceFragment.refreshReturn();
            }

            if (SalesReturnAdapter.net_return_value!=null && Double.parseDouble(SalesReturnAdapter.net_return_value) > 0){
                double net_paid_amount=paid_amount - Double.parseDouble(SalesReturnAdapter.net_return_value);
                if (net_paid_amount < 0) {
                    CashCollectionActivity.totalPaid.setText("0.00");
                    CashCollectionActivity.amountText.setText("0.00");
                    CashCollectionActivity.totalDiscount.setText(Utils.fourDecimalPoint(net_discount));
                    CashCollectionActivity.totalOutstanding.setText(Utils.twoDecimalPoint(net_outstanding));
                }else {
                    CashCollectionActivity.totalPaid.setText(Utils.twoDecimalPoint(net_paid_amount));
                    CashCollectionActivity.amountText.setText(Utils.twoDecimalPoint(net_paid_amount));
                    CashCollectionActivity.totalDiscount.setText(Utils.fourDecimalPoint(net_discount));
                    double net_outstand_value=net_outstanding;
                    CashCollectionActivity.totalOutstanding.setText(Utils.twoDecimalPoint(net_outstand_value));
                }

            }else {
                CashCollectionActivity.totalPaid.setText(Utils.twoDecimalPoint(paid_amount));
                CashCollectionActivity.amountText.setText(Utils.twoDecimalPoint(paid_amount));
                CashCollectionActivity.totalDiscount.setText(Utils.fourDecimalPoint(net_discount));
                CashCollectionActivity.totalOutstanding.setText(Utils.twoDecimalPoint(net_outstanding));
            }

         /*   CashCollectionActivity.totalPaid.setText(Utils.twoDecimalPoint(paid_amount));
            CashCollectionActivity.amountText.setText(Utils.twoDecimalPoint(paid_amount));
            CashCollectionActivity.totalDiscount.setText(Utils.fourDecimalPoint(net_discount));
            CashCollectionActivity.totalOutstanding.setText(Utils.twoDecimalPoint(net_outstanding));
            if (paid_amount==0.00){
                CashCollectionActivity.returnAmountEditText.setText("0.00");
                CashInvoiceFragment cashInvoiceFragment=new CashInvoiceFragment();
                cashInvoiceFragment.resetValues();
            }else if (paid_amount < Double.parseDouble(SalesReturnAdapter.net_return_value)){
                CashCollectionActivity.returnAmountEditText.setText("0.00");
            }*/

        }catch (Exception ex){

        }
    }

    public void setCalculation(CashCollectionInvoiceModel model,ViewHolder viewHolder,String value){
        try {
            double net_total=Double.parseDouble(viewHolder.netTotal.getText().toString());
            double payable=0.0;
            double discount=0.0;
            double paid_amt=0.0;
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

            double balance=net_total-payable-discount-paid_amt-discount_amt;

            if (balance<0){
                Toast.makeText(context,"Balance exceed",Toast.LENGTH_SHORT).show();
                viewHolder.payableText.setText(viewHolder.payableText.getText().toString().substring(0, viewHolder.payableText.length() - 1));
                viewHolder.payableText.setSelection(viewHolder.payableText.getText().length());
                viewHolder.balanceAmount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBalance())));
            }else {
                viewHolder.balanceAmount.setText(Utils.twoDecimalPoint(balance));
            }

            if (value.isEmpty()){
                paid_amt=Double.parseDouble(model.getPaidAmount());
            }
            double paid_value=paid_amt+payable;
            if (balance<0){
                viewHolder.paidAmount.setText(Utils.twoDecimalPoint(Double.parseDouble(viewHolder.payableText.getText().toString())));
            }else {
                viewHolder.paidAmount.setText(Utils.twoDecimalPoint(paid_value));
            }

            model.setBalance(Utils.twoDecimalPoint(balance));
            model.setDiscount(Utils.twoDecimalPoint(discount));
            model.setPayable(Utils.twoDecimalPoint(payable));

            if (payable==Double.parseDouble(model.getBalance())){
                viewHolder.discountAmount.setEnabled(false);
            }

        }catch (Exception ex){}
    }

    public void setDiscountCalculation(CashCollectionInvoiceModel model,ViewHolder viewHolder,String value){

        viewHolder.payableText.removeTextChangedListener(payableTextWatcher);

        try {
            double net_total=Double.parseDouble(viewHolder.netTotal.getText().toString());
            double payable=0.0;
            double discount=0.0;
            double paid_amt=0.0;
            double dicount_amt=0.0;
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

            double balance=net_total-payable-discount-paid_amt-dicount_amt;

            if (viewHolder.cashChecked.isChecked()) {
                double disc = 0.0;
                double main_bal=0.0;
                double net_pay=0.0;
                if (!value.isEmpty()) {
                    disc = Double.parseDouble(value);
                    main_bal=Double.parseDouble(viewHolder.netTotal.getText().toString());
                    Log.w("Main_Balance:",String.valueOf(main_bal));
                    net_pay=main_bal-disc;
                    viewHolder.balanceAmount.setText("0.0");
                    viewHolder.payableText.setText(Utils.twoDecimalPoint(net_pay));
                    if (Double.parseDouble(viewHolder.payableText.getText().toString()) < 0){
                        Toast.makeText(context, "Balance exceed", Toast.LENGTH_SHORT).show();
                        viewHolder.discountText.setText(viewHolder.discountText.getText().toString().substring(0, viewHolder.discountText.length() - 1));
                        viewHolder.discountText.setSelection(viewHolder.discountText.getText().length());
                    }
                } else {
                    viewHolder.balanceAmount.setText("0.0");
                    main_bal=Double.parseDouble(viewHolder.netTotal.getText().toString());
                    viewHolder.payableText.setText(Utils.twoDecimalPoint(main_bal));
                }
            } else {
              if (balance < 0) {
                        Toast.makeText(context, "Balance exceed", Toast.LENGTH_SHORT).show();
                        viewHolder.discountText.setText(viewHolder.discountText.getText().toString().substring(0, viewHolder.discountText.length() - 1));
                        viewHolder.discountText.setSelection(viewHolder.discountText.getText().length());
                        viewHolder.balanceAmount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBalance())));
                    } else {
                        viewHolder.balanceAmount.setText(Utils.twoDecimalPoint(balance));
                    }

                    if (value.isEmpty()) {
                        dicount_amt = Double.parseDouble(model.getDiscountAmount());
                    }
                    double net_discount = discount + dicount_amt;
                    if (balance < 0) {
                        viewHolder.discountAmount.setText(Utils.twoDecimalPoint(Double.parseDouble(viewHolder.discountText.getText().toString())));
                    } else {
                        viewHolder.discountAmount.setText(Utils.twoDecimalPoint(net_discount));
                    }
                }
            model.setBalance(Utils.twoDecimalPoint(balance));
            model.setDiscount(Utils.twoDecimalPoint(discount));
            model.setPayable(Utils.twoDecimalPoint(balance));
        }catch (Exception ex){

        }

      /*  if (viewHolder.payableText.getText().toString().isEmpty()){
            double dis=0.0;
            if (!value.isEmpty()){
                dis=Double.parseDouble(value);
            }
            double bal=Double.parseDouble(model.getBalance())-dis;
            viewHolder.payableText.setText(Utils.twoDecimalPoint(bal));
            viewHolder.balanceAmount.setText("0.00");
        }else {
            try {
                double net_total=Double.parseDouble(viewHolder.netTotal.getText().toString());
                double payable=0.0;
                double discount=0.0;
                double paid_amt=0.0;
                double dicount_amt=0.0;
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

                double balance=net_total-payable-discount-paid_amt-dicount_amt;

                if (balance<0){
                    Toast.makeText(context,"Balance exceed",Toast.LENGTH_SHORT).show();
                    viewHolder.discountText.setText(viewHolder.discountText.getText().toString().substring(0, viewHolder.discountText.length() - 1));
                    viewHolder.discountText.setSelection(viewHolder.discountText.getText().length());
                    viewHolder.balanceAmount.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getBalance())));
                }else {
                    viewHolder.balanceAmount.setText(Utils.twoDecimalPoint(balance));
                }

                if (value.isEmpty()){
                    dicount_amt=Double.parseDouble(model.getDiscountAmount());
                }
                double net_discount=discount+dicount_amt;
                if (balance<0){
                    viewHolder.discountAmount.setText(Utils.twoDecimalPoint(Double.parseDouble(viewHolder.discountText.getText().toString())));
                }else {
                    viewHolder.discountAmount.setText(Utils.twoDecimalPoint(net_discount));
                }

                model.setBalance(Utils.twoDecimalPoint(balance));
                model.setDiscount(Utils.twoDecimalPoint(discount));
                model.setPayable(Utils.twoDecimalPoint(balance));

            }catch (Exception ex){

            }
        }*/
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
            discountText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            payableText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

        }
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