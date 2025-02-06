package com.winapp.svcatering.zebraprinter;

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
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.winapp.svcatering.R;
import com.winapp.svcatering.activity.AddInvoiceActivityOld;
import com.winapp.svcatering.activity.CartActivity;
import com.winapp.svcatering.fragments.SummaryFragment;
import com.winapp.svcatering.model.CurrencyModel;
import com.winapp.svcatering.model.ExpenseModel;
import com.winapp.svcatering.model.InvoiceByProductModel;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;
import com.winapp.svcatering.model.InvoiceSummaryModel;
import com.winapp.svcatering.model.ReceiptDetailsModel;
import com.winapp.svcatering.model.ReceiptSummaryModel;
import com.winapp.svcatering.receipts.ReceiptPrintPreviewModel;
import com.winapp.svcatering.model.SalesOrderPrintPreviewModel;
import com.winapp.svcatering.salesreturn.SalesReturnPrintPreviewModel;
import com.winapp.svcatering.printpreview.InvoicePrintPreviewActivity;
import com.winapp.svcatering.utils.SessionManager;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ZebraPrinterActivity {

    public interface OnCompletionListener {
        public void onCompleted();
    }

    private Context context;
    private String macAddress;
    private ZebraPrinter printer;
    private BluetoothAdapter bluetoothAdapter;
    public static String configLabel ="";
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
    Bitmap logo,image;
    String logoStr = "",mUser="";
    private static final String HEADER = "PW " + LABEL_WIDTH + LINE_SEPARATOR
            + "TONE 0" + LINE_SEPARATOR + "SPEED 3" + LINE_SEPARATOR + "ZPL"
            + POSTFEED + LINE_SEPARATOR + "NO-PACE" + "BAR-SENSE"
            + LINE_SEPARATOR;
    byte FONT_TYPE;
    String custlastname;
    private OnCompletionListener listener;
    private String company_name;
    private String company_address1;
    private String company_address2;
    private String company_address3;
    private String company_phone;
    private String company_gst;
    private SessionManager session;
    private HashMap<String ,String> user;


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
                        Log.d("BluetoothTurnedOn","bluetooth()");
                        print();
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


    public ZebraPrinterActivity(Context context, String macAddress) {

        try {
            session=new SessionManager(context);
            user=session.getUserDetails();
            company_name=user.get(SessionManager.KEY_COMPANY_NAME);
            company_address1=user.get(SessionManager.KEY_ADDRESS1);
            company_address2=user.get(SessionManager.KEY_ADDRESS2);
            company_address3=user.get(SessionManager.KEY_ADDRESS3);
            company_phone=user.get(SessionManager.KEY_PHONE_NO);
            company_gst=user.get(SessionManager.KEY_COMPANY_REG_NO);

            if (!macAddress.matches("[0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(\\1[0-9A-Fa-f]{2}){4}$")) {
                //  throw new IllegalArgumentException(macAddress + "Invalid Mac Address");
                Toast.makeText(context,"Please connect the printer and try again",Toast.LENGTH_SHORT).show();
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
            print();
        }catch (Exception ex){}
    }

    private void print() {

        ProgressDialog dialog;
        dialog=new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Connecting to Printer...");
        dialog.show();

        if (context instanceof InvoicePrintPreviewActivity){
            InvoicePrintPreviewActivity printPreviewActivity=new InvoicePrintPreviewActivity();
            printPreviewActivity.closeAlert();
        }else if (context instanceof AddInvoiceActivityOld){
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

                    int width = 570, imgWidth, imgHeight;
                    int diff;

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
                                connection
                                        .write("! U1 setvar \"zpl.label_length\" \"90\""
                                                .getBytes());

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
                    }
                  /*  }else if (printerLanguage == PrinterLanguage.CPCL) {
                        Bitmap bmp  = BitmapFactory.decodeResource(context.getResources(), R.drawable.aadhi_print_logo);
                        if(bmp!=null){
                            image = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), true);
                            connection.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                            printer.printImage(new ZebraImageAndroid(image), 0, 0, -1, -1, false);
                        }
                    }*/

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

                            } /*else if (printerLanguage == PrinterLanguage.CPCL) {

                                Bitmap photo = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

                                if (photo != null) {
                                    Log.d("Print in CPCL Printer", "CPCL");
                                    image = Bitmap.createScaledBitmap(photo, 300, 80, true);
                                    printer.printImage(new ZebraImageAndroid(image), 0, 0, -1, -1, false);
                                }
                                connection.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                                Utils.setSignature("");
                            }*/
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
                    if (context instanceof AddInvoiceActivityOld){
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
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public void printSalesReturn(int copy, ArrayList<SalesReturnPrintPreviewModel> salesReturnHeader, ArrayList<SalesReturnPrintPreviewModel.SalesReturnDetails> salesReturnList) throws IOException {
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
    }

    // Print Receipt Details
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
                    }else if (receiptHeaderDetails.get(0).getPayMode().equalsIgnoreCase("BankTransfer")){
                        cpclConfigLabel += text(LEFT_MARGIN,  y += LINE_SPACING, "Mode");
                        cpclConfigLabel += text(350, y, "Tran Date");

                        cpclConfigLabel += text(LEFT_MARGIN, y+= LINE_SPACING, receiptHeaderDetails.get(0).getPaymentType());  // Display the Qty of Invoice

                        cpclConfigLabel += text(350, y, receiptHeaderDetails.get(0).getBankTransferDate());  // Display the Qty of Invoice

                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

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

    public void printInvoice(int copy,ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails, ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList, String isDoPrint){
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

                if (invoiceHeaderDetails.get(0).getSoNumber()!=null && !invoiceHeaderDetails.get(0).getSoNumber().isEmpty()){
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SO No")
                            + text(150, y, " : ") + text(180, y, invoiceHeaderDetails.get(0).getSoNumber());
                }

                if (invoiceHeaderDetails.get(0).getDoNumber()!=null && !invoiceHeaderDetails.get(0).getDoNumber().isEmpty()){
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
                    String deliveryAddress=invoiceHeaderDetails.get(0).getAddress1();
                    cpclConfigLabel += (text(LEFT_MARGIN, y += LINE_SPACING, "Address 1"));
                    cpclConfigLabel += (text(200, y, " : "));
                    Log.d("CustomerAddress1", deliveryAddress);
                    cpclConfigLabel += (text(230, y, (deliveryAddress.length() > 22) ? deliveryAddress.substring(0, 22) : deliveryAddress));
                    if (deliveryAddress.length() > 22) {
                        String deladdress = deliveryAddress.substring(22);
                        cpclConfigLabel += (text(230, y += LINE_SPACING, (deladdress.length() > 22) ? deladdress.substring(0, 21) : deladdress));
                    }
                }

                if (invoiceHeaderDetails.get(0).getAddress2() != null && !invoiceHeaderDetails.get(0).getAddress2().isEmpty()) {
                    String deliveryAddress=invoiceHeaderDetails.get(0).getAddress2();
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
                    String deliveryAddress=invoiceHeaderDetails.get(0).getAddress3();
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
                                ? invoice.getDescription().substring(0,34)
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
                                    cpclConfigLabel += text(70, y += LINE_SPACING, names);
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

                        if (Double.parseDouble(invoice.getNetQty()) < 0){
                            cpclConfigLabel += text(150, y += LINE_SPACING, "(as Return)");
                            cpclConfigLabel += text(310, y, Utils.getQtyValue(invoice.getNetQty()));  // Display the Qty of Invoice
                        }else {
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

                cpclConfigLabel += text(260, y, "GST("+invoiceHeaderDetails.get(0).getTaxType()+":"+ twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getTaxValue()))+" %)")
                        + text(420, y, " : ")
                        + text(470, y, twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getNetTax())));


                cpclConfigLabel += text(260, y += LINE_SPACING, "Net Total")
                        + text(420, y, " : ")
                        + text(470, y, twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getNetTotal())));


                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Total Outstanding")
                        + text(200, y, " : ")
                        + text(220, y, twoDecimalPoint(Double.parseDouble(invoiceHeaderDetails.get(0).getOutStandingAmount())));

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

        }catch (Exception ex){
            Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    /**
     *
     * Set the Function for the Delivery Order
     * @return
     */

    public void printDeliveryOrder(int copy,ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails, ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList){
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);

            for (int n = 0; n < copy; n++) {

                int y = 0;
                double net_qty_value=0;

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
                    String deliveryAddress=invoiceHeaderDetails.get(0).getAddress1();
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
                    String deliveryAddress=invoiceHeaderDetails.get(0).getAddress2();
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
                    String deliveryAddress=invoiceHeaderDetails.get(0).getAddress3();
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
                                ? invoice.getDescription().substring(0,34)
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
                        net_qty_value+=Double.parseDouble(invoice.getNetQty());
                        cpclConfigLabel += text(490, y, (int)Double.parseDouble(invoice.getNetQty())+""); // Display the Sub total of Particular product
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
    }


    public void printSettlement(int copy, String settlementNo,String settlementDate,String locationCode,String user,ArrayList<CurrencyModel> currencyList, ArrayList<ExpenseModel> expenseList){
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);

            for (int n = 0; n < copy; n++) {

                int y = 0;
                double net_total_values =0.00;
                double net_expense_value=0.00;
                double net_amount=0.00;

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

                        cpclConfigLabel += text(100, y, currency.getCurrencyName()+"");
                        cpclConfigLabel += text(350, y, currency.getCount()+""); // Display the Sub total of Particular product

                        cpclConfigLabel += text(450, y, Utils.twoDecimalPoint(Double.parseDouble(currency.getTotal()))+""); // Display the Sub total of Particular product

                        net_total_values +=Double.parseDouble(currency.getTotal());
                        index++;
                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel += text(230, y += LINE_SPACING, "Total Amount")
                        + text(400, y, " : ")
                        + text(450, y, Utils.twoDecimalPoint(net_total_values) +"");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (expenseList.size()>0){
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
                    cpclConfigLabel += text(100, y, "Expense");
                    cpclConfigLabel += text(450, y, "Amount");

                    cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title
                    int index = 1;
                    for (ExpenseModel expense : expenseList) {

                        if (Double.parseDouble(expense.getExpenseTotal())>0){
                            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, String.valueOf(index));  // Display the Serial Number

                            cpclConfigLabel += text(100, y, expense.getExpenseName()+"");
                            cpclConfigLabel += text(450, y, expense.getExpenseTotal()+""); // Display the Sub total of Particular product
                            net_expense_value +=Double.parseDouble(expense.getExpenseTotal());
                            index++;
                        }
                    }

                    cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                    cpclConfigLabel += text(200, y += LINE_SPACING, "Total Expense")
                            + text(420, y, " : ")
                            + text(450, y, Utils.twoDecimalPoint(net_expense_value) +"");

                    cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title
                }

                cpclConfigLabel += text(200, y += LINE_SPACING, "Total Amount")
                        + text(420, y, " : ")
                        + text(450, y, Utils.twoDecimalPoint(net_total_values) +"");

                cpclConfigLabel += text(200, y += LINE_SPACING, "Total Expense")
                        + text(420, y, " : ")
                        + text(450, y, Utils.twoDecimalPoint(net_expense_value) +"");

                net_amount=net_total_values+net_expense_value;

                cpclConfigLabel += text(200, y += LINE_SPACING, "Net Amount")
                        + text(420, y, " : ")
                        + text(450, y, Utils.twoDecimalPoint(net_amount) +"");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1" + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

                os.write(cpclConfigLabel.getBytes("iso-8859-1"));
                os.flush();
            }
            os.close();

        }catch (Exception ex){
            Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
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
                }*/


                if (salesOrderHeaderDetails.get(0).getAddress1() != null && !salesOrderHeaderDetails.get(0).getAddress1().isEmpty()) {
                    String deliveryAddress=salesOrderHeaderDetails.get(0).getAddress1();
                    cpclConfigLabel += (text(LEFT_MARGIN,
                            y += LINE_SPACING, "Address 1"));
                    cpclConfigLabel += (text(200, y, " : "));
                    Log.d("CustomerAddress1", deliveryAddress);
                    cpclConfigLabel += (text(230, y, (deliveryAddress.length() > 22) ? deliveryAddress.substring(0, 22) : deliveryAddress));
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

                 /*       if (Integer.parseInt(sales.getPcsperCarton()) == 1) {
                            cpclConfigLabel += text(380, y, Utils.twoDecimalPoint(Double.parseDouble(sales.getCartonPrice()))); // Display the Particular product price
                        } else {
                            cpclConfigLabel += text(380, y, Utils.twoDecimalPoint(Double.parseDouble(sales.getUnitPrice()))); // Display the Particular product price

                        }
*/
                        cpclConfigLabel += text(380, y, twoDecimalPoint(Double.parseDouble(sales.getPricevalue()))); // Display the Particular product price


                        cpclConfigLabel += text(486, y, twoDecimalPoint(Double.parseDouble(sales.getTotal()))); // Display the Sub total of Particular product

                        index++;
                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

               /* if (salesOrderHeaderDetails.get(0).getTaxType().equals("I")){
                    double sub_total=Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTotal()) - Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax());
                    cpclConfigLabel += text(260, y += LINE_SPACING, "Sub Total")
                            + text(420, y, " : ")
                            + text(470, y, Utils.twoDecimalPoint(sub_total));
                }else {
                    cpclConfigLabel += text(260, y += LINE_SPACING, "Sub Total")
                            + text(420, y, " : ")
                            + text(470, y, Utils.twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal())));

                }*/

                // Item Discount Text
                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "")
                        + text(140, y, ".")
                        + text(180, y, "");

               /* if (salesOrderHeaderDetails.get(0).getTaxType().equals("I")) {

                    double sub_total = Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTotal()) - Double.parseDouble(salesOrderHeaderDetails.get(0).getNetTax());

                    cpclConfigLabel += text(260, y, "Sub Total")
                            + text(420, y, " : ")
                            + text(470, y, Utils.twoDecimalPoint(sub_total));
                }else {
                    cpclConfigLabel += text(260, y, "Sub Total")
                            + text(420, y, " : ")
                            + text(470, y, Utils.twoDecimalPoint(Double.parseDouble(salesOrderHeaderDetails.get(0).getSubTotal())));
                }*/

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


    public void printInvoiceByProduct(int copy, ArrayList<InvoiceByProductModel> invoiceProductList){
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
    }

    public void printInvoiceSummary(int copy, ArrayList<InvoiceSummaryModel> invoiceSummaryList){
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            for (int n = 0; n < copy; n++) {
                int y = 0;
                double netpaidamount=0.00;
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
                        cpclConfigLabel += text(220, y ,Utils.twoDecimalPoint(Double.parseDouble(invoice.getNetTotal()))+"");
                        cpclConfigLabel += text(340, y,Utils.twoDecimalPoint(Double.parseDouble(invoice.getPaidAmount()))+"");
                        cpclConfigLabel += text(460, y,Utils.twoDecimalPoint(Double.parseDouble(invoice.getBalanceAmount()))+"");

                        try {
                            // Sathish 29-08-2020 Changing nettotal to balance amount total
                            if (Double.parseDouble(invoice.getBalanceAmount())!=0) {
                                nettotal += Double.parseDouble(invoice.getBalanceAmount());
                            }
                            // Sathish 04-09-2020 Display the net paid amount in this printer display
                            if (Double.parseDouble(invoice.getPaidAmount())!=0){
                                netpaidamount+=Double.parseDouble(invoice.getPaidAmount());
                            }
                        }catch (Exception ex){
                            Log.e("Error_in_calculating",ex.getMessage());
                        }

                        index++;
                    }
                }

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title
                cpclConfigLabel += text(220, y += LINE_SPACING, "Total")
                        + text(240, y, " : ")
                        + text(340,y,  twoDecimalPoint(netpaidamount))
                        + text(460, y, twoDecimalPoint(nettotal));

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1" + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

                os.write(cpclConfigLabel.getBytes("iso-8859-1"));
                os.flush();
            }
            os.close();

        }catch (Exception ex){
            Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    public void printCustomerStatement(int copy, ArrayList<InvoiceSummaryModel> invoiceSummaryList){
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            for (int n = 0; n < copy; n++) {
                int y = 0;
                double netpaidamount=0.00;
                double nettotal = 0.00;
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
                cpclConfigLabel += text(220, y, "Net Total");
                cpclConfigLabel += text(340, y, "Paid");
                cpclConfigLabel += text(460, y, "Balance");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (invoiceSummaryList.get(0).getSummaryList().size() > 0) {
                    int index = 1;
                    for (InvoiceSummaryModel.SummaryDetails invoice : invoiceSummaryList.get(0).getSummaryList()) {
                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, invoice.getInvoiceNumber().toString());
                        cpclConfigLabel += text(220, y ,Utils.twoDecimalPoint(Double.parseDouble(invoice.getNetTotal()))+"");
                        cpclConfigLabel += text(340, y,Utils.twoDecimalPoint(Double.parseDouble(invoice.getPaidAmount()))+"");
                        cpclConfigLabel += text(460, y,Utils.twoDecimalPoint(Double.parseDouble(invoice.getBalanceAmount()))+"");

                        try {
                            // Sathish 29-08-2020 Changing nettotal to balance amount total
                            if (Double.parseDouble(invoice.getBalanceAmount())!=0) {
                                nettotal += Double.parseDouble(invoice.getBalanceAmount());
                            }
                            // Sathish 04-09-2020 Display the net paid amount in this printer display
                            if (Double.parseDouble(invoice.getPaidAmount())!=0){
                                netpaidamount+=Double.parseDouble(invoice.getPaidAmount());
                            }

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
                        + text(220, y, twoDecimalPoint(nettotalvalue))
                        + text(340,y,  twoDecimalPoint(netpaidamount))
                        + text(460, y, twoDecimalPoint(nettotal));

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1" + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

                os.write(cpclConfigLabel.getBytes("iso-8859-1"));
                os.flush();
            }
            os.close();

        }catch (Exception ex){
            Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    // Print the receipt Details
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
    }

    public void printReceiptSummary(int copy, ArrayList<ReceiptSummaryModel> receiptDetailsList){
        try {

            FileOutputStream os = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            for (int n = 0; n < copy; n++) {
                int y = 0;
                double net_paid_amount=0.00;

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

                cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Receipt No");
                cpclConfigLabel += text(200, y, "Customer Name");
                cpclConfigLabel += text(450, y, "Paid Amt");

                cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

                if (receiptDetailsList.get(0).getReceiptDetailsList().size() > 0) {
                    int index = 0;
                    for (ReceiptSummaryModel.ReceiptDetails receipt : receiptDetailsList.get(0).getReceiptDetailsList()) {

                        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, receipt.getReceiptNo());  // Display the Serial Number
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
                                    cpclConfigLabel += text(200, y += LINE_SPACING, names);
                                    Log.d("BalancesDescription", "-->" + names);
                                } else {
                                    names = remark.substring(j, j + 35);
                                    cpclConfigLabel += text(200, y += LINE_SPACING, names);
                                    Log.d("BalancesValues", "-->" + names);
                                }
                            }
                        }
                        net_paid_amount+=Double.parseDouble(receipt.getPaidAmount());
                        cpclConfigLabel += text(450, y+= LINE_SPACING, Utils.twoDecimalPoint(Double.parseDouble(receipt.getPaidAmount()))+"");// Display the Sub total of Particular product
                        index++;
                    }
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
    }

    public int PrintText(OutputStream out,String Data, int Alignment, int Attribute, int TextSize) {
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


    private int SendData(byte[] send,OutputStream out) {
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
            }else {
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, company_name));
            }

            if (company_address1!=null && !company_address1.isEmpty()){
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, company_address1));
            }

            if(company_address2!=null && !company_address2.isEmpty()){
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, company_address2));
            }

            if (company_address3!=null && !company_address3.isEmpty()){
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, company_address3));
            }

            if (company_phone!=null && !company_phone.isEmpty()){
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, "TEL : "+company_phone));
            }

            if (company_gst!=null && !company_gst.isEmpty()){
                cpclConfigLabel.append(text(LEFT_MARGIN, y += LINE_SPACING, "CO REG NO : "+company_gst));
            }

        }catch (Exception exception){}
        return y;
    }

    private String textCenter(int x, int y, String text) {

        int length = (PAPER_WIDTH-text.length())/2;
        //	System.out.println(length);
        // Create a new StringBuilder.
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++) {
            builder.append(" ");
        }
        // Convert to string.
        String result = builder.toString()+text;

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

    private boolean disableBluetooth() {
        if ((bluetoothAdapter != null) && bluetoothAdapter.isEnabled()) {
            return bluetoothAdapter.disable();
        }
        return false;
    }

    /**
     * Method to write with a given format
     *
     * @param buffer     the array of bytes to actually write
     * @param pFormat    The format byte array
     * @param pAlignment The alignment byte array
     * @return true on successful write, false otherwise
     */
    public boolean writeWithFormat(OutputStream mOutputStream,byte[] buffer, final byte[] pFormat, final byte[] pAlignment) {
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
        /** The format that is being build on */
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

        public static byte[] rightAlign(){
            return new byte[]{0x1B, 'a', 0x02};
        }

        public static byte[] leftAlign(){
            return new byte[]{0x1B, 'a', 0x00};
        }

        public static byte[] centerAlign(){
            return new byte[]{0x1B, 'a', 0x01};
        }
    }
}
