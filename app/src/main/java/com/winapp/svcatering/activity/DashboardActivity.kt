package com.winapp.svcatering.activity

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.winapp.svcatering.R
import com.winapp.svcatering.activity.MainHomeActivity.CompanyChooseAdapter
import com.winapp.svcatering.adapter.CreditLimitDialogAdapter
import com.winapp.svcatering.adapter.DashboardAdapter
import com.winapp.svcatering.db.DBHelper
import com.winapp.svcatering.fragments.ProductsFragment
import com.winapp.svcatering.model.CreditLimitDialogResponse
import com.winapp.svcatering.model.CustomerModel
import com.winapp.svcatering.model.NewLocationModel
import com.winapp.svcatering.model.ProductsModel
import com.winapp.svcatering.receipts.ReceiptsListActivity
import com.winapp.svcatering.salesreturn.NewSalesReturnListActivity
import com.winapp.svcatering.utils.Constants
import com.winapp.svcatering.utils.GridSpacingItemDecoration
import com.winapp.svcatering.utils.SessionManager
import com.winapp.svcatering.utils.SharedPreferenceUtil
import com.winapp.svcatering.utils.Utils
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects

class DashboardActivity : NavigationActivity() {
    private var dashboardView: RecyclerView? = null
    private var dashboardAdapter: DashboardAdapter? = null
    private var titleList: ArrayList<String>? = null
    private var userName: TextView? = null
    private var dateText: TextView? = null
    private val customerList: ArrayList<CustomerModel>? = null
    private var session1: SessionManager? = null
    private var user1: HashMap<String, String>? = null
    private var company: HashMap<String, String>? = null
    private var companyCode: String? = null
    private var dbHelper: DBHelper? = null
    var creditLimitDialogAdapter: CreditLimitDialogAdapter? = null
    var rv_crditLimit: RecyclerView? = null
    private var catalogLayout: LinearLayout? = null
    private var salesOrderLayout: LinearLayout? = null
    private var invoiceLayout: LinearLayout? = null
    private var receiptsLayout: LinearLayout? = null
    private var deliveryLayout: LinearLayout? = null
    private var salesReturnLayout: LinearLayout? = null
    private var productsLayout: LinearLayout? = null
    private var customerLayout: LinearLayout? = null
    private var settingsLayout: LinearLayout? = null
    private var catalogCard: CardView? = null
    private var creditLimit_Img: ImageView? = null
    private var sharedPreferenceUtil: SharedPreferenceUtil? = null

    var locationCodem: String? = null
    private var timeText: TextView? = null
    var dialog: AlertDialog? = null
    var pDialog: SweetAlertDialog? = null
    var pdialog: ProgressDialog? = null
    var companyLogo: ImageView? = null
    var companyLogoString: String? = null
    private var locationDetailsl: ArrayList<NewLocationModel.LocationDetails>? = null
    var creditLimitListFilter = ArrayList<CreditLimitDialogResponse>()
    var fromWarehouseCode: String? = ""
    var fromWarehouseName = ""
    var isPermissionToChangeLocation = false
    var isCheckedCreditLimit = false
    var creditDialog: Dialog? = null
    var itemSize_creditl: TextView? = null
    var creditAmtApi = "10500"
    private var sharedPref_billdisc: SharedPreferences? = null
    private var myEdit: SharedPreferences.Editor? = null
    var isCheckedSO = false
    var isCheckedDO = false
    var isCheckedInvoice = false
    var isCheckedSalesReturn = false
    var isCheckedCatalog = false
    var isCheckedCustomer = false
    var isCheckedProduct = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val contentFrameLayout = findViewById<View>(R.id.content_frame) as FrameLayout
        //Remember this is the FrameLayout area within your activity_main.xml
        layoutInflater.inflate(R.layout.activity_dashboard, contentFrameLayout)
        // setContentView(R.layout.activity_dashboard);
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Home")
        userName = findViewById(R.id.user_name)
        dateText = findViewById(R.id.date)
        dbHelper = DBHelper(this)
        session1 = SessionManager(this)
        sharedPreferenceUtil = SharedPreferenceUtil(this)

        user1 = session1!!.userDetails
        company = session1!!.companyDetails
        companyLogoString = company!!.get(SessionManager.KEY_COMPANY_LOGO)
        companyCode = user1!![SessionManager.KEY_COMPANY_CODE]
        locationCodem = user1!![SessionManager.KEY_LOCATION_CODE]
        catalogLayout = findViewById(R.id.catalog_layout)
        salesOrderLayout = findViewById(R.id.sales_order_layout)
        invoiceLayout = findViewById(R.id.invoice_layout)
        receiptsLayout = findViewById(R.id.receipt_layout)
        deliveryLayout = findViewById(R.id.delivery_order_layout)
        salesReturnLayout = findViewById(R.id.sales_return_layout)
        productsLayout = findViewById(R.id.product_layout)
        customerLayout = findViewById(R.id.customer_layout)
        settingsLayout = findViewById(R.id.settings_layout)
        catalogCard = findViewById(R.id.catalog_card)
        creditLimit_Img = findViewById(R.id.creditLimit_dial)
        timeText = findViewById(R.id.time)
        companyLogo = findViewById(R.id.company_logo)
        val c = Calendar.getInstance().time
        println("Current time => $c")
        var myThread: Thread? = null
        val runnable: Runnable = CountDownRunner()
        myThread = Thread(runnable)
        myThread.start()

        val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        val formattedDate = df.format(c)
        dateText!!.setText(formattedDate)
        session1 = SessionManager(this)
        user1 = session1!!.userDetails
        userName!!.setText(user1!![SessionManager.KEY_USER_NAME])

        sharedPref_billdisc = getSharedPreferences("BillDiscPref", MODE_PRIVATE)
        myEdit = sharedPref_billdisc!!.edit()

        myEdit!!.putString("billDisc_amt","0.0")
        myEdit!!.putString("billDisc_percent","0.0")
        myEdit!!.apply()
        sharedPreferenceUtil!!.setStringPreference(sharedPreferenceUtil!!.KEY_ACTIVITY, "DO")

        dashboardView = findViewById(R.id.root_layout)
        titleList = ArrayList()
        if (intent != null) {
            val login = intent.getStringExtra("isLogin")
            if (login == "1") {
                val intent = Intent(this@DashboardActivity, DashboardActivity::class.java)
                startActivity(intent)
            }
        }
        if (locationCodem != null && !locationCodem!!.isEmpty()) {
            locationCodem = user1!![SessionManager.KEY_LOCATION_CODE]
        } else {
            Toast.makeText(this, "Choose Location", Toast.LENGTH_SHORT).show()
        }
        Log.w("Logged_In_Location:", locationCodem.toString())
        dbHelper!!.removeAllReturn()
        dbHelper!!.removeAllInvoiceItems()
        dbHelper!!.removeAllItems()
        Utils.clearCustomerSession(this)


        //   showCreditdialog(creditAmtApi);
        val settings = dbHelper!!.settings
        if (settings != null) {
            if (settings.size > 0) {
                for (model in settings) {
                    if (model.settingName == "showLocationPermission") {
                        Log.w("SettingNameloc:", model.settingName)
                        Log.w("SettingValueloc:", model.settingValue)
                        isPermissionToChangeLocation = if (model.settingValue == "True") {
                            true
                        } else {
                            false
                        }
                    } else if (model.settingName == "creditLimitSwitch") {
                        Log.w("SettingNamecrdt:", model.settingName)
                        Log.w("SettingValuecrdt:", model.settingValue)
                        isCheckedCreditLimit = if (model.settingValue == "1") {
                            true
                        } else {
                            false
                        }
                    } else if (model.settingName == "showSalesOrder") {
                        Log.w("SettingNameSO:", model.settingName)
                        Log.w("SettingValueSO:", model.settingValue)
                        isCheckedSO = if (model.settingValue == "True") {
                            true
                        } else {
                            false
                        }
                    } else if (model.settingName == "showDeliveryOrder") {
                        Log.w("SettingNameDO:", model.settingName)
                        Log.w("SettingValueDO:", model.settingValue)
                        isCheckedDO = if (model.settingValue == "True") {
                            true
                        } else {
                            false
                        }
                    } else if (model.settingName == "showInvoice") {
                        Log.w("SettingNameInv:", model.settingName)
                        Log.w("SettingValueInv:", model.settingValue)
                        isCheckedInvoice = if (model.settingValue == "True") {
                            true
                        } else {
                            false
                        }
                    } else if (model.settingName == "showSalesReturn") {
                        Log.w("SettingNameSaleRet:", model.settingName)
                        Log.w("SettingValueSaleRet:", model.settingValue)
                        isCheckedSalesReturn = if (model.settingValue == "True") {
                            true
                        } else {
                            false
                        }
                    } else if (model.settingName == "showCatelog") {
                        Log.w("SettingNameCatal:", model.settingName)
                        Log.w("SettingValueCatal:", model.settingValue)
                        isCheckedCatalog = if (model.settingValue == "True") {
                            true
                        } else {
                            false
                        }
                    } else if (model.settingName == "showCustomer") {
                        Log.w("SettingNameCust:", model.settingName)
                        Log.w("SettingValueCust:", model.settingValue)
                        isCheckedCustomer = if (model.settingValue == "True") {
                            true
                        } else {
                            false
                        }
                    } else if (model.settingName == "showProduct") {
                        Log.w("SettingNamePdt:", model.settingName)
                        Log.w("SettingValuePdt:", model.settingValue)
                        isCheckedProduct = if (model.settingValue == "True") {
                            true
                        } else {
                            false
                        }
                    }
                }
            }
            if (isCheckedSO && (locationCodem != null && !locationCodem!!.isEmpty()) ) {
                salesOrderLayout!!.setAlpha(0.9f)
                salesOrderLayout!!.setEnabled(true)
                salesOrderLayout!!.setClickable(true)
            } else {
                salesOrderLayout!!.setAlpha(0.4f)
                salesOrderLayout!!.setEnabled(false)
                salesOrderLayout!!.setClickable(false)
            }
            if (isCheckedCatalog && (locationCodem != null && !locationCodem!!.isEmpty()) ) {
                catalogCard!!.setAlpha(0.9f)
                catalogCard!!.setEnabled(true)
            } else {
                catalogCard!!.setAlpha(0.4f)
                catalogCard!!.setEnabled(false)
            }
            if (isCheckedSalesReturn && (locationCodem != null && !locationCodem!!.isEmpty()) ) {
                salesReturnLayout!!.setAlpha(0.9f)
                salesReturnLayout!!.setEnabled(true)
            } else {
                salesReturnLayout!!.setAlpha(0.4f)
                salesReturnLayout!!.setEnabled(false)
            }
            if (isCheckedDO && (locationCodem != null && !locationCodem!!.isEmpty()) ) {
                deliveryLayout!!.setAlpha(0.9f)
                deliveryLayout!!.setEnabled(true)
            } else {
                deliveryLayout!!.setAlpha(0.4f)
                deliveryLayout!!.setEnabled(false)
            }
            if (isCheckedCustomer && (locationCodem != null && !locationCodem!!.isEmpty()) ) {
                customerLayout!!.setAlpha(0.9f)
                customerLayout!!.setEnabled(true)
            } else {
                customerLayout!!.setAlpha(0.4f)
                customerLayout!!.setEnabled(false)
            }
            if (isCheckedProduct && (locationCodem != null && !locationCodem!!.isEmpty()) ) {
                productsLayout!!.setAlpha(0.9f)
                productsLayout!!.setEnabled(true)
            } else {
                productsLayout!!.setAlpha(0.4f)
                productsLayout!!.setEnabled(false)
            }
            if (isCheckedInvoice && (locationCodem != null && !locationCodem!!.isEmpty()) ) {
                invoiceLayout!!.setAlpha(0.9f)
                invoiceLayout!!.setEnabled(true)
                invoiceLayout!!.setClickable(true)
            } else {
                invoiceLayout!!.setAlpha(0.4f)
                invoiceLayout!!.setEnabled(false)
                invoiceLayout!!.setClickable(false)
            }
            //     Log.w("CompanyLogoString:",companyLogoString);
            /*  if (companyLogoString!=null && !companyLogoString.isEmpty()){
            byte[] imageByteArray = Base64.decode(companyLogoString, Base64.DEFAULT);
            Glide.with(this)
                    .asBitmap()
                    .load(companyLogoString)
                    .into(companyLogo);
        }else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.winapp_logo)
                    .into(companyLogo);
        }*/

            //    getCreditLimit();
            Glide.with(this)
                .asBitmap()
                .load(R.drawable.winapp_logo)
                .into(companyLogo!!)
            try {
                // getAllSettings();
                locationList
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.w("creditsetting", "$isCheckedCreditLimit...$isPermissionToChangeLocation")
            if (isCheckedCreditLimit) {
                if (creditLimitListFilter.size > 0) {
                    showCreditdialog(creditLimitListFilter)
                } else {
                    creditLimit
                }
            } else {
                creditLimit_Img!!.setVisibility(View.GONE)
                creditLimit_Img!!.setEnabled(false)
            }
            creditLimit_Img!!.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    if (creditLimitListFilter.size > 0) {
                        showCreditdialog(creditLimitListFilter)
                    } else {
                        creditLimit
                    }
                }
            })

            /* titleList.add("Catalog");
        titleList.add("Sales");
        titleList.add("Invoice");
        titleList.add("Receipt");
        titleList.add("Delivery Order");
        titleList.add("Sales Return");
        titleList.add("Product");
        titleList.add("Goods Receive");
        titleList.add("Settings");*/
            val userRolls = helper!!.userPermissions
            if (userRolls.size > 0) {
                for (roll in userRolls) {
                    when (roll.formName) {
                        "Catalog" -> if (roll.havePermission == "true") {
                            catalogLayout!!.setVisibility(View.VISIBLE)
                        } else {
                            catalogLayout!!.setVisibility(View.GONE)
                        }

                        "Sales Order" -> if (roll.havePermission == "true") {
                            salesOrderLayout!!.setVisibility(View.VISIBLE)
                        } else {
                            salesOrderLayout!!.setVisibility(View.GONE)
                        }

                        "Invoice" -> if (roll.havePermission == "true") {
                            invoiceLayout!!.setVisibility(View.VISIBLE)
                        } else {
                            invoiceLayout!!.setVisibility(View.GONE)
                        }

                        "Receipts" -> if (roll.havePermission == "true") {
                            receiptsLayout!!.setVisibility(View.VISIBLE)
                        } else {
                            receiptsLayout!!.setVisibility(View.GONE)
                        }

                        "Settings" -> if (roll.havePermission == "true") {
                            settingsLayout!!.setVisibility(View.VISIBLE)
                        } else {
                            settingsLayout!!.setVisibility(View.GONE)
                        }

                        "Sales Return" -> if (roll.havePermission == "true") {
                            salesReturnLayout!!.setVisibility(View.VISIBLE)
                        } else {
                            salesReturnLayout!!.setVisibility(View.GONE)
                        }

                        "Customer List" -> if (roll.havePermission == "true") {
                            customerLayout!!.setVisibility(View.VISIBLE)
                        } else {
                            customerLayout!!.setVisibility(View.GONE)
                        }
                    }
                }
            }
        }
        val settings1 = dbHelper!!.settings
        if (settings1 == null || settings1.size == 0) {
            dbHelper!!.insertSettings("invoice_switch", "0")
        }
        try {
            dashboardAdapter = DashboardAdapter(this, titleList)
            val mNoOfColumns = Utils.calculateNoOfColumns(this, 150f)
            // GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, mNoOfColumns);
            val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, mNoOfColumns)
            dashboardView!!.setLayoutManager(mLayoutManager)
            // categoriesView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            dashboardView!!.addItemDecoration(
                GridSpacingItemDecoration(
                    2,
                    GridSpacingItemDecoration.dpToPx(this, 2),
                    true
                )
            )
            dashboardView!!.setItemAnimator(DefaultItemAnimator())
            dashboardView!!.setAdapter(dashboardAdapter)
            dashboardView!!.setVisibility(View.GONE)
        } catch (ex: Exception) {
            Log.e("TAG", "Error in Populating the data:" + ex.message)
        }

        // Set the Onlclick listener in the Layout
        catalogLayout!!.setOnClickListener(View.OnClickListener {
            //  Intent intent=new Intent(DashboardActivity.this, CategoriesActivity.class);
            // startActivity(intent);
        })
        catalogCard!!.setOnClickListener(View.OnClickListener { /* Intent intent=new Intent(DashboardActivity.this,SchedulingActivity.class);
                startActivity(intent);*/
            val intent = Intent(this@DashboardActivity, CategoriesActivity::class.java)
            startActivity(intent)
        })
        salesOrderLayout!!.setOnClickListener(View.OnClickListener {
            sharedPreferenceUtil!!.setStringPreference(sharedPreferenceUtil!!.KEY_ACTIVITY, "SalesOrder")

            val intent = Intent(this@DashboardActivity, SalesOrderListActivity::class.java)
            startActivity(intent)
        })
        invoiceLayout!!.setOnClickListener(View.OnClickListener {
            sharedPreferenceUtil!!.setStringPreference(sharedPreferenceUtil!!.KEY_ACTIVITY, "Invoice")

            val intent = Intent(this@DashboardActivity, NewInvoiceListActivity::class.java)
            startActivity(intent)
        })
        receiptsLayout!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@DashboardActivity, ReceiptsListActivity::class.java)
            startActivity(intent)
        })
        deliveryLayout!!.setOnClickListener(View.OnClickListener {
            sharedPreferenceUtil!!.setStringPreference(sharedPreferenceUtil!!.KEY_ACTIVITY, "DO")

            val intent = Intent(this@DashboardActivity, DeliveryOrderListActivity::class.java)
            startActivity(intent)
        })
        productsLayout!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@DashboardActivity, StockProductsActivity::class.java)
            startActivity(intent)
        })
        customerLayout!!.setOnClickListener(View.OnClickListener { // need to implement
            val intent = Intent(this@DashboardActivity, CustomerListActivity::class.java)
            intent.putExtra("Message", "Open")
            intent.putExtra("from", "cus")
            startActivity(intent)
        })
        settingsLayout!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@DashboardActivity, SettingActivity::class.java)
            startActivity(intent)
        })
        salesReturnLayout!!.setOnClickListener(View.OnClickListener {
            /*  Intent intent=new Intent(DashboardActivity.this, SalesReturnActivity.class);
                    startActivity(intent);*/
            sharedPreferenceUtil!!.setStringPreference(sharedPreferenceUtil!!.KEY_ACTIVITY, "SalesReturn")

            val intent = Intent(this@DashboardActivity, NewSalesReturnListActivity::class.java)
            startActivity(intent)

            //throw new ArithmeticException("Test fix"); // Force a crash
        })
    }

    private fun showCreditdialog(creditLimitArrayList: ArrayList<CreditLimitDialogResponse>) {
        val li = LayoutInflater.from(this)
        val promptsView = li.inflate(R.layout.credit_limit_dialog, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
        val closeImg = promptsView.findViewById<ImageView>(R.id.dial_close)
        val okBtn = promptsView.findViewById<View>(R.id.ok_dial) as TextView
        itemSize_creditl = promptsView.findViewById<View>(R.id.itemSize_credit) as TextView
        rv_crditLimit = promptsView.findViewById<View>(R.id.rv_creditlimit_list) as RecyclerView
        if (creditLimitArrayList.size > 0) {
            creditLimit_Img!!.visibility = View.VISIBLE
            creditLimit_Img!!.isEnabled = true
            setCreditAdater(creditLimitArrayList)
        }
        okBtn.setOnClickListener { creditDialog!!.dismiss() }
        closeImg.setOnClickListener { creditDialog!!.dismiss() }
        alertDialogBuilder.setView(promptsView)
        creditDialog = alertDialogBuilder.create()
        creditDialog!!.setCancelable(true)
        creditDialog!!.show()
    }

    internal inner class CountDownRunner : Runnable {
        // @Override
        override fun run() {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    doWork()
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: Exception) {
                }
            }
        }
    }

    fun doWork() {
        runOnUiThread {
            try {
                val dt = Date()
                val hours = dt.hours
                val minutes = dt.minutes
                val seconds = dt.seconds
                val curTime = "$hours : $minutes : $seconds"
                timeText!!.text = curTime
            } catch (e: Exception) {
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val menuItem = menu.findItem(R.id.action_cart)
        val cartTotal = menu.findItem(R.id.action_total)
        cartTotal.setVisible(false)
        val cart = menu.findItem(R.id.action_cart)
        cart.setVisible(false)
        val search = menu.findItem(R.id.action_search)
        search.setVisible(false)
        val actionView = menuItem.actionView
        textCartItemCount = actionView!!.findViewById(R.id.cart_badge)
        setupBadge()
        actionView.setOnClickListener {
            ProductsFragment.closeSheet()
            onOptionsItemSelected(menuItem)
            val intent = Intent(applicationContext, CartActivity::class.java)
            startActivity(intent)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_cart) {
            // setFragment(new SchedulingFragment());
            return true
        } else if (id == R.id.action_search) {
            ProductsFragment.closeSheet()
            val intent = Intent(this@DashboardActivity, SearchProductActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.choose_company) {
            val companyList = dbHelper!!.allCompanies
            if (companyList.size > 0) {
                showCompanyDialog()
            } else {
                Toast.makeText(applicationContext, "No Company Found", Toast.LENGTH_SHORT).show()
            }
        } else if (id == R.id.choose_location) {
            if (isPermissionToChangeLocation) {
                if (locationDetailsl!!.size > 0) {
                    getLocationDialog(locationDetailsl)
                } else {
                    Toast.makeText(applicationContext, "No Location Found..!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "You Don't have permission to change location..!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        try {
            user1 = session1!!.userDetails
            locationCodem = user1!![SessionManager.KEY_LOCATION_CODE]
            menu.getItem(0).setVisible(true)
            menu.getItem(0).setTitle("Location : $locationCodem")
        } catch (ex: Exception) {
        }
        return true
    }

    @get:Throws(JSONException::class)
    private val locationList: Unit
        private get() {
            val requestQueue = Volley.newRequestQueue(this)
            val url = Utils.getBaseUrl(this) + "WarehouseList"
            // Initialize a new JsonArrayRequest instance
            Log.w("Given_url_location:", url)
            pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog!!.setTitleText("Loading Location...")
            pDialog!!.setCancelable(false)
            pDialog!!.show()
            locationDetailsl = ArrayList()
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.GET, url,
                null,
                Response.Listener { response: JSONObject ->
                    try {
                        Log.w("locationlist:", response.toString())
                        pDialog!!.dismiss()
                        val statusCode = response.optString("statusCode")
                        val statusMessage = response.optString("statusMessage")
                        if (statusCode == "1") {
                            val locationModel = NewLocationModel()
                            val locationArray = response.optJSONArray("responseData")
                            for (i in 0 until locationArray.length()) {
                                val jsonObject = locationArray.getJSONObject(i)
                                val locationDetails = NewLocationModel.LocationDetails()
                                locationDetails.locationName = jsonObject.optString("whsName")
                                locationDetails.locationCode = jsonObject.optString("whsCode")
                                locationDetailsl!!.add(locationDetails)
                            }
                            if (locationDetailsl!!.size > 0) {
                                locationModel.setLocationDetailsArrayList(locationDetailsl)
                                Utils.setLocationList(locationDetailsl)
                            }
                            Log.w("locatretun1:", "" + Utils.getLocationList().size)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error: VolleyError ->
                    // Do something when error occurred
                    pDialog!!.dismiss()
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
        }

    private fun getLocationDialog(locationDetailsArrayList: ArrayList<NewLocationModel.LocationDetails>?) {
        val builderSingle = AlertDialog.Builder(this)
        builderSingle.setTitle("Select Location")
        val arrayAdapter = ArrayAdapter<String>(this, R.layout.selection_single_dialog)
        for (i in locationDetailsArrayList!!.indices) {
            arrayAdapter.add(locationDetailsArrayList[i].locationName + " - " + locationDetailsArrayList[i].locationCode)
        }
        val checkedItem = Utils.getLocationCodeIndex(locationDetailsArrayList, locationCodem)
        fromWarehouseCode = locationCodem
        builderSingle.setSingleChoiceItems(arrayAdapter, checkedItem) { dialog, which ->
            fromWarehouseCode = locationDetailsArrayList[which].locationCode
            if (fromWarehouseCode != null && !fromWarehouseCode!!.isEmpty()) {
                if (locationCodem != fromWarehouseCode) {
                    Log.w("NewLocationSelected:", fromWarehouseCode.toString())
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Same location should not choose..!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(applicationContext, "Select Location", Toast.LENGTH_SHORT).show()
            }
        }
        builderSingle.setPositiveButton("Select") { dialogInterface, i ->
            Utils.setSessionForLocation(applicationContext, fromWarehouseCode)
            val intent = Intent(applicationContext, DashboardActivity::class.java)
            Log.w("locationCodemvv",""+fromWarehouseCode+locationCodem)
            startActivity(intent)
            finish()
        }
        builderSingle.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
        builderSingle.setCancelable(false)
        builderSingle.show()
    }

    private fun showCompanyDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ProductsFragment.closeSheet()
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.multiple_company_layout, viewGroup, false)

        //Now we need an AlertDialog.Builder object
        val builder = AlertDialog.Builder(this)

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)
        val companyListView = dialogView.findViewById<RecyclerView>(R.id.companyListView)
        val companyList = dbHelper!!.allCompanies
        companyListView.setHasFixedSize(true)
        var index = 0
        for (model in companyList) {
            if (model.getCompanyId() == companyCode) {
                val values = companyList[index]
                val values1 = companyList[0]
                companyList[0] = values
                companyList[index] = values1
                break
            }
            index++
        }
        // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        companyListView.layoutManager =
            LinearLayoutManager(this@DashboardActivity, LinearLayoutManager.VERTICAL, false)
        val companyAdapter = CompanyChooseAdapter(companyList, companyCode) { }
        companyListView.adapter = companyAdapter
        val yesButton = dialogView.findViewById<Button>(R.id.buttonYes)
        val noButton = dialogView.findViewById<Button>(R.id.buttonNo)
        //finally creating the alert dialog and displaying it
        dialog = builder.create()
        dialog!!.window!!.attributes.windowAnimations = R.style.PauseDialogAnimation
        dialog!!.setCancelable(false)
        dialog!!.show()
        yesButton.setOnClickListener {
            if (MainHomeActivity.newSelectedCompany != "0") {
                showConfirmDialog(MainHomeActivity.newSelectedCompanyName)
            } else {
                Toast.makeText(applicationContext, "Please select the Company", Toast.LENGTH_SHORT)
                    .show()
            }
            // dialog.dismiss();
        }
        noButton.setOnClickListener {
            dialog!!.dismiss()
            MainHomeActivity.newSelectedCompany = "0"
        }
    }

    fun showConfirmDialog(companyname: String) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle("Information")
        builder.setMessage("Are you sure want to Switch over $companyname ?")
        builder.setPositiveButton("YES") { dialogInterface, i ->
            dialog!!.dismiss()
            dialogInterface.dismiss()
            val intent = Intent(this@DashboardActivity, NewCompanySwitchActivity::class.java)
            intent.putExtra("from", "Dashboard")
            intent.putExtra("companyCode", MainHomeActivity.newSelectedCompany)
            intent.putExtra("locationCode", locationCodem)
            intent.putExtra("companyName", MainHomeActivity.newSelectedCompanyName)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("NO") { dialogInterface, i -> dialogInterface.dismiss() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    val creditLimit: Unit
        get() {
            // Initialize a new RequestQueue instance
            val requestQueue = Volley.newRequestQueue(this)
            val url = Utils.getBaseUrl(this) + "ReportCustomerCreditTermsList"
            // Initialize a new JsonArrayRequest instance
            Log.w("Given_Credit_URL:", url)
            val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.setTitleText("Loading Credit...")
            pDialog.setCancelable(false)
            pDialog.show()
            val jsonObjectRequest: JsonObjectRequest = object :
                JsonObjectRequest(Method.GET, url, null, Response.Listener { response: JSONObject ->
                    try {
                        creditLimitArrayList = ArrayList()
                        Log.w("Res_credit:", response.toString())
                        // Loop through the array elements
                        val uomArray = response.optJSONArray("responseData")
                        if (uomArray != null && uomArray.length() > 0) {
                            for (j in 0 until uomArray.length()) {
                                val obj = uomArray.getJSONObject(j)
                                val model = CreditLimitDialogResponse(
                                    obj.optString("balance"),
                                    obj.optString("creditLine"),
                                    obj.optString("customerCode"),
                                    obj.optString("customerName")
                                )
                                //                        creditLimitArrayList.add(0,new CreditLimitDialogResponse("550.80","550.80",
//                                "",""));
                                creditLimitArrayList.add(model)
                            }
                        }
                        pDialog.dismiss()
                        if (creditLimitArrayList.size > 0) {
                            for (i in creditLimitArrayList.indices) {
                                // if(Double.parseDouble(creditLimitArrayList.get(i).getCreditLine()) > 0) {
                              //  Log.w("bal_dash", ".." + creditLimitArrayList[i].balance.toDouble())
                                if (creditLimitArrayList[i].creditLine.toDouble() > 0.00) {
                                    if (creditLimitArrayList[i].balance.toDouble() >= creditLimitArrayList[i].creditLine.toDouble()) {
                                        creditLimitListFilter.add(creditLimitArrayList[i])
                                    }
                                }

                                //}
                            }
                            if (creditLimitListFilter.size > 0) {
                                runOnUiThread { showCreditdialog(creditLimitListFilter) }
                            }
                        } else {
                            Toast.makeText(this, "No credit data", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error: VolleyError ->
                    // Do something when error occurred
                    pDialog.dismiss()
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
        }

    fun setCreditAdater(arrayList: ArrayList<CreditLimitDialogResponse>) {
        // total.setText(splitModelList.get(0).getTotal());
        itemSize_creditl!!.text = "Customer " + arrayList.size
        rv_crditLimit!!.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        creditLimitDialogAdapter = CreditLimitDialogAdapter(this, arrayList)
        rv_crditLimit!!.adapter = creditLimitDialogAdapter
    }

    override fun onResume() {
        dbHelper!!.removeAllReturn()
        dbHelper!!.removeAllInvoiceItems()
        dbHelper!!.removeAllItems()
        Utils.clearCustomerSession(this)
        super.onResume()
    }

    companion object {
        var creditLimitArrayList = ArrayList<CreditLimitDialogResponse>()
        var productList: ArrayList<ProductsModel>? = null
        var newSelectedCompany = "0"
        var newSelectedCompanyName = ""
        var textCartItemCount: TextView? = null
        var mCartItemCount = 0
        fun setupBadge() {
            mCartItemCount = helper!!.numberOfRows()
            if (textCartItemCount != null) {
                if (mCartItemCount == 0) {
                    if (textCartItemCount!!.visibility != View.GONE) {
                        textCartItemCount!!.visibility = View.GONE
                    }
                } else {
                    textCartItemCount!!.text = Math.min(mCartItemCount, 99).toString()
                    if (textCartItemCount!!.visibility != View.VISIBLE) {
                        textCartItemCount!!.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}
