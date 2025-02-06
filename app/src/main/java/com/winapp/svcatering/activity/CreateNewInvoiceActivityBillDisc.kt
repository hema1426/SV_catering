package com.winapp.svcatering.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.winapp.svcatering.R
import com.winapp.svcatering.adapter.DuplicateInvoiceAdapter
import com.winapp.svcatering.adapter.NewProductSummaryAdapter
import com.winapp.svcatering.adapter.SelectProductAdapter
import com.winapp.svcatering.db.DBHelper
import com.winapp.svcatering.model.AppUtils
import com.winapp.svcatering.model.CreateInvoiceModel
import com.winapp.svcatering.model.CustomerDetails
import com.winapp.svcatering.model.CustomerModel
import com.winapp.svcatering.model.DeliveryAddressModel
import com.winapp.svcatering.model.DuplicateInvoiceDetail
import com.winapp.svcatering.model.DuplicateInvoiceModel
import com.winapp.svcatering.model.DuplicateSRDetails
import com.winapp.svcatering.model.HomePageModel
import com.winapp.svcatering.model.InvoicePrintPreviewModel
import com.winapp.svcatering.model.ItemGroupList
import com.winapp.svcatering.model.ProductSummaryModel
import com.winapp.svcatering.model.ProductsModel
import com.winapp.svcatering.model.SalesOrderPrintPreviewModel
import com.winapp.svcatering.model.SalesOrderPrintPreviewModel.SalesList
import com.winapp.svcatering.model.SettingsModel
import com.winapp.svcatering.model.UomModel
import com.winapp.svcatering.thermalprinter.PrinterUtils
import com.winapp.svcatering.utils.CaptureSignatureView
import com.winapp.svcatering.utils.Constants
import com.winapp.svcatering.utils.ImageUtil
import com.winapp.svcatering.utils.SessionManager
import com.winapp.svcatering.utils.SettingUtils
import com.winapp.svcatering.utils.Utils
import com.winapp.svcatering.utils.Utils.twoDecimalPoint
import com.winapp.svcatering.zebraprinter.TSCPrinter
import com.winapp.svcatering.zebraprinter.ZebraPrinterActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects

class CreateNewInvoiceActivityBillDisc : AppCompatActivity() {

    private var returnLayout: LinearLayout? = null
    private var showHideButton: ImageView? = null
    private var productSummaryAdapter: NewProductSummaryAdapter? = null
    private val productSummaryList: ArrayList<ProductSummaryModel>? = null
    private var productSummaryView: RecyclerView? = null
    private var companyCode: String? = null
    private var username: String? = null
    private var user: HashMap<String, String>? = null
    private var session: SessionManager? = null
    private var pDialog: SweetAlertDialog? = null
    private var productListView: RecyclerView? = null
    private var selectProductAdapter: SelectProductAdapter? = null
    private var btnCancel: Button? = null
    private var searchProduct: ImageView? = null
    private var productNameEditext: EditText? = null
    private var transLayout: LinearLayout? = null
    private var totalProducts: TextView? = null
    private var products: ArrayList<String>? = null
    private var productAutoComplete: AutoCompleteTextView? = null
    private var autoCompleteAdapter: ArrayAdapter<String>? = null
    private var cartonPrice: EditText? = null
    private var loosePrice: EditText? = null
    private var uomText: EditText? = null
    private var pcsPerCarton: EditText? = null
    private var stockCount: EditText? = null
    private var qtyValue: EditText? = null
    private var returnQtyText: EditText? = null
    private var customerNameText: EditText? = null
    private var priceText: EditText? = null
    private var subTotalValue: TextView? = null
    private var taxValueText: TextView? = null
    private var netTotalValue: TextView? = null
    private var customerDetails: ArrayList<CustomerDetails>? = null
    private var dbHelper: DBHelper? = null
    private var taxTitle: TextView? = null
    private var addProduct: Button? = null
    private var productsModel: ProductsModel? = null
    private var itemCount: TextView? = null
    private var noproductText: TextView? = null
    private var productId: String? = null
    private var productName: String? = null
    var isCartonQtyEdit = false
    var isQtyEdit = false
    var net_total_value = 0.0
    private val qtyTextWatcher: TextWatcher? = null
    private val cartonTextWatcher: TextWatcher? = null
    private val lqtyTextWatcher: TextWatcher? = null
    private val cartonPriceTextWatcher: TextWatcher? = null
    private var loosePriceTextWatcher: TextWatcher? = null
    private var focSwitch: Switch? = null
    private var exchangeSwitch: Switch? = null
    private var returnSwitch: Switch? = null
    private var focEditText: EditText? = null
    private var downArrow_inv_img: ImageView? = null
    private var hide_disc_layl: LinearLayout? = null
    private var exchangeEditext: EditText? = null
    private var discountEditext: EditText? = null
    private var item_discount_ed: EditText? = null
    private var discount_layInv: LinearLayout? = null
    private var bill_disc_percent_ed: EditText? = null
    private var bill_disc_amt_ed: EditText? = null
    private var bill_disc_layl: View? = null
    var isBillDiscountTouch = false
    var isBillPercentageTouch = false
    protected var billDiscAmountTextWatcher: TextWatcher? = null
    protected var billDiscPercentageTextWatcher: TextWatcher? = null
    var billDiscountPercentage = "0"
    var billDiscountAmount = "0.00"
    private var downarrow_billLayl: ImageView? = null
    private var prod_bottom_layout: LinearLayout? = null
    private var bill_disc_subtotal_txtl: TextView? = null
    private var disc_limit_txtl: TextView? = null
    private var exchange_inv: EditText? = null
    private var discountStr = "0"
    private val exchangeStr = "0"
    var bottomSheetDialog: BottomSheetDialog? = null
    private var returnEditext: EditText? = null
    private var scanProduct: ImageView? = null
    var RESULT_CODE = 12
    private var locationCode: String? = null
    private var stockQtyValue: TextView? = null
    private var stockLayout: LinearLayout? = null
    private val cqtyTW: TextWatcher? = null
    private val lqtyTW: TextWatcher? = null
    private var qtyTW: TextWatcher? = null
    private val beforeLooseQty: String? = null
    private val ss_Cqty: String? = null
    var isAllowLowStock = false
    var isCheckedCreditLimit = false

    private var focLayout: LinearLayout? = null
    private var sortButton: FloatingActionButton? = null
    private var emptyLayout: LinearLayout? = null
    private var productLayout: LinearLayout? = null
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
    private var signatureAlert: AlertDialog? = null
    var invoicePrintCheck: CheckBox? = null
    var companyName: String? = null
    var remarkStr: String? = null
    private var printerMacId: String? = null
    private var printerType: String? = null
    private var sharedPreferences: SharedPreferences? = null
    private var remarkText: EditText? = null
    private var activityFrom: String? = ""
    var saveTitle: TextView? = null
    private var saveMessage: TextView? = null
    private var editSoNumber: String? = ""
    private var duplicateInvNo: String? = ""
    private var editDoNumber: String? = ""
    private var orderNo: String? = ""
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
    private var itemGroup: ArrayList<ItemGroupList>? = null
    private var groupspinner: Spinner? = null
    private var currentSaveDateTime: String? = ""
    private var invoiceHeaderDetails: ArrayList<InvoicePrintPreviewModel>? = null
    private var invoicePrintList: ArrayList<InvoicePrintPreviewModel.InvoiceList>? = null
    private var salesReturnList: ArrayList<InvoicePrintPreviewModel.SalesReturnList>? = null
    private var salesOrderHeaderDetails: ArrayList<SalesOrderPrintPreviewModel>? = null

    private var duplicateInvoiceModel: ArrayList<DuplicateInvoiceModel>? = null
    private var duplicateInvoiceDetailList: ArrayList<DuplicateInvoiceDetail>? = null
    private var duplicateInvSRList: ArrayList<DuplicateSRDetails>? = null

    private var salesPrintList: ArrayList<SalesList>? = null
    private val signatureLayout: LinearLayout? = null
    var signatureCapture: ImageView? = null
    private var creditLimitAmount = "0.00"
    private var outstandingAmount = "0.00"
    var isCheckedInvoicePrint = false
    var isCheckedSalesPrint = false
    var isUomSetting = false
    var isDeliveryAddrSetting = false
    var isSignatureSetting = false
    var isDiscountSetting = false
    var isDiscountApiSetting = false
    var minimum_disc_amt = 0.00
    var maximum_disc_amt = 0.00
    var settings: ArrayList<SettingsModel>? = null
    var save_btn: MenuItem? = null
    var down_menu: MenuItem? = null
    var b = false
    private var uomSpinner: Spinner? = null
    private var delivery_addrSpinner: Spinner? = null
    private var uomList: ArrayList<UomModel>? = null
    private var deliveryAddrList: ArrayList<DeliveryAddressModel>? = null
    private val uomCode = ""
    private var uomName = ""
    private var deliverAddrNameStr = ""
    var uomTextView: TextView? = null
    private var isEditItem = false
    private var orderNoText: EditText? = null
    private var uomSpinnerLayl: LinearLayout? = null
    var duplicateAdapter: DuplicateInvoiceAdapter? = null
    private var customerCodeStr: String? = ""
    private var insertStatusDup: Boolean? = false
    var uomTxtTitl: TextView? = null
    private var billDiscApiStr: String? = "0.00"

    @SuppressLint("LogNotTimber", "ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_invoice)

        supportActionBar!!.title = "Invoice"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        session = SessionManager(this)
        user = session!!.userDetails
        dbHelper = DBHelper(this)
        progressDialog = ProgressDialog(this)
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
        uomTxtTitl = findViewById(R.id.uomTxtTitle)
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
        uomSpinner = findViewById(R.id.uom_spinner)
        delivery_addrSpinner = findViewById(R.id.delivery_addr_spinner)
        uomTextView = findViewById(R.id.uom_txt)
        orderNoText = findViewById(R.id.order_no)
        //signatureLayout=findViewById(R.id.signature_layout);
        uomSpinnerLayl = findViewById(R.id.uomSpinnerLay)
        downArrow_inv_img = findViewById(R.id.downArrow_inv_lay)
        hide_disc_layl = findViewById(R.id.hide_disc_lay)
        item_discount_ed = findViewById(R.id.discount_invoice)
        exchange_inv = findViewById(R.id.exchange_invoice)
        discount_layInv = findViewById(R.id.discount_layInv)
        disc_limit_txtl = findViewById(R.id.disc_limit_txt)
        bill_disc_layl = findViewById(R.id.bill_discLay_Inv)
        downarrow_billLayl = findViewById(R.id.downarrow_billLay)
        prod_bottom_layout = findViewById(R.id.prod_bottom_layout)
        bill_disc_percent_ed = findViewById(R.id.bill_disc_percent_inv)
        bill_disc_amt_ed = findViewById(R.id.bill_disc_amt_inv)
        bill_disc_subtotal_txtl = findViewById(R.id.bill_disc_subtotal_txt)


        settings = ArrayList()
        settings = dbHelper!!.settings
        Log.w("compcode..", "" + companyCode)
        Log.w(
            "salesmann..", "" + user!!.get(SessionManager.KEY_SALESMAN_NAME) + ".." +
                    user!!.get(SessionManager.KEY_SALESMAN_PHONE)
        );

        products = ArrayList()
        productAutoComplete!!.clearFocus()
        barcodeText!!.requestFocus()
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE)
        printerType = sharedPreferences!!.getString("printer_type", "")
        printerMacId = sharedPreferences!!.getString("mac_address", "")
        productAutoComplete!!.setEnabled(false)

        downArrow_inv_img!!.setOnClickListener(View.OnClickListener {
            if (downArrow_inv_img!!.getTag() == "hide") {
                hide_disc_layl!!.setVisibility(View.VISIBLE)
                downArrow_inv_img!!.setTag("show")
                Utils.slideUp(hide_disc_layl)
                downArrow_inv_img!!.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            } else {
                Utils.slideDown(hide_disc_layl)
                hide_disc_layl!!.setVisibility(View.GONE)
                downArrow_inv_img!!.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                downArrow_inv_img!!.setTag("hide")
            }
        })
        val printerUtils = PrinterUtils(this, printerMacId)
        //  printerUtils.connectPrinter();
        focEditText!!.setEnabled(false)
        exchangeEditext!!.setEnabled(false)
        discountEditext!!.setEnabled(false)
        returnEditext!!.setEnabled(false)
        //        qtyValue.setEnabled(false);
//        priceText.setEnabled(false);
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
            duplicateInvNo = intent.getStringExtra("duplicateInvNo")
            customerCodeStr = intent.getStringExtra("customerDupCode")
            billDiscApiStr = intent.getStringExtra("customerBillDisc")

            Log.w("billdiscpi_inv::", billDiscApiStr!!)

            currentSaveDateTime = intent.getStringExtra("currentDateTime")
            editDoNumber = intent.getStringExtra("editDoNumber")
            orderNo = intent.getStringExtra("orderNo")
            Log.w("GivenActivityFrom::", activityFrom.toString())
            if (activityFrom == "iv") {
                supportActionBar!!.setTitle("Invoice")
                priceText!!.setEnabled(true)
                downArrow_inv_img!!.setVisibility(View.VISIBLE)
            } else if (activityFrom == "ConvertInvoice") {
                priceText!!.setEnabled(true)
                orderNoText!!.setText(orderNo)
                supportActionBar!!.setTitle("Covert Invoice -$editSoNumber")
            } else if (activityFrom == "SalesEdit") {
                priceText!!.setEnabled(true)
                orderNoText!!.setText(orderNo)
                returnQtyText!!.setVisibility(View.GONE)
                supportActionBar!!.setTitle("SalesOrder Edit")
            } else if (activityFrom == "so") {
                priceText!!.setEnabled(true)
                returnQtyText!!.setVisibility(View.GONE)
                supportActionBar!!.setTitle("SalesOrder")
            } else if (activityFrom == "do") {
                priceText!!.setEnabled(true)
                returnQtyText!!.setVisibility(View.GONE)
                returnAdj!!.setVisibility(View.GONE)
                supportActionBar!!.setTitle("Delivery Order")
            } else if (activityFrom == "doEdit") {
                priceText!!.setEnabled(true)
                orderNoText!!.setText(orderNo)
                returnQtyText!!.setVisibility(View.GONE)
                returnAdj!!.setVisibility(View.GONE)
                supportActionBar!!.setTitle("Delivery Order Edit")
            } else if (activityFrom == "Duplicate") {
                priceText!!.setEnabled(true)
                downArrow_inv_img!!.setVisibility(View.VISIBLE)
                //  getDuplicateInvoiceDetails(duplicateInvNo!!)
                // orderNoText.setText(orderNo);
//                returnQtyText.setVisibility(View.GONE);
//                returnAdj.setVisibility(View.GONE);
                supportActionBar!!.setTitle("Invoice - $duplicateInvNo")
            }
        } else {
            orderNoText!!.setText("")
        }
        val settings = dbHelper!!.settings
        if (settings != null) {
            if (settings.size > 0) {
                for (model in settings) {
                    if (model.settingName == "invSwitch") {
                        Log.w("SettingNameI:", model.settingName)
                        Log.w("SettingValueI:", model.settingValue)
                        isCheckedInvoicePrint = if (model.settingValue == "1") {
                            true
                        } else {
                            false
                        }
                    } else if (model.settingName == "salesSwitch") {
                        Log.w("SettingNameI:", model.settingName)
                        Log.w("SettingValueI:", model.settingValue)
                        isCheckedSalesPrint = if (model.settingValue == "1") {
                            true
                        } else {
                            false
                        }
                    } else if (model.settingName == "UomSwitch") {
                        Log.w("SettingNameI:", model.settingName)
                        Log.w("SettingValueI:", model.settingValue)
                        isUomSetting = if (model.settingValue == "1") {
                            true
                        } else {
                            false
                        }
                    } else if (model.settingName == "deliveryAddressSwitch") {
                        Log.w("SettingNameI:", model.settingName)
                        Timber.w(model.settingValue)
                        isDeliveryAddrSetting = if (model.settingValue == "1") {
                            true
                        } else {
                            false
                        }
                    } else if (model.settingName == "discountSwitch") {
                        Log.w("SettingNameDis:", model.settingName)
                        Log.w("SettingValueDis:", model.settingValue)
                        isDiscountSetting = if (model.settingValue == "1") {
                            true
                        } else {
                            false
                        }
                    } else if (model.settingName == "signatureSwitch") {
                        Log.w("SettingNameSign:", model.settingName)
                        Log.w("SettingValueSign:", model.settingValue)
                        isSignatureSetting = if (model.settingValue == "1") {
                            true
                        } else {
                            false
                        }
                    } else if (model.settingName == "showDiscountAmount") {
                        Log.w("SettingNamedisc:", model.settingName)
                        Log.w("SettingValuedisc:", model.settingValue)
                        if (model.settingValue.equals("True", ignoreCase = true)) {
                            isDiscountApiSetting = true
                        } else {
                            isDiscountApiSetting = false
                        }
                    } else if (model.settingName == "discountAmountValidationFrom") {
                        Log.w("SettingNamediscmini:", model.settingName)
                        Log.w("SettingValuediscmini:", model.settingValue)
                        minimum_disc_amt = model.settingValue.toDouble()
                    } else if (model.settingName == "discountAmountValidationTo") {
                        Log.w("SettingNamediscmax:", model.settingName)
                        Log.w("SettingValuediscmax:", model.settingValue)
                        maximum_disc_amt = model.settingValue.toDouble()
                    }

                }
            }
        }
        Log.w("settingValdiss", "$maximum_disc_amt  .. $minimum_disc_amt" + isDiscountApiSetting)

        Log.w("settingVal", "$isDiscountSetting  .. $isSignatureSetting")
        if (activityFrom == "iv" || activityFrom == "ConvertInvoice" || activityFrom == "Duplicate") {
            downArrow_inv_img!!.setVisibility(View.VISIBLE)
            if (isDiscountSetting) {
                discount_layInv!!.setVisibility(View.VISIBLE)
            } else {
                discount_layInv!!.setVisibility(View.GONE)
            }
        }
        if (isDiscountApiSetting) {
//            disc_limit_txtl!!.setText("Mini value "+minimum_disc_amt+" to maxi value "+maximum_disc_amt)
            disc_limit_txtl!!.setText("Discount value " + minimum_disc_amt + " to " + maximum_disc_amt)

        } else {
            item_discount_ed!!.isEnabled = false
            disc_limit_txtl!!.setText("No discount")
        }
        if (isUomSetting) {
            uomSpinnerLayl!!.setVisibility(View.VISIBLE)
            uomTxtTitl!!.visibility = View.VISIBLE
        } else {
            uomSpinnerLayl!!.setVisibility(View.GONE)
            uomTxtTitl!!.visibility = View.INVISIBLE
        }
        if (isDeliveryAddrSetting) {
            delivery_addrSpinner!!.setVisibility(View.VISIBLE)
        } else {
            delivery_addrSpinner!!.setVisibility(View.GONE)
        }
        val jsonObject = JSONObject()
        try {
            jsonObject.put("User", username)
            jsonObject.put("CardCode", customerCode)
            getAllProducts(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObj = JSONObject()
        try {
            jsonObj.put("CustomerCode", customerCode)
            getDeliveryAddress(jsonObj)
        } catch (e: JSONException) {
            throw RuntimeException(e)
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
        val bottomSheet = findViewById<View>(R.id.design_bottom_sheet)
        bottomSheet.setBackgroundColor(Color.parseColor("#F2F2F2"))
        behavior = BottomSheetBehavior.from(bottomSheet)
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

        /*  signatureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignatureAlert();
            }
        });*/btnCancel!!.setOnClickListener(View.OnClickListener { viewCloseBottomSheet() })
        searchProduct!!.setOnClickListener(View.OnClickListener { viewCloseBottomSheet() })

        downarrow_billLayl!!.setOnClickListener {
            billDischide()
        }

        bill_disc_amt_ed!!.setOnTouchListener(View.OnTouchListener
        { view, motionEvent ->
            isBillDiscountTouch = true
            isBillPercentageTouch = false
            bill_disc_percent_ed!!.removeTextChangedListener(billDiscPercentageTextWatcher)
            bill_disc_amt_ed!!.addTextChangedListener(billDiscAmountTextWatcher)
            false
        })
//
//
        bill_disc_percent_ed!!.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            isBillPercentageTouch = true
            isBillDiscountTouch = false
            bill_disc_amt_ed!!.removeTextChangedListener(billDiscAmountTextWatcher)
            bill_disc_percent_ed!!.addTextChangedListener(billDiscPercentageTextWatcher)
            false
        })


        billDiscAmountTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Log.w("BeforeTextChanged:", "ErrorShowing")
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {

                val subtotal: Double = getSubtotal()
                Log.w("SubTotalAmtdisc:", subtotal.toString() + "")
                if (isBillDiscountTouch) {
                    if (!editable.toString().isEmpty()) {
                        if (editable.toString() == ".") {
                            bill_disc_amt_ed!!.setText("0.")
                            bill_disc_amt_ed!!.setSelection(bill_disc_amt_ed!!.getText().length)
                            setPercentageAmount("0".toDouble())
                            val net_subtotal = subtotal - 0
                            setCalculationSummaryView(net_subtotal)
                        } else {
                            setPercentageAmount(editable.toString().toDouble())
                            val net_subtotal = subtotal - editable.toString().toDouble()
                            setCalculationSummaryView(net_subtotal)
                        }
                    } else {
                        setPercentageAmount(0.0)
                        setCalculationSummaryView(subtotal)
                    }
                }
            }
        }
        bill_disc_amt_ed!!.addTextChangedListener(billDiscAmountTextWatcher)


        billDiscPercentageTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {

                val subtotal: Double = getSubtotal()
                if (isBillPercentageTouch) {
                    if (!editable.toString().isEmpty()) {
                        if (editable.toString() == ".") {
                            bill_disc_percent_ed!!.setText("0.")
                            bill_disc_percent_ed!!.setSelection( bill_disc_percent_ed!!.getText().length)
                            val amount: Double = percentageToAmount("0".toDouble())
                            bill_disc_amt_ed!!.removeTextChangedListener(billDiscAmountTextWatcher)
                            bill_disc_amt_ed!!.setText(twoDecimalPoint(amount) + "")
                            bill_disc_amt_ed!!.addTextChangedListener(billDiscAmountTextWatcher)
                            val net_subtotal: Double =
                                subtotal - bill_disc_amt_ed!!.getText().toString().toDouble()
                            setCalculationSummaryView(net_subtotal)
                        } else {
                            val amount: Double = percentageToAmount(editable.toString().toDouble())
                            Log.w("PercentageAmtdisc:", amount.toString() + "")
                            bill_disc_amt_ed!!.removeTextChangedListener(billDiscAmountTextWatcher)
                            bill_disc_amt_ed!!.setText(twoDecimalPoint(amount) + "")
                            bill_disc_amt_ed!!.addTextChangedListener(billDiscAmountTextWatcher)
                            val net_subtotal: Double =
                                subtotal - bill_disc_amt_ed!!.getText().toString().toDouble()
                            setCalculationSummaryView(net_subtotal)
                        }
                    } else {
                        bill_disc_amt_ed!!.removeTextChangedListener(billDiscAmountTextWatcher)
                        bill_disc_amt_ed!!.setText("")
                        bill_disc_amt_ed!!.addTextChangedListener(billDiscAmountTextWatcher)
                        setCalculationSummaryView(subtotal)
                    }
                }
            }
        }
        bill_disc_percent_ed!!.addTextChangedListener(billDiscPercentageTextWatcher)


        discTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val disc = editable.toString()

                if (!disc.isEmpty()) {
                    if (isInRange(minimum_disc_amt, maximum_disc_amt, disc.toDouble())) {
                        discountStr = item_discount_ed!!.getText().toString()
                    } else {
                        item_discount_ed!!.removeTextChangedListener(discTextWatcher)
                        item_discount_ed!!.setText("")
                        discountStr = "0"
                        item_discount_ed!!.addTextChangedListener(discTextWatcher)
                        //   Toast.makeText(getApplicationContext(), "Minimum discount amonut $1 !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    item_discount_ed!!.removeTextChangedListener(discTextWatcher)
                    item_discount_ed!!.setText("")
                    discountStr = "0"
                    item_discount_ed!!.addTextChangedListener(discTextWatcher)
                    //   Toast.makeText(getApplicationContext(), "Minimum discount amonut $1 !", Toast.LENGTH_SHORT).show();
                }
                setCalculationView()
            }
        }
        if (item_discount_ed!!.isFocusable()) {
            item_discount_ed!!.addTextChangedListener(discTextWatcher)
        }


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
                if (!returnQtyText!!.getText().toString().isEmpty()) {
                    val netreturnqty = returnQtyText!!.getText().toString()
                    val net_qty = netreturnqty.toString().toDouble()
                    if (!s.toString().isEmpty()) {
                        val damageqty = s.toString().toDouble()
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
                if (!returnQtyText!!.getText().toString().isEmpty()) {
                    val netreturnqty = returnQtyText!!.getText().toString()
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
                if (!editable.toString().isEmpty() ) {
                    addProduct!!.setAlpha(0.9f)
                    addProduct!!.setEnabled(true)
                } else {
                    addProduct!!.setAlpha(0.4f)
                    addProduct!!.setEnabled(false)
                }

                //  setButtonView();
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
                lowStockSetting
                setCalculationView()
                if (companyCode == "SUPERSTAR TRADERS PTE LTD") {
                    if (activityFrom == "iv" || activityFrom == "so" || activityFrom == "SalesEdit" ||
                        activityFrom == "Duplicate"
                    ) {
                        //  if (!isAllowLowStock){
                        checkLowStock()
                        // }
                    }
                } else {
                    if (companyCode == "AADHI INTERNATIONAL PTE LTD") {
                        if (activityFrom == "iv" || activityFrom == "so" || activityFrom == "SalesEdit") {
                            if (!isAllowLowStock) {
                                checkLowStock()
                            }
                        }
                    } else {
                        if (activityFrom == "iv" || activityFrom == "Duplicate") {
                            if (!isAllowLowStock) {
                                checkLowStock()
                            }
                        }
                    }
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
            Log.w("addpdt", "" + productAutoComplete!!.getText().toString())
            if (!productAutoComplete!!.getText().toString().isEmpty()) {
                if (!qtyValue!!.getText().toString()
                        .isEmpty() && !s.isEmpty() && qtyValue!!.getText()
                        .toString() != "0" && qtyValue!!.getText().toString() != "00" &&
                    qtyValue!!.getText().toString() != "000" && qtyValue!!.getText()
                        .toString() != "0000" &&
                    netTotalValue!!.getText().toString() != "0.00"
                ) {
                    if (addProduct!!.getText().toString() == "Update") {
                        // if (!cartonPrice.getText().toString().isEmpty() && !cartonPrice.getText().toString().equals("0.00") && !cartonPrice.getText().toString().equals("0.0") && !cartonPrice.getText().toString().equals("0")){
                        if (priceText!!.getText() != null && !priceText!!.getText().toString()
                                .isEmpty()
                        ) {
                            if (priceText!!.getText().toString().toDouble() > 0) {
                                val minimumsellingprice =
                                    minimumSellingPriceText!!.getText().toString().toDouble()
                                if (minimumsellingprice <= priceText!!.getText().toString()
                                        .toDouble()
                                ) {
                                    insertProducts()

                                    qtyValue!!.setEnabled(true)
                                    priceText!!.setEnabled(true)
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
                            Toast.makeText(
                                applicationContext,
                                "Enter the price",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        if (priceText!!.getText().toString() != null && !priceText!!.getText()
                                .toString().isEmpty()
                        ) {
                            if (priceText!!.getText().toString().toDouble() > 0) {
                                val minimumsellingprice =
                                    minimumSellingPriceText!!.getText().toString().toDouble()
                                if (minimumsellingprice <= priceText!!.getText().toString()
                                        .toDouble()
                                ) {
                                    addProduct("Add")
                                    qtyValue!!.setEnabled(true)
                                    priceText!!.setEnabled(true)
                                } else {
                                    showMinimumSellingpriceAlert(
                                        minimumSellingPriceText!!.getText().toString()
                                    )
                                }
                            } else {
                                if (focEditText!!.getText() != null && !focEditText!!.getText()
                                        .toString().isEmpty() && focEditText!!.getText()
                                        .toString() != "0" || returnQtyText!!.getText() != null && !returnQtyText!!.getText()
                                        .toString().isEmpty() && returnQtyText!!.getText()
                                        .toString() != "0"
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
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Enter the price",
                                Toast.LENGTH_SHORT
                            ).show()
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
            } else {
                Toast.makeText(applicationContext, "Select product", Toast.LENGTH_SHORT).show()
            }
        })
        returnAdj!!.setOnClickListener(View.OnClickListener { returnLayoutView!!.setVisibility(View.VISIBLE) })
        cancelReturn!!.setOnClickListener(View.OnClickListener {
            returnLayoutView!!.setVisibility(
                View.GONE
            )
        })
        bill_disc_layl!!.setOnClickListener {
            val products = dbHelper!!.allInvoiceProducts

            if (netTotalValue!!.text.toString().toDouble() > 0.00 && products!!.size>0) {
                bill_disc_percent_ed!!.setText(billDiscApiStr)
            }
        }
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
            val popupMenu = PopupMenu(this@CreateNewInvoiceActivityBillDisc, menuItemView)
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

    /* if (!qtyValue.getText().toString().isEmpty() && !s.isEmpty() && !qtyValue.getText().toString().equals("0") && !qtyValue.getText().toString().equals("00") && !qtyValue.getText().toString().equals("000") && !qtyValue.getText().toString().equals("0000") && !netTotalValue.getText().toString().equals("0.00")){
        if (priceText.getText().toString() != null && !priceText.getText().toString().isEmpty()) {
            double minimumsellingprice = Double.parseDouble(minimumSellingPriceText.getText().toString());
            if (minimumsellingprice <= Double.parseDouble(priceText.getText().toString())) {
                addProduct("Add");
            } else {
                showMinimumSellingpriceAlert(minimumSellingPriceText.getText().toString());
            }
        }else {
            Toast.makeText(getApplicationContext(), "Enter the price", Toast.LENGTH_SHORT).show();
        }*/
    fun showSignatureAlert() {
        val alertDialog = AlertDialog.Builder(this)
        val customLayout = layoutInflater.inflate(R.layout.signature_layout, null)
        alertDialog.setView(customLayout)
        val acceptButton = customLayout.findViewById<Button>(R.id.buttonYes)
        val cancelButton = customLayout.findViewById<Button>(R.id.buttonNo)
        val clearButton = customLayout.findViewById<Button>(R.id.buttonClear)
        val mContent = customLayout.findViewById<LinearLayout>(R.id.signature_layout)
        val mSig = CaptureSignatureView(this, null)
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
            Log.w("SignatureString:", signatureString!!)
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
                } else if (action == "Out of Stock") {
                    if (s.stockQty != null && s.stockQty != "null") {
                        if (s.stockQty.toDouble() < 0 || s.stockQty.toDouble() == 0.0) {
                            filterdNames.add(s)
                        }
                    }
                } else {
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
            Log.e("Error_in_filter", Objects.requireNonNull(ex.message!!))
        }
    }


    fun percentageToAmount(percentage: Double): Double {

        return getSubtotal() / 100.0f * percentage
    }

    fun calculatePercentage(obtained: Double, total: Double): Double {
        return obtained * 100 / total
    }

    fun setPercentageAmount(value: Double) {
        val per: Double = calculatePercentage(value, getSubtotal())
        Log.w("PercentageTxt:", per.toString() + "")
        //billDiscAmount.removeTextChangedListener(billDiscAmountTextWatcher);
        if (per == 0.0) {
            bill_disc_percent_ed!!.removeTextChangedListener(billDiscPercentageTextWatcher)
            bill_disc_percent_ed!!.setText("")
            bill_disc_percent_ed!!.addTextChangedListener(billDiscPercentageTextWatcher)
        } else {
            bill_disc_percent_ed!!.removeTextChangedListener(billDiscPercentageTextWatcher)
            bill_disc_percent_ed!!.setText(twoDecimalPoint(per) + "")
            bill_disc_percent_ed!!.addTextChangedListener(billDiscPercentageTextWatcher)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate(dateEditext: TextView?) {
        // Get Current Date
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            this@CreateNewInvoiceActivityBillDisc,
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
                Log.w("product_Code:", model.productCode)
                if (model.productCode == productId.trim { it <= ' ' }) {
                    if (model.stockQty != null && model.stockQty != "null") {
                        if (model.stockQty.toDouble() == 0.0 || model.stockQty.toDouble() < 0) {
                            stockQtyValue!!.text = model.stockQty
                            stockQtyValue!!.setTextColor(Color.parseColor("#D24848"))
                        } else if (model.stockQty.toDouble() > 0) {
                            stockQtyValue!!.setTextColor(Color.parseColor("#2ECC71"))
                            stockQtyValue!!.text = model.stockQty
                        }
                    }
                    // stockLayout.setVisibility(View.VISIBLE);
                    if (model.stockQty.toDouble() == 0.0 || model.stockQty.toDouble() < 0) {
                        if (isAllowLowStock) {
                            productAutoComplete!!.clearFocus()
                            cartonPrice!!.setText(model.unitCost)
                            if (model.lastPrice != null && !model.lastPrice.isEmpty() &&
                                model.lastPrice.toDouble() > 0.00
                            ) {
                                priceText!!.setText(model.lastPrice)
                            } else {
                                priceText!!.setText(model.unitCost)
                            }
                            loosePrice!!.setText(model.unitCost)
                            // uomText.setText(model.getUomCode());
                            stockCount!!.setText(model.stockQty)
                            pcsPerCarton!!.setText(model.pcsPerCarton)
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
                            qtyValue!!.isEnabled = false
                        }
                        qtyValue!!.isEnabled = true
                        qtyValue!!.setText("")
                        qtyValue!!.requestFocus()
                        openKeyborard(qtyValue)
                        qtyValue!!.setSelection(qtyValue!!.text.length)
                    } else {
                        productAutoComplete!!.clearFocus()
                        cartonPrice!!.setText(model.unitCost)
                        if (model.lastPrice != null && !model.lastPrice.isEmpty() && model.lastPrice.toDouble() > 0.00) {
                            priceText!!.setText(model.lastPrice)
                        } else {
                            priceText!!.setText(model.unitCost)
                        }
                        loosePrice!!.setText(model.unitCost)
                        // uomText.setText(model.getUomCode());
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
                    }
                } else {
                    Log.w("Not_found", "Product")
                }
                qtyValue!!.isEnabled = true
                qtyValue!!.requestFocus()
                openKeyborard(qtyValue)
                qtyValue!!.setSelection(qtyValue!!.text.length)
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
        val builder1 = AlertDialog.Builder(this@CreateNewInvoiceActivityBillDisc)
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
        bill_disc_percent_ed!!.setText("0.00")
        bill_disc_amt_ed!!.setText("0.00")

        if (isProductExist(productId)) {
            Log.w("ProductIdView:", productId!!)
            if (!qtyValue!!.text.toString().isEmpty() && qtyValue!!.text.toString()
                    .toDouble() > 0
            ) {
                showExistingProductAlert(productId, productName)
            } else {
                insertProducts()
            }
        } else {
            insertProducts()
        }
    }

    fun showExistingProductAlert(productId: String?, productName: String?) {
        val builder1 = AlertDialog.Builder(applicationContext)
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
            var itemdiscc = "0"
            var foc = "0"
            var uom = "PCS"
            var price_value = "0"

            bill_disc_percent_ed!!.setText("0.00")
            bill_disc_amt_ed!!.setText("0.00")

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
            if (!item_discount_ed!!.text.toString().isEmpty()) {
                itemdiscc = item_discount_ed!!.text.toString()
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
            if (!uomText!!.text.toString().isEmpty()) {
                uom = uomText!!.text.toString()
            }

            val priceValue = 0.0
            val net_qty = qty_value.toDouble() - return_qty.toDouble()
            val return_amt = return_qty.toDouble() * price_value.toDouble()
            val total = net_qty * price_value.toDouble()
            val sub_total = total - return_amt - discount.toDouble()
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
                netTotalValue!!.text.toString(),
                itemdiscc,
                "",
                "",
                "",
                "",
                "","","",""
            )

            // Adding Return Qty Table values
            if (return_qty.toInt() > 0) {
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
                uomTextView!!.text = ""
                stockCount!!.setText("")
                stockQtyValue!!.text = ""
                qtyValue!!.clearFocus()
                isEditItem = false
                productAutoComplete!!.clearFocus()
                focEditText!!.setText("")
                exchangeEditext!!.setText("")
                discountEditext!!.setText("")
                returnQtyText!!.setText("")
                item_discount_ed!!.setText("")
                focSwitch!!.isChecked = false
                exchangeSwitch!!.isChecked = false
                returnSwitch!!.isChecked = false
                stockLayout!!.visibility = View.GONE
                // priceText.setEnabled(false);
                // qtyValue.setEnabled(false);
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
            productSummaryAdapter = NewProductSummaryAdapter(
                this,
                products,
                object : NewProductSummaryAdapter.CallBack {
                    override fun searchCustomer(letter: String, pos: Int) {}
                    override fun removeItem(pid: String,time: String) {
                        showRemoveItemAlert(pid)
                    }

                    override fun editItem(model: CreateInvoiceModel) {
                        isEditItem = true
                        salesReturn!!.isChecked = false
                        salesReturn!!.isEnabled = false
                        productId = model.productCode
                        productName = model.productName
                        uomText!!.setText(model.uomCode)
                        uomName = model.uomCode

                        val jsonObject = JSONObject()
                        Log.w("pdtuomentryy", "" + model.productCode);

                        if (isUomSetting) {
                            val jsonObject = JSONObject()
                            try {
                                jsonObject.put("CustomerCode", selectCustomerId)
                                jsonObject.put("ItemCode",productId)
                                getUOM(jsonObject)

                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                        Log.w("EditUomText:", model.uomCode)
                   //     setUOMCode(uomList!!)
                        uomTextView!!.text = model.uomText
                        qtyValue!!.setText("")
                        val netqty = model.netQty.toDouble()

                        Log.w("itemdiscc:", model.itemDisc)

                        /*  if (model.getMinimumSellingPrice()!=null && !model.getMinimumSellingPrice().isEmpty()){
                        minimumSellingPriceText.setText(model.getMinimumSellingPrice());
                    }else {
                        minimumSellingPriceText.setText("0.00");
                    }*/qtyValue!!.removeTextChangedListener(qtyTW)
                        qtyValue!!.setText(Utils.getQtyValue(netqty.toString()))
                        qtyValue!!.addTextChangedListener(qtyTW)
                        productAutoComplete!!.setText(model.productName + "-" + model.productCode)
                        priceText!!.setText(model.price)
                        item_discount_ed!!.setText(model.itemDisc)

                        addProduct!!.text = "Update"
                        qtyValue!!.requestFocus()
                        qtyValue!!.setSelectAllOnFocus(true)
                        qtyValue!!.setSelection(qtyValue!!.text.length)
                        qtyValue!!.isEnabled = true
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
                        stockLayout!!.visibility = View.VISIBLE
                        stockQtyValue!!.setTextColor(Color.parseColor("#2ECC71"))
                        stockQtyValue!!.text = model.stockQty
                        stockCount!!.setText(model.stockQty)

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

    fun showRemoveItemAlert(pid: String?) {
        try {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE) // .setTitleText("Are you sure?")
                .setContentText("Are you sure want to remove this item ?")
                .setConfirmText("YES")
                .setConfirmClickListener { sDialog ->
                    dbHelper!!.deleteInvoiceProduct(pid)
                    clearFields()
                    sDialog.dismissWithAnimation()
                    getProducts()
                    setSummaryTotal()
                    Utils.refreshActionBarMenu(this@CreateNewInvoiceActivityBillDisc)
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


                /*     if (customerDetails.get(0).getTaxType().equals("I")){
                    subTotal.setText(Utils.twoDecimalPoint(net_total));
                    taxText.setText(Utils.twoDecimalPoint(net_tax));
                    netTotalText.setText(Utils.twoDecimalPoint(net_total));
                    itemDiscountText.setText(Utils.twoDecimalPoint(net_discount));

                    double sub_total=net_total - net_tax;
                    totalText.setText(Utils.twoDecimalPoint(sub_total));
                    double sub_total1=sub_total + net_tax;

                    // subTotalValue=Utils.twoDecimalPoint(sub_total1);
                    subTotalValue=Utils.twoDecimalPoint(net_total);

                    taxValue=Utils.twoDecimalPoint(net_tax);
                    netTotalValue=Utils.twoDecimalPoint(net_total);
                    itemDiscountAmount=Utils.twoDecimalPoint(net_discount);
                    totalValue=Utils.twoDecimalPoint(total_value);

                }else {
                    subTotal.setText(Utils.twoDecimalPoint(net_sub_total));
                    taxText.setText(Utils.twoDecimalPoint(net_tax));
                    totalText.setText(Utils.twoDecimalPoint(net_sub_total));
                    netTotalText.setText(Utils.twoDecimalPoint(net_total));
                    itemDiscountText.setText(Utils.twoDecimalPoint(net_discount));

                    subTotalValue=Utils.twoDecimalPoint(net_sub_total);
                    taxValue=Utils.twoDecimalPoint(net_tax);
                    netTotalValue=Utils.twoDecimalPoint(net_total);
                    itemDiscountAmount=Utils.twoDecimalPoint(net_discount);
                    totalValue=Utils.twoDecimalPoint(total_value);
                }*/

//                if (bill_disc_amt_ed!!.getText().toString().isNotEmpty()) {
//                    billDiscountAmount = bill_disc_amt_ed!!.getText().toString()
//                } else {
//                    billDiscountAmount = "0"
//                }
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
        isEditItem = false
        pcsPerCarton!!.setText("")
        uomText!!.setText("")
        uomTextView!!.text = ""
        stockCount!!.setText("")
        exchange_inv!!.setText("")
        item_discount_ed!!.setText("")
        qtyValue!!.clearFocus()
        productAutoComplete!!.clearFocus()
        focEditText!!.setText("")
        returnQtyText!!.setText("")
        stockLayout!!.visibility = View.GONE
        // priceText.setEnabled(false);
        getProducts()
        setSummaryTotal()
    }

    fun isProductExist(productId: String?): Boolean {
        var isExist = false
        try {
            val localCart = dbHelper!!.allCartItems
            if (localCart.size > 0) {
                for (cart in localCart) {
                    if (cart.carT_COLUMN_PID != null) {
                        if (cart.carT_COLUMN_PID == productId) {
                            Log.w("ProductIdPrint:", cart.carT_COLUMN_PID)
                            isExist = true
                            break
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("Exp_to_check_product:", Objects.requireNonNull(ex.message!!))
        }
        return isExist
    }

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

    fun getSubtotal(): Double {
        try {
            val localCart: ArrayList<CreateInvoiceModel>
            localCart = dbHelper!!.allInvoiceProducts
            var net_sub_total = 0.0
            var net_tax = 0.0
            var net_total = 0.0
            var discount = 0.0

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
                    if (model.itemDisc != null && !model.itemDisc.isEmpty()) {
                        discount += model.itemDisc.toDouble()
                    }
                }
                val sharedPreferences = getSharedPreferences("customerPref", MODE_PRIVATE)
                selectCustomerId = sharedPreferences.getString("customerId", "")
                customerDetails = dbHelper!!.getCustomer(selectCustomerId)
                val taxValue = customerDetails!!.get(0).taxPerc
                val taxType = customerDetails!!.get(0).taxType

                if (taxType == "I") {
                    net_total_value = net_total + discount
                } else {
                    net_total_value = net_sub_total + discount
                }
            }

        } catch (ex: Exception) {
        }
        return net_total_value
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
                    } else if (model.settingName == "creditLimitSwitch") {
                        Log.w("SettingNameI:", model.settingName)
                        Log.w("SettingValueI:", model.settingValue)
                        if (model.settingValue == "1") {
                            isCheckedCreditLimit = true
                        } else {
                            isCheckedCreditLimit = false
                        }
                    }

                }
            } else {
                isAllowLowStock = false
            }
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
                    taxTitle!!.text = "GST ( Exc ) : " + taxValue.toDouble().toInt() + " % "
                    subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                    bill_disc_subtotal_txtl!!.text =Utils.twoDecimalPoint(subTotal)

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
                    taxTitle!!.text = "GST ( Inc ) : " + taxValue.toDouble().toInt() + " % "
                    subTotalValue!!.text = Utils.twoDecimalPoint(sub_total)
                    bill_disc_subtotal_txtl!!.text =Utils.twoDecimalPoint(subTotal)

                } else if (taxType.matches("Z".toRegex())) {
                    taxValueText!!.text = "0.0"
                    // netTotal1 = subTotal + taxAmount;
                    netTotal1 = subTotal
                    val ProdNetTotal = Utils.twoDecimalPoint(netTotal1)
                    netTotalValue!!.text = "" + ProdNetTotal
                    subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                    bill_disc_subtotal_txtl!!.text =Utils.twoDecimalPoint(subTotal)
                    taxTitle!!.text = "GST ( Zero )"
                } else {
                    taxValueText!!.text = "0.0"
                    netTotalValue!!.text = "" + Prodtotal
                    subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                    bill_disc_subtotal_txtl!!.text =Utils.twoDecimalPoint(subTotal)
                    taxTitle!!.text = "GST ( Zero )"
                }
            } else if (taxValue.matches("".toRegex())) {
                taxValueText!!.text = "0.0"
                netTotalValue!!.text = "" + Prodtotal
                subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                bill_disc_subtotal_txtl!!.text =Utils.twoDecimalPoint(subTotal)
                taxTitle!!.text = "GST ( Zero )"
            } else {
                taxValueText!!.text = "0.0"
                netTotalValue!!.text = "" + Prodtotal
                subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                bill_disc_subtotal_txtl!!.text =Utils.twoDecimalPoint(subTotal)

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
            var disc = item_discount_ed!!.text.toString()
            var exchang = exchange_inv!!.text.toString()
            if (disc.matches("".toRegex())) {
                disc = "0"
            }
            if (exchang.matches("".toRegex())) {
                exchang = "0"
            }
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
            var tt = net_qty * cPriceCalc - discountStr.toDouble()
            Log.w("dis_invStr", "$tt..$disc")
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
                    taxTitle!!.text = "GST ( Exc ) :" + taxValue.toDouble().toInt() + " % "
                    subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                    bill_disc_subtotal_txtl!!.text =Utils.twoDecimalPoint(subTotal)
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
                    taxTitle!!.text = "GST ( Inc ) : " + taxValue.toDouble().toInt() + " % "
                    subTotalValue!!.text = Utils.twoDecimalPoint(sub_total)
                    bill_disc_subtotal_txtl!!.text =Utils.twoDecimalPoint(sub_total)

                } else if (taxType.matches("Z".toRegex())) {
                    taxValueText!!.text = "0.0"
                    // netTotal1 = subTotal + taxAmount;
                    netTotal1 = subTotal
                    val ProdNetTotal = Utils.twoDecimalPoint(netTotal1)
                    netTotalValue!!.text = "" + ProdNetTotal
                    subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                    bill_disc_subtotal_txtl!!.text =Utils.twoDecimalPoint(subTotal)

                    taxTitle!!.text = "GST ( Zero )"
                } else {
                    taxValueText!!.text = "0.0"
                    netTotalValue!!.text = "" + Prodtotal
                    subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                    bill_disc_subtotal_txtl!!.text =Utils.twoDecimalPoint(subTotal)

                    taxTitle!!.text = "GST ( Zero )"
                }
            } else if (taxValue.matches("".toRegex())) {
                taxValueText!!.text = "0.0"
                netTotalValue!!.text = "" + Prodtotal
                subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                bill_disc_subtotal_txtl!!.text =Utils.twoDecimalPoint(subTotal)

                taxTitle!!.text = "GST ( Zero )"
            } else {
                taxValueText!!.text = "0.0"
                netTotalValue!!.text = "" + Prodtotal
                subTotalValue!!.text = Utils.twoDecimalPoint(subTotal)
                bill_disc_subtotal_txtl!!.text =Utils.twoDecimalPoint(subTotal)

                taxTitle!!.text = "GST ( Zero )"
            }
            setButtonView()
        } catch (e: Exception) {
            Log.w("Error_Throwing::", e.message!!)
        }
    }


    fun setButtonView() {
        val products = dbHelper!!.allInvoiceProducts

        if (!focEditText!!.text.toString().isEmpty() && focEditText!!.text.toString() != "0") {
            addProduct!!.alpha = 0.9f
            addProduct!!.isEnabled = true
        }
        if ((netTotalValue!!.text.toString().toDouble() > 0.00 )&& (products!!.size>0)) {
            down_menu!!.isVisible = true
        }
        else{
            down_menu!!.isVisible = false
        }
            if (netTotalValue!!.text.toString().toDouble() > 0.00) {
            addProduct!!.alpha = 0.9f
            addProduct!!.isEnabled = true
        } else {
            if (!productNameEditext!!.text.toString().isEmpty()) {
                //  if ((!focEditText.getText().toString().isEmpty() && !focEditText.getText().toString().equals("0")) || (!returnQtyText.getText().toString().isEmpty() && !returnQtyText.getText().toString().equals("0"))) {
                if (!qtyValue!!.text.toString()
                        .isEmpty() && qtyValue!!.text.toString() != "0" && priceText!!.text.toString()
                        .toInt() > 0
                ) {
                    if (!priceText!!.text.toString().isEmpty() && priceText!!.text.toString()
                            .toDouble() > 0.00
                    ) {
                        addProduct!!.alpha = 0.9f
                        addProduct!!.isEnabled = true
                    } else {
                        addProduct!!.alpha = 0.4f
                        addProduct!!.isEnabled = false
                        Toast.makeText(applicationContext, "Price is Empty..!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    addProduct!!.alpha = 0.9f
                    addProduct!!.isEnabled = true
                }
                //         }

//                else {
//                    addProduct.setAlpha(0.4F);
//                    addProduct.setEnabled(false);
//                }
            } else {
                addProduct!!.alpha = 0.4f
                addProduct!!.isEnabled = false
            }
        }
    }

    //    public void setButtonView() {
    //        Log.w("focentr22","");
    //        if (Double.parseDouble(netTotalValue.getText().toString()) > 0.00) {
    //            addProduct.setAlpha(0.9F);
    //            addProduct.setEnabled(true);
    //        } else {
    //            if (!productNameEditext.getText().toString().isEmpty()) {
    //                if ((!focEditText.getText().toString().isEmpty() && !focEditText.getText().toString().equals("0"))) {
    //                    Log.w("focentr","");
    //                    addProduct.setAlpha(0.9F);
    //                    addProduct.setEnabled(true);
    //                }
    //                else {
    //                    if (!qtyValue.getText().toString().isEmpty() && !qtyValue.getText().toString().equals("0") && Integer.parseInt(priceText.getText().toString()) > 0) {
    //                        if (!priceText.getText().toString().isEmpty() && Double.parseDouble(priceText.getText().toString()) > 0.00) {
    //                            addProduct.setAlpha(0.9F);
    //                            addProduct.setEnabled(true);
    //                        } else {
    //                            addProduct.setAlpha(0.4F);
    //                            addProduct.setEnabled(false);
    //                            Toast.makeText(getApplicationContext(), "Price is Empty..!", Toast.LENGTH_SHORT).show();
    //                        }
    //                    } else {
    //                        addProduct.setAlpha(0.9F);
    //                        addProduct.setEnabled(true);
    //                    }
    //                }
    //            }
    //            else{
    //                addProduct.setAlpha(0.4F);
    //                addProduct.setEnabled(false);
    //            }
    //        }
    //    }
    fun getDeliveryAddress(jsonObject: JSONObject) {
        // Initialize a new RequestQueue instance
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "CustomerAddress"
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_delive_addrs_URL:", url + jsonObject.toString())
        deliveryAddrList = ArrayList()
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.setTitleText("Loading Address...")
        pDialog.setCancelable(false)
        pDialog.show()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            jsonObject,
            Response.Listener { response: JSONObject ->
                try {
                    Log.w("Res_delive_addrs:", response.toString())
                    // Loop through the array elements
                    val uomArray = response.optJSONArray("responseData")
                    if (uomArray != null && uomArray.length() > 0) {
                        for (j in 0 until uomArray.length()) {
                            val uomObject = uomArray.getJSONObject(j)
                            val model = DeliveryAddressModel()
                            model.deliveryAddress = uomObject.optString("address")
                            //                        model.setDeliveryAddressCode(uomObject.optString("uomName"));
                            deliveryAddrList!!.add(model)
                        }
                    }
                    Log.w("deliveryTxt:", uomArray.toString())
                    pDialog.dismiss()
                    if (deliveryAddrList!!.size > 0) {
//                    runOnUiThread(() -> {
                        setDeliveryAddrList(deliveryAddrList!!)
                        //  });
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
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
                            setUomList(uomList!!)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
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
                    //   progressDialog.dismiss();
                    //  {"productCode":"FG\/001245","productName":"RUM","companyName":"","supplierCategoryNumber":"","uomCode":"Ctn",
                    //  "uomName":"Carton","cartonPrice":"3000.000000","piecePrice":"0.000000","pcsPerCarton":"100.000000",
                    //  "price":"100.000000","taxType":"E","havTax":"Y","taxCode":"SR","taxRate":"7.000000","barCode":"",
                    //  "isActive":"N","createUser":"1","createDate":"13\/07\/2021","modifyDate":"29\/07\/2021","remarks":"",
                    //  "warehouse":"01","stockInHand":"0.000000","averagePrice":0,"manageBatchOrSerial":"None","manageBatchNumber":"N",
                    //  "manageSerialNumber":"N","batchNumber":"","expiryDate":null,"manufactureDate":"31\/07\/2021","imageURL":""}
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
                            //  product.setProductBarcode(productObject.optString("BarCode")); Add values In Futue
                            product.productBarcode = ""

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
                                product.setProductUOMList(uomList);*/productList!!.add(product)
                        }
                    }
                    HomePageModel.productsList = ArrayList()
                    setAdapter(productList)
                    HomePageModel.productsList.addAll(productList!!)
                    // pDialog.dismiss();
                    if (productList!!.size > 0) {
                        runOnUiThread { AppUtils.setProductsList(productList) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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

    fun isInRange(miniVal: Double, maxVal: Double, edTxt: Double): Boolean {
        return if (maxVal > miniVal) edTxt in miniVal..maxVal else edTxt in maxVal..miniVal
    }

    @SuppressLint("SetTextI18n")
    private fun setAdapter(productList: ArrayList<ProductsModel>?) {
        // Get the Settings to Display the Product list with the Settings
        if (settings!!.size > 0) {
            for (model in settings!!) {
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
            productAutoComplete!!.setText(model.productName + " - " + model.productCode)
            productId = model.productCode
            productName = model.productName
            if (isUomSetting) {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("CustomerCode", selectCustomerId)
                    jsonObject.put("ItemCode", model.productCode)
                    getUOM(jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            // setUomList(model.getProductUOMList());
            uomTextView!!.text = model.uomText
            if (model.lastPrice != null && !model.lastPrice.isEmpty() && model.lastPrice.toDouble() > 0.00) {
                priceText!!.setText(model.lastPrice)
            } else {
                priceText!!.setText(model.unitCost)
            }
            //  uomText.setText(model.getUomCode());
            stockCount!!.setText(model.stockQty)
            // looseQtyValue.setEnabled(true);
            qtyValue!!.setText("")
            focEditText!!.isEnabled = true
            exchangeEditext!!.isEnabled = true
            discountEditext!!.isEnabled = true
            returnQtyText!!.isEnabled = true
            if (model.minimumSellingPrice != null && !model.minimumSellingPrice.isEmpty()) {
                minimumSellingPriceText!!.text = model.minimumSellingPrice
            } else {
                minimumSellingPriceText!!.text = "0.00"
            }
            stockLayout!!.visibility = View.VISIBLE
            if (model.stockQty != null && model.stockQty != "null") {
                if (model.stockQty.toDouble() == 0.0 || model.stockQty.toDouble() < 0) {
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
            if (currentSaveDateTime == null || currentSaveDateTime!!.isEmpty()) {
                val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val currentDateandTime = sdf.format(Date())
                currentSaveDateTime = currentDateandTime
            }
            rootJsonObject.put("customerReferenceNo", orderNoText!!.text.toString())
            rootJsonObject.put("soDate", currentDate)
            rootJsonObject.put("currentDateTime", currentSaveDateTime)
            rootJsonObject.put("customerCode", `object`["customerCode"])
            rootJsonObject.put("customerName", `object`["customerName"])
            rootJsonObject.put("address", `object`["address"])
            rootJsonObject.put("street", `object`["street"])
            rootJsonObject.put("city", `object`["city"])
            rootJsonObject.put("creditLimit", `object`["creditLimit"])
            rootJsonObject.put("remark", remarkStr)
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
            rootJsonObject.put("delAddress1", deliverAddrNameStr)
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
                invoiceObject.put("price", Utils.fourDecimalPoint(model.price.toDouble()))
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
            Log.w("RootJsonForSaveSO:", rootJsonObject.toString())
            saveSalesOrder(rootJsonObject, "SalesOrder", copy)
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.w("Given_Error:", Objects.requireNonNull(e.message!!))
        }
    }

    fun billDiscountBottomSheet() {
        hideKeyboard()
        prod_bottom_layout!!.visibility = View.GONE
        bill_disc_layl!!.visibility = View.VISIBLE
        //  hideKeyboard();
        if (behavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior!!.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)
        } else {
            behavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }


    }

//    private fun showBottom_BillDialog(pos: Int) {
//        bottomSheetDialog = BottomSheetDialog(this)
//        bottomSheetDialog.setContentView(R.layout.bottom_sheet_billdisc_view)
//        delete_dob = bottomSheetDialog.findViewById<TextView>(R.id.delete_do)
//
//        val bottomSheet: FrameLayout = bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
//        val closeImg: ImageView = bottomSheetDialog.findViewById<ImageView>(R.id.dialog_close_do)
//        closeImg.setOnClickListener { bottomSheetDialog.dismiss() }
//
//        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
//        bottomSheetBehavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//        bottomSheetDialog.show()
//    }


    fun viewCloseBottomSheet() {
        // hideKeyboard();

        prod_bottom_layout!!.visibility = View.VISIBLE
        bill_disc_layl!!.visibility = View.GONE

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
        if (AppUtils.getProductsList() != null && AppUtils.getProductsList().size > 0) {
            for (product in AppUtils.getProductsList()) {
                //     Log.w("apiPdtDefa_uom", "" + product.defaultUom)
                if (stockProductView == "1") {
                    if (product.stockQty != null && product.stockQty != "null") {
                        if (product.stockQty.toDouble() > 0) {
                            Log.w("apiPdtUOm", "" + product.uomCode + product.uomText)
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
            Log.w("pdtsInv", "" + model.productName + "  .. " + model.uomCode)
            // setUomList(model.getProductUOMList());
            uomTextView!!.text = model.uomText
            if (isUomSetting) {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("CustomerCode", selectCustomerId)
                    jsonObject.put("ItemCode", model.productCode)
                    getUOM(jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

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
            focEditText!!.isEnabled = true
            exchangeEditext!!.isEnabled = true
            discountEditext!!.isEnabled = true
            returnEditext!!.isEnabled = true
            stockLayout!!.visibility = View.VISIBLE
            if (model.stockQty != null && model.stockQty != "null") {
                if (model.stockQty.toDouble() == 0.0 || model.stockQty.toDouble() < 0) {
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

    private fun setDeliveryAddrList(addrList: ArrayList<DeliveryAddressModel>) {
        val addressModel = DeliveryAddressModel()
        addressModel.deliveryAddress = "Select Delivery Address"
        addressModel.deliveryAddressCode = ""

//        Log.w("deladdrList:", addrList.toString());
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.cust_spinner_item);

//        for (int i = 0; i < addrList.size(); i++) {
//            adapter.add(addrList.get(i).getDeliveryAddress());
//        }
        val deliveryAddressModels = ArrayList<DeliveryAddressModel>()
        deliveryAddressModels.add(0, addressModel)
        deliveryAddressModels.addAll(addrList)
        val adapter = ArrayAdapter(this, R.layout.cust_spinner_item, deliveryAddressModels)
        delivery_addrSpinner!!.adapter = adapter
        if (productsModel != null) {
            //setuom
            Log.w("cg_addrsiz1", addrList.size.toString() + "")
            for (i in addrList.indices) {
                Log.w("cg_addrsiz", addrList.size.toString() + "")
                delivery_addrSpinner!!.setSelection(i)

//                if (addrList.get(i).getDeliveryAddress().equals(productsModel.getDefaultUom())) {
//                    Log.d("cg_addr_", productsModel.getDefaultUom());
//                    delivery_addrSpinner.setSelection(i);
//                    break;
//                }
            }
        }
        delivery_addrSpinner!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (isEditItem) {
                    var index = 0
                    for (model in deliveryAddressModels) {
                        if (model.deliveryAddress.equals(deliverAddrNameStr, ignoreCase = true)) {
                            delivery_addrSpinner!!.setSelection(index)
                            deliverAddrNameStr = model.deliveryAddress
                            //                            uomText.setText(model.getUomCode());
//                            priceText.setText(model.getPrice());
                            Log.w("selectAddrEd :", deliverAddrNameStr + "")
                            break
                        }
                        index++
                    }
                } else {
                    deliverAddrNameStr = delivery_addrSpinner!!.selectedItem.toString()
                    //                    priceText.setText(uomList.get(position).getPrice());
                    Log.w("deliAddr :", deliveryAddressModels[position].deliveryAddress)
                    Log.w("SelectAddr:", deliverAddrNameStr + "")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setUomList(uomList: ArrayList<UomModel>) {
        Log.w("UOMList:", uomList.toString())
        val adapter = ArrayAdapter(this, R.layout.cust_spinner_item, uomList)
        uomSpinner!!.adapter = adapter
        setUOMCode(uomList)
        if (productsModel != null) {
            //setuom
            Log.d("cg_uomsiz1", uomList.size.toString() + "")
            for (i in uomList.indices) {
                Log.w("cg_uomsiz", uomList.size.toString() + "")
                if (uomList[i].uomCode == productsModel!!.defaultUom) {
                    Log.w("cg_uoomcode_", productsModel!!.defaultUom)
                    uomSpinner!!.setSelection(i)
                    uomText!!.setText(uomList[i].uomCode)
                    priceText!!.setText(uomList[i].price)

                    break
                }
            }
        }
    }


    private fun setUOMCode(uomList: ArrayList<UomModel>) {
        uomSpinner!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (isEditItem) {
                    selectValueEdit(uomList, uomName)
                } else {
                    uomName = uomSpinner!!.selectedItem.toString()
                    uomText!!.setText(uomList[position].uomCode)
                    priceText!!.setText(uomList[position].price)
                    Log.w("UOMQtyValue:", uomList[position].uomEntry)
                    Log.w("SelectedUOM:", uomName + "")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    private fun selectValue(uomList: ArrayList<UomModel>, value: String) {
        try {
            var index = 0
            for (model in uomList) {
                if (model.uomCode.equals(value, ignoreCase = true)) {
                    uomSpinner!!.setSelection(index)
                    uomName = model.uomCode
                    uomText!!.setText(model.uomCode)
                    priceText!!.setText(model.price)
                    Log.w("SelectedUOMEd:", uomName + "")
                    break
                }
                index++
            }
        } catch (exception: Exception) {
        }
    }

    private fun selectValueEdit(uomList: ArrayList<UomModel>, value: String) {
        try {
            var index = 0
            for (model in uomList) {
                if (model.uomCode.equals(value, ignoreCase = true)) {
                    uomSpinner!!.setSelection(index)
                    uomName = model.uomCode
                    uomText!!.setText(model.uomCode)
//                    priceText!!.setText(model.price)

                    Log.w("SelectedUOMEdit:", uomName + "")
                    break
                }
                index++
            }
        } catch (exception: Exception) {
        }
    }

    fun openKeyborard(editText: EditText?) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun filter(text: String) {
        try {
            settings = dbHelper!!.settings
            if (settings!!.size > 0) {
                for (model in settings!!) {
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
                    if (stockProductView == "1") {
                        if (s.stockQty != null && s.stockQty != "null") {
                            if (s.stockQty.toDouble() > 0) {
                                filterProducts.add(s)
                            }
                        }
                    } else if (stockProductView == "0") {
                        if (s.stockQty != null && s.stockQty != "null") {
                            if (s.stockQty.toDouble() < 0 || s.stockQty.toDouble() == 0.0) {
                                filterProducts.add(s)
                            }
                        }
                    } else {
                        filterProducts.add(s)
                    }
                }
            }
            //calling a method of the adapter class and passing the filtered list
            selectProductAdapter!!.filterList(filterProducts)

            //setAdapter(filterProducts);
            totalProducts!!.text = filterProducts.size.toString() + " Products"
        } catch (ex: Exception) {
            Log.e("Error_in_filter", Objects.requireNonNull(ex.message!!))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                /*   Intent intent = new Intent(CreateNewInvoiceActivity.this, CustomerListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/finish()
                true
            }

            R.id.action_down -> {
                billDischide()
               // billDiscountBottomSheet()
                true
            }

            R.id.action_save -> {
                save_btn!!.setEnabled(b)
                val localCart = dbHelper!!.allInvoiceProducts
                if (localCart.size > 0) {
                    save_btn!!.setEnabled(true)
                    if (activityFrom == "iv" || activityFrom == "ConvertInvoice" ||
                        activityFrom == "Duplicate"
                    ) {
//                        showSaveAlert()
                        if (isCheckedCreditLimit) {
                            if (createInvoiceValidation()) {
                                showSaveAlert();
                            } else {
                                showAlertForCreditLimit();
                            }
                        } else {
                            showSaveAlert();
                        }
                    } else {
                        showSaveAlert()
                    }
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

            else -> super.onOptionsItemSelected(item)
        }
        //  return super.onOptionsItemSelected(item);
    }

    fun billDischide(){
        if (bill_disc_layl!!.getVisibility() == View.VISIBLE) {
            bill_disc_layl!!.setVisibility(View.GONE)
            if (behavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                behavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            //slideUp(searchFilterView);
        } else {
            bill_disc_layl!!.setVisibility(View.VISIBLE)
            if (behavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                behavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    fun createInvoiceValidation(): Boolean {
        var canCreateInvoice = false
        if (creditLimitAmount.toDouble() == 0.00) {
            canCreateInvoice = true
        } else if (creditLimitAmount.toDouble() > 0.00) {
            canCreateInvoice = if (outstandingAmount.toDouble() < creditLimitAmount.toDouble()) {
                val remainingAmount = creditLimitAmount.toDouble() - outstandingAmount.toDouble()
                val net_total = netTotalValue!!.text.toString().toDouble()
                if (remainingAmount >= net_total) {
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
        return canCreateInvoice
    }

    fun showAlertForCreditLimit() {
        val builder1 = AlertDialog.Builder(this@CreateNewInvoiceActivityBillDisc)
        builder1.setTitle("Information")
        builder1.setMessage(
            customerNameText!!.getText()
                .toString() + "- Your Credit Limit exceed Can't create Invoice"
        )
        builder1.setCancelable(false)
        builder1.setPositiveButton(
            "Ok"
        ) { dialog, id ->
            dialog.cancel()
        }
        val alert11 = builder1.create()
        alert11.show()
    }

    fun showSaveAlert() {
        try {
            // create an alert builder
            val builder = AlertDialog.Builder(this@CreateNewInvoiceActivityBillDisc)
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
            //invoicePrintCheck.setVisibility(View.GONE);
            if (activityFrom == "so" || activityFrom == "SalesEdit") {
                saveTitle!!.setText("Save SalesOrder")
                saveMessage!!.setText("Are you sure want to save SalesOrder?")
                invoicePrintCheck!!.setText("SalesOrder Print")
                if (isCheckedSalesPrint) {
                    invoicePrintCheck!!.setChecked(true)
                    isPrintEnable = true
                    copyLayout.visibility = View.VISIBLE
                } else {
                    invoicePrintCheck!!.setChecked(false)
                    isPrintEnable = false
                    copyLayout.visibility = View.GONE
                }
            } else if (activityFrom == "do" || activityFrom == "doEdit") {
                saveTitle!!.setText("Save Delivery Order")
                saveMessage!!.setText("Are you sure want to save Delivery Order?")
                invoicePrintCheck!!.setText("Delivery Order Print")
                invoicePrintCheck!!.setVisibility(View.GONE)
                if (isCheckedSalesPrint) {
                    invoicePrintCheck!!.setChecked(true)
                    isPrintEnable = true
                    copyLayout.visibility = View.VISIBLE
                } else {
                    invoicePrintCheck!!.setChecked(false)
                    isPrintEnable = false
                    copyLayout.visibility = View.GONE
                }
            } else {
                if (isCheckedInvoicePrint) {
                    invoicePrintCheck!!.setChecked(true)
                    isPrintEnable = true
                    copyLayout.visibility = View.VISIBLE
                } else {
                    invoicePrintCheck!!.setChecked(false)
                    isPrintEnable = false
                    copyLayout.visibility = View.GONE
                }
            }
            signatureButton.setOnClickListener { showSignatureAlert() }
            invoicePrintCheck!!.setOnClickListener(View.OnClickListener {
                if (invoicePrintCheck!!.isChecked()) {
                    isPrintEnable = true
                    copyLayout.visibility = View.VISIBLE
                } else {
                    isPrintEnable = false
                    copyLayout.visibility = View.GONE
                }
            })
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

            // create and show the alert dialog
            alert = builder.create()
            alert!!.show()
            okButton!!.setOnClickListener(View.OnClickListener { view1: View? ->
                try {
                    Log.w("entyyinv","")
                    if (companyCode == "AADHI INTERNATIONAL PTE LTD") {
                        if (activityFrom == "iv" || activityFrom == "ConvertInvoice"
                            || activityFrom == "Duplicate"
                        ) {
                            if (signatureString != null && !signatureString!!.isEmpty()) {
                                alert!!.dismiss()
                                if (activityFrom == "iv" || activityFrom == "ConvertInvoice"
                                    || activityFrom == "Duplicate"
                                ) {
                                    createInvoiceJson(noOfCopy.text.toString().toInt())
                                } else if (activityFrom == "so" || activityFrom == "SalesEdit") {
                                    createSalesOrderJson(noOfCopy.text.toString().toInt())
                                } else if (activityFrom == "do" || activityFrom == "doEdit") {
                                    createDOJson(noOfCopy.text.toString().toInt())
                                }
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Add Signature to Save Invoice",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            alert!!.dismiss()
                            if (activityFrom == "iv" || activityFrom == "ConvertInvoice"
                                || activityFrom == "Duplicate"
                            ) {
                                createInvoiceJson(noOfCopy.text.toString().toInt())
                            } else if (activityFrom == "so" || activityFrom == "SalesEdit") {
                                createSalesOrderJson(noOfCopy.text.toString().toInt())
                            } else if (activityFrom == "do" || activityFrom == "doEdit") {
                                createDOJson(noOfCopy.text.toString().toInt())
                            }
                        }
                    } else {
                        alert!!.dismiss()
                        if (activityFrom == "iv" || activityFrom == "ConvertInvoice"
                            || activityFrom == "Duplicate"
                        ) {
                            Log.w("signavail", "" + isSignatureSetting + ".." + signatureString)

                            if (isSignatureSetting) {
                                if (signatureString != null && !signatureString!!.isEmpty()) {
                                    createInvoiceJson(noOfCopy.text.toString().toInt())
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Add Signature to Save Invoice",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                createInvoiceJson(noOfCopy.text.toString().toInt())
                            }
                        } else if (activityFrom == "so" || activityFrom == "SalesEdit") {
                            createSalesOrderJson(noOfCopy.text.toString().toInt())
                        } else if (activityFrom == "do" || activityFrom == "doEdit") {
                            createDOJson(noOfCopy.text.toString().toInt())
                        }
                    }
                } catch (exception: Exception) {
                }
            })
            cancelButton!!.setOnClickListener(View.OnClickListener {
                save_btn!!.setEnabled(true)
                alert!!.cancel()
            })
        } catch (exception: Exception) {
        }
    }

    fun saveSalesOrder(jsonBody: JSONObject, action: String, copy: Int) {
        try {
            pDialog =
                SweetAlertDialog(this@CreateNewInvoiceActivityBillDisc, SweetAlertDialog.PROGRESS_TYPE)
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
            Log.w("GivenSalesOrdReq :", jsonBody.toString())
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
                    save_btn!!.setEnabled(true)
                    val statusCode = response.optString("statusCode")
                    val message = response.optString("statusMessage")
                    var responseData: JSONObject? = null
                    try {
                        responseData = response.getJSONObject("responseData")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    if (statusCode == "1") {
                        signatureString = ""
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
                        } else if (action == "DeliveryOrder" || action == "DeliveryOrderEdit") {
                            val intent =
                                Intent(applicationContext, DeliveryOrderListActivity::class.java)
                            startActivity(intent)
                            finish()
                            /*  if (isPrintEnable){
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
                        }*/isPrintEnable = false
                        } else {
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
                        val jsonObject = JSONObject()
                        try {
                            jsonObject.put("User", username)
                            jsonObject.put("CardCode", customerCode)
                            jsonObject.put("ItemGroupCode", "All")
                            getAllProducts(jsonObject)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
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
                    save_btn!!.setEnabled(true)
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
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
        val jsonObjectRequest: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, jsonObject,
                Response.Listener { response: JSONObject ->
                    try {
                        progressDialog.dismiss()
                        Log.w("res_custPdt:", response.toString())
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
                                model.creditLimitAmount = `object`.optString("creditLimit")
                                creditLimitAmount = `object`.optString("creditLimit")
                                outstandingAmount = `object`.optString("outstandingAmount")

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
                    Log.d("Error_throwing:", error.toString())
                    progressDialog.dismiss()
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
        jsonObjectRequest.setRetryPolicy(
            DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
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
                    //  if (!itemCode.equals("Select Brand")){
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

    @get:Throws(JSONException::class)
    private val grouplist: ArrayList<ItemGroupList>
        private get() {
            val requestQueue = Volley.newRequestQueue(this)
            val url = Utils.getBaseUrl(applicationContext) + "ItemGroupList"
            // Initialize a new JsonArrayRequest instance
            Log.w("Given_url_group:", url)
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
                            Log.w("grouplist:", response.toString())

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
        orderNoText!!.setText("")
        val intent: Intent
        intent = if (activityFrom == "iv" || activityFrom == "Duplicate") {
            Intent(this@CreateNewInvoiceActivityBillDisc, NewInvoiceListActivity::class.java)
        } else {
            Intent(this@CreateNewInvoiceActivityBillDisc, SalesOrderListActivity::class.java)
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
        save_btn = menu.findItem(R.id.action_save)
        down_menu = menu.findItem(R.id.action_down)
        if (activityFrom == "iv" || activityFrom == "ConvertInvoice" || activityFrom == "Duplicate") {
            down_menu!!.isVisible = true
        }
        return true
    }

    fun showDeleteAlert() {
        val builder1 = AlertDialog.Builder(this@CreateNewInvoiceActivityBillDisc)
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
        jsonObject.put("LocationCode", locationCode)
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "InvoiceDetails"
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_urlInv:", "$url ..$jsonObject")
        invoiceHeaderDetails = ArrayList()
        invoicePrintList = ArrayList()
        salesReturnList = ArrayList()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            jsonObject,
            Response.Listener { response: JSONObject ->
                try {
                    Log.w("DetailsResponse:: ", response.toString())
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
                            val qty1 = detailObject.optString("quantity").toDouble()
                            val price1 = detailObject.optString("price").toDouble()
                            val nettotal1 = qty1 * price1
                            invoiceListModel.total = detailObject.optString("total")
                            invoiceListModel.pricevalue = price1.toString()
                            invoiceListModel.uomCode = detailObject.optString("uomCode")
                            invoiceListModel.pcsperCarton = detailObject.optString("pcsPerCarton")
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
            },
            Response.ErrorListener { error: VolleyError ->
                // Do something when error occurred
                //  pDialog.dismiss();
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

    fun printInvoice(copy: Int) {
        try {
            /* if (pDialog!=null && pDialog.isShowing()){
                pDialog.dismiss();
            }*/
            PrinterUtils(this, printerMacId).printInvoice(
                copy,
                invoiceHeaderDetails,
                invoicePrintList,
                "false"
            )
            Utils.setSignature("")
        } catch (e: Exception) {
        }
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
        val jsonObjectRequest: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, jsonObject,
                Response.Listener { response: JSONObject ->
                    try {
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
                                        .isEmpty() && detailObject.optString("ReturnQty")
                                        .toDouble() > 0
                                ) {
                                    salesListModel = SalesList()
                                    salesListModel.productCode =
                                        detailObject.optString("ProductCode")
                                    salesListModel.description =
                                        detailObject.optString("ProductName")
                                    salesListModel.lqty = detailObject.optString("LQty")
                                    salesListModel.cqty = detailObject.optString("CQty")
                                    salesListModel.netQty =
                                        "-" + detailObject.optString("ReturnQty")
                                    val qty12 = detailObject.optString("ReturnQty").toDouble()
                                    val price12 = detailObject.optString("Price").toDouble()
                                    val nettotal12 = qty12 * price12
                                    salesListModel.total = nettotal12.toString()
                                    salesListModel.pricevalue = price12.toString()
                                    salesListModel.uomCode = detailObject.optString("UOMCode")
                                    salesListModel.cartonPrice =
                                        detailObject.optString("CartonPrice")
                                    salesListModel.unitPrice = detailObject.optString("Price")
                                    salesListModel.pcsperCarton =
                                        detailObject.optString("PcsPerCarton")
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

    fun createInvoiceJson(copy: Int) {
        // JSONObject rootJsonObject = new JSONObject();
        val rootJsonObject = JSONObject()
        val invoiceDetailsArray = JSONArray()
        var returnProductArray = JSONArray()
        var returnProductObject = JSONObject()
        var invoiceObject = JSONObject()
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
                rootJsonObject.put("doNo", "")
            } else if (activityFrom == "ConvertInvoiceFromDO") {
                rootJsonObject.put("mode", "I")
                rootJsonObject.put("soNo", "")
                rootJsonObject.put("doNo", editDoNumber)
                rootJsonObject.put("invoiceNumber", "")
            } else if (activityFrom == "EditDo") {
                rootJsonObject.put("mode", "E")
                rootJsonObject.put("soNo", "")
                rootJsonObject.put("doNo", editDoNumber)
                rootJsonObject.put("invoiceNumber", "")
            } else {
                rootJsonObject.put("invoiceNumber", "")
                rootJsonObject.put("mode", "I")
                rootJsonObject.put("soNo", "")
                rootJsonObject.put("doNo", "")
            }
            if (currentSaveDateTime == null || currentSaveDateTime!!.isEmpty()) {
                val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val currentDateandTime = sdf.format(Date())
                currentSaveDateTime = currentDateandTime
            }
            remarkStr = remarkText!!.text.toString()
            Log.w("remarkkc  ", "..$remarkStr")
            rootJsonObject.put("customerReferenceNo", orderNoText!!.text.toString())
            rootJsonObject.put("currentDateTime", currentSaveDateTime)
            rootJsonObject.put("invoiceDate", currentDate)
            rootJsonObject.put("customerCode", `object`["customerCode"])
            rootJsonObject.put("customerName", `object`["customerName"])
            rootJsonObject.put("address", `object`["address"])
            rootJsonObject.put("street", `object`["street"])
            rootJsonObject.put("city", `object`["city"])
            rootJsonObject.put("creditLimit", `object`["creditLimit"])
            rootJsonObject.put("Remark", remarkStr)
            rootJsonObject.put("currencyName", "Singapore Dollar")
            rootJsonObject.put("taxTotal", taxValueText!!.text.toString())
            rootJsonObject.put("subTotal", subTotalValue!!.text.toString())
            rootJsonObject.put("total", subTotalValue!!.text.toString())
            rootJsonObject.put("netTotal", netTotalValue!!.text.toString())
            rootJsonObject.put("itemDiscount", discountStr)
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
            rootJsonObject.put("delAddress1", deliverAddrNameStr)
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
                invoiceObject.put("price", Utils.fourDecimalPoint(model.price.toDouble()))
                invoiceObject.put("total", Utils.twoDecimalPoint(model.total.toDouble()))
                invoiceObject.put("itemDiscount", model.itemDisc)
                invoiceObject.put("totalTax", Utils.twoDecimalPoint(model.gstAmount.toDouble()))
                invoiceObject.put("subTotal", Utils.twoDecimalPoint(model.subTotal.toDouble()))
                invoiceObject.put("netTotal", Utils.twoDecimalPoint(model.netTotal.toDouble()))
                invoiceObject.put("taxType", `object`.optString("taxType"))
                invoiceObject.put("taxPerc", `object`.optString("taxPercentage"))
                var return_subtotal = 0.0
                if (model.returnQty != null && !model.returnQty.isEmpty() && model.returnQty != "null") {
                    return_subtotal = model.returnQty.toDouble() * model.price.toDouble()
                }
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
            Log.w("RootJsonSaveInv:", rootJsonObject.toString())
            Timber.d("APIRESPON: " + rootJsonObject.toString())

            saveSalesOrder(rootJsonObject, "Invoice", copy)
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.w("Given_Error:", Objects.requireNonNull(e.message!!))
        }
    }

    fun createDOJson(copy: Int) {
        val rootJsonObject = JSONObject()
        val saleDetailsArray = JSONArray()
        var deliveryObject = JSONObject()
        try {
            // Sales Header Add values
            val detailsArray = customerResponse.optJSONArray("responseData")
            val `object` = detailsArray.optJSONObject(0)
            if (activityFrom == "doEdit") {
                rootJsonObject.put("doNumber", editDoNumber.toString())
                rootJsonObject.put("mode", "E")
                rootJsonObject.put("status", "O")
            } else {
                rootJsonObject.put("doNumber", "")
                rootJsonObject.put("mode", "I")
                rootJsonObject.put("status", "")
            }
            rootJsonObject.put("doDate", currentDate)
            rootJsonObject.put("customerCode", `object`.optString("customerCode"))
            rootJsonObject.put("customerName", `object`.optString("customerName"))
            rootJsonObject.put("address", `object`.optString("address"))
            rootJsonObject.put("street", `object`.optString("street"))
            rootJsonObject.put("city", `object`.optString("city"))
            rootJsonObject.put("creditLimit", `object`.optString("creditLimit"))
            rootJsonObject.put("remark", remarkStr)
            rootJsonObject.put("currencyName", "Singapore Dollar")
            rootJsonObject.put("total", subTotalValue!!.text.toString())
            rootJsonObject.put("itemDiscount", "0.00")
            rootJsonObject.put("billDiscount", "0.00")
            rootJsonObject.put("billDiscountPercentage", "0")
            rootJsonObject.put("subTotal", subTotalValue!!.text.toString())
            rootJsonObject.put("taxTotal", taxValueText!!.text.toString())
            rootJsonObject.put("netTotal", netTotalValue!!.text.toString())
            rootJsonObject.put("delCustomerName", "")
            rootJsonObject.put("delAddress1", deliverAddrNameStr)
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

            // Sales Details Add to the Objects
            val localCart = dbHelper!!.allInvoiceProducts
            var index = 1
            Log.w("Given_local_cart_size:", localCart.size.toString())
            for (model in localCart) {
                deliveryObject = JSONObject()
                /*   if (activityFrom.equals("InvoiceEdit")){
                    rootJsonObject.put("invoiceNumber", AddInvoiceActivity.editInvoiceNumber);
                }else {
                    rootJsonObject.put("invoiceNumber", "");
                }*/deliveryObject.put("invoiceNumber", "")
                deliveryObject.put("companyCode", companyCode)
                deliveryObject.put("invoiceDate", currentDate)
                deliveryObject.put("slNo", index)
                deliveryObject.put("productCode", model.productCode)
                deliveryObject.put("productName", model.productName)
                // convert into int
                deliveryObject.put("price", Utils.twoDecimalPoint(model.price.toDouble()))
                deliveryObject.put("total", Utils.twoDecimalPoint(model.total.toDouble()))
                deliveryObject.put("itemDiscount", "0.00")
                deliveryObject.put("totalTax", Utils.twoDecimalPoint(model.gstAmount.toDouble()))
                deliveryObject.put("subTotal", Utils.twoDecimalPoint(model.subTotal.toDouble()))
                deliveryObject.put("netTotal", Utils.twoDecimalPoint(model.netTotal.toDouble()))
                deliveryObject.put("taxType", `object`.optString("taxType"))
                deliveryObject.put("taxPerc", `object`.optString("taxPercentage"))
                var return_subtotal = 0.0
                if (model.returnQty != null && !model.returnQty.isEmpty() && model.returnQty != "null") {
                    return_subtotal = model.returnQty.toDouble() * model.price.toDouble()
                }
                assert(model.returnQty != null)
                if (!model.returnQty.isEmpty() && model.returnQty.toString() != "null") {
                    deliveryObject.put("returnLQty", model.returnQty)
                    deliveryObject.put("returnQty", model.returnQty)
                    deliveryObject.put("qty", model.actualQty.toString())
                } else {
                    deliveryObject.put("returnLQty", "0")
                    deliveryObject.put("returnQty", "0")
                    deliveryObject.put("qty", model.actualQty.toString())
                }
                if (!model.focQty.toString().isEmpty() && model.focQty != "null") {
                    deliveryObject.put("focQty", model.focQty)
                } else {
                    deliveryObject.put("focQty", "0")
                }
                deliveryObject.put("returnSubTotal", Utils.twoDecimalPoint(return_subtotal))
                deliveryObject.put("returnNetTotal", Utils.twoDecimalPoint(return_subtotal))
                deliveryObject.put("taxCode", `object`.optString("taxCode"))
                deliveryObject.put("returnReason", "")
                deliveryObject.put("uomCode", model.uomCode)
                deliveryObject.put("itemRemarks", "")
                deliveryObject.put("locationCode", locationCode)
                deliveryObject.put("createUser", username)
                deliveryObject.put("modifyUser", username)
                saleDetailsArray.put(deliveryObject)
                index++
            }
            rootJsonObject.put("PostingDeliveryOrderDetails", saleDetailsArray)
            Log.w("RootSaveJson:", rootJsonObject.toString())
            saveSalesOrder(rootJsonObject, "DeliveryOrder", copy)
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.w("Given_Error:", Objects.requireNonNull(e.message!!))
        }
    }

    @Throws(IOException::class)
    private fun sentSalesOrderDataPrint(copy: Int) {
        if (printerType == "TSC Printer") {
            val printer = TSCPrinter(this@CreateNewInvoiceActivityBillDisc, printerMacId, "SalesOrder")
            printer.printSalesOrder(copy, salesOrderHeaderDetails, salesPrintList)
            Utils.setSignature("")
        } else if (printerType == "Zebra Printer") {
            val zebraPrinterActivity =
                ZebraPrinterActivity(this@CreateNewInvoiceActivityBillDisc, printerMacId)
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
        var discTextWatcher: TextWatcher? = null
        var progressDialog: ProgressDialog? = null
        var stockProductView = "2"
        var barcodeText: EditText? = null
        var customerCode: String? = null
        var isPrintEnable = false
        var selectedBank: TextView? = null
        var amountText: EditText? = null
        var currentLocationLatitude = 0.0
        var currentLocationLongitude = 0.0
        var signatureString: String? = ""
        var imageString: String? = null
        var current_latitude = "0.00"
        var current_longitude = "0.00"
        var currentDate: String? = null
        var customerResponse = JSONObject()

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