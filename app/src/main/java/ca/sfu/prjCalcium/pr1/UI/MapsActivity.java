package ca.sfu.prjCalcium.pr1.UI;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import ca.sfu.prjCalcium.pr1.Model.CustomInfoWindowAdapter;
import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.InspectionManager;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.Model.SearchResultList;
import ca.sfu.prjCalcium.pr1.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    // Constants used for SharedPreferences
    public static final String LAST_UPDATE_TIME_ON_SERVER = "lastUpdateTimeOnServer";
    public static final String RESTAURANT_UPDATE_TIME_ON_SERVER = "restaurantUpdateTimeOnServer";
    public static final String INSPECTION_LAST_UPDATE_TIME_ON_SERVER = "inspectionLastUpdateTimeOnServer";
    public static final String SHARED_PREF_IS_UPDATE_REQUIRED_NEXT_TIME = "UpdateRequired";
    public static final String SHARED_PREF_LAST_RUN_TIME = "LastRun";

    // Constants used to URLs
    private static final String restaurantJsonUrl = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private static final String inspectionJsonUrl = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";

    // Request code for call back listener
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 9021;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9022;

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    // Intent constants
    public static final String INTENT_EXTRA_RESTAURANT_INDEX = "index";
    public static final String MAPS_INTENT_EXTRA_SOURCE_ACTIVITY_COND = "sourceActivityCond";

    // Progress Dialogs
    ProgressDialog mProgressDialog;
    ProgressDialog jsonProgressDialog;

    private boolean mLocationPermissionGranted = false;
    private boolean mExternalStorageLocationGranted = false;

    private String restaurantUpdateTimeOnServer;
    private String inspectionUpdateTimeOnServer;

    private GoogleMap mMap;

    private RestaurantManager rManager = RestaurantManager.getInstance();
    private SearchResultList searchResultList = SearchResultList.getInstance();

    private ClusterManager<MyItem> mClusterManager;

    private static String inspectionURL;
    private static String restaurantURL;

    private boolean ifLoadCluster = true;
    private int sourceActivityCond;
    private int r_index;

    private boolean newDataDownloaded = false;

    public static final int MAPS_ACTIVITY_SOURCE_ACTIVITY_COND = 10157;

    public static Intent makeIntent(Context c) {
        return new Intent(c, MapsActivity.class);
    }

    public static Intent makeIntentWithSourceActivity(Context c, int sourceActivityCondCode) {
        Intent intent = new Intent(c, MapsActivity.class);
        intent.putExtra(MAPS_INTENT_EXTRA_SOURCE_ACTIVITY_COND, sourceActivityCondCode);

        return intent;
    }

    // checkCondition is used for selecting specific restaurant when we tap on GPS coordinate
    public static Intent makeIntentFromDetail(Context c, int restaurantIndex, int sourceActivityCondCode) {
        Intent intent = new Intent(c, MapsActivity.class);

        intent.putExtra(INTENT_EXTRA_RESTAURANT_INDEX, restaurantIndex);
        intent.putExtra(MAPS_INTENT_EXTRA_SOURCE_ACTIVITY_COND, sourceActivityCondCode);

        return intent;
    }

    @Override
    public void onBackPressed() {
        extractDataFromIntent();
        //check source activity to make android back button work differently
        if (sourceActivityCond == RestaurantDetailActivity.RESTAURANT_DETAIL_SOURCE_ACTIVITY_COND) {
            finish();
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        if (!rManager.isDataRead()) {
            // if not read (means we probably launched the app),
            // then check if update is available first, then it will handle the data-reading
            JsonTask restJsonTask = new JsonTask();
            restJsonTask.execute(restaurantJsonUrl, inspectionJsonUrl);
        } else {
            // otherwise, we are called from another activity, then init map, no need to redownload the data
            verifyLocationPermission();
        }
        initBackToListButton();
        searchBtn();
    }

    private void initBackToListButton() {
        Button listBtn = findViewById(R.id.mapBackToListBtn);

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = RestaurantListActivity.makeIntent(MapsActivity.this);
                startActivity(intent);
            }
        });
    }

    private void searchBtn() {
        Button searchBtn = findViewById(R.id.mapSearchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SearchActivity.makeIntent(MapsActivity.this, MAPS_ACTIVITY_SOURCE_ACTIVITY_COND);
                startActivity(intent);
            }
        });
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        extractDataFromIntent();

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (ifLoadCluster) {
            if (mLocationPermissionGranted) {
                getDeviceLocation();
            }
            setUpClusterer();
        } else {
            Restaurant r = rManager.getRestaurantAtIndex(r_index);

            LatLng coordinate = new LatLng(r.getLatitude(), r.getLongitude());

            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(coordinate, 15f);
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));

            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(coordinate)
                    .title(r.getRestaurantName()));
            if (r.getInspections().isEmpty()) {
                m.setSnippet(getString(R.string.info_window_info_no_inspection, r.getAddress()));
            } else {
                m.setSnippet(getString(R.string.info_window_info, r.getAddress(), r.getInspections().getInspection(0).getHazardRating()));
            }

            mMap.moveCamera(location);
            m.showInfoWindow();
        }
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        r_index = intent.getIntExtra(INTENT_EXTRA_RESTAURANT_INDEX, -1);
        sourceActivityCond = intent.getIntExtra(MAPS_INTENT_EXTRA_SOURCE_ACTIVITY_COND, -1);

        if (sourceActivityCond == RestaurantDetailActivity.RESTAURANT_DETAIL_SOURCE_ACTIVITY_COND) {
            // if we come from detail activity, then wen don't need to load the clusters.
            ifLoadCluster = false;
        }
    }

    /*
     * Note: This is the *only* entry point to initialize the map,
     * because we need to double check the permission in case the user turns it off manually.
     *
     * https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
     */
    private void verifyLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        int ifFineLocationGranted = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int ifCoarseLocationGranted = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (ifCoarseLocationGranted == PackageManager.PERMISSION_GRANTED && ifFineLocationGranted == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            initMap();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    PERMISSION_REQUEST_EXTERNAL_STORAGE
            );
        } else {
            mExternalStorageLocationGranted = true;
            initDataDownload();
        }
    }

    private void checkTime(long now) {
        long last = getLastStartTime();
        // 20 hours = 72000000 milliseconds
        if (now - last >= 72000000) {
            Toast.makeText(getApplicationContext(), R.string.maps_toast_need_update, Toast.LENGTH_LONG).show();

            // Need to check if server has updated version of files
            if (isUpdateNeededFromServer()) {
                createAskUserIfUpdateDialog();
            } else {
                Toast.makeText(getApplicationContext(), R.string.maps_toast_welcome_back, Toast.LENGTH_LONG).show();
                if (!rManager.isDataRead()) {
                    loadManagerFromExternal();
                }

                verifyLocationPermission();
            }

        } else {
            // Check user's last run choice
            SharedPreferences pref = getSharedPreferences(LAST_UPDATE_TIME_ON_SERVER, MODE_PRIVATE);
            boolean choice = pref.getBoolean(SHARED_PREF_IS_UPDATE_REQUIRED_NEXT_TIME, false);

            if (choice) {
                createAskUserIfUpdateDialog();
                Toast.makeText(getApplicationContext(), R.string.toast_have_update, Toast.LENGTH_LONG).show();
            } else {
                int permission = ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.ask_user_if_update_permission_denied_title)
                            .setMessage(getString(R.string.ask_user_if_update_permission_denied_message));
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    if (!rManager.isDataRead()) {
                        loadManagerFromInternal();
                    }
                } else {
                    if (!rManager.isDataRead()) {
                        loadManagerFromExternal();
                    }
                }
                verifyLocationPermission();
            }
        }
        setSharedReferencesData(SHARED_PREF_LAST_RUN_TIME, now);
    }

    // https://www.youtube.com/watch?v=fPFr0So1LmI&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=5
    private void getDeviceLocation() {
        FusedLocationProviderClient mFLPC = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                Task location = mFLPC.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 10f));
                        } else {
                            Toast.makeText(MapsActivity.this, getString(R.string.device_location_toast_cannot_get_location), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Maps", "SecurityException: " + e.getMessage());
            Toast.makeText(MapsActivity.this, getString(R.string.device_location_toast_cannot_get_location), Toast.LENGTH_SHORT).show();
        }
    }

    private void storeUpdateTimeToSharedPref() {
        SharedPreferences preferences = getSharedPreferences(LAST_UPDATE_TIME_ON_SERVER, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(RESTAURANT_UPDATE_TIME_ON_SERVER, restaurantUpdateTimeOnServer);
        editor.putString(INSPECTION_LAST_UPDATE_TIME_ON_SERVER, inspectionUpdateTimeOnServer);
        editor.apply();
    }

    private boolean isUpdateNeededFromServer() {
        SharedPreferences preferences = getSharedPreferences(LAST_UPDATE_TIME_ON_SERVER, MODE_PRIVATE);

        String lastRestaurantUpdateTime = preferences.getString(RESTAURANT_UPDATE_TIME_ON_SERVER, "");
        String lastInspectionUpdateTime = preferences.getString(INSPECTION_LAST_UPDATE_TIME_ON_SERVER, "");

        return !lastInspectionUpdateTime.equals(inspectionUpdateTimeOnServer) ||
                !lastRestaurantUpdateTime.equals(restaurantUpdateTimeOnServer);
    }

    private long getLastStartTime() {
        SharedPreferences pref = getSharedPreferences(LAST_UPDATE_TIME_ON_SERVER, MODE_PRIVATE);
        return pref.getLong(SHARED_PREF_LAST_RUN_TIME, 0);
    }

    private <T> void setSharedReferencesData(String name, T value) {
        SharedPreferences pref = getSharedPreferences(LAST_UPDATE_TIME_ON_SERVER, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (value instanceof Long) {
            editor.putLong(name, (Long) value);
        } else { // value instanceof Boolean
            editor.putBoolean(name, (Boolean) value);
        }
        editor.apply();
    }

    private void createAskUserIfUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ask_user_if_update_title).setMessage(R.string.ask_user_if_update_message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Update status of shared references
                setSharedReferencesData(SHARED_PREF_IS_UPDATE_REQUIRED_NEXT_TIME, false);

                // Update files
                if (!rManager.isDataRead()) {
                    verifyStoragePermissions(MapsActivity.this);
                    storeUpdateTimeToSharedPref();
                }
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(),
                        R.string.ask_user_if_update_toast_message_will_show_again, Toast.LENGTH_LONG).show();

                // Save user choice
                setSharedReferencesData(SHARED_PREF_IS_UPDATE_REQUIRED_NEXT_TIME, true);

                // Use old files
                if (!rManager.isDataRead()) {
                    loadManagerFromInternal();
                }
                verifyLocationPermission();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void initDataDownload() {
        if (mExternalStorageLocationGranted) {
            // execute this when the downloader must be fired
            final DownloadTask downloadTask = new DownloadTask(MapsActivity.this, new String[]{"/restaurant.csv", "/inspection.csv"});
            downloadTask.execute(restaurantURL, inspectionURL);
        }
    }

    private void loadManagerFromInternal() {
        rManager.clear();
        rManager.readRestaurantDataFromInternal(this);
        rManager.sort(new AlphabetComparator());
        for (Restaurant r : rManager) {
            InspectionManager iManager = r.getInspections();
            iManager.sort(new InspectionComparator().reversed());
        }
        rManager.setDataRead(true);
        ArrayList<String> favedRestaurants = new ArrayList<>(new HashSet<>(RestaurantDetailActivity.getFavedRestaurantFromSharedPref(this)));
        for (String number : favedRestaurants) {
            Restaurant r = rManager.getRestaurantByTrackingNumber(number);
            if (r != null) {
                r.setFaved(true);
            }
        }
    }

    private void loadManagerFromExternal() {
        rManager.clear();
        rManager.readRestaurantDataFromExternal();
        rManager.addInspectionsToRestaurantsFromExternal();
        rManager.sort(new AlphabetComparator());
        for (Restaurant r : rManager) {
            InspectionManager iManager = r.getInspections();
            iManager.sort(new InspectionComparator().reversed());
        }
        rManager.setDataRead(true);

        ArrayList<String> favedRestaurants = new ArrayList<>(new HashSet<>(RestaurantDetailActivity.getFavedRestaurantFromSharedPref(this)));
        ArrayList<String> favedInspections = new ArrayList<>(new HashSet<>(RestaurantDetailActivity.getFavedInspectionFromSharedPref(this)));

        List<Restaurant> changedRestaurants = new ArrayList<>();

        for (int i = 0; i < favedRestaurants.size(); i++) {
            Restaurant r = rManager.getRestaurantByTrackingNumber(favedRestaurants.get(i));
            if (r != null) {
                r.setFaved(true);

                if (newDataDownloaded) {
                    String latestInspectionString = favedInspections.get(i).substring(r.getTrackingNumber().length() + 1);
                    InspectionManager inspectionManager = r.getInspections();
                    if (!inspectionManager.isEmpty()) {
                        String currentLatestInspectionString = inspectionManager.getInspection(0).getInspectionDate().toString();

                        if (!currentLatestInspectionString.equals(latestInspectionString)) {
                            changedRestaurants.add(r);
                        }
                    }
                }
            }
        }

        if (newDataDownloaded) {
            StringBuilder info = new StringBuilder();
            for (Restaurant r : changedRestaurants) {
                InspectionManager inspectionManager = r.getInspections();

                if (!inspectionManager.isEmpty()) {
                    info.append(getString(R.string.new_inspection_alert_dialog_message_entry_line, r.getRestaurantName(),
                            r.getInspections().getInspection(0).getInspectionDate().toString(),
                            r.getInspections().getInspection(0).getHazardRating()));
                    info.append("\n");
                }
            }
            if (!info.toString().equals("")) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.restaurant_new_inspections_dialog_title)
                        .setMessage(info)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }
    }

    // https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        mExternalStorageLocationGranted = false;

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true;
                    initMap();
                }
                break;

            case PERMISSION_REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    mExternalStorageLocationGranted = true;
                    initDataDownload();
                } else { // if user does not give permission
                    if (!rManager.isDataRead()) {
                        loadManagerFromInternal();
                    }
                    verifyLocationPermission();
                }
                break;
        }
    }

    private void setUpClusterer() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();

        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>() {
            @Override
            public void onClusterItemInfoWindowClick(MyItem myItem) {
                LatLng restaurantLatLng = myItem.getPosition();
                int r_index = rManager.getRestaurantIndexByLatLng(restaurantLatLng);
                Intent intent = RestaurantDetailActivity.makeIntent(MapsActivity.this, r_index);
                startActivity(intent);
            }
        });
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(MapsActivity.this);
        mClusterManager.getMarkerCollection().setInfoWindowAdapter(adapter);

        ClusterRenderer<MyItem> clusterRenderer = new IconRenderer(MapsActivity.this, mMap, mClusterManager);
        mClusterManager.setRenderer(clusterRenderer);
    }

    private void addItems() {
        // if users click submit button without inputting anything, should return to the original map
        if (searchResultList.isSearched()) {
            for (Restaurant r : searchResultList) {
                MyItem i = new MyItem(r);
                mClusterManager.addItem(i);
            }
        } else {
            for (Restaurant r : rManager) {
                MyItem i = new MyItem(r);
                mClusterManager.addItem(i);
            }
        }

    }

    public class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private final String mTitle;
        private final String mSnippet;
        private String restaurantTracking;

        public MyItem(Restaurant r) {
            mPosition = new LatLng(r.getLatitude(), r.getLongitude());
            mTitle = r.getRestaurantName();
            restaurantTracking = r.getTrackingNumber();

            if (r.getInspections().isEmpty()) {
                mSnippet = getString(R.string.info_window_info_no_inspection, r.getAddress());
            } else {
                mSnippet = getString(R.string.info_window_info, r.getAddress(), r.getInspections().getInspection(0).getHazardRating());
            }
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public String getTitle() {
            return mTitle;
        }

        @Override
        public String getSnippet() {
            return mSnippet;
        }

        public String getRestaurantTracking() {
            return restaurantTracking;
        }
    }

    public class IconRenderer extends DefaultClusterRenderer<MyItem> {
        public IconRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {
            Restaurant r = rManager.getRestaurantByTrackingNumber(clusterItem.getRestaurantTracking());

            if (r.getInspections().isEmpty()) {
                BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.green);
                Bitmap b = bitmapDraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);

                marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            } else {
                Inspection recentInspection = r.getInspections().getInspection(0);

                switch (recentInspection.getHazardRating()) {
                    case "Low": {
                        BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.green);
                        Bitmap b = bitmapDraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);

                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        break;
                    }
                    case "Moderate": {
                        BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.yellow);

                        Bitmap b = bitmapDraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);

                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        break;
                    }
                    case "High": {
                        BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.red);

                        Bitmap b = bitmapDraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);

                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        break;
                    }
                }
            }
            super.onClusterItemRendered(clusterItem, marker);
        }
    }

    // https://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property
    public static class AlphabetComparator implements Comparator<Restaurant> {
        @Override
        public int compare(Restaurant r1, Restaurant r2) {
            return r1.getRestaurantName().compareTo(r2.getRestaurantName());
        }
    }

    public static class InspectionComparator implements Comparator<Inspection> {
        @Override
        public int compare(Inspection i1, Inspection i2) {
            return i1.getInspectionDate().compareTo(i2.getInspectionDate());
        }
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        private String[] downloadLocation;

        public DownloadTask(Context context, String[] downloadLocation) {
            this.context = context;
            this.downloadLocation = downloadLocation;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            for (int i = 0; i < sUrl.length; i++) {
                try {
                    URL link = new URL(sUrl[i]);
                    connection = (HttpURLConnection) link.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();
                    }

                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    int fileLength = connection.getContentLength();

                    // download the file
                    input = connection.getInputStream();

                    if (i == 0) {
                        output = new FileOutputStream(Environment
                                .getExternalStorageDirectory().toString()
                                + downloadLocation[0]);
                    } else {
                        output = new FileOutputStream(Environment
                                .getExternalStorageDirectory().toString()
                                + downloadLocation[1]);
                    }

                    byte[] data = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) // only if total length is known
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                } catch (Exception e) {
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    } finally {
                        if (connection != null)
                            connection.disconnect();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);

            mProgressDialog = new ProgressDialog(MapsActivity.this);
            mProgressDialog.setMessage(getString(R.string.maps_progress_dialog_downloading_csv));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);

            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DownloadTask.this.cancel(true); //cancel the task
                    if (!rManager.isDataRead()) {
                        loadManagerFromInternal();
                    }
                }
            });

            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null) {
                Toast.makeText(context, getString(R.string.download_task_download_error, result), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, R.string.toast_file_downloaded, Toast.LENGTH_SHORT).show();
                newDataDownloaded = true;

                loadManagerFromExternal();
                // In case user turns off permissions manually.
                verifyLocationPermission();
            }
        }
    }

    private class JsonTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            jsonProgressDialog = new ProgressDialog(MapsActivity.this);
            jsonProgressDialog.setMessage(getString(R.string.json_task_dialog_title));
            jsonProgressDialog.setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER);
            jsonProgressDialog.setCancelable(false);
            jsonProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection;
            BufferedReader reader;

            try {
                for (int i = 0; i < strings.length; i++) {
                    URL link = new URL(strings[i]);

                    connection = (HttpURLConnection) link.openConnection();
                    connection.connect();

                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuilder buffer = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }

                    JSONObject jsonObject = new JSONObject(buffer.toString());
                    JSONObject result = jsonObject.getJSONObject("result");

                    if (i == 0) {
                        restaurantUpdateTimeOnServer = result.getString("metadata_modified");
                        restaurantURL = result.getJSONArray("resources").getJSONObject(0).getString("url");
                        restaurantURL = restaurantURL.replace("http://", "https://");
                    } else {
                        inspectionUpdateTimeOnServer = result.getString("metadata_modified");
                        inspectionURL = result.getJSONArray("resources").getJSONObject(0).getString("url");
                        inspectionURL = inspectionURL.replace("http://", "https://");
                    }

                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            jsonProgressDialog.dismiss();

            long now = System.currentTimeMillis();
            checkTime(now);
        }
    }
}
