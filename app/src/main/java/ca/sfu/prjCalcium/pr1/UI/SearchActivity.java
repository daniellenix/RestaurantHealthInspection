package ca.sfu.prjCalcium.pr1.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import ca.sfu.prjCalcium.pr1.Model.SearchResultList;
import ca.sfu.prjCalcium.pr1.R;

public class SearchActivity extends AppCompatActivity {
    public static final String SEARCH_INTENT_EXTRA_SOURCE_ACTIVITY_COND = "searchActivitySourceActivityCondition";

    private SearchResultList list = SearchResultList.getInstance();

    public static Intent makeIntent(Context c, int sourceActivityCondCode) {
        Intent intent = new Intent(c, SearchActivity.class);
        intent.putExtra(SEARCH_INTENT_EXTRA_SOURCE_ACTIVITY_COND, sourceActivityCondCode);

        return intent;
    }

    // https://stackoverflow.com/questions/38158953/how-to-create-button-in-action-bar-in-android
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.myButton) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                list.clear();

                // https://developer.android.com/training/keyboard-input/style
                // textName and textViolation are the input from users
                EditText nameBox = findViewById(R.id.name_box);
                String textName = nameBox.getText().toString();

                EditText violationBox = findViewById(R.id.violations);
                String textViolations = violationBox.getText().toString();

                Spinner spinner = findViewById(R.id.spinner);
                int spinnerSelectedPos = spinner.getSelectedItemPosition();

                //save criteria to our restaurant list
                if ((textName.length() != 0) && (textViolations.length() != 0)) {
                    list.getRestaurantsByName(textName);
                    int numberInput = Integer.parseInt(textViolations);

                    if (spinnerSelectedPos == 0) {
                        list.getRestaurantsWithLessThanNCriticalViolationsWithinLastYear(numberInput);
                    }
                    else if (spinnerSelectedPos == 1) {
                        list.getRestaurantsWithMoreThanNCriticalViolationsWithinLastYear(numberInput);
                    }
                }
                else if (textName.length() != 0) {
                    list.getRestaurantsByName(textName);
                }
                else if (textViolations.length() != 0) {
                    int numberInput = Integer.parseInt(textViolations);

                    if (spinnerSelectedPos == 0) {
                        list.getRestaurantsWithLessThanNCriticalViolationsWithinLastYear(numberInput);
                    }
                    else if (spinnerSelectedPos == 1) {
                        list.getRestaurantsWithMoreThanNCriticalViolationsWithinLastYear(numberInput);
                    }
                }

                RadioGroup hazardGroup = findViewById(R.id.hazardLevel);

                int hazardGroupCheckedID = hazardGroup.getCheckedRadioButtonId();
                RadioButton hazardGroupRadioButton = findViewById(hazardGroupCheckedID);
                if (hazardGroupRadioButton != null) {
                    String choice = hazardGroupRadioButton.getText().toString();
                    // send users hazard level input
                    list.getRestaurantByMostRecentInspectionHazardLevel(choice);
                }

                RadioGroup favoriteGroup = findViewById(R.id.favorite);
                int favGroupCheckedID = favoriteGroup.getCheckedRadioButtonId();
                RadioButton FavGroupRadioButton = findViewById(favGroupCheckedID);
                int radioButtonNumber = favoriteGroup.indexOfChild(FavGroupRadioButton);
                // send users choice of displaying favorite restaurant or not
                if (radioButtonNumber == 0) {
                    list.getFavedRestaurants();
                }

                // after we submit our search criteria, go back to the previous screen
                Intent i = getIntent();
                int code = i.getIntExtra(SEARCH_INTENT_EXTRA_SOURCE_ACTIVITY_COND, -1);

                if (code == MapsActivity.MAPS_ACTIVITY_SOURCE_ACTIVITY_COND) {
                    Intent intent = MapsActivity.makeIntent(SearchActivity.this);
                    startActivity(intent);
                }
                else if (code == RestaurantListActivity.RESTAURANT_LIST_ACTIVITY_SOURCE_ACTIVITY_COND) {
                    Intent intent = RestaurantListActivity.makeIntent(SearchActivity.this);
                    startActivity(intent);
                } else {
                    Log.e("SearchActivity", "onClick: source activity condition code unexpected. ");
                }
            }
        });

        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                Intent i = getIntent();
                int code = i.getIntExtra(SEARCH_INTENT_EXTRA_SOURCE_ACTIVITY_COND, -1);
                if (code == MapsActivity.MAPS_ACTIVITY_SOURCE_ACTIVITY_COND){
                    Intent intent = MapsActivity.makeIntent(SearchActivity.this);
                    startActivity(intent);
                } else if (code == RestaurantListActivity.RESTAURANT_LIST_ACTIVITY_SOURCE_ACTIVITY_COND){
                    Intent intent = RestaurantListActivity.makeIntent(SearchActivity.this);
                    startActivity(intent);
                }
            }
        });
        createRadioButtons();
        setUpSpinners();
    }

    //https://developer.android.com/guide/topics/ui/controls/spinner
    private void setUpSpinners() {
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.range_list, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    //Reference:https://www.youtube.com/watch?v=_yaP4etGKlU&feature=youtu.be
    private void createRadioButtons() {
        RadioGroup hazardGroup = findViewById(R.id.hazardLevel);
        String[] hazardList= getResources().getStringArray(R.array.hazard_list);
        //create button for hazard level list
        for (String level : hazardList) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(level);
            hazardGroup.addView(radioButton);
        }
        hazardGroup.check(hazardGroup.getChildAt(3).getId());

        RadioGroup favoriteGroup = findViewById(R.id.favorite);
        String[] favorList = getResources().getStringArray(R.array.favorite_list);
        //create button for displaying favorite restaurant or not
        for (String favor : favorList) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(favor);
            favoriteGroup.addView(radioButton);
        }
        favoriteGroup.check(favoriteGroup.getChildAt(1).getId());
    }
}
