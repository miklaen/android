package bit.annanma1.passingdata;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class PassingDataMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passing_data_main);
        
        // Fetch intent data
        Bundle extras = getIntent().getExtras();
        String username = null;
		if (extras != null) {
			username = extras.getString("username");
		}
        
		// If the intent was passed from settings, set the username
		TextView yourUsername = (TextView) findViewById(R.id.textViewUsername);
        if (username != null) {
			yourUsername.setText(username);
		}
        
        // Button bind/handler
        Button submit = (Button) findViewById(R.id.buttonSettings);       
        submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent newActivity = new Intent(PassingDataMainActivity.this, SettingsActivity.class);
				startActivity(newActivity);
				finish();
			}
        	
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.passing_data_main, menu);
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
