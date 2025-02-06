package com.winapp.svcatering.model;

public class CashCollectionInvoiceModel {

    private String invoiceNumber;
    private String invoiceCode;
    private String netTotal;
    private String payable;
    private String discount;
    private String balance;
    private String checked;
    private String invoiceDate;
    private String customerCode;
    private String paidAmount;
    private String discountAmount;
    private boolean isDisabledDiscount;
    private String netBalance;
    private String tranType;

    private Boolean isselect;
    private Boolean isbackground;
    private Boolean iseditable;
    private boolean isPreSelect;

    public boolean isPreSelect() {
        return isPreSelect;
    }

    public void setPreSelect(boolean preSelect) {
        isPreSelect = preSelect;
    }


    public Boolean getIsselect() {
        return isselect;
    }

    public void setIsselect(Boolean isselect) {
        this.isselect = isselect;
    }

    public Boolean getIsbackground() {
        return isbackground;
    }

    public void setIsbackground(Boolean isbackground) {
        this.isbackground = isbackground;
    }

    public Boolean getIseditable() {
        return iseditable;
    }

    public void setIseditable(Boolean iseditable) {
        this.iseditable = iseditable;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    public String getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(String netBalance) {
        this.netBalance = netBalance;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public boolean isDisabledDiscount() {
        return isDisabledDiscount;
    }

    public void setDisabledDiscount(boolean disabledDiscount) {
        isDisabledDiscount = disabledDiscount;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }


    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(String netTotal) {
        this.netTotal = netTotal;
    }

    public String getPayable() {
        return payable;
    }

    public void setPayable(String payable) {
        this.payable = payable;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
