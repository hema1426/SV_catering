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
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
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
import com.cete.dynamicpdf.Page
import com.cete.dynamicpdf.PageOrientation
import com.cete.dynamicpdf.PageSize
import com.cete.dynamicpdf.TextAlign
import com.cete.dynamicpdf.pageelements.Label
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
import com.winapp.svcatering.R
import com.winapp.svcatering.adapter.ExpenselistModulAdapter
import com.winapp.svcatering.adapter.SelectCustomerAdapter
import com.winapp.svcatering.adapter.SortAdapter
import com.winapp.svcatering.db.DBHelper
import com.winapp.svcatering.fragments.CustomerFragment
import com.winapp.svcatering.fragments.ExpenseModuleListFragment
import com.winapp.svcatering.fragments.PaidInvoices
import com.winapp.svcatering.fragments.UnpaidInvoices
import com.winapp.svcatering.model.AppUtils
import com.winapp.svcatering.model.CustomerDetails
import com.winapp.svcatering.model.CustomerModel
import com.winapp.svcatering.model.InvoiceModel
import com.winapp.svcatering.model.InvoicePrintPreviewModel
import com.winapp.svcatering.model.InvoicePrintPreviewModel.InvoiceList
import com.winapp.svcatering.model.SupplierModel
import com.winapp.svcatering.model.UserListModel
import com.winapp.svcatering.utils.Constants
import com.winapp.svcatering.utils.ImageUtil
import com.winapp.svcatering.utils.Pager
import com.winapp.svcatering.utils.PdfUtils
import com.winapp.svcatering.utils.SessionManager
import com.winapp.svcatering.utils.Utils
import com.winapp.svcatering.zebraprinter.TSCPrinter
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Objects

class NewExpenseModuleListActivity : NavigationActivity(), View.OnClickListener,
    OnTabSelectedListener, OnPageChangeListener, OnLoadCompleteListener, OnItemSelectedListener {
    // Define the Button Variables
    private var allInvoiceButton: TextView? = null
    private var paidInvoiceButton: TextView? = null
    private var outstandingInvoiceButton: TextView? = null
    private var invoiceView: View? = null
    private var paidView: View? = null
    private val selectedTag: String? = null
    private var menu: Menu? = null
    private var invoiceListView: RecyclerView? = null
    private val expenseAdapter: ExpenselistModulAdapter? = null
    private val invoiceList: ArrayList<InvoiceModel>? = null
    private var pDialog: SweetAlertDialog? = null
    var pageNo = 1
    private var session1: SessionManager? = null
    private var user1: HashMap<String, String>? = null
    private var companyId: String? = null
    var supplierList: ArrayList<SupplierModel>? = ArrayList()

    //This is our tablayout
    private var tabLayout: TabLayout? = null

    //This is our viewPager
    private var viewPager: ViewPager? = null
    private var operationLayout: LinearLayout? = null
    private var emptyLayout: LinearLayout? = null
    private val toolbar1: Toolbar? = null
    private val lettersRecyclerview: RecyclerView? = null
    private var customerView: RecyclerView? = null
    private val adapter: SortAdapter? = null
    private val letters: ArrayList<String>? = null
    private val PartSpinner: SearchableSpinner? = null
    private val PartName: ArrayList<String>? = null
    private val PartId: List<String>? = null
    private val btnCancel: Button? = null
    private val customerName: TextView? = null
    private var customerNameAdapter: SelectCustomerAdapter? = null
    private val dateText: TextView? = null
    private val userName: TextView? = null
    private var companyCode: String? = null
    private val customerId: String? = null
    private var dbHelper: DBHelper? = null
    private var customerList: ArrayList<CustomerModel>? = null
    private var totalCustomers: TextView? = null
    private var cancelSheet: Button? = null
    private val customerDetails: ArrayList<CustomerDetails>? = null
    private var netTotalText: TextView? = null
    private var optionCancel: TextView? = null
    private var transLayout: LinearLayout? = null
    private var searchFilterView: View? = null
    private var fromDate: EditText? = null
    private var toDate: EditText? = null
    private var invoiceStatus: Spinner? = null
    private var supplierSpinner: SearchableSpinner? = null
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private val mHour = 0
    private val mMinute = 0
    private var selectSupplierName: String? = ""
    private var selectSuppliercode: String? = ""
    private var searchButton: Button? = null
    private var cancelSearch: Button? = null
    private var editInvoice: FloatingActionButton? = null
    private var deleteInvoice: FloatingActionButton? = null
    private var cashCollection: FloatingActionButton? = null
    private var printPreview: FloatingActionButton? = null
    private var expensePrint: FloatingActionButton? = null
    private var deliveryOrderPrint: FloatingActionButton? = null
    private var doPrintPreview: FloatingActionButton? = null
    private var cancelInvoice: FloatingActionButton? = null
    private var rootLayout: FrameLayout? = null
    private var mainLayout: LinearLayout? = null
    private var invoiceHeaderDetails: ArrayList<InvoicePrintPreviewModel>? = null
    private var invoicePrintList: ArrayList<InvoiceList>? = null
    var printInvoiceNumber: String? = null
    var noOfCopy: String? = null
    private var printerMacId: String? = null
    private var printerType: String? = null
    private var sharedPreferences: SharedPreferences? = null
    private var progressLayout: View? = null
    var redirectInvoice = false
    private val customerSelectCode = 23

    //    var invoiceTitlelistArray: Array<String>
//    var invoicepricelistArray: Array<String>
//    var invoiceQNTlistArray: Array<String>
//    var invoiceNOlistArray: Array<String>
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
    private var pd: ProgressDialog? = null

    var emptyTextView: TextView? = null
    var isInvoicePrint = true
    private var username: String? = null
    var shareLayout: LinearLayout? = null
    var printLayout: LinearLayout? = null
    var cancelButton: Button? = null
    var shareMode = "Share"
    var createInvoiceSetting = "false"
    private var salesReturnList: ArrayList<InvoicePrintPreviewModel.SalesReturnList>? = null
    private var selectCustomerCode: String? = ""
    private var selectCustomerName: String? = ""
    private val FILTER_CUSTOMER_CODE = 134
    private var selectedUser: String? = ""
    private var usersList: ArrayList<UserListModel>? = null
    var locationCodeL: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val contentFrameLayout = findViewById<FrameLayout>(R.id.content_frame)
        layoutInflater.inflate(R.layout.activity_new_expense_module_list, contentFrameLayout)


        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Expense")

        allInvoiceButton = findViewById(R.id.btn_all_invoice)
        paidInvoiceButton = findViewById(R.id.btn_paid_invoice)
        outstandingInvoiceButton = findViewById(R.id.btn_outstanding_invoice)
        allInvoiceButton!!.setOnClickListener(this)
        paidInvoiceButton!!.setOnClickListener(this)
        outstandingInvoiceButton!!.setOnClickListener(this)

        invoiceView = findViewById(R.id.invoice_view)
        paidView = findViewById(R.id.paid_view)
        invoiceListView = findViewById(R.id.invoiceList)
        session1 = SessionManager(this)
        user1 = session1!!.userDetails
        companyId = user1!!!![SessionManager.KEY_COMPANY_CODE]
        username = user1!![SessionManager.KEY_USER_NAME]
        operationLayout = findViewById(R.id.operation_layout)
        emptyLayout = findViewById(R.id.empty_layout)
        emptyTextView = findViewById(R.id.empty_text)
        netTotalText = findViewById(R.id.net_total_value)
        session1 = SessionManager(this)
        dbHelper = DBHelper(this)
        companyCode = user1!![SessionManager.KEY_COMPANY_CODE]
        locationCodeL = user1!![SessionManager.KEY_LOCATION_CODE]
        customerView = findViewById(R.id.customerList)
        totalCustomers = findViewById(R.id.total_customers)
        cancelSheet = findViewById(R.id.cancel_sheet)
        viewPager = findViewById(R.id.pager)
        searchFilterView = findViewById(R.id.search_filter)
        invoiceStatus = findViewById(R.id.invoice_status)
        supplierSpinner = findViewById(R.id.supplier_name_spinner)
        fromDate = findViewById(R.id.from_date)
        toDate = findViewById(R.id.to_date)
        searchButton = findViewById(R.id.btn_searchExpens)
        transLayout = findViewById(R.id.trans_layout)
        customerLayout = findViewById(R.id.customer_layout)
        invoiceOptionLayout = findViewById(R.id.expense_option)
        optionCancel = findViewById(R.id.option_cancel)
        cancelSheet = findViewById(R.id.cancel_sheet)
        cancelSearch = findViewById(R.id.btn_cancel)
        invoiceCustomerName = findViewById(R.id.invoice_name)
        invoiceNumber = findViewById(R.id.sr_no)
        editInvoice = findViewById(R.id.edit_invoice)
        deleteInvoice = findViewById(R.id.delete_invoice)
        cashCollection = findViewById(R.id.cash_collection)
        printPreview = findViewById(R.id.print_preview_expens)
        editInvoiceLayout = findViewById(R.id.edit_invoice_layout)
        deleteInvoiceLayout = findViewById(R.id.delete_invoice_layout)
        cashCollectionLayout = findViewById(R.id.cash_collection_layout)
        printPreviewLayout = findViewById(R.id.preview_invoice_layout)
        cancelInvoiceLayout = findViewById(R.id.cancel_invoice_layout)
        cancelInvoice = findViewById(R.id.cancel_invoice)
        rootLayout = findViewById(R.id.rootLayout)
        mainLayout = findViewById(R.id.main_layout)
        progressLayout = findViewById(R.id.progress_layout)
        pdfView = findViewById(R.id.pdfView)
        pdfViewLayout = findViewById(R.id.pdf_layout)
        expensePrint = findViewById(R.id.expense_print)
        invoicePrintLayout = findViewById(R.id.invoice_print_layout)
        doPrintLayout = findViewById(R.id.do_print_layout)
        deliveryOrderPrint = findViewById(R.id.do_print)
        doPrintPreview = findViewById(R.id.do_print_preview)

        shareLayout = findViewById(R.id.share_layout)
        printLayout = findViewById(R.id.print_layout)
        cancelButton = findViewById(R.id.cancel)
        company_name = user1!![SessionManager.KEY_COMPANY_NAME]
        company_address1 = user1!![SessionManager.KEY_ADDRESS1]
        company_address2 = user1!![SessionManager.KEY_ADDRESS2]
        company_address3 = user1!![SessionManager.KEY_ADDRESS3]
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE)
        printerType = sharedPreferences!!.getString("printer_type", "")
        printerMacId = sharedPreferences!!.getString("mac_address", "")

        // PrinterUtils printerUtils=new PrinterUtils(this,printerMacId);
        //   printerUtils.connectPrinter();


        pd = ProgressDialog(this@NewExpenseModuleListActivity)
        pd!!.setMessage("Downloading Product Image, please wait ...")
        pd!!.isIndeterminate = true
        pd!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        pd!!.setCancelable(false)
        pd!!.setButton(
            DialogInterface.BUTTON_NEGATIVE, "CANCEL"
        ) { dialog, whichButton -> dialog.dismiss() }
        pd!!.setProgressNumberFormat("%1d KB/%2d KB")
        pdfGenerateDialog = ProgressDialog(this)
        pdfGenerateDialog!!.setCancelable(false)
        pdfGenerateDialog!!.setMessage("Expense Pdf generating please wait...")

        emptyTextView!!.setVisibility(View.VISIBLE)
        dbHelper!!.removeAllItems()
        dbHelper!!.removeAllInvoiceItems()
        AddInvoiceActivityOld.order_no = ""
        Log.w("Printer_Mac_Id:", printerMacId!!)
        Log.w("Printer_Type:", printerType!!)



        try {
            getVendorList()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        AppUtils.setProductsList(null)
        verifyStoragePermissions(this)
        checkPermission()
        val settings = dbHelper!!.settings
        if (settings != null) {
            if (settings.size > 0) {
                for (model in settings) {
                    if (model.settingName == "create_invoice_switch") {
                        Log.w("SettingName:", model.settingName)
                        Log.w("SettingValue:", model.settingValue)
                        createInvoiceSetting = if (model.settingValue == "1") {
                            "true"
                        } else {
                            "false"
                        }
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
        /* if (getIntent() !=null){
            printInvoiceNumber=getIntent().getStringExtra("printInvoiceNumber");
            noOfCopy=getIntent().getStringExtra("noOfCopy");
            if (printInvoiceNumber!=null && !printInvoiceNumber.isEmpty()){
                try {
                    getInvoicePrintDetails(printInvoiceNumber,Integer.parseInt(noOfCopy));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }*/

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
                if (roll.formName == "Edit Invoice") {
                    if (roll.havePermission == "true") {
                        editInvoiceLayout!!.setVisibility(View.GONE)
                    } else {
                        editInvoiceLayout!!.setVisibility(View.GONE)
                    }
                } else if (roll.formName == "Delete Invoice") {
                    if (roll.havePermission == "true") {
                        deleteInvoiceLayout!!.setVisibility(View.GONE)
                    } else {
                        deleteInvoiceLayout!!.setVisibility(View.GONE)
                    }
                }
            }
        }
        behavior!!.addBottomSheetCallback(object : BottomSheetCallback() {
            @SuppressLint("LogNotTimber")
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
                            supportActionBar!!.setTitle("Expense")
                        }
                        transLayout!!.setVisibility(View.VISIBLE)
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED")
                        supportActionBar!!.setTitle("Expense")
                        transLayout!!.setVisibility(View.GONE)
                        if (redirectInvoice) {
                            CustomerFragment.isLoad = true
                            val intent = Intent(
                                this@NewExpenseModuleListActivity,
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
        cancelButton!!.setOnClickListener(View.OnClickListener { viewCloseBottomSheet() })
        cancelSheet!!.setOnClickListener(View.OnClickListener { viewCloseBottomSheet() })
        invoiceStatus!!.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                try {
                    (parent.getChildAt(0) as TextView).setTextColor(Color.BLUE)
                    (parent.getChildAt(0) as TextView).textSize = 12f
                } catch (ed: Exception) {
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        fromDate!!.setOnClickListener(View.OnClickListener { getDate(fromDate) })
        toDate!!.setOnClickListener(View.OnClickListener { getDate(toDate) })

        searchButton!!.setOnClickListener(View.OnClickListener {
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
                // ((ExpenseModuleListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).filterValidation();
                val expenseSearch = ExpenseModuleListFragment()
                try {
                    val oldFromDate = fromDate!!.getText().toString()
                    val oldToDate = toDate!!.getText().toString()
                    val fromDate = SimpleDateFormat("dd/MM/yyyy").parse(oldFromDate)
                    val toDate = SimpleDateFormat("dd/MM/yyyy").parse(oldToDate)
                    // Use SimpleDateFormat#format() to format a Date into a String in a certain pattern.
                    val fromDateString = SimpleDateFormat("yyyyMMdd").format(fromDate)
                    val toDateString = SimpleDateFormat("yyyyMMdd").format(toDate)
                    println("$fromDateString-$toDateString") // 2011-01-18
                    var invoice_status = ""
                    if (invoiceStatus!!.getSelectedItem() == "ALL") {
                        invoice_status = ""
                    } else if (invoiceStatus!!.getSelectedItem() == "PAID") {
                        invoice_status = "C"
                    } else if (invoiceStatus!!.getSelectedItem() == "UNPAID") {
                        invoice_status = "O"
                    }
                    if (selectedUser != null && !selectedUser!!.isEmpty()) {
                        username = selectedUser
                    }
                    expenseSearch.filterSearch(
                        this@NewExpenseModuleListActivity,
                        username,
                        selectSuppliercode,
                        invoice_status,
                        fromDateString,
                        toDateString,
                        locationCodeL
                    )
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                // ExpenseModuleListFragment.filterSearch(customer_name,invoiceStatus.getSelectedItem().toString(),fromDate.getText().toString(),toDate.getText().toString());
                invoiceStatus!!.setSelection(0)
            }
        })
        cancelSearch!!.setOnClickListener(View.OnClickListener {
            isSearchCustomerNameClicked = false
            selectCustomerId = ""
            invoiceStatus!!.setSelection(0)
            fromDate!!.setText(formattedDate)
            toDate!!.setText(formattedDate)
            searchFilterView!!.setVisibility(View.GONE)
            invoiceStatus!!.setSelection(0)
            val invoices = ExpenseModuleListFragment()
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

//        editInvoiceLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    viewCloseBottomSheet();
//                    getInvoiceDetails(invoiceNumberValue);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

//        editInvoice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    viewCloseBottomSheet();
//                    getInvoiceDetails(invoiceNumberValue);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        deleteInvoiceLayout!!.setOnClickListener(View.OnClickListener {
//            showRemoveAlert(
//                invoiceNumberValue
//            )
//        })
        // deleteInvoice!!.setOnClickListener(View.OnClickListener { showRemoveAlert(invoiceNumberValue) })
//        cashCollectionLayout!!.setOnClickListener(View.OnClickListener {
//            viewCloseBottomSheet()
//            val intent =
//                Intent(this@NewExpenseModuleListActivity, CashCollectionActivity::class.java)
//            intent.putExtra("customerCode", invoiceCustomerCodeValue)
//            intent.putExtra("customerName", invoiceCustomerValue)
//            startActivity(intent)
//            finish()
//        })
//        cashCollection!!.setOnClickListener(View.OnClickListener {
//            viewCloseBottomSheet()
//            val intent =
//                Intent(this@NewExpenseModuleListActivity, CashCollectionActivity::class.java)
//            intent.putExtra("customerCode", invoiceCustomerCodeValue)
//            intent.putExtra("customerName", invoiceCustomerValue)
//            startActivity(intent)
//            finish()
//        })
        printPreview!!.setOnClickListener(View.OnClickListener {
            viewCloseBottomSheet()
            if (createInvoiceSetting == "true") {
                val intent = Intent(
                    this@NewExpenseModuleListActivity,
                    NewExpensePrintPreviewActivity::class.java
                )
                intent.putExtra("invoiceNumber", invoiceNumberValue)
                intent.putExtra("outstandingAmount", oustandingAmount)
                startActivity(intent)
            } else {
                val intent = Intent(
                    this@NewExpenseModuleListActivity,
                    NewExpensePrintPreviewActivity::class.java
                )
                intent.putExtra("invoiceNumber", invoiceNumberValue)
                intent.putExtra("outstandingAmount", oustandingAmount)
                startActivity(intent)
            }
        })
//        doPrintPreview!!.setOnClickListener(View.OnClickListener {
//            viewCloseBottomSheet()
//            val intent = Intent(this@NewExpenseModuleListActivity, DOPrintPreview::class.java)
//            intent.putExtra("invoiceNumber", invoiceNumberValue)
//            intent.putExtra("outstandingAmount", oustandingAmount)
//            startActivity(intent)
//        })
//        printPreviewLayout!!.setOnClickListener(View.OnClickListener {
//            viewCloseBottomSheet()
//            if (createInvoiceSetting == "true") {
//                val intent = Intent(
//                    this@NewExpenseModuleListActivity,
//                    NewInvoicePrintPreviewActivity::class.java
//                )
//                intent.putExtra("invoiceNumber", invoiceNumberValue)
//                intent.putExtra("outstandingAmount", oustandingAmount)
//                startActivity(intent)
//            } else {
//                val intent = Intent(
//                    this@NewExpenseModuleListActivity,
//                    InvoicePrintPreviewActivity::class.java
//                )
//                intent.putExtra("invoiceNumber", invoiceNumberValue)
//                intent.putExtra("outstandingAmount", oustandingAmount)
//                startActivity(intent)
//            }
//        })
        printLayout!!.setOnClickListener(View.OnClickListener {
            if (printInvoiceNumber != null && !printInvoiceNumber!!.isEmpty()) {
                try {
                    getExpensePrintDetails(printInvoiceNumber, 1)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
        invoicePrintLayout!!.setOnClickListener(View.OnClickListener {
            try {
                viewCloseBottomSheet()
                isInvoicePrint = true
                getExpensePrintDetails(invoiceNumberValue, 1)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        })
        expensePrint!!.setOnClickListener(View.OnClickListener {
            try {
                viewCloseBottomSheet()
                isInvoicePrint = true
                getExpensePrintDetails(invoiceNumberValue, 1)
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
        loadFragment(ExpenseModuleListFragment())
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


    fun getInvoicePdf(invoiceno: String?, mode: String) {
        try {
            // Initialize a new RequestQueue instance
            val requestQueue = Volley.newRequestQueue(this)
            // Initialize a new JsonArrayRequest instance
            val jsonObject = JSONObject()
            jsonObject.put("InvoiceNo", invoiceno)
            val url = Utils.getBaseUrl(this) + "DownloadPDFAPInvoice"
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
                    } catch (e: java.lang.Exception) {
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
                    val params = java.util.HashMap<String, String>()
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
        } catch (ex: java.lang.Exception) {
        }
    }

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
                val folder = File(Constants.getFolderPath(this@NewExpenseModuleListActivity))
                if (!folder.exists()) {
                    val productsDirectory =
                        File(Constants.getFolderPath(this@NewExpenseModuleListActivity))
                    productsDirectory.mkdirs()
                }

                //create a new file
                val filepath = sUrl[1] + "_" + sUrl[2]
                val newfilePath = filepath.replace("/", "_")
                newFile = File(
                    Constants.getFolderPath(this@NewExpenseModuleListActivity),
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


    fun viewPdfLayout(
        invoiceNumber: String,
        invoiceDetails: java.util.ArrayList<InvoicePrintPreviewModel>?
    ) {
        //pdfGenerateDialog.show();
        if (VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                storagePath = Environment.getExternalStorageDirectory()
                invoiceNO = invoiceNumber
                FILE =
                    Environment.getExternalStorageDirectory().toString() + "/" + invoiceNO + ".pdf"
                fatchinvoiceList(invoiceDetails!!)
                //createInvoicePdfTable(invoiceDetails);
            } else {
                requestPermission() // Code for permission
            }
        } else {
            storagePath = Environment.getExternalStorageDirectory()
            invoiceNO = invoiceNumber
            FILE =
                Environment.getExternalStorageDirectory().toString() + "/" + invoiceNO + ".pdf"
            fatchinvoiceList(invoiceDetails)
            //createInvoicePdfTable(invoiceDetails);
        }
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
            visibleFragment = "Expense"
            invalidateOptionsMenu()
            loadFragment(ExpenseModuleListFragment())
        } else if (view.id == R.id.btn_paid_invoice) {
            allInvoiceButton!!.setBackgroundResource(R.drawable.button_unselect)
            paidInvoiceButton!!.setBackgroundResource(R.drawable.button_order)
            outstandingInvoiceButton!!.setBackgroundResource(R.drawable.button_unselect)
            allInvoiceButton!!.setTextColor(Color.parseColor("#212121"))
            paidInvoiceButton!!.setTextColor(Color.parseColor("#FFFFFF"))
            outstandingInvoiceButton!!.setTextColor(Color.parseColor("#212121"))
            invoiceView!!.visibility = View.GONE
            paidView!!.visibility = View.GONE
            visibleFragment = "paid"
            invalidateOptionsMenu()
            loadFragment(PaidInvoices())
        } else if (view.id == R.id.btn_outstanding_invoice) {
            allInvoiceButton!!.setBackgroundResource(R.drawable.button_unselect)
            paidInvoiceButton!!.setBackgroundResource(R.drawable.button_unselect)
            outstandingInvoiceButton!!.setBackgroundResource(R.drawable.button_order)
            allInvoiceButton!!.setTextColor(Color.parseColor("#212121"))
            paidInvoiceButton!!.setTextColor(Color.parseColor("#212121"))
            outstandingInvoiceButton!!.setTextColor(Color.parseColor("#FFFFFF"))
            invoiceView!!.visibility = View.VISIBLE
            paidView!!.visibility = View.GONE
            visibleFragment = "unpaid"
            invalidateOptionsMenu()
            loadFragment(UnpaidInvoices())
        }
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
        // addInvoice.setVisible(false)

//        if (createInvoiceSetting == "true") {
//            if (company_name == "AADHI INTERNATIONAL PTE LTD") {
//                addInvoice.setVisible(false)
//            } else {
//                addInvoice.setVisible(true)
//            }
//        } else {
//            addInvoice.setVisible(false)
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
        if (visibleFragment == "Expense") {
            filter.setVisible(true)
            //filter.setVisible(false);
        } else {
            filter.setVisible(false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish();
            //  onBackPressed()

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
            expenseAdapter!!.notifyDataSetChanged()
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
            expenseAdapter!!.notifyDataSetChanged()
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
                expenseAdapter!!.notifyDataSetChanged()
            } catch (ex: Exception) {
                Log.w("Error:", ex.message!!)
            }
        } else if (item.itemId == R.id.action_add) {
            startActivity(
                Intent(this, NewExpenseModuleAddActivity::class.java)
            )

//            val intent = Intent(applicationContext, CustomerListActivity::class.java)
//            intent.putExtra("from", "iv")
//            startActivityForResult(intent, customerSelectCode)


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
                isSearchCustomerNameClicked = false
                if (behavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                //slideUp(searchFilterView);
            } else {
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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == customerSelectCode) {
//            if (resultCode == RESULT_OK) {
//                val result = data!!.getStringExtra("customerCode")
//                Utils.setCustomerSession(this, result)
//                val intent =
//                    Intent(this@NewExpenseModuleListActivity, AddInvoiceActivity::class.java)
//                intent.putExtra("customerId", result)
//                intent.putExtra("activityFrom", "Invoice")
//                startActivity(intent)
//                //finish();
//            }
//            if (resultCode == RESULT_CANCELED) {
//                // Write your code if there's no result
//            }
//        } else if (requestCode == FILTER_CUSTOMER_CODE && resultCode == RESULT_OK) {
//            selectCustomerCode = data!!.getStringExtra("customerCode")
//            selectCustomerName = data.getStringExtra("customerName")
//            customerNameText!!.setText(selectCustomerName)
//            Log.w("CustomerCode:", selectCustomerCode!!)
//        }
//    } //onActivityResult

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
            val intent = Intent(this@NewExpenseModuleListActivity, AddInvoiceActivityOld::class.java)
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
        finish()
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
                this@NewExpenseModuleListActivity,
                "Whatsapp have not been installed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun hideKeyboard() {
        try {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            // TODO: handle exception
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

    fun fatchinvoiceList(invoiceDetails: ArrayList<InvoicePrintPreviewModel>?) {
        // Create a document and set it's properties
        val objDocument = Document()
        objDocument.creator = "Powered By : Google.com"
        objDocument.author = "Google"
        objDocument.title = "Google Invoice"
        // Create a page to add to the document
        val objPage = Page(PageSize.LETTER, PageOrientation.PORTRAIT, 20.0f)
        val strText = "INVOICE"
        val invoiceTitle = Label(strText, 0f, 0f, 504f, 100f, Font.getHelvetica(), 20f, TextAlign.CENTER)

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
    Invoice Number: ${invoiceDetails!![0].invoiceNumber}
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
                "SNo" + PdfUtils.snoSpace() + "Description   " + PdfUtils.getDescriptionSpace(
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

    // slide the view from its current position to below itself
    fun getDate(dateEditext: EditText?) {
        // Get Current Date
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            this@NewExpenseModuleListActivity,
            { view, year, monthOfYear, dayOfMonth -> dateEditext!!.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year) },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.show()
    }

    fun loadFragment(fragment: Fragment?) {
        val fm = supportFragmentManager
        val fragmentTransaction = fm.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment!!)
        fragmentTransaction.commit()
    }

    @Throws(JSONException::class)
    fun getExpensePrintDetails(invoiceNumber: String?, copy: Int) {
        // Initialize a new RequestQueue instance
        val jsonObject = JSONObject()
        // jsonObject.put("CompanyCode", companyId);
        jsonObject.put("InvoiceNo", invoiceNumber)
        jsonObject.put("LocationCode", locationCodeL)
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "APInvoiceDetails"
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_urlExp:", url)
        // pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        // pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        // pDialog.setTitleText("Generating Print Preview...");
        // pDialog.setCancelable(false);
        //  pDialog.show();
        invoiceHeaderDetails = ArrayList()
        invoicePrintList = ArrayList()
        salesReturnList = ArrayList()

        val jsonObjectRequest: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, jsonObject,
                Response.Listener { response: JSONObject ->
                    try {
                        Log.w("ResExpens::", response.toString())
                        val statusCode = response.optString("statusCode")
                        if (statusCode == "1") {
                            val responseData = response.getJSONArray("responseData")
                            val `object` = responseData.optJSONObject(0)
                            val model = InvoicePrintPreviewModel()
                            model.invoiceNumber = `object`.optString("poNo")
                            model.invoiceDate = `object`.optString("poDate")
                            model.customerCode = `object`.optString("vendorCode")
                            model.customerName = `object`.optString("vendorName")
                            model.overAllTotal = `object`.optString("overAllTotal")
                            // model.setAddress(object.optString("street"));
                            model.address =
                                `object`.optString("address1") + `object`.optString("address2") + `object`.optString(
                                    "address3"
                                )
                            model.address2 = `object`.optString("address2")
                            model.address3 = `object`.optString("address3")
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
                            val signFlag = `object`.optString("signFlag")
                            if (signFlag == "Y") {
                                val signature = `object`.optString("signature")
                                Utils.setSignature(signature)
                                createSignature()
                            } else {
                                Utils.setSignature("")
                            }
                            val detailsArray = `object`.optJSONArray("purchaseInvoiceDetails")
                            for (i in 0 until detailsArray.length()) {
                                val detailObject = detailsArray.optJSONObject(i)
                                val invoiceListModel = InvoiceList()
                                invoiceListModel.productCode = detailObject.optString("productCode")
                                invoiceListModel.description = detailObject.optString("productName")
                                invoiceListModel.lqty = detailObject.optString("unitQty")
                                invoiceListModel.cqty = detailObject.optString("cartonQty")
                                invoiceListModel.netQty = detailObject.optString("quantity")
                                invoiceListModel.netQuantity = detailObject.optString("netQuantity")
                                invoiceListModel.focQty = detailObject.optString("foc_Qty")
                                invoiceListModel.returnQty = detailObject.optString("returnQty")
                                invoiceListModel.cartonPrice = detailObject.optString("cartonPrice")
                                invoiceListModel.accountName = detailObject.optString("accountName")

                                invoiceListModel.unitPrice = detailObject.optString("price")
                                val qty1 = detailObject.optString("quantity").toDouble()
                                val price1 = detailObject.optString("price").toDouble()
                                val nettotal1 = qty1 * price1
                                invoiceListModel.total = detailObject.optString("lineTotal")
                                invoiceListModel.pricevalue = price1.toString()
                                invoiceListModel.uomCode = detailObject.optString("uomCode")
                                invoiceListModel.pcsperCarton =
                                    detailObject.optString("pcsPerCarton")
                                invoiceListModel.itemtax = detailObject.optString("totalTax")
                                invoiceListModel.subTotal = detailObject.optString("subTotal")
                                invoicePrintList!!.add(invoiceListModel)
                                model.invoiceList = invoicePrintList
                                invoiceHeaderDetails!!.add(model)
                            }
//                            val SRArray = `object`.optJSONArray("sR_Details")!!
//                            if (SRArray.length() > 0) {
//                                val SRoblect = SRArray.optJSONObject(0)
//                                val salesReturnModel = InvoicePrintPreviewModel.SalesReturnList()
//                                salesReturnModel.salesReturnNumber =
//                                    SRoblect.optString("salesReturnNumber")
//                                salesReturnModel.setsRSubTotal(SRoblect.optString("sR_SubTotal"))
//                                salesReturnModel.setsRTaxTotal(SRoblect.optString("sR_TaxTotal"))
//                                salesReturnModel.setsRNetTotal(SRoblect.optString("sR_NetTotal"))
//                                salesReturnList!!.add(salesReturnModel)
//                            }
//                            model.salesReturnList = salesReturnList
                            invoiceHeaderDetails!!.add(model)
                            printExpense(copy)
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


    @Throws(JSONException::class)
    private fun getVendorList() {
        // CommonMethods.showProgressDialog(this)
        val jsonObject = JSONObject()
        //  jsonObject.put("WhsCode", whsCode)

        val requestQueue = Volley.newRequestQueue(this)

        val url = Utils.getBaseUrl(this) + "vendorList"
        Log.w("url_vendorlist:", url)

        supplierList = ArrayList()

        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            Response.Listener { response: JSONObject ->
                try {
                    GlobalScope.launch {

                        Log.w("url_vendorlist_res:", response.toString())
                        val statusCode = response.optString("statusCode")
                        val statusMsg = response.optString("statusMessage")

                        if (statusCode == "1") {
                            val responseData = response.optJSONArray("responseData")!!

                            if (responseData!!.length() > 0) {
                                for (i in 0 until responseData.length()) {
                                    val obj = responseData.optJSONObject(i)

                                    val model = SupplierModel(
                                        obj.optString("vendorCode"),
                                        obj.optString("vendorName"),
                                        obj.optString("currencyCode"),
                                        obj.optString("currencyName"),
                                        obj.optString("taxType"),
                                        obj.optString("taxCode"),
                                        obj.optString("taxName"),
                                        obj.optString("taxPercentage")
                                    )

                                    supplierList!!.add(model)
                                }

                                withContext(Dispatchers.Main) {
                                    if (supplierList!!.size > 0) {
                                        setSupplierSpinner(supplierList!!)
                                    }
                                }
                            }
                        }
                        //  CommonMethods.cancelProgressDialog()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error: VolleyError ->
                // Do something when error occurred
                //  CommonMethods.cancelProgressDialog()
                Log.w("Error_throwing:", error.toString())
            }) {
            override fun getHeaders(): Map<String, String> {
                val params = java.util.HashMap<String, String>()
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
        jsonObjectRequest.retryPolicy = object : RetryPolicy {
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
        requestQueue.add(jsonObjectRequest)
    }


    fun setSupplierSpinner(spinnerlist: ArrayList<SupplierModel>) {
        val spinnerlst = SupplierModel(
            customerName = "Select Supplier",
            customerCode = "", currencyCode = "", currencyName = "",
            taxCode = "", taxName = "", taxPercentage = "", taxType = ""
        )
        spinnerlist.add(0, spinnerlst)

        Log.w("spinnnVal", "" + spinnerlist)
        val adapter = ArrayAdapter<String>(this, R.layout.cust_spinner_item)
        for (i in spinnerlist.indices) {
            adapter.add(spinnerlist[i].customerName)
        }
        supplierSpinner!!.adapter = adapter
        supplierSpinner!!.setTitle("")
        supplierSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View, position: Int, id: Long) {
                // On selecting a spinner item
                Log.w("spinnnVal1", "" + spinnerlist[position].customerName)
                selectSuppliercode = spinnerlist[position].customerCode
                selectSupplierName = spinnerlist[position].customerName
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
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
    @Throws(JSONException::class)
    fun validatePrinterConfiguration(): Boolean {
        var printetCheck = false
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(
                this@NewExpenseModuleListActivity,
                "This device does not support bluetooth",
                Toast.LENGTH_SHORT
            ).show()
        } else if (!mBluetoothAdapter.isEnabled) {
            // Bluetooth is not enabled :)
            Toast.makeText(
                this@NewExpenseModuleListActivity,
                "Enable bluetooth and connect the printer",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Bluetooth is enabled
            if (!printerType!!.isEmpty() && !printerMacId!!.isEmpty()) {
                printetCheck = true
            } else {
                Toast.makeText(this@NewExpenseModuleListActivity, "Please configure Printer", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return printetCheck
    }


    fun printExpense(copy: Int) {
        if (validatePrinterConfiguration()) {
            // Bluetooth is enabled
            if (printerType.equals("TSC Printer", ignoreCase = true)) {
                val printer = TSCPrinter(this@NewExpenseModuleListActivity, printerMacId, "Expense")
                printer.printExpense(copy, invoiceHeaderDetails, invoicePrintList)
                printer.setOnCompletionListener {
                    Utils.setSignature("")
                    Toast.makeText(
                        applicationContext,
                        "Expense printed successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                // ZebraPrinterActivity zebraPrinterActivity=new ZebraPrinterActivity(NewInvoiceListActivity.this,printerMacId);
                // zebraPrinterActivity.printInvoice(copy,invoiceHeaderDetails,invoicePrintList,"false");
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
        var invoicePrintLayout: LinearLayout? = null
        var doPrintLayout: LinearLayout? = null
        private var cancelInvoiceLayout: LinearLayout? = null
        private var visibleFragment = "Expense"
        var selectCustomerId = ""
        var REQUEST_PERMISSIONS = 154
        private var FILE: String? = null
        private const val PERMISSION_REQUEST_CODE = 100
        var pdfViewLayout: View? = null

        // Storage Permissions
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
            permission.READ_EXTERNAL_STORAGE,
            permission.WRITE_EXTERNAL_STORAGE,
            permission.MANAGE_EXTERNAL_STORAGE
        )

        @JvmStatic
        fun showInvoiceOption(
            invoiceCustomerCode: String?,
            customerName: String?,
            invoiceNum: String?,
            invoiceStatus: String,
            date: String?
        ) {
            Log.w("InvoiceStatusView:", invoiceStatus)
            isSearchCustomerNameClicked = false
            // searchFilterView.setVisibility(View.GONE);
            if (invoiceStatus == "Paid" || visibleFragment == "paid") {
                //   editInvoiceLayout.setVisibility(View.GONE);
                cashCollectionLayout!!.visibility = View.GONE
                deleteInvoiceLayout!!.visibility = View.GONE
                invoiceStatusValue = "P"
            } else if (invoiceStatus == "Partial") {
                //   editInvoiceLayout.setVisibility(View.GONE);
                cashCollectionLayout!!.visibility = View.GONE
                deleteInvoiceLayout!!.visibility = View.GONE
                invoiceStatusValue = "PR"
            } else if (invoiceStatus == "Open") {
                // editInvoiceLayout.setVisibility(View.VISIBLE);
                cashCollectionLayout!!.visibility = View.GONE
                deleteInvoiceLayout!!.visibility = View.GONE
                invoiceStatusValue = "O"
                // deleteInvoiceLayout.setVisibility(View.VISIBLE);
            }
            invoiceCustomerCodeValue = invoiceCustomerCode
            invoiceCustomerValue = customerName
            invoiceNumberValue = invoiceNum
            invoiceDate = date
            customerLayout!!.visibility = View.GONE
            invoiceOptionLayout!!.visibility =
                View.VISIBLE
            invoiceNumber!!.text = invoiceNum
            invoiceCustomerName!!.text = invoiceCustomerValue
            viewCloseBottomSheet()
        }

        fun viewCloseBottomSheet() {
            //  hideKeyboard();
            pdfViewLayout!!.visibility = View.GONE
            if (isSearchCustomerNameClicked) {
                customerLayout!!.visibility = View.VISIBLE
                invoiceOptionLayout!!.visibility = View.GONE
            } else {
                customerLayout!!.visibility = View.GONE
                invoiceOptionLayout!!.visibility =
                    View.VISIBLE
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

