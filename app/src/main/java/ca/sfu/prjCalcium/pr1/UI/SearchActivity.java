package ca.sfu.prjCalcium.pr1.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ca.sfu.prjCalcium.pr1.R;

import static ca.sfu.prjCalcium.pr1.UI.MapsActivity.INTENT_EXTRA_SOURCE_ACTIVITY_COND;

public class SearchActivity extends AppCompatActivity {

    public static Intent makeIntent(Context c, int sourceActivityCondCode) {
        Intent intent = new Intent(c, SearchActivity.class);
        intent.putExtra(INTENT_EXTRA_SOURCE_ACTIVITY_COND, sourceActivityCondCode);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }
}
