package ca.sfu.prjCalcium.pr1.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.InspectionManager;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.R;

public class RestaurantDetailActivity extends AppCompatActivity {

    public static final String R_DETAIL_RESTAURANT_POSITION_PASSED_IN = "r_detail_restaurant_position_passed_in";

//    RestaurantManager rManager = RestaurantManager.getInstance();
    Restaurant r;

    public static Intent makeIntent(Context c, int restaurantPosition) {
        Intent intent = new Intent(c, RestaurantDetailActivity.class);

        intent.putExtra(R_DETAIL_RESTAURANT_POSITION_PASSED_IN, restaurantPosition);
        
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        extractDataFromIntent();
        populateRestaurantInfo();
        populateRestaurantInspectionList();
    }

    private void populateRestaurantInspectionList() {
        // TODO: placeholder function, wiring up the basic logic

//        ArrayAdapter<Inspection> inspectionArrayAdapter = new MyInspectionAdapter();
//
//        ListView inspectionList = findViewById(R.id.restaurantDetailInspectionList);
//
//        inspectionList.setAdapter(inspectionArrayAdapter);
//
//        inspectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                int restaurantIndex = rManager.getItemIndex(r);
//                Intent inspectionIntent = InspectionActivity.makeIntent(RestaurantDetailActivity.this, restaurantIndex, position);
//
//                startActivity(inspectionIntent);
//            }
//        });
    }

    private void populateRestaurantInfo() {
        // TODO: Not much to write here since we don't yet have the IDs to work with.
    }

    private void extractDataFromIntent() {
//        TODO:
//        Intent intent = getIntent();
//
//        r = RestaurantManager.getRestaurantByIndex(intent.getIntExtra(RESTAURANT_POSITION_PASSED_IN, 0));
    }

//    private class MyInspectionAdapter extends ArrayAdapter<Inspection> {
//        public MyInspectionAdapter() {
//            super(RestaurantDetailActivity.this, R.layout.inspection_item, currentR.getInspectionManager().listify());
//        }
//
//        @NonNull
//        @Override
//        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            View itemView = convertView;
//
//            if (itemView == null) {
//                itemView = getLayoutInflater().inflate(R.layout.inspection_item, parent, false);
//            }
//
//            // Find the inspection to work with
//            InspectionManager iManager = currentR.getInspectionManager();
//            Inspection i = iManager.getInspectionByIndex();
//
//            // TODO: Fill in the view
//
//            return itemView;
//        }
//    }
}
