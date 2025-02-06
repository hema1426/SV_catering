package com.winapp.svcatering.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Process
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.winapp.svcatering.CommonMethods
import com.winapp.svcatering.R
import com.winapp.svcatering.activity.MainHomeActivity.MultipleCompanyModel
import com.winapp.svcatering.db.DBHelper
import com.winapp.svcatering.model.AddressZoneModel
import com.winapp.svcatering.model.UserRoll
import com.winapp.svcatering.utils.Constants
import com.winapp.svcatering.utils.ImageUtil
import com.winapp.svcatering.utils.InternetConnector_Receiver
import com.winapp.svcatering.utils.SessionManager
import com.winapp.svcatering.utils.SharedPreferenceUtil
import com.winapp.svcatering.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    // Define the variables
    private var userIdText: EditText? = null
    private var passwordText: EditText? = null
    private var loginButton: Button? = null
    private var isEmailValid = false
    private var isPasswordValid = false
    private var session: SessionManager? = null
    private val registerText: TextView? = null
    private var sharedPreferenceUtil: SharedPreferenceUtil? = null
    private var pDialog: SweetAlertDialog? = null
    private var activityFrom: String? = null
    private var passwordToggle: ImageView? = null
    private var dbHelper: DBHelper? = null
    private var usernameLayout: LinearLayout? = null
    private var passwordLayout: LinearLayout? = null
    private var mainLayout: LinearLayout? = null
    private var rememberMe: CheckBox? = null
    private var loginPreferences: SharedPreferences? = null
    private var loginPrefsEditor: SharedPreferences.Editor? = null
    private var saveLogin: Boolean? = null
    private var registerButton: Button? = null
    private val companyName: TextView? = null
    private val address1Text: TextView? = null
    private val address2Text: TextView? = null
    private var lastBackPressTime: Long = 0
    private val rememberStr: String? = null
    private var newSelectedCompany: String? = null
    private var newSelectedCompanyName: String? = null
    private var locationCode: String? = null
    var addressZoneList: ArrayList<AddressZoneModel> = ArrayList()
    private var spinner_addrl: Spinner? = null
    private var addressCode: String? = ""
    private var addressName: String? = ""
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        if (Utils.isTablet(this)) {
            setContentView(R.layout.sigin_layout_tablet)
        } else {
            setContentView(R.layout.activity_login)
        }
        Log.w("New Code updated:", "Success")
        activityFrom = intent.getStringExtra("from")
        passwordToggle = findViewById(R.id.password_toggle)
        usernameLayout = findViewById(R.id.user_name_layout)
        passwordLayout = findViewById(R.id.password_layout)
        mainLayout = findViewById(R.id.main_layout)
        rememberMe = findViewById(R.id.remember_me)
        userIdText = findViewById(R.id.email_id)
        passwordText = findViewById(R.id.password)
        loginButton = findViewById(R.id.btn_login)
        registerButton = findViewById(R.id.btn_register)
        spinner_addrl = findViewById(R.id.spinner_addr_zone)

        loginButton!!.setOnClickListener(this)
        dbHelper = DBHelper(this)
        session = SessionManager(this)
        sharedPreferenceUtil = SharedPreferenceUtil(this)

        // Set the Preference value in edittext for Remembering the values
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        loginPrefsEditor = loginPreferences!!.edit()
        saveLogin = loginPreferences!!.getBoolean("saveLogin", false)
        if (saveLogin!!) {
            userIdText!!.setText(loginPreferences!!.getString("username", ""))
            passwordText!!.setText(loginPreferences!!.getString("password", ""))
            rememberMe!!.setChecked(true)
        }
        passwordToggle!!.setOnClickListener(View.OnClickListener {
            if (passwordToggle!!.getTag() == "show") {
                passwordText!!.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                passwordToggle!!.setTag("hide")
                passwordToggle!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_hide
                    )
                )
            } else {
                passwordText!!.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                passwordToggle!!.setTag("show")
                passwordToggle!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_eye
                    )
                )
            }
            passwordText!!.setSelection(passwordText!!.length())
        })
        registerButton!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        })
        setAddressZone()

        // init the variables
        init()
    }

    private fun setWidthHeight(view: View) {
        val params = view.layoutParams
        // Changes the height and width to the specified *pixels*
        params.height = 50
        params.width = 500
        view.layoutParams = params
    }

    private fun init() {
        passwordText!!.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= passwordText!!.right - passwordText!!.totalPaddingRight) {
                    // your action for drawable click event
                    if (passwordToggle!!.tag == "show") {
                        passwordText!!.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        passwordToggle!!.tag = "hide"
                        passwordText!!.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_hide,
                            0
                        )
                    } else {
                        passwordText!!.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        passwordToggle!!.tag = "show"
                        passwordText!!.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_eye,
                            0
                        )
                    }
                    passwordText!!.setSelection(passwordText!!.length())
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btn_login) {
            if (InternetConnector_Receiver.isConnectingToInternet(this)) {
                try {
                    validateSession()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun goToRegister() {}
    @Throws(JSONException::class)
    private fun validateSession() {
        if (userIdText!!.text.toString().isEmpty()) {
            userIdText!!.error = "User Id is Empty"
            isEmailValid = false
        } /*else if (!Patterns.EMAIL_ADDRESS.matcher(emailIdText.getText().toString()).matches()) {
            emailIdText.setError(getResources().getString(R.string.invalid_email));
            isEmailValid = false;
        }*/ else {
            isEmailValid = true
        }
        // Check for a valid password.
        if (passwordText!!.text.toString().isEmpty()) {
            passwordText!!.error = resources.getString(R.string.error_password)
            isPasswordValid = false
        } else if (passwordText!!.text.length < 3) {
            passwordText!!.error = resources.getString(R.string.invalid_password)
            isPasswordValid = false
        } else {
            isPasswordValid = true
        }
        if (isEmailValid && isPasswordValid) {
            setSession(userIdText!!.text.toString(), passwordText!!.text.toString())
        }
    }

    @Throws(JSONException::class)
    private fun setSession(userId: String, password: String) {
        // Initialize a new RequestQueue instance
        val requestQueue = Volley.newRequestQueue(this)
        val jsonObject = JSONObject()
        jsonObject.put("Username", userId)
        jsonObject.put("Password", password)
        // http://172.16.5.60:8345/api/Login
        val url = Utils.getBaseUrl(this) + "Login"
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_login_URL:", url + jsonObject)
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog!!.setTitleText("Authenticating...")
        pDialog!!.setCancelable(false)
        pDialog!!.show()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            jsonObject,
            Response.Listener { response: JSONObject ->
                try {
                    Log.w("Res_SAP_login:", response.toString())
                    if (response.length() > 0) {
                        //  {"statusCode":1,"statusMessage":"Success",
                        //  "responseData":[{"userName":"User1","roleName":"DepartmentHead","userID":"1","companyCode":"WINAPP_DEMO",
                        //  "companyName":"WINAPP_DEMO","address1":"1 XYZ Chennai  IN 600001","address2":"     "}]}
                        val statusCode = response.optString("statusCode")
                        if (statusCode == "1") {
                            val userArray = response.optJSONArray("responseData")!!
                            val `object` = userArray.optJSONObject(0)
                            val username = `object`.optString("userName")
                            val rollname = `object`.optString("roleName")
                            // String locationCode=object.optString("LocationCode");
                            // String isuserpermission=object.optString("IsUserPermission");
                            // String ismainlocation=object.optString("IsMainLocation");

                            //  : {"statusCode":1,"statusMessage":"Success","responseData":[{"userName":"AADHIVAN 1","roleName":"","userID":"7",
                            //  "companyCode":"AADHI INTERNATIONAL PTE LTD","companyName":"AADHI INTERNATIONAL PTE LTD","address1":"101 Cecil street  ,
                            //  #20-11,\r\rSG-069533","address2":"","streetPO":"101 Cecil street  , #20-11","streetNO":"Tong Eng Building","zipcode":"069533",
                            //  "country":"SG","phone1":"+65 61006061"}]}
                            val companycode = `object`.optString("companyCode")
                            val companyname = `object`.optString("companyName")
                            val address1 = `object`.optString("streetPO")
                            val address2 = `object`.optString("streetNO")
                            val address3 =
                                `object`.optString("countryName") + "-" + `object`.optString("zipcode")
                            val postalcode = `object`.optString("zipcode")
                            val country = `object`.optString("countryName")
                            val phone = `object`.optString("phone1")
                            val gstNo = `object`.optString("gstNo")
                            val locationCode = `object`.optString("warehouse")
                            val ispermission = `object`.optString("locationAuthorization")
                            val logo = `object`.optString("logo")
                            val qrcode = `object`.optString("qrCode")
                            val paid = `object`.optString("paid")
                            val unpaid = `object`.optString("unPaid")
                            val paynow = `object`.optString("payNow")
                            val bank = `object`.optString("bank")
                            val cheque = `object`.optString("cheque")
                            val salesManName = `object`.optString("salesPersonName")
                            val salesManPhone = `object`.optString("salesPersonMobile")
                            val salesManMail = `object`.optString("salesPersonEmail")
                            val salesManOffice = `object`.optString("salesPersonOfficeNo")
                            val negativeStock = `object`.optString("allowNegativeStock")
                            val userMiddlename = `object`.optString("userMiddleName")
                            val invUOM = `object`.optString("invoiceDefaultUOM")
                            val salesUOM = `object`.optString("salesOrderDefaultUOM")
                            val returnUOM = `object`.optString("salesRetunDefaultUOM")
                            val settleNextDate = `object`.optString("haveSettlementByDate")
                            val shortCode = `object`.optString("shortCode")
                            val lastPrice = `object`.optString("showlastSalesPrice")
                            val totalSales = `object`.optString("invoiceListShowBalance")
                            sharedPreferenceUtil!!.setStringPreference(
                                sharedPreferenceUtil!!.KEY_SETTING_INV_UOM,
                                invUOM
                            )
                            sharedPreferenceUtil!!.setStringPreference(
                                sharedPreferenceUtil!!.KEY_SETTING_SO_UOM,
                                salesUOM
                            )
                            sharedPreferenceUtil!!.setStringPreference(
                                sharedPreferenceUtil!!.KEY_SETTING_RETURN_UOM,
                                returnUOM
                            )
                            sharedPreferenceUtil!!.setStringPreference(
                                sharedPreferenceUtil!!.KEY_SETTLEMENT_NEXT_DATE,
                                settleNextDate
                            )
                            //mahudoom given "shortCode": "TRAN",
                            sharedPreferenceUtil!!.setStringPreference(
                                sharedPreferenceUtil!!.KEY_SHORT_CODE,
                                shortCode
                            )
                            sharedPreferenceUtil!!.setStringPreference(
                                sharedPreferenceUtil!!.KEY_LAST_PRICE,
                                lastPrice
                            )
                            sharedPreferenceUtil!!.setStringPreference(
                                sharedPreferenceUtil!!.KEY_TOTAL_SALES,
                                totalSales
                            )
                            sharedPreferenceUtil!!.setStringPreference(
                                sharedPreferenceUtil!!.KEY_USER_MIDDLE_NAME,
                                userMiddlename
                            )
                            session!!.createLoginSession(
                                username,
                                password,
                                rollname,
                                locationCode,
                                "1",
                                ispermission,
                                companycode,
                                companyname,
                                address1,
                                address2,
                                address3,
                                country,
                                postalcode,
                                phone,
                                gstNo,
                                logo,
                                qrcode,
                                paid,
                                unpaid,
                                paynow,
                                bank,
                                cheque,
                                salesManName,
                                salesManPhone,
                                salesManMail,
                                salesManOffice,
                                negativeStock
                            )
                            // Adding the Preference values to the Session to remember the values
                            if (rememberMe!!.isChecked) {
                                loginPrefsEditor!!.putBoolean("saveLogin", true)
                                loginPrefsEditor!!.putString("username", username)
                                loginPrefsEditor!!.putString("password", password)
                                loginPrefsEditor!!.commit()
                            } else {
                                loginPrefsEditor!!.clear()
                                loginPrefsEditor!!.commit()
                            }
                            // adding details for Loading Content of the Details....
                            if (!logo.isEmpty()) {
                                Utils.setLogo(logo)
                                try {
                                    ImageUtil.saveStamp(this, logo, "Logo")
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            } else {
                                val filePath = Constants.getSignatureFolderPath(this)
                                val fileName = "Logo.jpg"
                                val mFile = File(filePath, fileName)
                                if (mFile.exists()) {
                                    mFile.delete()
                                }
                                Utils.setLogo("")
                            }
                            if (!qrcode.isEmpty()) {
                                Utils.setQrcode(qrcode)
                                try {
                                    ImageUtil.saveStamp(this, qrcode, "QrCode")
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            } else {
                                val filePath = Constants.getSignatureFolderPath(this)
                                val fileName = "QrCode.jpg"
                                val mFile = File(filePath, fileName)
                                if (mFile.exists()) {
                                    mFile.delete()
                                }
                                Utils.setQrcode("")
                            }
                            if (!paid.isEmpty()) {
                                Utils.setPaid(paid)
                                try {
                                    ImageUtil.saveStamp(this, paid, "Paid")
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            } else {
                                val filePath = Constants.getSignatureFolderPath(this)
                                val fileName = "Paid.jpg"
                                val mFile = File(filePath, fileName)
                                if (mFile.exists()) {
                                    mFile.delete()
                                }
                                Utils.setPaid("")
                            }
                            if (!unpaid.isEmpty()) {
                                Utils.setUnpaid(unpaid)
                                try {
                                    ImageUtil.saveStamp(this, unpaid, "UnPaid")
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            } else {
                                val filePath = Constants.getSignatureFolderPath(this)
                                val fileName = "UnPaid.jpg"
                                val mFile = File(filePath, fileName)
                                if (mFile.exists()) {
                                    mFile.delete()
                                }
                                Utils.setUnpaid("")
                            }
                            newSelectedCompany = companycode
                            newSelectedCompanyName = companyname
                            this.locationCode = locationCode
                            Log.w(
                                "savlog11",
                                "" + loginPreferences!!.getBoolean("saveLogin", false)
                            )
                            pDialog!!.dismiss()
                            getPrinterSetting(username)
                            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                            intent.putExtra("isLogin", "1")
                            startActivity(intent)
                            finish()
                            //  getCompaniesList();
                            //  getUserRollPermission(companycode,rollname);
                        } else {
                            pDialog!!.dismiss()
                            Toast.makeText(
                                applicationContext,
                                "Invalid Username or Password",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                // Do something when error occurred
                pDialog!!.dismiss()
                Log.w("Error_throwing:", error.toString())
                Toast.makeText(applicationContext, "Server Error,Please check", Toast.LENGTH_LONG)
                    .show()
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
                                            setAddressSpinner(addressZoneList!!)
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

    fun setAddressSpinner(spinnerlist: ArrayList<AddressZoneModel>) {
        // spinner_addrl!!.setTitle("")
        val addressModel = AddressZoneModel("","Select Address Zone")
        spinnerlist.add(0, addressModel)

        val adapter = ArrayAdapter<String>(this, R.layout.cust_spinner_item)
        for (i in spinnerlist.indices) {
            adapter.add(spinnerlist[i].name)
        }

        spinner_addrl!!.adapter = adapter
        spinner_addrl!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View, position: Int, id: Long) {
                // On selecting a spinner item

                addressCode = spinnerlist[position].code
                addressName = spinnerlist[position].name
                Log.w("addrZone",""+addressCode)

                if(spinnerlist[position].code.isNotEmpty()) {
                    sharedPreferenceUtil!!.setStringPreference(
                        Constants.KEY_ADDRESS_ZONE_CODE, addressCode
                    )
                    sharedPreferenceUtil!!.setStringPreference(
                        Constants.KEY_ADDRESS_ZONE_NAME, addressName
                    )
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
    }


    @Throws(JSONException::class)
    fun getPrinterSetting(userId: String?) {
        // Initialize a new RequestQueue instance
        val requestQueue = Volley.newRequestQueue(this)
        val url = Utils.getBaseUrl(this) + "UserSettingFlag"
        val jsonObject = JSONObject()
        jsonObject.put("User", userId)
        Log.w("PrinterSettingURL:", url + jsonObject)
        val jsonArrayRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonObject, Response.Listener { response: JSONObject ->
                try {
                    Log.w("PrinterResponse:", response.toString())
                    // Loop through the array elements
                    val statusCode = response.optString("statusCode")
                    if (statusCode == "1") {
                        val customerDetailArray = response.optJSONArray("responseData")
                        for (i in 0 until customerDetailArray.length()) {
                            val `object` = customerDetailArray.optJSONObject(i)
                            dbHelper!!.insertSettings("showLogo", `object`.optString("showLogo"))
                            dbHelper!!.insertSettings(
                                "showSignature",
                                `object`.optString("showSignature")
                            )
                            dbHelper!!.insertSettings(
                                "showPaidOrUnpaidImage",
                                `object`.optString("showPaidOrUnpaidImage")
                            )
                            dbHelper!!.insertSettings(
                                "showQRCode",
                                `object`.optString("showQRCode")
                            )
                            dbHelper!!.insertSettings(
                                "showUserName",
                                `object`.optString("showUserName")
                            )
                            dbHelper!!.insertSettings("showUom", `object`.optString("showUom"))
                            dbHelper!!.insertSettings(
                                "showReturnDetails",
                                `object`.optString("showReturnDetails")
                            )
                            dbHelper!!.insertSettings("editSO", `object`.optString("editSO"))
                            dbHelper!!.insertSettings(
                                "showOutstandingAmount",
                                `object`.optString("showOutstandingAmount")
                            )
                            dbHelper!!.insertSettings(
                                "showLocationPermission",
                                `object`.optString("showLocationPermission")
                            )
                            //                                dbHelper.insertSettings("showDiscountAmount","true");
//                                dbHelper.insertSettings("discountAmountValidationFrom","4.0");
//                                dbHelper.insertSettings("discountAmountValidationTo","23.0");
                            dbHelper!!.insertSettings(
                                "showDiscountAmount",
                                `object`.optString("showDiscountAmount")
                            )
                            dbHelper!!.insertSettings(
                                "discountAmountValidationFrom",
                                `object`.optString("discountAmountValidationFrom")
                            )
                            dbHelper!!.insertSettings(
                                "discountAmountValidationTo",
                                `object`.optString("discountAmountValidationTo")
                            )
                            dbHelper!!.insertSettings(
                                "showSalesOrder",
                                `object`.optString("showSalesOrder")
                            )
                            dbHelper!!.insertSettings(
                                "showDeliveryOrder",
                                `object`.optString("showDeliveryOrder")
                            )
                            dbHelper!!.insertSettings(
                                "showInvoice",
                                `object`.optString("showInvoice")
                            )
                            dbHelper!!.insertSettings(
                                "showSalesReturn",
                                `object`.optString("showSalesReturn")
                            )
                            dbHelper!!.insertSettings(
                                "showCatelog",
                                `object`.optString("showCatelog")
                            )
                            dbHelper!!.insertSettings(
                                "showCustomer",
                                `object`.optString("showCustomer")
                            )
                            dbHelper!!.insertSettings(
                                "showProduct",
                                `object`.optString("showProduct")
                            )
                            dbHelper!!.insertSettings(
                                "showAPInvoice",
                                `object`.optString("showAPInvoice")
                            )
                            //                                dbHelper.insertSettings("HAVESETTLEMENTBYDATE",object.optString("haveSettlementByDate"));
                            dbHelper!!.insertSettings(
                                "haveEditPrice",
                                `object`.optString("haveEditPrice")
                            )
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Error,in getting Printer Settings",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                // Do something when error occurred
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

    @Throws(JSONException::class)
    fun getUserRollPermission(companyCode: String?, userRoll: String?) {
        // Initialize a new RequestQueue instance
        val requestQueue = Volley.newRequestQueue(this)
        // Initialize a new JsonArrayRequest instance
        val jsonObject = JSONObject()
        jsonObject.put("CompanyCode", companyCode)
        jsonObject.put("RoleName", userRoll)
        val userRolls = ArrayList<UserRoll>()
        val url =
            Utils.getBaseUrl(this) + "MerchandiseApi/GetMobileUserRolePermission?Requestdata=" + jsonObject.toString()
        Log.w("Given_url:", url)
        val jsonArrayRequest: JsonArrayRequest = object : JsonArrayRequest(Method.GET, url, null,
            Response.Listener { response: JSONArray ->
                try {
                    Log.w("Response_is_UserRoll:", response.toString())
                    if (response.length() > 0) {
                        dbHelper!!.removeAllUserPermission()
                        for (i in 0 until response.length()) {
                            val `object` = response.getJSONObject(i)
                            val roll = UserRoll()
                            roll.formCode = `object`.optString("FormCode")
                            roll.formName = `object`.optString("FormName")
                            val permission = `object`.optBoolean("HavePermission")
                            Log.w("UserPermission_Print:", permission.toString())
                            if (permission) {
                                roll.havePermission = "true"
                            } else {
                                roll.havePermission = "false"
                            }
                            // roll.setIsActive(object.optString("IsActive"));
                            userRolls.add(roll)
                        }
                        dbHelper!!.insertUserRollPermission(userRolls)
                        if (userRolls.size > 0) {
                            getCompanyDetails(companyCode)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error: VolleyError ->
                // Do something when error occurred
                Log.w("Error_throwing:", error.toString())
                Toast.makeText(
                    applicationContext,
                    "Server not responding,please try again...",
                    Toast.LENGTH_LONG
                ).show()
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

    @get:Throws(JSONException::class)
    val companiesList: Unit
        get() {
            // Initialize a new RequestQueue instance
            val requestQueue = Volley.newRequestQueue(this)
            // Initialize a new JsonArrayRequest instance
            val url = Utils.getBaseUrl(this) + "MasterApi/GetAll_Company"
            Log.w("Given_url:", url)
            val companies = ArrayList<MultipleCompanyModel>()
            val jsonArrayRequest: JsonArrayRequest =
                object : JsonArrayRequest(Method.GET, url, null,
                    Response.Listener { response: JSONArray ->
                        try {
                            Log.w("AllCompanyResponse:", response.toString())
                            if (response.length() > 0) {
                                for (i in 0 until response.length()) {
                                    val jsonObject = response.getJSONObject(i)
                                    val model = MultipleCompanyModel()
                                    model.setCompanyId(jsonObject.optString("CompanyCode"))
                                    model.setCompanyName(jsonObject.optString("CompanyName"))
                                    model.setActive(true)
                                    companies.add(model)
                                }
                                dbHelper!!.removeAllCompanies()
                                dbHelper!!.insertCompany(companies)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { error: VolleyError ->
                        // Do something when error occurred
                        Log.w("Error_throwing:", error.toString())
                        Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG)
                            .show()
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

    override fun onBackPressed() {
        /* if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
        if (lastBackPressTime < System.currentTimeMillis() - 4000) {
            val snackbar = Snackbar
                .make(userIdText!!, "Click BACK again to exit", Snackbar.LENGTH_LONG)
            snackbar.show()
            lastBackPressTime = System.currentTimeMillis()
        } else {
            super.onBackPressed()
            val a = Intent(Intent.ACTION_MAIN)
            a.addCategory(Intent.CATEGORY_HOME)
            a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(a)
            finishAffinity()
            Process.killProcess(Process.myPid())
        }
    }

    @Throws(JSONException::class)
    fun getCompanyDetails(companyCode: String?) {
        // Initialize a new RequestQueue instance
        val requestQueue = Volley.newRequestQueue(this)
        // Initialize a new JsonArrayRequest instance
        val jsonObject = JSONObject()
        jsonObject.put("CompanyCode", companyCode)
        val userRolls = ArrayList<UserRoll>()
        pDialog!!.setTitleText("Getting Company Details...")
        val url =
            Utils.getBaseUrl(this) + "MasterApi/Get_Company?Requestdata=" + jsonObject.toString()
        Log.w("Given_url:", url)
        val jsonArrayRequest: JsonArrayRequest = object : JsonArrayRequest(
            Method.GET,
            url,
            null,
            Response.Listener { response: JSONArray ->
                try {
                    Log.w("_CompanyDetails:", response.toString())
                    if (response.length() > 0) {
                        for (i in 0 until response.length()) {
                            val `object` = response.getJSONObject(i)
                            Log.w("GivenCompanyLogo:", `object`.optString("LogoString"))
                            session!!.setCompanyDetails(
                                `object`.optString("ShortCode"), `object`.optString("LogoString")
                            )
                            // No need Load the Content Straight to the Dashboard

                            // This is for Without loading Data

                            //  Intent intent=new Intent(LoginActivity.this,DashboardActivity.class);
                            //  startActivity(intent);
                            //  finish();

                            // Loading all Data like customer and Products list
                            val intent =
                                Intent(this@LoginActivity, NewCompanySwitchActivity::class.java)
                            intent.putExtra("from", "Login")
                            intent.putExtra("companyCode", newSelectedCompany)
                            intent.putExtra("companyName", newSelectedCompanyName)
                            intent.putExtra("locationCode", locationCode)
                            startActivity(intent)
                            finish()
                        }
                        pDialog!!.dismiss()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Error in Getting Company details, Try again..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error: VolleyError ->
                // Do something when error occurred
                Log.w("Error_throwing:", error.toString())
                Toast.makeText(
                    applicationContext,
                    "Server not responding,please try again...",
                    Toast.LENGTH_LONG
                ).show()
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

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
    }
}