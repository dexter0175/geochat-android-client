

package co.siarhei.apps.android.geochat;


import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,SeekBar.OnSeekBarChangeListener {

    public SeekBar zoomSeekBar;

    private GoogleMap mMap;
    private LocationManager locationManager;

    public float lowestZoom = 15f;
    public float highestZoom = 20f; // 20 is the highest zoom level allowed by Google Maps
    public float deltaZoom;

    public float lowestRadius = 550f;
    public float highestRadius = 17f;
    public float deltaRadius;

    public float radiusIncrement;

    public float currentRadius;

    Circle circle;
    Location initialLocation;

    LatLng googInitialLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment =  (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SeekBar seekBar = findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        // Set provided (current) location as the initial location for the map
        this.initialLocation = new Location("");
        this.initialLocation.setLatitude(getIntent().getDoubleExtra("CURRENT_LOCATION_LAT", new Double(0)));
        this.initialLocation.setLongitude(getIntent().getDoubleExtra("CURRENT_LOCATION_LONG", new Double(0)));

        Button mapCloseButton = findViewById(R.id.map_button_close);
        mapCloseButton.setOnClickListener(v -> finish());

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add custom styling ...
        try {

            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map_json));

            if (!success) {
                Log.e("GeoChat Map ", "Style parsing failed.");
            }

        } catch (Resources.NotFoundException e) {
            Log.e("GeoChat Map ", "Can't find style. Error: ", e);
        }


        // Change Map default camera location ...
        googInitialLocation = new LatLng(this.initialLocation.getLatitude(), this.initialLocation.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(googInitialLocation).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(googInitialLocation, 14f));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16.75f));



//        mMap.setMinZoomPreference(lowestZoom);
//        mMap.setMaxZoomPreference(highestZoom);


        // Set UI Config for unique map operation
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);


        circle = mMap.addCircle(new CircleOptions()
            .center(googInitialLocation)
            .radius(lowestRadius)
            .strokeColor(Color.argb(190, 200, 200, 200))
            .fillColor(Color.argb(40, 111, 111, 111)));


        mMap.setOnCameraIdleListener(getCameraChangeListener());



    }




    public GoogleMap.OnCameraIdleListener getCameraChangeListener()
    {
        return () -> {
            CameraPosition position = mMap.getCameraPosition();

//                deltaZoom = highestZoom - position.zoom;
//                deltaRadius = lowestRadius - highestRadius;
//
//                radiusIncrement = (lowestRadius - highestRadius) / (highestZoom - lowestZoom);
//                    // delta(radius) / delta(zoom) = ~77.727272727272..
//
//                currentRadius = position.zoom*100;//highestRadius + (deltaZoom * radiusIncrement);
//
//
//                circle.setRadius(currentRadius);
//                circle.setCenter(mMap.getCameraPosition().target);


            Log.i("MAP ZOOM", "Current Zoom Level: "+position.zoom+"\nDzoom: "+deltaZoom+"\nDradius: "+deltaRadius+"\nRincrement: "+radiusIncrement+"\nRcurrent: "+currentRadius+"");

        };
    }







    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        circle.setRadius(progress/Math.pow (mMap.getCameraPosition().zoom/50,2));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
