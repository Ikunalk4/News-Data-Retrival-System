package com.inshort.newshub.exception;

public class RecordNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5063133817645688326L;

	public RecordNotFoundException(String message) {
        super(message);
    }
}
