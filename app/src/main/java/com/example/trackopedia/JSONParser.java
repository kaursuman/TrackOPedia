package com.example.trackopedia;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {

	public String getString(JSONObject json) {
		String res="false";
		
		try {
			res=json.getString("Value");
		} catch (JSONException e) {
			 res=e.getMessage();
		}
		return res;
	}
}
