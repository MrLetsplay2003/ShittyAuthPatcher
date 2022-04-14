package me.mrletsplay.shittyauthpatcher.util;

import java.util.Objects;

import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;
import me.mrletsplay.mrcore.json.converter.JSONValue;

public class ServerConfiguration implements JSONConvertible {
	
	@JSONValue
	public String
		authServer,
		accountsServer,
		sessionServer,
		servicesServer,
		skinHost;
	
	@JSONConstructor
	public ServerConfiguration() {}

	public ServerConfiguration(String authServer, String accountsServer, String sessionServer, String servicesServer, String skinHost) {
		this.authServer = authServer;
		this.accountsServer = accountsServer;
		this.sessionServer = sessionServer;
		this.servicesServer = servicesServer;
		this.skinHost = skinHost;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountsServer, authServer, servicesServer, sessionServer, skinHost);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerConfiguration other = (ServerConfiguration) obj;
		return Objects.equals(accountsServer, other.accountsServer)
				&& Objects.equals(authServer, other.authServer)
				&& Objects.equals(servicesServer, other.servicesServer)
				&& Objects.equals(sessionServer, other.sessionServer)
				&& Objects.equals(skinHost, other.skinHost);
	}
	
}
