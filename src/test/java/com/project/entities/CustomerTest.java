package com.project.entities;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CustomerTest {

    @Test
    @DisplayName("Check if Equals returns false when compared to null")
    void testEqualsReturnsFalseToNull() {
    	Customer user = new Customer();
        assertFalse(user.equals(null));
    }

    @Test
    @DisplayName("Check if Equals returns false when compared to a different class")
    void testEqualsReturnsFalseToDifferentClass() {
    	Customer user = new Customer();
        String notAnUser = "Anything else";
        assertFalse(user.equals(notAnUser));
    }

    @Test
    @DisplayName("Check if Equals returns false when ids are different")
    void testEqualsReturnsFalseToDifferentId() {
    	Customer user1 = new Customer();
    	user1.setId(1L);
    	Customer user2 = new Customer();
    	user2.setId(2L);
        assertFalse(user1.equals(user2));
    }

    @Test
    @DisplayName("Check if Equals returns true when ids are the same")
    void testEqualsReturnsTrueToSameId() {
    	Customer user1 = new Customer();
    	user1.setId(1L);
    	Customer user2 = new Customer();
    	user2.setId(1L);
        assertTrue(user1.equals(user2));
    }

}
