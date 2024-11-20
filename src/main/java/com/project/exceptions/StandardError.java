package com.project.exceptions;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

public record StandardError(
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss'Z'", timezone = "GMT")
		Instant timestamp,
		Integer status,
		String error,
		String message,
		String path
		) implements Serializable {

}
