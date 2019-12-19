

package co.siarhei.apps.android.geochat;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

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



    private GoogleMap mMap;

    public float lowestRadius = 550f;


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

        this.initialLocation = new Location("");
        this.initialLocation.setLatitude(getIntent().getDoubleExtra("CURRENT_LOCATION_LAT", (double) 0));
        this.initialLocation.setLongitude(getIntent().getDoubleExtra("CURRENT_LOCATION_LONG", (double) 0));

        Button mapCloseButton = findViewById(R.id.map_button_close);
        mapCloseButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("RADIUS",circle.getRadius());
            setResult(RESULT_OK, intent);
            finish();

        });

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {

            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map_json));

            if (!success) {
                Log.e("GeoChat Map ", "Style parsing failed.");
            }

        } catch (Resources.NotFoundException e) {
            Log.e("GeoChat Map ", "Can't find style. Error: ", e);
        }


        googInitialLocation = new LatLng(this.initialLocation.getLatitude(), this.initialLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(googInitialLocation).title("You are here!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(googInitialLocation, 14f));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10f));



        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);


        circle = mMap.addCircle(new CircleOptions()
            .center(googInitialLocation)
            .radius(lowestRadius)
            .strokeColor(Color.argb(190, 200, 200, 200))
            .fillColor(Color.argb(40, 111, 111, 111)));


        mMap.setOnCameraMoveListener(
        () -> {
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(googInitialLocation));
            circle.setCenter(mMap.getCameraPosition().target);
        });


    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(getApplicationContext(),"Radius was not set",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        circle.setRadius(progress/Math.pow (mMap.getCameraPosition().zoom/50,2.7));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
