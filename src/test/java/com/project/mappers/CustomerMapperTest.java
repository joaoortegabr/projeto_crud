package com.project.mappers;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.project.entities.Customer;
import com.project.models.dtos.CustomerRequest;
import com.project.models.dtos.CustomerResponse;
import com.project.models.mappers.CustomerMapperImpl;

public class CustomerMapperTest {

    private CustomerMapperImpl customerMapper;

    @BeforeEach
    void setUp() {
        customerMapper = new CustomerMapperImpl();
    }
    
    @Test
    @DisplayName("Check if a Customer mapper returns null if CustomerRequest is null")
    void shouldReturnNullWhenCustomerRequestIsNull() {
        CustomerRequest customerRequest = null;

        Customer result = customerMapper.toCustomer(customerRequest);

        assertNull(result, "O resultado deve ser null quando o CustomerRequest for null");
    }
    
    @Test
    @DisplayName("Check if a CustomerResponse mapper returns null if Customer is null")
    void shouldReturnNullWhenCustomerIsNull() {
        Customer customer = null;

        CustomerResponse result = customerMapper.toCustomerResponse(customer);

        assertNull(result, "O resultado deve ser null quando o Customer for null");
    }
    
}
