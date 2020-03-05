package ca.sfu.prjCalcium.pr1.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.R;

public class RestaurantDetailActivity extends AppCompatActivity {

    // Singleton
    // private RestaurantManager manager = RestaurantManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        populateListView();
        clickInspection();
    }

    private void populateListView() {
        ArrayAdapter<Inspection> adapter = new RestaurantDetailActivity.MyListAdapter();
        ListView list = findViewById(R.id.listView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Inspection> {

        public MyListAdapter() {
            super(RestaurantDetailActivity.this, R.layout.inspection_list, manager.getInspections());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            // make sure we have a view to work with
            View itemView = convertView;

            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.inspection_list, parent, false);
            }

            // find the inspection to work with
            Inspection currentInspection = manager.getInspections().get(position);

            // fill the hazard icon
            ImageView imageViewHazard = itemView.findViewById(R.id.hazard);
            imageViewHazard.setImageResource(currentInspection.getHazard());

            // fill the critical issues
            TextView textViewCriticalIssues = itemView.findViewById(R.id.criticalIssues);
            textViewCriticalIssues.setText(currentInspection.getCriticalIssues());

            // fill the non-critical issues
            TextView textViewNonCriticalIssues = itemView.findViewById(R.id.nonCriticalIssues);
            textViewNonCriticalIssues.setText(currentInspection.getNonCriticalIssues());

            // fill the time
            TextView textViewTime = itemView.findViewById(R.id.time);
            textViewTime.setText(currentInspection.getTime());

            return itemView;
        }
    }

    private void clickInspection() {
        ListView list = findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Inspection clickedInspection = manager.getInspections().get(position);
                String message = "You clicked # " + position + ", which is string: " + clickedInspection.toString();
                Toast.makeText(RestaurantDetailActivity.this, message, Toast.LENGTH_LONG).show();

                // Launch the inspection activity
            }
        });
    }
}
