package ca.sfu.prjCalcium.pr1.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.Model.SearchResultList;
import ca.sfu.prjCalcium.pr1.R;


/*
 * Represent the initial screen's logic structure.
 */
public class RestaurantListActivity extends AppCompatActivity {
    public static final int RESTAURANT_LIST_ACTIVITY_SOURCE_ACTIVITY_COND = 10056;

    // Singleton
    private RestaurantManager manager = RestaurantManager.getInstance();
    private SearchResultList searchResultList = SearchResultList.getInstance();

    public static Intent makeIntent(Context c) {
        return new Intent(c, RestaurantListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapViewBtn();
        searchBtn();
        populateListView();
        clickRestaurant();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateListView();
    }

    private void searchBtn() {
        Button searchBtn = findViewById(R.id.listSearchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SearchActivity.makeIntent(RestaurantListActivity.this, RESTAURANT_LIST_ACTIVITY_SOURCE_ACTIVITY_COND);
                startActivity(intent);
            }
        });
    }

    //https://gist.github.com/CreatorB/99cdb013a4888453b8a0
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }

    private void mapViewBtn() {
        Button mapBtn = findViewById(R.id.mapBtn);

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MapsActivity.makeIntentWithSourceActivity(RestaurantListActivity.this, RESTAURANT_LIST_ACTIVITY_SOURCE_ACTIVITY_COND);
                startActivity(intent);
            }
        });
    }

    private void populateListView() {
        // if coming from search page, load search results
        if (searchResultList.isSearched()) {
            ArrayAdapter<Restaurant> adapter = new MyListAdapter(searchResultList.getSearchResult());
            ListView list = findViewById(R.id.restaurantListView);
            list.setAdapter(adapter);
        } else {
            ArrayAdapter<Restaurant> adapter = new MyListAdapter(manager.getRestaurantsAsLists());
            ListView list = findViewById(R.id.restaurantListView);
            list.setAdapter(adapter);
        }

    }

    private void clickRestaurant() {
        ListView list = findViewById(R.id.restaurantListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = RestaurantDetailActivity.makeIntent(RestaurantListActivity.this, position);
                startActivity(intent);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<Restaurant> {

        public MyListAdapter(List<Restaurant> restaurantList) {
            super(RestaurantListActivity.this, R.layout.restaurant_list, restaurantList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            // make sure we have a view to work with
            View itemView = convertView;

            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurant_list, parent, false);
            }

            // find the restaurant to work with
            Restaurant currentRestaurant;

            // load specific list based on where it came from
            if (searchResultList.isSearched()) {
                currentRestaurant = searchResultList.getRestaurantAtIndex(position);
            } else {
                currentRestaurant = manager.getRestaurantAtIndex(position);
            }

            ImageView imageViewIcon = itemView.findViewById(R.id.icon);

            // images found from https://www.flaticon.com/
            if(currentRestaurant.getRestaurantName().contains("Papa John's")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.papajohns));
            } else if (currentRestaurant.getRestaurantName().contains("Pizza Hut")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.pizzahut));
            } else if (currentRestaurant.getRestaurantName().contains("Pizza") ||
                    currentRestaurant.getRestaurantName().contains("Panago")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.pizza));
            } else if (currentRestaurant.getRestaurantName().contains("7-Eleven")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.seveneleven));
            } else if (currentRestaurant.getRestaurantName().contains("Pearl") ||
                    currentRestaurant.getRestaurantName().contains("Chatime") ||
                    currentRestaurant.getRestaurantName().contains("Bubble")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.bubbletea));
            } else if (currentRestaurant.getRestaurantName().contains("McDonald's")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.brand));
            } else if (currentRestaurant.getRestaurantName().contains("Starbucks")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.starbucks));
            } else if (currentRestaurant.getRestaurantName().contains("Coffee") ||
                    currentRestaurant.getRestaurantName().contains("Cafe") ||
                    currentRestaurant.getRestaurantName().contains("Blenz")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.coffee));
            } else if (currentRestaurant.getRestaurantName().contains("Tim Hortons") ||
                    currentRestaurant.getRestaurantName().contains("Tim Horton's")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.timhortons));
            } else if (currentRestaurant.getRestaurantName().contains("Sushi") ||
                    currentRestaurant.getRestaurantName().contains("Japanese")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.sushi));
            } else if (currentRestaurant.getRestaurantName().contains("Pho")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.noodles));
            } else if (currentRestaurant.getRestaurantName().contains("Subway")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.subway));
            } else if (currentRestaurant.getRestaurantName().contains("Sub") ||
                    currentRestaurant.getRestaurantName().contains("Sandwich") ||
                    currentRestaurant.getRestaurantName().contains("Quizno's")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.sandwich));
            } else if (currentRestaurant.getRestaurantName().contains("Burger")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.burger));
            } else if (currentRestaurant.getRestaurantName().contains("A&W") ||
                    currentRestaurant.getRestaurantName().contains("A & W")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.aw));
            } else if (currentRestaurant.getRestaurantName().contains("Dairy Queen")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.dairyqueen));
            } else if (currentRestaurant.getRestaurantName().contains("Ice Cream")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.icecream));
            } else if (currentRestaurant.getRestaurantName().contains("Grill")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.bbq));
            } else if (currentRestaurant.getRestaurantName().contains("Noodle") ||
                    currentRestaurant.getRestaurantName().contains("Ramen")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.japan));
            } else if (currentRestaurant.getRestaurantName().contains("Juice")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.juice));
            } else if (currentRestaurant.getRestaurantName().contains("Chicken") ||
                    currentRestaurant.getRestaurantName().contains("KFC") ||
                    currentRestaurant.getRestaurantName().contains("Meats") ||
                    currentRestaurant.getRestaurantName().contains("BBQ") ||
                    currentRestaurant.getRestaurantName().contains("Meat")){
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.chicken));
            } else if (currentRestaurant.getRestaurantName().contains("Sweets") ||
                    currentRestaurant.getRestaurantName().contains("Sweet")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.candy));
            } else if (currentRestaurant.getRestaurantName().contains("Pub")) {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.beer));
            } else {
                imageViewIcon.setImageDrawable(getDrawable(R.drawable.fork));
            }

            // fill the name
            TextView textViewName = itemView.findViewById(R.id.name);
            textViewName.setText(currentRestaurant.getRestaurantName());

            TextView textViewTime = itemView.findViewById(R.id.time);
            TextView textViewIssues = itemView.findViewById(R.id.numOfIssues);
            ImageView imageViewHazard = itemView.findViewById(R.id.hazard);

            ImageView star = itemView.findViewById(R.id.listIfFavStar);
            if (currentRestaurant.isFaved()) {
                star.setImageDrawable(getDrawable(R.drawable.ic_star_black_filled_24dp));
            } else {
                star.setImageDrawable(getDrawable(R.drawable.ic_star_border_black_24dp));
            }

            if (currentRestaurant.getInspections().isEmpty()) {
                textViewTime.setText(R.string.no_recent_inspections);
                textViewIssues.setText(getString(R.string.restaurant_list_num_issues, 0));
                textViewIssues.setTextColor(getResources().getColor(R.color.green));
                imageViewHazard.setImageDrawable(getDrawable(R.drawable.green));
            } else {
                Inspection firstInspection = currentRestaurant.getInspections().getInspection(0);

                int totalIssues = firstInspection.getNumCritical() + firstInspection.getNumNonCritical();

                textViewIssues.setText(getString(R.string.restaurant_list_num_issues, totalIssues));

                //Display date in intelligent format
                Date currentDate = new Date();
                Date pastDate = firstInspection.getInspectionDate();

                long dateDifference = TimeUnit.MILLISECONDS.toDays(currentDate.getTime() - pastDate.getTime());

                if (dateDifference < 30) {
                    textViewTime.setText(getString(R.string.inspection_days_ago, dateDifference));
                } else if (dateDifference > 30 && dateDifference <= 366) {
                    SimpleDateFormat mmmDdFormatter = new SimpleDateFormat("MMM dd", Locale.CANADA);
                    textViewTime.setText(mmmDdFormatter.format(pastDate));
                } else {
                    SimpleDateFormat mmmYYYYFormatter = new SimpleDateFormat("MMM yyyy", Locale.CANADA);
                    textViewTime.setText(mmmYYYYFormatter.format(pastDate));
                }

                if (firstInspection.getHazardRating().equals("Low")) {
                    imageViewHazard.setImageDrawable(getDrawable(R.drawable.green));
                    textViewIssues.setTextColor(getResources().getColor(R.color.green));
                }

                if (firstInspection.getHazardRating().equals("Moderate")) {
                    imageViewHazard.setImageDrawable(getDrawable(R.drawable.yellow));
                    textViewIssues.setTextColor(getResources().getColor(R.color.yellow));
                }

                if (firstInspection.getHazardRating().equals("High")) {
                    imageViewHazard.setImageDrawable(getDrawable(R.drawable.red));
                    textViewIssues.setTextColor(Color.RED);
                }
            }

            return itemView;
        }
    }
}