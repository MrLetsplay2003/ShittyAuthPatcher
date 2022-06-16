package me.mrletsplay.shittyauthpatcher.version;

public class VersionsLoadException extends RuntimeException {

	private static final long serialVersionUID = -3673356701408110109L;

	public VersionsLoadException() {
		super();
	}

	public VersionsLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public VersionsLoadException(String message) {
		super(message);
	}

	public VersionsLoadException(Throwable cause) {
		super(cause);
	}

}
