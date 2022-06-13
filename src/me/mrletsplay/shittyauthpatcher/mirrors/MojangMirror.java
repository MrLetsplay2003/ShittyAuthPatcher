package me.mrletsplay.shittyauthpatcher.mirrors;

import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;

public class MojangMirror extends DownloadsMirror implements JSONConvertible {

	@JSONConstructor
	public MojangMirror(){
		super("Mojang","http://launchermeta.mojang.com/mc/game/version_manifest.json", "http://resources.download.minecraft.net/");
	}
}
