package bit.annanma1.eventhandlers;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class EventHandlersMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_handlers_main);   
        
        // Button event handlers
        Button ButtonEventTest = (Button) findViewById(R.id.btnEventTest);
        ButtonEventTest.setOnClickListener(new ClickHandlerWithToast());
        ButtonEventTest.setOnLongClickListener(new LongClickHandler());
        
        // EditText event handler
        EditText TextBox = (EditText) findViewById(R.id.editTextAt);
        TextBox.setOnKeyListener(new EditTextHandler());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_handlers_main, menu);
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
    
    public class ClickHandlerWithToast implements OnClickListener {

		@Override
		public void onClick(View v) {
			Toast.makeText(EventHandlersMainActivity.this, "You click button", Toast.LENGTH_SHORT).show();
			
		}
    	
    }
    
    public class LongClickHandler implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			Toast.makeText(EventHandlersMainActivity.this, "You click button long time", Toast.LENGTH_SHORT).show();
			return true;
		}
		
    }
    
    public class EditTextHandler implements OnKeyListener {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			EditText text = (EditText) findViewById(v.getId());
			
			// Don't type @
			if (keyCode == KeyEvent.KEYCODE_AT && event.getAction() == KeyEvent.ACTION_UP) {
				Toast.makeText(EventHandlersMainActivity.this, "Don't type @", Toast.LENGTH_LONG).show();			
			}
			
			// 8 character username
			if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
				if (text.length() == 8) {
					Toast.makeText(EventHandlersMainActivity.this, "Thankyou " + text.getText(), Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(EventHandlersMainActivity.this, "Usernames must be 8 characters, " + text.getText(), Toast.LENGTH_LONG).show();
				}
			}
			return false;
		}
    	
    }

}
