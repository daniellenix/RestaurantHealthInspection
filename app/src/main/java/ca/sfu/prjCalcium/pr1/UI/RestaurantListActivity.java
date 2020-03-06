package ca.sfu.prjCalcium.pr1.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.InspectionManager;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.Model.RestaurantManager;
import ca.sfu.prjCalcium.pr1.R;

//reference Brian Fraser's video

public class RestaurantListActivity extends AppCompatActivity {

    RestaurantManager data = RestaurantManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Test if I get correct data in inspectionManager
//        InspectionManager test = new InspectionManager();
//        test.readInspectionData(getApplicationContext());
//        Toast.makeText(getApplicationContext(),test.getInspection(2).toString(), Toast.LENGTH_LONG).show();
        data.readRestaurantData(getApplicationContext());
//        Toast.makeText(getApplicationContext(), data.getRestaurant(2).toString(), Toast.LENGTH_LONG).show();
    }
}
