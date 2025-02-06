package com.winapp.svcatering.model;

import java.util.ArrayList;

public class SettlementReceiptModel {
    // Define the variables for the Settlement
    private String totalInvoiceAmount;
    private String totalPaidAmount;
    private String totalLessAmount;
    private String totalCashAmount;
    private String totalChequeAmount;
    private ArrayList<SettlementReceiptDetailModel> settlementReceiptDetailModels;

    private ArrayList<CurrencyDenomination> currencyDenominations;
    private ArrayList<Expense> expenses;

    public String getTotalInvoiceAmount() {
        return totalInvoiceAmount;
    }

    public void setTotalInvoiceAmount(String totalInvoiceAmount) {
        this.totalInvoiceAmount = totalInvoiceAmount;
    }

    public String getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public void setTotalPaidAmount(String totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    public String getTotalLessAmount() {
        return totalLessAmount;
    }

    public void setTotalLessAmount(String totalLessAmount) {
        this.totalLessAmount = totalLessAmount;
    }

    public String getTotalCashAmount() {
        return totalCashAmount;
    }

    public void setTotalCashAmount(String totalCashAmount) {
        this.totalCashAmount = totalCashAmount;
    }

    public String getTotalChequeAmount() {
        return totalChequeAmount;
    }

    public void setTotalChequeAmount(String totalChequeAmount) {
        this.totalChequeAmount = totalChequeAmount;
    }

    public ArrayList<CurrencyDenomination> getCurrencyDenominations() {
        return currencyDenominations;
    }

    public ArrayList<SettlementReceiptDetailModel> getSettlementReceiptDetailModels() {
        return settlementReceiptDetailModels;
    }

    public void setSettlementReceiptDetailModels(ArrayList<SettlementReceiptDetailModel> settlementReceiptDetailModels) {
        this.settlementReceiptDetailModels = settlementReceiptDetailModels;
    }

    public void setCurrencyDenominations(ArrayList<CurrencyDenomination> currencyDenominations) {
        this.currencyDenominations = currencyDenominations;
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(ArrayList<Expense> expenses) {
        this.expenses = expenses;
    }

    public static class CurrencyDenomination{

        // Define the variables for the Currency
        private String denomination;
        private String count;
        private String total;

        public String getDenomination() {
            return denomination;
        }

        public void setDenomination(String denomination) {
            this.denomination = denomination;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }
    }

    public static class Expense{
        // Define the variables for the Expenses

        private String expeneName;
        private String expenseTotal;

        public String getExpeneName() {
            return expeneName;
        }

        public void setExpeneName(String expeneName) {
            this.expeneName = expeneName;
        }

        public String getExpenseTotal() {
            return expenseTotal;
        }

        public void setExpenseTotal(String expenseTotal) {
            this.expenseTotal = expenseTotal;
        }
    }
}
