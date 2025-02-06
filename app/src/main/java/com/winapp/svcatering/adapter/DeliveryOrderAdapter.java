package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.DeliveryOrderModel;
import com.winapp.svcatering.model.SalesOrderPrintPreviewModel;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class DeliveryOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    public static ArrayList<DeliveryOrderModel> deliveryOrderList;
    public static ArrayList<DeliveryOrderModel> salesOrderFilterList;
    Context mContext;
    CallBack callBack;

    private String companyId;
    private SessionManager session;
    private HashMap<String, String> user;
    private ArrayList<SalesOrderPrintPreviewModel.SalesList> salesOrdernewList;
    private String locationCode;

    public DeliveryOrderAdapter(Context context, RecyclerView mRecyclerView, ArrayList<DeliveryOrderModel> salesOrderList,CallBack callBack) {

        this.deliveryOrderList = salesOrderList;
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
        return deliveryOrderList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.delivery_order_item, parent, false);
            return new SalesOrderViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder  viewHolder, int position) {
        if ( viewHolder instanceof SalesOrderViewHolder) {
            session = new SessionManager(mContext);
            user = session.getUserDetails();
            companyId = user.get(SessionManager.KEY_COMPANY_CODE);
            locationCode=user.get(SessionManager.KEY_LOCATION_CODE);

            DeliveryOrderModel salesOrderModel = deliveryOrderList.get(position);
            ((SalesOrderViewHolder) viewHolder).name.setText(salesOrderModel.getCustomerName());
            ((SalesOrderViewHolder) viewHolder).date.setText(salesOrderModel.getDate());
            ((SalesOrderViewHolder) viewHolder).subtotal.setText(Utils.twoDecimalPoint(Double.parseDouble(salesOrderModel.getSubTotal())));
            ((SalesOrderViewHolder) viewHolder).doNumber.setText(salesOrderModel.getDoNumber());
//            ((SalesOrderViewHolder) viewHolder).balance.setText("$ "+salesOrderModel.getBalance());
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


            switch (salesOrderModel.getDoStatus()) {
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
                    callBack.showMoreOption(salesOrderModel.getDoCode(), salesOrderModel.getCustomerName(),((SalesOrderViewHolder) viewHolder).status.getText().toString());
                }
            });

        } else if (viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)  viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return deliveryOrderList == null ? 0 : deliveryOrderList.size();
    }

    public void setLoaded() {
        isLoading = false;
       // callBack.calculateNetTotal(deliveryOrderList);
    }

    static class SalesOrderViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView date;
        private TextView doNumber;
        private TextView balance;
        private TextView netTotal;
        private TextView subtotal;
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
            name=view.findViewById(R.id.name);
            date=view.findViewById(R.id.date);
            doNumber =view.findViewById(R.id.do_no);
            balance=view.findViewById(R.id.balance);
            subtotal =view.findViewById(R.id.balance_value);
            netTotal=view.findViewById(R.id.net_total);
            mainCard=view.findViewById(R.id.cardlist_item);
            status=view.findViewById(R.id.status);
            moreOption=view.findViewById(R.id.more);
            statusLayout=view.findViewById(R.id.status_layout);
            indicator=view.findViewById(R.id.indicator);
            productListView=view.findViewById(R.id.invoiceList);
            showHideBottomLayout=view.findViewById(R.id.show_hide);
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
      //  void calculateNetTotal(ArrayList<SalesOrderModel> salesList);
        void showMoreOption(String deliveryorderId, String customerName, String status);
    }

    public void filterList(ArrayList<DeliveryOrderModel> filterdNames) {
        deliveryOrderList = filterdNames;
        notifyDataSetChanged();
    }

    public static ArrayList<DeliveryOrderModel> getNotalInvoiceList(){
        return deliveryOrderList;
    }

    public static ArrayList<DeliveryOrderModel> getDeliveryOrderList(){
        return salesOrderFilterList;
    }


  /*  private void getSalesOrderDetails(String soNumber,RecyclerView.ViewHolder  viewHolder, int position,SalesOrderModel salesOrderModel) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyId);
        jsonObject.put("SoNo", soNumber);
        jsonObject.put("LocationCode",locationCode);
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        String url= Utils.getBaseUrl(mContext) +"SalesApi/GetSOByCode?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        salesOrdernewList =new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Sales_Details:",response.toString());
                        if (response.length()>0){
                            SalesOrderPrintPreviewModel model=new SalesOrderPrintPreviewModel();
                            model.setSoNumber(response.optString("SoNo"));
                            model.setSoDate(response.optString("SoDateString"));
                            model.setCustomerCode(response.optString("CustomerCode"));
                            model.setCustomerName(response.optString("CustomerName"));
                            model.setAddress(response.optString("Address1"));
                            model.setDeliveryAddress(response.optString("Address1"));
                            model.setSubTotal(response.optString("SubTotal"));
                            model.setNetTax(response.optString("Tax"));
                            model.setNetTotal(response.optString("NetTotal"));
                            model.setTaxType(response.optString("TaxType"));
                            model.setTaxValue(response.optString("TaxPerc"));
                            model.setOutStandingAmount(response.optString("BalanceAmount"));
                            model.setBillDiscount(response.optString("BillDIscount"));
                            model.setItemDiscount(response.optString("ItemDiscount"));
                            JSONArray products=response.getJSONArray("SoDetails");
                            for (int i=0;i<products.length();i++) {
                                JSONObject object = products.getJSONObject(i);
                                if (Double.parseDouble(object.optString("LQty"))>0){
                                    SalesOrderPrintPreviewModel.SalesList salesListModel =new SalesOrderPrintPreviewModel.SalesList();

                                    salesListModel.setProductCode(object.optString("ProductCode"));
                                    salesListModel.setDescription( object.optString("ProductName"));
                                    salesListModel.setLqty(object.optString("LQty"));
                                    salesListModel.setCqty(object.optString("CQty"));
                                    salesListModel.setNetQty(object.optString("LQty"));
                                    salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                    salesListModel.setUnitPrice(object.optString("Price"));
                                    double qty=Double.parseDouble(object.optString("LQty"));
                                    double price=Double.parseDouble(object.optString("Price"));

                                    double nettotal=qty * price;
                                    salesListModel.setTotal(String.valueOf(nettotal));
                                    salesListModel.setPricevalue(String.valueOf(price));

                                    salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                    salesListModel.setItemtax(object.optString("Tax"));
                                    salesListModel.setSubTotal(object.optString("SubTotal"));
                                    salesOrdernewList.add(salesListModel);


                                    if (Double.parseDouble(object.optString("CQty")) > 0) {
                                        salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(object.optString("ProductCode"));
                                        salesListModel.setDescription(object.optString("ProductName"));
                                        salesListModel.setLqty(object.optString("LQty"));
                                        salesListModel.setCqty(object.optString("CQty"));
                                        salesListModel.setNetQty(object.optString("CQty"));

                                        double qty1 = Double.parseDouble(object.optString("CQty"));
                                        double price1 = Double.parseDouble(object.optString("CartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(object.optString("UOMCode"));
                                        salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                        salesListModel.setUnitPrice(object.optString("Price"));
                                        salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        salesListModel.setItemtax(object.optString("Tax"));
                                        salesListModel.setSubTotal(object.optString("SubTotal"));
                                        salesOrdernewList.add(salesListModel);
                                    }

                                }else {
                                    if (Double.parseDouble(object.optString("CQty")) > 0) {
                                        SalesOrderPrintPreviewModel.SalesList salesListModel = new SalesOrderPrintPreviewModel.SalesList();
                                        salesListModel.setProductCode(object.optString("ProductCode"));
                                        salesListModel.setDescription(object.optString("ProductName"));
                                        salesListModel.setLqty(object.optString("LQty"));
                                        salesListModel.setCqty(object.optString("CQty"));
                                        salesListModel.setNetQty(object.optString("Qty"));
                                        salesListModel.setCartonPrice(object.optString("CartonPrice"));
                                        salesListModel.setUnitPrice(object.optString("Price"));

                                        double qty1 = Double.parseDouble(object.optString("CQty"));
                                        double price1 = Double.parseDouble(object.optString("CartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        salesListModel.setTotal(String.valueOf(nettotal1));
                                        salesListModel.setPricevalue(String.valueOf(price1));

                                        salesListModel.setUomCode(object.optString("UOMCode"));
                                        salesListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        salesListModel.setItemtax(object.optString("Tax"));
                                        salesListModel.setSubTotal(object.optString("SubTotal"));
                                        salesOrdernewList.add(salesListModel);
                                    }
                                }
                            }


                            model.setSalesList(salesOrdernewList);



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
        SalesOrderPrintPreviewAdapter adapter=new SalesOrderPrintPreviewAdapter(mContext, salesList);
        ((SalesOrderViewHolder) viewHolder).productListView.setAdapter(adapter);
        // notifyDataSetChanged();
    }*/

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