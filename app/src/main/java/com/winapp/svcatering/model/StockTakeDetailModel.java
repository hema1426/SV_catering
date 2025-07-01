package com.winapp.svcatering.model;

import java.util.ArrayList;

public class StockTakeDetailModel {


    private String Number;
    private String Date;
    private String Status;

    public ArrayList<StockTakeDetail> stockTakeDetailsList;


    public ArrayList<StockTakeDetail> getStockTakeDetailsList() {
        return stockTakeDetailsList;
    }

    public void setStockTakeDetailsList(ArrayList<StockTakeDetail> stockTakeDetailsList) {
        this.stockTakeDetailsList = stockTakeDetailsList;
    }

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

    public static class StockTakeDetail{
        private String description;
        private String itemCode;
        private String qty;
        private String uomCode;
        private String location;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

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
