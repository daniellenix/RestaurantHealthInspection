package ca.sfu.prjCalcium.pr1.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import static ca.sfu.prjCalcium.pr1.UI.MapsActivity.INTENT_EXTRA_SOURCE_ACTIVITY_COND;
import static ca.sfu.prjCalcium.pr1.UI.MapsActivity.MAPS_ACTIVITY_SOURCE_ACTIVITY_COND;
import static ca.sfu.prjCalcium.pr1.UI.RestaurantListActivity.RESTAURANT_LIST_ACTIVITY_SOURCE_ACTIVITY_COND;

public class SearchActivity extends AppCompatActivity {

    public static final int SEARCH_ACTIVITY_SOURCE_ACTIVITY_COND_Cancel = 101;
    public static final int SEARCH_ACTIVITY_SOURCE_ACTIVITY_COND_SUBMIT = 102;

    private SearchResultList list = SearchResultList.getInstance();
    private String spinnerText;

    public static Intent makeIntent(Context c, int sourceActivityCondCode) {
        Intent intent = new Intent(c, SearchActivity.class);
        intent.putExtra(INTENT_EXTRA_SOURCE_ACTIVITY_COND, sourceActivityCondCode);

        return intent;
    }

    //https://stackoverflow.com/questions/38158953/how-to-create-button-in-action-bar-in-android
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mybutton) {
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

                if(!list.isEmpty()){
                    list.clear();
                }
                //https://developer.android.com/training/keyboard-input/style
                //textName and textViolation are the input from users
                EditText nameBox = findViewById(R.id.name_box);
                String textName = nameBox.getText().toString();

                EditText violationBox = findViewById(R.id.violations);
                String textViolations = violationBox.getText().toString();

                //save criteria to our restaurant list
                if ((textName.length() != 0) && (textViolations.length() != 0)){
                    list.getRestaurantsByName(textName);
                    int numberInput = Integer.parseInt(textViolations);

                    if (spinnerText.equals("less than")) {
                        list.getRestaurantsWithLessThanNCriticalViolationsWithinLastYear(numberInput);
                    }
                    else if (spinnerText.equals("greater than")){
                        list.getRestaurantsWithMoreThanNCriticalViolationsWithinLastYear(numberInput);
                    }
                }
                else if(textName.length() != 0){
                    list.getRestaurantsByName(textName);
                }
                else if(textViolations.length() != 0){
                    int numberInput = Integer.parseInt(textViolations);

                    if (spinnerText.equals("less than")) {
                        list.getRestaurantsWithLessThanNCriticalViolationsWithinLastYear(numberInput);
                    }
                    else if (spinnerText.equals("greater than")){
                        list.getRestaurantsWithMoreThanNCriticalViolationsWithinLastYear(numberInput);
                    }
                }

                //after we submit our search criteria, go back to the previous screen
                Intent i = getIntent();
                int code = i.getIntExtra(INTENT_EXTRA_SOURCE_ACTIVITY_COND, -1);
                if (code == 10157){
                    Intent intent = MapsActivity.makeIntent(SearchActivity.this, SEARCH_ACTIVITY_SOURCE_ACTIVITY_COND_SUBMIT);
                    startActivity(intent);
                }
                else if(code == 10056){
                    Intent intent = RestaurantListActivity.makeIntentForSearch(SearchActivity.this, SEARCH_ACTIVITY_SOURCE_ACTIVITY_COND_SUBMIT);
                    startActivity(intent);
                }

            }
        });

        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                Intent i = getIntent();
                int code = i.getIntExtra(INTENT_EXTRA_SOURCE_ACTIVITY_COND, -1);
                if (code == MAPS_ACTIVITY_SOURCE_ACTIVITY_COND){
                    Intent intent = MapsActivity.makeIntent(SearchActivity.this, SEARCH_ACTIVITY_SOURCE_ACTIVITY_COND_Cancel);
                    startActivity(intent);
                }
                else if(code == RESTAURANT_LIST_ACTIVITY_SOURCE_ACTIVITY_COND){
                    Intent intent = RestaurantListActivity.makeIntentForSearch(SearchActivity.this, SEARCH_ACTIVITY_SOURCE_ACTIVITY_COND_Cancel);
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
        //get spinner value
        spinnerText = spinner.getSelectedItem().toString();

    }

    //Reference:https://www.youtube.com/watch?v=_yaP4etGKlU&feature=youtu.be
    private void createRadioButtons() {
        final RadioGroup hazardGroup = findViewById(R.id.hazardLevel);
        String[] hazardList= getResources().getStringArray(R.array.hazard_list);
        //create button for hazard level list
        for(int i = 0; i < hazardList.length; i++){
            String level = hazardList[i];
            RadioButton button1 = new RadioButton(this);
            button1.setText(level);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int idOfSelected = hazardGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = findViewById(idOfSelected);
                    String choice = radioButton.getText().toString();
                    // send users hazard level input
                    list.getRestaurantByMostRecentInspectionHazardLevel(choice);

                }
            });
            hazardGroup.addView(button1);
        }

        final RadioGroup favoriteGroup = findViewById(R.id.favorite);
        String[] favorList = getResources().getStringArray(R.array.favorite_list);
        //create button for displaying favorite restaurant or not
        for (int j = 0; j < favorList.length; j++){
            String favor = favorList[j];
            RadioButton button2 = new RadioButton(this);
            button2.setText(favor);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int idOfSelected = favoriteGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton) findViewById(idOfSelected);
                    String choice = radioButton.getText().toString();
                    // send users choice of displaying favorite restaurant or not
                    if (choice.equals("Yes")) {
                        list.getFavedRestaurants();
                    }
                }
            });
            favoriteGroup.addView(button2);
        }
    }
}
