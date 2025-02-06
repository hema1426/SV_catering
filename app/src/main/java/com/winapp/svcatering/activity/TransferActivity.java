package com.winapp.svcatering.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.winapp.svcatering.R;
import com.winapp.svcatering.adapter.TransferAdapter;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.LocationModel;
import com.winapp.svcatering.model.TransferModel;
import com.winapp.svcatering.utils.Constants;
import com.winapp.svcatering.utils.SessionManager;
import com.winapp.svcatering.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TransferActivity extends NavigationActivity implements View.OnClickListener {

    private Button transferInButton;
    private Button transferOutButton;
    public RecyclerView transferListView;
    public TransferAdapter transferAdapter;
    public TextView fromLocation;
    public TextView toLocation;
    public ArrayList<TransferModel> transferList;
    public ArrayList<LocationModel> locationList;
    public String [] locations;
    public int checkedItem=-1;
    public String fromLocationCode;
    public String toLocationCode;
    public String fromLocationName="";
    public String toLocationName="";
    public String transferMode;
    public SessionManager session;
    public HashMap<String ,String > user;
    public String locationCode;
    public String islocationPermission;
    public TextView transferDate;
    public Button addProduct;
    public EditText remarkText;
    private int mYear, mMonth, mDay, mHour, mMinute;
    public static String currentDate;
    public ImageView calendar;
    public DBHelper dbHelper;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_transfer_add, contentFrameLayout);
        getSupportActionBar().setTitle("Transfer");
        session=new SessionManager(this);
        dbHelper=new DBHelper(this);
        user=session.getUserDetails();
        locationCode=user.get(SessionManager.KEY_LOCATION_CODE);
        islocationPermission=user.get(SessionManager.IS_LOCATION_PERMISSION);

        fromLocation=findViewById(R.id.from_location);
        toLocation=findViewById(R.id.to_location);
        remarkText=findViewById(R.id.remarks);
        transferDate=findViewById(R.id.transfer_date);
        addProduct=findViewById(R.id.add_product_btn);
        calendar=findViewById(R.id.calendar);

        addProduct.setOnClickListener(this);
        fromLocation.setOnClickListener(this);
        toLocation.setOnClickListener(this);
        transferDate.setOnClickListener(this);
        calendar.setOnClickListener(this);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        transferDate.setText(formattedDate);
        currentDate=convertDate(transferDate.getText().toString());

        if (getIntent()!=null){
            transferMode=getIntent().getStringExtra("transferType");
            getSupportActionBar().setTitle(transferMode);
            if (transferMode.equals("Stock Request")){
                fromLocation.setEnabled(true);
                toLocation.setEnabled(false);
                toLocation.setText(locationCode);
                toLocationCode = locationCode;
            }else {
                if (transferMode.equals("Transfer In")) {
                    if (islocationPermission.equalsIgnoreCase("Y")){
                        fromLocation.setEnabled(true);
                        toLocation.setEnabled(true);
                    }else {
                        fromLocation.setEnabled(true);
                        toLocation.setEnabled(false);
                    }
                    toLocation.setText(locationCode);
                    toLocationCode = locationCode;
                } else {
                    if (islocationPermission.equalsIgnoreCase("Y")){
                        fromLocation.setEnabled(true);
                        toLocation.setEnabled(true);
                    }else {
                        fromLocation.setEnabled(false);
                        toLocation.setEnabled(true);
                    }
                    fromLocation.setText(locationCode);
                    fromLocationCode = locationCode;
                }
            }
        }

        getAllLocations();
    }

    public void onClick(View v) {
        if (v.getId()==R.id.from_location){
            showLocationPopup("Choose a From Location",fromLocation,"from");
        }else if (v.getId()==R.id.to_location){
            showLocationPopup("Choose a To Location",toLocation,"to");
        }else if (v.getId()==R.id.add_product_btn){
            validateDetails();
        }else if (v.getId()==R.id.transfer_date){
            getDate(transferDate);
        }else if (v.getId()==R.id.calendar){
            getDate(transferDate);
        }
    }

    public void validateDetails(){
        if (fromLocationCode!=null && !fromLocationCode.isEmpty() && toLocationCode!=null && !toLocationCode.isEmpty()){
            String remarks="";
            if (remarkText.getText().toString().isEmpty()){
                remarks=remarkText.getText().toString();
            }
            if (!fromLocationCode.equals(toLocationCode)){
                Intent intent=new Intent(getApplicationContext(),TransferProductAddActivity.class);
                intent.putExtra("fromLocationCode",fromLocationCode);
                intent.putExtra("toLocationCode",toLocationCode);
                intent.putExtra("transferType",transferMode);
                intent.putExtra("currentDate",transferDate.getText().toString());
                intent.putExtra("remarks",remarks);
                startActivity(intent);
            }else {
                Toast.makeText(getApplicationContext(),"From & To Location should not be same..!",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Choose the Location",Toast.LENGTH_SHORT).show();
        }
    }

    public void getAllLocations(){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url=Utils.getBaseUrl(this) +"WarehouseList";
        locationList=new ArrayList<>();
        Log.w("Given_url:",url);
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Loading Locations List...");
        dialog.setCancelable(false);
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null,
                response -> {
                    try {
                        dialog.dismiss();
                        Log.w("Response_TRANSFER:", response.toString());
                        String statusCode=response.optString("statusCode");
                        String statusMessage=response.optString("statusMessage");
                        if (statusCode.equals("1")){
                            JSONArray locationArray =response.optJSONArray("responseData");
                            assert locationArray != null;
                            locations=new String[locationArray.length()];
                            for (int i = 0; i< locationArray.length(); i++){
                                JSONObject object= locationArray.optJSONObject(i);
                                LocationModel model=new LocationModel();
                                model.setLocationCode(object.optString("whsCode"));
                                model.setLocationName(object.optString("whsName"));
                                locations[i]=object.optString("whsName")+" ( "+object.optString("whsCode")+" ) ";
                                locationList.add(model);
                            }
                            if (locationList.size()>0){

                            }else {
                                Toast.makeText(getApplicationContext(),"No Location found...",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),statusMessage,Toast.LENGTH_LONG).show();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    dialog.dismiss();
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

    public void showLocationPopup(String title,TextView locationText,String location){
        // AlertDialog builder instance to build the alert dialog
        ListView list;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        // title of the alert dialog
        alertDialog.setTitle(title);
        final String[] listItems = locations;
        // the function setSingleChoiceItems is the function which builds
        // the alert dialog with the single item selection
        alertDialog.setSingleChoiceItems(listItems, checkedItem, new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItem = which;
                locationText.setText(listItems[which]);
                if (location.equals("from")){
                    fromLocationCode=locationList.get(which).getLocationCode();
                    fromLocationName=locationList.get(which).getLocationName();
                }else {
                    toLocationCode=locationList.get(which).getLocationCode();
                    toLocationName=locationList.get(which).getLocationName();
                }
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // create and build the AlertDialog instance
        // with the AlertDialog builder instance
        AlertDialog customAlertDialog = alertDialog.create();
        // show the alert dialog when the button is clicked
        customAlertDialog.show();

     /*   list = customAlertDialog.getListView();
        final ListAdapter adaptor = customAlertDialog.getListView().getAdapter();
        for (int i = 0; i < listItems.length; i++) { // index
            if (i % 2 == 0) {
                // Disable choice in dialog
                adaptor.getView(i, null, list).setEnabled(false);
            } else {
                // Enable choice in dialog
                adaptor.getView(i, null, list).setEnabled(true);
            }
        }*/
    }

    public void getDate(TextView dateEditext){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateEditext.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        currentDate=convertDate(dateEditext.getText().toString());
                        Log.w("CurrentDateView:",currentDate);
                    }
                }, mYear, mMonth, mDay);
        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String convertDate(String strDate) {
        @SuppressLint("SimpleDateFormat")
        DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
        @SuppressLint("SimpleDateFormat")
        DateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
        String resultDate = "";
        try {
            resultDate=outputFormat.format(inputFormat.parse(strDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultDate;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            int count=dbHelper.numberOfRowsInInvoice();
            if (count>0){
                showDeleteAlert();
            }else {
                finish();
            }
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_HOME){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void showDeleteAlert(){
        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(TransferActivity.this);
        builder1.setMessage("Data Will be Cleared are you sure want to back?");
        builder1.setCancelable(false);
        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbHelper.removeAllInvoiceItems();
                        dbHelper.removeAllReturn();
                        finish();
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        androidx.appcompat.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}