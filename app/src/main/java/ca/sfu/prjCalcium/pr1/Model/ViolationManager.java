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

public class ViolationManager implements Iterable<Violation> {

    private static ViolationManager instance;
    private List<Violation> violations = new ArrayList<>();

    private ViolationManager() {
    }

    public static ViolationManager getInstance() {
        if (instance == null) {
            instance = new ViolationManager();
        }

        return instance;
    }

    public void readFromTxt(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.allviolations);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("utf-8")) //default
        );

        String line;
        try {
            //Skip headers
            reader.readLine();
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                //split token
                String[] token = line.split(",");

                //read
                Violation v = new Violation(Integer.parseInt(token[0]), token[1], token[2], token[3]);
                violations.add(v);

                Log.d("Activity", "Added: " + v);
            }

        } catch (IOException e) {
            Log.wtf("Activity", "Error Reading file");
        }
    }

    public Violation getViolation(int position) {
        return violations.get(position);
    }

    public List<Violation> getViolations() {
        return violations;
    }

    @Override
    public Iterator<Violation> iterator() {
        return violations.iterator();
    }
}
