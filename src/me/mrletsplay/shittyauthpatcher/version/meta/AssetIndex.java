package me.mrletsplay.shittyauthpatcher.version.meta;

import me.mrletsplay.mrcore.json.JSONObject;

public class AssetIndex {
	
	private JSONObject assetIndex;

	public AssetIndex(JSONObject assetIndex) {
		this.assetIndex = assetIndex;
	}
	
	public String getID() {
		return assetIndex.getString("id");
	}
	
	public String getURL() {
		return assetIndex.getString("url");
	}

}
