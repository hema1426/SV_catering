package com.winapp.svcatering.googlemap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.winapp.svcatering.R;


//import timber.log.Timber;

//import static com.mapbox.core.constants.Constants.PRECISION_6;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

/**
 * Use Mapbox Java Services to request directions from the Mapbox Directions API and show the
 * route with a LineLayer.
 */
public class MapBoxActivity extends AppCompatActivity
//        implements OnMapReadyCallback
{

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
//    private MapView mapView;
//    private DirectionsRoute currentRoute;
//    private MapboxDirections client;
//    private Point origin;
//    private Point destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_map_box_mp_main);
//        mapView = findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);
    }
//    @Override
//    public void onMapReady(@NonNull MapboxMap mapboxMap) {
//        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
//            @Override
//            public void onStyleLoaded(@NonNull Style style) {
//                origin = Point.fromLngLat(78.121719,  9.939093);
//                destination = Point.fromLngLat(77.951431, 9.587209);
//
//                initSource(style);
//                initLayers(style);
//                getRoute(mapboxMap, origin, destination);
//            }
//        });
//    }

    /**
     * Add the route and marker sources to the map
     */
//    private void initSource(@NonNull Style loadedMapStyle) {
//        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));
//        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[] {
//                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
//                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
//        loadedMapStyle.addSource(iconGeoJsonSource);
//    }

    /**
     * Add the route and marker icon layers to the map
     */
//    private void initLayers(@NonNull Style loadedMapStyle) {
//        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
//        routeLayer.setProperties(
//                lineCap(Property.LINE_CAP_ROUND),
//                lineJoin(Property.LINE_JOIN_ROUND),
//                lineWidth(5f),
//                lineColor(Color.parseColor("#009688"))
//        );
//        loadedMapStyle.addLayer(routeLayer);
//        loadedMapStyle.addImage(RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.red_marker)));
//        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
//                iconImage(RED_PIN_ICON_ID),
//                iconIgnorePlacement(true),
//                iconAllowOverlap(true),
//                iconOffset(new Float[] {0f, -9f})));
//    }


//    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {
//        client = MapboxDirections.builder()
//                .origin(origin)
//                .destination(destination)
//                .overview(DirectionsCriteria.OVERVIEW_FULL)
//                .profile(DirectionsCriteria.PROFILE_DRIVING)
//                .accessToken(getString(R.string.mapbox_access_token))
//                .build();
//
//        client.enqueueCall(new Callback<DirectionsResponse>() {
//            @Override
//            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
//                Timber.d("Response code: " + response.code());
//                Log.w("DirectionResponse:",response.toString());
//                if (response.body() == null) {
//                    Timber.e("No routes found, make sure you set the right user and access token.");
//                    return;
//                } else if (response.body().routes().size() < 1) {
//                    Timber.e("No routes found");
//                    return;
//                }
//                currentRoute = response.body().routes().get(0);
//                if (mapboxMap != null) {
//                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
//                        @Override
//                        public void onStyleLoaded(@NonNull Style style) {
//                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);
//                            if (source != null) {
//                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
//                            }
//                        }
//                    });
//                }
//            }
//            @Override
//            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
//                Timber.e("Error: " + throwable.getMessage());
//                Toast.makeText(MapBoxActivity.this, "Error: " + throwable.getMessage(),
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        LatLngBounds latLngBounds = new LatLngBounds.Builder()
//                .include(new LatLng(origin.latitude(),origin.longitude()))
//                .include(new LatLng(destination.latitude(),destination.longitude()))
//                .build();
//
//        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
//
//        CameraPosition position = new CameraPosition.Builder()
//                .target(new LatLng(destination.latitude(), destination.longitude()))ic_baseline_more_vert_24
//                .zoom(14)
//                .tilt(20)
//                .build();
//        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);
//
//        double distancevalue=distance(origin.latitude(),origin.longitude(),destination.latitude(),destination.longitude());
//        Log.w("OverAll_Distance:", String.valueOf(distancevalue));
//    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mapView.onStart();
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        mapView.onStop();
//    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//// Cancel the Directions API request
//        if (client != null) {
//            client.cancelCall();
//        }
//        mapView.onDestroy();
//    }

//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }


}