package ca.sfu.prjCalcium.pr1.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.Comparator;

import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.InspectionManager;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.R;

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

        manager.readRestaurantData(RestaurantListActivity.this);
        manager.sort(new AlphabetComparator());

        populateListView();
        clickRestaurant();
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

                Restaurant clickedRestaurant = manager.getRestaurants().get(position);
                String message = "You clicked # " + position + ", which is string: " + clickedRestaurant.toString();

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
            InspectionManager restaurantInspections = currentRestaurant.getInspections();

            // fill the restaurant icon

            // fill the name
            TextView textViewName = itemView.findViewById(R.id.name);
            textViewName.setText(currentRestaurant.getRestaurantName());

            // fill the number of issues
            TextView textViewIssues = itemView.findViewById(R.id.numOfIssues);
            int totalIssues = 0;
            for (Inspection i : restaurantInspections) {
                totalIssues += i.getNumCritical() + i.getNumNonCritical();
            }
            textViewIssues.setText("Number of Issues: " + totalIssues);

            // fill the time
            TextView textViewTime = itemView.findViewById(R.id.time);
//            textViewTime.setText(currentInspection.getInspectionDate());

            // fill the hazard icon
            ImageView imageViewHazard = itemView.findViewById(R.id.hazard);
//            imageViewHazard.setImageResource();

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
}
