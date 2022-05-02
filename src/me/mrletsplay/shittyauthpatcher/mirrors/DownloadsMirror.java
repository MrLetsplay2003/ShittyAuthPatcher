package me.mrletsplay.shittyauthpatcher.mirrors;

import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;
import me.mrletsplay.mrcore.json.converter.JSONValue;

public class DownloadsMirror implements JSONConvertible {
	@JSONValue
	public String name;

	@JSONValue
	public String version_manifest;

	@JSONValue
	public String assets_url;

	@JSONValue
	public boolean custom;

	@JSONConstructor
	public DownloadsMirror() {}

	public DownloadsMirror(String name, String version_manifest, String assets_url) {
		this.name = name;
		this.version_manifest = version_manifest;
		this.assets_url = assets_url;
		this.custom = false;
	}
}
