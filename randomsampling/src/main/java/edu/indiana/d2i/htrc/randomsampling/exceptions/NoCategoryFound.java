package edu.indiana.d2i.htrc.randomsampling.exceptions;

@SuppressWarnings("serial")
public class NoCategoryFound extends Exception {
	public NoCategoryFound(Throwable throwable) {
		super(throwable);
	} 
	
	public NoCategoryFound(String message, Throwable throwable) {
		super(message, throwable);
	}

	public NoCategoryFound(String message) {
		super(message);
	}
}
