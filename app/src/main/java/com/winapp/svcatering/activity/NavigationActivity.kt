package com.winapp.svcatering.activity

import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.winapp.svcatering.R
import com.winapp.svcatering.dashboard.GraphDashboardActivity
import com.winapp.svcatering.db.DBHelper
import com.winapp.svcatering.receipts.ReceiptsListActivity
import com.winapp.svcatering.salesreturn.NewSalesReturnListActivity
import com.winapp.svcatering.utils.Constants
import com.winapp.svcatering.utils.NetworkChangeReceiver
import com.winapp.svcatering.utils.SessionManager
import com.winapp.svcatering.utils.SharedPreferenceUtil
import com.winapp.svcatering.utils.Utils

open class NavigationActivity : AppCompatActivity() {
    var drawerLayout: DrawerLayout? = null
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    var toolbar: Toolbar? = null
    var mNavigationView: NavigationView? = null
    private val mCurrentSelectedPosition = 0
    @JvmField
    var session: SessionManager? = null
    private var sharedPreferenceUtil: SharedPreferenceUtil? = null
    var isCheckedSO1 = false
    var isCheckedInvoice1 = false
    var isCheckedCustomer1 = false
    var isCheckedSalesReturn1 = false
    var isAPIInvoice = false
    var locationCode: String? = null

    @JvmField
    var user: HashMap<String, String>? = null
    private var lastBackPressTime: Long = 0
    private var loginPreferences: SharedPreferences? = null
    private var loginPrefsEditor: SharedPreferences.Editor? = null
    var networkChangeReceiver: NetworkChangeReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        mNavigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Log.w("activity_cg", javaClass.simpleName.toString())

        networkChangeReceiver = NetworkChangeReceiver()
        drawerLayout = findViewById(R.id.drawer_layout)
        // Set the Preference value in edittext for Remembering the values
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        loginPrefsEditor = loginPreferences!!.edit()
        helper = DBHelper(this)
        session = SessionManager(this)
        sharedPreferenceUtil = SharedPreferenceUtil(this)
        user = session!!.userDetails

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout!!.setDrawerListener(actionBarDrawerToggle)
        mNavigationView!!.setItemIconTintList(null)
        try {
            val headerView = mNavigationView!!.getHeaderView(0)
            // get user name and email textViews
            val userName = headerView.findViewById<TextView>(R.id.navigation_username)
            val companyName = headerView.findViewById<TextView>(R.id.navigation_company_name)
            // set user name and email
            userName.text = user!!.get(SessionManager.KEY_USER_NAME)
            companyName.text = user!!.get(SessionManager.KEY_COMPANY_NAME)
            locationCode = user!![SessionManager.KEY_LOCATION_CODE]

        } catch (ex: Exception) {
        }
        val userRolls = helper!!.userPermissions
        val menu = mNavigationView!!.getMenu()
        val home = menu.findItem(R.id.navigation_item_home)
        val schedule = menu.findItem(R.id.navigation_item_schedule)
        val catalog = menu.findItem(R.id.navigation_item_catalog)
        val allcatagories = menu.findItem(R.id.navigation_item_catagories)
        val customers = menu.findItem(R.id.navigation_item_customer)
        val salesorder = menu.findItem(R.id.navigation_item_salesorder)
        val invoice = menu.findItem(R.id.navigation_item_invoice)
        val purchase_invoice = menu.findItem(R.id.navigation_item_purchase)
        val receipts = menu.findItem(R.id.navigation_item_receipts)
        val settings = menu.findItem(R.id.navigation_item_settings)
        val salesreturn = menu.findItem(R.id.navigation_item_sales_return)

        val settings1 = helper!!.settings
        if (settings1 != null) {
            if (settings1.size > 0) {
                for (model in settings1) {
//                   if (model.settingName == "showSalesOrder") {
//                        Log.w("SettingNameSO:", model.settingName)
//                        Log.w("SettingValueSO:", model.settingValue)
//                        isCheckedSO1 = if (model.settingValue == "True") {
//                            true
//                        } else {
//                            false
//                        }
//                    }
                    if (model.settingName == "showInvoice") {
                        Log.w("SettingNameInv:", model.settingName)
                        Log.w("SettingValueInv:", model.settingValue)
                        isCheckedInvoice1 = if (model.settingValue == "True") {
                            true
                        } else {
                            false
                        }
                    }   else if (model.settingName == "showCustomer") {
                        Log.w("SettingNameCust:", model.settingName)
                        Log.w("SettingValueCust:", model.settingValue)
                        isCheckedCustomer1 = if (model.settingValue == "True") {
                            true
                        } else {
                            false
                        }
                    }
//                   else if (model.settingName == "showSalesReturn") {
//                       Log.w("SettingNameSaleRet:", model.settingName)
//                       Log.w("SettingValueSaleRet:", model.settingValue)
//                       isCheckedSalesReturn1 = if (model.settingValue == "True") {
//                           true
//                       } else {
//                           false
//                       }
//                   }else if (model.settingName == "showAPInvoice") {
//                       Log.w("SettingNameApiInv:", model.settingName)
//                       Log.w("SettingValueApiInv:", model.settingValue)
//                       isAPIInvoice = if (model.settingValue == "True") {
//                           purchase_invoice.setVisible(true)
//                           true
//                       } else {
//                           purchase_invoice.setVisible(false)
//                           false
//                       }
//                   }
                }
            }
        }


        if (userRolls.size > 0) {
            for (roll in userRolls) {
                when (roll.formName) {
//                    "Catalog" -> if (roll.havePermission == "true") {
//                        catalog.setVisible(true)
//                    } else {
//                        catalog.setVisible(false)
//                    }

//                    "Merchandise Schedule" -> if (roll.havePermission == "true") {
//                        schedule.setVisible(true)
//                    } else {
//                        schedule.setVisible(false)
//                    }

                    "Customer List" -> if (roll.havePermission == "true") {
                        customers.setVisible(true)
                    } else {
                        customers.setVisible(false)
                    }

//                    "Sales Order" -> if (roll.havePermission == "true") {
//                        salesorder.setVisible(true)
//                    } else {
//                        salesorder.setVisible(false)
//                    }

                    "Invoice" -> if (roll.havePermission == "true") {
                        invoice.setVisible(true)
                    } else {
                        invoice.setVisible(false)
                    }

                    "Receipts" -> if (roll.havePermission == "true") {
                        receipts.setVisible(true)
                    } else {
                        receipts.setVisible(false)
                    }

                    "Settings" -> if (roll.havePermission == "true") {
                        settings.setVisible(true)
                    } else {
                        settings.setVisible(false)
                    }

//                    "Sales Return" -> if (roll.havePermission == "true") {
//                        salesreturn.setVisible(true)
//                    } else {
//                        salesreturn.setVisible(false)
//                    }
//
//                    "Sales Return" -> if (roll.havePermission == "true") {
//                        purchase_invoice.setVisible(true)
//                    } else {
//                        purchase_invoice.setVisible(false)
//                    }
                }
            }
        }
        // target.setVisible(false);
        mNavigationView!!.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { menuItem ->
            menuItem.setChecked(true)
            val itemId = menuItem.itemId
            if (itemId == R.id.navigation_item_home) { // setFragment(new HomeFragment());
                val intent = Intent(this@NavigationActivity, DashboardActivity::class.java)
                startActivity(intent)
                drawerLayout!!.closeDrawers()
                // mCurrentSelectedPosition = 0;
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_item_schedule) {
                val intent: Intent // setFragment(new SchedulingFragment());
                intent = Intent(this@NavigationActivity, SchedulingActivity::class.java)
                startActivity(intent)
                drawerLayout!!.closeDrawers()
                // mCurrentSelectedPosition = 1;
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_item_catalog) {
                    val intent: Intent //setFragment(new DashboardFragment());
                    intent = Intent(this@NavigationActivity, MainHomeActivity::class.java)
                    startActivity(intent)
                // mCurrentSelectedPosition = 2;
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_item_invoice) {
                if (isCheckedInvoice1 && (locationCode != null && !locationCode!!.isEmpty()) ) {
                    val intent: Intent //setFragment(new TableListFragment());
                    intent = Intent(this@NavigationActivity, NewInvoiceListActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this, "Check Location and Permission", Toast.LENGTH_SHORT).show()
                }
                // mCurrentSelectedPosition = 3;
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            }else if (itemId == R.id.navigation_item_purchase) {
                if (isAPIInvoice && (locationCode != null && !locationCode!!.isEmpty()) ) {
                    val intent: Intent //setFragment(new TableListFragment());
                    intent = Intent(this@NavigationActivity, PurchaseInvoiceListActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this, "Check Location and Permission", Toast.LENGTH_SHORT).show()
                }
                // mCurrentSelectedPosition = 3;
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            }
            else if (itemId == R.id.navigation_item_settings) {
                val intent: Intent
                intent = Intent(this@NavigationActivity, SettingActivity::class.java)
                startActivity(intent)
                // mCurrentSelectedPosition = 4;
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_item_salesorder) {
                if (isCheckedSO1 && (locationCode != null && !locationCode!!.isEmpty())) {
                    val intent: Intent
                    intent = Intent(this@NavigationActivity, SalesOrderListActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this, "Check Location and Permission", Toast.LENGTH_SHORT).show()
                }
                // mCurrentSelectedPosition = 5;
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_item_dashboard) {
                val intent: Intent
                intent = Intent(this@NavigationActivity, GraphDashboardActivity::class.java)
                startActivity(intent)
                // mCurrentSelectedPosition = 6;
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_item_customer) {
                if (isCheckedCustomer1 && (locationCode != null && !locationCode!!.isEmpty())) {
                    val intent1 = Intent(this@NavigationActivity, CustomerListActivity::class.java)
                    intent1.putExtra("from", "cus")
                    intent1.putExtra("Message", "Open")
                    startActivity(intent1)
                }  else{
                    Toast.makeText(this, "Check Location and Permission", Toast.LENGTH_SHORT).show()
                }
                //mCurrentSelectedPosition = 7;
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_item_receipts) {
                val intent: Intent
                intent = Intent(this@NavigationActivity, ReceiptsListActivity::class.java)
                startActivity(intent)
                // mCurrentSelectedPosition=9;
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_item_catagories) {
                    val intent: Intent
                    intent = Intent(this@NavigationActivity, CategoriesActivity::class.java)
                    startActivity(intent)

                // mCurrentSelectedPosition=8;
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_item_sales_return) {
                if (isCheckedSalesReturn1 && (locationCode != null && !locationCode!!.isEmpty())) {
                    val intent: Intent /*  intent=new Intent(NavigationActivity.this, SalesReturnActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();*/
                    intent = Intent(this@NavigationActivity, NewSalesReturnListActivity::class.java)
                    startActivity(intent)
                }   else{
                    Toast.makeText(this, "Check Location and Permission", Toast.LENGTH_SHORT).show()
                }
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_product_analysis) {
                val intent: Intent
                intent = Intent(this@NavigationActivity, ProductStockAnalysisActivity::class.java)
                startActivity(intent)
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_stock_take) {
                val intent: Intent
                intent = Intent(this@NavigationActivity, StockTakeListActivity::class.java)
                intent.putExtra("docNum", "")
                intent.putExtra("transferType", "")
                startActivity(intent)
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            }
            else if (itemId == R.id.navigation_settlement) {
                val intent: Intent
                intent = Intent(this@NavigationActivity, SettlementListActivity::class.java)
                startActivity(intent)
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_reports) {
                val intent: Intent
                intent = Intent(this@NavigationActivity, ReportsActivity::class.java)
                startActivity(intent)
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_transfer) {
                val intent: Intent
                intent = Intent(this@NavigationActivity, TransferListProductActivity::class.java)
                intent.putExtra("docNum", "")
                intent.putExtra("transferType", "")
                startActivity(intent)
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_stock_request) {
                val intent: Intent
                intent = Intent(this@NavigationActivity, StockRequestListActivity::class.java)
                intent.putExtra("docNum", "")
                intent.putExtra("transferType", "")
                startActivity(intent)
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_expense) {
                val intent: Intent
                intent = Intent(this@NavigationActivity, NewExpenseModuleListActivity::class.java)
                intent.putExtra("docNum", "")
                intent.putExtra("transferType", "")
                startActivity(intent)
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            } else if (itemId == R.id.navigation_item_signout) {
                showSignoutAlert()
                drawerLayout!!.closeDrawers()
                return@OnNavigationItemSelectedListener true
            }
            true
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle!!.syncState()
    }

    fun showSignoutAlert() {
        val alertDialog = AlertDialog.Builder(this@NavigationActivity)
        alertDialog.setTitle("Warning..!")
        alertDialog.setMessage("Are you sure want to Logout this Session?")
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(
            "YES"
        ) { dialog, id -> //                        loginPrefsEditor.clear();
//                        loginPrefsEditor.commit();
            Utils.clearCustomerSession(this@NavigationActivity)
            helper!!.removeAllItems()
            // helper.removeSettings();
            session!!.logoutUser()
            clearPreference()

            dialog.cancel()
        }
        alertDialog.setNegativeButton(
            "NO"
        ) { dialog, id -> dialog.cancel() }
        val alert11 = alertDialog.create()
        alert11.show()
    }

    fun clearPreference(){
        sharedPreferenceUtil!!.setStringPreference(
            sharedPreferenceUtil!!.KEY_SETTING_INV_UOM,
            ""
        )
        sharedPreferenceUtil!!.setStringPreference(
            sharedPreferenceUtil!!.KEY_SETTING_SO_UOM,
            ""
        )
        sharedPreferenceUtil!!.setStringPreference(
            sharedPreferenceUtil!!.KEY_SETTING_RETURN_UOM,
            ""
        )
        sharedPreferenceUtil!!.setStringPreference(
            sharedPreferenceUtil!!.KEY_SETTLEMENT_NEXT_DATE,
            "false"
        )
        //mahudoom given "shortCode": "TRAN",
        sharedPreferenceUtil!!.setStringPreference(
            sharedPreferenceUtil!!.KEY_SHORT_CODE,
            ""
        )
        sharedPreferenceUtil!!.setStringPreference(
            sharedPreferenceUtil!!.KEY_LAST_PRICE,
            ""
        )
        sharedPreferenceUtil!!.setStringPreference(
            sharedPreferenceUtil!!.KEY_TOTAL_SALES,
            ""
        )
        sharedPreferenceUtil!!.setStringPreference(
            sharedPreferenceUtil!!.KEY_USER_MIDDLE_NAME,
            ""
        )
        sharedPreferenceUtil!!.setStringPreference(
            Constants.KEY_ADDRESS_ZONE_CODE, "")
        sharedPreferenceUtil!!.setStringPreference(
            Constants.KEY_ADDRESS_ZONE_NAME, "")

    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        /* if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/if (lastBackPressTime < System.currentTimeMillis() - 4000) {
            val snackbar = Snackbar
                .make(drawer, "Click BACK again to exit", Snackbar.LENGTH_LONG)
            snackbar.show()
            lastBackPressTime = System.currentTimeMillis()
        } else {
            showCloseAlert()
            // super.onBackPressed();
            /*  Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            finishAffinity();
            android.os.Process.killProcess(android.os.Process.myPid());*/
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(networkChangeReceiver)
    }

    fun showCloseAlert() {
        val builder = AlertDialog.Builder(this@NavigationActivity)
        builder.setCancelable(false)
        builder.setTitle("Warning..!")
        builder.setMessage("Are You sure want to exit the App?")
        builder.setPositiveButton("YES") { dialogInterface, i ->
            val a = Intent(Intent.ACTION_MAIN)
            a.addCategory(Intent.CATEGORY_HOME)
            a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(a)
            finishAffinity()
            Process.killProcess(Process.myPid())
        }
        builder.setNegativeButton("NO") { dialogInterface, i -> dialogInterface.dismiss() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    companion object {
        @JvmField
        var helper: DBHelper? = null
    }
}