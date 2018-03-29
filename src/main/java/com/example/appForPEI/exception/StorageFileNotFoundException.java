package com.example.appForPEI.exception;


public class StorageFileNotFoundException extends StorageException {


	private static final long serialVersionUID = -6763929360625634420L;

	public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
