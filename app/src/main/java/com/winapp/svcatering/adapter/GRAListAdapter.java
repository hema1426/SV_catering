package com.winapp.svcatering.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.GRAListModel;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;


public class GRAListAdapter extends RecyclerView.Adapter<GRAListAdapter.ViewHolder> {
    private ArrayList<GRAListModel> graList;
    private Context context;
    private GRAOnClickListener onGRAListener;
    public GRAListAdapter(Context context, ArrayList<GRAListModel> graList, GRAOnClickListener graListener) {
        this.graList = graList;
        this.context=context;
        this.onGRAListener = graListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gra_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        try {
            GRAListModel model= graList.get(i);
            viewHolder.supplierName.setText(model.getSupplierName());
            viewHolder.graNumber.setText(model.getGraNumber());
            viewHolder.netTotal.setText("$ "+ Utils.twoDecimalPoint(Double.parseDouble(model.getNetTotal())));
            viewHolder.locationCode.setText(model.getLocationCode());
            viewHolder.date.setText(model.getGraDate());

            viewHolder.editGRA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGRAListener.onGRAClick(model);
                }
            });
        }catch (Exception exception){
            Log.w("DashboardAdapterError:",exception.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return graList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView graNumber;
        private TextView supplierName;
        private TextView date;
        private TextView netTotal;
        private TextView locationCode;
        private ImageView editGRA;
        private TextView status;
        private LinearLayout rootLayout;
        public ViewHolder(View view) {
            super(view);
            supplierName =view.findViewById(R.id.supplier_name);
            graNumber =view.findViewById(R.id.gra_number);
            date =view.findViewById(R.id.date);
            netTotal =view.findViewById(R.id.net_total);
            locationCode =view.findViewById(R.id.location_code);
            editGRA=view.findViewById(R.id.edit_gra);
           // status=view.findViewById(R.id.status);
            //rootLayout=view.findViewById(R.id.rootlayout);
        }
    }

    public void filterList(ArrayList<GRAListModel> filterdNames) {
        graList = filterdNames;
        notifyDataSetChanged();
    }

    public interface GRAOnClickListener {
        void onGRAClick(GRAListModel model);
    }
}
