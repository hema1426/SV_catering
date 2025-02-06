package com.winapp.svcatering.zebraprinter;

import static com.winapp.svcatering.utils.Utils.fourDecimalPoint;
import static com.winapp.svcatering.utils.Utils.getPrintSize;
import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.tscdll.TSCActivity;
import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.AddInvoiceActivityOld;
import com.winapp.svcatering.activity.CartActivity;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.fragments.SummaryFragment;
import com.winapp.svcatering.model.CurrencyModel;
import com.winapp.svcatering.model.CustomerStateModel;
import com.winapp.svcatering.model.ExpenseModel;
import com.winapp.svcatering.model.InvoiceByProductModel;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;
import com.winapp.svcatering.model.InvoiceSummaryModel;
import com.winapp.svcatering.model.ReceiptDetailsModel;
import com.winapp.svcatering.model.ReceiptSummaryModel;
import com.winapp.svcatering.model.ReportSalesSummaryModel;
import com.winapp.svcatering.model.ReportStockSummaryModel;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.model.SettlementReceiptDetailModel;
import com.winapp.svcatering.model.SettlementReceiptModel;
import com.winapp.svcatering.model.StockBadRequestReturnModel;
import com.winapp.svcatering.model.StockSummaryReportOpenModel;
import com.winapp.svcatering.model.TransferDetailModel;
import com.winapp.svcatering.receipts.ReceiptPrintPreviewModel;
import com.winapp.svcatering.model.SalesOrderPrintPreviewModel;
import com.winapp.svcatering.salesreturn.SalesReturnPrintPreviewModel;
import com.winapp.svcatering.printpreview.InvoicePrintPreviewActivity;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.SharedPreferenceUtil;
import com.winapp.svcatering.utils.Utils;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class TSCPrinter {

    public interface OnCompletionListener {
        public void onCompleted();
    }

    private Context context;
    private String macAddress;
    private ZebraPrinter printer;
    private BluetoothAdapter bluetoothAdapter;
    public static String configLabel = "";
    // private SettingsManager settings;
    private static final String FILE_NAME = "TEMP.LBL";
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String LINEBREAK = "\r\b";
    private static final int LABEL_WIDTH = 574;
    private static final int FONT = 5;
    private static final int FONT_SIZE = 0;
    private static final int LEFT_MARGIN = 10;
    private static final int RIGHT_MARGIN = 564;
    private static final int LINE_THICKNESS = 2;
    private static final int LINE_SPACING = 40;
    private static final String CMD_TEXT = "T";
    private static final String CMD_LINE = "L";
    private static final String CMD_PRINT = "PRINT" + LINE_SEPARATOR;
    private static final String SPACE = " ";
    private static final int POSTFEED = 50;
    public static int PAPER_WIDTH = 78;
    Bitmap logo, image;
    String logoStr = "logoprint", mUser = "";
    private static final String HEADER = "PW " + LABEL_WIDTH + LINE_SEPARATOR
            + "TONE 0" + LINE_SEPARATOR + "SPEED 3" + LINE_SEPARATOR + "ZPL"
            + POSTFEED + LINE_SEPARATOR + "NO-PACE" + "BAR-SENSE"
            + LINE_SEPARATOR;
    byte FONT_TYPE;
    String custlastname;
    private OnCompletionListener listener;
    private String company_name;
    private String company_code;
    private String company_address1;
    private String company_address2;
    private String company_address3;
    private String company_phone;
    private String company_gst;
    private SessionManager session;
    private HashMap<String, String> user;
    private String username;
    private String locationCode1;
    private TSCActivity TscDll;
    private int height = 100;
    private int bottom_height = 50;
    private int list_height = 10;
    private final int invoiceBottomLine = 30;
    private final int invoiveSubTotalHeight = 30;
    int finalHeight = 0;
    public ArrayList<SettingsModel> settingsList;
    private DBHelper dbHelper;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private String showUserName = "";
    private String showSignature = "";
    private String showLogo = "";
    private String showReturn = "";
    private int invoivePaynowHeight = 0;

    private String showUom = "";
    public static String shortCodeStr = "" ;
    private String showQrCode = "";
    private String showStamp = "";
    private String payNow = "";
    private String bankCode = "";
    private String currentDate = "";
    private String cheque = "";
    String locStr = " ";
    String dateStr = " ";

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
                        Log.d("BluetoothTurnedOn", "bluetooth()");
                        //print();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };

    public void setOnCompletionListener(OnCompletionListener listener) {
        this.listener = listener;
    }

    public TSCPrinter(Context context, String macAddress, String print) {
        try {
            this.context = context;
            this.macAddress = macAddress;
            session = new SessionManager(context);
            user = session.getUserDetails();
            dbHelper = new DBHelper(context);
            sharedPreferenceUtil = new SharedPreferenceUtil(context);

            TscDll = new TSCActivity();
            company_name = user.get(SessionManager.KEY_COMPANY_NAME);
            company_code = user.get(SessionManager.KEY_COMPANY_CODE);
            username = user.get(SessionManager.KEY_USER_NAME);
            locationCode1=user.get(SessionManager.KEY_LOCATION_CODE);
            company_address1 = user.get(SessionManager.KEY_ADDRESS1);
            company_address2 = user.get(SessionManager.KEY_ADDRESS2);
            company_address3 = user.get(SessionManager.KEY_ADDRESS3);
            company_phone = user.get(SessionManager.KEY_PHONE_NO);
            company_gst = user.get(SessionManager.KEY_COMPANY_REG_NO);
            payNow = user.get(SessionManager.KEY_PAY_NOW);
            cheque = user.get(SessionManager.KEY_CHEQUE);
            bankCode = user.get(SessionManager.KEY_BANK);

            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);
            SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            currentDate = df1.format(c);

            shortCodeStr = sharedPreferenceUtil.getStringPreference(sharedPreferenceUtil
                    .KEY_SHORT_CODE,"");

            settingsList = dbHelper.getSettings();
            if (settingsList != null) {
                if (settingsList.size() > 0) {
                    for (SettingsModel model : settingsList) {
                        if (model.getSettingName().equals("showUserName")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showUserName = "true";
                            } else {
                                showUserName = "false";
                            }
                        } else if (model.getSettingName().equals("showLogo")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showLogo = "true";
                            } else {
                                showLogo = "false";
                            }
                        } else if (model.getSettingName().equals("showReturnDetails")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showReturn = "true";
                            } else {
                                showReturn = "false";
                            }
                        } else if (model.getSettingName().equals("showUom")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showUom = "true";
                            } else {
                                showUom = "false";
                            }
                        } else if (model.getSettingName().equals("showSignature")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showSignature = "true";
                            } else {
                                showSignature = "false";
                            }
                        } else if (model.getSettingName().equals("showQRCode")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showQrCode = "true";
                            } else {
                                showQrCode = "false";
                            }
                        } else if (model.getSettingName().equals("showPaidOrUnpaidImage")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showStamp = "true";
                            } else {
                                showStamp = "false";
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

    }


    public TSCPrinter(Context context, String macAddress) {

        try {
            session = new SessionManager(context);
            user = session.getUserDetails();
            company_name = user.get(SessionManager.KEY_COMPANY_NAME);
            company_code = user.get(SessionManager.KEY_COMPANY_CODE);
            username = user.get(SessionManager.KEY_USER_NAME);
            company_address1 = user.get(SessionManager.KEY_ADDRESS1);
            company_address2 = user.get(SessionManager.KEY_ADDRESS2);
            company_address3 = user.get(SessionManager.KEY_ADDRESS3);
            company_phone = user.get(SessionManager.KEY_PHONE_NO);
            company_gst = user.get(SessionManager.KEY_COMPANY_REG_NO);
            payNow = user.get(SessionManager.KEY_PAY_NOW);
            cheque = user.get(SessionManager.KEY_CHEQUE);
            bankCode = user.get(SessionManager.KEY_BANK);
            settingsList = dbHelper.getSettings();
            if (settingsList != null) {
                if (settingsList.size() > 0) {
                    for (SettingsModel model : settingsList) {
                        if (model.getSettingName().equals("showUserName")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showUserName = "true";
                            } else {
                                showUserName = "false";
                            }
                        } else if (model.getSettingName().equals("showLogo")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showLogo = "true";
                            } else {
                                showLogo = "false";
                            }
                        } else if (model.getSettingName().equals("showReturnDetails")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showReturn = "true";
                            } else {
                                showReturn = "false";
                            }
                        } else if (model.getSettingName().equals("showUom")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showUom = "true";
                            } else {
                                showUom = "false";
                            }
                        } else if (model.getSettingName().equals("showSignature")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showSignature = "true";
                            } else {
                                showSignature = "false";
                            }
                        } else if (model.getSettingName().equals("showQRCode")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showQrCode = "true";
                            } else {
                                showQrCode = "false";
                            }
                        } else if (model.getSettingName().equals("showPaidOrUnpaidImage")) {
                            Log.w("SettingName:", model.getSettingName());
                            Log.w("SettingValue:", model.getSettingValue());
                            if (model.getSettingValue().equals("True")) {
                                showStamp = "true";
                            } else {
                                showStamp = "false";
                            }
                        }
                    }
                }
            }

            if (!macAddress.matches("[0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(\\1[0-9A-Fa-f]{2}){4}$")) {
                //  throw new IllegalArgumentException(macAddress + "Invalid Mac Address");
                Toast.makeText(context, "Please connect the printer and try again", Toast.LENGTH_SHORT).show();
            }
            this.context = context;
            this.macAddress = macAddress;
            try {
                Connection connection = null;
                connection = new BluetoothConnection(macAddress);
                connection.open();
                connection.write("! U1 setvar \"device.languages\" \"zpl\"".getBytes());
                SGD.SET("device.languages", "hybrid_xml_zpl", connection);
                // ! U1 do "device.reset" "" // reset device
                //  connection.close();
            } catch (ConnectionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // settings = new SettingsManager(context);
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            // print();
        } catch (Exception ex) {
        }
    }

    private void print() {

        ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Connecting to Printer...");
        dialog.show();

        if (context instanceof InvoicePrintPreviewActivity) {
            InvoicePrintPreviewActivity printPreviewActivity = new InvoicePrintPreviewActivity();
            printPreviewActivity.closeAlert();
        } else if (context instanceof AddInvoiceActivityOld) {
            SummaryFragment.closeAlert();
        } else if (context instanceof CartActivity) {
            CartActivity.closeAlert();
        }

        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
//				Looper.prepare();
                Connection connection = null;
                Log.d("macAddress", "->" + macAddress);
                //  Log.d("SalesordersetgetCode","-->"+SalesOrderSetGet.getShortCode());
                connection = new BluetoothConnection(macAddress);
                try {
                    connection.open();
                    printer = ZebraPrinterFactory.getInstance(connection);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.setMessage("Printing Inprogress...");
                        }
                    });

                    PrinterLanguage printerLanguage = printer.getPrinterControlLanguage();

                    Matrix matrix = new Matrix();
                    matrix.postRotate(180);

                    String showLogo = "True";
                    Log.d("showLogo", "......" + showLogo + "  " + printerLanguage);

                    // Print QR code
                    //printer.printImage(bitmap, 75, 0, 250, 250, false);

				/*	if (Company.getShortCode().equalsIgnoreCase("EVER")){
						try {
							Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.everlite_qrcode);
							if(bmp!=null){
								byte[] command = Utils.decodeBitmap(bmp);
								image =  Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
								printer.printImage(new ZebraImageAndroid(image), -200, 0, 200, 100, false);
							}else{
								Log.e("Print Photo error", "the file isn't exists");
							}
						} catch (Exception e) {
							e.printStackTrace();
							Log.e("PrintTools", "the file isn't exists");
						}
					}*/

                    if (logoStr.matches("logoprint")) {

                        String companyName = "AADHI1";

                        if (showLogo.matches("True")) {


                            //Bitmap logo = BitmapFactory.decodeResource(context.getResources(),R.drawable.aathilogoedit1);

                            logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.aadhi_print_logo);

                            // Bitmap logo =
                            // BitmapFactory.decodeResource(context.getResources(),
                            // R.drawable.test2); // don't remove

                            int width = 570, imgWidth, imgHeight;
                            int diff;

                          /*  if (companyName.matches("JUBI")) {
                                if (printerLanguage == PrinterLanguage.ZPL) {

                                    Log.d("PrintinZPLPrinterjubii", "ZPL");
                                    Log.d("LogoPrintHere1","Success1");

                                    if(logo!=null){
                                        image = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
                                        printer.printImage(new ZebraImageAndroid(image), -200, 0, 200, 100, false);
                                    }

                                    connection.write("! U1 setvar \"zpl.label_length\" \"90\"".getBytes());

                                    connection.write("! UTILITIES\r\nIN-MILLIMETERS\r\nSETFF 10 2\r\nPRINT\r\n".getBytes());

                                    connection.write("! U1 setvar \"ezpl.media_type\" \"continuous\"".getBytes());

                                } else if (printerLanguage == PrinterLanguage.CPCL) {
                                    Log.d("LogoPrintHere2","Success2");

                                    if(logo!=null){
                                        logo = Bitmap.createScaledBitmap(logo, logo.getWidth(), logo.getHeight(), true);
                                        connection.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                                        printer.printImage(new ZebraImageAndroid(logo), 0, 0, -1, -1, false);
                                    }
                                }
                            } else {
                                Log.d("PrintinZPLPrinter", "ZPL");

                                if (logo!=null){
                                    if (companyName.matches("HEZOMs")) {
                                    } else {
                                        image = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
                                    }
                                    if (width > logo.getWidth()) {
                                        diff = (570 - logo.getWidth()) / 2;
                                        imgWidth = image.getHeight();
                                        imgHeight = image.getHeight();
                                        Log.d("logo.getWidth()", "......" + image.getWidth() + "," + image.getHeight());
                                    } else {
                                        diff = 0;
                                        imgWidth = width;
                                        imgHeight = 250;
                                        Log.d("width", "......" + width);
                                    }

                                    if (printerLanguage == PrinterLanguage.ZPL) {

                                        Log.d("LogoPrintHere3","Success3");
                                        connection
                                                .write(("! U1 setvar \"zpl.label_length\" \""
                                                        + String.valueOf(imgHeight) + "\"")
                                                        .getBytes());
                                        connection
                                                .write("! UTILITIES\r\nIN-MILLIMETERS\r\nSETFF 10 2\r\nPRINT\r\n"
                                                        .getBytes());

                                        connection
                                                .write("! U1 setvar \"ezpl.media_type\" \"continuous\""
                                                        .getBytes());
//
                                        if (image != null){
                                            if (companyName.matches("HEZOM")) {
                                            } else {
                                                printer.printImage(new ZebraImageAndroid(image), -200, 0, 300, 100, false);

                                             *//*   try {
                                                    if (companyName.equalsIgnoreCase("EVER")){
                                                        try {
                                                            matrix.postRotate(360);
                                                            //Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.everlite_qrcode);
                                                            Bitmap bmp = LogOutSetGet.getQrCodeBitmap();
                                                            if(bmp!=null){
                                                                byte[] command = Utils.decodeBitmap(bmp);
                                                                image =  Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                                                                printer.printImage(new ZebraImageAndroid(image), -450, 0, 100, 100, false);
                                                            }else{
                                                                Log.e("Print Photo error", "the file isn't exists");
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            Log.e("PrintTools", "the file isn't exists");
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }*//*
                                            }
                                        }

                                    } else if (printerLanguage == PrinterLanguage.CPCL) {

                                        Log.d("LogoPrintHere4","Success4");
                                        if (logo!=null){
                                            logo = Bitmap.createScaledBitmap(logo, logo.getWidth(), logo.getHeight(), true);

                                            connection.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                                            printer.printImage(new ZebraImageAndroid(logo), diff, 0, imgWidth, imgHeight, false);
                                        }

                                    }
                                }
                            }*/
                        }
                    } else {
                        Log.d("Logo", "No Logo");
                    }

                    File filepath = context.getFileStreamPath(FILE_NAME);
                    printer.sendFileContents(filepath.getAbsolutePath());

                    String signature = Utils.getSignature();
                    Log.d("Print_img", "" + signature);

                    if (!signature.isEmpty() || Double.parseDouble(Utils.getInvoiceOutstandingAmount()) > 0 || Double.parseDouble(Utils.getInvoiceOutstandingAmount()) == 0) {
                        //   Log.w("ImagePrintValue:",signature);
                        Log.w("PrinterLanguageView:", printerLanguage.toString());

                        byte[] encodeByte;
                        byte[] encodeByte1 = Base64.decode(signature, Base64.DEFAULT);
                        String s;
                        try {
                            s = new String(encodeByte1, "UTF-8");
                            encodeByte = Base64.decode(s, Base64.DEFAULT);
                        } catch (Exception e) {
                            encodeByte = encodeByte1;
                        }

                        if (printerLanguage == PrinterLanguage.ZPL) {

                            Log.d("ZPLPrinter", "ZPL");

                            //	if (image!=null){
                            // Bitmap photo = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                            byte[] decodedString = Base64.decode(signature, Base64.DEFAULT);
                            Bitmap photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            image = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
                            printer.printImage(new ZebraImageAndroid(image), -300, 0, 300, 80, false);
                            //	}
                            connection.write("! U1 setvar \"zpl.label_length\" \"90\"".getBytes());
                            connection.write("! UTILITIES\r\nIN-MILLIMETERS\r\nSETFF 10 2\r\nPRINT\r\n".getBytes());
                            connection.write("! U1 setvar \"ezpl.media_type\" \"continuous\"".getBytes());

                        } else if (printerLanguage == PrinterLanguage.CPCL) {
                            Log.d("CPCL_Printer", "Enter");
                            //Bitmap photo = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                            Log.w("InvoiceModePrint:", Utils.getInvoiceMode());
                            if (Utils.getInvoiceMode().equalsIgnoreCase("Invoice")) {
                                Bitmap invoice_status = null;
                                if (Double.parseDouble(Utils.getInvoiceOutstandingAmount()) > 0) {
                                    invoice_status = BitmapFactory.decodeResource(context.getResources(), R.drawable.unpaid_new);
                                } else {
                                    invoice_status = BitmapFactory.decodeResource(context.getResources(), R.drawable.paid_new);
                                }
                                if (Utils.getInvoiceMode().equalsIgnoreCase("Invoice") && Utils.getReceiptMode() != null && !Utils.getReceiptMode().isEmpty() && Utils.getReceiptMode().equals("true")) {
                                    invoice_status = BitmapFactory.decodeResource(context.getResources(), R.drawable.paid_new);
                                }
                                Log.w("InvoiceBalanceAmount:", Utils.getInvoiceOutstandingAmount());
                                if (invoice_status != null) {
                                    Log.d("CPCL_Printer", "CPCL");
                                    invoice_status = Bitmap.createScaledBitmap(invoice_status, 200, 80, true);
                                    // printer.printImage(new ZebraImageAndroid(invoice_status), 0, 0, -100, -1, false);
                                    printer.printImage(new ZebraImageAndroid(invoice_status), 0, 0, 150, 150, false);
                                    Utils.setInvoiceMode("");
                                    Utils.setReceiptMode("");
                                } else {
                                    Log.w("StatusNotPrint", "NotPrint");
                                }
                            }

                            byte[] decodedString = Base64.decode(signature, Base64.DEFAULT);
                            Bitmap photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            //Bitmap photo=BitmapFactory.decodeResource(context.getResources(), R.drawable.paid_jpg);
                            if (photo != null) {
                                Log.d("CPCL_Printer", "CPCL");
                                photo = Bitmap.createScaledBitmap(photo, 300, 80, true);
                                printer.printImage(new ZebraImageAndroid(photo), 0, 0, -1, -1, false);
                                Utils.setSignature("");
                            } else {
                                Log.w("Signature_", "NotPrint");
                            }

                            //Bitmap photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            Bitmap receivedby = BitmapFactory.decodeResource(context.getResources(), R.drawable.received_by);
                            if (receivedby != null) {
                                Log.d("CPCL_Printer", "CPCL");
                                receivedby = Bitmap.createScaledBitmap(receivedby, 300, 50, true);
                                printer.printImage(new ZebraImageAndroid(receivedby), 0, 0, -1, -1, false);
                            } else {
                                Log.w("Signature_", "NotPrint");
                            }

                            connection.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                        } else {
                            Log.w("InvalidPrintCheck:", "Sucecess");
                        }
                    }

                    if (printerLanguage == PrinterLanguage.ZPL) {
                        connection.write("! U1 setvar \"zpl.label_length\" \"100\"".getBytes());
                        connection.write("! U1 setvar \"ezpl.media_type\" \"continuous\"".getBytes());
                    }
                    connection.close();
                    //  helper.showLongToast(R.string.printed_successfully);
                    //  if (context instanceof SettlementAddDenomination){
                    //    Intent intent=new Intent(context,SettlementMainHeader.class);
                    //    context.startActivity(intent);
                    //   ((SettlementAddDenomination) context).finish();
                    // }

                } catch (ZebraPrinterLanguageUnknownException e) {
                    //   helper.showErrorDialog(e.getMessage());
                } catch (ConnectionException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    handler.postDelayed(this, 3000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //  helper.dismissProgressDialog();
                    //disableBluetooth();
                    dialog.dismiss();
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onCompleted();
                            }
                        }
                    });
                }
                //	Looper.loop();
                //	Looper.myLooper().quit();
            }
        }).start();
    }


   /* private void print() {

        ProgressDialog dialog;
        dialog=new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Connecting to Printer...");
        dialog.show();

        if (context instanceof InvoicePrintPreviewActivity){
            InvoicePrintPreviewActivity printPreviewActivity=new InvoicePrintPreviewActivity();
            printPreviewActivity.closeAlert();
        }else if (context instanceof AddInvoiceActivity){
            SummaryFragment.closeAlert();
        }else if (context instanceof CartActivity){
            CartActivity.closeAlert();
        }

        final Handler handler=new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
//                Looper.prepare();
                Connection connection = null;
                connection = new BluetoothConnection(macAddress);
                try {
                    connection.open();
                    printer = ZebraPrinterFactory.getInstance(connection);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.setMessage("Printing Inprogress...");
                        }
                    });

                    PrinterLanguage printerLanguage = printer.getPrinterControlLanguage();

                    Matrix matrix = new Matrix();
                    matrix.postRotate(180);

                    int width = 570, imgWidth=0, imgHeight=0;
                    int diff = 0;
                    Log.w("PrinterLanguagePrint::",printerLanguage.toString());

                    if (printerLanguage == PrinterLanguage.ZPL) {
                        try {
                            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.aadhi_print_logo);
                            if (bmp != null) {
                                //  byte[] command = Utils.decodeBitmap(bmp);
                                image = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                                //  image =  Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), true);
                                if (width > image.getWidth()) {
                                    diff = (570 - image.getWidth()) / 2;
                                    imgWidth = image.getHeight();
                                    imgHeight = image.getHeight();
                                    Log.d("logo.getWidth()", "......" + image.getWidth() + "," + image.getHeight());
                                } else {
                                    diff = 0;
                                    imgWidth = width;
                                    imgHeight = 250;
                                    Log.d("width", "......" + width);
                                }
                                connection.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                                printer.printImage(new ZebraImageAndroid(image), -200, 0, 200, 100, false);
                                connection.write("! U1 setvar \"zpl.label_length\" \"90\"".getBytes());

                                connection
                                        .write("! UTILITIES\r\nIN-MILLIMETERS\r\nSETFF 10 2\r\nPRINT\r\n"
                                                .getBytes());

                                connection
                                        .write("! U1 setvar \"ezpl.media_type\" \"continuous\""
                                                .getBytes());
                                // printer.printImage(new ZebraImageAndroid(image), diff, 0, imgWidth, imgHeight, false);
                            } else {
                                Log.e("Print Photo error", "the file isn't exists");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("PrintTools", "the file isn't exists");
                        }
                    }else if (printerLanguage == PrinterLanguage.CPCL) {
                        Log.d("ImagePrintView","Success");

                        logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.aadhi_print_logo);
                        if (logo!=null){
                            logo = Bitmap.createScaledBitmap(logo, logo.getWidth(), logo.getHeight(), true);
                            connection.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                            printer.printImage(new ZebraImageAndroid(logo), diff, 0, imgWidth, imgHeight, false);
                        }


                        // Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.aadhi_print_logo);
                        // if (bmp != null) {
                        //    image = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), true);
                        //  connection.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                        //  printer.printImage(new ZebraImageAndroid(image), 0, 0, -1, -1, false);
                        //  }
                    }
                  *//*  }else if (printerLanguage == PrinterLanguage.CPCL) {
                        Bitmap bmp  = BitmapFactory.decodeResource(context.getResources(), R.drawable.aadhi_print_logo);
                        if(bmp!=null){
                            image = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), true);
                            connection.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                            printer.printImage(new ZebraImageAndroid(image), 0, 0, -1, -1, false);
                        }
                    }*//*

                    try {
                        File filepath1 = context.getFileStreamPath(FILE_NAME);
                        printer.sendFileContents(filepath1.getAbsolutePath());

                        if (printerLanguage == PrinterLanguage.ZPL) {
                            String img = Utils.getSignature();
                            // Log.d("Print_Signature", "" + img);
                            if (img !=null && !img.isEmpty()) {
                                byte[] encodeByte;
                                byte[] encodeByte1 = Base64.decode(img, Base64.DEFAULT);

                                String s;
                                try {
                                    s = new String(encodeByte1, "UTF-8");
                                    encodeByte = Base64.decode(s, Base64.DEFAULT);
                                } catch (Exception e) {
                                    encodeByte = encodeByte1;
                                }

                                Log.d("PrintinZPLPrinter", "ZPL");
                                Bitmap photo = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                                image = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
                                printer.printImage(new ZebraImageAndroid(image), -300, 0, 300, 80, false);

                                connection.write("! U1 setvar \"zpl.label_length\" \"90\"".getBytes());

                                connection.write("! UTILITIES\r\nIN-MILLIMETERS\r\nSETFF 10 2\r\nPRINT\r\n".getBytes());

                                connection.write("! U1 setvar \"ezpl.media_type\" \"continuous\"".getBytes());
                                Utils.setSignature("");

                            } *//*else if (printerLanguage == PrinterLanguage.CPCL) {

                                Bitmap photo = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

                                if (photo != null) {
                                    Log.d("Print in CPCL Printer", "CPCL");
                                    image = Bitmap.createScaledBitmap(photo, 300, 80, true);
                                    printer.printImage(new ZebraImageAndroid(image), 0, 0, -1, -1, false);
                                }
                                connection.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                                Utils.setSignature("");
                            }*//*
                        }
                        Log.w("Command Send","Success");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } catch (ZebraPrinterLanguageUnknownException e) {
                    dialog.setMessage(e.getMessage());
                } catch (ConnectionException e1) {
                    // TODO Auto-generated catch block
                    if (context instanceof AddInvoiceActivity){
                        SummaryFragment.redirectActivity();
                    }else if (context instanceof CartActivity){
                        ((CartActivity)context).redirectActivity();
                    }
                    handler.postDelayed(this,3000);
                    e1.printStackTrace();
                } finally {
                    dialog.dismiss();
                    // disableBluetooth();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onCompleted();
                            }
                        }
                    });
                }
                //  Looper.loop();
                //  Looper.myLooper().quit();
            }
        }).start();
    }*/

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public void printSalesReturn(int copy, ArrayList<SalesReturnPrintPreviewModel> salesReturnHeader,
                                 ArrayList<SalesReturnPrintPreviewModel.SalesReturnDetails> salesReturnList) throws IOException {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                TscDll.openport(macAddress);

                //String status = TscDll.printerstatus(300);
                int y = 0;
                height = 53;
                //    finalHeight = height + (salesReturnList.size() * list_height) + 20;
                finalHeight = height + (salesReturnList.size() * 9) + invoiveSubTotalHeight + invoiceBottomLine;
                ;

                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.sendcommand("BLINE 0 mm, 0 mm\r\n");//blackmark media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");


                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + "Sales Return" + "\"\n");


                y += LINE_SPACING + 10;
                // Define the Box
                //TscDll.sendcommand("BOX 0,"+y+",570,0,2\n");
                TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,9,9,\"" + "Sales Return No: " + salesReturnHeader.get(0).getSrNo() + "\"\n");
                TscDll.sendcommand("TEXT 400," + y + ",\"Bold.TTF\",0,7,7,\"" + salesReturnHeader.get(0).getSrDate() + "\"\n");
                y += 30;
                if (salesReturnHeader.get(0).getCustomerName().length() <= 45) {
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + "CUST: " + salesReturnHeader.get(0).getCustomerName() + "\"\n\n");
                } else {
                    String firstname = salesReturnHeader.get(0).getCustomerName().substring(0, 42);
                    String secondname = salesReturnHeader.get(0).getCustomerName().substring(42);

                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + "CUST: " + firstname + "\"\n\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                }
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "CODE: " + salesReturnHeader.get(0).getCustomerCode() + "\"\n");
                if (showUserName.equals("true")) {
                    TscDll.sendcommand("TEXT 400," + y + ",\"Poppins.TTF\",0,8,8,\"" + "User: " + username + "\"\n");
                }
                if (salesReturnHeader.get(0).getAddress1() != null &&
                        !salesReturnHeader.get(0).getAddress1().isEmpty()) {
                    y += 30;
                    Log.w("Address1_Value:", salesReturnHeader.get(0).getAddress1());
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" +  "ADDR: " +salesReturnHeader.get(0).getAddress1() + "\"\n");
                }

                if (salesReturnHeader.get(0).getAddress2() != null &&
                        !salesReturnHeader.get(0).getAddress2().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + salesReturnHeader.get(0).getAddress2() + "\"\n\n");
                }

                if (salesReturnHeader.get(0).getAddress3() != null &&
                        !salesReturnHeader.get(0).getAddress3().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + salesReturnHeader.get(0).getAddress3() + "\"\n");
                }
                if (salesReturnHeader.get(0).getAddressstate() != null &&
                        !salesReturnHeader.get(0).getAddressstate().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + salesReturnHeader.get(0).getAddressstate() + "\"\n");
                }
                if (salesReturnHeader.get(0).getAddresssZipcode() != null &&
                        !salesReturnHeader.get(0).getAddresssZipcode().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + salesReturnHeader.get(0).getAddresssZipcode() + "\"\n");
                }


                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SNO" + "\"\n");
                TscDll.sendcommand("TEXT 80," + y + ",\"Poppins.TTF\",0,8,8,\"" + "DESCRIPTION" + "\"\n");
                TscDll.sendcommand("TEXT 280," + y + ",\"Poppins.TTF\",0,8,8,\"" + "QTY" + "\"\n");
                TscDll.sendcommand("TEXT 380," + y + ",\"Poppins.TTF\",0,8,8,\"" + "PRICE" + "\"\n");
                TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,8,8,\"" + "TOTAL" + "\"\n");


                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                int index = 1;

                for (SalesReturnPrintPreviewModel.SalesReturnDetails salesReturnDetails : salesReturnList) {
                    y += 30;
                    TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + index + "\"\n");
                    if (salesReturnDetails.getUomCode() != null && !salesReturnDetails.getUomCode().isEmpty()) {

                        if (salesReturnDetails.getDescription().length() <= 45) {
                            TscDll.sendcommand("TEXT 80," + y + ",\"Poppins.TTF\",0,8,8,\"" + salesReturnDetails.getDescription() + "(" + salesReturnDetails.getUomCode() + ")" + "\"\n\n");
                        } else {
                            String firstname = salesReturnDetails.getDescription().substring(0, 42);
                            String secondname = salesReturnDetails.getDescription().substring(42);

                            TscDll.sendcommand("TEXT 80," + y + ",\"Poppins.TTF\",0,8,8,\"" + firstname + "\"\n\n");
                            y += 30;
                            TscDll.sendcommand("TEXT 80," + y + ",\"Poppins.TTF\",0,8,8,\"" + secondname + "(" + salesReturnDetails.getUomCode() + ")" + "\"\n\n");
                        }
                    } else {
                        if (salesReturnDetails.getDescription().length() <= 45) {
                            TscDll.sendcommand("TEXT 80," + y + ",\"Poppins.TTF\",0,8,8,\"" + salesReturnDetails.getDescription() + "\"\n\n");
                        } else {
                            String firstname = salesReturnDetails.getDescription().substring(0, 42);
                            String secondname = salesReturnDetails.getDescription().substring(42);

                            TscDll.sendcommand("TEXT 80," + y + ",\"Poppins.TTF\",0,8,8,\"" + firstname + "\"\n\n");
                            y += 30;
                            TscDll.sendcommand("TEXT 80," + y + ",\"Poppins.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                        }
                    }
                    y += 30;
                    TscDll.sendcommand("TEXT 290," + y + ",\"Bold.TTF\",0,8,8,\"" + (int) Double.parseDouble(salesReturnDetails.getNetqty()) + "\"\n");
                    TscDll.sendcommand("TEXT 390," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(salesReturnDetails.getPrice())) + "\"\n");
                    TscDll.sendcommand("TEXT 490," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(salesReturnDetails.getTotal())) + "\"\n");
                    index++;
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,9,9,\"" + "SUB TOTAL:$" + "\"\n");
                TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,9,9,\"" + twoDecimalPoint(Double.parseDouble(salesReturnHeader.get(0).getSubTotal())) + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,9,9,\"" + "GST(" + salesReturnHeader.get(0).getTaxType() + ":" + (int) Double.parseDouble(salesReturnHeader.get(0).getTaxValue()) + " % ):$" + "\"\n");
                TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,9,9,\"" + twoDecimalPoint(Double.parseDouble(salesReturnHeader.get(0).getTax())) + "\"\n");
       /* if(!salesReturnHeader.get(0).getItemDisc().isEmpty() && salesReturnHeader.get(0).getItemDisc() != null){
            y += LINE_SPACING;
            TscDll.sendcommand("TEXT 200,"+y+",\"Poppins.TTF\",0,9,9,\""+"ITEM DISCOUNT"+"\"\n");
            TscDll.sendcommand("TEXT 480,"+y+",\"Poppins.TTF\",0,9,9,\""+twoDecimalPoint(Double.parseDouble(salesReturnHeader.get(0).getItemDisc()))+"\"\n");
        }
        if(!salesReturnHeader.get(0).getBillDisc().isEmpty() && salesReturnHeader.get(0).getBillDisc() != null){
            y += LINE_SPACING;
            TscDll.sendcommand("TEXT 200,"+y+",\"Poppins.TTF\",0,9,9,\""+"BILL DISCOUNT"+"\"\n");
            TscDll.sendcommand("TEXT 480,"+y+",\"Poppins.TTF\",0,9,9,\""+twoDecimalPoint(Double.parseDouble(salesReturnHeader.get(0).getBillDisc()))+"\"\n");
        }*/

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,9,9,\"" + "GRAND TOTAL:$" + "\"\n");
                TscDll.sendcommand("TEXT 480," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(Double.parseDouble(salesReturnHeader.get(0).getNetTotal())) + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Customer Signature & Company Stamp" + "\"\n");

                if (showSignature.equals("true")) {

                    Log.d("cg_signature", "show_sign");
                    try {
                        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
                            y += LINE_SPACING;
                            String filePath = Constants.getSignatureFolderPath(context);
                            String fileName = "Signature.jpg";
                            File mFile = new File(filePath, fileName);
                            if (mFile.exists()) {
                                TscDll.sendpicture_resize(0, y, mFile.getAbsolutePath(),
                                        400, 80);
                            }
                            Log.d("cg_signature_data", Utils.getSignature());
//                            TscDll.sendbitmap_resize(0,y, ImageUtil.convertBase64toBitmap(Utils.getSignature()),
//                                    400,80);
                        }
                    } catch (Exception e) {
                        Log.d("cg_signature_err", e.getLocalizedMessage());
                    }
                }


                y += 150;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------------------------------------------------" + "\"\n");

                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "RECEIVED ABOVE GOODS IN GOOD ORDER AND CONDITION" + "\"\n");

                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------------------------------------------------" + "\"\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);

            }
        }, 100);
    }



   /* public void printSalesReturn(int copy, ArrayList<SalesReturnPrintPreviewModel> salesReturnHeader, ArrayList<SalesReturnPrintPreviewModel.SalesReturnDetails> salesReturnList) throws IOException {
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);

            for (int n = 0; n < copy; n++) {
                int y = 0;
                StringBuilder temp = new StringBuilder();
                y = printTitle(200, y, "SALES RETURN", temp);
                y = printCompanyDetails(y, temp);

                String cpclConfigLabel = temp.toString();

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "SR No")
                        + text(150, y, " : ") + text(180, y, salesReturnHeader.get(0).getSrNo());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "SR Date")
                        + text(150, y, " : ") + text(180, y, salesReturnHeader.get(0).getSrDate());


                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Customer Code")
                        + text(200, y, " : ") + text(220, y, salesReturnHeader.get(0).getCustomerCode());

                if (!salesReturnHeader.get(0).getCustomerName().isEmpty() && !salesReturnHeader.get(0).getCustomerName().equals("null")){
                    cpclConfigLabel += (text(LEFT_MARGIN,
                            y += LINE_SPACING, "Customer Name"));
                    cpclConfigLabel += (text(200, y, " : "));

                    cpclConfigLabel += (text(
                            220,
                            y,
                            (salesReturnHeader.get(0).getCustomerName().length() > 22) ? salesReturnHeader.get(0).getCustomerName()
                                    .substring(0, 21) : salesReturnHeader.get(0).getCustomerName()));
                    if (salesReturnHeader.get(0).getCustomerName().length() > 22) {
                        String customername = salesReturnHeader.get(0).getCustomerName().substring(21);
                        cpclConfigLabel += (text(
                                220,
                                y += LINE_SPACING,
                                (customername.length() > 22) ? customername
                                        .substring(0, 21) : customername));
                    }

                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                // cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Code");
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Description");
                cpclConfigLabel += text(330, y, "Qty");
                cpclConfigLabel += text(400, y, "Price");
                cpclConfigLabel += text(500, y, "Total");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (salesReturnList.size() > 0) {
                    int index = 1;
                    for (SalesReturnPrintPreviewModel.SalesReturnDetails salesreturn : salesReturnList) {

                        //   cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, String.valueOf(salesreturn.getProductCode()));  // Display the Serial Number

                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, (salesreturn.getDescription().length() > 25)
                                ? salesreturn.getDescription().substring(0, 24)
                                : salesreturn.getDescription());                      // Display the product Name

                        int count = 0;
                        String name = salesreturn.getDescription();
                        int len = name.length();
                        if (len > 25) {
                            int get_len = name.substring(24, len).length();
                            String remark = name.substring(24, len);
                            Log.d("BalanceString", "-->" + len + " " + get_len + " " + remark);
                            String names;

                            for (int j = 0; j < get_len; j = j + 25) {
                                count = count + 25;
                                if (count > get_len) {
                                    names = remark.substring(j, get_len);
                                    cpclConfigLabel += text(
                                            LEFT_MARGIN,
                                            y += LINE_SPACING, names);
                                    Log.d("BalancesDescription", "-->" + names);

                                } else {
                                    names = remark.substring(j, j + 25);
                                    cpclConfigLabel += text(
                                            LEFT_MARGIN,
                                            y += LINE_SPACING, names);
                                    Log.d("BalancesValues", "-->" + names);

                                }
                            }
                        }

                        cpclConfigLabel += text(330, y += LINE_SPACING, (int)Double.parseDouble(salesreturn.getNetqty())+"");

                        cpclConfigLabel += text(400, y, twoDecimalPoint(Double.parseDouble(salesreturn.getPrice())));

                        cpclConfigLabel += text(500, y, twoDecimalPoint(Double.parseDouble(salesreturn.getTotal())));
                        index++;
                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(310, y += LINE_SPACING, "Sub Total")
                        + text(450, y, " : ")
                        + text(486, y, twoDecimalPoint(Double.parseDouble(salesReturnHeader.get(0).getSubTotal())));

                cpclConfigLabel += text(310, y += LINE_SPACING, "GST")
                        + text(450, y, " : ")
                        + text(486, y, twoDecimalPoint(Double.parseDouble(salesReturnHeader.get(0).getTax())));

                cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
                        + text(450, y, " : ")
                        + text(486, y, twoDecimalPoint(Double.parseDouble(salesReturnHeader.get(0).getNetTotal())));

                cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1" + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

                os.write(cpclConfigLabel.getBytes(StandardCharsets.ISO_8859_1));
                os.flush();
            }
            os.close();

        }catch (Exception ex){
            Log.w("Error_in_print",ex.getMessage());
        }
    }*/

    // Print Receipt Details
/*
    public void printReceipts(int copy, ArrayList<ReceiptPrintPreviewModel> receiptHeaderDetails, ArrayList<ReceiptPrintPreviewModel.ReceiptsDetails> receiptList) throws IOException {
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            for (int n = 0; n < copy; n++) {

                int y = 0;
                double net_discount=0.00;

                StringBuilder temp = new StringBuilder();
                y = printTitle(200, y, "RECEIPT", temp);
                y = printCompanyDetails(y, temp);

                String cpclConfigLabel = temp.toString();

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Receipt No")
                        + text(210, y, " : ") + text(230, y, receiptHeaderDetails.get(0).getReceiptNumber());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Receipt Date")
                        + text(210, y, " : ") + text(230, y, receiptHeaderDetails.get(0).getReceiptDate());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Customer Code")
                        + text(210, y, " : ") + text(230, y, receiptHeaderDetails.get(0).getCustomerCode());

                cpclConfigLabel += (text(LEFT_MARGIN,
                        y += LINE_SPACING, "Customer Name"));
                cpclConfigLabel += (text(210, y, " : "));
                Log.d("CustnameeeeAddres2", receiptHeaderDetails.get(0).getCustomerName());
                cpclConfigLabel += (text(
                        230,
                        y,
                        (receiptHeaderDetails.get(0).getCustomerName().length() > 22) ? receiptHeaderDetails.get(0).getCustomerName()
                                .substring(0, 21) : receiptHeaderDetails.get(0).getCustomerName()));
                if (receiptHeaderDetails.get(0).getCustomerName().length() > 22) {
                    String customername = receiptHeaderDetails.get(0).getCustomerName().substring(21);
                    cpclConfigLabel += (text(
                            230,
                            y += LINE_SPACING,
                            (customername.length() > 22) ? customername
                                    .substring(0, 21) : customername));
                }

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Pay Mode")
                        + text(210, y, " : ") + text(230, y, receiptHeaderDetails.get(0).getPayMode());

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
                cpclConfigLabel += text(70, y, "Invoice No");
                cpclConfigLabel += text(280, y, "Invoice Date");
                cpclConfigLabel += text(450, y, "Amount");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (receiptList.size() > 0) {
                    int index = 1;
                    for (ReceiptPrintPreviewModel.ReceiptsDetails receipts : receiptList) {

                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, String.valueOf(index));  // Display the Serial Number

                        cpclConfigLabel += text(70, y, receipts.getInvoiceNumber());  // Display the Qty of Invoice

                        cpclConfigLabel += text(280, y, receipts.getInvoiceDate());  // Display the Qty of Invoice

                        cpclConfigLabel += text(450, y, twoDecimalPoint(Double.parseDouble(receipts.getAmount()))); // Display the Sub total of Particular product

                        net_discount+=Double.parseDouble(receipts.getDiscountAmount());

                        index++;
                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (receiptHeaderDetails.get(0).getPayMode()!=null && !receiptHeaderDetails.get(0).getPayMode().isEmpty()){
                    if (receiptHeaderDetails.get(0).getPayMode().equalsIgnoreCase("Cheque")){
                        cpclConfigLabel += text(LEFT_MARGIN,  y += LINE_SPACING, "BankCode");
                        cpclConfigLabel += text(240, y, "Cheque No");
                        cpclConfigLabel += text(420, y, "Cheque Date");

                        cpclConfigLabel += text(LEFT_MARGIN, y+= LINE_SPACING, receiptHeaderDetails.get(0).getBankCode());  // Display the Qty of Invoice

                        cpclConfigLabel += text(240, y, receiptHeaderDetails.get(0).getChequeNo());  // Display the Qty of Invoice

                        cpclConfigLabel += text(420, y, receiptHeaderDetails.get(0).getChequeDate());
                        cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title
                    }else if (receiptHeaderDetails.get(0).getPayMode().equalsIgnoreCase("BankTransfer")){
                        cpclConfigLabel += text(LEFT_MARGIN,  y += LINE_SPACING, "Mode");
                        cpclConfigLabel += text(350, y, "Tran Date");

                        cpclConfigLabel += text(LEFT_MARGIN, y+= LINE_SPACING, receiptHeaderDetails.get(0).getPaymentType());  // Display the Qty of Invoice

                        cpclConfigLabel += text(350, y, receiptHeaderDetails.get(0).getBankTransferDate());  // Display the Qty of Invoice

                        cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title
                    }
                }

                if (net_discount > 0.00){
                    cpclConfigLabel += text(280, y += LINE_SPACING, "Credit Amount")
                            + text(430, y, " : ")
                            + text(450, y, twoDecimalPoint(net_discount));
                }
                cpclConfigLabel += text(280, y += LINE_SPACING, "Total Amount")
                        + text(430, y, " : ")
                        + text(450, y, twoDecimalPoint(Double.parseDouble(receiptHeaderDetails.get(0).getTotalAmount())));

                cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1" + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

                os.write(cpclConfigLabel.getBytes("iso-8859-1"));
                os.flush();
            }
            os.close();

        }catch (Exception ex){
            Log.e("Err_printing_Receipts:",ex.getMessage());
        }
    }
*/

    public void printReceipts(int copy, ArrayList<ReceiptPrintPreviewModel> receiptHeaderDetails,
                              ArrayList<ReceiptPrintPreviewModel.ReceiptsDetails> receiptList) {
        if (company_code.equalsIgnoreCase("Trans Orient Singapore Pte Ltd")) {
            printReceiptstransOrient(copy,receiptHeaderDetails,receiptList);
        }
        else {
            printReceiptsOther(copy,receiptHeaderDetails,receiptList);
        }
    }

    public void printReceiptstransOrient(int copy, ArrayList<ReceiptPrintPreviewModel> receiptHeaderDetails,
                              ArrayList<ReceiptPrintPreviewModel.ReceiptsDetails> receiptList){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);
                list_height = 15;
                int y = 0;
                height = 65;
                finalHeight = height + (receiptList.size() * 11) + invoiveSubTotalHeight + invoiceBottomLine;
                ;

//                finalHeight = height + 10 + (receiptList.size() * list_height);
                //finalHeight = getPrintSize(finalHeight, "false", "false", "false", "false", "false", "false");

                //  finalHeight=height+(receiptList.size() * list_height)+20;
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                StringBuilder temp = new StringBuilder();
                if (company_name.length() > 38) {
                    temp.append((company_name.length() > 38) ? company_name.substring(0, 37) : company_name);
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                    String companyname = company_name.substring(37);
                    temp.append((companyname.length() > 37) ? companyname.substring(0, 36) : companyname);
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + temp.toString() + "\"\n\n");
                } else {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                }

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,10,10,\"" + "Receipt" + "\"\n");


                y += LINE_SPACING + 10;
                // Define the Box
                //TscDll.sendcommand("BOX 0,"+y+",570,0,2\n");
                TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,9,9,\"" + "Receipt No: " + receiptHeaderDetails.get(0).getReceiptNumber() + "\"\n");
                TscDll.sendcommand("TEXT 360," + y + ",\"Bold.TTF\",0,7,7,\"" + receiptHeaderDetails.get(0).getReceiptDate() + "\"\n");
                y += 30;
                if (receiptHeaderDetails.get(0).getCustomerName().length() <= 38) {
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + "CUST: " + receiptHeaderDetails.get(0).getCustomerName() + "\"\n\n");
                } else {
                    String firstname = receiptHeaderDetails.get(0).getCustomerName().substring(0, 35);
                    String secondname = receiptHeaderDetails.get(0).getCustomerName().substring(35);

                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + "CUST: " + firstname + "\"\n\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                }
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "CODE: " + receiptHeaderDetails.get(0).getCustomerCode() + "\"\n");
                TscDll.sendcommand("TEXT 360," + y + ",\"Poppins.TTF\",0,7,7,\"" + "Pay Mode: " + receiptHeaderDetails.get(0).getPayMode() + "\"\n");

                if (showUserName.equals("true")) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "User: " + username + "\"\n");
                }

                if (receiptHeaderDetails.get(0).getAddress1() != null &&
                        !receiptHeaderDetails.get(0).getAddress1().isEmpty()) {
                    y += 30;
                    Log.w("Address1_Value:", receiptHeaderDetails.get(0).getAddress1());
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" +  "ADDR: " +receiptHeaderDetails.get(0).getAddress1() + "\"\n");
                }

                if (receiptHeaderDetails.get(0).getAddress2() != null &&
                        !receiptHeaderDetails.get(0).getAddress2().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getAddress2() + "\"\n\n");
                }

                if (receiptHeaderDetails.get(0).getAddress3() != null &&
                        !receiptHeaderDetails.get(0).getAddress3().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getAddress3() + "\"\n");
                }
                if (receiptHeaderDetails.get(0).getAddressstate() != null &&
                        !receiptHeaderDetails.get(0).getAddressstate().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getAddressstate() + "\"\n");
                }
                if (receiptHeaderDetails.get(0).getAddresssZipcode() != null &&
                        !receiptHeaderDetails.get(0).getAddresssZipcode().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getAddresssZipcode() + "\"\n");
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SNo" + "\"\n");
                TscDll.sendcommand("TEXT 50," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Inv No" + "\"\n");
                TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Inv Date" + "\"\n");
                TscDll.sendcommand("TEXT 370," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Amt" + "\"\n");
                TscDll.sendcommand("TEXT 460," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Disc" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                int index = 1;
                double netBal = 0.0 ;
                double netTotal = 0.0 ;
                for (ReceiptPrintPreviewModel.ReceiptsDetails receiptsDetails : receiptList) {
                    y += 30;
                    TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + index + "\"\n");

                    TscDll.sendcommand("TEXT 50," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptsDetails.getInvoiceNumber() + "\"\n");
                    TscDll.sendcommand("TEXT 210," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptsDetails.getInvoiceDate() + "\"\n");
                    TscDll.sendcommand("TEXT 380," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptsDetails.getAmount())) + "\"\n");
                    TscDll.sendcommand("TEXT 460," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptsDetails.getDiscountAmount())) + "\"\n");

                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "Paid Amt: " + receiptsDetails.getAmount() + "\"\n");
                    TscDll.sendcommand("TEXT 370," + y + ",\"Poppins.TTF\",0,7,7,\"" + "Balance Amt: " + receiptsDetails.getBalanceAmount() + "\"\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "Credit Amt: " + receiptsDetails.getCreditAmount() + "\"\n");

                    netBal += Double.parseDouble(receiptsDetails.getBalanceAmount());
                    netTotal += Double.parseDouble(receiptsDetails.getAmount());

                    index++;
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                if (receiptHeaderDetails.get(0).getPayMode().equalsIgnoreCase("Cheque")) {
                    y += 20;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Bank Name" + "\"\n");
                    TscDll.sendcommand("TEXT 230," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Cheque No" + "\"\n");
                    TscDll.sendcommand("TEXT 380," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Cheque Date" + "\"\n");

                    y += 40;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getBankName() + "\"\n");
                    TscDll.sendcommand("TEXT 230," + y + ",\"Bold.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getChequeNo() + "\"\n");
                    TscDll.sendcommand("TEXT 380," + y + ",\"Bold.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getChequeDate() + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                }
                y += 20;
//        TscDll.sendcommand("TEXT 200,"+y+",\"Bold.TTF\",0,9,9,\""+"GRAND TOTAL:$"+"\"\n");
//        TscDll.sendcommand("TEXT 450,"+y+",\"Bold.TTF\",0,9,9,\""+twoDecimalPoint(Double.parseDouble(receiptHeaderDetails.get(0).getTotalAmount()))+"\"\n");
                TscDll.sendcommand("TEXT 180," + y + ",\"Bold.TTF\",0,9,9,\"" + "NET PAID AMT :$" + "\"\n");
                TscDll.sendcommand("TEXT 410," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(netTotal) + "\"\n");
                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 180," + y + ",\"Bold.TTF\",0,9,9,\"" + "NET BALANCE :$" + "\"\n");
                TscDll.sendcommand("TEXT 410," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(netBal)+ "\"\n");
//                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(Double.parseDouble(receiptHeaderDetails.get(0).getBalanceAmount() != null ?
//                        receiptHeaderDetails.get(0).getBalanceAmount() : "0.0")) + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Customer Signature & Company Stamp" + "\"\n");

                if (showSignature.equals("true")) {

                    Log.d("cg_signature", "show_sign");
                    try {
                        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
                            y += LINE_SPACING;
                            String filePath = Constants.getSignatureFolderPath(context);
                            String fileName = "Signature.jpg";
                            File mFile = new File(filePath, fileName);
                            if (mFile.exists()) {
                                TscDll.sendpicture_resize(0, y, mFile.getAbsolutePath(),
                                        400, 80);
                            }
                            Log.d("cg_signature_data", Utils.getSignature());
//                            TscDll.sendbitmap_resize(0,y, ImageUtil.convertBase64toBitmap(Utils.getSignature()),
//                                    400,80);
                        }
                    } catch (Exception e) {
                        Log.d("cg_signature_err", e.getLocalizedMessage());
                    }
                }


                y += 150;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------------------------------------------------" + "\"\n");

                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "RECEIVED ABOVE GOODS IN GOOD ORDER AND CONDITION" + "\"\n");

                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------------------------------------------------" + "\"\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);

            }
        }, 100);

    }
    public void printReceiptsOther(int copy, ArrayList<ReceiptPrintPreviewModel> receiptHeaderDetails,
                              ArrayList<ReceiptPrintPreviewModel.ReceiptsDetails> receiptList){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);
                list_height = 15;
                int y = 0;
                height = 65;
                finalHeight = height + (receiptList.size() * 11) + invoiveSubTotalHeight + invoiceBottomLine;
                ;

//                finalHeight = height + 10 + (receiptList.size() * list_height);
                //finalHeight = getPrintSize(finalHeight, "false", "false", "false", "false", "false", "false");

                //  finalHeight=height+(receiptList.size() * list_height)+20;
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                StringBuilder temp = new StringBuilder();
                if (company_name.length() > 38) {
                    temp.append((company_name.length() > 38) ? company_name.substring(0, 37) : company_name);
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                    String companyname = company_name.substring(37);
                    temp.append((companyname.length() > 37) ? companyname.substring(0, 36) : companyname);
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + temp.toString() + "\"\n\n");
                } else {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                }

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,10,10,\"" + "Receipt" + "\"\n");


                y += LINE_SPACING + 10;
                // Define the Box
                //TscDll.sendcommand("BOX 0,"+y+",570,0,2\n");
                TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,9,9,\"" + "Receipt No: " + receiptHeaderDetails.get(0).getReceiptNumber() + "\"\n");
                TscDll.sendcommand("TEXT 400," + y + ",\"Bold.TTF\",0,7,7,\"" + receiptHeaderDetails.get(0).getReceiptDate() + "\"\n");
                y += 30;
                if (receiptHeaderDetails.get(0).getCustomerName().length() <= 45) {
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + "CUST: " + receiptHeaderDetails.get(0).getCustomerName() + "\"\n\n");
                } else {
                    String firstname = receiptHeaderDetails.get(0).getCustomerName().substring(0, 42);
                    String secondname = receiptHeaderDetails.get(0).getCustomerName().substring(42);

                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + "CUST: " + firstname + "\"\n\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                }
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "CODE: " + receiptHeaderDetails.get(0).getCustomerCode() + "\"\n");
                TscDll.sendcommand("TEXT 400," + y + ",\"Poppins.TTF\",0,7,7,\"" + "Pay Mode: " + receiptHeaderDetails.get(0).getPayMode() + "\"\n");

                if (showUserName.equals("true")) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "User: " + username + "\"\n");
                }

                if (receiptHeaderDetails.get(0).getAddress1() != null &&
                        !receiptHeaderDetails.get(0).getAddress1().isEmpty()) {
                    y += 30;
                    Log.w("Address1_Value:", receiptHeaderDetails.get(0).getAddress1());
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" +  "ADDR: " +receiptHeaderDetails.get(0).getAddress1() + "\"\n");
                }

                if (receiptHeaderDetails.get(0).getAddress2() != null &&
                        !receiptHeaderDetails.get(0).getAddress2().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getAddress2() + "\"\n\n");
                }

                if (receiptHeaderDetails.get(0).getAddress3() != null &&
                        !receiptHeaderDetails.get(0).getAddress3().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getAddress3() + "\"\n");
                }
                if (receiptHeaderDetails.get(0).getAddressstate() != null &&
                        !receiptHeaderDetails.get(0).getAddressstate().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getAddressstate() + "\"\n");
                }
                if (receiptHeaderDetails.get(0).getAddresssZipcode() != null &&
                        !receiptHeaderDetails.get(0).getAddresssZipcode().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getAddresssZipcode() + "\"\n");
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SNo" + "\"\n");
                TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Inv No" + "\"\n");
                TscDll.sendcommand("TEXT 220," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Inv Date" + "\"\n");
                TscDll.sendcommand("TEXT 390," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Amt" + "\"\n");
                TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Disc" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                int index = 1;
                double netBal = 0.0 ;
                double netTotal = 0.0 ;
                for (ReceiptPrintPreviewModel.ReceiptsDetails receiptsDetails : receiptList) {
                    y += 30;
                    TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + index + "\"\n");

                    TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptsDetails.getInvoiceNumber() + "\"\n");
                    TscDll.sendcommand("TEXT 230," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptsDetails.getInvoiceDate() + "\"\n");
                    TscDll.sendcommand("TEXT 400," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptsDetails.getAmount())) + "\"\n");
                    TscDll.sendcommand("TEXT 490," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptsDetails.getDiscountAmount())) + "\"\n");

                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "Paid Amt: " + receiptsDetails.getAmount() + "\"\n");
                    TscDll.sendcommand("TEXT 400," + y + ",\"Poppins.TTF\",0,7,7,\"" + "Balance Amt: " + receiptsDetails.getBalanceAmount() + "\"\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "Credit Amt: " + receiptsDetails.getCreditAmount() + "\"\n");

                    netBal += Double.parseDouble(receiptsDetails.getBalanceAmount());
                    netTotal += Double.parseDouble(receiptsDetails.getAmount());

                    index++;
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                if (receiptHeaderDetails.get(0).getPayMode().equalsIgnoreCase("Cheque")) {
                    y += 20;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Bank Name" + "\"\n");
                    TscDll.sendcommand("TEXT 250," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Cheque No" + "\"\n");
                    TscDll.sendcommand("TEXT 420," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Cheque Date" + "\"\n");

                    y += 40;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getBankName() + "\"\n");
                    TscDll.sendcommand("TEXT 250," + y + ",\"Bold.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getChequeNo() + "\"\n");
                    TscDll.sendcommand("TEXT 420," + y + ",\"Bold.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getChequeDate() + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                }
                y += 20;
//        TscDll.sendcommand("TEXT 200,"+y+",\"Bold.TTF\",0,9,9,\""+"GRAND TOTAL:$"+"\"\n");
//        TscDll.sendcommand("TEXT 450,"+y+",\"Bold.TTF\",0,9,9,\""+twoDecimalPoint(Double.parseDouble(receiptHeaderDetails.get(0).getTotalAmount()))+"\"\n");
                TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,9,9,\"" + "NET PAID AMT :$" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(netTotal) + "\"\n");
                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,9,9,\"" + "NET BALANCE :$" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(netBal)+ "\"\n");
//                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(Double.parseDouble(receiptHeaderDetails.get(0).getBalanceAmount() != null ?
//                        receiptHeaderDetails.get(0).getBalanceAmount() : "0.0")) + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Customer Signature & Company Stamp" + "\"\n");

                if (showSignature.equals("true")) {

                    Log.d("cg_signature", "show_sign");
                    try {
                        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
                            y += LINE_SPACING;
                            String filePath = Constants.getSignatureFolderPath(context);
                            String fileName = "Signature.jpg";
                            File mFile = new File(filePath, fileName);
                            if (mFile.exists()) {
                                TscDll.sendpicture_resize(0, y, mFile.getAbsolutePath(),
                                        400, 80);
                            }
                            Log.d("cg_signature_data", Utils.getSignature());
//                            TscDll.sendbitmap_resize(0,y, ImageUtil.convertBase64toBitmap(Utils.getSignature()),
//                                    400,80);
                        }
                    } catch (Exception e) {
                        Log.d("cg_signature_err", e.getLocalizedMessage());
                    }
                }


                y += 150;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------------------------------------------------" + "\"\n");

                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "RECEIVED ABOVE GOODS IN GOOD ORDER AND CONDITION" + "\"\n");

                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------------------------------------------------" + "\"\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);

            }
        }, 100);

    }
    public void printReceiptsCopy_8_7(int copy, ArrayList<ReceiptPrintPreviewModel> receiptHeaderDetails, ArrayList<ReceiptPrintPreviewModel.ReceiptsDetails> receiptList) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);
                list_height = 15;
                int y = 0;
                height = 65;
                finalHeight = height + (receiptList.size() * 11) + invoiveSubTotalHeight + invoiceBottomLine;
                ;

//                finalHeight = height + 10 + (receiptList.size() * list_height);
                //finalHeight = getPrintSize(finalHeight, "false", "false", "false", "false", "false", "false");

                //  finalHeight=height+(receiptList.size() * list_height)+20;
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                StringBuilder temp = new StringBuilder();
                if (company_name.length() > 38) {
                    temp.append((company_name.length() > 38) ? company_name.substring(0, 37) : company_name);
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                    String companyname = company_name.substring(37);
                    temp.append((companyname.length() > 37) ? companyname.substring(0, 36) : companyname);
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + temp.toString() + "\"\n\n");
                } else {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                }

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,10,10,\"" + "Receipt" + "\"\n");


                y += LINE_SPACING + 10;
                // Define the Box
                //TscDll.sendcommand("BOX 0,"+y+",570,0,2\n");
                TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,9,9,\"" + "Receipt No: " + receiptHeaderDetails.get(0).getReceiptNumber() + "\"\n");
                TscDll.sendcommand("TEXT 400," + y + ",\"Bold.TTF\",0,7,7,\"" + receiptHeaderDetails.get(0).getReceiptDate() + "\"\n");
                y += 30;
                if (receiptHeaderDetails.get(0).getCustomerName().length() <= 45) {
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + "CUST: " + receiptHeaderDetails.get(0).getCustomerName() + "\"\n\n");
                } else {
                    String firstname = receiptHeaderDetails.get(0).getCustomerName().substring(0, 42);
                    String secondname = receiptHeaderDetails.get(0).getCustomerName().substring(42);

                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + "CUST: " + firstname + "\"\n\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                }
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "CODE: " + receiptHeaderDetails.get(0).getCustomerCode() + "\"\n");
                TscDll.sendcommand("TEXT 400," + y + ",\"Poppins.TTF\",0,7,7,\"" + "Pay Mode: " + receiptHeaderDetails.get(0).getPayMode() + "\"\n");

                if (showUserName.equals("true")) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "User: " + username + "\"\n");
                }

                if (receiptHeaderDetails.get(0).getAddress1() != null &&
                        !receiptHeaderDetails.get(0).getAddress1().isEmpty()) {
                    y += 30;
                    Log.w("Address1_Value:", receiptHeaderDetails.get(0).getAddress1());
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" +  "ADDR: " +receiptHeaderDetails.get(0).getAddress1() + "\"\n");
                }

                if (receiptHeaderDetails.get(0).getAddress2() != null &&
                        !receiptHeaderDetails.get(0).getAddress2().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getAddress2() + "\"\n\n");
                }

                if (receiptHeaderDetails.get(0).getAddress3() != null &&
                        !receiptHeaderDetails.get(0).getAddress3().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getAddress3() + "\"\n");
                }
                if (receiptHeaderDetails.get(0).getAddressstate() != null &&
                        !receiptHeaderDetails.get(0).getAddressstate().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getAddressstate() + "\"\n");
                }
                if (receiptHeaderDetails.get(0).getAddresssZipcode() != null &&
                        !receiptHeaderDetails.get(0).getAddresssZipcode().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getAddresssZipcode() + "\"\n");
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SNo" + "\"\n");
                TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Inv No" + "\"\n");
                TscDll.sendcommand("TEXT 220," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Inv Date" + "\"\n");
                TscDll.sendcommand("TEXT 390," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Amt" + "\"\n");
                TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Disc" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                int index = 1;
                double netBal = 0.0 ;
                double netTotal = 0.0 ;
                for (ReceiptPrintPreviewModel.ReceiptsDetails receiptsDetails : receiptList) {
                    y += 30;
                    TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + index + "\"\n");

                    TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptsDetails.getInvoiceNumber() + "\"\n");
                    TscDll.sendcommand("TEXT 230," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptsDetails.getInvoiceDate() + "\"\n");
                    TscDll.sendcommand("TEXT 400," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptsDetails.getAmount())) + "\"\n");
                    TscDll.sendcommand("TEXT 490," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptsDetails.getDiscountAmount())) + "\"\n");

                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "Paid Amt: " + receiptsDetails.getAmount() + "\"\n");
                    TscDll.sendcommand("TEXT 400," + y + ",\"Poppins.TTF\",0,7,7,\"" + "Balance Amt: " + receiptsDetails.getBalanceAmount() + "\"\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "Credit Amt: " + receiptsDetails.getCreditAmount() + "\"\n");

                    netBal += Double.parseDouble(receiptsDetails.getBalanceAmount());
                    netTotal += Double.parseDouble(receiptsDetails.getAmount());

                    index++;
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                if (receiptHeaderDetails.get(0).getPayMode().equalsIgnoreCase("Cheque")) {
                    y += 20;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Bank Name" + "\"\n");
                    TscDll.sendcommand("TEXT 250," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Cheque No" + "\"\n");
                    TscDll.sendcommand("TEXT 420," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Cheque Date" + "\"\n");

                    y += 40;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getBankName() + "\"\n");
                    TscDll.sendcommand("TEXT 250," + y + ",\"Bold.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getChequeNo() + "\"\n");
                    TscDll.sendcommand("TEXT 420," + y + ",\"Bold.TTF\",0,8,8,\"" + receiptHeaderDetails.get(0).getChequeDate() + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                }
                y += 20;
//        TscDll.sendcommand("TEXT 200,"+y+",\"Bold.TTF\",0,9,9,\""+"GRAND TOTAL:$"+"\"\n");
//        TscDll.sendcommand("TEXT 450,"+y+",\"Bold.TTF\",0,9,9,\""+twoDecimalPoint(Double.parseDouble(receiptHeaderDetails.get(0).getTotalAmount()))+"\"\n");
                TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,9,9,\"" + "NET PAID AMT :$" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(netTotal) + "\"\n");
                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,9,9,\"" + "NET BALANCE :$" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(netBal)+ "\"\n");
//                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(Double.parseDouble(receiptHeaderDetails.get(0).getBalanceAmount() != null ?
//                        receiptHeaderDetails.get(0).getBalanceAmount() : "0.0")) + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Customer Signature & Company Stamp" + "\"\n");

                if (showSignature.equals("true")) {

                    Log.d("cg_signature", "show_sign");
                    try {
                        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
                            y += LINE_SPACING;
                            String filePath = Constants.getSignatureFolderPath(context);
                            String fileName = "Signature.jpg";
                            File mFile = new File(filePath, fileName);
                            if (mFile.exists()) {
                                TscDll.sendpicture_resize(0, y, mFile.getAbsolutePath(),
                                        400, 80);
                            }
                            Log.d("cg_signature_data", Utils.getSignature());
//                            TscDll.sendbitmap_resize(0,y, ImageUtil.convertBase64toBitmap(Utils.getSignature()),
//                                    400,80);
                        }
                    } catch (Exception e) {
                        Log.d("cg_signature_err", e.getLocalizedMessage());
                    }
                }


                y += 150;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------------------------------------------------" + "\"\n");

                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "RECEIVED ABOVE GOODS IN GOOD ORDER AND CONDITION" + "\"\n");

                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------------------------------------------------" + "\"\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);

            }
        }, 100);
    }

    public void setSettleReceiptPrint(int copy, String username, String settlementDate,
                                      ArrayList<SettlementReceiptModel> receiptModelList,
                                      ArrayList<SettlementReceiptDetailModel> receiptModelDetailList,
                                      ArrayList<SettlementReceiptModel.CurrencyDenomination> currencyList,
                                      ArrayList<SettlementReceiptModel.Expense> expenseList) throws IOException {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);
                list_height = 9;
                int y = 0;
                int currencyListl = 0;
                int expenseListl = 0;
                int settleHeight = 0;
                height = 85;
                double currencytotal = 0.0;
                double expensetotal = 0.0;
                double expensetotal1 = 0.0;

                ArrayList<SettlementReceiptDetailModel> receiptDetailsModels1 = new ArrayList<>();

                if (currencyList.size() > 0) {
                    currencyListl = currencyList.size();
                }
                if (expenseList.size() > 0) {
                    expenseListl  = expenseList.size();
                }

                for (SettlementReceiptDetailModel receiptModel : receiptModelDetailList) {

                    if (receiptModel.getPaymode().equalsIgnoreCase("Cheque")) {
                        receiptDetailsModels1.add(receiptModel) ;

                    }
                }
                settleHeight = receiptDetailsModels1.size() * 20;
                Log.w("settlheig",""+settleHeight);

                finalHeight = height + (receiptModelDetailList.size() * 11) + (currencyListl * list_height)
                        + (expenseListl * list_height) + invoiveSubTotalHeight+settleHeight;                ;

//                finalHeight = height + 10 + (receiptList.size() * list_height);
                //finalHeight = getPrintSize(finalHeight, "false", "false", "false", "false", "false", "false");

                //  finalHeight=height+(receiptList.size() * list_height)+20;
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                StringBuilder temp = new StringBuilder();
                if (company_name.length() > 38) {
                    temp.append((company_name.length() > 38) ? company_name.substring(0, 37) : company_name);
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                    String companyname = company_name.substring(37);
                    temp.append((companyname.length() > 37) ? companyname.substring(0, 36) : companyname);
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + temp.toString() + "\"\n\n");
                } else {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                }

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + "SETTLEMENT WITH RECEIPT" + "\"\n");

                y += LINE_SPACING + 20;
                // Define the Box
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Date : " + settlementDate + "\"\n");
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "User : " + username + "\"\n");
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                if (receiptModelDetailList.size() > 0) {
                    y += 30;
                    TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Receipt No" + "\"\n");
                    TscDll.sendcommand("TEXT 150," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Receipt Amt" + "\"\n");
                    TscDll.sendcommand("TEXT 320," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Pay Mode" + "\"\n");
                    TscDll.sendcommand("TEXT 460," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Paid Amt" + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    int index = 1;

                    for (SettlementReceiptDetailModel receiptModel : receiptModelDetailList) {
                        y += 30;
                        TscDll.sendcommand("TEXT 5," + y + ",\"Bold.TTF\",0,7,7,\"" + receiptModel.getCustomerName() + "\"\n");
                        y += 30;
                        TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptModel.getReceiptNo() + "\"\n");
                        TscDll.sendcommand("TEXT 150," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptModel.getCreditAmount())) + "\"\n");
                        TscDll.sendcommand("TEXT 320," + y + ",\"Poppins.TTF\",0,8,8,\"" + (receiptModel.getPaymode()) + "\"\n");
                        TscDll.sendcommand("TEXT 470," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptModel.getPaidAmount())) + "\"\n");

                        if (receiptModel.getPaymode().equalsIgnoreCase("Cheque")) {
                            y += 30;
                            // TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                            TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "-------------------------------------------------" + "\"\n");
                            y += 20;
                            TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Bank Code" + "\"\n");
                            TscDll.sendcommand("TEXT 250," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Cheque No" + "\"\n");
                            TscDll.sendcommand("TEXT 420," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Cheque Date" + "\"\n");
                            y += 30;
                            //  TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                            TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "-------------------------------------------------" + "\"\n");

                            y += 20;
                            TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptModel.getBankCode() + "\"\n");
                            TscDll.sendcommand("TEXT 250," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptModel.getChequeNo() + "\"\n");
                            TscDll.sendcommand("TEXT 420," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptModel.getChequeDate() + "\"\n");
                        }
                        y += LINE_SPACING;
                        TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                        index++;
                    }
                    y += 30;
                    TscDll.sendcommand("TEXT 180," + y + ",\"Bold.TTF\",0,8,8,\"" + "Total Invoice Amount :$ " + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptModelList.get(0).getTotalInvoiceAmount())) + "\"\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 180," + y + ",\"Bold.TTF\",0,8,8,\"" + "Total Paid Amount :$ " + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptModelList.get(0).getTotalPaidAmount())) + "\"\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 180," + y + ",\"Bold.TTF\",0,8,8,\"" + "Total Less Amount :$ " + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptModelList.get(0).getTotalLessAmount())) + "\"\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 180," + y + ",\"Bold.TTF\",0,8,8,\"" + "Total Cash Amount :$ " + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptModelList.get(0).getTotalCashAmount())) + "\"\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 180," + y + ",\"Bold.TTF\",0,8,8,\"" + "Total Cheque Amount :$ " + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptModelList.get(0).getTotalChequeAmount())) + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                }
                if (currencyList.size() > 0) {

                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SNo" + "\"\n");
                    TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Denomination" + "\"\n");
                    TscDll.sendcommand("TEXT 320," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Count" + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Total" + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");


                    int index = 1;
                    currencytotal = 0.0;

                    for (SettlementReceiptModel.CurrencyDenomination currency : currencyList) {

                        y += 30;
                        TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + index + "\"\n");
                        TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + currency.getDenomination() + "\"\n");

                        TscDll.sendcommand("TEXT 340," + y + ",\"Poppins.TTF\",0,8,8,\"" + (int) Double.parseDouble(currency.getCount()) + "\"\n");
                        TscDll.sendcommand("TEXT 470," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(currency.getTotal())) + "\"\n");

                        currencytotal += Double.parseDouble(currency.getTotal());

                        index++;

                    }
                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    y += 30;
                    TscDll.sendcommand("TEXT 180," + y + ",\"Bold.TTF\",0,8,8,\"" + "Currency Total:$" + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(currencytotal) + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                }
                 expensetotal1 = 0.0 ;
                if (expenseList.size() > 0) {

                    for (SettlementReceiptModel.Expense expense : expenseList) {
                        expensetotal1 += Double.parseDouble(expense.getExpenseTotal());
                    }
                }
                if (expenseList.size() > 0 && expensetotal1 > 0 ) {
                    y += 20;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SNo" + "\"\n");
                    TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Expense Name" + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Total" + "\"\n");


                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    int indexexpen = 1;
                    expensetotal = 0.0;

                        for (SettlementReceiptModel.Expense expense : expenseList) {

                            y += 30;
                            TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + indexexpen + "\"\n");
                            TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + expense.getExpeneName() + "\"\n");
                            TscDll.sendcommand("TEXT 470," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(expense.getExpenseTotal())) + "\"\n");

                            expensetotal += Double.parseDouble(expense.getExpenseTotal());
                            indexexpen++;
                        }

                        y += LINE_SPACING;
                        TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                        y += 30;
                        TscDll.sendcommand("TEXT 180," + y + ",\"Bold.TTF\",0,8,8,\"" + "Expense Total:$" + "\"\n");
                        TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(expensetotal) + "\"\n");

                        y += LINE_SPACING;
                        TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                }

                double net_amount = Double.parseDouble(receiptModelList.get(0).getTotalCashAmount()) - expensetotal;

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Net Amount:" + "\"\n");
                TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(net_amount) + "\"\n");

                double excessOrShortage = currencytotal - (Double.parseDouble(receiptModelList.get(0).getTotalCashAmount()) - expensetotal);

                if (excessOrShortage > 0.00) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Excess Amt:" + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(excessOrShortage) + "\"\n");
                } else {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Shortage Amt:" + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(excessOrShortage) + "\"\n");
                }

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += LINE_SPACING + 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "------------" + "\"\n");
                TscDll.sendcommand("TEXT 350," + y + ",\"Poppins.TTF\",0,8,8,\"" + "--------------" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Received By" + "\"\n");
                TscDll.sendcommand("TEXT 360," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Authorized By" + "\"\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);

            }
        }, 100);
    }


    public void setSettlementPrintSave(int copy, String settlementNo, String settlementDate, String locationCode, String settlementBy,
                                       ArrayList<CurrencyModel> currencyList, ArrayList<ExpenseModel>
                                               expenseList) throws IOException {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                TscDll.openport(macAddress);

                //String status = TscDll.printerstatus(300);
                int y = 0;
                height = 70;
                double currencytotal = 0.0;
                double expensetotal = 0.0;
                finalHeight = height + (currencyList.size() * 8) + (expenseList.size() * 8) + invoiceBottomLine;
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

//                if (company_gst != null && !company_gst.isEmpty()) {
//                    y += LINE_SPACING;
//                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO : " + company_gst + "\"\n");
//                }
                if(settlementDate!=null && settlementDate.equals("null") && !settlementDate.equals("")){
                    dateStr = settlementDate ;
                }else{
                    dateStr = currentDate ;
                }
                    if(locationCode!=null && locationCode.equals("null") && !locationCode.equals("")){
                        locStr = locationCode ;
                    }else{
                        locStr = locationCode1 ;
                    }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + "SETTLEMENT" + "\"\n");

                y += LINE_SPACING + 20;
                // Define the Box
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Settlement No : " + settlementNo + "\"\n");
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Settlement Date : " + dateStr + "\"\n");
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Location Code : " + locStr + "\"\n");
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Settlement By : " + settlementBy + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                if (currencyList.size() > 0) {

                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SNo" + "\"\n");
                    TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Denomination" + "\"\n");
                    TscDll.sendcommand("TEXT 320," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Count" + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Total" + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    int index = 1;
                    currencytotal = 0.0;

                    for (CurrencyModel currency : currencyList) {

                        y += 30;
                        TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + index + "\"\n");
                        TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + currency.getCurrencyName() + "\"\n");

                        TscDll.sendcommand("TEXT 340," + y + ",\"Poppins.TTF\",0,8,8,\"" + (int) Double.parseDouble(currency.getCount()) + "\"\n");
                        TscDll.sendcommand("TEXT 470," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(currency.getTotal())) + "\"\n");

                        currencytotal += Double.parseDouble(currency.getTotal());

                        index++;

                    }
                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    y += 30;
                    TscDll.sendcommand("TEXT 180," + y + ",\"Bold.TTF\",0,8,8,\"" + "Currency Total:$" + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(currencytotal) + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                }

                if (expenseList.size() > 0) {
                    y += 20;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SNo" + "\"\n");
                    TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Expense Name" + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Total" + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    int indexexpen = 1;
                    expensetotal = 0.0;

                    for (ExpenseModel expense : expenseList) {

                        y += 30;
                        TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + indexexpen + "\"\n");
                        TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + expense.getExpenseName() + "\"\n");
                        TscDll.sendcommand("TEXT 470," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(expense.getExpenseTotal())) + "\"\n");

                        expensetotal += Double.parseDouble(expense.getExpenseTotal());
                        indexexpen++;
                    }

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    y += 30;
                    TscDll.sendcommand("TEXT 180," + y + ",\"Bold.TTF\",0,8,8,\"" + "Expense Total:$" + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(expensetotal) + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                }

                double netTotal = currencytotal + expensetotal;

                y += 30;
                TscDll.sendcommand("TEXT 100," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Total Amount:$" + "\"\n");
                TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(currencytotal) + "\"\n");

                y += 30;
                TscDll.sendcommand("TEXT 100," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Total Expense:$" + "\"\n");
                TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(expensetotal) + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Net Amount:$" + "\"\n");
                TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(netTotal) + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

       /* y += 20;
        TscDll.sendcommand("TEXT 0,"+y+",\"Poppins.TTF\",0,8,8,\""+"Customer Signature & Company Stamp"+"\"\n");


        y += 150;
        TscDll.sendcommand("TEXT 0,"+y+",\"Poppins.TTF\",0,8,8,\""+"-------------------------------------------------"+"\"\n");

        y += 30;
        TscDll.sendcommand("TEXT 0,"+y+",\"Poppins.TTF\",0,7,7,\""+"RECEIVED ABOVE GOODS IN GOOD ORDER AND CONDITION"+"\"\n");

        y += 30;
        TscDll.sendcommand("TEXT 0,"+y+",\"Poppins.TTF\",0,8,8,\""+"-------------------------------------------------"+"\"\n");*/

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);
            }
        }, 100);
    }

    public void printInvoice(int copy, ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails, ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList, String isDoPrint) {
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            for (int n = 0; n < copy; n++) {
                int y = 0;
                StringBuilder temp = new StringBuilder();
                y = printTitle(200, y, "TAX INVOICE", temp);
                y = printCompanyDetails(y, temp);

                String cpclConfigLabel = temp.toString();

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Invoice No")
                        + text(150, y, " : ") + text(180, y, invoiceHeaderDetails.get(0).getInvoiceNumber());

                if (invoiceHeaderDetails.get(0).getSoNumber() != null && !invoiceHeaderDetails.get(0).getSoNumber().isEmpty()) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SO No")
                            + text(150, y, " : ") + text(180, y, invoiceHeaderDetails.get(0).getSoNumber());
                }

                if (invoiceHeaderDetails.get(0).getDoNumber() != null && !invoiceHeaderDetails.get(0).getDoNumber().isEmpty()) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "DO No")
                            + text(150, y, " : ") + text(180, y, invoiceHeaderDetails.get(0).getDoNumber());
                }

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Invoice Date")
                        + text(150, y, " : ") + text(180, y, invoiceHeaderDetails.get(0).getInvoiceDate());

                cpclConfigLabel += (text(LEFT_MARGIN,
                        y += LINE_SPACING, "Customer Name"));
                cpclConfigLabel += (text(200, y, " : "));
                Log.d("CustnameeeeAddres2", invoiceHeaderDetails.get(0).getCustomerName());
                cpclConfigLabel += (text(
                        220,
                        y,
                        (invoiceHeaderDetails.get(0).getCustomerName().length() > 22) ? invoiceHeaderDetails.get(0).getCustomerName()
                                .substring(0, 21) : invoiceHeaderDetails.get(0).getCustomerName()));
                if (invoiceHeaderDetails.get(0).getCustomerName().length() > 22) {
                    String customername = invoiceHeaderDetails.get(0).getCustomerName().substring(21);
                    cpclConfigLabel += (text(
                            220,
                            y += LINE_SPACING,
                            (customername.length() > 22) ? customername
                                    .substring(0, 21) : customername));
                }

               /* if (invoiceHeaderDetails.get(0).getDeliveryAddress() != null && !invoiceHeaderDetails.get(0).getDeliveryAddress().isEmpty()) {
                    String deliveryAddress=invoiceHeaderDetails.get(0).getDeliveryAddress();
                    cpclConfigLabel += (text(LEFT_MARGIN,
                            y += LINE_SPACING, "Address"));
                    cpclConfigLabel += (text(200, y, " : "));
                    Log.d("CustnameeeeAddres2", deliveryAddress);
                    cpclConfigLabel += (text(
                            230,
                            y,
                            (deliveryAddress.length() > 22) ? deliveryAddress
                                    .substring(0, 22) : deliveryAddress));
                    if (deliveryAddress.length() > 22) {

                        String deladdress = deliveryAddress.substring(22);

                        cpclConfigLabel += (text(
                                230,
                                y += LINE_SPACING,
                                (deladdress.length() > 22) ? deladdress
                                        .substring(0, 21) : deladdress));
                    }
                }*/

                if (invoiceHeaderDetails.get(0).getAddress1() != null && !invoiceHeaderDetails.get(0).getAddress1().isEmpty()) {
                    String deliveryAddress = invoiceHeaderDetails.get(0).getAddress1();
                    cpclConfigLabel += (text(LEFT_MARGIN,
                            y += LINE_SPACING, "Address 1"));
                    cpclConfigLabel += (text(200, y, " : "));
                    Log.d("CustomerAddress1", deliveryAddress);
                    cpclConfigLabel += (text(
                            230,
                            y,
                            (deliveryAddress.length() > 22) ? deliveryAddress
                                    .substring(0, 22) : deliveryAddress));
                    if (deliveryAddress.length() > 22) {

                        String deladdress = deliveryAddress.substring(22);

                        cpclConfigLabel += (text(
                                230,
                                y += LINE_SPACING,
                                (deladdress.length() > 22) ? deladdress
                                        .substring(0, 21) : deladdress));
                    }
                }

                if (invoiceHeaderDetails.get(0).getAddress2() != null && !invoiceHeaderDetails.get(0).getAddress2().isEmpty()) {
                    String deliveryAddress = invoiceHeaderDetails.get(0).getAddress2();
                    cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING, "Address 2"));
                    cpclConfigLabel += (text(200, y, " : "));
                    Log.d("CustomerAddress1", deliveryAddress);
                    cpclConfigLabel += (text(230, y, (deliveryAddress.length() > 22) ? deliveryAddress.substring(0, 22) : deliveryAddress));
                    if (deliveryAddress.length() > 22) {
                        String deladdress = deliveryAddress.substring(22);
                        cpclConfigLabel += (text(230, y += LINE_SPACING, (deladdress.length() > 22) ? deladdress.substring(0, 21) : deladdress));
                    }
                }

                if (invoiceHeaderDetails.get(0).getAddress3() != null && !invoiceHeaderDetails.get(0).getAddress3().isEmpty()) {
                    String deliveryAddress = invoiceHeaderDetails.get(0).getAddress3();
                    cpclConfigLabel += (text(LEFT_MARGIN,
                            y += LINE_SPACING, "Address 3"));
                    cpclConfigLabel += (text(200, y, " : "));
                    Log.d("CustomerAddress1", deliveryAddress);
                    cpclConfigLabel += (text(
                            230,
                            y,
                            (deliveryAddress.length() > 22) ? deliveryAddress
                                    .substring(0, 22) : deliveryAddress));
                    if (deliveryAddress.length() > 22) {

                        String deladdress = deliveryAddress.substring(22);

                        cpclConfigLabel += (text(
                                230,
                                y += LINE_SPACING,
                                (deladdress.length() > 22) ? deladdress
                                        .substring(0, 21) : deladdress));
                    }
                }


                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
                cpclConfigLabel += text(70, y, "Description");
                cpclConfigLabel += text(320, y, "Qty");
                cpclConfigLabel += text(390, y, "Price");
                cpclConfigLabel += text(490, y, "Total");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (invoiceList.size() > 0) {
                    int index = 1;
                    for (InvoicePrintPreviewModel.InvoiceList invoice : invoiceList) {

                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, String.valueOf(index));  // Display the Serial Number

                        cpclConfigLabel += text(70, y, (invoice.getDescription().length() > 35)
                                ? invoice.getDescription().substring(0, 34)
                                : invoice.getDescription());                      // Display the product Name

                        int count = 0;
                        String name = invoice.getDescription();
                        int len = name.length();
                        if (len > 35) {
                            int get_len = name.substring(34, len).length();
                            String remark = name.substring(34, len);
                            Log.d("BalanceString", "-->" + len + " " + get_len + " " + remark);
                            String names;

                            for (int j = 0; j < get_len; j = j + 35) {
                                count = count + 35;
                                if (count > get_len) {
                                    names = remark.substring(j, get_len);
                                    cpclConfigLabel += text(
                                            70,
                                            y += LINE_SPACING, names);
                                    Log.d("BalancesDescription", "-->" + names);

                                } else {
                                    names = remark.substring(j, j + 35);
                                    cpclConfigLabel += text(
                                            70,
                                            y += LINE_SPACING, names);
                                    Log.d("BalancesValues", "-->" + names);

                                }
                            }
                        }

                        if (Double.parseDouble(invoice.getNetQty()) < 0) {
                            cpclConfigLabel += text(150, y += LINE_SPACING, "(as Return)");
                            cpclConfigLabel += text(310, y, Utils.getQtyValue(invoice.getNetQty()));  // Display the Qty of Invoice
                        } else {
                            cpclConfigLabel += text(320, y += LINE_SPACING, Utils.getQtyValue(invoice.getNetQty()));  // Display the Qty of Invoice
                        }
                       /* if (Integer.parseInt(invoice.getPcsperCarton()) == 1) {
                            cpclConfigLabel += text(400, y, Utils.twoDecimalPoint(Double.parseDouble(invoice.getCartonPrice()))); // Display the Particular product price
                        } else {
                            cpclConfigLabel += text(400, y, Utils.twoDecimalPoint(Double.parseDouble(invoice.getUnitPrice()))); // Display the Particular product price

                        }*/
                        cpclConfigLabel += text(390, y, twoDecimalPoint(Double.parseDouble(invoice.getPricevalue()))); // Display the Particular product price


                        cpclConfigLabel += text(490, y, twoDecimalPoint(Double.parseDouble(invoice.getTotal()))); // Display the Sub total of Particular product


                /*    if (invoice.getFocqty() > 0) {
                        cpclConfigLabel += text(70, y += LINE_SPACING,
                                "Foc")
                                + text(210, y, " : ")
                                + text(280, y, (int) products.getFocqty() + "(FOC)");
                    }

                    // Log.d("products.getExchangeqty() 1", ""+(int)
                    // products.getExchangeqty());

                    if (products.getExchangeqty() > 0) {
                        cpclConfigLabel += text(70, y += LINE_SPACING,
                                "Exchange")
                                + text(210, y, " : ")
                                + text(280, y,
                                (int) products.getExchangeqty());
                    }

                    if(products.getRetrnQty() >0){
                        cpclConfigLabel += text(70, y += LINE_SPACING,
                                "Return")
                                + text(210, y, " : ")
                                + text(280, y,
                                (int) products.getRetrnQty()+ "(Return)");
                    }*/

                        index++;
                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title


                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "")
                        + text(160, y, ".")
                        + text(180, y, "");

              /*  if (invoiceHeaderDetails.get(0).getTaxType().equals("I")) {

                    double sub_total = Double.parseDouble(invoiceHeaderDetails.get(0).getSubTotal()) - Double.parseDouble(invoiceHeaderDetails.get(0).getNetTax());

                    cpclConfigLabel += text(260, y, "Sub Total")
                            + text(420, y, " : ")
                            + text(470, y, Utils.twoDecimalPoint(sub_total));
                }else {
                    cpclConfigLabel += text(260, y, "Sub Total")
                            + text(420, y, " : ")
                            + text(470, y, Utils.twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getSubTotal())));
                }*/

                cpclConfigLabel += text(260, y, "Sub Total")
                        + text(420, y, " : ")
                        + text(470, y, twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getSubTotal())));

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "")
                        + text(140, y, ".")
                        + text(180, y, "");

                cpclConfigLabel += text(260, y, "GST(" + invoiceHeaderDetails.get(0).getTaxType() + ":" + twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getTaxValue())) + " %)")
                        + text(420, y, " : ")
                        + text(470, y, twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getNetTax())));


                cpclConfigLabel += text(260, y += LINE_SPACING, "Net Total")
                        + text(420, y, " : ")
                        + text(470, y, twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getNetTotal())));


                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Total Outstanding")
                        + text(200, y, " : ")
                        + text(220, y, twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getOutStandingAmount())));

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String dateTime = formatter.format(date);
                System.out.println(formatter.format(date));

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Print Date/Time")
                        + text(200, y, " : ")
                        + text(220, y, dateTime);


                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "--------------------------------------------");

                //  cpclConfigLabel += text(75, y += 140, "Received By");


                //cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "--------------------------------------------");

                //  cpclConfigLabel += text(75, y += LINE_SPACING, "Issued  By")+ text(210, y, " : ") + text(240, y,username.toUpperCase());
                //  cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "--------------------------------------------");

                cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1" + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

                //   byte[] arrayOfByte1 = { 27, 33 };
                // byte[] format = { 27, 33 };
                //   format[1] = ((byte)(0x1 | arrayOfByte1[2]));
                // byte[] format = {29, 33, 35 };
                //  os.write(format);

                //  os.write(new byte[] { 28, 46 }); //Cancels Chinese character mode
                // byte[] a = {97, 98, 99, 100, 101, 102, 103, 104, 105, 49, 45};
                // os.write(PrinterCommand.fontSizeSet(a[0]));

                // format[2] = ((byte)(0x1 | arrayOfByte1[2]));
                //  format[2] = ((byte)(0x10 | arrayOfByte1[2]));

                // os.write(format);
                // os.write(cpclConfigLabel.getBytes(),0,cpclConfigLabel.getBytes().length);
                //  String val="^XA^FO50,50^A@N,15,15,E:ARI000.FNT^F";
                //String value="^FS^XZ";
                // String config=val+cpclConfigLabel+value;
                //   os.write(val.getBytes());
                os.write(cpclConfigLabel.getBytes("iso-8859-1"));
                os.flush();
            }
            os.close();

          /*  Log.w("IsDoPrint:",isDoPrint);
            if (isDoPrint!=null && isDoPrint.equals("true")){
                printDeliveryOrder(copy,invoiceHeaderDetails,invoiceList);
            }*/

        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Set the Function for the Delivery Order
     *
     * @return
     */

    public void printDeliveryOrder(int copy, ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails, ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList) {
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);

            for (int n = 0; n < copy; n++) {

                int y = 0;
                double net_qty_value = 0;

                StringBuilder temp = new StringBuilder();
                y = printTitle(220, y, "DELIVERY ORDER", temp);
                y = printCompanyDetails(y, temp);

                String cpclConfigLabel = temp.toString();

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title


                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "DO No")
                        + text(150, y, " : ") + text(180, y, invoiceHeaderDetails.get(0).getInvoiceNumber());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "DO Date")
                        + text(150, y, " : ") + text(180, y, invoiceHeaderDetails.get(0).getInvoiceDate());

                cpclConfigLabel += (text(LEFT_MARGIN,
                        y += LINE_SPACING, "Customer Name"));
                cpclConfigLabel += (text(200, y, " : "));
                Log.d("CustnameeeeAddres2", invoiceHeaderDetails.get(0).getCustomerName());
                cpclConfigLabel += (text(
                        220,
                        y,
                        (invoiceHeaderDetails.get(0).getCustomerName().length() > 22) ? invoiceHeaderDetails.get(0).getCustomerName()
                                .substring(0, 21) : invoiceHeaderDetails.get(0).getCustomerName()));
                if (invoiceHeaderDetails.get(0).getCustomerName().length() > 22) {
                    String customername = invoiceHeaderDetails.get(0).getCustomerName().substring(21);
                    cpclConfigLabel += (text(220, y += LINE_SPACING, (customername.length() > 22) ? customername.substring(0, 21) : customername));
                }
               /* if (invoiceHeaderDetails.get(0).getDeliveryAddress() != null && !invoiceHeaderDetails.get(0).getDeliveryAddress().isEmpty()) {
                    String deliveryAddress=invoiceHeaderDetails.get(0).getDeliveryAddress();
                    cpclConfigLabel += (text(LEFT_MARGIN,
                            y += LINE_SPACING, "Address"));
                    cpclConfigLabel += (text(200, y, " : "));
                    Log.d("CustnameeeeAddres2", deliveryAddress);
                    cpclConfigLabel += (text(
                            230,
                            y,
                            (deliveryAddress.length() > 25) ? deliveryAddress
                                    .substring(0, 24) : deliveryAddress));
                    if (deliveryAddress.length() > 25) {

                        String deladdress = deliveryAddress.substring(24);

                        cpclConfigLabel += (text(
                                230,
                                y += LINE_SPACING,
                                (deladdress.length() > 25) ? deladdress
                                        .substring(0, 24) : deladdress));
                    }
                }*/


                if (invoiceHeaderDetails.get(0).getAddress1() != null && !invoiceHeaderDetails.get(0).getAddress1().isEmpty()) {
                    String deliveryAddress = invoiceHeaderDetails.get(0).getAddress1();
                    cpclConfigLabel += (text(LEFT_MARGIN,
                            y += LINE_SPACING, "Address 1"));
                    cpclConfigLabel += (text(200, y, " : "));
                    Log.d("CustomerAddress1", deliveryAddress);
                    cpclConfigLabel += (text(
                            230,
                            y,
                            (deliveryAddress.length() > 22) ? deliveryAddress
                                    .substring(0, 22) : deliveryAddress));
                    if (deliveryAddress.length() > 22) {

                        String deladdress = deliveryAddress.substring(22);

                        cpclConfigLabel += (text(
                                230,
                                y += LINE_SPACING,
                                (deladdress.length() > 22) ? deladdress
                                        .substring(0, 21) : deladdress));
                    }
                }


                if (invoiceHeaderDetails.get(0).getAddress2() != null && !invoiceHeaderDetails.get(0).getAddress2().isEmpty()) {
                    String deliveryAddress = invoiceHeaderDetails.get(0).getAddress2();
                    cpclConfigLabel += (text(LEFT_MARGIN,
                            y += LINE_SPACING, "Address 2"));
                    cpclConfigLabel += (text(200, y, " : "));
                    Log.d("CustomerAddress2", deliveryAddress);
                    cpclConfigLabel += (text(
                            230,
                            y,
                            (deliveryAddress.length() > 22) ? deliveryAddress
                                    .substring(0, 22) : deliveryAddress));
                    if (deliveryAddress.length() > 22) {

                        String deladdress = deliveryAddress.substring(22);

                        cpclConfigLabel += (text(
                                230,
                                y += LINE_SPACING,
                                (deladdress.length() > 22) ? deladdress
                                        .substring(0, 21) : deladdress));
                    }
                }


                if (invoiceHeaderDetails.get(0).getAddress3() != null && !invoiceHeaderDetails.get(0).getAddress3().isEmpty()) {
                    String deliveryAddress = invoiceHeaderDetails.get(0).getAddress3();
                    cpclConfigLabel += (text(LEFT_MARGIN,
                            y += LINE_SPACING, "Address 3"));
                    cpclConfigLabel += (text(200, y, " : "));
                    Log.d("CustomerAddress3", deliveryAddress);
                    cpclConfigLabel += (text(
                            230,
                            y,
                            (deliveryAddress.length() > 22) ? deliveryAddress
                                    .substring(0, 22) : deliveryAddress));
                    if (deliveryAddress.length() > 22) {

                        String deladdress = deliveryAddress.substring(22);

                        cpclConfigLabel += (text(
                                230,
                                y += LINE_SPACING,
                                (deladdress.length() > 22) ? deladdress
                                        .substring(0, 21) : deladdress));
                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
                cpclConfigLabel += text(70, y, "Description");
                cpclConfigLabel += text(490, y, "Qty");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (invoiceList.size() > 0) {
                    int index = 1;
                    for (InvoicePrintPreviewModel.InvoiceList invoice : invoiceList) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, String.valueOf(index));  // Display the Serial Number
                        cpclConfigLabel += text(70, y, (invoice.getDescription().length() > 35)
                                ? invoice.getDescription().substring(0, 34)
                                : invoice.getDescription());                      // Display the product Name

                        int count = 0;
                        String name = invoice.getDescription();
                        int len = name.length();
                        if (len > 35) {
                            int get_len = name.substring(34, len).length();
                            String remark = name.substring(34, len);
                            Log.d("BalanceString", "-->" + len + " " + get_len + " " + remark);
                            String names;

                            for (int j = 0; j < get_len; j = j + 35) {
                                count = count + 35;
                                if (count > get_len) {
                                    names = remark.substring(j, get_len);
                                    cpclConfigLabel += text(
                                            70,
                                            y += LINE_SPACING, names);
                                    Log.d("BalancesDescription", "-->" + names);

                                } else {
                                    names = remark.substring(j, j + 35);
                                    cpclConfigLabel += text(
                                            70,
                                            y += LINE_SPACING, names);
                                    Log.d("BalancesValues", "-->" + names);

                                }
                            }
                        }
                        net_qty_value += Double.parseDouble(invoice.getNetQty());
                        cpclConfigLabel += text(490, y, (int) Double.parseDouble(invoice.getNetQty()) + ""); // Display the Sub total of Particular product
                        index++;
                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(260, y += LINE_SPACING, "Total Qty")
                        + text(420, y, " : ")
                        + text(490, y, (int) net_qty_value + "");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1" + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

                os.write(cpclConfigLabel.getBytes("iso-8859-1"));
                os.flush();
            }
            os.close();

        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void printSettlement(int copy, String settlementNo, String settlementDate, String locationCode, String user, ArrayList<CurrencyModel> currencyList, ArrayList<ExpenseModel> expenseList) {
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);

            for (int n = 0; n < copy; n++) {

                int y = 0;
                double net_total_values = 0.00;
                double net_expense_value = 0.00;
                double net_amount = 0.00;

                StringBuilder temp = new StringBuilder();
                y = printTitle(220, y, "SETTLEMENT", temp);
                y = printCompanyDetails(y, temp);

                String cpclConfigLabel = temp.toString();

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Settlement No")
                        + text(210, y, " : ") + text(230, y, settlementNo);

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Settlement Date")
                        + text(210, y, " : ") + text(230, y, settlementDate);

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Location Code")
                        + text(210, y, " : ") + text(230, y, locationCode);

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Settlement By")
                        + text(210, y, " : ") + text(230, y, user);


                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
                cpclConfigLabel += text(100, y, "Denomination");
                cpclConfigLabel += text(350, y, "Count");
                cpclConfigLabel += text(450, y, "Total");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (currencyList.size() > 0) {
                    int index = 1;
                    for (CurrencyModel currency : currencyList) {

                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, String.valueOf(index));  // Display the Serial Number

                        cpclConfigLabel += text(100, y, currency.getCurrencyName() + "");
                        cpclConfigLabel += text(350, y, currency.getCount() + ""); // Display the Sub total of Particular product

                        cpclConfigLabel += text(450, y, Utils.twoDecimalPoint(Double.parseDouble(currency.getTotal())) + ""); // Display the Sub total of Particular product

                        net_total_values += Double.parseDouble(currency.getTotal());
                        index++;
                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(230, y += LINE_SPACING, "Total Amount")
                        + text(400, y, " : ")
                        + text(450, y, Utils.twoDecimalPoint(net_total_values) + "");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (expenseList.size() > 0) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
                    cpclConfigLabel += text(100, y, "Expense");
                    cpclConfigLabel += text(450, y, "Amount");

                    cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title
                    int index = 1;
                    for (ExpenseModel expense : expenseList) {

                        if (Double.parseDouble(expense.getExpenseTotal()) > 0) {
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, String.valueOf(index));  // Display the Serial Number

                            cpclConfigLabel += text(100, y, expense.getExpenseName() + "");
                            cpclConfigLabel += text(450, y, expense.getExpenseTotal() + ""); // Display the Sub total of Particular product
                            net_expense_value += Double.parseDouble(expense.getExpenseTotal());
                            index++;
                        }
                    }

                    cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                    cpclConfigLabel += text(200, y += LINE_SPACING, "Total Expense")
                            + text(420, y, " : ")
                            + text(450, y, Utils.twoDecimalPoint(net_expense_value) + "");

                    cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title
                }

                cpclConfigLabel += text(200, y += LINE_SPACING, "Total Amount")
                        + text(420, y, " : ")
                        + text(450, y, Utils.twoDecimalPoint(net_total_values) + "");

                cpclConfigLabel += text(200, y += LINE_SPACING, "Total Expense")
                        + text(420, y, " : ")
                        + text(450, y, Utils.twoDecimalPoint(net_expense_value) + "");

                net_amount = net_total_values + net_expense_value;

                cpclConfigLabel += text(200, y += LINE_SPACING, "Net Amount")
                        + text(420, y, " : ")
                        + text(450, y, Utils.twoDecimalPoint(net_amount) + "");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1" + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

                os.write(cpclConfigLabel.getBytes("iso-8859-1"));
                os.flush();
            }
            os.close();

        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private static String readFile(int size, InputStream is) {
        try {
            byte buffer[] = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void printReportStockSummaryOpen(int copy, String fromdate, String todate,
                                            ArrayList<StockSummaryReportOpenModel> stockSummaryReportOpenModels)
            throws IOException {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);

                int y = 0;
                height = 80;

                finalHeight = height + (stockSummaryReportOpenModels.size() * list_height);
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CLS\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                StringBuilder temp = new StringBuilder();
                if (company_name.length() > 38) {
                    temp.append((company_name.length() > 38) ? company_name.substring(0, 37) : company_name);
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                    String companyname = company_name.substring(37);
                    temp.append((companyname.length() > 37) ? companyname.substring(0, 36) : companyname);
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + temp.toString() + "\"\n\n");
                } else {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                }

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,9,9,2,\"" + "Stock Summary Opening Balance" + "\"\n");

                y += LINE_SPACING + 10;
                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "From Date: " + fromdate + "\"\n");
                TscDll.sendcommand("TEXT 330," + y + ",\"Poppins.TTF\",0,8,8,\"" + "To Date: " + todate + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Sno" + "\"\n");
                TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Opening Bal" + "\"\n");
                TscDll.sendcommand("TEXT 230," + y + ",\"Poppins.TTF\",0,8,8,\"" + "InQty" + "\"\n");
                TscDll.sendcommand("TEXT 320," + y + ",\"Poppins.TTF\",0,8,8,\"" + "OutQty" + "\"\n");
                TscDll.sendcommand("TEXT 430," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Closing Bal" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                int index = 1;
                Double openqty = 0.0;
                Double closeqty =0.0;
                Double inqty = 0.0;
                Double outqty = 0.0;
                for (StockSummaryReportOpenModel stockSummaryReportOpenModels : stockSummaryReportOpenModels) {

                    String itemNameStr = stockSummaryReportOpenModels.getItemName()+"-"+stockSummaryReportOpenModels.getItemCode() ;y += 30;
                    TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + index + "\"\n");
                    if (itemNameStr.length() <= 45) {
                        TscDll.sendcommand("TEXT 70," + y + ",\"Bold.TTF\",0,8,8,\"" + itemNameStr + "\"\n\n");
                    } else {
                        String firstname = itemNameStr.substring(0, 42);
                        String secondname = itemNameStr.substring(42);

                        TscDll.sendcommand("TEXT 70," + y + ",\"Bold.TTF\",0,8,8,\"" + firstname + "\"\n\n");
                        y += 30;
                        TscDll.sendcommand("TEXT 70," + y + ",\"Bold.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                    }

                    if(!stockSummaryReportOpenModels.getClosingQty().isEmpty() &&
                            !stockSummaryReportOpenModels.getClosingQty().equals("")){
                        closeqty = Double.parseDouble(stockSummaryReportOpenModels.getClosingQty());
                    }else{
                        closeqty = 0.0;
                    }
                    if(!stockSummaryReportOpenModels.getOpeningQty().isEmpty() &&
                            !stockSummaryReportOpenModels.getOpeningQty().equals("")){
                        openqty = Double.parseDouble(stockSummaryReportOpenModels.getOpeningQty());
                    }else{
                        openqty = 0.0;
                    }
                    if(!stockSummaryReportOpenModels.getInwardQty().isEmpty() &&
                            !stockSummaryReportOpenModels.getInwardQty().equals("")){
                        inqty = Double.parseDouble(stockSummaryReportOpenModels.getInwardQty());
                    }else{
                        inqty = 0.0;
                    }
                    if(!stockSummaryReportOpenModels.getOutwardQty().isEmpty() &&
                            !stockSummaryReportOpenModels.getOutwardQty().equals("")){
                        outqty = Double.parseDouble(stockSummaryReportOpenModels.getOutwardQty());
                    }else{
                        outqty = 0.0;
                    }
                    y += 30;
                    TscDll.sendcommand("TEXT 70," + y + ",\"Bold.TTF\",0,8,8,\"" +  openqty + "\"\n");
                    TscDll.sendcommand("TEXT 230," + y + ",\"Poppins.TTF\",0,8,8,\"" + inqty + "\"\n");
                    TscDll.sendcommand("TEXT 320," + y + ",\"Poppins.TTF\",0,8,8,\"" + outqty + "\"\n");
                    TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,8,8,\"" + closeqty + "\"\n");

                    index++;
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += LINE_SPACING + 50;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "------------" + "\"\n");
                TscDll.sendcommand("TEXT 230," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------" + "\"\n");
                TscDll.sendcommand("TEXT 420," + y + ",\"Poppins.TTF\",0,8,8,\"" + "------------" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Approved By" + "\"\n");
                TscDll.sendcommand("TEXT 230," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Issue By" + "\"\n");
                TscDll.sendcommand("TEXT 420," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Requested By" + "\"\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);
            }
        }, 100);
    }

    public void printStockBadRequestReturn(int copy, String fromdate, String todate, ArrayList<StockBadRequestReturnModel> stockBadRequestReturnModels)
            throws IOException {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);

                int y = 0;
                height = 100;

                finalHeight = height + (stockBadRequestReturnModels.size() * list_height);
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CLS\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                StringBuilder temp = new StringBuilder();
                if (company_name.length() > 38) {
                    temp.append((company_name.length() > 38) ? company_name.substring(0, 37) : company_name);
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                    String companyname = company_name.substring(37);
                    temp.append((companyname.length() > 37) ? companyname.substring(0, 36) : companyname);
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + temp.toString() + "\"\n\n");
                } else {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                }

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + "UNLOADING SHEET" + "\"\n");
                y += 30;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,9,9,2,\"" + "(Bad Stock Return)" + "\"\n");

                y += LINE_SPACING + 10;
                // Define the Box
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,9,9,\"" + "Transfer To: " + stockBadRequestReturnModels.get(0).getWarehouseCode() + "\"\n");
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "From Date: " + fromdate + "\"\n");
                TscDll.sendcommand("TEXT 330," + y + ",\"Poppins.TTF\",0,8,8,\"" + "To Date: " + todate + "\"\n");

                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,9,9,\"" + "Reason : " + stockBadRequestReturnModels.get(0).getReason() + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "NO" + "\"\n");
                TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + "DESCRIPTION" + "\"\n");
                TscDll.sendcommand("TEXT 400," + y + ",\"Poppins.TTF\",0,8,8,\"" + "QTY" + "\"\n");
                TscDll.sendcommand("TEXT 500," + y + ",\"Poppins.TTF\",0,8,8,\"" + "UOM" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                int index = 1;
                for (StockBadRequestReturnModel.StockBadRequestReturnDetails stockBadRequestReturnDetails :
                        stockBadRequestReturnModels.get(0).getStockBadRequestReturnDetailsList()) {

                    y += 30;
                    TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + index + "\"\n");
                    if (stockBadRequestReturnDetails.getDescription().length() <= 45) {
                        TscDll.sendcommand("TEXT 70," + y + ",\"Bold.TTF\",0,8,8,\"" + stockBadRequestReturnDetails.getDescription() + "\"\n\n");
                    } else {
                        String firstname = stockBadRequestReturnDetails.getDescription().substring(0, 42);
                        String secondname = stockBadRequestReturnDetails.getDescription().substring(42);

                        TscDll.sendcommand("TEXT 70," + y + ",\"Bold.TTF\",0,8,8,\"" + firstname + "\"\n\n");
                        y += 30;
                        TscDll.sendcommand("TEXT 70," + y + ",\"Bold.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                    }
                    y += 30;
                    TscDll.sendcommand("TEXT 410," + y + ",\"Bold.TTF\",0,8,8,\"" + (int) Double.parseDouble("-" + stockBadRequestReturnDetails.getQty()) + "\"\n");
                    TscDll.sendcommand("TEXT 510," + y + ",\"Poppins.TTF\",0,8,8,\"" + (stockBadRequestReturnDetails.getUomCode()) + "\"\n");

                    index++;
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += LINE_SPACING + 50;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "------------" + "\"\n");
                TscDll.sendcommand("TEXT 230," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------" + "\"\n");
                TscDll.sendcommand("TEXT 420," + y + ",\"Poppins.TTF\",0,8,8,\"" + "------------" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Approved By" + "\"\n");
                TscDll.sendcommand("TEXT 230," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Issue By" + "\"\n");
                TscDll.sendcommand("TEXT 420," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Requested By" + "\"\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);
            }
        }, 100);
    }


    public void printReportStockSummary(int copy, String fromdate, String todate, String locationCode, String companyId, ArrayList<ReportStockSummaryModel> reportStockSummaryModels)
            throws IOException {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);
                int y = 0;
                height = 70;

                finalHeight = height + (reportStockSummaryModels.get(0).getReportStockSummaryDetailsList().size() * list_height);
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media

                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                StringBuilder temp = new StringBuilder();
                if (company_name.length() > 38) {
                    temp.append((company_name.length() > 38) ? company_name.substring(0, 37) : company_name);
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                    String companyname = company_name.substring(37);
                    temp.append((companyname.length() > 37) ? companyname.substring(0, 36) : companyname);
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + temp.toString() + "\"\n\n");
                } else {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                }

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 160," + y + ",\"Bold.TTF\",0,10,10,\"" + "STOCK SUMMARY" + "\"\n");

                y += LINE_SPACING + 10;
                // Define the Box
                //  TscDll.sendcommand("TEXT 0,"+y+",\"Poppins.TTF\",0,8,8,\""+"Device ID: "+getDeviceName()+"\"\n");
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Location: " + locationCode + "\"\n");
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Date: " + fromdate + "\"\n");
                // TscDll.sendcommand("TEXT 330,"+y+",\"Poppins.TTF\",0,8,8,\""+"To Date: "+todate+"\"\n");
                TscDll.sendcommand("TEXT 310," + y + ",\"Poppins.TTF\",0,8,8,\"" + "User: " + username + "\"\n");
                //y += LINE_SPACING;
                // TscDll.sendcommand("TEXT 0,"+y+",\"Poppins.TTF\",0,8,8,\""+"Company ID: "+companyId+"\"\n");

                y += LINE_SPACING + 10;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "BF" + "\"\n");
                TscDll.sendcommand("TEXT 70," + y + ",\"Bold.TTF\",0,8,8,\"" + "In" + "\"\n");
                TscDll.sendcommand("TEXT 130," + y + ",\"Bold.TTF\",0,8,8,\"" + "Sales" + "\"\n");
                TscDll.sendcommand("TEXT 230," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Foc" + "\"\n");
                TscDll.sendcommand("TEXT 290," + y + ",\"Poppins.TTF\",0,8,8,\"" + "TR" + "\"\n");
//        TscDll.sendcommand("TEXT 280,"+y+",\"Poppins.TTF\",0,8,8,\""+"Ex"+"\"\n");
                TscDll.sendcommand("TEXT 350," + y + ",\"Poppins.TTF\",0,8,8,\"" + "R+" + "\"\n");
                TscDll.sendcommand("TEXT 430," + y + ",\"Bold.TTF\",0,8,8,\"" + "Out" + "\"\n");
                TscDll.sendcommand("TEXT 500," + y + ",\"Bold.TTF\",0,8,8,\"" + "Bal" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                for (ReportStockSummaryModel.ReportStockSummaryDetails reportStockSummaryDetails : reportStockSummaryModels.get(0).getReportStockSummaryDetailsList()) {

                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + reportStockSummaryDetails.getProductName() + " " + reportStockSummaryDetails.getUomCode() + "\"\n");

                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + (int) Double.parseDouble(reportStockSummaryDetails.getOpenQty()) + "\"\n");
                    TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + (int) Double.parseDouble(reportStockSummaryDetails.getIn()) + "\"\n");
                    TscDll.sendcommand("TEXT 130," + y + ",\"Poppins.TTF\",0,8,8,\"" + (int) Double.parseDouble(reportStockSummaryDetails.getSalesQty()) + "\"\n");
                    TscDll.sendcommand("TEXT 230," + y + ",\"Poppins.TTF\",0,8,8,\"" + (int) Double.parseDouble(reportStockSummaryDetails.getFocQty()) + "\"\n");
                    TscDll.sendcommand("TEXT 290," + y + ",\"Poppins.TTF\",0,8,8,\"" + (int) Double.parseDouble(reportStockSummaryDetails.getOtherInorOut()) + "\"\n");
//            TscDll.sendcommand("TEXT 280,"+y+",\"Bold.TTF\",0,8,8,\""+reportStockSummaryDetails.getEx()+"\"\n");
                    TscDll.sendcommand("TEXT 350," + y + ",\"Poppins.TTF\",0,8,8,\"" + (int) Double.parseDouble(reportStockSummaryDetails.getRtnQty()) + "\"\n");
                    TscDll.sendcommand("TEXT 430," + y + ",\"Poppins.TTF\",0,8,8,\"" + (int) Double.parseDouble(reportStockSummaryDetails.getOut()) + "\"\n");
                    TscDll.sendcommand("TEXT 500," + y + ",\"Poppins.TTF\",0,8,8,\"" + (int) Double.parseDouble(reportStockSummaryDetails.getBalance()) + "\"\n");

                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += LINE_SPACING + 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "--------------" + "\"\n");
                TscDll.sendcommand("TEXT 350," + y + ",\"Poppins.TTF\",0,8,8,\"" + "-----------------" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Sales Person" + "\"\n");
                TscDll.sendcommand("TEXT 360," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Warehouse Admin" + "\"\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);

            }
        }, 100);
    }


    public void printCustomerOutstanding(int copy, ArrayList<CustomerStateModel> customerStateModels, String date) throws IOException {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);

                int y = 0;
                height = 70;
                int totalheight = 0;
                if(customerStateModels.get(0).getCustInvoiceDetailsARList().size() >0){
                    totalheight = 30 ;
                }
                finalHeight = height + (customerStateModels.get(0).getCustInvoiceDetailList().size() * 5)
                        +(customerStateModels.get(0).getCustInvoiceDetailsARList().size() * 5)+totalheight+30;
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                try {
                    String filePath = Constants.getSignatureFolderPath(context);
                    String fileName = "Logo.jpg";
                    File mFile = new File(filePath, fileName);
                    if (mFile.exists()) {
                        y += LINE_SPACING;
                        TscDll.sendpicture_resize(180, y, mFile.getAbsolutePath(), 230, 200);
                        y += 170;
                    }
                } catch (Exception e) {
                }

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + "Customer Outstanding Report" + "\"\n");


                y += LINE_SPACING + 10;
                // Define the Box
                //TscDll.sendcommand("BOX 0,"+y+",570,0,2\n");
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "DATE: " + date + "\"\n");
                //  TscDll.sendcommand("TEXT 330,"+y+",\"Poppins.TTF\",0,8,8,\""+"TO DATE: "+toDate+"\"\n");

                y += 30;
                if (customerStateModels.get(0).getCustomerName().length() <= 45) {
                    TscDll.sendcommand("TEXT 00," + y + ",\"Poppins.TTF\",0,8,8,\"" + "CUST :" + customerStateModels.get(0).getCustomerName() + "\"\n\n");
                } else {
                    String firstname = customerStateModels.get(0).getCustomerName().substring(0, 42);
                    String secondname = customerStateModels.get(0).getCustomerName().substring(42);

                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + firstname + "\"\n\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "CUST :" + secondname + "\"\n\n");
                }

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 10," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Invoice No" + "\"\n");
                TscDll.sendcommand("TEXT 180," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Invoice Date" + "\"\n");
                TscDll.sendcommand("TEXT 340," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Net Total" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Balance" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                int index = 1;
                double sum_netbalance = 0.0;
                double sum_nettotal = 0.0;
                double sum_netbalance1 = 0.0;
                double sum_nettotal1 = 0.0;
                double sum_netbalanceFinal = 0.0;
                double sum_nettotalFinal = 0.0;

                for (CustomerStateModel.CustInvoiceDetails custInvoiceDetails :
                        customerStateModels.get(0).getCustInvoiceDetailList()) {
                    y += 35;
                    TscDll.sendcommand("TEXT 10," + y + ",\"Poppins.TTF\",0,8,8,\"" + custInvoiceDetails.getInvoiceNumber() + "\"\n");
                    TscDll.sendcommand("TEXT 180," + y + ",\"Poppins.TTF\",0,8,8,\"" + custInvoiceDetails.getInvoiceDate() + "\"\n");
                    TscDll.sendcommand("TEXT 340," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(custInvoiceDetails.getNetTotal())) + "\"\n");
                    TscDll.sendcommand("TEXT 450," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(custInvoiceDetails.getBalanceAmount())) + "\"\n");

                    sum_netbalance += Double.parseDouble(custInvoiceDetails.getBalanceAmount());
                    sum_nettotal += Double.parseDouble(custInvoiceDetails.getNetTotal());

                    index++;
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,8,8,\"" + "Total :($) " + "\"\n");
                TscDll.sendcommand("TEXT 330," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(sum_nettotal) + "\"\n");
                TscDll.sendcommand("TEXT 440," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(sum_netbalance) + "\"\n");

                if(customerStateModels.get(0).getCustInvoiceDetailsARList().size() > 0){

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                    y += 20;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "AR Credit Statement" + "\"\n");


                    for (CustomerStateModel.CustInvoiceDetailsAR custInvoiceDetailsAR : customerStateModels.get(0).getCustInvoiceDetailsARList()) {
                        y += 35;
                        TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + custInvoiceDetailsAR.getInvoiceNumber() + "\"\n");
                        TscDll.sendcommand("TEXT 180," + y + ",\"Poppins.TTF\",0,8,8,\"" + custInvoiceDetailsAR.getInvoiceDate() + "\"\n");
                        TscDll.sendcommand("TEXT 340," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(custInvoiceDetailsAR.getNetTotal())) + "\"\n");
                        TscDll.sendcommand("TEXT 450," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(custInvoiceDetailsAR.getBalanceAmount())) + "\"\n");

                        sum_netbalance1 += Double.parseDouble(custInvoiceDetailsAR.getBalanceAmount());
                        sum_nettotal1 += Double.parseDouble(custInvoiceDetailsAR.getNetTotal());

                        index++;
                    }
                    sum_netbalanceFinal = sum_nettotal - sum_nettotal1;
                    sum_nettotalFinal = sum_netbalance - sum_netbalance1;
                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    y += 20;
                    TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,8,8,\"" + "Total :($) " + "\"\n");
                    TscDll.sendcommand("TEXT 330," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(sum_nettotal1) + "\"\n");
                    TscDll.sendcommand("TEXT 440," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(sum_netbalance1) + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    y += 20;
                    TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,8,8,\"" + "Net Total :($) " + "\"\n");
                    TscDll.sendcommand("TEXT 330," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(sum_netbalanceFinal) + "\"\n");
                    TscDll.sendcommand("TEXT 440," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(sum_nettotalFinal) + "\"\n");
                }

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);
            }
        }, 100);
    }

    public void printReportSalesSummary(int copy, String fromDate, String toDate, ArrayList<ReportSalesSummaryModel> reportSalesSummaryModels)
            throws IOException {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);

                int y = 0;
                height = 110;

                int salesHeight = 0;
                int invoiceHeight = 0;
                if (reportSalesSummaryModels.get(0).getReportSalesSummaryDetailsList() != null && reportSalesSummaryModels.get(0).getReportSalesSummaryDetailsList().size() > 0) {
                    salesHeight = reportSalesSummaryModels.get(0).getReportSalesSummaryDetailsList().size() * 15;
                }

                if (reportSalesSummaryModels.get(0).getReportSalesSummaryInvDetailsList() != null && reportSalesSummaryModels.get(0).getReportSalesSummaryInvDetailsList().size() > 0) {
                    invoiceHeight = reportSalesSummaryModels.get(0).getReportSalesSummaryInvDetailsList().size() * 15;
                }
                finalHeight = height + (salesHeight) + (invoiceHeight) + 40;
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media

                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO : " + company_gst + "\"\n");
                }
                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + "Sales Summary" + "\"\n");


                y += LINE_SPACING + 10;
                // Define the Box
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "FromDate: " + fromDate + "\"\n");
                TscDll.sendcommand("TEXT 350," + y + ",\"Poppins.TTF\",0,8,8,\"" + "ToDate: " + toDate + "\"\n");
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Sales person: " + username + "\"\n");
                if (reportSalesSummaryModels.get(0).getReportSalesSummaryDetailsList() != null && reportSalesSummaryModels.get(0).getReportSalesSummaryDetailsList().size() > 0) {

                    y += LINE_SPACING + 20;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,10,10,\"" + "Cash Sales" + "\"\n");

                    y += 30;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    y += 20;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "NO" + "\"\n");
                    TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Invoice No" + "\"\n");
                    TscDll.sendcommand("TEXT 250," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Amount" + "\"\n");
                    TscDll.sendcommand("TEXT 430," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Type" + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    int index = 1;
                    double sum_salesummaryamt = 0.0;
                    double sum_invsummarybalance = 0.0;
                    double sum_invsummaryamt = 0.0;

                    for (ReportSalesSummaryModel.ReportSalesSummaryDetails reportSalesSummaryDetails :
                            reportSalesSummaryModels.get(0).getReportSalesSummaryDetailsList()) {

                        y += LINE_SPACING;
                        TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + index + "\"\n");
                        if (reportSalesSummaryDetails.getCustomer().length() <= 45) {
                            TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + reportSalesSummaryDetails.getCustomer() + "\"\n\n");
                        } else {
                            String firstname = reportSalesSummaryDetails.getCustomer().substring(0, 42);
                            String secondname = reportSalesSummaryDetails.getCustomer().substring(42);

                            TscDll.sendcommand("TEXT 60," + y + ",\"Bold.TTF\",0,8,8,\"" + firstname + "\"\n\n");
                            y += 30;
                            TscDll.sendcommand("TEXT 60," + y + ",\"Bold.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                        }

                        y += 30;
                        TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + reportSalesSummaryDetails.getTransNo() + "\"\n");
                        TscDll.sendcommand("TEXT 250," + y + ",\"Bold.TTF\",0,8,8,\"" + ("$   " + reportSalesSummaryDetails.getAmount()) + "\"\n");
                        if(reportSalesSummaryDetails.getType().equalsIgnoreCase("CS")) {
                            TscDll.sendcommand("TEXT 420," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Cash Sales" + "\"\n");
                        }
                        else{
                            TscDll.sendcommand("TEXT 420," + y + ",\"Poppins.TTF\",0,8,8,\"" + reportSalesSummaryDetails.getType() + "\"\n");
                        }
                        y += 30;
                        TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,7,7,\"" + reportSalesSummaryDetails.getPaymentDate() + "\"\n");

                        sum_salesummaryamt += Double.parseDouble(reportSalesSummaryDetails.getAmount());
                        index++;
                    }
                    y += LINE_SPACING + 20;
                    TscDll.sendcommand("TEXT 5," + y + ",\"Bold.TTF\",0,7,7,\"" + "Total Received " + ("$  " + twoDecimalPoint(sum_salesummaryamt)) + "\"\n");
                }
                if (reportSalesSummaryModels.get(0).getReportSalesSummaryInvDetailsList() != null && reportSalesSummaryModels.get(0).getReportSalesSummaryInvDetailsList().size() > 0) {

                    y += 30;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    y += 20;
                    TscDll.sendcommand("TEXT 5," + y + ",\"Bold.TTF\",0,10,10,\"" + "Invoice " + "\"\n");

                    y += 30;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "NO" + "\"\n");
                    TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + "TransNo" + "\"\n");
                    TscDll.sendcommand("TEXT 450," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Amount" + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    int invoiceindex = 1;
                    double sum_invsummaryamt = 0.0;
                    double sum_invsummarybalance = 0.0;
                    for (ReportSalesSummaryModel.ReportSalesSummaryInvDetails reportSalesSummaryInvDetails : reportSalesSummaryModels.get(0).getReportSalesSummaryInvDetailsList()) {

                        y += LINE_SPACING;
                        TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + invoiceindex + "\"\n");
                        if (reportSalesSummaryInvDetails.getCustomer().length() <= 45) {
                            TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + reportSalesSummaryInvDetails.getCustomer() + "\"\n\n");
                        } else {
                            String firstname = reportSalesSummaryInvDetails.getCustomer().substring(0, 42);
                            String secondname = reportSalesSummaryInvDetails.getCustomer().substring(42);

                            TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + firstname + "\"\n\n");
                            y += 30;
                            TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                        }

                        y += 30;
                        TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + reportSalesSummaryInvDetails.getTransNo() + "\"\n");
                        TscDll.sendcommand("TEXT 430," + y + ",\"Bold.TTF\",0,8,8,\"" + ("$   " + Utils.twoDecimalPoint(Double.parseDouble(reportSalesSummaryInvDetails.getAmount()))) + "\"\n");
                        y += 30;
                        TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Balance " + ("$  " + Utils.twoDecimalPoint(Double.parseDouble(reportSalesSummaryInvDetails.getBalanceAmount()))) + "\"\n");

                        sum_invsummaryamt += Double.parseDouble(reportSalesSummaryInvDetails.getAmount());
                        sum_invsummarybalance += Double.parseDouble(reportSalesSummaryInvDetails.getBalanceAmount());

                        invoiceindex++;
                    }
                    y += LINE_SPACING + 20;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,7,7,\"" + "Total Received " + ("$  " + twoDecimalPoint(sum_invsummaryamt)) + "\"\n");
                    TscDll.sendcommand("TEXT 380," + y + ",\"Bold.TTF\",0,7,7,\"" + "Balance " + ("$  " + twoDecimalPoint(sum_invsummarybalance)) + "\"\n");

                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Total Sales:$" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + (Utils.twoDecimalPoint(Double.parseDouble(reportSalesSummaryModels.get(0).getTotalSales()))) + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Total Cash Received:$" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + (Utils.twoDecimalPoint(Double.parseDouble(reportSalesSummaryModels.get(0).getCashReceived()))) + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Total Refunded:$" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + (Utils.twoDecimalPoint(Double.parseDouble(reportSalesSummaryModels.get(0).getRefunded()))) + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Total Cash In Hand:$" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + (Utils.twoDecimalPoint(Double.parseDouble(reportSalesSummaryModels.get(0).getCashInHand()))) + "\"\n");

                y += LINE_SPACING + 30;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += LINE_SPACING + 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------" + "\"\n");
                TscDll.sendcommand("TEXT 420," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Report By" + "\"\n");
                TscDll.sendcommand("TEXT 430," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Check By" + "\"\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);
            }
        }, 100);
    }

    public void printSalesOrder(int copy, ArrayList<SalesOrderPrintPreviewModel> salesOrderHeaderDetails,
                                ArrayList<SalesOrderPrintPreviewModel.SalesList> salesOrderList) throws IOException {

//        try {
//            if (TscDll.openport(macAddress).equals("-1")) {
//                Toast.makeText(context, "Printer not connected", Toast.LENGTH_SHORT).show();
//            } else {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);

                //String status = TscDll.printerstatus(300);
                int y = 0;
                height = 60;

                if (showSignature.equals("true")) {
                    String filePath2 = Constants.getSignatureFolderPath(context);
                    String fileName2 = "Signature.jpg";
                    File mFile2 = new File(filePath2, fileName2);
                    if (mFile2.exists()) {
                        height += 20;
                    }
                }

                if (showLogo.equals("true")) {
                    try {
                        String filePath = Constants.getSignatureFolderPath(context);
                        String fileName = "Logo.jpg";
                        File mFile = new File(filePath, fileName);
                        if (mFile.exists()) {
                            height += 30;
                        }
                    } catch (Exception e) {
                    }
                }
                if (payNow != null && !payNow.isEmpty()) {
                    invoivePaynowHeight = 30;
                }

                finalHeight = height + (salesOrderList.size() * 10) + invoiveSubTotalHeight + invoiceBottomLine + invoivePaynowHeight;
                ;

//                finalHeight = height + 30 + (invoiceList.size() * invoiceLineHeight) + invoiveReturnHeight
//                        + invoiveSubTotalHeight + invoiceBottomLine;

                Log.w("ActualFinalHeight::", finalHeight + "");
//                finalHeight = getPrintSize(finalHeight, showLogo, showQrCode, salesOrderHeaderDetails.get(0).getOutStandingAmount()
//                        , showSignature, showStamp, "false");

                //finalHeight=height+(salesOrderList.size() * 10)+10;
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 8\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                if (showLogo.equals("true")) {
                    try {
                        String filePath = Constants.getSignatureFolderPath(context);
                        String fileName = "Logo.jpg";
                        File mFile = new File(filePath, fileName);
                        if (mFile.exists()) {
                            y += LINE_SPACING;
                            TscDll.sendpicture_resize(180, y, mFile.getAbsolutePath(), 230, 200);
                            y += 170;
                        }
                    } catch (Exception e) {
                    }
                }

                StringBuilder temp = new StringBuilder();
                if (company_name.length() > 38) {
                    temp.append((company_name.length() > 38) ? company_name.substring(0, 37) : company_name);
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                    String companyname = company_name.substring(37);
                    temp.append((companyname.length() > 37) ? companyname.substring(0, 36) : companyname);
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + temp.toString() + "\"\n\n");
                } else {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                }

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO  : " + company_gst + "\"\n");
                }

                if (company_code.equals("SUPERSTAR TRADERS PTE LTD")) {
                    y += LINE_SPACING + 10;
                    if (Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax()) > 0) {
                        TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,10,10,\"" + "Tax Invoice" + "\"\n");
                    } else {
                        TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,10,10,\"" + "Invoice" + "\"\n");
                    }
                } else {
                    y += LINE_SPACING + 10;
                    TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,10,10,\"" + "Sales Order" + "\"\n");
                }

                y += LINE_SPACING + 10;
                // Define the Box
                //TscDll.sendcommand("BOX 0,"+y+",570,0,2\n");
                TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,9,9,\"" + "No: " + salesOrderHeaderDetails.get(0).getSoNumber() + "\"\n");
                TscDll.sendcommand("TEXT 400," + y + ",\"Bold.TTF\",0,8,8,\"" + salesOrderHeaderDetails.get(0).getSoDate() + "\"\n");
                y += LINE_SPACING;
                if (salesOrderHeaderDetails.get(0).getCustomerName().length() <= 45) {
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + "CUST: " + salesOrderHeaderDetails.get(0).getCustomerName() + "\"\n\n");
                } else {
                    String firstname = salesOrderHeaderDetails.get(0).getCustomerName().substring(0, 42);
                    String secondname = salesOrderHeaderDetails.get(0).getCustomerName().substring(42);

                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + "CUST: " + firstname + "\"\n\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                }
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "CODE: " + salesOrderHeaderDetails.get(0).getCustomerCode() + "\"\n");

//                        if (salesOrderHeaderDetails.get(0).getDeliveryAddress() != null &&
//                                !salesOrderHeaderDetails.get(0).getDeliveryAddress().isEmpty()) {
//
//                            if (salesOrderHeaderDetails.get(0).getAllowDeliveryAddress() != null &&
//                                    !salesOrderHeaderDetails.get(0).getAllowDeliveryAddress().isEmpty() &&
//                                    salesOrderHeaderDetails.get(0).getAllowDeliveryAddress().equalsIgnoreCase("Yes")) {
//
//                                if (salesOrderHeaderDetails.get(0).getDeliveryAddress().length() <= 38) {
//                                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + "Delivery Addr: " + salesOrderHeaderDetails.get(0).getDeliveryAddress() + "\"\n\n");
//                                } else {
//                                    String firstname = salesOrderHeaderDetails.get(0).getDeliveryAddress().substring(0, 35);
//                                    String secondname = salesOrderHeaderDetails.get(0).getDeliveryAddress().substring(35);
//
//                                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + "Delivery Addr: " + firstname + "\"\n\n");
//                                    y += 30;
//                                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + secondname + "\"\n\n");
//                                }
//                            }
//                        }

                if (!salesOrderHeaderDetails.get(0).getAddress().isEmpty()) {
                    y += LINE_SPACING;
                    if (salesOrderHeaderDetails.get(0).getAddress().length() <= 45) {
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "ADDR: " + salesOrderHeaderDetails.get(0).getAddress() + "\"\n\n");
                    } else {
                        String address1 = salesOrderHeaderDetails.get(0).getAddress().substring(0, 42);
                        String address2 = salesOrderHeaderDetails.get(0).getAddress().substring(42);

                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "ADDR: " + address1 + "\"\n\n");
                        y += 30;
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + address2 + "\"\n\n");
                    }
                }

                if (salesOrderHeaderDetails.get(0).getAddress1() != null &&
                        !salesOrderHeaderDetails.get(0).getAddress1().isEmpty()) {
                    y += 30;
                    Log.w("Address1_Value:", salesOrderHeaderDetails.get(0).getAddress1());
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\""  +salesOrderHeaderDetails.get(0).getAddress1() + "\"\n");
                }

                if (salesOrderHeaderDetails.get(0).getAddress2() != null &&
                        !salesOrderHeaderDetails.get(0).getAddress2().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + salesOrderHeaderDetails.get(0).getAddress2() + "\"\n\n");
                }

                if (salesOrderHeaderDetails.get(0).getAddress3() != null &&
                        !salesOrderHeaderDetails.get(0).getAddress3().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + salesOrderHeaderDetails.get(0).getAddress3() + "\"\n");
                }
                if (salesOrderHeaderDetails.get(0).getAddressstate() != null &&
                        !salesOrderHeaderDetails.get(0).getAddressstate().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + salesOrderHeaderDetails.get(0).getAddressstate() + "\"\n");
                }
                if (salesOrderHeaderDetails.get(0).getAddresssZipcode() != null &&
                        !salesOrderHeaderDetails.get(0).getAddresssZipcode().isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + salesOrderHeaderDetails.get(0).getAddresssZipcode() + "\"\n");
                }
                if (showUserName.equals("true")) {
                    y += 30;
                    TscDll.sendcommand("TEXT 400," + y + ",\"Poppins.TTF\",0,8,8,\"" + "User: " + username + "\"\n");
                }

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SNO" + "\"\n");
                TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + "PRODUCT" + "\"\n");
                TscDll.sendcommand("TEXT 280," + y + ",\"Poppins.TTF\",0,8,8,\"" + "QTY" + "\"\n");
                TscDll.sendcommand("TEXT 380," + y + ",\"Poppins.TTF\",0,8,8,\"" + "PRICE" + "\"\n");
                TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,8,8,\"" + "TOTAL" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                int index = 1;
                for (SalesOrderPrintPreviewModel.SalesList salesOrder : salesOrderList) {
                    y += 30;
                    TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + index + "\"\n");
                    String productName = "";
                    String uomcode = "";
                    if (salesOrder.getUomCode() != null && !salesOrder.getUomCode().isEmpty() && !salesOrder.getUomCode().equals("null")) {
                        uomcode = "(" + salesOrder.getUomCode() + ")";
                    }
                    if (Double.parseDouble(twoDecimalPoint(Double.parseDouble(salesOrder.getTotal()))) == 0.00) {
                        productName = salesOrder.getDescription() + "-(as FOC)"+"(PCS)";
                    } else if (Double.parseDouble(twoDecimalPoint(Double.parseDouble(salesOrder.getTotal()))) > 0.00) {
                        if (showUom.equals("true")) {
                            productName = salesOrder.getDescription() + uomcode;
                        }else{
                            productName = salesOrder.getDescription();
                        }
                    } else {
                        productName = salesOrder.getDescription() + "-(as RTN)"+"(PCS)";
                    }

                    TscDll.sendcommand("TEXT 50," + y + ",\"Bold.TTF\",0,8,8,\"" + productName + "\"\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 290," + y + ",\"Poppins.TTF\",0,9,9,\"" + (int) Double.parseDouble(salesOrder.getNetQty()) + "\"\n");
                    if(shortCodeStr.equalsIgnoreCase("FUXIN")) {

                        TscDll.sendcommand("TEXT 390," + y + ",\"Poppins.TTF\",0,9,9,\"" +
                                fourDecimalPoint(Double.parseDouble(salesOrder.getPricevalue()))+ "\"\n");

                        TscDll.sendcommand("TEXT 490," + y + ",\"Poppins.TTF\",0,9,9,\"" +
                                fourDecimalPoint(Double.parseDouble(salesOrder.getTotal())) + "\"\n");
                    }else{
                        if(shortCodeStr.equalsIgnoreCase("SUPERSTAR")) {

                            TscDll.sendcommand("TEXT 390," + y + ",\"Poppins.TTF\",0,9,9,\"" +
                                    fourDecimalPoint(Double.parseDouble(salesOrder.getPricevalue())) + "\"\n");
                            TscDll.sendcommand("TEXT 490," + y + ",\"Poppins.TTF\",0,9,9,\"" +
                                    twoDecimalPoint(Double.parseDouble(salesOrder.getTotal())) + "\"\n");
                        }else{
                            TscDll.sendcommand("TEXT 390," + y + ",\"Poppins.TTF\",0,9,9,\"" + salesOrder.getPricevalue() + "\"\n");

                            TscDll.sendcommand("TEXT 490," + y + ",\"Poppins.TTF\",0,9,9,\"" +
                                    twoDecimalPoint(Double.parseDouble(salesOrder.getTotal())) + "\"\n");
                        }

                    }
                    index++;
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                if (company_code.equals("SUPERSTAR TRADERS PTE LTD")) {
                    y += 20;
                    TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SUB TOTAL : $ " + "\"\n");
                    TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal())) + "\"\n");

                    y += LINE_SPACING;
                    // TscDll.sendcommand("TEXT 200,"+y+",\"Poppins.TTF\",0,9,9,\""+"GST("+salesOrderHeaderDetails.get(0).getTaxType()+":"+ (int)Double.parseDouble(salesOrderHeaderDetails.get(0).getTaxValue())+" % ):$"+"\"\n");
                    // TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,8,8,\"" + "GST : $ " + "\"\n");
                    TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,9,9,\"" + "GST(" + salesOrderHeaderDetails.get(0).getTaxType() + ":" + (int) Double.parseDouble(salesOrderHeaderDetails.get(0).getTaxValue()) + " % ):$" + "\"\n");
                    TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax())) + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,8,8,\"" + "GRAND TOTAL : $ " + "\"\n");
                    TscDll.sendcommand("TEXT 480," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTotal())) + "\"\n");
                } else {
                    if (shortCodeStr.equalsIgnoreCase("FUXIN")) {
                        y += 20;
                        TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,9,9,\"" + "SUB TOTAL:$" + "\"\n");
                        TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,9,9,\"" + fourDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal())) + "\"\n");

                        y += LINE_SPACING;
                        TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,9,9,\"" + "GST(" + salesOrderHeaderDetails.get(0).getTaxType() + ":" + (int) Double.parseDouble(salesOrderHeaderDetails.get(0).getTaxValue()) + " % ):$" + "\"\n");
                        TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,9,9,\"" + fourDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax())) + "\"\n");

                        y += LINE_SPACING;
                        TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,9,9,\"" + "GRAND TOTAL:$" + "\"\n");
                        TscDll.sendcommand("TEXT 480," + y + ",\"Bold.TTF\",0,9,9,\"" + fourDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTotal())) + "\"\n");
                    }else{
                        y += 20;
                        TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,9,9,\"" + "SUB TOTAL:$" + "\"\n");
                        TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,9,9,\"" + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal())) + "\"\n");

                        y += LINE_SPACING;
                        TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,9,9,\"" + "GST(" + salesOrderHeaderDetails.get(0).getTaxType() + ":" + (int) Double.parseDouble(salesOrderHeaderDetails.get(0).getTaxValue()) + " % ):$" + "\"\n");
                        TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,9,9,\"" + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax())) + "\"\n");

                        y += LINE_SPACING;
                        TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,9,9,\"" + "GRAND TOTAL:$" + "\"\n");
                        TscDll.sendcommand("TEXT 480," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTotal())) + "\"\n");
                    }
                }

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                if (salesOrderHeaderDetails.get(0).getOutStandingAmount() != null && Double.parseDouble(salesOrderHeaderDetails.get(0).getOutStandingAmount()) > 0.00) {
                    y += 20;
                    if (shortCodeStr.equalsIgnoreCase("FUXIN")) {
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"TOTAL OUTSTANDING: " + fourDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getOutStandingAmount())) + "\"\n");
                    }else{
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"TOTAL OUTSTANDING: " + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getOutStandingAmount())) + "\"\n");
                    }
                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                }

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Customer Signature & Company Stamp" + "\"\n");

                if (showSignature.equals("true")) {
                    try {
                        if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
                            y += LINE_SPACING;
                            String filePath = Constants.getSignatureFolderPath(context);
                            String fileName = "Signature.jpg";
                            File mFile = new File(filePath, fileName);
                            if (mFile.exists()) {
                                TscDll.sendpicture_resize(0, y, mFile.getAbsolutePath(), 400, 80);
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                y += 150;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------------------------------------------------" + "\"\n");

                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "RECEIVED ABOVE GOODS IN GOOD ORDER AND CONDITION" + "\"\n");

                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------------------------------------------------" + "\"\n");


                //  y += LINE_SPACING;
                //  TscDll.sendcommand("BAR 0,"+y+",800,2\n");

                if (payNow != null && !payNow.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,8,8,2,\" PAYNOW : " + payNow + "\"\n");
                }
                if (bankCode != null && !bankCode.isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,8,8,2,\" BANK : " + bankCode + "\"\n");
                }
                if (cheque != null && !cheque.isEmpty()) {
                    y += 30;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,8,8,2,\" CHEQUE : " + cheque + "\"\n");
                }

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);
            }
        }, 100);
//            }
//        }catch (Exception e){
//
//        }
    }

    public void printPurchaseInvoice(int copy, ArrayList<SalesOrderPrintPreviewModel> salesOrderHeaderDetails,
                                ArrayList<SalesOrderPrintPreviewModel.SalesList> salesOrderList) throws IOException {

//        try {
//            if (TscDll.openport(macAddress).equals("-1")) {
//                Toast.makeText(context, "Printer not connected", Toast.LENGTH_SHORT).show();
//            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TscDll.openport(macAddress);

                        //String status = TscDll.printerstatus(300);
                        int y = 0;
                        height = 60;

                        if (showSignature.equals("true")) {
                            String filePath2 = Constants.getSignatureFolderPath(context);
                            String fileName2 = "Signature.jpg";
                            File mFile2 = new File(filePath2, fileName2);
                            if (mFile2.exists()) {
                                height += 20;
                            }
                        }

                        if (showLogo.equals("true")) {
                            try {
                                String filePath = Constants.getSignatureFolderPath(context);
                                String fileName = "Logo.jpg";
                                File mFile = new File(filePath, fileName);
                                if (mFile.exists()) {
                                    height += 30;
                                }
                            } catch (Exception e) {
                            }
                        }
                        if (payNow != null && !payNow.isEmpty()) {
                            invoivePaynowHeight = 30;
                        }

                        finalHeight = height + (salesOrderList.size() * 10) + invoiveSubTotalHeight
                                + invoiceBottomLine + invoivePaynowHeight;


//                finalHeight = height + 30 + (invoiceList.size() * invoiceLineHeight) + invoiveReturnHeight
//                        + invoiveSubTotalHeight + invoiceBottomLine;

                        Log.w("ActualFinalHeight::", finalHeight + "");
//                finalHeight = getPrintSize(finalHeight, showLogo, showQrCode, salesOrderHeaderDetails.get(0).getOutStandingAmount()
//                        , showSignature, showStamp, "false");

                        //finalHeight=height+(salesOrderList.size() * 10)+10;
                        TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                        TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                        TscDll.clearbuffer();
                        TscDll.sendcommand("SPEED 4\r\n");
                        TscDll.sendcommand("DENSITY 8\r\n");
                        TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                        TscDll.sendcommand("SET TEAR ON\r\n");
                        TscDll.sendcommand("SET COUNTER @1 1\r\n");
                        TscDll.sendcommand("@1 = \"0001\"\r\n");

                        if (showLogo.equals("true")) {
                            try {
                                String filePath = Constants.getSignatureFolderPath(context);
                                String fileName = "Logo.jpg";
                                File mFile = new File(filePath, fileName);
                                if (mFile.exists()) {
                                    y += LINE_SPACING;
                                    TscDll.sendpicture_resize(180, y, mFile.getAbsolutePath(), 230, 200);
                                    y += 170;
                                }
                            } catch (Exception e) {
                            }
                        }

                        StringBuilder temp = new StringBuilder();
                        if (company_name.length() > 38) {
                            temp.append((company_name.length() > 38) ? company_name.substring(0, 37) : company_name);
                            y += LINE_SPACING;
                            TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                            String companyname = company_name.substring(37);
                            temp.append((companyname.length() > 37) ? companyname.substring(0, 36) : companyname);
                            TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + temp.toString() + "\"\n\n");
                        } else {
                            y += LINE_SPACING;
                            TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                        }

                        if (company_address1 != null && !company_address1.isEmpty()) {
                            y += LINE_SPACING;
                            TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                        }

                        if (company_address2 != null && !company_address2.isEmpty()) {
                            y += LINE_SPACING;
                            TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                        }

                        if (company_address3 != null && !company_address3.isEmpty()) {
                            y += LINE_SPACING;
                            TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                        }

                        if (company_phone != null && !company_phone.isEmpty()) {
                            y += LINE_SPACING;
                            TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                        }

                        if (company_gst != null && !company_gst.isEmpty()) {
                            y += LINE_SPACING;
                            TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO  : " + company_gst + "\"\n");
                        }

//                        if (company_code.equals("SUPERSTAR TRADERS PTE LTD")) {
//                            y += LINE_SPACING + 10;
//                            if (Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax()) > 0) {
//                                TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,10,10,\"" + "Tax Invoice" + "\"\n");
//                            } else {
//                                TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,10,10,\"" + "Invoice" + "\"\n");
//                            }
//                        } else {
                            y += LINE_SPACING + 10;
                            TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,10,10,\"" + "Purchase Invoice" + "\"\n");
                      //  }

                        y += LINE_SPACING + 10;
                        // Define the Box
                        //TscDll.sendcommand("BOX 0,"+y+",570,0,2\n");
                        TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,9,9,\"" + "No: " + salesOrderHeaderDetails.get(0).getSoNumber() + "\"\n");
                        TscDll.sendcommand("TEXT 400," + y + ",\"Bold.TTF\",0,10,10,\"" + salesOrderHeaderDetails.get(0).getSoDate() + "\"\n");
                        y += LINE_SPACING;
                        if (salesOrderHeaderDetails.get(0).getCustomerName().length() <= 45) {
                            TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,10,10,\"" + "CUST: " + salesOrderHeaderDetails.get(0).getCustomerName() + "\"\n\n");
                        } else {
                            String firstname = salesOrderHeaderDetails.get(0).getCustomerName().substring(0, 42);
                            String secondname = salesOrderHeaderDetails.get(0).getCustomerName().substring(42);

                            TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,10,10,\"" + "CUST: " + firstname + "\"\n\n");
                            y += 30;
                            TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,10,10,\"" + secondname + "\"\n\n");
                        }
                        y += 30;
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "CODE: " + salesOrderHeaderDetails.get(0).getCustomerCode() + "\"\n");

//                        if (salesOrderHeaderDetails.get(0).getDeliveryAddress() != null &&
//                                !salesOrderHeaderDetails.get(0).getDeliveryAddress().isEmpty()) {
//
//                            if (salesOrderHeaderDetails.get(0).getAllowDeliveryAddress() != null &&
//                                    !salesOrderHeaderDetails.get(0).getAllowDeliveryAddress().isEmpty() &&
//                                    salesOrderHeaderDetails.get(0).getAllowDeliveryAddress().equalsIgnoreCase("Yes")) {
//
//                                if (salesOrderHeaderDetails.get(0).getDeliveryAddress().length() <= 38) {
//                                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + "Delivery Addr: " + salesOrderHeaderDetails.get(0).getDeliveryAddress() + "\"\n\n");
//                                } else {
//                                    String firstname = salesOrderHeaderDetails.get(0).getDeliveryAddress().substring(0, 35);
//                                    String secondname = salesOrderHeaderDetails.get(0).getDeliveryAddress().substring(35);
//
//                                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + "Delivery Addr: " + firstname + "\"\n\n");
//                                    y += 30;
//                                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + secondname + "\"\n\n");
//                                }
//                            }
//                        }

                        if (!salesOrderHeaderDetails.get(0).getAddress().isEmpty()) {
                            y += LINE_SPACING;
                            if (salesOrderHeaderDetails.get(0).getAddress().length() <= 45) {
                                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "ADDR: " + salesOrderHeaderDetails.get(0).getAddress() + "\"\n\n");
                            } else {
                                String address1 = salesOrderHeaderDetails.get(0).getAddress().substring(0, 42);
                                String address2 = salesOrderHeaderDetails.get(0).getAddress().substring(42);

                                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "ADDR: " + address1 + "\"\n\n");
                                y += 30;
                                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + address2 + "\"\n\n");
                            }
                        }
                        if (showUserName.equals("true")) {
                            y += 30;
                            TscDll.sendcommand("TEXT 400," + y + ",\"Poppins.TTF\",0,8,8,\"" + "User: " + username + "\"\n");
                        }
                        if (salesOrderHeaderDetails.get(0).getAddress1() != null &&
                                !salesOrderHeaderDetails.get(0).getAddress1().isEmpty()) {
                            y += 30;
                            Log.w("Address1_Value:", salesOrderHeaderDetails.get(0).getAddress1());
                            TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" +  "ADDR: " +salesOrderHeaderDetails.get(0).getAddress1() + "\"\n");
                        }

                        if (salesOrderHeaderDetails.get(0).getAddress2() != null &&
                                !salesOrderHeaderDetails.get(0).getAddress2().isEmpty()) {
                            y += 30;
                            TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + salesOrderHeaderDetails.get(0).getAddress2() + "\"\n\n");
                        }

                        if (salesOrderHeaderDetails.get(0).getAddress3() != null &&
                                !salesOrderHeaderDetails.get(0).getAddress3().isEmpty()) {
                            y += 30;
                            TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + salesOrderHeaderDetails.get(0).getAddress3() + "\"\n");
                        }
                        if (salesOrderHeaderDetails.get(0).getAddressstate() != null &&
                                !salesOrderHeaderDetails.get(0).getAddressstate().isEmpty()) {
                            y += 30;
                            TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + salesOrderHeaderDetails.get(0).getAddressstate() + "\"\n");
                        }
                        if (salesOrderHeaderDetails.get(0).getAddresssZipcode() != null &&
                                !salesOrderHeaderDetails.get(0).getAddresssZipcode().isEmpty()) {
                            y += 30;
                            TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + salesOrderHeaderDetails.get(0).getAddresssZipcode() + "\"\n");
                        }


                        y += LINE_SPACING;
                        TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                        y += 20;
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SNO" + "\"\n");
                        TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + "PRODUCT" + "\"\n");
                        TscDll.sendcommand("TEXT 280," + y + ",\"Poppins.TTF\",0,8,8,\"" + "QTY" + "\"\n");
                        TscDll.sendcommand("TEXT 380," + y + ",\"Poppins.TTF\",0,8,8,\"" + "PRICE" + "\"\n");
                        TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,8,8,\"" + "TOTAL" + "\"\n");

                        y += LINE_SPACING;
                        TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                        int index = 1;
                        for (SalesOrderPrintPreviewModel.SalesList salesOrder : salesOrderList) {
                            y += 30;
                            TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + index + "\"\n");
                            String productName = "";
                            String uomcode = "";
                            if (salesOrder.getUomCode() != null && !salesOrder.getUomCode().isEmpty() && !salesOrder.getUomCode().equals("null")) {
                                uomcode = "(" + salesOrder.getUomCode() + ")";
                            }
                            if (Double.parseDouble(twoDecimalPoint(Double.parseDouble(salesOrder.getTotal()))) == 0.00) {
                                productName = salesOrder.getDescription() + "-(as FOC)"+"(PCS)";
                            } else if (Double.parseDouble(twoDecimalPoint(Double.parseDouble(salesOrder.getTotal()))) > 0.00) {
                                if (showUom.equals("true")) {
                                    productName = salesOrder.getDescription() + uomcode;
                                }else{
                                    productName = salesOrder.getDescription();
                                }
                            } else {
                                productName = salesOrder.getDescription() + "-(as RTN)"+"(PCS)";
                            }

                            TscDll.sendcommand("TEXT 50," + y + ",\"Bold.TTF\",0,8,8,\"" + productName + "\"\n");
                            y += 30;
                            TscDll.sendcommand("TEXT 290," + y + ",\"Poppins.TTF\",0,9,9,\"" + (int) Double.parseDouble(salesOrder.getNetQty()) + "\"\n");
                            if(shortCodeStr.equalsIgnoreCase("FUXIN")) {

                                TscDll.sendcommand("TEXT 390," + y + ",\"Poppins.TTF\",0,9,9,\"" +
                                        fourDecimalPoint(Double.parseDouble(salesOrder.getPricevalue()))+ "\"\n");

                                TscDll.sendcommand("TEXT 490," + y + ",\"Poppins.TTF\",0,9,9,\"" +
                                        fourDecimalPoint(Double.parseDouble(salesOrder.getTotal())) + "\"\n");
                            }else{
                                TscDll.sendcommand("TEXT 390," + y + ",\"Poppins.TTF\",0,9,9,\"" + salesOrder.getPricevalue() + "\"\n");

                                TscDll.sendcommand("TEXT 490," + y + ",\"Poppins.TTF\",0,9,9,\"" +
                                        twoDecimalPoint(Double.parseDouble(salesOrder.getTotal())) + "\"\n");
                            }
                            index++;
                        }
                        y += LINE_SPACING;
                        TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                        if (company_code.equals("SUPERSTAR TRADERS PTE LTD")) {
                            y += 20;
                            TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SUB TOTAL : $ " + "\"\n");
                            TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal())) + "\"\n");

                            y += LINE_SPACING;
                            // TscDll.sendcommand("TEXT 200,"+y+",\"Poppins.TTF\",0,9,9,\""+"GST("+salesOrderHeaderDetails.get(0).getTaxType()+":"+ (int)Double.parseDouble(salesOrderHeaderDetails.get(0).getTaxValue())+" % ):$"+"\"\n");
                            // TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,8,8,\"" + "GST : $ " + "\"\n");
                            TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,9,9,\"" + "GST(" + salesOrderHeaderDetails.get(0).getTaxType() + ":" + (int) Double.parseDouble(salesOrderHeaderDetails.get(0).getTaxValue()) + " % ):$" + "\"\n");
                            TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax())) + "\"\n");

                            y += LINE_SPACING;
                            TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,8,8,\"" + "GRAND TOTAL : $ " + "\"\n");
                            TscDll.sendcommand("TEXT 480," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTotal())) + "\"\n");
                        } else {
                            if (shortCodeStr.equalsIgnoreCase("FUXIN")) {
                                y += 20;
                                TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,9,9,\"" + "SUB TOTAL:$" + "\"\n");
                                TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,9,9,\"" + fourDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal())) + "\"\n");

                                y += LINE_SPACING;
                                TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,9,9,\"" + "GST(" + salesOrderHeaderDetails.get(0).getTaxType() + ":" + (int) Double.parseDouble(salesOrderHeaderDetails.get(0).getTaxValue()) + " % ):$" + "\"\n");
                                TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,9,9,\"" + fourDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax())) + "\"\n");

                                y += LINE_SPACING;
                                TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,9,9,\"" + "GRAND TOTAL:$" + "\"\n");
                                TscDll.sendcommand("TEXT 480," + y + ",\"Bold.TTF\",0,9,9,\"" + fourDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTotal())) + "\"\n");
                            }else{
                                y += 20;
                                TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,9,9,\"" + "SUB TOTAL:$" + "\"\n");
                                TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,9,9,\"" + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal())) + "\"\n");

                                y += LINE_SPACING;
                                TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,9,9,\"" + "GST(" + salesOrderHeaderDetails.get(0).getTaxType() + ":" + (int) Double.parseDouble(salesOrderHeaderDetails.get(0).getTaxValue()) + " % ):$" + "\"\n");
                                TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,9,9,\"" + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax())) + "\"\n");

                                y += LINE_SPACING;
                                TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,9,9,\"" + "GRAND TOTAL:$" + "\"\n");
                                TscDll.sendcommand("TEXT 480," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTotal())) + "\"\n");
                            }
                        }

                        y += LINE_SPACING;
                        TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                        if (salesOrderHeaderDetails.get(0).getOutStandingAmount() != null && Double.parseDouble(salesOrderHeaderDetails.get(0).getOutStandingAmount()) > 0.00) {
                            y += 20;
                            if (shortCodeStr.equalsIgnoreCase("FUXIN")) {
                                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"TOTAL OUTSTANDING: " + fourDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getOutStandingAmount())) + "\"\n");
                            }else{
                                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"TOTAL OUTSTANDING: " + twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getOutStandingAmount())) + "\"\n");
                            }
                            y += LINE_SPACING;
                            TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                        }

                        y += 20;
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Customer Signature & Company Stamp" + "\"\n");

                        if (showSignature.equals("true")) {
                            try {
                                if (Utils.getSignature() != null && !Utils.getSignature().isEmpty()) {
                                    y += LINE_SPACING;
                                    String filePath = Constants.getSignatureFolderPath(context);
                                    String fileName = "Signature.jpg";
                                    File mFile = new File(filePath, fileName);
                                    if (mFile.exists()) {
                                        TscDll.sendpicture_resize(0, y, mFile.getAbsolutePath(), 400, 80);
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }

                        y += 150;
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------------------------------------------------" + "\"\n");

                        y += 30;
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "RECEIVED ABOVE GOODS IN GOOD ORDER AND CONDITION" + "\"\n");

                        y += 30;
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------------------------------------------------" + "\"\n");


                        //  y += LINE_SPACING;
                        //  TscDll.sendcommand("BAR 0,"+y+",800,2\n");

                        if (payNow != null && !payNow.isEmpty()) {
                            y += LINE_SPACING;
                            TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,8,8,2,\" PAYNOW : " + payNow + "\"\n");
                        }
                        if (bankCode != null && !bankCode.isEmpty()) {
                            y += 30;
                            TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,8,8,2,\" BANK : " + bankCode + "\"\n");
                        }
                        if (cheque != null && !cheque.isEmpty()) {
                            y += 30;
                            TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,8,8,2,\" CHEQUE : " + cheque + "\"\n");
                        }

                        TscDll.printlabel(1, copy);
                        TscDll.closeport(5000);
                    }
                }, 100);
//            }
//        }catch (Exception e){
//
//        }
    }
    private boolean isBlueToothConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

    public void printTransferDetail(int copy, String transferNo, String type, ArrayList<TransferDetailModel> transferDetailModels) throws IOException {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);

                int y = 0;
                height = 70;
                finalHeight = height + ( transferDetailModels.get(0).getTransferDetailsList().size() * list_height) + invoiceBottomLine;
                //  finalHeight = getPrintSize(finalHeight, "false", "false", "false", "false", "false", "false");

                //  finalHeight=height+(transferDetailModels.size() * 20);
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                Log.w("FinalHeightPrint:", finalHeight + ""+transferDetailModels.size());
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media

                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                StringBuilder temp = new StringBuilder();
                if (company_name.length() > 38) {
                    temp.append((company_name.length() > 38) ? company_name.substring(0, 37) : company_name);
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                    String companyname = company_name.substring(37);
                    temp.append((companyname.length() > 37) ? companyname.substring(0, 36) : companyname);
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + temp.toString() + "\"\n\n");
                } else {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                }

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 180," + y + ",\"Bold.TTF\",0,10,10,\"" + "LOADING SHEET" + "\"\n");
                y += 30;
                TscDll.sendcommand("TEXT 210," + y + ",\"Bold.TTF\",0,9,9,\"" + "(" + type + ")" + "\"\n");

                y += LINE_SPACING + 10;
                // Define the Box
                TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,9,9,\"" + "No: " + transferNo + "\"\n");
                TscDll.sendcommand("TEXT 400," + y + ",\"Bold.TTF\",0,7,7,\"" + transferDetailModels.get(0).getDate() + "\"\n");
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "From Location: " + transferDetailModels.get(0).getFromLocation() + "\"\n");
                TscDll.sendcommand("TEXT 330," + y + ",\"Poppins.TTF\",0,8,8,\"" + "To Location: " + transferDetailModels.get(0).getToLocation() + "\"\n");
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + transferDetailModels.get(0).getFromLocationName() + "\"\n");
                TscDll.sendcommand("TEXT 330," + y + ",\"Poppins.TTF\",0,8,8,\"" + transferDetailModels.get(0).getToLocationName() + "\"\n");


      /*  y += 30;
        String sts="";
        if (transferDetailModels.get(0).getStatus().equals("O")){
            sts="Open";
        }else if (transferDetailModels.get(0).getStatus().equals("C")){
            sts="Closed";
        }else {
            sts="Progress";
        }
        TscDll.sendcommand("TEXT 0,"+y+",\"Poppins.TTF\",0,8,8,\""+"Status: "+sts+"\"\n");*/

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "NO" + "\"\n");
                TscDll.sendcommand("TEXT 60," + y + ",\"Poppins.TTF\",0,8,8,\"" + "DESCRIPTION" + "\"\n");
                TscDll.sendcommand("TEXT 350," + y + ",\"Poppins.TTF\",0,8,8,\"" + "QTY" + "\"\n");
                TscDll.sendcommand("TEXT 500," + y + ",\"Poppins.TTF\",0,8,8,\"" + "UOM" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                int index = 1;
                for (TransferDetailModel.TransferDetails transferDetails : transferDetailModels.get(0).getTransferDetailsList()) {

                    y += 30;
                    TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + index + "\"\n");
                    if (transferDetails.getDescription().length() <= 45) {
                        TscDll.sendcommand("TEXT 60," + y + ",\"Bold.TTF\",0,8,8,\"" + transferDetails.getDescription() + "\"\n\n");
                    } else {
                        String firstname = transferDetails.getDescription().substring(0, 42);
                        String secondname = transferDetails.getDescription().substring(42);

                        TscDll.sendcommand("TEXT 60," + y + ",\"Bold.TTF\",0,8,8,\"" + firstname + "\"\n\n");
                        y += 30;
                        TscDll.sendcommand("TEXT 60," + y + ",\"Bold.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                    }
                    y += 30;
                    TscDll.sendcommand("TEXT 360," + y + ",\"Bold.TTF\",0,8,8,\"" + (int) (Double.parseDouble(transferDetails.getQty())) + "\"\n");
                    TscDll.sendcommand("TEXT 500," + y + ",\"Poppins.TTF\",0,8,8,\"" + (transferDetails.getUomCode()) + "\"\n");

                    index++;
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += LINE_SPACING + 50;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "------------" + "\"\n");
                TscDll.sendcommand("TEXT 230," + y + ",\"Poppins.TTF\",0,8,8,\"" + "----------" + "\"\n");
                TscDll.sendcommand("TEXT 420," + y + ",\"Poppins.TTF\",0,8,8,\"" + "------------" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Approved By" + "\"\n");
                TscDll.sendcommand("TEXT 230," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Issue By" + "\"\n");
                TscDll.sendcommand("TEXT 420," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Requested By" + "\"\n");

//        y += +10;
//        TscDll.sendcommand("TEXT 0,"+y+",\"Bold.TTF\",0,8,8,\""++"\"\n");
//        TscDll.sendcommand("TEXT 280,"+y+",\"Bold.TTF\",0,8,8,\""++"\"\n");
//        TscDll.sendcommand("TEXT 430,"+y+",\"Bold.TTF\",0,8,8,\""++"\"\n");
//
//        y += +10;
//        TscDll.sendcommand("TEXT 0,"+y+",\"Bold.TTF\",0,8,8,\""+dat+"\"\n");
//        TscDll.sendcommand("TEXT 350,"+y+",\"Bold.TTF\",0,8,8,\""+dat+"\"\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);
            }
        }, 100);
    }


    /*
    public void printSalesOrder(int copy,ArrayList<SalesOrderPrintPreviewModel> salesOrderHeaderDetails, ArrayList<SalesOrderPrintPreviewModel.SalesList> salesOrderList) throws IOException {
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            for (int n = 0; n < copy; n++) {

                int y = 0;

                StringBuilder temp = new StringBuilder();
                y = printTitle(200, y, "SALES ORDER", temp);
                y = printCompanyDetails(y, temp);

                String cpclConfigLabel = temp.toString();

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SO No")
                        + text(150, y, " : ") + text(180, y, salesOrderHeaderDetails.get(0).getSoNumber());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SO Date")
                        + text(150, y, " : ") + text(180, y, salesOrderHeaderDetails.get(0).getSoDate());

                cpclConfigLabel += (text(LEFT_MARGIN,
                        y += LINE_SPACING, "Customer Name"));
                cpclConfigLabel += (text(200, y, " : "));
                Log.d("CustnameeeeAddres2", salesOrderHeaderDetails.get(0).getCustomerName());
                cpclConfigLabel += (text(
                        220,
                        y,
                        (salesOrderHeaderDetails.get(0).getCustomerName().length() > 22) ? salesOrderHeaderDetails.get(0).getCustomerName()
                                .substring(0, 21) : salesOrderHeaderDetails.get(0).getCustomerName()));
                if (salesOrderHeaderDetails.get(0).getCustomerName().length() > 22) {
                    String customername = salesOrderHeaderDetails.get(0).getCustomerName().substring(21);
                    cpclConfigLabel += (text(
                            220,
                            y += LINE_SPACING,
                            (customername.length() > 22) ? customername
                                    .substring(0, 21) : customername));
                }
                */
/*if (salesOrderHeaderDetails.get(0).getDeliveryAddress() != null && !salesOrderHeaderDetails.get(0).getDeliveryAddress().isEmpty()) {

                    String deliveryAddress=salesOrderHeaderDetails.get(0).getDeliveryAddress();
                    cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING, "Address"));
                    cpclConfigLabel += (text(200, y, " : "));

                    Log.d("CustnameeeeAddres2", deliveryAddress);

                    cpclConfigLabel += (text(230, y, (deliveryAddress.length() > 25) ? deliveryAddress.substring(0, 24) : deliveryAddress));

                    if (deliveryAddress.length() > 25) {
                        String deladdress = deliveryAddress.substring(24);
                        cpclConfigLabel += (text(230, y += LINE_SPACING, (deladdress.length() > 25) ? deladdress.substring(0, 24) : deladdress));
                    }
                }*//*



                if (salesOrderHeaderDetails.get(0).getAddress1() != null && !salesOrderHeaderDetails.get(0).getAddress1().isEmpty()) {
                    String deliveryAddress=salesOrderHeaderDetails.get(0).getAddress1();
                    cpclConfigLabel += (text(LEFT_MARGIN,
                            y += LINE_SPACING, "Address 1"));
                    cpclConfigLabel += (text(200, y, " : "));
                    Log.d("CustomerAddress1", deliveryAddress);
                    cpclConfigLabel += (text(
                            230,
                            y,
                            (deliveryAddress.length() > 22) ? deliveryAddress
                                    .substring(0, 22) : deliveryAddress));
                    if (deliveryAddress.length() > 22) {

                        String deladdress = deliveryAddress.substring(22);

                        cpclConfigLabel += (text(
                                230,
                                y += LINE_SPACING,
                                (deladdress.length() > 22) ? deladdress
                                        .substring(0, 21) : deladdress));
                    }
                }

                if (salesOrderHeaderDetails.get(0).getAddress2() != null && !salesOrderHeaderDetails.get(0).getAddress2().isEmpty()) {
                    String deliveryAddress=salesOrderHeaderDetails.get(0).getAddress2();
                    cpclConfigLabel += (text(LEFT_MARGIN,
                            y += LINE_SPACING, "Address 2"));
                    cpclConfigLabel += (text(200, y, " : "));
                    Log.d("CustomerAddress2", deliveryAddress);
                    cpclConfigLabel += (text(
                            230,
                            y,
                            (deliveryAddress.length() > 22) ? deliveryAddress
                                    .substring(0, 22) : deliveryAddress));
                    if (deliveryAddress.length() > 22) {

                        String deladdress = deliveryAddress.substring(22);

                        cpclConfigLabel += (text(
                                230,
                                y += LINE_SPACING,
                                (deladdress.length() > 22) ? deladdress
                                        .substring(0, 21) : deladdress));
                    }
                }

                if (salesOrderHeaderDetails.get(0).getAddress3() != null && !salesOrderHeaderDetails.get(0).getAddress3().isEmpty()) {
                    String deliveryAddress=salesOrderHeaderDetails.get(0).getAddress3();
                    cpclConfigLabel += (text(LEFT_MARGIN,
                            y += LINE_SPACING, "Address 3"));
                    cpclConfigLabel += (text(200, y, " : "));
                    Log.d("CustomerAddress3", deliveryAddress);
                    cpclConfigLabel += (text(
                            230,
                            y,
                            (deliveryAddress.length() > 22) ? deliveryAddress
                                    .substring(0, 22) : deliveryAddress));
                    if (deliveryAddress.length() > 22) {

                        String deladdress = deliveryAddress.substring(22);

                        cpclConfigLabel += (text(
                                230,
                                y += LINE_SPACING,
                                (deladdress.length() > 22) ? deladdress
                                        .substring(0, 21) : deladdress));
                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");

                cpclConfigLabel += text(70, y, "Description");
                cpclConfigLabel += text(250, y, "Qty");
                cpclConfigLabel += text(365, y, "Price");
                cpclConfigLabel += text(500, y, "Total");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (salesOrderList.size() > 0) {
                    int index = 1;
                    for (SalesOrderPrintPreviewModel.SalesList sales : salesOrderList) {

                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, String.valueOf(index));  // Display the Serial Number

                        cpclConfigLabel += text(70, y, (sales.getDescription().length() > 35)
                                ? sales.getDescription().substring(0, 34)
                                : sales.getDescription());                      // Display the product Name

                        int count = 0;
                        String name = sales.getDescription();
                        int len = name.length();
                        if (len > 35) {
                            int get_len = name.substring(34, len).length();
                            String remark = name.substring(34, len);
                            Log.d("BalanceString", "-->" + len + " " + get_len + " " + remark);
                            String names;

                            for (int j = 0; j < get_len; j = j + 35) {
                                count = count + 34;
                                if (count > get_len) {
                                    names = remark.substring(j, get_len);
                                    cpclConfigLabel += text(
                                            70,
                                            y += LINE_SPACING, names);
                                    Log.d("BalancesDescription", "-->" + names);

                                } else {
                                    names = remark.substring(j, j + 35);
                                    cpclConfigLabel += text(
                                            70,
                                            y += LINE_SPACING, names);
                                    Log.d("BalancesValues", "-->" + names);

                                }
                            }
                        }

                        cpclConfigLabel += text(250,  y += LINE_SPACING, (int)Double.parseDouble(sales.getNetQty())+"");  // Display the Qty of Invoice

                 */
/*       if (Integer.parseInt(sales.getPcsperCarton()) == 1) {
                            cpclConfigLabel += text(380, y, Utils.twoDecimalPoint(Double.parseDouble(sales.getCartonPrice()))); // Display the Particular product price
                        } else {
                            cpclConfigLabel += text(380, y, Utils.twoDecimalPoint(Double.parseDouble(sales.getUnitPrice()))); // Display the Particular product price

                        }
*//*

                        cpclConfigLabel += text(380, y, twoDecimalPoint(Double.parseDouble(sales.getPricevalue()))); // Display the Particular product price


                        cpclConfigLabel += text(486, y, twoDecimalPoint(Double.parseDouble(sales.getTotal()))); // Display the Sub total of Particular product

                        index++;
                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

               */
/* if (salesOrderHeaderDetails.get(0).getTaxType().equals("I")){
                    double sub_total=Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTotal()) - Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax());
                    cpclConfigLabel += text(260, y += LINE_SPACING, "Sub Total")
                            + text(420, y, " : ")
                            + text(470, y, Utils.twoDecimalPoint(sub_total));
                }else {
                    cpclConfigLabel += text(260, y += LINE_SPACING, "Sub Total")
                            + text(420, y, " : ")
                            + text(470, y, Utils.twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal())));

                }*//*


                // Item Discount Text
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "")
                        + text(140, y, ".")
                        + text(180, y, "");

               */
/* if (salesOrderHeaderDetails.get(0).getTaxType().equals("I")) {

                    double sub_total = Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTotal()) - Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax());

                    cpclConfigLabel += text(260, y, "Sub Total")
                            + text(420, y, " : ")
                            + text(470, y, Utils.twoDecimalPoint(sub_total));
                }else {
                    cpclConfigLabel += text(260, y, "Sub Total")
                            + text(420, y, " : ")
                            + text(470, y, Utils.twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal())));
                }*//*


                cpclConfigLabel += text(260, y, "Sub Total")
                        + text(420, y, " : ")
                        + text(470, y, twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal())));

                // Bill Discount Text
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "")
                        + text(140, y, ".")
                        + text(180, y, "");


                cpclConfigLabel += text(260, y, "GST("+salesOrderHeaderDetails.get(0).getTaxType()+":"+ twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getTaxValue()))+" %)")
                        + text(420, y, " : ")
                        + text(470, y, twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax())));


                cpclConfigLabel += text(260, y += LINE_SPACING, "Net Total")
                        + text(420, y, " : ")
                        + text(470, y, twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTotal())));

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1" + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

                os.write(cpclConfigLabel.getBytes(),0,cpclConfigLabel.getBytes().length);
                os.flush();
            }
            os.close();

        }catch (Exception ex){
            Log.w("GivenErrorPrinting:",ex.getMessage());
            //  Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
*/
    public void printExpense(int copy, ArrayList<InvoicePrintPreviewModel> invoiceDetails,
                             ArrayList<InvoicePrintPreviewModel.InvoiceList> expenseList) throws IOException {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                TscDll.openport(macAddress);
                int y = 0;
                height = 65;
                finalHeight = height + (expenseList.size() * 9) + invoiceBottomLine;
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + "Expense" + "\"\n");

                y += LINE_SPACING + 10;
                // Define the Box
                //TscDll.sendcommand("BOX 0,"+y+",570,0,2\n");
                TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,9,9,\"" + "No: " + invoiceDetails.get(0).getInvoiceNumber() + "\"\n");
                TscDll.sendcommand("TEXT 400," + y + ",\"Poppins.TTF\",0,8,8,\"" + invoiceDetails.get(0).getInvoiceDate() + "\"\n");
                y += LINE_SPACING;
                if (invoiceDetails.get(0).getCustomerName().length() <= 45) {
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Supplier: " + invoiceDetails.get(0).getCustomerName() + "\"\n\n");
                } else {
                    String firstname = invoiceDetails.get(0).getCustomerName().substring(0, 42);
                    String secondname = invoiceDetails.get(0).getCustomerName().substring(42);

                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Supplier: " + firstname + "\"\n\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                }
                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"" + "CODE: " + invoiceDetails.get(0).getCustomerCode() + "\"\n");
                if (!invoiceDetails.get(0).getAddress().isEmpty()) {
                    y += LINE_SPACING;
                    if (invoiceDetails.get(0).getAddress().length() <= 45) {
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "ADDR: " + invoiceDetails.get(0).getAddress() + "\"\n\n");
                    } else {
                        String address1 = invoiceDetails.get(0).getAddress().substring(0, 42);
                        String address2 = invoiceDetails.get(0).getAddress().substring(42);

                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "ADDR: " + address1 + "\"\n\n");
                        y += 30;
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + address2 + "\"\n\n");
                    }
                }
                if (showUserName.equals("true")) {
                    y += 30;
                    TscDll.sendcommand("TEXT 400," + y + ",\"Poppins.TTF\",0,8,8,\"" + "User: " + username + "\"\n");
                }

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");


                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SNO" + "\"\n");
                TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SERVICE" + "\"\n");
                TscDll.sendcommand("TEXT 200," + y + ",\"Poppins.TTF\",0,8,8,\"" + "ACCOUNT" + "\"\n");
                TscDll.sendcommand("TEXT 480," + y + ",\"Poppins.TTF\",0,8,8,\"" + "TOTAL" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                int index = 1;
                double sum_total = 0.0;
                for (InvoicePrintPreviewModel.InvoiceList productDetails : expenseList) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + index + "\"\n");

                    if (productDetails.getDescription().length() <= 45) {
                        TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + productDetails.getDescription() + "\"\n\n");
                    } else {
                        String firstname = productDetails.getDescription().substring(0, 42);
                        String secondname = productDetails.getDescription().substring(42);

                        TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + firstname + "\"\n\n");
                        y += 30;
                        TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                    }
                    y += 30;
                    TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + productDetails.getProductCode() + "\"\n\n");
                    if (productDetails.getAccountName().length() <= 45) {
                        TscDll.sendcommand("TEXT 150," + y + ",\"Poppins.TTF\",0,8,8,\"" + productDetails.getAccountName() + "\"\n\n");
                    } else {
                        String firstname = productDetails.getAccountName().substring(0, 42);
                        String secondname = productDetails.getAccountName().substring(42);

                        TscDll.sendcommand("TEXT 150," + y + ",\"Poppins.TTF\",0,8,8,\"" + firstname + "\"\n\n");
                        y += 30;
                        TscDll.sendcommand("TEXT 150," + y + ",\"Poppins.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                    }
                    TscDll.sendcommand("TEXT 490," + y + ",\"Bold.TTF\",0,8,8,\"" + Double.parseDouble(productDetails.getTotal()) + "\"\n");
                    sum_total += Double.parseDouble(productDetails.getTotal());
                    index++;
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 280," + y + ",\"Bold.TTF\",0,8,8,\"" + "TOTAL :" + "\"\n");
                TscDll.sendcommand("TEXT 490," + y + ",\"Bold.TTF\",0,8,8,\"" + sum_total + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "------------" + "\"\n");
                TscDll.sendcommand("TEXT 350," + y + ",\"Poppins.TTF\",0,8,8,\"" + "--------------" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Received By" + "\"\n");
                TscDll.sendcommand("TEXT 360," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Authorized By" + "\"\n");

                TscDll.printlabel(1, copy);
                height = 0;
                TscDll.closeport(5000);
            }
        }, 100);
    }


    public void printInvoiceByProduct(int copy, ArrayList<InvoiceByProductModel.ProductDetails>
            invoiceProductList, String fromDate, String toDate, String locationCode) throws IOException {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);
                int y = 0;
                height = 80;
                finalHeight = height + (invoiceProductList.size() * 8);
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + "Invoice By Product" + "\"\n");


                y += LINE_SPACING + 10;
                // Define the Box
                //TscDll.sendcommand("BOX 0,"+y+",570,0,2\n");
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "FROM DATE: " + fromDate + "\"\n");
                TscDll.sendcommand("TEXT 350," + y + ",\"Poppins.TTF\",0,8,8,\"" + "TO DATE: " + toDate + "\"\n");

                y += 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Location Code: " + locationCode + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "SNO" + "\"\n");
                TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + "DESCRIPTION" + "\"\n");
                TscDll.sendcommand("TEXT 500," + y + ",\"Poppins.TTF\",0,8,8,\"" + "QTY" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                int index = 1;
                double sum_qty = 0.0;
                for (InvoiceByProductModel.ProductDetails productDetails : invoiceProductList) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + index + "\"\n");

                    if (productDetails.getProductName().length() <= 45) {
                        TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + productDetails.getProductName() + "\"\n\n");
                    } else {
                        String firstname = productDetails.getProductName().substring(0, 42);
                        String secondname = productDetails.getProductName().substring(42);

                        TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + firstname + "\"\n\n");
                        y += 30;
                        TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + secondname + "\"\n\n");
                    }
                    y += 30;
                    TscDll.sendcommand("TEXT 70," + y + ",\"Poppins.TTF\",0,8,8,\"" + productDetails.getProductCode() + "\"\n\n");
                    TscDll.sendcommand("TEXT 510," + y + ",\"Bold.TTF\",0,8,8,\"" + (int) Double.parseDouble(productDetails.getProductQty()) + "\"\n");
                    sum_qty += Double.parseDouble(productDetails.getProductQty());
                    index++;
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,8,8,\"" + "TOTAL QTY :" + "\"\n");
                TscDll.sendcommand("TEXT 510," + y + ",\"Bold.TTF\",0,8,8,\"" + (int) sum_qty + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                TscDll.printlabel(1, copy);
                height = 0;
                TscDll.closeport(5000);
            }
        }, 100);
    }



    /*public void printInvoiceByProduct(int copy, ArrayList<InvoiceByProductModel> invoiceProductList){
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);

            for (int n = 0; n < copy; n++) {

                int y = 0;
                double net_qty_value=0;

                StringBuilder temp = new StringBuilder();
                y = printTitle(180, y, "INVOICE BY PRODUCTS", temp);
                y = printCompanyDetails(y, temp);

                String cpclConfigLabel = temp.toString();

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "From Date")
                        + text(210, y, " : ") + text(230, y, invoiceProductList.get(0).getFromDate());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "To Date")
                        + text(210, y, " : ") + text(230, y, invoiceProductList.get(0).getToDate());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Location Code")
                        + text(210, y, " : ") + text(230, y, invoiceProductList.get(0).getLocationCode());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Location Name")
                        + text(210, y, " : ") + text(230, y, invoiceProductList.get(0).getLocationName());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Type")
                        + text(210, y, " : ") + text(230, y, invoiceProductList.get(0).getPrintType());


                //    cpclConfigLabel += (text(220, y, (invoiceHeaderDetails.get(0).getCustomerName().length() > 22) ? invoiceHeaderDetails.get(0).getCustomerName()
                //          .substring(0, 21) : invoiceHeaderDetails.get(0).getCustomerName()));
                //  if (invoiceHeaderDetails.get(0).getCustomerName().length() > 22) {
                //    String customername = invoiceHeaderDetails.get(0).getCustomerName().substring(21);
                //  cpclConfigLabel += (text(220, y += LINE_SPACING, (customername.length() > 22) ? customername.substring(0, 21) : customername));
                //  }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
                cpclConfigLabel += text(70, y, "Description");
                cpclConfigLabel += text(490, y, "Qty");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (invoiceProductList.get(0).getProductDetails().size() > 0) {
                    int index = 1;
                    for (InvoiceByProductModel.ProductDetails invoice : invoiceProductList.get(0).getProductDetails()) {

                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, String.valueOf(index));  // Display the Serial Number
                        cpclConfigLabel += text(70, y, (invoice.getProductName().length() > 35)
                                ? invoice.getProductName().substring(0,34)
                                : invoice.getProductName());

                        // Display the product Name

                        int count = 0;
                        String name = invoice.getProductName();
                        int len = name.length();
                        if (len > 35) {
                            int get_len = name.substring(34, len).length();
                            String remark = name.substring(34, len);
                            Log.d("BalanceString", "-->" + len + " " + get_len + " " + remark);
                            String names;

                            for (int j = 0; j < get_len; j = j + 35) {
                                count = count + 35;
                                if (count > get_len) {
                                    names = remark.substring(j, get_len);
                                    cpclConfigLabel += text(
                                            70,
                                            y += LINE_SPACING, names);
                                    Log.d("BalancesDescription", "-->" + names);

                                } else {
                                    names = remark.substring(j, j + 35);
                                    cpclConfigLabel += text(
                                            70,
                                            y += LINE_SPACING, names);
                                    Log.d("BalancesValues", "-->" + names);

                                }
                            }
                        }
                        net_qty_value+=Double.parseDouble(invoice.getProductQty());
                        cpclConfigLabel += text(490, y+= LINE_SPACING, (int)Double.parseDouble(invoice.getProductQty())+""); // Display the Sub total of Particular product
                        index++;
                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(260, y += LINE_SPACING, "Total Qty")
                        + text(420, y, " : ")
                        + text(490, y, (int)net_qty_value+"");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1" + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

                os.write(cpclConfigLabel.getBytes("iso-8859-1"));
                os.flush();
            }
            os.close();

        }catch (Exception ex){
            Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }*/

    public void printInvoiceSummary(int copy, ArrayList<InvoiceSummaryModel> invoiceSummaryList) {
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            for (int n = 0; n < copy; n++) {
                int y = 0;
                double netpaidamount = 0.00;
                double nettotal = 0.00;

                StringBuilder temp = new StringBuilder();
                y = printTitle(180, y, "INVOICE SUMMARY", temp);
                y = printCompanyDetails(y, temp);

                String cpclConfigLabel = temp.toString();

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "From Date")
                        + text(210, y, " : ") + text(230, y, invoiceSummaryList.get(0).getFromDate());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "To Date")
                        + text(210, y, " : ") + text(230, y, invoiceSummaryList.get(0).getToDate());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Location Code")
                        + text(210, y, " : ") + text(230, y, invoiceSummaryList.get(0).getWarehouseCode());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Location Name")
                        + text(210, y, " : ") + text(230, y, invoiceSummaryList.get(0).getWarehouseName());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Type")
                        + text(210, y, " : ") + text(230, y, invoiceSummaryList.get(0).getPrintType());

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Invoice No");
                cpclConfigLabel += text(220, y, "Net Total");
                cpclConfigLabel += text(340, y, "Paid");
                cpclConfigLabel += text(460, y, "Balance");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (invoiceSummaryList.get(0).getSummaryList().size() > 0) {
                    int index = 1;
                    for (InvoiceSummaryModel.SummaryDetails invoice : invoiceSummaryList.get(0).getSummaryList()) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, invoice.getInvoiceNumber().toString());
                        cpclConfigLabel += text(220, y, Utils.twoDecimalPoint(Double.parseDouble(invoice.getNetTotal())) + "");
                        cpclConfigLabel += text(340, y, Utils.twoDecimalPoint(Double.parseDouble(invoice.getPaidAmount())) + "");
                        cpclConfigLabel += text(460, y, Utils.twoDecimalPoint(Double.parseDouble(invoice.getBalanceAmount())) + "");

                        try {
                            // Sathish 29-08-2020 Changing nettotal to balance amount total
                            if (Double.parseDouble(invoice.getBalanceAmount()) != 0) {
                                nettotal += Double.parseDouble(invoice.getBalanceAmount());
                            }
                            // Sathish 04-09-2020 Display the net paid amount in this printer display
                            if (Double.parseDouble(invoice.getPaidAmount()) != 0) {
                                netpaidamount += Double.parseDouble(invoice.getPaidAmount());
                            }
                        } catch (Exception ex) {
                            Log.e("Error_in_calculating", ex.getMessage());
                        }

                        index++;
                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title
                cpclConfigLabel += text(220, y += LINE_SPACING, "Total")
                        + text(240, y, " : ")
                        + text(340, y, twoDecimalPoint(netpaidamount))
                        + text(460, y, twoDecimalPoint(nettotal));

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1" + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

                os.write(cpclConfigLabel.getBytes("iso-8859-1"));
                os.flush();
            }
            os.close();

        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void printCustomerStatementDate(int copy, ArrayList<CustomerStateModel> customerStateModels, String fromDate, String toDate) throws IOException {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);
                int y = 0;
                height = 70;
                int totalheight = 0;
                if(customerStateModels.get(0).getCustInvoiceDetailsARList().size() >0){
                    totalheight = 30 ;
                }
                //finalHeight=height+(customerStateModels.size() * list_height)+20;
                finalHeight = height + (customerStateModels.get(0).getCustInvoiceDetailList().size() * 5)
                        +(customerStateModels.get(0).getCustInvoiceDetailsARList().size() * 5)+totalheight+30;
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                try {
                    String filePath = Constants.getSignatureFolderPath(context);
                    String fileName = "Logo.jpg";
                    File mFile = new File(filePath, fileName);
                    if (mFile.exists()) {
                        y += LINE_SPACING;
                        TscDll.sendpicture_resize(180, y, mFile.getAbsolutePath(), 230, 200);
                        y += 170;
                    }
                } catch (Exception e) {
                }

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + "Customer Outstanding (as on Date)" + "\"\n");


                y += LINE_SPACING + 10;
                // Define the Box
                //TscDll.sendcommand("BOX 0,"+y+",570,0,2\n");
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "FROM DATE: " + fromDate + "\"\n");
                TscDll.sendcommand("TEXT 330," + y + ",\"Poppins.TTF\",0,8,8,\"" + "TO DATE: " + toDate + "\"\n");

                y += 40;
                if (customerStateModels.get(0).getCustomerName().length() <= 45) {
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,9,9,\"" + "CUST :" + customerStateModels.get(0).getCustomerName() + "\"\n\n");
                } else {
                    String firstname = customerStateModels.get(0).getCustomerName().substring(0, 42);
                    String secondname = customerStateModels.get(0).getCustomerName().substring(42);

                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,9,9,\"" + firstname + "\"\n\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,9,9,\"" + "CUST :" + secondname + "\"\n\n");
                }

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                // TscDll.sendcommand("TEXT 0,"+y+",\"Poppins.TTF\",0,8,8,\""+"SNo"+"\"\n");
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Invoice No" + "\"\n");
                TscDll.sendcommand("TEXT 220," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Invoice Date" + "\"\n");
                TscDll.sendcommand("TEXT 370," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Net Total" + "\"\n");
                TscDll.sendcommand("TEXT 470," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Balance" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                int index = 1;
                double sum_netbalance = 0.0;
                double sum_nettotal = 0.0;
                double sum_netbalance1 = 0.0;
                double sum_nettotal1 = 0.0;
                double sum_netbalanceFinal = 0.0;
                double sum_nettotalFinal = 0.0;

                for (CustomerStateModel.CustInvoiceDetails custInvoiceDetails :
                        customerStateModels.get(0).getCustInvoiceDetailList()) {
                    y += 35;
                    // TscDll.sendcommand("TEXT 5,"+y+",\"Poppins.TTF\",0,8,8,\""+index+"\"\n");
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + custInvoiceDetails.getInvoiceNumber() + "\"\n");
                    TscDll.sendcommand("TEXT 220," + y + ",\"Poppins.TTF\",0,8,8,\"" + custInvoiceDetails.getInvoiceDate() + "\"\n");
                    TscDll.sendcommand("TEXT 370," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(custInvoiceDetails.getNetTotal())) + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(custInvoiceDetails.getBalanceAmount())) + "\"\n");

                    sum_netbalance += Double.parseDouble(custInvoiceDetails.getBalanceAmount());
                    sum_nettotal += Double.parseDouble(custInvoiceDetails.getNetTotal());

                    index++;
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Total : ( $ ) " + "\"\n");
                TscDll.sendcommand("TEXT 420," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(sum_nettotal) + "\"\n");
                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Balance : ( $ ) " + "\"\n");
                TscDll.sendcommand("TEXT 420," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(sum_netbalance) + "\"\n");

                if(customerStateModels.get(0).getCustInvoiceDetailsARList().size() > 0){
                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                    y += 20;

                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "AR Credit Statement" + "\"\n");

                    for (CustomerStateModel.CustInvoiceDetailsAR custInvoiceDetailsAR : customerStateModels.get(0).getCustInvoiceDetailsARList()) {
                        y += 35;
                        TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + custInvoiceDetailsAR.getInvoiceNumber() + "\"\n");
                        TscDll.sendcommand("TEXT 180," + y + ",\"Poppins.TTF\",0,8,8,\"" + custInvoiceDetailsAR.getInvoiceDate() + "\"\n");
                        TscDll.sendcommand("TEXT 340," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(custInvoiceDetailsAR.getNetTotal())) + "\"\n");
                        TscDll.sendcommand("TEXT 450," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(custInvoiceDetailsAR.getBalanceAmount())) + "\"\n");

                        sum_netbalance1 += Double.parseDouble(custInvoiceDetailsAR.getBalanceAmount());
                        sum_nettotal1 += Double.parseDouble(custInvoiceDetailsAR.getNetTotal());

                        index++;
                    }
                    sum_netbalanceFinal = sum_nettotal - sum_nettotal1;
                    sum_nettotalFinal = sum_netbalance - sum_netbalance1;
                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    y += 20;
                    TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,8,8,\"" + "Total :($) " + "\"\n");
                    TscDll.sendcommand("TEXT 330," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(sum_nettotal1) + "\"\n");
                    TscDll.sendcommand("TEXT 440," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(sum_netbalance1) + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    y += 20;
                    TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,8,8,\"" + "Outstanding Balance :($) " + "\"\n");
                    TscDll.sendcommand("TEXT 330," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(sum_netbalanceFinal) + "\"\n");
                    TscDll.sendcommand("TEXT 440," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(sum_nettotalFinal) + "\"\n");
                }


                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);
            }
        }, 100);
    }

    public void printCustomerStatement(int copy, ArrayList<CustomerStateModel> customerStateModels, String fromDate, String toDate) throws IOException {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);
                int y = 0;
                height = 70;
                int totalheight = 0;
                if(customerStateModels.get(0).getCustInvoiceDetailsARList().size() >0){
                    totalheight = 30 ;
                }
                //finalHeight=height+(customerStateModels.size() * list_height)+20;
                finalHeight = height + (customerStateModels.get(0).getCustInvoiceDetailList().size() * 5)
                        +(customerStateModels.get(0).getCustInvoiceDetailsARList().size() * 5)+totalheight+30;
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                try {
                    String filePath = Constants.getSignatureFolderPath(context);
                    String fileName = "Logo.jpg";
                    File mFile = new File(filePath, fileName);
                    if (mFile.exists()) {
                        y += LINE_SPACING;
                        TscDll.sendpicture_resize(180, y, mFile.getAbsolutePath(), 230, 200);
                        y += 170;
                    }
                } catch (Exception e) {
                }

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + "Customer Outstanding Period" + "\"\n");


                y += LINE_SPACING + 10;
                // Define the Box
                //TscDll.sendcommand("BOX 0,"+y+",570,0,2\n");
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "FROM DATE: " + fromDate + "\"\n");
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "TO DATE: " + toDate + "\"\n");

                y += 40;
                if (customerStateModels.get(0).getCustomerName().length() <= 45) {
                    TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,9,9,\"" + "CUST :" + customerStateModels.get(0).getCustomerName() + "\"\n\n");
                } else {
                    String firstname = customerStateModels.get(0).getCustomerName().substring(0, 42);
                    String secondname = customerStateModels.get(0).getCustomerName().substring(42);

                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,9,9,\"" + firstname + "\"\n\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,9,9,\"" + "CUST :" + secondname + "\"\n\n");
                }

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                // TscDll.sendcommand("TEXT 0,"+y+",\"Poppins.TTF\",0,8,8,\""+"SNo"+"\"\n");
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Invoice No" + "\"\n");
                TscDll.sendcommand("TEXT 220," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Invoice Date" + "\"\n");
                TscDll.sendcommand("TEXT 370," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Net Total" + "\"\n");
                TscDll.sendcommand("TEXT 470," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Balance" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                int index = 1;
                double sum_netbalance = 0.0;
                double sum_nettotal = 0.0;
                double sum_netbalance1 = 0.0;
                double sum_nettotal1 = 0.0;
                double sum_netbalanceFinal = 0.0;
                double sum_nettotalFinal = 0.0;

                for (CustomerStateModel.CustInvoiceDetails custInvoiceDetails :
                        customerStateModels.get(0).getCustInvoiceDetailList()) {
                    y += 35;
                    // TscDll.sendcommand("TEXT 5,"+y+",\"Poppins.TTF\",0,8,8,\""+index+"\"\n");
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + custInvoiceDetails.getInvoiceNumber() + "\"\n");
                    TscDll.sendcommand("TEXT 220," + y + ",\"Poppins.TTF\",0,8,8,\"" + custInvoiceDetails.getInvoiceDate() + "\"\n");
                    TscDll.sendcommand("TEXT 370," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(custInvoiceDetails.getNetTotal())) + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(custInvoiceDetails.getBalanceAmount())) + "\"\n");

                    sum_netbalance += Double.parseDouble(custInvoiceDetails.getBalanceAmount());
                    sum_nettotal += Double.parseDouble(custInvoiceDetails.getNetTotal());

                    index++;
                }
                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Total : ( $ ) " + "\"\n");
                TscDll.sendcommand("TEXT 420," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(sum_nettotal) + "\"\n");
                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Balance : ( $ ) " + "\"\n");
                TscDll.sendcommand("TEXT 420," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(sum_netbalance) + "\"\n");

                if(customerStateModels.get(0).getCustInvoiceDetailsARList().size() > 0){
                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                    y += 20;

                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "AR Credit Statement" + "\"\n");

                    for (CustomerStateModel.CustInvoiceDetailsAR custInvoiceDetailsAR : customerStateModels.get(0).getCustInvoiceDetailsARList()) {
                        y += 35;
                        TscDll.sendcommand("TEXT 5," + y + ",\"Poppins.TTF\",0,8,8,\"" + custInvoiceDetailsAR.getInvoiceNumber() + "\"\n");
                        TscDll.sendcommand("TEXT 180," + y + ",\"Poppins.TTF\",0,8,8,\"" + custInvoiceDetailsAR.getInvoiceDate() + "\"\n");
                        TscDll.sendcommand("TEXT 340," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(custInvoiceDetailsAR.getNetTotal())) + "\"\n");
                        TscDll.sendcommand("TEXT 450," + y + ",\"Poppins.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(custInvoiceDetailsAR.getBalanceAmount())) + "\"\n");

                        sum_netbalance1 += Double.parseDouble(custInvoiceDetailsAR.getBalanceAmount());
                        sum_nettotal1 += Double.parseDouble(custInvoiceDetailsAR.getNetTotal());

                        index++;
                    }
                    sum_netbalanceFinal = sum_nettotal - sum_nettotal1;
                    sum_nettotalFinal = sum_netbalance - sum_netbalance1;
                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    y += 20;
                    TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,8,8,\"" + "Total :($) " + "\"\n");
                    TscDll.sendcommand("TEXT 330," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(sum_nettotal1) + "\"\n");
                    TscDll.sendcommand("TEXT 440," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(sum_netbalance1) + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                    y += 20;
                    TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,8,8,\"" + "Outstanding Balance :($) " + "\"\n");
                    TscDll.sendcommand("TEXT 330," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(sum_netbalanceFinal) + "\"\n");
                    TscDll.sendcommand("TEXT 440," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(sum_nettotalFinal) + "\"\n");
                }


                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);
            }
        }, 100);
    }



/*
    public void printCustomerStatement(int copy, ArrayList<InvoiceSummaryModel> invoiceSummaryList) throws IOException {
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            for (int n = 0; n < copy; n++) {
                int y = 0;
                double netpaidamount=0.00;
                double netbalanceamount = 0.00;
                double nettotalvalue=0.00;

                StringBuilder temp = new StringBuilder();
                y = printTitle(160, y, "CUSTOMER STATEMENT", temp);
                y = printCompanyDetails(y, temp);

                String cpclConfigLabel = temp.toString();

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title


                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "Customer Name")
                        + text(210, y, " : ") + text(240, y, invoiceSummaryList.get(0).getCustomerName());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "From Date")
                        + text(210, y, " : ") + text(240, y, invoiceSummaryList.get(0).getFromDate());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "To Date")
                        + text(210, y, " : ") + text(240, y, invoiceSummaryList.get(0).getToDate());

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Invoice No");
                cpclConfigLabel += text(200, y, "Date");
                cpclConfigLabel += text(340, y, "Net Total");
                cpclConfigLabel += text(460, y, "Balance");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (invoiceSummaryList.get(0).getSummaryList().size() > 0) {
                    int index = 1;
                    for (InvoiceSummaryModel.SummaryDetails invoice : invoiceSummaryList.get(0).getSummaryList()) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, invoice.getInvoiceNumber().toString());
                        cpclConfigLabel += text(200, y ,invoice.getDate()+"");
                        cpclConfigLabel += text(340, y,Utils.twoDecimalPoint(Double.parseDouble(invoice.getNetTotal()))+"");
                        cpclConfigLabel += text(460, y,Utils.twoDecimalPoint(Double.parseDouble(invoice.getBalanceAmount()))+"");

                        try {
                            // Sathish 29-08-2020 Changing netbalanceamount to balance amount total
                            if (Double.parseDouble(invoice.getBalanceAmount())!=0) {
                                netbalanceamount += Double.parseDouble(invoice.getBalanceAmount());
                            }
                            // Sathish 04-09-2020 Display the net paid amount in this printer display
                            //if (Double.parseDouble(invoice.getPaidAmount())!=0){
                             //   netpaidamount+=Double.parseDouble(invoice.getPaidAmount());
                          //  }

                            if (Double.parseDouble(invoice.getNetTotal())!=0){
                                nettotalvalue+=Double.parseDouble(invoice.getNetTotal());
                            }
                        }catch (Exception ex){
                            Log.e("Error_in_calculating",ex.getMessage());
                        }

                        index++;
                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Total")
                        + text(220, y, "")
                        + text(340,y,  twoDecimalPoint(nettotalvalue))
                        + text(460, y, twoDecimalPoint(netbalanceamount));

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1" + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

                os.write(cpclConfigLabel.getBytes("iso-8859-1"));
                os.flush();
            }
            os.close();

        }catch (Exception ex){
            Log.w("GivenIssuesValues:",ex.getMessage().toString());
            Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
*/

    public void printReceiptDetail(int copy, String fromDate, String toDate,
                                   ArrayList<ReceiptDetailsModel> receiptDetailsModels) throws IOException {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);

                int y = 0;
                height = 70;
                int settleHeight = 0;
                ArrayList<ReceiptDetailsModel.ReceiptDetails> receiptDetailsModels1 = new ArrayList<>() ;

                for (ReceiptDetailsModel.ReceiptDetails receiptDetails :
                        receiptDetailsModels.get(0).getReceiptDetailsList()) {

                    if (receiptDetails.getPaymode().equalsIgnoreCase("Cheque")) {
                        receiptDetailsModels1.add(receiptDetails) ;
                    }
                }
                Log.w("listheig",""+receiptDetailsModels1.size());
                settleHeight = receiptDetailsModels1.size() * 30;

                finalHeight = height + (receiptDetailsModels.get(0).getReceiptDetailsList().size() * 15) + invoiveSubTotalHeight+settleHeight;
                Log.w("settlheig",""+settleHeight+"..final "+finalHeight);

                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media
                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                StringBuilder temp = new StringBuilder();
                if (company_name.length() > 38) {
                    temp.append((company_name.length() > 38) ? company_name.substring(0, 37) : company_name);
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                    String companyname = company_name.substring(37);
                    temp.append((companyname.length() > 37) ? companyname.substring(0, 36) : companyname);
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + temp.toString() + "\"\n\n");
                } else {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                }

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + "Receipt Detail" + "\"\n");

                y += LINE_SPACING + 20;
                // Define the Box
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "From Date: " + fromDate + "\"\n");
                TscDll.sendcommand("TEXT 330," + y + ",\"Poppins.TTF\",0,8,8,\"" + "To Date: " + toDate + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Customer / Receipt No" + "\"\n");
                TscDll.sendcommand("TEXT 460," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Total" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                double sum_cashamt = 0.0;
                double sum_chequeamt = 0.0;
                double sum_totalamt = 0.0;
                for (ReceiptDetailsModel.ReceiptDetails receiptDetails :
                        receiptDetailsModels.get(0).getReceiptDetailsList()) {

                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptDetails.getCustomerName() + "\"\n");

                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptDetails.getReceiptNumber() + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptDetails.getReceiptTotal())) + "\"\n");

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");
                    // TscDll.sendcommand("TEXT 0,"+y+",\"Poppins.TTF\",0,8,8,\""+"-------------------------------------------------------------"+"\"\n");

                    y += 20;
                    TscDll.sendcommand("TEXT 80," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Paymode    :" + "\"\n");
                    TscDll.sendcommand("TEXT 200," + y + ",\"Bold.TTF\",0,8,8,\"" + receiptDetails.getPaymode() + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptDetails.getReceiptTotal())) + "\"\n");


                    if (receiptDetails.getPaymode().equalsIgnoreCase("Cheque")) {
                        y += 30;
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "-------------------------------------------------" + "\"\n");
                        y += 20;
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Bank Name" + "\"\n");
                        TscDll.sendcommand("TEXT 250," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Cheque No" + "\"\n");
                        TscDll.sendcommand("TEXT 420," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Cheque Date" + "\"\n");
                        y += 30;
                        TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "-------------------------------------------------" + "\"\n");
                        y += 20;
                        TscDll.sendcommand("TEXT 0," + y + ",\"Bold.TTF\",0,8,8,\"" + receiptDetails.getBankCode() + "\"\n");
                        TscDll.sendcommand("TEXT 250," + y + ",\"Bold.TTF\",0,8,8,\"" + receiptDetails.getChequeNo() + "\"\n");
                        TscDll.sendcommand("TEXT 420," + y + ",\"Bold.TTF\",0,8,8,\"" + receiptDetails.getChequeDate() + "\"\n");
                    }
                    sum_totalamt += Double.parseDouble(receiptDetails.getReceiptTotal());

                    y += LINE_SPACING;
                    TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                }

                for (int i = 0; i < receiptDetailsModels.get(0).getReceiptDetailsList().size(); i++) {

                    if (receiptDetailsModels.get(0).getReceiptDetailsList().get(i).getPaymode().equalsIgnoreCase("Cash")) {
                        sum_cashamt += Double.parseDouble(receiptDetailsModels.get(0).getReceiptDetailsList().get(i).getReceiptTotal());
                    } else {
                        sum_chequeamt += Double.parseDouble(receiptDetailsModels.get(0).getReceiptDetailsList().get(i).getReceiptTotal());
                    }
                }
                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Cheque Amount :($)" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(sum_chequeamt) + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Cash Amount :($)" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(sum_cashamt) + "\"\n");

                y += LINE_SPACING + 20;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 30;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Total Amount :($)" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(sum_totalamt) + "\"\n");


                y += LINE_SPACING;
                //  TscDll.sendcommand("BAR 0,"+y+",800,2\n");

                y += LINE_SPACING + 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "------------" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Received By" + "\"\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);

            }
        }, 100);
    }


  /*  // Print the receipt Details
    public void printReceiptDetails(int copy, ArrayList<ReceiptDetailsModel> receiptDetailsList){
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);

            for (int n = 0; n < copy; n++) {

                int y = 0;

                StringBuilder temp = new StringBuilder();
                y = printTitle(180, y, "RECEIPT DETAILS", temp);
                y = printCompanyDetails(y, temp);

                String cpclConfigLabel = temp.toString();

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "From Date")
                        + text(210, y, " : ") + text(230, y, receiptDetailsList.get(0).getFromDate());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                        "To Date")
                        + text(210, y, " : ") + text(230, y, receiptDetailsList.get(0).getToDate());

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Receipt No");
                cpclConfigLabel += text(200, y, "Customer Name");
                cpclConfigLabel += text(490, y, "Total");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (receiptDetailsList.get(0).getReceiptDetailsList().size() > 0) {
                    int index = 0;
                    for (ReceiptDetailsModel.ReceiptDetails receipt : receiptDetailsList.get(0).getReceiptDetailsList()) {

                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, receipt.getReceiptNumber());  // Display the Serial Number
                        cpclConfigLabel += text(200, y, (receipt.getCustomerName().length() > 35)
                                ? receipt.getCustomerName().substring(0,34)
                                : receipt.getCustomerName());

                        // Display the product Name

                        int count = 0;
                        String name = receipt.getCustomerName();
                        int len = name.length();
                        if (len > 35) {
                            int get_len = name.substring(34, len).length();
                            String remark = name.substring(34, len);
                            Log.d("BalanceString", "-->" + len + " " + get_len + " " + remark);
                            String names;

                            for (int j = 0; j < get_len; j = j + 35) {
                                count = count + 35;
                                if (count > get_len) {
                                    names = remark.substring(j, get_len);
                                    cpclConfigLabel += text(
                                            200,
                                            y += LINE_SPACING, names);
                                    Log.d("BalancesDescription", "-->" + names);

                                } else {
                                    names = remark.substring(j, j + 35);
                                    cpclConfigLabel += text(
                                            200,
                                            y += LINE_SPACING, names);
                                    Log.d("BalancesValues", "-->" + names);

                                }
                            }
                        }
                        //  net_qty_value+=Double.parseDouble(receipt.getProductQty());
                        cpclConfigLabel += text(490, y+= LINE_SPACING, receipt.getReceiptTotal()+"");// Display the Sub total of Particular product

                        cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title
                        for (ReceiptDetailsModel.ReceiptDetails.InvoiceDetails details:receiptDetailsList.get(0).getReceiptDetailsList().get(index).getInvoiceDetailsList()){
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, details.getInvoiceNumber()+"");
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, details.getInvoiceDate()+"");
                            cpclConfigLabel += text(490, y+= LINE_SPACING, Utils.twoDecimalPoint(Double.parseDouble(details.getNetTotal()))+"");
                        }
                        cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                        cpclConfigLabel += text(100, y += LINE_SPACING, "Paymode")
                                + text(250, y, " : ")
                                + text(280, y, receipt.getPaymode()+"")
                                + text(490, y, Utils.twoDecimalPoint(Double.parseDouble(receipt.getPaymodeTotal()))+"");

                        cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                        index++;
                    }
                }

                cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1" + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

                os.write(cpclConfigLabel.getBytes("iso-8859-1"));
                os.flush();
            }
            os.close();

        }catch (Exception ex){
            Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }*/


    public void printReceiptSummary(int copy, String fromDate, String toDate, ArrayList<ReceiptSummaryModel> receiptSummaryModels) throws IOException {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TscDll.openport(macAddress);

                int y = 0;
                height = 85;

                finalHeight = height + (receiptSummaryModels.get(0).getReceiptDetailsList().size() * 12) + invoiveSubTotalHeight;
                TscDll.sendcommand("SIZE 80 mm, " + finalHeight + " mm\n");
                TscDll.sendcommand("GAP 0 mm, 0 mm\r\n");//Gap media

                TscDll.clearbuffer();
                TscDll.sendcommand("SPEED 4\r\n");
                TscDll.sendcommand("DENSITY 12\r\n");
                TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                TscDll.sendcommand("SET TEAR ON\r\n");
                TscDll.sendcommand("SET COUNTER @1 1\r\n");
                TscDll.sendcommand("@1 = \"0001\"\r\n");

                StringBuilder temp = new StringBuilder();
                if (company_name.length() > 38) {
                    temp.append((company_name.length() > 38) ? company_name.substring(0, 37) : company_name);
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                    String companyname = company_name.substring(37);
                    temp.append((companyname.length() > 37) ? companyname.substring(0, 36) : companyname);
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + temp.toString() + "\"\n\n");
                } else {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Bold.TTF\",0,10,10,2,\"" + company_name + "\"\n\n");
                }

                if (company_address1 != null && !company_address1.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address1 + "\"\n");
                }

                if (company_address2 != null && !company_address2.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address2 + "\"\n\n");
                }

                if (company_address3 != null && !company_address3.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\"" + company_address3 + "\"\n");
                }

                if (company_phone != null && !company_phone.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" TEL : " + company_phone + "\"\n");
                }

                if (company_gst != null && !company_gst.isEmpty()) {
                    y += LINE_SPACING;
                    TscDll.sendcommand("TEXT 260," + y + ",\"Poppins.TTF\",0,9,9,2,\" GST REG NO : " + company_gst + "\"\n");
                }

                y += LINE_SPACING + 10;
                TscDll.sendcommand("TEXT 280," + y + ",\"Bold.TTF\",0,10,10,2,\"" + "Receipt Summary Report" + "\"\n");

                y += LINE_SPACING + 20;
                // Define the Box
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "From Date: " + fromDate + "\"\n");
                TscDll.sendcommand("TEXT 330," + y + ",\"Poppins.TTF\",0,8,8,\"" + "To Date: " + toDate + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "User: " + username + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 20;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Customer / Receipt No" + "\"\n");
                //   TscDll.sendcommand("TEXT 180,"+y+",\"Poppins.TTF\",0,8,8,\""+"Recpt No"+"\"\n");
                TscDll.sendcommand("TEXT 300," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Mode" + "\"\n");
                TscDll.sendcommand("TEXT 460," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Paid Amt" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                double sum_cashamt = 0.0;
                double sum_chequeamt = 0.0;
                double sum_totalamt = 0.0;

                for (ReceiptSummaryModel.ReceiptDetails receiptDetails : receiptSummaryModels.get(0).getReceiptDetailsList()) {
                    y += 40;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptDetails.getCustomerName() + "\"\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,7,7,\"Inv-No :" + receiptDetails.getInvoiceNo() + "\"\n");
                    y += 30;
                    TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptDetails.getReceiptNo() + "\"\n");
                    TscDll.sendcommand("TEXT 300," + y + ",\"Poppins.TTF\",0,8,8,\"" + receiptDetails.getPaymode() + "\"\n");
                    TscDll.sendcommand("TEXT 470," + y + ",\"Bold.TTF\",0,8,8,\"" + twoDecimalPoint(Double.parseDouble(receiptDetails.getPaidAmount())) + "\"\n");

                    sum_totalamt += Double.parseDouble(receiptDetails.getPaidAmount());

                }
                for (int i = 0; i < receiptSummaryModels.get(0).getReceiptDetailsList().size(); i++) {

                    if (receiptSummaryModels.get(0).getReceiptDetailsList().get(i).getPaymode().equalsIgnoreCase("Cash")) {
                        sum_cashamt += Double.parseDouble(receiptSummaryModels.get(0).getReceiptDetailsList().get(i).getPaidAmount());
                    } else {
                        sum_chequeamt += Double.parseDouble(receiptSummaryModels.get(0).getReceiptDetailsList().get(i).getPaidAmount());
                    }
                }

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Cheque Amount :($)" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(sum_chequeamt) + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Cash Amount :($)" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(sum_cashamt) + "\"\n");

                y += LINE_SPACING + 20;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += 30;
                TscDll.sendcommand("TEXT 100," + y + ",\"Bold.TTF\",0,9,9,\"" + "Total Amount :($)" + "\"\n");
                TscDll.sendcommand("TEXT 450," + y + ",\"Bold.TTF\",0,9,9,\"" + twoDecimalPoint(sum_totalamt) + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("BAR 0," + y + ",800,2\n");

                y += LINE_SPACING + 30;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "------------" + "\"\n");

                y += LINE_SPACING;
                TscDll.sendcommand("TEXT 0," + y + ",\"Poppins.TTF\",0,8,8,\"" + "Received By" + "\"\n");

                TscDll.printlabel(1, copy);
                TscDll.closeport(5000);
            }
        }, 100);
    }
   /* public void printReceiptSummary(int copy, ArrayList<ReceiptSummaryModel> receiptDetailsList){
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            for (int n = 0; n < copy; n++) {
                int y = 0;
                double net_paid_amount=0.00;
                double net_check_amount=0.00;
                double net_cash_amount=0.00;

                StringBuilder temp = new StringBuilder();
                y = printTitle(130, y, "RECEIPT SUMMARY REPORT", temp);
                y = printCompanyDetails(y, temp);

                String cpclConfigLabel = temp.toString();

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "From Date")
                        + text(210, y, " : ") + text(230, y, receiptDetailsList.get(0).getFromDate());

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "To Date")
                        + text(210, y, " : ") + text(230, y, receiptDetailsList.get(0).getToDate());

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Rcpt No");
                cpclConfigLabel += text(110, y, "Mode");
                cpclConfigLabel += text(210, y, "Cust Name");
                cpclConfigLabel += text(450, y, "Paid Amt");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (receiptDetailsList.get(0).getReceiptDetailsList().size() > 0) {
                    int index = 0;
                    for (ReceiptSummaryModel.ReceiptDetails receipt : receiptDetailsList.get(0).getReceiptDetailsList()) {

                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, receipt.getReceiptNo());  // Display the Serial Number
                        cpclConfigLabel += text(110, y, receipt.getPaymode()+"");// Display the Sub total of Particular product

                        cpclConfigLabel += text(210, y, (receipt.getCustomerName().length() > 35)
                                ? receipt.getCustomerName().substring(0,34)
                                : receipt.getCustomerName());

                        // Display the product Name

                        int count = 0;
                        String name = receipt.getCustomerName();
                        int len = name.length();
                        if (len > 35) {
                            int get_len = name.substring(34, len).length();
                            String remark = name.substring(34, len);
                            Log.d("BalanceString", "-->" + len + " " + get_len + " " + remark);
                            String names;

                            for (int j = 0; j < get_len; j = j + 35) {
                                count = count + 35;
                                if (count > get_len) {
                                    names = remark.substring(j, get_len);
                                    cpclConfigLabel += text(210, y += LINE_SPACING, names);
                                    Log.d("BalancesDescription", "-->" + names);
                                } else {
                                    names = remark.substring(j, j + 35);
                                    cpclConfigLabel += text(210, y += LINE_SPACING, names);
                                    Log.d("BalancesValues", "-->" + names);
                                }
                            }
                        }
                        if (receipt.getPaymode().equalsIgnoreCase("Cheque")){
                            net_check_amount+=Double.parseDouble(receipt.getPaidAmount());
                        }else if (receipt.getPaymode().equalsIgnoreCase("Cash")){
                            net_cash_amount+=Double.parseDouble(receipt.getPaidAmount());
                        }
                        net_paid_amount+=Double.parseDouble(receipt.getPaidAmount());
                        cpclConfigLabel += text(450, y+= LINE_SPACING, Utils.twoDecimalPoint(Double.parseDouble(receipt.getPaidAmount()))+"");// Display the Sub total of Particular product
                        index++;
                    }
                    cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                    cpclConfigLabel += text(100, y += LINE_SPACING, "Cheque Amount")
                            + text(280, y, " : ")
                            + text(450, y, Utils.twoDecimalPoint(net_check_amount)+"");

                    cpclConfigLabel += text(100, y += LINE_SPACING, "Cash Amount")
                            + text(250, y, " : ")
                            + text(450, y, Utils.twoDecimalPoint(net_cash_amount)+"");

                    cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                    cpclConfigLabel += text(100, y += LINE_SPACING, "Total Amount")
                            + text(250, y, " : ")
                            + text(450, y, Utils.twoDecimalPoint(net_paid_amount)+"");

                    cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
                }

                cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1" + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;
                os.write(cpclConfigLabel.getBytes("iso-8859-1"));
                os.flush();
            }
            os.close();
        }catch (Exception ex){
            Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }*/

    public int PrintText(OutputStream out, String Data, int Alignment, int Attribute, int TextSize) {
        byte[] by = new byte[]{(byte) 27, (byte) 97, (byte) 48, (byte) 27, (byte) 33, (byte) 0, (byte) 29, (byte) 66, (byte) 0, (byte) 29, (byte) 33, (byte) 0};
        by[2] = (byte) (by[2] + Alignment);
        if (Attribute > 0) {
            if ((Attribute & 4) > 0) {
                by[5] = (byte) (by[5] + 128);
                Log.e("Test", "Mode:" + String.valueOf(by[5]));
            }

            if ((Attribute & 1) > 0) {
                ++by[5];
                Log.e("Test", "Mode:" + String.valueOf(by[5]));
            }

            if ((Attribute & 2) > 0) {
                by[5] = (byte) (by[5] + 8);
                Log.e("Test", "Mode:" + String.valueOf(by[5]));
            }
        }

        if ((Attribute & 8) > 0) {
            by[8] = 1;
            Log.e("Test", "Reverse:" + String.valueOf(by[8]));
        }

        by[11] = (byte) TextSize;

        try {
            byte[] e = Data.getBytes(Objects.requireNonNull(System.getProperty("file.encoding")));
            out.write(by, 0, 12);

            out.write(Data.getBytes());
            out.flush();
            //this.SendData(Data.getBytes(),out);
            return 0;
        } catch (IOException var7) {
            var7.printStackTrace();
            return 402;
        }
    }


    private int SendData(byte[] send, OutputStream out) {
        try {
            short e = 512;
            int nPos = 0;
            boolean nLen = false;
            int nNum = (send.length + e - 1) / e;

            for (int i = 0; i < nNum; ++i) {
                int var8;
                if (i == nNum - 1) {
                    var8 = send.length - i * e;
                } else {
                    var8 = e;
                }

                out.write(send, nPos, var8);
                nPos += var8;
            }

            return 0;
        } catch (IOException var7) {
            var7.printStackTrace();
            return 402;
        }
    }


   /* private int SendData(String Data,OutputStream out) {
       // if (g_nConnect != 1) {
          //  return 101;
      //  } else {
            try {
                byte[] e = Data.getBytes(System.getProperty("file.encoding"));
                short nPackSize = 512;
                int nPos = 0;
                boolean nLen = false;
                int nNum = (e.length + nPackSize - 1) / nPackSize;

                for (int i = 0; i < nNum; ++i) {
                    int var9;
                    if (i == nNum - 1) {
                        var9 = e.length - i * nPackSize;
                    } else {
                        var9 = nPackSize;
                    }

                    out.write(e, nPos, var9);
                    nPos += var9;
                }

                return 0;
            } catch (IOException var8) {
                var8.printStackTrace();
                return 402;
            }
       // }
    }*/


    private void sendData(byte[] buffer, OutputStream os) throws IOException {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
            os.write(byteBuffer.array());
            os.flush();
            // tell the user data were sent
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public byte[] convertExtendedAscii(String input)
    {
        int length = input.length();
        byte[] retVal = newbyte[length];

        for(int i=0; i<length; i++)
        {
            char c = input.charAt(i);

            if (c < 127)
            {
                retVal[i] = (byte)c;
            }
            else
            {
                retVal[i] = (byte)(c - 256);
            }
        }

        return retVal;
    }*/


    private int printCompanyDetails(int y, StringBuilder cpclConfigLabel) {
        try {
            if (company_name.length() > 38) {
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, (company_name.length() > 38) ? company_name.substring(0, 37) : company_name));
                String companyname = company_name.substring(37);
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, (companyname.length() > 37) ? companyname.substring(0, 36) : companyname));
            } else {
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, company_name));
            }

            if (company_address1 != null && !company_address1.isEmpty()) {
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, company_address1));
            }

            if (company_address2 != null && !company_address2.isEmpty()) {
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, company_address2));
            }

            if (company_address3 != null && !company_address3.isEmpty()) {
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, company_address3));
            }

            if (company_phone != null && !company_phone.isEmpty()) {
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, "TEL : " + company_phone));
            }

            if (company_gst != null && !company_gst.isEmpty()) {
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, "GST REG NO : " + company_gst));
            }

        } catch (Exception exception) {
        }
        return y;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private String textCenter(int x, int y, String text) {

        int length = (PAPER_WIDTH - text.length()) / 2;
        //	System.out.println(length);
        // Create a new StringBuilder.
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(" ");
        }
        // Convert to string.
        String result = builder.toString() + text;

        return new StringBuilder(CMD_TEXT).append(SPACE).append(FONT)
                .append(SPACE).append(FONT_SIZE).append(SPACE).append(x)
                .append(SPACE).append(y).append(SPACE).append(result)
                .append(LINE_SEPARATOR).toString();
    }

    private int printTitle(int x, int y, String title, StringBuilder cpclConfigLabel) {
        cpclConfigLabel.append(text(x, y += LINE_SPACING, title));
        return y;
    }

    private String text(int x, int y, double number) {
        return text(x, y, String.format("%.2f", number));
    }

    private String text(int x, int y, String text) {
        return new StringBuilder(CMD_TEXT).append(SPACE).append(FONT)
                .append(SPACE).append(FONT_SIZE).append(SPACE).append(x)
                .append(SPACE).append(y).append(SPACE).append(text)
                .append(LINE_SEPARATOR).toString();
    }

    private String horizontalLine(int y, int thickness) {
        return new StringBuilder(CMD_LINE).append(SPACE).append(LEFT_MARGIN)
                .append(SPACE).append(y).append(SPACE).append(RIGHT_MARGIN)
                .append(SPACE).append(y).append(SPACE).append(thickness)
                .append(LINE_SEPARATOR).toString();
    }

    private String verticalLine(int y, int thickness) {
        return new StringBuilder(CMD_LINE).append(SPACE).append(RIGHT_MARGIN)
                .append(SPACE).append(y).append(SPACE).append(LEFT_MARGIN)
                .append(SPACE).append(y).append(SPACE).append(thickness)
                .append(LINE_SEPARATOR).toString();
    }

   /* private boolean disableBluetooth() {
        if ((bluetoothAdapter != null) && bluetoothAdapter.isEnabled()) {
            return bluetoothAdapter.disable();
        }
        return false;
    }*/

    /**
     * Method to write with a given format
     *
     * @param buffer     the array of bytes to actually write
     * @param pFormat    The format byte array
     * @param pAlignment The alignment byte array
     * @return true on successful write, false otherwise
     */
    public boolean writeWithFormat(OutputStream mOutputStream, byte[] buffer, final byte[] pFormat, final byte[] pAlignment) {
        try {
            // Notify printer it should be printed with given alignment:
            mOutputStream.write(pAlignment);
            // Notify printer it should be printed in the given format:
            mOutputStream.write(pFormat);
            // Write the actual data:
            mOutputStream.write(buffer, 0, buffer.length);

            // Share the sent message back to the UI Activity
            //  App.getInstance().getHandler().obtainMessage(MESSAGE_WRITE, buffer.length, -1, buffer).sendToTarget();
            return true;
        } catch (IOException e) {
            Log.e("TAG", "Exception during write", e);
            return false;
        }
    }

    /**
     * Class for formatting
     */
    public static class Formatter {
        /**
         * The format that is being build on
         */
        private byte[] mFormat;

        public Formatter() {
            // Default:
            mFormat = new byte[]{27, 33, 0};
        }

        /**
         * Method to get the Build result
         *
         * @return the format
         */
        public byte[] get() {
            return mFormat;
        }

        public Formatter bold() {
            // Apply bold:
            mFormat[2] = ((byte) (0x8 | mFormat[2]));
            return this;
        }

        public Formatter small() {
            mFormat[2] = ((byte) (0x1 | mFormat[2]));
            return this;
        }

        public Formatter height() {
            mFormat[2] = ((byte) (0x10 | mFormat[2]));
            return this;
        }

        public Formatter width() {
            mFormat[2] = ((byte) (0x20 | mFormat[2]));
            return this;
        }

        public Formatter underlined() {
            mFormat[2] = ((byte) (0x80 | mFormat[2]));
            return this;
        }

        public static byte[] rightAlign() {
            return new byte[]{0x1B, 'a', 0x02};
        }

        public static byte[] leftAlign() {
            return new byte[]{0x1B, 'a', 0x00};
        }

        public static byte[] centerAlign() {
            return new byte[]{0x1B, 'a', 0x01};
        }
    }
}
