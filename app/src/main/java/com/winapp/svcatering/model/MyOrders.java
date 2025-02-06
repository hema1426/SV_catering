package com.winapp.svcatering.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MyOrders {

    @SerializedName("order")
    @Expose
    private ArrayList<Order> order = null;

    public ArrayList<Order> getOrder() {
        return order;
    }

    public void setOrder(ArrayList<Order> order) {
        this.order = order;
    }

    public static class Order {

        @SerializedName("order_id")
        @Expose
        private String orderId;
        @SerializedName("firstname")
        @Expose
        private String firstname;
        @SerializedName("lastname")
        @Expose
        private String lastname;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("telephone")
        @Expose
        private String telephone;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("payment_address_1")
        @Expose
        private String paymentAddress1;
        @SerializedName("payment_address_2")
        @Expose
        private String paymentAddress2;
        @SerializedName("payment_city")
        @Expose
        private String paymentCity;
        @SerializedName("payment_postcode")
        @Expose
        private String paymentPostcode;
        @SerializedName("payment_country")
        @Expose
        private String paymentCountry;
        @SerializedName("payment_method")
        @Expose
        private String paymentMethod;
        @SerializedName("payment_code")
        @Expose
        private String paymentCode;
        @SerializedName("shipping_method")
        @Expose
        private String shippingMethod;
        @SerializedName("shipping_code")
        @Expose
        private String shippingCode;
        @SerializedName("total")
        @Expose
        private String total;
        @SerializedName("date_added")
        @Expose
        private String dateAdded;
        @SerializedName("date_modified")
        @Expose
        private String dateModified;

        @SerializedName("order_status_id")
        @Expose
        private String orderStatusId;

        public String getOrderStatusId() {
            return orderStatusId;
        }

        public void setOrderStatusId(String orderStatusId) {
            this.orderStatusId = orderStatusId;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPaymentAddress1() {
            return paymentAddress1;
        }

        public void setPaymentAddress1(String paymentAddress1) {
            this.paymentAddress1 = paymentAddress1;
        }

        public String getPaymentAddress2() {
            return paymentAddress2;
        }

        public void setPaymentAddress2(String paymentAddress2) {
            this.paymentAddress2 = paymentAddress2;
        }

        public String getPaymentCity() {
            return paymentCity;
        }

        public void setPaymentCity(String paymentCity) {
            this.paymentCity = paymentCity;
        }

        public String getPaymentPostcode() {
            return paymentPostcode;
        }

        public void setPaymentPostcode(String paymentPostcode) {
            this.paymentPostcode = paymentPostcode;
        }

        public String getPaymentCountry() {
            return paymentCountry;
        }

        public void setPaymentCountry(String paymentCountry) {
            this.paymentCountry = paymentCountry;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getPaymentCode() {
            return paymentCode;
        }

        public void setPaymentCode(String paymentCode) {
            this.paymentCode = paymentCode;
        }

        public String getShippingMethod() {
            return shippingMethod;
        }

        public void setShippingMethod(String shippingMethod) {
            this.shippingMethod = shippingMethod;
        }

        public String getShippingCode() {
            return shippingCode;
        }

        public void setShippingCode(String shippingCode) {
            this.shippingCode = shippingCode;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getDateAdded() {
            return dateAdded;
        }

        public void setDateAdded(String dateAdded) {
            this.dateAdded = dateAdded;
        }

        public String getDateModified() {
            return dateModified;
        }

        public void setDateModified(String dateModified) {
            this.dateModified = dateModified;
        }

    }
}
