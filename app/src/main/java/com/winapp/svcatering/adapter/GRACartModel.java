package com.winapp.svcatering.adapter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GRACartModel {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("item")
    @Expose
    private ArrayList<Item> item = null;
    @SerializedName("product")
    @Expose
    private ArrayList<Product> product = null;
    @SerializedName("response_code")
    @Expose
    private Integer responseCode;

    @SerializedName("nettotal")
    @Expose
    private String nettotal;

    public String getNettotal() {
        return nettotal;
    }

    public void setNettotal(String nettotal) {
        this.nettotal = nettotal;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Item> getItem() {
        return item;
    }

    public void setItem(ArrayList<Item> item) {
        this.item = item;
    }

    public ArrayList<Product> getProduct() {
        return product;
    }

    public void setProduct(ArrayList<Product> product) {
        this.product = product;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public int uniqueId;

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public class Item {

        @SerializedName("customer_id")
        @Expose
        private String customerId;
        @SerializedName("cart")
        @Expose
        private String cart;

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getCart() {
            return cart;
        }

        public void setCart(String cart) {
            this.cart = cart;
        }
    }

    public class Product {

        @SerializedName("product_id")
        @Expose
        private String productId;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("quantity")
        @Expose
        private String quantity;
        @SerializedName("price")
        @Expose
        private String price;
        @SerializedName("total")
        @Expose
        private String total;
        @SerializedName("url")
        @Expose
        private String url;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    public String getCART_COLUMN_ID() {
        return CART_COLUMN_ID;
    }

    public void setCART_COLUMN_ID(String CART_COLUMN_ID) {
        this.CART_COLUMN_ID = CART_COLUMN_ID;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnitQty() {
        return unitQty;
    }

    public void setUnitQty(String unitQty) {
        this.unitQty = unitQty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCART_COLUMN_IMAGE() {
        return CART_COLUMN_IMAGE;
    }

    public void setCART_COLUMN_IMAGE(String CART_COLUMN_IMAGE) {
        this.CART_COLUMN_IMAGE = CART_COLUMN_IMAGE;
    }

    public String getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(String netTotal) {
        this.netTotal = netTotal;
    }

    public String getCART_COLUMN_WEIGHT() {
        return CART_COLUMN_WEIGHT;
    }

    public void setCART_COLUMN_WEIGHT(String CART_COLUMN_WEIGHT) {
        this.CART_COLUMN_WEIGHT = CART_COLUMN_WEIGHT;
    }

    public  String CART_COLUMN_ID = "id";
    public  String productId = "pid";
    public  String productName = "product_name";
    public  String unitQty = "qty";
    public String netQty="net_qty";
    public  String price = "price";
    public  String CART_COLUMN_IMAGE = "pimage";
    public  String netTotal = "net_price";
    public String CART_COLUMN_WEIGHT="net_weight";
    public String cartonQty ="ctn_qty";
    public String cartonPrice ="ctn_price";
    public String pcsPerCarton ="pcs_percarton";
    public String unitPrice ="unit_price";
    public String taxValue ="tax_value";
    public String totalValue ="total";

    public String foc_qty;
    public String foc_type;
    public String exchange_qty;
    public String exchange_type;
    public String discount;
    public String return_qty;
    public String return_type;
    public String stockRefNo;
    public String subTotal;
    public String stockQty;
    public String uomCode;

    public String getNetQty() {
        return netQty;
    }

    public void setNetQty(String netQty) {
        this.netQty = netQty;
    }

    public String getUomCode() {
        return uomCode;
    }

    public void setUomCode(String uomCode) {
        this.uomCode = uomCode;
    }

    public String getStockQty() {
        return stockQty;
    }

    public void setStockQty(String stockQty) {
        this.stockQty = stockQty;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getStockRefNo() {
        return stockRefNo;
    }

    public void setStockRefNo(String stockRefNo) {
        this.stockRefNo = stockRefNo;
    }

    public String getFoc_qty() {
        return foc_qty;
    }

    public void setFoc_qty(String foc_qty) {
        this.foc_qty = foc_qty;
    }

    public String getFoc_type() {
        return foc_type;
    }

    public void setFoc_type(String foc_type) {
        this.foc_type = foc_type;
    }

    public String getExchange_qty() {
        return exchange_qty;
    }

    public void setExchange_qty(String exchange_qty) {
        this.exchange_qty = exchange_qty;
    }

    public String getExchange_type() {
        return exchange_type;
    }

    public void setExchange_type(String exchange_type) {
        this.exchange_type = exchange_type;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getReturn_qty() {
        return return_qty;
    }

    public void setReturn_qty(String return_qty) {
        this.return_qty = return_qty;
    }

    public String getReturn_type() {
        return return_type;
    }

    public void setReturn_type(String return_type) {
        this.return_type = return_type;
    }

    public String getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(String totalValue) {
        this.totalValue = totalValue;
    }

    public String getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(String taxValue) {
        this.taxValue = taxValue;
    }

    public String getCartonQty() {
        return cartonQty;
    }

    public void setCartonQty(String cartonQty) {
        this.cartonQty = cartonQty;
    }

    public String getCartonPrice() {
        return cartonPrice;
    }

    public void setCartonPrice(String cartonPrice) {
        this.cartonPrice = cartonPrice;
    }

    public String getPcsPerCarton() {
        return pcsPerCarton;
    }

    public void setPcsPerCarton(String pcsPerCarton) {
        this.pcsPerCarton = pcsPerCarton;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }
}
