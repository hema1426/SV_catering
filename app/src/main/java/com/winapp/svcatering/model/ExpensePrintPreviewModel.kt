package com.winapp.svcatering.model

class ExpensePrintPreviewModel {
    var invoiceNumber: String? = null
    var invoiceDate: String? = null
    var customerCode: String? = null
    var customerName: String? = null
    var address: String? = null
    var deliveryAddress: String? = null
    var billDiscount: String? = null
    var itemDiscount: String? = null
    var subTotal: String? = null
    var netTax: String? = null
    var netTotal: String? = null
    var taxType: String? = null
    var taxValue: String? = null
    var outStandingAmount: String? = null
    var balanceAmount: String? = null
    var address1: String? = null
    var address2: String? = null
    var address3: String? = null
    var soNumber: String? = null
    var doNumber: String? = null
    var soDate: String? = null
    var doDate: String? = null
    var overAllTotal: String? = null
    var paymentTerm: String? = null
    var invoiceList: ArrayList<InvoiceList>? = null
    var salesReturnList: ArrayList<SalesReturnList>? = null

    class InvoiceList {
        var productCode: String? = null
        var sno: String? = null
        var description: String? = null
        var lqty: String? = null
        var cqty: String? = null
        var netQty: String? = null
        var netQuantity: String? = null
        var returnQty: String? = null
        var price: String? = null
        var total: String? = null
        var cartonPrice: String? = null
        var focQty: String? = null
        var unitPrice: String? = null
        var pcsperCarton: String? = null
        var itemtax: String? = null
        var subTotal: String? = null
        var uomCode: String? = null
        var pricevalue: String? = null
    }

    class SalesReturnList {
        var salesReturnNumber: String? = null
        private var sRSubTotal: String? = null
        private var sRTaxTotal: String? = null
        private var sRNetTotal: String? = null
        fun getsRSubTotal(): String? {
            return sRSubTotal
        }

        fun setsRSubTotal(sRSubTotal: String?) {
            this.sRSubTotal = sRSubTotal
        }

        fun getsRTaxTotal(): String? {
            return sRTaxTotal
        }

        fun setsRTaxTotal(sRTaxTotal: String?) {
            this.sRTaxTotal = sRTaxTotal
        }

        fun getsRNetTotal(): String? {
            return sRNetTotal
        }

        fun setsRNetTotal(sRNetTotal: String?) {
            this.sRNetTotal = sRNetTotal
        }
    }
}
