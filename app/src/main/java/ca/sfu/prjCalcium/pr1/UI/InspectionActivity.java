package ca.sfu.prjCalcium.pr1.UI;

import android.Manifest;
import android.annotation.SuppressLint;
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
import java.util.Locale;

import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.Model.Violation;
import ca.sfu.prjCalcium.pr1.Model.ViolationManager;
import ca.sfu.prjCalcium.pr1.R;

/**
 * Represent the logic inside the screen that displays the inspections.
 */
public class InspectionActivity extends AppCompatActivity {

    ProgressDialog mProgressDialog;

    Restaurant r;
    Inspection i;

    // Singleton
    private RestaurantManager rManager;
    private boolean mExternalStorageLocationGranted = false;

    private static final String I_INSPECTION_POSITION_PASSED_IN = "i_inspection_position_passed_in";
    private static final String I_RESTAURANT_POSITION_PASSED_IN = "i_restaurant_position_passed_in";

    public static Intent makeIntent(Context c, int restaurantIndex, int restaurantInspectionIndex) {
        Intent intent = new Intent(c, InspectionActivity.class);

        intent.putExtra(I_RESTAURANT_POSITION_PASSED_IN, restaurantIndex);
        intent.putExtra(I_INSPECTION_POSITION_PASSED_IN, restaurantInspectionIndex);

        return intent;
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1235;
    private static String url = "https://data.surrey.ca/dataset/948e994d-74f5-41a2-b3cb-33fa6a98aa96/resource/30b38b66-649f-4507-a632-d5f6f5fe87f1/download/fraserhealthrestaurantinspectionreports.csv";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        rManager = RestaurantManager.getInstance();

        extractDataFromIntent();
        setInformation();
        populateListView();
        clickViolation();

        verifyStoragePermissions(InspectionActivity.this);
    }

    private void initDataDownload() {
        if (mExternalStorageLocationGranted) {
            mProgressDialog = new ProgressDialog(InspectionActivity.this);
            mProgressDialog.setMessage("Currently downloading file");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);

            // execute this when the downloader must be fired
            final InspectionActivity.DownloadTask downloadTask = new InspectionActivity.DownloadTask(InspectionActivity.this);
            downloadTask.execute(url);

            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancel(true); //cancel the task
                }
            });
        }
    }

    private void setInformation() {
        TextView date = findViewById(R.id.inspectionDate);

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.CANADA);

        date.setText(getString(R.string.inspection_date, formatter.format(i.getInspectionDate())));

        TextView hazardLevel = findViewById(R.id.InspectionHazardLevel);
        hazardLevel.setText(getString(R.string.restaurant_detail_hazard_level, i.getHazardRating()));

        TextView inspectionType = findViewById(R.id.inspectionType);
        inspectionType.setText(getString(R.string.inspection_inspection_type, i.getInspeType()));

        TextView numOfCriticalIssues = findViewById(R.id.InspectionCriticalIssues);
        numOfCriticalIssues.setText(getString(R.string.restaurant_detail_num_critical_issue, i.getNumCritical()));

        TextView numOfNonCriticalIssues = findViewById(R.id.InspectionNonCriticalIssues);
        numOfNonCriticalIssues.setText(getString(R.string.restaurant_detail_num_non_critical_issue, i.getNumNonCritical()));

        ImageView hazardImgView = findViewById(R.id.InspectionImageView);
        if (i.getHazardRating().equals("Low")) {
            hazardImgView.setImageDrawable(getDrawable(R.drawable.green));
            hazardLevel.setTextColor(getResources().getColor(R.color.green));
        }

        if (i.getHazardRating().equals("Moderate")) {
            hazardImgView.setImageDrawable(getDrawable(R.drawable.yellow));
            hazardLevel.setTextColor(getResources().getColor(R.color.yellow));
        }

        if (i.getHazardRating().equals("High")) {
            hazardImgView.setImageDrawable(getDrawable(R.drawable.red));
            hazardLevel.setTextColor(Color.RED);
        }
    }

    private void populateListView() {
        ArrayAdapter<Violation> adapter = new InspectionActivity.MyListAdapter();
        ListView list = findViewById(R.id.violationListView);
        list.setAdapter(adapter);
    }

    // toast message when violation is clicked
    private void clickViolation() {
        ListView list = findViewById(R.id.violationListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Violation clickedViolation = i.getVioLump().getViolations().get(position);
                String message = clickedViolation.getDetails();
                Toast.makeText(InspectionActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();

        r = rManager.getRestaurantAtIndex(intent.getIntExtra(I_RESTAURANT_POSITION_PASSED_IN, 0));
        i = r.getInspections().getInspection(intent.getIntExtra(I_INSPECTION_POSITION_PASSED_IN, 0));
    }

    private class MyListAdapter extends ArrayAdapter<Violation> {

        public MyListAdapter() {
            super(InspectionActivity.this, R.layout.violation_list, i.getVioLump().getViolations());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            // make sure we have a view to work with
            View itemView = convertView;

            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.violation_list, parent, false);
            }

            // find the violation manager to work with
            ViolationManager vManager = i.getVioLump();

            if (vManager.isEmpty()) {
                return itemView;
            }

            Violation currentViolation = vManager.getViolations().get(position);

            // fill the violation type icon (pest, food, ..)
            ImageView imageViewNature = itemView.findViewById(R.id.natureOfViolation);

            if ((100 <= currentViolation.getCode() && currentViolation.getCode() <= 199)) {
                imageViewNature.setImageResource(R.drawable.regulations);
            } else if (200 <= currentViolation.getCode() && currentViolation.getCode() <= 299) {
                imageViewNature.setImageResource(R.drawable.food);
            } else if (300 <= currentViolation.getCode() && currentViolation.getCode() <= 399) {
                imageViewNature.setImageResource(R.drawable.settings);
            } else if (400 <= currentViolation.getCode() && currentViolation.getCode() <= 499) {
                imageViewNature.setImageResource(R.drawable.employee);
            } else {
                imageViewNature.setImageResource(R.drawable.operator);
            }

            // fill the short description
            TextView textViewShortDescription = itemView.findViewById(R.id.description);
            textViewShortDescription.setText(currentViolation.convertDetailsToCategories());

            // fill the severity icon - (critical or non-critical)
            ImageView imageViewSeverity = itemView.findViewById(R.id.severity);
            if (currentViolation.getCritical().equals("Not Critical")) {
                imageViewSeverity.setImageDrawable(getDrawable(R.drawable.yellow_x));
            } else {
                imageViewSeverity.setImageDrawable(getDrawable(R.drawable.red_x));
            }

            return itemView;
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

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
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

                output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/InspectionList.csv");

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
            }
        }
    }
}
