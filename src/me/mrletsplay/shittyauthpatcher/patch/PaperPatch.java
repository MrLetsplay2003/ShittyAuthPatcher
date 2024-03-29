package me.mrletsplay.shittyauthpatcher.patch;

import java.io.File;
import java.io.FileNotFoundException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import me.mrletsplay.shittyauthpatcher.util.LibraryPatcher;
import me.mrletsplay.shittyauthpatcher.util.ServerConfiguration;

public class PaperPatch implements Patch {

	private static final String
		SERVER = "server",
		SERVER_OUT = "server-out";

	@Override
	public String getDescription() {
		return "Patch for the official Paper server jar (1.18+ only)";
	}

	@Override
	public OptionParser createParser() {
		OptionParser parser = Patch.createBaseParser();

		parser.accepts(SERVER, "Path to the server jar file")
			.withRequiredArg().ofType(File.class)
			.required();

		parser.accepts(SERVER_OUT, "Output file for the patched server jar")
			.withRequiredArg().ofType(File.class);

		Patch.requireKey(parser);
		return parser;
	}

	@Override
	public void patch(OptionSet options) throws Exception {
		ServerConfiguration servers = Patch.getServerConfiguration(options);

		File serverFile = (File) options.valueOf(SERVER);
		if(!serverFile.exists()) {
			throw new FileNotFoundException(serverFile.getAbsolutePath());
		}

		File out = serverFile;
		if(options.has(SERVER_OUT)) {
			out = (File) options.valueOf(SERVER_OUT);
		}

		File key = Patch.getPublicKeyFile(options);

		System.out.println("Output for authlib: " + out.getAbsolutePath());

		LibraryPatcher.patchPaper(serverFile.toPath(), out.toPath(), servers, key);
	}

}
