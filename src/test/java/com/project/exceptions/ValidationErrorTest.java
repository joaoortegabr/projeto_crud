package com.project.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ValidationErrorTest {

    @Test
    @DisplayName("Check if constructor, getters, and setters are working correctly")
    void shouldTestConstructorAndGetterSetter() {
    	Instant timestamp = Instant.now();
        Integer status = 400;
        Map<String, String> errors = new HashMap<>();
        errors.put("field1", "error1");
        errors.put("field2", "error2");
        String path = "/api/v1/resource";

        ValidationError validationError = new ValidationError(timestamp, status, errors, path);

        assertEquals(timestamp, validationError.getTimestamp());
        assertEquals(status, validationError.getStatus());
        assertEquals(errors, validationError.getErrors());
        assertEquals(path, validationError.getPath());
    }

    @Test
    @DisplayName("Check if setters are working correctly")
    void shouldTestSettersWorkingCorrectly() {
        ValidationError validationError = new ValidationError();

        Instant timestamp = Instant.now();
        Integer status = 500;
        Map<String, String> errors = new HashMap<>();
        errors.put("field3", "error3");
        String path = "/api/v1/other";

        validationError.setTimestamp(timestamp);
        validationError.setStatus(status);
        validationError.setPath(path);

        assertEquals(timestamp, validationError.getTimestamp());
        assertEquals(status, validationError.getStatus());
        assertEquals(path, validationError.getPath());
        assertTrue(validationError.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Check if map error is set correctly")
    void testErrorMap() {

        Instant timestamp = Instant.now();
        Integer status = 500;
        String path = "/api/v1/other";
        Map<String, String> errors = new HashMap<>();
        errors.put("field1", "Error message");
        ValidationError validationError = new ValidationError(timestamp, status, errors, path);

        assertEquals(errors, validationError.getErrors());
        assertEquals("Error message", validationError.getErrors().get("field1"));
    }

    @Test
    @DisplayName("Check if timestamp is formatted correctly in JSON")
    void testJsonFormatAnnotation() throws Exception {
        Instant timestamp = Instant.now();
        Integer status = 404;
        Map<String, String> errors = new HashMap<>();
        errors.put("field", "error");
        String path = "/api/v1/another";

        ValidationError validationError = new ValidationError(timestamp, status, errors, path);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String json = objectMapper.writeValueAsString(validationError);

        assertTrue(json.contains("timestamp"));
        assertTrue(json.contains("T"));
        assertTrue(json.contains("Z"));
    }
}