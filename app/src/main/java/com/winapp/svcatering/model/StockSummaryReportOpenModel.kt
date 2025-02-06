package com.winapp.svcatering.model


import com.google.gson.annotations.SerializedName

data class StockSummaryReportOpenModel(
    @SerializedName("itemCode")
    var itemCode: String,
    @SerializedName("itemName")
    var itemName: String,
    @SerializedName("warehouse")
    var warehouse: String,
    @SerializedName("openingQty")
    var openingQty: String,
    @SerializedName("inwardQty")
    var inwardQty: String,
    @SerializedName("outwardQty")
    var outwardQty: String,
    @SerializedName("closingQty")
    var closingQty: String
)