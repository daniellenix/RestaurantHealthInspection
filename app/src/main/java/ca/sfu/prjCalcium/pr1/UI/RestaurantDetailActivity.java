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
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.R;

public class RestaurantDetailActivity extends AppCompatActivity {

    Restaurant r;
    private RestaurantManager manager;

    public static final String R_DETAIL_RESTAURANT_POSITION_PASSED_IN = "r_detail_restaurant_position_passed_in";


    public static Intent makeIntent(Context c, int restaurantPosition) {
        Intent intent = new Intent(c, RestaurantDetailActivity.class);

        intent.putExtra(R_DETAIL_RESTAURANT_POSITION_PASSED_IN, restaurantPosition);

        return intent;
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

        gpsView.setText("Longitude: " + r.getLongitude() + "\n" + "Latitude: " + r.getLatitude());
    }

    private void populateListView() {
        ArrayAdapter<Inspection> adapter = new RestaurantDetailActivity.MyListAdapter();
        ListView list = findViewById(R.id.restaurantInspectionListView);
        list.setAdapter(adapter);
    }

    private void clickInspection() {
        ListView list = findViewById(R.id.restaurantInspectionListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Inspection clickedInspection = r.getInspections().getInspection(position);
                String message = "You clicked # " + position + ", which is string: " + clickedInspection.toString();
                Toast.makeText(RestaurantDetailActivity.this, message, Toast.LENGTH_LONG).show();

                // Launch the inspection activity
            }
        });
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();

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

            // fill the hazard icon
            ImageView imageViewHazard = itemView.findViewById(R.id.hazard);
//            imageViewHazard.setImageResource();

            // fill the critical issues
            TextView textViewCriticalIssues = itemView.findViewById(R.id.criticalIssues);
            textViewCriticalIssues.setText("Critical Issues: " + currentInspection.getNumCritical());

            // fill the non-critical issues
            TextView textViewNonCriticalIssues = itemView.findViewById(R.id.nonCriticalIssues);
            textViewNonCriticalIssues.setText("Non-critical Issues: " + currentInspection.getNumNonCritical());

            // fill the time
            TextView textViewTime = itemView.findViewById(R.id.time);
            textViewTime.setText(currentInspection.getInspectionDate());

            return itemView;
        }
    }
}
