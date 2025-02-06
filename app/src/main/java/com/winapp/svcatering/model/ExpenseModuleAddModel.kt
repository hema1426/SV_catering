package com.winapp.svcatering.model


data class ExpenseModuleAddModel(
    var expenseName: String,
    var expenseAmt: Double,
    var supplierCode:String,
    var accountCode:String
    )
