package me.mrletsplay.shittyauthpatcher.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import me.mrletsplay.mrcore.misc.classfile.ByteCode;
import me.mrletsplay.mrcore.misc.classfile.ClassField;
import me.mrletsplay.mrcore.misc.classfile.ClassFile;
import me.mrletsplay.mrcore.misc.classfile.ClassMethod;
import me.mrletsplay.mrcore.misc.classfile.Instruction;
import me.mrletsplay.mrcore.misc.classfile.InstructionInformation;
import me.mrletsplay.mrcore.misc.classfile.attribute.AttributeCode;
import me.mrletsplay.mrcore.misc.classfile.pool.entry.ConstantPoolEntry;
import me.mrletsplay.mrcore.misc.classfile.pool.entry.ConstantPoolFieldRefEntry;
import me.mrletsplay.mrcore.misc.classfile.pool.entry.ConstantPoolStringEntry;
import me.mrletsplay.mrcore.misc.classfile.util.ClassFileUtils;

public class LibraryPatcher {

	private static final String
			DEFAULT_AUTH_SERVER = "https://authserver.mojang.com",
			DEFAULT_ACCOUNTS_SERVER = "https://api.mojang.com",
			DEFAULT_SESSION_SERVER = "https://sessionserver.mojang.com",
			DEFAULT_SERVICES_SERVER = "https://api.minecraftservices.com",
			LEGACY_SKIN_SERVER = "http://skins.minecraft.net",
			OLD_LEGACY_SKIN_SERVER = "http://s3.amazonaws.com/MinecraftSkins/",
			OLD_LEGACY_CLOAK_SERVER = "http://s3.amazonaws.com/MinecraftCloaks/",
			OLD_OLD_LEGACY_SKIN_SERVER = "http://www.minecraft.net/skin/",
			OLD_OLD_LEGACY_CLOAK_SERVER = "http://www.minecraft.net/cloak/get.jsp?user=",
			LEGACY_SESSION_SERVER = "http://session.minecraft.net",
			LEGACY_AUTHENTICATION_SERVER = "https://login.minecraft.net",
			OLD_LEGACY_SESSION_SERVER = "http://www.minecraft.net/game/";

	/**
	 * Patches the authlib jar file
	 * @param authLib Path to the authlib jar
	 * @param outputFile Path to store the patched authlib file
	 * @param serverConfiguration Servers to use when patching
	 * @param publicKeyFile The public key file to use when patching
	 * @throws IOException If an I/O error occurs while patching
	 * @throws PatchingException If patching fails
	 */
	public static void patchAuthlib(Path authLib, Path outputFile, ServerConfiguration serverConfiguration, File publicKeyFile) throws IOException, PatchingException {
		System.out.println("Patching authlib");

		if(!outputFile.equals(authLib)) Files.copy(authLib, outputFile, StandardCopyOption.REPLACE_EXISTING);

		try(FileSystem fs = FileSystems.newFileSystem(outputFile, (ClassLoader) null)) {
			Path sessionService = fs.getPath("com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService.class");
			if(!Files.exists(sessionService)) {
				System.out.println("YggdrasilMinecraftSessionService.class not found, assuming no authlib");
				return;
			}

			ClassFile sessionClass;
			try(InputStream in = Files.newInputStream(sessionService)) {
				sessionClass = new ClassFile(in);
			}

			ClassField domainsField = null;
			for(ClassField f : sessionClass.getFields()) {
				if(f.getName().getValue().equals("WHITELISTED_DOMAINS")
						|| f.getName().getValue().equals("ALLOWED_DOMAINS")) domainsField = f;
			}

			if(domainsField != null) { // Otherwise the version is probably too old (e.g. 1.7.2), and doesn't have whitelisted URLs
				ClassMethod meth = sessionClass.getMethods("<clinit>")[0];

				AttributeCode codeAttr = meth.getCodeAttribute();

				ByteCode code = codeAttr.getCode();
				List<InstructionInformation> iis = code.parseCode();
				int startIdx = -1, endIdx = -1; // Find beginning and end of array initialization
				for(int i = 0; i < iis.size(); i++) {
					InstructionInformation ii = iis.get(i);
					if(ii.getInstruction() == Instruction.ANEWARRAY && startIdx == -1) {
						startIdx = i;
					}

					if(ii.getInstruction() == Instruction.PUTSTATIC) {
						short s = 0;
						s += (ii.getInformation()[0] & 0xFF) << 8;
						s += ii.getInformation()[1] & 0xFF;

						ConstantPoolFieldRefEntry fr = (ConstantPoolFieldRefEntry) sessionClass.getConstantPool().getEntry(s);
						String fieldName = fr.getNameAndType().getName().getValue();
						if(fieldName.equals(domainsField.getName().getValue())) { // BLOCKED_DOMAINS / WHITELISTED_DOMAINS
							endIdx = i;
						}
					}
				}

				System.out.println("Patching with host: " + serverConfiguration.skinHost);
				int en = ClassFileUtils.getOrAppendString(sessionClass, ClassFileUtils.getOrAppendUTF8(sessionClass, serverConfiguration.skinHost));
				int en2 = ClassFileUtils.getOrAppendString(sessionClass, ClassFileUtils.getOrAppendUTF8(sessionClass, ".minecraft.net"));

				iis.subList(0, endIdx).clear();

				List<InstructionInformation> newInstrs = new ArrayList<>();
				newInstrs.add(new InstructionInformation(Instruction.ICONST_2));
				newInstrs.add(new InstructionInformation(Instruction.ANEWARRAY, ClassFileUtils.getShortBytes(ClassFileUtils.getOrAppendClass(sessionClass, ClassFileUtils.getOrAppendUTF8(sessionClass, "java/lang/String")))));
				newInstrs.add(new InstructionInformation(Instruction.DUP));
				newInstrs.add(new InstructionInformation(Instruction.ICONST_0));
				newInstrs.add(new InstructionInformation(Instruction.LDC_W, ClassFileUtils.getShortBytes(en)));
				newInstrs.add(new InstructionInformation(Instruction.AASTORE));
				newInstrs.add(new InstructionInformation(Instruction.DUP));
				newInstrs.add(new InstructionInformation(Instruction.ICONST_1));
				newInstrs.add(new InstructionInformation(Instruction.LDC_W, ClassFileUtils.getShortBytes(en2)));
				newInstrs.add(new InstructionInformation(Instruction.AASTORE));
				iis.addAll(0, newInstrs);

				code.replace(ByteCode.of(iis));
			}

			replaceStrings(sessionClass, DEFAULT_SESSION_SERVER, serverConfiguration.sessionServer);

			try(OutputStream fOut = Files.newOutputStream(sessionService)) {
				sessionClass.write(fOut);
			}

			Path environment = fs.getPath("com/mojang/authlib/yggdrasil/YggdrasilEnvironment.class");
			if(Files.exists(environment)) {
				System.out.println("Patching YggdrasilEnvironment.class");
				ClassFile environmentClass;
				try(InputStream in = Files.newInputStream(environment)) {
					environmentClass = new ClassFile(in);
				}

				replaceStrings(environmentClass, DEFAULT_AUTH_SERVER, serverConfiguration.authServer);
				replaceStrings(environmentClass, DEFAULT_ACCOUNTS_SERVER, serverConfiguration.accountsServer);
				replaceStrings(environmentClass, DEFAULT_SESSION_SERVER, serverConfiguration.sessionServer);
				replaceStrings(environmentClass, DEFAULT_SERVICES_SERVER, serverConfiguration.servicesServer);

				try(OutputStream out = Files.newOutputStream(environment)) {
					environmentClass.write(out);
				}
			}

			Path pubkeyPath = fs.getPath("yggdrasil_session_pubkey.der");
			if(publicKeyFile != null && publicKeyFile.exists()) {
				System.out.println("Copying public key from " + publicKeyFile.getAbsolutePath());
				Files.copy(publicKeyFile.toPath(), pubkeyPath, StandardCopyOption.REPLACE_EXISTING);
			}else {
				System.out.println("No public key provided or key file doesn't exist. Skipping");
			}
		}

		System.out.println("Done patching authlib!");
	}

	/**
	 * Patches the minecraft jar file. Only needed for MC version &lt; 1.7.6, as the new skins API was introduced in release 1.7.6
	 * @param minecraft Path to the minecraft jar file
	 * @param outputFile Path to store the patched jar file
	 * @param serverConfiguration Servers to use when patching
	 * @throws IOException If an I/O error occurs while patching
	 * @throws PatchingException If patching fails
	 */
	public static void patchMinecraft(Path minecraft, Path outputFile, ServerConfiguration serverConfiguration) throws IOException, PatchingException {
		System.out.println("Patching Minecraft");

		Files.copy(minecraft, outputFile, StandardCopyOption.REPLACE_EXISTING);

		try(FileSystem fs = FileSystems.newFileSystem(outputFile, (ClassLoader) null)) {
			Path manifestPath = fs.getPath("/META-INF/MANIFEST.MF");
			if(Files.exists(manifestPath)) {
				System.out.println("Patching manifest");
				Manifest oldManifest;
				try(InputStream in = Files.newInputStream(manifestPath)) {
					oldManifest = new Manifest(in);
				}

				Manifest newManifest = new Manifest();
				newManifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
				newManifest.getMainAttributes().putValue("Main-Class", oldManifest.getMainAttributes().getValue("Main-Class"));

				try(OutputStream out = Files.newOutputStream(manifestPath)) {
					newManifest.write(out);
				}
			}

			System.out.println("Deleting Mojang signatures");
			Files.deleteIfExists(fs.getPath("/META-INF/MOJANGCS.RSA"));
			Files.deleteIfExists(fs.getPath("/META-INF/MOJANGCS.SF"));
			Files.deleteIfExists(fs.getPath("/META-INF/MOJANG_C.DSA"));
			Files.deleteIfExists(fs.getPath("/META-INF/MOJANG_C.SF"));
			Files.deleteIfExists(fs.getPath("/META-INF/CODESIGN.RSA"));
			Files.deleteIfExists(fs.getPath("/META-INF/CODESIGN.SF"));

			System.out.println("Replacing legacy server URLs");
			Files.walk(fs.getPath("/")).forEach(f -> replaceLegacyServers(f, serverConfiguration));
		}

		System.out.println("Done patching Minecraft!");
	}

	private static void replaceLegacyServers(Path path, ServerConfiguration serverConfiguration) {
		try {
			if(Files.isDirectory(path) || !path.getFileName().toString().endsWith(".class")) return;

			ClassFile cf;
			try(InputStream in = Files.newInputStream(path)) {
				cf = new ClassFile(in);
			}

			replaceStrings(cf, LEGACY_SKIN_SERVER, serverConfiguration.sessionServer);
			replaceStrings(cf, LEGACY_SESSION_SERVER, serverConfiguration.sessionServer);
			replaceStrings(cf, LEGACY_AUTHENTICATION_SERVER, serverConfiguration.sessionServer);
			replaceStrings(cf, OLD_LEGACY_SKIN_SERVER, serverConfiguration.sessionServer + "/MinecraftSkins/");
			replaceStrings(cf, OLD_LEGACY_CLOAK_SERVER, serverConfiguration.sessionServer + "/MinecraftCloaks/");
			replaceStrings(cf, OLD_OLD_LEGACY_SKIN_SERVER, serverConfiguration.sessionServer + "/MinecraftSkins/");
			replaceStrings(cf, OLD_OLD_LEGACY_CLOAK_SERVER, serverConfiguration.sessionServer + "/MinecraftCloaks/");
			replaceStrings(cf, OLD_LEGACY_SESSION_SERVER, serverConfiguration.sessionServer + "/game/");

			try(OutputStream fOut = Files.newOutputStream(path)) {
				cf.write(fOut);
			}
		}catch(IOException e) {
			throw new PatchingException("Failed to patch Minecraft", e);
		}
	}

	private static void replaceStrings(ClassFile cf, String find, String replace) {
		for(int i = 1; i < cf.getConstantPool().getSize() + 1; i++) {
			ConstantPoolEntry e = cf.getConstantPool().getEntry(i);
			if(e instanceof ConstantPoolStringEntry) {
				String s = e.as(ConstantPoolStringEntry.class).getString().getValue();
				if(s.startsWith(find)) {
					cf.getConstantPool().setEntry(i, new ConstantPoolStringEntry(cf.getConstantPool(), ClassFileUtils.getOrAppendUTF8(cf, s.replace(find, replace))));
				}
			}
		}
	}

	/**
	 * Patches the server jar file
	 * @param server Path to the server jar
	 * @param outputFile Path to store the patched server file
	 * @param serverConfiguration Servers to use when patching
	 * @param publicKeyFile The public key file to use when patching
	 * @throws IOException If an I/O error occurs while patching
	 * @throws PatchingException If patching fails
	 */
	public static void patchServer(Path server, Path outputFile, ServerConfiguration serverConfiguration, File publicKeyFile) throws IOException, PatchingException {
		System.out.println("Patching server");

		Files.copy(server, outputFile, StandardCopyOption.REPLACE_EXISTING);

		boolean patched = false;

		try(FileSystem fs = FileSystems.newFileSystem(outputFile, (ClassLoader) null)) {
			Path authlibFolder = fs.getPath("/META-INF/libraries/com/mojang/authlib");
			if(Files.exists(authlibFolder)) {
				Path authlibJar = Files.list(Files.list(authlibFolder).findFirst().orElse(null)).findFirst().orElse(null);
				patchAuthlib(authlibJar, authlibJar, serverConfiguration, publicKeyFile);
				patched = true;

				// Update hash
				byte[] bytes = Files.readAllBytes(authlibJar);
				MessageDigest digest;
				try {
					digest = MessageDigest.getInstance("SHA-256");
					String newHash = bytesToHex(digest.digest(bytes));

					Path libListPath = fs.getPath("/META-INF/libraries.list");
					String libList = Files.readString(libListPath);
					libList = libList.replaceAll("\n[0-9a-f]+\tcom.mojang.authlib", "\n" + newHash + "\tcom\\.mojang:authlib");
					Files.writeString(libListPath, libList);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}else if(!Files.exists(fs.getPath("/com/mojang/authlib"))){
				System.out.println("Patching server as legacy jar");
				Files.walk(fs.getPath("/")).forEach(f -> replaceLegacyServers(f, serverConfiguration));
				patched = true;
			}
		}

		if(!patched) patchAuthlib(server, outputFile, serverConfiguration, publicKeyFile);

		System.out.println("Done patching server!");
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder str = new StringBuilder();
		for(int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if(hex.length() == 1) str.append('0');
			str.append(hex);
		}
		return str.toString();
	}

}