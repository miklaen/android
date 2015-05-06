package bit.annanma1.musicschool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class MusicSchoolMainActivity extends Activity {

	// Global variables
	public String selectedInstrument;
	public String selectedMonth;
	static MusicSchoolMainActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_school_main);
		activity = this;

		// Radio group bind/handler
		RadioGroup instrumentGroup = (RadioGroup) findViewById(R.id.radioGroupInstruments);
		instrumentGroup.setOnCheckedChangeListener(new radioGroupListener());

		// Set the default instrument as Accordion, in case the enroll button is
		// clicked without first choosing an instrument
		RadioButton instrument = (RadioButton) findViewById(R.id.radio_accordion);
		selectedInstrument = instrument.getText().toString();

		// Button bind/handler
		Button submit = (Button) findViewById(R.id.buttonSubmit);
		submit.setOnClickListener(new ClickHandler());

		// Get string array of months for spinner adapter
		Resources resources = getResources();
		String[] months = resources.getStringArray(R.array.months);
		
		// Bind spinner
		Spinner monthSpinner = (Spinner) findViewById(R.id.spinnerMonth);
		ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, months);
		monthSpinner.setAdapter(monthAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.music_school_main, menu);
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

	public class radioGroupListener implements
			RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// Bind the selected instrument
			RadioButton instrument = (RadioButton) findViewById(checkedId);
			selectedInstrument = instrument.getText().toString();
		}

	}

	public class ClickHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			DialogFragment confirm = LessonConfirmationFragment
					.newInstance(selectedInstrument);

			confirm.show(getFragmentManager(), "confirm");
		}

	}

	public static class LessonConfirmationFragment extends DialogFragment {

		public static LessonConfirmationFragment newInstance(String instrument) {

			LessonConfirmationFragment frag = new LessonConfirmationFragment();

			// Supply instrument input as an argument.
			Bundle args = new Bundle();
			args.putString("instrument", instrument);
			frag.setArguments(args);

			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			String instrument = getArguments().getString("instrument");

			Builder builder = new AlertDialog.Builder(getActivity());

			builder.setIcon(R.drawable.music);
			builder.setTitle("Really enroll in " + instrument + " lessons?");
			builder.setPositiveButton("Yes", activity.new YesButtonHandler());
			builder.setNegativeButton("No", activity.new NoButtonHandler());

			Dialog dialog = builder.create();

			return dialog;
		}

	}

	public class YesButtonHandler implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {

			// Bind the feedback textView
			TextView TextEnrolled = (TextView) findViewById(R.id.textViewEnrolled);
			// Bind the spinner
			Spinner monthSpinner = (Spinner) findViewById(R.id.spinnerMonth);
			// Retrieve the string of the selected spinner value
			selectedMonth = (String) monthSpinner.getSelectedItem();

			TextEnrolled.setText("You have enrolled for " + selectedInstrument
					+ " lessons, starting in " + selectedMonth);
		}

	}

	public class NoButtonHandler implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// Bind the feedback textView
			TextView TextEnrolled = (TextView) findViewById(R.id.textViewEnrolled);

			TextEnrolled.setText("Oh well...");
		}

	}

}