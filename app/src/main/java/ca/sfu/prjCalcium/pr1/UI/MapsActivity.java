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
import java.util.Comparator;

import ca.sfu.prjCalcium.pr1.Model.CustomInfoWindowAdapter;
import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.InspectionManager;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    // Constants used for SharedPreferences
    public static final String LAST_UPDATE_TIME_ON_SERVER = "lastUpdateTimeOnServer";
    public static final String RESTAURANT_UPDATE_TIME_ON_SERVER = "restaurantUpdateTimeOnServer";
    public static final String INSPECTION_LAST_UPDATE_TIME_ON_SERVER = "inspectionLastUpdateTimeOnServer";

    // Constants used to URLs
    private static final String inspectionURL = "https://data.surrey.ca/dataset/948e994d-74f5-41a2-b3cb-33fa6a98aa96/resource/30b38b66-649f-4507-a632-d5f6f5fe87f1/download/fraserhealthrestaurantinspectionreports.csv";
    private static final String restaurantURL = "https://data.surrey.ca/dataset/3c8cb648-0e80-4659-9078-ef4917b90ffb/resource/0e5d04a2-be9b-40fe-8de2-e88362ea916b/download/restaurants.csv";
    private static final String restaurantJsonUrl = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private static final String inspectionJsonUrl = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";

    // Request code for call back listener
    private static final int REQUEST_EXTERNAL_STORAGE = 1235;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1234;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    // Progress Dialogs
    ProgressDialog mProgressDialog;
    ProgressDialog jsonProgressDialog;
    private boolean mLocationPermissionGranted = false;
    private boolean mExternalStorageLocationGranted = false;
    private String restaurantUpdateTimeOnServer;
    private String inspectionUpdateTimeOnServer;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFLPC;

    private RestaurantManager manager = RestaurantManager.getInstance();

    private ClusterManager<MyItem> mClusterManager;

    private int check;
    private  int index;

    public static Intent makeIntent(Context c) {
        return new Intent(c, MapsActivity.class);
    }

    public static Intent makeSecondIntent(Context c, int restaurantIndex, int condition) {
        Intent intent = new Intent(c, MapsActivity.class);
        intent.putExtra("index", restaurantIndex);
        intent.putExtra("checkCondition", condition);
        return intent;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        long now = System.currentTimeMillis();

        JsonTask restJsonTask = new JsonTask();
        restJsonTask.execute(restaurantJsonUrl, inspectionJsonUrl);

        checkTime(now);
        initBackToListButton();

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

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            getDeviceLocation();
        }

        setUpClusterer();

        Intent intent = getIntent();
        index = intent.getIntExtra("index", 0);
        check = intent.getIntExtra("checkCondition", 0);
        if(check == 1) {

            LatLng coordinate = new LatLng(manager.getRestaurantAtIndex(index).getLatitude(),
                                           manager.getRestaurantAtIndex(index).getLongitude());
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    coordinate, 15);
            mMap.animateCamera(location);

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
            }

        } else {
            // Check user's last run choice
            SharedPreferences pref = getSharedPreferences("Time", MODE_PRIVATE);
            boolean choice = pref.getBoolean("UpdateRequired", false);
            if (choice) {
                createAskUserIfUpdateDialog();
                Toast.makeText(getApplicationContext(), "Have Update!", Toast.LENGTH_LONG).show();
            } else {
                int permission = ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Permission Denied, Using deafault data")
                            .setMessage("If you want to update data\n" +
                                    "go to Setting -> Apps -> Permission -> enable");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    loadManagerFromInternal();
                } else {
                    loadManagerFromExternal();
                }
                verifyLocationPermission();
            }
        }
        setSharedReferencesData("LastRun", now);
    }

    // https://www.youtube.com/watch?v=fPFr0So1LmI&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=5
    private void getDeviceLocation() {
        mFLPC = LocationServices.getFusedLocationProviderClient(this);

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
                            Toast.makeText(MapsActivity.this, "Could not determine location. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Maps", "SecurityException: " + e.getMessage());
            Toast.makeText(MapsActivity.this, "Could not determine location. ", Toast.LENGTH_SHORT).show();
        }
    }

    // https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
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
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            mExternalStorageLocationGranted = true;
            initDataDownload();
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
        SharedPreferences pref = getSharedPreferences("Time", MODE_PRIVATE);
        return pref.getLong("LastRun", 0);
    }

    private <T> void setSharedReferencesData(String name, T value) {
        SharedPreferences pref = getSharedPreferences("Time", MODE_PRIVATE);
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
        builder.setTitle("Update Found").setMessage("Do you want to update now?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Update status of shared references
                setSharedReferencesData("UpdateRequired", false);

                // Update files
                verifyStoragePermissions(MapsActivity.this);
                storeUpdateTimeToSharedPref();
            }
        });
        builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(),
                        "This message will show again on next start", Toast.LENGTH_LONG).show();

                // Use old files
                if (!manager.isDataRead()) {
                    loadManagerFromInternal();
                }
                verifyLocationPermission();

                // Save user choice
                setSharedReferencesData("UpdateRequired", true);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void initDataDownload() {
        if (mExternalStorageLocationGranted) {
            mProgressDialog = new ProgressDialog(MapsActivity.this);
            mProgressDialog.setMessage("Currently downloading files");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);

            // execute this when the downloader must be fired
            final DownloadTask downloadTask1 = new DownloadTask(MapsActivity.this, "/restaurant.csv");
            final DownloadTask downloadTask2 = new DownloadTask(MapsActivity.this, "/inspection.csv");

            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downloadTask1.cancel(true); //cancel the task
                    downloadTask2.cancel(true);

                    loadManagerFromInternal();
                }
            });

            downloadTask1.execute(restaurantURL);
            downloadTask2.execute(inspectionURL);
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
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {

                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
            case REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    mExternalStorageLocationGranted = true;
                    initDataDownload();
                } else { // if user does not give permission
                    loadManagerFromInternal();
                    verifyLocationPermission();
                }
            }
        }
    }

    private void setUpClusterer() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>() {
            @Override
            public void onClusterItemInfoWindowClick(MyItem myItem) {
                LatLng restaurantLatLng = myItem.getPosition();
                int r_index = manager.getRestaurantIndexByLatLng(restaurantLatLng);
                Intent intent = RestaurantDetailActivity.makeIntent(MapsActivity.this, r_index);
                startActivity(intent);
            }
        });
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(MapsActivity.this);
        mClusterManager.getMarkerCollection().setInfoWindowAdapter(adapter);

        // Add cluster items (markers) to the cluster manager.
        addItems();

        ClusterRenderer<MyItem> clusterRenderer = new IconRenderer(MapsActivity.this, mMap, mClusterManager);
        mClusterManager.setRenderer(clusterRenderer);
    }

    private void addItems() {
        for (Restaurant r : manager) {
            MyItem i = new MyItem(r);
            mClusterManager.addItem(i);
        }
    }

    public class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private final String mTitle;
        private final String mSnippet;

        public MyItem(Restaurant r) {
            mPosition = new LatLng(r.getLatitude(), r.getLongitude());
            mTitle = r.getRestaurantName();
            String sniAdd = "Address:" + r.getAddress();
            String sniStr;

            if (r.getInspections().isEmpty()) {
                sniStr = sniAdd + "\n" + "Hazard level undefined";
            } else {
                Inspection recentInspection = r.getInspections().getInspection(0);
                sniStr = sniAdd + "\n" + "Hazard level: " + recentInspection.getHazardRating();
            }

            mSnippet = sniStr;
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
    }

    public class IconRenderer extends DefaultClusterRenderer<MyItem> {
        public IconRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {
            LatLng restaurantLatLng = clusterItem.getPosition();
            int r_index = manager.getRestaurantIndexByLatLng(restaurantLatLng);
            Restaurant r = manager.getRestaurantAtIndex(r_index);

            if (r.getInspections().isEmpty()) {
                BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.green);
                Bitmap b = bitmapDraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);

                marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            } else {
                Inspection recentInspection = r.getInspections().getInspection(0);

                if (recentInspection.getHazardRating().equals("Low")) {
                    BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.green);
                    Bitmap b = bitmapDraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);

                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                } else if (recentInspection.getHazardRating().equals("Moderate")) {
                    BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.yellow);

                    Bitmap b = bitmapDraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);

                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                } else if (recentInspection.getHazardRating().equals("High")) {
                    BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.red);

                    Bitmap b = bitmapDraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);

                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                }
            }
            super.onClusterItemRendered(clusterItem, marker);
        }
    }

    private void loadManagerFromInternal() {
        manager.clear();
        manager.readRestaurantDataFromInternal(this);
        manager.sort(new AlphabetComparator());
        for (Restaurant r : manager) {
            InspectionManager iManager = r.getInspections();
            iManager.sort(new InspectionComparator().reversed());
        }
        manager.setDataRead(true);
    }

    private void loadManagerFromExternal() {
        manager.clear();
        manager.readRestaurantDataFromExternal();
        manager.addInspectionsToRestaurantsFromExternal();
        manager.sort(new AlphabetComparator());
        for (Restaurant r : manager) {
            InspectionManager iManager = r.getInspections();
            iManager.sort(new InspectionComparator().reversed());
        }
        manager.setDataRead(true);
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

        private String downloadLocation;

        public DownloadTask(Context context, String downloadLocation) {
            this.context = context;
            this.downloadLocation = downloadLocation;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream outputRestaurant = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
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
                outputRestaurant = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + downloadLocation);

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
                    outputRestaurant.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (outputRestaurant != null)
                        outputRestaurant.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
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
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();

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
            jsonProgressDialog.setMessage("Please wait");
            jsonProgressDialog.setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER);
            jsonProgressDialog.setCancelable(false);
            jsonProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {

                for (int i = 0; i < 2; i++) {
                    URL link = new URL(strings[i]);

                    connection = (HttpURLConnection) link.openConnection();
                    connection.connect();

                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuilder buffer = new StringBuilder();
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }

                    JSONObject jsonObject = new JSONObject(buffer.toString());
                    JSONObject result = jsonObject.getJSONObject("result");

                    if (i == 0) {
                        restaurantUpdateTimeOnServer = result.getString("metadata_modified");
                    } else {
                        inspectionUpdateTimeOnServer = result.getString("metadata_modified");
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
        }
    }
}
