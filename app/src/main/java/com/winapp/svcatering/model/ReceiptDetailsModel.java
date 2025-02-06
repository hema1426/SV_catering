package com.winapp.svcatering.model;

import java.util.ArrayList;

public class ReceiptDetailsModel {

    private String customerCode;
    private String customerName;
    private String fromDate;
    private String toDate;
    private ArrayList<ReceiptDetails> receiptDetailsList;

    public ArrayList<ReceiptDetails> getReceiptDetailsList() {
        return receiptDetailsList;
    }

    public void setReceiptDetailsList(ArrayList<ReceiptDetails> receiptDetailsList) {
        this.receiptDetailsList = receiptDetailsList;
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

    public static class ReceiptDetails{

        private String receiptNumber;
        private String receiptDate;
        private String paymode;
        private String receiptTotal;
        private String paymodeTotal;
        private String customerCode;
        private String customerName;
        private String chequeDate;
        private String chequeNo;
        private String bankCode;
        private ArrayList<InvoiceDetails> invoiceDetailsList;

        public ArrayList<InvoiceDetails> getInvoiceDetailsList() {
            return invoiceDetailsList;
        }

        public void setInvoiceDetailsList(ArrayList<InvoiceDetails> invoiceDetailsList) {
            this.invoiceDetailsList = invoiceDetailsList;
        }

        public String getChequeDate() {
            return chequeDate;
        }

        public void setChequeDate(String chequeDate) {
            this.chequeDate = chequeDate;
        }

        public String getChequeNo() {
            return chequeNo;
        }

        public void setChequeNo(String chequeNo) {
            this.chequeNo = chequeNo;
        }

        public String getBankCode() {
            return bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getReceiptNumber() {
            return receiptNumber;
        }

        public void setReceiptNumber(String receiptNumber) {
            this.receiptNumber = receiptNumber;
        }

        public String getReceiptDate() {
            return receiptDate;
        }

        public void setReceiptDate(String receiptDate) {
            this.receiptDate = receiptDate;
        }

        public String getPaymode() {
            return paymode;
        }

        public void setPaymode(String paymode) {
            this.paymode = paymode;
        }

        public String getReceiptTotal() {
            return receiptTotal;
        }

        public void setReceiptTotal(String receiptTotal) {
            this.receiptTotal = receiptTotal;
        }

        public String getPaymodeTotal() {
            return paymodeTotal;
        }

        public void setPaymodeTotal(String paymodeTotal) {
            this.paymodeTotal = paymodeTotal;
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


        public static class InvoiceDetails{

            private String invoiceNumber;
            private String invoiceDate;
            private String netTotal;


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
        }
    }
}
