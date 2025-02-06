package com.winapp.svcatering.newtransfer;

import java.util.ArrayList;

public class TransferInModel {
    private ArrayList<TransferInDetails> transferInDetails;

    public ArrayList<TransferInDetails> getTransferInDetails() {
        return transferInDetails;
    }

    public void setTransferInDetails(ArrayList<TransferInDetails> transferInDetails) {
        this.transferInDetails = transferInDetails;
    }

    public static class TransferInDetails {

        private String productCode;
        private String productName;
        private int stockInHand;
        private String qty;
        private String inventoryUOM;

        public String getInventoryUOM() {
            return inventoryUOM;
        }

        public void setInventoryUOM(String inventoryUOM) {
            this.inventoryUOM = inventoryUOM;
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

        public int getStockInHand() {
            return stockInHand;
        }

        public void setStockInHand(int stockInHand) {
            this.stockInHand = stockInHand;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }
    }
}
