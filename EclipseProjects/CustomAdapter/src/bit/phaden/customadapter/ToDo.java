package bit.phaden.customadapter;

import android.graphics.drawable.Drawable;

public class ToDo 
{
	String toDoName;
	Drawable toDoImage;
	
	public ToDo(String toDoName, Drawable toDoImage)
	{
		this.toDoName = toDoName;
		this.toDoImage = toDoImage;
	}

	@Override
	public String toString() 
	{
		return toDoName;
	}

}


