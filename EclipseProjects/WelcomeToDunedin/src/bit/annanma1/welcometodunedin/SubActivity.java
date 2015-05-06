package bit.annanma1.welcometodunedin;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class SubActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sub);

		// Fetch intent data
        Bundle extras = getIntent().getExtras();
        String activityName = null;     
		if (extras != null) {
			activityName = extras.getString("activity");
			
			// TextView bind
			TextView title = (TextView) findViewById(R.id.textViewSub);
	        if (activityName != null) {
	        	title.setText(activityName);
			}
	        
	        // ImageView bind
	     	ImageView image = (ImageView) findViewById(R.id.imageViewSub);
	     	
	     	switch(activityName) {
	     		case "Activities":
	     			image.setImageResource(R.drawable.monarch);
	     			break;
	     		case "Dining":
	     			image.setImageResource(R.drawable.customhouse);
	     			break;
	     		case "Services":
	     			image.setImageResource(R.drawable.library);
	     			break;
	     		case "Shopping":
	     			image.setImageResource(R.drawable.thieves_alley);
	     			break;	
	     	}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sub, menu);
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
}
