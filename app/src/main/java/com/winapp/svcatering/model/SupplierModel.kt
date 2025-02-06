package com.winapp.svcatering.model


import com.google.gson.annotations.SerializedName

data class SupplierModel(
    @SerializedName("customerCode")
    var customerCode: String,
    @SerializedName("customerName")
    var customerName: String,
    var currencyCode: String,
    var currencyName: String,
    var taxType: String,
    var taxCode: String,
    var taxName: String,
var taxPercentage: String,

    )