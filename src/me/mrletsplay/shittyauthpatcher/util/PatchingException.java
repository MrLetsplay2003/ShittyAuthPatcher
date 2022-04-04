package me.mrletsplay.shittyauthpatcher.util;

public class PatchingException extends RuntimeException {

	private static final long serialVersionUID = -1739611603176643053L;

	public PatchingException() {
		super();
	}

	public PatchingException(String message, Throwable cause) {
		super(message, cause);
	}

	public PatchingException(String message) {
		super(message);
	}

	public PatchingException(Throwable cause) {
		super(cause);
	}
	
}
