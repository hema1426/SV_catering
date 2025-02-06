package com.winapp.svcatering.googlemap;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.winapp.svcatering.R;
import com.winapp.svcatering.db.DBHelper;
import com.winapp.svcatering.model.CustomerDetails;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton mapStyleButton;
    private LatLng mOrigin;
    private LatLng mDestination;
    private Polyline mPolyline;
    private ArrayList<LatLng> mMarkerPoints;
    private SupportMapFragment mapFragment;
    private String customername;
    private String outstanding;
    private String address;
    private String latitude;
    private String longitude;
    private ArrayList<CustomersMarkerInfo> customersMarkerInfoList;
    List<MarkerOptions> markerList;
    Button btnCall;
    Button btnTrack;
    public static int REQUEST_PHONE_CALL=123;

    private TextView customerName;
    private TextView customerAddress;
    private TextView customerPhoneNo;
    private TextView customerLocationKm;
    private String  customer_id;
    private DBHelper dbHelper;
    private ArrayList<CustomerDetails> customerDetails;
    private TextView outstandingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapStyleButton=findViewById(R.id.map_option);
        dbHelper=new DBHelper(this);
        customerDetails=new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mMarkerPoints = new ArrayList<>();
        btnCall=findViewById(R.id.btn_call);
        btnTrack=findViewById(R.id.btn_track);
        customerName=findViewById(R.id.customer_name_value);
        customerAddress=findViewById(R.id.customer_address);
        customerPhoneNo=findViewById(R.id.phone_no);
        customerLocationKm=findViewById(R.id.location_km);
        outstandingText=findViewById(R.id.outstanding_amount);

        if (getIntent() !=null){
            customer_id=getIntent().getStringExtra("customerId");
            customername=getIntent().getStringExtra("customerName");
            outstanding=getIntent().getStringExtra("outstanding");
            address=getIntent().getStringExtra("address");
            latitude=getIntent().getStringExtra("latitude");
            longitude=getIntent().getStringExtra("longitude");
        }

        customerDetails=dbHelper.getCustomer(customer_id);

        customerName.setText(customerDetails.get(0).getCustomerName());
        if (customerDetails.get(0).getCustomerAddress1()!=null && !customerDetails.get(0).getCustomerAddress1().isEmpty()){
            customerAddress.setText(customerDetails.get(0).getCustomerAddress1());
        }else {
            customerAddress.setText("No Address Found");
        }
        if (customerDetails.get(0).getPhoneNo()!=null && !customerDetails.get(0).getPhoneNo().isEmpty()){
            customerPhoneNo.setText(customerDetails.get(0).getPhoneNo());
        }else {
            customerPhoneNo.setText("Not found");
        }

        customerLocationKm.setText("Not found");
        outstandingText.setText("Outstanding : "+outstanding);

        mapStyleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View menuItemView = findViewById(R.id.map_option);
                PopupMenu popupMenu = new PopupMenu(MapsActivity.this, menuItemView);
                popupMenu.getMenuInflater().inflate(R.menu.map_style, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_satellite:
                               // mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                return true;
                            case R.id.action_roadmap:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                return true;
                            case R.id.action_hybrid:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                return true;
                            case R.id.action_terrain:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MapsActivity.this, MapBoxActivity.class);
                intent.putExtra("customerName","232424");
                intent.putExtra("outstanding","1355.600");
                intent.putExtra("address","13,North Street , T.Nagar, Chennai..");
                intent.putExtra("latitude","9.939093");
                intent.putExtra("longitude","78.121719");
                startActivity(intent);
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPhoneNumber("+65 4532233");
            }
        });
    }

    // Calling the Customer
    public void callToCustomer(String mobileNo){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+mobileNo));
       /* if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }*/
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        }
        else {
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber("+65 4532233");
            }
        }
    }
    public void callPhoneNumber(String mobileNumber) {
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                            Manifest.permission.CALL_PHONE}, 101);
                    return;
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + mobileNumber));
                startActivity(callIntent);
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + mobileNumber));
                startActivity(callIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        markerList=new ArrayList<>();
     /*   boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        if (!success) {
            Log.e("TAG", "Style parsing failed.");
        }*/

     /*   LatLng customerPoint = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(customerPoint)
                .title(customername)
                .snippet("Snoqualmie Falls is located 25 miles east of Seattle.")
                .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE));

        InfoWindowData info = new InfoWindowData();
        info.setImage("snowqualmie");
        info.setOutstanding("Outstanding : "+outstanding);
        info.setAddress("Address : "+address);
        info.setTransport("Reach the site by bus, car and train.");

        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);
        mMap.setInfoWindowAdapter(customInfoWindow);

        Marker m = mMap.addMarker(markerOptions);
        m.setTag(info);
        m.showInfoWindow();*/

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(customerPoint));


        customersMarkerInfoList=new ArrayList<>();
        CustomersMarkerInfo info=new CustomersMarkerInfo();
        info.setLatitude(9.939093);
        info.setLongiude(78.121719);
        info.setTitle("AZMAL TRADERS");
        info.setSnippet("Food");
        customersMarkerInfoList.add(info);

        info=new CustomersMarkerInfo();
        info.setLatitude(13.067439);
        info.setLongiude(80.237617);
        info.setTitle("Murugan Traders");
        info.setSnippet("Fruits Shop");
        customersMarkerInfoList.add(info);

        info=new CustomersMarkerInfo();
        info.setLatitude(11.004556);
        info.setLongiude(76.961632);
        info.setTitle("HUA Plastics");
        info.setSnippet("Plastics Shop");
        customersMarkerInfoList.add(info);

        info=new CustomersMarkerInfo();
        info.setLatitude(10.775632);
        info.setLongiude(78.692360);
        info.setTitle("Trichy Shops");
        info.setSnippet("Plastics Shop");
        customersMarkerInfoList.add(info);

        for (CustomersMarkerInfo markerInfo:customersMarkerInfoList){
            drawMarker(markerInfo.getLatitude(),markerInfo.getLongiude(),markerInfo.getTitle(),markerInfo.getSnippet());
        }

        showAllMarkers();
    }

    private void drawMarker(double latitude,double longitude,String title,String snippet){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(latitude,longitude))
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_ORANGE));
        // Adding marker on the Google Map
        mMap.addMarker(markerOptions);
        markerList.add(markerOptions);
    }

    public void showAllMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions m : markerList) {
            builder.include(m.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30);
        // Zoom and animate the google map to show all markers
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
    }

    class CustomersMarkerInfo{
        double latitude;
        double longiude;
        String title;
        String snippet;
        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongiude() {
            return longiude;
        }

        public void setLongiude(double longiude) {
            this.longiude = longiude;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSnippet() {
            return snippet;
        }

        public void setSnippet(String snippet) {
            this.snippet = snippet;
        }
    }
}