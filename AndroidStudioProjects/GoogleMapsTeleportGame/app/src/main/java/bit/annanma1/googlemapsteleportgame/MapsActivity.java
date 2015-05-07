package bit.annanma1.googlemapsteleportgame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Random;


public class MapsActivity extends Activity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Bind the teleport button
        Button teleport = (Button) findViewById(R.id.buttonTeleport);
        // Set the teleport button handler
        teleport.setOnClickListener(new TeleportButtonHandler());

        // Bind the map fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Set the map object
        mMap = map;

        // Set starting coordinates to Dunedin
        LatLng dunedin = new LatLng(-45.8658, 170.5196);

        // Move map location to starting coordinates
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dunedin, 13));

        // Add a marker to the map. When clicked, display city name and coordinates
        mMap.addMarker(new MarkerOptions()
                .title("Dunedin")
                .snippet("Latitude " + dunedin.latitude + " Longitude " + dunedin.longitude)
                .position(dunedin));
    }

    // Move the map to, and set a marker at the coordinates lat lng
    public void teleport(double lat, double lng) {
        // Set coordinates
        LatLng loc = new LatLng(lat, lng);

        // Move map location to new coordinates
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));

        // Add a marker to the map. When clicked, display coordinates
        mMap.addMarker(new MarkerOptions()
                .title("Coordinates")
                .snippet("Latitude " + lat + " Longitude " + lng)
                .position(loc));
    }

    private class TeleportButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // pseudo-random number generator
            Random rgen = new Random();

            // generate random number for longitude (between 0 & 180)
            double rLng = rgen.nextDouble();
            double longitude = rLng * 180;

            // generate random number for latitude (between 0 and 90)
            double rLat = rgen.nextDouble();
            double latitude = rLat * 90;

            //  50% chance the longitude value will be negative
            if (rgen.nextInt(2) == 0) {
                longitude *= -1;
            }

            // 50% chance the latitude value will be negative
            if (rgen.nextInt(2) == 0) {
                latitude *= -1;
            }

            // teleport to the randomly selected coordinates
            teleport(latitude, longitude);
        }
    }
}
