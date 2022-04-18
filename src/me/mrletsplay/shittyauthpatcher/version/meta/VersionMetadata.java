package me.mrletsplay.shittyauthpatcher.version.meta;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.shittyauthpatcher.version.MinecraftVersion;

public class VersionMetadata {
	
	private JSONObject meta;
	private VersionMetadata inheritsFromMeta;
	
	public VersionMetadata(JSONObject meta) {
		this.meta = meta;
		if(meta.has("inheritsFrom")) {
			String i = meta.getString("inheritsFrom");
			MinecraftVersion inherit = MinecraftVersion.getVersion(i);
			if(inherit == null) throw new MetadataLoadException("Unknown version in inheritsFrom field: " + i);
			try {
				this.inheritsFromMeta = inherit.loadMetadata();
			} catch (IOException e) {
				throw new MetadataLoadException(e);
			}
		}
	}
	
	public String getId() {
		return meta.getString("id");
	}
	
	public List<Library> getLibraries() {
		List<Library> libs = new ArrayList<>();
		meta.getJSONArray("libraries").forEach(l -> libs.add(new Library((JSONObject) l)));
		if(inheritsFromMeta != null) libs.addAll(inheritsFromMeta.getLibraries());
		return libs;
	}
	
	public String getClientDownloadURL() {
		if(!meta.has("downloads")) {
			if(inheritsFromMeta == null) throw new MetadataLoadException("Invalid metadata: No download url found");
			return inheritsFromMeta.getClientDownloadURL();
		}
		
		return meta.getJSONObject("downloads").getJSONObject("client").getString("url");
	}
	
	public AssetIndex getAssetIndex() {
		if(!meta.has("assetIndex")) {
			if(inheritsFromMeta == null) throw new MetadataLoadException("Invalid metadata: No asset index found");
			return inheritsFromMeta.getAssetIndex();
		}
		
		return new AssetIndex(meta.getJSONObject("assetIndex"));
	}
	
	public String getAssets() {
		if(!meta.has("assets")) {
			if(inheritsFromMeta == null) throw new MetadataLoadException("Invalid metadata: No assets found");
			return inheritsFromMeta.getAssets();
		}
		
		return meta.getString("assets");
	}

	public JavaVersion getJavaVersion() {
		if(!meta.has("javaVersion")) {
			if(inheritsFromMeta == null) throw new MetadataLoadException("Invalid metadata: No java version found");
			return inheritsFromMeta.getJavaVersion();
		}
		
		return new JavaVersion(meta.getJSONObject("javaVersion"));
	}
	
	public List<Argument> getGameArguments() {
		if(!meta.has("arguments") && !meta.has("minecraftArguments")) {
			if(inheritsFromMeta == null) throw new MetadataLoadException("Invalid metadata: No arguments found");
			return inheritsFromMeta.getGameArguments();
		}
		
		if(meta.has("arguments")) {
			JSONObject arguments = meta.getJSONObject("arguments");

			List<Argument> args = new ArrayList<>();
			if(arguments.has("game")) args.addAll(arguments.getJSONArray("game").stream()
					.map(a -> a instanceof String ? new Argument((String) a) : new Argument((JSONObject) a))
					.collect(Collectors.toList()));
			if(inheritsFromMeta != null) args.addAll(inheritsFromMeta.getGameArguments());
			return args;
		}else {
			return Arrays.stream(meta.getString("minecraftArguments").split(" "))
					.map(a -> new Argument(a))
					.collect(Collectors.toList());
		}
	}
	
	public List<Argument> getJVMArguments() {
		if(!meta.has("arguments")) {
			if(inheritsFromMeta == null) return Collections.emptyList();
			return inheritsFromMeta.getJVMArguments();
		}
		
		JSONObject arguments = meta.getJSONObject("arguments");
		
		List<Argument> args = new ArrayList<>();
		if(arguments.has("jvm")) args.addAll(arguments.getJSONArray("jvm").stream()
				.map(a -> a instanceof String ? new Argument((String) a) : new Argument((JSONObject) a))
				.collect(Collectors.toList()));
		if(inheritsFromMeta != null) args.addAll(inheritsFromMeta.getJVMArguments());
		return args;
	}
	
	public boolean usesLegacyArgs() {
		return meta.has("minecraftArguments") || (inheritsFromMeta != null && inheritsFromMeta.usesLegacyArgs());
	}
	
	public String getType() {
		return meta.getString("type");
	}
	
	public String getMainClass() {
		if(!meta.has("mainClass")) {
			if(inheritsFromMeta == null) throw new MetadataLoadException("Invalid metadata: No main class found");
			return inheritsFromMeta.getMainClass();
		}
		
		return meta.getString("mainClass");
	}
	
	public Instant getReleaseTime() {
		return Instant.from(MinecraftVersion.TIME_FORMATTER.parse(meta.getString("releaseTime")));
	}
	
}
