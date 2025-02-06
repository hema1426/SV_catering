//package com.winapp.svcatering.activity;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.net.ConnectivityManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.TextView;
//
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.drawerlayout.widget.DrawerLayout;
//
//import com.google.android.material.navigation.NavigationView;
//import com.google.android.material.snackbar.Snackbar;
//import com.winapp.svcatering.R;
//import com.winapp.svcatering.dashboard.GraphDashboardActivity;
//import com.winapp.svcatering.db.DBHelper;
//import com.winapp.svcatering.model.UserRoll;
//import com.winapp.svcatering.receipts.ReceiptsListActivity;
//import com.winapp.svcatering.salesreturn.NewSalesReturnListActivity;
//import com.winapp.svcatering.utils.NetworkChangeReceiver;
//import com.winapp.svcatering.utils.SessionManager;
//import com.winapp.svcatering.utils.Utils;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//public class NavigationActivityCopySAP extends AppCompatActivity {
//
//    DrawerLayout drawerLayout;
//    ActionBarDrawerToggle actionBarDrawerToggle;
//    Toolbar toolbar;
//    NavigationView mNavigationView;
//    private int mCurrentSelectedPosition = 0;
//    static DBHelper helper;
//    SessionManager session;
//    HashMap<String ,String > user;
//    private long lastBackPressTime = 0;
//    private SharedPreferences loginPreferences;
//    private SharedPreferences.Editor loginPrefsEditor;
//    NetworkChangeReceiver networkChangeReceiver;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_navigation);
//        mNavigationView = findViewById(R.id.nav_view);
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        Log.w("activity_cg",getClass().getSimpleName().toString());
//
//        networkChangeReceiver=new NetworkChangeReceiver();
//        drawerLayout = findViewById(R.id.drawer_layout);
//        // Set the Preference value in edittext for Remembering the values
//        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
//        loginPrefsEditor = loginPreferences.edit();
//
//        helper=new DBHelper(this);
//        session=new SessionManager(this);
//        user=session.getUserDetails();
//        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawerLayout.setDrawerListener(actionBarDrawerToggle);
//        mNavigationView.setItemIconTintList(null);
//
//        try {
//            View headerView = mNavigationView.getHeaderView(0);
//            // get user name and email textViews
//            TextView userName = headerView.findViewById(R.id.navigation_username);
//            TextView companyName = headerView.findViewById(R.id.navigation_company_name);
//            // set user name and email
//            userName.setText(user.get(SessionManager.KEY_USER_NAME));
//            companyName.setText(user.get(SessionManager.KEY_COMPANY_NAME));
//        }catch (Exception ex){
//
//        }
//
//        ArrayList<UserRoll> userRolls=helper.getUserPermissions();
//        Menu menu = mNavigationView.getMenu();
//        MenuItem home = menu.findItem(R.id.navigation_item_home);
//        MenuItem schedule = menu.findItem(R.id.navigation_item_schedule);
//        MenuItem catalog=menu.findItem(R.id.navigation_item_catalog);
//        MenuItem allcatagories=menu.findItem(R.id.navigation_item_catagories);
//        MenuItem customers=menu.findItem(R.id.navigation_item_customer);
//        MenuItem salesorder=menu.findItem(R.id.navigation_item_salesorder);
//        MenuItem invoice=menu.findItem(R.id.navigation_item_invoice);
//        MenuItem receipts=menu.findItem(R.id.navigation_item_receipts);
//        MenuItem settings=menu.findItem(R.id.navigation_item_settings);
//        MenuItem salesreturn=menu.findItem(R.id.navigation_item_sales_return);
//
//        if (userRolls.size()>0){
//            for (UserRoll roll:userRolls){
//                switch (roll.getFormName()) {
//                    case "Catalog":
//                        if (roll.getHavePermission().equals("true")) {
//                            catalog.setVisible(true);
//                        } else {
//                            catalog.setVisible(false);
//                        }
//                        break;
//                    case "Merchandise Schedule":
//                        if (roll.getHavePermission().equals("true")) {
//                            schedule.setVisible(true);
//                        } else {
//                            schedule.setVisible(false);
//                        }
//                        break;
//                    case "Customer List":
//                        if (roll.getHavePermission().equals("true")) {
//                            customers.setVisible(true);
//                        } else {
//                            customers.setVisible(false);
//                        }
//                        break;
//                    case "Sales Order":
//                        if (roll.getHavePermission().equals("true")) {
//                            salesorder.setVisible(true);
//                        } else {
//                            salesorder.setVisible(false);
//                        }
//                        break;
//                    case "Invoice":
//                        if (roll.getHavePermission().equals("true")) {
//                            invoice.setVisible(true);
//                        } else {
//                            invoice.setVisible(false);
//                        }
//                        break;
//                    case "Receipts":
//                        if (roll.getHavePermission().equals("true")) {
//                            receipts.setVisible(true);
//                        } else {
//                            receipts.setVisible(false);
//                        }
//                        break;
//                    case "Settings":
//                        if (roll.getHavePermission().equals("true")) {
//                            settings.setVisible(true);
//                        } else {
//                            settings.setVisible(false);
//                        }
//                        break;
//                    case "Sales Return":
//                        if (roll.getHavePermission().equals("true")){
//                            salesreturn.setVisible(true);
//                        }else {
//                            salesreturn.setVisible(false);
//                        }
//                        break;
//                }
//            }
//        }
//       // target.setVisible(false);
//
//        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                menuItem.setChecked(true);
//                int itemId = menuItem.getItemId();
//                if (itemId == R.id.navigation_item_home) {// setFragment(new HomeFragment());
//                    Intent intent = new Intent(NavigationActivity.this, DashboardActivity.class);
//                    startActivity(intent);
//                    drawerLayout.closeDrawers();
//                    // mCurrentSelectedPosition = 0;
//                    return true;
//                } else if (itemId == R.id.navigation_item_schedule) {
//                    Intent intent;// setFragment(new SchedulingFragment());
//                    intent = new Intent(NavigationActivity.this, SchedulingActivity.class);
//                    startActivity(intent);
//                    drawerLayout.closeDrawers();
//                    // mCurrentSelectedPosition = 1;
//                    return true;
//                } else if (itemId == R.id.navigation_item_catalog) {
//                    Intent intent;//setFragment(new DashboardFragment());
//                    intent = new Intent(NavigationActivity.this, MainHomeActivity.class);
//                    startActivity(intent);
//                    // mCurrentSelectedPosition = 2;
//                    drawerLayout.closeDrawers();
//                    return true;
//                } else if (itemId == R.id.navigation_item_invoice) {
//                    Intent intent;//setFragment(new TableListFragment());
//                    intent = new Intent(NavigationActivity.this, NewInvoiceListActivity.class);
//                    startActivity(intent);
//                    // mCurrentSelectedPosition = 3;
//                    drawerLayout.closeDrawers();
//                    return true;
//                } else if (itemId == R.id.navigation_item_settings) {
//                    Intent intent;
//                    intent = new Intent(NavigationActivity.this, SettingActivity.class);
//                    startActivity(intent);
//                    // mCurrentSelectedPosition = 4;
//                    drawerLayout.closeDrawers();
//                    return true;
//                } else if (itemId == R.id.navigation_item_salesorder) {
//                    Intent intent;
//                    intent = new Intent(NavigationActivity.this, SalesOrderListActivity.class);
//                    startActivity(intent);
//                    // mCurrentSelectedPosition = 5;
//                    drawerLayout.closeDrawers();
//                    return true;
//                } else if (itemId == R.id.navigation_item_dashboard) {
//                    Intent intent;
//                    intent = new Intent(NavigationActivity.this, GraphDashboardActivity.class);
//                    startActivity(intent);
//                    // mCurrentSelectedPosition = 6;
//                    drawerLayout.closeDrawers();
//                    return true;
//                } else if (itemId == R.id.navigation_item_customer) {
//                    Intent intent1 = new Intent(NavigationActivity.this, CustomerListActivity.class);
//                    intent1.putExtra("from", "cus");
//                    intent1.putExtra("Message", "Open");
//                    startActivity(intent1);
//                    //mCurrentSelectedPosition = 7;
//
//                    drawerLayout.closeDrawers();
//                    return true;
//                } else if (itemId == R.id.navigation_item_receipts) {
//                    Intent intent;
//                    intent = new Intent(NavigationActivity.this, ReceiptsListActivity.class);
//                    startActivity(intent);
//                    // mCurrentSelectedPosition=9;
//                    drawerLayout.closeDrawers();
//                    return true;
//                } else if (itemId == R.id.navigation_item_catagories) {
//                    Intent intent;
//                    intent = new Intent(NavigationActivity.this, CategoriesActivity.class);
//                    startActivity(intent);
//                    // mCurrentSelectedPosition=8;
//                    drawerLayout.closeDrawers();
//                    return true;
//                } else if (itemId == R.id.navigation_item_sales_return) {
//                    Intent intent;/*  intent=new Intent(NavigationActivity.this, SalesReturnActivity.class);
//                        startActivity(intent);
//                        drawerLayout.closeDrawers();*/
//                    intent = new Intent(NavigationActivity.this, NewSalesReturnListActivity.class);
//                    startActivity(intent);
//                    drawerLayout.closeDrawers();
//                    return true;
//                } else if (itemId == R.id.navigation_product_analysis) {
//                    Intent intent;
//                    intent = new Intent(NavigationActivity.this, ProductStockAnalysisActivity.class);
//                    startActivity(intent);
//                    drawerLayout.closeDrawers();
//                    return true;
//                } else if (itemId == R.id.navigation_settlement) {
//                    Intent intent;
//                    intent = new Intent(NavigationActivity.this, SettlementListActivity.class);
//                    startActivity(intent);
//                    drawerLayout.closeDrawers();
//                    return true;
//                } else if (itemId == R.id.navigation_reports) {
//                    Intent intent;
//                    intent = new Intent(NavigationActivity.this, ReportsActivity.class);
//                    startActivity(intent);
//                    drawerLayout.closeDrawers();
//                    return true;
//                } else if (itemId == R.id.navigation_transfer) {
//                    Intent intent;
//                    intent = new Intent(NavigationActivity.this, TransferListProductActivity.class);
//                    intent.putExtra("docNum", "");
//                    intent.putExtra("transferType", "");
//                    startActivity(intent);
//                    drawerLayout.closeDrawers();
//                    return true;
//                } else if (itemId == R.id.navigation_stock_request) {
//                    Intent intent;
//                    intent = new Intent(NavigationActivity.this, StockRequestListActivity.class);
//                    intent.putExtra("docNum", "");
//                    intent.putExtra("transferType", "");
//                    startActivity(intent);
//                    drawerLayout.closeDrawers();
//                    return true;
//                }else if (itemId == R.id.navigation_expense) {
//                    Intent intent;
//                    intent = new Intent(NavigationActivity.this, NewExpenseModuleListActivity.class);
//                    intent.putExtra("docNum", "");
//                    intent.putExtra("transferType", "");
//                    startActivity(intent);
//                    drawerLayout.closeDrawers();
//                    return true;
//                }
//
//                else if (itemId == R.id.navigation_item_signout) {
//                    showSignoutAlert();
//                    drawerLayout.closeDrawers();
//                    return true;
//                }
//                return true;
//            }
//        });
//    }
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        actionBarDrawerToggle.syncState();
//    }
//
//    public void showSignoutAlert(){
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(NavigationActivity.this);
//        alertDialog.setTitle("Warning..!");
//        alertDialog.setMessage("Are you sure want to Logout this Session?");
//        alertDialog.setCancelable(false);
//        alertDialog.setPositiveButton(
//                "YES",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
////                        loginPrefsEditor.clear();
////                        loginPrefsEditor.commit();
//                        Utils.clearCustomerSession(NavigationActivity.this);
//                        helper.removeAllItems();
//                       // helper.removeSettings();
//                        session.logoutUser();
//
//                        dialog.cancel();
//                    }
//                });
//        alertDialog.setNegativeButton("NO",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert11 = alertDialog.create();
//        alert11.show();
//    }
//
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//       /* if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }*/
//        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
//            Snackbar snackbar = Snackbar
//                    .make(drawer, "Click BACK again to exit", Snackbar.LENGTH_LONG);
//            snackbar.show();
//            this.lastBackPressTime = System.currentTimeMillis();
//        } else {
//            showCloseAlert();
//           // super.onBackPressed();
//          /*  Intent a = new Intent(Intent.ACTION_MAIN);
//            a.addCategory(Intent.CATEGORY_HOME);
//            a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(a);
//            finishAffinity();
//            android.os.Process.killProcess(android.os.Process.myPid());*/
//        }
//    }
//    @Override
//    protected void onStart(){
//        super.onStart();
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(networkChangeReceiver, filter);
//    }
//    @Override
//    protected void onStop(){
//        super.onStop();
//        unregisterReceiver(networkChangeReceiver);
//    }
//
//    public void showCloseAlert(){
//        AlertDialog.Builder builder=new AlertDialog.Builder(NavigationActivity.this);
//        builder.setCancelable(false);
//        builder.setTitle("Warning..!");
//        builder.setMessage("Are You sure want to exit the App?");
//        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Intent a = new Intent(Intent.ACTION_MAIN);
//                a.addCategory(Intent.CATEGORY_HOME);
//                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(a);
//                finishAffinity();
//                android.os.Process.killProcess(android.os.Process.myPid());
//            }
//        });
//        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
//        AlertDialog alertDialog=builder.create();
//        alertDialog.show();
//    }
//}