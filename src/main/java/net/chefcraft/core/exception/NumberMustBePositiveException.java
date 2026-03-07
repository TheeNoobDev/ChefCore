package net.chefcraft.core.exception;

public class NumberMustBePositiveException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public NumberMustBePositiveException(String message) {
		super(message);
	}
	
	public NumberMustBePositiveException(String message, Throwable cause) {
		super(message, cause); 
	}

}
