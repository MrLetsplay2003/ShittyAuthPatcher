package me.mrletsplay.shittyauthpatcher.version.meta;

public class DownloadableFile {

	private String path;
	private String url;
	
	public DownloadableFile(String path, String url) {
		this.path = path;
		this.url = url;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getURL() {
		return url;
	}
	
}
