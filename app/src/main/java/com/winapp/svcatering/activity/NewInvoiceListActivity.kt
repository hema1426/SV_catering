package com.winapp.svcatering.activity

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import com.cete.dynamicpdf.Document
import com.cete.dynamicpdf.Font
import com.cete.dynamicpdf.Grayscale
import com.cete.dynamicpdf.Page
import com.cete.dynamicpdf.PageOrientation
import com.cete.dynamicpdf.PageSize
import com.cete.dynamicpdf.TextAlign
import com.cete.dynamicpdf.VAlign
import com.cete.dynamicpdf.pageelements.Label
import com.cete.dynamicpdf.pageelements.Table2
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import com.winapp.svcatering.BuildConfig
import com.winapp.svcatering.CommonMethods
import com.winapp.svcatering.R
import com.winapp.svcatering.adapter.InvoiceAdapter
import com.winapp.svcatering.adapter.SelectCustomerAdapter
import com.winapp.svcatering.adapter.SortAdapter
import com.winapp.svcatering.db.DBHelper
import com.winapp.svcatering.fragments.AllInvoices
import com.winapp.svcatering.fragments.CustomerFragment
import com.winapp.svcatering.fragments.PaidInvoices
import com.winapp.svcatering.fragments.UnpaidInvoices
import com.winapp.svcatering.model.AddressZoneModel
import com.winapp.svcatering.model.AppUtils
import com.winapp.svcatering.model.CustomerDetails
import com.winapp.svcatering.model.CustomerModel
import com.winapp.svcatering.model.InvoiceModel
import com.winapp.svcatering.model.InvoicePrintPreviewModel
import com.winapp.svcatering.model.UserListModel
import com.winapp.svcatering.multiselectspinner.MultiSelectSpinnerView
import com.winapp.svcatering.multiselectspinner.MultipleSelectSpinnerPojo
import com.winapp.svcatering.printpreview.InvoicePrintPreviewActivity
import com.winapp.svcatering.printpreview.NewInvoicePrintPreviewActivity
import com.winapp.svcatering.thermalprinter.PrinterUtils
import com.winapp.svcatering.utils.Constants
import com.winapp.svcatering.utils.ImageUtil
import com.winapp.svcatering.utils.Pager
import com.winapp.svcatering.utils.PdfUtils
import com.winapp.svcatering.utils.SessionManager
import com.winapp.svcatering.utils.SharedPreferenceUtil
import com.winapp.svcatering.utils.UserAdapter
import com.winapp.svcatering.utils.Utils
import com.winapp.svcatering.zebraprinter.TSCPrinter
import com.winapp.svcatering.zebraprinter.ZebraPrinterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Objects

class NewInvoiceListActivity : NavigationActivity(), View.OnClickListener, OnTabSelectedListener,
    OnPageChangeListener, OnLoadCompleteListener, OnItemSelectedListener {
    // Define the Button Variables
    private var allInvoiceButton: TextView? = null
    private var paidInvoiceButton: TextView? = null
    private var outstandingInvoiceButton: TextView? = null
    private var invoiceView: View? = null
    private var paidView: View? = null
    private val selectedTag: String? = null
    private var menu: Menu? = null
    private var invoiceListView: RecyclerView? = null
    private val invoiceAdapter: InvoiceAdapter? = null
    private val invoiceList: ArrayList<InvoiceModel>? = null
    private var pDialog: SweetAlertDialog? = null
    var pageNo = 1
     var session1: SessionManager? = null
      var user1: HashMap<String, String>? = null
    private var companyId: String? = null

    //This is our tablayout
    private var tabLayout: TabLayout? = null

    //This is our viewPager
    private var viewPager: ViewPager? = null
    private var operationLayout: LinearLayout? = null
    private var emptyLayout: LinearLayout? = null
//    override var toolbar: Toolbar? = null
    private val lettersRecyclerview: RecyclerView? = null
    private var customerView: RecyclerView? = null
    private val adapter: SortAdapter? = null
    private val letters: ArrayList<String>? = null
    private val PartSpinner: SearchableSpinner? = null
    private val PartName: ArrayList<String>? = null
    private val PartId: List<String>? = null
    private val btnCancel: Button? = null
    private val customerName: TextView? = null
    private var customerNameEdittext: EditText? = null
    private var customerNameAdapter: SelectCustomerAdapter? = null
    private val dateText: TextView? = null
    private val userName: TextView? = null
    private var companyCode: String? = null
    private val customerId: String? = null
    private var dbHelper: DBHelper? = null
    private var customerList: ArrayList<CustomerModel>? = null
    private var totalCustomers: TextView? = null
    private var cancelSheet: Button? = null
    private var customerDetails: ArrayList<CustomerDetails>? = null
    private var netTotalText: TextView? = null
    private var optionCancel: TextView? = null
    private var transLayout: LinearLayout? = null
    private var searchFilterView: View? = null
    private var customerNameText: EditText? = null
    private var fromDate: EditText? = null
    private var fromDatelay: LinearLayout? = null
    private var toDate: EditText? = null
    private var phoneNoEd: EditText? = null
    var zoneSpinner: MultiSelectSpinnerView? = null
    private var invoiceStatus: Spinner? = null
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private val mHour = 0
    private val mMinute = 0
    private var searchButton: Button? = null
    private var cancelSearch: Button? = null
    private var editInvoice: FloatingActionButton? = null
    private var deleteInvoice: FloatingActionButton? = null
    private var cashCollection: FloatingActionButton? = null
    private var printPreview: FloatingActionButton? = null
    private var invoicePrint: FloatingActionButton? = null
    private var deliveryOrderPrint: FloatingActionButton? = null
    private var doPrintPreview: FloatingActionButton? = null
    private var cancelInvoice: FloatingActionButton? = null
    private var rootLayout: FrameLayout? = null
    private var mainLayout: LinearLayout? = null
    private var invoiceHeaderDetails: ArrayList<InvoicePrintPreviewModel>? = null
    private var invoicePrintList: ArrayList<InvoicePrintPreviewModel.InvoiceList>? = null
    var printInvoiceNumber: String? = null
    var noOfCopy: String? = null
    var DOPrint = "false"
    private var printerMacId: String? = null
    private var printerType: String? = null
    private var sharedPreferenceUtil: SharedPreferenceUtil? = null
    private var sharedPreferences: SharedPreferences? = null
    private var sharedPref_billdisc: SharedPreferences? = null
    private var myEdit: SharedPreferences.Editor? = null
    private var progressLayout: View? = null
    var redirectInvoice = false
    private val customerSelectCode = 23
    lateinit var invoiceTitlelistArray: Array<String>
    lateinit var invoicepricelistArray: Array<String>
    lateinit var invoiceQNTlistArray: Array<String>
    lateinit var invoiceNOlistArray: Array<String>
    var invoiceNO = ""
    var path_pdf: TextView? = null
    var storagePath: File? = null
    var pdfView: PDFView? = null
    var pageNumber = 0
    var pdfFileName: String? = null
    var pdfFile: File? = null
    private var company_name: String? = null
    private var company_address1: String? = null
    private var company_address2: String? = null
    private var company_address3: String? = null
    private var pdfGenerateDialog: ProgressDialog? = null
    var oustandingAmount = "0.0"
    var emptyTextView: TextView? = null
    var isInvoicePrint = true
    private var username: String? = null
    private var pd: ProgressDialog? = null
    var shareLayout: LinearLayout? = null
    var printLayout: LinearLayout? = null
    var cancelButton: Button? = null
    var shareMode = "Share"
    var createInvoiceSetting = "false"
    private var salesReturnList: ArrayList<InvoicePrintPreviewModel.SalesReturnList>? = null
    private var selectCustomerCode: String? = ""
    private val FILTER_CUSTOMER_CODE = 134
    private var salesManSpinner: Spinner? = null
    private var selectedUser: String? = ""
    private var usersList: ArrayList<UserListModel>? = null
    var zoneMutipleCode: String? = ""

    var addressZoneList: ArrayList<AddressZoneModel> = ArrayList()

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val contentFrameLayout = findViewById<FrameLayout>(R.id.content_frame)
        layoutInflater.inflate(R.layout.activity_new_invoice_list, contentFrameLayout)
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Invoices")
        pd = ProgressDialog(this@NewInvoiceListActivity)
        pd!!.setMessage("Downloading Product Image, please wait ...")
        pd!!.isIndeterminate = true
        pd!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        pd!!.setCancelable(false)
        pd!!.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            "CANCEL"
        ) { dialog, whichButton -> dialog.dismiss() }
        pd!!.setProgressNumberFormat("%1d KB/%2d KB")
        pdfGenerateDialog = ProgressDialog(this)
        pdfGenerateDialog!!.setCancelable(false)
        pdfGenerateDialog!!.setMessage("Invoice Pdf generating please wait...")
        allInvoiceButton = findViewById(R.id.btn_all_invoice)
        paidInvoiceButton = findViewById(R.id.btn_paid_invoice)
        outstandingInvoiceButton = findViewById(R.id.btn_outstanding_invoice)

        allInvoiceButton!!.setOnClickListener(this)
        paidInvoiceButton!!.setOnClickListener(this)
        outstandingInvoiceButton!!.setOnClickListener(this)
        invoiceView = findViewById(R.id.invoice_view)
        paidView = findViewById(R.id.paid_view)
        invoiceListView = findViewById(R.id.invoiceList)
        phoneNoEd = findViewById(R.id.phoneNo_value)
        zoneSpinner = findViewById(R.id.zoneSpinnerl)
       // spinner_addrl = findViewById(R.id.spinner_addr_zone)
        var zoneSpinner: MultiSelectSpinnerView? = null

        session1 = SessionManager(this)
        user = session1!!.userDetails
        companyId = user!!.get(SessionManager.KEY_COMPANY_CODE)
        username = user!!.get(SessionManager.KEY_USER_NAME)
        sharedPreferenceUtil = SharedPreferenceUtil(this)
        sharedPref_billdisc = getSharedPreferences("BillDiscPref", MODE_PRIVATE)
        myEdit = sharedPref_billdisc!!.edit()

        visibleFragment = "invoices"
        operationLayout = findViewById(R.id.operation_layout)
        emptyLayout = findViewById(R.id.empty_layout)
        emptyTextView = findViewById(R.id.empty_text)
        customerNameEdittext = findViewById(R.id.customer_search)
        netTotalText = findViewById(R.id.net_total_value)
        session1 = SessionManager(this)
        dbHelper = DBHelper(this)
        companyCode = user!!.get(SessionManager.KEY_COMPANY_CODE)
        locationCode1 = user!!.get(SessionManager.KEY_LOCATION_CODE)
        Log.w(
            "bankDetail ", "" + user!!.get(SessionManager.KEY_PAY_NOW) + " bank " + user!!.get(
                SessionManager.KEY_BANK) + " cheque " + user!!.get(SessionManager.KEY_CHEQUE))

        shortCodeStr = sharedPreferenceUtil!!.getStringPreference(
            sharedPreferenceUtil!!.KEY_SHORT_CODE, ""
        )
        zoneStrCode = sharedPreferenceUtil!!.getStringPreference(
                Constants.KEY_ADDRESS_ZONE_CODE,
                Constants.DEFAULT_STRING
        )
        zoneStrName = sharedPreferenceUtil!!.getStringPreference(
                Constants.KEY_ADDRESS_ZONE_NAME,
                Constants.DEFAULT_STRING
        )
     Log.w("zoneStrCode",""+zoneStrCode)

        customerView = findViewById(R.id.customerList)
        totalCustomers = findViewById(R.id.total_customers)
        cancelSheet = findViewById(R.id.cancel_sheet)
        viewPager = findViewById(R.id.pager)
        searchFilterView = findViewById(R.id.search_filter)
        invoiceStatus = findViewById(R.id.invoice_status)
        customerNameText = findViewById(R.id.customer_name_value)
        fromDate = findViewById(R.id.from_date)
        fromDatelay = findViewById(R.id.fromdateLay)
        toDate = findViewById(R.id.to_date)
        searchButton = findViewById(R.id.btn_search)
        transLayout = findViewById(R.id.trans_layout)
        customerLayout = findViewById(R.id.customer_layout)
        invoiceOptionLayout = findViewById(R.id.invoice_option)
        optionCancel = findViewById(R.id.option_cancel)
        cancelSheet = findViewById(R.id.cancel_sheet)
        cancelSearch = findViewById(R.id.btn_cancel)
        invoiceCustomerName = findViewById(R.id.invoice_name)
        invoiceNumber = findViewById(R.id.sr_no)
        editInvoice = findViewById(R.id.edit_invoice)
        deleteInvoice = findViewById(R.id.delete_invoice)
        cashCollection = findViewById(R.id.cash_collection)
        printPreview = findViewById(R.id.print_preview)
        editInvoiceLayout = findViewById(R.id.edit_invoice_layout)
        deleteInvoiceLayout = findViewById(R.id.delete_invoice_layout)
        cashCollectionLayout = findViewById(R.id.cash_collection_layout)
        printPreviewLayout = findViewById(R.id.preview_invoice_layout)
        duplicateInvoiceLayout = findViewById(R.id.duplicate_inv_layout)
        cancelInvoiceLayout = findViewById(R.id.cancel_invoice_layout)
        cancelInvoice = findViewById(R.id.cancel_invoice)
        rootLayout = findViewById(R.id.rootLayout)
        mainLayout = findViewById(R.id.main_layout)
        progressLayout = findViewById(R.id.progress_layout)
        pdfView = findViewById(R.id.pdfView)
        pdfViewLayout = findViewById(R.id.pdf_layout)
        invoicePrint = findViewById(R.id.invoice_print)
        invoicePrintLayout = findViewById(R.id.invoice_print_layout)
        doPrintLayout = findViewById(R.id.do_print_layout)
        deliveryOrderPrint = findViewById(R.id.do_print)
        doPrintPreview = findViewById(R.id.do_print_preview)
        salesManSpinner = findViewById(R.id.salesman_spinner)
        salesManSpinner!!.setOnItemSelectedListener(this)

        shareLayout = findViewById(R.id.share_layout)
        printLayout = findViewById(R.id.print_layout)
        cancelButton = findViewById(R.id.cancel)
        myEdit!!.putString("billDisc_amt", "0.0")
        myEdit!!.putString("billDisc_percent", "0.0")
        myEdit!!.apply()
        company_name = user!!.get(SessionManager.KEY_COMPANY_NAME)
        company_address1 = user!!.get(SessionManager.KEY_ADDRESS1)
        company_address2 = user!!.get(SessionManager.KEY_ADDRESS2)
        company_address3 = user!!.get(SessionManager.KEY_ADDRESS3)
        Log.w("salenamm", user!!.get(SessionManager.KEY_SALESMAN_NAME)!!)
        Log.w("salephon", user!!.get(SessionManager.KEY_SALESMAN_PHONE)!!)
        Log.w("salemail", user!!.get(SessionManager.KEY_SALESMAN_EMAIL)!!)

        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE)
        printerType = sharedPreferences!!.getString("printer_type", "")
        printerMacId = sharedPreferences!!.getString("mac_address", "")
        isLastSales = sharedPreferenceUtil!!.getStringPreference(sharedPreferenceUtil!!.KEY_TOTAL_SALES, "")

        // PrinterUtils printerUtils=new PrinterUtils(this,printerMacId);
        //   printerUtils.connectPrinter();
        emptyTextView!!.setVisibility(View.VISIBLE)
        dbHelper!!.removeAllItems()
        dbHelper!!.removeAllInvoiceItems()
        AddInvoiceActivityOld.order_no = ""
        Log.w("Printer_Mac_Id:", printerMacId!!)
        Log.w("Printer_Type:", printerType!!)
        try {
            allUsers
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        if (Utils.getAddressZonelist().size > 0) {
            setZoneAdapter(Utils.getAddressZonelist()!!)
        } else {
            setAddressZone()
        }

        AppUtils.setProductsList(null)
        verifyStoragePermissions(this)
        checkPermission()
        val settings = dbHelper!!.settings
        if (settings != null) {
            if (settings.size > 0) {
                for (model in settings) {
                    if (model.settingName == "create_invoice_switch") {
                        Log.w("SettingNameInv:", model.settingName)
                        Log.w("SettingValueInv:", model.settingValue)
                        createInvoiceSetting = if (model.settingValue == "1") {
                            "true"
                        } else {
                            "false"
                        }
                    } else if (model.settingName == "showSignature") {
                        Log.w("SettingNamesign:", model.settingName)
                        Log.w("SettingValuesign:", model.settingValue)
                    }
                }
            }
        }

        //Initializing the tablayout
        tabLayout = findViewById<View>(R.id.tabLayout) as TabLayout
        //Adding the tabs using addTab() method
        tabLayout!!.addTab(tabLayout!!.newTab().setText("ALL"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("PAID"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("OUTSTANDING"))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        //Initializing viewPager
        viewPager = findViewById<View>(R.id.pager) as ViewPager
        //Creating our pager adapter
        val adapter = Pager(supportFragmentManager, tabLayout!!.tabCount)
        //Adding adapter to pager
        viewPager!!.adapter = adapter
        //Adding onTabSelectedListener to swipe views
        tabLayout!!.setOnTabSelectedListener(this)
        viewPager!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        viewPager!!.offscreenPageLimit = 3
        //  dbHelper.removeAllProducts();
        val bottomSheet = findViewById<View>(R.id.design_bottom_sheet)
        behavior = BottomSheetBehavior.from(bottomSheet)
        val c = Calendar.getInstance().time
        println("Current time => $c")
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = df.format(c)
        fromDate!!.setText(formattedDate)
        toDate!!.setText(formattedDate)

        // This function for print the invoice details
        if (intent != null) {
            printInvoiceNumber = intent.getStringExtra("printInvoiceNumber")
            noOfCopy = intent.getStringExtra("noOfCopy")
            //            if(getIntent().getStringExtra("DOPrint").equals("null") &&
//                    getIntent().getStringExtra("DOPrint") != null) {
//                DOPrint = getIntent().getStringExtra("DOPrint");
            //   }
            if (printInvoiceNumber != null && !printInvoiceNumber!!.isEmpty()) {
                try {
                    getInvoicePrintDetails(printInvoiceNumber, noOfCopy!!.toInt(), DOPrint)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }

        /*customerList=dbHelper.getAllCustomers();
        if (customerList!=null && customerList.size()>0){
            emptyTextView.setVisibility(View.GONE);
            setAdapter(customerList);
        }else {
            getCustomers();
            //new GetCustomersTask().execute();
        }*/

        //  getCustomers();
        val userRolls = dbHelper!!.userPermissions
        if (userRolls.size > 0) {
            for (roll in userRolls) {
//                if (roll.formName == "Edit Invoice") {
//                    if (roll.havePermission == "true") {
//                        editInvoiceLayout!!.setVisibility(View.GONE)
//                    } else {
//                        editInvoiceLayout!!.setVisibility(View.GONE)
//                    }
//                } else if (roll.formName == "Delete Invoice") {
//                    if (roll.havePermission == "true") {
//                        deleteInvoiceLayout!!.setVisibility(View.GONE)
//                    } else {
//                        deleteInvoiceLayout!!.setVisibility(View.GONE)
//                    }
//                }
            }
        }
        behavior!!.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> Log.i(
                        "BottomSheetCallback",
                        "BottomSheetBehavior.STATE_DRAGGING"
                    )

                    BottomSheetBehavior.STATE_SETTLING -> Log.i(
                        "BottomSheetCallback",
                        "BottomSheetBehavior.STATE_SETTLING"
                    )

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED")
                        if (invoiceOptionLayout!!.getVisibility() == View.VISIBLE) {
                            supportActionBar!!.setTitle("Select Option")
                        } else {
                            supportActionBar!!.setTitle("Invoices")
                        }
                        transLayout!!.setVisibility(View.VISIBLE)
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED")
                        supportActionBar!!.setTitle("Invoices")
                        transLayout!!.setVisibility(View.GONE)
                        if (redirectInvoice) {
                            CustomerFragment.isLoad = true
                            val intent = Intent(
                                this@NewInvoiceListActivity,
                                AddInvoiceActivityOld::class.java
                            )
                            intent.putExtra("customerId", selectCustomerId)
                            intent.putExtra("activityFrom", "Invoice")
                            startActivity(intent)
                            finish()
                        }
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> Log.i(
                        "BottomSheetCallback",
                        "BottomSheetBehavior.STATE_HIDDEN"
                    )
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.i("BottomSheetCallback", "slideOffset: $slideOffset")
            }
        })
//        duplicateInvoiceLayout!!.setOnClickListener(View.OnClickListener {
//            viewCloseBottomSheet()
//            if (invStatus != "Closed" && invStatus != "InProgress Invoice") {
//                try {
//                    getInvoiceEditDetails(invoiceNumberValue)
//                } catch (e: JSONException) {
//                    throw RuntimeException(e)
//                }
//                //   redirectActivity(customerNameStr, customerCodeStr, invoiceNumberValue);
//            } else {
//                Toast.makeText(
//                    applicationContext,
//                    "This Invoice already Closed",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }

        customerNameEdittext!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val cusname = editable.toString()
                if (!cusname.isEmpty()) {
                    filter(cusname)
                }
            }
        })
        cancelButton!!.setOnClickListener(View.OnClickListener { viewCloseBottomSheet() })
        cancelSheet!!.setOnClickListener(View.OnClickListener { viewCloseBottomSheet() })
        invoiceStatus!!.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                try {
                    (parent.getChildAt(0) as TextView).setTextColor(Color.BLUE)
                    (parent.getChildAt(0) as TextView).textSize = 12f
                    if (invoiceStatus!!.getSelectedItem() == "UNPAID") {
                        //  invoice_status = "O";
                        fromDatelay!!.setAlpha(0.4f)
                        fromDatelay!!.setEnabled(false)
                    } else {
                        fromDatelay!!.setAlpha(0.9f)
                        fromDatelay!!.setEnabled(true)
                    }
                } catch (ed: Exception) {
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        customerNameText!!.setOnClickListener(View.OnClickListener { //  isSearchCustomerNameClicked=true;
            //  viewCloseBottomSheet();
            val intent = Intent(applicationContext, FilterCustomerListActivity::class.java)
            startActivityForResult(intent, FILTER_CUSTOMER_CODE)
        })
        fromDate!!.setOnClickListener(View.OnClickListener { getDate(fromDate) })
        toDate!!.setOnClickListener(View.OnClickListener { getDate(toDate) })
        searchButton!!.setOnClickListener(View.OnClickListener {
            val customer_name = customerNameText!!.getText().toString()
            val sdformat = SimpleDateFormat("dd/MM/yyyy")
            var d1: Date? = null
            var d2: Date? = null
            try {
                d1 = sdformat.parse(fromDate!!.getText().toString())
                d2 = sdformat.parse(toDate!!.getText().toString())
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (d1!!.compareTo(d2) > 0) {
                Toast.makeText(
                    applicationContext,
                    "From date should not be greater than to date",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                searchFilterView!!.setVisibility(View.GONE)
                isSearchCustomerNameClicked = true
                // ((AllInvoices)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).filterValidation();
                val invoices = AllInvoices()
                try {
                    val oldFromDate = fromDate!!.getText().toString()
                    val oldToDate = toDate!!.getText().toString()
                    val fromDate = SimpleDateFormat("dd/MM/yyyy").parse(oldFromDate)
                    val toDate = SimpleDateFormat("dd/MM/yyyy").parse(oldToDate)
                    // Use SimpleDateFormat#format() to format a Date into a String in a certain pattern.
                    var fromDateString = SimpleDateFormat("yyyyMMdd").format(fromDate)
                    val toDateString = SimpleDateFormat("yyyyMMdd").format(toDate)
                    println("$fromDateString-$toDateString") // 2011-01-18
                    var invoice_status = ""
                    if (invoiceStatus!!.getSelectedItem() == "ALL") {
                        invoice_status = ""
                    } else if (invoiceStatus!!.getSelectedItem() == "PAID") {
                        invoice_status = "C"
                    } else if (invoiceStatus!!.getSelectedItem() == "UNPAID") {
                        invoice_status = "O"
                        fromDateString = ""
                    }
                    if (selectedUser != null && selectedUser!!.isNotEmpty()) {
                        username = selectedUser
                    }
                    var zoneMutipleCodeStr = ""
                    var phoneNoStr = ""

                    if(phoneNoEd!!.text.toString().isNotEmpty()){
                        phoneNoStr = phoneNoEd!!.text.toString()
                    }else{
                        phoneNoStr = ""
                    }
                    if(zoneMutipleCode!!.isNotEmpty()){
                        zoneMutipleCodeStr = zoneMutipleCode!!
                    }else{
                        zoneMutipleCodeStr = zoneStrCode!!
                    }
                    invoices.filterSearch(
                        this@NewInvoiceListActivity,
                        username,
                        selectCustomerCode,
                        invoice_status,
                        fromDateString,
                        toDateString,
                        locationCode1, zoneMutipleCodeStr,phoneNoStr!!
                    )
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                // AllInvoices.filterSearch(customer_name,invoiceStatus.getSelectedItem().toString(),fromDate.getText().toString(),toDate.getText().toString());
                invoiceStatus!!.setSelection(0)
            }
        })
        cancelSearch!!.setOnClickListener(View.OnClickListener {
            isSearchCustomerNameClicked = false
            selectCustomerId = ""
            customerNameText!!.setText("")
            invoiceStatus!!.setSelection(0)
            fromDate!!.setText(formattedDate)
            toDate!!.setText(formattedDate)
            searchFilterView!!.setVisibility(View.GONE)
            invoiceStatus!!.setSelection(0)
            val invoices = AllInvoices()
            invoices.filterCancel()
        })
        shareLayout!!.setOnClickListener(View.OnClickListener {
            if (behavior!!.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                behavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
            sharePdfView(pdfFile)
        })
        optionCancel!!.setOnClickListener(View.OnClickListener { viewCloseBottomSheet() })
//        editInvoiceLayout!!.setOnClickListener(View.OnClickListener {
//            try {
//                viewCloseBottomSheet()
//                getInvoiceDetails(invoiceNumberValue)
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//        })
//        editInvoice.setOnClickListener(View.OnClickListener {
//            try {
//                viewCloseBottomSheet()
//                getInvoiceDetails(invoiceNumberValue)
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//        })
//        deleteInvoiceLayout.setOnClickListener(View.OnClickListener {
//            showRemoveAlert(
//                invoiceNumberValue
//            )
//        })
            //deleteInvoice.setOnClickListener(View.OnClickListener { showRemoveAlert(invoiceNumberValue) })

        cashCollectionLayout!!.setOnClickListener(View.OnClickListener {
            viewCloseBottomSheet()
            val intent = Intent(this@NewInvoiceListActivity, CashCollectionActivity::class.java)
            intent.putExtra("customerCode", invoiceCustomerCodeValue)
            intent.putExtra("customerName", invoiceCustomerValue)
            startActivity(intent)
            finish()
        })
        cashCollection!!.setOnClickListener(View.OnClickListener {
            viewCloseBottomSheet()
            val intent = Intent(this@NewInvoiceListActivity, CashCollectionActivity::class.java)
            intent.putExtra("customerCode", invoiceCustomerCodeValue)
            intent.putExtra("customerName", invoiceCustomerValue)
            startActivity(intent)
            finish()
        })
        printPreview!!.setOnClickListener(View.OnClickListener {
            viewCloseBottomSheet()
            if (createInvoiceSetting == "true") {
                val intent =
                    Intent(this@NewInvoiceListActivity, NewInvoicePrintPreviewActivity::class.java)
                intent.putExtra("invoiceNumber", invoiceNumberValue)
                intent.putExtra("outstandingAmount", oustandingAmount)
                startActivity(intent)
            } else {
                val intent =
                    Intent(this@NewInvoiceListActivity, InvoicePrintPreviewActivity::class.java)
                intent.putExtra("invoiceNumber", invoiceNumberValue)
                intent.putExtra("outstandingAmount", oustandingAmount)
                startActivity(intent)
            }
        })
//        doPrintPreview!!.setOnClickListener(View.OnClickListener {
//            viewCloseBottomSheet()
//            val intent = Intent(this@NewInvoiceListActivity, DOPrintPreview::class.java)
//            intent.putExtra("invoiceNumber", invoiceNumberValue)
//            intent.putExtra("outstandingAmount", oustandingAmount)
//            startActivity(intent)
//        })
        printPreviewLayout!!.setOnClickListener(View.OnClickListener {
            viewCloseBottomSheet()
            if (createInvoiceSetting == "true") {
                val intent =
                    Intent(this@NewInvoiceListActivity, NewInvoicePrintPreviewActivity::class.java)
                intent.putExtra("invoiceNumber", invoiceNumberValue)
                intent.putExtra("outstandingAmount", oustandingAmount)
                startActivity(intent)
            } else {
                val intent =
                    Intent(this@NewInvoiceListActivity, InvoicePrintPreviewActivity::class.java)
                intent.putExtra("invoiceNumber", invoiceNumberValue)
                intent.putExtra("outstandingAmount", oustandingAmount)
                startActivity(intent)
            }
        })
        printLayout!!.setOnClickListener(View.OnClickListener {
            if (printInvoiceNumber != null && !printInvoiceNumber!!.isEmpty()) {
                try {
                    getInvoicePrintDetails(printInvoiceNumber, 1, "false")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
        invoicePrintLayout!!.setOnClickListener(View.OnClickListener {
            try {
                viewCloseBottomSheet()
                isInvoicePrint = true
                getInvoicePrintDetails(invoiceNumberValue, 1, "false")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        })
        invoicePrint!!.setOnClickListener(View.OnClickListener {
            try {
                viewCloseBottomSheet()
                isInvoicePrint = true
                getInvoicePrintDetails(invoiceNumberValue, 1, "false")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        })

        /*  doPrintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    viewCloseBottomSheet();
                    isInvoicePrint=false;
                    getInvoicePrintDetails(invoiceNumberValue,1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
*/
//        deliveryOrderPrint.setOnClickListener(View.OnClickListener {
//            try {
//                viewCloseBottomSheet()
//                isInvoicePrint = false
//                getInvoicePrintDetails(invoiceNumberValue, 1, "false")
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//        })
       // cancelInvoice.setOnClickListener(View.OnClickListener { showRemoveAlert(invoiceNumberValue) })
//        cancelInvoiceLayout.setOnClickListener(View.OnClickListener {
//            showRemoveAlert(
//                invoiceNumberValue
//            )
//        })
        loadFragment(AllInvoices())
    }

    @Throws(JSONException::class)
    private fun setAddressZone() {
        // Initialize a new RequestQueue instance
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "ZoneList"
        // Initialize a new JsonArrayRequest instance

        addressZoneList = arrayListOf()
        Log.w("Given_url_addr:", url)
        CommonMethods.showProgressDialog(this)

        val jsonArrayRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, null,
            Response.Listener { response: JSONObject ->
                try {
                    GlobalScope.launch {
                        withContext(Dispatchers.Main) {
                            Log.w("Res_is_addr:", response.toString())
                            if (response.length() > 0) {

                                //pDialog.dismiss();
                                val statusCode = response.optString("statusCode")
                                val statusMessage = response.optString("statusMessage")

                                if (statusCode == "1") {
                                    val resArray = response.optJSONArray("responseData")

                                    for (i in 0 until resArray.length()) {
                                        val jsonObject: JSONObject = resArray.getJSONObject(i)
                                        val obj = resArray.optJSONObject(i)

                                        val model =  AddressZoneModel(
                                            jsonObject.optString("costCenterCode"),
                                            jsonObject.optString("costCenterName")
                                        )
                                        addressZoneList!!.add(model)
                                    }
                                    withContext(Dispatchers.Main) {
                                        if (addressZoneList!!.size > 0) {
                                            Utils.setAddressZonelist(addressZoneList)
                                            setZoneAdapter(addressZoneList!!)
                                        }
                                    }

                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        statusMessage,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                    CommonMethods.cancelProgressDialog()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                // Do something when error occurred
                CommonMethods.cancelProgressDialog()
                Log.w("Error_throwing:", error.toString())
                Toast.makeText(
                    applicationContext,
                    "Server Error,Please check",
                    Toast.LENGTH_LONG
                ).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                val creds = java.lang.String.format(
                    "%s:%s",
                    Constants.API_SECRET_CODE,
                    Constants.API_SECRET_PASSWORD
                )
                val auth = "Basic " + Base64.encodeToString(creds.toByteArray(), Base64.DEFAULT)
                params["Authorization"] = auth
                return params
            }
        }
        jsonArrayRequest.retryPolicy = object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        }
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest)
    }
    fun setZoneAdapter(arrayList: java.util.ArrayList<AddressZoneModel>) {

        val nameList: java.util.ArrayList<MultipleSelectSpinnerPojo> = arrayList.map {
            MultipleSelectSpinnerPojo(  it.name, false)
        } as java.util.ArrayList<MultipleSelectSpinnerPojo>

        Log.w("cg_spinner:", zoneStrName.toString())
        nameList.forEach {
            if (zoneStrCode!!.isNotEmpty()  && it.text.contains(zoneStrName.toString())) {
                it.isSelected = true
                it.isEnabled = false
            }
        }

        zoneSpinner!!.buildCheckedSpinner(nameList) { selectedPositionList, displayString ->
//                tvSelectedPosition.text = "Selected position:  $selectedPositionList" //if kotlin, python selecteed:returned postion will be 0,2
//                tvDispString.text = "Display String:  $displayString"
            Log.w("zoneSpinnermulti:", displayString.trim())

            var selectedList =  TextUtils.split(displayString.trim(),",")

            var filterList =  arrayList.filter { selected ->
                selectedList.contains(selected.name)
            }

            Log.w("zoneSpinnermulti21:", filterList.toString())

            zoneMutipleCode = filterList.joinToString(",") {
                it.code
            }
            Log.w("zoneSpinnermulti2v:",  zoneMutipleCode.toString())
        }
    }

    @get:Throws(JSONException::class)
    private val allUsers: Unit
        private get() {
            val requestQueue = Volley.newRequestQueue(this)
            val url = Utils.getBaseUrl(this) + "UserList"
            // Initialize a new JsonArrayRequest instance
            Log.w("Given_url_UserList:", url)
            usersList = ArrayList()
            val jsonObject = JSONObject()
            jsonObject.put("User", username)
            pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog!!.setTitleText("Getting Users...")
            pDialog!!.setCancelable(false)
            pDialog!!.show()
            val jsonObjectRequest: JsonObjectRequest =
                object : JsonObjectRequest(Method.POST, url, jsonObject,
                    Response.Listener { response: JSONObject ->
                        try {
                            Log.w("UserListResponse:", response.toString())
                            val statusCode = response.optString("statusCode")
                            val message = response.optString("statusMessage")
                            if (statusCode == "1") {
                                val responseData = response.getJSONArray("responseData")
                                for (i in 0 until responseData.length()) {
                                    val `object` = responseData.optJSONObject(i)
                                    val model = UserListModel()
                                    model.userName = `object`.optString("userName")
                                    model.gender = `object`.optString("sex")
                                    model.jobTitle = `object`.optString("jobTitle")
                                    usersList!!.add(model)
                                }
                                if (usersList!!.size > 0) {
                                    setUserListAdapter(usersList)
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "No Users Found...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            pDialog!!.dismiss()
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
        }

    fun setUserListAdapter(usersList: ArrayList<UserListModel>?) {
        val customAdapter = UserAdapter(applicationContext, usersList)
        salesManSpinner!!.adapter = customAdapter
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        viewPager!!.currentItem = tab.position
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {}
    override fun onTabReselected(tab: TabLayout.Tab) {}
    override fun onPageChanged(page: Int, pageCount: Int) {
        pageNumber = page
        title = String.format("%s %s / %s", pdfFileName, page + 1, pageCount)
    }

    override fun loadComplete(nbPages: Int) {
        // PdfDocument.Meta meta = pdfView.getDocumentMeta();
        // printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    override fun onResume() {
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE)
        printerType = sharedPreferences!!.getString("printer_type", "")
        printerMacId = sharedPreferences!!.getString("mac_address", "")
        super.onResume()
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btn_all_invoice) {
            allInvoiceButton!!.setBackgroundResource(R.drawable.button_order)
            paidInvoiceButton!!.setBackgroundResource(R.drawable.button_unselect)
            outstandingInvoiceButton!!.setBackgroundResource(R.drawable.button_unselect)
            allInvoiceButton!!.setTextColor(Color.parseColor("#FFFFFF"))
            paidInvoiceButton!!.setTextColor(Color.parseColor("#212121"))
            outstandingInvoiceButton!!.setTextColor(Color.parseColor("#212121"))
            invoiceView!!.visibility = View.GONE
            paidView!!.visibility = View.VISIBLE
            invalidateOptionsMenu()
            if (visibleFragment != "invoices") loadFragment(AllInvoices())
            visibleFragment = "invoices"
        } else if (view.id == R.id.btn_paid_invoice) {
            allInvoiceButton!!.setBackgroundResource(R.drawable.button_unselect)
            paidInvoiceButton!!.setBackgroundResource(R.drawable.button_order)
            outstandingInvoiceButton!!.setBackgroundResource(R.drawable.button_unselect)
            allInvoiceButton!!.setTextColor(Color.parseColor("#212121"))
            paidInvoiceButton!!.setTextColor(Color.parseColor("#FFFFFF"))
            outstandingInvoiceButton!!.setTextColor(Color.parseColor("#212121"))
            invoiceView!!.visibility = View.GONE
            paidView!!.visibility = View.GONE
            invalidateOptionsMenu()
            if (visibleFragment != "paid") loadFragment(PaidInvoices())
            visibleFragment = "paid"
        } else if (view.id == R.id.btn_outstanding_invoice) {
            allInvoiceButton!!.setBackgroundResource(R.drawable.button_unselect)
            paidInvoiceButton!!.setBackgroundResource(R.drawable.button_unselect)
            outstandingInvoiceButton!!.setBackgroundResource(R.drawable.button_order)
            allInvoiceButton!!.setTextColor(Color.parseColor("#212121"))
            paidInvoiceButton!!.setTextColor(Color.parseColor("#212121"))
            outstandingInvoiceButton!!.setTextColor(Color.parseColor("#FFFFFF"))
            invoiceView!!.visibility = View.VISIBLE
            paidView!!.visibility = View.GONE
            invalidateOptionsMenu()
            if (visibleFragment != "unpaid") loadFragment(UnpaidInvoices())
            visibleFragment = "unpaid"
        }
    }

    fun showRemoveAlert(InvoiceId: String?) {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE) // .setTitleText("Are you sure?")
            .setContentText("Are you sure want Cancel this Invoice ?")
            .setConfirmText("YES")
            .setConfirmClickListener { sDialog ->
                try {
                    sDialog.dismiss()
                    viewCloseBottomSheet()
                    val date = Utils.changeDateFormat(invoiceDate)
                    setDeleteInvoice(InvoiceId, invoiceStatusValue, date)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            .showCancelButton(true)
            .setCancelText("No")
            .setCancelClickListener { sDialog -> sDialog.cancel() }.show()
    }

    @Throws(JSONException::class)
    private fun getInvoiceDetails(invoiceNumber: String?) {
        // Initialize a new RequestQueue instance
        val jsonObject = JSONObject()
        ///jsonObject.put("CompanyCode",companyId);
        jsonObject.put("InvoiceNo", invoiceNumber)
        jsonObject.put("LocationCode", locationCode1)
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "InvoiceDetails"
        // Initialize a new JsonArrayRequest instance
        Log.w("invoicDetail", "" + url + jsonObject)
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog!!.setTitleText("Getting Invoice Details...")
        pDialog!!.setCancelable(false)
        pDialog!!.show()
        val jsonObjectRequest: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, jsonObject,
                Response.Listener { response: JSONObject ->
                    try {
                        Log.w("Invoice_DetailsSAP:", response.toString())
                        if (response.length() > 0) {

                            // {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"WinApp","customerName":"WinApp",
                            // "invoiceNumber":"41","invoiceStatus":"O","invoiceDate":"6\/8\/2021 12:00:00 am","netTotal":"26.750000",
                            // "balanceAmount":"26.750000","totalDiscount":"0.000000","paidAmount":"0.000000","contactPersonCode":"",
                            // "createDate":"13\/8\/2021 12:00:00 am","updateDate":"13\/8\/2021 12:00:00 am","remark":"",
                            // "fDocTotal":"0.000000","fTaxAmount":"0.000000","receivedAmount":"0.000000","total":"26.750000",
                            // "fTotal":"0.000000","iTotalDiscount":"0.000000","taxTotal":"1.750000","iPaidAmount":"0.000000",
                            // "currencyCode":"SGD","currencyName":"Singapore Dollar","companyCode":"WINAPP_DEMO","docEntry":"28",
                            // "taxPer":null,"discountPercentage":null,"subTotal":"25.000000","taxType":"I","taxCode":"","taxPerc":"",
                            // "billDiscount":"0.000000",
                            //
                            // "invoiceDetails":[{"slNo":"1","companyCode":"WINAPP_DEMO","invoiceNo":"41",
                            // "productCode":"FG\/001245","productName":"Milk","quantity":"5.000000","price":"5.000000","currency":"SGD",
                            // "taxRate":"0.000000","discountPercentage":"0.000000","lineTotal":"26.750000","fRowTotal":"0.000000",
                            // "warehouseCode":"01","salesEmployeeCode":"-1","accountCode":"400000","taxStatus":"Y","unitPrice":"5.000000",
                            // "customerCategoryNo":"","barCodes":"","totalTax":"1.750000","fTaxAmount":"0.000000","taxCode":"SR",
                            // "taxType":"Y","taxPerc":"0.000000","uoMCode":null,"invoiceDate":"6\/8\/2021 12:00:00 am",
                            // "dueDate":"6\/8\/2021 12:00:00 am","createDate":"13\/8\/2021 12:00:00 am","updateDate":"13\/8\/2021 12:00:00 am",
                            // "createdUser":"manager","uomCode":"Ctn","uoMName":"Carton","cartonPrice":"3000.000000","piecePrice":"0.000000",
                            // "pcsPerCarton":"100.000000","lPrice":"100.000000","unitQty":"1.000000","retailPrice":"100.000000",
                            // "cartonQty":"1.000000","netTotal":"25.000000","subTotal":"25.00000000000","purchaseTaxPerc":"0.000000",
                            // "purchaseTaxRate":"7.000000","taxAmount":"1.750000","purchaseTaxCode":"SR","total":"26.750000",
                            // "itemDiscount":"0.000000"}]}]}
                            val statusCode = response.optString("statusCode")
                            if (statusCode == "1") {
                                val salesArray = response.optJSONArray("responseData")
                                val salesObject = salesArray.optJSONObject(0)
                                val invoice_number = salesObject.optString("invoiceNumber")
                                val invoice_code = salesObject.optString("code")
                                val company_code = salesObject.optString("CompanyCode")
                                val customer_code = salesObject.optString("customerCode")
                                val customer_name = salesObject.optString("customerName")
                                val total = salesObject.optString("total")
                                val sub_total = salesObject.optString("subTotal")
                                val bill_discount = salesObject.optString("billDiscount")
                                val item_discount = salesObject.optString("ItemDiscount")
                                Utils.setInvoiceOutstandingAmount(salesObject.optString("balanceAmount"))
                                val tax = salesObject.optString("taxTotal")
                                val net_total = salesObject.optString("netTotal")
                                val currency_rate = salesObject.optString("CurrencyRate")
                                val currency_name = salesObject.optString("currencyName")
                                val tax_type = salesObject.optString("taxType")
                                val tax_perc = salesObject.optString("taxPerc")
                                val tax_code = salesObject.optString("taxCode")
                                val phone_no = salesObject.optString("DelPhoneNo")
                                val so_date = salesObject.optString("soDate")
                                dbHelper!!.removeCustomer()
                                dbHelper!!.insertCustomer(
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
                                    salesObject.optString("currencyCode")
                                )
                                dbHelper!!.removeAllItems()
                                dbHelper!!.removeCustomerTaxes()
                                val model = CustomerDetails()
                                model.customerCode = customer_code
                                model.customerName = customer_name
                                model.customerAddress1 = salesObject.optString("address1")
                                model.taxPerc = salesObject.optString("taxPercentage")
                                model.taxType = salesObject.optString("taxType")
                                model.taxCode = salesObject.optString("taxCode")
                                val taxList = ArrayList<CustomerDetails>()
                                taxList.add(model)
                                dbHelper!!.insertCustomerTaxValues(taxList)
                                val products = salesObject.getJSONArray("invoiceDetails")
                                for (i in 0 until products.length()) {
                                    val `object` = products.getJSONObject(i)

                                    //  "salesOrderDetails":[{"slNo":"1","companyCode":"WINAPP_DEMO",
                                    //  "soNo":"8","productCode":"FG\/001245","productName":"Milk","quantity":"5.000000","cartonQty":"1.000000",
                                    //  "price":"99.000000","currency":"SGD","taxRate":"0.000000","discountPercentage":"1.000000","lineTotal":"529.650000",
                                    //  "fRowTotal":"0.000000","warehouseCode":"01","salesEmployeeCode":"-1","accountCode":"400000","taxStatus":"Y",
                                    //  "unitPrice":"100.000000","customerCategoryNo":"","barCodes":"","totalTax":"34.650000","fTaxAmount":"0.000000",
                                    //  "taxCode":"SR","taxType":"E","taxPerc":"0.000000","uoMCode":null,"soDate":"12\/8\/2021 12:00:00 am",
                                    //  "dueDate":"12\/8\/2021 12:00:00 am","createDate":"12\/8\/2021 12:00:00 am","updateDate":"12\/8\/2021 12:00:00 am",
                                    //  "createdUser":"manager","uomCode":"Ctn","uoMName":"Carton","cartonPrice":"3000.000000","piecePrice":"0.000000",
                                    //  "pcsPerCarton":"100.000000","lPrice":"100.000000","unitQty":"1.000000","retailPrice":"100.000000",
                                    //  "netTotal":"495.000000","subTotal":"500.00000000000","purchaseTaxPerc":"1.000000","purchaseTaxRate":"7.000000",
                                    //  "taxAmount":"34.650000","purchaseTaxCode":"SR","total":"529.650000","itemDiscount":"5.000000"}]}]}
                                    var lqty: String? = "0.0"
                                    var cqty: String? = "0.0"
                                    if (`object`.optString("unitQty") != "null") {
                                        lqty = `object`.optString("unitQty")
                                    }
                                    if (`object`.optString("quantity") != "null") {
                                        cqty = `object`.optString("quantity")
                                    }
                                    //                                    if (object.optString("bP_CatalogNo") != null) {
//                                        invoiceListModel.setCustomerItemCode(object.optString("bP_CatalogNo"));
//                                    }
                                    val actualPrice = `object`.optString("unitPrice").toDouble()
                                    if (createInvoiceSetting == "true") {

                                        /*  double priceValue=0.0;
                                        double net_qty=Double.parseDouble(qty_value) - Double.parseDouble(return_qty);

                                        double return_amt=(Double.parseDouble(return_qty)*Double.parseDouble(price_value));
                                        double total=(net_qty * Double.parseDouble(price_value));
                                        double sub_total=total-return_amt-Double.parseDouble(discount);


                                        boolean insertStatus=dbHelper.insertCreateInvoiceCart(
                                                productId.toString().trim(),
                                                productName,
                                                qty_value,
                                                return_qty,
                                                String.valueOf(net_qty),
                                                foc,
                                                price_value,
                                                String.valueOf(total),
                                                subTotalValue.getText().toString(),
                                                taxValueText.getText().toString(),
                                                netTotalValue.getText().toString()
                                        );*/
                                    } else {
                                        dbHelper!!.insertCart(
                                            `object`.optString("productCode"),
                                            `object`.optString("productName"),
                                            cqty,
                                            "0",
                                            `object`.optString("subTotal"),
                                            "",
                                            `object`.optString("lineTotal"),
                                            "weight", actualPrice.toString(),
                                            `object`.optString("subTotal"),
                                            `object`.optString("pcsPerCarton"),
                                            `object`.optString("totalTax"),
                                            `object`.optString("subTotal"),
                                            `object`.optString("taxType"),
                                            `object`.optString("FOCQty"),
                                            "",
                                            `object`.optString("ExchangeQty"),
                                            "",
                                            "0.00",
                                            `object`.optString("ReturnQty"),
                                            "", "", `object`.optString("total"),
                                            `object`.optString("stockInHand"),
                                            `object`.optString("uomCode"),
                                            `object`.optString("minimumSellingPrice"),
                                            `object`.optString("stockInHand")
                                        )
                                    }
                                    val count = dbHelper!!.numberOfRows()
                                    if (products.length() == count) {
                                        Utils.setCustomerSession(
                                            this@NewInvoiceListActivity,
                                            customer_code
                                        )
                                        if (createInvoiceSetting == "true") {
                                            val intent = Intent(
                                                applicationContext,
                                                CreateNewInvoiceActivity::class.java
                                            )
                                            intent.putExtra("from", "invoice")
                                            intent.putExtra("customerName", customer_name)
                                            intent.putExtra("customerCode", customer_code)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            val intent = Intent(
                                                this@NewInvoiceListActivity,
                                                AddInvoiceActivityOld::class.java
                                            )
                                            intent.putExtra("billDiscount", bill_discount)
                                            intent.putExtra("itemDiscount", item_discount)
                                            intent.putExtra("subTotal", sub_total)
                                            intent.putExtra("customerId", customer_code)
                                            intent.putExtra("invoiceNumber", invoice_code)
                                            intent.putExtra("activityFrom", "InvoiceEdit")
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                                }
                                val count = dbHelper!!.numberOfRows()
                                if (count == 0) {
                                    Toast.makeText(
                                        applicationContext,
                                        "No Products Details Found,Try again!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                            }
                        }

                        /*   Log.w("Invoice_Details:",response.toString());
                        if (response.length()>0){
                            String invoice_no=response.optString("InvoiceNo");
                            String company_code=response.optString("CompanyCode");
                            String customer_code=response.optString("CustomerCode");
                            String customer_name=response.optString("CustomerName");
                            String total=response.optString("Total");
                            String sub_total=response.optString("SubTotal");
                            String tax=response.optString("Tax");
                            String net_total=response.optString("NetTotal");
                            String bill_discount=response.optString("BillDIscount");
                            String item_discount=response.optString("ItemDiscount");
                            String currency_rate=response.optString("CurrencyRate");
                            String currency_name=response.optString("CurrencyName");
                            String tax_type=response.optString("TaxType");
                            String tax_perc=response.optString("TaxPerc");
                            String tax_code=response.optString("TaxCode");
                            String phone_no=response.optString("DelPhoneNo");
                            String deliveryCode=response.optString("DeliveryCode");
                            SettingUtils.setDeliveryAddressCode(deliveryCode);
                            dbHelper.removeCustomer();
                            dbHelper.insertCustomer(
                                    customer_code,
                                    customer_name,
                                    phone_no,
                                    response.optString("Address1"),
                                    response.optString("Address2"),
                                    response.optString("Address3"),
                                    response.optString("IsActive"),
                                    response.optString("HaveTax"),
                                    response.optString("TaxType"),
                                    response.optString("TaxPerc"),
                                    response.optString("TaxCode"),
                                    response.optString("CreditLimit"),
                                    "Singapore",
                                    response.optString("CurrencyCode"));
                            customerDetails=dbHelper.getCustomer();

                            dbHelper.removeAllItems();
                            JSONArray products=response.getJSONArray("InvoiceDetails");
                            for (int i=0;i<products.length();i++){
                                JSONObject object=products.getJSONObject(i);

                                String retail_price="0.0";
                                String unit_price="0.0";
                                if (!object.optString("RetailPrice").equals("null")){
                                    retail_price=object.optString("RetailPrice");
                                }

                                if (!object.optString("UnitCost").equals("null")){
                                    unit_price=object.optString("UnitCost");
                                }
                                dbHelper.insertCart(
                                        object.optString("ProductCode"),
                                        object.optString("ProductName"),
                                        object.optString("CQty"),
                                        object.optString("LQty"),
                                        object.optString("SubTotal"),
                                        "",
                                        object.optString("NetTotal"),
                                        "weight",
                                        object.optString("CartonPrice"),
                                        object.optString("Price"),
                                        object.optString("PcsPerCarton"),
                                        object.optString("Tax"),
                                        object.optString("SubTotal"),
                                        object.optString("TaxType"),
                                        object.optString("FOCQty"),
                                        "",
                                        object.optString("ExchangeQty"),
                                        "",
                                        object.optString("ItemDiscount"),
                                        object.optString("ReturnQty"),
                                        "","",object.optString("Total"),"",object.optString("UOMCode"));

                                int count=dbHelper.numberOfRows();
                                if (products.length()==count){
                                    Utils.setCustomerSession(NewInvoiceListActivity.this,customer_code);
                                    Intent intent=new Intent(NewInvoiceListActivity.this,AddInvoiceActivity.class);
                                    intent.putExtra("billDiscount",bill_discount);
                                    intent.putExtra("itemDiscount",item_discount);
                                    intent.putExtra("subTotal",sub_total);
                                    intent.putExtra("customerId",customer_code);
                                    intent.putExtra("invoiceNumber",invoice_no);
                                    intent.putExtra("activityFrom","InvoiceEdit");
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            int count=dbHelper.numberOfRows();
                            if (count==0){
                                Toast.makeText(getApplicationContext(),"No Products Details Found,Try again!",Toast.LENGTH_SHORT).show();
                            }
                        }*/pDialog!!.dismiss()
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

    @Throws(JSONException::class)
    private fun getInvoiceEditDetails(invoiceNo: String?) {
        // Initialize a new RequestQueue instance
        val jsonObject = JSONObject()
        jsonObject.put("InvoiceNo", invoiceNo)
        val requestQueue = Volley.newRequestQueue(this)
        //    EditSODetails
        //    EditSODetailsWithFOC
        val url = Utils.getBaseUrl(this) + "DuplicateInvoiceDetails"
        Log.w("JsonValue:", jsonObject.toString())
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url_duplicat:", url)
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog!!.setTitleText("Getting Invoice Details...")
        pDialog!!.setCancelable(false)
        pDialog!!.show()
        val jsonObjectRequest: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, jsonObject,
                Response.Listener { response: JSONObject ->
                    try {
                        Log.w("inv_duplicat_edit:", response.toString())
                        if (response.length() > 0) {
                            val statusCode = response.optString("statusCode")
                            if (statusCode == "1") {
                                val salesArray = response.optJSONArray("responseData")
                                if (salesArray.length() > 0) {
                                    val salesObject = salesArray.optJSONObject(0)
                                    val invoice_no = salesObject.optString("invoiceNumber")
                                    val company_code = salesObject.optString("CompanyCode")
                                    val customer_code = salesObject.optString("customerCode")
                                    val customer_name = salesObject.optString("customerName")
                                    val total = salesObject.optString("total")
                                    val inv_date = salesObject.optString("invoiceDate")
                                    val sub_total = salesObject.optString("subTotal")
                                    val bill_discount = salesObject.optString("billDiscount")
                                    val item_discount = salesObject.optString("ItemDiscount")
                                    val tax = salesObject.optString("taxTotal")
                                    val customerBill_Disc =
                                        salesObject.optString("customerDiscountPercentage")
                                    // String customerBill_Disc=salesObject.optString("billDiscountPercentage");
                                    val net_total = salesObject.optString("netTotal")
                                    val currency_rate = salesObject.optString("CurrencyRate")
                                    val currency_name = salesObject.optString("currencyName")
                                    val tax_type = salesObject.optString("taxType")
                                    val tax_perc = salesObject.optString("taxPerc")
                                    val tax_code = salesObject.optString("taxCode")
                                    val phone_no = salesObject.optString("DelPhoneNo")
                                    val so_date = salesObject.optString("soDate")
                                    val signFlag = salesObject.optString("signFlag")
                                    Utils.setInvoiceMode("Invoice")
                                    if (signFlag == "Y") {
                                        val signature = salesObject.optString("signature")
                                        Utils.setSignature(signature)
                                    }
                                    dbHelper!!.removeCustomer()
                                    dbHelper!!.insertCustomer(
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
                                        "",
                                        "Singapore",
                                        salesObject.optString("currencyCode")
                                    )
                                    customerDetails = dbHelper!!.customer
                                    dbHelper!!.removeAllItems()
                                    dbHelper!!.removeAllInvoiceItems()
                                    dbHelper!!.removeCustomerTaxes()
                                    val model = CustomerDetails()
                                    model.customerCode = customer_code
                                    model.customerName = customer_name
                                    model.customerAddress1 = salesObject.optString("address1")
                                    model.taxPerc = salesObject.optString("taxPerc")
                                    model.taxType = salesObject.optString("taxType")
                                    model.taxCode = salesObject.optString("taxCode")
                                    val taxList = ArrayList<CustomerDetails>()
                                    taxList.add(model)
                                    Log.w("TaxModelPrint::", model.toString())
                                    dbHelper!!.insertCustomerTaxValues(taxList)
                                    val products = salesObject.getJSONArray("invoiceDetails")
                                    for (i in 0 until products.length()) {
                                        val `object` = products.getJSONObject(i)
                                        var lqty: String? = "0.0"
                                        var cqty = "0.0"
                                        if (`object`.optString("unitQty") != "null") {
                                            lqty = `object`.optString("unitQty")
                                        }
                                        if (`object`.optString("quantity") != "null") {
                                            cqty = `object`.optString("quantity")
                                        }
                                        val priceValue = 0.0
                                        val return_qty = "0"
                                        val net_qty = cqty.toDouble() - return_qty.toDouble()
                                        val price_value = `object`.optString("price")
                                        //String price_value=object.optString("grossPrice");
                                        val return_amt =
                                            return_qty.toDouble() * price_value.toDouble()
                                        val total1 = net_qty * price_value.toDouble()
                                        val sub_total1 = total1 - return_amt
                                        val laterDate = System.currentTimeMillis()
                                        val millisec = 18000
                                        val original = Timestamp(laterDate)
                                        val cal = Calendar.getInstance()
                                        cal.timeInMillis = original.time
                                        cal.add(Calendar.MILLISECOND, millisec)
                                        val timeStamp = Timestamp(cal.time.time)
                                        dbHelper!!.insertCreateInvoiceCartEdit(
                                            `object`.optString("productCode"),
                                            `object`.optString("productName"),
                                            `object`.optString("uomCode"),
                                            cqty,
                                            return_qty, net_qty.toString(),
                                            `object`.optString("foc_Qty"),
                                            price_value,
                                            `object`.optString("stockInHand"),
                                            `object`.optString("total"),
                                            `object`.optString("subTotal"),
                                            `object`.optString("priceWithGST"),
                                            `object`.optString("netTotal"),
                                            `object`.optString("itemDiscount"),
                                            salesObject.optString("billDiscount"),
                                            "",
                                            "",
                                            `object`.optString("exc_Qty"),
                                            salesObject.optString("minimumSellingPrice"),
                                            `object`.optString("stockInHand"), timeStamp.toString(),
                                            `object`.optString("itemAllowFOC")
                                        )
                                        myEdit!!.putString(
                                            "billDisc_amt",
                                            salesObject.optString("billDiscount")
                                        )
                                        myEdit!!.putString(
                                            "billDisc_percent",
                                            salesObject.optString("discountPercentage")
                                        )
                                        myEdit!!.apply()
                                        Log.w("ProductsLength:", products.length().toString() + "")
                                        Log.w(
                                            "ActualPrintProducts:",
                                            dbHelper!!.numberOfRowsInInvoice().toString() + ""
                                        )
                                        if (products.length() == dbHelper!!.numberOfRowsInInvoice()) {
                                            redirectActivity(
                                                customer_code,
                                                customer_name,
                                                invoiceNo,
                                                customerBill_Disc,
                                                inv_date
                                            )
                                            break
                                        }
                                    }
                                } else {
                                    Toast.makeText(this, "no data found", Toast.LENGTH_LONG).show()
                                    pDialog!!.dismiss()
                                }
                            } else {
                                pDialog!!.dismiss()
                            }
                            pDialog!!.dismiss()
                        }
                        pDialog!!.dismiss()
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

    fun redirectActivity(
        customer_code: String?, customer_name: String?, inv_code: String?,
        customerBill_Disc: String?, inv_date: String?
    ) {
        //  if (products.length()==dbHelper.numberOfRowsInInvoice()){
        Utils.setCustomerSession(this@NewInvoiceListActivity, customer_code)
        // setCustomerDetails(customer_code);
        val intent = Intent(applicationContext, CreateNewInvoiceActivity::class.java)
        intent.putExtra("customerCode", customer_code)
        intent.putExtra("duplicateInvNo", inv_code)
        intent.putExtra("duplicateInvDate", inv_date)
        intent.putExtra("customerName", customer_name)
        // intent.putExtra("customerBillDisc", customerBill_Disc);
        intent.putExtra("from", "Duplicate")
        startActivity(intent)
        finish()
    }

    @Throws(JSONException::class)
    private fun setDeleteInvoice(invoiceNumber: String?, status: String?, invoiceDate: String) {
        // Initialize a new RequestQueue instance
        val jsonObject = JSONObject()
        jsonObject.put("InvoiceStatus", status)
        jsonObject.put("InvoiceNo", invoiceNumber)
        jsonObject.put("InvoiceDate", invoiceDate)
        Log.w("GivenInputRequest::", jsonObject.toString())
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "CancellationDocument"
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url)
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog!!.setTitleText("Cancelling Invoice...")
        pDialog!!.setCancelable(false)
        pDialog!!.show()
        val jsonObjectRequest: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, jsonObject,
                Response.Listener { response: JSONObject ->
                    try {
                        Log.w("DelInvoice_Response_is:", response.toString())
                        //  {"statusCode":1,"statusMessage":"Invoice Cancelled Successfully","responseData":{"docNum":"8010025","error":null}}
                        // {"IsSaved":false,"Result":"pass","IsDeleted":true}
                        if (response.length() > 0) {
                            val statusCode = response.optString("statusCode")
                            val statusMessage = response.optString("statusMessage")
                            if (statusCode == "1" && statusMessage == "Invoice Cancelled Successfully") {
                                Toast.makeText(
                                    applicationContext,
                                    "Invoice Cancelled Successfully...!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                                startActivity(intent)
                                dbHelper!!.removeAllProducts()
                            } else {
                                val `object` = response.getJSONObject("responseData")
                                val error = `object`.optString("error")
                                Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
                            }
                        }
                        pDialog!!.dismiss()
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

    fun setNettotal(invoiceList: ArrayList<InvoiceModel>) {
        var net_amount = 0.0
        for (model in invoiceList) {
            if (model.balance != null && model.balance != "null") {
                net_amount = net_amount + model.balance.toDouble()
            }
        }
        netTotalText!!.text = "$ " + Utils.twoDecimalPoint(net_amount)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.sorting_menu, menu)
        this.menu = menu
        val item = menu.findItem(R.id.action_barcode)
        item.setVisible(false)
        val addInvoice = menu.findItem(R.id.action_add)
        addInvoice.setVisible(false)

//        if (createInvoiceSetting.equals("true")) {
//            if (company_name.equals("AADHI INTERNATIONAL PTE LTD")) {
//                addInvoice.setVisible(false);
//            } else {
//                addInvoice.setVisible(true);
//            }
//        } else {
//            addInvoice.setVisible(false);
//        }
        /*  ArrayList<UserRoll> userRolls=helper.getUserPermissions();
        if (userRolls.size()>0) {
            for (UserRoll roll : userRolls) {
                if (roll.getFormName().equals("Add Invoice")){
                    if (roll.getHavePermission().equals("true")){
                       // addInvoice.setVisible(true);
                        if (createInvoiceSetting=="true"){
                            addInvoice.setVisible(true);
                        }else {
                            addInvoice.setVisible(false);
                        }
                    }else {
                        addInvoice.setVisible(true);
                    }
                }
            }
        }*/this.menu = menu
        val filter = menu.findItem(R.id.action_filter)
        if (visibleFragment == "invoices") {
            filter.setVisible(true)
            //filter.setVisible(false);
        } else {
            filter.setVisible(false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { //finish();
            onBackPressed()

            /*   case R.id.action_remove:
                showRemoveAlert();
                break;*/
        } else if (item.itemId == R.id.action_customer_name) {
            Collections.sort(invoiceList) { obj1, obj2 ->
                // ## Ascending order
                obj1.name.compareTo(obj2.name, ignoreCase = true) // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
            }
            invoiceAdapter!!.notifyDataSetChanged()
        } else if (item.itemId == R.id.action_amount) {
            Collections.sort(invoiceList) { obj1, obj2 ->
                // ## Ascending order
                //  return obj1.getNetTotal().compareToIgnoreCase(obj2.getNetTotal()); // To compare string values
                java.lang.Double.valueOf(obj1.netTotal)
                    .compareTo(java.lang.Double.valueOf(obj2.netTotal)) // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
            }
            invoiceAdapter!!.notifyDataSetChanged()
        } else if (item.itemId == R.id.action_date) {
            try {
                Collections.sort(invoiceList) { obj1, obj2 ->
                    val sdfo = SimpleDateFormat("yyyy-MM-dd")
                    // Get the two dates to be compared
                    var d1: Date? = null
                    var d2: Date? = null
                    try {
                        d1 = sdfo.parse(obj1.date)
                        d2 = sdfo.parse(obj2.date)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    // ## Ascending order
                    //  return obj1.getNetTotal().compareToIgnoreCase(obj2.getNetTotal()); // To compare string values
                    d1!!.compareTo(d2) // To compare integer values

                    // ## Descending order
                    // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                    // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                }
                invoiceAdapter!!.notifyDataSetChanged()
            } catch (ex: Exception) {
                Log.w("Error:", ex.message!!)
            }
        } else if (item.itemId == R.id.action_add) {
            val intent = Intent(applicationContext, CustomerListActivity::class.java)
            intent.putExtra("from", "iv")
            startActivityForResult(intent, customerSelectCode)
            /* SharedPreferences sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
            String selectCustomerId = sharedPreferences.getString("customerId", "");
            if (selectCustomerId!=null && !selectCustomerId.isEmpty()){
                customerDetails=dbHelper.getCustomer(selectCustomerId);
                if (customerDetails.size()>0){
                    showCustomerDialog(this,customerDetails.get(0).getCustomerName(),customerDetails.get(0).getCustomerCode(),customerDetails.get(0).getCustomerAddress1());
                }else {
                    customerLayout.setVisibility(View.VISIBLE);
                    invoiceOptionLayout.setVisibility(View.GONE);
                    customerNameText.setText("");
                    searchFilterView.setVisibility(View.GONE);
                    isSearchCustomerNameClicked=false;
                    //  viewCloseBottomSheet();
                    if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
            }else {
                    customerLayout.setVisibility(View.VISIBLE);
                    invoiceOptionLayout.setVisibility(View.GONE);
                    customerNameText.setText("");
                    searchFilterView.setVisibility(View.GONE);
                    isSearchCustomerNameClicked=false;
                    //  viewCloseBottomSheet();
                    if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
            }*/
        } else if (item.itemId == R.id.action_filter) {
            if (searchFilterView!!.visibility == View.VISIBLE) {
                searchFilterView!!.visibility = View.GONE
                customerNameText!!.setText("")
                isSearchCustomerNameClicked = false
                if (behavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                //slideUp(searchFilterView);
            } else {
                customerNameText!!.setText("")
                isSearchCustomerNameClicked = false
                searchFilterView!!.visibility = View.VISIBLE
                if (behavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                // slideDown(searchFilterView);
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == customerSelectCode) {
            if (resultCode == RESULT_OK) {
                val result = data!!.getStringExtra("customerCode")
                Utils.setCustomerSession(this, result)
                val intent = Intent(this@NewInvoiceListActivity, AddInvoiceActivityOld::class.java)
                intent.putExtra("customerId", result)
                intent.putExtra("activityFrom", "Invoice")
                startActivity(intent)
                //finish();
            }
            if (resultCode == RESULT_CANCELED) {
                // Write your code if there's no result
            }
        } else if (requestCode == FILTER_CUSTOMER_CODE && resultCode == RESULT_OK) {
            selectCustomerCode = data!!.getStringExtra("customerCode")
            selectCustomerName = data.getStringExtra("customerName")
            customerNameText!!.setText(selectCustomerName)
            Log.w("CustomerCode:", selectCustomerCode!!)
        }
    } //onActivityResult

    private fun showCustomerDialog(
        activity: Activity,
        customer_name: String,
        customer_code: String,
        desc: String
    ) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.cutom_alert_dialog, viewGroup, false)

        //Now we need an AlertDialog.Builder object
        val builder = AlertDialog.Builder(this)

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)
        val customerName = dialogView.findViewById<TextView>(R.id.customer_name_value)
        val description = dialogView.findViewById<TextView>(R.id.description)
        customerName.text = "$customer_name - $customer_code"
        description.text = "Do you want to continue this customer ?"
        val yesButton = dialogView.findViewById<Button>(R.id.buttonYes)
        val noButton = dialogView.findViewById<Button>(R.id.buttonNo)

        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        yesButton.setOnClickListener {
            alertDialog.dismiss()
            dbHelper!!.removeCustomer()
            dbHelper!!.removeAllItems()
            setCustomerDetails(customer_code)
            AddInvoiceActivityOld.customerId = customer_code
            val intent = Intent(this@NewInvoiceListActivity, AddInvoiceActivityOld::class.java)
            intent.putExtra("customerId", customer_code)
            intent.putExtra("activityFrom", "Invoice")
            startActivity(intent)
            finish()
        }
        noButton.setOnClickListener {
            alertDialog.dismiss()
            isSearchCustomerNameClicked = false
            customerLayout!!.visibility = View.VISIBLE
            invoiceOptionLayout!!.visibility = View.GONE
            //   viewCloseBottomSheet();
            if (behavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                behavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }
    }

    fun setCustomerDetails(customerId: String?) {
        val sharedPreferences = getSharedPreferences("customerPref", MODE_PRIVATE)
        val customerPredEdit = sharedPreferences.edit()
        customerPredEdit.putString("customerId", customerId)
        customerPredEdit.apply()
    }

    override fun onBackPressed() {
        //Execute your code here
        //Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        // startActivity(intent);
        if (behavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            behavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
        } else {
            finish()
        }
    }

    val customers: Unit
        get() {
            //Initialize a new RequestQueue instance
            val requestQueue = Volley.newRequestQueue(this)
            val url = Utils.getBaseUrl(this) + "CustomerList"
            customerList = ArrayList()
            emptyTextView!!.text = "Customers List loading please wait..."
            Log.w("Given_url:", url)
            val jsonArrayRequest: JsonObjectRequest = object : JsonObjectRequest(Method.GET,
                url, null,
                Response.Listener { response: JSONObject ->
                    try {
                        // pDialog.dismiss();
                        // Loop through the array elements
                        Log.w("Customer_Response:", response.toString())
                        val statusCode = response.optString("statusCode")
                        if (statusCode == "1") {
                            val customerDetailArray = response.optJSONArray("responseData")
                            for (i in 0 until customerDetailArray.length()) {
                                val `object` = customerDetailArray.optJSONObject(i)
                                //  if (customerObject.optBoolean("IsActive")) {
                                val model = CustomerModel()
                                model.customerCode = `object`.optString("customerCode")
                                model.customerName = `object`.optString("customerName")
                                model.address1 = `object`.optString("address")
                                model.address2 = `object`.optString("street")
                                model.address3 = `object`.optString("city")
                                model.customerAddress = `object`.optString("address")
                                model.haveTax = `object`.optString("HaveTax")
                                model.taxType = `object`.optString("taxType")
                                model.taxPerc = `object`.optString("taxPercentage")
                                model.taxCode = `object`.optString("taxCode")
                                model.billDiscPercentage = `object`.optString("discountPercentage")

                                //  model.setCustomerBarcode(object.optString("BarCode"));
                                // model.setCustomerBarcode(String.valueOf(i));
                                if (`object`.optString("outstandingAmount") == "null" || `object`.optString(
                                        "outstandingAmount"
                                    ).isEmpty()
                                ) {
                                    model.outstandingAmount = "0.00"
                                } else {
                                    model.outstandingAmount =
                                        `object`.optString("outstandingAmount")
                                }
                                customerList!!.add(model)
                                // }
                            }
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error,in getting Customer list",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        if (customerList!!.size > 0) {
                            emptyTextView!!.visibility = View.GONE
                            customerView!!.visibility = View.VISIBLE
                            //new InsertCustomerTask().execute();
                            setAdapter(customerList)
                        } else {
                            customerView!!.visibility = View.GONE
                            emptyTextView!!.text = "No Customer found.."
                            emptyTextView!!.visibility = View.VISIBLE
                            progressLayout!!.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error: VolleyError ->

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
            jsonArrayRequest.setRetryPolicy(object : RetryPolicy {
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
            requestQueue.add(jsonArrayRequest)
        }

//    internal inner class InsertCustomerTask : AsyncTask<Void?, Int?, String>() {
//        var TAG = javaClass.simpleName
//        override fun onPreExecute() {
//            super.onPreExecute()
//        }
//
//        protected override fun doInBackground(vararg arg0: Void): String {
//            Log.d("$TAG DoINBackGround", "On doInBackground...")
//            dbHelper!!.removeAllCustomers()
//            dbHelper!!.insertCustomerList(customerList)
//            return "You are at PostExecute"
//        }
//
//        override fun onPostExecute(result: String) {
//            super.onPostExecute(result)
//        }
//    }

    private fun setAdapter(customerNames: ArrayList<CustomerModel>?) {
        progressLayout!!.visibility = View.GONE
        customerView!!.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        customerNameAdapter =
            SelectCustomerAdapter(this, customerNames) { customer, customername, pos ->
                customerNameEdittext!!.setText("")
                setAdapter(customerList)
                // customerNameAdapter.notifyDataSetChanged();
                if (isSearchCustomerNameClicked) {
                    // viewCloseBottomSheet();
                    //  searchFilterView.setVisibility(View.GONE);
                    if (behavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        behavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                    } else {
                        behavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
                    }
                    setCustomerDetails(customer)
                    customerNameText!!.setText(customername)
                    selectCustomerId = customer
                    selectCustomerName = customername
                    //  redirectInvoice=true;
                } else {
                    val count = dbHelper!!.numberOfRows()
                    if (count > 0) {
                        showProductDeleteAlert(customer)
                    } else {
                        if (behavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                            behavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                        } else {
                            behavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
                        }
                        dbHelper!!.removeAllItems()
                        setCustomerDetails(customer)
                        selectCustomerId = customer
                        redirectInvoice = true
                        /* Intent intent=new Intent(NewInvoiceListActivity.this,AddInvoiceActivity.class);
                            intent.putExtra("customerId",customer);
                            intent.putExtra("activityFrom","Invoice");
                            startActivity(intent);
                            finish();*/
                    }
                }
            }
        customerView!!.adapter = customerNameAdapter
        totalCustomers!!.text = customerNames!!.size.toString() + " Customers"
    }

    fun showProductDeleteAlert(customerId: String) {
        val builder1 = AlertDialog.Builder(this)
        builder1.setTitle("Warning !")
        builder1.setMessage("Products in Cart will be removed..")
        builder1.setCancelable(false)
        builder1.setPositiveButton(
            "OK"
        ) { dialog, id ->
            if (behavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                behavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
            dialog.cancel()
            setCustomerDetails(customerId)
            dbHelper!!.removeAllItems()
            selectCustomerId = customerId
            redirectInvoice = true
            //Intent intent=new Intent(NewInvoiceListActivity.this,AddInvoiceActivity.class);
            // intent.putExtra("customerId",customerId);
            // intent.putExtra("activityFrom","Invoice");
            //startActivity(intent);
            // finish();
        }
        builder1.setNegativeButton(
            "CANCEL"
        ) { dialog, id -> dialog.cancel() }
        val alert11 = builder1.create()
        alert11.show()
    }

    fun getInvoicePdf(invoiceno: String?, mode: String) {
        try {
            // Initialize a new RequestQueue instance
            val requestQueue = Volley.newRequestQueue(this)
            // Initialize a new JsonArrayRequest instance
            val jsonObject = JSONObject()
            jsonObject.put("InvoiceNo", invoiceno)
            val url = Utils.getBaseUrl(this) + "DownloadPDFInvoice"
            Log.w("Given_url_PdfDownload:", "$url/$jsonObject")
            pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog!!.setTitleText("Generating Pdf...")
            pDialog!!.setCancelable(false)
            pDialog!!.show()
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST,
                url,
                jsonObject,
                Response.Listener { response: JSONObject ->
                    try {
                        pDialog!!.dismiss()
                        Log.w("InvoicePdfResponse:", response.toString())
                        //    {"statusCode":1,"statusMessage":"Success",
                        //    "responseData":{"pdfURL":"http:\/\/172.16.5.60:8349\/PDF\/InvoiceNo_15031.pdf"}}
                        if (response.length() > 0) {
                            val statusCode = response.optString("statusCode")
                            if (statusCode == "1") {
                                val `object` = response.optJSONObject("responseData")
                                val pdfUrl = `object`.optString("pdfURL")
                                shareMode = mode
                                InvoicePdfDownload(this).execute(pdfUrl, "invoice", invoiceno)
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Error in Getting report..",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    Log.w(
                        "Error_throwing:",
                        error.toString()
                    )
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
        }
    }

    fun viewPdfLayout(invoiceNumber: String, invoiceDetails: ArrayList<InvoicePrintPreviewModel>) {
        //pdfGenerateDialog.show();
        if (VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                storagePath = Environment.getExternalStorageDirectory()
                invoiceNO = invoiceNumber
                FILE =
                    Environment.getExternalStorageDirectory().toString() + "/" + invoiceNO + ".pdf"
                fatchinvoiceList(invoiceDetails)
                //createInvoicePdfTable(invoiceDetails);
            } else {
                requestPermission() // Code for permission
            }
        } else {
            storagePath = Environment.getExternalStorageDirectory()
            invoiceNO = invoiceNumber
            FILE = Environment.getExternalStorageDirectory().toString() + "/" + invoiceNO + ".pdf"
            fatchinvoiceList(invoiceDetails)
            //createInvoicePdfTable(invoiceDetails);
        }
    }

//    private inner class DownloadFile : AsyncTask<String?, Void?, Void?>() {
//        protected override fun doInBackground(vararg strings: String): Void? {
//            val fileUrl = strings[0] // -> http://maven.apache.org/maven-1.x/maven.pdf
//            val fileName = strings[1] // -> maven.pdf
//            val extStorageDirectory = Constants.getPdfFolderPath(this@NewInvoiceListActivity)
//            val folder = File(extStorageDirectory)
//            if (!folder.exists()) {
//                val productsDirectory =
//                    File(Constants.getPdfFolderPath(this@NewInvoiceListActivity))
//                productsDirectory.mkdirs()
//            }
//            try {
//                val newFile = File(folder, fileName)
//                if (newFile.exists()) {
//                    newFile.delete()
//                }
//                Log.w("NewFileName:", newFile.toString())
//                //                newFile.createNewFile();
//                FileDownloader.downloadFile(fileUrl, newFile)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            return null
//        }
//    }

    private inner class InvoicePdfDownload(private val c: Context) :
        AsyncTask<String?, Int?, String?>() {
        private var file_progress_count = 0
        var newFile: File? = null
        override fun doInBackground(vararg sUrl: String?): String? {
            var `is`: InputStream? = null
            var os: OutputStream? = null
            var con: HttpURLConnection? = null
            val length: Int
            try {
                val url = URL(sUrl[0])
                con = url.openConnection() as HttpURLConnection
                con!!.connect()
                if (con.responseCode != HttpURLConnection.HTTP_OK) {
                    return "HTTP CODE: " + con.responseCode + " " + con.responseMessage
                }
                length = con.contentLength
                pd!!.max = length / 1000
                `is` = con.inputStream
                Log.w("DownloadImageURL:", url.toString())

                //String folderPath = Environment.getExternalStorageDirectory() + "/CatalogErp/Products";
                val folder = File(Constants.getFolderPath(this@NewInvoiceListActivity))
                if (!folder.exists()) {
                    val productsDirectory =
                        File(Constants.getFolderPath(this@NewInvoiceListActivity))
                    productsDirectory.mkdirs()
                }

                //create a new file
                val filepath = sUrl[1] + "_" + sUrl[2]
                val newfilePath = filepath.replace("/", "_")
                newFile = File(
                    Constants.getFolderPath(this@NewInvoiceListActivity),
                    "$newfilePath.pdf"
                )
                if (newFile!!.exists()) {
                    newFile!!.delete()
                }
                Log.w("GivenFilePath:", newFile.toString())
                newFile!!.createNewFile()
                //os = new FileOutputStream(Environment.getExternalStorageDirectory()+File.separator+"CatalogImages" + File.separator + "a-computer-engineer.jpg");
                os = FileOutputStream(newFile)
                val data = ByteArray(4096)
                var total: Long = 0
                var count: Int
                while (`is`.read(data).also { count = it } != -1) {
                    if (isCancelled) {
                        `is`.close()
                        return null
                    }
                    total += count.toLong()
                    if (length > 0) {
                        publishProgress(total.toInt())
                    }
                    file_progress_count = (100 * total / length.toLong()).toInt()
                    os.write(data, 0, count)
                }
            } catch (e: Exception) {
                Log.w("File_Write_Error:", e.message!!)
                return e.toString()
            } finally {
                try {
                    os?.close()
                    `is`?.close()
                } catch (ioe: IOException) {
                }
                con?.disconnect()
            }
            return null
        }


        override fun onPreExecute() {
            super.onPreExecute()
            pd!!.setMessage("Downloading Pdf..")
            pd!!.show()
        }

          fun onProgressUpdate(vararg progress: Int) {
            super.onProgressUpdate()
            pd!!.isIndeterminate = false
            pd!!.progress = progress[0] / 1000
        }

        override fun onPostExecute(result: String?) {
            try {
                Log.w("ProgressCount:", file_progress_count.toString() + "")
                if (file_progress_count == 100) {
                    pd!!.dismiss()
                    if (newFile!!.exists()) {
                        if (shareMode == "Share") {
                            pdfFile = newFile
                            displayFromAsset(newFile)
                        } else {
                            shareWhatsapp(newFile)
                        }
                    } else {
                        Toast.makeText(applicationContext, "NO file Download", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                if (result != null) {
                }
            } catch (exception: Exception) {
            }
        }
    }

    fun shareWhatsapp(file: File?) {
        val shareIntent = Intent()
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.setAction(Intent.ACTION_SEND)
        //without the below line intent will show error
        shareIntent.setType("application/pdf")
        shareIntent.putExtra(
            Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(
                Objects.requireNonNull(
                    applicationContext
                ),
                BuildConfig.APPLICATION_ID + ".provider", file!!
            )
        )
        // Target whatsapp:
        shareIntent.setPackage("com.whatsapp")
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            shareWhatsappBusiness(file)
            //Toast.makeText(NewInvoiceListActivity.this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    fun shareWhatsappBusiness(file: File?) {
        val shareIntent = Intent()
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.setAction(Intent.ACTION_SEND)
        //without the below line intent will show error
        shareIntent.setType("application/pdf")
        shareIntent.putExtra(
            Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(
                Objects.requireNonNull(
                    applicationContext
                ),
                BuildConfig.APPLICATION_ID + ".provider", file!!
            )
        )
        // Target whatsapp:
        shareIntent.setPackage("com.whatsapp.w4b")
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                this@NewInvoiceListActivity,
                "Whatsapp have not been installed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /*public void showPdf(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                storagePath = Environment.getExternalStorageDirectory();
                invoiceNO = "KJHS_q23";
                FILE = Environment.getExternalStorageDirectory() + "/" + invoiceNO+".pdf";
                fatchinvoiceList();
            } else {
                requestPermission(); // Code for permission
            }
        } else {
            storagePath = Environment.getExternalStorageDirectory();
            invoiceNO = "KJHS_q23";
            FILE = Environment.getExternalStorageDirectory() + "/" + invoiceNO+".pdf";
            fatchinvoiceList();

        }
    }
*/
    fun hideKeyboard() {
        try {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            // TODO: handle exception
        }
    }

    private fun filter(text: String) {
        try {
            //new array list that will hold the filtered data
            val filterdNames = ArrayList<CustomerModel>()
            //looping through existing elements
            for (s in customerList!!) {
                //if the existing elements contains the search input
                if (s.customerName.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault())) || s.customerCode.lowercase(
                        Locale.getDefault()
                    ).contains(text.lowercase(Locale.getDefault()))
                ) {
                    //adding the element to filtered list
                    filterdNames.add(s)
                }
            }
            //calling a method of the adapter class and passing the filtered list
            customerNameAdapter!!.filterList(filterdNames)
        } catch (ex: Exception) {
            Log.e("Error_in_filter", Objects.requireNonNull(ex.message!!))
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE)
        return if (result == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            false
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this,
                "Write External Storage permission allows us to save files. Please allow this permission in App Settings.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .")
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .")
            }
        }
    }

    fun fatchinvoiceList(invoiceDetails: ArrayList<InvoicePrintPreviewModel>) {
        // Create a document and set it's properties
        val objDocument = Document()
        objDocument.creator = "Powered By : Google.com"
        objDocument.author = "Google"
        objDocument.title = "Google Invoice"
        // Create a page to add to the document
        val objPage = Page(PageSize.LETTER, PageOrientation.PORTRAIT, 20.0f)
        val strText = "INVOICE"
        val invoiceTitle =
            Label(strText, 0f, 0f, 504f, 100f, Font.getHelvetica(), 20f, TextAlign.CENTER)
        // Create a Label to add to the page
        val objLabel = Label(
            "$company_name\n$company_address1\n $company_address2\n$company_address3",
            0f,
            100f,
            504f,
            100f,
            Font.getHelvetica(),
            12f,
            TextAlign.LEFT
        )
        val objLabel2 = Label(
            """
    Invoice Number: ${invoiceDetails[0].invoiceNumber}
    Invoice Date: ${invoiceDetails[0].invoiceDate}
    Customer Name: ${invoiceDetails[0].customerName}
    """.trimIndent(), 0f, 100f, 504f, 100f, Font.getHelvetica(), 12f, TextAlign.RIGHT
        )
        // Add label to page
        //objLabel.setTextOutlineColor(com.cete.dynamicpdf.Color);
        objPage.elements.add(invoiceTitle)
        objPage.elements.add(objLabel)
        objPage.elements.add(objLabel2)
        val line = Label(PdfUtils.drawLine(), 0f, 220f, 700f, 400f)
        objPage.elements.add(line)
        var index = 1
        var space = 240
        for (model in invoiceDetails) {
            Log.w("BiggestLength:", PdfUtils.getLength(model.invoiceList).toString() + "")
            val titleLabel = Label(
                "SNo" + PdfUtils.snoSpace() + "Description    " + PdfUtils.getDescriptionSpace(
                    15,
                    '\t'
                ) + "Qty" + PdfUtils.qtySpace() + "Price" + PdfUtils.priceSpace() + "Total",
                0f,
                200f,
                700f,
                100f,
                Font.getHelvetica(),
                15f,
                TextAlign.LEFT
            )
            objPage.elements.add(titleLabel)
            for (invoiceList in model.invoiceList) {
                if (invoiceList.description.length < 15) {
                    val titleLabel1 = Label(
                        index.toString() + "" + PdfUtils.snoSpace() + invoiceList.description + PdfUtils.getDescriptionSpace(
                            PdfUtils.getLength(model.invoiceList) + 11,
                            '\t'
                        ) + invoiceList.netQty.toDouble()
                            .toInt() + PdfUtils.qtySpace() + Utils.twoDecimalPoint(invoiceList.pricevalue.toDouble()) + PdfUtils.priceSpace() + Utils.twoDecimalPoint(
                            invoiceList.total.toDouble()
                        ), 0f, space.toFloat(), 700f, 100f, Font.getHelvetica(), 15f, TextAlign.LEFT
                    )
                    objPage.elements.add(titleLabel1)
                } else {
                    val titleLabel1 = Label(
                        index.toString() + "" + PdfUtils.snoSpace() + invoiceList.description.substring(
                            0,
                            15
                        ) + PdfUtils.getDescriptionSpace(15, '\t') + invoiceList.netQty.toDouble()
                            .toInt() + PdfUtils.qtySpace() + Utils.twoDecimalPoint(invoiceList.pricevalue.toDouble()) + PdfUtils.priceSpace() + Utils.twoDecimalPoint(
                            invoiceList.total.toDouble()
                        ), 0f, space.toFloat(), 700f, 100f, Font.getHelvetica(), 15f, TextAlign.LEFT
                    )
                    objPage.elements.add(titleLabel1)
                }
                index++
                space = space + 30
            }
            val totalLabel = Label(
                "Sub Total:" + Utils.twoDecimalPoint(model.subTotal.toDouble()) + " \nGST: " + Utils.twoDecimalPoint(
                    model.netTax.toDouble()
                ) + " \nNet Total: " + Utils.twoDecimalPoint(model.netTotal.toDouble()),
                0f,
                (space + 50).toFloat(),
                504f,
                100f,
                Font.getHelvetica(),
                15f,
                TextAlign.RIGHT
            )
            objPage.elements.add(totalLabel)
        }
        val line1 = Label(PdfUtils.drawLine(), 0f, (space + 30).toFloat(), 700f, 400f)
        objPage.elements.add(line1)

        // Add page to document
        objDocument.pages.add(objPage)
        try {
            objDocument.draw(FILE)
            try {
                val filepath = File(FILE)
                pdfFile = filepath
                sharePdfView(pdfFile)
                // displayFromAsset(pdfFile);
            } catch (e: ActivityNotFoundException) {
            }
        } catch (edx: Exception) {
            Log.e("PdfOpenError:", edx.message!!)
        }
    }

    fun sharePdfView(pdfFile: File?) {
        try {
            if (pdfGenerateDialog != null && pdfGenerateDialog!!.isShowing) {
                pdfGenerateDialog!!.dismiss()
            }
            Log.w("URL-FromthisPdf:", Uri.fromFile(pdfFile).toString())
            //FileProvider.getUriForFile(this,"Share",pdfFile);
            val share = Intent()
            share.setAction(Intent.ACTION_SEND)
            share.setType("application/pdf")
            share.putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(
                    Objects.requireNonNull(
                        applicationContext
                    ),
                    BuildConfig.APPLICATION_ID + ".provider", pdfFile!!
                )
            )
            startActivity(Intent.createChooser(share, "Share"))
        } catch (e: Exception) {
        }
    }

    fun createInvoicePdfTable(invoiceDetails: ArrayList<InvoicePrintPreviewModel?>?) {
        //Create a document object.
        val document = Document()
        document.creator = "Powered By : Google.com"
        document.author = "Google"
        document.title = "Google Invoice"

        //Create a page.
        val page = Page(PageSize.LETTER, PageOrientation.PORTRAIT, 20.0f)

        //Create Table2 object.
        val table2 = Table2(0f, 0f, 700f, 1000f)

        // Add columns to the table
        table2.columns.add(150f)
        table2.columns.add(90f)
        table2.columns.add(90f)
        table2.columns.add(90f)

        // Add rows to the table and add cells to the rows
        val row1 = table2.rows.add(
            40f,
            Font.getHelveticaBold(),
            16f,
            Grayscale.getBlack(),
            Grayscale.getGray()
        )
        row1.cellDefault.align = TextAlign.CENTER
        row1.cellDefault.vAlign = VAlign.CENTER
        row1.cells.add("Header 1")
        row1.cells.add("Header 2")
        row1.cells.add("Header 3")
        row1.cells.add("Header 4")
        val row2 = table2.rows.add(30f)
        val cell1 = row2.cells.add(
            "Rowheader 1",
            Font.getHelveticaBold(),
            16f,
            Grayscale.getBlack(),
            Grayscale.getGray(),
            1
        )
        cell1.align = TextAlign.CENTER
        cell1.vAlign = VAlign.CENTER
        row2.cells.add("Item 1")
        row2.cells.add("Item 2")
        row2.cells.add("Item 3")
        val row3 = table2.rows.add(30f)
        val cell2 = row3.cells.add(
            "Rowheader 2",
            Font.getHelveticaBold(),
            16f,
            Grayscale.getBlack(),
            Grayscale.getGray(),
            1
        )
        cell2.align = TextAlign.CENTER
        cell2.vAlign = VAlign.CENTER
        row3.cells.add("Item 4")
        row3.cells.add(
            "Item 5, this item is much longer than the rest so that " +
                    "you can see that each row will automatically expand to fit to the " +
                    "height of the largest element in that row."
        )
        row3.cells.add("Item 6")
        val row4 = table2.rows.add(30f)
        val cell3 = row4.cells.add(
            "Rowheader 3",
            Font.getHelveticaBold(),
            16f,
            Grayscale.getBlack(),
            Grayscale.getGray(),
            1
        )
        cell3.align = TextAlign.CENTER
        cell3.vAlign = VAlign.CENTER
        row4.cells.add("Item 7")
        row4.cells.add("Item 8")
        row4.cells.add("Item 9")
        // Add the table to the page
        page.elements.add(table2)
        //add page to the document.
        document.pages.add(page)

        //Save document to file
        try {
            document.draw(FILE)
            try {
                val filepath = File(FILE)
                pdfFile = filepath
                // sharePdfView(pdfFile);
                displayFromAsset(pdfFile)
            } catch (e: ActivityNotFoundException) {
            }
        } catch (edx: Exception) {
            Log.e("PdfOpenError:", edx.message!!)
        }
    }

    private fun displayFromAsset(assetFileName: File?) {
        Log.w("DisplayFileName::", assetFileName.toString())
        customerLayout!!.visibility = View.GONE
        invoiceOptionLayout!!.visibility = View.GONE
        pdfViewLayout!!.visibility = View.VISIBLE
        if (behavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
        } else {
            behavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
        pdfView!!.fromFile(assetFileName)
            .defaultPage(pageNumber)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .onPageChange(this)
            .enableAnnotationRendering(true)
            .onLoad(this)
            .scrollHandle(DefaultScrollHandle(this))
            .load()
    }

    // slide the view from its current position to below itself
    fun slideDown(view: View) {
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            0f,  // fromYDelta
            view.height.toFloat()
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    fun slideUp(view: View) {
        // view.setVisibility(View.VISIBLE);
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            view.height.toFloat(),  // fromYDelta
            0f
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    fun getDate(dateEditext: EditText?) {
        // Get Current Date
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this@NewInvoiceListActivity,
            { view, year, monthOfYear, dayOfMonth -> dateEditext!!.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year) },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.show()
    }

    /* private void getInvoicePrintDetails(String invoiceNumber,int copy) throws JSONException {
        // Initialize a new RequestQueue instance
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyId);
        jsonObject.put("InvoiceNo",invoiceNumber);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url= Utils.getBaseUrl(this) +"SalesApi/GetInvoiceByCode?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Generating Print Preview...");
        pDialog.setCancelable(false);
        pDialog.show();
        invoiceHeaderDetails =new ArrayList<>();
        invoicePrintList=new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Invoice_Details:",response.toString());
                        if (response.length() > 0) {
                            InvoicePrintPreviewModel model = new InvoicePrintPreviewModel();
                            model.setInvoiceNumber(response.optString("InvoiceNo"));
                            model.setInvoiceDate(response.optString("InvoiceDateString"));
                            model.setCustomerCode(response.optString("CustomerCode"));
                            model.setCustomerName(response.optString("CustomerName"));
                            model.setAddress(response.optString("Address1"));
                            model.setDeliveryAddress(response.optString("Address1"));
                            model.setSubTotal(response.optString("SubTotal"));
                            model.setNetTax(response.optString("Tax"));
                            model.setNetTotal(response.optString("NetTotal"));
                            model.setTaxType(response.optString("TaxType"));
                            model.setTaxValue(response.optString("TaxPerc"));
                            model.setOutStandingAmount(response.optString("BalanceAmount"));
                            model.setBillDiscount(response.optString("BillDIscount"));
                            model.setItemDiscount(response.optString("ItemDiscount"));

                            JSONArray products = response.getJSONArray("InvoiceDetails");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject object = products.getJSONObject(i);
                                if (Double.parseDouble(object.optString("LQty")) > 0 || Double.parseDouble(object.optString("LQty")) < 0) {
                                    InvoicePrintPreviewModel.InvoiceList invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                                    invoiceListModel.setProductCode(object.optString("ProductCode"));
                                    invoiceListModel.setDescription(object.optString("ProductName"));
                                    invoiceListModel.setLqty(object.optString("LQty"));
                                    invoiceListModel.setCqty(object.optString("CQty"));
                                    invoiceListModel.setNetQty(object.optString("LQty"));
                                    double qty = Double.parseDouble(object.optString("LQty"));
                                    double price = Double.parseDouble(object.optString("Price"));

                                    double nettotal = qty * price;
                                    invoiceListModel.setTotal(String.valueOf(nettotal));
                                    invoiceListModel.setPricevalue(String.valueOf(price));
                                    invoiceListModel.setCartonPrice(object.optString("CartonPrice"));
                                    invoiceListModel.setUnitPrice(object.optString("Price"));
                                    invoiceListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                    invoiceListModel.setItemtax(object.optString("Tax"));
                                    invoiceListModel.setSubTotal(object.optString("SubTotal"));
                                    invoicePrintList.add(invoiceListModel);


                                    if (!object.optString("ReturnQty").isEmpty() && !object.optString("ReturnQty").equals("null") && Double.parseDouble(object.optString("ReturnQty")) > 0)  {
                                        invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                                        invoiceListModel.setProductCode(object.optString("ProductCode"));
                                        invoiceListModel.setDescription(object.optString("ProductName"));
                                        invoiceListModel.setLqty(object.optString("LQty"));
                                        invoiceListModel.setCqty(object.optString("CQty"));
                                        invoiceListModel.setNetQty("-"+object.optString("ReturnQty"));

                                        double qty1 = Double.parseDouble(object.optString("ReturnQty"));
                                        double price1 = Double.parseDouble(object.optString("Price"));
                                        double nettotal1 = qty1 * price1;
                                        invoiceListModel.setTotal("-"+String.valueOf(nettotal1));
                                        invoiceListModel.setPricevalue(String.valueOf(price1));

                                        invoiceListModel.setUomCode(object.optString("UOMCode"));
                                        invoiceListModel.setCartonPrice(object.optString("CartonPrice"));
                                        invoiceListModel.setUnitPrice(object.optString("Price"));
                                        invoiceListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        invoiceListModel.setItemtax(object.optString("Tax"));
                                        invoiceListModel.setSubTotal(object.optString("SubTotal"));
                                        invoicePrintList.add(invoiceListModel);
                                    }


                                    if (Double.parseDouble(object.optString("CQty")) > 0 || Double.parseDouble(object.optString("CQty")) < 0) {
                                        invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                                        invoiceListModel.setProductCode(object.optString("ProductCode"));
                                        invoiceListModel.setDescription(object.optString("ProductName"));
                                        invoiceListModel.setLqty(object.optString("LQty"));
                                        invoiceListModel.setCqty(object.optString("CQty"));
                                        invoiceListModel.setNetQty(object.optString("CQty"));

                                        double qty1 = Double.parseDouble(object.optString("CQty"));
                                        double price1 = Double.parseDouble(object.optString("CartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        invoiceListModel.setTotal(String.valueOf(nettotal1));
                                        invoiceListModel.setPricevalue(String.valueOf(price1));

                                        invoiceListModel.setUomCode(object.optString("UOMCode"));
                                        invoiceListModel.setCartonPrice(object.optString("CartonPrice"));
                                        invoiceListModel.setUnitPrice(object.optString("Price"));
                                        invoiceListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        invoiceListModel.setItemtax(object.optString("Tax"));
                                        invoiceListModel.setSubTotal(object.optString("SubTotal"));
                                        invoicePrintList.add(invoiceListModel);
                                    }

                                } else {
                                    if (Double.parseDouble(object.optString("CQty")) > 0 || Double.parseDouble(object.optString("CQty")) < 0) {
                                        Log.w("PrintedCqtyValue:", object.optString("CQty"));
                                        InvoicePrintPreviewModel.InvoiceList invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                                        invoiceListModel.setProductCode(object.optString("ProductCode"));
                                        invoiceListModel.setDescription(object.optString("ProductName"));
                                        invoiceListModel.setLqty(object.optString("LQty"));
                                        invoiceListModel.setCqty(object.optString("CQty"));
                                        invoiceListModel.setNetQty(object.optString("CQty"));

                                        double qty1 = Double.parseDouble(object.optString("CQty"));
                                        double price1 = Double.parseDouble(object.optString("CartonPrice"));
                                        double nettotal1 = qty1 * price1;
                                        invoiceListModel.setTotal(String.valueOf(nettotal1));
                                        invoiceListModel.setPricevalue(String.valueOf(price1));

                                        invoiceListModel.setUomCode(object.optString("UOMCode"));
                                        invoiceListModel.setCartonPrice(object.optString("CartonPrice"));
                                        invoiceListModel.setUnitPrice(object.optString("Price"));
                                        invoiceListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                        invoiceListModel.setItemtax(object.optString("Tax"));
                                        invoiceListModel.setSubTotal(object.optString("SubTotal"));
                                        invoicePrintList.add(invoiceListModel);


                                        if (!object.optString("ReturnQty").isEmpty() && !object.optString("ReturnQty").equals("null") && Double.parseDouble(object.optString("ReturnQty")) > 0)  {
                                            invoiceListModel = new InvoicePrintPreviewModel.InvoiceList();
                                            invoiceListModel.setProductCode(object.optString("ProductCode"));
                                            invoiceListModel.setDescription(object.optString("ProductName"));
                                            invoiceListModel.setLqty(object.optString("LQty"));
                                            invoiceListModel.setCqty(object.optString("CQty"));
                                            invoiceListModel.setNetQty("-"+object.optString("ReturnQty"));

                                            double qty12 = Double.parseDouble(object.optString("ReturnQty"));
                                            double price12 = Double.parseDouble(object.optString("Price"));
                                            double nettotal12 = qty12 * price12;
                                            invoiceListModel.setTotal("-"+String.valueOf(nettotal12));
                                            invoiceListModel.setPricevalue(String.valueOf(price12));

                                            invoiceListModel.setUomCode(object.optString("UOMCode"));
                                            invoiceListModel.setCartonPrice(object.optString("CartonPrice"));
                                            invoiceListModel.setUnitPrice(object.optString("Price"));
                                            invoiceListModel.setPcsperCarton(object.optString("PcsPerCarton"));
                                            invoiceListModel.setItemtax(object.optString("Tax"));
                                            invoiceListModel.setSubTotal(object.optString("SubTotal"));
                                            invoicePrintList.add(invoiceListModel);
                                        }
                                    }
                                }
                            }

                            model.setInvoiceList(invoicePrintList);
                            invoiceHeaderDetails.add(model);
                        }

                        pDialog.dismiss();
                        ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(NewInvoiceListActivity.this,printerMacId);
                        if (isInvoicePrint){
                            zebraPrinterActivity.printInvoice(copy,invoiceHeaderDetails,invoicePrintList,"false");
                        }else {
                            zebraPrinterActivity.printDeliveryOrder(copy,invoiceHeaderDetails,invoicePrintList);
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
    }*/
    fun loadFragment(fragment: Fragment?) {
        val fm = supportFragmentManager
        val fragmentTransaction = fm.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment!!)
        fragmentTransaction.commit()
    }

    fun getCustomerDetails(customerCode: String?, isloader: Boolean) {
        // Initialize a new RequestQueue instance
        val requestQueue = Volley.newRequestQueue(this@NewInvoiceListActivity)
        // Initialize a new JsonArrayRequest instance
        val jsonObject = JSONObject()
        try {
            jsonObject.put("CustomerCode", customerCode)
            jsonObject.put("CompanyCode", companyCode)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val url =
            Utils.getBaseUrl(this@NewInvoiceListActivity) + "MasterApi/GetCustomer?Requestdata=" + jsonObject.toString()
        Log.w("Given_url:", url)
        val progressDialog = ProgressDialog(this@NewInvoiceListActivity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Customer Details Loading...")
        if (isloader) {
            progressDialog.show()
        }
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            Response.Listener { response: JSONObject ->
                try {
                    Log.w("details_response:", response.toString())
                    if (response.length() > 0) {
                        if (response.optString("ErrorMessage") == "null") {
                            oustandingAmount = response.optString("BalanceAmount")
                            progressDialog.dismiss()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error in getting response",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    // pDialog.dismiss();
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error: VolleyError ->
                // Do something when error occurred
                //  pDialog.dismiss();
                Log.w("Error_throwing:", error.toString())
                progressDialog.dismiss()
            }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                val creds =
                    String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD)
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

    @Throws(JSONException::class)
    private fun getInvoicePrintDetails(invoiceNumber: String?, copy: Int, doPrint: String) {
        // Initialize a new RequestQueue instance
        val jsonObject = JSONObject()
        // jsonObject.put("CompanyCode", companyId);
        jsonObject.put("InvoiceNo", invoiceNumber)
        jsonObject.put("LocationCode", locationCode1)
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "InvoiceDetails"
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url + jsonObject)
        // pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        // pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        // pDialog.setTitleText("Generating Print Preview...");
        // pDialog.setCancelable(false);
        //  pDialog.show();
        invoiceHeaderDetails = ArrayList()
        invoicePrintList = ArrayList()
        salesReturnList = ArrayList()
        // {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"WinApp","customerName":"WinApp","invoiceNumber":"33",
        // "invoiceStatus":"O","invoiceDate":"6\/8\/2021 12:00:00 am","netTotal":"26.750000","balanceAmount":"26.750000","totalDiscount":
        // "0.000000","paidAmount":"0.000000","contactPersonCode":"","createDate":"6\/8\/2021 12:00:00 am","updateDate":"6\/8\/2021 12:00:00 am",
        // "remark":"","fDocTotal":"0.000000","fTaxAmount":"0.000000","receivedAmount":"0.000000","total":"26.750000","fTotal":"0.000000",
        // "iTotalDiscount":"0.000000","taxTotal":"1.750000","iPaidAmount":"0.000000","currencyCode":"SGD","currencyName":"Singapore Dollar",
        // "companyCode":"WINAPP_DEMO","docEntry":"20","invoiceDetails":[{"slNo":"1","companyCode":"WINAPP_DEMO","invoiceNo":"33",
        // "productCode":"FG\/001245","productName":"RUM","quantity":"5.000000","price":"5.000000","currency":"SGD","taxRate":"0.000000",
        // "discountPercentage":"0.000000","lineTotal":"26.750000","fRowTotal":"0.000000","warehouseCode":"01","salesEmployeeCode":"-1",
        // "accountCode":"400000","taxStatus":"Y","unitPrice":"5.000000","customerCategoryNo":"","barCodes":"","totalTax":"1.750000",
        // "fTaxAmount":"0.000000","taxCode":"","taxType":"Y","taxPerc":"0.000000","uoMCode":null,"invoiceDate":"6\/8\/2021 12:00:00 am",
        // "dueDate":"6\/8\/2021 12:00:00 am","createDate":"6\/8\/2021 12:00:00 am","updateDate":"6\/8\/2021 12:00:00 am","createdUser":"manager"}]}]}
        val jsonObjectRequest: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, jsonObject,
                Response.Listener { response: JSONObject ->
                    try {
                        Log.w("DetailsResponse::", response.toString())
                        val statusCode = response.optString("statusCode")
                        if (statusCode == "1") {
                            val responseData = response.getJSONArray("responseData")
                            val `object` = responseData.optJSONObject(0)
                            val model = InvoicePrintPreviewModel()
                            model.invoiceNumber = `object`.optString("invoiceNumber")
                            model.invoiceDate = `object`.optString("invoiceDate")
                            model.customerCode = `object`.optString("customerCode")
                            model.customerName = `object`.optString("customerName")
                            model.overAllTotal = `object`.optString("overAllTotal")
                            // model.setAddress(object.optString("street"));
                            model.address =
                                `object`.optString("address1") + `object`.optString("address2") + `object`.optString(
                                    "address3"
                                )
                            model.address1 = `object`.optString("address1")
                            model.address2 = `object`.optString("address2")
                            model.address3 = `object`.optString("address3")
                            model.addressstate = (`object`.optString("street") + " "
                                    + `object`.optString("block") + " " + `object`.optString("city"))
                            model.addresssZipcode =
                                (`object`.optString("countryName") + " " + `object`.optString("state") + " "
                                        + `object`.optString("zipcode"))

                            // model.setDeliveryAddress(model.getAddress());
                            model.subTotal = `object`.optString("subTotal")
                            model.netTax = `object`.optString("taxTotal")
                            model.netTotal = `object`.optString("netTotal")
                            model.paymentTerm = `object`.optString("paymentTerm")
                            model.taxType = `object`.optString("taxType")
                            model.taxValue = `object`.optString("taxPerc")
                            model.outStandingAmount = `object`.optString("totalOutstandingAmount")
                            model.balanceAmount = `object`.optString("balanceAmount")
                            Utils.setInvoiceOutstandingAmount(`object`.optString("balanceAmount"))
                            Utils.setInvoiceMode("Invoice")
                            model.billDiscount = `object`.optString("billDiscount")
                            model.itemDiscount = `object`.optString("totalDiscount")
                            model.soNumber = `object`.optString("soNumber")
                            model.soDate = `object`.optString("soDate")
                            model.doDate = `object`.optString("doDate")
                            model.doNumber = `object`.optString("doNumber")
                            model.allowDeliveryAddress = `object`.optString("showShippingAddress")
                            model.currentAddress = `object`.optString("CurrentAddress")
                            val signFlag = `object`.optString("signFlag")
                            if (signFlag == "Y") {
                                val signature = `object`.optString("signature")
                                Utils.setSignature(signature)
                                createSignature()
                            } else {
                                Utils.setSignature("")
                            }
                            val detailsArray = `object`.optJSONArray("invoiceDetails")
                            for (i in 0 until detailsArray.length()) {
                                val detailObject = detailsArray.optJSONObject(i)
                                if (detailObject.optString("quantity").toDouble() > 0) {
                                    val invoiceListModel = InvoicePrintPreviewModel.InvoiceList()
                                    invoiceListModel.productCode =
                                        detailObject.optString("productCode")
                                    invoiceListModel.description =
                                        detailObject.optString("productName")
                                    invoiceListModel.lqty = detailObject.optString("unitQty")
                                    invoiceListModel.cqty = detailObject.optString("cartonQty")
                                    invoiceListModel.netQty = detailObject.optString("quantity")
                                    invoiceListModel.excQty = detailObject.optString("exc_Qty")
                                    invoiceListModel.netQuantity =
                                        detailObject.optString("netQuantity")
                                    invoiceListModel.focQty = detailObject.optString("foc_Qty")
                                    invoiceListModel.saleType = ""
                                    if (detailObject.optString("bP_CatalogNo") != null) {
                                        invoiceListModel.customerItemCode =
                                            detailObject.optString("bP_CatalogNo")
                                    }
                                    invoiceListModel.returnQty = detailObject.optString("returnQty")
                                    invoiceListModel.cartonPrice =
                                        detailObject.optString("cartonPrice")
                                    invoiceListModel.unitPrice = detailObject.optString("price")
                                    val qty = detailObject.optString("quantity").toDouble()
                                    val price = detailObject.optString("price").toDouble()
                                    invoiceListModel.uomCode = detailObject.optString("uomCode")
                                    val nettotal = qty * price
                                    invoiceListModel.total = nettotal.toString()
                                    invoiceListModel.pricevalue = price.toString()
                                    invoiceListModel.pcsperCarton =
                                        detailObject.optString("pcsPerCarton")
                                    invoiceListModel.itemtax = detailObject.optString("totalTax")
                                    invoiceListModel.subTotal = detailObject.optString("subTotal")
                                    invoicePrintList!!.add(invoiceListModel)
                                    Log.w("invoicSizeEntr1", "")
                                }
                                if (!detailObject.optString("returnQty")
                                        .isEmpty() && detailObject.optString("returnQty") != "null" && detailObject.optString(
                                        "returnQty"
                                    ).toDouble() > 0
                                ) {
                                    val invoiceListModel = InvoicePrintPreviewModel.InvoiceList()
                                    invoiceListModel.productCode =
                                        detailObject.optString("productCode")
                                    invoiceListModel.description =
                                        detailObject.optString("productName")
                                    invoiceListModel.lqty = detailObject.optString("unitQty")
                                    invoiceListModel.cqty = detailObject.optString("cartonQty")
                                    invoiceListModel.netQty = detailObject.optString("quantity")
                                    invoiceListModel.excQty = detailObject.optString("exc_Qty")
                                    invoiceListModel.netQuantity =
                                        detailObject.optString("returnQty")
                                    invoiceListModel.focQty = detailObject.optString("foc_Qty")
                                    invoiceListModel.saleType = "Return"
                                    if (detailObject.optString("bP_CatalogNo") != null) {
                                        invoiceListModel.customerItemCode =
                                            detailObject.optString("bP_CatalogNo")
                                    }
                                    invoiceListModel.returnQty = detailObject.optString("returnQty")
                                    invoiceListModel.cartonPrice =
                                        detailObject.optString("cartonPrice")
                                    invoiceListModel.unitPrice = detailObject.optString("price")
                                    val qty = detailObject.optString("quantity").toDouble()
                                    val price = detailObject.optString("price").toDouble()
                                    val nettotal = qty * price
                                    invoiceListModel.total = "0.00"
                                    invoiceListModel.pricevalue = "0.00"
                                    invoiceListModel.pcsperCarton =
                                        detailObject.optString("pcsPerCarton")
                                    invoiceListModel.itemtax = detailObject.optString("totalTax")
                                    invoiceListModel.subTotal = detailObject.optString("subTotal")
                                    invoicePrintList!!.add(invoiceListModel)
                                    Log.w("invoicSizeEntr2", "")
                                }
                                if (!detailObject.optString("exc_Qty")
                                        .isEmpty() && detailObject.optString("exc_Qty") != "null" && detailObject.optString(
                                        "exc_Qty"
                                    ).toDouble() > 0
                                ) {
                                    val invoiceListModel = InvoicePrintPreviewModel.InvoiceList()
                                    invoiceListModel.productCode =
                                        detailObject.optString("productCode")
                                    invoiceListModel.description =
                                        detailObject.optString("productName")
                                    invoiceListModel.lqty = detailObject.optString("unitQty")
                                    invoiceListModel.cqty = detailObject.optString("cartonQty")
                                    invoiceListModel.netQty = detailObject.optString("quantity")
                                    invoiceListModel.excQty = detailObject.optString("exc_Qty")
                                    invoiceListModel.netQuantity = detailObject.optString("exc_Qty")
                                    invoiceListModel.focQty = detailObject.optString("foc_Qty")
                                    invoiceListModel.saleType = "Exchange"
                                    if (detailObject.optString("bP_CatalogNo") != null) {
                                        invoiceListModel.customerItemCode =
                                            detailObject.optString("bP_CatalogNo")
                                    }
                                    invoiceListModel.returnQty = detailObject.optString("returnQty")
                                    invoiceListModel.cartonPrice =
                                        detailObject.optString("cartonPrice")
                                    invoiceListModel.unitPrice = detailObject.optString("price")
                                    val qty = detailObject.optString("quantity").toDouble()
                                    val price = detailObject.optString("price").toDouble()
                                    val nettotal = qty * price
                                    invoiceListModel.total = "0.00"
                                    invoiceListModel.pricevalue = "0.00"
                                    invoiceListModel.pcsperCarton =
                                        detailObject.optString("pcsPerCarton")
                                    invoiceListModel.itemtax = detailObject.optString("totalTax")
                                    invoiceListModel.subTotal = detailObject.optString("subTotal")
                                    invoicePrintList!!.add(invoiceListModel)
                                    Log.w("invoicSizeEntr3", "")
                                }
                                if (!detailObject.optString("foc_Qty")
                                        .isEmpty() && detailObject.optString("foc_Qty") != "null" && detailObject.optString(
                                        "foc_Qty"
                                    ).toDouble() > 0
                                ) {
                                    val invoiceListModel = InvoicePrintPreviewModel.InvoiceList()
                                    invoiceListModel.productCode =
                                        detailObject.optString("productCode")
                                    invoiceListModel.description =
                                        detailObject.optString("productName")
                                    invoiceListModel.lqty = detailObject.optString("unitQty")
                                    invoiceListModel.cqty = detailObject.optString("cartonQty")
                                    invoiceListModel.netQty = detailObject.optString("quantity")
                                    invoiceListModel.excQty = detailObject.optString("exc_Qty")
                                    invoiceListModel.netQuantity = detailObject.optString("foc_Qty")
                                    invoiceListModel.focQty = detailObject.optString("foc_Qty")
                                    invoiceListModel.saleType = "FOC"
                                    if (detailObject.optString("bP_CatalogNo") != null) {
                                        invoiceListModel.customerItemCode =
                                            detailObject.optString("bP_CatalogNo")
                                    }
                                    invoiceListModel.returnQty = detailObject.optString("returnQty")
                                    invoiceListModel.cartonPrice =
                                        detailObject.optString("cartonPrice")
                                    invoiceListModel.unitPrice = detailObject.optString("price")
                                    val qty = detailObject.optString("quantity").toDouble()
                                    val price = detailObject.optString("price").toDouble()
                                    val nettotal = qty * price
                                    invoiceListModel.total = "0.00"
                                    invoiceListModel.pricevalue = "0.00"
                                    invoiceListModel.pcsperCarton =
                                        detailObject.optString("pcsPerCarton")
                                    invoiceListModel.itemtax = detailObject.optString("totalTax")
                                    invoiceListModel.subTotal = detailObject.optString("subTotal")
                                    invoicePrintList!!.add(invoiceListModel)
                                    Log.w("invoicSizeEntr4", "")
                                }
                            }
                            model.invoiceList = invoicePrintList
                            val SRArray = `object`.optJSONArray("sR_Details")!!
                            if (SRArray.length() > 0) {
                                val SRoblect = SRArray.optJSONObject(0)
                                val salesReturnModel = InvoicePrintPreviewModel.SalesReturnList()
                                salesReturnModel.salesReturnNumber =
                                    SRoblect.optString("salesReturnNumber")
                                salesReturnModel.setsRSubTotal(SRoblect.optString("sR_SubTotal"))
                                salesReturnModel.setsRTaxTotal(SRoblect.optString("sR_TaxTotal"))
                                salesReturnModel.setsRNetTotal(SRoblect.optString("sR_NetTotal"))
                                salesReturnList!!.add(salesReturnModel)
                            }
                            model.salesReturnList = salesReturnList
                            invoiceHeaderDetails!!.add(model)
                            printInvoice(copy, doPrint)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error in printing Data...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error: VolleyError ->
                    // Do something when error occurred
                    //  pDialog.dismiss();
                    Log.w("Error_throwing:", error.toString())
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        selectedUser = usersList!![position].userName
        Log.w("UserSelected:", selectedUser!!)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedUser = ""
    }

    private fun createSignature() {
        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
            try {
                ImageUtil.saveStamp(this, Utils.getSignature(), "Signature")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun validatePrinterConfiguration(): Boolean {
        var printetCheck = false
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "This device does not support bluetooth", Toast.LENGTH_SHORT)
                .show()
        } else if (!mBluetoothAdapter.isEnabled) {
            // Bluetooth is not enabled :)
            Toast.makeText(this, "Enable bluetooth and connect the printer", Toast.LENGTH_SHORT)
                .show()
        } else {
            // Bluetooth is enabled
            if (!printerType!!.isEmpty() && !printerMacId!!.isEmpty()) {
                printetCheck = true
            } else {
                Toast.makeText(this, "Please configure Printer", Toast.LENGTH_SHORT).show()
            }
        }
        return printetCheck
    }

    fun printInvoice(copy: Int, doPrint: String?) {
        if (validatePrinterConfiguration()) {
            if (createInvoiceSetting == "true") {
                Log.w("invPrinter", "cg")
                val printerUtils = PrinterUtils(this, printerMacId)
                printerUtils.printInvoice(copy, invoiceHeaderDetails, invoicePrintList, doPrint)

                /*  TSCPrinter printer = new TSCPrinter(NewInvoiceListActivity.this, printerMacId);
            printer.printInvoice(copy, invoiceHeaderDetails, invoicePrintList, "false");
            printer.setOnCompletionListener(new TSCPrinter.OnCompletionListener() {
                @Override
                public void onCompleted() {
                    Utils.setSignature("");
                    Toast.makeText(getApplicationContext(), "Invoice printed successfully!", Toast.LENGTH_SHORT).show();
                }
            });*/
            } else {
                if (isInvoicePrint) {
                    Log.w("invPrinter11", "")
                    if (printerType.equals("Zebra Printer", ignoreCase = true)) {
                        val zebraPrinterActivity =
                            ZebraPrinterActivity(this@NewInvoiceListActivity, printerMacId)
                        zebraPrinterActivity.printInvoice(
                            copy,
                            invoiceHeaderDetails,
                            invoicePrintList,
                            "false"
                        )
                    } else if (printerType.equals("TSC Printer", ignoreCase = true)) {
                        Log.w("invPrinter22", "")
                        val printer = TSCPrinter(this@NewInvoiceListActivity, printerMacId)
                        printer.printInvoice(copy, invoiceHeaderDetails, invoicePrintList, "false")
                        printer.setOnCompletionListener {
                            Utils.setSignature("")
                            Toast.makeText(
                                applicationContext,
                                "Invoice printed successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                        // ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(NewInvoiceListActivity.this,printerMacId);
                        // zebraPrinterActivity.printInvoice(copy,invoiceHeaderDetails,invoicePrintList,"false");
                    }
                } else {
                    // zebraPrinterActivity.printDeliveryOrder(copy,invoiceHeaderDetails,invoicePrintList);
                }
            }
        } else {
            Toast.makeText(applicationContext, "Please configure the Printer", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        var selectCustomer: TextView? = null
        var behavior: BottomSheetBehavior<*>? = null
        var customerLayout: View? = null
        var salesOrderOptionLayout: View? = null
        @JvmField
        var isSearchCustomerNameClicked = false
        var invoiceOptionLayout: View? = null
        var invoiceCustomerName: TextView? = null
        var invoiceDate: String? = null
        var invoiceStatusValue: String? = null
        var invoiceNumber: TextView? = null
        var invoiceCustomerCodeValue: String? = null
        var invoiceCustomerValue: String? = null
        var invoiceNumberValue: String? = null
        var editInvoiceLayout: LinearLayout? = null
        var deleteInvoiceLayout: LinearLayout? = null
        var cashCollectionLayout: LinearLayout? = null
        var printPreviewLayout: LinearLayout? = null
        var duplicateInvoiceLayout: LinearLayout? = null
        var invoicePrintLayout: LinearLayout? = null
        var doPrintLayout: LinearLayout? = null
        private var cancelInvoiceLayout: LinearLayout? = null
        @JvmField
        var isLastSales = "false"
        private var visibleFragment = "invoices"
        @JvmField
        var selectCustomerId = ""
        var selectCustomerName: String? = ""
        var REQUEST_PERMISSIONS = 154
        private var FILE: String? = null
        private const val PERMISSION_REQUEST_CODE = 100
        var pdfViewLayout: View? = null
        var invStatus: String? = null
        var customerCodeStr = ""
        var customerNameStr = ""
        var zoneStrName: String? = ""
        @JvmField
        var zoneStrCode: String? = ""
        @JvmField
        var locationCode1: String? = ""

        // Storage Permissions
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
            permission.READ_EXTERNAL_STORAGE,
            permission.WRITE_EXTERNAL_STORAGE,
            permission.MANAGE_EXTERNAL_STORAGE
        )
        @JvmField
        var shortCodeStr = ""
        @JvmStatic
        fun showInvoiceOption(
            invoiceCustomerCode: String,
            customerName: String,
            invoiceNum: String?,
            invoiceStatus: String,
            date: String?
        ) {
            Log.w("InvoiceStatusView:", invoiceStatus)
            invStatus = invoiceStatus
            customerCodeStr = invoiceCustomerCode
            customerNameStr = customerName
            isSearchCustomerNameClicked = false
            // searchFilterView.setVisibility(View.GONE);
            if (invoiceStatus == "Paid" || visibleFragment == "paid") {
                //   editInvoiceLayout.setVisibility(View.GONE);
                cashCollectionLayout!!.visibility = View.GONE
                deleteInvoiceLayout!!.visibility = View.GONE
                invoiceStatusValue = "P"
            } else if (invoiceStatus == "Partial") {
                //   editInvoiceLayout.setVisibility(View.GONE);
                cashCollectionLayout!!.visibility = View.VISIBLE
                deleteInvoiceLayout!!.visibility = View.GONE
                invoiceStatusValue = "PR"
            } else if (invoiceStatus == "Open" || invoiceStatus == "O") {
                // editInvoiceLayout.setVisibility(View.VISIBLE);
                cashCollectionLayout!!.visibility = View.VISIBLE
                //  duplicateInvoiceLayout.setVisibility(View.VISIBLE);
                invoiceStatusValue = "O"
                // deleteInvoiceLayout.setVisibility(View.VISIBLE);
            }
            invoiceCustomerCodeValue = invoiceCustomerCode
            invoiceCustomerValue = customerName
            invoiceNumberValue = invoiceNum
            invoiceDate = date
            customerLayout!!.visibility = View.GONE
            invoiceOptionLayout!!.visibility = View.VISIBLE
            invoiceNumber!!.text = invoiceNum
            invoiceCustomerName!!.text =
                invoiceCustomerValue
            viewCloseBottomSheet()
        }

        @JvmStatic
        fun viewCloseBottomSheet() {
            //  hideKeyboard();
            pdfViewLayout!!.visibility = View.GONE
            if (isSearchCustomerNameClicked) {
                customerLayout!!.visibility = View.VISIBLE
                invoiceOptionLayout!!.visibility = View.GONE
            } else {
                customerLayout!!.visibility = View.GONE
                invoiceOptionLayout!!.visibility = View.VISIBLE
            }
            if (behavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                behavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
            // get the Customer name from the local db
        }

        /**
         * Checks if the app has permission to write to device storage
         *
         *
         * If the app does not has permission then the user will be prompted to grant permissions
         *
         * @param activity
         */
        fun verifyStoragePermissions(activity: Activity) {
            // Check if we have write permission
            val permission =
                ActivityCompat.checkSelfPermission(activity, permission.WRITE_EXTERNAL_STORAGE)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
            checkPermission(activity)
        }

        private fun checkPermission(activity: Activity): Boolean {
            return if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.isExternalStorageManager()
            } else {
                val result =
                    ContextCompat.checkSelfPermission(activity, permission.READ_EXTERNAL_STORAGE)
                val result1 =
                    ContextCompat.checkSelfPermission(activity, permission.WRITE_EXTERNAL_STORAGE)
                result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
            }
        }
    }
}