package com.winapp.svcatering.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.winapp.svcatering.R;

public class GraphDashboardActivity extends AppCompatActivity implements View.OnClickListener {

    // Define the Variables

    public LinearLayout invoiceLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_graph_dashboard);
        invoiceLayout=findViewById(R.id.invoice_layout);
        invoiceLayout.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {
        Fragment fragment;
        if (view.getId()==R.id.invoice_layout){
            fragment = new InvoiceDashboardFragment();
            redirectFragment(fragment);
        }
    }
    /**
     *
     * ReDirect the fragment as we selected fragment in view..
     * @param fragment
     */
    public void redirectFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, fragment);
        ft.commit();
    }
}