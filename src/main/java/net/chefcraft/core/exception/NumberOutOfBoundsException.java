package net.chefcraft.core.exception;

public class NumberOutOfBoundsException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public NumberOutOfBoundsException(String message) {
		super(message);
	}
	
	public NumberOutOfBoundsException(String message, Throwable cause) {
		super(message, cause); 
	}
	
}
