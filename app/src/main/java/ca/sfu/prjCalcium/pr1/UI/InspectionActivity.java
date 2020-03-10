package ca.sfu.prjCalcium.pr1.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import java.text.SimpleDateFormat;
import java.util.Locale;

import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.Model.Violation;
import ca.sfu.prjCalcium.pr1.Model.ViolationManager;
import ca.sfu.prjCalcium.pr1.R;

public class InspectionActivity extends AppCompatActivity {

    Restaurant r;
    Inspection i;

    // Singleton
    private RestaurantManager rManager;

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

        rManager = RestaurantManager.getInstance();

        extractDataFromIntent();
        setInformation();
        populateListView();
        clickViolation();
    }

    private void setInformation() {
        TextView date = findViewById(R.id.inspectionDate);

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.CANADA);

        date.setText(getString(R.string.inspection_date, formatter.format(i.getInspectionDate())));

        TextView hazardLevel = findViewById(R.id.InspectionHazardLevel);
        hazardLevel.setText(getString(R.string.restaurant_detail_hazard_level, i.getHazardRating()));

        TextView inspectionType = findViewById(R.id.inspectionType);
        inspectionType.setText(getString(R.string.inspection_inspection_type, i.getInspeType()));

        TextView numOfCriticalIssues = findViewById(R.id.InspectionCriticalIssues);
        numOfCriticalIssues.setText(getString(R.string.restaurant_detail_num_critical_issue, i.getNumCritical()));

        TextView numOfNonCriticalIssues = findViewById(R.id.InspectionNonCriticalIssues);
        numOfNonCriticalIssues.setText(getString(R.string.restaurant_detail_num_non_critical_issue, i.getNumNonCritical()));

        ImageView hazardImgView = findViewById(R.id.InspectionImageView);
        if (i.getHazardRating().equals("Low")) {
            hazardImgView.setImageDrawable(getDrawable(R.drawable.green));
            hazardLevel.setTextColor(getResources().getColor(R.color.green));
        }

        if (i.getHazardRating().equals("Moderate")) {
            hazardImgView.setImageDrawable(getDrawable(R.drawable.yellow));
            hazardLevel.setTextColor(getResources().getColor(R.color.yellow));
        }

        if (i.getHazardRating().equals("High")) {
            hazardImgView.setImageDrawable(getDrawable(R.drawable.red));
            hazardLevel.setTextColor(Color.RED);
        }
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

                Violation clickedViolation = i.getVioLump().getViolations().get(position);
                String message = clickedViolation.getDetails();
                Toast.makeText(InspectionActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();

        r = rManager.getRestaurantAtIndex(intent.getIntExtra(I_RESTAURANT_POSITION_PASSED_IN, 0));
        i = r.getInspections().getInspection(intent.getIntExtra(I_INSPECTION_POSITION_PASSED_IN, 0));
    }

    private class MyListAdapter extends ArrayAdapter<Violation> {

        public MyListAdapter() {
            super(InspectionActivity.this, R.layout.violation_list, i.getVioLump().getViolations());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            // make sure we have a view to work with
            View itemView = convertView;

            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.violation_list, parent, false);
            }

            // find the violation manager to work with
            ViolationManager vManager = i.getVioLump();

            if (vManager.isEmpty()) {
                return itemView;
            }

            Violation currentViolation = vManager.getViolations().get(position);

            // fill the violation type icon (pest, food, ..)
            ImageView imageViewNature = itemView.findViewById(R.id.natureOfViolation);

            if ((100 <= currentViolation.getCode() && currentViolation.getCode() <= 199)) {
                imageViewNature.setImageResource(R.drawable.regulations);
            } else if (200 <= currentViolation.getCode() && currentViolation.getCode() <= 299) {
                imageViewNature.setImageResource(R.drawable.food);
            } else if (300 <= currentViolation.getCode() && currentViolation.getCode() <= 399) {
                imageViewNature.setImageResource(R.drawable.settings);
            } else if (400 <= currentViolation.getCode() && currentViolation.getCode() <= 499) {
                imageViewNature.setImageResource(R.drawable.employee);
            } else {
                imageViewNature.setImageResource(R.drawable.operator);
            }

            // fill the short description
            TextView textViewShortDescription = itemView.findViewById(R.id.description);
            textViewShortDescription.setText(currentViolation.convertDetailsToCategories());

            // fill the severity icon - (critical or non-critical)
            ImageView imageViewSeverity = itemView.findViewById(R.id.severity);
            if (currentViolation.getCritical().equals("Not Critical")) {
                imageViewSeverity.setImageDrawable(getDrawable(R.drawable.yellow_x));
            } else {
                imageViewSeverity.setImageDrawable(getDrawable(R.drawable.red_x));
            }

            return itemView;
        }
    }
}
