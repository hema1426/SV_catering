package com.winapp.svcatering.model;

public class TaxModel {

    private String taxCode;
    private String taxName;
    private String taxPerc;
    private String taxShortCode;
    private String taxType;

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public String getTaxPerc() {
        return taxPerc;
    }

    public void setTaxPerc(String taxPerc) {
        this.taxPerc = taxPerc;
    }

    public String getTaxShortCode() {
        return taxShortCode;
    }

    public void setTaxShortCode(String taxShortCode) {
        this.taxShortCode = taxShortCode;
    }
}
