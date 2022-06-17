package me.mrletsplay.shittyauthpatcher.version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;

import me.mrletsplay.mrcore.io.IOUtils;
import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.shittyauthpatcher.version.meta.MetadataLoadException;
import me.mrletsplay.shittyauthpatcher.version.meta.VersionMetadata;

public abstract class AbstractMinecraftVersion {

	protected String id;
	protected MinecraftVersionType type;
	protected Instant releaseTime;
	protected VersionsList list; // Used to resolve inheritsFrom in metadata
	protected VersionMetadata cachedMeta;

	public void setList(VersionsList list) {
		this.list = list;
	}

	public VersionsList getList() {
		return list;
	}

	public String getId() {
		if(id != null) return id;
		return id = getMetadata().getId();
	}

	public MinecraftVersionType getType() {
		if(type != null) return type;
		return type = getMetadata().getType();
	}

	public Instant getReleaseTime() {
		if(releaseTime != null) return releaseTime;
		return releaseTime = getMetadata().getReleaseTime();
	}

	public boolean isOlderThan(AbstractMinecraftVersion other) {
		return releaseTime.isBefore(other.releaseTime);
	}

	public boolean isNewerThan(AbstractMinecraftVersion other) {
		return releaseTime.isAfter(other.releaseTime);
	}

	@Override
	public String toString() {
		return getId();
	}

	protected abstract JSONObject loadMetadataJSON() throws MetadataLoadException;

	public VersionMetadata getMetadata() {
		try {
			if(cachedMeta != null) return cachedMeta;
			return cachedMeta = loadMetadata(null);
		} catch (IOException ignored) { // Exception cannot happen, because we're not using a cache file
			return null;
		}
	}

	public VersionMetadata loadMetadata(File cacheFile) throws MetadataLoadException, IOException {
		if(cacheFile != null) {
			if(!cacheFile.exists()) {
				System.out.println("Downloading " + cacheFile + "...");
				JSONObject dl = loadMetadataJSON();
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

			return new VersionMetadata(this, meta);
		}else {
			return new VersionMetadata(this, loadMetadataJSON());
		}
	}

	public VersionMetadata loadMetadata() throws MetadataLoadException {
		try {
			return loadMetadata(null);
		} catch (IOException ignored) { // Cannot happen because we're not using a cache file
			return null;
		}
	}

}
