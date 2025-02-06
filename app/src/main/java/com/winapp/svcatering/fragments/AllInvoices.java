package com.winapp.svcatering.fragments;

import static com.winapp.svcatering.activity.NewInvoiceListActivity.isLastSales;
import static com.winapp.svcatering.activity.NewInvoiceListActivity.zoneStrCode;

import android.content.Context;
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
import android.widget.TextView;

import androidx.annotation.Nullable;
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
import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.NewInvoiceListActivity;
import com.winapp.svcatering.adapter.InvoiceAdapter;
import com.winapp.svcatering.model.InvoiceModel;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

//Our class extending fragment
public class AllInvoices extends Fragment {

    //Overriden method onCreateView
    public static RecyclerView invoiceListView;
    private static InvoiceAdapter invoiceAdapter;
    private static ArrayList<InvoiceModel> invoiceList=new ArrayList<>();
    private static ArrayList<InvoiceModel> displayInvoiceList=new ArrayList<>();
    private SweetAlertDialog pDialog;
    public int pageNo=1;
    private SessionManager session;
    private HashMap<String,String > user;
    private String companyId;
    public String InvoiceStatus;
    public static LinearLayout emptyLayout;

     public static LinearLayout outstandingLayout;
    public static LinearLayout totalSalesLayout;
    static TextView total_sales_valuel;
    public int paidPageNo=1;
    public int unpaidPageNo=1;
    //This is our tablayout
    private TabLayout tabLayout;
    //This is our viewPager
    private ViewPager viewPager;
    static TextView netTotalText;

    boolean isfirstTime=true;
    TextView titletext;
    View progressLayout;
    ImageView loadingImage;
    String isFound="true";
    String username="";
    String locationCode="";
    String currentDate="";

    public Button createNewInvoice;

    public AllInvoices() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InvoiceStatus = getArguments() != null ? getArguments().getString("ARG_STATUS") : "1";
        Log.w("InvoiceStatus",InvoiceStatus);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes

        View view=inflater.inflate(R.layout.invoices_layout, container, false);

        invoiceListView =view.findViewById(R.id.invoiceList);
        netTotalText=view.findViewById(R.id.net_total_value);
        emptyLayout=view.findViewById(R.id.empty_layout);
        outstandingLayout=view.findViewById(R.id.outstanding_layout);
        totalSalesLayout=view.findViewById(R.id.totalSales_layout);
        total_sales_valuel=view.findViewById(R.id.total_sales_value);
        progressLayout=view.findViewById(R.id.progress_layout);
        titletext=view.findViewById(R.id.title);
        createNewInvoice=view.findViewById(R.id.create_invoice);
        titletext.setText("All Invoices Loading....");
        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        username=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        loadingImage=view.findViewById(R.id.loading_image);

        Glide.with(this)
                .load(R.raw.loading_image1)
                .into(loadingImage);

      /*  if (invoiceList.size()==0 && isfirstTime){
            getInvoices(companyId,String.valueOf(pageNo),"ALL");
        }*/
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = df1.format(c);
        getInvoices(companyId,String.valueOf(pageNo),"ALL",currentDate,currentDate);

        if(isLastSales.equalsIgnoreCase("True")){
            totalSalesLayout.setVisibility(View.VISIBLE);
        }
        else{
            totalSalesLayout.setVisibility(View.INVISIBLE);
        }
        /*if (invoiceList.size()>0){
            invoiceListView.setVisibility(View.VISIBLE);
            outstandingLayout.setVisibility(View.VISIBLE);
        }else {
            invoiceListView.setVisibility(View.GONE);
            outstandingLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }*/

        invoiceListView.setAdapter(invoiceAdapter);

       /* invoiceAdapter.setOnLoadMoreListener(new InvoiceAdapter.OnLoadMoreListener() {
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
                            //getInvoices(companyId,String.valueOf(pageNo),"ALL");
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
            invoiceList=new ArrayList<>();
           // getInvoices(companyId, "1", "PAID");
           // setNettotal(invoiceList);
        }
    }

    public static void setNettotal(ArrayList<InvoiceModel> invoiceList){
//        try {
            double net_amount=0.0;
            double total_sales=0.0;
            double net_total_sales=0.0;
            double balance_amt=0.0;
            for (InvoiceModel model:invoiceList){
                if (model.getBalance()!=null && !model.getBalance().equals("null")){
                    balance_amt=Double.parseDouble(model.getBalance());
                }else {
                    balance_amt=0.0;
                }
                net_amount+=balance_amt;

                net_total_sales = net_total_sales + Double.parseDouble(model.getNetTotal());

            }
            netTotalText.setText("$ "+Utils.twoDecimalPoint(net_amount));
            total_sales_valuel.setText("$ "+Utils.twoDecimalPoint(net_total_sales));
            Log.w("neeess",""+net_amount+".."+net_total_sales);
//        }catch (Exception ex){}

    }

    public void getInvoices(String companyCode,String pageNo,String action,String fromdate,String todate ) {
        try {
            Log.w("LoadingAction:",action);
            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            // Initialize a new JsonArrayRequest instance
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("User","All"); //mahudoom said user set to ALL-only zone based work
//            jsonObject.put("User",username);
//            jsonObject.put("LocationCode",locationCode);
            jsonObject.put("CustomerCode","");
            jsonObject.put("FromDate",fromdate);
            jsonObject.put("ToDate", todate);
            jsonObject.put("DocStatus","");
            jsonObject.put("CostCenterCodeAndName",zoneStrCode);
            jsonObject.put("PhoneNo","");
            String url = Utils.getBaseUrl(getActivity()) + "InvoiceList";
            Log.w("zoneStrCodeaa",""+zoneStrCode);

            Log.w("Given_url_AllInvoices:", url+"/"+jsonObject.toString());
            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Getting All Invoices...");
            pDialog.setCancelable(false);
           /* if (pageNo.equals("1") && isfirstTime) {
                pDialog.show();
            }*/
            isfirstTime=false;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> {
                        try {
                            Log.w("SAP_is_Invoice:", response.toString());
                           // {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"CUS\/686","customerName":"VH FACTORY",
                            // "invoiceNumber":"1","invoiceStatus":"O","invoiceDate":"13\/07\/2021","netTotal":"128.400000","balance":"128.400000",
                            // "totalDiscount":"0.000000","paidAmount":"0.000000","contactPersonCode":"0","createDate":"13\/07\/2021",
                            // "updateDate":"13\/07\/2021","remark":""}]}
                          //  pDialog.dismiss();
                            invoiceList=new ArrayList<>();
                            if (response.length() > 0) {
                                String statusCode=response.optString("statusCode");
                                if (statusCode.equals("1")){
                                    JSONArray invoiceArray=response.optJSONArray("responseData");
                                    for (int i=0;i<invoiceArray.length();i++){
                                        JSONObject object=invoiceArray.optJSONObject(i);
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

                                        ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceLists=new ArrayList<>();
                                        model.setInvoiceList(invoiceLists);
                                        invoiceList.add(model);
                                        displayInvoiceList.add(model);
                                    }
                                }
                              //  invoiceAdapter.notifyDataSetChanged();
                               // invoiceAdapter.setLoaded();
                            }
                            setShowHide();
                            progressLayout.setVisibility(View.GONE);
                            if (invoiceList.size()>0){
                                setNettotal(invoiceList);
                            }
                            setAdapter();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                //pDialog.dismiss();
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
            Log.w("entyylist","");
            invoiceListView.setVisibility(View.VISIBLE);
            outstandingLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }else {
            Log.w("entyylist22","");
            invoiceListView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            outstandingLayout.setVisibility(View.GONE);
        }
    }


    public void setInvoiceAdapter(Context context,ArrayList<InvoiceModel> invoiceList,String invoiceStatus){
        ArrayList<InvoiceModel> filteredList = new ArrayList<>();
       /* for (InvoiceModel model:invoiceList){
            switch (invoiceStatus) {
                case "ALL":
                    filteredList.add(model);
                    break;
                case "PAID":
                    if (Double.parseDouble(model.getBalance()) == 0) {
                        filteredList.add(model);
                    }
                    break;
                case "UNPAID":
                    if (model.getStatus().equals("Partial") || model.getStatus().equals("Open")) {
                        filteredList.add(model);
                    }
                    break;
            }
        }
*/
        invoiceListView.setHasFixedSize(true);
        invoiceListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        invoiceAdapter=new InvoiceAdapter(context, invoiceListView, invoiceList, new InvoiceAdapter.CallBack() {
            @Override
            public void calculateNetTotal(ArrayList<InvoiceModel> invoiceList) {
                setNettotal(invoiceList);
            }
            @Override
            public void showMoreOption(String invoiceId, String customerName,String status) {
                // InvoiceListActivity.showInvoiceOption();
            }
        });
        setShowHide();
        invoiceListView.setAdapter(invoiceAdapter);
        setNettotal(invoiceList);
        NewInvoiceListActivity.isSearchCustomerNameClicked=false;
        NewInvoiceListActivity.selectCustomerId="";
    }


    public void setAdapter(){
        invoiceListView.setHasFixedSize(true);
        invoiceListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        invoiceAdapter=new InvoiceAdapter(getActivity(), invoiceListView, invoiceList, new InvoiceAdapter.CallBack() {
            @Override
            public void calculateNetTotal(ArrayList<InvoiceModel> invoiceList) {
                setNettotal(invoiceList);
            }
            @Override
            public void showMoreOption(String invoiceId, String customerName ,String status) {
                // InvoiceListActivity.showInvoiceOption();
            }
        });
        if (invoiceList.size()>0){
            invoiceListView.setVisibility(View.VISIBLE);
            outstandingLayout.setVisibility(View.VISIBLE);
        }else {
            invoiceListView.setVisibility(View.GONE);
            outstandingLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }

        invoiceListView.setAdapter(invoiceAdapter);

       /* invoiceAdapter.setOnLoadMoreListener(new InvoiceAdapter.OnLoadMoreListener() {
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
        if (invoiceList.size()>0){
            setNettotal(invoiceList);
        }
    }


    public  void filterCancel() {
      //  setFilterAdapter(displayInvoiceList);
        invoiceList=new ArrayList<>();
        getInvoices(companyId,String.valueOf(pageNo),"ALL",currentDate,currentDate);
    }

    public  void filterSearch(Context context,String username, String customerCode, String invoiceStatus,
                              String fromdate, String todate,String location,String zoneStr,String phoneNo) throws JSONException {
       try {
            // Initialize a new RequestQueue instance
          //  http://94.237.70.51:153/es/data/api/SalesApi/GetAllInvoiceList?
            //  Requestdata={"CustomerCode":"","InvoiceNo":"","StartDate":"08/06/2021","EndDate":"08/06/2021","CompanyCode":"10"}
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            // Initialize a new JsonArrayRequest instance
            JSONObject jsonObject=new JSONObject();
           jsonObject.put("User","All"); //mahudoom said user set to ALL-only zone based work
//            jsonObject.put("User",username);
            jsonObject.put("CustomerCode",customerCode);
            jsonObject.put("FromDate",fromdate);
            jsonObject.put("ToDate",todate);
//            jsonObject.put("LocationCode",location);
            jsonObject.put("DocStatus",invoiceStatus);
           jsonObject.put("CostCenterCodeAndName",zoneStr);
           jsonObject.put("PhoneNo",phoneNo);

            String url = Utils.getBaseUrl(context) + "InvoiceList";
            Log.w("AllInvoicesFilter:", url+"/"+jsonObject.toString());
            pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Getting All Invoices...");
            pDialog.setCancelable(false);
            pDialog.show();
           /* if (pageNo.equals("1") && isfirstTime) {
                pDialog.show();
            }*/
            isfirstTime=false;
            invoiceList=new ArrayList<>();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject,
                    response -> {
                        try {
                            Log.w("Invoice_Filter:", response.toString());
                            //  pDialog.dismiss();
                            if (response.length() > 0) {
                                String statusCode=response.optString("statusCode");
                                if (statusCode.equals("1")){
                                    JSONArray invoiceArray=response.optJSONArray("responseData");
                                    for (int i=0;i<invoiceArray.length();i++){
                                        JSONObject object=invoiceArray.optJSONObject(i);
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

                                        ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceLists=new ArrayList<>();
                                        model.setInvoiceList(invoiceLists);
                                        invoiceList.add(model);
                                        displayInvoiceList.add(model);
                                    }
                                }
                               // invoiceAdapter.notifyDataSetChanged();
                               // invoiceAdapter.setLoaded();
                            }
                            pDialog.dismiss();
                          //  setShowHide();
                       //     progressLayout.setVisibility(View.GONE);
                            setInvoiceAdapter(context,invoiceList,invoiceStatus);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                //pDialog.dismiss();
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

        }catch (Exception ex){
           Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }

 /*   public static void filterSearch(String customerName, String invoiceStatus, String fromdate, String todate) {
        try {
            ArrayList<InvoiceModel> filterdNames = new ArrayList<>();
            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
            Date from_date=null;
            Date to_date=null;
            try {
                from_date=sdf.parse(fromdate);
                to_date=sdf.parse(todate);
            }  catch (ParseException e) {
                e.printStackTrace();
            }
            for (InvoiceModel model:InvoiceAdapter.getInvoiceList()){
                Date compareDate=sdf.parse(model.getDate());
                if (from_date.equals(to_date)){
                    if (from_date.equals(compareDate)){
                        if (!customerName.isEmpty()){
                            if (model.getName().toLowerCase().contains(customerName.toLowerCase())) {
                                switch (invoiceStatus) {
                                    case "ALL":
                                        filterdNames.add(model);
                                        break;
                                    case "PAID":
                                        if (Double.parseDouble(model.getBalance()) == 0) {
                                            filterdNames.add(model);
                                        }
                                        break;
                                    case "UNPAID":
                                        if (model.getStatus().equals("Partial") || model.getStatus().equals("Open")) {
                                            filterdNames.add(model);
                                        }
                                        break;
                                }
                                invoiceAdapter.filterList(filterdNames);
                            }
                        }else {
                            switch (invoiceStatus) {
                                case "ALL":
                                    filterdNames.add(model);
                                    break;
                                case "PAID":
                                    if (Double.parseDouble(model.getBalance()) == 0) {
                                        filterdNames.add(model);
                                    }
                                    break;
                                case "UNPAID":
                                    if (model.getStatus().equals("Partial") || model.getStatus().equals("Open")) {
                                        filterdNames.add(model);
                                    }
                                    break;
                            }
                            invoiceAdapter.filterList(filterdNames);
                        }
                    }
                } else if(compareDate.compareTo(from_date) >= 0 && compareDate.compareTo(to_date) <= 0) {
                    System.out.println("Compare date occurs after from date");
                    if (!customerName.isEmpty()){
                        if (model.getName().toLowerCase().contains(customerName.toLowerCase())) {
                            switch (invoiceStatus) {
                                case "ALL":
                                    filterdNames.add(model);
                                    break;
                                case "PAID":
                                    if (Double.parseDouble(model.getBalance()) == 0) {
                                        filterdNames.add(model);
                                    }
                                    break;
                                case "UNPAID":
                                    if (model.getStatus().equals("Partial") || model.getStatus().equals("Open")) {
                                        filterdNames.add(model);
                                    }
                                    break;
                            }
                            invoiceAdapter.filterList(filterdNames);
                        }
                    }else {
                        switch (invoiceStatus) {
                            case "ALL":
                                filterdNames.add(model);
                                break;
                            case "PAID":
                                if (Double.parseDouble(model.getBalance()) == 0) {
                                    filterdNames.add(model);
                                }
                                break;
                            case "UNPAID":
                                if (model.getStatus().equals("Partial") || model.getStatus().equals("Open")) {
                                    filterdNames.add(model);
                                }
                                break;
                        }
                        invoiceAdapter.filterList(filterdNames);
                    }
                }
                invoiceAdapter.filterList(filterdNames);
            }
            Log.w("FilteredSize:",filterdNames.size()+"");
            if (filterdNames.size()>0){
                invoiceListView.setVisibility(View.VISIBLE);
                outstandingLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
                setNettotal(filterdNames);
               // invoiceAdapter.filterList(filterdNames);
            }else {
                invoiceListView.setVisibility(View.GONE);
                outstandingLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            }
        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }
*/
    public void setFilterAdapter(ArrayList<InvoiceModel> invoiceList){
        invoiceListView.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
        outstandingLayout.setVisibility(View.VISIBLE);
        invoiceListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        invoiceAdapter=new InvoiceAdapter(getActivity(), invoiceListView, invoiceList, new InvoiceAdapter.CallBack() {
            @Override
            public void calculateNetTotal(ArrayList<InvoiceModel> invoiceList) {
                setNettotal(invoiceList);
            }
            @Override
            public void showMoreOption(String invoiceId, String customerName ,String status) {
               // InvoiceListActivity.showInvoiceOption();
            }
        });
       /* invoiceAdapter.setOnLoadMoreListener(new InvoiceAdapter.OnLoadMoreListener() {
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
                        invoiceList.remove(invoiceList.size() - 1);
                        invoiceAdapter.notifyItemRemoved(invoiceList.size());

                        //Load data
                        int index = invoiceList.size();
                        int end = index + 20;
                        pageNo=pageNo+1;
                        getInvoices(companyId,String.valueOf(pageNo),"ALL");
                    }
                }, 5000);
            }
        });*/
    }
}