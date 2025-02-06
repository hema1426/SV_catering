package com.winapp.svcatering.printpreview;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.example.tscdll.TSCActivity;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;

public class TSCPrinter {
    // Define the MAC address
    private String printerMacAddress;
    // Define the Context
    private Context context;
    // Define the Printer address
    private TSCActivity TscDll;
    // Define the Constructor
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

    public TSCPrinter(Context context,String printerMacAddress){
        this.context=context;
        this.printerMacAddress=printerMacAddress;
        TscDll = new TSCActivity();
    }

    /***
     * Printing the Invoice from the Print previewActivity
     * @get all values of the Print Invoice Details
     * @get Invoice Header
     * @get Invoice Details
     * @get Print details of the invoice
     */

   // private ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails;
   // private ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList;

    public void printInvoice(ArrayList<InvoicePrintPreviewModel> invoiceHeaderDetails,ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList){
        try {

           // TEXT 290,50,”TST24.BF2",0,1,1,3,”0001(1/1)”
           // TEXT 25,78,”TST24.BF2",0,1,1,”檸檬汁(中)”
           // TEXT 25,106,”TST24.BF2",0,1,1,””
           // TEXT 25,134,”TST24.BF2",0,1,1,”售價$45 (外帶)”
           // TEXT 25,162,”TST24.BF2",0,1,1,”一小時內飲用最佳”
           // TEXT 25,190,”TST24.BF2",0,1,1,”日傑茶坊 TEL:0000–0000"

            String text="value";
            int y=0;

            TscDll.openport(printerMacAddress);
            //  TscDll.downloadpcx("UL.PCX");
            // TscDll.downloadbmp("Triangle.bmp");
            // TscDll.downloadttf("ARIAL.TTF");
            TscDll.setup(70, 110, 4, 4, 0, 0, 0);
            TscDll.clearbuffer();
            TscDll.sendcommand("SET TEAR ON\n");
            TscDll.sendcommand("SET COUNTER @1 1\n");
            TscDll.sendcommand("@1 = \"0001\"\n");
            TscDll.sendcommand("TEXT 100,300,\"3\",0,1,1,@1\n");

            StringBuilder temp = new StringBuilder();
            y = printTitle(170, y, "RECEIPT SUMMARY", temp);
            y = printCompanyDetails(y, temp);
            String cpclConfigLabel = temp.toString();

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                    "Invoice No")
                    + text(150, y, " : ")
                    + text(180, y, "IN00-276");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                    "Invoice Date")
                    + text(150, y, " : ")
                    + text(180, y, "28-10-2020");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                    "Customer Name")
                    + text(150, y, " : ")
                    + text(180, y, "Testing");

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                    "Delivery Address")
                    + text(150, y, " : ")
                    + text(180, y, "Test Address");

            cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS); // Line of the Title

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "SNo");
            cpclConfigLabel += text(70, y, "Description");
            cpclConfigLabel += text(250, y, "Qty");
            cpclConfigLabel += text(365, y, "Price");
            cpclConfigLabel += text(500, y, "Total");
            if (invoiceList.size()>0){
                int index=1;
                for (InvoicePrintPreviewModel.InvoiceList invoice:invoiceList){

                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, String.valueOf(index));  // Display the Serial Number

                    cpclConfigLabel += text(70, y, (invoice.getDescription().length() > 10)
                                                    ? invoice.getDescription().substring(0, 9)
                                                    : invoice.getDescription());                      // Display the product Name

                    int count=0;
                    String name = invoice.getDescription();
                    int len =name.length();
                    if(len>10) {
                        int get_len = name.substring(9, len).length();
                        String remark = name.substring(9, len);
                        Log.d("BalanceString", "-->" + len + " " + get_len + " " + remark);
                        String names;

                        for (int j = 0; j < get_len; j = j + 10) {
                            count = count + 10;
                            if (count > get_len) {
                                names = remark.substring(j, get_len);
                                cpclConfigLabel += text(
                                        100,
                                        y += LINE_SPACING, names);
                                Log.d("BalancesDescription", "-->" + names);

                            } else {
                                names = remark.substring(j, j + 10);
                                cpclConfigLabel += text(
                                        100,
                                        y += LINE_SPACING, names);
                                Log.d("BalancesValues", "-->" + names);

                            }
                        }
                    }

                    cpclConfigLabel += text(250, y , invoice.getNetQty());  // Display the Qty of Invoice

                    if (Integer.parseInt(invoice.getPcsperCarton())==1){
                        cpclConfigLabel += text(380, y, Utils.twoDecimalPoint(Double.parseDouble(invoice.getCartonPrice()))); // Display the Particular product price
                    }else {
                        cpclConfigLabel += text(380, y, Utils.twoDecimalPoint(Double.parseDouble(invoice.getUnitPrice()))); // Display the Particular product price

                    }

                    cpclConfigLabel += text(486, y, invoice.getTotal()); // Display the Sub total of Particular product


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


                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            "Item Discount")
                            + text(140, y, " : ")
                            + text(180, y, invoiceHeaderDetails.get(0).getItemDiscount());


                    cpclConfigLabel += text(LEFT_MARGIN,
                            y += LINE_SPACING, "Bill Discount")
                            + text(140, y, " : ")
                            + text(180, y, invoiceHeaderDetails.get(0).getBillDiscount());


                    cpclConfigLabel += text(310, y += LINE_SPACING,
                            "Sub Total")
                            + text(450, y, " : ")
                            + text(486, y, invoiceHeaderDetails.get(0).getSubTotal());


                    cpclConfigLabel += text(310, y, "Tax")
                            + text(450, y, " : ")
                            + text(486, y, invoiceHeaderDetails.get(0).getNetTax());


                    cpclConfigLabel += text(310, y += LINE_SPACING, "Net Total")
                            + text(450, y, " : ")
                            + text(486, y, invoiceHeaderDetails.get(0).getNetTotal());

                    index++;
                }
            }

            TscDll.sendcommand(cpclConfigLabel);

          //  TscDll.printerfont(100, 250, "3", 0, 1, 1, "987654321");
            String status = TscDll.status();
            Log.w("Status_Print:",status);
            TscDll.printlabel(1, 1);
            TscDll.sendfile("zpl.txt");
            TscDll.closeport();
            Log.w("Invoice_details:",cpclConfigLabel);
            Toast.makeText(context,"Printed Successfully",Toast.LENGTH_SHORT).show();
        }catch (Exception ex){
            Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

      /* mydll = cdll.LoadLibrary('k:\Work\SCANNER\Printer\TSCLIB_V0201_x64\TSCLIB.dll')
       print 'Start Printing.'
            mydll.openport("TSC TA300")
            mydll.setup("32","25","2","10","0","0","0")
            mydll.clearbuffer()


                 TscDll.sendcommand("TEXT 100,760,\"ARIAL.TTF\",0,15,15,\""+text+"\"\n");
            TscDll.sendcommand("TEXT 48,56,\"2\",0,1,1,\"I'm Testing\"");
            TscDll.sendcommand("CODEPAGE UTF-8\n");
            TscDll.sendcommand("TEXT 25,190,”TST24.BF2\",0,1,1,”日傑茶坊 TEL:0000–0000\"");
            TscDll.sendcommand("TEXT 0,0,\"FONT001\",0,1,1,\"THIS IS 桂花烏龍奶茶\"\n");



            # LABEL TEMPLATE
         mydll.sendcommand("SIZE 50.8 mm,25.4 mm")
        mydll.sendcommand('GAP 3 mm,0 mm')
        mydll.sendcommand('DIRECTION 0')
        mydll.sendcommand('CLS')
        # Draw Label Image
        mydll.sendcommand('BOX 12,12,584.4,282,4,19.2')
        mydll.sendcommand("QRCODE 417.6,160,H,4,A,0,\"ABCabc123\"")
        mydll.sendcommand("TEXT 48,56,\"2\",0,1,1,\"I'm Testing\"")

        # Print
        mydll.sendcommand('PRINT 1,1')
        mydll.closeport()
        print 'Finished Printing.'*/


 /*   private void createCurrentReceiptSummary(String receiptdate, String username,
                                             ArrayList<Receipt> receiptlist, List<ProductDetails> footerValue, ArrayList<Receipt> receiptinvoicelist, String custCode, String custName, String credit)
            throws IOException {
        // TODO Auto-generated method stub
        String printinvoicedetail =  MobileSettingsSetterGetter.getPrintReceiptSummary_PrintInvoiceDetail();
        logoStr = "logoprint";
        footerArr.clear();
        footerArr = footerValue;

        final String locationName = SalesOrderSetGet.getLocationname();
        final String locationCode = SalesOrderSetGet.getLocationcode();
        final String userName=username;

        Log.d("receipt logoStr", "rece" + logoStr);

        FileOutputStream os = context.openFileOutput(FILE_NAME,
                Context.MODE_PRIVATE);

        int y = 0;
        String paymode_cash = "", paymode_cheque = "", paymode_other = "";
        double cashamount = 0.00, chequeamount = 0.00, total = 0.00, otheramount = 0.00;
        StringBuilder temp = new StringBuilder();
        y = printTitle(170, y, "RECEIPT SUMMARY", temp);
        y = printCompanyDetails(y, temp);

        String cpclConfigLabel = temp.toString();

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Receipt Date")
                + text(170, y, " : ") + text(190, y, receiptdate);
        if(!custCode.matches("")){
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Customer Code")
                    + text(170, y, " : ") + text(190, y, custCode);

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "CustomerName")
                    + text(170, y, " : ") + text(190, y, custName);
        }

        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Location Code")
                + text(170, y, " : ") + text(190, y, locationCode+"("+locationName+")");
        if (!userName.isEmpty()){
            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "User")
                    + text(170, y, " : ") + text(190, y, userName);
        }

        cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
        cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, "Receipt No");
        cpclConfigLabel += text(210, y, "Customer Name");
        cpclConfigLabel += text(430, y, "Paid Amount");


        cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
        for (Receipt receipt : receiptlist) {

            cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING, receipt
                    .getReceiptno().toString());
            // cpclConfigLabel += text(140, y,
            // prods.getDescription().toString());
		*//*	cpclConfigLabel += text(
					160,
					y,
					(receipt.getCustomername().length() > 11) ? receipt
							.getCustomername().substring(0, 10) : receipt
							.getCustomername());*//*

            cpclConfigLabel += text(
                    250,
                    y,
                    (receipt.getCustomername().length() > 11) ? receipt.getCustomername().substring(0, 10) :
                            receipt.getCustomername());

            int count=0;
            String name = receipt.getCustomername();
            int len =name.length();
            if(len>11) {
                int get_len = name.substring(10, len).length();
                String remark = name.substring(10, len);
                Log.d("BalanceString", "-->" + len + " " + get_len + " " + remark);
                String names;

                for (int i = 0; i < get_len; i = i + 11) {
                    count = count + 11;
                    if (count > get_len) {
                        names = remark.substring(i, get_len);
                        cpclConfigLabel += text(
                                250,
                                y += LINE_SPACING, names);
                        Log.d("Balances", "-->" + names);

                    } else {
                        names = remark.substring(i, i + 11);
                        cpclConfigLabel += text(
                                250,
                                y += LINE_SPACING, names);
                        Log.d("BalancesValues", "-->" + names);

                    }
                }
            }
            cpclConfigLabel += text(480, y, receipt.getPaidamount());
            total += receipt.getPaidamount();
        }
        cpclConfigLabel += horizontalLine(y += LINE_SPACING, LINE_THICKNESS);
        cpclConfigLabel += text(220, y += LINE_SPACING, "Total")
                + text(240, y, " : ")
                + text(480, y, twoDecimalPoint(total));

        Log.d("footerArr", "" + footerArr.size());

        if (footerArr.size() > 0) {
            // cpclConfigLabel += text(230, y += LINE_SPACING, "*********");
            for (ProductDetails footer : footerArr) {
                Log.d("footer value", "val " + footer.getReceiptMessage());
                if (footer.getReceiptMessage() != null
                        && !footer.getReceiptMessage().isEmpty()) {
                    cpclConfigLabel += text(LEFT_MARGIN, y += LINE_SPACING,
                            footer.getReceiptMessage());
                }
            }
        }

        cpclConfigLabel = "! 0 200 200 " + (y += (LINE_SPACING * 3)) + " 1"
                + LINE_SEPARATOR + HEADER + cpclConfigLabel + CMD_PRINT;

        os.write(cpclConfigLabel.getBytes());
        os.flush();
        os.close();

    }
*/


    private int printCompanyDetails(int y, StringBuilder cpclConfigLabel) {
        cpclConfigLabel.append(textCenter(LEFT_MARGIN, y += LINE_SPACING, "WINAPP SOLUTION PTE LTD"));
        return y;
    }

    private String textCenter(int x, int y, String text) {

        int length = (PAPER_WIDTH-text.length())/2;
        //	System.out.println(length);
        // Create a new StringBuilder.
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < length; i++)
        {
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
}
