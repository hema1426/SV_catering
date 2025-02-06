package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
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
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.model.SalesOrderModel;
import com.winapp.svcatering.model.SalesOrderPrintPreviewModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PurchaseInvoiceAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private String company_code;

    public static ArrayList<SalesOrderModel> salesOrderList;
    public static ArrayList<SalesOrderModel> salesOrderFilterList;
    Context mContext;
    CallBack callBack;

    private String companyId;
    private SessionManager session;
    private HashMap<String, String> user;
    private ArrayList<SalesOrderPrintPreviewModel.SalesList> salesOrdernewList;
    private String locationCode;

    public PurchaseInvoiceAdapterNew(Context context, RecyclerView mRecyclerView, ArrayList<SalesOrderModel> salesOrderList, CallBack callBack) {

        this.salesOrderList = salesOrderList;
        this.salesOrderFilterList=salesOrderList;
        this.mContext=context;
        this.callBack=callBack;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return salesOrderList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.purchase_inv_list_items, parent, false);
            return new SalesOrderViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder  viewHolder, @SuppressLint("RecyclerView") int position) {
        if ( viewHolder instanceof SalesOrderViewHolder) {
            session = new SessionManager(mContext);
            user = session.getUserDetails();
            companyId = user.get(SessionManager.KEY_COMPANY_CODE);
            locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
            company_code = user.get(SessionManager.KEY_COMPANY_CODE);

            SalesOrderModel salesOrderModel = salesOrderList.get(position);
            ((SalesOrderViewHolder) viewHolder).name.setText(salesOrderModel.getName());
            ((SalesOrderViewHolder) viewHolder).date.setText(salesOrderModel.getDate());
//            if (salesOrderModel.getAddress().equals("null") || salesOrderModel.getAddress().isEmpty()){
//                ((SalesOrderViewHolder) viewHolder).address.setText("Address not found");
//            }else {
//                ((SalesOrderViewHolder) viewHolder).address.setText(salesOrderModel.getAddress());
//            }
            ((SalesOrderViewHolder) viewHolder).soNumber.setText(salesOrderModel.getSaleOrderNumber());
            ((SalesOrderViewHolder) viewHolder).balance.setText("$ "+salesOrderModel.getBalance());
            if (salesOrderModel.getNetTotal()!=null && !salesOrderModel.getNetTotal().equals("null")){
                ((SalesOrderViewHolder) viewHolder).netTotal.setText("$ "+Utils.twoDecimalPoint(Double.parseDouble(salesOrderModel.getNetTotal())));
            }else {
                ((SalesOrderViewHolder) viewHolder).netTotal.setText("$ "+"0.00");
            }

           /* if (position % 2 ==1){
                ((SalesOrderViewHolder) viewHolder).mainCard.setBackgroundColor(Color.parseColor("#f3f3f3"));
            }else {
                ((SalesOrderViewHolder) viewHolder).mainCard.setBackgroundColor(Color.parseColor("#ffffff"));
            }*/
            switch (salesOrderModel.getStatus()) {
                case "C":
                    ((SalesOrderViewHolder) viewHolder).status.setText("Closed");
                    ((SalesOrderViewHolder) viewHolder).statusLayout.setBackgroundResource(R.drawable.invoice_closed);
                    ((SalesOrderViewHolder) viewHolder).indicator.setBackgroundResource(R.drawable.invoice_closed);
                    break;
                case "O":
                    ((SalesOrderViewHolder) viewHolder).status.setText("Open");
                    ((SalesOrderViewHolder) viewHolder).statusLayout.setBackgroundResource(R.drawable.invoice_status_paid);
                    ((SalesOrderViewHolder) viewHolder).indicator.setBackgroundResource(R.drawable.invoice_status_paid);
                    break;
                default:
                    ((SalesOrderViewHolder) viewHolder).status.setText("Open");
                    ((SalesOrderViewHolder) viewHolder).statusLayout.setBackgroundResource(R.drawable.invoice_status_paid);
                    ((SalesOrderViewHolder) viewHolder).indicator.setBackgroundResource(R.drawable.invoice_status_paid);
                    break;
            }

            ((SalesOrderViewHolder) viewHolder).moreOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.showMoreOption(salesOrderModel.getSalesOrderCode(),
                            salesOrderModel.getName(),((SalesOrderViewHolder) viewHolder).status.getText().toString());
                }
            });

            ((SalesOrderViewHolder)viewHolder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    callBack.showMoreOption(salesOrderModel.getSalesOrderCode(),
                            salesOrderModel.getName(),((SalesOrderViewHolder) viewHolder).status.getText().toString());
                    return false;
                }
            });



            ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((SalesOrderViewHolder) viewHolder).showHideBottomLayout.getTag().equals("hide")){
                        ((SalesOrderViewHolder) viewHolder).bottomLayout.setVisibility(View.VISIBLE);
                        ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setTag("show");
                        ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_up_24));
                        try {
                            if (salesOrderModel.getSalesList().size()>0){
                                setSalesAdapter(viewHolder,position,salesOrderModel.getSalesList());
                                ((SalesOrderViewHolder) viewHolder).progressLayout.setVisibility(View.GONE);
                                ((SalesOrderViewHolder) viewHolder).mainLayout.setVisibility(View.VISIBLE);
                            }else {
                                getSalesOrderDetails(salesOrderModel.getSalesOrderCode(),viewHolder,position,salesOrderModel);
                                salesOrderModel.setShow(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        salesOrderModel.setShow(false);
                        ((SalesOrderViewHolder) viewHolder).bottomLayout.setVisibility(View.GONE);
                        ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setTag("hide");
                        ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_down_24));
                    }
                }
            });


            if (salesOrderModel.isShow()){
                ((SalesOrderViewHolder) viewHolder).bottomLayout.setVisibility(View.VISIBLE);
                ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setTag("show");
                ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_up_24));
                try {
                    setSalesAdapter(viewHolder,position,salesOrderModel.getSalesList());
                    ((SalesOrderViewHolder) viewHolder).progressLayout.setVisibility(View.GONE);
                    ((SalesOrderViewHolder) viewHolder).mainLayout.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                ((SalesOrderViewHolder) viewHolder).bottomLayout.setVisibility(View.GONE);
                ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setTag("hide");
                ((SalesOrderViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_down_24));
            }


        } else if ( viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)  viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return salesOrderList == null ? 0 : salesOrderList.size();
    }

    public void setLoaded() {
        isLoading = false;
        callBack.calculateNetTotal(salesOrderList);
    }

    static class SalesOrderViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView date;
        private TextView soNumber;
        private TextView balance;
        private TextView netTotal;
        private TextView address;
        private CardView mainCard;
        private TextView status;
        private ImageView moreOption;
        private LinearLayout statusLayout;
        private View indicator;

        private RecyclerView productListView;
        private ImageView showHideBottomLayout;
        private LinearLayout mainLayout;
        private LinearLayout progressLayout;
        private LinearLayout bottomLayout;

        public SalesOrderViewHolder(View view) {
            super(view);
            name=view.findViewById(R.id.purchas_name_item);
            date=view.findViewById(R.id.purchas_date_item);
            soNumber=view.findViewById(R.id.purchas_no_item);
            balance=view.findViewById(R.id.purchas_balance_item);
            address=view.findViewById(R.id.address);
            netTotal=view.findViewById(R.id.purchas_nettotal_item);
            mainCard=view.findViewById(R.id.cardlist_item);
            status=view.findViewById(R.id.purchas_status_item);
            moreOption=view.findViewById(R.id.more);
            statusLayout=view.findViewById(R.id.status_layout);
            indicator=view.findViewById(R.id.indicator);

            productListView=view.findViewById(R.id.invoiceList);
            showHideBottomLayout=view.findViewById(R.id.show_hide_purchase);
            mainLayout=view.findViewById(R.id.main_layout);
            progressLayout=view.findViewById(R.id.progress_layout);
            bottomLayout=view.findViewById(R.id.bottom_layout);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar1);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        Log.w("Given_date_printed:",date);
        return date;
    }

    public interface CallBack {
        void calculateNetTotal(ArrayList<SalesOrderModel> salesList);
        void showMoreOption(String salesorderId,String customerName,String status);
    }

    public void filterList(ArrayList<SalesOrderModel> filterdNames) {
        salesOrderList = filterdNames;
        notifyDataSetChanged();
    }

    public static ArrayList<SalesOrderModel> getNotalInvoiceList(){
        return salesOrderList;
    }

    public static ArrayList<SalesOrderModel> getSalesOrderList(){
        return salesOrderFilterList;
    }


    private void getSalesOrderDetails(String soNumber,RecyclerView.ViewHolder  viewHolder, int position,SalesOrderModel salesOrderModel) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("InvoiceNo",soNumber);
        jsonObject.put("LocationCode",locationCode);
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        String url= Utils.getBaseUrl(mContext) +"PurchaseInvoiceDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url_purchas:",url+jsonObject);
        salesOrdernewList =new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        Log.w("purchase_Details:",response.toString());
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")) {
                            JSONArray responseData = response.getJSONArray("responseData");
                            JSONObject object = responseData.optJSONObject(0);

                            SalesOrderPrintPreviewModel model = new SalesOrderPrintPreviewModel();
                            model.setSoNumber(object.optString("poNo"));
                            model.setSoDate(object.optString("poDate"));
                            model.setCustomerCode(object.optString("customerCode"));
                            model.setCustomerName(object.optString("customerName"));
                            model.setAddress(object.optString("address1") + object.optString("address2") + object.optString("address3"));
                            model.setDeliveryAddress(model.getAddress());
                            model.setSubTotal(object.optString("subTotal"));
                            model.setNetTax(object.optString("taxTotal"));
                            model.setNetTotal(object.optString("netTotal"));
                            model.setTaxType(object.optString("taxType"));
                            model.setTaxValue(object.optString("taxPerc"));
                            model.setOutStandingAmount(object.optString("balanceAmount"));
                            Utils.setInvoiceMode("SalesOrder");
                            model.setBillDiscount(object.optString("billDiscount"));
                            model.setItemDiscount(object.optString("totalDiscount"));
                            model.setAddress1(object.optString("address1"));
                            model.setAddress2(object.optString("address2"));
                            model.setAddress3(object.optString("address3"));
                            model.setAddressstate(object.optString("block")+" "+object.optString("street")+" "
                                    +object.optString("city"));
                            model.setAddresssZipcode(object.optString("countryName")+" "+object.optString("state")+" "
                                    +object.optString("zipcode"));

                            String signFlag = object.optString("signFlag");
                            if (signFlag.equals("Y")) {
                                String signature = object.optString("signature");
                                Utils.setSignature(signature);
                            } else {
                                Utils.setSignature("");
                            }

                            JSONArray detailsArray = object.optJSONArray("purchaseInvoiceDetails");
                            for (int i=0;i<detailsArray.length();i++){
                                JSONObject detailObject=detailsArray.optJSONObject(i);
                                SalesOrderPrintPreviewModel.SalesList salesListModel =new SalesOrderPrintPreviewModel.SalesList();
                                salesListModel.setProductCode(detailObject.optString("productCode"));
                                salesListModel.setDescription( detailObject.optString("productName"));
                                salesListModel.setLqty(detailObject.optString("unitQty"));
                                salesListModel.setCqty(detailObject.optString("cartonQty"));
                                salesListModel.setNetQty(detailObject.optString("quantity"));
                                salesListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                                salesListModel.setUnitPrice(detailObject.optString("price"));
                                double qty=Double.parseDouble(detailObject.optString("quantity"));
                                double price=Double.parseDouble(detailObject.optString("price"));

                                double nettotal=qty * price;
                                salesListModel.setTotal(String.valueOf(nettotal));
                                salesListModel.setPricevalue(String.valueOf(price));

                                salesListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                                salesListModel.setItemtax(detailObject.optString("totalTax"));
                                salesListModel.setSubTotal(detailObject.optString("subTotal"));
                                salesOrdernewList.add(salesListModel);


                                model.setSalesList(salesOrdernewList);
                                //      salesOrderHeaderDetails.add(model);
                            }
                        }

                        if (salesOrdernewList.size()>0){
                            ((SalesOrderViewHolder) viewHolder).progressLayout.setVisibility(View.GONE);
                            ((SalesOrderViewHolder) viewHolder).mainLayout.setVisibility(View.VISIBLE);
                            salesOrderModel.setSalesList(salesOrdernewList);
                            setSalesAdapter(viewHolder,position,salesOrdernewList);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            Log.w("Error_throwing:",error.toString());
        }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }
            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }
            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    public void setSalesAdapter(@NonNull RecyclerView.ViewHolder  viewHolder, int position, ArrayList<SalesOrderPrintPreviewModel.SalesList> salesList){
        ((SalesOrderViewHolder) viewHolder).productListView.setHasFixedSize(true);
        ((SalesOrderViewHolder) viewHolder).productListView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        PurchaseInvoicePrintPreviewAdapter adapter=new PurchaseInvoicePrintPreviewAdapter(mContext, salesList);
        ((SalesOrderViewHolder) viewHolder).productListView.setAdapter(adapter);
        // notifyDataSetChanged();
    }

    /*private Date getDate(long time) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        sdf.setTimeZone(tz);//set time zone.
        String localTime = sdf.format(new Date(time) * 1000));
        Date date = new Date();
        try {
            date = sdf.parse(localTime);//get local date
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }*/
}