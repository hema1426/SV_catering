package com.winapp.svcatering.salesreturn

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.winapp.svcatering.BuildConfig
import com.winapp.svcatering.R
import com.winapp.svcatering.activity.NewInvoiceListActivity
import com.winapp.svcatering.activity.SalesOrderListActivity
import com.winapp.svcatering.adapter.NewSalesReturnProductAdapter
import com.winapp.svcatering.adapter.SelectProductAdapter
import com.winapp.svcatering.db.DBHelper
import com.winapp.svcatering.model.AppUtils
import com.winapp.svcatering.model.CreateInvoiceModel
import com.winapp.svcatering.model.CustomerDetails
import com.winapp.svcatering.model.CustomerModel
import com.winapp.svcatering.model.HomePageModel
import com.winapp.svcatering.model.InvoicePrintPreviewModel
import com.winapp.svcatering.model.ItemGroupList
import com.winapp.svcatering.model.ProductSummaryModel
import com.winapp.svcatering.model.ProductsModel
import com.winapp.svcatering.model.SalesOrderPrintPreviewModel
import com.winapp.svcatering.model.SalesOrderPrintPreviewModel.SalesList
import com.winapp.svcatering.model.UomModel
import com.winapp.svcatering.thermalprinter.PrinterUtils
import com.winapp.svcatering.utils.BarCodeScanner
import com.winapp.svcatering.utils.CaptureSignatureView
import com.winapp.svcatering.utils.Constants
import com.winapp.svcatering.utils.FileCompressor
import com.winapp.svcatering.utils.ImageUtil
import com.winapp.svcatering.utils.SessionManager
import com.winapp.svcatering.utils.SettingUtils
import com.winapp.svcatering.utils.SharedPreferenceUtil
import com.winapp.svcatering.utils.Utils
import com.winapp.svcatering.zebraprinter.TSCPrinter
import com.winapp.svcatering.zebraprinter.ZebraPrinterActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects

class NewSalesReturnProductAddActivity : AppCompatActivity() {
    var returnLayout: LinearLayout? = null
    var showHideButton: ImageView? = null
    private var productSummaryAdapter: NewSalesReturnProductAdapter? = null
    private val productSummaryList: ArrayList<ProductSummaryModel>? = null
    var productSummaryView: RecyclerView? = null
    var companyCode: String? = null
    var username: String? = null
    var user: HashMap<String, String>? = null
    var session: SessionManager? = null
    var pDialog: SweetAlertDialog? = null
    private var productListView: RecyclerView? = null
    private var selectProductAdapter: SelectProductAdapter? = null
    var btnCancel: Button? = null
    var searchProduct: ImageView? = null
    var productNameEditext: EditText? = null
    var transLayout: LinearLayout? = null
    var totalProducts: TextView? = null
    var products: ArrayList<String>? = null
    var productAutoComplete: AutoCompleteTextView? = null
    var autoCompleteAdapter: ArrayAdapter<String>? = null
    var cartonPrice: EditText? = null
    var loosePrice: EditText? = null
    var uomText: EditText? = null
    private var uomName = ""
    var pcsPerCarton: EditText? = null
    var stockCount: EditText? = null
    var qtyValue: EditText? = null
    var returnQtyText: EditText? = null
    var customerNameText: EditText? = null
    var priceText: EditText? = null
    var subTotalValue: TextView? = null
    var taxValueText: TextView? = null
    var netTotalValue: TextView? = null
    var customerDetails: ArrayList<CustomerDetails>? = null
    private var dbHelper: DBHelper? = null
    private var sharedPreferenceUtil: SharedPreferenceUtil? = null
    private var settingUOMReturnval: String? = "PCS"
    var taxTitle: TextView? = null
    var addProduct: Button? = null
    var productsModel: ProductsModel? = null
    var itemCount: TextView? = null
    var noproductText: TextView? = null
    private var isEditItem = false
    private var productEditId: String? = null
    private var pdtStockVal: String? = "0.00"
    var productId: String? = null
    var editTimeStamp: String? = null
    var productName: String? = null
    var uomChangel: TextView? = null
    var ed_uomTxtl: TextView? = null
    var uomTxtTitl: TextView? = null
    var isCartonQtyEdit = false
    var isQtyEdit = false
    var ischangeUOM = false
    var isUomSetting = true
    private var uomList: ArrayList<UomModel>? = null
    private var uomListEdit: ArrayList<UomModel>? = null
    var qtyTextWatcher: TextWatcher? = null
    var cartonTextWatcher: TextWatcher? = null
    var lqtyTextWatcher: TextWatcher? = null
    var cartonPriceTextWatcher: TextWatcher? = null
    var loosePriceTextWatcher: TextWatcher? = null
    var focSwitch: Switch? = null
    var exchangeSwitch: Switch? = null
    var returnSwitch: Switch? = null
    var focEditText: EditText? = null
    var exchangeEditext: EditText? = null
    var discountEditext: EditText? = null
    var returnEditext: EditText? = null
    var scanProduct: ImageView? = null
    var RESULT_CODE = 12
    var scannedBarcode: String? = ""

    var locationCode: String? = null
    var stockQtyValue: TextView? = null
    var stockLayout: LinearLayout? = null
    private val cqtyTW: TextWatcher? = null
    private val lqtyTW: TextWatcher? = null
    private var qtyTW: TextWatcher? = null
    var beforeLooseQty: String? = null
    var ss_Cqty: String? = null
    var isAllowLowStock = false
    var focLayout: LinearLayout? = null
    var sortButton: FloatingActionButton? = null
    var emptyLayout: LinearLayout? = null
    var productLayout: LinearLayout? = null
    private val isViewShown = false
    var selectCustomerId: String? = null
    var isReverseCalculationEnabled = true
    var isCartonPriceEdit = true
    var isPriceEdit = true
    var salesReturn: CheckBox? = null
    var salesReturnText: TextView? = null
    var minimumSellingPriceText: TextView? = null
    private val cancelSheet: ImageView? = null
    private var cancelButton: Button? = null
    private var okButton: Button? = null
    private var alert: AlertDialog? = null
    var invoicePrintCheck: CheckBox? = null
    private var signatureAlert: AlertDialog? = null
    var signatureCapture: ImageView? = null
    var companyName: String? = null
    private var printerMacId: String? = null
    private var printerType: String? = null
    private var sharedPreferences: SharedPreferences? = null
    private var remarkText: EditText? = null
    private var activityFrom: String? = ""
    var saveTitle: TextView? = null
    var selectImagel: TextView? = null
    private var saveMessage: TextView? = null
    private var editSoNumber: String? = ""
    var returnAdj: TextView? = null
    var returnLayoutView: LinearLayout? = null
    var cancelReturn: Button? = null
    var saveReturn: Button? = null
    var netReturnQty: TextView? = null
    var expiryReturnQty: EditText? = null
    var damageReturnQty: EditText? = null
    var expiryQtyTextWatcher: TextWatcher? = null
    var damageQtyTextWatcher: TextWatcher? = null
    var invoiceDate: TextView? = null
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private val mHour = 0
    private val mMinute = 0
    var itemGroup: ArrayList<ItemGroupList>? = null
    var groupspinner: Spinner? = null
    val REQUEST_TAKE_PHOTO = 1
    val REQUEST_GALLERY_PHOTO = 2
    var mPhotoFile: File? = null
    var mCompressor: FileCompressor? = null
    var currentSaveDateTime: String? = ""
    private var invoiceHeaderDetails: ArrayList<InvoicePrintPreviewModel>? = null
    private var invoicePrintList: ArrayList<InvoicePrintPreviewModel.InvoiceList>? = null
    private var salesReturnList: ArrayList<InvoicePrintPreviewModel.SalesReturnList>? = null
    private var salesOrderHeaderDetails: ArrayList<SalesOrderPrintPreviewModel>? = null
    private var salesPrintList: ArrayList<SalesList>? = null
    private var uomSpinnerLayl: LinearLayout? = null
    private var uomSpinner: Spinner? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_sales_return_product_add)
        supportActionBar!!.title = "Sales Return"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        signatureString = ""
        Log.w("activity_cg", javaClass.simpleName.toString())
        session = SessionManager(this)
        imageString = ""
        user = session!!.userDetails
        dbHelper = DBHelper(this)
        progressDialog = ProgressDialog(this)
        mCompressor = FileCompressor(this)
        sharedPreferenceUtil = SharedPreferenceUtil(this)
        uomSpinnerLayl = findViewById(R.id.uomSpinnerLay)
        uomSpinner = findViewById(R.id.uom_spinner)
        uomChangel = findViewById(R.id.uomChange)
        ed_uomTxtl = findViewById(R.id.ed_uomTxt)
        uomTxtTitl = findViewById(R.id.uomTxtTitle)

        settingUOMReturnval = sharedPreferenceUtil!!.getStringPreference(sharedPreferenceUtil!!.KEY_SETTING_RETURN_UOM,
            "")
        Log.w("settingUOMreturn..",""+settingUOMReturnval)

        companyCode = user!!.get(SessionManager.KEY_COMPANY_CODE)
        companyName = user!!.get(SessionManager.KEY_COMPANY_NAME)
        username = user!!.get(SessionManager.KEY_USER_NAME)
        locationCode = user!!.get(SessionManager.KEY_LOCATION_CODE)
        returnLayout = findViewById(R.id.return_layout)
        showHideButton = findViewById(R.id.show_hide)
        productSummaryView = findViewById(R.id.product_summary)
        productListView = findViewById(R.id.productList)
        btnCancel = findViewById(R.id.cancel_sheet)
        searchProduct = findViewById(R.id.search_product)
        productNameEditext = findViewById(R.id.product_search)
        transLayout = findViewById(R.id.trans_layout)
        totalProducts = findViewById(R.id.total_products)
        productAutoComplete = findViewById(R.id.product_name)
        groupspinner = findViewById(R.id.spinner_group)
        cartonPrice = findViewById(R.id.carton_price)
        priceText = findViewById(R.id.price)

        loosePrice = findViewById(R.id.loose_price)
        pcsPerCarton = findViewById(R.id.pcs_per_ctn)
        uomText = findViewById(R.id.uom)
        stockCount = findViewById(R.id.stock_count)
        qtyValue = findViewById(R.id.qty)
        subTotalValue = findViewById(R.id.balance_value)
        taxValueText = findViewById(R.id.tax)
        netTotalValue = findViewById(R.id.net_total)
        taxTitle = findViewById(R.id.tax_title)
        addProduct = findViewById(R.id.add_product)
        itemCount = findViewById(R.id.item_count)
        noproductText = findViewById(R.id.no_product_text)
        focSwitch = findViewById(R.id.foc_switch)
        exchangeSwitch = findViewById(R.id.exchange_switch)
        returnSwitch = findViewById(R.id.return_switch)
        focEditText = findViewById(R.id.foc)
        exchangeEditext = findViewById(R.id.exchange_text)
        returnEditext = findViewById(R.id.return_text)
        discountEditext = findViewById(R.id.discount_text)
        scanProduct = findViewById(R.id.scan_product)
        stockQtyValue = findViewById(R.id.stock_qty)
        stockLayout = findViewById(R.id.stock_layout)
        focLayout = findViewById(R.id.foc_layout)
        emptyLayout = findViewById(R.id.empty_layout)
        sortButton = findViewById(R.id.fab)
        productLayout = findViewById(R.id.product_layout)
        salesReturn = findViewById(R.id.sales_return)
        salesReturnText = findViewById(R.id.sales_return_text)
        minimumSellingPriceText = findViewById(R.id.minimum_selling_price)
        barcodeText = findViewById(R.id.barcode_text)
        returnQtyText = findViewById(R.id.return_qty)
        customerNameText = findViewById(R.id.customer_name_text)
        remarkText = findViewById(R.id.remarks)
        returnAdj = findViewById(R.id.return_adj)
        returnLayoutView = findViewById(R.id.return_layout_view)
        cancelReturn = findViewById(R.id.cancel_return)
        saveReturn = findViewById(R.id.save_return)
        netReturnQty = findViewById(R.id.net_return_qty)
        expiryReturnQty = findViewById(R.id.saleable_return_qty)
        damageReturnQty = findViewById(R.id.damage_qty)
        invoiceDate = findViewById(R.id.invoice_date)
        products = ArrayList()
        productAutoComplete!!.clearFocus()
        barcodeText!!.requestFocus()
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE)
        printerType = sharedPreferences!!.getString("printer_type", "")
        printerMacId = sharedPreferences!!.getString("mac_address", "")
        productAutoComplete!!.setEnabled(false)

        //  PrinterUtils printerUtils=new PrinterUtils(this,printerMacId);
        //  printerUtils.connectPrinter();
        focEditText!!.setEnabled(false)
        exchangeEditext!!.setEnabled(false)
        discountEditext!!.setEnabled(false)
        returnEditext!!.setEnabled(false)
        qtyValue!!.setEnabled(false)
        val c = Calendar.getInstance().time
        println("Current time => $c")
        val df1 = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        currentDate = df1.format(c)
        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = df.format(c)
        invoiceDate!!.setText(formattedDate)
        if (intent != null) {
            customerNameText!!.setText(intent.getStringExtra("customerName"))
            customerCode = intent.getStringExtra("customerCode")
            activityFrom = intent.getStringExtra("from")
            editSoNumber = intent.getStringExtra("editSoNumber")
            currentSaveDateTime = intent.getStringExtra("currentDateTime")
            Log.w("GivenActivityFrom::", activityFrom.toString())
            supportActionBar!!.setTitle("Sales Return")
        }
        val jsonObject = JSONObject()
        try {
            jsonObject.put("User", username)
            jsonObject.put("CardCode", customerCode)
//            jsonObject.put("ItemGroupCode", "All")
            jsonObject.put("LocationCode", locationCode)

            getAllProducts(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        try {
            grouplist
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val sharedPreferences = getSharedPreferences("customerPref", MODE_PRIVATE)
        selectCustomerId = sharedPreferences.getString("customerId", "")
        if (selectCustomerId != null && !selectCustomerId!!.isEmpty()) {
            customerDetails = dbHelper!!.getCustomer(selectCustomerId)
            getCustomerDetails(selectCustomerId, false, "")
        }

//        val settings = dbHelper!!.settings
//        if (settings != null) {
//            if (settings.size > 0) {
//                for (model in settings) {
//                    if (model.settingName == "UomSwitch") {
//                        Log.w("SettingNameRet:", model.settingName)
//                        Log.w("SettingValueRet:", model.settingValue)
//                        isUomSetting = if (model.settingValue == "1") {
//                            true
//                        } else {
//                            true
//                        }
//                    }
//                }
//            }
//        }

        val bottomSheet = findViewById<View>(R.id.design_bottom_sheet)
        bottomSheet.setBackgroundColor(Color.parseColor("#F2F2F2"))
        behavior = BottomSheetBehavior.from(bottomSheet)
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
                        transLayout!!.setVisibility(View.VISIBLE)
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED")
                        transLayout!!.setVisibility(View.GONE)
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
        btnCancel!!.setOnClickListener(View.OnClickListener { viewCloseBottomSheet() })
        searchProduct!!.setOnClickListener(View.OnClickListener { viewCloseBottomSheet() })
        productNameEditext!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val product = editable.toString()
                if (!product.isEmpty()) {
                    filter(product)
                } else {
                    setAdapter(AppUtils.getProductsList())
                }
            }
        })
        returnQtyText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                setCalculationView()
                if (!editable.toString().isEmpty()) {
                    expiryReturnQty!!.setText(editable.toString())
                } else {
                    expiryReturnQty!!.setText("")
                }
            }
        })
        expiryQtyTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!qtyValue!!.getText().toString().isEmpty()) {
                    val netreturnqty = qtyValue!!.getText().toString()
                    val net_qty = netreturnqty.toInt()
                    if (!s.toString().isEmpty()) {
                        val damageqty = s.toString().toInt()
                        if (net_qty > damageqty) {
                            val value = net_qty - damageqty
                            damageReturnQty!!.removeTextChangedListener(damageQtyTextWatcher)
                            damageReturnQty!!.setText(value.toString())
                            damageReturnQty!!.addTextChangedListener(damageQtyTextWatcher)
                        } else if (net_qty == damageqty) {
                            damageReturnQty!!.removeTextChangedListener(damageQtyTextWatcher)
                            damageReturnQty!!.setText("0")
                            damageReturnQty!!.addTextChangedListener(damageQtyTextWatcher)
                        } else if (net_qty < damageqty) {
                            expiryReturnQty!!.setText("0")
                            damageReturnQty!!.removeTextChangedListener(damageQtyTextWatcher)
                            damageReturnQty!!.setText(netreturnqty)
                            damageReturnQty!!.addTextChangedListener(damageQtyTextWatcher)
                            Toast.makeText(
                                applicationContext,
                                "Return Qty Exceed...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        damageReturnQty!!.removeTextChangedListener(damageQtyTextWatcher)
                        damageReturnQty!!.setText(netreturnqty)
                        damageReturnQty!!.addTextChangedListener(damageQtyTextWatcher)
                    }
                }
            }
        }
        expiryReturnQty!!.addTextChangedListener(expiryQtyTextWatcher)

        uomChangel!!.setOnClickListener(){
                ischangeUOM = true
                uomChangel!!.visibility = View.VISIBLE
                ed_uomTxtl!!.visibility = View.GONE
                uomSpinnerLayl!!.visibility = View.VISIBLE

                val jsonObject = JSONObject()
                if(isEditItem){
                    try {
                        Log.w("pdtediyyy",""+productEditId);
                        jsonObject.put("CustomerCode", selectCustomerId)
                        jsonObject.put("ItemCode",productEditId)
                        getUOM(jsonObject)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.w("Errort:", Objects.requireNonNull(e.message!!))
                    }
                }else{
                    try {
                        jsonObject.put("CustomerCode", selectCustomerId)
                        jsonObject.put("ItemCode",productId)
                        getUOM(jsonObject)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.w("Errort:", Objects.requireNonNull(e.message!!))
                    }
                }
//            else{
//                Toast.makeText(this, "UOM settings not enabled", Toast.LENGTH_LONG).show()
//
//            }
        }

        /*  expiryReturnQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!returnQtyText.getText().toString().isEmpty()){
                    String netreturnqty=returnQtyText.getText().toString();
                    int net_qty=Integer.parseInt(netreturnqty);
                    if(!s.toString().isEmpty()){
                        int damageqty=Integer.parseInt(s.toString());
                        if (net_qty > damageqty){
                            int value=net_qty-damageqty;
                            damageReturnQty.setText(String.valueOf(value));
                        }else if (net_qty==damageqty){
                            damageReturnQty.setText("0");
                        }else if (net_qty < damageqty){
                            expiryReturnQty.setText("");
                            damageReturnQty.setText(netreturnqty);
                            Toast.makeText(getApplicationContext(),"Return Qty Exceed...",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        damageReturnQty.setText(netreturnqty);
                    }
                }
            }
        });*/damageQtyTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!qtyValue!!.getText().toString().isEmpty()) {
                    val netreturnqty = qtyValue!!.getText().toString()
                    val net_qty = netreturnqty.toInt()
                    if (!s.toString().isEmpty()) {
                        val damageqty = s.toString().toInt()
                        if (net_qty > damageqty) {
                            val value = net_qty - damageqty
                            expiryReturnQty!!.removeTextChangedListener(expiryQtyTextWatcher)
                            expiryReturnQty!!.setText(value.toString())
                            expiryReturnQty!!.addTextChangedListener(expiryQtyTextWatcher)
                        } else if (net_qty == damageqty) {
                            expiryReturnQty!!.removeTextChangedListener(expiryQtyTextWatcher)
                            expiryReturnQty!!.setText("0")
                            expiryReturnQty!!.addTextChangedListener(expiryQtyTextWatcher)
                        } else if (net_qty < damageqty) {
                            damageReturnQty!!.setText("0")
                            expiryReturnQty!!.removeTextChangedListener(expiryQtyTextWatcher)
                            expiryReturnQty!!.setText(netreturnqty)
                            expiryReturnQty!!.addTextChangedListener(expiryQtyTextWatcher)
                            Toast.makeText(
                                applicationContext,
                                "Return Qty Exceed...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        expiryReturnQty!!.removeTextChangedListener(expiryQtyTextWatcher)
                        expiryReturnQty!!.setText(netreturnqty)
                        expiryReturnQty!!.addTextChangedListener(expiryQtyTextWatcher)
                    }
                }
            }
        }
        damageReturnQty!!.addTextChangedListener(damageQtyTextWatcher)

        /*        damageReturnQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!returnQtyText.getText().toString().isEmpty()){
                    String netreturnqty=returnQtyText.getText().toString();
                    int net_qty=Integer.parseInt(netreturnqty);
                    if(!s.toString().isEmpty()){
                        int damageqty=Integer.parseInt(s.toString());
                        if (net_qty > damageqty){
                            int value=net_qty-damageqty;
                            expiryReturnQty.setText(String.valueOf(value));
                        }else if (net_qty==damageqty){
                            expiryReturnQty.setText("0");
                        }else if (net_qty < damageqty){
                            damageReturnQty.setText("");
                            expiryReturnQty.setText(netreturnqty);
                            Toast.makeText(getApplicationContext(),"Return Qty Exceed...",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        expiryReturnQty.setText(netreturnqty);
                    }
                }
            }
        });*/focEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                //setCalculation();
                setCalculationView()
                //setButtonView();
            }
        })
        loosePriceTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                setCalculationView()
            }
        }
        priceText!!.addTextChangedListener(loosePriceTextWatcher)
        qtyTW = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val qty = qtyValue!!.getText().toString()
                var stock = 0.0
                lowStockSetting
                if (!qty.matches("".toRegex())) {
                    if (!stockQtyValue!!.getText().toString().isEmpty()) {
                        stock = stockQtyValue!!.getText().toString().toDouble()
                    }
                    setCalculationView()
                    if (stock < qty.toDouble()) {
                        // Toast.makeText(getActivity(),"Stock is Low please check",Toast.LENGTH_SHORT).show();
                        //  qtyValue.setText("");
                    } else {
                        setCalculationView()
                    }
                }
                val length = qtyValue!!.getText().length
                if (length == 0) {
                    if (qtyValue!!.getText().toString().matches("0".toRegex())) {
                    } else {
                        setCalculationView()
                    }
                    setCalculationView()
                }
                /*   if (activityFrom.equals("iv")){
                    if (!isAllowLowStock){
                        checkLowStock();
                    }
                }*/if (!s.toString().isEmpty()) {
                    expiryReturnQty!!.setText(s.toString())
                } else {
                    expiryReturnQty!!.setText("")
                }
            }
        }
        qtyValue!!.addTextChangedListener(qtyTW)
        productAutoComplete!!.setOnItemClickListener(OnItemClickListener { parent, view, position, rowId ->
            val selectProduct = parent.getItemAtPosition(position) as String
            val product =
                selectProduct.split("~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            productId = product[1].trim { it <= ' ' }
            productName = product[0].trim { it <= ' ' }
            qtyValue!!.requestFocus()
            Log.w("Selected_product", product[1])
            setProductResult(product[1].trim { it <= ' ' })
            /*  try {
                        getProductPrice(productId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
        })
        addProduct!!.setOnClickListener(View.OnClickListener {
            val s = productAutoComplete!!.getText().toString()
            if (!qtyValue!!.getText().toString().isEmpty() && !s.isEmpty() && qtyValue!!.getText()
                    .toString() != "0" && qtyValue!!.getText().toString() != "00" &&
                qtyValue!!.getText().toString() != "000" && qtyValue!!.getText()
                    .toString() != "0000" && netTotalValue!!.getText().toString() != "0.00"
            ) {
                if (addProduct!!.getText().toString() == "Update") {
                    // if (!cartonPrice.getText().toString().isEmpty() && !cartonPrice.getText().toString().equals("0.00") && !cartonPrice.getText().toString().equals("0.0") && !cartonPrice.getText().toString().equals("0")){
                    if (priceText!!.getText() != null && !priceText!!.getText().toString().isEmpty()) {
                        if (priceText!!.getText().toString().toDouble() > 0) {
                            val minimumsellingprice =
                                minimumSellingPriceText!!.getText().toString().toDouble()
                            if (minimumsellingprice <= priceText!!.getText().toString().toDouble()) {
                                insertProducts()
                            } else {
                                showMinimumSellingpriceAlert(
                                    minimumSellingPriceText!!.getText().toString()
                                )
                            }
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Price should not be zero",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Enter the price", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    if (priceText!!.getText().toString() != null && !priceText!!.getText().toString()
                            .isEmpty()
                    ) {
                        if (priceText!!.getText().toString().toDouble() > 0) {
                            val minimumsellingprice =
                                minimumSellingPriceText!!.getText().toString().toDouble()
                            if (minimumsellingprice <= priceText!!.getText().toString().toDouble()) {
                                Log.w("saleReturn..","")
                                addProduct("Add")
                            } else {
                                showMinimumSellingpriceAlert(
                                    minimumSellingPriceText!!.getText().toString()
                                )
                            }
                        } else {
                            if (focEditText!!.getText() != null && !focEditText!!.getText().toString()
                                    .isEmpty() && focEditText!!.getText()
                                    .toString() != "0" || returnQtyText!!.getText() != null && !returnQtyText!!.getText()
                                    .toString().isEmpty() && returnQtyText!!.getText()
                                    .toString() != "0"
                            ) {
                                Log.w("saleReturn1..","")
                                addProduct("Add")
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Price should not be zero",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(applicationContext, "Enter the price", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                if (focEditText!!.getText() != null && !focEditText!!.getText().toString()
                        .isEmpty() && focEditText!!.getText()
                        .toString() != "0" || returnQtyText!!.getText() != null && !returnQtyText!!.getText()
                        .toString().isEmpty() && returnQtyText!!.getText().toString() != "0"
                ) {
                    addProduct("Add")
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Price should not be zero",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        returnAdj!!.setOnClickListener(View.OnClickListener { returnLayoutView!!.setVisibility(View.VISIBLE) })
        cancelReturn!!.setOnClickListener(View.OnClickListener { returnLayoutView!!.setVisibility(View.GONE) })
        saveReturn!!.setOnClickListener(View.OnClickListener { /* if (!expiryReturnQty.getText().toString().isEmpty() && !damageReturnQty.getText().toString().isEmpty()){
                    if (!damageReturnQty.getText().toString().isEmpty()){
             //           if (Integer.parseInt(damageReturnQty.getText().toString())> 0){
                            if (Integer.parseInt(expiryReturnQty.getText().toString()) == 0){
                                dbHelper.updateReturnQty("Delete","0","Expiry",productId);
                                dbHelper.updateReturnQty("Update",damageReturnQty.getText().toString(),"Damage",productId);
                            }else if (Integer.parseInt(damageReturnQty.getText().toString())==0){
                                dbHelper.updateReturnQty("Delete","0","Damage",productId);
                                dbHelper.updateReturnQty("Update",expiryReturnQty.getText().toString(),"Expiry",productId);
                            }else {
                                dbHelper.updateReturnQty("Update",damageReturnQty.getText().toString(),"Damage",productId);
                                dbHelper.updateReturnQty("Update",expiryReturnQty.getText().toString(),"Expiry",productId);
                            }
                      //  }
                    }
                }*/
            expiryReturnQty!!.clearFocus()
            damageReturnQty!!.clearFocus()
            returnLayoutView!!.setVisibility(View.GONE)
        })
        invoiceDate!!.setOnClickListener(View.OnClickListener { getDate(invoiceDate) })
        getProducts()


        // Setting the sorting
        sortButton!!.setOnClickListener(View.OnClickListener {
            val menuItemView = findViewById<View>(R.id.fab)
            val popupMenu = PopupMenu(this@NewSalesReturnProductAddActivity, menuItemView)
            popupMenu.menuInflater.inflate(R.menu.sort_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_all -> {
                        setProductsDisplay("All Products")
                        true
                    }

                    R.id.action_stock -> {
                        setProductsDisplay("In Stock")
                        true
                    }

                    R.id.action_outstock -> {
                        setProductsDisplay("Out of Stock")
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        })
    }

    fun setProductsDisplay(action: String) {
        try {
            // productsAdapterNew.setWithFooter(false);
            // sortAdapter.resetPosition();
            val filterdNames = ArrayList<ProductsModel>()
            //   for (ProductsModel s : selectProductAdapter.getProductsList()) {
            for (s in AppUtils.getProductsList()) {
                if (action == "In Stock") {
                    if (s.stockQty != null && s.stockQty != "null") {
                        if (s.stockQty.toDouble() > 0) {
                            filterdNames.add(s)
                        }
                    }
                }  else if (action == "Out of Stock") {
                    if (s.stockQty != null && s.stockQty != "null") {
                        if (s.stockQty.toDouble() < 0 || s.stockQty.toDouble() == 0.0) {
                            filterdNames.add(s)
                        }
                    }
                }else {
                    filterdNames.add(s)
                }
            }

            // Setting filter option in Localdb
            if (action == "In Stock") {
                dbHelper!!.insertSettings("stock_view", "1")
            } else if (action == "Out of Stock") {
                dbHelper!!.insertSettings("stock_view", "0")
            } else {
                dbHelper!!.insertSettings("stock_view", "2")
            }
            //calling a method of the adapter class and passing the filtered list
            if (filterdNames.size > 0) {
                /* for (ProductsModel model:filterdNames){
                    Log.w("products_Option_values:",model.getProductName());
                }*/
                if (selectProductAdapter != null) {
                    selectProductAdapter!!.filterList(filterdNames)
                }
                selectProductAdapter!!.filterList(filterdNames)
                productLayout!!.visibility = View.VISIBLE
                emptyLayout!!.visibility = View.GONE
                totalProducts!!.text = filterdNames.size.toString() + " Products"
            } else {
                productLayout!!.visibility = View.GONE
                emptyLayout!!.visibility = View.VISIBLE
                totalProducts!!.text = filterdNames.size.toString() + " Products"
            }
        } catch (ex: Exception) {
            Log.e("Error_in_filter", Objects.requireNonNull(ex.message)!!)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate(dateEditext: TextView?) {
        // Get Current Date
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this@NewSalesReturnProductAddActivity,
            { view, year, monthOfYear, dayOfMonth ->
                dateEditext!!.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                currentDate = convertDate(
                    dateEditext.text.toString()
                )
                Log.w("CurrentDateView:", currentDate!!)
            }, mYear, mMonth, mDay
        )
        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show()
    }

    fun setProductResult(productId: String) {
        try {
            lowStockSetting
            for (model in AppUtils.getProductsList()) {
                Log.w("Product_Code:", model.productCode)
                if (model.productCode == productId.trim { it <= ' ' }) {
                    if (model.stockQty != null && model.stockQty != "null") {
                        if (model.stockQty.toDouble().equals("0") || model.stockQty.toDouble() < 0) {
                            stockQtyValue!!.text = model.stockQty
                            stockQtyValue!!.setTextColor(Color.parseColor("#D24848"))
                        } else if (model.stockQty.toDouble() > 0) {
                            stockQtyValue!!.setTextColor(Color.parseColor("#2ECC71"))
                            stockQtyValue!!.text = model.stockQty
                        }
                    }
                    // stockLayout.setVisibility(View.VISIBLE);
                    if (model.stockQty.toDouble().equals("0") || model.stockQty.toDouble() < 0) {
                        if (isAllowLowStock) {
                            productAutoComplete!!.clearFocus()
                            cartonPrice!!.setText(model.unitCost)
                            priceText!!.setText(model.unitCost)
                            loosePrice!!.setText(model.unitCost)
                            uomText!!.setText(model.uomCode)
                            stockCount!!.setText(model.stockQty)
                            pcsPerCarton!!.setText(model.pcsPerCarton)
                            qtyValue!!.isEnabled = true
                            qtyValue!!.setText("")
                            qtyValue!!.requestFocus()
                            openKeyborard(qtyValue)
                            qtyValue!!.setSelection(qtyValue!!.text.length)
                            focEditText!!.isEnabled = true
                            exchangeEditext!!.isEnabled = true
                            discountEditext!!.isEnabled = true
                            returnEditext!!.isEnabled = true
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Low Stock Please check",
                                Toast.LENGTH_SHORT
                            ).show()
                            productAutoComplete!!.clearFocus()
                            productAutoComplete!!.setText("")
                        //    qtyValue!!.isEnabled = false
                        }
                    } else {
                        productAutoComplete!!.clearFocus()
                        cartonPrice!!.setText(model.unitCost)
                        priceText!!.setText(model.unitCost)
                        loosePrice!!.setText(model.unitCost)
                        uomText!!.setText(model.uomCode)
                        stockCount!!.setText(model.stockQty)
                        pcsPerCarton!!.setText(model.pcsPerCarton)
                        qtyValue!!.isEnabled = true
                        priceText!!.isEnabled = true
                        qtyValue!!.setText("")
                        qtyValue!!.requestFocus()
                        openKeyborard(qtyValue)
                        qtyValue!!.setSelection(qtyValue!!.text.length)
                        focEditText!!.isEnabled = true
                        exchangeEditext!!.isEnabled = true
                        discountEditext!!.isEnabled = true
                        returnEditext!!.isEnabled = true
                    }
                } else {
                    Log.w("Not_found", "Product")
                }
            }
        } catch (ex: Exception) {
        }
    }

    fun showAlert() {
        SweetAlertDialog(applicationContext, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Warning !")
            .setContentText("Net total should not be zero or Atleast add one Qty")
            .setConfirmText("Cancel")
            .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation() }.show()
    }

    fun showAlertForDeleteProduct() {
        SweetAlertDialog(applicationContext, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Warning !")
            .setContentText("Products Will be cleared are you sure want to back?")
            .setConfirmText("Cancel")
            .setConfirmClickListener { sDialog ->
                dbHelper!!.removeAllInvoiceItems()
                sDialog.dismissWithAnimation()
            }.show()
    }

    fun showMinimumSellingpriceAlert(sellingPrice: String) {
        val builder1 = AlertDialog.Builder(applicationContext)
        builder1.setTitle("Warning !")
        builder1.setMessage(
            productName + "-" + "Minimum Selling Price is : $ " + Utils.twoDecimalPoint(
                sellingPrice.toDouble()
            )
        )
        builder1.setCancelable(false)
        builder1.setPositiveButton("OK") { dialog, id -> dialog.dismiss() }
        val alert11 = builder1.create()
        alert11.show()
    }

    fun addProduct(action: String?) {
        Log.w("ProductIdView2Retr:",""+ isProductExist(productId))
        if (isProductExist(productId)) {
            if (!qtyValue!!.text.toString().isEmpty() && qtyValue!!.text.toString()
                    .toDouble() > 0
            ) {
                showExistingProductAlert(productId, productName)
            }
//            else {
//                insertProducts()
//            }
        } else {
            insertProducts()
        }
    }

    fun showExistingProductAlert(productId: String?, productName: String?) {
        val builder1 = AlertDialog.Builder(this@NewSalesReturnProductAddActivity)
        builder1.setTitle("Warning !")
        builder1.setMessage("$productName - $productId\nAlready Exist Do you want to replace ? ")
        builder1.setCancelable(false)
        builder1.setPositiveButton("YES") { dialog, id ->
            dialog.cancel()
            insertProducts()
        }
        builder1.setNegativeButton("NO") { dialog, id ->
            dialog.cancel()
            clearFields()
        }
        val alert11 = builder1.create()
        alert11.show()
    }

    fun insertProducts() {
        try {
            var focType = "pcs"
            var exchangeType = "pcs"
            var returnType = "pcs"
            var discount = "0"
            var return_qty = "0"
            val lPriceCalc = "0"
            var foc = "0"
            var uom = "PCS"
            var price_value = "0"
            if (focSwitch!!.isChecked) {
                focType = "ctn"
            }
            if (exchangeSwitch!!.isChecked) {
                exchangeType = "ctn"
            }
            if (returnSwitch!!.isChecked) {
                returnType = "ctn"
            }
            val ctn_qty = "0"
            var qty_value = "0"
            if (!qtyValue!!.text.toString().isEmpty()) {
                qty_value = qtyValue!!.text.toString()
            }
            if (!discountEditext!!.text.toString().isEmpty()) {
                discount = discountEditext!!.text.toString()
            }
            if (!returnQtyText!!.text.toString().isEmpty()) {
                return_qty = returnQtyText!!.text.toString()
            }
            if (!priceText!!.text.toString().isEmpty()) {
                price_value = priceText!!.text.toString()
            }
            if (!focEditText!!.text.toString().isEmpty()) {
                foc = focEditText!!.text.toString()
            }
            val priceValue = 0.0
            val net_qty = qty_value.toDouble() - return_qty.toDouble()
            val return_amt = return_qty.toDouble() * price_value.toDouble()
            val total = net_qty * price_value.toDouble()
            val sub_total = total - return_amt - discount.toDouble()
            var timeStamp: String? = Calendar.getInstance().timeInMillis.toString()
            if(isEditItem){
                timeStamp = editTimeStamp
            }else{
                timeStamp = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Date())
            }
            if (!uomText!!.text.toString().isEmpty()) {
                uom = uomText!!.text.toString()
            }
            Log.w("uomsalereturn",""+uom)
            val insertStatus = dbHelper!!.insertCreateInvoiceCart(
                productId.toString().trim { it <= ' ' },
                productName,
                uom,
                uom,
                qty_value,
                return_qty, net_qty.toString(),
                foc,
                price_value,
                stockQtyValue!!.text.toString(), total.toString(),
                subTotalValue!!.text.toString(),
                taxValueText!!.text.toString(),
                netTotalValue!!.text.toString(), "",
                "",
                "",
                "",
                "",
                "","",timeStamp,"")

            // Adding Return Qty Table values
            if (qty_value.toInt() > 0) {
                dbHelper!!.updateReturnQty("Delete", "0", "Saleable Return", productId)
                dbHelper!!.updateReturnQty("Delete", "0", "Damaged/Expired", productId)
                dbHelper!!.insertReturnProduct(
                    productId,
                    productName,
                    expiryReturnQty!!.text.toString(),
                    "Saleable Return"
                )
                dbHelper!!.insertReturnProduct(
                    productId,
                    productName,
                    damageReturnQty!!.text.toString(),
                    "Damaged/Expired"
                )
            }
            if (insertStatus) {
                subTotalValue!!.text = "0.0"
                taxValueText!!.text = "0.00"
                netTotalValue!!.text = "0.0"
                productAutoComplete!!.setText("")
                priceText!!.setText("0.00")
                qtyValue!!.setText("")
                uomText!!.setText("")
                stockCount!!.setText("")
                stockQtyValue!!.text = ""
                qtyValue!!.clearFocus()
                productAutoComplete!!.clearFocus()
                focEditText!!.setText("")
                exchangeEditext!!.setText("")
                discountEditext!!.setText("")
                returnQtyText!!.setText("")
                editTimeStamp = ""
                uomChangel!!.visibility = View.GONE
                ed_uomTxtl!!.visibility = View.GONE
                uomSpinnerLayl!!.visibility = View.VISIBLE
                ischangeUOM = false
                focSwitch!!.isChecked = false
                exchangeSwitch!!.isChecked = false
                returnSwitch!!.isChecked = false
                stockLayout!!.visibility = View.GONE
                priceText!!.isEnabled = false
               // qtyValue!!.isEnabled = false
                focEditText!!.isEnabled = false
                exchangeEditext!!.isEnabled = false
                discountEditext!!.isEnabled = false
                addProduct!!.text = "Add"
                hideKeyboard()
                getProducts()
            } else {
                Toast.makeText(applicationContext, "Error in Add product", Toast.LENGTH_LONG).show()
            }
        } catch (ec: Exception) {
            Log.e("Error_InsertProduct:", ec.message!!)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getProducts() {
        val products = dbHelper!!.allInvoiceProducts
        if (products.size > 0) {
            itemCount!!.text = "Products ( " + products.size + " )"
            productSummaryView!!.layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            productSummaryAdapter = NewSalesReturnProductAdapter(
                this,
                products,
                object : NewSalesReturnProductAdapter.CallBack {
                    override fun searchCustomer(letter: String, pos: Int) {}
                    override fun removeItem(pid: String,updateTime: String) {
                        showRemoveItemAlert(pid,updateTime)
                    }

                    override fun editItem(model: CreateInvoiceModel) {
                        isEditItem = true
                        salesReturn!!.isChecked = false
                        salesReturn!!.isEnabled = false
                        productId = model.productCode
                        editTimeStamp = model.updateTime
                        productName = model.productName
                        qtyValue!!.setText("")
                        val netqty = model.netQty.toDouble()

                        /*  if (model.getMinimumSellingPrice()!=null && !model.getMinimumSellingPrice().isEmpty()){
                        minimumSellingPriceText.setText(model.getMinimumSellingPrice());
                    }else {
                        minimumSellingPriceText.setText("0.00");
                    }*/qtyValue!!.removeTextChangedListener(qtyTW)
                        qtyValue!!.setText(Utils.getQtyValue(netqty.toString()))
                        qtyValue!!.addTextChangedListener(qtyTW)
                        productAutoComplete!!.setText(model.productName + "-" + model.productCode)
                        priceText!!.setText(model.price)
                        addProduct!!.text = "Update"
                        qtyValue!!.requestFocus()
                        qtyValue!!.setSelectAllOnFocus(true)
                        qtyValue!!.setSelection(qtyValue!!.text.length)
                        qtyValue!!.isEnabled = true
                        priceText!!.isEnabled = true
                        productEditId = model.productCode
                        pdtStockVal = model.stockProductQty

                        val jsonObject = JSONObject()
                        jsonObject.put("CustomerCode", selectCustomerId)
                        jsonObject.put("ItemCode",productEditId)
                        getUOMEdit(jsonObject,model.uomCode,model.stockProductQty)


                        if (model.focQty != null && !model.focQty.isEmpty() && model.focQty != "null") {
                            focEditText!!.setText(model.focQty)
                        } else {
                            focEditText!!.setText("0")
                        }
                        if (model.returnQty != null && !model.returnQty.isEmpty() && model.returnQty != "null") {
                            returnQtyText!!.setText(model.returnQty)
                        } else {
                            returnQtyText!!.setText("0")
                        }
                        focEditText!!.isEnabled = true
                        returnQtyText!!.isEnabled = true
                       // stockLayout!!.visibility = View.VISIBLE
                        stockQtyValue!!.setTextColor(Color.parseColor("#2ECC71"))
                        stockQtyValue!!.text = model.stockQty
                        stockCount!!.setText(model.stockQty)

                        uomValueVisible(model.uomCode)

                        /* for (ProductsModel m:productList) {
                        if (m.getProductCode().equals(productId)) {
                            if (m.getStockQty()!=null && !m.getStockQty().equals("null")){
                                if (Double.parseDouble(m.getStockQty()) == 0 || Double.parseDouble(m.getStockQty()) < 0) {
                                    stockQtyValue.setText(m.getStockQty());
                                    stockQtyValue.setTextColor(Color.parseColor("#D24848"));
                                    stockCount.setText(m.getStockQty());
                                } else if (Double.parseDouble(m.getStockQty()) > 0) {
                                    stockQtyValue.setTextColor(Color.parseColor("#2ECC71"));
                                    stockQtyValue.setText(m.getStockQty());
                                    stockCount.setText(m.getStockQty());
                                }
                            }
                        }
                    }*/

                        //  setCalculationView();
                    }
                })
            productSummaryView!!.adapter = productSummaryAdapter
            noproductText!!.visibility = View.GONE
            productSummaryView!!.visibility = View.VISIBLE
            itemCount!!.visibility = View.VISIBLE
        } else {
            itemCount!!.visibility = View.GONE
            noproductText!!.visibility = View.VISIBLE
            productSummaryView!!.visibility = View.GONE
        }
        Utils.refreshActionBarMenu(this)
        hideKeyboard()
        setSummaryTotal()
    }

    fun showRemoveItemAlert(pid: String?,updateTime:String) {
        try {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE) // .setTitleText("Are you sure?")
                .setContentText("Are you sure want to remove this item ?")
                .setConfirmText("YES")
                .setConfirmClickListener { sDialog ->
                    dbHelper!!.deleteInvoiceProductNew(pid,updateTime)
                    clearFields()
                    sDialog.dismissWithAnimation()
                    getProducts()
                    setSummaryTotal()
                    Utils.refreshActionBarMenu(this@NewSalesReturnProductAddActivity)
                }
                .showCancelButton(true)
                .setCancelText("No")
                .setCancelClickListener { sDialog -> sDialog.cancel() }
                .show()
        } catch (ex: Exception) {
        }
    }

    fun hideKeyboard() {
        try {
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (e: Exception) {
        }
    }

    fun setSummaryTotal() {
        try {
            val localCart: ArrayList<CreateInvoiceModel>
            localCart = dbHelper!!.allInvoiceProducts
            var net_sub_total = 0.0
            var net_tax = 0.0
            var net_total = 0.0
            if (localCart.size > 0) {
                for (model in localCart) {
                    if (model.subTotal != null && !model.subTotal.isEmpty()) {
                        net_sub_total += model.subTotal.toDouble()
                    }
                    if (model.gstAmount != null && !model.gstAmount.isEmpty()) {
                        net_tax += model.gstAmount.toDouble()
                    }
                    if (model.netTotal != null && !model.netTotal.isEmpty()) {
                        net_total += model.netTotal.toDouble()
                    }
                }
                val sharedPreferences = getSharedPreferences("customerPref", MODE_PRIVATE)
                selectCustomerId = sharedPreferences.getString("customerId", "")
                customerDetails = dbHelper!!.getCustomer(selectCustomerId)
                val taxValue = customerDetails!!.get(0).taxPerc
                val taxType = customerDetails!!.get(0).taxType
                Log.w("GivenEditTax:", net_tax.toString() + "")
                Log.w("GivenNetTotalValues:", net_total.toString() + "")
                if (taxType == "I") {
                    setCalculationSummaryView(net_total)
                } else {
                    setCalculationSummaryView(net_sub_total)
                }
            }
        } catch (ex: Exception) {
        }
    }

    fun clearFields() {
        subTotalValue!!.text = "0.0"
        taxValueText!!.text = "0.00"
        netTotalValue!!.text = "0.0"
        productAutoComplete!!.setText("")
        priceText!!.setText("0.00")
        qtyValue!!.setText("")
        pcsPerCarton!!.setText("")
        uomText!!.setText("")
        stockCount!!.setText("")
        editTimeStamp = ""
        stockQtyValue!!.text = ""
        qtyValue!!.clearFocus()
        productAutoComplete!!.clearFocus()
        focEditText!!.setText("")
        returnQtyText!!.setText("")
        stockLayout!!.visibility = View.GONE
        uomChangel!!.visibility = View.GONE
        ed_uomTxtl!!.visibility = View.GONE
        uomSpinnerLayl!!.visibility = View.VISIBLE
        ischangeUOM = false
        // priceText.setEnabled(false);
        getProducts()
        setSummaryTotal()
    }
    fun isProductExist(productId: String?): Boolean {
        var isExist = false
        // try {
        val localCart = dbHelper!!.allInvoiceProducts

        if (localCart.size > 0) {

            for (cart in localCart) {
                Log.w("localPdt1",""+cart.productCode)

                if (cart.productCode != null) {
                    if (cart.productCode == productId) {
                        Log.w("ProductIdPrint:", cart.productCode)
                        isExist = true
                        break
                    }
                }
            }
        }
//        } catch (ex: Exception) {
//            Log.e("Exp_to_check_product:", Objects.requireNonNull(ex.message!!))
//        }
        return isExist
    }

//    fun isProductExist(productId: String?): Boolean {
//        var isExist = false
//      //  try {
//            val localCart = dbHelper!!.allCartItems
//            if (localCart.size > 0) {
//                for (cart in localCart) {
//                    if (cart.carT_COLUMN_PID != null) {
//                        if (cart.carT_COLUMN_PID == productId) {
//                            Log.w("ProductIdPrint:", cart.carT_COLUMN_PID)
//                            isExist = true
//                            break
//                        }
//                    }
//                }
//            }
////        } catch (ex: Exception) {
////            Log.e("Exp_to_check_product:", Objects.requireNonNull(ex.message)!!)
////        }
//        return isExist
//    }

    fun checkLowStock() {
        var stock = 0.0
        var qty = 0.0
        if (!stockQtyValue!!.text.toString().isEmpty()) {
            if (!stockQtyValue!!.text.toString().isEmpty()) {
                stock = stockQtyValue!!.text.toString().toDouble()
            }
            if (!qtyValue!!.text.toString().isEmpty()) {
                qty = qtyValue!!.text.toString().toDouble()
            }
            if (stock < qty) {
                Toast.makeText(applicationContext, "Low Stock please check", Toast.LENGTH_SHORT)
                    .show()
                qtyValue!!.removeTextChangedListener(qtyTW)
                qtyValue!!.setText("")
                qtyValue!!.addTextChangedListener(qtyTW)
                setCalculationView()
            }
        }
    }

    val lowStockSetting: Unit
        /**
         * Get = the low stock invoice setting to allow the Negative stock allow the invoice
         */
        get() {
            val settings = dbHelper!!.settings
            if (settings.size > 0) {
                for (model in settings) {
                    if (model.settingName == "allow_negative_switch") {
                        isAllowLowStock = model.settingValue == "1"
                    }
                    Log.w("SettingNameRet11:", model.settingName)
                }
            }else {
                isAllowLowStock = false
            }
        }
    fun uomValueVisible(uomcode:String){
        ed_uomTxtl!!.setText(uomcode)
        uomChangel!!.visibility = View.VISIBLE
        ed_uomTxtl!!.visibility = View.VISIBLE
        uomSpinnerLayl!!.visibility = View.GONE
    }

    fun setCalculationSummaryView(subTotal: Double) {
        try {
            var taxAmount1 = 0.0
            var netTotal1 = 0.0
            val sharedPreferences = getSharedPreferences("customerPref", MODE_PRIVATE)
            val selectCustomerId = sharedPreferences.getString("customerId", "")
            if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
                customerDetails = dbHelper!!.getCustomer(selectCustomerId)
            }
            val taxValue = customerDetails!![0].taxPerc
            val taxType = customerDetails!![0].taxType
            Log.w("TaxType-Summary:", taxType)
            Log.w("TaxValue12-Summary:", taxValue)
            Log.w("SubTotalValues:", subTotal.toString())
            val Prodtotal = Utils.twoDecimalPoint(subTotal)
            if (!taxType.matches("".toRegex()) && !taxValue.matches("".toRegex())) {
                val taxValueCalc = taxValue.toDouble()
                if (taxType.matches("E".toRegex())) {
                    taxAmount1 = subTotal * taxValueCalc / 100
                    val prodTax = Utils.twoDecimalPoint(taxAmount1)
                    taxValueText!!.text = "" + prodTax
                    netTotal1 = subTotal + taxAmount1
                    val ProdNetTotal = Utils.twoDecimalPoint(netTotal1)
                    netTotalValue!!.text = "" + ProdNetTotal
                    taxTitle!!.text = "GST ( Exc )"
                    subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                } else if (taxType.matches("I".toRegex())) {
                    taxAmount1 = subTotal * taxValueCalc / (100 + taxValueCalc)
                    val prodTax = Utils.twoDecimalPoint(taxAmount1)
                    taxValueText!!.text = "" + prodTax
                    // netTotal1 = subTotal + taxAmount1;
                    netTotal1 = subTotal
                    val ProdNetTotal = Utils.twoDecimalPoint(netTotal1)
                    netTotalValue!!.text = "" + ProdNetTotal
                    val dTotalIncl = netTotal1 - taxAmount1
                    val totalIncl = Utils.twoDecimalPoint(dTotalIncl)
                    Log.d("totalIncl", "" + totalIncl)
                    val sub_total = subTotal - taxAmount1
                    taxTitle!!.text = "GST ( Inc )"
                    subTotalValue!!.text = Utils.twoDecimalPoint(sub_total)
                } else if (taxType.matches("Z".toRegex())) {
                    taxValueText!!.text = "0.0"
                    // netTotal1 = subTotal + taxAmount;
                    netTotal1 = subTotal
                    val ProdNetTotal = Utils.twoDecimalPoint(netTotal1)
                    netTotalValue!!.text = "" + ProdNetTotal
                    subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                    taxTitle!!.text = "GST ( Zero )"
                } else {
                    taxValueText!!.text = "0.0"
                    netTotalValue!!.text = "" + Prodtotal
                    subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                    taxTitle!!.text = "GST ( Zero )"
                }
            } else if (taxValue.matches("".toRegex())) {
                taxValueText!!.text = "0.0"
                netTotalValue!!.text = "" + Prodtotal
                subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                taxTitle!!.text = "GST ( Zero )"
            } else {
                taxValueText!!.text = "0.0"
                netTotalValue!!.text = "" + Prodtotal
                subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                taxTitle!!.text = "GST ( Zero )"
            }
        } catch (e: Exception) {
        }
    }

    fun setCalculationView() {
        try {
            val taxAmount = 0.0
            val netTotal = 0.0
            var taxAmount1 = 0.0
            var netTotal1 = 0.0
            var return_qty = 0.0
            val pcspercarton = 0.0
            val cqtyCalc = 0.0
            val lqtyCalc = 0.0
            var net_qty = 0.0
            val sharedPreferences = getSharedPreferences("customerPref", MODE_PRIVATE)
            val selectCustomerId = sharedPreferences.getString("customerId", "")
            if (selectCustomerId != null && !selectCustomerId.isEmpty()) {
                customerDetails = dbHelper!!.getCustomer(selectCustomerId)
            }
            val taxValue = customerDetails!![0].taxPerc
            val taxType = customerDetails!![0].taxType
            Log.w("TaxType12:", taxType)
            Log.w("TaxValue12:", taxValue)
            var price = priceText!!.text.toString()
            var qty = qtyValue!!.text.toString()
            if (price.matches("".toRegex())) {
                price = "0"
            }
            if (qty.matches("".toRegex())) {
                qty = "0"
            }
            val cPriceCalc = price.toDouble()
            if (!returnQtyText!!.text.toString()
                    .isEmpty() && returnQtyText!!.text.toString() != "null"
            ) {
                return_qty = returnQtyText!!.text.toString().toDouble()
                netReturnQty!!.text = return_qty.toString()
            } else {
                netReturnQty!!.text = "0"
                expiryReturnQty!!.setText("")
                damageReturnQty!!.setText("")
            }
            net_qty = qty.toDouble() - return_qty
            var tt = net_qty * cPriceCalc
            Log.w("TOTALVALUES:", tt.toString())
            val Prodtotal = Utils.twoDecimalPoint(tt)
            var subTotal = 0.0
            val itemDisc = discountEditext!!.text.toString()
            subTotal = if (!itemDisc.matches("".toRegex())) {
                val itmDisc = itemDisc.toDouble()
                tt - itmDisc
            } else {
                tt
            }
            val sbTtl = Utils.twoDecimalPoint(subTotal)


            /*  if (return_qty!=0){
                double return_amt=0.0;
                return_amt=(return_qty*cPriceCalc);
                subTotal=subTotal-return_amt;
            }*/

            // sl_total_inclusive.setText("" + sbTtl);
            tt = subTotal
            Log.w("SubTotalValues:", subTotal.toString())
            if (!taxType.matches("".toRegex()) && !taxValue.matches("".toRegex())) {
                val taxValueCalc = taxValue.toDouble()
                if (taxType.matches("E".toRegex())) {
                    taxAmount1 = subTotal * taxValueCalc / 100
                    val prodTax = Utils.twoDecimalPoint(taxAmount1)
                    taxValueText!!.text = "" + prodTax
                    netTotal1 = subTotal + taxAmount1
                    val ProdNetTotal = Utils.twoDecimalPoint(netTotal1)
                    netTotalValue!!.text = "" + ProdNetTotal
                    taxTitle!!.text = "GST ( Exc )"
                    subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                } else if (taxType.matches("I".toRegex())) {
                    taxAmount1 = subTotal * taxValueCalc / (100 + taxValueCalc)
                    val prodTax = Utils.twoDecimalPoint(taxAmount1)
                    taxValueText!!.text = "" + prodTax
                    netTotal1 = subTotal
                    val ProdNetTotal = Utils.twoDecimalPoint(netTotal1)
                    netTotalValue!!.text = "" + ProdNetTotal
                    val dTotalIncl = netTotal1 - taxAmount1
                    val totalIncl = Utils.twoDecimalPoint(dTotalIncl)
                    Log.d("totalIncl", "" + totalIncl)
                    val sub_total = subTotal - taxAmount1
                    taxTitle!!.text = "GST ( Inc )"
                    subTotalValue!!.text = Utils.twoDecimalPoint(sub_total)
                } else if (taxType.matches("Z".toRegex())) {
                    taxValueText!!.text = "0.00"
                    netTotal1 = subTotal
                    val ProdNetTotal = Utils.twoDecimalPoint(netTotal1)
                    netTotalValue!!.text = "" + ProdNetTotal
                    subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                    taxTitle!!.text = "GST ( Zero )"
                } else {
                    taxValueText!!.text = "0.00"
                    netTotalValue!!.text = "" + Prodtotal
                    subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                    taxTitle!!.text = "GST ( Zero )"
                }
            } else if (taxValue.matches("".toRegex())) {
                taxValueText!!.text = "0.00"
                netTotalValue!!.text = "" + Prodtotal
                subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                taxTitle!!.text = "GST ( Zero )"
            } else {
                taxValueText!!.text = "0.00"
                netTotalValue!!.text = "" + Prodtotal
                subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                taxTitle!!.text = "GST ( Zero )"
            }
            setButtonView()
        } catch (e: Exception) {
            Log.w("Error_Throwing::", e.message!!)
        }
    }

    fun setButtonView() {
        if (netTotalValue!!.text.toString().toDouble() > 0) {
            addProduct!!.alpha = 0.9f
            addProduct!!.isEnabled = true
        } else {
            if (!focEditText!!.text.toString()
                    .isEmpty() && focEditText!!.text.toString() != "0" || !returnQtyText!!.text.toString()
                    .isEmpty() && returnQtyText!!.text.toString() != "0"
            ) {
                addProduct!!.alpha = 0.9f
                addProduct!!.isEnabled = true
            } else {
                addProduct!!.alpha = 0.4f
                addProduct!!.isEnabled = false
            }
        }
    }

    fun getAllProducts(jsonObject: JSONObject) {
        // Initialize a new RequestQueue instance
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "CustomerProductList"
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_SAP_PROUCT_URL:", url + jsonObject.toString())
        productList = ArrayList()
        products = ArrayList()
        //  SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        //  pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        //  pDialog.setTitleText("Loading Products...");
        //  pDialog.setCancelable(false);
        //  pDialog.show();
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            jsonObject,
            Response.Listener { response: JSONObject ->
                try {

                   Log.w("Response_SAP_PRODUCTS:", response.toString())
                    // Loop through the array elements
                    val productArray = response.optJSONArray("responseData")
                    for (i in 0 until Objects.requireNonNull(productArray).length()) {
                        // Get current json object
                        val productObject = productArray.getJSONObject(i)
                        val product = ProductsModel()
                        if (productObject.optString("isActive") == "N") {
                            product.companyCode = "1"
                            // Adding bp name for products
                            if (productObject.optString("bP_Description") != null && !productObject.optString(
                                    "bP_Description"
                                ).isEmpty() && productObject.optString("bP_Description") != "null"
                            ) {
                                product.productName = productObject.optString("bP_Description")
                            } else {
                                product.productName = productObject.optString("productName")
                            }
                            if (productObject.optString("bP_CatalogNo") != null) {
                                product.customerItemCode = productObject.optString("bP_CatalogNo")
                            }
                            product.productCode = productObject.optString("productCode")
                            product.weight = ""
                            product.productImage = productObject.optString("imageURL")
                            product.wholeSalePrice = "0.00"
                            product.retailPrice = productObject.optDouble("retailPrice")
                            product.barcode = productObject.optString("barCode")
                            product.cartonPrice = productObject.optString("cartonPrice")
                            product.pcsPerCarton = productObject.optString("pcsPerCarton")
                            product.unitCost = productObject.optString("price")
                            product.lastPrice = productObject.optString("lastSalesPrice")
                            product.minimumSellingPrice =
                                productObject.optString("minimumSellingPrice")
                            product.defaultUom = productObject.optString("defaultSalesUOM")
                            if (productObject.optString("stockInHand") != "null") {
                                product.stockQty = productObject.optString("stockInHand")
                            } else {
                                product.stockQty = "0"
                            }
                            product.uomCode = productObject.optString("uomCode")
                            product.isItemFOC = productObject.optString("itemAllowFOC")

                            //  product.setProductBarcode(productObject.optString("BarCode")); Add values In Futue
                            // product.productBarcode = ""

                            /*  ArrayList<UomModel> uomList=new ArrayList<>();
                                JSONArray uomArray=productObject.optJSONArray("uomDetails");
                                if (uomArray!=null && uomArray.length() > 0){
                                    for (int j = 0; j< uomArray.length(); j++){
                                        JSONObject uomObject = uomArray.getJSONObject(j);
                                        UomModel uomModel =new UomModel();
                                        uomModel.setUomCode(uomObject.optString("uomCode"));
                                        uomModel.setUomName(uomObject.optString("uomName"));
                                        uomModel.setUomEntry(uomObject.optString("uomEntry"));
                                        uomModel.setAltQty(uomObject.optString("altQty"));
                                        uomModel.setBaseQty(uomObject.optString("baseQty"));

                                        uomList.add(uomModel);
                                    }
                                }
                                product.setUomText(uomList.toString());
                                product.setProductUOMList(uomList);*/

                            productList!!.add(product)
                        }
                    }
                    HomePageModel.productsList = ArrayList()
//                    productList!!.get(0).barcode = "1234566"
//                    productList!!.get(1).barcode = "888801566968"
                    setAdapter(productList)
                    HomePageModel.productsList.addAll(productList!!)
                    // pDialog.dismiss();
                    if (productList!!.size > 0) {
                        runOnUiThread {
                            AppUtils.setProductsList(productList)

                            // setProductsDisplay("All Products")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.w("Errorn:", Objects.requireNonNull(e.message!!))                }
            },
            Response.ErrorListener { error: VolleyError ->
                // Do something when error occurred
                // pDialog.dismiss();
                Log.w("Error_throwing:", error.toString())
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

    @SuppressLint("SetTextI18n")
    private fun setAdapter(productList: ArrayList<ProductsModel>?) {
        // Get the Settings to Display the Product list with the Settings
        val settings = dbHelper!!.settings
        if (settings!!.size > 0) {
            for (model in settings) {
                if (model.settingName == "stock_view") {
                    if (model.settingValue == "1") {
                        stockProductView = "1"
                    } else if (model.settingValue == "2") {
                        stockProductView = "2"
                    } else {
                        stockProductView = "0"
                    }
                }
            }
        }
        // Filter the products list depending the Settings
        val filteredProducts = ArrayList<ProductsModel>()
        //   for (ProductsModel s : selectProductAdapter.getProductsList()) {
        if (productList != null && productList.size > 0) {
            for (product in productList) {
                if (stockProductView == "1") {
                    if (product.stockQty != null && product.stockQty != "null") {
                        if (product.stockQty.toDouble() > 0) {
                            filteredProducts.add(product)
                        }
                    }
                } else if (stockProductView == "0") {
                    if (product.stockQty != null && product.stockQty != "null") {
                        if (product.stockQty.toDouble() < 0 || product.stockQty.toDouble() == 0.0) {
                            filteredProducts.add(product)
                        }
                    }
                } else {
                    filteredProducts.add(product)
                }
            }
        }
        if (filteredProducts.size > 0) {
            productLayout!!.visibility = View.VISIBLE
            emptyLayout!!.visibility = View.GONE
        } else {
            productLayout!!.visibility = View.GONE
            emptyLayout!!.visibility = View.VISIBLE
        }
        productListView!!.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        selectProductAdapter = SelectProductAdapter(
            this,
            filteredProducts
        ) { model -> // Need to implement the Product price Later
            /* try {
                        getProductPrice(model.getProductCode());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("CustomerCode", selectCustomerId)
                    jsonObject.put("ItemCode", model.productCode)
                    getUOM(jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.w("Errors:", Objects.requireNonNull(e.message!!))                }


            productAutoComplete!!.setText(model.productName + " - " + model.productCode)
            productId = model.productCode
            productName = model.productName
            priceText!!.setText(Utils.twoDecimalPoint(model.unitCost.toDouble()))
            uomText!!.setText(model.uomCode)
            stockCount!!.setText(model.stockQty)
            // looseQtyValue.setEnabled(true);
            qtyValue!!.setText("")
            priceText!!.isEnabled = true
            focEditText!!.isEnabled = true
            exchangeEditext!!.isEnabled = true
            discountEditext!!.isEnabled = true
            returnQtyText!!.isEnabled = true
            if (model.minimumSellingPrice != null && !model.minimumSellingPrice.isEmpty()) {
                minimumSellingPriceText!!.text = model.minimumSellingPrice
            } else {
                minimumSellingPriceText!!.text = "0.00"
            }

            // stockLayout.setVisibility(View.VISIBLE);
            if (model.stockQty != null && model.stockQty != "null") {
                pdtStockVal = model.stockQty
                if (model.stockQty.toDouble().equals("0") || model.stockQty.toDouble() < 0) {
                    stockQtyValue!!.text = model.stockQty
                    stockQtyValue!!.setTextColor(Color.parseColor("#D24848"))
                } else if (model.stockQty.toDouble() > 0) {
                    stockQtyValue!!.setTextColor(Color.parseColor("#2ECC71"))
                    stockQtyValue!!.text = model.stockQty
                }
            }
            qtyValue!!.requestFocus()
            openKeyborard(qtyValue)
            viewCloseBottomSheet()
        }

        // products.add(productObject.optString("ProductName")+" - "+productObject.optString("ProductCode"));
        products = ArrayList()
        if (filteredProducts != null && filteredProducts.size > 0) {
            for (product in filteredProducts) {
                products!!.add(product.productName + " ~ " + product.productCode)
            }
        }
        productListView!!.adapter = selectProductAdapter
        autoCompleteAdapter =
            object : ArrayAdapter<String>(this, android.R.layout.select_dialog_item, products!!) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val v = super.getView(position, convertView, parent)
                    (v as TextView).textSize = 14f
                    /*    Typeface Type = getFont () ;  // custom method to get a font from "assets" folder
                ((TextView) v).setTypeface(Type);
                ((TextView) v).setTextColor(YourColor);*/v.gravity =
                        Gravity.LEFT or Gravity.CENTER_VERTICAL
                    return v
                }
            }
        productAutoComplete!!.threshold = 1
        productAutoComplete!!.setAdapter(autoCompleteAdapter)
        totalProducts!!.text = filteredProducts.size.toString() + " Products"
        //dialog.dismiss();
    }

    fun createSalesOrderJson(copy: Int) {

        // JSONObject rootJsonObject = new JSONObject();
        val rootJsonObject = JSONObject()
        val invoiceDetailsArray = JSONArray()
        var invoiceObject = JSONObject()

        //  {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"CUS\/686","customerName":"VH FACTORY","groupCode":"100",
        //  "contactPerson":"","creditLimit":"150.000000","currencyCode":"SGD","currencyName":"Singapore Dollar","taxType":"","taxCode":"SR",
        //  "taxName":"Sales Standard Rated Supplier SR","taxPercentage":"7.000000","balance":"21.600000","outstandingAmount":"128.400000",
        //  "address":"","street":"","city":"","state":"","zipCode":"","country":"","createDate":"13\/07\/2021","updateDate":"30\/07\/2021",
        //  "active":"N","remark":""}]}
        val detailsArray = customerResponse.optJSONArray("responseData")
        val `object` = detailsArray.optJSONObject(0)
        try {
            // Sales Header Add values
            /*  if (activityFrom.equals("InvoiceEdit")){
                rootJsonObject.put("invoiceNumber", AddInvoiceActivity.editInvoiceNumber);
                rootJsonObject.put("mode", "E");
            }else if (activityFrom.equals("ConvertInvoiceFromDO")){
                rootJsonObject.put("mode", "I");
                rootJsonObject.put("soNo","");
                rootJsonObject.put("doNo",AddInvoiceActivity.editDoNumber);
                rootJsonObject.put("invoiceNumber", "");
            }
            else if (activityFrom.equals("ConvertInvoice")){
                rootJsonObject.put("mode", "I");
                rootJsonObject.put("soNo",AddInvoiceActivity.editSoNumber);
                rootJsonObject.put("invoiceNumber", "");
            }else {
                rootJsonObject.put("invoiceNumber", "");
                rootJsonObject.put("mode", "I");
            }*/
            if (activityFrom == "SalesEdit") {
                rootJsonObject.put("soNumber", editSoNumber.toString())
                rootJsonObject.put("mode", "E")
                rootJsonObject.put("status", "O")
            } else {
                rootJsonObject.put("soNumber", "")
                rootJsonObject.put("mode", "I")
                rootJsonObject.put("status", "")
            }
            rootJsonObject.put("soDate", currentDate)
            rootJsonObject.put("currentDateTime", currentSaveDateTime)
            rootJsonObject.put("customerCode", `object`["customerCode"])
            rootJsonObject.put("customerName", `object`["customerName"])
            rootJsonObject.put("address", `object`["address"])
            rootJsonObject.put("street", `object`["street"])
            rootJsonObject.put("city", `object`["city"])
            rootJsonObject.put("creditLimit", `object`["creditLimit"])
            rootJsonObject.put("remark", remarkText!!.text.toString())
            rootJsonObject.put("currencyName", "Singapore Dollar")
            rootJsonObject.put("taxTotal", taxValueText!!.text.toString())
            rootJsonObject.put("subTotal", subTotalValue!!.text.toString())
            rootJsonObject.put("total", subTotalValue!!.text.toString())
            rootJsonObject.put("netTotal", netTotalValue!!.text.toString())
            rootJsonObject.put("itemDiscount", "0.00")
            rootJsonObject.put("billDiscount", "0.00")
            rootJsonObject.put("totalDiscount", "0")
            rootJsonObject.put("billDiscountPercentage", "0.00")
            rootJsonObject.put("deliveryCode", SettingUtils.getDeliveryAddressCode())
            rootJsonObject.put("delCustomerName", "")
            rootJsonObject.put("delAddress1", `object`.optString("delAddress1"))
            rootJsonObject.put("delAddress2 ", `object`.optString("delAddress2"))
            rootJsonObject.put("delAddress3 ", `object`.optString("delAddress3"))
            rootJsonObject.put("delPhoneNo", `object`.optString("contactNo"))
            rootJsonObject.put("remark", `object`.optString("remark"))
            rootJsonObject.put("haveTax", `object`.optString("haveTax"))
            rootJsonObject.put("taxType", `object`.optString("taxType"))
            rootJsonObject.put("taxPerc", `object`.optString("taxPercentage"))
            rootJsonObject.put("taxCode", `object`.optString("taxCode"))
            rootJsonObject.put("currencyCode", `object`.optString("currencyCode"))
            rootJsonObject.put("currencyValue", "")
            rootJsonObject.put("CurrencyRate", "1")
            rootJsonObject.put("postalCode", `object`.optString("postalCode"))
            rootJsonObject.put("createUser", username)
            rootJsonObject.put("modifyUser", username)
            rootJsonObject.put("companyName", companyName)
            rootJsonObject.put("stockUpdated", "1")
            rootJsonObject.put("invoiceType", "M")
            rootJsonObject.put("companyCode", companyCode)
            rootJsonObject.put("locationCode", locationCode)
            rootJsonObject.put("signature", signatureString)
            rootJsonObject.put("latitude", current_latitude)
            rootJsonObject.put("longitude", current_longitude)

            // Sales Details Add to the Objects
            val localCart = dbHelper!!.allInvoiceProducts
            var index = 1
            for (model in localCart) {
                invoiceObject = JSONObject()
                /*   if (activityFrom.equals("InvoiceEdit")){
                    rootJsonObject.put("invoiceNumber", AddInvoiceActivity.editInvoiceNumber);
                }else {
                    rootJsonObject.put("invoiceNumber", "");
                }*/invoiceObject.put("companyCode", companyCode)
                invoiceObject.put("invoiceDate", currentDate)
                invoiceObject.put("slNo", index)
                invoiceObject.put("productCode", model.productCode)
                invoiceObject.put("productName", model.productName)
                invoiceObject.put("qty", model.actualQty.toString())
                // convert into int
                invoiceObject.put("price", Utils.twoDecimalPoint(model.price.toDouble()))
                invoiceObject.put("total", Utils.twoDecimalPoint(model.total.toDouble()))
                invoiceObject.put("itemDiscount", "0.00")
                invoiceObject.put("totalTax", Utils.twoDecimalPoint(model.gstAmount.toDouble()))
                invoiceObject.put("subTotal", Utils.twoDecimalPoint(model.subTotal.toDouble()))
                invoiceObject.put("netTotal", Utils.twoDecimalPoint(model.netTotal.toDouble()))
                invoiceObject.put("taxType", `object`.optString("taxType"))
                invoiceObject.put("taxPerc", `object`.optString("taxPercentage"))
                var return_subtotal = 0.0
                if (model.returnQty != null && !model.returnQty.isEmpty() && model.returnQty != "null") {
                    return_subtotal = model.returnQty.toDouble() * model.price.toDouble()
                }
                assert(model.returnQty != null)
                if (!model.returnQty.isEmpty() && model.returnQty.toString() != "null") {
                    invoiceObject.put("returnLQty", model.returnQty)
                    invoiceObject.put("returnQty", model.returnQty)
                } else {
                    invoiceObject.put("returnLQty", "0")
                    invoiceObject.put("returnQty", "0")
                }
                if (!model.focQty.toString().isEmpty() && model.focQty != "null") {
                    invoiceObject.put("focQty", model.focQty)
                } else {
                    invoiceObject.put("focQty", "0")
                }
                invoiceObject.put("returnSubTotal", return_subtotal.toString() + "")
                invoiceObject.put("returnNetTotal", return_subtotal.toString() + "")
                invoiceObject.put("taxCode", `object`.optString("taxCode"))
                invoiceObject.put("uomCode", model.uomCode)
                invoiceObject.put("itemRemarks", "")
                invoiceObject.put("locationCode", locationCode)
                invoiceObject.put("createUser", username)
                invoiceObject.put("modifyUser", username)
                invoiceDetailsArray.put(invoiceObject)
                index++
            }
            rootJsonObject.put("PostingSalesOrderDetails", invoiceDetailsArray)
            Log.w("RootJsonForSave:", rootJsonObject.toString())
            saveSalesOrder(rootJsonObject, "SalesOrder", copy)
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.w("Given_Error:", Objects.requireNonNull(e.message)!!)
        }
    }

    fun viewCloseBottomSheet() {
        // hideKeyboard();
        productNameEditext!!.setText("")
        selectProductAdapter!!.notifyDataSetChanged()
        if (behavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
        } else {
            behavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }

        // Showing the Products Depending the Settings Values
        val settings = dbHelper!!.settings
        if (settings.size > 0) {
            for (model in settings) {
                if (model.settingName == "stock_view") {
                    if (model.settingValue == "1") {
                        stockProductView = "1"
                    } else if (model.settingValue == "2") {
                        stockProductView = "2"
                    } else {
                        stockProductView = "0"
                    }
                }
            }
        }
        // Filter the products list depending the Settings
        val filteredProducts = ArrayList<ProductsModel>()
        //   for (ProductsModel s : selectProductAdapter.getProductsList()) {
        if (productList != null && productList!!.size > 0) {
            for (product in productList!!) {
                if (stockProductView == "1") {
                    if (product.stockQty != null && product.stockQty != "null") {
                        if (product.stockQty.toDouble() > 0) {
                            filteredProducts.add(product)
                        }
                    }
                } else if (stockProductView == "0") {
                    if (product.stockQty != null && product.stockQty != "null") {
                        if (product.stockQty.toDouble() < 0 || product.stockQty.toDouble() == 0.0) {
                            filteredProducts.add(product)
                        }
                    }
                } else {
                    filteredProducts.add(product)
                }
            }
        }
        if (filteredProducts.size > 0) {
            productLayout!!.visibility = View.VISIBLE
            emptyLayout!!.visibility = View.GONE
        } else {
            productLayout!!.visibility = View.GONE
            emptyLayout!!.visibility = View.VISIBLE
        }
        productListView!!.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        selectProductAdapter = SelectProductAdapter(this, filteredProducts) { model ->
            productsModel = model
            productId = productsModel!!.productCode
            qtyValue!!.isEnabled = true
            // Need to implement the product price concept in SAP
            /*  try {
                        getProductPrice(productId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/if (model.minimumSellingPrice != null && !model.minimumSellingPrice.isEmpty()) {
            minimumSellingPriceText!!.text = model.minimumSellingPrice
        } else {
            minimumSellingPriceText!!.text = "0.00"
        }
            productName = productsModel!!.productName
            productAutoComplete!!.setText(model.productName + " - " + model.productCode)
            //  cartonPrice.setText(model.getUnitCost()+"");
            //  loosePrice.setText(model.getUnitCost());
            priceText!!.setText(model.unitCost)
            priceText!!.isEnabled = true
            uomText!!.setText(model.uomCode)
            stockCount!!.setText(model.stockQty)
            pcsPerCarton!!.setText(model.pcsPerCarton)
            qtyValue!!.isEnabled = true
            qtyValue!!.requestFocus()
            //  behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            openKeyborard(qtyValue)
            val jsonObject = JSONObject()
            try {
                jsonObject.put("CustomerCode", selectCustomerId)
                jsonObject.put("ItemCode", model.productCode)
                getUOM(jsonObject)
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.w("Errors:", Objects.requireNonNull(e.message!!))
            }
                // looseQtyValue.setEnabled(true);
            cartonPrice!!.isEnabled = true
            focEditText!!.isEnabled = true
            exchangeEditext!!.isEnabled = true
            discountEditext!!.isEnabled = true
            returnEditext!!.isEnabled = true

            //stockLayout.setVisibility(View.VISIBLE);
            if (model.stockQty != null && model.stockQty != "null") {
                pdtStockVal = model.stockQty

                if (model.stockQty.toDouble().equals("0") || model.stockQty.toDouble() < 0) {
                    stockQtyValue!!.text = model.stockQty
                    stockQtyValue!!.setTextColor(Color.parseColor("#D24848"))
                } else if (model.stockQty.toDouble() > 0) {
                    stockQtyValue!!.setTextColor(Color.parseColor("#2ECC71"))
                    stockQtyValue!!.text = model.stockQty
                }
            }
            viewCloseBottomSheet()
        }
        productListView!!.adapter = selectProductAdapter
        totalProducts!!.text = filteredProducts.size.toString() + " Products"
        // get the Customer name from the local db
    }

    fun openKeyborard(editText: EditText?) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun filter(text: String) {
        try {
            //new array list that will hold the filtered data
            val filterProducts = ArrayList<ProductsModel>()
            //looping through existing elements
            //   for (ProductsModel s : selectProductAdapter.getProductsList()) {
            for (s in AppUtils.getProductsList()) {
                //if the existing elements contains the search input
                if (s.productName.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault())) || s.productCode.lowercase(
                        Locale.getDefault()
                    ).contains(text.lowercase(Locale.getDefault()))
                ) {
                    //adding the element to filtered list
                    filterProducts.add(s)
                }
            }
            //calling a method of the adapter class and passing the filtered list
            selectProductAdapter!!.filterList(filterProducts)

            //setAdapter(filterProducts);
            totalProducts!!.text = filterProducts.size.toString() + " Products"
        } catch (ex: Exception) {
            Log.e("Error_in_filter", Objects.requireNonNull(ex.message)!!)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                /*   Intent intent = new Intent(this, CustomerListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/finish()
                true
            }

            R.id.action_save -> {
                val localCart = dbHelper!!.allInvoiceProducts
                if (localCart.size > 0) {
                    showSaveAlert()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Add the Product First...!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                true
            }
            R.id.action_scan_menu -> {
                scannedBarcode = ""
                val intent = Intent(this@NewSalesReturnProductAddActivity, BarCodeScanner::class.java)
                startActivityForResult(intent, RESULT_CODE)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
        //  return super.onOptionsItemSelected(item);
    }

    fun createInvoiceJson(copy: Int) {

        // JSONObject rootJsonObject = new JSONObject();
        val rootJsonObject = JSONObject()
        val invoiceDetailsArray = JSONArray()
        var returnProductArray = JSONArray()
        var returnProductObject = JSONObject()
        var invoiceObject = JSONObject()

        //  {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"CUS\/686","customerName":"VH FACTORY","groupCode":"100",
        //  "contactPerson":"","creditLimit":"150.000000","currencyCode":"SGD","currencyName":"Singapore Dollar","taxType":"","taxCode":"SR",
        //  "taxName":"Sales Standard Rated Supplier SR","taxPercentage":"7.000000","balance":"21.600000","outstandingAmount":"128.400000",
        //  "address":"","street":"","city":"","state":"","zipCode":"","country":"","createDate":"13\/07\/2021","updateDate":"30\/07\/2021",
        //  "active":"N","remark":""}]}
        val detailsArray = customerResponse.optJSONArray("responseData")
        val `object` = detailsArray.optJSONObject(0)
        try {
            // Sales Header Add values
            /*  if (activityFrom.equals("InvoiceEdit")){
                rootJsonObject.put("invoiceNumber", AddInvoiceActivity.editInvoiceNumber);
                rootJsonObject.put("mode", "E");
            }else if (activityFrom.equals("ConvertInvoiceFromDO")){
                rootJsonObject.put("mode", "I");
                rootJsonObject.put("soNo","");
                rootJsonObject.put("doNo",AddInvoiceActivity.editDoNumber);
                rootJsonObject.put("invoiceNumber", "");
            }
            else if (activityFrom.equals("ConvertInvoice")){
                rootJsonObject.put("mode", "I");
                rootJsonObject.put("soNo",AddInvoiceActivity.editSoNumber);
                rootJsonObject.put("invoiceNumber", "");
            }else {
                rootJsonObject.put("invoiceNumber", "");
                rootJsonObject.put("mode", "I");
            }*/
            if (activityFrom == "ConvertInvoice") {
                rootJsonObject.put("mode", "I")
                rootJsonObject.put("soNo", editSoNumber)
                rootJsonObject.put("invoiceNumber", "")
            } else {
                rootJsonObject.put("invoiceNumber", "")
                rootJsonObject.put("mode", "I")
            }
            rootJsonObject.put("currentDateTime", currentSaveDateTime)
            rootJsonObject.put("invoiceDate", currentDate)
            rootJsonObject.put("customerCode", `object`["customerCode"])
            rootJsonObject.put("customerName", `object`["customerName"])
            rootJsonObject.put("address", `object`["address"])
            rootJsonObject.put("street", `object`["street"])
            rootJsonObject.put("city", `object`["city"])
            rootJsonObject.put("creditLimit", `object`["creditLimit"])
            rootJsonObject.put("remark", remarkText!!.text.toString())
            rootJsonObject.put("currencyName", "Singapore Dollar")
            rootJsonObject.put("taxTotal", taxValueText!!.text.toString())
            rootJsonObject.put("subTotal", subTotalValue!!.text.toString())
            rootJsonObject.put("total", subTotalValue!!.text.toString())
            rootJsonObject.put("netTotal", netTotalValue!!.text.toString())
            rootJsonObject.put("itemDiscount", "0.00")
            rootJsonObject.put("billDiscount", "0.00")
            rootJsonObject.put("Paymode", "")
            rootJsonObject.put("ChequeDateString", "")
            rootJsonObject.put("BankCode", "")
            rootJsonObject.put("AccountNo", "")
            rootJsonObject.put("ChequeNo", "")

            /*  if (customerResponse.optString("CurrencyCode").equals("SGD")){
                rootJsonObject.put("FTotal", totalValue);
                rootJsonObject.put("FItemDiscount", itemDiscountAmount);
                rootJsonObject.put("FBillDiscount", billDiscountAmount);
                rootJsonObject.put("FSubTotal", subTotalValue);
                rootJsonObject.put("FTax", taxValue);
                rootJsonObject.put("FNetTotal", netTotalValue);
            }else {
                rootJsonObject.put("FTotal", "0");
                rootJsonObject.put("FItemDiscount", "0");
                rootJsonObject.put("FBillDiscount", "0");
                rootJsonObject.put("FSubTotal", "0");
                rootJsonObject.put("FTax", "0");
                rootJsonObject.put("FNetTotal", "0");
            }*/rootJsonObject.put("totalDiscount", "0")
            rootJsonObject.put("billDiscountPercentage", "0.00")
            rootJsonObject.put("deliveryCode", SettingUtils.getDeliveryAddressCode())
            rootJsonObject.put("delCustomerName", "")
            rootJsonObject.put("delAddress1", `object`.optString("delAddress1"))
            rootJsonObject.put("delAddress2 ", `object`.optString("delAddress2"))
            rootJsonObject.put("delAddress3 ", `object`.optString("delAddress3"))
            rootJsonObject.put("delPhoneNo", `object`.optString("contactNo"))
            rootJsonObject.put("remark", `object`.optString("remark"))
            rootJsonObject.put("haveTax", `object`.optString("haveTax"))
            rootJsonObject.put("taxType", `object`.optString("taxType"))
            rootJsonObject.put("taxPerc", `object`.optString("taxPercentage"))
            rootJsonObject.put("taxCode", `object`.optString("taxCode"))
            rootJsonObject.put("currencyCode", `object`.optString("currencyCode"))
            rootJsonObject.put("currencyValue", "")
            rootJsonObject.put("CurrencyRate", "1")
            rootJsonObject.put("status", "0")
            rootJsonObject.put("postalCode", `object`.optString("postalCode"))
            rootJsonObject.put("createUser", username)
            rootJsonObject.put("modifyUser", username)
            rootJsonObject.put("companyName", companyName)
            rootJsonObject.put("stockUpdated", "1")
            rootJsonObject.put("invoiceType", "M")
            rootJsonObject.put("companyCode", companyCode)
            rootJsonObject.put("locationCode", locationCode)
            rootJsonObject.put("signature", signatureString)
            rootJsonObject.put("latitude", current_latitude)
            rootJsonObject.put("longitude", current_longitude)

            // Sales Details Add to the Objects
            val localCart = dbHelper!!.allInvoiceProducts
            var index = 1
            for (model in localCart) {
                invoiceObject = JSONObject()
                /*   if (activityFrom.equals("InvoiceEdit")){
                    rootJsonObject.put("invoiceNumber", AddInvoiceActivity.editInvoiceNumber);
                }else {
                    rootJsonObject.put("invoiceNumber", "");
                }*/invoiceObject.put("invoiceNumber", "")
                invoiceObject.put("companyCode", companyCode)
                invoiceObject.put("invoiceDate", currentDate)
                invoiceObject.put("slNo", index)
                invoiceObject.put("productCode", model.productCode)
                invoiceObject.put("productName", model.productName)
                // convert into int
                invoiceObject.put("price", Utils.twoDecimalPoint(model.price.toDouble()))
                invoiceObject.put("total", Utils.twoDecimalPoint(model.total.toDouble()))
                invoiceObject.put("itemDiscount", "0.00")
                invoiceObject.put("totalTax", Utils.twoDecimalPoint(model.gstAmount.toDouble()))
                invoiceObject.put("subTotal", Utils.twoDecimalPoint(model.subTotal.toDouble()))
                invoiceObject.put("netTotal", Utils.twoDecimalPoint(model.netTotal.toDouble()))
                invoiceObject.put("taxType", `object`.optString("taxType"))
                invoiceObject.put("taxPerc", `object`.optString("taxPercentage"))
                var return_subtotal = 0.0
                if (model.returnQty != null && !model.returnQty.isEmpty() && model.returnQty != "null") {
                    return_subtotal = model.returnQty.toDouble() * model.price.toDouble()
                }
                assert(model.returnQty != null)
                if (!model.returnQty.isEmpty() && model.returnQty.toString() != "null") {
                    invoiceObject.put("returnLQty", model.returnQty)
                    invoiceObject.put("returnQty", model.returnQty)
                    invoiceObject.put("qty", model.actualQty.toString())
                } else {
                    invoiceObject.put("returnLQty", "0")
                    invoiceObject.put("returnQty", "0")
                    invoiceObject.put("qty", model.actualQty.toString())
                }
                if (!model.focQty.toString().isEmpty() && model.focQty != "null") {
                    invoiceObject.put("focQty", model.focQty)
                } else {
                    invoiceObject.put("focQty", "0")
                }
                invoiceObject.put("returnSubTotal", Utils.twoDecimalPoint(return_subtotal))
                invoiceObject.put("returnNetTotal", Utils.twoDecimalPoint(return_subtotal))
                invoiceObject.put("taxCode", `object`.optString("taxCode"))
                invoiceObject.put("returnReason", "")
                invoiceObject.put("uomCode", model.uomCode)
                invoiceObject.put("itemRemarks", "")
                invoiceObject.put("locationCode", locationCode)
                invoiceObject.put("createUser", username)
                invoiceObject.put("modifyUser", username)
                val returnProducts = dbHelper!!.getReturnProducts(model.productCode)
                returnProductArray = JSONArray()
                if (returnProducts.size > 0) {
                    for (returnProductsModel in returnProducts) {
                        Log.w(
                            "ReturnProductsValues:",
                            returnProductsModel.productCode + "-" + returnProductsModel.productName + "--" + returnProductsModel.returnQty
                        )
                        if (returnProductsModel.returnQty.toInt() > 0) {
                            returnProductObject = JSONObject()
                            returnProductObject.put(
                                "ReturnReason",
                                returnProductsModel.returnReason
                            )
                            returnProductObject.put("ReturnQty", returnProductsModel.returnQty)
                            returnProductArray.put(returnProductObject)
                        }
                    }
                }
                invoiceObject.put("ReturnDetails", returnProductArray)
                invoiceDetailsArray.put(invoiceObject)
                index++
            }
            rootJsonObject.put("PostingInvoiceDetails", invoiceDetailsArray)
            Log.w("RootJsonForSave:", rootJsonObject.toString())
            saveSalesOrder(rootJsonObject, "Invoice", copy)
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.w("Given_Error:", Objects.requireNonNull(e.message)!!)
        }
    }

    fun showSaveAlert() {
        try {
            // create an alert builder
            val builder = AlertDialog.Builder(this)
            // set the custom layout
            builder.setCancelable(false)
            val customLayout = layoutInflater.inflate(R.layout.invoice_save_option, null)
            builder.setView(customLayout)
            // add a button
            okButton = customLayout.findViewById(R.id.btn_ok)
            cancelButton = customLayout.findViewById(R.id.btn_cancel)
            invoicePrintCheck = customLayout.findViewById(R.id.invoice_print_check)
            saveMessage = customLayout.findViewById(R.id.save_message)
            saveTitle = customLayout.findViewById(R.id.save_title)
            signatureCapture = customLayout.findViewById(R.id.signature_capture)
            val noOfCopy = customLayout.findViewById<TextView>(R.id.no_of_copy)
            val copyPlus = customLayout.findViewById<Button>(R.id.increase)
            val copyMinus = customLayout.findViewById<Button>(R.id.decrease)
            val signatureButton = customLayout.findViewById<Button>(R.id.btn_signature)
            val copyLayout = customLayout.findViewById<LinearLayout>(R.id.print_layout)
            selectImagel = customLayout.findViewById(R.id.select_imageInv)

            selectImagel!!.setOnClickListener {
                if (selectImagel!!.getTag() == "view_image") {
                    showImage()
                } else {
                    selectImage()
                }
            }

            if (mPhotoFile != null && mPhotoFile!!.length() > 0) {
                selectImagel!!.setText("View Image")
                selectImagel!!.setTag("view_image")
            } else {
                selectImagel!!.setText("Select Image")
                selectImagel!!.setTag("select_image")
            }
            //invoicePrintCheck.setVisibility(View.GONE);
            if (activityFrom == "sales_return") {
                saveTitle!!.setText("Save Sales Return")
                saveMessage!!.setText("Are you sure want to save Sales Return?")
                invoicePrintCheck!!.setText("Sales Return Print")
                invoicePrintCheck!!.setChecked(false)
                isPrintEnable = false
            } else {
                invoicePrintCheck!!.setChecked(true)
                isPrintEnable = true
            }
            invoicePrintCheck!!.setOnClickListener(View.OnClickListener {
                if (invoicePrintCheck!!.isChecked()) {
                    isPrintEnable = true
                } else {
                    isPrintEnable = false
                }
            })
            okButton!!.setOnClickListener(View.OnClickListener { view1: View? ->
                try {
                    createSalesReturnJson()
                    alert!!.dismiss()
                } catch (exception: Exception) {
                }
            })
            cancelButton!!.setOnClickListener(View.OnClickListener { alert!!.dismiss() })
            copyPlus.setOnClickListener {
                val copyvalue = noOfCopy.text.toString()
                var copy = copyvalue.toInt()
                copy++
                noOfCopy.text = copy.toString() + ""
            }
            copyMinus.setOnClickListener {
                if (noOfCopy.text.toString() != "1") {
                    val copyvalue = noOfCopy.text.toString()
                    var copy = copyvalue.toInt()
                    copy--
                    noOfCopy.text = copy.toString() + ""
                }
            }
            signatureButton.setOnClickListener { showSignatureAlert() }

            // create and show the alert dialog
            alert = builder.create()
            alert!!.show()
        } catch (exception: Exception) {
        }
    }

    fun showSignatureAlert() {
        val alertDialog = AlertDialog.Builder(this)
        val customLayout = layoutInflater.inflate(R.layout.signature_layout, null)
        alertDialog.setView(customLayout)
        val acceptButton = customLayout.findViewById<Button>(R.id.buttonYes)
        val cancelButton = customLayout.findViewById<Button>(R.id.buttonNo)
        val clearButton = customLayout.findViewById<Button>(R.id.buttonClear)
        val mContent = customLayout.findViewById<LinearLayout>(R.id.signature_layout)
        acceptButton.isEnabled = false
        acceptButton.alpha = 0.4f
        val mSig = CaptureSignatureView(
            this@NewSalesReturnProductAddActivity,
            null
        ) {
            acceptButton.isEnabled = true
            acceptButton.alpha = 1f
        }
        mContent.addView(
            mSig,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        acceptButton.setOnClickListener { // byte[] signature = captureSignatureView.getBytes();
            val signature = mSig.bitmap
            signatureCapture!!.setImageBitmap(signature)
            signatureString = ImageUtil.convertBimaptoBase64(signature)
            Utils.setSignature(signatureString)
            signatureAlert!!.dismiss()
            Log.w("SignatureString:", signatureString)
        }
        cancelButton.setOnClickListener { signatureAlert!!.dismiss() }
        clearButton.setOnClickListener {
            signatureString = ""
            Utils.setSignature("")
            mSig.ClearCanvas()
        }
        signatureAlert = alertDialog.create()
        signatureAlert!!.setCanceledOnTouchOutside(false)
        signatureAlert!!.show()
    }

    fun saveSalesOrder(jsonBody: JSONObject, action: String, copy: Int) {
        try {
            pDialog = SweetAlertDialog(
                this@NewSalesReturnProductAddActivity,
                SweetAlertDialog.PROGRESS_TYPE
            )
            pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
            if (action == "SalesOrder") {
                pDialog!!.setTitleText("Saving Sales Order...")
            } else if (action == "DeliveryOrder") {
                pDialog!!.setTitleText("Saving Delivery Order...")
            } else {
                pDialog!!.setTitleText("Saving Invoice...")
            }
            pDialog!!.setCancelable(false)
            pDialog!!.show()
            val requestQueue = Volley.newRequestQueue(this)
            Log.w("GivenInvoiceRequest:", jsonBody.toString())
            var URL = ""
            URL = if (action == "SalesOrder") {
                Utils.getBaseUrl(this) + "PostingSalesOrder"
            } else if (action == "DeliveryOrder") {
                Utils.getBaseUrl(this) + "PostingDeliveryOrder"
            } else {
                Utils.getBaseUrl(this) + "PostingInvoice"
            }
            Log.w("Given_InvoiceApi:", URL)
            //    {"statusCode":2,"statusMessage":"Failed","responseData":{"docNum":null,"error":"Invoice :One of the base documents has already been closed  [INV1.BaseEntry][line: 1]"}}
            val salesOrderRequest: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST,
                URL,
                jsonBody,
                Response.Listener { response: JSONObject ->
                    Log.w("Invoice_ResponseSap:", response.toString())
                    Utils.clearCustomerSession(this)
                    AppUtils.setProductsList(null)
                    dbHelper!!.removeAllReturn()
                    // dbHelper.removeCustomer();
                    // {"statusCode":1,"statusMessage":"Invoice Created Successfully","responseData":{"docNum":"35","error":null}}
                    pDialog!!.dismiss()
                    val statusCode = response.optString("statusCode")
                    val message = response.optString("statusMessage")
                    var responseData: JSONObject? = null
                    try {
                        responseData = response.getJSONObject("responseData")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    if (statusCode == "1") {
                        if (action == "SalesOrder" || action == "SalesEdit") {
                            if (isPrintEnable) {
                                try {
                                    dbHelper!!.removeAllInvoiceItems()
                                    val `object` = response.optJSONObject("responseData")
                                    val doucmentNo = `object`.optString("docNum")
                                    //   String result=object.optString("Result");
                                    if (!doucmentNo.isEmpty()) {
                                        // getSalesOrderDetails(doucmentNo, copy);
                                        val intent =
                                            Intent(this, SalesOrderListActivity::class.java)
                                        intent.putExtra("printSoNumber", doucmentNo)
                                        intent.putExtra("noOfCopy", copy.toString())
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "Error in getting printing data",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        redirectActivity()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                dbHelper!!.removeAllInvoiceItems()
                                redirectActivity()
                            }
                            isPrintEnable = false
                        } /*else if (action.equals("DeliveryOrder") || action.equals("DeliveryOrderEdit")){
                        if (isPrintEnable){
                            // {"statusCode":1,"statusMessage":"Delivery Order Created Successfully","responseData":{"docNum":"11","error":null}}
                            try {
                                dbHelper.removeAllItems();
                                JSONObject object=response.optJSONObject("responseData");
                                String doucmentNo =object.optString("docNum");
                                //   String result=object.optString("Result");
                                if (!doucmentNo.isEmpty()) {
                                    getDoDetails(doucmentNo, copy);
                                }else {
                                    Toast.makeText(getApplicationContext(),"Error in getting printing data",Toast.LENGTH_SHORT).show();
                                    redirectActivity();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            dbHelper.removeAllItems();
                            redirectActivity();
                        }
                        isPrintEnable=false;
                    }*/ else {
                            if (isPrintEnable) {
                                try {
                                    //updateStockQty();
                                    dbHelper!!.removeAllInvoiceItems()
                                    val `object` = response.optJSONObject("responseData")
                                    val doucmentNo = `object`.optString("docNum")
                                    //   String result=object.optString("Result");
                                    if (!doucmentNo.isEmpty()) {
                                        getInvoicePrintDetails(doucmentNo, copy)
                                        val intent = Intent(
                                            applicationContext,
                                            NewInvoiceListActivity::class.java
                                        )
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "Error in getting printing data",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        redirectActivity()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                dbHelper!!.removeAllInvoiceItems()
                                redirectActivity()
                            }
                            isPrintEnable = false
                        }
                    } else {
//                    Log.w("ErrorValues:",responseData.optString("error"));
                        if (responseData != null) {
                            Toast.makeText(
                                applicationContext,
                                responseData.optString("error"),
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error in Saving Data...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    Log.w("SalesOrder_Response:", error.toString())
                    pDialog!!.dismiss()
                }) {
                /* @Override
                 public byte[] getBody() {
                     return jsonBody.toString().getBytes();
                 }*/
                override fun getBodyContentType(): String {
                    return "application/json"
                }

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
            salesOrderRequest.setRetryPolicy(object : RetryPolicy {
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
            requestQueue.add(salesOrderRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCustomerDetails(customerCode: String?, isloader: Boolean, from: String?) {
        // Initialize a new RequestQueue instance
        val requestQueue = Volley.newRequestQueue(this)
        // Initialize a new JsonArrayRequest instance
        val jsonObject = JSONObject()
        try {
            jsonObject.put("CustomerCode", customerCode)
            // jsonObject.put("CompanyCode",companyCode);
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.w("JsonValueForCustomer:", jsonObject.toString())
        val url = Utils.getBaseUrl(applicationContext) + "Customer"
        Log.w("Given_url:", url)
        val progressDialog = ProgressDialog(applicationContext)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Customer Details Loading...")
        if (isloader) {
            progressDialog.show()
        }
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST,
            url,
            jsonObject,
            Response.Listener { response: JSONObject ->
                try {
                    progressDialog.dismiss()
                    Log.w("SAP-response_customer:", response.toString())
                    val customerList = ArrayList<CustomerModel>()
                    val statusCode = response.optString("statusCode")
                    if (statusCode == "1") {
                        customerResponse = response
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
                            //  model.setCustomerBarcode(object.optString("BarCode"));
                            // model.setCustomerBarcode(String.valueOf(i));
                            if (`object`.optString("outstandingAmount") == "null" || `object`.optString(
                                    "outstandingAmount"
                                ).isEmpty()
                            ) {
                                model.outstandingAmount = "0.00"
                            } else {
                                model.outstandingAmount = `object`.optString("outstandingAmount")
                            }
                            customerList.add(model)
                            // }
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Error,in getting Customer list",
                            Toast.LENGTH_LONG
                        ).show()
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

    private fun setupGroup(itemGroupLists: ArrayList<ItemGroupList>) {
        val myAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemGroupLists)
        groupspinner!!.adapter = myAdapter
        groupspinner!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                val jsonObject = JSONObject()
                try {
                    val itemCode = itemGroup!![i].groupCode
                    Log.e("selectspinn", "" + itemCode)
                  //  if (itemCode != "Select Brand") {
                        jsonObject.put("User", username)
                        jsonObject.put("CardCode", customerCode)
                        jsonObject.put("ItemGroupCode", itemCode)
                        getAllProducts(jsonObject)
                  //  }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                return
            }
        }
    }
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile
                )
                mPhotoFile = photoFile
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    /**
     * Select image fro gallery
     */
    private fun dispatchGalleryIntent() {
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO)
    }
    fun showImage() {
        val builder = AlertDialog.Builder(this@NewSalesReturnProductAddActivity)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.image_view_layout, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.invoice_image)
        Glide.with(this)
            .load(mPhotoFile)
            .error(R.drawable.no_image_found)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            }).into(imageView)
        builder.setCancelable(false)
        builder.setTitle("Invoice Image")
        builder.setView(dialogView)
        builder.setNeutralButton(
            "NEW IMAGE"
        ) { dialogInterface, i -> selectImage() }
        builder.setPositiveButton(
            "OK"
        ) { dialog, which ->
            selectImagel!!.setTag("view_image")
            selectImagel!!.setText("View Image")
            dialog.dismiss()
        }.create().show()
    }

    fun selectImage() {
        val items = arrayOf<CharSequence>(
            "Take Photo",  /* "Choose from Library",*/
            "Cancel"
        )
        val builder = AlertDialog.Builder(this@NewSalesReturnProductAddActivity)
        builder.setItems(
            items
        ) { dialog: DialogInterface, item: Int ->
            if (items[item] == "Take Photo") {
                requestStoragePermission(true)
            } //else if (items[item].equals("Choose from Library")) {
            else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun requestStoragePermission(isCamera: Boolean) {
        var permission = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.CAMERA
            )
        }
        Dexter.withContext(this)
            .withPermissions(
                permission
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        if (isCamera) {
                            dispatchTakePictureIntent()
                        } else {
                            dispatchGalleryIntent()
                        }
                    }
                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .withErrorListener { error: DexterError? ->
                Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT)
                    .show()
            }
            .onSameThread()
            .check()
    }

    @get:Throws(JSONException::class)
    private val grouplist: ArrayList<ItemGroupList>
        private get() {
            val requestQueue = Volley.newRequestQueue(this)
            val url = Utils.getBaseUrl(applicationContext) + "ItemGroupList"
            // Initialize a new JsonArrayRequest instance
            Log.w("Given_url_gup_brand:", url)
            //    pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            //   pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            //   pDialog.setTitleText("Loading Groups...");
            //  pDialog.setCancelable(false);
            //  pDialog.show();
            itemGroup = ArrayList()
            //  itemGroup.add(new ItemGroupList("Select Brand", "Select Brand"));
            val jsonObjectRequest: JsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null,
                    Response.Listener { response: JSONObject ->
                        try {
                            Log.w("group_brandlist:", response.toString())

                            // pDialog.dismiss();
                            val statusCode = response.optString("statusCode")
                            val statusMessage = response.optString("statusMessage")
                            if (statusCode == "1") {
                                val groupArray = response.optJSONArray("responseData")
                                for (i in 0 until groupArray.length()) {
                                    val jsonObject = groupArray.getJSONObject(i)
                                    val groupName = jsonObject.getString("itemGroupName")
                                    val groupCode = jsonObject.getString("itemGroupCode")
                                    val itemGroupList = ItemGroupList(groupCode, groupName)
                                    itemGroup!!.add(itemGroupList)
                                }
                                if (itemGroup!!.size > 0) {
                                    setupGroup(itemGroup!!)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.w("Error4:", Objects.requireNonNull(e.message!!))
                        }
                    }, Response.ErrorListener { error: VolleyError ->
                        // Do something when error occurred
                        //  pDialog.dismiss();
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
            return itemGroup!!
        }

    fun redirectActivity() {
        val intent: Intent
        intent = if (activityFrom == "iv" || activityFrom == "ConvertInvoice") {
            Intent(this@NewSalesReturnProductAddActivity, NewInvoiceListActivity::class.java)
        } else {
            Intent(this@NewSalesReturnProductAddActivity, SalesOrderListActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val count = dbHelper!!.numberOfRowsInInvoice()
            if (count > 0) {
                showDeleteAlert()
            } else {
                finish()
            }
            return true
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
    }

    fun showDeleteAlert() {
        val builder1 = AlertDialog.Builder(this@NewSalesReturnProductAddActivity)
        builder1.setMessage("Data Will be Cleared are you sure want to back?")
        builder1.setCancelable(false)
        builder1.setPositiveButton(
            "Yes"
        ) { dialog, id ->
            dbHelper!!.removeAllInvoiceItems()
            dbHelper!!.removeAllReturn()
            finish()
            dialog.cancel()
        }
        builder1.setNegativeButton(
            "No"
        ) { dialog, id -> dialog.cancel() }
        val alert11 = builder1.create()
        alert11.show()
    }

    @Throws(JSONException::class)
    private fun getInvoicePrintDetails(invoiceNumber: String, copy: Int) {
        // Initialize a new RequestQueue instance
        val jsonObject = JSONObject()
        // jsonObject.put("CompanyCode", companyId);
        jsonObject.put("InvoiceNo", invoiceNumber)
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "InvoiceDetails"
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url)
        //  pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        // pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        // pDialog.setTitleText("Processing please wait...");
        // pDialog.setCancelable(false);
        // pDialog.show();
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
                            //  model.setAddress(object.optString("street"));
                            model.address =
                                `object`.optString("address1") + `object`.optString("address2") + `object`.optString(
                                    "address3"
                                )
                            model.address1 = `object`.optString("address1")
                            model.address2 = `object`.optString("address2")
                            model.address3 = `object`.optString("address3")
                            model.addressstate =
                                ( `object`.optString("street") + " "+
                                        `object`.optString("block") + " " + `object`.optString("city"))
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
                            model.allowDeliveryAddress = response.optString("showShippingAddress")
                            model.deliveryAddress =
                                `object`.optString("shipAddress2") + `object`.optString("shipAddress3") + `object`.optString(
                                    "shipStreet"
                                )
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
                                val invoiceListModel = InvoicePrintPreviewModel.InvoiceList()
                                invoiceListModel.productCode = detailObject.optString("productCode")
                                invoiceListModel.description = detailObject.optString("productName")
                                invoiceListModel.lqty = detailObject.optString("unitQty")
                                invoiceListModel.cqty = detailObject.optString("cartonQty")
                                invoiceListModel.netQty = detailObject.optString("quantity")
                                invoiceListModel.netQuantity = detailObject.optString("netQuantity")
                                invoiceListModel.focQty = detailObject.optString("foc_Qty")
                                invoiceListModel.returnQty = detailObject.optString("returnQty")
                                invoiceListModel.cartonPrice = detailObject.optString("cartonPrice")
                                invoiceListModel.unitPrice = detailObject.optString("price")
                                invoiceListModel.uomCode = detailObject.optString("uomCode")
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
                            printInvoice(copy)
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

    private fun createSignature() {
        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
            try {
                ImageUtil.saveStamp(this, Utils.getSignature(), "Signature")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun printInvoice(copy: Int) {
        try {
            /* if (pDialog!=null && pDialog.isShowing()){
                pDialog.dismiss();
            }*/
            val printerUtils = PrinterUtils(this, printerMacId)
            printerUtils.printInvoice(copy, invoiceHeaderDetails, invoicePrintList, "false")
            Utils.setSignature("")
        } catch (e: Exception) {
        }
    }

    @Throws(JSONException::class)
    private fun getSalesOrderDetails(soNumber: String, copy: Int) {
        // Initialize a new RequestQueue instance
        val jsonObject = JSONObject()
        //  jsonObject.put("CompanyCode",companyId);
        jsonObject.put("SalesOrderNo", soNumber)
        // jsonObject.put("LocationCode",locationCode);
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "SalesOrderDetails"
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_url:", url)
        //   pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        //   pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        //  pDialog.setTitleText("Generating Print Preview...");
        //  pDialog.setCancelable(false);
        //  pDialog.show();
        salesOrderHeaderDetails = ArrayList()
        salesPrintList = ArrayList()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            jsonObject,
            Response.Listener { response: JSONObject ->
                try {
                    // {"statusCode":1,"statusMessage":"Success","responseData":[{"customerCode":"WinApp","customerName":"WinApp","soNumber":"3",
                    // "soStatus":"O","soDate":"6\/8\/2021 12:00:00 am","netTotal":"26.750000","balanceAmount":"26.750000",
                    // "totalDiscount":"0.000000","paidAmount":"0.000000","contactPersonCode":"","createDate":"7\/8\/2021 12:00:00 am",
                    // "updateDate":"7\/8\/2021 12:00:00 am","remark":"","fDocTotal":"0.000000","fTaxAmount":"0.000000",
                    // "receivedAmount":"0.000000","total":"26.750000","fTotal":"0.000000","iTotalDiscount":"0.000000",
                    // "taxTotal":"1.750000","iPaidAmount":"0.000000","currencyCode":"SGD","currencyName":"Singapore Dollar",
                    // "companyCode":"WINAPP_DEMO","docEntry":"3","address1":"SingaporeShipTo1   Changi 890323 SG","taxPercentage":"0.000000",
                    // "discountPercentage":"0.000000",
                    //
                    //
                    // "salesOrderDetails":[{"slNo":"1","companyCode":"WINAPP_DEMO","soNo":"3",
                    // "productCode":"FG\/001245","productName":"Milk","quantity":"5.000000","cartonQty":"1.000000",
                    // "price":"5.000000","currency":"SGD","taxRate":"0.000000","discountPercentage":"0.000000",
                    // "lineTotal":"26.750000","fRowTotal":"0.000000","warehouseCode":"01","salesEmployeeCode":"-1","accountCode":"400000",
                    // "taxStatus":"Y","unitPrice":"5.000000","customerCategoryNo":"","barCodes":"","totalTax":"1.750000",
                    // "fTaxAmount":"0.000000","taxCode":"","taxType":"E","taxPerc":"0.000000","uoMCode":null,"soDate":"6\/8\/2021 12:00:00 am",
                    // "dueDate":"6\/8\/2021 12:00:00 am","createDate":"7\/8\/2021 12:00:00 am","updateDate":"7\/8\/2021 12:00:00 am",
                    // "createdUser":"manager","uomCode":"Ctn","uoMName":"Carton","cartonPrice":"3000.000000","piecePrice":"0.000000",
                    // "pcsPerCarton":"100.000000","lPrice":"100.000000","unitQty":"1.000000","retailPrice":"100.000000"}]}]}
                    Log.w("Sales_Details:", response.toString())
                    val statusCode = response.optString("statusCode")
                    if (statusCode == "1") {
                        val responseData = response.getJSONArray("responseData")
                        val `object` = responseData.optJSONObject(0)
                        val model = SalesOrderPrintPreviewModel()
                        model.soNumber = `object`.optString("soNumber")
                        model.soDate = `object`.optString("soDate")
                        model.customerCode = `object`.optString("customerCode")
                        model.customerName = `object`.optString("customerName")
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
                        model.taxType = `object`.optString("taxType")
                        model.taxValue = `object`.optString("taxPerc")
                        model.outStandingAmount = `object`.optString("outstandingAmount")
                        model.billDiscount = `object`.optString("billDiscount")
                        model.itemDiscount = `object`.optString("totalDiscount")
                        Utils.setInvoiceMode("SalesOrder")
                        val signFlag = `object`.optString("signFlag")
                        if (signFlag == "Y") {
                            val signature = `object`.optString("signature")
                            Utils.setSignature(signature)
                            createSignature()
                        } else {
                            Utils.setSignature("")
                        }
                        val detailsArray = `object`.optJSONArray("salesOrderDetails")
                        for (i in 0 until detailsArray.length()) {
                            val detailObject = detailsArray.optJSONObject(i)
                            var salesListModel = SalesList()
                            salesListModel.productCode = detailObject.optString("productCode")
                            salesListModel.description = detailObject.optString("productName")
                            salesListModel.lqty = detailObject.optString("unitQty")
                            salesListModel.cqty = detailObject.optString("cartonQty")
                            salesListModel.netQty = detailObject.optString("quantity")
                            salesListModel.cartonPrice = detailObject.optString("cartonPrice")
                            salesListModel.unitPrice = detailObject.optString("price")
                            val qty1 = detailObject.optString("quantity").toDouble()
                            val price1 = detailObject.optString("price").toDouble()
                            val nettotal1 = qty1 * price1
                            salesListModel.total = nettotal1.toString()
                            salesListModel.pricevalue = price1.toString()
                            salesListModel.uomCode = detailObject.optString("uomCode")
                            salesListModel.pcsperCarton = detailObject.optString("pcsPerCarton")
                            salesListModel.itemtax = detailObject.optString("totalTax")
                            salesListModel.subTotal = detailObject.optString("subTotal")
                            salesPrintList!!.add(salesListModel)
                            if (!detailObject.optString("ReturnQty")
                                    .isEmpty() && detailObject.optString("ReturnQty").toDouble() > 0
                            ) {
                                salesListModel = SalesList()
                                salesListModel.productCode = detailObject.optString("ProductCode")
                                salesListModel.description = detailObject.optString("ProductName")
                                salesListModel.lqty = detailObject.optString("LQty")
                                salesListModel.cqty = detailObject.optString("CQty")
                                salesListModel.netQty = "-" + detailObject.optString("ReturnQty")
                                val qty12 = detailObject.optString("ReturnQty").toDouble()
                                val price12 = detailObject.optString("Price").toDouble()
                                val nettotal12 = qty12 * price12
                                salesListModel.total = nettotal12.toString()
                                salesListModel.pricevalue = price12.toString()
                                salesListModel.uomCode = detailObject.optString("UOMCode")
                                salesListModel.cartonPrice = detailObject.optString("CartonPrice")
                                salesListModel.unitPrice = detailObject.optString("Price")
                                salesListModel.pcsperCarton = detailObject.optString("PcsPerCarton")
                                salesListModel.itemtax = detailObject.optString("Tax")
                                salesListModel.subTotal = detailObject.optString("subTotal")
                                salesPrintList!!.add(salesListModel)
                            }
                            model.salesList = salesPrintList
                            salesOrderHeaderDetails!!.add(model)
                        }
                        sentSalesOrderDataPrint(copy)
                        // pDialog.dismiss();
                    } else {
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
    fun getUOM(jsonObject: JSONObject) {
        // Initialize a new RequestQueue instance
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "ItemUOMDetails"
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_UOM_URL:", url + jsonObject.toString())
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.setTitleText("Loading UOM...")
        pDialog.setCancelable(false)
        pDialog.show()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            jsonObject,
            Response.Listener { response: JSONObject ->
                try {
                    uomList = ArrayList()
                    Log.w("Res_UOM:", response.toString())
                    // Loop through the array elements
                    val uomArray = response.optJSONArray("responseData")
                    if (uomArray != null && uomArray.length() > 0) {
                        for (j in 0 until uomArray.length()) {
                            val uomObject = uomArray.getJSONObject(j)
                            val uomModel = UomModel()
                            uomModel.uomCode = uomObject.optString("uomCode")
                            uomModel.uomName = uomObject.optString("uomName")
                            uomModel.uomEntry = uomObject.optString("uomEntry")
                            uomModel.altQty = uomObject.optString("altQty")
                            uomModel.baseQty = uomObject.optString("baseQty")
                            uomModel.price = uomObject.optString("price")
                            uomList!!.add(uomModel)
                        }
                    }
                    Log.w("UOM_TEXT:", uomArray.toString())
                    pDialog.dismiss()
                    if (uomList!!.size > 0) {
                        runOnUiThread {
                            if(ischangeUOM) {
                                setUomList(uomList!!)
                            }else{
                                setUomList(uomList!!)
                                //  defaultUOMset(uomList!!)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.w("Errory:", Objects.requireNonNull(e.message!!))
                }
            },
            Response.ErrorListener { error: VolleyError ->
                // Do something when error occurred
                pDialog.dismiss()
                Log.w("Error_throwing:", error.toString())
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
    fun getUOMEdit(jsonObject: JSONObject, uomcode:String, pdtStockStr :String) {
        // Initialize a new RequestQueue instance
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "ItemUOMDetails"
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_UOM_URL:", url + jsonObject.toString())
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.setTitleText("Loading UOM...")
        pDialog.setCancelable(false)
        pDialog.show()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            jsonObject,
            Response.Listener { response: JSONObject ->
                try {
                    uomListEdit = ArrayList()
                    Log.w("Res_UOM:", response.toString())
                    // Loop through the array elements
                    val uomArray = response.optJSONArray("responseData")
                    if (uomArray != null && uomArray.length() > 0) {
                        for (j in 0 until uomArray.length()) {
                            val uomObject = uomArray.getJSONObject(j)
                            val uomModel = UomModel()
                            uomModel.uomCode = uomObject.optString("uomCode")
                            uomModel.uomName = uomObject.optString("uomName")
                            uomModel.uomEntry = uomObject.optString("uomEntry")
                            uomModel.altQty = uomObject.optString("altQty")
                            uomModel.baseQty = uomObject.optString("baseQty")
                            uomModel.price = uomObject.optString("price")
                            uomListEdit!!.add(uomModel)
                        }
                    }
                    Log.w("UOM_TEXT:", uomArray.toString())
                    pDialog.dismiss()
                    var pdtStockl = 0.0
                    if (uomListEdit!!.size > 0) {
                        for (i in uomListEdit!!.indices) {
                            if (uomcode.equals("CTN", true)) {
                               // stockLayout!!.visibility = View.VISIBLE
                                var baseCtnQty = uomListEdit!![i].baseQty.toDouble()
                              //  pdtStockl = pdtStockStr.toDouble()
                              //  var ctnStockVal = pdtStockl / baseCtnQty!!
                               //// stockQtyValue!!.setTextColor(Color.parseColor("#2ECC71"))

                              //  stockQtyValue!!.setText(Utils.twoDecimalPoint(ctnStockVal).toString())
                                Log.w("baseCtnQtyEditaa", "" + baseCtnQty)
                            } else {
                               // stockLayout!!.visibility = View.VISIBLE
                               // stockQtyValue!!.setTextColor(Color.parseColor("#2ECC71"))

                               // stockQtyValue!!.text = pdtStockStr
                            }
                        }
//                        runOnUiThread {
//                            if(ischangeUOM) {
//                                setUomList(uomList!!)
//                            }else{
//                                defaultUOMset(uomList!!)
//                            }
//                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.w("Errory:", Objects.requireNonNull(e.message!!))
                }
            },
            Response.ErrorListener { error: VolleyError ->
                // Do something when error occurred
                pDialog.dismiss()
                Log.w("Error_throwing:", error.toString())
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
    private fun setUomList(uomList: ArrayList<UomModel>) {
        Log.w("UOMList:", uomList.toString())
        val adapter = ArrayAdapter(this, R.layout.cust_spinner_item, uomList)
        uomSpinner!!.adapter = adapter
        setUOMCode(uomList)

//        if (productsModel != null) {
//            //setuom
//            Log.d("cg_uomsiz1", uomList.size.toString() + "")
//                Log.w("cg_uomsiz", uomList.size.toString() + "")
//                if (activityFrom == "iv" || activityFrom == "ConvertInvoice" ||
//                    activityFrom == "Duplicate" || activityFrom == "ConvertInvoiceFromDO"
//                    || activityFrom == "ReOrderInvoice"
//                ) {
//                    UOMdetail(settingUOMInvval!!,uomList)
//                }
//            if (activityFrom == "so" || activityFrom == "SalesEdit" || activityFrom == "ReOrderSales" ) {
//                UOMdetail(settingUOMSOval!!,uomList)
//            }
//
//            }

    }
    private fun setUOMCode(uomList: ArrayList<UomModel>) {
        uomSpinner!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
//                if (isEditItem) {
//                    selectValue(uomList, uomName, uomPriceEd!!)
//                    Log.w("uomentyy:", uomName + "kk"+uomPriceEd)
//
//                } else {
                uomName = uomSpinner!!.selectedItem.toString()
                uomText!!.setText(uomName)
                Log.w("uomentyy:", uomName+pdtStockVal)
                if(uomName.equals("CTN",true)) {
                    var ctnStockVal = 0.00
                    var baseCtnQty = uomList[position].baseQty.toDouble()

                   // var pdtStock = pdtStockVal!!.toDouble()

                    //ctnStockVal = pdtStock / baseCtnQty
                   // stockQtyValue!!.setText(Utils.twoDecimalPoint(ctnStockVal).toString())
                    Log.w("ctnStockaa",""+ctnStockVal+".."+ Utils.twoDecimalPoint(ctnStockVal).toString())
                    Log.w("ctnStock22",""+baseCtnQty)
                }
                else{
                 //   stockQtyValue!!.setText(pdtStockVal.toString())
                }
                uomText!!.setText(uomList[position].uomCode)
                priceText!!.setText(uomList[position].price)
                Log.w("uomentyy1:", uomName)

                Log.w("UOMQtyValue:", uomList[position].uomEntry)
                Log.w("SelectedUOM:", uomName + "")
                // }
                qtyValue!!.setText("")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_CODE) {
                val barcodeText = data!!.extras!!.getString("Contents")
                Log.w("BarcodeTextInv:", barcodeText!!)
                val mp = MediaPlayer.create(this, R.raw.beep) // sound is inside res/raw/mysound
                mp.start()
                scannedBarcode = barcodeText
                searchAndSendActivity(barcodeText)
            }
            else if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    mPhotoFile = mCompressor!!.compressToFile(mPhotoFile)
                    imageString = ImageUtil.getBase64StringImage(mPhotoFile)
                    //  Log.w("GivenImage1:",imageString);
                    Utils.w("GivenImage1Inv", imageString)
                    showImage()
                } catch (e: IOException) {
                    e.printStackTrace()
                }


                /* Glide.with(MainActivity.this)
                        .load(mPhotoFile)
                        .apply(new RequestOptions().centerCrop()
                                .circleCrop()
                                .placeholder(R.drawable.profile_pic_place_holder))
                        .into(imageViewProfilePic);*/
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                val selectedImage = data!!.data
                try {
                    mPhotoFile =
                        mCompressor!!.compressToFile(File(getRealPathFromUri(selectedImage)))
                    selectImagel!!.setText(selectedImage.toString())
                    imageString = ImageUtil.getBase64StringImage(mPhotoFile)
                    // Log.w("GivenImage2:",imageString);
                    Utils.w("GivenImage2Inv", imageString)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need Permissions")
        builder.setMessage(
            "This app needs permission to use this feature. You can grant them in app settings."
        )
        builder.setPositiveButton("GOTO SETTINGS") { dialog: DialogInterface, which: Int ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog: DialogInterface, which: Int -> dialog.cancel() }
        builder.show()
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.setData(uri)
        startActivityForResult(intent, 101)
    }

    /**
     * Create file with current timestamp name
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp =
            SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(mFileName, ".jpg", storageDir)
    }

    /**
     * Get real file path from URI
     */
    fun getRealPathFromUri(contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = contentResolver.query(contentUri!!, proj, null, null, null)
            assert(cursor != null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }
    fun searchAndSendActivity(barcode: String?) {
        try {
            val model = getProductData(barcode)
            if (model != null) {
                if (productSummaryList != null && productSummaryList.size > 0) {
                    if (!isAlreadyExist(barcode)) {
                        Toast.makeText(applicationContext, "Product Found...", Toast.LENGTH_SHORT)
                            .show()
                        Log.w("entypdt",""+model.barcode)
                        setProductDetails(model)
                        //  addItem(model);

                    }
                } else {
                    Toast.makeText(applicationContext, "Product Found...", Toast.LENGTH_SHORT)
                        .show()
                    Log.w("entypdt2",""+model.barcode)
                    //addItem(model);
                    setProductDetails(model)
                    // setProductDetails(model)
                }
            } else {
                //  showBarcodeAlert(barcode);
                Toast.makeText(applicationContext, "No Product found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
        }
    }
    private fun setProductDetails(model: ProductsModel) {
        productsModel = model
        productId = productsModel!!.productCode
        Log.w("pdtsInv1", "" + model.productName + "  .. " + model.barcode)
        Log.w("pdtsInvCode", "" + model.productCode)
        // setUomList(model.getProductUOMList());
       // uomTextView!!.text = model.uomText
//        if (isUomSetting) {
//            val jsonObject = JSONObject()
//            try {
//                jsonObject.put("CustomerCode", selectCustomerId)
//                jsonObject.put("ItemCode", model.productCode)
//                getUOM(jsonObject)
//            } catch (e: JSONException) {
//                e.printStackTrace()
//                Log.w("Errord:", Objects.requireNonNull(e.message!!))
//            }
//        }

        // Need to implement the product price concept in SAP
        /*  try {
                    getProductPrice(productId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/if (model.minimumSellingPrice != null && !model.minimumSellingPrice.isEmpty()) {
            minimumSellingPriceText!!.text = model.minimumSellingPrice
        } else {
            minimumSellingPriceText!!.text = "0.00"
        }
        productName = productsModel!!.productName
        productAutoComplete!!.setText(model.productName + " - " + model.productCode)
        //  cartonPrice.setText(model.getUnitCost()+"");
        //  loosePrice.setText(model.getUnitCost());
        if (model.lastPrice != null && !model.lastPrice.isEmpty() && model.lastPrice.toDouble() > 0.00) {
            priceText!!.setText(model.lastPrice)
        } else {
            priceText!!.setText(model.unitCost)
        }
        // uomText.setText(model.getUomCode());
        stockCount!!.setText(model.stockQty)
        pcsPerCarton!!.setText(model.pcsPerCarton)
        qtyValue!!.isEnabled = true
        qtyValue!!.requestFocus()
        //  behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        openKeyborard(qtyValue)

        // looseQtyValue.setEnabled(true);
        cartonPrice!!.isEnabled = true
//        if(isFOCStr.equals("Yes")){
//            focEditText!!.isEnabled = true
//        }
//        else{
            focEditText!!.isEnabled = false
        //}
        exchangeEditext!!.isEnabled = true
        discountEditext!!.isEnabled = true
        returnEditext!!.isEnabled = true
        stockLayout!!.visibility = View.VISIBLE
        if (model.stockQty != null && model.stockQty != "null") {
            pdtStockVal = model.stockQty
            if (model.stockQty.toDouble() == 0.0 || model.stockQty.toDouble() < 0) {
                stockQtyValue!!.text = model.stockQty
                stockQtyValue!!.setTextColor(Color.parseColor("#D24848"))
            } else if (model.stockQty.toDouble() > 0) {
                stockQtyValue!!.setTextColor(Color.parseColor("#2ECC71"))
                stockQtyValue!!.text = model.stockQty
            }
        }
    }
    fun getProductData(keyId: String?): ProductsModel? {
        var index = 0
        if (AppUtils.getProductsList() != null && AppUtils.getProductsList().size > 0) {
            for (model in AppUtils.getProductsList()) {
                //   Log.w("pdtsbarcodd", "" + keyId + "  .. "+AppUtils.getProductsList().get(1).barcode)

                if (keyId == model.barcode || keyId == model.productCode) {
                    Log.w("pdtsbarcodd1", "" + keyId + "  .. "+model.barcode)

                    return model
                }
                index++
            }
        }
        return null
    }
    private fun isAlreadyExist(scannedBarcode: String?): Boolean {
        var check = false
        try {
            for (pdt in productSummaryAdapter!!.getList()) {
                if (pdt.productBarCode.trim { it <= ' ' } == scannedBarcode!!.trim { it <= ' ' }) {
                    Toast.makeText(
                        applicationContext,
                        "This Products Already Added..",
                        Toast.LENGTH_SHORT
                    ).show()
                    check = true
                    break
                }
            }
        } catch (exception: Exception) {
        }
        return check
    }

    fun createSalesReturnJson() {
        val c = Calendar.getInstance().time
        println("Current time => $c")
        val df = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        currentDateString = df.format(c)
        val df3 = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        currentDate = df3.format(c)
        val rootJsonObject = JSONObject()
        val srHeaderDetails = JSONObject()
        val srBatchDetails = JSONObject()
        var returnProductArray = JSONArray()
        val saleDetailsArray = JSONArray()
        var returnProductObject = JSONObject()
        val srBatchArray = JSONArray()
        var saleObject = JSONObject()

        //  salesReturnNo = SalesReturnList.salesReturnNo;


        // {"SRNumber":"","Mode":"I","SRDate":"20210827","customerCode":"C1006","customerName":"A S KUMAR","address":"","street":"","city":"",
        // "creditLimit":"0.000000","remark":"","currencyName":"Singapore Dollar","taxTotal":"3.50","subTotal":"50.00","total":"50.00",
        // "netTotal":"53.50","itemDiscount":"0.00","billDiscount":"0.00","totalDiscount":null,"billDiscountPercentage":"0","deliveryCode":"0",
        // "DelCustomerName":"","delAddress1":"","delAddress2":null,"delAddress3":null,"delPhoneNo":"","haveTax":"","taxType":"Z","taxPerc":"",
        // "taxCode":"ZR","currencyCode":"SGD","currencyValue":"","CurrencyRate":"1","status":"","postalCode":"","createUser":"User1",
        // "modifyUser":"User1","companyName":null,"stockUpdated":null,"SOType":null,"companyCode":"AATHI_LIVE_DB","locationCode":"HQ","DONo":null,
        // "signature":"",
        //
        // "PostingDeliveryOrderDetails":[{"companyCode":"AATHI_LIVE_DB","SRDate":"20210827","slNo":"1","productCode":"SKU-AAFZ-0001",
        // "productName":"ASHOKA BHATURA 325 GM","cartonQty":"5","unitQty":"0","qty":"5.0","pcsPerCarton":"1","price":"10.00","cartonPrice":"10.00",
        // "retailPrice":"10.00","total":"50.0","itemDiscount":"0","totalTax":"3.50","subTotal":"50.00","netTotal":"53.50","taxType":"Z","taxPerc":"",
        // "returnLQty":"0","returnQty":"0","focQty":"0","exchangeQty":"0","returnSubTotal":"0.0","returnNetTotal":"0.0","taxCode":"ZR","uomCode":"PCS",
        // "itemRemarks":"","locationCode":"01","createUser":"User1","modifyUser":"User1"}]}
        try {
            // Sales Header Add values
            val detailsArray = customerResponse.optJSONArray("responseData")
            val `object` = detailsArray.optJSONObject(0)

            /* if (activityFrom.equals("SREdit")){
                rootJsonObject.put("SRNumber", AddInvoiceActivity.editSoNumber);
                rootJsonObject.put("mode", "E");
                rootJsonObject.put("status", "O");
            }else {
                rootJsonObject.put("soNumber", "");
                rootJsonObject.put("mode", "I");
                rootJsonObject.put("status", "");
            }*/rootJsonObject.put("SRNumber", "")
            rootJsonObject.put("mode", "I")
            rootJsonObject.put("status", "")
            rootJsonObject.put("SRDate", currentDateString)
            rootJsonObject.put("currentDateTime", currentSaveDateTime)
            rootJsonObject.put("customerCode", `object`.optString("customerCode"))
            rootJsonObject.put("customerName", `object`.optString("customerName"))
            rootJsonObject.put("address", `object`.optString("address"))
            rootJsonObject.put("street", `object`.optString("street"))
            rootJsonObject.put("city", `object`.optString("city"))
            rootJsonObject.put("creditLimit", `object`.optString("creditLimit"))
            rootJsonObject.put("remark", "")
            rootJsonObject.put("currencyName", "Singapore Dollar")
            rootJsonObject.put("total", netTotalValue!!.text.toString())
            rootJsonObject.put("itemDiscount", "0.00")
            rootJsonObject.put("billDiscount", "0.00")
            rootJsonObject.put("billDiscountPercentage", "0.00")
            rootJsonObject.put("subTotal", subTotalValue!!.text.toString())
            rootJsonObject.put("taxTotal", taxValueText!!.text.toString())
            rootJsonObject.put("netTotal", netTotalValue!!.text.toString())
            rootJsonObject.put("DeliveryCode", SettingUtils.getDeliveryAddressCode())
            rootJsonObject.put("delCustomerName", "")
            rootJsonObject.put("currentDateTime", Utils.getCurrentDateTime())
            rootJsonObject.put("delAddress1", `object`.optString("delAddress1"))
            rootJsonObject.put("delAddress2 ", `object`.optString("delAddress2"))
            rootJsonObject.put("delAddress3 ", `object`.optString("delAddress3"))
            rootJsonObject.put("delPhoneNo", `object`.optString("contactNo"))
            rootJsonObject.put("haveTax", `object`.optString("haveTax"))
            rootJsonObject.put("taxType", `object`.optString("taxType"))
            rootJsonObject.put("taxPerc", `object`.optString("taxPercentage"))
            rootJsonObject.put("taxCode", `object`.optString("taxCode"))
            rootJsonObject.put("currencyCode", `object`.optString("currencyCode"))
            rootJsonObject.put("currencyValue", "")
            rootJsonObject.put("currencyRate", "1")
            rootJsonObject.put("postalCode", `object`.optString("postalCode"))
            rootJsonObject.put("createUser", username)
            rootJsonObject.put("modifyUser", username)
            rootJsonObject.put("companyCode", companyCode)
            rootJsonObject.put("locationCode", locationCode)
            rootJsonObject.put("signature", signatureString)
            rootJsonObject.put("latitude", current_latitude)
            rootJsonObject.put("longitude", current_longitude)
            rootJsonObject.put("uomCode", "PCS")

            // Sales Details Add to the Objects
            val localCart = dbHelper!!.allInvoiceProducts
            Log.w("Given_local_cart_size:", localCart.size.toString())

            //  "PostingSalesOrderDetails":[{"companyCode":"WINAPP_DEMO","soDate":"20210806","slNo":1,"productCode":"FG\/001245","productName":
            //  "RUM","cartonQty":"0","unitQty":"5","qty":"5.0","pcsPerCarton":"100","price":"5.00","cartonPrice":"500","retailPrice":"500",
            //  "total":"25.0","itemDiscount":"0","totalTax":"0.0","subTotal":"25.0","netTotal":"25.00","taxType":"I","taxPerc":"","returnLQty":"0",
            //  "returnQty":"0","focQty":"0","exchangeQty":"0","returnSubTotal":"0.0","returnNetTotal":"0.0","taxCode":"","uomCode":"Ctn",
            //  "itemRemarks":"","locationCode":"01","createUser":"User1","modifyUser":"User1"}]}
            var index = 1
            for (model in localCart) {
                saleObject = JSONObject()
                saleObject.put("companyCode", companyCode)
                saleObject.put("slNo", index)
                saleObject.put("productCode", model.productCode)
                saleObject.put("productName", model.productName)
                saleObject.put("cartonQty", model.actualQty)
                saleObject.put("unitQty", model.actualQty)
                saleObject.put("qty", model.actualQty.toString())
                saleObject.put("price", Utils.twoDecimalPoint(model.price.toDouble()))
                saleObject.put("pcsPerCarton", "1")
                saleObject.put("cartonPrice", "0.00")
                saleObject.put("total", Utils.twoDecimalPoint(model.total.toDouble()))
                saleObject.put("itemDiscount", "0.00")
                saleObject.put("itemDiscountPercentage", "0")
                saleObject.put("totalTax", model.gstAmount)
                saleObject.put("subTotal", Utils.twoDecimalPoint(model.subTotal.toDouble()))
                saleObject.put("netTotal", Utils.twoDecimalPoint(model.netTotal.toDouble()))
                saleObject.put("taxType", `object`.optString("taxType"))
                saleObject.put("taxPerc", `object`.optString("taxPercentage"))
                saleObject.put("focQty", "0")
                var return_subtotal = 0.0
                if (model.actualQty != null && !model.actualQty.isEmpty() && model.actualQty != "null") {
                    return_subtotal = model.actualQty.toDouble() * model.price.toDouble()
                }
                assert(model.actualQty != null)
                if (!model.actualQty.isEmpty() && model.actualQty.toString() != "null") {
                    saleObject.put("returnLQty", model.actualQty)
                    saleObject.put("returnQty", model.actualQty)
                } else {
                    saleObject.put("returnLQty", "0")
                    saleObject.put("returnQty", "0")
                }
                saleObject.put("exchangeQty", "0")
                saleObject.put("returnSubTotal", return_subtotal.toString() + "")
                saleObject.put("returnNetTotal", return_subtotal.toString() + "")
                saleObject.put("taxCode", `object`.optString("taxCode"))
                //                saleObject.put("uomCode",model.getUomCode());
                saleObject.put("uomCode", model.uomCode)
                saleObject.put("retailPrice", "0.00")
                saleObject.put("DamageStock", "")
                saleObject.put("itemRemarks", "")
                saleObject.put("locationCode", locationCode)
                saleObject.put("createUser", username)
                saleObject.put("modifyUser", username)
                val returnProducts = dbHelper!!.getReturnProducts(model.productCode)
                returnProductArray = JSONArray()
                if (returnProducts.size > 0) {
                    for (returnProductsModel in returnProducts) {
                        Log.w(
                            "ReturnProductsValues:",
                            returnProductsModel.productCode + "-" + returnProductsModel.productName + "--" + returnProductsModel.returnQty
                        )
                        if (returnProductsModel.returnQty != null && !returnProductsModel.returnQty.isEmpty() && returnProductsModel.returnQty.toInt() > 0) {
                            returnProductObject = JSONObject()
                            returnProductObject.put(
                                "ReturnReason",
                                returnProductsModel.returnReason
                            )
                            returnProductObject.put("ReturnQty", returnProductsModel.returnQty)
                            returnProductArray.put(returnProductObject)
                        }
                    }
                }
                saleObject.put("ReturnDetails", returnProductArray)
                saleDetailsArray.put(saleObject)
                index++
            }
            rootJsonObject.put("PostingSalesReturnDetails", saleDetailsArray)
            Log.w("RootSalesReturn:", rootJsonObject.toString())
            saveSalesReturn(rootJsonObject, 1)
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.w("Given_Error:", Objects.requireNonNull(e.message)!!)
        }
    }

    fun saveSalesReturn(jsonBody: JSONObject, noofCopyPrint: Int) {
        try {
            pDialog = SweetAlertDialog(
                this@NewSalesReturnProductAddActivity,
                SweetAlertDialog.PROGRESS_TYPE
            )
            pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog!!.setTitleText("Saving Sales Return...")
            pDialog!!.setCancelable(false)
            pDialog!!.show()
            val requestQueue = Volley.newRequestQueue(this@NewSalesReturnProductAddActivity)
            Log.w("GivenSalesReturn:", jsonBody.toString())
            val URL = Utils.getBaseUrl(applicationContext) + "PostingSalesReturn"
            Log.w("Given_SalesReturnApi:", URL)
            val salesOrderRequest: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST,
                URL,
                jsonBody,
                Response.Listener { response: JSONObject ->
                    Log.w("Sales_returnResponse:", response.toString())
                    try {
                        //   {"statusCode":1,"statusMessage":"Sales Return Created Successfully","responseData":{"docNum":"1000005","error":null}}
                        val statusCode = response.optString("statusCode")
                        val statusMessage = response.optString("statusMessage")
                        val jsonObject = response.optJSONObject("responseData")
                        val salesReturnNumber = jsonObject.optString("docNum")
                        var errorMessage = ""
                        val error = jsonObject.optString("error")
                        if (error != "null") {
                            errorMessage = error
                        }
                        if (statusCode == "1") {
                            dbHelper!!.removeAllItems()
                            dbHelper!!.removeCustomer()
                            dbHelper!!.removeAllReturn()
                            Utils.clearCustomerSession(applicationContext)
                            AppUtils.setProductsList(null)
                            pDialog!!.dismiss()
                            SalesReturnList.isEdit = false
                            if (isPrintEnable) {
                                val intent = Intent(
                                    applicationContext,
                                    NewSalesReturnListActivity::class.java
                                )
                                intent.putExtra("srNumber", salesReturnNumber)
                                intent.putExtra("noOfCopy", noofCopyPrint.toString())
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(
                                    applicationContext,
                                    NewSalesReturnListActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            pDialog!!.dismiss()
                            Toast.makeText(
                                applicationContext,
                                "$statusMessage : $errorMessage",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    Log.w("SR_Error:", error.toString())
                    pDialog!!.dismiss()
                }) {
                override fun getBody(): ByteArray {
                    return jsonBody.toString().toByteArray()
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }

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
            salesOrderRequest.setRetryPolicy(object : RetryPolicy {
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
            requestQueue.add(salesOrderRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun sentSalesOrderDataPrint(copy: Int) {
        if (printerType == "TSC Printer") {
            val printer =
                TSCPrinter(this@NewSalesReturnProductAddActivity, printerMacId, "SalesOrder")
            printer.printSalesOrder(copy, salesOrderHeaderDetails, salesPrintList)
            printer.setOnCompletionListener {
                Utils.setSignature("")
                Toast.makeText(
                    applicationContext,
                    "SalesOrder printed successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (printerType == "Zebra Printer") {
            val zebraPrinterActivity =
                ZebraPrinterActivity(this@NewSalesReturnProductAddActivity, printerMacId)
            zebraPrinterActivity.printSalesOrder(copy, salesOrderHeaderDetails, salesPrintList)
        }
    }

    fun setNewPrint() {
        val printerUtils = PrinterUtils(this, printerMacId)
        printerUtils.printLabel()
    }

    companion object {
        var productList: ArrayList<ProductsModel>? = null
        var behavior: BottomSheetBehavior<*>? = null
        var progressDialog: ProgressDialog? = null
        var stockProductView = "2"
        var barcodeText: EditText? = null
        var customerCode: String? = null
        var isPrintEnable = false
        var selectedBank: TextView? = null
        var amountText: EditText? = null
        var currentLocationLatitude = 0.0
        var currentLocationLongitude = 0.0
        var signatureString = ""
        var imageString: String? = ""
        var current_latitude = "0.00"
        var current_longitude = "0.00"
        var currentDate: String? = null
        var customerResponse = JSONObject()
        private var currentDateString: String? = null
        var salesReturnNo: String? = null
        @RequiresApi(api = Build.VERSION_CODES.O)
        private fun convertDate(strDate: String): String {
            @SuppressLint("SimpleDateFormat") val inputFormat: DateFormat =
                SimpleDateFormat("dd-MM-yyyy")
            @SuppressLint("SimpleDateFormat") val outputFormat: DateFormat =
                SimpleDateFormat("yyyyMMdd")
            var resultDate = ""
            try {
                resultDate = outputFormat.format(inputFormat.parse(strDate))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return resultDate
        }
    }
}