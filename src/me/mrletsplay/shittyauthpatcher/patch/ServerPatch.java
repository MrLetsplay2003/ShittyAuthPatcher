package me.mrletsplay.shittyauthpatcher.patch;

import java.io.File;
import java.io.FileNotFoundException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import me.mrletsplay.shittyauthpatcher.util.LibraryPatcher;
import me.mrletsplay.shittyauthpatcher.util.ServerConfiguration;

public class ServerPatch implements Patch {

	private static final String
		SERVER = "server",
		SERVER_OUT = "server-out";

	@Override
	public String getDescription() {
		return "Patch for the default (notchian) server jar, as well as the Spigot server jar";
	}

	@Override
	public OptionParser createParser() {
		OptionParser parser = Patch.createBaseParser();

		parser.accepts(SERVER, "Path to the server jar file")
			.withRequiredArg().ofType(File.class)
			.required();

		parser.accepts(SERVER_OUT, "Output file for the patched server jar")
			.withRequiredArg().ofType(File.class)
			.required();
		return parser;
	}

	@Override
	public void patch(OptionSet options) throws Exception {
		ServerConfiguration servers = Patch.getServerConfiguration(options);

		File serverFile = (File) options.valueOf(SERVER);
		if(!serverFile.exists()) {
			throw new FileNotFoundException(serverFile.getAbsolutePath());
		}

		File out = (File) options.valueOf(SERVER_OUT);
		LibraryPatcher.patchServer(serverFile.toPath(), out.toPath(), servers);
	}

}
