package me.mrletsplay.shittyauthpatcher.version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import me.mrletsplay.mrcore.http.HttpRequest;
import me.mrletsplay.mrcore.io.IOUtils;
import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConverter;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;
import me.mrletsplay.mrcore.json.converter.JSONValue;
import me.mrletsplay.shittyauthpatcher.version.meta.MetadataLoadException;
import me.mrletsplay.shittyauthpatcher.version.meta.VersionMetadata;

public class MinecraftVersion implements JSONConvertible {
	
	// TODO: java runtime: https://launchermeta.mojang.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/all.json

	public static final List<MinecraftVersion> VERSIONS = new ArrayList<>();
	public static final MinecraftVersion LATEST_RELEASE, LATEST_SNAPSHOT;

//	public static final String MOD_MAIN_PATH = "net/ddns/minersonline/mc/moded_client/MainClass.class",
//			MOD_MAIN_NAME = "net.ddns.minersonline.mc.moded_client.MainClass";

	
	public static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
		    // date/time
		    .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
		    // offset (hh:mm - "+00:00" when it's zero)
		    .optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd()
		    // offset (hhmm - "+0000" when it's zero)
		    .optionalStart().appendOffset("+HHMM", "+0000").optionalEnd()
		    // offset (hh - "Z" when it's zero)
		    .optionalStart().appendOffset("+HH", "Z").optionalEnd()
		    // create formatter
		    .toFormatter();

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
	
	private JSONObject cachedMeta;
	
	private Instant releaseTime;
	
	private boolean imported;
	
	@JSONConstructor
	private MinecraftVersion() {}
	
	public MinecraftVersion(String id, MinecraftVersionType type, Instant releaseTime) {
		this.id = id;
		this.type = type;
		this.releaseTime = releaseTime;
		this.imported = true;
	}

	public String getId() {
		return id;
	}

	public MinecraftVersionType getType() {
		return type;
	}

	public String getURL() {
		return url;
	}
	
	public boolean isImported() {
		return imported;
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

	@Override
	public void preDeserialize(JSONObject object) {
		releaseTime = Instant.from(TIME_FORMATTER.parse(object.getString("releaseTime")));
	}
	
	private JSONObject downloadMetadata() {
		if(cachedMeta != null) return cachedMeta;
		return cachedMeta = HttpRequest.createGet(url).execute().asJSONObject();
	}

	public VersionMetadata loadMetadata(File cacheFile) throws IOException {
		if(imported) throw new UnsupportedOperationException("Metadata can't be loaded for imported versions");
		if(cacheFile != null) {
			if(!cacheFile.exists()) {
				System.out.println("Downloading " + cacheFile + "...");
				JSONObject dl = downloadMetadata();
				IOUtils.createFile(cacheFile);
				Files.writeString(cacheFile.toPath(), dl.toString());
			}

			JSONObject meta;
			try {
				meta = new JSONObject(Files.readString(cacheFile.toPath()));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			return new VersionMetadata(meta);
		}else {
			return new VersionMetadata(downloadMetadata());
		}
	}
	
	public VersionMetadata loadMetadata() throws IOException {
		return loadMetadata(null);
	}
	
	public static void addVersion(MinecraftVersion version) {
		VERSIONS.add(version);
		VERSIONS.sort(Comparator.<MinecraftVersion, Instant>comparing(v -> {
			try {
				return v.releaseTime == null ? v.releaseTime = v.loadMetadata().getReleaseTime() : v.releaseTime;
			} catch (IOException e) {
				throw new MetadataLoadException(e);
			}
		}).reversed());
	}
	
}
