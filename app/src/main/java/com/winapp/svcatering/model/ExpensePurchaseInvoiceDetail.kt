package com.winapp.svcatering.model

data class ExpensePurchaseInvoiceDetail(
    var SODate: String,
    var WarehouseCode: String,
    var cartonPrice: Double,
    var cartonQty: Any,
    var companyCode: String,
    var createUser: String,
    var exchangeQty: Any,
    var focQty: String,
    var itemDiscount: String,
    var itemRemarks: String,
    var modifyUser: String,
    var netTotal: Double,
    var pcsPerCarton: Any,
    var price: String,
    var productCode: String,
    var productName: String,
    var qty: String,
    var retailPrice: Any,
    var returnLQty: String,
    var returnNetTotal: String,
    var returnQty: String,
    var returnSubTotal: String,
    var slNo: Int,
    var subTotal: Double,
    var taxCode: String,
    var taxPerc: String,
    var taxType: String,
    var total: Double,
    var totalTax: String,
    var unitQty: Any,
    var uomCode: String,
    var AccountCode: String
)
{
    constructor() :this("","",0.0,"","","","",
        "","","","",0.0,"","","","",
        "","","","","","",0,0.0,"",
        "","",0.00,"",0.0,"","")
}