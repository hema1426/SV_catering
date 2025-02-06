package com.winapp.svcatering.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.winapp.svcatering.utils.GridSpacingItemDecoration;
import com.winapp.svcatering.R;
import com.winapp.svcatering.utils.Utils;
import com.winapp.svcatering.adapter.DashboardAdapter;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView dashboardView;
    private DashboardAdapter dashboardAdapter;
    private ArrayList<String> titleList;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View view=inflater.inflate(R.layout.fragment_dashboard, container, false);
        dashboardView=view.findViewById(R.id.root_layout);
        titleList=new ArrayList<>();
        titleList.add("Catalog");
        titleList.add("Sales");
        titleList.add("Invoice");
        titleList.add("Receipt");
        titleList.add("Delivery Order");
        titleList.add("Sales Return");
        titleList.add("Product");
        titleList.add("Goods Receive");
        titleList.add("Settings");

        try {
            dashboardAdapter = new DashboardAdapter(getActivity(), titleList);
            int mNoOfColumns = Utils.calculateNoOfColumns(getActivity(),200);
            // GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, mNoOfColumns);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), mNoOfColumns);
            dashboardView.setLayoutManager(mLayoutManager);
            // categoriesView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            dashboardView.addItemDecoration(new GridSpacingItemDecoration(2, GridSpacingItemDecoration.dpToPx(getActivity(),2), true));
            dashboardView.setItemAnimator(new DefaultItemAnimator());
            dashboardView.setAdapter(dashboardAdapter);
            dashboardView.setVisibility(View.VISIBLE);
        }catch (Exception ex){
            Log.e("TAG","Error in Populating the data:"+ex.getMessage());
        }
        return view;
    }
}