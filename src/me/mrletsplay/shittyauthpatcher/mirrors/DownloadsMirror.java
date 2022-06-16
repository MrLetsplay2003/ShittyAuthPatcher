package me.mrletsplay.shittyauthpatcher.mirrors;

import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;
import me.mrletsplay.mrcore.json.converter.JSONValue;
import me.mrletsplay.shittyauthpatcher.version.VersionsList;

public class DownloadsMirror implements JSONConvertible {

	public static final DownloadsMirror MOJANG = new DownloadsMirror("Mojang","http://launchermeta.mojang.com/mc/game/version_manifest.json", "http://resources.download.minecraft.net/");

	@JSONValue
	private String name;

	@JSONValue
	private String versionManifestURL;

	@JSONValue
	private String assetsURL;

	private VersionsList versions;

	@JSONConstructor
	public DownloadsMirror() {}

	public DownloadsMirror(String name, String versionManifestURL, String assetsURL) {
		this.name = name;
		this.versionManifestURL = versionManifestURL;
		this.assetsURL = assetsURL;
	}

	public String getName() {
		return name;
	}

	public String getVersionManifest() {
		return versionManifestURL;
	}

	public String getAssetsURL() {
		return assetsURL;
	}

	public VersionsList getVersions() {
		if(versions == null) versions = VersionsList.load(versionManifestURL);
		return versions;
	}

}
