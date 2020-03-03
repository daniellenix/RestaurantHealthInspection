package ca.sfu.prjCalcium.pr1.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ca.sfu.prjCalcium.pr1.Model.Restaurant;
import ca.sfu.prjCalcium.pr1.R;

//reference Brian Fraser's video

public class RestaurantListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
