package com.winapp.svcatering.model


import com.google.gson.annotations.SerializedName

data class AccountModel(
    @SerializedName("accountCode")
    var accountCode: String,
    @SerializedName("accountName")
    var accountName: String
)
