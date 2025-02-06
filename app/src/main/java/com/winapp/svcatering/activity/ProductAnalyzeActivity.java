package com.winapp.svcatering.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.ProductAnalysisAdapter;
import com.winapp.svcatering.model.ProductAnalyzeModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ProductAnalyzeActivity extends AppCompatActivity {

    // Define the variables for using
    private static RecyclerView productView;
    private Button searchButton;
    private EditText fromDate;
    private EditText toDate;
    private static ProductAnalysisAdapter productAnalysisAdapter;
    public static ArrayList<ProductAnalyzeModel.ProductDetails> productList;
    private SessionManager session;
    private HashMap<String,String> user;
    private String companyCode;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private TextView productName;
    private TextView uomText;
    private String product_name;
    private String uom_code;
    private String product_id;
    private String customer_code;
    private String currentDateString;
    private static TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_analyze);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Analyze");

        session=new SessionManager(this);
        user=session.getUserDetails();
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        productView=findViewById(R.id.productDetailsList);
        searchButton=findViewById(R.id.search_btn);
        fromDate=findViewById(R.id.from_date);
        toDate=findViewById(R.id.to_date);
        productName=findViewById(R.id.product_name);
        uomText=findViewById(R.id.uom);
        emptyText=findViewById(R.id.empty_text);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        if (getIntent()!=null){
            product_name=getIntent().getStringExtra("productName");
            uom_code=getIntent().getStringExtra("uom");
            product_id=getIntent().getStringExtra("productId");
            customer_code=getIntent().getStringExtra("customerCode");
            productName.setText(product_name);
            uomText.setText(uom_code);
        }

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        currentDateString = df.format(c);

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(fromDate);
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(toDate);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fromDate.getText().toString().isEmpty() && !toDate.getText().toString().isEmpty()){
                    filterSearch(fromDate.getText().toString(),toDate.getText().toString());
                }else {
                    Toast.makeText(getApplicationContext(),"Select the dates",Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            getProductsDetails(product_id,customer_code);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getProductsDetails(String productCode,String customerCode) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyCode);
        jsonObject.put("ProductCode",productCode);
        jsonObject.put("CustomerCode",customerCode);

       // http://ezysales.sg:457/es/data/api/SalesApi/GetInvoiceByScope?Requestdata={CompanyCode:1,CustomerCode:"00001",ProductCode:"0000406"}
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SalesApi/GetInvoiceByScope?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        productList=new ArrayList<>();
        ProgressDialog progressDialog=new ProgressDialog(ProductAnalyzeActivity.this);
        progressDialog.setMessage("Product Details Loading Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,
                null, response -> {
                    try{
                        Log.w("ProductDetails:",response.toString());
                        progressDialog.dismiss();
                        if (response.length()>0) {
                            for (int i=0;i<response.length();i++){
                                JSONObject object=response.getJSONObject(i);
                                ProductAnalyzeModel.ProductDetails model=new ProductAnalyzeModel.ProductDetails();
                                model.setInvoiceNo(object.optString("InvoiceNo"));
                                model.setQty(object.optString("Qty"));
                                model.setCost(object.optString("Price"));
                                model.setNetPrice(object.optString("NetTotal"));
                                model.setInvoiceDate(object.optString("InvoiceDateString"));
                                if (object.optString("Profit").equals("null")){
                                    model.setProfit("0");
                                }else {
                                    model.setProfit(object.optString("Profit"));
                                }
                                productList.add(model);
                            }
                        }
                        setProductAdapter(productList);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error ->  {
                    progressDialog.dismiss();
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


    public void setProductAdapter(ArrayList<ProductAnalyzeModel.ProductDetails> productList){
        if (productList.size()>0){
            productView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
            productAnalysisAdapter = new ProductAnalysisAdapter(this,productList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            productView.setLayoutManager(linearLayoutManager);
            productView.setAdapter(productAnalysisAdapter);
        }else {
            productView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }
    }

    public static void filterSearch(String fromdate, String todate) {
        try {
            ArrayList<ProductAnalyzeModel.ProductDetails> filterdNames = new ArrayList<>();
            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
            Date from_date=null;
            Date to_date=null;
            try {
                from_date=sdf.parse(fromdate);
                to_date=sdf.parse(todate);
            }  catch (ParseException e) {
                e.printStackTrace();
            }
            for (ProductAnalyzeModel.ProductDetails model: productList){
                Date compareDate=sdf.parse(model.getInvoiceDate());
                if (from_date.equals(to_date)){
                    if (from_date.equals(compareDate)){
                        filterdNames.add(model);
                    }
                    productAnalysisAdapter.filterList(filterdNames);
                } else if(compareDate.compareTo(from_date) >= 0 && compareDate.compareTo(to_date) <= 0) {
                    System.out.println("Compare date occurs after from date");
                    filterdNames.add(model);
                    productAnalysisAdapter.filterList(filterdNames);
                }
                productAnalysisAdapter.filterList(filterdNames);
            }
            Log.w("FilteredSize:",filterdNames.size()+"");
            if (filterdNames.size()>0){
                productView.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.GONE);
            }else {
                productView.setVisibility(View.GONE);
                emptyText.setVisibility(View.VISIBLE);
            }
        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }



    public void getDate(EditText dateEditext){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(ProductAnalyzeActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateEditext.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home){
           finish();
        }
        return super.onOptionsItemSelected(item);
    }


}