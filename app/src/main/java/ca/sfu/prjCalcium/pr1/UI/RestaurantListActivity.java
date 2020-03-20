package ca.sfu.prjCalcium.pr1.UI;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.InspectionManager;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.R;


/**
 * Represent the initial screen's logic structure.
 */
public class RestaurantListActivity extends AppCompatActivity {

    ProgressDialog mProgressDialog;

    // Singleton
    private RestaurantManager manager = RestaurantManager.getInstance();
    private boolean mExternalStorageLocationGranted = false;

    public static Intent makeIntent(Context c) {
        return new Intent(c, RestaurantListActivity.class);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1235;

    private static String restaurantURL = "https://data.surrey.ca/dataset/3c8cb648-0e80-4659-9078-ef4917b90ffb/resource/0e5d04a2-be9b-40fe-8de2-e88362ea916b/download/restaurants.csv";
    private static String inspectionURL = "https://data.surrey.ca/dataset/948e994d-74f5-41a2-b3cb-33fa6a98aa96/resource/30b38b66-649f-4507-a632-d5f6f5fe87f1/download/fraserhealthrestaurantinspectionreports.csv";

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!manager.isDataRead()) {
            verifyStoragePermissions(RestaurantListActivity.this);
        } else {
            populateListView();
            clickRestaurant();
        }
    }

    public void verifyStoragePermissions(Activity activity) {
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

    private void initDataDownload() {
        if (mExternalStorageLocationGranted) {
            mProgressDialog = new ProgressDialog(RestaurantListActivity.this);
            mProgressDialog.setMessage("Currently downloading files");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);

            // execute this when the downloader must be fired
            final DownloadTask downloadTask1 = new DownloadTask(RestaurantListActivity.this, "/restaurant.csv");
            final DownloadTask downloadTask2 = new DownloadTask(RestaurantListActivity.this, "/inspection.csv");
            downloadTask1.execute(restaurantURL);
            downloadTask2.execute(inspectionURL);

            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask1.cancel(true); //cancel the task
                    downloadTask2.cancel(true);
                }
            });
        }
    }

    private void populateListView() {
        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.restaurantListView);
        list.setAdapter(adapter);
    }

    private void clickRestaurant() {
        ListView list = findViewById(R.id.restaurantListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = RestaurantDetailActivity.makeIntent(RestaurantListActivity.this, position);
                startActivity(intent);
            }
        });
    }


    private class MyListAdapter extends ArrayAdapter<Restaurant> {

        public MyListAdapter() {
            super(RestaurantListActivity.this, R.layout.restaurant_list, manager.getRestaurantsAsLists());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            // make sure we have a view to work with
            View itemView = convertView;

            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurant_list, parent, false);
            }

            // find the restaurant to work with
            Restaurant currentRestaurant = manager.getRestaurantAtIndex(position);


            // fill the name
            TextView textViewName = itemView.findViewById(R.id.name);
            textViewName.setText(currentRestaurant.getRestaurantName());

            TextView textViewTime = itemView.findViewById(R.id.time);
            TextView textViewIssues = itemView.findViewById(R.id.numOfIssues);
            ImageView imageViewHazard = itemView.findViewById(R.id.hazard);

            if (currentRestaurant.getInspections().isEmpty()) {
                textViewTime.setText(R.string.no_recent_inspections);
                textViewIssues.setText(R.string.zero_number_of_issues);
                textViewIssues.setTextColor(getResources().getColor(R.color.green));
                imageViewHazard.setImageDrawable(getDrawable(R.drawable.green));
            } else {
                Inspection firstInspection = currentRestaurant.getInspections().getInspection(0);

                int totalIssues = firstInspection.getNumCritical() + firstInspection.getNumNonCritical();

                textViewIssues.setText(getString(R.string.restaurant_list_num_issues, totalIssues));

                //Display date in intelligent format
                Date currentDate = new Date();
                Date pastDate = firstInspection.getInspectionDate();

                long dateDifference = TimeUnit.MILLISECONDS.toDays(currentDate.getTime() - pastDate.getTime());

                if (dateDifference < 30){
                    textViewTime.setText(getString(R.string.inspection_days_ago, dateDifference));
                } else if (dateDifference > 30 && dateDifference <= 366){
                    SimpleDateFormat formatter1 = new SimpleDateFormat("MMM dd", Locale.CANADA);
                    textViewTime.setText(formatter1.format(pastDate));
                } else {
                    SimpleDateFormat formatter2 = new SimpleDateFormat("MMM yyyy", Locale.CANADA);
                    textViewTime.setText(formatter2.format(pastDate));
                }

                if (firstInspection.getHazardRating().equals("Low")) {
                    imageViewHazard.setImageDrawable(getDrawable(R.drawable.green));
                    textViewIssues.setTextColor(getResources().getColor(R.color.green));
                }

                if (firstInspection.getHazardRating().equals("Moderate")) {
                    imageViewHazard.setImageDrawable(getDrawable(R.drawable.yellow));
                    textViewIssues.setTextColor(getResources().getColor(R.color.yellow));
                }

                if (firstInspection.getHazardRating().equals("High")) {
                    imageViewHazard.setImageDrawable(getDrawable(R.drawable.red));
                    textViewIssues.setTextColor(Color.RED);
                }
            }

            return itemView;
        }
    }

    // https://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property
    public class AlphabetComparator implements Comparator<Restaurant> {
        @Override
        public int compare(Restaurant r1, Restaurant r2) {
            return r1.getRestaurantName().compareTo(r2.getRestaurantName());
        }
    }

    public class InspectionComparator implements Comparator<Inspection> {
        @Override
        public int compare(Inspection i1, Inspection i2) {
            return i1.getInspectionDate().compareTo(i2.getInspectionDate());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mExternalStorageLocationGranted = false;

        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    mExternalStorageLocationGranted = true;
                    initDataDownload();
                }
            }
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

                manager.clear();
                manager.readRestaurantData();
                manager.addInspectionsToRestaurants();
                manager.sort(new AlphabetComparator());
                for (Restaurant r : manager) {
                    InspectionManager iManager = r.getInspections();
                    iManager.sort(new InspectionComparator().reversed());
                }
                manager.setDataRead(true);

                populateListView();
                clickRestaurant();
            }
        }
    }
}
