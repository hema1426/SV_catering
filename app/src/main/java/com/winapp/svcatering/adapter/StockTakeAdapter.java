package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.StockTakeModel;
import com.winapp.svcatering.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class StockTakeAdapter extends RecyclerView.Adapter<StockTakeAdapter.TransferViewHolder> implements Filterable {
    /**
     * Declare the Context and Arraylist variables
     */
    private Context mContext;
    private ArrayList<StockTakeModel> transferList;
    private ArrayList<StockTakeModel> transferListFilter;
    private SharedPreferences sharedpreferences;
    private int mContainerId;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private final static String TAG = "DashBoardActivity";
    private CallBack callBack;
    private SessionManager session;
    private String locationCode;
    private HashMap<String,String> user;

    public static class TransferViewHolder extends RecyclerView.ViewHolder {
        private TextView takeNo;
        private TextView date;
        private TextView tolocation;
        private ImageView status;
        private ImageView printPreview;
        LinearLayout statusLayout;

        public TransferViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.take_date);
            takeNo = view.findViewById(R.id.take_no);
            tolocation = view.findViewById(R.id.to_location);
            status = view.findViewById(R.id.status);
            printPreview=view.findViewById(R.id.print_preview);
           // progressBar = view.findViewById(R.id.progressBar);
           // statusLayout = view.findViewById(R.id.status_layout);
        }
    }

    /**
     * Constructor for the send the arraylist and context
     *
     * @param mContext
     * @param transferList
     */
    public StockTakeAdapter(Context mContext, ArrayList<StockTakeModel> transferList, CallBack callBack) {
        this.mContext = mContext;
        this.transferList = transferList;
        this.callBack = callBack;
        this.transferListFilter = new ArrayList<>(transferList);
        session=new SessionManager(mContext);
        user=session.getUserDetails();
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */

    @Override
    public TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_take_list_item, parent, false);
        return new TransferViewHolder(itemView);
    }

    /**
     * @param holder
     * @param position
     */

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final TransferViewHolder holder, final int position) {
        try {
            final StockTakeModel model = transferList.get(position);
            int sn=position+1;
            String apidate = "" ;
            String apidateFormat = "" ;
            holder.takeNo.setText(model.getStockTakeNo());

            apidate =model.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
            Date datem = null;
            datem = sdf.parse(apidate);

            apidateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(datem);

            holder.date.setText(apidateFormat);
            holder.tolocation.setText(model.getLocation());
            
            if (model.getStatus().equals("Open") || model.getStatus().equals("O")){
                holder.status.setImageResource(R.drawable.open);
            }else if (model.getStatus().equals("C") || model.getStatus().equals("Closed")){
                holder.status.setImageResource(R.drawable.closed);
            }else {
                holder.status.setImageResource(R.drawable.hold);
            }
            holder.printPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.callDescription(model.getCode(),model,"Preview");
                }
            });

        } catch (Exception ex) {
            Log.e("Error_in_adapter:", Objects.requireNonNull(ex.getMessage()));
        }
    }

    /**
     * @return array list count
     */
    @Override
    public int getItemCount() {
        return transferList.size();
    }

    public interface CallBack {
        void callDescription(String transferId,StockTakeModel model ,String mode);
        void convertTransfer(String requestId);
    }

    public Bitmap getImage(String base64String) {
        String base64Image = base64String.split(",")[1];
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public void filterList(ArrayList<StockTakeModel> filterdNames) {
        this.transferList = filterdNames;
        notifyDataSetChanged();
    }


    @Override
    public Filter getFilter() {
        return TransferFilter;
    }
    private Filter TransferFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<StockTakeModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(transferListFilter);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (StockTakeModel item : transferListFilter) {
                    if (item.stockTakeNo.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            transferList.clear();
            transferList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}