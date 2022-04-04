package me.mrletsplay.shittyauthpatcher.util;

import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;
import me.mrletsplay.mrcore.json.converter.JSONValue;

public class ServerConfiguration implements JSONConvertible {
	
	@JSONValue
	public String
		authServer,
		accountsServer,
		sessionServer,
		servicesServer;
	
	@JSONConstructor
	public ServerConfiguration() {}
	
}
