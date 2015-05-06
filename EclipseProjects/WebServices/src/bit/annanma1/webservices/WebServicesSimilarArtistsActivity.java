package bit.annanma1.webservices;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class WebServicesSimilarArtistsActivity extends Activity {
	String artist = null;
	String jsonString = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_services_similar_artists);

		// Fetch intent data
		Bundle extras = getIntent().getExtras();		
		if (extras != null) {
			artist = extras.getString("artist");
			jsonString = extras.getString("json");
		}

		// Create array and an array adapter to populate ListView
		ArrayList<String> artistDisplayArray = populateArray();
		ArrayAdapter<String> artistAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, artistDisplayArray);
		
		// Populate title TextView
		TextView title = (TextView) findViewById(R.id.textViewTitleSimilar);
		title.setText("Artists similar to " + artist);

		// Get reference to the ListView and set its adapter
		ListView artistList = (ListView) findViewById(R.id.listViewSimilar);
		artistList.setAdapter(artistAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_services_similar_artists, menu);
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
		return super.onOptionsItemSelected(item);
	}
	
	public ArrayList<String> populateArray() {
		// Initialize ArrayList to return data
		ArrayList<String> artistDataArray = new ArrayList<String>();

		// Create a JSON Object from the string
		JSONObject artistData = null;
		try {
			artistData = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create a JSON Array from the JSON Object artists, within the root JSON
		// Object artistData
		JSONObject artistsObject = null;
		JSONArray artistArray = null;
		if (artistData != null) {
			try {
				artistsObject = artistData.getJSONObject("similarartists");
				artistArray = artistsObject.getJSONArray("artist");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Populate ArrayList
		if (artistArray != null) {
			for (int i = 0; i < artistArray.length(); i++) {
				JSONObject currentArtist = null;
				try {
					currentArtist = artistArray.getJSONObject(i);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String currentartistTitle = null;
				try {
					currentartistTitle = currentArtist.getString("name");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				artistDataArray.add(currentartistTitle);
			}
		}
		return artistDataArray;
	}
}
