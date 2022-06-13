# ShittyAuthPatcher
A command-line tool for patching Mojang's authlib as well as Minecraft

# Download
Prebuilt jar files can be downloaded from [here](https://ci.graphite-official.com/job/ShittyAuthPatcher/)

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
To patch a client jar file, use the `--client` option

e.g. with `https://your.server` being a server running [ShittyAuthServer](https://github.com/MrLetsplay2003/ShittyAuthServer):
```sh
$ java -jar ShittyAuthPatcher-VERSION.jar \
    --client \
    --all-server https://your.server \
    --client-lib /path/to/your/.minecraft/libraries/com/mojang/authlib/1.18.2/1.18.2.jar \
    --client-lib-out authlib.jar \
    --client-patch-minecraft \
    --client-minecraft /home/mr/.minecraft/versions/1.18.2/1.18.2.jar \
    --client-minecraft-out client.jar
```
Notes:
- `--client-lib-out` and `--client-minecraft-out` are optional and will default to replacing the original jar file if not specified
- `--client-patch-minecraft` and the `--client-minecraft-...` options are only needed for Minecraft versions older than 1.7.6

## Server
To patch a server jar file, use the `--server` option

e.g. with `https://your.server` being a server running [ShittyAuthServer](https://github.com/MrLetsplay2003/ShittyAuthServer):
```sh
$ java -jar ShittyAuthPatcher-VERSION.jar \
    --server \
    --all-server https://your.server \
    --server-jar server.jar \
    --server-out server-patched.jar
```
Notes:
- `--server-out` is optional and will default to replacing the original jar file if not specified
