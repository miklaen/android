package bit.annanma1.placesofinterest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
import java.util.Collections;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    //****************** Global Constants ******************//
    public final static String TAG = MapsActivity.class.getSimpleName();    // TAG for error logging

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;  // Define a request code to send to Google Play services if connection fails
    private final static int PLACE_PICKER_REQUEST = 1;  // Define a request code for a place picker
    //******************************************************//

    //****************** Global Variables ******************//
    public boolean alreadyRunning;  // Monitor the status of this activity
    public Location mLocation;      // Current location of device
    public int mRadius;     // Radius of places displayed on map
    public Circle mRadiusCircle;    // Circle drawn on map representing the search radius
    public ArrayList<MarkerOptions> mMarkers;   // List of markers to display on map
    public ArrayList<MarkerOptions> mUserAddedMarkers;   // List of user added markers
    public ArrayList<CharSequence> mPlaceIDs;

    private GoogleMap mMap;     // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;   // Access point for google api
    private LocationRequest mLocationRequest;   // Requests the current location at certain intervals
    private AutoCompleteAdapter mAdapter;       // Adapter for autocomplete places search
    private AutoCompleteTextView mPredictTextView;  // Autocomplete places search TextView
    //******************************************************//

    //****************** Override Methods ******************//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize places search radius
        mRadius = 200;

        // Initialize markers ArrayList
        mMarkers = new ArrayList<>();

        // Initialize user added markers ArrayList
        mUserAddedMarkers = new ArrayList<>();

        mPlaceIDs = new ArrayList<>();

        // Initialize map
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

        // Set up searchbar ********//
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
        //**************************//

        // Set up Seekbar **********//
        SeekBar searchRadius = (SeekBar) findViewById(R.id.seekBarSearchRadius);
        searchRadius.setOnSeekBarChangeListener(new RadiusSeekBarChangeListener());
        alreadyRunning = false;
        //**************************//
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
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mAdapter.setGoogleApiClient(null);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mAdapter != null) {
            mAdapter.setGoogleApiClient(mGoogleApiClient);
        }
        findMyLocation();
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
    //****************** END Override Methods ******************//

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

        // Set map long click handler (for adding locations to the map)
        mMap.setOnMapLongClickListener(new MapLongClickHandler());
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

        // Draw circle on map to represent the area around the device in which markers are displayed
        drawCircleOnMap();

        // Ensure the app only zooms to current location when the app is first initialised
        if (!alreadyRunning) {
            float zoomLevel = 17.0f; //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

            if (isOnline()) {
                // Kick off AsyncTask to retrieve nearby locations
                PlacesApiTask getNearbyPlaces = new PlacesApiTask();
                getNearbyPlaces.execute(mLocation);
            } else {
                Toast.makeText(this, "Can't retrieve nearby places, network is unavailable", Toast.LENGTH_LONG).show();
            }

            alreadyRunning = true;
        }
    }

    /**
     * Draw a circle on the map to represent the search radius
     */
    public void drawCircleOnMap() {

        if (mRadiusCircle != null) {
            mRadiusCircle.remove();
        }

        double currentLatitude = mLocation.getLatitude();
        double currentLongitude = mLocation.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        mRadiusCircle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(mRadius)
                .strokeColor(Color.RED));
    }

    /**
     * Create a request for the current location every 1 - 10 seconds
     */
    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)         // 10 seconds, in milliseconds
                .setFastestInterval(1000);      // 1 second, in milliseconds
    }

    /**
     * Find the last known location of the device
     */
    public void findMyLocation() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
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
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, "GooglePlayServicesNotAvailableException thrown");
            e.printStackTrace();
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
        // hide the keyboard
        hideSoftKeyboard();

        if (TextUtils.isEmpty(id) || mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            return;
        }

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
     * Check whether we have internet access.
     *
     * @return true if device is online, false if not.
     */
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method used to hide the soft keyboard
     */
    public void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.d(TAG, "Exception thrown");
            e.printStackTrace();
        }
    }

    /**
     * Zoom the map on location at place
     *
     * @param place The place on which the map is centered.
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
    }

    /**
     * Add location markers to the map
     *
     * @param locationArray Array of nearby locations
     */
    private void addLocationsToMap(ArrayList<JSONObject> locationArray) {

        // Clear markers ArrayList so only locations retrieved from the most recent api call are displayed
        mMarkers.clear();

        // Retrieve data from each location in locationArray
        for (JSONObject currentLocation : locationArray) {
            LatLng loc = getLocation(currentLocation);
            String name = getLocationName(currentLocation);
            String addr = getLocationAddress(currentLocation);

            // Create marker
            MarkerOptions options = new MarkerOptions()
                    .position(loc)
                    .title(name)
                    .snippet(addr);

            // Add marker to mMarkers ArrayList
            mMarkers.add(options);
        }

        // Add display markers
        addMarkersFromList();
    }

    /**
     * Find the distance between two places
     *
     * @param first LatLng of location 1
     * @param second LatLng of location 2
     * @return distance as a float
     */
    public static float distanceBetween(LatLng first, LatLng second) {
        float[] distance = new float[1];
        Location.distanceBetween(first.latitude, first.longitude, second.latitude, second.longitude, distance);
        return distance[0];
    }


    /**
     * Add markers to the map from arraylists.
     */
    private void addMarkersFromList() {
        // Clear all current markers from map
        mMap.clear();

        // Add markers retrieved from api
        for (MarkerOptions marker : mMarkers) {
            mMap.addMarker(marker);
        }

        // Get device location (center of radius)
        LatLng center = mRadiusCircle.getCenter();

        // Add user added markers if any within the set radius
        if (mUserAddedMarkers.size() > 0) {
            for (MarkerOptions marker : mUserAddedMarkers) {
                if (distanceBetween(marker.getPosition(),center) < mRadius) {
                    mMap.addMarker(marker);
                }
            }
        }
    }

    /**
     * Retrieve the coordinates of currentLocation
     *
     * @param currentLocation the current location
     * @return the coordinates of the current location
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
            Log.d(TAG, "JSONException thrown");
            e.printStackTrace();
        }
        return coords;
    }

    /**
     * Retrieve the name of the place at currentLocation
     *
     * @param currentLocation the current location
     * @return the name of the current location
     */
    private String getLocationName(JSONObject currentLocation) {
        String name = "";
        try {
            name = currentLocation.getString("name");
        } catch (JSONException e) {
            Log.d(TAG, "JSONException thrown");
            e.printStackTrace();
        }
        return name;
    }

    /**
     * Retrieve the address of the place at currentLocation
     *
     * @param currentLocation the current location
     * @return the address of the current location
     */
    private String getLocationAddress(JSONObject currentLocation) {
        String address = "";
        try {
            address = currentLocation.getString("vicinity");
        } catch (JSONException e) {
            Log.d(TAG, "JSONException thrown");
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

        try {
            // Create a JSON Object from the string
            JSONObject locationData = new JSONObject(JSONString);

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
            Log.d(TAG, "JSONException thrown");
            e.printStackTrace();
        }
        return locationsArrayList;
    }

    /**
     * Shows pop-up dialog for user to add a location to the map
     */
    protected void addPlaceDialog(LatLng latLng) {

        LayoutInflater factory = LayoutInflater.from(this);

        final LatLng newLatLng = latLng;

        // add_place is a Layout XML file containing text fields to display
        // in alert dialog
        final View textEntryView = factory.inflate(R.layout.add_place, null);

        // Get each editText field
        final EditText placeName = (EditText) textEntryView
                .findViewById(R.id.editTextPlaceName);
        final EditText placeAddress = (EditText) textEntryView
                .findViewById(R.id.editTextPlaceAddress);
        final EditText placePhone = (EditText) textEntryView
                .findViewById(R.id.editTextPlacePhone);
        final EditText placeWebsite = (EditText) textEntryView
                .findViewById(R.id.editTextPlaceWeb);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MapsActivity.this);

        // set title
        alertDialogBuilder.setTitle("Add Place to Map");

        // set dialog buttons
        alertDialogBuilder.setView(textEntryView).setCancelable(false)
                .setPositiveButton("Add Place", null)
                .setNegativeButton("Cancel", null);

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button positive = alertDialog
                        .getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Reset errors
                        placeName.setError(null);
                        placeAddress.setError(null);
                        placePhone.setError(null);
                        placeWebsite.setError(null);

                        // Deal with input errors
                        boolean cancel = false;
                        View focusView = null;

                        // Get values from the EditText fields
                        String name = placeName.getText().toString();
                        String address = placeAddress.getText().toString();
                        String phone = placePhone.getText().toString();
                        String website = placeWebsite.getText().toString();

                        // Check user entered an address
                        if (TextUtils.isEmpty(address)) {
                            placeAddress.setError(getString(R.string.error_field_required));
                            focusView = placeAddress;
                            cancel = true;
                        }

                        // Check user entered a place name
                        if (TextUtils.isEmpty(name)) {
                            placeName.setError(getString(R.string.error_field_required));
                            focusView = placeName;
                            cancel = true;
                        }

                        if (cancel) {
                            // There was an error; don't attempt anything
                            // and focus the first form field with an error.
                            focusView.requestFocus();
                        } else {
                            // add the location
                            AddPlaceRequest place =
                                    new AddPlaceRequest(
                                            name, // Name
                                            newLatLng, // Latitude and longitude
                                            address, // Address
                                            Collections.singletonList(Place.TYPE_ESTABLISHMENT), // Place types
                                            phone, // Phone number
                                            Uri.parse(website) // Website
                                    );

                            Places.GeoDataApi.addPlace(mGoogleApiClient, place)
                                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                                        @Override
                                        public void onResult(PlaceBuffer places) {
                                            Log.i(TAG, "Place add result: " + places.getStatus().toString());
                                            Log.i(TAG, "Added place: " + places.get(0).getName().toString());
                                            Toast.makeText(getApplicationContext(), "Place Added", Toast.LENGTH_SHORT).show();
                                            mPlaceIDs.add(places.get(0).getId());
                                            places.release();
                                        }
                                    });

                            // create a new marker for location
                            MarkerOptions newMarker = new MarkerOptions()
                                    .position(newLatLng)
                                    .title(name)
                                    .snippet(address)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                            // add marker to user added markers ArrayList
                            mUserAddedMarkers.add(newMarker);

                            // add marker to map
                            mMap.addMarker(newMarker);

                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        // show it
        alertDialog.show();
    }
    //****************************** END Methods *******************************//

    //***************************** Inline Classes *****************************//

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
                    + "&radius=" + mRadius
                    + "&types=establishment&sensor=true&key=" + getString(R.string.browser_api_key);

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
                Log.d(TAG, "IOException thrown");
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

    /**
     * Check for long clicks on the map.
     */
    private class MapLongClickHandler implements GoogleMap.OnMapLongClickListener {
        @Override
        public void onMapLongClick(LatLng latLng) {
            // Add a marker at the location the map was clicked
            addPlaceDialog(latLng);
        }
    }

    /**
     * Check for changes to the search radius SeekBar.
     */
    private class RadiusSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mRadius = progress;

            // Draw circle to represent current radius
            drawCircleOnMap();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Toast.makeText(MapsActivity.this, "Search Radius: " + mRadius + " metres",
                    Toast.LENGTH_SHORT).show();

            // Draw circle to represent current radius
            drawCircleOnMap();

            if (isOnline()) {
                // Kick off AsyncTask to retrieve nearby locations
                PlacesApiTask getNearbyPlaces = new PlacesApiTask();
                getNearbyPlaces.execute(mLocation);
            } else {
                Toast.makeText(MapsActivity.this, "Can't retrieve nearby places, network is unavailable", Toast.LENGTH_LONG).show();
            }
        }
    }
}
