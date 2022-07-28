package me.mrletsplay.shittyauthpatcher.patch;

import java.io.File;
import java.io.FileNotFoundException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import me.mrletsplay.shittyauthpatcher.util.LibraryPatcher;
import me.mrletsplay.shittyauthpatcher.util.ServerConfiguration;

public class AuthlibPatch implements Patch {

	private static final String
		LIBRARY = "library",
		LIBRARY_OUT = "library-out";

	@Override
	public String getDescription() {
		return "Patch for the authlib jar, used both in the client and the server";
	}

	@Override
	public OptionParser createParser() {
		OptionParser parser = Patch.createBaseParser();

		parser.accepts(LIBRARY, "Path of the authlib jar file")
			.withRequiredArg().ofType(File.class)
			.required();

		parser.accepts(LIBRARY_OUT, "Output file for the patched authlib jar")
			.withRequiredArg().ofType(File.class);

		Patch.requireKey(parser);
		return parser;
	}

	@Override
	public void patch(OptionSet options) throws Exception {
		ServerConfiguration servers = Patch.getServerConfiguration(options);

		File clientLibFile = (File) options.valueOf(LIBRARY);
		if(!clientLibFile.exists()) {
			throw new FileNotFoundException(clientLibFile.getAbsolutePath());
		}

		File out = clientLibFile;
		if(options.has(LIBRARY_OUT)) {
			out = (File) options.valueOf(LIBRARY_OUT);
		}

		File key = Patch.getPublicKeyFile(options);

		System.out.println("Output for authlib: " + out.getAbsolutePath());

		LibraryPatcher.patchAuthlib(clientLibFile.toPath(), out.toPath(), servers, key);
	}

}
