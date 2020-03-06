package ca.sfu.prjCalcium.pr1.Model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.R;

public class InspectionManager implements Iterable<Inspection> {

    private List<Inspection> inspectionSample = new ArrayList<>();

    public void readInspectionData(Context context) {
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

                //read data
                Inspection sample = new Inspection();
                sample.setTrackingNumber(tokens[0]);
                sample.setInspectionDate(tokens[1]);
                sample.setInspeType(tokens[2]);
                sample.setNumCritical(Integer.parseInt(tokens[3]));
                sample.setNumNonCritical(Integer.parseInt(tokens[4]));
                sample.setHazardRating(tokens[5]);
                if(tokens.length > 6 && tokens[6].length() > 0) {
                    sample.setVioLump(tokens[6]);
                }
                else{
                    sample.setVioLump("");
                }
                inspectionSample.add(sample);

                Log.d("MyActivity", "Just created: " + sample);
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
    }

    public List<Inspection> getInspections() {
        return inspectionSample;
    }

    public Inspection getInspection(int position){
        return inspectionSample.get(position);
    }

    @Override
    public Iterator<Inspection> iterator() {
        return inspectionSample.iterator();
    }

}
