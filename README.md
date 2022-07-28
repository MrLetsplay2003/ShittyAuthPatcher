# ShittyAuthPatcher
A command-line tool for patching Mojang's authlib as well as Minecraft

# Download
Prebuilt jar files can be downloaded from [here](https://ci.graphite-official.com/job/ShittyAuthPatcher/19/me.mrletsplay$ShittyAuthPatcher/)

# Compiling the tool
The tool uses Maven for building.

To compile the tool, use
```
$ mvn package
```
which will generate a `ShittyAuthPatcher-VERSION.jar` in the `target` folder

# Usage
The tool can be used to patch both client as well as server jars.

## Client
To patch your Minecraft client, use the `authlib` patch

e.g. with `https://your.server` being a server running [ShittyAuthServer](https://github.com/MrLetsplay2003/ShittyAuthServer):
```sh
$ java -jar ShittyAuthPatcher-VERSION.jar authlib \
    --patch-all https://your.server \
    --patch-download-key \
    --library /path/to/your/.minecraft/libraries/com/mojang/authlib/1.18.2/1.18.2.jar \
    --library-out /path/to/your/.minecraft/libraries/com/mojang/authlib/1.18.2/1.18.2.jar
```
This will patch the authlib jar file in-place.

For older versions of Minecraft (older than 1.7.6), there is no authlib jar file. Instead, you need to patch the minecraft jar itself using the `minecraft` patch.
e.g.:
```sh
$ java -jar ShittyAuthPatcher-VERSION.jar minecraft \
    --patch-all https://your.server \
    --minecraft /home/mr/.minecraft/versions/1.18.2/1.18.2.jar \
    --minecraft-out client.jar
```
Notes:
- If you don't specify the `--*-out` parameters, they will default to replacing the original jar file

## Server
To patch a (notchian) server jar file, use the `server` patch

e.g. with `https://your.server` being a server running [ShittyAuthServer](https://github.com/MrLetsplay2003/ShittyAuthServer):
```sh
$ java -jar ShittyAuthPatcher-VERSION.jar server \
    --patch-all https://your.server \
    --patch-download-key \
    --server server.jar \
    --server-out server-patched.jar
```
Notes:
- `--server-out` is optional and will default to replacing the original jar file if not specified
- This patch currently only works with notchian servers, meaning only the official server jar files provided by Mojang. If you plan on patching your Spigot or Paper server jar, you might need to do some manual work as well.

## Custom (non-ShittyAuth) server setups
You can also provide all of the separate servers when patching the jar file if you're not using a ShittyAuthServer instance. Just use the `--patch-skins`, `--patch-accounts`, `--patch-services`, `--patch-session`, `--patch-skins` instead of `--patch-all`. You must also specify the path to the public key of the server using `--patch-key`.

For further information about these parameters, use the `--help` option on any patch.