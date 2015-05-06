package bit.annannma1.requestingdata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class RequestingDataMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requesting_data_main);
        
     // Button bind/handler
        Button submit = (Button) findViewById(R.id.buttonTextColour);       
        submit.setOnClickListener(new ClickHandler());     
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.requesting_data_main, menu);
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
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				// Get the data
				int returnData = data.getIntExtra("TextColour", 0);
				
				TextView text = (TextView) findViewById(R.id.textViewText);
				text.setTextColor(returnData);
			}
		}
	}

	public class ClickHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(RequestingDataMainActivity.this, SettingsActivity.class);
			
			// Start an activity with an id to track the request
			startActivityForResult(intent, 0);			
		}    	
    }
}
