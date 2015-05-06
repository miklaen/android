package bit.annanma1.fragments;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ShowListFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.listview_fragment, container, false);
		
		// Reference to listView
		ListView cities = (ListView) v.findViewById(R.id.listViewCities);
				
		// Get resources and array of cities
        Resources resources = getResources();
		String[] citiesArray = resources.getStringArray(R.array.cities);
		
		// Create adapter
		ArrayAdapter<String> activityAdapter = new ArrayAdapter<String> (getActivity(), android.R.layout.simple_list_item_1, citiesArray);		
		
		// Set adapter
		cities.setAdapter(activityAdapter);
		
		return v;
	}

}
