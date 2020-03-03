package ca.sfu.prjCalcium.pr1.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.RestrictionsManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.R;

public class RestaurantListActivity extends AppCompatActivity {

    public static Intent makeIntent(Context c) {
        return new Intent(c, RestaurantListActivity.class);
    }

//    placeholder code for getting the list of restaurants
//    RestaurantManager rManager = RestaurantManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateRestaurantList();
    }

    private void populateRestaurantList() {
//        TODO: This is a placeholder function for now, since right now we don't have the names of the views
//        this block of the code is pretty much just gonna be the logic, names can be easily changed
//        after the fact
//
//        Might need a custom ListAdapter to show all information required.
//        ArrayAdapter<Restaurant> restaurantArrayAdapter = new MyRestaurantAdapter();
//
//        ListView listRestaurant = findViewById(R.id.restaurantListActivityMainList);
//        listRestaurant.setAdapter(restaurantArrayAdapter);
//        listRestaurant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent rDetailIntent = RestaurantDetailActivity.makeIntent(RestaurantListActivity.this, position);
//                startActivity(rDetailIntent);
//            }
//        });
    }

//    private class MyRestaurantAdapter extends ArrayAdapter<Restaurant> {
//
//        public MyRestaurantAdapter() {
//            // RManager should be able to output as a list for the ArrayAdapter to work with
//            super(RestaurantListActivity.this, R.layout.restaurant_item, rManager.listify());
//        }
//
//        @NonNull
//        @Override
//        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            View itemView = convertView;
//
//            if (itemView == null) {
//                itemView = getLayoutInflater().inflate(R.layout.restaurant_item, parent, false);
//            }
//
//            // Find the Restaurant to work with
//            Restaurant r = rManager.getRestaurantByIndex(position);
//
//            // TODO: Fill the View
//
//            return itemView;
//        }
//    }
}
