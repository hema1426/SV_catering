package com.winapp.svcatering.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CartModel {

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

    public String getCART_COLUMN_PID() {
        return CART_COLUMN_PID;
    }

    public void setCART_COLUMN_PID(String CART_COLUMN_PID) {
        this.CART_COLUMN_PID = CART_COLUMN_PID;
    }

    public String getCART_COLUMN_PNAME() {
        return CART_COLUMN_PNAME;
    }

    public void setCART_COLUMN_PNAME(String CART_COLUMN_PNAME) {
        this.CART_COLUMN_PNAME = CART_COLUMN_PNAME;
    }

    public String getCART_COLUMN_QTY() {
        return CART_COLUMN_QTY;
    }

    public void setCART_COLUMN_QTY(String CART_COLUMN_QTY) {
        this.CART_COLUMN_QTY = CART_COLUMN_QTY;
    }

    public String getCART_COLUMN_PRICE() {
        return CART_COLUMN_PRICE;
    }

    public void setCART_COLUMN_PRICE(String CART_COLUMN_PRICE) {
        this.CART_COLUMN_PRICE = CART_COLUMN_PRICE;
    }

    public String getCART_COLUMN_IMAGE() {
        return CART_COLUMN_IMAGE;
    }

    public void setCART_COLUMN_IMAGE(String CART_COLUMN_IMAGE) {
        this.CART_COLUMN_IMAGE = CART_COLUMN_IMAGE;
    }

    public String getCART_COLUMN_NET_PRICE() {
        return CART_COLUMN_NET_PRICE;
    }

    public void setCART_COLUMN_NET_PRICE(String CART_COLUMN_NET_PRICE) {
        this.CART_COLUMN_NET_PRICE = CART_COLUMN_NET_PRICE;
    }

    public String getCART_COLUMN_WEIGHT() {
        return CART_COLUMN_WEIGHT;
    }

    public void setCART_COLUMN_WEIGHT(String CART_COLUMN_WEIGHT) {
        this.CART_COLUMN_WEIGHT = CART_COLUMN_WEIGHT;
    }

    public  String CART_COLUMN_ID = "id";
    public  String CART_COLUMN_PID = "pid";
    public  String CART_COLUMN_PNAME = "product_name";
    public  String CART_COLUMN_QTY = "qty";
    public  String CART_COLUMN_PRICE = "price";
    public  String CART_COLUMN_IMAGE = "pimage";
    public  String CART_COLUMN_NET_PRICE = "net_price";
    public String CART_COLUMN_WEIGHT="net_weight";
    public String CART_COLUMN_CTN_QTY="ctn_qty";
    public String CART_COLUMN_CTN_PRICE="ctn_price";
    public String CART_PCS_PER_CARTON="pcs_percarton";
    public String CART_UNIT_PRICE="unit_price";
    public String CART_TAX_VALUE="tax_value";
    public String CART_TOTAL_VALUE="total";

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
    public String minimumSellingPrice;
    public String stockProductQty;

    public String getStockProductQty() {
        return stockProductQty;
    }

    public void setStockProductQty(String stockProductQty) {
        this.stockProductQty = stockProductQty;
    }

    public String getMinimumSellingPrice() {
        return minimumSellingPrice;
    }

    public void setMinimumSellingPrice(String minimumSellingPrice) {
        this.minimumSellingPrice = minimumSellingPrice;
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

    public String getCART_TOTAL_VALUE() {
        return CART_TOTAL_VALUE;
    }

    public void setCART_TOTAL_VALUE(String CART_TOTAL_VALUE) {
        this.CART_TOTAL_VALUE = CART_TOTAL_VALUE;
    }

    public String getCART_TAX_VALUE() {
        return CART_TAX_VALUE;
    }

    public void setCART_TAX_VALUE(String CART_TAX_VALUE) {
        this.CART_TAX_VALUE = CART_TAX_VALUE;
    }

    public String getCART_COLUMN_CTN_QTY() {
        return CART_COLUMN_CTN_QTY;
    }

    public void setCART_COLUMN_CTN_QTY(String CART_COLUMN_CTN_QTY) {
        this.CART_COLUMN_CTN_QTY = CART_COLUMN_CTN_QTY;
    }

    public String getCART_COLUMN_CTN_PRICE() {
        return CART_COLUMN_CTN_PRICE;
    }

    public void setCART_COLUMN_CTN_PRICE(String CART_COLUMN_CTN_PRICE) {
        this.CART_COLUMN_CTN_PRICE = CART_COLUMN_CTN_PRICE;
    }

    public String getCART_PCS_PER_CARTON() {
        return CART_PCS_PER_CARTON;
    }

    public void setCART_PCS_PER_CARTON(String CART_PCS_PER_CARTON) {
        this.CART_PCS_PER_CARTON = CART_PCS_PER_CARTON;
    }

    public String getCART_UNIT_PRICE() {
        return CART_UNIT_PRICE;
    }

    public void setCART_UNIT_PRICE(String CART_UNIT_PRICE) {
        this.CART_UNIT_PRICE = CART_UNIT_PRICE;
    }
}
