package com.winapp.svcatering.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.R;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static TextView textCartTotal;
    NavigationView mNavigationView;
    private int mCurrentSelectedPosition = 0;
    static TextView textCartItemCount;
    static int mCartItemCount=0;
    public static  String customerId="empty";
    DBHelper helper;
    SessionManager session;
    HashMap<String ,String > user;
    String username;
    String companyname;
    private long lastBackPressTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Utils.isTablet(this)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper=new DBHelper(this);
        session=new SessionManager(this);
        user=session.getUserDetails();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (getIntent().getStringExtra("customer_name")!=null){
            customerId =getIntent().getStringExtra("customer_id");
//            HomeFragment.selectCustomer.setText(customerName);
        }

        mCartItemCount= helper.numberOfRows();
        setupBadge();

        //setFragment(new HomeFragment());

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setItemIconTintList(null);

        try {
            View headerView = mNavigationView.getHeaderView(0);
            // get user name and email textViews
            TextView userName = headerView.findViewById(R.id.navigation_username);
            TextView companyName = headerView.findViewById(R.id.navigation_company_name);
            // set user name and email
            userName.setText(user.get(SessionManager.KEY_USER_NAME));
            companyName.setText(user.get(SessionManager.KEY_COMPANY_NAME));
        }catch (Exception ex){

        }


        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_home:
                       // setFragment(new HomeFragment());
                        startActivity(getIntent());
                        finish();
                        closeDrawer();
                        mCurrentSelectedPosition = 0;
                        return true;
                    case R.id.navigation_item_schedule:
                       // setFragment(new SchedulingFragment());
                        Intent intent1=new Intent(MainActivity.this,SchedulingActivity.class);
                        startActivity(intent1);
                        closeDrawer();
                        mCurrentSelectedPosition = 1;
                        return true;
                    case R.id.navigation_item_dashboard:
                        //setFragment(new DashboardFragment());
                        Intent intent4=new Intent(MainActivity.this,DashboardActivity.class);
                        startActivity(intent4);
                        mCurrentSelectedPosition = 2;
                        closeDrawer();
                        return true;
                    case R.id.navigation_item_invoice:
                        //setFragment(new TableListFragment());
                        Intent intent=new Intent(MainActivity.this,NewInvoiceListActivity.class);
                        startActivity(intent);
                        mCurrentSelectedPosition = 3;
                        closeDrawer();
                        return true;
                  /*  case R.id.navigation_item_cart:
                        Intent intent2=new Intent(MainActivity.this,CartActivity.class);
                        startActivity(intent2);
                        mCurrentSelectedPosition = 4;
                        closeDrawer();
                        return true;*/
                    case R.id.navigation_item_salesorder:
                        Intent intent3=new Intent(MainActivity.this,SalesOrderListActivity.class);
                        startActivity(intent3);
                        mCurrentSelectedPosition = 5;
                        closeDrawer();
                        return true;
                    case R.id.navigation_item_graphical:
                        Intent intent5=new Intent(MainActivity.this,MainDashboardActivity.class);
                        startActivity(intent5);
                        mCurrentSelectedPosition = 5;
                        closeDrawer();
                        return true;
                    default:
                        return true;
                }
            }
        });
       // onBackPressed();

        //add this line to display menu1 when the activity is loaded
      //  displaySelectedScreen(R.id.nav_menu1);
    }

    /*@Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/

    public void closeDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        View actionView = menuItem.getActionView();
        textCartItemCount = actionView.findViewById(R.id.cart_badge);
        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
                Intent intent=new Intent(getApplicationContext(),CartActivity.class);
                startActivity(intent);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
           // setFragment(new SchedulingFragment());

            String a="1011";

            return true;
        }else if (id==R.id.action_search){
            Intent intent=new Intent(MainActivity.this,SearchProductActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int itemId) {
        //initializing the fragment object which is selected
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem myItem = menu.findItem(R.id.action_total);
        myItem.setTitle("Total: 100");

     //   final MenuItem menuItem = menu.findItem(R.id.action_total);
    //    View actionView = menuItem.getActionView();
//        textCartTotal = actionView.findViewById(R.id.net_total);
     //   textCartTotal.setText("Total:100");

        return true;
    }

  /*  // Replace the fragments
    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(content_frame, fragment);
        fragmentTransaction.commit();
    }
*/
    private static void setupBadge() {
        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
       /* if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            Snackbar snackbar = Snackbar
                    .make(drawer, "Click BACK again to exit", Snackbar.LENGTH_LONG);
            snackbar.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
            Intent a=new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            finishAffinity();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

   // ((FragmentName) getFragmentManager().findFragmentById(R.id.fragment_id)).methodName();

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
           /// ((ProductsFragment) getFragmentManager().findFragmentById(content_frame)).methodName();
          //  getAllProducts(String.valueOf(pageNo));
            Log.d("Daiya", "ORIENTATION_LANDSCAPE");

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
          //  getAllProducts(String.valueOf(pageNo));
            Log.d("Daiya", "ORIENTATION_PORTRAIT");
        }
    }


}
