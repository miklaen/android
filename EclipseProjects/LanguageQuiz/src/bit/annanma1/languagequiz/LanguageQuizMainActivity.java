package bit.annanma1.languagequiz;

import java.util.Locale;
import java.util.Random;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class LanguageQuizMainActivity extends Activity {

	// Variables to be used in onCreate and subclasses
	public Resources resources;
	public RadioButton selectedGender;
	public TextView label;
	public TextView tvCount;
	public TextView tvScore;
	public int score;
	public int imageCount;
	private String[] imagesArray;
	private String[] nounsArray;
	boolean checkAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_quiz_main);
        
        // Reset score and current image to zero
        score = 0;        
        imageCount = 0;    
               
        // Get resources and array of image names
        resources = getResources();
        imagesArray = resources.getStringArray(R.array.image_names);
        
     	// Get array of nouns to display in label
        nounsArray = resources.getStringArray(R.array.nouns);            
        
        // Radio group bind/handler
        RadioGroup instrumentGroup = (RadioGroup) findViewById(R.id.radioGroupGender);
        instrumentGroup.setOnCheckedChangeListener(new radioGroupListener());        

        // Button bind/handler
        Button submit = (Button) findViewById(R.id.buttonSubmit);       
        submit.setOnClickListener(new ClickHandler());       
        
        // Set the default gender as Der, in case the submit button is clicked without first choosing a gender
        selectedGender = (RadioButton) findViewById(R.id.radioDer);
        
     	// Shuffle order of images/nouns
        setImageOrder();
        
        // Display the first question
        nextQuestion();       
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.language_quiz_main, menu);
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
    
    public void setImageOrder() {
    	Random rGen = new Random();
    	
    	for (int i = 0; i < 100; i++) {
    		int val1 = rGen.nextInt(imagesArray.length);
    		int val2 = rGen.nextInt(imagesArray.length);
    		String temp;
    		
    		// Change order of images array
    		temp = imagesArray[val1];
    		imagesArray[val1] = imagesArray[val2];
    		imagesArray[val2] = temp;
    		
    		// Change order of nouns array
    		temp = nounsArray[val1];
    		nounsArray[val1] = nounsArray[val2];
    		nounsArray[val2] = temp;
		}
    }
    
    public void nextQuestion() {
    	
    	// Get resources
    	resources = getResources();
    	
        // Get image resource id
        int resourceId = resources.getIdentifier(imagesArray[imageCount], "drawable", getPackageName());  
        
        tvCount = (TextView) findViewById(R.id.textViewImageCount);
        tvCount.setText("Question: " + (imageCount + 1) + "/" + imagesArray.length);
        
        // Display score
        tvScore = (TextView) findViewById(R.id.textViewScore);
        tvScore.setText("Correct: " + score + "/" + imageCount);
        
        // Set image from resource id
        ImageView ivDisplay = (ImageView) findViewById(R.id.ivDisplay);
        ivDisplay.setImageResource(resourceId);
        
        // Set image label from resource id
        label = (TextView) findViewById(R.id.textViewLabel);
        label.setText(nounsArray[imageCount]);
        
        // Set button text
        Button submit = (Button) findViewById(R.id.buttonSubmit);  
        submit.setText("Select");
        
        // Set button to 'check answer' mode
        checkAnswer = true;
        
        // Reset feedback text
        TextView feedback = (TextView) findViewById(R.id.textViewShowFeedback);
        feedback.setText("");
    }
    
    public class radioGroupListener implements RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// Bind the selected gender
			selectedGender = (RadioButton) findViewById(checkedId);			
		}
    	
    }
    
    public class ClickHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
	
			if (checkAnswer) {
				// Change the button text
				Button submit = (Button) findViewById(R.id.buttonSubmit); 
				submit.setText("Continue");
				
				// Retrieve the string of the selected gender
				String gender = selectedGender.getText().toString().toLowerCase(Locale.ENGLISH);
				
				// Retrieve the first 3 letters of the image name as the correct answer
				String answer = imagesArray[imageCount].substring(0, 3);
				
				TextView feedback = (TextView) findViewById(R.id.textViewShowFeedback);
				
				// Provide feedback
				if (gender.equals(answer)) {
					score++;
					feedback.setText("Correct!");
				}
				else {
					feedback.setText("The correct answer is " + answer);
				}
				
				// Change the button to 'next question' mode
				checkAnswer = false;
			}
			else {
				// Increment imageCount
		    	imageCount++;
		    	
		    	if (imageCount > imagesArray.length - 1) {
		    		Intent newActivity = new Intent(LanguageQuizMainActivity.this, ResultsActivity.class);
		    		newActivity.putExtra("score", score);
					startActivity(newActivity);
					finish();
				}
		    	else
		        nextQuestion();
			}
			
			
		}
    	
    }
}
