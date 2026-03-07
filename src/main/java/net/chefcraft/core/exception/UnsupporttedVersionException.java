package net.chefcraft.core.exception;

public class UnsupporttedVersionException extends RuntimeException  {

	private static final long serialVersionUID = 1L;

	public UnsupporttedVersionException(String message) {
		super(message);
	}
	
	public UnsupporttedVersionException(String message, Throwable cause) {
		super(message, cause); 
	}
}
