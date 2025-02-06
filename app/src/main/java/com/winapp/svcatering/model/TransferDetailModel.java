package com.winapp.svcatering.model;

import java.util.ArrayList;

public class TransferDetailModel {


    private String Number;
    private String Date;
    private String Status;
    private String fromLocation;
    private String toLocation;
    private String toLocationName;
    private String fromLocationName;

    public String getToLocationName() {
        return toLocationName;
    }

    public void setToLocationName(String toLocationName) {
        this.toLocationName = toLocationName;
    }

    public String getFromLocationName() {
        return fromLocationName;
    }

    public void setFromLocationName(String fromLocationName) {
        this.fromLocationName = fromLocationName;
    }

    public ArrayList<TransferDetails> transferDetailsList;

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

    public ArrayList<TransferDetails> getTransferDetailsList() {
        return transferDetailsList;
    }

    public void setTransferDetailsList(ArrayList<TransferDetails> transferDetailsList) {
        this.transferDetailsList = transferDetailsList;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public static class TransferDetails{
        private String description;
        private String itemCode;
        private String qty;
        private String uomCode;

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
