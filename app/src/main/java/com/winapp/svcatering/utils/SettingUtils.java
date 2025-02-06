package com.winapp.svcatering.utils;

public class SettingUtils {
    public static String deliveryAddressCode="0";
    public static String deliveryAddress;
    public static String getDeliveryAddressCode() {
        return deliveryAddressCode;
    }

    public static void setDeliveryAddressCode(String deliveryAddressCode) {
        SettingUtils.deliveryAddressCode = deliveryAddressCode;
    }

    public static String getDeliveryAddress() {
        return deliveryAddress;
    }

    public static void setDeliveryAddress(String deliveryAddress) {
        SettingUtils.deliveryAddress = deliveryAddress;
    }
}
