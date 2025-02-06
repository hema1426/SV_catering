package com.winapp.svcatering.model;

public class DeliveryAddressModel {

    private String deliveryAddressCode;
    private String deliveryAddress;

    public String getDeliveryAddressCode() {
        return deliveryAddressCode;
    }

    public void setDeliveryAddressCode(String deliveryAddressCode) {
        this.deliveryAddressCode = deliveryAddressCode;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    @Override
    public String toString() {
        return deliveryAddress;
    }
}
