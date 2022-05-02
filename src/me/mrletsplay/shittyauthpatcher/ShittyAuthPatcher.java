package me.mrletsplay.shittyauthpatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.mrletsplay.shittyauthpatcher.util.LibraryPatcher;
import me.mrletsplay.shittyauthpatcher.util.ServerConfiguration;

public class ShittyAuthPatcher {

	public static void main(String[] args) throws IOException {
		OptionParser parser = new OptionParser();
		parser.acceptsAll(Arrays.asList("help", "h"), "Show help").forHelp();

		// Client

		parser.acceptsAll(Arrays.asList("client"), "Patch a client jar");

		OptionSpec<File> clientLib = parser.accepts("client-lib", "Path of the authlib jar file")
				.requiredIf("client")
				.withRequiredArg().ofType(File.class);

		OptionSpec<File> clientLibOut = parser.accepts("client-lib-out", "Output file for the patched authlib jar")
				.withRequiredArg().ofType(File.class);

		parser.accepts("client-patch-minecraft", "Also patch the minecraft jar file")
				.availableIf("client");

		OptionSpec<File> clientMinecraft = parser.accepts("client-minecraft", "Path of the minecraft jar file")
				.requiredIf("client-patch-minecraft")
				.withRequiredArg().ofType(File.class);

		OptionSpec<File> clientMinecraftOut = parser.accepts("client-minecraft-out", "Output file for the patched minecraft jar")
				.availableIf("client-patch-minecraft")
				.withRequiredArg().ofType(File.class);

		// Server

		parser.acceptsAll(Arrays.asList("server"), "Patch a server jar");

		OptionSpec<File> serverJar = parser.accepts("server-jar", "Path to the server jar file")
				.requiredIf("server")
				.withRequiredArg().ofType(File.class);

		OptionSpec<File> serverOut = parser.accepts("server-out", "Output file for the patched server jar")
				.availableIf("server")
				.withRequiredArg().ofType(File.class);

		// Common

		OptionSpec<String> allServer = parser.accepts("all-server", "Shorthand for specifying 'auth-server', 'accounts-server', 'session-server', 'services-server' and 'skin-host' to be the same server")
				.withRequiredArg().ofType(String.class);

		OptionSpec<String> authServer = parser.accepts("auth-server", "Base URL of the authentication server (e.g. 'https://auth.example.com/')")
				.requiredUnless("all-server")
				.withRequiredArg().ofType(String.class);

		OptionSpec<String> accountsServer = parser.accepts("accounts-server", "Base URL of the accounts server (e.g. 'https://accounts.example.com/')")
				.requiredUnless("all-server")
				.withRequiredArg().ofType(String.class);

		OptionSpec<String> sessionServer = parser.accepts("session-server", "Base URL of the session server (e.g. 'https://session.example.com/')")
				.requiredUnless("all-server")
				.withRequiredArg().ofType(String.class);

		OptionSpec<String> servicesServer = parser.accepts("services-server", "Base URL of the api server (e.g. 'https://services.example.com/')")
				.requiredUnless("all-server")
				.withRequiredArg().ofType(String.class);

		OptionSpec<String> skinHost = parser.accepts("skin-host", "Host name of the skin server (e.g. 'skins.example.com')")
				.requiredUnless("all-server")
				.withRequiredArg().ofType(String.class);

		OptionSet opts = parser.parse(args);
		if(opts.has("help") || !opts.hasOptions()) {
			parser.printHelpOn(System.out);
			return;
		}

		ServerConfiguration serverConfiguration = new ServerConfiguration();
		if(opts.has(allServer)) {
			serverConfiguration.authServer = opts.valueOf(allServer);
			serverConfiguration.accountsServer = opts.valueOf(allServer);
			serverConfiguration.sessionServer = opts.valueOf(allServer);
			serverConfiguration.servicesServer = opts.valueOf(allServer);
			serverConfiguration.skinHost = new URL(opts.valueOf(allServer)).getHost();
		}

		// Also allows overriding of specific servers
		if(opts.has(authServer)) serverConfiguration.authServer = opts.valueOf(authServer);
		if(opts.has(accountsServer)) serverConfiguration.accountsServer = opts.valueOf(accountsServer);
		if(opts.has(sessionServer)) serverConfiguration.sessionServer = opts.valueOf(sessionServer);
		if(opts.has(servicesServer)) serverConfiguration.servicesServer = opts.valueOf(servicesServer);
		if(opts.has(skinHost)) serverConfiguration.skinHost = opts.valueOf(skinHost);

		if(opts.has("client")) {
			File clientLibFile = opts.valueOf(clientLib);
			if(!clientLibFile.exists()) {
				throw new FileNotFoundException(clientLibFile.getAbsolutePath());
			}

			File out = clientLibFile;
			if(opts.has(clientLibOut)) {
				out = opts.valueOf(clientLibOut);
			}

			System.out.println("Output for authlib: " + out.getAbsolutePath());

			LibraryPatcher.patchAuthlib(clientLibFile.toPath(), out.toPath(), serverConfiguration);

			if(opts.has("client-patch-minecraft")) {
				File minecraft = opts.valueOf(clientMinecraft);

				File mcOut = minecraft;
				if(opts.has(clientMinecraftOut)) {
					mcOut = opts.valueOf(clientMinecraftOut);
				}

				System.out.println("Output for minecraft: " + mcOut.getAbsolutePath());

				LibraryPatcher.patchMinecraft(minecraft.toPath(), mcOut.toPath(), serverConfiguration);
			}
		}

		if(opts.has("server")) {
			File serverFile = opts.valueOf(serverJar);
			if(!serverFile.exists()) {
				throw new FileNotFoundException(serverFile.getAbsolutePath());
			}

			File out = opts.valueOf(serverOut);
			LibraryPatcher.patchServer(serverFile.toPath(), out.toPath(), serverConfiguration);
		}
	}

}
