package com.winapp.svcatering.salesreturn;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.SelectCustomerAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerGroupModel;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.SalesReturnModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.ImageUtil;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SalesReturnList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SalesReturnList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView salesReturnView;
    private static LinearLayout emptyLayout;
    private FrameLayout recyclerLayout;
    private SessionManager session;
    private HashMap<String ,String > user;
    private String companyId;
    private SweetAlertDialog pDialog;
    private int pageNo=1;
    private BottomSheetBehavior behavior;
    private ArrayList<CustomerModel> customerList;
    private SelectCustomerAdapter customerNameAdapter;
    private RecyclerView customerView;
    public  static TextView selectCustomer;
    private Button btnCancel;
    private TextView customerName;
    private EditText customerNameEdittext;
    private DBHelper dbHelper;
    private static TextView netTotalText;
    private ArrayList<CustomerDetails> customerDetails;
    private LinearLayout transLayout;
    private View customerLayout;
    private View receiptsOptions;
    private TextView soCustomerName;
    private TextView srNumber;
    private TextView optionCancel;
    private TextView cancelSheet;
    private Button searchButton;
    private Button cancelSearch;
    private Spinner userListSpinner;
    public static LinearLayout outstandingLayout;
    private View searchFilterView;
    private EditText customerNameText;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private EditText fromDateText;
    private EditText toDateText;
    private boolean isSearchCustomerNameClicked;
    boolean addnewCustomer;
    private String userName;
    private static FrameLayout recyclerViewLayout;
    public static ArrayList<SalesReturnModel> salesReturnList;
    static SalesReturnlistAdapter salesReturnlistAdapter;
    LinearLayout editLayout;
    LinearLayout deleteLayout;
    LinearLayout printPreviewLayout;
    FloatingActionButton editSalesReturn;
    FloatingActionButton deleteSalesReturn;
    FloatingActionButton printPreview;
    String locationCode;
    public static ArrayList<String> searchableCustomerList;
    public static boolean isEdit=false;
    public static String salesReturnNo;
    public String salesReturnCode="";
    View progressLayout;
    Context mContext;
    public static String stockAdjustRefNo;
    public static ArrayList<String> stockAdjustRefNoList;
    private Spinner customerGroupSpinner;
    private ArrayList<CustomerGroupModel> customersGroupList;
    ProgressDialog dialog;
    String currentDate="";


    public void onCreate() {
         mContext = getActivity();
    }

    public SalesReturnList() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SalesReturnList.
     */
    // TODO: Rename and change types and number of parameters
    public static SalesReturnList newInstance(String param1, String param2) {
        SalesReturnList fragment = new SalesReturnList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_sales_return_list,container,false);

        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        dbHelper=new DBHelper(getActivity());

        salesReturnView=view.findViewById(R.id.salesReturnList);
        emptyLayout=view.findViewById(R.id.empty_layout);

        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        customerView=view.findViewById(R.id.customerList);
        netTotalText=view.findViewById(R.id.net_total);
        customerNameEdittext=view.findViewById(R.id.customer_search);
        transLayout=view.findViewById(R.id.trans_layout);
      //  customerDetails=dbHelper.getCustomer();
        customerLayout=view.findViewById(R.id.customer_layout);
        customerGroupSpinner=view.findViewById(R.id.customer_group);
        receiptsOptions =view.findViewById(R.id.receipt_options_layout);
        soCustomerName=view.findViewById(R.id.name);
        srNumber =view.findViewById(R.id.so_no);
        optionCancel=view.findViewById(R.id.option_cancel);
        cancelSheet=view.findViewById(R.id.cancel_sheet);
        fromDateText =view.findViewById(R.id.from_date);
        toDateText =view.findViewById(R.id.to_date);
        userListSpinner =view.findViewById(R.id.user_list_spinner);
        emptyLayout=view.findViewById(R.id.empty_layout);
        outstandingLayout=view.findViewById(R.id.outstanding_layout);
        cancelSearch=view.findViewById(R.id.btn_cancel);
        searchButton=view.findViewById(R.id.btn_search);
        searchFilterView=view.findViewById(R.id.search_filter);
        customerNameText=view.findViewById(R.id.customer_name_value);
        recyclerViewLayout=view.findViewById(R.id.reclerview_layout);
        editLayout=view.findViewById(R.id.edit_layout);
        deleteLayout=view.findViewById(R.id.delete_layout);
        printPreviewLayout=view.findViewById(R.id.print_preview_layout);
        editSalesReturn=view.findViewById(R.id.edit_sr);
        deleteSalesReturn=view.findViewById(R.id.delete_sr);
        printPreview=view.findViewById(R.id.print_preview);
        progressLayout=view.findViewById(R.id.progress_layout);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        fromDateText.setText(formattedDate);
        toDateText.setText(formattedDate);

        stockAdjustRefNoList=new ArrayList<>();

       // getCustomers();

        View bottomSheet = view.findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        transLayout.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        transLayout.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);
            }
        });

        try {
            Date c1 = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c1);
            SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            currentDate = df1.format(c1);
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("User",userName);
            jsonObject.put("CustomerCode","");
            jsonObject.put("FromDate",currentDate);
            jsonObject.put("ToDate", currentDate);
            jsonObject.put("DocStatus","");
            getSalesReturn(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        salesReturnView.setHasFixedSize(true);
        salesReturnView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        salesReturnlistAdapter =new SalesReturnlistAdapter(getActivity(), salesReturnView, salesReturnList, new SalesReturnlistAdapter.CallBack() {
            @Override
            public void calculateNetTotal(ArrayList<SalesReturnModel> receiptsList) {
                setNettotal(receiptsList);
            }
            @Override
            public void showMoreOption(String salesreturn_no, String customerName){
                customerLayout.setVisibility(View.GONE);
                receiptsOptions.setVisibility(View.VISIBLE);
                srNumber.setText(salesreturn_no);
                soCustomerName.setText(customerName);
                customerLayout.setVisibility(View.GONE);
                receiptsOptions.setVisibility(View.VISIBLE);
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        salesReturnView.setAdapter(salesReturnlistAdapter);
       /* salesReturnlistAdapter.setOnLoadMoreListener(new SalesReturnlistAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                salesReturnList.add(null);
                salesReturnlistAdapter.notifyItemInserted(salesReturnList.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");
                        try {
                            //Remove loading item
                            if (salesReturnList!=null){
                                salesReturnList.remove(salesReturnList.size() - 1);
                                salesReturnlistAdapter.notifyItemRemoved(salesReturnList.size());
                                //Load data
                                int index = salesReturnList.size();
                                int end = index + 20;
                                pageNo=pageNo+1;
                            }
                            JSONObject object=new JSONObject();
                            object.put("CompanyCode",companyId);
                            object.put("PageSize","20");
                            object.put("PageNo",pageNo);
                          //  getSalesReturn(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, 5000);
            }
        });*/

        deleteSalesReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveAlert(srNumber.getText().toString());
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveAlert(srNumber.getText().toString());
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        editSalesReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                try {
                    getSalesReturnDetails(srNumber.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                try {
                    getSalesReturnDetails(srNumber.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        printPreviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                Intent intent=new Intent(getActivity(), SalesReturnPrintPreview.class);
                intent.putExtra("srNumber",srNumber.getText().toString());
                startActivity(intent);
            }
        });
        printPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                Intent intent=new Intent(getActivity(), SalesReturnPrintPreview.class);
                intent.putExtra("srNumber",srNumber.getText().toString());
                startActivity(intent);
            }
        });

        optionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        return view;
    }


    public void getCustomersGroups(String groupCode){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url=Utils.getBaseUrl(getActivity()) +"BPGroupList";
        customerList=new ArrayList<>();
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("GroupCode",groupCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("Given_urlcustGroup:",url+jsonObject);
        dialog=new ProgressDialog(getActivity());
        dialog.setMessage("Loading Customers Groups...");
        dialog.setCancelable(false);
        dialog.show();
        customersGroupList=new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        Log.w("SAP_CUSTOMERS_GROUP:", response.toString());
                        String statusCode=response.optString("statusCode");
                        if (statusCode.equals("1")){
                            JSONArray customerDetailArray=response.optJSONArray("responseData");
                            for (int i=0;i<customerDetailArray.length();i++){
                                JSONObject object=customerDetailArray.optJSONObject(i);
                                CustomerGroupModel model = new CustomerGroupModel();
                                model.setCustomerGroupCode(object.optString("groupCode"));
                                model.setCustomerGroupName(object.optString("groupName"));
                                customersGroupList.add(model);
                            }
                        }else {
                            Toast.makeText(getActivity(),"Error,in getting Customer Group list",Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                        if (customersGroupList.size()>0){
                            //setCustomerGroupSpinner(customersGroupList);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    dialog.dismiss();
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


    private void getSalesReturnDetails(String srNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("SalesReturnNo", srNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url= Utils.getBaseUrl(getActivity()) +"SalesReturnDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        stockAdjustRefNoList=new ArrayList<>();
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting SalesReturn Details...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
                    try{
                        Log.w("EditDetails:",response.toString());
                        if (response.length()>0){
                            String statusCode=response.optString("statusCode");
                            if (statusCode.equals("1")){
                                JSONArray salesArray=response.optJSONArray("responseData");
                                assert salesArray != null;
                                JSONObject salesObject=salesArray.optJSONObject(0);
                                String salesorder_no=salesObject.optString("srNumber");
                                String salesorder_code = salesObject.optString("code");
                                String company_code=salesObject.optString("CompanyCode");
                                String customer_code=salesObject.optString("customerCode");
                                String customer_name=salesObject.optString("customerName");
                                String total=salesObject.optString("total");
                                String sub_total=salesObject.optString("subTotal");
                                String bill_discount=salesObject.optString("billDiscount");
                                String item_discount=salesObject.optString("ItemDiscount");
                                String tax=salesObject.optString("taxTotal");
                                String net_total=salesObject.optString("netTotal");
                                String currency_rate=salesObject.optString("CurrencyRate");
                                String currency_name=salesObject.optString("currencyName");
                                String tax_type=salesObject.optString("taxType");
                                String tax_perc=salesObject.optString("taxPerc");
                                String tax_code=salesObject.optString("taxCode");
                                String phone_no=salesObject.optString("DelPhoneNo");
                                String so_date=salesObject.optString("soDate");
                                String signFlag=salesObject.optString("signFlag");
                                if (salesObject.optString("signature")!=null &&
                                        !salesObject.optString("signature").equals("null") && !salesObject.optString("signature").isEmpty()){
                                    String signature = salesObject.optString("signature");
                                    Utils.setSignature(signature);
                                    createSignature();
                                } else {
                                    Utils.setSignature("");
                                }

                                dbHelper.removeCustomer();
                                dbHelper.insertCustomer(
                                        customer_code,
                                        customer_name,
                                        phone_no,
                                        salesObject.optString("address1"),
                                        salesObject.optString("Address2"),
                                        salesObject.optString("Address3"),
                                        salesObject.optString("IsActive"),
                                        salesObject.optString("HaveTax"),
                                        salesObject.optString("taxType"),
                                        salesObject.optString("taxPerc"),
                                        salesObject.optString("taxCode"),
                                        salesObject.optString("CreditLimit"),
                                        "Singapore",
                                        salesObject.optString("currencyCode"));
                                dbHelper.removeAllItems();

                                dbHelper.removeCustomerTaxes();
                                SalesReturnCustomer.customerCodeEdittext.setText(customer_code);
                                SalesReturnCustomer.customerName.setText(currency_name);
                                CustomerDetails model=new CustomerDetails();
                                model.setCustomerCode(customer_code);
                                model.setCustomerName(customer_name);
                                model.setCustomerAddress1(salesObject.optString("address1"));
                                model.setTaxPerc( salesObject.optString("taxPercentage"));
                                model.setTaxType(salesObject.optString("taxType"));
                                model.setTaxCode( salesObject.optString("taxCode"));
                                ArrayList<CustomerDetails> taxList =new ArrayList<>();
                                taxList.add(model);
                                dbHelper.insertCustomerTaxValues(taxList);

                                JSONArray products=salesObject.getJSONArray("salesReturnDetails");
                                for (int i=0;i<products.length();i++) {
                                    JSONObject object = products.getJSONObject(i);

                                    String lqty = "0.0";
                                    String cqty = "0.0";
                                    if (!object.optString("unitQty").equals("null")) {
                                        lqty = object.optString("unitQty");
                                    }

                                    if (!object.optString("quantity").equals("null")) {
                                        cqty = object.optString("quantity");
                                    }

                                    double actualPrice = Double.parseDouble(object.optString("unitPrice"));

                                    dbHelper.insertCart(
                                            object.optString("productCode"),
                                            object.optString("productName"),
                                            cqty,
                                            "0",
                                            object.optString("subTotal"),
                                            "",
                                            object.optString("lineTotal"),
                                            "weight",
                                            String.valueOf(actualPrice),
                                            object.optString("subTotal"),
                                            object.optString("pcsPerCarton"),
                                            object.optString("totalTax"),
                                            object.optString("subTotal"),
                                            object.optString("taxType"),
                                            object.optString("FOCQty"),
                                            "",
                                            object.optString("ExchangeQty"),
                                            "",
                                            "0.00",
                                            object.optString("ReturnQty"),
                                            "",
                                            "",
                                            object.optString("total"),
                                            "",
                                            object.optString("uomCode"),
                                            object.optString("minimumSellingPrice"),"");


                                }
                                    int count = dbHelper.numberOfRows();
                                    if (products.length()==count){
                                    isEdit=true;
                                   // stockAdjustRefNo=object.optString("StockAdjRefCode");
                                    SalesReturnActivity.productButton.performClick();
                                }
                            }
                        }
                        pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            pDialog.dismiss();
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

    private void createSignature() {
        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
            try {
                ImageUtil.saveStamp(mContext, Utils.getSignature(), "Signature");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getCustomers(){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url= Utils.getBaseUrl(getActivity()) +"MasterApi/GetCustomer_All?Requestdata={CompanyCode:"+companyId+"}";
        customerList=new ArrayList<>();
        searchableCustomerList=new ArrayList<>();
        Log.w("Given_url:",url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.w("Response_is:", response.toString());
                        // pDialog.dismiss();
                        // Loop through the array elements
                        for (int i = 0; i < response.length(); i++) {
                            // Get current json object
                            JSONObject productObject = response.getJSONObject(i);
                            CustomerModel model=new CustomerModel();
                            model.setCustomerCode(productObject.optString("CustomerCode"));
                            model.setCustomerName(productObject.optString("CustomerName"));
                            model.setCustomerAddress(productObject.optString("Address1"));
                            customerList.add(model);
                            searchableCustomerList.add(productObject.optString("CustomerName")+" - "+productObject.optString("CustomerCode"));
                        }
                        if (customerList.size()>0){
                            setAdapter(customerList);
//                            ((SalesReturnActivity)getActivity()).setDataToAdapter(searchableCustomerList);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
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
        jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(jsonArrayRequest);
    }

    private void setAdapter(ArrayList<CustomerModel> customerNames) {
        progressLayout.setVisibility(View.GONE);
        customerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        customerNameAdapter = new SelectCustomerAdapter(getActivity(), customerNames, new SelectCustomerAdapter.CallBack() {
            @Override
            public void searchCustomer(String customer,String customername, int pos) {
                customerLayout.setVisibility(View.VISIBLE);
                receiptsOptions.setVisibility(View.GONE);
                int count =dbHelper.numberOfRows();
            }
        });
        customerView.setAdapter(customerNameAdapter);
    }

    public void getSalesReturn(JSONObject jsonObject){

        try {
            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
            // Initialize a new JsonArrayRequest instance
            String url=Utils.getBaseUrl(getActivity()) +"SalesReturnList";
            Log.w("Given_url:",url);
            Log.w("Enter","SalesReturn");
            salesReturnList=new ArrayList<>();
            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Getting Sales Return...");
            pDialog.setCancelable(false);
            pDialog.show();
            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
                                try{
                                    Log.w("Sales_Return_values:",response.toString());
                                    if (response.length()>0) {
                                        String statusCode=response.optString("statusCode");
                                        if (statusCode.equals("1")){
                                            JSONArray detailsArray=response.optJSONArray("responseData");
                                            for (int i = 0; i < detailsArray.length(); i++) {
                                                JSONObject object=detailsArray.getJSONObject(i);
                                                SalesReturnModel model=new SalesReturnModel();
                                                model.setCustomerName(object.optString("customerName"));
                                                model.setSalesReturnNumber(object.optString("srNumber"));
                                                model.setSalesReturnDate(object.optString("srDate"));
                                                model.setBalanceAmount(object.optString("balance"));
                                                model.setNetAmount(object.optString("netTotal"));
                                                model.setSalesReturnCode(object.optString("code"));
                                                model.setLocationCode("");
                                                model.setUser("");
                                                salesReturnList.add(model);
                                            }
                                            salesReturnlistAdapter.notifyDataSetChanged();
                                            salesReturnlistAdapter.setLoaded();
                                            if (salesReturnList.size()>0){
                                                recyclerViewLayout.setVisibility(View.VISIBLE);
                                                emptyLayout.setVisibility(View.GONE);
                                            }else {
                                                recyclerViewLayout.setVisibility(View.GONE);
                                                emptyLayout.setVisibility(View.VISIBLE);
                                            }
                                        }else {
                                            recyclerViewLayout.setVisibility(View.GONE);
                                            emptyLayout.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    pDialog.dismiss();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }, error -> {
                        pDialog.dismiss();
                        // Do something when error occurred
                        Log.w("Error_throwing:",error.toString());
                        Toast.makeText(getActivity(),"Server Error , Please Try again..",Toast.LENGTH_LONG).show();
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
            jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
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
            requestQueue.add(jsonArrayRequest);
        }catch (Exception ex){}
    }

    public void showRemoveAlert(String salesReturnId){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                // .setTitleText("Are you sure?")
                .setContentText("Are you sure want Delete SalesReturn ?")
                .setConfirmText("YES")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        try {
                            sDialog.dismiss();
                            setDeleteSalesReturn(salesReturnId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .showCancelButton(true)
                .setCancelText("No")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }}).show();
    }



    private void setDeleteSalesReturn(String srNumber) throws JSONException {
        // Initialize a new RequestQueue instance

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyId);
        jsonObject.put("SalesReturnNo",srNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url= Utils.getBaseUrl(getActivity()) +"SalesApi/DeleteSR?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Deleting Sales return...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url,
                null,
                response -> {
                    try{
                        Log.w("SaleOrder_Response_is:",response.toString());
                        if (response.length()>0){
                            boolean issaved=response.optBoolean("IsSaved");
                            String result=response.optString("Result");
                            boolean isDeleted=response.optBoolean("IsDeleted");
                            if (result.equals("pass") && isDeleted){
                                getActivity().finish();
                                startActivity(getActivity().getIntent());
                            }else {
                                Toast.makeText(getActivity(),"Error in Deleting SalesOrder",Toast.LENGTH_LONG).show();
                            }
                        }
                        pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            pDialog.dismiss();
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

    public static void filterSearch(String customerName, String location, String fromdate, String todate) {
        try {
            ArrayList<SalesReturnModel> filterdNames = new ArrayList<>();
            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
            Date from_date=null;
            Date to_date=null;
            try {
                from_date=sdf.parse(fromdate);
                to_date=sdf.parse(todate);
            }  catch (ParseException e) {
                e.printStackTrace();
            }
            for (SalesReturnModel model: SalesReturnlistAdapter.getSalesReturnList()){
                Date compareDate=sdf.parse(model.getSalesReturnDate());
                if (from_date.equals(to_date)){
                    if (from_date.equals(compareDate)){
                        if (!customerName.isEmpty()){
                            if (model.getCustomerName().toLowerCase().contains(customerName.toLowerCase())) {
                                if (model.getLocationCode()!=null && !model.getLocationCode().equals("null")){
                                    if (model.getLocationCode().equals(location)){
                                        filterdNames.add(model);
                                    }
                                }
                               salesReturnlistAdapter.filterList(filterdNames);
                            }
                        }else {
                            if (model.getCustomerName().toLowerCase().contains(customerName.toLowerCase())) {
                                if (model.getLocationCode()!=null && !model.getLocationCode().equals("null")){
                                    if (model.getLocationCode().equals(location)){
                                        filterdNames.add(model);
                                    }
                                }
                                salesReturnlistAdapter.filterList(filterdNames);
                            }
                            salesReturnlistAdapter.filterList(filterdNames);
                        }
                    }
                } else if(compareDate.compareTo(from_date) >= 0 && compareDate.compareTo(to_date) <= 0) {
                    System.out.println("Compare date occurs after from date");
                    if (!customerName.isEmpty()){
                        if (model.getCustomerName().toLowerCase().contains(customerName.toLowerCase())) {
                            if (model.getCustomerName().toLowerCase().contains(customerName.toLowerCase())) {
                                if (model.getLocationCode()!=null && !model.getLocationCode().equals("null")){
                                    if (model.getLocationCode().equals(location)){
                                        filterdNames.add(model);
                                    }
                                }
                                salesReturnlistAdapter.filterList(filterdNames);
                            }
                            salesReturnlistAdapter.filterList(filterdNames);
                        }
                    }else {
                        if (model.getCustomerName().toLowerCase().contains(customerName.toLowerCase())) {
                            if (model.getLocationCode()!=null && !model.getLocationCode().equals("null")){
                                if (model.getLocationCode().equals(location)){
                                    filterdNames.add(model);
                                }
                            }
                            salesReturnlistAdapter.filterList(filterdNames);
                        }
                        salesReturnlistAdapter.filterList(filterdNames);
                    }
                }
                salesReturnlistAdapter.filterList(filterdNames);
            }

            Log.w("FilteredSize:",filterdNames.size()+"");

            if (filterdNames.size()>0){
                recyclerViewLayout.setVisibility(View.VISIBLE);
                outstandingLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
                //  setNettotal(filterdNames);
                // invoiceAdapter.filterList(filterdNames);
            }else {
                recyclerViewLayout.setVisibility(View.GONE);
                outstandingLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            }
        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }



    public static void setNettotal(ArrayList<SalesReturnModel> salesReturnList){
        try {
            double net_amount=0.0;
            double balance_amt=0.0;
            for (SalesReturnModel model: salesReturnList){
                if (model.getBalanceAmount()!=null && !model.getBalanceAmount().equals("null")){
                    balance_amt=Double.parseDouble(model.getBalanceAmount());
                }else {
                    balance_amt=0.0;
                }
                net_amount+=balance_amt;
            }
            netTotalText.setText("$ "+Utils.twoDecimalPoint(net_amount));
        }catch (Exception ex){}

    }

}