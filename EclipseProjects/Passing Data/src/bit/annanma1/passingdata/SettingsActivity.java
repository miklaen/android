package bit.annanma1.passingdata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		// EditText event handler
        EditText username = (EditText) findViewById(R.id.editTextUsername);
        username.setOnKeyListener(new EditTextHandler());
        
        // Button bind/handler
        Button submit = (Button) findViewById(R.id.buttonReturn);       
        submit.setOnClickListener(new ClickHandler());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
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
	
	public class EditTextHandler implements OnKeyListener {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			return false;
		}
    	
    }
	
	public class ClickHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			EditText text = (EditText) findViewById(R.id.editTextUsername);
			String name = text.getText().toString();
			
			// check username is 5+ characters
			if (text.length() < 5) {
				Toast.makeText(SettingsActivity.this, "Usernames must be at least 5 characters long, " + text.getText(), Toast.LENGTH_LONG).show();
			}
			else {
				Intent newActivity = new Intent(SettingsActivity.this, PassingDataMainActivity.class);
	    		newActivity.putExtra("username", name);
				startActivity(newActivity);
				finish();
			}			
		}
    	
    }
}
