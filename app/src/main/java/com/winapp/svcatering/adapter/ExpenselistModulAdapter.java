package com.winapp.svcatering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.winapp.svcatering.activity.NewExpenseModuleListActivity;
import com.winapp.svcatering.model.InvoiceModel;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExpenselistModulAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private ArrayList<InvoiceModel> invoiceList;
    private ArrayList<InvoiceModel> invoiceFilterList;
    Context mContext;
    CallBack callBack;
    private String companyId;
    private SessionManager session;
    private HashMap<String, String> user;
    private ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceshowList;

    public ExpenselistModulAdapter(Context context, RecyclerView mRecyclerView, ArrayList<InvoiceModel> invoiceList, CallBack callBack) {

        this.invoiceList = invoiceList;
        this.invoiceFilterList = invoiceList;
        this.mContext = context;
        this.callBack = callBack;

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
        //return invoiceList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        return (invoiceList != null && invoiceList.get(position) != null) ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.expense_list_new_items, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        if (viewHolder instanceof UserViewHolder) {
            session = new SessionManager(mContext);
            user = session.getUserDetails();
            companyId = user.get(SessionManager.KEY_COMPANY_CODE);
            InvoiceModel invoice = invoiceList.get(position);
            ((UserViewHolder) viewHolder).name.setText(invoice.getName());
            ((UserViewHolder) viewHolder).date.setText(invoice.getDate());
            //  ((UserViewHolder) viewHolder).address.setText(invoice.getAddress());
            ((UserViewHolder) viewHolder).soNumber.setText(invoice.getInvoiceNumber());
            ((UserViewHolder) viewHolder).balance.setText("$ " + Utils.twoDecimalPoint(Double.parseDouble(invoice.getBalance())));
            ((UserViewHolder) viewHolder).netTotal.setText("$ " + Utils.twoDecimalPoint(Double.parseDouble(invoice.getNetTotal())));

         /*   if (Double.parseDouble(invoice.getBalance())==0){
                ((UserViewHolder) viewHolder).status.setText("Paid");
            }else if (Double.parseDouble(invoice.getBalance()) < Double.parseDouble(invoice.getNetTotal())){
                ((UserViewHolder) viewHolder).status.setText("Partial");
            }else if (Double.parseDouble(invoice.getBalance())==Double.parseDouble(invoice.getNetTotal())){
                ((UserViewHolder) viewHolder).status.setText("Open");
            }*/

           /* if (position % 2 ==0){
                ((UserViewHolder) viewHolder).mainCard.setBackgroundColor(Color.parseColor("#f3f3f3"));
            }else {
                ((UserViewHolder) viewHolder).mainCard.setBackgroundColor(Color.parseColor("#ffffff"));
            }*/

            if (invoice.getStatus().equals("C")) {
                //  ((UserViewHolder) viewHolder).statusLayout.setBackgroundResource(R.drawable.invoice_closed);
                // ((UserViewHolder)viewHolder).indicator.setBackgroundResource(R.drawable.invoice_closed);
                ((UserViewHolder) viewHolder).statusLayout.setBackgroundResource(R.drawable.invoice_status_paid);
                ((UserViewHolder) viewHolder).indicator.setBackgroundResource(R.drawable.invoice_status_paid);
                ((UserViewHolder) viewHolder).status.setText("Paid");
            } else if (invoice.getStatus().equals("O")) {
                //((UserViewHolder) viewHolder).statusLayout.setBackgroundResource(R.drawable.invoice_status_partial);
                // ((UserViewHolder)viewHolder).indicator.setBackgroundResource(R.drawable.invoice_status_partial);
                ((UserViewHolder) viewHolder).statusLayout.setBackgroundResource(R.drawable.invoice_closed);
                ((UserViewHolder) viewHolder).indicator.setBackgroundResource(R.drawable.invoice_closed);
                ((UserViewHolder) viewHolder).status.setText("Open");
            } else {
                ((UserViewHolder) viewHolder).statusLayout.setBackgroundResource(R.drawable.invoice_status_paid);
                ((UserViewHolder) viewHolder).indicator.setBackgroundResource(R.drawable.invoice_status_paid);
            }

            ((UserViewHolder) viewHolder).moreOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* callBack.showMoreOption(
                            invoice.getInvoiceNumber(),
                            invoice.getName()
                    );*/
                    // InvoiceListActivity.showInvoiceOption(invoice.getCustomerCode(),invoice.getName(),invoice.getInvoiceNumber());
                    NewExpenseModuleListActivity.showInvoiceOption(
                            invoice.getCustomerCode(),
                            invoice.getName(),
                            invoice.getInvoiceCode(),
                            ((UserViewHolder) viewHolder).status.getText().toString(),
                            invoice.getDate()
                    );
                    // Intent intent=new Intent(mContext, CashCollectionActivity.class);
                    // intent.putExtra("customerCode",invoice.getCustomerCode());
                    // intent.putExtra("customerName",invoice.getName());
                    // mContext.startActivity(intent);
                }
            });

            // Set the LongPress option to Show Values
            ((UserViewHolder) viewHolder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    NewExpenseModuleListActivity.showInvoiceOption(
                            invoice.getCustomerCode(),
                            invoice.getName(),
                            invoice.getInvoiceCode(),
                            ((UserViewHolder) viewHolder).status.getText().toString(),
                            invoice.getDate()
                    );
                    return false;
                }
            });

            ((UserViewHolder) viewHolder).shareOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // NewInvoiceListActivity.viewPdfLayout();
                    //((NewInvoiceListActivity)mContext).viewPdfLayout();
                    try {
                        // getInvoiceDetails(invoice.getInvoiceNumber(),viewHolder,position,invoice,"pdf");
                        ((NewExpenseModuleListActivity) mContext).getInvoicePdf(invoice.getInvoiceCode(), "Share");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            ((UserViewHolder) viewHolder).shareWhatsApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // NewInvoiceListActivity.viewPdfLayout();
                    //((NewInvoiceListActivity)mContext).viewPdfLayout();
                    try {
                        // getInvoiceDetails(invoice.getInvoiceNumber(),viewHolder,position,invoice,"pdf");
                        ((NewExpenseModuleListActivity) mContext).getInvoicePdf(invoice.getInvoiceCode(), "Whatsapp");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            ((UserViewHolder) viewHolder).showHideBottomLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((UserViewHolder) viewHolder).showHideBottomLayout.getTag().equals("hide")) {
                        ((UserViewHolder) viewHolder).bottomLayout.setVisibility(View.VISIBLE);
                        ((UserViewHolder) viewHolder).showHideBottomLayout.setTag("show");
                        ((UserViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_up_24));
                        try {
                            if (invoice.getInvoiceList().size() > 0) {
                                setInvoiceAdapter(viewHolder, position, invoice.getInvoiceList());
                                ((UserViewHolder) viewHolder).progressLayout.setVisibility(View.GONE);
                                ((UserViewHolder) viewHolder).mainLayout.setVisibility(View.VISIBLE);
                            } else {
                                getExpenseDetails(invoice.getInvoiceCode(), viewHolder, position, invoice, "show");
                                invoice.setShow(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        invoice.setShow(false);
                        ((UserViewHolder) viewHolder).bottomLayout.setVisibility(View.GONE);
                        ((UserViewHolder) viewHolder).showHideBottomLayout.setTag("hide");
                        ((UserViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_down_24));
                    }
                }
            });


            if (invoice.isShow()) {
                ((UserViewHolder) viewHolder).bottomLayout.setVisibility(View.VISIBLE);
                ((UserViewHolder) viewHolder).showHideBottomLayout.setTag("show");
                ((UserViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_up_24));
                try {
                    //getReceiptsDetails(receiptsModel.getReceiptNumber(),viewHolder,position,receiptsModel);
                    setInvoiceAdapter(viewHolder, position, invoice.getInvoiceList());
                    ((UserViewHolder) viewHolder).progressLayout.setVisibility(View.GONE);
                    ((UserViewHolder) viewHolder).mainLayout.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ((UserViewHolder) viewHolder).bottomLayout.setVisibility(View.GONE);
                ((UserViewHolder) viewHolder).showHideBottomLayout.setTag("hide");
                ((UserViewHolder) viewHolder).showHideBottomLayout.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_keyboard_arrow_down_24));
            }


        } else if (viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return invoiceList == null ? 0 : invoiceList.size();
    }

    public void setLoaded() {
        isLoading = false;
        callBack.calculateNetTotal(invoiceList);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView date;
        private TextView soNumber;
        private TextView balance;
        private TextView netTotal;
        private TextView status;
        private CardView mainCard;
        private ImageView moreOption;
        private LinearLayout statusLayout;
        private View indicator;
        private RecyclerView productListView;
        private ImageView showHideBottomLayout;
        private LinearLayout mainLayout;
        private LinearLayout progressLayout;
        private CardView bottomLayout;
        private ImageView shareOption;
        private ImageView shareWhatsApp;

        //    private TextView address;
        public UserViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            date = view.findViewById(R.id.date);
            soNumber = view.findViewById(R.id.so_no);
            balance = view.findViewById(R.id.balance);
            status = view.findViewById(R.id.status);
            //  address=view.findViewById(R.id.address);
            netTotal = view.findViewById(R.id.net_total);
            mainCard = view.findViewById(R.id.cardlist_item);
            moreOption = view.findViewById(R.id.more_optionExpense);
            statusLayout = view.findViewById(R.id.status_layout);
            indicator = view.findViewById(R.id.indicator);
            productListView = view.findViewById(R.id.expensList);
            showHideBottomLayout = view.findViewById(R.id.show_hideExp);
            mainLayout = view.findViewById(R.id.main_layout);
            progressLayout = view.findViewById(R.id.progress_layout);
            bottomLayout = view.findViewById(R.id.bottom_layoutExp);
            shareOption = view.findViewById(R.id.share_option);
            shareWhatsApp = view.findViewById(R.id.share_whatsapp);
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

    public interface CallBack {
        void calculateNetTotal(ArrayList<InvoiceModel> invoiceList);

        void showMoreOption(String invoiceId, String customerName);
    }

    public void filterList(ArrayList<InvoiceModel> filterdNames) {
        invoiceList = filterdNames;
        notifyDataSetChanged();
    }

    public ArrayList<InvoiceModel> getNotalInvoiceList() {
        return invoiceList;
    }

    public ArrayList<InvoiceModel> getInvoiceList() {
        return invoiceFilterList;
    }

    private void getExpenseDetails(String invoiceNumber, RecyclerView.ViewHolder viewHolder, int position,
                                   InvoiceModel invoice, String action) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject = new JSONObject();
        // jsonObject.put("CompanyCode",companyId);
        jsonObject.put("InvoiceNo", invoiceNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        String url = Utils.getBaseUrl(mContext) + "APInvoiceDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_urlExpen:", url);
        invoiceshowList = new ArrayList<>();
        ArrayList<InvoicePrintPreviewModel> pdfInvoiceList = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject,
                response -> {
                    try {
                        Log.w("Expense_DetailsRES:", response.toString());

                        if (response.length() > 0) {
                            JSONArray responseData = response.getJSONArray("responseData");
                            JSONObject object = responseData.optJSONObject(0);
                            InvoicePrintPreviewModel model = new InvoicePrintPreviewModel();
                            model.setInvoiceNumber(object.optString("poNo"));
                            model.setInvoiceDate(object.optString("poDate"));
                            model.setCustomerCode(object.optString("vendorCode"));
                            model.setCustomerName(object.optString("vendorName"));
                            //  model.setAddress(response.optString("Address1"));
                            // model.setDeliveryAddress(response.optString("Address1"));
                            model.setSubTotal(object.optString("total"));
                            model.setNetTax(object.optString("taxTotal"));
                            model.setNetTotal(object.optString("netTotal"));
                            model.setTaxType(object.optString("TaxType"));
                            model.setTaxValue(object.optString("TaxPerc"));
                            model.setOutStandingAmount(object.optString("balanceAmount"));
                            model.setBillDiscount(object.optString("BillDIscount"));
                            model.setItemDiscount(object.optString("ItemDiscount"));

                            JSONArray products = object.getJSONArray("purchaseInvoiceDetails");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject detailObject = products.getJSONObject(i);
                                InvoicePrintPreviewModel.InvoiceList invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                                invoiceListModel.setProductCode(detailObject.optString("productCode"));
                                invoiceListModel.setDescription(detailObject.optString("productName"));
                                invoiceListModel.setLqty(detailObject.optString("unitQty"));
                                invoiceListModel.setCqty(detailObject.optString("cartonQty"));
                                invoiceListModel.setNetQty(detailObject.optString("quantity"));
                                invoiceListModel.setNetQuantity(detailObject.optString("netQuantity"));
                                invoiceListModel.setFocQty(detailObject.optString("foc_Qty"));
                                invoiceListModel.setReturnQty(detailObject.optString("returnQty"));
                                invoiceListModel.setCartonPrice(detailObject.optString("cartonPrice"));
                                invoiceListModel.setUnitPrice(detailObject.optString("price"));
                                invoiceListModel.setAccountName(detailObject.optString("accountName"));
                                double qty = Double.parseDouble(detailObject.optString("quantity"));
                                double price = Double.parseDouble(detailObject.optString("price"));

                                double nettotal = qty * price;
                                // invoiceListModel.setTotal(String.valueOf(nettotal));
                                invoiceListModel.setTotal(detailObject.optString("lineTotal"));
                                invoiceListModel.setPricevalue(String.valueOf(price));

                                invoiceListModel.setUomCode(detailObject.optString("uomCode"));
                                invoiceListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
                                invoiceListModel.setItemtax(detailObject.optString("totalTax"));
                                invoiceListModel.setSubTotal(detailObject.optString("subTotal"));
                                invoiceshowList.add(invoiceListModel);
                            }

                            model.setInvoiceList(invoiceshowList);
                            pdfInvoiceList.add(model);
                        }

                        if (invoiceshowList.size() > 0) {
                            ((UserViewHolder) viewHolder).progressLayout.setVisibility(View.GONE);
                            ((UserViewHolder) viewHolder).mainLayout.setVisibility(View.VISIBLE);
                            invoice.setInvoiceList(invoiceshowList);
                            setInvoiceAdapter(viewHolder, position, invoiceshowList);
                        }

                        if (action.equals("pdf")) {
                            ((NewExpenseModuleListActivity) mContext).viewPdfLayout(invoiceNumber, pdfInvoiceList);
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
    }

    public void setInvoiceAdapter(@NonNull RecyclerView.ViewHolder viewHolder, int position, ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList) {
        ((UserViewHolder) viewHolder).productListView.setHasFixedSize(true);
        ((UserViewHolder) viewHolder).productListView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        ExpensePrintPreviewAdapter adapter = new ExpensePrintPreviewAdapter(mContext, invoiceList, "Invoice");
        ((UserViewHolder) viewHolder).productListView.setAdapter(adapter);
        // notifyDataSetChanged();
    }
}