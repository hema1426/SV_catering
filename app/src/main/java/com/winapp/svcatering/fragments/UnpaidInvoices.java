package com.winapp.svcatering.fragments;

import static com.winapp.svcatering.activity.NewInvoiceListActivity.isLastSales;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.NewInvoiceListActivity;
import com.winapp.svcatering.adapter.InvoiceAdapter;
import com.winapp.svcatering.adapter.UnPaidInvoiceAdapter;
import com.winapp.svcatering.model.InvoiceModel;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
///Our class extending fragment
public class UnpaidInvoices extends Fragment {

    //Overriden method onCreateView

    private RecyclerView invoiceListView;
    private UnPaidInvoiceAdapter invoiceAdapter;
    private ArrayList<InvoiceModel> invoiceList;
    private SweetAlertDialog pDialog;
    int pageNo=1;
    private SessionManager session;
    private HashMap<String,String > user;
    private String companyId;
    public String InvoiceStatus;
    int paidPageNo=1;
    int unpaidPageNo=1;

    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;
    TextView netTotalText;
    LinearLayout emptyLayout;
    RelativeLayout outstandingLayout;
    public static LinearLayout totalSalesLayout;
    TextView total_sales_valuel;
    boolean isfirstTime;
    TextView titletext;
    View progressLayout;
    public Button createNewInvoice;
    private String locationCode;
    private ImageView loadingImage;
    String username;
    String currentDate="";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes

        View view=inflater.inflate(R.layout.unpaid_invoice_list, container, false);

        invoiceListView =view.findViewById(R.id.unpaidinvoiceList);
        netTotalText=view.findViewById(R.id.net_total_value);
        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        username=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        emptyLayout=view.findViewById(R.id.empty_layout);
        outstandingLayout=view.findViewById(R.id.outstanding_layout);
        totalSalesLayout=view.findViewById(R.id.totalSales_layout);
        total_sales_valuel=view.findViewById(R.id.total_sales_value1);
        progressLayout=view.findViewById(R.id.progress_layout);
        createNewInvoice=view.findViewById(R.id.create_invoice);
        titletext=view.findViewById(R.id.title);
        titletext.setText("Outstanding Invoices Loading....");

        loadingImage=view.findViewById(R.id.loading_image);

        Glide.with(this)
                .load(R.raw.loading_image1)
                .into(loadingImage);

       // setUnPaidInvoices();

        if(isLastSales.equalsIgnoreCase("True")){
            totalSalesLayout.setVisibility(View.VISIBLE);
        }
        else{
            totalSalesLayout.setVisibility(View.INVISIBLE);
        }

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = df1.format(c);
        invoiceList=new ArrayList<>();
        getInvoices(companyId,String.valueOf(pageNo),"ALL",currentDate,currentDate);

        invoiceListView.setHasFixedSize(true);
        invoiceListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        invoiceAdapter=new UnPaidInvoiceAdapter(getActivity(), invoiceListView, invoiceList, new UnPaidInvoiceAdapter.CallBack() {
            @Override
            public void calculateNetTotal(ArrayList<InvoiceModel> invoiceList) {
                setNettotal(invoiceList);
            }
            @Override
            public void showMoreOption(String invoiceId, String customerName) {
                // InvoiceListActivity.showInvoiceOption();
            }
        });
        /*if (invoiceList.size()>0){
            invoiceListView.setVisibility(View.VISIBLE);
            outstandingLayout.setVisibility(View.VISIBLE);
        }else {
            invoiceListView.setVisibility(View.GONE);
            outstandingLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }*/
        invoiceListView.setVisibility(View.VISIBLE);
        //   outstandingLayout.setVisibility(View.VISIBLE);
        invoiceListView.setAdapter(invoiceAdapter);
        /*invoiceAdapter.setOnLoadMoreListener(new UnPaidInvoiceAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                invoiceList.add(null);
                invoiceAdapter.notifyItemInserted(invoiceList.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");
                        //Remove loading item
                        try {
                            invoiceList.remove(invoiceList.size() - 1);
                            invoiceAdapter.notifyItemRemoved(invoiceList.size());
                            //Load data
                            int index = invoiceList.size();
                            int end = index + 20;
                            pageNo=pageNo+1;
                            getInvoices(companyId,String.valueOf(pageNo),"ALL");
                        }catch (Exception ex1){}

                    }
                }, 5000);
            }
        });*/

        createNewInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewInvoiceListActivity.isSearchCustomerNameClicked=true;
                NewInvoiceListActivity.viewCloseBottomSheet();
            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
          //  invoiceList =new ArrayList<>();
           // getInvoices(companyId,"1","UNPAID");
           // setUnPaidInvoices();
        }
        else {
        }
    }


    public void setUnPaidInvoices(){
        try {
            ArrayList<InvoiceModel> invoiceList=InvoiceAdapter.getInvoiceList();
            ArrayList<InvoiceModel> filterList=new ArrayList<>();
            for (InvoiceModel model:invoiceList){
                if (model.getStatus()!=null){
                    if (model.getStatus().equals("Partial") || model.getStatus().equals("Open")){
                        filterList.add(model);
                    }
                }
            }
            setNettotal(filterList);
            setAdapter(filterList);
        }catch (Exception Ex){
        }
    }

    public void setAdapter(ArrayList<InvoiceModel> invoiceList){

        if (invoiceList.size()>0){
            emptyLayout.setVisibility(View.GONE);
            invoiceListView.setVisibility(View.VISIBLE);
            invoiceListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            invoiceAdapter = new UnPaidInvoiceAdapter(getActivity(), invoiceListView, invoiceList, new UnPaidInvoiceAdapter.CallBack() {
                @Override
                public void calculateNetTotal(ArrayList<InvoiceModel> invoiceList) {
                    setNettotal(invoiceList);

                }
                @Override
                public void showMoreOption(String invoiceId, String customerName) {

                }
            });
            invoiceListView.setAdapter(invoiceAdapter);
        }else {
            emptyLayout.setVisibility(View.VISIBLE);
            invoiceListView.setVisibility(View.GONE);
        }
    }


    public void getInvoices(String companyCode,String pageNo,String action,String fromdate,String todate) {
        try {
            Log.w("LoadingAction:",action);
            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            // Initialize a new JsonArrayRequest instance
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("User",username);
            jsonObject.put("LocationCode",locationCode);
            jsonObject.put("CustomerCode","");
            jsonObject.put("FromDate","");
            jsonObject.put("ToDate", "");
            jsonObject.put("DocStatus","");
            String url = Utils.getBaseUrl(getActivity()) + "InvoiceList";
            Log.w("Given_url:", url);
            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Getting All Invoices...");
            pDialog.setCancelable(false);
            if (pageNo.equals("1") && isfirstTime) {
                pDialog.show();
            }
            isfirstTime=false;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    response -> {
                        try {
                            Log.w("Response_is:", response.toString());
                            pDialog.dismiss();
                            if (response.length() > 0) {
                                    String statusCode = response.optString("statusCode");
                                    if (statusCode.equals("1")) {
                                        JSONArray invoiceArray = response.optJSONArray("responseData");
                                        for (int i = 0; i < invoiceArray.length(); i++) {
                                            JSONObject object = invoiceArray.optJSONObject(i);
                                            if (object.optString("invoiceStatus").equals("O")){
                                                InvoiceModel model = new InvoiceModel();
                                                model.setName(object.optString("customerName"));
                                                model.setDate(object.optString("invoiceDate"));
                                                model.setBalance(object.optString("balance"));
                                                model.setInvoiceNumber(object.optString("invoiceNumber"));
                                                // model.setAddress(object.optString("Address1"));
                                                model.setNetTotal(object.optString("netTotal"));
                                                model.setStatus(object.optString("invoiceStatus"));
                                                model.setCustomerCode(object.optString("customerCode"));
                                                model.setInvoiceCode(object.optString("code"));
                                                // isFound=invoiceObject.optString("ErrorMessage");

                                                if (!object.optString("balance").equals("null") && object.optString("balance") != null
                                                        && Double.parseDouble(object.optString("balance")) > 0) {
                                                    ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceLists = new ArrayList<>();
                                                    model.setInvoiceList(invoiceLists);
                                                    invoiceList.add(model);
                                                }
                                            }
                                        }
                                    }
                                    invoiceAdapter.notifyDataSetChanged();
                                    invoiceAdapter.setLoaded();
                            }
                            setShowHide();
                            progressLayout.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                pDialog.dismiss();
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
        } catch (Exception ex) {
        }
    }

    public void setShowHide(){
        if (invoiceList.size()>0){
            invoiceListView.setVisibility(View.VISIBLE);
            outstandingLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }else {
            invoiceListView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            outstandingLayout.setVisibility(View.GONE);
        }
    }


    public void setNettotal(ArrayList<InvoiceModel> invoiceList){
        outstandingLayout.setVisibility(View.VISIBLE);
        double net_amount=0.0;
        double net_total_sales=0.0;
        for (InvoiceModel model:invoiceList){
            net_amount=net_amount+Double.parseDouble(model.getBalance());
            net_total_sales = net_total_sales + Double.parseDouble(model.getNetTotal());

        }
        netTotalText.setText("$ "+ Utils.twoDecimalPoint(net_amount));
        total_sales_valuel.setText("$ "+Utils.twoDecimalPoint(net_total_sales));
    }

}
