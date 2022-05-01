package me.mrletsplay.shittyauthpatcher.version.meta;

import me.mrletsplay.mrcore.json.JSONObject;

public class JavaVersion {
	
	private JSONObject javaVersion;
	
	public JavaVersion(JSONObject javaVersion) {
		this.javaVersion = javaVersion;
	}
	
	public String getComponent() {
		return javaVersion.getString("component");
	}
	
	public int getMajorVersion() {
		return javaVersion.getInt("majorVersion");
	}

}
