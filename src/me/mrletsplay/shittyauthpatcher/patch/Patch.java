package me.mrletsplay.shittyauthpatcher.patch;

import java.net.URI;
import java.util.Arrays;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import me.mrletsplay.shittyauthpatcher.util.ServerConfiguration;

public interface Patch {

	public static final String
		PATCH_ALL = "patch-all",
		PATCH_AUTH = "patch-auth",
		PATCH_ACCOUNTS = "patch-accounts",
		PATCH_SESSION = "patch-session",
		PATCH_SERVICES = "patch-services",
		PATCH_SKINS = "patch-skins";

	public String getDescription();

	public OptionParser createParser();

	public void patch(OptionSet options) throws Exception;

	public static OptionParser createBaseParser() {
		OptionParser parser = new OptionParser();
		parser.acceptsAll(Arrays.asList("help", "h"), "Show help").forHelp();

		parser.accepts(PATCH_ALL, "Shorthand for specifying all 'patch-...' options to be the same server")
			.withRequiredArg().ofType(String.class);

		parser.accepts(PATCH_AUTH, "Base URL of the authentication server (e.g. 'https://auth.example.com/')")
			.requiredUnless(PATCH_ALL)
			.withRequiredArg().ofType(String.class);

		parser.accepts(PATCH_ACCOUNTS, "Base URL of the accounts server (e.g. 'https://accounts.example.com/')")
			.requiredUnless(PATCH_ALL)
			.withRequiredArg().ofType(String.class);

		parser.accepts(PATCH_SESSION, "Base URL of the session server (e.g. 'https://session.example.com/')")
			.requiredUnless(PATCH_ALL)
			.withRequiredArg().ofType(String.class);

		parser.accepts(PATCH_SERVICES, "Base URL of the api server (e.g. 'https://services.example.com/')")
			.requiredUnless(PATCH_ALL)
			.withRequiredArg().ofType(String.class);

		parser.accepts(PATCH_SKINS, "Host name of the skin server (e.g. 'skins.example.com')")
			.requiredUnless(PATCH_ALL)
			.withRequiredArg().ofType(String.class);
		return parser;
	}

	public static ServerConfiguration getServerConfiguration(OptionSet options) {
		ServerConfiguration serverConfiguration = new ServerConfiguration();
		if(options.has(PATCH_ALL)) {
			serverConfiguration.authServer = (String) options.valueOf(PATCH_ALL);
			serverConfiguration.accountsServer = (String) options.valueOf(PATCH_ALL);
			serverConfiguration.sessionServer = (String) options.valueOf(PATCH_ALL);
			serverConfiguration.servicesServer = (String) options.valueOf(PATCH_ALL);
			serverConfiguration.skinHost = URI.create((String) options.valueOf(PATCH_ALL)).getHost();
		}

		// Also allows overriding of specific servers
		if(options.has(PATCH_AUTH)) serverConfiguration.authServer = (String) options.valueOf(PATCH_AUTH);
		if(options.has(PATCH_ACCOUNTS)) serverConfiguration.accountsServer = (String) options.valueOf(PATCH_ACCOUNTS);
		if(options.has(PATCH_SESSION)) serverConfiguration.sessionServer = (String) options.valueOf(PATCH_SESSION);
		if(options.has(PATCH_SERVICES)) serverConfiguration.servicesServer = (String) options.valueOf(PATCH_SERVICES);
		if(options.has(PATCH_SKINS)) serverConfiguration.skinHost = (String) options.valueOf(PATCH_SKINS);
		return serverConfiguration;
	}

}
