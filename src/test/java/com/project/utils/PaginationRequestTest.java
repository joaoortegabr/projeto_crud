package com.project.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaginationRequestTest {

    @Test
    @DisplayName("Check if Page getter return correct value")
    void testPage() {
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setPage(1);
        assertEquals(1, paginationRequest.getPage());
    }

    @Test
    @DisplayName("Check if Size getter return correct value")
    void testSize() {
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setSize(20);
        assertEquals(20, paginationRequest.getSize());
    }

    @Test
    @DisplayName("Check if Sort getter return correct value")
    void testSortField() {
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setSortField("name");
        assertEquals("name", paginationRequest.getSortField());
    }

    @Test
    @DisplayName("Check if Sort Direction getter return correct value")
    void testSortDirection() {
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setSortDirection("ASC");
        assertEquals("ASC", paginationRequest.getSortDirection());
    }
}

