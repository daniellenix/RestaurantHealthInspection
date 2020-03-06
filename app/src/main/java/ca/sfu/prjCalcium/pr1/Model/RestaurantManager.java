package ca.sfu.prjCalcium.pr1.Model;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.sfu.prjCalcium.pr1.R;

public class RestaurantManager implements Iterable<Restaurant> {

    private static RestaurantManager instance;
    private List<Restaurant> restaurants = new ArrayList<>();

    private RestaurantManager() {
    }

    public static RestaurantManager getInstance() {
        if (instance == null) {
            instance = new RestaurantManager();
        }

        return instance;
    }

    public void readRestaurantData(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.restaurants_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("utf-8"))
        );

        String line = "";
        try {

            reader.readLine();

            while ((line = reader.readLine()) != null) {
                //Split by ","
                String[] tokens = line.split(",");

                //Read data
                Restaurant sample = new Restaurant();
                sample.setTrackingNumber(tokens[0]);
                sample.setRestaurantName(tokens[1]);
                sample.setAddress(tokens[2]);
                sample.setPhysicalCity(tokens[3]);
                sample.setFacType(tokens[4]);
                sample.setLatitude(Double.parseDouble(tokens[5]));
                sample.setLongitude(Double.parseDouble(tokens[6]));
                restaurants.add(sample);

                Log.d("MyActivity", "Just Created" + sample);

            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
    }

    public Restaurant getRestaurant(int position) {
        return restaurants.get(position);
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurants.iterator();
    }
}
