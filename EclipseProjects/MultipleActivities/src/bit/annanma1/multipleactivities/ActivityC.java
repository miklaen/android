package bit.annanma1.multipleactivities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ActivityC extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity);

        // TextView bind
        TextView textView = (TextView) findViewById(R.id.textViewActivity);
        textView.setText("Activity C");
        
        // Button bind/handler
        Button submit = (Button) findViewById(R.id.buttonChange);
        submit.setOnClickListener(new ClickHandler());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_c, menu);
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
	
	public class ClickHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			Uri goToWebsite = Uri.parse("http://www.google.com");
			
			Intent surfTheWeb = new Intent(Intent.ACTION_VIEW, goToWebsite);
			startActivity(surfTheWeb);
		}
    	
    }
}
