package ca.sfu.prjCalcium.pr1.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import ca.sfu.prjCalcium.pr1.Model.ViolationManager;
import ca.sfu.prjCalcium.pr1.R;

public class RestaurantListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Testing Violation Model
        ViolationManager vm = new ViolationManager();
        vm.readFromTxt(getApplicationContext());
        Toast.makeText(getApplicationContext(), vm.getViolation(5).toString(), Toast.LENGTH_LONG).show();
    }
}
