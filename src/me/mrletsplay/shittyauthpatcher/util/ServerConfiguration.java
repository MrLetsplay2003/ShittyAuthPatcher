package me.mrletsplay.shittyauthpatcher.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;
import me.mrletsplay.mrcore.json.converter.JSONValue;
import me.mrletsplay.mrcore.misc.ByteUtils;
import me.mrletsplay.mrcore.misc.FriendlyException;

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

	/**
	 * Computes a hash string of all servers in this configuration.
	 * This is used to distinguish different configurations without the need for keeping track of all value, e.g. for keeping files related to this server configuration.
	 * @return A hexadecimal MD5 hash string of this configuration
	 */
	public String hashString() {
		String theString = authServer + "\n" + accountsServer + "\n" + sessionServer + "\n" + servicesServer + "\n" + skinHost;
		try {
			MessageDigest d = MessageDigest.getInstance("MD5");
			return ByteUtils.bytesToHex(d.digest(theString.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			throw new FriendlyException("Failed to hash values", e);
		}
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
