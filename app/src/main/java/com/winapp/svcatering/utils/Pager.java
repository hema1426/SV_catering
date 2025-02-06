package com.winapp.svcatering.utils;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.winapp.svcatering.fragments.AllInvoices;
import com.winapp.svcatering.fragments.PaidInvoices;
import com.winapp.svcatering.fragments.UnpaidInvoices;

//Extending FragmentStatePagerAdapter
public class Pager extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;

    //Constructor to the class
    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                AllInvoices tab1 = new AllInvoices();
                return tab1;
            case 1:
                PaidInvoices tab2 = new PaidInvoices();
                return tab2;
            case 2:
                UnpaidInvoices tab3 = new UnpaidInvoices();
                return tab3;
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}
