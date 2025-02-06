package com.winapp.svcatering.model;

public class TransferProductModel {
    private String productCode;
    private String productName;
    private String pcsPerCarton;
    private String stockInHand;
    private boolean favourite;

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

    public String getPcsPerCarton() {
        return pcsPerCarton;
    }

    public void setPcsPerCarton(String pcsPerCarton) {
        this.pcsPerCarton = pcsPerCarton;
    }

    public String getStockInHand() {
        return stockInHand;
    }

    public void setStockInHand(String stockInHand) {
        this.stockInHand = stockInHand;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
