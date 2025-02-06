package com.winapp.svcatering.model;

import androidx.annotation.NonNull;

public class UomModel {

    private String uomCode;
    private String uomName;
    private String uomEntry;
    private String altQty;
    private String baseQty;
    private String price;
    private Boolean isChecked;

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUomCode() {
        return uomCode;
    }

    public void setUomCode(String uomCode) {
        this.uomCode = uomCode;
    }

    public String getUomName() {
        return uomName;
    }

    public void setUomName(String uomName) {
        this.uomName = uomName;
    }

    public String getUomEntry() {
        return uomEntry;
    }

    public void setUomEntry(String uomEntry) {
        this.uomEntry = uomEntry;
    }

    public String getAltQty() {
        return altQty;
    }

    public void setAltQty(String altQty) {
        this.altQty = altQty;
    }

    public String getBaseQty() {
        return baseQty;
    }

    public void setBaseQty(String baseQty) {
        this.baseQty = baseQty;
    }

    @NonNull
    @Override
    public String toString() {
        return uomName;
    }
}
