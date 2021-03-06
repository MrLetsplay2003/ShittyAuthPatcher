package me.mrletsplay.shittyauthpatcher.version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.shittyauthpatcher.version.meta.MetadataLoadException;

/**
 * Represents a Minecraft version which was imported from the file system
 * @author MrLetsplay2003
 */
public class ImportedMinecraftVersion extends AbstractMinecraftVersion {

	private File metadataFile;

	public ImportedMinecraftVersion(VersionsList list, File metadataFile) {
		this.list = list;
		this.metadataFile = metadataFile;
	}

	public File getMetadataFile() {
		return metadataFile;
	}

	@Override
	protected JSONObject loadMetadataJSON() {
		try {
			return new JSONObject(Files.readString(metadataFile.toPath()));
		}catch(IOException e) {
			throw new MetadataLoadException("Failed to load metadata from file: " + metadataFile.getAbsolutePath());
		}
	}

}
