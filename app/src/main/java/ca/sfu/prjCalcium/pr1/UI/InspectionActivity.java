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

import ca.sfu.prjCalcium.pr1.Model.Violation;
import ca.sfu.prjCalcium.pr1.Model.ViolationManager;
import ca.sfu.prjCalcium.pr1.R;

public class InspectionActivity extends AppCompatActivity {

    // Singleton
    private ViolationManager violationManager = ViolationManager.getInstance();

    private static final String I_INSPECTION_POSITION_PASSED_IN = "i_inspection_position_passed_in";
    private static final String I_RESTAURANT_POSITION_PASSED_IN = "i_restaurant_position_passed_in";

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

        setInformation();
        populateListView();
        clickViolation();
    }

    private void setInformation() {
        TextView date = findViewById(R.id.date);
        date.setText("Date: ");

        TextView hazardLevel = findViewById(R.id.hazardLevel);
        hazardLevel.setText("Hazard level: ");

        TextView inspectionType = findViewById(R.id.inspectionType);
        inspectionType.setText("Inspection Type: ");

        TextView numOfCriticalIssues = findViewById(R.id.criticalIssues);
        numOfCriticalIssues.setText("# Of Critical Issues: ");

        TextView numOfNonCriticalIssues = findViewById(R.id.nonCriticalIssues);
        numOfNonCriticalIssues.setText("# Of Non Critical Issues: ");
    }

    private void populateListView() {
        ArrayAdapter<Violation> adapter = new InspectionActivity.MyListAdapter();
        ListView list = findViewById(R.id.violationListView);
        list.setAdapter(adapter);
    }

    // toast message when violation is clicked
    private void clickViolation() {
        ListView list = findViewById(R.id.violationListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Violation clickedViolation = violationManager.getViolations().get(position);
                String message = "Long description: " + clickedViolation.getDetails();
                Toast.makeText(InspectionActivity.this, message, Toast.LENGTH_LONG).show();

                // Launch the inspection activity
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<Violation> {

        public MyListAdapter() {
            super(InspectionActivity.this, R.layout.violation_list, violationManager.getViolations());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            // make sure we have a view to work with
            View itemView = convertView;

            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.violation_list, parent, false);
            }

            // find the violation to work with
            Violation currentViolation = violationManager.getViolations().get(position);

            // fill the violation type icon (pest, food, ..)
            ImageView imageViewNature = itemView.findViewById(R.id.natureOfViolation);
            imageViewNature.setImageResource(currentViolation.getCode());

            // fill the short description
            TextView textViewShortDescription = itemView.findViewById(R.id.description);
            textViewShortDescription.setText(currentViolation.getDetails());

            // fill the severity icon - (critical or non-critical)
            ImageView imageViewSeverity = itemView.findViewById(R.id.severity);
//            imageViewSeverity.setImageResource();

            return itemView;
        }
    }

    private void extractDataFromIntent() {
        // TODO:
//        Intent intent = getIntent();
//
//        r = rManager.getRestaurantByIndex(intent.getIntExtra(I_RESTAURANT_POSITION_PASSED_IN, 0));
//        i = r.getInspectionManager().getInspectionByIndex(intent.getIntExtra(I_INSPECTION_POSITION_PASSED_IN, 0));
    }
}
