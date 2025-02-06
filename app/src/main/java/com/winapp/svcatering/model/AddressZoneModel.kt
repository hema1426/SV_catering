package com.winapp.svcatering.model


import com.google.gson.annotations.SerializedName

data class AddressZoneModel(
    @SerializedName("Code")
    var code: String,
    @SerializedName("Name")
    var name: String
)