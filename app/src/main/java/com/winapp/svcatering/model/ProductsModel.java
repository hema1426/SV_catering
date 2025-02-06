package com.winapp.svcatering.model;

import java.util.ArrayList;

public class ProductsModel {

    private String companyCode;
    private String productCode;
    private String productName;
    private String productImage;
    private String pcsPerCarton;
    private String customerItemCode;
    private String unitCost;
    private double averageCost;
    private String wholeSalePrice;
    private double retailPrice;
    private String minimumSellingPrice;
    private boolean isActive;
    private String stockQty;
    private String qty;
    private String focQty;
    private String imageUrl;
    private String weight;
    private String cartonPrice;
    private String pcsPerCartion;
    private String uomCode;
    private String catagoryCode;
    private String productBarcode;
    private String lastPrice;
    private String uomText;
    private String defaultUom;
    private String barcode;
    private String isItemFOC;

    public String getIsItemFOC() {
        return isItemFOC;
    }

    public void setIsItemFOC(String isItemFOC) {
        this.isItemFOC = isItemFOC;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    private ArrayList<UomModel> productUOMList;

    public String getCustomerItemCode() {
        return customerItemCode;
    }

    public void setCustomerItemCode(String customerItemCode) {
        this.customerItemCode = customerItemCode;
    }

    public String getUomText() {
        return uomText;
    }

    public void setUomText(String uomText) {
        this.uomText = uomText;
    }

    public ArrayList<UomModel> getProductUOMList() {
        return productUOMList;
    }

    public void setProductUOMList(ArrayList<UomModel> productUOMList) {
        this.productUOMList = productUOMList;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    public String getCatagoryCode() {
        return catagoryCode;
    }

    public void setCatagoryCode(String catagoryCode) {
        this.catagoryCode = catagoryCode;
    }

    public String getUomCode() {
        return uomCode;
    }

    public void setUomCode(String uomCode) {
        this.uomCode = uomCode;
    }

    public String getCartonPrice() {
        return cartonPrice;
    }

    public void setCartonPrice(String cartonPrice) {
        this.cartonPrice = cartonPrice;
    }

    public String getPcsPerCartion() {
        return pcsPerCartion;
    }

    public void setPcsPerCartion(String pcsPerCartion) {
        this.pcsPerCartion = pcsPerCartion;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
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

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getPcsPerCarton() {
        return pcsPerCarton;
    }

    public void setPcsPerCarton(String pcsPerCarton) {
        this.pcsPerCarton = pcsPerCarton;
    }

    public String getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(String unitCost) {
        this.unitCost = unitCost;
    }

    public double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(double averageCost) {
        this.averageCost = averageCost;
    }

    public String getWholeSalePrice() {
        return wholeSalePrice;
    }

    public void setWholeSalePrice(String wholeSalePrice) {
        this.wholeSalePrice = wholeSalePrice;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public String getMinimumSellingPrice() {
        return minimumSellingPrice;
    }

    public void setMinimumSellingPrice(String minimumSellingPrice) {
        this.minimumSellingPrice = minimumSellingPrice;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStockQty() {
        return stockQty;
    }

    public void setStockQty(String stockQty) {
        this.stockQty = stockQty;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getFocQty() {
        return focQty;
    }

    public void setFocQty(String focQty) {
        this.focQty = focQty;
    }

    public String getDefaultUom() {
        return defaultUom;
    }

    public void setDefaultUom(String defaultUom) {
        this.defaultUom = defaultUom;
    }
}
