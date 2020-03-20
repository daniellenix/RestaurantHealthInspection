package ca.sfu.prjCalcium.pr1.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

import ca.sfu.prjCalcium.pr1.Model.CustomInfoWindowAdapter;
import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.InspectionManager;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1234;
    boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFLPC;

    private RestaurantManager manager = RestaurantManager.getInstance();

    private android.os.Handler handler = new android.os.Handler();
    private ClusterManager<MyItem> mClusterManager;

    public static Intent makeIntent(Context c) {
        return new Intent(c, MapsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        if (!manager.isDataRead()) {
            manager.readRestaurantData(this);

            manager.sort(new RestaurantListActivity.AlphabetComparator());

            for (Restaurant r : manager) {
                InspectionManager iManager = r.getInspections();

                iManager.sort(new RestaurantListActivity.InspectionComparator().reversed());
            }

            manager.setDataRead(true);
        }

        getLocationPermission();
        initButton();
    }

    private void initButton() {
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDeviceLocation();
                handler.postDelayed(this, 50000);
            }
        }, 50000);

        setUpClusterer();
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
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    // https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    initMap();
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
}
