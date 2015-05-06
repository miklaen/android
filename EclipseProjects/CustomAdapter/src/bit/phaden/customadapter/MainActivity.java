package bit.phaden.customadapter;



import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;



public class MainActivity extends Activity 
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        String[] navigationItems = {"Services", "Fun Things To Do", "Dining", "Shopping"};
        
        ArrayAdapter<String> navigationAdapter = new ArrayAdapter<String>(	this, 
        																	android.R.layout.simple_list_item_1, 
        																	navigationItems);
        
        ListView navigationList = (ListView) findViewById(R.id.lvNavigation);
        navigationList.setAdapter(navigationAdapter);
        navigationList.setOnItemClickListener(new NavigationListClickHandler());
        
    }

    //====================================================================================
    public class NavigationListClickHandler implements OnItemClickListener
    {
		@Override
		// Changed the names of the arguments. Auto implement gives you arg0, arg1, arg2....
		// AdapterView is the parent class of ListView. The passed in arg is the actual listView that was clicked on
		public void onItemClick(AdapterView<?> list, View view, int position, long id) 
		{
			String clickedItem = (String) list.getItemAtPosition(position).toString();
			
			// for debugging			
			//Toast.makeText(MainActivity.this, clickedItem, Toast.LENGTH_LONG).show();	
			
			Intent goToIntent;
			
			// Could do this with a parallel arary instead
			// Or just make the second argument to the intent constructor somehow...
			
			switch(clickedItem)
			{
			case "Fun Things To Do":
				goToIntent = new Intent(MainActivity.this, ToDoActivity.class);
				break;
			default:
				goToIntent = new Intent(MainActivity.this, MainActivity.class); // Who knows?
			} //end switch
			
			startActivity(goToIntent);
			
		}   // end  NavigationListClickHandler	
    }
  
}
