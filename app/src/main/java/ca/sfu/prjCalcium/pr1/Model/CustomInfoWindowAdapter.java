package ca.sfu.prjCalcium.pr1.Model;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import ca.sfu.prjCalcium.pr1.R;

//Reference https://gist.github.com/ccjeng/ff8ca25e0e92302639dadbe4a8533279
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;

    public CustomInfoWindowAdapter(Activity context){
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.custom_info_window, null);

        TextView name = view.findViewById(R.id.textName);
        TextView snippet = view.findViewById(R.id.textSnippet);

        name.setText(marker.getTitle());
        snippet.setText((marker.getSnippet()));
        return view;
    }
}
