package bit.annanma1.webservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class WebServicesArtistImageActivity extends Activity {
	String JSONString = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_services_artist_image);

		// Retrieve data
		AsyncAPICall getArtist = new AsyncAPICall();
		getArtist.execute();

		Button showImage = (Button) findViewById(R.id.buttonShowImage);
		showImage.setOnClickListener(new ImageButtonHandler());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_services_artist_image, menu);
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
		if (id == R.id.action_search) {
			Intent intent = new Intent(this, WebServicesSearchActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	public String getImage() {
		// Create a JSON Object from the string
		JSONObject artistData = null;
		String imageURL = null;
		try {
			artistData = new JSONObject(JSONString);
		} catch (JSONException e) {
		}

		// Create a JSON Array from the JSON Object artists, within the root
		// JSON
		JSONObject artistsObject = null;
		JSONObject artist = null;

		if (artistData != null) {
			try {
				// Inside the "artists" object...
				artistsObject = artistData.getJSONObject("artists");
				// ...we should find the "artist" array
				artist = artistsObject.getJSONObject("artist");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Populate ArrayList
		if (artist != null) {
			// ... inside which is an "image" array
			JSONArray imageArray = null;
			JSONObject artistImageObject = null;

			// Get image
			try {
				imageArray = artist.getJSONArray("image");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (imageArray != null) {
				try {
					artistImageObject = imageArray.getJSONObject(4);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (artistImageObject != null) {
				try {
					imageURL = artistImageObject.getString("#text");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			imageURL = null;
		}
		return imageURL;
	}

	public class ImageButtonHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			String url = getImage();
			AsyncImage getImage = new AsyncImage();
			getImage.execute(url);
		}
	}

	// AsyncTask to retrieve the top artist from last.fm
	class AsyncAPICall extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			StringBuilder sb = null;

			// Hard coded api call
			String urlString = "http://ws.audioscrobbler.com/2.0/?method=chart.getTopArtists&api_key=58384a2141a4b9737eacb9d0989b8a8c&limit=1&format=json";

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
				sb = new StringBuilder();
				while ((responseString = br.readLine()) != null) {
					sb = sb.append(responseString);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Get the string from the StringBuilder
			JSONString = sb.toString();

			return JSONString;
		}
	}

	// AsyncTask to retrieve the top artist's images from last.fm
	class AsyncImage extends AsyncTask<String, Void, Bitmap> {
		String sourceURL;

		@Override
		protected Bitmap doInBackground(String... params) {
			sourceURL = params[0];
			Bitmap image = null;
			try {
				// Convert string to URLObject
				URL url = new URL(sourceURL);

				// Create HttpURLConnection object
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();

				// Send the URL
				connection.connect();

				// Get an input stream from the HttpURLConnection decode using
				// BitmapFactory
				InputStream input = connection.getInputStream();
				image = BitmapFactory.decodeStream(input);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return image;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			ImageView artistImage = (ImageView) findViewById(R.id.imageViewTopArtist);
			artistImage.setImageBitmap(result);
		}
	}
}
