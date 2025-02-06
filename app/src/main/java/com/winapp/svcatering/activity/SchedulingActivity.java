package com.winapp.svcatering.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.SchedulingCustomerAdapter;
import com.winapp.svcatering.adapter.ShedulingAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CustomerDetails;
import com.winapp.svcatering.model.CustomerModel;
import com.winapp.svcatering.model.CustomerScheduleModel;
import com.winapp.svcatering.model.ScheduleModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.GridSpacingItemDecoration;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SchedulingActivity extends NavigationActivity {
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
    EditText customerNameEdittext;
    TextView todayDate;
    ImageView backButton;
    SweetAlertDialog pDialog;
    DBHelper dbHelper;
    ArrayList<CustomerModel> customerList;
    TextView totalCustomers;
    private String companyId;
    SessionManager session;
    HashMap<String ,String> user;
    String locationCode;
    String userName;
    TextView emptyText;
    ArrayList<CustomerDetails> customerDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
 //       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FrameLayout contentFrameLayout =findViewById(R.id.content_frame);
        //Remember this is the FrameLayout area within your activity_main.xml
      //  getLayoutInflater().inflate(R.layout.activity_dashboard, contentFrameLayout);
        if (Utils.isTablet(this)){
            Log.w("This is Tablet","Success");
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Log.w("This is_","LandScape");
              //  setContentView(R.layout.fragment_scheduling);
                getLayoutInflater().inflate(R.layout.fragment_scheduling, contentFrameLayout);
            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                Log.w("This is_","Portrait");
               // setContentView(R.layout.scheduling_portrait);
                getLayoutInflater().inflate(R.layout.scheduling_portrait, contentFrameLayout);
            }
        }else {
            Log.w("This is not tablet","Success");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
          //  setContentView(R.layout.scheduling_portrait);
            getLayoutInflater().inflate(R.layout.scheduling_portrait, contentFrameLayout);
        }
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        dbHelper=new DBHelper(this);
        session=new SessionManager(this);
        user=session.getUserDetails();
        companyId=user.get(SessionManager.KEY_COMPANY_CODE);
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        userName=user.get(SessionManager.KEY_USER_NAME);
        scheduleView=findViewById(R.id.root_layout);
        scheduleCustomerView=findViewById(R.id.customerView);
        showCustomer=findViewById(R.id.show_customer);
        btnCancel=findViewById(R.id.btn_cancel);
        customerNameEdittext=findViewById(R.id.customer_search);
        todayDate=findViewById(R.id.today_date);
        backButton=findViewById(R.id.back);
        CalendarView calendarView =findViewById(R.id.calendarView);
        viewCatalog=findViewById(R.id.view_catalog);
        emptyText=findViewById(R.id.empty_text);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        todayDate.setText(formattedDate);

        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate1 = df1.format(c);

        try {
            getScheduleList(formattedDate1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  for (Calendar calendar : calendarView.getSelectedDates()) {
                    System.out.println(calendar.getTime().toString());
                    Toast.makeText(getApplicationContext(), calendar.getTime().toString(), Toast.LENGTH_SHORT).show();
                }*/
              Intent intent=new Intent(SchedulingActivity.this,MainHomeActivity.class);
              startActivity(intent);
              finish();
            }
        });
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                try {
                    getScheduleList(getDate(eventDay.getCalendar().getTime().toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

      /*  calendarView.setOnForwardPageChangeListener(() ->
                Toast.makeText(getApplicationContext(), "Forward", Toast.LENGTH_SHORT).show());

        calendarView.setOnPreviousPageChangeListener(() ->
                Toast.makeText(getApplicationContext(), "Previous", Toast.LENGTH_SHORT).show());*/




        /// calendarView.setSelectedDates(getSelectedDays());

        // List<EventDay> events = new ArrayList<>();

        //  Calendar cal = Calendar.getInstance();
        // cal.add(Calendar.DAY_OF_MONTH, 7);
        // events.add(new EventDay(cal, R.drawable.sample_four_icons));

        // calendarView.setEvents(events);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        scheduleView.setLayoutManager(mLayoutManager);
        scheduleView.addItemDecoration(new GridSpacingItemDecoration(1, GridSpacingItemDecoration.dpToPx(getApplicationContext(),10), true));
        scheduleView.setItemAnimator(new DefaultItemAnimator());


        RecyclerView.LayoutManager mLayoutManager1 = new GridLayoutManager(getApplicationContext(), 1);
        scheduleCustomerView.setLayoutManager(mLayoutManager1);
        scheduleCustomerView.addItemDecoration(new GridSpacingItemDecoration(1, GridSpacingItemDecoration.dpToPx(getApplicationContext(),10), true));
        scheduleCustomerView.setItemAnimator(new DefaultItemAnimator());


        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
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
               // Intent intent=new Intent(SchedulingActivity.this,MainActivity.class);
              //  startActivity(intent);
              //  finish();
            }
        });

        customerNameEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                String cusname=editable.toString();
                if (!cusname.isEmpty()){
                    filter(cusname);
                }else {
                    if (schedulingCustomerAdapter!=null){
                        schedulingCustomerAdapter.filterList(customersList);
                    }
                }
            }
        });
    }

    public void setCustomerDetails(String customerId){
        SharedPreferences sharedPreferences = getSharedPreferences("customerPref",MODE_PRIVATE);
        SharedPreferences.Editor customerPredEdit= sharedPreferences.edit();
        customerPredEdit.putString("customerId", customerId);
        customerPredEdit.apply();
        Intent intent=new Intent(SchedulingActivity.this,MainHomeActivity.class);
        startActivity(intent);
        finish();
    }


    public String getDate(String eventDay){
        Date date=null;
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
        String temp = eventDay;
        try {
            date = formatter.parse(temp);
            Log.e("formated date ", date + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formateDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
        Log.v("output date ",formateDate);
        return formateDate;
    }

    public void viewCloseBottomSheet(){
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


    public void getScheduleList(String date) throws JSONException {
        // Initialize a new RequestQueue instance

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("CompanyCode",companyId);
        jsonObject.put("LocationCode",locationCode);
        jsonObject.put("UserName",userName);
        jsonObject.put("Date",date);
      //  String url= Utils.getBaseUrl(this) +"MerchandiseApi/GetScheduling_ByDate?Requestdata={CompanyCode:"+companyId+",LocationCode:"+locationCode+",UserName:"+userName+",Date:"+date+"}";
      //  String url=Utils.getBaseUrl(this)+"MerchandiseApi/GetScheduling_ByDate?Requestdata={\"CompanyCode\":\""+companyId+"\",\"LocationCode\":\""+locationCode+"\",\"UserName\":\""+userName+"\",\"Date\":\""+date+"\"}";
        String url=Utils.getBaseUrl(this)+"MerchandiseApi/GetScheduling_ByDate?Requestdata="+jsonObject.toString();

        scheduleList=new ArrayList<>();
        customersList=new ArrayList<>();

        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading Customers...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.w("Response_is:", response.toString());
                        pDialog.dismiss();
                        // Loop through the array elements
                        for (int i = 0; i < response.length(); i++) {
                            // Get current json object
                            JSONObject scheduleObject = response.getJSONObject(i);
                            ScheduleModel model=new ScheduleModel();
                            model.setDay(getDayFromDateString(scheduleObject.optString("ScheduleDateString"),"dd/MM/yyyy"));
                            model.setDate(getDateString(scheduleObject.optString("ScheduleDateString")));
                            model.setYear(getMonth(scheduleObject.optString("ScheduleDateString"))+" "+getYear(scheduleObject.optString("ScheduleDateString")));
                            model.setName(scheduleObject.optString("CustomerName"));
                            if (!scheduleObject.optString("Address1").equals("null")&& !scheduleObject.optString("Address2").equals("null")&&!scheduleObject.optString("Address3").equals("null")){
                                model.setAddress(scheduleObject.optString("Address1")+","+scheduleObject.optString("Address2")+","+scheduleObject.optString("Address3"));
                            }else if (!scheduleObject.optString("Address1").equals("null")&& !scheduleObject.optString("Address2").equals("null")){
                                model.setAddress(scheduleObject.optString("Address1")+","+scheduleObject.optString("Address2"));
                            }else if (!scheduleObject.optString("Address1").equals("null")){
                                model.setAddress(scheduleObject.optString("Address1"));
                            }else {
                                model.setAddress("Address Not found");
                            }
                            model.setSortOrder(scheduleObject.optString("SortOrder"));
                            model.setTime("");
                            model.setStatus("Open");
                            model.setCustomerCode(scheduleObject.optString("CustomerCode"));
                            scheduleList.add(model);

                            CustomerScheduleModel model1=new CustomerScheduleModel();
                            model1.setCustomerName(scheduleObject.optString("CustomerName"));
                            model1.setDateTime("Schedule @ "+scheduleObject.optString("ScheduleDateString"));
                            model1.setCustomerCode(scheduleObject.optString("CustomerCode"));
                           // model1.setProducts("Product: Fair and Handsome");
                           // model1.setPaymentAmount("Payment:$450");
                            model1.setStatus("Open");
                            customersList.add(model1);
                        }
                        if (scheduleList.size()>0){
                            setAdapter();
                            scheduleView.setVisibility(View.VISIBLE);
                            emptyText.setVisibility(View.GONE);
                        }else {
                            emptyText.setVisibility(View.VISIBLE);
                            scheduleView.setVisibility(View.GONE);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();
                    // Do something when error occurred
                    Log.w("Error_throwing:",error.toString());
                }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }
            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }
            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }



    private static String getMonth(String date) throws ParseException{
        Date d = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    private static String getDateString(String date) throws ParseException{
        Date d = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dat = new SimpleDateFormat("dd").format(cal.getTime());
        return dat;
    }

    private static String getYear(String date) throws ParseException{
        Date d = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());
        return year;
    }

    public static String getDayFromDateString(String stringDate,String dateTimeFormat) {
        String[] daysArray = new String[] {"Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"};
        String day = "";

        int dayOfWeek =0;
        //dateTimeFormat = yyyy-MM-dd HH:mm:ss
        SimpleDateFormat formatter = new SimpleDateFormat(dateTimeFormat);
        Date date;
        try {
            date = formatter.parse(stringDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek < 0) {
                dayOfWeek += 7;
            }
            day = daysArray[dayOfWeek];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return day;
    }

    public void setAdapter(){

        if (customersList.size()>0){
            showCustomer.setText(customersList.size()+" Customers");
        }else {
            showCustomer.setVisibility(View.GONE);
        }
        shedulingAdapter = new ShedulingAdapter(getApplicationContext(), scheduleList, customerCode -> {
            //getCustomerDetails(customerCode);
            setCustomerDetails(customerCode);
        });
        scheduleView.setAdapter(shedulingAdapter);

        schedulingCustomerAdapter = new SchedulingCustomerAdapter(getApplicationContext(), customersList, customerCode -> {
           /// getCustomerDetails(customerCode);
            setCustomerDetails(customerCode);
        });
        scheduleCustomerView.setVisibility(View.VISIBLE);
        scheduleCustomerView.setAdapter(schedulingCustomerAdapter);
    }


    private void filter(String text) {
        try {
            //new array list that will hold the filtered data
            ArrayList<CustomerScheduleModel> filterdNames = new ArrayList<>();
            //looping through existing elements
            for (CustomerScheduleModel s : customersList) {
                //if the existing elements contains the search input
                if (s.getCustomerName().toLowerCase().contains(text.toLowerCase())) {
                    //adding the element to filtered list
                    filterdNames.add(s);
                }
            }
            //calling a method of the adapter class and passing the filtered list
            if (schedulingCustomerAdapter!=null){
                schedulingCustomerAdapter.filterList(filterdNames);
            }
        }catch (Exception ex){
            Log.e("Error_in_filter", Objects.requireNonNull(ex.getMessage()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;

         /*   case R.id.action_remove:
                showRemoveAlert();
                break;*/
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //Execute your code here
        Intent intent=new Intent(getApplicationContext(),MainHomeActivity.class);
        startActivity(intent);
        finish();

    }

    private void getCustomerDetails(String customerCode) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Initialize a new JsonArrayRequest instance
        String url=Utils.getBaseUrl(this) +"MasterApi/GetCustomer?Requestdata={\"CustomerCode\": \""+customerCode+"\",\"CompanyCode\": \""+companyId+"\"}";
        Log.w("Given_url:",url);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Getting Customer Details...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try{
                        Log.w("Response_is:",response.toString());
                        if (response.length()>0) {
                            if (response.optString("ErrorMessage").equals("null")){
                                dbHelper.removeCustomer();
                                dbHelper.insertCustomer(
                                        response.optString("CustomerCode"),
                                        response.optString("CustomerName"),
                                        response.optString("PhoneNo"),
                                        response.optString("Address1"),
                                        response.optString("Address2"),
                                        response.optString("Address3"),
                                        response.optString("IsActive"),
                                        response.optString("HaveTax"),
                                        response.optString("TaxType"),
                                        response.optString("TaxPerc"),
                                        response.optString("TaxCode"),
                                        response.optString("CreditLimit"),
                                        response.optString("Country"),
                                        response.optString("CurrencyCode"));
                                customerDetails=dbHelper.getCustomer();
                                Intent intent=new Intent(SchedulingActivity.this,MainHomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(this,"Error in getting response",Toast.LENGTH_LONG).show();
                            }
                        }
                        pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
            // Do something when error occurred
            pDialog.dismiss();
            Log.w("Error_throwing:",error.toString());
            Toast.makeText(this,error.toString(),Toast.LENGTH_LONG).show();
        }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", Constants.API_SECRET_CODE, Constants.API_SECRET_PASSWORD);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }
            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }
            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }
}