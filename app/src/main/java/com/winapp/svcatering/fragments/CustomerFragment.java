package com.winapp.svcatering.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.winapp.svcatering.activity.AddInvoiceActivityOld;
import com.winapp.svcatering.activity.NewInvoiceListActivity;
import com.winapp.svcatering.activity.OrderHistoryActivity;
import com.winapp.svcatering.activity.SalesOrderListActivity;
import com.winapp.svcatering.adapter.SelectCustomerAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.DeliveryAddressModel;
import com.winapp.svcatering.utils.BarCodeScanner;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.SettingUtils;
import com.winapp.svcatering.utils.Utils;

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

import static android.content.Context.MODE_PRIVATE;
import static com.winapp.svcatering.activity.AddInvoiceActivityOld.activityFrom;

public class CustomerFragment extends Fragment {

    public  String customerId;
    private SweetAlertDialog pDialog;
    private ArrayList<CustomerDetails> customerDetails;
    private SessionManager session;
    private HashMap<String,String> user;
    private String companyCode;
    private DBHelper dbHelper;
    private EditText customerCodeEdittext;
    private EditText customerAddress;
    private EditText customerName;
    public static EditText dateEditext;
    public static EditText dueDateEdittext;
    private EditText currencyCode;
    private EditText currencyName;
    private EditText currencyRate;
    private TextView creditLimit;
    private TextView balanceLimit;
    private TextView outstandingLimit;
    public static EditText remarksEditText;
    private Button addProduct;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private JSONObject customerObject;
    private ImageView scanCustomer;
    private View view;
    private FloatingActionButton orderHistroy;
    private BottomSheetBehavior behavior;
    private ImageView selectCustomer;
    private LinearLayout transLayout;
    private ArrayList<CustomerModel> customerList;
    private RecyclerView customerView;
    private Button cancelSheet;
    private SelectCustomerAdapter customerNameAdapter;
    private EditText customerNameEdittext;
    public static JSONObject customerResponse=new JSONObject();
    private ArrayList<CustomerDetails> allCustomersList;
    public static boolean isLoad=false;
    public Spinner deliveryAddressSpinner;
    public ArrayList<DeliveryAddressModel> addressList;
    public String selectedDeliveryAddress="";
    public TextView noofInvoicesText;
    public static EditText orderDateText;
    public static EditText orderNoText;
    public static String tranNo="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_customer,container,false);
        customerId = AddInvoiceActivityOld.customerId;
        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        dbHelper=new DBHelper(getActivity());
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        customerObject=new JSONObject();
        customerCodeEdittext=view.findViewById(R.id.customer_code);
        customerAddress=view.findViewById(R.id.customer_address);
        customerName=view.findViewById(R.id.customer_name_value);
        dateEditext=view.findViewById(R.id.date);
        dueDateEdittext=view.findViewById(R.id.due_date);
        currencyCode=view.findViewById(R.id.currency_code);
        currencyName=view.findViewById(R.id.currency_name);
        currencyRate =view.findViewById(R.id.currency_rate);
        creditLimit=view.findViewById(R.id.credit_limit);
        balanceLimit=view.findViewById(R.id.balance);
        outstandingLimit=view.findViewById(R.id.outstanding);
        remarksEditText=view.findViewById(R.id.remarks);
        addProduct=view.findViewById(R.id.add_product);
        scanCustomer=view.findViewById(R.id.scan_code);
        orderHistroy=view.findViewById(R.id.order_history);
        transLayout=view.findViewById(R.id.trans_layout);
        selectCustomer=view.findViewById(R.id.select_customer);
        customerView=view.findViewById(R.id.customerList);
        cancelSheet=view.findViewById(R.id.cancel_sheet);
        customerNameEdittext=view.findViewById(R.id.customer_search);
        deliveryAddressSpinner=view.findViewById(R.id.delivery_address_spinner);
        noofInvoicesText=view.findViewById(R.id.no_of_invoices);

        orderDateText=view.findViewById(R.id.order_date);
        orderNoText=view.findViewById(R.id.order_no);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        dateEditext.setText(formattedDate);
        dueDateEdittext.setText(formattedDate);
//        Log.w("Given_customer_Id",customerId);
       /* customerDetails=dbHelper.getCustomer();
        if( customerDetails!=null && customerDetails.size()>0){
            setLocalValues(customerDetails);
        }else {
            getCustomerDetails(customerId);
        }*/

        if (activityFrom.equals("InvoiceEdit")
                || activityFrom.equals("SalesEdit")
                || activityFrom.equals("ConvertInvoice")
                || activityFrom.equals("ReOrderInvoice")
                || activityFrom.equals("ReOrderSales")
        ){
            selectCustomer.setVisibility(View.GONE);
        }else {
            selectCustomer.setVisibility(View.GONE);
        }

        if (customerName.getText().toString().isEmpty()){
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
            String selectCustomerId = sharedPreferences.getString("customerId", "");
            if (selectCustomerId != null && !selectCustomerId.isEmpty()){
//            customerDetails=dbHelper.getCustomer(selectCustomerId);
                //setAllValues(customerDetails);
                if (isLoad){
                    getCustomerDetails(selectCustomerId,true);
                }else {
                    getCustomerDetails(selectCustomerId,false);
                }

            }else {
                if (isLoad){
                    getCustomerDetails(customerId,true);
                }else {
                    getCustomerDetails(customerId,false);
                }
            }
        }


        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (AddInvoiceActivityOld.viewPager!=null){
                        AddInvoiceActivityOld.viewPager.setCurrentItem(1);
                    }
                }catch (Exception exception){}
            }
        });

        dateEditext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getDate(dateEditext);
            }
        });

        dueDateEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(dueDateEdittext);
            }
        });
        // Scan the barcode Customer
        scanCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), BarCodeScanner.class);
                startActivity(intent);
            }
        });


        orderHistroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), OrderHistoryActivity.class);
                intent.putExtra("customerName",customerName.getText().toString());
                intent.putExtra("companyCode",companyCode);
                intent.putExtra("customerCode",customerCodeEdittext.getText().toString().trim());
                intent.putExtra("activityFrom", activityFrom);
                startActivity(intent);
            }
        });

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
                        customerNameEdittext.setText("");
                        setAdapter(customerList);
                        Utils.hideKeyboard(getActivity(),customerNameEdittext);
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

        selectCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  customerList=dbHelper.getAllCustomers();
                if (customerList!=null && customerList.size()>0){
                    setAdapter(customerList);
                }else {
                   // getCustomers();
                    new GetCustomersTask().execute();
                }*/
                new GetCustomersTask().execute();
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        orderNoText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateOrderNo(s.toString());
            }
        });


        //  getCustomers();

        customerNameEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                String cusname=editable.toString();
                if (!cusname.isEmpty()){
                    filter(cusname);
                }
            }
        });

        cancelSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });


         deliveryAddressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String deliveryCode=addressList.get(position).getDeliveryAddressCode();
                String deliveryAddress=addressList.get(position).getDeliveryAddress();
                selectedDeliveryAddress=deliveryAddressSpinner.getSelectedItem().toString();
                Log.w("DeliveryAddress:",selectedDeliveryAddress+"");
                if (!deliveryCode.equals("0")){
                    SettingUtils.setDeliveryAddressCode(deliveryCode);
                    SettingUtils.setDeliveryAddress(deliveryAddress);
                }else {
                    SettingUtils.setDeliveryAddressCode("0");
                    SettingUtils.setDeliveryAddress("");
                }
                Log.w("ItemString:",deliveryCode);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    public void updateOrderNo(String text){
        Utils.setOrderNo("");
        Utils.setOrderNo(text);
    }

    private void setDeliveryAddress(String address){
        for (DeliveryAddressModel model:addressList){
            if (model.getDeliveryAddress().equals(address)){
                selectedDeliveryAddress=model.getDeliveryAddress();
                SettingUtils.setDeliveryAddressCode(model.getDeliveryAddressCode());
            }else {
                selectedDeliveryAddress="";
                SettingUtils.setDeliveryAddressCode("0");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void selectValue(ArrayList<DeliveryAddressModel> addressList, String  value) {
        int index=0;
        for (DeliveryAddressModel model:addressList){
            if (model.getDeliveryAddressCode().equals(value)){
                deliveryAddressSpinner.setSelection(index);
                break;
            }
            index++;
        }
       /* for (int i = 0; i < spinner.getCount(); i++) {
            Log.w("ValuesPrinted:",value);
            if (spinner.getItemAtPosition(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }*/
    }

    class GetCustomersTask extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute() {
            super.onPreExecute();
            customerList=new ArrayList<>();
            allCustomersList=new ArrayList<>();
            ProgressDialog progressDialog=new ProgressDialog(getContext());
            progressDialog.setMessage("Customers List Loading...Please wait...");
            progressDialog.setCancelable(false);

        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround", "On doInBackground...");
            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            String url=Utils.getBaseUrl(getActivity()) +"CustomerList";
            Log.w("Given_url:",url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            Log.w("Response_is_Customers:", response.toString());
                            // Loop through the array elements
                            String statusCode=response.optString("statusCode");
                            if (statusCode.equals("1")){
                                JSONArray customerDetailArray=response.optJSONArray("responseData");
                                for (int i=0;i<customerDetailArray.length();i++){
                                    JSONObject object=customerDetailArray.optJSONObject(i);
                                    //  if (customerObject.optBoolean("IsActive")) {
                                    CustomerModel model = new CustomerModel();
                                    model.setCustomerCode(object.optString("customerCode"));
                                    model.setCustomerName(object.optString("customerName"));
                                    model.setAddress1(object.optString("address"));
                                    model.setAddress2(object.optString("street"));
                                    model.setAddress3(object.optString("city"));
                                    model.setCustomerAddress(object.optString("address"));
                                    model.setHaveTax(object.optString("HaveTax"));
                                    model.setTaxType(object.optString("taxType"));
                                    model.setTaxPerc(object.optString("taxPercentage"));
                                    model.setTaxCode(object.optString("taxCode"));
                                    model.setBillDiscPercentage(object.optString("discountPercentage"));
                                    //  model.setCustomerBarcode(object.optString("BarCode"));
                                    // model.setCustomerBarcode(String.valueOf(i));
                                    if (object.optString("outstandingAmount").equals("null") || object.optString("outstandingAmount").isEmpty()) {
                                        model.setOutstandingAmount("0.00");
                                    } else {
                                        model.setOutstandingAmount(object.optString("outstandingAmount"));
                                    }
                                    customerList.add(model);
                                    // }
                                }
                            }else {
                                Toast.makeText(getActivity(),"Error,in getting Customer list",Toast.LENGTH_LONG).show();
                            }
                            dbHelper.removeAllCustomers();
                            //dbHelper.insertCustomerList(customerList);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }, error -> {
               // pDialog.dismiss();
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

            return null;
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //  progressDialog.dismiss();
            if (customerList.size()>0){
                //  dbHelper.insertCustomersDetails(allCustomersList);
                setAdapter(customerList);
            }
        }
    }



    public void getCustomers(){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        String url= Utils.getBaseUrl(getContext()) +"MasterApi/GetCustomer_All?Requestdata={CompanyCode:"+companyCode+"}";
        customerList=new ArrayList<>();
        allCustomersList=new ArrayList<>();
        ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Customers List Loading...Please wait...");
        progressDialog.setCancelable(false);
       // progressDialog.show();
        Log.w("Given_url:",url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.w("Response_is_Customers:", response.toString());
                        // Loop through the array elements
                        for (int i = 0; i < response.length(); i++) {
                            // Get current json object
                            JSONObject productObject = response.getJSONObject(i);
                            CustomerModel model=new CustomerModel();
                            model.setCustomerCode(productObject.optString("CustomerCode"));
                            model.setCustomerName(productObject.optString("CustomerName"));
                            model.setAddress1(productObject.optString("Address1"));
                            model.setHaveTax(customerObject.optString("HaveTax"));
                            model.setTaxType(customerObject.optString("TaxType"));
                            model.setTaxPerc(customerObject.optString("TaxPerc"));
                            model.setTaxCode(customerObject.optString("TaxCode"));
                            if (productObject.optString("BalanceAmount").equals("null") || productObject.optString("BalanceAmount").isEmpty()){
                                model.setOutstandingAmount("0.00");
                            }else {
                                model.setOutstandingAmount(productObject.optString("BalanceAmount"));
                            }
                            customerList.add(model);

                           /* CustomerDetails details=new CustomerDetails();
                            details.setCustomerCode(customerObject.optString("CustomerCode"));
                            details.setCustomerName(customerObject.optString("CustomerName"));
                            details.setPhoneNo(customerObject.optString("PhoneNo"));
                            details.setCustomerAddress1(customerObject.optString("Address1"));
                            details.setCustomerAddress2(customerObject.optString("Address2"));
                            details.setCustomerAddress3(customerObject.optString("Address3"));
                            details.setIsActive(customerObject.optString("IsActive"));
                            details.setHaveTax(customerObject.optString("HaveTax"));
                            details.setTaxType(customerObject.optString("TaxType"));
                            details.setTaxPerc(customerObject.optString("TaxPerc"));
                            details.setTaxCode(customerObject.optString("TaxCode"));
                            details.setCreditLimit(customerObject.optString("CreditLimit"));
                            details.setCountry(customerObject.optString("Country"));
                            details.setCurrencyCode(customerObject.optString("CurrencyCode"));
                            details.setBalanceAmount(customerObject.optString("BalanceAmount"));

                            allCustomersList.add(details);*/
                        }
                      //  progressDialog.dismiss();
                        if (customerList.size()>0){
                            //new InsertCustomerTask().execute();
                          //  dbHelper.insertCustomersDetails(allCustomersList);
                            setAdapter(customerList);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
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

    private class InsertCustomerTask extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround", "On doInBackground...");
            dbHelper.removeAllCustomers();
            dbHelper.insertCustomerList(customerList);
            return "You are at PostExecute";
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private void setAdapter(ArrayList<CustomerModel> customerNames) {
        customerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        customerNameAdapter = new SelectCustomerAdapter(getContext(), customerNames, new SelectCustomerAdapter.CallBack() {
            @Override
            public void searchCustomer(String customer,String customername, int pos) {
                int count=dbHelper.numberOfRows();
                Log.w("No_ofCounts:",count+"");
                if (count>0){
                    showCustomerAlert(customer);
                }else {
                    if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                    AddInvoiceActivityOld.customerId=customer;
                    if (customer!=null && !customer.isEmpty() && customer.equals("null")){
                        customerDetails=dbHelper.getCustomer(customer);
                        setAllValues(customerDetails);
                        setCustomerDetails(customer);
                    }else {
                        setCustomerDetails(customer);
                        getCustomerDetails(customer,true);
                    }
                }
            }
        });
        customerView.setAdapter(customerNameAdapter);
    }

    public void setCustomerDetails(String customerId){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
        SharedPreferences.Editor customerPredEdit= sharedPreferences.edit();
        customerPredEdit.putString("customerId", customerId);
        customerPredEdit.apply();
    }

    public void showCustomerAlert(String customer){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Warning...!");
        builder.setMessage("Products Will be removed from the cart");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                dbHelper.removeAllItems();
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                AddInvoiceActivityOld.customerId=customer;
                setCustomerDetails(customer);
                customerDetails=dbHelper.getCustomer(customer);
                getCustomerDetails(customer,true);
             //   setAllValues(customerDetails);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void filter(String text) {
        try {
            //new array list that will hold the filtered data
            ArrayList<CustomerModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (CustomerModel s : customerList) {
                //if the existing elements contains the search input
                if (s.getCustomerName().toLowerCase().contains(text.toLowerCase()) || s.getCustomerCode().toLowerCase().contains(text.toLowerCase())) {
                    //adding the element to filtered list
                    filterdNames.add(s);
                }
            }
            //calling a method of the adapter class and passing the filtered list
            customerNameAdapter.filterList(filterdNames);
        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }

    private void getCustomerDetails(String customerCode,boolean isloader) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        // Initialize a new JsonArrayRequest instance
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("CustomerCode",customerCode);
           // jsonObject.put("CompanyCode",companyCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url= Utils.getBaseUrl(getContext()) +"Customer";
        Log.w("Given_url:",url);
        ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Customer Details Loading...");
        if (isloader){
            progressDialog.show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try{
                        Log.w("SAP-response_customer:",response.toString());
                      //  {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"CUS\/686","customerName":"VH FACTORY",
                        //  "groupCode":"100","contactPerson":"","creditLimit":"150.000000","currencyCode":"SGD","currencyName":"Singapore Dollar",
                        //  "taxType":"","taxCode":"SR","taxName":"Sales Standard Rated Supplier SR","taxPercentage":"7.000000","balance":"21.600000",
                        //  "outstandingAmount":"128.400000","address":"","street":"","city":"","state":"","zipCode":"","country":"",
                        //  "createDate":"13\/07\/2021","updateDate":"30\/07\/2021","active":"N","remark":""}]}
                        if (response.length()>0) {
                            if (response.optString("statusCode").equals("1")){
                                SummaryFragment.customerResponse=response;
                                CustomerFragment.customerResponse=response;
                                customerObject=response;
                                setAllValues(response);
                                progressDialog.dismiss();
                            }else {
                                Toast.makeText(getActivity(),"Error in getting response",Toast.LENGTH_LONG).show();
                            }
                        }
                        // pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
                    showAlertDialog();
            // Do something when error occurred
            //  pDialog.dismiss();
            Log.w("Error_throwing:",error.toString());
            progressDialog.dismiss();
            // Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
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

    public void showAlertDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Warning..!");
        builder.setMessage("Customer Information is not valid,Please try again..");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (activityFrom.equals("InvoiceEdit")
                        || activityFrom.equals("ReOrderInvoice")
                        || activityFrom.equals("Invoice")
                ){
                    Intent intent=new Intent(getActivity(), NewInvoiceListActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                }else if (activityFrom.equals("SalesOrder") || activityFrom.equals("SalesEdit") || activityFrom.equals("ReOrderSales") || activityFrom.equals("ConvertInvoice")){
                    Intent intent=new Intent(getActivity(), SalesOrderListActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                }
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
      //  getActivity().getWindow().setBackgroundDrawableResource(R.color.primaryDark);
    }


    private void setAllValues(JSONObject customerObject) {
      //  {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"CUS\/686","customerName":"VH FACTORY","groupCode":"100",
        //  "contactPerson":"","creditLimit":"150.000000","currencyCode":"SGD","currencyName":"Singapore Dollar","taxType":"","taxCode":"SR",
        //  "taxName":"Sales Standard Rated Supplier SR","taxPercentage":"7.000000","balance":"21.600000","outstandingAmount":"128.400000",
        //  "address":"","street":"","city":"","state":"","zipCode":"","country":"","createDate":"13\/07\/2021","updateDate":"30\/07\/2021",
        //  "active":"N","remark":""}]}

        JSONArray detailsArray=customerObject.optJSONArray("responseData");
        JSONObject object=detailsArray.optJSONObject(0);

        customerName.setText(object.optString("customerName"));
        customerCodeEdittext.setText(object.optString("customerCode"));
       // customerAddress.setText(customerObject.optString("Address1")+","+customerObject.optString("Address2")+","+customerObject.optString("Address3"));
        customerAddress.setText(object.optString("address"));
        noofInvoicesText.setText("No Of Invoices : "+object.optString("outstandingInvoiceCount"));
        if (object.optString("currencyCode").equals("null")){
            currencyCode.setText("SGD");
        }else {
            currencyCode.setText(object.optString("currencyCode"));
        }
        //currencyRate.setText(object.optString("CurrencyRate"));
        currencyRate.setText("1.000000");
        currencyName.setText(object.optString("currencyName"));
        if (object.optString("CreditLimit").equals("null")){
            creditLimit.setText("0.00");
        }else {
            creditLimit.setText(Utils.twoDecimalPoint(Double.parseDouble(object.optString("creditLimit"))));
        }
        if (object.optString("outstandingAmount").equals("null")){
            balanceLimit.setText("0.00");
            outstandingLimit.setText("0.00");
        }else {
            balanceLimit.setText(Utils.twoDecimalPoint(Double.parseDouble(object.optString("balance"))));
            outstandingLimit.setText(Utils.twoDecimalPoint(Double.parseDouble(object.optString("outstandingAmount"))));
        }

        dbHelper.removeCustomerTaxes();
        CustomerDetails model=new CustomerDetails();
        model.setCustomerCode(object.optString("customerCode"));
        model.setCustomerName(object.optString("customerName"));
        model.setCustomerAddress1(object.optString("address"));
        model.setTaxPerc(object.optString("taxPercentage"));
        model.setTaxType(object.optString("taxType"));
        model.setTaxCode(object.optString("taxCode"));
        ArrayList<CustomerDetails> taxList =new ArrayList<>();
        taxList.add(model);
        dbHelper.insertCustomerTaxValues(taxList);

        if (AddInvoiceActivityOld.order_no!=null && !AddInvoiceActivityOld.order_no.isEmpty()){
            orderNoText.setText(AddInvoiceActivityOld.order_no);
        }else {
            orderNoText.setText("");
        }

        remarksEditText.setText(object.optString("remark"));
        isLoad=false;

       // setCustomerDeliveryAddress(customerObject);
    }

    private void setCustomerDeliveryAddress(JSONObject customerObject){
        JSONArray addressArray=customerObject.optJSONArray("AllCustomerAddress");
        addressList=new ArrayList<>();
        for (int i=0;i<addressArray.length();i++){
            JSONObject object=addressArray.optJSONObject(i);
            if (object.optString("Address1")!=null && !object.optString("Address1").equals("null")){
                DeliveryAddressModel model=new DeliveryAddressModel();
                model.setDeliveryAddressCode(object.optString("DeliveryCode"));
                model.setDeliveryAddress(object.optString("Address1"));
                addressList.add(model);
            }
        }



        ArrayAdapter<DeliveryAddressModel> adapter=new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,addressList);
        deliveryAddressSpinner.setAdapter(adapter);

        if (SettingUtils.getDeliveryAddress()!=null && !SettingUtils.getDeliveryAddress().isEmpty()){
            selectValue(addressList,SettingUtils.getDeliveryAddressCode());
        }
       // deliveryAddressSpinner.setSelection(0, true);
     //   View v = deliveryAddressSpinner.getSelectedView();
      //  ((TextView)v).setTextSize(12F);
        //Set the listener for when each option is clicked.
    }

    private void setAllValues(ArrayList<CustomerDetails> customerObjectList) {

        customerName.setText(customerObjectList.get(0).getCustomerName());
        customerCodeEdittext.setText(customerObjectList.get(0).getCustomerCode());
       // customerAddress.setText(customerObjectList.get(0).getCustomerAddress1()+","+ customerObjectList.get(0).getCustomerAddress2()+","+ customerObjectList.get(0).getCustomerAddress3());
        customerAddress.setText(customerObjectList.get(0).getCustomerAddress1());

        if (customerObjectList.get(0).getCurrencyCode().equals("null")){
            currencyCode.setText("SGD");
        }else {
            currencyCode.setText(customerObjectList.get(0).getCurrencyCode());
        }
        currencyRate.setText("1.0000");
        currencyName.setText("Singapore Dollar");
        if (customerObjectList.get(0).getCreditLimit().equals("null")){
            creditLimit.setText("0.00");
        }else {
            creditLimit.setText(customerObjectList.get(0).getCreditLimit());
        }
        if (customerObjectList.get(0).getBalanceAmount()!=null && !customerObjectList.get(0).getBalanceAmount().equals("null")){
            balanceLimit.setText(customerObjectList.get(0).getBalanceAmount());
            outstandingLimit.setText(customerObjectList.get(0).getBalanceAmount());
        }else {
            balanceLimit.setText("0.00");
            outstandingLimit.setText("0.00");
        }

        dbHelper.removeCustomerTaxes();
        CustomerDetails model=new CustomerDetails();
        model.setCustomerCode(customerObjectList.get(0).getCustomerCode());
        model.setCustomerName(customerObjectList.get(0).getCustomerName());
        model.setCustomerAddress1(customerObjectList.get(0).getCustomerAddress1());
        model.setTaxPerc(customerObjectList.get(0).getTaxPerc());
        model.setTaxType(customerObjectList.get(0).getTaxType());
        model.setTaxCode(customerObjectList.get(0).getTaxCode());
        ArrayList<CustomerDetails> taxList =new ArrayList<>();
        taxList.add(model);
        dbHelper.insertCustomerTaxValues(taxList);

        remarksEditText.setText(customerObjectList.get(0).getRemarks());
    }

    public void getDate(EditText dateEditext){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateEditext.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }
}