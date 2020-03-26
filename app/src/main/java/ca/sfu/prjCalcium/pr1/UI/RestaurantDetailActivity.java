package ca.sfu.prjCalcium.pr1.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.R;

/**
 * Represent the logic inside the screen that displays the detail of a restaurant
 */
public class RestaurantDetailActivity extends AppCompatActivity {

    public static final int RESTAURANT_DETAIL_SOURCE_ACTIVITY_COND = 9012;
    Restaurant r;
    int r_index;
    private RestaurantManager manager;

    public static final String R_DETAIL_RESTAURANT_POSITION_PASSED_IN = "r_detail_restaurant_position_passed_in";

    public static Intent makeIntent(Context c, int restaurantPosition) {
        Intent intent = new Intent(c, RestaurantDetailActivity.class);

        intent.putExtra(R_DETAIL_RESTAURANT_POSITION_PASSED_IN, restaurantPosition);
        return intent;
    }

    //https://stackoverflow.com/questions/38158953/how-to-create-button-in-action-bar-in-android
    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        manager = RestaurantManager.getInstance();

        extractDataFromIntent();
        populateInfo();
        populateListView();
        clickInspection();
    }

    private void populateInfo() {
        TextView nameView = findViewById(R.id.detailName);
        nameView.setText(r.getRestaurantName());

        TextView addressView = findViewById(R.id.detailAddress);
        addressView.setText(r.getAddress());

        TextView gpsView = findViewById(R.id.detailGps);
        gpsView.setText(getString(R.string.restaurant_detail_long_lati, r.getLongitude(), r.getLatitude()));
        gpsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reference: https://stackoverflow.com/questions/32161757/how-to-animate-the-camera-to-a-specified-location-in-google-maps-v2-for-android
                Intent intent = MapsActivity.makeIntentFromDetail(RestaurantDetailActivity.this, r_index, RESTAURANT_DETAIL_SOURCE_ACTIVITY_COND);

                startActivity(intent);
            }
        });
    }

    private void populateListView() {
        ArrayAdapter<Inspection> adapter = new RestaurantDetailActivity.MyListAdapter();
        ListView list = findViewById(R.id.restaurantInspectionListView);
        list.setEmptyView(findViewById(R.id.inspectionNoItems));
        list.setAdapter(adapter);
    }

    private void clickInspection() {
        ListView list = findViewById(R.id.restaurantInspectionListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = InspectionActivity.makeIntent(RestaurantDetailActivity.this, r_index, position);
                startActivity(intent);
            }
        });
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();

        r_index = intent.getIntExtra(R_DETAIL_RESTAURANT_POSITION_PASSED_IN, 0);

        r = manager.getRestaurantAtIndex(intent.getIntExtra(R_DETAIL_RESTAURANT_POSITION_PASSED_IN, 0));
    }

    private class MyListAdapter extends ArrayAdapter<Inspection> {

        public MyListAdapter() {
            super(RestaurantDetailActivity.this, R.layout.inspection_list, r.getInspections().getInspections());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            // make sure we have a view to work with
            View itemView = convertView;

            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.inspection_list, parent, false);
            }

            // find the inspection to work with
            Inspection currentInspection = r.getInspections().getInspection(position);

            TextView hazardTextView = itemView.findViewById(R.id.inspectionListHazard);
            hazardTextView.setText(getString(R.string.restaurant_detail_hazard_level, currentInspection.getHazardRating()));

            // fill the hazard icon
            ImageView imageViewHazard = itemView.findViewById(R.id.hazard);
            if (currentInspection.getHazardRating().equals("Low")) {
                imageViewHazard.setImageDrawable(getDrawable(R.drawable.green));
                hazardTextView.setTextColor(getResources().getColor(R.color.green));
            }

            if (currentInspection.getHazardRating().equals("Moderate")) {
                imageViewHazard.setImageDrawable(getDrawable(R.drawable.yellow));
                hazardTextView.setTextColor(getResources().getColor(R.color.yellow));

            }

            if (currentInspection.getHazardRating().equals("High")) {
                imageViewHazard.setImageDrawable(getDrawable(R.drawable.red));
                hazardTextView.setTextColor(Color.RED);
            }


            // fill the critical issues
            TextView textViewCriticalIssues = itemView.findViewById(R.id.InspectionCriticalIssues);
            textViewCriticalIssues.setText(getString(R.string.restaurant_detail_num_critical_issue, currentInspection.getNumCritical()));

            // fill the non-critical issues
            TextView textViewNonCriticalIssues = itemView.findViewById(R.id.InspectionNonCriticalIssues);
            textViewNonCriticalIssues.setText(getString(R.string.restaurant_detail_num_non_critical_issue, currentInspection.getNumNonCritical()));

            // fill the time
            TextView textViewTime = itemView.findViewById(R.id.time);

            Date currentDate = new Date();
            Date pastDate = currentInspection.getInspectionDate();

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

            return itemView;
        }
    }
}


