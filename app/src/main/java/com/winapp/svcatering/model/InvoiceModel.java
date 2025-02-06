package com.winapp.svcatering.model;

import java.util.ArrayList;

public class InvoiceModel {
    private String invoiceNumber;
    private String invoiceCode;
    private String name;
    private String date;
    private String balance;
    private String netTotal;
    private String address;
    private String status;
    private String customerCode;

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    private boolean isShow=false;
    private ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList;

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public ArrayList<InvoicePrintPreviewModel.InvoiceList> getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(ArrayList<InvoicePrintPreviewModel.InvoiceList> invoiceList) {
        this.invoiceList = invoiceList;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(String netTotal) {
        this.netTotal = netTotal;
    }
}
