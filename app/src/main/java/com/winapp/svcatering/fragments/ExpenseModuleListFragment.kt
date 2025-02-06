package com.winapp.svcatering.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.winapp.svcatering.R
import com.winapp.svcatering.activity.NewInvoiceListActivity
import com.winapp.svcatering.adapter.ExpenselistModulAdapter
import com.winapp.svcatering.model.InvoiceModel
import com.winapp.svcatering.model.InvoicePrintPreviewModel.InvoiceList
import com.winapp.svcatering.utils.Constants
import com.winapp.svcatering.utils.SessionManager
import com.winapp.svcatering.utils.Utils
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//Our class extending fragment
class ExpenseModuleListFragment : Fragment() {
    private var pDialog: SweetAlertDialog? = null
    var pageNo = 1
    private var session: SessionManager? = null
    private var user: HashMap<String, String>? = null
    private var companyId: String? = null
    var InvoiceStatus: String? = null
    var paidPageNo = 1
    var unpaidPageNo = 1

    //This is our tablayout
    private val tabLayout: TabLayout? = null

    //This is our viewPager
    private val viewPager: ViewPager? = null
    var isfirstTime = true
    var titletext: TextView? = null
    var progressLayout: View? = null
    var loadingImage: ImageView? = null
    var isFound = "true"
    var username: String? = ""
    var locationCode: String? = ""
    var currentDate = ""
    var createNewInvoice: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InvoiceStatus = if (arguments != null) requireArguments().getString("ARG_STATUS") else "1"
        Log.w("InvoiceStatus", InvoiceStatus!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        val view = inflater.inflate(R.layout.expense_list_layout, container, false)
        invoiceListView = view.findViewById(R.id.expens_list)
        netTotalText = view.findViewById(R.id.net_total_value)
        emptyLayout = view.findViewById(R.id.empty_layout)
        outstandingLayout = view.findViewById(R.id.outstanding_layout)
        progressLayout = view.findViewById(R.id.progress_layout)
        titletext = view.findViewById(R.id.title)
        createNewInvoice = view.findViewById(R.id.create_invoice)
        titletext!!.setText("All Expense Loading....")

        session = SessionManager(activity)
        user = session!!.userDetails
        companyId = user!!.get(SessionManager.KEY_COMPANY_CODE)
        username = user!!.get(SessionManager.KEY_USER_NAME)
        locationCode = user!!.get(SessionManager.KEY_LOCATION_CODE)
        loadingImage = view.findViewById(R.id.loading_image)
        Glide.with(this)
            .load(R.raw.loading_image1)
            .into(loadingImage!!)

        /*  if (invoiceList.size()==0 && isfirstTime){
            getInvoices(companyId,String.valueOf(pageNo),"ALL");
        }*/
        val c = Calendar.getInstance().time
        println("Current time => $c")
        val df1 = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        currentDate = df1.format(c)
        getExpenseDetail(companyId, pageNo.toString(), "ALL", currentDate, currentDate, "")

        /*if (invoiceList.size()>0){
            invoiceListView.setVisibility(View.VISIBLE);
            outstandingLayout.setVisibility(View.VISIBLE);
        }else {
            invoiceListView.setVisibility(View.GONE);
            outstandingLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }*/invoiceListView!!.setAdapter(expenselistModulAdapter)

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
        });*/createNewInvoice!!.setOnClickListener(View.OnClickListener {
            NewInvoiceListActivity.isSearchCustomerNameClicked = true
            NewInvoiceListActivity.viewCloseBottomSheet()
        })
        return view
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            invoiceList = ArrayList()
            // getInvoices(companyId, "1", "PAID");
            // setNettotal(invoiceList);
        }
    }

    fun getExpenseDetail(
        companyCode: String?,
        pageNo: String?,
        action: String?,
        fromdate: String?,
        todate: String?,
        supplierCode: String?
    ) {
        try {
            Log.w("LoadingAction:", action!!)
            // Initialize a new RequestQueue instance
            val requestQueue = Volley.newRequestQueue(activity)
            // Initialize a new JsonArrayRequest instance
            val jsonObject = JSONObject()
            jsonObject.put("User", username)
            jsonObject.put("LocationCode", locationCode)
            jsonObject.put("VendorCode", supplierCode)
            jsonObject.put("FromDate", fromdate)
            jsonObject.put("ToDate", todate)
            jsonObject.put("DocStatus", "")
            val url = Utils.getBaseUrl(activity) + "PurchaseInvoiceList"
            Log.w("Given_url_expense:", "$url.. $jsonObject")
            pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
            pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog!!.setTitleText("Getting All Invoices...")
            pDialog!!.setCancelable(false)
            /* if (pageNo.equals("1") && isfirstTime) {
                pDialog.show();
            }*/isfirstTime = false
            invoiceList = ArrayList()
            val jsonObjectRequest: JsonObjectRequest =
                object : JsonObjectRequest(Method.POST, url, jsonObject,
                    Response.Listener { response: JSONObject ->
                        try {
                            Log.w("expense_Res :", response.toString())
                            // {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"CUS\/686","customerName":"VH FACTORY",
                            // "invoiceNumber":"1","invoiceStatus":"O","invoiceDate":"13\/07\/2021","netTotal":"128.400000","balance":"128.400000",
                            // "totalDiscount":"0.000000","paidAmount":"0.000000","contactPersonCode":"0","createDate":"13\/07\/2021",
                            // "updateDate":"13\/07\/2021","remark":""}]}
                            //  pDialog.dismiss();
                            if (response.length() > 0) {
                                val statusCode = response.optString("statusCode")
                                if (statusCode == "1") {
                                    val invoiceArray = response.optJSONArray("responseData")
                                    for (i in 0 until invoiceArray.length()) {
                                        val `object` = invoiceArray.optJSONObject(i)
                                        val model = InvoiceModel()
                                        model.name = `object`.optString("vendorName")
                                        model.date = `object`.optString("poDate")
                                        model.balance = `object`.optString("balance")
                                        model.invoiceNumber = `object`.optString("poNumber")
                                        // model.setAddress(object.optString("Address1"));
                                        model.netTotal = `object`.optString("netTotal")
                                        model.status = `object`.optString("poStatus")
                                        model.customerCode = `object`.optString("customerCode")
                                        model.invoiceCode = `object`.optString("code")
                                        // isFound=invoiceObject.optString("ErrorMessage");
                                        val invoiceLists = ArrayList<InvoiceList>()
                                        model.invoiceList = invoiceLists
                                        invoiceList.add(model)
                                        displayInvoiceList.add(model)
                                    }
                                }
                                //  invoiceAdapter.notifyDataSetChanged();
                                // invoiceAdapter.setLoaded();
                            }
                            setShowHide()
                            progressLayout!!.visibility = View.GONE
                            if (invoiceList.size > 0) {
                                setNettotal(invoiceList)
                            }
                            setAdapter()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { error: VolleyError ->
                        //pDialog.dismiss();
                        // Do something when error occurred
                        Log.w("Error_throwing:", error.toString())
                    }) {
                    override fun getHeaders(): Map<String, String> {
                        val params = HashMap<String, String>()
                        val creds = String.format(
                            "%s:%s",
                            Constants.API_SECRET_CODE,
                            Constants.API_SECRET_PASSWORD
                        )
                        val auth =
                            "Basic " + Base64.encodeToString(creds.toByteArray(), Base64.DEFAULT)
                        params["Authorization"] = auth
                        return params
                    }
                }
            jsonObjectRequest.setRetryPolicy(object : RetryPolicy {
                override fun getCurrentTimeout(): Int {
                    return 50000
                }

                override fun getCurrentRetryCount(): Int {
                    return 50000
                }

                @Throws(VolleyError::class)
                override fun retry(error: VolleyError) {
                }
            })
            // Add JsonArrayRequest to the RequestQueue
            requestQueue.add(jsonObjectRequest)
        } catch (ex: Exception) {
        }
    }

    fun setShowHide() {
        if (invoiceList.size > 0) {
            invoiceListView!!.visibility = View.VISIBLE
            outstandingLayout!!.visibility = View.VISIBLE
            emptyLayout!!.visibility = View.GONE
        } else {
            invoiceListView!!.visibility = View.GONE
            emptyLayout!!.visibility = View.VISIBLE
            outstandingLayout!!.visibility = View.GONE
        }
    }

    fun setInvoiceAdapter(
        context: Context,
        invoiceList: ArrayList<InvoiceModel>,
        invoiceStatus: String?
    ) {
        val filteredList = ArrayList<InvoiceModel>()
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
*/invoiceListView!!.setHasFixedSize(true)
        invoiceListView!!.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.VERTICAL, false
        )
        expenselistModulAdapter =
            ExpenselistModulAdapter(
                context,
                invoiceListView,
                invoiceList,
                object : ExpenselistModulAdapter.CallBack {
                    override fun calculateNetTotal(invoiceList: ArrayList<InvoiceModel>) {
                        setNettotal(invoiceList)
                    }

                    override fun showMoreOption(invoiceId: String, customerName: String) {
                        // InvoiceListActivity.showInvoiceOption();
                    }
                })
        if (invoiceList.size > 0) {
            invoiceListView!!.visibility = View.VISIBLE
            outstandingLayout!!.visibility = View.VISIBLE
            emptyLayout!!.visibility = View.GONE
        } else {
            invoiceListView!!.visibility = View.GONE
            outstandingLayout!!.visibility = View.GONE
            emptyLayout!!.visibility = View.VISIBLE
        }
        invoiceListView!!.adapter = expenselistModulAdapter
        setNettotal(invoiceList)
        NewInvoiceListActivity.isSearchCustomerNameClicked = false
        NewInvoiceListActivity.selectCustomerId = ""
    }

    fun setAdapter() {
        invoiceListView!!.setHasFixedSize(true)
        invoiceListView!!.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.VERTICAL, false
        )
        expenselistModulAdapter = ExpenselistModulAdapter(
            activity,
            invoiceListView,
            invoiceList,
            object : ExpenselistModulAdapter.CallBack {
                override fun calculateNetTotal(invoiceList: ArrayList<InvoiceModel>) {
                    setNettotal(invoiceList)
                }

                override fun showMoreOption(invoiceId: String, customerName: String) {
                    // InvoiceListActivity.showInvoiceOption();
                }
            })
        if (invoiceList.size > 0) {
            invoiceListView!!.visibility = View.VISIBLE
            outstandingLayout!!.visibility = View.VISIBLE
        } else {
            invoiceListView!!.visibility = View.GONE
            outstandingLayout!!.visibility = View.GONE
            emptyLayout!!.visibility = View.VISIBLE
        }
        invoiceListView!!.adapter = expenselistModulAdapter

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
        });*/if (invoiceList.size > 0) {
            setNettotal(invoiceList)
        }
    }

    fun filterCancel() {
        //  setFilterAdapter(displayInvoiceList);
        invoiceList = ArrayList()
        getExpenseDetail(companyId, pageNo.toString(), "ALL", currentDate, currentDate, "")
    }

    @Throws(JSONException::class)
    fun filterSearch(
        context: Context,
        username: String?,
        vendorCode: String?,
        invoiceStatus: String?,
        fromdate: String?,
        todate: String?,
        location: String?
    ) {
        try {
            // Initialize a new RequestQueue instance
            //  http://94.237.70.51:153/es/data/api/SalesApi/GetAllInvoiceList?
            //  Requestdata={"CustomerCode":"","InvoiceNo":"","StartDate":"08/06/2021","EndDate":"08/06/2021","CompanyCode":"10"}
            val requestQueue = Volley.newRequestQueue(context)
            // Initialize a new JsonArrayRequest instance
            val jsonObject = JSONObject()
            jsonObject.put("User", username)
            jsonObject.put("VendorCode", vendorCode)
            jsonObject.put("FromDate", fromdate)
            jsonObject.put("ToDate", todate)
            jsonObject.put("LocationCode", location)
            jsonObject.put("DocStatus", invoiceStatus)
            val url = Utils.getBaseUrl(context) + "PurchaseInvoiceList"

            Log.w("AllExpensFilter:", "$url/$jsonObject")

            pDialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog!!.setTitleText("Getting All Expense...")
            pDialog!!.setCancelable(false)
            pDialog!!.show()
            /* if (pageNo.equals("1") && isfirstTime) {
                pDialog.show();
            }*/isfirstTime = false
            invoiceList = ArrayList()
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, url,
                jsonObject,
                Response.Listener { response: JSONObject ->
                    try {
                        Log.w("Expense_Filter:", response.toString())
                        //  pDialog.dismiss();
                        if (response.length() > 0) {
                            val statusCode = response.optString("statusCode")
                            if (statusCode == "1") {
                                val invoiceArray = response.optJSONArray("responseData")
                                for (i in 0 until invoiceArray.length()) {
                                    val `object` = invoiceArray.optJSONObject(i)
                                    val model = InvoiceModel()
                                    model.name = `object`.optString("vendorName")
                                    model.date = `object`.optString("poDate")
                                    model.balance = `object`.optString("balance")
                                    model.invoiceNumber = `object`.optString("poNumber")
                                    // model.setAddress(object.optString("Address1"));
                                    model.netTotal = `object`.optString("netTotal")
                                    model.status = `object`.optString("poStatus")
                                    model.customerCode = `object`.optString("customerCode")
                                    model.invoiceCode = `object`.optString("code")
                                    // isFound=invoiceObject.optString("ErrorMessage");
                                    val invoiceLists = ArrayList<InvoiceList>()
                                    model.invoiceList = invoiceLists
                                    invoiceList.add(model)
                                    displayInvoiceList.add(model)
                                }
                            }
                            // invoiceAdapter.notifyDataSetChanged();
                            // invoiceAdapter.setLoaded();
                        }
                        pDialog!!.dismiss()
                        //  setShowHide();
                        //     progressLayout.setVisibility(View.GONE);
                        setInvoiceAdapter(context, invoiceList, invoiceStatus)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error: VolleyError ->
                    //pDialog.dismiss();
                    // Do something when error occurred
                    Log.w("Error_throwing:", error.toString())
                }) {
                override fun getHeaders(): Map<String, String> {
                    val params = HashMap<String, String>()
                    val creds = String.format(
                        "%s:%s",
                        Constants.API_SECRET_CODE,
                        Constants.API_SECRET_PASSWORD
                    )
                    val auth = "Basic " + Base64.encodeToString(creds.toByteArray(), Base64.DEFAULT)
                    params["Authorization"] = auth
                    return params
                }
            }
            jsonObjectRequest.setRetryPolicy(object : RetryPolicy {
                override fun getCurrentTimeout(): Int {
                    return 50000
                }

                override fun getCurrentRetryCount(): Int {
                    return 50000
                }

                @Throws(VolleyError::class)
                override fun retry(error: VolleyError) {
                }
            })
            // Add JsonArrayRequest to the RequestQueue
            requestQueue.add(jsonObjectRequest)
        } catch (ex: Exception) {
            Log.e("Error_in_filter", ex.message!!)
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
    fun setFilterAdapter(invoiceList: ArrayList<InvoiceModel?>?) {
        invoiceListView!!.visibility = View.VISIBLE
        emptyLayout!!.visibility = View.GONE
        outstandingLayout!!.visibility = View.VISIBLE
        invoiceListView!!.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.VERTICAL, false
        )
        expenselistModulAdapter = ExpenselistModulAdapter(
            activity,
            invoiceListView,
            invoiceList,
            object : ExpenselistModulAdapter.CallBack {
                override fun calculateNetTotal(invoiceList: ArrayList<InvoiceModel>) {
                    setNettotal(invoiceList)
                }

                override fun showMoreOption(invoiceId: String, customerName: String) {
                    // InvoiceListActivity.showInvoiceOption();
                }
            })
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

    companion object {
        //Overriden method onCreateView
        var invoiceListView: RecyclerView? = null
        private var expenselistModulAdapter: ExpenselistModulAdapter? = null
        private var invoiceList = ArrayList<InvoiceModel>()

        private val displayInvoiceList = ArrayList<InvoiceModel>()
        var emptyLayout: LinearLayout? = null
        var outstandingLayout: LinearLayout? = null
        var netTotalText: TextView? = null
        fun setNettotal(invoiceList: ArrayList<InvoiceModel>) {
            try {
                var net_amount = 0.0
                var balance_amt = 0.0
                for (model in invoiceList) {
                    balance_amt = if (model.balance != null && model.balance != "null") {
                        model.balance.toDouble()
                    } else {
                        0.0
                    }
                    net_amount += balance_amt
                }
                netTotalText!!.text = "$ " + Utils.twoDecimalPoint(net_amount)
            } catch (ex: Exception) {
            }
        }
    }
}