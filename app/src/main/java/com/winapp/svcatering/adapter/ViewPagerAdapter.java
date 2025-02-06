package com.winapp.svcatering.adapter;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.winapp.svcatering.fragments.CategoriesTabFragments;
import com.winapp.svcatering.model.AllCategories;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private int noOfItems;
    private ArrayList<AllCategories> allCategoriesList;


    public ViewPagerAdapter(FragmentManager fm, int noOfItems,ArrayList<AllCategories> allCategoriesList) {
        super(fm);
        this.noOfItems = noOfItems;
        this.allCategoriesList=allCategoriesList;
    }

    @Override
    public Fragment getItem(int position) {
        Log.w("DefinedCatagoryCode:",allCategoriesList.get(position).getCategoryCode());
        return CategoriesTabFragments.newInstance(allCategoriesList.get(position).getCategoryCode());
    }

    @Override
    public int getCount() {
        return noOfItems;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return allCategoriesList.get(position).getDescription();
    }
}