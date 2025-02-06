package com.winapp.svcatering.model;

import java.util.ArrayList;

public class SalesOrderPrintPreviewModel {

    private String soNumber;
    private String soDate;
    private String customerCode;
    private String customerName;
    private String address;
    private String deliveryAddress;
    private String billDiscount;
    private String itemDiscount;
    private String subTotal;
    private String netTax;
    private String netTotal;
    private String taxType;
    private String taxValue;
    private String outStandingAmount;

    private String address1;
    private String address2;
    private String address3;

    private String addressstate;
    private String addresssZipcode;
    private ArrayList<SalesList> salesList;

    private String allowDeliveryAddress;

    public String getAllowDeliveryAddress() {
        return allowDeliveryAddress;
    }

    public void setAllowDeliveryAddress(String allowDeliveryAddress) {
        this.allowDeliveryAddress = allowDeliveryAddress;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getSoNumber() {
        return soNumber;
    }

    public void setSoNumber(String soNumber) {
        this.soNumber = soNumber;
    }

    public String getSoDate() {
        return soDate;
    }

    public void setSoDate(String soDate) {
        this.soDate = soDate;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getBillDiscount() {
        return billDiscount;
    }

    public void setBillDiscount(String billDiscount) {
        this.billDiscount = billDiscount;
    }

    public String getItemDiscount() {
        return itemDiscount;
    }

    public void setItemDiscount(String itemDiscount) {
        this.itemDiscount = itemDiscount;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getNetTax() {
        return netTax;
    }

    public void setNetTax(String netTax) {
        this.netTax = netTax;
    }

    public String getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(String netTotal) {
        this.netTotal = netTotal;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(String taxValue) {
        this.taxValue = taxValue;
    }

    public String getOutStandingAmount() {
        return outStandingAmount;
    }

    public void setOutStandingAmount(String outStandingAmount) {
        this.outStandingAmount = outStandingAmount;
    }

    public String getAddressstate() {
        return addressstate;
    }

    public void setAddressstate(String addressstate) {
        this.addressstate = addressstate;
    }

    public String getAddresssZipcode() {
        return addresssZipcode;
    }

    public void setAddresssZipcode(String addresssZipcode) {
        this.addresssZipcode = addresssZipcode;
    }

    public ArrayList<SalesList> getSalesList() {
        return salesList;
    }

    public void setSalesList(ArrayList<SalesList> salesList) {
        this.salesList = salesList;
    }

    public static class SalesList {
        private String productCode;
        private String sno;
        private String description;
        private String lqty;
        private String cqty;
        private String netQty;
        private String price;
        private String total;
        private String cartonPrice;
        private String unitPrice;
        private String pcsperCarton;
        private String itemtax;
        private String subTotal;
        private String pricevalue;
        private String uomCode;


        public String getUomCode() {
            return uomCode;
        }

        public void setUomCode(String uomCode) {
            this.uomCode = uomCode;
        }

        public String getPricevalue() {
            return pricevalue;
        }

        public void setPricevalue(String pricevalue) {
            this.pricevalue = pricevalue;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getSno() {
            return sno;
        }

        public void setSno(String sno) {
            this.sno = sno;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLqty() {
            return lqty;
        }

        public void setLqty(String lqty) {
            this.lqty = lqty;
        }

        public String getCqty() {
            return cqty;
        }

        public void setCqty(String cqty) {
            this.cqty = cqty;
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

        public String getCartonPrice() {
            return cartonPrice;
        }

        public void setCartonPrice(String cartonPrice) {
            this.cartonPrice = cartonPrice;
        }

        public String getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(String unitPrice) {
            this.unitPrice = unitPrice;
        }

        public String getPcsperCarton() {
            return pcsperCarton;
        }

        public void setPcsperCarton(String pcsperCarton) {
            this.pcsperCarton = pcsperCarton;
        }

        public String getItemtax() {
            return itemtax;
        }

        public void setItemtax(String itemtax) {
            this.itemtax = itemtax;
        }

        public String getSubTotal() {
            return subTotal;
        }

        public void setSubTotal(String subTotal) {
            this.subTotal = subTotal;
        }
    }
}
