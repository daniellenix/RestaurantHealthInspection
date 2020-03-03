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
import android.widget.ListView;

import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.InspectionManager;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.R;

public class InspectionActivity extends AppCompatActivity {

    private static final String I_INSPECTION_POSITION_PASSED_IN = "i_inspection_position_passed_in";
    private static final String I_RESTAURANT_POSITION_PASSED_IN = "i_restaurant_position_passed_in";

    // RestaurantManager rManager = RestaurantManager.getInstance();
    Restaurant r;
    Inspection i;

    public static Intent makeIntent(Context c, int restaurantIndex, int restaurantInspectionIndex) {
        Intent intent = new Intent(c, InspectionActivity.class);

        intent.putExtra(I_RESTAURANT_POSITION_PASSED_IN, restaurantIndex);
        intent.putExtra(I_INSPECTION_POSITION_PASSED_IN, restaurantInspectionIndex);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
        
        extractDataFromIntent();
        populateInspectionDetail();
        populateViolationList();
    }

    private void populateViolationList() {
        // TODO: placeholder function, wiring up the basic logic

//        ArrayAdapter<Violation> violationArrayAdapter = new MyViolationAdapter();
//
//        ListView violationList = findViewById(R.id.violationsInspectionList);
//
//        violationList.setAdapter(violationArrayAdapter);
//
//        violationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // TODO: Display long description
//            }
//        });
    }

    private void populateInspectionDetail() {
        // TODO: Not much to write here since we don't yet have the IDs to work with.
    }

    private void extractDataFromIntent() {
        // TODO:
//        Intent intent = getIntent();
//
//        r = rManager.getRestaurantByIndex(intent.getIntExtra(I_RESTAURANT_POSITION_PASSED_IN, 0));
//        i = r.getInspectionManager().getInspectionByIndex(intent.getIntExtra(I_INSPECTION_POSITION_PASSED_IN, 0));
    }

//    private class MyViolationAdapter extends ArrayAdapter<Violation> {
//        public MyViolationAdapter() {
//            super(InspectionActivity.this, R.layout.violation_item, i.getViolationManager().listify());
//        }
//
//        @NonNull
//        @Override
//        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            View itemView = convertView;
//
//            if (itemView == null) {
//                itemView = getLayoutInflater().inflate(R.layout.violation_item, parent, false);
//            }
//
//            // Find the inspection to work with
//            ViolationManager vManager = i.getInspectionManager();
//            Violation v = vManager.getViolationByIndex();
//
//            // TODO: Fill in the view
//
//            return itemView;
//        }
//    }
}
