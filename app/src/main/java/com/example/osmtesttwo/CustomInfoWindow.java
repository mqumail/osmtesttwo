package com.example.osmtesttwo;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

public class CustomInfoWindow extends MarkerInfoWindow {

    private POI selectedPoi;

    public CustomInfoWindow(MapView mapView) {
        super(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mapView);
        Button btn = (Button)(mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_moreinfo));

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Toast.makeText(view.getContext(), "Button clicked", Toast.LENGTH_LONG).show();
                if (selectedPoi.mUrl != null){
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedPoi.mUrl));
                    view.getContext().startActivity(myIntent);
                }
            }
        });
    }

    @Override public void onOpen(Object item){
        super.onOpen(item);
        mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_moreinfo).setVisibility(View.VISIBLE);

        Marker marker = (Marker) item;
        selectedPoi = (POI)marker.getRelatedObject();
    }
}
