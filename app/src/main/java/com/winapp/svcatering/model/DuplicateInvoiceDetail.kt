package com.winapp.svcatering.model

data class DuplicateInvoiceDetail(
    var accountCode: String,
    var barCodes: String,
    var cartonPrice: String,
    var cartonQty: String,
    var companyCode: String,
    var createDate: String,
    var createdUser: String,
    var currency: String,
    var customerCategoryNo: String,
    var discountPercentage: String,
    var dueDate: String,
    var fRowTotal: String,
    var fTaxAmount: String,
    var foc_Qty: String,
    var gTotal: String,
    var gTotalFC: String,
    var invoiceDate: String,
    var invoiceNo: String,
    var itemDiscount: String,
    var lPrice: String,
    var lineTotal: String,
    var minimumSellingPrice: String,
    var netQuantity: String,
    var netTotal: String,
    var pcsPerCarton: String,
    var piecePrice: String,
    var price: String,
    var priceWithGST: String,
    var productCode: String,
    var productName: String,
    var purchaseTaxCode: String,
    var purchaseTaxPerc: String,
    var purchaseTaxRate: String,
    var quantity: String,
    var retailPrice: String,
    var returnQty: String,
    var salesEmployeeCode: String,
    var slNo: String,
    var subTotal: String,
    var taxAmount: String,
    var taxCode: String,
    var taxPerc: String,
    var taxRate: String,
    var taxStatus: String,
    var taxType: String,
    var total: String,
    var itemtotalTax: String,
    var unitPrice: String,
    var lQty: String,
    var uoMCode: Any,
    var uoMName: String,
    var uomCode: String,
    var updateDate: String,
    var vatPrcnt: String,
    var warehouseCode: String
)

{
    constructor() :this("","","","","","","",""
        ,"","","","","","","","","",""
        ,"","","","","","","","","","",""
        ,"","","","","","","",""
        ,"","","","","","","","","","",""
        ,"","","","","","",""
    )
}