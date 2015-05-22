package edu.indiana.d2i.htrc.randomsampling.exceptions;

@SuppressWarnings("serial")
public class SampleNumberTooLarge extends Exception {
	public SampleNumberTooLarge(Throwable throwable) {
		super(throwable);
	} 
	
	public SampleNumberTooLarge(String message, Throwable throwable) {
		super(message, throwable);
	}

	public SampleNumberTooLarge(String message) {
		super(message);
	}
}
