package com.winapp.svcatering.model;

import java.util.ArrayList;

public class ReportStockSummaryModel {

    private String deviceId;
    private String compantId;

    public ArrayList<ReportStockSummaryDetails> reportStockSummaryDetailsList;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCompantId() {
        return compantId;
    }

    public void setCompantId(String compantId) {
        this.compantId = compantId;
    }

    public ArrayList<ReportStockSummaryDetails> getReportStockSummaryDetailsList() {
        return reportStockSummaryDetailsList;
    }

    public void setReportStockSummaryDetailsList(ArrayList<ReportStockSummaryDetails> reportStockSummaryDetailsList) {
        this.reportStockSummaryDetailsList = reportStockSummaryDetailsList;
    }

    public static class ReportStockSummaryDetails {

        private String productCode;
        private String productName;
        private String uomCode;
        private String openQty;
        private String in;
        private String ex;
        private String salesQty;
        private String focQty;
        private String rtnQty;
        private String out;
        private String balance;
        private String otherInorOut;

        public String getOtherInorOut() {
            return otherInorOut;
        }

        public void setOtherInorOut(String otherInorOut) {
            this.otherInorOut = otherInorOut;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getUomCode() {
            return uomCode;
        }

        public void setUomCode(String uomCode) {
            this.uomCode = uomCode;
        }

        public String getOpenQty() {
            return openQty;
        }

        public void setOpenQty(String openQty) {
            this.openQty = openQty;
        }

        public String getIn() {
            return in;
        }

        public void setIn(String in) {
            this.in = in;
        }

        public String getSalesQty() {
            return salesQty;
        }

        public void setSalesQty(String salesQty) {
            this.salesQty = salesQty;
        }

        public String getFocQty() {
            return focQty;
        }

        public void setFocQty(String focQty) {
            this.focQty = focQty;
        }

        public String getRtnQty() {
            return rtnQty;
        }

        public void setRtnQty(String rtnQty) {
            this.rtnQty = rtnQty;
        }

        public String getOut() {
            return out;
        }

        public void setOut(String out) {
            this.out = out;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getEx() {
            return ex;
        }

        public void setEx(String ex) {
            this.ex = ex;
        }
    }
}
