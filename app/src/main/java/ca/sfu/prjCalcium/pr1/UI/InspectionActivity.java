package ca.sfu.prjCalcium.pr1.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ca.sfu.prjCalcium.pr1.Model.Inspection;
import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.R;

public class InspectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
    }
    private List<Inspection> inspectionSample = new ArrayList<>();

    private void readRestaurantData(){
        InputStream is = getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("utf-8"))
        );

        String line = "";
        try {
            //Step over headers
            reader.readLine();

            while((line = reader.readLine()) != null){
                //split by ","
                String[] tokens = line.split(",");

                //read data
                Inspection sample = new Inspection();
                sample.setTracking_number(tokens[0]);
                sample.setInspection_Date(tokens[1]);
                sample.setInspeType(tokens[2]);
                sample.setNumCritical(Integer.parseInt(tokens[3]));
                sample.setNumNonCritical(Integer.parseInt(tokens[4]));
                sample.setHazardRating(tokens[5]);
                sample.setVioLump(tokens[6]);
                inspectionSample.add(sample);

                Log.d("MyActivity", "Just created: " + sample);
            }
        }catch (IOException e){
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
    }
}
