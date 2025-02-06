package com.winapp.svcatering.receipts;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.ReceiptInvoiceAdapter;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReceiptsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    ArrayList<ReceiptsModel> receiptsList;
    Context mContext;
    CallBack callBack;
    public static ArrayList<ReceiptsModel> receiptsFilterList;
    private String companyId;
    private SessionManager session;
    private HashMap<String, String> user;
    private ArrayList<InvoiceModel> receiptInvoiceList;

    public ReceiptsListAdapter(Context context, RecyclerView mRecyclerView, ArrayList<ReceiptsModel> receiptsList, CallBack callBack) {

        this.receiptsList = receiptsList;
        receiptsFilterList=receiptsList;
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
        return receiptsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.receipts_list_items, parent, false);
            return new ReceiptsViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder  viewHolder, @SuppressLint("RecyclerView") int position) {

        try {
            session = new SessionManager(mContext);
            user = session.getUserDetails();
            companyId = user.get(SessionManager.KEY_COMPANY_CODE);
            if ( viewHolder instanceof ReceiptsViewHolder) {
                ReceiptsModel receiptsModel = receiptsList.get(position);
                ((ReceiptsViewHolder) viewHolder).name.setText(receiptsModel.getName());
                if (receiptsModel.getDate().equals("null")){
                    ((ReceiptsViewHolder) viewHolder).date.setText("");
                }else {
                    ((ReceiptsViewHolder) viewHolder).date.setText(receiptsModel.getDate());
                }

                if (receiptsModel.getReceiptNumber().equals("null")){
                    ((ReceiptsViewHolder) viewHolder).customerCode.setText("");
                }else {
                    ((ReceiptsViewHolder) viewHolder).customerCode.setText(receiptsModel.getReceiptNumber());
                }
                if (receiptsModel.getNetTotal().equals("null")){
                    ((ReceiptsViewHolder) viewHolder).netTotal.setText("$ "+ "0.00");
                }else {
                    ((ReceiptsViewHolder) viewHolder).netTotal.setText("$ "+ Utils.twoDecimalPoint(Double.parseDouble(receiptsModel.getNetTotal())));
                }

                ((ReceiptsViewHolder) viewHolder).transactionMode.setText(receiptsModel.getTransactionMode());

                ((ReceiptsViewHolder) viewHolder).moreOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callBack.showMoreOption(receiptsModel.getReceiptCode(),receiptsModel.getName(),receiptsModel.getCustomerCode(),receiptsModel.getTransactionMode(),receiptsModel);
                    }
                });

                ((ReceiptsViewHolder)viewHolder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        callBack.showMoreOption(receiptsModel.getReceiptCode(),receiptsModel.getName(),receiptsModel.getCustomerCode(),receiptsModel.getTransactionMode(),receiptsModel);
                        return false;
                    }
                });

                ((ReceiptsViewHolder)viewHolder).shareWhatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callBack.openWhatsapp();
                    }
                });

                ((ReceiptsViewHolder)viewHolder).showHideBottomLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (((ReceiptsViewHolder)viewHolder).showHideBottomLayout.getTag().equals("hide")){
                            ((ReceiptsViewHolder)viewHolder).bottomLayout.setVisibility(View.VISIBLE);
                            ((ReceiptsViewHolder)viewHolder).showHideBottomLayout.setTag("show");
                            ((ReceiptsViewHolder)viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_up_24));
                            try {
                                if (receiptsModel.getInvoiceList().size()>0){
                                    setInvoiceAdapter(viewHolder,position,receiptsModel.getInvoiceList());
                                    ((ReceiptsViewHolder)viewHolder).progressLayout.setVisibility(View.GONE);
                                    ((ReceiptsViewHolder)viewHolder).mainLayout.setVisibility(View.VISIBLE);
                                }else {
                                    getReceiptsDetails(receiptsModel.getReceiptCode(),viewHolder,position,receiptsModel);
                                    receiptsModel.setShow(true);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                             receiptsModel.setShow(false);
                            ((ReceiptsViewHolder)viewHolder).bottomLayout.setVisibility(View.GONE);
                            ((ReceiptsViewHolder)viewHolder).showHideBottomLayout.setTag("hide");
                            ((ReceiptsViewHolder)viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_down_24));
                        }
                    }
                });

                if (receiptsModel.isShow()){
                    ((ReceiptsViewHolder)viewHolder).bottomLayout.setVisibility(View.VISIBLE);
                    ((ReceiptsViewHolder)viewHolder).showHideBottomLayout.setTag("show");
                    ((ReceiptsViewHolder)viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_up_24));
                    try {
                        //getReceiptsDetails(receiptsModel.getReceiptNumber(),viewHolder,position,receiptsModel);
                        setInvoiceAdapter(viewHolder,position,receiptsModel.getInvoiceList());
                        ((ReceiptsViewHolder)viewHolder).progressLayout.setVisibility(View.GONE);
                        ((ReceiptsViewHolder)viewHolder).mainLayout.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    ((ReceiptsViewHolder)viewHolder).bottomLayout.setVisibility(View.GONE);
                    ((ReceiptsViewHolder)viewHolder).showHideBottomLayout.setTag("hide");
                    ((ReceiptsViewHolder)viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_down_24));
                }


            } else if ( viewHolder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder)  viewHolder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }catch (Exception ex){}
    }

  /*  @Override
    public int getItemCount() {
        return receiptsList == null ? 0 : receiptsList.size();
    }*/

    @Override
    public int getItemCount() {
        return (null != receiptsList ? receiptsList.size() : 0);
    }

    public void setLoaded() {
        isLoading = false;
        callBack.calculateNetTotal(receiptsList);
    }

    private ArrayList<InvoiceModel> getReceiptsDetails(String receiptNumber,@NonNull RecyclerView.ViewHolder  viewHolder, int position,ReceiptsModel receiptsModel) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("CompanyCode", companyId);
        jsonObject.put("ReceiptNo", receiptNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        String url = Utils.getBaseUrl(mContext) + "SalesApi/GetReceiptsByCode?Requestdata=" + jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url);
        receiptInvoiceList=new ArrayList<>();
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.w("GivenReceiptResponse:",response.toString());
                        if (response.length()>0){
                            for (int i=0;i<response.length();i++){
                                JSONObject object=response.getJSONObject(i);
                                InvoiceModel model=new InvoiceModel();
                                model.setInvoiceNo(object.optString("InvoiceNo"));
                                model.setInvoiceDate(object.optString("InvoiceDateString"));
                                model.setNetAmount(object.optString("NetTotal"));
                                model.setPaidAmount(object.optString("PaidAmount"));
                                model.setBalanceAmount(object.optString("BalanceAmount"));
                                receiptInvoiceList.add(model);
                            }
                        }
                        if (receiptInvoiceList.size()>0){
                            ((ReceiptsViewHolder)viewHolder).progressLayout.setVisibility(View.GONE);
                            ((ReceiptsViewHolder)viewHolder).mainLayout.setVisibility(View.VISIBLE);
                            receiptsModel.setInvoiceList(receiptInvoiceList);
                            setInvoiceAdapter(viewHolder,position,receiptInvoiceList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            Log.w("Error_throwing:", error.toString());
        }) {
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

        return receiptInvoiceList;
    }

    public void setInvoiceAdapter(@NonNull RecyclerView.ViewHolder  viewHolder, int position,ArrayList<InvoiceModel> invoices){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        ((ReceiptsViewHolder)viewHolder).invoiceList.setHasFixedSize(true);
        ((ReceiptsViewHolder)viewHolder).invoiceList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        // ((ReceiptsViewHolder)viewHolder).invoiceList.setLayoutManager(layoutManager);
        ReceiptInvoiceAdapter adapter=new ReceiptInvoiceAdapter(mContext,invoices);
        ((ReceiptsViewHolder)viewHolder).invoiceList.setAdapter(adapter);
        ((ReceiptsViewHolder)viewHolder).noofInvoices.setText("TOTAL INVOICES : "+invoices.size()+"");
        // notifyDataSetChanged();
    }

    static class ReceiptsViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView date;
        private TextView customerCode;
        private TextView netTotal;
        private TextView transactionMode;
        private ImageView moreOption;
        private ImageView shareWhatsapp;
        private ImageView showHideBottomLayout;
        private CardView bottomLayout;
        private RecyclerView invoiceList;
        private TextView noofInvoices;
        private LinearLayout mainLayout;
        private LinearLayout progressLayout;

        public ReceiptsViewHolder(View view) {
            super(view);
            name=view.findViewById(R.id.name);
            date=view.findViewById(R.id.date);
            netTotal=view.findViewById(R.id.net_total);
            customerCode=view.findViewById(R.id.customer_code);
            transactionMode=view.findViewById(R.id.transaction_mode);
            moreOption=view.findViewById(R.id.more_option);
            shareWhatsapp=view.findViewById(R.id.share_whatsapp);
            bottomLayout=view.findViewById(R.id.bottom_layout);
            showHideBottomLayout=view.findViewById(R.id.show_hide);
            invoiceList=view.findViewById(R.id.invoiceList);
            noofInvoices=view.findViewById(R.id.no_of_invoices);
            mainLayout=view.findViewById(R.id.main_layout);
            progressLayout=view.findViewById(R.id.progress_layout);
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

    public void filterList(ArrayList<ReceiptsModel> filterdNames) {
        receiptsList = filterdNames;
        notifyDataSetChanged();
    }

    public static ArrayList<ReceiptsModel> getReceiptsList(){
        return receiptsFilterList;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        Log.w("Given_date_printed:",date);
        return date;
    }

    public interface CallBack {
        void calculateNetTotal(ArrayList<ReceiptsModel> salesList);
        void showMoreOption(String receiptNo,String customerName,String  customerCode,String paymentMode,ReceiptsModel model);
        void openWhatsapp();
    }

    public void setOrder() {
        try {
            Collections.sort(receiptsList, new Comparator<ReceiptsModel>() {
                public int compare(ReceiptsModel obj1, ReceiptsModel obj2) {
                    SimpleDateFormat sdfo = new SimpleDateFormat("MM-dd-yyyy");
                    // Get the two dates to be compared
                    Date d1 = null;
                    Date d2 = null;
                    try {
                        d1 = sdfo.parse(obj1.getDate());
                        d2 = sdfo.parse(obj2.getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // ## Ascending order
                    //  return obj1.getNetTotal().compareToIgnoreCase(obj2.getNetTotal()); // To compare string values
                    return d1.compareTo(d2); // To compare integer values
                    // ## Descending order
                    // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                    // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                }
            });
            notifyDataSetChanged();
        } catch (Exception ex) {
        }
    }

   public static class InvoiceModel {
        private String invoiceNo;
        private String invoiceDate;
        private String netAmount;
        private String paidAmount;
        private String balanceAmount;

        public String getInvoiceNo() {
            return invoiceNo;
        }

        public void setInvoiceNo(String invoiceNo) {
            this.invoiceNo = invoiceNo;
        }

        public String getInvoiceDate() {
            return invoiceDate;
        }

        public void setInvoiceDate(String invoiceDate) {
            this.invoiceDate = invoiceDate;
        }

        public String getNetAmount() {
            return netAmount;
        }

        public void setNetAmount(String netAmount) {
            this.netAmount = netAmount;
        }

        public String getPaidAmount() {
            return paidAmount;
        }

        public void setPaidAmount(String paidAmount) {
            this.paidAmount = paidAmount;
        }

        public String getBalanceAmount() {
            return balanceAmount;
        }

        public void setBalanceAmount(String balanceAmount) {
            this.balanceAmount = balanceAmount;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}