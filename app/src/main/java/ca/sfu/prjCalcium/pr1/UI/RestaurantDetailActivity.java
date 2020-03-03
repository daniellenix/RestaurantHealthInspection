package ca.sfu.prjCalcium.pr1.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ca.sfu.prjCalcium.pr1.R;

public class RestaurantDetailActivity extends AppCompatActivity {

    public static final String RESTAURANT_POSITION_PASSED_IN = "restaurant_position_passed_in";

    public static Intent makeIntent(Context c, int restaurantPosition) {
        Intent intent = new Intent(c, RestaurantDetailActivity.class);

        intent.putExtra(RESTAURANT_POSITION_PASSED_IN, restaurantPosition);
        
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
    }
}
