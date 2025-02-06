
package com.winapp.svcatering.adapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.winapp.svcatering.R;
import com.winapp.svcatering.model.ExpenseModel;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private ArrayList<ExpenseModel> currency;
    public CallBack callBack;
    private int selectedPosition = -1;// no selection by default

    public ExpenseAdapter(ArrayList<ExpenseModel> expenses, CallBack callBack) {
        this.currency = expenses;
        this.callBack=callBack;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.expense_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ExpenseModel model=currency.get(position);
        viewHolder.expense.setText(model.getExpenseName());
        viewHolder.sno.setText(String.valueOf(position+1));
        if (model.getExpenseTotal()!=null && !model.getExpenseTotal().isEmpty() && !model.getExpenseTotal().equals("null")){
            if (Double.parseDouble(model.getExpenseTotal()) > 0){
                viewHolder.amount.setText(model.getExpenseTotal());
            }else {
                viewHolder.amount.setText("");
            }
        }else {
            viewHolder.amount.setText("");
        }
        viewHolder.amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    callBack.sortKeyboard(viewHolder.amount);
                } else {

                }
            }
        });

        viewHolder.amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals(".")) {
                    viewHolder.amount.setText("0.");
                    viewHolder.amount.setSelection(viewHolder.amount.getText().length());
                } else {
                    if (!editable.toString().isEmpty()) {
                        double count = Double.parseDouble(editable.toString());
                        model.setExpenseTotal(String.valueOf(count));
                    } else {
                        model.setExpenseTotal("0.00");
                    }
                    callBack.setExpenseTotal();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return currency.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView expense;
        private EditText amount;
        private TextView sno;
        public ViewHolder(View view) {
            super(view);
            expense = view.findViewById(R.id.expense);
            amount =view.findViewById(R.id.amount);
            sno =view.findViewById(R.id.sno);
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
        void sortKeyboard(EditText editText);
        void setExpenseTotal();
    }

    public void resetPosition(){
        selectedPosition=-1;
        notifyDataSetChanged();
    }
}