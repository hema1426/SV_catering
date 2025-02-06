package com.winapp.svcatering.tscprinter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.datecs.api.emsr.EMSR;
import com.datecs.api.printer.Printer;
import com.datecs.api.printer.ProtocolAdapter;
import com.datecs.api.rfid.ContactlessCard;
import com.datecs.api.rfid.RC663;
import com.datecs.api.universalreader.UniversalReader;
import com.winapp.svcatering.R;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;
import com.winapp.svcatering.model.SalesOrderPrintPreviewModel;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class TSCPrinterActivity {

    public interface OnCompletedListener {
        public void onCompleted();
    }
    private Context context;
    private String macAddress;
    public static String configLabel ="";

    // public static final int DESCRIPTION_WIDTH1 = 53;

  /*  public static final int PAPER_WIDTH = 80;
    public static final int SLNO_WIDTH = 4;
    public static final int DESCRIPTION_WIDTH = 42;
    public static final int QTY_WIDTH = 8;
    public static final int PRICE_WIDTH = 8;
    public static final int TAX_WIDTH = 8;
    public static final int TOTAL_WIDTH = 8;
    public static final int LEFT_SPACE = 28;
    public static final int RIGHT_SPACE = 25;
    private static final String LINE_SEPARATOR = "\r\n";*/

    public static int PAPER_WIDTH = 0;
    public static  int SLNO_WIDTH = 0;
    public static  int DESCRIPTION_WIDTH = 0;
    public static  int EXPENSE_DESCRIPTION_WIDTH = 0;
    public static  int QTY_WIDTH = 0;
    public static  int PRICE_WIDTH = 0;
    public static  int TAX_WIDTH = 0;
    public static  int TAX_TYPE_WIDTH = 0;
    public static  int TOTAL_WIDTH = 0;
    public static  int TOTAL_SPACE = 0;
    public static  int LEFT_SPACE = 0;
    public static  int RIGHT_SPACE = 0;
    public static  int BETWEEN_SPACE =0;
    public static  int INVOICENO = 0;
    public static  int INVOICEDATE =0;
    public static  int CREDIT_AMOUNT = 0;
    public static  int PAID_AMOUNT = 0;
    public static  int NETTOTAL = 0;
    public static  int NUM_WIDTH = 0;

    public static  int GST_SUMMARY_LABEL = 0;
    public static  int GST_AMOUNT_LABEL = 0;
    public static  int GST_LABEL = 0;

    public static  int GST_SUMMARY_VALUE = 0;
    public static  int GST_AMOUNT_VALUE = 0;
    public static  int GST_VALUE = 0;

    public static  int PRODUCT_NAME_WIDTH = 0;
    public static  int CARTON_WIDTH = 0;
    public static  int LOOSE_WIDTH = 0;
    public static  int QUANTITY_WIDTH = 0;
    public static  int INVOICE_NO_WIDTH = 0;
    public static  int CUSTOMER_NAME_WIDTH =0;
    public static  int  TOT_WIDTH = 0;
    public static  int  DESCRIPTION_WIDTH_DO = 0;

    public static int  FOCQTY_WIDTH = 0;
    public static int EXCHANGE_WIDTH = 0;

    private static String LINE_SEPARATOR = "";
    private static int TITLE_PRODUCT_WIDTH=0;

    private static final String COLON  = ":";
    private static final String PRODUCT_CODE  = "ProductCode";
    private static final String PRODUCT_NAME  = "ProductName";
    private static final String QUANTITY  = "Quantity";
    private static final String PRICE  = "Price";
    private static final String TOTAL  = "Total";
    private static final String DO_NO  = "Do No";
    private static final String DO_DATE  = "Do Date";
    private static final String INVOICE_NO  = "Invoice No";
    private static final String INVOICE_DATE  = "Invoice Date";
    private static final String SALESRETURN_NO  = "Sales Return No";
    private static final String SALESRETURN_DATE  = "Sales Return Date";
    private static final String TRANSFER_NO  = "Transfer No";
    private static final String TRANSFER_DATE  = "Transfer Date";
    private static final String CUSTOMER_CODE  = "Customer Code";
    private static final String CUSTOMER_NAME  = "Customer Name";
    private static final String ADDRESS  = "Address";
    private static final String ADDRESS2  = "Address2";
    private static final String ADDRESS3  = "Address3";

    private static final String HEAD_PHONE  = "Head Phone";
    private static final String EMAIL  = "Email";

    private static final String TERM  = "Term";
    private static final String PHONE_NO  = "Phone No";
    private static final String ITEM_DISCOUNT  = "Item Discount";
    private static final String BILL_DISCOUNT  = "Bill Discount";
    private static final String SUB_TOTAL  = "SubTotal";
    private static final String TAX  = "Tax";
    private static final String NET_TOTAL  = "NetTotal";
    private static final String TOTAL_OUTSTANDING  = "Total Outstanding";

    private static final String TAG = "Constants.TAG" + "Bluetooth";
    public static final String DEVICE_NAME = "device_name";
    private static final boolean D = true;
    public static final String TOAST = "toast";
    private static final int REQUEST_ENABLE_BT = 3;
    private OnCompletedListener listener;
    private InitCompletionListener initListener;
    private boolean isConnected = false;
    private ProtocolAdapter.Channel mPrinterChannel;
    private BluetoothAdapter btAdapter;
    private Printer mPrinter;
    private ProtocolAdapter mProtocolAdapter;
    private BluetoothSocket mBtSocket;
    private EMSR mEMSR;
    private RC663 mRC663;
    private ProtocolAdapter.Channel mUniversalChannel;
    // Request to get the bluetooth device
    private static final int DEFAULT_NETWORK_PORT = 9100;
    private PrinterServer mPrinterServer;
    private Socket mNetSocket;

    String foc="",exchangeQty="";

    public static  int HEADER_SPACE = 0;
    public static  int  SALES_RETURN_WIDTH = 0;
    public static  int  SALES_DATE_WIDTH = 0;
    public static  int  AMOUNT_WIDTH = 0;
    public static  int  CONCATENATE_LENGTH  = 0;
    public static String company_name;
    public static String company_address1;
    public static String company_address2;
    public static String company_address3;

    SessionManager session;
    HashMap<String ,String > userValues;
    String user;

    public interface InitCompletionListener {
        public void initCompleted();
    }
    public void setInitCompletionListener(InitCompletionListener initListener) {
        this.initListener = initListener;
    }

    // @Offline
    boolean checkOffline;
    String onlineMode,valid_url,dialogStatus,serverdateTime="",printertype="" ;

    public TSCPrinterActivity(){

    }
    public TSCPrinterActivity(Context context, String macAddress,String printertype){
        if (!macAddress
                .matches("[0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(\\1[0-9A-Fa-f]{2}){4}$")) {
            throw new IllegalArgumentException(macAddress
                    + context.getString(R.string.is_not_valid_mac_address));
        }
        session=new SessionManager(context);
        userValues=session.getUserDetails();
        user=userValues.get(SessionManager.KEY_USER_NAME);

        company_name=userValues.get(SessionManager.KEY_COMPANY_NAME);
        company_address1=userValues.get(SessionManager.KEY_ADDRESS1);
        company_address2=userValues.get(SessionManager.KEY_ADDRESS2);
        company_address3=userValues.get(SessionManager.KEY_ADDRESS3);
        serverdateTime=getCurrentTime();

        this.context = context;
        this.macAddress = macAddress;
        this.printertype = printertype;
        initPrinterWidth();

    }

    public void initPrinterWidth(){

        String malaysiaShowGST  = "";

        if(malaysiaShowGST!=null && !malaysiaShowGST.isEmpty()){

        }else{
            malaysiaShowGST="";
        }

        if(printertype.matches("4 Inch Bluetooth")) {
            PAPER_WIDTH = 80;
            SLNO_WIDTH = 4;
            if (malaysiaShowGST.matches("1")) {
                DESCRIPTION_WIDTH = 42;
            }else{
                DESCRIPTION_WIDTH = 52;
            }
            EXPENSE_DESCRIPTION_WIDTH = 59;
            QTY_WIDTH = 8;
            PRICE_WIDTH = 8;
            TAX_WIDTH = 8;
            TOTAL_WIDTH = 8;
            LEFT_SPACE = 28;
            RIGHT_SPACE = 25;
            TAX_TYPE_WIDTH = 2;
            LINE_SEPARATOR = "\r\n";
            TOTAL_SPACE = 54;
            BETWEEN_SPACE = 36;
            NUM_WIDTH = 5;
            INVOICENO = 15;
            INVOICEDATE = 15;
            CREDIT_AMOUNT = 15;
            PAID_AMOUNT = 15;
            NETTOTAL = 15;
            DESCRIPTION_WIDTH_DO = 68;
            GST_SUMMARY_LABEL = 31;
            GST_AMOUNT_LABEL = 15;
            GST_LABEL = 13;

            GST_SUMMARY_VALUE = 24;
            GST_AMOUNT_VALUE = 22;
            GST_VALUE = 13;


            PRODUCT_NAME_WIDTH = 45;
            CARTON_WIDTH = 10;
            LOOSE_WIDTH = 10;
            QUANTITY_WIDTH = 10;
            EXCHANGE_WIDTH =10;
            FOCQTY_WIDTH=10;

            INVOICE_NO_WIDTH = 25;
            CUSTOMER_NAME_WIDTH = 25;
            TOT_WIDTH = 25;

            SALES_RETURN_WIDTH = 26;
            SALES_DATE_WIDTH = 27;
            AMOUNT_WIDTH = 27;
            CONCATENATE_LENGTH = 27;
            HEADER_SPACE  = 25;

        }else if(printertype.matches("2.5 Inch Bluetooth Generic")) {

            Log.w("Init_2.5Inch","Printer");
            PAPER_WIDTH = 30;
            SLNO_WIDTH = 4;
            if (malaysiaShowGST.matches("1")) {
                DESCRIPTION_WIDTH = 8;
            }else{
                DESCRIPTION_WIDTH = 8;
            }
            EXPENSE_DESCRIPTION_WIDTH = 20;

            DESCRIPTION_WIDTH_DO = 15;
            QTY_WIDTH = 3;
            PRICE_WIDTH = 4;
            TAX_WIDTH = 4;
            TOTAL_WIDTH = 2;
            LEFT_SPACE = 1;
            RIGHT_SPACE = 10;
            TOTAL_SPACE = 4;
            TAX_TYPE_WIDTH = 2;
            BETWEEN_SPACE = 0;
//            LINE_SEPARATOR = "{br}";
            LINE_SEPARATOR = "\r\n";

            TITLE_PRODUCT_WIDTH=6;


            NUM_WIDTH = 3;
            INVOICENO = 4;
            INVOICEDATE = 4;
            CREDIT_AMOUNT = 4;
            PAID_AMOUNT = 4;
            NETTOTAL = 4;

            GST_SUMMARY_LABEL = 12;
            GST_AMOUNT_LABEL = 13;
            GST_LABEL = 13;

            GST_SUMMARY_VALUE = 10;
            GST_AMOUNT_VALUE = 15;
            GST_VALUE = 13;

            PRODUCT_NAME_WIDTH = 13;
            CARTON_WIDTH = 5;
            LOOSE_WIDTH = 5;
            QTY_WIDTH = 5;
            FOCQTY_WIDTH =5;
            EXCHANGE_WIDTH =5;

            INVOICE_NO_WIDTH = 12;
            CUSTOMER_NAME_WIDTH =10;
            TOT_WIDTH =4;

            SALES_RETURN_WIDTH = 8;
            SALES_DATE_WIDTH = 8;
            AMOUNT_WIDTH = 13;
            HEADER_SPACE  = 12;
            CONCATENATE_LENGTH = 15;
        }
        else if(printertype.matches("3 Inch Bluetooth Generic")) {
            PAPER_WIDTH = 46;
            SLNO_WIDTH = 4;
            if (malaysiaShowGST.matches("1")) {
                DESCRIPTION_WIDTH = 9;
            }else{
                DESCRIPTION_WIDTH = 20;
            }
            EXPENSE_DESCRIPTION_WIDTH = 35;

            DESCRIPTION_WIDTH_DO = 34;
            QTY_WIDTH = 8;
            PRICE_WIDTH = 8;
            TAX_WIDTH = 8;
            TOTAL_WIDTH = 7;
            LEFT_SPACE = 1;
            RIGHT_SPACE = 25;
            TOTAL_SPACE = 12;
            TAX_TYPE_WIDTH = 2;
            BETWEEN_SPACE = 3;
//            LINE_SEPARATOR = "{br}";
            LINE_SEPARATOR = "\r\n";

            NUM_WIDTH = 3;
            INVOICENO = 11;
            INVOICEDATE = 11;
            CREDIT_AMOUNT = 7;
            PAID_AMOUNT = 7;
            NETTOTAL = 7;

            GST_SUMMARY_LABEL = 14;
            GST_AMOUNT_LABEL = 15;
            GST_LABEL = 13;

            GST_SUMMARY_VALUE = 10;
            GST_AMOUNT_VALUE = 19;
            GST_VALUE = 13;

            PRODUCT_NAME_WIDTH = 22;
            CARTON_WIDTH = 7;
            LOOSE_WIDTH = 7;
            QTY_WIDTH = 7;
            FOCQTY_WIDTH =7;
            EXCHANGE_WIDTH =7;


            INVOICE_NO_WIDTH = 15;
            CUSTOMER_NAME_WIDTH =20;
            TOT_WIDTH =7;

            SALES_RETURN_WIDTH = 15;
            SALES_DATE_WIDTH = 15;
            AMOUNT_WIDTH = 16;
            HEADER_SPACE  = 15;
            CONCATENATE_LENGTH = 17;
        }
    }

    public void initGenericPrinter() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (isBluetoothEnabled()) {
            print();
            //  initConnection();
        }else {
            context.registerReceiver(bluetoothReceiver, new IntentFilter(
                    BluetoothAdapter.ACTION_STATE_CHANGED));
            enableBluetooth();
        }
    }
    public void print(){
        //  btAdapter = BluetoothAdapter.getDefaultAdapter();
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Please wait...");
        dialog.setMessage("Connecting to the Printer");
       // dialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_spinner));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                    BluetoothDevice btDevice = btAdapter.getRemoteDevice(macAddress);
                    BluetoothSocket btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
                    btSocket.connect();
                    mBtSocket = btSocket;
               /* InputStream in = mBtSocket.getInputStream();
                OutputStream out = mBtSocket.getOutputStream();*/
                    Printer.setDebug(true);
                    EMSR.setDebug(true);
                    mProtocolAdapter = new ProtocolAdapter(btSocket.getInputStream(), btSocket.getOutputStream());
                    if (mProtocolAdapter.isProtocolEnabled()) {
                        //  Log.d(LOG_TAG, "Protocol mode is enabled");
                        // Get printer instance
                        mPrinterChannel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_PRINTER);
                        mPrinter = new Printer(mPrinterChannel.getInputStream(), mPrinterChannel.getOutputStream());

                        // Check if printer has encrypted magnetic head
                        ProtocolAdapter.Channel emsrChannel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_EMSR);
                        try {
                            // Close channel silently if it is already opened.
                            try {
                                emsrChannel.close();
                            } catch (IOException e) {
                            }

                            // Try to open EMSR channel. If method failed, then probably EMSR is not supported
                            // on this device.
                            emsrChannel.open();

                            mEMSR = new EMSR(emsrChannel.getInputStream(), emsrChannel.getOutputStream());
                            EMSR.EMSRKeyInformation keyInfo = mEMSR.getKeyInformation(EMSR.KEY_AES_DATA_ENCRYPTION);
                            if (!keyInfo.tampered && keyInfo.version == 0) {
                                //  Log.d(LOG_TAG, "Missing encryption key");
                                // If key version is zero we can load a new key in plain mode.
                                byte[] keyData = CryptographyHelper.createKeyExchangeBlock(0xFF,
                                        EMSR.KEY_AES_DATA_ENCRYPTION, 1, CryptographyHelper.AES_DATA_KEY_BYTES,
                                        null);
                                mEMSR.loadKey(keyData);
                            }
                            mEMSR.setEncryptionType(EMSR.ENCRYPTION_TYPE_AES256);
                            mEMSR.enable();
                            // Log.d(LOG_TAG, "Encrypted magnetic stripe reader is available");
                        } catch (IOException e) {
                            if (mEMSR != null) {
                                mEMSR.close();
                                mEMSR = null;
                            }
                        }

                        // Check if printer has encrypted magnetic head
                        ProtocolAdapter.Channel rfidChannel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_RFID);

                        try {
                            // Close channel silently if it is already opened.
                            try {
                                rfidChannel.close();
                            } catch (IOException e) {
                            }

                            // Try to open RFID channel. If method failed, then probably RFID is not supported
                            // on this device.
                            rfidChannel.open();

                            mRC663 = new RC663(rfidChannel.getInputStream(), rfidChannel.getOutputStream());
                            mRC663.setCardListener(new RC663.CardListener() {
                                @Override
                                public void onCardDetect(ContactlessCard card) {
                                    // processContactlessCard(card);
                                }
                            });
                            mRC663.enable();
                            //  Log.d(LOG_TAG, "RC663 reader is available");
                        } catch (IOException e) {
                            if (mRC663 != null) {
                                mRC663.close();
                                mRC663 = null;
                            }
                        }

                        // Check if printer has encrypted magnetic head
                        mUniversalChannel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_UNIVERSAL_READER);
                        new UniversalReader(mUniversalChannel.getInputStream(), mUniversalChannel.getOutputStream());

                    } else {
                        //   Log.d(LOG_TAG, "Protocol mode is disabled");

                        // Protocol mode it not enables, so we should use the row streams.
                        mPrinter = new Printer(mProtocolAdapter.getRawInputStream(), mProtocolAdapter.getRawOutputStream());
                    }
                    //  mPrinter = new Printer(mProtocolAdapter.getRawInputStream(), mProtocolAdapter.getRawOutputStream());
                    mPrinter.setConnectionListener(new Printer.ConnectionListener() {
                        @Override
                        public void onDisconnect() {
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    dialog.dismiss();
                    if (initListener != null) {
                        initListener.initCompleted();
                    }
                }
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();
        /*final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(context.getString(R.string.title_please_wait));
        dialog.setMessage(context.getString(R.string.connecting_to_printer));
        dialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_spinner));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                    BluetoothDevice btDevice = btAdapter.getRemoteDevice(macAddress);
                    BluetoothSocket btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
                    btSocket.connect();
                    mBtSocket = btSocket;
                    InputStream in = mBtSocket.getInputStream();
                    OutputStream out = mBtSocket.getOutputStream();
                    mProtocolAdapter = new ProtocolAdapter(in, out);
                    mPrinter = new Printer(mProtocolAdapter.getRawInputStream(), mProtocolAdapter.getRawOutputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    dialog.dismiss();
                    if (initListener != null) {
                        initListener.initCompleted();
                    }
                }
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();*/
    }

    // Printing the code for the invoice,salesorder,SalesReturn,Receipt to print

    // print invoice
    public void printInvoice(int copy, ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails, ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList){

        try {

           // String configLabel = "";

            String address1="";
            String address2="";
            String address3="";
            String malaysiaShowGST="0";
            String user="";
            String taxType="";
            double taxZ = 0.00, taxS = 0.00, subTotalZ = 0.00, subTotalS = 0.00;
            String localCurrency="$";
            double totalvalue = 0;
            int s = 1;


            String companyTaxValue = "";
            if (companyTaxValue != null && !companyTaxValue.isEmpty()) {
                companyTaxValue = companyTaxValue.split("\\.")[0];
            }
            Log.d("printerType_at_printing", "-->" + printertype);
            if (printertype.matches("4 Inch Bluetooth")) {
                byte[] send = new byte[3];
                //Title
                send[0] = 0x1b;
                send[1] = 0x61;
                send[2] = 0;
                GlobalData.mService.write(send);
            }

            //  GlobalData.mTTransmission.sendBytes(send);
            for (int n = 0; n < copy; n++) {


                configLabel += subAlignCenter("TAX INVOICE");
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;

                configLabel += printCompanyInformation();

                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;


                /*String invTime = "";
                if (showCreateTime.equalsIgnoreCase("True")) {
                    for (ProductDetails prd : productdet) {
                        invTime = prd.getCreateDate().toString().split("\\ ")[1];
                    }

                } else {
                    invTime = "";
                }*/

                configLabel += subAlignDataLeft(HEADER_SPACE, INVOICE_NO);
                configLabel += COLON + " " + invoiceHeaderDetails.get(0).getInvoiceNumber();
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignDataLeft(HEADER_SPACE, INVOICE_DATE);
                configLabel += COLON + " " + invoiceHeaderDetails.get(0).getInvoiceDate() + " " ;//+ invTime;
                configLabel += LINE_SEPARATOR;


                    if (!invoiceHeaderDetails.get(0).getCustomerCode().equals("")) {
                        configLabel += subAlignDataLeft(HEADER_SPACE, CUSTOMER_CODE);
                        configLabel += COLON + " " + invoiceHeaderDetails.get(0).getCustomerCode();
                        configLabel += LINE_SEPARATOR;
                    }
                }

                if (!invoiceHeaderDetails.get(0).getCustomerName().matches("")) {
                    String customername=invoiceHeaderDetails.get(0).getCustomerName();
                        configLabel += subAlignDataLeft(HEADER_SPACE, CUSTOMER_NAME);
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (customername.length() > 30) {
                                String data = concatenateStr(customername);
                                configLabel += COLON + " " + data;
                            } else {
                                configLabel += COLON + " " + customername;
                            }

                        } // Sathish 02-09-2020 - Change the 2.5 Inches Blue tooth printer Settings
                        else if (printertype.matches("2.5 Inch Bluetooth Generic")) {

                            if (customername.length() > 17) {
                                String data = concatenateStrCustomerName(customername);
                                configLabel += COLON + " " + data;
                            } else {
                                configLabel += COLON + " " + customername;
                            }

                        } else {
                            configLabel += COLON + " " + customername;
                        }
                        configLabel += LINE_SEPARATOR;

                    }


                    if (!address1.matches("")) {
                        configLabel += subAlignDataLeft(HEADER_SPACE, ADDRESS);
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (address1.length() > 29) {
                                String data = concatenateStr(address1);
                                configLabel += COLON + " " + data;
                            } else {
                                configLabel += COLON + " " + address1;
                            }

                        }

                        // Change the 2.5 inches printer settings blutooth printer
                        else if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                            if (address1.length() > 17) {
                                String data = concatenateStrCustomerName(address1);
                                configLabel += COLON + " " + data;
                            } else {
                                configLabel += COLON + " " + address1;
                            }

                        } else {
                            configLabel += COLON + " " + address1;
                        }
                        configLabel += LINE_SEPARATOR;
                    }

                    if (!address2.matches("")) {

                        configLabel += subAlignDataLeft(HEADER_SPACE, "        ");
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (address2.length() > 29) {
                                String data = concatenateStr(address2);
                                configLabel += " " + " " + data;
                            } else {
                                configLabel += " " + " " + address2;
                            }

                        }

                        // Sathish 02-09-2020 Change the Bluetooth print Settings Alignment
                        else if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                            if (address2.length() > 17) {
                                String data = concatenateStrCustomerName(address2);
                                configLabel += " " + " " + data;
                            } else {
                                configLabel += " " + " " + address2;
                            }

                        } else {
                            configLabel += " " + " " + address2;
                        }
                        configLabel += LINE_SEPARATOR;

                    }


                    if (!address3.matches("")) {
                        configLabel += subAlignDataLeft(HEADER_SPACE, "        ");
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (address3.length() > 29) {
                                String data = concatenateStr(address3);
                                configLabel += " " + " " + data;
                            } else {
                                configLabel += " " + " " + address3;
                            }
                        }
                        // Sathish 02-09-2020 Change the printer settings in 2.5 inches
                        else if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                            if (address3.length() > 17) {
                                String data = concatenateStrCustomerName(address3);
                                configLabel += " " + " " + data;
                            } else {
                                configLabel += " " + " " + address3;
                            }
                        } else {
                            configLabel += " " + " " + address3;
                        }
                        configLabel += LINE_SEPARATOR;
                    }


               /*     if (!CustomerSetterGetter.getCustomerPhone().matches("")) {
                        configLabel += subAlignDataLeft(HEADER_SPACE, PHONE_NO);
                        configLabel += COLON + " " + CustomerSetterGetter.getCustomerPhone();
                        configLabel += LINE_SEPARATOR;
                    }


                    if (!CustomerSetterGetter.getCustomerHP().matches("")) {
                        configLabel += subAlignDataLeft(HEADER_SPACE, HEAD_PHONE);
                        configLabel += COLON + " " + CustomerSetterGetter.getCustomerHP();
                        configLabel += LINE_SEPARATOR;

                    }*/



                   /* if (!CustomerSetterGetter.getCustomerEmail().matches("")) {
                        configLabel += subAlignDataLeft(HEADER_SPACE, EMAIL);
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (CustomerSetterGetter.getCustomerEmail().length() > 21) {
                                String data = concatenateStr(CustomerSetterGetter.getCustomerEmail());
                                configLabel += COLON + " " + data;
                            } else {
                                configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                            }

                        }
                      *//*  // Sathish 02-09-2020 Change the Printer bluetooth
                        else if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                            if (CustomerSetterGetter.getCustomerEmail().length() > 15) {
                                String data = concatenateStr(CustomerSetterGetter.getCustomerEmail());
                                configLabel += COLON + " " + data;
                            } else {
                                configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                            }
                        } else {
                            configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                        }*//*
                        configLabel += LINE_SEPARATOR;
                    }*/

                /*
                    if (!CustomerSetterGetter.getCustomerTerms().matches("")) {

                        configLabel += subAlignDataLeft(HEADER_SPACE, TERM);
                        configLabel += COLON + " " + CustomerSetterGetter.getCustomerTerms();
                        configLabel += LINE_SEPARATOR;

                    }*/

                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignData(SLNO_WIDTH, "No");
                configLabel += subAlignData(DESCRIPTION_WIDTH, "Name");
                configLabel += subAlignRightData(QTY_WIDTH, "  " + "Qty");
                configLabel += subAlignRightData(PRICE_WIDTH, " " + "Price");
                //if (malaysiaShowGST.matches("1")) {
                 //   configLabel += subAlignRightData(TAX_WIDTH, "GST(" + companyTaxValue + "%)");
              //  }
                configLabel += subAlignRightData(TOTAL_WIDTH, " " + "Total");
                configLabel += subAlignRightData(TAX_TYPE_WIDTH, "  ");
                configLabel += LINE_SEPARATOR;
                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;

                int index=1;
                for (InvoicePrintPreviewModel.InvoiceList prod : invoiceList) {
                    configLabel += subAlignData(SLNO_WIDTH, index+"");
                    configLabel += subAlignData(DESCRIPTION_WIDTH, (prod.getDescription().length() > 40) ? prod.getDescription().substring(0, 39) : prod.getDescription());
                    // print next line if it is 3 Inch Bluetooth Generic printer
                    if (printertype.matches("3 Inch Bluetooth Generic")) {
                        if (prod.getDescription().length() > DESCRIPTION_WIDTH) {
                            configLabel += LINE_SEPARATOR;
                            if (malaysiaShowGST.matches("1")) {
                                configLabel += subAlignRightData(15, "");
                            } else {
                                configLabel += subAlignRightData(24, "");
                            }
                        }
                    }

                    if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                        if (prod.getDescription().length() > DESCRIPTION_WIDTH) {
                            configLabel += LINE_SEPARATOR;
                            if (malaysiaShowGST.matches("1")) {
                                configLabel += subAlignRightData(15, "");
                            } else {
                                configLabel += subAlignRightData(13, "");
                            }
                        }
                    }
                       /*configLabel += subAlignRightData(QTY_WIDTH, "" + prod.getQty());
                       configLabel += subAlignRightData(PRICE_WIDTH, "" + prod.getPrice());*/
                    configLabel += subAlignRightData(QTY_WIDTH, "  " +  Utils.getQtyValue(prod.getNetQty()));
                    double value = Double.parseDouble(prod.getPricevalue());
                    // Log.d("ParseString",""+value);
                  //  String values = String.format("%.2f", value);
                    configLabel += subAlignRightData(PRICE_WIDTH, "  " + Utils.twoDecimalPoint(value));
                    configLabel += subAlignRightData(TOTAL_WIDTH, "  " + Utils.twoDecimalPoint(Double.parseDouble(prod.getTotal())));

                    // Define the FOC and ReturnQty values to print the printer

            /*
                    if (prod.getFocqty() > 0) {
                        configLabel += LINE_SEPARATOR;
                        configLabel += subAlignData(SLNO_WIDTH, "  ");
                        configLabel += subAlignData(SLNO_WIDTH, "Foc      ");
                        //configLabel += subAlignDataRight(RIGHT_SPACE,"Foc ");
                        configLabel += COLON + " " + (int) prod.getFocqty();
                    }
                    if (prod.getExchangeqty() > 0) {
                        configLabel += LINE_SEPARATOR;
                        configLabel += subAlignData(SLNO_WIDTH, "  ");
                        configLabel += subAlignData(SLNO_WIDTH, "Exchange ");
                        // configLabel += subAlignDataRight(RIGHT_SPACE,"Exchange ");
                        configLabel += COLON + " " + (int) prod.getExchangeqty();
                    }

                    if ((prod.getIssueQty() != null && !prod
                            .getIssueQty().isEmpty())
                            && (prod.getReturnQty() != null && !prod
                            .getReturnQty().isEmpty())) {

                        if ((Double.valueOf(prod.getIssueQty()) > 0)
                                && (Double.valueOf(prod.getReturnQty()) > 0)) {
                            configLabel += LINE_SEPARATOR;
                            configLabel += subAlignDataRight(RIGHT_SPACE, "Issue ");
                            configLabel += COLON + " " + prod.getIssueQty();
                            configLabel += subAlignDataRight(RIGHT_SPACE, "Return ");
                            configLabel += COLON + " " + prod.getReturnQty();
                        }


                    }*/


                    configLabel += LINE_SEPARATOR;
                    index++;
                }
            }catch (Exception exception){

        }

            configLabel += horizontalLine("-");
        //    for (ProductDetails prd : productdet) {
                configLabel += LINE_SEPARATOR;
       // Utils.twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getItemDiscount()))
                if (invoiceHeaderDetails.get(0).getItemDiscount()!=null && Double.parseDouble(invoiceHeaderDetails.get(0).getItemDiscount()) > 0) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, "Item Discount");
                    configLabel += subAlignDataRight(1, COLON);
                    configLabel += subAlignRightData(TOTAL_SPACE, " " + invoiceHeaderDetails.get(0).getItemDiscount().toString());
                    configLabel += LINE_SEPARATOR;
                    // configLabel += subAlignDataLeftSpace(9,COLON+" "+prd.getItemdisc().toString());
                }

                String localCurrency="$";
                int currencyLen = localCurrency.length() - 2;
                if (currencyLen > 3) {
                    localCurrency = (localCurrency.length() > 3) ? localCurrency.substring(0, 2) : localCurrency;

                }
                System.out.println("currencyLen-->" + currencyLen);
                String taxType=invoiceHeaderDetails.get(0).getTaxType();
                if (taxType.equalsIgnoreCase("E")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE - currencyLen, "Total Amount (" + localCurrency + ")");/*Amt Excel.GST(" + localCurrency + ")*/
                } else if (taxType.equalsIgnoreCase("I")) {
                    configLabel += subAlignDataLeft(RIGHT_SPACE - currencyLen, "Total Amount (" + localCurrency + ")");/*Amt Incel.GST(" + localCurrency + ")*/
                } else {
                    configLabel += subAlignDataLeft(RIGHT_SPACE, "Total Amount (" + localCurrency + ") ");
                }

           if (invoiceHeaderDetails.get(0).getTaxType().equals("I")) {
               double bill_disc=0.0;
               if (invoiceHeaderDetails.get(0).getBillDiscount()!=null && !invoiceHeaderDetails.get(0).getBillDiscount().isEmpty() && invoiceHeaderDetails.get(0).getBillDiscount().equals("null")){
                   bill_disc=Double.parseDouble(invoiceHeaderDetails.get(0).getBillDiscount().toString());
               }

               double sub_total = Double.parseDouble(invoiceHeaderDetails.get(0).getSubTotal()) - Double.parseDouble(invoiceHeaderDetails.get(0).getNetTax());
               double net_sub_tot=sub_total+bill_disc;

               if (printertype.matches("3 Inch Bluetooth Generic")) {
                   configLabel += subAlignDataRight(1, COLON);
                   configLabel += subAlignRightData(TOTAL_SPACE, "                " + twoDecimalPoint(net_sub_tot));
               } else if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                   configLabel += subAlignDataRight(1, COLON);
                   configLabel += subAlignRightData(TOTAL_SPACE, "      " + twoDecimalPoint(net_sub_tot));
               }
           }else {
               double bill_disc=0.0;
               if (invoiceHeaderDetails.get(0).getBillDiscount()!=null && !invoiceHeaderDetails.get(0).getBillDiscount().isEmpty() && invoiceHeaderDetails.get(0).getBillDiscount().equals("null")){
                   bill_disc=Double.parseDouble(invoiceHeaderDetails.get(0).getBillDiscount().toString());
               }
               double sub_total = Double.parseDouble(invoiceHeaderDetails.get(0).getSubTotal())+bill_disc;
               if (printertype.matches("3 Inch Bluetooth Generic")) {
                   configLabel += subAlignDataRight(1, COLON);
                   configLabel += subAlignRightData(TOTAL_SPACE, "                " + twoDecimalPoint(sub_total));
               } else if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                   configLabel += subAlignDataRight(1, COLON);
                   configLabel += subAlignRightData(TOTAL_SPACE, "      " + twoDecimalPoint(sub_total));
               }
           }

                configLabel += LINE_SEPARATOR;
             //   if (Double.parseDouble(invoiceHeaderDetails.get(0).getBillDiscount()) > 0) {
                    //configLabel +=  COLON+" "+"12.022";

                    //   configLabel += subAlignDataRight(1,COLON);
                    //  configLabel += subAlignRightData(54," "+prd.getItemdisc().toString());


                    //configLabel += subAlignData("Item Discount  : 12.022");
                    // configLabel += subAlignDataDetail("SubTotal   : 12.022");
                    configLabel += subAlignDataLeft(RIGHT_SPACE, "Bill Discount");
                    configLabel += subAlignDataRight(1, COLON);
                    configLabel += subAlignRightData(TOTAL_SPACE, " " + invoiceHeaderDetails.get(0).getBillDiscount().toString());
                    configLabel += LINE_SEPARATOR;
                    // configLabel += subAlignDataLeftSpace(9,COLON+" "+prd.getBilldisc().toString());
                    // configLabel +=  COLON+" "+"12.022";
                    if (invoiceHeaderDetails.get(0).getTaxType().equals("I")) {

                        double sub_total = Double.parseDouble(invoiceHeaderDetails.get(0).getSubTotal()) - Double.parseDouble(invoiceHeaderDetails.get(0).getNetTax());

                        configLabel += subAlignDataLeft(RIGHT_SPACE, "Sub Total");
                        configLabel += subAlignDataRight(1, COLON);
                        configLabel += subAlignRightData(TOTAL_SPACE, " " + Utils.twoDecimalPoint(sub_total));
                    }else {
                        configLabel += subAlignDataLeft(RIGHT_SPACE, "Sub Total");
                        configLabel += subAlignDataRight(1, COLON);
                        configLabel += subAlignRightData(TOTAL_SPACE, " " + Utils.twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getSubTotal())));
                    }

                    configLabel += LINE_SEPARATOR;
              //  }

                //else{


                String displayTax;
                if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                    if (taxType.matches("I")) {
                        displayTax = "Incl";
                    } else if (taxType.matches("Z")) {
                        displayTax = "Zero";
                    } else {
                        displayTax = "Excl";
                    }
                } else {
                    if (taxType.matches("I")) {
                        displayTax = "Inclusive";
                    } else if (taxType.matches("Z")) {
                        displayTax = "Zero";
                    } else {
                        displayTax = "Exclusive";
                    }
                }

                String companyTaxValue=invoiceHeaderDetails.get(0).getTaxValue();

                configLabel += subAlignDataLeft(RIGHT_SPACE, "(+) GST(" + displayTax + ") @ " + companyTaxValue + "% (" + localCurrency + ") ");
                configLabel += subAlignDataRight(1, COLON);
                configLabel += subAlignRightData(TOTAL_SPACE, "" + Utils.twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getNetTax())));

                //configLabel +=  COLON+" "+"12.022";
                //  configLabel += subAlignDataDetail("Tax  : 12.0222");
                configLabel += LINE_SEPARATOR;
                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;


                configLabel += subAlignDataLeft(RIGHT_SPACE - currencyLen, "Total Amount Payable(" + localCurrency + ")");
                configLabel += subAlignDataRight(1, COLON);

                configLabel += subAlignRightData(TOTAL_SPACE, "" + Utils.twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getNetTotal())));

                configLabel += LINE_SEPARATOR;

                // This is for the Paid and balance amount description print....

              /*  configLabel += subAlignDataLeft(RIGHT_SPACE, "Paid Amount (" + localCurrency + ") ");
                configLabel += subAlignDataRight(1, COLON);
                configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getPaidamount().toString());

                configLabel += LINE_SEPARATOR;
                configLabel += subAlignDataLeft(RIGHT_SPACE, "Balance (" + localCurrency + ") ");
                configLabel += subAlignDataRight(1, COLON);
                configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getBalanceAmount().toString());

                configLabel += LINE_SEPARATOR;*/
                configLabel += horizontalLine("-");


         //   }


            //  configLabel += LINE_SEPARATOR;
            //  configLabel += horizontalLine("-");
            //  configLabel += LINE_SEPARATOR;
            // configLabel += LINE_SEPARATOR;
            //  configLabel += LINE_SEPARATOR;
            //  configLabel += LINE_SEPARATOR;
            //  configLabel += LINE_SEPARATOR;
            //  configLabel += LINE_SEPARATOR;
            //  configLabel += LINE_SEPARATOR;
            //  configLabel += LINE_SEPARATOR;
            //  configLabel += LINE_SEPARATOR;
            // configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;
            //  configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;
            if (printertype.equals("2.5 Inch Bluetooth Generic")) {
                configLabel += subAlignDataLeft(RIGHT_SPACE, "Received By");
            } else {
                configLabel += subAlignDataBtSpace("Received By");
            }
            configLabel += subAlignDataRight(BETWEEN_SPACE, " ");
            configLabel += subAlignDataBtSpace("Authorized By");
            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Printed Date ");
            configLabel += subAlignDataLeftSpace(17, COLON + " " + serverdateTime);

            // print next line if it is 3 Inch Bluetooth Generic printer
            if (printertype.matches("3 Inch Bluetooth Generic")) {
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignDataLeft(RIGHT_SPACE, "Issued By ");
                configLabel += subAlignDataLeftSpace(17, COLON + " " + user);

            }
            // Sathish 02-09-2020 Printer Align issues for 2.5 Inches
            else if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignDataLeft(RIGHT_SPACE, "Issued By ");
                configLabel += subAlignDataLeftSpace(14, COLON + " " + user);

            } else if (printertype.matches("4 Inch Bluetooth")) {
                configLabel += subAlignDataRight(13, " ");
                configLabel += subAlignDataRight(11, "Issued By : ");
                configLabel += subAlignDataLeftSpace(14, " " + user);
            }

            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignCenter("Thank You");
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;
            configLabel += LINE_SEPARATOR;

            System.out.println(configLabel);


            //Start
            /*String valueUTF8 = URLEncoder.encode(configLabel, "UTF-8");
            String res = valueUTF8.replaceAll("\\%", "\\_");
            String result = res.replaceAll("\\+", "\\ ");

            Log.d("valueUTF8",""+result);*/
            // End

            sendMessage(configLabel);

         /*catch (Exception e) {
           // helper.showErrorDialog(e.getMessage());
        }*/


    }

    public void printSalesOrder(int copy, ArrayList<SalesOrderPrintPreviewModel> salesOrderHeaderDetails, ArrayList<SalesOrderPrintPreviewModel.SalesList> salesOrderList){

        try {

            // String configLabel = "";
            String address1="";
            String address2="";
            String address3="";
            String malaysiaShowGST="0";
            String user="";
            String taxType="";
            double taxZ = 0.00, taxS = 0.00, subTotalZ = 0.00, subTotalS = 0.00;
            String localCurrency="$";
            double totalvalue = 0;
            int s = 1;

            String companyTaxValue = "";
            if (companyTaxValue != null && !companyTaxValue.isEmpty()) {
                companyTaxValue = companyTaxValue.split("\\.")[0];
            }
            Log.d("printerType_at_printing", "-->" + printertype);
            if (printertype.matches("4 Inch Bluetooth")) {
                byte[] send = new byte[3];
                //Title
                send[0] = 0x1b;
                send[1] = 0x61;
                send[2] = 0;
                GlobalData.mService.write(send);
            }

            //  GlobalData.mTTransmission.sendBytes(send);
            for (int n = 0; n < copy; n++) {


                configLabel += subAlignCenter("SALES ORDER");
                configLabel += LINE_SEPARATOR;
                configLabel += LINE_SEPARATOR;

                configLabel += printCompanyInformation();

                configLabel += horizontalLine("-");
                configLabel += LINE_SEPARATOR;


                /*String invTime = "";
                if (showCreateTime.equalsIgnoreCase("True")) {
                    for (ProductDetails prd : productdet) {
                        invTime = prd.getCreateDate().toString().split("\\ ")[1];
                    }

                } else {
                    invTime = "";
                }*/

                configLabel += subAlignDataLeft(HEADER_SPACE, "SO Number");
                configLabel += COLON + " " + salesOrderHeaderDetails.get(0).getSoNumber();
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignDataLeft(HEADER_SPACE, "SO Date");
                configLabel += COLON + " " + salesOrderHeaderDetails.get(0).getSoDate() + " " ;//+ invTime;
                configLabel += LINE_SEPARATOR;

                if (!salesOrderHeaderDetails.get(0).getCustomerCode().equals("")) {
                    configLabel += subAlignDataLeft(HEADER_SPACE, CUSTOMER_CODE);
                    configLabel += COLON + " " + salesOrderHeaderDetails.get(0).getCustomerCode();
                    configLabel += LINE_SEPARATOR;
                }
            }

            if (!salesOrderHeaderDetails.get(0).getCustomerName().matches("")) {
                String customername=salesOrderHeaderDetails.get(0).getCustomerName();
                configLabel += subAlignDataLeft(HEADER_SPACE, CUSTOMER_NAME);
                if (printertype.matches("3 Inch Bluetooth Generic")) {
                    if (customername.length() > 30) {
                        String data = concatenateStr(customername);
                        configLabel += COLON + " " + data;
                    } else {
                        configLabel += COLON + " " + customername;
                    }

                } // Sathish 02-09-2020 - Change the 2.5 Inches Blue tooth printer Settings
                else if (printertype.matches("2.5 Inch Bluetooth Generic")) {

                    if (customername.length() > 17) {
                        String data = concatenateStrCustomerName(customername);
                        configLabel += COLON + " " + data;
                    } else {
                        configLabel += COLON + " " + customername;
                    }

                } else {
                    configLabel += COLON + " " + customername;
                }
                configLabel += LINE_SEPARATOR;

            }


            if (!address1.matches("")) {
                configLabel += subAlignDataLeft(HEADER_SPACE, ADDRESS);
                if (printertype.matches("3 Inch Bluetooth Generic")) {
                    if (address1.length() > 29) {
                        String data = concatenateStr(address1);
                        configLabel += COLON + " " + data;
                    } else {
                        configLabel += COLON + " " + address1;
                    }

                }

                // Change the 2.5 inches printer settings blutooth printer
                else if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                    if (address1.length() > 17) {
                        String data = concatenateStrCustomerName(address1);
                        configLabel += COLON + " " + data;
                    } else {
                        configLabel += COLON + " " + address1;
                    }

                } else {
                    configLabel += COLON + " " + address1;
                }
                configLabel += LINE_SEPARATOR;
            }

            if (!address2.matches("")) {

                configLabel += subAlignDataLeft(HEADER_SPACE, "        ");
                if (printertype.matches("3 Inch Bluetooth Generic")) {
                    if (address2.length() > 29) {
                        String data = concatenateStr(address2);
                        configLabel += " " + " " + data;
                    } else {
                        configLabel += " " + " " + address2;
                    }

                }

                // Sathish 02-09-2020 Change the Bluetooth print Settings Alignment
                else if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                    if (address2.length() > 17) {
                        String data = concatenateStrCustomerName(address2);
                        configLabel += " " + " " + data;
                    } else {
                        configLabel += " " + " " + address2;
                    }

                } else {
                    configLabel += " " + " " + address2;
                }
                configLabel += LINE_SEPARATOR;

            }


            if (!address3.matches("")) {
                configLabel += subAlignDataLeft(HEADER_SPACE, "        ");
                if (printertype.matches("3 Inch Bluetooth Generic")) {
                    if (address3.length() > 29) {
                        String data = concatenateStr(address3);
                        configLabel += " " + " " + data;
                    } else {
                        configLabel += " " + " " + address3;
                    }
                }
                // Sathish 02-09-2020 Change the printer settings in 2.5 inches
                else if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                    if (address3.length() > 17) {
                        String data = concatenateStrCustomerName(address3);
                        configLabel += " " + " " + data;
                    } else {
                        configLabel += " " + " " + address3;
                    }
                } else {
                    configLabel += " " + " " + address3;
                }
                configLabel += LINE_SEPARATOR;
            }


               /*     if (!CustomerSetterGetter.getCustomerPhone().matches("")) {
                        configLabel += subAlignDataLeft(HEADER_SPACE, PHONE_NO);
                        configLabel += COLON + " " + CustomerSetterGetter.getCustomerPhone();
                        configLabel += LINE_SEPARATOR;
                    }


                    if (!CustomerSetterGetter.getCustomerHP().matches("")) {
                        configLabel += subAlignDataLeft(HEADER_SPACE, HEAD_PHONE);
                        configLabel += COLON + " " + CustomerSetterGetter.getCustomerHP();
                        configLabel += LINE_SEPARATOR;

                    }*/



                   /* if (!CustomerSetterGetter.getCustomerEmail().matches("")) {
                        configLabel += subAlignDataLeft(HEADER_SPACE, EMAIL);
                        if (printertype.matches("3 Inch Bluetooth Generic")) {
                            if (CustomerSetterGetter.getCustomerEmail().length() > 21) {
                                String data = concatenateStr(CustomerSetterGetter.getCustomerEmail());
                                configLabel += COLON + " " + data;
                            } else {
                                configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                            }

                        }
                      *//*  // Sathish 02-09-2020 Change the Printer bluetooth
                        else if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                            if (CustomerSetterGetter.getCustomerEmail().length() > 15) {
                                String data = concatenateStr(CustomerSetterGetter.getCustomerEmail());
                                configLabel += COLON + " " + data;
                            } else {
                                configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                            }
                        } else {
                            configLabel += COLON + " " + CustomerSetterGetter.getCustomerEmail();
                        }*//*
                        configLabel += LINE_SEPARATOR;
                    }*/

                /*
                    if (!CustomerSetterGetter.getCustomerTerms().matches("")) {

                        configLabel += subAlignDataLeft(HEADER_SPACE, TERM);
                        configLabel += COLON + " " + CustomerSetterGetter.getCustomerTerms();
                        configLabel += LINE_SEPARATOR;

                    }*/

            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignData(SLNO_WIDTH, "No");
            configLabel += subAlignData(DESCRIPTION_WIDTH, "Name");
            configLabel += subAlignRightData(QTY_WIDTH, "  " + "Qty");
            configLabel += subAlignRightData(PRICE_WIDTH, " " + "Price");
            //if (malaysiaShowGST.matches("1")) {
            //   configLabel += subAlignRightData(TAX_WIDTH, "GST(" + companyTaxValue + "%)");
            //  }
            configLabel += subAlignRightData(TOTAL_WIDTH, " " + "Total");
            configLabel += subAlignRightData(TAX_TYPE_WIDTH, "  ");
            configLabel += LINE_SEPARATOR;
            configLabel += horizontalLine("-");
            configLabel += LINE_SEPARATOR;

            int index=1;
            for (SalesOrderPrintPreviewModel.SalesList prod : salesOrderList) {
                configLabel += subAlignData(SLNO_WIDTH, index+"");
                configLabel += subAlignData(DESCRIPTION_WIDTH, (prod.getDescription().length() > 40) ? prod.getDescription().substring(0, 39) : prod.getDescription());
                // print next line if it is 3 Inch Bluetooth Generic printer
                if (printertype.matches("3 Inch Bluetooth Generic")) {
                    if (prod.getDescription().length() > DESCRIPTION_WIDTH) {
                        configLabel += LINE_SEPARATOR;
                        if (malaysiaShowGST.matches("1")) {
                            configLabel += subAlignRightData(15, "");
                        } else {
                            configLabel += subAlignRightData(24, "");
                        }
                    }
                }

                if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                    if (prod.getDescription().length() > DESCRIPTION_WIDTH) {
                        configLabel += LINE_SEPARATOR;
                        if (malaysiaShowGST.matches("1")) {
                            configLabel += subAlignRightData(15, "");
                        } else {
                            configLabel += subAlignRightData(13, "");
                        }
                    }
                }
                       /*configLabel += subAlignRightData(QTY_WIDTH, "" + prod.getQty());
                       configLabel += subAlignRightData(PRICE_WIDTH, "" + prod.getPrice());*/
                configLabel += subAlignRightData(QTY_WIDTH, "  " +  Utils.getQtyValue(prod.getNetQty()));
                double value = Double.parseDouble(prod.getPricevalue());
                // Log.d("ParseString",""+value);
                //  String values = String.format("%.2f", value);
                configLabel += subAlignRightData(PRICE_WIDTH, "  " + Utils.twoDecimalPoint(value));
                configLabel += subAlignRightData(TOTAL_WIDTH, "  " + Utils.twoDecimalPoint(Double.parseDouble(prod.getTotal())));

                // Define the FOC and ReturnQty values to print the printer

            /*
                    if (prod.getFocqty() > 0) {
                        configLabel += LINE_SEPARATOR;
                        configLabel += subAlignData(SLNO_WIDTH, "  ");
                        configLabel += subAlignData(SLNO_WIDTH, "Foc      ");
                        //configLabel += subAlignDataRight(RIGHT_SPACE,"Foc ");
                        configLabel += COLON + " " + (int) prod.getFocqty();
                    }
                    if (prod.getExchangeqty() > 0) {
                        configLabel += LINE_SEPARATOR;
                        configLabel += subAlignData(SLNO_WIDTH, "  ");
                        configLabel += subAlignData(SLNO_WIDTH, "Exchange ");
                        // configLabel += subAlignDataRight(RIGHT_SPACE,"Exchange ");
                        configLabel += COLON + " " + (int) prod.getExchangeqty();
                    }

                    if ((prod.getIssueQty() != null && !prod
                            .getIssueQty().isEmpty())
                            && (prod.getReturnQty() != null && !prod
                            .getReturnQty().isEmpty())) {

                        if ((Double.valueOf(prod.getIssueQty()) > 0)
                                && (Double.valueOf(prod.getReturnQty()) > 0)) {
                            configLabel += LINE_SEPARATOR;
                            configLabel += subAlignDataRight(RIGHT_SPACE, "Issue ");
                            configLabel += COLON + " " + prod.getIssueQty();
                            configLabel += subAlignDataRight(RIGHT_SPACE, "Return ");
                            configLabel += COLON + " " + prod.getReturnQty();
                        }


                    }*/


                configLabel += LINE_SEPARATOR;
                index++;
            }
        }catch (Exception exception){

        }

        configLabel += horizontalLine("-");
        //    for (ProductDetails prd : productdet) {
        configLabel += LINE_SEPARATOR;
        // Utils.twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getItemDiscount()))
        if (Double.parseDouble(salesOrderHeaderDetails.get(0).getItemDiscount()) > 0) {
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Item Discount");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(TOTAL_SPACE, " " + salesOrderHeaderDetails.get(0).getItemDiscount().toString());
            configLabel += LINE_SEPARATOR;
            // configLabel += subAlignDataLeftSpace(9,COLON+" "+prd.getItemdisc().toString());
        }

        String localCurrency="$";
        int currencyLen = localCurrency.length() - 2;
        if (currencyLen > 3) {
            localCurrency = (localCurrency.length() > 3) ? localCurrency.substring(0, 2) : localCurrency;

        }
        System.out.println("currencyLen-->" + currencyLen);
        String taxType=salesOrderHeaderDetails.get(0).getTaxType();
        if (taxType.equalsIgnoreCase("E")) {
            configLabel += subAlignDataLeft(RIGHT_SPACE - currencyLen, "Total Amount (" + localCurrency + ")");/*Amt Excel.GST(" + localCurrency + ")*/
        } else if (taxType.equalsIgnoreCase("I")) {
            configLabel += subAlignDataLeft(RIGHT_SPACE - currencyLen, "Total Amount (" + localCurrency + ")");/*Amt Incel.GST(" + localCurrency + ")*/
        } else {
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Total Amount (" + localCurrency + ") ");
        }

        if (salesOrderHeaderDetails.get(0).getTaxType().equals("I")) {
            double bill_disc=0.0;
            if (salesOrderHeaderDetails.get(0).getBillDiscount()!=null && !salesOrderHeaderDetails.get(0).getBillDiscount().isEmpty() && salesOrderHeaderDetails.get(0).getBillDiscount().equals("null")){
                bill_disc=Double.parseDouble(salesOrderHeaderDetails.get(0).getBillDiscount().toString());
            }

            double sub_total = Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal()) - Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax());
            double net_sub_tot=sub_total+bill_disc;

            if (printertype.matches("3 Inch Bluetooth Generic")) {
                configLabel += subAlignDataRight(1, COLON);
                configLabel += subAlignRightData(TOTAL_SPACE, "                " + twoDecimalPoint(net_sub_tot));
            } else if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                configLabel += subAlignDataRight(1, COLON);
                configLabel += subAlignRightData(TOTAL_SPACE, "      " + twoDecimalPoint(net_sub_tot));
            }
        }else {
            double bill_disc=0.0;
            if (salesOrderHeaderDetails.get(0).getBillDiscount()!=null && !salesOrderHeaderDetails.get(0).getBillDiscount().isEmpty() && salesOrderHeaderDetails.get(0).getBillDiscount().equals("null")){
                bill_disc=Double.parseDouble(salesOrderHeaderDetails.get(0).getBillDiscount().toString());
            }
            double sub_total = Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal())+bill_disc;
            if (printertype.matches("3 Inch Bluetooth Generic")) {
                configLabel += subAlignDataRight(1, COLON);
                configLabel += subAlignRightData(TOTAL_SPACE, "                " + twoDecimalPoint(sub_total));
            } else if (printertype.matches("2.5 Inch Bluetooth Generic")) {
                configLabel += subAlignDataRight(1, COLON);
                configLabel += subAlignRightData(TOTAL_SPACE, "      " + twoDecimalPoint(sub_total));
            }
        }

        configLabel += LINE_SEPARATOR;
        //   if (Double.parseDouble(invoiceHeaderDetails.get(0).getBillDiscount()) > 0) {
        //configLabel +=  COLON+" "+"12.022";

        //   configLabel += subAlignDataRight(1,COLON);
        //  configLabel += subAlignRightData(54," "+prd.getItemdisc().toString());


        //configLabel += subAlignData("Item Discount  : 12.022");
        // configLabel += subAlignDataDetail("SubTotal   : 12.022");
        configLabel += subAlignDataLeft(RIGHT_SPACE, "Bill Discount");
        configLabel += subAlignDataRight(1, COLON);
        configLabel += subAlignRightData(TOTAL_SPACE, " " + salesOrderHeaderDetails.get(0).getBillDiscount().toString());
        configLabel += LINE_SEPARATOR;
        // configLabel += subAlignDataLeftSpace(9,COLON+" "+prd.getBilldisc().toString());
        // configLabel +=  COLON+" "+"12.022";
        if (salesOrderHeaderDetails.get(0).getTaxType().equals("I")) {

            double sub_total = Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal()) - Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax());

            configLabel += subAlignDataLeft(RIGHT_SPACE, "Sub Total");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(TOTAL_SPACE, " " + Utils.twoDecimalPoint(sub_total));
        }else {
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Sub Total");
            configLabel += subAlignDataRight(1, COLON);
            configLabel += subAlignRightData(TOTAL_SPACE, " " + Utils.twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal())));
        }

        configLabel += LINE_SEPARATOR;
        //  }

        //else{


        String displayTax;
        if (printertype.matches("2.5 Inch Bluetooth Generic")) {
            if (taxType.matches("I")) {
                displayTax = "Incl";
            } else if (taxType.matches("Z")) {
                displayTax = "Zero";
            } else {
                displayTax = "Excl";
            }
        } else {
            if (taxType.matches("I")) {
                displayTax = "Inclusive";
            } else if (taxType.matches("Z")) {
                displayTax = "Zero";
            } else {
                displayTax = "Exclusive";
            }
        }

        String companyTaxValue=salesOrderHeaderDetails.get(0).getTaxValue();

        configLabel += subAlignDataLeft(RIGHT_SPACE, "(+) GST(" + displayTax + ") @ " + companyTaxValue + "% (" + localCurrency + ") ");
        configLabel += subAlignDataRight(1, COLON);
        configLabel += subAlignRightData(TOTAL_SPACE, "" + Utils.twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax())));

        //configLabel +=  COLON+" "+"12.022";
        //  configLabel += subAlignDataDetail("Tax  : 12.0222");
        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;


        configLabel += subAlignDataLeft(RIGHT_SPACE - currencyLen, "Total Amount Payable(" + localCurrency + ")");
        configLabel += subAlignDataRight(1, COLON);

        configLabel += subAlignRightData(TOTAL_SPACE, "" + Utils.twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTotal())));

        configLabel += LINE_SEPARATOR;

        // This is for the Paid and balance amount description print....

              /*  configLabel += subAlignDataLeft(RIGHT_SPACE, "Paid Amount (" + localCurrency + ") ");
                configLabel += subAlignDataRight(1, COLON);
                configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getPaidamount().toString());

                configLabel += LINE_SEPARATOR;
                configLabel += subAlignDataLeft(RIGHT_SPACE, "Balance (" + localCurrency + ") ");
                configLabel += subAlignDataRight(1, COLON);
                configLabel += subAlignRightData(TOTAL_SPACE, " " + prd.getBalanceAmount().toString());

                configLabel += LINE_SEPARATOR;*/
        configLabel += horizontalLine("-");


        //   }


        //  configLabel += LINE_SEPARATOR;
        //  configLabel += horizontalLine("-");
        //  configLabel += LINE_SEPARATOR;
        // configLabel += LINE_SEPARATOR;
        //  configLabel += LINE_SEPARATOR;
        //  configLabel += LINE_SEPARATOR;
        //  configLabel += LINE_SEPARATOR;
        //  configLabel += LINE_SEPARATOR;
        //  configLabel += LINE_SEPARATOR;
        //  configLabel += LINE_SEPARATOR;
        //  configLabel += LINE_SEPARATOR;
        // configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        //  configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        if (printertype.equals("2.5 Inch Bluetooth Generic")) {
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Received By");
        } else {
            configLabel += subAlignDataBtSpace("Received By");
        }
        configLabel += subAlignDataRight(BETWEEN_SPACE, " ");
        configLabel += subAlignDataBtSpace("Authorized By");
        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        configLabel += subAlignDataLeft(RIGHT_SPACE, "Printed Date ");
        configLabel += subAlignDataLeftSpace(17, COLON + " " + serverdateTime);

        // print next line if it is 3 Inch Bluetooth Generic printer
        if (printertype.matches("3 Inch Bluetooth Generic")) {
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Issued By ");
            configLabel += subAlignDataLeftSpace(17, COLON + " " + user);

        }
        // Sathish 02-09-2020 Printer Align issues for 2.5 Inches
        else if (printertype.matches("2.5 Inch Bluetooth Generic")) {
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignDataLeft(RIGHT_SPACE, "Issued By ");
            configLabel += subAlignDataLeftSpace(14, COLON + " " + user);

        } else if (printertype.matches("4 Inch Bluetooth")) {
            configLabel += subAlignDataRight(13, " ");
            configLabel += subAlignDataRight(11, "Issued By : ");
            configLabel += subAlignDataLeftSpace(14, " " + user);
        }

        configLabel += LINE_SEPARATOR;
        configLabel += horizontalLine("-");
        configLabel += LINE_SEPARATOR;
        configLabel += subAlignCenter("Thank You");
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;
        configLabel += LINE_SEPARATOR;

        System.out.println(configLabel);


        //Start
            /*String valueUTF8 = URLEncoder.encode(configLabel, "UTF-8");
            String res = valueUTF8.replaceAll("\\%", "\\_");
            String result = res.replaceAll("\\+", "\\ ");

            Log.d("valueUTF8",""+result);*/
        // End

        sendMessage(configLabel);

         /*catch (Exception e) {
           // helper.showErrorDialog(e.getMessage());
        }*/


    }

// Define the methods for the alignment for printer depending the paper width
private  String subAlignCenter(String data){
    int length = (PAPER_WIDTH-data.length())/2;
    //	System.out.println(length);
    // Create a new StringBuilder.
    StringBuilder builder = new StringBuilder();
    for(int i=0; i < length; i++)
    {
        builder.append(" ");
    }

    // Convert to string.
    String result = builder.toString()+data;
    // Print result.
    //	System.out.println(result);
    // Print result.
    return result;
}
    private  String horizontalLine(String data){
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < PAPER_WIDTH; i++) {
            builder.append(data);
        }
        String result = builder.toString();
        return result;
    }
    private static String horizontalLineSpace(String data){
        StringBuilder builderLine = new StringBuilder();
        for(int i=0; i < 41; i++)
        {
            builderLine.append(data);
        }
        String result1 = builderLine.toString();
        int length = (PAPER_WIDTH-result1.length())/2;
        //System.out.println(length);
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++)
        {
            builder.append(" ");
        }
        String result = builder.toString()+result1;
        return result;
    }
    private  String subAlignData(int size,String data){
        int length = (size-data.length());
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++)
        {
            builder.append(" ");
        }
        String result = data+builder.toString();
        return result;
    }
    private  String subAlignRightData(int size,String data){
        int length = (size-data.length());
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++) {
            builder.append(" ");
        }
        String result = builder.toString()+data;
        return result;
    }

    private  String subAlignDataBtSpace(String data){

        StringBuilder builder = new StringBuilder();
        for(int i=0; i < 5; i++) {
            builder.append(" ");
        }
        String result = builder.toString()+data+builder.toString();
        return result;
    }


    private  String subAlignDataLeftSpace(int size,String data){
        int length = size - data.length();
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++)
        {
            builder.append(" ");
        }

        String result = data+builder.toString();
        return result;
    }

    private  String subAlignDataLeft(int size,String data){
        int length = (size-data.length());
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++)
        {
            builder.append(" ");
        }
        String result = data+builder.toString();
        return result;
    }

    private  String subAlignDataRight(int size,String data){
        int length = (size-data.length());
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++)
        {
            builder.append(" ");
        }
        String result = builder.toString()+data;
        return result;
    }
    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tot = df.format(d);

        return tot;
    }

    public String concatenateStr(String data){
        String firstStr ="",lastStr="",result="";
        StringBuilder builder = new StringBuilder();
        if (data.length()>29){
            firstStr = data.substring(0, 29);
            lastStr = data.substring(29, data.length());
            for(int i=0; i < CONCATENATE_LENGTH; i++) {
                builder.append(" ");
            }
            result = firstStr+LINE_SEPARATOR+builder.toString()+lastStr;
        }else{
            result = data;
        }

        System.out.format(result);
        return result;
    }

    public String concatenateStrCustomerName(String data){
        String firstStr ="",lastStr="",result="";
        StringBuilder builder = new StringBuilder();
        if (data.length()>16){
            firstStr = data.substring(0, 16);
            lastStr = data.substring(16, data.length());
            for(int i=0; i < CONCATENATE_LENGTH; i++) {
                builder.append(" ");
            }
            result = firstStr+"\n"+builder.toString()+lastStr;
        }else{
            result = data;
        }

        System.out.format(result);
        return result;
    }

    /* public String concatenateStr(String data){
         String firstStr ="",lastStr="";
         StringBuilder builder = new StringBuilder();
         if (data.length()>29){
             firstStr = data.substring(0, 29);
             lastStr = data.substring(29, data.length());
             for(int i=0; i < CONCATENATE_LENGTH; i++)
             {
                 builder.append(" ");
             }
         }
         String result = firstStr+LINE_SEPARATOR+builder.toString()+lastStr;
         System.out.format(result);
         return result;
     }*/
    private void sendMessage(final String message)
    {
        if(printertype.matches("4 Inch Bluetooth")) {
            try {
                // helper.updateProgressDialog(R.string.connecting_to_printer);
                // Check that we're actually connected before trying anything

                if (GlobalData.mService.getState() != GlobalData.STATE_CONNECTED)
                {
                    Toast.makeText(context, "Not Connected", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (message.length() > 0) {
                    // Get the message bytes and tell the BluetoothService to write
                    byte [] send ;
                    try {
                        send = message.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        send = message.getBytes();

                    }

                    GlobalData.mService.write(send);

                }
            } catch (Exception e) {
               // helper.showErrorDialog(e.getMessage());
            }
            finally {
                // if(flag){
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((Activity) context).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onCompleted();
                                    if(GlobalData.mService!=null){
                                        GlobalData.mService.stop();
                                    }
                                }
                            }
                        });
                    }
                },1500);
            }
        }else if(printertype.matches("3 Inch Bluetooth Generic")) {
            try{
                mPrinter.reset();
                mPrinter.printTaggedText(message.toString());
                mPrinter.feedPaper(110);
                mPrinter.flush();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally{
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((Activity) context).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    if (listener != null) {
                                        listener.onCompleted();
                                    }
                                    closeBluetoothConnection();
                                    closePrinterConnection();
                                  /*  if (mPrinter != null) {
                                        mPrinter.close();
                                    }
                                    if (mProtocolAdapter != null) {
                                        mProtocolAdapter.close();
                                    }
                                    if (mBtSocket != null) {
                                        mBtSocket.close();
                                    }*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                    }
                },1500);
            }
        }else if(printertype.matches("2.5 Inch Bluetooth Generic")) {
            try{
                // Print Light bold text
             /*   mPrinter.reset();
                byte[] format= { 27, 33, 0 };
                byte[] center =  { 0x1b, 'a', 0x01 };
                byte[] arrayOfByte1 = { 27, 33, 0 };
                format[2] = ((byte)(0x8 | arrayOfByte1[2]));
                mPrinter.write(format);
                mPrinter.printTaggedText(message.toString());
                mPrinter.feedPaper(110);
                mPrinter.flush();*/


                // Long Bold text
                mPrinter.reset();
                byte[] arrayOfByte1 = { 27, 33, 0 };
                byte[] format = { 27, 33, 0 };
                byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
                format[2] = ((byte)(0x8 | arrayOfByte1[2]));
                format[2] = ((byte) (0x20 | arrayOfByte1[2]));
                mPrinter.write(format);
                mPrinter.printTaggedText(message.toString());
                mPrinter.feedPaper(110);
                mPrinter.flush();


            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally{
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((Activity) context).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    if (listener != null) {
                                        listener.onCompleted();
                                    }
                                    closeBluetoothConnection();
                                    closePrinterConnection();
                                  /*  if (mPrinter != null) {
                                        mPrinter.close();
                                    }
                                    if (mProtocolAdapter != null) {
                                        mProtocolAdapter.close();
                                    }
                                    if (mBtSocket != null) {
                                        mBtSocket.close();
                                    }*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                    }
                },1500);
            }
        }
    }

    public String getCurrentTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now));
        return dtf.format(now);
    }




    private String printCompanyInformation() {
        String configLabel ="";

        if (!company_name.equals("")) {
            configLabel += subAlignCenter(company_name);
            configLabel += LINE_SEPARATOR;
        }
            if (!company_address1.equals("")&&(!company_address2.equals(""))&&!company_address3.equals("")) {
                //configLabel += subAlignCenter(Company.getAddress1() + ", " +Company.getAddress2() + ", " + Company.getAddress3());
                configLabel += subAlignCenter(company_address1);
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignCenter(company_address2);
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignCenter(company_address3);

            }else if (!company_address1.equals("") && (!company_address2.equals("")) && company_address3.equals("")) {
                // configLabel += subAlignCenter(Company.getAddress1() + ", " +Company.getAddress2());
                configLabel += subAlignCenter(company_address1);
                configLabel += LINE_SEPARATOR;
                configLabel += subAlignCenter(company_address2);

            }else  if (!company_address1.equals("") && (company_address2.equals("")) && company_address3.equals("")) {
                //  configLabel += subAlignCenter(Company.getAddress1());
                configLabel += subAlignCenter(company_address1);
            } else if (!company_address1.equals("")&&(!company_address2.equals(""))) {
            // configLabel += subAlignCenter(Company.getAddress1() + ", " + Company.getAddress2());

            configLabel += subAlignCenter(company_address1);
            configLabel += LINE_SEPARATOR;
            configLabel += subAlignCenter(company_address2);

        }else if (!company_address1.equals("") && (company_address2.equals(""))) {
            configLabel += subAlignCenter(company_address1);
        }else  if (!company_address1.equals("")) {
                configLabel += subAlignCenter(company_address1);
            }
            configLabel += LINE_SEPARATOR;

        return configLabel;
    }





























    private  void closePrinterConnection() {
        if (mRC663 != null) {
            try {
                mRC663.disable();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mRC663.close();
        }

        if (mEMSR != null) {
            mEMSR.close();
        }

        if (mPrinter != null) {
            mPrinter.close();
        }

        if (mProtocolAdapter != null) {
            mProtocolAdapter.close();
        }
    }
    private void closeBluetoothConnection() {
        // Close Bluetooth connection
        BluetoothSocket s = mBtSocket;
        mBtSocket = null;
        if (s != null) {
            //  Log.d(LOG_TAG, "Close Bluetooth socket");
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        context.unregisterReceiver(bluetoothReceiver);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        print();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };
    private boolean isBluetoothEnabled() {
        if (btAdapter != null) {
            return btAdapter.isEnabled();
        }
        return false;
    }

    private boolean enableBluetooth() {
        if (btAdapter == null) {
           // helper.showErrorDialog(R.string.no_bluetooth_support);
            return false;
        } else if (!btAdapter.isEnabled()) {
            return btAdapter.enable();
        }
        return false;
    }
    public void setOnCompletedListener(OnCompletedListener listener) {
        this.listener = listener;
    }
}
