package com.tyct.thankyoutrust.parsers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tyct.thankyoutrust.model.Users;

public class UsersJSONParser {
	
	public static List<Users> parseFeed(String content) {
	
		try {
			JSONArray ar = new JSONArray(content);
			List<Users> userList = new ArrayList<>();
			
			for (int i = 0; i < ar.length(); i++) {
				
				JSONObject obj = ar.getJSONObject(i);
				Users user = new Users();
				
				user.setInfoID(obj.getInt("infoID"));
				user.setFirstName(obj.getString("firstName"));
				user.setLastName(obj.getString("lastName"));
				user.setPassword(obj.getString("password"));
				user.setEmail(obj.getString("email"));
				
				userList.add(user);
			}
			
			return userList;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}