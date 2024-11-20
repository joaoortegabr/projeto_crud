package com.project.exceptions;

public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -7580828335251690971L;

	public ResourceNotFoundException(Object id) {
		super("Resource not found: ID " + id);
	}

}
