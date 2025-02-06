package com.winapp.svcatering.model;

import java.util.ArrayList;

public class CustomerStateModel {
    private String fromDate;
    private String toDate;
    private String customerName;
    private ArrayList<CustInvoiceDetails> custInvoiceDetailList;
    private ArrayList<CustInvoiceDetailsAR> custInvoiceDetailsARList;

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public ArrayList<CustInvoiceDetailsAR> getCustInvoiceDetailsARList() {
        return custInvoiceDetailsARList;
    }

    public void setCustInvoiceDetailsARList(ArrayList<CustInvoiceDetailsAR> custInvoiceDetailsARList) {
        this.custInvoiceDetailsARList = custInvoiceDetailsARList;
    }

    public ArrayList<CustInvoiceDetails> getCustInvoiceDetailList() {
        return custInvoiceDetailList;
    }

    public void setCustInvoiceDetailList(ArrayList<CustInvoiceDetails> custInvoiceDetailList) {
        this.custInvoiceDetailList = custInvoiceDetailList;
    }

    public static class CustInvoiceDetails {

        private String invoiceNumber;
        private String invoiceDate;
        private String netTotal;
        private String balanceAmount;

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

        public String getNetTotal() {
            return netTotal;
        }

        public void setNetTotal(String netTotal) {
            this.netTotal = netTotal;
        }

        public String getBalanceAmount() {
            return balanceAmount;
        }

        public void setBalanceAmount(String balanceAmount) {
            this.balanceAmount = balanceAmount;
        }
    }
    public static class CustInvoiceDetailsAR {

        private String invoiceNumber;
        private String invoiceDate;
        private String netTotal;
        private String balanceAmount;

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

        public String getNetTotal() {
            return netTotal;
        }

        public void setNetTotal(String netTotal) {
            this.netTotal = netTotal;
        }

        public String getBalanceAmount() {
            return balanceAmount;
        }

        public void setBalanceAmount(String balanceAmount) {
            this.balanceAmount = balanceAmount;
        }
    }

}
