package com.winapp.svcatering.fragments;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.winapp.svcatering.adapter.SchedulingCustomerAdapter;
import com.winapp.svcatering.model.CustomerScheduleModel;
import com.winapp.svcatering.utils.GridSpacingItemDecoration;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.ShedulingAdapter;
import com.winapp.svcatering.model.ScheduleModel;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SchedulingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SchedulingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView scheduleView;
    private ShedulingAdapter shedulingAdapter;
    private SchedulingCustomerAdapter schedulingCustomerAdapter;
    private ArrayList<CustomerScheduleModel> customersList;
    private RecyclerView scheduleCustomerView;
    private ArrayList<ScheduleModel> scheduleList;
    private Button viewCatalog;
    private TextView showCustomer;
    BottomSheetBehavior behavior;
    Button btnCancel;
    HashMap<String ,String> user;
    SessionManager session;
    String companyCode;

    public SchedulingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SchedulingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SchedulingFragment newInstance(String param1, String param2) {
        SchedulingFragment fragment = new SchedulingFragment();
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
        int orientation = this.getResources().getConfiguration().orientation;
        View view = null;
        if (!Utils.isTablet(getActivity())){
            Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            view=inflater.inflate(R.layout.scheduling_portrait, container, false);
        }else {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                view=inflater.inflate(R.layout.scheduling_portrait, container, false);
                // code for portrait mode
            } else {
                // code for landscape mode
                view=inflater.inflate(R.layout.fragment_scheduling, container, false);
            }
        }

        scheduleView=view.findViewById(R.id.root_layout);
        scheduleCustomerView=view.findViewById(R.id.customerView);
        showCustomer=view.findViewById(R.id.show_customer);
        btnCancel=view.findViewById(R.id.btn_cancel);

        CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        viewCatalog=view.findViewById(R.id.btn_catalog);

        getScheduledCustomers();

        viewCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Calendar calendar : calendarView.getSelectedDates()) {
                    System.out.println(calendar.getTime().toString());
                    Toast.makeText(getActivity(),
                            calendar.getTime().toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        calendarView.setOnForwardPageChangeListener(() ->
                Toast.makeText(getActivity(), "Forward", Toast.LENGTH_SHORT).show());

        calendarView.setOnPreviousPageChangeListener(() ->
                Toast.makeText(getActivity(), "Previous", Toast.LENGTH_SHORT).show());

       /// calendarView.setSelectedDates(getSelectedDays());

       // List<EventDay> events = new ArrayList<>();

      //  Calendar cal = Calendar.getInstance();
       // cal.add(Calendar.DAY_OF_MONTH, 7);
       // events.add(new EventDay(cal, R.drawable.sample_four_icons));

       // calendarView.setEvents(events);

        scheduleList=new ArrayList<>();
        ScheduleModel model=new ScheduleModel();
        model.setDay("Friday");
        model.setDate("14");
        model.setYear("July 2020");
        model.setName("Product Company 1");
        model.setTime("12:45 Am");
        model.setStatus("Open");
        scheduleList.add(model);

        model=new ScheduleModel();
        model.setDay("Saturday");
        model.setDate("15");
        model.setYear("July 2020");
        model.setName("Product Company 4");
        model.setTime("12:30 Am");
        model.setStatus("Close");
        scheduleList.add(model);


        model=new ScheduleModel();
        model.setDay("Sunday");
        model.setDate("16");
        model.setYear("July 2020");
        model.setName("Product Company 6");
        model.setTime("12:20 Pm");
        model.setStatus("Close");
        scheduleList.add(model);


        model=new ScheduleModel();
        model.setDay("Sunday");
        model.setDate("17");
        model.setYear("July 2020");
        model.setName("Product Company 19");
        model.setTime("12:20 Am");
        model.setStatus("Open");
        scheduleList.add(model);


        model=new ScheduleModel();
        model.setDay("Monday");
        model.setDate("20");
        model.setYear("Sep 2020");
        model.setName("Product Company 20");
        model.setTime("12:45 Am");
        model.setStatus("Open");
        scheduleList.add(model);

        shedulingAdapter = new ShedulingAdapter(getActivity(), scheduleList, productId -> {
            //showRemoveAlert(productId);

        });
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        scheduleView.setLayoutManager(mLayoutManager);
        scheduleView.addItemDecoration(new GridSpacingItemDecoration(1, GridSpacingItemDecoration.dpToPx(getActivity(),10), true));
        scheduleView.setItemAnimator(new DefaultItemAnimator());
        scheduleView.setVisibility(View.VISIBLE);
        scheduleView.setAdapter(shedulingAdapter);


        View bottomSheet = view.findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);
            }
        });

        showCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCloseBottomSheet();
            }
        });

        return view;
    }

    private List<Calendar> getSelectedDays() {
        List<Calendar> calendars = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Calendar calendar = DateUtils.getCalendar();
            calendar.add(Calendar.DAY_OF_MONTH, i);
            calendars.add(calendar);
        }
        return calendars;
    }

    public void getScheduledCustomers(){

        customersList=new ArrayList<>();
        CustomerScheduleModel model=new CustomerScheduleModel();
        model.setCustomerName("Azmal Trading Company");
        model.setDateTime("Meeting @ 12 PM");
        model.setProducts("Product: Fair and Handsome");
        model.setPaymentAmount("Payment:$450");
        model.setStatus("Open");
        customersList.add(model);

        model=new CustomerScheduleModel();
        model.setCustomerName("Azmal Trading Company");
        model.setDateTime("Meeting @ 12 PM");
        model.setProducts("Product: Fair and Handsome");
        model.setPaymentAmount("Payment:$450");
        model.setStatus("Closed");
        customersList.add(model);


        model=new CustomerScheduleModel();
        model.setCustomerName("Azmal Trading Company");
        model.setDateTime("Meeting @ 12 PM");
        model.setProducts("Product: Fair and Handsome");
        model.setPaymentAmount("Payment:$450");
        model.setStatus("Closed");
        customersList.add(model);


        model=new CustomerScheduleModel();
        model.setCustomerName("Azmal Trading Company");
        model.setDateTime("Meeting @ 12 PM");
        model.setProducts("Product: Fair and Handsome");
        model.setPaymentAmount("Payment:$450");
        model.setStatus("Open");
        customersList.add(model);


        model=new CustomerScheduleModel();
        model.setCustomerName("Azmal Trading Company");
        model.setDateTime("Meeting @ 12 PM");
        model.setProducts("Product: Fair and Handsome");
        model.setPaymentAmount("Payment:$450");
        model.setStatus("Cancel");
        customersList.add(model);

        schedulingCustomerAdapter = new SchedulingCustomerAdapter(getActivity(), customersList, productId -> {
            //showRemoveAlert(productId);

        });
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        scheduleCustomerView.setLayoutManager(mLayoutManager);
        scheduleCustomerView.addItemDecoration(new GridSpacingItemDecoration(1, GridSpacingItemDecoration.dpToPx(getActivity(),10), true));
        scheduleCustomerView.setItemAnimator(new DefaultItemAnimator());
        scheduleCustomerView.setVisibility(View.VISIBLE);
        scheduleCustomerView.setAdapter(schedulingCustomerAdapter);
    }

    public void viewCloseBottomSheet(){
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
}