package com.winapp.svcatering.model

data class CreditLimitDialogResponse(
    val balance: String,
    val creditLine: String,
    val customerCode: String,
    val customerName: String
)