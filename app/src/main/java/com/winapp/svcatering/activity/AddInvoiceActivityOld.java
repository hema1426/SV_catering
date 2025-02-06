package com.winapp.svcatering.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.winapp.svcatering.R;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.fragments.CustomerFragment;
import com.winapp.svcatering.fragments.ProductFragment;
import com.winapp.svcatering.fragments.SummaryFragment;
import com.winapp.svcatering.model.CartModel;
import com.google.android.material.tabs.TabLayout;
import com.winapp.svcatering.utils.HomeViewPager;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;

import static com.winapp.svcatering.utils.Utils.twoDecimalPoint;

public class AddInvoiceActivityOld extends NavigationActivity {

    private static TabLayout tabLayout;
    public static HomeViewPager viewPager;
    private PagerAdapter tabAdapter;
    public static String customerId;
    public static String activityFrom;
    public Menu menu;
    private static ArrayList<CartModel> localCart;
    public static DBHelper dbHelper;
    public static String editInvoiceNumber="";
    public static String editSoNumber="";
    public static String editSoDate="";
    public static String editDoNumber="";
    public static String editDoDate="";
    public static int selected_pos=0;
    public static String bill_discount="0.00";
    public static String sub_total="0.00";
    public static String item_discount="0.00";
    public ProgressDialog dialog;
    private String companyCode;
    private String userName;
    private LinearLayout customerLayout;
    private LinearLayout productLayout;
    private LinearLayout summaryLayout;
    private View customerView;
    private View productView;
    private View summaryView;
    public static String invoice_remarks="";
    public static String invoice_delivery_date="";
    public static String order_no="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_add_invoice, contentFrameLayout);
        //setContentView(R.layout.activity_add_invoice);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewpager);
        customerLayout=findViewById(R.id.customer_layout);
        productLayout=findViewById(R.id.product_layout);
        summaryLayout=findViewById(R.id.summary_layout);
        summaryView=findViewById(R.id.summary_view);
        productView=findViewById(R.id.product_view);
        customerView=findViewById(R.id.customer_view);

        dbHelper=new DBHelper(this);

        tabLayout.addTab(tabLayout.newTab().setText("CUSTOMER").setIcon(R.drawable.ic_add_user));
        tabLayout.addTab(tabLayout.newTab().setText("PRODUCT").setIcon(R.drawable.ic_delivery_box));
        tabLayout.addTab(tabLayout.newTab().setText("SUMMARY").setIcon(R.drawable.ic_summary));

       // tabAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
       // viewPager.setAdapter(tabAdapter);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if (getIntent()!=null){
            activityFrom=getIntent().getStringExtra("activityFrom");
        }else {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Sales Order");
        }

        session=new SessionManager(this);

        user=session.getUserDetails();
        companyCode=user.get(SessionManager.KEY_COMPANY_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);

        // dbHelper.removeAllProducts();

        assert activityFrom != null;
        switch (activityFrom) {
            case "SalesOrder":
                Objects.requireNonNull(getSupportActionBar()).setTitle("Sales Order");
                break;
            case "InvoiceEdit":
                Objects.requireNonNull(getSupportActionBar()).setTitle("Invoice Edit" + " ( " + getIntent().getStringExtra("invoiceNumber") + " )");
                editInvoiceNumber = getIntent().getStringExtra("invoiceNumber");
                order_no=getIntent().getStringExtra("orderNo");
                break;
            case "SalesEdit":
                Objects.requireNonNull(getSupportActionBar()).setTitle("Sales Edit" + " ( " + getIntent().getStringExtra("soNumber") + " )");
                editSoNumber = getIntent().getStringExtra("soNumber");
                order_no=getIntent().getStringExtra("orderNo");
                break;
            case "ConvertInvoice":
                Objects.requireNonNull(getSupportActionBar()).setTitle("Convert Invoice" + " ( " + getIntent().getStringExtra("soNumber") + " )");
                editSoNumber = getIntent().getStringExtra("soNumber");
                editSoDate=getIntent().getStringExtra("soDate");
                break;
            case "ConvertInvoiceFromDO":
                Objects.requireNonNull(getSupportActionBar()).setTitle("Convert Invoice" + " ( " + getIntent().getStringExtra("doNumber") + " )");
                editDoNumber = getIntent().getStringExtra("doNumber");
                editDoDate=getIntent().getStringExtra("doDate");
                break;
            case "ReOrderSales":
                Objects.requireNonNull(getSupportActionBar()).setTitle("SalesOrder");
                break;
            case "DeliveryOrder":
                Objects.requireNonNull(getSupportActionBar()).setTitle("DeliveryOrder");
                break;
            case "ReOrderInvoice":
                Objects.requireNonNull(getSupportActionBar()).setTitle("Invoice");
                break;
            default:
                Objects.requireNonNull(getSupportActionBar()).setTitle("Invoice");
                break;
        }

        //intent.putExtra("billDiscount",bill_discount);
      //  intent.putExtra("itemDiscount",item_discount);
       // intent.putExtra("subTotal",sub_total);

        try {
            if (getIntent()!=null){
                customerId=getIntent().getStringExtra("customerId");
            }

          /*  if (activityFrom.equals("SalesEdit") || activityFrom.equals("DeliveryOrderEdit")){
                viewPager.setCurrentItem(1);
            }else if (activityFrom.equals("ConvertInvoice")){
                viewPager.setCurrentItem(2);
            }*/

            if (activityFrom.equals("InvoiceEdit")
                    || activityFrom.equals("SalesEdit")
                    || activityFrom.equals("ConvertInvoice")
                    || activityFrom.equals("ReOrderInvoice")
                    || activityFrom.equals("ReOrderSales")
                    || activityFrom.equals("DeliveryOrderEdit")
                    || activityFrom.equals("ConvertInvoiceFromDO")
            ){
                bill_discount=getIntent().getStringExtra("billDiscount");
                item_discount=getIntent().getStringExtra("itemDiscount");
                sub_total=getIntent().getStringExtra("subTotal");
                // viewPager.setCurrentItem(1);
            }

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (viewPager!=null){
                        viewPager.setCurrentItem(tab.getPosition());
                       // Log.w("Tabselected:",tab.getPosition()+"");
                        //setMenuShowHide(tab.getPosition(),tab);
                    }
                }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }
                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    Log.w("TabReselected:",tab.getPosition()+"");
                   // setMenuShowHide(tab.getPosition(),tab);
                }
            });
        }catch (Exception exception){
        }

        if (activityFrom.equals("ConvertInvoice") || activityFrom.equals("ConvertInvoiceFromDO")){
            loadFragment(new SummaryFragment());
            productView.setVisibility(View.GONE);
            customerView.setVisibility(View.GONE);
            summaryView.setVisibility(View.VISIBLE);
            /// invoice_remarks=CustomerFragment.remarksEditText.getText().toString();
            //  invoice_delivery_date=CustomerFragment.dueDateEdittext.getText().toString();
            selected_pos=2;
            invalidateOptionsMenu();

        }else if (activityFrom.equals("SalesEdit") || activityFrom.equals("InvoiceEdit")){
            loadFragment(new ProductFragment());
            productView.setVisibility(View.VISIBLE);
            customerView.setVisibility(View.GONE);
            summaryView.setVisibility(View.GONE);
            // invoice_remarks=CustomerFragment.remarksEditText.getText().toString();
            //  invoice_delivery_date=CustomerFragment.dueDateEdittext.getText().toString();
            selected_pos=1;
            invalidateOptionsMenu();
        }else {
            loadFragment(new CustomerFragment());
            selected_pos=0;
            invalidateOptionsMenu();
        }

       // loadFragment(new CustomerFragment());


        productLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new ProductFragment());
                MenuItem action_save = menu.findItem(R.id.action_save);
                action_save.setVisible(false);
                productView.setVisibility(View.VISIBLE);
                customerView.setVisibility(View.GONE);
                summaryView.setVisibility(View.GONE);
                order_no=CustomerFragment.orderNoText.getText().toString();
                //  invoice_remarks=CustomerFragment.remarksEditText.getText().toString();
              //  invoice_delivery_date=CustomerFragment.dueDateEdittext.getText().toString();
            }
        });


        summaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new SummaryFragment());
                MenuItem action_save = menu.findItem(R.id.action_save);
                action_save.setVisible(true);
                productView.setVisibility(View.GONE);
                customerView.setVisibility(View.GONE);
                summaryView.setVisibility(View.VISIBLE);
               // invoice_remarks=CustomerFragment.remarksEditText.getText().toString();
               // invoice_delivery_date=CustomerFragment.dueDateEdittext.getText().toString();
            }
        });

        customerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuItem action_save = menu.findItem(R.id.action_save);
                action_save.setVisible(false);
                loadFragment(new CustomerFragment());
                productView.setVisibility(View.GONE);
                customerView.setVisibility(View.VISIBLE);
                summaryView.setVisibility(View.GONE);
            }
        });
    }

    public void setMenuShowHide(int pos,TabLayout.Tab tab){
        try {
            selected_pos=pos;
            invalidateOptionsMenu();
        }catch (Exception exception){}

      /*  if (pos!=2){
            if (this.menu!=null){
                MenuItem action_save = menu.findItem(R.id.action_save);
                action_save.setVisible(false);
            }
        }else {
            localCart=new ArrayList<>();
            localCart = dbHelper.getAllCartItems();
            Log.w("LocalCartSize:",localCart.size()+"");
            if (localCart.size()>0){
                if (this.menu!=null){
                    MenuItem action_save = menu.findItem(R.id.action_save);
                    action_save.setVisible(true);
                }
            }else {
                if (this.menu!=null){
                    MenuItem action_save = menu.findItem(R.id.action_save);
                    action_save.setVisible(false);
                }
            }
        }*/
    }

    class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    CustomerFragment tab1 = new CustomerFragment();
                    return tab1;
                case 1:
                    ProductFragment tab2 = new ProductFragment();
                    return tab2;
                case 2:
                    SummaryFragment tab3 = new SummaryFragment();
                    return tab3;
                default:
                    return null;
            }
        }
        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_menu, menu);
        this.menu=menu;
        if (selected_pos!=2){
                MenuItem action_save = menu.findItem(R.id.action_save);
                action_save.setVisible(false);
        }else {
            localCart=new ArrayList<>();
            localCart = dbHelper.getAllCartItems();
            if (localCart.size()>0){
                 MenuItem action_save = menu.findItem(R.id.action_save);
                 action_save.setVisible(true);
            }else {
                MenuItem action_save = menu.findItem(R.id.action_save);
                action_save.setVisible(false);
            }
        }
        return true;
    }

    public void loadFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        try {
            ArrayList<CartModel>  localCart = dbHelper.getAllCartItems();
            double price=0;
            if (localCart.size()>0){
                for (int j = 0;j<localCart.size();j++){
                    if (localCart.get(j).getCART_COLUMN_NET_PRICE() !=null && !localCart.get(j).getCART_COLUMN_NET_PRICE().equals("null")){
                        price+= Double.parseDouble(localCart.get(j).getCART_COLUMN_NET_PRICE());
                    }
                }

                menu.getItem(0).setTitle("Net Amt: $ "+twoDecimalPoint(price));
              /*  if (selected_pos==2){
                    menu.getItem(0).setTitle("Net Amt 1: $ "+twoDecimalPoint(Double.parseDouble(SummaryFragment.netTotalValue)));
                }else {
                    menu.getItem(0).setTitle("Net Amt 2: $ "+twoDecimalPoint(price));
                }*/
            }else {
                menu.getItem(0).setVisible(false);
            }
        }catch (Exception ex){

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            switch (activityFrom) {
                case "SalesOrder":
                    Log.w("ActionFromvalue:",activityFrom);
                    SummaryFragment.showSaveOption("SalesOrder");
                    break;
                case "SalesEdit":
                    Log.w("ActionFromvalue:",activityFrom);
                    SummaryFragment.showSaveOption("SalesEdit");
                    break;
                case "Invoice":
                    Log.w("ActionFromvalue:",activityFrom);
                    SummaryFragment.showSaveOption("Invoice");
                    break;
                case "InvoiceEdit":
                    Log.w("ActionFromvalue:",activityFrom);
                    SummaryFragment.showSaveOption("InvoiceEdit");
                    break;
                case "ConvertInvoice":
                    Log.w("ActionFromvalue:",activityFrom);
                    SummaryFragment.showSaveOption("ConvertInvoice");
                    break;
                case "ConvertInvoiceFromDO":
                    Log.w("ActionFromvalue:",activityFrom);
                    SummaryFragment.showSaveOption("ConvertInvoiceDO");
                    break;
                case "ReOrderSales":
                    SummaryFragment.showSaveOption("ReSalesOrder");
                    break;
                case "DeliveryOrder":
                    SummaryFragment.showSaveOption("DeliveryOrder");
                    break;
                case "ReOrderInvoice":
                    SummaryFragment.showSaveOption("ReInvoice");
                    break;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showSaveAlert(String action, String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AddInvoiceActivityOld.this);
        builder1.setTitle("Information !");
        builder1.setMessage(message);
        builder1.setCancelable(false);
        builder1.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (action.equals("SalesOrder") || action.equals("SalesEdit")){
                            SummaryFragment.createAndValidateJsonObject(1);
                        }else {
                            SummaryFragment.createInvoiceJson(1);
                        }
                    }
                });
        builder1.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        int count=dbHelper.numberOfRows();
        if (count>0){
            showAlert();
        }else {
            finish();
        }
    }

    public static void disableTab(boolean check){
        try {
            for (int i = 0; i < 3; i++){
                Objects.requireNonNull(tabLayout.getTabAt(i)).view.setEnabled(check);
            }
            if (check){
                viewPager.disableScroll(false);
            }else {
                viewPager.disableScroll(true);
            }
            //  Also don't forget to call viewPager.invalidate() method after calling disable scrolling method,
            //  because of that it instantly disable swipe over.
            //viewPager.invalidate();

        }catch (Exception ex){
            Log.w("Disble_movingError:", Objects.requireNonNull(ex.getMessage()));
        }

    }


    public void showAlert(){
        AlertDialog.Builder builder=new AlertDialog.Builder(AddInvoiceActivityOld.this);
        builder.setTitle("Warning..!");
        builder.setMessage("All Products Will be cleared ,Are you sure want to back?");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                activityFrom=getIntent().getStringExtra("activityFrom");
                Log.w("Activity_From:",activityFrom);
                assert activityFrom != null;
                dbHelper.removeAllItems();
                dbHelper.removeCustomer();
                Utils.setOrderNo("");
                Utils.setOrderDate("");
                if (activityFrom.equals("SalesOrder") || activityFrom.equals("SalesEdit") || activityFrom.equals("ConvertInvoice")){
                    Intent intent=new Intent(AddInvoiceActivityOld.this,SalesOrderListActivity.class);
                    startActivity(intent);
                    finish();
                }else if (activityFrom.equals("DeliveryOrder") || activityFrom.equals("ConvertInvoiceFromDO") || activityFrom.equals("DeliveryOrderEdit")){
                    Intent intent=new Intent(AddInvoiceActivityOld.this,DeliveryOrderListActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (activityFrom.equals("InvoiceEdit") || activityFrom.equals("Invoice") ){
                    Intent intent=new Intent(AddInvoiceActivityOld.this,NewInvoiceListActivity.class);
                    startActivity(intent);
                    finish();
                }else if (activityFrom.equals("ReOrderInvoice") || activityFrom.equals("ReOrderSales")){
                    finish();
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                ProductFragment productFragment=new ProductFragment();
                productFragment.getBarcodeValues(ProductFragment.barcodeText.getText().toString().trim());
                return true;
            }
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // false;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onDestroy() {
        Utils.setOrderNo("");
        AddInvoiceActivityOld.order_no="";
        super.onDestroy();
    }
}