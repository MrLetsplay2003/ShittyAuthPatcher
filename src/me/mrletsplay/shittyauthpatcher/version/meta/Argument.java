package me.mrletsplay.shittyauthpatcher.version.meta;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import me.mrletsplay.mrcore.json.JSONArray;
import me.mrletsplay.mrcore.json.JSONObject;

public class Argument {
	
	private List<String> value;
	private JSONArray rules;
	
	public Argument(JSONObject argument) {
		Object v = argument.get("value");
		if(v instanceof String) {
			this.value = Collections.singletonList((String) v);
		}else {
			this.value = ((JSONArray) v).stream()
					.map(a -> (String) a)
					.collect(Collectors.toList());
		}
		
		this.rules = argument.getJSONArray("rules");
	}
	
	public Argument(String value) {
		this.value = Collections.singletonList(value);
	}
	
	public List<String> getValue() {
		return value;
	}
	
	public JSONArray getRules() {
		return rules;
	}

}
