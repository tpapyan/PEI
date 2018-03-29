package com.example.appForPEI.exception;

public class XmlException extends RuntimeException {

	private static final long serialVersionUID = 7802029232637039823L;

	public XmlException(String message) {
        super(message);
    }

    public XmlException(String message, Throwable cause) {
        super(message, cause);
    }
}
