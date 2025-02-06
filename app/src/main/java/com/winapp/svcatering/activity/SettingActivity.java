package com.winapp.svcatering.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.UomSettingInvoiceAdapter;
import com.winapp.svcatering.adapter.UomSettingTransferAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.AppUtils;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.HomePageModel;
import com.winapp.svcatering.model.ProductImageModel;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.model.UomModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.DeviceListActivity;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SettingActivity extends AppCompatActivity implements Runnable, CompoundButton.OnCheckedChangeListener {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch invoiceSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch alllowNegativeStockSwitch;
    private Switch createInvoiceSwitch;
    public DBHelper dbHelper;
    public ArrayList<SettingsModel> settingsList;
    private ArrayList<UomModel> uomList;
    public Button btnScan;
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private Button mScan, mPrint, mDisc;
    private BluetoothAdapter mBluetoothAdapter;
    private final UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    private BluetoothDevice mBluetoothDevice;
    private EditText macAddress;
    private LinearLayout macAddressLayout;
    private Button saveChangesBtn;
    private SharedPreferences sharedPreferences;
    // Creating an Editor object
    // to edit(write to the file)
    private SharedPreferences.Editor myEdit;
    private RadioGroup radioPrinterGroup;
    private RadioButton printerSelectButton;
    private String printerType;
    private String printerMacId;
    private RadioButton tscPrinterRadio;
    private RadioButton zebraPrinterRadio;
    private RadioButton inch_3_BluetoothRadio;
    private RadioButton inch_2_BlutoothRadio;
    private ProgressDialog progressBarDialog;
    private ImageView refeshItem;
    private ArrayList<CustomerModel> customerList;
    private SessionManager session;
    private HashMap<String, String> user;
    private String companyId;
    private String locationCode;
    public static ArrayList<ProductsModel> productList;
    private ProgressDialog dialog;
    private LinearLayout productSettingLayout;
    private Button btnLogin;
    private EditText userIdText;
    private EditText passwordText;
    private boolean isEmailValid;
    private boolean isPasswordValid;
    private AlertDialog alertDialog;
    private String isLProductSettingLogin = "";
    private ArrayList<CustomerDetails> allCustomersList;
    private ProgressDialog pd;
    private ImageView iv;
    public ProductImageDownload di;
    public Button downloadImage;
    public ArrayList<ProductImageModel> productsImagesList;
    private int imageCount = 0;
    private int showCount = 1;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    public SwitchCompat inv_switch;
    public SwitchCompat sales_switch, discount_Switch, signature_Switch;
    public SwitchCompat receipt_switch;
    public SwitchCompat uom_Switch;
    public SwitchCompat creditLimit_Switch;
    public SwitchCompat deliveryAddress_Switch;
    public SwitchCompat latLong_Switch;
    public SwitchCompat email_Switch;
    public UomSettingInvoiceAdapter uomSettingInvoiceAdapter;
    public UomSettingTransferAdapter uomSettingTransferAdapter;
    public RecyclerView rv_uom_transf_setting;
    public RecyclerView rv_uom_invoice_setting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
        productsImagesList = new ArrayList<>();
        pd = new ProgressDialog(SettingActivity.this);
        pd.setMessage("Downloading Product Images, please wait ...");
        pd.setIndeterminate(true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCancelable(false);
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                new ProductImageDownload(SettingActivity.this).cancel(true);
                dialog.dismiss();
            }
        });
        pd.setProgressNumberFormat("%1d KB/%2d KB");

        requestBlePermissions(this, 134);
        // checkPermission();
        verifyStoragePermissions(this);

        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                di.cancel(true);
            }
        });

        invoiceSwitch = findViewById(R.id.invoice_switch);
        createInvoiceSwitch = findViewById(R.id.create_invoice_switch);
        btnScan = findViewById(R.id.scan);
        dbHelper = new DBHelper(this);
        macAddress = findViewById(R.id.mac_address);
        macAddressLayout = findViewById(R.id.mac_address_layout);
        saveChangesBtn = findViewById(R.id.save_changes);
        settingsList = dbHelper.getSettings();
        sharedPreferences = getSharedPreferences("PrinterPref", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        radioPrinterGroup = findViewById(R.id.radioGroup);
        tscPrinterRadio = findViewById(R.id.tsc_printer);
        zebraPrinterRadio = findViewById(R.id.zebra_printer);
        inch_2_BlutoothRadio = findViewById(R.id.inch_2_5printer);
        inch_3_BluetoothRadio = findViewById(R.id.inch_3printer);
        refeshItem = findViewById(R.id.refresh_item);
        productSettingLayout = findViewById(R.id.product_setting_layout);
        btnLogin = findViewById(R.id.login);
        alllowNegativeStockSwitch = findViewById(R.id.allow_negative_stock);
        downloadImage = findViewById(R.id.download);
        signature_Switch = findViewById(R.id.signatureSwitch);
        discount_Switch = findViewById(R.id.discountSwitch);
        inv_switch = findViewById(R.id.invSwitch);
        sales_switch = findViewById(R.id.salesSwitch);
        receipt_switch = findViewById(R.id.receiptSwitch);
        uom_Switch = findViewById(R.id.UomSwitch);
        creditLimit_Switch = findViewById(R.id.creditLimitSwitch);
        deliveryAddress_Switch = findViewById(R.id.deliveryAddressSwitch);
        latLong_Switch = findViewById(R.id.latlong_LocSwitch);
        email_Switch = findViewById(R.id.sentEmailSwitch);
        rv_uom_transf_setting = findViewById(R.id.uom_transf_setting);
        rv_uom_invoice_setting = findViewById(R.id.uom_invoice_setting);
        inv_switch.setOnCheckedChangeListener(this);
        sales_switch.setOnCheckedChangeListener(this);
        receipt_switch.setOnCheckedChangeListener(this);
        uom_Switch.setOnCheckedChangeListener(this);
        creditLimit_Switch.setOnCheckedChangeListener(this);
        deliveryAddress_Switch.setOnCheckedChangeListener(this);
        latLong_Switch.setOnCheckedChangeListener(this);
        signature_Switch.setOnCheckedChangeListener(this);
        discount_Switch.setOnCheckedChangeListener(this);
        email_Switch.setOnCheckedChangeListener(this);

        session = new SessionManager(this);
        user = session.getUserDetails();
        companyId = user.get(SessionManager.KEY_COMPANY_CODE);
        locationCode = user.get(SessionManager.KEY_LOCATION_CODE);

        printerType = sharedPreferences.getString("printer_type", "");
        printerMacId = sharedPreferences.getString("mac_address", "");
        isLProductSettingLogin = sharedPreferences.getString("isProductSettingLogin", "");

        ArrayList<SettingsModel> settings = dbHelper.getSettings();
        if (settings != null) {
            if (settings.size() > 0) {
                for (SettingsModel model : settings) {
                    if (model.getSettingName().equals("create_invoice_switch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            createInvoiceSwitch.setChecked(true);
                        } else {
                            createInvoiceSwitch.setChecked(false);
                        }
                    } else if (model.getSettingName().equals("invSwitch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            inv_switch.setChecked(true);
                        } else {
                            inv_switch.setChecked(false);
                        }
                    } else if (model.getSettingName().equals("sentEmailSwitch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            email_Switch.setChecked(true);
                        } else {
                            email_Switch.setChecked(false);
                        }
                    } else if (model.getSettingName().equals("salesSwitch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            sales_switch.setChecked(true);
                        } else {
                            sales_switch.setChecked(false);
                        }
                    } else if (model.getSettingName().equals("receiptSwitch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            receipt_switch.setChecked(true);
                        } else {
                            receipt_switch.setChecked(false);
                        }
                    } else if (model.getSettingName().equals("UomSwitch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            uom_Switch.setChecked(true);
                        } else {
                            uom_Switch.setChecked(false);
                        }
                    } else if (model.getSettingName().equals("creditLimitSwitch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            creditLimit_Switch.setChecked(true);
                        } else {
                            creditLimit_Switch.setChecked(false);
                        }
                    } else if (model.getSettingName().equals("deliveryAddressSwitch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            deliveryAddress_Switch.setChecked(true);
                        } else {
                            deliveryAddress_Switch.setChecked(false);
                        }
                    }else if (model.getSettingName().equals("latlong_LocSwitch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            latLong_Switch.setChecked(true);
                        } else {
                            latLong_Switch.setChecked(false);
                        }
                    } else if (model.getSettingName().equals("signatureSwitch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            signature_Switch.setChecked(true);
                        } else {
                            signature_Switch.setChecked(false);
                        }
                    } else if (model.getSettingName().equals("discountSwitch")) {
                        Log.w("SettingName:", model.getSettingName());
                        Log.w("SettingValue:", model.getSettingValue());
                        if (model.getSettingValue().equals("1")) {
                            discount_Switch.setChecked(true);
                        } else {
                            discount_Switch.setChecked(false);
                        }
                    }

                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        try {
//            {"CustomerCode":"C1348","ItemCode":"AAA01"}
            jsonObject.put("CustomerCode", "C1348");
            jsonObject.put("ItemCode", "AAA01");
            getUOM(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isLProductSettingLogin.equals("true")) {
            productSettingLayout.setVisibility(View.VISIBLE);
            btnLogin.setText("Logout");
        } else {
            productSettingLayout.setVisibility(View.GONE);
            btnLogin.setText("Login");
        }

        if (printerType != null && !printerType.isEmpty()) {
            if (printerType.equals("TSC Printer")) {
                tscPrinterRadio.setChecked(true);
            } else if (printerType.equals("3 Inch Bluetooth Generic")) {
                inch_3_BluetoothRadio.setChecked(true);
            } else if (printerType.equals("2.5 Inch Bluetooth Generic")) {
                inch_2_BlutoothRadio.setChecked(true);
            } else {
                zebraPrinterRadio.setChecked(true);
            }
        }

        if (printerMacId != null && !printerMacId.isEmpty()) {
            macAddressLayout.setVisibility(View.VISIBLE);
            macAddress.setText(printerMacId);
        }

        if (settingsList.size() > 0) {
            for (SettingsModel model : settingsList) {
                if (model.getSettingName().equals(invoiceSwitch.getTag().toString())) {
                    if (model.getSettingValue().equals("1")) {
                        invoiceSwitch.setChecked(true);
                    } else {
                        invoiceSwitch.setChecked(false);
                    }
                } else if (model.getSettingName().equals(alllowNegativeStockSwitch.getTag().toString())) {
                    if (model.getSettingValue().equals("1")) {
                        alllowNegativeStockSwitch.setChecked(true);
                    } else {
                        alllowNegativeStockSwitch.setChecked(false);
                    }
                }
            }
        }

        invoiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", "" + isChecked);
                if (isChecked) {
                    dbHelper.insertSettings(invoiceSwitch.getTag().toString(), "1");
                } else {
                    dbHelper.insertSettings(invoiceSwitch.getTag().toString(), "0");
                }
            }
        });

        createInvoiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", "" + isChecked);
                if (isChecked) {
                    dbHelper.insertSettings(createInvoiceSwitch.getTag().toString(), "1");
                } else {
                    dbHelper.insertSettings(createInvoiceSwitch.getTag().toString(), "0");
                }
            }
        });

        alllowNegativeStockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Log.w("NeStock_Switch_Status:", "" + isChecked);
                if (isChecked) {
                    dbHelper.insertSettings(alllowNegativeStockSwitch.getTag().toString(), "1");
                } else {
                    dbHelper.insertSettings(alllowNegativeStockSwitch.getTag().toString(), "0");
                }
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(SettingActivity.this, "Bluetooth not Connected", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(SettingActivity.this, DeviceListActivity.class);
                        startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                    }
                }
            }
        });

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   if (!macAddress.getText().toString().isEmpty()){
                int selectedId = radioPrinterGroup.getCheckedRadioButtonId();
                printerSelectButton = findViewById(selectedId);
                String printer_type = printerSelectButton.getText().toString();
                String printet_mac_id = macAddress.getText().toString().trim();
                myEdit.putString("printer_type", printer_type);
                myEdit.putString("mac_address", printet_mac_id);
                myEdit.apply();
                finish();
                //   }else {
                //    Toast.makeText(getApplicationContext(),"Scan the Printer Mac Id",Toast.LENGTH_LONG).show();
                //  }
            }
        });

        refeshItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRefreshAlert();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnLogin.getText().toString().equals("Login")) {
                    showLoginAlert();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setMessage("Are you sure want to Logout product Settings ?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            myEdit.putString("isProductSettingLogin", "false");
                            myEdit.apply();
                            dialogInterface.dismiss();
                            startActivity(getIntent());
                            finish();
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productsImagesList.size() != 0) {
                    pd.show();
                    getAllImages(productsImagesList);
                } else {
                    Toast.makeText(getApplicationContext(), "There is no products to download,refresh data and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

      /*  JSONObject object =new JSONObject();
        try {
            object.put("CompanyCode",companyId);
            object.put("LocationCode",locationCode);
            object.put("PageSize",2000);
            object.put("PageNo","1");
            getAllProductsBack(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }


    public static void requestBlePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
        else
            ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, requestCode);
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }
        }
    }

    public void showLoginAlert() {
        // load the dialog_promt_user.xml layout and inflate to view
        LayoutInflater layoutinflater = LayoutInflater.from(SettingActivity.this);
        View promptUserView = layoutinflater.inflate(R.layout.login_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingActivity.this);
        alertDialogBuilder.setView(promptUserView);
        alertDialogBuilder.setCancelable(false);
        userIdText = promptUserView.findViewById(R.id.username);
        passwordText = promptUserView.findViewById(R.id.password);
        Button buttonLogin = promptUserView.findViewById(R.id.buttonLogin);
        Button buttonCancel = promptUserView.findViewById(R.id.buttonCancel);
        // prompt for username
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validateSession();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        // all set and time to build and show up!
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void validateSession() throws JSONException {
        if (userIdText.getText().toString().isEmpty()) {
            userIdText.setError("Username is Empty");
            isEmailValid = false;
        } /*else if (!Patterns.EMAIL_ADDRESS.matcher(emailIdText.getText().toString()).matches()) {
            emailIdText.setError(getResources().getString(R.string.invalid_email));
            isEmailValid = false;
        }*/ else {
            isEmailValid = true;
        }
        // Check for a valid password.
        if (passwordText.getText().toString().isEmpty()) {
            passwordText.setError(getResources().getString(R.string.error_password));
            isPasswordValid = false;
        } else if (passwordText.getText().length() < 3) {
            passwordText.setError(getResources().getString(R.string.invalid_password));
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
        }
        if (isEmailValid && isPasswordValid) {
            userIdText.setError(null);
            passwordText.setError(null);
            setSession(userIdText.getText().toString(), passwordText.getText().toString());
        }
    }

    public void setSession(String username, String password) {
        if (username.trim().equals("admin124") && password.trim().equals("settings")) {
            productSettingLayout.setVisibility(View.VISIBLE);
            btnLogin.setText("Logout");
            alertDialog.dismiss();
            myEdit.putString("isProductSettingLogin", "true");
            myEdit.apply();
        } else {
            Toast.makeText(getApplicationContext(), "Username or Password mismatch", Toast.LENGTH_SHORT).show();
            myEdit.putString("isProductSettingLogin", "false");
            myEdit.apply();
            productSettingLayout.setVisibility(View.GONE);
            btnLogin.setText("Login");
        }
    }

    public void showRefreshAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingActivity.this);
        alertDialog.setTitle("Information..!");
        alertDialog.setMessage("Are you sure want to Refresh the Data ?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("YES", (dialog, id) -> {
            dialog.cancel();
            try {
                getCustomers();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = alertDialog.create();
        alert11.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    assert mExtra != null;
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.w("bluetho",""+mDeviceAddress);
                    macAddress.setText(mDeviceAddress);

                    Log.w(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
                 /*   mBluetoothConnectProgressDialog = ProgressDialog.show(this, "Connecting...", mBluetoothDevice.getName() + " : " + mBluetoothDevice.getAddress(), true, false);
                    mBluetoothConnectProgressDialog.setButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mBluetoothConnectProgressDialog.dismiss();
                        }
                    });*/
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    showAlert(mBluetoothDevice.getName(), mBluetoothDevice.getAddress());

                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    //pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(SettingActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(SettingActivity.this, "Enable bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public void showAlert(String deviceName, String deviceaddress) {
        //the first thing you need to to is to initialize the progressDialog Class like this
        progressBarDialog = new ProgressDialog(this);
        progressBarDialog.setIcon(R.drawable.ic_print);
        progressBarDialog.setCancelable(false);
        // progressBarDialog.setTitle("Connecting...");
        progressBarDialog.setMessage("Connecting....  " + deviceName + "-" + deviceaddress);
        progressBarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //set the Cancel button
        progressBarDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        progressBarDialog.show();
    }


    private void ListPairedDevices() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.w(TAG, "PairedDevices: " + mDevice.getName() + "  " + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
           // mBluetoothConnectProgressDialog.dismiss();
            progressBarDialog.dismiss();
            macAddressLayout.setVisibility(View.VISIBLE);
            macAddress.setText(mBluetoothDevice.getAddress());
            Toast.makeText(SettingActivity.this, "Printer Connected", Toast.LENGTH_SHORT).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
         /*   case R.id.action_remove:
                showRemoveAlert();
                break;*/
        }
        return true;
    }

    public void getCustomers() throws JSONException {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyId);
        String url= Utils.getBaseUrl(this) +"MasterApi/GetCustomer_All?Requestdata="+jsonObject.toString();
        customerList=new ArrayList<>();
        allCustomersList=new ArrayList<>();
        dialog=new ProgressDialog(SettingActivity.this);
        dialog.setMessage("Updating Customer List...");
        dialog.setCancelable(false);
        dialog.show();
        Log.w("Given_url:",url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.w("Response_is_Customer:", response.toString());
                        // Loop through the array elements
                        for (int i = 0; i < response.length(); i++) {
                            // Get current json object
                            JSONObject customerObject = response.getJSONObject(i);
                            CustomerModel model=new CustomerModel();
                            if (customerObject.optBoolean("IsActive")) {
                                model.setCustomerCode(customerObject.optString("CustomerCode"));
                                model.setCustomerName(customerObject.optString("CustomerName"));
                                model.setCustomerAddress(customerObject.optString("Address1"));
                                model.setHaveTax(customerObject.optString("HaveTax"));
                                model.setTaxType(customerObject.optString("TaxType"));
                                model.setTaxPerc(customerObject.optString("TaxPerc"));
                                model.setTaxCode(customerObject.optString("TaxCode"));
                                if (customerObject.optString("BalanceAmount").equals("null") || customerObject.optString("BalanceAmount").isEmpty()) {
                                    model.setOutstandingAmount("0.00");
                                } else {
                                    model.setOutstandingAmount(customerObject.optString("BalanceAmount"));
                                }
                                customerList.add(model);

                              /*  CustomerDetails details=new CustomerDetails();
                                details.setCustomerCode(productObject.optString("CustomerCode"));
                                details.setCustomerName(productObject.optString("CustomerName"));
                                details.setPhoneNo(productObject.optString("PhoneNo"));
                                details.setCustomerAddress1(productObject.optString("Address1"));
                                details.setCustomerAddress2(productObject.optString("Address2"));
                                details.setCustomerAddress3(productObject.optString("Address3"));
                                details.setIsActive(productObject.optString("IsActive"));
                                details.setHaveTax(productObject.optString("HaveTax"));
                                details.setTaxType(productObject.optString("TaxType"));
                                details.setTaxPerc(productObject.optString("TaxPerc"));
                                details.setTaxCode(productObject.optString("TaxCode"));
                                details.setCreditLimit(productObject.optString("CreditLimit"));
                                details.setCountry(productObject.optString("Country"));
                                details.setCurrencyCode(productObject.optString("CurrencyCode"));
                                details.setBalanceAmount(productObject.optString("BalanceAmount"));

                                allCustomersList.add(details);*/
                            }
                        }
                        if (customerList.size()>0){
                            dbHelper.removeAllCustomers();
                            dbHelper.insertCustomerList(customerList);
                           // dbHelper.removeAllCustomersDetails();
                            //dbHelper.insertCustomersDetails(allCustomersList);
                            dialog.dismiss();
                            JSONObject object =new JSONObject();
                            try {
                                object.put("CompanyCode",companyId);
                                object.put("LocationCode",locationCode);
                                object.put("PageSize",5000);
                                object.put("PageNo","1");
                              //  getAllProducts(object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
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
        jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(jsonArrayRequest);
    }

    public void getAllProductsBack(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(SettingActivity.this);
        // String url= Utils.getBaseUrl(getContext()) +"ProductApi/GetProduct_All?Requestdata={CompanyCode:"+companyCode+",PageSize:1300,PageNo:"+pageNo+"}";
        String url=Utils.getBaseUrl(SettingActivity.this) +"ProductApi/GetProduct_All_ByFacets?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_product_url:",url);
        productList=new ArrayList<>();
        productsImagesList=new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try{
                        Log.w("Response_is:",response.toString());
                        // Loop through the array elements
                        for(int i=0;i<response.length();i++){
                            // Get current json object
                            JSONObject productObject = response.getJSONObject(i);
                            ProductsModel product =new ProductsModel();
                            if (productObject.optBoolean("IsActive")) {
                                product.setCompanyCode(productObject.optString("CompanyCode"));
                                product.setProductName(productObject.optString("ProductName"));
                                product.setProductCode(productObject.optString("ProductCode"));
                                product.setWeight(productObject.optString("Weight"));
                                product.setProductImage(productObject.optString("ProductImagePath"));
                                product.setRetailPrice(productObject.optDouble("RetailPrice"));
                                product.setWholeSalePrice(productObject.optString("WholeSalePrice"));
                                product.setCartonPrice(productObject.optString("CartonPrice"));
                                product.setPcsPerCarton(productObject.optString("PcsPerCarton"));
                                product.setUnitCost(productObject.optString("UnitCost"));
                                product.setStockQty(productObject.optString("StockQty"));
                                product.setUomCode(productObject.optString("UOMCode"));
                                //  product.setProductBarcode(productObject.optString("BarCode")); Add values In Futue
                                product.setProductBarcode("");

                                // Adding Array for the Product Images for local db
                                if (productObject.optString("ProductImagePath")!=null && !productObject.optString("ProductImagePath").equals("null") && !productObject.optString("ProductImagePath").isEmpty()){
                                    ProductImageModel productImageModel=new ProductImageModel();
                                    productImageModel.setProductId(productObject.optString("ProductCode"));
                                    productImageModel.setProductName(productObject.optString("ProductName"));
                                    productImageModel.setImagePath(productObject.optString("ProductImagePath"));
                                    productsImagesList.add(productImageModel);
                                }
                                productList.add(product);
                            }
                        }
                        HomePageModel.productsList=new ArrayList<>();
                        HomePageModel.productsList.addAll(productList);
                        dbHelper.removeAllProducts();
                        if (productList.size()>0){
                            AppUtils.setProductsList(productList);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Do something when error occurred
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
        jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(jsonArrayRequest);
    }

    public void getAllProducts(JSONObject jsonObject){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(SettingActivity.this);
        // String url= Utils.getBaseUrl(getContext()) +"ProductApi/GetProduct_All?Requestdata={CompanyCode:"+companyCode+",PageSize:1300,PageNo:"+pageNo+"}";
        String url=Utils.getBaseUrl(SettingActivity.this) +"ProductApi/GetProduct_All_ByFacets?Requestdata="+jsonObject.toString();
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_product_url:",url);
        productList=new ArrayList<>();
        productsImagesList=new ArrayList<>();
        dialog=new ProgressDialog(SettingActivity.this);
        dialog.setMessage("Updating Products List...");
        dialog.setCancelable(false);
        dialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try{
                        Log.w("Response_is:",response.toString());
                        // Loop through the array elements
                        for(int i=0;i<response.length();i++){
                            // Get current json object
                            JSONObject productObject = response.getJSONObject(i);
                            ProductsModel product =new ProductsModel();
                            if (productObject.optBoolean("IsActive")) {
                                product.setCompanyCode(productObject.optString("CompanyCode"));
                                product.setProductName(productObject.optString("ProductName"));
                                product.setProductCode(productObject.optString("ProductCode"));
                                product.setWeight(productObject.optString("Weight"));
                                product.setProductImage(productObject.optString("ProductImagePath"));
                                product.setRetailPrice(productObject.optDouble("RetailPrice"));
                                product.setWholeSalePrice(productObject.optString("WholeSalePrice"));
                                product.setCartonPrice(productObject.optString("CartonPrice"));
                                product.setPcsPerCarton(productObject.optString("PcsPerCarton"));
                                product.setUnitCost(productObject.optString("UnitCost"));
                                product.setStockQty(productObject.optString("StockQty"));
                                product.setUomCode(productObject.optString("UOMCode"));
                                //  product.setProductBarcode(productObject.optString("BarCode")); Add values In Futue
                                product.setProductBarcode("");

                                // Adding Array for the Product Images for local db
                                if (productObject.optString("ProductImagePath")!=null && !productObject.optString("ProductImagePath").equals("null") && !productObject.optString("ProductImagePath").isEmpty()) {
                                    ProductImageModel productImageModel = new ProductImageModel();
                                    productImageModel.setProductId(productObject.optString("ProductCode"));
                                    productImageModel.setProductName(productObject.optString("ProductName"));
                                    productImageModel.setImagePath(productObject.optString("ProductImagePath"));
                                    productsImagesList.add(productImageModel);
                                }

                                productList.add(product);
                            }
                        }
                        HomePageModel.productsList=new ArrayList<>();
                        HomePageModel.productsList.addAll(productList);
                        dbHelper.removeAllProducts();
                        if (productList.size()>0){
                            AppUtils.setProductsList(productList);
                        }
                        dialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Do something when error occurred
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
        jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(jsonArrayRequest);
    }



    public void getAllImages(ArrayList<ProductImageModel> productsImagesList){
        Log.w("GivenImagesSizes:",productsImagesList.size()+"");
        di = new ProductImageDownload(SettingActivity.this);
        di.execute(productsImagesList.get(imageCount).getImagePath(),productsImagesList.get(imageCount).getProductName(),productsImagesList.get(imageCount).getProductId());
        Log.w("ProductImagePathView:",productsImagesList.get(imageCount).getImagePath());
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.invSwitch:
                if (isChecked){
                    dbHelper.insertSettings(inv_switch.getTag().toString(),"1");
                }else {
                    dbHelper.insertSettings(inv_switch.getTag().toString(),"0");
                }
                break;
            case R.id.salesSwitch:
                if (isChecked){
                    dbHelper.insertSettings(sales_switch.getTag().toString(),"1");
                }else {
                    dbHelper.insertSettings(sales_switch.getTag().toString(),"0");
                }
                break;
            case R.id.receiptSwitch:
                if (isChecked){
                    dbHelper.insertSettings(receipt_switch.getTag().toString(),"1");
                }else {
                    dbHelper.insertSettings(receipt_switch.getTag().toString(),"0");
                }
                break;
            case R.id.sentEmailSwitch:
                if (isChecked){
                    dbHelper.insertSettings(email_Switch.getTag().toString(),"1");
                }else {
                    dbHelper.insertSettings(email_Switch.getTag().toString(),"0");
                }
                break;
            case R.id.UomSwitch:
                if (isChecked){
                    dbHelper.insertSettings(uom_Switch.getTag().toString(),"1");
                }else {
                    dbHelper.insertSettings(uom_Switch.getTag().toString(),"0");
                }
                break;
            case R.id.creditLimitSwitch:
                if (isChecked){
                    dbHelper.insertSettings(creditLimit_Switch.getTag().toString(),"1");
                }else {
                    dbHelper.insertSettings(creditLimit_Switch.getTag().toString(),"0");
                }
                break;
            case R.id.deliveryAddressSwitch:
                if (isChecked){
                    dbHelper.insertSettings(deliveryAddress_Switch.getTag().toString(),"1");
                }else {
                    dbHelper.insertSettings(deliveryAddress_Switch.getTag().toString(),"0");
                }
                break;
            case R.id.signatureSwitch:
                if (isChecked){
                    dbHelper.insertSettings(signature_Switch.getTag().toString(),"1");
                }else {
                    dbHelper.insertSettings(signature_Switch.getTag().toString(),"0");
                }
                break;
            case R.id.latlong_LocSwitch:
                if (isChecked){
                    dbHelper.insertSettings(latLong_Switch.getTag().toString(),"1");
                }else {
                    dbHelper.insertSettings(latLong_Switch.getTag().toString(),"0");
                }
                break;
            case R.id.discountSwitch:
                if (isChecked){
                    dbHelper.insertSettings(discount_Switch.getTag().toString(),"1");
                }else {
                    dbHelper.insertSettings(discount_Switch.getTag().toString(),"0");
                }
                break;
         }
    }
    public void getUOM(JSONObject jsonObject) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Utils.getBaseUrl(this) + "ItemUOMDetails";
        // Initialize a new JsonArrayRequest instance
        Log.w("Given_UOM_URLSe:", url + jsonObject.toString());

        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading UOM...");
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try {
                uomList = new ArrayList<>();

                Log.w("Res_UOMSett:", response.toString());
                // Loop through the array elements
                JSONArray uomArray = response.optJSONArray("responseData");
                if (uomArray != null && uomArray.length() > 0) {
                    for (int j = 0; j < uomArray.length(); j++) {
                        JSONObject uomObject = uomArray.getJSONObject(j);
                        UomModel uomModel = new UomModel();
                        uomModel.setUomCode(uomObject.optString("uomCode"));
                        uomModel.setUomName(uomObject.optString("uomName"));
                        uomModel.setUomEntry(uomObject.optString("uomEntry"));
                        uomModel.setAltQty(uomObject.optString("altQty"));
                        uomModel.setBaseQty(uomObject.optString("baseQty"));
                        uomModel.setPrice(uomObject.optString("price"));
                        uomList.add(uomModel);
                    }
                }

                Log.w("UOM_TEXTSett:", uomArray.toString());
                pDialog.dismiss();
                if (uomList.size() > 0) {
                    runOnUiThread(() -> {
                        setUOMInvoiceAdapter(uomList);
                        setUOMATransferdapter(uomList);
                    });
                }
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

    public void setUOMInvoiceAdapter(ArrayList<UomModel> uomList ) {
        rv_uom_invoice_setting.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        rv_uom_invoice_setting.setLayoutManager(mLayoutManager);
        uomSettingInvoiceAdapter = new UomSettingInvoiceAdapter(SettingActivity.this, uomList);
        rv_uom_invoice_setting.setAdapter(uomSettingInvoiceAdapter);
    }
    public void setUOMATransferdapter(ArrayList<UomModel> uomList ) {
        rv_uom_transf_setting.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        rv_uom_transf_setting.setLayoutManager(mLayoutManager);
        uomSettingTransferAdapter = new UomSettingTransferAdapter(SettingActivity.this, uomList);
        rv_uom_transf_setting.setAdapter(uomSettingTransferAdapter);
    }

    private class ProductImageDownload extends AsyncTask<String, Integer, String> {

        private Context c;
        private int file_progress_count = 0;
        File newFile;
        public ProductImageDownload(Context c) {
            this.c = c;
        }
        @Override
        protected String doInBackground(String... sUrl) {
            InputStream is = null;
            OutputStream os = null;
            HttpURLConnection con = null;
            int length;
            try {
                URL url = new URL(sUrl[0]);
                con = (HttpURLConnection) url.openConnection();
                con.connect();

                if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "HTTP CODE: " + con.getResponseCode() + " " + con.getResponseMessage();
                }

                length = con.getContentLength();
                pd.setMax(length / (1000));
                is = con.getInputStream();

                Log.w("DownloadImageURL:",url.toString());

                //String folderPath = Environment.getExternalStorageDirectory() + "/CatalogErp/Products";
                File folder = new File(Constants.folderPath);
                if (!folder.exists()) {
                    File productsDirectory = new File(Constants.folderPath);
                    productsDirectory.mkdirs();
                }

                //create a new file
                String filepath= sUrl[1]+"_"+sUrl[2];
                String newfilePath=filepath.replace("/","_");
                newFile = new File(Constants.folderPath, newfilePath+".jpg");
                if (newFile.exists()){
                    newFile.delete();
                }
                Log.w("GivenFilePath:",newFile.toString());
                //os = new FileOutputStream(Environment.getExternalStorageDirectory()+File.separator+"CatalogImages" + File.separator + "a-computer-engineer.jpg");
                os = new FileOutputStream(newFile);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = is.read(data)) != -1) {
                    if (isCancelled()) {
                        is.close();
                        return null;
                    }
                    total += count;
                    if (length > 0) {
                        publishProgress((int) total);
                    }
                    this.file_progress_count = (int) ((100 * total) / ((long) length));
                    os.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (os != null)
                        os.close();
                    if (is != null)
                        is.close();
                } catch (IOException ioe) {
                }
                if (con != null)
                    con.disconnect();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Downloading Product Image.."+"("+showCount+" / "+productsImagesList.size()+")");
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            pd.setIndeterminate(false);
            pd.setProgress(progress[0] / 1000);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
            if (file_progress_count==100){
              //  pd.dismiss();
                dbHelper.insertProductsImageOneByOne(productsImagesList.get(imageCount).getProductId(),productsImagesList.get(imageCount).getProductName());
                imageCount++;
                showCount++;
                if (imageCount!=productsImagesList.size()){
                    new ProductImageDownload(SettingActivity.this).execute(productsImagesList.get(imageCount).getImagePath(),productsImagesList.get(imageCount).getProductName(),productsImagesList.get(imageCount).getProductId());
                }else {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"All Products Downloaded",Toast.LENGTH_SHORT).show();
                }
            }
            if (result != null) {
                //pd.dismiss();
                imageCount++;
                new ProductImageDownload(SettingActivity.this).execute(productsImagesList.get(imageCount).getImagePath(),productsImagesList.get(imageCount).getProductName(),productsImagesList.get(imageCount).getProductId());
               // Toast.makeText(c, "Download error: " + result, Toast.LENGTH_LONG).show();
                Log.w("ResultPrinted:",result);
            }
        }catch (Exception exception){ }
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}