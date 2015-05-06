package bit.annanma1.languagequiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ResultsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		
		int score = 0;
		
		// Get value that was passed from the last activity
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			score = extras.getInt("score");
		}
		
		TextView scoreDisplay = (TextView) findViewById(R.id.textViewScoreDisplay);
		scoreDisplay.setText(String.valueOf(score) + " out of 11");
		
		// Button bind/handler
        Button replay = (Button) findViewById(R.id.buttonReplay);
        replay.setOnClickListener(new ClickHandler());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
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
			Intent newActivity = new Intent(ResultsActivity.this, LanguageQuizMainActivity.class);
			startActivity(newActivity);
			finish();
		}
    	
    }
}
