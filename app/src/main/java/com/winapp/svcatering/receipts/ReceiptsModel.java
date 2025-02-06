package com.winapp.svcatering.receipts;

import java.util.ArrayList;

public class ReceiptsModel {
    private String saleOrderNumber;
    private String customerCode;
    private String customerName;
    private String name;
    private String date;
    private String balance;
    private String netTotal;
    private String address;
    private String status;
    private String receiptNumber;
    private String receiptCode;
    private String transactionMode;
    private String dateSortingString;
    private String invoiceNumber;
    private String invoiceDate;
    private String creditLimit;
    private String user;
    private boolean isShow=false;
    private ArrayList<ReceiptsListAdapter.InvoiceModel> invoiceList;

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getReceiptCode() {
        return receiptCode;
    }

    public void setReceiptCode(String receiptCode) {
        this.receiptCode = receiptCode;
    }

    public ArrayList<ReceiptsListAdapter.InvoiceModel> getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(ArrayList<ReceiptsListAdapter.InvoiceModel> invoiceList) {
        this.invoiceList = invoiceList;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDateSortingString() {
        return dateSortingString;
    }

    public void setDateSortingString(String dateSortingString) {
        this.dateSortingString = dateSortingString;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String customerCode) {
        this.receiptNumber = customerCode;
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

    public String getSaleOrderNumber() {
        return saleOrderNumber;
    }

    public void setSaleOrderNumber(String saleOrderNumber) {
        this.saleOrderNumber = saleOrderNumber;
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
