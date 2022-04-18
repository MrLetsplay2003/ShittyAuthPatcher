package me.mrletsplay.shittyauthpatcher.version.meta;

import me.mrletsplay.mrcore.json.JSONArray;
import me.mrletsplay.mrcore.json.JSONObject;

public class Library {
	
	private JSONObject library;
	
	public Library(JSONObject library) {
		this.library = library;
	}
	
	public String getName() {
		return library.getString("name");
	}
	
	public JSONArray getRules() {
		return library.optJSONArray("rules").orElse(null);
	}
	
	public DownloadableFile getArtifactDownload() {
		if(!library.has("downloads")) return null;
		JSONObject downloads = library.getJSONObject("downloads");
		if(!downloads.has("artifact")) return null;
		JSONObject artifact = downloads.getJSONObject("artifact");
		String path = artifact.getString("path");
		String url = artifact.getString("url");
		return new DownloadableFile(path, url);
	}
	
	public DownloadableFile getNativesDownload(String osName) {
		if(!library.has("natives")) return null;
		JSONObject natives = library.getJSONObject("natives");
		if(!natives.has(osName)) return null;
		JSONObject nativeLib = library.getJSONObject("downloads").getJSONObject("classifiers").getJSONObject(natives.getString(osName).replace("${arch}", "64"));
		String nativesPath = nativeLib.getString("path");
		String nativesURL = nativeLib.getString("url");
		return new DownloadableFile(nativesPath, nativesURL);
	}
	
	public String getGeneratedPath() {
		String name = getName();
		String[] spl = name.split(":");
		
		return spl[0].replace('.', '/') + "/" + spl[1] + "/" + spl[2] + "/" + spl[1] + "-" + spl[2] + ".jar";
	}

}
