package ca.sfu.prjCalcium.pr1.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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

    // Singleton
    private RestaurantManager manager = RestaurantManager.getInstance();

    public static Intent makeIntent(Context c) {
        return new Intent(c, RestaurantListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        long now = System.currentTimeMillis();

        if (!manager.isDataRead()) {
            manager.readRestaurantData(RestaurantListActivity.this);

            manager.sort(new AlphabetComparator());

            for (Restaurant r : manager) {
                InspectionManager iManager = r.getInspections();

                iManager.sort(new InspectionComparator().reversed());
            }

            manager.setDataRead(true);
        }

        populateListView();
        clickRestaurant();

        // Test code
        //clearSharedReferencesData();
        //checkTestTime(now);
        checkTime(now);
    }

    private void checkTime(long now) {
        long last = getLastStartTime();
        // 20 hours = 72000000 milliseconds
        if (now - last >= 72000000) {
            Toast.makeText(getApplicationContext(), "Need Update!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Up to date!", Toast.LENGTH_LONG).show();
        }
        saveStartTime(now);
    }

    private long getLastStartTime() {
        SharedPreferences pref = getSharedPreferences("Time", MODE_PRIVATE);
        long time = pref.getLong("LastRun", 0);
        return time;
    }

    private void saveStartTime(long now) {
        SharedPreferences pref = getSharedPreferences("Time", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("LastRun", now);
        editor.commit();
    }

    // For testing only
    private void clearSharedReferencesData() {
        SharedPreferences pref = getSharedPreferences("Time", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    // For testing only
    private void setSharedReferencesData(long time) {
        SharedPreferences pref = getSharedPreferences("Time", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("LastRun", time);
        editor.commit();
    }

    // For testing only
    private void checkTestTime(long now) {
        clearSharedReferencesData();
        setSharedReferencesData(now - 72000000); // launch time - 20 hours
        long test = getLastStartTime();
        checkTime(now);
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
            super(RestaurantListActivity.this, R.layout.restaurant_list, manager.getRestaurants());
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
}
