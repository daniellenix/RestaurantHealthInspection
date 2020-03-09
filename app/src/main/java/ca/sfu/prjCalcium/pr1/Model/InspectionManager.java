package ca.sfu.prjCalcium.pr1.Model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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
 * Represent a list of inspections for a restaurant.
 */
public class InspectionManager implements Iterable<Inspection> {

    private List<Inspection> inspections = new ArrayList<>();

    public void addInspectionsByTrackingNumber(Context context, String restID) {
        InputStream is = context.getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("utf-8"))
        );

        String line = "";
        try {
            //Step over headers
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                //split by ","
                String[] tokens = line.split(",");

                if (tokens[0].substring(1, tokens[0].length() - 1).equals(restID)) { // Restaurant tracking number corresponds to the inspection
                    Inspection sample = new Inspection();

                    sample.setTrackingNumber(tokens[0].substring(1, tokens[0].length() - 1));

                    Date d = new SimpleDateFormat("yyyyMMdd", Locale.CANADA).parse(tokens[1]);

                    sample.setInspectionDate(d);
                    sample.setInspeType(tokens[2].substring(1, tokens[2].length() - 1));
                    sample.setNumCritical(Integer.parseInt(tokens[3]));
                    sample.setNumNonCritical(Integer.parseInt(tokens[4]));
                    sample.setHazardRating(tokens[5].substring(1, tokens[5].length() - 1));
                    if (tokens.length > 6 && tokens[6].length() > 0) {
                        String[] vioLumpStrArray = Arrays.copyOfRange(tokens, 6, tokens.length);
                        String vioLumpStr = TextUtils.join(",", vioLumpStrArray);
                        vioLumpStr = vioLumpStr.substring(1, vioLumpStr.length() - 1);

                        List<String> tokens_for_violations = new ArrayList<String>();

                        if (!vioLumpStr.contains("|")) {
                            tokens_for_violations.add(vioLumpStr);
                        } else {
                            tokens_for_violations = Arrays.asList(vioLumpStr.split("\\|")); // a list of violations
                        }

                        ViolationManager vManager = new ViolationManager();

                        for (String t : tokens_for_violations) {
                            String[] v_token = t.split(",");

                            Violation v = new Violation(Integer.parseInt(v_token[0]), v_token[1], v_token[2], v_token[3]);
                            vManager.add(v);
                        }

                        sample.setVioLump(vManager);

                    }
                    inspections.add(sample);
                }
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Inspection getInspection(int position) {
        return inspections.get(position);
    }

    public List<Inspection> getInspections() {
        return inspections;
    }

    public boolean isEmpty() {
        return inspections.isEmpty();
    }

    @Override
    public Iterator<Inspection> iterator() {
        return inspections.iterator();
    }

    public void sort(Comparator<Inspection> c) {
        Collections.sort(inspections, c);
    }
}
