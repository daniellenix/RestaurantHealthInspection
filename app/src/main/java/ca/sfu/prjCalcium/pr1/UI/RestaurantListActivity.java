package ca.sfu.prjCalcium.pr1.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.R;

public class RestaurantListActivity extends AppCompatActivity {

    // Singleton
    // private RestaurantManager manager = RestaurantManager.getInstance();

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

    private class MyListAdapter extends ArrayAdapter<Restaurant> {

        public MyListAdapter() {
            super(RestaurantListActivity.this, R.layout.restaurant_list, manager.getRestaurants());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            // make sure we have a view to work with
            View itemView = convertView;

            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurant_list, parent, false);
            }

            // find the lens to work with
            Restaurant currentRestaurant = manager.getLenses().get(position);

            // fill the restaurant icon
            ImageView imageView = itemView.findViewById(R.id.icon);
            imageView.setImageResource(currentRestaurant.getIconID());

            // fill the name
            TextView textViewName = itemView.findViewById(R.id.name);
            textViewName.setText(currentRestaurant.getName());

            // fill the number of issues
            TextView textViewIssues = itemView.findViewById(R.id.numOfIssues);
            textViewIssues.setText(currentRestaurant.getIssue());

            // fill the hazard icon
            TextView textViewTime = itemView.findViewById(R.id.time);
            textViewTime.setText(currentRestaurant.getTime());


            return itemView;
        }
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
}
