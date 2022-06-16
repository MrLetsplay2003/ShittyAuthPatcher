package me.mrletsplay.shittyauthpatcher.version;

import me.mrletsplay.mrcore.http.HttpRequest;
import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;
import me.mrletsplay.mrcore.json.converter.JSONValue;
import me.mrletsplay.shittyauthpatcher.version.meta.MetadataLoadException;

/**
 * Represents a Minecraft version which is provided by a downloads mirror
 * @author MrLetsplay2003
 */
public class DefaultMinecraftVersion extends AbstractMinecraftVersion implements JSONConvertible {

	@JSONValue
	private String url;

	@JSONConstructor
	private DefaultMinecraftVersion() {}

	@Override
	protected JSONObject loadMetadataJSON() throws MetadataLoadException {
		return HttpRequest.createGet(url).execute().asJSONObject();
	}

	@Override
	public void preDeserialize(JSONObject object) {
		this.id = object.getString("id");
		this.type = MinecraftVersionType.decodePrimitive(object.getString("type"));
	}

}
