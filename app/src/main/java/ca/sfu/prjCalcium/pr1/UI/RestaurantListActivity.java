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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.InspectionManager;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.R;

public class RestaurantListActivity extends AppCompatActivity {

    // Singleton
    private RestaurantManager manager = RestaurantManager.getInstance();
    private InspectionManager inspectionManager = InspectionManager.getInstance();

    public static Intent makeIntent(Context c) {
        return new Intent(c, RestaurantListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateListView();
        clickRestaurant();
    }

    private void populateListView() {
        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.listView);
        list.setAdapter(adapter);
    }

    private void clickRestaurant() {
        ListView list = findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Restaurant clickedRestaurant = manager.getRestaurants().get(position);
                String message = "You clicked # " + position + ", which is string: " + clickedRestaurant.toString();
                Toast.makeText(RestaurantListActivity.this, message, Toast.LENGTH_LONG).show();

                // Launch the restaurant detail activity
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
            Restaurant currentRestaurant = manager.getRestaurants().get(position);
            Inspection currentInspection = inspectionManager.getInspections().get(position);

            // fill the restaurant icon

            // fill the name
            TextView textViewName = itemView.findViewById(R.id.name);
            textViewName.setText(currentRestaurant.getRestaurantName());

            // fill the number of issues
            TextView textViewIssues = itemView.findViewById(R.id.numOfIssues);
            textViewIssues.setText(currentInspection.getNumCritical() + currentInspection.getNumNonCritical());

            // fill the time
            TextView textViewTime = itemView.findViewById(R.id.time);
            textViewTime.setText(currentInspection.getInspectionDate());

            // fill the hazard icon
            ImageView imageViewHazard = itemView.findViewById(R.id.hazard);
//            imageViewHazard.setImageResource();

            return itemView;
        }
    }
}
