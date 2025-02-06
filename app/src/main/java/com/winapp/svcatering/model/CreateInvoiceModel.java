package com.winapp.svcatering.model;

public class CreateInvoiceModel {
    private String productCode;
    private String productName;
    private String actualQty;
    private String focQty;
    private String returnQty;
    private String netQty;
    private String price;
    private String total;
    private String subTotal;
    private String gstAmount;
    private String netTotal;
    private String stockQty;
    private String stockProductQty;
    private String uomCode;
    private String uomText;

    private String saleableQty;
    private String damagedQty;
    private String exchangeQty;
    private String productBarCode;
    private String minimumSellingPrice;
    private String updateTime;
    private String isItemFOC;

    public String getIsItemFOC() {
        return isItemFOC;
    }

    public void setIsItemFOC(String isItemFOC) {
        this.isItemFOC = isItemFOC;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getStockProductQty() {
        return stockProductQty;
    }

    public void setStockProductQty(String stockProductQty) {
        this.stockProductQty = stockProductQty;
    }

    public String getMinimumSellingPrice() {
        return minimumSellingPrice;
    }

    public void setMinimumSellingPrice(String minimumSellingPrice) {
        this.minimumSellingPrice = minimumSellingPrice;
    }

    public String getProductBarCode() {
        return productBarCode;
    }

    public void setProductBarCode(String productBarCode) {
        this.productBarCode = productBarCode;
    }

    public String getExchangeQty() {
        return exchangeQty;
    }

    public void setExchangeQty(String exchangeQty) {
        this.exchangeQty = exchangeQty;
    }

    public String getSaleableQty() {
        return saleableQty;
    }

    public void setSaleableQty(String saleableQty) {
        this.saleableQty = saleableQty;
    }

    public String getDamagedQty() {
        return damagedQty;
    }

    public void setDamagedQty(String damagedQty) {
        this.damagedQty = damagedQty;
    }

    public String getItemDisc() {
        return itemDisc;
    }

    public void setItemDisc(String itemDisc) {
        this.itemDisc = itemDisc;
    }

    private String itemDisc;
    private String billDisc;

    public String getUomText() {
        return uomText;
    }

    public String getBillDisc() {
        return billDisc;
    }

    public void setBillDisc(String billDisc) {
        this.billDisc = billDisc;
    }

    public void setUomText(String uomText) {
        this.uomText = uomText;
    }

    public String getUomCode() {
        return uomCode;
    }

    public void setUomCode(String uomCode) {
        this.uomCode = uomCode;
    }

    public String getStockQty() {
        return stockQty;
    }

    public void setStockQty(String stockQty) {
        this.stockQty = stockQty;
    }

    public String getFocQty() {
        return focQty;
    }

    public void setFocQty(String focQty) {
        this.focQty = focQty;
    }

    public String getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(String gstAmount) {
        this.gstAmount = gstAmount;
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

    public String getActualQty() {
        return actualQty;
    }

    public void setActualQty(String actualQty) {
        this.actualQty = actualQty;
    }

    public String getReturnQty() {
        return returnQty;
    }

    public void setReturnQty(String returnQty) {
        this.returnQty = returnQty;
    }

    public String getNetQty() {
        return netQty;
    }

    public void setNetQty(String netQty) {
        this.netQty = netQty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }


    public String getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(String netTotal) {
        this.netTotal = netTotal;
    }
}
