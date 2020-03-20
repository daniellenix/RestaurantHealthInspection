package ca.sfu.prjCalcium.pr1.Model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import ca.sfu.prjCalcium.pr1.R;

/**
 * Represent a list of restaurants.
 */
public class RestaurantManager implements Iterable<Restaurant> {

    private static RestaurantManager instance;
    private boolean isDataRead = false;

    private RestaurantManager() {
    }

    public boolean isDataRead() {
        return isDataRead;
    }

    private List<Restaurant> restaurants = new ArrayList<>();

    public void setDataRead(boolean dataRead) {
        isDataRead = dataRead;
    }

    public static RestaurantManager getInstance() {
        if (instance == null) {
            instance = new RestaurantManager();
        }

        return instance;
    }

    public void readRestaurantData(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.restaurants_itr1);

        // Trying to read data
//        InputStream fis = null;
//        File textFile = new File(Environment
//                .getExternalStorageDirectory().toString()
//                + "/test.csv");
//        try {
//            fis = new FileInputStream(textFile);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8)
        );

        String line = "";
        try {

            reader.readLine();

            while ((line = reader.readLine()) != null) {
                //Split by ","
                String[] tokens = line.split(",");

                //Read data
                Restaurant sample = new Restaurant();
                sample.setTrackingNumber(tokens[0].substring(1, tokens[0].length() - 1)); // Remove the quotation marks
                sample.setRestaurantName(tokens[1].substring(1, tokens[1].length() - 1));
                sample.setAddress(tokens[2].substring(1, tokens[2].length() - 1));
                sample.setPhysicalCity(tokens[3].substring(1, tokens[3].length() - 1));
                sample.setFacType(tokens[4].substring(1, tokens[4].length() - 1));
                sample.setLatitude(Double.parseDouble(tokens[5]));
                sample.setLongitude(Double.parseDouble(tokens[6]));
                sample.setInspections(context);
                restaurants.add(sample);

            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
    }

    public Restaurant getRestaurantAtIndex(int position) {
        return restaurants.get(position);
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void sort(Comparator<Restaurant> c) {
        Collections.sort(restaurants, c);
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurants.iterator();
    }
}
