package com.project.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.entities.Customer;
import com.project.exceptions.ResourceNotFoundException;
import com.project.models.mocks.CustomerMock;
import com.project.service.impl.CustomerServiceImpl;
import com.project.utils.PaginationRequest;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CustomerServiceImpl customerService;

    @InjectMocks
    private CustomerController customerController;
    
    @InjectMocks
    private CustomerMock customerMock;

    private Customer customer;
    private List<Customer> customerList;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        customer = customerMock.single();
        customerList = customerMock.list();
    }

    @Test
    @DisplayName("Check if a list of Customers is returned")
    void shouldReturnListOfCustomers() throws Exception {
    	Page<Customer> customerPage = new PageImpl<>(customerList, PageRequest.of(0, customerList.size()), customerList.size());
    	
        when(customerService.findAll(any(PaginationRequest.class))).thenReturn(customerPage);
        
        mockMvc.perform(get("/api/v1/customers")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("João Silva"));
        
        assertThat(customerList).hasSizeGreaterThan(0);
        assertThat(customerList.get(0).getId()).isEqualTo(1);
        assertThat(customerList.get(0).getName()).isEqualTo("João Silva");
        
        verify(customerService, times(1)).findAll(any(PaginationRequest.class));
    }

    @Test
    @DisplayName("Check if an Customer is returned")
    void shouldReturnAnCustomer() throws Exception {
        when(customerService.findById(1L)).thenReturn(customer);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/customers/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        Customer returnedCustomer = objectMapper.readValue(content, Customer.class);
        
        assertThat(returnedCustomer).isNotNull();
        assertThat(returnedCustomer.getId()).isEqualTo(1);
        assertThat(returnedCustomer.getName()).isEqualTo("João Silva");
        
        verify(customerService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Check if a new Customer is created")
    void shouldCreateAnCustomer() throws Exception {
        when(customerService.save(any(Customer.class))).thenReturn(customer);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/customers")
                .contentType(APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"João Silva\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/customers/1")))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        Customer returnedCustomer = objectMapper.readValue(content, Customer.class);

        assertThat(returnedCustomer).isNotNull();
        assertThat(returnedCustomer.getId()).isEqualTo(1);
        assertThat(returnedCustomer.getName()).isEqualTo("João Silva");
        
        verify(customerService, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Check if the selected Customer is updated")
    void shouldUpdateAnCustomer() throws Exception {
        when(customerService.update(eq(1L), any(Customer.class))).thenReturn(customer);

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/customers/1")
                .contentType(APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"João Silva\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        Customer updatedCustomer = objectMapper.readValue(content, Customer.class);

        assertThat(updatedCustomer).isNotNull();
        assertThat(updatedCustomer.getId()).isEqualTo(1);
        assertThat(updatedCustomer.getName()).isEqualTo("João Silva");
        
        verify(customerService, times(1)).update(eq(1L), any(Customer.class));
    }

    @Test
    @DisplayName("Check if the selected Customer is deleted")
    void shouldReturnCustomerDeletedSuccessful() throws Exception {
    	when(customerService.delete(1L)).thenReturn("Registro removido com sucesso.");

    	MvcResult mvcResult = mockMvc.perform(delete("/api/v1/customers/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Registro removido com sucesso."))
                .andReturn();

    	String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).isNotNull();
        assertThat(content).isEqualTo("Registro removido com sucesso.");
        assertThat(customerService.findById(1L)).isNull();
       
        verify(customerService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Check if the selected Customer is set active")
    void shouldReturnActivatedSuccessfully() throws Exception {
        when(customerService.toggleActivateAccount(1L)).thenReturn("Cadastro ativado com sucesso.");
        when(customerService.findById(1L)).thenReturn(customer);

        MvcResult mvcResult = mockMvc.perform(patch("/api/v1/customers/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Cadastro ativado com sucesso."))
                .andReturn();

    	String content = mvcResult.getResponse().getContentAsString();
    	
        assertThat(content).isNotNull();
        assertThat(content).isEqualTo("Cadastro ativado com sucesso.");
        assertThat(customerService.findById(1L).getActive()).isEqualTo(true);
    	
        verify(customerService, times(1)).toggleActivateAccount(1L);
    }
    
    @Test
    @DisplayName("Check if the selected Customer is set inactive")
    void shouldReturnDeactivatedSuccessfully() throws Exception {
    	Customer inactiveCustomer = new Customer();
        inactiveCustomer.setId(1L);
        inactiveCustomer.setRegistrationDate(LocalDate.now());
        inactiveCustomer.setActive(false);
    	
    	when(customerService.toggleActivateAccount(1L)).thenReturn("Cadastro inativado com sucesso.");
        when(customerService.findById(1L)).thenReturn(inactiveCustomer);

        MvcResult mvcResult = mockMvc.perform(patch("/api/v1/customers/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Cadastro inativado com sucesso."))
                .andReturn();

    	String content = mvcResult.getResponse().getContentAsString();
    	
        assertThat(content).isNotNull();
        assertThat(content).isEqualTo("Cadastro inativado com sucesso.");
        assertThat(customerService.findById(1L).getActive()).isEqualTo(false);
    	
        verify(customerService, times(1)).toggleActivateAccount(1L);
    }
    
    @Test
    @DisplayName("Check if method throws ResourceNotFoundException")
    void shouldThrowException() throws Exception {
        when(customerService.findById(2L)).thenThrow(new ResourceNotFoundException(2L));

    	Assertions.assertThrows(ResourceNotFoundException.class, () -> customerService.findById(2L));
    	
        verify(customerService, times(1)).findById(2L);
    }

}
