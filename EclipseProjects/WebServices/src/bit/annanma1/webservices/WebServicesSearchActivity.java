package bit.annanma1.webservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import bit.annanma1.webservices.WebServicesMainActivity.AsyncAPICall;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WebServicesSearchActivity extends Activity {
	String JSONString = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_services_search);

		// Set search button click handler
		Button btnSearch = (Button) findViewById(R.id.buttonSearch);
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Get text from TextView
				EditText enteredArtist = (EditText) findViewById(R.id.editTextSearch);
				String artistString = enteredArtist.getText().toString();

				if (artistString.trim().length() == 0) {
					Toast.makeText(getApplicationContext(),
							"Please enter an artist", Toast.LENGTH_SHORT)
							.show();
				} else {
					AsyncAPICall getArtists = new AsyncAPICall();
					getArtists.execute(artistString);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_services_search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.action_artists) {
			Intent intent = new Intent(this, WebServicesMainActivity.class);
			startActivity(intent);
			finish();
		}
		if (id == R.id.action_image) {
			Intent intent = new Intent(this,
					WebServicesArtistImageActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	// AsyncTask to search for similar artists on last.fm
	class AsyncAPICall extends AsyncTask<String, Void, String> {
		String artist;

		@Override
		protected String doInBackground(String... params) {
			// Retrieve the value passed in from the EditText. As its the only
			// value passed into the task it should be the first value in params
			artist = params[0];

			// Remove any spaces from input since they tend to throw errors
			String artistString = artist.replaceAll("\\s+", "");

			StringBuilder sb = null;
			int responseCode = 200;

			// Api call
			String urlString = "http://ws.audioscrobbler.com/2.0/?method=artist.getSimilar&artist="
					+ artistString
					+ "&autocorrect=1&api_key=58384a2141a4b9737eacb9d0989b8a8c&limit=10&format=json";

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
			Intent intent = new Intent(WebServicesSearchActivity.this,
					WebServicesSimilarArtistsActivity.class);
			// Send the returned JSONString to the next activity to be displayed
			// to the user
			intent.putExtra("json", result);

			// Send the artist the user entered into the EditText to be
			// displayed in the next activity
			intent.putExtra("artist", artist);
			
			startActivity(intent);
		}
	}
}
