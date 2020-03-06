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
import ca.sfu.prjCalcium.pr1.Model.Violation;
import ca.sfu.prjCalcium.pr1.Model.ViolationManager;
import ca.sfu.prjCalcium.pr1.R;

public class InspectionActivity extends AppCompatActivity {

    // Singleton
     private Violation violationManager = ViolationManager.getInstance();

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
        ArrayAdapter<Inspection> adapter = new InspectionActivity.MyListAdapter();
        ListView list = findViewById(R.id.listView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Inspection> {

        public MyListAdapter() {
            super(InspectionActivity.this, R.layout.violation_list, violationManager.getViolations());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            // make sure we have a view to work with
            View itemView = convertView;

            if(itemView == null) {
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

    // toast message when violation is clicked
    private void clickViolation() {
        ListView list = findViewById(R.id.listView);
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
}
