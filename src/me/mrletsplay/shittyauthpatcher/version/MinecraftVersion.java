package me.mrletsplay.shittyauthpatcher.version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import me.mrletsplay.mrcore.http.HttpRequest;
import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConverter;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;
import me.mrletsplay.mrcore.json.converter.JSONValue;

public class MinecraftVersion implements JSONConvertible {
	
	// TODO: java runtime: https://launchermeta.mojang.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/all.json

	public static final List<MinecraftVersion> VERSIONS = new ArrayList<>();
	public static final MinecraftVersion LATEST_RELEASE, LATEST_SNAPSHOT;
//	public static final String MOD_MAIN_PATH = "net/ddns/minersonline/mc/moded_client/MainClass.class",
//			MOD_MAIN_NAME = "net.ddns.minersonline.mc.moded_client.MainClass";

	static {
		JSONObject obj = HttpRequest.createGet("https://minersonline.ddns.net/download/version_manifest.json").execute().asJSONObject();
		
		for(Object o : obj.getJSONArray("versions")) {
			VERSIONS.add(JSONConverter.decodeObject((JSONObject) o, MinecraftVersion.class));
		}
		
		JSONObject latest = obj.getJSONObject("latest");
		
		LATEST_RELEASE = VERSIONS.stream()
				.filter(v -> v.getId().equals(latest.getString("release")))
				.findFirst().orElse(VERSIONS.get(0));
		LATEST_SNAPSHOT = VERSIONS.stream()
				.filter(v -> v.getId().equals(latest.getString("snapshot")))
				.findFirst().orElse(VERSIONS.get(0));
	}

	@JSONValue
	private String id;
	
	@JSONValue
	private MinecraftVersionType type;
	
	@JSONValue
	private String url;
	
	@JSONConstructor
	private MinecraftVersion() {}
	
	public String getId() {
		return id;
	}

	public MinecraftVersionType getType() {
		return type;
	}

	public String getURL() {
		return url;
	}

	@Override
	public String toString() {
		return id;
	}
	
	public boolean isOlderThan(MinecraftVersion other) {
		return VERSIONS.indexOf(this) > VERSIONS.indexOf(other);
	}
	
	public boolean isNewerThan(MinecraftVersion other) {
		return VERSIONS.indexOf(this) < VERSIONS.indexOf(other);
	}
	
	public static MinecraftVersion getVersion(String id) {
		return VERSIONS.stream()
				.filter(v -> v.getId().equals(id))
				.findFirst().orElse(null);
	}

	public JSONObject loadMetadata(File cacheFile) throws IOException {
		if(cacheFile != null) {
			if(!cacheFile.exists()) {
				System.out.println("Downloading " + cacheFile + "...");
				HttpRequest.createGet(url).execute().transferTo(cacheFile);
			}

			JSONObject meta;
			try {
				meta = new JSONObject(Files.readString(cacheFile.toPath()));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			return meta;
		}else {
			return HttpRequest.createGet(url).execute().asJSONObject();
		}
	}
	
	public JSONObject loadMetadata() throws IOException {
		return loadMetadata(null);
	}
	
}
