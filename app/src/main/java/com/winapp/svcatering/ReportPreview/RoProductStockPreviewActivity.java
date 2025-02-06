package com.winapp.svcatering.ReportPreview;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.ReportPreview.adapter.RoProductStockPreviewAdapter;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RoProductStockPreviewActivity extends AppCompatActivity {
    private TextView companyNametext;
    private TextView companyAddress1Text;
    private TextView companyAddress2Text;
    private TextView companyAddress3Text;
    private TextView companyPhoneText;
    private TextView companyGstText;
    private String company_name;
    private String company_address1;
    private String company_address2;
    private String company_address3;
    public SweetAlertDialog pDialog;
    String username = "winapp";
    String companyId = "1";
    private ArrayList<ProductsModel> productsModelList ;
    private TextView fromdatel;
    private TextView todatel;
    private TextView loc_txtl;
    public String fromdatetxt = "27-10-2022";
    public String todatetxt = "27-10-2022";
    public String loccodetxt = "HQ";
    private RecyclerView rv_pdtstocklistl;
    private TextView username_txtl;
    public RoProductStockPreviewAdapter roProductStockPreviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ro_product_stock);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Products Stock");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.w("activity_cg",getClass().getSimpleName().toString());

        companyNametext =findViewById (R.id.company_name);
        companyAddress1Text =findViewById (R.id.company_addr1);
        companyAddress2Text =findViewById (R.id.company_addr2);
        companyAddress3Text =findViewById (R.id.company_addr3);
        companyGstText =findViewById (R.id.company_gst);
        companyPhoneText =findViewById (R.id.company_phone);
        fromdatel = findViewById(R.id.fromdate_txt);
        todatel = findViewById(R.id.todate_txt);
        loc_txtl = findViewById(R.id.loc_txt);
        username_txtl = findViewById(R.id.username_txt);
        rv_pdtstocklistl =  findViewById(R.id.rv_pdtstocklist);

        fromdatel.setText(fromdatetxt);
        todatel.setText(todatetxt);
        loc_txtl.setText(loccodetxt);
        username_txtl.setText(username);

        try {
            getProductList(companyId,loccodetxt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getProductList(String companyCode, String locCode) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("CompanyCode", companyCode);
        jsonObject.put("LocationCode", locCode);
        jsonObject.put("PageSize", "100");
        jsonObject.put("PageNo", "1");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"ProductApi/GetAllCatalogProduct?Requestdata=" + jsonObject.toString();
        Log.w("url_pdtlist:", url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        productsModelList =new ArrayList<>();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.w("pdtlist_res:", response.toString());
                        if (response.length() > 0) {
//
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = response.optJSONObject(i);
                                ProductsModel productsModel = new ProductsModel();
                                productsModel.setProductName(object.optString("ProductName"));
                                productsModel.setProductCode(object.optString("ProductCode"));
                                productsModel.setCartonPrice(object.optString("CartonCost"));
                                productsModel.setStockQty(object.optString("StockQty"));

                                productsModelList.add(productsModel);
                            }
                            if(productsModelList.size()>0){
                                Log.e("pdtstck",""+productsModelList.size());
                                setPdtStockAdapter(productsModelList);
                            }
                            pDialog.dismiss();
                        }

                        pDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            pDialog.dismiss();
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

    private void setPdtStockAdapter(ArrayList<ProductsModel> productsModelList) {
        roProductStockPreviewAdapter = new RoProductStockPreviewAdapter(this, productsModelList,"Product Stock");
        rv_pdtstocklistl.setHasFixedSize(true);
        rv_pdtstocklistl.setLayoutManager(new LinearLayoutManager(this));
        rv_pdtstocklistl.setAdapter(roProductStockPreviewAdapter);
    }
}