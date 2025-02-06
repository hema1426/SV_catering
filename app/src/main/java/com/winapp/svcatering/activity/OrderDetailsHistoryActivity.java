package com.winapp.svcatering.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.OrderDetailsAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OrderDetailsHistoryActivity extends AppCompatActivity {

    private TextView orderDate;
    private TextView orderTime;
    private TextView customerName;
    private TextView orderId;
    private TextView orderStatus;
    private TextView paidAmount;
    private TextView dueAmount;
    private TextView dueDays;
    private TextView totalOrderText;
    private TextView totalOutstanding;
    private RecyclerView orderDetailsView;
    private Button btnReorder;
    private ArrayList<OrderDetailsAdapter.OrderDetailsModel> orderDetailsList;
    private OrderDetailsAdapter orderDetailsAdapter;
    private SweetAlertDialog pDialog;
    private String orderNumber;
    private String activityFrom;
    private String invoiceNumber;
    private String salesOrderNumber;
    private SessionManager session;
    private HashMap<String,String > user;
    private SharedPreferences sharedPref_billdisc;

    private String companyId;
    private String locationCode;
    private DBHelper dbHelper;
    private SharedPreferences.Editor myEdit;

    private String userName;
    private String order_date;
    private String customer_name;
    private String paid_amount;
    private String due_amount;
    private String order_status;
    private double net_orderAmt=0.0;
    private boolean isQtyCheck=false;
    private CheckBox checkAllProducts;
    private String customer_code;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Details");
        dbHelper=new DBHelper(this);
        session=new SessionManager(this);
        user=session.getUserDetails();

        sharedPref_billdisc = getSharedPreferences("BillDiscPref", MODE_PRIVATE);
        myEdit = sharedPref_billdisc.edit();

        Log.w("activity_cg",getClass().getSimpleName().toString());

        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);
        locationCode=user.get((SessionManager.KEY_LOCATION_CODE));
        orderDate=findViewById(R.id.order_date);
        orderTime=findViewById(R.id.order_time);
        customerName=findViewById(R.id.customer_name_value);
        orderId=findViewById(R.id.order_id);
        orderStatus=findViewById(R.id.order_status);
        paidAmount=findViewById(R.id.paid_amount);
        dueAmount=findViewById(R.id.due_amount);
        dueDays=findViewById(R.id.due_days);
        totalOrderText =findViewById(R.id.total_orders);
        totalOutstanding=findViewById(R.id.total_outstanding);
        orderDetailsView =findViewById(R.id.orderListDetailsView);
        btnReorder=findViewById(R.id.btn_reorder);
        checkAllProducts=findViewById(R.id.check_all_products);

        if (getIntent()!=null){
            orderNumber=getIntent().getStringExtra("orderNumberHis");
            activityFrom=getIntent().getStringExtra("activityFrom");
            order_date=getIntent().getStringExtra("orderDateHis");
            customer_name=getIntent().getStringExtra("customerNameHis");
            customer_code=getIntent().getStringExtra("customerCodeHis");
            paid_amount=getIntent().getStringExtra("paidAmountHis");
            due_amount=getIntent().getStringExtra("dueAmountHis");
           // order_status=getIntent().getStringExtra("orderStatus");
            getSupportActionBar().setTitle(customer_name);
            orderId.setText(orderNumber);
            orderDate.setText(order_date);
            customerName.setText(customer_name);
            paidAmount.setText(paid_amount);
            dueAmount.setText(due_amount);
            orderStatus.setText(order_status);
//            if (order_status.equals("Open")){
//                orderStatus.setTextColor(Color.parseColor("#229954"));
//            }else {
//                orderStatus.setTextColor(Color.parseColor("#5DADE2"));
//            }


            if (activityFrom.equals("SalesOrder")){
                salesOrderNumber=orderNumber;
                try {
                    getSODetails(salesOrderNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                invoiceNumber=orderNumber;
                try {
                    getInvoiceDetails(invoiceNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        btnReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (net_orderAmt!=0.0){
                    showReorderConfirmDialog();
                }else {
                    Toast.makeText(getApplicationContext(),"Please add product to reorder",Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkAllProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAllProducts.isChecked()){
                    Log.w("checkpdt1.","");
                    checkAllProducts.setText("Uncheck All Products");
                    int index=0;
                    showDialog();
                    for (OrderDetailsAdapter.OrderDetailsModel model : orderDetailsList){
                        model.setProductCheck(true);
                        insertOrderProducts(true,model);
                        if(index==orderDetailsList.size()-1){
                            closeDialog();
                        }
                        index++;
                    }
                    orderDetailsAdapter.notifyDataSetChanged();
                    setNetTotal(orderDetailsList);
                }else {
                    Log.w("checkpdt2.","");

                    checkAllProducts.setText("Check All Products");
                    for (OrderDetailsAdapter.OrderDetailsModel model : orderDetailsList){
                        model.setProductCheck(false);
                        insertOrderProducts(false,model);
                    }
                    orderDetailsAdapter.notifyDataSetChanged();
                    setNetTotal(orderDetailsList);
                }
            }
        });


       /* checkAllProducts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    checkAllProducts.setText("Uncheck All Products");
                    for (OrderDetailsAdapter.OrderDetailsModel model : orderDetailsList){
                        model.setProductCheck(true);
                    }
                    orderDetailsAdapter.notifyDataSetChanged();
                }else {
                    checkAllProducts.setText("Check All Products");
                    for (OrderDetailsAdapter.OrderDetailsModel model : orderDetailsList){
                        model.setProductCheck(false);
                    }
                    orderDetailsAdapter.notifyDataSetChanged();
                }
            }
        });
*/
    }

    public void showDialog(){
        progressDialog=new ProgressDialog(OrderDetailsHistoryActivity.this);
        progressDialog.setMessage("Inserting Products..Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public void closeDialog(){
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public void insertOrderProducts(boolean isCheked, OrderDetailsAdapter.OrderDetailsModel model){
        Log.w("AddModel:",model.toString());
        if (isCheked){
            String return_qty = "0";
            double net_qty = Double.parseDouble(model.getCtnQty()) - Double.parseDouble(return_qty);
            String price_value = model.getLoosePrice();

            long laterDate = System.currentTimeMillis();
            int millisec = 18000;
            Timestamp original = new Timestamp(laterDate);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(original.getTime());
            cal.add(Calendar.MILLISECOND, millisec);
            Timestamp timeStamp = new Timestamp(cal.getTime().getTime());

            dbHelper.insertCreateInvoiceCartEdit(
                    model.getProductId(),
                    model.getProductName(),
                    model.getUomcode(),
                    model.getCtnQty(),
                    model.getReturnQty(),
                    String.valueOf(net_qty),
                    model.getFocQty(),
                    price_value,
                    model.getStockQty(),
                    model.getTotal(),
                    model.getSubTotal(),
                    model.getPriceWithGST(),
                    model.getNetAmount(),
                    model.getItemDiscount(),
                    model.getBillDisc(),
                    "",
                    "",
                    model.getExchangeQty(),
                    model.getMinimumSellingPrice(),model.getStockQty(), String.valueOf(timeStamp),
                    model.getIsItemFOC()
            );
            myEdit.putString("billDisc_amt", model.getBillDisc());
            myEdit.putString("billDisc_percent", model.getBillDiscPercentage());
            myEdit.apply();
//            dbHelper.insertCart(
//                    model.getProductId(),
//                    model.getProductName(),
//                    model.getCtnQty(),
//                    model.getPcsQty(),
//                    model.getSubTotal(),
//                    "",
//                    model.getNetAmount(),
//                    "weight",
//                    model.getCartonPrice(),
//                    model.getLoosePrice(),
//                    model.getPcsPerCarton(),
//                    model.getTax(),
//                    model.getSubTotal(),
//                    model.getTaxType(),
//                    model.getFocQty(),
//                    "",
//                    model.getExchangeQty(),
//                    "",
//                    model.getItemDiscount(),
//                    model.getReturnQty(),
//                    "",
//                    "",
//                    model.getTotal(),
//                    model.getStockQty(),
//                    model.getUomcode(),"0.00");
       //    // Toast.makeText(getApplicationContext(),"Product added to your list",Toast.LENGTH_SHORT).show();
        }else {
            dbHelper.deleteInvoiceProductNew(model.getProductId(),model.getProductId());
          //  Toast.makeText(getApplicationContext(),"Product removed from your list",Toast.LENGTH_SHORT).show();
        }
    }

    private void showReorderConfirmDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.reorder_confirm_dialog, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        final CheckBox checkQtybox=dialogView.findViewById(R.id.reorder_qty_check);
        Button yesButton=dialogView.findViewById(R.id.buttonYes);
        Button noButton=dialogView.findViewById(R.id.buttonNo);

        checkQtybox.setOnCheckedChangeListener((compoundButton, b) -> isQtyCheck = b);

        checkQtybox.setChecked(true);
        checkQtybox.setVisibility(View.GONE);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (checkQtybox.isChecked()){
                    if (activityFrom.equals("SalesOrder")){
                      //  AddInvoiceActivity.customerId=customer_code;
                        Log.w("ActualPrintProductsHis:",dbHelper.numberOfRowsInInvoice()+"");

                        Utils.setCustomerSession(OrderDetailsHistoryActivity.this,customer_code);
                        Intent intent=new Intent(OrderDetailsHistoryActivity.this, CreateNewInvoiceActivity.class);
                        intent.putExtra("customerCode",customer_code);
                        intent.putExtra("customerName",customer_name);
                        intent.putExtra("from","ReOrderSales");
                        startActivity(intent);
                        finish();
                    }else {
                        Log.w("ActualPrintProductsHis:",dbHelper.numberOfRowsInInvoice()+"");

                        // AddInvoiceActivity.customerId=customer_code;
                        Utils.setCustomerSession(OrderDetailsHistoryActivity.this,customer_code);
                        Intent intent=new Intent(OrderDetailsHistoryActivity.this,CreateNewInvoiceActivity.class);
                        intent.putExtra("customerCode",customer_code);
                        intent.putExtra("customerName",customer_name);
                        intent.putExtra("from","ReOrderInvoice");
                        startActivity(intent);
                        finish();
                    }
                }else {
                     Intent intent=new Intent(OrderDetailsHistoryActivity.this,ReOrderCartActivity.class);
                     startActivity(intent);
                }
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Execute your code here
        int count=dbHelper.numberOfRows();
        if (count>0){
            showAlert();
        }else {
            finish();
        }
    }

    private void showAlert() {
        AlertDialog.Builder builder=new AlertDialog.Builder(OrderDetailsHistoryActivity.this);
        builder.setTitle("Warning..!");
        builder.setMessage("All Products in cart will be removed.!");
        builder.setCancelable(false);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                dbHelper.removeAllItems();
                finish();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    private void getSODetails(String invoiceNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("SalesOrderNo",invoiceNumber);
        jsonObject.put("LocationCode", locationCode);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SalesOrderDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_urlSOhis:",url+jsonObject);
        orderDetailsList=new ArrayList<>();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting SO Details...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        Log.w("SO_Details_his:",response.toString());
                        String statusCode = response.optString("statusCode");

                        if (response.length()>0) {
                            if (statusCode.equals("1")) {
                                JSONArray salesArray = response.optJSONArray("responseData");
                                if (salesArray.length() > 0) {
                                    JSONObject salesObject = salesArray.optJSONObject(0);

                                    String customer_code = salesObject.optString("customerCode");
                                    String customer_name = salesObject.optString("customerName");
                                    String phone_no = salesObject.optString("DelPhoneNo");
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
                                    CustomerDetails model = new CustomerDetails();
                                    model.setCustomerCode(customer_code);
                                    model.setCustomerName(customer_name);
                                    model.setCustomerAddress1(salesObject.optString("address1"));
                                    model.setTaxPerc(salesObject.optString("taxPerc"));
                                    model.setTaxType(salesObject.optString("taxType"));
                                    model.setTaxCode(salesObject.optString("taxCode"));

                                    ArrayList<CustomerDetails> taxList = new ArrayList<>();
                                    taxList.add(model);
                                    dbHelper.insertCustomerTaxValues(taxList);

                                    JSONArray products = salesObject.getJSONArray("salesOrderDetails");
                                    for (int i = 0; i < products.length(); i++) {
                                        JSONObject object = products.getJSONObject(i);

                                        String lqty = "0.0";
                                        String cqty = "0.0";
                                        if (!object.optString("unitQty").equals("null")) {
                                            lqty = object.optString("unitQty");
                                        }
                                        if (!object.optString("quantity").equals("null")) {
                                            cqty = object.optString("quantity");
                                        }
//                                        invoiceListModel.setProductCode(detailObject.optString("productCode"));
//                                        invoiceListModel.setDescription(detailObject.optString("productName"));
//                                        invoiceListModel.setLqty(detailObject.optString("unitQty"));
//                                        invoiceListModel.setCqty(detailObject.optString("cartonQty"));
//                                        invoiceListModel.setNetQty(detailObject.optString("quantity"));
//                                        invoiceListModel.setExcQty(detailObject.optString("exc_Qty"));
//                                        invoiceListModel.setNetQuantity(detailObject.optString("netQuantity"));
//                                        invoiceListModel.setFocQty(detailObject.optString("foc_Qty"));
//                                        invoiceListModel.setSaleType("");
//                                        if (detailObject.optString("bP_CatalogNo") != null) {
//                                            invoiceListModel.setCustomerItemCode(detailObject.optString("bP_CatalogNo"));
//                                        }
//                                        invoiceListModel.setReturnQty(detailObject.optString("returnQty"));
//                                        invoiceListModel.setCartonPrice(detailObject.optString("cartonPrice"));
//                                        invoiceListModel.setUnitPrice(detailObject.optString("price"));
//                                        double qty = Double.parseDouble(detailObject.optString("quantity"));
//                                        double price = Double.parseDouble(detailObject.optString("price"));
//                                        invoiceListModel.setUomCode(detailObject.optString("uomCode"));
//
//                                        double nettotal = qty * price;
//                                        invoiceListModel.setTotal(String.valueOf(nettotal));
//                                        invoiceListModel.setPricevalue(String.valueOf(price));
//
//                                        invoiceListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
//                                        invoiceListModel.setItemtax(detailObject.optString("totalTax"));
//                                        invoiceListModel.setSubTotal(detailObject.optString("subTotal"));
//
                                        OrderDetailsAdapter.OrderDetailsModel model1 = new OrderDetailsAdapter.OrderDetailsModel();
                                        model1.setProductId(object.optString("productCode"));
                                        model1.setProductName(object.optString("productName"));
                                        model1.setUomcode(object.optString("uomCode"));
                                        model1.setFocQty(object.optString("focQty"));
                                        model1.setReturnQty("0.00");
                                        model1.setTotal(object.optString("total"));
                                        model1.setCtnQty(cqty);
                                        model1.setStockQty(object.optString("stockInHand"));

                                        //  model.setPcsQty(lqty);
                                        model1.setPcsPerCarton(object.optString("pcsPerCarton"));
                                        model1.setCartonPrice(object.optString("cartonQty"));
                                        model1.setLoosePrice(object.optString("price"));
                                        model1.setTax(object.optString("totalTax"));
                                        model1.setSubTotal(object.optString("subTotal"));
                                        model1.setExchangeQty("0.00");
                                        model1.setNetQty(object.optString("quantity"));
                                        model1.setPriceWithGST(object.optString("taxAmount"));
                                        model1.setItemDiscount(object.optString("itemDiscount"));
                                        model1.setBillDisc(salesObject.optString("billDiscount"));
                                        model1.setIsItemFOC(object.optString("itemAllowFOC"));
                                        model1.setMinimumSellingPrice(salesObject.optString("minimumSellingPrice"));
                                        model1.setBillDiscPercentage("0.00");
                                        double qty = Double.parseDouble(object.optString("quantity"));
                                        double price = Double.parseDouble(object.optString("price"));
                                        double nettotal = qty * price;

                                        model1.setNetAmount(String.valueOf(nettotal));
                                        model1.setProductCheck(false);

                                        orderDetailsList.add(model1);
                                    }
                                }
                                setOrdersAdapter(orderDetailsList);
                                pDialog.dismiss();
                            }
                        }
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

    private void getInvoiceDetails(String invoiceNumber) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("InvoiceNo",invoiceNumber);
        jsonObject.put("LocationCode", locationCode);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"InvoiceDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_urlhis:",url+jsonObject);
        orderDetailsList=new ArrayList<>();
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting Invoice Details...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        Log.w("Invoice_Details_his:",response.toString());
                        String statusCode = response.optString("statusCode");

                        if (response.length()>0) {
                            if (statusCode.equals("1")) {
                                JSONArray salesArray = response.optJSONArray("responseData");
                                if (salesArray.length() > 0) {
                                    JSONObject salesObject = salesArray.optJSONObject(0);

                                    String customer_code = salesObject.optString("customerCode");
                                    String customer_name = salesObject.optString("customerName");
                                    String phone_no = salesObject.optString("DelPhoneNo");
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
                                    CustomerDetails model = new CustomerDetails();
                                    model.setCustomerCode(customer_code);
                                    model.setCustomerName(customer_name);
                                    model.setCustomerAddress1(salesObject.optString("address1"));
                                    model.setTaxPerc(salesObject.optString("taxPerc"));
                                    model.setTaxType(salesObject.optString("taxType"));
                                    model.setTaxCode(salesObject.optString("taxCode"));

                                    ArrayList<CustomerDetails> taxList = new ArrayList<>();
                                    taxList.add(model);
                                    dbHelper.insertCustomerTaxValues(taxList);

                                    JSONArray products = salesObject.getJSONArray("invoiceDetails");
                                    for (int i = 0; i < products.length(); i++) {
                                        JSONObject object = products.getJSONObject(i);

                                        String lqty = "0.0";
                                        String cqty = "0.0";
                                        if (!object.optString("unitQty").equals("null")) {
                                            lqty = object.optString("unitQty");
                                        }
                                        if (!object.optString("quantity").equals("null")) {
                                            cqty = object.optString("quantity");
                                        }
//                                        invoiceListModel.setProductCode(detailObject.optString("productCode"));
//                                        invoiceListModel.setDescription(detailObject.optString("productName"));
//                                        invoiceListModel.setLqty(detailObject.optString("unitQty"));
//                                        invoiceListModel.setCqty(detailObject.optString("cartonQty"));
//                                        invoiceListModel.setNetQty(detailObject.optString("quantity"));
//                                        invoiceListModel.setExcQty(detailObject.optString("exc_Qty"));
//                                        invoiceListModel.setNetQuantity(detailObject.optString("netQuantity"));
//                                        invoiceListModel.setFocQty(detailObject.optString("foc_Qty"));
//                                        invoiceListModel.setSaleType("");
//                                        if (detailObject.optString("bP_CatalogNo") != null) {
//                                            invoiceListModel.setCustomerItemCode(detailObject.optString("bP_CatalogNo"));
//                                        }
//                                        invoiceListModel.setReturnQty(detailObject.optString("returnQty"));
//                                        invoiceListModel.setCartonPrice(detailObject.optString("cartonPrice"));
//                                        invoiceListModel.setUnitPrice(detailObject.optString("price"));
//                                        double qty = Double.parseDouble(detailObject.optString("quantity"));
//                                        double price = Double.parseDouble(detailObject.optString("price"));
//                                        invoiceListModel.setUomCode(detailObject.optString("uomCode"));
//
//                                        double nettotal = qty * price;
//                                        invoiceListModel.setTotal(String.valueOf(nettotal));
//                                        invoiceListModel.setPricevalue(String.valueOf(price));
//
//                                        invoiceListModel.setPcsperCarton(detailObject.optString("pcsPerCarton"));
//                                        invoiceListModel.setItemtax(detailObject.optString("totalTax"));
//                                        invoiceListModel.setSubTotal(detailObject.optString("subTotal"));
//
                                        OrderDetailsAdapter.OrderDetailsModel model1 = new OrderDetailsAdapter.OrderDetailsModel();
                                        model1.setProductId(object.optString("productCode"));
                                        model1.setProductName(object.optString("productName"));
                                        model1.setUomcode(object.optString("uomCode"));
                                        model1.setFocQty(object.optString("foc_Qty"));
                                        model1.setReturnQty(object.optString("returnQty"));
                                        model1.setTotal(object.optString("total"));
                                        model1.setCtnQty(cqty);
                                        model1.setStockQty(object.optString("stockInHand"));

                                      //  model.setPcsQty(lqty);
                                        model1.setPcsPerCarton(object.optString("pcsPerCarton"));
                                        model1.setCartonPrice(object.optString("cartonQty"));
                                        model1.setLoosePrice(object.optString("price"));
                                        model1.setTax(object.optString("totalTax"));
                                        model1.setSubTotal(object.optString("subTotal"));
                                        model1.setExchangeQty(object.optString("exc_Qty"));
                                        model1.setNetQty(object.optString("quantity"));
                                        model1.setPriceWithGST(object.optString("taxAmount"));
                                        model1.setIsItemFOC(object.optString("itemAllowFOC"));
                                        model1.setItemDiscount(object.optString("itemDiscount"));
                                        model1.setBillDisc(salesObject.optString("billDiscount"));
                                        model1.setMinimumSellingPrice(salesObject.optString("minimumSellingPrice"));
                                        model1.setBillDiscPercentage(salesObject.optString("billDiscountPercentage"));
                                        double qty = Double.parseDouble(object.optString("quantity"));
                                        double price = Double.parseDouble(object.optString("price"));
                                        double nettotal = qty * price;
                                        model1.setNetAmount(String.valueOf(nettotal));
                                        model1.setProductCheck(false);

                                        orderDetailsList.add(model1);
                                    }
                                }
                                setOrdersAdapter(orderDetailsList);
                                pDialog.dismiss();
                            }
                        }
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

    private void setOrdersAdapter(ArrayList<OrderDetailsAdapter.OrderDetailsModel> ordersList) {
        orderDetailsView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OrderDetailsHistoryActivity.this);
        orderDetailsView.setLayoutManager(layoutManager);
        orderDetailsAdapter = new OrderDetailsAdapter(this,ordersList, orderList -> {
            double net_order_amount=0.0;
            int count=0;
            for (OrderDetailsAdapter.OrderDetailsModel model:orderList){
                if (model.isProductCheck()){
                    net_order_amount+=Double.parseDouble(model.getNetAmount());
                    count++;
                }
            }
            if (count==orderList.size()){
                checkAllProducts.setChecked(true);
                checkAllProducts.setText("Uncheck All Products");
            }else {
                checkAllProducts.setChecked(false);
                checkAllProducts.setText("Check All Products");
            }
            net_orderAmt=net_order_amount;
            totalOrderText.setText("$ "+Utils.twoDecimalPoint(net_order_amount));
        });
        orderDetailsView.setAdapter(orderDetailsAdapter);
    }

    public void setNetTotal(ArrayList<OrderDetailsAdapter.OrderDetailsModel> orderList){
        double net_order_amount=0.0;
        int count=0;
        for (OrderDetailsAdapter.OrderDetailsModel model:orderList){
            if (model.isProductCheck()){
                net_order_amount+=Double.parseDouble(model.getNetAmount());
                count++;
            }
        }
        if (count==orderList.size()){
            checkAllProducts.setChecked(true);
            checkAllProducts.setText("Uncheck All Products");
        }else {
            checkAllProducts.setChecked(false);
            checkAllProducts.setText("Check All Products");
        }
        net_orderAmt=net_order_amount;
        totalOrderText.setText("$ "+Utils.twoDecimalPoint(net_order_amount));
    }
}