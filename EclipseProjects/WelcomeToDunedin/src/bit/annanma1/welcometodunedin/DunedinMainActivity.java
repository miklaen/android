package bit.annanma1.welcometodunedin;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class DunedinMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dunedin_main);
        
        // Populate listView
        setUpActivityList();
        
        ListView activityListView = (ListView) findViewById(R.id.listViewPages);
        activityListView.setOnItemClickListener(new ListViewHandler());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dunedin_main, menu);
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
    	
	public void setUpActivityList() {
		
		// Get resources and array of activities
        Resources resources = getResources();
		String[] activities = resources.getStringArray(R.array.activities);
		
		// Create adapter
		ArrayAdapter<String> activityAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, activities);
		
		// Get reference
		ListView activityListView = (ListView) findViewById(R.id.listViewPages);
		
		// Set ListView's adapter
		activityListView.setAdapter(activityAdapter);
	}
	
	public class ListViewHandler implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String clickedItem = (String) parent.getItemAtPosition(position).toString();
			
			Intent goToIntent = new Intent(DunedinMainActivity.this, SubActivity.class);
			
			// Pass activity data to sub activity
			goToIntent.putExtra("activity", clickedItem);
			
			startActivity(goToIntent);
		}
		
	}
}
