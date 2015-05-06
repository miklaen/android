package bit.annanma1.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FragmentsMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragments_main);
		
		Button btnImageView = (Button) findViewById(R.id.buttonImageView);
		btnImageView.setOnClickListener(new ImageViewFragment());
		
		Button btnListView = (Button) findViewById(R.id.buttonListView);
		btnListView.setOnClickListener(new ListViewFragment());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fragments_main, menu);
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
	
	public class ImageViewFragment implements OnClickListener {

		@Override
		public void onClick(View v) {
			Fragment dynFrag = new ShowImageFragment();
			FragmentManager fragMan = getFragmentManager();
			
			FragmentTransaction fragTrans = fragMan.beginTransaction();
			
			fragTrans.replace(R.id.fragment_container1, dynFrag);
			
			fragTrans.commit();
		}
		
	}
	
	public class ListViewFragment implements OnClickListener {

		@Override
		public void onClick(View v) {
			Fragment dynFrag = new ShowListFragment();
			FragmentManager fragMan = getFragmentManager();
			
			FragmentTransaction fragTrans = fragMan.beginTransaction();
			
			fragTrans.replace(R.id.fragment_container2, dynFrag);
			
			fragTrans.commit();
		}
		
	}
}
