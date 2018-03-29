package com.example.appForPEI.exception;

public class StorageException extends RuntimeException {


	private static final long serialVersionUID = -6941183900053764345L;

	public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
