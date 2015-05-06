package bit.phaden.customadapter;



import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ToDoActivity extends Activity 
{
	ToDo[] toDoArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_to_do);
		
		// This method loads toDoArray with instances of class ToDo, one for each list item
		initialiseDataArray();
		
		// YOUR CODE HERE
		
		// Make custom adapter
		
		// Get a reference to the ListView with id lvToDo
		
		// Set the ListView's adapter

	} // end onCreate

//====================================================================================
// Custom adapter to return layout with an ImageView and a TextView
 public class ToDoArrayAdapter extends ArrayAdapter<ToDo>
 {

	public ToDoArrayAdapter(Context context, int resource, ToDo[] objects) 
	{
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup container) 
	{
		// YOUR CODE HERE
		
		// Get a LayoutInflater
		
		
		// Inflate custom_list_view and store the returned View in a variable
		
		
		// Get references to the controls in custom_list_view. Get both the ImageView and the TextView
		
		
		// Get the current ToDo instance. Use the Adapter base class's getItem command
		
		
		// Use the data fields of the current ToDo instance to initialise the View controls correctly
		
		// Return your customview
		
	}
 }// end class ToDoArrayAdapter

//====================================================================================

private void initialiseDataArray()
{
	Resources resourceMachine = getResources();
	
	String[] activityNames = resourceMachine.getStringArray(R.array.activity_names);
	String[] imageFileNames = resourceMachine.getStringArray(R.array.image_file_names);
	
	toDoArray = new ToDo[activityNames.length];
	
	for (int i=0; i<activityNames.length; i++)
	{
		String activity = activityNames[i];
		
		String imageFile = imageFileNames[i];
		int resourceId = resourceMachine.getIdentifier(imageFile, "drawable", getPackageName());
		Drawable image = resourceMachine.getDrawable(resourceId);
		
		ToDo currentToDo = new ToDo(activity, image);
		
		toDoArray[i] = currentToDo;
	}    	
	
}// end initialiseDataArray
	//====================================================================================

} // end class ToDoActivity