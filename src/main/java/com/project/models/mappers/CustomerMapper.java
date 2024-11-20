package com.project.models.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.entities.Customer;
import com.project.models.dtos.CustomerRequest;
import com.project.models.dtos.CustomerResponse;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    
	@Mapping(target = "active", ignore = true)
	@Mapping(target = "datastate", ignore = true)
	@Mapping(target = "registrationDate", ignore = true)
	Customer toCustomer(CustomerRequest customerRequest);
    
	CustomerResponse toCustomerResponse(Customer customer);

}
