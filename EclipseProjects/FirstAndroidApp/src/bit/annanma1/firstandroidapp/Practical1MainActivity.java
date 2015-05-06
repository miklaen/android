package bit.annanma1.firstandroidapp;

import java.util.Random;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class Practical1MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical1_main);
        TextView txtFridays = (TextView)findViewById(R.id.txtFridays);
        txtFridays.setText(fridays());
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.practical1_main, menu);
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
    
    public String fridays() {
    	Resources resourceResolver = getResources();
        int datesArray[] = resourceResolver.getIntArray(R.array.FebFridays);
        
        String fridays = "February Fridays on:";
        
        for (int i = 0; i < datesArray.length; i++) {
			fridays = fridays.concat(" " + Integer.toString(datesArray[i]));
		}
        
        return fridays;
    }
    
//    public String dogBreed() {
//    	//Pick a string
//    	String dogBreed = "";
//    	
//    	Random rGen = new Random();
//    	int dogValue = rGen.nextInt(4);
//    	
//    	switch(dogValue)
//    	{
//    	case 0:
//    		dogBreed = "Poodle";
//    		break;
//    	case 1:
//    		dogBreed = "Labrador";
//    		break;
//    	case 2:
//    		dogBreed = "Shar Pei";
//    		break;
//    	case 3:
//    		dogBreed = "Newfoundland";
//    		break;
//    	}
//    	
//    	return dogBreed;
//    }
}
