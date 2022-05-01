package me.mrletsplay.shittyauthpatcher.mirrors;

import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;
import me.mrletsplay.mrcore.json.converter.JSONValue;

public class DownloadsMirror implements JSONConvertible {
	@JSONValue
	public String version_manifest;

	@JSONValue
	public String assets_url;

	@JSONConstructor
	public DownloadsMirror() {}

	public DownloadsMirror(String version_manifest, String assets_url) {
		this.version_manifest = version_manifest;
		this.assets_url = assets_url;
	}
}
