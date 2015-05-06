package bit.annanma1.complexscreencontrols;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class ComplexScreenControlsMainActivity extends Activity {

	public RadioButton selected;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complex_screen_controls_main);
        
        RadioGroup instrumentGroup = (RadioGroup) findViewById(R.id.radioGroupInstruments);
        instrumentGroup.setOnCheckedChangeListener(new radioGroupListener());
        
        Button submit = (Button) findViewById(R.id.buttonSubmit);
        submit.setOnClickListener(new ClickHandler());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.complex_screen_controls_main, menu);
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
    
    public class radioGroupListener implements RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			selected = (RadioButton) findViewById(checkedId);
			
		}
    	
    }
    
    public class ClickHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			TextView TextEnrolled = (TextView) findViewById(R.id.textViewEnrolled);
			TextEnrolled.setText("You have enrolled for " + selected.getText().toString() + " lessons");
		}
    	
    }
}
