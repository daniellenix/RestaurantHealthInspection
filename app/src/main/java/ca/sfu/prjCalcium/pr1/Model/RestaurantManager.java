package ca.sfu.prjCalcium.pr1.Model;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ca.sfu.prjCalcium.pr1.R;

/**
 * Represent a list of restaurants.
 */
public class RestaurantManager implements Iterable<Restaurant> {

    private static RestaurantManager instance;
    private boolean isDataRead = false;
    private List<Restaurant> restaurants = new ArrayList<>();

    private RestaurantManager() {
    }

    public static RestaurantManager getInstance() {
        if (instance == null) {
            instance = new RestaurantManager();
        }

        return instance;
    }

    public boolean isDataRead() {
        return isDataRead;
    }

    public void setDataRead(boolean dataRead) {
        isDataRead = dataRead;
    }

    public void readRestaurantDataFromExternal() {
        // Trying to read data
        InputStream fis = null;
        File textFile = new File(Environment
                .getExternalStorageDirectory().toString()
                + "/restaurant.csv");
        try {
            fis = new FileInputStream(textFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(fis, StandardCharsets.UTF_8)
        );

        String line = "";
        try {
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                //Split by ","
                String[] tokens = line.split(",");

                //Read data
                Restaurant sample = new Restaurant();
                sample.setTrackingNumber(tokens[0]); // Remove the quotation marks

                if (tokens[1].indexOf('\"') >= 0) { // if the restaurant name has comma, there should be a quotation mark
                    int i = 1;
                    StringBuilder restName = new StringBuilder();
                    while (tokens[i].indexOf('\"') != -1) {
                        restName.append(tokens[i]); // concat the string until the ending quotation mark
                        i++;
                    }
                    sample.setRestaurantName(restName.toString());
                    sample.setAddress(tokens[i]);
                    sample.setPhysicalCity(tokens[++i]);
                    sample.setFacType(tokens[++i]);
                    sample.setLatitude(Double.parseDouble(tokens[++i]));
                    sample.setLongitude(Double.parseDouble(tokens[++i]));
                } else {
                    sample.setRestaurantName(tokens[1]);
                    sample.setAddress(tokens[2]);
                    sample.setPhysicalCity(tokens[3]);
                    sample.setFacType(tokens[4]);
                    sample.setLatitude(Double.parseDouble(tokens[5]));
                    sample.setLongitude(Double.parseDouble(tokens[6]));
                }
                sample.setInspections(new InspectionManager());
                restaurants.add(sample);
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
    }

    public void readRestaurantDataFromInternal(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.restaurants_itr1);
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

    public Restaurant getRestaurantByTrackingNumber(String trackingNumber) {
        for (Restaurant r : restaurants) {
            if (r.getTrackingNumber().equals(trackingNumber)) {
                return r;
            }
        }

        return null;
    }

    public int getRestaurantIndexByLatLng(LatLng latLng) {
        for (Restaurant r : restaurants) {
            LatLng currentRLatLng = new LatLng(r.getLatitude(), r.getLongitude());
            if (latLng.equals(currentRLatLng)) {
                return restaurants.indexOf(r);
            }
        }

        return -1;
    }

    public void sort(Comparator<Restaurant> c) {
        Collections.sort(restaurants, c);
    }

    public void addInspectionsToRestaurantsFromExternal() {
        InputStream fis = null;
        File textFile = new File(Environment
                .getExternalStorageDirectory().toString()
                + "/inspection.csv");
        try {
            fis = new FileInputStream(textFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(fis, StandardCharsets.UTF_8)
        );

        String line = "";

        try {
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");

                if (tokens.length == 0) continue;

                Inspection inspection = new Inspection();
                Restaurant r = getRestaurantByTrackingNumber(tokens[0]);
                if (r == null) {
                    Log.e("R", "Restaurant is not found in the data base, skip. ");
                    continue;
                }

                InspectionManager currentRInspectionManager = r.getInspections();

                inspection.setTrackingNumber(tokens[0]);

                if (tokens[1].length() != 8) {
                    Log.e("R", "Restaurant date is not parsable. ");
                    continue;
                }

                Date d = new SimpleDateFormat("yyyyMMdd", Locale.CANADA).parse(tokens[1]);

                inspection.setInspectionDate(d);
                inspection.setInspeType(tokens[2]);
                inspection.setNumCritical(Integer.parseInt(tokens[3]));
                inspection.setNumNonCritical(Integer.parseInt(tokens[4]));

                if (tokens.length <= 5) {
                    inspection.setHazardRating("N/A");
                    currentRInspectionManager.add(inspection);
                    continue;
                }

                if (tokens[5].length() > 0) {
                    String[] vioLumpStrArray = Arrays.copyOfRange(tokens, 5, tokens.length - 1);
                    String vioLumpStr = TextUtils.join(",", vioLumpStrArray);
                    vioLumpStr = vioLumpStr.replace("\"", "");

                    List<String> tokens_for_violations = new ArrayList<String>();

                    if (!vioLumpStr.contains("|")) {
                        tokens_for_violations.add(vioLumpStr);
                    } else {
                        tokens_for_violations = Arrays.asList(vioLumpStr.split("\\|")); // a list of violations
                    }

                    ViolationManager vManager = new ViolationManager();

                    for (String t : tokens_for_violations) {
                        String[] v_token = t.split(",");

                        if (v_token.length > 4) {
                            String[] vDetail = Arrays.copyOfRange(v_token, 2, v_token.length - 1);
                            String vDetailStr = TextUtils.join(",", vDetail);
                            Violation v = new Violation(Integer.parseInt(v_token[0]), v_token[1], vDetailStr, v_token[v_token.length - 1]);
                            vManager.add(v);
                        } else if (v_token.length < 4) {
                            if (v_token.length < 3) {
                                Log.e("R", "V_token incorrect length. ");
                                continue;
                            }
                            Violation v = new Violation(Integer.parseInt(v_token[0]), v_token[1], v_token[2], "N/A");
                            vManager.add(v);
                        } else {
                            Violation v = new Violation(Integer.parseInt(v_token[0]), v_token[1], v_token[2], v_token[3]);
                            vManager.add(v);
                        }
                    }
                    inspection.setVioLump(vManager);
                }

                inspection.setHazardRating(tokens[tokens.length - 1]);
                currentRInspectionManager.add(inspection);
            }

        } catch (IOException | ParseException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
    }

    public void clear() {
        restaurants.clear();
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurants.iterator();
    }

    public List<Restaurant> getRestaurantsAsLists() {
        return restaurants;
    }
}
