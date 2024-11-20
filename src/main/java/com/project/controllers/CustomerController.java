package com.project.controllers;

import java.net.URI;

import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.project.entities.Customer;
import com.project.models.dtos.CustomerRequest;
import com.project.models.dtos.CustomerResponse;
import com.project.models.mappers.CustomerMapper;
import com.project.service.impl.CustomerServiceImpl;
import com.project.utils.PaginationRequest;

@RestController
@RequestMapping(value = "api/v1/customers")
public class CustomerController {
	
	private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
	
	private final CustomerServiceImpl customerService;
	
	CustomerMapper mapper = Mappers.getMapper(CustomerMapper.class);
	
	public CustomerController(CustomerServiceImpl customerService) {
		this.customerService = customerService;
	}
	
	@GetMapping
	public ResponseEntity<Page<CustomerResponse>> findAll(PaginationRequest paginationRequest) {
		log.info("Receiving request in findAll");
		Page<Customer> customerPage = customerService.findAll(paginationRequest);
		
		Page<CustomerResponse> customerResponsePage = customerPage.map(mapper::toCustomerResponse);

	    return ResponseEntity.ok(customerResponsePage);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<CustomerResponse>findById(@PathVariable Long id) {
		log.info("Receiving request in findById with param: {}", id);
		CustomerResponse customer = mapper.toCustomerResponse(customerService.findById(id));
		return ResponseEntity.ok().body(customer);
	}
	
	@PostMapping
	public ResponseEntity<CustomerResponse> save(@RequestBody CustomerRequest customerRequest) {
		log.info("Receiving request in save with param: {}", customerRequest);
		Customer customer = mapper.toCustomer(customerRequest);
		customerService.save(customer);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(customer.getId()).toUri();
		CustomerResponse savedCustomerResponse = mapper.toCustomerResponse(customer);
		return ResponseEntity.created(uri).body(savedCustomerResponse);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<CustomerResponse> update(@PathVariable Long id, @RequestBody CustomerRequest customerRequest) {
		log.info("Receiving request in update with params: {} and {}", id, customerRequest);
		Customer customer = mapper.toCustomer(customerRequest);
		customerService.update(id, customer);
		CustomerResponse updatedCustomerResponse = mapper.toCustomerResponse(customer);
		return ResponseEntity.ok().body(updatedCustomerResponse);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		log.info("Receiving request in delete with param: {}", id);
		String msg = customerService.delete(id);
		return ResponseEntity.ok(msg);
	}
	
	@PatchMapping(value = "/{id}")
	public String toggleActivateAccount(@PathVariable Long id) {
		log.info("Receiving request in toggleActivation with param: {}", id);
		return customerService.toggleActivateAccount(id);
	}

}
