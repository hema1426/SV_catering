package com.winapp.svcatering.model;

import java.util.ArrayList;

public class StockBadRequestReturnModel {


    private String warehouseCode;
    private String reason;
    private String Number;
    private String Date;
    private String Status;

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public ArrayList<StockBadRequestReturnDetails> stockBadRequestReturnDetailsList;

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ArrayList<StockBadRequestReturnDetails> getStockBadRequestReturnDetailsList() {
        return stockBadRequestReturnDetailsList;
    }

    public void setStockBadRequestReturnDetailsList(ArrayList<StockBadRequestReturnDetails> stockBadRequestReturnDetailsList) {
        this.stockBadRequestReturnDetailsList = stockBadRequestReturnDetailsList;
    }


    public static class StockBadRequestReturnDetails{
       private String description;
        private String qty;
        private String uomCode;

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }


        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUomCode() {
            return uomCode;
        }

        public void setUomCode(String uomCode) {
            this.uomCode = uomCode;
        }

    }
}
