package com.winapp.svcatering.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.SelectCustomerAdapter;
import com.winapp.svcatering.adapter.SortAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CustomerSelectActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private RecyclerView lettersRecyclerview;
    private RecyclerView customerView;
    SortAdapter adapter;
    ArrayList<String> letters;
    private SearchableSpinner PartSpinner;
    ArrayList<String> PartName;
    List<String> PartId;
    public static TextView selectCustomer;
    Button btnCancel;
    TextView customerName;
    BottomSheetBehavior behavior;
    EditText customerNameEdittext;
    SelectCustomerAdapter customerNameAdapter;
    TextView dateText;
    TextView userName;
    SessionManager session;
    HashMap<String,String> user;
    String companyCode;
    String customerId;
    SweetAlertDialog pDialog;
    DBHelper dbHelper;
    ArrayList<CustomerModel> customerList;
    TextView totalCustomers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_select);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        session=new SessionManager(this);
        user=session.getUserDetails();
        dbHelper=new DBHelper(this);
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        customerView=findViewById(R.id.customerList);
        totalCustomers=findViewById(R.id.total_customers);
        getCustomers();
    }

    private void setAdapter(ArrayList<CustomerModel> customerNames) {
        customerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        customerNameAdapter=new SelectCustomerAdapter(this, customerNames, new SelectCustomerAdapter.OnMoreButtonClicked() {
            @Override
            public void moreOption(String customer,String id,String outstanding ,String isFoc) {

            }

            @Override
            public void createInvoice(String customerCode, String customerName, String taxcode,
                                      String taxperc, String taxtype, String billdisc , String isFoc, String mailid) {

            }
        });
        customerView.setAdapter(customerNameAdapter);
        totalCustomers.setText(customerNames.size()+" Customers");
    }


    public void getCustomers(){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"MasterApi/GetCustomer_All?Requestdata={CompanyCode:"+companyCode+"}";
        customerList=new ArrayList<>();
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading Customers...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url, null,
                response -> {
                    try {
                        Log.w("Response_is:", response.toString());
                        pDialog.dismiss();
                        // Loop through the array elements
                        for (int i = 0; i < response.length(); i++) {
                            // Get current json object
                            JSONObject productObject = response.getJSONObject(i);
                            CustomerModel model=new CustomerModel();
                            model.setCustomerCode(productObject.optString("CustomerCode"));
                            model.setCustomerName(productObject.optString("CustomerName"));
                            model.setCustomerAddress(productObject.optString("Address1"));
                            customerList.add(model);
                        }

                        if (customerList.size()>0){
                            setAdapter(customerList);
                        }
                        //populateCategoriesData();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;

         /*   case R.id.action_remove:
                showRemoveAlert();
                break;*/
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}