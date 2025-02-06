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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.StockRequestListActivity;
import com.winapp.svcatering.model.TransferModel;
import com.winapp.svcatering.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.TransferViewHolder> implements Filterable {
    /**
     * Declare the Context and Arraylist variables
     */
    private Context mContext;
    private ArrayList<TransferModel> transferList;
    private ArrayList<TransferModel> transferListFilter;
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
        // Declare the Required Variables
        private ImageView thumbnail;
      //  private final ProgressBar progressBar;
        private TextView transferNo;
        private TextView date;
        private TextView tolocation;
        private TextView fromlocation;
        private TextView user;
        private ImageView status;
        private ImageView print;
        private ImageView printPreview;
        private ImageView convertTransfer;
        private TextView snNo;
        LinearLayout statusLayout;

        public TransferViewHolder(View view) {
            super(view);
            transferNo = view.findViewById(R.id.transfer_no);
            date = view.findViewById(R.id.date);
            tolocation = view.findViewById(R.id.to_location);
            fromlocation=view.findViewById(R.id.from_location);
            user = view.findViewById(R.id.user);
            status = view.findViewById(R.id.status);
            print=view.findViewById(R.id.print);
            snNo=view.findViewById(R.id.sn_no);
            thumbnail = view.findViewById(R.id.more_option);
            printPreview=view.findViewById(R.id.print_preview);
            convertTransfer=view.findViewById(R.id.convert);
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
    public TransferAdapter(Context mContext, ArrayList<TransferModel> transferList, CallBack callBack) {
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transfer_list_item, parent, false);
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
            final TransferModel model = transferList.get(position);
            int sn=position+1;
            holder.snNo.setText(sn+"");
            holder.transferNo.setText(model.getTransferNo());
            holder.date.setText(model.getDate());
            holder.tolocation.setText(model.getToLocation());
            holder.fromlocation.setText(model.getFromLocation());
            holder.user.setText(model.getUser());
            if (mContext instanceof StockRequestListActivity){
                locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
                assert locationCode != null;
                if (locationCode.equalsIgnoreCase(model.fromLocation)){
                    holder.convertTransfer.setVisibility(View.VISIBLE);
                }else {
                    holder.convertTransfer.setVisibility(View.GONE);
                }
            }else {
              //  holder.status.setVisibility(View.VISIBLE);
                holder.convertTransfer.setVisibility(View.GONE);
            }
            Log.w("StatusValue:",model.getStatus()+"");
            if (model.getStatus().equals("Open") || model.getStatus().equals("O")){
                holder.status.setImageResource(R.drawable.open);
            }else if (model.getStatus().equals("C") || model.getStatus().equals("Closed")){
                holder.status.setImageResource(R.drawable.closed);
            }else {
                holder.status.setImageResource(R.drawable.hold);
            }

          //  holder.status.setText(model.getStatus());
            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });

            holder.print.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.callDescription(model.getTransferNo(),"Print");
                }
            });

            holder.printPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.callDescription(model.getTransferNo(),"Preview");
                }
            });

            holder.convertTransfer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (model.getStatus().equals("O") || model.getStatus().equals("Open")){
                        callBack.convertTransfer(model.getTransferNo().toString());
                    }else {
                        Toast.makeText(mContext,"Request Already Closed...!",Toast.LENGTH_SHORT).show();
                    }
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
        void callDescription(String transferId,String mode);
        void convertTransfer(String requestId);
    }

    public Bitmap getImage(String base64String) {
        String base64Image = base64String.split(",")[1];
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public void filterList(ArrayList<TransferModel> filterdNames) {
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
            List<TransferModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(transferListFilter);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (TransferModel item : transferListFilter) {
                    if (item.transferNo.toLowerCase().contains(filterPattern)) {
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