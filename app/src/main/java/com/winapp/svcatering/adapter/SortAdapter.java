package com.winapp.svcatering.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;

import java.util.ArrayList;

public class SortAdapter extends RecyclerView.Adapter<SortAdapter.ViewHolder> {
    private ArrayList<String> letters;
    public CallBack callBack;
    private int selectedPosition = -1;// no selection by default

    public SortAdapter(ArrayList<String> countries,CallBack callBack) {
        this.letters = countries;
        this.callBack=callBack;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.tv_letters.setText(letters.get(position));

        if (selectedPosition == position) {
            viewHolder.itemView.setSelected(true);
            viewHolder.selectLayout.setBackgroundResource(R.drawable.circle);
            viewHolder.tv_letters.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            viewHolder.itemView.setSelected(false);
            viewHolder.selectLayout.setBackgroundResource(R.drawable.circle_not_selected);
            viewHolder.tv_letters.setTextColor(Color.parseColor("#212121"));
        }

        viewHolder.selectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition >= 0)
                    notifyItemChanged(selectedPosition);
                    selectedPosition = viewHolder.getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                    callBack.sortProduct(viewHolder.tv_letters.getText().toString());
            }
        });

    /*    viewHolder.tv_letters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition >= 0)
                    notifyItemChanged(selectedPosition);
                    selectedPosition = viewHolder.getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                    callBack.sortProduct(viewHolder.tv_letters.getText().toString());
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return letters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_letters;
        private LinearLayout selectLayout;
        public ViewHolder(View view) {
            super(view);
            tv_letters = view.findViewById(R.id.tv_sorting);
            selectLayout=view.findViewById(R.id.select_layout);
        }
    }

    public interface CallBack{
        void sortProduct(String letter);
    }

    public void resetPosition(){
        selectedPosition=-1;
        notifyDataSetChanged();
    }
}