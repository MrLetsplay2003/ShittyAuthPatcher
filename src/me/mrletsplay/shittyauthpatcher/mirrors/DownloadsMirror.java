package me.mrletsplay.shittyauthpatcher.mirrors;

import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;
import me.mrletsplay.mrcore.json.converter.JSONValue;

public class DownloadsMirror implements JSONConvertible {
	@JSONValue
	public String name;

	@JSONValue
	public String versionManifest;

	@JSONValue
	public String assetsURL;

	@JSONValue
	public boolean custom;

	@JSONConstructor
	public DownloadsMirror() {}

	public DownloadsMirror(String name, String versionManifest, String assetsURL) {
		this.name = name;
		this.versionManifest = versionManifest;
		this.assetsURL = assetsURL;
		this.custom = false;
	}
}
