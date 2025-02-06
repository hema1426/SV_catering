package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.ShowCatagoryActivity;
import com.winapp.svcatering.model.DepartmentModel;

import java.util.ArrayList;
import java.util.Objects;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ProfileViewHolder> {
    /**
     * Declare the Context and Arraylist variables
     */
    private Context mContext;
    private ArrayList<DepartmentModel> productsList;
    SharedPreferences sharedpreferences;
    private int mContainerId;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private final static String TAG = "DashBoardActivity";
    CallBack callBack;

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        // Declare the Required Variables
        private ImageView thumbnail;
        private final ProgressBar progressBar;
        private TextView productName;
        private TextView stockStatus;
        private TextView ctnQty;
        private TextView netPcs;
        private TextView ctnPcs;
        LinearLayout statusLayout;

        public ProfileViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.product_name);
            stockStatus = view.findViewById(R.id.stock_status);
            ctnQty = view.findViewById(R.id.ctn_qty);
            netPcs = view.findViewById(R.id.net_price);
            ctnPcs = view.findViewById(R.id.stock_count);
            thumbnail = view.findViewById(R.id.thumbnail);
            progressBar = view.findViewById(R.id.progressBar);
            statusLayout = view.findViewById(R.id.status_layout);
        }
    }

    /**
     * Constructor for the send the arraylist and context
     *
     * @param mContext
     * @param productsList
     */
    public DepartmentAdapter(Context mContext, ArrayList<DepartmentModel> productsList, CallBack callBack) {
        this.mContext = mContext;
        this.productsList = productsList;
        this.callBack = callBack;
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.department_items, parent, false);
        return new ProfileViewHolder(itemView);
    }

    /**
     * @param holder
     * @param position
     */

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ProfileViewHolder holder, final int position) {
        try {
            final DepartmentModel deparment = productsList.get(position);
            // loading deparment cover using Glide library
            holder.productName.setText(deparment.getDepartmentName().trim());
            if (deparment.getDepartmentImage()!=null && !deparment.getDepartmentImage().equals("null")){
                Glide.with(mContext)
                        .load(deparment.getDepartmentImage())
                        .error(R.drawable.no_image_found)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).into(holder.thumbnail);
            }else {
                holder.thumbnail.setImageResource(R.drawable.no_image_found);
            }


           /* String input = deparment.getDepartmentImage();
            boolean isBase64 = input.indexOf("base64") != -1 ? true : false;
            if (isBase64) {
                Bitmap bitmap = getImage(deparment.getDepartmentImage());
                holder.thumbnail.setImageBitmap(bitmap);
            } else {
                holder.thumbnail.setImageResource(R.drawable.no_image_found);
            }*/
            // OnclickListener on Image Thumbnail
            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext, ShowCatagoryActivity.class);
                    intent.putExtra("departmentId",deparment.getDepartmentCode());
                    intent.putExtra("departmentName",deparment.getDepartmentName());
                    intent.putExtra("from","department");
                    mContext.startActivity(intent);
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
        return productsList.size();
    }

    public interface CallBack {
        void callDescription();
    }

    public Bitmap getImage(String base64String) {
        String base64Image = base64String.split(",")[1];
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public void filterList(ArrayList<DepartmentModel> filterdNames) {
        this.productsList = filterdNames;
        notifyDataSetChanged();
    }
}