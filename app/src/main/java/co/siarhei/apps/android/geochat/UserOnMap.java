package co.siarhei.apps.android.geochat;


import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class UserOnMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Location initialLocation;
    LatLng googInitialLocation;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_on_map);

        SupportMapFragment mapFragment =  (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.initialLocation = new Location("");
        this.initialLocation.setLatitude(getIntent().getDoubleExtra("CURRENT_LOCATION_LAT", (double) 0));
        this.initialLocation.setLongitude(getIntent().getDoubleExtra("CURRENT_LOCATION_LONG", (double) 0));
        this.username = getIntent().getStringExtra("USERNAME");
        Button mapCloseButton = findViewById(R.id.map_button_close);
        mapCloseButton.setOnClickListener(v -> {
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
        mMap.addMarker(new MarkerOptions().position(googInitialLocation).title(username));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(googInitialLocation, 14f));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10f));



        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        }




    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }


}
