
package com.winapp.svcatering.adapter;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.winapp.svcatering.R;
import com.winapp.svcatering.model.CurrencyModel;
import com.winapp.svcatering.printpreview.SettlementPrintPreview;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;

public class CurrencyDenominationAdapter extends RecyclerView.Adapter<CurrencyDenominationAdapter.ViewHolder> {
    private ArrayList<CurrencyModel> currency;
    public CallBack callBack;
    private int selectedPosition = -1;// no selection by default
    private Context context;

    public CurrencyDenominationAdapter(Context context,ArrayList<CurrencyModel> currency, CallBack callBack) {
        this.currency = currency;
        this.callBack=callBack;
        this.context=context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.denomination_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        try {
            CurrencyModel model=currency.get(position);
            viewHolder.currency.setText(model.getCurrencyName());
            viewHolder.count.setText(model.getCount());
            viewHolder.currencyCount.setText(model.getCount());
            viewHolder.total.setText(Utils.twoDecimalPoint(Double.parseDouble(model.getTotal())));
            viewHolder.serialNumber.setText(String.valueOf(position+1));
            if (context instanceof SettlementPrintPreview){
                viewHolder.count.setVisibility(View.GONE);
                viewHolder.serialNumber.setVisibility(View.VISIBLE);
                viewHolder.currencyCount.setVisibility(View.VISIBLE);
            }else {
                viewHolder.count.setVisibility(View.VISIBLE);
                viewHolder.serialNumber.setVisibility(View.GONE);
                viewHolder.currencyCount.setVisibility(View.GONE);
            }
            viewHolder.count.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        callBack.setKeyboard(viewHolder.count);
                    } else {

                    }
                }
            });

            viewHolder.count.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }
                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        if (!editable.toString().isEmpty()){
                            double count=Double.parseDouble(editable.toString());
                            double currency= Double.parseDouble(viewHolder.currency.getText().toString());
                            double total = count * currency;
                            viewHolder.total.setText(Utils.twoDecimalPoint(total));
                            model.setTotal(String.valueOf(total));
                            model.setCount(String.valueOf(count));
                            callBack.setCurrencyTotal();
                        }else {
                            model.setTotal("0.00");
                            viewHolder.total.setText("0.00");
                            model.setCount("");
                            callBack.setCurrencyTotal();
                        }
                    }catch (Exception exception){}
                }
            });
        }catch (Exception exception){}
    }

    @Override
    public int getItemCount() {
        return currency.size();
    }

    public ArrayList<CurrencyModel> getCurrencyList(){
        return currency;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView currency;
        private TextView total;
        private EditText count;
        private TextView currencyCount;
        private TextView serialNumber;
        public ViewHolder(View view) {
            super(view);
            currency = view.findViewById(R.id.currency);
            total=view.findViewById(R.id.total);
            count=view.findViewById(R.id.count);
            currencyCount=view.findViewById(R.id.currency_count);
            serialNumber=view.findViewById(R.id.sno);
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

    public interface CallBack{
        void setKeyboard(EditText count);
        void setCurrencyTotal();
    }

    public void resetPosition(){
        selectedPosition=-1;
        notifyDataSetChanged();
    }
}