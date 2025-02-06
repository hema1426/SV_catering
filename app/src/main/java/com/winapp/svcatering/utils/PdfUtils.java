package com.winapp.svcatering.utils;

import com.winapp.svcatering.model.ExpensePrintPreviewModel;
import com.winapp.svcatering.model.InvoicePrintPreviewModel;

import java.util.ArrayList;

public class PdfUtils {

    public static String descriptionSpace(){
        return "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";
    }

    public static String snoSpace(){
        return "\t\t\t\t\t\t\t\t\t\t\t";
    }

    public static String qtySpace(){
        return "\t\t\t\t\t\t\t\t\t\t\t\t";
    }

    public static String priceSpace(){
        return "\t\t\t\t\t\t\t\t\t\t\t\t";
    }

    public static String totalSpace(){
        return "\t\t\t\t\t\t\t\t\t\t\t\t\t\t";
    }

    public static String drawLine(){
        return "____________________________________________________________________________________";
    }

    public static String getDescriptionSpace(int length, char charToFill) {
        char[] array = new char[length];
        int pos = 0;
        while (pos < length) {
            array[pos] = charToFill;
            pos++;
        }
        return new String(array);
    }

    public static int getLength(ArrayList<InvoicePrintPreviewModel.InvoiceList> Str) {
        int largestString = Str.get(0).getDescription().length();
        int index = 0;
        for(int i = 0; i < Str.size(); i++) {
            if(Str.get(i).getDescription().length() > largestString) {
                largestString = Str.get(i).getDescription().length();
                index = i;
            }
        }
        System.out.println("Index " + index + " "+ Str.get(index) + " " + "is the largest and is size " + largestString);
        return largestString;
    }

    public static int getLength1(ArrayList<ExpensePrintPreviewModel.InvoiceList> Str) {
        int largestString = Str.get(0).getDescription().length();
        int index = 0;
        for(int i = 0; i < Str.size(); i++) {
            if(Str.get(i).getDescription().length() > largestString) {
                largestString = Str.get(i).getDescription().length();
                index = i;
            }
        }
        System.out.println("Index " + index + " "+ Str.get(index) + " " + "is the largest and is size " + largestString);
        return largestString;
    }

}
