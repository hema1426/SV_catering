package com.winapp.svcatering.model;

public class StockTakeModel {

    public String stockTakeNo;
    public String date;
    public String Location;
    public String status;
    public String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStockTakeNo() {
        return stockTakeNo;
    }

    public void setStockTakeNo(String stockTakeNo) {
        this.stockTakeNo = stockTakeNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
