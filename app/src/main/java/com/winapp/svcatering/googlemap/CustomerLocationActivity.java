package com.winapp.svcatering.googlemap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.maps.MapView;
import com.winapp.svcatering.R;

import java.util.Objects;

//import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
//import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
//import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
//import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
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
public class CustomerLocationActivity extends AppCompatActivity
//        implements
//        OnMapReadyCallback , MapboxMap.OnMapClickListener, PermissionsListener
        {

    private MapView mapView;
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private String customerName;
    private double latitude;
    private double longitude;
//    private MapboxMap mapboxMap;
    private static final String GEOJSON_SOURCE_ID = "GEOJSON_SOURCE_ID";
    private static final String MARKER_IMAGE_ID = "MARKER_IMAGE_ID";
    private static final String MARKER_LAYER_ID = "MARKER_LAYER_ID";
    private static final String CALLOUT_LAYER_ID = "CALLOUT_LAYER_ID";
    private static final String PROPERTY_SELECTED = "selected";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_CAPITAL = "capital";
//    private GeoJsonSource source;
//    private FeatureCollection featureCollection;
//    private Style mapStyle;
//    PermissionsManager permissionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_customer_location);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//        mapView = findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);
        if (getIntent()!=null){
            customerName=getIntent().getStringExtra("customerName");
            latitude=getIntent().getDoubleExtra("latitude",0);
            longitude=getIntent().getDoubleExtra("longitude",0);
            getSupportActionBar().setTitle(customerName);
        }
    }

//    @Override
//    public void onMapReady(@NonNull MapboxMap mapboxMap) {
//        this.mapboxMap=mapboxMap;
//        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
//        symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(longitude, latitude)));
//       // symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(-54.14164, -33.981818)));
//      //  symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(-56.990533, -30.583266)));
//        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")
//                .withImage(ICON_ID, BitmapFactory.decodeResource(CustomerLocationActivity.this.getResources(), R.drawable.mapbox_marker_icon_default))
//                .withSource(new GeoJsonSource(SOURCE_ID, FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
//                .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID).withProperties(iconImage(ICON_ID), iconAllowOverlap(true), iconIgnorePlacement(true))
//                ), new Style.OnStyleLoaded() {
//            @Override
//            public void onStyleLoaded(@NonNull Style style) {
//                mapStyle=style;
//                CameraPosition position = new CameraPosition.Builder()
//                        .target(new LatLng(latitude, longitude))
//                        .zoom(12)
//                        .tilt(20)
//                        .build();
//                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);
//                mapboxMap.addOnMapClickListener(CustomerLocationActivity.this);
//                enableLocationComponent(mapStyle);
//            }
//        });
//
//        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(@NonNull Marker marker) {
//                Toast.makeText(CustomerLocationActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();
//                return true;
//            }
//        });
//
//
//       /* this.mapboxMap = mapboxMap;
//        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
//            @Override
//            public void onStyleLoaded(@NonNull Style style) {
//             //   new LoadGeoJsonDataTask(CustomerLocationActivity.this).execute();
//                mapboxMap.addOnMapClickListener(CustomerLocationActivity.this);
//            }
//        });*/
//
//     //   mapbox.addMarker(new MarkerOptions() .position(point) .title("Add Placement") .snippet("Some snippet"));
//
//        mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
//            @Nullable
//            @Override
//            public View getInfoWindow(@NonNull Marker marker) {
//
//                // return your info window view.
//
//                return mapView;
//            }
//        });
//    }

    @SuppressWarnings( {"MissingPermission"})
//    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
//        if (PermissionsManager.areLocationPermissionsGranted(this)) {
//            LocationComponent locationComponent = mapboxMap.getLocationComponent();
//            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
//            locationComponent.setLocationComponentEnabled(true);
//            locationComponent.setCameraMode(CameraMode.TRACKING);
//            locationComponent.setRenderMode(RenderMode.COMPASS);
//        } else {
//            permissionsManager = new PermissionsManager(this);
//            permissionsManager.requestLocationPermissions(this);
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @Override
//    public void onExplanationNeeded(List<String> permissionsToExplain) {
//        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onPermissionResult(boolean granted) {
//        if (granted) {
//            mapboxMap.getStyle(new Style.OnStyleLoaded() {
//                @Override
//                public void onStyleLoaded(@NonNull Style style) {
//                    enableLocationComponent(style);
//                }
//            });
//        } else {
//            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
//            finish();
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
//        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
//        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
//        mapView.onLowMemory();
    }

//    @Override
//    public boolean onMapClick(@NonNull LatLng point) {
//        return handleClickIcon(mapboxMap.getProjection().toScreenLocation(point));
//    }


    /**
     * Sets up all of the sources and layers needed for this example
     *
     * @param collection the FeatureCollection to set equal to the globally-declared FeatureCollection
     */
//    public void setUpData(final FeatureCollection collection) {
//        featureCollection = collection;
//        if (mapboxMap != null) {
//            mapboxMap.getStyle(style -> {
//                setupSource(style);
//                setUpImage(style);
//                setUpMarkerLayer(style);
//                setUpInfoWindowLayer(style);
//            });
//        }
//    }

    /**
     * Adds the GeoJSON source to the map
     */
//    private void setupSource(@NonNull Style loadedStyle) {
//        source = new GeoJsonSource(GEOJSON_SOURCE_ID, featureCollection);
//        loadedStyle.addSource(source);
//    }

    /**
     * Adds the marker image to the map for use as a SymbolLayer icon
     */
//    private void setUpImage(@NonNull Style loadedStyle) {
//        loadedStyle.addImage(MARKER_IMAGE_ID, BitmapFactory.decodeResource(
//                this.getResources(), R.drawable.red_marker));
//    }

    /**
     * Updates the display of data on the map after the FeatureCollection has been modified
     */
//    private void refreshSource() {
//        if (source != null && featureCollection != null) {
//            source.setGeoJson(featureCollection);
//        }
//    }

    /**
     * Setup a layer with maki icons, eg. west coast city.
     */
//    private void setUpMarkerLayer(@NonNull Style loadedStyle) {
//        loadedStyle.addLayer(new SymbolLayer(MARKER_LAYER_ID, GEOJSON_SOURCE_ID)
//                .withProperties(
//                        iconImage(MARKER_IMAGE_ID),
//                        iconAllowOverlap(true),
//                        iconOffset(new Float[] {0f, -8f})
//                ));
//    }

    /**
     * Setup a layer with Android SDK call-outs
     * <p>
     * name of the feature is used as key for the iconImage
     * </p>
     */
//    private void setUpInfoWindowLayer(@NonNull Style loadedStyle) {
//        loadedStyle.addLayer(new SymbolLayer(CALLOUT_LAYER_ID, GEOJSON_SOURCE_ID)
//                .withProperties(
//                        /* show image with id title based on the value of the name feature property */
//                        iconImage("{name}"),
//
//                        /* set anchor of icon to bottom-left */
//                        iconAnchor(ICON_ANCHOR_BOTTOM),
//
//                        /* all info window and marker image to appear at the same time*/
//                        iconAllowOverlap(true),
//
//                        /* offset the info window to be above the marker */
//                        iconOffset(new Float[] {-2f, -28f})
//                )
///* add a filter to show only when selected feature property is true */
//                .withFilter(eq((get(PROPERTY_SELECTED)), literal(true))));
//    }

    /**
     * This method handles click events for SymbolLayer symbols.
     * <p>
     * When a SymbolLayer icon is clicked, we moved that feature to the selected state.
     * </p>
     *
     * @param screenPoint the point on screen clicked
     */
//    private boolean handleClickIcon(PointF screenPoint) {
//        List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, MARKER_LAYER_ID);
//        if (!features.isEmpty()) {
//            String name = features.get(0).getStringProperty(PROPERTY_NAME);
//            List<Feature> featureList = featureCollection.features();
//            if (featureList != null) {
//                for (int i = 0; i < featureList.size(); i++) {
//                    if (featureList.get(i).getStringProperty(PROPERTY_NAME).equals(name)) {
//                        if (featureSelectStatus(i)) {
//                            setFeatureSelectState(featureList.get(i), false);
//                        } else {
//                            setSelected(i);
//                        }
//                    }
//                }
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * Set a feature selected state.
     *
     * @param index the index of selected feature
     */
//    private void setSelected(int index) {
//        if (featureCollection.features() != null) {
//            Feature feature = featureCollection.features().get(index);
//            setFeatureSelectState(feature, true);
//            refreshSource();
//        }
//    }

    /**
     * Selects the state of a feature
     *
     * @param feature the feature to be selected.
     */
//    private void setFeatureSelectState(Feature feature, boolean selectedState) {
//        if (feature.properties() != null) {
//            feature.properties().addProperty(PROPERTY_SELECTED, selectedState);
//            refreshSource();
//        }
//    }

    /**
     * Checks whether a Feature's boolean "selected" property is true or false
     *
     * @param index the specific Feature's index position in the FeatureCollection's list of Features.
     * @return true if "selected" is true. False if the boolean property is false.
     */
//    private boolean featureSelectStatus(int index) {
//        if (featureCollection == null) {
//            return false;
//        }
//        return featureCollection.features().get(index).getBooleanProperty(PROPERTY_SELECTED);
//    }

    /**
     * Invoked when the bitmaps have been generated from a view.
     */
//    public void setImageGenResults(HashMap<String, Bitmap> imageMap) {
//        if (mapboxMap != null) {
//            mapboxMap.getStyle(style -> {
// calling addImages is faster as separate addImage calls for each bitmap.
//                style.addImages(imageMap);
//            });
//        }
//    }


    /**
     * AsyncTask to load data from the assets folder.
     */
//    private static class LoadGeoJsonDataTask extends AsyncTask<Void, Void, FeatureCollection> {
//
//        private final WeakReference<CustomerLocationActivity> activityRef;
//
//        LoadGeoJsonDataTask(CustomerLocationActivity activity) {
//            this.activityRef = new WeakReference<>(activity);
//        }
//
//        @Override
//        protected FeatureCollection doInBackground(Void... params) {
//            CustomerLocationActivity activity = activityRef.get();
//
//            if (activity == null) {
//                return null;
//            }
//
//            String geoJson = loadGeoJsonFromAsset(activity, "us_west_coast.geojson");
//            return FeatureCollection.fromJson(geoJson);
//        }
//
//        @Override
//        protected void onPostExecute(FeatureCollection featureCollection) {
//            super.onPostExecute(featureCollection);
//            CustomerLocationActivity activity = activityRef.get();
//            if (featureCollection == null || activity == null) {
//                return;
//            }
//
//// This example runs on the premise that each GeoJSON Feature has a "selected" property,
//// with a boolean value. If your data's Features don't have this boolean property,
//// add it to the FeatureCollection 's features with the following code:
//            for (Feature singleFeature : featureCollection.features()) {
//                singleFeature.addBooleanProperty(PROPERTY_SELECTED, false);
//            }
//
//            activity.setUpData(featureCollection);
//            new GenerateViewIconTask(activity).execute(featureCollection);
//        }
//
//        static String loadGeoJsonFromAsset(Context context, String filename) {
//            try {
//// Load GeoJSON file from local asset folder
//                InputStream is = context.getAssets().open(filename);
//                int size = is.available();
//                byte[] buffer = new byte[size];
//                is.read(buffer);
//                is.close();
//                return new String(buffer, Charset.forName("UTF-8"));
//            } catch (Exception exception) {
//                throw new RuntimeException(exception);
//            }
//        }
//    }

    /**
     * AsyncTask to generate Bitmap from Views to be used as iconImage in a SymbolLayer.
     * <p>
     * Call be optionally be called to update the underlying data source after execution.
     * </p>
     * <p>
     * Generating Views on background thread since we are not going to be adding them to the view hierarchy.
     * </p>
     */
//    private static class GenerateViewIconTask extends AsyncTask<FeatureCollection, Void, HashMap<String, Bitmap>> {
//
//        private final HashMap<String, View> viewMap = new HashMap<>();
//        private final WeakReference<CustomerLocationActivity> activityRef;
//        private final boolean refreshSource;
//
//        GenerateViewIconTask(CustomerLocationActivity activity, boolean refreshSource) {
//            this.activityRef = new WeakReference<>(activity);
//            this.refreshSource = refreshSource;
//        }
//
//        GenerateViewIconTask(CustomerLocationActivity activity) {
//            this(activity, false);
//        }
//
//        @SuppressLint("StringFormatInvalid")
//        @SuppressWarnings("WrongThread")
//        @Override
//        protected HashMap<String, Bitmap> doInBackground(FeatureCollection... params) {
//            CustomerLocationActivity activity = activityRef.get();
//            if (activity != null) {
//                HashMap<String, Bitmap> imagesMap = new HashMap<>();
//                LayoutInflater inflater = LayoutInflater.from(activity);
//
//                FeatureCollection featureCollection = params[0];
//
//                for (Feature feature : featureCollection.features()) {
//
//                    BubbleLayout bubbleLayout = (BubbleLayout) inflater.inflate(R.layout.symbol_layer_info_window_layout_callout, null);
//
//                    String name = feature.getStringProperty(PROPERTY_NAME);
//                    TextView titleTextView = bubbleLayout.findViewById(R.id.info_window_title);
//                    titleTextView.setText(name);
//
//                    String style = feature.getStringProperty(PROPERTY_CAPITAL);
//                    TextView descriptionTextView = bubbleLayout.findViewById(R.id.info_window_description);
//                    descriptionTextView.setText(String.format(activity.getString(R.string.address), style));
//
//                    int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                    bubbleLayout.measure(measureSpec, measureSpec);
//
//                    float measuredWidth = bubbleLayout.getMeasuredWidth();
//
//                    bubbleLayout.setArrowPosition(measuredWidth / 2 - 5);
//
//                    Bitmap bitmap = SymbolGenerator.generate(bubbleLayout);
//                    imagesMap.put(name, bitmap);
//                    viewMap.put(name, bubbleLayout);
//                }
//
//                return imagesMap;
//            } else {
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(HashMap<String, Bitmap> bitmapHashMap) {
//            super.onPostExecute(bitmapHashMap);
//            CustomerLocationActivity activity = activityRef.get();
//            if (activity != null && bitmapHashMap != null) {
//                activity.setImageGenResults(bitmapHashMap);
//                if (refreshSource) {
//                    activity.refreshSource();
//                }
//            }
//            //Toast.makeText(activity, R.string.tap_on_marker_instruction, Toast.LENGTH_SHORT).show();
//        }
//    }

    /**
     * Utility class to generate Bitmaps for Symbol.
     */
//    private static class SymbolGenerator {
//
//        /**
//         * Generate a Bitmap from an Android SDK View.
//         *
//         * @param view the View to be drawn to a Bitmap
//         * @return the generated bitmap
//         */
//        static Bitmap generate(@NonNull View view) {
//            int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//            view.measure(measureSpec, measureSpec);
//
//            int measuredWidth = view.getMeasuredWidth();
//            int measuredHeight = view.getMeasuredHeight();
//
//            view.layout(0, 0, measuredWidth, measuredHeight);
//            Bitmap bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
//            bitmap.eraseColor(Color.TRANSPARENT);
//            Canvas canvas = new Canvas(bitmap);
//            view.draw(canvas);
//            return bitmap;
//        }
//    }
}