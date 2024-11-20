package com.project.exceptions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.project.controllers.CustomerController;
import com.project.entities.Customer;
import com.project.models.mocks.CustomerMock;
import com.project.service.impl.CustomerServiceImpl;
import com.project.utils.PaginationRequest;

import jakarta.validation.ConstraintViolationException;

@WebMvcTest(CustomerController.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerServiceImpl customerService;

    @InjectMocks
    private CustomerController customerController;
    
    @InjectMocks
    private CustomerMock customerMock;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CustomerController(customerService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Check if ResourceNotFoundException is thrown")
    void shouldReturnResourceNotFoundError() throws Exception {
        when(customerService.findById(4L))
        	.thenThrow(new ResourceNotFoundException("4"));
        
        mockMvc.perform(get("/api/v1/customers/4"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Resource not found: ID 4"))
                .andExpect(jsonPath("$.path").value("/api/v1/customers/4"));
    }
    
    @Test
    @DisplayName("Check if ConstraintViolationException is thrown")
    void shouldReturnInvalidRequestError() throws Exception {
        when(customerService.save(any(Customer.class)))
        	.thenThrow(new ConstraintViolationException("Invalid request", null));
        
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
        		.content("{ \"cpf\": \"xfhghjgj\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.path").value("/api/v1/customers"));
    }
    
    @Test
    @DisplayName("Check if DataIntegrityViolationException is thrown")
    void shouldReturnDatabaseError() throws Exception {
        when(customerService.save(any(Customer.class)))
        	.thenThrow(new DataIntegrityViolationException("Registro já existe no banco de dados."));

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
        		.content("{ \"cpf\": \"05352751023\" }"));
        		
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
				.content("{ \"cpf\": \"05352751023\" }"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Database error"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Registro já existe no banco de dados."))
                .andExpect(jsonPath("$.path").value("/api/v1/customers"));
    }

    @Test
    @DisplayName("Check if general Exception is thrown")
    void shouldReturnInternalServerError() throws Exception {
        when(customerService.findAll(any(PaginationRequest.class)))
    		.thenThrow(new RuntimeException("Not identified error"));
        
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Not identified error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Not identified error"))
                .andExpect(jsonPath("$.path").value("/api/v1/customers"));
    }
	
}
