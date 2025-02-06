package com.winapp.svcatering.model;

import java.util.ArrayList;

public class ReportSalesSummaryModel {

    private String fromDate;
    private String toDate;
    private String companyId;
    private String salesPerson;
    private String totalSales;
    private String cashReceived;
    private String refunded;
    private String cashInHand;

    public ArrayList<ReportSalesSummaryDetails> reportSalesSummaryDetailsList;
    public ArrayList<ReportSalesSummaryInvDetails> reportSalesSummaryInvDetailsList;

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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getSalesPerson() {
        return salesPerson;
    }

    public void setSalesPerson(String salesPerson) {
        this.salesPerson = salesPerson;
    }
    public ArrayList<ReportSalesSummaryDetails> getReportSalesSummaryDetailsList() {
        return reportSalesSummaryDetailsList;
    }

    public void setReportSalesSummaryDetailsList(ArrayList<ReportSalesSummaryDetails> reportSalesSummaryDetailsList) {
        this.reportSalesSummaryDetailsList = reportSalesSummaryDetailsList;
    }

    public String getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(String totalSales) {
        this.totalSales = totalSales;
    }

    public String getCashReceived() {
        return cashReceived;
    }

    public void setCashReceived(String cashReceived) {
        this.cashReceived = cashReceived;
    }

    public String getRefunded() {
        return refunded;
    }

    public void setRefunded(String refunded) {
        this.refunded = refunded;
    }

    public String getCashInHand() {
        return cashInHand;
    }

    public void setCashInHand(String cashInHand) {
        this.cashInHand = cashInHand;
    }

    public ArrayList<ReportSalesSummaryInvDetails> getReportSalesSummaryInvDetailsList() {
        return reportSalesSummaryInvDetailsList;
    }

    public void setReportSalesSummaryInvDetailsList(ArrayList<ReportSalesSummaryInvDetails> reportSalesSummaryInvDetailsList) {
        this.reportSalesSummaryInvDetailsList = reportSalesSummaryInvDetailsList;
    }

    public static class ReportSalesSummaryDetails{
       private String transNo;
        private String Customer;
        private String Amount;
        private String Type;
        private String PaymentDate;
        private String customerCode;

        public String getTransNo() {
            return transNo;
        }

        public void setTransNo(String transNo) {
            this.transNo = transNo;
        }

        public String getCustomer() {
            return Customer;
        }

        public void setCustomer(String customer) {
            Customer = customer;
        }

        public String getCustomerCode() {
            return customerCode;
        }

        public void setCustomerCode(String customerCode) {
            this.customerCode = customerCode;
        }

        public String getAmount() {
            return Amount;
        }

        public void setAmount(String amount) {
            Amount = amount;
        }

        public String getType() {
            return Type;
        }

        public void setType(String type) {
            Type = type;
        }

        public String getPaymentDate() {
            return PaymentDate;
        }

        public void setPaymentDate(String paymentDate) {
            PaymentDate = paymentDate;
        }
    }

    public static class ReportSalesSummaryInvDetails{
        private String transNo;
        private String Customer;
        private String Amount;
        private String Type;
        private String PaymentDate;
        private String customerCode;
        private String balanceAmount;

        public String getTransNo() {
            return transNo;
        }

        public void setTransNo(String transNo) {
            this.transNo = transNo;
        }

        public String getCustomer() {
            return Customer;
        }

        public void setCustomer(String customer) {
            Customer = customer;
        }

        public String getCustomerCode() {
            return customerCode;
        }

        public void setCustomerCode(String customerCode) {
            this.customerCode = customerCode;
        }

        public String getAmount() {
            return Amount;
        }

        public void setAmount(String amount) {
            Amount = amount;
        }

        public String getType() {
            return Type;
        }

        public void setType(String type) {
            Type = type;
        }

        public String getPaymentDate() {
            return PaymentDate;
        }

        public void setPaymentDate(String paymentDate) {
            PaymentDate = paymentDate;
        }

        public String getBalanceAmount() {
            return balanceAmount;
        }

        public void setBalanceAmount(String balanceAmount) {
            this.balanceAmount = balanceAmount;
        }
    }

}
