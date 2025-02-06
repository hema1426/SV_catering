package com.winapp.svcatering.salesreturn;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.SelectCustomerAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerGroupModel;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.utils.BarCodeScanner;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
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

public class SalesReturnCustomer extends Fragment {

    public  String customerId;
    private SweetAlertDialog pDialog;
    private ArrayList<CustomerDetails> customerDetails;
    private SessionManager session;
    private HashMap<String,String> user;
    private String companyCode;
    private DBHelper dbHelper;
    public static EditText customerCodeEdittext;
    public static EditText customerName;
    public static EditText dueDateEdittext;
    public static EditText remarksEditText;
    private Button addProduct;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private JSONObject customerObject;
    private ImageView scanCustomer;
    View view;
    private ArrayList<CustomerModel> customerList;
    private SelectCustomerAdapter customerNameAdapter;
    private RecyclerView customerView;
    private static BottomSheetBehavior behavior;
    private EditText customerNameEdittext;
    private TextView cancelSheet;
    private LinearLayout transLayout;
    private ImageView selectCustomer;
    View progressLayout;
    private ArrayList<CustomerDetails> allCustomersList;
    private String selectCustomerId;
    ProgressDialog dialog;
    private SharedPreferences sharedPreferences;
    private Spinner customerGroupSpinner;
    private ArrayList<CustomerGroupModel> customersGroupList;
    private String username;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.sales_return_customer,container,false);
        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        dbHelper=new DBHelper(getActivity());
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        username=user.get(SessionManager.KEY_USER_NAME);
        customerObject=new JSONObject();
        customerCodeEdittext=view.findViewById(R.id.customer_code);
        customerName=view.findViewById(R.id.customer_name_value);
        dueDateEdittext=view.findViewById(R.id.due_date);
        remarksEditText=view.findViewById(R.id.remarks);
        addProduct=view.findViewById(R.id.add_product);
        scanCustomer=view.findViewById(R.id.scan_code);
        customerView=view.findViewById(R.id.customerList);
        customerNameEdittext=view.findViewById(R.id.customer_search);
        cancelSheet=view.findViewById(R.id.cancel_sheet);
        transLayout=view.findViewById(R.id.trans_layout);
        selectCustomer=view.findViewById(R.id.select_customer);
        customerGroupSpinner=view.findViewById(R.id.customer_group);
        progressLayout=view.findViewById(R.id.progress_layout);

        if (SalesReturnList.isEdit){
            selectCustomer.setEnabled(false);
        }else {
            selectCustomer.setEnabled(true);
        }

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        dueDateEdittext.setText(formattedDate);

        sharedPreferences = getActivity().getSharedPreferences("customerPref",MODE_PRIVATE);
        selectCustomerId = sharedPreferences.getString("customerId", "");
        if (selectCustomerId!=null && !selectCustomerId.isEmpty()){
            //getCustomerDetails(selectCustomerId);
            customerDetails=dbHelper.getCustomer(selectCustomerId);
            int size=customerDetails.size();
            Log.w("Size of the customer:",+size+"");
            if (customerDetails.size()>0){
                customerName.setText(customerDetails.get(0).getCustomerName());
                customerCodeEdittext.setText(customerDetails.get(0).getCustomerCode());
            }
        }

       /* customerList=dbHelper.getAllCustomers();
        if (customerList!=null && customerList.size()>0){
            setAdapter(customerList);
        }else {
            getCustomers();
        }*/

        //getCustomers();

        try {
            getCustomersGroups(username);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!customerName.getText().toString().isEmpty() && !customerCodeEdittext.getText().toString().isEmpty()){
                //    ((SalesReturnActivity)getActivity()).loadFragment(new SalesReturnProduct());
                    SalesReturnActivity.productButton.performClick();
                }else {
                    Toast.makeText(getActivity(),"Please select Customer first",Toast.LENGTH_SHORT).show();
                }
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
                viewCloseBottomSheet();
            }
        });

        selectCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        return view;
    }


    public void getCustomersGroups(String username){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url=Utils.getBaseUrl(getContext()) +"BPGroupList";
        customerList=new ArrayList<>();
        allCustomersList=new ArrayList<>();
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("User",username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("Given_urlcustGroup:",url+jsonObject);
        dialog=new ProgressDialog(getContext());
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
                            Toast.makeText(getContext(),"Error,in getting Customer Group list",Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                        if (customersGroupList.size()>0){
                            setCustomerGroupSpinner(customersGroupList);
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

    public void setCustomerGroupSpinner(ArrayList<CustomerGroupModel> customersGroupList){
        ArrayAdapter<CustomerGroupModel> adapter=new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,customersGroupList);
        customerGroupSpinner.setAdapter(adapter);
        customerGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String groupCode=customersGroupList.get(position).getCustomerGroupCode();
                String groupName=customersGroupList.get(position).getCustomerGroupName();
                getCustomers(groupCode);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void getCustomers(String groupCode){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url=Utils.getBaseUrl(getContext()) +"CustomerList";
        customerList=new ArrayList<>();
        allCustomersList=new ArrayList<>();
        Log.w("Given_url:",url);
        ProgressDialog dialog=new ProgressDialog(getContext());
        dialog.setMessage("Loading Customers List...");
        dialog.setCancelable(false);
        dialog.show();
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("GroupCode",groupCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        Log.w("Response_SAP_CUSTOMERS:", response.toString());

                        // {"statusCode":1,"statusMessage":"Success",
                        // "responseData":[{"customerCode":"CUS\/686","customerName":"VH FACTORY","groupCode":"100",
                        // "contactPerson":"","creditLimit":"150.000000","currencyCode":"SGD","currencyName":"Singapore Dollar",
                        // "taxType":"","taxCode":"SR","taxName":"Sales Standard Rated Supplier SR","taxPercentage":"7.000000",
                        // "balance":"21.600000","outstandingAmount":"128.400000","address":"","street":"","city":"","state":"",
                        // "zipCode":"","country":"","createDate":"13\/07\/2021","updateDate":"30\/07\/2021","active":"N","remark":""},
                        // {"customerCode":"WinApp","customerName":"WinApp","groupCode":"100","contactPerson":"","creditLimit":"0.000000"
                        // ,"currencyCode":"SGD","currencyName":"Singapore Dollar","taxType":"I","taxCode":"","taxName":"","taxPercentage":"",
                        // "balance":"0.000000","outstandingAmount":"0.000000","address":"","street":"","city":"","state":"","zipCode":""
                        // ,"country":"","createDate":"29\/07\/2021","updateDate":"30\/07\/2021","active":"N","remark":""}]}

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
                            Toast.makeText(getContext(),"Error,in getting Customer list",Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                        if (customerList.size()>0){
                            // new InsertCustomerTask().execute();
                            setAdapter(customerList);
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


    private void setAdapter(ArrayList<CustomerModel> customerNames) {
        progressLayout.setVisibility(View.GONE);
        customerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        customerNameAdapter = new SelectCustomerAdapter(getActivity(), customerNames, new SelectCustomerAdapter.CallBack() {
            @Override
            public void searchCustomer(String customer_code, String customername, int pos) {
                // Set the Customer name and Code to the Edittext
                int size =dbHelper.numberOfRows();
                if (size > 0){
                    setProductDeleteAlert(customername,customer_code);
                }else {
                    customerName.setText(customername);
                    customerCodeEdittext.setText(customer_code);
                    viewCloseBottomSheet();
                    //getCustomerDetails(customer_code);
                    Utils.setCustomerSession(getActivity(),customer_code);
                    getCustomerDetails(customer_code,true);
                }
            }
        });
        customerView.setAdapter(customerNameAdapter);
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
        Log.w("GetCustomerDetailsURL:",url);
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
                        if (response.length()>0) {
                            if (response.optString("statusCode").equals("1")){
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

    public void setProductDeleteAlert(String customername,String customer_code){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(requireActivity());
        builder1.setTitle("Information!");
        builder1.setMessage("Selected Products will be removed.");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbHelper.removeAllItems();
                        customerName.setText(customername);
                        customerCodeEdittext.setText(customer_code);
                        viewCloseBottomSheet();
                        getCustomerDetails(customer_code,true);
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        viewCloseBottomSheet();
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
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

    public static void viewCloseBottomSheet(){
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        // get the Customer name from the local db
    }



    private void setAllValues(JSONObject customerObject) {
       // customerName.setText(customerObject.optString("CustomerName"));
      //  customerCodeEdittext.setText(customerObject.optString("CustomerCode"));
       // remarksEditText.setText(customerObject.optString("Remarks"));
       // Utils.setCustomerSession(requireContext(),customerObject.optString("CustomerCode"));

        JSONArray detailsArray=customerObject.optJSONArray("responseData");
        JSONObject object=detailsArray.optJSONObject(0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        Utils.setCurrentDateTime(currentDateandTime);

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
    }

    private void setLocalValues(ArrayList<CustomerDetails> customerModels){
        customerName.setText(customerModels.get(0).getCustomerName());
        customerCodeEdittext.setText(customerModels.get(0).getCustomerCode());
        remarksEditText.setText(customerObject.optString("Remarks"));
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
        datePickerDialog.show();
    }

}