package com.winapp.svcatering.model;

public class CustomerTaxModel {

    public String taxCode;
    public String taxPerc;
    public String taxType;
    public String customerId;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getTaxPerc() {
        return taxPerc;
    }

    public void setTaxPerc(String taxPerc) {
        this.taxPerc = taxPerc;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }
}
