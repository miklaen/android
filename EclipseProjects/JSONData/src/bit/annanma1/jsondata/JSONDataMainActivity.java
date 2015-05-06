package bit.annanma1.jsondata;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class JSONDataMainActivity extends Activity {

	AssetManager am;
	String JSONInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jsondata_main);

		parseJSONFile("dunedin_events.json");

		// Create array and an array adapter to populate ListView with the key
		// "title"
		ArrayList<String> eventDisplayArray = populateArray("title");
		ArrayAdapter<String> eventAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, eventDisplayArray);

		// Get reference to the ListView and set its adapter
		ListView eventList = (ListView) findViewById(R.id.listViewEvents);
		eventList.setAdapter(eventAdapter);
		eventList.setOnItemClickListener(new EventListClickHandler());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jsondata_main, menu);
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

	public void parseJSONFile(String fileName) {
		// Get an asset manager and create an input stream from the json file
		am = getAssets();
		InputStream inputStream = null;
		try {
			inputStream = am.open(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Determine size of file and prepare buffer array for read
		int fileSizeInBytes = 0;
		try {
			fileSizeInBytes = inputStream.available();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] JSONBuffer = new byte[fileSizeInBytes];

		// Read the stream into the buffer
		try {
			inputStream.read(JSONBuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create a string from the byte[]
		JSONInput = new String(JSONBuffer);
	}

	public ArrayList<String> populateArray(String key) {
		// Initialize ArrayList to return data
		ArrayList<String> eventDisplayArray = new ArrayList<String>();

		// Create a JSON Object from the string
		JSONObject eventData = null;
		try {
			eventData = new JSONObject(JSONInput);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create a JSON Array from the JSON Object events, within the root JSON
		// Object eventData
		JSONObject eventsObject = null;
		JSONArray eventArray = null;
		if (eventData != null) {
			try {
				eventsObject = eventData.getJSONObject("events");
				eventArray = eventsObject.getJSONArray("event");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Populate ArrayList
		if (eventArray != null) {
			for (int i = 0; i < eventArray.length(); i++) {
				JSONObject currentEvent = null;
				try {
					currentEvent = eventArray.getJSONObject(i);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String currentEventTitle = null;
				try {
					currentEventTitle = currentEvent.getString(key);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				eventDisplayArray.add(currentEventTitle);
			}
		}
		return eventDisplayArray;
	}

	public class EventListClickHandler implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> list, View view, int position,
				long id) {
			// list.getItemAtPosition(position)
			// .toString();

			ArrayList<String> eventDescriptionArray = populateArray("description");
			String clickedItem = (String) eventDescriptionArray.get(position)
					.toString();

			// for debugging
			Toast.makeText(JSONDataMainActivity.this, clickedItem,
					Toast.LENGTH_LONG).show();

		}

	}
}
