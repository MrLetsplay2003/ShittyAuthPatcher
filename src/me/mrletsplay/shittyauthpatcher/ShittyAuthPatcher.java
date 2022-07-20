package me.mrletsplay.shittyauthpatcher;

import java.util.LinkedHashMap;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import me.mrletsplay.shittyauthpatcher.patch.AuthlibPatch;
import me.mrletsplay.shittyauthpatcher.patch.MinecraftPatch;
import me.mrletsplay.shittyauthpatcher.patch.Patch;
import me.mrletsplay.shittyauthpatcher.patch.ServerPatch;

public class ShittyAuthPatcher {

	private static final Map<String, Patch> PATCHES = new LinkedHashMap<>();

	static {
		PATCHES.put("authlib", new AuthlibPatch());
		PATCHES.put("minecraft", new MinecraftPatch());
		PATCHES.put("server", new ServerPatch());
	}

	public static void main(String[] args) throws Exception {
		String patch;
		if(args.length == 0 || !PATCHES.containsKey(patch = args[0])) {
			System.out.println("First argument must be a valid patch:");
			printPatches();
			return;
		}

		Patch p = PATCHES.get(patch);

		OptionParser parser = p.createParser();
		OptionSet opts = parser.parse(args);
		if(opts.has("help") || !opts.hasOptions()) {
			parser.printHelpOn(System.out);
			return;
		}

		p.patch(opts);
	}

	private static void printPatches() {
		PATCHES.forEach((name, patch) -> System.out.println(name + " - " + patch.getDescription()));
	}

}
