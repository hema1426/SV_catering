package com.winapp.svcatering.model;

public class ProductStockAnalysisModel {

    private String productId;
    private String productName;
    private String purchaseQty;
    private String salesQty;
    private String balanceQty;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPurchaseQty() {
        return purchaseQty;
    }

    public void setPurchaseQty(String purchaseQty) {
        this.purchaseQty = purchaseQty;
    }

    public String getSalesQty() {
        return salesQty;
    }

    public void setSalesQty(String salesQty) {
        this.salesQty = salesQty;
    }

    public String getBalanceQty() {
        return balanceQty;
    }

    public void setBalanceQty(String balanceQty) {
        this.balanceQty = balanceQty;
    }
}
