package me.mrletsplay.shittyauthpatcher.mirrors;

public class MojangMirror extends DownloadsMirror {
    public MojangMirror(){
        super("Mojang","http://launchermeta.mojang.com/mc/game/version_manifest.json", "http://resources.download.minecraft.net/");
    }
}
