package com.winapp.svcatering.model;

import java.util.ArrayList;

public class ProductAnalyzeModel {

    // Define the Variables to use Product analysis
    private String productName;
    private String uom;
    private ArrayList<ProductDetails> productDetailList;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public ArrayList<ProductDetails> getProductDetailList() {
        return productDetailList;
    }

    public void setProductDetailList(ArrayList<ProductDetails> productDetailList) {
        this.productDetailList = productDetailList;
    }

    public static class ProductDetails {
        private String invoiceNo;
        private String qty;
        private String cost;
        private String netPrice;
        private String profit;
        private String invoiceDate;


        public String getInvoiceDate() {
            return invoiceDate;
        }

        public void setInvoiceDate(String invoiceDate) {
            this.invoiceDate = invoiceDate;
        }

        public String getInvoiceNo() {
            return invoiceNo;
        }

        public void setInvoiceNo(String invoiceNo) {
            this.invoiceNo = invoiceNo;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }

        public String getNetPrice() {
            return netPrice;
        }

        public void setNetPrice(String netPrice) {
            this.netPrice = netPrice;
        }

        public String getProfit() {
            return profit;
        }

        public void setProfit(String profit) {
            this.profit = profit;
        }
    }
}
