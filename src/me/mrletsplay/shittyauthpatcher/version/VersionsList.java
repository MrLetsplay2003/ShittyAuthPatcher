package me.mrletsplay.shittyauthpatcher.version;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import me.mrletsplay.mrcore.http.HttpRequest;
import me.mrletsplay.mrcore.http.HttpResult;
import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.mrcore.json.converter.JSONConverter;

public class VersionsList {

	private List<AbstractMinecraftVersion> versions = new ArrayList<>();
	private AbstractMinecraftVersion latestRelease;
	private AbstractMinecraftVersion latestSnapshot;

	public VersionsList(JSONObject manifestJSON) {
		initVersions(manifestJSON);
	}

	private void initVersions(JSONObject manifestJSON){
		for(Object o : manifestJSON.getJSONArray("versions")) {
			versions.add(JSONConverter.decodeObject((JSONObject) o, DefaultMinecraftVersion.class));
		}

		JSONObject latest = manifestJSON.getJSONObject("latest");

		latestRelease = versions.stream()
				.filter(v -> v.getId().equals(latest.getString("release")))
				.findFirst().orElse(versions.get(0));
		latestSnapshot = versions.stream()
				.filter(v -> v.getId().equals(latest.getString("snapshot")))
				.findFirst().orElse(versions.get(0));
	}

	public List<AbstractMinecraftVersion> getVersions() {
		return versions;
	}

	public AbstractMinecraftVersion getLatestRelease() {
		return latestRelease;
	}

	public AbstractMinecraftVersion getLatestSnapshot() {
		return latestSnapshot;
	}

	public boolean isVersionOlderThan(DefaultMinecraftVersion version, DefaultMinecraftVersion other) {
		return versions.indexOf(version) > versions.indexOf(other);
	}

	public boolean isVersionNewerThan(DefaultMinecraftVersion version, DefaultMinecraftVersion other) {
		return versions.indexOf(version) < versions.indexOf(other);
	}

	public AbstractMinecraftVersion getVersion(String id) {
		return versions.stream()
			.filter(v -> v.getId().equals(id))
			.findFirst().orElse(null);
	}

	public void addVersion(AbstractMinecraftVersion version) {
		versions.add(version);
		versions.sort(Comparator.<AbstractMinecraftVersion, Instant>comparing(v -> v.getReleaseTime()).reversed());
	}

	public void addVersions(Collection<? extends AbstractMinecraftVersion> version) {
		versions.addAll(version);
		versions.sort(Comparator.<AbstractMinecraftVersion, Instant>comparing(v -> v.getReleaseTime()).reversed());
	}

	public void clearVersions() {
		versions.clear();
	}

	/**
	 * Creates a new versions list and initializes it with the default versions from the provided manifest url
	 * @param manifestURL A url to the manifest to initialize the list with
	 * @return A new versions list
	 */
	public static VersionsList load(String manifestURL) {
		HttpResult r = HttpRequest.createGet(manifestURL).execute();
		if(!r.isSuccess()) throw new VersionsLoadException("Failed to load versions: " + r.asString());
		return new VersionsList(r.asJSONObject());
	}

}
