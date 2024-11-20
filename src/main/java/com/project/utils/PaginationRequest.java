package com.project.utils;

public class PaginationRequest {
    
    private int page = 0;
    private int size = 10;
    private String sortField = "name";
    private String sortDirection = "asc";

    public PaginationRequest() {
	}

	public PaginationRequest(int page, int size, String sortField, String sortDirection) {
		this.page = page;
		this.size = size;
		this.sortField = sortField;
		this.sortDirection = sortDirection;
	}

	public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
    
}
