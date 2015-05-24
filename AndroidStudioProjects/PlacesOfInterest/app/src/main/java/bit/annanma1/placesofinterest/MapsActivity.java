package bit.annanma1.placesofinterest;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // TAG for error logging
    public static final String TAG = MapsActivity.class.getSimpleName();

    // Define a request code to send to Google Play services if connection fails
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Define a request code for a place picker
    private final static int PLACE_PICKER_REQUEST = 1;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private AutoCompleteAdapter mAdapter;
    private AutoCompleteTextView mPredictTextView;

    public Location mLocation;
    public boolean alreadyRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setUpMapIfNeeded();

        // Initialize GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();

        // Kick off location request
        createLocationRequest();

        // Set up place picker button
        ImageButton placePick = (ImageButton) findViewById(R.id.imageButtonPlacePicker);
        placePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPlacePicker();
            }
        });

        // Set up searchbar
        mPredictTextView = (AutoCompleteTextView) findViewById(R.id.editTextAutoComplete);
        mAdapter = new AutoCompleteAdapter(this);
        mPredictTextView.setAdapter(mAdapter);

        mPredictTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutoCompletePlace place = (AutoCompletePlace) parent.getItemAtPosition(position);
                findPlaceById(place.getId());
            }
        });

        alreadyRunning = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mAdapter.setGoogleApiClient( null );
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        // Set map type (Normal/Terrain/Hybrid)
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    /**
     * Handles the location of the device
     */
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        mLocation = location;
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);


        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                //.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation))
                ;
        //mMap.addMarker(options);

        if (!alreadyRunning) {
            float zoomLevel = 17.0f; //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

            // Kick off AsyncTask to retrieve nearby locations
            PlacesApiTask getNearbyPlaces = new PlacesApiTask();
            getNearbyPlaces.execute(mLocation);

            alreadyRunning = true;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mAdapter != null) {
            mAdapter.setGoogleApiClient(mGoogleApiClient);
        }
        findMyLocation();
    }

    public void findMyLocation() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
        * Google Play services can resolve some errors it detects.
        * If the error has a resolution, try sending an Intent to
        * start a Google Play services activity that can resolve
        * error.
        */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            /*
             * Thrown if Google Play services canceled the original
             * PendingIntent
             */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
        /*
         * If no resolution is available, display a dialog to the
         * user with the error.
         */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    /**
     * Display the Place Picker, allowing the user can select a place from
     * a list of nearby places.
     */
    private void displayPlacePicker() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())
            return;

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(getApplicationContext()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d(TAG, "GooglePlayServicesRepairableException thrown");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, "GooglePlayServicesNotAvailableException thrown");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check whether the return code is that of the place picker intent
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            goToPlace(PlacePicker.getPlace(data, this));
        }
    }

    /**
     * Find the location selected by the user from the autocomplete search TextView
     */
    private void findPlaceById(String id) {
        if (TextUtils.isEmpty(id) || mGoogleApiClient == null || !mGoogleApiClient.isConnected())
            return;

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, id).setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if (places.getStatus().isSuccess()) {
                    Place place = places.get(0);
                    goToPlace(place);
                    mPredictTextView.setText("");
                    mAdapter.clear();
                }

                //Release the PlaceBuffer to prevent a memory leak
                places.release();
            }
        });
    }


    /**
     * Zoom the map on location at @param
     *
     * @param place
     */
    private void goToPlace(Place place) {
        //Find place
        LatLng loc = place.getLatLng();
        String name = (String) place.getName();
        String addr = (String) place.getAddress();

        MarkerOptions options = new MarkerOptions()
                .position(loc)
                .title(name)
                .snippet(addr);
        Marker marker = mMap.addMarker(options);

        marker.showInfoWindow();

        // Center map on location
        float zoomLevel = 19.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoomLevel));
    }

    /**
     * Add location markers to the map
     *
     * @param locationArray
     */
    private void addLocationsToMap(ArrayList<JSONObject> locationArray) {

        // Retrieve data from each location in locationArray
        for (JSONObject currentLocation : locationArray) {
            LatLng loc = getLocation(currentLocation);
            String name = getLocationName(currentLocation);
            String addr = getLocationAddress(currentLocation);

            MarkerOptions options = new MarkerOptions()
                    .position(loc)
                    .title(name)
                    .snippet(addr);
            mMap.addMarker(options);
        }
    }

    /**
     * Retrieve the coordinates of @param
     *
     * @param currentLocation
     * @return
     */
    private LatLng getLocation(JSONObject currentLocation) {
        // Coordinates to be returned
        LatLng coords = null;

        // Create JSON Object geometry from within the currentLocation object
        JSONObject geometry = null;
        try {
            geometry = currentLocation.getJSONObject("geometry");

            // Create JSON object location from within the geometry object
            JSONObject location = geometry.getJSONObject("location");

            // Get latitude and longitude from within the location object
            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");

            coords = new LatLng(lat, lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return coords;
    }

    /**
     * Retrieve the name of the place at @param
     *
     * @param currentLocation
     * @return
     */
    private String getLocationName(JSONObject currentLocation) {
        String name = "";
        try {
            name = currentLocation.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * Retrieve the address of the place at @param
     *
     * @param currentLocation
     * @return
     */
    private String getLocationAddress(JSONObject currentLocation) {
        String address = "";
        try {
            address = currentLocation.getString("vicinity");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return address;
    }

    /**
     * Create an ArrayList of locations from a string of JSON data.
     *
     * @param JSONString contains location data of nearby places.
     * @return ArrayList of JSONObjects.
     */
    private ArrayList<JSONObject> populateLocationsArray(String JSONString) {
        // ArrayList of JSONObjects to be returned
        ArrayList<JSONObject> locationsArrayList = new ArrayList<>();

        JSONObject locationData = null;
        try {
            // Create a JSON Object from the string
            locationData = new JSONObject(JSONString);

            // Create JSON Array from the location data
            JSONArray results = locationData.getJSONArray("results");
            if (results != null) {
                for (int i = 0; i < results.length(); i++) {
                    // Create JSON Object location from the results array
                    JSONObject currentLocation = results.getJSONObject(i);

                    // Add the currentLocation object to arraylist
                    locationsArrayList.add(currentLocation);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return locationsArrayList;
    }

    /**
     * Async Task to retrieve nearby locations.
     * Returns data in JSON format
     */
    public class PlacesApiTask extends AsyncTask<Location, Void, String> {

        @Override
        protected String doInBackground(Location... params) {
            String JSONString = null;

            Location location = params[0];
            String latitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());

            // api call
            String urlString = "https://maps.googleapis.com/maps/api/place/search/json?location="
                    + latitude + "," + longitude
                    + "&radius=200&types=establishment&sensor=true&key=" + getString(R.string.browser_api_key);

            // Convert string to URLObject
            try {
                URL URLObject = new URL(urlString);

                // Create HttpURLConnection object
                HttpURLConnection connection = (HttpURLConnection) URLObject
                        .openConnection();

                // Send the URL
                connection.connect();

                // If it doesn't return 200, we have no data
                int responseCode = connection.getResponseCode();

                // Get an input stream from the HttpURLConnection and set up a
                // BufferedReader
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                // Read the input
                String responseString;
                StringBuilder sb = new StringBuilder();
                while ((responseString = br.readLine()) != null) {
                    sb = sb.append(responseString);
                }

                // Get the string from the StringBuilder
                JSONString = sb.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return JSONString;
        }


        @Override
        protected void onPostExecute(String result) {
            // Create ArrayList of retrieved data
            ArrayList<JSONObject> locationArray = populateLocationsArray(result);

            // add locations to the map
            addLocationsToMap(locationArray);
        }
    }
}
