package com.winapp.svcatering.model;

public class SalesReturnModel {

    private String salesReturnNumber;
    private String salesReturnDate;
    private String salesReturnCode;
    private String paidAmount;
    private String netAmount;
    private String balanceAmount;
    private boolean isReturnChecked;
    private String customerName;
    private String user;
    private String locationCode;
    private String isCheked;
    private String customerCode;

    public String getSalesReturnCode() {
        return salesReturnCode;
    }

    public void setSalesReturnCode(String salesReturnCode) {
        this.salesReturnCode = salesReturnCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getIsCheked() {
        return isCheked;
    }

    public void setIsCheked(String isCheked) {
        this.isCheked = isCheked;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public boolean isReturnChecked() {
        return isReturnChecked;
    }

    public void setReturnChecked(boolean returnChecked) {
        isReturnChecked = returnChecked;
    }

    public String getSalesReturnNumber() {
        return salesReturnNumber;
    }

    public void setSalesReturnNumber(String salesReturnNumber) {
        this.salesReturnNumber = salesReturnNumber;
    }

    public String getSalesReturnDate() {
        return salesReturnDate;
    }

    public void setSalesReturnDate(String salesReturnDate) {
        this.salesReturnDate = salesReturnDate;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(String balanceAmount) {
        this.balanceAmount = balanceAmount;
    }
}
