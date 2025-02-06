package com.winapp.svcatering.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.winapp.svcatering.activity.AddInvoiceActivityOld;
import com.winapp.svcatering.activity.MainHomeActivity;
import com.winapp.svcatering.fragments.ProductFragment;
import com.winapp.svcatering.model.CartModel;
import com.winapp.svcatering.model.CashCollectionInvoiceModel;
import com.winapp.svcatering.model.CreateInvoiceModel;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.ProductImageModel;
import com.winapp.svcatering.model.ProductsModel;
import com.winapp.svcatering.model.ReturnProductsModel;
import com.winapp.svcatering.model.SalesReturnModel;
import com.winapp.svcatering.model.SettingsModel;
import com.winapp.svcatering.model.UserRoll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Catalog.db";
    public static final String TABLE_CART = "Cart";
    public static final String TABLE_CUSTOMER = "Customers";
    public static final String TABLE_CUSTOMER_URL = "CustomerUrl";
    public static final String TABLE_PRODUCTS = "Products";
    public static final String TABLE_COMPANIES = "Companies";
    public static final String TABLE_CATALOG_PRODUCTS = "CatalogProducts";
    public static final String TABLE_CASH_COLLECTION = "CashCollection";
    public static final String TABLE_SETTINGS = "Settings";
    public static final String TABLE_USER_ROLL_SETTINGS = "UserSettings";
    public static final String TABLE_CUSTOMER_LIST = "CustomerList";
    public static final String TABLE_SALES_RETURN = "SalesReturn";
    public static final String TABLE_PRODUCT_IMAGE = "ProductsImage";
    public static final String TABLE_SAP_CART = "SapCart";
    public static final String CUSTOMER_TAX_TABLE = "CustomerTaxTable";
    public static final String CREATE_INVOICE_TABLE = "CreateInvoiceTable";
    public static final String RETURN_PRODUCT_TABLE = "Return_Products";


    public String PRODUCT_CODE = "product_code";
    public String PRODUCT_NAME = "product_name";
    public String ACTUAL_QTY = "actual_qty";
    public String RETURN_QTY = "return_qty";
    public String NET_QTY = "net_qty";
    public String PRICE = "price";
    public String TOTAL = "total";
    public String SUB_TOTAL = "sub_total";
    public String GST_AMOUNT = "gst_amount";
    public String NET_TOTAL = "net_total";
    public String FOC_QTY = "foc_qty";
    public String STOCK_QTY = "stock_qty";
    public String STOCK_QTYP = "stock_qtyp";
    public String UPDATE_TIME = "update_time";
    public String ISITEM_FOC = "item_foc";
    public String ITEM_DISC = "item_disc";
    public String BILL_DISC = "bill_disc";
    public String SALEABLE = "saleable";
    public String DAMAGED = "damaged";
    public String EXCHANGE_QTY = "exchange_qty";
    public String MINIMUMSELL_PRICE = "minimumsell_price";

    public String UOM_CODE = "uom_code";
    public String UOM_TEXT = "uom_text";

    private HashMap hp;
    Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table " + TABLE_CART + " " +
                "(ID INTEGER PRIMARY KEY   AUTOINCREMENT,pid text, pname text,ctnqty text,qty text,price text, pimage text,netprice text,netweight text," +
                "ctnprice text,unitprice text,pcspercarton text,tax text,total text,taxtype text,foc_qty text,foc_type text,exchange_qty text,exchange_type text,discount text,return_qty text,return_type text,stockrefno text,sub_total text,stock_qty text,uomcode text,minimumsellingprice text,stock_qtyp text)"
        );

        db.execSQL("create table " + TABLE_CUSTOMER_URL + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT,customerurl text)"
        );

        db.execSQL("create table " + TABLE_CUSTOMER + " " +
                "(cid text,customername text,phoneno text,address1 text,address2 text, address3 text,isactive text,havetax text,taxtype text," +
                " taxperc text,taxcode text,creditlimit text,country text,currencycode text)"
        );

        db.execSQL("create table " + TABLE_PRODUCTS + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT,productid text,productname text,productweight text,productimage text,retailprice text,wholesaleprice text,stockqty text," +
                "cartonprice text,unitcost text,uomcode text,pcspercarton text,barcode text)"
        );

        db.execSQL("create table " + TABLE_COMPANIES + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT,companyid text,companyname text,isactive text)"
        );
        db.execSQL("create table " + TABLE_CASH_COLLECTION + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT,customercode text,invoiceno text,date text,nettotal text,payable text,discount text,balance text,netbalance text,paidamount text,discountamount text,ischecked text)"
        );

        db.execSQL("create table " + TABLE_SETTINGS + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT,settingname text,settingvalue text)"
        );

        db.execSQL("create table " + TABLE_USER_ROLL_SETTINGS + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT,formcode text,formname text,havepermission text,isactive text)"
        );

        db.execSQL("create table " + TABLE_CUSTOMER_LIST + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT,customercode text,customername text,address text,outstanding text,taxcode text,taxperc text,taxtype text,havetax text)"
        );

        db.execSQL("create table " + TABLE_SALES_RETURN + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT,customercode text,salesreturnno text,date text,paidamount text,balanceamount text,ischecked)"
        );

        db.execSQL("create table " + TABLE_PRODUCT_IMAGE + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT,productcode text,productname text,imagepath text)"
        );

        db.execSQL("create table " + TABLE_SAP_CART + " " +
                "(ID INTEGER PRIMARY KEY   AUTOINCREMENT,pid text, pname text,ctnqty text,qty text,price text, pimage text,netprice text,netweight text," +
                "ctnprice text,unitprice text,pcspercarton text,tax text,total text,taxtype text,foc_qty text,foc_type text,exchange_qty text,exchange_type text,discount text,return_qty text,return_type text,stockrefno text,sub_total text,stock_qty text,uomcode text,minimumSellingPrice text,stock_qtyp text)"
        );

        db.execSQL("create table " + CUSTOMER_TAX_TABLE + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT,customerId text,taxCode text,taxType text,taxPerc text,customerName text,address text)"
        );

        db.execSQL("CREATE TABLE " + CREATE_INVOICE_TABLE + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," + PRODUCT_CODE + " TEXT," + PRODUCT_NAME + " TEXT," + UOM_CODE + " TEXT," + UOM_TEXT + " TEXT," + ACTUAL_QTY + " TEXT," + STOCK_QTY + " TEXT," + RETURN_QTY + " TEXT," + NET_QTY + " TEXT," + FOC_QTY + " TEXT," + PRICE + " TEXT," + TOTAL + " TEXT," + SUB_TOTAL + " TEXT," + GST_AMOUNT + " TEXT," + NET_TOTAL + " TEXT," + ITEM_DISC + " TEXT,"+ BILL_DISC +" TEXT,"+ SALEABLE + " TEXT,"+ DAMAGED +" TEXT,"+ EXCHANGE_QTY +" TEXT,"+ MINIMUMSELL_PRICE +" TEXT,"+STOCK_QTYP+ " TEXT,"+ UPDATE_TIME +" TEXT,"+ISITEM_FOC+ " TEXT )"

        );

        db.execSQL("CREATE TABLE " + RETURN_PRODUCT_TABLE + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT,productcode text,productname text,returnqty text,returnreason text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_URL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATALOG_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CASH_COLLECTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_ROLL_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALES_RETURN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_IMAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAP_CART);
        db.execSQL("DROP TABLE IF EXISTS " + CUSTOMER_TAX_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_INVOICE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RETURN_PRODUCT_TABLE);
        onCreate(db);
    }

    public void removeAllReturn() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(RETURN_PRODUCT_TABLE, null, null);
        db.close();
    }

    public boolean updateReturnQty(String action, String returnqty, String returnreason, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (action.equals("Update")) {
            contentValues.put("returnqty", returnqty);
            contentValues.put("returnreason", returnreason);
            db.update(RETURN_PRODUCT_TABLE, contentValues, "productcode = ? AND returnreason = ?", new String[]{id, returnreason});
            Log.w("Return_updated_Success", contentValues.toString());
        } else {
            Log.w("Return_delete_Success", contentValues.toString());
            db.delete(RETURN_PRODUCT_TABLE, "productcode = ? AND returnreason = ?", new String[]{id, returnreason});
        }
        return true;
    }

    @SuppressLint("Range")
    public ArrayList<ReturnProductsModel> getReturnProducts(String productId) {
        ArrayList<ReturnProductsModel> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        //  Cursor cursor =  db.rawQuery( "SELECT * FROM "+ RETURN_PRODUCT_TABLE +" WHERE productcode =  '" +productId+ "'", null );
        Cursor cursor = db.rawQuery("SELECT * FROM " + RETURN_PRODUCT_TABLE + " where productcode = ?", new String[]{productId});
        Log.w("GetReturnQtyQuery::", "SELECT * FROM " + RETURN_PRODUCT_TABLE + " WHERE productcode='" + productId + "'");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ReturnProductsModel model = new ReturnProductsModel();
            model.setProductCode(cursor.getString(cursor.getColumnIndex("productcode")));
            model.setProductName(cursor.getString(cursor.getColumnIndex("productname")));
            model.setReturnQty(cursor.getString(cursor.getColumnIndex("returnqty")));
            model.setReturnReason(cursor.getString(cursor.getColumnIndex("returnreason")));
            returnList.add(model);
            cursor.moveToNext();
        }
        cursor.close();
        return returnList;
    }


    public void insertReturnProduct(String productId, String productName, String returnQty, String returnReason) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("productcode", productId);
            cv.put("productname", productName);
            cv.put("returnqty", returnQty);
            cv.put("returnreason", returnReason);
            db.insert(RETURN_PRODUCT_TABLE, null, cv);
            Log.w("ReturnInsert:", cv.toString());
            db.close();
        } catch (Exception e) {
            Log.e("ProblemReturnUpdating", e + " ");
        }
    }

    public void insertSalesReturn(ArrayList<SalesReturnModel> salesReturnList) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (salesReturnList.size() > 0) {
            try {
                // salesreturnno text,date text,paidamount text,balanceamount text
                for (SalesReturnModel sales : salesReturnList) {
                    ContentValues cv = new ContentValues();
                    cv.put("salesreturnno", sales.getSalesReturnNumber());
                    cv.put("date", sales.getSalesReturnDate());
                    cv.put("paidamount", sales.getPaidAmount());
                    cv.put("balanceamount", sales.getBalanceAmount());
                    cv.put("ischecked", sales.getIsCheked());
                    cv.put("customercode", sales.getCustomerCode());
                    db.insert(TABLE_SALES_RETURN, null, cv);
                    Log.w("InsertAllValuesSuccess:", "Success");
                }
                db.close();
            } catch (Exception e) {
                Log.e("Problem", e + " ");
            }
        }
    }

    public void insertProductsImages(ArrayList<ProductImageModel> productImagesList) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (productImagesList.size() > 0) {
            try {
                for (ProductImageModel imageModel : productImagesList) {
                    ContentValues cv = new ContentValues();
                    String imagepath = imageModel.getProductName() + "_" + imageModel.getProductId() + ".jpg";
                    cv.put("productcode", imageModel.getProductId());
                    cv.put("productname", imageModel.getProductName());
                    cv.put("imagepath", imagepath);
                    db.insert(TABLE_PRODUCT_IMAGE, null, cv);
                }
                db.close();
            } catch (Exception e) {
                Log.e("ProblemImageUpdating", e + " ");
            }
        }
    }

    public void insertProductsImageOneByOne(String productId, String productName) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            String imagepath = productName + "_" + productId + ".jpg";
            cv.put("productcode", productId);
            cv.put("productname", productName);
            cv.put("imagepath", imagepath);
            db.insert(TABLE_PRODUCT_IMAGE, null, cv);
            Log.w("ImageInsert:", cv.toString());
            db.close();
        } catch (Exception e) {
            Log.e("ProblemImageUpdating", e + " ");
        }
    }

    @SuppressLint("Range")
    public String getProductImage(String productcode) {
        String productImage = "";
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT_IMAGE + " WHERE productcode='" + productcode + "'", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            productImage = cursor.getString(cursor.getColumnIndex("imagepath"));
            cursor.moveToNext();
        }
        cursor.close();
        return productImage;
    }

    public void insertCustomerTaxValues(ArrayList<CustomerDetails> customerTaxValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (customerTaxValues.size() > 0) {
            try {
                // salesreturnno text,date text,paidamount text,balanceamount text
                for (CustomerDetails model : customerTaxValues) {
                    ContentValues cv = new ContentValues();
                    cv.put("customerId", model.getCustomerCode());
                    cv.put("taxType", model.getTaxType());
                    cv.put("taxCode", model.getTaxCode());
                    cv.put("taxPerc", model.getTaxPerc());
                    cv.put("customerName", model.getCustomerName());
                    cv.put("address", model.getCustomerAddress1());
                    db.insert(CUSTOMER_TAX_TABLE, null, cv);
                    Log.w("AllTaxValues:", cv.toString());
                }
                db.close();
            } catch (Exception e) {
                Log.e("ProblemTax", e + " ");
            }
        }
    }

    @SuppressLint("Range")
    public ArrayList<CustomerDetails> getCustomer(String customerId) {
        ArrayList<CustomerDetails> taxList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CUSTOMER_TAX_TABLE, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CustomerDetails model = new CustomerDetails();
            model.setCustomerCode(cursor.getString(cursor.getColumnIndex("customerId")));
            model.setTaxCode(cursor.getString(cursor.getColumnIndex("taxCode")));
            model.setTaxType(cursor.getString(cursor.getColumnIndex("taxType")));
            model.setTaxPerc(cursor.getString(cursor.getColumnIndex("taxPerc")));
            model.setCustomerAddress1(cursor.getString(cursor.getColumnIndex("address")));
            model.setCustomerName(cursor.getString(cursor.getColumnIndex("customerName")));
            taxList.add(model);
            cursor.moveToNext();
        }
        cursor.close();
        return taxList;
    }

    public Integer removeCustomerTaxes() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CUSTOMER_TAX_TABLE, null, null);
    }

    @SuppressLint("Range")
    public ArrayList<SalesReturnModel> getAllSalesReturn() {
        ArrayList<SalesReturnModel> salesRetrunList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SALES_RETURN, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SalesReturnModel model = new SalesReturnModel();
            model.setCustomerCode(cursor.getString(cursor.getColumnIndex("customercode")));
            model.setSalesReturnNumber(cursor.getString(cursor.getColumnIndex("salesreturnno")));
            model.setSalesReturnDate(cursor.getString(cursor.getColumnIndex("date")));
            model.setPaidAmount(cursor.getString(cursor.getColumnIndex("paidamount")));
            model.setBalanceAmount(cursor.getString(cursor.getColumnIndex("balanceamount")));
            model.setIsCheked(cursor.getString(cursor.getColumnIndex("ischecked")));
            salesRetrunList.add(model);
            cursor.moveToNext();
        }
        cursor.close();
        return salesRetrunList;
    }

    public Integer deleteSalesReturn(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SALES_RETURN, "salesreturnno = ?", new String[]{id});
    }

    public boolean updateSalesReturn(String id, String paidamount, String balanceamount, String checked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("paidamount", paidamount);
        contentValues.put("balanceamount", balanceamount);
        contentValues.put("ischecked", checked);
        Log.w("CheckedValues:", checked);
        db.update(TABLE_SALES_RETURN, contentValues, "salesreturnno = ?", new String[]{id});
        Log.w("Values_updated_Success", contentValues.toString());
        return true;
    }

    public void removeAllSalesReturn() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SALES_RETURN, null, null);
        db.close();
    }


    public void insertCustomerList(ArrayList<CustomerModel> customersList) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (customersList.size() > 0) {
            try {
                for (CustomerModel roll : customersList) {
                    if (!roll.getCustomerCode().equals("null") && !roll.getCustomerName().equals("null") && !roll.getTaxCode().equals("null") && !roll.getTaxType().equals("null") && !roll.getTaxPerc().equals("null")) {
                        ContentValues cv = new ContentValues();
                        cv.put("customercode", roll.getCustomerCode());
                        cv.put("customername", roll.getCustomerName());
                        cv.put("address", roll.getAddress1());
                        cv.put("outstanding", roll.getOutstandingAmount());
                        cv.put("taxcode", roll.getTaxCode());
                        cv.put("taxperc", roll.getTaxPerc());
                        cv.put("taxtype", roll.getTaxType());
                        cv.put("havetax", roll.getHaveTax());
                        Log.w("AllCustomerValues:", cv.toString());
                        db.insert(TABLE_CUSTOMER_LIST, null, cv);
                        Log.w("InsertAllValuesSuccess:", "Success");
                    }
                }
                db.close();
            } catch (Exception e) {
                Log.e("Problem", e + " ");
            }
        }
    }


    @SuppressLint("Range")
    public ArrayList<CustomerModel> getAllCustomers() {
        ArrayList<CustomerModel> customerList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CUSTOMER_LIST, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CustomerModel model = new CustomerModel();
            model.setCustomerCode(cursor.getString(cursor.getColumnIndex("customercode")));
            model.setCustomerName(cursor.getString(cursor.getColumnIndex("customername")));
            model.setAddress1(cursor.getString(cursor.getColumnIndex("address")));
            model.setOutstandingAmount(cursor.getString(cursor.getColumnIndex("outstanding")));
            customerList.add(model);
            cursor.moveToNext();
        }
        cursor.close();
        return customerList;
    }

    public Integer removeCustomer(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.w("RemovedCustomerId:", id);
        return db.delete(TABLE_CUSTOMER_LIST, "customercode = ?", new String[]{id});
    }

    public void removeAllCustomers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CUSTOMER_LIST, null, null);
        db.close();
    }

    public void removeAllCustomersDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CUSTOMER, null, null);
        db.close();
    }

    @SuppressLint("Recycle")
    public void insertSettings(String settingname, String settingvalue) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String q = "SELECT * FROM " + TABLE_SETTINGS + " WHERE settingname='" + settingname + "'";
        cursor = db.rawQuery(q, null);
        if (cursor.getCount() != 0) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("settingname", settingname);
                contentValues.put("settingvalue", settingvalue);
                db.update(TABLE_SETTINGS, contentValues, "settingname = ?", new String[]{settingname});
                Log.w("Values_updated_Success", "CashCollection");
            } catch (Exception e) {
                Log.e("Problem", e + " ");
            }
        } else {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("settingname", settingname);
                contentValues.put("settingvalue", settingvalue);
                db.insert(TABLE_SETTINGS, null, contentValues);
                db.close();
            } catch (Exception ex) {
            }
        }
    }


    public void insertUserRollPermission(ArrayList<UserRoll> userRolls) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (userRolls.size() > 0) {
            try {
                for (UserRoll roll : userRolls) {
                    ContentValues cv = new ContentValues();
                    cv.put("formcode", roll.getFormCode());
                    cv.put("formname", roll.getFormName());
                    cv.put("havepermission", roll.getHavePermission());
                    cv.put("isactive", roll.getIsActive());
                    db.insert(TABLE_USER_ROLL_SETTINGS, null, cv);
                    Log.w("InsertAllValuesSuccess:", "Success");
                }
                db.close();
            } catch (Exception e) {
                Log.e("Problem", e + " ");
            }
        }
    }

    @SuppressLint("Range")
    public ArrayList<UserRoll> getUserPermissions() {
        ArrayList<UserRoll> userRolls = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER_ROLL_SETTINGS, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            UserRoll rolls = new UserRoll();
            rolls.setFormCode(cursor.getString(cursor.getColumnIndex("formcode")));
            rolls.setFormName(cursor.getString(cursor.getColumnIndex("formname")));
            rolls.setHavePermission(cursor.getString(cursor.getColumnIndex("havepermission")));
            userRolls.add(rolls);
            cursor.moveToNext();
        }
        cursor.close();
        return userRolls;
    }

    public void removeAllUserPermission() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER_ROLL_SETTINGS, null, null);
        db.close();
    }

    @SuppressLint("Recycle")
    public boolean insertCashcollection(String customercode, String invno, String indate, String nettotal, String payable, String discount, String bal, String netbal, String paidamt, String discountamt, String checked) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String q = "SELECT * FROM " + TABLE_CASH_COLLECTION + " WHERE invoiceno='" + invno + "'";
        cursor = db.rawQuery(q, null);
        if (cursor.getCount() != 0) {
            try {
                updateInvoice(invno, payable, discount, bal, checked);
            } catch (Exception e) {
                Log.e("Problem", e + " ");
            }
        } else {
            try {
                ContentValues cv = new ContentValues();
                cv.put("customercode", customercode);
                cv.put("invoiceno", invno);
                cv.put("date", indate);
                cv.put("nettotal", nettotal);
                cv.put("payable", payable);
                cv.put("discount", discount);
                cv.put("balance", bal);
                cv.put("netbalance", netbal);
                cv.put("paidamount", paidamt);
                cv.put("discountamount", discountamt);
                cv.put("ischecked", checked);

                Log.w("isCheckedHere_insert:", cv.toString());
                db.insert(TABLE_CASH_COLLECTION, null, cv);
                Log.w("InsertedInvoiceNumber:", invno);
                db.close();
            } catch (Exception e) {
                Log.e("Problem", e + " ");
            }
        }

        return true;
    }

    @SuppressLint("Range")
    public ArrayList<SettingsModel> getSettings() {
        ArrayList<SettingsModel> settings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SETTINGS, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SettingsModel model = new SettingsModel();
            model.setSettingName(cursor.getString(cursor.getColumnIndex("settingname")));
            model.setSettingValue(cursor.getString(cursor.getColumnIndex("settingvalue")));
            settings.add(model);
            cursor.moveToNext();
        }
        cursor.close();
        return settings;
    }

    public void removeAllInvoices() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CASH_COLLECTION, null, null);
        db.close();
    }

    public void deleteInvoice(String invoiceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CASH_COLLECTION, "invoiceno = ?", new String[]{invoiceId});
        db.close();
    }


    @SuppressLint("Range")
    public ArrayList<CashCollectionInvoiceModel> getAllInvoices() {
        ArrayList<CashCollectionInvoiceModel> cashcollection = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CASH_COLLECTION, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CashCollectionInvoiceModel model = new CashCollectionInvoiceModel();
            model.setCustomerCode(cursor.getString(cursor.getColumnIndex("customercode")));
            model.setInvoiceNumber(cursor.getString(cursor.getColumnIndex("invoiceno")));
            model.setInvoiceDate(cursor.getString(cursor.getColumnIndex("date")));
            model.setNetTotal(cursor.getString(cursor.getColumnIndex("nettotal")));
            model.setPayable(cursor.getString(cursor.getColumnIndex("payable")));
            model.setDiscount(cursor.getString(cursor.getColumnIndex("discount")));
            model.setBalance(cursor.getString(cursor.getColumnIndex("balance")));
            model.setNetBalance(cursor.getString(cursor.getColumnIndex("netbalance")));
            model.setPaidAmount(cursor.getString(cursor.getColumnIndex("paidamount")));
            model.setDiscountAmount(cursor.getString(cursor.getColumnIndex("discountamount")));
            model.setChecked(cursor.getString(cursor.getColumnIndex("ischecked")));
            cashcollection.add(model);
            cursor.moveToNext();
        }
        cursor.close();
        return cashcollection;
    }

    public boolean updateInvoice(String id, String payable, String discount, String balance, String checked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("payable", payable);
        contentValues.put("discount", discount);
        contentValues.put("balance", balance);
        contentValues.put("ischecked", checked);
        Log.w("CheckedValues:", checked);
        db.update(TABLE_CASH_COLLECTION, contentValues, "invoiceno = ?", new String[]{id});
        Log.w("Values_updated", contentValues.toString());
        return true;
    }

    public boolean updateInvoiceChecked(String id, String checked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ischecked", checked);
        Log.w("CheckedValues_CheckAll:", checked);
        db.update(TABLE_CASH_COLLECTION, contentValues, "invoiceno = ?", new String[]{id});
        Log.w("updated_Success_Checked", contentValues.toString());
        return true;
    }

    public boolean insertUrl(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("customerurl", url);
            db.insert(TABLE_CUSTOMER_URL, null, contentValues);
            Log.w("inserted_Customer", "Success");
        } catch (Exception ex) {
        }
        return true;
    }

    @SuppressLint("Range")
    public String getCustomerUrl() {
        String customerUrl = null;
        SQLiteDatabase db = this.getWritableDatabase(); //get the database that was created in this instance
        Cursor cursor = db.rawQuery("select * from " + TABLE_CUSTOMER_URL, null);
        if (cursor.moveToLast()) {
            customerUrl = cursor.getString(cursor.getColumnIndex("customerurl"));
        } else {
            Log.e("error not found", "user can't be found or database empty1");
        }
        return customerUrl;
    }

    public boolean insertCompany(ArrayList<MainHomeActivity.MultipleCompanyModel> companyList) {
        SQLiteDatabase db = this.getWritableDatabase();
        int size = companyList.size();
        // Check that both lists have the same size
        if (size == 0) {
            throw new IllegalArgumentException();
            // Or some more elegant way to handle this error condition
        }
        try {
            for (int i = 0; i < size; ++i) {
                ContentValues cv = new ContentValues();
                cv.put("companyid", companyList.get(i).getCompanyId());
                cv.put("companyname", companyList.get(i).getCompanyName());
                cv.put("isactive", companyList.get(i).isActive());
                db.insertOrThrow(TABLE_COMPANIES, null, cv);
            }
            Log.w("Insert_all_success", "Success");
            db.close();
        } catch (Exception e) {
            Log.e("Problem", e + " ");
        }
        return true;
    }

    public void removeAllCompanies() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COMPANIES, null, null);
        db.close();
    }

    public boolean updateCompany(String companyid, String isactive) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String q = "SELECT * FROM " + TABLE_COMPANIES + "";
        cursor = db.rawQuery(q, null);
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range")
                    String isact = cursor.getString(cursor.getColumnIndex("isactive"));
                    if (isact.equals("1")) {
                        @SuppressLint("Range")
                        String id = cursor.getString(cursor.getColumnIndex("companyid"));
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("isactive", "0");
                        db.update(TABLE_COMPANIES, contentValues, "companyid= ?", new String[]{id});
                    }
                } while (cursor.moveToNext());
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("isactive", isactive);
        db.update(TABLE_COMPANIES, contentValues, "companyid = ?", new String[]{companyid});
        return true;
    }

    public String getCompany() {
        SQLiteDatabase db = this.getWritableDatabase();
        String companyid = "1";
        Cursor cursor = null;
        String q = "SELECT * FROM " + TABLE_COMPANIES + "";
        cursor = db.rawQuery(q, null);
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    String isact = cursor.getString(cursor.getColumnIndex("isactive"));
                    if (isact.equals("1")) {
                        companyid = cursor.getString(cursor.getColumnIndex("companyid"));
                    }
                } while (cursor.moveToNext());
            }
        }
        return companyid;
    }

    public ArrayList<MainHomeActivity.MultipleCompanyModel> getAllCompanies() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<MainHomeActivity.MultipleCompanyModel> companyList = new ArrayList<>();
        Cursor cursor = null;
        String q = "SELECT * FROM " + TABLE_COMPANIES + "";
        cursor = db.rawQuery(q, null);
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    MainHomeActivity.MultipleCompanyModel model = new MainHomeActivity.MultipleCompanyModel();
                    model.setCompanyId(cursor.getString(cursor.getColumnIndex("companyid")));
                    model.setCompanyName(cursor.getString(cursor.getColumnIndex("companyname")));
                    model.setActive(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("isactive"))));
                    companyList.add(model);
                } while (cursor.moveToNext());
            }
        }
        return companyList;
    }

    public boolean insertCustomer(String cid, String customername, String phoneno, String add1, String add2, String add3, String isactive, String havetax
            , String taxtype, String taxperc, String taxcode, String creditlimit, String country, String currencycode) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("cid", cid);
            contentValues.put("customername", customername);
            contentValues.put("phoneno", phoneno);
            contentValues.put("address1", add1);
            contentValues.put("address2", add2);
            contentValues.put("address3", add3);
            contentValues.put("isactive", isactive);
            contentValues.put("havetax", havetax);
            contentValues.put("taxtype", taxtype);
            contentValues.put("taxcode", taxcode);
            contentValues.put("taxperc", taxperc);
            contentValues.put("creditlimit", creditlimit);
            contentValues.put("country", country);
            contentValues.put("currencycode", currencycode);
            db.insert(TABLE_CUSTOMER, null, contentValues);
            Log.w("Values_inserted", "Success");
        } catch (Exception ex) {

        }
        return true;
    }


    public boolean insertCustomersDetails(ArrayList<CustomerDetails> customerDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (customerDetails.size() > 0) {
            try {
                for (CustomerDetails details : customerDetails) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("cid", details.getCustomerCode());
                    contentValues.put("customername", details.getCustomerName());
                    contentValues.put("phoneno", details.getPhoneNo());
                    contentValues.put("address1", details.getCustomerAddress1());
                    contentValues.put("address2", details.getCustomerAddress2());
                    contentValues.put("address3", details.getCustomerAddress3());
                    contentValues.put("isactive", details.getIsActive());
                    contentValues.put("havetax", details.getHaveTax());
                    contentValues.put("taxtype", details.getTaxType());
                    contentValues.put("taxcode", details.getTaxCode());
                    contentValues.put("taxperc", details.getTaxPerc());
                    contentValues.put("creditlimit", details.getCreditLimit());
                    contentValues.put("country", details.getCountry());
                    contentValues.put("currencycode", details.getCurrencyCode());
                    db.insert(TABLE_CUSTOMER, null, contentValues);
                    Log.w("Values_inserted", contentValues.toString());
                }
            } catch (Exception ex) {

            }
        }
        return true;
    }

    public void insertProducts(Context context, ArrayList<ProductsModel> products) {
        int size = products.size();
        // Check that both lists have the same size
        if (size == 0) {
            throw new IllegalArgumentException();
            // Or some more elegant way to handle this error condition
        }
        SQLiteDatabase db = getWritableDatabase();
        try {
            for (int i = 0; i < size; ++i) {
                ContentValues cv = new ContentValues();
                cv.put("productid", products.get(i).getProductCode());
                cv.put("productname", products.get(i).getProductName());
                cv.put("productweight", products.get(i).getWeight());
                cv.put("productimage", products.get(i).getProductImage());
                cv.put("retailprice", products.get(i).getRetailPrice());
                cv.put("wholesaleprice", products.get(i).getWholeSalePrice());
                cv.put("stockqty", products.get(i).getStockQty());
                cv.put("cartonprice", products.get(i).getCartonPrice());
                cv.put("unitcost", products.get(i).getUnitCost());
                cv.put("uomcode", products.get(i).getUomCode());
                cv.put("pcspercarton", products.get(i).getPcsPerCarton());
                cv.put("barcode", products.get(i).getProductBarcode());
                db.insertOrThrow(TABLE_PRODUCTS, null, cv);
            }

            Log.w("Insert_all_success_1", "Success");
            if (context instanceof AddInvoiceActivityOld) {
                ProductFragment.closeDialog();
            }
            db.close();
        } catch (Exception e) {
            Log.e("Problem", e + " ");
        }
    }

    public void insertCatalogProducts(ArrayList<ProductsModel> products) {
        int size = products.size();
        // Check that both lists have the same size
        if (size == 0) {
            throw new IllegalArgumentException();
            // Or some more elegant way to handle this error condition
        }
        SQLiteDatabase db = getWritableDatabase();
        try {
            for (int i = 0; i < size; ++i) {
                ContentValues cv = new ContentValues();
                cv.put("productid", products.get(i).getProductCode());
                cv.put("productname", products.get(i).getProductName());
                cv.put("productweight", products.get(i).getWeight());
                cv.put("productimage", products.get(i).getProductImage());
                cv.put("retailprice", products.get(i).getRetailPrice());
                cv.put("wholesaleprice", products.get(i).getWholeSalePrice());
                cv.put("stockqty", products.get(i).getStockQty());
                cv.put("cartonprice", products.get(i).getCartonPrice());
                cv.put("unitcost", products.get(i).getUnitCost());
                cv.put("uomcode", products.get(i).getUomCode());
                cv.put("pcspercarton", products.get(i).getPcsPerCarton());
                db.insertOrThrow(TABLE_PRODUCTS, null, cv);
            }
            Log.w("Insert_all_success", "Success");
            db.close();
        } catch (Exception e) {
            Log.e("Problem", e + " ");
        }
    }

    public void removeAllProducts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, null, null);
        db.close();
    }


    public ArrayList<ProductsModel> getAllProducts() {
        ArrayList<ProductsModel> productsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ProductsModel model = new ProductsModel();
            model.setProductCode(cursor.getString(cursor.getColumnIndex("productid")));
            model.setProductName(cursor.getString(cursor.getColumnIndex("productname")));
            model.setWeight(cursor.getString(cursor.getColumnIndex("productweight")));
            model.setProductImage(cursor.getString(cursor.getColumnIndex("productimage")));
            model.setRetailPrice(Double.parseDouble(cursor.getString(cursor.getColumnIndex("retailprice"))));
            model.setWholeSalePrice(cursor.getString(cursor.getColumnIndex("wholesaleprice")));
            model.setStockQty(cursor.getString(cursor.getColumnIndex("stockqty")));
            model.setCartonPrice(cursor.getString(cursor.getColumnIndex("cartonprice")));
            model.setUnitCost(cursor.getString(cursor.getColumnIndex("unitcost")));
            model.setUomCode(cursor.getString(cursor.getColumnIndex("uomcode")));
            model.setPcsPerCarton(cursor.getString(cursor.getColumnIndex("pcspercarton")));
            productsList.add(model);
            cursor.moveToNext();
        }
        cursor.close();
        return productsList;
    }

    public ArrayList<ProductsModel> getProductFromBarcode(String barcode) {
        ArrayList<ProductsModel> products = new ArrayList<>();
        ProductsModel model = new ProductsModel();
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE barcode='" + barcode + "'", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            model.setProductCode(cursor.getString(cursor.getColumnIndex("productid")));
            model.setProductName(cursor.getString(cursor.getColumnIndex("productname")));
            model.setWeight(cursor.getString(cursor.getColumnIndex("productweight")));
            model.setProductImage(cursor.getString(cursor.getColumnIndex("productimage")));
            model.setRetailPrice(Double.parseDouble(cursor.getString(cursor.getColumnIndex("retailprice"))));
            model.setWholeSalePrice(cursor.getString(cursor.getColumnIndex("wholesaleprice")));
            model.setStockQty(cursor.getString(cursor.getColumnIndex("stockqty")));
            model.setCartonPrice(cursor.getString(cursor.getColumnIndex("cartonprice")));
            model.setUnitCost(cursor.getString(cursor.getColumnIndex("unitcost")));
            model.setUomCode(cursor.getString(cursor.getColumnIndex("uomcode")));
            model.setPcsPerCarton(cursor.getString(cursor.getColumnIndex("pcspercarton")));
            model.setProductBarcode(cursor.getString(cursor.getColumnIndex("barcode")));
            products.add(model);
            cursor.moveToNext();
        }
        cursor.close();
        return products;
    }

    public ArrayList<ProductsModel> getAllCatalogProducts() {
        ArrayList<ProductsModel> productsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ProductsModel model = new ProductsModel();
            model.setProductCode(cursor.getString(cursor.getColumnIndex("productid")));
            model.setProductName(cursor.getString(cursor.getColumnIndex("productname")));
            model.setWeight(cursor.getString(cursor.getColumnIndex("productweight")));
            model.setProductImage(cursor.getString(cursor.getColumnIndex("productimage")));
            model.setRetailPrice(Double.parseDouble(cursor.getString(cursor.getColumnIndex("retailprice"))));
            model.setStockQty(cursor.getString(cursor.getColumnIndex("stockqty")));
            model.setCartonPrice(cursor.getString(cursor.getColumnIndex("cartonprice")));
            model.setUnitCost(cursor.getString(cursor.getColumnIndex("unitcost")));
            model.setUomCode(cursor.getString(cursor.getColumnIndex("uomcode")));
            model.setPcsPerCarton(cursor.getString(cursor.getColumnIndex("pcspercarton")));
            productsList.add(model);
            cursor.moveToNext();
        }
        cursor.close();
        return productsList;
    }

    public ArrayList<CustomerDetails> getCustomer(String customerId, String id) {
        ArrayList<CustomerDetails> customerDetails = new ArrayList<>();
        String customerName = null;
        SQLiteDatabase db = this.getWritableDatabase(); //get the database that was created in this instance
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CUSTOMER_LIST + " WHERE customercode = '" + customerId + "'", null);
        if (cursor.moveToLast()) {
            CustomerDetails customer = new CustomerDetails();
            customer.setCustomerCode(cursor.getString(cursor.getColumnIndex("customercode")));
            customer.setCustomerName(cursor.getString(cursor.getColumnIndex("customername")));
            customer.setTaxPerc(cursor.getString(cursor.getColumnIndex("taxperc")));
            customer.setTaxType(cursor.getString(cursor.getColumnIndex("taxtype")));
            customer.setTaxCode(cursor.getString(cursor.getColumnIndex("taxcode")));


            //            customer.setCustomerAddress1(cursor.getString(cursor.getColumnIndex("address1")));
            // customer.setCurrencyCode(cursor.getString(cursor.getColumnIndex("currencycode")));
            //customer.setCreditLimit(cursor.getString(cursor.getColumnIndex("creditlimit")));
            customerDetails.add(customer);
        } else {
            Log.e("error not found", "user can't be found or database empty3");
        }
        return customerDetails;
    }

    public ArrayList<CustomerDetails> getCustomer() {
        ArrayList<CustomerDetails> customerDetails = new ArrayList<>();
     /*   String customerName = null;
        SQLiteDatabase db = this.getWritableDatabase(); //get the database that was created in this instance
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CUSTOMER, null);
        if (cursor.moveToLast()) {
            CustomerDetails customer=new CustomerDetails();
            customer.setCustomerCode(cursor.getString(cursor.getColumnIndex("cid")));
            customer.setCustomerName(cursor.getString(cursor.getColumnIndex("customername")));
            customer.setTaxPerc(cursor.getString(cursor.getColumnIndex("taxperc")));
            customer.setTaxType(cursor.getString(cursor.getColumnIndex("taxtype")));
            customer.setCustomerAddress1(cursor.getString(cursor.getColumnIndex("address1")));
            customer.setCurrencyCode(cursor.getString(cursor.getColumnIndex("currencycode")));
            customer.setCreditLimit(cursor.getString(cursor.getColumnIndex("creditlimit")));
            customerDetails.add(customer);
        }else {
            Log.e("error not found", "user can't be found or database empty2");
        }*/
        return customerDetails;
    }

    public boolean updateReturnCheck(String id, String returnType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("return_type", returnType);
        db.update(TABLE_CART, contentValues, "pid = ?", new String[]{id});
        return true;
    }


    public boolean insertCreateInvoiceCartEdit(String productCode, String productName, String
            uomcode, String actualQty, String returnQty, String netQty, String foc, String price,
            String stock, String total, String subTotal, String gstAmount, String netTotal ,
          String itemDisc ,String billDisc ,String saleable ,String damaged ,String exchangeQty,
          String minimumSellPrice,String productStock,String updateTime , String isItemFOC) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        // contentValues.put(PRODUCT_CODE, productCode+" ");
        contentValues.put(PRODUCT_CODE, productCode);
        contentValues.put(PRODUCT_NAME, productName);
        contentValues.put(UOM_CODE, uomcode);
        contentValues.put(ACTUAL_QTY, actualQty);
        contentValues.put(RETURN_QTY, returnQty);
        contentValues.put(PRICE, price);
        contentValues.put(NET_QTY, netQty);
        contentValues.put(FOC_QTY, foc);
        contentValues.put(TOTAL, total);
        contentValues.put(SUB_TOTAL, subTotal);
        contentValues.put(GST_AMOUNT, gstAmount);
        contentValues.put(NET_TOTAL, netTotal);
        contentValues.put(STOCK_QTY, stock);
        contentValues.put(ITEM_DISC, itemDisc);
        contentValues.put(BILL_DISC, billDisc);
        contentValues.put(SALEABLE, saleable);
        contentValues.put(DAMAGED, damaged);
        contentValues.put(EXCHANGE_QTY, exchangeQty);
        contentValues.put(MINIMUMSELL_PRICE, minimumSellPrice);
        contentValues.put(STOCK_QTYP, productStock);
        contentValues.put(UPDATE_TIME, updateTime);
        contentValues.put(ISITEM_FOC, isItemFOC);

        db.insert(CREATE_INVOICE_TABLE, null, contentValues);
        Log.w("InsertProductValuesC:", contentValues.toString());

        return true;
    }


    public boolean insertCreateInvoiceCart(String productCode, String productName, String uomcode,
                                           String uomText, String actualQty, String returnQty,
                                           String netQty, String foc,
                                           String price, String stock, String total, String subTotal,
                                           String gstAmount, String netTotal, String itemDisc ,String billDisc
            ,String saleable,String damaged,String exchangeQty,String minimumSellPrice
            ,String productStock,String updateTime , String isItemFOC) {
        Cursor cursor = null;
        String netqty = null;
        String focQty = null;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String q = "SELECT net_qty,foc_qty FROM " + CREATE_INVOICE_TABLE + " WHERE update_time='" + updateTime + "'";
            cursor = db.rawQuery(q, null);
            if (cursor.getCount() != 0) {
                if (cursor.moveToFirst()) {
                    do {
                        netqty = cursor.getString(cursor.getColumnIndex("net_qty"));
                        focQty = cursor.getString(cursor.getColumnIndex("foc_qty"));
                    } while (cursor.moveToNext());
                }
               // if (Double.parseDouble(netqty) > 0 || Double.parseDouble(focQty) > 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(PRODUCT_CODE, productCode);
                    contentValues.put(PRODUCT_NAME, productName);
                    contentValues.put(UOM_CODE, uomcode);
                    contentValues.put(ACTUAL_QTY, actualQty);
                    contentValues.put(RETURN_QTY, returnQty);
                    contentValues.put(PRICE, price);
                    contentValues.put(NET_QTY, netQty);
                    contentValues.put(FOC_QTY, foc);
                    contentValues.put(TOTAL, total);
                    contentValues.put(SUB_TOTAL, subTotal);
                    contentValues.put(GST_AMOUNT, gstAmount);
                    contentValues.put(NET_TOTAL, netTotal);
                    contentValues.put(STOCK_QTY, stock);
                    contentValues.put(ITEM_DISC, itemDisc);
                    contentValues.put(BILL_DISC, billDisc);
                    contentValues.put(SALEABLE, saleable);
                    contentValues.put(DAMAGED, damaged);
                    contentValues.put(EXCHANGE_QTY, exchangeQty);
                    contentValues.put(MINIMUMSELL_PRICE, minimumSellPrice);
                    contentValues.put(STOCK_QTYP, productStock);
                    contentValues.put(UPDATE_TIME, updateTime);
                    contentValues.put(ISITEM_FOC, isItemFOC);

                    db.update(CREATE_INVOICE_TABLE, contentValues, "update_time = ?", new String[]{updateTime});
                    //db.update(CREATE_INVOICE_TABLE, contentValues, "product_code = ?", new String[]{productCode});
                    Log.w("Cart_updated", "Success");
                    Toast.makeText(context, "Product Updated Successfully", Toast.LENGTH_LONG).show();
                    Log.w("InsertProductValuesIn1:", contentValues.toString());
                } else {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(PRODUCT_CODE, productCode);
                    contentValues.put(PRODUCT_NAME, productName);
                    contentValues.put(UOM_CODE, uomcode);
                    contentValues.put(ACTUAL_QTY, actualQty);
                    contentValues.put(RETURN_QTY, returnQty);
                    contentValues.put(PRICE, price);
                    contentValues.put(NET_QTY, netQty);
                    contentValues.put(FOC_QTY, foc);
                    contentValues.put(TOTAL, total);
                    contentValues.put(SUB_TOTAL, subTotal);
                    contentValues.put(GST_AMOUNT, gstAmount);
                    contentValues.put(NET_TOTAL, netTotal);
                    contentValues.put(STOCK_QTY, stock);
                    contentValues.put(UOM_TEXT, uomText);
                    contentValues.put(ITEM_DISC, itemDisc);
                    contentValues.put(BILL_DISC, billDisc);
                    contentValues.put(SALEABLE, saleable);
                    contentValues.put(DAMAGED, damaged);
                    contentValues.put(EXCHANGE_QTY, exchangeQty);
                    contentValues.put(MINIMUMSELL_PRICE, minimumSellPrice);
                    contentValues.put(STOCK_QTYP, productStock);
                    contentValues.put(UPDATE_TIME, updateTime);
                    contentValues.put(ISITEM_FOC, isItemFOC);

                    db.insert(CREATE_INVOICE_TABLE, null, contentValues);
                    Log.w("InsertProductValuesIn2:", contentValues.toString());
                }
          //  }
//        else {
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(PRODUCT_CODE, productCode);
//                contentValues.put(PRODUCT_NAME, productName);
//                contentValues.put(UOM_CODE, uomcode);
//                contentValues.put(ACTUAL_QTY, actualQty);
//                contentValues.put(RETURN_QTY, returnQty);
//                contentValues.put(PRICE, price);
//                contentValues.put(NET_QTY, netQty);
//                contentValues.put(FOC_QTY, foc);
//                contentValues.put(TOTAL, total);
//                contentValues.put(SUB_TOTAL, subTotal);
//                contentValues.put(GST_AMOUNT, gstAmount);
//                contentValues.put(NET_TOTAL, netTotal);
//                contentValues.put(STOCK_QTY, stock);
//                contentValues.put(UOM_TEXT, uomText);
//                contentValues.put(ITEM_DISC, itemDisc);
//                contentValues.put(BILL_DISC, billDisc);
//                contentValues.put(SALEABLE, saleable);
//                contentValues.put(DAMAGED, damaged);
//                contentValues.put(EXCHANGE_QTY, exchangeQty);
//                contentValues.put(MINIMUMSELL_PRICE, minimumSellPrice);
//                contentValues.put(STOCK_QTYP, productStock);
//
//                db.insert(CREATE_INVOICE_TABLE, null, contentValues);
//                Log.w("InsertProductValuesIn3:", contentValues.toString());
//            }
        }
        catch (Exception ex) {
        } finally {
            //  cursor.close();
            //   db.close();
        }
        return true;
    }


    public int getRandomProductId() {
        Random r = new Random();
        int low = 10;
        int high = 100;
        return r.nextInt(high - low) + low;
    }


    public boolean insertCart(String pid,
                              String pname, String ctnqty, String qty, String price, String pimage, String netprice, String netweight,
                              String ctnprice, String unitprice, String pcspercarton, String tax, String subtotal, String taxtype, String foc_qty,
                              String foc_type, String exchange_qty, String exchange_type, String discount,
                              String return_qty, String return_type, String ref_no, String total, String stock,
                              String uomcode, String minimumsellingprice ,String productStock) {
        Cursor cursor = null;
        String netqty = null;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String q = "SELECT qty FROM " + TABLE_CART + " WHERE pid='" + pid + "'";
            cursor = db.rawQuery(q, null);
            if (cursor.getCount() != 0) {
                if (cursor.moveToFirst()) {
                    do {
                        netqty = cursor.getString(cursor.getColumnIndex("qty"));
                    } while (cursor.moveToNext());
                }

                ContentValues contentValues = new ContentValues();
                contentValues.put("pname", pname);
                contentValues.put("ctnqty", ctnqty);
                contentValues.put("qty", qty);
                contentValues.put("price", price);
                contentValues.put("pimage", pimage);
                contentValues.put("netprice", netprice);
                contentValues.put("netweight", netweight);
                contentValues.put("ctnprice", ctnprice);
                contentValues.put("unitprice", unitprice);
                contentValues.put("pcspercarton", pcspercarton);
                contentValues.put("tax", tax);
                contentValues.put("total", total);
                contentValues.put("sub_total", subtotal);
                contentValues.put("taxtype", taxtype);
                contentValues.put("foc_qty", foc_qty);
                contentValues.put("foc_type", foc_type);
                contentValues.put("exchange_qty", exchange_qty);
                contentValues.put("exchange_type", exchange_type);
                contentValues.put("discount", discount);
                contentValues.put("return_qty", return_qty);
                contentValues.put("return_type", return_type);
                contentValues.put("stockrefno", ref_no);
                contentValues.put("stock_qty", stock);
                contentValues.put("uomcode", uomcode);
                contentValues.put("minimumsellingprice", minimumsellingprice);
                contentValues.put("stock_qtyp", productStock);

                db.update(TABLE_CART, contentValues, "pid = ?", new String[]{pid});
                Log.w("Cart_updated", "Success");
                Toast.makeText(context, "Product Updated2 Successfully", Toast.LENGTH_LONG).show();
                Log.w("InsertProductValues1:", contentValues.toString());
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put("pid", pid);
                contentValues.put("pname", pname);
                contentValues.put("ctnqty", ctnqty);
                contentValues.put("qty", qty);
                contentValues.put("price", price);
                contentValues.put("pimage", pimage);
                contentValues.put("netprice", netprice);
                contentValues.put("netweight", netweight);
                contentValues.put("ctnprice", ctnprice);
                contentValues.put("unitprice", unitprice);
                contentValues.put("pcspercarton", pcspercarton);
                contentValues.put("tax", tax);
                contentValues.put("total", total);
                contentValues.put("sub_total", subtotal);
                contentValues.put("taxtype", taxtype);
                contentValues.put("foc_qty", foc_qty);
                contentValues.put("foc_type", foc_type);
                contentValues.put("exchange_qty", exchange_qty);
                contentValues.put("exchange_type", exchange_type);
                contentValues.put("discount", discount);
                contentValues.put("return_qty", return_qty);
                contentValues.put("return_type", return_type);
                contentValues.put("stockrefno", ref_no);
                contentValues.put("stock_qty", stock);
                contentValues.put("uomcode", uomcode);
                contentValues.put("minimumsellingprice", minimumsellingprice);
                contentValues.put("stock_qtyp", productStock);

                db.insert(TABLE_CART, null, contentValues);
                Log.w("InsertProductValues:", contentValues.toString());
            }
        } catch (Exception ex) {
        } finally {
            cursor.close();
            db.close();
        }
        return true;
    }

    public Cursor getData(int pid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_CART + " where id=" + pid + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_CART);
        return numRows;
    }

    public int numberOfRowsInInvoice() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CREATE_INVOICE_TABLE);
        return numRows;
    }

    @SuppressLint("Recycle")
    public boolean updateProductStock(String pid, int value, String action) {
        String q = "SELECT stockqty FROM " + TABLE_PRODUCTS + " WHERE productid='" + pid + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String netqty = null;
        cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            do {
                netqty = cursor.getString(cursor.getColumnIndex("stockqty"));
            } while (cursor.moveToNext());
        }
        if (action.equals("addInvoice")) {
            assert netqty != null;
            double qt = Double.parseDouble(netqty) - value;
            ContentValues contentValues = new ContentValues();
            contentValues.put("stockqty", String.valueOf(qt));
            db.update(TABLE_PRODUCTS, contentValues, "productid = ?", new String[]{pid});
            Log.w("Net_qty_AddInvoice", qt + "");
        } else {
            double qt = Double.parseDouble(netqty) + value;
            ContentValues contentValues = new ContentValues();
            contentValues.put("stockqty", String.valueOf(qt));
            db.update(TABLE_PRODUCTS, contentValues, "productid = ?", new String[]{pid});
            Log.w("Net_qty_RemoveInvoice", qt + "");
        }
        return true;
    }


    public boolean updateCart(String id, String qty, String ctnQty, String sub_total, String tax, String net_total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        double total_value = Double.parseDouble(sub_total) + Double.parseDouble(tax);
        contentValues.put("qty", qty);
        contentValues.put("ctnqty", ctnQty);
        contentValues.put("sub_total", sub_total);
        contentValues.put("total", String.valueOf(total_value));
        contentValues.put("tax", tax);
        contentValues.put("netprice", net_total);
        // MY_TABLE_NAME, cv, "_id = ?", new String[]{id}
        db.update(TABLE_CART, contentValues, "pid = ?", new String[]{id});
        return true;
    }

    public void updateQty(String id) {
        String q = "SELECT qty FROM " + TABLE_CART + " WHERE pid='" + id + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String netqty = null;
        cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            do {
                netqty = cursor.getString(cursor.getColumnIndex("qty"));
            } while (cursor.moveToNext());
        }
        int qt = Integer.parseInt(netqty) + 1;
        ContentValues contentValues = new ContentValues();
        contentValues.put("qty", String.valueOf(qt));
        //  contentValues.put("netprice", net_amount);
        db.update(TABLE_CART, contentValues, "pid=" + id, null);
        Log.w("Net_qty", netqty);
    }

    public Integer deleteProduct(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CART, "pid = ?", new String[]{id});
    }

    public Integer deleteInvoiceProduct(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CREATE_INVOICE_TABLE, "product_code = ?", new String[]{id});
    }
    public Integer deleteInvoiceProductNew(String id,String updateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CREATE_INVOICE_TABLE, "update_time = ?", new String[]{updateTime});
    }


    public ArrayList<CreateInvoiceModel> getAllInvoiceProducts() {
        ArrayList<CreateInvoiceModel> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT * FROM " + CREATE_INVOICE_TABLE, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CreateInvoiceModel data = new CreateInvoiceModel();
            data.setProductCode(cursor.getString(cursor.getColumnIndex(PRODUCT_CODE)));
            data.setProductName(cursor.getString(cursor.getColumnIndex(PRODUCT_NAME)));
            data.setUomCode(cursor.getString(cursor.getColumnIndex(UOM_CODE)));
            data.setActualQty(cursor.getString(cursor.getColumnIndex(ACTUAL_QTY)));
            data.setFocQty(cursor.getString(cursor.getColumnIndex(FOC_QTY)));
            data.setReturnQty(cursor.getString(cursor.getColumnIndex(RETURN_QTY)));
            data.setNetQty(cursor.getString(cursor.getColumnIndex(NET_QTY)));
            data.setPrice(cursor.getString(cursor.getColumnIndex(PRICE)));
            data.setTotal(cursor.getString(cursor.getColumnIndex(TOTAL)));
            data.setSubTotal(cursor.getString(cursor.getColumnIndex(SUB_TOTAL)));
            data.setGstAmount(cursor.getString(cursor.getColumnIndex(GST_AMOUNT)));
            data.setNetTotal(cursor.getString(cursor.getColumnIndex(NET_TOTAL)));
            data.setStockQty(cursor.getString(cursor.getColumnIndex(STOCK_QTY)));
            data.setUomText(cursor.getString(cursor.getColumnIndex(UOM_TEXT)));
            data.setItemDisc(cursor.getString(cursor.getColumnIndex(ITEM_DISC)));
            data.setBillDisc(cursor.getString(cursor.getColumnIndex(BILL_DISC)));
            data.setSaleableQty(cursor.getString(cursor.getColumnIndex(SALEABLE)));
            data.setDamagedQty(cursor.getString(cursor.getColumnIndex(DAMAGED)));
            data.setExchangeQty(cursor.getString(cursor.getColumnIndex(EXCHANGE_QTY)));
            data.setMinimumSellingPrice(cursor.getString(cursor.getColumnIndex(MINIMUMSELL_PRICE)));
            data.setStockProductQty(cursor.getString(cursor.getColumnIndex(STOCK_QTYP)));
            data.setUpdateTime(cursor.getString(cursor.getColumnIndex(UPDATE_TIME)));
            data.setIsItemFOC(cursor.getString(cursor.getColumnIndex(ISITEM_FOC)));

            array_list.add(data);
            cursor.moveToNext();
        }
        cursor.close();
        return array_list;
    }

    public ArrayList<CartModel> getAllCartItems() {
        ArrayList<CartModel> array_list = new ArrayList<CartModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("select * from " + TABLE_CART, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            CartModel data = new CartModel();
            data.setUniqueId(cursor.getColumnIndex(String.valueOf(cursor.getColumnIndex("ID"))));
            data.setCART_COLUMN_PID(cursor.getString(cursor.getColumnIndex("pid")));
            data.setCART_COLUMN_PNAME(cursor.getString(cursor.getColumnIndex("pname")));
            data.setCART_COLUMN_PRICE(cursor.getString(cursor.getColumnIndex("price")));
            data.setCART_COLUMN_QTY(cursor.getString(cursor.getColumnIndex("qty")));
            data.setCART_PCS_PER_CARTON(cursor.getString(cursor.getColumnIndex("pcspercarton")));
            data.setCART_COLUMN_NET_PRICE(cursor.getString(cursor.getColumnIndex("netprice")));
            data.setCART_COLUMN_IMAGE(cursor.getString(cursor.getColumnIndex("pimage")));
            data.setCART_COLUMN_WEIGHT(cursor.getString(cursor.getColumnIndex("netweight")));
            data.setCART_COLUMN_CTN_PRICE(cursor.getString(cursor.getColumnIndex("ctnprice")));
            data.setCART_UNIT_PRICE(cursor.getString(cursor.getColumnIndex("unitprice")));
            data.setCART_COLUMN_CTN_QTY(cursor.getString(cursor.getColumnIndex("ctnqty")));
            data.setCART_TAX_VALUE(cursor.getString(cursor.getColumnIndex("tax")));
            data.setCART_TOTAL_VALUE(cursor.getString(cursor.getColumnIndex("total")));
            data.setSubTotal(cursor.getString(cursor.getColumnIndex("sub_total")));
            data.setFoc_qty(cursor.getString(cursor.getColumnIndex("foc_qty")));
            data.setFoc_type(cursor.getString(cursor.getColumnIndex("foc_type")));
            data.setExchange_qty(cursor.getString(cursor.getColumnIndex("exchange_qty")));
            data.setExchange_type(cursor.getString(cursor.getColumnIndex("exchange_type")));
            data.setDiscount(cursor.getString(cursor.getColumnIndex("discount")));
            data.setReturn_qty(cursor.getString(cursor.getColumnIndex("return_qty")));
            data.setReturn_type(cursor.getString(cursor.getColumnIndex("return_type")));
            data.setStockRefNo(cursor.getString(cursor.getColumnIndex("stockrefno")));
            data.setStockQty(cursor.getString(cursor.getColumnIndex("stock_qty")));
            data.setUomCode(cursor.getString(cursor.getColumnIndex("uomcode")));
            data.setMinimumSellingPrice(cursor.getString(cursor.getColumnIndex("minimumsellingprice")));
            data.setStockProductQty(cursor.getString(cursor.getColumnIndex("stock_qtyp")));
            array_list.add(data);
            cursor.moveToNext();
        }
        cursor.close();
        return array_list;
    }

    public void removeAllItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);
        db.close();
    }
    public void removeSettings() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SETTINGS, null, null);
        db.close();
    }

    public void removeAllInvoiceItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CREATE_INVOICE_TABLE, null, null);
        db.close();
    }

    public void removeCustomer() {
        SQLiteDatabase db = this.getWritableDatabase();
        //  db.delete(TABLE_CUSTOMER,null,null);
        db.close();
    }
}