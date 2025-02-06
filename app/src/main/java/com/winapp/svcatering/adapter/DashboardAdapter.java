package com.winapp.svcatering.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.NewInvoiceListActivity;
import com.winapp.svcatering.receipts.ReceiptsListActivity;
import com.winapp.svcatering.activity.SalesOrderListActivity;
import com.winapp.svcatering.activity.SchedulingActivity;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.salesreturn.SalesReturnActivity;

import java.util.ArrayList;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
    private ArrayList<String> letters;
    private Context context;
    private DBHelper dbHelper;
    View view;
    public DashboardAdapter(FragmentActivity activity, ArrayList<String> letters) {
        this.letters = letters;
        this.context=activity;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dashboard_items, viewGroup, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.tv_letters.setText(letters.get(i).trim());
        dbHelper=new DBHelper(context);
        viewHolder.dashboardItem.setTag(letters.get(i).trim());
      /*  ArrayList<UserRoll> userRolls=dbHelper.getUserPermissions();
        if (userRolls.size()>0) {
            for (UserRoll roll : userRolls) {
                if (roll.getFormName().equals(viewHolder.dashboardItem.getTag().toString())) {
                    if (roll.getHavePermission().equals("true")) {
                        viewHolder.dashboardItem.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.dashboardItem.setVisibility(View.GONE);
                    }
                }
            }
        }*/

        if (i==0){
            viewHolder.thumbnail.setImageResource(R.drawable.catalog);
        }else if (i==1){
            viewHolder.thumbnail.setImageResource(R.drawable.sales);
        }else if (i==2){
            viewHolder.thumbnail.setImageResource(R.drawable.invoice);
        }else if (i==3){
            viewHolder.thumbnail.setImageResource(R.drawable.receipt);
        }else if (i==4){
            viewHolder.thumbnail.setImageResource(R.drawable.sales_return);
        }else if (i==5){
            viewHolder.thumbnail.setImageResource(R.drawable.product);
        }else if (i==6){
            viewHolder.thumbnail.setImageResource(R.drawable.goods_receive);
        }else if (i==7){
            viewHolder.thumbnail.setImageResource(R.drawable.settings);
        }

        viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i==0){
                    Intent intent=new Intent(context, SchedulingActivity.class);
                    context.startActivity(intent);
                }else if (i==1){
                    Intent intent=new Intent(context, SalesOrderListActivity.class);
                    context.startActivity(intent);
                }else if (i==2){
                    Intent intent=new Intent(context, NewInvoiceListActivity.class);
                    context.startActivity(intent);
                }else if (i==3){
                    Intent intent=new Intent(context, ReceiptsListActivity.class);
                    context.startActivity(intent);
                }else if (i==4){
                    Intent intent=new Intent(context, SalesReturnActivity.class);
                    context.startActivity(intent);
                }
            }
        });

        viewHolder.dashboardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i==0){
                    Intent intent=new Intent(context, SchedulingActivity.class);
                    context.startActivity(intent);
                }else if (i==1){
                    Intent intent=new Intent(context, SalesOrderListActivity.class);
                    context.startActivity(intent);
                }else if (i==2){
                    Intent intent=new Intent(context, NewInvoiceListActivity.class);
                    context.startActivity(intent);
                }else if (i==3){

                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return letters.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_letters;
        private ImageView thumbnail;
        private LinearLayout dashboardItem;
        public ViewHolder(View view) {
            super(view);
            tv_letters =view.findViewById(R.id.name);
            thumbnail=view.findViewById(R.id.thumbnail);
            dashboardItem=view.findViewById(R.id.dashboard_item);
        }
    }

}