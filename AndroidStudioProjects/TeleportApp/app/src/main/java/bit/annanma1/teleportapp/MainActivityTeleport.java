package bit.annanma1.teleportapp;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Random;


public class MainActivityTeleport extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_teleport);

//        Button teleport = (Button) findViewById(R.id.buttonTeleport);
//        teleport.setOnClickListener(new TeleportButtonHandler());

        // Location utility objects
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria defaultCriteria = new Criteria();

        String providerName = locManager.getBestProvider(defaultCriteria, false);

        locManager.requestLocationUpdates(providerName, 500, 10, new CustomLocationListener());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_teleport, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Sift through the JSON String to find relevant data
    public String parseLocation(String JSONString) {
        // Initialise location string
        String location = null;
        // Create a JSON Object from the string
        JSONObject locData = null;
        try {
            locData = new JSONObject(JSONString);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (locData == null) {
            location = "Nothing near here, sorry...";
        } else {
            try {
                String place = locData.getString("geoplugin_place");
                String region = locData.getString("geoplugin_region");
                String country = locData.getString("geoplugin_countryCode");

                location = place + ", " + region + ", " + country;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return location;
    }

    // Define a LocationListener
    private class CustomLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            TextView tvLongitude = (TextView) findViewById(R.id.textViewLongVal);
            TextView tvLatitude = (TextView) findViewById(R.id.textViewLatVal);

            // Pull the individual data values form the passed-in new Location...
            double lng = location.getLongitude();
            double lat = location.getLatitude();

            // ...and display them in the TextViews
            tvLongitude.setText(String.valueOf(lng));
            tvLatitude.setText(String.valueOf(lat));

            // Create an array to store longitude & latitude
            Double[] remoteParams = {lat, lng};

            // Initialise AsyncTask CallGeoPlugin
            CallGeoPlugin callGeoPlugin = new CallGeoPlugin();

            // Pass array holding latitude and longitude into callGeoPlugin
            callGeoPlugin.execute(remoteParams);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    // Button click handler calls an Async Task
    private class TeleportButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            TextView tvLongitude = (TextView) findViewById(R.id.textViewLongVal);
            TextView tvLatitude = (TextView) findViewById(R.id.textViewLatVal);
            TextView tvLocation = (TextView) findViewById(R.id.textViewCity);

            // Clear the location TextView
            tvLocation.setText("");

            // Declare strings to be passed into the TextViews declared above
            String longVal;
            String latVal;

            // 3 decimal place format
            DecimalFormat threeDP = new DecimalFormat("0.000");

            // pseudo-random number generator
            Random rgen = new Random();

            // generate random number for longitude (between 0 & 180)
            double lng = rgen.nextDouble();
            double longBase = lng * 180;

            // generate random number for latitude (between 0 and 90)
            double lat = rgen.nextDouble();
            double latBase = lat * 90;

            //  50% chance the longitude value will be negative
            if (rgen.nextInt(2) == 0) {
                longBase *= -1;
            }
            // round the value to 3 decimal places
            longVal = threeDP.format(longBase);

            // 50% chance the latitude value will be negative
            if (rgen.nextInt(2) == 0) {
                latBase *= -1;
            }
            // round the value to 3 decimal places
            latVal = threeDP.format(latBase);

            // Display the longitude/latitude values in respective TextViews
            tvLongitude.setText(longVal);
            tvLatitude.setText(latVal);

            // Create an array to store longitude & latitude
            Double[] remoteParams = {latBase, longBase};

            // Initialise AsyncTask CallGeoPlugin
            CallGeoPlugin callGeoPlugin = new CallGeoPlugin();

            // Pass array holding latitude and longitude into callGeoPlugin
            callGeoPlugin.execute(remoteParams);
        }
    }

    // Async Task that retrieves a String of JSON containing location information
    public class CallGeoPlugin extends AsyncTask<Double, Void, String> {

        @Override
        protected String doInBackground(Double... userLocation) {
            double lat = userLocation[0];
            double lng = userLocation[1];

            String JSONString;
            StringBuilder sb = null;
            int responseCode = 200;

            String urlString = "http://www.geoplugin.net/extras/location.gp?lat="
                    +lat+"&long="+lng+"&format=json";

            // Convert string to URLObject
            URL URLObject = null;
            try {
                URLObject = new URL(urlString);

                // Create HttpURLConnection object
                HttpURLConnection connection = null;

                connection = (HttpURLConnection) URLObject.openConnection();

                // Send the URL
                connection.connect();

                // If it doesn't return 200, we have no data
                responseCode = connection.getResponseCode();

                // Get an input stream from the HttpURLConnection and set up a
                // BufferedReader
                InputStream is = null;
                is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                // Read the input
                String responseString;
                sb = new StringBuilder();
                while ((responseString = br.readLine()) != null) {
                    sb = sb.append(responseString);
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Response code " + responseCode, Toast.LENGTH_LONG).show();
            }

            // Get the string from the StringBuilder
            JSONString = sb.toString();

            return JSONString;
        }

        @Override
        protected void onPostExecute(String result) {
            String loc = parseLocation(result);

            TextView tvLocation = (TextView) findViewById(R.id.textViewCity);
            tvLocation.setText(loc);
        }
    }
}
