package com.project.service;

import org.springframework.data.domain.Page;

import com.project.entities.Customer;
import com.project.utils.PaginationRequest;

public interface CustomerService {

	Page<Customer> findAll(PaginationRequest paginationRequest);
	
	Customer findById(Long id);
	
	Customer save(Customer customer);
	
	Customer update(Long id, Customer customer);
	
	String delete(Long id);
	
}
