package me.mrletsplay.shittyauthpatcher.patch;

import java.io.File;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import me.mrletsplay.shittyauthpatcher.util.LibraryPatcher;
import me.mrletsplay.shittyauthpatcher.util.ServerConfiguration;

public class MinecraftPatch implements Patch {

	private static final String
		MINECRAFT = "minecraft",
		MINECRAFT_OUT = "minecraft-out";

	@Override
	public String getDescription() {
		return "Patch for the client minecraft.jar, only needed for older versions of Minecraft that don't use authlib";
	}

	@Override
	public OptionParser createParser() {
		OptionParser parser = Patch.createBaseParser();

		parser.accepts(MINECRAFT, "Path of the minecraft jar file")
			.withRequiredArg().ofType(File.class)
			.required();

		parser.accepts(MINECRAFT_OUT, "Output file for the patched minecraft jar")
			.withRequiredArg().ofType(File.class);
		return parser;
	}

	@Override
	public void patch(OptionSet options) throws Exception {
		ServerConfiguration servers = Patch.getServerConfiguration(options);

		File minecraft = (File) options.valueOf(MINECRAFT);

		File mcOut = minecraft;
		if(options.has(MINECRAFT_OUT)) {
			mcOut = (File) options.valueOf(MINECRAFT_OUT);
		}

		System.out.println("Output for Minecraft: " + mcOut.getAbsolutePath());

		LibraryPatcher.patchMinecraft(minecraft.toPath(), mcOut.toPath(), servers);
	}

}
